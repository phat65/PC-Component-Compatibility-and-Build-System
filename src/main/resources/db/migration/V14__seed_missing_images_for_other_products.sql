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
