package com.example.PCOnlineShop.service.auth;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.account.RegistrationService;
import com.example.PCOnlineShop.service.password.PasswordResetService;
import com.example.PCOnlineShop.service.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegistrationService registrationService;
    private final AccountService accountService;
    private final VerificationService verificationService;
    private final PasswordResetService passwordResetService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(Account account, String addressStr) {
        registrationService.register(account, addressStr);
    }

    public Account getByPhoneNumber(String phoneNumber) {
        return accountService.getByPhoneNumber(phoneNumber);
    }

    public void sendResetCode(String identifier) {
        passwordResetService.sendResetCode(identifier);
    }

    public boolean verifyResetCode(String identifier, String code) {
        return passwordResetService.verifyResetCode(identifier, code);
    }

    public void resetPassword(String identifier, String newPassword) {
        passwordResetService.resetPassword(identifier, newPassword);
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

    public Account saveStaff(Account account) {
        return accountService.saveStaff(account);
    }

    public Account saveCustomer(Account account) {
        return accountService.saveCustomer(account);
    }

    public void updatePassword(String email, String newPassword) {
        accountService.updatePassword(email, newPassword);
    }

    public void sendVerifyCode(String email) {
        verificationService.sendVerifyCode(email);
    }

    public void verifyAccount(String email, String code) {
        verificationService.verifyAccount(email, code);
    }
}
