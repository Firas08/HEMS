package com.enflex.hems.consumption;

import com.enflex.hems.consumption.api.ConsumptionService;
import com.enflex.hems.consumption.impl.ConsumptionServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * OSGi Bundle Activator for Energy Consumption Monitoring Service
 *
 * This activator manages the lifecycle of the consumption monitoring service
 * which tracks and simulates household electrical consumption patterns.
 * Provides a simple, autonomous service with no external dependencies.
 *
 * The service offers:
 * - Real-time consumption calculation with base load and appliances
 * - Time-based consumption prediction using typical family patterns
 * - Interactive appliance simulation for testing and optimization
 * - Operational status monitoring
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.consumption.api.ConsumptionService
 * @see com.enflex.hems.consumption.impl.ConsumptionServiceImpl
 */
public class Activator implements BundleActivator {

    /** Service registration handle for consumption service cleanup during shutdown */
    private ServiceRegistration<ConsumptionService> serviceRegistration;

    /**
     * Initializes and starts the consumption monitoring service.
     *
     * Creates service instance and registers it with OSGi service registry
     * for discovery and consumption by other bundles. This service has no
     * external dependencies and starts immediately.
     *
     * Service Configuration:
     * - Base consumption: 1.5 kW (refrigeration, lighting, electronics)
     * - Available appliances: climatisation, chauffage, lave-vaisselle, four
     * - Prediction algorithm: time-based family usage patterns
     *
     * @param context OSGi bundle context for service registration
     * @throws Exception if service creation or registration fails
     */
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("🏠 Consumption Service démarré !");

        // Étape 1 : Créer une instance du service
        ConsumptionServiceImpl consumptionService = new ConsumptionServiceImpl();

        // Étape 2 : Enregistrer le service dans le registre OSGi
        // D'autres bundles pourront maintenant le découvrir et l'utiliser
        serviceRegistration = context.registerService(
                ConsumptionService.class, // Interface exposée
                consumptionService, // Implémentation concrète
                null // Propriétés du service (optionnel)
        );

        System.out.println("✅ Consumption Service enregistré dans OSGi");
        System.out.println("📊 Consommation de base : 1.5 kW");
        System.out.println("🔌 Appareils disponibles : climatisation, chauffage, lave-vaisselle, four");
    }

    /**
     * Stops the consumption monitoring service and performs cleanup.
     *
     * Unregisters the service from OSGi registry to ensure proper resource
     * cleanup and prevent service zombies. Provides confirmation logging
     * for operational monitoring.
     *
     * @param context OSGi bundle context for service operations
     * @throws Exception if cleanup operations fail
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        // Désenregistrer le service du registre OSGi
        // Évite les fuites mémoire et informe les autres bundles
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }

        System.out.println("❌ Consumption Service arrêté");
        System.out.println("🧹 Ressources libérées");
    }
}