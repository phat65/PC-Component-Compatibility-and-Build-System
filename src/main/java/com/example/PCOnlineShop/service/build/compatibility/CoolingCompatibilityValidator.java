package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import org.springframework.stereotype.Component;

@Component
public class CoolingCompatibilityValidator {

    public CompatibilityResult validate(BuildItemDto buildItem, Cooling cooling) {
        Case pcCase = buildItem.getPcCase();
        if (pcCase == null) {
            return CompatibilityResult.success();
        }

        return validateWithCase(cooling, pcCase);
    }

    CompatibilityResult validateWithCase(Cooling cooling, Case pcCase) {
        if ("Air".equalsIgnoreCase(cooling.getType())
                && pcCase.getCpuMaxCoolerHeight() > 0
                && cooling.getFanSize() > 0) {
            int estimatedHeight = cooling.getFanSize() + 30;
            if (estimatedHeight > pcCase.getCpuMaxCoolerHeight()) {
                return CompatibilityResult.incompatible("Selected air cooler is too tall for selected case");
            }
        }

        if ("Liquid".equalsIgnoreCase(cooling.getType()) && cooling.getRadiatorSize() > 0) {
            int radiatorSize = cooling.getRadiatorSize();

            if (pcCase.getFormFactor().toUpperCase().contains("MINI") && radiatorSize > 240) {
                return CompatibilityResult.incompatible("Selected radiator is too large for selected mini case");
            } else if (pcCase.getFormFactor().toUpperCase().contains("MICRO") && radiatorSize > 280) {
                return CompatibilityResult.incompatible("Selected radiator is too large for selected micro case");
            }
        }

        return CompatibilityResult.success();
    }
}
