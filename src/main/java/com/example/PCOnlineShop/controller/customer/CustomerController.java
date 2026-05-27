package com.example.PCOnlineShop.controller.customer;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.service.customer.CustomerService;
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
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/list")
    public String listCustomers(@RequestParam(defaultValue = "active") String statusFilter,
                                Model model) {
        List<Account> customerList = customerService.getAllCustomers(statusFilter);
        model.addAttribute("customerList", customerList);
        model.addAttribute("statusFilter", statusFilter);

        return "customer/customer-list";
    }

    @GetMapping("/view/{id}")
    public String viewCustomer(@PathVariable int id, Model model) {
        Account account = customerService.getById(id);
        if (account == null) {
            return "redirect:/customer/list";
        }

        model.addAttribute("account", account);
        return "customer/view-customer";
    }

    @GetMapping("/add")
    public String addCustomerForm(Model model) {
        model.addAttribute("account", new Account());
        return "customer/add-customer";
    }

    @PostMapping("/add")
    public String saveCustomer(@Valid @ModelAttribute("account") Account account,
                               BindingResult result,
                               @RequestParam(name = "addressStr", required = false) String addressStr,
                               Model model) {
        if (result.hasErrors()) {
            return "customer/add-customer";
        }

        try {
            customerService.createCustomer(account, addressStr);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/add-customer";
        }

        return "redirect:/customer/list";
    }

    @GetMapping("/edit/{id}")
    public String editCustomerForm(@PathVariable int id, Model model) {
        Account account = customerService.getById(id);
        if (account == null) {
            return "redirect:/customer/list";
        }

        model.addAttribute("account", account);
        return "customer/edit-customer";
    }

    @PostMapping("/edit")
    public String updateCustomer(@Valid @ModelAttribute("account") Account account,
                                 BindingResult result,
                                 @RequestParam(name = "addressStr", required = false) String addressStr,
                                 Model model) {
        if (result.hasErrors()) {
            return "customer/edit-customer";
        }

        try {
            customerService.updateCustomer(account, addressStr);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/edit-customer";
        }

        return "redirect:/customer/list";
    }

    @GetMapping("/delete/{id}")
    public String toggleCustomerStatus(@PathVariable int id) {
        customerService.toggleCustomerEnabled(id);
        return "redirect:/customer/list";
    }
}
