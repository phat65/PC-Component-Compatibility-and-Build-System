package com.example.PCOnlineShop.controller.staff;

import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.service.order.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff/shipping")
@RequiredArgsConstructor
@Slf4j
public class StaffShippingController {
    private static final String SHIPPING_LIST_VIEW = "staffshipping/shipping-list";
    private static final String REDIRECT_SHIPPING_LIST = "redirect:/staff/shipping/list";

    private final OrderService orderService;

    @GetMapping("/list")
    public String viewShippingList(Model model) {
        List<Order> shippingQueueOrders = orderService.getShippingQueueOrders();
        model.addAttribute("shippingOrders", shippingQueueOrders);
        return SHIPPING_LIST_VIEW;
    }

    @PostMapping("/update-status/{orderId}")
    public String updateShippingOrderStatus(@PathVariable int orderId,
                                            @RequestParam("newStatus") String newStatus,
                                            RedirectAttributes redirectAttributes) {
        try {
            String result = orderService.processShippingStatusUpdate(orderId, newStatus);
            addStatusMessage(redirectAttributes, result);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unexpected error updating shipping status for order {}", orderId, e);
            redirectAttributes.addFlashAttribute("error", "Unable to update shipping status. Please try again.");
        }

        return REDIRECT_SHIPPING_LIST;
    }

    private void addStatusMessage(RedirectAttributes redirectAttributes, String result) {
        if (result.startsWith("Success")) {
            redirectAttributes.addFlashAttribute("success", result);
        } else if (result.startsWith("No change")) {
            redirectAttributes.addFlashAttribute("info", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
    }
}
