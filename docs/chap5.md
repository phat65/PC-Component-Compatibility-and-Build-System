\chapter{System Implementation}

\section{Implementation Overview}

This chapter presents the implementation of the proposed PC Component Compatibility and Build System. The purpose of this chapter is not to provide a step-by-step installation guide, but to describe the main technologies used in the system and explain how they support the implemented features.

\vspace{0.1cm}

The system is implemented as a web-based PC online shop with product browsing, cart management, order processing, online payment, user authentication, staff management features, and a PC build compatibility mechanism. The main implementation technologies include Spring Boot, Thymeleaf, Spring Security, PayOS, MySQL, Flyway, and Docker.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.28\textwidth}|p{0.58\textwidth}|}
        \hline
        \textbf{Technology} & \textbf{Role in the System} \\
        \hline
        Spring Boot & Implements the backend application, MVC controllers, business services, repositories, validation, scheduling, and application configuration. \\
        \hline
        Thymeleaf & Renders server-side web pages for storefront, authentication, product browsing, cart, checkout, and management screens. \\
        \hline
        Spring Security & Handles authentication, authorization, role-based access control, and protected page access. \\
        \hline
        MySQL and Flyway & Stores application data and manages database schema changes through versioned migrations. \\
        \hline
        PayOS & Provides online payment integration for checkout and payment status processing. \\
        \hline
        Docker & Provides a consistent local database environment for development and testing. \\
        \hline
    \end{tabular}
    \caption{Main technologies used in the implementation.}
    \label{tab:chap5-implementation-technologies}
\end{table}

\section{Backend Implementation with Spring Boot}

The backend is implemented using Spring Boot. It acts as the central layer that receives user requests, processes business logic, communicates with the database, and returns either web pages or response data depending on the feature.

\vspace{0.1cm}

The project follows a layered structure. Controllers are responsible for request mapping, route handling, model attributes, redirects, and response composition. Services contain business rules such as product visibility, order creation, payment handling, inventory updates, and PC compatibility checking. Repositories are responsible for database access through Spring Data JPA, while model classes represent persistent entities.

\vspace{0.1cm}

This structure keeps the system easier to maintain because each layer has a clear responsibility. For example, the order controller handles the checkout route, but the actual order creation and payment preparation are processed in the service layer. Similarly, the PC build pages display component options, while compatibility decisions are handled by dedicated build services.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.24\textwidth}|p{0.30\textwidth}|p{0.34\textwidth}|}
        \hline
        \textbf{Layer} & \textbf{Main Package or Resource} & \textbf{Implementation Role} \\
        \hline
        Presentation Layer & \texttt{src/main/resources/templates} and \texttt{src/main/resources/static} & Provides Thymeleaf pages, CSS, JavaScript, and client-side interactions. \\
        \hline
        Controller Layer & \texttt{controller} package & Handles routes, request parameters, form submissions, redirects, and response preparation. \\
        \hline
        Service Layer & \texttt{service} package & Contains business rules for product management, PC compatibility, cart, order, payment, feedback, and dashboard logic. \\
        \hline
        Persistence Layer & \texttt{repository} and \texttt{model} packages & Maps entities and performs database access through Spring Data JPA. \\
        \hline
        Database Layer & MySQL and Flyway migrations & Stores application data and manages schema evolution through versioned migration files. \\
        \hline
    \end{tabular}
    \caption{Implementation layers and their responsibilities.}
    \label{tab:chap5-layer-implementation}
\end{table}

\section{Core Workflow Implementation}

This section presents the PC build compatibility workflow, which is the main workflow of the system. It is selected because compatibility filtering is the core feature that distinguishes the proposed system from a normal online shop.

\vspace{0.1cm}

In this workflow, the customer opens a component selection page and the system reads the current build state from the session. The controller asks the build service to load products for the selected component type. The build service retrieves product data from the repository, checks each candidate through the compatibility service, and returns only suitable components to the page.

\vspace{0.1cm}

When the customer selects a component, the selected product is loaded and validated again before it is saved into the build session. This ensures that compatibility is enforced in the backend service layer, not only by the displayed component list.

% Screenshot suggestion:
% Insert a screenshot of the PC build page here.
% Caption suggestion:
% Figure 5.x: PC build interface with selected components and compatible options.

% Sequence diagram suggestion:
% Insert the PC build compatibility sequence diagram here.
% Suggested participants:
% Customer -> Build Page -> Build Controller -> Build Service -> Component Repository -> Database -> Compatibility Service -> Build Session
% Caption suggestion:
% Figure 5.x: Sequence diagram for PC build compatibility workflow.

\section{User Interface Implementation}

The user interface is mainly implemented with Thymeleaf templates and static assets. Thymeleaf is used to render dynamic server-side pages such as home page, product listing, product detail, login, registration, cart, checkout, order tracking, and PC build pages.

\vspace{0.1cm}

This implementation style is suitable for the system because most user flows are page-based. When a user opens a page, the controller prepares the required data and sends it to the template. The template then displays products, categories, cart items, order information, or selected build components.

