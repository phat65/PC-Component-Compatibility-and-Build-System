package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.PowerSupply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GpuCompatibilityValidator {

    private final BuildPowerCalculator buildPowerCalculator;

    public CompatibilityResult validate(BuildItemDto buildItem, GPU gpu) {
        CPU cpu = buildItem.getCpu();
        if (cpu != null && CompatibilityRules.parsePcieVersion(cpu.getPcieVersion())
                < CompatibilityRules.parsePcieVersion(gpu.getPcieVersion())) {
            return CompatibilityResult.incompatible("CPU PCIe version is lower than selected GPU requirement");
        }

        Mainboard mainboard = buildItem.getMainboard();
        if (mainboard != null && CompatibilityRules.parsePcieVersion(mainboard.getPcieVersion())
                < CompatibilityRules.parsePcieVersion(gpu.getPcieVersion())) {
            return CompatibilityResult.incompatible("Mainboard PCIe version is lower than selected GPU requirement");
        }

        Case pcCase = buildItem.getPcCase();
        if (pcCase != null && pcCase.getGpuMaxLength() < gpu.getLength()) {
            return CompatibilityResult.incompatible("Selected GPU is too long for selected case");
        }

        PowerSupply psu = buildItem.getPowerSupply();
        if (psu != null && !buildPowerCalculator.hasEnoughWattage(buildItem, psu)) {
            return CompatibilityResult.incompatible("Selected PSU does not provide enough wattage for this build");
        }

        return CompatibilityResult.success();
    }
}
