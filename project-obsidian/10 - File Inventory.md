
## Mục đích

  

Đây là appendix liệt kê file/path chính của dự án để tra cứu nhanh trong Obsidian.

  

## Root-level notable files

  

- `pom.xml`

- `compose.yaml`

- `compose.prod.example.yaml`

- `ENVIRONMENT.md`

- `agents.md`

- `.env.dev`

- `.env.dev.example`

- `.env.prod.example`

- `HashPasswords.java`

- `src/main/resources/application.yml`

  

## Java entry/config/constant/scheduler

  

- `src/main/java/com/example/PCOnlineShop/PcOnlineShopApplication.java`

- `src/main/java/com/example/PCOnlineShop/config/PayOSConfig.java`

- `src/main/java/com/example/PCOnlineShop/config/WebConfig.java`

- `src/main/java/com/example/PCOnlineShop/config/auth/AccountUserDetails.java`

- `src/main/java/com/example/PCOnlineShop/config/auth/SecurityConfig.java`

- `src/main/java/com/example/PCOnlineShop/constant/RoleName.java`

- `src/main/java/com/example/PCOnlineShop/scheduler/OrderScheduler.java`

  

## Controllers

  

- `src/main/java/com/example/PCOnlineShop/controller/address/AddressController.java`

- `src/main/java/com/example/PCOnlineShop/controller/auth/AuthController.java`

- `src/main/java/com/example/PCOnlineShop/controller/auth/CustomerProfileController.java`

- `src/main/java/com/example/PCOnlineShop/controller/blog/ExternalBlogController.java`

- `src/main/java/com/example/PCOnlineShop/controller/brand/BrandController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/BuildController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/BuildSuggestionController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/CaseController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/CoolingController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/CpuController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/GpuController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/MainboardController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/MemoryController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/OtherController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/PowerSupplyController.java`

- `src/main/java/com/example/PCOnlineShop/controller/build/StorageController.java`

- `src/main/java/com/example/PCOnlineShop/controller/cart/CartController.java`

- `src/main/java/com/example/PCOnlineShop/controller/category/CategoryController.java`

- `src/main/java/com/example/PCOnlineShop/controller/chat/AIChatController.java`

- `src/main/java/com/example/PCOnlineShop/controller/customer/CustomerController.java`

- `src/main/java/com/example/PCOnlineShop/controller/dashboard/DashboardController.java`

- `src/main/java/com/example/PCOnlineShop/controller/feedback/FeedbackController.java`

- `src/main/java/com/example/PCOnlineShop/controller/home/HomeController.java`

- `src/main/java/com/example/PCOnlineShop/controller/order/OrderController.java`

- `src/main/java/com/example/PCOnlineShop/controller/payment/PaymentController.java`

- `src/main/java/com/example/PCOnlineShop/controller/product/ProductController.java`

- `src/main/java/com/example/PCOnlineShop/controller/product/ProductDetailController.java`

- `src/main/java/com/example/PCOnlineShop/controller/product/ProductFeedbackController.java`

- `src/main/java/com/example/PCOnlineShop/controller/staff/StaffController.java`

- `src/main/java/com/example/PCOnlineShop/controller/staff/StaffShippingController.java`

- `src/main/java/com/example/PCOnlineShop/controller/warranty/StaffWarrantyController.java`

  

## Services

  

- `src/main/java/com/example/PCOnlineShop/service/account/AccountService.java`

- `src/main/java/com/example/PCOnlineShop/service/account/RegistrationService.java`

- `src/main/java/com/example/PCOnlineShop/service/address/AddressService.java`

- `src/main/java/com/example/PCOnlineShop/service/auth/AccountUserDetailsService.java`

- `src/main/java/com/example/PCOnlineShop/service/auth/AuthService.java`

- `src/main/java/com/example/PCOnlineShop/service/auth/EmailService.java`

- `src/main/java/com/example/PCOnlineShop/service/blog/HacomScraperService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/BuildService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/CaseService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/CompatibilityService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/CoolingService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/CpuService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/GpuService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/MainboardService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/MemoryService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/PerformanceScoreCalculator.java`

