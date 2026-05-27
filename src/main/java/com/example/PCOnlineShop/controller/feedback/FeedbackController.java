package com.example.PCOnlineShop.controller.feedback;

import com.example.PCOnlineShop.model.feedback.Feedback;
import com.example.PCOnlineShop.service.feedback.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staff/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public String list(@RequestParam(required = false) String dateSort,
                       @RequestParam(required = false) String ratingSort,
                       Model model) {
        String sortKey = resolveSortKey(dateSort, ratingSort);
        List<Feedback> data = feedbackService.findAllNoPaging(sortKey);

        model.addAttribute("data", data);
        model.addAttribute("dateSort", dateSort);
        model.addAttribute("ratingSort", ratingSort);

        return "feedback/feedback-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id,
                         @RequestParam(required = false) String back,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            Feedback feedback = feedbackService.get(id);
            model.addAttribute("fb", feedback);
            model.addAttribute("back", back);
            return "feedback/feedback-detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/feedback";
        }
    }

    @PostMapping("/{id}/reply")
    public String reply(@PathVariable Integer id,
                        @RequestParam String reply,
                        RedirectAttributes redirectAttributes) {
        try {
            feedbackService.updateReply(id, reply);
            redirectAttributes.addFlashAttribute("msg", "Feedback reply saved successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/staff/feedback";
    }

    private String resolveSortKey(String dateSort, String ratingSort) {
        if (ratingSort != null && !ratingSort.isBlank()) {
            return ratingSort;
        }
        if (dateSort != null && !dateSort.isBlank()) {
            return dateSort;
        }
        return "pendingFirst";
    }
}
