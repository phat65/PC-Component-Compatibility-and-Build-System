package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.MemoryService;
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
public class MemoryController {
    private static final String MEMORY_VIEW = "build/memory";
    private static final String REDIRECT_MEMORY = "redirect:/build/memory";
    private static final String REDIRECT_STORAGE = "redirect:/build/storage";

    private final MemoryService memoryService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/memory")
    public String showMemoryPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Memory> memories = buildService.getCompatibleMemory(buildItem);
        addMemoryModel(model, memories, memories, null, null);
        return MEMORY_VIEW;
    }

    @PostMapping("/memory/filter")
    public String filterMemories(@RequestParam(required = false) List<String> brands,
                                 @RequestParam(required = false) String sortBy,
                                 @ModelAttribute("buildItems") BuildItemDto buildItem,
                                 Model model) {
        List<Memory> compatibleMemories = buildService.getCompatibleMemory(buildItem);
        List<Memory> filteredMemories = memoryService.filterMemories(compatibleMemories, brands, sortBy);

        addMemoryModel(model, filteredMemories, compatibleMemories, brands, sortBy);
        return MEMORY_VIEW;
    }

    @PostMapping("/selectMemory")
    public String selectMemory(@RequestParam(value = "memoryId", required = false) Integer memoryId,
                               @ModelAttribute("buildItems") BuildItemDto buildItem,
                               RedirectAttributes redirectAttributes) {
        // Memory is REQUIRED - must select one
        if (memoryId == null && buildItem.getMemory() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select memory to continue.");
            return REDIRECT_MEMORY;
        }

        // Only update if user selected new memory
        if (memoryId == null) {
            return REDIRECT_STORAGE;
        }

        return buildService.findSelectableCompatibleMemoryByProductId(memoryId, buildItem)
                .map(memory -> selectAndContinue(buildItem, memory))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addMemoryModel(Model model,
                                List<Memory> memories,
                                List<Memory> brandSource,
                                List<String> selectedBrands,
                                String selectedSort) {
        model.addAttribute("memories", memories);
        model.addAttribute("allBrands", memoryService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, Memory memory) {
        buildItem.setMemory(memory);
        return REDIRECT_STORAGE;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected memory is not available or compatible.");
        return REDIRECT_MEMORY;
    }
}
