"""Renders report.json (from pytest-json-report) into a styled, self-contained
HTML dashboard. Kept dependency-free (no Jinja2) so it runs the same locally
and in CI with nothing beyond the stdlib.
"""

import html
import json
import sys
from datetime import datetime, timezone
from pathlib import Path

STATUS_META = {
    "passed": {"label": "Passed", "color": "good", "icon": "✓"},
    "failed": {"label": "Failed", "color": "critical", "icon": "✕"},
    "error": {"label": "Error", "color": "critical", "icon": "✕"},
    "skipped": {"label": "Skipped", "color": "warning", "icon": "●"},
}


def format_duration(seconds):
    if seconds < 1:
        return f"{seconds * 1000:.0f} ms"
    if seconds < 60:
        return f"{seconds:.2f} s"
    minutes, secs = divmod(seconds, 60)
    return f"{int(minutes)}m {secs:.1f}s"


def test_duration(test):
    return sum(test.get(phase, {}).get("duration", 0) for phase in ("setup", "call", "teardown"))


def split_nodeid(nodeid):
    file_part, _, test_part = nodeid.partition("::")
    module = Path(file_part).stem.replace("test_", "").replace("_", " ")
    return module, test_part


def stat_tile(label, value, status=None):
    color_css = f"var(--status-{status})" if status else "var(--text-primary)"
    return f"""
    <div class="tile">
      <div class="tile-value" style="color:{color_css}">{value}</div>
      <div class="tile-label">{html.escape(label)}</div>
    </div>"""


def status_pill(outcome):
    meta = STATUS_META.get(outcome, {"label": outcome.title(), "color": "warning", "icon": "?"})
    return (
        f'<span class="pill pill-{meta["color"]}">'
        f'<span class="pill-icon">{meta["icon"]}</span>{meta["label"]}</span>'
    )


def build_table_rows(tests):
    rows = []
    for test in tests:
        module, name = split_nodeid(test["nodeid"])
        duration = format_duration(test_duration(test))
        rows.append(f"""
        <tr>
          <td class="col-module">{html.escape(module)}</td>
          <td class="col-test">{html.escape(name)}</td>
          <td class="col-status">{status_pill(test["outcome"])}</td>
          <td class="col-duration">{duration}</td>
        </tr>""")
    return "\n".join(rows)


def build_meter(passed, failed, skipped, total):
    if total == 0:
        return '<div class="meter"><div class="meter-empty"></div></div>'

    segments = []
    for count, color in ((passed, "good"), (failed, "critical"), (skipped, "warning")):
        if count:
            pct = (count / total) * 100
            segments.append(f'<div class="meter-seg" style="width:{pct:.3f}%;background:var(--status-{color})"></div>')
    return f'<div class="meter">{"".join(segments)}</div>'


def render(report, repo_url, run_url):
    summary = report["summary"]
    total = summary.get("total", 0)
    passed = summary.get("passed", 0)
    failed = summary.get("failed", 0) + summary.get("error", 0)
    skipped = summary.get("skipped", 0)
    pass_rate = (passed / total * 100) if total else 0
    duration = format_duration(report.get("duration", 0))
    generated = datetime.now(timezone.utc).strftime("%b %d, %Y %H:%M UTC")

    overall_status = "good" if failed == 0 and total > 0 else "critical" if failed else "warning"
    headline = f"{pass_rate:.0f}%"

    tiles = "".join([
        stat_tile("Total tests", total),
        stat_tile("Passed", passed, "good" if passed else None),
        stat_tile("Failed", failed, "critical" if failed else None),
        stat_tile("Skipped", skipped, "warning" if skipped else None),
        stat_tile("Duration", duration),
    ])

    rows = build_table_rows(report["tests"])
    meter = build_meter(passed, failed, skipped, total)

    repo_link = f'<a href="{repo_url}" class="footer-link">Repository</a>' if repo_url else ""
    run_link = f'<a href="{run_url}" class="footer-link">Workflow run</a>' if run_url else ""

    return f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Locator Framework — Test Dashboard</title>
