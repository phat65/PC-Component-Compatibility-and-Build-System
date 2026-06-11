package com.example.PCOnlineShop.controller;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CartModelAdvice {

    private final AccountService accountService;
    private final CartService cartService;

    @ModelAttribute("cartItemCount")
    public int cartItemCount(@AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null || !isCustomer(currentUser)) {
            return 0;
        }

        try {
            Account account = accountService.getByPhoneNumber(currentUser.getUsername());
            return account == null ? 0 : cartService.countItems(account);
        } catch (RuntimeException e) {
            log.warn("Unable to load cart item count for {}", currentUser.getUsername(), e);
            return 0;
        }
    }

    private boolean isCustomer(UserDetails currentUser) {
        return currentUser.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_CUSTOMER".equals(authority.getAuthority()));
    }
}
