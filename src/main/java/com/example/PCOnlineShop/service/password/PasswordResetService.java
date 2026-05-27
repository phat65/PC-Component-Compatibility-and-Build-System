package com.example.PCOnlineShop.service.password;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final Map<String, ResetCode> resetCodeMap = new ConcurrentHashMap<>();

    public void sendResetCode(String identifier) {
        identifier = normalizeIdentifier(identifier);
        Optional<Account> optionalAccount = findByIdentifier(identifier);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("No account found with the provided information!");
        }

        String code = generateCode();

        Account acc = optionalAccount.get();
        String content =
                "Hello " + acc.getFullName() + ",\n\n" +
                        "Your verification code is: " + code + "\n\n" +
                        "This code will expire in 5 minutes.\n\n" +
                        "Best regards,\nPC Online Shop";
        mailService.sendEmail(acc.getEmail(), "Password reset verification code", content);
        resetCodeMap.put(identifier, new ResetCode(code, expiresAt(), false));
    }

    public boolean verifyResetCode(String identifier, String code) {
        identifier = normalizeIdentifier(identifier);
        if (!StringUtils.hasText(code)) {
            return false;
        }

        ResetCode stored = resetCodeMap.get(identifier);
        if (stored == null || stored.isExpired() || !stored.code().equals(code.trim())) {
            resetCodeMap.remove(identifier);
            return false;
        }

        resetCodeMap.put(identifier, stored.markVerified());
        return true;
    }

    public void resetPassword(String identifier, String newPassword) {
        identifier = normalizeIdentifier(identifier);
        ResetCode resetCode = resetCodeMap.get(identifier);
        if (resetCode == null || resetCode.isExpired() || !resetCode.verified()) {
            resetCodeMap.remove(identifier);
            throw new IllegalArgumentException("Please verify your reset code before changing password!");
        }

        Optional<Account> optionalAccount = findByIdentifier(identifier);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("Account not found!");
        }

        Account acc = optionalAccount.get();
        acc.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(acc);
        resetCodeMap.remove(identifier);
    }

    private Optional<Account> findByIdentifier(String identifier) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(identifier);
        if (optionalAccount.isEmpty()) {
            optionalAccount = accountRepository.findByPhoneNumber(identifier);
        }
        return optionalAccount;
    }

    private String normalizeIdentifier(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            throw new IllegalArgumentException("Email or phone number is required!");
        }
        return identifier.trim();
    }

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private Instant expiresAt() {
        return Instant.now().plus(CODE_TTL);
    }

    private record ResetCode(String code, Instant expiresAt, boolean verified) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        ResetCode markVerified() {
            return new ResetCode(code, expiresAt, true);
        }
    }
}
