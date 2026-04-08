package com.enflex.hems.manager.impl;

import com.enflex.hems.manager.api.EnergyManager;
import com.enflex.hems.solar.api.SolarService;
import com.enflex.hems.battery.api.BatteryService;
import com.enflex.hems.consumption.api.ConsumptionService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of the Energy Management System Controller
 *
 * This class provides intelligent energy flow optimization for Home Energy
 * Management Systems (HEMS). It coordinates solar production, battery storage,
 * consumption patterns, and grid interactions to minimize energy costs and
 * maximize renewable energy utilization.
 *
 * Key Features:
 * - Real-time energy balance calculations
 * - Intelligent charge/discharge decisions
 * - Automatic grid trading operations
 * - Configurable optimization thresholds
 * - Autonomous operation mode with timer-based cycles
 *
 * Decision Logic:
 * - Surplus energy: Charge battery first, then sell to grid
 * - Deficit energy: Use battery first, then buy from grid
 * - Economic optimization: Consider energy prices and battery levels
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.manager.api.EnergyManager
 * @see com.enflex.hems.solar.api.SolarService
 * @see com.enflex.hems.battery.api.BatteryService
 * @see com.enflex.hems.consumption.api.ConsumptionService
 */
public class EnergyManagerImpl implements EnergyManager {

    /** OSGi service reference for solar energy production data */
    private SolarService solarService;

    /** OSGi service reference for battery management operations */
    private BatteryService batteryService;

    /** OSGi service reference for consumption monitoring data */
    private ConsumptionService consumptionService;

    /** Operational status flag for the energy management system */
    private boolean operational = true;

    /** Flag indicating whether automatic optimization mode is active */
    private boolean automaticMode = false;

    /** Timer for periodic energy optimization in automatic mode */
    private Timer optimizationTimer;

    /** Minimum surplus energy threshold to trigger charging actions (kW) */
    private static final double SURPLUS_THRESHOLD = 0.5;

    /** Minimum deficit energy threshold to trigger discharging actions (kW) */
    private static final double DEFICIT_THRESHOLD = -0.5;

    /** Battery level considered full, stops charging to prevent overcharge (%) */
    private static final double BATTERY_FULL_LEVEL = 95.0;

    /** Battery level considered low, triggers grid purchase instead of discharge (%) */
    private static final double BATTERY_LOW_LEVEL = 15.0;

    /**
     * Constructs energy manager with required service dependencies.
     *
     * Initializes the energy management system with references to all
     * required subsystem services for energy monitoring and control.
     *
     * @param solarService Service providing solar energy production data
     * @param batteryService Service for battery charge/discharge operations
     * @param consumptionService Service providing energy consumption data
     */
    public EnergyManagerImpl( SolarService solarService, BatteryService batteryService, ConsumptionService consumptionService ) {
        this.solarService = solarService;
        this.batteryService = batteryService;
        this.consumptionService = consumptionService;
    }

    /**
     * Executes comprehensive energy optimization cycle.
     *
     * Analyzes current energy production, consumption, and storage levels
     * to make optimal decisions for energy distribution. Implements intelligent
     * logic for surplus/deficit handling with configurable thresholds.
     *
     * Optimization Steps:
     * 1. Verify system operational status
     * 2. Collect current energy data from all subsystems
     * 3. Calculate energy balance (production - consumption)
     * 4. Apply decision logic based on surplus/deficit thresholds
     * 5. Execute appropriate energy management actions
     * 6. Log optimization results for monitoring
     *
     * Error Handling:
     * Gracefully handles service unavailability and data collection failures
     * with comprehensive error logging and system status verification.
     */
    @Override
    public void optimizeEnergy() {
        try {
            // 1. Récupérer toutes les données avec vérifications
            if (!operational) {
                System.out.println("⚠️ System nicht betriebsbereit - Optimierung abgebrochen");
                return;
            }

            double production = solarService.getCurrentProduction();
            double consumption = consumptionService.getCurrentConsumption();
            double batteryLevel = batteryService.getCurrentLevel();

            // 2. Calculer le bilan énergétique
            double balance = production - consumption;

            // 3. Logique de décision intelligente
            if (balance >= SURPLUS_THRESHOLD) {
                // SURPLUS d'énergie
                handleSurplus(balance, batteryLevel);
            } else if (balance <= DEFICIT_THRESHOLD) {
                // DÉFICIT d'énergie
                handleDeficit(balance, batteryLevel);
            } else {
                // Situation ÉQUILIBRÉE
                System.out.println("⚖️ Ausgewogene Situation - keine Aktion erforderlich");
            }

            System.out.println("🔍 Analyse: Produktion=" + production + "kW, Verbrauch=" + consumption + "kW, Bilanz=" + balance + "kW, Batterie=" + batteryLevel + "%");

        }  catch ( Exception e ) {
            System.err.println("❌ Fehler bei der Energieoptimierung: " + e.getMessage());
            System.err.println("🔧 Überprüfen Sie, dass alle Services verfügbar sind");

        }

    }

