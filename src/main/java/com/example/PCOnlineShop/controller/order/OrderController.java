package com.example.PCOnlineShop.controller.order;

import com.example.PCOnlineShop.dto.order.CheckoutDTO;
import com.example.PCOnlineShop.dto.order.CheckoutPageDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.order.Order;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.order.OrderService;
import com.example.PCOnlineShop.service.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final AccountService accountService;

    private Account getCurrentAccount(UserDetails userDetails) {
        if (userDetails == null) return null;
        return accountService.getByPhoneNumber(userDetails.getUsername());
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(role));
    }

    private boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    private boolean isStaff() {
        return hasRole("ROLE_STAFF");
    }

    private boolean isStaffOrAdmin() {
        return isStaff() || isAdmin();
    }

    private String roleOrderListPath() {
        if (isAdmin()) return "/admin/orders";
        if (isStaff()) return "/staff/orders";
        return "/account/orders";
    }

    private String roleOrderDetailPath(long orderId) {
        return roleOrderListPath() + "/" + orderId;
    }

    private void populateOrderDetailModel(long id, Model model, Account currentAccount, boolean managementView) {
        Order order = orderService.getOrderDetailForView(id, currentAccount, managementView);
        model.addAttribute("isStaffOrAdmin", managementView);
        model.addAttribute("order", order);
        model.addAttribute("details", orderService.getOrderDetails(id));
        model.addAttribute("pageTitle", "Order Detail #" + order.getOrderId());
        model.addAttribute("paymentInfo", paymentService.getPaymentInfoSafe(id));
    }

    @GetMapping("/orders/list")
    public String redirectLegacyOrderList(@AuthenticationPrincipal UserDetails currentUserDetails) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        return "redirect:" + roleOrderListPath();
    }

    @GetMapping("/orders/detail/{id}")
    public String redirectLegacyOrderDetail(@PathVariable long id,
                                            @AuthenticationPrincipal UserDetails currentUserDetails) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        return "redirect:" + roleOrderDetailPath(id);
    }

    @GetMapping("/account/orders")
    public String viewCustomerOrderList(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        model.addAttribute("isStaffOrAdmin", false);
        model.addAttribute("pageTitle", "My Orders");
        model.addAttribute("customerOrders", orderService.getOrdersByAccount(currentAccount));
        return "orders/order-list";
    }

    @GetMapping("/account/orders/{id}")
    public String viewCustomerOrderDetail(@PathVariable long id, Model model,
                                          @AuthenticationPrincipal UserDetails currentUserDetails,
                                          RedirectAttributes redirectAttributes) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        try {
            populateOrderDetailModel(id, model, currentAccount, false);
            return "orders/order-detail";
        } catch (SecurityException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account/orders";
        } catch (RuntimeException e) {
            log.error("Unexpected error loading order detail {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Unable to load order detail.");
            return "redirect:/account/orders";
        }
    }

    @GetMapping("/admin/orders")
    public String viewAdminOrderList(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        model.addAttribute("isStaffOrAdmin", true);
        model.addAttribute("pageTitle", "Order Management");
        model.addAttribute("adminOrderList", orderService.findAllOrdersForAdmin());
        return "orders/admin-order-list";
    }

    @GetMapping("/admin/orders/{id}")
    public String viewAdminOrderDetail(@PathVariable long id, Model model,
                                       @AuthenticationPrincipal UserDetails currentUserDetails,
                                       RedirectAttributes redirectAttributes) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        try {
            populateOrderDetailModel(id, model, currentAccount, true);
            return "orders/admin-order-detail";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/orders";
        } catch (RuntimeException e) {
            log.error("Unexpected error loading admin order detail {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Unable to load order detail.");
            return "redirect:/admin/orders";
        }
    }

    @GetMapping("/staff/orders")
    public String viewStaffOrderList(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        model.addAttribute("isStaffOrAdmin", true);
        model.addAttribute("pageTitle", "Order Management");
        model.addAttribute("adminOrderList", orderService.findAllOrdersForAdmin());
        return "orders/staff-order-list";
    }

    @GetMapping("/staff/orders/{id}")
    public String viewStaffOrderDetail(@PathVariable long id, Model model,
                                       @AuthenticationPrincipal UserDetails currentUserDetails,
                                       RedirectAttributes redirectAttributes) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        try {
            populateOrderDetailModel(id, model, currentAccount, true);
            return "orders/staff-order-detail";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/orders";
        } catch (RuntimeException e) {
            log.error("Unexpected error loading staff order detail {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Unable to load order detail.");
            return "redirect:/staff/orders";
        }
    }

    @PostMapping("/admin/orders/{id}/complete-pickup")
    public String completeAdminPickupOrder(@PathVariable long id, RedirectAttributes redirectAttributes) {
        return completePickupOrder(id, "/admin/orders/" + id, redirectAttributes);
    }

    @PostMapping("/staff/orders/{id}/complete-pickup")
    public String completeStaffPickupOrder(@PathVariable long id, RedirectAttributes redirectAttributes) {
        return completePickupOrder(id, "/staff/orders/" + id, redirectAttributes);
    }

    @GetMapping("/orders/checkout")
    public String showCheckoutPage(Model model,
                                   @AuthenticationPrincipal UserDetails currentUserDetails,
                                   RedirectAttributes redirectAttributes) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        try {
            CheckoutPageDTO data = orderService.prepareCheckoutData(currentAccount);

            model.addAttribute("checkoutDTO", data.getCheckoutDTO());
            model.addAttribute("cartItems", data.getSelectedItems());
            model.addAttribute("grandTotal", data.getGrandTotal());
            model.addAttribute("account", currentAccount);
            model.addAttribute("defaultAddress", data.getDefaultAddress());
            model.addAttribute("allAddresses", data.getAllAddresses());
            model.addAttribute("pageTitle", "Checkout");
            model.addAttribute("postActionUrl", "/orders/checkout");

            return "orders/checkout";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/orders/checkout")
    public String processCheckout(@Valid @ModelAttribute("checkoutDTO") CheckoutDTO checkoutDTO,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal UserDetails currentUserDetails,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        Account currentAccount = getCurrentAccount(currentUserDetails);
        if (currentAccount == null) return "redirect:/auth/login";

        if (bindingResult.hasErrors()) {
            try {
                CheckoutPageDTO data = orderService.prepareCheckoutData(currentAccount);
                model.addAttribute("cartItems", data.getSelectedItems());
                model.addAttribute("grandTotal", data.getGrandTotal());
                model.addAttribute("account", currentAccount);
                model.addAttribute("allAddresses", data.getAllAddresses());
                model.addAttribute("defaultAddress", data.getDefaultAddress());
                model.addAttribute("pageTitle", "Checkout");
                model.addAttribute("postActionUrl", "/orders/checkout");
                return "orders/checkout";
            } catch (IllegalStateException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/cart";
            } catch (RuntimeException e) {
                log.error("Unexpected error rebuilding checkout form for account {}", currentAccount.getAccountId(), e);
                redirectAttributes.addFlashAttribute("error", "Unable to load checkout data.");
                return "redirect:/cart";
            }
        }

        try {
            String payosCheckoutUrl = paymentService.createCheckoutPaymentLink(currentAccount, checkoutDTO);
            return "redirect:" + payosCheckoutUrl;

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/checkout";
        } catch (RuntimeException e) {
            log.error("Unexpected error processing checkout for account {}", currentAccount.getAccountId(), e);
            redirectAttributes.addFlashAttribute("error", "Unable to create payment. Please try again.");
            return "redirect:/orders/checkout";
        }
    }

    private String completePickupOrder(long id, String redirectPath, RedirectAttributes redirectAttributes) {
        try {
            orderService.completePickupOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pickup order marked as completed.");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unexpected error completing pickup order {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Unable to complete pickup order.");
        }
        return "redirect:" + redirectPath;
    }
}
