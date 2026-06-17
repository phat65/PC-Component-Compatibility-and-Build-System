package com.example.PCOnlineShop.dto.build;

import com.example.PCOnlineShop.model.build.*;
import com.example.PCOnlineShop.model.product.Product;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildItemDto {
    private static final Set<String> OTHER_CHILD_CATEGORIES = Set.of(
            "keyboard",
            "mouse",
            "monitor",
            "chair",
            "headset",
            "mousepad",
            "speaker",
            "desk",
            "webcam",
            "add-on"
    );

    private Mainboard mainboard;
    private CPU cpu;
    private Memory memory;
    private GPU gpu;
    private Storage storage;
    private PowerSupply powerSupply;
    private Case pcCase;
    private Cooling cooling;
    private List<Product> other = new ArrayList<>();

    public boolean isEmpty() {
        return mainboard == null && cpu == null && memory == null && gpu == null &&
               storage == null && powerSupply == null && pcCase == null &&
               cooling == null && !hasOtherProducts();
    }

    public double calculateTotalPrice() {
        double total = 0;
        if (mainboard != null) total += mainboard.getPrice();
        if (cpu != null) total += cpu.getPrice();
        if (memory != null) total += memory.getPrice();
        if (gpu != null) total += gpu.getPrice();
        if (storage != null) total += storage.getPrice();
        if (powerSupply != null) total += powerSupply.getPrice();
        if (pcCase != null) total += pcCase.getPrice();
        if (cooling != null) total += cooling.getPrice();
        if (other != null) {
            total += other.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(Product::getPrice)
                    .sum();
        }
        // Round to 2 decimal places
        return Math.round(total * 100.0) / 100.0;
    }

    public boolean hasOtherProducts() {
        return other != null && !other.isEmpty();
    }

    public boolean hasOtherProduct(Integer productId) {
        if (productId == null || other == null) {
            return false;
        }

        return other.stream()
                .filter(Objects::nonNull)
                .anyMatch(product -> product.getProductId() == productId);
    }

    public void clearOtherProducts() {
        if (other == null) {
            other = new ArrayList<>();
            return;
        }
        other.clear();
    }

    public void selectOtherProduct(Product product) {
        if (product == null) {
            return;
        }
        if (other == null) {
            other = new ArrayList<>();
        }

        String incomingCategory = resolveOtherChildCategory(product);
        other.removeIf(existing -> shouldReplaceOtherProduct(existing, product, incomingCategory));

        if (!hasOtherProduct(product.getProductId())) {
            other.add(product);
        }
    }

    private boolean shouldReplaceOtherProduct(Product existing, Product incoming, String incomingCategory) {
        if (existing == null) {
            return false;
        }
        if (existing.getProductId() == incoming.getProductId()) {
            return true;
        }

        String existingCategory = resolveOtherChildCategory(existing);
        return incomingCategory != null && incomingCategory.equals(existingCategory);
    }

    private String resolveOtherChildCategory(Product product) {
        if (product == null || product.getCategories() == null) {
            return null;
        }

        return product.getCategories().stream()
                .filter(Objects::nonNull)
                .map(category -> category.getCategoryName() == null ? "" : category.getCategoryName().trim().toLowerCase())
                .filter(OTHER_CHILD_CATEGORIES::contains)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get formatted total price string with 2 decimal places
     */
    public String getFormattedTotalPrice() {
        return String.format("%.2f", calculateTotalPrice());
    }
}
