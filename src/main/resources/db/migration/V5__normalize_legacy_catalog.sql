-- Normalize legacy catalog prices, image gaps, and brand mappings.

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

-- Align seeded VND component prices with a current Vietnamese retail baseline.
-- This keeps build suggestion realistic after V17 converted legacy USD-like prices.

UPDATE product
SET price = CASE product_name
    WHEN 'ASUS PRIME B550M-A' THEN 2300000
    WHEN 'MSI MPG X670E CARBON' THEN 9800000
    WHEN 'GIGABYTE B760M DS3H DDR4' THEN 2400000
    WHEN 'ASUS ROG STRIX Z790-E GAMING' THEN 10500000
    WHEN 'MSI PRO H610M-E DDR4' THEN 1900000
    WHEN 'GIGABYTE X570 AORUS ELITE' THEN 4200000
    WHEN 'ASUS TUF GAMING B550-PLUS' THEN 2500000
    WHEN 'MSI B450 TOMAHAWK MAX II' THEN 2300000
    WHEN 'GIGABYTE B650 AORUS ELITE' THEN 5200000
    WHEN 'ASUS PRIME H610M-K D4' THEN 1900000
    WHEN 'MSI MAG B550 TOMAHAWK' THEN 3000000
    WHEN 'GIGABYTE Z790 AORUS MASTER' THEN 12000000
    WHEN 'ASUS ROG CROSSHAIR X670E HERO' THEN 13500000
    WHEN 'MSI MPG B650M EDGE WIFI' THEN 5000000
    WHEN 'GIGABYTE A520M S2H' THEN 1550000
    WHEN 'ASUS PRIME B450M-K II' THEN 1800000
    WHEN 'MSI Z590 PRO WIFI' THEN 4200000
    WHEN 'GIGABYTE B365M DS3H' THEN 1600000
    WHEN 'ASUS PRIME X570-P' THEN 3800000
    WHEN 'MSI B650 TOMAHAWK WIFI' THEN 5200000
    WHEN 'GIGABYTE X399 AORUS XTREME' THEN 9000000
    WHEN 'ASUS ROG STRIX B650E-F GAMING WIFI' THEN 6500000
    WHEN 'MSI PRO X670-P WIFI' THEN 5900000
    WHEN 'GIGABYTE B550I AORUS PRO AX' THEN 4500000
    WHEN 'ASUS Prime H310M-K R2.0' THEN 900000
    WHEN 'MSI A320M-A Pro' THEN 950000
    WHEN 'MSI H310M PRO-VDH PLUS' THEN 900000
    WHEN 'ASRock A320M-HDV R4.0' THEN 950000
    WHEN 'ASUS PRIME H310M-E R2.0' THEN 900000
    WHEN 'MSI H310M PRO-M2 PLUS' THEN 900000
    ELSE price
END
WHERE product_name IN (
    'ASUS PRIME B550M-A', 'MSI MPG X670E CARBON', 'GIGABYTE B760M DS3H DDR4',
    'ASUS ROG STRIX Z790-E GAMING', 'MSI PRO H610M-E DDR4', 'GIGABYTE X570 AORUS ELITE',
    'ASUS TUF GAMING B550-PLUS', 'MSI B450 TOMAHAWK MAX II', 'GIGABYTE B650 AORUS ELITE',
    'ASUS PRIME H610M-K D4', 'MSI MAG B550 TOMAHAWK', 'GIGABYTE Z790 AORUS MASTER',
    'ASUS ROG CROSSHAIR X670E HERO', 'MSI MPG B650M EDGE WIFI', 'GIGABYTE A520M S2H',
    'ASUS PRIME B450M-K II', 'MSI Z590 PRO WIFI', 'GIGABYTE B365M DS3H',
    'ASUS PRIME X570-P', 'MSI B650 TOMAHAWK WIFI', 'GIGABYTE X399 AORUS XTREME',
    'ASUS ROG STRIX B650E-F GAMING WIFI', 'MSI PRO X670-P WIFI', 'GIGABYTE B550I AORUS PRO AX',
    'ASUS Prime H310M-K R2.0', 'MSI A320M-A Pro', 'MSI H310M PRO-VDH PLUS',
    'ASRock A320M-HDV R4.0', 'ASUS PRIME H310M-E R2.0', 'MSI H310M PRO-M2 PLUS'
);

