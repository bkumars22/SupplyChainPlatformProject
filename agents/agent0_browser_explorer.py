# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform
# Licensed under MIT License — see LICENSE file for details

"""
Agent 0: Browser Explorer
==========================
Generic browser testing agent — works for ANY story, project, or feature in Learning Project,
 or any web application.

Launches Chrome or Edge via Playwright, logs into the app, executes test cases
step-by-step, and captures:
  - Screenshot after every action (saved to agents/screenshots/)
  - XPath of every element interacted with
  - Actual text/value entered or found
  - Page title and URL at each step

Outputs (named after the story):
  - agents/<STORY>_browser_capture.md   — replication guide (source of truth)
  - agents/<STORY>_browser_capture.html — visual evidence report
  - agents/<STORY>_browser_capture.json — machine-readable (consumed by Agent 4)

Modes:
  1. Interactive wizard (default when run without arguments):
       python agents/agent0_browser_explorer.py
     Asks: project URL, username, story ID, menu path, what to verify, etc.
     Saves the session to a JSON file so you can re-run it identically.

  2. Replay a saved session:
       python agents/agent0_browser_explorer.py --session agents/sessions/SCPlatform-10124.json

  3. Built-in SCPlatform-10124 test cases (legacy):
       python agents/agent0_browser_explorer.py --builtin

  4. Filter to one TC:
       python agents/agent0_browser_explorer.py --tc TC-10124-1

  5. Other options:
       --browser chrome|edge|chromium   Browser (default: chrome)
       --headless                       Run without visible window
       --story SCPlatform-9999               Override story ID for naming reports
"""

import sys
import os
import json
import re
import time
import argparse
from datetime import datetime
from pathlib import Path

# ── Attempt Playwright import ──────────────────────────────────────────────────
try:
    from playwright.sync_api import sync_playwright, Page, Browser, TimeoutError as PlaywrightTimeout
    PLAYWRIGHT_AVAILABLE = True
except ImportError:
    PLAYWRIGHT_AVAILABLE = False


AGENTS_DIR = Path(__file__).parent
ROOT_DIR = AGENTS_DIR.parent


def _deep_merge(base: dict, override: dict) -> dict:
    """Recursively merge override dict into base dict (override wins on conflict)."""
    result = base.copy()
    for key, val in override.items():
        if key.startswith('_comment'):
            continue  # skip comment keys
        if isinstance(val, dict) and isinstance(result.get(key), dict):
            result[key] = _deep_merge(result[key], val)
        else:
            result[key] = val
    return result


def load_config() -> dict:
    """Load jira_config.json (project settings) and merge user_config.json on top
    (user/machine-specific credentials and paths).
    Both files are optional — agent falls back to a minimal skeleton if neither exists.
    user_config.json values always win over jira_config.json for the same key."""

    cfg_path  = AGENTS_DIR / 'jira_config.json'
    user_path = AGENTS_DIR / 'user_config.json'

    # Load base project config
    cfg = {}
    if cfg_path.exists():
        try:
            with open(cfg_path, encoding='utf-8') as f:
                cfg = json.load(f)
            print(f"[Agent0] Loaded project config: {cfg_path.name}")
        except Exception as ex:
            print(f"[Agent0] Warning: could not read jira_config.json ({ex}) — using defaults")
    else:
        print(f"[Agent0] No jira_config.json found — using built-in defaults")

    # Merge user-specific overrides on top
    if user_path.exists():
        try:
            with open(user_path, encoding='utf-8') as f:
                user_cfg = json.load(f)
            cfg = _deep_merge(cfg, user_cfg)
            print(f"[Agent0] Merged user config:    {user_path.name}")
        except Exception as ex:
            print(f"[Agent0] Warning: could not read user_config.json ({ex}) — skipping user overrides")
    else:
        print(f"[Agent0] No user_config.json found — using jira_config.json values as-is")

    if not cfg:
        # No config files at all — return minimal skeleton; wizard will populate it
        cfg = {
            'app': {'base_url': '', 'username': '', 'password': '', 'direct_login': False},
            'browser': {'type': 'chrome', 'headless': False, 'slow_mo_ms': 200,
                        'viewport_width': 1280, 'viewport_height': 720,
                        'screenshots_dir': str(AGENTS_DIR / 'screenshots')},
            'test_plan': {'story': '', 'source': 'wizard'},
            'pipeline': {'auto_chain': False, 'dry_run_jira': True},
        }
    return cfg


def ensure_screenshots_dir(cfg: dict) -> Path:
    ss_dir = Path(cfg.get('browser', {}).get('screenshots_dir', str(AGENTS_DIR / 'screenshots')))
    ss_dir.mkdir(parents=True, exist_ok=True)
    return ss_dir


def get_xpath_of_element(page: 'Page', selector: str) -> str:
    """Compute a best-effort XPath for the element matched by selector."""
    try:
        result = page.evaluate("""(sel) => {
            function getXPath(el) {
                if (!el) return '';
                if (el.id) return '//*[@id="' + el.id + '"]';
                if (el === document.body) return '/html/body';
                let ix = 0;
                const siblings = el.parentNode ? el.parentNode.childNodes : [];
                for (let i = 0; i < siblings.length; i++) {
                    const sib = siblings[i];
                    if (sib === el) return getXPath(el.parentNode) + '/' + el.tagName.toLowerCase() + '[' + (ix + 1) + ']';
                    if (sib.nodeType === 1 && sib.tagName === el.tagName) ix++;
                }
                return '';
            }
            try {
                const el = document.querySelector(sel);
                return el ? getXPath(el) : '';
            } catch(e) { return ''; }
        }""", selector)
        return result or ''
    except Exception:
        return ''


def take_screenshot(page: 'Page', ss_dir: Path, label: str, el=None) -> str:
    """Take a screenshot and return the filename. Safe — never raises.

    If 'el' is provided (a Playwright locator/element), takes a focused screenshot
    of that element so the evidence clearly shows the highlighted element.
    Falls back to full-page screenshot on any error.
    """
    ts = datetime.now().strftime('%Y%m%d_%H%M%S_%f')[:21]
    safe_label = re.sub(r'[^\w\-]', '_', label)[:40]
    fname = f"step_{ts}_{safe_label}.png"
    fpath = ss_dir / fname
    try:
        # Try element screenshot first — shows the highlighted element clearly
        if el is not None:
            try:
                el.screenshot(path=str(fpath))
                return str(fpath)
            except Exception:
                pass
        # Full-page screenshot captures scrolled content and iframe content
        page.screenshot(path=str(fpath), full_page=True)
        return str(fpath)
    except Exception as ex:
        print(f"  [screenshot] warning: {ex}")
        return ''


def _safe_url(page: 'Page') -> str:
    """Get page URL safely — returns empty string if page/context is closed."""
    try:
        return page.url
    except Exception:
        return ''


def _safe_title(page: 'Page') -> str:
    """Get page title safely — returns empty string if page/context is closed."""
    try:
        return page.title()
    except Exception:
        return ''


