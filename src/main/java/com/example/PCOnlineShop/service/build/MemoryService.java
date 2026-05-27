package com.example.PCOnlineShop.service.build;

import com.example.PCOnlineShop.model.build.Memory;
import com.example.PCOnlineShop.model.product.Brand;
import com.example.PCOnlineShop.repository.build.MemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemoryService {
    private final MemoryRepository memoryRepository;

    public List<Memory> getAllMemory() {
        return memoryRepository.findAllWithImages();
    }

    public Memory addMemory(Memory memory) {
        return memoryRepository.save(memory);
    }

    public Memory updateMemory(Memory memory) {
        return memoryRepository.save(memory);
    }

    public void deleteMemory(int id) {
        memoryRepository.deleteById(id);
    }

    public List<Memory> filterMemories(List<Memory> memories, List<String> brands, String sortBy) {
        if (brands != null && !brands.isEmpty()) {
            memories = memories.stream()
                    .filter(memory -> brands.contains(memory.getProduct().getBrand().getName()))
                    .toList();
        }

        Comparator<Memory> comparator = getMemoryComparator(sortBy);
        if (comparator != null) {
            memories = memories.stream()
                    .sorted(comparator)
                    .toList();
        }
        return memories;
    }

    private Comparator<Memory> getMemoryComparator(String sortBy) {
        return switch (sortBy == null ? "" : sortBy) {
            case "priceAsc" -> Comparator.comparing(memory -> memory.getProduct().getPrice());
            case "priceDesc" -> Comparator.comparing((Memory memory) -> memory.getProduct().getPrice()).reversed();
            case "nameAsc" -> Comparator.comparing(memory -> memory.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing((Memory memory) -> memory.getProduct().getProductName(), String.CASE_INSENSITIVE_ORDER).reversed();
            default -> null;
        };
    }

    public List<Brand> getAllBrands(List<Memory> memories) {
        return memories.stream()
                .map(memory -> memory.getProduct().getBrand())
                .distinct()
                .toList();
    }
}
