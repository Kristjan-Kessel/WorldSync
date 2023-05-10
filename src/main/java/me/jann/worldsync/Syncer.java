package me.jann.worldsync;

import me.jann.worldsync.weather.WeatherAPI;
import me.jann.worldsync.weather.WeatherResult;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class Syncer {
    private final WorldSync main;
    private final WeatherAPI weatherAPI;
    private final World world;
    private final String syncTarget;
    public WeatherResult result;

    private final BukkitTask[] loops = new BukkitTask[2];

    public Syncer(WeatherAPI weatherAPI, World world, String syncTarget, WorldSync main) {
        this.weatherAPI = weatherAPI;
        this.world = world;
        this.syncTarget = syncTarget;
        this.main = main;
        result = weatherAPI.getWeather(syncTarget);
    }

    public void startLoop(){

        //time loop
        loops[0] = Bukkit.getScheduler().runTaskTimer(main, ()->{
            result = weatherAPI.getWeather(syncTarget);
            //check if result was valid
            if(result==null){
                cancelLoop();
                return;
            }
            world.setTime(convertPercentageToTimeTicks(calculateDaylightCycleRatio()));
        },0,main.timeUpdateFrequency);

        //weather loop
        loops[1] = Bukkit.getScheduler().runTaskTimer(main, this::setWeatherFromResult,0,main.weatherUpdateFrequency);
    }


    public void cancelLoop(){
        for (BukkitTask loop : loops) {
            loop.cancel();
        }
    }

    public void setWeatherFromResult(){
        if(result.weatherCondition.contains("thunder")){
            world.setThundering(true);
            world.setThunderDuration(main.weatherUpdateFrequency+5);
        }else if(result.weatherCondition.contains("rain")){
            world.setStorm(true);
            world.setWeatherDuration(main.weatherUpdateFrequency+5);
        }else{
            world.setClearWeatherDuration(main.weatherUpdateFrequency+5);
        }
    }

    public static long convertPercentageToTimeTicks(double percentage){
        if(percentage<0){
            percentage*=-1;
            //night 12000-0
            return (long) (12000*(1+percentage));
        }else{
            //day 0-12000
            return (long) (12000*percentage);
        }
    }

    double calculateDaylightCycleRatio() {

        LocalDateTime current = LocalDateTime.now(ZoneOffset.UTC);

        LocalDateTime sunrise = result.sunriseDate;
        LocalDateTime sunset = result.sunsetDate;

        long currentEpoch = current.toInstant(ZoneOffset.UTC).getEpochSecond();
        long sunriseEpoch = sunrise.toInstant(ZoneOffset.UTC).getEpochSecond();
        long sunsetEpoch = sunset.toInstant(ZoneOffset.UTC).getEpochSecond();

        if(current.isAfter(sunrise) && current.isBefore(sunset)){
            //day
            currentEpoch-=sunriseEpoch;
            sunsetEpoch-=sunriseEpoch;
            return currentEpoch*1.0/sunsetEpoch;
        } else

        if(current.isBefore(sunrise)){
            //night
            if(current.isBefore(sunset)) {
                sunsetEpoch -= 86400;
            }

            currentEpoch-=sunsetEpoch;
            sunriseEpoch-=sunsetEpoch;
            return currentEpoch*-1.0/sunriseEpoch;
        } else

        if (current.isBefore(sunset) && current.isBefore(sunrise)){
            //night
            sunriseEpoch+=86400;
            currentEpoch-=sunsetEpoch;
            sunriseEpoch-=sunsetEpoch;
            return currentEpoch*-1.0/sunriseEpoch;
        } else

        if (current.isAfter(sunset) && sunset.isAfter(sunrise)){
            //night
            sunriseEpoch+=86400;
            currentEpoch-=sunsetEpoch;
            sunriseEpoch-=sunsetEpoch;
            return currentEpoch*-1.0/sunriseEpoch;
        }

        main.log.warning("Somethings gone horribly wrong!");
        cancelLoop();
        return -1;
    }

}
