document.addEventListener("DOMContentLoaded", function() {
    function getCsrfToken() {
        const match = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]+)/);
        return match ? decodeURIComponent(match[1]) : '';
    }

    function withCsrfHeaders(headers) {
        const csrfToken = getCsrfToken();
        if (!csrfToken) {
            return headers;
        }

        return {
            ...headers,
            'X-XSRF-TOKEN': csrfToken
        };
    }

    function escapeHtml(value) {
        return String(value ?? '')
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    const shipHomeRadio = document.getElementById("ship-home");
    const shipStoreRadio = document.getElementById("ship-store");
    const shippingDetailsBlock = document.getElementById("shipping-details-block");
    const shippingMethodInput = document.getElementById("shippingMethodInput");

    const inputFullName = document.getElementById("shippingFullNameInput");
    const inputPhone = document.getElementById("shippingPhoneInput");
    const inputAddress = document.getElementById("shippingAddressInput");

    const displayName = document.getElementById("display-name");
    const displayPhone = document.getElementById("display-phone");
    const displayAddress = document.getElementById("display-address");

    const listModal = document.getElementById("address-list-modal");
    const newModal = document.getElementById("new-address-modal");
    const editModal = document.getElementById("edit-address-modal");
    const addressListContainer = document.getElementById("address-radio-list");

    const nameRegex = /^[\p{L}\s]+$/u;
    const phoneRegex = /^(0[35789])([0-9]{8})$/;

    function updateShippingMethod() {
        if (!shipHomeRadio || !shipStoreRadio || !shippingMethodInput || !shippingDetailsBlock) return;

        shipHomeRadio.closest('.custom-radio')?.classList.toggle('selected', shipHomeRadio.checked);
        shipStoreRadio.closest('.custom-radio')?.classList.toggle('selected', shipStoreRadio.checked);

        if (shipStoreRadio.checked) {
            shippingDetailsBlock.classList.add("hidden");
            shippingMethodInput.value = "Nhận tại cửa hàng";
        } else {
            shippingDetailsBlock.classList.remove("hidden");
            shippingMethodInput.value = "Giao hàng tận nơi";
        }
    }

    function setMainAddress(name, phone, address) {
        if (displayName) displayName.innerText = name;
        if (displayPhone) displayPhone.innerText = phone;
        if (displayAddress) displayAddress.innerText = address;
        if (inputFullName) inputFullName.value = name;
        if (inputPhone) inputPhone.value = phone;
        if (inputAddress) inputAddress.value = address;

        document.getElementById("address-display-block")?.classList.remove("hidden");
        document.getElementById("no-address-block")?.classList.add("hidden");
    }

    function validateAddressFields(name, phone, address, errorDiv) {
        if (!errorDiv) return false;
        errorDiv.innerText = "";

        if (!name || !phone || !address) {
            errorDiv.innerText = "Please fill in all required information.";
            return false;
        }
        if (!nameRegex.test(name)) {
            errorDiv.innerText = "Full name can only contain letters and spaces.";
            return false;
        }
        if (!phoneRegex.test(phone)) {
            errorDiv.innerText = "Phone number is invalid. Use 10 digits starting with 03, 05, 07, 08, or 09.";
            return false;
        }
        return true;
    }

    function addAddressToRadioList(addr) {
        if (!addressListContainer) return;

        const noAddressMsg = document.getElementById("no-address-msg");
        if (noAddressMsg) noAddressMsg.style.display = 'none';

        const oldChecked = document.querySelector('input[name="selectedAddress"]:checked');
        if (oldChecked) oldChecked.checked = false;

        const isDefault = Boolean(addr.default);
        const item = document.createElement('div');
        item.className = 'address-item';
        item.innerHTML = `
            <div>
                <input type="radio" name="selectedAddress"
                       id="addr-${escapeHtml(addr.addressId)}"
                       value="${escapeHtml(addr.addressId)}"
                       data-name="${escapeHtml(addr.fullName)}"
                       data-phone="${escapeHtml(addr.phone)}"
                       data-address="${escapeHtml(addr.address)}"
                       ${isDefault ? 'checked' : ''}>
                <label for="addr-${escapeHtml(addr.addressId)}">
                    <strong class="addr-name">${escapeHtml(addr.fullName)}</strong>
                    <span>-</span>
                    <span class="addr-phone">${escapeHtml(addr.phone)}</span>
                    ${isDefault ? '<span class="address-default-badge">(Default)</span>' : ''}
                    <br>
                    <span class="addr-text text-muted">${escapeHtml(addr.address)}</span>
                </label>
            </div>
            <div class="address-item-actions">
                <button type="button" class="link-button btn-edit-address"
                        data-id="${escapeHtml(addr.addressId)}"
                        data-name="${escapeHtml(addr.fullName)}"
                        data-phone="${escapeHtml(addr.phone)}"
                        data-address="${escapeHtml(addr.address)}">
                    Edit
                </button>
                ${!isDefault ? `
                    <button type="button" class="link-button btn-set-default"
                            data-id="${escapeHtml(addr.addressId)}">
                        Set as Default
                    </button>
                ` : ''}
            </div>
        `;

        addressListContainer.appendChild(item);
    }

    shipHomeRadio?.addEventListener("change", updateShippingMethod);
    shipStoreRadio?.addEventListener("change", updateShippingMethod);
    updateShippingMethod();

    document.getElementById("btn-change-address")?.addEventListener("click", function() {
        if (listModal) listModal.style.display = "flex";
    });

    document.getElementById("btn-cancel-list")?.addEventListener("click", function() {
        if (listModal) listModal.style.display = "none";
    });

    document.getElementById("btn-open-new-address-modal")?.addEventListener("click", function() {
        if (listModal) listModal.style.display = "none";
        if (newModal) newModal.style.display = "flex";
        const errorDiv = document.getElementById("new-address-error");
        if (errorDiv) errorDiv.innerText = "";
    });

    document.getElementById("btn-cancel-new")?.addEventListener("click", function() {
        if (newModal) newModal.style.display = "none";
        if (listModal) listModal.style.display = "flex";
    });

    document.getElementById("btn-confirm-address")?.addEventListener("click", function() {
        const selectedRadio = document.querySelector('input[name="selectedAddress"]:checked');
        if (!selectedRadio) {
            alert("Please select an address.");
            return;
        }

        setMainAddress(
            selectedRadio.getAttribute('data-name'),
            selectedRadio.getAttribute('data-phone'),
            selectedRadio.getAttribute('data-address')
        );

        if (listModal) listModal.style.display = "none";
    });

    document.getElementById("btn-confirm-new-address")?.addEventListener("click", function() {
        const fullName = document.getElementById("new-name")?.value.trim();
        const phone = document.getElementById("new-phone")?.value.trim();
        const address = document.getElementById("new-address")?.value.trim();
        const errorDiv = document.getElementById("new-address-error");

        if (!validateAddressFields(fullName, phone, address, errorDiv)) return;

        const formData = new FormData();
        formData.append('fullName', fullName);
        formData.append('phone', phone);
        formData.append('address', address);

        fetch('/address/add', {
            method: 'POST',
            headers: withCsrfHeaders({}),
            body: formData,
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errData => {
                        throw new Error(errData.error || 'Unknown server error.');
                    });
                }
                return response.json();
            })
            .then(newAddress => {
                addAddressToRadioList(newAddress);
                setMainAddress(newAddress.fullName, newAddress.phone, newAddress.address);

                if (newModal) newModal.style.display = "none";
                if (listModal) listModal.style.display = "flex";
            })
            .catch(err => {
                if (errorDiv) errorDiv.innerText = err.message || "System error. Could not add address.";
            });
    });

    document.getElementById("btn-cancel-edit")?.addEventListener("click", function() {
        if (editModal) editModal.style.display = "none";
        if (listModal) listModal.style.display = "flex";
    });

    addressListContainer?.addEventListener("click", function(event) {
        const target = event.target;

        if (target.classList.contains("btn-edit-address")) {
            document.getElementById("edit-address-id").value = target.getAttribute("data-id");
            document.getElementById("edit-name").value = target.getAttribute("data-name");
            document.getElementById("edit-phone").value = target.getAttribute("data-phone");
            document.getElementById("edit-address").value = target.getAttribute("data-address");

            const errorDiv = document.getElementById("edit-address-error");
            if (errorDiv) errorDiv.innerText = "";

            if (listModal) listModal.style.display = "none";
            if (editModal) editModal.style.display = "flex";
        }

        if (target.classList.contains("btn-set-default")) {
            if (!confirm("Set this address as default?")) return;

            const formData = new FormData();
            formData.append('addressId', target.getAttribute("data-id"));

            fetch('/address/set-default', {
                method: 'POST',
                headers: withCsrfHeaders({}),
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        alert("Error: " + data.error);
                    } else {
                        alert(data.message || "Default address updated.");
                        location.reload();
                    }
                })
                .catch(() => {
                    alert("System error while setting default address.");
                });
        }
    });

    document.getElementById("btn-confirm-edit")?.addEventListener("click", function() {
        const inputEditId = document.getElementById("edit-address-id");
        const inputEditName = document.getElementById("edit-name");
        const inputEditPhone = document.getElementById("edit-phone");
        const inputEditAddress = document.getElementById("edit-address");
        const errorDiv = document.getElementById("edit-address-error");

        if (!inputEditId || !inputEditName || !inputEditPhone || !inputEditAddress) return;

        const id = inputEditId.value;
        const name = inputEditName.value.trim();
        const phone = inputEditPhone.value.trim();
        const address = inputEditAddress.value.trim();

        if (!validateAddressFields(name, phone, address, errorDiv)) return;

        const formData = new FormData();
        formData.append('addressId', id);
        formData.append('fullName', name);
        formData.append('phone', phone);
        formData.append('address', address);

        fetch('/address/update', {
            method: 'POST',
            headers: withCsrfHeaders({}),
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errData => {
                        throw new Error(errData.error || 'Unknown error.');
                    });
                }
                return response.json();
            })
            .then(updatedAddress => {
                const radio = document.getElementById('addr-' + updatedAddress.addressId);
                if (!radio) return;

                const addressItem = radio.closest('.address-item');
                const label = addressItem.querySelector('label');

                radio.setAttribute('data-name', updatedAddress.fullName);
                radio.setAttribute('data-phone', updatedAddress.phone);
                radio.setAttribute('data-address', updatedAddress.address);

                label.querySelector('.addr-name').innerText = updatedAddress.fullName;
                label.querySelector('.addr-phone').innerText = updatedAddress.phone;
                label.querySelector('.addr-text').innerText = updatedAddress.address;

                const editButton = addressItem.querySelector('.btn-edit-address');
                if (editButton) {
                    editButton.setAttribute('data-name', updatedAddress.fullName);
                    editButton.setAttribute('data-phone', updatedAddress.phone);
                    editButton.setAttribute('data-address', updatedAddress.address);
                }

                if (radio.checked) {
                    setMainAddress(updatedAddress.fullName, updatedAddress.phone, updatedAddress.address);
                }

                if (editModal) editModal.style.display = "none";
                if (listModal) listModal.style.display = "flex";
            })
            .catch(err => {
                if (errorDiv) errorDiv.innerText = err.message;
            });
    });
});
