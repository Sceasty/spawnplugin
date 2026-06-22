package com.spawnplugin;

import com.spawnplugin.commands.DelSpawnCommand;
import com.spawnplugin.commands.SetSpawnCommand;
import com.spawnplugin.commands.SpawnAdminCommand;
import com.spawnplugin.commands.SpawnCommand;
import com.spawnplugin.listeners.PlayerListener;
import com.spawnplugin.managers.ConfigManager;
import com.spawnplugin.managers.SpawnManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnPlugin extends JavaPlugin {

    private static SpawnPlugin instance;
    private ConfigManager configManager;
    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.spawnManager = new SpawnManager(this);

        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("delspawn").setExecutor(new DelSpawnCommand(this));
        getCommand("spawnplugin").setExecutor(new SpawnAdminCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("SpawnPlugin v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (spawnManager != null) {
            spawnManager.cancelAllWarmups();
        }
        getLogger().info("SpawnPlugin disabled!");
    }

    public static SpawnPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
}
