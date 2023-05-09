package me.jann.worldsync;

import me.jann.worldsync.weather.WeatherAPI;
import me.jann.worldsync.weather.WeatherResult;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;

public class Syncer {
    private static final int timeUpdateFrequency = 20;
    private static final int weatherUpdateFrequency = 20*60*15;
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
        },0,timeUpdateFrequency);

        //weather loop
        loops[1] = Bukkit.getScheduler().runTaskTimer(main, this::setWeatherFromResult,0,weatherUpdateFrequency);
    }

    public void cancelLoop(){
        for (BukkitTask loop : loops) {
            loop.cancel();
        }
    }

    public void setWeatherFromResult(){
        if(result.weatherCondition.contains("thunder")){
            world.setThundering(true);
            world.setThunderDuration(weatherUpdateFrequency);
        }else if(result.weatherCondition.contains("rain")){
            world.setStorm(true);
            world.setWeatherDuration(weatherUpdateFrequency);
        }else{
            world.setClearWeatherDuration(weatherUpdateFrequency);
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
        //current date in gmt
        Date current = new Date(new Date().getTime() - result.timeZoneOffset * 1000);
        Date sunrise = result.sunriseDate;
        Date sunset = result.sunsetDate;

        long currentEpoch = current.toInstant().getEpochSecond();
        long sunriseEpoch = sunrise.toInstant().getEpochSecond();
        long sunsetEpoch = sunset.toInstant().getEpochSecond();


        if(current.after(sunrise) && current.before(sunset)){
            //day
            currentEpoch-=sunriseEpoch;
            sunsetEpoch-=sunriseEpoch;
            return currentEpoch*1.0/sunsetEpoch;
        }

        if(current.before(sunrise)){
            //night
            if(current.before(sunset)) {
                sunsetEpoch -= 86400;
            }

            currentEpoch-=sunsetEpoch;
            sunriseEpoch-=sunsetEpoch;
            return currentEpoch*-1.0/sunriseEpoch;
        }

        if (current.after(sunset) && current.after(sunrise)){
            //night
            sunriseEpoch+=86400;
            currentEpoch-=sunsetEpoch;
            sunriseEpoch-=sunsetEpoch;
            return currentEpoch*-1.0/sunriseEpoch;
        }

        main.log.warning("Somethings gone horribly wrong!");
        return -1;
    }

}
