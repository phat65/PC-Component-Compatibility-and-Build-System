package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.PowerSupply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PowerSupplyCompatibilityValidator {

    private final BuildPowerCalculator buildPowerCalculator;

    public CompatibilityResult validate(BuildItemDto buildItem, PowerSupply psu) {
        if (!buildPowerCalculator.hasEnoughWattage(buildItem, psu)) {
            return CompatibilityResult.incompatible("Selected PSU does not provide enough wattage for this build");
        }

        Case pcCase = buildItem.getPcCase();
        if (pcCase != null
                && pcCase.getPsuFormFactor() != null
                && psu.getFormFactor() != null
                && !CompatibilityRules.isPsuFormFactorCompatible(pcCase.getPsuFormFactor(), psu.getFormFactor())) {
            return CompatibilityResult.incompatible("Selected PSU form factor is not supported by selected case");
        }

        return CompatibilityResult.success();
    }
}
