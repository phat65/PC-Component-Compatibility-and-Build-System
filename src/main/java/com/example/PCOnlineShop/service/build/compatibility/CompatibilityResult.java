package com.example.PCOnlineShop.service.build.compatibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record CompatibilityResult(boolean compatible, List<String> reasons) {

    public CompatibilityResult {
        reasons = reasons == null ? List.of() : List.copyOf(reasons);
    }

    public static CompatibilityResult compatible() {
        return new CompatibilityResult(true, List.of());
    }

    public static CompatibilityResult incompatible(String reason) {
        return new CompatibilityResult(false, List.of(reason));
    }

    public static CompatibilityResult incompatible(List<String> reasons) {
        return new CompatibilityResult(false, reasons);
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasReasons() {
        return !reasons.isEmpty();
    }

    public static class Builder {
        private final List<String> reasons = new ArrayList<>();

        public Builder addReason(String reason) {
            if (reason != null && !reason.isBlank()) {
                reasons.add(reason);
            }
            return this;
        }

        public Builder addReasons(List<String> reasons) {
            if (reasons != null) {
                reasons.forEach(this::addReason);
            }
            return this;
        }

        public CompatibilityResult build() {
            if (reasons.isEmpty()) {
                return CompatibilityResult.compatible();
            }
            return CompatibilityResult.incompatible(Collections.unmodifiableList(reasons));
        }
    }
}
