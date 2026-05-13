package com.example.PCOnlineShop.service.build;
import com.example.PCOnlineShop.model.build.CPU;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.CpuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CpuService {
    private final CpuRepository cpuRepository;

    public List<CPU> getAllCpus() {
        return cpuRepository.findAllWithImages();
    }

    public CPU addCpu(CPU cpu) {
        return cpuRepository.save(cpu);
    }

    public CPU updateCpu(CPU cpu) {
        return cpuRepository.save(cpu);
    }

    public Optional<CPU> findSelectableCpuByProductId(int productId) {
        return cpuRepository.findByIdWithImages(productId);
    }

    public void deleteCpu(int id) {
        cpuRepository.deleteById(id);
    }

    public List<CPU> filterCpus(List<CPU> cpus, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            cpus = cpus.stream()
                    .filter(cpu -> brands.contains(cpu.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<CPU> comparator = getCpuComparator(sortBy);
        if (comparator != null) {
            cpus = cpus.stream()
                    .sorted(comparator)
                    .toList();
        }
        return cpus;
    }

    private Comparator<CPU> getCpuComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(cpu -> cpu.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((CPU cpu) -> cpu.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(cpu -> cpu.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((CPU cpu) -> cpu.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<CPU> cpus) {
        return cpus.stream()
                .map(cpu -> cpu.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
