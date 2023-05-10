package me.jann.worldsync.commands;

import me.jann.worldsync.WorldSync;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> completions = new ArrayList<>();

        if(!sender.hasPermission(WorldSync.ADMIN_PERMISSION)) return completions;

        if(args.length == 1){
            completions.add("reload");
            completions.add("sync");
            completions.add("info");
        }else if(args.length == 2){
            if(args[0].equalsIgnoreCase("sync") || args[0].equalsIgnoreCase("info")){
                for (World world: Bukkit.getWorlds()){
                    completions.add(world.getName());
                }
            }
        }

        List<String> poscompl = new ArrayList<>(completions);
        poscompl.removeIf(s -> !s.startsWith(args[args.length-1]));

        if(!poscompl.isEmpty()){
            completions = poscompl;
        }

        return completions;
    }
}
