package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.PowerSupply;
import org.springframework.stereotype.Component;

@Component
public class BuildPowerCalculator {

    private static final int STORAGE_DEFAULT_WATTAGE = 10;
    private static final double POWER_BUFFER_RATIO = 1.2;

    public int calculateTotalTdp(BuildItemDto buildItem) {
        if (buildItem == null) {
            return 0;
        }

        int totalTdp = 0;

        if (buildItem.getCpu() != null && buildItem.getCpu().getTdp() != null) {
            totalTdp += buildItem.getCpu().getTdp();
        }

        if (buildItem.getGpu() != null) {
            totalTdp += buildItem.getGpu().getTdp();
        }

        if (buildItem.getMemory() != null) {
            totalTdp += buildItem.getMemory().getTdp();
        }

        if (buildItem.getStorage() != null) {
            totalTdp += STORAGE_DEFAULT_WATTAGE;
        }

        if (buildItem.getCooling() != null) {
            totalTdp += buildItem.getCooling().getTdp();
        }

        return (int) (totalTdp * POWER_BUFFER_RATIO);
    }

    public boolean hasEnoughWattage(BuildItemDto buildItem, PowerSupply powerSupply) {
        return powerSupply != null && powerSupply.getWattage() >= calculateTotalTdp(buildItem);
    }
}
