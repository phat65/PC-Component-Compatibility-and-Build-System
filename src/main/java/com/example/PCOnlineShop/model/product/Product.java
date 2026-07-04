package com.example.PCOnlineShop.model.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "product_id")
    private int productId;

    @NotBlank(message = "Product name is required" )
    @Size(min = 5, max = 100, message = "Product name must be between 5 and 100 characters")
//    @Pattern(regexp = "^(?=.{3,100}$)(?!.* {2})(?!.*[_\\-.]{2})[ \\p{L}\\p{M}\\p{N}\\s\\-\\._/+&,:;'\"®™()\\[\\]°%×–—]+(?<![ \\-_/+&,:;'\"\\.])$")
    @Column (name = "product_name")
    private String productName;


    @Column (name = "price")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "1", message = "Price must be at least 1 VND")
    @DecimalMax(value = "500000000", message = "Price must be at most 500,000,000 VND")
    private double price;


    @Column (name = "status")
    private boolean status = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_status", nullable = false, length = 30)
    private ProductLifecycleStatus lifecycleStatus = ProductLifecycleStatus.SELLING;

    @Column (name = "description")
    private String description;

    @Column (name = "specification")
    private String specification;

    @Column (name = "created_at")
    private Date createAt;

    @Column (name = "inventory_quantity")
    private Integer inventoryQuantity;

    @Column(name = "performance_score")
    @Min(value = 0, message = "Performance score must be at least 0")
    @Max(value = 100, message = "Performance score must be at most 100")
    private Integer performanceScore = 50; // Default score

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @OrderBy("displayOrder ASC")
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images;

    public boolean isVisibleOnStorefront() {
        return status;
    }

    public boolean isSelling() {
        return lifecycleStatus == ProductLifecycleStatus.SELLING;
    }

    public boolean isSellableOnStorefront() {
        return isVisibleOnStorefront() && isSelling();
    }

}
