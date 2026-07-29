package com.lazyz.wrtpkill;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerConnectionListener implements Listener {

    private final WRTPKILL plugin;
    private final NamespacedKey lockKey;
    private final NamespacedKey quitTimeKey;

    public PlayerConnectionListener(WRTPKILL plugin) {
        this.plugin = plugin;
        this.lockKey = new NamespacedKey(plugin, "rtp_locked");
        this.quitTimeKey = new NamespacedKey(plugin, "wrtp_quit_time");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("suicide-settings.offline-clear-enabled", true)) return;
        event.getPlayer().getPersistentDataContainer().set(quitTimeKey, PersistentDataType.LONG, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawnLoc = plugin.getTargetSpawnLocation();

        if (plugin.getConfig().getBoolean("suicide-settings.first-join-teleport-enabled", true) && !player.hasPlayedBefore() && spawnLoc != null) {
            player.getScheduler().run(plugin, task -> player.teleportAsync(spawnLoc), null);
        }

        if (plugin.getConfig().getBoolean("suicide-settings.offline-clear-enabled", true)) {
            if (player.getPersistentDataContainer().has(quitTimeKey, PersistentDataType.LONG)) {
                long quitTime = player.getPersistentDataContainer().get(quitTimeKey, PersistentDataType.LONG);
                player.getPersistentDataContainer().remove(quitTimeKey);

                long offlineMinutes = plugin.getConfig().getLong("suicide-settings.offline-clear-minutes", 3);
                long offlineMillis = offlineMinutes * 60L * 1000L;

                if (System.currentTimeMillis() - quitTime >= offlineMillis) {
                    player.getScheduler().runDelayed(plugin, task -> {
                        player.getInventory().clear();
                        player.getEnderChest().clear();
                        if (spawnLoc != null) player.teleportAsync(spawnLoc);

                        if (player.getPersistentDataContainer().has(lockKey, PersistentDataType.BYTE)) {
                            player.getPersistentDataContainer().remove(lockKey);
                        }

                        MessageUtils.send(player, plugin, "merged_offline_notice");
                    }, null, 10L);
                }
            }
        }
    }
}