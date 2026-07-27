from locators.modular_locator_framework import ModularLocatorEngine
from locators.playwright_strategies import (
    PlaywrightExactIdStrategy,
    PlaywrightRoleStrategy,
    PlaywrightVisibleTextStrategy,
)

LOGIN_URL = "https://the-internet.herokuapp.com/login"


def test_locator_engine_logs_in_on_a_real_page(page):
    page.goto(LOGIN_URL)

    username_engine = ModularLocatorEngine(strategies=[PlaywrightExactIdStrategy(page)])
    password_engine = ModularLocatorEngine(strategies=[PlaywrightExactIdStrategy(page)])

    username_result = username_engine.find_element(dom=None, target="username")
    password_result = password_engine.find_element(dom=None, target="password")

    assert username_result["strategy_used"] == "playwright_exact_id"
    assert password_result["strategy_used"] == "playwright_exact_id"

    username_result["element"].fill("tomsmith")
    password_result["element"].fill("SuperSecretPassword!")

    # The real submit button has no id, so ExactId fails and the engine
    # falls through to a role-based lookup - same pattern as the stale-id
    # fallback demo, but against a real, unmodified page.
    button_engine = ModularLocatorEngine(strategies=[
        PlaywrightExactIdStrategy(page),
        PlaywrightRoleStrategy(page, role="button", accessible_name="Login"),
    ])
    button_result = button_engine.find_element(dom=None, target="login-button")
    assert button_result["strategy_used"] == "playwright_role"

    button_result["element"].click()

    flash_engine = ModularLocatorEngine(strategies=[PlaywrightExactIdStrategy(page)])
    flash_result = flash_engine.find_element(dom=None, target="flash")
    assert "You logged into a secure area" in flash_result["element"].text_content()


def test_locator_engine_reports_failed_login_via_visible_text(page):
    page.goto(LOGIN_URL)

    page.locator("#username").fill("wrong-user")
    page.locator("#password").fill("wrong-password")
    page.get_by_role("button", name="Login").click()

    error_engine = ModularLocatorEngine(strategies=[
        PlaywrightExactIdStrategy(page),
        PlaywrightVisibleTextStrategy(page, expected_text="Your username is invalid!"),
    ])
    result = error_engine.find_element(dom=None, target="stale-error-id")

    assert result["strategy_used"] == "playwright_visible_text"
