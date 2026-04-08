package com.enflex.hems.web.api;

/**
 * Web Dashboard Service Interface for HEMS User Interface
 *
 * This interface defines the contract for web-based dashboard services that
 * provide HTTP server management and user interface access for the Home Energy
 * Management System (HEMS). Enables web browser access to real-time energy
 * data and system controls.
 *
 * Key Capabilities:
 * - HTTP server lifecycle management (start/stop operations)
 * - Configurable port assignment for web access
 * - Server status monitoring and health checks
 * - Dashboard URL generation for client access
 *
 * The web dashboard serves as the primary user interface for:
 * - Real-time energy production and consumption monitoring
 * - Battery status and charge level visualization
 * - System configuration and manual overrides
 * - Historical data charts and analytics
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05

 */
public interface WebDashboardService {

    /**
     * Starts the HTTP web server on specified port.
     *
     * Initializes embedded web server to serve dashboard interface and
     * REST API endpoints for energy management system access.
     *
     * @param port TCP port number for web server (e.g., 8080)
     */
    void startWebServer(int port);

    /**
     * Stops the HTTP web server and releases resources.
     *
     * Gracefully shuts down web server, closes all connections,
     * and frees allocated network resources.
     */
    void stopWebServer();

    /**
     * Checks if the web server is currently active and serving requests.
     *
     * @return true if web server is running and accepting connections,
     *         false if stopped or unavailable
     */
    boolean isWebServerRunning();

    /**
     * Retrieves the complete dashboard access URL.
     *
     * Provides full URL for accessing the web dashboard interface,
     * including protocol, hostname, and configured port.
     *
     * @return Complete dashboard URL (e.g., "http://localhost:8080")
     */
    String getDashboardUrl();
}