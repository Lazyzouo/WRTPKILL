package com.lazyz.wrtpkill;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PosCommand implements CommandExecutor {
    private final WRTPKILL plugin;
    private final NamespacedKey noPosKey;

    public PosCommand(WRTPKILL plugin) {
        this.plugin = plugin;
        this.noPosKey = new NamespacedKey(plugin, "wrtp_nopos_hidden");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, plugin, "only_player");
            return true;
        }

        if (command.getName().equalsIgnoreCase("nopos")) {
            boolean isHidden = player.getPersistentDataContainer().has(noPosKey, PersistentDataType.BYTE);

            if (isHidden) {
                player.getPersistentDataContainer().remove(noPosKey);
                MessageUtils.send(player, plugin, "nopos_public_1");
                MessageUtils.send(player, plugin, "nopos_public_2");
            } else {
                player.getPersistentDataContainer().set(noPosKey, PersistentDataType.BYTE, (byte) 1);
                MessageUtils.send(player, plugin, "nopos_hidden_1");
                MessageUtils.send(player, plugin, "nopos_hidden_2");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("pos")) {
            sendMsg(player, "");
            sendMsg(player, divider());
            sendLocalized(player, "pos_title", "&#F8D34B&l✦ 全服位置总览 ✦");

            boolean isOp = player.isOp() || player.hasPermission("worldrtp.admin");

            Map<String, List<Player>> worldGroups = new LinkedHashMap<>();
            int count = 0;

            for (Player target : Bukkit.getOnlinePlayers()) {
                boolean isHidden = target.getPersistentDataContainer().has(noPosKey, PersistentDataType.BYTE);

                if (isHidden && !isOp) {
                    continue;
                }

                String worldName = target.getLocation().getWorld().getName();

                worldGroups.computeIfAbsent(worldName, k -> new ArrayList<>()).add(target);
                count++;
            }

            if (count == 0) {
                sendLocalized(player, "pos_none", "&#FF5E62&l✖ 暂无可显示的玩家位置");
            } else {
                for (Map.Entry<String, List<Player>> entry : worldGroups.entrySet()) {
                    String worldName = entry.getKey();
                    List<Player> playersInWorld = entry.getValue();

                    sendLocalized(player, "pos_world_header",
                            "&#FF1744&l▎ &#D0D7DE&l世界 {world} &#7D5BA6&l({count} 人)",
                            "world", gradient(worldName, 0xFF1744, 0xFF6B81),
                            "count", String.valueOf(playersInWorld.size()));

                    for (Player target : playersInWorld) {
                        boolean isHidden = target.getPersistentDataContainer().has(noPosKey, PersistentDataType.BYTE);
                        String hiddenTag = (isHidden && isOp)
                                ? MessageUtils.getString(plugin, "pos_hidden_tag", " &#FF5E62&l[已隐藏]") : "";
                        Location loc = target.getLocation();

                        String coordinates = String.format("X %d • Y %d • Z %d",
                                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                        sendLocalized(player, "pos_player_entry",
                                "&#20E3B2&l✦ &f&l{player}{hidden} &#4C6580&l┃ {coordinates}",
                                "player", target.getName(), "hidden", hiddenTag,
                                "coordinates", gradient(coordinates, 0x7CFF6B, 0x00C853));
                    }

                    sendMsg(player, divider());
                }
            }

            sendLocalized(player, "pos_total", "&#F8D34B&l✦ 共 {count} 名玩家 ✦",
                    "count", String.valueOf(count));
            sendMsg(player, divider());
            sendMsg(player, "");
            return true;
        }

        MessageUtils.send(player, plugin, "command_failed");
        return true;
    }

    private void sendMsg(Player player, String msg) {
        MessageUtils.sendRaw(player, msg);
    }

    private void sendLocalized(Player player, String path, String fallback, String... replacements) {
        MessageUtils.sendLeftAlignedRaw(player, MessageUtils.getString(plugin, path, fallback), replacements);
    }

    /** Matches the help-menu divider exactly, while keeping titles on their own non-wrapping line. */
    private String divider() {
        return "&b━━━━━━&3━━━━━━&9━━━━━━ &e✧ &9━━━━━━&3━━━━━━&b━━━━━━";
    }

    private String gradient(String text, int startRgb, int endRgb) {
        int[] codePoints = text.codePoints().toArray();
        if (codePoints.length == 0) return "";

        StringBuilder result = new StringBuilder();
        int steps = Math.max(1, codePoints.length - 1);
        for (int i = 0; i < codePoints.length; i++) {
            double ratio = (double) i / steps;
            int red = interpolate((startRgb >> 16) & 0xFF, (endRgb >> 16) & 0xFF, ratio);
            int green = interpolate((startRgb >> 8) & 0xFF, (endRgb >> 8) & 0xFF, ratio);
            int blue = interpolate(startRgb & 0xFF, endRgb & 0xFF, ratio);
            int rgb = (red << 16) | (green << 8) | blue;

            result.append(String.format("&#%06X&l", rgb));
            result.appendCodePoint(codePoints[i]);
        }
        return result.toString();
    }

    private int interpolate(int start, int end, double ratio) {
        return (int) Math.round(start + (end - start) * ratio);
    }
}
