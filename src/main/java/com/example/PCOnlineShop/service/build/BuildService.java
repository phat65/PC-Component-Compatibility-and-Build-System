package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.build.*;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.build.*;
import com.example.PCOnlineShop.repository.product.ProductRepository;
import com.example.PCOnlineShop.service.build.compatibility.CompatibilityResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BuildService {
    private final CompatibilityService compatibilityService;
    private final MainboardRepository mainboardRepository;
    private final GpuRepository gpuRepository;
    private final CpuRepository cpuRepository;
    private final CaseRepository caseRepository;
    private final MemoryRepository memoryRepository;
    private final StorageRepository storageRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final CoolingRepository coolingRepository;
    private final ProductRepository productRepository;

    /**
     * Get compatible mainboards based on current build
     * Returns all mainboards if build is empty, otherwise filters by compatibility
     */
    public List<Mainboard> getCompatibleMainboards(BuildItemDto buildItem) {
        List<Mainboard> allMainboards = mainboardRepository.findAllWithImages();

        if (buildItem.isEmpty()) {
            log.debug("Build is empty, returning all {} mainboards", allMainboards.size());
            return sortByPerformanceAndPrice(allMainboards);
        }

        List<Mainboard> compatible = allMainboards.stream()
                .filter(mainboard -> compatibilityService.checkMainboardCompatibility(buildItem, mainboard))
                .collect(Collectors.toList());

        log.debug("Found {} compatible mainboards out of {}", compatible.size(), allMainboards.size());
        return sortByPerformanceAndPrice(compatible);
    }

    /**
     * Get compatible CPUs based on mainboard socket
     */
    public List<CPU> getCompatibleCpus(BuildItemDto buildItem) {
        List<CPU> allCpus = cpuRepository.findAllWithImages();

        if (buildItem.getMainboard() == null) {
            log.debug("No mainboard selected, returning all {} CPUs", allCpus.size());
            return sortByPerformanceAndPrice(allCpus);
        }

        List<CPU> compatible = allCpus.stream()
                .filter(cpu -> compatibilityService.checkCpuCompatibility(buildItem, cpu))
                .collect(Collectors.toList());

        log.debug("Found {} compatible CPUs for socket {}",
                compatible.size(),
                buildItem.getMainboard().getSocket());
        return sortByPerformanceAndPrice(compatible);
    }

    /**
     * Get compatible GPUs - all GPUs are compatible, sorted by performance
     */
    public List<GPU> getCompatibleGPUs(BuildItemDto buildItem) {
        List<GPU> allGpus = gpuRepository.findAllWithImages();

        List<GPU> compatible = allGpus.stream()
                .filter(gpu -> compatibilityService.checkGpuCompatibility(buildItem, gpu))
                .collect(Collectors.toList());

        log.debug("Found {} compatible GPUs", compatible.size());
        return sortByPerformanceAndPrice(compatible);
    }

    /**
     * Get compatible cases based on form factor, GPU length, and cooler height
     */
    public List<Case> getCompatibleCases(BuildItemDto buildItem) {
        List<Case> allCases = caseRepository.findAllWithImages();

        if (buildItem.isEmpty()) {
            log.debug("Build is empty, returning all {} cases", allCases.size());
            return sortByPrice(allCases);
        }

        List<Case> compatible = allCases.stream()
                .filter(pcCase -> compatibilityService.checkCaseCompatibility(buildItem, pcCase))
                .collect(Collectors.toList());

        log.debug("Found {} compatible cases out of {}", compatible.size(), allCases.size());
        return sortByPrice(compatible);
    }

    /**
     * Get compatible memory based on mainboard's memory type
     */
    public List<Memory> getCompatibleMemory(BuildItemDto buildItem) {
        List<Memory> allMemory = memoryRepository.findAllWithImages();

        if (buildItem.getMainboard() == null) {
            log.debug("No mainboard selected, returning all {} memory", allMemory.size());
            return sortByPerformanceAndPrice(allMemory);
        }

        List<Memory> compatible = allMemory.stream()
                .filter(memory -> compatibilityService.checkMemoryCompatibility(buildItem, memory))
                .collect(Collectors.toList());

        log.debug("Found {} compatible memory for type {}",
                compatible.size(),
                buildItem.getMainboard().getMemoryType());
        return sortByPerformanceAndPrice(compatible);
    }

    /**
     * Get compatible storage - all storage is compatible
     */
    public List<Storage> getCompatibleStorage(BuildItemDto buildItem) {
        List<Storage> allStorage = storageRepository.findAllWithImages();

        List<Storage> compatible = allStorage.stream()
                .filter(storage -> compatibilityService.checkStorageCompatibility(buildItem, storage))
                .collect(Collectors.toList());

        log.debug("Found {} compatible storage devices", compatible.size());
        return sortByPerformanceAndPrice(compatible);
    }

    /**
     * Get compatible power supplies based on required wattage and form factor
     */
    public List<PowerSupply> getCompatiblePowerSupplies(BuildItemDto buildItem) {
        List<PowerSupply> allPowerSupplies = powerSupplyRepository.findAllWithImages();

        if (buildItem.isEmpty()) {
            log.debug("Build is empty, returning all {} PSUs", allPowerSupplies.size());
            return sortByWattageAndPrice(allPowerSupplies);
        }

        List<PowerSupply> compatible = allPowerSupplies.stream()
                .filter(psu -> compatibilityService.checkPowerSupplyCompatibility(buildItem, psu))
                .collect(Collectors.toList());

        log.debug("Found {} compatible PSUs", compatible.size());
        return sortByWattageAndPrice(compatible);
    }

    /**
     * Get compatible cooling based on CPU TDP and case cooler height
     */
    public List<Cooling> getCompatibleCoolings(BuildItemDto buildItem) {
        List<Cooling> allCoolings = coolingRepository.findAllWithImages();

        if (buildItem.getCpu() == null) {
            log.debug("No CPU selected, returning all {} cooling solutions", allCoolings.size());
            return sortByTdpAndPrice(allCoolings);
        }

        List<Cooling> compatible = allCoolings.stream()
                .filter(cooling -> compatibilityService.checkCoolingCompatibility(buildItem, cooling))
                .collect(Collectors.toList());

        log.debug("Found {} compatible cooling solutions for TDP {}",
                compatible.size(),
                buildItem.getCpu().getTdp());
        return sortByTdpAndPrice(compatible);
    }

    public Optional<Mainboard> findSelectableCompatibleMainboardByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                mainboardRepository::findByIdWithImages,
                mainboard -> compatibilityService.validateMainboardCompatibility(buildItem, mainboard));
    }

    public Optional<CPU> findSelectableCompatibleCpuByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                cpuRepository::findByIdWithImages,
                cpu -> compatibilityService.validateCpuCompatibility(buildItem, cpu));
    }

    public Optional<GPU> findSelectableCompatibleGpuByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                gpuRepository::findByIdWithImages,
                gpu -> compatibilityService.validateGpuCompatibility(buildItem, gpu));
    }

    public Optional<Case> findSelectableCompatibleCaseByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                caseRepository::findByIdWithImages,
                pcCase -> compatibilityService.validateCaseCompatibility(buildItem, pcCase));
    }

    public Optional<Memory> findSelectableCompatibleMemoryByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                memoryRepository::findByIdWithImages,
                memory -> compatibilityService.validateMemoryCompatibility(buildItem, memory));
    }

    public Optional<Storage> findSelectableCompatibleStorageByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                storageRepository::findByIdWithImages,
                storage -> compatibilityService.validateStorageCompatibility(buildItem, storage));
    }

    public Optional<PowerSupply> findSelectableCompatiblePowerSupplyByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                powerSupplyRepository::findByIdWithImages,
                psu -> compatibilityService.validatePowerSupplyCompatibility(buildItem, psu));
    }

    public Optional<Cooling> findSelectableCompatibleCoolingByProductId(Integer productId, BuildItemDto buildItem) {
        return findSelectableCompatible(
                productId,
                coolingRepository::findByIdWithImages,
                cooling -> compatibilityService.validateCoolingCompatibility(buildItem, cooling));
    }

    // Helper methods for sorting

    private <T> Optional<T> findSelectableCompatible(Integer productId,
                                                     Function<Integer, Optional<T>> finder,
                                                     Function<T, CompatibilityResult> validator) {
        if (productId == null) {
            return Optional.empty();
        }

        return finder.apply(productId)
                .filter(component -> validator.apply(component).compatible());
    }

    private <T> List<T> sortByPerformanceAndPrice(List<T> items) {
        return sortBy(items, byPerformanceDescThenPrice());
    }

    private <T> List<T> sortByPrice(List<T> items) {
        return sortBy(items, byPriceAsc());
    }

    private List<PowerSupply> sortByWattageAndPrice(List<PowerSupply> items) {
        return sortBy(items, byMetricDescThenPrice(PowerSupply::getWattage));
    }

    private List<Cooling> sortByTdpAndPrice(List<Cooling> items) {
        return sortBy(items, byMetricDescThenPrice(Cooling::getTdp));
    }

    private <T> List<T> sortBy(List<T> items, Comparator<T> comparator) {
        return items.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private <T> Comparator<T> byPerformanceDescThenPrice() {
        return Comparator
                .comparing((T item) -> getPerformanceScore(item), Comparator.reverseOrder())
                .thenComparing(this::getPrice);
    }

    private <T> Comparator<T> byPriceAsc() {
        return Comparator.comparing(this::getPrice);
    }

    private <T, U extends Comparable<? super U>> Comparator<T> byMetricDescThenPrice(Function<T, U> metric) {
        return Comparator
                .comparing(metric, Comparator.reverseOrder())
                .thenComparing(this::getPrice);
    }

    private <T> Integer getPerformanceScore(T item) {
        Product product = getComponentProduct(item);
        return product != null && product.getPerformanceScore() != null ? product.getPerformanceScore() : 0;
    }

    private <T> Double getPrice(T item) {
        Product product = getComponentProduct(item);
        return product != null ? product.getPrice() : 0.0;
    }

    private Product getComponentProduct(Object item) {
        if (item instanceof Mainboard mainboard) {
            return mainboard.getProduct();
        } else if (item instanceof CPU cpu) {
            return cpu.getProduct();
        } else if (item instanceof GPU gpu) {
            return gpu.getProduct();
        } else if (item instanceof Memory memory) {
            return memory.getProduct();
        } else if (item instanceof Storage storage) {
            return storage.getProduct();
        } else if (item instanceof Case pcCase) {
            return pcCase.getProduct();
        } else if (item instanceof PowerSupply powerSupply) {
            return powerSupply.getProduct();
        } else if (item instanceof Cooling cooling) {
            return cooling.getProduct();
        }
        return null;
    }
    // Other (generic product)
    public List<Product> getOtherProducts() {
        List<Product> otherProducts = new ArrayList<>();
        otherProducts= productRepository.findAllWithImages().stream()
                .filter(Product::isSellableOnStorefront)
                .filter(p -> p
                        .getCategories().stream().anyMatch(c -> c
                                .getCategoryName().equalsIgnoreCase("Other"))).toList();
        return sortByPerformanceAndPrice(otherProducts);
    }

    public Optional<Product> findOtherByProductId(Integer productId) {
        return productRepository.findById(productId)
                .filter(Product::isSellableOnStorefront);
    }
}
