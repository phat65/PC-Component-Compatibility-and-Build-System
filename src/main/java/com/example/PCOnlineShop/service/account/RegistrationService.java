package com.example.PCOnlineShop.service.account;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.address.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    public void register(Account account, String addressStr) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new IllegalArgumentException("Email already exists!");
        }
        if (accountRepository.existsByPhoneNumber(account.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists!");
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(RoleName.Customer);
        account.setEnabled(false);
        Account savedAccount = accountRepository.save(account);

        if (addressStr != null && !addressStr.isEmpty()) {
            addressService.saveDefaultAddress(
                    savedAccount,
                    account.getFirstname() + " " + account.getLastname(),
                    account.getPhoneNumber(),
                    addressStr
            );
        }
    }
}
