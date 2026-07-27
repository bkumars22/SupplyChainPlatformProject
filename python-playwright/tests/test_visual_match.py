"""
Tests for VisualMatchStrategy.

No live browser needed here - we PROVE the matching logic works by
generating two synthetic images ourselves:
  1. A "full page screenshot" - a plain background with a drawn button
  2. A "reference image" - a cropped image of just that same button

cv2 does the actual template-matching search; nothing about the match
location or confidence is asserted from guesswork - it's the real
output of a real OpenCV call against real image files (written to
pytest's tmp_path, not the repo).
"""

import cv2
import numpy as np

from locators.modular_locator_framework import ModularLocatorEngine
from locators.visual_match_strategy import VisualMatchStrategy

TRUE_BOX = (350, 120, 470, 160)  # x1, y1, x2, y2 - known position of the drawn button


def _create_fake_page_screenshot(path):
    """Draws a plain page with a blue 'button' at a known position."""
    page = np.full((400, 600, 3), 240, dtype=np.uint8)  # light gray background

    x1, y1, x2, y2 = TRUE_BOX
    cv2.rectangle(page, (x1, y1), (x2, y2), (200, 130, 40), -1)   # blue fill
    cv2.rectangle(page, (x1, y1), (x2, y2), (255, 255, 255), 2)   # white border
    cv2.putText(page, "Submit", (x1 + 25, y1 + 25), cv2.FONT_HERSHEY_SIMPLEX,
                0.6, (255, 255, 255), 2)

    cv2.imwrite(str(path), page)


def _create_reference_crop(full_page_path, out_path):
    """Crops just the button region out of the full page - this becomes our reference image."""
    full_page = cv2.imread(str(full_page_path))
    x1, y1, x2, y2 = TRUE_BOX
    cv2.imwrite(str(out_path), full_page[y1:y2, x1:x2])


def _create_unrelated_image(path):
    """A completely different image, with no button - used to test the 'not found' case."""
    blank = np.full((100, 100, 3), 250, dtype=np.uint8)
    cv2.circle(blank, (50, 50), 30, (0, 0, 200), -1)  # just a red circle
    cv2.imwrite(str(path), blank)


def test_finds_reference_image_at_its_true_position(tmp_path):
    page_path = tmp_path / "fake_page.png"
    ref_path = tmp_path / "button_reference.png"
    _create_fake_page_screenshot(page_path)
    _create_reference_crop(page_path, ref_path)

    strategy = VisualMatchStrategy(str(ref_path), confidence_threshold=0.8)
    match = strategy.find(str(page_path))

    assert match is not None
    assert match["x"] == TRUE_BOX[0]
    assert match["y"] == TRUE_BOX[1]
    assert match["width"] == TRUE_BOX[2] - TRUE_BOX[0]
    assert match["height"] == TRUE_BOX[3] - TRUE_BOX[1]
    assert match["confidence"] >= 0.8


def test_reports_not_found_for_unrelated_image(tmp_path):
    page_path = tmp_path / "fake_page.png"
    unrelated_path = tmp_path / "unrelated.png"
    _create_fake_page_screenshot(page_path)
    _create_unrelated_image(unrelated_path)

    strategy = VisualMatchStrategy(str(unrelated_path), confidence_threshold=0.8)
    match = strategy.find(str(page_path))

    assert match is None


def test_missing_files_return_none_instead_of_raising(tmp_path):
    strategy = VisualMatchStrategy(str(tmp_path / "does_not_exist.png"))
    match = strategy.find(str(tmp_path / "also_missing.png"))

    assert match is None


def test_below_threshold_match_is_treated_as_not_found(tmp_path):
    page_path = tmp_path / "fake_page.png"
    ref_path = tmp_path / "button_reference.png"
    _create_fake_page_screenshot(page_path)
    _create_reference_crop(page_path, ref_path)

    # A real match exists here (same fixtures as the success test above) - a
    # threshold of 1.01 makes it impossible to clear, proving the
    # confidence_threshold gate is actually enforced, not just decorative.
    strategy = VisualMatchStrategy(str(ref_path), confidence_threshold=1.01)
    match = strategy.find(str(page_path))

    assert match is None


def test_plugs_into_modular_locator_engine_as_a_fallback_strategy(tmp_path):
    """
    The real point of this strategy per its module docstring: it's a
    fallback module for ModularLocatorEngine, not just a standalone class.
    "dom" here is repurposed as the screenshot path (see
    VisualMatchStrategy's docstring) - the engine itself needs zero
    changes to support this.
    """
    page_path = tmp_path / "fake_page.png"
    ref_path = tmp_path / "button_reference.png"
    _create_fake_page_screenshot(page_path)
    _create_reference_crop(page_path, ref_path)

    engine = ModularLocatorEngine(strategies=[VisualMatchStrategy(str(ref_path), confidence_threshold=0.8)])
    result = engine.find_element(dom=str(page_path), target=None)

    assert result["strategy_used"] == "visual_match"
    assert result["element"]["x"] == TRUE_BOX[0]
    assert result["healed"] is False
