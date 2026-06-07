# Betting AI Verification

> Intelligent sports betting verification system powered by LLM analysis and batch data processing.

## Overview

A web application that automates the verification of sports betting transactions using AI. The system detects suspicious patterns, insider information usage, and match-fixing attempts — replacing manual review with automated risk assessment.

**Built for:** Bookmaker companies that process large volumes of bets and need scalable, consistent risk management.

## The Problem

Traditional bet administration relies on manual moderation or simple rule-based filters. These approaches:
- Don't scale with transaction volume
- Miss complex behavioral patterns
- Produce inconsistent decisions (human factor)
- Can't interpret semantic context of a bet (e.g. a final vs. an obscure regional match)

## Solution

This system introduces a two-stage pipeline:

1. **Data preparation** — CSV import, validation, normalization, and aggregation of bet/user/match data into a structured analytical context
2. **AI audit** — the context is sent to an LLM (Groq API) which returns a risk classification and a human-readable reasoning

Risk levels: `LOW` · `MEDIUM` · `HIGH`

## Architecture

```
Admin UI (Web)
     │
     ▼
Spring Boot Backend
     ├── CSV batch import & validation
     ├── Analytical context builder
     ├── Groq LLM integration (AI audit)
     ├── Redis (caching)
     └── Background job: live match sync
          │
          ├── MySQL (bets, users, matches, results)
          └── football-data.org API (live match data)
```

**Key entities:**
- `Bet` — financial transaction with amount, outcome, timestamp
- `User` — registered player with balance, bet history, and status (active / restricted / blocked)
- `Match` — sport event sourced from an external provider (unique external ID)
- `BetAnalysisContext` — consolidated payload sent to the LLM for analysis
- `RiskLevel` — result of verification: LOW / MEDIUM / HIGH

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Batch |
| Database | MySQL (JPA/Hibernate) |
| Cache | Redis |
| AI integration | Groq API (LLM) |
| Sports data | football-data.org API v4 |
| HTTP client | Spring WebClient (reactive) |
| Containerization | Docker, Docker Compose |
| API | REST (JSON) |
| Frontend | HTML / CSS / JavaScript |
| Data input | CSV batch import |

## Features

- **Batch CSV import** — upload large datasets of bets for bulk processing
- **AI-powered risk classification** — each bet gets a risk level + explanation from the LLM
- **Live match sync** — background job keeps sport event data up to date 24/7
- **Admin dashboard** — monitor flagged bets, manage user statuses, review AI reasoning
- **Anomaly detection** — catches insider info usage, unusual financial activity, match-fixing patterns

## Performance Metrics

The system tracks three KPI groups:

- **Throughput (T)** — bets processed per unit time during import
- **Automation rate (K_auto)** — % of bets classified as LOW risk (no admin action needed)
- **AI success rate (SR)** — % of successful LLM API calls (vs timeouts / rate limit errors)

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Groq API key — free tier at [console.groq.com](https://console.groq.com)
- football-data.org API key — free tier at [football-data.org](https://www.football-data.org)

### Setup

```bash
git clone https://github.com/NaoriYchiha/betting-ai-verification.git
cd betting-ai-verification
```

Set your API keys in the backend environment (via `application.properties` file):

```env
GROQ_API_KEY=your_groq_api_key
FOOTBALL_API_KEY=your_football_data_api_key
```

### Run with Docker

```bash
docker compose up --build
```

This starts three containers:

| Container | Port | Description |
|---|---|---|
| `mysql_db` | 3307 | MySQL database (`bettingAi`) |
| `redis_cache` | 6379 | Redis cache |
| `app_backend` | 8080 | Spring Boot application |

App will be available at `http://localhost:8080`.

### Import bets

Upload a CSV file with bet data via the admin UI or REST endpoint. The system will validate, process, and run AI verification on each transaction.

## External Integrations

### football-data.org API

Live match data is fetched from [football-data.org](https://www.football-data.org) (v4) using Spring WebClient. The background job retrieves upcoming matches for the next 7 days and keeps the database in sync automatically.

```
GET https://api.football-data.org/v4/matches?dateFrom=TODAY&dateTo=TODAY+7d
```

Authentication: `X-Auth-Token` header. Free tier is sufficient for development and testing.

### Groq API (LLM)

Each bet's analytical context (user profile, match details, bet parameters) is sent to Groq's inference API for risk classification. The model returns a structured verdict: risk level (`LOW` / `MEDIUM` / `HIGH`) and a plain-text explanation for the administrator. Free tier with fast inference — no payment required.

## Project Background

Developed as a bachelor's thesis (2026) at a KhPI. The research analyzed the business process of bet administration, identified weaknesses in manual and rule-based approaches, and proposed an AI-augmented TO-BE model with formal KPI evaluation.

## Author

**Bohdan Lytvynovych** — [@NaoriYchiha](https://github.com/NaoriYchiha)  
Bachelor's degree project · Computer Science · 2026
