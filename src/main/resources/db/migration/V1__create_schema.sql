CREATE TABLE account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'Customer',
    email VARCHAR(100),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    gender BOOLEAN,
    address VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE account_address (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_account_address_account
        FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

CREATE TABLE brand (
    brand_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    website VARCHAR(255),
    status BOOLEAN DEFAULT TRUE,
    CONSTRAINT uk_brand_name UNIQUE (name)
);

CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    parent_id INT NULL,
    category_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    created_at DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT uk_category_name UNIQUE (category_name),
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id) REFERENCES category(category_id) ON DELETE SET NULL
);

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    brand_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    lifecycle_status VARCHAR(30) NOT NULL DEFAULT 'SELLING',
    description VARCHAR(500),
    specification VARCHAR(500),
    created_at DATE DEFAULT (CURRENT_DATE),
    inventory_quantity INT DEFAULT 0,
    performance_score INT DEFAULT 50,
    CONSTRAINT chk_product_performance_score CHECK (performance_score BETWEEN 0 AND 100),
    CONSTRAINT chk_product_lifecycle_status CHECK (lifecycle_status IN ('DRAFT', 'SELLING', 'DISCONTINUED')),
    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id) REFERENCES brand(brand_id)
);

CREATE TABLE product_category (
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product_category_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_category_category
        FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE,
    INDEX idx_product_category_product_id (product_id),
    INDEX idx_product_category_category_id (category_id)
);

CREATE TABLE cpu (
    product_id INT PRIMARY KEY,
    socket VARCHAR(50),
    tdp INT,
    max_memory_speed INT,
    memory_channels INT,
    pcie_version VARCHAR(20),
    has_igpu BOOLEAN,
    CONSTRAINT fk_cpu_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE gpu (
    product_id INT PRIMARY KEY,
    vram INT,
    memory_type VARCHAR(50),
    tdp INT,
    gpu_interface VARCHAR(50),
    pcie_version VARCHAR(20),
    length INT,
    CONSTRAINT fk_gpu_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE mainboard (
    product_id INT PRIMARY KEY,
    socket VARCHAR(50),
    chipset VARCHAR(50),
    form_factor VARCHAR(50),
    memory_slots INT,
    max_memory_speed INT,
    memory_type VARCHAR(50),
    pcie_version VARCHAR(20),
    m2_slots INT,
    sata_ports INT,
    CONSTRAINT fk_mainboard_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE memory (
    product_id INT PRIMARY KEY,
    type VARCHAR(50),
    capacity INT,
    speed INT,
    tdp INT,
    modules INT DEFAULT 1,
    CONSTRAINT fk_memory_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE storage (
    product_id INT PRIMARY KEY,
    type VARCHAR(50),
    capacity INT,
    interface VARCHAR(50),
    read_speed INT,
    write_speed INT,
    CONSTRAINT fk_storage_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE pc_case (
    product_id INT PRIMARY KEY,
    form_factor VARCHAR(50),
    gpu_max_length INT,
    cpu_max_cooler_height INT,
    psu_form_factor VARCHAR(20),
    CONSTRAINT fk_case_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE power_supply (
    product_id INT PRIMARY KEY,
    wattage INT,
    efficiency VARCHAR(50),
    modular BOOLEAN,
    form_factor VARCHAR(20),
    CONSTRAINT fk_power_supply_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE cooling (
    product_id INT PRIMARY KEY,
    type VARCHAR(50),
    max_tdp INT,
    fan_size INT,
    radiator_size INT,
    CONSTRAINT fk_cooling_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE image (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255),
    created_at DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_image_product
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

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

CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    status VARCHAR(50),
    updated_date DATE,
    CONSTRAINT fk_cart_account
        FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE,
    CONSTRAINT uk_cart_account UNIQUE (account_id)
);

CREATE TABLE cart_item (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    is_selected BOOLEAN NOT NULL DEFAULT TRUE,
    is_build_item BOOLEAN NOT NULL DEFAULT FALSE,
    build_id VARCHAR(50) NULL,
    CONSTRAINT fk_cart_item_cart
        FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product
        FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    final_amount DECIMAL(15,2),
    status VARCHAR(50),
    created_date DATE DEFAULT (CURRENT_DATE),
    shipping_method VARCHAR(50) NOT NULL,
    note TEXT NULL,
    shipping_full_name VARCHAR(100) NOT NULL,
    shipping_phone VARCHAR(20) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    ready_to_ship_date DATETIME NULL,
    shipment_received_date DATETIME NULL,
    payment_id VARCHAR(255) NULL,
    payment_status VARCHAR(50) NULL,
    paid_at DATETIME NULL,
    CONSTRAINT fk_orders_account
        FOREIGN KEY (account_id) REFERENCES account(account_id)
);

CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    gateway_payment_id VARCHAR(255),
    amount DECIMAL(15,2),
    currency VARCHAR(10) DEFAULT 'VND',
    status VARCHAR(50),
    raw_payload TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    order_code BIGINT NULL UNIQUE,
    CONSTRAINT fk_payments_orders
        FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

CREATE TABLE order_detail (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_order_detail_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_detail_product
        FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    product_id INT NOT NULL,
    comment VARCHAR(500),
    rating INT,
    comment_status VARCHAR(50),
    reply VARCHAR(500) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_feedback_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_feedback_account
        FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT fk_feedback_product
        FOREIGN KEY (product_id) REFERENCES product(product_id)
);