    /**
     * Handles energy surplus situations with intelligent battery and grid management.
     *
     * Implements priority-based surplus energy allocation:
     * 1. Charge battery if not full (up to maximum charge rate)
     * 2. Sell excess energy to grid if battery is full
     *
     * @param surplus Amount of excess energy available (kW, positive value)
     * @param batteryLevel Current battery charge level (percentage 0-100)
     */
    private void handleSurplus(double surplus, double batteryLevel) {
        System.out.println("🌞 ÜBERSCHUSS erkannt: +" + surplus + "kW");

        if (batteryLevel < BATTERY_FULL_LEVEL) {
            // Batterie pas pleine → CHARGER
            double energyToCharge = Math.min(surplus, 2.0); // Max 2kW de charge
            boolean charged = batteryService.charge(energyToCharge);

            if (charged) {
                System.out.println("🔋⬆️ Batterie laden: +" + energyToCharge + "kW");
            } else {
                System.out.println("❌ Batterie-Ladevorgang fehlgeschlagen");
            }
        } else {
            // Batterie pleine → VENDRE au réseau
            System.out.println("💰 Batterie voll → Verkauf ans Netz: " + surplus + "kW");
            sellToGrid(surplus);
        }
    }

    /**
     * Handles energy deficit situations with battery discharge and grid purchase logic.
     *
     * Implements cost-optimized deficit energy sourcing:
     * 1. Discharge battery if sufficient charge available
     * 2. Purchase from grid if battery low or insufficient
     * 3. Hybrid approach: battery + grid if needed energy exceeds battery capacity
     *
     * @param deficit Amount of energy shortage (kW, negative value)
     * @param batteryLevel Current battery charge level (percentage 0-100)
     */
    private void handleDeficit(double deficit, double batteryLevel) {
        double neededEnergy = Math.abs(deficit); // Convertir en positif
        System.out.println("⚡ DEFIZIT erkannt: -" + neededEnergy + "kW");

        if (batteryLevel > BATTERY_LOW_LEVEL) {
            // Batterie disponible → DÉCHARGER
            double discharged = batteryService.discharge(neededEnergy);

            if (discharged > 0) {
                System.out.println("🔋⬇️ Batterie entladen: -" + discharged + "kW");

                // Si la batterie n'a pas pu fournir assez
                double remaining = neededEnergy - discharged;
                if (remaining > 0.1) { // Seuil de 0.1kW
                    System.out.println("🏪 Ergänzung aus Netz: " + remaining + "kW");
                    buyFromGrid(remaining);
                }
            } else {
                System.out.println("❌ Batterie nicht verfügbar → Kauf aus Netz: " + neededEnergy + "kW");
                buyFromGrid(neededEnergy);
            }
        } else {
            // Batterie faible → ACHETER au réseau
            System.out.println("🏪 Batterie schwach → Kauf aus Netz: " + neededEnergy + "kW");
            buyFromGrid(neededEnergy);
        }
    }

    /**
     * Simulates energy sale to electrical grid with economic calculation.
     *
     * @param energy Amount of energy to sell to grid (kW)
     */
    private void sellToGrid(double energy) {
        // Simulation - dans un vrai système, on communiquerait avec le réseau
        System.out.println("💸 Netzverkauf: " + energy + "kW → Guthaben: " + (energy * 0.15) + "€");
    }

    /**
     * Simulates energy purchase from electrical grid with cost calculation.
     *
     * @param energy Amount of energy to purchase from grid (kW)
     */
    private void buyFromGrid(double energy) {
        // Simulation - dans un vrai système, on communiquerait avec le réseau
        System.out.println("💳 Netzkauf: " + energy + "kW → Kosten: " + (energy * 0.25) + "€");
    }

