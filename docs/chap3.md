\chapter{System Analysis and Design}

\section{System Overview}

The proposed system is a web-based PC Component Compatibility and Build System that supports users in selecting compatible computer hardware components while providing essential e-commerce functionalities. The system is designed to help users build PC configurations more easily and reduce the possibility of choosing incompatible components.

\vspace{0.1cm}

The system combines several main functions, including product browsing, component filtering, PC build creation, shopping cart management, order processing, user management, and online payment support. These functions are integrated into a single platform so that users can move from component selection to purchasing without needing to use multiple separate tools.

\vspace{0.1cm}

A key feature of the system is its compatibility filtering mechanism. Instead of allowing users to freely select incompatible components and then displaying warning messages, the system uses the user's current selections to determine which components should be shown next. For example, after a user selects a processor, the system can filter motherboard options based on the processor socket. This helps guide users toward valid PC configurations step by step.

\vspace{0.1cm}

From a design perspective, the system follows a layered architecture consisting of the presentation layer, controller layer, application layer, and data layer. This structure separates user interface processing, request handling, business logic, and data management, making the system easier to maintain and extend.

\begin{figure}
    \centering
    \includegraphics[width=0.6\linewidth]{Images/Figure 3.1: Overall overview of the proposed system..png}
    \caption{Overall overview of the proposed system.}
    \label{fig:system-overview}
\end{figure}

\section{User Roles and Use Case Analysis}

Use case analysis is used to identify the main interactions between users and the system. Based on the system requirements and the use case diagram, the system has four actors: User, Customer, Staff, and Admin. User is the general actor for authentication, while Customer and Staff inherit common user behavior. Admin is a specialized staff role with additional system management permissions.

\vspace{0.1cm}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.18\textwidth}|p{0.26\textwidth}|p{0.46\textwidth}|}
        \hline
        \textbf{Actor} & \textbf{Relationship} & \textbf{Description} \\
        \hline

        User
        &
        General actor
        &
        Represents any user who can access the system and perform common authentication behavior such as login.
        \\
        \hline

        Customer
        &
        Specializes User
        &
        Represents buyers who use the platform to register an account, search and browse products, build PC configurations, add products to the cart, place orders, make payments, and view order status.
        \\
        \hline

        Staff
        &
        Specializes User
        &
        Represents internal users who support daily store operations, including staff dashboard monitoring, shipping status management, warranty checking, feedback review, and order management.
        \\
        \hline

        Admin
        &
        Specializes Staff
        &
        Represents administrative users who manage categories, brands, products, component specifications, staff accounts, customer accounts, and dashboard statistics.
        \\
        \hline
    \end{tabular}
    \caption{Actors of the proposed system.}
    \label{tab:system-actors}
\end{table}

\begin{figure}
    \centering
    \includegraphics[width=1.05\linewidth]{Images/pcusecase_cropped.pdf}
    \caption{Use case diagram of the proposed system.}
    \label{fig:use-case-diagram}
\end{figure}
\newpage
\subsection{Use Case Summary}

The main use cases are grouped by actor to show the responsibilities and interactions of each role. Customer use cases focus on product searching, PC building, cart operation, order placement, payment, and order tracking. Staff use cases focus on daily operational management. Admin use cases focus on system data management and business monitoring.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.18\textwidth}|p{0.28\textwidth}|p{0.46\textwidth}|}
        \hline
        \textbf{Actor} & \textbf{Use Case} & \textbf{Description} \\
        \hline

        User
        &
        Login
        &
        Authenticates a registered user and allows the user to access role-based system functions.
        \\
        \hline

        Customer
        &
        Register
        &
        Creates a customer account before using customer-specific functions that require authentication.
        \\
        \hline

        Customer
        &
        Search Products
        &
        Searches for products by keyword or related criteria while browsing the product catalog.
        \\
        \hline

        Customer
        &
        Build PC Configuration
        &
        Selects PC components step by step, while the system displays compatible component options based on the current build.
        \\
        \hline

        Customer
        &
        Add Products to Cart
        &
        Adds selected products or PC build items to the shopping cart before checkout.
        \\
        \hline

        Customer
        &
        Place Order
        &
        Confirms selected cart items, provides required order information, creates an order, and proceeds to payment.
        \\
        \hline

        Customer
        &
        View Order Status
        &
        Reviews the current status of placed orders after checkout.
        \\
        \hline
    \end{tabular}
    \caption{User and customer use case summary.}
    \label{tab:customer-use-case-summary}
