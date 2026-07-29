package com.lazyz.wrtpkill;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WRTPKILL extends JavaPlugin {
    private final Map<String, DynamicRTPCommand> registeredCommands = new HashMap<>();
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Preserve server-specific values while adding newly introduced message defaults.
        getConfig().options().copyDefaults(true);
        migrateDefaultMessages();
        saveConfig();
        languageManager = new LanguageManager(this);

        AdminCommand adminCmd = new AdminCommand(this);
        if (getCommand("wrtp") != null) {
            getCommand("wrtp").setExecutor(adminCmd);
            getCommand("wrtp").setTabCompleter(adminCmd);
        }

        TPAExecutor tpaExecutor = new TPAExecutor(this);
        String[] tpaCmds = {"tpa", "tpaccept", "tpdeny", "tpcancel"};
        for (String cmd : tpaCmds) {
            if (getCommand(cmd) != null) {
                getCommand(cmd).setExecutor(tpaExecutor);
                getCommand(cmd).setTabCompleter(tpaExecutor);
            }
        }

        SuicideCommand suicideCommand = new SuicideCommand(this);
        if (getCommand("suicide") != null) {
            getCommand("suicide").setExecutor(suicideCommand);
        }

        PosCommand posCommand = new PosCommand(this);
        if (getCommand("pos") != null) {
            getCommand("pos").setExecutor(posCommand);
        }
        if (getCommand("nopos") != null) {
            getCommand("nopos").setExecutor(posCommand);
        }

        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        syncDynamicCommands();

        printStartupBanner();
        Bukkit.getAsyncScheduler().runNow(this, task -> new UpdateChecker(this).checkForUpdates());
    }

    @Override
    public void onDisable() {
        getLogger().info("WRTPKILL disabled / WRTPKILL 已卸载");
    }

    public void syncDynamicCommands() {
        ConfigurationSection section = getConfig().getConfigurationSection("worlds");
        if (section != null) {
            for (String cmdName : section.getKeys(false)) {
                if (!registeredCommands.containsKey(cmdName.toLowerCase())) {
                    DynamicRTPCommand cmd = new DynamicRTPCommand(cmdName.toLowerCase(), this);
                    Bukkit.getServer().getCommandMap().register(getDescription().getName().toLowerCase(), cmd);
                    registeredCommands.put(cmdName.toLowerCase(), cmd);
                }
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.updateCommands();
        }
    }

    public Location getTargetSpawnLocation() {
        String worldName = getConfig().getString("suicide-settings.respawn-world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        if (getConfig().getBoolean("custom-spawn.enabled", false)) {
            double x = getConfig().getDouble("custom-spawn.x", 0.5);
            double y = getConfig().getDouble("custom-spawn.y", 70.0);
            double z = getConfig().getDouble("custom-spawn.z", 0.5);
            float yaw = (float) getConfig().getDouble("custom-spawn.yaw", 0.0);
            float pitch = (float) getConfig().getDouble("custom-spawn.pitch", 0.0);
            return new Location(world, x, y, z, yaw, pitch);
        }
        return world.getSpawnLocation();
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    String getPluginJarName() {
        return getFile().getName();
    }

    public void reloadPluginConfiguration() {
        reloadConfig();
        languageManager.reload();
        syncDynamicCommands();
    }

    private void printStartupBanner() {
        String version = getDescription().getVersion();
        String language = languageManager.getLanguage();
        getLogger().info("============================================================");
        getLogger().info("  WRTPKILL v" + version + "  |  Folia / Paper 1.21.11");
        getLogger().info("  Author: Lazyz  |  Language: " + language);
        getLogger().info("  RTP + TPA + Position + Respawn Management");
        getLogger().info("  随机传送 + 玩家互传 + 坐标查询 + 复活管理");
        getLogger().info("  " + UpdateChecker.REPOSITORY_URL);
        getLogger().info("============================================================");
    }

    private void migrateDefaultMessages() {
        String path = "messages.death_respawned";
        String oldDefault = "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n"
                + "&c ☠ &c&l你已死亡并完成复活 &c☠\n"
                + "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━";
        String centeredDefault = "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━\n"
                + "           &c☠ &c&l你已死亡并完成复活 &c☠\n"
                + "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━";

        if (oldDefault.equals(getConfig().getString(path))) {
            getConfig().set(path, centeredDefault);
        }
    }
}
