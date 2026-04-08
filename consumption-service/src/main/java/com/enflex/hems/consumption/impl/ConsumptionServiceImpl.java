package com.enflex.hems.consumption.impl;

import com.enflex.hems.consumption.api.ConsumptionService;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Energy Consumption Monitoring Service
 *
 * This implementation simulates a smart home's electrical consumption patterns
 * with base load consumption plus controllable household appliances.
 * Provides realistic consumption modeling for energy management optimization.
 *
 * Features:
 * - Base consumption simulation (refrigeration, lighting, electronics)
 * - Individual appliance power modeling and control
 * - Time-based consumption prediction using typical family patterns
 * - Real-time appliance state management
 * - Operational status monitoring
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public class ConsumptionServiceImpl implements ConsumptionService {

    /** Base consumption for essential home systems (kW) */
    private static final double BASE_CONSUMPTION = 1.5; // 1.5 kW

    /** Operational status flag for the consumption monitoring system */
    private boolean operational = true;

    /** Map of currently active appliances and their power consumption */
    private Map<String, Double> activeAppliances = new HashMap<>();

    /** Catalog of available appliances and their rated power consumption */
    private Map<String, Double> availableAppliances = Map.of(
            "climatisation", 3.0,    // Climatisation : 3 kW
            "chauffage", 4.0,        // Chauffage : 4 kW
            "lave-vaisselle", 2.0,   // Lave-vaisselle : 2 kW
            "four", 2.5              // Four électrique : 2.5 kW
    );

    /**
     * Calculates total current electrical consumption.
     *
     * Aggregates base consumption with all currently active appliances
     * to provide real-time total power usage for energy management decisions.
     *
     * @return Total current consumption in kilowatts (kW)
     */
    @Override
    public double getCurrentConsumption() {
        // Commencer par la consommation de base
        double total = BASE_CONSUMPTION;

        // Ajouter la consommation de tous les appareils allumés
        for (double power : activeAppliances.values()) {
            total += power;
        }

        return total;
    }

    /**
     * Predicts electrical consumption for the next hour based on typical patterns.
     *
     * Uses time-of-day analysis to forecast energy usage based on typical
     * family consumption patterns including morning routines, evening activities,
     * and nighttime base loads.
     *
     * Prediction Logic:
     * - Morning peak (7-9h): Wake-up, shower, breakfast preparation
     * - Evening peak (18-22h): Return home, cooking, entertainment
     * - Night period (0-6h): Sleep mode with essential systems only
     * - Standard hours: Normal daytime consumption
     *
     * @return Predicted consumption for next hour in kilowatts (kW)
     */
    @Override
    public double predict() {
        LocalTime now = LocalTime.now();
        int nextHour = now.getHour() + 1; // Heure suivante

        // Logique de prédiction selon les habitudes familiales
        if (nextHour >= 7 && nextHour <= 9) {
            // Pic du matin : réveil, douche, petit-déjeuner
            return BASE_CONSUMPTION + 2.0; // +2 kW
        } else if (nextHour >= 18 && nextHour <= 22) {
            // Pic du soir : retour du travail, cuisine, TV
            return BASE_CONSUMPTION + 4.0; // +4 kW
        } else if (nextHour >= 0 && nextHour <= 6) {
            // Nuit : famille dort, juste le frigo
            return BASE_CONSUMPTION + 0.5; // +0.5 kW
        }

        // Journée normale : consommation standard
        return BASE_CONSUMPTION + 1.0; // +1 kW
    }

    /**
     * Returns operational status of the consumption monitoring system.
     *
     * @return true if monitoring system is operational and providing
     *         reliable consumption data
     */
    @Override
    public boolean isOperational() {
        return operational;
    }

    /**
     * Simulates appliance activation or deactivation with real-time impact.
     *
     * Modifies the active appliance registry to immediately affect total
     * consumption calculations. Provides realistic appliance control
     * simulation for energy management testing and optimization.
     *
     * @param appliance Name of the appliance to control
     * @param turnOn true to activate appliance, false to deactivate
     * @return true if simulation successful, false if appliance unknown
     *         or system not operational
     */
    @Override
    public boolean simulateAppliance(String appliance, boolean turnOn) {
        // Vérifier si le système fonctionne
        if (!operational) {
            return false; // Système en panne
        }

        // Vérifier si l'appareil existe dans notre catalogue
        if (!availableAppliances.containsKey(appliance)) {
            return false; // Appareil inconnu
        }

        if (turnOn) {
            // ALLUMER : ajouter l'appareil à la liste des actifs
            double power = availableAppliances.get(appliance);
            activeAppliances.put(appliance, power);
            System.out.println("🔌 " + appliance + " eingeschaltet (" + power + " kW)");
        } else {
            // ÉTEINDRE : retirer l'appareil de la liste des actifs
            activeAppliances.remove(appliance);
            System.out.println("🔌 " + appliance + " ausgeschaltet");
        }

        return true; // Simulation réussie
    }

    /**
     * Utility method to modify operational status for testing purposes.
     *
     * @param operational new operational status
     */
    public void setOperational(boolean operational) {
        this.operational = operational;
    }

    /**
     * Utility method to retrieve currently active appliances.
     *
     * @return Defensive copy of active appliances map
     */
    public Map<String, Double> getActiveAppliances() {
        return new HashMap<>(activeAppliances); // Copie défensive
    }
}