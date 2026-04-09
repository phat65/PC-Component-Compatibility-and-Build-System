-- ==============================================
-- Cart
-- ==============================================
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    status VARCHAR(50),
    updated_date DATE,
    FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);

CREATE TABLE cart_item (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- ==============================================
-- Orders and Payments (orders uses BIGINT)
-- ==============================================
CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    final_amount DECIMAL(10,2),
    status VARCHAR(50),
    created_date DATE DEFAULT (CURRENT_DATE),
    shipping_method VARCHAR(50) NOT NULL,
    note TEXT NULL,
    shipping_full_name VARCHAR(100) NOT NULL,
    shipping_phone VARCHAR(20) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    ready_to_ship_date DATETIME NULL COMMENT 'Timestamp when status became Ready to Ship',
    shipment_received_date DATETIME NULL COMMENT 'Thời điểm shipper chuyển trạng thái sang Delivering',
    payment_id VARCHAR(255) NULL,
    payment_status VARCHAR(50) NULL,
    paid_at DATETIME NULL,
    FOREIGN KEY (account_id) REFERENCES account(account_id)
);

CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    gateway_payment_id VARCHAR(255),
    amount DECIMAL(10,2),
    currency VARCHAR(10) DEFAULT 'VND',
    status VARCHAR(50),
    raw_payload TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    order_code BIGINT NULL UNIQUE,
    CONSTRAINT fk_payments_orders FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- ==============================================
-- Order details, Feedback
-- ==============================================
CREATE TABLE order_detail (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

ALTER TABLE cart_item
ADD COLUMN is_selected BIT DEFAULT 1 NOT NULL,
ADD COLUMN is_build_item BIT DEFAULT 0 NOT NULL,
ADD COLUMN build_id VARCHAR(50) NULL ;
