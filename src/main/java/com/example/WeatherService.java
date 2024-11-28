import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WeatherService {
    private static final String API_KEY = "972f1318-50d8-41f4-9d8f-b7c86fea7fd3";
    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";

    public static void main(String[] args) {
        double lat = 55.75; // Широта
        double lon = 37.62; // Долгота
        int limit = 3; // Количество дней для прогноза

        try {
            String response = getWeatherData(lat, lon, limit);
            if (response != null) {
                parseAndDisplayWeather(response, limit);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запросе данных: " + e.getMessage());
        }
    }

    private static String getWeatherData(double lat, double lon, int limit) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%s?lat=%s&lon=%s&limit=%d", BASE_URL, lat, lon, limit);

        Request request = new Request.Builder()
                .url(url)
                .header("X-Yandex-Weather-Key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                System.err.println("Ошибка: " + response.code() + " - " + response.message());
                return null;
            }
        }
    }

    private static void parseAndDisplayWeather(String response, int limit) {
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

        System.out.println("Полный ответ от API:\n" + jsonResponse);

        JsonObject fact = jsonResponse.getAsJsonObject("fact");
        int currentTemp = fact.get("temp").getAsInt();
        System.out.println("\nТекущая температура: " + currentTemp + "°C");

        JsonArray forecasts = jsonResponse.getAsJsonArray("forecasts");
        double totalTemp = 0;
        int count = 0;

        for (int i = 0; i < Math.min(limit, forecasts.size()); i++) {
            JsonObject forecast = forecasts.get(i).getAsJsonObject();
            JsonObject dayParts = forecast.getAsJsonObject("parts").getAsJsonObject("day");
            totalTemp += dayParts.get("temp_avg").getAsDouble();
            count++;
        }

        double averageTemp = totalTemp / count;
        System.out.println("Средняя температура за " + count + " дней: " + averageTemp + "°C");
    }
}
