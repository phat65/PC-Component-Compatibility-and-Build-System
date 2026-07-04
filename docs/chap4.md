\chapter{Compatibility Checking Mechanism}

\section{Compatibility Model}

The compatibility checking mechanism is the core technical feature of the proposed PC Component Compatibility and Build System. A PC build is treated as a group of selected hardware components, including CPU, motherboard, memory, GPU, storage, power supply, PC case, and cooling device. Each selected component affects the list of components that can be selected next.

\vspace{0.1cm}

The system uses a rule-based filtering approach. Instead of allowing users to select any component and then displaying warnings after an invalid combination is created, the system evaluates the current build state and displays only compatible component options. This makes the build process more direct and reduces the chance of selecting unsuitable hardware.

\vspace{0.1cm}

Compatibility checking depends on technical specifications stored for each component type. General product information such as name, price, brand, image, status, lifecycle status, and inventory quantity is stored in the product data model, while hardware-specific attributes are stored in component-specific tables.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.22\textwidth}|p{0.36\textwidth}|p{0.34\textwidth}|}
        \hline
        \textbf{Component} & \textbf{Main Specifications} & \textbf{Compatibility Purpose} \\
        \hline
        CPU & Socket, TDP, memory speed, memory channels, PCIe version, integrated graphics & Checks motherboard socket, memory support, PCIe support, and power estimation. \\
        \hline
        Motherboard & Socket, form factor, memory type, memory slots, PCIe version, M.2 slots, SATA ports & Acts as the central component for CPU, memory, storage, GPU, and case compatibility. \\
        \hline
        Memory & Type, capacity, speed, TDP, number of modules & Checks memory type, speed support, slot count, and CPU memory support. \\
        \hline
        GPU & PCIe version, TDP, length, VRAM & Checks PCIe support, case clearance, and power requirement. \\
        \hline
        Storage & Type, capacity, interface & Checks whether the motherboard supports NVMe or SATA storage. \\
        \hline
        Power Supply & Wattage, efficiency, form factor & Checks required power and case PSU form factor support. \\
        \hline
        PC Case & Form factor, GPU clearance, CPU cooler height, PSU form factor & Checks motherboard size, GPU length, cooler clearance, and PSU fit. \\
        \hline
        Cooling & Type, fan size, radiator size, max TDP & Checks whether the cooler can fit inside the selected case. \\
        \hline
    \end{tabular}
    \caption{Main specifications used for compatibility checking.}
    \label{tab:chap4-compatibility-specifications}
\end{table}

\section{Build Selection Flow}

The build process is based on the current build state stored in the user's session. When a user starts building a PC, the build state is empty. After each component selection, the selected component is stored in the session and becomes a constraint for later selections.

\vspace{0.1cm}

For each component page, the system loads available products from the corresponding repository, validates them against the current build state, and returns only compatible products to the view. When the user submits a selected component, the system validates the selected product again before saving it into the build state. This second validation step ensures that compatibility is enforced by the backend service layer, not only by the displayed interface.

\vspace{0.1cm}

The general flow can be summarized as follows:

\begin{enumerate}
    \item The user opens a component selection page.
    \item The system reads the current build state from the session.
    \item Available products are loaded from the database.
    \item Each product is checked against the current build state.
    \item Compatible products are displayed to the user.
    \item The selected product is validated again before being saved.
\end{enumerate}

\vspace{0.1cm}

% Flowchart suggestion:
% Insert a compatibility filtering flowchart here.
% Suggested flow:
% Open component page -> Read current build state from session -> Load available products -> Check each product with compatibility rules -> Display compatible products -> User selects product -> Validate selected product again -> Save selected product to session
% Caption suggestion:
% Figure 4.x: Compatibility filtering flow during component selection.

This design helps prevent invalid choices before they are made. It also allows users to build a PC step by step without needing to manually understand every hardware rule.

\section{Compatibility Rules}

