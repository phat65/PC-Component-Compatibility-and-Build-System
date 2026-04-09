CREATE TABLE brand (
    brand_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    website VARCHAR(255),
    status BIT DEFAULT 1
);

CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    display_order INT,
    created_at DATE DEFAULT (CURRENT_DATE)
);

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    brand_id INT,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status BIT DEFAULT 1,
    description VARCHAR(500),
    specification VARCHAR(500),
    created_at DATE DEFAULT (CURRENT_DATE),
    inventory_quantity INT DEFAULT 0,
    performance_score INT DEFAULT 50 CHECK (performance_score BETWEEN 0 AND 100),
    FOREIGN KEY (brand_id) REFERENCES brand(brand_id)
);

-- Many-to-many product-category (after product & category exist)
CREATE TABLE product_category (
    product_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_category_id (category_id)
);

-- ==============================================
-- Component tables (use product_id as PK that references product)
-- ==============================================
CREATE TABLE cpu
(
    product_id       INT PRIMARY KEY,
    socket           VARCHAR(50),
    tdp              INT,
    max_memory_speed INT,
    memory_channels  INT,
    pcie_version     VARCHAR(20),
    has_igpu         BIT,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE gpu
(
    product_id    INT PRIMARY KEY,
    vram          INT,
    memory_type   VARCHAR(50),
    tdp           INT,
    gpu_interface VARCHAR(50),
    pcie_version  VARCHAR(20),
    length        INT,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE mainboard
(
    product_id       INT PRIMARY KEY,
    socket           VARCHAR(50),
    chipset          VARCHAR(50),
    form_factor      VARCHAR(50),
    memory_slots     INT,
    max_memory_speed INT,
    memory_type      VARCHAR(50),
    pcie_version     VARCHAR(20),
    m2_slots         INT,
    sata_ports       INT,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE memory
(
    product_id INT PRIMARY KEY,
    type       VARCHAR(50),
    capacity   INT,
    speed      INT,
    tdp        INT,
    modules    INT DEFAULT 1,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE storage
(
    product_id  INT PRIMARY KEY,
    type        VARCHAR(50),
    capacity    INT,
    interface   VARCHAR(50),
    read_speed  INT,
    write_speed INT,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE pc_case
(
    product_id            INT PRIMARY KEY,
    form_factor           VARCHAR(50),
    gpu_max_length        INT,
    cpu_max_cooler_height INT,
    psu_form_factor       VARCHAR(20),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE power_supply
(
    product_id  INT PRIMARY KEY,
    wattage     INT,
    efficiency  VARCHAR(50),
    modular     BIT,
    form_factor VARCHAR(20),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE cooling
(
    product_id    INT PRIMARY KEY,
    type          VARCHAR(50),
    max_tdp       INT,
    fan_size      INT,
    radiator_size INT,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE image (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255),
    created_at DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);