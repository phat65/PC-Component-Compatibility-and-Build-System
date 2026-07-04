# Database Schema

Flyway migrations in `src/main/resources/db/migration` are the schema source of truth. Do not edit applied migrations after a shared database has used them; add a new `V...__description.sql` migration instead.

Current migration head: `V20__drop_unused_chat_tables.sql`.

## Core Tables

### Account

- `account`: login identity, profile fields, role, enabled status.
- `account_address`: saved customer addresses, linked to `account`.

### Product Catalog

- `brand`: product brand metadata.
- `category`: product categories, including optional `parent_id` for grouped categories.
- `product`: catalog item, price, visibility status, inventory, performance score, lifecycle status.
- `product_category`: many-to-many product/category mapping.
- `image`: product image URLs served through `/image/...`.

Storefront visibility depends on both `product.status = true` and `product.lifecycle_status = 'SELLING'`.

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

Chat currently keeps room/message state in application memory and has no database tables.

### External Product Source

- `external_product_source`: tracks external provider/source identifiers for imported or matched products.

## Deploy Behavior

For a fresh database, the application creates the schema by running Flyway automatically on startup. The Docker deploy compose file creates the MySQL database, then the Spring Boot app applies migrations.