- `src/main/java/com/example/PCOnlineShop/service/build/PowerSupplyService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/RuleBasedBuildService.java`

- `src/main/java/com/example/PCOnlineShop/service/build/StorageService.java`

- `src/main/java/com/example/PCOnlineShop/service/cart/CartService.java`

- `src/main/java/com/example/PCOnlineShop/service/chat/AIChatService.java`

- `src/main/java/com/example/PCOnlineShop/service/customer/CustomerService.java`

- `src/main/java/com/example/PCOnlineShop/service/feedback/FeedbackService.java`

- `src/main/java/com/example/PCOnlineShop/service/feedback/FeedbackServiceImpl.java`

- `src/main/java/com/example/PCOnlineShop/service/mail/MailService.java`

- `src/main/java/com/example/PCOnlineShop/service/order/OrderService.java`

- `src/main/java/com/example/PCOnlineShop/service/password/PasswordResetService.java`

- `src/main/java/com/example/PCOnlineShop/service/payment/PaymentService.java`

- `src/main/java/com/example/PCOnlineShop/service/product/BrandService.java`

- `src/main/java/com/example/PCOnlineShop/service/product/CategoryService.java`

- `src/main/java/com/example/PCOnlineShop/service/product/ImageService.java`

- `src/main/java/com/example/PCOnlineShop/service/product/ProductService.java`

- `src/main/java/com/example/PCOnlineShop/service/product/SpecToCategoryMappingService.java`

- `src/main/java/com/example/PCOnlineShop/service/staff/StaffService.java`

- `src/main/java/com/example/PCOnlineShop/service/validation/ComponentSpecValidationService.java`

- `src/main/java/com/example/PCOnlineShop/service/verification/VerificationService.java`

  

## Repositories

  

- `src/main/java/com/example/PCOnlineShop/repository/account/AccountRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/account/AddressRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/CaseRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/CoolingRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/CpuRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/GpuRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/MainboardRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/MemoryRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/PowerSupplyRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/build/StorageRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/cart/CartItemRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/cart/CartRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/feedback/FeedbackRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/feedback/FeedbackSpecs.java`

- `src/main/java/com/example/PCOnlineShop/repository/order/OrderDetailRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/order/OrderRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/payment/PaymentRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/product/BrandRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/product/CategoryRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/product/ImageRepository.java`

- `src/main/java/com/example/PCOnlineShop/repository/product/ProductRepository.java`

  

## Models

  

- `src/main/java/com/example/PCOnlineShop/model/account/Account.java`

- `src/main/java/com/example/PCOnlineShop/model/account/Address.java`

- `src/main/java/com/example/PCOnlineShop/model/build/BuildPreset.java`

- `src/main/java/com/example/PCOnlineShop/model/build/Case.java`

- `src/main/java/com/example/PCOnlineShop/model/build/Cooling.java`

- `src/main/java/com/example/PCOnlineShop/model/build/CPU.java`

- `src/main/java/com/example/PCOnlineShop/model/build/GPU.java`

- `src/main/java/com/example/PCOnlineShop/model/build/Mainboard.java`

- `src/main/java/com/example/PCOnlineShop/model/build/Memory.java`

- `src/main/java/com/example/PCOnlineShop/model/build/PowerSupply.java`

- `src/main/java/com/example/PCOnlineShop/model/build/Storage.java`

- `src/main/java/com/example/PCOnlineShop/model/cart/Cart.java`

- `src/main/java/com/example/PCOnlineShop/model/cart/CartItem.java`

- `src/main/java/com/example/PCOnlineShop/model/chat/ChatMessage.java`

- `src/main/java/com/example/PCOnlineShop/model/chat/ChatRoom.java`

- `src/main/java/com/example/PCOnlineShop/model/feedback/Feedback.java`

- `src/main/java/com/example/PCOnlineShop/model/order/Order.java`

- `src/main/java/com/example/PCOnlineShop/model/order/OrderDetail.java`

- `src/main/java/com/example/PCOnlineShop/model/payment/Payment.java`

