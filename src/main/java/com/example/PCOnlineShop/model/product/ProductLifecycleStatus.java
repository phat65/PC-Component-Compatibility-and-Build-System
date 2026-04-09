package com.example.PCOnlineShop.model.product;

public enum ProductLifecycleStatus {
    DRAFT("Draft"),
    SELLING("Selling"),
    DISCONTINUED("Discontinued");

    private final String displayName;

    ProductLifecycleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
