package com.example.PCOnlineShop.controller.product;

import com.example.PCOnlineShop.dto.product.ProductComponentSpecFormDTO;
import com.example.PCOnlineShop.model.product.*;
import com.example.PCOnlineShop.service.product.BrandService;
import com.example.PCOnlineShop.service.product.CategoryService;
import com.example.PCOnlineShop.service.product.ComponentSpecService;
import com.example.PCOnlineShop.service.product.ProductCommandService;
import com.example.PCOnlineShop.service.product.ProductImageService;
import com.example.PCOnlineShop.service.product.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/staff/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ComponentSpecService componentSpecService;
    private final ProductCommandService productCommandService;

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.getMainCategories();
    }

    @ModelAttribute("brands")
    public List<Brand> brands() {
        return brandService.getAllBrands();
    }

    @ModelAttribute("gearCategories")
    public List<Category> gearCategories() {
        return categoryService.getGearCategories();
    }

    // Exception handler for validation errors
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleValidationException(IllegalArgumentException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("product", new Product());
        model.addAttribute("isEdit", false);
        return "product/product-form";
    }

    // ===== DANH SÁCH =====
    @GetMapping("/list")
    public String listProducts(Model model) {
        List<Product> products = productService.getProductsForManagement();
        model.addAttribute("products", products);
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("categories", categoryService.getMainCategories());
        return "product/product-list";
    }

    // ===== FORM THÊM =====
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("isEdit", false);
        return "product/product-form";
    }

    // ===== SPEC FORM (ADD) — ĐÃ SỬA ĐỂ GỘP =====
    @GetMapping("/spec-form")
    public String getSpecForm(@RequestParam("categoryId") int categoryId, Model model) {

        return addComponentSpecForm(model, componentSpecService.buildCreateSpecForm(categoryId));

    }

    // ===== SPEC FORM (EDIT) — ĐÃ SỬA ĐỂ GỘP =====
    @GetMapping("/spec-form-edit")
    public String getSpecFormForEdit(@RequestParam("categoryId") int categoryId,
                                     @RequestParam("productId") int productId,
                                     Model model) {

        return addComponentSpecForm(model, componentSpecService.buildEditSpecForm(categoryId, productId));

    }

    // ===== SAVE NEW PRODUCT =====
    @PostMapping("/save")
    public String saveProduct(/* @Valid */ @ModelAttribute("product") Product product,
                              BindingResult result,
                              @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
                              @RequestParam(value = "brand.brandId", required = false) Integer brandId,
                              @RequestParam Map<String, String> params,
                              @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                              Model model) {

        categoryIds = normalizeCategoryIds(categoryIds);
        Integer primaryCategoryId = categoryIds.isEmpty() ? null : categoryIds.get(0);
        Integer submittedGearCategoryId = resolveSubmittedGearCategoryId(categoryIds);

        if (productService.existsNonDiscontinuedProductName(product.getProductName())) {
            result.rejectValue("productName", "error.product", "Product name already exists.");
        }
        if (brandId == null) {
            model.addAttribute("brandError", "Please select brand");
        }
        if (categoryIds.isEmpty()) {
            model.addAttribute("categoryError", "Please select at least one category");
        } else if (categoryService.isGearParentCategory(primaryCategoryId)
                && (submittedGearCategoryId == null || !categoryService.isGearChildCategory(submittedGearCategoryId))) {
            model.addAttribute("categoryError", "Please select a valid gear type");
        }

        if (result.hasErrors() || brandId == null || model.containsAttribute("categoryError")) {
            model.addAttribute("isEdit", false);
            model.addAttribute("submittedCategoryId", primaryCategoryId);
            model.addAttribute("submittedGearCategoryId", submittedGearCategoryId);
            model.addAttribute("submittedBrandId", brandId);
            return "product/product-form";
        }

        categoryIds = buildCategoryIdsForSave(primaryCategoryId, submittedGearCategoryId);

        List<String> specErrors = componentSpecService.validateSpecParams(primaryCategoryId, params);
        if (!specErrors.isEmpty()) {
            model.addAttribute("specErrors", specErrors);
            model.addAttribute("isEdit", false);
            model.addAttribute("submittedCategoryId", primaryCategoryId);
            model.addAttribute("submittedGearCategoryId", submittedGearCategoryId);
            model.addAttribute("submittedBrandId", brandId);
            return "product/product-form";
        }

        try {
            productCommandService.createProduct(product, categoryIds, brandId, params, imageFiles);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("submittedCategoryId", primaryCategoryId);
            model.addAttribute("submittedGearCategoryId", submittedGearCategoryId);
            model.addAttribute("submittedBrandId", brandId);
            return "product/product-form";
        } catch (IOException e) {
            log.error("Image upload failed while creating product {}", product.getProductName(), e);
            model.addAttribute("error", "Failed to save product images. Please try again.");
            model.addAttribute("isEdit", false);
            model.addAttribute("submittedCategoryId", primaryCategoryId);
            model.addAttribute("submittedGearCategoryId", submittedGearCategoryId);
            model.addAttribute("submittedBrandId", brandId);
            return "product/product-form";
        } catch (RuntimeException e) {
            log.error("Unexpected error creating product {}", product.getProductName(), e);
            model.addAttribute("error", "Failed to save product. Please try again.");
            model.addAttribute("isEdit", false);
            model.addAttribute("submittedCategoryId", primaryCategoryId);
            model.addAttribute("submittedGearCategoryId", submittedGearCategoryId);
            model.addAttribute("submittedBrandId", brandId);
            return "product/product-form";
        }

        return "redirect:/staff/products/list";
    }

    // ===== FORM SỬA =====
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        Product product = productService.findProductById(id).orElse(null);
        if (product == null) return "redirect:/staff/products/list";
        model.addAttribute("product", product);
        model.addAttribute("isEdit", true);
        return "product/product-update";
    }

    // ===== UPDATE PRODUCT =====
    @PostMapping("/edit")
    public String updateProduct(@Valid @ModelAttribute("product") Product incoming,
                                BindingResult result,
                                Model model,
                                @RequestParam Map<String, String> params,
                                @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
                                @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                @RequestParam(value = "deleteImageIds", required = false) String deleteImageIds
    ) {

        Product current = productService.findProductById(incoming.getProductId()).orElse(null);
        if (current == null) return "redirect:/staff/products/list";

        Integer currentPrimaryCategoryId = current.getCategories().isEmpty() ? null : current.getCategories().getFirst().getCategoryId();
        Integer incomingPrimaryCategoryId = categoryIds != null && !categoryIds.isEmpty() ? categoryIds.get(0) : currentPrimaryCategoryId;

        String newName = incoming.getProductName();
        if (!Objects.equals(newName, current.getProductName()) && productService.existsNonDiscontinuedProductName(newName)) {
            result.rejectValue("productName", "error.product", "Product name already exists.");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            incoming.setImages(current.getImages());
            incoming.setBrand(current.getBrand());
            incoming.setCategories(current.getCategories());
            return "product/product-update";
        }

        if (incomingPrimaryCategoryId != null && !incomingPrimaryCategoryId.equals(currentPrimaryCategoryId)) {
            model.addAttribute("error", "Cannot change primary category of an existing product. Specs will not be updated.");
            model.addAttribute("isEdit", true);
            incoming.setImages(current.getImages());
            incoming.setBrand(current.getBrand());
            incoming.setCategories(current.getCategories());
            return "product/product-update";
        }

        try {
            productCommandService.updateProduct(incoming, categoryIds, params, imageFiles, deleteImageIds);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isEdit", true);
            incoming.setImages(current.getImages());
            incoming.setBrand(current.getBrand());
            incoming.setCategories(current.getCategories());
            return "product/product-update";
        } catch (IOException ex) {
            log.error("Image upload failed while updating product {}", incoming.getProductId(), ex);
            model.addAttribute("error", "Failed to save product images. Please try again.");
            model.addAttribute("isEdit", true);
            incoming.setImages(current.getImages());
            incoming.setBrand(current.getBrand());
            incoming.setCategories(current.getCategories());
            return "product/product-update";
        } catch (RuntimeException ex) {
            log.error("Unexpected error updating product {}", incoming.getProductId(), ex);
            model.addAttribute("error", "Failed to update product. Please try again.");
            model.addAttribute("isEdit", true);
            incoming.setImages(current.getImages());
            incoming.setBrand(current.getBrand());
            incoming.setCategories(current.getCategories());
            return "product/product-update";
        }

        return "redirect:/staff/products/list";
    }

    // ===== HIDE PRODUCT =====
    @PostMapping("/{id}/hide")
    public String hideProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productCommandService.discontinueProductIfAllowed(id);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/staff/products/list";
        }

        redirectAttributes.addFlashAttribute("message", "Product has been discontinued and hidden successfully.");
        return "redirect:/staff/products/list";
    }

    // ===== XÓA ẢNH (AJAX) =====
    @DeleteMapping("/image/{imageId}")
    @ResponseBody
    public String deleteImage(@PathVariable int imageId) {
        productImageService.deleteProductImageById(imageId);
        return "success";
    }

    // ========= HELPERS =========

    private String addComponentSpecForm(Model model, ProductComponentSpecFormDTO form) {
        if (form.attributeName() != null && form.spec() != null) {
            model.addAttribute(form.attributeName(), form.spec());
        }
        return form.template();
    }

    private List<Integer> normalizeCategoryIds(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }

        Set<Integer> uniqueCategoryIds = new LinkedHashSet<>();
        for (Integer categoryId : categoryIds) {
            if (categoryId != null) {
                uniqueCategoryIds.add(categoryId);
            }
        }
        return new ArrayList<>(uniqueCategoryIds);
    }

    private Integer resolveSubmittedGearCategoryId(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.size() < 2) {
            return null;
        }
        for (int index = 1; index < categoryIds.size(); index++) {
            Integer categoryId = categoryIds.get(index);
            if (categoryService.isGearChildCategory(categoryId)) {
                return categoryId;
            }
        }
        return null;
    }

    private List<Integer> buildCategoryIdsForSave(Integer primaryCategoryId, Integer submittedGearCategoryId) {
        if (categoryService.isGearParentCategory(primaryCategoryId)) {
            return List.of(primaryCategoryId, submittedGearCategoryId);
        }
        return List.of(primaryCategoryId);
    }

}

