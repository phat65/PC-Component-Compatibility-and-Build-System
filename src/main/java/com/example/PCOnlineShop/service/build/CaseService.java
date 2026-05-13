package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.Case;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CaseService {
    private final CaseRepository caseRepository;

    public List<Case> getAllCases() {
        return caseRepository.findAllWithImages();
    }

    public Case addCase(Case pcCase) {
        return caseRepository.save(pcCase);
    }

    public Case updateCase(Case pcCase) {
        return caseRepository.save(pcCase);
    }

    public Optional<Case> findSelectableCaseByProductId(int productId) {
        return caseRepository.findByIdWithImages(productId);
    }

    public void deleteCase(int id) {
        caseRepository.deleteById(id);
    }

    public List<Case> filterCases(List<Case> cases, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            cases = cases.stream()
                    .filter(c -> brands.contains(c.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<Case> comparator = getCaseComparator(sortBy);
        if (comparator != null) {
            cases = cases.stream()
                    .sorted(comparator)
                    .toList();
        }
        return cases;
    }

    private Comparator<Case> getCaseComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(c -> c.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((Case c) -> c.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(c -> c.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((Case c) -> c.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<Case> cases) {
        return cases.stream()
                .map(c -> c.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