The compatibility rules are grouped by component type. Each rule compares the candidate component with the components already selected in the current build. If one required condition is not satisfied, the candidate component is removed from the compatible list.

\subsection{CPU and Motherboard}

CPU and motherboard compatibility is determined mainly by socket matching. A CPU can only be used with a motherboard that supports the same socket type. For example, an LGA1700 CPU requires an LGA1700 motherboard, while an AM5 CPU requires an AM5 motherboard.

\vspace{0.1cm}

This rule is applied in both directions. If the user selects a motherboard first, the CPU list is filtered by the motherboard socket. If the user selects a CPU first, the motherboard list is filtered by the CPU socket.

\subsection{Memory}

Memory compatibility is checked using memory type, number of modules, and memory speed. The memory type must match the motherboard memory type, such as DDR4 or DDR5. The number of memory modules must not exceed the available motherboard slots.

\vspace{0.1cm}

The system also checks memory speed against both motherboard and CPU support. If the selected memory speed exceeds the maximum speed supported by either component, the memory is considered incompatible. In addition, the system checks whether the number of memory modules is reasonable for the CPU memory channel support.

\subsection{GPU}

GPU compatibility is checked through PCIe support, physical clearance, and power requirement. The GPU PCIe version must be supported by the selected CPU and motherboard. If a case has already been selected, the GPU length must not exceed the maximum GPU length supported by the case.

\vspace{0.1cm}

Because the GPU is usually one of the highest power-consuming components, the system also checks whether the selected power supply can provide enough wattage for the build after the GPU is included.

\subsection{Storage}

Storage compatibility is checked based on the storage interface and motherboard support. If the selected storage uses NVMe, the motherboard must have an available M.2 slot. If the selected storage uses SATA, the motherboard must have a SATA port.

\vspace{0.1cm}

If no motherboard has been selected yet, storage products are treated as selectable because there is no motherboard constraint to evaluate at that point.

\subsection{Case and Cooling}

The PC case controls several physical compatibility conditions. The case must support the selected motherboard form factor, provide enough clearance for the selected GPU, support the selected PSU form factor, and provide enough space for the selected cooling device.

\vspace{0.1cm}

Cooling compatibility is mainly checked against the selected case. For air coolers, the system estimates cooler height and compares it with the maximum CPU cooler height supported by the case. For liquid coolers, radiator size is compared with the case form factor limitation.

\subsection{Power Supply}

Power supply compatibility is checked using estimated power consumption and PSU form factor. The required power is calculated from selected components such as CPU, GPU, memory, storage, and cooling, then multiplied by a safety buffer.

\vspace{0.1cm}

\[
RequiredPower = (CPU_{TDP} + GPU_{TDP} + Memory_{TDP} + StoragePower + Cooling_{TDP}) \times 1.2
\]

\vspace{0.1cm}

The selected power supply must satisfy:

\[
PSU_{Wattage} \geq RequiredPower
\]

\vspace{0.1cm}

The system also checks whether the PSU form factor is supported by the selected case.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.28\textwidth}|p{0.58\textwidth}|}
        \hline
        \textbf{Rule Group} & \textbf{Main Validation Conditions} \\
        \hline
        CPU and motherboard & CPU socket must match motherboard socket. \\
        \hline
        Memory & Memory type, module count, and speed must be supported by motherboard and CPU. \\
        \hline
        GPU & PCIe version, GPU length, and required power must be supported. \\
        \hline
        Storage & NVMe requires M.2 slot; SATA requires SATA port. \\
        \hline
        Case & Case must support motherboard form factor, GPU length, PSU form factor, and cooling clearance. \\
        \hline
        Power supply & PSU wattage must be greater than or equal to estimated required power. \\
        \hline
        Cooling & Cooler size or radiator size must fit the selected case. \\
        \hline
    \end{tabular}
    \caption{Summary of compatibility validation rules.}
    \label{tab:chap4-compatibility-rule-summary}
\end{table}

\section{Filtering and Validation Mechanism}

