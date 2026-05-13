  
> [!tip]

> Nếu mục tiêu là mở Obsidian và thấy dự án thành sơ đồ ngay, bắt đầu từ note này hoặc mở file `PCOnlineShop Project Map.canvas`.

  

## Cách dùng trong Obsidian

  

### Cách 1: Canvas

  

Mở file:

  

- `PCOnlineShop Project Map.canvas`

  

Bạn sẽ thấy sơ đồ node đã được sắp sẵn theo cụm:

  

- overview

- kiến trúc

- config/security

- database

- product

- build

- order/payment

- supporting modules

- route map

  

### Cách 2: Graph View

  

1. Mở note này.

2. Mở `Local Graph`.

3. Bật hiển thị `Links`.

4. Tăng `Depth` lên `2` hoặc `3`.

5. Có thể group bằng `tags` hoặc pin các note trung tâm.

  

## Hub Notes

  

Các note nên dùng làm node trung tâm:

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[02 - Architecture and Conventions]]

- [[03 - Runtime Config and Security]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[10 - File Inventory]]

- [[12 - System Mindmap]]

  

## Sơ đồ logic khuyến nghị

  

```text

Index

 ├─ Overview

 ├─ Architecture

 ├─ Runtime/Security

 ├─ Database

 ├─ Product

 ├─ Build PC

 ├─ Order/Payment

 ├─ Supporting Modules

 ├─ Route Map

 └─ File Inventory

```

  

## Cross-links quan trọng

  

- [[05 - Product and Catalog Module]] <-> [[04 - Database and Data Model]]

- [[06 - Build PC Module]] <-> [[05 - Product and Catalog Module]]

- [[07 - Order Payment Cart Warranty Module]] <-> [[04 - Database and Data Model]]

- [[07 - Order Payment Cart Warranty Module]] <-> [[03 - Runtime Config and Security]]

- [[08 - Supporting Modules and UI]] <-> [[03 - Runtime Config and Security]]

- [[09 - Route Map]] <-> toàn bộ module notes

  

## Mở rộng nếu muốn graph dày hơn

  

Nếu cần, tôi có thể tách tiếp thành note atom nhỏ hơn:

  

- từng controller

- từng service

- từng entity

- từng bảng DB

- từng template group

  

Lúc đó graph sẽ dày và rõ “cụm nấm” hơn rất nhiều.

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[12 - System Mindmap]]