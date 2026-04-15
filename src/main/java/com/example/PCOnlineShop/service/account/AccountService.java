package com.example.PCOnlineShop.service.account;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account getByPhoneNumber(String phoneNumber) {
        return accountRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    public Account saveStaff(Account account) {
        return saveAccount(account, RoleName.Staff);
    }

    public Account saveCustomer(Account account) {
        return saveAccount(account, RoleName.Customer);
    }

    public void updatePassword(String email, String newPassword) {
        accountRepository.findByEmail(email).ifPresent(acc -> {
            acc.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.save(acc);
        });
    }

    private Account saveAccount(Account account, RoleName roleName) {
        accountRepository.findByEmail(account.getEmail()).ifPresent(existing -> {
            if (account.getAccountId() == 0 || existing.getAccountId() != account.getAccountId()) {
                throw new IllegalArgumentException("Email already exists!");
            }
        });

        accountRepository.findByPhoneNumber(account.getPhoneNumber()).ifPresent(existing -> {
            if (account.getAccountId() == 0 || existing.getAccountId() != account.getAccountId()) {
                throw new IllegalArgumentException("Phone number already exists!");
            }
        });

        Account existing = (account.getAccountId() == 0)
                ? null
                : accountRepository.findById(account.getAccountId()).orElse(null);

        if (existing != null) {
            if (!existing.getPassword().equals(account.getPassword())) {
                account.setPassword(passwordEncoder.encode(account.getPassword()));
            } else {
                account.setPassword(existing.getPassword());
            }
            account.setEnabled(existing.getEnabled());
        } else {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setEnabled(true);
        }

        account.setRole(roleName);
        return accountRepository.save(account);
    }
}
