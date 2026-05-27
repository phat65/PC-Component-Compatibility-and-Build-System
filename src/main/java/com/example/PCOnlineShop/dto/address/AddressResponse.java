package com.example.PCOnlineShop.dto.address;

import com.example.PCOnlineShop.model.account.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddressResponse {

    private final int addressId;
    private final String fullName;
    private final String phone;
    private final String address;
    private final boolean defaultAddress;

    public AddressResponse(Address address) {
        this.addressId = address.getAddressId();
        this.fullName = address.getFullName();
        this.phone = address.getPhone();
        this.address = address.getAddress();
        this.defaultAddress = address.isDefault();
    }

    public int getAddressId() {
        return addressId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @JsonProperty("default")
    public boolean isDefault() {
        return defaultAddress;
    }
}
