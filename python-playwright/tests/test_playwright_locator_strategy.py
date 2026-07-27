from locators.modular_locator_framework import ModularLocatorEngine
from locators.playwright_strategies import PlaywrightExactIdStrategy, PlaywrightVisibleTextStrategy

PAGE_HTML = """
<!DOCTYPE html>
<html>
<body>
    <button id="submit-btn-v2" aria-label="Submit form">Submit</button>
</body>
</html>
"""


def test_engine_finds_element_via_playwright_exact_id(page):
    page.set_content(PAGE_HTML)
    engine = ModularLocatorEngine(strategies=[PlaywrightExactIdStrategy(page)])

    result = engine.find_element(dom=None, target="submit-btn-v2")

    assert result["strategy_used"] == "playwright_exact_id"
    assert result["element"].text_content() == "Submit"


def test_engine_falls_back_to_visible_text_when_id_is_stale(page):
    page.set_content(PAGE_HTML)
    engine = ModularLocatorEngine(strategies=[
        PlaywrightExactIdStrategy(page),
        PlaywrightVisibleTextStrategy(page, expected_text="Submit"),
    ])

    result = engine.find_element(dom=None, target="old-submit-id")

    assert result["strategy_used"] == "playwright_visible_text"
    assert result["element"].get_attribute("id") == "submit-btn-v2"
