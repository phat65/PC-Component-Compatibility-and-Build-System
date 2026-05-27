package com.example.PCOnlineShop.service.account;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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

    @Transactional
    public Account updateCustomerProfile(String currentPhoneNumber, Account updatedAccount) {
        Account existing = accountRepository.findByPhoneNumber(currentPhoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản!"));

        String firstname = normalize(updatedAccount.getFirstname());
        String lastname = normalize(updatedAccount.getLastname());

        if (!StringUtils.hasText(firstname) || !StringUtils.hasText(lastname)) {
            throw new IllegalArgumentException("Họ và tên không được để trống!");
        }

        existing.setFirstname(firstname);
        existing.setLastname(lastname);
        existing.setGender(updatedAccount.getGender());

        return accountRepository.save(existing);
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

    public boolean changePassword(String phoneNumber, String currentPassword, String newPassword) {
        Optional<Account> optionalAccount = accountRepository.findByPhoneNumber(phoneNumber);
        if (optionalAccount.isEmpty()) {
            return false;
        }

        Account account = optionalAccount.get();
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            return false;
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        return true;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