    /**
     * Activates autonomous energy optimization mode with periodic execution.
     *
     * Starts timer-based automatic optimization cycles running every 5 seconds.
     * Includes safety checks to prevent multiple timer instances and ensures
     * optimizations only run when system is operational.
     *
     * Timer Management:
     * - Prevents duplicate timer creation
     * - Configurable optimization interval (5 seconds default)
     * - Automatic cleanup on mode deactivation
     */
    @Override
    public void startAutomaticMode() {
        // Éviter de démarrer plusieurs timers
        if (automaticMode) {
            return; // Déjà en mode automatique
        }

        // Activer le mode automatique
        automaticMode = true;

        // Créer et démarrer le timer
        optimizationTimer = new Timer();
        optimizationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (operational && automaticMode) {
                    optimizeEnergy(); // Appeler l'optimisation
                }
            }
        }, 0, 5000); // Démarrage immédiat, puis toutes les 5 secondes

        System.out.println("🤖 Automatik-Modus aktiviert - Optimierung alle 5 Sekunden");
    }

    /**
     * Deactivates autonomous optimization mode and stops all timers.
     *
     * Safely stops automatic optimization cycles with proper timer cleanup
     * to prevent resource leaks and unwanted background operations.
     *
     * Cleanup Operations:
     * - Sets automatic mode flag to false
     * - Cancels and nullifies optimization timer
     * - Provides confirmation logging
     */
    @Override
    public void stopAutomaticMode() {
        // Vérifier si on était déjà arrêté
        if (!automaticMode) {
            return; // Déjà arrêté
        }

        // Désactiver le mode automatique
        automaticMode = false;

        // Arrêter et nettoyer le timer
        if (optimizationTimer != null) {
            optimizationTimer.cancel();
            optimizationTimer = null;
        }

        System.out.println("⏹️ Automatik-Modus gestoppt");
    }

    /**
     * Generates comprehensive energy system status report.
     *
     * Collects real-time data from all subsystems and formats a detailed
     * status summary including production, consumption, storage, balance
     * calculations, and operational mode indicators.
     *
     * Status Information:
     * - Current solar production (kW)
     * - Total energy consumption (kW)
     * - Battery level and stored energy (% and kWh)
     * - Energy balance calculation (surplus/deficit)
     * - System status classification
     * - Automatic mode status
     *
     * @return Formatted status string with comprehensive energy metrics,
     *         or error message if data collection fails
     */
    @Override
    public String getEnergyStatus() {
        try {
            // Récupérer les données de tous les services
            double production = solarService.getCurrentProduction();
            double consumption = consumptionService.getCurrentConsumption();
            double batteryLevel = batteryService.getCurrentLevel();
            double batteryCapacity = batteryService.getCapacity();

            // Calculer le bilan énergétique
            double energyBalance = production - consumption;

            // Calculer l'énergie stockée en kWh
            double storedEnergy = (batteryLevel / 100.0) * batteryCapacity;

            // Déterminer le statut
            String status;
            if (energyBalance > 0.5) {
                status = "ÜBERSCHUSS";
            } else if (energyBalance < -0.5) {
                status = "DEFIZIT";
            } else {
                status = "AUSGEWOGEN";
            }

            // Formater le résumé complet
            return String.format(
                    "🌞 Produktion: %.1fkW | 🏠 Verbrauch: %.1fkW | 🔋 Batterie: %.0f%% (%.1f/%.1fkWh) | ⚡ Bilanz: %+.1fkW | 📊 Status: %s | 🤖 Auto: %s",
                    production,
                    consumption,
                    batteryLevel,
                    storedEnergy,
                    batteryCapacity,
                    energyBalance,
                    status,
                    automaticMode ? "EIN" : "AUS"
            );

        } catch (Exception e) {
            // Gestion d'erreur si un service n'est pas disponible
            return "❌ Fehler: Unmöglich, Energiedaten abzurufen - " + e.getMessage();
        }
    }

    /**
     * Returns the operational status of the energy management system.
     *
     * @return true if the energy manager is operational and ready to perform
     *         optimization operations
     */
    @Override
    public boolean isOperational() {
        return operational;
    }

    /**
     * Forces immediate execution of specified energy management action.
     *
     * Bypasses automatic optimization logic to execute specific actions
     * for testing, debugging, or manual control purposes. Supports
     * standard energy operations with predefined parameters.
     *
     * Supported Actions:
     * - "charge": Force battery charging at 2kW
     * - "discharge": Force battery discharge at 2kW
     * - "sell": Simulate grid energy sale of 1kW
     * - "buy": Simulate grid energy purchase of 1kW
     *
     * @param action String identifier for the energy action to execute
     * @return true if the specified action was successfully executed,
     *         false if action failed or is unknown
     */
    @Override
    public boolean forceAction(String action) {
        if (!operational) {
            return false;
        }

        switch (action.toLowerCase()) {
            case "charge":
                return batteryService.charge(2.0); // Force charge 2kW
            case "discharge":
                double discharged = batteryService.discharge(2.0);
                return discharged > 0;
            case "sell":
                sellToGrid(1.0); // Simule vente 1kW
                return true;
            case "buy":
                buyFromGrid(1.0); // Simule achat 1kW
                return true;
            default:
                System.out.println("❌ Unbekannte Aktion: " + action);
                return false;
        }
    }
}