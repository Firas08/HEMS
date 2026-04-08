package com.enflex.hems.manager.api;

/**
 * Energy Management System Controller Interface
 *
 * Main service for intelligent energy management that coordinates all subsystems
 * to optimize energy flows within a Home Energy Management System (HEMS).
 * Acts as the central decision-making component for energy distribution,
 * storage, and grid interaction strategies.
 *
 * This service orchestrates:
 * - Solar energy production monitoring
 * - Battery charge/discharge decisions
 * - Grid energy trading operations
 * - Load consumption optimization
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 * @see com.enflex.hems.solar.api.SolarService
 * @see com.enflex.hems.battery.api.BatteryService
 * @see com.enflex.hems.consumption.api.ConsumptionService
 */
public interface EnergyManager {

    /**
     * Executes a single energy optimization cycle.
     *
     * Analyzes current energy situation across all subsystems and makes
     * optimal decisions for energy distribution. Considers production,
     * consumption, storage levels, and economic factors.
     */
    void optimizeEnergy();

    /**
     * Starts continuous automatic energy optimization.
     *
     * Enables autonomous operation mode with periodic optimization cycles.
     * System will automatically optimize energy flows at regular intervals
     * without manual intervention.
     */
    void startAutomaticMode();

    /**
     * Stops automatic energy optimization mode.
     *
     * Returns control to manual operation, stopping all automated
     * optimization cycles and decisions.
     */
    void stopAutomaticMode();

    /**
     * Retrieves comprehensive energy system status.
     *
     * Provides current state summary including production, consumption,
     * storage levels, and active operations for monitoring and debugging.
     *
     * @return Human-readable energy system status summary
     */
    String getEnergyStatus();

    /**
     * Checks operational status of the energy management system.
     *
     * Verifies that all required subsystems are available and the
     * energy manager can perform optimization operations successfully.
     *
     * @return true if energy manager is fully operational
     */
    boolean isOperational();

    /**
     * Forces execution of a specific energy management action.
     *
     * Overrides automatic optimization logic to execute a particular
     * action immediately. Primarily used for testing, debugging,
     * and demonstration purposes.
     *
     * @param action Energy action to execute: "charge", "discharge", "buy", "sell"
     * @return true if the specified action was successfully executed
     */
    boolean forceAction(String action);
}