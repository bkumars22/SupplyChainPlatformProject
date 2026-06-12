# Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
# Supply Chain Intelligence Platform
# Licensed under MIT License — see LICENSE file for details

"""Agent12: Report generator wrapper

This script invokes the project's HTML report generator (generate_all_html_reports.py).
Run from project root or agents/ folder. It will produce HTML reports from any
capture JSON files and open them in the default browser.
"""
from pathlib import Path
import subprocess
import sys


def main():
    here = Path(__file__).parent
    gen = here / "generate_all_html_reports.py"
    if not gen.exists():
        print("generate_all_html_reports.py not found in agents/. Cannot proceed.")
        sys.exit(1)

    # Use the same Python interpreter to run the generator
    try:
        subprocess.check_call([sys.executable, str(gen)])
    except subprocess.CalledProcessError as e:
        print("Generator failed with exit code:", e.returncode)
        sys.exit(e.returncode)


if __name__ == '__main__':
    main()
