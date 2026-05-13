
## Kiểu kiến trúc đang dùng

  

Đây là kiến trúc layered tương đối cổ điển:

  

- Controller: nhận request, bind param, render view hoặc trả JSON

- Service: business logic

- Repository: persistence/query

- Model/entity: ánh xạ DB

- DTO: payload trung gian cho các use case phù hợp

- Template/static: render UI

  

Không nên ép dự án này sang style mới như:

  

- frontend SPA toàn phần

- REST-first cho toàn hệ thống

- domain rewrite lớn

  

trừ khi có yêu cầu rõ ràng.

  

## Boundary trách nhiệm

  

### Controller nên làm gì

  

- khai báo route

- nhận query param, path variable, form data

- chuyển dữ liệu vào service

- gắn attribute vào `Model`

- quyết định redirect hay render template

- compose HTTP response nếu là API

  

### Controller không nên làm gì

  

- chứa business rule phức tạp

- chứa compatibility logic

- chứa inventory logic

- tự xử lý query database rải rác

  

### Service nên làm gì

  

- thực thi nghiệp vụ

- validate các rule ở mức use case

- điều phối repository

- quản lý transaction

- đảm bảo consistency giữa các module

  

### Repository nên làm gì

  

- query dữ liệu

- projection/fetch graph nếu cần

- method naming theo domain

  

Repository không phải nơi nhét business logic.

  

## Naming và style

  

Dự án dùng style pha trộn:

  

- có chỗ constructor injection viết tay

- có chỗ dùng Lombok `@RequiredArgsConstructor`

- entity dùng `@Data`

- controller/service đôi khi dùng tiếng Anh cho code, tiếng Việt cho comment/status

  

Khi sửa code:

  

- nên match style cục bộ của file đang động vào

- không refactor rộng nếu không cần cho bug/feature hiện tại

  

## MVC-first

  

Phần lớn flow đi theo:

  

1. User request route.

2. Controller gọi service.

3. Service dùng repository lấy dữ liệu.

4. Controller đưa dữ liệu vào model.

5. Thymeleaf render HTML.

  

Các area API hiện có:

  

- `/api/build/**`

- một số endpoint JSON cho payment info

  

## Domain slices chính

  

### Product

  

- product

- brand

- category

- image

- build component spec liên kết với product

  

### Build

  

- chọn linh kiện từng bước

- lưu trạng thái tạm qua session/build DTO

- rule-based suggestion

- compatibility filtering

  

### Order/Payment

  

- checkout

- order detail

- payment record

- PayOS callback/webhook

- scheduler cleanup

  

### Auth/Account

  

- register

- verify

- login

- password reset

- profile

- address

  

### Staff/Admin

  

- dashboard

- product management

- brand management

- shipping

- feedback reply

- customer/staff management

  

## Quy tắc quan trọng đang tồn tại trong code

  

### Storefront product rule

  

Storefront chỉ nên hiện product khi:

  

- `status = true`

- `lifecycleStatus = SELLING`

  

Rule này được encode rõ trong `ProductService` và `ProductRepository`.

  

### Product management rule

  

Staff-side có thể thao tác product ở nhiều trạng thái hơn storefront.

  

### Build package rule

  

Có một cây package build đang hoạt động:

  

- `controller.build`

- `service.build`

- `repository.build`

- `model.build`

  

Không nên tạo thêm cây build song song mới.

  

### Schema change rule

  

Mọi thay đổi DB nên qua Flyway migration mới.

  

## Luồng UI chung

  

Thymeleaf dùng nhiều fragment:

  

- header theo role

- footer

- build sidebar

  

Điều này giúp đổi layout theo từng vai trò mà vẫn giữ MVC.

  

## Trade-off thực tế của codebase

  

### Ưu điểm

  

- dễ đọc nếu quen Spring MVC

- domain business nằm khá rõ trong service

- flow storefront và quản trị tách tương đối rõ

- build compatibility đã có nền tảng

  

### Hạn chế

  

- naming/status string chưa chuẩn hóa hoàn toàn

- UI framework bị trộn

- một số service/controller còn nhiều logic procedural

- có dấu hiệu tech debt ở các module thử nghiệm

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[03 - Runtime Config and Security]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[12 - System Mindmap]]