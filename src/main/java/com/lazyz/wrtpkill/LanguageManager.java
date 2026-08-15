package com.lazyz.wrtpkill;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class LanguageManager {
    public static final String DEFAULT_LANGUAGE = "zh_CN";
    private static final String ENGLISH_LANGUAGE = "en_US";
    private static final Map<String, List<String>> LEGACY_ENGLISH_MESSAGES = Map.of(
            "merged_offline_notice", List.of("&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n&e * &e&lOffline cleanup and access update &e*\n  &8- &7Reason: &cOffline duration exceeded\n  &8- &7Penalty: &cInventory and Ender Chest cleared; returned to spawn\n  &8- &7Benefit: &aRTP and TPA restrictions were removed\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"),
            "unlock_death_merged", List.of("&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n           &c☠ &c&lYou died and respawned at spawn &c☠\n  &8- &7Your RTP and TPA restrictions were removed.\n&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━"),
            "tpa_success", List.of(
                    "&8[&aOK&8] &aTeleported to &e{target}&a.",
                    "&8[&aOK&8] &aTeleported to a safe point at least 32 blocks from &e{target}&a."),
            "tpa_accepted_sender", List.of(
                    "&8[&aOK&8] &e{target} &aaccepted your request. Teleporting...")
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
            applyBundledEnglishMigrations();
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

    private void applyBundledEnglishMigrations() {
        InputStream resource = plugin.getResource(ENGLISH_RESOURCE);
        if (resource == null) return;

        try (resource;
             InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            FileConfiguration bundledMessages = YamlConfiguration.loadConfiguration(reader);
            for (Map.Entry<String, List<String>> entry : LEGACY_ENGLISH_MESSAGES.entrySet()) {
                String path = "messages." + entry.getKey();
                if (!entry.getValue().contains(selectedMessages.getString(path))) continue;

                Object replacement = bundledMessages.get(path);
                if (replacement != null) selectedMessages.set(path, replacement);
            }
        } catch (IOException exception) {
            plugin.logConsole("&eCould not load bundled English message migrations: "
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