\end{table}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.18\textwidth}|p{0.28\textwidth}|p{0.46\textwidth}|}
        \hline
        \textbf{Actor} & \textbf{Use Case} & \textbf{Description} \\
        \hline

        Staff
        &
        View Staff Dashboard
        &
        Views daily operational information such as order workload, shipping queue, and inventory alerts.
        \\
        \hline

        Staff
        &
        Manage Shipping Status
        &
        Updates delivery-related order status according to the current shipping stage.
        \\
        \hline

        Staff
        &
        Check Warranty Information
        &
        Searches customer orders or phone numbers and reviews warranty details derived from purchased products.
        \\
        \hline

        Staff
        &
        Review Feedback
        &
        Reviews customer feedback and handles feedback-related actions when necessary.
        \\
        \hline

        Staff
        &
        Manage Orders
        &
        Views and manages order information for operational support.
        \\
        \hline

        Admin
        &
        View Admin Dashboard
        &
        Views business monitoring information such as revenue statistics and order statistics.
        \\
        \hline

        Admin
        &
        Manage Categories
        &
        Maintains product category data used for product organization and filtering.
        \\
        \hline

        Admin
        &
        Manage Brands
        &
        Maintains brand information used for product organization and filtering.
        \\
        \hline

        Admin
        &
        Manage Component Specifications
        &
        Maintains technical component data from product management so that the compatibility filtering mechanism can work accurately.
        \\
        \hline

        Admin
        &
        Manage Staff
        &
        Manages staff accounts in the system.
        \\
        \hline
        
        Admin
        &
        Manage Customers
        &
        Manages customer accounts in the system.
        \\
        \hline
    \end{tabular}
    \caption{Staff and admin use case summary.}
    \label{tab:staff-admin-use-case-summary}
\end{table}

\subsection{Detailed Use Case Specifications}

The following use case specifications describe the main workflows that represent the core business value of the system. The PC building workflow is the most important use case because it directly supports compatibility-based component selection.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.24\textwidth}|p{0.66\textwidth}|}
        \hline
        \multicolumn{2}{|l|}{\textbf{Use Case: Build PC Configuration}} \\
        \hline
        Primary Actor & Customer \\
        \hline
        Goal & To create a PC configuration by selecting compatible components from available product data. \\
        \hline
        Preconditions & The customer accesses the PC build page, and component product data is available in the system. \\
        \hline
        Main Flow &
        The customer selects a component category, chooses a component, and adds it to the current build. The system stores the current selection, checks relevant compatibility rules, and displays compatible component options. The customer continues selecting components until the configuration is complete or ready to be added to the cart.
        \\
        \hline
        Alternative Flow &
        If no compatible component is available for the next category, the system displays no matching option and the customer may change or remove previous selections.
        \\
        \hline
        Postconditions & The selected PC configuration is maintained in the session and can be added to the shopping cart. \\
        \hline
    \end{tabular}
    \caption{Use case specification for building a PC configuration.}
    \label{tab:use-case-build-pc}
\end{table}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.24\textwidth}|p{0.66\textwidth}|}
        \hline
        \multicolumn{2}{|l|}{\textbf{Use Case: Place Order}} \\
        \hline
        Primary Actor & Customer \\
        \hline
        Goal & To create an order from selected cart items and complete the payment process. \\
        \hline
        Preconditions & The customer is logged in and has at least one selected item in the shopping cart. \\
        \hline
        Main Flow &
        The customer reviews selected cart items, enters required order information, and confirms order placement. The system creates an order, records order details, calculates the final amount, and prepares the payment process. If online payment is selected, the system redirects the customer to the payment provider and updates the payment result after receiving the response.
        \\
        \hline
        Alternative Flow &
        If payment fails or is cancelled, the order and payment status are updated according to the result, and the customer may retry payment or choose another available option.
        \\
        \hline
        Postconditions & The order is stored in the system with its corresponding order details, payment status, and shipping information. \\
        \hline
    \end{tabular}
    \caption{Use case specification for placing an order.}
    \label{tab:use-case-place-order}
\end{table}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.24\textwidth}|p{0.66\textwidth}|}
        \hline
        \multicolumn{2}{|l|}{\textbf{Use Case: Manage Shipping Status}} \\
        \hline
        Primary Actor & Staff \\
        \hline
        Goal & To keep order delivery information updated during fulfillment. \\
        \hline
        Preconditions & The staff member is authenticated and has permission to access shipping management functions. \\
        \hline
        Main Flow &
        The staff member views shipping-related orders, selects an order, checks its current status, and updates the delivery status according to the actual fulfillment stage. The system validates the status transition and saves the updated order information.
        \\
        \hline
        Alternative Flow &
        If the selected status transition is invalid, the system rejects the update and keeps the previous order status.
        \\
        \hline
        Postconditions & The order status is updated and can be viewed by authorized users and the customer. \\
        \hline
    \end{tabular}
    \caption{Use case specification for managing shipping status.}
    \label{tab:use-case-manage-shipping}
