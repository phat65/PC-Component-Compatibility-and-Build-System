ALTER TABLE category
ADD COLUMN parent_id INT NULL AFTER category_id,
ADD CONSTRAINT fk_category_parent
    FOREIGN KEY (parent_id) REFERENCES category(category_id)
    ON DELETE SET NULL;

INSERT INTO category (category_name, description, display_order, parent_id)
SELECT seed.category_name, seed.description, seed.display_order, parent.category_id
FROM (
    SELECT 'Chair' AS category_name, 'Gaming and office chairs' AS description, 904 AS display_order
    UNION ALL
    SELECT 'Headset', 'Gaming headsets and audio gear', 905
    UNION ALL
    SELECT 'Mousepad', 'Mousepads and desk mats', 906
    UNION ALL
    SELECT 'Speaker', 'Speakers and desktop audio', 907
    UNION ALL
    SELECT 'Desk', 'Gaming desks and setup furniture', 908
    UNION ALL
    SELECT 'Webcam', 'Streaming and video call webcams', 909
) seed
JOIN category parent ON parent.category_name = 'Other'
WHERE NOT EXISTS (
    SELECT 1
    FROM category existing
    WHERE LOWER(existing.category_name) = LOWER(seed.category_name)
);

UPDATE category child
JOIN category parent ON parent.category_name = 'Other'
SET child.parent_id = parent.category_id
WHERE child.category_name IN (
    'Keyboard',
    'Mouse',
    'Monitor',
    'Add-on',
    'Chair',
    'Headset',
    'Mousepad',
    'Speaker',
    'Desk',
    'Webcam'
);
