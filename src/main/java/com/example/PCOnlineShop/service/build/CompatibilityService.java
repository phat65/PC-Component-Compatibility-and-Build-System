package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.build.Storage;
import com.example.PCOnlineShop.service.build.compatibility.CaseCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.CompatibilityResult;
import com.example.PCOnlineShop.service.build.compatibility.CoolingCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.CpuCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.GpuCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.MainboardCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.MemoryCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.PowerSupplyCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.StorageCompatibilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompatibilityService {

    private final MainboardCompatibilityValidator mainboardValidator;
    private final CpuCompatibilityValidator cpuValidator;
    private final GpuCompatibilityValidator gpuValidator;
    private final CaseCompatibilityValidator caseValidator;
    private final PowerSupplyCompatibilityValidator powerSupplyValidator;
    private final MemoryCompatibilityValidator memoryValidator;
    private final StorageCompatibilityValidator storageValidator;
    private final CoolingCompatibilityValidator coolingValidator;

    public boolean checkMainboardCompatibility(BuildItemDto buildItem, Mainboard mainboard) {
        return validateMainboardCompatibility(buildItem, mainboard).compatible();
    }

    public CompatibilityResult validateMainboardCompatibility(BuildItemDto buildItem, Mainboard mainboard) {
        return mainboardValidator.validate(buildItem, mainboard);
    }

    public boolean checkCpuCompatibility(BuildItemDto buildItem, CPU cpu) {
        return validateCpuCompatibility(buildItem, cpu).compatible();
    }

    public CompatibilityResult validateCpuCompatibility(BuildItemDto buildItem, CPU cpu) {
        return cpuValidator.validate(buildItem, cpu);
    }

    public boolean checkGpuCompatibility(BuildItemDto buildItem, GPU gpu) {
        return validateGpuCompatibility(buildItem, gpu).compatible();
    }

    public CompatibilityResult validateGpuCompatibility(BuildItemDto buildItem, GPU gpu) {
        return gpuValidator.validate(buildItem, gpu);
    }

    public boolean checkCaseCompatibility(BuildItemDto buildItem, Case pcCase) {
        return validateCaseCompatibility(buildItem, pcCase).compatible();
    }

    public CompatibilityResult validateCaseCompatibility(BuildItemDto buildItem, Case pcCase) {
        return caseValidator.validate(buildItem, pcCase);
    }

    public boolean checkPowerSupplyCompatibility(BuildItemDto buildItem, PowerSupply psu) {
        return validatePowerSupplyCompatibility(buildItem, psu).compatible();
    }

    public CompatibilityResult validatePowerSupplyCompatibility(BuildItemDto buildItem, PowerSupply psu) {
        return powerSupplyValidator.validate(buildItem, psu);
    }

    public boolean checkMemoryCompatibility(BuildItemDto buildItem, Memory memory) {
        return validateMemoryCompatibility(buildItem, memory).compatible();
    }

    public CompatibilityResult validateMemoryCompatibility(BuildItemDto buildItem, Memory memory) {
        return memoryValidator.validate(buildItem, memory);
    }

    public boolean checkStorageCompatibility(BuildItemDto buildItem, Storage storage) {
        return validateStorageCompatibility(buildItem, storage).compatible();
    }

    public CompatibilityResult validateStorageCompatibility(BuildItemDto buildItem, Storage storage) {
        return storageValidator.validate(buildItem, storage);
    }

    public boolean checkCoolingCompatibility(BuildItemDto buildItem, Cooling cooling) {
        return validateCoolingCompatibility(buildItem, cooling).compatible();
    }

    public CompatibilityResult validateCoolingCompatibility(BuildItemDto buildItem, Cooling cooling) {
        return coolingValidator.validate(buildItem, cooling);
    }

    public List<String> validateFullBuild(BuildItemDto buildItem) {
        List<String> errors = new ArrayList<>();

        if (buildItem.getMainboard() != null) {
            errors.addAll(validateMainboardCompatibility(buildItem, buildItem.getMainboard()).reasons());
        }

        if (buildItem.getCpu() != null) {
            errors.addAll(validateCpuCompatibility(buildItem, buildItem.getCpu()).reasons());
        }

        if (buildItem.getGpu() != null) {
            errors.addAll(validateGpuCompatibility(buildItem, buildItem.getGpu()).reasons());
        }

        if (buildItem.getMemory() != null) {
            errors.addAll(validateMemoryCompatibility(buildItem, buildItem.getMemory()).reasons());
        }

        if (buildItem.getStorage() != null) {
            errors.addAll(validateStorageCompatibility(buildItem, buildItem.getStorage()).reasons());
        }

        if (buildItem.getPcCase() != null) {
            errors.addAll(validateCaseCompatibility(buildItem, buildItem.getPcCase()).reasons());
        }

        if (buildItem.getPowerSupply() != null) {
            errors.addAll(validatePowerSupplyCompatibility(buildItem, buildItem.getPowerSupply()).reasons());
        }

        if (buildItem.getCooling() != null) {
            errors.addAll(validateCoolingCompatibility(buildItem, buildItem.getCooling()).reasons());
        }

        return errors;
    }

    public boolean isComponentCompatibleWithBuild(BuildItemDto buildItem, Object component) {
        return validateComponentCompatibilityWithBuild(buildItem, component).compatible();
    }

    public CompatibilityResult validateComponentCompatibilityWithBuild(BuildItemDto buildItem, Object component) {
        if (component instanceof CPU) {
            return validateCpuCompatibility(buildItem, (CPU) component);
        } else if (component instanceof GPU) {
            return validateGpuCompatibility(buildItem, (GPU) component);
        } else if (component instanceof Mainboard) {
            return validateMainboardCompatibility(buildItem, (Mainboard) component);
        } else if (component instanceof Memory) {
            return validateMemoryCompatibility(buildItem, (Memory) component);
        } else if (component instanceof Storage) {
            return validateStorageCompatibility(buildItem, (Storage) component);
        } else if (component instanceof Case) {
            return validateCaseCompatibility(buildItem, (Case) component);
        } else if (component instanceof PowerSupply) {
            return validatePowerSupplyCompatibility(buildItem, (PowerSupply) component);
        } else if (component instanceof Cooling) {
            return validateCoolingCompatibility(buildItem, (Cooling) component);
        }
        return CompatibilityResult.success();
    }
}
