package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.Storage;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;

    public List<Storage> getAllStorages() {
        return storageRepository.findAllWithImages();
    }


    public Storage addStorage(Storage storage) {
        return storageRepository.save(storage);
    }

    public Storage updateStorage(Storage storage) {
        return storageRepository.save(storage);
    }

    public void deleteStorage(int id) {
        storageRepository.deleteById(id);
    }

    public List<Storage> filterStorages(List<Storage> storages, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            storages = storages.stream()
                    .filter(storage -> brands.contains(storage.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<Storage> comparator = getStorageComparator(sortBy);
        if (comparator != null) {
            storages = storages.stream()
                    .sorted(comparator)
                    .toList();
        }
        return storages;
    }

    private Comparator<Storage> getStorageComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(storage -> storage.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((Storage storage) -> storage.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(storage -> storage.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((Storage storage) -> storage.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<Storage> storages) {
        return storages.stream()
                .map(storage -> storage.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
