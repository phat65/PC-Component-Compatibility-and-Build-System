\chapter{Conclusion and Future Work}

\section{Conclusion}

This thesis presented the design and implementation of a PC Component Compatibility and Build System integrated with an online PC shop. The system supports product browsing, PC build creation, compatibility filtering, cart management, order processing, online payment, staff operation, and administrator management.

The main goal of the system is to help users choose compatible PC components more easily. Instead of allowing users to create invalid configurations and showing warnings afterward, the system filters component options based on the current build state. The project uses Spring Boot, Thymeleaf, Spring Security, Spring Data JPA, MySQL, Flyway, and PayOS in a layered architecture, which separates controllers, services, repositories, database access, and user interfaces.

\section{Limitations}

The system still has some limitations. The compatibility result depends on the correctness of product specification data. If important data such as socket type, form factor, dimension, or power requirement is missing or incorrect, the filtering result may also be incorrect.

The current rules focus on common compatibility factors such as CPU socket, memory type, storage interface, case clearance, cooling size, and PSU wattage. More detailed real-world issues such as BIOS version, RAM qualified vendor lists, cable clearance, and exact radiator mounting position are not fully covered. The build suggestion feature is also rule-based and does not learn from user behavior, purchase history, or product popularity.

\section{Future Work}

Future work can improve the system by expanding compatibility rules, improving build suggestion, and enhancing the PC build interface. The system can support more detailed checks such as BIOS support, connector availability, cable clearance, and radiator mounting positions. It can also use product popularity, user preferences, or purchase history to suggest more suitable configurations.

The e-commerce workflow can also be extended with better shipping integration, order notifications, promotion management, warranty claim handling, and more payment or delivery providers. In addition, more automated tests should be added for compatibility rules, order processing, payment handling, and role-based access control.
