package com.lazyz.wrtpkill;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DynamicRTPCommand extends Command {

    private final WRTPKILL plugin;
    private final NamespacedKey lockKey;
    private final NamespacedKey teleportPendingKey;
    private final String cmdName;

    public DynamicRTPCommand(String name, WRTPKILL plugin) {
        super(name);
        this.cmdName = name;
        this.plugin = plugin;
        this.lockKey = new NamespacedKey(plugin, "rtp_locked");
        this.teleportPendingKey = new NamespacedKey(plugin, "teleport_pending");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        String configPath = "worlds." + cmdName;

        if (!plugin.getConfig().contains(configPath)) {
            MessageUtils.send(sender, plugin, "rtp_unknown_command");
            return true;
        }

        if (!(sender instanceof Player player)) {
            MessageUtils.send(sender, plugin, "only_player");
            return true;
        }

        if (player.getPersistentDataContainer().has(lockKey, PersistentDataType.BYTE)) {
            boolean isWhitelisted = false;
            List<String> whitelist = plugin.getConfig().getStringList("whitelist");
            for (String name : whitelist) {
                if (name.equalsIgnoreCase(player.getName())) {
                    isWhitelisted = true; break;
                }
            }

            if (isWhitelisted) {
                MessageUtils.send(player, plugin, "rtp_admin_bypass");
            } else {
                MessageUtils.send(player, plugin, "rtp_locked");
                return true;
            }
        }

        String realWorldName = plugin.getConfig().getString(configPath + ".world-name", cmdName);
        World targetWorld = Bukkit.getWorld(realWorldName);
        if (targetWorld == null) {
            MessageUtils.send(player, plugin, "rtp_no_world", "world", realWorldName);
            return true;
        }

        int minX, maxX, minZ, maxZ;
        boolean useBorder = plugin.getConfig().getBoolean(configPath + ".use-border", false);

        if (useBorder) {
            WorldBorder border = targetWorld.getWorldBorder();
            double radius = border.getSize() / 2.0;
            Location center = border.getCenter();
            minX = (int) Math.max(-29999990, center.getX() - radius + 5);
            maxX = (int) Math.min(29999990, center.getX() + radius - 5);
            minZ = (int) Math.max(-29999990, center.getZ() - radius + 5);
            maxZ = (int) Math.min(29999990, center.getZ() + radius - 5);
        } else {
            minX = plugin.getConfig().getInt(configPath + ".min-x", -5000);
            maxX = plugin.getConfig().getInt(configPath + ".max-x", 5000);
            minZ = plugin.getConfig().getInt(configPath + ".min-z", -5000);
            maxZ = plugin.getConfig().getInt(configPath + ".max-z", 5000);
        }

        if (minX > maxX) { int temp = minX; minX = maxX; maxX = temp; }
        if (minZ > maxZ) { int temp = minZ; minZ = maxZ; maxZ = temp; }

        MessageUtils.send(player, plugin, "rtp_searching");

        int x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
        int z = ThreadLocalRandom.current().nextInt(minZ, maxZ + 1);

        targetWorld.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            int localX = x & 15;
            int localZ = z & 15;

            int configMaxY = plugin.getConfig().contains(configPath + ".scan-max-y") ? plugin.getConfig().getInt(configPath + ".scan-max-y") : -999;
            int configMinY = plugin.getConfig().contains(configPath + ".scan-min-y") ? plugin.getConfig().getInt(configPath + ".scan-min-y") : -999;

            int teleportY = getTeleportY(chunk, targetWorld, localX, localZ, configMaxY, configMinY);

            if (teleportY == -1) {
                MessageUtils.send(player, plugin, "rtp_unsafe");
                return;
            }

            Location loc = new Location(targetWorld, x + 0.5, teleportY, z + 0.5);
            long attemptId = ThreadLocalRandom.current().nextLong();
            player.getPersistentDataContainer().set(lockKey, PersistentDataType.BYTE, (byte) 1);
            player.getPersistentDataContainer().set(teleportPendingKey, PersistentDataType.LONG, attemptId);

            player.teleportAsync(loc).whenComplete((success, error) -> {
                Long currentAttempt = player.getPersistentDataContainer()
                        .get(teleportPendingKey, PersistentDataType.LONG);
                if (currentAttempt == null || currentAttempt != attemptId) return;

                player.getPersistentDataContainer().remove(teleportPendingKey);
                if (error == null && Boolean.TRUE.equals(success)) {
                    MessageUtils.send(player, plugin, "rtp_success", "world", targetWorld.getName(), "x", String.valueOf(x), "y", String.valueOf(teleportY), "z", String.valueOf(z));
                } else {
                    player.getPersistentDataContainer().remove(lockKey);
                    MessageUtils.send(player, plugin, "rtp_cancelled");
                }
            });
        });

        return true;
    }

    private int getTeleportY(Chunk chunk, World world, int localX, int localZ, int configMaxY, int configMinY) {
        World.Environment env = world.getEnvironment();
        int maxY = (configMaxY != -999) ? configMaxY : (env == World.Environment.NETHER ? 120 : world.getMaxHeight() - 1);
        int minY = (configMinY != -999) ? configMinY : world.getMinHeight();

        maxY = Math.min(maxY, world.getMaxHeight() - 1);
        minY = Math.max(minY, world.getMinHeight());
        if (minY > maxY) { int temp = minY; minY = maxY; maxY = temp; }

        if (env == World.Environment.NETHER) {
            for (int y = maxY; y > minY; y--) {
                if (!chunk.getBlock(localX, y - 1, localZ).getType().isAir() &&
                        chunk.getBlock(localX, y, localZ).getType().isAir() &&
                        chunk.getBlock(localX, y + 1, localZ).getType().isAir()) {
                    return y;
                }
            }
        } else {
            for (int y = maxY; y >= minY; y--) {
                Material type = chunk.getBlock(localX, y, localZ).getType();
                if (!type.isAir()) {
                    return y + 1;
                }
            }
        }
        return -1;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return java.util.Collections.emptyList();
    }
}
