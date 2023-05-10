package me.jann.worldsync.weather;

import java.time.LocalDateTime;
import java.util.Date;


public class WeatherResult {

    public final String weatherCondition;
    public final LocalDateTime sunriseDate;
    public final LocalDateTime sunsetDate;
    public final Long timeZoneOffset;

    public WeatherResult(LocalDateTime sunriseDate, LocalDateTime sunsetDate, String weatherCondition, long timeZoneOffset) {
        this.weatherCondition = weatherCondition;
        this.sunriseDate = sunriseDate;
        this.sunsetDate = sunsetDate;
        this.timeZoneOffset = timeZoneOffset;
    }


}
