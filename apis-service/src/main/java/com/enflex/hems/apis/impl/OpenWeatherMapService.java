package com.enflex.hems.apis.impl;

import com.enflex.hems.apis.weather.WeatherService;
import com.enflex.hems.apis.weather.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * OpenWeatherMap API Implementation for Weather Service
 *
 * This implementation integrates with OpenWeatherMap REST API to provide
 * real-time meteorological data for solar energy production calculations.
 * Uses Java 11+ native HTTP client for efficient API communication.
 *
 * Features:
 * - Direct OpenWeatherMap API integration
 * - JSON response parsing with Jackson ObjectMapper
 * - Comprehensive weather data extraction
 * - Solar irradiance calculation based on real weather conditions
 * - Error handling with graceful fallback
 *
 * API Configuration:
 * - Provider: OpenWeatherMap
 * - Units: Metric (Celsius, km/h, etc.)
 * - Data: Current weather conditions
 * - Rate limits: Standard OpenWeatherMap free tier
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public class OpenWeatherMapService implements WeatherService {

    /** OpenWeatherMap API key for authentication */
    private static final String API_KEY = "abf47017c1142057d677accae24a9fd3";

    /** Base URL for OpenWeatherMap current weather API endpoint */
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    /**
     * Retrieves current weather data from OpenWeatherMap API.
     *
     * Makes HTTP GET request to OpenWeatherMap API, parses JSON response,
     * and creates WeatherData object with current meteorological conditions.
     * Includes comprehensive error handling for network and parsing failures.
     *
     * @param city City name for weather data retrieval
     * @return WeatherData object with current conditions, or null if error occurs
     */
    @Override
    public WeatherData getCurrentWeather(String city) {
        System.out.println("🌐 Wetter-API-Aufruf für: " + city);

        try {
            // 1. Construire l'URL
            String url = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
            System.out.println("📡 URL: " + url);

            // 2. Client HTTP Java natif (Java 11+)
            HttpClient client = HttpClient.newHttpClient();

            // 3. Créer la requête
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // 4. Exécuter la requête
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 5. Récupérer le JSON
            String jsonString = response.body();
            System.out.println("📄 JSON erhalten: " + jsonString);

            // 6. Parser le JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonString);

            // 7. Créer WeatherData
            WeatherData weather = new WeatherData();
            weather.setCity(city);
            weather.setTemperature(root.get("main").get("temp").asDouble());
            weather.setHumidity(root.get("main").get("humidity").asDouble());
            weather.setCloudiness(root.get("clouds").get("all").asDouble());
            weather.setDescription(root.get("weather").get(0).get("description").asText());

            System.out.println("✅ Echte Wetterdaten abgerufen: " + weather);
            return weather;

        } catch (Exception e) {
            System.err.println("❌ API-Fehler: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculates solar energy production based on current weather conditions.
     *
     * Retrieves weather data for specified city and applies solar irradiance
     * calculations to estimate photovoltaic energy production potential.
     *
     * @param city City for weather data retrieval
     * @param maxCapacity Maximum solar panel capacity in kilowatts (kW)
     * @return Estimated solar production in kilowatts (kW)
     */
    @Override
    public double calculateSolarProduction(String city, double maxCapacity) {
        WeatherData weather = getCurrentWeather(city);
        if (weather != null) {
            double irradiance = weather.getSolarIrradiance() / 100.0;
            return maxCapacity * irradiance;
        }
        return 0.0;
    }

    /**
     * Tests OpenWeatherMap API service availability.
     *
     * Performs test API call to verify service connectivity and functionality.
     * Uses Essen as test location for availability verification.
     *
     * @return true if API is accessible and responding correctly
     */
    @Override
    public boolean isWeatherServiceAvailable() {
        return getCurrentWeather("Essen") != null;
    }
}