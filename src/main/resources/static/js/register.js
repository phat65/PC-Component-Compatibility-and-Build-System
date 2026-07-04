function validatePassword() {
    const password = document.getElementById("password")?.value || "";
    const confirm = document.getElementById("confirmPassword")?.value || "";
    const errorText = document.getElementById("errorText");

    if (!errorText) {
        return password === confirm;
    }

    if (password.length < 6) {
        errorText.textContent = "Password must be at least 6 characters.";
        errorText.classList.remove("auth-alert--hidden");
        return false;
    }

    if (password !== confirm) {
        errorText.textContent = "Confirm password does not match.";
        errorText.classList.remove("auth-alert--hidden");
        return false;
    }

    errorText.textContent = "";
    errorText.classList.add("auth-alert--hidden");
    return true;
}

document.addEventListener("DOMContentLoaded", async function () {
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");
    const wardSelect = document.getElementById("ward");
    const addressInput = document.getElementById("address");
    const manualAddressInput = document.getElementById("manualAddress");
    const addressHelp = document.getElementById("addressHelp");
    const addressGroup = document.querySelector(".auth-address");

    if (!provinceSelect || !districtSelect || !wardSelect || !addressInput || !manualAddressInput) {
        return;
    }

    manualAddressInput.addEventListener("input", updateAddress);

    let provinces = [];
    try {
        provinces = await fetchJson("https://provinces.open-api.vn/api/p/");
        provinces.forEach(province => provinceSelect.add(new Option(province.name, province.code)));
    } catch (error) {
        enableManualAddressOnly();
        return;
    }

    const oldAddress = addressInput.value;
    let provinceName = "";
    let districtName = "";
    let wardName = "";
    let streetAddress = "";

    if (oldAddress) {
        const parts = oldAddress.split(",").map(part => part.trim()).filter(Boolean);
        if (parts.length >= 4) {
            streetAddress = parts.slice(0, parts.length - 4).join(", ");
            wardName = parts[parts.length - 4] || "";
            districtName = parts[parts.length - 3] || "";
            provinceName = parts[parts.length - 2] || "";
        } else {
            manualAddressInput.value = oldAddress;
        }
    }

    if (streetAddress) {
        manualAddressInput.value = streetAddress;
    }

    if (provinceName) {
        await prefillAddress(provinceName, districtName, wardName);
    }

    provinceSelect.addEventListener("change", handleProvinceChange);
    districtSelect.addEventListener("change", handleDistrictChange);
    wardSelect.addEventListener("change", updateAddress);
    updateAddress();

    async function prefillAddress(provinceNameValue, districtNameValue, wardNameValue) {
        const foundProvince = provinces.find(province => province.name === provinceNameValue);
        if (!foundProvince) {
            return;
        }

        provinceSelect.value = foundProvince.code;
        const provinceDetail = await fetchJson(`https://provinces.open-api.vn/api/p/${foundProvince.code}?depth=2`);
        provinceDetail.districts.forEach(district => districtSelect.add(new Option(district.name, district.code)));

        const foundDistrict = provinceDetail.districts.find(district => district.name === districtNameValue);
        if (!foundDistrict) {
            updateAddress();
            return;
        }

        districtSelect.value = foundDistrict.code;
        const districtDetail = await fetchJson(`https://provinces.open-api.vn/api/d/${foundDistrict.code}?depth=2`);
        districtDetail.wards.forEach(ward => wardSelect.add(new Option(ward.name, ward.name)));
        wardSelect.value = wardNameValue;
        updateAddress();
    }

    async function handleProvinceChange() {
        districtSelect.innerHTML = "<option value=''>District</option>";
        wardSelect.innerHTML = "<option value=''>Ward</option>";

        if (!provinceSelect.value) {
            updateAddress();
            return;
        }

        try {
            const data = await fetchJson(`https://provinces.open-api.vn/api/p/${provinceSelect.value}?depth=2`);
            data.districts.forEach(district => districtSelect.add(new Option(district.name, district.code)));
        } catch (error) {
            enableManualAddressOnly();
            return;
        }

        updateAddress();
    }

    async function handleDistrictChange() {
        wardSelect.innerHTML = "<option value=''>Ward</option>";

        if (!districtSelect.value) {
            updateAddress();
            return;
        }

        try {
            const data = await fetchJson(`https://provinces.open-api.vn/api/d/${districtSelect.value}?depth=2`);
            data.wards.forEach(ward => wardSelect.add(new Option(ward.name, ward.name)));
        } catch (error) {
            enableManualAddressOnly();
            return;
        }

        updateAddress();
    }

    function updateAddress() {
        const manualAddress = manualAddressInput.value.trim();
        const ward = selectedText(wardSelect);
        const district = selectedText(districtSelect);
        const province = selectedText(provinceSelect);

        addressInput.value = [manualAddress, ward, district, province, "Viet Nam"]
            .filter(Boolean)
            .join(", ");
    }

    function selectedText(select) {
        if (!select.value) {
            return "";
        }
        return select.options[select.selectedIndex]?.text || "";
    }

    function enableManualAddressOnly() {
        provinceSelect.disabled = true;
        districtSelect.disabled = true;
        wardSelect.disabled = true;
        addressGroup?.classList.add("auth-address--disabled");
        manualAddressInput.required = true;
        manualAddressInput.focus();

        if (addressHelp) {
            addressHelp.textContent = "Address lookup is unavailable. Type your full address manually.";
        }
    }
});

async function fetchJson(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
    }
    return response.json();
}
