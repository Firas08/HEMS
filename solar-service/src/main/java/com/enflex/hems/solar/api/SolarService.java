package com.enflex.hems.solar.api;

/**
 * Solar Energy Production Management Service Interface
 *
 * This interface defines the contract for managing solar panel energy production
 * in a Home Energy Management System (HEMS). It provides methods for real-time
 * monitoring, prediction, and status management of solar energy generation.
 *
 * The service integrates with weather APIs to calculate realistic solar production
 * based on meteorological conditions including irradiance, temperature, and cloud coverage.
 *
 * Implementation typically connects to:
 * - OpenWeatherMap API for meteorological data
 * - Solar panel monitoring systems
 * - Energy management controllers
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.solar.impl.SolarServiceImpl
 * @see com.enflex.hems.apis.weather.WeatherService
 */
public interface SolarService {

    /**
     * Retrieves the current real-time solar energy production.
     *
     * This method calculates instantaneous power generation based on current
     * meteorological conditions including solar irradiance, panel temperature,
     * and cloud coverage. Uses real weather data when available, falls back
     * to time-based simulation otherwise.
     *
     * @return current solar production in kilowatts (kW). Returns 0.0 during
     *         nighttime or when panels are not operational. Typical range: [0.0, 5.0]
     */
    double getCurrentProduction();

    /**
     * Returns the maximum theoretical solar energy production capacity.
     *
     * This represents the peak power output of the solar installation under
     * optimal conditions (full sun, optimal temperature, no shading).
     * Value is typically configured during system installation.
     *
     * @return maximum production capacity in kilowatts (kW). Fixed value
     *         representing total installed solar panel capacity
     */
    double getMaxProduction();

    /**
     * Determines the operational status of the solar energy system.
     *
     * Checks if the solar service and its dependencies (weather service,
     * panel monitoring) are available and functioning correctly.
     *
     * @return true if the solar system is fully operational and can provide
     *         accurate production data; false if any component is unavailable
     */
    boolean isOperational();

    /**
     * Predicts solar energy production for a specified future time period.
     *
     * Uses weather forecast data combined with sun position calculations to
     * estimate future solar production. Accuracy decreases with longer forecast
     * periods due to weather uncertainty.
     *
     * @param hoursAhead number of hours in the future to predict (typically 1-72)
     * @return predicted solar production in kilowatts (kW) for the specified
     *         future time. Returns 0.0 for nighttime predictions
     */
    double getPredictedProduction(int hoursAhead);

    /**
     * Returns comprehensive status information about the solar energy system.
     *
     * Provides detailed information including current weather conditions,
     * cloud coverage, solar irradiance levels, and system operational status.
     * Used for monitoring, debugging, and user interface display.
     *
     * @return human-readable status string containing weather conditions and
     *         solar metrics, or "Simulation mode" if weather service unavailable
     */
    String getStatus();
}