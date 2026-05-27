package com.example.PCOnlineShop.service.product;

import com.example.PCOnlineShop.dto.product.ProductComponentSpecFormDTO;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.build.Storage;
import com.example.PCOnlineShop.model.product.Category;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.build.CaseRepository;
import com.example.PCOnlineShop.repository.build.CoolingRepository;
import com.example.PCOnlineShop.repository.build.CpuRepository;
import com.example.PCOnlineShop.repository.build.GpuRepository;
import com.example.PCOnlineShop.repository.build.MainboardRepository;
import com.example.PCOnlineShop.repository.build.MemoryRepository;
import com.example.PCOnlineShop.repository.build.PowerSupplyRepository;
import com.example.PCOnlineShop.repository.build.StorageRepository;
import com.example.PCOnlineShop.service.validation.ComponentSpecValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComponentSpecService {
    private static final String DEFAULT_TEMPLATE = "product/specs/default-spec-form";

    private final CpuRepository cpuRepository;
    private final GpuRepository gpuRepository;
    private final MainboardRepository mainboardRepository;
    private final MemoryRepository memoryRepository;
    private final StorageRepository storageRepository;
    private final CaseRepository caseRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final CoolingRepository coolingRepository;
    private final ProductService productService;
    private final ComponentSpecCategoryService componentSpecCategoryService;
    private final ComponentSpecValidationService specValidationService;

    public ProductComponentSpecFormDTO buildCreateSpecForm(int categoryId) {
        return switch (categoryId) {
            case 1 -> new ProductComponentSpecFormDTO("mb", new Mainboard(), "product/specs/mainboard-spec-form");
            case 2 -> new ProductComponentSpecFormDTO("cpu", new CPU(), "product/specs/cpu-spec-form");
            case 3 -> new ProductComponentSpecFormDTO("gpu", new GPU(), "product/specs/gpu-spec-form");
            case 4 -> new ProductComponentSpecFormDTO("mem", new Memory(), "product/specs/memory-spec-form");
            case 5 -> new ProductComponentSpecFormDTO("storage", new Storage(), "product/specs/storage-spec-form");
            case 6 -> new ProductComponentSpecFormDTO("pcase", new Case(), "product/specs/case-spec-form");
            case 7 -> new ProductComponentSpecFormDTO("psu", new PowerSupply(), "product/specs/powersupply-spec-form");
            case 8 -> new ProductComponentSpecFormDTO("cl", new Cooling(), "product/specs/cooling-spec-form");
            case 9 -> new ProductComponentSpecFormDTO(null, null, "product/specs/fan-spec-form");
            default -> new ProductComponentSpecFormDTO(null, null, DEFAULT_TEMPLATE);
        };
    }

    public ProductComponentSpecFormDTO buildEditSpecForm(int categoryId, int productId) {
        return switch (categoryId) {
            case 1 -> new ProductComponentSpecFormDTO(
                    "mb",
                    mainboardRepository.findByProduct_ProductId(productId).orElseGet(Mainboard::new),
                    "product/specs/mainboard-spec-form"
            );
            case 2 -> new ProductComponentSpecFormDTO(
                    "cpu",
                    cpuRepository.findByProduct_ProductId(productId).orElseGet(CPU::new),
                    "product/specs/cpu-spec-form"
            );
            case 3 -> new ProductComponentSpecFormDTO(
                    "gpu",
                    gpuRepository.findByProduct_ProductId(productId).orElseGet(GPU::new),
                    "product/specs/gpu-spec-form"
            );
            case 4 -> new ProductComponentSpecFormDTO(
                    "mem",
                    memoryRepository.findByProduct_ProductId(productId).orElseGet(Memory::new),
                    "product/specs/memory-spec-form"
            );
            case 5 -> new ProductComponentSpecFormDTO(
                    "storage",
                    storageRepository.findByProduct_ProductId(productId).orElseGet(Storage::new),
                    "product/specs/storage-spec-form"
            );
            case 6 -> new ProductComponentSpecFormDTO(
                    "pcase",
                    caseRepository.findByProduct_ProductId(productId).orElseGet(Case::new),
                    "product/specs/case-spec-form"
            );
            case 7 -> new ProductComponentSpecFormDTO(
                    "psu",
                    powerSupplyRepository.findByProduct_ProductId(productId).orElseGet(PowerSupply::new),
                    "product/specs/powersupply-spec-form"
            );
            case 8 -> new ProductComponentSpecFormDTO(
                    "cl",
                    coolingRepository.findByProduct_ProductId(productId).orElseGet(Cooling::new),
                    "product/specs/cooling-spec-form"
            );
            case 9 -> new ProductComponentSpecFormDTO(null, null, "product/specs/fan-spec-form");
            default -> new ProductComponentSpecFormDTO(null, null, DEFAULT_TEMPLATE);
        };
    }

    public List<String> validateSpecParams(int categoryId, Map<String, String> params) {
        List<String> errors = new ArrayList<>();

        switch (categoryId) {
            case 1 -> {
                if (isBlankOrNull(params.get("mainboard.socket")))
                    errors.add("Socket is required");
                if (isBlankOrNull(params.get("mainboard.chipset")))
                    errors.add("Chipset is required");
                if (isBlankOrNull(params.get("mainboard.formFactor")))
                    errors.add("Form Factor is required");
                if (isBlankOrNull(params.get("mainboard.memoryType")))
                    errors.add("Memory Type is required");
                if (getInt(params, "mainboard.memorySlots") == null || getInt(params, "mainboard.memorySlots") < 1)
                    errors.add("Memory Slots must be at least 1");
                if (getInt(params, "mainboard.maxMemorySpeed") == null || getInt(params, "mainboard.maxMemorySpeed") < 1)
                    errors.add("Max Memory Speed must be at least 1");
                if (isBlankOrNull(params.get("mainboard.pcieVersion")))
                    errors.add("PCIe Version is required");
            }
            case 2 -> {
                if (isBlankOrNull(params.get("cpu.socket")))
                    errors.add("Socket is required");
                if (getInt(params, "cpu.tdp") == null || getInt(params, "cpu.tdp") < 1)
                    errors.add("TDP must be at least 1");
                if (isBlankOrNull(params.get("cpu.pcieVersion")))
                    errors.add("PCIe Version is required");
            }
            case 3 -> {
                if (getInt(params, "gpu.vram") == null || getInt(params, "gpu.vram") < 1)
                    errors.add("VRAM is required");
                if (isBlankOrNull(params.get("gpu.memoryType")))
                    errors.add("Memory Type is required");
                if (getInt(params, "gpu.tdp") == null || getInt(params, "gpu.tdp") < 1)
                    errors.add("TDP must be at least 1");
            }
            case 4 -> {
                if (getInt(params, "mem.capacity") == null || getInt(params, "mem.capacity") < 1)
                    errors.add("Capacity is required");
                if (isBlankOrNull(params.get("mem.type")))
                    errors.add("Type is required");
                if (getInt(params, "mem.speed") == null || getInt(params, "mem.speed") < 1)
                    errors.add("Speed is required");
            }
            case 5 -> {
                if (getInt(params, "storage.capacity") == null || getInt(params, "storage.capacity") < 1)
                    errors.add("Capacity is required");
                if (isBlankOrNull(params.get("storage.type")))
                    errors.add("Type is required");
                if (isBlankOrNull(params.get("storage.interfaceType")))
                    errors.add("Interface Type is required");
            }
            case 6 -> {
                if (isBlankOrNull(params.get("pcase.formFactor")))
                    errors.add("Form Factor is required");
                if (getInt(params, "pcase.gpu.maxLength") == null)
                    errors.add("GPU Max Length is required");
            }
            case 7 -> {
                if (getInt(params, "psu.wattage") == null || getInt(params, "psu.wattage") < 1)
                    errors.add("Wattage is required");
                if (isBlankOrNull(params.get("psu.efficiency")))
                    errors.add("Efficiency is required");
            }
            case 8 -> {
                if (isBlankOrNull(params.get("cl.type")))
                    errors.add("Type is required");
                if (getInt(params, "cl.tdp") == null || getInt(params, "cl.tdp") < 1)
                    errors.add("TDP is required");
            }
        }

        return errors;
    }

    @Transactional
    public void saveProductSpec(Product product, int categoryId, Map<String, String> params) {
        switch (categoryId) {
            case 1 -> {
                Mainboard mb = mainboardRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(Mainboard::new);
                mb.setProduct(product);
                mb.setSocket(params.getOrDefault("mainboard.socket", ""));
                mb.setChipset(params.getOrDefault("mainboard.chipset", ""));
                mb.setFormFactor(params.getOrDefault("mainboard.formFactor", ""));
                mb.setMemoryType(params.getOrDefault("mainboard.memoryType", ""));

                Integer memSlots = getInt(params, "mainboard.memorySlots");
                mb.setMemorySlots(memSlots != null ? memSlots : 0);

                Integer maxMemSpeed = getInt(params, "mainboard.maxMemorySpeed");
                mb.setMaxMemorySpeed(maxMemSpeed != null ? maxMemSpeed : 0);

                mb.setPcieVersion(params.getOrDefault("mainboard.pcieVersion", ""));

                Integer m2 = getInt(params, "mainboard.m2Slots");
                mb.setM2Slots(m2 != null ? m2 : 0);

                Integer sata = getInt(params, "mainboard.sataPorts");
                mb.setSataPorts(sata != null ? sata : 0);

                validateComponentSpec(mb);
                mainboardRepository.save(mb);
                updateProductCategoriesFromSpec(product, categoryId, mb);
            }
            case 2 -> {
                CPU cpu = cpuRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(CPU::new);
                cpu.setProduct(product);
                cpu.setSocket(params.getOrDefault("cpu.socket", ""));

                Integer tdp = getInt(params, "cpu.tdp");
                cpu.setTdp(tdp != null ? tdp : 0);

                Integer maxMemSpeed = getInt(params, "cpu.maxMemorySpeed");
                cpu.setMaxMemorySpeed(maxMemSpeed != null ? maxMemSpeed : 0);

                Integer memChannels = getInt(params, "cpu.memoryChannels");
                cpu.setMemoryChannels(memChannels != null ? memChannels : 0);

                cpu.setHasIGPU(getBool(params, "cpu.hasIGPU"));
                cpu.setPcieVersion(params.getOrDefault("cpu.pcieVersion", ""));

                validateComponentSpec(cpu);
                cpuRepository.save(cpu);
                updateProductCategoriesFromSpec(product, categoryId, cpu);
            }
            case 3 -> {
                GPU gpu = gpuRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(GPU::new);
                gpu.setProduct(product);

                Integer vram = getInt(params, "gpu.vram");
                gpu.setVram(vram != null ? vram : 0);

                gpu.setMemoryType(params.getOrDefault("gpu.memoryType", ""));

                Integer tdp = getInt(params, "gpu.tdp");
                gpu.setTdp(tdp != null ? tdp : 0);

                Integer length = getInt(params, "gpu.length");
                gpu.setLength(length != null ? length : 0);

                gpu.setGpuInterface(params.getOrDefault("gpu.gpuInterface", ""));
                gpu.setPcieVersion(params.getOrDefault("gpu.pcieVersion", ""));

                validateComponentSpec(gpu);
                gpuRepository.save(gpu);
                updateProductCategoriesFromSpec(product, categoryId, gpu);
            }
            case 4 -> {
                Memory mem = memoryRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(Memory::new);
                mem.setProduct(product);

                Integer capacity = getInt(params, "mem.capacity");
                mem.setCapacity(capacity != null ? capacity : 0);

                mem.setType(params.getOrDefault("mem.type", ""));

                Integer speed = getInt(params, "mem.speed");
                mem.setSpeed(speed != null ? speed : 0);

                Integer tdp = getInt(params, "mem.tdp");
                mem.setTdp(tdp != null ? tdp : 0);

                Integer modules = getInt(params, "mem.modules");
                mem.setModules(modules != null ? modules : 0);

                validateComponentSpec(mem);
                memoryRepository.save(mem);
                updateProductCategoriesFromSpec(product, categoryId, mem);
            }
            case 5 -> {
                Storage st = storageRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(Storage::new);
                st.setProduct(product);

                Integer capacity = getInt(params, "storage.capacity");
                st.setCapacity(capacity != null ? capacity : 0);

                st.setType(params.getOrDefault("storage.type", ""));
                st.setInterfaceType(params.getOrDefault("storage.interfaceType", ""));

                Integer readSpeed = getInt(params, "storage.readSpeed");
                st.setReadSpeed(readSpeed != null ? readSpeed : 0);

                Integer writeSpeed = getInt(params, "storage.writeSpeed");
                st.setWriteSpeed(writeSpeed != null ? writeSpeed : 0);

                validateComponentSpec(st);
                storageRepository.save(st);
                updateProductCategoriesFromSpec(product, categoryId, st);
            }
            case 6 -> {
                Case pcCase = caseRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(Case::new);
                pcCase.setProduct(product);
                pcCase.setFormFactor(params.getOrDefault("pcase.formFactor", ""));

                Integer gpuMax = getInt(params, "pcase.gpu.maxLength");
                pcCase.setGpuMaxLength(gpuMax != null ? gpuMax : 0);

                pcCase.setPsuFormFactor(params.getOrDefault("pcase.psuFormFactor", ""));

                Integer cpuMax = getInt(params, "pcase.cpu.maxCoolerHeight");
                pcCase.setCpuMaxCoolerHeight(cpuMax != null ? cpuMax : 0);

                validateComponentSpec(pcCase);
                caseRepository.save(pcCase);
                updateProductCategoriesFromSpec(product, categoryId, pcCase);
            }
            case 7 -> {
                PowerSupply psu = powerSupplyRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(PowerSupply::new);
                psu.setProduct(product);

                Integer wattage = getInt(params, "psu.wattage");
                psu.setWattage(wattage != null ? wattage : 0);

                psu.setEfficiency(params.getOrDefault("psu.efficiency", ""));
                psu.setModular(getBool(params, "psu.modular"));
                psu.setFormFactor(params.getOrDefault("psu.formFactor", ""));

                validateComponentSpec(psu);
                powerSupplyRepository.save(psu);
                updateProductCategoriesFromSpec(product, categoryId, psu);
            }
            case 8 -> {
                Cooling cl = coolingRepository.findByProduct_ProductId(product.getProductId())
                        .orElseGet(Cooling::new);
                cl.setProduct(product);
                cl.setType(params.getOrDefault("cl.type", ""));

                Integer fanSize = getInt(params, "cl.fanSize");
                cl.setFanSize(fanSize != null ? fanSize : 0);

                Integer radiatorSize = getInt(params, "cl.radiatorSize");
                cl.setRadiatorSize(radiatorSize != null ? radiatorSize : 0);

                Integer tdp = getInt(params, "cl.tdp");
                cl.setTdp(tdp != null ? tdp : 0);

                validateComponentSpec(cl);
                coolingRepository.save(cl);
                updateProductCategoriesFromSpec(product, categoryId, cl);
            }
            default -> { }
        }
    }

    private void updateProductCategoriesFromSpec(Product product, int categoryId, Object spec) {
        List<Category> allCategories = componentSpecCategoryService.resolveCategoriesForSpec(categoryId, spec);
        product.setCategories(allCategories);
        productService.saveProduct(product);
    }

    private boolean isBlankOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Integer getInt(Map<String, String> params, String key) {
        try {
            String value = params.get(key);
            return value == null || value.isBlank() ? null : Integer.parseInt(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean getBool(Map<String, String> params, String key) {
        return "true".equalsIgnoreCase(params.get(key));
    }

    private void validateComponentSpec(Object component) {
        List<String> errors = new ArrayList<>();
        if (component instanceof Mainboard mb) {
            errors = specValidationService.validateMainboard(mb);
        } else if (component instanceof CPU cpu) {
            errors = specValidationService.validateCpu(cpu);
        } else if (component instanceof GPU gpu) {
            errors = specValidationService.validateGpu(gpu);
        } else if (component instanceof Memory memory) {
            errors = specValidationService.validateMemory(memory);
        } else if (component instanceof Storage storage) {
            errors = specValidationService.validateStorage(storage);
        } else if (component instanceof Case pcCase) {
            errors = specValidationService.validateCase(pcCase);
        } else if (component instanceof PowerSupply psu) {
            errors = specValidationService.validatePowerSupply(psu);
        } else if (component instanceof Cooling cooling) {
            errors = specValidationService.validateCooling(cooling);
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }
}