UPDATE product
SET price = CASE product_name
    WHEN 'AMD Ryzen 5 5600X' THEN 3200000
    WHEN 'Intel Core i5-12400F' THEN 3600000
    WHEN 'Intel Core i7-13700K' THEN 8500000
    WHEN 'AMD Ryzen 9 7950X' THEN 13000000
    WHEN 'AMD Ryzen 7 5800X3D' THEN 7200000
    WHEN 'Intel Core i9-13900K' THEN 12000000
    WHEN 'AMD Ryzen 5 7600' THEN 4500000
    WHEN 'Intel Core i3-12100F' THEN 2200000
    WHEN 'AMD Ryzen 7 7700' THEN 6500000
    WHEN 'Intel Core i5-13600K' THEN 6500000
    WHEN 'AMD Ryzen 9 5950X' THEN 9500000
    WHEN 'Intel Core i9-12900KS' THEN 9800000
    WHEN 'AMD Ryzen 3 4100' THEN 1500000
    WHEN 'Intel Core i5-14600K' THEN 7500000
    WHEN 'AMD Ryzen 5 5500' THEN 2000000
    WHEN 'AMD Ryzen 9 7900X' THEN 9000000
    WHEN 'AMD Ryzen 7 7800X3D' THEN 9500000
    WHEN 'Intel Core i9-14900K' THEN 14000000
    WHEN 'Intel Core i7-12700F' THEN 5900000
    WHEN 'Intel Core i5-12400' THEN 3900000
    WHEN 'Intel Core i3-13100' THEN 3000000
    WHEN 'AMD Ryzen 5 4600G' THEN 2400000
    WHEN 'AMD Ryzen 7 5700G' THEN 4300000
    WHEN 'AMD Ryzen 3 5300G' THEN 2300000
    WHEN 'Intel Core i5-11400F' THEN 3000000
    WHEN 'Intel Core i7-11700K' THEN 5800000
    WHEN 'AMD Ryzen 9 5900X' THEN 7600000
    WHEN 'AMD Ryzen 5 7500F' THEN 3900000
    WHEN 'Intel Core i9-11900K' THEN 6500000
    WHEN 'AMD Ryzen 3 3200G' THEN 1900000
    ELSE price
END
WHERE product_name IN (
    'AMD Ryzen 5 5600X', 'Intel Core i5-12400F', 'Intel Core i7-13700K',
    'AMD Ryzen 9 7950X', 'AMD Ryzen 7 5800X3D', 'Intel Core i9-13900K',
    'AMD Ryzen 5 7600', 'Intel Core i3-12100F', 'AMD Ryzen 7 7700',
    'Intel Core i5-13600K', 'AMD Ryzen 9 5950X', 'Intel Core i9-12900KS',
    'AMD Ryzen 3 4100', 'Intel Core i5-14600K', 'AMD Ryzen 5 5500',
    'AMD Ryzen 9 7900X', 'AMD Ryzen 7 7800X3D', 'Intel Core i9-14900K',
    'Intel Core i7-12700F', 'Intel Core i5-12400', 'Intel Core i3-13100',
    'AMD Ryzen 5 4600G', 'AMD Ryzen 7 5700G', 'AMD Ryzen 3 5300G',
    'Intel Core i5-11400F', 'Intel Core i7-11700K', 'AMD Ryzen 9 5900X',
    'AMD Ryzen 5 7500F', 'Intel Core i9-11900K', 'AMD Ryzen 3 3200G'
);

