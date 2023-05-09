package me.jann.worldsync;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class WorldSync extends JavaPlugin {

    private String API_KEY;
    public WeatherAPI weatherAPI;
    public Logger log;

    @Override
    public void onEnable() {

        log = getLogger();

        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        API_KEY = this.getConfig().getString("api-key");

        if(!WeatherAPI.isApiKeyValid(API_KEY)) {
            log.warning("The provided OpenWeatherMap API key is invalid. The plugin will now disable itself. Please check that the key is correct and try again.");
            // Disable the plugin
            Bukkit.getPluginManager().disablePlugin(this);
        }

        weatherAPI = new WeatherAPI(API_KEY);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
