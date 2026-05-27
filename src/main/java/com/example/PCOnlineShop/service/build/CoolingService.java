package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.Cooling;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.CoolingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CoolingService {
    private final CoolingRepository coolingRepository;

    public List<Cooling> getCoolings() {
        return coolingRepository.findAllWithImages();
    }

    public Cooling addCooling(Cooling cooling) {
        return coolingRepository.save(cooling);
    }

    public Cooling updateCooling(Cooling cooling) {
        return coolingRepository.save(cooling);
    }

    public void deleteCooling(int id) {
        coolingRepository.deleteById(id);
    }

    public List<Cooling> filterCoolings(List<Cooling> coolings, List<String> brands, String sortBy) {
        if (coolings == null || coolings.isEmpty()) {
            return List.of();
        }

        if (brands != null && !brands.isEmpty()) {
            coolings = coolings.stream()
                    .filter(c -> c.getProduct() != null
                            && c.getProduct().getBrand() != null
                            && brands.contains(c.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<Cooling> comparator = getCoolingComparator(sortBy);
        if (comparator != null) {
            coolings = coolings.stream()
                    .sorted(comparator)
                    .toList();
        }
        return coolings;
    }

    private Comparator<Cooling> getCoolingComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(c -> c.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((Cooling c) -> c.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(c -> c.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((Cooling c) -> c.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<Cooling> coolings) {
        if (coolings == null || coolings.isEmpty()) {
            return List.of();
        }

        return coolings.stream()
                .map(Cooling::getProduct)
                .filter(Objects::nonNull)
                .map(product -> product.getBrand())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}
