
## Maven và dependency

  

File nguồn: `pom.xml`

  

Dependency chính:

  

- `spring-boot-starter-data-jpa`

- `spring-boot-starter-thymeleaf`

- `thymeleaf-extras-springsecurity6`

- `spring-boot-starter-web`

- `spring-boot-starter-validation`

- `mysql-connector-j`

- `flyway-core`

- `flyway-mysql`

- `spring-boot-starter-actuator`

- `spring-boot-starter-security`

- `spring-boot-starter-mail`

- `payos-java`

- `jsoup`

- `lombok`

  

## Application config

  

File chính: `src/main/resources/application.yml`

  

Thông tin đang thấy trực tiếp trong file:

  

- `spring.application.name = pconlineshop`

- profile mặc định là `dev`

- datasource driver là MySQL

- `ddl-auto = none`

- Flyway bật

- mail SMTP bật auth và starttls

- session cookie `http-only: true`

- session cookie `same-site: lax`

  

## Web MVC config

  

File: `config/WebConfig.java`

  

### Chức năng chính

  

- map `/image/**` tới thư mục vật lý `./uploads/images/`

- khai báo `ObjectMapper` bean

  

## PayOS config

  

File: `config/PayOSConfig.java`

  

Bind các property:

  

- `spring.payos.client-id`

- `spring.payos.api-key`

- `spring.payos.checksum-key`

  

sau đó tạo bean `PayOS`.

  

## Security config

  

File: `config/auth/SecurityConfig.java`

  

### Password

  

- dùng `BCryptPasswordEncoder`

  

### Login form

  

- login page: `/auth/login`

- login processing url: `/auth/login`

- username param: `phoneNumber`

- password param: `password`

  

### CSRF

  

- dùng `CookieCsrfTokenRepository.withHttpOnlyFalse()`

- bỏ qua CSRF cho `POST /payment/webhook`

- có filter phụ để đảm bảo token được materialize vào cookie

  

### Success handling sau login

  

- `STAFF` -> `/dashboard/staff`

- `ADMIN` -> `/dashboard/admin`

- customer -> ưu tiên `redirect` param nếu safe, nếu không về `/home`

  

### Logout

  

- `POST /auth/logout`

- invalidate session

- clear authentication

- delete `JSESSIONID`

  

## Authorization map mức cao

  

### Public

  

- `/`

- `/auth/**`

- `/products/**`

- GET `/payment/callback/**`

- POST `/payment/webhook`

- `/assets/**`, `/css/**`, `/js/**`, `/image/**`, `/images/**`, `/static/**`, `/webfonts/**`, `/uploads/**`, `/error`

- `/blog/**`

- `/chat/**`

  

### Chỉ customer

  

- `/cart/**`

- `/checkout/**`

  

### Customer, staff, admin

  

- `/orders/list`

- `/orders/detail/**`

- `/payment/info/**`

- `/payment/continue/**`

  

### Staff hoặc admin

  

- `/staff/products/**`

- `/staff/warranty/**`

- `/staff/shipping/**`

- `/orders/update-all-status`

  

### Chỉ admin

  

- `/staff/list/**`

- `/staff/add/**`

- `/staff/edit/**`

- `/staff/view/**`

- `/staff/delete/**`

  

## AccountUserDetails

  

File: `service/auth/AccountUserDetailsService.java`

  

Behavior:

  

- load user theo `phoneNumber`

- lấy `Account` từ `AccountRepository`

- wrap thành `AccountUserDetails`

  

## Session và redirect safety

  

Security có method `isSafeRedirectUrl`:

  

- redirect phải bắt đầu bằng `/`

- không được bắt đầu bằng `//`

  

## Scheduling và async

  

Entry point bật:

  

- `@EnableScheduling`

- `@EnableAsync`

  

Module dùng async rõ ràng:

  

- gửi mã verify qua email

- gửi mã reset password

  

Module dùng scheduling rõ ràng:

  

- `OrderScheduler`

- `HacomScraperService.scheduledRefresh()`

  

## Các rủi ro đáng chú ý

  

> [!warning]

> Đây là các điểm nên note khi maintain, không phải khẳng định lỗi production chắc chắn.

  

- `AIChatService` đang hardcode API key và endpoint mẫu, không phù hợp production.

- `VerificationService` và `PasswordResetService` lưu mã xác thực trong `Map` in-memory, nên mất khi restart và không có TTL thực sự ở tầng dữ liệu.

- Có sự pha trộn role string như `ADMIN/STAFF/CUSTOMER` và enum `RoleName`.

- Status order/payment phần lớn đang dùng string literals, dễ tạo drift giữa module.

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[02 - Architecture and Conventions]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[12 - System Mindmap]]