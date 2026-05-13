# Lynx Market Event Service 📈

A high-performance Java microservice for simulating and managing Market Events in an **Event-Driven Architecture**. The system supports both manual event triggering via administrators and automatic generation through a probabilistic engine.

## ✨ Core Features

- **Event Injection:** REST API for manual event injection by administrators.
- **Auto-Simulation:** A "Dice Roll" engine that generates random events based on configurable probabilities.
- **Conflict Prevention:** Validation mechanism that prevents overlapping simultaneous MARKET-wide events.
- **Automated Lifecycle:** Automatic event expiration system based on a set number of "ticks" (minutes).
- **Kafka Integration:** Publishes events to the `market.events.active` topic for external consumers (Frontend/Trading Services).

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot 3.x** (JPA, Web, Scheduling)
- **Apache Kafka** (Asynchronous Messaging)
- **H2 Database** (In-memory storage for state persistence)
- **Docker & Docker Compose** (Local Infrastructure)
- **Lombok & SLF4J** (Clean code and logging)

## 🚀 Getting Started

### 1. Infrastructure
Ensure Docker Desktop is running, then execute:
```bash
docker-compose up -d
```
This command starts the Kafka broker and the **Kafka UI** at `http://localhost:8090`.

### 2. Application
Run the project from IntelliJ IDEA or via Gradle:
```bash
./gradlew bootRun
```
The application will start on port `8081`.

## 📖 API Documentation

### Trigger Event (Manual)
**POST** `/api/v1/admin/events/trigger`

```json
{
  "event_type": "BEAR_CRASH",
  "scope": "SECTOR",
  "target": "TECH",
  "magnitude": 2.5,
  "duration_ticks": 10
}
```

## ⚙️ Configuration (application.yml)
All simulation parameters are centralized:
- `event-probability-percent`: Probability of an automatic event appearing.
- `auto-scheduler-rate`: Frequency of the "Dice Roll".
- `available-sectors/stocks`: Lists of available targets for simulation.

---
Developed by **Team Lynx** - 2026