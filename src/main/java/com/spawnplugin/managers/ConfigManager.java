package com.spawnplugin.managers;

import com.spawnplugin.SpawnPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final SpawnPlugin plugin;
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    private int warmup;
    private int cooldown;
    private boolean teleportOnFirstJoin;
    private boolean teleportOnDeath;
    private boolean soundEnabled;
    private boolean particlesEnabled;
    private boolean cancelOnMove;
    private List<String> disabledWorlds;
    private String prefix;

    public ConfigManager(SpawnPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration c = plugin.getConfig();

        this.warmup = Math.max(0, c.getInt("settings.warmup", 3));
        this.cooldown = Math.max(0, c.getInt("settings.cooldown", 5));
        this.teleportOnFirstJoin = c.getBoolean("settings.teleport-on-first-join", false);
        this.teleportOnDeath = c.getBoolean("settings.teleport-on-death", true);
        this.soundEnabled = c.getBoolean("settings.sound-enabled", true);
        this.particlesEnabled = c.getBoolean("settings.particles-enabled", true);
        this.cancelOnMove = c.getBoolean("settings.cancel-on-move", true);
        this.disabledWorlds = c.getStringList("settings.disabled-worlds");
        this.prefix = c.getString("messages.prefix", "&8[&6SpawnPlugin&8] ");
    }

    public Component getMessage(String path, String... replacements) {
        String raw = plugin.getConfig().getString("messages." + path);
        if (raw == null) {
            return legacySerializer.deserialize("&cMessage not found: " + path);
        }
        raw = prefix + raw;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            raw = raw.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return legacySerializer.deserialize(raw);
    }

    public Component getMessagePlain(String path, String... replacements) {
        String raw = plugin.getConfig().getString("messages." + path);
        if (raw == null) {
            return legacySerializer.deserialize("&cMessage not found: " + path);
        }
        for (int i = 0; i < replacements.length - 1; i += 2) {
            raw = raw.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return legacySerializer.deserialize(raw);
    }

    public int getWarmup() { return warmup; }
    public int getCooldown() { return cooldown; }
    public boolean isTeleportOnFirstJoin() { return teleportOnFirstJoin; }
    public boolean isTeleportOnDeath() { return teleportOnDeath; }
    public boolean isSoundEnabled() { return soundEnabled; }
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public boolean isCancelOnMove() { return cancelOnMove; }
    public List<String> getDisabledWorlds() { return disabledWorlds; }
}
