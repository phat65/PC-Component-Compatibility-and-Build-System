package com.example.PCOnlineShop.controller.auth;

import com.example.PCOnlineShop.dto.account.RegisterRequest;
import com.example.PCOnlineShop.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("account", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("account") RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (StringUtils.hasText(request.getPassword())
                && StringUtils.hasText(request.getConfirmPassword())
                && !request.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Confirm password does not match.");
        }

        if (bindingResult.hasErrors()) {
            clearSensitiveFields(request);
            model.addAttribute("error", firstValidationMessage(bindingResult));
            return "auth/register";
        }

        try {
            authService.register(request);
        } catch (IllegalArgumentException e) {
            clearSensitiveFields(request);
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }

        redirectAttributes.addFlashAttribute("phoneNumber", request.getPhoneNumber());
        sendVerifyCodeForRedirect(request.getEmail(), redirectAttributes);
        return redirectToVerify(request.getEmail(), redirectAttributes);
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("email") String email, Model model) {
        if (!StringUtils.hasText(email)) {
            model.addAttribute("error", "Email is required.");
            model.addAttribute("account", new RegisterRequest());
            return "auth/register";
        }

        email = email.trim();
        model.addAttribute("email", email);
        model.addAttribute("resendDelaySeconds", authService.getVerifyResendRemainingSeconds(email));
        return "auth/verify";
    }

    @PostMapping("/verify/resend")
    public String resendVerifyCode(@RequestParam("email") String email,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (!StringUtils.hasText(email)) {
            model.addAttribute("error", "Email is required.");
            model.addAttribute("account", new RegisterRequest());
            return "auth/register";
        }

        sendVerifyCodeForRedirect(email, redirectAttributes);
        return redirectToVerify(email, redirectAttributes);
    }

    @PostMapping("/verify")
    public String verify(@RequestParam("email") String email,
                         @RequestParam("code") String code,
                         Model model) {
        try {
            authService.verifyAccount(email, code);
            return "redirect:/auth/login?success";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", "The verification code is invalid or has expired.");
            model.addAttribute("email", email);
            if (StringUtils.hasText(email)) {
                model.addAttribute("resendDelaySeconds", authService.getVerifyResendRemainingSeconds(email));
            }
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

        model.addAttribute("error", "The verification code is invalid or has expired.");
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

    private String firstValidationMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().getFirst().getDefaultMessage();
    }

    private void clearSensitiveFields(RegisterRequest request) {
        request.setPassword(null);
        request.setConfirmPassword(null);
    }

    private void sendVerifyCodeForRedirect(String email, RedirectAttributes redirectAttributes) {
        try {
            authService.sendVerifyCode(email);
            redirectAttributes.addFlashAttribute("success", "Verification code sent. Please check your email.");
            redirectAttributes.addFlashAttribute("resendDelaySeconds", authService.getVerifyResendCooldownSeconds());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (StringUtils.hasText(email)) {
                redirectAttributes.addFlashAttribute("resendDelaySeconds", authService.getVerifyResendRemainingSeconds(email));
            }
        }
    }

    private String redirectToVerify(String email, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("email", email.trim());
        return "redirect:/auth/verify";
    }
}
