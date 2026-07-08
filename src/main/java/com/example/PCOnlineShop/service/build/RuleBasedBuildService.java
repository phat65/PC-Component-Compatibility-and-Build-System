package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.dto.build.BuildPlanDto;
import com.example.PCOnlineShop.dto.build.ComponentDto;
import com.example.PCOnlineShop.model.build.*;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.build.*;
import com.example.PCOnlineShop.service.build.compatibility.CompatibilityResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class RuleBasedBuildService {
    private static final double BASIC_BUILD_BUDGET_VND = 10_000_000;
    private static final double MID_RANGE_BUDGET_VND = 25_000_000;
    private static final double HIGH_RANGE_BUDGET_VND = 37_500_000;

    private final CpuRepository cpuRepository;
    private final GpuRepository gpuRepository;
    private final MainboardRepository mainboardRepository;
    private final MemoryRepository memoryRepository;
    private final StorageRepository storageRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final CaseRepository caseRepository;
    private final CoolingRepository coolingRepository;
    private final CompatibilityService compatibilityService;

    public BuildPlanDto suggestBuild(String presetName, double totalBudget) {
        log.info("Suggesting build for preset: {}, budget: {} VND", presetName, totalBudget);
        // 1. Get preset
        BuildPreset preset = BuildPreset.valueOf(presetName.toUpperCase().replace(" ", "_").replace("-", "_"));
        // 2. Validate budget
        if (totalBudget < preset.getSuggestedMinBudget()) {
            log.warn("Budget {} VND is below recommended {} VND", totalBudget, preset.getSuggestedMinBudget());
        }
        // 3. Create temporary BuildItemDto for compatibility checking
        BuildItemDto tempBuild = new BuildItemDto();
        // Step 1: Select Mainboard first (base for all compatibility)
        Mainboard mainboard = selectMainboardEntity(preset, totalBudget, tempBuild);
        if (mainboard == null) {
            log.error("Cannot build - no mainboard found");
            return createEmptyPlan(preset, totalBudget);
        }
        tempBuild.setMainboard(mainboard);
        // Step 2: Select CPU (filtered by budget, checked for socket compatibility)
        CPU cpu = selectCpuEntity(preset, totalBudget, tempBuild);
        if (cpu == null) {
            log.error("Cannot build - no compatible CPU found for socket: {}", mainboard.getSocket());
            return createEmptyPlan(preset, totalBudget);
        }
        tempBuild.setCpu(cpu);
        // Step 3: Select Memory (filtered by budget, checked for DDR type and slots)
        Memory memory = selectMemoryEntity(preset, totalBudget, tempBuild);
        if (memory == null) {
            log.error("Cannot build - no compatible Memory found for type: {}", mainboard.getMemoryType());
        }
        tempBuild.setMemory(memory);
        // Step 4: Select GPU (filtered by budget, checked for PCIe compatibility)
        GPU gpu = selectGpuEntity(preset, totalBudget, tempBuild);
        if (gpu == null) {
            log.warn("No compatible GPU found - continuing without GPU");
        }
        tempBuild.setGpu(gpu);
        // Step 5: Select Storage (filtered by budget, checked for interface support)
        Storage storage = selectStorageEntity(preset, totalBudget, tempBuild);
        if (storage == null) {
            log.warn("No compatible Storage found");
        }
        tempBuild.setStorage(storage);
        // Step 6: Select Cooling before PSU so PSU sizing includes cooler TDP
        Cooling cooling = selectCoolingEntity(preset, totalBudget, tempBuild);
        tempBuild.setCooling(cooling);
        // Step 7: Select PSU (compatibility-based: sufficient wattage + form factor match)
        PowerSupply psu = selectPsuEntity(preset, totalBudget, tempBuild);
        tempBuild.setPowerSupply(psu);
        // Step 8: Select Case (compatibility-based: form factor + GPU length + PSU + cooling)
        Case pcCase = selectCaseEntity(preset, totalBudget, tempBuild);
        tempBuild.setPcCase(pcCase);
        // 5. Convert to BuildPlanDto for frontend
        BuildPlanDto plan = convertToPlanDto(tempBuild, preset, totalBudget);
        log.info("Build suggestion completed with compatibility checks");
        return plan;
    }
    private BuildPlanDto createEmptyPlan(BuildPreset preset, double totalBudget) {
        return BuildPlanDto.builder()
            .totalBudget(totalBudget)
            .purpose(preset.getName())
            .build();
    }
    private BuildPlanDto convertToPlanDto(BuildItemDto items, BuildPreset preset, double totalBudget) {
        BuildPlanDto plan = BuildPlanDto.builder()
            .totalBudget(totalBudget)
            .purpose(preset.getName())
            .build();
        if (items.getMainboard() != null) {
            plan.setMainboard(entityToDto(items.getMainboard().getProduct(), "Mainboard"));
        }
        if (items.getCpu() != null) {
            plan.setCpu(entityToDto(items.getCpu().getProduct(), "CPU"));
        }
        if (items.getGpu() != null) {
            plan.setGpu(entityToDto(items.getGpu().getProduct(), "GPU"));
        }
        if (items.getMemory() != null) {
            plan.setMemory(entityToDto(items.getMemory().getProduct(), "Memory"));
        }
        if (items.getStorage() != null) {
            plan.setStorage(entityToDto(items.getStorage().getProduct(), "Storage"));
        }
        if (items.getPowerSupply() != null) {
            plan.setPowerSupply(entityToDto(items.getPowerSupply().getProduct(), "PowerSupply"));
        }
        if (items.getCooling() != null) {
            plan.setCooling(entityToDto(items.getCooling().getProduct(), "Cooling"));
        }
        if (items.getPcCase() != null) {
            plan.setPcCase(entityToDto(items.getPcCase().getProduct(), "Case"));
        }
        return plan;
    }
    private ComponentDto entityToDto(com.example.PCOnlineShop.model.product.Product product, String category) {
        return ComponentDto.builder()
            .productId((long) product.getProductId())
            .productName(product.getProductName())
            .price(product.getPrice())
            .score(product.getPerformanceScore())
            .category(category)
            .build();
    }
    private record BudgetRange(double target, double min, double max) {
        static BudgetRange around(double target) {
            return new BudgetRange(target, target * 0.8, target * 1.2);
        }
        boolean contains(double price) {
            return price >= min && price <= max;
        }
    }
    private <T> T selectCompatibleComponent(String componentName,
                                           List<T> candidates,
                                           BudgetRange budgetRange,
                                           Function<T, CompatibilityResult> validator) {
        T inRange = findCompatible(candidates, budgetRange, validator, componentName);
        if (inRange != null) {
            logSelected(componentName, "80-120% range", inRange, budgetRange.target());
            return inRange;
        }
        log.warn("No {} in 80-120% range, selecting any compatible candidate", componentName);
        T fallback = findCompatible(candidates, null, validator, componentName);
        if (fallback != null) {
            logSelected(componentName, "fallback", fallback, budgetRange.target());
        }
        return fallback;
    }
    private <T> T findCompatible(String componentName,
                                 List<T> candidates,
                                 Function<T, CompatibilityResult> validator) {
        return findCompatible(candidates, null, validator, componentName);
    }
    private <T> T findCompatible(List<T> candidates,
                                 BudgetRange budgetRange,
                                 Function<T, CompatibilityResult> validator,
                                 String componentName) {
        for (T candidate : candidates) {
            CompatibilityResult result = validator.apply(candidate);
            if (!result.compatible()) {
                log.debug("{} {} incompatible: {}", componentName, getProductName(candidate), result.reasons());
                continue;
            }
            if (budgetRange == null || budgetRange.contains(getPrice(candidate))) {
                return candidate;
            }
        }
        return null;
    }
    private <T> void logSelected(String componentName, String selectionMode, T component, double budget) {
        log.info("Selected {} ({}): {} - {} VND (Budget: {} VND)",
                componentName,
                selectionMode,
                getProductName(component),
                getPrice(component),
                budget);
    }
    private String getProductName(Object component) {
        Product product = getProduct(component);
        return product != null ? product.getProductName() : "Unknown";
    }
    private double getPrice(Object component) {
        Product product = getProduct(component);
        return product != null ? product.getPrice() : 0;
    }
    private Product getProduct(Object component) {
        if (component instanceof Mainboard mainboard) {
            return mainboard.getProduct();
        } else if (component instanceof CPU cpu) {
            return cpu.getProduct();
        } else if (component instanceof Memory memory) {
            return memory.getProduct();
        } else if (component instanceof GPU gpu) {
            return gpu.getProduct();
        } else if (component instanceof Storage storage) {
            return storage.getProduct();
        } else if (component instanceof PowerSupply psu) {
            return psu.getProduct();
        } else if (component instanceof Cooling cooling) {
            return cooling.getProduct();
        } else if (component instanceof Case pcCase) {
            return pcCase.getProduct();
        }
        return null;
    }
    // ============================================
    // COMPONENT SELECTION: Budget-filtered + Compatibility-checked
    // Order: Mainboard -> CPU -> Memory -> GPU -> Storage -> PSU -> Cooling -> Case
    // ============================================
    private Mainboard selectMainboardEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("mainboard", totalBudget);
        int minScore = totalBudget >= HIGH_RANGE_BUDGET_VND ? 60 : (totalBudget >= MID_RANGE_BUDGET_VND ? 50 : 40);
        log.debug("Selecting Mainboard: budget={} VND (target range: 80%-120%)", budget);
        List<Mainboard> mainboards = mainboardRepository.findBestMainboardsByBudgetAndScore(budget * 1.2, minScore);
        if (mainboards.isEmpty()) {
            log.warn("No mainboard with score >= {}, trying with lower score", minScore);
            mainboards = mainboardRepository.findBestMainboardsByBudgetAndScore(budget * 1.3, minScore - 20);
        }
        if (mainboards.isEmpty()) {
            mainboards = mainboardRepository.findBestMainboardsByBudgetAndScore(budget * 1.5, 0);
        }
        BudgetRange budgetRange = BudgetRange.around(budget);
        for (Mainboard mainboard : mainboards) {
            if (budgetRange.contains(getPrice(mainboard))) {
                logSelected("Mainboard", "80-120% range", mainboard, budget);
                return mainboard;
            }
        }
        if (!mainboards.isEmpty()) {
            Mainboard selected = mainboards.get(0);
            log.warn("No mainboard in 80-120% range, selected: {} - {} VND",
                    selected.getProduct().getProductName(),
                    selected.getProduct().getPrice());
            return selected;
        }
        log.error("No Mainboard found");
        return null;
    }
    private CPU selectCpuEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("cpu", totalBudget);
        int minScore = Math.max(preset.getRequirement("cpu_score_min") - 20, 40);
        log.debug("Selecting CPU: budget={} VND (target range: 80%-120%)", budget);
        List<CPU> cpus = cpuRepository.findBestCpusByBudgetAndScore(budget * 1.2, minScore);
        if (cpus.isEmpty()) {
            cpus = cpuRepository.findBestCpusByBudgetAndScore(budget * 1.3, 30);
        }
        if (cpus.isEmpty()) {
            cpus = cpuRepository.findBestCpusByBudgetAndScore(budget * 1.5, 0);
        }

        List<CPU> preferredCpus = cpus;
        if (prefersIntegratedGraphics(preset, totalBudget)) {
            List<CPU> integratedCpus = cpus.stream()
                    .filter(cpu -> Boolean.TRUE.equals(cpu.getHasIGPU()))
                    .toList();
            if (!integratedCpus.isEmpty()) {
                preferredCpus = integratedCpus;
                log.debug("Office/basic build: prioritizing {} CPU candidates with integrated graphics", integratedCpus.size());
            }
        }

        CPU selected = selectCompatibleComponent("CPU", preferredCpus, BudgetRange.around(budget),
                cpu -> compatibilityService.validateCpuCompatibility(tempBuild, cpu));
        if (selected == null && preferredCpus != cpus) {
            log.warn("No compatible iGPU CPU found, falling back to all compatible CPU candidates");
            selected = selectCompatibleComponent("CPU", cpus, BudgetRange.around(budget),
                    cpu -> compatibilityService.validateCpuCompatibility(tempBuild, cpu));
        }
        if (selected == null) {
            log.error("No compatible CPU found");
        }
        return selected;
    }
    private Memory selectMemoryEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("memory", totalBudget);
        int minScore = 30;
        log.debug("Selecting Memory: budget={} VND (target range: 80%-120%)", budget);
        List<Memory> memories = memoryRepository.findBestMemoryByBudgetAndScore(budget * 1.2, minScore);
        if (memories.isEmpty()) {
            memories = memoryRepository.findBestMemoryByBudgetAndScore(budget * 1.5, 0);
        }
        Memory selected = selectCompatibleComponent("Memory", memories, BudgetRange.around(budget),
                memory -> compatibilityService.validateMemoryCompatibility(tempBuild, memory));
        if (selected == null) {
            log.error("No compatible Memory found");
        }
        return selected;
    }
    private GPU selectGpuEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("gpu", totalBudget);
        if (!requiresDedicatedGpu(preset, totalBudget)) {
            log.info("Skipping dedicated GPU for {} preset at {} VND budget", preset.name(), totalBudget);
            return null;
        }
        int minScore = Math.max(preset.getRequirement("gpu_score_min") - 20, 40);
        log.debug("Selecting GPU: budget={} VND (target range: 80%-120%)", budget);
        List<GPU> gpus = gpuRepository.findBestGpusByBudgetAndScore(budget * 1.2, minScore);
        if (gpus.isEmpty()) {
            gpus = gpuRepository.findBestGpusByBudgetAndScore(budget * 1.3, 30);
        }
        if (gpus.isEmpty()) {
            gpus = gpuRepository.findBestGpusByBudgetAndScore(budget * 1.5, 0);
        }
        GPU selected = selectCompatibleComponent("GPU", gpus, BudgetRange.around(budget),
                gpu -> compatibilityService.validateGpuCompatibility(tempBuild, gpu));
        if (selected == null) {
            log.error("No compatible GPU found");
        }
        return selected;
    }
    private Storage selectStorageEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("storage", totalBudget);
        int minScore = 30;
        log.debug("Selecting Storage: budget={} VND (target range: 80%-120%)", budget);
        List<Storage> storages = storageRepository.findBestStorageByBudgetAndScore(budget * 1.2, minScore);
        if (storages.isEmpty()) {
            storages = storageRepository.findBestStorageByBudgetAndScore(budget * 1.5, 0);
        }
        Storage selected = selectCompatibleComponent("Storage", storages, BudgetRange.around(budget),
                storage -> compatibilityService.validateStorageCompatibility(tempBuild, storage));
        if (selected == null) {
            log.error("No compatible Storage found");
        }
        return selected;
    }
    private PowerSupply selectPsuEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("psu", totalBudget);
        log.debug("Selecting PSU: budget={} VND (target range: 80%-120%)", budget);
        List<PowerSupply> psus = powerSupplyRepository.findBestPsuByBudgetAndScore(budget * 1.2, 0);
        if (psus.isEmpty()) {
            log.warn("No PSU found in budget {} VND, relaxing to {} VND", budget, budget * 1.5);
            psus = powerSupplyRepository.findBestPsuByBudgetAndScore(budget * 1.5, 0);
        }
        PowerSupply selected = selectCompatibleComponent("PSU", psus, BudgetRange.around(budget),
                psu -> compatibilityService.validatePowerSupplyCompatibility(tempBuild, psu));
        if (selected != null) {
            return selected;
        }
        log.warn("No PSU in budget is compatible, searching all PSUs");
        psus = powerSupplyRepository.findAllWithImages();
        selected = findCompatible("PSU", psus,
                psu -> compatibilityService.validatePowerSupplyCompatibility(tempBuild, psu));
        if (selected != null) {
            logSelected("PSU", "over budget", selected, budget);
            return selected;
        }
        log.error("No compatible PSU found");
        return null;
    }
    private Cooling selectCoolingEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("cooling", totalBudget);
        if (usesStockCooling(preset, totalBudget)) {
            log.info("Skipping aftermarket cooling for {} preset at {} VND budget", preset.name(), totalBudget);
            return null;
        }
        log.debug("Selecting Cooling: budget={} VND (target range: 80%-120%)", budget);
        List<Cooling> coolings = coolingRepository.findBestCoolingByBudgetAndScore(budget * 1.2, 0);
        if (coolings.isEmpty()) {
            coolings = coolingRepository.findBestCoolingByBudgetAndScore(budget * 1.5, 0);
        }
        Cooling selected = selectCompatibleComponent("Cooling", coolings, BudgetRange.around(budget),
                cooling -> compatibilityService.validateCoolingCompatibility(tempBuild, cooling));
        if (selected == null) {
            log.error("No compatible Cooling found");
        }
        return selected;
    }
    private Case selectCaseEntity(BuildPreset preset, double totalBudget, BuildItemDto tempBuild) {
        double budget = preset.calculateComponentBudget("case", totalBudget);
        log.debug("Selecting Case: budget={} VND (target range: 80%-120%)", budget);
        List<Case> cases = caseRepository.findBestCasesByBudgetAndScore(budget * 1.2, 0);
        log.info("Found {} cases within budget {} VND", cases.size(), budget);
        logBuildStateForCaseSelection(tempBuild);
        Case selected = selectCompatibleComponent("Case", cases, BudgetRange.around(budget),
                pcCase -> compatibilityService.validateCaseCompatibility(tempBuild, pcCase));
        if (selected != null) {
            return selected;
        }
        selected = selectCaseWithExpandedBudget(tempBuild, budget, 2.0);
        if (selected != null) {
            return selected;
        }
        selected = selectCaseWithExpandedBudget(tempBuild, budget, 4.0);
        if (selected != null) {
            return selected;
        }
        log.warn("No Case found even in 4x budget, searching all cases");
        cases = caseRepository.findAllWithImages();
        log.info("Found {} total cases in database", cases.size());
        selected = findCompatible("Case", cases,
                pcCase -> compatibilityService.validateCaseCompatibility(tempBuild, pcCase));
        if (selected != null) {
            logSelected("Case", "any price", selected, budget);
            return selected;
        }
        log.error("No compatible Case found in entire database");
        return null;
    }
    private Case selectCaseWithExpandedBudget(BuildItemDto tempBuild, double budget, double multiplier) {
        double expandedBudget = budget * multiplier;
        log.warn("No Case found in previous budget range, trying with {}x budget {} VND", multiplier, expandedBudget);
        List<Case> cases = caseRepository.findBestCasesByBudgetAndScore(expandedBudget, 0);
        log.info("Found {} cases within {}x budget {} VND", cases.size(), multiplier, expandedBudget);
        Case selected = findCompatible("Case", cases,
                pcCase -> compatibilityService.validateCaseCompatibility(tempBuild, pcCase));
        if (selected != null) {
            logSelected("Case", multiplier + "x budget", selected, budget);
        }
        return selected;
    }
    private boolean prefersIntegratedGraphics(BuildPreset preset, double totalBudget) {
        return preset == BuildPreset.OFFICE || (totalBudget <= BASIC_BUILD_BUDGET_VND && !requiresDedicatedGpu(preset, totalBudget));
    }
    private boolean requiresDedicatedGpu(BuildPreset preset, double totalBudget) {
        return preset.calculateComponentBudget("gpu", totalBudget) > 0
                && preset.getRequirement("gpu_score_min") > 0;
    }
    private boolean usesStockCooling(BuildPreset preset, double totalBudget) {
        return preset == BuildPreset.OFFICE && totalBudget <= BASIC_BUILD_BUDGET_VND;
    }
    private void logBuildStateForCaseSelection(BuildItemDto tempBuild) {
        log.info("Current build state for case compatibility:");
        if (tempBuild.getMainboard() != null) {
            log.info("   Mainboard: {} (Form: {})",
                     tempBuild.getMainboard().getProduct().getProductName(),
                     tempBuild.getMainboard().getFormFactor());
        }
        if (tempBuild.getGpu() != null) {
            log.info("   GPU: {} (Length: {}mm)",
                     tempBuild.getGpu().getProduct().getProductName(),
                     tempBuild.getGpu().getLength());
        }
        if (tempBuild.getCooling() != null) {
            log.info("   Cooling: {} (Type: {}, FanSize: {}, Radiator: {})",
                     tempBuild.getCooling().getProduct().getProductName(),
                     tempBuild.getCooling().getType(),
                     tempBuild.getCooling().getFanSize(),
                     tempBuild.getCooling().getRadiatorSize());
        }
        if (tempBuild.getPowerSupply() != null) {
            log.info("   PSU: {} (Form Factor: {})",
                     tempBuild.getPowerSupply().getProduct().getProductName(),
                     tempBuild.getPowerSupply().getFormFactor());
        }
    }
    /**
     * Convert BuildPlanDto (with ComponentDto) to BuildItemDto (with actual entities)
     * for storing in session
     */
    public BuildItemDto convertPlanToItems(BuildPlanDto plan) {
        log.info("Converting BuildPlanDto to BuildItemDto for session storage");
        if (plan == null) {
            throw new IllegalArgumentException("Build plan is required");
        }

        BuildItemDto items = new BuildItemDto();
        if (plan.getCpu() != null) {
            items.setCpu(resolveRequiredComponent(plan.getCpu(), "CPU", cpuRepository::findByIdWithImages));
        }
        if (plan.getGpu() != null) {
            items.setGpu(resolveRequiredComponent(plan.getGpu(), "GPU", gpuRepository::findByIdWithImages));
        }
        if (plan.getMainboard() != null) {
            items.setMainboard(resolveRequiredComponent(plan.getMainboard(), "Mainboard", mainboardRepository::findByIdWithImages));
        }
        if (plan.getMemory() != null) {
            items.setMemory(resolveRequiredComponent(plan.getMemory(), "Memory", memoryRepository::findByIdWithImages));
        }
        if (plan.getStorage() != null) {
            items.setStorage(resolveRequiredComponent(plan.getStorage(), "Storage", storageRepository::findByIdWithImages));
        }
        if (plan.getPowerSupply() != null) {
            items.setPowerSupply(resolveRequiredComponent(plan.getPowerSupply(), "PowerSupply", powerSupplyRepository::findByIdWithImages));
        }
        if (plan.getPcCase() != null) {
            items.setPcCase(resolveRequiredComponent(plan.getPcCase(), "Case", caseRepository::findByIdWithImages));
        }
        if (plan.getCooling() != null) {
            items.setCooling(resolveRequiredComponent(plan.getCooling(), "Cooling", coolingRepository::findByIdWithImages));
        }
        List<String> compatibilityErrors = compatibilityService.validateFullBuild(items);
        if (!compatibilityErrors.isEmpty()) {
            throw new IllegalArgumentException("Suggested build is no longer compatible: "
                    + String.join("; ", compatibilityErrors));
        }

        log.info("BuildItemDto created successfully");
        return items;
    }

    private <T> T resolveRequiredComponent(ComponentDto component,
                                           String componentName,
                                           Function<Integer, Optional<T>> resolver) {
        if (component.getProductId() == null) {
            throw new IllegalArgumentException(componentName + " is missing product id");
        }

        int productId = component.getProductId().intValue();
        return resolver.apply(productId)
            .orElseThrow(() -> new IllegalArgumentException(
                componentName + " is unavailable or missing component data: productId=" + productId));
    }
}


