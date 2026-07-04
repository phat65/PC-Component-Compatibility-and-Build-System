package com.example.PCOnlineShop.service.account;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.dto.account.RegisterRequest;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.address.AddressService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    @Transactional
    public void register(RegisterRequest request) {
        String email = normalize(request.getEmail());
        String phoneNumber = normalize(request.getPhoneNumber());

        Account account = resolveAccountForRegistration(email, phoneNumber);
        boolean existingPendingAccount = account.getAccountId() != 0;

        account.setFirstname(normalize(request.getFirstname()));
        account.setLastname(normalize(request.getLastname()));
        account.setGender(request.getGender());
        account.setEmail(email);
        account.setPhoneNumber(phoneNumber);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(RoleName.Customer);
        account.setEnabled(false);

        Account savedAccount = accountRepository.save(account);
        String address = normalize(request.getAddress());

        if (StringUtils.hasText(address)) {
            saveOrUpdateDefaultAddress(
                    existingPendingAccount,
                    savedAccount,
                    savedAccount.getFullName(),
                    savedAccount.getPhoneNumber(),
                    address
            );
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private Account resolveAccountForRegistration(String email, String phoneNumber) {
        Optional<Account> accountByEmail = accountRepository.findByEmail(email);
        Optional<Account> accountByPhone = accountRepository.findByPhoneNumber(phoneNumber);

        if (accountByEmail.isPresent() && accountByPhone.isPresent()) {
            Account emailAccount = accountByEmail.get();
            Account phoneAccount = accountByPhone.get();

            if (emailAccount.getAccountId() != phoneAccount.getAccountId()) {
                if (Boolean.TRUE.equals(emailAccount.getEnabled())) {
                    throw new IllegalArgumentException("Email already exists.");
                }
                if (Boolean.TRUE.equals(phoneAccount.getEnabled())) {
                    throw new IllegalArgumentException("Phone number already exists.");
                }
                throw new IllegalArgumentException("Email and phone number belong to different pending accounts. Please verify the existing account or use another email and phone number.");
            }

            return reusePendingAccountOrThrow(emailAccount);
        }

        if (accountByEmail.isPresent()) {
            Account existingAccount = accountByEmail.get();
            if (Boolean.TRUE.equals(existingAccount.getEnabled())) {
                throw new IllegalArgumentException("Email already exists.");
            }
            if (existingAccount.getRole() != RoleName.Customer) {
                throw new IllegalArgumentException("Email already exists.");
            }
            return existingAccount;
        }

        if (accountByPhone.isPresent()) {
            Account existingAccount = accountByPhone.get();
            if (Boolean.TRUE.equals(existingAccount.getEnabled())) {
                throw new IllegalArgumentException("Phone number already exists.");
            }
            if (existingAccount.getRole() != RoleName.Customer) {
                throw new IllegalArgumentException("Phone number already exists.");
            }
            return existingAccount;
        }

        return new Account();
    }

    private Account reusePendingAccountOrThrow(Account account) {
        if (Boolean.TRUE.equals(account.getEnabled())) {
            throw new IllegalArgumentException("Account already exists. Please login instead.");
        }
        if (account.getRole() != RoleName.Customer) {
            throw new IllegalArgumentException("Account already exists.");
        }
        return account;
    }

    private void saveOrUpdateDefaultAddress(boolean existingPendingAccount,
                                            Account account,
                                            String fullName,
                                            String phone,
                                            String address) {
        if (existingPendingAccount) {
            addressService.updateDefaultAddress(account, fullName, phone, address);
            return;
        }

        addressService.saveDefaultAddress(account, fullName, phone, address);
    }
}
