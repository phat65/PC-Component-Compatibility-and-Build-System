package com.example.PCOnlineShop.controller.build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.MainboardService;

import lombok.RequiredArgsConstructor;

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
        model.addAttribute("mainboards", buildService.getCompatibleMainboards(buildItem));
        model.addAttribute("allBrands", mainboardService.getAllBrands(buildItem));
        return MAINBOARD_VIEW;
    }

    // Filter motherboard by brands
    @PostMapping("/mainboard/filter")
    public String filterMainboards(@RequestParam(required = false) List<String> brands,
                                   @RequestParam(required = false) String sortBy,
                                   @ModelAttribute("buildItems") BuildItemDto buildItem,
                                   Model model) {
        List<Mainboard> mainboards = buildService.getCompatibleMainboards(buildItem);
        Map<String,List<String>> filters = new HashMap<>();
        filters.put("brands", brands);
        mainboards = mainboardService.filterMainboards(mainboards, filters, sortBy);


        model.addAttribute("mainboards", mainboards);
        model.addAttribute("allBrands", mainboardService.getAllBrands(buildItem));
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
                                  org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        // Mainboard is REQUIRED - must select one
        if (mainboardId == null && buildItem.getMainboard() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a mainboard to continue.");
            return "redirect:/build/mainboard";
        }

        // Only update if user selected a new mainboard
        if (mainboardId != null) {
            buildItem.setMainboard(mainboardService.getMainboardById(mainboardId));
        }
        // If mainboardId is null but buildItem.mainboard exists, keep it
        return "redirect:/build/cpu";
    }
    // Adding, editing, and deleting motherboards will be done by the administrator via the admin page.
}