The filtering mechanism is implemented in the application layer through service classes. Controllers are responsible for routing, request parameters, model attributes, and redirects, while compatibility decisions are handled by dedicated service classes.

\vspace{0.1cm}

The compatibility service acts as the central validation entry point. It delegates validation to component-specific validators, such as CPU validator, motherboard validator, memory validator, GPU validator, storage validator, case validator, power supply validator, and cooling validator. Each validator returns a compatibility result that indicates whether the component is compatible and provides a reason when the component is rejected.

\vspace{0.1cm}

The system applies compatibility validation in three main situations:

\begin{itemize}
    \item When displaying a component list, incompatible products are filtered out.
    \item When a user selects a component, the selected product is validated again before being stored in the session.
    \item When a suggested build is applied, the complete build is validated before being accepted.
\end{itemize}

\vspace{0.1cm}

This layered validation approach makes the mechanism more reliable. The user interface guides users by showing compatible options, while the backend service layer remains responsible for enforcing the actual rules.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.30\textwidth}|p{0.58\textwidth}|}
        \hline
        \textbf{Implementation Element} & \textbf{Role in the Compatibility Mechanism} \\
        \hline
        \texttt{BuildService} & Loads component candidates, filters compatible products for manual build pages, and returns sorted results to controllers. \\
        \hline
        \texttt{CompatibilityService} & Acts as the central validation entry point and delegates component checks to specific compatibility validators. \\
        \hline
        Component compatibility validators & Validate each component type, such as CPU, motherboard, memory, GPU, storage, case, power supply, and cooling. \\
        \hline
        \texttt{CompatibilityResult} & Represents the validation result, including whether a component is compatible and the reason when it is rejected. \\
        \hline
        \texttt{BuildPowerCalculator} & Estimates required build wattage based on selected components and supports power supply validation. \\
        \hline
        \texttt{RuleBasedBuildService} & Generates suggested builds by selecting compatible components according to preset purpose, budget, performance score, and fallback rules. \\
        \hline
    \end{tabular}
    \caption{Implementation elements of the compatibility mechanism.}
    \label{tab:chap4-compatibility-implementation-elements}
\end{table}

% Diagram suggestion:
% Insert a service-level compatibility validation diagram here if needed.
% Suggested structure:
% Build Controller -> BuildService -> CompatibilityService -> Component Validator -> CompatibilityResult
% Caption suggestion:
% Figure 4.x: Service-level structure of the compatibility validation mechanism.

\section{Rule-Based Build Suggestion}

Besides manual component selection, the system also supports rule-based build suggestion. In this function, the user selects a build purpose and enters a budget. The system then generates a suggested PC configuration based on predefined presets, component budget distribution, product performance score, and compatibility rules.

\vspace{0.1cm}

The available presets include high-end gaming, mid-range gaming, workstation, office or productivity, budget gaming, and streaming or content creation. Each preset defines a suggested minimum budget, the percentage of budget assigned to each component type, and minimum performance expectations for important components.

\begin{table}[H]
    \centering
    \footnotesize
    \renewcommand{\arraystretch}{1.25}
    \begin{tabular}{|p{0.26\textwidth}|p{0.42\textwidth}|p{0.22\textwidth}|}
        \hline
        \textbf{Preset} & \textbf{Purpose} & \textbf{Main Priority} \\
        \hline
        Gaming - High End & AAA gaming at high resolution and high settings & GPU, CPU, memory \\
        \hline
        Gaming - Mid Range & Gaming at 1080p or 1440p & GPU, CPU \\
        \hline
        Workstation & Rendering, video editing, CAD, and productivity workloads & CPU, memory, GPU \\
        \hline
        Office/Productivity & Office work, web browsing, and light tasks & CPU, motherboard, storage \\
        \hline
        Budget Gaming & Entry-level gaming with controlled cost & GPU, CPU, motherboard \\
        \hline
        Streaming/Content Creation & Gaming combined with streaming or content creation & CPU, GPU, memory \\
        \hline
    \end{tabular}
    \caption{Build suggestion presets.}
    \label{tab:chap4-build-suggestion-presets}
