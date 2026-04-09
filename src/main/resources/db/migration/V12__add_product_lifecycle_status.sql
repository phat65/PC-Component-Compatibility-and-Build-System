ALTER TABLE product
ADD COLUMN lifecycle_status VARCHAR(30) NOT NULL DEFAULT 'SELLING' AFTER status;

UPDATE product
SET lifecycle_status = CASE
    WHEN status = b'1' THEN 'SELLING'
    WHEN LOWER(product_name) LIKE '%[archived-%' THEN 'DISCONTINUED'
    ELSE 'DRAFT'
END;
