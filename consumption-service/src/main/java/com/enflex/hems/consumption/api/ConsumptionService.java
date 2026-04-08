package com.enflex.hems.consumption.api;

/**
 * Energy Consumption Monitoring Service Interface
 *
 * Defines the contract for smart home electrical consumption monitoring
 * and prediction services within a Home Energy Management System (HEMS).
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public interface ConsumptionService {

    /**
     * Retourne la consommation électrique actuelle de la maison
     * @return Consommation en kW (kilowatts)
     */
    double getCurrentConsumption();

    /**
     * Prédit la consommation électrique pour la prochaine heure
     * Basé sur les habitudes et l'heure de la journée
     * @return Consommation prévue en kW
     */
    double predict();

    /**
     * Vérifie si le système de mesure fonctionne correctement
     * @return true si opérationnel, false en cas de panne
     */
    boolean isOperational();

    /**
     * Simule l'allumage ou l'extinction d'un appareil électroménager
     * Permet de tester l'impact sur la consommation totale
     * @param appliance Nom de l'appareil (ex: "climatisation", "four")
     * @param turnOn true pour allumer, false pour éteindre
     * @return true si la simulation a réussi, false si l'appareil n'existe pas
     */
    boolean simulateAppliance(String appliance, boolean turnOn);
}