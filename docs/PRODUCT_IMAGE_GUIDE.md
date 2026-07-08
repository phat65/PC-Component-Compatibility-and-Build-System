# Product Image Guide

Product image files are stored on disk under `uploads/images` and served by Spring through `/image/{filename}`.

## Manual Admin Flow

Use the staff product form to upload images. The application stores the file under `uploads/images` and creates an `image.image_url` row with this shape:

```text
/image/generated-file-name.jpg
```

Accepted upload types in code are JPEG and PNG, with a 5MB limit per image.

## Seed Data Flow

For products inserted by Flyway seed data:

1. Put the image file in `uploads/images`.
2. Replace the seeded placeholder row or add an `image` row in a migration:

```sql
UPDATE image i
JOIN product p ON p.product_id = i.product_id
SET i.image_url = '/image/example-product.jpg'
WHERE p.product_name = 'Example Product'
  AND i.image_url = '/images/no-image.svg';
```

If the product has no image row yet, add one:

```sql
INSERT INTO image (product_id, image_url)
SELECT p.product_id, '/image/example-product.jpg'
FROM product p
WHERE p.product_name = 'Example Product'
  AND NOT EXISTS (
      SELECT 1
      FROM image i
      WHERE i.product_id = p.product_id
        AND i.image_url = '/image/example-product.jpg'
  );
```

3. Keep filenames ASCII-friendly and stable. Spaces work in existing seed data, but hyphenated filenames are easier to maintain.

The Docker image copies the repository's `uploads/images` folder into the initial `uploads_data` volume. If that named volume already exists, Docker preserves its current files and does not overwrite them.
