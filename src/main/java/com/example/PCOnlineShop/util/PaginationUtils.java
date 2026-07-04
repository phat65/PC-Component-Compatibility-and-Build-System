package com.example.PCOnlineShop.util;

import java.util.ArrayList;
import java.util.List;

public final class PaginationUtils {
    public static final int ELLIPSIS = -1;

    private static final int SIBLING_PAGES = 3;

    private PaginationUtils() {
    }

    public static List<Integer> compactPageNumbers(int currentPage, int totalPages) {
        if (totalPages <= 0) {
            return List.of();
        }

        int safeCurrentPage = Math.min(Math.max(currentPage, 0), totalPages - 1);
        List<Integer> pages = new ArrayList<>();

        int previousPage = -1;
        int windowStart = Math.max(0, safeCurrentPage - SIBLING_PAGES);
        int windowEnd = Math.min(totalPages - 1, safeCurrentPage + SIBLING_PAGES);

        for (int page = 0; page < totalPages; page++) {
            boolean shouldShow = page == 0 || page == totalPages - 1 || (page >= windowStart && page <= windowEnd);
            if (!shouldShow) {
                continue;
            }

            if (previousPage >= 0) {
                int gap = page - previousPage;
                if (gap == 2) {
                    pages.add(previousPage + 1);
                } else if (gap > 2) {
                    pages.add(ELLIPSIS);
                }
            }

            pages.add(page);
            previousPage = page;
        }

        return pages;
    }
}
