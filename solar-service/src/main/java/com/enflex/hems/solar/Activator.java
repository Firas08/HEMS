package com.enflex.hems.solar;

import com.enflex.hems.solar.api.SolarService;
import com.enflex.hems.solar.impl.SolarServiceImpl;
import com.enflex.hems.apis.weather.WeatherService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Bundle Activator for Solar Energy Management Service
 *
 * Manages the lifecycle of the solar service bundle, handles dependency injection
 * with weather service, and registers the service in OSGi framework.
 * Provides fallback to simulation mode when weather service unavailable.
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.solar.api.SolarService
 * @see com.enflex.hems.solar.impl.SolarServiceImpl
 */
public class Activator implements BundleActivator {

    /** Service registration handle for proper cleanup during shutdown */
    private ServiceRegistration<SolarService> serviceRegistration;

    /** Solar service implementation instance */
    private SolarServiceImpl solarServiceImpl;

    /**
     * Starts the solar service bundle and registers it in OSGi.
     * Creates service instance, attempts weather service injection,
     * and registers solar service for other bundles to consume.
     *
     * @param context OSGi bundle context for service operations
     * @throws Exception if bundle startup fails
     */
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("🌞 Solar Service démarré !");

        // Créer l'implémentation
        solarServiceImpl = new SolarServiceImpl();

        // Récupérer le WeatherService si disponible
        ServiceReference<WeatherService> weatherRef = context.getServiceReference(WeatherService.class);
        if (weatherRef != null) {
            WeatherService weatherService = context.getService(weatherRef);
            solarServiceImpl.setWeatherService(weatherService);
            System.out.println("✅ Service météo connecté au Solar Service - Ville: Essen");
        } else {
            System.out.println("⚠️ Service météo non disponible - mode simulation");
        }

        // Enregistrer le service
        serviceRegistration = context.registerService(SolarService.class, solarServiceImpl, null);
        System.out.println("✅ Solar Service avec météo réelle enregistré");
    }

    /**
     * Stops the solar service and cleans up OSGi registrations.
     * Unregisters the service to prevent zombies in service registry.
     *
     * @param context OSGi bundle context
     * @throws Exception if bundle shutdown fails
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
        System.out.println("🛑 Solar Service arrêté");
    }
}