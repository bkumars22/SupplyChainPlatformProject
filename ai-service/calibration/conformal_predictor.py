# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — Conformal Prediction for risk scores
"""
Split Conformal Prediction — gives SCIP's IsolationForest risk score a
mathematically guaranteed confidence interval instead of a bare number.

Mechanism: on a calibration set (predictions with a known reference value),
compute the absolute error for each prediction. The (1-alpha) quantile of
these errors becomes the margin applied to new predictions — this margin
is guaranteed to contain the reference value (1-alpha) of the time, subject
to calibration and production data being exchangeable (same distribution).

Honest limitation: SCIP does not yet persist historical ground-truth
outcomes (e.g. "was this supplier actually late next quarter?"), so there
is no true label to calibrate against. Until that pipeline exists, this
calibrates the IsolationForest's continuous score against the same
quality-score-threshold rule that already produces the categorical
HIGH/MEDIUM/LOW label — i.e. it bounds how far the anomaly-detection score
can drift from the rule-based band, not from real future outcomes. That is
still useful (it flags when the two disagree sharply) but it is not the
same guarantee you'd get calibrating against real deliveries. Wiring real
outcome tracking is the next step, not done yet.
"""

from __future__ import annotations

import logging
from dataclasses import dataclass

import numpy as np

logger = logging.getLogger("calibration.conformal_predictor")


@dataclass
class ConformalInterval:
    point_estimate: float
    lower_bound: float
    upper_bound: float
    confidence_level: float

    def __str__(self):
        return (f"{self.point_estimate:.3f} guaranteed within "
                f"[{self.lower_bound:.3f}, {self.upper_bound:.3f}] "
                f"at {self.confidence_level*100:.0f}% confidence")


class ConformalPredictor:
    """Split Conformal Prediction — the simplest, most defensible variant."""

    def __init__(self, confidence_level: float = 0.90):
        self.confidence_level = confidence_level
        self.alpha = 1 - confidence_level
        self._margin: float | None = None

    def calibrate(self, predicted_scores: np.ndarray, reference_scores: np.ndarray) -> None:
        """Run on a calibration set — the margin is only valid if this set is
        exchangeable with what predict_interval() is later called on."""
        errors = np.abs(np.asarray(predicted_scores) - np.asarray(reference_scores))
        n = len(errors)
        if n == 0:
            raise ValueError("Cannot calibrate on an empty set.")

        # (1-alpha) quantile with the standard finite-sample correction.
        quantile_level = min(np.ceil((n + 1) * (1 - self.alpha)) / n, 1.0)
        self._margin = float(np.quantile(errors, quantile_level))

    def predict_interval(self, point_prediction: float) -> ConformalInterval:
        if self._margin is None:
            raise ValueError("Call calibrate() before predicting intervals.")
        return ConformalInterval(
            point_estimate=point_prediction,
            lower_bound=max(0.0, point_prediction - self._margin),
            upper_bound=min(1.0, point_prediction + self._margin),
            confidence_level=self.confidence_level,
        )


def rule_based_reference_severity(quality_score: float) -> float:
    """Deterministic reference used only as a calibration target — matches
    the same thresholds score_risk() uses for the categorical risk label,
    so the continuous score can be checked against the rule the label
    already trusts. See module docstring for why this isn't real ground
    truth yet."""
    if quality_score <= 15.0:
        return 0.9
    elif quality_score <= 30.0:
        return 0.5
    return 0.1


def add_confidence_bounds(
    risk_scores: list[dict],
    confidence_level: float = 0.90,
) -> list[dict]:
    """Attach riskLower/riskUpper/riskConfidence to each entry in risk_scores.

    Requires each entry to already have "riskScoreContinuous" (0-1) and
    "qualityScore" set by score_risk(). Calibrates on the batch itself —
    with SCIP's current supplier counts (~10) that's a small calibration
    set, so treat the bound as indicative, not a tight production-grade
    guarantee (which needs a larger, held-out calibration set).
    """
    if len(risk_scores) < 2:
        return risk_scores

    try:
        predicted = np.array([s["riskScoreContinuous"] for s in risk_scores])
        reference = np.array([rule_based_reference_severity(s["qualityScore"]) for s in risk_scores])

        predictor = ConformalPredictor(confidence_level=confidence_level)
        predictor.calibrate(predicted, reference)

        for s, p in zip(risk_scores, predicted):
            interval = predictor.predict_interval(float(p))
            s["riskLower"] = round(interval.lower_bound, 4)
            s["riskUpper"] = round(interval.upper_bound, 4)
            s["riskConfidence"] = interval.confidence_level
    except Exception as exc:
        logger.warning("add_confidence_bounds failed: %s", exc)

    return risk_scores
