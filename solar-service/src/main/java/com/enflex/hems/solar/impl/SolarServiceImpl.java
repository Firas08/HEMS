package com.enflex.hems.solar.impl;

import com.enflex.hems.solar.api.SolarService;
import com.enflex.hems.apis.weather.WeatherService;
import com.enflex.hems.apis.weather.WeatherData;

/**
 * Implementation of the SolarService interface that calculates solar panel production
 * based on real weather data or simulation.
 *
 * This service integrates with OpenWeatherMap API through WeatherService to provide
 * realistic solar production calculations for a residential installation in Essen, Germany.
 *
 * @author EnFlex.IT Demo
 * @version 1.0
 * @since 2025-01-01
 */
public class SolarServiceImpl implements SolarService {

    /** Weather service dependency injected by OSGi */
    private WeatherService weatherService;

    /** Target city for weather data */
    private static final String CITY = "Essen";

    /** Maximum solar panel capacity in kilowatts (kW) */
    private static final double MAX_CAPACITY = 5.0;

    /**
     * Sets the weather service dependency.
     * This method is called by OSGi framework for dependency injection.
     *
     * @param weatherService the weather service instance to inject
     */
    public void setWeatherService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Calculates the current solar production in kilowatts.
     * Uses real weather data when available, otherwise falls back to simulation.
     *
     * @return current solar production in kW (kilowatts)
     */
    @Override
    public double getCurrentProduction() {
        if (weatherService != null) {
            double realProduction = weatherService.calculateSolarProduction(CITY, MAX_CAPACITY);
            System.out.println("🌞 Solar production " + CITY + ": " + realProduction + " kW");
            return realProduction;
        } else {
            return simulateProduction();
        }
    }

    /**
     * Returns the maximum production capacity of the solar installation.
     *
     * @return maximum production capacity in kW (currently not implemented, returns 0)
     */
    @Override
    public double getMaxProduction() {
        return 0;
    }

    /**
     * Predicts solar production for a specified number of hours in the future.
     * Adjusts current production based on sun position at the target time.
     *
     * @param hoursAhead number of hours in the future to predict
     * @return predicted solar production in kW
     */
    @Override
    public double getPredictedProduction(int hoursAhead) {
        if (weatherService != null) {
            WeatherData weather = weatherService.getCurrentWeather(CITY);
            if (weather != null) {
                double baseProduction = weatherService.calculateSolarProduction(CITY, MAX_CAPACITY);
                double hourFactor = Math.max(0.1, Math.sin(Math.PI * (System.currentTimeMillis() / 3600000 + hoursAhead) / 12.0));
                return baseProduction * hourFactor;
            }
        }
        return simulateProduction();
    }

    /**
     * Checks if the solar service is operational.
     *
     * @return true if weather service is available and accessible, false otherwise
     */
    @Override
    public boolean isOperational() {
        return weatherService != null && weatherService.isWeatherServiceAvailable();
    }

    /**
     * Returns a human-readable status description of the solar service.
     *
     * @return status string with weather conditions and solar metrics, or error message
     */
    @Override
    public String getStatus() {
        if (weatherService != null) {
            WeatherData weather = weatherService.getCurrentWeather(CITY);
            if (weather != null) {
                return String.format("Solar production Essen - Weather: %s, Clouds: %.0f%%, Irradiance: %.0f%%",
                        weather.getDescription(), weather.getCloudiness(), weather.getSolarIrradiance());
            }
        }
        return "Simulation mode - Weather service unavailable";
    }

    /**
     * Simulates solar production based on time of day when weather data is unavailable.
     * Uses a sinusoidal function to model sun position throughout the day.
     * Production is zero between 20:00 and 06:00 (night time).
     * Peak production occurs around 13:00 (solar noon).
     *
     * @return simulated solar production in kW
     */
    private double simulateProduction() {
        long currentTime = System.currentTimeMillis();
        int hour = (int) ((currentTime / 3600000) % 24);
        if (hour < 6 || hour > 20) return 0.0;
        double sunFactor = Math.sin(Math.PI * (hour - 6) / 14.0);
        return MAX_CAPACITY * sunFactor * (0.7 + Math.random() * 0.3);
    }
}