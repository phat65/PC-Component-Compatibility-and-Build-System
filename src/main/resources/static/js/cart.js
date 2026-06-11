document.addEventListener("DOMContentLoaded", function () {
    const cartContainer = document.querySelector(".cart-container");
    if (!cartContainer) return;

    const grandTotalElement = document.getElementById("grand-total-value");
    const selectedItemsCountElement = document.getElementById("selected-items-count");
    const selectedItemsTotalElement = document.getElementById("selected-items-total");
    const checkoutLink = document.getElementById("checkout-link");
    const selectAllCheckbox = document.getElementById("select-all-checkbox");

    function getCsrfToken() {
        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        const metaToken = csrfMeta ? csrfMeta.getAttribute("content") : "";
        if (metaToken) {
            return metaToken;
        }

        const cookieMatch = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]+)/);
        if (cookieMatch) {
            return decodeURIComponent(cookieMatch[1]);
        }

        return "";
    }

    function getCsrfHeader() {
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
        return csrfHeaderMeta ? csrfHeaderMeta.getAttribute("content") : "X-XSRF-TOKEN";
    }

    function getCsrfParameter() {
        const csrfParameterMeta = document.querySelector('meta[name="_csrf_parameter"]');
        return csrfParameterMeta ? csrfParameterMeta.getAttribute("content") : "_csrf";
    }

    function formatMoney(value) {
        return new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
            maximumFractionDigits: 0
        }).format(value || 0);
    }

    function postAjax(url, body, onSuccess, onError) {
        const headers = {
            "Accept": "application/json"
        };
        const csrfToken = getCsrfToken();
        if (csrfToken) {
            headers[getCsrfHeader()] = csrfToken;
            if (body instanceof FormData && !body.has(getCsrfParameter())) {
                body.append(getCsrfParameter(), csrfToken);
            }
        }

        fetch(url, {
            method: "POST",
            headers: headers,
            body: body
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errData => {
                        throw new Error(errData.error || "Unknown server error");
                    });
                }
                return response.status === 204 ? null : response.json();
            })
            .then(data => {
                if (onSuccess) onSuccess(data);
            })
            .catch(error => {
                console.error("AJAX Error:", error);
                if (onError) onError(error.message);
                showMessage(error.message || "An error occurred.", "danger");
            });
    }

    function updateHeaderCartCount(cartItemCount) {
        if (cartItemCount === undefined || cartItemCount === null) {
            return;
        }

        document.querySelectorAll(".cart-count").forEach(element => {
            element.textContent = cartItemCount;
        });
    }

    function updateTotals(newGrandTotal) {
        const formattedTotal = formatMoney(newGrandTotal);

        if (grandTotalElement) {
            grandTotalElement.textContent = formattedTotal;
        }
        if (selectedItemsTotalElement) {
            selectedItemsTotalElement.textContent = formattedTotal;
        }

        const selectedCheckboxes = document.querySelectorAll(".cart-item-checkbox:checked");
        if (selectedItemsCountElement) {
            selectedItemsCountElement.textContent = selectedCheckboxes.length;
        }

        if (checkoutLink) {
            if (newGrandTotal > 0 && selectedCheckboxes.length > 0) {
                checkoutLink.classList.remove("disabled");
            } else {
                checkoutLink.classList.add("disabled");
            }
        }

        const allItemCheckboxes = document.querySelectorAll(".cart-item-checkbox");
        if (selectAllCheckbox) {
            selectAllCheckbox.checked = allItemCheckboxes.length > 0 && selectedCheckboxes.length === allItemCheckboxes.length;
        }
    }

    function showMessage(message, type = "success") {
        const messagesContainer = document.getElementById("cart-messages");
        if (!messagesContainer) return;

        messagesContainer.innerHTML = `
            <div class="alert alert-${type}" role="alert">
                ${message}
            </div>`;
    }

    function updateQuantity(cartItemId, quantity) {
        const formData = new FormData();
        formData.append("quantity", quantity);

        postAjax(`/cart/update/${cartItemId}`, formData,
            (data) => {
                const cartItemElement = document.querySelector(`.cart-item[data-cart-item-id="${cartItemId}"]`);
                if (cartItemElement) {
                    cartItemElement.querySelector(".quantity-display").value = quantity;

                    const price = parseFloat(cartItemElement.getAttribute("data-price"));
                    cartItemElement.querySelector(".item-subtotal").textContent = formatMoney(price * quantity);

                    cartItemElement.querySelector(".decrease-qty-btn").disabled = quantity <= 1;
                    const inventory = parseInt(cartItemElement.getAttribute("data-inventory"), 10);
                    cartItemElement.querySelector(".increase-qty-btn").disabled = quantity >= inventory;
                }

                if (data && data.newGrandTotal !== undefined) {
                    updateTotals(data.newGrandTotal);
                }
                updateHeaderCartCount(data && data.cartItemCount);
                showMessage("Quantity updated.", "success");
            },
            (errorMsg) => showMessage(errorMsg, "danger")
        );
    }

    function removeItem(cartItemId) {
        postAjax(`/cart/remove/${cartItemId}`, new FormData(),
            (data) => {
                const cartItemElement = document.querySelector(`.cart-item[data-cart-item-id="${cartItemId}"]`);
                if (cartItemElement) {
                    cartItemElement.remove();
                }

                if (data && data.newGrandTotal !== undefined) {
                    updateTotals(data.newGrandTotal);
                }
                updateHeaderCartCount(data && data.cartItemCount);

                if (document.querySelectorAll(".cart-item").length === 0) {
                    location.reload();
                    return;
                }

                showMessage("Item removed.", "success");
            },
            (errorMsg) => showMessage(errorMsg, "danger")
        );
    }

    function clearSelectedItems() {
        const formData = new FormData();
        document.querySelectorAll(".cart-item-checkbox:checked").forEach(checkbox => {
            formData.append("cartItemIds", checkbox.getAttribute("data-cart-item-id"));
        });

        postAjax("/cart/clear-selected", formData,
            (data) => {
                document.querySelectorAll(".cart-item-checkbox:checked").forEach(checkbox => {
                    const cartItemElement = checkbox.closest(".cart-item");
                    if (cartItemElement) {
                        cartItemElement.remove();
                    }
                });

                if (data && data.newGrandTotal !== undefined) {
                    updateTotals(data.newGrandTotal);
                }
                updateHeaderCartCount(data && data.cartItemCount);

                if (document.querySelectorAll(".cart-item").length === 0) {
                    location.reload();
                    return;
                }

                showMessage("Selected item(s) removed.", "success");
            },
            (errorMsg) => showMessage(errorMsg, "danger")
        );
    }

    function toggleSelectItem(cartItemId, isSelected) {
        const url = isSelected ? `/cart/select/${cartItemId}` : `/cart/deselect/${cartItemId}`;

        postAjax(url, new FormData(),
            (data) => {
                if (data && data.newGrandTotal !== undefined) {
                    updateTotals(data.newGrandTotal);
                }
                updateHeaderCartCount(data && data.cartItemCount);
            },
            (errorMsg) => {
                const checkbox = document.querySelector(`.cart-item-checkbox[data-cart-item-id="${cartItemId}"]`);
                if (checkbox) {
                    checkbox.checked = !isSelected;
                }
                showMessage(errorMsg, "danger");
            }
        );
    }

    cartContainer.addEventListener("click", function (event) {
        const actionButton = event.target.closest(".increase-qty-btn, .decrease-qty-btn, .remove-item-btn");
        if (!actionButton) return;

        const cartItemElement = actionButton.closest(".cart-item");
        if (!cartItemElement) return;

        const cartItemId = cartItemElement.getAttribute("data-cart-item-id");
        if (!cartItemId) return;

        const quantityInput = cartItemElement.querySelector(".quantity-display");
        const currentQuantity = parseInt(quantityInput.value, 10);

        if (actionButton.classList.contains("increase-qty-btn")) {
            const inventory = parseInt(cartItemElement.getAttribute("data-inventory"), 10);
            if (currentQuantity < inventory) {
                updateQuantity(cartItemId, currentQuantity + 1);
            }
            return;
        }

        if (actionButton.classList.contains("decrease-qty-btn")) {
            if (currentQuantity > 1) {
                updateQuantity(cartItemId, currentQuantity - 1);
            }
            return;
        }

        if (confirm("Are you sure you want to remove this item?")) {
            removeItem(cartItemId);
        }
    });

    cartContainer.addEventListener("change", function (event) {
        const target = event.target;
        if (!target.classList.contains("cart-item-checkbox")) return;

        toggleSelectItem(target.getAttribute("data-cart-item-id"), target.checked);
    });

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", function (event) {
            const isSelected = event.target.checked;
            document.querySelectorAll(".cart-item-checkbox").forEach(checkbox => {
                if (checkbox.checked !== isSelected) {
                    checkbox.checked = isSelected;
                    toggleSelectItem(checkbox.getAttribute("data-cart-item-id"), isSelected);
                }
            });
        });
    }

    const deleteSelectedCartBtn = document.querySelector(".delete-selected-cart-btn");
    if (deleteSelectedCartBtn) {
        deleteSelectedCartBtn.addEventListener("click", function () {
            const selectedCheckboxes = document.querySelectorAll(".cart-item-checkbox:checked");
            if (selectedCheckboxes.length === 0) {
                showMessage("Please select at least one item to delete.", "warning");
                return;
            }

            if (confirm(`Remove ${selectedCheckboxes.length} selected item(s)?`)) {
                clearSelectedItems();
            }
        });
    }

    const initialTotal = parseFloat((grandTotalElement && grandTotalElement.getAttribute("data-raw-total")) || 0);
    updateTotals(initialTotal);
});
