CREATE TABLE account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'Customer',
    email VARCHAR(100),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    gender BIT,
    address VARCHAR(255),
    enabled BIT DEFAULT 1
);
CREATE TABLE account_address (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    is_default BIT DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES account(account_id) ON DELETE CASCADE
);