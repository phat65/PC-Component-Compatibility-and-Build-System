package com.example.PCOnlineShop.controller.payment;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.order.OrderService;
import com.example.PCOnlineShop.service.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final AccountService accountService;

    @GetMapping("/callback/success")
    public String handleSuccessCallback(@RequestParam("orderCode") long orderCode,
                                        RedirectAttributes redirectAttributes) {
        try {
            boolean isPaid = paymentService.verifyPaymentStatus(orderCode);

            if (isPaid) {
                redirectAttributes.addFlashAttribute("success", "Payment Successful. Order confirmed.");
            } else {
                redirectAttributes.addFlashAttribute("info", "Payment processing...");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error checking payment.");
        }
        return "redirect:/orders/list";
    }

    @GetMapping("/callback/failed")
    public String handleFailedCallback(@RequestParam(value = "orderCode", required = false) Long orderCode,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (orderCode != null) {
                paymentService.processFailedPayment(orderCode);
            }
            redirectAttributes.addFlashAttribute("error", "Payment Cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to update cancelled payment.");
        }
        return "redirect:/orders/list";
    }

    @GetMapping("/continue/{orderId}")
    public String continuePayment(@PathVariable long orderId,
                                  @AuthenticationPrincipal UserDetails currentUserDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            validateOrderAccess(orderId, currentUserDetails);
            String checkoutUrl = paymentService.getOrRegeneratePaymentUrl(orderId);
            return "redirect:" + checkoutUrl;
        } catch (SecurityException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to retrieve payment link: " + e.getMessage());
            return "redirect:/orders/detail/" + orderId;
        }
    }

    @PostMapping("/webhook")
    @ResponseBody
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody Object body) {
        try {
            paymentService.handleWebhook(body);
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/info/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getPaymentInfo(@PathVariable long orderId,
                                            @AuthenticationPrincipal UserDetails currentUserDetails) {
        if (currentUserDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Please login again."));
        }

        try {
            validateOrderAccess(orderId, currentUserDetails);
            return ResponseEntity.ok(paymentService.getPaymentInfoByOrderId(orderId));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private void validateOrderAccess(long orderId, UserDetails currentUserDetails) {
        orderService.getOrderDetailForView(orderId, getCurrentAccount(currentUserDetails), isStaffOrAdmin());
    }

    private Account getCurrentAccount(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return accountService.getByPhoneNumber(userDetails.getUsername());
    }

    private boolean isStaffOrAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_STAFF") || role.getAuthority().equals("ROLE_ADMIN"));
    }
}

