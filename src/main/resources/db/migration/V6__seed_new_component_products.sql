CREATE TEMPORARY TABLE seed_new_component_product (
    product_name VARCHAR(255) PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    price DECIMAL(15,0) NOT NULL,
    description VARCHAR(500),
    specification VARCHAR(500),
    inventory_quantity INT NOT NULL,
    performance_score INT NOT NULL
);

INSERT INTO seed_new_component_product
(product_name, brand_name, category_name, price, description, specification, inventory_quantity, performance_score)
VALUES
('AMD Ryzen 5 9600X', 'AMD', 'CPU', 6500000, 'Zen 5 AM5 CPU for efficient gaming builds', '6C/12T, up to 5.4GHz, 65W, DDR5-5600, PCIe 5.0', 18, 88),
('AMD Ryzen 7 9700X', 'AMD', 'CPU', 8900000, '8-core Zen 5 AM5 CPU for gaming and creation', '8C/16T, up to 5.5GHz, 65W, DDR5-5600, PCIe 5.0', 12, 92),
('Kingston NV3 1TB PCIe 4.0 NVMe', 'Kingston', 'Storage', 1700000, 'Gen 4 NVMe SSD for gaming and everyday storage', '1TB, M.2 2280, PCIe 4.0 x4, up to 6000/4000 MB/s', 30, 72),
('Kingston NV3 2TB PCIe 4.0 NVMe', 'Kingston', 'Storage', 3000000, 'Higher-capacity Gen 4 NVMe SSD', '2TB, M.2 2280, PCIe 4.0 x4, up to 6000/5000 MB/s', 22, 76),
('Kingston NV3 4TB PCIe 4.0 NVMe', 'Kingston', 'Storage', 5800000, 'High-capacity Gen 4 NVMe SSD', '4TB, M.2 2280, PCIe 4.0 x4, up to 6000/5000 MB/s', 10, 78),
('Corsair Vengeance 32GB (2x16GB) DDR5 6000MHz CL30 EXPO', 'Corsair', 'Memory', 3200000, 'Low-latency DDR5 kit for AM5 builds', '32GB, 2x16GB, DDR5-6000, CL30, EXPO', 24, 82),
('G.Skill Flare X5 32GB (2x16GB) DDR5 6000MHz CL36', 'G.Skill', 'Memory', 3000000, 'DDR5 EXPO memory kit for Ryzen systems', '32GB, 2x16GB, DDR5-6000, CL36', 18, 78),
('Corsair RM850e ATX 3.1 850W 80+ Gold', 'Corsair', 'Power Supply', 3500000, 'Fully modular ATX 3.1 power supply', '850W, 80 PLUS Gold, fully modular, ATX 3.1', 20, 86),
('Arctic Liquid Freezer III 360 A-RGB', 'Arctic', 'Cooling', 2800000, '360mm AIO liquid cooler for modern CPUs', 'AIO, 360mm radiator, 3x120mm fans', 14, 84);

INSERT INTO product (brand_id, product_name, price, status, lifecycle_status, description, specification, inventory_quantity, performance_score)
SELECT b.brand_id, s.product_name, s.price, TRUE, 'SELLING', s.description, s.specification, s.inventory_quantity, s.performance_score
FROM seed_new_component_product s
JOIN brand b ON b.name = s.brand_name
WHERE NOT EXISTS (
    SELECT 1
    FROM product p
    WHERE p.product_name = s.product_name
);

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name = s.category_name
WHERE NOT EXISTS (
    SELECT 1 FROM product_category pc
    WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
);

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name IN ('Socket AM5', 'PCIe 5.0')
WHERE s.product_name IN ('AMD Ryzen 5 9600X', 'AMD Ryzen 7 9700X')
  AND NOT EXISTS (
      SELECT 1 FROM product_category pc
      WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
  );

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name IN ('M.2 NVMe', 'PCIe 4.0')
WHERE s.product_name LIKE 'Kingston NV3%'
  AND NOT EXISTS (
      SELECT 1 FROM product_category pc
      WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
  );

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name = 'DDR5'
WHERE s.category_name = 'Memory'
  AND NOT EXISTS (
      SELECT 1 FROM product_category pc
      WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
  );

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name = 'ATX PSU'
WHERE s.product_name = 'Corsair RM850e ATX 3.1 850W 80+ Gold'
  AND NOT EXISTS (
      SELECT 1 FROM product_category pc
      WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
  );