\end{table}

The suggestion process selects components in a fixed order: motherboard, CPU, memory, GPU, storage, power supply, cooling, and case. The motherboard is selected early because it provides many important constraints, including CPU socket, memory type, PCIe version, storage interfaces, and form factor.

\vspace{0.1cm}

The build suggestion flow can be summarized as follows:

% Required package in the LaTeX preamble:
% \usepackage{tikz}
\begin{figure}[H]
    \centering
    \footnotesize
    \begin{tikzpicture}[
        flowstep/.style={
            draw,
            rounded corners,
            align=center,
            text width=0.78\linewidth,
            minimum height=0.75cm
        },
        arrow/.style={->, thick}
    ]
        \node[flowstep] (step1) at (0,0) {User selects build purpose and enters budget};
        \node[flowstep] (step2) at (0,-1.25) {System selects the matching build preset};
        \node[flowstep] (step3) at (0,-2.50) {Budget is distributed across component groups};
        \node[flowstep] (step4) at (0,-3.75) {Motherboard is selected as the initial constraint component};
        \node[flowstep] (step5) at (0,-5.00) {CPU, memory, GPU, storage, power supply, cooling, and case are selected using budget, performance score, and compatibility rules};
        \node[flowstep] (step6) at (0,-6.50) {If no candidate is found, the system relaxes the budget condition and searches again};
        \node[flowstep] (step7) at (0,-7.75) {Complete build is validated before being applied to the build session};

        \draw[arrow] (step1) -- (step2);
        \draw[arrow] (step2) -- (step3);
        \draw[arrow] (step3) -- (step4);
        \draw[arrow] (step4) -- (step5);
        \draw[arrow] (step5) -- (step6);
        \draw[arrow] (step6) -- (step7);
    \end{tikzpicture}
    \caption{Rule-based build suggestion flow.}
    \label{fig:chap4-build-suggestion-flow}
\end{figure}

\vspace{0.1cm}

For each component type, the system first searches for products that fit the target budget range and performance requirement. If no compatible component is found in the preferred range, the system relaxes the budget condition and searches for another compatible candidate. This fallback behavior is especially useful for power supply and case selection because these components may need to exceed the initial budget range to satisfy power and physical constraints.

\vspace{0.1cm}

After a suggested build is generated, it is converted into the same build state format used by the manual build flow. Before the suggested build is applied to the session, the system validates the complete build again. This ensures that the suggestion feature does not bypass compatibility rules.

\section{Advantages and Limitations}

The proposed mechanism has several advantages. First, it reduces invalid selections by filtering products before they are shown to users. Second, the rule-based design is transparent because each decision is based on explicit specifications such as socket, memory type, PCIe version, storage interface, physical clearance, and wattage. Third, the same compatibility rules are reused in both manual selection and build suggestion, which keeps the system behavior consistent.

\vspace{0.1cm}

However, the mechanism also has limitations. Its accuracy depends on correct product specification data. If a product has incorrect socket, form factor, or dimension information, the filtering result may also be incorrect. The mechanism also focuses on major compatibility constraints and does not cover every possible real-world issue, such as BIOS version requirements, exact RAM qualified vendor lists, detailed case radiator mounting positions, or cable clearance.

\vspace{0.1cm}

The power calculation is also an estimation. It uses selected component TDP values and a safety buffer, which is suitable for guiding users but not equivalent to a complete electrical power analysis. Finally, the build suggestion function is rule-based, so it does not learn from user behavior or historical purchase data.

\section{Chapter Summary}

This chapter described the compatibility checking mechanism of the proposed system. The system uses a rule-based filtering approach to display compatible components based on the current build state, while also validating selected components again before saving them. The chapter also presented the main compatibility rules, the filtering and validation mechanism, the rule-based build suggestion function, and the limitations of the proposed approach.


 
