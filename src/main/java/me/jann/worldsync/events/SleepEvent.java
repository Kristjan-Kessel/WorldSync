package me.jann.worldsync.events;

import me.jann.worldsync.Syncer;
import me.jann.worldsync.WorldSync;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static me.jann.worldsync.WorldSync.colorCode;

public class SleepEvent implements Listener {

    private final WorldSync main;

    public SleepEvent(WorldSync main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerBed(PlayerBedEnterEvent e){
        if(!main.sleepRegenEnabled || e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player p = e.getPlayer();

        if(!main.syncers.containsKey(p.getLocation().getWorld().getName())) return;

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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @EventHandler
    public void onUseClock(PlayerInteractEvent e){
        //TODO: add config option to disable this

        if(e.getItem() == null || !e.getItem().getType().equals(Material.CLOCK)) return;
        if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        World world = e.getPlayer().getWorld();

        Syncer syncer = main.syncers.get(world.getName());
        if(syncer == null) return;

        LocalTime now = LocalTime.now();
        now.plus(syncer.result.timeZoneOffset, ChronoUnit.SECONDS);


        String msg = colorCode(now.format(formatter));
        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));

    }

}
