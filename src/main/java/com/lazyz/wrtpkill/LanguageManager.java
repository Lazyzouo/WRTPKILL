package com.lazyz.wrtpkill;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Locale;

public final class LanguageManager {
    public static final String DEFAULT_LANGUAGE = "zh_CN";
    private static final String ENGLISH_LANGUAGE = "en_US";

    private final WRTPKILL plugin;
    private FileConfiguration selectedMessages;
    private String language;

    public LanguageManager(WRTPKILL plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        language = normalize(plugin.getConfig().getString("language", DEFAULT_LANGUAGE));
        ensureLanguageFile(ENGLISH_LANGUAGE);

        if (ENGLISH_LANGUAGE.equals(language)) {
            selectedMessages = YamlConfiguration.loadConfiguration(
                    new File(plugin.getDataFolder(), "lang/" + ENGLISH_LANGUAGE + ".yml"));
        } else {
            selectedMessages = plugin.getConfig();
        }
    }

    public Object get(String path) {
        Object value = selectedMessages.get("messages." + path);
        if (value != null) return value;
        return plugin.getConfig().get("messages." + path);
    }

    public String getString(String path, String fallback) {
        Object value = get(path);
        return value instanceof String text ? text : fallback;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isEnglish() {
        return ENGLISH_LANGUAGE.equals(language);
    }

    private void ensureLanguageFile(String locale) {
        File languageFile = new File(plugin.getDataFolder(), "lang/" + locale + ".yml");
        if (!languageFile.exists()) {
            plugin.saveResource("lang/" + locale + ".yml", false);
        }
    }

    private String normalize(String configured) {
        if (configured == null) return DEFAULT_LANGUAGE;
        String normalized = configured.trim().replace('-', '_').toLowerCase(Locale.ROOT);
        return normalized.equals("en_us") || normalized.equals("en") ? ENGLISH_LANGUAGE : DEFAULT_LANGUAGE;
    }
}
