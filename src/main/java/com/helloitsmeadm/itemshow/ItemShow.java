package com.helloitsmeadm.itemshow;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemShow extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save config
        saveDefaultConfig();

        // Enable Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
