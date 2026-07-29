package com.lazyz.wrtpkill;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TPAExecutor implements CommandExecutor, TabCompleter {

    private final WRTPKILL plugin;
    private final NamespacedKey lockKey;
    private final NamespacedKey teleportPendingKey;
    private final Map<UUID, TPARequest> pendingRequests = new ConcurrentHashMap<>();

    private static class TPARequest {
        UUID sender;
        long timestamp;
        public TPARequest(UUID sender, long timestamp) {
            this.sender = sender;
            this.timestamp = timestamp;
        }
    }

    public TPAExecutor(WRTPKILL plugin) {
        this.plugin = plugin;
        this.lockKey = new NamespacedKey(plugin, "rtp_locked");
        this.teleportPendingKey = new NamespacedKey(plugin, "teleport_pending");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, plugin, "only_player");
            return true;
        }

        if (!plugin.getConfig().getBoolean("tpa-enabled", true)) {
            MessageUtils.send(player, plugin, "tpa_disabled");
            return true;
        }

        String cmdName = command.getName().toLowerCase();

        if (cmdName.equals("tpa")) {
            if (args.length == 0) {
                MessageUtils.send(player, plugin, "tpa_usage");
                return true;
            }

            if (isLocked(player)) {
                MessageUtils.send(player, plugin, "rtp_locked");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                MessageUtils.send(player, plugin, "tpa_player_offline");
                return true;
            }

            if (target.equals(player)) {
                MessageUtils.send(player, plugin, "tpa_self");
                return true;
            }

            long currentTime = System.currentTimeMillis();
            pendingRequests.put(target.getUniqueId(), new TPARequest(player.getUniqueId(), currentTime));

            MessageUtils.send(player, plugin, "tpa_sent", "target", target.getName());
            MessageUtils.sendTpaRequest(target, plugin, player.getName());

            Bukkit.getAsyncScheduler().runDelayed(plugin, task -> {
                TPARequest req = pendingRequests.get(target.getUniqueId());
                if (req != null && req.sender.equals(player.getUniqueId()) && req.timestamp == currentTime) {
                    pendingRequests.remove(target.getUniqueId());

                    Player t = Bukkit.getPlayer(target.getUniqueId());
                    if (t != null) MessageUtils.send(t, plugin, "tpa_timeout_receiver", "sender", player.getName());

                    Player p = Bukkit.getPlayer(player.getUniqueId());
                    if (p != null) MessageUtils.send(p, plugin, "tpa_timeout_sender", "target", target.getName());
                }
            }, 30, TimeUnit.SECONDS);

            return true;
        }

        if (cmdName.equals("tpaccept")) {
            TPARequest req = pendingRequests.remove(player.getUniqueId());
            if (req == null) {
                MessageUtils.send(player, plugin, "tpa_no_request");
                return true;
            }

            Player senderPlayer = Bukkit.getPlayer(req.sender);
            if (senderPlayer == null || !senderPlayer.isOnline()) {
                MessageUtils.send(player, plugin, "tpa_sender_offline");
                return true;
            }

            if (isLocked(senderPlayer)) {
                MessageUtils.send(player, plugin, "tpa_target_locked");
                MessageUtils.send(senderPlayer, plugin, "tpa_sender_locked");
                return true;
            }

            MessageUtils.send(player, plugin, "tpa_accepted_receiver");
            MessageUtils.send(senderPlayer, plugin, "tpa_accepted_sender", "target", player.getName());

            long attemptId = java.util.concurrent.ThreadLocalRandom.current().nextLong();
            senderPlayer.getPersistentDataContainer().set(lockKey, PersistentDataType.BYTE, (byte) 1);
            senderPlayer.getPersistentDataContainer().set(teleportPendingKey, PersistentDataType.LONG, attemptId);

            senderPlayer.teleportAsync(player.getLocation()).whenComplete((success, error) -> {
                Long currentAttempt = senderPlayer.getPersistentDataContainer()
                        .get(teleportPendingKey, PersistentDataType.LONG);
                if (currentAttempt == null || currentAttempt != attemptId) return;

                senderPlayer.getPersistentDataContainer().remove(teleportPendingKey);
                if (error == null && Boolean.TRUE.equals(success)) {
                    MessageUtils.send(senderPlayer, plugin, "tpa_success", "target", player.getName());
                } else {
                    senderPlayer.getPersistentDataContainer().remove(lockKey);
                    MessageUtils.send(senderPlayer, plugin, "tpa_fail");
                }
            });
            return true;
        }

        if (cmdName.equals("tpdeny")) {
            TPARequest req = pendingRequests.remove(player.getUniqueId());
            if (req == null) {
                MessageUtils.send(player, plugin, "tpa_no_request");
                return true;
            }
            MessageUtils.send(player, plugin, "tpa_denied_receiver");

            Player senderPlayer = Bukkit.getPlayer(req.sender);
            if (senderPlayer != null) {
                MessageUtils.send(senderPlayer, plugin, "tpa_denied_sender", "target", player.getName());
            }
            return true;
        }

        if (cmdName.equals("tpcancel")) {
            boolean found = false;
            for (Map.Entry<UUID, TPARequest> entry : pendingRequests.entrySet()) {
                if (entry.getValue().sender.equals(player.getUniqueId())) {
                    pendingRequests.remove(entry.getKey());
                    found = true;
                    MessageUtils.send(player, plugin, "tpa_cancelled_sender");

                    Player targetPlayer = Bukkit.getPlayer(entry.getKey());
                    if (targetPlayer != null) {
                        MessageUtils.send(targetPlayer, plugin, "tpa_cancelled_receiver", "sender", player.getName());
                    }
                    break;
                }
            }
            if (!found) MessageUtils.send(player, plugin, "tpa_no_request");
            return true;
        }

        MessageUtils.send(player, plugin, "command_failed");
        return true;
    }

    private boolean isLocked(Player player) {
        if (!player.getPersistentDataContainer().has(lockKey, PersistentDataType.BYTE)) return false;
        List<String> whitelist = plugin.getConfig().getStringList("whitelist");
        for (String name : whitelist) {
            if (name.equalsIgnoreCase(player.getName())) return false;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("tpa") && args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}
