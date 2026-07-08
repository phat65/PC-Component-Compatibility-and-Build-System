package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.dto.build.BuildPlanDto;
import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.build.Storage;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.build.CaseRepository;
import com.example.PCOnlineShop.repository.build.CoolingRepository;
import com.example.PCOnlineShop.repository.build.CpuRepository;
import com.example.PCOnlineShop.repository.build.GpuRepository;
import com.example.PCOnlineShop.repository.build.MainboardRepository;
import com.example.PCOnlineShop.repository.build.MemoryRepository;
import com.example.PCOnlineShop.repository.build.PowerSupplyRepository;
import com.example.PCOnlineShop.repository.build.StorageRepository;
import com.example.PCOnlineShop.service.build.compatibility.BuildPowerCalculator;
import com.example.PCOnlineShop.service.build.compatibility.CaseCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.CoolingCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.CpuCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.GpuCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.MainboardCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.MemoryCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.PowerSupplyCompatibilityValidator;
import com.example.PCOnlineShop.service.build.compatibility.StorageCompatibilityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleBasedBuildServiceTest {

    @Mock
    private CpuRepository cpuRepository;
    @Mock
    private GpuRepository gpuRepository;
    @Mock
    private MainboardRepository mainboardRepository;
    @Mock
    private MemoryRepository memoryRepository;
    @Mock
    private StorageRepository storageRepository;
    @Mock
    private PowerSupplyRepository powerSupplyRepository;
    @Mock
    private CaseRepository caseRepository;
    @Mock
    private CoolingRepository coolingRepository;

    private RuleBasedBuildService buildService;

    @BeforeEach
    void setUp() {
        BuildPowerCalculator powerCalculator = new BuildPowerCalculator();
        CoolingCompatibilityValidator coolingValidator = new CoolingCompatibilityValidator();
        CompatibilityService compatibilityService = new CompatibilityService(
                new MainboardCompatibilityValidator(),
                new CpuCompatibilityValidator(),
                new GpuCompatibilityValidator(powerCalculator),
                new CaseCompatibilityValidator(coolingValidator),
                new PowerSupplyCompatibilityValidator(powerCalculator),
                new MemoryCompatibilityValidator(),
                new StorageCompatibilityValidator(),
                coolingValidator);

        buildService = new RuleBasedBuildService(
                cpuRepository,
                gpuRepository,
                mainboardRepository,
                memoryRepository,
                storageRepository,
                powerSupplyRepository,
                caseRepository,
                coolingRepository,
                compatibilityService);
    }

    @Test
    void suggestBuildUsesSellableFallbackForPsuInsteadOfRepositoryFindAll() {
        when(mainboardRepository.findBestMainboardsByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(mainboard(1, "Mainboard", 90)));
        when(cpuRepository.findBestCpusByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(cpu(2, "CPU", 260, 90)));
        when(memoryRepository.findBestMemoryByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(memory(3, "Memory", 120)));
        when(gpuRepository.findBestGpusByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(gpu(4, "GPU", 420, 300)));
        when(storageRepository.findBestStorageByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(storage(5, "Storage", 120)));
        when(powerSupplyRepository.findBestPsuByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(powerSupply(6, "Too Small After Cooling PSU", 80, 750)));
        when(powerSupplyRepository.findAllWithImages())
                .thenReturn(List.of(powerSupply(7, "Sellable Fallback PSU", 140, 800)));
        when(coolingRepository.findBestCoolingByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(cooling(8, "Cooling", 40)));
        when(caseRepository.findBestCasesByBudgetAndScore(anyDouble(), anyInt()))
                .thenReturn(List.of(pcCase(9, "Case", 80)));

        BuildPlanDto plan = buildService.suggestBuild("GAMING_MID", 1200);

        assertThat(plan.getPowerSupply()).isNotNull();
        assertThat(plan.getPowerSupply().getProductName()).isEqualTo("Sellable Fallback PSU");
        verify(powerSupplyRepository).findAllWithImages();
        verify(powerSupplyRepository, never()).findAll();
    }

    private Mainboard mainboard(int id, String name, double price) {
        Mainboard mainboard = new Mainboard();
        mainboard.setProductId(id);
        mainboard.setProduct(product(id, name, price, 80));
        mainboard.setSocket("AM5");
        mainboard.setMemoryType("DDR5");
        mainboard.setMemorySlots(4);
        mainboard.setMaxMemorySpeed(6000);
        mainboard.setPcieVersion("PCIe 4.0 x16");
        mainboard.setM2Slots(1);
        mainboard.setSataPorts(4);
        mainboard.setFormFactor("ATX");
        return mainboard;
    }

    private CPU cpu(int id, String name, double price, int tdp) {
        CPU cpu = new CPU();
        cpu.setProductId(id);
        cpu.setProduct(product(id, name, price, 80));
        cpu.setSocket("AM5");
        cpu.setTdp(tdp);
        cpu.setMaxMemorySpeed(6000);
        cpu.setMemoryChannels(2);
        cpu.setPcieVersion("PCIe 4.0 x16");
        return cpu;
    }

    private Memory memory(int id, String name, double price) {
        Memory memory = new Memory();
        memory.setProductId(id);
        memory.setProduct(product(id, name, price, 60));
        memory.setType("DDR5");
        memory.setSpeed(5600);
        memory.setModules(2);
        memory.setTdp(10);
        return memory;
    }

    private GPU gpu(int id, String name, double price, int tdp) {
        GPU gpu = new GPU();
        gpu.setProductId(id);
        gpu.setProduct(product(id, name, price, 80));
        gpu.setPcieVersion("PCIe 4.0 x16");
        gpu.setLength(300);
        gpu.setTdp(tdp);
        return gpu;
    }

    private Storage storage(int id, String name, double price) {
        Storage storage = new Storage();
        storage.setProductId(id);
        storage.setProduct(product(id, name, price, 60));
        storage.setInterfaceType("NVMe");
        return storage;
    }

    private PowerSupply powerSupply(int id, String name, double price, int wattage) {
        PowerSupply powerSupply = new PowerSupply();
        powerSupply.setProductId(id);
        powerSupply.setProduct(product(id, name, price, 60));
        powerSupply.setWattage(wattage);
        powerSupply.setFormFactor("ATX");
        return powerSupply;
    }

    private Cooling cooling(int id, String name, double price) {
        Cooling cooling = new Cooling();
        cooling.setProductId(id);
        cooling.setProduct(product(id, name, price, 60));
        cooling.setType("Air");
        cooling.setFanSize(120);
        cooling.setTdp(50);
        return cooling;
    }

    private Case pcCase(int id, String name, double price) {
        Case pcCase = new Case();
        pcCase.setProductId(id);
        pcCase.setProduct(product(id, name, price, 60));
        pcCase.setFormFactor("ATX");
        pcCase.setGpuMaxLength(360);
        pcCase.setCpuMaxCoolerHeight(170);
        pcCase.setPsuFormFactor("ATX");
        return pcCase;
    }

    private Product product(int id, String name, double price, int score) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setPrice(price);
        product.setPerformanceScore(score);
        product.setStatus(true);
        return product;
    }
}
