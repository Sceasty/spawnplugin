package com.spawnplugin.managers;

import com.spawnplugin.SpawnPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnManager {

    private final SpawnPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, BukkitTask> warmups = new HashMap<>();
    private final Map<UUID, Location> warmupOrigins = new HashMap<>();

    public SpawnManager(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasSpawn(World world) {
        return plugin.getConfig().contains("spawns." + world.getName());
    }

    public Location getSpawn(World world) {
        return plugin.getConfig().getSerializable("spawns." + world.getName(), Location.class);
    }

    public void setSpawn(Location location) {
        plugin.getConfig().set("spawns." + location.getWorld().getName(), location);
        plugin.saveConfig();
    }

    public void deleteSpawn(World world) {
        plugin.getConfig().set("spawns." + world.getName(), null);
        plugin.saveConfig();
    }

    public boolean hasWarmup(UUID playerId) {
        return warmups.containsKey(playerId);
    }

    public void startWarmup(Player player, Location target) {
        UUID uuid = player.getUniqueId();
        cancelWarmup(uuid);

        int warmupSeconds = plugin.getConfigManager().getWarmup();
        if (player.hasPermission("spawnplugin.bypass.warmup")) {
            warmupSeconds = 0;
        }

        if (warmupSeconds <= 0) {
            teleportPlayer(player, target);
            return;
        }

        warmupOrigins.put(uuid, player.getLocation().clone());

        int[] counter = {warmupSeconds};

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelWarmup(uuid);
                    return;
                }

                if (counter[0] <= 0) {
                    cancelWarmup(uuid);
                    teleportPlayer(player, target);
                    return;
                }

                player.sendActionBar(plugin.getConfigManager().getMessage("teleporting", "seconds", String.valueOf(counter[0])));
                counter[0]--;
            }
        }, 0L, 20L);

        warmups.put(uuid, task);
    }

    public void cancelWarmup(UUID playerId) {
        BukkitTask task = warmups.remove(playerId);
        if (task != null) {
            task.cancel();
        }
        warmupOrigins.remove(playerId);
    }

    public void cancelAllWarmups() {
        warmups.values().forEach(BukkitTask::cancel);
        warmups.clear();
        warmupOrigins.clear();
    }

    public boolean checkMovement(UUID playerId, Location currentLocation) {
        Location origin = warmupOrigins.get(playerId);
        return origin == null || currentLocation.distanceSquared(origin) > 0.25;
    }

    public boolean isOnCooldown(UUID playerId) {
        Long expiry = cooldowns.get(playerId);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            cooldowns.remove(playerId);
            return false;
        }
        return true;
    }

    public int getCooldownRemaining(UUID playerId) {
        Long expiry = cooldowns.get(playerId);
        if (expiry == null) return 0;
        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? (int) Math.ceil(remaining / 1000.0) : 0;
    }

    public void applyCooldown(UUID playerId) {
        int seconds = plugin.getConfigManager().getCooldown();
        if (seconds > 0) {
            cooldowns.put(playerId, System.currentTimeMillis() + (seconds * 1000L));
        }
    }

    public void teleportPlayer(Player player, Location target) {
        player.teleportAsync(target).thenAccept(success -> {
            if (!success) return;
            applyCooldown(player.getUniqueId());

            if (plugin.getConfigManager().isSoundEnabled()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            }
            if (plugin.getConfigManager().isParticlesEnabled()) {
                player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 1, 1, 1, 0.1);
            }

            player.sendMessage(plugin.getConfigManager().getMessage("teleported"));
        });
    }
}