INSERT INTO product_category (product_id, category_id)
SELECT p.product_id, c.category_id
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
JOIN category c ON c.category_name = 'AIO Cooling'
WHERE s.product_name = 'Arctic Liquid Freezer III 360 A-RGB'
  AND NOT EXISTS (
      SELECT 1 FROM product_category pc
      WHERE pc.product_id = p.product_id AND pc.category_id = c.category_id
  );

INSERT INTO cpu (product_id, socket, tdp, max_memory_speed, memory_channels, pcie_version, has_igpu)
SELECT p.product_id, 'AM5', 65, 5600, 2, '5.0', TRUE
FROM product p
WHERE p.product_name = 'AMD Ryzen 5 9600X'
  AND NOT EXISTS (SELECT 1 FROM cpu c WHERE c.product_id = p.product_id);

INSERT INTO cpu (product_id, socket, tdp, max_memory_speed, memory_channels, pcie_version, has_igpu)
SELECT p.product_id, 'AM5', 65, 5600, 2, '5.0', TRUE
FROM product p
WHERE p.product_name = 'AMD Ryzen 7 9700X'
  AND NOT EXISTS (SELECT 1 FROM cpu c WHERE c.product_id = p.product_id);

INSERT INTO storage (product_id, type, capacity, interface, read_speed, write_speed)
SELECT p.product_id, 'NVMe SSD', 1000, 'NVMe', 6000, 4000
FROM product p
WHERE p.product_name = 'Kingston NV3 1TB PCIe 4.0 NVMe'
  AND NOT EXISTS (SELECT 1 FROM storage st WHERE st.product_id = p.product_id);

INSERT INTO storage (product_id, type, capacity, interface, read_speed, write_speed)
SELECT p.product_id, 'NVMe SSD', 2000, 'NVMe', 6000, 5000
FROM product p
WHERE p.product_name = 'Kingston NV3 2TB PCIe 4.0 NVMe'
  AND NOT EXISTS (SELECT 1 FROM storage st WHERE st.product_id = p.product_id);

INSERT INTO storage (product_id, type, capacity, interface, read_speed, write_speed)
SELECT p.product_id, 'NVMe SSD', 4000, 'NVMe', 6000, 5000
FROM product p
WHERE p.product_name = 'Kingston NV3 4TB PCIe 4.0 NVMe'
  AND NOT EXISTS (SELECT 1 FROM storage st WHERE st.product_id = p.product_id);

INSERT INTO memory (product_id, type, capacity, speed, tdp, modules)
SELECT p.product_id, 'DDR5', 32, 6000, 10, 2
FROM product p
WHERE p.product_name = 'Corsair Vengeance 32GB (2x16GB) DDR5 6000MHz CL30 EXPO'
  AND NOT EXISTS (SELECT 1 FROM memory m WHERE m.product_id = p.product_id);

INSERT INTO memory (product_id, type, capacity, speed, tdp, modules)
SELECT p.product_id, 'DDR5', 32, 6000, 10, 2
FROM product p
WHERE p.product_name = 'G.Skill Flare X5 32GB (2x16GB) DDR5 6000MHz CL36'
  AND NOT EXISTS (SELECT 1 FROM memory m WHERE m.product_id = p.product_id);

INSERT INTO power_supply (product_id, wattage, efficiency, modular, form_factor)
SELECT p.product_id, 850, '80+ Gold', TRUE, 'ATX'
FROM product p
WHERE p.product_name = 'Corsair RM850e ATX 3.1 850W 80+ Gold'
  AND NOT EXISTS (SELECT 1 FROM power_supply ps WHERE ps.product_id = p.product_id);

INSERT INTO cooling (product_id, type, max_tdp, fan_size, radiator_size)
SELECT p.product_id, 'AIO', 300, 120, 360
FROM product p
WHERE p.product_name = 'Arctic Liquid Freezer III 360 A-RGB'
  AND NOT EXISTS (SELECT 1 FROM cooling cl WHERE cl.product_id = p.product_id);

INSERT INTO image (product_id, image_url)
SELECT p.product_id, '/images/no-image.svg'
FROM seed_new_component_product s
JOIN product p ON p.product_name = s.product_name
WHERE NOT EXISTS (
    SELECT 1 FROM image i WHERE i.product_id = p.product_id
);
