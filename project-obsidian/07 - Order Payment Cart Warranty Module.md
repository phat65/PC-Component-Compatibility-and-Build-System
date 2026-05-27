  
## Phạm vi module

  

Note này gom các chức năng liên quan chặt với nhau:

  

- cart

- checkout

- order

- payment

- shipping status

- warranty

- scheduler cleanup

  

## Thành phần chính

  

### Controller

  

- `CartController`

- `OrderController`

- `PaymentController`

- `StaffShippingController`

- `StaffWarrantyController`

  

### Service

  

- `CartService`

- `OrderService`

- `PaymentService`

- `AddressService`

  

### Repository

  

- `CartRepository`

- `CartItemRepository`

- `OrderRepository`

- `OrderDetailRepository`

- `PaymentRepository`

  

### DTO

  

- `CartItemDTO`

- `CartSummaryDTO`

- `CheckoutDTO`

- `CheckoutPageDTO`

- `OrderSearchRequest`

- `PaymentInfoDTO`

- `WarrantyDetailDTO`

  

## Cart flow

  

Base route:

  

- `/cart`

  

Các action:

  

- xem giỏ

- thêm sản phẩm

- thêm list item

- update quantity

- remove item

- select/deselect item

- clear cart

  

Cart không chỉ là danh sách item, mà còn có flag:

  

- `is_selected`

- `is_build_item`

- `build_id`

  

## Checkout flow

  

Controller: `OrderController`

  

Route chính:

  

- `GET /orders/checkout`

- `POST /orders/checkout`

  

### `OrderService.prepareCheckoutData(...)`

  

Làm các việc:

  

- lấy cart items đã selected

- tính tổng tiền

- load address của account

- chọn default address

- prefill `CheckoutDTO`

  

### `OrderService.processCheckout(...)`

  

- lấy map item dùng để checkout

- validate không rỗng

- tạo order

  

## Tạo order

  

Method: `OrderService.createOrder(...)`

  

Process:

  

1. tạo `Order`

2. gắn account

3. set `status = "Pending Payment"`

4. snapshot shipping info

5. tạo `OrderDetail` từ cart item

6. tính `finalAmount`

7. save order

  

## Payment flow

  

Controller: `PaymentController`

  

Base route:

  

- `/payment`

  

Các route chính:

  

- `/callback/success`

- `/callback/failed`

- `/continue/{orderId}`

- `/webhook`

- `/info/{orderId}`

  

## `PaymentService`

  

Đây là service trung tâm của integration PayOS.

  

### `createPaymentRecord(Order order)`

  

- tạo record `Payment`

- status `PENDING`

  

### `createPayOSLink(Payment payment)`

  

Làm các bước:

  

1. tạo `orderCode` unique bằng timestamp

2. build description, returnUrl, cancelUrl

3. convert `OrderDetail` thành `PaymentLinkItem`

4. gọi PayOS tạo payment link

5. lưu `gatewayPaymentId`

6. trả checkout URL

  

### `getOrRegeneratePaymentUrl(orderId)`

  

- tìm payment theo order

- chỉ cho tiếp tục nếu order còn `Pending Payment`

- cố query trạng thái từ PayOS

- nếu chưa paid thì regenerate link

  

### `handleWebhook(Object body)`

  

Nếu webhook code là `"00"`:

  

- payment status -> `SUCCESS`

- lưu `gatewayPaymentId`

- lưu `rawPayload`

- order payment status -> `PAID`

- order status -> `Ready to Ship`

- set `paidAt`

  

Nếu không thành công:

  

- nếu payment còn `PENDING` thì gọi cancel order

  

## Inventory rollback

  

Method: `OrderService.rollBackInventory(Order order)`

  

Khi cancel order:

  

- duyệt `OrderDetail`

- cộng lại `inventoryQuantity` cho từng product

- save product

  

## Cancel order từ payment

  

Method: `OrderService.cancelOrderFromPaymentId(long paymentId)`

  

Chỉ áp dụng khi:

  

- order status đang `Pending Payment`

- payment status đang `PENDING`

  

Sau đó:

  

- payment -> `CANCELLED`

- order -> `Cancelled`

- order.paymentStatus -> `CANCELLED`

- rollback inventory

- nếu có `orderCode` thì gọi PayOS cancel

  

## Shipping flow

  

Controller: `StaffShippingController`

  

Route:

  

- `GET /staff/shipping/list`

- `POST /staff/shipping/update-status/{orderId}`

  

### Transition rule

  

Hệ thống cho phép:

  

- `Ready to Ship -> Delivering | Completed | Cancelled`

- `Delivering -> Completed | Cancelled | Delivery Failed`

  

Nếu transition sai sẽ throw `IllegalArgumentException`.

  

## Warranty flow

  

Controller: `StaffWarrantyController`

  

Route:

  

- `GET /staff/warranty/check`

- `POST /staff/warranty/search`

  

Warranty không có bảng riêng, mà được derive từ:

  

- order

- order detail

- category của product

  

### Warranty months map

  

`OrderService` đang giữ map hardcoded `categoryId -> số tháng bảo hành`.

  

Khi tra cứu:

  

1. lấy order theo phone number

2. lấy order detail

3. lấy category đầu tiên của product

4. cộng số tháng bảo hành

5. suy ra trạng thái:

   - `Active`

   - `Expiring Soon`

   - `Expired`

  

## Scheduler

  

File: `scheduler/OrderScheduler.java`

  

Chạy:

  

- `@Scheduled(fixedRate = 60000)`

  

Thực tế code đang trừ `3 phút`:

  

- `LocalDateTime.now().minusMinutes(3)`

  

Tức là comment và implementation chưa khớp hoàn toàn.

  

Scheduler sẽ:

  

1. tìm payment `PENDING` quá hạn

2. gọi `orderService.cancelOrderFromPaymentId(...)`

  

## Rủi ro/chú ý của module

  

- order/payment status dùng string literal, cần cẩn thận khi thêm trạng thái mới

- scheduler comment ghi `15 phút` nhưng code dùng `3 phút`

- callback success chỉ verify payment status rồi redirect, còn cập nhật trạng thái dựa vào webhook/service

- logic warranty đang phụ thuộc `categoryId` hardcoded

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[02 - Architecture and Conventions]]

- [[03 - Runtime Config and Security]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[10 - File Inventory]]

- [[12 - System Mindmap]]