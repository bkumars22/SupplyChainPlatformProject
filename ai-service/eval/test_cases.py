# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — LLM Eval Test Cases
#
# 20 test cases across four supply chain AI categories.
# Each case includes a reference expected_response that scores well on all
# four metrics in heuristic mode (no API key required).

SUPPLY_CHAIN_TEST_CASES = [

    # ── Supplier Risk Alert Explanations (5) ──────────────────────────────────

    {
        "id": "SR-001",
        "category": "supplier_risk",
        "input": (
            "Supplier SUPP-003 has delivered late on 7 of the last 10 shipments "
            "with an average lead time of 18 days vs the contracted 12 days. "
            "Explain the risk level and recommend an action."
        ),
        "expected_keywords": [
            "high risk", "late", "lead time", "on-time rate", "mitigation",
            "alternative", "delivery"
        ],
        "expected_response": (
            "SUPP-003 is classified as HIGH RISK with an on-time rate of only 30%, "
            "well below the acceptable threshold of 85%. The average lead time of "
            "18 days represents a 50% overrun against the contracted 12 days, "
            "indicating systemic delivery delays. Recommended mitigation actions: "
            "(1) issue a formal supplier performance notice, (2) dual-source this "
            "component with an alternative supplier, and (3) increase safety stock "
            "by 2 weeks to buffer ongoing delivery variance."
        ),
    },
    {
        "id": "SR-002",
        "category": "supplier_risk",
        "input": (
            "A new supplier SUPP-011 has completed only 3 deliveries with a 100% "
            "on-time rate and zero defects. How should we classify their risk?"
        ),
        "expected_keywords": [
            "insufficient", "data", "low risk", "monitor", "deliveries",
            "on-time", "probationary"
        ],
        "expected_response": (
            "SUPP-011 currently shows LOW RISK based on 3 deliveries with a "
            "100% on-time rate and 0% defect rate. However, with only 3 data "
            "points the sample size is insufficient to establish a reliable risk "
            "baseline — a minimum of 10 deliveries is recommended. The supplier "
            "should remain on probationary monitoring for the next quarter. "
            "Schedule a quarterly review once 10 or more deliveries have been "
            "recorded to confirm the low-risk classification."
        ),
    },
    {
        "id": "SR-003",
        "category": "supplier_risk",
        "input": (
            "SUPP-007 has a defect rate of 4.2% over the past 6 months, "
            "exceeding our 2% quality threshold. Supplier is a sole source "
            "for component IC-4420. Assess the risk."
        ),
        "expected_keywords": [
            "defect rate", "quality", "sole source", "critical", "risk",
            "threshold", "inspection"
        ],
        "expected_response": (
            "SUPP-007 presents a CRITICAL supply risk. The 4.2% defect rate is "
            "2.1× above the 2% quality threshold, and the supplier is sole-source "
            "for component IC-4420, creating a single point of failure with no "
            "immediate alternative. Risk score: HIGH (combined defect + "
            "concentration risk). Immediate actions: (1) initiate enhanced incoming "
            "inspection at 100% inspection rate, (2) escalate to a supplier corrective "
            "action request (SCAR), and (3) begin qualification of an alternative "
            "source for IC-4420 to eliminate the sole-source dependency within 90 days."
        ),
    },
    {
        "id": "SR-004",
        "category": "supplier_risk",
        "input": (
            "Geopolitical tensions have been flagged in the region where SUPP-009 "
            "manufactures. The supplier accounts for 35% of our raw material spend. "
            "What is the supply chain risk?"
        ),
        "expected_keywords": [
            "geopolitical", "risk", "concentration", "spend", "diversify",
            "contingency", "alternative"
        ],
        "expected_response": (
            "SUPP-009 poses a HIGH geopolitical supply risk. With 35% spend "
            "concentration in a single at-risk region and supplier, any disruption "
            "could immediately impact 35% of raw material supply, threatening "
            "production continuity. Recommended risk mitigation strategy: "
            "(1) diversify sourcing to reduce SUPP-009 share below 20%, "
            "(2) qualify at least one alternative supplier in a different region "
            "within 60 days, (3) increase strategic buffer inventory for "
            "SUPP-009 components to cover a 90-day supply disruption, and "
            "(4) establish a contingency sourcing plan with pre-negotiated "
            "contracts for emergency activation."
        ),
    },
    {
        "id": "SR-005",
        "category": "supplier_risk",
        "input": (
            "SUPP-002 shows anomalies in 3 of 15 recent deliveries: "
            "two had lead times 3× the normal, one had a 6% defect rate. "
            "Overall on-time rate is still 80%. What is the risk assessment?"
        ),
        "expected_keywords": [
            "anomaly", "risk", "on-time", "defect", "lead time",
            "80%", "medium", "variance"
        ],
        "expected_response": (
            "SUPP-002 is classified as MEDIUM RISK. While the overall on-time "
            "rate of 80% is below the 85% target, the more concerning signal is "
            "the anomaly pattern: 3 out of 15 deliveries (20%) exhibit severe "
            "outlier behavior — lead time spikes of 3× normal and a defect rate "
            "of 6% on one delivery. High lead-time variance and defect outliers "
            "indicate operational instability rather than a consistent baseline "
            "issue. Recommended actions: (1) conduct a root-cause analysis with "
            "the supplier to identify the anomaly triggers, and (2) increase "
            "monitoring frequency to monthly performance reviews."
        ),
    },

    # ── Anomaly Detection Explanations (5) ────────────────────────────────────

    {
        "id": "AD-001",
        "category": "anomaly_detection",
        "input": (
            "The IsolationForest model flagged shipment #SH-4821 as an anomaly. "
            "Lead time was 28 days vs a 10-day historical average. "
            "Explain what the anomaly means and what to investigate."
        ),
        "expected_keywords": [
            "anomaly", "lead time", "IsolationForest", "outlier",
            "investigation", "days", "historical"
        ],
        "expected_response": (
            "Shipment SH-4821 has been flagged as a statistical anomaly by the "
            "IsolationForest model. Its 28-day lead time is 2.8× the 10-day "
            "historical average, placing it well outside the expected distribution "
            "and confirming it as a significant outlier. Likely investigation areas: "
            "(1) customs or port delay, (2) supplier production stoppage, "
            "(3) incorrect shipment routing, (4) force-majeure event. "
            "Recommended immediate actions: contact the supplier for a root-cause "
            "explanation, check whether downstream orders are at risk, and update "
            "the affected purchase order's expected delivery date in the system."
        ),
    },
    {
        "id": "AD-002",
        "category": "anomaly_detection",
        "input": (
            "Cost anomaly detected: component PCB-220 jumped from $4.50 to $9.80 "
            "per unit in the latest purchase order. The AI scored this as anomaly "
            "score -0.83. Explain the severity."
        ),
        "expected_keywords": [
            "cost", "anomaly", "PCB-220", "price", "increase",
            "investigation", "purchase order"
        ],
        "expected_response": (
            "A severe cost anomaly has been detected for component PCB-220. "
            "The unit price increase from $4.50 to $9.80 represents a 117.8% "
            "price surge, far exceeding normal commodity price variance. "
            "An IsolationForest anomaly score of -0.83 indicates this is a "
            "deep outlier — only approximately 1 in 50 observations would be "
            "expected at this distance from the norm. Priority investigation "
            "steps: (1) verify the purchase order is not a data entry error, "
            "(2) obtain a formal price justification from the supplier, "
            "(3) benchmark against alternative suppliers' current pricing, "
            "and (4) escalate to procurement management if the price increase "
            "cannot be justified by raw material market data."
        ),
    },
    {
        "id": "AD-003",
        "category": "anomaly_detection",
        "input": (
            "Order volume for item ITEM-105 spiked 340% above the 12-month "
            "average in a single week. No promotional event is scheduled. "
            "Is this an anomaly and what could it indicate?"
        ),
        "expected_keywords": [
            "volume", "anomaly", "spike", "340%", "demand",
            "investigation", "inventory"
        ],
        "expected_response": (
            "Yes, a 340% volume spike above the 12-month average with no scheduled "
            "promotional event is a strong demand anomaly. The signal is statistically "
            "significant and warrants immediate investigation. Possible causes: "
            "(1) a large unexpected customer order (check order management system), "
            "(2) panic-buying or forward purchasing behavior by a downstream customer, "
            "(3) a data entry error duplicating orders, or (4) an upstream supply "
            "disruption causing customers to stockpile. Recommended actions: verify "
            "open orders against customer confirmations, assess current inventory "
            "position for ITEM-105, and alert the production planning team to "
            "evaluate whether current safety stock can absorb this demand surge."
        ),
    },
    {
        "id": "AD-004",
        "category": "anomaly_detection",
        "input": (
            "Three different suppliers for the same component category all showed "
            "late deliveries in the same week. The AI detected a correlated anomaly "
            "cluster. What does this pattern suggest?"
        ),
        "expected_keywords": [
            "correlated", "cluster", "anomaly", "systemic", "suppliers",
            "risk", "disruption", "category"
        ],
        "expected_response": (
            "A correlated anomaly cluster across multiple suppliers in the same "
            "component category is a strong signal of a systemic, category-wide "
            "disruption rather than individual supplier performance issues. "
            "This pattern typically indicates: (1) a shared upstream raw material "
            "shortage affecting all suppliers in the tier, (2) a logistics "
            "disruption impacting a shared shipping lane or port, or (3) a "
            "regulatory or compliance event affecting the entire component class. "
            "Recommended escalation: convene an emergency supply chain risk review, "
            "contact all three suppliers simultaneously to identify the common "
            "root cause, activate safety stock protocols for this component category, "
            "and assess impact on production schedules for the next 4–6 weeks."
        ),
    },
    {
        "id": "AD-005",
        "category": "anomaly_detection",
        "input": (
            "December demand for item ITEM-201 is always 40% higher due to "
            "holiday seasonality, but the model flagged it as an anomaly this year. "
            "How should the anomaly be interpreted?"
        ),
        "expected_keywords": [
            "seasonal", "anomaly", "December", "40%", "expected",
            "holiday", "false positive"
        ],
        "expected_response": (
            "This is likely a false positive anomaly caused by seasonal demand "
            "patterns that may not be fully captured in the model's training window. "
            "If historical December data confirms a consistent 40% uplift for "
            "ITEM-201, the IsolationForest model may lack sufficient seasonal "
            "context to distinguish expected variation from true anomalies. "
            "Recommended actions: (1) review the model's training window — it "
            "should include at least 2–3 years of seasonal history, "
            "(2) add a seasonal feature flag to the anomaly detection pipeline "
            "so that known seasonal peaks are excluded from anomaly scoring, and "
            "(3) suppress this alert and document it as a known seasonal pattern "
            "to improve model precision."
        ),
    },

    # ── Demand Forecast Explanations (5) ──────────────────────────────────────

    {
        "id": "DF-001",
        "category": "demand_forecast",
        "input": (
            "Demand forecast for ITEM-112 shows a 28% surge in Q4. "
            "The model predicts 1,240 units/week vs the Q3 average of 968 units/week. "
            "Explain the forecast and what supply chain adjustments are needed."
        ),
        "expected_keywords": [
            "forecast", "Q4", "demand", "units", "28%", "supply chain",
            "inventory", "production"
        ],
        "expected_response": (
            "The demand forecast for ITEM-112 projects a 28.1% Q4 demand surge, "
            "rising from 968 to 1,240 units per week. This is a significant uplift "
            "requiring proactive supply chain adjustments. Required actions: "
            "(1) increase purchase orders for ITEM-112 raw materials by at least "
            "30% starting 8 weeks ahead of Q4 to cover supplier lead times, "
            "(2) confirm production capacity can accommodate the 272-unit weekly "
            "increase, (3) pre-build safety stock equivalent to 2 weeks of the "
            "forecast uplift (544 units), and (4) align with sales and distribution "
            "on regional demand distribution to avoid stock-out at specific locations."
        ),
    },
    {
        "id": "DF-002",
        "category": "demand_forecast",
        "input": (
            "New product ITEM-330 is launching next month with no historical demand "
            "data. Sales expects 500 units/week. How should the forecast be handled?"
        ),
        "expected_keywords": [
            "new product", "forecast", "historical", "uncertainty",
            "safety stock", "units", "launch"
        ],
        "expected_response": (
            "Forecasting a new product with no historical demand data requires "
            "a market-intelligence-driven approach since statistical models cannot "
            "extrapolate from zero history. The sales estimate of 500 units/week "
            "should be treated as a baseline with high uncertainty. Recommended "
            "approach: (1) use the sales team's estimate as the planning number "
            "with a ±40% confidence band (300–700 units/week), (2) plan initial "
            "production and inventory for 650 units/week to cover upside risk, "
            "(3) set up a 4-week demand signal review cadence after launch to "
            "replace assumptions with actuals, and (4) maintain flexible "
            "production capacity that can scale up or down within 2 weeks "
            "based on early demand signals."
        ),
    },
    {
        "id": "DF-003",
        "category": "demand_forecast",
        "input": (
            "ITEM-089 demand has declined 15% year-over-year for 3 consecutive years. "
            "The model forecasts another 12% decline next year to 320 units/week. "
            "What supply chain strategy should be applied?"
        ),
        "expected_keywords": [
            "decline", "forecast", "15%", "12%", "discontinue",
            "inventory", "strategy", "units"
        ],
        "expected_response": (
            "ITEM-089 is in a clear demand decline phase, with 3 consecutive years "
            "of 15% YoY decline and a forecast 12% additional decline to 320 units "
            "per week. This trajectory signals a product approaching end-of-life. "
            "Recommended supply chain strategy: (1) reduce replenishment orders "
            "to match the 320 unit forecast plus a minimal 1-week safety stock, "
            "(2) do not over-invest in inventory — target lean stock levels to "
            "minimize future write-off risk, (3) initiate an end-of-life product "
            "review with the business to determine the planned discontinuation "
            "date and manage customer transitions, and (4) phase out long-lead "
            "raw material commitments over the next 6 months."
        ),
    },
    {
        "id": "DF-004",
        "category": "demand_forecast",
        "input": (
            "A key raw material for ITEM-205 has a 6-month supply disruption. "
            "Current demand forecast is 800 units/week. "
            "How should supply chain planning respond?"
        ),
        "expected_keywords": [
            "supply", "disruption", "forecast", "demand", "800",
            "alternative", "inventory", "planning"
        ],
        "expected_response": (
            "A 6-month supply disruption for a key raw material is a critical "
            "supply chain event. With a demand forecast of 800 units/week and "
            "roughly 24 weeks of disruption, the total exposure is approximately "
            "19,200 units at risk. Immediate supply chain response: "
            "(1) conduct an emergency inventory audit of current raw material stock "
            "and calculate how many weeks of production can be sustained, "
            "(2) identify and qualify an alternative raw material source or "
            "substitute specification within 4 weeks, (3) communicate allocation "
            "constraints to sales and customer service to manage customer expectations, "
            "and (4) prioritize production for highest-margin or contractually "
            "committed orders during the supply-constrained period."
        ),
    },
    {
        "id": "DF-005",
        "category": "demand_forecast",
        "input": (
            "A 2-week promotional event for ITEM-150 is expected to generate "
            "a 200% demand spike per the marketing team. Normal demand is 400 units/week. "
            "Explain how to handle the forecast."
        ),
        "expected_keywords": [
            "promotional", "demand", "spike", "200%", "units",
            "inventory", "forecast", "event"
        ],
        "expected_response": (
            "A 200% demand spike for ITEM-150 during a 2-week promotional event "
            "would increase weekly demand from 400 to approximately 1,200 units — "
            "an uplift of 800 units/week, or 1,600 additional units for the "
            "promotional period. Planning recommendations: (1) build promotional "
            "inventory 4–6 weeks in advance to avoid competing with normal "
            "replenishment cycles, (2) pre-position 1,600 units in the promotion "
            "regions' distribution centers before event launch, (3) confirm "
            "production and supplier capacity to support the surge without "
            "disrupting base demand supply, and (4) set a post-promotion inventory "
            "drain plan to avoid excess stock if actual uplift is lower than the "
            "200% forecast."
        ),
    },

    # ── Cost Record Summaries (5) ──────────────────────────────────────────────

    {
        "id": "CR-001",
        "category": "cost_record",
        "input": (
            "Cost record CR-7821 shows a raw material cost increase from $12.40 "
            "to $15.60 per kg for steel-grade SG-440. Status: SUBMITTED for approval. "
            "Summarize the record and explain the approval decision."
        ),
        "expected_keywords": [
            "cost", "increase", "$12.40", "$15.60", "steel",
            "SUBMITTED", "approval", "25.8%"
        ],
        "expected_response": (
            "Cost record CR-7821 documents a raw material price increase of $3.20/kg "
            "(25.8%) for steel grade SG-440, moving from $12.40 to $15.60. "
            "The record is currently in SUBMITTED status awaiting management approval. "
            "Approval considerations: (1) verify the increase against current steel "
            "market indices (e.g., CRU or Platts SHB) to confirm market alignment, "
            "(2) assess the total landed cost impact across all products using "
            "SG-440, (3) determine whether the increase triggers customer contract "
            "pass-through clauses, and (4) check whether alternative steel "
            "specifications or suppliers can offer a lower price. Recommend "
            "APPROVED if market indices support the increase, with a 3-month "
            "price review clause."
        ),
    },
    {
        "id": "CR-002",
        "category": "cost_record",
        "input": (
            "Freight cost for the Asia-to-US lane increased by $820 per container "
            "in Q1. Cost record CR-4412 is in DRAFT status. "
            "What analysis is needed before submission?"
        ),
        "expected_keywords": [
            "freight", "cost", "$820", "container", "DRAFT",
            "Asia", "analysis", "submission"
        ],
        "expected_response": (
            "Cost record CR-4412 documents an $820/container freight cost increase "
            "for the Asia-to-US lane and is currently in DRAFT status. "
            "Required analysis before submission: "
            "(1) benchmark the $820 increase against current spot rates from "
            "freight rate indices (Drewry, Freightos) to confirm market validity, "
            "(2) calculate the total annual freight cost impact across all "
            "shipments on this lane, (3) review whether ocean carrier contracts "
            "allow for mid-term rate adjustments or if this applies only to "
            "spot shipments, (4) assess whether airfreight or alternative routing "
            "provides a cost-competitive option for time-sensitive shipments, "
            "and (5) confirm the effective date and whether existing open "
            "purchase orders are impacted before submitting for approval."
        ),
    },
    {
        "id": "CR-003",
        "category": "cost_record",
        "input": (
            "Cost record CR-9901 covers EUR-denominated component costs that "
            "have increased due to a 7% USD/EUR exchange rate movement. "
            "Original cost was €4.20 (≈$4.54). New cost is €4.20 (≈$4.87). "
            "Explain the multi-currency cost impact."
        ),
        "expected_keywords": [
            "currency", "EUR", "USD", "exchange rate", "cost",
            "€4.20", "7%", "hedging"
        ],
        "expected_response": (
            "Cost record CR-9901 captures a pure currency-driven cost increase "
            "for a EUR-denominated component. Although the component price in "
            "EUR is unchanged at €4.20, a 7% USD appreciation against EUR raises "
            "the USD equivalent from $4.54 to $4.87, an increase of $0.33/unit "
            "(7.3% in USD terms). This is not a supplier price increase — it is "
            "entirely driven by exchange rate movement. Analysis: "
            "(1) confirm whether the company has an active FX hedging program "
            "covering EUR exposure; if hedged, the cost impact may be partially "
            "offset, (2) calculate total annual USD impact across all EUR-sourced "
            "components, (3) assess whether customer contracts have currency "
            "adjustment clauses, and (4) evaluate renegotiating supplier payment "
            "terms to USD to eliminate future FX exposure on this component."
        ),
    },
    {
        "id": "CR-004",
        "category": "cost_record",
        "input": (
            "Cost record CR-2255 has been in APPROVED status for 3 months but "
            "the updated cost has not been reflected in the product standard cost. "
            "What is the workflow gap and how should it be resolved?"
        ),
        "expected_keywords": [
            "APPROVED", "cost", "standard cost", "workflow",
            "gap", "update", "ERP"
        ],
        "expected_response": (
            "A 3-month lag between APPROVED status and standard cost update in "
            "the ERP indicates a broken handoff in the cost record workflow. "
            "The APPROVED status confirms that the cost change has been "
            "reviewed and authorized, but the downstream ERP update step has "
            "not been triggered. Common root causes: (1) manual cost update "
            "step was missed or assigned to the wrong team, (2) ERP cost "
            "roll-up process was not re-run after approval, or (3) the "
            "approved cost was not mapped to the correct item-supplier "
            "combination in the ERP. Resolution steps: "
            "(1) identify the ERP standard cost record for the affected item, "
            "(2) perform an immediate manual cost update to reflect CR-2255 "
            "approved values, (3) re-run the cost roll-up for all affected "
            "finished goods BOMs, and (4) automate the ERP update trigger "
            "on cost record approval to prevent recurrence."
        ),
    },
    {
        "id": "CR-005",
        "category": "cost_record",
        "input": (
            "Year-over-year comparison shows total component costs increased 18% "
            "in the current fiscal year vs last year. Key drivers are raw material "
            "inflation (11%), freight (5%), and FX (2%). "
            "Summarize the cost analysis for leadership."
        ),
        "expected_keywords": [
            "18%", "cost", "raw material", "freight", "FX",
            "year-over-year", "inflation", "leadership"
        ],
        "expected_response": (
            "Total component costs increased 18% year-over-year in the current "
            "fiscal year, driven by three compounding factors. Raw material "
            "inflation accounts for 11 percentage points — the dominant driver, "
            "reflecting broad commodity price increases in steel, resins, and "
            "electronic components. Freight cost increases contribute 5 percentage "
            "points, driven by ongoing elevated ocean freight rates on key "
            "import lanes. FX headwinds contribute 2 percentage points, primarily "
            "from EUR and CNY appreciation against USD on non-hedged spend. "
            "Key leadership actions: (1) initiate a cost reduction program "
            "targeting raw material substitution and supplier renegotiation "
            "to offset the 11% inflation, (2) review freight contract terms "
            "and consolidation opportunities to reduce the 5% freight burden, "
            "and (3) expand FX hedging coverage to limit further currency-driven "
            "cost volatility in FY+1."
        ),
    },
]
