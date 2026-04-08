package com.enflex.hems.web;

import com.enflex.hems.web.api.WebDashboardService;
import com.enflex.hems.web.server.WebDashboardServiceImpl;
import com.enflex.hems.manager.api.EnergyManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Bundle Activator for Web Dashboard Service
 *
 * This activator manages the lifecycle of the web-based dashboard interface
 * for the Home Energy Management System (HEMS). Starts embedded Jetty server
 * and integrates with energy management services for real-time data display.
 *
 * Features:
 * - Automatic Energy Manager service discovery and integration
 * - Fallback demo mode when Energy Manager unavailable
 * - Embedded Jetty web server management
 * - Service registration for OSGi ecosystem integration
 * - Graceful startup and shutdown handling
 *
 * The dashboard provides:
 * - Real-time energy monitoring interface
 * - System control capabilities
 * - Responsive modern web UI
 * - REST API endpoints for data access
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.web.api.WebDashboardService
 * @see com.enflex.hems.web.server.WebDashboardServiceImpl
 * @see com.enflex.hems.manager.api.EnergyManager
 */
public class Activator implements BundleActivator {

    /** Service registration handle for web dashboard cleanup during shutdown */
    private ServiceRegistration<WebDashboardService> serviceRegistration;

    /** Web dashboard service implementation instance */
    private WebDashboardServiceImpl webDashboardService;

    /**
     * Starts the web dashboard service with Energy Manager integration.
     *
     * Performs service discovery for Energy Manager and initializes web dashboard
     * with either full integration or demo mode fallback. Starts embedded Jetty
     * server on port 8080 and registers service in OSGi registry.
     *
     * Integration Modes:
     * - Full Mode: With Energy Manager for real-time data
     * - Demo Mode: Standalone with simulated data when Energy Manager unavailable
     *
     * @param context OSGi bundle context for service operations
     * @throws Exception if critical initialization failures occur
     */
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("🌐 Web-Dashboard gestartet!");

        try {
            // Récupérer l'Energy Manager
            ServiceReference<EnergyManager> energyManagerRef =
                    context.getServiceReference(EnergyManager.class);

            if (energyManagerRef != null) {
                EnergyManager energyManager = context.getService(energyManagerRef);

                // Créer le service web dashboard
                webDashboardService = new WebDashboardServiceImpl(energyManager);

                // Démarrer le serveur web sur port 8080
                webDashboardService.startWebServer(8080);

                // Enregistrer le service dans OSGi
                serviceRegistration = context.registerService(
                        WebDashboardService.class,
                        webDashboardService,
                        null
                );

                System.out.println("✅ Web-Dashboard in OSGi registriert");
                System.out.println("🌐 Interface erreichbar unter: " + webDashboardService.getDashboardUrl());
                System.out.println("🎨 Modernes Dashboard bereit!");

            } else {
                System.err.println("⚠️ Energy Manager nicht gefunden - Dashboard im Demo-Modus");

                // Mode démo sans Energy Manager
                webDashboardService = new WebDashboardServiceImpl(null);
                webDashboardService.startWebServer(8080);

                serviceRegistration = context.registerService(
                        WebDashboardService.class,
                        webDashboardService,
                        null
                );

                System.out.println("🌐 Dashboard im Demo-Modus: " + webDashboardService.getDashboardUrl());
            }

        } catch (Exception e) {
            System.err.println("❌ Fehler beim Web-Dashboard-Start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stops the web dashboard service and performs cleanup operations.
     *
     * Gracefully shuts down embedded Jetty server, unregisters service
     * from OSGi registry, and releases all allocated resources.
     *
     * Cleanup Operations:
     * - Web server shutdown with connection cleanup
     * - Service unregistration from OSGi registry
     * - Resource reference cleanup
     * - Confirmation logging
     *
     * @param context OSGi bundle context for service operations
     * @throws Exception if cleanup operations encounter errors
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("🛑 Web-Dashboard wird gestoppt...");

        try {
            // Arrêter le serveur web
            if (webDashboardService != null) {
                webDashboardService.stopWebServer();
                webDashboardService = null;
            }

            // Désenregistrer le service
            if (serviceRegistration != null) {
                serviceRegistration.unregister();
                serviceRegistration = null;
            }

            System.out.println("✅ Web-Dashboard ordnungsgemäß gestoppt");

        } catch (Exception e) {
            System.err.println("❌ Fehler beim Web-Dashboard-Stopp: " + e.getMessage());
        }
    }
}