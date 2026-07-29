package com.lazyz.wrtpkill;

import java.util.regex.Pattern;

final class StartupBannerLayout {
    static final int INNER_WIDTH = 76;
    private static final String BORDER_STYLE = "&3&l";
    private static final Pattern FORMATTING_CODE =
            Pattern.compile("(?i)&(?:#[0-9a-f]{6}|[0-9a-fk-orx])");

    private StartupBannerLayout() {
    }

    static String border() {
        return BORDER_STYLE + "+" + "=".repeat(INNER_WIDTH) + "+";
    }
    static String sectionDivider() {
        return BORDER_STYLE + "+" + "-".repeat(INNER_WIDTH) + "+";
    }


    static String line(String content) {
        int rightPadding = Math.max(1,
                INNER_WIDTH - visibleWidth(content) - 1);
        return BORDER_STYLE + "| &r" + content + "&r"
                + " ".repeat(rightPadding) + BORDER_STYLE + "|";
    }

    static String centeredLine(String content) {
        int availablePadding = Math.max(0,
                INNER_WIDTH - visibleWidth(content));
        int leftPadding = availablePadding / 2;
        int rightPadding = availablePadding - leftPadding;
        return BORDER_STYLE + "|" + " ".repeat(leftPadding) + content + "&r"
                + " ".repeat(rightPadding) + BORDER_STYLE + "|";
    }

    static int visibleWidth(String content) {
        String unformatted = FORMATTING_CODE.matcher(content).replaceAll("");
        return unformatted.codePoints()
                .map(codePoint -> isWide(codePoint) ? 2 : 1)
                .sum();
    }

    private static boolean isWide(int codePoint) {
        Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
        return script == Character.UnicodeScript.HAN
                || script == Character.UnicodeScript.HANGUL
                || script == Character.UnicodeScript.HIRAGANA
                || script == Character.UnicodeScript.KATAKANA
                || codePoint >= 0xFF01 && codePoint <= 0xFF60
                || codePoint >= 0xFFE0 && codePoint <= 0xFFE6;
    }
}
