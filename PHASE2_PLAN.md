# 🚀 SCIP Phase 2 — Enterprise Upgrade Plan
## Taking the project from 8.2 → 9.5/10

---

## Phase 2 Delivery Plan

### Sprint 1 — DevOps Foundation (Week 1)
| Task | Files | Impact |
|---|---|---|
| Docker Compose — all 4 services | docker-compose.yml | Recruiters can run with 1 command |
| Dockerfile for Java backend | Dockerfile | Containerised backend |
| Dockerfile for Python AI | ai-service/Dockerfile | Containerised AI |
| Dockerfile for React web | scweb/Dockerfile | Containerised frontend |
| GitHub Actions CI | .github/workflows/ci.yml | Auto-build + test on every push |
| GitHub Actions CD | .github/workflows/cd.yml | Auto-deploy on merge to main |

### Sprint 2 — Database Upgrade (Week 1-2)
| Task | Files | Impact |
|---|---|---|
| PostgreSQL config | application-prod.properties | Production-ready DB |
| Flyway migrations | src/main/resources/db/migration/ | Schema versioning |
| Docker Compose PostgreSQL service | docker-compose.yml | DB in container |
| H2 kept for dev profile | application-dev.properties | Fast local dev |

### Sprint 3 — Redis Caching (Week 2)
| Task | Files | Impact |
|---|---|---|
| Redis config | RedisConfig.java | Cache setup |
| Dashboard API caching | DashboardController.java | 10x faster dashboard |
| Supplier list caching | SupplierRestController.java | Cached with TTL |
| Cache invalidation on writes | CostRecordService.java | Smart cache busting |

### Sprint 4 — WebSocket Real-time Alerts (Week 2-3)
| Task | Files | Impact |
|---|---|---|
| WebSocket config | WebSocketConfig.java | Real-time connection |
| Alert publisher | AlertWebSocketService.java | Push alerts to browser |
| React WebSocket hook | useAlerts.js | Live alert updates |
| Dashboard live updates | DashboardPage.js | Real-time KPI changes |

### Sprint 5 — Apache Artemis MQ (Week 3)
| Task | Files | Impact |
|---|---|---|
| Artemis config | ArtemisConfig.java | Message broker setup |
| Cost approval events | CostApprovalPublisher.java | Event-driven approval |
| Alert consumer | AlertConsumer.java | MQ-driven alert creation |
| Dead letter queue | DLQConfig.java | Failed message handling |

### Sprint 6 — Live Demo Deployment (Week 3-4)
| Task | Files | Impact |
|---|---|---|
| Railway/Render deployment | railway.json | Public URL for demo |
| Environment variables | .env.example | Secure config |
| Health check endpoints | HealthController.java | Monitoring ready |
| Production README | README.md | Live demo link |

---

## Architecture After Phase 2

```
GitHub Push → GitHub Actions CI
                    ↓
              Run 36 Playwright Tests
                    ↓
              Build Docker Images
                    ↓
              Push to Registry
                    ↓
              Deploy to Railway/Render

Production Architecture:
┌─────────────────────────────────────────────┐
│  React Web App (Docker)                     │
│  Nginx reverse proxy                        │
└──────────────┬──────────────────────────────┘
               │ WebSocket + REST
               ↓
┌─────────────────────────────────────────────┐
│  Spring Boot API (Docker)                   │
│  ├── Redis Cache (15min TTL)                │
│  ├── Apache Artemis MQ                      │
│  │   ├── cost.approval.queue                │
│  │   ├── supplier.alert.queue               │
│  │   └── notification.queue                 │
│  └── WebSocket /ws/alerts                   │
└──────┬──────────────────────┬───────────────┘
       │ JPA                  │ HTTP
       ↓                      ↓
┌──────────────┐    ┌─────────────────────────┐
│  PostgreSQL  │    │  Python FastAPI AI       │
│  (Docker)    │    │  IsolationForest ML      │
│              │    │  Claude AI               │
└──────────────┘    └─────────────────────────┘
```

---

## Estimated Score After Each Sprint

| After Sprint | Score | Key Addition |
|---|---|---|
| Current | 8.2/10 | H2, no containers, manual start |
| Sprint 1 (Docker + CI) | 8.7/10 | One command startup, auto CI |
| Sprint 2 (PostgreSQL) | 8.9/10 | Production-ready DB |
| Sprint 3 (Redis) | 9.1/10 | Performance layer |
| Sprint 4 (WebSocket) | 9.3/10 | Real-time architecture |
| Sprint 5 (Artemis MQ) | 9.5/10 | Enterprise messaging |
| Sprint 6 (Live Demo) | 9.7/10 | Publicly accessible demo |

---

## What Each Addition Proves to Interviewers

### Docker Compose
> "I can containerise a 4-service application and make it reproducible across environments"

### GitHub Actions CI
> "My code is always tested. Every push runs 36 E2E tests automatically"

### PostgreSQL + Flyway
> "I understand database migrations, schema versioning, and production DB management"

### Redis Caching
> "I think about performance. Dashboard loads in <100ms with caching vs 800ms without"

### WebSocket
> "I can build real-time systems. Alerts appear instantly without page refresh"

### Apache Artemis MQ
> "I understand event-driven architecture. Cost approvals trigger downstream processes asynchronously"

### Live Demo
> "Here's the URL. Log in now. See it working."
> (This alone closes 60% of interviews)

