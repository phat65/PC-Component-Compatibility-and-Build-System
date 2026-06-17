package com.example.PCOnlineShop.controller.home;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.product.CategoryService;
import com.example.PCOnlineShop.service.product.BrandService;
import com.example.PCOnlineShop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 48;
    private static final String DEFAULT_SORT_FIELD = "price";
    private static final String DEFAULT_SORT_DIRECTION = "desc";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "productId",
            "productName",
            "price",
            "createAt",
            "performanceScore"
    );

    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final AccountService accountService;

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(required = false) Integer category,
                       @RequestParam(required = false) Integer brand,
                       Authentication authentication,
                       Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String phoneNumber = authentication.getName();
            Account user = accountService.getByPhoneNumber(phoneNumber);
            model.addAttribute("currentUser", user);
        }

        List<Category> categories = categoryService.getMainCategories();
        List<Brand> brands = brandService.getAllBrands();
        List<Product> products;

        if (category != null) {
            products = productService.getStorefrontProductsByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else if (brand != null) {
            products = productService.getStorefrontProductsByBrand(brand);
            model.addAttribute("selectedBrand", brand);
        } else {
            products = productService.getFeaturedStorefrontProducts();
        }

        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("featuredProducts", products);

        return "home";
    }

    @GetMapping("/products")
    public String productHome(@RequestParam(required = false) Integer category,
                              @RequestParam(required = false) Integer brand,
                              @RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortField,
                              @RequestParam(defaultValue = DEFAULT_SORT_DIRECTION) String sortDir,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                              Model model) {
        String normalizedKeyword = normalizeKeyword(keyword);
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = resolvePageSize(size);
        String resolvedSortField = resolveSortField(sortField);
        String resolvedSortDir = resolveSortDirection(sortDir);
        Pageable pageable = PageRequest.of(
                resolvedPage,
                resolvedSize,
                createSort(resolvedSortField, resolvedSortDir)
        );

        Page<Product> productPage = productService.searchVisibleSellingProducts(
                category,
                brand,
                minPrice,
                maxPrice,
                normalizedKeyword,
                pageable
        );
        List<Category> gearCategories = categoryService.getGearCategories();
        Category gearParentCategory = categoryService.getGearParentCategory().orElse(null);
        boolean gearCategorySelected = isGearCategorySelected(category, gearParentCategory, gearCategories);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", resolvedPage);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageNumbers",
                IntStream.range(0, productPage.getTotalPages()).boxed().toList());
        model.addAttribute("categories", categoryService.getMainCategories());
        model.addAttribute("gearCategories", gearCategories);
        model.addAttribute("gearParentCategory", gearParentCategory);
        model.addAttribute("gearCategorySelected", gearCategorySelected);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("keyword", normalizedKeyword);
        model.addAttribute("sortField", resolvedSortField);
        model.addAttribute("sortDir", resolvedSortDir);
        model.addAttribute("size", resolvedSize);

        return "product/product-home";
    }

    private int resolvePageSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private String resolveSortField(String sortField) {
        return ALLOWED_SORT_FIELDS.contains(sortField) ? sortField : DEFAULT_SORT_FIELD;
    }

    private String resolveSortDirection(String sortDir) {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : DEFAULT_SORT_DIRECTION;
    }

    private Sort createSort(String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        return "asc".equals(sortDir) ? sort.ascending() : sort.descending();
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim().replaceAll("\\s+", " ");
        return normalized.isEmpty() ? null : normalized;
    }

    private boolean isGearCategorySelected(Integer categoryId, Category gearParentCategory, List<Category> gearCategories) {
        if (categoryId == null) {
            return false;
        }
        if (gearParentCategory != null && gearParentCategory.getCategoryId() == categoryId) {
            return true;
        }
        return gearCategories.stream()
                .anyMatch(category -> category.getCategoryId() == categoryId);
    }
}
