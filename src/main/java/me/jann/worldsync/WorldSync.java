package me.jann.worldsync;

import me.jann.worldsync.commands.SyncCommand;
import me.jann.worldsync.commands.SyncTab;
import me.jann.worldsync.events.SleepEvent;
import me.jann.worldsync.weather.WeatherAPI;
import me.jann.worldsync.weather.WeatherResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WorldSync extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "worldsync.admin";
    public final HashMap<String,Syncer> syncers = new HashMap<>();
    private String API_KEY;
    public WeatherAPI weatherAPI;
    public Logger log;
    public boolean sleepRegenEnabled;

    @Override
    public void onEnable() {

        log = getLogger();

        getCommand("worldsync").setExecutor(new SyncCommand(this));
        getCommand("worldsync").setTabCompleter(new SyncTab());

        getServer().getPluginManager().registerEvents(new SleepEvent(this),this);

        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        loadFromConfig(null);
    }

    public void loadFromConfig(CommandSender sender){

        //clear old syncers
        for(Syncer syncer:syncers.values()){
            syncer.cancelLoop();
        }
        syncers.clear();

        API_KEY = this.getConfig().getString("api-key");

        sleepRegenEnabled = this.getConfig().getBoolean("sleep-regen");

        if(!WeatherAPI.isApiKeyValid(API_KEY)) {

            if(sender instanceof Player){
                sender.sendMessage(colorCode("&cThe provided OpenWeatherMap API key is invalid. Check the config.yml and try again with &7/worldsync reload"));
            }

            log.warning("The provided OpenWeatherMap API key is invalid. Check the config.yml and try again with '/worldsync reload'");


            return;
        }

        weatherAPI = new WeatherAPI(API_KEY);

        //load new syncers
        for(String worldName : getConfig().getConfigurationSection("worlds").getKeys(false)){
            World world = Bukkit.getWorld(worldName);
            if(world==null){
                log.warning("World "+worldName+" does not exist. Please check your config.yml");
                if (sender instanceof Player) {
                    sender.sendMessage(colorCode("&cWorld &7"+worldName+"&c does not exist. Please check your config.yml"));
                }
                continue;
            }
            world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE,101);

            String location = getConfig().getString("worlds."+worldName);
            WeatherResult result = weatherAPI.getWeather(location);
            if(result == null){
                log.warning("Could not find location "+location+". Please check your config.yml");
                if (sender instanceof Player) {
                    sender.sendMessage(colorCode("&cCould not find location &7"+location+"&c. Please check your config.yml"));
                }
                continue;
            }

            Syncer syncer = new Syncer(weatherAPI, world, location,this);
            syncers.put(worldName,syncer);
            syncer.startLoop();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    public static String colorCode(String message) {

        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }

}
