package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Storage;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.StorageService;
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
public class StorageController {
    private static final String STORAGE_VIEW = "build/storage";

    private final StorageService storageService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/storage")
    public String showStoragePage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Storage> storages = buildService.getCompatibleStorage(buildItem);
        model.addAttribute("storages", storages);
        model.addAttribute("allBrands", storageService.getAllBrands(storages));
        return STORAGE_VIEW;
    }

    @PostMapping("/storage/filter")
    public String filterStorages(@RequestParam(required = false) List<String> brands,
                                 @RequestParam(required = false) String sortBy,
                                 @ModelAttribute("buildItems") BuildItemDto buildItem,
                                 Model model) {
        List<Storage> storages = buildService.getCompatibleStorage(buildItem);
        storages = storageService.filterStorages(storages, brands, sortBy);

        model.addAttribute("storages", storages);
        model.addAttribute("allBrands", storageService.getAllBrands(buildService.getCompatibleStorage(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return STORAGE_VIEW;
    }

    @PostMapping("/selectStorage")
    public String selectStorage(@RequestParam(value = "storageId", required = false) Integer storageId,
                                @ModelAttribute("buildItems") BuildItemDto buildItem,
                                RedirectAttributes redirectAttributes) {
        // Storage is REQUIRED - must select one
        if (storageId == null && buildItem.getStorage() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select storage to continue.");
            return "redirect:/build/storage";
        }

        // Only update if user selected new storage
        if (storageId != null) {
            return storageService.findSelectableStorageByProductId(storageId)
                    .map(storage -> {
                        buildItem.setStorage(storage);
                        return "redirect:/build/psu";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected storage is not available.");
                        return "redirect:/build/storage";
                    });
        }
        // If storageId is null but buildItem.storage exists, keep it
        return "redirect:/build/psu";
    }
}
