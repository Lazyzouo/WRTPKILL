package com.lazyz.wrtpkill;

final class MessageLayout {
    private static final String DIVIDER_TEXT = "━━━━━━━━━━━━━━━━━━ ✧ ━━━━━━━━━━━━━━━━━━";
    private static final int DIVIDER_WIDTH = visibleLength(DIVIDER_TEXT);

    private MessageLayout() {
    }

    static String centerOnDivider(String legacyText) {
        if (legacyText == null || legacyText.isEmpty()) return legacyText;

        String content = stripVisibleLeadingWhitespace(legacyText);
        int contentWidth = visibleLength(content);
        if (contentWidth == 0) return "";

        int padding = Math.max(0, (DIVIDER_WIDTH - contentWidth) / 2);
        return " ".repeat(padding) + content;
    }

    static int visibleLength(String legacyText) {
        if (legacyText == null || legacyText.isEmpty()) return 0;

        int width = 0;
        for (int index = 0; index < legacyText.length();) {
            int formattingLength = formattingCodeLength(legacyText, index);
            if (formattingLength > 0) {
                index += formattingLength;
                continue;
            }

            int codePoint = legacyText.codePointAt(index);
            width++;
            index += Character.charCount(codePoint);
        }
        return width;
    }

    private static String stripVisibleLeadingWhitespace(String legacyText) {
        StringBuilder result = new StringBuilder(legacyText.length());
        boolean leading = true;

        for (int index = 0; index < legacyText.length();) {
            int formattingLength = formattingCodeLength(legacyText, index);
            if (formattingLength > 0) {
                result.append(legacyText, index, index + formattingLength);
                index += formattingLength;
                continue;
            }

            int codePoint = legacyText.codePointAt(index);
            index += Character.charCount(codePoint);
            if (leading && Character.isWhitespace(codePoint)) continue;

            leading = false;
            result.appendCodePoint(codePoint);
        }
        return result.toString();
    }

    private static int formattingCodeLength(String text, int index) {
        if (text.charAt(index) != '&' || index + 1 >= text.length()) return 0;

        char code = Character.toLowerCase(text.charAt(index + 1));
        if (code == '#' && index + 8 <= text.length()) {
            for (int hexIndex = index + 2; hexIndex < index + 8; hexIndex++) {
                if (Character.digit(text.charAt(hexIndex), 16) < 0) return 0;
            }
            return 8;
        }

        return "0123456789abcdefklmnorx".indexOf(code) >= 0 ? 2 : 0;
    }
}
