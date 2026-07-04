package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
@RequestMapping("/build")
@SessionAttributes("buildItems")
public class OtherController {
    private static final String OTHER_VIEW = "build/other";
    private static final String REDIRECT_GEAR = "redirect:/build/gear";

    private final BuildService buildService;
    private final CategoryService categoryService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping({"/gear", "/other"})
    public String showGearPage(@ModelAttribute("buildItems") BuildItemDto buildItems, Model model) {
        addOtherProducts(model, buildService.getOtherProducts());
        return OTHER_VIEW;
    }

    @PostMapping({"/selectGear", "/selectOther"})
    public String selectOther(@RequestParam(value = "otherIds", required = false) List<Integer> otherIds,
                              @ModelAttribute("buildItems") BuildItemDto buildItems,
                              RedirectAttributes redirectAttributes) {
        if (otherIds == null || otherIds.isEmpty()) {
            buildItems.clearOtherProducts();
            redirectAttributes.addFlashAttribute("message", "No optional accessories selected.");
            return REDIRECT_GEAR;
        }

        buildItems.clearOtherProducts();
        for (Integer otherId : otherIds) {
            if (otherId == null) {
                continue;
            }

            Product otherProduct = buildService.findOtherByProductId(otherId).orElse(null);
            if (otherProduct == null) {
                return rejectSelection(redirectAttributes);
            }
            buildItems.selectOtherProduct(otherProduct);
        }

        redirectAttributes.addFlashAttribute("message", "Optional accessories updated.");
        return REDIRECT_GEAR;
    }

    private void addOtherProducts(Model model, List<Product> others) {
        List<Product> safeOthers = others != null ? others : List.of();
        model.addAttribute("others", safeOthers);
        model.addAttribute("otherCategoryOptions", buildOtherCategoryOptions(safeOthers));
        model.addAttribute("otherCategoryMap", buildOtherCategoryMap(safeOthers));
        model.addAttribute("otherCategoryIdMap", buildOtherCategoryIdMap(safeOthers));
        model.addAttribute("otherCategoryLabelMap", buildOtherCategoryLabelMap(safeOthers));
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Product not found.");
        return REDIRECT_GEAR;
    }

    private Map<Integer, String> buildOtherCategoryMap(List<Product> others) {
        Map<Integer, String> categoryMap = new LinkedHashMap<>();
        for (Product product : others) {
            categoryMap.put(product.getProductId(), resolveOtherCategorySlug(product));
        }
        return categoryMap;
    }

    private Map<Integer, Integer> buildOtherCategoryIdMap(List<Product> others) {
        Map<Integer, Integer> categoryMap = new LinkedHashMap<>();
        for (Product product : others) {
            Category category = resolveOtherChildCategory(product);
            categoryMap.put(product.getProductId(), category != null ? category.getCategoryId() : null);
        }
        return categoryMap;
    }

    private Map<Integer, String> buildOtherCategoryLabelMap(List<Product> others) {
        Map<Integer, String> categoryMap = new LinkedHashMap<>();
        for (Product product : others) {
            Category category = resolveOtherChildCategory(product);
            categoryMap.put(product.getProductId(), category != null ? category.getCategoryName() : "Gear");
        }
        return categoryMap;
    }

    private List<OtherCategoryOption> buildOtherCategoryOptions(List<Product> others) {
        Map<String, OtherCategoryOption> options = new LinkedHashMap<>();
        for (Category category : categoryService.getGearCategories()) {
            if (category == null || category.getCategoryName() == null || category.getCategoryName().isBlank()) {
                continue;
            }
            String slug = toCategorySlug(category.getCategoryName());
            options.put(slug, new OtherCategoryOption(category.getCategoryName(), slug, category.getCategoryId()));
        }

        for (Product product : others) {
            Category category = resolveOtherChildCategory(product);
            String slug = category != null ? toCategorySlug(category.getCategoryName()) : resolveOtherCategorySlug(product);
            if (!options.containsKey(slug)) {
                options.put(slug, new OtherCategoryOption(toCategoryLabel(slug), slug, null));
            }
        }

        return List.copyOf(options.values());
    }

    private String resolveOtherCategorySlug(Product product) {
        if (product.getCategories() == null) {
            return "other";
        }

        Category otherChildCategory = resolveOtherChildCategory(product);
        if (otherChildCategory != null) {
            return toCategorySlug(otherChildCategory.getCategoryName());
        }

        return "other";
    }

    private Category resolveOtherChildCategory(Product product) {
        if (product.getCategories() == null) {
            return null;
        }

        Category gearChildCategory = product.getCategories().stream()
                .filter(Objects::nonNull)
                .filter(category -> category.getCategoryName() != null)
                .filter(this::isGearChildCategory)
                .findFirst()
                .orElse(null);

        if (gearChildCategory != null) {
            return gearChildCategory;
        }

        return product.getCategories().stream()
                .filter(Objects::nonNull)
                .filter(category -> category.getCategoryName() != null)
                .filter(category -> !isGearParentCategory(category))
                .findFirst()
                .orElse(null);
    }

    private boolean isGearChildCategory(Category category) {
        Category parent = category.getParent();
        return parent != null && isGearParentCategory(parent);
    }

    private boolean isGearParentCategory(Category category) {
        return category.getCategoryName() != null
                && "Other".equalsIgnoreCase(category.getCategoryName().trim());
    }

    private String toCategorySlug(String categoryName) {
        return categoryName.toLowerCase(Locale.ROOT).trim().replaceAll("[^a-z0-9]+", "");
    }

    private String toCategoryLabel(String slug) {
        if (slug == null || slug.isBlank()) {
            return "Gear";
        }
        return slug.substring(0, 1).toUpperCase(Locale.ROOT) + slug.substring(1);
    }

    public static class OtherCategoryOption {
        private final String label;
        private final String slug;
        private final Integer categoryId;

        private OtherCategoryOption(String label, String slug, Integer categoryId) {
            this.label = label;
            this.slug = slug;
            this.categoryId = categoryId;
        }

        public String getLabel() {
            return label;
        }

        public String getSlug() {
            return slug;
        }

        public Integer getCategoryId() {
            return categoryId;
        }
    }
}
