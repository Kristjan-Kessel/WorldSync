package me.jann.worldsync;

import me.jann.worldsync.weather.WeatherAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class WorldSync extends JavaPlugin {

    HashMap<String,Syncer> syncers = new HashMap<>();
    private String API_KEY;
    public WeatherAPI weatherAPI;
    public Logger log;

    @Override
    public void onEnable() {

        log = getLogger();

        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        loadFromConfig();
    }

    private void loadFromConfig(){
        API_KEY = this.getConfig().getString("api-key");

        if(!WeatherAPI.isApiKeyValid(API_KEY)) {
            log.warning("The provided OpenWeatherMap API key is invalid. The plugin will now disable itself. Please check that the key is correct and try again.");
            // Disable the plugin
            Bukkit.getPluginManager().disablePlugin(this);
        }

        weatherAPI = new WeatherAPI(API_KEY);

        //clear old syncers
        for(Syncer syncer:syncers.values()){
            syncer.cancelLoop();
        }
        syncers.clear();

        //load new syncers
        for(String worldName : getConfig().getConfigurationSection("worlds").getKeys(false)){
            World world = Bukkit.getWorld(worldName);
            if(world==null){
                log.warning("World "+worldName+" does not exist. Please check your config.yml");
                continue;
            }
            world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
            Syncer syncer = new Syncer(weatherAPI, world, getConfig().getString("worlds."+worldName),this);
            syncers.put(worldName,syncer);
            syncer.startLoop();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
