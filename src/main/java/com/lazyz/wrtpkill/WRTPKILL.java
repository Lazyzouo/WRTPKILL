package com.lazyz.wrtpkill;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WRTPKILL extends JavaPlugin {
    private final Map<String, DynamicRTPCommand> registeredCommands = new HashMap<>();
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigurationUpdater.Result configurationUpdate = null;
        try {
            configurationUpdate = ConfigurationUpdater.update(this);
        } catch (IOException exception) {
            logConsole("&cConfiguration update failed; the existing file was kept: &f"
                    + exception.getMessage()
                    + " &8/ &c配置更新失败，原文件已保留。");
        }
        reloadConfig();
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
        if (configurationUpdate != null && configurationUpdate.configRewritten()) {
            logConsole("&aConfiguration updated; existing server values were preserved. "
                    + "&8/ &a配置已自动更新，现有服务器参数已保留。");
        }
        if (configurationUpdate != null && configurationUpdate.configRewritten()
                && configurationUpdate.backupPath() != null) {
            logConsole("&7Configuration backup: &f"
                    + configurationUpdate.backupPath().getFileName());
        }
        if (configurationUpdate != null && configurationUpdate.legacyBaselineRemoved()) {
            logConsole("&aRemoved legacy default baseline; config.yml is now the only active configuration. "
                    + "&8/ &a已移除旧默认值基线，当前仅使用 config.yml。");
        }
        Bukkit.getAsyncScheduler().runNow(this, task -> new UpdateChecker(this).checkForUpdates());
    }

    @Override
    public void onDisable() {
        logConsole("&cWRTPKILL disabled / WRTPKILL 已卸载");
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
        logConsole(StartupBannerLayout.border());
        logConsole(StartupBannerLayout.centeredLine(
                "&b&lWRTPKILL TELEPORT MANAGEMENT v" + version));
        logConsole(StartupBannerLayout.centeredLine(
                "&f&lTELEPORT & RESPAWN CONTROL &8/ &f&l传送与复活管理"));
        logConsole(StartupBannerLayout.sectionDivider());
        logConsole(StartupBannerLayout.line("&fVersion / 版本 &8: &a" + version));
        logConsole(StartupBannerLayout.line("&fAuthor  / 作者 &8: &eLazyz"));
        logConsole(StartupBannerLayout.line(
                "&fTested  / 测试 &8: &aPaper & Folia 1.21.11"));
        logConsole(StartupBannerLayout.line("&fLanguage/ 语言 &8: &b" + language));
        logConsole(StartupBannerLayout.line(
                "&fGitHub         &8: &9" + UpdateChecker.REPOSITORY_URL));
        logConsole(StartupBannerLayout.line(
                "&aOpen source. &fNo telemetry or server-data upload."));
        logConsole(StartupBannerLayout.border());
        String enabled = languageManager.isEnglish()
                ? "&a&l» WRTPKILL v" + version + " by Lazyz started successfully on Paper/Folia."
                : "&a&l» WRTPKILL v" + version + " by Lazyz 已在 Paper/Folia 核心上成功启动！";
        logConsole(enabled);
    }

    void logConsole(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes(
                '&', "&8[&bWRTPKILL&8] &r" + message));
    }

}
