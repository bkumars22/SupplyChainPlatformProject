from playwright.sync_api import TimeoutError as PlaywrightTimeoutError

from locators.modular_locator_framework import LocatorStrategy

DEFAULT_WAIT_MS = 3000


class PlaywrightExactIdStrategy(LocatorStrategy):
    name = "playwright_exact_id"

    def __init__(self, page):
        self.page = page

    def find(self, dom, target):
        locator = self.page.locator(f"#{target}").first
        try:
            locator.wait_for(state="attached", timeout=DEFAULT_WAIT_MS)
            return locator
        except PlaywrightTimeoutError:
            return None


class PlaywrightVisibleTextStrategy(LocatorStrategy):
    name = "playwright_visible_text"

    def __init__(self, page, expected_text):
        self.page = page
        self.expected_text = expected_text

    def find(self, dom, target):
        locator = self.page.get_by_text(self.expected_text).first
        try:
            locator.wait_for(state="attached", timeout=DEFAULT_WAIT_MS)
            return locator
        except PlaywrightTimeoutError:
            return None


class PlaywrightRoleStrategy(LocatorStrategy):
    name = "playwright_role"

    def __init__(self, page, role, accessible_name):
        self.page = page
        self.role = role
        self.accessible_name = accessible_name

    def find(self, dom, target):
        locator = self.page.get_by_role(self.role, name=self.accessible_name).first
        try:
            locator.wait_for(state="attached", timeout=DEFAULT_WAIT_MS)
            return locator
        except PlaywrightTimeoutError:
            return None
