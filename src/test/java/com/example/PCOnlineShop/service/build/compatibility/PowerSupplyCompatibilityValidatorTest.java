package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.build.Storage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PowerSupplyCompatibilityValidatorTest {

    private final BuildPowerCalculator powerCalculator = new BuildPowerCalculator();
    private final PowerSupplyCompatibilityValidator validator =
            new PowerSupplyCompatibilityValidator(powerCalculator);

    @Test
    void rejectsPsuBelowBuildTdpWithBuffer() {
        PowerSupply psu = powerSupply(500);

        CompatibilityResult result = validator.validate(powerHungryBuild(), psu);

        assertThat(powerCalculator.calculateTotalTdp(powerHungryBuild())).isEqualTo(504);
        assertThat(result.compatible()).isFalse();
        assertThat(result.reasons()).contains("Selected PSU does not provide enough wattage for this build");
    }

    @Test
    void acceptsPsuThatMeetsBuildTdpWithBuffer() {
        PowerSupply psu = powerSupply(550);

        CompatibilityResult result = validator.validate(powerHungryBuild(), psu);

        assertThat(result.compatible()).isTrue();
    }

    private BuildItemDto powerHungryBuild() {
        CPU cpu = new CPU();
        cpu.setTdp(100);

        GPU gpu = new GPU();
        gpu.setTdp(250);

        Memory memory = new Memory();
        memory.setTdp(10);

        Storage storage = new Storage();

        Cooling cooling = new Cooling();
        cooling.setTdp(50);

        BuildItemDto buildItem = new BuildItemDto();
        buildItem.setCpu(cpu);
        buildItem.setGpu(gpu);
        buildItem.setMemory(memory);
        buildItem.setStorage(storage);
        buildItem.setCooling(cooling);
        return buildItem;
    }

    private PowerSupply powerSupply(int wattage) {
        PowerSupply powerSupply = new PowerSupply();
        powerSupply.setWattage(wattage);
        powerSupply.setFormFactor("ATX");
        return powerSupply;
    }
}
