# AGENTS.md

## Project Snapshot

This repository is a Spring Boot PC online shop with a custom PC build flow.

Important characteristics of the current codebase:

- It is not REST-only. The main application style is Spring MVC + Thymeleaf server-rendered pages.
- Some features are JSON/API based, especially build suggestion and external component import.
- Root package: `com.example.PCOnlineShop`
- Main stack: Java 21, Spring Boot 3.5.x, Maven, MySQL, Flyway, Spring Security, Thymeleaf, Mail, PayOS, Jsoup
- Application entry enables both scheduling and async execution.

## Real Project Structure

Use the current package layout as the source of truth:

- `src/main/java/com/example/PCOnlineShop/controller`
  MVC controllers and a few REST controllers
- `src/main/java/com/example/PCOnlineShop/service`
  Business logic grouped by domain
- `src/main/java/com/example/PCOnlineShop/repository`
  Spring Data JPA repositories
- `src/main/java/com/example/PCOnlineShop/model`
  JPA entities grouped by domain: `account`, `product`, `build`, `order`, `payment`, `cart`, `chat`, `feedback`
- `src/main/java/com/example/PCOnlineShop/dto`
  DTOs used mainly for `build`, `order`, `cart`, `payment`, `warranty`, `ai`, `blog`
- `src/main/java/com/example/PCOnlineShop/integration/component`
  External component catalog provider, mapping, fetch, and persistence flow
- `src/main/resources/templates`
  Thymeleaf views
- `src/main/resources/static`
  Frontend assets
- `src/main/resources/db/migration`
  Flyway schema and seed migrations
- `uploads/images`
  Uploaded product images stored on disk

## Architecture Rules For This Repo

Follow the existing architecture instead of forcing a new one:

- Keep controllers thin.
  They should handle routing, request params, model attributes, redirects, and HTTP response composition.
- Put business rules in services.
- Keep repositories focused on persistence and queries.
- Use entities for persistence and DTOs where the existing flow already uses DTOs.
- Do not convert Thymeleaf page flows into API-first flows unless explicitly requested.
- When adding a page feature, update controller, service, template, and static assets only as needed.
- When adding an API feature, prefer DTO-based payloads and keep controller code small.

## Existing Conventions To Follow

- Match the local style of the file you touch.
  The codebase mixes explicit constructor injection and Lombok `@RequiredArgsConstructor`.
- Reuse existing package names and domain slices before creating new classes.
- Prefer small, focused methods and readable query/service names.
- Avoid hardcoded values when there is already an enum, repository method, constant, or service rule available.
- Keep changes minimal and do not rewrite unrelated code.

## MVC, API, And View Guidance

This repo is primarily MVC:

- Many controllers return template names such as `home`, `product/product-list`, `auth/login`, `build/...`
- Use `@Controller` for server-rendered page flows.
- Use `@RestController` only where the current feature is already API-oriented.

Current API-style areas include:

- `controller.build.BuildSuggestionController`
- `controller.product.ExternalComponentCatalogController`

Do not assume every new feature should be a REST endpoint.

## Product Domain Rules

The product module has more rules than a generic catalog:

- Product visibility on the storefront depends on both:
  - `status = true`
  - `lifecycleStatus = SELLING`
- Staff-facing product management may work with broader product states.
- Product image files are stored under `uploads/images` and linked through `/image/...`
- Product/category/brand relationships are important for filtering and related products.
- Product specifications for PC parts are tied to build entities such as `CPU`, `GPU`, `Mainboard`, `Memory`, `Storage`, `Case`, `PowerSupply`, and `Cooling`.

If you change storefront product queries, preserve the `status + lifecycleStatus` rule unless the task explicitly changes that behavior.

## Build Module Rules

PC build compatibility is a core business requirement.

When touching build logic, consider at least:

- CPU socket and motherboard socket
- Memory type, slots, speed, and board support
- GPU interface and physical fit
- PSU wattage and form factor
- Case form factor and clearance
- Cooling type and size
- Storage interface compatibility

