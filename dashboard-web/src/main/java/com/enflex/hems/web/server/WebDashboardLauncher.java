package com.enflex.hems.web.server;

import com.enflex.hems.web.server.WebDashboardServiceImpl;
import com.enflex.hems.apis.impl.OpenWeatherMapService;
import com.enflex.hems.solar.impl.SolarServiceImpl;

/**
 * Standalone Web Dashboard Launcher with Real Weather Integration
 *
 * This launcher class provides a standalone entry point for testing and
 * demonstrating the HEMS web dashboard with real-time weather data integration.
 * Creates a complete testing environment with OpenWeatherMap API connectivity
 * for realistic solar production calculations based on Essen weather conditions.
 *
 * Features:
 * - Real-time weather service initialization with OpenWeatherMap API
 * - Solar service configuration with live weather data integration
 * - Embedded web server startup on configurable port (default 8080)
 * - Graceful shutdown handling with cleanup operations
 * - Comprehensive logging for testing and debugging
 *
 * Usage:
 * Run this class directly to start a complete HEMS dashboard instance
 * with live weather integration for development and demonstration purposes.
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public class WebDashboardLauncher {

    /**
     * Main entry point for standalone web dashboard testing.
     *
     * Initializes complete HEMS web dashboard environment with:
     * - OpenWeatherMap weather service integration
     * - Solar service with real-time weather-based calculations
     * - Web server startup with dashboard interface
     * - Graceful shutdown handling
     *
     * The dashboard will be accessible at http://localhost:8080 with
     * real-time solar production data based on current Essen weather.
     *
     * @param args Command line arguments (currently unused)
     */
    public static void main(String[] args) {
        System.out.println("🚀 Web-Dashboard-Start mit ECHTEN Wetterdaten aus Essen");

        try {
            // 1. Créer et tester le service météo
            OpenWeatherMapService weatherService = new OpenWeatherMapService();
            System.out.println("🌤️ OpenWeatherMap-Service initialisiert");

            // 2. Créer le service solaire avec météo d'Essen
            SolarServiceImpl solarService = new SolarServiceImpl();
            solarService.setWeatherService(weatherService);
            System.out.println("🌞 Solar-Service mit Essen-Wetter verbunden");

            // 3. Test rapide pour voir les vraies données
            System.out.println("\n🧪 Test der ECHTEN Wetterdaten:");
            double production = solarService.getCurrentProduction();
            String status = solarService.getStatus();
            System.out.println("📊 Aktuelle Solarproduktion: " + production + " kW");
            System.out.println("📋 Status: " + status);

            // 4. Démarrer le serveur web
            WebDashboardServiceImpl webService = new WebDashboardServiceImpl(null);
            webService.startWebServer(8080);

            System.out.println("\n✅ Dashboard gestartet mit ECHTEN Wetterdaten!");
            System.out.println("🌐 Öffnen Sie Ihren Browser auf: http://localhost:8080");
            System.out.println("🌤️ Solarproduktion basiert auf ECHTEM Essen-Wetter!");
            System.out.println("⏹️  Drücken Sie Ctrl+C zum Stoppen");

            // Garder le programme en vie
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🛑 Server wird gestoppt...");
                webService.stopWebServer();
            }));

            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("❌ Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}