package com.enflex.hems.web.server;

import com.enflex.hems.web.api.WebDashboardService;
import com.enflex.hems.manager.api.EnergyManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Jetty-based Web Dashboard Service Implementation
 *
 * This implementation provides a complete web server solution for the HEMS
 * dashboard using Eclipse Jetty embedded server. Serves static web content
 * (HTML, CSS, JavaScript) and REST API endpoints for real-time energy data.
 *
 * Architecture:
 * - Static Content Handler: Serves dashboard web interface files
 * - API Handler: REST endpoints for energy system data and control
 * - Dual Handler Configuration: Combines static and dynamic content serving
 *
 * Features:
 * - Embedded Jetty server with configurable port
 * - Static resource serving from classpath
 * - RESTful API integration for energy data
 * - Graceful server lifecycle management
 * - CORS support for web client integration
 *
 * URL Structure:
 * - / (root): Dashboard web interface
 * - /api/energy/*: Energy system REST API endpoints
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public class WebDashboardServiceImpl implements WebDashboardService {

    /** Jetty server instance for web hosting */
    private Server jettyServer;

    /** Energy management service for data integration */
    private EnergyManager energyManager;

    /** Server port configuration */
    private int port = 8080;

    /** Server running state flag */
    private boolean isRunning = false;

    /**
     * Constructs web dashboard service with energy manager integration.
     *
     * @param energyManager Energy management service for dashboard data,
     *                      can be null for testing scenarios
     */
    public WebDashboardServiceImpl(EnergyManager energyManager) {
        this.energyManager = energyManager;
    }

    /**
     * Starts embedded Jetty web server with dual handler configuration.
     *
     * Configures and starts Jetty server with:
     * - Static content handler for dashboard web files (HTML/CSS/JS)
     * - API servlet handler for energy system REST endpoints
     * - Welcome file configuration (index.html)
     * - Port binding and server lifecycle management
     *
     * @param port TCP port for web server binding
     */
    @Override
    public void startWebServer(int port) {
        this.port = port;
        try {
            System.out.println("🌐 Jetty-Webserver-Start auf Port " + port);

            // Créer le serveur Jetty
            jettyServer = new Server(port);

            // Handler pour les fichiers statiques (HTML/CSS/JS)
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setResourceBase(getClass().getClassLoader().getResource("static").toExternalForm());
            resourceHandler.setDirectoriesListed(false);
            resourceHandler.setWelcomeFiles(new String[]{"index.html"});

            // Handler pour les APIs REST
            ServletContextHandler apiHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            apiHandler.setContextPath("/api");

            // Servlet pour les données énergétiques
            EnergyDataServlet energyServlet = new EnergyDataServlet(energyManager);
            apiHandler.addServlet(new ServletHolder(energyServlet), "/energy/*");

            // Combiner les handlers
            HandlerList handlers = new HandlerList();
            handlers.addHandler(resourceHandler);
            handlers.addHandler(apiHandler);

            jettyServer.setHandler(handlers);

            // Démarrer le serveur
            jettyServer.start();
            isRunning = true;

            System.out.println("✅ Webserver erfolgreich gestartet!");
            System.out.println("🌐 Dashboard erreichbar unter: " + getDashboardUrl());

        } catch (Exception e) {
            System.err.println("❌ Fehler beim Webserver-Start: " + e.getMessage());
            e.printStackTrace();
            isRunning = false;
        }
    }

    /**
     * Stops the embedded Jetty web server gracefully.
     *
     * Performs clean shutdown of server resources and updates running state.
     * Handles shutdown errors gracefully with logging.
     */
    @Override
    public void stopWebServer() {
        if (jettyServer != null && isRunning) {
            try {
                System.out.println("🛑 Webserver wird gestoppt...");
                jettyServer.stop();
                isRunning = false;
                System.out.println("✅ Webserver gestoppt");
            } catch (Exception e) {
                System.err.println("❌ Fehler beim Server-Stopp: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if web server is currently running and accepting connections.
     *
     * @return true if server is active and operational
     */
    @Override
    public boolean isWebServerRunning() {
        return isRunning && jettyServer != null && jettyServer.isRunning();
    }

    /**
     * Generates complete dashboard access URL.
     *
     * @return Full URL for dashboard access including protocol and port
     */
    @Override
    public String getDashboardUrl() {
        return "http://localhost:" + port;
    }
}