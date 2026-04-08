package com.enflex.hems.apis.weather;

/**
 * Weather Service Interface for Meteorological Data Integration
 *
 * This interface defines the contract for weather data services that provide
 * meteorological information for solar energy production calculations and
 * energy management optimization within a Home Energy Management System (HEMS).
 *
 * The service integrates with external weather APIs to provide:
 * - Real-time weather conditions for specified locations
 * - Solar production calculations based on meteorological data
 * - Service availability and health monitoring
 *
 * Typical implementations connect to services like OpenWeatherMap API
 * to provide accurate, location-specific weather data for energy optimization.
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public interface WeatherService {

    /**
     * Retrieves current weather data for specified city.
     *
     * @param city City name (e.g., "Paris")
     * @return Weather data object, or null if error occurs
     */
    WeatherData getCurrentWeather(String city);

    /**
     * Calculates solar panel production potential based on weather conditions.
     *
     * @param city City for weather data retrieval
     * @param maxCapacity Maximum panel capacity (e.g., 5.0 kW)
     * @return Estimated production in kilowatts (kW)
     */
    double calculateSolarProduction(String city, double maxCapacity);

    /**
     * Tests weather API service availability.
     *
     * @return true if weather service is accessible and operational
     */
    boolean isWeatherServiceAvailable();
}