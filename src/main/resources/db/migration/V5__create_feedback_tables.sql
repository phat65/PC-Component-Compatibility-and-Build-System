CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    product_id INT NOT NULL,
    comment VARCHAR(500),
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment_status VARCHAR(50),
    reply VARCHAR(500) NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account(account_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
