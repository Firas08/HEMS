package com.enflex.hems.battery;

import com.enflex.hems.battery.api.BatteryService;
import com.enflex.hems.battery.impl.BatteryServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Bundle Activator for Battery Energy Storage Service
 *
 * This activator manages the lifecycle of the battery management service
 * which provides energy storage capabilities for the Home Energy Management
 * System (HEMS). The service operates autonomously with no external dependencies.
 *
 * Battery System Configuration:
 * - Capacity: 10 kWh residential battery
 * - Initial charge: 50%
 * - Charge/discharge operations with safety limits
 * - Real-time level monitoring
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.battery.api.BatteryService
 * @see com.enflex.hems.battery.impl.BatteryServiceImpl
 */
public class Activator implements BundleActivator {

    /** Service registration handle for battery service cleanup during shutdown */
    private ServiceRegistration<BatteryService> serviceRegistration;

    /**
     * Initializes and starts the battery energy storage service.
     *
     * Creates service instance and registers it with OSGi service registry.
     * This service has no external dependencies and starts immediately.
     *
     * @param context OSGi bundle context for service registration
     * @throws Exception if service creation or registration fails
     */
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("🔋 Battery Service gestartet!");

        // Créer l'instance du service
        BatteryServiceImpl batteryService = new BatteryServiceImpl();

        // L'enregistrer dans OSGi et garder la référence
        serviceRegistration = context.registerService(
                BatteryService.class,
                batteryService,
                null);

        System.out.println("✅ Battery Service in OSGi registriert");
    }

    /**
     * Stops the battery service and performs cleanup operations.
     *
     * Unregisters the service from OSGi registry to ensure proper resource
     * cleanup and prevent service zombies.
     *
     * @param context OSGi bundle context for service operations
     * @throws Exception if cleanup operations fail
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
        System.out.println("🛑 Battery Service gestoppt");
    }

}