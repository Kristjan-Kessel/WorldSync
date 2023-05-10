package me.jann.worldsync.commands;

import me.jann.worldsync.WorldSync;
import me.jann.worldsync.weather.WeatherResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static me.jann.worldsync.WorldSync.colorCode;

public class SyncCommand implements CommandExecutor {

    private final WorldSync main;
    public SyncCommand(WorldSync main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(WorldSync.ADMIN_PERMISSION)){
            if(args.length>0){
                if(args[0].equalsIgnoreCase("reload")){
                    main.reloadConfig();
                    main.loadFromConfig(sender);
                    sender.sendMessage(colorCode("&bReloaded WorldSync config"));
                }else if(args[0].equalsIgnoreCase("sync")){
                    if(args.length == 3){
                        String worldName = args[1];
                        String location = args[2].replace("_"," ");

                        World world = Bukkit.getWorld(worldName);
                        if(world == null){
                            sender.sendMessage(colorCode("&cWorld &7"+worldName+"&c not found"));
                            return true;
                        }

                        WeatherResult result = main.weatherAPI.getWeather(location);
                        if(result == null){
                            sender.sendMessage(colorCode("&cLocation &7"+location+"&c not found"));
                            return true;
                        }

                        main.getConfig().set("worlds."+worldName,location);
                        main.saveConfig();

                        main.loadFromConfig(sender);
                        sender.sendMessage(colorCode("&7Synced &f"+worldName+"&7 to &f"+location));
                        return true;
                    }
                    sender.sendMessage(colorCode("&7/ws sync <world> <location>"));
                }else if(args[0].equalsIgnoreCase("info")){
                    if(args.length == 2){
                        String worldName = args[1];
                        World world = Bukkit.getWorld(worldName);
                        if(world == null){
                            sender.sendMessage(colorCode("&cWorld &7"+worldName+"&c not found"));
                            return true;
                        }

                        WeatherResult result = main.syncers.get(world.getName()).result;
                        if(result == null){
                            sender.sendMessage(colorCode("&cWorld &7"+worldName+"&c not synced"));
                            return true;
                        }

                        sender.sendMessage(colorCode("&7World: &f"+worldName));
                        sender.sendMessage(colorCode("&7Sunrise: &f"+result.sunriseDate));
                        sender.sendMessage(colorCode("&7Sunset: &f"+result.sunsetDate));
                        sender.sendMessage(colorCode("&7Current: &f"+ LocalDateTime.now(ZoneOffset.UTC)));
                        sender.sendMessage(colorCode("&7Weather: &f"+result.weatherCondition));

                        return true;
                    }
                    sender.sendMessage(colorCode("&7/ws info <world>"));
                }
            }
        }
        return true;
    }
}
