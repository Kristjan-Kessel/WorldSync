package me.jann.worldsync.weather;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;


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
                return null;
            }

            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            scanner.close();

            // Parse the JSON response to retrieve the sunrise and sunset times
            // use gson here instead of json

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(inline.toString(), JsonObject.class);
            JsonObject sysObject = jsonObject.getAsJsonObject("sys");
            long timezoneOffset = jsonObject.get("timezone").getAsLong();
            long sunriseLong = sysObject.get("sunrise").getAsLong() - timezoneOffset;
            long sunsetLong = sysObject.get("sunset").getAsLong() - timezoneOffset;
            JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
            String weatherCondition = weatherArray.get(0).getAsJsonObject().get("main").getAsString();

            Date sunsetDate = new Date(sunsetLong * 1000);
            Date sunriseDate = new Date(sunriseLong * 1000);

            return new WeatherResult(sunriseDate,sunsetDate,weatherCondition, timezoneOffset);
        } catch (IOException e) {
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
            return false;
        }
    }

}