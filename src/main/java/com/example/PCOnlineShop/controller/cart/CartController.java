package com.example.PCOnlineShop.controller.cart;

import com.example.PCOnlineShop.dto.cart.CartSummaryDTO;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.cart.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private static final String LOGIN_REQUIRED_REDIRECT = "redirect:/auth/login?required";
    private static final String DEFAULT_REFERER_REDIRECT = "/";

    private final CartService cartService;
    private final AccountService accountService;

    private Account getCurrentAccount(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return accountService.getByPhoneNumber(userDetails.getUsername());
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        Account account = getCurrentAccount(currentUser);
        if (account == null) {
            return "redirect:/auth/login";
        }

        CartSummaryDTO cartSummary = cartService.getCartDetails(account);

        model.addAttribute("isEmpty", cartSummary.getItems().isEmpty());
        model.addAttribute("cartItems", cartSummary.getItems());
        model.addAttribute("grandTotal", cartSummary.getSelectedTotal());
        return "cart/view";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable int productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @AuthenticationPrincipal UserDetails currentUser,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        Account account = getCurrentAccount(currentUser);
        if (account == null) {
            return LOGIN_REQUIRED_REDIRECT;
        }

        try {
            cartService.addToCart(account, productId, quantity);
            addSuccess(redirectAttributes, "Product added to cart!");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            addError(redirectAttributes, e);
        } catch (RuntimeException e) {
            log.error("Unexpected error adding product {} to cart for account {}", productId, account.getAccountId(), e);
            addError(redirectAttributes, "Unable to add product to cart. Please try again.");
        }
        return redirectToReferer(request);
    }

    @GetMapping("/addListItem")
    public String addListItemToCart(@ModelAttribute("productIds") List<Integer> productIds,
                                    @RequestParam(defaultValue = "1") int quantity,
                                    @AuthenticationPrincipal UserDetails currentUser,
                                    RedirectAttributes redirectAttributes) {
        Account account = getCurrentAccount(currentUser);
        if (account == null) {
            return LOGIN_REQUIRED_REDIRECT;
        }

        if (productIds == null || productIds.isEmpty()) {
            return "redirect:/build/start";
        }

        try {
            cartService.addListToCart(account, productIds, quantity);
            addSuccess(redirectAttributes, "Products added!");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            addError(redirectAttributes, e);
        } catch (RuntimeException e) {
            log.error("Unexpected error adding product list to cart for account {}", account.getAccountId(), e);
            addError(redirectAttributes, "Unable to add products to cart. Please try again.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/update/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> updateQuantityAjax(@PathVariable int cartItemId,
                                                @RequestParam int quantity,
                                                @AuthenticationPrincipal UserDetails currentUser) {
        return handleCartAction(currentUser,
                account -> cartService.updateQuantity(account, cartItemId, quantity));
    }

    @PostMapping("/remove/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> removeFromCartAjax(@PathVariable int cartItemId,
                                                @AuthenticationPrincipal UserDetails currentUser) {
        return handleCartAction(currentUser,
                account -> cartService.removeFromCart(account, cartItemId));
    }

    @PostMapping("/select/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> selectItem(@PathVariable int cartItemId,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        return handleCartAction(currentUser,
                account -> cartService.toggleSelectItem(account, cartItemId, true));
    }

    @PostMapping("/deselect/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> deselectItem(@PathVariable int cartItemId,
                                          @AuthenticationPrincipal UserDetails currentUser) {
        return handleCartAction(currentUser,
                account -> cartService.toggleSelectItem(account, cartItemId, false));
    }

    @PostMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails currentUser, RedirectAttributes redirectAttributes) {
        Account account = getCurrentAccount(currentUser);
        if (account != null) {
            cartService.clearCart(account);
            redirectAttributes.addFlashAttribute("success", "Cart cleared.");
        }
        return "redirect:/cart";
    }

    private ResponseEntity<?> handleCartAction(UserDetails currentUser, Consumer<Account> action) {
        Account account = getCurrentAccount(currentUser);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Please log in."));
        }

        try {
            action.accept(account);
            double newTotal = cartService.calculateSelectedTotalForAccount(account);
            return ResponseEntity.ok(Map.of("message", "Success", "newGrandTotal", newTotal));
        } catch (IllegalArgumentException | EntityNotFoundException | SecurityException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Unexpected cart action error for account {}", account.getAccountId(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Unable to update cart. Please try again."));
        }
    }

    private String redirectToReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "redirect:" + DEFAULT_REFERER_REDIRECT;
        }

        String contextPath = request.getContextPath();
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (isDefaultPort(request) ? "" : ":" + request.getServerPort())
                + contextPath;

        if (referer.startsWith(baseUrl)) {
            String localPath = referer.substring(baseUrl.length());
            return "redirect:" + (localPath.isBlank() ? DEFAULT_REFERER_REDIRECT : localPath);
        }

        if (!referer.startsWith("//") && (referer.startsWith(contextPath + "/") || referer.startsWith("/"))) {
            return "redirect:" + referer;
        }

        return "redirect:" + DEFAULT_REFERER_REDIRECT;
    }

    private boolean isDefaultPort(HttpServletRequest request) {
        int port = request.getServerPort();
        return ("http".equals(request.getScheme()) && port == 80)
                || ("https".equals(request.getScheme()) && port == 443);
    }

    private void addSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("success", message);
    }

    private void addError(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    private void addError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("error", message);
    }
}
