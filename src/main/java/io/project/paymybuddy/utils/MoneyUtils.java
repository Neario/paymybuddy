package io.project.paymybuddy.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("moneyUtils")
public class MoneyUtils {

    public static int toCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).intValueExact();
    }

    public static BigDecimal toEuros(int amount) {
        return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public static int withFee(int cents,  BigDecimal feeRate) {
        int fee = BigDecimal.valueOf(cents)
                .multiply(feeRate)
                .intValue();
        return cents + fee ;
    }
}
