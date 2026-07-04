package com.example.PCOnlineShop.service.verification;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.mail.MailService;
import lombok.RequiredArgsConstructor;
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
public class VerificationService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(15);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final MailService mailService;

    private final Map<String, VerificationCode> verificationCodeMap = new ConcurrentHashMap<>();

    public void sendVerifyCode(String email) {
        email = normalizeEmail(email);
        enforceResendCooldown(email);

        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("No account found with the provided email!");
        }

        String code = generateCode();

        Account acc = optionalAccount.get();
        String content =
                "Hello " + acc.getFullName() + ",\n\n" +
                        "Thank you for registering an account at PC Online Shop.\n\n" +
                        "Your account verification code is: " + code + "\n\n" +
                        "Please enter this code within 5 minutes to activate your account.\n\n" +
                        "Best regards,\nPC Online Shop";

        mailService.sendEmail(acc.getEmail(), "PC Online Shop account verification", content);
        verificationCodeMap.put(email, new VerificationCode(code, expiresAt(), Instant.now()));
    }

    public void verifyAccount(String email, String code) {
        if (!StringUtils.hasText(code)) {
            throw new IllegalArgumentException("The verification code is invalid or has expired!");
        }

        email = normalizeEmail(email);
        VerificationCode storedCode = verificationCodeMap.get(email);
        if (storedCode == null || storedCode.isExpired() || !storedCode.code().equals(code.trim())) {
            verificationCodeMap.remove(email);
            throw new IllegalArgumentException("The verification code is invalid or has expired!");
        }

        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("Account not found!");
        }

        Account acc = optionalAccount.get();
        acc.setEnabled(true);
        accountRepository.save(acc);
        verificationCodeMap.remove(email);
    }

    public long getResendCooldownSeconds() {
        return RESEND_COOLDOWN.toSeconds();
    }

    public long getResendRemainingSeconds(String email) {
        email = normalizeEmail(email);
        VerificationCode existingCode = verificationCodeMap.get(email);
        if (existingCode == null || existingCode.isExpired()) {
            return 0;
        }

        Instant nextAllowedAt = existingCode.sentAt().plus(RESEND_COOLDOWN);
        Instant now = Instant.now();
        if (!now.isBefore(nextAllowedAt)) {
            return 0;
        }

        return Duration.between(now, nextAllowedAt).toSeconds() + 1;
    }

    private String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required!");
        }
        return email.trim();
    }

    private void enforceResendCooldown(String email) {
        VerificationCode existingCode = verificationCodeMap.get(email);
        if (existingCode == null || existingCode.isExpired()) {
            return;
        }

        long remainingSeconds = getResendRemainingSeconds(email);
        if (remainingSeconds > 0) {
            throw new IllegalArgumentException("Please wait " + remainingSeconds + " seconds before requesting another code.");
        }
    }

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private Instant expiresAt() {
        return Instant.now().plus(CODE_TTL);
    }

    private record VerificationCode(String code, Instant expiresAt, Instant sentAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