<style>
  :root {{
    color-scheme: light;
    --surface-1:      #fcfcfb;
    --page-plane:     #f9f9f7;
    --text-primary:   #0b0b0b;
    --text-secondary: #52514e;
    --text-muted:     #898781;
    --gridline:       #e1e0d9;
    --border:         rgba(11,11,11,0.10);
    --status-good:      #0ca30c;
    --status-warning:   #b8790f;
    --status-critical:  #d03b3b;
    --status-good-bg:     rgba(12,163,12,0.10);
    --status-warning-bg:  rgba(184,121,15,0.12);
    --status-critical-bg: rgba(208,59,59,0.10);
  }}
  @media (prefers-color-scheme: dark) {{
    :root:where(:not([data-theme="light"])) {{
      color-scheme: dark;
      --surface-1:      #1a1a19;
      --page-plane:     #0d0d0d;
      --text-primary:   #ffffff;
      --text-secondary: #c3c2b7;
      --text-muted:     #898781;
      --gridline:       #2c2c2a;
      --border:         rgba(255,255,255,0.10);
      --status-good:      #0ca30c;
      --status-warning:   #fab219;
      --status-critical:  #e66767;
      --status-good-bg:     rgba(12,163,12,0.16);
      --status-warning-bg:  rgba(250,178,25,0.14);
      --status-critical-bg: rgba(230,103,103,0.14);
    }}
  }}
  :root[data-theme="dark"] {{
    color-scheme: dark;
    --surface-1:      #1a1a19;
    --page-plane:     #0d0d0d;
    --text-primary:   #ffffff;
    --text-secondary: #c3c2b7;
    --text-muted:     #898781;
    --gridline:       #2c2c2a;
    --border:         rgba(255,255,255,0.10);
    --status-good:      #0ca30c;
    --status-warning:   #fab219;
    --status-critical:  #e66767;
    --status-good-bg:     rgba(12,163,12,0.16);
    --status-warning-bg:  rgba(250,178,25,0.14);
    --status-critical-bg: rgba(230,103,103,0.14);
  }}

  * {{ box-sizing: border-box; }}
  body {{
    margin: 0;
    font-family: system-ui, -apple-system, "Segoe UI", sans-serif;
    background: var(--page-plane);
    color: var(--text-primary);
  }}
  .wrap {{ max-width: 960px; margin: 0 auto; padding: 32px 20px 64px; }}

  header {{ display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 28px; }}
  h1 {{ font-size: 20px; font-weight: 600; margin: 0 0 4px; }}
  .subtitle {{ color: var(--text-secondary); font-size: 13px; margin: 0; }}

  .theme-toggle {{
    border: 1px solid var(--border);
    background: var(--surface-1);
    color: var(--text-secondary);
    border-radius: 8px;
    padding: 6px 12px;
    font-size: 13px;
    cursor: pointer;
  }}
  .theme-toggle:hover {{ color: var(--text-primary); }}

  .hero-card {{
    background: var(--surface-1);
    border: 1px solid var(--border);
    border-radius: 16px;
    padding: 28px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 28px;
    flex-wrap: wrap;
  }}
  .hero-figure {{
    font-size: 56px;
    font-weight: 600;
    line-height: 1;
  }}
  .hero-meta {{ flex: 1; min-width: 200px; }}
  .hero-label {{ color: var(--text-secondary); font-size: 14px; margin-bottom: 10px; }}

  .meter {{ display: flex; gap: 2px; height: 10px; border-radius: 6px; overflow: hidden; background: var(--gridline); }}
  .meter-seg {{ height: 100%; }}
  .meter-empty {{ width: 100%; height: 100%; }}

  .tiles {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 12px; margin-bottom: 28px; }}
  .tile {{
    background: var(--surface-1);
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 16px;
  }}
  .tile-value {{ font-size: 28px; font-weight: 600; line-height: 1.1; margin-bottom: 4px; }}
  .tile-label {{ color: var(--text-secondary); font-size: 12px; }}

  table {{ width: 100%; border-collapse: collapse; background: var(--surface-1); border: 1px solid var(--border); border-radius: 12px; overflow: hidden; }}
  thead th {{
    text-align: left;
    font-size: 11px;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    color: var(--text-muted);
    padding: 12px 16px;
    border-bottom: 1px solid var(--gridline);
  }}
  tbody td {{ padding: 12px 16px; border-bottom: 1px solid var(--gridline); font-size: 14px; }}
  tbody tr:last-child td {{ border-bottom: none; }}
  .col-module {{ color: var(--text-secondary); width: 22%; }}
  .col-test {{ font-weight: 500; }}
  .col-status {{ width: 120px; }}
  .col-duration {{ width: 100px; text-align: right; font-variant-numeric: tabular-nums; color: var(--text-secondary); }}

  .pill {{
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 600;
  }}
  .pill-good {{ background: var(--status-good-bg); color: var(--status-good); }}
  .pill-warning {{ background: var(--status-warning-bg); color: var(--status-warning); }}
  .pill-critical {{ background: var(--status-critical-bg); color: var(--status-critical); }}
  .pill-icon {{ font-size: 11px; }}

  footer {{ margin-top: 28px; display: flex; gap: 16px; align-items: center; color: var(--text-muted); font-size: 12px; flex-wrap: wrap; }}
  .footer-link {{ color: var(--text-secondary); text-decoration: none; border-bottom: 1px solid var(--border); }}
  .footer-link:hover {{ color: var(--text-primary); }}

  @media (max-width: 480px) {{
    .hero-figure {{ font-size: 40px; }}
    .col-module {{ display: none; }}
  }}
