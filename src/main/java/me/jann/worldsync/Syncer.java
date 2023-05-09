package me.jann.worldsync;

import me.jann.worldsync.weather.WeatherAPI;
import me.jann.worldsync.weather.WeatherResult;
import org.bukkit.World;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Syncer {
    private final WeatherAPI weatherAPI;
    private final World world;
    private final String syncTarget;
    private WeatherResult result;

    public Syncer(WeatherAPI weatherAPI, World world, String syncTarget) {
        this.weatherAPI = weatherAPI;
        this.world = world;
        this.syncTarget = syncTarget;

        result = weatherAPI.getWeather(syncTarget);
    }

    /*
    Mc day lasts 0-24000 ticks
    0 is sunrise
    6000 is noon
    12000 is sunset
    18000 is midnight
     */

    public static long convertToTimeTicks(double percentage){
        if(percentage<0){
            //night 12000-0
            return (long) (12000*(1+percentage));
        }else{
            //day 0-12000
            return (long) (12000*percentage);
        }
    }

    double calculate() {
        //current date in gmt
        Date current = new Date(new Date().getTime() - result.timeZoneOffset * 1000);
        Date sunrise = result.sunriseDate;
        Date sunset = result.sunsetDate;

        long currentEpoch = current.toInstant().getEpochSecond();
        long sunriseEpoch = sunrise.toInstant().getEpochSecond();
        long sunsetEpoch = sunset.toInstant().getEpochSecond();


        if(current.after(sunrise) && current.before(sunset)){
            currentEpoch-=sunriseEpoch;
            sunsetEpoch-=sunriseEpoch;
            return currentEpoch*1.0/sunsetEpoch;
        }

        if(current.before(sunrise)){

            if(current.before(sunset)) {
                sunsetEpoch -= 86400;
            }

            currentEpoch-=sunsetEpoch;
            sunriseEpoch-=sunsetEpoch;
            return currentEpoch*-1.0/sunriseEpoch;
        }

        return -1;
    }

}
