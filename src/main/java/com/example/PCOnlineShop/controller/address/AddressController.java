package com.example.PCOnlineShop.controller.address;

import com.example.PCOnlineShop.dto.address.AddressRequest;
import com.example.PCOnlineShop.dto.address.AddressResponse;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.account.Address;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.address.AddressService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;
    private final AccountService accountService;

    private Account getCurrentAccount(UserDetails userDetails) {
        if (userDetails == null) return null;
        return accountService.getByPhoneNumber(userDetails.getUsername());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewAddress(
            @Valid @ModelAttribute AddressRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUserDetails) {

        Account account = getCurrentAccount(currentUserDetails);
        if (account == null) {
            return unauthorized();
        }

        if (bindingResult.hasErrors()) {
            return badRequest(firstValidationMessage(bindingResult));
        }

        try {
            Address newAddress = addressService.addNewAddress(
                    account,
                    request.getFullName(),
                    request.getPhone(),
                    request.getAddress()
            );
            return ResponseEntity.ok(new AddressResponse(newAddress));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Unexpected error adding address for account {}", account.getAccountId(), e);
            return systemError();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateAddress(
            @RequestParam("addressId") int addressId,
            @Valid @ModelAttribute AddressRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUserDetails) {

        Account account = getCurrentAccount(currentUserDetails);
        if (account == null) {
            return unauthorized();
        }

        if (bindingResult.hasErrors()) {
            return badRequest(firstValidationMessage(bindingResult));
        }

        try {
            Address updatedAddress = addressService.updateAddress(
                    account,
                    addressId,
                    request.getFullName(),
                    request.getPhone(),
                    request.getAddress()
            );
            return ResponseEntity.ok(new AddressResponse(updatedAddress));
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Unexpected error updating address {} for account {}", addressId, account.getAccountId(), e);
            return systemError();
        }
    }

    @PostMapping("/set-default")
    public ResponseEntity<?> setDefaultAddress(
            @RequestParam("addressId") int addressId,
            @AuthenticationPrincipal UserDetails currentUserDetails) {

        Account account = getCurrentAccount(currentUserDetails);
        if (account == null) {
            return unauthorized();
        }

        try {
            addressService.setDefaultAddress(account, addressId);
            return ResponseEntity.ok(Map.of("message", "Đặt làm mặc định thành công."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Unexpected error setting default address {} for account {}", addressId, account.getAccountId(), e);
            return systemError();
        }
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(401).body(Map.of("error", "Please login again."));
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    private ResponseEntity<Map<String, String>> systemError() {
        return ResponseEntity.internalServerError().body(Map.of("error", "Error system."));
    }

    private String firstValidationMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().getFirst().getDefaultMessage();
    }
}
