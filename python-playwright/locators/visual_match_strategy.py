"""
Visual Match Strategy
----------------------
A locator strategy that finds a UI element by IMAGE, not by ID/text/class.

This is a genuine computer-vision technique called "template matching":
you give it a small reference image (a screenshot of just the button/icon
you're looking for), and it searches a larger image (the full page
screenshot) for the region that looks most like it.

This is a real, useful fallback for the Modular Locator Framework -
when ID, text, AND class-based matching all fail (e.g. the element has
no reliable text or attributes, like an icon-only button), a saved
reference image can still find it purely by appearance.

Plugs into the same `LocatorStrategy` contract as every other module in
this framework (modular_locator_framework.py, playwright_strategies.py),
but with one difference worth being explicit about: every other strategy
here searches a DOM (a list of elements, or a live Playwright Page), while
this one searches a screenshot. `find()`'s first argument is still named
`dom` to satisfy that shared contract, but for this strategy it must be a
path to a full-page screenshot image, not a DOM list - see
ModularLocatorEngine.find_element(dom=screenshot_path, target=None) in
test_visual_match.py for how that looks in practice.

Requires: opencv-python-headless, numpy (see requirements.txt) - the
headless build, not opencv-python, since this only ever calls
imread/imwrite/matchTemplate (no GUI/imshow) and headless avoids a
missing-libGL.so.1 failure on CI's display-less Ubuntu runner.

Author: Kumaraswamy B
"""

import cv2
import numpy as np

from locators.modular_locator_framework import LocatorStrategy


class VisualMatchStrategy(LocatorStrategy):
    """
    Finds an element by comparing a small reference image against
    regions of a larger screenshot, using OpenCV's template matching.
    """
    name = "visual_match"

    def __init__(self, reference_image_path, confidence_threshold=0.8):
        """
        reference_image_path: path to a saved image of JUST the element
                               (e.g. a cropped screenshot of one button)
        confidence_threshold: how close the match must be, from 0 to 1.
                               0.8 = 80% visual similarity required.
        """
        self.reference_image_path = reference_image_path
        self.confidence_threshold = confidence_threshold

    def find(self, dom, target=None):
        """
        dom: path to the FULL PAGE screenshot to search within (see the
             module docstring for why this strategy repurposes "dom" as
             a screenshot path rather than a DOM list)
        target: unused here (kept only so this fits the same .find()
                shape as the other strategies in the framework)

        Returns a dict with the match location and confidence, or None
        if nothing matched closely enough.
        """
        screenshot_path = dom

        # Load both images in grayscale - color doesn't matter for shape/layout matching,
        # and grayscale is faster and more robust to minor color/theme changes.
        full_page = cv2.imread(screenshot_path, cv2.IMREAD_GRAYSCALE)
        reference = cv2.imread(self.reference_image_path, cv2.IMREAD_GRAYSCALE)

        if full_page is None or reference is None:
            return None

        # cv2.matchTemplate slides the small reference image across the
        # full screenshot and scores how well it matches at every position.
        result = cv2.matchTemplate(full_page, reference, cv2.TM_CCOEFF_NORMED)

        # We only care about the single BEST match location and its score.
        min_val, max_val, min_loc, max_loc = cv2.minMaxLoc(result)

        if max_val < self.confidence_threshold:
            return None  # nothing matched closely enough - treat as not found

        ref_height, ref_width = reference.shape
        top_left_x, top_left_y = max_loc

        return {
            "x": top_left_x,
            "y": top_left_y,
            "width": ref_width,
            "height": ref_height,
            "center_x": top_left_x + ref_width // 2,
            "center_y": top_left_y + ref_height // 2,
            "confidence": round(float(max_val), 4),
        }


def find_element_with_healing(screenshot_path, reference_image_path, confidence_threshold=0.8):
    """
    Convenience wrapper matching the reporting style of the earlier
    ModularLocatorEngine, so this plugs into the same framework shape.
    """
    strategy = VisualMatchStrategy(reference_image_path, confidence_threshold)
    match = strategy.find(screenshot_path)

    report = {"strategy_used": strategy.name, "match": match}
    if match:
        print(f"  FOUND via {strategy.name} (confidence: {match['confidence']})")
        print(f"  Location: x={match['x']}, y={match['y']}, "
              f"size={match['width']}x{match['height']}")
    else:
        print(f"  NOT FOUND - no region matched above {confidence_threshold} confidence")

    return report
