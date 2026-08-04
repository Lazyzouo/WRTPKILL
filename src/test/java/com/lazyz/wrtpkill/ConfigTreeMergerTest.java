package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTreeMergerTest {
    @Test
    void bootstrapAddsMissingDefaultsAndKeepsEveryUserValue() {
        Map<String, Object> defaults = mapOf(
                "language", "zh_CN",
                "updater", mapOf("enabled", true, "auto-download", true));
        Map<String, Object> user = mapOf(
                "language", "en_US",
                "updater", mapOf("enabled", false),
                "custom-value", 27);

        Map<String, Object> merged = ConfigTreeMerger.merge(defaults, user, null);

        assertEquals("en_US", merged.get("language"));
        assertEquals(27, merged.get("custom-value"));
        assertEquals(mapOf("enabled", false, "auto-download", true), merged.get("updater"));
    }

    @Test
    void laterUpdatesPreserveValuesDeletionsAndCustomWorlds() {
        Map<String, Object> oldDefaults = mapOf(
                "language", "zh_CN",
                "worlds", mapOf(
                        "nether", mapOf("world-name", "world_nether", "max-x", 200),
                        "overworld", mapOf("world-name", "world")));
        Map<String, Object> newDefaults = mapOf(
                "language", "en_US",
                "worlds", mapOf(
                        "nether", mapOf("world-name", "world_nether", "max-x", 500,
                                "use-border", false),
                        "overworld", mapOf("world-name", "world"),
                        "end", mapOf("world-name", "world_the_end")),
                "new-feature", mapOf("enabled", true));
        Map<String, Object> user = mapOf(
                "language", "zh_CN",
                "worlds", mapOf(
                        "nether", mapOf("world-name", "training_nether", "max-x", 200),
                        "arena", mapOf("world-name", "practice", "max-x", 80)));

        Map<String, Object> merged = ConfigTreeMerger.merge(newDefaults, user, oldDefaults);
        @SuppressWarnings("unchecked")
        Map<String, Object> worlds = (Map<String, Object>) merged.get("worlds");

        assertEquals("zh_CN", merged.get("language"));
        assertTrue(merged.containsKey("new-feature"));
        assertTrue(worlds.containsKey("nether"));
        assertTrue(worlds.containsKey("arena"));
        assertTrue(worlds.containsKey("end"));
        assertFalse(worlds.containsKey("overworld"));
        assertEquals(mapOf("world-name", "training_nether", "max-x", 200,
                "use-border", false), worlds.get("nether"));
    }

    @Test
    void userListsAndEmptySectionsRemainIndependentCopies() {
        List<String> whitelist = new java.util.ArrayList<>(List.of("PlayerOne"));
        Map<String, Object> user = mapOf("whitelist", whitelist, "worlds", Map.of());
        Map<String, Object> merged = ConfigTreeMerger.merge(user, user, user);

        whitelist.add("PlayerTwo");

        assertEquals(List.of("PlayerOne"), merged.get("whitelist"));
        assertEquals(Map.of(), merged.get("worlds"));
    }

    private static Map<String, Object> mapOf(Object... entries) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            result.put((String) entries[index], entries[index + 1]);
        }
        return result;
    }
}
