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
    private static final String REDIRECT_STORAGE = "redirect:/build/storage";
    private static final String REDIRECT_PSU = "redirect:/build/psu";

    private final StorageService storageService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping("/storage")
    public String showStoragePage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<Storage> storages = buildService.getCompatibleStorage(buildItem);
        addStorageModel(model, storages, storages, null, null);
        return STORAGE_VIEW;
    }

    @PostMapping("/storage/filter")
    public String filterStorages(@RequestParam(required = false) List<String> brands,
                                 @RequestParam(required = false) String sortBy,
                                 @ModelAttribute("buildItems") BuildItemDto buildItem,
                                 Model model) {
        List<Storage> compatibleStorages = buildService.getCompatibleStorage(buildItem);
        List<Storage> filteredStorages = storageService.filterStorages(compatibleStorages, brands, sortBy);

        addStorageModel(model, filteredStorages, compatibleStorages, brands, sortBy);
        return STORAGE_VIEW;
    }

    @PostMapping("/selectStorage")
    public String selectStorage(@RequestParam(value = "storageId", required = false) Integer storageId,
                                @ModelAttribute("buildItems") BuildItemDto buildItem,
                                RedirectAttributes redirectAttributes) {
        // Storage is REQUIRED - must select one
        if (storageId == null && buildItem.getStorage() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select storage to continue.");
            return REDIRECT_STORAGE;
        }

        // Only update if user selected new storage
        if (storageId == null) {
            return REDIRECT_PSU;
        }

        return buildService.findSelectableCompatibleStorageByProductId(storageId, buildItem)
                .map(storage -> selectAndContinue(buildItem, storage))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addStorageModel(Model model,
                                 List<Storage> storages,
                                 List<Storage> brandSource,
                                 List<String> selectedBrands,
                                 String selectedSort) {
        model.addAttribute("storages", storages);
        model.addAttribute("allBrands", storageService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, Storage storage) {
        buildItem.setStorage(storage);
        return REDIRECT_PSU;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected storage is not available or compatible.");
        return REDIRECT_STORAGE;
    }
}
