/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}(g.eto || (g.eto = {})).nunjucksGlobalGetPaginationButtons = f()}})(function(){var define,module,exports;return (function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
"use strict";

module.exports = function (params) {
  var text = params.text;
  var items = params.items;
  var backItem = params.backItem;
  var forwardItem = params.forwardItem;
  var min = params.min;
  var max = params.max;
  var current = params.current;
  var count = params.count || 6;
  var url = params.url;

  // Passed a string of text, just return that.
  if (text) {
    return {
      text: text,
      backItem: backItem,
      forwardItem: forwardItem
    };
  }

  // Already passed a list of items, just return that.
  if (items) {
    return {
      items: items,
      backItem: backItem,
      forwardItem: forwardItem
    };
  }
  var hasPrevious = current !== min;
  var hasNext = current !== max;
  var buttons = {
    items: []
  };

  // Previous button
  if (hasPrevious) {
    buttons.backItem = {
      text: current - 1,
      href: getHref(current - 1, url)
    };
  }

  // Next button
  if (hasNext) {
    buttons.forwardItem = {
      text: current + 1,
      href: getHref(current + 1, url)
    };
  }
  var halfCount = Math.floor(count / 2);
  var useMin = current - halfCount <= min;
  var useMax = current + halfCount >= max;
  var startBound = useMin ? min : current - halfCount;
  var endBound = useMax ? max : current + halfCount;
  var difference;

  // If we are using the lowest bound, try to add the missing indexes to the end
  if (useMin && !useMax) {
    difference = halfCount - (current - min);
    while (endBound < max && difference > 0) {
      endBound++;
      difference--;
    }
    if (endBound === max) {
      useMax = true;
    }
  } else if (useMax && !useMin) {
    // If we are using the highest bound, try to add the missing indexes to the beginning
    difference = halfCount - (max - current);
    while (startBound > min && difference > 0) {
      startBound--;
      difference--;
    }
    if (startBound === min) {
      useMin = true;
    }
  }

  // If we aren't using the min bound, a spacer will be inserted
  if (!useMin) {
    startBound++;
  }

  // If we aren't using the max bound, a spacer will be inserted
  if (!useMax) {
    endBound--;
  }
  for (var LCV = startBound; LCV <= endBound; LCV++) {
    if (!useMin && LCV === startBound) {
      // Add the first item of the list
      buttons.items.push({
        text: min,
        href: getHref(min, url),
        active: false
      });
      // If the spacer can be a number item (e.g. 1...3)
      if (LCV - 2 === min) {
        var minMiddleItem = LCV - 1;
        buttons.items.push({
          text: minMiddleItem,
          href: getHref(minMiddleItem, url),
          active: false
        });
      } else {
        // Add first spacer
        buttons.items.push('...');
      }
    }
    buttons.items.push({
      text: LCV,
      href: getHref(LCV, url),
      active: current === LCV
    });
    if (!useMax && LCV === endBound) {
      // If the spacer can be a number item (e.g. 13...15)
      if (LCV + 2 === max) {
        var maxMiddleItem = LCV + 1;
        buttons.items.push({
          text: maxMiddleItem,
          href: getHref(maxMiddleItem, url),
          active: false
        });
      } else {
        // Add last spacer.
        buttons.items.push('...');
      }
      // Add the last item of the list
      buttons.items.push({
        text: max,
        href: getHref(max, url),
        active: false
      });
    }
  }
  return buttons;
};
function getHref(text, str) {
  return str ? str.replace('${item}', text) : text;
}


},{}]},{},[1])(1)
});

//# sourceMappingURL=get-pagination-buttons.js.map
