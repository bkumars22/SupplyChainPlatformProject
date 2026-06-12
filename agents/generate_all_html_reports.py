# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform
# Licensed under MIT License — see LICENSE file for details

import json, html, webbrowser
from pathlib import Path
from datetime import datetime

ROOT = Path(__file__).parent
OUT = ROOT
GUIDE_PATH = Path(__file__).parent.parent / "docs" / "LearningProject_Guide.html"
GUIDE_PATH.parent.mkdir(parents=True, exist_ok=True)

def render_step(step, root: Path):
    parts = []
    name = html.escape(str(step.get("name") or step.get("action") or step.get("step") or "step"))
    parts.append(f"<h4>{name}</h4>")
    meta = []
    for k in ("selector","action","value","url","title","xpath","time"):
        if k in step and step[k] not in (None, ""):
            meta.append(f"<strong>{html.escape(k)}:</strong> {html.escape(str(step[k]))}")
    if meta:
        parts.append("<p>" + "<br>".join(meta) + "</p>")
    ss = step.get("screenshot") or step.get("screenshot_path") or step.get("image")
    if ss:
        ss_path = Path(ss) if Path(ss).is_absolute() else (root / ss)
        if ss_path.exists():
            parts.append(f'<img src="{ss_path.resolve().as_uri()}" style="max-width:100%;border:1px solid #ddd;margin:8px 0">')
    return "\n".join(parts)

def render_html(data, json_path: Path):
    title = json_path.stem
    parts = [
        "<!doctype html><html><head><meta charset='utf-8'>",
        f"<title>{html.escape(title)}</title>",
        "<meta name='viewport' content='width=device-width,initial-scale=1'>",
        "<style>body{font-family:Segoe UI,Arial;line-height:1.45;padding:18px;color:#222}h1{color:#0b63a7}img{display:block;max-width:100%}section{border:1px solid #ddd;padding:12px;border-radius:6px;margin:12px 0}</style>",
        "</head><body>",
        f"<h1>Report — {html.escape(title)}</h1>",
        f"<p><em>Generated: {datetime.now().isoformat(sep=' ', timespec='seconds')}</em></p>"
    ]
    if isinstance(data, dict) and ("test_cases" in data or "results" in data or "tests" in data):
        tests = data.get("test_cases") or data.get("results") or data.get("tests")
        for t in tests:
            parts.append("<section>")
            parts.append(f"<h2>{html.escape(str(t.get('title') or t.get('name') or t.get('id') or 'Test'))}</h2>")
            desc = t.get("description") or t.get("desc")
            if desc:
                parts.append(f"<p>{html.escape(str(desc))}</p>")
            steps = t.get("steps") or t.get("actions") or t.get("events") or []
            if steps:
                for s in steps:
                    parts.append(render_step(s, json_path.parent))
            else:
                parts.append("<pre>" + html.escape(json.dumps(t, indent=2)) + "</pre>")
            parts.append("</section>")
    else:
        parts.append("<h2>Raw JSON</h2>")
        parts.append("<pre>" + html.escape(json.dumps(data, indent=2)) + "</pre>")
    parts.append("</body></html>")
    return "\n".join(parts)

# find JSON capture files
candidates = list(ROOT.glob("*_browser_capture.json")) + list(ROOT.glob("browser_capture.json"))
generated = []
for p in candidates:
    try:
        data = json.loads(p.read_text(encoding="utf-8"))
    except Exception:
        continue
    out_path = p.with_suffix(".html")
    out_path.write_text(render_html(data, p), encoding="utf-8")
    generated.append(out_path)

# if none found, create the learning guide HTML
if not generated:
    GUIDE_HTML = """<!doctype html><html lang='en'><head><meta charset='utf-8'><title>LearningProject — Step-by-Step Guide</title><meta name='viewport' content='width=device-width,initial-scale=1'><style>body{font-family:Segoe UI,Arial,Helvetica,sans-serif;line-height:1.45;margin:24px;color:#222}h1,h2,h3{color:#0b63a7}pre{background:#f4f4f4;padding:12px;border-radius:6px;overflow:auto}code{background:#eef;padding:2px 6px;border-radius:4px}.card{border:1px solid #ddd;padding:14px;border-radius:8px;margin-bottom:12px}.muted{color:#666;font-size:0.95em}ul.hl{margin-left:1.1em}</style></head><body><h1>LearningProject — Full Step-by-Step Guide</h1><p class='muted'>Purpose: teach setup, run, and report generation for the LearningProject (agents + Playwright) on Windows.</p><h2>1. Quick overview</h2><div class='card'><strong>Key components</strong><ul class='hl'><li><code>agents/</code> — browser automation scripts (e.g., agent0_browser_explorer.py)</li><li><code>docs/</code> — guides and learning material</li><li><code>.venv-agent</code> (recommended) — virtual environment</li><li>Artifacts: <code>agents/*.html</code>, <code>agents/*.md</code>, <code>agents/*.json</code>, <code>agents/screenshots/</code></li></ul></div><h2>2. Prerequisites (Windows)</h2><ul><li>Python 3.8+ installed and on PATH</li><li>Git (optional)</li><li>PowerShell (pwsh recommended)</li><li>Visual Studio Code (recommended)</li></ul><h2>3. Create workspace & virtual environment</h2><div class='card'><pre><code>cd "."\npython -m venv .venv-agent\nSet-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force\n.\\.venv-agent\\Scripts\\Activate.ps1\npip install --upgrade pip</code></pre></div><h2>4. Install project dependencies</h2><div class='card'><pre><code>pip install playwright openpyxl paramiko\n.\\.venv-agent\\Scripts\\playwright.cmd install chromium</code></pre></div><h2>5. Place agent code</h2><div class='card'><pre><code>LearningProject\\\n└─ agents\\\n   └─ agent0_browser_explorer.py</code></pre></div><h2>6. Run the agent to generate an HTML learning report</h2><div class='card'><pre><code>cd "."\n.\\.venv-agent\\Scripts\\Activate.ps1\npython .\\agents\\agent0_browser_explorer.py --builtin --headless --story LEARNING-ARCH</code></pre></div><h2>7. Generate HTML from existing JSON (no browser run)</h2><div class='card'><pre><code>python .\\agents\\generate_all_html_reports.py</code></pre></div><h2>8. Project architecture (concise)</h2><div class='card'><ol><li>Launcher or CLI triggers an agent.</li><li>Agent drives browser (Playwright), captures per-step metadata + screenshots.</li><li>Agent writes JSON → renderer converts JSON → MD + HTML for human consumption.</li><li>Optional: Agent4 consumes JSON to produce automation scripts; Agent2 creates additional reports.</li></ol></div><footer class='muted'>Last updated: """ + datetime.now().isoformat(sep=' ', timespec='seconds') + """</footer></body></html>"""
    GUIDE_PATH.write_text(GUIDE_HTML, encoding="utf-8")
    generated.append(GUIDE_PATH)

# open generated files
for f in generated:
    webbrowser.open(f.resolve().as_uri())

print("Opened:", ", ".join(str(x) for x in generated))
