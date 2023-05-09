package me.jann.worldsync.weather;

import me.jann.worldsync.weather.WeatherResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class WeatherAPI {

    //just for testing, needs to be gotten from config later
    private final String API_KEY;

    public WeatherAPI(String apiKey) {
        this.API_KEY = apiKey;
    }

    public WeatherResult getWeather(String location){

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + API_KEY);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.out.println("Error: " + responseCode);
                return null;
            }

            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            scanner.close();

            // Parse the JSON response to retrieve the sunrise and sunset times
            JSONObject jsonObject = new JSONObject(inline.toString());
            JSONObject sysObject = jsonObject.getJSONObject("sys");
            long timezoneOffset = jsonObject.getLong("timezone");
            long sunriseLong = sysObject.getLong("sunrise")-timezoneOffset;
            long sunsetLong = sysObject.getLong("sunset")-timezoneOffset;

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            String weatherCondition = weatherArray.getJSONObject(0).getString("main");

            Date sunsetDate = new Date(sunsetLong * 1000);
            Date sunriseDate = new Date(sunriseLong * 1000);

            return new WeatherResult(sunriseDate,sunsetDate,weatherCondition, timezoneOffset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isApiKeyValid(String apiKey) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=London&appid=" + apiKey);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            return responseCode == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}