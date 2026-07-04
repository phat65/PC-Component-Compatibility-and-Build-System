package com.example.PCOnlineShop.service.feedback;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PCOnlineShop.constant.OrderStatus;
import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.model.feedback.Feedback;
import com.example.PCOnlineShop.model.product.Product;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import com.example.PCOnlineShop.repository.feedback.FeedbackRepository;
import com.example.PCOnlineShop.repository.order.OrderDetailRepository;
import com.example.PCOnlineShop.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private static final String STATUS_ALLOW = "Allow";
    private static final String STATUS_PENDING = "PENDING";
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    private static final int MAX_COMMENT_LENGTH = 500;
    private static final int MAX_REPLY_LENGTH = 500;
    private static final List<String> BANNED_WORDS = List.of(
            "ngu", "điên", "vl", "cl", "dm", "chết",
            "mẹ", "đụ", "cặc",
            "fuck", "shit", "bitch"
    );

    private final FeedbackRepository feedbackRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final OrderDetailRepository orderDetailRepository;

    /** FULL LIST – NO PAGING */
    @Override
    public List<Feedback> findAllNoPaging(String sortKey) {
        Sort sort = toSort(sortKey);
        return feedbackRepository.findAll(sort);
    }

    /** SORT RULE */
    private Sort toSort(String key) {
        if (key == null) key = "pendingFirst";

        return switch (key) {

            case "pendingFirst" ->
                    Sort.by(Sort.Order.asc("reply"), Sort.Order.desc("createdAt"));

            default -> Sort.by("createdAt").descending();
        };
    }

    /** CRUD */
    @Override
    public Feedback get(Integer id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback does not exist: " + id));
    }

    @Override
    @Transactional
    public void updateReply(Integer id, String reply) {
        Feedback fb = get(id);
        String normalizedReply = normalizeRequiredText(reply, "Reply", MAX_REPLY_LENGTH);
        fb.setReply(normalizedReply);
        fb.setCommentStatus(STATUS_ALLOW);
        feedbackRepository.save(fb);
    }

    @Override
    @Transactional
    public void approveFeedback(Integer id) {
        Feedback fb = get(id);
        fb.setCommentStatus(STATUS_ALLOW);
        feedbackRepository.save(fb);
    }

    @Override
    public Page<Feedback> getAllowedByProduct(Integer productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return feedbackRepository.findByProduct_ProductIdAndCommentStatusOrderByCreatedAtDesc(
                productId, STATUS_ALLOW, pageable
        );
    }

    @Override
    @Transactional
    public void createFeedback(Integer productId, Integer accountId, Integer rating, String comment) {
        validateRating(rating);
        String normalizedComment = normalizeRequiredText(comment, "Comment", MAX_COMMENT_LENGTH);

        boolean hasPurchased = orderDetailRepository
                .existsByOrder_Account_AccountIdAndProduct_ProductIdAndOrder_Status(
                        accountId, productId, OrderStatus.COMPLETED);

        if (!hasPurchased)
            throw new IllegalArgumentException("You can review only the products you've purchased!");

        normalizedComment = maskBannedWords(normalizedComment);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("The product does not exist!"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("The account does not exist!"));

        Feedback fb = new Feedback();
        fb.setProduct(product);
        fb.setAccount(account);
        fb.setRating(rating);
        fb.setComment(normalizedComment);
        fb.setCommentStatus(STATUS_PENDING);
        fb.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(fb);

    }
    @Override
    public Double getAverageRating(Integer productId) {
        Double avg = feedbackRepository.getAverageRating(productId);
        return (avg == null) ? 0.0 : avg;
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < MIN_RATING || rating > MAX_RATING) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
    }

    private String normalizeRequiredText(String value, String fieldName, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters.");
        }

        return normalized;
    }

    private String maskBannedWords(String comment) {
        String masked = comment;
        for (String word : BANNED_WORDS) {
            String pattern = "(?iu)(?<![\\p{L}\\p{N}_])" + Pattern.quote(word) + "(?![\\p{L}\\p{N}_])";
            masked = masked.replaceAll(pattern, "***");

            String normalizedWord = removeVietnameseAccents(word);
            if (!normalizedWord.equalsIgnoreCase(word)) {
                String normalizedPattern = "(?iu)(?<![\\p{L}\\p{N}_])"
                        + Pattern.quote(normalizedWord)
                        + "(?![\\p{L}\\p{N}_])";
                masked = masked.replaceAll(normalizedPattern, "***");
            }
        }
        return masked;
    }

    private String removeVietnameseAccents(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return normalized.toLowerCase(Locale.ROOT);
    }

}
