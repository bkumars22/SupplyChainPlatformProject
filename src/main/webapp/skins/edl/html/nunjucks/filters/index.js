/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}(g.eto || (g.eto = {})).nunjucksFilterIndex = f()}})(function(){var define,module,exports;return (function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
"use strict";

module.exports = function (attrs) {
  var str = '';
  var i;
  for (i in attrs) {
    if (typeof attrs[i] === 'boolean') {
      if (attrs[i]) {
        str += i + ' ';
      }
    } else {
      str += i + '="' + attrs[i] + '" ';
    }
  }
  return str;
};


},{}],2:[function(require,module,exports){
"use strict";

module.exports = function (env) {
  env.addFilter('isArray', require('./is-array'));
  env.addFilter('attributes', require('./attributes'));
};


},{"./attributes":1,"./is-array":3}],3:[function(require,module,exports){
"use strict";

module.exports = function (mixed) {
  return mixed instanceof Array;
};


},{}]},{},[2])(2)
});

//# sourceMappingURL=index.js.map
