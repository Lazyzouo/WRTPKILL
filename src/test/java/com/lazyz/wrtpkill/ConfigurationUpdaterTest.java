package com.lazyz.wrtpkill;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationUpdaterTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void updatesSchemaAndCommentsWithoutReplacingUserSettings() throws IOException {
        Path config = temporaryDirectory.resolve("config.yml");
        Path baseline = temporaryDirectory.resolve(ConfigurationUpdater.BASELINE_FILE_NAME);
        String firstDefaults = """
                # Official header
                language: "zh_CN"
                updater:
                  enabled: true
                worlds:
                  nether:
                    world-name: "world_nether"
                """;
        Files.writeString(config, """
                language: "en_US"
                updater:
                  enabled: false
                worlds:
                  arena:
                    world-name: "practice"
                custom-value: 27
                """, StandardCharsets.UTF_8);

        ConfigurationUpdater.Result first = ConfigurationUpdater.update(
                config, baseline, firstDefaults);

        assertTrue(first.configRewritten());
        assertEquals(firstDefaults, Files.readString(baseline, StandardCharsets.UTF_8));
        YamlConfiguration firstResult = YamlConfiguration.loadConfiguration(config.toFile());
        assertEquals("en_US", firstResult.getString("language"));
        assertFalse(firstResult.getBoolean("updater.enabled"));
        assertEquals("practice", firstResult.getString("worlds.arena.world-name"));
        assertFalse(firstResult.contains("worlds.nether"));
        assertEquals(27, firstResult.getInt("custom-value"));
        assertTrue(Files.readString(config, StandardCharsets.UTF_8).contains("# Official header"));

        firstResult.set("updater", null);
        firstResult.save(config.toFile());
        String secondDefaults = """
                # Updated official header
                language: "zh_CN"
                updater:
                  enabled: true
                  auto-download: true
                new-feature:
                  enabled: true
                worlds:
                  nether:
                    world-name: "world_nether"
                """;

        ConfigurationUpdater.update(config, baseline, secondDefaults);

        YamlConfiguration secondResult = YamlConfiguration.loadConfiguration(config.toFile());
        assertEquals("en_US", secondResult.getString("language"));
        assertFalse(secondResult.contains("updater"));
        assertTrue(secondResult.getBoolean("new-feature.enabled"));
        assertEquals("practice", secondResult.getString("worlds.arena.world-name"));
        assertEquals(27, secondResult.getInt("custom-value"));
        assertTrue(Files.readString(config, StandardCharsets.UTF_8)
                .contains("# Updated official header"));
    }

    @Test
    void invalidYamlIsNeverOverwritten() throws IOException {
        Path config = temporaryDirectory.resolve("config.yml");
        Path baseline = temporaryDirectory.resolve(ConfigurationUpdater.BASELINE_FILE_NAME);
        String invalid = "language: [";
        Files.writeString(config, invalid, StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> ConfigurationUpdater.update(
                config, baseline, "language: \"zh_CN\"\n"));

        assertEquals(invalid, Files.readString(config, StandardCharsets.UTF_8));
        assertFalse(Files.exists(baseline));
    }
}
