package com.example.PCOnlineShop.controller.build;

import com.example.PCOnlineShop.dto.build.BuildItemDto;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.cart.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes({"buildItems"})
@RequestMapping("/build")
public class BuildController {
    private static final String BUILD_START_VIEW = "build/build-pc";
    private static final String PRESET_RESULT_VIEW = "build/preset-result";

    private final CartService cartService;
    private final AccountService accountService;

    public BuildController(CartService cartService, AccountService accountService) {
        this.cartService = cartService;
        this.accountService = accountService;
    }

    private Account getCurrentAccount(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return accountService.getByPhoneNumber(userDetails.getUsername());
    }

    @ModelAttribute("buildItems")
    public BuildItemDto buildItems() {
        return new BuildItemDto();
    }

    @GetMapping({"", "/"})
    public String redirectToBuildStartPage() {
        return "redirect:/build/start";
    }

    @GetMapping("/start")
    public String showBuildStartPage() {
        return BUILD_START_VIEW;
    }

    @GetMapping("/preset-result")
    public String showPresetResultPage() {
        return PRESET_RESULT_VIEW;
    }

    @GetMapping("/startover")
    public String restartBuild(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/build/mainboard";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/finish")
    public String finishBuild(@ModelAttribute("buildItems") BuildItemDto buildItems,
                              @AuthenticationPrincipal UserDetails currentUser,
                              SessionStatus sessionStatus,
                              RedirectAttributes redirectAttributes) {
        Account account = getCurrentAccount(currentUser);
        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Please login again.");
            return "redirect:/auth/login";
        }

        try {
            cartService.addBuildToCart(account, buildItems);
            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("success", "PC build has been added to your cart.");
            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Unable to save PC build: " + e.getMessage());
            return "redirect:/build/start";
        }
    }
}
