package com.lazyz.wrtpkill;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final WRTPKILL plugin;

    public ReloadCommand(WRTPKILL plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.reloadPluginConfiguration();

        MessageUtils.send(sender, plugin, "reload_success");
        return true;
    }
}
