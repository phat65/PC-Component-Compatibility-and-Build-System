package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.GPU;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.GpuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GpuService {
    private final GpuRepository gpuRepository;

    public List<GPU> getAllGpu() {
        return gpuRepository.findAllWithImages();
    }

    public GPU addGpu(GPU gpu) {
        return gpuRepository.save(gpu);
    }

    public GPU updateGpu(GPU gpu) {
        return gpuRepository.save(gpu);
    }

    public void deleteGpu(int id) {
        gpuRepository.deleteById(id);
    }

    public List<GPU> filterGpus(List<GPU> gpus, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            gpus = gpus.stream()
                    .filter(gpu -> brands.contains(gpu.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<GPU> comparator = getGpuComparator(sortBy);
        if (comparator != null) {
            gpus = gpus.stream()
                    .sorted(comparator)
                    .toList();
        }
        return gpus;
    }

    private Comparator<GPU> getGpuComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(gpu -> gpu.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((GPU gpu) -> gpu.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(gpu -> gpu.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((GPU gpu) -> gpu.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<GPU> gpus) {
        return gpus.stream()
                .map(gpu -> gpu.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
