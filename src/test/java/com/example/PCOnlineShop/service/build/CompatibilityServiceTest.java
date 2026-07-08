package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.service.build.compatibility.BuildPowerCalculator;
import com.example.PCOnlineShop.service.build.compatibility.CaseCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.CoolingCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.CpuCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.GpuCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.MainboardCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.MemoryCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.PowerSupplyCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.StorageCompatibilityValidator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompatibilityServiceTest {

    private static final String PSU_WATTAGE_ERROR =
            "Selected PSU does not provide enough wattage for this build";

    private final BuildPowerCalculator powerCalculator = new BuildPowerCalculator();
    private final CoolingCompatibilityValidator coolingValidator = new CoolingCompatibilityValidator();
    private final CompatibilityService compatibilityService = new CompatibilityService(
            new MainboardCompatibilityValidator(),
            new CpuCompatibilityValidator(),
            new GpuCompatibilityValidator(powerCalculator),
            new CaseCompatibilityValidator(coolingValidator),
            new PowerSupplyCompatibilityValidator(powerCalculator),
            new MemoryCompatibilityValidator(),
            new StorageCompatibilityValidator(),
            coolingValidator);

    @Test
    void validateFullBuildDeduplicatesRepeatedReasons() {
        BuildItemDto buildItem = new BuildItemDto();
        buildItem.setGpu(gpu(300));
        buildItem.setPowerSupply(powerSupply(300));

        List<String> errors = compatibilityService.validateFullBuild(buildItem);

        assertThat(errors).contains(PSU_WATTAGE_ERROR);
        assertThat(Collections.frequency(errors, PSU_WATTAGE_ERROR)).isEqualTo(1);
    }

    private GPU gpu(int tdp) {
        GPU gpu = new GPU();
        gpu.setPcieVersion("PCIe 4.0 x16");
        gpu.setTdp(tdp);
        return gpu;
    }

    private PowerSupply powerSupply(int wattage) {
        PowerSupply powerSupply = new PowerSupply();
        powerSupply.setWattage(wattage);
        powerSupply.setFormFactor("ATX");
        return powerSupply;
    }
}
