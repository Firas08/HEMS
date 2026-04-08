package com.enflex.hems.battery.impl;

import com.enflex.hems.battery.api.BatteryService;

/**
 * Implementation of Battery Energy Storage Management Service
 *
 * This implementation provides a realistic battery simulation with configurable
 * capacity, charge/discharge operations, and state management. Designed for
 * home energy storage systems with lithium-ion battery characteristics.
 *
 * Battery Configuration:
 * - Maximum capacity: 10 kWh (typical residential battery)
 * - Initial charge level: 50%
 * - Operational status monitoring
 * - Realistic charge/discharge limitations
 *
 * Features:
 * - Percentage-based level tracking (0-100%)
 * - Energy conversion between kW and percentage
 * - Overcharge and over-discharge protection
 * - Operational status verification
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
public class BatteryServiceImpl implements BatteryService {

    /** Maximum battery capacity in kilowatt-hours (kWh) */
    private static final double MAX_CAPACITY = 10.0; // 10 kWh

    /** Current battery charge level as percentage (0-100%) */
    private double currentLevel = 50.0; // Commence à 50%

    /** Operational status flag for battery system */
    private boolean operational = true;

    /**
     * Retrieves current battery charge level.
     *
     * @return Current charge level as percentage (0-100%)
     */
    @Override
    public double getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Retrieves maximum battery storage capacity.
     *
     * @return Maximum capacity in kilowatt-hours (kWh)
     */
    @Override
    public double getCapacity() {
        return MAX_CAPACITY;
    }

    /**
     * Charges the battery with specified energy amount.
     *
     * Converts energy from kW to percentage and adds to current level
     * with overcharge protection. Respects operational status and
     * maximum capacity limitations.
     *
     * @param energy Energy to add in kilowatts (kW)
     * @return true if charging operation succeeded, false if battery
     *         is non-operational or already at full capacity
     */
    @Override
    public boolean charge(double energy) {
        if (!operational) {
            return false; //Batterie en panne

        } else if (currentLevel >=100.0) {
            return false; // batterie est full
        }
        // Convertir energy (kW) en pourcentage
        double energyPercent = (energy / MAX_CAPACITY) * 100.0;

        // Ajouter l'énergie sans dépasser 100%
        currentLevel = Math.min(100.0, currentLevel + energyPercent);

        return true; // Chargement réussi
    }

    /**
     * Discharges energy from the battery.
     *
     * Calculates available energy based on current level and provides
     * the minimum between requested and available energy. Includes
     * over-discharge protection and operational status verification.
     *
     * @param energy Requested energy amount in kilowatts (kW)
     * @return Actual energy delivered in kilowatts (kW), may be less
     *         than requested if insufficient charge available
     */
    @Override
    public double discharge(double energy) {
        if (!operational|| currentLevel<=0.0) {
            return 0.0;
        }
        // Convertir level actuel en kWh
        double availableEnergy = (currentLevel / 100.0) * MAX_CAPACITY;

        // Prendre le minimum entre demandé et disponible
        double actualEnergy = Math.min(energy, availableEnergy);

        // Convertir l'énergie prise en pourcentage et l'enlever
        double energyPercent = (actualEnergy / MAX_CAPACITY) * 100.0;
        currentLevel = Math.max(0.0, currentLevel - energyPercent);

        return actualEnergy; // Énergie réellement fournie

    }

    /**
     * Checks battery system operational status.
     *
     * @return true if battery system is operational and functional
     */
    @Override
    public boolean isOperational() {
        return operational;
    }

}