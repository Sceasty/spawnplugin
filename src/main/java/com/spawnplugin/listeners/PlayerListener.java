package com.spawnplugin.listeners;

import com.spawnplugin.SpawnPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final SpawnPlugin plugin;

    public PlayerListener(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!plugin.getConfigManager().isTeleportOnDeath()) return;
        if (event.isBedSpawn() || event.isAnchorSpawn()) return;

        Player player = event.getPlayer();
        Location spawn = plugin.getSpawnManager().getSpawn(player.getWorld());
        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getConfigManager().isTeleportOnFirstJoin()) return;
        if (player.hasPlayedBefore()) return;

        Location spawn = plugin.getSpawnManager().getSpawn(player.getWorld());
        if (spawn != null) {
            plugin.getSpawnManager().teleportPlayer(player, spawn);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getSpawnManager().cancelWarmup(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getConfigManager().isCancelOnMove()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!plugin.getSpawnManager().hasWarmup(uuid)) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        plugin.getSpawnManager().cancelWarmup(uuid);
        player.sendMessage(plugin.getConfigManager().getMessage("teleport-cancelled"));
    }
}
