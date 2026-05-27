package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/build")
@SessionAttributes({"buildItems"})
public class CaseController {
    private static final String CASE_VIEW = "build/cases";
    private static final String REDIRECT_CASE = "redirect:/build/case";
    private static final String REDIRECT_COOLING = "redirect:/build/cooling";

    private final CaseService caseService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/case")
    public String showCaseSelectionPage(Model model, @ModelAttribute("buildItems") BuildItemDto buildItem) {
        List<Case> cases = buildService.getCompatibleCases(buildItem);
        addCaseModel(model, cases, cases, null, null);
        return CASE_VIEW;
    }

    @PostMapping("/case/filter")
    public String filterCases(@RequestParam(required = false) List<String> brands,
                              @RequestParam(required = false) String sortBy,
                              @ModelAttribute("buildItems") BuildItemDto buildItem,
                              Model model) {
        List<Case> compatibleCases = buildService.getCompatibleCases(buildItem);
        List<Case> filteredCases = caseService.filterCases(compatibleCases, brands, sortBy);

        addCaseModel(model, filteredCases, compatibleCases, brands, sortBy);
        return CASE_VIEW;
    }

    @PostMapping("/selectCase")
    public String selectCase(@RequestParam(value = "caseId", required = false) Integer caseId,
                             @ModelAttribute("buildItems") BuildItemDto buildItem,
                             RedirectAttributes redirectAttributes) {
        if (caseId == null && buildItem.getPcCase() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a case to continue.");
            return REDIRECT_CASE;
        }

        if (caseId == null) {
            return REDIRECT_COOLING;
        }

        return buildService.findSelectableCompatibleCaseByProductId(caseId, buildItem)
                .map(pcCase -> selectAndContinue(buildItem, pcCase))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addCaseModel(Model model,
                              List<Case> cases,
                              List<Case> brandSource,
                              List<String> selectedBrands,
                              String selectedSort) {
        model.addAttribute("cases", cases);
        model.addAttribute("allBrands", caseService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, Case pcCase) {
        buildItem.setPcCase(pcCase);
        return REDIRECT_COOLING;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected case is not available or compatible.");
        return REDIRECT_CASE;
    }
}
