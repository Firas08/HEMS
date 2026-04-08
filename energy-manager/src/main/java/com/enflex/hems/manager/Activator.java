package com.enflex.hems.manager;

import com.enflex.hems.manager.api.EnergyManager;
import com.enflex.hems.manager.impl.EnergyManagerImpl;
import com.enflex.hems.solar.api.SolarService;
import com.enflex.hems.battery.api.BatteryService;
import com.enflex.hems.consumption.api.ConsumptionService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Bundle Activator for Energy Management System
 *
 * This activator manages the lifecycle of the central energy management service
 * which coordinates solar production, battery storage, and consumption monitoring.
 *
 * The activator implements a polling-based dependency resolution strategy to handle
 * the complex multi-service dependencies required by the energy management system.
 * It waits for all three required services (Solar, Battery, Consumption) to become
 * available before creating and registering the energy manager.
 *
 * Key Features:
 * - Asynchronous service dependency resolution
 * - Non-blocking bundle startup with background polling
 * - Resilient to unpredictable service startup ordering
 * - Graceful error handling and logging
 *
 * Required Dependencies:
 * - SolarService: Solar energy production monitoring
 * - BatteryService: Battery charge/discharge management
 * - ConsumptionService: Energy consumption tracking
 *
 * Service Export:
 * - EnergyManager: Central energy coordination and optimization
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.manager.api.EnergyManager
 * @see com.enflex.hems.manager.impl.EnergyManagerImpl
 * @see com.enflex.hems.solar.api.SolarService
 * @see com.enflex.hems.battery.api.BatteryService
 * @see com.enflex.hems.consumption.api.ConsumptionService
 */
public class Activator implements BundleActivator {

    /** Service registration handle for energy manager cleanup during shutdown */
    private ServiceRegistration<EnergyManager> serviceRegistration;

    /**
     * Initializes the energy management system with multi-service dependency resolution.
     *
     * This method implements an asynchronous polling strategy to handle the complex
     * dependency requirements of the energy management system. Since the energy manager
     * requires three separate services to function, this activator creates a background
     * thread to wait for all dependencies to become available before proceeding with
     * service creation and registration.
     *
     * Dependency Resolution Strategy:
     * - Creates background thread to avoid blocking OSGi framework startup
     * - Polls for service availability at 1-second intervals
     * - Waits until ALL required services are simultaneously available
     * - Creates energy manager instance with complete dependency set
     * - Registers energy manager service for consumption by other bundles
     *
     * Error Handling:
     * - Comprehensive exception catching with detailed error logging
     * - Graceful handling of service unavailability scenarios
     * - Non-blocking failure mode preserves framework stability
     *
     * Threading:
     * - Uses dedicated background thread for dependency polling
     * - Prevents blocking of OSGi bundle startup sequence
     * - Allows framework to continue initialization while waiting
     *
     * @param context OSGi bundle context providing access to service registry
     *                and framework operations
     *
     * @throws Exception if critical initialization failures prevent bundle startup
     *
     * @see #stop(BundleContext)
     * @since 1.0
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        System.out.println("🧠 Energy Manager: Warten auf Solar-, Batterie- und Verbrauchsservices...");

        new Thread(() -> {
            try {
                SolarService solar = null;
                BatteryService battery = null;
                ConsumptionService consumption = null;

                // Polling loop (simple for demo)
                while (solar == null || battery == null || consumption == null) {
                    ServiceReference<SolarService> solarRef = context.getServiceReference(SolarService.class);
                    ServiceReference<BatteryService> batteryRef = context.getServiceReference(BatteryService.class);
                    ServiceReference<ConsumptionService> consumptionRef = context
                            .getServiceReference(ConsumptionService.class);

                    if (solarRef != null)
                        solar = context.getService(solarRef);
                    if (batteryRef != null)
                        battery = context.getService(batteryRef);
                    if (consumptionRef != null)
                        consumption = context.getService(consumptionRef);

                    if (solar == null || battery == null || consumption == null) {
                        Thread.sleep(1000);
                    }
                }

                // Créer l'Energy Manager avec les services trouvés
                EnergyManagerImpl energyManager = new EnergyManagerImpl(
                        solar, battery, consumption);

                // Enregistrer le service EnergyManager
                serviceRegistration = context.registerService(
                        EnergyManager.class,
                        energyManager,
                        null);

                System.out.println("✅ Energy Manager erfolgreich registriert!");
            } catch (Exception e) {
                System.err.println("❌ Fehler beim verzögerten Start des Energy Managers: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Stops the energy management system and performs cleanup operations.
     *
     * Unregisters the energy manager service from the OSGi service registry
     * to ensure proper resource cleanup and prevent service zombies.
     *
     * Cleanup Operations:
     * - Unregisters energy manager service from OSGi registry
     * - Releases service registration handles
     * - Logs shutdown completion for operational monitoring
     *
     * Error Handling:
     * - Defensive null checking prevents exceptions during shutdown
     * - Graceful handling of cases where service was never registered
     *
     * Note: Background polling thread terminates automatically when the
     * bundle context becomes invalid during bundle shutdown.
     *
     * @param context OSGi bundle context for service operations
     *
     * @throws Exception if cleanup operations fail, though shutdown exceptions
     *                   are typically logged rather than propagated
     *
     * @see #start(BundleContext)
     * @since 1.0
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
        System.out.println("🛑 Energy Manager gestoppt");
    }
}