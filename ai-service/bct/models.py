# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform — BCT Shared Core Models
#
# Every testing module (behavioral contract, injection, leakage, bias,
# multi-turn) produces results in this same shape, so they can all feed
# into one unified report via bct/runner.py.

from dataclasses import dataclass, field
from enum import Enum
from datetime import datetime, timezone


class TestCategory(Enum):
    BEHAVIORAL_CONTRACT = "behavioral_contract"
    PROMPT_INJECTION = "prompt_injection"
    DATA_LEAKAGE = "data_leakage"
    BIAS_FAIRNESS = "bias_fairness"
    MULTI_TURN_ESCALATION = "multi_turn_escalation"


class Severity(Enum):
    LOW = "low"
    MEDIUM = "medium"
    HIGH = "high"
    CRITICAL = "critical"


@dataclass
class TestCase:
    id: str
    category: TestCategory
    description: str
    input_prompt: str
    expected_behavior: str          # what the system SHOULD do
    severity_if_failed: Severity
    # Rule-based judging signatures (see bct/judges.py):
    # a response FAILS the case if any violation_patterns regex matches.
    # compliance_patterns are informational — logged as evidence when matched,
    # not required for a pass.
    violation_patterns: list = field(default_factory=list)
    compliance_patterns: list = field(default_factory=list)
    # Which system_adapters target this case should be sent to (see
    # bct.system_adapters.get_target) — lets one module test contracts
    # belonging to different systems (e.g. a tutor vs. a data assistant)
    # without mixing up which adapter judges which rule.
    target_system: str = "assistant"


@dataclass
class TestResult:
    test_case: TestCase
    actual_response: str
    passed: bool
    notes: str = ""
    tested_at: str = field(default_factory=lambda: datetime.now(timezone.utc).isoformat())


@dataclass
class ModuleReport:
    category: TestCategory
    total_tests: int
    passed: int
    failed: int
    critical_failures: list  # TestResult where severity_if_failed == CRITICAL and passed == False
    results: list = field(default_factory=list)  # full TestResult list, for reporting/JSON export

    @property
    def pass_rate(self) -> float:
        return (self.passed / self.total_tests * 100) if self.total_tests else 0.0

    def summary(self) -> str:
        return (
            f"{self.category.value}: {self.passed}/{self.total_tests} passed "
            f"({self.pass_rate:.1f}%), {len(self.critical_failures)} critical failures"
        )
