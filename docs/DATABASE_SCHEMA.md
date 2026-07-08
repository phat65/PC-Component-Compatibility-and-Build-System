# Database Schema

Flyway migrations in `src/main/resources/db/migration` are the schema source of truth. This branch intentionally squashes the early development migrations into a clean baseline, so existing databases with old Flyway history must be reset before using it.

Current migration head: `V7__seed_new_gear_products.sql`.

## Core Tables

### Account

- `account`: login identity, profile fields, role, enabled status.
- `account_address`: saved customer addresses, linked to `account`.

### Product Catalog

- `brand`: product brand metadata.
- `category`: product categories, including optional `parent_id` for grouped categories. Category ids `1..9` are intentionally stable because the component spec service currently maps them directly: Mainboard, CPU, GPU, Memory, Storage, Case, Power Supply, Cooling, Gear.
- `product`: catalog item, price, visibility status, inventory, performance score, lifecycle status.
- `product_category`: many-to-many product/category mapping.
- `image`: product image URLs served through `/image/...`.

Storefront visibility depends on both `product.status = true` and `product.lifecycle_status = 'SELLING'`.

The clean seed keeps the legacy product catalog, normalizes legacy prices/brand mappings, and appends new current products. Gear products are mapped to both the `Gear` parent category and their child category such as `Keyboard`, `Mouse`, `Monitor`, or `Headset`.

### PC Build Components

Each component table uses `product_id` as its primary key and foreign key to `product`.

- `cpu`
- `gpu`
- `mainboard`
- `memory`
- `storage`
- `pc_case`
- `power_supply`
- `cooling`

### Cart, Order, Payment

- `cart`: one shopping cart per account.
- `cart_item`: selected items, including build item metadata.
- `orders`: order header, shipping snapshot, fulfillment timestamps, payment snapshot.
- `order_detail`: product line items and captured item price.
- `payments`: PayOS/payment transaction records linked to `orders`.

### Feedback

- `feedback`: customer product comments, ratings, moderation status, staff reply.

Chat has no database tables in the clean schema.

### External Product Source

- `external_product_source`: tracks external provider/source identifiers for imported or matched products.

## Deploy Behavior

For a fresh database, the application creates the schema by running Flyway automatically on startup. The Docker deploy compose file creates the MySQL database, then the Spring Boot app applies migrations.

If the old local database already has earlier Flyway history, reset the database or Docker volume before treating this migration set as the clean baseline for deployment. Do not run this squashed set against a database that already applied the old `V1..V21` files.
