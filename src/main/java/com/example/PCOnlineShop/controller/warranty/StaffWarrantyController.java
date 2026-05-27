package com.example.PCOnlineShop.controller.warranty;

import com.example.PCOnlineShop.dto.warranty.WarrantyDetailDTO;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/staff/warranty")
@RequiredArgsConstructor
public class StaffWarrantyController {
    private static final String WARRANTY_CHECK_VIEW = "warranty/check-warranty";

    private final OrderService orderService;

    @GetMapping("/check")
    public String showCheckPage(@RequestParam(required = false) String phone, Model model) {
        String normalizedPhone = normalize(phone);
        if (StringUtils.hasText(normalizedPhone)) {
            model.addAttribute("phone", normalizedPhone);
        }

        return WARRANTY_CHECK_VIEW;
    }

    @PostMapping("/search")
    public String searchWarrantyByPhone(@RequestParam String phone,
                                        @RequestParam(required = false) Integer orderId,
                                        Model model) {
        String normalizedPhone = normalize(phone);
        model.addAttribute("phone", normalizedPhone);

        if (!StringUtils.hasText(normalizedPhone)) {
            model.addAttribute("error", "Phone number is required.");
            model.addAttribute("orders", List.of());
            return WARRANTY_CHECK_VIEW;
        }

        List<Order> orders = orderService.getOrdersByPhoneNumberForWarranty(normalizedPhone);
        model.addAttribute("orders", orders);

        if (orderId != null) {
            List<WarrantyDetailDTO> warrantyDetails = orderService.getWarrantyDetailsByOrderId(orderId);
            model.addAttribute("warrantyDetails", warrantyDetails);
            model.addAttribute("selectedOrderId", orderId);
        }

        return WARRANTY_CHECK_VIEW;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
