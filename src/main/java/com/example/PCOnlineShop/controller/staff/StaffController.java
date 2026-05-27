package com.example.PCOnlineShop.controller.staff;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.staff.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/list")
    public String listStaff(@RequestParam(defaultValue = "active") String statusFilter,
                            Model model) {
        List<Account> staffList = staffService.getAllStaff(statusFilter);
        model.addAttribute("staffList", staffList);
        model.addAttribute("statusFilter", statusFilter);

        return "staff/staff-list";
    }

    @GetMapping("/view/{id}")
    public String viewStaff(@PathVariable int id, Model model) {
        Account account = staffService.getById(id);
        if (account == null) {
            return "redirect:/staff/list";
        }

        model.addAttribute("account", account);
        return "staff/view-staff";
    }

    @GetMapping("/add")
    public String addStaffForm(Model model) {
        model.addAttribute("account", new Account());
        return "staff/add-staff";
    }

    @PostMapping("/add")
    public String saveStaff(@Valid @ModelAttribute("account") Account account,
                            BindingResult result,
                            @RequestParam(name = "addressStr", required = false) String addressStr,
                            Model model) {
        if (result.hasErrors()) {
            return "staff/add-staff";
        }

        try {
            staffService.createStaff(account, addressStr);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "staff/add-staff";
        }

        return "redirect:/staff/list";
    }

    @GetMapping("/edit/{id}")
    public String editStaffForm(@PathVariable int id, Model model) {
        Account account = staffService.getById(id);
        if (account == null) {
            return "redirect:/staff/list";
        }

        model.addAttribute("account", account);
        return "staff/edit-staff";
    }

    @PostMapping("/edit")
    public String updateStaff(@Valid @ModelAttribute("account") Account account,
                              BindingResult result,
                              @RequestParam(name = "addressStr", required = false) String addressStr,
                              Model model) {
        if (result.hasErrors()) {
            return "staff/edit-staff";
        }

        try {
            staffService.updateStaff(account, addressStr);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "staff/edit-staff";
        }

        return "redirect:/staff/list";
    }

    @GetMapping("/delete/{id}")
    public String deactivateStaff(@PathVariable int id) {
        staffService.toggleStaffEnabled(id);
        return "redirect:/staff/list";
    }
}
