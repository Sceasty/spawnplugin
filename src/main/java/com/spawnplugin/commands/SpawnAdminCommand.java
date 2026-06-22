package com.spawnplugin.commands;

import com.spawnplugin.SpawnPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SpawnAdminCommand implements CommandExecutor {

    private final SpawnPlugin plugin;

    public SpawnAdminCommand(SpawnPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("spawnplugin.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("invalid-usage-admin"));
            return true;
        }

        plugin.getConfigManager().load();
        sender.sendMessage(plugin.getConfigManager().getMessage("config-reloaded"));
        return true;
    }
}
