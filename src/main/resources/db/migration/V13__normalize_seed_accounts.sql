-- Normalize legacy plain-text seeded passwords to BCrypt.
UPDATE account
SET password = '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy'
WHERE password = 'pass123';

-- Ensure default login accounts exist in Flyway-managed seed data.
INSERT INTO account (phone_number, password, role, email, first_name, last_name, gender, address, enabled)
VALUES ('0999999999', '$2a$10$y9zPOi3sD4pSfsqLIxwyE.LgVoc8psDEqY1Y5wADzsln.mPm/L3US', 'Admin', 'admin@shop.com', 'System', 'Admin', 1, NULL, 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    role = VALUES(role),
    email = VALUES(email),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    gender = VALUES(gender),
    enabled = VALUES(enabled);

INSERT INTO account (phone_number, password, role, email, first_name, last_name, gender, address, enabled)
VALUES ('0888888888', '$2a$10$sITODd4nhaZQWwFrQo6gOOar9U/eQSveLx9faUb3khy.7OS6RaMiq', 'Staff', 'staff@shop.com', 'Default', 'Staff', 1, NULL, 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    role = VALUES(role),
    email = VALUES(email),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    gender = VALUES(gender),
    enabled = VALUES(enabled);

INSERT INTO account (phone_number, password, role, email, first_name, last_name, gender, address, enabled)
VALUES ('0777777777', '$2a$10$m6Sd9JesmN2LN25LhOOn..uJP0O5vDOMlstHCICFz4Hk0aULu4D.q', 'Customer', 'customer@shop.com', 'Default', 'Customer', 0, NULL, 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    role = VALUES(role),
    email = VALUES(email),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    gender = VALUES(gender),
    enabled = VALUES(enabled);

-- Ensure each default login account has one address row.
INSERT INTO account_address (account_id, full_name, phone, address, is_default)
SELECT a.account_id, 'System Admin', '0999999999', 'Ha Noi, Viet Nam', 1
FROM account a
WHERE a.phone_number = '0999999999'
  AND NOT EXISTS (
      SELECT 1
      FROM account_address aa
      WHERE aa.account_id = a.account_id
  );

INSERT INTO account_address (account_id, full_name, phone, address, is_default)
SELECT a.account_id, 'Default Staff', '0888888888', 'TP Ho Chi Minh, Viet Nam', 1
FROM account a
WHERE a.phone_number = '0888888888'
  AND NOT EXISTS (
      SELECT 1
      FROM account_address aa
      WHERE aa.account_id = a.account_id
  );

INSERT INTO account_address (account_id, full_name, phone, address, is_default)
SELECT a.account_id, 'Default Customer', '0777777777', 'Da Nang, Viet Nam', 1
FROM account a
WHERE a.phone_number = '0777777777'
  AND NOT EXISTS (
      SELECT 1
      FROM account_address aa
      WHERE aa.account_id = a.account_id
  );
