"""
Modular Locator Framework
--------------------------
A pluggable design for finding UI elements in test automation.

Instead of one hard-coded function, each "way of finding an element"
(exact ID, visible text, class+tag, etc.) is its own independent
module (a class). You can plug in new modules, remove old ones, or
reorder them, WITHOUT changing the core engine.

This is a real, standard software design pattern called the
"Strategy Pattern" - widely used in real automation frameworks.

To connect this to a REAL browser (Selenium example), see the
`SeleniumExactIdStrategy` example at the bottom - it shows exactly
where real driver.find_element() calls would replace the simulated
DOM lookups used here.

Author: Kumaraswamy B
"""

import logging
from abc import ABC, abstractmethod

logger = logging.getLogger(__name__)


# ============================================================
# STEP 1: The "contract" every locator module must follow
# ============================================================
class LocatorStrategy(ABC):
    """
    Every locator strategy (module) must implement `find()`.
    This is what makes them swappable - the engine only ever
    calls `.find()`, so it doesn't care HOW each module works
    internally, only that it follows this same shape.
    """

    @abstractmethod
    def find(self, dom, target):
        """Return the matching element dict, or None if not found."""
        pass

    @property
    @abstractmethod
    def name(self):
        """A short name for this strategy, used in reporting."""
        pass


# ============================================================
# STEP 2: Individual, independent locator modules
# ============================================================
class ExactIdStrategy(LocatorStrategy):
    name = "exact_id"

    def find(self, dom, target):
        for el in dom:
            if el.get("id") == target:
                return el
        return None


class VisibleTextStrategy(LocatorStrategy):
    name = "visible_text"

    def __init__(self, expected_text):
        self.expected_text = expected_text

    def find(self, dom, target):
        for el in dom:
            if el.get("text") == self.expected_text:
                return el
        return None


class ClassAndTagStrategy(LocatorStrategy):
    name = "class_and_tag"

    def __init__(self, expected_class, expected_tag):
        self.expected_class = expected_class
        self.expected_tag = expected_tag

    def find(self, dom, target):
        for el in dom:
            if el.get("class") == self.expected_class and el.get("tag") == self.expected_tag:
                return el
        return None


class NearbyLabelStrategy(LocatorStrategy):
    """
    A NEW module, added later, without touching the engine at all.
    This demonstrates the whole point of the pattern: extendability.
    """
    name = "nearby_label"

    def __init__(self, expected_label):
        self.expected_label = expected_label

    def find(self, dom, target):
        for el in dom:
            if el.get("aria_label") == self.expected_label:
                return el
        return None


# ============================================================
# STEP 3: The engine - runs whichever modules you plug into it
# ============================================================
class ModularLocatorEngine:
    def __init__(self, strategies):
        """
        `strategies` is just a LIST of strategy objects.
        You decide which modules to use, and in what order,
        entirely from OUTSIDE the engine - the engine itself
        never needs to change.
        """
        self.strategies = strategies

    def find_element(self, dom, target):
        report = {"target": target, "strategy_used": None, "element": None}
        failed_strategies = []

        for strategy in self.strategies:
            result = strategy.find(dom, target)
            if result:
                report["strategy_used"] = strategy.name
                report["element"] = result
                report["healed"] = len(failed_strategies) > 0
                if report["healed"]:
                    logger.warning(
                        "Locator healed for target '%s': %s failed, recovered via '%s'",
                        target, failed_strategies, strategy.name,
                    )
                return report
            failed_strategies.append(strategy.name)

        report["strategy_used"] = "none_found"
        report["healed"] = False
        logger.warning(
            "Locator NOT found for target '%s': all strategies failed (%s)",
            target, failed_strategies,
        )
        return report


# ============================================================
# DEMO - real, working, testable
# ============================================================
CURRENT_PAGE_DOM = [
    {"id": "submit-btn-v2", "text": "Submit", "class": "btn-primary", "tag": "button", "aria_label": "Submit form"},
    {"id": "user-email-input", "text": "", "class": "form-control", "tag": "input", "aria_label": "Email address"},
]

