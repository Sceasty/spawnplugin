package com.spawnplugin.commands;

import com.spawnplugin.SpawnPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelSpawnCommand implements CommandExecutor {

    private final SpawnPlugin plugin;

    public DelSpawnCommand(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        if (!player.hasPermission("spawnplugin.setspawn")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (!plugin.getSpawnManager().hasSpawn(player.getWorld())) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-spawn-set"));
            return true;
        }

        plugin.getSpawnManager().deleteSpawn(player.getWorld());
        player.sendMessage(plugin.getConfigManager().getMessage("spawn-deleted"));
        return true;
    }
}
