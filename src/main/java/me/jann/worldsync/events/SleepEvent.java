package me.jann.worldsync.events;

import me.jann.worldsync.WorldSync;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class SleepEvent implements Listener {

    private final WorldSync main;

    public SleepEvent(WorldSync main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerBed(PlayerBedEnterEvent e){
        if(!main.sleepRegenEnabled || e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player p = e.getPlayer();

        p.sendMessage("got here");

        if(!main.syncers.containsKey(p.getLocation().getWorld().getName())) return;

        p.sendMessage("got here");

        BukkitTask[] tasks = new BukkitTask[1];

        PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 30, 0, false, false, false);

        tasks[0] = Bukkit.getScheduler().runTaskTimer(main,()->{
            if(p.isValid() && p.isOnline() && p.isSleeping() && p.getHealth()>p.getMaxHealth()){
                p.addPotionEffect(effect);
            }else{
                tasks[0].cancel();
            }
        },0,20);

    }

}
