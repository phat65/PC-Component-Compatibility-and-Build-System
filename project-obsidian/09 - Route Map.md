
## Mục đích

  

Note này gom các route quan trọng theo controller để tra nhanh khi maintain.

  

## HomeController

  

Base: không có class-level request mapping

  

- `GET /` -> landing page

- `GET /home` -> home page

- `GET /products` -> catalog page

- `GET /products/{id}` -> product detail

  

## AuthController

  

Base: `/auth`

  

- `GET /register`

- `POST /register`

- `GET /verify`

- `POST /verify`

- `GET /login`

- `GET /forget-password`

- `POST /forget-password`

- `GET /code-forget-password`

- `POST /code-forget-password`

- `GET /reset-password`

- `POST /reset-password`

- `POST /profile/change-password`

  

## CustomerProfileController

  

Base: `/profile`

  

- `GET /profile`

- `POST /profile/update`

  

## AddressController

  

Base: không có class-level mapping

  

- `POST /address/add`

- `POST /address/update`

- `POST /address/set-default`

  

## ProductController

  

Base: `/staff/products`

  

- `GET /list`

- `GET /add`

- `GET /spec-form`

- `GET /spec-form-edit`

- `POST /save`

- `GET /edit/{id}`

- `POST /edit`

- `POST /{id}/hide`

- `DELETE /image/{imageId}`

  

## ProductDetailController

  

Base: `/product`

  

- `GET /detail/{id}`

  

## ProductFeedbackController

  

Base: `/products`

  

- `POST /{id}` -> tạo feedback cho product

  

## CategoryController

  

Base: `/category`

  

- `GET /{id}`

  

## BrandController

  

Base: `/admin/brand`

  

- `GET /list`

- `GET /add`

- `POST /add`

- `GET /update/{id}`

- `POST /update/{id}`

- `POST /{id}/status`

- `GET /merge`

- `POST /merge`

  

## BuildController

  

Base: `/build`

  

- `GET /start`

- `GET /preset-result`

- `GET /startover`

- `GET /finish`

  

## Build component controllers

  

Base chung: `/build`

  

### MainboardController

  

- `GET /mainboard`

- `POST /mainboard/filter`

- `GET /mainboard/{id}`

- `POST /selectMainboard`

  

### CpuController

  

- `GET /cpu`

- `POST /cpu/filter`

- `POST /selectCpu`

  

### GpuController

  

- `GET /gpu`

- `POST /gpu/filter`

- `POST /selectGpu`

  

### MemoryController

  

- `GET /memory`

- `POST /memory/filter`

- `POST /selectMemory`

  

### StorageController

  

- `GET /storage`

- `POST /storage/filter`

- `POST /selectStorage`

  

### PowerSupplyController

  

- `GET /psu`

- `POST /psu/filter`

- `POST /selectPsu`

  

### CoolingController

  

- `GET /cooling`

- `POST /cooling/filter`

- `POST /selectCooling`

  

### CaseController

  

- `GET /case`

- `POST /case/filter`

- `POST /selectCase`

  

### OtherController

  

- `GET /other`

- `POST /selectOther`

  

## BuildSuggestionController

  

Base: `/api/build`

  

- `GET /presets`

- `POST /suggest`

- `POST /apply`

  

## CartController

  

Base: `/cart`

  

- `GET /cart`

- `POST /add/{productId}`

- `GET /addListItem`

- `POST /update/{cartItemId}`

- `POST /remove/{cartItemId}`

- `POST /select/{cartItemId}`

- `POST /deselect/{cartItemId}`

- `POST /clear`

  

## OrderController

  

Base: `/orders`

  

- `GET /list`

- `GET /detail/{id}`

- `GET /checkout`

- `POST /checkout`

  

## PaymentController

  

Base: `/payment`

  

- `GET /callback/success`

- `GET /callback/failed`

- `GET /continue/{orderId}`

- `POST /webhook`

- `GET /info/{orderId}`

  

## FeedbackController

  

Base: `/staff/feedback`

  

- `GET /staff/feedback`

- `GET /staff/feedback/{id}`

- `POST /staff/feedback/{id}/reply`

  

## CustomerController

  

Base: `/customer`

  

- `GET /list`

- `GET /view/{id}`

- `GET /add`

- `POST /add`

  

## StaffController

  

Base: `/staff`

  

- `GET /list`

- `GET /view/{id}`

- `GET /add`

- `POST /add`

- `GET /edit/{id}`

- `POST /edit`

- `GET /delete/{id}`

  

## StaffShippingController

  

Base: `/staff/shipping`

  

- `GET /list`

- `POST /update-status/{orderId}`

  

## StaffWarrantyController

  

Base: `/staff/warranty`

  

- `GET /check`

- `POST /search`

  

## ExternalBlogController

  

Base: `/blog`

  

- `GET /blog/hacom`

- `GET /blog`

  

## AIChatController

  

Base: `/chat`

  

- `GET /chat/{roomId}`

- `POST /chat/send`

  

## DashboardController

  

Base: không có class-level request mapping

  

- `GET /dashboard/admin`

- `GET /dashboard/staff`

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[02 - Architecture and Conventions]]

- [[03 - Runtime Config and Security]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[10 - File Inventory]]

- [[12 - System Mindmap]]