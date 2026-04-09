package com.example.PCOnlineShop.model.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "external_product_source",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_external_product_source_provider_external_id",
                columnNames = {"provider_code", "external_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalProductSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_product_source_id")
    private Integer externalProductSourceId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "provider_code", nullable = false, length = 50)
    private String providerCode;

    @Column(name = "external_id", nullable = false, length = 255)
    private String externalId;

    @Column(name = "sku", length = 255)
    private String sku;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "product_url", length = 500)
    private String productUrl;
}