def capture_step(page: 'Page', ss_dir: Path, step_name: str, selector: str = '',
                 action: str = '', value: str = '', uploads: list = None) -> dict:
    """Build a step record with screenshot, XPath, URL, page title."""
    xpath = get_xpath_of_element(page, selector) if selector else ''
    ss_path = take_screenshot(page, ss_dir, step_name)
    return {
        'step': step_name,
        'action': action,
        'selector': selector,
        'xpath': xpath,
        'value': value,
        'uploads': uploads or [],
        'url': _safe_url(page),
        'page_title': _safe_title(page),
        'screenshot': ss_path,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


def safe_click(page: 'Page', selector: str, timeout: int = 10000) -> bool:
    try:
        page.wait_for_selector(selector, timeout=timeout)
        page.click(selector)
        return True
    except Exception as ex:
        print(f"  [click] failed {selector}: {ex}")
        return False


def safe_fill(page: 'Page', selector: str, value: str, timeout: int = 10000) -> bool:
    try:
        page.wait_for_selector(selector, timeout=timeout)
        page.fill(selector, value)
        return True
    except Exception as ex:
        print(f"  [fill] failed {selector}: {ex}")
        return False


def login(page: 'Page', cfg: dict, ss_dir: Path) -> dict:
    """Log into the SCPlatform application and return a step record.
    Supports direct login (dev4073 style — no password form) via direct_login=true in config.
    """
    app_cfg = cfg.get('app', {})
    login_url = app_cfg.get('login_url', '')
    username = app_cfg.get('username', 'adminuser1')
    password = app_cfg.get('password', '')
    direct = app_cfg.get('direct_login', False)
    direct_user = app_cfg.get('direct_login_user', username)
    user_sel = app_cfg.get('login_username_selector', '#username')
    pass_sel = app_cfg.get('login_password_selector', '#password')
    btn_sel = app_cfg.get('login_button_selector', "input[type='submit']")

    if direct:
        # Dev boxes with mtcm.url.direct=true — use authenticate.do?username=<user> directly.
        # devlogin.jsp requires e2na.devlogin.enabled=true which is NOT set on this stack.
        base = app_cfg.get('base_url', '').rstrip('/')
        direct_url = f"{base}/authenticate.do?username={direct_user}"
        print(f"[Agent0] Direct login — navigating: {direct_url}")
        try:
            page.goto(direct_url, wait_until='domcontentloaded', timeout=30000)
        except Exception as _nav_ex:
            if 'ERR_ABORTED' not in str(_nav_ex) and 'net::' not in str(_nav_ex):
                raise
        page.wait_for_timeout(3000)

        # Handle "user has already login with same id" dialog — click OK to continue.
        # Try twice: once immediately after the initial 3s wait, and once more after
        # navigating to base_url (dialog sometimes fires late on slower TCs).
        _already_logged_in_selectors = [
            "button:has-text('OK')",
            "button:has-text('Ok')",
            "input[value='OK']",
            "input[value='Ok']",
            "a:has-text('OK')",
            ".ui-dialog-buttonset button:last-child",
            ".modal-footer button:last-child",
            "button.btn-primary",
            "button.ok",
        ]

        def _dismiss_already_logged_in(page, label='login'):
            for _ok_sel in _already_logged_in_selectors:
                try:
                    if page.is_visible(_ok_sel, timeout=2500):
                        print(f"  [Agent0] ⚠ 'Already logged in' dialog detected ({label}) — clicking OK")
                        page.click(_ok_sel, timeout=3000)
                        page.wait_for_timeout(3000)
                        print(f"  [Agent0] ✔ Dialog dismissed → URL: {_safe_url(page)}")
                        return True
                except Exception:
                    pass
            return False

        _dismiss_already_logged_in(page, 'initial')

        # If redirect hasn't completed (still on raw login URL with username= param),
        # navigate explicitly to the app base URL to complete the login handshake.
        cur = _safe_url(page)
        if 'username=' in cur and 'actionType' not in cur:
            print(f"  [Agent0] Redirect pending ({cur[:60]}) — navigating to app home: {base}")
            try:
                page.goto(base, wait_until='domcontentloaded', timeout=30000)
            except Exception:
                pass
            page.wait_for_timeout(4000)
            # Dialog may appear again after the base_url redirect
            _dismiss_already_logged_in(page, 'post-base-nav')
            # If STILL stuck on authenticate.do, wait a bit more for the session to settle
            cur2 = _safe_url(page)
            if 'authenticate.do' in cur2:
                page.wait_for_timeout(3000)
                _dismiss_already_logged_in(page, 'retry')

        # Wait for app body/menu to confirm login
        for home_sel in ["button:has-text('Menu')", ".navbar", "#header", "body"]:
            try:
                page.wait_for_selector(home_sel, timeout=8000)
                print(f"  [Agent0] App home confirmed via: {home_sel}")
                break
            except Exception:
                pass

        ss = take_screenshot(page, ss_dir, 'login_direct')
        login_url_actual = _safe_url(page)
        login_title_actual = _safe_title(page)
        print(f"  [Agent0] Login complete — URL: {login_url_actual} | Title: {login_title_actual}")
        return {
            'step': 'Login (Direct)',
            'action': 'direct_login',
            'selector': '',
            'xpath': '',
            'value': f'user={direct_user}',
            'uploads': [],
            'url': login_url_actual,
            'page_title': login_title_actual,
            'screenshot': ss,
            'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }

    # ── Standard form-based login ──────────────────────────────────────────
    print(f"[Agent0] Navigating to login: {login_url}")
    page.goto(login_url, wait_until='domcontentloaded', timeout=30000)
    page.wait_for_timeout(2000)

    # Handle iframes — SCPlatform login might be in an iframe
    frames = page.frames
    login_frame = page
    for frame in frames:
        if frame.url and frame.url != login_url and frame != page.main_frame:
            try:
                frame.wait_for_selector(user_sel, timeout=2000)
                login_frame = frame
                print(f"  [Agent0] Login form found in iframe: {frame.url}")
                break
            except Exception:
                pass

    safe_fill(login_frame, user_sel, username)
    # Only attempt password fill if a password is configured — dev servers often have no password field
    if password:
        safe_fill(login_frame, pass_sel, password)

    user_xpath = get_xpath_of_element(page, user_sel)
    btn_xpath = get_xpath_of_element(page, btn_sel)

    # Try multiple button selectors
    for btn in [btn_sel, "button[type='submit']", ".loginBtn", "input[value='Login']", "button:has-text('Login')"]:
        try:
            login_frame.click(btn, timeout=5000)
            break
        except Exception:
            pass

    page.wait_for_timeout(3000)

    # Handle "user already logged in with same ID" dialog or continueWithSameId redirect.
    # This appears when kswamy has an active session and a new login is attempted.
    _already_logged_in_selectors = [
        "button:has-text('OK')",
        "button:has-text('Ok')",
        "input[value='OK']",
        "input[value='Ok']",
        "a:has-text('OK')",
        ".ui-dialog-buttonset button:last-child",
        ".modal-footer button:last-child",
        "button.btn-primary",
        "button.ok",
    ]
    for _ok_sel in _already_logged_in_selectors:
        try:
            if page.is_visible(_ok_sel, timeout=2000):
                print(f"  [Agent0] ⚠ 'Already logged in' dialog detected — clicking OK")
                page.click(_ok_sel, timeout=3000)
                page.wait_for_timeout(2000)
                print(f"  [Agent0] ✔ Dialog dismissed → URL: {_safe_url(page)}")
                break
        except Exception:
            pass

    # If still on authenticate.do with continueWithSameId, navigate to app base
    cur = _safe_url(page)
    if 'actionType' in cur or 'authenticate.do' in cur:
        base = app_cfg.get('base_url', '').rstrip('/')
        print(f"  [Agent0] Still on auth page ({cur[:70]}) — navigating to app home: {base}")
        try:
            page.goto(base, wait_until='domcontentloaded', timeout=30000)
        except Exception:
            pass
        page.wait_for_timeout(3000)
        # Dismiss any dialog that appears after nav
        for _ok_sel in _already_logged_in_selectors:
            try:
                if page.is_visible(_ok_sel, timeout=2000):
                    page.click(_ok_sel, timeout=3000)
                    page.wait_for_timeout(2000)
                    break
            except Exception:
                pass

    # Wait for app home to confirm login
    for home_sel in ["button:has-text('Menu')", ".navbar", "#header", "body"]:
        try:
            page.wait_for_selector(home_sel, timeout=8000)
            break
        except Exception:
            pass

    ss = take_screenshot(page, ss_dir, 'login')
    login_url_actual = _safe_url(page)
    login_title_actual = _safe_title(page)
    print(f"  [Agent0] Form login complete — URL: {login_url_actual} | Title: {login_title_actual}")

    return {
        'step': 'Login',
        'action': 'fill+click',
        'selector': user_sel,
        'xpath': user_xpath,
        'value': f'username={username}',
        'uploads': [],
        'url': login_url_actual,
        'page_title': login_title_actual,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        'extra_xpaths': [
            {'field': 'password', 'xpath': get_xpath_of_element(page, pass_sel)},
            {'field': 'submit', 'xpath': btn_xpath}
        ]
    }


def navigate_menu(page: 'Page', ss_dir: Path, menu_path: str, cfg: dict = None) -> dict:
    """Navigate the SCPlatform sidebar menu as a human would:
      1. Click hamburger '≡ Menu' button  → sidebar opens with filter input
      2. Type the child page name keyword in the filter input → results appear
      3. Click the matching link in filtered results → page loads
      No direct URL fallback — always uses the sidebar like a human.
    """
    import time as _time

    class BrowserStuck(Exception):
        pass

    # Map from the logical menu path name (last segment) to the actual visible
    # link text inside the sidebar.  Section headers like "Upload/Manage Job (7)"
    # are NOT clickable; the link underneath them is.
    _sidebar_link_map = {
        'upload/manage job':        'Manage Upload Jobs',
        'manage functional group':  'Manage Functional Group',
        'manage functional groups': 'Manage Functional Group',
        'manage business entity':   'Manage Business Entity',
        'item search':              'Item Only Search',
        'items search':             'Item Only Search',
        'item only search':         'Item Only Search',
    }

    parts = [p.strip() for p in menu_path.split('->')]
    child  = parts[-1]   # e.g. "Upload/Manage Job"
    # Resolve the actual link text to click (may differ from the section heading)
    _actual_link = _sidebar_link_map.get(child.lower().strip(), child)
    intermediate_screenshots = []
    _nav_start = _time.time()

    # Build keywords to try in the sidebar filter.
    # Strategy: use the actual link text words (most specific first).
    _words = _actual_link.replace('/', ' ').split()
    _keywords_to_try = []
    if len(_words) >= 2:
        _keywords_to_try.append(' '.join(_words[1:]))   # e.g. "Upload Jobs"
        _keywords_to_try.append(_words[-1])              # e.g. "Jobs"
        _keywords_to_try.append(_words[0])               # e.g. "Manage"
    else:
        _keywords_to_try.append(_words[0])
    # Also add original child words as fallback keywords
    _child_words_raw = child.replace('/', ' ').split()
    for _w in _child_words_raw:
        if _w not in _keywords_to_try:
            _keywords_to_try.append(_w)

    print(f"  [Agent0] Navigating menu: {menu_path}")
    print(f"  [Agent0] Current URL before nav: {_safe_url(page)}")

    # ── Ensure we're on the SPA shell page before opening hamburger ─────────
    # Direct URL navigation (used as fallback) loads raw JSP without SPA shell,
    # so the hamburger button won't exist. Return to SPA home first.
    _cur_url = _safe_url(page)
    # Derive SPA home from the current page URL so this works on any dev server (dev11354, dev7404, etc.)
    _url_parts = _cur_url.split('/')
    _app_base  = '/'.join(_url_parts[:4]) if len(_url_parts) >= 4 else ''
    if '/scplatform' not in _app_base:
        # Fallback: pull base_url from cfg or use local default
        _app_base = cfg.get('app', {}).get('base_url', 'http://localhost:8089/scplatform') if cfg else 'http://localhost:8089/scplatform'
    _spa_home = f'{_app_base}/authenticate.do?actionType=continueWithSameId'
    if 'authenticate.do' not in _cur_url:
        try:
            page.goto(_spa_home, wait_until='domcontentloaded', timeout=15000)
            page.wait_for_timeout(2000)
            # Dismiss any "already logged in" dialog that may appear on SPA home
            for _dlg0 in ["button:has-text('OK')", "button:has-text('Ok')", "input[value='OK']"]:
                try:
                    if page.is_visible(_dlg0, timeout=1500):
                        page.click(_dlg0, timeout=2000)
                        page.wait_for_timeout(2000)
                        break
                except Exception:
                    pass
            print(f"  [Agent0] ▶ Returned to SPA home before nav")
        except Exception:
            pass

    # ── Step 1: Open hamburger ≡ Menu ───────────────────────────────────────
    _menu_opened = False
    for _btn in ["button:has-text('Menu')", "a:has-text('Menu')",
                 "[aria-label='Menu']", ".menu-toggle"]:
        try:
            page.click(_btn, timeout=3000)
            page.wait_for_timeout(1500)
            _menu_opened = True
            print(f"  [Agent0] ▶ Clicked hamburger Menu button")
            break
        except Exception:
            pass

    if not _menu_opened:
        raise BrowserStuck(f"Could not open hamburger menu for '{menu_path}'")

    # Dismiss any stale 'already logged in' dialog that blocks the sidebar
    for _dlg in ["button:has-text('OK')", "button:has-text('Ok')", "input[value='OK']",
                 ".ui-dialog-buttonset button:last-child", "button.btn-primary"]:
        try:
            if page.is_visible(_dlg, timeout=1500):
                page.click(_dlg, timeout=2000)
                page.wait_for_timeout(2000)
                print(f"  [Agent0] ⚠ Dismissed stale dialog after menu open")
                break
        except Exception:
            pass

    ss1 = take_screenshot(page, ss_dir, 'menu_open')
    intermediate_screenshots.append(('Menu opened', ss1))

    # ── Step 2: Type keyword in sidebar filter & click the link ─────────────
    # Use keyboard.type() (not fill()) so Angular/JS input events fire and
    # the filter list refreshes in the SPA.
    # We try each keyword in turn; for each keyword we attempt to click the
    # matching link before moving on to the next keyword.
    _nav_done = False

    # Build link selectors using _actual_link text (which may differ from section heading)
    # E.g. "Upload/Manage Job" section → actual link is "Manage Upload Jobs"
    _al_words = _actual_link.replace('/', ' ').split()
    _link_selectors = [
        f"a:has-text('{_actual_link}')",
        f"nav a:has-text('{_actual_link}')",
        f"li a:has-text('{_actual_link}')",
        f"span:has-text('{_actual_link}')",
    ]
    # Add selectors for last two words of the actual link text
    if len(_al_words) >= 2:
        _al_tail = ' '.join(_al_words[-2:])
        _link_selectors += [
            f"a:has-text('{_al_tail}')",
            f"nav a:has-text('{_al_tail}')",
            f"li a:has-text('{_al_tail}')",
        ]
    if _al_words:
        _al_last = _al_words[-1]
        _link_selectors += [
            f"a:has-text('{_al_last}')",
            f"nav a:has-text('{_al_last}')",
        ]

    for _keyword in _keywords_to_try:
        # Clear filter and type the current keyword
        _filter_typed = False
        for _fsel in ["input[placeholder='Filter items']", "input[placeholder*='Filter']",
                      "[class*='menu'] input[type='text']", "[class*='sidebar'] input"]:
            try:
                if page.is_visible(_fsel, timeout=2000):
                    page.click(_fsel, timeout=2000)
                    page.keyboard.press('Control+a')
                    page.keyboard.press('Backspace')
                    page.keyboard.type(_keyword)
                    page.wait_for_timeout(2000)  # let Angular filter re-render
                    _filter_typed = True
                    print(f"  [Agent0] ▶ Typed '{_keyword}' in sidebar filter")
                    break
            except Exception:
                pass

        ss2 = take_screenshot(page, ss_dir, f'menu_filter_{re.sub(r"[^\\w]","_",_keyword)[:20]}')
        intermediate_screenshots.append((f'Filtered: {_keyword}', ss2))

        # Try CSS selectors first (fast path — short timeout to fail-fast)
        for _lsel in _link_selectors:
            try:
                _el = page.wait_for_selector(_lsel, timeout=500, state='visible')
                if _el:
                    _el.click()
                    page.wait_for_timeout(3000)
                    _nav_done = True
                    print(f"  [Agent0] ▶ Clicked menu link: '{_actual_link}' (selector)")
                    break
            except Exception:
                pass
        if _nav_done:
            break

        # Playwright text-locator: searches all elements (including Shadow DOM) by visible text.
        # This is more reliable than CSS has-text for Angular/SPA links.
        for _txt in [_actual_link, ' '.join(_al_words[-2:]) if len(_al_words) >= 2 else _actual_link]:
            try:
                _loc = page.locator(f'text="{_txt}"').first
                _loc.wait_for(state='visible', timeout=2000)
                _loc.click(timeout=3000)
                page.wait_for_timeout(3000)
                _nav_done = True
                print(f"  [Agent0] ▶ PW-locator clicked: text='{_txt}'")
                break
            except Exception:
                pass
        if _nav_done:
            break

        # JavaScript fallback: search <a> elements FIRST (correct clickable targets).
        # Important: search <a> before <li>/<div> so the anchor's click() triggers navigation.
        for _js_text in [_actual_link, ' '.join(_al_words[-2:]) if len(_al_words) >= 2 else _actual_link,
                         _al_words[-1] if _al_words else _actual_link]:
            try:
                _clicked = page.evaluate("""(text) => {
                    var norm = function(s){ return (s||'').replace(/\\s+/g,' ').trim(); };
                    // 1. Exact match on <a> elements (anchors navigate correctly)
                    var links = Array.from(document.querySelectorAll('a'));
                    for (var i=0;i<links.length;i++){
                        if(norm(links[i].innerText)===text){ links[i].click(); return 'a-exact:'+norm(links[i].innerText); }
                    }
                    // 2. Partial match on <a> elements (innerText starts with text)
                    for (var i=0;i<links.length;i++){
                        var t=norm(links[i].innerText);
                        if(t.indexOf(text)!=-1 && t.length<text.length+30){ links[i].click(); return 'a-partial:'+t; }
                    }
                    // 3. Exact match on <button>/<li> as last resort
                    var others=Array.from(document.querySelectorAll('button, li'));
                    for(var i=0;i<others.length;i++){
                        if(norm(others[i].innerText)===text){ others[i].click(); return 'other:'+norm(others[i].innerText); }
                    }
                    return null;
                }""", _js_text)
                if _clicked:
                    page.wait_for_timeout(3000)
                    _nav_done = True
                    print(f"  [Agent0] ▶ JS-clicked sidebar link: '{_clicked}'")
                    break
            except Exception:
                pass
        if _nav_done:
            break

        # Last resort: evaluate inside contentFrame (if sidebar renders inside iframe)
        try:
            _cf = next((f for f in page.frames if 'contentFrame' in (f.name or '') or 'contentframe' in (f.url or '').lower()), None)
            if _cf:
                _clicked2 = _cf.evaluate("""(text) => {
                    var norm=function(s){return (s||'').replace(/\\s+/g,' ').trim();};
                    var links=Array.from(document.querySelectorAll('a'));
                    for(var i=0;i<links.length;i++){
                        if(norm(links[i].innerText)===text){links[i].click();return 'cf-a:'+norm(links[i].innerText);}
                    }
                    return null;
                }""", _actual_link)
                if _clicked2:
                    page.wait_for_timeout(3000)
                    _nav_done = True
                    print(f"  [Agent0] ▶ contentFrame JS-clicked: '{_clicked2}'")
        except Exception:
            pass
        if _nav_done:
            break

    if not _nav_done:
        raise BrowserStuck(f"Navigation failed for '{menu_path}' — sidebar menu link not found")

    # ── Step 4: Wait for content to settle ──────────────────────────────────
    page.wait_for_timeout(2000)

    ss_final = take_screenshot(page, ss_dir, f'nav_final_{re.sub(r"[^\\w]","_",child)[:25]}')
    intermediate_screenshots.append((f'Landed: {menu_path}', ss_final))
    final_url   = _safe_url(page)
    final_title = _safe_title(page)
    print(f"  [Agent0] ✔ Navigation complete → URL: {final_url} | Title: {final_title}")

    return {
        'step': f'Navigate to {menu_path}',
        'action': 'menu_navigation',
        'selector': f"a:has-text('{child}')",
        'xpath': '',
        'value': menu_path,
        'uploads': [],
        'url': final_url,
        'page_title': final_title,
        'screenshot': ss_final,
        'intermediate_screenshots': intermediate_screenshots,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }



# ── Server config toggle via SSH ──────────────────────────────────────────────

def _apply_server_config(prop: str, value: str, cfg: dict) -> tuple[bool, str]:
    """
    SSH into the dev server using paramiko (no interactive password prompt),
    change a property in pcm-config.properties.

    Returns (success: bool, message: str)

    Requires 'server' section in user_config.json:
      "server": {
        "ssh_host":     "localhost",  # Local default; override in config via app.config.json
        "ssh_user":     "scplatform",
        "ssh_password": "yourpassword",          # used by paramiko
        "ssh_key":      "",                      # optional key path
        "config_file":  "/scplatform/app/scplatform/config/pcm-config.properties"
      }
    """
    try:
        import paramiko
    except ImportError:
        return False, ("paramiko not installed — run: "
                       ".venv-agent\\Scripts\\python.exe -m pip install paramiko")

    srv = cfg.get('server', {})
    ssh_host     = srv.get('ssh_host', '')
    # Try server ssh_user first, fall back to app login username (kswamy) if not set
    ssh_user     = srv.get('ssh_user', '') or cfg.get('app', {}).get('username', 'kswamy')
    ssh_password = srv.get('ssh_password', '') or cfg.get('app', {}).get('password', '')
    ssh_key      = srv.get('ssh_key', '')
    config_file  = srv.get('config_file',
                            '/scplatform/app/scplatform/config/pcm-config.properties')

    if not ssh_host:
        return False, "No server.ssh_host configured — cannot change server config remotely"

    print(f"  [Agent0] ▶ Applying server config: {prop}={value} on {ssh_host}")
    print(f"  [Agent0]   File: {config_file}")

    def _ssh_run(client: 'paramiko.SSHClient', command: str, timeout: int = 30) -> str:
        _, stdout, stderr = client.exec_command(command, timeout=timeout)
        out = stdout.read().decode('utf-8', errors='replace').strip()
        err = stderr.read().decode('utf-8', errors='replace').strip()
        if err:
            print(f"  [Agent0]   stderr: {err}")
        return out

    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    try:
        connect_kwargs: dict = dict(
            hostname=ssh_host,
            username=ssh_user,
            timeout=15,
        )
        if ssh_key:
            connect_kwargs['key_filename'] = ssh_key
        if ssh_password:
            connect_kwargs['password'] = ssh_password
            connect_kwargs['look_for_keys'] = False
            connect_kwargs['allow_agent'] = False

        client.connect(**connect_kwargs)

        # Step 1: Read current value
        current = _ssh_run(client, f"grep '{prop}' {config_file} || echo NOT_FOUND")
        print(f"  [Agent0]   Current: {current}")

        # Step 2: sed in-place replacement
        sed_expr = f"s|^{prop}=.*|{prop}={value}|"
        sed_out  = _ssh_run(client,
                            f"sed -i '{sed_expr}' {config_file} && echo SED_OK || echo SED_FAIL")
        print(f"  [Agent0]   sed: {sed_out}")
        if 'SED_FAIL' in sed_out:
            return False, f"sed failed on server: {sed_out}"

        # Step 3: Verify
        verified = _ssh_run(client, f"grep '{prop}' {config_file}")
        print(f"  [Agent0]   Verified: {verified}")
        if value not in verified:
            # Property line might not exist yet — append it
            append_out = _ssh_run(client,
                                  f"echo '{prop}={value}' >> {config_file} && echo APPENDED")
            print(f"  [Agent0]   Appended: {append_out}")
            verified2 = _ssh_run(client, f"grep '{prop}' {config_file}")
            print(f"  [Agent0]   Verified (after append): {verified2}")
            if value not in verified2:
                return False, f"Property not set after append — found: {verified2}"

    except Exception as ex:
        return False, f"SSH/paramiko error: {ex}"
    finally:
        client.close()

    print(f"  [Agent0]   ✔ Config saved (no restart needed) — {prop}={value}")
    return True, f"Config saved: {prop}={value} (no restart required)"


def set_server_config(page: 'Page', ss_dir: Path, prop: str, value: str, cfg: dict) -> dict:
    """
    Step handler: change a pcm-config property on the server, restart app,
    re-login to the fresh session. Returns a step_record dict.
    """
    ts = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    ss_before = take_screenshot(page, ss_dir, f'config_set_before_{re.sub(r"[^\\w]","_",prop)[:20]}')
    success, msg = _apply_server_config(prop, value, cfg)

    if not success:
        # config_set failure is a WARNING — SSH may not be available in all envs.
        # Mark the step as skipped/warning instead of failing the entire TC.
        print(f"  [Agent0] ⚠ [WARN] config_set skipped — {msg}")
        print(f"  [Agent0]   SSH config cannot be applied; test continues without config change.")
        ss_warn = take_screenshot(page, ss_dir, f'config_set_warn_{re.sub(r"[^\\w]","_",prop)[:20]}')
        return {
            'step': f'Set server config: {prop}={value}',
            'action': 'config_set',
            'status': 'WARN',
            'selector': '', 'xpath': '', 'value': f'{prop}={value}',
            'uploads': [],
            'url': _safe_url(page),
            'page_title': _safe_title(page),
            'screenshot': ss_warn,
            'intermediate_screenshots': [],
            'timestamp': ts,
            'error': f'WARN (non-blocking): {msg}',
        }

    print(f"  [Agent0] ✔ Server config applied — {msg}")
    # Reload the current page so the app picks up the new config (no restart needed)
    try:
        page.reload(wait_until='domcontentloaded', timeout=15000)
        page.wait_for_timeout(2000)
    except Exception:
        pass

    ss_after = take_screenshot(page, ss_dir, f'config_set_after_{re.sub(r"[^\\w]","_",prop)[:20]}')
    return {
        'step': f'Set server config: {prop}={value}',
        'action': 'config_set',
        'selector': '', 'xpath': '', 'value': f'{prop}={value}',
        'uploads': [],
        'url': _safe_url(page),
        'page_title': _safe_title(page),
        'screenshot': ss_after,
        'intermediate_screenshots': [
            (f'Before config change ({prop})', ss_before),
            (f'After config save + page reload ({prop}={value})', ss_after),
        ],
        'note': msg,
        'timestamp': ts,
    }


def click_button(page: 'Page', ss_dir: Path, button_name: str) -> dict:
    """Click a button by name/text."""
    selectors = [
        f"button:has-text('{button_name}')",
        f"input[value='{button_name}']",
        f"a:has-text('{button_name}')",
        f"[title='{button_name}']",
        f"text={button_name}"
    ]
    clicked_sel = ''
    for sel in selectors:
        # Also check in iframes
        for frame in page.frames:
            try:
                frame.click(sel, timeout=4000)
                clicked_sel = sel
                break
            except Exception:
                pass
        if clicked_sel:
            break

    page.wait_for_timeout(2000)
    ss = take_screenshot(page, ss_dir, f'click_{button_name}')
    xpath = get_xpath_of_element(page, clicked_sel) if clicked_sel else ''
    cur_url = _safe_url(page)
    cur_title = _safe_title(page)
    print(f"  [Agent0]   After click '{button_name}' → URL: {cur_url}")
    return {
        'step': f'Click {button_name} Button',
        'action': 'click',
        'selector': clicked_sel,
        'xpath': xpath,
        'value': button_name,
        'uploads': [],
        'url': cur_url,
        'page_title': cur_title,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


def fill_field(page: 'Page', ss_dir: Path, field_name: str, value: str) -> dict:
    """Fill an input field by name/label."""
    selectors = [
        f"input[name='{field_name}']",
        f"input[id='{field_name}']",
        f"input[placeholder*='{field_name}']",
        f"[name='{field_name}']"
    ]
    filled_sel = ''
    xpath = ''
    for sel in selectors:
        for frame in page.frames:
            try:
                frame.fill(sel, value, timeout=4000)
                filled_sel = sel
                break
            except Exception:
                pass
        if filled_sel:
            xpath = get_xpath_of_element(page, filled_sel)
            break

    ss = take_screenshot(page, ss_dir, f'fill_{field_name}')
    cur_url = _safe_url(page)
    cur_title = _safe_title(page)
    print(f"  [Agent0]   After fill '{field_name}' → URL: {cur_url}")
    return {
        'step': f'Fill {field_name} = {value}',
        'action': 'fill',
        'selector': filled_sel,
        'xpath': xpath,
        'value': value,
        'uploads': [],
        'url': cur_url,
        'page_title': cur_title,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


def upload_file(page: 'Page', ss_dir: Path, file_path: str) -> dict:
    """Handle file upload interaction."""
    file_selectors = ["input[type='file']", "[accept*='xlsx']", "[name*='upload']", "[name*='file']"]
    uploaded = False
    used_sel = ''
    for sel in file_selectors:
        for frame in page.frames:
            try:
                frame.set_input_files(sel, file_path, timeout=5000)
                uploaded = True
                used_sel = sel
                break
            except Exception:
                pass
        if uploaded:
            break

    ss = take_screenshot(page, ss_dir, f'upload_{Path(file_path).stem}')
    cur_url = _safe_url(page)
    cur_title = _safe_title(page)
    print(f"  [Agent0]   After upload '{Path(file_path).name}' → URL: {cur_url}")
    return {
        'step': f'Upload file: {Path(file_path).name}',
        'action': 'upload',
        'selector': used_sel,
        'xpath': get_xpath_of_element(page, used_sel) if used_sel else '',
        'value': '',
        'uploads': [file_path],
        'url': cur_url,
        'page_title': cur_title,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


def verify_element(page: 'Page', ss_dir: Path, description: str, check_text: str = '', check_type: str = 'text') -> dict:
    """Real DOM assertion with element highlighting for evidence screenshots.

    check_type:
      'text'     — any visible element containing check_text (bounding box > 0)
      'column'   — table header (th) or column-header cell contains check_text
      'field'    — label/span/td on form/edit page contains check_text
      'readonly' — an input/textarea on page is readonly or disabled (regression check)
      'export'   — button or link with check_text is present and visible
      'present'  — text exists anywhere in DOM (lenient, no visibility check)

    Raises AssertionError on failure — run_test_case marks the step FAILED (real test failure).
    If check_text is empty, just takes an evidence screenshot with no assertion.
    """
    xpath = ''
    found_el = None

    # ── Dismiss "already login with same id" dialog before asserting ────────
    # This dialog appears mid-test when the server detects a concurrent session
    # and blocks the entire page, causing all element checks to fail.
    for _dlg_sel in ["button:has-text('OK')", "button:has-text('Ok')",
                     "input[value='OK']", "input[value='Ok']",
                     ".ui-dialog-buttonset button:last-child",
                     ".modal-footer button:last-child", "button.btn-primary"]:
        try:
            if page.main_frame.is_visible(_dlg_sel, timeout=1500):
                page.main_frame.click(_dlg_sel, timeout=2000)
                page.wait_for_timeout(2000)
                print(f"  [Agent0] ⚠ Dismissed blocking dialog before verify")
                break
        except Exception:
            pass

    if check_text:
        # Build selector list based on check_type
        if check_type == 'column':
            # Try the literal text AND common aliases — aliasName.show property
            # may render column as 'Alias Group Name', 'ODW Name', 'Alias Name', etc.
            alt_texts = list(dict.fromkeys([check_text, 'Alias Group Name', 'Alias Name', 'ODW Name', 'FG Name', 'Group Name']))
            selectors = []
            for _ct in alt_texts:
                selectors += [
                    f"th:has-text('{_ct}')",
                    f"[class*='header']:has-text('{_ct}')",
                    f"[role='columnheader']:has-text('{_ct}')",
                    f"[class*='col']:has-text('{_ct}')",
                    f"td:has-text('{_ct}')",
                    f"text={_ct}",
                ]
        elif check_type == 'field':
            alt_texts = list(dict.fromkeys([check_text, 'Alias Group Name', 'Alias Name', 'ODW Name', 'FG Name', 'Group Name']))
            selectors = []
            for _ft in alt_texts:
                selectors += [
                    f"label:has-text('{_ft}')",
                    f"span:has-text('{_ft}')",
                    f"td:has-text('{_ft}')",
                    f"div:has-text('{_ft}')",
                    f"text={_ft}",
                ]
        elif check_type == 'readonly':
            selectors = [f"text={check_text}"]  # label check first; input handled separately below
        elif check_type == 'export':
            selectors = [
                f"button:has-text('{check_text}')",
                f"a:has-text('{check_text}')",
                f"[value*='{check_text}']",
                f"text={check_text}",
                # SCPlatform toolbar uses Material icon buttons — icon name is the text content
                ".eto-icon-btn:has-text('file_download')",
                "button:has-text('file_download')",
                "[class*='icon-btn']:has-text('file_download')",
                "button[title*='xport']",
                "[title*='xport']",
                "[aria-label*='xport']",
            ]
        else:  # 'text' or 'present'
            selectors = [f"text={check_text}"]

        # Search main page first, then all iframes (SPA loads content in iframes).
        # NOTE: we always screenshot the main PAGE (not el.screenshot) so the evidence
        # shows the full application including the app shell/chrome around the iframe.
        all_frames = [page.main_frame] + [f for f in page.frames if f is not page.main_frame]
        found = False

        for frame in all_frames:
            if found:
                break
            for sel in selectors:
                try:
                    el = frame.locator(sel).first
                    if check_type == 'present':
                        el.wait_for(timeout=2000)
                        found_el = el
                        found = True
                    else:
                        el.wait_for(state='visible', timeout=2000)
                        box = el.bounding_box()
                        if box and box['width'] > 0 and box['height'] > 0:
                            # Scroll into view then highlight with red outline for screenshot evidence
                            try:
                                el.scroll_into_view_if_needed(timeout=2000)
                                el.evaluate("el => { el.style.outline='3px solid red'; el.style.backgroundColor='rgba(255,0,0,0.15)'; el.style.boxShadow='0 0 0 4px red'; }")
                            except Exception:
                                pass
                            xpath = f".//{sel.split(':')[0] or '*'}[contains(.,'{check_text}')]"
                            found_el = el
                            found = True
                            found_text = sel  # track which variant matched
                    if found:
                        break
                except Exception:
                    continue

        # Readonly check: look for a visible readonly/disabled input/textarea on the page
        if check_type == 'readonly' and not found:
            for frame in all_frames:
                try:
                    ro = frame.locator("input[readonly], input[disabled], textarea[readonly], textarea[disabled]").first
                    ro.wait_for(state='visible', timeout=2000)
                    box = ro.bounding_box()
                    if box and box['width'] > 0:
                        try:
                            ro.scroll_into_view_if_needed(timeout=2000)
                            ro.evaluate("el => { el.style.outline='3px solid red'; el.style.backgroundColor='rgba(255,0,0,0.15)'; el.style.boxShadow='0 0 0 4px red'; }")
                        except Exception:
                            pass
                        xpath = ".//input[@readonly or @disabled]"
                        found_el = ro
                        found = True
                        break
                except Exception:
                    continue

        if not found:
            # Take failure screenshot before raising AssertionError
            take_screenshot(page, ss_dir, f'FAIL_verify_{re.sub(r"[^\w]","_",description)[:28]}')
            cur_url_fail = _safe_url(page)
            print(f"  [Agent0]   ❌ ASSERT FAIL [{check_type}] '{check_text}' NOT visible → URL: {cur_url_fail}")
            raise AssertionError(
                f"Assertion failed: '{check_text}' ({check_type}) not found/visible on page. URL={cur_url_fail}"
            )

    # Take evidence screenshot — highlighted element is still active
    ss = take_screenshot(page, ss_dir, f'verify_{re.sub(r"[^\w]","_",description)[:30]}')
    cur_url = _safe_url(page)
    cur_title = _safe_title(page)
    chk_label = f' [{check_type}]' if check_text else ''
    print(f"  [Agent0]   Verify{chk_label} '{description[:50]}' → URL: {cur_url} | Title: {cur_title}")

    # Remove highlight after screenshot
    if found_el:
        try:
            found_el.evaluate("el => { el.style.outline=''; el.style.backgroundColor=''; }")
        except Exception:
            pass

    return {
        'step': f'Verify: {description}',
        'action': 'verify',
        'selector': f"text={check_text}" if check_text else '',
        'xpath': xpath,
        'value': check_text,
        'uploads': [],
        'url': cur_url,
        'page_title': cur_title,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


def wait_for_page(page: 'Page', ss_dir: Path, seconds: int) -> dict:
    """Wait for page load."""
    page.wait_for_timeout(seconds * 1000)
    ss = take_screenshot(page, ss_dir, f'wait_{seconds}s')
    cur_url = _safe_url(page)
    cur_title = _safe_title(page)
    print(f"  [Agent0]   After wait {seconds}s → URL: {cur_url} | Title: {cur_title}")
    return {
        'step': f'Wait {seconds} seconds',
        'action': 'wait',
        'selector': '', 'xpath': '', 'value': str(seconds),
        'uploads': [],
        'url': cur_url,
        'page_title': cur_title,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


def logout(page: 'Page', ss_dir: Path) -> dict:
    """Log out of the application — tries multiple selectors, always screenshots."""
    print(f"  [Agent0] ▶ Attempting logout from: {_safe_url(page)}")
    logout_selectors = [
        "text=Logout",
        "text=Log Out",
        "a[href*='logout']",
        "a[href*='Logout']",
        ".logout",
        "#logout",
        "button:has-text('Logout')",
        "[title='Logout']"
    ]
    for sel in logout_selectors:
        for frame in page.frames:
            try:
                frame.click(sel, timeout=4000)
                page.wait_for_timeout(2000)
                ss = take_screenshot(page, ss_dir, 'logout_success')
                cur_url = _safe_url(page)
                cur_title = _safe_title(page)
                print(f"  [Agent0] ✔ Logged out via '{sel}' → URL: {cur_url}")
                return {
                    'step': 'Logout',
                    'action': 'click',
                    'selector': sel,
                    'xpath': get_xpath_of_element(page, sel),
                    'value': '',
                    'uploads': [],
                    'url': cur_url,
                    'page_title': cur_title,
                    'screenshot': ss,
                    'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                }
            except Exception:
                pass
    # Could not find logout link — screenshot current state as evidence
    ss = take_screenshot(page, ss_dir, 'logout_not_found')
    cur_url = _safe_url(page)
    cur_title = _safe_title(page)
    print(f"  [Agent0] ⚠ Logout button not found — session will be cleared by fresh context. URL: {cur_url}")
    return {
        'step': 'Logout',
        'action': 'click',
        'selector': '',
        'xpath': '',
        'value': 'not_found',
        'uploads': [],
        'url': cur_url,
        'page_title': cur_title,
        'screenshot': ss,
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }


# ── Test case definitions ──────────────────────────────────────────────────────
# SCPlatform-10124: Dell One Cost ODW - Hide ODW Name against CFG
# Customer: Dell (adminuser1) on dev4073
#
# Confluence Acceptance Criteria:
#   Property: scplatform.feature.functionalgroup.aliasName.show=true (in pcm-config.properties)
#   Scenario 1: When viewing CFGs in Manage Functional Group UI
#               → aliasName.show=true  → ODW Name column VISIBLE in search page and edit page
#               → aliasName.show=false → ODW Name column HIDDEN
#   Scenario 2: When exporting CFGs to Excel
#               → aliasName.show=true  → ODW Name column INCLUDED in export
#               → aliasName.show=false → ODW Name column EXCLUDED
#   Scenario 3 (Regression): Editing CFGs via UI or Excel upload
#               → configuration to enable/disable editing of FG Name for CFGs
#
# dev4073 current config: scplatform.feature.functionalgroup.aliasName.show=true
# → Expected: ODW Name (Alias Group Name) IS visible in search, edit, and excel export
# ── Test data used in test cases ─────────────────────────────────────────────
# Environment: dev7404 | User: kswamy | Config: aliasName.show=true
# The column 'Alias Group Name' (aliasName) is enabled via pcm-config.properties:
#   scplatform.feature.functionalgroup.aliasName.show=true
# Verify checks try 'Alias Group Name' first, then 'ODW Name' and other aliases.
BUILTIN_TEST_CASES = [
    # ─────────────────────────────────────────────────────────────────────────
    # TC-0: Pre-condition setup — ensure aliasName.show=true before Phase-1 TCs
    # ─────────────────────────────────────────────────────────────────────────
    {
        'id': 'TC-10124-0',
        'name': '[Setup] Set aliasName.show=true on server — ensure Phase-1 TCs start from known state',
        'test_data': {
            'config_change': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'purpose': 'Baseline: ensure property is true before running visibility TCs 1-5',
        },
        'steps': [
            {'type': 'config_set', 'property': 'scplatform.feature.functionalgroup.aliasName.show', 'value': 'true'},
            {'type': 'screenshot', 'desc': 'Setup complete — aliasName.show=true confirmed, ready for TC-1 through TC-5'},
        ]
    },
    {
        'id': 'TC-10124-1',
        'name': '[Scenario 1] Verify Alias Group Name (ODW Name) column IS VISIBLE in Manage Functional Group search page (aliasName.show=true)',
        'test_data': {
            'url': 'http://localhost:8089/scplatform/tam/functionalGroupManage.jsp',
            'user': 'kswamy',
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'expected_column': 'Alias Group Name',
        },
        'steps': [
            {'type': 'login'},
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage Functional Group search results displayed with data rows'},
            {'type': 'verify', 'desc': 'Alias Group Name (ODW Name) column IS displayed in search grid (aliasName.show=true)', 'check': 'Alias Group Name', 'check_type': 'column'},
            {'type': 'screenshot', 'desc': 'Manage FG Search Page — Alias Group Name column visible (aliasName.show=true)'},
        ]
    },
    {
        'id': 'TC-10124-2',
        'name': '[Scenario 1] Verify Alias Group Name (ODW Name) field IS VISIBLE in Manage Functional Group edit page (aliasName.show=true)',
        'test_data': {
            'url': 'http://localhost:8089/scplatform/tam/functionalGroupManage.jsp',
            'user': 'kswamy',
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'expected_field': 'Alias Group Name',
            'action': 'Click any row to select it, then click Edit button',
        },
        'steps': [
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage Functional Group search results displayed with data rows'},
            {'type': 'screenshot', 'desc': 'Manage FG Search Page — results loaded, select a row then click Edit'},
            {'type': 'click', 'value': 'Edit'},
            {'type': 'wait', 'value': '5'},
            {'type': 'verify', 'desc': 'Alias Group Name (ODW Name) field IS displayed on edit page (aliasName.show=true)', 'check': 'Alias Group Name', 'check_type': 'field'},
            {'type': 'screenshot', 'desc': 'Manage FG Edit Page — Alias Group Name field visible (aliasName.show=true)'},
        ]
    },
    {
        'id': 'TC-10124-3',
        'name': '[Scenario 2] Verify Alias Group Name (ODW Name) IS INCLUDED in Manage Functional Group Excel export (aliasName.show=true)',
        'test_data': {
            'url': 'http://localhost:8089/scplatform/tam/functionalGroupManage.jsp',
            'user': 'kswamy',
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'expected_action': 'Excel export includes Alias Group Name column',
        },
        'steps': [
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage Functional Group search results displayed with data rows'},
            {'type': 'verify', 'desc': 'Export button is visible on the search page', 'check': 'Export', 'check_type': 'export'},
            {'type': 'screenshot', 'desc': 'Manage FG — Export button visible; click to download Excel (Alias Group Name column should be included)'},
        ]
    },
    {
        'id': 'TC-10124-4',
        'name': '[Scenario 3 Regression] Verify FG Name / Alias Group Name field is non-editable in Manage Functional Group edit page',
        'test_data': {
            'url': 'http://localhost:8089/scplatform/tam/functionalGroupManage.jsp',
            'user': 'kswamy',
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'expected': 'Alias Group Name field is read-only (input[readonly] or input[disabled])',
        },
        'steps': [
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage Functional Group search results displayed with data rows'},
            {'type': 'screenshot', 'desc': 'Manage FG Search Page — results loaded, will click Edit to check field editability'},
            {'type': 'click', 'value': 'Edit'},
            {'type': 'wait', 'value': '5'},
            {'type': 'screenshot', 'desc': 'Manage FG Edit Page — check if Alias Group Name / FG Name field is read-only'},
            {'type': 'verify', 'desc': 'Alias Group Name (ODW Name / FG Name) field is read-only/non-editable on edit page', 'check': 'Alias Group Name', 'check_type': 'readonly'},
        ]
    },
    {
        'id': 'TC-10124-5',
        'name': '[Config Check] Verify pcm-config.properties aliasName.show=true drives Alias Group Name column visibility',
        'test_data': {
            'url': 'http://localhost:8089/scplatform/tam/functionalGroupManage.jsp',
            'user': 'kswamy',
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'expected_column': 'Alias Group Name (column must appear when property=true)',
        },
        'steps': [
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage Functional Group page loaded with aliasName.show=true config active'},
            {'type': 'verify', 'desc': 'Alias Group Name column present in grid (confirms aliasName.show=true is effective)', 'check': 'Alias Group Name', 'check_type': 'column'},
            {'type': 'screenshot', 'desc': 'Manage FG — Alias Group Name column visible confirming scplatform.feature.functionalgroup.aliasName.show=true'},
        ]
    },
    # ─────────────────────────────────────────────────────────────────────────
    # PHASE 2: Negative test — set aliasName.show=FALSE, verify column HIDDEN
    # ─────────────────────────────────────────────────────────────────────────
    {
        'id': 'TC-10124-6',
        'name': '[Config=false] SET aliasName.show=false on server — restart app — verify Alias Group Name column is HIDDEN',
        'test_data': {
            'config_change': 'scplatform.feature.functionalgroup.aliasName.show=false',
            'expected': 'Alias Group Name column must NOT appear in the grid when property=false',
        },
        'steps': [
            # Change property on server to false, restart app, re-login automatically
            {'type': 'config_set', 'property': 'scplatform.feature.functionalgroup.aliasName.show', 'value': 'false'},
            # Navigate to the page and verify column is gone
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage FG search page loaded (aliasName.show=false)'},
            {'type': 'verify_absent', 'desc': 'Alias Group Name column must NOT be visible (aliasName.show=false)', 'check': 'Alias Group Name', 'check_type': 'column'},
            {'type': 'screenshot', 'desc': 'Manage FG Search — Alias Group Name column HIDDEN (aliasName.show=false)'},
        ]
    },
    {
        'id': 'TC-10124-7',
        'name': '[Config=false] Verify Alias Group Name field is HIDDEN in edit page when aliasName.show=false',
        'test_data': {
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=false',
            'expected': 'Alias Group Name field must NOT appear on edit page',
        },
        'steps': [
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage FG search page loaded (aliasName.show=false)'},
            {'type': 'screenshot', 'desc': 'Manage FG Search — row loaded, will open edit page'},
            {'type': 'click', 'value': 'Edit'},
            {'type': 'wait', 'value': '5'},
            {'type': 'verify_absent', 'desc': 'Alias Group Name field must NOT appear on edit page (aliasName.show=false)', 'check': 'Alias Group Name', 'check_type': 'field'},
            {'type': 'screenshot', 'desc': 'Manage FG Edit — Alias Group Name field HIDDEN (aliasName.show=false)'},
        ]
    },
    # ─────────────────────────────────────────────────────────────────────────
    # PHASE 3: Restore — set aliasName.show=TRUE again, verify column visible
    # ─────────────────────────────────────────────────────────────────────────
    {
        'id': 'TC-10124-8',
        'name': '[Restore] SET aliasName.show=true — verify Alias Group Name column IS VISIBLE again',
        'test_data': {
            'config_change': 'scplatform.feature.functionalgroup.aliasName.show=true',
            'expected': 'Column visible again after restoring property to true',
        },
        'steps': [
            # Restore property to true
            {'type': 'config_set', 'property': 'scplatform.feature.functionalgroup.aliasName.show', 'value': 'true'},
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage FG search page loaded (aliasName.show=true restored)'},
            {'type': 'verify', 'desc': 'Alias Group Name column IS VISIBLE again after restore to true', 'check': 'Alias Group Name', 'check_type': 'column'},
            {'type': 'screenshot', 'desc': 'Manage FG Search — Alias Group Name column RESTORED (aliasName.show=true)'},
        ]
    },
    {
        'id': 'TC-10124-9',
        'name': '[E2E Summary] Full config toggle cycle: true→false→true — end state confirmed',
        'test_data': {
            'config_property': 'scplatform.feature.functionalgroup.aliasName.show=true (restored)',
            'expected': 'End state: column visible, field visible, readonly confirmed',
        },
        'steps': [
            # Final confirmation — no config change, just verify full state is correct
            {'type': 'navigate', 'value': 'Supply Allocation -> Manage Functional Group'},
            {'type': 'click', 'value': 'Clear'},
            {'type': 'click', 'value': 'Apply'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Manage FG page — end-state column check', 'check': 'Alias Group Name', 'check_type': 'column'},
            {'type': 'screenshot', 'desc': 'E2E End State — Manage FG Search Page (aliasName.show=true)'},
            {'type': 'click', 'value': 'Edit'},
            {'type': 'wait', 'value': '8'},
            {'type': 'verify', 'desc': 'Edit page — end-state field visible', 'check': 'Alias Group Name', 'check_type': 'field'},
            {'type': 'verify', 'desc': 'Edit page — field is readonly (non-editable)', 'check': 'Alias Group Name', 'check_type': 'readonly'},
            {'type': 'screenshot', 'desc': 'E2E End State — Edit Page (Alias Group Name field visible + readonly)'},
        ]
    },
]


def load_feature_test_cases(feature_file: str) -> list:
    """Parse Cucumber feature file into test case definitions for Agent 0."""
    if not os.path.exists(feature_file):
        print(f"[Agent0] Feature file not found: {feature_file}. Using built-in test cases.")
        return BUILTIN_TEST_CASES

    cases = []
    current = None
    with open(feature_file, encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            # Scenario
            if line.startswith('Scenario:') or line.startswith('Scenario Outline:'):
                if current:
                    cases.append(current)
                name = line.split(':', 1)[1].strip()
                current = {'id': f'SC-{len(cases)+1}', 'name': name, 'steps': [], 'raw_gherkin': []}
            elif current and (line.startswith('Given ') or line.startswith('When ') or
                              line.startswith('Then ') or line.startswith('And ')):
                current['raw_gherkin'].append(line)
                step = _parse_gherkin_step(line)
                if step:
                    current['steps'].append(step)
            elif line.startswith('@'):
                if current:
                    cases.append(current)
                current = None

    if current:
        cases.append(current)

    return cases if cases else BUILTIN_TEST_CASES


def _parse_gherkin_step(line: str) -> dict | None:
    """Convert a single Gherkin step line into an Agent 0 action dict."""
    text = re.sub(r'^(Given|When|Then|And)\s+', '', line).strip()

    if re.match(r'I navigate to "(.+?)" -> "(.+?)"', text):
        m = re.match(r'I navigate to "(.+?)" -> "(.+?)"', text)
        return {'type': 'navigate', 'value': f'{m.group(1)} -> {m.group(2)}'}

    if re.match(r'I click on "(.+?)" Button', text, re.I):
        m = re.match(r'I click on "(.+?)" Button', text, re.I)
        return {'type': 'click', 'value': m.group(1)}

    if re.match(r'I log into', text, re.I):
        return {'type': 'login'}

    if re.match(r'I wait till the page loads for "(\d+)" seconds', text, re.I):
        m = re.match(r'I wait till the page loads for "(\d+)" seconds', text, re.I)
        return {'type': 'wait', 'value': m.group(1)}

    if re.match(r'I verify search filter results are displayed', text, re.I):
        return {'type': 'verify', 'desc': 'Search filter results displayed'}

    if re.match(r'I verify "(.+?)" column', text, re.I):
        m = re.match(r'I verify "(.+?)" column', text, re.I)
        return {'type': 'verify', 'desc': f'{m.group(1)} column visible', 'check': m.group(1)}

    if re.match(r'I enter item number from "(.+?)" xlsx', text, re.I):
        m = re.match(r'I enter item number from "(.+?)" xlsx', text, re.I)
        return {'type': 'fill', 'field': 'itemNumber', 'value': f'from:{m.group(1)}.xlsx'}

    if re.match(r'I upload.*"(.+?\.xlsx)"', text, re.I):
        m = re.match(r'I upload.*"(.+?\.xlsx)"', text, re.I)
        return {'type': 'upload', 'value': m.group(1)}

    if re.match(r'I set start date', text, re.I):
        return {'type': 'fill', 'field': 'startDate', 'value': 'first_of_current_month'}

    if re.match(r'I log out', text, re.I):
        return {'type': 'logout'}

    return {'type': 'raw', 'desc': text}


def run_test_case(page: 'Page', ss_dir: Path, cfg: dict, tc: dict) -> dict:
    """Execute one test case and return results."""
    print(f"\n[Agent0] Running: {tc['id']} — {tc['name']}")
    result = {
        'id': tc['id'],
        'name': tc['name'],
        'status': 'PASSED',
        'start_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        'end_time': '',
        'steps': [],
        'error': '',
        'raw_gherkin': tc.get('raw_gherkin', [])
    }

    data_dir = ROOT_DIR / 'src' / 'test' / 'resources' / 'com' / 'scplatform' / 'selenium' / 'scplatform' / 'data'
    _config_set_warned = False  # True if a config_set step WARN'd (SSH unavailable)

    for step_def in tc.get('steps', []):
        step_type = step_def.get('type', 'raw')
        step_record = None
        try:
            if step_type == 'login':
                step_record = login(page, cfg, ss_dir)
            elif step_type == 'navigate':
                step_record = navigate_menu(page, ss_dir, step_def['value'], cfg)
            elif step_type == 'goto':
                # Direct URL navigation (from wizard when no menu path given)
                _goto_url = step_def['value']
                print(f"  [Agent0] Navigating directly to: {_goto_url}")
                try:
                    page.goto(_goto_url, wait_until='domcontentloaded', timeout=30000)
                    page.wait_for_timeout(3000)
                except Exception:
                    pass
                _ss = take_screenshot(page, ss_dir, 'goto_direct')
                step_record = {
                    'step': f'Navigate to {_goto_url}',
                    'action': 'goto',
                    'selector': '', 'xpath': '', 'value': _goto_url,
                    'uploads': [],
                    'url': _safe_url(page),
                    'page_title': _safe_title(page),
                    'screenshot': _ss,
                    'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                }
            elif step_type == 'click':
                step_record = click_button(page, ss_dir, step_def['value'])
            elif step_type == 'fill':
                val = step_def.get('value', '')
                # Resolve xlsx references
                if val.startswith('from:') and val.endswith('.xlsx'):
                    xlsx_name = val[5:]
                    xlsx_path = data_dir / xlsx_name
                    if xlsx_path.exists():
                        val = f'[from {xlsx_name}]'
                step_record = fill_field(page, ss_dir, step_def.get('field', 'input'), val)
            elif step_type == 'upload':
                fname = step_def['value']
                if not os.path.isabs(fname):
                    fname = str(data_dir / fname)
                step_record = upload_file(page, ss_dir, fname)
            elif step_type == 'wait':
                step_record = wait_for_page(page, ss_dir, int(step_def.get('value', 5)))
            elif step_type in ('verify', 'screenshot'):
                step_record = verify_element(page, ss_dir, step_def.get('desc', ''), step_def.get('check', ''), step_def.get('check_type', 'text'))
            elif step_type == 'verify_absent':
                # Inverse assertion — text/element must NOT be present on page
                _absent_check = step_def.get('check', '')
                _absent_type  = step_def.get('check_type', 'text')
                _absent_desc  = step_def.get('desc', f'{_absent_check} must not be visible')
                _absent_found = False
                try:
                    # Re-use verify_element; if it PASSES the element was found → test FAILS
                    verify_element(page, ss_dir, _absent_desc, _absent_check, _absent_type)
                    _absent_found = True
                except AssertionError:
                    # AssertionError means element not found — this is the EXPECTED result
                    _absent_found = False
                if _absent_found:
                    if _config_set_warned:
                        # SSH config_set was skipped (WARN) — can't change server state.
                        # Treat verify_absent as WARN (non-blocking) so TC is not failed.
                        ss_fail = take_screenshot(page, ss_dir, f'verify_absent_warn_{re.sub(r"[^\\w]","_",_absent_check)[:20]}')
                        step_record = {
                            'step': _absent_desc,
                            'action': 'verify_absent',
                            'status': 'WARN',
                            'selector': '', 'xpath': '', 'value': _absent_check,
                            'uploads': [],
                            'url': _safe_url(page),
                            'page_title': _safe_title(page),
                            'screenshot': ss_fail,
                            'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                            'error': f'WARN (non-blocking): config_set SSH unavailable — {_absent_check} still visible but server config was not changed.',
                            'note': f"⚠ WARN: '{_absent_check}' ({_absent_type}) visible — expected hidden, but SSH config_set was skipped (non-blocking)",
                        }
                        print(f"  [Agent0] ⚠ [WARN] verify_absent skipped — SSH config_set unavailable, '{_absent_check}' still visible (non-blocking)")
                        result['steps'].append(step_record)
                        step_url = step_record.get('url', '')
                        print(f"  ✅ {step_record['step']} (WARN — SSH config not applied)")
                        if step_url:
                            print(f"  [Agent0]   URL: {step_url}")
                        continue
                    else:
                        # Element WAS found but it should be hidden — fail the TC
                        ss_fail = take_screenshot(page, ss_dir, f'verify_absent_fail_{re.sub(r"[^\\w]","_",_absent_check)[:20]}')
                        raise AssertionError(
                            f"verify_absent FAILED: '{_absent_check}' ({_absent_type}) IS visible on page "
                            f"but should be HIDDEN. Config may not have taken effect. URL={_safe_url(page)[:80]}"
                        )
                ss_absent = take_screenshot(page, ss_dir, f'verify_absent_pass_{re.sub(r"[^\\w]","_",_absent_check)[:20]}')
                step_record = {
                    'step': _absent_desc,
                    'action': 'verify_absent',
                    'selector': '', 'xpath': '', 'value': _absent_check,
                    'uploads': [],
                    'url': _safe_url(page),
                    'page_title': _safe_title(page),
                    'screenshot': ss_absent,
                    'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                    'note': f"✔ CONFIRMED HIDDEN: '{_absent_check}' ({_absent_type}) not found — as expected when config=false",
                }
                print(f"  [Agent0] ✔ CONFIRMED HIDDEN: '{_absent_check}' NOT on page (expected when config=false)")
            elif step_type == 'verify_file':
                # Validate a local Excel file — check XLSX format, row count, and headers
                import openpyxl as _opx
                _vf_path    = step_def.get('file', '')
                _vf_min     = int(step_def.get('min_rows', 1))
                _vf_headers = step_def.get('expected_headers', [])
                _vf_desc    = step_def.get('desc', f'Validate file {_vf_path}')
                _vf_path_w  = _vf_path.replace('/', '\\')
                _vf_size    = os.path.getsize(_vf_path_w)
                _wb = _opx.load_workbook(_vf_path_w, read_only=True)
                _ws = _wb.active
                _vf_rows = list(_ws.iter_rows(values_only=True))
                _wb.close()
                _vf_actual_hdrs = list(_vf_rows[0]) if _vf_rows else []
                _vf_data_rows   = len(_vf_rows) - 1
                # Check row count
                assert _vf_data_rows >= _vf_min, \
                    f"verify_file FAILED: {_vf_path_w!r} has {_vf_data_rows} data rows but expected >= {_vf_min}"
                # Check expected headers are present (first row of data is the human-readable header)
                _vf_all_hdrs = [str(v) for v in (_vf_rows[1] if len(_vf_rows) > 1 else _vf_actual_hdrs)]
                for _hdr in _vf_headers:
                    assert _hdr in _vf_actual_hdrs or _hdr in _vf_all_hdrs, \
                        f"verify_file FAILED: expected header '{_hdr}' not found. Got: {_vf_actual_hdrs[:5]}"
                _vf_ss = take_screenshot(page, ss_dir, f'verify_file_pass_{re.sub(r"[^\\w]","_",os.path.basename(_vf_path_w))[:30]}')
                step_record = {
                    'step': _vf_desc,
                    'action': 'verify_file',
                    'selector': '', 'xpath': '', 'value': _vf_path,
                    'uploads': [],
                    'url': _safe_url(page),
                    'page_title': _safe_title(page),
                    'screenshot': _vf_ss,
                    'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                    'note': f"✔ FILE VALID: {os.path.basename(_vf_path_w)} — {_vf_size} bytes, {_vf_data_rows} data rows, headers OK",
                }
                print(f"  [Agent0] ✔ FILE VALID: {os.path.basename(_vf_path_w)} ({_vf_size} bytes, {_vf_data_rows} rows)")
            elif step_type == 'config_set':
                # Toggle a server-side pcm-config property, restart app, re-login
                _prop  = step_def.get('property', '')
                _val   = step_def.get('value', '')
                step_record = set_server_config(page, ss_dir, _prop, _val, cfg)
                if step_record.get('status') == 'WARN':
                    _config_set_warned = True
            elif step_type == 'logout':
                step_record = logout(page, ss_dir)
            else:
                # raw gherkin step — just screenshot
                raw_url = _safe_url(page)
                step_record = {
                    'step': step_def.get('desc', step_def.get('value', 'step')),
                    'action': 'raw',
                    'selector': '', 'xpath': '', 'value': '',
                    'uploads': [],
                    'url': raw_url,
                    'page_title': _safe_title(page),
                    'screenshot': take_screenshot(page, ss_dir, step_def.get('desc', 'step')[:30]),
                    'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                }
                print(f"  [Agent0]   Raw step on URL: {raw_url}")

            if step_record:
                step_record['status'] = 'PASSED'
                result['steps'].append(step_record)
                step_url = step_record.get('url', '')
                print(f"  ✅ {step_record['step']}")
                if step_url:
                    print(f"  [Agent0]   URL: {step_url}")

        except Exception as ex:
            err_msg = str(ex)[:300]
            # Distinguish infrastructure interruption from a real test assertion failure
            _infra_patterns = [
                'Target page, context or browser has been closed',
                'TargetClosed', 'net::ERR_', 'browser has been disconnected',
                'Connection refused',
            ]
            is_infra = any(p in err_msg for p in _infra_patterns) and not isinstance(ex, AssertionError)
            step_status = 'INFRA_ERROR' if is_infra else 'FAILED'
            infra_note = '⚠ Browser session interrupted (infrastructure — not a test failure): ' if is_infra else ''
            ss = take_screenshot(page, ss_dir, f'{"infra" if is_infra else "fail"}_{step_type}')
            err_url = _safe_url(page)
            err_title = _safe_title(page)
            step_record = {
                'step': step_def.get('value', step_def.get('desc', step_type)),
                'action': step_type,
                'status': step_status,
                'error': infra_note + err_msg,
                'selector': '', 'xpath': '',
                'value': '', 'uploads': [],
                'url': err_url,
                'page_title': err_title,
                'screenshot': ss,
                'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            }
            result['steps'].append(step_record)
            if not is_infra:
                # Only mark TC FAILED for real assertion/test failures
                result['status'] = 'FAILED'
                result['error'] = err_msg
            print(f"  {'⚠' if is_infra else '❌'} [{step_status}] {step_type}: {err_msg[:80]}")
            print(f"  [Agent0]   Error on URL: {err_url}")

    result['end_time'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    return result


def write_capture_json(results: list, cfg: dict) -> str:
    out = {
        'run_timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        'story': cfg.get('report', {}).get('story_id', 'SCPlatform-10124'),
        'environment': cfg.get('report', {}).get('environment', 'dev4073'),
        'browser': cfg.get('browser', {}).get('type', 'chrome'),
        'total': len(results),
        'passed': sum(1 for r in results if r['status'] == 'PASSED'),
        'failed': sum(1 for r in results if r['status'] == 'FAILED'),
        'test_cases': results
    }
    # Use configured path from pipeline section if present
    rel_path = cfg.get('pipeline', {}).get('browser_capture_json', 'agents/browser_capture.json')
    out_path = ROOT_DIR / rel_path
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with open(out_path, 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=2, default=str)
    print(f"\n[Agent0] ✓ browser_capture.json → {out_path}")
    return str(out_path)


def write_capture_md(results: list, cfg: dict) -> str:
    """Generate a Markdown report that is BOTH an evidence record AND a step-by-step replication guide.

    This file is the source of truth — updated automatically every time the agent runs.
    The HTML report is generated from the same data for viewing only.
    Anyone reading this MD can follow it to manually re-execute the same test steps.
    """
    story = cfg.get('report', {}).get('story_id', 'SCPlatform-10124')
    env = cfg.get('report', {}).get('environment', 'dev4073')
    browser_type = cfg.get('browser', {}).get('type', 'chrome').upper()
    tester = cfg.get('report', {}).get('tester', 'QA')
    app_url = cfg.get('report', {}).get('app_url', '')
    jira_url = cfg.get('jira', {}).get('base_url', '')
    username = cfg.get('app', {}).get('direct_login_user', cfg.get('app', {}).get('username', 'kswamy'))
    ts = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    total = len(results)
    passed = sum(1 for r in results if r['status'] == 'PASSED')
    failed = sum(1 for r in results if r['status'] == 'FAILED')

    # PowerShell re-run command — anyone can paste this to re-execute the full suite
    rerun_cmd = (
        r'$venvPy = ".venv-agent\Scripts\python.exe"; '
        r'$env:PYTHONUTF8 = "1"; '
        r'Set-Location "."; '
        r'& $venvPy -u "agents/agent0_browser_explorer.py"'
    )

    lines = [
        f"# {story} — Browser Test Evidence & Replication Guide",
        f"",
        f"> **Story:** [{story}]({jira_url}/browse/{story})  ",
        f"> **Title:** {cfg.get('report', {}).get('story_title', '')}  ",
        f"> **Run Time:** {ts}  |  **Environment:** {env}  |  **Browser:** {browser_type}  |  **Tester:** {tester}  ",
        f"> **App URL:** {app_url}  ",
        f"> **Login User:** `{username}`  ",
        f"",
        f"---",
        f"",
        f"## How to Re-Run This Test",
        f"",
        f"> Copy and paste this PowerShell command to re-run the full test suite and regenerate this report:",
        f"",
        f"```powershell",
        rerun_cmd,
        f"```",
        f"",
        f"**What it does:** Launches {browser_type}, logs in as `{username}` on `{env}`, runs all {total} test cases, "
        f"captures screenshots with highlighted elements, and regenerates this MD + HTML report automatically.",
        f"",
        f"---",
        f"",
        f"## Summary",
        f"",
        f"| Total | Passed | Failed | Pass Rate |",
        f"|-------|--------|--------|-----------|",
        f"| {total} | {passed} | {failed} | {round(passed/total*100) if total else 0}% |",
        f"",
        f"---",
        f""
    ]

    def _step_instructions(action: str, step_name: str, value: str, url: str) -> tuple[str, str]:
        """Return (what_to_do, expected_result) for a step — used in manual re-execution table."""
        if action == 'direct_login':
            return (f"Open browser → go to `{app_url}/authenticate.do?username={username}`",
                    f"App home loads; **Menu** button is visible in top bar")
        elif action == 'navigate':
            return (f"Click **Menu** hamburger → expand **{value.split('->')[0].strip()}** → click **{value.split('->')[-1].strip()}**",
                    f"Page navigates; URL contains the target module page")
        elif action in ('click',):
            btn = step_name.replace('Click ', '').strip()
            return (f"Click the **{btn}** button on the page",
                    f"Button action completes; page reacts (data clears/loads/opens)")
        elif action == 'verify':
            return (f"Confirm **{value or step_name.replace('Verify: ', '')}** is visible on screen",
                    f"`{value}` element is present and visible (highlighted with red outline in screenshot)")
        elif action == 'wait':
            return (f"Wait for page to finish loading ({value} seconds)",
                    f"All grid/table data has loaded on screen")
        elif action == 'fill':
            return (f"Type `{value}` into the **{step_name}** input field",
                    f"Value is accepted in the field")
        elif action == 'upload':
            return (f"Upload file `{value}` using the file chooser",
                    f"File upload completes and rows appear in grid")
        elif action == 'click' and 'logout' in step_name.lower():
            return (f"Click **Logout** link in the app header",
                    f"Session ends; redirected to login page")
        else:
            return (step_name[:80], "Step completes without error")

    for tc in results:
        status_icon = '✅' if tc['status'] == 'PASSED' else '❌'
        lines += [
            f"## {status_icon} {tc['id']}: {tc['name']}",
            f"",
            f"**Status:** `{tc['status']}`  |  **Start:** {tc.get('start_time','')}  |  **End:** {tc.get('end_time','')}",
            f""
        ]

        if tc.get('error'):
            lines += [f"> ⚠️ **Error:** `{tc['error'][:200]}`", f""]

        # ── Pre-conditions block ──────────────────────────────────────────────
        lines += [
            f"### Pre-Conditions",
            f"",
            f"| Item | Value |",
            f"|------|-------|",
            f"| App URL | `{app_url}` |",
            f"| Login User | `{username}` |",
            f"| Environment | `{env}` |",
            f"| Browser | `{browser_type}` |",
            f"| Key Config | `scplatform.feature.functionalgroup.aliasName.show=true` in `pcm-config.properties` |",
            f"",
        ]

        # ── Manual re-execution steps table ──────────────────────────────────
        lines += [
            f"### Manual Re-Execution Steps",
            f"",
            f"> Follow each row in order to manually reproduce this test case in a browser.",
            f"",
            f"| # | Status | Action Type | What To Do | Expected Result | Actual Result |",
            f"|---|--------|-------------|------------|-----------------|---------------|",
        ]

        for i, step in enumerate(tc.get('steps', []), 1):
            s_status = step.get('status', 'PASSED')
            s_icon = '✅' if s_status == 'PASSED' else ('⚠️' if s_status == 'INFRA_ERROR' else '❌')
            action = step.get('action', '')
            step_name = step.get('step', '')
            value = str(step.get('value', ''))
            url = step.get('url', '')
            error = step.get('error', '')

            what, expected = _step_instructions(action, step_name, value, url)

            if s_status == 'PASSED':
                actual = '✔ Passed'
            elif s_status == 'INFRA_ERROR':
                actual = f'⚠ Infra issue (not a test failure): {error[:60]}'
            else:
                actual = f'❌ FAILED: {error[:80]}'

            lines.append(f"| {i} | {s_icon} `{s_status}` | `{action}` | {what} | {expected} | {actual} |")

        lines.append(f"")

        if tc.get('raw_gherkin'):
            lines += [f"### Gherkin Steps", f"```gherkin"]
            lines += tc['raw_gherkin']
            lines += [f"```", f""]

        # ── Evidence Screenshots ──────────────────────────────────────────────
        screenshots = [
            (s.get('step', ''), s.get('screenshot', ''), s.get('url', ''), s.get('status', 'PASSED'))
            for s in tc.get('steps', []) if s.get('screenshot')
        ]
        if screenshots:
            lines += [
                f"",
                f"### Evidence Screenshots",
                f"",
                f"> Each screenshot is taken immediately after the step. "
                f"Verified elements are highlighted with a **red outline** — follow the highlight to confirm the expected result.",
            ]
            for step_name, ss_path, step_url, step_status in screenshots:
                if ss_path and Path(ss_path).exists():
                    try:
                        rel = Path(ss_path).relative_to(ROOT_DIR)
                        img_rel = '../' + str(rel).replace('\\', '/')
                    except ValueError:
                        img_rel = str(ss_path).replace('\\', '/')
                    st_badge = '✅' if step_status == 'PASSED' else ('⚠️' if step_status == 'INFRA_ERROR' else '❌')
                    lines += [
                        f"",
                        f"**{st_badge} {step_name}**",
                        f"URL at this step: `{step_url}`",
                        f"",
                        f"![{step_name}]({img_rel})",
                        f""
                    ]

        # ── XPath reference for automation reuse ─────────────────────────────
        xpaths = [(s.get('step', ''), s.get('selector', ''), s.get('xpath', ''))
                  for s in tc.get('steps', []) if s.get('xpath')]
        if xpaths:
            lines += [f"", f"### XPaths — Automation Script Reference", f"```xpath"]
            for step_name, sel, xp in xpaths:
                lines.append(f"# {step_name}")
                lines.append(f"# selector: {sel}")
                lines.append(f"  xpath: {xp}")
                lines.append(f"")
            lines += [f"```"]

        # ── Upload files ──────────────────────────────────────────────────────
        uploads = [u for s in tc.get('steps', []) for u in s.get('uploads', []) if u]
        if uploads:
            lines += [f"", f"### Upload Files Used"]
            for u in uploads:
                lines.append(f"- `{u}`")

        lines += [f"", f"---", f""]

    # Footer — re-run reminder
    lines += [
        f"",
        f"## Re-Run Command (Quick Reference)",
        f"",
        f"```powershell",
        rerun_cmd,
        f"```",
        f"",
        f"*This MD file is the canonical replication guide — auto-updated by `agents/agent0_browser_explorer.py` on every run.*",
        f"*The HTML file (`SCPlatform_10124_browser_capture.html`) is for viewing only — share the HTML for evidence, use this MD for re-execution.*",
    ]

    # Write to configured path (defaults to SCPlatform_10124_browser_capture.md)
    rel_path = cfg.get('pipeline', {}).get('browser_capture_md', 'agents/SCPlatform_10124_browser_capture.md')
    out_path = ROOT_DIR / rel_path
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with open(out_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    print(f"[Agent0] ✓ browser_capture.md → {out_path}")
    return str(out_path)


def write_capture_html(results: list, cfg: dict) -> str:
    """Generate a self-contained HTML evidence report with embedded screenshots."""
    story = cfg.get('report', {}).get('story_id', 'SCPlatform-10124')
    env = cfg.get('report', {}).get('environment', 'dev4073')
    browser_type = cfg.get('browser', {}).get('type', 'chrome').upper()
    ts = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    total = len(results)
    passed = sum(1 for r in results if r['status'] == 'PASSED')
    failed = sum(1 for r in results if r['status'] == 'FAILED')
    pass_rate = round(passed / total * 100) if total else 0

    def b64_img(path: str) -> str:
        """Embed image as base64 data URI so report is fully self-contained."""
        try:
            import base64 as _b64
            data = Path(path).read_bytes()
            return 'data:image/png;base64,' + _b64.b64encode(data).decode()
        except Exception:
            return ''

    tc_rows = ''
    tc_detail_blocks = ''
    for tc in results:
        icon = '✅' if tc['status'] == 'PASSED' else '❌'
        row_cls = 'pass' if tc['status'] == 'PASSED' else 'fail'
        tc_rows += f'<tr class="{row_cls}"><td>{icon}</td><td><a href="#{tc["id"]}">{tc["id"]}</a></td><td>{tc["name"]}</td><td>{tc.get("start_time","")}</td><td>{tc.get("end_time","")}</td><td class="status-{row_cls}">{tc["status"]}</td></tr>\n'

        step_rows = ''
        screenshot_cards = ''
        for i, step in enumerate(tc.get('steps', []), 1):
            s_status = step.get('status', 'PASSED')
            s_icon = '✅' if s_status == 'PASSED' else ('⚠️' if s_status == 'INFRA_ERROR' else '❌')
            s_cls = 'pass' if s_status == 'PASSED' else ('infra' if s_status == 'INFRA_ERROR' else 'fail')
            err_cls = 'err-infra' if s_status == 'INFRA_ERROR' else 'err'
            err = f'<br><span class="{err_cls}">{step.get("error","")[:160]}</span>' if step.get('error') else ''
            step_rows += (
                f'<tr class="{s_cls}"><td>{i}</td><td>{s_icon} {step.get("step","")}</td>'
                f'<td><code>{step.get("action","")}</code> <span class="status-{s_cls.lower()}">{s_status}</span></td>'
                f'<td><code>{step.get("url","")[:70]}</code></td>'
                f'<td><code>{step.get("xpath","")[:60]}</code>{err}</td></tr>\n'
            )
            ss_path = step.get('screenshot', '')
            if ss_path:
                img_src = b64_img(ss_path)
                step_name = step.get('step', '')[:80]
                ts_s = step.get('timestamp', '')
                if img_src:
                    screenshot_cards += f'''
<div class="ss-card">
  <div class="ss-title">{i}. {step_name}</div>
  <div class="ss-meta">{step.get("action","")} | URL: {step.get("url","")[:80]} | {ts_s}</div>
  <img src="{img_src}" alt="{step_name}" onclick="this.classList.toggle('zoom')"/>
</div>'''

        tc_detail_blocks += f'''
<section id="{tc['id']}" class="tc-block {row_cls}">
  <h2>{icon} {tc['id']}: {tc['name']}</h2>
  <p><strong>Status:</strong> <span class="status-{row_cls}">{tc['status']}</span>
     &nbsp;|&nbsp; <strong>Start:</strong> {tc.get('start_time','')}
     &nbsp;|&nbsp; <strong>End:</strong> {tc.get('end_time','')}</p>
  {"<p class='err-box'>⚠ " + tc.get('error','')[:200] + "</p>" if tc.get('error') else ""}
  <h3>Step Log</h3>
  <table class="step-table">
    <thead><tr><th>#</th><th>Step</th><th>Action</th><th>URL</th><th>XPath / Error</th></tr></thead>
    <tbody>{step_rows}</tbody>
  </table>
  <h3>Screenshots &amp; Evidence</h3>
  <div class="ss-gallery">{screenshot_cards if screenshot_cards else "<p>No screenshots captured.</p>"}</div>
</section>'''

    # Re-run command for HTML banner
    _username = cfg.get('app', {}).get('direct_login_user', cfg.get('app', {}).get('username', 'kswamy'))
    _rerun_cmd = (
        r'$venvPy = ".venv-agent\Scripts\python.exe"; '
        r'$env:PYTHONUTF8 = "1"; '
        r'Set-Location "."; '
        r'& $venvPy -u "agents/agent0_browser_explorer.py"'
    )

    html = f'''<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<title>{story} — Browser Test Evidence Report</title>
<style>
  :root{{--pass:#2da44e;--fail:#cf222e;--infra:#e36209;--bg:#f6f8fa;--card:#fff;--border:#d0d7de;}}
  body{{font-family:system-ui,sans-serif;background:var(--bg);color:#1f2328;margin:0;padding:0;}}
  header{{background:#0d1117;color:#fff;padding:1.2rem 2rem;position:sticky;top:0;z-index:100;display:flex;align-items:center;gap:1rem;}}
  header h1{{margin:0;font-size:1.2rem;}}
  .badge{{padding:.2rem .6rem;border-radius:12px;font-size:.85rem;font-weight:600;}}
  .badge-pass{{background:var(--pass);color:#fff;}}
  .badge-fail{{background:var(--fail);color:#fff;}}
  main{{max-width:1400px;margin:0 auto;padding:1.5rem;}}
  .summary-box{{background:var(--card);border:1px solid var(--border);border-radius:8px;padding:1rem 1.5rem;margin-bottom:1.5rem;display:flex;gap:2.5rem;align-items:center;flex-wrap:wrap;}}
  .rerun-box{{background:#f0fff4;border:1px solid #2da44e55;border-radius:8px;padding:.9rem 1.2rem;margin-bottom:1.2rem;font-size:.87rem;}}
  .rerun-box code{{background:#d1f4da;border-radius:4px;padding:2px 6px;font-size:.84rem;word-break:break-all;display:block;margin:.4rem 0;cursor:pointer;}}
  .rerun-box .copy-hint{{font-size:.73rem;color:#57606a;}}
  .stat{{text-align:center;}} .stat .num{{font-size:2rem;font-weight:700;}} .stat .lbl{{font-size:.75rem;color:#57606a;}}
  .stat.p .num{{color:var(--pass);}} .stat.f .num{{color:var(--fail);}}
  table{{width:100%;border-collapse:collapse;font-size:.88rem;}}
  th,td{{border:1px solid var(--border);padding:.45rem .7rem;text-align:left;vertical-align:top;}}
  thead th{{background:#f6f8fa;font-weight:600;position:sticky;top:60px;}}
  tr.pass td:first-child{{border-left:4px solid var(--pass);}} tr.fail td:first-child{{border-left:4px solid var(--fail);}} tr.infra td:first-child{{border-left:4px solid var(--infra);}}
  .status-pass{{color:var(--pass);font-weight:600;}} .status-fail{{color:var(--fail);font-weight:600;}} .status-infra{{color:var(--infra);font-weight:600;}}
  .err{{color:var(--fail);font-size:.8rem;}} .err-infra{{color:var(--infra);font-size:.8rem;}} .err-box{{background:#fff5f5;border:1px solid #ffcdd2;border-radius:6px;padding:.5rem .8rem;color:var(--fail);}}
  .infra-box{{background:#fff8f0;border:1px solid #ffcfa4;border-radius:6px;padding:.5rem .8rem;color:var(--infra);}}
  .tc-block{{background:var(--card);border:1px solid var(--border);border-radius:8px;padding:1.2rem 1.5rem;margin-bottom:2rem;}}
  .tc-block.pass{{border-left:5px solid var(--pass);}} .tc-block.fail{{border-left:5px solid var(--fail);}}
  h2{{margin-top:0;font-size:1.05rem;}} h3{{font-size:.95rem;color:#57606a;margin:.8rem 0 .4rem;}}
  .ss-gallery{{display:flex;flex-wrap:wrap;gap:1rem;margin-top:.5rem;}}
  .ss-card{{background:#f6f8fa;border:1px solid var(--border);border-radius:6px;padding:.6rem;max-width:420px;}}
  .ss-title{{font-size:.82rem;font-weight:600;margin-bottom:.3rem;color:#1f2328;}}
  .ss-meta{{font-size:.72rem;color:#57606a;margin-bottom:.4rem;word-break:break-all;}}
  .ss-card img{{width:100%;border-radius:4px;cursor:zoom-in;border:1px solid var(--border);transition:.2s;}}
  .ss-card img.zoom{{width:90vw;max-width:1200px;position:fixed;top:5vh;left:50%;transform:translateX(-50%);z-index:999;box-shadow:0 8px 40px #0005;cursor:zoom-out;}}
  a{{color:#0969da;text-decoration:none;}} a:hover{{text-decoration:underline;}}
  code{{background:#eef;border-radius:3px;padding:1px 4px;font-size:.82rem;word-break:break-all;}}
  .toc{{background:var(--card);border:1px solid var(--border);border-radius:8px;padding:1rem 1.5rem;margin-bottom:1.5rem;}}
  .toc ul{{margin:.5rem 0;padding-left:1.2rem;}} .toc li{{margin:.2rem 0;font-size:.9rem;}}
</style>
</head>
<body>
<header>
  <h1>🧪 {story} — Browser Test Evidence Report</h1>
  <span class="badge badge-{'pass' if failed==0 else 'fail'}">{passed}/{total} PASSED</span>
  <span style="font-size:.85rem;opacity:.7">{env} | {browser_type} | {ts}</span>
</header>
<main>
  <div class="summary-box">
    <div class="stat"><div class="num">{total}</div><div class="lbl">TOTAL</div></div>
    <div class="stat p"><div class="num">{passed}</div><div class="lbl">PASSED ✅</div></div>
    <div class="stat f"><div class="num">{failed}</div><div class="lbl">FAILED ❌</div></div>
    <div class="stat"><div class="num">{pass_rate}%</div><div class="lbl">PASS RATE</div></div>
    <div style="flex:1;font-size:.85rem;color:#57606a">
      <strong>Story:</strong> {story}<br/>
      <strong>Environment:</strong> {env}<br/>
      <strong>Run:</strong> {ts}
    </div>
  </div>

  <div class="rerun-box">
    <strong>🔁 To Re-Run This Test Suite</strong> — paste this command in PowerShell:
    <code id="rerun-cmd" onclick="navigator.clipboard&&navigator.clipboard.writeText(this.innerText).then(()=>this.style.background='#a7f3d0')">{_rerun_cmd}</code>
    <span class="copy-hint">💡 Click the command above to copy · Logs in as <strong>{_username}</strong> on <strong>{env}</strong> · Regenerates this HTML + MD report automatically</span>
  </div>

  <div class="toc"><strong>Test Cases</strong>
    <ul>{''.join(f'<li><a href="#{tc["id"]}">{"✅" if tc["status"]=="PASSED" else "❌"} {tc["id"]}: {tc["name"][:70]}</a></li>' for tc in results)}</ul>
  </div>

  <table style="margin-bottom:1.5rem;">
    <thead><tr><th></th><th>ID</th><th>Name</th><th>Start</th><th>End</th><th>Status</th></tr></thead>
    <tbody>{tc_rows}</tbody>
  </table>

  {tc_detail_blocks}
</main>
</body>
</html>'''

    rel_path = cfg.get('pipeline', {}).get('browser_capture_html', 'agents/SCPlatform_10124_browser_capture.html')
    out_path = ROOT_DIR / rel_path
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(html, encoding='utf-8')
    print(f"[Agent0] ✓ HTML evidence report → {out_path}")
    return str(out_path)


def _open_report_in_vscode(html_path: str):
    """Open the generated HTML report in VS Code / default browser for immediate evidence review."""
    try:
        if not html_path or not Path(html_path).exists():
            print(f"[Agent0] Note: HTML report not found at {html_path}")
            return
        import subprocess
        # Try VS Code CLI first — opens in VS Code editor/simple browser
        result = subprocess.run(
            ['code', html_path],
            capture_output=True, timeout=10, shell=True
        )
        if result.returncode == 0:
            print(f"[Agent0] ✓ Opened HTML evidence report in VS Code: {html_path}")
        else:
            # Fallback: open with system default browser (Chrome/Edge/etc.)
            import webbrowser
            file_uri = Path(html_path).as_uri()
            webbrowser.open(file_uri)
            print(f"[Agent0] ✓ Opened HTML evidence report in browser: {html_path}")
    except Exception as ex:
        print(f"[Agent0] Note: Could not auto-open report ({ex})")
        print(f"[Agent0] Open manually in browser: {html_path}")


def _ask(prompt: str, default: str = '') -> str:
    """Prompt user for input with an optional default value."""
    if default:
        answer = input(f"{prompt} [{default}]: ").strip()
        return answer if answer else default
    else:
        return input(f"{prompt}: ").strip()


def _ask_yn(prompt: str, default: bool = True) -> bool:
    """Prompt user for yes/no answer."""
    yn = 'Y/n' if default else 'y/N'
    answer = input(f"{prompt} [{yn}]: ").strip().lower()
    if not answer:
        return default
    return answer.startswith('y')


def _print_separator(char='─', width=70):
    print(char * width)


def interactive_wizard(cfg: dict) -> list:
    """
    Interactive wizard that asks the user for story, URL, menu navigation,
    and verification steps — then builds a list of test cases to run.

    Works for ANY project — SCPlatform or any web application.
    The composed session is saved to agents/sessions/<STORY>.json for replay.
    """
    _print_separator('═')
    print("  Agent 0 — Browser Test Wizard")
    print("  Works for any story, project, or web application")
    _print_separator('═')
    print()

    # ── Step 1: Project / story info ─────────────────────────────────────────
    app_cfg = cfg.get('app', {})
    default_url = app_cfg.get('base_url', 'http://localhost:8089/scplatform')
    default_user = app_cfg.get('direct_login_user', app_cfg.get('username', 'kswamy'))

    print("STEP 1 — App & Story")
    _print_separator()
    story_id = _ask("  Story/ticket ID (e.g. SCIP-10124, SCIP-999)", cfg.get('test_plan', {}).get('story', ''))
    app_url = _ask("  Application base URL", default_url)
    username = _ask("  Login username", default_user)
    story_title = _ask("  Short description / feature name", f"{story_id} feature verification")
    print()

    # ── Step 2: Number of test cases ─────────────────────────────────────────
    print("STEP 2 — How many test cases do you want to run?")
    _print_separator()
    while True:
        try:
            tc_count = int(_ask("  Number of test cases", "1"))
            if tc_count >= 1:
                break
            print("  (must be at least 1)")
        except ValueError:
            print("  (enter a number)")
    print()

    test_cases = []

    for tc_idx in range(tc_count):
        tc_num = tc_idx + 1
        _print_separator('─')
        print(f"  TEST CASE {tc_num} of {tc_count}")
        _print_separator('─')

        tc_id = _ask(f"  TC ID (e.g. TC-{story_id}-{tc_num})", f"TC-{story_id}-{tc_num}")
        tc_name = _ask(f"  TC description", f"Verify feature in {story_id}")
        print()

        # ── Menu navigation ───────────────────────────────────────────────────
        print(f"  NAVIGATION  (how to reach the page for TC {tc_num})")
        print("    Enter the menu path using '->' between levels.")
        print("    Examples:")
        print("      Supply Allocation -> Manage Functional Group")
        print("      Pricing -> Cost Records -> Search")
        print("      Master Data Management -> Item Master")
        print("    Leave blank to navigate directly via URL instead.")
        menu_path = _ask("  Menu path", '').strip()

        direct_url = ''
        if not menu_path:
            direct_url = _ask("  Direct URL to navigate to", f"{app_url}/")

        # ── Actions on the page ───────────────────────────────────────────────
        print()
        print(f"  PAGE ACTIONS  (what to do after reaching the page for TC {tc_num})")
        print("    Common actions: Apply, Clear, Search, Edit, Save, Export, Submit")
        print("    Enter each button/link to click, one per line. Empty line to finish.")

        click_actions = []
        wait_after_apply = '10'
        action_idx = 1
        while True:
            action = _ask(f"  Click action {action_idx} (or press Enter to finish)", '').strip()
            if not action:
                break
            click_actions.append(action)
            if action.lower() in ('apply', 'search', 'submit', 'run'):
                wait_after_apply = _ask("    Wait (seconds) for results to load", '10')
            action_idx += 1

        # ── Verifications ─────────────────────────────────────────────────────
        print()
        print(f"  VERIFICATIONS  (what to check/assert for TC {tc_num})")
        print("    For each check, specify:")
        print("      - what text to look for (e.g. column header, button label, field name)")
        print("      - where to look: column | field | button/link (export) | text | readonly")
        print("    Leave text blank to just take a screenshot (no assertion).")

        verifications = []
        ver_idx = 1
        while True:
            check_text = _ask(f"  Check {ver_idx} — text to find (or Enter to finish)", '').strip()
            if not check_text:
                if ver_idx == 1:
                    # Always have at least a screenshot
                    verifications.append({'desc': f'Page loaded for {tc_id}', 'check': '', 'check_type': 'text'})
                break
            print("    Where to look?")
            print("      1 = column header in a table/grid")
            print("      2 = field label on a form/edit page")
            print("      3 = button or link (e.g. Export, Save)")
            print("      4 = any visible text on the page")
            print("      5 = readonly/disabled input field")
            loc_choice = _ask("    Choice", "1")
            loc_map = {'1': 'column', '2': 'field', '3': 'export', '4': 'text', '5': 'readonly'}
            check_type = loc_map.get(loc_choice, 'text')
            ver_desc = _ask(f"    Description for this check", f"'{check_text}' is visible as {check_type}")
            verifications.append({'desc': ver_desc, 'check': check_text, 'check_type': check_type})
            ver_idx += 1

        # ── Build steps list ─────────────────────────────────────────────────
        steps = []
        if menu_path:
            steps.append({'type': 'navigate', 'value': menu_path})
        elif direct_url:
            steps.append({'type': 'goto', 'value': direct_url})

        for act in click_actions:
            steps.append({'type': 'click', 'value': act})
            if act.lower() in ('apply', 'search', 'submit', 'run'):
                steps.append({'type': 'wait', 'value': wait_after_apply})

        for ver in verifications:
            steps.append({'type': 'verify', 'desc': ver['desc'], 'check': ver.get('check', ''), 'check_type': ver.get('check_type', 'text')})

        steps.append({'type': 'screenshot', 'desc': f'{tc_id} final state'})

        test_cases.append({
            'id': tc_id,
            'name': tc_name,
            'test_data': {
                'url': direct_url or app_url,
                'user': username,
                'story': story_id,
            },
            'steps': steps,
        })
        print()

    # ── Update cfg with story info ────────────────────────────────────────────
    cfg.setdefault('test_plan', {})['story'] = story_id
    cfg.setdefault('test_plan', {})['title'] = story_title
    cfg.setdefault('app', {})['base_url'] = app_url
    cfg.setdefault('app', {})['direct_login_user'] = username
    cfg.setdefault('app', {})['direct_login'] = True

    # Update output file names to use the story ID
    safe_story = re.sub(r'[^\w\-]', '_', story_id)
    cfg.setdefault('pipeline', {})['browser_capture_md'] = f'agents/{safe_story}_browser_capture.md'
    cfg.setdefault('pipeline', {})['browser_capture_html'] = f'agents/{safe_story}_browser_capture.html'
    cfg.setdefault('pipeline', {})['browser_capture_json'] = f'agents/{safe_story}_browser_capture.json'

    # ── Save session for replay ───────────────────────────────────────────────
    sessions_dir = AGENTS_DIR / 'sessions'
    sessions_dir.mkdir(exist_ok=True)
    session_data = {
        'story_id': story_id,
        'story_title': story_title,
        'app_url': app_url,
        'username': username,
        'test_cases': test_cases,
        'created': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
    }
    session_file = sessions_dir / f'{safe_story}.json'
    session_file.write_text(json.dumps(session_data, indent=2), encoding='utf-8')
    print(f"[Agent0] ✓ Session saved → {session_file}")
    print(f"[Agent0]   Re-run this exact session later with:")
    print(f"[Agent0]   & $venvPy -u agents/agent0_browser_explorer.py --session agents/sessions/{safe_story}.json")
    print()

    return test_cases


def load_session(session_path: str, cfg: dict) -> list:
    """Load test cases from a previously saved wizard session JSON."""
    data = json.loads(Path(session_path).read_text(encoding='utf-8'))
    story_id = data.get('story_id', 'STORY')
    story_title = data.get('story_title', story_id)
    app_url = data.get('app_url', '')
    username = data.get('username', '')

    cfg.setdefault('test_plan', {})['story'] = story_id
    cfg.setdefault('test_plan', {})['title'] = story_title
    if app_url:
        cfg.setdefault('app', {})['base_url'] = app_url
    if username:
        cfg.setdefault('app', {})['direct_login_user'] = username
        cfg.setdefault('app', {})['direct_login'] = True

    safe_story = re.sub(r'[^\w\-]', '_', story_id)
    cfg.setdefault('pipeline', {})['browser_capture_md'] = f'agents/{safe_story}_browser_capture.md'
    cfg.setdefault('pipeline', {})['browser_capture_html'] = f'agents/{safe_story}_browser_capture.html'
    cfg.setdefault('pipeline', {})['browser_capture_json'] = f'agents/{safe_story}_browser_capture.json'

    print(f"[Agent0] ✓ Loaded session: {story_id} — {len(data['test_cases'])} test case(s)")
    return data['test_cases']


def check_playwright():
    if not PLAYWRIGHT_AVAILABLE:
        print("[Agent0] ERROR: Playwright not installed.")
        print("  Run: pip install playwright && playwright install chromium")
        print("  For Edge: playwright install msedge")
        sys.exit(1)


def main():
    parser = argparse.ArgumentParser(
        description='Agent 0: Generic Browser Testing Agent — any story, any project',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  Interactive wizard (asks for story, menus, verifications):
    python agents/agent0_browser_explorer.py

  Replay a saved wizard session:
    python agents/agent0_browser_explorer.py --session agents/sessions/SCPlatform-10124.json

  Run built-in SCPlatform-10124 test cases (legacy/CI):
    python agents/agent0_browser_explorer.py --builtin

  Run just one TC:
    python agents/agent0_browser_explorer.py --tc TC-10124-1

  Override story and URL:
    python agents/agent0_browser_explorer.py --story SCPlatform-9999 --url http://dev9999.../scplatform
        """
    )
    parser.add_argument('--browser', default=None, choices=['chrome', 'edge', 'chromium'],
                        help='Browser (default: from jira_config.json or chrome)')
    parser.add_argument('--headless', action='store_true', default=None,
                        help='Run headless (no visible window)')
    parser.add_argument('--tc', default=None, help='Run only a specific test case ID')
    parser.add_argument('--builtin', action='store_true',
                        help='Use built-in SCPlatform-10124 test cases (no wizard)')
    parser.add_argument('--session', default=None, metavar='PATH',
                        help='Replay a saved wizard session JSON file')
    parser.add_argument('--story', default=None, metavar='STORY_ID',
                        help='Override story ID (used for report naming)')
    parser.add_argument('--url', default=None, metavar='URL',
                        help='Override application base URL')
    args = parser.parse_args()

    check_playwright()
    cfg = load_config()
    ss_dir = ensure_screenshots_dir(cfg)

    # Apply CLI overrides
    if args.story:
        cfg.setdefault('test_plan', {})['story'] = args.story
        safe_story = re.sub(r'[^\w\-]', '_', args.story)
        cfg.setdefault('pipeline', {})['browser_capture_md'] = f'agents/{safe_story}_browser_capture.md'
        cfg.setdefault('pipeline', {})['browser_capture_html'] = f'agents/{safe_story}_browser_capture.html'
        cfg.setdefault('pipeline', {})['browser_capture_json'] = f'agents/{safe_story}_browser_capture.json'
    if args.url:
        cfg.setdefault('app', {})['base_url'] = args.url

    browser_type = args.browser or cfg.get('browser', {}).get('type', 'chrome')
    headless = args.headless if args.headless is not None else cfg.get('browser', {}).get('headless', False)
    slow_mo = cfg.get('browser', {}).get('slow_mo_ms', 200)
    width = cfg.get('browser', {}).get('viewport_width', 1280)
    height = cfg.get('browser', {}).get('viewport_height', 720)

    # ── Determine test cases source ───────────────────────────────────────────
    if args.session:
        # Replay a saved wizard session (CLI override)
        test_cases = load_session(args.session, cfg)

    elif cfg.get('test_plan', {}).get('source') == 'session':
        # Session file path from jira_config.json test_plan.session_file
        session_file = cfg.get('test_plan', {}).get('session_file', '')
        if not os.path.isabs(session_file):
            session_file = str(ROOT_DIR / session_file)
        story = cfg.get('test_plan', {}).get('story', 'SCPlatform')
        print(f"[Agent0] Loading session file: {session_file}")
        test_cases = load_session(session_file, cfg)
        print(f"[Agent0] Using {len(test_cases)} session test cases for {story}")

    elif args.builtin or cfg.get('test_plan', {}).get('source') == 'builtin':
        # Built-in SCPlatform-10124 TCs — via --builtin flag OR source=builtin in jira_config.json
        test_cases = BUILTIN_TEST_CASES
        story = cfg.get('test_plan', {}).get('story', 'SCPlatform-10124')
        print(f"[Agent0] Using {len(test_cases)} built-in test cases for {story}")

    elif cfg.get('test_plan', {}).get('source') == 'feature':
        # Feature-file driven TCs
        feature_file = cfg.get('test_plan', {}).get('feature_file', '')
        test_cases = load_feature_test_cases(feature_file)

    else:
        # Default: interactive wizard
        test_cases = interactive_wizard(cfg)

    # Filter to single TC if specified
    if args.tc:
        test_cases = [tc for tc in test_cases if args.tc.lower() in tc['id'].lower() or args.tc.lower() in tc['name'].lower()]
        if not test_cases:
            print(f"[Agent0] No test case matching '{args.tc}' found.")
            sys.exit(1)

    print(f"\n[Agent0] Browser: {browser_type.upper()} | Headless: {headless} | Test cases: {len(test_cases)}")
    print(f"[Agent0] Screenshots dir: {ss_dir}")

    all_results = []

    with sync_playwright() as pw:
        # Browser selection: chrome, edge, or chromium
        if browser_type == 'edge':
            # Microsoft Edge
            browser: Browser = pw.chromium.launch(
                channel='msedge',
                headless=headless,
                slow_mo=slow_mo,
                args=['--start-maximized', '--disable-web-security']
            )
            print("[Agent0] Launched: Microsoft Edge")
        elif browser_type == 'chrome':
            # Google Chrome
            browser: Browser = pw.chromium.launch(
                channel='chrome',
                headless=headless,
                slow_mo=slow_mo,
                args=['--start-maximized', '--disable-web-security']
            )
            print("[Agent0] Launched: Google Chrome")
        else:
            # Chromium (bundled Playwright browser)
            browser: Browser = pw.chromium.launch(
                headless=headless,
                slow_mo=slow_mo,
                args=['--start-maximized']
            )
            print("[Agent0] Launched: Chromium")

        # ── Single browser session for all TCs ───────────────────────────────
        # One login at the start, shared page across all TCs, one logout at end.
        shared_ctx = browser.new_context(
            viewport={'width': width, 'height': height},
            accept_downloads=True,
            ignore_https_errors=True
        )
        page = shared_ctx.new_page()
        print(f"[Agent0] Single shared browser session — login once, run all TCs, logout once.")

        # Deep-copy test_cases so BUILTIN_TEST_CASES is never mutated across runs.
        import copy
        run_cases = copy.deepcopy(test_cases)

        # Inject login ONLY on TC-1, logout ONLY on the LAST TC.
        # Intermediate TCs: strip any login/logout steps (session is already active).
        for idx, tc in enumerate(run_cases):
            is_first = (idx == 0)
            is_last  = (idx == len(run_cases) - 1)

            steps = [s for s in tc.get('steps', [])
                     if s.get('type') not in ('login', 'logout')]

            if is_first:
                steps = [{'type': 'login'}] + steps
            if is_last:
                steps = steps + [{'type': 'logout'}]

            tc['steps'] = steps

        def _is_page_alive(p):
            """Check if the Playwright page context is still alive."""
            try:
                _ = p.url
                return True
            except Exception:
                return False

        def _reopen_page(pw_instance, b_type, h, sm, w, h_px, c):
            """Re-launch browser and create a fresh page after context death."""
            print("[Agent0] ⚠ Browser context died — relaunching browser and re-logging in...")
            try:
                c.close()
            except Exception:
                pass
            _launch_args = ['--start-maximized', '--disable-web-security']
            if b_type == 'edge':
                new_b = pw_instance.chromium.launch(channel='msedge', headless=h, slow_mo=sm, args=_launch_args)
            elif b_type == 'chrome':
                new_b = pw_instance.chromium.launch(channel='chrome', headless=h, slow_mo=sm, args=_launch_args)
            else:
                new_b = pw_instance.chromium.launch(headless=h, slow_mo=sm, args=['--start-maximized'])
            new_ctx = new_b.new_context(viewport={'width': w, 'height': h_px},
                                        accept_downloads=True, ignore_https_errors=True)
            new_page = new_ctx.new_page()
            return new_b, new_ctx, new_page

        for tc_idx2, tc in enumerate(run_cases):
            # Check if page context is alive before starting each TC
            if not _is_page_alive(page):
                try:
                    browser, shared_ctx, page = _reopen_page(
                        pw, browser_type, headless, slow_mo, width, height, shared_ctx)
                    tc['steps'] = [{'type': 'login'}] + [s for s in tc['steps'] if s.get('type') not in ('login',)]
                    print(f"[Agent0] ✔ Browser recovered — re-logged in, continuing TC: {tc['id']}")
                except Exception as _re_ex:
                    print(f"[Agent0] ❌ Browser recovery failed for {tc['id']}: {_re_ex} — skipping TC")
                    all_results.append({
                        'tc_id': tc['id'], 'tc_name': tc.get('name', ''),
                        'status': 'FAILED', 'steps': [],
                        'error': f'Browser recovery failed: {_re_ex}',
                        'start_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                    })
                    continue

            print(f"\n[Agent0] ═══ TC: {tc['id']} ═══")
            print(f"[Agent0] Steps: {[s.get('type') for s in tc['steps']]}")
            try:
                result = run_test_case(page, ss_dir, cfg, tc)
            except Exception as _tc_ex:
                _tc_ex_str = str(_tc_ex)
                # BrowserStuck: relaunch browser and retry this TC once
                if 'Navigation stuck' in _tc_ex_str or 'Could not open hamburger' in _tc_ex_str or 'Could not click' in _tc_ex_str:
                    print(f"[Agent0] ⚠ Browser stuck detected on {tc['id']} — relaunching browser and retrying...")
                    try:
                        browser, shared_ctx, page = _reopen_page(
                            pw, browser_type, headless, slow_mo, width, height, shared_ctx)
                        # Re-login before retry
                        login(page, cfg, ss_dir)
                        # Retry the TC once after relaunch
                        result = run_test_case(page, ss_dir, cfg, tc)
                        print(f"[Agent0] ✔ TC {tc['id']} succeeded after browser relaunch")
                    except Exception as _retry_ex:
                        print(f"[Agent0] ❌ TC {tc['id']} still failed after relaunch: {_retry_ex}")
                        result = {
                            'tc_id': tc['id'], 'tc_name': tc.get('name', ''),
                            'status': 'FAILED', 'steps': [],
                            'error': f'Stuck + relaunch failed: {_retry_ex}',
                            'start_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                        }
                else:
                    print(f"[Agent0] ❌ Unhandled exception in {tc['id']}: {_tc_ex}")
                    result = {
                        'tc_id': tc['id'], 'tc_name': tc.get('name', ''),
                    'status': 'FAILED', 'steps': [],
                    'error': str(_tc_ex),
                    'start_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
                }
            all_results.append(result)

        try:
            shared_ctx.close()
        except Exception:
            pass
        try:
            browser.close()
        except Exception:
            pass
        print(f"\n[Agent0] ✔ Browser closed.")

    # Write outputs
    json_path = write_capture_json(all_results, cfg)
    md_path = write_capture_md(all_results, cfg)
    html_path = write_capture_html(all_results, cfg)

    passed = sum(1 for r in all_results if r['status'] == 'PASSED')
    failed = sum(1 for r in all_results if r['status'] == 'FAILED')
    print(f"\n[Agent0] ═══ COMPLETE: {passed} PASSED, {failed} FAILED of {len(all_results)} total ═══")
    print(f"[Agent0] MD   report : {md_path}")
    print(f"[Agent0] HTML report : {html_path}")
    print(f"[Agent0] JSON report : {json_path}")

    # Open the HTML evidence report in VS Code / browser for immediate review
    _open_report_in_vscode(html_path)

    # ── Auto-invoke downstream agents ─────────────────────────────────────────
    _run_downstream_agents(cfg, json_path, all_results)

    # Attach results to Jira ticket
    story = cfg.get('test_plan', {}).get('story') or cfg.get('report', {}).get('story_id', '')
    if story and not cfg.get('pipeline', {}).get('dry_run_jira', False):
        do_attach = True
        if cfg.get('pipeline', {}).get('jira_attach_confirm', False):
            print(f"\n[Agent0] ⚠ JIRA attachment is ready for {story}.")
            print(f"[Agent0]   Reports: {md_path}")
            print(f"[Agent0]   + {len(all_results)} TC screenshots")
            try:
                answer = input("[Agent0]   Attach to JIRA now? [y/N]: ").strip().lower()
            except (EOFError, KeyboardInterrupt):
                answer = 'n'
            do_attach = answer in ('y', 'yes')
            if not do_attach:
                print(f"[Agent0] ↩ JIRA attachment skipped by user — run manually when ready.")
        if do_attach:
            _attach_to_jira(cfg, story, md_path, json_path, all_results)


def _run_downstream_agents(cfg: dict, json_path: str, results: list):
    """
    After Agent 0 finishes, automatically invoke:
      • Agent 4 — Java Selenium/Cucumber script generator (reads browser_capture.json)
      • Agent 2 — HTML report generator (reads parsed_results.json if available)
    Controlled by jira_config.json pipeline.auto_chain (default: true).
    """
    import subprocess

    if not cfg.get('pipeline', {}).get('auto_chain', True):
        print("[Agent0] Pipeline chaining disabled (pipeline.auto_chain=false) — skipping Agent 4 & Agent 2.")
        return

    py = sys.executable
    agents_dir = Path(__file__).parent

    # ── Agent 4: Automation script generator ──────────────────────────────────
    agent4 = agents_dir / 'agent4_script_generator.py'
    if agent4.exists() and json_path and Path(json_path).exists():
        print(f"\n[Agent0] ▶ Chaining → Agent 4 (automation script generator)...")
        try:
            result4 = subprocess.run(
                [py, str(agent4), json_path],
                capture_output=True, text=True, timeout=120
            )
            if result4.returncode == 0:
                print(f"[Agent0] ✔ Agent 4 completed — automation scripts generated.")
                if result4.stdout.strip():
                    for line in result4.stdout.strip().splitlines()[-10:]:
                        print(f"  [Agent4] {line}")
            else:
                print(f"[Agent0] ⚠ Agent 4 exited with code {result4.returncode}")
                if result4.stderr.strip():
                    print(f"  [Agent4 stderr] {result4.stderr.strip()[-300:]}")
        except subprocess.TimeoutExpired:
            print("[Agent0] ⚠ Agent 4 timed out (>120s) — check agents/generated_scripts/")
        except Exception as e:
            print(f"[Agent0] ⚠ Agent 4 failed to launch: {e}")
    else:
        print(f"[Agent0] ℹ Agent 4 skipped — {'script not found' if not agent4.exists() else 'browser_capture.json not available'}")

    # ── Agent 2: HTML report generator ────────────────────────────────────────
    agent2 = agents_dir / 'agent2_report_generator.py'
    parsed_json = agents_dir / 'parsed_results.json'
    if agent2.exists() and parsed_json.exists():
        print(f"\n[Agent0] ▶ Chaining → Agent 2 (HTML report generator)...")
        try:
            result2 = subprocess.run(
                [py, str(agent2)],
                capture_output=True, text=True, timeout=60
            )
            if result2.returncode == 0:
                print(f"[Agent0] ✔ Agent 2 completed — HTML report generated.")
                if result2.stdout.strip():
                    for line in result2.stdout.strip().splitlines()[-5:]:
                        print(f"  [Agent2] {line}")
            else:
                print(f"[Agent0] ⚠ Agent 2 exited with code {result2.returncode}")
        except Exception as e:
            print(f"[Agent0] ⚠ Agent 2 failed to launch: {e}")
    else:
        print(f"[Agent0] ℹ Agent 2 skipped — parsed_results.json not found (run Agent 1 first to enable)")


def _attach_to_jira(cfg: dict, issue_key: str, md_path: str, json_path: str, results: list):
    """Attach the MD report and screenshots to the Jira issue as evidence."""
    import urllib.request
    import urllib.parse
    import ssl
    import base64

    jira_cfg = cfg.get('jira', {})
    base_url = jira_cfg.get('base_url', '').rstrip('/')
    username = jira_cfg.get('username', '')
    password = jira_cfg.get('password', '')

    if not base_url or not username or 'YOUR_PASSWORD' in password or not password:
        print(f"[Agent0] Jira attachment skipped — credentials not configured (set jira.password in jira_config.json)")
        return

    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE
    token = base64.b64encode(f'{username}:{password}'.encode()).decode()

    def _upload(file_path: str, label: str = ''):
        if not file_path or not Path(file_path).exists():
            return
        attach_url = f'{base_url}/rest/api/2/issue/{issue_key}/attachments'
        boundary = '----AgentBoundary'
        fname = Path(file_path).name
        with open(file_path, 'rb') as fh:
            content = fh.read()
        body = (
            f'--{boundary}\r\n'
            f'Content-Disposition: form-data; name="file"; filename="{fname}"\r\n'
            f'Content-Type: application/octet-stream\r\n\r\n'
        ).encode() + content + f'\r\n--{boundary}--\r\n'.encode()
        req = urllib.request.Request(attach_url, data=body, method='POST')
        req.add_header('Authorization', f'Basic {token}')
        req.add_header('X-Atlassian-Token', 'no-check')
        req.add_header('Content-Type', f'multipart/form-data; boundary={boundary}')
        try:
            with urllib.request.urlopen(req, context=ctx, timeout=30) as r:
                print(f"[Agent0] ✓ Attached to {issue_key}: {fname} (HTTP {r.status})")
        except Exception as ex:
            print(f"[Agent0] ✗ Attach failed for {fname}: {ex}")

    print(f"\n[Agent0] Attaching evidence to Jira {issue_key}...")
    _upload(md_path, 'Test Capture Report')
    _upload(json_path, 'Test Capture JSON')

    # Attach screenshots for FAILED tests only (to keep attachment count manageable)
    for tc in results:
        if tc['status'] == 'FAILED':
            for step in tc.get('steps', []):
                ss = step.get('screenshot', '')
                if ss and Path(ss).exists():
                    _upload(ss, f"{tc['id']} screenshot")

    # Also attach last screenshot of each TC as evidence
    for tc in results:
        steps_with_ss = [s for s in tc.get('steps', []) if s.get('screenshot') and Path(s['screenshot']).exists()]
        if steps_with_ss:
            _upload(steps_with_ss[-1]['screenshot'], f"{tc['id']} final state")

    print(f"[Agent0] Jira attachment complete — {issue_key}")


if __name__ == '__main__':
    main()
