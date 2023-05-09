package me.jann.worldsync.commands;

import me.jann.worldsync.WorldSync;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommand implements CommandExecutor {

    private final WorldSync main;
    public SyncCommand(WorldSync main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(WorldSync.ADMIN_PERMISSION)){
            if(args.length==1){
                if(args[0].equalsIgnoreCase("reload")){
                    main.reloadConfig();
                    main.loadFromConfig();
                    sender.sendMessage("Reloaded WorldSync config");
                    return true;
                }
            }
        }
        return true;
    }
}
