package com.example.PCOnlineShop.service.validation;

import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.build.Storage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ComponentSpecValidationService {
    private static final Set<String> CPU_SOCKETS = Set.of("AM4", "AM5", "LGA1151", "LGA1200", "LGA1700", "TR4");
    private static final Set<String> FORM_FACTORS = Set.of("Mini-ITX", "Micro-ATX", "ATX", "E-ATX");
    private static final Set<String> MEMORY_TYPES = Set.of("DDR4", "DDR5");
    private static final Set<String> GPU_MEMORY_TYPES = Set.of("GDDR5", "GDDR6", "GDDR6X");
    private static final Set<String> PCIE_VERSIONS = Set.of("3.0", "4.0", "5.0");
    private static final Set<String> GPU_INTERFACES = Set.of("PCIe x16", "PCIe x8");
    private static final Set<String> STORAGE_TYPES = Set.of("SSD", "HDD", "NVMe");
    private static final Set<String> STORAGE_INTERFACES = Set.of("SATA", "NVMe", "M.2");
    private static final Set<String> PSU_EFFICIENCIES = Set.of(
            "80+",
            "80+ Bronze",
            "80+ Silver",
            "80+ Gold",
            "80+ Platinum",
            "80+ Titanium"
    );
    private static final Set<String> PSU_FORM_FACTORS = Set.of("ATX", "SFX", "SFX-L");
    private static final Set<String> COOLING_TYPES = Set.of("Air", "AIO");
    private static final Set<Integer> VALID_RADIATOR_SIZES = Set.of(0, 120, 240, 280, 360, 420, 480);
    private static final Set<Integer> VALID_MEMORY_MODULES = Set.of(1, 2, 4);

    public List<String> validateGpu(GPU gpu) {
        List<String> errors = new ArrayList<>();
        if (gpu == null) {
            errors.add("GPU specification is required");
            return errors;
        }

        requireRange(errors, "GPU VRAM", gpu.getVram(), 1, 48, "GB");
        requireOneOf(errors, "GPU memory type", gpu.getMemoryType(), GPU_MEMORY_TYPES);
        requireRange(errors, "GPU TDP", gpu.getTdp(), 10, 500, "W");
        optionalRange(errors, "GPU length", gpu.getLength(), 50, 450, "mm");
        optionalOneOf(errors, "GPU interface", gpu.getGpuInterface(), GPU_INTERFACES);
        requireOneOf(errors, "GPU PCIe version", gpu.getPcieVersion(), PCIE_VERSIONS);
        return errors;
    }

    public List<String> validateCpu(CPU cpu) {
        List<String> errors = new ArrayList<>();
        if (cpu == null) {
            errors.add("CPU specification is required");
            return errors;
        }

        requireOneOf(errors, "CPU socket", cpu.getSocket(), CPU_SOCKETS);
        requireRange(errors, "CPU TDP", cpu.getTdp(), 1, 500, "W");
        optionalRange(errors, "CPU max memory speed", cpu.getMaxMemorySpeed(), 800, 10000, "MHz");
        optionalRange(errors, "CPU memory channels", cpu.getMemoryChannels(), 1, 8, "");
        requireOneOf(errors, "CPU PCIe version", cpu.getPcieVersion(), PCIE_VERSIONS);
        return errors;
    }

    public List<String> validateMainboard(Mainboard mainboard) {
        List<String> errors = new ArrayList<>();
        if (mainboard == null) {
            errors.add("Mainboard specification is required");
            return errors;
        }

        requireOneOf(errors, "Mainboard socket", mainboard.getSocket(), CPU_SOCKETS);
        requireText(errors, "Mainboard chipset", mainboard.getChipset());
        requireOneOf(errors, "Mainboard form factor", mainboard.getFormFactor(), FORM_FACTORS);
        requireOneOf(errors, "Mainboard memory type", mainboard.getMemoryType(), MEMORY_TYPES);
        requireRange(errors, "Mainboard memory slots", mainboard.getMemorySlots(), 1, 8, "");
        requireRange(errors, "Mainboard max memory speed", mainboard.getMaxMemorySpeed(), 800, 10000, "MHz");
        requireOneOf(errors, "Mainboard PCIe version", mainboard.getPcieVersion(), PCIE_VERSIONS);
        optionalRange(errors, "Mainboard M.2 slots", mainboard.getM2Slots(), 0, 5, "");
        optionalRange(errors, "Mainboard SATA ports", mainboard.getSataPorts(), 0, 12, "");
        return errors;
    }

    public List<String> validateMemory(Memory memory) {
        List<String> errors = new ArrayList<>();
        if (memory == null) {
            errors.add("Memory specification is required");
            return errors;
        }

        requireRange(errors, "Memory capacity", memory.getCapacity(), 1, 256, "GB");
        requireOneOf(errors, "Memory type", memory.getType(), MEMORY_TYPES);
        requireRange(errors, "Memory speed", memory.getSpeed(), 800, 10000, "MHz");
        optionalRange(errors, "Memory TDP", memory.getTdp(), 0, 30, "W");
        if (memory.getModules() > 0 && !VALID_MEMORY_MODULES.contains(memory.getModules())) {
            errors.add("Memory modules must be 1, 2, or 4");
        }
        return errors;
    }

    public List<String> validateStorage(Storage storage) {
        List<String> errors = new ArrayList<>();
        if (storage == null) {
            errors.add("Storage specification is required");
            return errors;
        }

        requireRange(errors, "Storage capacity", storage.getCapacity(), 1, 8000, "GB");
        requireOneOf(errors, "Storage type", storage.getType(), STORAGE_TYPES);
        requireOneOf(errors, "Storage interface", storage.getInterfaceType(), STORAGE_INTERFACES);
        optionalRange(errors, "Storage read speed", storage.getReadSpeed(), 0, 14000, "MB/s");
        optionalRange(errors, "Storage write speed", storage.getWriteSpeed(), 0, 12000, "MB/s");
        return errors;
    }

    public List<String> validatePowerSupply(PowerSupply psu) {
        List<String> errors = new ArrayList<>();
        if (psu == null) {
            errors.add("Power supply specification is required");
            return errors;
        }

        requireRange(errors, "PSU wattage", psu.getWattage(), 100, 2000, "W");
        requireOneOf(errors, "PSU efficiency", psu.getEfficiency(), PSU_EFFICIENCIES);
        optionalOneOf(errors, "PSU form factor", psu.getFormFactor(), PSU_FORM_FACTORS);
        return errors;
    }

    public List<String> validateCase(Case pcCase) {
        List<String> errors = new ArrayList<>();
        if (pcCase == null) {
            errors.add("Case specification is required");
            return errors;
        }

        requireOneOf(errors, "Case form factor", pcCase.getFormFactor(), FORM_FACTORS);
        requireRange(errors, "Case GPU max length", pcCase.getGpuMaxLength(), 1, 500, "mm");
        optionalOneOf(errors, "Case PSU form factor", pcCase.getPsuFormFactor(), PSU_FORM_FACTORS);
        optionalRange(errors, "Case CPU max cooler height", pcCase.getCpuMaxCoolerHeight(), 0, 300, "mm");
        return errors;
    }

    public List<String> validateCooling(Cooling cooling) {
        List<String> errors = new ArrayList<>();
        if (cooling == null) {
            errors.add("Cooling specification is required");
            return errors;
        }

        requireOneOf(errors, "Cooling type", cooling.getType(), COOLING_TYPES);
        optionalRange(errors, "Cooling fan size", cooling.getFanSize(), 40, 200, "mm");
        if (!VALID_RADIATOR_SIZES.contains(cooling.getRadiatorSize())) {
            errors.add("Cooling radiator size must be 0, 120, 240, 280, 360, 420, or 480mm");
        }
        requireRange(errors, "Cooling TDP", cooling.getTdp(), 1, 500, "W");
        return errors;
    }

    public List<String> validateAllComponents(
            GPU gpu, CPU cpu, Mainboard mainboard, Memory memory,
            Storage storage, PowerSupply psu, Case pcCase, Cooling cooling) {

        List<String> allErrors = new ArrayList<>();
        if (gpu != null) allErrors.addAll(validateGpu(gpu));
        if (cpu != null) allErrors.addAll(validateCpu(cpu));
        if (mainboard != null) allErrors.addAll(validateMainboard(mainboard));
        if (memory != null) allErrors.addAll(validateMemory(memory));
        if (storage != null) allErrors.addAll(validateStorage(storage));
        if (psu != null) allErrors.addAll(validatePowerSupply(psu));
        if (pcCase != null) allErrors.addAll(validateCase(pcCase));
        if (cooling != null) allErrors.addAll(validateCooling(cooling));
        return allErrors;
    }

    private void requireText(List<String> errors, String fieldName, String value) {
        if (isBlank(value)) {
            errors.add(fieldName + " is required");
        }
    }

    private void requireOneOf(List<String> errors, String fieldName, String value, Set<String> allowedValues) {
        requireText(errors, fieldName, value);
        if (!isBlank(value) && !allowedValues.contains(value.trim())) {
            errors.add(fieldName + " must be one of: " + String.join(", ", allowedValues));
        }
    }

    private void optionalOneOf(List<String> errors, String fieldName, String value, Set<String> allowedValues) {
        if (!isBlank(value) && !allowedValues.contains(value.trim())) {
            errors.add(fieldName + " must be one of: " + String.join(", ", allowedValues));
        }
    }

    private void requireRange(List<String> errors, String fieldName, Integer value, int min, int max, String unit) {
        if (value == null || value <= 0) {
            errors.add(fieldName + " is required");
            return;
        }
        range(errors, fieldName, value, min, max, unit);
    }

    private void optionalRange(List<String> errors, String fieldName, Integer value, int min, int max, String unit) {
        if (value == null || value == 0) {
            return;
        }
        range(errors, fieldName, value, min, max, unit);
    }

    private void range(List<String> errors, String fieldName, int value, int min, int max, String unit) {
        if (value < min || value > max) {
            String suffix = isBlank(unit) ? "" : unit;
            errors.add(fieldName + " must be between " + min + suffix + " and " + max + suffix);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
