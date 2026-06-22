package com.spawnplugin.commands;

import com.spawnplugin.SpawnPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SpawnCommand implements TabExecutor {

    private final SpawnPlugin plugin;

    public SpawnCommand(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        if (!player.hasPermission("spawnplugin.spawn")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        World targetWorld;
        if (args.length > 0) {
            targetWorld = Bukkit.getWorld(args[0]);
            if (targetWorld == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("world-not-found", "world", args[0]));
                return true;
            }
        } else {
            targetWorld = player.getWorld();
        }

        if (!player.hasPermission("spawnplugin.bypass.disabled-world")
                && plugin.getConfigManager().getDisabledWorlds().contains(targetWorld.getName())) {
            player.sendMessage(plugin.getConfigManager().getMessage("disabled-world"));
            return true;
        }

        if (!plugin.getSpawnManager().hasSpawn(targetWorld)) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-spawn-set"));
            return true;
        }

        UUID uuid = player.getUniqueId();
        if (!player.hasPermission("spawnplugin.bypass.cooldown") && plugin.getSpawnManager().isOnCooldown(uuid)) {
            int remaining = plugin.getSpawnManager().getCooldownRemaining(uuid);
            player.sendMessage(plugin.getConfigManager().getMessage("cooldown", "seconds", String.valueOf(remaining)));
            return true;
        }

        Location spawn = plugin.getSpawnManager().getSpawn(targetWorld);
        if (spawn == null || spawn.getWorld() == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-spawn-set"));
            return true;
        }

        plugin.getSpawnManager().startWarmup(player, spawn);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream().map(World::getName).filter(n -> n.startsWith(args[0])).toList();
        }
        return List.of();
    }
}