\end{table}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.24\textwidth}|p{0.66\textwidth}|}
        \hline
        \multicolumn{2}{|l|}{\textbf{Use Case: Manage Component Specifications}} \\
        \hline
        Primary Actor & Admin \\
        \hline
        Goal & To maintain accurate component specification data for compatibility filtering. \\
        \hline
        Preconditions & The admin is authenticated and has permission to access product management functions. \\
        \hline
        Main Flow &
        The admin accesses product management, selects a component product, and creates or updates component-specific specifications such as socket type, memory type, form factor, power requirement, and physical size. The system validates and stores the updated specification data so that it can be used in product browsing and compatibility filtering.
        \\
        \hline
        Alternative Flow &
        If required product data or specification data is missing or invalid, the system prevents the update and asks the admin to correct the information.
        \\
        \hline
        Postconditions & Component specifications are updated and available for compatibility filtering and product management functions. \\
        \hline
    \end{tabular}
    \caption{Use case specification for managing component specifications.}
    \label{tab:use-case-manage-component-specifications}
\end{table}



\section{System Functional Capabilities}

After identifying the actors and their use cases, the system can also be viewed from a functional perspective. While the use case analysis explains how Customer, Staff, and Administrator interact with the system, this section summarizes what the system is able to provide as a complete software solution.

\vspace{0.1cm}

The functional capabilities are organized into major areas that support the full workflow of the platform, from account access and product discovery to PC building, purchasing, order handling, and system administration.

\vspace{0.1cm}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.28\textwidth}|p{0.64\textwidth}|}
        \hline
        \textbf{Functional Area} & \textbf{System Capabilities} \\
        \hline

        Account and Access Management
        &
        Supports user registration, login, logout, role-based access control, and account status management. The system separates access permissions for customers, staff members, and administrators so that each role can use only the functions assigned to it.
        \\
        \hline

        Product Catalog and Component Data
        &
        Stores and displays product information such as name, price, brand, category, description, image, stock quantity, and selling status. For PC components, the system also maintains technical specifications such as socket type, memory type, form factor, power requirement, and physical size, which are later used for compatibility filtering.
        \\
        \hline

        PC Build and Compatibility Filtering
        &
        Allows users to create a PC configuration by selecting components from different hardware groups. Based on the current selections, the system filters the next available components using compatibility rules such as CPU socket, motherboard socket, memory standard, case form factor, GPU clearance, cooling size, and PSU wattage.
        \\
        \hline

        Shopping Cart and Checkout
        &
        Allows selected products or PC build items to be added to the shopping cart. The system supports quantity updates, item removal, item selection, checkout preparation, shipping information input, and transition from cart data to order data.
        \\
        \hline
    \end{tabular}
    \caption{Customer-facing and core functional capabilities of the proposed system.}
    \label{tab:core-functional-capabilities}
\end{table}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.28\textwidth}|p{0.64\textwidth}|}
        \hline
        \textbf{Functional Area} & \textbf{System Capabilities} \\
        \hline

        Order, Payment, and Shipping
        &
        Manages the purchasing workflow after checkout. The system records order information, order details, payment status, shipping information, and order status. It also supports online payment integration and allows shipping progress to be updated during fulfillment.
        \\
        \hline

        Warranty, Feedback, and Support
        &
        Supports post-purchase activities by allowing warranty information to be checked from order and product data. The system also supports customer feedback management and user support features such as product comments, replies, and assisted communication.
        \\
        \hline

        Staff Operation Management
        &
        Provides functions for daily store operations, including viewing assigned work, monitoring shipping-related orders, updating delivery progress, checking warranty information, and reviewing customer feedback when required.
        \\
        \hline

        Administration and Reporting
        &
        Provides management functions for products, categories, brands, component specifications, customer accounts, staff accounts, orders, feedback, and dashboard statistics. These functions help administrators maintain accurate system data and monitor the overall operation of the platform.
        \\
        \hline
    \end{tabular}
    \caption{Operational and administrative functional capabilities of the proposed system.}
    \label{tab:operation-functional-capabilities}
\end{table}

Among these capabilities, \textit{PC Build and Compatibility Filtering} is the core capability of the proposed system because it directly supports the main objective of helping users select compatible PC components. The other capabilities support this core function by providing product data, account access, purchasing flow, order fulfillment, after-sales service, and administrative control.



