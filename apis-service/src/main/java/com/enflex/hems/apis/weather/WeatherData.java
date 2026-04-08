package com.enflex.hems.apis.weather;

/**
 * Weather Data Container Class
 *
 * This class encapsulates meteorological data retrieved from weather APIs
 * and provides advanced solar irradiance calculations for photovoltaic
 * energy production estimation. Includes realistic physics-based models
 * for solar panel performance optimization.
 *
 * Features:
 * - Standard weather data storage (temperature, humidity, cloudiness)
 * - Advanced solar irradiance calculation with multiple factors
 * - Temperature coefficient modeling for PV panels
 * - Atmospheric transmission effects simulation
 * - Optimal solar condition assessment
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public class WeatherData {

    /** City name for weather data location */
    private String city;

    /** Current temperature in degrees Celsius */
    private double temperature;

    /** Relative humidity percentage (0-100%) */
    private double humidity;

    /** Cloud coverage percentage (0-100%) */
    private double cloudiness;

    /** Weather condition description */
    private String description;

    /**
     * Default constructor for weather data initialization.
     */
    public WeatherData() {
    }

    /**
     * Retrieves the city name for this weather data.
     *
     * @return City name
     */
    public String getCity() {
        return city;
    }

    /**
     * Retrieves current temperature.
     *
     * @return Temperature in degrees Celsius
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Retrieves relative humidity level.
     *
     * @return Humidity percentage (0-100%)
     */
    public double getHumidity() {
        return humidity;
    }

    /**
     * Retrieves cloud coverage level.
     *
     * @return Cloudiness percentage (0-100%)
     */
    public double getCloudiness() {
        return cloudiness;
    }

    /**
     * Retrieves weather condition description.
     *
     * @return Human-readable weather description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the city name for this weather data.
     *
     * @param city City name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets the current temperature.
     *
     * @param temperature Temperature in degrees Celsius
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Sets the relative humidity level.
     *
     * @param humidity Humidity percentage (0-100%)
     */
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    /**
     * Sets the cloud coverage level.
     *
     * @param cloudiness Cloudiness percentage (0-100%)
     */
    public void setCloudiness(double cloudiness) {
        this.cloudiness = cloudiness;
    }

    /**
     * Sets the weather condition description.
     *
     * @param description Human-readable weather description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Calculates realistic solar irradiance based on physical factors.
     *
     * This method implements a comprehensive solar irradiance model that
     * considers multiple meteorological and physical factors affecting
     * photovoltaic panel performance:
     *
     * 1. Solar Position: Time-based sun angle calculation using sinusoidal model
     * 2. Cloud Impact: Realistic attenuation with minimum diffuse radiation
     * 3. Temperature Effects: PV panel efficiency temperature coefficient (-0.4%/°C)
     * 4. Atmospheric Transmission: Humidity-based atmospheric absorption
     * 5. Extreme Conditions: Cold weather performance modeling
     *
     * Physical Models:
     * - Sun angle: Sinusoidal function from 6h-18h
     * - Cloud factor: 90% max attenuation, 10% minimum (diffuse light)
     * - Temperature coefficient: -0.4% per degree above 25°C
     * - Humidity factor: Up to 15% atmospheric absorption
     *
     * @return Solar irradiance percentage (0-100%) representing available
     *         solar energy compared to Standard Test Conditions
     */
    public double getSolarIrradiance() {
        // 1. FACTEUR TEMPOREL - Position du soleil
        long currentTime = System.currentTimeMillis();
        int hour = (int) ((currentTime / 3600000) % 24);

        // Angle du soleil (0 la nuit, 1 au zénith à midi)
        double sunAngle = 0;
        if (hour >= 6 && hour <= 18) {
            // Fonction sinusoïdale pour simuler la course du soleil
            sunAngle = Math.sin(Math.PI * (hour - 6) / 12.0);
        }

        // 2. FACTEUR NUAGES - Impact réel sur l'irradiance
        // 0% nuages = 100% irradiance, 100% nuages = 10% irradiance (diffuse)
        double cloudFactor = Math.max(0.1, 1.0 - (cloudiness / 100.0 * 0.9));

        // 3. FACTEUR TEMPÉRATURE - Performance des panneaux photovoltaïques
        // Optimum à 25°C, perte de 0.4% par degré au-dessus
        // Gain négligeable en dessous (limitation par l'irradiance)
        double tempFactor = 1.0;
        if (temperature > 25) {
            tempFactor = 1.0 - ((temperature - 25) * 0.004);
        } else if (temperature < -10) {
            // En dessous de -10°C, efficacité réduite par le froid extrême
            tempFactor = 0.8 + ((temperature + 10) * 0.02);
        }

        // 4. FACTEUR HUMIDITÉ - Impact sur la transmission atmosphérique
        double humidityFactor = Math.max(0.85, 1.0 - (humidity / 100.0 * 0.15));

        // 5. CALCUL FINAL - Irradiance solaire réaliste
        double irradiance = sunAngle * cloudFactor * tempFactor * humidityFactor * 100;

        // Limiter entre 0 et 100%
        return Math.max(0, Math.min(100, irradiance));
    }

    /**
     * Determines if current conditions are optimal for solar energy production.
     *
     * Uses calculated solar irradiance to assess whether conditions are
     * favorable for maximum photovoltaic energy generation. Threshold set
     * at 70% irradiance based on practical solar system performance metrics.
     *
     * @return true if solar irradiance exceeds 70% (optimal conditions),
     *         false for suboptimal conditions
     */
    public boolean isOptimalForSolar() {
        return getSolarIrradiance() > 70; // Plus de 70% = optimal
    }

    /**
     * Provides formatted string representation of weather data.
     *
     * @return Formatted weather summary with city, temperature, and cloudiness
     */
    @Override
    public String toString() {
        return String.format("WeatherData{city='%s', temp=%.1f°C, clouds=%.0f%%}",
                city, temperature, cloudiness);
    }
}