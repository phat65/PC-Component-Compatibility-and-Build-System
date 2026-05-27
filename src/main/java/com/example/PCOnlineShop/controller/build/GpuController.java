package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.GpuService;
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
public class GpuController {
    private static final String GPU_VIEW = "build/build-gpu";
    private static final String REDIRECT_GPU = "redirect:/build/gpu";
    private static final String REDIRECT_CASE = "redirect:/build/case";

    private final BuildService buildService;
    private final GpuService gpuService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    // Hien thi danh sach GPU
    @GetMapping("/gpu")
    public String showGpuPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<GPU> gpus = buildService.getCompatibleGPUs(buildItem);
        addGpuModel(model, gpus, gpus, null, null);
        return GPU_VIEW;
    }

    // Filter GPU by brands
    @PostMapping("/gpu/filter")
    public String filterGpus(@RequestParam(required = false) List<String> brands,
                             @RequestParam(required = false) String sortBy,
                             @ModelAttribute("buildItems") BuildItemDto buildItem,
                             Model model) {
        List<GPU> compatibleGpus = buildService.getCompatibleGPUs(buildItem);
        List<GPU> filteredGpus = gpuService.filterGpus(compatibleGpus, brands, sortBy);

        addGpuModel(model, filteredGpus, compatibleGpus, brands, sortBy);
        return GPU_VIEW;
    }

    // Chon GPU
    @PostMapping("/selectGpu")
    public String selectGpu(@RequestParam(required = false) Integer gpuId,
                            @ModelAttribute("buildItems") BuildItemDto buildItem,
                            RedirectAttributes redirectAttributes) {
        // GPU is OPTIONAL - can proceed without it (using iGPU from CPU)
        // Only update if user selected a GPU
        if (gpuId == null) {
            return REDIRECT_CASE;
        }

        return buildService.findSelectableCompatibleGpuByProductId(gpuId, buildItem)
                .map(gpu -> selectAndContinue(buildItem, gpu))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addGpuModel(Model model,
                             List<GPU> gpus,
                             List<GPU> brandSource,
                             List<String> selectedBrands,
                             String selectedSort) {
        model.addAttribute("gpus", gpus);
        model.addAttribute("allBrands", gpuService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, GPU gpu) {
        buildItem.setGpu(gpu);
        return REDIRECT_CASE;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected GPU is not available or compatible.");
        return REDIRECT_GPU;
    }
}
