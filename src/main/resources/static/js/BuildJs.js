document.addEventListener("DOMContentLoaded", function () {
    const priceTotal = document.getElementById("priceTotal");
    const productGrid = document.querySelector(".product-grid");
    const mainContent = document.querySelector(".build-page .main-content:not(.start-screen)");
    const detailHeader = document.querySelector(".build-page .detail-header");

    function componentTitleFromHeader(text) {
        const normalized = String(text || "").replace(/^select\s+/i, "").trim().toUpperCase();
        const titles = {
            GPU: "Graphics Cards",
            CPU: "Processors",
            MOTHERBOARD: "Motherboards",
            MAINBOARD: "Motherboards",
            CASE: "PC Cases",
            COOLING: "Cooling",
            MEMORY: "Memory",
            STORAGE: "Storage",
            PSU: "Power Supplies",
            OTHER: "Add-ons"
        };

        return titles[normalized] || normalized || "Components";
    }

    function currentCartCount() {
        const existingCount = document.querySelector(".cart-count");
        return existingCount ? existingCount.textContent.trim() || "0" : "0";
    }

    if (mainContent && detailHeader && !mainContent.querySelector(".build-topbar")) {
        const topbar = document.createElement("div");
        topbar.className = "build-topbar";

        const title = document.createElement("h1");
        title.className = "build-component-title";
        title.textContent = componentTitleFromHeader(detailHeader.textContent);

        const actions = document.createElement("div");
        actions.className = "build-top-actions";
        actions.innerHTML = `
            <a href="/cart" class="build-icon-link site-cart-link" aria-label="Cart">
                <img src="/images/shopping_cart_40dp_E3E3E3_FILL0_wght400_GRAD0_opsz40.svg" alt="">
                <span class="cart-count">${currentCartCount()}</span>
            </a>
            <a href="/profile" class="build-icon-link" aria-label="Account">
                <img src="/images/account_circle_40dp_E3E3E3_FILL0_wght400_GRAD0_opsz40.svg" alt="">
            </a>
        `;

        topbar.appendChild(title);
        topbar.appendChild(actions);
        mainContent.prepend(topbar);
    }

    const knownHiddenIds = [
        "selectedMainboardId",
        "selectedCpuId",
        "selectedGpuId",
        "selectedCaseId",
        "selectedCoolingId",
        "selectedMemoryId",
        "selectedStorageId",
        "selectedPsuId",
        "selectedOtherId"
    ];

    const fields = {
        name: document.getElementById("detailName"),
        image: document.querySelector(".detail-image"),
        socket: document.getElementById("detailSocket"),
        ram: document.getElementById("detailRAM"),
        form: document.getElementById("detailForm"),
        chipset: document.getElementById("detailChipset"),
        tdp: document.getElementById("detailTdp") || document.getElementById("detailTDP"),
        igpu: document.getElementById("detailIGPU"),
        pcie: document.getElementById("detailPCIe"),
        vram: document.getElementById("detailVRAM"),
        formFactor: document.getElementById("detailFormFactor"),
        gpuLength: document.getElementById("detailGpuLength"),
        cpuHeight: document.getElementById("detailCpuHeight"),
        psuFormFactor: document.getElementById("detailPsuFormFactor"),
        type: document.getElementById("detailType"),
        fanSize: document.getElementById("detailFanSize"),
        capacity: document.getElementById("detailCapacity"),
        modules: document.getElementById("detailModules"),
        speed: document.getElementById("detailSpeed"),
        wattage: document.getElementById("detailWattage"),
        efficiency: document.getElementById("detailEfficiency"),
        modular: document.getElementById("detailModular"),
        description: document.getElementById("detailDescription"),
        price: document.getElementById("detailPrice")
    };

    function findHiddenInputForCard(card) {
        const form = card.closest("form");
        if (form) {
            const preferred = form.querySelector("input[type=hidden][id^='selected']");
            if (preferred) {
                return preferred;
            }

            const fallback = form.querySelector("input[type=hidden]");
            if (fallback) {
                return fallback;
            }
        }

        for (const id of knownHiddenIds) {
            const input = document.getElementById(id);
            if (input) {
                return input;
            }
        }

        return document.querySelector("input[type=hidden]");
    }

    function parseMoney(text) {
        const cleaned = String(text || "").replace(/[^\d.-]/g, "");
        const value = parseFloat(cleaned);
        return Number.isNaN(value) ? 0 : value;
    }

    function parsePriceFromCard(card) {
        const dataPrice = card.getAttribute("data-price");
        if (dataPrice) {
            return parseMoney(dataPrice);
        }

        const priceElement = card.querySelector("strong");
        return priceElement ? parseMoney(priceElement.innerText) : 0;
    }

    function formatMoney(value) {
        return "$" + (Number.isInteger(value) ? value.toFixed(0) : value.toFixed(2));
    }

    function readDisplayedTotal() {
        return priceTotal ? parseMoney(priceTotal.innerText) : 0;
    }

    const originalTotalPrice = readDisplayedTotal();
    const initiallySelectedCards = Array.from(document.querySelectorAll(".product-card.selected"));
    const initialPageSelectionPrice = initiallySelectedCards
        .reduce((sum, card) => sum + parsePriceFromCard(card), 0);

    function value(card, name) {
        return card.getAttribute("data-" + name) || "";
    }

    function normalizeFilterText(text) {
        return String(text || "").trim().toLowerCase();
    }

    function createFilterOption(value, label) {
        const option = document.createElement("option");
        option.value = value;
        option.textContent = label;
        return option;
    }

    function enhanceFilterBar() {
        const filterBar = document.querySelector(".build-page .filter-bar");
        if (!filterBar || filterBar.querySelector(".build-brand-filter")) {
            return;
        }

        const brandSelect = document.createElement("select");
        brandSelect.className = "build-brand-filter";
        brandSelect.setAttribute("aria-label", "Filter by brand");
        brandSelect.appendChild(createFilterOption("", "All Brands"));

        document.querySelectorAll("#filterPopup input[name='brands']").forEach(input => {
            brandSelect.appendChild(createFilterOption(input.value, input.value));
        });

        const priceSelect = document.createElement("select");
        priceSelect.className = "build-price-filter";
        priceSelect.setAttribute("aria-label", "Filter by price");
        [
            ["", "All Prices"],
            ["0-100", "Under $100"],
            ["100-300", "$100 - $300"],
            ["300-700", "$300 - $700"],
            ["700-1200", "$700 - $1200"],
            ["1200-", "Over $1200"]
        ].forEach(([value, label]) => priceSelect.appendChild(createFilterOption(value, label)));

        const label = document.createElement("span");
        label.className = "build-filter-label";
        label.textContent = "Filter by:";

        const searchBox = document.getElementById("searchBox");
        if (searchBox && searchBox.nextSibling) {
            filterBar.insertBefore(label, searchBox.nextSibling);
            filterBar.insertBefore(brandSelect, label.nextSibling);
            filterBar.insertBefore(priceSelect, brandSelect.nextSibling);
        } else {
            filterBar.appendChild(label);
            filterBar.appendChild(brandSelect);
            filterBar.appendChild(priceSelect);
        }

        brandSelect.addEventListener("change", filterProducts);
        priceSelect.addEventListener("change", filterProducts);
    }

    function setText(element, label, content, suffix) {
        if (!element) {
            return;
        }

        const displayValue = content || "...";
        element.innerText = label + ": " + displayValue + (content && suffix ? suffix : "");
    }

    function updateDetailImage(card, name) {
        if (!fields.image) {
            return;
        }

        const cardImage = card.querySelector(".product-image img");
        if (cardImage && cardImage.src) {
            fields.image.innerHTML = "";
            const detailImg = document.createElement("img");
            detailImg.src = cardImage.src;
            detailImg.alt = name || "Product image";
            fields.image.appendChild(detailImg);
            return;
        }

        fields.image.innerHTML = '<div class="build-no-image">No Image Available</div>';
    }

    function updateDetailPanel(card) {
        const name = value(card, "name");
        const price = parsePriceFromCard(card);

        if (fields.name) {
            fields.name.innerText = name || "Selected component";
        }

        updateDetailImage(card, name);

        if (value(card, "pcie")) {
            setText(fields.pcie || fields.socket, "PCIe", value(card, "pcie"));
        } else {
            setText(fields.socket, "Socket", value(card, "socket"));
        }

        if (value(card, "vram")) {
            setText(fields.vram || fields.ram, "VRAM", value(card, "vram"), " GB");
        } else {
            setText(fields.ram, "RAM", value(card, "ramtype"));
        }

        setText(fields.form, "Form Factor", value(card, "formfactor"));
        setText(fields.chipset, "Chipset", value(card, "chipset"));
        setText(fields.tdp, "TDP", value(card, "tdp"), value(card, "tdp") ? "W" : "");
        setText(fields.igpu, "IGPU", value(card, "igpu") === "true" ? "Yes" : (value(card, "igpu") ? "No" : ""));
        setText(fields.formFactor, "Form Factor", value(card, "formfactor"));
        setText(fields.gpuLength, "GPU Max Length", value(card, "gpumaxlength"), "mm");
        setText(fields.cpuHeight, "CPU Cooler Max Height", value(card, "cpumaxheight"), "mm");
        setText(fields.psuFormFactor, "PSU Form Factor", value(card, "psuformfactor"));
        setText(fields.type, "Type", value(card, "type"));
        setText(fields.fanSize, "Fan Size", value(card, "fansize"));
        setText(fields.capacity, "Capacity", value(card, "capacity"), "GB");
        setText(fields.modules, "Modules", value(card, "modules"));
        setText(fields.speed, "Speed", value(card, "speed"), "MHz");
        setText(fields.wattage, "Wattage", value(card, "wattage"), "W");
        setText(fields.efficiency, "Efficiency", value(card, "efficiency"));
        setText(fields.modular, "Modular", value(card, "modular") === "true" ? "Yes" : (value(card, "modular") ? "No" : ""));
        if (fields.description) {
            fields.description.innerText = value(card, "description") || "No description available";
        }

        if (fields.price) {
            fields.price.innerText = "Price: " + formatMoney(price);
        }
    }

    function isMultiSelectForm(form) {
        return form && form.dataset.multiSelect === "true";
    }

    function selectedCardsForForm(form) {
        if (!form) {
            return [];
        }

        return Array.from(form.querySelectorAll(".product-card.selected"));
    }

    function syncMultiSelectHiddenInputs(form) {
        const container = form.querySelector("[data-selected-items]");
        if (!container) {
            return;
        }

        const inputName = container.dataset.selectedItems || "selectedIds";
        container.innerHTML = "";

        selectedCardsForForm(form).forEach(selectedCard => {
            const id = selectedCard.getAttribute("data-id");
            if (!id) {
                return;
            }

            const input = document.createElement("input");
            input.type = "hidden";
            input.name = inputName;
            input.value = id;
            container.appendChild(input);
        });
    }

    function updateTotalForSelection(selectedPrice) {
        if (!priceTotal) {
            return;
        }

        const tempTotal = originalTotalPrice - initialPageSelectionPrice + selectedPrice;
        priceTotal.innerText = formatMoney(Math.max(tempTotal, 0));
    }

    function selectCard(card) {
        const form = card.closest("form");

        if (isMultiSelectForm(form)) {
            card.classList.toggle("selected");
            syncMultiSelectHiddenInputs(form);
            updateDetailPanel(card);

            const selectedPrice = selectedCardsForForm(form)
                .reduce((sum, selectedCard) => sum + parsePriceFromCard(selectedCard), 0);
            updateTotalForSelection(selectedPrice);
            return;
        }

        document.querySelectorAll(".product-card.selected").forEach(selected => {
            selected.classList.remove("selected");
        });
        card.classList.add("selected");

        const id = card.getAttribute("data-id");
        const hiddenInput = findHiddenInputForCard(card);
        if (hiddenInput && id != null) {
            hiddenInput.value = id;
        }

        updateDetailPanel(card);
        updateTotalForSelection(parsePriceFromCard(card));
    }

    function ensureSearchEmptyState() {
        if (!productGrid || document.getElementById("buildSearchEmpty")) {
            return;
        }

        const emptyState = document.createElement("div");
        emptyState.id = "buildSearchEmpty";
        emptyState.className = "build-grid-empty";
        emptyState.textContent = "No matching components found.";
        emptyState.hidden = true;
        productGrid.appendChild(emptyState);
    }

    function updateSearchEmptyState() {
        const emptyState = document.getElementById("buildSearchEmpty");
        if (!emptyState) {
            return;
        }

        const cards = Array.from(document.querySelectorAll(".product-card"));
        emptyState.hidden = cards.length === 0 || cards.some(card => card.style.display !== "none");
    }

    document.addEventListener("click", function (event) {
        const card = event.target.closest(".product-card");
        if (!card) {
            return;
        }

        selectCard(card);
    });

    document.querySelectorAll("form[data-require-selection='true']").forEach(form => {
        form.addEventListener("submit", function () {
            return true;
        });
    });

    const filterBtn = document.getElementById("openFilterPopup");
    const filterPopup = document.getElementById("filterPopup");
    const closeFilterBtn = document.getElementById("closeFilterPopup");

    if (filterBtn && filterPopup && closeFilterBtn) {
        filterBtn.addEventListener("click", function () {
            filterPopup.style.display = "flex";
        });
        closeFilterBtn.addEventListener("click", function () {
            filterPopup.style.display = "none";
        });
        filterPopup.addEventListener("click", function (event) {
            if (event.target === filterPopup) {
                filterPopup.style.display = "none";
            }
        });
    }

    const openOverviewBtn = document.getElementById("openOverviewBtn");
    const overviewPopup = document.getElementById("overviewPopup");
    const closeOverviewBtn = document.getElementById("closeOverviewBtn");
    const closeOverviewFooterBtn = document.getElementById("closeOverviewFooterBtn");

    function closeOverview() {
        if (overviewPopup) {
            overviewPopup.style.display = "none";
        }
    }

    if (openOverviewBtn && overviewPopup) {
        openOverviewBtn.addEventListener("click", function (event) {
            event.preventDefault();
            overviewPopup.style.display = "flex";
        });
    }

    closeOverviewBtn?.addEventListener("click", closeOverview);
    closeOverviewFooterBtn?.addEventListener("click", closeOverview);

    if (overviewPopup) {
        overviewPopup.addEventListener("click", function (event) {
            if (event.target === overviewPopup) {
                closeOverview();
            }
        });
    }

    const saveAsImageBtn = document.getElementById("saveAsImageBtn");
    if (saveAsImageBtn) {
        saveAsImageBtn.addEventListener("click", function () {
            const overviewContent = document.querySelector(".overview-popup-content");
            if (!overviewContent || typeof html2canvas !== "function") {
                alert("Cannot save overview image right now.");
                return;
            }

            const footer = overviewContent.querySelector(".overview-footer");
            const closeBtn = overviewContent.querySelector(".close-overview");

            if (footer) {
                footer.hidden = true;
            }
            if (closeBtn) {
                closeBtn.hidden = true;
            }

            overviewContent.classList.add("capturing-image");

            setTimeout(() => {
                html2canvas(overviewContent, {
                    backgroundColor: "#ffffff",
                    scale: 3,
                    logging: false,
                    useCORS: true,
                    allowTaint: true,
                    scrollY: 0,
                    scrollX: 0,
                    windowHeight: overviewContent.scrollHeight,
                    height: overviewContent.scrollHeight,
                    width: 1000,
                    imageTimeout: 0,
                    removeContainer: true
                }).then(function (canvas) {
                    if (footer) {
                        footer.hidden = false;
                    }
                    if (closeBtn) {
                        closeBtn.hidden = false;
                    }
                    overviewContent.classList.remove("capturing-image");

                    const link = document.createElement("a");
                    const timestamp = new Date().toISOString().slice(0, 10);
                    link.download = "PC-Build-Overview-" + timestamp + ".png";
                    link.href = canvas.toDataURL("image/png", 1.0);
                    link.click();
                }).catch(function () {
                    if (footer) {
                        footer.hidden = false;
                    }
                    if (closeBtn) {
                        closeBtn.hidden = false;
                    }
                    overviewContent.classList.remove("capturing-image");
                    alert("Error saving image. Please try again.");
                });
            }, 100);
        });
    }

    const searchBox = document.getElementById("searchBox");
    if (searchBox) {
        let searchTimeout;

        searchBox.addEventListener("input", function () {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(filterProducts, 300);
        });

        searchBox.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                clearTimeout(searchTimeout);
                filterProducts();
            }
        });

        searchBox.addEventListener("change", function () {
            if (!this.value.trim()) {
                clearFilter();
            }
        });
    }

    ensureSearchEmptyState();
    enhanceFilterBar();
    if (initiallySelectedCards.length > 0) {
        updateDetailPanel(initiallySelectedCards[0]);
    } else {
        const firstCard = document.querySelector(".product-card");
        if (firstCard) {
            updateDetailPanel(firstCard);
        }
    }
    updateSearchEmptyState();
});

