
## Mục tiêu module

Cho phép user:

- chọn từng linh kiện PC theo từng bước
- chỉ nhìn thấy linh kiện phù hợp với build hiện tại
- nhận đề xuất build preset theo ngân sách
- áp dụng preset vào build session

Đây là một trong các module có business rule mạnh nhất trong dự án.

## Thành phần chính

### Controller

- `BuildController`
- `BuildSuggestionController`
- `CpuController`
- `GpuController`
- `MainboardController`
- `MemoryController`
- `StorageController`
- `CaseController`
- `PowerSupplyController`
- `CoolingController`
- `OtherController`

### Service

- `BuildService`
- `CompatibilityService`
- `RuleBasedBuildService`
- `CpuService`
- `GpuService`
- `MainboardService`
- `MemoryService`
- `StorageService`
- `CaseService`
- `PowerSupplyService`
- `CoolingService`
- `PerformanceScoreCalculator`

### Model

- `CPU`
- `GPU`
- `Mainboard`
- `Memory`
- `Storage`
- `Case`
- `PowerSupply`
- `Cooling`
- `BuildPreset`

### DTO

- `BuildItemDto`
- `BuildPlanDto`
- `BuildRequestDto`
- `ComponentDto`
- `ComponentRule`

## Route map tổng quát

Base route chính:

- `/build`

Các bước UI:

- `/build/start`
- `/build/mainboard`
- `/build/cpu`
- `/build/gpu`
- `/build/memory`
- `/build/storage`
- `/build/psu`
- `/build/cooling`
- `/build/case`
- `/build/other`
- `/build/finish`
- `/build/preset-result`
- `/build/startover`

API route:

- `/api/build/presets`
- `/api/build/suggest`
- `/api/build/apply`

## `BuildService`

Đây là service dùng để lọc linh kiện tương thích theo trạng thái build hiện tại.

Method chính:

- `getCompatibleMainboards(...)`
- `getCompatibleCpus(...)`
- `getCompatibleGPUs(...)`
- `getCompatibleCases(...)`
- `getCompatibleMemory(...)`
- `getCompatibleStorage(...)`
- `getCompatiblePowerSupplies(...)`
- `getCompatibleCoolings(...)`
- `getOtherProducts()`

### Logic chung

Pattern thường thấy:

1. load toàn bộ component từ repository
2. nếu build trống hoặc thiếu dependency thì trả về danh sách rộng hơn
3. nếu đã có component liên quan thì lọc bằng `CompatibilityService`
4. sort lại theo score/price/wattage/TDP

## `CompatibilityService`

Đây là nơi encode compatibility rules.

## Mainboard compatibility

Kiểm tra:

- CPU socket
- GPU PCIe version
- memory type
- memory modules vs memory slots
- memory speed vs max speed board hỗ trợ
- storage NVMe cần M.2 slot
- storage SATA cần SATA port

## CPU compatibility

Kiểm tra:

- socket với mainboard
- max memory speed
- memory modules so với memory channels

## GPU compatibility

Kiểm tra:

- PCIe version với CPU
- PCIe version với mainboard
- độ dài GPU với case
- PSU wattage đủ cho total TDP

## Case compatibility

Kiểm tra:

- form factor case vs form factor mainboard
- GPU max length
- cooling height cho air cooler
- radiator size cho liquid cooler
- PSU form factor

## Power supply compatibility

Kiểm tra:

- tổng TDP
- form factor với case

## Memory compatibility

Kiểm tra:

- DDR type
- slots
- speed với board
- speed với CPU
- modules so với memory channels

## Storage compatibility

Kiểm tra:

- NVMe cần M.2
- SATA cần SATA

## Cooling compatibility

Kiểm tra:

- air cooler height
- radiator size theo form factor case

## `RuleBasedBuildService`

Đây là service đề xuất cấu hình preset theo ngân sách.

### Đầu vào

- `presetName`
- `totalBudget`

### Đầu ra

- `BuildPlanDto`

### Trình tự chọn component

1. Mainboard
2. CPU
3. Memory
4. GPU
5. Storage
6. PSU
7. Cooling
8. Case

### Cách chọn

- mỗi loại linh kiện có budget allocation theo preset
- có target range khoảng `80% - 120%` budget thành phần
- ưu tiên component có score phù hợp
- lọc compatibility trong quá trình chọn
- có fallback nếu không tìm thấy item lý tưởng

## Session/state build

Theo note dự án, session attribute quan trọng là:

- `buildItems`

Điều này có nghĩa build flow mang tính stateful theo session người dùng.

## Template build

Các template chính:

- `build/build-pc.html`
- `build/mainboards.html`
- `build/build-cpu.html`
- `build/build-gpu.html`
- `build/memory.html`
- `build/storage.html`
- `build/psu.html`
- `build/cooling.html`
- `build/cases.html`
- `build/other.html`
- `build/preset-result.html`

Fragment liên quan:

- `layout/build-sidebar.html`

## Điểm cần lưu ý khi sửa

- đừng thêm package tree build mới
- đừng bỏ qua rule `status + lifecycleStatus`
- bất kỳ thay đổi nào ở spec field đều phải đối chiếu với migration và entity
- khi thêm category build mới cần update migration, entity, repository, controller, service compatibility và spec form

## Related Notes

- [[00 - Index]]
- [[01 - Project Overview]]
- [[02 - Architecture and Conventions]]
- [[03 - Runtime Config and Security]]
- [[04 - Database and Data Model]]
- [[05 - Product and Catalog Module]]
- [[07 - Order Payment Cart Warranty Module]]
- [[08 - Supporting Modules and UI]]
- [[09 - Route Map]]
- [[10 - File Inventory]]
- [[12 - System Mindmap]]
