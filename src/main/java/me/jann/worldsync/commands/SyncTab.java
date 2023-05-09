package me.jann.worldsync.commands;

import me.jann.worldsync.WorldSync;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class SyncTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission(WorldSync.ADMIN_PERMISSION)){
            return null;
        }

        if(args.length<2){
            return List.of("reload");
        }
        return null;
    }
}
