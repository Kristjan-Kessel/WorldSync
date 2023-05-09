package me.jann.worldsync;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WeatherAPI {

    //just for testing, needs to be gotten from config later
    private final String API_KEY;

    public WeatherAPI(String apiKey) {
        this.API_KEY = apiKey;
    }

    public void getWeather(String location){

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + API_KEY);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.out.println("Error: " + responseCode);
                return;
            }

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            scanner.close();

            // Parse the JSON response to retrieve the sunrise and sunset times
            JSONObject jsonObject = new JSONObject(inline);
            JSONObject sysObject = jsonObject.getJSONObject("sys");
            long sunriseTime = sysObject.getLong("sunrise");
            long sunsetTime = sysObject.getLong("sunset");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            String weatherCondition = weatherArray.getJSONObject(0).getString("main");

            // Format the sunrise and sunset times as strings in the "HH:mm" format
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String sunriseTimeString = dateFormat.format(new Date(sunriseTime * 1000));
            String sunsetTimeString = dateFormat.format(new Date(sunsetTime * 1000));

            // Calculate the amount of time between sunrise and sunset
            long timeDifference = sunsetTime - sunriseTime;

            // Print the sunrise and sunset times and the time difference between them
            System.out.println("Sunrise time: " + sunriseTimeString);
            System.out.println("Sunset time: " + sunsetTimeString);
            System.out.println("Time between sunrise and sunset: " + (timeDifference / 3600) + " hours " + ((timeDifference % 3600) / 60) + " minutes");

            System.out.println("Weather condition: " + weatherCondition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isApiKeyValid(String apiKey) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=London&appid=" + apiKey);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}