if __name__ == "__main__":
    print("=" * 70)
    print("MODULAR LOCATOR FRAMEWORK - Demo")
    print("=" * 70)

    # Plug in exactly the modules you want, in the order you want
    engine = ModularLocatorEngine(strategies=[
        ExactIdStrategy(),
        VisibleTextStrategy(expected_text="Submit"),
        ClassAndTagStrategy(expected_class="btn-primary", expected_tag="button"),
        NearbyLabelStrategy(expected_label="Submit form"),
    ])

    # Case 1: old/broken ID - engine falls through modules until one matches
    result = engine.find_element(CURRENT_PAGE_DOM, target="old-submit-id")
    print(f"\nSearching for: 'old-submit-id'")
    print(f"  Found via module: {result['strategy_used']}")
    print(f"  Element: {result['element']}")

    # Now swap in a DIFFERENT set of modules - no engine code changes needed
    minimal_engine = ModularLocatorEngine(strategies=[ExactIdStrategy()])
    result2 = minimal_engine.find_element(CURRENT_PAGE_DOM, target="old-submit-id")
    print(f"\nSame search, but with ONLY the ExactId module plugged in:")
    print(f"  Strategy used: {result2['strategy_used']}")
    print(f"  Element: {result2['element']}")


"""
============================================================
HOW THIS CONNECTS TO A REAL BROWSER (Selenium)
============================================================
In this demo, strategies search a Python list (CURRENT_PAGE_DOM).
In a real project, you'd replace the list search with an actual
Selenium call. Example of what a REAL strategy module looks like:

    from selenium.webdriver.common.by import By
    from selenium.common.exceptions import NoSuchElementException

    class SeleniumExactIdStrategy(LocatorStrategy):
        name = "selenium_exact_id"

        def __init__(self, driver):
            self.driver = driver

        def find(self, dom, target):
            try:
                return self.driver.find_element(By.ID, target)
            except NoSuchElementException:
                return None

Everything else in the engine stays exactly the same - that's the
real value of this pattern: swap what's INSIDE a module without
touching how modules plug into the system.

============================================================
HOW THIS CONNECTS TO PLAYWRIGHT (same pattern, different engine)
============================================================
Playwright's API is different from Selenium's, but since every
strategy just needs to implement `.find()`, you can write
Playwright-based modules the exact same way - the
ModularLocatorEngine itself needs ZERO changes.

    from playwright.sync_api import Page

    class PlaywrightExactIdStrategy(LocatorStrategy):
        name = "playwright_exact_id"

        def __init__(self, page: Page):
            self.page = page

        def find(self, dom, target):
            locator = self.page.locator(f"#{target}")
            if locator.count() > 0:
                return locator.first
            return None

    class PlaywrightVisibleTextStrategy(LocatorStrategy):
        name = "playwright_visible_text"

        def __init__(self, page: Page, expected_text):
            self.page = page
            self.expected_text = expected_text

        def find(self, dom, target):
            locator = self.page.get_by_text(self.expected_text)
            if locator.count() > 0:
                return locator.first
            return None

    class PlaywrightRoleStrategy(LocatorStrategy):
        # Playwright has a feature Selenium doesn't have built-in as easily:
        # finding elements by ACCESSIBILITY ROLE (button, link, textbox, etc).
        # This is often more resilient than ID or class matching, since
        # accessibility roles rarely change even when the DOM structure does.
        name = "playwright_role"

        def __init__(self, page: Page, role, accessible_name):
            self.page = page
            self.role = role
            self.accessible_name = accessible_name

        def find(self, dom, target):
            locator = self.page.get_by_role(self.role, name=self.accessible_name)
            if locator.count() > 0:
                return locator.first
            return None

    # Real usage - identical structure to the Selenium version:
    #
    #   engine = ModularLocatorEngine(strategies=[
    #       PlaywrightExactIdStrategy(page),
    #       PlaywrightVisibleTextStrategy(page, expected_text="Submit"),
    #       PlaywrightRoleStrategy(page, role="button", accessible_name="Submit"),
    #   ])
    #   result = engine.find_element(dom=None, target="submit-btn")
    #   if result["element"]:
    #       result["element"].click()

Because the engine only ever calls `strategy.find(dom, target)`, it
never needs to know or care whether that strategy is backed by
Selenium, Playwright, or a plain Python list (like this demo uses).
That's the entire value of the plugin/module design: the TOOL
underneath can change freely without breaking anything else.
"""
