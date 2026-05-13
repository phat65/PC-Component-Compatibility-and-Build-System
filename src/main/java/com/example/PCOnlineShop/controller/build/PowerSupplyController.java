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

    private final PowerSupplyService powerSupplyService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/psu")
    public String showPsuPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<PowerSupply> psus = buildService.getCompatiblePowerSupplies(buildItem);
        model.addAttribute("psus", psus);
        model.addAttribute("allBrands", powerSupplyService.getAllBrands(psus));
        return PSU_VIEW;
    }

    @PostMapping("/psu/filter")
    public String filterPowerSupplies(@RequestParam(required = false) List<String> brands,
                                      @RequestParam(required = false) String sortBy,
                                      @ModelAttribute("buildItems") BuildItemDto buildItem,
                                      Model model) {
        List<PowerSupply> powerSupplies = buildService.getCompatiblePowerSupplies(buildItem);
        powerSupplies = powerSupplyService.filterPowerSupplies(powerSupplies, brands, sortBy);

        model.addAttribute("psus", powerSupplies);
        model.addAttribute("allBrands", powerSupplyService.getAllBrands(buildService.getCompatiblePowerSupplies(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return PSU_VIEW;
    }

    @PostMapping("/selectPsu")
    public String selectPsu(@RequestParam(value = "psuId", required = false) Integer psuId,
                            @ModelAttribute("buildItems") BuildItemDto buildItem,
                            RedirectAttributes redirectAttributes) {
        // Only update if user selected new PSU
        if (psuId != null) {
            return powerSupplyService.findSelectablePowerSupplyByProductId(psuId)
                    .map(psu -> {
                        buildItem.setPowerSupply(psu);
                        return "redirect:/build/other";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected PSU is not available.");
                        return "redirect:/build/psu";
                    });
        }
        // Stay on PSU page after selection
        return "redirect:/build/other";
    }
}
