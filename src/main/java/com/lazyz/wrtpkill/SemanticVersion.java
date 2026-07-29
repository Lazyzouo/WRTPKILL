package com.lazyz.wrtpkill;

final class SemanticVersion {
    private SemanticVersion() {
    }

    static int compare(String left, String right) {
        int[] leftParts = parse(left);
        int[] rightParts = parse(right);
        for (int index = 0; index < Math.max(leftParts.length, rightParts.length); index++) {
            int leftPart = index < leftParts.length ? leftParts[index] : 0;
            int rightPart = index < rightParts.length ? rightParts[index] : 0;
            if (leftPart != rightPart) return Integer.compare(leftPart, rightPart);
        }
        return 0;
    }

    private static int[] parse(String version) {
        String core = clean(version).split("[-+]", 2)[0];
        String[] parts = core.split("\\.");
        int[] parsed = new int[parts.length];
        for (int index = 0; index < parts.length; index++) {
            try {
                parsed[index] = Integer.parseInt(parts[index].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ignored) {
                parsed[index] = 0;
            }
        }
        return parsed;
    }

    private static String clean(String version) {
        if (version == null) return "0.0.0";
        return version.trim().replaceFirst("^[vV]", "");
    }
}
