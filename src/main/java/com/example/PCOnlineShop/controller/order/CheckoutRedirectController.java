package com.example.PCOnlineShop.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckoutRedirectController {

    @GetMapping("/checkout")
    public String redirectToCheckout() {
        return "redirect:/orders/checkout";
    }
}
