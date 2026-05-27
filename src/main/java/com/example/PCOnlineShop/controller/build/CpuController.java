package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.service.build.BuildService;
import com.example.PCOnlineShop.service.build.CpuService;
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
public class CpuController {
    private static final String CPU_VIEW = "build/build-cpu";
    private static final String REDIRECT_CPU = "redirect:/build/cpu";
    private static final String REDIRECT_GPU = "redirect:/build/gpu";

    private final CpuService cpuService;
    private final BuildService buildService;

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    // Hiển thị danh sách CPU
    @GetMapping("/cpu")
    public String showCpuPage(@ModelAttribute("buildItems") BuildItemDto buildItem, Model model) {
        List<CPU> cpus = buildService.getCompatibleCpus(buildItem);
        addCpuModel(model, cpus, cpus, null, null);
        return CPU_VIEW;
    }

    // Filter CPU by brands
    @PostMapping("/cpu/filter")
    public String filterCpus(@RequestParam(required = false) List<String> brands,
                             @RequestParam(required = false) String sortBy,
                             @ModelAttribute("buildItems") BuildItemDto buildItem,
                             Model model) {
        List<CPU> compatibleCpus = buildService.getCompatibleCpus(buildItem);
        List<CPU> filteredCpus = cpuService.filterCpus(compatibleCpus, brands, sortBy);

        addCpuModel(model, filteredCpus, compatibleCpus, brands, sortBy);
        return CPU_VIEW;
    }

    // Chọn CPU
    // Chọn CPU sẽ lưu vào buildItem và chuyển sang bước chọn linh kiện tiếp theo
    @PostMapping("/selectCpu")
    public String selectCpu(@RequestParam(value = "cpuId", required = false) Integer cpuId,
                            @ModelAttribute("buildItems") BuildItemDto buildItem,
                            RedirectAttributes redirectAttributes) {
        // CPU is REQUIRED - must select one
        if (cpuId == null && buildItem.getCpu() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a CPU to continue.");
            return REDIRECT_CPU;
        }

        // Only update if user selected a new CPU
        if (cpuId == null) {
            return REDIRECT_GPU;
        }

        return buildService.findSelectableCompatibleCpuByProductId(cpuId, buildItem)
                .map(cpu -> selectAndContinue(buildItem, cpu))
                .orElseGet(() -> rejectSelection(redirectAttributes));
    }

    private void addCpuModel(Model model,
                             List<CPU> cpus,
                             List<CPU> brandSource,
                             List<String> selectedBrands,
                             String selectedSort) {
        model.addAttribute("cpus", cpus);
        model.addAttribute("allBrands", cpuService.getAllBrands(brandSource));
        model.addAttribute("selectedBrands", selectedBrands);
        model.addAttribute("selectedSort", selectedSort);
    }

    private String selectAndContinue(BuildItemDto buildItem, CPU cpu) {
        buildItem.setCpu(cpu);
        return REDIRECT_GPU;
    }

    private String rejectSelection(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Selected CPU is not available or compatible.");
        return REDIRECT_CPU;
    }
}
