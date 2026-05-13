
## Tổng quan schema

DB chính theo note dự án là `pconlineshop`.

Flyway migration nằm ở:

`src/main/resources/db/migration`

Các migration hiện có:

- `V1__create_account_tables.sql`
- `V2__create_product_tables.sql`
- `V3__create_order_tables.sql`
- `V4__create_chat_tables.sql`
- `V5__create_feedback_tables.sql`
- `V6__seed_account.sql`
- `V7__seed_product.sql`
- `V8__seed_order.sql`
- `V9__seed_feedback.sql`
- `V10__create_external_product_source_table.sql`
- `V11__rename_corsair_provider_code_to_algolia.sql`
- `V12__add_product_lifecycle_status.sql`
- `V13__normalize_seed_accounts.sql`
- `V14__seed_missing_images_for_other_products.sql`

## Nhóm bảng chính

### Account

Từ `V1`:

- `account`
- `account_address`

Entity tương ứng:

- `model/account/Account.java`
- `model/account/Address.java`

### Product

Từ `V2`:

- `brand`
- `category`
- `product`
- `product_category`
- `cpu`
- `gpu`
- `mainboard`
- `memory`
- `storage`
- `pc_case`
- `power_supply`
- `cooling`
- `image`

### Order, cart, payment

Từ `V3`:

- `cart`
- `cart_item`
- `orders`
- `payments`
- `order_detail`

### Chat

Từ `V4`:

- `ChatRoom`
- `ChatMessage`

### Feedback

Từ `V5`:

- `feedback`

### External source

Từ `V10`:

- `external_product_source`

## Entity map

## `Account`

Các field nổi bật:

- `accountId`
- `phoneNumber`
- `password`
- `role`
- `email`
- `firstname`
- `lastname`
- `gender`
- `enabled`
- `addresses`

Method nghiệp vụ phụ:

- `getFullName()`
- `getDefaultAddress()`

## `Product`

Các field nổi bật:

- `productId`
- `productName`
- `price`
- `status`
- `lifecycleStatus`
- `description`
- `specification`
- `createAt`
- `inventoryQuantity`
- `performanceScore`
- `categories`
- `brand`
- `images`

Method nghiệp vụ phụ:

- `isVisibleOnStorefront()`
- `isSelling()`
- `isSellableOnStorefront()`

### Product lifecycle

Enum: `ProductLifecycleStatus`

Các trạng thái quan trọng thấy trực tiếp/gián tiếp trong code:

- `SELLING`
- `DISCONTINUED`
- `DRAFT`

Migration `V12` thêm cột `lifecycle_status` và backfill dữ liệu cũ.

## `Order`

Các field nổi bật:

- `orderId`
- `account`
- `finalAmount`
- `status`
- `createdDate`
- `shippingMethod`
- `note`
- `shippingFullName`
- `shippingPhone`
- `shippingAddress`
- `readyToShipDate`
- `shipmentReceivedDate`
- `orderDetails`
- `paymentId`
- `paymentStatus`
- `paidAt`

## `Payment`

Các field nổi bật:

- `paymentId`
- `order`
- `gatewayPaymentId`
- `amount`
- `status`
- `rawPayload`
- `createdAt`
- `orderCode`

## `Cart`

Các field nổi bật:

- `cartId`
- `account`
- `status`
- `updatedDate`
- `items`

## Build component entities

Mỗi component build dùng `product_id` làm PK đồng thời tham chiếu tới `product`.

### Ví dụ `CPU`

Field:

- `productId`
- `socket`
- `tdp`
- `maxMemorySpeed`
- `memoryChannels`
- `hasIGPU`
- `pcieVersion`
- `product`

Pattern này lặp lại cho:

- GPU
- Mainboard
- Memory
- Storage
- Case
- PowerSupply
- Cooling

## Repository đáng chú ý

### `ProductRepository`

Chứa:

- query theo brand/category
- fetch images
- search với filter
- visible storefront filter theo `status + lifecycleStatus`
- random related products
- reassign brand khi merge brand

### `OrderRepository`

Chứa:

- query order theo role
- query theo phone number
- shipping queue query
- đếm order theo status

### `PaymentRepository`

Chứa:

- tìm payment pending cũ hơn mốc thời gian
- tìm theo `orderId`
- tìm theo `orderCode`

## Sơ đồ quan hệ khái niệm

```text
Account 1---n Address
Account 1---1 Cart
Cart 1---n CartItem
CartItem n---1 Product

Product n---1 Brand
Product n---n Category
Product 1---n Image

Product 1---0..1 CPU
Product 1---0..1 GPU
Product 1---0..1 Mainboard
Product 1---0..1 Memory
Product 1---0..1 Storage
Product 1---0..1 Case
Product 1---0..1 PowerSupply
Product 1---0..1 Cooling

Account 1---n Order
Order 1---n OrderDetail
OrderDetail n---1 Product
Order 1---n Payment

Account 1---n Feedback
Product 1---n Feedback
```

## Ghi chú migration evolution

### `V1`

- tạo `account`, `account_address`

### `V2`

- tạo product catalog
- tạo build component tables
- tạo image

### `V3`

- thêm cart, orders, payments, order_detail
- thêm cột `is_selected`, `is_build_item`, `build_id` vào `cart_item`

### `V10`

- thêm bảng external product source cho mapping với provider ngoài

### `V12`

- thêm `lifecycle_status` cho product
- backfill dữ liệu cũ sang `SELLING`, `DISCONTINUED`, `DRAFT`

## Related Notes

- [[00 - Index]]
- [[01 - Project Overview]]
- [[02 - Architecture and Conventions]]
- [[03 - Runtime Config and Security]]
- [[05 - Product and Catalog Module]]
- [[06 - Build PC Module]]
- [[07 - Order Payment Cart Warranty Module]]
- [[08 - Supporting Modules and UI]]
- [[09 - Route Map]]
- [[12 - System Mindmap]]