UPDATE product
SET price = CASE product_name
    WHEN 'NVIDIA GeForce RTX 3060' THEN 7300000
    WHEN 'AMD Radeon RX 6600 XT' THEN 5800000
    WHEN 'NVIDIA GeForce RTX 4070' THEN 14500000
    WHEN 'AMD Radeon RX 7900 XT' THEN 20000000
    WHEN 'NVIDIA GeForce RTX 4080' THEN 28000000
    WHEN 'AMD Radeon RX 6800' THEN 9500000
    WHEN 'NVIDIA GeForce RTX 3050' THEN 5300000
    WHEN 'AMD Radeon RX 6500 XT' THEN 4400000
    WHEN 'NVIDIA GeForce RTX 4070 Ti' THEN 19500000
    WHEN 'AMD Radeon RX 7800 XT' THEN 13000000
    WHEN 'NVIDIA GeForce RTX 4060' THEN 8200000
    WHEN 'AMD Radeon RX 6700 XT' THEN 7500000
    WHEN 'NVIDIA GeForce GTX 1660 SUPER' THEN 4200000
    WHEN 'NVIDIA GeForce RTX 4090' THEN 47000000
    WHEN 'AMD Radeon RX 7600' THEN 6500000
    WHEN 'NVIDIA GeForce RTX 4060 Ti' THEN 10500000
    WHEN 'NVIDIA GeForce RTX 2060' THEN 4500000
    WHEN 'NVIDIA GeForce GTX 1650 SUPER' THEN 3500000
    WHEN 'AMD Radeon RX 5500 XT' THEN 3600000
    WHEN 'NVIDIA GeForce RTX 2080 Ti' THEN 8500000
    WHEN 'NVIDIA GeForce RTX 4060 SUPER' THEN 9500000
    WHEN 'AMD Radeon RX 6400' THEN 2500000
    WHEN 'NVIDIA GeForce RTX 3070' THEN 9000000
    WHEN 'NVIDIA GeForce RTX 3090' THEN 16000000
    WHEN 'NVIDIA GeForce RTX 4070 SUPER' THEN 15900000
    WHEN 'AMD Radeon RX 7700 XT' THEN 11000000
    ELSE price
END
WHERE product_name IN (
    'NVIDIA GeForce RTX 3060', 'AMD Radeon RX 6600 XT', 'NVIDIA GeForce RTX 4070',
    'AMD Radeon RX 7900 XT', 'NVIDIA GeForce RTX 4080', 'AMD Radeon RX 6800',
    'NVIDIA GeForce RTX 3050', 'AMD Radeon RX 6500 XT', 'NVIDIA GeForce RTX 4070 Ti',
    'AMD Radeon RX 7800 XT', 'NVIDIA GeForce RTX 4060', 'AMD Radeon RX 6700 XT',
    'NVIDIA GeForce GTX 1660 SUPER', 'NVIDIA GeForce RTX 4090', 'AMD Radeon RX 7600',
    'NVIDIA GeForce RTX 4060 Ti', 'NVIDIA GeForce RTX 2060', 'NVIDIA GeForce GTX 1650 SUPER',
    'AMD Radeon RX 5500 XT', 'NVIDIA GeForce RTX 2080 Ti', 'NVIDIA GeForce RTX 4060 SUPER',
    'AMD Radeon RX 6400', 'NVIDIA GeForce RTX 3070', 'NVIDIA GeForce RTX 3090',
    'NVIDIA GeForce RTX 4070 SUPER', 'AMD Radeon RX 7700 XT'
);

UPDATE product
SET price = CASE product_name
    WHEN 'Corsair Vengeance LPX 16GB (2x8GB) DDR4 3200MHz C16' THEN 1200000
    WHEN 'Kingston Fury Beast 8GB DDR4 3200MHz' THEN 570000
    WHEN 'Teamgroup T-Force Vulcan Z 16GB (2x8GB) DDR4 3200MHz' THEN 1050000
    WHEN 'Kingston Fury Beast 32GB (2x16GB) DDR4 3200MHz' THEN 2100000
    WHEN 'Corsair Vengeance 32GB (2x16GB) DDR5 5600MHz C36' THEN 2900000
    WHEN 'Kingston Fury Beast 16GB (2x8GB) DDR5 5200MHz' THEN 1400000
    WHEN 'Crucial 16GB DDR5 4800MHz' THEN 1200000
    WHEN 'Crucial 8GB DDR4 2666MHz' THEN 390000
    WHEN 'Kingston ValueRAM 8GB DDR4 2400MHz' THEN 350000
    WHEN 'Corsair Vengeance LPX 8GB DDR4 3000MHz' THEN 500000
    WHEN 'G.Skill Aegis 16GB (2x8GB) DDR4 3200MHz' THEN 950000
    WHEN 'Crucial Ballistix 16GB (2x8GB) DDR4 3000MHz' THEN 1000000
    ELSE price
