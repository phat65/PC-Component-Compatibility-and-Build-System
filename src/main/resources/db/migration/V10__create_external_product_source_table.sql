CREATE TABLE external_product_source (
    external_product_source_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    provider_code VARCHAR(50) NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    sku VARCHAR(255),
    currency VARCHAR(10),
    product_url VARCHAR(500),
    CONSTRAINT fk_external_product_source_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    CONSTRAINT uk_external_product_source_provider_external_id
        UNIQUE (provider_code, external_id),
    INDEX idx_external_product_source_product_id (product_id)
);
