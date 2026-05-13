  

## Phạm vi note

  

Gồm các module không phải core catalog/build/order nhưng vẫn quan trọng:

  

- auth/account/profile/address

- customer/staff/dashboard

- feedback

- blog scrape

- AI chat

- UI/template/static organization

  

## Auth, account, profile, address

  

### Controller

  

- `AuthController`

- `CustomerProfileController`

- `AddressController`

  

### Service

  

- `AuthService`

- `RegistrationService`

- `AccountService`

- `AddressService`

- `VerificationService`

- `PasswordResetService`

- `MailService`

  

## Đăng ký tài khoản

  

Flow:

  

1. `GET /auth/register` mở form

2. `POST /auth/register` validate confirm password

3. `AuthService.register(...)`

4. `RegistrationService.register(...)`

5. encode password

6. set role `Customer`

7. set `enabled = false`

8. lưu default address nếu có

9. redirect sang verify email

  

## Verify account

  

Flow:

  

1. `GET /auth/verify?email=...`

2. gửi mã verify

3. `POST /auth/verify`

4. nếu code đúng thì `enabled = true`

  

### Lưu ý

  

Mã verify đang lưu trong `Map<String, String>` in-memory.

  

## Quên mật khẩu

  

Flow:

  

1. `GET /auth/forget-password`

2. `POST /auth/forget-password`

3. gửi mã reset theo email hoặc phone

4. `GET /auth/code-forget-password`

5. `POST /auth/code-forget-password`

6. `GET /auth/reset-password`

7. `POST /auth/reset-password`

  

### Lưu ý

  

Mã reset cũng lưu trong memory map, chưa có persistence/expiry cứng ở DB.

  

## Profile

  

`CustomerProfileController` phụ trách:

  

- xem profile

- update profile

  

`AuthController` còn xử lý:

  

- `/profile/change-password`

  

## Customer và staff management

  

### Controller

  

- `CustomerController`

- `StaffController`

- `DashboardController`

  

### Chức năng

  

- list/view/add customer

- list/view/add/edit/delete staff

- admin dashboard

- staff dashboard

  

## Dashboard

  

### Admin dashboard

  

Hiển thị:

  

- total users

- total staff

- total orders

- total products

- revenue tính từ tổng order details

  

### Staff dashboard

  

Hiển thị:

  

- pending orders

- shipped orders

- products in stock

- feedback count

  

### Lưu ý

  

Một số status trong dashboard như `PENDING`, `SHIPPED` có thể không đồng nhất hoàn toàn với các status string ở order/payment flow khác.

  

## Feedback module

  

### Controller

  

- `FeedbackController`

- `ProductFeedbackController`

  

### Service

  

- `FeedbackService`

- `FeedbackServiceImpl`

  

### Chức năng

  

- staff xem list feedback

- staff xem detail feedback

- staff reply feedback

- customer tạo feedback cho product

- product detail hiển thị feedback allowed

  

### Rule đáng chú ý

  

Customer chỉ được feedback nếu đã mua product với order status `Completed`.

  

Ngoài ra code còn:

  

- lọc từ cấm theo regex đơn giản

- set `commentStatus = "Allow"`

  

## Blog module

  

### Controller

  

- `ExternalBlogController`

  

### Service

  

- `HacomScraperService`

  

### Flow

  

- `/blog` redirect sang `/blog/hacom`

- scrape `https://hacom.vn/tin-tuc`

- cache trong memory

- refresh định kỳ mỗi 10 phút

  

## AI chat module

  

### Controller

  

- `AIChatController`

  

### Service

  

- `AIChatService`

  

### Hiện trạng

  

- room được lưu tạm trong `Map<Integer, ChatRoom>`

- message từ AI gọi qua `RestTemplate`

- endpoint và API key đang hardcode

  

> [!warning]

> Module này nhìn giống prototype hơn là implementation production-ready.

  

## UI organization

  

### Template folders

  

- `auth`

- `blog`

- `brand`

- `build`

- `cart`

- `chat`

- `customer`

- `feedback`

- `layout`

- `orders`

- `product`

- `profile`

- `staff`

- `staffshipping`

- `warranty`

  

### Layout fragments

  

- `layout/header-default.html`

- `layout/header-admin.html`

- `layout/header-staff.html`

- `layout/header-customer.html`

- `layout/footer.html`

- `layout/build-sidebar.html`

  

### Static assets

  

CSS:

  

- `main.css`

- `home.css`

- `BuildStyle.css`

- `cart.css`

- `OrderStyle.css`

- `ShippingStyle.css`

- `StaffStyle.css`

- `WarrantyStyle.css`

- `Feedback.css`

  

JS:

  

- `BuildJs.js`

- `suggest.js`

- `cart.js`

- `checkout.js`

- `register.js`

- `Staff.js`

  

### Tình trạng UI hiện tại

  

- có Bootstrap local

- có Bootstrap CDN ở một số template

- có Tailwind CDN ở một số template

- header/footer chứa nhiều style inline

- encoding ở vài template/comment đang có dấu hiệu lỗi mojibake

  

### Kết luận UI

  

Không bị khoá cứng bởi framework, nhưng đang thiếu chuẩn hóa asset/layout.

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[02 - Architecture and Conventions]]

- [[03 - Runtime Config and Security]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[09 - Route Map]]

- [[10 - File Inventory]]

- [[12 - System Mindmap]]