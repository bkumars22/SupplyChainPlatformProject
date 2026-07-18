# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — statistical significance for before/after claims
"""
p-value + Cohen's d for proving a before/after improvement is genuine,
not random chance. See significance/prove_rag_improvement.py for SCIP's
concrete use: does RAG historical context measurably improve validated
explanation quality, versus generating explanations with no context?
"""

from __future__ import annotations

from dataclasses import dataclass

import numpy as np
from scipy import stats


@dataclass
class StatisticalProof:
    before_mean: float
    after_mean: float
    p_value: float
    cohens_d: float
    is_significant: bool       # p < alpha, conventionally alpha = 0.05
    effect_size_label: str     # negligible / small / medium / large

    def summary(self) -> str:
        return (
            f"Before: {self.before_mean:.3f}, After: {self.after_mean:.3f}\n"
            f"p-value: {self.p_value:.6f} ({'significant' if self.is_significant else 'not significant'})\n"
            f"Cohen's d: {self.cohens_d:.2f} ({self.effect_size_label} effect)"
        )


def prove_improvement(
    before_scores: np.ndarray,
    after_scores: np.ndarray,
    alpha: float = 0.05,
) -> StatisticalProof:
    """
    before_scores / after_scores: arrays of individual measurements (e.g.
    pass/fail as 0/1 across repeated runs), not just two summary numbers —
    the distribution is what makes the test meaningful.
    """
    before_scores = np.asarray(before_scores, dtype=float)
    after_scores = np.asarray(after_scores, dtype=float)

    t_statistic, p_value = stats.ttest_ind(after_scores, before_scores)

    pooled_std = np.sqrt(
        ((len(before_scores) - 1) * np.var(before_scores, ddof=1) +
         (len(after_scores) - 1) * np.var(after_scores, ddof=1))
        / (len(before_scores) + len(after_scores) - 2)
    )
    cohens_d = 0.0 if pooled_std == 0 else (np.mean(after_scores) - np.mean(before_scores)) / pooled_std

    abs_d = abs(cohens_d)
    if abs_d < 0.2:
        label = "negligible"
    elif abs_d < 0.5:
        label = "small"
    elif abs_d < 0.8:
        label = "medium"
    else:
        label = "large"

    return StatisticalProof(
        before_mean=float(np.mean(before_scores)),
        after_mean=float(np.mean(after_scores)),
        p_value=float(p_value),
        cohens_d=float(cohens_d),
        is_significant=bool(p_value < alpha),
        effect_size_label=label,
    )
