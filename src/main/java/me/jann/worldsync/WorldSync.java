package me.jann.worldsync;

import me.jann.worldsync.commands.SyncCommand;
import me.jann.worldsync.commands.SyncTab;
import me.jann.worldsync.weather.WeatherAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class WorldSync extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "worldsync.admin";
    HashMap<String,Syncer> syncers = new HashMap<>();
    private String API_KEY;
    public WeatherAPI weatherAPI;
    public Logger log;

    @Override
    public void onEnable() {

        log = getLogger();

        getCommand("worldsync").setExecutor(new SyncCommand(this));
        getCommand("worldsync").setTabCompleter(new SyncTab());

        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        loadFromConfig();
    }

    public void loadFromConfig(){

        //clear old syncers
        for(Syncer syncer:syncers.values()){
            syncer.cancelLoop();
        }
        syncers.clear();

        API_KEY = this.getConfig().getString("api-key");

        if(!WeatherAPI.isApiKeyValid(API_KEY)) {
            log.warning("The provided OpenWeatherMap API key is invalid. Check your config.yml and try again with '/worldsync reload'");
            return;
        }

        weatherAPI = new WeatherAPI(API_KEY);

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
