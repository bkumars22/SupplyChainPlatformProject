# Playwright Modular Locator Framework

[![Tests and Dashboard](https://github.com/bkumars22/-playwright-modular-locator-framework/actions/workflows/tests-dashboard.yml/badge.svg)](https://github.com/bkumars22/-playwright-modular-locator-framework/actions/workflows/tests-dashboard.yml)

**Live dashboard:** https://bkumars22.github.io/-playwright-modular-locator-framework/
(auto-updated on every push to `main` — see [CI/CD dashboard](#cicd-dashboard-github-actions--pages) below)

A pluggable, testable locator framework for UI test automation, built around
the **Strategy design pattern**. Instead of hard-coding "how to find an
element," each way of finding one (exact ID, visible text, class+tag,
accessibility role, ...) is its own independent, swappable module. The core
engine never changes — you just plug different strategies in.

The framework is demonstrated three ways in this repo, in increasing order
of realism:

1. **Pure Python** — strategies search a plain in-memory list (`dict`
   objects standing in for DOM elements). No browser involved at all.
2. **Playwright against synthetic HTML** — the same strategy pattern,
   backed by a real Chromium page rendering an inline HTML snippet.
3. **Playwright against a real, live website** — a public login form
   (`the-internet.herokuapp.com/login`), including a fallback scenario
   where the primary locator (`id`) doesn't exist on the real button and
   the engine falls through to a role-based strategy.

---

## Why this exists

Real UI automation breaks constantly because locators go stale — an `id`
gets renamed, a class changes, markup gets restructured. Most test suites
hard-code a single locator per element and simply fail when it changes.

This framework instead tries a **prioritized list of strategies** per
element and uses the first one that matches, so a single stale locator
doesn't fail the test — and you get visibility into *which* strategy
ended up finding the element (useful for noticing when your "primary"
locator has quietly gone stale).

### Auto-healing (detect + log)

Whenever an element is found via anything *other than* the first strategy
in the list, `ModularLocatorEngine` treats that as a "healed" lookup:

- `find_element(...)` returns `report["healed"]` (`True`/`False`)
- A `WARNING`-level log line is emitted naming the target, which
  strategies failed first, and which strategy ultimately found it

```
WARNING locators.modular_locator_framework: Locator healed for target
'login-button': ['playwright_exact_id'] failed, recovered via 'playwright_role'
```

This is detection/visibility only — it does not persist healed locators
or rewrite your strategy lists automatically. It's the first, honest step
toward self-healing: knowing *when* your primary locator has gone stale,
without pretending the framework silently fixes your code for you.

---

## Project structure

```
python-playwright/
├── locators/
│   ├── __init__.py
│   ├── modular_locator_framework.py   # Core engine + Strategy pattern + in-memory demo
│   ├── playwright_strategies.py       # Playwright-backed strategy implementations
│   └── visual_match_strategy.py       # OpenCV template-matching fallback strategy
├── tests/
│   ├── __init__.py
│   ├── test_modular_locator_framework.py    # Pure-Python unit tests (no browser)
│   ├── test_playwright_locator_strategy.py  # Playwright + synthetic inline HTML
│   ├── test_real_page_navigation.py         # Playwright + a real, live webpage
│   └── test_visual_match.py                 # OpenCV template matching against synthetic images
├── venv/                # Local virtual environment (not committed)
├── pytest.ini            # testpaths config
├── requirements.txt      # Frozen dependency versions
├── .gitignore
└── README.md
```

---

## The core design

### 1. The contract (`locators/modular_locator_framework.py`)

Every locator strategy implements one method:

```python
class LocatorStrategy(ABC):
    @abstractmethod
    def find(self, dom, target):
        """Return the matching element, or None if not found."""

    @property
    @abstractmethod
    def name(self):
        """A short name for this strategy, used in reporting."""
```

### 2. The engine

```python
class ModularLocatorEngine:
    def __init__(self, strategies):
        self.strategies = strategies

    def find_element(self, dom, target):
        for strategy in self.strategies:
            result = strategy.find(dom, target)
            if result:
                return {"target": target, "strategy_used": strategy.name, "element": result}
        return {"target": target, "strategy_used": "none_found", "element": None}
```

The engine has **zero knowledge** of Selenium, Playwright, or anything else
— it only ever calls `strategy.find(dom, target)`. That's what makes the
underlying tool swappable without touching the engine.

### 3. In-memory strategies (for learning/demo purposes)

- `ExactIdStrategy` — matches on `id`
- `VisibleTextStrategy` — matches on visible text
- `ClassAndTagStrategy` — matches on CSS class + tag
- `NearbyLabelStrategy` — matches on an ARIA label

### 4. Playwright strategies (`locators/playwright_strategies.py`)

The same pattern, backed by a real `Page` object:

- `PlaywrightExactIdStrategy` — `page.locator(f"#{target}")`
- `PlaywrightVisibleTextStrategy` — `page.get_by_text(...)`
- `PlaywrightRoleStrategy` — `page.get_by_role(...)`, matching by
  accessibility role + accessible name (often the most resilient option,
  since roles rarely change even when markup does)

Swapping from Selenium to Playwright (or anything else) only means writing
new strategy classes — `ModularLocatorEngine` itself never changes.

### 5. Visual matching strategy (`locators/visual_match_strategy.py`)

Every strategy above matches against a DOM — an ID, some text, a class, an
ARIA label. `VisualMatchStrategy` is the fallback for when none of that
exists (an icon-only button with no reliable attributes, say): it matches
by *appearance* instead, using OpenCV template matching to search a
full-page screenshot for the region that looks most like a small reference
image of just the element.

- `VisualMatchStrategy` — `cv2.matchTemplate` against a full-page
  screenshot; returns the best match's position + confidence, or `None`
  below `confidence_threshold`
- Same `LocatorStrategy` contract as every other module, with one twist:
  `find()`'s `dom` argument is repurposed as a screenshot *path* rather
  than a DOM list, since this strategy has nothing to search but pixels —
  see the class's own docstring, and `test_visual_match.py`'s
  `test_plugs_into_modular_locator_engine_as_a_fallback_strategy` for how
  that looks plugged into the real engine

---

## CI/CD dashboard (GitHub Actions + Pages)

`.github/workflows/tests-dashboard.yml` runs on every push to `main`:

1. Sets up Python, installs dependencies from `requirements.txt`
2. Installs the Chromium browser binary
3. Runs the full test suite, producing both a machine-readable
   `report.json` (via `pytest-json-report`) and a detailed
   `report.html` (via `pytest-html`)
4. `scripts/generate_dashboard.py` turns `report.json` into a styled,
   self-contained `dashboard.html` — a hero pass-rate figure, a
   pass/fail/skip meter, stat tiles, and a per-test results table,
   with light/dark mode
5. Publishes `dashboard.html` to **GitHub Pages** as `index.html` (with
   the full `report.html` alongside it, linked from the dashboard's
   footer as "Detailed report") — regardless of whether tests passed or
   failed, so the dashboard always reflects the latest run
6. The workflow itself still reports red/green correctly in the Actions
   tab (a failing test fails the workflow run, even though the dashboard
   still gets published)

**One-time setup required in the GitHub UI** (can't be done via git push):
Repo → **Settings → Pages → Build and deployment → Source** → select
**"GitHub Actions"**. After that, every push to `main` auto-updates the
dashboard at the live URL linked at the top of this README.

You can also trigger a run manually from the **Actions** tab via
"Run workflow" (the `workflow_dispatch` trigger), or regenerate the
dashboard locally:

```bash
pytest --json-report --json-report-file=report.json
python scripts/generate_dashboard.py report.json dashboard.html
```

Then open `dashboard.html` directly in a browser.

---

## Setup from scratch

**Prerequisites:** Python 3.10+ (developed against 3.14).

```bash
cd python-playwright

# 1. Create and activate a virtual environment
python -m venv venv
venv\Scripts\activate          # Windows
# source venv/bin/activate     # macOS/Linux

# 2. Install dependencies
pip install -r requirements.txt

# 3. Install the Chromium browser binary Playwright drives
python -m playwright install chromium
```

---

## Running the tests

Run everything (headless, default):

```bash
pytest -v
```

Run only the pure-Python unit tests (fastest, no browser):

```bash
pytest tests/test_modular_locator_framework.py -v
```

Run **headed** so you can actually watch Chromium open and interact with
pages (add `--slowmo` in milliseconds to slow it down for observation):

```bash
pytest --headed --slowmo 500 -v
```

Generate a browsable HTML report (self-contained — open the file directly,
no server needed):

```bash
pytest --html=report.html --self-contained-html
```

Then open `report.html` in any browser to see pass/fail status per test.

---

## What each test file actually validates

| File | What it proves |
|---|---|
| `test_modular_locator_framework.py` | The engine correctly falls through a chain of strategies, and correctly reports `none_found` when nothing matches — pure logic, no browser. |
| `test_playwright_locator_strategy.py` | The same fallback behavior works when strategies are backed by a real Chromium page instead of a Python list. |
| `test_real_page_navigation.py` | The framework works end-to-end against a real, unmodified website: fills in a login form by `id`, and — because the real submit button has no `id` — falls back to a role-based (`PlaywrightRoleStrategy`) lookup to find and click it, then asserts on the resulting page state. A second test verifies a failed-login flash message via `PlaywrightVisibleTextStrategy`. Both fallbacks trigger the auto-healing warning log described above. |

As an end user, the meaningful signal is the **pass/fail result and which
`strategy_used` was reported** — not what flashes on screen in headed mode
(headless is the default and is what CI would run).

---

## Extending the framework

To add a new locator strategy (e.g. CSS selector, XPath, test-id
attribute), implement `LocatorStrategy`:

```python
class PlaywrightTestIdStrategy(LocatorStrategy):
    name = "playwright_test_id"

    def __init__(self, page):
        self.page = page

    def find(self, dom, target):
        locator = self.page.get_by_test_id(target)
        if locator.count() > 0:
            return locator.first
        return None
```

Then plug it into any engine's strategy list, in whatever priority order
makes sense:

```python
engine = ModularLocatorEngine(strategies=[
    PlaywrightExactIdStrategy(page),
    PlaywrightTestIdStrategy(page),
    PlaywrightRoleStrategy(page, role="button", accessible_name="Submit"),
])
```

No other code needs to change.

---

## Tech stack

- Python
- Playwright (sync API)
- pytest
- GitHub Actions (CI)

---

## Usage

```python
from playwright.sync_api import sync_playwright
from locators.modular_locator_framework import ModularLocatorEngine
from locators.playwright_strategies import PlaywrightExactIdStrategy, PlaywrightVisibleTextStrategy

with sync_playwright() as p:
    browser = p.chromium.launch()
    page = browser.new_page()
    page.goto("https://example.com")

    engine = ModularLocatorEngine(strategies=[
        PlaywrightExactIdStrategy(page),
        PlaywrightVisibleTextStrategy(page, expected_text="Submit"),
    ])

    result = engine.find_element(dom=None, target="submit-btn")
    if result["element"]:
        result["element"].click()

    browser.close()
```

---

## Test coverage snapshot

9 automated tests, run via pytest and GitHub Actions CI. This table is a
point-in-time snapshot — for current results, use the **live dashboard**
linked at the top of this README rather than this table.

| Module | Test | Status | Duration |
|---|---|---|---|
| modular locator framework | test_exact_id_match | ✓ Passed | 1 ms |
| modular locator framework | test_falls_through_to_next_strategy_when_id_is_stale | ✓ Passed | 1 ms |
| modular locator framework | test_no_strategy_matches | ✓ Passed | 1 ms |
| modular locator framework | test_warning_logged_when_locator_heals | ✓ Passed | 1 ms |
| modular locator framework | test_no_warning_logged_when_primary_strategy_matches | ✓ Passed | 1 ms |
| playwright locator strategy | test_engine_finds_element_via_playwright_exact_id [chromium] | ✓ Passed | 760 ms |
| playwright locator strategy | test_engine_falls_back_to_visible_text_when_id_is_stale [chromium] | ✓ Passed | 3.09 s |
| real page navigation | test_locator_engine_logs_in_on_a_real_page [chromium] | ✓ Passed | 4.15 s |
| real page navigation | test_locator_engine_reports_failed_login_via_visible_text [chromium] | ✓ Passed | 4.04 s |

**Total: 9 passed, 0 failed, 0 skipped — 12.09s**

This covers unit-level tests of the fallback logic itself, plus end-to-end
Playwright tests against a real browser, including a full login-flow
scenario proving the healing behavior works on an actual page, not just
simulated data.

---

## Status

This is a personal learning project exploring resilient test automation
design patterns. It is not a production system and is not affiliated with
any employer or commercial product — built independently to understand and
demonstrate the Strategy pattern applied to test automation.

## Author

Kumaraswamy B
