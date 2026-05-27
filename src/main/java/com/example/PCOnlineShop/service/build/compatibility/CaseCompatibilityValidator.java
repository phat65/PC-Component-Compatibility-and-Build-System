package com.example.PCOnlineShop.service.build.compatibility;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.PowerSupply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CaseCompatibilityValidator {

    private final CoolingCompatibilityValidator coolingCompatibilityValidator;

    public CompatibilityResult validate(BuildItemDto buildItem, Case pcCase) {
        Mainboard mainboard = buildItem.getMainboard();
        if (mainboard != null
                && !CompatibilityRules.isCaseFormFactorCompatible(pcCase.getFormFactor(), mainboard.getFormFactor())) {
            return CompatibilityResult.incompatible("Case form factor does not support selected mainboard");
        }

        GPU gpu = buildItem.getGpu();
        if (gpu != null && pcCase.getGpuMaxLength() > 0 && pcCase.getGpuMaxLength() < gpu.getLength()) {
            return CompatibilityResult.incompatible("Selected GPU is too long for selected case");
        }

        Cooling cooling = buildItem.getCooling();
        if (cooling != null) {
            CompatibilityResult coolingResult = coolingCompatibilityValidator.validateWithCase(cooling, pcCase);
            if (!coolingResult.compatible()) {
                return coolingResult;
            }
        }

        PowerSupply powerSupply = buildItem.getPowerSupply();
        if (powerSupply != null
                && pcCase.getPsuFormFactor() != null
                && powerSupply.getFormFactor() != null
                && !CompatibilityRules.isPsuFormFactorCompatible(pcCase.getPsuFormFactor(), powerSupply.getFormFactor())) {
            return CompatibilityResult.incompatible("Case PSU form factor does not support selected PSU");
        }

        return CompatibilityResult.success();
    }
}
