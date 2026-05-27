package com.example.PCOnlineShop.controller.blog;

import com.example.PCOnlineShop.dto.blog.BlogLinkDto;
import com.example.PCOnlineShop.service.blog.HacomScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blog")
public class ExternalBlogController {
    private static final String HACOM_VIEW = "blog/hacom-list";
    private static final String REDIRECT_HACOM = "redirect:/blog/hacom";
    private static final String HACOM_SOURCE_NAME = "HACOM";
    private static final String HACOM_SOURCE_URL = "https://hacom.vn/tin-tuc";

    private final HacomScraperService scraperService;

    @GetMapping("/hacom")
    public String showHacom(Model model) {
        try {
            List<BlogLinkDto> links = scraperService.getLatest();
            model.addAttribute("links", links);
        } catch (Exception e) {
            model.addAttribute("links", List.of());
            model.addAttribute("error", "Unable to load blog posts at this time.");
        }

        model.addAttribute("sourceName", HACOM_SOURCE_NAME);
        model.addAttribute("sourceUrl", HACOM_SOURCE_URL);
        return HACOM_VIEW;
    }

    @GetMapping
    public String showBlogHome() {
        return REDIRECT_HACOM;
    }
}
