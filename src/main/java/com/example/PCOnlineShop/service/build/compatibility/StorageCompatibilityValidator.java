package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Storage;
import org.springframework.stereotype.Component;

@Component
public class StorageCompatibilityValidator {

    public CompatibilityResult validate(BuildItemDto buildItem, Storage storage) {
        Mainboard mainboard = buildItem.getMainboard();
        if (mainboard == null) {
            return CompatibilityResult.success();
        }

        if ("NVMe".equalsIgnoreCase(storage.getInterfaceType()) && mainboard.getM2Slots() <= 0) {
            return CompatibilityResult.incompatible("Selected mainboard does not have an M.2 slot for NVMe storage");
        } else if ("SATA".equalsIgnoreCase(storage.getInterfaceType()) && mainboard.getSataPorts() <= 0) {
            return CompatibilityResult.incompatible("Selected mainboard does not have a SATA port for selected storage");
        }

        return CompatibilityResult.success();
    }
}
