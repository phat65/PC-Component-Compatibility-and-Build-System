package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.build.Storage;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.repository.product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComponentSpecCategoryService {
    private static final int MAINBOARD_CATEGORY_ID = 1;
    private static final int CPU_CATEGORY_ID = 2;
    private static final int GPU_CATEGORY_ID = 3;
    private static final int MEMORY_CATEGORY_ID = 4;
    private static final int STORAGE_CATEGORY_ID = 5;
    private static final int CASE_CATEGORY_ID = 6;
    private static final int POWER_SUPPLY_CATEGORY_ID = 7;
    private static final int COOLING_CATEGORY_ID = 8;

    private final CategoryRepository categoryRepository;

    private List<Category> mapMainboard(Mainboard mainboard) {
        LinkedHashSet<Category> categories = baseCategories(MAINBOARD_CATEGORY_ID);
        if (mainboard != null) {
            addCategory(categories, "Socket " + mainboard.getSocket(), mainboard.getSocket());
            addCategory(categories, mainboard.getFormFactor(), mainboard.getFormFactor());
            addCategory(categories, mainboard.getMemoryType(), mainboard.getMemoryType());
            addCategory(categories, "PCIe " + mainboard.getPcieVersion(), mainboard.getPcieVersion());
        }
        return toList(categories);
    }

    private List<Category> mapCpu(CPU cpu) {
        LinkedHashSet<Category> categories = baseCategories(CPU_CATEGORY_ID);
        if (cpu != null) {
            addCategory(categories, "Socket " + cpu.getSocket(), cpu.getSocket());
            addCategory(categories, "PCIe " + cpu.getPcieVersion(), cpu.getPcieVersion());
        }
        return toList(categories);
    }

    private List<Category> mapGpu(GPU gpu) {
        LinkedHashSet<Category> categories = baseCategories(GPU_CATEGORY_ID);
        if (gpu != null) {
            addCategory(categories, gpu.getMemoryType(), gpu.getMemoryType());
            addCategory(categories, "PCIe " + gpu.getPcieVersion(), gpu.getPcieVersion());
        }
        return toList(categories);
    }

    private List<Category> mapMemory(Memory memory) {
        LinkedHashSet<Category> categories = baseCategories(MEMORY_CATEGORY_ID);
        if (memory != null) {
            addCategory(categories, memory.getType(), memory.getType());
        }
        return toList(categories);
    }

    private List<Category> mapStorage(Storage storage) {
        LinkedHashSet<Category> categories = baseCategories(STORAGE_CATEGORY_ID);
        if (storage != null && hasText(storage.getInterfaceType())) {
            if (storage.getInterfaceType().equalsIgnoreCase("NVMe")) {
                addCategory(categories, "M.2 NVMe", storage.getInterfaceType());
            } else if (storage.getInterfaceType().equalsIgnoreCase("SATA")) {
                addCategory(categories, "SATA", storage.getInterfaceType());
            } else if (storage.getInterfaceType().equalsIgnoreCase("M.2")) {
                if (hasText(storage.getType()) && storage.getType().equalsIgnoreCase("NVMe")) {
                    addCategory(categories, "M.2 NVMe", storage.getType());
                } else {
                    addCategory(categories, "M.2 SATA", storage.getInterfaceType());
                }
            }
        }
        return toList(categories);
    }

    private List<Category> mapCase(Case pcCase) {
        LinkedHashSet<Category> categories = baseCategories(CASE_CATEGORY_ID);
        if (pcCase != null) {
            addCategory(categories, pcCase.getFormFactor(), pcCase.getFormFactor());
            addCategory(categories, pcCase.getPsuFormFactor() + " PSU", pcCase.getPsuFormFactor());
        }
        return toList(categories);
    }

    private List<Category> mapPowerSupply(PowerSupply powerSupply) {
        LinkedHashSet<Category> categories = baseCategories(POWER_SUPPLY_CATEGORY_ID);
        if (powerSupply != null) {
            addCategory(categories, powerSupply.getFormFactor() + " PSU", powerSupply.getFormFactor());
        }
        return toList(categories);
    }

    private List<Category> mapCooling(Cooling cooling) {
        LinkedHashSet<Category> categories = baseCategories(COOLING_CATEGORY_ID);
        if (cooling != null && hasText(cooling.getType())) {
            if (cooling.getType().equalsIgnoreCase("Air")) {
                addCategory(categories, "Air Cooling", cooling.getType());
            } else if (cooling.getType().equalsIgnoreCase("AIO")) {
                addCategory(categories, "AIO Cooling", cooling.getType());
            }
        }
        return toList(categories);
    }

    public List<Category> resolveCategoriesForSpec(int primaryCategoryId, Object component) {
        return switch (primaryCategoryId) {
            case MAINBOARD_CATEGORY_ID -> requireType(component, Mainboard.class).map(this::mapMainboard).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case CPU_CATEGORY_ID -> requireType(component, CPU.class).map(this::mapCpu).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case GPU_CATEGORY_ID -> requireType(component, GPU.class).map(this::mapGpu).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case MEMORY_CATEGORY_ID -> requireType(component, Memory.class).map(this::mapMemory).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case STORAGE_CATEGORY_ID -> requireType(component, Storage.class).map(this::mapStorage).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case CASE_CATEGORY_ID -> requireType(component, Case.class).map(this::mapCase).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case POWER_SUPPLY_CATEGORY_ID -> requireType(component, PowerSupply.class).map(this::mapPowerSupply).orElseGet(() -> baseCategoryList(primaryCategoryId));
            case COOLING_CATEGORY_ID -> requireType(component, Cooling.class).map(this::mapCooling).orElseGet(() -> baseCategoryList(primaryCategoryId));
            default -> baseCategoryList(primaryCategoryId);
        };
    }

    private LinkedHashSet<Category> baseCategories(int primaryCategoryId) {
        LinkedHashSet<Category> categories = new LinkedHashSet<>();
        categoryRepository.findById(primaryCategoryId).ifPresent(categories::add);
        return categories;
    }

    private List<Category> baseCategoryList(int primaryCategoryId) {
        return toList(baseCategories(primaryCategoryId));
    }

    private void addCategory(Set<Category> categories, String categoryName, String sourceValue) {
        if (!hasText(sourceValue) || !hasText(categoryName)) {
            return;
        }
        findCategoryByName(categoryName).ifPresent(categories::add);
    }

    private Optional<Category> findCategoryByName(String name) {
        if (!hasText(name)) {
            return Optional.empty();
        }
        return categoryRepository.findByCategoryNameIgnoreCase(name.trim());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private List<Category> toList(LinkedHashSet<Category> categories) {
        return new ArrayList<>(categories);
    }

    private <T> Optional<T> requireType(Object component, Class<T> type) {
        if (type.isInstance(component)) {
            return Optional.of(type.cast(component));
        }
        return Optional.empty();
    }
}
