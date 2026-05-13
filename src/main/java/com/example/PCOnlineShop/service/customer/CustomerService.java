package com.example.PCOnlineShop.service.customer;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.address.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final AccountRepository accountRepository;
    private final AddressService addressService;

    public List<Account> getAllCustomers(String statusFilter) {
        if ("active".equalsIgnoreCase(statusFilter)) {
            return accountRepository.findByRoleAndEnabledWithAddresses(RoleName.Customer, true);
        }

        if ("inactive".equalsIgnoreCase(statusFilter)) {
            return accountRepository.findByRoleAndEnabledWithAddresses(RoleName.Customer, false);
        }

        return accountRepository.findAllByRoleWithAddresses(RoleName.Customer);
    }

    public Account getById(int id) {
        return accountRepository.findById(id).orElse(null);
    }

    public void saveDefaultAddress(Account account, String addressStr) {
        if (account == null) {
            return;
        }

        addressService.saveDefaultAddress(account, account.getFullName(), account.getPhoneNumber(), addressStr);
    }
}