\section{System Architecture}

\begin{figure}[htpb]
    \centering
    \includegraphics[width=0.4\linewidth]{Images/overall architecture system.png}
    \caption{System Architecture}
    \label{fig:placeholder}
\end{figure}

The system is designed using a layered architecture. This architecture separates the system into different layers, each responsible for a specific set of tasks. The main layers are the presentation layer, the controller layer, the application layer, and the data layer. In addition, the system integrates with external services for online payment, email delivery, and external product or blog information.


\subsection{Presentation Layer}

The presentation layer is responsible for user interaction. It provides the web interface through which customers, staff, and administrators access the system.

\vspace{0.1cm}

For customers, the interface supports product browsing, searching, PC building, cart management, checkout, and order viewing. For staff, the interface supports operational tasks such as shipping management, warranty checking, and feedback handling. For administrators, the interface provides management screens for products, categories, brands, orders, users, and dashboard statistics.

\vspace{0.1cm}

This layer communicates with the backend mainly through Spring MVC page requests and form submissions. Some features, such as build suggestion and address-related interactions, use JSON-based requests. The presentation layer does not directly access the database, which helps maintain separation between the user interface and the system's business logic.

\subsection{Controller Layer}

The controller layer receives requests from the presentation layer and determines the proper response. Most controllers are Spring MVC controllers that return Thymeleaf templates or redirects. A smaller number of controllers expose JSON-based endpoints for features such as build suggestion and address management.

\vspace{0.1cm}

Controllers are responsible for request mapping, request parameter handling, form binding, validation result handling, model attribute preparation, and HTTP response composition. Business rules are not handled directly in this layer; instead, controllers delegate processing to services.

\subsection{Application Layer}

The application layer contains the main business logic of the system. It receives requests from controllers, processes them, applies business rules, and returns appropriate responses or model data for the views.

\vspace{0.1cm}

The main responsibilities of this layer include authentication and authorization, product management, component specification management, compatibility filtering, build suggestion, cart management, order processing, payment handling, warranty support, feedback management, email sending, and dashboard reporting.

\vspace{0.1cm}

The compatibility filtering logic is also handled in this layer. When a user selects a component, the backend evaluates related specifications and prepares suitable component options for the build pages. This ensures that compatibility decisions are processed consistently and are not dependent only on the web interface.

\subsection{Data Layer}

The data layer is responsible for storing and managing system data. The system uses Spring Data JPA repositories and a MySQL relational database to store information about accounts, addresses, products, categories, brands, images, component specifications, carts, orders, payments, and feedback.

\vspace{0.1cm}

The use of a relational database is suitable for this system because the data contains many structured relationships. For example, a product belongs to a brand, products can belong to categories, a cart contains multiple cart items, and an order contains multiple order details.

\vspace{0.1cm}

Foreign key constraints are used to maintain data integrity between related tables. Database changes are managed through Flyway migrations, while Hibernate schema generation is disabled. This makes the database structure more controlled and consistent across development and deployment environments.

\subsection{External Services and Background Processing}

The system integrates with PayOS to support online payment processing and payment status tracking. It also uses an email service for account verification and password-related communication. In addition, external content sources can be used to support blog or product reference information.

\vspace{0.1cm}

Background processing is enabled for scheduled and asynchronous tasks. Scheduled tasks are used for order and payment cleanup activities and for synchronizing external content when required. This separates time-based processing from normal user request handling.

\section{Database Design}

The database is designed to support two main needs: e-commerce operations and PC component compatibility filtering. E-commerce data includes accounts, addresses, carts, orders, payments, and feedback. Compatibility data includes general product information and component-specific technical specifications.

\vspace{0.1cm}

The design follows relational database principles and is maintained through Flyway migration files. Common product information is stored in a general product table, while technical hardware attributes are stored in separate component tables. This keeps shared product data centralized while still allowing each component type to maintain its own specifications.

\begin{figure}
    \centering
    \includegraphics[width=1\linewidth]{Images/erd đồ án_cropped-1.pdf}
    \caption{Entity Relationship Diagram of the system database.}
    \label{fig:database-erd}