END
WHERE product_name IN (
    'Corsair Vengeance LPX 16GB (2x8GB) DDR4 3200MHz C16',
    'Kingston Fury Beast 8GB DDR4 3200MHz',
    'Teamgroup T-Force Vulcan Z 16GB (2x8GB) DDR4 3200MHz',
    'Kingston Fury Beast 32GB (2x16GB) DDR4 3200MHz',
    'Corsair Vengeance 32GB (2x16GB) DDR5 5600MHz C36',
    'Kingston Fury Beast 16GB (2x8GB) DDR5 5200MHz',
    'Crucial 16GB DDR5 4800MHz',
    'Crucial 8GB DDR4 2666MHz',
    'Kingston ValueRAM 8GB DDR4 2400MHz',
    'Corsair Vengeance LPX 8GB DDR4 3000MHz',
    'G.Skill Aegis 16GB (2x8GB) DDR4 3200MHz',
    'Crucial Ballistix 16GB (2x8GB) DDR4 3000MHz'
);

UPDATE product
SET price = CASE product_name
    WHEN 'Samsung 980 Pro 1TB PCIe 4.0 NVMe' THEN 2200000
    WHEN 'WD Black SN850X 1TB PCIe 4.0 NVMe' THEN 2500000
    WHEN 'Crucial P5 Plus 1TB PCIe 4.0 NVMe' THEN 1900000
    WHEN 'Kingston KC3000 1TB PCIe 4.0 NVMe' THEN 2300000
    WHEN 'Samsung 970 Evo Plus 500GB PCIe 3.0 NVMe' THEN 1200000
    WHEN 'WD Blue SN570 1TB PCIe 3.0 NVMe' THEN 1550000
    WHEN 'Crucial P3 1TB PCIe 3.0 NVMe' THEN 1450000
    WHEN 'Kingston NV2 500GB PCIe 4.0 NVMe' THEN 800000
    WHEN 'WD Blue 500GB 2.5 inch SATA III' THEN 850000
    WHEN 'Kingston A400 480GB 2.5 inch SATA III' THEN 790000
    WHEN 'Crucial BX500 240GB 2.5 inch SATA III' THEN 400000
    WHEN 'Seagate Barracuda 1TB 3.5 inch 7200RPM' THEN 900000
    WHEN 'WD Blue 2TB 3.5 inch 7200RPM' THEN 1300000
    ELSE price
END
WHERE product_name IN (
    'Samsung 980 Pro 1TB PCIe 4.0 NVMe',
    'WD Black SN850X 1TB PCIe 4.0 NVMe',
    'Crucial P5 Plus 1TB PCIe 4.0 NVMe',
    'Kingston KC3000 1TB PCIe 4.0 NVMe',
    'Samsung 970 Evo Plus 500GB PCIe 3.0 NVMe',
    'WD Blue SN570 1TB PCIe 3.0 NVMe',
    'Crucial P3 1TB PCIe 3.0 NVMe',
    'Kingston NV2 500GB PCIe 4.0 NVMe',
    'WD Blue 500GB 2.5 inch SATA III',
    'Kingston A400 480GB 2.5 inch SATA III',
    'Crucial BX500 240GB 2.5 inch SATA III',
    'Seagate Barracuda 1TB 3.5 inch 7200RPM',
    'WD Blue 2TB 3.5 inch 7200RPM'
);