\vspace{0.1cm}

Some parts of the system also use JSON-style interactions, especially in features related to build suggestion. However, the overall application remains primarily a Spring MVC and Thymeleaf system rather than a pure REST API application.

% Screenshot suggestion:
% Insert screenshots of important user interfaces here.
% Recommended screenshots:
% 1. Product listing or product detail page
% 2. PC build page
% 3. Cart or checkout page
% 4. Staff or admin dashboard
% Caption suggestion:
% Figure 5.x: Main user interfaces implemented with Thymeleaf.

\section{Authentication and Authorization}

Authentication and authorization are implemented using Spring Security. The system supports different user roles, including customer, staff, and administrator. Each role has access to different parts of the application.

\vspace{0.1cm}

Customers can browse products, manage their cart, place orders, make payments, and use the PC build function. Staff users can access management features related to products, orders, and customers. Administrators have broader permissions for system management and administrative functions.

\vspace{0.1cm}

Role-based access control helps protect sensitive pages and prevents users from accessing functions outside their permission level. This is especially important for management screens, order processing, and staff or administrator features.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.20\textwidth}|p{0.66\textwidth}|}
        \hline
        \textbf{Role} & \textbf{Main Accessible Functions} \\
        \hline
        Customer & Browse and search products, build PC configurations, manage cart items, place orders, make payments, view order status, and submit feedback. \\
        \hline
        Staff & Access staff dashboard, manage shipping status, check warranty information, review feedback, and manage operational order information. \\
        \hline
        Administrator & Access admin dashboard, manage products, categories, brands, component specifications, staff accounts, customer accounts, and business statistics. \\
        \hline
    \end{tabular}
    \caption{Role-based access summary.}
    \label{tab:chap5-role-access-summary}
\end{table}

\section{Database Implementation}

The system uses MySQL as the main relational database. Data is organized around important business domains such as accounts, products, categories, brands, carts, orders, payments, feedback, and PC build components.

\vspace{0.1cm}

Spring Data JPA is used to map Java entity classes to database tables and to simplify data access through repository interfaces. This allows the system to perform common persistence operations without writing repetitive SQL code in the application layer.

\vspace{0.1cm}

Flyway is used to manage database schema changes. Instead of relying on manual database updates, schema and seed changes are stored as versioned migration files. This makes the database structure more consistent across development environments and helps track how the schema evolves over time.

% Diagram/table suggestion:
% If needed, insert a small implementation-focused database figure here.
% Recommended content:
% JPA Entity -> Repository -> MySQL table mapping, or a screenshot/list of Flyway migration files.
% Caption suggestion:
% Figure 5.x: Database implementation with JPA repositories and Flyway migrations.

\section{Payment Implementation with PayOS}

Online payment is implemented through PayOS integration. During checkout, when a user selects online payment, the system creates a payment request and generates a checkout link through PayOS. The user is then redirected to the payment page to complete the transaction.

\vspace{0.1cm}

After the payment process, the system updates payment and order status according to the result returned from PayOS. The payment service is responsible for creating payment links, storing gateway payment identifiers, checking payment status, and handling payment-related callbacks.

\vspace{0.1cm}

This integration allows the system to support a more complete e-commerce workflow. Instead of only recording orders manually, the system can connect order creation with online payment processing and payment status tracking.

% Screenshot or sequence diagram suggestion:
% Insert a PayOS payment page screenshot or payment callback sequence diagram here if available.
% Caption suggestion:
% Figure 5.x: PayOS payment integration workflow.

\section{Docker Environment}

Docker is used to provide a consistent database environment for development. The project includes a Docker Compose configuration for running a MySQL service with the required database name, user, password, port mapping, and persistent storage volume.

\vspace{0.1cm}

Using Docker reduces environment differences between machines. Developers can run the same database service configuration without manually installing and configuring MySQL in different ways. This makes local development and testing more stable.

\vspace{0.1cm}

In this project, Docker is used as environment support rather than as the main topic of the implementation. Therefore, the report only explains its role instead of presenting detailed installation commands.

% Screenshot/table suggestion:
% Insert a short Docker Compose service summary or screenshot of the running MySQL container if needed.
% Caption suggestion:
% Figure 5.x: Local MySQL database environment provided by Docker.

\section{Implementation Summary}

The system implementation combines Spring Boot for backend logic, Thymeleaf for server-rendered user interfaces, Spring Security for access control, MySQL and Flyway for database management, PayOS for online payment, and Docker for development environment support.

\vspace{0.1cm}

These technologies work together to support the main features of the PC online shop, including product browsing, shopping cart, checkout, order management, payment processing, user roles, and PC compatibility checking. The implementation follows a layered structure so that routing, business logic, persistence, and presentation remain separated and easier to maintain.

\vspace{0.1cm}

Overall, the implementation provides a functional foundation for an online PC component shop and leaves room for future improvements such as richer product filtering, enhanced build recommendation, more detailed reporting, and broader automated testing.
