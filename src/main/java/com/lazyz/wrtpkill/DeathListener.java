package com.lazyz.wrtpkill;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathListener implements Listener {

    private final WRTPKILL plugin;
    private final NamespacedKey lockKey;
    private final NamespacedKey teleportPendingKey;
    private final NamespacedKey pendingUnlockNoticeKey;
    private final NamespacedKey pendingSuicideNoticeKey;
    private final NamespacedKey pendingDeathNoticeKey;
    private final Map<UUID, String> pendingDeathMessages = new ConcurrentHashMap<>();

    public DeathListener(WRTPKILL plugin) {
        this.plugin = plugin;
        this.lockKey = new NamespacedKey(plugin, "rtp_locked");
        this.teleportPendingKey = new NamespacedKey(plugin, "teleport_pending");
        this.pendingUnlockNoticeKey = new NamespacedKey(plugin, "pending_death_unlock_notice");
        this.pendingSuicideNoticeKey = new NamespacedKey(plugin, "pending_suicide_notice");
        this.pendingDeathNoticeKey = new NamespacedKey(plugin, "pending_death_notice");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        boolean hadLock = player.getPersistentDataContainer().has(lockKey, PersistentDataType.BYTE)
                || player.getPersistentDataContainer().has(teleportPendingKey, PersistentDataType.LONG);

        String pendingMessage;
        if (hadLock) {
            player.getPersistentDataContainer().remove(lockKey);
            player.getPersistentDataContainer().remove(teleportPendingKey);
            player.getPersistentDataContainer().set(pendingUnlockNoticeKey, PersistentDataType.BYTE, (byte) 1);
            player.getPersistentDataContainer().remove(pendingSuicideNoticeKey);
            player.getPersistentDataContainer().remove(pendingDeathNoticeKey);
            pendingMessage = "unlock_death_merged";
        } else if (player.getPersistentDataContainer().has(pendingSuicideNoticeKey, PersistentDataType.BYTE)) {
            player.getPersistentDataContainer().remove(pendingDeathNoticeKey);
            pendingMessage = "suicide_success";
        } else {
            player.getPersistentDataContainer().set(pendingDeathNoticeKey, PersistentDataType.BYTE, (byte) 1);
            pendingMessage = "death_respawned";
        }

        pendingDeathMessages.put(player.getUniqueId(), pendingMessage);
        schedulePendingDeathMessage(player, 5L);

        if (plugin.getConfig().getBoolean("suicide-settings.force-respawn-enabled", true)) {
            Location targetLocation = plugin.getTargetSpawnLocation();
            if (targetLocation != null) {
                player.getScheduler().runDelayed(plugin, task -> {
                    if (player.isDead()) {
                        player.spigot().respawn();
                    }
                    player.teleportAsync(targetLocation);
                }, null, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (plugin.getConfig().getBoolean("suicide-settings.force-respawn-enabled", true)) {
            Location targetLocation = plugin.getTargetSpawnLocation();
            if (targetLocation != null) {
                event.setRespawnLocation(targetLocation);
            }
        }
        schedulePendingDeathMessage(player, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        Player player = event.getPlayer();
        schedulePendingDeathMessage(player, 1L);

        if (plugin.getConfig().getBoolean("suicide-settings.force-respawn-enabled", true)) {
            Location targetLocation = plugin.getTargetSpawnLocation();
            if (targetLocation != null) {
                String expectedWorld = plugin.getConfig().getString("suicide-settings.respawn-world", "world");
                player.getScheduler().runDelayed(plugin, task -> {
                    if (player.isOnline() && !player.isDead() && !player.getWorld().getName().equals(expectedWorld)) {
                        player.teleportAsync(targetLocation);
                    }
                }, null, 10L);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        schedulePendingDeathMessage(player, 20L);
    }

    private void schedulePendingDeathMessage(Player player, long delayTicks) {
        player.getScheduler().runDelayed(plugin,
                task -> sendPendingDeathMessage(player), null, delayTicks);
    }

    private void sendPendingDeathMessage(Player player) {
        if (!player.isOnline() || player.isDead()) return;

        String pendingMessage = pendingDeathMessages.remove(player.getUniqueId());
        if (pendingMessage != null) {
            clearPendingDeathMarkers(player);
        } else {
            pendingMessage = consumePendingDeathMessage(player);
        }

        if (pendingMessage != null) {
            MessageUtils.send(player, plugin, pendingMessage);
        }
    }

    private String consumePendingDeathMessage(Player player) {
        if (player.getPersistentDataContainer().has(pendingUnlockNoticeKey, PersistentDataType.BYTE)) {
            clearPendingDeathMarkers(player);
            return "unlock_death_merged";
        }

        if (player.getPersistentDataContainer().has(pendingSuicideNoticeKey, PersistentDataType.BYTE)) {
            clearPendingDeathMarkers(player);
            return "suicide_success";
        }

        if (player.getPersistentDataContainer().has(pendingDeathNoticeKey, PersistentDataType.BYTE)) {
            clearPendingDeathMarkers(player);
            return "death_respawned";
        }

        return null;
    }

    private void clearPendingDeathMarkers(Player player) {
        player.getPersistentDataContainer().remove(pendingUnlockNoticeKey);
        player.getPersistentDataContainer().remove(pendingSuicideNoticeKey);
        player.getPersistentDataContainer().remove(pendingDeathNoticeKey);
    }
}
