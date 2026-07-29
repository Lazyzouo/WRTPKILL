package com.lazyz.wrtpkill;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final WRTPKILL plugin;
    private final Map<UUID, Long> confirmSetSpawn = new ConcurrentHashMap<>();

    public AdminCommand(WRTPKILL plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            String name = plugin.getDescription().getName();
            String version = plugin.getDescription().getVersion();
            List<String> authors = plugin.getDescription().getAuthors();
            String author = authors.isEmpty() ? "Unknown" : authors.get(0);

            if (sender.hasPermission("worldrtp.admin")) {
                MessageUtils.send(sender, plugin, "help_menu_admin", "name", name, "version", version, "author", author);
            } else {
                MessageUtils.send(sender, plugin, "help_menu_player", "name", name, "version", version, "author", author);
            }
            sendDynamicRtpHelp(sender);
            MessageUtils.send(sender, plugin, "help_menu_footer");
            return true;
        }

        if (args[0].equalsIgnoreCase("whitelist")) {
            if (!sender.isOp()) {
                MessageUtils.send(sender, plugin, "no_permission");
                return true;
            }

            if (args.length != 3 || !args[1].equalsIgnoreCase("add")) {
                MessageUtils.send(sender, plugin, "whitelist_add_usage");
                return true;
            }

            String playerName = args[2];
            List<String> whitelist = new ArrayList<>(plugin.getConfig().getStringList("whitelist"));
            for (String existingName : whitelist) {
                if (existingName.equalsIgnoreCase(playerName)) {
                    MessageUtils.send(sender, plugin, "whitelist_add_exists", "player", existingName);
                    return true;
                }
            }

            whitelist.add(playerName);
            plugin.getConfig().set("whitelist", whitelist);
            plugin.saveConfig();
            MessageUtils.send(sender, plugin, "whitelist_add_success", "player", playerName);
            return true;
        }

        if (!sender.hasPermission("worldrtp.admin")) {
            MessageUtils.send(sender, plugin, "no_permission");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfiguration();
            MessageUtils.send(sender, plugin, "reload_success");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player player)) {
                MessageUtils.send(sender, plugin, "not_player");
                return true;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
                if (confirmSetSpawn.containsKey(player.getUniqueId()) && System.currentTimeMillis() - confirmSetSpawn.get(player.getUniqueId()) < 10000L) {
                    confirmSetSpawn.remove(player.getUniqueId());

                    Location loc = player.getLocation();
                    plugin.getConfig().set("custom-spawn.enabled", true);
                    plugin.getConfig().set("custom-spawn.x", loc.getX());
                    plugin.getConfig().set("custom-spawn.y", loc.getY());
                    plugin.getConfig().set("custom-spawn.z", loc.getZ());
                    plugin.getConfig().set("custom-spawn.yaw", (double) loc.getYaw());
                    plugin.getConfig().set("custom-spawn.pitch", (double) loc.getPitch());
                    plugin.getConfig().set("suicide-settings.respawn-world", loc.getWorld().getName());
                    plugin.saveConfig();

                    MessageUtils.send(player, plugin, "spawn_set_success");
                } else {
                    MessageUtils.send(player, plugin, "spawn_set_expired");
                }
                return true;
            }

            confirmSetSpawn.put(player.getUniqueId(), System.currentTimeMillis());
            MessageUtils.send(player, plugin, "spawn_set_confirm");
            return true;
        }

        if (args[0].equalsIgnoreCase("add") && args.length == 3) {
            String cmdName = args[1].toLowerCase();
            String worldName = args[2];

            plugin.getConfig().set("worlds." + cmdName + ".world-name", worldName);
            plugin.getConfig().set("worlds." + cmdName + ".use-border", true);
            plugin.getConfig().set("worlds." + cmdName + ".min-x", -5000);
            plugin.getConfig().set("worlds." + cmdName + ".max-x", 5000);
            plugin.getConfig().set("worlds." + cmdName + ".min-z", -5000);
            plugin.getConfig().set("worlds." + cmdName + ".max-z", 5000);
            plugin.saveConfig();

            plugin.syncDynamicCommands();
            MessageUtils.send(sender, plugin, "add_success", "cmd", cmdName, "world", worldName);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
            String cmdName = args[1].toLowerCase();
            if (plugin.getConfig().contains("worlds." + cmdName)) {
                plugin.getConfig().set("worlds." + cmdName, null);
                plugin.saveConfig();
                MessageUtils.send(sender, plugin, "remove_success", "cmd", cmdName);
            } else {
                MessageUtils.send(sender, plugin, "remove_fail");
            }
            return true;
        }

        MessageUtils.send(sender, plugin, "wrong_usage");
        return true;
    }

    /** Lists every configured RTP command so help stays correct after /wrtp add or reload. */
    private void sendDynamicRtpHelp(CommandSender sender) {
        ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
        if (worlds == null || worlds.getKeys(false).isEmpty()) return;

        MessageUtils.sendRaw(sender, MessageUtils.getString(plugin, "dynamic_rtp_help_header",
                "&d ► &d&l随机传送指令 &d◄"));
        for (String commandName : new TreeSet<>(worlds.getKeys(false))) {
            String worldName = worlds.getString(commandName + ".world-name", commandName);
            MessageUtils.sendRaw(sender, MessageUtils.getString(plugin, "dynamic_rtp_help_entry",
                            "  &f/{command} &8┃ &d✦ &7随机传送至 &f&l{world}"),
                    "command", commandName, "world", worldName);
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subCmds = new ArrayList<>(List.of("help"));
            if (sender.hasPermission("worldrtp.admin")) {
                subCmds.addAll(Arrays.asList("add", "remove", "reload", "setspawn"));
            }
            if (sender.isOp()) subCmds.add("whitelist");
            for (String sub : subCmds) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("whitelist") && sender.isOp()) {
            if ("add".startsWith(args[1].toLowerCase())) {
                completions.add("add");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("whitelist")
                && args[1].equalsIgnoreCase("add") && sender.isOp()) {
            List<String> whitelist = plugin.getConfig().getStringList("whitelist");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                boolean alreadyWhitelisted = whitelist.stream()
                        .anyMatch(name -> name.equalsIgnoreCase(onlinePlayer.getName()));
                if (!alreadyWhitelisted && onlinePlayer.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(onlinePlayer.getName());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setspawn")) {
            if ("confirm".startsWith(args[1].toLowerCase())) {
                completions.add("confirm");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("worlds");
            if (section != null) {
                for (String cmdName : section.getKeys(false)) {
                    if (cmdName.startsWith(args[1].toLowerCase())) {
                        completions.add(cmdName);
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(world.getName());
                }
            }
        }
        return completions;
    }
}
