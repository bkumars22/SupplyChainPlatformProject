# Demo Scenario — Multi-Tier Supplier Risk

**Version:** 1.0
**Author:** Kumara Swamy
**Date:** September 2026

---

## Purpose

Phases 1–2 of `scip_master_plan` (dependency-cascaded risk, ESG scoring,
scenario simulation, action suggestions) are real, tested features — but a
working feature and a compelling demo are different things. This is the
one scenario every future demo, pitch, and pilot conversation should use.
The same story, told well once, beats a different ad-hoc example every
time.

All numbers below are pulled live from the running application (seeded
in `data.sql`, `SUPP-011` / `SUPP-012`), not invented for this document —
run the walkthrough yourself before a live pitch to confirm they still
match.

## The Scenario

- **Meridian Assemblies** (`SUPP-011`) — a Tier-1 supplier with healthy
  direct metrics: 90% on-time delivery, composite score 84/100, tier
  APPROVED.
- Meridian **sole-sources** its Structural Frame Assembly from
  **Coastal Components** (`SUPP-012`) — no backup supplier qualified.
- Coastal Components is actually struggling: 40% on-time delivery, a low
  quality score, composite score 28/100 (tier PROBATION) — and 2 logged
  ESG compliance violations in the past 12 months.

Every conventional supplier scorecard — including SCIP's own direct
metrics view — shows Meridian as fine. It's the dependency that's
invisible until it isn't.

## The Walkthrough

1. **"Here's Meridian Assemblies. Direct risk: 16%, composite score 84,
   APPROVED tier."**
   `GET /api/suppliers/SUPP-011` — every conventional tool stops here.
   Looks fine.

2. **"But Meridian depends entirely on Coastal Components for one
   structural component. No backup source qualified."**
   `GET /api/suppliers/SUPP-011/structural-risk` — shows the sole-source
   dependency regardless of Coastal's current risk level.

3. **"Effective risk, once we account for that dependency: 55%. Same
   supplier, very different picture."**
   `GET /api/suppliers/SUPP-011/cascaded-risk` →
   `directRisk: 0.16`, `effectiveRisk: 0.549`, with an active
   sole-source flag naming Coastal Components and the 72% risk driving it.

4. **"Coastal Components also has 2 compliance violations logged this
   year — a second, independent warning sign."**
   `GET /api/suppliers/SUPP-012/esg-risk` → 2 violations, ESG score 52.3/100.

5. **"The system doesn't just flag this — it suggests a specific action.
   You approve or dismiss it. It never acts alone."**
   `GET /api/suppliers/SUPP-011/action-suggestions` →
   `diversify_sourcing`, priority `high`, citing Coastal Components and
   the Structural Frame Assembly by name, `requiresHumanApproval: true`.

6. **"This is the gap industry data says most companies report: real
   failures they didn't see coming. This is built specifically to see it."**

## Optional: Show the Scenario Simulator Live

To make the risk tangible instead of abstract, run the what-if endpoint
during the demo with a dramatic hypothetical — "what if Coastal had a
factory shutdown tomorrow":

```
POST /api/scenarios/simulate
{ "hypotheticalChanges": { "SUPP-012": 0.95 } }
```

Meridian's projected effective risk jumps from 0.549 to 0.673, and the
response is explicitly flagged `"simulation": true`. Follow it by
re-fetching `GET /api/suppliers/SUPP-011/cascaded-risk` to show the real
stored data is untouched — the projection never wrote anything.

## Why This Structure Works

It starts with what a conventional tool would show (nothing concerning),
then reveals what SCIP sees that others don't — that contrast is the
entire pitch, made concrete instead of abstract.
