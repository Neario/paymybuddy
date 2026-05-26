package io.project.paymybuddy.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("moneyUtils")
public class MoneyUtils {

    public static BigDecimal toEuros(int amount) {
        return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }
}
