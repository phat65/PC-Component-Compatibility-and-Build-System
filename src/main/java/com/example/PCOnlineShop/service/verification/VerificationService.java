package com.example.PCOnlineShop.service.verification;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final AccountRepository accountRepository;
    private final MailService mailService;

    private final Map<String, String> verificationCodeMap = new HashMap<>();

    @Async
    public void sendVerifyCode(String email) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("No account found with the provided email!");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodeMap.put(email, code);

        Account acc = optionalAccount.get();
        String content =
                "Hello " + acc.getFullName() + ",\n\n" +
                        "Thank you for registering an account at PC Online Shop.\n\n" +
                        "Your account verification code is: " + code + "\n\n" +
                        "Please enter this code within 5 minutes to activate your account.\n\n" +
                        "Best regards,\nPC Online Shop";

        mailService.sendEmail(acc.getEmail(), "PC Online Shop account verification", content);
    }

    public void verifyAccount(String email, String code) {
        String storedCode = verificationCodeMap.get(email);
        if (storedCode == null || !storedCode.equals(code)) {
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
}
