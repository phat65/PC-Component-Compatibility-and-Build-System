package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.PowerSupply;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.PowerSupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PowerSupplyService {
    private final PowerSupplyRepository powerSupplyRepository;

    public List<PowerSupply> getAllPowerSupply() {
        return powerSupplyRepository.findAllWithImages();
    }

    public PowerSupply addPowerSupply(PowerSupply powerSupply) {
        return powerSupplyRepository.save(powerSupply);
    }

    public PowerSupply updatePowerSupply(PowerSupply powerSupply) {
        return powerSupplyRepository.save(powerSupply);
    }

    public Optional<PowerSupply> findSelectablePowerSupplyByProductId(int productId) {
        return powerSupplyRepository.findByIdWithImages(productId);
    }

    public void deletePowerSupply(int id) {
        powerSupplyRepository.deleteById(id);
    }

    public List<PowerSupply> filterPowerSupplies(List<PowerSupply> powerSupplies, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            powerSupplies = powerSupplies.stream()
                    .filter(psu -> brands.contains(psu.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<PowerSupply> comparator = getPowerSupplyComparator(sortBy);
        if (comparator != null) {
            powerSupplies = powerSupplies.stream()
                    .sorted(comparator)
                    .toList();
        }
        return powerSupplies;
    }

    private Comparator<PowerSupply> getPowerSupplyComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(psu -> psu.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((PowerSupply psu) -> psu.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(psu -> psu.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((PowerSupply psu) -> psu.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<PowerSupply> powerSupplies) {
        return powerSupplies.stream()
                .map(psu -> psu.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
