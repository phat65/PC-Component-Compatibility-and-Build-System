ALTER TABLE product MODIFY price DECIMAL(15,0) NOT NULL;
ALTER TABLE orders MODIFY final_amount DECIMAL(15,0);
ALTER TABLE payments MODIFY amount DECIMAL(15,0);
ALTER TABLE order_detail MODIFY price DECIMAL(15,0) NOT NULL;

UPDATE product
SET price = ROUND(price * 25000, 0)
WHERE price > 0 AND price < 10000;

UPDATE order_detail
SET price = ROUND(price * 25000, 0)
WHERE price > 0 AND price < 10000;

UPDATE orders
SET final_amount = ROUND(final_amount * 25000, 0)
WHERE final_amount > 0 AND final_amount < 10000;

UPDATE payments
SET amount = ROUND(amount * 25000, 0)
WHERE amount > 0 AND amount < 10000;

UPDATE payments
SET currency = 'VND'
WHERE currency IS NULL OR currency = '';
