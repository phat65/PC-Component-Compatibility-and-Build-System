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
    private static final String REDIRECT_MAINBOARD = "redirect:/build/mainboard";
    private static final String REDIRECT_CPU = "redirect:/build/cpu";

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
        addMainboardModel(model, mainboards, mainboards, null, null);
        return MAINBOARD_VIEW;
    }

    // Filter motherboard by brands
    @PostMapping("/mainboard/filter")
    public String filterMainboards(@RequestParam(required = false) List<String> brands,
                                   @RequestParam(required = false) String sortBy,
                                   @ModelAttribute("buildItems") BuildItemDto buildItem,
                                   Model model) {
        List<Mainboard> compatibleMainboards = buildService.getCompatibleMainboards(buildItem);
        List<Mainboard> filteredMainboards = mainboardService.filterMainboards(compatibleMainboards, brands, sortBy);

        addMainboardModel(model, filteredMainboards, compatibleMainboards, brands, sortBy);
        return MAINBOARD_VIEW;
    }

    // Hiển thị chi tiết motherboard
    @Deprecated(forRemoval = true)
    @GetMapping("/mainboard/{id}")
    public String redirectMainboardDetail(@PathVariable int id) {
        return REDIRECT_MAINBOARD;
    }

    //Chọn motherboard
    @PostMapping("/selectMainboard")
    public String selectMainboard(@RequestParam(required = false) Integer mainboardId,
                                  @ModelAttribute("buildItems") BuildItemDto buildItem,
                                  RedirectAttributes redirectAttributes) {
        // Mainboard is REQUIRED - must select one
        if (mainboardId == null && buildItem.getMainboard() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a mainboard to continue.");
            return REDIRECT_MAINBOARD;
        }

        // Only update if user selected a new mainboard
        if (mainboardId == null) {
            return REDIRECT_CPU;
        }

        return buildService.findSelectableCompatibleMainboardByProductId(mainboardId, buildItem)
                .map(mainboard -> selectAndContinue(buildItem, mainboard))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addMainboardModel(Model model,
                                   List<Mainboard> mainboards,
                                   List<Mainboard> brandSource,
                                   List<String> selectedBrands,
                                   String selectedSort) {
        model.addAttribute("mainboards", mainboards);
        model.addAttribute("allBrands", mainboardService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, Mainboard mainboard) {
        buildItem.setMainboard(mainboard);
        return REDIRECT_CPU;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected mainboard is not available or compatible.");
        return REDIRECT_MAINBOARD;
    }

    // Adding, editing, and deleting motherboards will be done by the administrator via the admin page.
}
