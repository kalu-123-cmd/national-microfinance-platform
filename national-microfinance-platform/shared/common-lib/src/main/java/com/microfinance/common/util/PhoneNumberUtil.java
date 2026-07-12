package com.microfinance.common.util;

import java.util.regex.Pattern;

public final class PhoneNumberUtil {
    private static final Pattern ETH = Pattern.compile("^(\\+251|0)(9|7)\\d{8}$");
    private static final Pattern E164 = Pattern.compile("^\\+[1-9]\\d{6,14}$");
    private PhoneNumberUtil() {}

    public static boolean isValidEthiopian(String p) { return p != null && ETH.matcher(p.trim()).matches(); }
    public static boolean isValidE164(String p) { return p != null && E164.matcher(p.trim()).matches(); }
    public static String normalizeEthiopian(String p) {
        if (p == null) return null;
        String c = p.trim().replaceAll("\\s+", "");
        return c.startsWith("0") ? "+251" + c.substring(1) : c;
    }
    public static String mask(String p) {
        if (p == null || p.length() < 7) return "***";
        return p.substring(0, p.length() - 6) + "***" + p.substring(p.length() - 3);
    }
}
