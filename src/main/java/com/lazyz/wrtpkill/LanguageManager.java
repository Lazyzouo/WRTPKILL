package com.lazyz.wrtpkill;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public final class LanguageManager {
    public static final String DEFAULT_LANGUAGE = "zh_CN";
    private static final String ENGLISH_LANGUAGE = "en_US";
    private static final Map<String, String> LEGACY_ENGLISH_PANELS = Map.of(
            "merged_offline_notice", "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n&e * &e&lOffline cleanup and access update &e*\n  &8- &7Reason: &cOffline duration exceeded\n  &8- &7Penalty: &cInventory and Ender Chest cleared; returned to spawn\n  &8- &7Benefit: &aRTP and TPA restrictions were removed\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━",
            "unlock_death_merged", "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n           &c☠ &c&lYou died and respawned at spawn &c☠\n  &8- &7Your RTP and TPA restrictions were removed.\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"
    );
    private static final String ENGLISH_RESOURCE = "lang/" + ENGLISH_LANGUAGE + ".yml";


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
            applyBundledEnglishPanelMigrations();
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

    private void applyBundledEnglishPanelMigrations() {
        InputStream resource = plugin.getResource(ENGLISH_RESOURCE);
        if (resource == null) return;

        try (resource;
             InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            FileConfiguration bundledMessages = YamlConfiguration.loadConfiguration(reader);
            for (Map.Entry<String, String> entry : LEGACY_ENGLISH_PANELS.entrySet()) {
                String path = "messages." + entry.getKey();
                if (!entry.getValue().equals(selectedMessages.getString(path))) continue;

                Object replacement = bundledMessages.get(path);
                if (replacement != null) selectedMessages.set(path, replacement);
            }
        } catch (IOException exception) {
            plugin.logConsole("&eCould not load bundled English panel layouts: "
                    + exception.getMessage());
        }
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
