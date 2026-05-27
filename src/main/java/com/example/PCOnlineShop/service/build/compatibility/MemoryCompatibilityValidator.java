package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import org.springframework.stereotype.Component;

@Component
public class MemoryCompatibilityValidator {

    public CompatibilityResult validate(BuildItemDto buildItem, Memory memory) {
        Mainboard mainboard = buildItem.getMainboard();
        if (mainboard != null) {
            if (!mainboard.getMemoryType().equals(memory.getType())) {
                return CompatibilityResult.incompatible("Selected memory type does not match selected mainboard");
            }
            if (memory.getModules() > mainboard.getMemorySlots()) {
                return CompatibilityResult.incompatible("Selected memory modules exceed mainboard memory slots");
            }
            if (memory.getSpeed() > mainboard.getMaxMemorySpeed()) {
                return CompatibilityResult.incompatible("Selected memory speed exceeds mainboard maximum memory speed");
            }
        }

        CPU cpu = buildItem.getCpu();
        if (cpu != null) {
            if (memory.getSpeed() > cpu.getMaxMemorySpeed()) {
                return CompatibilityResult.incompatible("Selected memory speed exceeds CPU maximum memory speed");
            }
            if (cpu.getMemoryChannels() != null && memory.getModules() > 0
                    && memory.getModules() > cpu.getMemoryChannels() * 2) {
                return CompatibilityResult.incompatible("Selected memory modules exceed CPU memory channel support");
            }
        }

        return CompatibilityResult.success();
    }
}
