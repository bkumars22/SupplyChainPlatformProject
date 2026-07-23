from abc import ABC

import pytest

from locators.modular_locator_framework import LocatorStrategy, ModularLocatorEngine

PAGE_HTML = """
<!DOCTYPE html>
<html>
<body>
    <button id="submit-btn-v2" aria-label="Submit form">Submit</button>
</body>
</html>
"""


class PlaywrightExactIdStrategy(LocatorStrategy):
    name = "playwright_exact_id"

    def __init__(self, page):
        self.page = page

    def find(self, dom, target):
        locator = self.page.locator(f"#{target}")
        if locator.count() > 0:
            return locator.first
        return None


class PlaywrightVisibleTextStrategy(LocatorStrategy):
    name = "playwright_visible_text"

    def __init__(self, page, expected_text):
        self.page = page
        self.expected_text = expected_text

    def find(self, dom, target):
        locator = self.page.get_by_text(self.expected_text)
        if locator.count() > 0:
            return locator.first
        return None


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
