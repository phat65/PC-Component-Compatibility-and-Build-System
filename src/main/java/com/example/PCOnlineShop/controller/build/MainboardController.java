package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.MainboardService;
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
public class MainboardController {
    private static final String MAINBOARD_VIEW = "build/mainboards";

    private final MainboardService mainboardService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    // show list motherboard
    @GetMapping("/mainboard")
    public String showMainboardPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Mainboard> mainboards = buildService.getCompatibleMainboards(buildItem);
        model.addAttribute("mainboards", mainboards);
        model.addAttribute("allBrands", mainboardService.getAllBrands(mainboards));
        return MAINBOARD_VIEW;
    }

    // Filter motherboard by brands
    @PostMapping("/mainboard/filter")
    public String filterMainboards(@RequestParam(required = false) List<String> brands,
                                   @RequestParam(required = false) String sortBy,
                                   @ModelAttribute("buildItems") BuildItemDto buildItem,
                                   Model model) {
        List<Mainboard> mainboards = buildService.getCompatibleMainboards(buildItem);
        mainboards = mainboardService.filterMainboards(mainboards, brands, sortBy);

        model.addAttribute("mainboards", mainboards);
        model.addAttribute("allBrands", mainboardService.getAllBrands(buildService.getCompatibleMainboards(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return MAINBOARD_VIEW;
    }

    // Hiển thị chi tiết motherboard
    @Deprecated(forRemoval = true)
    @GetMapping("/mainboard/{id}")
    public String redirectMainboardDetail(@PathVariable int id) {
        return "redirect:/build/mainboard";
    }

    //Chọn motherboard
    @PostMapping("/selectMainboard")
    public String selectMainboard(@RequestParam(required = false) Integer mainboardId,
                                  @ModelAttribute("buildItems") BuildItemDto buildItem,
                                  RedirectAttributes redirectAttributes) {
        // Mainboard is REQUIRED - must select one
        if (mainboardId == null && buildItem.getMainboard() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a mainboard to continue.");
            return "redirect:/build/mainboard";
        }

        // Only update if user selected a new mainboard
        if (mainboardId != null) {
            return mainboardService.findSelectableMainboardByProductId(mainboardId)
                    .map(mainboard -> {
                        buildItem.setMainboard(mainboard);
                        return "redirect:/build/cpu";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected mainboard is not available.");
                        return "redirect:/build/mainboard";
                    });
        }
        // If mainboardId is null but buildItem.mainboard exists, keep it
        return "redirect:/build/cpu";
    }
    // Adding, editing, and deleting motherboards will be done by the administrator via the admin page.
}
