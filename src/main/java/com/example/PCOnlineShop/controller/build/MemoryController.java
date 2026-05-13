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

    private final MemoryService memoryService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/memory")
    public String showMemoryPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Memory> memories = buildService.getCompatibleMemory(buildItem);
        model.addAttribute("memories", memories);
        model.addAttribute("allBrands", memoryService.getAllBrands(memories));
        return MEMORY_VIEW;
    }

    @PostMapping("/memory/filter")
    public String filterMemories(@RequestParam(required = false) List<String> brands,
                                 @RequestParam(required = false) String sortBy,
                                 @ModelAttribute("buildItems") BuildItemDto buildItem,
                                 Model model) {
        List<Memory> memories = buildService.getCompatibleMemory(buildItem);
        memories = memoryService.filterMemories(memories, brands, sortBy);

        model.addAttribute("memories", memories);
        model.addAttribute("allBrands", memoryService.getAllBrands(buildService.getCompatibleMemory(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return MEMORY_VIEW;
    }

    @PostMapping("/selectMemory")
    public String selectMemory(@RequestParam(value = "memoryId", required = false) Integer memoryId,
                               @ModelAttribute("buildItems") BuildItemDto buildItem,
                               RedirectAttributes redirectAttributes) {
        // Memory is REQUIRED - must select one
        if (memoryId == null && buildItem.getMemory() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select memory to continue.");
            return "redirect:/build/memory";
        }

        // Only update if user selected new memory
        if (memoryId != null) {
            return memoryService.findSelectableMemoryByProductId(memoryId)
                    .map(memory -> {
                        buildItem.setMemory(memory);
                        return "redirect:/build/storage";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected memory is not available.");
                        return "redirect:/build/memory";
                    });
        }
        // If memoryId is null but buildItem.memory exists, keep it
        return "redirect:/build/storage";
    }
}
