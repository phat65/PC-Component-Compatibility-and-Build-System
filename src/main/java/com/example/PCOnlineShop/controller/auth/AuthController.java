package com.example.PCOnlineShop.controller.auth;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("account", new Account());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("account") Account account,
                           @RequestParam("address") String addressStr,
                           @RequestParam("confirmPassword") String confirmPassword,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        try {
            authService.register(account, addressStr, confirmPassword);
            redirectAttributes.addFlashAttribute("phoneNumber", account.getPhoneNumber());
            redirectAttributes.addAttribute("email", account.getEmail());
            return "redirect:/auth/verify";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("email") String email, Model model) {
        try {
            authService.sendVerifyCode(email);
            model.addAttribute("email", email);
            return "auth/verify";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", new Account());
            return "auth/register";
        }
    }

    @PostMapping("/verify")
    public String verify(@RequestParam("email") String email,
                         @RequestParam("code") String code,
                         Model model) {
        try {
            authService.verifyAccount(email, code);
            return "redirect:/auth/login?success";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "⚠️ Mã xác nhận không đúng hoặc đã hết hạn!");
            model.addAttribute("email", email);
            return "auth/verify";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/forget-password")
    public String showForgetPasswordPage() {
        return "auth/forget-password";
    }

    @PostMapping("/forget-password")
    public String processForgetPassword(@RequestParam("identifier") String identifier,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        try {
            authService.sendResetCode(identifier);
            redirectAttributes.addAttribute("identifier", identifier);
            return "redirect:/auth/code-forget-password";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forget-password";
        }
    }

    @GetMapping("/code-forget-password")
    public String showCodeForgetPassword(@RequestParam("identifier") String identifier, Model model) {
        model.addAttribute("identifier", identifier);
        return "auth/code-forget-password";
    }

    @PostMapping("/code-forget-password")
    public String verifyResetCode(@RequestParam("identifier") String identifier,
                                  @RequestParam("code") String code,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (authService.verifyResetCode(identifier, code)) {
            redirectAttributes.addAttribute("identifier", identifier);
            return "redirect:/auth/reset-password";
        }

        model.addAttribute("error", "⚠️ Mã xác nhận không đúng hoặc đã hết hạn!");
        model.addAttribute("identifier", identifier);
        return "auth/code-forget-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam("identifier") String identifier, Model model) {
        model.addAttribute("identifier", identifier);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("identifier") String identifier,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {
        try {
            authService.resetPassword(identifier, newPassword, confirmPassword);
            return "redirect:/auth/login?resetSuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("identifier", identifier);
            return "auth/reset-password";
        }
    }
}
