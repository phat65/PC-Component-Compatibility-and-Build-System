
## Phạm vi module

  

Module này bao gồm:

  

- storefront product browsing

- product detail

- related products

- category/brand filtering

- product management cho staff

- brand management

- image upload

- mapping product với build specification

  

## Thành phần chính

  

### Controller

  

- `controller/home/HomeController.java`

- `controller/product/ProductController.java`

- `controller/product/ProductDetailController.java`

- `controller/product/ProductFeedbackController.java`

- `controller/category/CategoryController.java`

- `controller/brand/BrandController.java`

  

### Service

  

- `service/product/ProductService.java`

- `service/product/CategoryService.java`

- `service/product/BrandService.java`

- `service/product/ImageService.java`

- `service/product/SpecToCategoryMappingService.java`

- `service/validation/ComponentSpecValidationService.java`

  

### Repository

  

- `repository/product/ProductRepository.java`

- `repository/product/CategoryRepository.java`

- `repository/product/BrandRepository.java`

- `repository/product/ImageRepository.java`

  

## Storefront flow

  

## `GET /home`

  

Controller: `HomeController.home(...)`

  

Chức năng:

  

- lấy category chính

- lấy brand

- nếu có `category` query param thì lọc theo category

- nếu có `brand` query param thì lọc theo brand

- nếu không có filter thì lấy featured products

  

## `GET /products`

  

Controller: `HomeController.productHome(...)`

  

Chức năng:

  

- search/lọc theo category, brand, minPrice, maxPrice, keyword, sort, page, size

- render `product/product-home.html`

  

Điểm quan trọng:

  

- logic storefront search dùng `ProductService.searchVisibleCatalogProducts(...)`

- service này buộc `lifecycleStatus = SELLING`

  

## `GET /products/{id}`

  

Controller: `HomeController.showProductDetail(...)`

  

Chức năng:

  

- lấy product visible + selling

- đưa images vào model

- lấy related products theo primary category

- lấy feedback đã allow

- tính average rating

  

## Product visibility rule

  

Đây là rule quan trọng nhất của catalog:

  

- product phải `status = true`

- product phải `lifecycleStatus = SELLING`

  

Rule này xuất hiện ở:

  

- `Product.isSellableOnStorefront()`

- `ProductService.getVisibleSellingProductById(...)`

- `ProductRepository.findVisibleByProductIdAndLifecycleStatus(...)`

  

## `ProductService`

  

Method đáng chú ý:

  

- `getFeaturedProducts()`

- `getProductsByCategory(Integer categoryId)`

- `getProductsByBrand(Integer brandId)`

- `searchVisibleCatalogProducts(...)`

- `getVisibleSellingProductById(Integer id)`

- `discontinueProduct(Product product)`

  

### Ý nghĩa business

  

- staff có thể thêm/update product

- storefront chỉ dùng visible selling products

- hidden/discontinued product vẫn có thể tồn tại trong hệ thống

  

## `ProductRepository`

  

### Nhóm query storefront

  

- `findByStatusTrueAndLifecycleStatusOrderByProductIdDesc`

- `findByCategoryAndStatusTrueAndLifecycleStatus`

- `findByBrandAndStatusTrueAndLifecycleStatus`

- `findByStatusTrueAndLifecycleStatus`

- `searchProducts(...)`

- `findVisibleByProductIdAndLifecycleStatus(...)`

  

### Nhóm query related/search

  

- `findRandomRelatedProductsByCategory`

- `findRandomByCategoryAndStatusTrueAndLifecycleStatus`

- `search(...)`

  

### Nhóm query staff/admin

  

- `findAllWithImages()`

- `findWithDetailsByProductId(...)`

- `reassignBrandByIds(...)`

  

## Product management của staff

  

Controller: `ProductController`

  

Base route:

  

- `/staff/products`

  

Các chức năng:

  

- list product

- add product

- lấy spec form theo category

- edit product

- update spec

- upload/delete image

- hide product

  

### Điểm đáng chú ý trong `ProductController`

  

- validate tên product không trùng active product name

- category đầu tiên được coi là primary category cho spec

- không cho đổi primary category với product đã tồn tại

- nếu lifecycle khác `SELLING` thì ép `status = false`

- upload ảnh lưu ra `uploads/images`

  

### Hide/discontinue

  

`POST /staff/products/{id}/hide`

  

Thao tác logic:

  

- gọi `productService.discontinueProduct(product)`

- nghĩa là set:

  - `lifecycleStatus = DISCONTINUED`

  - `status = false`

  

## Brand management

  

Controller: `BrandController`

  

Base route:

  

- `/admin/brand`

  

Chức năng:

  

- list

- add

- update

- đổi status

- merge brand

  

Merge brand đáng chú ý vì có repo method:

  

- `ProductRepository.reassignBrandByIds(sourceId, targetId)`

  

## Ảnh sản phẩm

  

## Lưu trữ

  

- file vật lý: `uploads/images`

- URL public: `/image/**`

- mapping bởi `WebConfig`

  

## Category và build spec

  

Category không chỉ để phân loại catalog, mà còn điều khiển spec form:

  

- 1 -> Mainboard

- 2 -> CPU

- 3 -> GPU

- 4 -> Memory

- 5 -> Storage

- 6 -> Case

- 7 -> PowerSupply

- 8 -> Cooling

  

Primary category được dùng để:

  

- chọn spec form

- validate spec

- save spec vào bảng build tương ứng

  

## Feedback ở product detail

  

Product detail còn gắn với feedback:

  

- render feedback đã được duyệt

- hiển thị average rating

- đếm số lượng feedback

  

Vì vậy product module không hoàn toàn độc lập với feedback module.

  

## Related Notes

  

- [[00 - Index]]

- [[01 - Project Overview]]

- [[02 - Architecture and Conventions]]

- [[03 - Runtime Config and Security]]

- [[04 - Database and Data Model]]

- [[06 - Build PC Module]]

- [[07 - Order Payment Cart Warranty Module]]

- [[08 - Supporting Modules and UI]]

- [[09 - Route Map]]

- [[10 - File Inventory]]

- [[12 - System Mindmap]]