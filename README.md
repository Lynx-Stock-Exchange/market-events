# Lynx Market Event Service 📈

Microserviciu Java performant pentru simularea și gestionarea evenimentelor de piață (Market Events) într-o arhitectură de tip **Event-Driven**. Sistemul permite atât declanșarea manuală a crizelor/oportunităților financiare, cât și generarea lor automată folosind un motor probabilistic.

## ✨ Funcționalități Principale

- **Event Injection:** API REST pentru injectarea manuală a evenimentelor de către administratori.
- **Auto-Simulation:** Motor de tip "Dice Roll" care generează evenimente aleatorii bazate pe probabilități configurabile.
- **Conflict Prevention:** Mecanism de validare care previne suprapunerea a două evenimente globale (MARKET-wide) simultane.
- **Automated Lifecycle:** Sistem de expirare automată a evenimentelor după un număr setat de "ticks" (minute).
- **Kafka Integration:** Publicarea evenimentelor pe topic-ul `market.events.active` pentru consumatori externi (Frontend/Trading Services).

## 🛠 Tehnologii Folosite

- **Java 21**
- **Spring Boot 3.x** (JPA, Web, Scheduling)
- **Apache Kafka** (Mesagerie asincronă)
- **H2 Database** (Stocare în memorie pentru persistența stării)
- **Docker & Docker Compose** (Infrastructură locală)
- **Lombok & SLF4J** (Cod curat și logging)

## 🚀 Pornirea Proiectului

### 1. Infrastructură
Asigurați-vă că aveți Docker Desktop pornit, apoi rulați:
```bash
docker-compose up -d
```
Această comandă va porni broker-ul Kafka și interfața vizuală **Kafka UI** la adresa `http://localhost:8090`.

### 2. Aplicația
Rulați proiectul din IntelliJ IDEA sau folosind Gradle:
```bash
./gradlew bootRun
```
Aplicația va porni pe portul `8081`.

## 📖 Ghid de Utilizare API

### Declanșare Eveniment (Manual)
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

## ⚙️ Configurare (application.yml)
Toți parametrii simulării sunt centralizați:
- `event-probability-percent`: Șansa de apariție a unui eveniment automat.
- `auto-scheduler-rate`: Cât de des se aruncă "zarul".
- `available-sectors/stocks`: Listele de ținte disponibile pentru simulare.

---
Developed by **Team Lynx** - 2026