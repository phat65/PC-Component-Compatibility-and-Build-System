package com.example.PCOnlineShop.controller.brand;

import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.service.product.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/brand")
@RequiredArgsConstructor
public class BrandController {

    private static final String BRAND_LIST_VIEW = "brand/brand-list";
    private static final String BRAND_FORM_VIEW = "brand/brand-form";
    private static final String BRAND_MERGE_VIEW = "brand/brand-merge";
    private static final String REDIRECT_BRAND_LIST = "redirect:/admin/brand/list";

    private final BrandService brandService;

    @GetMapping("/list")
    public String showBrandPage(Model model) {
        addBrandList(model);
        return BRAND_LIST_VIEW;
    }

    @GetMapping("/add")
    public String showAddBrandPage(Model model) {
        Brand brand = new Brand();
        brand.setStatus(true);
        prepareBrandForm(model, brand, false);
        return BRAND_FORM_VIEW;
    }

    @PostMapping("/add")
    public String createBrand(@Valid @ModelAttribute("brand") Brand brand,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            prepareBrandForm(model, brand, false);
            return BRAND_FORM_VIEW;
        }

        try {
            brandService.createBrand(brand);
            return REDIRECT_BRAND_LIST;
        } catch (IllegalArgumentException e) {
            prepareBrandForm(model, brand, false);
            model.addAttribute("error", e.getMessage());
            return BRAND_FORM_VIEW;
        } catch (DataIntegrityViolationException e) {
            prepareBrandForm(model, brand, false);
            model.addAttribute("error", "Cannot save brand because the database rejected this value.");
            return BRAND_FORM_VIEW;
        }
    }

    @GetMapping("/update/{id}")
    public String showEditBrandPage(@PathVariable int id,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        try {
            prepareBrandForm(model, brandService.getRequiredBrand(id), true);
            return BRAND_FORM_VIEW;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return REDIRECT_BRAND_LIST;
        }
    }

    @PostMapping("/update/{id}")
    public String updateBrand(@PathVariable int id,
                              @Valid @ModelAttribute("brand") Brand brand,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            brand.setBrandId(id);
            prepareBrandForm(model, brand, true);
            return BRAND_FORM_VIEW;
        }

        try {
            brandService.updateBrand(id, brand);
            return REDIRECT_BRAND_LIST;
        } catch (IllegalArgumentException e) {
            brand.setBrandId(id);
            prepareBrandForm(model, brand, true);
            model.addAttribute("error", e.getMessage());
            return BRAND_FORM_VIEW;
        } catch (DataIntegrityViolationException e) {
            brand.setBrandId(id);
            prepareBrandForm(model, brand, true);
            model.addAttribute("error", "Cannot save brand because the database rejected this value.");
            return BRAND_FORM_VIEW;
        }
    }

    @PostMapping("/{id}/status")
    public String updateBrandStatus(@PathVariable Integer id,
                                    @RequestParam boolean status,
                                    Model model) {
        try {
            brandService.updateBrandStatus(id, status);
            return REDIRECT_BRAND_LIST;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            addBrandList(model);
            return BRAND_LIST_VIEW;
        }
    }

    @GetMapping("/merge")
    public String showMergePage(Model model) {
        addBrandList(model);
        return BRAND_MERGE_VIEW;
    }

    @PostMapping("/merge")
    public String merge(@RequestParam Integer sourceId,
                        @RequestParam Integer targetId,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        try {
            long count = brandService.mergeBrands(sourceId, targetId);
            redirectAttributes.addAttribute("merged", count);
            return REDIRECT_BRAND_LIST;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            addBrandList(model);
            return BRAND_MERGE_VIEW;
        }
    }

    private void prepareBrandForm(Model model, Brand brand, boolean isEdit) {
        model.addAttribute("brand", brand);
        model.addAttribute("isEdit", isEdit);
    }

    private void addBrandList(Model model) {
        model.addAttribute("brand", brandService.getAllBrands());
    }
}