function normalizeSearchText(text) {
    return String(text || "")
        .trim()
        .toLowerCase()
        .replace(/\s+/g, " ")
        .replace(/[^\w\s-]/g, "");
}

function filterProducts() {
    const searchBox = document.getElementById("searchBox");
    const brandSelect = document.querySelector(".build-brand-filter");
    const priceSelect = document.querySelector(".build-price-filter");
    const query = normalizeSearchText(searchBox ? searchBox.value : "");
    const selectedBrand = normalizeFilterText(brandSelect ? brandSelect.value : "");
    const selectedPrice = priceSelect ? priceSelect.value : "";
    const cards = document.querySelectorAll(".product-card");

    cards.forEach(card => {
        const name = normalizeSearchText(card.getAttribute("data-name"));
        const brand = normalizeFilterText(card.getAttribute("data-brand"));
        const price = parseFloat(card.getAttribute("data-price") || "0");

        const matchesSearch = !query || name.includes(query);
        const matchesBrand = !selectedBrand || brand === selectedBrand;
        const matchesPrice = !selectedPrice || priceMatchesRange(price, selectedPrice);

        card.style.display = matchesSearch && matchesBrand && matchesPrice ? "flex" : "none";
    });

    const emptyState = document.getElementById("buildSearchEmpty");
    if (emptyState) {
        emptyState.hidden = Array.from(cards).some(card => card.style.display !== "none");
    }
}

function normalizeFilterText(text) {
    return String(text || "").trim().toLowerCase();
}

function priceMatchesRange(price, range) {
    const [minValue, maxValue] = String(range).split("-");
    const min = minValue ? parseFloat(minValue) : 0;
    const max = maxValue ? parseFloat(maxValue) : Number.POSITIVE_INFINITY;
    return price >= min && price < max;
}

function clearFilter() {
    const searchBox = document.getElementById("searchBox");
    if (searchBox) {
        searchBox.value = "";
    }
    const brandSelect = document.querySelector(".build-brand-filter");
    if (brandSelect) {
        brandSelect.value = "";
    }
    const priceSelect = document.querySelector(".build-price-filter");
    if (priceSelect) {
        priceSelect.value = "";
    }

    document.querySelectorAll(".product-card").forEach(card => {
        card.style.display = "flex";
    });

    const emptyState = document.getElementById("buildSearchEmpty");
    if (emptyState) {
        emptyState.hidden = true;
    }
}
