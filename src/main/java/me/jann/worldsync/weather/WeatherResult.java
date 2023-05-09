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
