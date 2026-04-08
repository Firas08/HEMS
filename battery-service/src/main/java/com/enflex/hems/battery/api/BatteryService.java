package com.enflex.hems.battery.api;

/**
 * Battery Energy Storage Management Service Interface
 *
 * This interface defines the contract for battery energy storage system
 * management within a Home Energy Management System (HEMS). Provides
 * comprehensive battery monitoring, charging, and discharging capabilities
 * for energy optimization and grid independence.
 *
 * Key Functions:
 * - Real-time battery state monitoring (level, capacity)
 * - Controlled energy charging and discharging operations
 * - System health and operational status verification
 * - Energy flow management for optimization algorithms
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 *
 */
public interface BatteryService {

    /**
     * Retrieves current battery charge level.
     *
     * @return Current battery level as percentage (0-100%)
     */
    double getCurrentLevel();

    /**
     * Retrieves maximum battery storage capacity.
     *
     * @return Maximum capacity in kilowatt-hours (kWh)
     */
    double getCapacity();

    /**
     * Charges the battery with specified energy amount.
     *
     * @param energy Energy to add in kilowatts (kW)
     * @return true if charging operation succeeded
     */
    boolean charge(double energy);

    /**
     * Discharges energy from the battery.
     *
     * @param energy Requested energy amount in kilowatts (kW)
     * @return Actual energy delivered in kilowatts (kW)
     */
    double discharge(double energy);

    /**
     * Checks battery system operational status.
     *
     * @return true if battery system is operational and functional
     */
    boolean isOperational();
}