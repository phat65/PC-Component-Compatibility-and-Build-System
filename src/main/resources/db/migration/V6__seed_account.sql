-- Shared BCrypt hash for sample seeded users: pass123
-- Default login accounts:
--   Admin    : 0999999999 / admin123
--   Staff    : 0888888888 / staff123
--   Customer : 0777777777 / cus123

INSERT INTO account (phone_number, password, role, email, first_name, last_name, gender, address, enabled)
VALUES
('0900000001', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user1@example.com', 'Nguyen', 'An', 1, NULL, 1),
('0900000002', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user2@example.com', 'Tran', 'Binh', 0, NULL, 1),
('0900000003', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user3@example.com', 'Le', 'Cuong', 1, NULL, 1),
('0900000004', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user4@example.com', 'Pham', 'Dung', 0, NULL, 1),
('0900000005', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user5@example.com', 'Hoang', 'Em', 1, NULL, 1),
('0900000006', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user6@example.com', 'Do', 'Phong', 1, NULL, 1),
('0900000007', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user7@example.com', 'Vu', 'Lam', 0, NULL, 1),
('0900000008', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user8@example.com', 'Dang', 'Hieu', 1, NULL, 1),
('0900000009', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user9@example.com', 'Bui', 'Phuc', 0, NULL, 1),
('0900000010', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user10@example.com', 'Cao', 'Minh', 1, NULL, 1),

('0900000011', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user11@example.com', 'Nguyen', 'Khanh', 1, NULL, 1),
('0900000012', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user12@example.com', 'Tran', 'Quang', 0, NULL, 1),
('0900000013', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user13@example.com', 'Le', 'Huy', 1, NULL, 1),
('0900000014', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user14@example.com', 'Pham', 'Tuan', 0, NULL, 1),
('0900000015', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user15@example.com', 'Hoang', 'Bao', 1, NULL, 1),
('0900000016', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user16@example.com', 'Do', 'Son', 1, NULL, 1),
('0900000017', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user17@example.com', 'Vu', 'Tien', 0, NULL, 1),
('0900000018', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user18@example.com', 'Dang', 'Long', 1, NULL, 1),
('0900000019', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user19@example.com', 'Bui', 'Nam', 0, NULL, 1),
('0900000020', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user20@example.com', 'Cao', 'Hai', 1, NULL, 1),

('0900000021', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user21@example.com', 'Nguyen', 'Van', 1, NULL, 1),
('0900000022', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user22@example.com', 'Tran', 'Phong', 0, NULL, 1),
('0900000023', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user23@example.com', 'Le', 'Hung', 1, NULL, 1),
('0900000024', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user24@example.com', 'Pham', 'Son', 0, NULL, 1),
('0900000025', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user25@example.com', 'Hoang', 'Tu', 1, NULL, 1),
('0900000026', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user26@example.com', 'Do', 'Trung', 1, NULL, 1),
('0900000027', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user27@example.com', 'Vu', 'Khoa', 0, NULL, 1),
('0900000028', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user28@example.com', 'Dang', 'Luc', 1, NULL, 1),
('0900000029', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Customer', 'user29@example.com', 'Bui', 'Dat', 0, NULL, 1),
('0900000030', '$2a$10$tf5jN1DG.PfzxRhIenmP7OL.FSaxrd2TfqxlkDKiRsYrQcYyTOTDy', 'Staff', 'user30@example.com', 'Cao', 'Manh', 1, NULL, 1),
('0999999999', '$2a$10$y9zPOi3sD4pSfsqLIxwyE.LgVoc8psDEqY1Y5wADzsln.mPm/L3US', 'Admin', 'admin@shop.com', 'System', 'Admin', 1, NULL, 1),
('0888888888', '$2a$10$sITODd4nhaZQWwFrQo6gOOar9U/eQSveLx9faUb3khy.7OS6RaMiq', 'Staff', 'staff@shop.com', 'Default', 'Staff', 1, NULL, 1),
('0777777777', '$2a$10$m6Sd9JesmN2LN25LhOOn..uJP0O5vDOMlstHCICFz4Hk0aULu4D.q', 'Customer', 'customer@shop.com', 'Default', 'Customer', 0, NULL, 1);

INSERT INTO account_address (account_id, full_name, phone, address, is_default) VALUES
(1, 'Nguyen An', '0900000001', 'Phường Hoàn Kiếm, Quận Hoàn Kiếm, Hà Nội, Việt Nam', 1),
(2, 'Tran Binh', '0900000002', 'Phường 1, Quận 1, TP Hồ Chí Minh, Việt Nam', 1),
(3, 'Le Cuong', '0900000003', 'Phường Hải Châu, Quận Hải Châu, Đà Nẵng, Việt Nam', 1),
(4, 'Pham Dung', '0900000004', 'Phường Máy Chai, Quận Ngô Quyền, Hải Phòng, Việt Nam', 1),
(5, 'Hoang Em', '0900000005', 'Phường Hưng Lợi, Quận Ninh Kiều, Cần Thơ, Việt Nam', 1),
(6, 'Do Phong', '0900000006', 'Phường Phú Hội, TP Huế, Thừa Thiên Huế, Việt Nam', 1),
(7, 'Vu Lam', '0900000007', 'Phường Bãi Cháy, TP Hạ Long, Quảng Ninh, Việt Nam', 1),
(8, 'Dang Hieu', '0900000008', 'Phường Trường Thi, TP Thanh Hóa, Thanh Hóa, Việt Nam', 1),
(9, 'Bui Phuc', '0900000009', 'Phường Quang Trung, TP Vinh, Nghệ An, Việt Nam', 1),
(10, 'Cao Minh', '0900000010', 'Phường Hòa Thọ Đông, Quận Cẩm Lệ, Đà Nẵng, Việt Nam', 1),

(11, 'Nguyen Khanh', '0900000011', 'Phường Cổ Nhuế, Quận Bắc Từ Liêm, Hà Nội, Việt Nam', 1),
(12, 'Tran Quang', '0900000012', 'Phường Bình Thạnh, Quận Bình Thạnh, TP HCM, Việt Nam', 1),
(13, 'Le Huy', '0900000013', 'Phường Thanh Khê, Quận Thanh Khê, Đà Nẵng, Việt Nam', 1),
(14, 'Pham Tuan', '0900000014', 'Phường Tứ Minh, TP Hải Dương, Hải Dương, Việt Nam', 1),
(15, 'Hoang Bao', '0900000015', 'Phường Lê Hồng Phong, TP Thái Bình, Thái Bình, Việt Nam', 1),
(16, 'Do Son', '0900000016', 'Phường Lương Khánh Thiện, TP Phủ Lý, Hà Nam, Việt Nam', 1),
(17, 'Vu Tien', '0900000017', 'Phường Nam Bình, TP Ninh Bình, Ninh Bình, Việt Nam', 1),
(18, 'Dang Long', '0900000018', 'Phường Đồng Tiến, TP Hòa Bình, Hòa Bình, Việt Nam', 1),
(19, 'Bui Nam', '0900000019', 'Phường Tam Thanh, TP Lạng Sơn, Lạng Sơn, Việt Nam', 1),
(20, 'Cao Hai', '0900000020', 'Phường Đại Phúc, TP Bắc Ninh, Bắc Ninh, Việt Nam', 1),

(21, 'Nguyen Van', '0900000021', 'Phường Văn Chương, Quận Đống Đa, Hà Nội, Việt Nam', 1),
(22, 'Tran Phong', '0900000022', 'Phường Bình Khánh, Quận 2, TP HCM, Việt Nam', 1),
(23, 'Le Hung', '0900000023', 'Phường An Hải Bắc, Quận Sơn Trà, Đà Nẵng, Việt Nam', 1),
(24, 'Pham Son', '0900000024', 'Phường Đằng Lâm, Quận Hải An, Hải Phòng, Việt Nam', 1),
(25, 'Hoang Tu', '0900000025', 'Phường An Nghiệp, Quận Ninh Kiều, Cần Thơ, Việt Nam', 1),
(26, 'Do Trung', '0900000026', 'Phường Phước Vĩnh, TP Huế, Thừa Thiên Huế, Việt Nam', 1),
(27, 'Vu Khoa', '0900000027', 'Phường Hồng Gai, TP Hạ Long, Quảng Ninh, Việt Nam', 1),
(28, 'Dang Luc', '0900000028', 'Phường Ba Đình, TP Thanh Hóa, Thanh Hóa, Việt Nam', 1),
(29, 'Bui Dat', '0900000029', 'Phường Cửa Nam, TP Vinh, Nghệ An, Việt Nam', 1),
(30, 'Cao Manh', '0900000030', 'Phường Trần Hưng Đạo, TP Nam Định, Nam Định, Việt Nam', 1);
