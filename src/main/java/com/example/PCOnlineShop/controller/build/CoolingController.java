package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.CoolingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
@SessionAttributes({"buildItems"})
@RequestMapping("/build")
public class CoolingController {
    private static final String COOLING_VIEW = "build/cooling";

    private final CoolingService coolingService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/cooling")
    public String showCoolingPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Cooling> coolings = buildService.getCompatibleCoolings(buildItem);
        model.addAttribute("coolings", coolings);
        model.addAttribute("allBrands", coolingService.getAllBrands(coolings));
        return COOLING_VIEW;
    }

    @PostMapping("/cooling/filter")
    public String filterCoolings(@RequestParam(required = false) List<String> brands,
                                 @RequestParam(required = false) String sortBy,
                                 @ModelAttribute("buildItems") BuildItemDto buildItem,
                                 Model model) {
        List<Cooling> coolings = buildService.getCompatibleCoolings(buildItem);
        coolings = coolingService.filterCoolings(coolings, brands, sortBy);

        model.addAttribute("coolings", coolings);
        model.addAttribute("allBrands", coolingService.getAllBrands(buildService.getCompatibleCoolings(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return COOLING_VIEW;
    }

    @PostMapping("/selectCooling")
    public String selectCooling(@RequestParam(value = "coolingId", required = false) Integer coolingId,
                                @ModelAttribute("buildItems") BuildItemDto buildItem,
                                RedirectAttributes redirectAttributes) {
        // Cooling is OPTIONAL - can use stock cooler from CPU
        // Only update if user selected a cooling
        if (coolingId != null) {
            return coolingService.findSelectableCoolingByProductId(coolingId)
                    .map(cooling -> {
                        buildItem.setCooling(cooling);
                        return "redirect:/build/memory";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected cooling is not available.");
                        return "redirect:/build/cooling";
                    });
        }
        // Allow proceeding even if cooling is null
        return "redirect:/build/memory";
    }
}