UPDATE product
SET price = CASE product_name
    WHEN 'Golden Field 1000B' THEN 350000
    WHEN 'SAMA MT100' THEN 450000
    WHEN 'KENOO M200' THEN 500000
    WHEN 'Xigmatek Eros' THEN 650000
    WHEN 'Vision V1' THEN 550000
    WHEN 'SAMA Falcon' THEN 700000
    WHEN 'VSP V21' THEN 700000
    WHEN 'Golden Field N1' THEN 320000
    WHEN 'Techware Neo' THEN 600000
    WHEN 'VSP KA-280' THEN 480000
    WHEN 'Cooler Master MasterBox Q300L' THEN 950000
    WHEN 'Thermaltake Versa H18' THEN 850000
    WHEN 'Deepcool Matrexx 30 SI' THEN 850000
    WHEN 'Cooler Master N200' THEN 950000
    WHEN 'Thermaltake Versa H17' THEN 800000
    ELSE price
END
WHERE product_name IN (
    'Golden Field 1000B', 'SAMA MT100', 'KENOO M200', 'Xigmatek Eros', 'Vision V1',
    'SAMA Falcon', 'VSP V21', 'Golden Field N1', 'Techware Neo', 'VSP KA-280',
    'Cooler Master MasterBox Q300L', 'Thermaltake Versa H18', 'Deepcool Matrexx 30 SI',
    'Cooler Master N200', 'Thermaltake Versa H17'
);

UPDATE product
SET price = CASE product_name
    WHEN 'Corsair CV550 550W 80+ Bronze' THEN 1350000
    WHEN 'Cooler Master MWE Bronze V2 650W' THEN 1250000
    WHEN 'Seasonic S12III 550W 80+ Bronze' THEN 1200000
    WHEN 'Thermaltake Smart BX1 650W 80+ Bronze' THEN 1200000
    WHEN 'EVGA 600 W1 600W 80+' THEN 950000
    WHEN 'be quiet! System Power 9 500W 80+ Bronze' THEN 1050000
    WHEN 'Corsair CX650M 650W 80+ Bronze' THEN 1600000
    WHEN 'Corsair RM750e 750W 80+ Gold' THEN 3000000
    WHEN 'Thermaltake Toughpower GF1 750W 80+ Gold' THEN 2700000
    WHEN 'Antec NeoECO Gold Zen 700W' THEN 1800000
    ELSE price
END
WHERE product_name IN (
    'Corsair CV550 550W 80+ Bronze',
    'Cooler Master MWE Bronze V2 650W',
    'Seasonic S12III 550W 80+ Bronze',
    'Thermaltake Smart BX1 650W 80+ Bronze',
    'EVGA 600 W1 600W 80+',
    'be quiet! System Power 9 500W 80+ Bronze',
    'Corsair CX650M 650W 80+ Bronze',
    'Corsair RM750e 750W 80+ Gold',
    'Thermaltake Toughpower GF1 750W 80+ Gold',
    'Antec NeoECO Gold Zen 700W'
);

-- Backfill missing images for "Other" products seeded at the end of V7.
-- These products currently have no rows in image, so product detail falls back to /images/no-image.svg.
INSERT INTO image (product_id, image_url)
SELECT seed.product_id, seed.image_url
FROM (
    SELECT 282 AS product_id, '/image/Logitech K120 Office Keyboard.jpg' AS image_url
    UNION ALL
    SELECT 283, '/image/Corsair K55 RGB Pro.jpg'
    UNION ALL
    SELECT 284, '/image/Razer BlackWidow V3 Mechanical.jpg'
    UNION ALL
    SELECT 285, '/image/Logitech G102 LightSync.jpg'
    UNION ALL
    SELECT 286, '/image/Razer DeathAdder Essential.jpg'
    UNION ALL
    SELECT 287, '/image/ASUS ROG Gladius III.jpg'
    UNION ALL
    SELECT 288, '/image/ASUS VZ249HE 24-inch IPS.jpg'
    UNION ALL
    SELECT 289, '/image/MSI Optix G241 144Hz.jpg'
    UNION ALL
    SELECT 290, '/image/Gigabyte G27F2 165Hz.jpg'
) seed
WHERE NOT EXISTS (
    SELECT 1
    FROM image i
    WHERE i.product_id = seed.product_id
);

