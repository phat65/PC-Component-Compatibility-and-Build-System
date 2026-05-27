package com.example.PCOnlineShop.controller.product;

import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.service.feedback.FeedbackService;
import com.example.PCOnlineShop.service.product.CategoryService;
import com.example.PCOnlineShop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProductDetailController {
    private static final String PRODUCT_DETAILS_VIEW = "product/product-details";
    private static final String REDIRECT_PRODUCT_LIST = "redirect:/products";
    private static final int FEEDBACK_PAGE = 0;
    private static final int FEEDBACK_PAGE_SIZE = 100;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final FeedbackService feedbackService;

    @GetMapping({"/products/{id}", "/product/detail/{id}"})
    public String showProductDetail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.findVisibleSellingProductById(id).orElse(null);
        if (product == null) {
            return REDIRECT_PRODUCT_LIST;
        }

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("images", product.getImages());
        model.addAttribute("relatedProducts", productService.getRelatedStorefrontProducts(product));

        var feedbackPage = feedbackService.getAllowedByProduct(id, FEEDBACK_PAGE, FEEDBACK_PAGE_SIZE);
        model.addAttribute("feedbackPage", feedbackPage);
        model.addAttribute("avgRating", feedbackService.getAverageRating(id));
        model.addAttribute("feedbackCount", feedbackPage.getTotalElements());

        return PRODUCT_DETAILS_VIEW;
    }
}
