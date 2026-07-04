package com.example.PCOnlineShop.service.auth;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.PCOnlineShop.dto.account.RegisterRequest;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.account.Address;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.account.RegistrationService;
import com.example.PCOnlineShop.service.address.AddressService;
import com.example.PCOnlineShop.service.password.PasswordResetService;
import com.example.PCOnlineShop.service.verification.VerificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegistrationService registrationService;
    private final AccountService accountService;
    private final AddressService addressService;
    private final VerificationService verificationService;
    private final PasswordResetService passwordResetService;

    public void register(RegisterRequest request) {
        validatePasswordConfirmation(request.getPassword(), request.getConfirmPassword(), "Confirm password does not match.");
        registrationService.register(request);
    }

    public Account getByPhoneNumber(String phoneNumber) {
        return accountService.getByPhoneNumber(phoneNumber);
    }

    public List<Address> getAddressesForAccount(Account account) {
        return addressService.getAddressesForAccount(account);
    }

    public Account updateCustomerProfile(String phoneNumber, Account updatedAccount) {
        return accountService.updateCustomerProfile(phoneNumber, updatedAccount);
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

    public void resetPassword(String identifier, String newPassword, String confirmPassword) {
        validatePasswordConfirmation(newPassword, confirmPassword, "Confirm password does not match.");
        passwordResetService.resetPassword(identifier, newPassword);
    }

    public boolean changePassword(String phoneNumber, String currentPassword, String newPassword) {
        return accountService.changePassword(phoneNumber, currentPassword, newPassword);
    }

    public void changePassword(String phoneNumber, String currentPassword, String newPassword, String confirmPassword) {
        validatePasswordConfirmation(newPassword, confirmPassword, "New password does not match.");

        if (!accountService.changePassword(phoneNumber, currentPassword, newPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
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

    public long getVerifyResendCooldownSeconds() {
        return verificationService.getResendCooldownSeconds();
    }

    public long getVerifyResendRemainingSeconds(String email) {
        return verificationService.getResendRemainingSeconds(email);
    }

    public void verifyAccount(String email, String code) {
        verificationService.verifyAccount(email, code);
    }

    private void validatePasswordConfirmation(String password, String confirmPassword, String errorMessage) {
        if (!StringUtils.hasText(password) || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