\end{figure}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.26\textwidth}|p{0.34\textwidth}|p{0.30\textwidth}|}
        \hline
        \textbf{Data Group} & \textbf{Main Tables or Entities} & \textbf{Purpose} \\
        \hline
        Product catalog & Product, brand, category, product image, product category & Stores storefront product information and product organization data. \\
        \hline
        Component specifications & CPU, GPU, motherboard, memory, storage, case, power supply, cooling & Stores technical attributes used by compatibility filtering. \\
        \hline
        Account and address & Account, account address & Stores login, role, contact, status, and delivery address information. \\
        \hline
        Cart and order & Cart, cart item, order, order detail & Supports shopping cart, checkout, and purchase records. \\
        \hline
        Payment & Payment & Stores payment gateway data, payment status, amount, and transaction information. \\
        \hline
        Feedback & Feedback & Stores product comments, ratings, approval status, and replies. \\
        \hline
    \end{tabular}
    \caption{Main database groups and responsibilities.}
    \label{tab:database-groups}
\end{table}

\newpage
\subsection{Product and Component Data}

\begin{figure}[htbp]
    \centering
    \includegraphics[width=0.8\linewidth]{Images/product_cropped-1.pdf}
    \caption{Product and component data relationship diagram.}
    \label{fig:product-component-relationship}
\end{figure}
The product data model separates common product attributes from hardware-specific attributes. The product table stores shared information such as product name, price, description, selling status, lifecycle status, stock quantity, performance score, brand reference, and image relationships.

\vspace{0.1cm}

Categories organize products into hardware groups, and the many-to-many relationship between products and categories is handled through the product category table. Component-specific tables such as CPU, GPU, motherboard, memory, storage, case, power supply, and cooling use the product identifier as their key and store the technical attributes needed for compatibility checking.

\vspace{0.1cm}

This structure allows the system to treat every component as a product for selling purposes while still storing technical data separately for compatibility rules. For example, CPU and motherboard compatibility can be checked by socket type, while motherboard and memory compatibility can be checked by memory standard.

\subsection{User, Cart, Order, and Payment Data}

\begin{figure}[htbp]
    \centering
    \includegraphics[width=0.8\linewidth]{Sections/account_cropped-1.pdf}
    \caption{User, cart, order, and payment data relationship diagram.}
    \label{fig:user-cart-order-payment-relationship}
\end{figure}
The account table stores login, role, contact, and account status information. Account addresses are stored separately so that delivery information can be managed independently from authentication data.

\vspace{0.1cm}

The cart and cart item tables support shopping before checkout. Each cart item references a product and stores quantity, selection status, and whether the item comes from the PC build flow. When checkout is completed, order and order detail records are created to store the purchase history.

\vspace{0.1cm}

Payment data is separated from order data to support gateway tracking. The payment table records payment status, amount, currency, gateway identifiers, and transaction-related information.

\subsection{Feedback Data}

The feedback table stores product ratings and comments. Each feedback record links a customer account with a reviewed product and stores rating value, comment content, status, reply content, and creation time.
\begin{figure}[htbp]
    \centering
    \includegraphics[width=0.5\linewidth]{Images/chat_cropped-1.pdf}
    \caption{Feedback data relationship diagram.}
    \label{fig:feedback-relationship}
\end{figure}
\subsection{Entity Relationships}

The most important relationships in the database are summarized in Table \ref{tab:database-key-relationships}. These relationships allow the same schema to support both product purchasing and compatibility filtering.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.30\textwidth}|p{0.52\textwidth}|}
        \hline
        \textbf{Relationship} & \textbf{Purpose} \\
        \hline
        Brand to product & One brand can have many products, while each product belongs to one brand. \\
        \hline
        Product to category & Products and categories are connected through a product category table to support many-to-many classification. \\
        \hline
        Product to component table & Each component-specific table extends product data with technical specifications used for compatibility checking. \\
        \hline
        Account to address, cart, and order & One account can have delivery addresses, a shopping cart, and multiple orders. \\
        \hline
        Order to order detail and payment & Orders store purchase records, order details store purchased products, and payment stores transaction information. \\
        \hline
        Account and product to feedback & Feedback records show which customer reviewed which product. \\
        \hline
    \end{tabular}
    \caption{Key entity relationships in the database.}
    \label{tab:database-key-relationships}
\end{table}

\section{Chapter Summary}

This chapter presented the analysis and design of the proposed PC Component Compatibility and Build System. The chapter described the overall system concept, user roles, use cases, functional capabilities, system architecture, and database design.

\vspace{0.1cm}

The proposed design separates the system into presentation, controller, application, and data layers. This structure improves maintainability and supports clear responsibility separation among the web interface, request handling, backend business logic, and database storage.

\vspace{0.1cm}

The database design supports both product purchasing and compatibility filtering. General product information is stored separately from component-specific specifications, allowing the system to manage products efficiently while still supporting hardware compatibility rules.

\vspace{0.1cm}

The next chapter focuses on the compatibility filtering mechanism, which is the core functionality of the proposed system.
