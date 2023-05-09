package me.jann.worldsync.weather;

import java.util.Date;


public class WeatherResult {

    public final String weatherCondition;
    public final Date sunriseDate;
    public final Date sunsetDate;
    public final Long timeZoneOffset;

    public WeatherResult(Date sunriseDate, Date sunsetDate, String weatherCondition, long timeZoneOffset) {
        this.weatherCondition = weatherCondition;
        this.sunriseDate = sunriseDate;
        this.sunsetDate = sunsetDate;
        this.timeZoneOffset = timeZoneOffset;
    }


}
