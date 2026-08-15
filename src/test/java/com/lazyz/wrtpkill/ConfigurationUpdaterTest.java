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

        ConfigurationUpdater.Result first = ConfigurationUpdater.update(config, firstDefaults);

        assertFalse(first.configRewritten());
        YamlConfiguration firstResult = YamlConfiguration.loadConfiguration(config.toFile());
        assertEquals("en_US", firstResult.getString("language"));
        assertFalse(firstResult.getBoolean("updater.enabled"));
        assertEquals("practice", firstResult.getString("worlds.arena.world-name"));
        assertFalse(firstResult.contains("worlds.nether"));
        assertEquals(27, firstResult.getInt("custom-value"));
        assertFalse(Files.readString(config, StandardCharsets.UTF_8).contains("# Official header"));

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

        ConfigurationUpdater.update(config, secondDefaults);

        YamlConfiguration secondResult = YamlConfiguration.loadConfiguration(config.toFile());
        assertEquals("en_US", secondResult.getString("language"));
        assertTrue(secondResult.getBoolean("updater.enabled"));
        assertTrue(secondResult.getBoolean("updater.auto-download"));
        assertTrue(secondResult.getBoolean("new-feature.enabled"));
        assertEquals("practice", secondResult.getString("worlds.arena.world-name"));
        assertEquals(27, secondResult.getInt("custom-value"));
        assertTrue(Files.readString(config, StandardCharsets.UTF_8)
                .contains("# Updated official header"));
    }

    @Test
    void createsKitloaderStyleBackupAndAdvancesSchemaMetadata() throws IOException {
        Path config = temporaryDirectory.resolve("config.yml");
        Files.writeString(config, "config-version: 1\nfeature:\n  enabled: false\n",
                StandardCharsets.UTF_8);

        ConfigurationUpdater.Result result = ConfigurationUpdater.update(
                config, "# Official\nconfig-version: 2\nfeature:\n  enabled: true\n");

        assertTrue(result.configRewritten());
        assertTrue(result.backupPath() != null);
        assertTrue(Files.isRegularFile(result.backupPath()));
        assertTrue(result.backupPath().getFileName().toString().startsWith("config-v1-to-v2-"));
        YamlConfiguration updated = YamlConfiguration.loadConfiguration(config.toFile());
        assertEquals(2, updated.getInt("config-version"));
        assertFalse(updated.getBoolean("feature.enabled"));
        assertEquals("config-version: 1\nfeature:\n  enabled: false\n",
                Files.readString(result.backupPath(), StandardCharsets.UTF_8));
    }

    @Test
    void migratesTheOldOfficialTpaRadiusMessageToTheDynamicPlaceholder() throws IOException {
        Path config = temporaryDirectory.resolve("config.yml");
        Files.writeString(config, """
                config-version: 1
                messages:
                  tpa_success: "&8[&a✔&8] &a已传送至距离 &e{target} &a至少 32 格的安全位置！"
                """, StandardCharsets.UTF_8);

        ConfigurationUpdater.update(config, """
                config-version: 2
                tpa-safe-radius: 32
                messages:
                  tpa_success: "&8[&a✔&8] &a已传送至距离 &e{target} &a至少 {radius} 格的安全位置！"
                """);

        YamlConfiguration updated = YamlConfiguration.loadConfiguration(config.toFile());
        assertEquals(2, updated.getInt("config-version"));
        assertEquals(32, updated.getInt("tpa-safe-radius"));
        assertTrue(updated.getString("messages.tpa_success", "").contains("{radius}"));
    }

    @Test
    void refusesToDowngradeAConfigurationWithANewerSchema() throws IOException {
        Path config = temporaryDirectory.resolve("config.yml");
        String newer = "config-version: 3\nfeature: true\n";
        Files.writeString(config, newer, StandardCharsets.UTF_8);

        IOException exception = assertThrows(IOException.class, () -> ConfigurationUpdater.update(
                config, "config-version: 2\nfeature: false\n"));

        assertTrue(exception.getMessage().contains("Refusing to downgrade"));
        assertEquals(newer, Files.readString(config, StandardCharsets.UTF_8));
        assertFalse(Files.exists(temporaryDirectory.resolve("config-backups")));
    }

    @Test
    void invalidYamlIsNeverOverwritten() throws IOException {
        Path config = temporaryDirectory.resolve("config.yml");
        String invalid = "language: [";
        Files.writeString(config, invalid, StandardCharsets.UTF_8);

        assertThrows(IOException.class, () -> ConfigurationUpdater.update(
                config, "language: \"zh_CN\"\n"));

        assertEquals(invalid, Files.readString(config, StandardCharsets.UTF_8));
    }

    @Test
    void removesTheLegacyDefaultBaselineFile() throws IOException {
        Path baseline = temporaryDirectory.resolve(
                ConfigurationUpdater.LEGACY_BASELINE_FILE_NAME);
        Files.writeString(baseline, "official: defaults\n", StandardCharsets.UTF_8);

        assertTrue(ConfigurationUpdater.removeLegacyBaseline(temporaryDirectory));
        assertFalse(Files.exists(baseline));
        assertFalse(ConfigurationUpdater.removeLegacyBaseline(temporaryDirectory));
    }
}
