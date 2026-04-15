package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.build.BuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/build")
@SessionAttributes("buildItems")
public class OtherController {
    private static final String OTHER_VIEW = "build/other";

    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/other")
    public String showOtherPage(@ModelAttribute("buildItems") BuildItemDto buildItems, Model model) {
        List<Product> others = buildService.getOtherProducts();
        model.addAttribute("others", others != null ? others : new ArrayList<>());
        return OTHER_VIEW;
    }

    @PostMapping("/selectOther")
    public String selectOther(@RequestParam(value = "otherId", required = false) Integer otherId,
                              @ModelAttribute("buildItems") BuildItemDto buildItems,
                              RedirectAttributes redirectAttributes) {
        if (otherId == null) {
            buildItems.setOther(null);
            redirectAttributes.addFlashAttribute("message", "Please select a product before finishing the build.");
            return "redirect:/build/other";
        }

        Optional<Product> otherOpt = buildService.findOtherByProductId(otherId);
        if (otherOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Product not found.");
            return "redirect:/build/other";
        }

        buildItems.setOther(otherOpt.get());
        return "redirect:/build/other";
    }
}
