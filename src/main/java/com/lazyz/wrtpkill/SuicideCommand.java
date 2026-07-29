package com.lazyz.wrtpkill;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SuicideCommand implements CommandExecutor {

    private final WRTPKILL plugin;
    private final NamespacedKey lockKey;
    private final NamespacedKey pendingSuicideNoticeKey;

    public SuicideCommand(WRTPKILL plugin) {
        this.plugin = plugin;
        this.lockKey = new NamespacedKey(plugin, "rtp_locked");
        this.pendingSuicideNoticeKey = new NamespacedKey(plugin, "pending_suicide_notice");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, plugin, "not_player");
            return true;
        }

        if (command.getName().equalsIgnoreCase("suicide")) {
            player.getScheduler().run(plugin, task -> {
                if (!player.isDead() && player.getHealth() > 0) {

                    boolean hadLock = player.getPersistentDataContainer().has(lockKey, PersistentDataType.BYTE);
                    if (!hadLock) {
                        player.getPersistentDataContainer().set(pendingSuicideNoticeKey, PersistentDataType.BYTE, (byte) 1);
                    }
                    player.setHealth(0.0);

                } else {
                    MessageUtils.send(player, plugin, "already_dead");
                }
            }, null);
            return true;
        }

        MessageUtils.send(sender, plugin, "command_failed");
        return true;
    }
}
