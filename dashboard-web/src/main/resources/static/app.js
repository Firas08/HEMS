/**
 * HEMS Dashboard - JavaScript Application
 *
 * Modern web dashboard application for Home Energy Management System (HEMS)
 * real-time monitoring and control. Provides interactive interface for energy
 * production, consumption, battery status, and system optimization control.
 *
 * Features:
 * - Real-time data updates via REST API integration
 * - Interactive energy system controls (auto mode, manual optimization)
 * - Animated UI elements with smooth transitions
 * - Responsive design with glassmorphism aesthetics
 * - Live connection status monitoring
 * - Energy balance calculations and cost analysis
 *
 * Architecture:
 * - Class-based ES6 structure for maintainability
 * - Fetch API for REST endpoint communication
 * - Event-driven updates with automatic polling
 * - Modular component updates with error handling
 *
 * @author EnFlex.IT Development Team
 * @version 1.0
 * @since 2025-01-05
 */
class HEMSDashboard {
    /**
     * Constructs HEMS dashboard application with default configuration.
     *
     * Initializes dashboard state, API endpoints, and starts automatic
     * initialization sequence including UI setup, data loading, and
     * event listener registration.
     */
    constructor() {
        this.isAutoMode = false;
        this.updateInterval = null;
        this.apiBaseUrl = '/api/energy';

        this.init();
    }

    /**
     * Initializes complete dashboard application.
     *
     * Performs sequential initialization of all dashboard components:
     * UI setup, initial data loading, automatic updates, event handlers,
     * and live clock functionality.
     */
    init() {
        console.log('🚀 HEMS Dashboard wird initialisiert');

        // Initialiser l'interface
        this.initializeUI();

        // Charger les données initiales
        this.loadInitialData();

        // Démarrer les mises à jour automatiques
        this.startAutoUpdate();

        // Initialiser les event listeners
        this.initializeEventListeners();

        // Démarrer l'horloge
        this.startClock();

        console.log('✅ Dashboard erfolgreich initialisiert');
    }

    /**
     * Initializes user interface elements and animations.
     *
     * Sets up loading overlay transitions and triggers entrance
     * animations for dashboard cards with staggered timing.
     */
    initializeUI() {
        // Masquer l'overlay de chargement après l'initialisation
        setTimeout(() => {
            const loadingOverlay = document.getElementById('loadingOverlay');
            if (loadingOverlay) {
                loadingOverlay.classList.remove('show');
            }
        }, 1500);

        // Animer l'apparition des cards
        this.animateCardsEntrance();
    }

    /**
     * Animates dashboard cards entrance with staggered timing.
     *
     * Creates smooth fade-in and slide-up animation for all energy
     * cards with 100ms delay between each card for visual appeal.
     */
    animateCardsEntrance() {
        const cards = document.querySelectorAll('.energy-card');
        cards.forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';

            setTimeout(() => {
                card.style.transition = 'all 0.6s ease-out';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });
    }

    /**
     * Loads initial dashboard data from HEMS API.
     *
     * Performs first data fetch with loading indicator and error
     * handling for system connectivity verification.
     */
    async loadInitialData() {
        try {
            this.showLoading('Laden der Anfangsdaten...');
            await this.updateAllData();
            this.hideLoading();
        } catch (error) {
            console.error('Fehler beim initialen Laden:', error);
            this.showError('Verbindungsfehler zum Energiesystem');
        }
    }

