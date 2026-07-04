\chapter{Related Works and Technical Foundations}

This chapter reviews existing PC building platforms and technical approaches related to component compatibility support. The purpose is to identify what existing systems already provide, what limitations remain, and why a rule-based filtering approach is suitable for the proposed PC Component Compatibility and Build System.

\vspace{0.1cm}

The review focuses on three aspects: existing PC building platforms, compatibility support approaches, and rule-based validation. These topics provide the background for the system analysis and compatibility mechanism described in the following chapters.

\section{PC Building Platforms}

Online PC building platforms help users select hardware components and create complete PC configurations. They are useful because a custom PC build depends on many relationships among components, such as CPU and motherboard socket, memory type, graphics card clearance, power supply wattage, and case form factor.

\vspace{0.1cm}

In general, existing platforms can be viewed from two directions. Some platforms focus on build planning and compatibility checking, while others focus on e-commerce functions such as product browsing, price comparison, and purchasing. This is related to product configuration research, where users select components under a set of constraints to form a valid configuration \cite{schneeweiss2011}. The proposed system is positioned between these two directions by combining compatibility support with a direct shopping and ordering workflow.

\subsection{HACOM}

HACOM is a Vietnamese computer retail platform that provides an online Build PC function \cite{hacom}. The tool allows users to choose components from many hardware groups such as CPU, motherboard, RAM, SSD, VGA, power supply, case, cooling device, monitor, keyboard, mouse, and other accessories.

\vspace{0.1cm}

The main strength of HACOM is that PC configuration is connected directly with a retail workflow. Users can select components, view estimated cost, add the configuration to the cart, or proceed to ordering. This is close to the direction of the proposed system because the PC building process is not separated from purchasing.

\vspace{0.1cm}

However, the public interface is still mainly designed for product selection and sales support. For the proposed system, the important lesson is that a local PC shop can benefit from a build flow that is both convenient for purchasing and explicit about compatibility rules.

\subsection{GEARVN}

GEARVN is another Vietnamese retailer that provides a Build PC page for users who want to create a custom gaming or working PC configuration \cite{gearvn}. The platform is strongly connected to e-commerce features such as product categories, cart, payment information, warranty lookup, and store services.

\vspace{0.1cm}

The strength of GEARVN is its retail ecosystem and customer-oriented purchasing flow. Users can browse products, access store information, view support policies, and use the Build PC function in the same website. This shows that PC building tools are useful in the Vietnamese retail context, especially when they are connected with ordering and after-sales support.

\vspace{0.1cm}

At the same time, the proposed system focuses more directly on explaining and enforcing compatibility through backend rules. This helps reduce invalid selections during the build process and keeps compatibility behavior consistent between manual building and rule-based build suggestion.

\subsection{Comparison of Existing Platforms}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.18\textwidth}|p{0.24\textwidth}|p{0.24\textwidth}|p{0.24\textwidth}|}
        \hline
        \textbf{Platform} & \textbf{Main Strength} & \textbf{Main Limitation} & \textbf{Implication for the Proposed System} \\
        \hline
        HACOM & Provides a local Vietnamese Build PC workflow connected with product selection, cost estimation, cart, and ordering. & The build experience is mainly sales-oriented, so compatibility reasoning should still be presented clearly. & The proposed system should combine local e-commerce flow with explicit compatibility filtering. \\
        \hline
        GEARVN & Provides a Vietnamese retail ecosystem with Build PC, product categories, cart, warranty lookup, and store support. & The platform emphasizes retail convenience, while detailed backend compatibility rules may not be fully visible to users. & The proposed system should keep purchasing convenient while making compatibility enforcement clear and consistent. \\
        \hline
    \end{tabular}
    \caption{Comparison of existing PC building platforms.}
    \label{tab:existing-pc-building-platforms}
\end{table}

The comparison shows that PC building support is relevant in the Vietnamese market. The proposed system follows this direction by integrating compatibility filtering into product selection, cart management, and ordering, while keeping the rule-based mechanism explicit and maintainable.

\section{Compatibility Support Approaches}

Compatibility support can be implemented in different ways. Some systems allow users to select components freely and show warnings when incompatibilities are detected. This gives users flexibility, but it still requires them to understand and fix the problem afterward.

\vspace{0.1cm}

