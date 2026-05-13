package com.example.PCOnlineShop.service.auth;

import org.springframework.stereotype.Service;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.account.RegistrationService;
import com.example.PCOnlineShop.service.password.PasswordResetService;
import com.example.PCOnlineShop.service.verification.VerificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegistrationService registrationService;
    private final AccountService accountService;
    private final VerificationService verificationService;
    private final PasswordResetService passwordResetService;

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
        return accountService.changePassword(phoneNumber, currentPassword, newPassword);
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