- `src/main/java/com/example/PCOnlineShop/model/product/Brand.java`

- `src/main/java/com/example/PCOnlineShop/model/product/Category.java`

- `src/main/java/com/example/PCOnlineShop/model/product/Image.java`

- `src/main/java/com/example/PCOnlineShop/model/product/Product.java`

- `src/main/java/com/example/PCOnlineShop/model/product/ProductLifecycleStatus.java`

  

## DTOs

  

- `src/main/java/com/example/PCOnlineShop/dto/ai/AiPcBuildCriteria.java`

- `src/main/java/com/example/PCOnlineShop/dto/ai/AiPcBuildRequest.java`

- `src/main/java/com/example/PCOnlineShop/dto/blog/BlogLinkDto.java`

- `src/main/java/com/example/PCOnlineShop/dto/build/BuildItemDto.java`

- `src/main/java/com/example/PCOnlineShop/dto/build/BuildPlanDto.java`

- `src/main/java/com/example/PCOnlineShop/dto/build/BuildRequestDto.java`

- `src/main/java/com/example/PCOnlineShop/dto/build/ComponentDto.java`

- `src/main/java/com/example/PCOnlineShop/dto/build/ComponentRule.java`

- `src/main/java/com/example/PCOnlineShop/dto/cart/CartItemDTO.java`

- `src/main/java/com/example/PCOnlineShop/dto/cart/CartSummaryDTO.java`

- `src/main/java/com/example/PCOnlineShop/dto/order/CheckoutDTO.java`

- `src/main/java/com/example/PCOnlineShop/dto/order/CheckoutPageDTO.java`

- `src/main/java/com/example/PCOnlineShop/dto/order/OrderSearchRequest.java`

- `src/main/java/com/example/PCOnlineShop/dto/payment/PaymentInfoDTO.java`

- `src/main/java/com/example/PCOnlineShop/dto/warranty/WarrantyDetailDTO.java`

  

## Templates

  

- `src/main/resources/templates/dashboard-admin.html`

- `src/main/resources/templates/dashboard-staff.html`

- `src/main/resources/templates/home.html`

- `src/main/resources/templates/landing.html`

- `src/main/resources/templates/auth/code-forget-password.html`

- `src/main/resources/templates/auth/forget-password.html`

- `src/main/resources/templates/auth/login.html`

- `src/main/resources/templates/auth/register.html`

- `src/main/resources/templates/auth/reset-password.html`

- `src/main/resources/templates/auth/verify.html`

- `src/main/resources/templates/blog/hacom-list.html`

- `src/main/resources/templates/brand/brand-form.html`

- `src/main/resources/templates/brand/brand-list.html`

- `src/main/resources/templates/brand/brand-merge.html`

- `src/main/resources/templates/brand/brand-status.html`

- `src/main/resources/templates/build/build-cpu.html`

- `src/main/resources/templates/build/build-gpu.html`

- `src/main/resources/templates/build/build-pc.html`

- `src/main/resources/templates/build/cases.html`

- `src/main/resources/templates/build/cooling.html`

- `src/main/resources/templates/build/mainboards.html`

- `src/main/resources/templates/build/memory.html`

- `src/main/resources/templates/build/other.html`

- `src/main/resources/templates/build/preset-result.html`

- `src/main/resources/templates/build/psu.html`

- `src/main/resources/templates/build/storage.html`

- `src/main/resources/templates/cart/view.html`

- `src/main/resources/templates/chat/chat.html`

- `src/main/resources/templates/customer/add-customer.html`

- `src/main/resources/templates/customer/customer-list.html`

- `src/main/resources/templates/customer/view-customer.html`

- `src/main/resources/templates/feedback/feedback-detail.html`

- `src/main/resources/templates/feedback/feedback-list.html`

- `src/main/resources/templates/layout/build-sidebar.html`

- `src/main/resources/templates/layout/footer.html`

- `src/main/resources/templates/layout/header-admin.html`

- `src/main/resources/templates/layout/header-customer.html`

- `src/main/resources/templates/layout/header-default.html`

- `src/main/resources/templates/layout/header-staff.html`

