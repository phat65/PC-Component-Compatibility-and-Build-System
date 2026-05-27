package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.build.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/build")
@SessionAttributes("buildItems")
public class OtherController {
    private static final String OTHER_VIEW = "build/other";
    private static final String REDIRECT_OTHER = "redirect:/build/other";

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
    public String selectOther(@RequestParam(value = "otherId", required = false) Integer otherId,
                              @ModelAttribute("buildItems") BuildItemDto buildItems,
                              RedirectAttributes redirectAttributes) {
        if (otherId == null) {
            buildItems.setOther(null);
            redirectAttributes.addFlashAttribute("error", "Please select a product before finishing the build.");
            return REDIRECT_OTHER;
        }

        return buildService.findOtherByProductId(otherId)
                .map(other -> selectAndStay(buildItems, other))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addOtherProducts(Model model, List<Product> others) {
        model.addAttribute("others", others != null ? others : List.of());
    }

    private String selectAndStay(BuildItemDto buildItems, Product other) {
        buildItems.setOther(other);
        return REDIRECT_OTHER;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Product not found.");
        return REDIRECT_OTHER;
    }
}
