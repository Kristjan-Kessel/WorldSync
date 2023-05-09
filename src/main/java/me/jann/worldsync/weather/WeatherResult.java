package me.jann.worldsync.weather;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WeatherResult {

    public final String weatherCondition;
    public final LocalTime sunrise;
    public final LocalTime sunset;
    public final Long timeZoneOffset;

    public WeatherResult(LocalTime sunrise, LocalTime sunset, String weatherCondition, long timeZoneOffset) {
        this.weatherCondition = weatherCondition;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.timeZoneOffset = timeZoneOffset;
    }

    public LocalTime getCurrentTime(){
        LocalTime time = LocalTime.now(ZoneId.of("UTC"));
        time = time.plus(timeZoneOffset, ChronoUnit.SECONDS);
        return time;
    }

}