    /**
     * Updates all dashboard data sections from API endpoints.
     *
     * Fetches comprehensive energy metrics and updates solar, battery,
     * consumption, and system status displays. Includes error handling
     * and connection status management.
     */
    async updateAllData() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/metrics`);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();

            // Mettre à jour chaque section
            this.updateSolarData(data.solar || {});
            this.updateBatteryData(data.battery || {});
            this.updateConsumptionData(data.consumption || {});
            this.updateSystemStatus();

            // Calculer et afficher le bilan énergétique
            this.updateEnergyBalance(data);

        } catch (error) {
            console.error('Fehler bei Datenaktualisierung:', error);
            this.updateConnectionStatus(false);
        }
    }

    /**
     * Updates solar energy production display elements.
     *
     * Refreshes solar production data including current output, daily
     * generation, system efficiency, operational status, and location
     * information with smooth animations.
     *
     * @param {Object} solarData Solar metrics from API response
     */
    updateSolarData(solarData) {
        const production = solarData.current_production || 0;
        const daily = solarData.daily_production || 0;
        const efficiency = solarData.efficiency || 0;
        const status = solarData.status || 'UNKNOWN';
        const city = solarData.city || 'Essen';

        // Mise à jour des valeurs
        this.updateElement('solarProduction', production.toFixed(1));
        this.updateElement('solarDaily', `${daily.toFixed(1)} kWh`);
        this.updateElement('solarEfficiency', Math.round(efficiency));
        this.updateElement('solarStatus', status);
        this.updateElement('solarCity', city); // ← NOUVEAU : Afficher la ville !

        // Mise à jour du progress circulaire
        this.updateCircularProgress('solarProgress', efficiency);

        // Animation de la valeur
        this.animateValue('solarProduction', production);
    }

    /**
     * Updates battery storage system display elements.
     *
     * Refreshes battery data including charge level, capacity utilization,
     * charging status, and system health with visual battery indicator.
     *
     * @param {Object} batteryData Battery metrics from API response
     */
    updateBatteryData(batteryData) {
        const level = batteryData.level_percent || 0;
        const currentKwh = batteryData.current_kwh || 0;
        const capacityKwh = batteryData.capacity_kwh || 10;
        const isCharging = batteryData.charging || false;
        const health = batteryData.health || 100;

        // Mise à jour des valeurs
        this.updateElement('batteryLevel', Math.round(level));
        this.updateElement('batteryCapacity', `${currentKwh.toFixed(1)} / ${capacityKwh.toFixed(1)} kWh`);
        this.updateElement('batteryHealth', `${health}%`);
        this.updateElement('batteryStatus', isCharging ? 'LADEN' : 'ENTLADEN');

        // Mise à jour de la visualisation batterie
        this.updateBatteryVisual(level);

        // Animation de la valeur
        this.animateValue('batteryLevel', level);
    }

    /**
     * Updates energy consumption display elements.
     *
     * Refreshes consumption data including current usage, hourly predictions,
     * and active appliance count with smooth value animations.
     *
     * @param {Object} consumptionData Consumption metrics from API response
     */
    updateConsumptionData(consumptionData) {
        const current = consumptionData.current_consumption || 0;
        const prediction = consumptionData.prediction_next_hour || 0;
        const appliances = consumptionData.active_appliances || 0;

        // Mise à jour des valeurs
        this.updateElement('currentConsumption', current.toFixed(1));
        this.updateElement('consumptionPrediction', `${prediction.toFixed(1)} kW`);
        this.updateElement('activeAppliances', appliances);

        // Animation de la valeur
        this.animateValue('currentConsumption', current);
    }

    /**
     * Updates energy balance calculations and cost analysis.
     *
     * Calculates energy surplus/deficit status, daily costs, and potential
     * savings based on production vs consumption differential.
     *
     * @param {Object} data Complete energy system data
     */
    updateEnergyBalance(data) {
        const production = data.solar?.current_production || 0;
        const consumption = data.consumption?.current_consumption || 0;
        const balance = production - consumption;

        let status, className;
        if (balance > 0.5) {
            status = 'ÜBERSCHUSS';
            className = 'surplus';
        } else if (balance < -0.5) {
            status = 'DEFIZIT';
            className = 'deficit';
        } else {
            status = 'AUSGEGLICHEN';
            className = 'balanced';
        }

        // Mise à jour des éléments
        this.updateElement('energyBalance', `${balance >= 0 ? '+' : ''}${balance.toFixed(1)} kW`);
        this.updateElement('balanceStatus', status);

        // Simuler coûts et économies
        const cost = Math.max(0, -balance * 0.25 * 24 / 1000);
        const savings = Math.max(0, balance * 0.15 * 24 / 1000);

        this.updateElement('costToday', `${cost.toFixed(2)}€`);
        this.updateElement('savingsToday', `${savings.toFixed(2)}€`);
    }

    /**
     * Updates system operational status indicators.
     *
     * Refreshes system status display, automatic mode indicators,
     * and connection status with appropriate visual feedback.
     */
    updateSystemStatus() {
        const statusElement = document.getElementById('systemStatus');
        const aiStatusElement = document.getElementById('aiStatus');

        if (statusElement) {
            statusElement.textContent = 'System betriebsbereit';
        }

        if (aiStatusElement) {
            aiStatusElement.textContent = this.isAutoMode ?
                '🤖 Automatische Optimierung läuft...' :
                '⏸️ Manueller Modus - Bereit zur Optimierung';
        }

        this.updateConnectionStatus(true);
    }

    /**
     * Updates connection status indicator.
     *
     * @param {boolean} isConnected Connection state to display
     */
    updateConnectionStatus(isConnected) {
        const statusElement = document.getElementById('connectionStatus');
        const dotElement = statusElement?.querySelector('.connection-dot');

        if (statusElement && dotElement) {
            statusElement.querySelector('span').textContent = isConnected ? 'Verbunden' : 'Getrennt';
            dotElement.style.background = isConnected ? 'var(--success)' : 'var(--danger)';
        }
    }

    /**
     * Updates circular progress indicator.
     *
     * @param {string} elementId Target element ID
     * @param {number} percentage Progress percentage (0-100)
     */
    updateCircularProgress(elementId, percentage) {
        const element = document.getElementById(elementId);
        const circle = element?.querySelector('.progress-circle');

        if (circle) {
            const degrees = (percentage / 100) * 360;
            circle.style.background = `conic-gradient(var(--solar-primary) ${degrees}deg, var(--bg-secondary) 0deg)`;
        }
    }

    /**
     * Updates visual battery level indicator.
     *
     * @param {number} percentage Battery level percentage (0-100)
     */
    updateBatteryVisual(percentage) {
        const batteryFill = document.getElementById('batteryFill');
        if (batteryFill) {
            batteryFill.style.height = `${percentage}%`;

            // Couleur selon le niveau
            let color;
            if (percentage > 60) {
                color = 'var(--success)';
            } else if (percentage > 30) {
                color = 'var(--warning)';
            } else {
                color = 'var(--danger)';
            }
            batteryFill.style.background = `linear-gradient(to top, ${color}, var(--battery-secondary))`;
        }
    }

    /**
     * Animates numeric value transitions.
     *
     * @param {string} elementId Target element for animation
     * @param {number} targetValue Final value to animate to
     */
    animateValue(elementId, targetValue) {
        const element = document.getElementById(elementId);
        if (!element) return;

        const startValue = parseFloat(element.textContent) || 0;
        const diff = targetValue - startValue;
        const steps = 30;
        const stepValue = diff / steps;
        let current = startValue;
        let step = 0;

        const animation = setInterval(() => {
            step++;
            current += stepValue;

            if (step >= steps) {
                current = targetValue;
                clearInterval(animation);
            }

            element.textContent = current.toFixed(1);
        }, 16); // 60 FPS
    }

    /**
     * Initializes all dashboard event listeners.
     *
     * Sets up click handlers for control buttons, action buttons,
     * and window resize handling for responsive behavior.
     */
    initializeEventListeners() {
        // Bouton mode automatique
        const autoBtn = document.getElementById('autoModeBtn');
        autoBtn?.addEventListener('click', () => this.toggleAutoMode());

        // Bouton optimisation manuelle
        const optimizeBtn = document.getElementById('optimizeBtn');
        optimizeBtn?.addEventListener('click', () => this.manualOptimize());

        // Boutons d'actions rapides
        document.querySelectorAll('.action-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const action = e.target.closest('.action-btn').dataset.action;
                this.executeAction(action);
            });
        });

        // Gestion du redimensionnement
        window.addEventListener('resize', () => this.handleResize());
    }

    /**
     * Toggles automatic optimization mode.
     *
     * Sends control command to API and updates UI state based on
     * current automatic mode status.
     */
    async toggleAutoMode() {
        try {
            const action = this.isAutoMode ? 'auto_stop' : 'auto_start';
            const response = await fetch(`${this.apiBaseUrl}/control?action=${action}`);

            if (response.ok) {
                this.isAutoMode = !this.isAutoMode;
                this.updateAutoModeButton();
                this.showNotification(
                    this.isAutoMode ? 'Automatischer Modus aktiviert' : 'Automatischer Modus deaktiviert',
                    'success'
                );
            }
        } catch (error) {
            console.error('Fehler beim Auto-Modus-Wechsel:', error);
            this.showNotification('Kommunikationsfehler', 'error');
        }
    }

    /**
     * Triggers manual energy optimization.
     *
     * Sends optimization command to API and schedules data refresh
     * to reflect optimization results.
     */
    async manualOptimize() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/control?action=optimize`);

            if (response.ok) {
                this.showNotification('Optimierung gestartet', 'success');
                // Mettre à jour immédiatement les données
                setTimeout(() => this.updateAllData(), 1000);
            }
        } catch (error) {
            console.error('Fehler bei manueller Optimierung:', error);
            this.showNotification('Optimierungsfehler', 'error');
        }
    }

    /**
     * Executes quick action command.
     *
     * @param {string} action Action identifier to execute
     */
    async executeAction(action) {
        try {
            const response = await fetch(`${this.apiBaseUrl}/control?action=${action}`);

            if (response.ok) {
                this.showNotification(`Aktion ${action} ausgeführt`, 'success');
                setTimeout(() => this.updateAllData(), 1000);
            }
        } catch (error) {
            console.error(`Fehler bei Aktion ${action}:`, error);
            this.showNotification(`Fehler bei Aktion ${action}`, 'error');
        }
    }

    /**
     * Updates automatic mode button appearance.
     *
     * Changes button text and styling based on current automatic
     * mode state for clear user feedback.
     */
    updateAutoModeButton() {
        const autoBtn = document.getElementById('autoModeBtn');
        if (autoBtn) {
            autoBtn.textContent = this.isAutoMode ? '⏸️ Auto Stoppen' : '🤖 Auto Modus';
            autoBtn.classList.toggle('active', this.isAutoMode);
        }
    }

    /**
     * Starts live clock display with German locale formatting.
     *
     * Updates time display every second with localized time format
     * for user interface enhancement.
     */
    startClock() {
        const updateTime = () => {
            const now = new Date();
            const timeString = now.toLocaleTimeString('de-DE');
            this.updateElement('currentTime', timeString);
        };

        updateTime();
        setInterval(updateTime, 1000);
    }

    /**
     * Starts automatic data updates with 10-second intervals.
     *
     * Establishes periodic data refresh for real-time dashboard
     * updates with optimized polling frequency.
     */
    startAutoUpdate() {
        // Mise à jour toutes les 10 secondes (plus réaliste)
        this.updateInterval = setInterval(() => {
            this.updateAllData();
        }, 10000); // ← 10 secondes au lieu de 3
    }

    /**
     * Stops automatic data updates.
     *
     * Clears update interval to prevent unnecessary API calls
     * when dashboard is not active.
     */
    stopAutoUpdate() {
        if (this.updateInterval) {
            clearInterval(this.updateInterval);
            this.updateInterval = null;
        }
    }

    // Utility Methods

    /**
     * Updates DOM element content safely.
     *
     * @param {string} id Element ID to update
     * @param {string} content New content to display
     */
    updateElement(id, content) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = content;
        }
    }

    /**
     * Shows loading overlay with custom message.
     *
     * @param {string} message Loading message to display
     */
    showLoading(message = 'Laden...') {
        const overlay = document.getElementById('loadingOverlay');
        const text = overlay?.querySelector('.loading-text');

        if (overlay) {
            overlay.classList.add('show');
        }
        if (text) {
            text.textContent = message;
        }
    }

    /**
     * Hides loading overlay.
     */
    hideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.classList.remove('show');
        }
    }

    /**
     * Shows notification message.
     *
     * @param {string} message Notification content
     * @param {string} type Notification type (info, success, error)
     */
    showNotification(message, type = 'info') {
        // Simple console log pour l'instant
        console.log(`${type.toUpperCase()}: ${message}`);

        // TODO: Implémenter un système de notifications visuelles
    }

    /**
     * Shows error message and updates connection status.
     *
     * @param {string} message Error message to display
     */
    showError(message) {
        console.error('Dashboard Error:', message);
        this.updateConnectionStatus(false);
    }

    /**
     * Handles window resize events for responsive design.
     */
    handleResize() {
        // Gestion du responsive si nécessaire
        console.log('Window resized');
    }
}

// Dashboard initialization when page loads
document.addEventListener('DOMContentLoaded', () => {
    console.log('🌐 Seite geladen, Dashboard wird initialisiert...');

    // Afficher l'overlay de chargement
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.classList.add('show');
    }

    // Initialiser le dashboard
    window.hemsDashboard = new HEMSDashboard();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (window.hemsDashboard) {
        window.hemsDashboard.stopAutoUpdate();
    }
});