</style>
</head>
<body>
  <div class="wrap">
    <header>
      <div>
        <h1>Playwright Modular Locator Framework</h1>
        <p class="subtitle">Test dashboard &middot; generated {generated}</p>
      </div>
      <button class="theme-toggle" onclick="toggleTheme()">Toggle theme</button>
    </header>

    <div class="hero-card">
      <div class="hero-figure" style="color:var(--status-{overall_status})">{headline}</div>
      <div class="hero-meta">
        <div class="hero-label">Pass rate &middot; {passed}/{total} tests passing</div>
        {meter}
      </div>
    </div>

    <div class="tiles">
      {tiles}
    </div>

    <table>
      <thead>
        <tr>
          <th class="col-module">Module</th>
          <th class="col-test">Test</th>
          <th class="col-status">Status</th>
          <th class="col-duration">Duration</th>
        </tr>
      </thead>
      <tbody>
        {rows}
      </tbody>
    </table>

    <footer>
      <span>pytest &middot; {total} tests &middot; {duration}</span>
      {repo_link}
      {run_link}
      <a href="report.html" class="footer-link">Detailed report</a>
    </footer>
  </div>

  <script>
    function toggleTheme() {{
      const root = document.documentElement;
      const current = root.getAttribute('data-theme');
      const next = current === 'dark' ? 'light' : 'dark';
      root.setAttribute('data-theme', next);
      localStorage.setItem('theme', next);
    }}
    const saved = localStorage.getItem('theme');
    if (saved) document.documentElement.setAttribute('data-theme', saved);
  </script>
</body>
</html>
"""


def main():
    report_path = sys.argv[1] if len(sys.argv) > 1 else "report.json"
    output_path = sys.argv[2] if len(sys.argv) > 2 else "dashboard.html"
    repo_url = sys.argv[3] if len(sys.argv) > 3 else ""
    run_url = sys.argv[4] if len(sys.argv) > 4 else ""

    report = json.loads(Path(report_path).read_text())
    html_out = render(report, repo_url, run_url)
    Path(output_path).write_text(html_out, encoding="utf-8")
    print(f"Wrote {output_path}")


if __name__ == "__main__":
    main()