Existing build logic is centered around:

- `service.build.CompatibilityService`
- `service.build.RuleBasedBuildService`
- Session attribute `buildItems`

Important repository-specific note:

- There are parallel package trees under both:
  - `controller.build` and `service.build`
  - `controller.build.component` and `service.build.component`
- Before editing, inspect which tree the active route/view already belongs to.
- Do not introduce a third parallel structure.
- Do not start a broad consolidation refactor unless explicitly requested.

## Order, Payment, And Warranty Rules

Order and payment behavior is interconnected:

- Checkout and order creation live in the order/cart/payment service flow.
- PayOS integration is already wired in the project.
- Inventory rollback is part of order cancellation handling.
- Shipping status transitions are validated in service logic.
- Warranty details are derived from order details and product category.
- Scheduled order/payment cleanup exists under `scheduler`.

If you change order status or payment behavior, keep the logic centralized in services and preserve inventory consistency.

## Security And Routing

Security is defined in `config.auth.SecurityConfig`.

Important patterns:

- Roles are effectively `ADMIN`, `STAFF`, and `CUSTOMER`
- Public routes include storefront, auth, build pages, build API, assets, and some blog/chat/payment paths
- Staff/admin functions mostly live under `/staff/**`, `/dashboard/**`, and order/customer/staff management routes

If you add or change endpoints, check whether the security configuration also needs an update.

## External Integration Rules

The repo already contains external component catalog import logic:

- Use `integration.component` for provider/client/mapper/import work
- `ExternalComponentCatalogController` is feature-flagged with `app.catalog.external-import.enabled`
- Do not place provider-specific import logic directly inside product controllers or generic product services

## Database Rules

- Database name: `pconlineshop`
- Local Docker MySQL is defined in `compose.yaml`
- Host port is `3307`
- Flyway is enabled and is the expected path for schema/data changes
- New database changes should be added as new versioned files in `src/main/resources/db/migration`
- Do not rely on `ddl-auto` or manual one-off SQL as the main migration path

## Configuration Rules

- Main application config is in `src/main/resources/application.yml`
- The current file contains local datasource, mail, and PayOS settings
- Do not add more hardcoded secrets if environment-based configuration can be used
- When adding config-backed features, prefer typed configuration or property binding patterns already used in the project

## Testing And Verification

- `src/test` is currently absent or minimal, so do not assume automated coverage exists
- For business-rule changes, verify with the smallest meaningful Maven command available
- For MVC changes, verify the affected page flow or route
- For repository/query changes, verify against entity mapping and actual query usage
- For migration changes, verify entity-field-to-column alignment carefully

## What The Agent Should Do

When modifying code:

- Inspect the existing package, controller, service, repository, entity, DTO, and template flow first
- Follow current naming and routing conventions
- Reuse existing services and repositories before adding new layers
- Keep changes local to the feature being changed
- Explain major changes briefly and concretely

When adding features:

- Identify whether the feature belongs to MVC pages, JSON API, or both
- Update only the affected layers
- Keep business logic out of controllers
- Respect current domain boundaries such as `product`, `build`, `order`, `auth`, `integration`

When fixing bugs:

- Find the root cause first
- Prefer minimal, safe fixes
- Avoid incidental refactors unless they are necessary to fix the issue

## What The Agent Should Avoid

- Do not introduce a new architecture style without an explicit request
- Do not move many files around without a strong reason
- Do not bypass Flyway for schema changes
- Do not ignore `lifecycleStatus` when working on storefront product behavior
- Do not put business logic into controllers, templates, or repository default methods
- Do not expose entities directly as API responses in DTO-based flows unless explicitly requested
- Do not create another duplicated build package tree
- Do not edit generated or runtime directories such as `target` unless the task explicitly requires it
- Do not assume frontend requirements that are not already present in templates/static assets or user requirements
