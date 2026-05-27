package com.example.PCOnlineShop.controller.auth;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.account.Address;
import com.example.PCOnlineShop.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class CustomerProfileController {

    private final AuthService authService;

    @GetMapping("")
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String phoneNumber = principal.getName();
        Account account = authService.getByPhoneNumber(phoneNumber);

        if (account == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản!");
            model.addAttribute("account", new Account());
            model.addAttribute("addresses", List.of());
            return "profile/view-profile";
        }

        addProfileAttributes(model, account);
        return "profile/view-profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("account") Account updatedAccount,
                                Principal principal,
                                Model model) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        try {
            Account updated = authService.updateCustomerProfile(principal.getName(), updatedAccount);
            addProfileAttributes(model, updated);
            model.addAttribute("success", "Cập nhật thông tin thành công!");
        } catch (IllegalArgumentException e) {
            Account account = authService.getByPhoneNumber(principal.getName());
            addProfileAttributes(model, account);
            model.addAttribute("error", e.getMessage());
        }

        return "profile/view-profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal,
                                 Model model) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        Account account = authService.getByPhoneNumber(principal.getName());
        addProfileAttributes(model, account);

        try {
            authService.changePassword(principal.getName(), currentPassword, newPassword, confirmPassword);
            model.addAttribute("pwdSuccess", "✅ Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("pwdError", e.getMessage());
        }

        return "profile/view-profile";
    }

    private void addProfileAttributes(Model model, Account account) {
        if (account == null) {
            model.addAttribute("account", new Account());
            model.addAttribute("addresses", List.of());
            return;
        }

        List<Address> addresses = authService.getAddressesForAccount(account);
        model.addAttribute("account", account);
        model.addAttribute("addresses", addresses);
    }
}
