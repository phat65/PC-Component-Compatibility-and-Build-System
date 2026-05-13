package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.Mainboard;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.MainboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainboardService {
    private final MainboardRepository mainboardRepository;

    @Transactional(readOnly = true)
    public List<Mainboard> getAllMainboards() {
        return mainboardRepository.findAllWithImages();
    }

    public Mainboard addMainboard(Mainboard mainboard) {
        return mainboardRepository.save(mainboard);
    }

    public Mainboard updateMainboard(Mainboard mainboard) {
        return mainboardRepository.save(mainboard);
    }

    @Transactional(readOnly = true)
    public Optional<Mainboard> findSelectableMainboardByProductId(int productId) {
        return mainboardRepository.findByIdWithImages(productId);
    }

    public void deleteMainboard(int id) {
        mainboardRepository.deleteById(id);
    }

    public List<Mainboard> filterMainboards(List<Mainboard> mainboards, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            mainboards = mainboards.stream()
                    .filter(mb -> brands.contains(mb.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<Mainboard> comparator = getMainboardComparator(sortBy);
        if (comparator != null) {
            mainboards = mainboards.stream()
                    .sorted(comparator)
                    .toList();
        }
        return mainboards;
    }

    private Comparator<Mainboard> getMainboardComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(mb -> mb.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((Mainboard mb) -> mb.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(mb -> mb.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((Mainboard mb) -> mb.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<Mainboard> mainboards) {
        return mainboards.stream()
                .map(mb -> mb.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
