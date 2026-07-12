package com.microfinance.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtil {
    public static final String CURRENCY = "ETB";
    public static final int SCALE = 2;
    public static final RoundingMode RM = RoundingMode.HALF_UP;

    private MoneyUtil() {}

    public static BigDecimal of(double v) { return BigDecimal.valueOf(v).setScale(SCALE, RM); }
    public static BigDecimal add(BigDecimal a, BigDecimal b) { return a.add(b).setScale(SCALE, RM); }
    public static BigDecimal sub(BigDecimal a, BigDecimal b) { return a.subtract(b).setScale(SCALE, RM); }
    public static BigDecimal mul(BigDecimal a, BigDecimal f) { return a.multiply(f).setScale(SCALE, RM); }
    public static BigDecimal div(BigDecimal a, BigDecimal d) { return a.divide(d, SCALE, RM); }
    public static boolean isPos(BigDecimal a) { return a != null && a.compareTo(BigDecimal.ZERO) > 0; }
    public static boolean isNonNeg(BigDecimal a) { return a != null && a.compareTo(BigDecimal.ZERO) >= 0; }

    public static BigDecimal simpleInterest(BigDecimal principal, BigDecimal annualPct, int months) {
        BigDecimal monthlyRate = annualPct.divide(BigDecimal.valueOf(1200), 10, RM);
        return principal.multiply(monthlyRate).multiply(BigDecimal.valueOf(months)).setScale(SCALE, RM);
    }

    public static BigDecimal emiAmount(BigDecimal principal, BigDecimal annualPct, int months) {
        if (annualPct.compareTo(BigDecimal.ZERO) == 0) return div(principal, BigDecimal.valueOf(months));
        BigDecimal r = annualPct.divide(BigDecimal.valueOf(1200), 10, RM);
        BigDecimal onePlusR = BigDecimal.ONE.add(r);
        BigDecimal factor = onePlusR.pow(months);
        return principal.multiply(r).multiply(factor)
                .divide(factor.subtract(BigDecimal.ONE), SCALE, RM);
    }

    public static String fmt(BigDecimal a) { return CURRENCY + " " + a.setScale(SCALE, RM).toPlainString(); }
}
