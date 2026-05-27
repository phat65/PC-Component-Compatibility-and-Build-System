package com.example.PCOnlineShop.service.address;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.account.Address;
import com.example.PCOnlineShop.repository.account.AddressRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAddressesForAccount(Account account) {
        if (account == null) {
            return List.of();
        }
        return addressRepository.findByAccountOrderByIsDefaultDescAddressIdAsc(account);
    }

    public Optional<Address> getDefaultAddress(Account account) {
        if (account == null) {
            return Optional.empty();
        }
        return addressRepository.findDefaultByAccount(account);
    }
    
    @Transactional
    public void saveDefaultAddress(Account account, String fullName, String phone, String address) {
        fullName = normalize(fullName);
        phone = normalize(phone);
        address = normalize(address);

        if (account == null || address == null || address.isEmpty()) {
            return;
        }

        addressRepository.clearDefaultByAccount(account);

        Address addr = new Address();
        addr.setAccount(account);
        addr.setFullName(fullName);
        addr.setPhone(phone);
        addr.setAddress(address);
        addr.setDefault(true);
        addressRepository.save(addr);
    }

    @Transactional
    public void updateDefaultAddress(Account account, String fullName, String phone, String address) {
        fullName = normalize(fullName);
        phone = normalize(phone);
        address = normalize(address);

        if (account == null || address == null || address.isEmpty()) {
            return;
        }

        Address defaultAddress = addressRepository.findDefaultByAccount(account).orElse(new Address());

        addressRepository.clearDefaultByAccount(account);

        defaultAddress.setAccount(account);
        defaultAddress.setFullName(fullName);
        defaultAddress.setPhone(phone);
        defaultAddress.setAddress(address);
        defaultAddress.setDefault(true);

        addressRepository.save(defaultAddress);
    }

    @Transactional
    public Address addNewAddress(Account account, String fullName, String phone, String address)
            throws IllegalArgumentException {

        fullName = normalize(fullName);
        phone = normalize(phone);
        address = normalize(address);

        if (addressRepository.existsByAccountAndPhone(account, phone)) {
            throw new IllegalArgumentException("This phone number is already being used for a different address.");
        }

        Address newAddress = new Address();
        newAddress.setAccount(account);
        newAddress.setFullName(fullName);
        newAddress.setPhone(phone);
        newAddress.setAddress(address);

        List<Address> existing = addressRepository.findByAccount(account);
        if (existing == null || existing.isEmpty()) {
            newAddress.setDefault(true);
        }

        return addressRepository.save(newAddress);
    }

    @Transactional
    public Address updateAddress(Account account, int addressId, String fullName, String phone, String address)
            throws IllegalArgumentException {

        fullName = normalize(fullName);
        phone = normalize(phone);
        address = normalize(address);

        Address existingAddress = addressRepository.findByAccountAndAddressId(account, addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address with ID not found: " + addressId));

        if (addressRepository.existsByAccountAndPhoneAndAddressIdNot(account, phone, addressId)) {
            throw new IllegalArgumentException("This phone number is already being used for a different address.");
        }

        existingAddress.setFullName(fullName);
        existingAddress.setPhone(phone);
        existingAddress.setAddress(address);

        return addressRepository.save(existingAddress);
    }

    @Transactional
    public void setDefaultAddress(Account account, int addressId) {
        Address newDefault = addressRepository.findByAccountAndAddressId(account, addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address with ID not found: " + addressId));

        addressRepository.clearDefaultByAccount(account);

        newDefault.setDefault(true);
        addressRepository.save(newDefault);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
