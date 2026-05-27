package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.Storage;
import org.springframework.stereotype.Component;

@Component
public class MainboardCompatibilityValidator {

    public CompatibilityResult validate(BuildItemDto buildItem, Mainboard mainboard) {
        CPU cpu = buildItem.getCpu();
        if (cpu != null && !mainboard.getSocket().equals(cpu.getSocket())) {
            return CompatibilityResult.incompatible("Mainboard socket does not match selected CPU");
        }

        GPU gpu = buildItem.getGpu();
        if (gpu != null && CompatibilityRules.parsePcieVersion(mainboard.getPcieVersion())
                < CompatibilityRules.parsePcieVersion(gpu.getPcieVersion())) {
            return CompatibilityResult.incompatible("Mainboard PCIe version is lower than selected GPU requirement");
        }

        Memory memory = buildItem.getMemory();
        if (memory != null) {
            if (!mainboard.getMemoryType().equals(memory.getType())) {
                return CompatibilityResult.incompatible("Mainboard memory type does not match selected memory");
            }
            if (memory.getModules() > mainboard.getMemorySlots()) {
                return CompatibilityResult.incompatible("Selected memory modules exceed mainboard memory slots");
            }
            if (memory.getSpeed() > mainboard.getMaxMemorySpeed()) {
                return CompatibilityResult.incompatible("Selected memory speed exceeds mainboard maximum memory speed");
            }
        }

        Storage storage = buildItem.getStorage();
        if (storage != null) {
            if ("NVMe".equalsIgnoreCase(storage.getInterfaceType()) && mainboard.getM2Slots() <= 0) {
                return CompatibilityResult.incompatible("Mainboard does not have an M.2 slot for selected NVMe storage");
            } else if ("SATA".equalsIgnoreCase(storage.getInterfaceType()) && mainboard.getSataPorts() <= 0) {
                return CompatibilityResult.incompatible("Mainboard does not have a SATA port for selected storage");
            }
        }

        return CompatibilityResult.success();
    }
}
