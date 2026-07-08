-- Default login accounts:
-- Admin    : 0999999999 / admin123
-- Staff    : 0888888888 / staff123
-- Customer : 0777777777 / cus123

INSERT INTO account (phone_number, password, role, email, first_name, last_name, gender, address, enabled)
VALUES
('0999999999', '$2a$10$y9zPOi3sD4pSfsqLIxwyE.LgVoc8psDEqY1Y5wADzsln.mPm/L3US', 'Admin', 'admin@shop.com', 'System', 'Admin', TRUE, NULL, TRUE),
('0888888888', '$2a$10$sITODd4nhaZQWwFrQo6gOOar9U/eQSveLx9faUb3khy.7OS6RaMiq', 'Staff', 'staff@shop.com', 'Default', 'Staff', TRUE, NULL, TRUE),
('0777777777', '$2a$10$m6Sd9JesmN2LN25LhOOn..uJP0O5vDOMlstHCICFz4Hk0aULu4D.q', 'Customer', 'customer@shop.com', 'Default', 'Customer', FALSE, NULL, TRUE),
('0900000001', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'nguyen.an@example.com', 'Nguyen', 'An', TRUE, NULL, TRUE),
('0900000002', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'tran.binh@example.com', 'Tran', 'Binh', FALSE, NULL, TRUE),
('0900000003', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'le.cuong@example.com', 'Le', 'Cuong', TRUE, NULL, TRUE);

INSERT INTO account_address (account_id, full_name, phone, address, is_default)
SELECT account_id, 'Default Customer', phone_number, 'Ha Noi, Viet Nam', TRUE
FROM account
WHERE phone_number IN ('0777777777', '0900000001', '0900000002', '0900000003');
