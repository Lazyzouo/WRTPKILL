package com.lazyz.wrtpkill;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class ConfigurationUpdater {
    static final String BASELINE_FILE_NAME = ".wrtpkill-default-config.yml";
    static final String CONFIG_VERSION_PATH = "config-version";
    static final int CURRENT_CONFIG_VERSION = 1;
    private static final String CONFIG_RESOURCE = "config.yml";
    private static final DateTimeFormatter BACKUP_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS", Locale.ROOT);

    private ConfigurationUpdater() {
    }

    static Result update(JavaPlugin plugin) throws IOException {
        Path dataDirectory = plugin.getDataFolder().toPath();
        Path configFile = dataDirectory.resolve(CONFIG_RESOURCE);
        Path baselineFile = dataDirectory.resolve(BASELINE_FILE_NAME);
        String officialText = readOfficialConfig(plugin);
        return update(configFile, baselineFile, officialText);
    }

    static Result update(Path configFile, Path baselineFile, String officialText)
            throws IOException {
        String userText = Files.readString(configFile, StandardCharsets.UTF_8);

        YamlConfiguration officialConfig = loadYaml(officialText, "bundled config.yml");
        YamlConfiguration userConfig = loadYaml(userText, configFile.toString());
        int bundledConfigVersion = readConfigVersion(officialConfig, "bundled config.yml");
        if (bundledConfigVersion > 0 && bundledConfigVersion != CURRENT_CONFIG_VERSION) {
            throw new IOException("Bundled config.yml declares schema v" + bundledConfigVersion
                    + " but this plugin requires v" + CURRENT_CONFIG_VERSION + ".");
        }
        int userConfigVersion = readConfigVersion(userConfig, configFile.toString());
        if (bundledConfigVersion > 0 && userConfigVersion > bundledConfigVersion) {
            throw new IOException("Server config.yml uses newer schema v" + userConfigVersion
                    + "; this plugin supports up to v" + bundledConfigVersion
                    + ". Refusing to downgrade it.");
        }
        Map<String, Object> previousDefaults = null;
        String baselineText = null;
        if (Files.isRegularFile(baselineFile)) {
            baselineText = Files.readString(baselineFile, StandardCharsets.UTF_8);
            try {
                previousDefaults = toMap(loadYaml(baselineText, baselineFile.toString()));
            } catch (IOException ignored) {
                // A damaged internal baseline is safely rebuilt from the bundled defaults.
                baselineText = null;
            }
        }

        Map<String, Object> userValues = toMap(userConfig);
        Map<String, Object> officialValues = toMap(officialConfig);
        Map<String, Object> mergedValues = ConfigTreeMerger.merge(
                officialValues, userValues, previousDefaults);
        if (bundledConfigVersion > 0) {
            // Schema metadata is owned by the plugin, while all functional values remain user-owned.
            mergedValues.put(CONFIG_VERSION_PATH, bundledConfigVersion);
        }
        boolean defaultsChanged = baselineText == null || !officialText.equals(baselineText);
        boolean valuesChanged = !mergedValues.equals(userValues);
        boolean exactFreshConfig = baselineText == null && officialText.equals(userText);
        boolean rewriteConfig = valuesChanged || (defaultsChanged && !exactFreshConfig);
        Path backupPath = null;

        if (rewriteConfig) {
            backupPath = createBackup(configFile, userConfigVersion, bundledConfigVersion);
            synchronizeSection(officialConfig, mergedValues);
            copyUserComments(userConfig, officialConfig);
            writeAtomically(configFile, officialConfig.saveToString());
        }
        if (defaultsChanged) writeAtomically(baselineFile, officialText);

        return new Result(rewriteConfig, defaultsChanged, backupPath);
    }

    private static String readOfficialConfig(JavaPlugin plugin) throws IOException {
        try (InputStream input = plugin.getResource(CONFIG_RESOURCE)) {
            if (input == null) throw new IOException("Bundled config.yml is missing");
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static YamlConfiguration loadYaml(String content, String source) throws IOException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.options().parseComments(true);
        try {
            configuration.loadFromString(content);
            return configuration;
        } catch (InvalidConfigurationException exception) {
            throw new IOException("Invalid YAML in " + source, exception);
        }
    }

    private static Map<String, Object> toMap(ConfigurationSection section) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection child) {
                result.put(key, toMap(child));
            } else {
                result.put(key, normalize(value));
            }
        }
        return result;
    }

    private static Object normalize(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(String.valueOf(entry.getKey()), normalize(entry.getValue()));
            }
            return result;
        }
        if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>(list.size());
            for (Object element : list) result.add(normalize(element));
            return result;
        }
        return value;
    }

    private static int readConfigVersion(ConfigurationSection config, String source) throws IOException {
        Object value = config.get(CONFIG_VERSION_PATH);
        if (value == null) return 0;

        int parsed;
        if (value instanceof Number number) {
            double numericValue = number.doubleValue();
            parsed = number.intValue();
            if (numericValue != parsed) {
                throw new IOException(source + " has a non-integer '" + CONFIG_VERSION_PATH + "' value.");
            }
        } else if (value instanceof String text) {
            try {
                parsed = Integer.parseInt(text.trim());
            } catch (NumberFormatException exception) {
                throw new IOException(source + " has an invalid '" + CONFIG_VERSION_PATH + "' value.", exception);
            }
        } else {
            throw new IOException(source + " has an invalid '" + CONFIG_VERSION_PATH + "' value type.");
        }

        if (parsed < 0) {
            throw new IOException(source + " has a negative '" + CONFIG_VERSION_PATH + "' value.");
        }
        return parsed;
    }

    private static Path createBackup(Path configFile, int previousVersion, int currentVersion)
            throws IOException {
        if (!Files.isRegularFile(configFile)) return null;

        Path backupDirectory = configFile.getParent().resolve("config-backups");
        Files.createDirectories(backupDirectory);
        String timestamp = BACKUP_TIMESTAMP.format(LocalDateTime.now());
        String prefix = "config-v" + previousVersion + "-to-v" + currentVersion + "-" + timestamp;
        for (int attempt = 0; ; attempt++) {
            String suffix = attempt == 0 ? "" : "-" + attempt;
            Path candidate = backupDirectory.resolve(prefix + suffix + ".yml");
            try {
                return Files.copy(configFile, candidate, StandardCopyOption.COPY_ATTRIBUTES);
            } catch (java.nio.file.FileAlreadyExistsException ignored) {
                // Two restarts can occur in the same millisecond; keep both backups.
            }
        }
    }

    private static void synchronizeSection(
            ConfigurationSection section,
            Map<String, Object> desiredValues) {
        for (String existingKey : List.copyOf(section.getKeys(false))) {
            if (!desiredValues.containsKey(existingKey)) section.set(existingKey, null);
        }

        for (Map.Entry<String, Object> entry : desiredValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> map) {
                ConfigurationSection child = section.getConfigurationSection(key);
                if (child == null) {
                    section.set(key, null);
                    child = section.createSection(key);
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> childValues = (Map<String, Object>) map;
                synchronizeSection(child, childValues);
            } else {
                section.set(key, ConfigTreeMerger.deepCopy(value));
            }
        }
    }

    private static void copyUserComments(
            YamlConfiguration userConfig,
            YamlConfiguration mergedConfig) {
        for (String path : userConfig.getKeys(true)) {
            if (!mergedConfig.contains(path)) continue;
            List<String> comments = userConfig.getComments(path);
            if (mergedConfig.getComments(path).isEmpty() && !comments.isEmpty()) {
                mergedConfig.setComments(path, comments);
            }
            List<String> inlineComments = userConfig.getInlineComments(path);
            if (mergedConfig.getInlineComments(path).isEmpty() && !inlineComments.isEmpty()) {
                mergedConfig.setInlineComments(path, inlineComments);
            }
        }
    }

    private static void writeAtomically(Path target, String content) throws IOException {
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(
                target.getParent(), target.getFileName().toString() + ".", ".tmp");
        try {
            Files.writeString(temporary, content, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    record Result(boolean configRewritten, boolean baselineUpdated, Path backupPath) {
    }
}
