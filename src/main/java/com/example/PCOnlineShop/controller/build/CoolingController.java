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
    private static final String REDIRECT_COOLING = "redirect:/build/cooling";
    private static final String REDIRECT_MEMORY = "redirect:/build/memory";

    private final CoolingService coolingService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/cooling")
    public String showCoolingPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Cooling> coolings = buildService.getCompatibleCoolings(buildItem);
        addCoolingModel(model, coolings, coolings, null, null);
        return COOLING_VIEW;
    }

    @PostMapping("/cooling/filter")
    public String filterCoolings(@RequestParam(required = false) List<String> brands,
                                 @RequestParam(required = false) String sortBy,
                                 @ModelAttribute("buildItems") BuildItemDto buildItem,
                                 Model model) {
        List<Cooling> compatibleCoolings = buildService.getCompatibleCoolings(buildItem);
        List<Cooling> filteredCoolings = coolingService.filterCoolings(compatibleCoolings, brands, sortBy);

        addCoolingModel(model, filteredCoolings, compatibleCoolings, brands, sortBy);
        return COOLING_VIEW;
    }

    @PostMapping("/selectCooling")
    public String selectCooling(@RequestParam(value = "coolingId", required = false) Integer coolingId,
                                @ModelAttribute("buildItems") BuildItemDto buildItem,
                                RedirectAttributes redirectAttributes) {
        if (coolingId == null) {
            return REDIRECT_MEMORY;
        }

        return buildService.findSelectableCompatibleCoolingByProductId(coolingId, buildItem)
                .map(cooling -> selectAndContinue(buildItem, cooling))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addCoolingModel(Model model,
                                 List<Cooling> coolings,
                                 List<Cooling> brandSource,
                                 List<String> selectedBrands,
                                 String selectedSort) {
        model.addAttribute("coolings", coolings);
        model.addAttribute("allBrands", coolingService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, Cooling cooling) {
        buildItem.setCooling(cooling);
        return REDIRECT_MEMORY;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected cooling is not available or compatible.");
        return REDIRECT_COOLING;
    }
}
