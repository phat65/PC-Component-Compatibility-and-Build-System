CREATE TEMPORARY TABLE seed_gear_product (
    product_name VARCHAR(255) PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL,
    child_category_name VARCHAR(100) NOT NULL,
    price DECIMAL(15,0) NOT NULL,
    description VARCHAR(500),
    specification VARCHAR(500),
    inventory_quantity INT NOT NULL,
    performance_score INT NOT NULL
);

INSERT INTO seed_gear_product
(child_category_name, brand_name, product_name, price, description, specification, inventory_quantity, performance_score)
VALUES
('Keyboard', 'Logitech', 'Logitech MX Keys S Wireless Keyboard', 2800000, 'Low profile wireless productivity keyboard', 'Wireless, USB-C, multi-device', 20, 76),
('Keyboard', 'Logitech', 'Logitech G915 X Lightspeed TKL', 5200000, 'Low profile wireless gaming keyboard', 'TKL, Lightspeed, RGB, mechanical', 12, 88),
('Keyboard', 'Razer', 'Razer Huntsman V3 Pro TKL', 5600000, 'Analog optical esports keyboard', 'TKL, analog optical switches, RGB', 10, 90),
('Keyboard', 'Razer', 'Razer BlackWidow V4 75', 4300000, 'Compact hot-swap mechanical keyboard', '75 percent, mechanical, RGB, hot-swap', 12, 84),
('Keyboard', 'SteelSeries', 'SteelSeries Apex Pro TKL Wireless', 6200000, 'Adjustable actuation wireless keyboard', 'TKL, OmniPoint switches, OLED display', 8, 92),
('Keyboard', 'Corsair', 'Corsair K70 RGB Pro', 3600000, 'Full-size mechanical gaming keyboard', 'Mechanical, RGB, tournament switch', 15, 82),
('Keyboard', 'Keychron', 'Keychron Q1 Max', 4900000, 'Premium wireless custom keyboard', '75 percent, QMK, tri-mode, aluminum', 9, 86),
('Keyboard', 'Keychron', 'Keychron K2 HE', 3400000, 'Magnetic switch wireless keyboard', '75 percent, HE switches, Bluetooth', 14, 82),
('Keyboard', 'Akko', 'Akko MOD 007B HE', 3600000, 'Hall effect gaming keyboard', '75 percent, magnetic switches, tri-mode', 11, 84),
('Keyboard', 'ASUS', 'ASUS ROG Azoth', 5800000, 'Premium compact wireless gaming keyboard', '75 percent, OLED, tri-mode, gasket mount', 8, 90),
('Mouse', 'Logitech', 'Logitech MX Master 3S', 2500000, 'Ergonomic wireless productivity mouse', '8000 DPI, silent clicks, multi-device', 22, 76),
('Mouse', 'Logitech', 'Logitech G502 X Lightspeed', 3200000, 'Wireless gaming mouse with hybrid switches', 'Lightspeed, HERO sensor, adjustable buttons', 16, 84),
('Mouse', 'Razer', 'Razer Viper V3 Pro', 3900000, 'Lightweight wireless esports mouse', 'Focus Pro sensor, HyperSpeed wireless', 12, 92),
('Mouse', 'Razer', 'Razer Basilisk V3 Pro', 3600000, 'Ergonomic wireless gaming mouse', 'Focus Pro sensor, tilt wheel, RGB', 12, 86),
('Mouse', 'SteelSeries', 'SteelSeries Rival 5', 1400000, 'Multi-genre wired gaming mouse', 'TrueMove Air sensor, 9 buttons, RGB', 18, 72),
('Mouse', 'SteelSeries', 'SteelSeries Aerox 3 Wireless', 2300000, 'Lightweight wireless gaming mouse', 'Wireless, 200-hour battery, IP54', 14, 78),
('Mouse', 'Corsair', 'Corsair M75 Wireless', 2900000, 'Ambidextrous wireless FPS mouse', 'Wireless, optical switches, lightweight', 14, 82),
('Mouse', 'ASUS', 'ASUS ROG Harpe Ace Aim Lab Edition', 3300000, 'Ultralight wireless esports mouse', 'Aim Lab tuned, 54g class, wireless', 10, 86),
('Mouse', 'Cooler Master', 'Cooler Master MM712', 1700000, 'Lightweight wireless gaming mouse', 'Wireless, optical sensor, 59g class', 16, 74),
('Mouse', 'HyperX', 'HyperX Pulsefire Haste 2 Wireless', 2400000, 'Lightweight wireless gaming mouse', 'Wireless, 26K sensor, low weight', 16, 80),
('Monitor', 'ASUS', 'ASUS TUF Gaming VG27AQ3A', 6200000, '27-inch QHD high-refresh gaming monitor', '27 inch, QHD, IPS, 180Hz', 10, 82),
('Monitor', 'MSI', 'MSI G274QPF-QD', 5900000, '27-inch QHD quantum-dot gaming monitor', '27 inch, QHD, Rapid IPS, 170Hz', 9, 80),
('Monitor', 'Gigabyte', 'Gigabyte M27Q', 6100000, 'QHD KVM gaming monitor', '27 inch, QHD, IPS, 170Hz, KVM', 9, 82),
('Monitor', 'LG', 'LG UltraGear 27GP850-B', 6500000, 'Fast Nano IPS gaming monitor', '27 inch, QHD, Nano IPS, 165Hz', 8, 84),
('Monitor', 'Dell', 'Dell G2724D', 5600000, 'QHD gaming monitor with clean styling', '27 inch, QHD, IPS, 165Hz', 10, 78),
('Monitor', 'Samsung', 'Samsung Odyssey G5 G55C 27', 5200000, 'Curved QHD gaming monitor', '27 inch, QHD, VA, 165Hz, curved', 8, 76),
('Monitor', 'ViewSonic', 'ViewSonic VX2758A-2K-PRO', 4700000, 'Value QHD gaming monitor', '27 inch, QHD, IPS, 180Hz', 11, 76),
('Monitor', 'AOC', 'AOC Q27G3XMN', 7200000, 'Mini LED QHD gaming monitor', '27 inch, QHD, Mini LED, high refresh', 6, 86),
('Monitor', 'ASUS', 'ASUS ProArt PA278CV', 6900000, 'Color-focused QHD productivity monitor', '27 inch, QHD, IPS, USB-C', 7, 80),
('Monitor', 'MSI', 'MSI Modern MD272QXP', 5400000, 'QHD productivity monitor', '27 inch, QHD, IPS, USB-C', 8, 74),
('Headset', 'SteelSeries', 'SteelSeries Arctis Nova 5 Wireless', 3300000, 'Wireless gaming headset with app profiles', '2.4GHz wireless, Bluetooth, USB-C', 14, 84),
('Headset', 'HyperX', 'HyperX Cloud III Wireless', 3100000, 'Wireless gaming headset for PC and console', '2.4GHz wireless, DTS Headphone:X', 13, 82),
('Headset', 'Logitech', 'Logitech G PRO X 2 Lightspeed', 4900000, 'Premium wireless gaming headset', 'Graphene drivers, Lightspeed, Bluetooth', 9, 88),
('Headset', 'Razer', 'Razer BlackShark V2 Pro 2023', 4500000, 'Wireless esports headset', '2.4GHz wireless, HyperClear mic', 10, 86),
('Headset', 'Corsair', 'Corsair HS80 Max Wireless', 3600000, 'Comfort wireless gaming headset', '2.4GHz wireless, Bluetooth, Dolby Audio', 12, 82),
('Headset', 'SteelSeries', 'SteelSeries Arctis Nova Pro Wireless', 8500000, 'Premium wireless gaming headset', 'ANC, hot-swap battery, base station', 5, 94),
('Headset', 'HyperX', 'HyperX Cloud Alpha Wireless', 3300000, 'Long battery wireless headset', '2.4GHz wireless, dual chamber drivers', 10, 82),
('Headset', 'Logitech', 'Logitech G733 Lightspeed', 2900000, 'Lightweight wireless RGB headset', 'Lightspeed wireless, RGB, Blue VOICE', 14, 78),
('Headset', 'Razer', 'Razer Barracuda X 2022', 2400000, 'Multi-platform wireless headset', '2.4GHz wireless, Bluetooth, USB-C', 15, 76),
('Headset', 'ASUS', 'ASUS ROG Delta S Wireless', 3900000, 'Wireless gaming headset', '2.4GHz wireless, Bluetooth, lightweight', 8, 82),
('Mousepad', 'Logitech', 'Logitech G Powerplay Wireless Charging Mousepad', 2900000, 'Wireless charging mousepad', 'Powerplay charging, hard and cloth surfaces', 8, 80),
('Mousepad', 'SteelSeries', 'SteelSeries QcK Heavy XXL', 850000, 'Large thick cloth gaming mousepad', 'XXL cloth surface, heavy rubber base', 20, 68),
('Mousepad', 'Razer', 'Razer Gigantus V2 XXL', 750000, 'Large soft gaming mousepad', 'XXL cloth surface, micro-weave texture', 22, 66),
('Mousepad', 'Corsair', 'Corsair MM700 RGB Extended', 1500000, 'Extended RGB mousepad', 'Cloth surface, RGB, USB hub', 12, 74),
('Mousepad', 'ASUS', 'ASUS ROG Sheath BLK LTD', 850000, 'Large gaming mousepad', 'Extended cloth surface, stitched edges', 16, 68),
('Mousepad', 'Cooler Master', 'Cooler Master MP511 XL', 650000, 'Cordura gaming mousepad', 'XL Cordura fabric, splash resistant', 18, 66),
('Mousepad', 'HyperX', 'HyperX Pulsefire Mat RGB XL', 1200000, 'RGB gaming mousepad', 'XL cloth surface, RGB lighting', 14, 72),
('Mousepad', 'Razer', 'Razer Strider Large', 850000, 'Hybrid gaming mousepad', 'Hybrid surface, anti-slip base', 16, 70),
('Mousepad', 'SteelSeries', 'SteelSeries QcK Prism Cloth XL', 1600000, 'RGB cloth gaming mousepad', 'XL cloth surface, dual-zone RGB', 10, 74),
('Mousepad', 'Logitech', 'Logitech G840 XL', 1100000, 'Extended cloth mousepad', 'XL cloth surface, rubber base', 15, 70),
('Speaker', 'Logitech', 'Logitech G560 Lightsync Speakers', 4200000, 'RGB 2.1 gaming speaker system', '2.1 speakers, Lightsync RGB, DTS:X', 8, 82),
('Speaker', 'Razer', 'Razer Nommo V2 X', 2800000, 'Compact gaming speakers', '2.0 speakers, Bluetooth and USB audio', 10, 74),
('Speaker', 'Creative', 'Creative Pebble Pro', 1500000, 'Compact USB desktop speakers', '2.0 speakers, USB-C audio, Bluetooth', 16, 68),
('Speaker', 'Edifier', 'Edifier MR4 Studio Monitor', 2200000, 'Compact studio monitor speakers', '2.0 active speakers, balanced input', 12, 76),
('Speaker', 'Logitech', 'Logitech Z407 Bluetooth Speakers', 2300000, 'Wireless control desktop speakers', '2.1 speakers, Bluetooth, wireless dial', 14, 72),
('Speaker', 'Creative', 'Creative Stage Air V2', 1100000, 'Compact desktop soundbar', 'USB-C, Bluetooth, under-monitor design', 18, 64),
('Speaker', 'Razer', 'Razer Leviathan V2 X', 2500000, 'USB-C gaming soundbar', 'Compact soundbar, Chroma RGB, USB-C', 10, 74),
('Speaker', 'Edifier', 'Edifier G2000 Gaming Speakers', 1800000, 'Compact RGB gaming speakers', '2.0 speakers, RGB, Bluetooth', 14, 70),
('Speaker', 'Logitech', 'Logitech Z625 Speaker System', 4300000, 'Powerful THX 2.1 speaker system', '2.1 speakers, THX, optical input', 8, 82),
('Speaker', 'Creative', 'Creative Sound Blaster Katana SE', 6200000, 'Gaming soundbar for desktop setups', 'Soundbar, RGB, USB, Bluetooth', 5, 86),
('Chair', 'Corsair', 'Corsair TC100 Relaxed Fabric', 4900000, 'Fabric gaming chair with relaxed fit', 'Fabric upholstery, adjustable armrests', 7, 76),
('Chair', 'Razer', 'Razer Iskur V2', 13500000, 'Premium ergonomic gaming chair', 'Adaptive lumbar support, 4D armrests', 4, 90),
('Chair', 'Cooler Master', 'Cooler Master Caliber X2', 6200000, 'Large gaming chair for long sessions', 'Ergonomic chair, recline, 4D armrests', 6, 80),
('Chair', 'Secretlab', 'Secretlab TITAN Evo Regular', 14500000, 'Premium gaming chair', 'Cold-cure foam, magnetic pillow, lumbar support', 5, 92),
('Chair', 'Corsair', 'Corsair TC200 Fabric', 6500000, 'Racing-style gaming chair', 'Fabric, 4D armrests, wide seat', 6, 80),
('Chair', 'Thermaltake', 'Thermaltake Argent E700', 18000000, 'Designer premium gaming chair', 'Aluminum frame, leather upholstery', 3, 92),
('Chair', 'Cooler Master', 'Cooler Master Hybrid 1 Ergo', 8200000, 'Ergonomic gaming office chair', 'Mesh back, lumbar support, headrest', 5, 84),
('Chair', 'Razer', 'Razer Fujin', 12500000, 'Mesh ergonomic gaming chair', 'Mesh design, lumbar support, adjustable arms', 4, 88),
('Chair', 'Secretlab', 'Secretlab TITAN Evo XL', 15900000, 'Large premium gaming chair', 'XL size, magnetic pillow, lumbar support', 3, 92),
('Chair', 'MSI', 'MSI MAG CH130 I Repeltek Fabric', 5900000, 'Fabric gaming chair', 'Water-repellent fabric, recline, armrests', 6, 78),
('Desk', 'Secretlab', 'Secretlab MAGNUS Pro', 18500000, 'Sit-to-stand metal gaming desk', 'Electric standing desk, cable tray', 3, 92),
('Desk', 'Corsair', 'Corsair Platform 6 Creator Edition', 15500000, 'Modular desk for gaming and creation', 'Large modular desk, rail system', 3, 88),
('Desk', 'Cooler Master', 'Cooler Master GD160 ARGB Gaming Desk', 9200000, 'Wide gaming desk with RGB accents', '160cm desk, cable management, RGB', 5, 78),
('Desk', 'Thermaltake', 'Thermaltake ToughDesk 300 RGB', 12900000, 'Electric gaming desk', 'Height adjustable, RGB, large surface', 4, 86),
('Desk', 'Secretlab', 'Secretlab MAGNUS Metal Desk', 12500000, 'Magnetic cable-management desk', 'Metal desk, cable tray, magnetic accessories', 5, 84),
('Desk', 'Cooler Master', 'Cooler Master GD120 ARGB Desk', 6900000, 'Compact RGB gaming desk', '120cm desk, RGB accents, cable routing', 6, 72),
('Desk', 'Corsair', 'Corsair Platform 4 Desk', 9800000, 'Compact modular creator desk', 'Modular rails, cable routing, sturdy frame', 4, 80),
('Desk', 'Thermaltake', 'Thermaltake Level 20 BattleStation', 14900000, 'Premium electric gaming desk', 'Height adjustable, RGB, wide surface', 3, 88),
('Desk', 'Secretlab', 'Secretlab MAGNUS Pro XL', 21900000, 'Large sit-to-stand gaming desk', 'XL electric desk, cable tray, metal surface', 2, 94),
('Desk', 'Razer', 'Razer Iskur Desk Mat Bundle', 4200000, 'Compact gaming desk bundle', 'Desk surface, cable routing, setup accessories', 8, 66),
('Webcam', 'Logitech', 'Logitech Brio 4K', 3600000, '4K webcam for streaming and calls', '4K, HDR, Windows Hello support', 11, 84),
('Webcam', 'Razer', 'Razer Kiyo Pro', 3300000, 'Wide-angle webcam for streamers', '1080p60, adaptive light sensor', 9, 80),
('Webcam', 'Elgato', 'Elgato Facecam MK.2', 3900000, 'Dedicated streaming webcam', '1080p60, Sony sensor, app control', 8, 86),
('Webcam', 'Logitech', 'Logitech StreamCam', 2700000, 'USB-C streaming webcam', '1080p60, USB-C, auto framing', 12, 78),
('Webcam', 'Razer', 'Razer Kiyo X', 1700000, 'Affordable streaming webcam', '1080p30, autofocus, USB', 15, 70),
('Webcam', 'Elgato', 'Elgato Facecam Pro', 7200000, '4K60 webcam for creators', '4K60, large sensor, app control', 4, 92),
('Webcam', 'Logitech', 'Logitech C920s HD Pro', 1900000, 'Reliable full HD webcam', '1080p30, privacy shutter, stereo mic', 18, 72),
('Webcam', 'ASUS', 'ASUS ROG Eye S', 2200000, 'Compact streaming webcam', '1080p60, beamforming mic, USB', 10, 74),
('Webcam', 'Dell', 'Dell UltraSharp WB7022', 5200000, 'Premium 4K webcam', '4K HDR, AI auto framing, Windows Hello', 5, 86),
('Webcam', 'AOC', 'AOC AM420 Webcam', 950000, 'Entry webcam for video calls', '1080p, USB, built-in microphone', 20, 60),
('Microphone', 'HyperX', 'HyperX QuadCast S RGB', 3300000, 'USB condenser microphone for streaming', 'USB, RGB, tap-to-mute, shock mount', 10, 84),
('Microphone', 'Blue', 'Blue Yeti USB Microphone', 2800000, 'Popular USB microphone', 'USB, multiple pickup patterns, desktop stand', 12, 78),
('Microphone', 'Razer', 'Razer Seiren V2 X', 2300000, 'Compact USB streaming microphone', 'USB, supercardioid pickup, shock absorber', 14, 74),
('Microphone', 'Elgato', 'Elgato Wave 3', 3600000, 'USB microphone for streamers', 'USB-C, Clipguard, Wave Link software', 9, 86),
('Microphone', 'HyperX', 'HyperX SoloCast', 1400000, 'Compact USB microphone', 'USB, tap-to-mute, cardioid pickup', 18, 68),
('Microphone', 'Logitech', 'Logitech Blue Sona', 7200000, 'Dynamic XLR broadcast microphone', 'XLR, dynamic capsule, ClearAmp', 5, 90),
('Microphone', 'Razer', 'Razer Seiren Mini', 1200000, 'Ultra-compact USB microphone', 'USB, supercardioid, compact stand', 20, 64),
('Microphone', 'Elgato', 'Elgato Wave DX', 2500000, 'Dynamic XLR microphone', 'XLR, cardioid, broadcast voice', 10, 78),
('Microphone', 'Creative', 'Creative Live Mic M3', 1700000, 'USB condenser microphone', 'USB, cardioid, mute control', 12, 70),
('Microphone', 'Blue', 'Blue Yeti Nano', 2100000, 'Compact USB microphone', 'USB, dual pickup patterns, compact stand', 14, 72),
('Controller', 'Microsoft', 'Microsoft Xbox Wireless Controller Carbon Black', 1500000, 'Wireless Xbox controller for PC gaming', 'Bluetooth, Xbox Wireless, USB-C', 18, 76),
('Controller', 'Razer', 'Razer Wolverine V2 Chroma', 3500000, 'Wired pro controller for PC and Xbox', 'Mecha-tactile buttons, extra triggers, RGB', 8, 84),
('Controller', 'Logitech', 'Logitech F310 Gamepad', 550000, 'Affordable wired PC gamepad', 'USB wired, XInput and DirectInput', 25, 58),
('Controller', 'Microsoft', 'Microsoft Xbox Elite Wireless Controller Series 2', 4200000, 'Premium Xbox and PC controller', 'Adjustable sticks, paddles, charging dock', 7, 88),
('Controller', 'Razer', 'Razer Wolverine V3 Pro', 5200000, 'Wireless esports controller', 'Wireless, back paddles, Hall effect sticks', 5, 90),
('Controller', 'ASUS', 'ASUS ROG Raikiri Pro', 3900000, 'OLED pro controller for PC', 'Wireless, OLED display, rear buttons', 6, 84),
('Controller', 'SteelSeries', 'SteelSeries Stratus Duo', 1600000, 'Wireless PC controller', '2.4GHz wireless, Bluetooth, rechargeable', 10, 70),
('Controller', 'HyperX', 'HyperX Clutch Gladiate RGB', 1200000, 'Wired Xbox and PC controller', 'Wired, dual trigger locks, RGB', 12, 68),
('Controller', 'MSI', 'MSI Force GC30 V2 White', 950000, 'Wireless PC gamepad', 'Wireless, dual vibration, USB dongle', 14, 64),
('Controller', 'Cooler Master', 'Cooler Master Storm Controller', 1300000, 'Wired PC gaming controller', 'USB wired, ergonomic grips, programmable buttons', 10, 66),
('Add-on', 'Elgato', 'Elgato Stream Deck MK.2', 3600000, 'Programmable control pad', '15 LCD keys, USB-C, custom profiles', 10, 86),
('Add-on', 'Corsair', 'Corsair ST100 RGB Premium Headset Stand', 1800000, 'RGB headset stand with USB audio', 'Aluminum stand, RGB, USB passthrough', 12, 70),
('Add-on', 'Razer', 'Razer Mouse Dock Pro', 2100000, 'Wireless charging dock', 'Wireless charging, RGB, 4K polling support', 9, 78),
('Add-on', 'Elgato', 'Elgato Key Light Air', 3600000, 'Desktop streaming light', 'Wi-Fi control, adjustable brightness', 8, 82),
('Add-on', 'Elgato', 'Elgato Wave XLR', 3800000, 'USB XLR audio interface', 'XLR preamp, USB-C, mixer control', 7, 84),
('Add-on', 'Logitech', 'Logitech Litra Beam', 2500000, 'Desktop key light for streamers', 'LED key light, adjustable color temperature', 10, 76),
('Add-on', 'Razer', 'Razer Base Station V2 Chroma', 1900000, 'RGB headset stand and USB hub', 'Headset stand, USB hub, 3.5mm audio', 10, 72),
('Add-on', 'ASUS', 'ASUS ROG Throne Qi', 2600000, 'RGB headset stand with wireless charging', 'Qi charging, RGB, USB hub', 8, 76),
('Add-on', 'SteelSeries', 'SteelSeries Arena Wireless Mic', 2200000, 'Wireless accessory microphone', 'Wireless mic, desktop receiver, low latency', 8, 70),
('Add-on', 'Cooler Master', 'Cooler Master MasterAccessory GEM', 450000, 'Magnetic cable and accessory holder', 'Magnetic mount, cable routing, compact design', 20, 58);

INSERT INTO product (brand_id, product_name, price, status, lifecycle_status, description, specification, inventory_quantity, performance_score)
SELECT b.brand_id, s.product_name, s.price, TRUE, 'SELLING', s.description, s.specification, s.inventory_quantity, s.performance_score
FROM seed_gear_product s
JOIN brand b ON b.name = s.brand_name
WHERE NOT EXISTS (
    SELECT 1 FROM product p WHERE p.product_name = s.product_name
);

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_gear_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name = 'Gear'
WHERE NOT EXISTS (
    SELECT 1 FROM product_category pc
    WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
);

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_gear_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name = s.child_category_name
WHERE NOT EXISTS (
    SELECT 1 FROM product_category pc
    WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
);

INSERT INTO image (product_id, image_url)
SELECT p.product_id, '/images/no-image.svg'
FROM seed_gear_product s
JOIN product p ON p.product_name = s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM image i WHERE i.product_id = p.product_id
);
