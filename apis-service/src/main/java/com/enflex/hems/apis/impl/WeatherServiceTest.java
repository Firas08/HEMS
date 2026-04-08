package com.enflex.hems.apis.impl;

import com.enflex.hems.apis.weather.WeatherData;
import com.enflex.hems.apis.weather.WeatherService;

/**
 * Classe pour tester notre service météo
 */
public class WeatherServiceTest {

    public static void main(String[] args) {
        System.out.println("🧪 Test du WeatherService");

        // Créer notre service
        WeatherService weatherService = new OpenWeatherMapService();

        // Test 1: Récupérer la météo de Paris
        System.out.println("\n--- Test 1: Météo Paris ---");
        WeatherData parisWeather = weatherService.getCurrentWeather("Paris");
        System.out.println("Résultat: " + parisWeather);

        // Test 2: Calculer production solaire
        System.out.println("\n--- Test 2: Production solaire ---");
        double production = weatherService.calculateSolarProduction("Paris", 5.0);
        System.out.println("Production estimée: " + production + " kW");

        // Test 3: Vérifier si le service fonctionne
        System.out.println("\n--- Test 3: Service disponible ---");
        boolean available = weatherService.isWeatherServiceAvailable();
        System.out.println("Service disponible: " + available);

        System.out.println("\n✅ Tests terminés !");
    }
}
