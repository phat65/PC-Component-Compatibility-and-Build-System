package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.PowerSupplyService;
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
public class PowerSupplyController {
    private static final String PSU_VIEW = "build/psu";
    private static final String REDIRECT_PSU = "redirect:/build/psu";
    private static final String REDIRECT_OTHER = "redirect:/build/other";

    private final PowerSupplyService powerSupplyService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/psu")
    public String showPsuPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<PowerSupply> psus = buildService.getCompatiblePowerSupplies(buildItem);
        addPowerSupplyModel(model, psus, psus, null, null);
        return PSU_VIEW;
    }

    @PostMapping("/psu/filter")
    public String filterPowerSupplies(@RequestParam(required = false) List<String> brands,
                                      @RequestParam(required = false) String sortBy,
                                      @ModelAttribute("buildItems") BuildItemDto buildItem,
                                      Model model) {
        List<PowerSupply> compatiblePowerSupplies = buildService.getCompatiblePowerSupplies(buildItem);
        List<PowerSupply> filteredPowerSupplies = powerSupplyService.filterPowerSupplies(
                compatiblePowerSupplies, brands, sortBy);

        addPowerSupplyModel(model, filteredPowerSupplies, compatiblePowerSupplies, brands, sortBy);
        return PSU_VIEW;
    }

    @PostMapping("/selectPsu")
    public String selectPsu(@RequestParam(value = "psuId", required = false) Integer psuId,
                            @ModelAttribute("buildItems") BuildItemDto buildItem,
                            RedirectAttributes redirectAttributes) {
        // Only update if user selected new PSU
        if (psuId == null) {
            return REDIRECT_OTHER;
        }

        return buildService.findSelectableCompatiblePowerSupplyByProductId(psuId, buildItem)
                .map(psu -> selectAndContinue(buildItem, psu))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addPowerSupplyModel(Model model,
                                     List<PowerSupply> powerSupplies,
                                     List<PowerSupply> brandSource,
                                     List<String> selectedBrands,
                                     String selectedSort) {
        model.addAttribute("psus", powerSupplies);
        model.addAttribute("allBrands", powerSupplyService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, PowerSupply powerSupply) {
        buildItem.setPowerSupply(powerSupply);
        return REDIRECT_OTHER;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected PSU is not available or compatible.");
        return REDIRECT_PSU;
    }
}
