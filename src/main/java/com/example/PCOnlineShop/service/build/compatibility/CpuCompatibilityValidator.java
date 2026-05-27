package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import org.springframework.stereotype.Component;

@Component
public class CpuCompatibilityValidator {

    public CompatibilityResult validate(BuildItemDto buildItem, CPU cpu) {
        Mainboard mainboard = buildItem.getMainboard();
        if (mainboard != null && !mainboard.getSocket().equals(cpu.getSocket())) {
            return CompatibilityResult.incompatible("CPU socket does not match selected mainboard");
        }

        Memory memory = buildItem.getMemory();
        if (memory != null) {
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
