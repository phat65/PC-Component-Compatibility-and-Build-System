package com.example.PCOnlineShop.service.common;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@Component("money")
public class MoneyFormatService {

    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");
    public static final String VND = "VND";

    public String format(Object amount) {
        BigDecimal value = toBigDecimal(amount);
        NumberFormat formatter = NumberFormat.getNumberInstance(VIETNAM_LOCALE);
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(toWholeVnd(value)) + " " + VND;
    }

    public long toWholeVnd(Object amount) {
        return toWholeVnd(toBigDecimal(amount)).longValue();
    }

    private BigDecimal toWholeVnd(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal toBigDecimal(Object amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        if (amount instanceof BigDecimal decimal) {
            return decimal;
        }
        if (amount instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(amount.toString().trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
