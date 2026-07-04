package com.example.PCOnlineShop.controller.category;

import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.product.CategoryService;
import com.example.PCOnlineShop.service.product.ProductService;
import com.example.PCOnlineShop.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 48;
    private static final String DEFAULT_SORT_FIELD = "productId";
    private static final String DEFAULT_SORT_DIRECTION = "desc";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "productId",
            "productName",
            "price",
            "createAt",
            "performanceScore"
    );

    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * ✅ Hiển thị tất cả sản phẩm thuộc một category
     */
    @GetMapping("/{id}")
    public String viewCategoryProducts(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_FIELD) String sortField,
            @RequestParam(defaultValue = DEFAULT_SORT_DIRECTION) String sortDir,
            Model model
    ) {
        Category category = categoryService.findById(id).orElse(null);
        if (category == null) {
            model.addAttribute("error", "Category not found!");
            return "error/404";
        }

        int resolvedPage = Math.max(page, 0);
        int resolvedSize = resolvePageSize(size);
        String resolvedSortField = resolveSortField(sortField);
        String resolvedSortDir = resolveSortDirection(sortDir);
        Pageable pageable = PageRequest.of(
                resolvedPage,
                resolvedSize,
                createSort(resolvedSortField, resolvedSortDir)
        );
        Page<Product> productPage = productService.searchVisibleSellingProducts(id, null, pageable);

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", category);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", resolvedPage);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageNumbers",
                PaginationUtils.compactPageNumbers(resolvedPage, productPage.getTotalPages()));
        model.addAttribute("size", resolvedSize);
        model.addAttribute("sortField", resolvedSortField);
        model.addAttribute("sortDir", resolvedSortDir);

        return "product/category-products";
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
}
