package com.example.PCOnlineShop.service.address;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.account.Address;
import com.example.PCOnlineShop.repository.account.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<Address> getAddressesForAccount(Account account) {
        return addressRepository.findByAccount(account);
    }

    public Optional<Address> getDefaultAddress(Account account) {
        return addressRepository.findDefaultByAccount(account);
    }

    public void saveDefaultAddress(Account account, String fullName, String phone, String address) {
        if (account == null || address == null || address.isEmpty()) {
            return;
        }

        addressRepository.findByAccount(account).forEach(a -> a.setDefault(false));

        Address addr = new Address();
        addr.setAccount(account);
        addr.setFullName(fullName);
        addr.setPhone(phone);
        addr.setAddress(address);
        addr.setDefault(true);
        addressRepository.save(addr);
    }

    public Address addNewAddress(Account account, String fullName, String phone, String address)
            throws IllegalArgumentException {

        if (addressRepository.existsByAccountAndPhone(account, phone)) {
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng cho một địa chỉ khác.");
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

        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ với ID: " + addressId));

        if (existingAddress.getAccount().getAccountId() != account.getAccountId()) {
            throw new SecurityException("Bạn không có quyền sửa địa chỉ này.");
        }

        if (addressRepository.existsByAccountAndPhoneAndAddressIdNot(account, phone, addressId)) {
            throw new IllegalArgumentException("Số điện thoại này đã được sử dụng cho một địa chỉ khác.");
        }

        existingAddress.setFullName(fullName);
        existingAddress.setPhone(phone);
        existingAddress.setAddress(address);

        return addressRepository.save(existingAddress);
    }

    @Transactional
    public void setDefaultAddress(Account account, int addressId) {
        Address newDefault = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ với ID: " + addressId));

        if (newDefault.getAccount().getAccountId() != account.getAccountId()) {
            throw new SecurityException("Bạn không có quyền thay đổi địa chỉ này.");
        }

        addressRepository.clearDefaultByAccount(account);

        newDefault.setDefault(true);
        addressRepository.save(newDefault);
    }
}
