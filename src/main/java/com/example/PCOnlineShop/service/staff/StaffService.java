package com.example.PCOnlineShop.service.staff;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.address.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final AccountRepository accountRepository;
    private final AddressService addressService;

    public List<Account> getAllStaff(String statusFilter) {
        if ("active".equalsIgnoreCase(statusFilter)) {
            return accountRepository.findByRoleAndEnabledWithAddresses(RoleName.Staff, true);
        }

        if ("inactive".equalsIgnoreCase(statusFilter)) {
            return accountRepository.findByRoleAndEnabledWithAddresses(RoleName.Staff, false);
        }

        return accountRepository.findAllByRoleWithAddresses(RoleName.Staff);
    }

    public Account getById(int id) {
        return accountRepository.findById(id).orElse(null);
    }

    public void deactivateStaff(int id) {
        accountRepository.findById(id).ifPresent(acc -> {
            acc.setEnabled(!acc.getEnabled());
            accountRepository.save(acc);
        });
    }

    public void saveDefaultAddress(Account account, String addressStr) {
        if (account == null) {
            return;
        }

        addressService.saveDefaultAddress(account, account.getFullName(), account.getPhoneNumber(), addressStr);
    }

    public void updateDefaultAddress(Account account, String addressStr) {
        if (account == null) {
            return;
        }

        addressService.updateDefaultAddress(account, account.getFullName(), account.getPhoneNumber(), addressStr);
    }
}
