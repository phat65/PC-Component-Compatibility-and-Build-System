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
        model.addAttribute("gpus", gpus);
        model.addAttribute("allBrands", gpuService.getAllBrands(gpus));
        return GPU_VIEW;
    }

    // Filter GPU by brands
    @PostMapping("/gpu/filter")
    public String filterGpus(@RequestParam(required = false) List<String> brands,
                             @RequestParam(required = false) String sortBy,
                             @ModelAttribute("buildItems") BuildItemDto buildItem,
                             Model model) {
        List<GPU> gpus = buildService.getCompatibleGPUs(buildItem);
        gpus = gpuService.filterGpus(gpus, brands, sortBy);

        model.addAttribute("gpus", gpus);
        model.addAttribute("allBrands", gpuService.getAllBrands(buildService.getCompatibleGPUs(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return GPU_VIEW;
    }

    // Chon GPU
    @PostMapping("/selectGpu")
    public String selectGpu(@RequestParam(required = false) Integer gpuId,
                            @ModelAttribute("buildItems") BuildItemDto buildItem,
                            RedirectAttributes redirectAttributes) {
        // GPU is OPTIONAL - can proceed without it (using iGPU from CPU)
        // Only update if user selected a GPU
        if (gpuId != null) {
            return gpuService.findSelectableGpuByProductId(gpuId)
                    .map(gpu -> {
                        buildItem.setGpu(gpu);
                        return "redirect:/build/case";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected GPU is not available.");
                        return "redirect:/build/gpu";
                    });
        }
        // Allow proceeding even if GPU is null
        return "redirect:/build/case";
    }
}
