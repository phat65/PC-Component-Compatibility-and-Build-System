package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.build.BuildService;
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
    private static final String REDIRECT_OTHER = "redirect:/build/other";
    private static final List<OtherCategoryOption> OTHER_CATEGORY_OPTIONS = List.of(
            new OtherCategoryOption("Keyboard", "keyboard"),
            new OtherCategoryOption("Mouse", "mouse"),
            new OtherCategoryOption("Monitor", "monitor"),
            new OtherCategoryOption("Chair", "chair"),
            new OtherCategoryOption("Headset", "headset"),
            new OtherCategoryOption("Mousepad", "mousepad"),
            new OtherCategoryOption("Speaker", "speaker"),
            new OtherCategoryOption("Desk", "desk"),
            new OtherCategoryOption("Webcam", "webcam"),
            new OtherCategoryOption("Add-on", "addon")
    );

    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/other")
    public String showOtherPage(@ModelAttribute("buildItems") BuildItemDto buildItems, Model model) {
        addOtherProducts(model, buildService.getOtherProducts());
        return OTHER_VIEW;
    }

    @PostMapping("/selectOther")
    public String selectOther(@RequestParam(value = "otherIds", required = false) List<Integer> otherIds,
                              @ModelAttribute("buildItems") BuildItemDto buildItems,
                              RedirectAttributes redirectAttributes) {
        if (otherIds == null || otherIds.isEmpty()) {
            buildItems.clearOtherProducts();
            redirectAttributes.addFlashAttribute("message", "Optional accessories cleared.");
            return REDIRECT_OTHER;
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
        return REDIRECT_OTHER;
    }

    private void addOtherProducts(Model model, List<Product> others) {
        List<Product> safeOthers = others != null ? others : List.of();
        model.addAttribute("others", safeOthers);
        model.addAttribute("otherCategoryOptions", OTHER_CATEGORY_OPTIONS);
        model.addAttribute("otherCategoryMap", buildOtherCategoryMap(safeOthers));
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Product not found.");
        return REDIRECT_OTHER;
    }

    private Map<Integer, String> buildOtherCategoryMap(List<Product> others) {
        Map<Integer, String> categoryMap = new LinkedHashMap<>();
        for (Product product : others) {
            categoryMap.put(product.getProductId(), resolveOtherCategorySlug(product));
        }
        return categoryMap;
    }

    private String resolveOtherCategorySlug(Product product) {
        if (product.getCategories() == null) {
            return "other";
        }

        return product.getCategories().stream()
                .filter(Objects::nonNull)
                .map(category -> category.getCategoryName() == null ? "" : category.getCategoryName().trim())
                .filter(categoryName -> !"Other".equalsIgnoreCase(categoryName))
                .map(this::toCategorySlug)
                .findFirst()
                .orElse("other");
    }

    private String toCategorySlug(String categoryName) {
        return categoryName.toLowerCase(Locale.ROOT).replace("-", "");
    }

    public static class OtherCategoryOption {
        private final String label;
        private final String slug;

        private OtherCategoryOption(String label, String slug) {
            this.label = label;
            this.slug = slug;
        }

        public String getLabel() {
            return label;
        }

        public String getSlug() {
            return slug;
        }
    }
}
