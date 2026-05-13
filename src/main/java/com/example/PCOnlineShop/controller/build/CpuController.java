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
        model.addAttribute("cpus", cpus);
        model.addAttribute("allBrands", cpuService.getAllBrands(cpus));
        return CPU_VIEW;
    }

    // Filter CPU by brands
    @PostMapping("/cpu/filter")
    public String filterCpus(@RequestParam(required = false) List<String> brands,
                             @RequestParam(required = false) String sortBy,
                             @ModelAttribute("buildItems") BuildItemDto buildItem,
                             Model model) {
        List<CPU> cpus = buildService.getCompatibleCpus(buildItem);
        cpus = cpuService.filterCpus(cpus, brands, sortBy);

        model.addAttribute("cpus", cpus);
        model.addAttribute("allBrands", cpuService.getAllBrands(buildService.getCompatibleCpus(buildItem)));
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedSort", sortBy);
        return CPU_VIEW;
    }

    // Chọn CPU
    // Chọn CPU sẽ lưu vào buildItem và chuyển sang bước chọn linh kiện tiếp theo
    @PostMapping("/selectCpu")
    public String selectCpu(@RequestParam(required = false) Integer cpuId,
                            @ModelAttribute("buildItems") BuildItemDto buildItem,
                            RedirectAttributes redirectAttributes) {
        // CPU is REQUIRED - must select one
        if (cpuId == null && buildItem.getCpu() == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a CPU to continue.");
            return "redirect:/build/cpu";
        }

        // Only update if user selected a new CPU
        if (cpuId != null) {
            return cpuService.findSelectableCpuByProductId(cpuId)
                    .map(cpu -> {
                        buildItem.setCpu(cpu);
                        return "redirect:/build/gpu";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Selected CPU is not available.");
                        return "redirect:/build/cpu";
                    });
        }
        // If cpuId is null but buildItem.cpu exists, keep it
        return "redirect:/build/gpu";
    }
}
