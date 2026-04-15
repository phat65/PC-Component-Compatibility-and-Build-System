package com.example.PCOnlineShop.service.password;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final Map<String, String> resetCodeMap = new HashMap<>();

    @Async
    public void sendResetCode(String identifier) {
        Optional<Account> optionalAccount = findByIdentifier(identifier);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("No account found with the provided information!");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        resetCodeMap.put(identifier, code);

        Account acc = optionalAccount.get();
        String content =
                "Hello " + acc.getFullName() + ",\n\n" +
                        "Your verification code is: " + code + "\n\n" +
                        "This code will expire in 5 minutes.\n\n" +
                        "Best regards,\nPC Online Shop";
        mailService.sendEmail(acc.getEmail(), "Password reset verification code", content);
    }

    public boolean verifyResetCode(String identifier, String code) {
        String stored = resetCodeMap.get(identifier);
        return stored != null && stored.equals(code);
    }

    public void resetPassword(String identifier, String newPassword) {
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
}
