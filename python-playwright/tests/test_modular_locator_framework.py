from locators.modular_locator_framework import (
    ClassAndTagStrategy,
    ExactIdStrategy,
    ModularLocatorEngine,
    NearbyLabelStrategy,
    VisibleTextStrategy,
)

DOM = [
    {"id": "submit-btn-v2", "text": "Submit", "class": "btn-primary", "tag": "button", "aria_label": "Submit form"},
    {"id": "user-email-input", "text": "", "class": "form-control", "tag": "input", "aria_label": "Email address"},
]


def test_exact_id_match():
    engine = ModularLocatorEngine(strategies=[ExactIdStrategy()])
    result = engine.find_element(DOM, target="submit-btn-v2")
    assert result["strategy_used"] == "exact_id"
    assert result["element"]["id"] == "submit-btn-v2"


def test_falls_through_to_next_strategy_when_id_is_stale():
    engine = ModularLocatorEngine(strategies=[
        ExactIdStrategy(),
        VisibleTextStrategy(expected_text="Submit"),
        ClassAndTagStrategy(expected_class="btn-primary", expected_tag="button"),
        NearbyLabelStrategy(expected_label="Submit form"),
    ])
    result = engine.find_element(DOM, target="old-submit-id")
    assert result["strategy_used"] == "visible_text"
    assert result["element"]["id"] == "submit-btn-v2"


def test_no_strategy_matches():
    engine = ModularLocatorEngine(strategies=[ExactIdStrategy()])
    result = engine.find_element(DOM, target="does-not-exist")
    assert result["strategy_used"] == "none_found"
    assert result["element"] is None