- `src/main/resources/templates/orders/checkout.html`

- `src/main/resources/templates/orders/order-detail.html`

- `src/main/resources/templates/orders/order-list.html`

- `src/main/resources/templates/product/category-products.html`

- `src/main/resources/templates/product/product-details.html`

- `src/main/resources/templates/product/product-form.html`

- `src/main/resources/templates/product/product-home.html`

- `src/main/resources/templates/product/product-list.html`

- `src/main/resources/templates/product/product-update.html`

- `src/main/resources/templates/product/specs/case-spec-form.html`

- `src/main/resources/templates/product/specs/cooling-spec-form.html`

- `src/main/resources/templates/product/specs/cpu-spec-form.html`

- `src/main/resources/templates/product/specs/fan-spec-form.html`

- `src/main/resources/templates/product/specs/gpu-spec-form.html`

- `src/main/resources/templates/product/specs/mainboard-spec-form.html`

- `src/main/resources/templates/product/specs/memory-spec-form.html`

- `src/main/resources/templates/product/specs/powersupply-spec-form.html`

- `src/main/resources/templates/product/specs/storage-spec-form.html`

- `src/main/resources/templates/profile/view-profile.html`

- `src/main/resources/templates/staff/add-staff.html`

- `src/main/resources/templates/staff/edit-staff.html`

- `src/main/resources/templates/staff/staff-list.html`

- `src/main/resources/templates/staff/view-staff.html`

- `src/main/resources/templates/staffshipping/shipping-list.html`

- `src/main/resources/templates/warranty/check-warranty.html`

  

## Static assets

  

- `src/main/resources/static/assets/image/logo.jpg`

- `src/main/resources/static/css/animate.css`

- `src/main/resources/static/css/bootstrap.min.css`

- `src/main/resources/static/css/BuildStyle.css`

- `src/main/resources/static/css/cart.css`

- `src/main/resources/static/css/Feedback.css`

- `src/main/resources/static/css/font-awesome.min.css`

- `src/main/resources/static/css/home.css`

- `src/main/resources/static/css/main.css`

- `src/main/resources/static/css/OrderStyle.css`

- `src/main/resources/static/css/prettyPhoto.css`

- `src/main/resources/static/css/price-range.css`

- `src/main/resources/static/css/responsive.css`

- `src/main/resources/static/css/ShippingStyle.css`

- `src/main/resources/static/css/StaffStyle.css`

- `src/main/resources/static/css/WarrantyStyle.css`

- `src/main/resources/static/js/bootstrap.bundle.min.js`

- `src/main/resources/static/js/BuildJs.js`

- `src/main/resources/static/js/cart.js`

- `src/main/resources/static/js/checkout.js`

- `src/main/resources/static/js/register.js`

- `src/main/resources/static/js/Staff.js`

- `src/main/resources/static/js/suggest.js`

  

## Flyway migrations

  

- `src/main/resources/db/migration/V1__create_account_tables.sql`

- `src/main/resources/db/migration/V2__create_product_tables.sql`

- `src/main/resources/db/migration/V3__create_order_tables.sql`

- `src/main/resources/db/migration/V4__create_chat_tables.sql`

- `src/main/resources/db/migration/V5__create_feedback_tables.sql`

- `src/main/resources/db/migration/V6__seed_account.sql`

- `src/main/resources/db/migration/V7__seed_product.sql`

- `src/main/resources/db/migration/V8__seed_order.sql`

- `src/main/resources/db/migration/V9__seed_feedback.sql`

- `src/main/resources/db/migration/V10__create_external_product_source_table.sql`

- `src/main/resources/db/migration/V11__rename_corsair_provider_code_to_algolia.sql`

- `src/main/resources/db/migration/V12__add_product_lifecycle_status.sql`

- `src/main/resources/db/migration/V13__normalize_seed_accounts.sql`

- `src/main/resources/db/migration/V14__seed_missing_images_for_other_products.sql`

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[04 - Database and Data Model]]

- [[05 - Product and Catalog Module]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[11 - Graph View Start Here]]

- [[12 - System Mindmap]]