INSERT INTO brand (name, description, website, status)
SELECT 'Logitech', 'Gaming mice, keyboards, webcams, and audio gear', 'https://www.logitechg.com', TRUE
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name = 'Logitech');

INSERT INTO brand (name, description, website, status)
SELECT 'Razer', 'Gaming peripherals and accessories', 'https://www.razer.com', TRUE
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE name = 'Razer');

UPDATE product p JOIN brand b ON b.name = 'AMD' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'AMD %';
UPDATE product p JOIN brand b ON b.name = 'Intel' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Intel %';
UPDATE product p JOIN brand b ON b.name = 'NVIDIA' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'NVIDIA %';
UPDATE product p JOIN brand b ON b.name = 'ASUS' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'ASUS %';
UPDATE product p JOIN brand b ON b.name = 'MSI' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'MSI %';
UPDATE product p JOIN brand b ON b.name = 'Gigabyte' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Gigabyte %' OR p.product_name LIKE 'GIGABYTE %';
UPDATE product p JOIN brand b ON b.name = 'ASRock' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'ASRock %';
UPDATE product p JOIN brand b ON b.name = 'Corsair' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Corsair %';
UPDATE product p JOIN brand b ON b.name = 'G.Skill' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'G.Skill %' OR p.product_name LIKE 'GSkill %';
UPDATE product p JOIN brand b ON b.name = 'Kingston' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Kingston %';
UPDATE product p JOIN brand b ON b.name = 'Crucial' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Crucial %';
UPDATE product p JOIN brand b ON b.name = 'Teamgroup' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Teamgroup %';
UPDATE product p JOIN brand b ON b.name = 'Samsung' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Samsung %';
UPDATE product p JOIN brand b ON b.name = 'Western Digital' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'WD %' OR p.product_name LIKE 'Western Digital %';
UPDATE product p JOIN brand b ON b.name = 'Seagate' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Seagate %';
UPDATE product p JOIN brand b ON b.name = 'SanDisk' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'SanDisk %';
UPDATE product p JOIN brand b ON b.name = 'Toshiba' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Toshiba %';
UPDATE product p JOIN brand b ON b.name = 'Cooler Master' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Cooler Master %';
UPDATE product p JOIN brand b ON b.name = 'Thermaltake' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Thermaltake %';
UPDATE product p JOIN brand b ON b.name = 'NZXT' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'NZXT %';
UPDATE product p JOIN brand b ON b.name = 'Lian Li' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Lian Li %';
UPDATE product p JOIN brand b ON b.name = 'Fractal Design' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Fractal Design %';
UPDATE product p JOIN brand b ON b.name = 'Phanteks' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Phanteks %';
UPDATE product p JOIN brand b ON b.name = 'Noctua' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Noctua %';
UPDATE product p JOIN brand b ON b.name = 'Deepcool' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Deepcool %';
UPDATE product p JOIN brand b ON b.name = 'Thermalright' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Thermalright %';
UPDATE product p JOIN brand b ON b.name = 'be quiet!' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'be quiet %' OR p.product_name LIKE 'be quiet!%';
UPDATE product p JOIN brand b ON b.name = 'Arctic' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Arctic %';
UPDATE product p JOIN brand b ON b.name = 'Seasonic' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Seasonic %';
UPDATE product p JOIN brand b ON b.name = 'EVGA' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'EVGA %';
UPDATE product p JOIN brand b ON b.name = 'FSP' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'FSP %';
UPDATE product p JOIN brand b ON b.name = 'Antec' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Antec %';
UPDATE product p JOIN brand b ON b.name = 'Hyte' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Hyte %';
UPDATE product p JOIN brand b ON b.name = 'Logitech' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Logitech %';
UPDATE product p JOIN brand b ON b.name = 'Razer' SET p.brand_id = b.brand_id WHERE p.product_name LIKE 'Razer %';