Another approach is to guide users before invalid combinations are created. The system filters available component options based on the current build state, so users are less likely to select incompatible hardware. This approach is close to constraint-based recommendation, where explicit rules or constraints are used to identify suitable items \cite{le2023constraint}. It is easier for beginner users because it reduces invalid choices during the selection process.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.24\textwidth}|p{0.34\textwidth}|p{0.32\textwidth}|}
        \hline
        \textbf{Approach} & \textbf{Description} & \textbf{Suitability for This Project} \\
        \hline
        Warning-based checking & Users select components first, then the system reports possible compatibility issues. & Useful, but still requires users to correct invalid builds manually. \\
        \hline
        Filtering-based support & The system displays only suitable options based on the current component selections. & Suitable because it prevents many invalid choices before they happen. \\
        \hline
        Recommendation-based support & The system suggests components or complete builds based on budget, purpose, performance, or preferences. & Useful as a supporting feature, but advanced personalization is outside the main scope. \\
        \hline
    \end{tabular}
    \caption{Common compatibility support approaches.}
    \label{tab:compatibility-support-approaches}
\end{table}

\subsection{Filtering-Based Support}

Filtering-based support is the main approach used in the proposed system. When a user selects a component, the system uses that selection as a constraint for later choices. For example, after an AM5 processor is selected, the motherboard list can be filtered to show only AM5-compatible motherboards. Similarly, if a DDR5 motherboard is selected, the memory list can be limited to DDR5 modules.

\vspace{0.1cm}

The same idea applies to physical and power-related constraints. A selected graphics card can affect case options based on GPU length, while selected CPU and GPU components can affect suitable power supply options based on estimated wattage.

\subsection{Recommendation-Based Support}

Recommendation-based support attempts to suggest suitable components or complete configurations based on user needs such as budget, usage purpose, or performance priority. In e-commerce, recommendation techniques are often used to help users find relevant products from a large product set \cite{dutta2011}. For example, the system may suggest a gaming-oriented build for users who prioritize graphics performance or a low-cost build for users with a limited budget.

\vspace{0.1cm}

In this project, recommendation is implemented in a limited rule-based form through build suggestion presets. More advanced recommendation methods, such as learning from user behavior, product popularity, or historical purchase data, are considered future work rather than the main focus of the current system. This decision is suitable because maintaining configuration knowledge can become complex when the product domain grows \cite{felfernig2021}.

\section{Rule-Based Compatibility Validation}

The proposed system uses rule-based validation to support compatibility filtering. In this approach, compatibility is evaluated using predefined rules derived from hardware specifications. Rule-based or constraint-based approaches are commonly used in product configuration because they can represent valid and invalid combinations clearly \cite{schneeweiss2011}.

\vspace{0.1cm}

For example, CPU and motherboard compatibility can be checked by comparing socket types:

\begin{equation}
CPU_{socket} = Motherboard_{socket}
\end{equation}

If the socket values match, the CPU and motherboard satisfy this compatibility rule. Similar rules can be applied to memory type, memory speed, PCIe support, storage interface, case clearance, cooling size, PSU wattage, and PSU form factor.

\vspace{0.1cm}

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.26\textwidth}|p{0.58\textwidth}|}
        \hline
        \textbf{Rule Area} & \textbf{Example Validation} \\
        \hline
        CPU and motherboard & CPU socket must match motherboard socket. \\
        \hline
        Motherboard and memory & Memory type, speed, and module count must be supported. \\
        \hline
        GPU and case & GPU length must fit within the selected case clearance. \\
        \hline
        Storage and motherboard & NVMe storage requires an M.2 slot; SATA storage requires a SATA port. \\
        \hline
        Power supply & PSU wattage must satisfy the estimated required power of selected components. \\
        \hline
        Cooling and case & Air cooler height or radiator size must fit the selected case. \\
        \hline
    \end{tabular}
    \caption{Examples of rule-based compatibility validation.}
    \label{tab:rule-based-validation-examples}
\end{table}

The advantage of this approach is that it is transparent and maintainable. Each rule can be explained clearly, updated when product specifications change, and reused across manual component selection and rule-based build suggestion. This makes it suitable for the proposed system, where correctness and understandability are more important than complex personalization.

\section{Chapter Summary}

This chapter reviewed existing PC building platforms and common compatibility support approaches. The review shows that Vietnamese retailers such as HACOM and GEARVN already connect PC building tools with product browsing and purchasing. The proposed system follows this practical direction while focusing on explicit rule-based compatibility filtering.

\vspace{0.1cm}

Based on this review, the system adopts filtering-based support and rule-based compatibility validation. These approaches are suitable because many PC compatibility constraints can be represented through clear hardware rules, and the same rules can be reused in both manual PC building and rule-based build suggestion.
