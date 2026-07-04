\chapter{Introduction}

\section{Background}

Building a personal computer allows users to choose hardware components according to their performance needs, budget, and upgrade plans. This flexibility is useful for gamers, students, office users, content creators, and other users who need a computer configuration that matches their specific purpose.

\vspace{0.1cm}

However, selecting PC components is not simple for many users. A complete PC build includes several related parts such as CPU, motherboard, memory, graphics card, storage, power supply, case, and cooling device. These components must satisfy technical constraints such as socket type, memory standard, form factor, physical clearance, and power requirement.

\vspace{0.1cm}

Many online computer stores provide product browsing and purchasing functions, but users may still need to check component compatibility manually. This is difficult for beginners and can lead to unsuitable component choices. To address this problem, this project proposes a web-based PC Component Compatibility and Build System that combines product purchasing with compatibility filtering.

\begin{figure}[H]
    \centering
    \includegraphics[width=1\textwidth]{Images/overviewchap1.png}
    \caption{Conceptual overview of the proposed system.}
    \label{fig:proposed-system-overview}
\end{figure}

\section{Problem Statement}

Building a custom PC requires users to select components that can work together. Users who lack hardware knowledge may choose incompatible parts, such as a CPU and motherboard with different sockets, memory that is not supported by the motherboard, a graphics card that does not fit the case, or a power supply that does not provide enough wattage.

\vspace{0.1cm}

Although compatibility information is available from manufacturers and online resources, checking it manually takes time and requires technical understanding. Therefore, a system is needed to guide users during component selection and reduce the risk of purchasing incompatible hardware.

\section{Objectives}

The main objective of this project is to develop a web-based platform that helps users build compatible PC configurations and purchase selected components through the same system.

\vspace{0.1cm}

The specific objectives are:

\begin{itemize}
    \item Provide product browsing, searching, filtering, and product detail viewing for computer hardware.
    \item Implement compatibility filtering based on the user's current PC build selections.
    \item Support PC build creation using component groups such as CPU, motherboard, memory, GPU, storage, case, cooling, and power supply.
    \item Provide e-commerce functions including cart management, checkout, order processing, and online payment.
    \item Provide management functions for products, categories, brands, component specifications, orders, users, and feedback.
\end{itemize}

\section{Contributions}

The main contribution of this project is an integrated system that connects PC component compatibility support with an online shopping workflow.

\vspace{0.1cm}

The project provides a rule-based compatibility filtering mechanism that displays suitable component options based on the user's current selections. This helps prevent many invalid combinations before they are added to a build.

\vspace{0.1cm}

The project also demonstrates how a Spring Boot MVC and Thymeleaf-based e-commerce application can support both normal shopping functions and a specialized PC build flow. The system includes product management, cart and order processing, online payment integration, staff operation support, and administrative management.

\section{Scope of the Project}

The scope of this project includes the development of a web-based PC online shop with compatibility-based PC building support. Users can browse products, create PC configurations, view compatible component options, add selected products to the cart, place orders, and complete payment.

\vspace{0.1cm}

The system also supports staff and administrator functions, including product and specification management, order monitoring, shipping status management, warranty checking, feedback management, user management, and dashboard reporting.

\vspace{0.1cm}

Advanced features such as AI-based personalized recommendations, detailed benchmark prediction, real-time hardware monitoring, and complete real-world compatibility coverage such as BIOS version matching or RAM qualified vendor lists are outside the scope of the current project.
