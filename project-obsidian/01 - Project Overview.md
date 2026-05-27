# PCOnlineShop - Obsidian Index

  

> [!info]

> Bộ note này được viết để có thể copy thẳng vào Obsidian và dùng như một mini knowledge base cho dự án `Team3_SWP391_SE1968_NJ`.

  

## Cách đọc nhanh

  

- Bắt đầu từ [[01 - Project Overview]]

- Nếu muốn mở kiểu sơ đồ ngay trong Obsidian: [[11 - Graph View Start Here]]

- Nếu muốn xem mindmap ngay trong note: [[12 - System Mindmap]]

- Nếu cần hiểu kiến trúc và chuẩn code: [[02 - Architecture and Conventions]]

- Nếu cần hiểu config, bảo mật, session, static resource: [[03 - Runtime Config and Security]]

- Nếu cần hiểu DB, bảng, entity, migration: [[04 - Database and Data Model]]

- Nếu cần hiểu catalog và quản lý sản phẩm: [[05 - Product and Catalog Module]]

- Nếu cần hiểu tính năng build PC và logic tương thích: [[06 - Build PC Module]]

- Nếu cần hiểu giỏ hàng, checkout, thanh toán, đơn hàng, bảo hành: [[07 - Order Payment Cart Warranty Module]]

- Nếu cần hiểu các module phụ và UI: [[08 - Supporting Modules and UI]]

- Nếu cần tra route/controller theo màn hình: [[09 - Route Map]]

- Nếu cần danh sách file/path toàn dự án: [[10 - File Inventory]]

  

## Snapshot dự án

  

- Tên dự án: `PCOnlineShop`

- Kiểu ứng dụng: Spring Boot MVC + Thymeleaf, có thêm một số endpoint JSON

- Package gốc: `com.example.PCOnlineShop`

- Java: `21`

- Spring Boot: `3.5.6`

- Build tool: `Maven`

- Database: `MySQL`

- Migration: `Flyway`

- Payment gateway: `PayOS`

- HTML rendering: `Thymeleaf`

- Scheduling: bật

- Async execution: bật

  

## Tổng số artifact chính

  

- Controller: `31`

- Service: `36`

- Repository: `21`

- Model/entity: `24`

- DTO: `15`

- Template Thymeleaf: `64`

- Static asset: `23`

- Flyway migration: `14`

  

## Luồng đọc khuyến nghị cho người mới vào dự án

  

1. Đọc [[01 - Project Overview]] để hiểu mục tiêu và domain.

2. Đọc [[02 - Architecture and Conventions]] để nắm style và boundary.

3. Đọc [[03 - Runtime Config and Security]] để nắm startup, security, resource mapping.

4. Đọc [[04 - Database and Data Model]] để hiểu schema và entity.

5. Đọc [[05 - Product and Catalog Module]] và [[06 - Build PC Module]] vì đây là core business.

6. Đọc [[07 - Order Payment Cart Warranty Module]] để hiểu flow mua hàng.

7. Dùng [[09 - Route Map]] và [[10 - File Inventory]] để tra cứu nhanh khi code.

  

## Ghi chú quan trọng

  

- Dự án này không phải REST-only.

- Nhiều tính năng chính đang đi theo `Spring MVC -> Service -> Repository -> Thymeleaf`.

- Không nên tự ý đổi toàn bộ sang SPA/API-first nếu không có yêu cầu rõ ràng.

- Storefront product phải tôn trọng rule `status = true` và `lifecycleStatus = SELLING`.

  

## Graph Entry Points

  

- [[11 - Graph View Start Here]]

- [[12 - System Mindmap]]

- `PCOnlineShop Project Map.canvas`

  

## Related Notes

  

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