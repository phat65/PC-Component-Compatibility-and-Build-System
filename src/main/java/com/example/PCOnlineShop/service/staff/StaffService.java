package com.example.PCOnlineShop.service.staff;

import com.example.PCOnlineShop.constant.RoleName;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.service.account.AccountService;
import com.example.PCOnlineShop.service.address.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_INACTIVE = "inactive";

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final AddressService addressService;

    public List<Account> getAllStaff(String statusFilter) {
        Boolean enabled = parseStatusFilter(statusFilter);
        if (enabled == null) {
            return accountRepository.findAllByRoleWithAddresses(RoleName.Staff);
        }

        return accountRepository.findByRoleAndEnabledWithAddresses(RoleName.Staff, enabled);
    }

    public Optional<Account> findStaffById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }

        return accountRepository.findById(id)
                .filter(account -> account.getRole() == RoleName.Staff);
    }

    public Account getById(int id) {
        return findStaffById(id).orElse(null);
    }

    public Account getRequiredStaff(Integer id) {
        return findStaffById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found"));
    }

    @Transactional
    public Account createStaff(Account account, String addressStr) {
        if (account.getAccountId() != 0) {
            throw new IllegalArgumentException("New staff account must not have an id");
        }
        Account saved = accountService.saveStaff(account);
        saveDefaultAddress(saved, addressStr);
        return saved;
    }

    @Transactional
    public Account updateStaff(Account account, String addressStr) {
        getRequiredStaff(account.getAccountId());
        Boolean requestedEnabled = account.getEnabled();
        Account saved = accountService.saveStaff(account);
        if (requestedEnabled != null && !requestedEnabled.equals(saved.getEnabled())) {
            saved.setEnabled(requestedEnabled);
            saved = accountRepository.save(saved);
        }
        updateDefaultAddress(saved, addressStr);
        return saved;
    }

    @Transactional
    public boolean toggleStaffEnabled(int id) {
        return findStaffById(id)
                .map(account -> {
                    account.setEnabled(!Boolean.TRUE.equals(account.getEnabled()));
                    accountRepository.save(account);
                    return true;
                })
                .orElse(false);
    }

    public void deactivateStaff(int id) {
        toggleStaffEnabled(id);
    }

    public void saveDefaultAddress(Account account, String addressStr) {
        if (account == null || isBlank(addressStr)) {
            return;
        }

        addressService.saveDefaultAddress(
                account,
                account.getFullName(),
                account.getPhoneNumber(),
                addressStr.trim()
        );
    }

    public void updateDefaultAddress(Account account, String addressStr) {
        if (account == null || isBlank(addressStr)) {
            return;
        }

        addressService.updateDefaultAddress(
                account,
                account.getFullName(),
                account.getPhoneNumber(),
                addressStr.trim()
        );
    }

    private Boolean parseStatusFilter(String statusFilter) {
        if (statusFilter == null) {
            return null;
        }

        return switch (statusFilter.trim().toLowerCase(Locale.ROOT)) {
            case STATUS_ACTIVE -> true;
            case STATUS_INACTIVE -> false;
            default -> null;
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
