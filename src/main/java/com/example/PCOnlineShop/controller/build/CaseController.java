package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/build")
@SessionAttributes({"buildItems"})
public class CaseController {
    private static final String CASE_VIEW = "build/cases";

    private final CaseService caseService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/case")
    public String showCaseSelectionPage(Model model, @ModelAttribute("buildItems") BuildItemDto buildItem) {
        List<Case> cases = buildService.getCompatibleCases(buildItem);
        model.addAttribute("cases", cases);
        model.addAttribute("allBrands", caseService.getAllBrands(cases));
        return CASE_VIEW;
    }

    @PostMapping("/case/filter")
    public String filterCases(@RequestParam(required = false) List<String> brands,
                              @RequestParam(required = false) String sortBy,
                              @ModelAttribute("buildItems") BuildItemDto buildItem,
                              Model model) {
        List<Case> cases = buildService.getCompatibleCases(buildItem);
        cases = caseService.filterCases(cases, brands, sortBy);

        model.addAttribute("cases", cases);
        model.addAttribute("allBrands", caseService.getAllBrands(buildService.getCompatibleCases(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return CASE_VIEW;
    }

    @PostMapping("/selectCase")
    public String selectCase(@RequestParam(value = "caseId", required = false) Integer caseId,
                           @ModelAttribute("buildItems") BuildItemDto buildItem,
                           org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        // Case is REQUIRED - must select one
        if (caseId == null && buildItem.getPcCase() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a case to continue.");
            return "redirect:/build/case";
        }

        // Only update if user selected a new case
        if (caseId != null) {
            return caseService.findSelectableCaseByProductId(caseId)
                    .map(pcCase -> {
                        buildItem.setPcCase(pcCase);
                        return "redirect:/build/cooling";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected case is not available.");
                        return "redirect:/build/case";
                    });
        }
        // If caseId is null but buildItem.pcCase exists, keep it
        return "redirect:/build/cooling";
    }
}
