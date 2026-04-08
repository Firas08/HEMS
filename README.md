# HEMS - Home Energy Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java">
  <img src="https://img.shields.io/badge/OSGi-7.0-blue.svg" alt="OSGi">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36.svg" alt="Maven">
</p>

**HEMS (Home Energy Management System)** is a modular system based on **OSGi architecture** designed to monitor, manage, and optimize energy production and consumption within a smart home.

## 🏗️ Project Architecture

The project is divided into several independent OSGi modules (bundles) to ensure loose coupling, maintainability, and flexibility:

- 🔋 **`battery-service`**: Manages the state, capacity, and charge/discharge cycles of the energy storage system.
- ☀️ **`solar-service`**: Monitors real-time solar energy production.
- ⚡ **`consumption-service`**: Measures and tracks the home's overall electricity consumption.
- 🧠 **`energy-manager`**: The system's central intelligent orchestrator. It manages energy flows by making smart decisions (e.g., storing solar surplus, discharging battery) and interacts with all other services.
- 🖥️ **`dashboard-web`**: Provides the visual interface (dashboard) for real-time energy monitoring and controls.
- 🔌 **`apis-service`**: Exposes data access points for potential external integrations.

## 🚀 How to Run

Running the project is incredibly straightforward directly from your IDE!

1. Open/Import the `energie-machin` project into your favorite IDE (IntelliJ IDEA, Eclipse, vsCode, etc.) as a Maven project.
2. Navigate to the `dashboard-web` module.
3. Locate the `WebDashboardLauncher` class at this path:
   ```text
   dashboard-web/src/main/java/com/enflex/hems/web/server/WebDashboardLauncher.java
   ```
4. Open the file, right-click, and select **Run** (or click the green 'Play' button next to the `main` method).

The backend services and the interactive dashboard will launch instantly!

## ⚙️ Build from Source (Optional)

If you make modifications to the source code and want to recompile all the bundles, simply run the following Maven command at the project root:

```bash
mvn clean install
```
