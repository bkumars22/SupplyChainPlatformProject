/* 2024-11-29 
 * DO NOT MODIFY THIS FILE DIRECTLY OR YOU WILL NOT BE ABLE TO UPDATE TO A NEW VERSION.
 *
 * Copyright (c) 2024 scplatform, LLC
 * All Rights Reserved.
 * Version 24.3.1-SNAPSHOT 
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF scplatform
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */
(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}(g.eto || (g.eto = {})).ComplexAutocomplete = f()}})(function(){var define,module,exports;return (function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
module.exports = function(attrs) {

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
module.exports = function(env) {
  env.addFilter('isArray', require('./is-array'));
  env.addFilter('attributes', require('./attributes'));
};

},{"./attributes":1,"./is-array":3}],3:[function(require,module,exports){
module.exports = function(mixed) {
  return mixed instanceof Array;
};

},{}],4:[function(require,module,exports){
module.exports = function(params) {

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
    return {text: text, backItem: backItem, forwardItem: forwardItem};
  }

  // Already passed a list of items, just return that.
  if (items) {
    return {items: items, backItem: backItem, forwardItem: forwardItem};
  }

  var hasPrevious = current !== min;
  var hasNext = current !== max;
  var buttons = {items: []};

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
  var useMin = (current - halfCount <= min);
  var useMax = (current + halfCount >= max);
  var startBound = (useMin) ? min : current - halfCount;
  var endBound = (useMax) ? max : current + halfCount;
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
  } else if (useMax && !useMin) { // If we are using the highest bound, try to add the missing indexes to the beginning
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
      if (LCV-2 === min) {
        var minMiddleItem = LCV-1;
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
      if (LCV+2 === max) {
        var maxMiddleItem = LCV+1;
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

},{}],5:[function(require,module,exports){
module.exports = function(env) {
  env.addGlobal('uniqueId', require('./unique-id'));
  env.addGlobal('getPaginationButtons', require('./get-pagination-buttons'));
  env.addGlobal('parseGridColumns', require('./parse-grid-columns'));
  env.addGlobal('mapFavorites', require('./map-favorites'));
};

},{"./get-pagination-buttons":4,"./map-favorites":6,"./parse-grid-columns":7,"./unique-id":8}],6:[function(require,module,exports){
/**
 * Maps list of favorites to overflow menu.
 */
var map = require('lodash/map');

function mapFavoriteToOverflowItem(f) {
  return {
    url: f.url,
    text: f.text,
    attrs: {
      title: f.title,
      "data-id": f.id,
      "data-home": f.isHome === true,
      "data-app-name": (f.app ? f.app : ''),
      "data-role-name": (f.roleName ? f.roleName : ''),
      "data-hide": f.hide === true,
      "data-external": f.isExternal === true
    },
    className: (f.hide === true ? 'display-xs-none' : '')
  }
}

module.exports = function(favorites) {
  return map(favorites, function(f) {
    var o = mapFavoriteToOverflowItem(f);
    if (f.items) o.items = map(f.items, mapFavoriteToOverflowItem)
    return o;
  });
};

},{"lodash/map":286}],7:[function(require,module,exports){
/**
 * Get Grid configuration.

 {
  columns             Array[Column]
  [{
    name              String
    label             String
    title             String
    renderedCell      Boolean
    collapsed         Boolean
    frozen            Boolean
    resizeable        Boolean
    sortable          Boolean
    sortOrder         'asc' | 'desc'
    sortIndex         Number
    expandable        Boolean
    expandableLines   Number
    alignment         'left' | 'center' | 'right'
    exposedActions    Array[Action]
    [{
      label           String
      icon            String
      iconClassName   String
      separator       Boolean
    }]
    menuActions       Array[Action]
    attrs             Object
    columns           Array[Column]
    dataField         String
    filterRow         'string'  | 'number'  | 'date' | 'boolean' | 'enum'
    width             Number
  }]
  filterRow           Boolean
  addRow
  {
    position          'top' | 'bottom'
    newRow            Boolean
                      // Built from column.filter.type
  }
  rows                Array[Object]
  rowNumbers          Boolean
  rowSelection        {
    type              'none' | 'radio' | 'checkbox' | 'checkall' | 'menu'
    name              String // name for all inputs
    valueField        String // dataField for value of input
  }
  rowExposedActions   Array[Action]
  rowMenuActions      Array[Action]
 }

 */

var map = require('lodash/map');

var actionFilter = {
  id: 'filter',
  label: 'Filter',
  icon: 'filter_list'
};

var actionCollapse = {
  id: 'collapse',
  label: 'Collapse',
  icon: 'vertical_align_bottom',
  iconClassName: 'rotate-90'
};

var actionFreeze = {
  id: 'freeze',
  label: 'Freeze',
  icon: 'ac_unit'
};

var actionResize = {
  id: 'resize',
  label: 'Resize',
  icon: 'straighten'
};

var actionRemove = {
  id: 'remove',
  label: 'Remove',
  icon: 'remove_circle_outline'
};

var actionGridEditor = {
  id: 'config',
  label: 'Open Grid Editor',
  icon: 'settings'
};

var columnDefaults = {
  collapsed: false,
  frozen: false,
  resizeable: true,
  sortable: true,
  expandable: false,
  expandableLines: 2,
  menuActions: [ actionFilter, actionFreeze, actionCollapse, actionResize, actionRemove, { separator:true }, actionGridEditor ]
};

module.exports = function(options) {

  // Deep copy
  options = JSON.parse(JSON.stringify(options));

  // Must have columns
  if (!isNonEmptyArray(options.columns)) {
    // @todo instead of throwing an error, just return empty everything
    throw Error('Parameter `columns` must be a non-empty array.');
  }

  // Set some column defaults.
  setColumnDefaults(options.columns);

  var frozenColumnsTree = [];
  var scrollingColumnsTree = [];

  // Add rowActions
  if ( isNonEmptyArray(options.rowExposedActions)
    || isNonEmptyArray(options.rowMenuActions)) {
    frozenColumnsTree.push({
      rowActions: true,
      rowExposedActions: options.rowExposedActions,
      rowMenuActions: options.rowMenuActions
    });
  }

  // Add rowNumbers
  if (options.rowNumbers) {
    frozenColumnsTree.push({ rowNumbers: true });
  }

  // Add rowDrilldown
  if (options.rowDrilldown) {
    frozenColumnsTree.push({ rowDrilldown: true });
  }

  // Add rowSelection
  if (options.rowSelection) {

    // rowSelection type defaults to checkall
    options.rowSelection.type = options.rowSelection.type || 'checkall';

    if ((['radio', 'checkbox', 'checkall', 'menu']).indexOf(options.rowSelection.type) !== -1) {
      frozenColumnsTree.push({ rowSelection: options.rowSelection });
    }
  }

  // Sort Columns into frozen and scrolling
  // NOT RECURSIVE! Only moves top level columns.
  for (var i=0; i<options.columns.length; ++i) {
    if (options.columns[i].frozen) {
      frozenColumnsTree.push(options.columns[i]);
    } else {
      scrollingColumnsTree.push(options.columns[i]);
    }
  }

  var frozen = getTheadAndFlatColumns(frozenColumnsTree);
  var scrolling = getTheadAndFlatColumns(scrollingColumnsTree);

  if (frozen.thead.length < scrolling.thead.length) {
    for (var i=frozen.thead.length; i<scrolling.thead.length; i++) {
      frozen.thead.unshift([{ colspan: frozen.columns.length }]);
    }
  } else if (scrolling.thead.length < frozen.thead.length) {
    for (var i=scrolling.thead.length; i<frozen.thead.length; i++) {
      scrolling.thead.unshift([{ colspan: scrolling.columns.length }]);
    }
  }

  return {
    frozenThead: frozen.thead,
    frozenColumns: frozen.columns,
    scrollingThead: scrolling.thead,
    scrollingColumns: scrolling.columns
  };
}

/**
 * Sets some column defaults.
 * Sets different defaults for group columns and data columns.
 * @param {Array} columns
 */
function setColumnDefaults(columns) {
  for (var i=0; i<columns.length; i++) {
    columns[i] = Object.assign({}, columnDefaults, columns[i]);
    if (columns[i].columns) setColumnDefaults(columns[i].columns);
  }
}

function getTheadAndFlatColumns(tree) {

  // Get <thead> matrix.
  var matrix = traverse(tree).matrix;

  // We'll store all columns in a `flatColumns` array that we'll use later to render each data row.
  var columns = [];
  for (var i=0; i<matrix[0].length; i++) {

    // Store flat column definition.
    columns.push(matrix[0][i]);

    // Before we merge empty cells together, we need to remove the RowActions and RowNumbers columns from the matrix.
    // They have no content in the <thead> so these can be merged.
    if (matrix[0][i].rowActions || matrix[0][i].rowNumbers || matrix[0][i].rowDrilldown) delete matrix[0][i];
  }

  return {
    columns: columns,

    // Lastly, let's insert some empty cells in the holes in our matrix, "merging" with colspan where possible.
    // Then we normalize the array by removing undefined indices so we can iterate over cells without worrying about colspan.
    thead: normalize(merge(matrix))
  };
}

/**
 * Given a column, get colspan and rowIndex.
 * @param {Array} columns - array of columns
 * @param {Number} columnOffset - parent column column index
 * @param {Array} matrix - for placing columns
 * @return {Object} { matrix, rowIndex, colspan }
 */
function traverse(columns, columnOffset, matrix) {
  if (columnOffset === undefined) columnOffset = 0;
  if (matrix === undefined) matrix = [[]];

  // Max column row index. The farthest distance to the bottom header row.
  var maxRowIndex = 0;

  // Total colspan of all columns passed.
  var totalColspan = 0;

  for (var i=0, column, columnIndex, rowIndex, result; i<columns.length; i++) {
    column = columns[i];
    columnIndex = columnOffset + totalColspan;
    if (Array.isArray(column.columns) && column.columns.length) {

      // Get rowIndex and colspan of column.
      result = traverse(column.columns, columnIndex, matrix);
      rowIndex = result.rowIndex;
      column.colspan = result.colspan;

      // Track max rowIndex.
      maxRowIndex = Math.max(maxRowIndex, rowIndex);

      // Place column in header matrix.
      if (!matrix[rowIndex]) matrix[rowIndex] = [];
      matrix[rowIndex][columnIndex] = column;

      // Accum colspan.
      totalColspan += result.colspan;

    } else {
      matrix[0][columnIndex] = column;
      totalColspan++;
    }
  }

  return {
    matrix: matrix,
    rowIndex: maxRowIndex + 1,
    colspan: totalColspan
  };
}

/**
 * Takes a header matrix and fills empty spaces with blank <th>s.
 * Spans columns with colspan where appropriate.
 * @param {Array} matrix of placed columns
 * @return {Array}
 */
function merge(matrix) {
  var numColumns = matrix[0].length

  // We'll use this to track which columns can be merged.
  var canMergeMask = (new Array(numColumns)).fill(true);
  var canMerge = function(i, j) { return canMergeMask[j] && !matrix[i][j] };

  // This gets the colspan of a matrix cell.
  var colspan = function(i,j) { return matrix[i][j] ? matrix[i][j].colspan || 1 : 1 };

  // Matrix is built upside down, so iterate reversed, from the "top".
  for (var i = matrix.length-1, j, previousMerging; i >= 0; --i) {
    j = 0;
    previousMerging = false;
    while (j < numColumns) {
      var canMergeCurrent = canMerge(i,j);

      // Start merge.
      if (!previousMerging && canMergeCurrent) {
        if (!matrix[i][j]) matrix[i][j] = {};
        matrix[i][j].colspan = 1;
        previousMerging = matrix[i][j];
      }
      // Continue merging.
      else if (previousMerging && canMergeCurrent) {
        if (matrix[i][j]) delete matrix[i][j];
        ++previousMerging.colspan;
      }
      // Stop merge.
      else if (previousMerging && !canMergeCurrent) {
        if (!matrix[i][j]) matrix[i][j] = {};
        if (previousMerging.colspan === 1) delete previousMerging.colspan;
        previousMerging = false;
        for (var k=0; k < colspan(i,j); k++) canMergeMask[j+k] = false;
      }
      // Not merging, still can't, but empty spot, so add cell.
      else if (!matrix[i][j]) matrix[i][j] = {};

      // Incrementing by colspan skips spots in matrix
      // that are actually covered by a previously spanning column.
      j += colspan(i,j);
    }
  }
  return matrix;
}

/**
 * Takes a header matrix and returns an array without undefined elements.
 * @param {Array} header
 * @return {Array}
 */
function normalize(matrix) {
  var thead=[];
  for (var i=matrix.length-1, tr; i>=0; --i) {
    thead.push((tr=[]));
    for (var j=0; j<matrix[i].length; ++j) {
      if (matrix[i][j]) tr.push(matrix[i][j]);
    }
  }
  return thead;
}

/**
 * Returns true if parameter is a non-empty array.
 * @param  {Mixed} a
 * @return {Boolean}
 */
function isNonEmptyArray(a) {
  return Array.isArray(a) && a.length;
}

},{"lodash/map":286}],8:[function(require,module,exports){
/**
 * Creates a unique id. The Javascript uses prefix `eto`, so here
 * we use `etot` (extra t for template) to assure no collisions.
 */
var uniqueId = require('lodash/uniqueId');
module.exports = uniqueId.bind(null, 'etot');

},{"lodash/uniqueId":311}],9:[function(require,module,exports){
module.exports = function ( nunjucks, env, obj, __require ) {

  var oldRoot = obj.root;

  obj.root = function( env, context, frame, runtime, cb ) {
    var oldGetTemplate = env.getTemplate;
    env.getTemplate = function( name, ec, parentName, ignoreMissing, cb ) {
      if( typeof ec === "function" ) {
        cb = ec;
        ec = false;
      }

      if(typeof parentName === 'function') {
        cb = parentName;
        parentName = null;
        ec = ec || false;
      }

      if(typeof ec === 'function') {
        cb = ec;
        ec = false;
      }

      var _require = function(name) {
        try {
          return __require(name);
        } catch (e) {
          if ( frame.get( "_require" ) ) return frame.get( "_require" )( name );
        }
      };
      var tmpl = _require( name );
      frame.set( "_require", _require );
      if( ec ) tmpl.compile();
      cb( null, tmpl );
    };

    oldRoot( env, context, frame, runtime, function( err, res ) {
      env.getTemplate = oldGetTemplate;
      cb( err, res );
    } );
  };

  var src = {
    obj: obj,
    type: "code"
  };

  return new nunjucks.Template( src, env );

};

},{}],10:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
require('./results-complex.js');
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/complex-autocomplete.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n<div ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "attrs"))), env.opts.autoescape);
output += " class=\"eto-complex-autocomplete";
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "disabled")?" disabled":"")), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "required")?" required":"")), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "responsive")?" row":"")), env.opts.autoescape);
output += runtime.suppressValue(((((!(env.getFilter("isArray").call(context, runtime.contextOrFrameLookup(context, frame, "value"))) && runtime.contextOrFrameLookup(context, frame, "value")) || ((env.getFilter("isArray").call(context, runtime.contextOrFrameLookup(context, frame, "value"))) && runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "value")),"length")))?" has-value":"")), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "tip")?" has-tip":"")), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "className")?" " + runtime.contextOrFrameLookup(context, frame, "className"):"")), env.opts.autoescape);
output += "\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "messageType")?" data-message-type=\"" + runtime.contextOrFrameLookup(context, frame, "messageType") + "\"":""))), env.opts.autoescape);
output += ">\n  <label class=\"eto-complex-autocomplete__label";
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "labelClassName")?" " + runtime.contextOrFrameLookup(context, frame, "labelClassName"):"")), env.opts.autoescape);
output += "\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "id")?" for=\"" + runtime.contextOrFrameLookup(context, frame, "id") + "\"":""))), env.opts.autoescape);
output += ">";
output += runtime.suppressValue((runtime.contextOrFrameLookup(context, frame, "label")?runtime.contextOrFrameLookup(context, frame, "label"):""), env.opts.autoescape);
output += "</label>\n  <div class=\"eto-complex-autocomplete__container";
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "containerClassName")?" " + runtime.contextOrFrameLookup(context, frame, "containerClassName"):"")), env.opts.autoescape);
output += "\">\n    <div class=\"eto-complex-autocomplete__gray-container\">\n      <div class=\"eto-complex-autocomplete__field-container\">\n        <input class=\"eto-complex-autocomplete__field\" type=\"text\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "id")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "id") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "name")?" name=\"" + runtime.contextOrFrameLookup(context, frame, "name") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "placeholder") && !((!(env.getFilter("isArray").call(context, runtime.contextOrFrameLookup(context, frame, "value"))) && runtime.contextOrFrameLookup(context, frame, "value")) || ((env.getFilter("isArray").call(context, runtime.contextOrFrameLookup(context, frame, "value"))) && runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "value")),"length")))?" placeholder=\"" + runtime.contextOrFrameLookup(context, frame, "placeholder") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "delimiter")?" delimiter=\"" + runtime.contextOrFrameLookup(context, frame, "delimiter") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "exclude")?" exclude=\"" + runtime.contextOrFrameLookup(context, frame, "exclude") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "replaceDelimiter")?" replaceDelimiter=\"" + runtime.contextOrFrameLookup(context, frame, "replaceDelimiter") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "required")?" required":"")), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "disabled")?" disabled":"")), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, (" autocomplete=\"off\"")), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, (" autocorrect=\"off\"")), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, (" spellcheck=\"false\"")), env.opts.autoescape);
output += ">\n          <span class=\"eto-complex-autocomplete__field__addon\"><i translate=\"no\" class=\"notranslate md-icon\">close</i></span>\n      </div>";
if(runtime.contextOrFrameLookup(context, frame, "tip")) {
output += "<button type=\"button\" class=\"eto-complex-autocomplete__tip\" ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "tipAttrs"))), env.opts.autoescape);
output += "><i translate=\"no\" class=\"notranslate md-icon\">";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "tip"), env.opts.autoescape);
output += "</i></button>";
;
}
output += "<div class=\"eto-complex-autocomplete__inline-tags\">";
frame = frame.push();
var t_3 = runtime.contextOrFrameLookup(context, frame, "value");
if(t_3) {t_3 = runtime.fromIterator(t_3);
var t_2 = t_3.length;
for(var t_1=0; t_1 < t_3.length; t_1++) {
var t_4 = t_3[t_1];
frame.set("v", t_4);
frame.set("loop.index", t_1 + 1);
frame.set("loop.index0", t_1);
frame.set("loop.revindex", t_2 - t_1);
frame.set("loop.revindex0", t_2 - t_1 - 1);
frame.set("loop.first", t_1 === 0);
frame.set("loop.last", t_1 === t_2 - 1);
frame.set("loop.length", t_2);
output += "<input type=\"hidden\" name=\"";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "name"), env.opts.autoescape);
output += "_value\" value=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, t_4), env.opts.autoescape);
output += "\">";
;
}
}
frame = frame.pop();
output += "</div>\n      <div class=\"eto-complex-autocomplete__message\" role=\"alert\" aria-live=\"polite\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "messageId")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "messageId") + "\"":""))), env.opts.autoescape);
output += ">";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "messageContent")?runtime.contextOrFrameLookup(context, frame, "messageContent"):""))), env.opts.autoescape);
output += "</div>\n    </div>\n  </div>";
var tasks = [];
tasks.push(
function(callback) {
env.getTemplate("./results-complex.js", false, "C:/unity/html/templates/complex-autocomplete.js", false, function(t_6,t_5) {
if(t_6) { cb(t_6); return; }
callback(null,t_5);});
});
tasks.push(
function(template, callback){
template.render(context.getVariables(), frame, function(t_8,t_7) {
if(t_8) { cb(t_8); return; }
callback(null,t_7);});
});
tasks.push(
function(result, callback){
output += result;
callback(null);
});
env.waterfall(tasks, function(){
output += "</div>\n";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
});
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/complex-autocomplete.js'], require);

},{"../../shim":9,"./results-complex.js":14,"nunjucks":314}],11:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/popover.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n<div ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "attrs"))), env.opts.autoescape);
output += " class=\"eto-popover";
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "hideCaret")?" hide-caret":"")), env.opts.autoescape);
output += runtime.suppressValue((runtime.contextOrFrameLookup(context, frame, "className")?" " + runtime.contextOrFrameLookup(context, frame, "className"):""), env.opts.autoescape);
output += "\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "anchorX")?" data-anchor-x=\"" + runtime.contextOrFrameLookup(context, frame, "anchorX") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "anchorY")?" data-anchor-y=\"" + runtime.contextOrFrameLookup(context, frame, "anchorY") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "id")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "id") + "\"":""))), env.opts.autoescape);
output += ">\n  <div class=\"eto-popover__content\">\n    ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.contextOrFrameLookup(context, frame, "content")), env.opts.autoescape);
output += "\n  </div>\n  <span class=\"eto-popover__caret\"></span>\n</div>\n\n";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/popover.js'], require);

},{"../../shim":9,"nunjucks":314}],12:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/result-complex-item.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n\n";
if(runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "groupItem")),"length")) {
output += "\n<div class=\"eto-results__scroll\">\n  \n  ";
frame = frame.push();
var t_3 = runtime.contextOrFrameLookup(context, frame, "groupItem");
if(t_3) {t_3 = runtime.fromIterator(t_3);
var t_2 = t_3.length;
for(var t_1=0; t_1 < t_3.length; t_1++) {
var t_4 = t_3[t_1];
frame.set("group", t_4);
frame.set("loop.index", t_1 + 1);
frame.set("loop.index0", t_1);
frame.set("loop.revindex", t_2 - t_1);
frame.set("loop.revindex0", t_2 - t_1 - 1);
frame.set("loop.first", t_1 === 0);
frame.set("loop.last", t_1 === t_2 - 1);
frame.set("loop.length", t_2);
output += "\n    ";
if(runtime.memberLookup((runtime.memberLookup((t_4),"items")),"length") > 0) {
output += "\n    <ul>\n      ";
if(runtime.memberLookup((t_4),"title") && runtime.memberLookup((runtime.memberLookup((t_4),"items")),"length") > 0) {
output += "\n        <li class=\"eto-results__group-title\">";
output += runtime.suppressValue(runtime.memberLookup((t_4),"title"), env.opts.autoescape);
output += "</li>\n      ";
;
}
output += "\n      ";
frame = frame.push();
var t_7 = runtime.memberLookup((t_4),"items");
if(t_7) {t_7 = runtime.fromIterator(t_7);
var t_6 = t_7.length;
for(var t_5=0; t_5 < t_7.length; t_5++) {
var t_8 = t_7[t_5];
frame.set("item", t_8);
frame.set("loop.index", t_5 + 1);
frame.set("loop.index0", t_5);
frame.set("loop.revindex", t_6 - t_5);
frame.set("loop.revindex0", t_6 - t_5 - 1);
frame.set("loop.first", t_5 === 0);
frame.set("loop.last", t_5 === t_6 - 1);
frame.set("loop.length", t_6);
var t_9;
t_9 = runtime.memberLookup((t_8),"_text");
frame.set("dataValue", t_9, true);
if(frame.topLevel) {
context.setVariable("dataValue", t_9);
}
if(frame.topLevel) {
context.addExport("dataValue", t_9);
}
if(runtime.memberLookup((t_8),"hide") && runtime.contextOrFrameLookup(context, frame, "hideRequired")) {
var t_10;
t_10 = "eto-results__option-hide";
frame.set("hide", t_10, true);
if(frame.topLevel) {
context.setVariable("hide", t_10);
}
if(frame.topLevel) {
context.addExport("hide", t_10);
}
;
}
else {
var t_11;
t_11 = "";
frame.set("hide", t_11, true);
if(frame.topLevel) {
context.setVariable("hide", t_11);
}
if(frame.topLevel) {
context.addExport("hide", t_11);
}
;
}
output += "\n        ";
if(runtime.memberLookup((t_8),"data")) {
output += "\n           ";
if(runtime.memberLookup((runtime.memberLookup((t_8),"data")),"value")) {
var t_12;
t_12 = runtime.memberLookup((runtime.memberLookup((t_8),"data")),"value");
frame.set("dataValue", t_12, true);
if(frame.topLevel) {
context.setVariable("dataValue", t_12);
}
if(frame.topLevel) {
context.addExport("dataValue", t_12);
}
;
}
else {
var t_13;
t_13 = runtime.memberLookup((t_8),"data");
frame.set("dataValue", t_13, true);
if(frame.topLevel) {
context.setVariable("dataValue", t_13);
}
if(frame.topLevel) {
context.addExport("dataValue", t_13);
}
;
}
output += "\n        ";
;
}
var t_14;
t_14 = ((runtime.memberLookup((t_8),"exclude")?" eto-checkbox__exclude":""));
frame.set("checkboxClassName", t_14, true);
if(frame.topLevel) {
context.setVariable("checkboxClassName", t_14);
}
if(frame.topLevel) {
context.addExport("checkboxClassName", t_14);
}
output += "\n        ";
output += "\n          ";
if(runtime.contextOrFrameLookup(context, frame, "isCopyToClipboardRequired")) {
output += "\n            ";
if(t_8 && runtime.memberLookup((t_8),"isHighlightSelection")) {
var t_15;
t_15 = ((runtime.contextOrFrameLookup(context, frame, "isSelected") || runtime.memberLookup((t_8),"selected")?"eto-results__selected-option eto-results-selected-option-color":"eto-results__option"));
frame.set("itemClass", t_15, true);
if(frame.topLevel) {
context.setVariable("itemClass", t_15);
}
if(frame.topLevel) {
context.addExport("itemClass", t_15);
}
;
}
else {
if(runtime.contextOrFrameLookup(context, frame, "isSelected") || runtime.memberLookup((t_8),"selected")) {
var t_16;
t_16 = "eto-results__selected-option";
frame.set("itemClass", t_16, true);
if(frame.topLevel) {
context.setVariable("itemClass", t_16);
}
if(frame.topLevel) {
context.addExport("itemClass", t_16);
}
;
}
else {
var t_17;
t_17 = "eto-results__option";
frame.set("itemClass", t_17, true);
if(frame.topLevel) {
context.setVariable("itemClass", t_17);
}
if(frame.topLevel) {
context.addExport("itemClass", t_17);
}
;
}
;
}
output += "\n          ";
;
}
else {
output += "\n          ";
var t_18;
t_18 = ((runtime.contextOrFrameLookup(context, frame, "isSelected") || runtime.memberLookup((t_8),"selected")?"eto-results__selected-option eto-results-selected-option-color":"eto-results__option"));
frame.set("itemClass", t_18, true);
if(frame.topLevel) {
context.setVariable("itemClass", t_18);
}
if(frame.topLevel) {
context.addExport("itemClass", t_18);
}
;
}
output += "\n        <li class=\"";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "itemClass"), env.opts.autoescape);
output += " ";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "hide"), env.opts.autoescape);
output += "\" role=\"option\" unselectable=\"on\" data-index=\"";
output += runtime.suppressValue(runtime.memberLookup((t_8),"_index"), env.opts.autoescape);
output += "\" data-value=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, runtime.contextOrFrameLookup(context, frame, "dataValue")), env.opts.autoescape);
output += "\" data-text=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, runtime.memberLookup((t_8),"_text")), env.opts.autoescape);
output += "\" ";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "resultsId")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "resultsId") + "-" + runtime.memberLookup((t_8),"_index") + "\"":""))), env.opts.autoescape);
output += ">\n          <label class=\"eto-checkbox\">\n            <input class=\"eto-checkbox__field\" type=\"checkbox\" ";
output += runtime.suppressValue((runtime.contextOrFrameLookup(context, frame, "isSelected") || runtime.memberLookup((t_8),"selected")?" checked":""), env.opts.autoescape);
output += ">\n            <span class=\"eto-checkbox__box ";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "checkboxClassName"), env.opts.autoescape);
output += "\"></span>\n            <span class=\"eto-checkbox__label\">\n              ";
if(runtime.memberLookup((t_8),"_add")) {
output += "\n                ";
if(runtime.contextOrFrameLookup(context, frame, "addTermIconName") == "playlist_add") {
output += "\n                  <span class=\"eto-btn--link\"><i translate=\"no\" class=\"notranslate md-icon add_term\">playlist_add</i> Add</span>\n                ";
;
}
else {
if(runtime.contextOrFrameLookup(context, frame, "addTermIconName") == "add") {
output += "\n                  <span class=\"md-icon\">add</span>\n                ";
;
}
else {
output += "\n                ";
;
}
;
}
output += "\n                ";
if(runtime.contextOrFrameLookup(context, frame, "highlight")) {
output += "\n                  <b>";
output += runtime.suppressValue(runtime.memberLookup((t_8),"text"), env.opts.autoescape);
output += "</b>\n                ";
;
}
else {
output += "\n                  ";
output += runtime.suppressValue(runtime.memberLookup((t_8),"text"), env.opts.autoescape);
output += "\n                ";
;
}
output += "\n              ";
;
}
else {
output += "\n                ";
if(runtime.memberLookup((t_8),"_text") !== runtime.contextOrFrameLookup(context, frame, "undefined") && runtime.memberLookup((t_8),"text") !== runtime.memberLookup((t_8),"_text")) {
output += "\n                  ";
if(runtime.contextOrFrameLookup(context, frame, "supportHtmlAsOption")) {
output += " \n                    ";
output += runtime.suppressValue(runtime.memberLookup((t_8),"decodedText") || runtime.memberLookup((t_8),"text"), env.opts.autoescape);
output += "\n                  ";
;
}
else {
output += "\n\n                    ";
if(runtime.contextOrFrameLookup(context, frame, "isSelected") && runtime.memberLookup((t_8),"_text")) {
output += "\n                      ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_8),"_text")), env.opts.autoescape);
output += "\n                    ";
;
}
else {
output += "\n                      ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_8),"text")), env.opts.autoescape);
output += "\n                    ";
;
}
output += "\n\n                  ";
;
}
output += "\n                ";
;
}
else {
if(runtime.memberLookup((t_8),"data") !== runtime.contextOrFrameLookup(context, frame, "undefined")) {
output += "\n                  ";
if(runtime.contextOrFrameLookup(context, frame, "supportHtmlAsOption")) {
output += "\n                  ";
output += runtime.suppressValue(runtime.memberLookup((t_8),"decodedText") || runtime.memberLookup((t_8),"text"), env.opts.autoescape);
output += "\n                  ";
;
}
else {
output += "\n\n                    ";
if(runtime.contextOrFrameLookup(context, frame, "isSelected") && runtime.memberLookup((t_8),"_text")) {
output += "\n                      ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_8),"_text")), env.opts.autoescape);
output += "\n                    ";
;
}
else {
output += "\n                      ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_8),"text")), env.opts.autoescape);
output += "\n                    ";
;
}
output += "\n\n                  ";
;
}
output += "\n                ";
;
}
else {
output += "\n                  ";
if(runtime.contextOrFrameLookup(context, frame, "isSelected") && runtime.memberLookup((t_8),"_text")) {
output += "\n                    ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_8),"_text")), env.opts.autoescape);
output += "\n                  ";
;
}
else {
output += "\n                    ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_8),"text")), env.opts.autoescape);
output += "\n                  ";
;
}
output += "\n                ";
;
}
;
}
output += "\n              ";
;
}
output += "\n              ";
frame = frame.push();
var t_21 = runtime.memberLookup((t_8),"meta");
if(t_21) {t_21 = runtime.fromIterator(t_21);
var t_20 = t_21.length;
for(var t_19=0; t_19 < t_21.length; t_19++) {
var t_22 = t_21[t_19];
frame.set("m", t_22);
frame.set("loop.index", t_19 + 1);
frame.set("loop.index0", t_19);
frame.set("loop.revindex", t_20 - t_19);
frame.set("loop.revindex0", t_20 - t_19 - 1);
frame.set("loop.first", t_19 === 0);
frame.set("loop.last", t_19 === t_20 - 1);
frame.set("loop.length", t_20);
output += "\n                <span class=\"meta\">";
output += runtime.suppressValue(env.getFilter("safe").call(context, t_22), env.opts.autoescape);
output += "</span>\n              ";
;
}
}
frame = frame.pop();
output += "\n            </span>\n            <span class=\"eto-checkbox__message\"></span>\n            ";
if(runtime.contextOrFrameLookup(context, frame, "isEditable") && runtime.contextOrFrameLookup(context, frame, "isSelected")) {
output += "\n              <span class=\"eto-btn--link eto-results__selected-option-edit\"><i translate=\"no\" class=\"notranslate md-icon add_term\">edit</i></span>\n            ";
;
}
output += "\n          </label>\n        </li>\n      ";
;
}
}
frame = frame.pop();
output += "\n    </ul>\n    ";
;
}
output += "\n  ";
;
}
}
frame = frame.pop();
output += "\n</div>\n";
output += "\n  ";
if(runtime.contextOrFrameLookup(context, frame, "isCopyToClipboardRequired")) {
output += "\n  <div class=\"eto-results__copy-all-selected\">\n    <a href=\"javascript:void(0)\"><span translate=\"no\" class=\"notranslate md-icon md-icon--sm\">content_copy</span>Copy all selected to clipboard</a>\n  </div>\n  ";
;
}
output += "\n  ";
if(runtime.contextOrFrameLookup(context, frame, "viewAll")) {
output += "\n<div class=\"eto-results__view-all\"><a href=\"javascript:void(0)\">View All Results</a></div>\n  ";
;
}
output += "\n";
;
}
else {
output += "\n  <div class=\"eto-results__empty\">No results.</div>\n";
;
}
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/result-complex-item.js'], require);

},{"../../shim":9,"nunjucks":314}],13:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/result-complex-table.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n\n";
output += "\n<div class=\"eto-results__scroll\">\n  ";
var t_1;
t_1 = false;
frame.set("hasAdd", t_1, true);
if(frame.topLevel) {
context.setVariable("hasAdd", t_1);
}
if(frame.topLevel) {
context.addExport("hasAdd", t_1);
}
output += "\n  ";
frame = frame.push();
var t_4 = runtime.contextOrFrameLookup(context, frame, "groupItem");
if(t_4) {t_4 = runtime.fromIterator(t_4);
var t_3 = t_4.length;
for(var t_2=0; t_2 < t_4.length; t_2++) {
var t_5 = t_4[t_2];
frame.set("group", t_5);
frame.set("loop.index", t_2 + 1);
frame.set("loop.index0", t_2);
frame.set("loop.revindex", t_3 - t_2);
frame.set("loop.revindex0", t_3 - t_2 - 1);
frame.set("loop.first", t_2 === 0);
frame.set("loop.last", t_2 === t_3 - 1);
frame.set("loop.length", t_3);
output += "\n  ";
if(runtime.memberLookup((runtime.memberLookup((t_5),"items")),"length") > 0) {
output += "       \n    ";
frame = frame.push();
var t_8 = runtime.memberLookup((t_5),"items");
if(t_8) {t_8 = runtime.fromIterator(t_8);
var t_7 = t_8.length;
for(var t_6=0; t_6 < t_8.length; t_6++) {
var t_9 = t_8[t_6];
frame.set("item", t_9);
frame.set("loop.index", t_6 + 1);
frame.set("loop.index0", t_6);
frame.set("loop.revindex", t_7 - t_6);
frame.set("loop.revindex0", t_7 - t_6 - 1);
frame.set("loop.first", t_6 === 0);
frame.set("loop.last", t_6 === t_7 - 1);
frame.set("loop.length", t_7);
output += "\n      ";
if(runtime.memberLookup((t_9),"_add")) {
output += "       \n        ";
var t_10;
t_10 = true;
frame.set("hasAdd", t_10, true);
if(frame.topLevel) {
context.setVariable("hasAdd", t_10);
}
if(frame.topLevel) {
context.addExport("hasAdd", t_10);
}
output += "\n      ";
;
}
output += "\n    ";
;
}
}
frame = frame.pop();
output += "\n  ";
;
}
output += "\n  ";
;
}
}
frame = frame.pop();
output += "\n\n  ";
if(runtime.contextOrFrameLookup(context, frame, "hasAdd") && !runtime.contextOrFrameLookup(context, frame, "isSelected")) {
output += "\n  <ul class=\"add-term-group\">\n    ";
frame = frame.push();
var t_13 = runtime.contextOrFrameLookup(context, frame, "groupItem");
if(t_13) {t_13 = runtime.fromIterator(t_13);
var t_12 = t_13.length;
for(var t_11=0; t_11 < t_13.length; t_11++) {
var t_14 = t_13[t_11];
frame.set("group", t_14);
frame.set("loop.index", t_11 + 1);
frame.set("loop.index0", t_11);
frame.set("loop.revindex", t_12 - t_11);
frame.set("loop.revindex0", t_12 - t_11 - 1);
frame.set("loop.first", t_11 === 0);
frame.set("loop.last", t_11 === t_12 - 1);
frame.set("loop.length", t_12);
output += "\n    ";
if(runtime.memberLookup((runtime.memberLookup((t_14),"items")),"length") > 0) {
output += "       \n      ";
frame = frame.push();
var t_17 = runtime.memberLookup((t_14),"items");
if(t_17) {t_17 = runtime.fromIterator(t_17);
var t_16 = t_17.length;
for(var t_15=0; t_15 < t_17.length; t_15++) {
var t_18 = t_17[t_15];
frame.set("item", t_18);
frame.set("loop.index", t_15 + 1);
frame.set("loop.index0", t_15);
frame.set("loop.revindex", t_16 - t_15);
frame.set("loop.revindex0", t_16 - t_15 - 1);
frame.set("loop.first", t_15 === 0);
frame.set("loop.last", t_15 === t_16 - 1);
frame.set("loop.length", t_16);
output += "\n        ";
if(runtime.memberLookup((t_18),"_add")) {
output += "       \n          <li class=\"eto-results__option\" data-text=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, runtime.memberLookup((t_18),"_text")), env.opts.autoescape);
output += "\" data-index=\"";
output += runtime.suppressValue(runtime.memberLookup((t_18),"_index"), env.opts.autoescape);
output += "\" data-value=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, runtime.contextOrFrameLookup(context, frame, "dataValue")), env.opts.autoescape);
output += "\" ";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "resultsId")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "resultsId") + "-" + runtime.memberLookup((t_18),"_index") + "\"":""))), env.opts.autoescape);
output += " >\n            <span class=\"eto-btn--link\"><i translate=\"no\" class=\"notranslate md-icon add_term\">playlist_add</i> Add</span>\n            <b>";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_18),"text")), env.opts.autoescape);
output += "</b>\n          </li>\n        ";
;
}
output += "\n      ";
;
}
}
frame = frame.pop();
output += "\n    ";
;
}
output += "\n    ";
;
}
}
frame = frame.pop();
output += "\n  </ul>\n  ";
;
}
output += "\n  <table ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "attrs"))), env.opts.autoescape);
output += " class=\"eto-results__scroll_table eto-table";
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "className")?" " + runtime.contextOrFrameLookup(context, frame, "className"):"")), env.opts.autoescape);
output += "\">\n    <colgroup>\n      ";
frame = frame.push();
var t_21 = runtime.contextOrFrameLookup(context, frame, "resultColumns");
if(t_21) {t_21 = runtime.fromIterator(t_21);
var t_20 = t_21.length;
for(var t_19=0; t_19 < t_21.length; t_19++) {
var t_22 = t_21[t_19];
frame.set("c", t_22);
frame.set("loop.index", t_19 + 1);
frame.set("loop.index0", t_19);
frame.set("loop.revindex", t_20 - t_19);
frame.set("loop.revindex0", t_20 - t_19 - 1);
frame.set("loop.first", t_19 === 0);
frame.set("loop.last", t_19 === t_20 - 1);
frame.set("loop.length", t_20);
output += "\n        <col/>\n      ";
;
}
}
frame = frame.pop();
output += "\n    </colgroup>\n    <thead>\n      <tr class=\"eto-result-table-row\">\n        <th></th>\n        ";
frame = frame.push();
var t_25 = runtime.contextOrFrameLookup(context, frame, "resultColumns");
if(t_25) {t_25 = runtime.fromIterator(t_25);
var t_24 = t_25.length;
for(var t_23=0; t_23 < t_25.length; t_23++) {
var t_26 = t_25[t_23];
frame.set("c", t_26);
frame.set("loop.index", t_23 + 1);
frame.set("loop.index0", t_23);
frame.set("loop.revindex", t_24 - t_23);
frame.set("loop.revindex0", t_24 - t_23 - 1);
frame.set("loop.first", t_23 === 0);
frame.set("loop.last", t_23 === t_24 - 1);
frame.set("loop.length", t_24);
output += "\n          ";
if(runtime.memberLookup((t_26),"sortDir")) {
output += "\n            ";
if(runtime.memberLookup((t_26),"sortDir") === "asc") {
var t_27;
t_27 = "eto-table-column--sortable eto-table-column--asc";
frame.set("thClass", t_27, true);
if(frame.topLevel) {
context.setVariable("thClass", t_27);
}
if(frame.topLevel) {
context.addExport("thClass", t_27);
}
;
}
else {
var t_28;
t_28 = "eto-table-column--sortable eto-table-column--desc";
frame.set("thClass", t_28, true);
if(frame.topLevel) {
context.setVariable("thClass", t_28);
}
if(frame.topLevel) {
context.addExport("thClass", t_28);
}
;
}
output += "\n            <th class=\"";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "thClass"), env.opts.autoescape);
output += "\">\n              <div class=\"eto-table-column__label\" style=\"cursor: auto;\">\n                ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_26),"text")), env.opts.autoescape);
output += "\n              </div>\n            </th>  \n          ";
;
}
else {
output += "\n            <th>";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_26),"text")), env.opts.autoescape);
output += "</th>\n          ";
;
}
output += "\n        ";
;
}
}
frame = frame.pop();
output += "\n        <th></th>\n      </tr>\n    </thead>\n    <tbody>\n      ";
frame = frame.push();
var t_31 = runtime.contextOrFrameLookup(context, frame, "groupItem");
if(t_31) {t_31 = runtime.fromIterator(t_31);
var t_30 = t_31.length;
for(var t_29=0; t_29 < t_31.length; t_29++) {
var t_32 = t_31[t_29];
frame.set("group", t_32);
frame.set("loop.index", t_29 + 1);
frame.set("loop.index0", t_29);
frame.set("loop.revindex", t_30 - t_29);
frame.set("loop.revindex0", t_30 - t_29 - 1);
frame.set("loop.first", t_29 === 0);
frame.set("loop.last", t_29 === t_30 - 1);
frame.set("loop.length", t_30);
output += "\n        ";
if(runtime.memberLookup((runtime.memberLookup((t_32),"items")),"length") > 0) {
output += "       \n          ";
frame = frame.push();
var t_35 = runtime.memberLookup((t_32),"items");
if(t_35) {t_35 = runtime.fromIterator(t_35);
var t_34 = t_35.length;
for(var t_33=0; t_33 < t_35.length; t_33++) {
var t_36 = t_35[t_33];
frame.set("item", t_36);
frame.set("loop.index", t_33 + 1);
frame.set("loop.index0", t_33);
frame.set("loop.revindex", t_34 - t_33);
frame.set("loop.revindex0", t_34 - t_33 - 1);
frame.set("loop.first", t_33 === 0);
frame.set("loop.last", t_33 === t_34 - 1);
frame.set("loop.length", t_34);
output += "\n            ";
if(runtime.contextOrFrameLookup(context, frame, "isSelected") || !runtime.memberLookup((t_36),"_add")) {
var t_37;
t_37 = runtime.memberLookup((t_36),"_text");
frame.set("dataValue", t_37, true);
if(frame.topLevel) {
context.setVariable("dataValue", t_37);
}
if(frame.topLevel) {
context.addExport("dataValue", t_37);
}
if(runtime.memberLookup((t_36),"data")) {
output += "\n                ";
if(runtime.memberLookup((runtime.memberLookup((t_36),"data")),"value")) {
var t_38;
t_38 = runtime.memberLookup((runtime.memberLookup((t_36),"data")),"value");
frame.set("dataValue", t_38, true);
if(frame.topLevel) {
context.setVariable("dataValue", t_38);
}
if(frame.topLevel) {
context.addExport("dataValue", t_38);
}
;
}
else {
var t_39;
t_39 = runtime.memberLookup((t_36),"data");
frame.set("dataValue", t_39, true);
if(frame.topLevel) {
context.setVariable("dataValue", t_39);
}
if(frame.topLevel) {
context.addExport("dataValue", t_39);
}
;
}
output += "\n              ";
;
}
output += "\n              ";
if(runtime.memberLookup((t_36),"hide") && runtime.contextOrFrameLookup(context, frame, "hideRequired")) {
var t_40;
t_40 = "eto-results__option-hide";
frame.set("hide", t_40, true);
if(frame.topLevel) {
context.setVariable("hide", t_40);
}
if(frame.topLevel) {
context.addExport("hide", t_40);
}
;
}
else {
var t_41;
t_41 = "";
frame.set("hide", t_41, true);
if(frame.topLevel) {
context.setVariable("hide", t_41);
}
if(frame.topLevel) {
context.addExport("hide", t_41);
}
;
}
var t_42;
t_42 = ((runtime.contextOrFrameLookup(context, frame, "isSelected")?"eto-results__selected-option eto-results-selected-option-color":"eto-results__option"));
frame.set("itemClass", t_42, true);
if(frame.topLevel) {
context.setVariable("itemClass", t_42);
}
if(frame.topLevel) {
context.addExport("itemClass", t_42);
}
output += "<tr class=\"";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "itemClass"), env.opts.autoescape);
output += " eto-result-table-row\" data-text=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, runtime.memberLookup((t_36),"_text")), env.opts.autoescape);
output += "\" data-index=\"";
output += runtime.suppressValue(runtime.memberLookup((t_36),"_index"), env.opts.autoescape);
output += "\" data-value=\"";
output += runtime.suppressValue(env.getFilter("escape").call(context, runtime.contextOrFrameLookup(context, frame, "dataValue")), env.opts.autoescape);
output += "\" ";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "resultsId")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "resultsId") + "-" + runtime.memberLookup((t_36),"_index") + "\"":""))), env.opts.autoescape);
output += " style=\"display: table-row;\">\n                <td>\n                  <label class=\"eto-checkbox\">\n                    <input class=\"eto-checkbox__field eto-row-indicator\" type=\"checkbox\" ";
output += runtime.suppressValue((runtime.contextOrFrameLookup(context, frame, "isSelected") || runtime.memberLookup((t_36),"selected")?" checked":""), env.opts.autoescape);
output += ">\n                    <span class=\"eto-checkbox__box\"></span>\n                </label>\n                </td>\n                ";
frame = frame.push();
var t_45 = runtime.memberLookup((t_36),"columnData");
if(t_45) {t_45 = runtime.fromIterator(t_45);
var t_44 = t_45.length;
for(var t_43=0; t_43 < t_45.length; t_43++) {
var t_46 = t_45[t_43];
frame.set("c", t_46);
frame.set("loop.index", t_43 + 1);
frame.set("loop.index0", t_43);
frame.set("loop.revindex", t_44 - t_43);
frame.set("loop.revindex0", t_44 - t_43 - 1);
frame.set("loop.first", t_43 === 0);
frame.set("loop.last", t_43 === t_44 - 1);
frame.set("loop.length", t_44);
output += "\n                  <td class=\"text\">\n                    ";
if(runtime.contextOrFrameLookup(context, frame, "isSelected") && runtime.memberLookup((t_46),"_text")) {
output += "\n                      ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_46),"_text")), env.opts.autoescape);
output += "\n                    ";
;
}
else {
output += "\n                      ";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_46),"text")), env.opts.autoescape);
output += "\n                    ";
;
}
output += "\n                  </td>\n                ";
;
}
}
frame = frame.pop();
output += "\n                <td class=\"text-align-right\">\n                  ";
if(runtime.memberLookup((t_36),"_add") && runtime.contextOrFrameLookup(context, frame, "isSelected")) {
output += "       \n                    <span translate=\"no\" class=\"notranslate md-icon add_term\">fiber_new</span>\n                  ";
;
}
output += "\n                </td>\n              </tr>\n            ";
;
}
output += " \n          ";
;
}
}
frame = frame.pop();
output += "\n        ";
;
}
output += "  \n        ";
if(runtime.memberLookup((t_32),"hasMoreRecords")) {
output += "\n          <tr class=\"eto-result-table-row\" ";
output += runtime.suppressValue(env.getFilter("safe").call(context, (" id=\"" + runtime.contextOrFrameLookup(context, frame, "resultsId") + "-hasMoreRecords\"")), env.opts.autoescape);
output += " style=\"display: table-row;\">\n            <td class=\"eto-results__hasMoreRecords\" colspan=\"";
output += runtime.suppressValue(runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "resultColumns")),"length") + 2, env.opts.autoescape);
output += "\">";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((t_32),"title")), env.opts.autoescape);
output += "</td>\n          </tr>\n       ";
;
}
output += "\n      ";
;
}
}
frame = frame.pop();
output += "\n    </tbody>\n  </table>\n</div>\n    ";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/result-complex-table.js'], require);

},{"../../shim":9,"nunjucks":314}],14:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
require('./result-complex-table.js');
require('./result-complex-item.js');
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/results-complex.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n<div class=\"eto-results\" ";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "resultsId")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "resultsId") + "\"":""))), env.opts.autoescape);
output += " onselectstart=\"return false;\">\n  <div class=\"eto-tabs\"></div>\n  <div class=\"eto-results-available eto-tab-content__item\" role=\"listbox\">";
var t_1;
t_1 = true;
frame.set("hideRequired", t_1, true);
if(frame.topLevel) {
context.setVariable("hideRequired", t_1);
}
if(frame.topLevel) {
context.addExport("hideRequired", t_1);
}
output += "<div class=\"eto-results__select-all\" role=\"presentation\"><a href=\"javascript:void(0)\" tabindex=\"0\">Select All</a></div>\n    ";
if(runtime.contextOrFrameLookup(context, frame, "loadMoreOptionsOnScrollMessage") && runtime.contextOrFrameLookup(context, frame, "availableLength") > 0) {
output += "\n    <table class=\"eto-results__scroll_table eto-table eto-results__autocomplete-message\">\n      <tbody>\n        <tr class=\"eto-result-table-row\" style=\"display: table-row;\">\n          <td class=\"eto-results__hasMoreRecords\" colspan=\"5\">";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.contextOrFrameLookup(context, frame, "loadMoreOptionsOnScrollMessage")), env.opts.autoescape);
output += " </td>\n        </tr>\n      </tbody>\n    </table>\n    ";
;
}
var t_2;
t_2 = runtime.contextOrFrameLookup(context, frame, "content");
frame.set("groupItem", t_2, true);
if(frame.topLevel) {
context.setVariable("groupItem", t_2);
}
if(frame.topLevel) {
context.addExport("groupItem", t_2);
}
var t_3;
t_3 = false;
frame.set("isSelected", t_3, true);
if(frame.topLevel) {
context.setVariable("isSelected", t_3);
}
if(frame.topLevel) {
context.addExport("isSelected", t_3);
}
var t_4;
t_4 = true;
frame.set("hideRequired", t_4, true);
if(frame.topLevel) {
context.setVariable("hideRequired", t_4);
}
if(frame.topLevel) {
context.addExport("hideRequired", t_4);
}
if(runtime.contextOrFrameLookup(context, frame, "resultColumns") && runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "resultColumns")),"length") > 0) {
var tasks = [];
tasks.push(
function(callback) {
env.getTemplate("./result-complex-table.js", false, "C:/unity/html/templates/results-complex.js", false, function(t_6,t_5) {
if(t_6) { cb(t_6); return; }
callback(null,t_5);});
});
tasks.push(
function(template, callback){
template.render(context.getVariables(), frame, function(t_8,t_7) {
if(t_8) { cb(t_8); return; }
callback(null,t_7);});
});
tasks.push(
function(result, callback){
output += result;
callback(null);
});
env.waterfall(tasks, function(){
});
}
else {
output += "\n    ";
var t_9;
t_9 = runtime.contextOrFrameLookup(context, frame, "isCopyToClipboardRequired");
frame.set("isCopyToClipboardRequired", t_9, true);
if(frame.topLevel) {
context.setVariable("isCopyToClipboardRequired", t_9);
}
if(frame.topLevel) {
context.addExport("isCopyToClipboardRequired", t_9);
}
var tasks = [];
tasks.push(
function(callback) {
env.getTemplate("./result-complex-item.js", false, "C:/unity/html/templates/results-complex.js", false, function(t_11,t_10) {
if(t_11) { cb(t_11); return; }
callback(null,t_10);});
});
tasks.push(
function(template, callback){
template.render(context.getVariables(), frame, function(t_13,t_12) {
if(t_13) { cb(t_13); return; }
callback(null,t_12);});
});
tasks.push(
function(result, callback){
output += result;
callback(null);
});
env.waterfall(tasks, function(){
});
}
output += "\n  </div>";
var t_14;
t_14 = (runtime.contextOrFrameLookup(context, frame, "isCopyToClipboardRequired")?"with-copy-all":"");
frame.set("extraClass", t_14, true);
if(frame.topLevel) {
context.setVariable("extraClass", t_14);
}
if(frame.topLevel) {
context.addExport("extraClass", t_14);
}
output += "<div class=\"eto-results-selected eto-tab-content__item ";
output += runtime.suppressValue(runtime.contextOrFrameLookup(context, frame, "extraClass"), env.opts.autoescape);
output += "\" role=\"listbox\">";
var t_15;
t_15 = false;
frame.set("hideRequired", t_15, true);
if(frame.topLevel) {
context.setVariable("hideRequired", t_15);
}
if(frame.topLevel) {
context.addExport("hideRequired", t_15);
}
output += "<div class=\"eto-results__clear-all\" role=\"presentation\"><a href=\"javascript:void(0)\">Clear All</a></div>";
var t_16;
t_16 = runtime.contextOrFrameLookup(context, frame, "selected");
frame.set("groupItem", t_16, true);
if(frame.topLevel) {
context.setVariable("groupItem", t_16);
}
if(frame.topLevel) {
context.addExport("groupItem", t_16);
}
var t_17;
t_17 = true;
frame.set("isSelected", t_17, true);
if(frame.topLevel) {
context.setVariable("isSelected", t_17);
}
if(frame.topLevel) {
context.addExport("isSelected", t_17);
}
if(runtime.contextOrFrameLookup(context, frame, "resultColumns") && runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "resultColumns")),"length") > 0) {
var tasks = [];
tasks.push(
function(callback) {
env.getTemplate("./result-complex-table.js", false, "C:/unity/html/templates/results-complex.js", false, function(t_19,t_18) {
if(t_19) { cb(t_19); return; }
callback(null,t_18);});
});
tasks.push(
function(template, callback){
template.render(context.getVariables(), frame, function(t_21,t_20) {
if(t_21) { cb(t_21); return; }
callback(null,t_20);});
});
tasks.push(
function(result, callback){
output += result;
callback(null);
});
env.waterfall(tasks, function(){
});
}
else {
var t_22;
t_22 = runtime.contextOrFrameLookup(context, frame, "isEditable");
frame.set("isEditable", t_22, true);
if(frame.topLevel) {
context.setVariable("isEditable", t_22);
}
if(frame.topLevel) {
context.addExport("isEditable", t_22);
}
var t_23;
t_23 = runtime.contextOrFrameLookup(context, frame, "isCopyToClipboardRequired");
frame.set("isCopyToClipboardRequired", t_23, true);
if(frame.topLevel) {
context.setVariable("isCopyToClipboardRequired", t_23);
}
if(frame.topLevel) {
context.addExport("isCopyToClipboardRequired", t_23);
}
var tasks = [];
tasks.push(
function(callback) {
env.getTemplate("./result-complex-item.js", false, "C:/unity/html/templates/results-complex.js", false, function(t_25,t_24) {
if(t_25) { cb(t_25); return; }
callback(null,t_24);});
});
tasks.push(
function(template, callback){
template.render(context.getVariables(), frame, function(t_27,t_26) {
if(t_27) { cb(t_27); return; }
callback(null,t_26);});
});
tasks.push(
function(result, callback){
output += result;
callback(null);
});
env.waterfall(tasks, function(){
});
}
output += "\n    \n  </div>\n</div>\n";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/results-complex.js'], require);

},{"../../shim":9,"./result-complex-item.js":12,"./result-complex-table.js":13,"nunjucks":314}],15:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/tab.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n\n  <a ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "attrs"))), env.opts.autoescape);
output += " tabindex=\"0\" role=\"tab\" class=\"eto-tabs__tab";
output += runtime.suppressValue((runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"removeable")?" eto-tabs__tab--removeable":""), env.opts.autoescape);
output += runtime.suppressValue((runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"active")?" eto-tabs__tab--active":""), env.opts.autoescape);
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "className")?" " + runtime.contextOrFrameLookup(context, frame, "className"):"")), env.opts.autoescape);
output += "\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"selector")?" data-tab=\"" + runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"selector") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"href")?" href=\"" + runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"href") + "\"":""))), env.opts.autoescape);
output += "><span class=\"eto-tabs__tab-content\">";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.memberLookup((runtime.contextOrFrameLookup(context, frame, "item")),"content")), env.opts.autoescape);
output += "</span><span tabindex=\"0\" class=\"eto-tabs__tab-close\"></span></a>\n";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/tab.js'], require);

},{"../../shim":9,"nunjucks":314}],16:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
require('./tab.js');
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/tabs.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n\n<nav ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "attrs"))), env.opts.autoescape);
output += " class=\"eto-tabs";
output += runtime.suppressValue(((runtime.contextOrFrameLookup(context, frame, "className")?" " + runtime.contextOrFrameLookup(context, frame, "className"):"")), env.opts.autoescape);
output += "\" role=\"tablist\">\n  <div class=\"eto-tabs__container\">\n    <div class=\"eto-tabs__scroll\">\n      ";
frame = frame.push();
var t_3 = runtime.contextOrFrameLookup(context, frame, "items");
if(t_3) {t_3 = runtime.fromIterator(t_3);
var t_2 = t_3.length;
for(var t_1=0; t_1 < t_3.length; t_1++) {
var t_4 = t_3[t_1];
frame.set("item", t_4);
frame.set("loop.index", t_1 + 1);
frame.set("loop.index0", t_1);
frame.set("loop.revindex", t_2 - t_1);
frame.set("loop.revindex0", t_2 - t_1 - 1);
frame.set("loop.first", t_1 === 0);
frame.set("loop.last", t_1 === t_2 - 1);
frame.set("loop.length", t_2);
output += "\n        ";
var t_5;
t_5 = runtime.memberLookup((t_4),"attrs");
frame.set("attrs", t_5, true);
if(frame.topLevel) {
context.setVariable("attrs", t_5);
}
if(frame.topLevel) {
context.addExport("attrs", t_5);
}
output += "\n        ";
var t_6;
t_6 = runtime.memberLookup((t_4),"className");
frame.set("className", t_6, true);
if(frame.topLevel) {
context.setVariable("className", t_6);
}
if(frame.topLevel) {
context.addExport("className", t_6);
}
output += "\n        ";
var tasks = [];
tasks.push(
function(callback) {
env.getTemplate("./tab.js", false, "C:/unity/html/templates/tabs.js", false, function(t_8,t_7) {
if(t_8) { cb(t_8); return; }
callback(null,t_7);});
});
tasks.push(
function(template, callback){
template.render(context.getVariables(), frame, function(t_10,t_9) {
if(t_10) { cb(t_10); return; }
callback(null,t_9);});
});
tasks.push(
function(result, callback){
output += result;
callback(null);
});
env.waterfall(tasks, function(){
output += "\n      ";
});
}
}
frame = frame.pop();
output += "\n    </div>\n  </div>\n  <div class=\"eto-tabs__btns\" role=\"presentation\">\n    <a class=\"eto-tabs__btn eto-tabs__btn--backward\"></a>\n    <a class=\"eto-tabs__btn eto-tabs__btn--forward\"></a>\n  </div>\n</nav>\n";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/tabs.js'], require);

},{"../../shim":9,"./tab.js":15,"nunjucks":314}],17:[function(require,module,exports){
var nunjucks = require('nunjucks');
var env = nunjucks.env || new nunjucks.Environment();
(function () { (function() {(window.nunjucksPrecompiled = window.nunjucksPrecompiled || {})["C:/unity/html/templates/tooltip.js"] = (function() {
function root(env, context, frame, runtime, cb) {
var lineno = 0;
var colno = 0;
var output = "";
try {
var parentTemplate = null;
output += "\n<div ";
output += runtime.suppressValue(env.getFilter("safe").call(context, env.getFilter("attributes").call(context, runtime.contextOrFrameLookup(context, frame, "attrs"))), env.opts.autoescape);
output += " class=\"eto-tooltip";
output += runtime.suppressValue((runtime.contextOrFrameLookup(context, frame, "className")?" " + runtime.contextOrFrameLookup(context, frame, "className"):""), env.opts.autoescape);
output += "\"";
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "anchorX")?" data-anchor-x=\"" + runtime.contextOrFrameLookup(context, frame, "anchorX") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "anchorY")?" data-anchor-y=\"" + runtime.contextOrFrameLookup(context, frame, "anchorY") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "messageType")?" data-message-type=\"" + runtime.contextOrFrameLookup(context, frame, "messageType") + "\"":""))), env.opts.autoescape);
output += runtime.suppressValue(env.getFilter("safe").call(context, ((runtime.contextOrFrameLookup(context, frame, "id")?" id=\"" + runtime.contextOrFrameLookup(context, frame, "id") + "\"":""))), env.opts.autoescape);
output += ">\n  <div class=\"eto-tooltip__content\">";
output += runtime.suppressValue(env.getFilter("safe").call(context, runtime.contextOrFrameLookup(context, frame, "content")), env.opts.autoescape);
output += "</div>\n  <span class=\"eto-tooltip__caret\"></span>\n</div>\n";
if(parentTemplate) {
parentTemplate.rootRenderFunc(env, context, frame, runtime, cb);
} else {
cb(null, output);
}
;
} catch (e) {
  cb(runtime.handleError(e, lineno, colno));
}
}
return {
root: root
};

})();
})();
})();
module.exports = require('../../shim')(nunjucks, env, window.nunjucksPrecompiled['C:/unity/html/templates/tooltip.js'], require);

},{"../../shim":9,"nunjucks":314}],18:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _childElements = _interopRequireDefault(require("../utils/child-elements"));
var _derivedProps = _interopRequireDefault(require("../utils/derived-props"));
var _domBindings = _interopRequireDefault(require("../utils/dom-bindings"));
var _domEvents = _interopRequireDefault(require("../utils/dom-events"));
var _events = _interopRequireDefault(require("../utils/events"));
var _instanceCache = _interopRequireDefault(require("../utils/instance-cache"));
var _props = _interopRequireDefault(require("../utils/props"));
var _assign = _interopRequireDefault(require("lodash/assign"));
var _difference = _interopRequireDefault(require("lodash/difference"));
var _keys = _interopRequireDefault(require("lodash/keys"));
var _each = _interopRequireDefault(require("lodash/each"));
var _omit = _interopRequireDefault(require("lodash/omit"));
var _pick = _interopRequireDefault(require("lodash/pick"));
var _extend = _interopRequireDefault(require("lodash/extend"));
var _reject = _interopRequireDefault(require("lodash/reject"));
var _makeElement = _interopRequireDefault(require("../helpers/make-element"));
require("../helpers/nunjucks");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); } /**
 * # Base
 * The base component off which others should be extended. Implements
 * events, evented properties, computed properties, a DOM event listeners,
 * and DOM bindings.
 *
 * It should _not_ be instantiated directly, but rather be the basis for other
 * components.
 *
 * Emits and consumes events as defined {@link ../utils/events.js.html|here}.
 *
 * @example
 * class ChildComponent extends Base {
 *
 *   init(params = {}) {
 *     super.init(params);
 *     // ...
 *   }
 *
 *   _onItemClick(e) {
 *     // ...
 *   }
 * }
 *
 * // Read more {@link ../utils/props.js.html|here}
 * ChildComponent.prototype.props = {
 *   value: '',
 *   label: ''
 * };
 *
 * // A list of properties which CAN be passed to the constructor.
 * // Takes precedence over a blacklist.
 * ChildComponent.prototype.whitelist = ['value', 'label'];
 *
 * // A list of properties which CAN NOT be passed to the constructor.
 * ChildComponent.prototype.blacklist = [];
 *
 * // Read more {@link ../utils/dom-events.js.html|here}
 * ChildComponent.prototype.events = {
 *   'click .item': '_onItemClick'
 * };
 *
 * // Read more {@link ../utils/dom-bindings.js.html|here}
 * ChildComponent.prototype.bindings = {
 *   value: {
 *     type: 'booleanClass',
 *     name: 'has-value'
 *   }
 * };
 *
 * // Read more {@link ../utils/derived-props.js.html|here}
 * ChildComponent.prototype.derived = {
 *   valueLabel: {
 *     deps: ['value', 'label'],
 *     fn: function() {
 *       return this.value + '-' + this.label;
 *     }
 *   }
 * };
 *
 * @module components/base.js
 */
var noop = function noop() {};

/**
 * Base class methods.
 */
var Base = /*#__PURE__*/function () {
  /**
   * If an instance has already been instantiated on the element, return it.
   * Otherwise, creates a new instance.
   * @param {Object} params Parameters to store.
   */
  function Base() {
    var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    _classCallCheck(this, Base);
    // Is component already instantiated on element?
    var instance;
    if (params.el && (instance = _instanceCache["default"].get(this, params.el))) {
      if (console && console.debug) {
        console.debug("There is already an instance of the same type on this element. Using existing instance.");
      }
      return instance;
    }

    // Component-specific setup.
    this.init(params);

    // Add instance to instance cache.
    // @todo if (this.el) { ...
    _instanceCache["default"].set(this, this.el);
  }

  /**
   * Initialize an instance of the base component.
   * @param {Object} params Parameters to store.
   */
  return _createClass(Base, [{
    key: "init",
    value: function init() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      // Setup events.
      _events["default"].init(this);

      // Setup properties. Change events will be fired on
      // this component when property values change.
      _props["default"].init(this, this.props = this._getInitProps(params));

      // Setup derived properties.
      _derivedProps["default"].init(this);

      // Add child element querying and caching.
      _childElements["default"].init(this);

      // Ensure this component is backed by an element.
      this._initElement(params);

      // Init child components
      this._initChildren();

      // Listen for changes to the element to do some setup of nested components.
      this.listenTo(this, 'change:el', this._handleElementChange);

      // Cache any elements we might need
      (this._cacheElements || noop).call(this, params);
      this._hasBeenRemoved = false;
    }

    /**
     * Remove the component and clean up.
     * @param {Boolean} keepElement Optional Keep the element instead of removing it from its parent.
     */
  }, {
    key: "remove",
    value: function remove(keepElement) {
      try {
        if (this._hasBeenRemoved) return;
        this._removeChildren(keepElement);
        _instanceCache["default"]["delete"](this, this.el);
        if (this.el && this.el.parentNode && !keepElement) {
          this.el.parentNode.removeChild(this.el);
        }
        this.trigger('removed', this);
        this.stopListening();
        this.undelegateEvents();
        this.removeBindings();
        delete this._eventManager;
        delete this._parsedBindings;
        this._hasBeenRemoved = true;
      } catch (e) {
        console.warn({
          e: e
        });
      }
    }

    /**
     * Render the template. If there is a current element and it has
     * a parent, insert the new element into the DOM.
     * @param {Object} params Optional Properties to merge into the component props.
     */
  }, {
    key: "render",
    value: function render() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      var el = this.el;
      var parent = el && el.parentNode;
      if (this.template) {
        this.el = (0, _makeElement["default"])(this.template.render((0, _extend["default"])({}, this._props, params)));
        if (parent) {
          parent.replaceChild(this.el, el);
        }
      } else {
        this.el = document.createElement(this.tagName || 'div');
      }
    }

    /**
     * Update the component. Used to inform the component the DOM
     * has changed and we should reparse.
     * @todo should we support passing in newEl, oldEl?
     */
  }, {
    key: "update",
    value: function update() {
      this._handleElementChange();
    }

    /**
     * When the element changes, do any setup that is necessary.
     */
  }, {
    key: "_handleElementChange",
    value: function _handleElementChange(newEl, oldEl) {
      _instanceCache["default"].deleteInstance(this);
      _instanceCache["default"].set(this, this.el);
      this.delegateEvents();
      this.applyBindingsForKey();
      (this._cacheElements || noop).call(this);
      this._initChildren();
    }

    /**
     * Get the properties which should be initialized.
     * @param {Object} props
     * @return {Object}
     */
  }, {
    key: "_getInitProps",
    value: function _getInitProps(props) {
      // Whitelist properties
      if (this.whitelist && this.whitelist.length) {
        props = (0, _pick["default"])(props, this.whitelist);
      }
      // Blacklisted properties
      else if (this.blacklist && this.blacklist.length) {
        props = (0, _omit["default"])(props, this.blacklist);
      }
      return (0, _assign["default"])({}, this.props, props);
    }

    /**
     * Ensure that there is an element present on this component.
     * Then, init the DOM event listeners and the DOM bindings.
     * If we had an element to start with, reverse populate the
     * the DOM bindings with values from the element.
     *
     * Props that were declared in constructor params take precendence over
     * value from DOM, so we pass them.
     *
     * @param {Object} params
     */
  }, {
    key: "_initElement",
    value: function _initElement() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      var hasElement = this.el && !!this.el.innerHTML;

      // No element or empty element, so use a template.
      if (!hasElement && !this._canInitEmpty) {
        this.render(params);
      }
      var declaredProps = (0, _keys["default"])(params);

      // Initialize DOM event listeners.
      _domEvents["default"].init(this);

      // Only get prop values from DOM if not passed in constructor params.
      var whitelistProps;
      if (declaredProps && declaredProps.length && hasElement) {
        whitelistProps = (0, _difference["default"])(
        // ignore nested props
        (0, _reject["default"])((0, _keys["default"])(this.bindings), function (key) {
          return key.indexOf('.') !== -1;
        }),
        // ignore props declared in params
        declaredProps,
        // ignore computed props
        (0, _keys["default"])(this.derived || {}));
      }

      // Initialize DOM data bindings.
      _domBindings["default"].init(this, hasElement, whitelistProps);
    }

    /**
     * Initialize child components
     */
  }, {
    key: "_initChildren",
    value: function _initChildren(filter) {
      var _this = this;
      this._children = this._children || {};
      (0, _each["default"])(this.children, function (child, name) {
        if (filter && filter !== name) return;
        var el = child.selector ? _this.query(child.selector) : _this.el;

        // Don't init without an element because there is no reasonable way to attach
        // the child element to the parent.
        if (!el) return;

        // Update existing child component
        if (_this._children[name]) {
          _this._children[name].el = el;
          _this._children[name].update();
        }
        // Create new child component
        else {
          // Pass the data from the property with the same name.
          _this._children[name] = new child.cls((0, _assign["default"])({}, child.props, _this[name], {
            el: el
          }));

          // Update when our prop changes.
          _this.bindProxyObjectProps(_this._children[name], [name]);

          // Proxy events from the child
          if (child.events) {
            _this._children[name].bindProxyEvents(_this, child.events);
          }

          // Listen for events from the child and fire callbacks
          _this.bindComponentListeners(_this._children[name], child.on);
        }
      });
    }

    /**
     * Remove child components.
     * @param {Boolean} keepElement Optional Keep the element instead of removing it from its parent.
     */
  }, {
    key: "_removeChildren",
    value: function _removeChildren(keepElement) {
      var _this2 = this;
      if (!this._children) return;
      (0, _each["default"])(this.children, function (child, name) {
        if (!_this2._children[name]) return;
        _this2.unbindProxyObjectProps(_this2._children[name], [_this2[name]]);
        if (child.events) _this2._children[name].unbindProxyEvents(_this2, child.events);
        _this2._children[name].remove(keepElement);
      });
    }

    /**
     * Given a source object and an array of method names, bind proxy methods.
     * @param {object} obj
     * @param {array} methods
     */
  }, {
    key: "bindProxyMethods",
    value: function bindProxyMethods(obj, methods) {
      var _this3 = this;
      (0, _each["default"])(methods, function (key) {
        if (_this3[key] !== undefined) {
          throw new Error("Method `".concat(key, "` already exists on target."));
        }
        _this3[key] = obj[key].bind(obj);
      });
    }

    /**
     * Given an array of method names, unbind proxy methods.
     * @param {array} methods
     */
  }, {
    key: "unbindProxyMethods",
    value: function unbindProxyMethods(methods) {
      var _this4 = this;
      (0, _each["default"])(methods, function (key) {
        delete _this4[key];
      });
    }

    /**
     * Proxy property values from one object to another.
     * @param {Object} obj
     * @param {Array} props
     */
  }, {
    key: "bindProxyProps",
    value: function bindProxyProps(obj, props) {
      var _this5 = this;
      (0, _each["default"])(props, function (prop) {
        if (!_this5[prop] && obj[prop]) {
          _this5[prop] = obj[prop];
        }
        obj.listenTo(_this5, 'change:' + prop, function (newValue) {
          obj[prop] = newValue;
        });
      });
    }

    /**
     * Stop proxying property values from one object to another.
     * @param {Object} obj
     * @param {Array} props
     */
  }, {
    key: "unbindProxyProps",
    value: function unbindProxyProps(obj, props) {
      var _this6 = this;
      (0, _each["default"])(props, function (prop) {
        obj.stopListening(_this6, 'change:' + prop);
      });
    }

    /**
     * Bind a list of properties from one object to another. These properties
     * must be objects. Each property of each object will be set as its own
     * property on the second object.
     * Ex:
     * let obj = {filter: {name: 'test, length: 2, style}};
     * let obj2;
     * obj.bindProxyObjectProps(obj2, ['filter']);
     * obj2.name = 'test';
     * obj2.length = 2;
     * @param {Object} toObj
     * @param {Array} props
     */
  }, {
    key: "bindProxyObjectProps",
    value: function bindProxyObjectProps(toObj, props) {
      var _this7 = this;
      (0, _each["default"])(props, function (prop) {
        // Changes from this object are set on the child
        toObj.listenTo(_this7, "change:".concat(prop), function (val) {
          return toObj.set(val);
        });

        // Changes from the child object are pushed up to the parent.
        _this7.listenTo(toObj, "change", function (inst, changes) {
          (0, _each["default"])(changes, function (change) {
            if (_this7[prop] && _this7[prop].hasOwnProperty(change.name)) {
              var oldValue = _this7[prop][change.name];
              _this7[prop][change.name] = change.value;
              _this7.trigger("change:".concat(prop), _this7[prop]);
              _this7.trigger("change", [{
                value: change.value,
                name: change.name,
                oldValue: oldValue
              }]);
            } else if (_this7[prop] === null) {
              var obj = {};
              obj[change.name] = change.value;
              _this7[prop] = obj;
            }
          });
        });
      });
    }

    /**
     * Stop proxying the properties of one object through to another.
     * @param {Object} toObj
     * @param {Array} props
     */
  }, {
    key: "unbindProxyObjectProps",
    value: function unbindProxyObjectProps(toObj, props) {
      var _this8 = this;
      (0, _each["default"])(props, function (prop) {
        toObj.stopListening(_this8, "change:".concat(prop));
      });
    }

    /**
     * Proxy an event from one object through to another.
     * @param {Object} toObj
     * @param {Array} evts
     */
  }, {
    key: "bindProxyEvents",
    value: function bindProxyEvents(toObj, evts) {
      var _this9 = this;
      (0, _each["default"])(evts, function (evt) {
        toObj.listenTo(_this9, evt, function () {
          Array.prototype.unshift.call(arguments, evt);
          toObj.trigger.apply(toObj, arguments);
        });
      });
    }

    /**
     * Stop proxying an event from one object through to another.
     * @param {Object} toObj
     * @param {Array} evts
     */
  }, {
    key: "unbindProxyEvents",
    value: function unbindProxyEvents(toObj, evts) {
      var _this10 = this;
      (0, _each["default"])(evts, function (evt) {
        toObj.stopListening(_this10, evt);
      });
    }

    /**
     * Add event listeners to another component and run callbacks.
     * @param  {Object} child
     * @param  {Object} evts
     */
  }, {
    key: "bindComponentListeners",
    value: function bindComponentListeners(child, evts) {
      var _this11 = this;
      (0, _each["default"])(evts, function (cb, evt) {
        _this11.listenTo(child, evt, typeof cb === 'function' ? cb : _this11[cb]);
      });
    }

    /**
     * Remove event listeners on another component.
     * @param  {Object} child
     * @param  {Object} evts
     */
  }, {
    key: "unbindComponentListeners",
    value: function unbindComponentListeners(child, evts) {
      var _this12 = this;
      (0, _each["default"])(evts, function (cb, evt) {
        _this12.stopListening(child, evt, typeof cb === 'function' ? cb : _this12[cb]);
      });
    }
  }]);
}();
/**
 * The default properties for the base class.
 * @type {Object}
 */
Base.prototype.props = {
  el: null
};

/**
 * A whitelist of properties that can be set on construction.
 * @type {Array}
 */
Base.prototype.whitelist = [];

/**
 * A blacklist of properties that cannot be set on construction.
 * @type {Array}
 */
Base.prototype.blacklist = [];

/**
 * The default events for the base class.
 * @type {Object}
 */
Base.prototype.events = {};

/**
 * The default bindings for the base class.
 * @type {Object}
 */
Base.prototype.bindings = {};

/**
 * The default derived for the base class.
 * @type {Object}
 */
Base.prototype.derived = {};
var _default = exports["default"] = Base;
module.exports = exports.default;


},{"../helpers/make-element":29,"../helpers/nunjucks":32,"../utils/child-elements":39,"../utils/derived-props":40,"../utils/dom-bindings":41,"../utils/dom-events":42,"../utils/events":43,"../utils/instance-cache":45,"../utils/props":46,"lodash/assign":247,"lodash/difference":252,"lodash/each":253,"lodash/extend":255,"lodash/keys":283,"lodash/omit":291,"lodash/pick":295,"lodash/reject":299}],19:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _base = _interopRequireDefault(require("./base"));
var _popover = _interopRequireDefault(require("./popover"));
var _complexAutocomplete = _interopRequireDefault(require("../../../html/templates/precompiled/complex-autocomplete"));
var _resultsComplexMixin = _interopRequireDefault(require("../utils/results-complex-mixin"));
var _inputMessage = _interopRequireDefault(require("../utils/input-message"));
var _domManipulation = _interopRequireDefault(require("../helpers/dom-manipulation"));
var _getIds = _interopRequireWildcard(require("../helpers/get-ids"));
var _makeElement = _interopRequireDefault(require("../helpers/make-element"));
var _mixin = _interopRequireDefault(require("../helpers/mixin"));
var _windowEvents = _interopRequireDefault(require("../helpers/events/window-events"));
var _assign = _interopRequireDefault(require("lodash/assign"));
var _omitBy = _interopRequireDefault(require("lodash/omitBy"));
var _each = _interopRequireDefault(require("lodash/each"));
var _filter = _interopRequireDefault(require("lodash/filter"));
var _map = _interopRequireDefault(require("lodash/map"));
var _reverse = _interopRequireDefault(require("lodash/reverse"));
var _union = _interopRequireDefault(require("lodash/union"));
var _remove2 = _interopRequireDefault(require("lodash/remove"));
var _difference = _interopRequireDefault(require("lodash/difference"));
var _find2 = _interopRequireDefault(require("lodash/find"));
var _get2 = _interopRequireDefault(require("lodash/get"));
var _uniqBy = _interopRequireDefault(require("lodash/uniqBy"));
var _reduce = _interopRequireDefault(require("lodash/reduce"));
var _he = _interopRequireDefault(require("he"));
var _tooltip = _interopRequireDefault(require("./tooltip"));
function _getRequireWildcardCache(e) { if ("function" != typeof WeakMap) return null; var r = new WeakMap(), t = new WeakMap(); return (_getRequireWildcardCache = function _getRequireWildcardCache(e) { return e ? t : r; })(e); }
function _interopRequireWildcard(e, r) { if (!r && e && e.__esModule) return e; if (null === e || "object" != _typeof(e) && "function" != typeof e) return { "default": e }; var t = _getRequireWildcardCache(r); if (t && t.has(e)) return t.get(e); var n = { __proto__: null }, a = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var u in e) if ("default" !== u && {}.hasOwnProperty.call(e, u)) { var i = a ? Object.getOwnPropertyDescriptor(e, u) : null; i && (i.get || i.set) ? Object.defineProperty(n, u, i) : n[u] = e[u]; } return n["default"] = e, t && t.set(e, n), n; }
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function ownKeys(e, r) { var t = Object.keys(e); if (Object.getOwnPropertySymbols) { var o = Object.getOwnPropertySymbols(e); r && (o = o.filter(function (r) { return Object.getOwnPropertyDescriptor(e, r).enumerable; })), t.push.apply(t, o); } return t; }
function _objectSpread(e) { for (var r = 1; r < arguments.length; r++) { var t = null != arguments[r] ? arguments[r] : {}; r % 2 ? ownKeys(Object(t), !0).forEach(function (r) { _defineProperty(e, r, t[r]); }) : Object.getOwnPropertyDescriptors ? Object.defineProperties(e, Object.getOwnPropertyDescriptors(t)) : ownKeys(Object(t)).forEach(function (r) { Object.defineProperty(e, r, Object.getOwnPropertyDescriptor(t, r)); }); } return e; }
function _defineProperty(e, r, t) { return (r = _toPropertyKey(r)) in e ? Object.defineProperty(e, r, { value: t, enumerable: !0, configurable: !0, writable: !0 }) : e[r] = t, e; }
function _toConsumableArray(r) { return _arrayWithoutHoles(r) || _iterableToArray(r) || _unsupportedIterableToArray(r) || _nonIterableSpread(); }
function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }
function _unsupportedIterableToArray(r, a) { if (r) { if ("string" == typeof r) return _arrayLikeToArray(r, a); var t = {}.toString.call(r).slice(8, -1); return "Object" === t && r.constructor && (t = r.constructor.name), "Map" === t || "Set" === t ? Array.from(r) : "Arguments" === t || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(t) ? _arrayLikeToArray(r, a) : void 0; } }
function _iterableToArray(r) { if ("undefined" != typeof Symbol && null != r[Symbol.iterator] || null != r["@@iterator"]) return Array.from(r); }
function _arrayWithoutHoles(r) { if (Array.isArray(r)) return _arrayLikeToArray(r); }
function _arrayLikeToArray(r, a) { (null == a || a > r.length) && (a = r.length); for (var e = 0, n = Array(a); e < a; e++) n[e] = r[e]; return n; }
function _callSuper(t, o, e) { return o = _getPrototypeOf(o), _possibleConstructorReturn(t, _isNativeReflectConstruct() ? Reflect.construct(o, e || [], _getPrototypeOf(t).constructor) : o.apply(t, e)); }
function _possibleConstructorReturn(t, e) { if (e && ("object" == _typeof(e) || "function" == typeof e)) return e; if (void 0 !== e) throw new TypeError("Derived constructors may only return object or undefined"); return _assertThisInitialized(t); }
function _assertThisInitialized(e) { if (void 0 === e) throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); return e; }
function _isNativeReflectConstruct() { try { var t = !Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); } catch (t) {} return (_isNativeReflectConstruct = function _isNativeReflectConstruct() { return !!t; })(); }
function _superPropGet(t, e, o, r) { var p = _get(_getPrototypeOf(1 & r ? t.prototype : t), e, o); return 2 & r && "function" == typeof p ? function (t) { return p.apply(o, t); } : p; }
function _get() { return _get = "undefined" != typeof Reflect && Reflect.get ? Reflect.get.bind() : function (e, t, r) { var p = _superPropBase(e, t); if (p) { var n = Object.getOwnPropertyDescriptor(p, t); return n.get ? n.get.call(arguments.length < 3 ? e : r) : n.value; } }, _get.apply(null, arguments); }
function _superPropBase(t, o) { for (; !{}.hasOwnProperty.call(t, o) && null !== (t = _getPrototypeOf(t));); return t; }
function _getPrototypeOf(t) { return _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf.bind() : function (t) { return t.__proto__ || Object.getPrototypeOf(t); }, _getPrototypeOf(t); }
function _inherits(t, e) { if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function"); t.prototype = Object.create(e && e.prototype, { constructor: { value: t, writable: !0, configurable: !0 } }), Object.defineProperty(t, "prototype", { writable: !1 }), e && _setPrototypeOf(t, e); }
function _setPrototypeOf(t, e) { return _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function (t, e) { return t.__proto__ = e, t; }, _setPrototypeOf(t, e); }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); } /**
 * # Complex Autocomplete
 * A component that tracks the state of a autocomplete and responds to interactions.
 *
 * @example
 * new ComplexAutocomplete({
 *   el: ...,
 *   value: ...,
 *   label: ...,
 *   disabled: true|false
 *   onSelect: function(datum) { return false; // rejects the selection }
 *   onDeselect: function(datum) { return false; //rejects deselection }
 * });
 *
 * @module components/complete-autocomplete.js
 */
var specialKeyCodeMap = {
  8: "backspace",
  9: "tab",
  16: "shift",
  18: "alt",
  27: "esc",
  37: "left",
  39: "right",
  13: "enter",
  38: "up",
  40: "down"
};
var delimiterMap = {
  "comma": ",",
  "semicolon": ";"
};
var excludeMap = {
  "minus": "-",
  "exclamation": "!"
};

// For Lazyload
var MAX_PAGE_SIZE = 100;
var DEFAULT_PAGE_SIZE = 20;
var DEFAULT_LOADMOREOPTOINS_SCROLL_MESSAGE = "SHOWING TOP RESULTS. KEEP TYPING TO REFINE YOUR SEARCH OR SCROLL.";

/**
 * Simple component cache.
 */
var ComponentCache = /*#__PURE__*/function () {
  function ComponentCache() {
    _classCallCheck(this, ComponentCache);
    this.cache = new WeakMap();
  }

  /**
   * Stores an instance of sub components.
   * @param {Object} component
   * @param {Function} Ctor The class of the component.
   */
  return _createClass(ComponentCache, [{
    key: "store",
    value: function store(component, Ctor) {
      if (!this.cache.get(Ctor)) this.cache.set(Ctor, []);
      this.cache.get(Ctor).push(component);
    }

    /**
     * Find a component.
     * @param {Function} Ctor
     * @param {Function} cb
     */
  }, {
    key: "find",
    value: function find(Ctor, cb) {
      if (!this.cache.get(Ctor)) return;
      return (0, _find2["default"])(this.cache.get(Ctor), cb);
    }

    /**
     * Get components of a type.
     * @param {Function} Ctor
     */
  }, {
    key: "get",
    value: function get(Ctor) {
      return this.cache.get(Ctor);
    }

    /**
     * Remove a component.
     * @param {Object} component
     * @param {Function} Ctor The class of the component.
     */
  }, {
    key: "remove",
    value: function remove(component, Ctor) {
      if (!this.cache.get(Ctor)) this.cache.set(Ctor, []);
      (0, _remove2["default"])(this.cache.get(Ctor), component);
    }

    /**
     * Removes all components that are not a descendent of the passed element.
     * @param {Element} el - root
     */
  }, {
    key: "prune",
    value: function prune(el) {
      (0, _each["default"])(this.cache, function (g) {
        var keep = [];
        (0, _each["default"])(g, function (c) {
          if (c.el) {
            if (el.contains(c.el)) {
              keep.push(c);
            } else {
              c.remove();
            }
          }
        });
        g = keep;
      });
    }
  }]);
}();
/**
 * Complex Autocomplete class methods.
 * @extends Base
 */
var ComplexAutocomplete = /*#__PURE__*/function (_Base) {
  function ComplexAutocomplete() {
    _classCallCheck(this, ComplexAutocomplete);
    return _callSuper(this, ComplexAutocomplete, arguments);
  }
  _inherits(ComplexAutocomplete, _Base);
  return _createClass(ComplexAutocomplete, [{
    key: "init",
    value:
    /**
     * Preprocess params before super.
     * @param {Object} params Parameters to store.
     */
    function init() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      this._cc = new ComponentCache();
      _superPropGet(ComplexAutocomplete, "init", this, 3)([preprocess(params)]);
      this.searchQuery = '';
      this.loadMoreOptionsOnScroll = params.loadMoreOptionsOnScroll;
      if (params.options) {
        this.options = params.options;
        this.originalOptions = params.originalOptions;
        this.invalidOptions = params.invalidOptions;
        this.matchedResultsAsStrings = [];
        this.showSelectedOptionsInAvailable = params.showSelectedOptionsInAvailable || false;
        this._reGenerateTags();
      }
      this._addWindowListeners();
      this._addEventListeners();
      this.initialCollection = [];
      if (this.loadMoreOptionsOnScroll) {
        this.matchedResults = []; // accumulator for all results
        this.page = 0;
        this.selectedTabCurrentPage = 0;
        this.selectedTabNewScrollPosition = 0;
        this.lastAvailableTabScrollPosition = 0;
        this.lastSelectedTabScrollPosition = 0;
        this.newScrollPosition = 0;
        this.hiddenOptions = [];
        this.loadMoreOptionsOnScrollMessage = this.loadMoreOptionsOnScroll && (params.loadMoreOptionsOnScrollMessage || DEFAULT_LOADMOREOPTOINS_SCROLL_MESSAGE);
        if (this.pageSize <= MAX_PAGE_SIZE && this.pageSize < DEFAULT_PAGE_SIZE) {
          this.pageSize = DEFAULT_PAGE_SIZE;
        }
        this.processedSelectedItemsByPage = params.options.slice(0);
        this.isSelectedTabHasScrollEvent = false;
      }
      if (this.supportHtmlAsOption) {
        this.highlight = false;
      }
    }

    /**
     * Remove sub components.
     * @param {Boolean} keepElement
     */
  }, {
    key: "remove",
    value: function remove(keepElement) {
      this._removeWindowListeners();
      this._removeEventListeners();
      this._removeTagsPopover();
      this._inputEl = null;
      this._selectEl = null;
      this._resultsEl = null;
      this._anchorEl = null;
      this._resultsAvailableEl = null;
      this._resultsSelectedEl = null;
      this._tagsEl = null;
      this._selectAllEl = null;
      this._clearAllEl = null;
      this._unbindSoftScroll();
      _superPropGet(ComplexAutocomplete, "remove", this, 3)([keepElement]);
    }

    /**
     * Enables the component.
     */
  }, {
    key: "enable",
    value: function enable() {
      this.disabled = false;
    }

    /**
     * Disables the component.
     */
  }, {
    key: "disable",
    value: function disable() {
      this.disabled = true;
    }

    /**
     * Gets current value.
     * @return {string|array}
     */
  }, {
    key: "getValue",
    value: function getValue() {
      if (this.loadMoreOptionsOnScroll && this.selectAllActive) {
        return uniq([].concat(_toConsumableArray(this.value), _toConsumableArray(this.matchedResultsAsStrings)));
      } else {
        return this.value;
      }
    }

    /**
     * Sets current value.
     * @param {number|string|array} value
     */
  }, {
    key: "setValue",
    value: function setValue(value, removeAll) {
      var _this = this;
      var originalValue = value;
      if (_typeof(value) !== undefined) {
        var collection = this.options.slice(0);
        if (typeof value === 'string') {
          value = [value];
        }
        if (Array.isArray(value)) {
          // Remove duplicates
          if (value.length > 0 && _typeof(value[0]) === 'object') {
            value = value.map(function (v) {
              var c = value;
              if (!_this.supportHtmlAsOption) {
                c = _he["default"].decode(v.value);
              }
              return c;
            });
          }
          value = uniq(value);
        }
        var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
        collection = addValuesToOptions(collection, value, this.add, this.exclude, this.noValue, this.excludeNoValue, this.options, isGridParent, {
          supportHtmlAsOption: this.supportHtmlAsOption
        });
        this.options = this.originalOptions = _toConsumableArray(updateOptions(value, collection, this.add));
        this.value = verifyValue(value);
        this._reGenerateTags(removeAll);
      }
    }

    /**
     * Clears value.
     */
  }, {
    key: "clearValue",
    value: function clearValue(e) {
      this.value = [];
    }
  }, {
    key: "clearInputValue",
    value: function clearInputValue(e) {
      this._inputEl.value = '';
      e.preventDefault();
      e.stopPropagation();
      this._inputEl.focus();
    }

    /**
     * Sets the content of the dropdown.
     * @param {array} collection
     * Accepts many formats...
     *
     * [ '', '', '' ]
     * [ {title, items: [ '', '', '' ] } ,... ]
     * [ {title, items: [ { text, meta, data } ] } ,... ]
     * [ {title, items: [ { text, meta, data, columnData } ] }, resultColumns:{text, sortDir, searchable}... ]
     */
  }, {
    key: "setContent",
    value: function setContent(initialCollection) {
      this.isInputChanged = true;
      this.selectAllActive = false;
      this.initialCollection = initialCollection.slice(0);
      if (typeof initialCollection[0] !== 'string') {
        var _initialCollection$, _initialCollection$$r;
        if (((_initialCollection$ = initialCollection[0]) === null || _initialCollection$ === void 0 ? void 0 : (_initialCollection$$r = _initialCollection$.resultColumns) === null || _initialCollection$$r === void 0 ? void 0 : _initialCollection$$r.length) > 0) {
          this.matchedResultsAsStrings = (0, _reduce["default"])(initialCollection, function (acc, group) {
            return [].concat(_toConsumableArray(acc), _toConsumableArray((0, _map["default"])(group.items, function (i) {
              var _i$data;
              if (typeof i === 'string') return i;else return _he["default"].decode(i === null || i === void 0 ? void 0 : (_i$data = i.data) === null || _i$data === void 0 ? void 0 : _i$data.value);
            })));
          }, []);
        } else {
          this.matchedResultsAsStrings = (0, _reduce["default"])(initialCollection, function (acc, group) {
            return [].concat(_toConsumableArray(acc), _toConsumableArray((0, _map["default"])(group.items, function (i) {
              if (typeof i === 'string') return i;else return _he["default"].decode(i.text);
            })));
          }, []);
        }
      } else {
        this.matchedResultsAsStrings = initialCollection.map(function (i) {
          return _he["default"].decode(i);
        });
      }
      if (this.searchQuery.length == 0) {
        this.page = 0;
      }
      this.matchedResults = initialCollection.slice(0);
      var subCollection = initialCollection.slice(0);
      if (this.loadMoreOptionsOnScroll) {
        if (typeof initialCollection[0] === 'string') {
          subCollection = initialCollection.slice(0, this.pageSize);
        } else {
          subCollection = this._getOptionsPerPageForGroupbyOptions({
            initialCollection: initialCollection,
            pageNumber: this.page,
            pageSize: this.pageSize
          });
        }
      }
      var collection = this._setContentForChunk(subCollection);
      this.options = collection.slice(0);
      if (this.value) {
        this.options = _toConsumableArray(updateOptions(this.value, this.options, this.add));
      }
      this.originalOptions = this.options.slice(0);
      this._resetOptions({
        isInputChanged: this.isInputChanged
      });
    }

    /**
     * Returns the available count
     * Available count includes selected values
     */
  }, {
    key: "_getTheAvailableCountWithSelected",
    value: function _getTheAvailableCountWithSelected(_ref) {
      var showSelectedOptionsInAvailable = _ref.showSelectedOptionsInAvailable;
      if (showSelectedOptionsInAvailable) {
        return this.matchedResultsAsStrings.length;
      } else {
        return uniq((0, _difference["default"])(this.matchedResultsAsStrings, this.value)).length;
      }
    }

    /**
    * Returns the selected options count
    */
  }, {
    key: "_getTheSelectedCount",
    value: function _getTheSelectedCount() {
      if (this.selectAllActive) {
        return uniq([].concat(_toConsumableArray(this.value), _toConsumableArray(this.matchedResultsAsStrings))).length;
      }
      return this._selected.length;
    }

    /**
     * For Lazyload approach
     * process chunk of all matched results
     * isAddValuesRequired - weather to add selected options to loaded options
     */
  }, {
    key: "_setContentForChunk",
    value: function _setContentForChunk(collection) {
      var _this2 = this;
      var isAddValuesRequired = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : true;
      var term = this._inputEl.value || '';
      term = term.trim();
      var termEmpty = term === '';
      var termMatchedRegex = new RegExp("^".concat(escapeRegexCharacters(term), "$"), 'i');
      var termMatched = false;
      var highlightRegex = new RegExp("\\b(".concat((0, _map["default"])(queryTokenizer(term), escapeRegexCharacters).join('|'), ")"), 'gi');
      var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;

      // leave if array wasn't passed
      if (!Array.isArray(collection)) {
        return;
      }

      // transform array of strings to single group.
      if (typeof collection[0] === 'string') {
        collection = [{
          items: collection
        }];
      }
      this.resultColumns = (collection[0] ? collection[0].resultColumns : null) || this.resultColumns;

      // iterate options
      // transform group with array of strings to objects
      (0, _each["default"])(collection, function (group) {
        if (group.items && typeof group.items[0] === 'string') {
          group.items = group.items.map(function (str) {
            var exclude = isExcludeTerm(str, _this2.exclude, excludeMap, isGridParent);
            var isNoValue = !!(_this2.noValue && (_this2.noValue === str || _this2.excludeNoValue === str));
            return {
              displayText: str,
              text: str,
              _text: str,
              data: str,
              exclude: exclude,
              noValue: isNoValue
            };
          });
        }
      });
      var processedCollection = [];
      var includeList = [];
      var excludeList = [];
      (0, _each["default"])(collection, function (group) {
        if (!group.title) {
          (0, _each["default"])(group.items, function (item) {
            item._text = (item.data ? item.data.value : false) || item._text;
            item.displayText = item.text || item._text;
            item.exclude = isExcludeTerm(item._text, _this2.exclude, excludeMap, isGridParent);
            item.noValue = !!(_this2.noValue && (_this2.noValue === item._text || _this2.excludeNoValue === item._text));
            if (item.exclude) excludeList.push(item);else includeList.push(item);
          });
        }
      });
      processedCollection.push({
        items: []
      }); //Add term
      processedCollection.push({
        items: excludeList
      }); //Exclude
      processedCollection.push({
        items: includeList
      }); //Include
      (0, _each["default"])(collection, function (group) {
        //Other groups
        if (group.title) {
          processedCollection.push(group);
        }
      });
      collection = processedCollection;
      var highlightIndex = [];
      if (this.resultColumns) {
        for (var i = 0; i < this.resultColumns.length; i++) {
          var col = this.resultColumns[i];
          if (col.searchable === true) {
            highlightIndex.push(i);
          }
        }
      }
      (0, _each["default"])(collection, function (group) {
        // if term is not empty, add and highlight
        if (!termEmpty) {
          (0, _each["default"])(group.items, function (datum) {
            datum._text = datum._text || (datum.data ? datum.data.value : false) || datum.text;
            if (!_this2.supportHtmlAsOption) {
              datum._text = _he["default"].decode(datum._text);
            } else {
              datum.decodedText = _he["default"].decode(datum._text);
            }
            _this2.add && termMatchedRegex.test(datum._text) && (termMatched = true);
            if (_this2.highlight) {
              if (highlightIndex.length > 0) {
                (0, _each["default"])(highlightIndex, function (index) {
                  // For Locations example: Selected tab doesn't need to show the highlight for matched text of option
                  datum.columnData[index]._text = datum.columnData[index].text;
                  datum.columnData[index].text = _this2._highlightMatch(highlightRegex, datum.columnData[index].text);
                });
              } else {
                // matched string gets highlighted here
                datum.text = _this2._highlightMatch(highlightRegex, datum.text);
              }
            } else {
              datum.text = datum.text || datum._text;
            }
          });
        }
      });
      if (this.add && !termMatched && !termEmpty) {
        if (!collection || collection.length === 0) collection = resetCollection();

        //Check if there is an unselected 'add' term item present already, replace if present
        collection = removeAddTerm(collection);
        var displayText = term;
        var decodedText = '';
        if (this.supportHtmlAsOption && term) {
          if (term !== _he["default"].decode(term)) {
            decodedText = _he["default"].decode(term);
          }
        }
        var termObj = [{
          _add: true,
          _text: term,
          displayText: term,
          text: displayText,
          data: term,
          decodedText: decodedText
        }];
        if (this.resultColumns && this.resultColumns.length > 0) {
          var columnData = this.resultColumns.map(function (c) {
            var text = '';
            if (c.searchable === true) {
              text = _this2.highlight ? "<b>".concat(term, "</b>") : "".concat(term);
            }
            return {
              text: text
            };
          });
          termObj[0]['columnData'] = columnData;
        }

        //Add the term to the collection
        collection[0].items.unshift(termObj[0]);
      }
      //Add any values not in collection to the collection so that it can be rendered in selected tab
      if (!collection || collection.length === 0) collection = resetCollection();
      if (isAddValuesRequired) {
        if (this.loadMoreOptionsOnScroll) {
          if (this.page === 0) {
            collection = addValuesToOptions(collection, this.value, this.add, this.exclude, this.noValue, this.excludeNoValue, this.options, isGridParent, {
              allMatchedResults: this.matchedResults,
              loadedOptions: this.originalOptions,
              supportHtmlAsOption: this.supportHtmlAsOption
            });
          }
        } else {
          collection = addValuesToOptions(collection, this.value, this.add, this.exclude, this.noValue, this.excludeNoValue, this.options, isGridParent, {
            supportHtmlAsOption: this.supportHtmlAsOption
          });
        }
      }
      return collection;
    }

    /**
     * Returns options per page if lazyload feature enabled
     * @param {{Array, Number, Number}} param0  
     * initialCollection - {Array}
     * pageNumber - {Number}
     * pageSize - {Number}
     * @returns 
     */
  }, {
    key: "_getOptionsPerPageForGroupbyOptions",
    value: function _getOptionsPerPageForGroupbyOptions(_ref2) {
      var initialCollection = _ref2.initialCollection,
        pageNumber = _ref2.pageNumber,
        pageSize = _ref2.pageSize;
      var groupIndex = 0;
      var startIndex = 0;
      var length = initialCollection.length;
      var collection = [];
      var currentPageSize = 0;
      if (pageNumber > 0) {
        groupIndex = this.nextGroupIndex;
        startIndex = this.nextItemIndex;
        for (var i = groupIndex; i < length; i++) {
          if (currentPageSize < pageSize) {
            var groupItemsLen = initialCollection[i].items.length;
            var endIndex = groupItemsLen - startIndex > pageSize - currentPageSize ? pageSize - currentPageSize + startIndex : groupItemsLen;
            var collectionSlice = _objectSpread(_objectSpread({}, initialCollection[i]), {}, {
              items: initialCollection[i].items.slice(startIndex, endIndex)
            });
            currentPageSize = currentPageSize + collectionSlice.items.length;
            collection = [].concat(_toConsumableArray(collection), [collectionSlice]);
            if (endIndex === groupItemsLen) {
              this.nextGroupIndex = i + 1;
              this.nextItemIndex = 0;
            } else {
              this.nextGroupIndex = i;
              this.nextItemIndex = endIndex;
            }
          }
          return collection;
        }
        return;
      }
      for (var _i = groupIndex; _i < length; _i++) {
        if (currentPageSize < pageSize) {
          var _groupItemsLen = initialCollection[_i].items.length;
          var _endIndex = _groupItemsLen > pageSize - currentPageSize ? pageSize - currentPageSize : _groupItemsLen;
          var _collectionSlice = _objectSpread(_objectSpread({}, initialCollection[_i]), {}, {
            items: initialCollection[_i].items.slice(startIndex, _endIndex)
          });
          currentPageSize = currentPageSize + _collectionSlice.items.length;
          collection = [].concat(_toConsumableArray(collection), [_collectionSlice]);
          this.nextGroupIndex = _i;
          this.nextItemIndex = _endIndex;
        } else {}
      }
      return collection;
    }

    /**
     * Cache elements when el changes.
     */
  }, {
    key: "_cacheElements",
    value: function _cacheElements() {
      this._inputEl = this.query('.eto-complex-autocomplete__field');
      this._selectEl = this.query('select');
      this._resultsEl = this.query('.eto-results');
      this._resultsAvailableEl = this.query('.eto-results-available');
      this._resultsSelectedEl = this.query('.eto-results-selected');
      this._resultsEl.style.display = 'none';
      this._anchorEl = this.query('.eto-complex-autocomplete__field-container');
      this._tagsEl = this.query('.eto-complex-autocomplete__inline-tags');
      this._selectAllEl = this.query('.eto-results__select-all');
      this._clearAllEl = this.query('.eto-results__clear-all');
      this.placeholder = this.placeholder || '';
    }

    /**
     * Add window event listeners.
     */
  }, {
    key: "_addWindowListeners",
    value: function _addWindowListeners() {
      this._winEvents = new _windowEvents["default"](this);
      this._winEvents.bind('keydown [data-tab=".eto-results-available"]', '_onResultsAvailableKeydown');
      this._winEvents.bind('keydown [data-tab=".eto-results-selected"]', '_onResultsSelectedKeydown');
      this._winEvents.bind('keydown .eto-results__select-all a', '_onSelectOrClearAllKeydown');
      this._winEvents.bind('keydown .eto-results__clear-all a', '_onSelectOrClearAllKeydown');
      this._winEvents.bind('keydown', '_onInputKeyDown');
      this._winEvents.bind('keyup', '_onInputKeyUp');
      this._winEvents.bind('click', '_onWindowClick');
      this._winEvents.bind('resize', 'resize');
      this._winEvents.bind('scroll', '_resizeHeight');
      this._winEvents.bind('DOMMouseScroll', '_resizeHeight');
      this._winEvents.bind('click .eto-results__option', '_onOptionClick');
      this._winEvents.bind('click .eto-results__selected-option', '_onSelectedOptionClick');
      this._winEvents.bind('click .eto-tabs__tab', '_resetOptionsOnTabClick');
      this._winEvents.bind('click .eto-results__select-all a', '_selectAllValues');
      this._winEvents.bind('click .eto-results__clear-all a', '_clearAllValues');
    }

    /**
     * Remove window event listeners.
     */
  }, {
    key: "_removeWindowListeners",
    value: function _removeWindowListeners() {
      if (this._winEvents) {
        this._winEvents.remove();
        this._winEvents = null;
      }
    }

    /**
     * Reset the result options to all options.
     */
  }, {
    key: "_resetOptions",
    value: function _resetOptions() {
      var _this3 = this,
        _this$_tabs2;
      var _ref3 = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
        _ref3$currentTabIndex = _ref3.currentTabIndex,
        currentTabIndex = _ref3$currentTabIndex === void 0 ? 0 : _ref3$currentTabIndex,
        isInputChanged = _ref3.isInputChanged,
        _ref3$onScroll = _ref3.onScroll,
        onScroll = _ref3$onScroll === void 0 ? false : _ref3$onScroll;
      // Use originalOptions so available results are always in the same order.
      var items = [];
      // Include selected items in search results.
      if (this.showSelectedOptionsInAvailable) {
        items = this._getAvailableOptionsWithSelected();
      } else {
        items = this.originalOptions.filter(function (c) {
          return _this3._selected.length > 0 ? !_this3._selected.find(function (d) {
            return c._text === d._text;
          }) : c;
        });
      }
      // Map selected to order of selection for selected in Results template.
      var processedSelectedItems = processSelectedOptions(this._selected, this.value, this.exclude);
      var finalProcessedSelectedItems = processedSelectedItems.slice(0) || [];
      if (this.loadMoreOptionsOnScroll && !isInputChanged && !this.selectAllActive) {
        var _this$_tabs;
        if (((_this$_tabs = this._tabs) === null || _this$_tabs === void 0 ? void 0 : _this$_tabs.getActive().getAttribute('data-tab')) == '.eto-results-selected') {
          if (this.selectedTabCurrentPage > 0) {
            this.processedSelectedItemsByPage = this._getOptionsPerPage(processedSelectedItems, 0, (this.selectedTabCurrentPage + 1) * this.pageSize);
          } else {
            this.processedSelectedItemsByPage = this._getOptionsPerPage(processedSelectedItems, 0, this.pageSize);
          }
          finalProcessedSelectedItems = this.processedSelectedItemsByPage.slice(0);
          this._renderSelectedResults(finalProcessedSelectedItems, items.length ? [{
            items: items
          }] : [], this.searchQuery.length > 0 ? this._getTheAvailableCountWithSelected({
            showSelectedOptionsInAvailable: this.showSelectedOptionsInAvailable
          }) : 0, this._getTheSelectedCount(), false, this.isEditable);
          if (this.loadMoreOptionsOnScroll) {
            this._bindScrollEventToSelectedTab();
          }
          if (onScroll) {
            var _this$_resultsEl$quer;
            (_this$_resultsEl$quer = this._resultsEl.querySelector('.eto-results__scroll')) === null || _this$_resultsEl$quer === void 0 ? void 0 : _this$_resultsEl$quer.scrollTo(0, this.selectedTabNewScrollPosition);
          }
          this.isInputChanged = false;
          return;
        }
      }
      var addIcon = ((_this$_tabs2 = this._tabs) === null || _this$_tabs2 === void 0 ? void 0 : _this$_tabs2.getActive().getAttribute('data-tab')) != '.eto-results-selected' ? 'add' : '';
      if (items && items.filter(function (c) {
        return c.items;
      }).length > 0) {
        if (!this.showSelectedOptionsInAvailable) {
          items = (0, _map["default"])(items, function (o) {
            return {
              title: o.title,
              items: o.items.filter(function (c) {
                return !c.selected;
              }),
              hasMoreRecords: o.hasMoreRecords
            };
          });
        }
        var availableCount;
        if (this.loadMoreOptionsOnScroll) {
          if (this.searchQuery.length > 0) {
            availableCount = this._getTheAvailableCountWithSelected({
              showSelectedOptionsInAvailable: this.showSelectedOptionsInAvailable
            });
          } else {
            availableCount = 0;
          }
        } else {
          if (this.showSelectedOptionsInAvailable) {
            availableCount = this._getTheAvailableCountWithSelected({
              showSelectedOptionsInAvailable: this.showSelectedOptionsInAvailable
            });
          } else {
            availableCount = getAvailableCount(this.options, this.showSelectedOptionsInAvailable);
          }
        }
        this._renderResults(items, processedSelectedItems, availableCount, this._selected.length, false, false, this.loadMoreOptionsOnScrollMessage, this.highlight, addIcon, this.supportHtmlAsOption);
      } else {
        items = (0, _map["default"])(items, function (o) {
          return (0, _assign["default"])({}, o);
        });
        this._renderResults(items.length ? [{
          items: items
        }] : [], processedSelectedItems, getAvailableCount(this.options), this._getTheSelectedCount(), false, false, this.loadMoreOptionsOnScrollMessage, this.highlight, addIcon, this.supportHtmlAsOption);
      }
      if (this._selected.length > 0) {
        this._tabs.enableTab(1);
        this._badges[1].setAttribute('data-type', 'info');
      } else {
        this._tabs.disableTab(1);
        this._badges[1].removeAttribute('data-type');
      }
      if (this.loadMoreOptionsOnScroll) {
        if (this._tabs && this._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
          var scrollEl = this._resultsEl.querySelector('.eto-results__scroll');
          if (scrollEl) {
            scrollEl.removeEventListener('scroll', function (e) {
              console.log('event listenere removed');
            });
            scrollEl.addEventListener('scroll', function (e) {
              if (_this3._tabs && _this3._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
                _this3._onResultsScroll(e);
              }
            }, {
              passive: true
            });
          }
        }
        if (onScroll) {
          this._resultsEl.querySelector('.eto-results__scroll').scrollTo(0, this.newScrollPosition);
        }
      }
      this.isInputChanged = false;
    }

    /**
     * 
     * @param {Array} collection - options
     * @param {Number} startPage 
     * @param {Number} pageSize 
     * @returns 
     */
  }, {
    key: "_getOptionsPerPage",
    value: function _getOptionsPerPage(collection, startPage, pageSize) {
      var optionsPerPage = [];
      var i,
        j,
        k = 0;
      var overallIndexCount = 0;
      var finalOptionIndex = startPage + pageSize;
      for (i = 0; i < collection.length; i++) {
        var subCollection = {
          items: []
        };
        for (j = 0; j < collection[i].items.length; j++) {
          if (overallIndexCount >= startPage && overallIndexCount < finalOptionIndex) {
            var item = collection[i].items[j];
            item._index = k++;
            subCollection.items.push(item);
          }
          overallIndexCount++;
        }
        optionsPerPage.push(subCollection);
      }
      return optionsPerPage;
    }

    /**
     * Reset options and set the correct tab once a different tab has been selected
     */
  }, {
    key: "_resetOptionsOnTabClick",
    value: function _resetOptionsOnTabClick(e) {
      this.selectedTabNewScrollPosition = 0;
      var tab = e.toElement ? e.toElement.closest('.eto-tabs__tab') : checkClosest(e.target, 'eto-tabs__tab');
      if (this._tabs) {
        var tabs = this._tabs.queryAll('.eto-tabs__tab');
        tabs = Array.from(tabs);
        var index = tabs.indexOf(tab);
        if (this._resultsEl.contains(e.target)) {
          this._inputEl.focus();
          this.page = 0;
          this.isOnScroll = false;
          this._resetOptions();
          this._tabs.setActive(index);
          if (index === 0) {
            var _this$_resultsEl$quer2;
            this._selectAllEl.style.display = 'inline';
            this._clearAllEl.style.display = 'none';
            this._inputEl.value = this.searchQuery;
            (_this$_resultsEl$quer2 = this._resultsEl.querySelector('.eto-results__scroll')) === null || _this$_resultsEl$quer2 === void 0 ? void 0 : _this$_resultsEl$quer2.scrollTo(0, this.lastAvailableTabScrollPosition);
          } else {
            var _this$_resultsEl$quer3;
            if (this.loadMoreOptionsOnScroll) {
              this._bindScrollEventToSelectedTab();
            }
            (_this$_resultsEl$quer3 = this._resultsEl.querySelector('.eto-results__scroll')) === null || _this$_resultsEl$quer3 === void 0 ? void 0 : _this$_resultsEl$quer3.scrollTo(0, this.lastSelectedTabScrollPosition);
          }
          this._resizeHeight();
          this._resetCursors();
        }
      }
    }

    /**
     * Attaching scroll event to select tab
     */
  }, {
    key: "_bindScrollEventToSelectedTab",
    value: function _bindScrollEventToSelectedTab() {
      var _this4 = this;
      this._selectAllEl.style.display = 'none';
      this._clearAllEl.style.display = 'inline';
      if (this.loadMoreOptionsOnScroll) {
        if (this._tabs.getActive().getAttribute('data-tab') == '.eto-results-selected') {
          var scrollEl = this._resultsEl.querySelector('.eto-results-selected .eto-results__scroll');
          var hasScroll = scrollEl && scrollEl.scrollHeight - scrollEl.clientHeight > 0;
          if (scrollEl && hasScroll) {
            scrollEl.removeEventListener('scroll', function (e) {
              this._onResultsScroll(e, 1);
            }, {
              passive: true
            });
            scrollEl.addEventListener('scroll', function (e) {
              _this4.isSelectedTabHasScrollEvent = true;
              var scrollPosition = Math.floor(e.target.scrollHeight * .9);
              if (scrollPosition > 0) {
                _this4.selectedTabNewScrollPosition = scrollPosition;
              }
              _this4._onResultsScroll(e, 1);
            }, {
              passive: true
            });
          }
        }
      }
    }

    /**
     * Select a value.
     * @param value
     */
  }, {
    key: "_selectValue",
    value: function _selectValue(value, multipleText) {
      if (!value || typeof value !== 'string') return;
      var delimiterChar = delimiterMap[this.delimiter];
      var escapedDelimiter = '\\' + delimiterChar;
      if (multipleText === true) {
        //If value has escaped delimiter or encoded delimiter, replace that with alternate delimiter.
        //Then split it on delimiter and replace alternate delimiter with escaped delimiter.
        var delimiterRegex = new RegExp("".concat(escapeRegexCharacters(delimiterChar)), 'g');
        var encodedDelimiterRegex = new RegExp("&#092;".concat(escapeRegexCharacters(delimiterChar)), 'g');
        var encodedDelimiterChar = "&#092;" + delimiterChar;
        var replaceDelimiterChar = this.replaceDelimiter;
        if (value.match(encodedDelimiterRegex)) {
          value = value.replace(encodedDelimiterRegex, replaceDelimiterChar).split(delimiterChar);
        } else if (value.match(delimiterRegex)) {
          value = value.split(delimiterChar);
        }
        if (Array.isArray(value) && value.length > 0) {
          for (var i = 0; i < value.length; i++) {
            if (value[i].indexOf(replaceDelimiterChar) > -1) {
              value[i] = value[i].replace(replaceDelimiterChar, encodedDelimiterChar);
            }
            this.setValue([_he["default"].decode(value[i])].concat(_toConsumableArray(this.value)));
          }
        } else {
          this.setValue([_he["default"].decode(value)].concat(_toConsumableArray(this.value)));
        }
      } else {
        if (this.supportHtmlAsOption) {
          this.setValue(uniq([value].concat(_toConsumableArray(this.value))));
        } else {
          this.setValue(uniq([_he["default"].decode(value)].concat(_toConsumableArray(this.value)))); // Decodes the encoded text of option
        }
      }
      this._updateCountBadges();
    }

    /**
     * Deselect a value.
     * @param value
     */
  }, {
    key: "_deselectValue",
    value: function _deselectValue(value, removeAll) {
      if (!value && value !== "") return;
      var values = (0, _map["default"])(this._selected, function (o) {
        return o._text;
      });
      values = (0, _filter["default"])(values, function (v) {
        return v !== value;
      });
      this.setValue((0, _filter["default"])(this.value, function (v) {
        return values.find(function (val) {
          return val === v;
        }) != null;
      }), removeAll);
      this._updateCountBadges();
    }
  }, {
    key: "_updateCountBadges",
    value: function _updateCountBadges() {
      if (this._badges) {
        this._badges[1] && this._badges[1].setAttribute('data-type', 'info');
        this._badges[1].innerHTML = this._selected.length;
        if (!this.showSelectedOptionsInAvailable) {
          this._badges[0].innerHTML = getAvailableCount(this.options);
        }
      }
    }

    /**
     * Checks for shift and escape key presses
     * @param {Object} e
     */
  }, {
    key: "_onInputKeyDown",
    value: function _onInputKeyDown(e) {
      var keyName = specialKeyCodeMap[e.which || e.keyCode];
      switch (keyName) {
        case 'esc':
          if (keyName === 'esc' || this.el.contains(e.target) || this._resultsEl.contains(e.target) || this._tagsEl.contains(e.target)) {
            this.close();
            this.el.querySelector('.eto-complex-autocomplete__field').blur();
          }
          break;
        case 'tab':
          this._onInputTabKeydown(e);
          break;
        case 'enter':
          if (this._isOpen) {
            if (this._tabs && this._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
              if (this._cursorEl) {
                this._onOptionClickProcess(this._cursorEl.querySelector('.eto-checkbox__field'));
                this.keyPressed = undefined;
                e.preventDefault();
                e.stopPropagation();
              }
            } else {
              if (this._selectedCursorEl) {
                this._onSelectedOptionClickProcess(this._selectedCursorEl.querySelector('.eto-checkbox__field'));
                this.keyPressed = undefined;
                e.preventDefault();
                e.stopPropagation();
              }
            }
          }
          break;
        case 'up':
          if (this._isOpen) {
            if (this._tabs && this._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
              e.preventDefault();
              this._moveCursor(-1);
            } else {
              e.preventDefault();
              this._moveSelectedCursor(-1);
            }
          }
          break;
        case 'down':
          if (this._isOpen) {
            if (this._tabs && this._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
              e.preventDefault();
              this._moveCursor(+1);
            } else {
              e.preventDefault();
              this._moveSelectedCursor(+1);
            }
          }
          break;
        case 'shift':
          this.keyPressed = keyName;
          break;
        case 'alt':
          e.preventDefault();
          e.stopImmediatePropagation();
          e.stopPropagation();
          break;
      }
    }

    /**
     * Clears keyPressed variable on keyUp event
     * @param {Object} e
     */
  }, {
    key: "_onInputKeyUp",
    value: function _onInputKeyUp(e) {
      var keyName = specialKeyCodeMap[e.which || e.keyCode];
      switch (keyName) {
        case 'tab':
          var focusable = !(e.target.classList.contains('eto-complex-autocomplete__tip') || e.target.classList.contains('eto-tag__remove'));
          if (focusable && (this.el.contains(e.target) || this._tagsEl.contains(e.target))) {
            this._onInputFocus();
          }
          e.preventDefault();
          e.stopPropagation();
          break;
        default:
          this.keyPressed = undefined;
          break;
      }
    }

    /**
     * On input focus.
     * @param {Object} e
     */
  }, {
    key: "_onInputFocus",
    value: function _onInputFocus(e) {
      if (this.disabled) return;
      this.hasFocus = true;
      this._inputEl.setAttribute('placeholder', this.placeholder);
      this._tagsEl.style.display = 'none';
      this._focusValue = e ? e.target.value : null;
      if (this._inputEl.value === '') {
        this.searchQuery = '';
        if (this.options) {
          this._inputEl.setAttribute('placeholder', this.placeholder);
          this._tagsEl.style.display = 'none';
          // when input query is empty available count is 0
          this.open(0, this._selected.length);
          this._resetOptions();
          this._tabs.setActive(0);
          this._selectAllEl.style.display = 'inline';
          this._clearAllEl.style.display = 'none';
          this._resizeHeight();
          if (this._tagsPopover) this._tagsPopover.popover.close();
        }
      }
      this.trigger('inputFocus');
    }

    /**
     * On input blur.
     * @param {Object} e
     */
  }, {
    key: "_onInputBlur",
    value: function _onInputBlur(e) {
      if (this._inputEl.value == '' && !this._resultsEl.contains(e.target)) {
        this.hasFocus = false;
        this._tagsEl.style.display = 'inherit';
        this._inputEl.setAttribute('placeholder', this.placeholder);
        this.trigger('inputBlur');
      }
      this.searchQuery = this._inputEl.value;
    }
  }, {
    key: "_processClose",
    value: function _processClose() {
      this.page = 0;
      this.selectAllActive = false;
      this.hasFocus = false;
      this._tagsEl.style.display = 'inherit';
      this._inputEl.setAttribute('placeholder', this.placeholder);
      this._inputEl.value = '';
      this._reGenerateTags();
      this.setContent([]);
      this.isSelectedTabHasScrollEvent = false;
      this.matchedResults = []; // accumulator for all results
      this.selectedTabCurrentPage = 0;
      this.newScrollPosition = 0;
      this.selectedTabNewScrollPosition = 0;
      this.lastAvailableTabScrollPosition = 0;
      this.lastSelectedTabScrollPosition = 0;
      this.hiddenOptions = [];
    }

    /**
    * On input keydown.
    * @param {Object} e
    */
  }, {
    key: "_onKeyDown",
    value: function _onKeyDown(e) {
      if (e.which === 17) {
        //press ctrl key
        this._ctrlPressed = true;
      }
    }
    /**
     /**
     * On input change.
     * @param {Object} e
     */
  }, {
    key: "_onInputChange",
    value: function _onInputChange(e) {
      this.searchQuery = e.target.value;
      this.page = 0;
      this.isOnScroll = false;
      if (e.which === 18) {
        return;
      }
      this.newScrollPosition = 0;
      if (36 < e.which && e.which < 41 || 13 === e.which) {
        // arrow buttons or enter buttons
        return;
      }
      if (e.which === 17) {
        this._ctrlPressed = false;
        return;
      }
      if (this._ctrlPressed) return;
      if (e.which === 17) {
        this._ctrlPressed = false;
        return;
      }
      if (this._ctrlPressed) return;
      if (e.which === 17) {
        this._ctrlPressed = false;
        return;
      }
      if (this._ctrlPressed) return;
      this.hasFocus = true;
      this._tagsEl.style.display = 'none';
      this._resetCursors();
      var delimiterChar = delimiterMap[this.delimiter];
      if (delimiterChar === e.target.value.slice(-1)) {
        e.preventDefault();
        if ('' === e.target.value.split(delimiterChar).join('').trim()) {
          // use case 1: getting empty string when all delimiters and spaces are stripped
          // use case 2: starting with delimiter
          // skip value in all these cases, and clear the field
          e.target.value = '';
        } else if (e.target.value.endsWith("\\" + delimiterChar)) {
          // input ends with delimited delimiter, proceed like normal input
          this.trigger('inputChange', e.target.value);
        } else {
          // input ends with delimiter, proceed with adding the term
          this._onDelimiter(e.target.value.slice(0, -1));
        }
      } else {
        this.trigger('inputChange', e.target.value);
      }
    }

    /** Gets Called on change event
     * On input value change.
     * @param {Object} e
     */
  }, {
    key: "_onInputValueChange",
    value: function _onInputValueChange(e) {
      this.searchQuery = e.target.value;
    }

    /**
     * On option click.
     * @param {Object} e
     */
  }, {
    key: "_onOptionClick",
    value: function _onOptionClick(e) {
      if (this._resultsEl.contains(e.target)) {
        this._onOptionClickProcess(e.target);
        this.keyPressed = undefined;
        e.preventDefault();
        e.stopPropagation();
        e.stopImmediatePropagation();
      }
    }
  }, {
    key: "_onOptionClickProcess",
    value: function _onOptionClickProcess(elem) {
      if (this._resultsEl.contains(elem)) {
        this.selectAllActive = false;
        var result = this._getResultFromElement(elem);
        var currentSelected, currentSelectedItem;
        var clickTargetElmName = this.resultColumns && !result._add ? 'tr' : 'li';
        var clickTargetParentElmName = this.resultColumns && !result._add ? 'tbody' : 'ul';
        var listElements = checkClosest(elem, clickTargetElmName).parentElement;
        if (!this.options[0].items) {
          currentSelected = this.originalOptions.indexOf(this.originalOptions.find(function (c) {
            return c.value === result.data;
          }));
        } else {
          currentSelected = Array.from(listElements.parentElement.querySelectorAll(clickTargetParentElmName)).indexOf(checkClosest(elem, clickTargetParentElmName));
          currentSelectedItem = Array.from(listElements.children).indexOf(checkClosest(elem, clickTargetElmName));
        }
        if (this.keyPressed === 'shift') {
          this._performShiftClickOperation(listElements, elem, 'available', currentSelected, currentSelectedItem, result.data);
        } else {
          if (this._selected.filter(function (c) {
            return c._text === result._text;
          }).length === 0) {
            //Don't select if onSelect returns false, allows for custom behavior
            if (!(typeof this.onSelect === 'function' && this.onSelect(result) === false)) {
              this._selectValue(result._text, result.multipleText);
              var closestCb = checkClosest(elem, 'eto-results__option');
              if (closestCb.querySelector('.eto-checkbox__field')) {
                closestCb.querySelector('.eto-checkbox__field').checked = true;
              }
              var closest = checkClosest(elem, clickTargetElmName);
              if (closest) closest.className += ' eto-results-selected-option-color';
              var value = result._text || result.text;
              this.trigger('selected', result.data || value);
            }
          } else {
            //Don't select if onDeselect returns false, allows for custom behavior
            if (!(typeof this.onDeselect === 'function' && this.onDeselect(result) === false)) {
              this._deselectValue(result._text);
              var _closestCb = checkClosest(elem, 'eto-results__option');
              if (_closestCb.querySelector('.eto-checkbox__field')) {
                _closestCb.querySelector('.eto-checkbox__field').checked = false;
              }
              var _closest = checkClosest(elem, clickTargetElmName);
              _closest && _closest.classList.remove('eto-results-selected-option-color');
              if (this._selected.length === 0) {
                this._inputEl.setAttribute('placeholder', this.placeholder);
              }
              var _value = result._text || result.text;
              this.trigger('unselected', result.data || _value);
            }
          }
        }
        this.previouslySelected = currentSelected;
        this.previouslySelectedItem = currentSelectedItem;
        if (result.multipleText === true) {
          this.close();
          this.el.querySelector('.eto-complex-autocomplete__field').blur();
        } else {
          if (!this.loadMoreOptionsOnScroll) {
            this._inputEl.value = '';
          }
        }
      }
    }

    /**
     * On removal of selected option
     * @param {Object} e - event
     */
  }, {
    key: "_onSelectedOptionClick",
    value: function _onSelectedOptionClick(e) {
      if (this._resultsEl.contains(e.target)) {
        this._onSelectedOptionClickProcess(e.target);
        this.keyPressed = undefined;
        e.preventDefault();
        e.stopPropagation();
      }
    }

    /**
     * On removal of selected option
     * @param {Object} e - event
     */
  }, {
    key: "_onSelectedOptionClickProcess",
    value: function _onSelectedOptionClickProcess(elem) {
      var value = _domManipulation["default"].getAttribute(checkClosest(elem, 'eto-results__selected-option'), 'data-text');
      if (this._resultsEl.contains(elem)) {
        var currentSelected, currentSelectedItem;
        var clickTargetElmName = this.resultColumns ? 'tr' : 'li';
        var clickTargetParentElmName = this.resultColumns ? 'tbody' : 'ul';
        var listElements = checkClosest(elem, clickTargetElmName).parentElement;
        if (!this.options[0].items) {
          currentSelected = this.originalOptions.indexOf(this.originalOptions.find(function (c) {
            return c.value === value;
          }));
        } else {
          currentSelected = Array.from(listElements.parentElement.querySelectorAll(clickTargetParentElmName)).indexOf(checkClosest(elem, clickTargetParentElmName));
          currentSelectedItem = Array.from(listElements.children).indexOf(checkClosest(elem, clickTargetElmName));
        }
        if (this.keyPressed === 'shift') {
          this._performShiftClickOperation(listElements, elem, 'selected', currentSelected, currentSelectedItem, value);
        } else {
          if (this._selected.filter(function (c) {
            return c._text === value;
          }).length === 1 && this._selected.length > 0) {
            if (!(typeof this.onDeselect === 'function' && this.onDeselect(value) === false)) {
              this._deselectValue(value);
              if (!this.loadMoreOptionsOnScroll) {
                this._tabs.setActive(1);
              }
              this._resizeHeight();
              var closestCb = checkClosest(elem, 'eto-results__selected-option');
              closestCb.querySelector('.eto-checkbox__field').checked = false;
              var closest = checkClosest(elem, clickTargetElmName);
              closest && closest.classList.remove('eto-results-selected-option-color');
              this.trigger('unselected', value);
            }
          } else {
            //Don't select if onSelect returns false, allows for custom behavior
            if (!(typeof this.onSelect === 'function' && this.onSelect(value) === false)) {
              this._selectValue(value);
              var _closestCb2 = checkClosest(elem, 'eto-results__selected-option');
              _closestCb2.querySelector('.eto-checkbox__field').checked = true;
              var _closest2 = checkClosest(elem, clickTargetElmName);
              if (_closest2) _closest2.className += ' eto-results-selected-option-color';
              this.trigger('selected', value);
            }
          }
          if (this._resultsEl.contains(elem) && this._selected.length === 0) {
            this._inputEl.setAttribute('placeholder', this.placeholder);
          }
        }
        this.previouslySelectSelected = currentSelected;
        this.previouslySelectSelectedItem = currentSelectedItem;
      }
    }

    /**
     * On removal of selected option
     * @param {List} listElements - list of Elements
     * @param {Object} e - event
     * @param {String} from - from which tab
     * @param {Number} currentSelected - index of current selected element
     * @param {Number} currentSelectedItem - index of current selected item
     * @param {String} value - value of the item selected
     */
  }, {
    key: "_performShiftClickOperation",
    value: function _performShiftClickOperation(listElements, elem, from, currentSelected, currentSelectedItem, value) {
      var _this5 = this;
      var previouslySelected = from === 'available' ? this.previouslySelected : this.previouslySelectSelected;
      var previouslySelectedItem = from === 'available' ? this.previouslySelectedItem : this.previouslySelectSelectedItem;
      if (this.options[0].items) {
        var arr = Array.from(Array.from(listElements.parentElement.querySelectorAll('ul')));
        arr.forEach(function (ele) {
          ele = Array.from(ele.children);
        });
        var min, max, minItem, maxItem;
        if (previouslySelected > currentSelected) {
          min = currentSelected;
          max = previouslySelected;
          minItem = currentSelectedItem;
          maxItem = previouslySelectedItem;
        } else if (previouslySelected < currentSelected) {
          min = previouslySelected;
          max = currentSelected;
          maxItem = currentSelectedItem;
          minItem = previouslySelectedItem;
        } else {
          min = max = currentSelected;
          maxItem = Math.max(currentSelectedItem, previouslySelectedItem);
          minItem = Math.min(previouslySelectedItem, currentSelectedItem);
        }
        if (min === max) {
          if (!isNaN(minItem) && !isNaN(maxItem)) {
            var items;
            for (var i = minItem; i <= maxItem; i++) {
              items = Array.from(arr[min].children);
              if (from === 'available') {
                this._selectValue(items[i].getAttribute('data-text'));
                items[i].querySelector('.eto-checkbox__field').checked = true;
                items[i].className += ' eto-results-selected-option-color';
              } else {
                this._deselectValue(items[i].getAttribute('data-text'));
                items[i].querySelector('.eto-checkbox__field').checked = false;
                items[i].classList.remove('eto-results-selected-option-color');
              }
            }
          } else {
            if (from === 'selected') {
              this._deselectValue(value);
              this._tabs.setActive(1);
              this._resizeHeight();
              elem.parentNode.querySelector('.eto-checkbox__field').checked = false;
              if (this._resultsEl.contains(elem) && this._selected.length === 0) {
                this._inputEl.setAttribute('placeholder', this.placeholder);
              }
            } else {
              this._selectValue(value);
              elem.parentNode.querySelector('.eto-checkbox__field').checked = true;
              var closest = checkClosest(elem, 'li');
              if (closest) closest.className += ' eto-results-selected-option-color';
              if (this._selected.length === 0) {
                this._inputEl.setAttribute('placeholder', this.placeholder);
              }
            }
          }
        } else {
          var _items, minItemIndex, maxItemIndex;
          for (var _i2 = min; _i2 <= max; _i2++) {
            _items = Array.from(arr[_i2].children);
            minItemIndex = _i2 === min ? minItem : 1;
            maxItemIndex = _i2 === max ? maxItem : _items.length - 1;
            for (var j = minItemIndex; j <= maxItemIndex; j++) {
              if (from === 'available') {
                this._selectValue(_items[j].getAttribute('data-text'));
                _items[j].querySelector('.eto-checkbox__field').checked = true;
                _items[j].className += ' eto-results-selected-option-color';
              } else {
                this._deselectValue(_items[j].getAttribute('data-text'));
                _items[j].querySelector('.eto-checkbox__field').checked = false;
                _items[j].classList.remove('eto-results-selected-option-color');
              }
            }
          }
        }
      } else {
        var _arr = Array.from(listElements.children);
        if (previouslySelected > currentSelected) {
          var _loop = function _loop(_i3) {
            if (!_this5.originalOptions[_i3].selected && from === 'available') {
              _this5._selectValue(_this5.originalOptions[_i3].value);
              var node = _arr.find(function (c) {
                return c.getAttribute('data-text') === _this5.originalOptions[_i3].value;
              });
              if (node) {
                node.querySelector('.eto-checkbox__field').checked = true;
                node.className += ' eto-results-selected-option-color';
              }
            } else if (_this5.originalOptions[_i3].selected && from === 'selected') {
              _this5._deselectValue(_this5.originalOptions[_i3].value);
              var _node = _arr.find(function (c) {
                return c.getAttribute('data-text') === _this5.originalOptions[_i3].value;
              });
              if (_node) {
                _node.querySelector('.eto-checkbox__field').checked = false;
                _node.classList.remove('eto-results-selected-option-color');
              }
            }
          };
          for (var _i3 = currentSelected; _i3 <= previouslySelected; _i3++) {
            _loop(_i3);
          }
        } else if (previouslySelected < currentSelected) {
          var _loop2 = function _loop2(_i4) {
            if (!_this5.originalOptions[_i4].selected && from === 'available') {
              _this5._selectValue(_this5.originalOptions[_i4].value);
              var node = _arr.find(function (c) {
                return c.getAttribute('data-text') === _this5.originalOptions[_i4].value;
              });
              if (node) {
                node.querySelector('.eto-checkbox__field').checked = true;
                node.className += ' eto-results-selected-option-color';
              }
            } else if (_this5.originalOptions[_i4].selected && from === 'selected') {
              _this5._deselectValue(_this5.originalOptions[_i4].value);
              var _node2 = _arr.find(function (c) {
                return c.getAttribute('data-text') === _this5.originalOptions[_i4].value;
              });
              if (_node2) {
                _node2.querySelector('.eto-checkbox__field').checked = false;
                _node2.classList.remove('eto-results-selected-option-color');
              }
            }
          };
          for (var _i4 = previouslySelected; _i4 <= currentSelected; _i4++) {
            _loop2(_i4);
          }
        } else {
          if (from === 'selected') {
            this._deselectValue(value);
            this._tabs.setActive(1);
            this._resizeHeight();
            elem.parentNode.querySelector('.eto-checkbox__field').checked = false;
            var _closest3 = checkClosest(elem, 'li');
            _closest3 && _closest3.classList.remove('eto-results-selected-option-color');
            if (this._resultsEl.contains(elem) && this._selected.length === 0) {
              this._inputEl.setAttribute('placeholder', this.placeholder);
            }
          } else {
            this._selectValue(value);
            elem.parentNode.querySelector('.eto-checkbox__field').checked = true;
            var _closest4 = checkClosest(elem, 'li');
            if (_closest4) _closest4.className += ' eto-results-selected-option-color';
            if (this._selected.length === 0) {
              this._inputEl.setAttribute('placeholder', this.placeholder);
            }
          }
        }
      }
    }
    /**
     * 
     * @param {Array} options
     * @returns 
     */
  }, {
    key: "_getAvailableValues",
    value: function _getAvailableValues(options) {
      var _this6 = this;
      var availableValues = [];
      if (options.filter(function (c) {
        return c.items;
      }).length === 0) {
        availableValues = this.originalOptions.filter(function (c) {
          return _this6._selected.filter(function (d) {
            return d._text === c._text;
          }).length === 0;
        });
      } else {
        for (var i = 0; i < options.length; i++) {
          availableValues = availableValues.concat(options[i].items.filter(function (c) {
            return !c.selected && !c._add;
          }));
        }
        availableValues = (0, _map["default"])(availableValues, function (a) {
          return (0, _assign["default"])({}, a);
        });
      }
      return availableValues;
    }
    /**
     * Select all values on select all click
     */
  }, {
    key: "_selectAllValues",
    value: function _selectAllValues(e) {
      if (this._resultsEl.contains(e.target)) {
        this.selectAllActive = true;
        this.selectedTabCurrentPage = 0;
        var availableValues = [];
        if (this.loadMoreOptionsOnScroll) {
          var _this$resultColumns;
          if (((_this$resultColumns = this.resultColumns) === null || _this$resultColumns === void 0 ? void 0 : _this$resultColumns.length) > 0) {
            this.options = this._setContentForChunk(this.initialCollection).slice(0);
            availableValues = this._getAvailableValues(this._setContentForChunk(this.initialCollection));
          } else {
            availableValues = this._getAvailableValues(this._setContentForChunk(uniq([].concat(_toConsumableArray(this.value), _toConsumableArray(this.matchedResultsAsStrings))).slice(0, this.pageSize), false));
          }
          this.value = [].concat(_toConsumableArray(this.value), _toConsumableArray(this.matchedResultsAsStrings));
        } else {
          availableValues = this._getAvailableValues(this.options);
        }
        var availableLength = availableValues.length;
        for (var i = availableLength - 1; i >= 0; i--) {
          if (!availableValues[i]._add) {
            this._selectValue(availableValues[i]._text, availableValues[i].multipleText);
          }
        }
        this.trigger('selected', availableValues);
        this._resetOptions();
        this._inputEl.value = '';
        this._tabs.setActive(1);
        if (this.loadMoreOptionsOnScroll) {
          this._bindScrollEventToSelectedTab();
        }
        this._resizeHeight();
        e.stopPropagation();
        e.preventDefault();
      }
    }

    /**
     * Clear all values on clear all click
     */
  }, {
    key: "_clearAllValues",
    value: function _clearAllValues(e) {
      if (this._resultsEl.contains(e.target)) {
        this.selectAllActive = false;
        var deselectedValues = [];
        if (!this.loadMoreOptionsOnScroll) {
          while (this._selected.length > 0) {
            deselectedValues.push(this._selected[0]);
            this._deselectValue(this._selected[0]._text);
          }
          this.trigger('unselected', deselectedValues);
        } else {
          this.trigger('unselected', this.value);
          this.value = [];
          this.page = 0;
          this.setContent(this.initialCollection);
        }
        this._inputEl.setAttribute('placeholder', this.placeholder);
        // Reset to Available tab
        this._resetOptions({
          isInputChanged: true
        });
        this._tabs.setActive(0);
        this._resizeHeight();
        this._inputEl.value = this.searchQuery;
        e.stopPropagation();
        e.preventDefault();
      }
    }
  }, {
    key: "_replaceNewlines",
    value: function _replaceNewlines(temp) {
      var delimiterChar = delimiterMap[this.delimiter];
      //remove all tab and new lines
      temp = temp.replace(/[\s]*[\n\r\t]+[\s]*/g, delimiterChar);
      //remove all starting and ending delimiter or spaces
      var exp = new RegExp('^[' + delimiterChar + '\\s]+|[' + delimiterChar + '\\s]+$', 'g');
      temp = temp.replace(exp, '');
      exp = new RegExp(delimiterChar + '[' + delimiterChar + '\\s]*' + delimiterChar, 'g');
      temp = temp.replace(exp, delimiterChar);
      temp = temp.replace(/(^\s+|\s+$)/g, '');
      return temp;
    }
  }, {
    key: "_onPaste",
    value: function _onPaste(e) {
      var _this7 = this;
      if (!this.add || this.resultColumns && this.resultColumns != null) {
        this._inputEl.value = "";
        e.stopPropagation();
        e.preventDefault();
        return;
      }
      var me = this;
      var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
      var pastedData;
      var delimiterChar = delimiterMap[this.delimiter];
      if (window && window.clipboardData) {
        //IE specific handling using the contents of the clipboard
        pastedData = window.clipboardData.getData('Text');
      } else {
        //Non-IE specific handling
        pastedData = e.clipboardData.getData('text');
      }
      var vals = [];
      if (this.splitInput && typeof this.splitInput === 'function') {
        vals = this.splitInput(pastedData);
      } else {
        var prefix = this._inputEl.value;
        if (prefix) {
          pastedData = prefix + pastedData;
          this._inputEl.value = '';
        }
        var escapedDelimiter = '\\' + delimiterChar;
        var replaceDelimiter = this.replaceDelimiter;
        pastedData = this._replaceNewlines(pastedData);
        if (pastedData.indexOf(escapedDelimiter) != -1) {
          pastedData = pastedData.replace(escapedDelimiter, replaceDelimiter);
        }
        var splitVals = pastedData.split(delimiterChar);
        if (Array.isArray(splitVals) && splitVals.length > 0) {
          for (var i = 0; i < splitVals.length; i++) {
            if (splitVals[i].indexOf(replaceDelimiter) > -1) {
              vals.push(splitVals[i].replace(replaceDelimiter, escapedDelimiter));
            } else {
              vals.push(splitVals[i]);
            }
          }
        }
      }
      this.open();
      vals = (0, _reverse["default"])(vals);
      vals.forEach(function (v) {
        var isValueEncoded = v && v !== _he["default"].decode(v);
        if (_this7.supportHtmlAsOption && isValueEncoded) {
          me._selectValue(v); // For not encoding already encoded text in the option
        } else {
          me._selectValue(_he["default"].encode(v));
        }
      });
      me.trigger('selected', vals);
      if (this.value) {
        var collection = this.options.slice(0);
        collection = addValuesToOptions(collection, this.value, this.add, true, this.exclude, this.noValue, this.excludeNoValue, isGridParent, {
          supportsHtmlAsOption: this.supportHtmlAsOption
        });
        this.options = this.originalOptions = _toConsumableArray(updateOptions(this.value, collection, this.add));
      }
      this._resetOptions();
      this._tabs.setActive(1);
      if (this.loadMoreOptionsOnScroll) {
        this._bindScrollEventToSelectedTab();
      }
      this._resizeHeight();
      e.stopPropagation();
      e.preventDefault();
    }

    /**
     * On user typing in a delimiter
     * @param {Object} result
     */
  }, {
    key: "_onDelimiter",
    value: function _onDelimiter(result) {
      if (!result) return;
      if (!(typeof this.onSelect === 'function' && this.onSelect(result.data.datum) === false)) {
        var existing = false;
        var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
        this.options.forEach(function (c) {
          if (c.items.find(function (d) {
            return d._text === result;
          }) != null) existing = true;
        });
        if (this.add || !this.add && existing) {
          this._selectValue(result);
          if (this.value) {
            var collection = resetCollection();
            collection = addValuesToOptions(collection, this.value, this.add, this.exclude, this.noValue, this.excludeNoValue, this.options, isGridParent);
            this.options = this.originalOptions = _toConsumableArray(updateOptions(this.value, collection, this.add));
          }
          this._inputEl.value = '';
          this._resetOptions();
          this._tabs.setActive(1);
          if (this.loadMoreOptionsOnScroll) {
            this._bindScrollEventToSelectedTab();
          }
          this._resizeHeight();
          this.trigger('selected', result);
        }
      }
    }
  }, {
    key: "quoteAttr",
    value: function quoteAttr(s, preserveCR) {
      preserveCR = preserveCR ? '&#13;' : '\n';
      return ('' + s /* Forces the conversion to string. */).replace(/&/g, '&amp;') /* This MUST be the 1st replacement. */.replace(/'/g, '&apos;') /* The 4 other predefined entities, required. */.replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      /*
      You may add other replacements here for HTML only
      (but it's not necessary).
      Or for XML, only if the named entities are defined in its DTD.
      */.replace(/\r\n/g, preserveCR) /* Must be before the next replacement. */.replace(/[\r\n]/g, preserveCR);
    }

    /**
     * Opens the selected results popup
     */
  }, {
    key: "_openSelectedItems",
    value: function _openSelectedItems(e) {
      if (this.options) {
        this._inputEl.setAttribute('placeholder', this.placeholder);
        this._tagsEl.style.display = 'none';
        this.open(getAvailableCount(this.options), this._selected.length);
        this._resetOptions();
        this._tabs.setActive(1);
        if (this.loadMoreOptionsOnScroll) {
          this._bindScrollEventToSelectedTab();
        }
        this._selectAllEl.style.display = 'none';
        this._clearAllEl.style.display = 'inline';
        this._resizeHeight();
        if (this._tagsPopover) this._tagsPopover.popover.close();
      }
    }

    /**
     * Regenerate tags
     */
  }, {
    key: "_reGenerateTags",
    value: function _reGenerateTags(removeAll) {
      var _this8 = this;
      if (this._isOpen || removeAll) return;
      this._tagsEl.innerHTML = '';
      this._tagsEl.style.width = 'auto';
      var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
      var text = '';
      var selectedOptions = [];
      (0, _each["default"])(this.value, function (val) {
        var result = _this8._selected.find(function (item) {
          return item._text === val;
        });
        selectedOptions.push(result);
      });
      var tag = '';
      //Autocomplete defined inside a Grid
      if (isGridParent) {
        //Always display one tag
        if (this._selected.length == 1) {
          text = this.resultColumns && this.resultColumns.length > 0 ? selectedOptions[0].displayText : selectedOptions[0]._text;
          var value = unescape(selectedOptions[0]._text);
          var classes = '';
          classes += !selectedOptions[0].exclude && selectedOptions[0].noValue ? ' eto-tag--novalue' : ' ';
          tag = '<span class="eto-grid-tag eto-grid-tag--sm' + classes + '" data-value="' + this.quoteAttr(value) + '">\n' + '         <span class="eto-grid-tag__label">' + text + ';' + '</span>\n' + '         <input type="hidden" name="' + this.name + '_value" value="' + _he["default"].encode(value) + '">\n' + '        </span>';
        } else if (this._selected.length > 1) {
          text = this.resultColumns && this.resultColumns.length > 0 ? selectedOptions[0].displayText : selectedOptions[0]._text;
          var hoverText = this._selected.length - 1 + ' more';
          tag = '<span class="eto-grid-tag eto-grid-tag--sm">\n' + '        <span class="eto-grid-tag__label">' + text + ';' + '</span>\n' + '        </span><span class="eto-tag-hover">\n' + '  <span class="eto-grid-tag-hover__label">\n' + '&#43; ' + hoverText + '</span>\n' + '        </span>';
        }
        this._tagsEl.innerHTML = tag;
        this._inputEl.setAttribute('placeholder', '');
        if (this._tagsEl && this._tagsEl.clientWidth > this._anchorEl.clientWidth - 60) {
          this._tagsEl.style.width = this._anchorEl.clientWidth - 20 + 'px';
        }
      } else {
        for (var i = 0; i < selectedOptions.length; i++) {
          var _selectedOptions$i;
          text = this.resultColumns && this.resultColumns.length > 0 ? selectedOptions[i].displayText : (_selectedOptions$i = selectedOptions[i]) === null || _selectedOptions$i === void 0 ? void 0 : _selectedOptions$i._text;
          var _value2 = unescape(selectedOptions[i]._text);
          var optionInvalidInfo = getInvalidOptionInfo(selectedOptions[i]._text, this.invalidOptions);
          var _classes = selectedOptions[i].exclude ? ' eto-tag--exclusion ' : '';
          _classes += !selectedOptions[i].exclude && selectedOptions[i].noValue ? ' eto-tag--novalue' : ' ';
          _classes += optionInvalidInfo ? ' eto-tag--error' : ' ';
          var componentId = this.id;
          text = text.replace(/</g, "&lt;").replace(/>/g, "&gt;");
          if (!optionInvalidInfo) {
            tag += '<span class="eto-tag eto-tag--sm' + _classes + '" data-value="' + this.quoteAttr(_value2) + '">\n' + '         <span class="eto-tag__label">' + text + '</span>\n' + '         <span class="eto-tag__remove" tabindex="0"><i translate="no" class="notranslate md-icon md-icon--sm">close</i></span>\n' + '         <input type="hidden" name="' + this.name + '_value" value="' + _he["default"].encode(_value2) + '">\n' + '        </span>';
          } else {
            if (optionInvalidInfo !== null && optionInvalidInfo !== void 0 && optionInvalidInfo.message) {
              tag += '<span data-tooltip="#' + componentId + '-tooltip-' + text + '-' + i + '" aria-describedby="#' + componentId + '-tooltip-' + text + '-' + i + '" class="eto-tag eto-tag--sm' + _classes + '" data-value="' + this.quoteAttr(_value2) + '">\n' + '         <span class="eto-tag__label">' + text.toString() + '</span>\n' + '         <span class="eto-tag__remove" tabindex="0"><i translate="no" class="notranslate md-icon md-icon--sm">close</i></span>\n' + '         <input type="hidden" name="' + this.name + '_value" value="' + _he["default"].encode(_value2) + '">\n' + '        </span>\n' + '        <div class="eto-tooltip" data-message-type="' + (optionInvalidInfo === null || optionInvalidInfo === void 0 ? void 0 : optionInvalidInfo.messageType) + '" data-anchor-x="center" data-anchor-y="top" id="#' + componentId + '-tooltip-' + text + '-' + i + '">\n' + '        <div class="eto-tooltip__content">' + (optionInvalidInfo === null || optionInvalidInfo === void 0 ? void 0 : optionInvalidInfo.message) + '</div>\n' + '        <span class="eto-tooltip__caret"></span>\n' + '        </div>';
            } else {
              tag += '<span class="eto-tag eto-tag--sm' + _classes + '" data-value="' + this.quoteAttr(_value2) + '">\n' + '         <span class="eto-tag__label">' + text.toString() + '</span>\n' + '         <span class="eto-tag__remove" tabindex="0"><i translate="no" class="notranslate md-icon md-icon--sm">close</i></span>\n' + '         <input type="hidden" name="' + this.name + '_value" value="' + _he["default"].encode(_value2) + '">\n' + '        </span>';
            }
          }
        }
        if (selectedOptions.length > 0) {
          this._tagsEl.innerHTML = tag;
          this._inputEl.setAttribute('placeholder', '');
        }
        if (this._checkTagFit()) {
          if (selectedOptions.length > 1) {
            text = this._selected.length + ' selected';
            var _tag = '<span class="eto-tag eto-tag--sm eto-tag-hover">\n' + '         <span class="eto-tag__label" style="text-decoration: underline; text-decoration-style: dashed;">' + text.toString() + '</span>\n' + '         <span class="eto-tag__remove" tabindex="0"><i translate="no" class="notranslate md-icon md-icon--sm">close</i></span>\n' + '        </span>';
            this._tagsEl.innerHTML = _tag;
            this._inputEl.setAttribute('placeholder', '');
          } else if (selectedOptions.length == 1) {
            var tagLabel = this.query('.eto-complex-autocomplete__inline-tags .eto-tag__label');
            if (tagLabel && tagLabel.clientWidth > this._anchorEl.clientWidth - 60) {
              tagLabel.style.width = this._anchorEl.clientWidth - 100 + 'px';
            }
          }
        }
      }
    }

    /**
     * Check the tags to see that they don't exceed beyond the input field
     */
  }, {
    key: "_checkTagFit",
    value: function _checkTagFit() {
      return this._tagsEl.clientWidth > this._anchorEl.clientWidth - 60;
    }

    /**
     * On hover of tag containing multiple components.
     * @param {Event} e
     */
  }, {
    key: "_onTagHover",
    value: function _onTagHover(e) {
      var itemEl = e.delegateTarget;
      if (!itemEl.contains(e.relatedTarget)) {
        this._initTagsPopover();
      }
      e.stopPropagation();
    }
  }, {
    key: "_onTagHoverOut",
    value: function _onTagHoverOut(e) {
      var itemEl = e.delegateTarget;
      if (itemEl !== e.relatedTarget && !itemEl.contains(e.relatedTarget)) {
        if (this._tagsPopover) this._tagsPopover.popover.close();
      }
      e.stopPropagation();
    }

    /**
     * Initializes and opens a popover containing all selected tags.
     */
  }, {
    key: "_initTagsPopover",
    value: function _initTagsPopover() {
      var _this9 = this;
      if (this._tagsPopover) {
        return;
      }
      var fieldEl = this._anchorEl;
      var selectedOptions = [];
      (0, _each["default"])(this.value, function (val) {
        var result = _this9._selected.find(function (item) {
          return item._text === val;
        });
        selectedOptions.push(result);
      });
      var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
      var selectedOptionsStrings = [];
      // selectedOptionsStrings = selectedOptions.slice(0).map(c => c.text.replace(/</g, "&lt;").replace(/>/g, "&gt;")).join(', ');
      // Popover doesn't need to have highlight for the matched option
      selectedOptionsStrings = selectedOptions.slice(0).map(function (c) {
        return c._text;
      }).join(', ');
      var popover = new _popover["default"]({
        el: (0, _makeElement["default"])("\n        <div class=\"eto-popover hide-caret ".concat(this.disabled ? 'disabled' : '', " eto-complex-autocomplete__popover\" style=\"width:").concat(fieldEl.clientWidth, "px;pointer-events:none;").concat(isGridParent ? 'min-width: 380px;' : '', "\">\n          <div class=\"eto-popover__content\" style=\"word-break: break-word\">\n            ").concat(selectedOptionsStrings, "\n          </div>\n          <span class=\"eto-popover__caret\"></span>\n        </div>")),
        hideCaret: true
      });

      // On Popover close, clean up.
      popover.on('closed', function () {
        popover.remove();
        delete _this9._tagsPopover;
      });

      // Store references
      this._tagsPopover = {
        popover: popover
      };

      // Open Popover
      // As a perk, using the fieldEl as the anchor means you can click the input field and the popover will not close.
      popover.open({
        anchorTo: fieldEl
      });
    }

    /**
     * Removes the tags popover.
     */
  }, {
    key: "_removeTagsPopover",
    value: function _removeTagsPopover() {
      if (this._tagsPopover) {
        this._tagsPopover.reorder.remove(true);
        this._tagsPopover.popover.remove();
      }
    }

    /**
     * On remove a tag, deselect the value.
     * @param {object} e - event
     */
  }, {
    key: "_onRemoveTag",
    value: function _onRemoveTag(e) {
      var value = _domManipulation["default"].getAttribute(e.target.parentNode, 'data-value');
      if (typeof value === 'undefined' || value === null) {
        while (this._selected.length > 0) {
          this._deselectValue(this._selected[0]._text, true);
        }
        this._resetOptions();
        this._tabs.setActive(0);
        this._resizeHeight();
        this._inputEl.setAttribute('placeholder', this.placeholder);
      } else {
        var _e$target, _e$target$parentEleme;
        var tagId = (_e$target = e.target) === null || _e$target === void 0 ? void 0 : (_e$target$parentEleme = _e$target.parentElement) === null || _e$target$parentEleme === void 0 ? void 0 : _e$target$parentEleme.getAttribute('data-tooltip');
        if (tagId) {
          var elem = document.querySelector("[data-tooltip='".concat(tagId, "']"));
          this._cleanUpTooltip(elem);
        }
        this._deselectValue(value);
      }
      this._reGenerateTags();
      this._resetOptions();
      if (this._selected.length === 0) {
        this._inputEl.setAttribute('placeholder', this.placeholder);
      }
    }

    /**
     * on remove icon of selected tag keydown
     * @param {event} e 
     */
  }, {
    key: "_onRemoveTagKeydown",
    value: function _onRemoveTagKeydown(e) {
      var keyName = specialKeyCodeMap[e.which || e.keyCode];
      switch (keyName) {
        case 'enter':
          this._onRemoveTag(e);
          break;
      }
    }

    /**
     * On window resize.
     * @param {Event} e
     */
  }, {
    key: "resize",
    value: function resize(e) {
      this._popupHeight = undefined;
      this._resizeHeight();
      if (!this._isOpen) {
        this._reGenerateTags();
      }
    }
  }, {
    key: "_findOption",
    value: function _findOption(value) {
      var re = null;
      if (this.options) {
        this.options.forEach(function (c) {
          var item = c.items.find(function (d) {
            return d._text === value;
          });
          if (item != null) re = item;
        });
      }
      return re;
    }

    //for sorting on UI.
  }, {
    key: "_sortOptions",
    value: function _sortOptions(values) {
      var re = values;
      if (values && this.resultColumns && this.resultColumns.length > 0) {
        var sortDir = 'desc';
        var sortIndex = -1;
        var me = this;
        for (var i = 0; i < this.resultColumns.length; i++) {
          var col = this.resultColumns[i];
          if (col.sortDir) {
            sortDir = col.sortDir;
            sortIndex = i;
            break;
          }
        }
        if (sortIndex > -1) {
          values.sort(function (value1, value2) {
            var option1 = me._findOption(value1);
            var option2 = me._findOption(value2);
            if (option1.columnData[sortIndex].text === option2.columnData[sortIndex].text) return 0;
            if (sortDir === 'desc') return option1.columnData[sortIndex].text > option2.columnData[sortIndex].text ? -1 : 1;else return option1.columnData[sortIndex].text < option2.columnData[sortIndex].text ? -1 : 1;
          });
          re = values;
        }
      }
      return re;
    }
    /**
      * When a tag with a tooltip is hovered, show the tooltip.
      * @param  {Object} e
      */
  }, {
    key: "_onTooltipMouseover",
    value: function _onTooltipMouseover(e) {
      var tagEl = e.delegateTarget;
      this._initTooltip(tagEl);
    }

    /**
     * When a tag with a tooltip is no longer hovered, hide the tooltip.
     * @param  {Object} e
     */
  }, {
    key: "_onTooltipMouseout",
    value: function _onTooltipMouseout(e) {
      var tagEl = e.delegateTarget;
      this._cleanUpTooltip(tagEl);
    }

    /**
     * Initialize a tooltip for a tag.
     * @param {Element} tagEl
     */
  }, {
    key: "_initTooltip",
    value: function _initTooltip(tagEl) {
      // Already created.
      if (this._cc.find(_tooltip["default"], function (i) {
        return i.anchorEl === tagEl;
      })) return false;
      var tagId = tagEl.getAttribute('data-tooltip');
      var el = document.getElementById(tagId);
      if (el) {
        var tip = new _tooltip["default"]({
          el: el,
          anchorEl: tagEl
        });
        this._cc.store(tip, _tooltip["default"]);
        tip.open();
      }
    }
  }, {
    key: "_cleanUpTooltip",
    value: function _cleanUpTooltip(tagEl) {
      var tip = this._cc.find(_tooltip["default"], function (i) {
        return i.anchorEl === tagEl;
      });
      if (tip) {
        tip.close();
        tip.remove(true);
        this._cc.remove(tip, _tooltip["default"]);
      }
    }
    /**
     * Call to resize dropdown height.
     */
  }, {
    key: "resizeDropdown",
    value: function resizeDropdown(e, modalBodyElement) {
      if (this._isOpen) {
        this._resizeHeight(e, modalBodyElement);
      }
    }

    /**
     * If input element is inside modal component.
     * Scroll event on modal for relative position of dropdown w.r.t modal and input(target) element.
     */
  }, {
    key: "_addEventListeners",
    value: function _addEventListeners() {
      var _this$_inputEl;
      var modalBodyElement = (_this$_inputEl = this._inputEl) === null || _this$_inputEl === void 0 ? void 0 : _this$_inputEl.closest('.eto-modal__body');
      if (modalBodyElement) {
        modalBodyElement.addEventListener('scroll', this.resizeDropdown.bind(this, modalBodyElement));
      }
    }

    /**
     * Remove modal event listener.
     */
  }, {
    key: "_removeEventListeners",
    value: function _removeEventListeners() {
      var _this$_inputEl2;
      var modalBodyElement = (_this$_inputEl2 = this._inputEl) === null || _this$_inputEl2 === void 0 ? void 0 : _this$_inputEl2.closest('.eto-modal__body');
      if (modalBodyElement) {
        modalBodyElement.removeEventListener('scroll', this.resizeDropdown.bind(this, modalBodyElement));
      }
    }

    /**
    * On input button keydown and key is tab
    * @param {event} e 
    */
  }, {
    key: "_onInputTabKeydown",
    value: function _onInputTabKeydown(e) {
      var keyName = specialKeyCodeMap[e.which || e.keyCode];
      if (this._isOpen) {
        switch (keyName) {
          case 'tab':
            {
              var resultsDropdownAvailableTab = this._affixed.el.querySelector("[data-tab='.eto-results-available']");
              resultsDropdownAvailableTab === null || resultsDropdownAvailableTab === void 0 ? void 0 : resultsDropdownAvailableTab.focus();
              e.preventDefault();
              e.stopPropagation();
              e.stopImmediatePropagation();
              break;
            }
        }
      }
    }

    /**
     * On results available tab key down
     * @param {event} e 
     */
  }, {
    key: "_onResultsAvailableKeydown",
    value: function _onResultsAvailableKeydown(e) {
      if (this._resultsEl.contains(e.target)) {
        e.stopPropagation();
        e.stopImmediatePropagation();
        var keyName = specialKeyCodeMap[e.which || e.keyCode];
        switch (keyName) {
          case 'enter':
            {
              this._resultsEl.querySelector('[data-tab=".eto-results-available"]').click();
              break;
            }
        }
      }
    }

    /**
    * On results selected tab keydown
    * @param {event} e 
    */
  }, {
    key: "_onResultsSelectedKeydown",
    value: function _onResultsSelectedKeydown(e) {
      if (this._resultsEl.contains(e.target)) {
        e.stopPropagation();
        e.stopImmediatePropagation();
        var keyName = specialKeyCodeMap[e.which || e.keyCode];
        switch (keyName) {
          case 'enter':
            {
              this._resultsEl.querySelector('[data-tab=".eto-results-selected"]').click();
              break;
            }
          case 'tab':
            {
              this._resetCursors();
              break;
            }
        }
      }
    }

    /**
     * on select all button keydown(tab click)
     * @param {event} e 
     */
  }, {
    key: "_onSelectOrClearAllKeydown",
    value: function _onSelectOrClearAllKeydown(e) {
      if (this._resultsEl.contains(e.target)) {
        var keyName = specialKeyCodeMap[e.which || e.keyCode];
        switch (keyName) {
          case 'tab':
            {
              this._resetCursors();
              if (!e.shiftKey) {
                this.close();
                var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
                if (isGridParent) {
                  var _this$_anchorEl$close, _this$_anchorEl$close2, _this$_anchorEl$close3;
                  (_this$_anchorEl$close = this._anchorEl.closest('td')) === null || _this$_anchorEl$close === void 0 ? void 0 : (_this$_anchorEl$close2 = _this$_anchorEl$close.nextElementSibling) === null || _this$_anchorEl$close2 === void 0 ? void 0 : (_this$_anchorEl$close3 = _this$_anchorEl$close2.querySelector('input')) === null || _this$_anchorEl$close3 === void 0 ? void 0 : _this$_anchorEl$close3.focus();
                } else {
                  var _this$_anchorEl$close4, _this$_anchorEl$close5;
                  (_this$_anchorEl$close4 = this._anchorEl.closest('.eto-complex-autocomplete__container')) === null || _this$_anchorEl$close4 === void 0 ? void 0 : (_this$_anchorEl$close5 = _this$_anchorEl$close4.querySelector('button.eto-complex-autocomplete__tip')) === null || _this$_anchorEl$close5 === void 0 ? void 0 : _this$_anchorEl$close5.focus();
                }
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();
                break;
              }
            }
        }
      }
    }

    /**
     * @param {*} e 
     * @param {*} currentTabIndex 
     * @returns 
     */
  }, {
    key: "_onResultsScroll",
    value: function _onResultsScroll(e, currentTabIndex) {
      if (this._tabs && this._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
        this.lastAvailableTabScrollPosition = e.target.scrollTop;
      } else {
        this.lastSelectedTabScrollPosition = e.target.scrollTop;
      }
      if (Math.ceil(e.target.scrollTop) >= e.target.scrollHeight - e.target.clientHeight && Math.round(e.target.scrollTop) !== 0) {
        this.newScrollPosition = Math.floor(e.target.scrollHeight) * .9;
        if (this._tabs && this._tabs.getActive().getAttribute('data-tab') == '.eto-results-available') {
          if (typeof this.matchedResults[0] === 'string') {
            var matchedCountFromOptions = this._getMatchedCountFromOptions({
              options: this.originalOptions,
              excludeAddTerm: true,
              compare: false
            });
            if (this.matchedResults.length > matchedCountFromOptions) {
              this.isOnScroll = true;
              this.page = this.page + 1;
              var subCollection = this.matchedResults.slice(this.page * this.pageSize, (this.page + 1) * this.pageSize);
              if (subCollection.length == 0) return;
              var collection = this._setContentForChunk(subCollection);
              // push latest chunk to existing results
              var mergedOptions = this._mergeOptions(this.options, collection);
              this.options = mergedOptions.slice(0);
              if (this.value) {
                this.options = _toConsumableArray(updateOptions(this.value, this.options, this.add));
              }
              this.originalOptions = this.options.slice(0);
              this._resetOptions({
                onScroll: true
              });
            }
          } else {
            var matchedResultsCount = this._getMatchedCountFromOptions({
              options: this.matchedResults,
              compare: false
            });
            var _matchedCountFromOptions = this._getMatchedCountFromOptions({
              options: this.originalOptions,
              excludeAddTerm: true,
              compare: false
            });
            // Groupby options
            if (matchedResultsCount > _matchedCountFromOptions) {
              this.isOnScroll = true;
              this.page = this.page + 1;
              var _subCollection = this._getOptionsPerPageForGroupbyOptions({
                initialCollection: this.matchedResults,
                pageNumber: this.page,
                pageSize: this.pageSize
              });
              // Merge options on scroll to original options
              var _collection = this._setContentForChunk(_subCollection);
              this.options = this._mergeOptions(this.options, _collection).slice(0);
              // update selection for options
              if (this.value) {
                this.options = _toConsumableArray(updateOptions(this.value, this.options, this.add));
              }
              this.originalOptions = this.options.slice(0);
              this._resetOptions({
                onScroll: true
              });
            }
          }
        } else {
          // Use originalOptions so available results are always in the same order.
          var items = this.originalOptions.slice(0);
          // Exclude selected items in results.
          // Map selected to order of selection for selected in Results template.
          var selectedOptionsLength = this._selected.length;
          if (this.selectAllActive) {
            selectedOptionsLength = uniq([].concat(_toConsumableArray(this.value), _toConsumableArray(this.matchedResultsAsStrings))).length;
          }
          var includeSelectedOptions = true;
          if (this.selectAllActive && selectedOptionsLength > this._selected.length || !this.selectAllActive && selectedOptionsLength > getAvailableCount(this.processedSelectedItemsByPage, includeSelectedOptions)) {
            this.selectedTabCurrentPage++;
            if (this.selectAllActive) {
              var availableValues = [];
              availableValues = this._getAvailableValues(this._setContentForChunk(uniq([].concat(_toConsumableArray(this.value), _toConsumableArray(this.matchedResultsAsStrings))).slice(this.selectedTabCurrentPage * this.pageSize, (this.selectedTabCurrentPage + 1) * this.pageSize)));
              var availableLength = availableValues.length;
              for (var i = availableLength - 1; i >= 0; i--) {
                if (!availableValues[i]._add) {
                  this._selectValue(availableValues[i]._text, availableValues[i].multipleText);
                }
              }
            }
            this._resetOptions({
              currentTabIndex: currentTabIndex,
              onScroll: true
            });
          }
        }
      }
    }

    /**
     * Returns count of options which matches the input query
     * @param {*} param0 
     * compare - always false because search criteria lies outside the component
     * @returns 
     */
  }, {
    key: "_getMatchedCountFromOptions",
    value: function _getMatchedCountFromOptions(_ref4) {
      var _this10 = this;
      var options = _ref4.options,
        excludeAddTerm = _ref4.excludeAddTerm,
        compare = _ref4.compare;
      var matchedCountFromOptions = 0;
      if (typeof options[0] === 'string') {
        options.forEach(function (item, index) {
          if (excludeAddTerm && index == 0) {
            return;
          }
          if (compare) {
            item.items.forEach(function (i) {
              if (i._text.toLowerCase().startsWith(_this10.searchQuery.toLowerCase()) && !i.hide) {
                matchedCountFromOptions++;
              }
            });
          } else {
            item.items.forEach(function (i) {
              matchedCountFromOptions++;
            });
          }
        });
      } else {
        options.forEach(function (group, index) {
          group.items.forEach(function (item) {
            if (item._add && excludeAddTerm) {
              return;
            }
            if (compare) {
              var _item$_text, _item$text;
              if (((_item$_text = item._text) !== null && _item$_text !== void 0 && _item$_text.toLowerCase().startsWith(_this10.searchQuery.toLowerCase()) || (_item$text = item.text) !== null && _item$text !== void 0 && _item$text.toLowerCase().startsWith(_this10.searchQuery.toLowerCase())) && !item.hide) {
                matchedCountFromOptions++;
              }
            } else {
              matchedCountFromOptions++;
            }
          });
        });
      }
      return matchedCountFromOptions;
    }

    /**
     * Add options loaded on scroll to existing options
     * @param {*} originalOptions 
     * @param {*} subCollection 
     * @returns 
     */
  }, {
    key: "_mergeOptions",
    value: function _mergeOptions(originalOptions, subCollection) {
      var _this11 = this;
      if (typeof this.matchedResults[0] === 'string') {
        var mergedOptions = [];
        // store options yet to be loaded which are passed as value(selected)
        if (this.page === 1) {
          originalOptions.forEach(function (group, index) {
            var hiddenOptions = (0, _map["default"])(group.items.filter(function (item) {
              return item.hide;
            }), '_text');
            _this11.hiddenOptions = [].concat(_toConsumableArray(_this11.hiddenOptions), _toConsumableArray(hiddenOptions));
          });
        }

        // Foreach hidden option check weather it is available in the newly loaded chunk(subcollection param) on scroll
        if (this.hiddenOptions && this.hiddenOptions.length > 0) {
          this.hiddenOptions.forEach(function (item) {
            var hiddenOptionFound = null;
            subCollection.forEach(function (sGroup) {
              sGroup.items.forEach(function (sgItem) {
                if (sgItem._text === item) {
                  hiddenOptionFound = sgItem;
                }
              });
            });
            // If available remove it from previous loaded options (which has hide: true)
            if (hiddenOptionFound) {
              originalOptions = originalOptions.map(function (group, index) {
                return {
                  items: group.items.filter(function (option) {
                    return !((0, _get2["default"])(hiddenOptionFound, '_text', '') === option._text);
                  })
                };
              });
              // Remove it from hiddenoptions
              _this11.hiddenOptions = _this11.hiddenOptions.filter(function (option) {
                return !(option === (0, _get2["default"])(hiddenOptionFound, '_text', ''));
              });
            }
          });
        }
        originalOptions.forEach(function (group, index) {
          var mergedGroup = null;
          mergedGroup = _objectSpread(_objectSpread({}, group), {}, {
            items: (0, _uniqBy["default"])([].concat(_toConsumableArray(group.items), _toConsumableArray(subCollection[index].items)), '_text')
          });
          mergedOptions.push(mergedGroup);
        });
        return mergedOptions;
      } else {
        var mergedOptionsForGroupsWithoutTitle = [];
        var mergedOptionsForGroupsWithTitle = [];
        if (this.page === 1) {
          originalOptions.forEach(function (group, index) {
            var hiddenOptions = (0, _map["default"])(group.items.filter(function (item) {
              return item.hide;
            }), '_text');
            _this11.hiddenOptions = [].concat(_toConsumableArray(_this11.hiddenOptions), _toConsumableArray(hiddenOptions));
          });
        }
        // Foreach hidden option check weather it is available in the loaded chunk on scroll
        if (this.hiddenOptions && this.hiddenOptions.length > 0) {
          this.hiddenOptions.forEach(function (item) {
            var hiddenOptionFound = null;
            subCollection.forEach(function (sGroup) {
              sGroup.items.forEach(function (sgItem) {
                if (sgItem._text === item) {
                  hiddenOptionFound = sgItem;
                }
              });
            });
            // If available remove it from previous loaded options (which has hide: true)
            if (hiddenOptionFound) {
              originalOptions = originalOptions.map(function (group, index) {
                return _objectSpread(_objectSpread({}, group), {}, {
                  items: group.items.filter(function (option) {
                    return !((0, _get2["default"])(hiddenOptionFound, '_text', '') === option._text);
                  })
                });
              });
              // Remove it from hiddenoptions
              _this11.hiddenOptions = _this11.hiddenOptions.filter(function (option) {
                return !(option === (0, _get2["default"])(hiddenOptionFound, '_text', ''));
              });
            }
          });
        }
        var resultOptions = originalOptions.slice(0);
        subCollection.forEach(function (group, index) {
          var mergedGroup = null;
          if (!group.title && index < 3) {
            mergedGroup = _objectSpread(_objectSpread({}, group), {}, {
              items: (0, _uniqBy["default"])([].concat(_toConsumableArray(resultOptions[index].items), _toConsumableArray(group.items)), '_text')
            });
            mergedOptionsForGroupsWithoutTitle.push(mergedGroup);
          } else {
            if (group.title) {
              var matchedIndex = resultOptions.findIndex(function (Ogroup) {
                return Ogroup.title === group.title;
              });
              if (matchedIndex > -1) {
                resultOptions[matchedIndex].items = (0, _uniqBy["default"])([].concat(_toConsumableArray(resultOptions[matchedIndex].items), _toConsumableArray(group.items)), '_text');
                mergedGroup = resultOptions[matchedIndex];
              } else {
                resultOptions.push(subCollection[index]);
              }
            }
            mergedOptionsForGroupsWithTitle = resultOptions.slice(3, resultOptions.length);
          }
        });
        return [].concat(mergedOptionsForGroupsWithoutTitle, _toConsumableArray(mergedOptionsForGroupsWithTitle));
      }
    }

    // * [ '', '', '' ]
    // * [ {title, items: [ '', '', '' ] } ,... ]
    // * [ {title, items: [ { text, meta, data } ] } ,... ]
    // * [ {title, items: [ { text, meta, data, columnData } ] }, resultColumns:{text, sortDir, searchable}... ]
    // Show selected options(value prop) in available options 
  }, {
    key: "_getAvailableOptionsWithSelected",
    value: function _getAvailableOptionsWithSelected() {
      var _this12 = this;
      if (!this.initialCollection.filter(function (i) {
        return i.items;
      }).length) {
        return this.originalOptions.map(function (group, index) {
          if (index === 0) return _objectSpread({}, group);
          var groupFilteredItems = group.items.filter(function (c) {
            return _this12.initialCollection.find(function (d) {
              return d && _he["default"].decode(d) === c._text;
            });
          });
          return _objectSpread(_objectSpread({}, group), {}, {
            items: groupFilteredItems
          });
        });
      } else {
        return this.originalOptions.map(function (group, index) {
          if (index === 0) return _objectSpread({}, group);
          var initialCollectionGroup = _this12.initialCollection.reduce(function (acc, g) {
            return [].concat(_toConsumableArray(acc), _toConsumableArray(g.items));
          }, []);
          var groupFilteredItems = [];
          if (initialCollectionGroup.length && _typeof(initialCollectionGroup[0]) == "object") {
            groupFilteredItems = group.items.filter(function (c) {
              return initialCollectionGroup.find(function (d) {
                return d._text && _he["default"].decode(d._text) === c._text;
              });
            });
          } else {
            groupFilteredItems = group.items.filter(function (c) {
              return initialCollectionGroup.find(function (d) {
                return d && _he["default"].decode(d) === c._text;
              });
            });
          }
          return _objectSpread(_objectSpread({}, group), {}, {
            items: groupFilteredItems
          });
        }).filter(function (i) {
          return Object.keys(i).length !== 0;
        });
      }
    }
  }]);
}(_base["default"]);
/**
 * The default properties.
 * @type {Object}
 */
ComplexAutocomplete.prototype.props = {
  el: null,
  id: null,
  value: null,
  label: null,
  add: false,
  required: false,
  disabled: false,
  hasFocus: false,
  onSelect: null,
  invalidOptions: null,
  onDeselect: null,
  delimiter: 'comma',
  // 'comma' // 'semicolon'
  exclude: 'minus',
  // 'minus' // 'exclamation'
  noValue: null,
  splitInput: null,
  highlight: true,
  _isOverflowing: false,
  options: null,
  originalOptions: null,
  replaceDelimiter: '||#|',
  resultColumns: null,
  pageSize: 0,
  loadMoreOptionsOnScroll: false,
  showSelectedOptionsInAvailable: false,
  supportHtmlAsOption: false
};

/**
 * The derived properties.
 * @type {Object}
 */
ComplexAutocomplete.prototype.derived = {
  _selected: {
    deps: ['options'],
    fn: function fn() {
      var _this13 = this;
      if (this.options) {
        if (this.options.filter(function (c) {
          return c.items;
        }).length === 0) {
          return (0, _filter["default"])(this.options, function (o) {
            return o.selected;
          });
        } else {
          var selected = [];
          var isGridParent = checkClosest(this.el, 'eto-grid-edit-cell') !== null ? true : false;
          for (var i = 0; i < this.options.length; i++) {
            selected = [].concat(_toConsumableArray(selected), _toConsumableArray(this.options[i].items.filter(function (c) {
              return c.selected;
            })));
          }
          (0, _each["default"])(selected, function (option) {
            option.exclude = isExcludeTerm(option._text, _this13.exclude, excludeMap, isGridParent);
            option.noValue = !!(_this13.noValue && (_this13.noValue === option._text || _this13.excludeNoValue === option._text));
            if (!_this13.supportHtmlAsOption) {
              option.text = reQuoteAttr(option.text);
              option._text = reQuoteAttr(option._text);
            }
            if (typeof option.data !== 'string') {
              if (option.data && option.data.value) option.data.value = reQuoteAttr(option.data.value);
            } else {
              option.data = reQuoteAttr(option.data);
            }
          });
          return selected;
        }
      }
      return [];
    }
  },
  excludeNoValue: {
    deps: ['noValue'],
    fn: function fn() {
      var excludeChar = excludeMap[this.exclude] || excludeMap['minus'];
      if (this.noValue && this.noValue.length > 0) {
        return excludeChar + this.noValue;
      }
      return null;
    }
  }
};

/**
 * A whitelist of properties that can be set on construction.
 * @type {Array}
 */
ComplexAutocomplete.prototype.whitelist = ['el', 'id', 'name', 'value', 'disabled', 'required', 'label', 'add', 'onSelect', 'onDeselect', 'delimiter', 'exclude', 'highlight', 'placeholder', 'noValue', 'replaceDelimiter', 'resultColumns', 'invalidOptions', 'pageSize', 'loadMoreOptionsOnScroll', 'loadMoreOptionsOnScrollMessage', 'showSelectedOptionsInAvailable', 'supportHtmlAsOption'];

/**
 * The DOM bindings.
 * @type {Object}
 */
ComplexAutocomplete.prototype.bindings = {
  label: {
    type: 'innerHTML',
    selector: '.eto-complex-autocomplete__label'
  },
  id: [{
    type: 'attribute',
    name: 'id',
    selector: '.eto-complex-autocomplete__field'
  }, {
    type: 'attribute',
    name: 'for',
    selector: '.eto-complex-autocomplete__label'
  }],
  name: {
    type: 'attribute',
    name: 'name',
    selector: '.eto-complex-autocomplete__field'
  },
  value: [{
    type: function type(el, value) {},
    selector: '.eto-complex-autocomplete__field'
  }, {
    type: function type(el, value) {
      if (value.length > 0) {
        el.classList.add('has-value');
      } else {
        el.classList.remove('has-value');
      }
    }
  }],
  _isOverflowing: {
    type: 'booleanClass',
    name: 'is-overflowing'
  },
  required: [{
    type: 'booleanAttribute',
    name: 'required',
    selector: '.eto-complex-autocomplete__field'
  }, {
    type: 'booleanClass',
    name: 'required'
  }],
  disabled: [{
    type: 'booleanAttribute',
    name: 'disabled',
    selector: '.eto-complex-autocomplete__field'
  }, {
    type: 'booleanClass',
    name: 'disabled'
  }, {
    type: function type(el, disabled) {
      if (this._tagsPopover) {
        if (disabled) {
          this._tagsPopover.popover.el.classList.add('disabled');
        } else {
          this._tagsPopover.popover.el.classList.remove('disabled');
        }
      }
    }
  }],
  hasFocus: {
    type: 'booleanClass',
    name: 'has-focus'
  },
  delimiter: {
    type: 'attribute',
    name: 'delimiter',
    selector: '.eto-complex-autocomplete__field'
  },
  replaceDelimiter: {
    type: 'attribute',
    name: 'replaceDelimiter',
    selector: '.eto-complex-autocomplete__field'
  }
};

/**
 * DOM event listeners.
 * @type {Object}
 */
ComplexAutocomplete.prototype.events = {
  'click .eto-complex-autocomplete__field': '_onInputFocus',
  'blur .eto-complex-autocomplete__field': '_onInputBlur',
  'keydown .eto-complex-autocomplete__field': '_onKeyDown',
  'keyup .eto-complex-autocomplete__field': '_onInputChange',
  'change .eto-complex-autocomplete__field': '_onInputValueChange',
  'click .eto-complex-autocomplete__clear': 'clearValue',
  'click .eto-tag__remove': '_onRemoveTag',
  'keydown .eto-tag__remove': '_onRemoveTagKeydown',
  'mouseover .eto-tag-hover': '_onTagHover',
  'mouseout .eto-tag-hover': '_onTagHoverOut',
  'mouseover .eto-grid-tag__label': '_onTagHover',
  'mouseout .eto-grid-tag__label': '_onTagHoverOut',
  'mouseover [data-tooltip]': '_onTooltipMouseover',
  'mouseout [data-tooltip]': '_onTooltipMouseout',
  'click .eto-tag__label': '_openSelectedItems',
  'click .eto-grid-tag__label': '_openSelectedItems',
  'click .eto-grid-tag-hover__label': '_openSelectedItems',
  'paste .eto-complex-autocomplete__field': '_onPaste',
  'click .eto-complex-autocomplete__field__addon': 'clearInputValue'
};

/**
 * The default template.
 * @type {Object}
 */
ComplexAutocomplete.prototype.template = _complexAutocomplete["default"];

/**
 * Mixin results behavior.
 */
(0, _mixin["default"])(ComplexAutocomplete.prototype, _resultsComplexMixin["default"], _inputMessage["default"]);

/**
 * Export component.
 */
var _default = exports["default"] = ComplexAutocomplete;
/**
 * Verifies that value is object or string or array of strings or objects.
 * Transforms string to [ string ].
 * Transforms object to [ object ].
 * @param {string|object|array} value
 * @return {array}
 */
function verifyValue(value) {
  var error = false;
  if (Array.isArray(value)) {
    // type check array elements
    for (var i = 0; i < value.length; i++) {
      if (typeof value[i] !== 'string' && 'object' !== _typeof(value[i])) {
        error = true;
        break;
      }
    }
    // remove duplicates
    value = uniq(value);
  } else if (typeof value !== 'undefined') {
    error = true;
  }
  if (error) {
    throw Error('Value must be string or array of strings.');
  }
  return value;
}

/**
 * Provided a value, transforms to array, validates, removes dupes.
 * Returns options with correct values selected.
 * @param {number|string|Array} value - undefined | number | string | array
 * @param {Array} options
 * @param {Boolean} add
 * @return {Array}
 */
function updateOptions(value, options, add) {
  if (typeof value === 'string') {
    value = [value];
  }
  if (Array.isArray(value)) {
    // Remove duplicates
    value = uniq(value);

    // Iterate over options, selecting those that exist in value array.
    return (0, _map["default"])(options, function (o) {
      var _loop3 = function _loop3() {
        var index = value.indexOf(o.items[i]._text);
        o.items[i].selected = false;
        if (index !== -1) {
          var item = o.items.find(function (c) {
            return c._text === value[index];
          });
          item.selected = true;
          if (add && item._add) {
            item.text = item._text;
          }
        }
      };
      for (var i = 0; i < o.items.length; i++) {
        _loop3();
      }
      return o;
    });
  } else {
    return (0, _map["default"])(options, function (o) {
      o.selected = false;
      return o;
    });
  }
}

/**
 * Processes params for initialization.
 * Transforms `value` to array.
 * @param {object} params
 */
function preprocess(params) {
  params = (0, _getIds["default"])(params, {
    id: (0, _getIds.suffixSelector)('__field'),
    messageId: (0, _getIds.suffixSelector)('__message'),
    resultsId: '.eto-results'
  });
  if (params.exclude) {
    var isExclude = (0, _filter["default"])(excludeMap, function (exclude) {
      return exclude === params.exclude;
    });
    if (!isExclude) {
      throw Error("Exclude can only be - or |. ".concat(params.exclude, " cannot be used for exclusion"));
    }
  }
  var inputEl = params.el.querySelector('.eto-complex-autocomplete__field');
  var name = null;
  if (inputEl) name = inputEl.getAttribute('name');
  params.name = params.name || name || '';
  if (!params.replaceDelimiter && inputEl) {
    params.replaceDelimiter = inputEl.getAttribute('replaceDelimiter');
  }
  if (params.el) {
    // infer value
    if (typeof params.value === 'undefined') {
      var valueNodes = params.el.querySelectorAll('input[type="hidden"]');
      var values = [];
      if (valueNodes.length !== 0) {
        for (var i = 0; i < valueNodes.length; i++) {
          values.push(valueNodes[i].value);
        }
      }
      params.value = values;
    }
  }
  params.value = verifyValue(params.value) || [];

  //Index 0 - Exclude list, Index 1: Include list
  if (params.value && params.value.length > 0) {
    var _values = [];
    var columnData = {};
    var textMap = {};
    if (_typeof(params.value[0]) === 'object') {
      _values = params.value.map(function (v) {
        columnData[v.value] = v.columnData;
        textMap[v.value] = v.text;
        return v.value;
      });
      params.value = _values;
    }
    var collection = _toConsumableArray(params.value);
    var excludeList = (0, _filter["default"])(collection, function (item) {
      return isExcludeTerm(item, params.exclude || 'minus', excludeMap);
    });
    var includeList = (0, _filter["default"])(collection, function (item) {
      return !excludeList.find(function (excludeItem) {
        return excludeItem === item;
      });
    });
    collection = [{
      items: []
    }, {
      items: excludeList
    }, {
      items: includeList
    }];
    var excludeChar = excludeMap[params.exclude] || excludeMap['minus'];
    var excludeNoValue = excludeChar + params.noValue;
    (0, _each["default"])(collection, function (group, index) {
      group.items = group.items.map(function (str) {
        return {
          text: textMap[str] || str,
          displayText: textMap[str] || str,
          _text: str,
          data: str,
          selected: true,
          exclude: index === 1,
          noValue: !!(params.noValue && (str === params.noValue || str === excludeNoValue)),
          columnData: columnData && columnData[str] ? columnData[str] : null
        };
      });
    });
    params.options = collection;
    params.originalOptions = params.options.slice(0);
  } else {
    params.options = resetCollection();
    params.originalOptions = params.options.slice(0);
  }
  return params;
}

/**
 * Escapes characters that have special meaning inside a regular expression.
 * @param {String} str
 * @return {String}
 */
function escapeRegexCharacters(str) {
  return str.replace(/[\-\[\]\/{}()*+?.\\^$|]/g, "\\$&");
}

/**
 * Separates a query on whitespace.
 * @param {String} str
 * @return {Array}
 */
function queryTokenizer(str) {
  return str ? str.split(/\s+/) : [];
}

/**
 * Replace parent's children with passed elements.
 * @param {Element} parent
 * @param {Array|NodeList} children
 */
function replaceChildren(parent, children) {
  while (parent.firstChild) parent.removeChild(parent.firstChild);
  (0, _each["default"])(children, function (el) {
    return parent.appendChild(el);
  });
}

/**
 * Returns the count of available options
 */
function getAvailableCount(options, includeSelectedOptions) {
  var itemsLength = 0;
  if (options.filter(function (c) {
    return c.items;
  }).length > 0) {
    itemsLength = 0;
    options.forEach(function (c) {
      var uniqItems = (0, _uniqBy["default"])(c.items, '_text');
      if (uniqItems && uniqItems.length > 0) {
        if (includeSelectedOptions) {
          itemsLength += uniqItems.filter(function (d) {
            return !d._add;
          }).length;
        } else {
          itemsLength += uniqItems.filter(function (d) {
            return !d._add && !d.selected;
          }).length;
        }
      } else if (c.hasMoreRecords) {
        //handler more records
        itemsLength += '+';
      }
    });
  } else {
    var uniqOptions = uniq(options);
    itemsLength = uniqOptions.filter(function (c) {
      return !c.add;
    }).length;
  }
  return itemsLength;
}

/**
 * Check for closest element or element with class (IE and Edge fix)
 * @param {Object} e - event
 * @param {String} s - element or class name
 */
function checkClosest(el, s) {
  if (el.closest) {
    return el.closest(s) || el.closest('.' + s);
  } else {
    do {
      if (el.tagName === s.toUpperCase() || el.classList.contains(s)) return el;
      el = el.parentElement || el.parentNode;
    } while (el !== null && el.nodeType === 1);
    return null;
  }
}

/**
 * Reorder selected in order of selection/value
 */
function processSelectedOptions(selected, value, exclude) {
  var collection = [];
  var items = [];
  var excludeItems = [];
  if (selected.length > 0) {
    value.forEach(function (val) {
      var result = selected.find(function (item) {
        return item._text === val;
      });
      try {
        if (isExcludeTerm(result._text, exclude || 'minus', excludeMap)) excludeItems.push(result);else items.push(result);
      } catch (e) {
        console.info({
          e: e
        });
      }
    });
    collection = [{
      items: []
    }, {
      items: excludeItems
    }, {
      items: items
    }];
  }
  return collection;
}
function reQuoteAttr(s, preserveCR) {
  preserveCR = preserveCR ? '&#13;' : '\n';
  return ('' + s /* Forces the conversion to string. */).replace(/&amp/g, '&') /* This MUST be the 1st replacement. */.replace(/&apos;/g, "'") /* The 4 other predefined entities, required. */.replace(/&quot;/g, '"').replace(/&lt;/g, '<').replace(/&gt;/g, '>')
  /*
  You may add other replacements here for HTML only
  (but it's not necessary).
  Or for XML, only if the named entities are defined in its DTD.
  */.replace(/\r\n/g, preserveCR) /* Must be before the next replacement. */.replace(/[\r\n]/g, preserveCR);
}

/**
 * Add an array of values not in the collection to the collection
 */

function addValuesToOptions(collection, value, add, exclude, noValue, excludeNoValue, options, isGridParent) {
  var optionsInfo = arguments.length > 8 && arguments[8] !== undefined ? arguments[8] : {};
  if (!collection || collection.length === 0) collection = resetCollection();
  var foundVals = [];
  //map values then remove value if present
  collection.forEach(function (group) {
    var arr = value.filter(function (val) {
      return group.items.findIndex(function (item) {
        return item._text === val;
      }) > -1;
    });
    foundVals = (0, _union["default"])(foundVals, arr);
  });
  var vals = (0, _difference["default"])(value, foundVals);
  var columnData = {};
  var textMap = {};
  var addMap = {};
  if (options) {
    (0, _each["default"])(options, function (op) {
      if (op.items) {
        (0, _each["default"])(op.items, function (item) {
          columnData[item._text] = item.columnData;
          textMap[item._text] = item.text;
          addMap[item._text] = item._add;
        });
      } else {
        if (op.columnData) {
          columnData[op.value] = op.columnData;
        }
        if (op.text) {
          textMap[op.value] = op.text;
        }
      }
    });
  }
  var allMatchedResults = optionsInfo.allMatchedResults,
    loadedOptions = optionsInfo.loadedOptions;
  (0, _each["default"])(vals, function (val) {
    var isExclude = isExcludeTerm(val, exclude || 'minus', excludeMap, isGridParent);
    var isNoValue = noValue && (val === noValue || val === excludeNoValue);
    var decodedText = '';
    if (optionsInfo !== null && optionsInfo !== void 0 && optionsInfo.supportHtmlAsOption && val) {
      if (val !== _he["default"].decode(val)) {
        decodedText = _he["default"].decode(val);
      }
    }
    var hide = allMatchedResults && isValueExistsInOptions(val, allMatchedResults);
    if (isExclude) {
      var finalObj = (0, _omitBy["default"])({
        decodedText: decodedText,
        displayText: textMap[val] || val,
        text: textMap[val] || val,
        _text: val,
        data: val,
        exclude: true,
        noValue: isNoValue,
        _add: addMap[val],
        columnData: columnData[val] ? columnData[val] : null,
        hide: hide
      }, ['', undefined]);
      collection[1].items.unshift(finalObj);
    } else {
      var _finalObj = (0, _omitBy["default"])({
        decodedText: decodedText,
        displayText: textMap[val] || val,
        text: textMap[val] || val,
        _text: val,
        data: val,
        exclude: false,
        noValue: isNoValue,
        _add: addMap[val],
        columnData: columnData[val] ? columnData[val] : null,
        hide: hide
      }, ['', undefined]);
      collection[2].items.unshift(_finalObj);
    }
  });
  return collection;
}
function isValueExistsInOptions(val, options) {
  if (typeof options[0] === 'string') {
    return options.includes(val);
  }
  var bool = false;
  options.forEach(function (group) {
    if (!bool) {
      var exists = group.items.find(function (item) {
        return item._text === val || item.text === val;
      });
      if (exists) {
        bool = true;
        return;
      }
    }
  });
  return bool;
}

/**
 * Removes previously added 'add term' that is not yet selected from options, unmarks previously selected 'add term'
 */
function removeAddTerm(collection) {
  if (!collection) return collection;

  //Remove term
  //map values then remove value if present
  collection.forEach(function (group) {
    var arr = group.items.filter(function (item) {
      return !item._add || item._add && item.selected;
    });
    (0, _each["default"])(arr, function (item) {
      if (item._add && item.selected) item._add = false;
    });
    group.items = arr;
  });
  return collection;
}
function isExcludeTerm(str, exclude, excludeMap, isGridParent) {
  var excludeChar = excludeMap[exclude] || excludeMap['minus'];
  return str && str.startsWith(excludeChar) && _typeof(isGridParent) !== undefined && !isGridParent;
}
function resetCollection() {
  return [{
    items: []
  }, {
    items: []
  }, {
    items: []
  }];
}

/**
 * If selected option is invalid, get provided option configuration
 */

function getInvalidOptionInfo(selectedOption, invalidOptions) {
  return (0, _find2["default"])(invalidOptions, function (option) {
    return selectedOption === option.name;
  });
}

/**
 * @param {Array} arr 
 * @returns 
 */
function uniq(arr) {
  try {
    return _toConsumableArray(new Set(arr));
  } catch (e) {
    return [];
  }
}
module.exports = exports.default;


},{"../../../html/templates/precompiled/complex-autocomplete":10,"../helpers/dom-manipulation":26,"../helpers/events/window-events":27,"../helpers/get-ids":28,"../helpers/make-element":29,"../helpers/mixin":30,"../utils/input-message":44,"../utils/results-complex-mixin":47,"./base":18,"./popover":20,"./tooltip":23,"he":53,"lodash/assign":247,"lodash/difference":252,"lodash/each":253,"lodash/filter":256,"lodash/find":257,"lodash/get":262,"lodash/map":286,"lodash/omitBy":292,"lodash/reduce":298,"lodash/remove":300,"lodash/reverse":302,"lodash/union":309,"lodash/uniqBy":310}],20:[function(require,module,exports){
"use strict";

function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _base = _interopRequireDefault(require("./base"));
var _popover = _interopRequireDefault(require("../../../html/templates/precompiled/popover"));
var _windowEvents = _interopRequireDefault(require("../helpers/events/window-events"));
var _getParent = _interopRequireDefault(require("../helpers/traversal/get-parent"));
var _matches = _interopRequireDefault(require("../helpers/traversal/matches"));
var _mixin = _interopRequireDefault(require("../helpers/mixin"));
var _domManipulation = _interopRequireDefault(require("../helpers/dom-manipulation"));
var _affixedMenu = _interopRequireDefault(require("../utils/affixed-menu"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); }
function _callSuper(t, o, e) { return o = _getPrototypeOf(o), _possibleConstructorReturn(t, _isNativeReflectConstruct() ? Reflect.construct(o, e || [], _getPrototypeOf(t).constructor) : o.apply(t, e)); }
function _possibleConstructorReturn(t, e) { if (e && ("object" == _typeof(e) || "function" == typeof e)) return e; if (void 0 !== e) throw new TypeError("Derived constructors may only return object or undefined"); return _assertThisInitialized(t); }
function _assertThisInitialized(e) { if (void 0 === e) throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); return e; }
function _isNativeReflectConstruct() { try { var t = !Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); } catch (t) {} return (_isNativeReflectConstruct = function _isNativeReflectConstruct() { return !!t; })(); }
function _superPropGet(t, e, o, r) { var p = _get(_getPrototypeOf(1 & r ? t.prototype : t), e, o); return 2 & r && "function" == typeof p ? function (t) { return p.apply(o, t); } : p; }
function _get() { return _get = "undefined" != typeof Reflect && Reflect.get ? Reflect.get.bind() : function (e, t, r) { var p = _superPropBase(e, t); if (p) { var n = Object.getOwnPropertyDescriptor(p, t); return n.get ? n.get.call(arguments.length < 3 ? e : r) : n.value; } }, _get.apply(null, arguments); }
function _superPropBase(t, o) { for (; !{}.hasOwnProperty.call(t, o) && null !== (t = _getPrototypeOf(t));); return t; }
function _getPrototypeOf(t) { return _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf.bind() : function (t) { return t.__proto__ || Object.getPrototypeOf(t); }, _getPrototypeOf(t); }
function _inherits(t, e) { if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function"); t.prototype = Object.create(e && e.prototype, { constructor: { value: t, writable: !0, configurable: !0 } }), Object.defineProperty(t, "prototype", { writable: !1 }), e && _setPrototypeOf(t, e); }
function _setPrototypeOf(t, e) { return _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function (t, e) { return t.__proto__ = e, t; }, _setPrototypeOf(t, e); } /**
 * # Popover
 * A component that shows and hides a popover. Should do some sanity checks on positioning as well.
 *
 * @example
 * new Popover({
 *   el: element,
 *   anchorX: 'left|right|center',
 *   anchorY: 'top|bottom|middle',
 *   hideCaret: boolean
 * });
 *
 * @module components/popover.js
 */
/**
 * Popover class methods.
 * @extends Base
 */
var Popover = /*#__PURE__*/function (_Base) {
  function Popover() {
    _classCallCheck(this, Popover);
    return _callSuper(this, Popover, arguments);
  }
  _inherits(Popover, _Base);
  return _createClass(Popover, [{
    key: "init",
    value:
    /**
     * Bind some events that will be callback for window-level DOM events.
     * @param {Object} params Parameters to store.
     */
    function init() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      _superPropGet(Popover, "init", this, 3)([params]);
      this._addWindowListeners();
    }

    /**
     * Clean up the window events on remove.
     * @param {Boolean} keepElement
     */
  }, {
    key: "remove",
    value: function remove(keepElement) {
      this._unaffix();
      _superPropGet(Popover, "remove", this, 3)([keepElement]);
      this._removeWindowListeners();
    }

    /**
     * Cache the caret element.
     */
  }, {
    key: "_cacheElements",
    value: function _cacheElements() {
      this._affixEl = this.el;
      this._parentEl = this._affixEl.parentNode;
      this._caretEl = this.query('[class*="__caret"]');
    }

    /**
     * Add window event listeners so that we can listen for any click that
     * might be valid to open this popover.
     */
  }, {
    key: "_addWindowListeners",
    value: function _addWindowListeners() {
      this._winEvents = new _windowEvents["default"](this);
      this._winEvents.bind('click [data-popover]', '_onToggleClick');
      this._winEvents.bind('click', '_onWindowClick');
    }

    /**
     * Remove window event listeners.
     */
  }, {
    key: "_removeWindowListeners",
    value: function _removeWindowListeners() {
      this._winEvents.remove();
      this._winEvents = null;
    }

    /**
     * Adds a focus trap. Shifts focus to popover.
     * @param {Object} e
     */
  }, {
    key: "_addFocusTrap",
    value: function _addFocusTrap() {
      this._lastFocus = document.activeElement;
      this._trapEl = createFocusTrap();
      this.el.appendChild(this._trapEl);
      this.el.setAttribute('tabindex', '0');
      this.el.style.outline = 'none';
      this.el.focus();
    }

    /**
     * Removes the focus trap. Returns focus to
     * @param {Object} e
     */
  }, {
    key: "_removeFocusTrap",
    value: function _removeFocusTrap() {
      this.el.style.outline = '';
      this.el.removeAttribute('tabindex');
      this.el.removeChild(this._trapEl);
      this._trapEl = null;
      this._lastFocus = null;
    }

    /**
     * When a popover toggle is clicked, toggle the popover.
     * @param {Object} e
     */
  }, {
    key: "_onToggleClick",
    value: function _onToggleClick(e) {
      var el = _domManipulation["default"].getElementMatchingParent(e.target, '[data-popover]', this.el);
      if (el) {
        var query = el.getAttribute('data-popover');
        if ((0, _matches["default"])(this.el, query)) {
          this.anchorEl = el;
          this.toggle();
          if (this._isOpen) {
            this._addFocusTrap();
          } else {
            this._removeFocusTrap();
          }
          e.preventDefault();
        }
      }
    }

    /**
     * Upon focusing the focus trap, we want to return focus to the original element.
     */
  }, {
    key: "_onFocusTrap",
    value: function _onFocusTrap() {
      this._lastFocus.focus();
      this._removeFocusTrap();
    }

    /**
     * When the window is clicked and it's not part of the popover, close the popover.
     * @param {Objec} e
     */
  }, {
    key: "_onWindowClick",
    value: function _onWindowClick(e) {
      if (!this._isOpen) return;
      if (!e.etoPopoverNoClose && e.target !== this.el && !(0, _getParent["default"])(e.target, this.el) && e.target !== this._affix.anchorEl && !(0, _getParent["default"])(e.target, this._affix.anchorEl) && !_domManipulation["default"].getElementMatchingParent(e.target, '[data-affixed]')) {
        this.close();
      }
    }
  }]);
}(_base["default"]);
/**
 * The default properties.
 * @type {Object}
 */
Popover.prototype.props = {
  el: null,
  anchorX: 'center',
  anchorY: 'bottom',
  hideCaret: false,
  _caretEl: null
};

/**
 * A whitelist of properties that can be set on construction.
 * @type {Array}
 */
Popover.prototype.whitelist = ['el', 'anchorX', 'anchorY', 'hideCaret', 'anchorEl', 'rootEl'];

/**
 * The DOM bindings.
 * @type {Object}
 */
Popover.prototype.bindings = {
  hideCaret: {
    type: 'booleanClass',
    name: 'hide-caret'
  }
};

/**
 * The DOM events.
 * @type {Object}
 */
Popover.prototype.events = {
  'focus .focus-trap': '_onFocusTrap'
};

/**
 * The default template.
 * @type {Object}
 */
Popover.prototype.template = _popover["default"];
(0, _mixin["default"])(Popover.prototype, _affixedMenu["default"]);
var _default = exports["default"] = Popover;
/**
 * Creates an element that we can listen for focus and close popover.
 * @return {Element}
 */
function createFocusTrap() {
  var trap = document.createElement('div');
  trap.setAttribute('class', 'focus-trap');
  trap.setAttribute('tabindex', '0');
  trap.style.height = '0px';
  trap.style.outline = 'none';
  return trap;
}
module.exports = exports.default;


},{"../../../html/templates/precompiled/popover":11,"../helpers/dom-manipulation":26,"../helpers/events/window-events":27,"../helpers/mixin":30,"../helpers/traversal/get-parent":36,"../helpers/traversal/matches":37,"../utils/affixed-menu":38,"./base":18}],21:[function(require,module,exports){
"use strict";

function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _base = _interopRequireDefault(require("./base"));
var _windowEvents = _interopRequireDefault(require("../helpers/events/window-events"));
var _request = _interopRequireDefault(require("../helpers/animation/request"));
var _cancel = _interopRequireDefault(require("../helpers/animation/cancel"));
var _domManipulation = _interopRequireDefault(require("../helpers/dom-manipulation"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); }
function _callSuper(t, o, e) { return o = _getPrototypeOf(o), _possibleConstructorReturn(t, _isNativeReflectConstruct() ? Reflect.construct(o, e || [], _getPrototypeOf(t).constructor) : o.apply(t, e)); }
function _possibleConstructorReturn(t, e) { if (e && ("object" == _typeof(e) || "function" == typeof e)) return e; if (void 0 !== e) throw new TypeError("Derived constructors may only return object or undefined"); return _assertThisInitialized(t); }
function _assertThisInitialized(e) { if (void 0 === e) throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); return e; }
function _isNativeReflectConstruct() { try { var t = !Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); } catch (t) {} return (_isNativeReflectConstruct = function _isNativeReflectConstruct() { return !!t; })(); }
function _superPropGet(t, e, o, r) { var p = _get(_getPrototypeOf(1 & r ? t.prototype : t), e, o); return 2 & r && "function" == typeof p ? function (t) { return p.apply(o, t); } : p; }
function _get() { return _get = "undefined" != typeof Reflect && Reflect.get ? Reflect.get.bind() : function (e, t, r) { var p = _superPropBase(e, t); if (p) { var n = Object.getOwnPropertyDescriptor(p, t); return n.get ? n.get.call(arguments.length < 3 ? e : r) : n.value; } }, _get.apply(null, arguments); }
function _superPropBase(t, o) { for (; !{}.hasOwnProperty.call(t, o) && null !== (t = _getPrototypeOf(t));); return t; }
function _getPrototypeOf(t) { return _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf.bind() : function (t) { return t.__proto__ || Object.getPrototypeOf(t); }, _getPrototypeOf(t); }
function _inherits(t, e) { if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function"); t.prototype = Object.create(e && e.prototype, { constructor: { value: t, writable: !0, configurable: !0 } }), Object.defineProperty(t, "prototype", { writable: !1 }), e && _setPrototypeOf(t, e); }
function _setPrototypeOf(t, e) { return _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function (t, e) { return t.__proto__ = e, t; }, _setPrototypeOf(t, e); } /**
 * # Scrollable
 * Drag an element around, with optional constraints.
 *
 * @example
 * new Scrollable({
 *   el: element,
 *   axis: 'xy|x|y'
 * });
 *
 * @module components/scrollable.js
 */
var DRAG_LIMIT = 30; // How far past the boundaries can we drag, in pixels.
var DRAG_RESISTANCE = 25; // The multiplication factor for drag elasticity.
var VELOCITY_MIN = 3;
var TIME_CONST = 325;

/**
 * Scrollable class methods.
 * @extends Base
 */
var Scrollable = /*#__PURE__*/function (_Base) {
  function Scrollable() {
    _classCallCheck(this, Scrollable);
    return _callSuper(this, Scrollable, arguments);
  }
  _inherits(Scrollable, _Base);
  return _createClass(Scrollable, [{
    key: "start",
    value:
    /**
     * Start the drag.
     * @param {String} type
     */
    function start(type) {
      if (this._isMoving) return;
      if (this._scrollRequest) (0, _cancel["default"])(this._scrollRequest);
      this._addWindowListeners(type);
      this._disableSelection();
      this._isMoving = true;
    }

    /**
     * Stop the move.
     */
  }, {
    key: "stop",
    value: function stop() {
      if (!this._isMoving) return;
      this._scrollAtVelocity();
      this._enableSelection();
      this._removeWindowListeners();
      this._moves = null;
      this._isMoving = false;
    }

    /**
     * Move the scroll element by a given number of pixels.
     * @param {Object} offset
     */
  }, {
    key: "move",
    value: function move(offset) {
      this.set(this._getMinsMaxes());
      this.set({
        _x: this._x + offset.x,
        _y: this._y + offset.y
      });
      this._updatePosition();
    }

    /**
     * Move the scroll element to a particular point.
     * @param {Object} points
     */
  }, {
    key: "moveTo",
    value: function moveTo(points) {
      var offset = {
        x: points.x || 0,
        y: points.y || 0
      };
      return this.move(this._getMoveDelta(offset, {
        x: this._x,
        y: this._y
      }));
    }

    /**
     * Scroll the element by a given number of pixels.
     * @param {Object} offset
     */
  }, {
    key: "scroll",
    value: function scroll(offset) {
      this.set(this._getMinsMaxes());
      this.set({
        _x: this._x + offset.x,
        _y: this._y + offset.y
      });
      this._conformPosition();
    }

    /**
     * Scroll to make sure an element is in view.
     * @param {Element} el
     */
  }, {
    key: "scrollToElement",
    value: function scrollToElement(el) {
      return this.ensureInView({
        x: el.offsetLeft,
        y: el.offsetTop,
        width: el.offsetWidth,
        height: el.offsetHeight
      });
    }

    /**
     * Ensure that a set of points are in view and scroll them into view if not.
     * @param {Object} points
     */
  }, {
    key: "ensureInView",
    value: function ensureInView(points) {
      var newPoints = {
        x: this._x,
        y: this._y
      };
      var _a = points.x;
      var _b = _a + points.width;
      var _c = points.y;
      var _d = _c + points.height;
      var a = -this._x;
      var b = a + this.el.clientWidth;
      var c = -this._y;
      var d = c + this.el.clientHeight;
      if (_a < a) newPoints.x = -_a; // Align to the left
      if (_b > b) newPoints.x = b - _b - a; // Align to the right
      if (_c < c) newPoints.y = -_c; // Align to the top
      if (_d > d) newPoints.y = d - _d - c; // Align to the bottom

      this.moveTo(newPoints);
    }

    /**
     * Clean up the window events on remove.
     * @param {Boolean} keepElement
     */
  }, {
    key: "remove",
    value: function remove(keepElement) {
      _superPropGet(Scrollable, "remove", this, 3)([keepElement]);
      this._removeWindowListeners();
    }

    /**
     * Add window event listeners so that we can track mouse and touch moves outside of
     * our element.
     */
  }, {
    key: "_addWindowListeners",
    value: function _addWindowListeners(type) {
      this._winEvents = new _windowEvents["default"](this);
      if (type === 'mouse') {
        this._winEvents.bind('mousemove', '_onMouseMove');
        this._winEvents.bind('mouseup', '_onMouseUp');
      } else {
        this._winEvents.bind('touchmove', '_onTouchMove');
        this._winEvents.bind('touchend', '_onTouchEnd');
      }
    }

    /**
     * Remove window event listeners.
     */
  }, {
    key: "_removeWindowListeners",
    value: function _removeWindowListeners() {
      if (!this._winEvents) return;
      this._winEvents.remove();
      this._winEvents = null;
    }

    /**
     * Add a move to the list. The move list is used to compute velocity.
     */
  }, {
    key: "_addMove",
    value: function _addMove(move) {
      this._moves = this._moves || [];
      this._moves.push(move);
      this._updateVelocities();
    }

    /**
     * Update the position of the container element.
     */
  }, {
    key: "_updatePosition",
    value: function _updatePosition() {
      var x = Math.min(Math.max(this._x, this._minX), this._maxX);
      var y = Math.min(Math.max(this._y, this._minY), this._maxY);
      var modX = 0;
      var modY = 0;
      var atStartX = false;
      var atStartY = false;
      var atEndX = false;
      var atEndY = false;

      // Over-dragged on the x-axis
      if (this._x > this._maxX) {
        modX = Math.min(this._x / DRAG_RESISTANCE, DRAG_LIMIT);
        atEndX = true;
      } else if (this._minX < 0 && this._x < this._minX) {
        modX = Math.max((this._x - this._minX) / DRAG_RESISTANCE, -DRAG_LIMIT);
        atStartX = true;
      }

      // Over-dragged on the y-axis
      if (this._y > this._maxY) {
        modY = Math.min(this._y / DRAG_RESISTANCE, DRAG_LIMIT);
        atEndY = true;
      } else if (this._minY < 0 && this._y < this._minY) {
        modY = Math.max((this._y - this._minY) / DRAG_RESISTANCE, -DRAG_LIMIT);
        atStartY = true;
      }
      x = x + modX;
      y = y + modY;
      this.position = {
        x: x,
        y: y,
        minX: this._minX,
        maxX: this._maxX,
        minY: this._minY,
        maxY: this._maxY,
        atStartX: atStartX,
        atStartY: atStartY,
        atEndX: atEndX,
        atEndY: atEndY
      };
      translate(this._containerEl, x, y, this.axis);
    }

    /**
     * Conform the position by setting x and y to be within bounds.
     */
  }, {
    key: "_conformPosition",
    value: function _conformPosition() {
      this.set({
        _x: Math.min(Math.max(this._x, this._minX), this._maxX),
        _y: Math.min(Math.max(this._y, this._minY), this._maxY)
      });
      this._updatePosition();
    }

    /**
     * Determine the current velocity.
     */
  }, {
    key: "_updateVelocities",
    value: function _updateVelocities() {
      var moves = this._moves;
      var avgX = 0;
      var avgY = 0;
      var m = Math.min(6, moves.length - 1);
      for (var i = 1; i < m; i++) {
        if (moves[moves.length - i].time === moves[moves.length - i - 1].time) {
          avgX += avgX / i;
          avgY += avgY / i;
        } else {
          avgX += 10 * (moves[moves.length - i].x - moves[moves.length - i - 1].x) / (moves[moves.length - i].time - moves[moves.length - i - 1].time) / m;
          avgY += 10 * (moves[moves.length - i].y - moves[moves.length - i - 1].y) / (moves[moves.length - i].time - moves[moves.length - i - 1].time) / m;
        }
      }
      this._velocityX = avgX;
      this._velocityY = avgY;
    }

    /**
     * Scroll the element at the current velocities.
     */
  }, {
    key: "_scrollAtVelocity",
    value: function _scrollAtVelocity() {
      // Not moving fast enough, just conform the position now.
      if (Math.abs(this._velocityX) < VELOCITY_MIN && Math.abs(this._velocityY) < VELOCITY_MIN) {
        return this._conformPosition();
      }
      var amplitudeX = 10 * this._velocityX; // This determines friction basically. Was originally 0.8. Need to tweak.
      var amplitudeY = 10 * this._velocityY; // This determines friction basically. Was originally 0.8. Need to tweak.
      var scrollTargetX = Math.round(this._x + amplitudeX);
      var scrollTargetY = Math.round(this._y + amplitudeY);
      var scrollTimestamp = Date.now();
      this._scrollRequest = (0, _request["default"])(scroll.bind(this));
      function scroll() {
        var elapsed = Date.now() - scrollTimestamp;
        var exp = Math.exp(-elapsed / TIME_CONST);
        var deltaX = -amplitudeX * exp;
        var deltaY = -amplitudeY * exp;
        var x = Math.abs(deltaX) > 0.5 && !this._atXBoundary ? scrollTargetX + deltaX : null;
        var y = Math.abs(deltaY) > 0.5 && !this._atYBoundary ? scrollTargetY + deltaY : null;
        this._x = x || this._x;
        this._y = y || this._y;

        // We have more to do, so loop back through.
        if (x || y) {
          this._updatePosition();
          this._scrollRequest = (0, _request["default"])(scroll.bind(this));
        } else {
          this._scrollRequest = null;
          this._conformPosition();
        }
      }
    }

    /**
     * Cache elements.
     */
  }, {
    key: "_cacheElements",
    value: function _cacheElements() {
      this._containerEl = this.el.children[0];
      if (!this._containerEl) throw new Error('Scrollable element must have a child to actually scroll.');
      this.set(this._getMinsMaxes());
      this._conformPosition();
    }

    /**
     * Get mins and maxes.
     * @return {Object}
     */
  }, {
    key: "_getMinsMaxes",
    value: function _getMinsMaxes() {
      return {
        _minX: this.el.clientWidth - this._containerEl.clientWidth,
        _maxX: 0,
        _minY: this.el.clientHeight - this._containerEl.clientHeight,
        _maxY: 0
      };
    }

    /**
     * Get the difference between the x and y values of the two given moves.
     * @param {Object} a The new move.
     * @param {Object} b The old move.
     */
  }, {
    key: "_getMoveDelta",
    value: function _getMoveDelta(a, b) {
      return {
        x: a.x - b.x,
        y: a.y - b.y,
        time: a.time - b.time
      };
    }

    /**
     * Compute a move relative to the last and then move to that position.
     * @param {Object} move
     */
  }, {
    key: "_computeAndMove",
    value: function _computeAndMove(move) {
      var last = this._moves[this._moves.length - 1];
      this._addMove(move);
      this.move(this._getMoveDelta(move, last));
    }

    /**
     * Has the touchmoved far enought to be considered a move.
     * @param {Number} x
     * @param {Number} y
     * @return {Boolean}
     */
  }, {
    key: "_hasMoved",
    value: function _hasMoved(x, y) {
      return this._moves && this._moves.length > 4;
    }

    /**
     * Disable selection of text on the body.
     */
  }, {
    key: "_disableSelection",
    value: function _disableSelection() {
      _domManipulation["default"].addClass(document.body, 'disable-user-select');
    }

    /**
     * Enable selection of text on the body.
     */
  }, {
    key: "_enableSelection",
    value: function _enableSelection() {
      _domManipulation["default"].removeClass(document.body, 'disable-user-select');
    }

    /**
     * When a touch event starts, start dragging if we match.
     * @param {Object} e
     */
  }, {
    key: "_onTouchStart",
    value: function _onTouchStart(e) {
      this._addMove({
        x: e.touches[0].clientX,
        y: e.touches[0].clientY,
        time: e.timeStamp
      });
      this.start('touch');
    }

    /**
     * When the touch moves, update the position.
     * @param  {Object} e
     */
  }, {
    key: "_onTouchMove",
    value: function _onTouchMove(e) {
      var x = e.touches[0].clientX;
      var y = e.touches[0].clientY;
      this._computeAndMove({
        x: x,
        y: y,
        time: e.timeStamp
      });
      this._lastMoveEndTime = this._hasMoved(x, y) ? Date.now() : 0;
    }

    /**
     * Stop dragging.
     * @param  {Object} e
     */
  }, {
    key: "_onTouchEnd",
    value: function _onTouchEnd(e) {
      // touchend has no touches

      // this._addMove({
      //   x: e.touches[0].clientX,
      //   y: e.touches[0].clientY,
      //   time: e.timeStamp
      // });

      // this._lastMoveEndTime = this._hasMoved(e.touches[0].clientX, e.touches[0].clientY) ? Date.now() : 0;

      this.stop();
    }

    /**
     * When a mousedown event fires, start dragging if we match.
     * @param {Object} e
     */
  }, {
    key: "_onMouseDown",
    value: function _onMouseDown(e) {
      if (e.button !== 0) return;
      this._addMove({
        x: e.clientX,
        y: e.clientY,
        time: e.timeStamp
      });
      this.start('mouse');
    }

    /**
     * When the mouse moves, update the position.
     * @param {Object} e
     */
  }, {
    key: "_onMouseMove",
    value: function _onMouseMove(e) {
      this._computeAndMove({
        x: e.clientX,
        y: e.clientY,
        time: e.timeStamp
      });
    }

    /**
     * Stop dragging.
     * @param {Object} e
     */
  }, {
    key: "_onMouseUp",
    value: function _onMouseUp(e) {
      if (e.button !== 0) return;
      this._addMove({
        x: e.clientX,
        y: e.clientY,
        time: e.timeStamp
      });
      this._lastMoveEndTime = this._hasMoved(e.clientX, e.clientY) ? Date.now() : 0;
      this.stop();
    }

    /**
     * Prevent click events if we're dragging.
     * @param {Object} e
     */
  }, {
    key: "_onClick",
    value: function _onClick(e) {
      if (this._lastMoveEndTime && Date.now() - this._lastMoveEndTime < 50) {
        e.preventDefault();
        e.stopPropagation();
      }
    }
  }]);
}(_base["default"]);
/**
 * The default properties.
 * @type {Object}
 */
Scrollable.prototype.props = {
  el: null,
  axis: 'xy',
  position: null,
  _x: 0,
  _y: 0,
  _velocityX: 0,
  _velocityY: 0,
  _minX: 0,
  _maxX: 0,
  _minY: 0,
  _maxY: 0,
  _containerEl: null,
  _scrollRequest: null,
  _moves: null,
  _lastMoveEndTime: 0,
  _winEvents: null,
  _isMoving: false
};

/**
 * A whitelist of properties that can be set on construction.
 * @type {Array}
 */
Scrollable.prototype.whitelist = ['el', 'axis'];

/**
 * DOM event listeners.
 * @type {Object}
 */
Scrollable.prototype.events = {
  'touchstart': '_onTouchStart',
  'mousedown': '_onMouseDown',
  'click': '_onClick'
};

/**
 * The DOM bindings.
 * @type {Object}
 */
Scrollable.prototype.bindings = {
  _isScrolling: {
    type: 'booleanClass',
    name: 'scrolling'
  }
};

/**
 * Derived properties.
 * @type {Object}
 */
Scrollable.prototype.derived = {
  _isScrolling: {
    deps: ['_isMoving', '_scrollRequest'],
    fn: function fn() {
      return this._isMoving || this._scrollRequest;
    }
  },
  _atXBoundary: {
    deps: ['_x', '_maxX', '_minX'],
    fn: function fn() {
      return this._x >= this._maxX || this._x <= this._minX;
    }
  },
  _atYBoundary: {
    deps: ['_y', '_maxY', '_minY'],
    fn: function fn() {
      return this._y >= this._maxY || this._y <= this._minY;
    }
  }
};

/**
 * Set the CSS translate properties on an element.
 * @param  {Element} el
 * @param  {Number} x
 * @param  {Number} y
 * @param  {String} axis
 */
function translate(el, x, y, axis) {
  x = !isNaN(x) ? x : 0;
  y = !isNaN(y) ? y : 0;
  var str = "translate(".concat(x, "px, ").concat(y, "px)");
  if (axis === 'x') {
    str = "translateX(".concat(x, "px)");
  } else if (axis === 'y') {
    str = "translateY(".concat(y, "px)");
  }
  el.style.webkitTransform = str;
  el.style.MozTransform = str;
  el.style.msTransform = str;
  el.style.OTransform = str;
  el.style.transform = str;
}
var _default = exports["default"] = Scrollable;
module.exports = exports.default;


},{"../helpers/animation/cancel":24,"../helpers/animation/request":25,"../helpers/dom-manipulation":26,"../helpers/events/window-events":27,"./base":18}],22:[function(require,module,exports){
"use strict";

function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _base = _interopRequireDefault(require("./base"));
var _tabs = _interopRequireDefault(require("../../../html/templates/precompiled/tabs"));
var _tab = _interopRequireDefault(require("../../../html/templates/precompiled/tab"));
var _windowEvents = _interopRequireDefault(require("../helpers/events/window-events"));
var _scrollable = _interopRequireDefault(require("./scrollable"));
var _domManipulation = _interopRequireDefault(require("../helpers/dom-manipulation"));
var _getParent = _interopRequireDefault(require("../helpers/traversal/get-parent"));
var _makeElement = _interopRequireDefault(require("../helpers/make-element"));
var _debounce = _interopRequireDefault(require("lodash/debounce"));
var _isElement = _interopRequireDefault(require("lodash/isElement"));
var _each = _interopRequireDefault(require("lodash/each"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _defineProperty(e, r, t) { return (r = _toPropertyKey(r)) in e ? Object.defineProperty(e, r, { value: t, enumerable: !0, configurable: !0, writable: !0 }) : e[r] = t, e; }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); }
function _callSuper(t, o, e) { return o = _getPrototypeOf(o), _possibleConstructorReturn(t, _isNativeReflectConstruct() ? Reflect.construct(o, e || [], _getPrototypeOf(t).constructor) : o.apply(t, e)); }
function _possibleConstructorReturn(t, e) { if (e && ("object" == _typeof(e) || "function" == typeof e)) return e; if (void 0 !== e) throw new TypeError("Derived constructors may only return object or undefined"); return _assertThisInitialized(t); }
function _assertThisInitialized(e) { if (void 0 === e) throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); return e; }
function _isNativeReflectConstruct() { try { var t = !Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); } catch (t) {} return (_isNativeReflectConstruct = function _isNativeReflectConstruct() { return !!t; })(); }
function _superPropGet(t, e, o, r) { var p = _get(_getPrototypeOf(1 & r ? t.prototype : t), e, o); return 2 & r && "function" == typeof p ? function (t) { return p.apply(o, t); } : p; }
function _get() { return _get = "undefined" != typeof Reflect && Reflect.get ? Reflect.get.bind() : function (e, t, r) { var p = _superPropBase(e, t); if (p) { var n = Object.getOwnPropertyDescriptor(p, t); return n.get ? n.get.call(arguments.length < 3 ? e : r) : n.value; } }, _get.apply(null, arguments); }
function _superPropBase(t, o) { for (; !{}.hasOwnProperty.call(t, o) && null !== (t = _getPrototypeOf(t));); return t; }
function _getPrototypeOf(t) { return _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf.bind() : function (t) { return t.__proto__ || Object.getPrototypeOf(t); }, _getPrototypeOf(t); }
function _inherits(t, e) { if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function"); t.prototype = Object.create(e && e.prototype, { constructor: { value: t, writable: !0, configurable: !0 } }), Object.defineProperty(t, "prototype", { writable: !1 }), e && _setPrototypeOf(t, e); }
function _setPrototypeOf(t, e) { return _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function (t, e) { return t.__proto__ = e, t; }, _setPrototypeOf(t, e); } /**
 * # Tabs
 * A component that lists tabs and displays tab content on click.
 *
 * @example
 * new Tabs({
 *   el: ...,
 *   items: [{
 *     selector:""
 *     content:""
 *     active:""
 *     removeable:""
 *   }, ...]
 * });
 *
 * @module components/tabs.js
 */
var TAB_CLASS = 'eto-tabs__tab';
var ACTIVE_TAB_CLASS = 'eto-tabs__tab--active';
var ACTIVE_CONTENT_CLASS = 'eto-tab-content__item--active';
var TAB_CLOSE_CLASS = 'eto-tabs__tab-close';
var CONTAINER_CLASS = 'eto-tabs__container';
var SCROLL_CLASS = 'eto-tabs__scroll';
var OVERFLOWING_CLASS = 'eto-tabs--is-overflowing';
var BACKWARD_CLASS = 'eto-tabs__btn--backward';
var FORWARD_CLASS = 'eto-tabs__btn--forward';
var AFFIXED_CLASS = 'eto-tabs__tab--affixed-';
var PLACEHOLDER_CLASS = 'eto-tabs__tab-placeholder';

/**
 * Tabs class methods.
 * @extends Base
 */
var Tabs = /*#__PURE__*/function (_Base) {
  function Tabs() {
    _classCallCheck(this, Tabs);
    return _callSuper(this, Tabs, arguments);
  }
  _inherits(Tabs, _Base);
  return _createClass(Tabs, [{
    key: "init",
    value:
    /**
     * Setup window events to listen for resizes.
     */
    function init(params) {
      _superPropGet(Tabs, "init", this, 3)([preprocess(params)]);
      this._addWindowListeners();
      this._cacheDimensions();
      this._determineOverflow();
      this.listenTo(this, 'change:_activeTab', this._onActiveTabChange);
      this._scrollToActiveTab();
    }

    /**
     * Clean up the window events on remove.
     * @param {Boolean} keepElement
     */
  }, {
    key: "remove",
    value: function remove(keepElement) {
      _superPropGet(Tabs, "remove", this, 3)([keepElement]);
      this._removeWindowListeners();
      this._removeScrollable();
    }

    /**
     * Set the active tab.
     * @param {Element|Number} tab
     */
  }, {
    key: "setActive",
    value: function setActive(tab) {
      // If this is a number, treat as an index.
      tab = typeof tab === 'number' ? this.queryAll(".".concat(TAB_CLASS))[tab] : tab;

      // Already active or not existing.
      if (!tab || tab === this._activeTab) return;
      this._unaffixActiveTab();
      this._hideActiveTabContent();
      this._activeTab = tab;
      this._showActiveTabContent();
    }

    /**
     * Get the active tab.
     */
  }, {
    key: "getActive",
    value: function getActive() {
      return this._activeTab;
    }

    /**
     * Disable a tab.
     * @param {Element|Number} tab
     */
  }, {
    key: "disableTab",
    value: function disableTab(tab) {
      tab = typeof tab === 'number' ? this.queryAll(".".concat(TAB_CLASS))[tab] : tab;
      if (!tab) return;
      _domManipulation["default"].addAttribute(tab, 'disabled');
      _domManipulation["default"].setAttribute(tab, 'tabindex', '-1');
    }

    /**
     * Enable a tab.
     * @param {Element|Number} tab
     */
  }, {
    key: "enableTab",
    value: function enableTab(tab) {
      tab = typeof tab === 'number' ? this.queryAll(".".concat(TAB_CLASS))[tab] : tab;
      if (!tab) return;
      _domManipulation["default"].removeAttribute(tab, 'disabled');
      _domManipulation["default"].setAttribute(tab, 'tabindex', '0');
    }

    /**
     * Add a tab to the list.
     * @param {Element|Object} tab
     * @param {Number} index
     */
  }, {
    key: "addTab",
    value: function addTab(tab, index) {
      tab = (0, _isElement["default"])(tab) ? tab : (0, _makeElement["default"])(_tab["default"].render({
        item: tab
      }));
      var currentChild = index !== undefined && this._scrollEl.children[index];
      if (!currentChild) {
        this._scrollEl.appendChild(tab);
      } else {
        this._scrollEl.insertBefore(tab, currentChild);
      }
      this._onResize();
    }

    /**
     * Remove a tab from the list.
     */
  }, {
    key: "removeTab",
    value: function removeTab(tab) {
      tab = typeof tab === 'number' ? this.queryAll(".".concat(TAB_CLASS))[tab] : tab;
      if (tab === this._activeTab) {
        this._removeActiveTabPlaceholder();
        this.setActive(tab.previousElementSibling || tab.nextElementSibling);
      }
      tab.parentNode.removeChild(tab);
      this._onResize();
    }

    /**
     * Cache child elements when the main element changes.
     */
  }, {
    key: "_cacheElements",
    value: function _cacheElements() {
      this._containerEl = this.query(".".concat(CONTAINER_CLASS));
      this._scrollEl = this.query(".".concat(SCROLL_CLASS));
      this._initScrollable();
      this._determineActivetab();
    }

    /**
     * Add window event listeners to listen for resizes so we can track
     * whether to show buttons or not.
     */
  }, {
    key: "_addWindowListeners",
    value: function _addWindowListeners() {
      this._winEvents = new _windowEvents["default"](this);
      this._winEvents.bind('resize', '_onResize');
      this._winEvents.bind('click', '_onWindowClick');
      this._winEvents.bind('blur', '_onWindowClick');
    }

    /**
     * Remove window event listeners.
     */
  }, {
    key: "_removeWindowListeners",
    value: function _removeWindowListeners() {
      if (!this._winEvents) return;
      this._winEvents.remove();
      this._winEvents = null;
    }
  }, {
    key: "_onWindowClick",
    value: function _onWindowClick(e) {
      if (!this._containsElement(e.target)) {
        this._scrollToActiveTab();
      }
    }
  }, {
    key: "_containsElement",
    value: function _containsElement(el) {
      return el === this.el || (0, _getParent["default"])(el, this.el);
    }

    /**
     * Add the scrollable helper to facilitate scrolling through overflowing tabs.
     */
  }, {
    key: "_initScrollable",
    value: function _initScrollable() {
      if (!this._scrollable) {
        this._scrollable = new _scrollable["default"]({
          el: this._containerEl,
          axis: 'x'
        });
      } else {
        this._scrollable.el = this._containerEl;
      }
    }

    /**
     * Remove the scrollable.
     */
  }, {
    key: "_removeScrollable",
    value: function _removeScrollable() {
      this._scrollable.remove();
      this._scrollable = null;
    }

    /**
     * Try to determine the active tab.
     */
  }, {
    key: "_determineActivetab",
    value: function _determineActivetab() {
      this.setActive(this.query(".".concat(ACTIVE_TAB_CLASS)) || this.query(".".concat(TAB_CLASS)));
    }

    /**
     * Determine if the tabs are overflowing and we should show the nav.
     */
  }, {
    key: "_determineOverflow",
    value: function _determineOverflow() {
      this._isOverflowing = false;
      this._isOverflowing = this._containerElWidth < this._scrollElWidth;
    }

    /**
     * Show content that matches the active tab.
     */
  }, {
    key: "_showActiveTabContent",
    value: function _showActiveTabContent() {
      if (!this._activeTab) return;
      var content = document.querySelectorAll(this._activeTab.getAttribute('data-tab'));
      (0, _each["default"])(content, function (c) {
        _domManipulation["default"].addClass(c, ACTIVE_CONTENT_CLASS);
      });
    }

    /**
     * Hide content that matches the active tab.
     */
  }, {
    key: "_hideActiveTabContent",
    value: function _hideActiveTabContent() {
      if (!this._activeTab) return;
      var content = document.querySelectorAll(this._activeTab.getAttribute('data-tab'));
      (0, _each["default"])(content, function (c) {
        _domManipulation["default"].removeClass(c, ACTIVE_CONTENT_CLASS);
      });
    }

    /**
     * Cache the sizes of the active tab and the container.
     */
  }, {
    key: "_cacheDimensions",
    value: function _cacheDimensions() {
      this._containerElWidth = this._containerEl && this._containerEl.clientWidth;
      this._scrollElWidth = this._scrollEl && this._scrollEl.clientWidth;
    }

    /**
     * Scroll to a tab.
     * @param {Element} tab
     */
  }, {
    key: "_scrollToTab",
    value: function _scrollToTab(tab) {
      this._scrollable.scrollToElement(tab);
    }

    /**
     * Scroll to the active tab.
     */
  }, {
    key: "_scrollToActiveTab",
    value: function _scrollToActiveTab() {
      this._activeTab && this._scrollToTab(this._activeTab);
    }

    /**
     * Affix the active tab to the start or end of the scroll.
     */
  }, {
    key: "_affixActiveTab",
    value: function _affixActiveTab() {
      var type = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 'start';
      var x = arguments.length > 1 ? arguments[1] : undefined;
      if (!this._activeTab) return;
      this._insertActiveTabPlaceholder();
      this._affixedType = type;
      _domManipulation["default"].addClass(this._activeTab, AFFIXED_CLASS + type);
      this._activeTab.style.left = x + 'px';
    }

    /**
     * Unaffix the active tab.
     */
  }, {
    key: "_unaffixActiveTab",
    value: function _unaffixActiveTab() {
      if (!this._activeTab) return;
      this._removeActiveTabPlaceholder();
      _domManipulation["default"].removeClass(this._activeTab, [AFFIXED_CLASS + 'start', AFFIXED_CLASS + 'end']);
      this._affixedType = null;
      this._activeTab.style.left = '';
    }

    /**
     * Add a placeholder that is the same size as the active tab.
     * @return {[type]} [description]
     */
  }, {
    key: "_insertActiveTabPlaceholder",
    value: function _insertActiveTabPlaceholder() {
      if (!this._activeTab) return;
      this._tabPlaceholder = this._tabPlaceholder || (0, _makeElement["default"])("<span class=\"".concat(PLACEHOLDER_CLASS, "\"><span></span></span>"));
      if (this._tabPlaceholder.previousElementSibling !== this._activeTab) {
        this._activeTab.parentNode.insertBefore(this._tabPlaceholder, this._activeTab);
      }
      this._updatePlaceholderSize();
    }

    /**
     * Remove the placeholder for the active tab if it exists.
     */
  }, {
    key: "_removeActiveTabPlaceholder",
    value: function _removeActiveTabPlaceholder() {
      if (!this._activeTab || !this._tabPlaceholder || !this._tabPlaceholder.parentNode) return;
      this._tabPlaceholder.parentNode.removeChild(this._tabPlaceholder);
    }

    /**
     * Update the size of the active tab placeholder to match.
     */
  }, {
    key: "_updatePlaceholderSize",
    value: function _updatePlaceholderSize() {
      if (!this._activeTab || !this._tabPlaceholder) return;
      this._tabPlaceholder.children[0].style.width = this._activeTab.clientWidth + 'px';
      this._tabPlaceholder.children[0].style.height = this._activeTab.clientHeight + 'px';
    }

    /**
     * When the active tab changes, emit an event.
     */
  }, {
    key: "_onActiveTabChange",
    value: function _onActiveTabChange() {
      this.trigger('activeChanged', this._activeTab, this._oldProps._activeTab);
      this._scrollToActiveTab();
    }

    /**
     * When a tab is clicked, make it active.
     * @param  {Object} e
     */
  }, {
    key: "_onTabClick",
    value: function _onTabClick(e) {
      if (e.etoCloseClicked || _domManipulation["default"].hasAttribute(e.target, 'disabled')) return;
      this.setActive(_domManipulation["default"].getElementMatchingParent(e.target, '.' + TAB_CLASS, this.el));
    }

    /**
     * On enter or space, simulate click.
     * @param {Event} e
     */
  }, {
    key: "_onTabKeypress",
    value: function _onTabKeypress(e) {
      if (e.keyCode == 32 || e.keyCode == 13) {
        e.preventDefault();
        this._onTabClick(e);
      }
    }

    /**
     * Scroll a tab into view when it gains focus.
     * @param  {Object} e
     */
  }, {
    key: "_onTabFocus",
    value: function _onTabFocus(e) {
      this._scrollToTab(_domManipulation["default"].getElementMatchingParent(e.target, '.' + TAB_CLASS, this.el));
    }

    /**
     * When the backward button is clicked, slide the scroller to the right.
     * @param {Object} e
     */
  }, {
    key: "_onBackwardClick",
    value: function _onBackwardClick(e) {
      e.preventDefault();
      this._scrollable.scroll({
        x: this._containerEl.clientWidth - this._activeTab.clientWidth
      });
    }

    /**
     * When the forward button is clicked, slide the scroller to the left.
     * @param {Object} e
     */
  }, {
    key: "_onForwardClick",
    value: function _onForwardClick(e) {
      e.preventDefault();
      this._scrollable.scroll({
        x: -(this._containerEl.clientWidth - this._activeTab.clientWidth)
      });
    }

    /**
     * When the window resizes, determine if we should show the left-right buttons.
     * @param {Object} e
     */
  }, {
    key: "_onResize",
    value: function _onResize() {
      this._cacheDimensions();
      this._determineOverflow();
      this._scrollToActiveTabDebounced = this._scrollToActiveTabDebounced || (0, _debounce["default"])(this._scrollToActiveTab.bind(this), 25);
      this._scrollToActiveTabDebounced();
    }

    /**
     * Close a tab because its close button was clicked.
     * @param {Object} e
     */
  }, {
    key: "_onTabCloseClick",
    value: function _onTabCloseClick(e) {
      e.etoCloseClicked = true;
      this.removeTab(_domManipulation["default"].getElementMatchingParent(e.target, '.' + TAB_CLASS, this.el));
    }

    /**
     * On enter or space, simulate click.
     * @param {Event} e
     */
  }, {
    key: "_onTabCloseKeypress",
    value: function _onTabCloseKeypress(e) {
      if (e.keyCode === 32 || e.keyCode === 13) {
        e.preventDefault();
        this._onTabCloseClick(e);
      }
    }

    /**
     * Prevent dragging because this messes us up. Firefox lets you drag
     * links around and it borks the whole thing.
     * @param  {Object} e
     */
  }, {
    key: "_onTabDragStart",
    value: function _onTabDragStart(e) {
      e.preventDefault();
    }
  }]);
}(_base["default"]);
/**
 * The default properties.
 * @type {Object}
 */
Tabs.prototype.props = {
  el: null,
  _containerEl: null,
  _scrollEl: null,
  _activeTab: null,
  _scrollable: null,
  _isOverflowing: false,
  _containerElWidth: 0,
  _scrollElWidth: 0,
  _affixedType: null
};

/**
 * A whitelist of properties that can be set on construction.
 * @type {Array}
 */
Tabs.prototype.whitelist = ['el'];

/**
 * The DOM bindings.
 * @type {Object}
 */
Tabs.prototype.bindings = {
  _activeTab: {
    type: function type(el, value, prevValue) {
      if (prevValue) _domManipulation["default"].removeClass(prevValue, ACTIVE_TAB_CLASS);
      if (value) _domManipulation["default"].addClass(value, ACTIVE_TAB_CLASS);
    }
  },
  _isOverflowing: [{
    type: 'booleanClass',
    name: OVERFLOWING_CLASS
  }],
  _affixedType: {
    type: 'attribute',
    name: 'data-affixed-type'
  }
};

/**
 * DOM event listeners.
 * @type {Object}
 */
Tabs.prototype.events = _defineProperty(_defineProperty(_defineProperty(_defineProperty(_defineProperty(_defineProperty(_defineProperty(_defineProperty({}, 'focus .' + TAB_CLASS, '_onTabFocus'), 'click .' + TAB_CLASS, '_onTabClick'), 'keypress .' + TAB_CLASS, '_onTabKeypress'), 'dragstart .' + TAB_CLASS, '_onTabDragStart'), 'click .' + TAB_CLOSE_CLASS, '_onTabCloseClick'), 'keypress .' + TAB_CLOSE_CLASS, '_onTabCloseKeypress'), 'click .' + BACKWARD_CLASS, '_onBackwardClick'), 'click .' + FORWARD_CLASS, '_onForwardClick');

/**
 * The default template.
 * @type {Object}
 */
Tabs.prototype.template = _tabs["default"];

/**
 * Preprocess passed params.
 * @param {Object} params
 * @return {Object}
 */
function preprocess(params) {
  // A list of items to render
  if (params.items) {
    // Render the items into the template
    var markup = _tabs["default"].render({
      items: params.items
    });

    // If an element was passed, use it.
    if (params.el) {
      params.el.innerHTML = markup;
    }
    // No element passed, so make one.
    else {
      params.el = (0, _makeElement["default"])(markup);
    }
  }
  return params;
}
var _default = exports["default"] = Tabs;
module.exports = exports.default;


},{"../../../html/templates/precompiled/tab":15,"../../../html/templates/precompiled/tabs":16,"../helpers/dom-manipulation":26,"../helpers/events/window-events":27,"../helpers/make-element":29,"../helpers/traversal/get-parent":36,"./base":18,"./scrollable":21,"lodash/debounce":251,"lodash/each":253,"lodash/isElement":272}],23:[function(require,module,exports){
"use strict";

function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _popover = _interopRequireDefault(require("./popover"));
var _affix = _interopRequireDefault(require("../helpers/position/affix"));
var _tooltip = _interopRequireDefault(require("../../../html/templates/precompiled/tooltip"));
var _windowEvents = _interopRequireDefault(require("../helpers/events/window-events"));
var _getParent = _interopRequireDefault(require("../helpers/traversal/get-parent"));
var _matches = _interopRequireDefault(require("../helpers/traversal/matches"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); }
function _callSuper(t, o, e) { return o = _getPrototypeOf(o), _possibleConstructorReturn(t, _isNativeReflectConstruct() ? Reflect.construct(o, e || [], _getPrototypeOf(t).constructor) : o.apply(t, e)); }
function _possibleConstructorReturn(t, e) { if (e && ("object" == _typeof(e) || "function" == typeof e)) return e; if (void 0 !== e) throw new TypeError("Derived constructors may only return object or undefined"); return _assertThisInitialized(t); }
function _assertThisInitialized(e) { if (void 0 === e) throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); return e; }
function _isNativeReflectConstruct() { try { var t = !Boolean.prototype.valueOf.call(Reflect.construct(Boolean, [], function () {})); } catch (t) {} return (_isNativeReflectConstruct = function _isNativeReflectConstruct() { return !!t; })(); }
function _getPrototypeOf(t) { return _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf.bind() : function (t) { return t.__proto__ || Object.getPrototypeOf(t); }, _getPrototypeOf(t); }
function _inherits(t, e) { if ("function" != typeof e && null !== e) throw new TypeError("Super expression must either be null or a function"); t.prototype = Object.create(e && e.prototype, { constructor: { value: t, writable: !0, configurable: !0 } }), Object.defineProperty(t, "prototype", { writable: !1 }), e && _setPrototypeOf(t, e); }
function _setPrototypeOf(t, e) { return _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function (t, e) { return t.__proto__ = e, t; }, _setPrototypeOf(t, e); } /**
 * # Tooltip
 * A component that shows and hides a tooltip. Same as a Popover but opens on hover.
 *
 * @example
 * new Tooltip({
 *   el: element,
 *   anchorX: 'left|right|center',
 *   anchorY: 'top|bottom|middle'
 * });
 *
 * @module components/popover.js
 */
/**
 * Tooltip class methods.
 * @extends Base
 */
var Tooltip = /*#__PURE__*/function (_Popover) {
  function Tooltip() {
    _classCallCheck(this, Tooltip);
    return _callSuper(this, Tooltip, arguments);
  }
  _inherits(Tooltip, _Popover);
  return _createClass(Tooltip, [{
    key: "_cacheElements",
    value:
    /**
     * Cache the caret element.
     */
    function _cacheElements() {
      this._affixEl = this.el;
      this._parentEl = this.el.parentNode;
      this._caretEl = this.query('[class*="__caret"]');
    }

    /**
     * Add window event listeners so that we can listen for any click that
     * might be valid to open this modal.
     */
  }, {
    key: "_addWindowListeners",
    value: function _addWindowListeners() {
      this._winEvents = new _windowEvents["default"](this);
      this._winEvents.bind('mouseover [data-tooltip]', '_onToggleMouseover');
      this._winEvents.bind('mouseout [data-tooltip]', '_onToggleMouseout');
      this._winEvents.bind('scroll', '_onWindowScroll');
      this._winEvents.bind('DOMMouseScroll', '_onWindowScroll');
    }

    /**
     * Remove window event listeners.
     */
  }, {
    key: "_removeWindowListeners",
    value: function _removeWindowListeners() {
      this._winEvents.remove();
      this._winEvents = null;
    }

    /**
     * When a tooltip toggle is moused over, open the tooltip.
     * @param {Object} e
     */
  }, {
    key: "_onToggleMouseover",
    value: function _onToggleMouseover(e) {
      if (this._isOpen) return;
      var query = e.target.getAttribute('data-tooltip');
      if ((0, _matches["default"])(this.el, query)) {
        this.anchorEl = e.target;
        this.open();
        e.preventDefault();
      }
    }

    /**
     * When the window is clicked and it's not part of the popover, close the popover.
     * @param {Objec} e
     */
  }, {
    key: "_onToggleMouseout",
    value: function _onToggleMouseout(e) {
      if (!this._isOpen) return;
      if ((e.target === this.anchorEl || e.delegateTarget === this.anchorEl) && !e.target.contains(e.relatedTarget)) {
        this.close();
        e.preventDefault();
      }
    }
    /**
     * When the window is scrolled and element is out of focus
     * @param {Objec} e 
     */
  }, {
    key: "_onWindowScroll",
    value: function _onWindowScroll(e) {
      if (!this._isOpen) return;
      if (this.anchorEl != document.activeElement) {
        this.close();
        e.preventDefault();
      }
    }
  }]);
}(_popover["default"]);
/**
 * A whitelist of properties that can be set on construction.
 * @type {Array}
 */
Tooltip.prototype.whitelist = ['el', 'anchorX', 'anchorY', 'anchorEl', 'rootEl'];

/**
 * The default template.
 * @type {Object}
 */
Tooltip.prototype.template = _tooltip["default"];
var _default = exports["default"] = Tooltip;
module.exports = exports.default;


},{"../../../html/templates/precompiled/tooltip":17,"../helpers/events/window-events":27,"../helpers/position/affix":33,"../helpers/traversal/get-parent":36,"../helpers/traversal/matches":37,"./popover":20}],24:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
/**
 * # Cancel Animation
 * Cancel animation frame polyfill.
 * @module helpers/animation/cancel.js
 */
var cancel = window.cancelAnimationFrame || window.mozCancelAnimationFrame || window.webkitCancelAnimationFrame || window.cancelRequestAnimationFrame || window.msCancelRequestAnimationFrame || window.mozCancelRequestAnimationFrame || window.webkitCancelRequestAnimationFrame || function cancelAnimationFrame(id) {
  window.clearTimeout(id);
};
var _default = exports["default"] = cancel;
module.exports = exports.default;


},{}],25:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
/**
 * # Request Animation
 * Request animation frame polyfill.
 * @module helpers/animation/request.js
 */
var request = window.requestAnimationFrame || window.msRequestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame || function () {
  var fps = 60;
  var del = 1000 / fps;
  var start = Date.now();
  var prev = start;
  return function requestAnimationFrame(callback) {
    var requestTime = Date.now();
    var timeout = Math.max(0, del - (requestTime - prev));
    var timeToCall = requestTime + timeout;
    prev = timeToCall;
    return window.setTimeout(function onAnimationFrame() {
      callback(timeToCall - start);
    }, timeout);
  };
}();
var _default = exports["default"] = request;
module.exports = exports.default;


},{}],26:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _matches = _interopRequireDefault(require("./traversal/matches"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # DOM Manipulation
 * Helper methods for easily working with the DOM.
 * Based heavily on https://github.com/ampersandjs/ampersand-dom
 *
 * @example
 *
 * @module helpers/dom-manipulation.js
 */

/**
 * Get a string from a value, converting null, and undefined to ''.
 * @param  {Mixed} val
 * @return {String}
 */
function getString(val) {
  if (!val && val !== 0) {
    return '';
  } else {
    return val;
  }
}

/**
 * Does an element have a class?
 * @param  {Element}  el
 * @param  {String}  cls
 * @return {Boolean}
 */
function hasClass(el, cls) {
  if (el.classList) {
    return el.classList.contains(cls);
  } else {
    return new RegExp('(^| )' + cls + '( |$)', 'gi').test(el.className);
  }
}

/**
 * Does an element have a boolean property?
 * @param  {Element}  el
 * @param  {String}  prop
 * @return {Boolean}
 */
function hasBooleanProperty(el, prop) {
  var val = el[prop];
  return prop in el && (val === true || val === false);
}

/**
 * Is DOM element hidden?
 * @param  {Element}  el
 * @return {Boolean}
 */
function isHidden(el) {
  return dom.getAttribute(el, 'data-edl-hidden') === 'true';
}

/**
 * Store as an attribute how an element is being hidden.
 * @param  {Element} el
 * @param  {String} mode `visibility` or `display`
 */
function storeDisplayStyle(el, mode) {
  dom.setAttribute(el, 'data-edl-' + mode, el.style[mode]);
}

/**
 * Show an element.
 * @param  {Element} el
 * @param  {String} mode `visibility` or `display`
 */
function _show(el, mode) {
  el.style[mode] = dom.getAttribute(el, 'data-edl-' + mode) || '';
  dom.removeAttribute(el, 'data-edl-hidden');
}

/**
 * Hide an element.
 * @param  {Element} el
 * @param  {String} mode `visibility` or `display`
 */
function _hide(el, mode) {
  dom.setAttribute(el, 'data-edl-hidden', 'true');
  el.style[mode] = mode === 'visibility' ? 'hidden' : 'none';
}

/**
 * The DOM manipulation API.
 * @type {Object}
 */
var dom = {
  /**
   * Set the text on an element
   * @param  {Element} el
   * @param  {Mixed} val
   */
  text: function text(el, val) {
    el.textContent = getString(val);
  },
  /**
   * See if an element has another element for a parent.
   * @param {Element} child
   * @param {Element} possibleParent
   * @return {Boolean}
   */
  elementHasParent: function elementHasParent(child, possibleParent) {
    var parent = child.parentNode;
    while (parent) {
      if (parent === possibleParent) {
        return true;
      }
      parent = parent.parentNode;
    }
    return false;
  },
  /**
   * See if an element has another element for a parent.
   * Similar to `closest` from jQuery.
   * @param {Element} parent
   * @param {String} query
   * @param {Array|Element} limitEl The last element we should check.
   * @return {Boolean|Element}
   */
  getElementMatchingParent: function getElementMatchingParent(parent, query, limitEl) {
    limitEl = limitEl instanceof Array ? limitEl : [limitEl || document.body];
    while (parent && parent !== document) {
      if ((0, _matches["default"])(parent, query)) {
        return parent;
      }
      if (limitEl.indexOf(parent) !== -1) {
        return false;
      }
      parent = parent.parentNode;
    }
    return false;
  },
  /**
   * Get the offset position of the element.
   * @param {Element} el
   * @param {Boolean} viewPortOffset Return an offset relative to the viewport, not page.
   * @return {Object}
   */
  getElementOffset: function getElementOffset(el, viewPortOffset) {
    var rect = {
      top: 0,
      left: 0
    };

    // Native implementation
    if (el.getBoundingClientRect) {
      var bounding = el.getBoundingClientRect();
      rect.left = bounding.left;
      rect.top = bounding.top;
      if (!viewPortOffset) {
        rect.left += typeof window.scrollX !== 'undefined' ? window.scrollX : window.pageXOffset;
        rect.top += typeof window.scrollY !== 'undefined' ? window.scrollY : window.pageYOffset;
      }
    } else {
      var x = 0,
        y = 0;
      do {
        x += el.offsetLeft - (!viewPortOffset ? el.scrollLeft : 0);
        y += el.offsetTop - (!viewPortOffset ? el.scrollTop : 0);
      } while (el = el.offsetParent);
      rect.left = x;
      rect.top = y;
    }
    return rect;
  },
  /**
   * Append an array of children to a node.
   * @param {Element} el
   * @param {Array} children
   * @param {Boolean} empty Empty the node before adding children?
   */
  appendChildren: function appendChildren(el, children, empty) {
    empty = empty === undefined ? false : empty;
    if (empty) {
      el.textContent = '';
    }
    var domList = children instanceof window.HTMLCollection;
    if (domList) {
      while (children.length) {
        el.appendChild(children[0]);
      }
    } else {
      var i = 0;
      var len = children.length;
      for (; i < len; i++) {
        if (children[i]) {
          el.appendChild(children[i]);
        }
      }
    }
  },
  /**
   * Add a class or a list of classes to an element.
   * @param {Element} el
   * @param {String|Array} cls
   */
  addClass: function addClass(el, cls) {
    // Convert the given class to a string if we can.
    cls = getString(cls);

    // No class, nothing to add.
    if (!cls) return;

    // An array was passed, so add them each.
    if (Array.isArray(cls)) {
      cls.forEach(function (c) {
        dom.addClass(el, c);
      });
    }
    // Element already has a class list, append to it.
    else if (el.classList) {
      el.classList.add(cls);
    }
    // No class list yet.
    else {
      // If class is not already present on this element.
      if (!hasClass(el, cls)) {
        // Add it to the class list.
        if (el.classList) {
          el.classList.add(cls);
        }
        // Or append the string.
        else {
          el.className += ' ' + cls;
        }
      }
    }
  },
  /**
   * Remove a class or list of classes from an element.
   * @todo support array of elements
   * @param  {Element} el
   * @param  {String|Array} cls
   */
  removeClass: function removeClass(el, cls) {
    // Remove an array of classes
    if (Array.isArray(cls)) {
      cls.forEach(function (c) {
        dom.removeClass(el, c);
      });
    }
    // Element has a class list, remove that way.
    else if (el.classList) {
      cls = getString(cls);
      if (cls) el.classList.remove(cls);
    }
    // Remove the old fashioned way.
    else {
      el.className = el.className.replace(new RegExp('(^|\\b)' + cls.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
    }
  },
  /**
   * Does an element have a class?
   * @param  {Element}  el
   * @param  {String}  cls
   * @return {Boolean}
   */
  hasClass: hasClass,
  /**
   * Toggle a class on an element.
   * @param  {Element}  el
   * @param  {String}  cls
   * @return {Boolean}
   */
  toggleClass: function toggleClass(el, cls) {
    if (this.hasClass(el, cls)) {
      this.removeClass(el, cls);
    } else {
      this.addClass(el, cls);
    }
  },
  /**
   * Remove old classes and replace with new.
   * @param  {Element} el
   * @param  {String|Array} prevCls
   * @param  {String|Array} newCls
   */
  switchClass: function switchClass(el, prevCls, newCls) {
    if (prevCls) this.removeClass(el, prevCls);
    this.addClass(el, newCls);
  },
  /**
   * Add an attribute to an element. If the attribute
   * is already set, it will be cleared of content.
   * @param {Element} el
   * @param {String} attr
   */
  addAttribute: function addAttribute(el, attr) {
    el.setAttribute(attr, '');
    if (hasBooleanProperty(el, attr)) el[attr] = true;
  },
  /**
   * Remove an attribute form an element.
   * @param  {Element} el
   * @param  {String} attr
   */
  removeAttribute: function removeAttribute(el, attr) {
    el.removeAttribute(attr);
    if (hasBooleanProperty(el, attr)) el[attr] = false;
  },
  /**
   * Set the value of an attribute on an element.
   * @param {Element} el
   * @param {String} attr
   * @param {Mixed} value
   */
  setAttribute: function setAttribute(el, attr, value) {
    el.setAttribute(attr, getString(value));
  },
  /**
   * Get an attribute from an element.
   * @param  {Element} el
   * @param  {String} attr
   * @return {String}
   */
  getAttribute: function getAttribute(el, attr) {
    return el.getAttribute(attr);
  },
  /**
   * Does an element have an attribute?
   * @param  {element}  el
   * @param  {String}  attr
   * @return {Boolean}
   */
  hasAttribute: function hasAttribute(el, attr) {
    return el.hasAttribute(attr);
  },
  /**
   * Hide an element using an optional mode, unless it's already hidden.
   * @param  {Element} el
   * @param  {String} mode Optional `display` or `visibility`
   */
  hide: function hide(el, mode) {
    if (!mode) mode = 'display';
    if (!isHidden(el)) {
      storeDisplayStyle(el, mode);
      _hide(el, mode);
    }
  },
  /**
   * Show an element using an optional mode.
   * @param  {Element} el
   * @param  {String} mode Optional `display` or `visibility`
   */
  show: function show(el, mode) {
    if (!mode) mode = 'display';
    _show(el, mode);
  },
  /**
   * Toggle the visibility of an element
   * @param  {Element} el
   * @param  {String} mode Optional `display` or `visibility`
   */
  toggle: function toggle(el, mode) {
    if (!isHidden(el)) {
      dom.hide(el, mode);
    } else {
      dom.show(el, mode);
    }
  },
  /**
   * Set the HTML of an element
   * @param  {Element} el
   * @param  {String} content
   */
  html: function html(el, content) {
    el.innerHTML = content;
  },
  /**
   * Get preceding sibling element node.
   * Ignores text, comment nodes.
   * @param  {Element} el
   */
  previousSibling: function previousSibling(el) {
    while (el = el.previousSibling) {
      if (el.nodeType === 1) return el;
    }
    return null;
  },
  /**
   * Get next sibling element.
   * Ignores text, comment nodes.
   * @param  {Element} el
   */
  nextSibling: function nextSibling(el) {
    while (el = el.nextSibling) {
      if (el.nodeType === 1) return el;
    }
    return null;
  },
  /**
   * Gets index of an element relative to its sibling elements
   * @param  {Element} el
   */
  index: function index(el) {
    var parent = el.parentElement;
    if (!parent) {
      throw new Error('Element has no parent element.');
    }
    return Array.prototype.indexOf.call(parent.children, el);
  }
};
var _default = exports["default"] = dom;
module.exports = exports.default;


},{"./traversal/matches":37}],27:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _eventsMixin = _interopRequireDefault(require("events-mixin"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); } /**
 * # Window events
 * Window events with delegation like components.
 *
 * @example
 * var winEvents = WindowEvents(obj);
 * winEvents.on('click [data-hook="stuff"]', '_onStuffClick');
 *
 * @module helpers/events/window-events.js
 */
var WindowEvents = /*#__PURE__*/function () {
  /**
   * Init the events mixin.
   * @param  {Object} obj The object to bind callbacks to.
   */
  function WindowEvents(obj) {
    _classCallCheck(this, WindowEvents);
    this.events = (0, _eventsMixin["default"])(window, obj);
  }

  /**
   * Proxy binds through to eventsMixin.
   */
  return _createClass(WindowEvents, [{
    key: "bind",
    value: function bind() {
      this.events.bind.apply(this.events, arguments);
    }

    /**
     * Proxy unbinds through to eventsMixin.
     */
  }, {
    key: "unbind",
    value: function unbind() {
      this.events.unbind.apply(this.events, arguments);
    }

    /**
     * Clean up this instance.
     */
  }, {
    key: "remove",
    value: function remove() {
      this.events.unbind();
      this.events = null;
    }
  }]);
}();
var _default = exports["default"] = WindowEvents;
module.exports = exports.default;


},{"events-mixin":52}],28:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = getIds;
exports.suffixSelector = suffixSelector;
exports.uuidv4 = uuidv4;
var _uniqueId = _interopRequireDefault(require("lodash/uniqueId"));
var _forOwn = _interopRequireDefault(require("lodash/forOwn"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Get IDs
 * Parses parameters for IDs (field and error message).
 *
 * @module helpers/get-ids.js
 */

function suffixSelector(suffix) {
  return "[class$=\"".concat(suffix, "\"],[class*=\"").concat(suffix, " \"]");
}
function uuidv4() {
  return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, function (c) {
    return (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16);
  });
}
function getId(el, selector) {
  if (!el) return;
  var target = el.querySelector(selector);
  if (target) {
    var id = target.getAttribute('id');
    if (id) {
      return id;
    }
  }
}
function setId(params, prop, selector) {
  if (typeof params[prop] === 'undefined') {
    params[prop] = getId(params.el, selector) || (0, _uniqueId["default"])('eto');
  }
}
function getIds(params) {
  var ids = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {
    id: suffixSelector('__field'),
    messageId: suffixSelector('__message')
  };
  (0, _forOwn["default"])(ids, function (selector, prop) {
    return setId(params, prop, selector);
  });
  return params;
}


},{"lodash/forOwn":261,"lodash/uniqueId":311}],29:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = _default;
/**
 * # Make Element
 * Make en element using a string of HTML.
 *
 * @example
 * makeElement('<div></div>');
 *
 * @module helpers/make-element.js
 *
 * @param {String} html
 * @return {Element}
 */
function _default(html) {
  if (!html) {
    throw new Error('Cannot create element with no HTML!');
  }
  var el = document.createElement('div');
  el.innerHTML = html;
  var el2 = el.children[0];
  el2.parentNode.removeChild(el2);
  return el2;
}
module.exports = exports.default;


},{}],30:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = _default;
var _assign = _interopRequireDefault(require("lodash/assign"));
var _forEach = _interopRequireDefault(require("lodash/forEach"));
var _forOwn = _interopRequireDefault(require("lodash/forOwn"));
var _union = _interopRequireDefault(require("lodash/union"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _toConsumableArray(r) { return _arrayWithoutHoles(r) || _iterableToArray(r) || _unsupportedIterableToArray(r) || _nonIterableSpread(); }
function _nonIterableSpread() { throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method."); }
function _unsupportedIterableToArray(r, a) { if (r) { if ("string" == typeof r) return _arrayLikeToArray(r, a); var t = {}.toString.call(r).slice(8, -1); return "Object" === t && r.constructor && (t = r.constructor.name), "Map" === t || "Set" === t ? Array.from(r) : "Arguments" === t || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(t) ? _arrayLikeToArray(r, a) : void 0; } }
function _iterableToArray(r) { if ("undefined" != typeof Symbol && null != r[Symbol.iterator] || null != r["@@iterator"]) return Array.from(r); }
function _arrayWithoutHoles(r) { if (Array.isArray(r)) return _arrayLikeToArray(r); }
function _arrayLikeToArray(r, a) { (null == a || a > r.length) && (a = r.length); for (var e = 0, n = Array(a); e < a; e++) n[e] = r[e]; return n; } /**
 * # Mixin
 * Mixin behavior into a component that extends Base class.
 *
 * @example
 * mixin(MyComponent.prototype, {
 *   props: {},
 *   whitelist: [],
 *   blacklist: [],
 *   methods: {},
 *   bindings: {}
 * }, ... );
 *
 * @module helpers/mixin.js
 */
function add(proto, mixin, key, cb) {
  if (mixin[key] && Object.getOwnPropertyNames(mixin[key]).length) {
    // make sure we aren't changing inheirited props up the chain
    if (!proto.hasOwnProperty(key)) {
      proto[key] = (0, _assign["default"])({}, proto[key]);
    }

    // callback for each item on mixin
    (0, _forOwn["default"])(mixin[key], cb || function (v, k) {
      // check for collision
      if (typeof proto[key][k] !== 'undefined') {
        throw "Collision with mixin, ".concat(k, " already exists on ").concat(key, ".");
      }
      proto[key][k] = v;
    });
  }
}
function _default(proto) {
  for (var _len = arguments.length, mixins = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
    mixins[_key - 1] = arguments[_key];
  }
  (0, _forEach["default"])(mixins, function (mixin) {
    //
    // add props
    //
    add(proto, mixin, 'props');

    //
    // add derived props
    //
    add(proto, mixin, 'derived');

    //
    // add whitelist
    //
    proto.whitelist = (0, _union["default"])(proto.whitelist, mixin.whitelist);

    //
    // add blacklist
    //
    proto.blacklist = (0, _union["default"])(proto.blacklist, mixin.blacklist);

    //
    // add methods
    //
    (0, _forOwn["default"])(mixin.methods, function (method, key) {
      // check for collision
      if (proto.hasOwnProperty(key)) {
        throw "Collision with mixin, method ".concat(key, " already exists.");
      }
      proto[key] = method;
    });

    //
    // add bindings
    //
    add(proto, mixin, 'bindings', function (mixinBinding, key) {
      // if we already have a binding for some key, add bindings to existing key
      if (proto.bindings[key]) {
        var protoBinding = proto.bindings[key];
        var protoIsArray = Array.isArray(protoBinding);
        if (!protoIsArray) protoBinding = [protoBinding];
        var mixinIsArray = Array.isArray(mixinBinding);
        if (!mixinIsArray) mixinBinding = [mixinBinding];
        proto.bindings[key] = [].concat(_toConsumableArray(protoBinding), _toConsumableArray(mixinBinding));
      } else {
        proto.bindings[key] = mixinBinding;
      }
    });

    //
    // add events
    //
    add(proto, mixin, 'events');
  });
}
module.exports = exports.default;


},{"lodash/assign":247,"lodash/forEach":260,"lodash/forOwn":261,"lodash/union":309}],31:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = _default;
/**
 * Convert a string to a number if it's a number string.
 * @param {String} str
 * @return {Number|String}
 */
function _default(str) {
  // Remove commas
  var str2 = str.replace(/,/gi, '');

  // See if this is a valid number
  var matches = str2.match(/[0-9.]{1,}/);

  // Number is the same length as the string means all characters
  // were valid.
  if (matches && matches[0] && matches[0].length === str2.length) {
    if (str2.indexOf('.') !== -1) {
      return parseFloat(str2);
    }
    return parseInt(str2, 10);
  }
  return str;
}
module.exports = exports.default;


},{}],32:[function(require,module,exports){
"use strict";

var _nunjucks = _interopRequireDefault(require("nunjucks"));
var _globals = _interopRequireDefault(require("../../../html/globals"));
var _filters = _interopRequireDefault(require("../../../html/filters"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * Add custom filters and globals to template environment.
 */

_nunjucks["default"].env = new _nunjucks["default"].Environment();
(0, _globals["default"])(_nunjucks["default"].env);
(0, _filters["default"])(_nunjucks["default"].env);


},{"../../../html/filters":2,"../../../html/globals":5,"nunjucks":314}],33:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _domManipulation = _interopRequireDefault(require("../dom-manipulation"));
var _boxPosition = _interopRequireDefault(require("./box-position"));
var _debounce = _interopRequireDefault(require("lodash/debounce"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); }
function _classCallCheck(a, n) { if (!(a instanceof n)) throw new TypeError("Cannot call a class as a function"); }
function _defineProperties(e, r) { for (var t = 0; t < r.length; t++) { var o = r[t]; o.enumerable = o.enumerable || !1, o.configurable = !0, "value" in o && (o.writable = !0), Object.defineProperty(e, _toPropertyKey(o.key), o); } }
function _createClass(e, r, t) { return r && _defineProperties(e.prototype, r), t && _defineProperties(e, t), Object.defineProperty(e, "prototype", { writable: !1 }), e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); } /**
 * # Affix
 * Affix one element to another.
 *
 * @example
 * new Affix({
 *   el: el,
 *   anchorEl: el2,
 *   caretEl: el3,
 *   anchorY: 'top', // 'middle', 'bottom'
 *   anchorX: 'left', // 'center', 'right'
 * })
 *
 * @module helpers/position/affix.js
 */
var Affix = /*#__PURE__*/function () {
  /**
   * Store the reference elements and position.
   * @param  {Object} params
   */
  function Affix() {
    var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    _classCallCheck(this, Affix);
    this._storeParams(params);
    this._addEventListeners();
    this._insertEl();
    this._setPosition();
    this._updateDebounced = (0, _debounce["default"])(this.update.bind(this), 500);
  }

  /**
   * Stop listening and clean up event listeners
   * @param {Object} params Optional
   * @return {Object} this
   */
  return _createClass(Affix, [{
    key: "remove",
    value: function remove() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      if (!params.keepEl) this._removeEl();
      this._removeEventListeners();
      return this;
    }

    /**
     * Update the position.
     * @param {Element} anchorEl Optional A new anchor element
     * @return {Object} this
     */
  }, {
    key: "update",
    value: function update(anchorEl) {
      this.anchorEl = anchorEl || this.anchorEl;
      this._setPosition();
      return this;
    }

    /**
     * Store a set of parameters.
     * @param {Object} params
     */
  }, {
    key: "_storeParams",
    value: function _storeParams(params) {
      this.el = params.el;
      this.anchorEl = params.anchorEl;
      this.rootEl = params.rootEl;
      this.caretEl = params.caretEl;
      this.anchorY = params.anchorY || 'top';
      this.anchorX = params.anchorX || 'center';
      this.isFixed = params.isFixed || false;
    }

    /**
     * Listen for window resizes to update the position.
     */
  }, {
    key: "_addEventListeners",
    value: function _addEventListeners() {
      var _this$anchorEl;
      this._onResizeBound = this._onResizeBound || this._onResize.bind(this);
      this._onScrollBound = this._onScrollBound || this._onScroll.bind(this);
      // If anchor element is present inside scrollable grid
      var gridScrollElement = (_this$anchorEl = this.anchorEl) === null || _this$anchorEl === void 0 ? void 0 : _this$anchorEl.closest('.eto-grid-scroll');
      if (gridScrollElement) {
        gridScrollElement.addEventListener('scroll', this._onScrollBound);
      }
      window.addEventListener('resize', this._onResizeBound);
      window.addEventListener('scroll', this._onScrollBound);
    }

    /**
     * Remove event listeners
     */
  }, {
    key: "_removeEventListeners",
    value: function _removeEventListeners() {
      var _this$anchorEl2;
      window.removeEventListener('resize', this._onResizeBound);
      window.removeEventListener('scroll', this._onScrollBound);
      var gridScrollElement = (_this$anchorEl2 = this.anchorEl) === null || _this$anchorEl2 === void 0 ? void 0 : _this$anchorEl2.closest('.eto-grid-scroll');
      if (gridScrollElement) {
        gridScrollElement.removeEventListener('scroll', this._onScrollBound);
      }
    }

    /**
     * Insert the element into the DOM.
     */
  }, {
    key: "_insertEl",
    value: function _insertEl() {
      this.el.setAttribute('data-affixed', '');
      this._getRootEl().appendChild(this.el);
    }

    /**
     * Remove the element from the DOM.
     */
  }, {
    key: "_removeEl",
    value: function _removeEl() {
      this.el.parentNode.removeChild(this.el);
      this.el.removeAttribute('data-affixed');
    }

    /**
     * Set the position of the target element.
     */
  }, {
    key: "_setPosition",
    value: function _setPosition() {
      // Fixed position
      this.el.style.setProperty('position', this.isFixed ? 'fixed' : 'absolute', 'important');

      // Target element properties
      var _dom$getElementOffset = _domManipulation["default"].getElementOffset(this.anchorEl, this.isFixed),
        targetTop = _dom$getElementOffset.top,
        targetLeft = _dom$getElementOffset.left;
      var targetWidth = this.anchorEl.offsetWidth;
      var targetHeight = this.anchorEl.offsetHeight;

      // Element to affix properties
      var elWidth = this.el.offsetWidth;
      var elHeight = this.el.offsetHeight;

      // Maxes
      var docHeight = this.isFixed ? window.innerHeight : document.documentElement.offsetHeight;
      var docWidth = this.isFixed ? window.innerWidth : document.documentElement.offsetWidth;

      // Get the values
      var _this$_calculatePosit = this._calculatePosition({
          anchorX: this.anchorX,
          anchorY: this.anchorY,
          targetTop: targetTop,
          targetLeft: targetLeft,
          elHeight: elHeight,
          elWidth: elWidth,
          targetHeight: targetHeight,
          targetWidth: targetWidth,
          minX: 0,
          minY: 0,
          maxX: Math.max(docWidth - elWidth, 0),
          maxY: Math.max(docHeight - elHeight, 0)
        }),
        elTop = _this$_calculatePosit.elTop,
        elLeft = _this$_calculatePosit.elLeft;

      // Position the caret
      var _this$_positionCaret = this._positionCaret({
          elLeft: elLeft,
          elTop: elTop,
          elWidth: elWidth,
          elHeight: elHeight,
          targetHeight: targetHeight,
          targetWidth: targetWidth,
          targetLeft: targetLeft,
          targetTop: targetTop
        }),
        extraLeft = _this$_positionCaret.extraLeft,
        extraTop = _this$_positionCaret.extraTop;

      // Set the position
      this.el.style.left = elLeft + extraLeft + 'px';
      this.el.style.top = elTop + extraTop + 'px';
    }

    /**
     * Get the proper top position for an anchor direction.
     * @param  {Object} p
     * @return {Object}
     */
  }, {
    key: "_calculatePosition",
    value: function _calculatePosition(p) {
      // Keep track of what we're trying to do here, so on subsequent, nested calls to this
      // method we can see what has already been tried.
      p.previousAttempts = (p.previousAttempts || 0) + 1;
      p.previousChecks = p.previousChecks || [];
      var finalCheck = p.previousAttempts > 3;
      var top;
      var left;

      // Y-axis check
      switch (p.anchorY) {
        case 'bottom':
          top = p.targetTop + p.targetHeight;
          break;
        case 'middle':
          top = p.targetTop - (p.elHeight - p.targetHeight) / 2;
          break;
        default:
          top = p.targetTop - p.elHeight;
          break;
      }

      // Under min
      if (top < p.minY) {
        if (!finalCheck && p.previousChecks.indexOf('overY') === -1) {
          p.previousChecks.push('underY');
          p.anchorY = this._getNewAnchorY(true, p.anchorY, p.anchorX);
          return this._calculatePosition(p);
        } else {
          top = p.minY;
        }
      }

      // Over max
      if (top > p.maxY) {
        if (!finalCheck && p.previousChecks.indexOf('underY') === -1) {
          p.previousChecks.push('overY');
          p.anchorY = this._getNewAnchorY(false, p.anchorY, p.anchorX);
          return this._calculatePosition(p);
        } else {
          top = p.maxY;
        }
      }

      // X-axis check
      switch (p.anchorX) {
        case 'right':
          left = p.targetLeft + p.targetWidth - (p.anchorY !== 'middle' && !p.isOverlapping ? p.elWidth : 0);
          break;
        case 'center':
          left = p.targetLeft - (p.elWidth - p.targetWidth) / 2;
          break;
        default:
          left = p.targetLeft - (p.anchorY === 'middle' ? p.elWidth : 0);
          break;
      }

      // Under min
      if (left < p.minX) {
        if (!finalCheck && p.previousChecks.indexOf('overX') === -1) {
          p.previousChecks.push('underX');
          p.anchorX = this._getNewAnchorX(true, p.anchorX, p.anchorY);
          return this._calculatePosition(p);
        } else {
          left = p.minX;
        }
      }

      // Over max
      if (left > p.maxX) {
        if (!finalCheck && p.previousChecks.indexOf('underX') === -1) {
          p.previousChecks.push('overX');
          p.anchorX = this._getNewAnchorX(false, p.anchorX, p.anchorY);
          return this._calculatePosition(p);
        } else {
          left = p.targetLeft - p.elWidth > 0 && p.targetLeft - p.elWidth < p.maxX ? p.targetLeft - p.elWidth : p.maxX;
        }
      }

      // One element is covering another. Try to fix that, but bail out after four tries.
      if ((0, _boxPosition["default"])({
        width: p.elWidth,
        height: p.elHeight,
        left: left,
        top: top
      }, {
        width: p.targetWidth,
        height: p.targetHeight,
        left: p.targetLeft,
        top: p.targetTop
      }) === 'overlap') {
        p.isOverlapping = true;

        // Try Y
        if (p.repositionY !== false) {
          // Will start undefined, then true, then false. This limits us to entering
          // this loop twice, once to try moving in each direction.
          p.repositionY = !p.repositionY;

          // First try to put above, then try to put below.
          p.anchorY = this._getNewAnchorY(p.repositionY, 'middle', p.anchorX);

          // Give us one more shot at positioning
          p.previousAttempts--;
          return this._calculatePosition(p);
        }
        // Try X
        else if (p.repositionX !== false) {
          // Will start undefined, then true, then false. This limits us to entering
          // this loop twice, once to try moving in each direction.
          p.repositionX = !p.repositionX;

          // First try to put above, then try to put below.
          p.anchorX = this._getNewAnchorX(p.repositionX, 'center', p.anchorY);

          // Give us one more shot at positioning
          p.previousAttempts--;
          return this._calculatePosition(p);
        }
      }
      return {
        elTop: top,
        elLeft: left,
        anchorX: p.anchorX,
        anchorY: p.anchorY
      };
    }

    /**
     * Determine the new y-axis anchor
     * @param  {Boolean} underMin Under the min?
     * @param  {String} anchorY
     * @param  {String} anchorX
     * @return {String}
     */
  }, {
    key: "_getNewAnchorY",
    value: function _getNewAnchorY(underMin, anchorY, anchorX) {
      // If the x-axis is anchored in the center, skip
      // trying to anchor to the middle because then we'd
      // be overlaying the button.
      if (anchorX === 'center' || anchorY === 'middle') {
        return underMin ? 'bottom' : 'top';
      } else {
        return 'middle';
      }
    }

    /**
     * Determine the new y-axis anchor
     * @param  {Boolean} underMin Under the min?
     * @param  {String} anchorY
     * @param  {String} anchorX
     * @return {String}
     */
  }, {
    key: "_getNewAnchorX",
    value: function _getNewAnchorX(underMin, anchorX, anchorY) {
      // If the y-axis is anchored in the center, skip
      // trying to anchor to the middle because then we'd
      // be overlaying the button.
      if (anchorY === 'middle' || anchorX === 'center') {
        return underMin ? 'left' : 'right';
      } else {
        return 'center';
      }
    }

    /**
     * Set the position of the caret.
     * @param {Object} p
     * @return {Object}
     */
  }, {
    key: "_positionCaret",
    value: function _positionCaret() {
      var p = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      if (!this.caretEl) return {
        extraLeft: 0,
        extraTop: 0
      };
      var caretPosition = (0, _boxPosition["default"])({
        width: p.elWidth,
        height: p.elHeight,
        left: p.elLeft,
        top: p.elTop
      }, {
        width: p.targetWidth,
        height: p.targetHeight,
        left: p.targetLeft,
        top: p.targetTop
      });
      var caretDimensions = this.caretEl.getBoundingClientRect();
      var caretWidth = caretDimensions.width;
      var caretHeight = caretDimensions.height;
      var left = Math.min(p.elWidth, Math.max(0, p.targetLeft - p.elLeft + p.targetWidth / 2));
      var top = Math.min(p.elHeight, Math.max(0, p.targetTop - p.elTop + p.targetHeight / 2));
      this.caretEl.style.left = Math.round(left) + 'px';
      this.caretEl.style.top = Math.round(top) + 'px';
      var extraLeft = 0;
      var extraTop = 0;
      this.caretEl.setAttribute('data-position', caretPosition);
      switch (caretPosition) {
        case 'above':
          extraTop = -caretWidth / 2;
          break;
        case 'below':
          extraTop = caretWidth / 2;
          break;
        case 'left':
          extraLeft = -caretHeight / 2;
          break;
        default:
          extraLeft = caretHeight / 2;
          break;
      }
      return {
        extraLeft: extraLeft,
        extraTop: extraTop
      };
    }

    /**
     * Get the root element. Want to check if there's a top-level form for working
     * with ASP .NET pages.
     */
  }, {
    key: "_getRootEl",
    value: function _getRootEl() {
      // if (this.rootEl) return this.rootEl; // @todo: we can't use this yet because we need to be able to calc offsets relative to this root not the body.
      var form = document.querySelector('body > form');
      return form && form.getAttribute('data-affixed') === null ? form : document.body;
    }

    /**
     * On resize, update the position.
     */
  }, {
    key: "_onResize",
    value: function _onResize() {
      this.update();
    }

    /**
     * When the window/grid scrolls, ensure the proper position of the popover.
     */
  }, {
    key: "_onScroll",
    value: function _onScroll() {
      this._updateDebounced();
    }
  }]);
}();
var _default = exports["default"] = Affix;
module.exports = exports.default;


},{"../dom-manipulation":26,"./box-position":34,"lodash/debounce":251}],34:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = _default;
/**
 * # Box Position
 * How is one element positioned relative to another?
 *
 * @example
 * boxPosition(
 * {width: 100, height: 300, left: 0, top: 0},
 * {width: 200, height: 50, left: 100, top: 40}
 * )
 *
 * @module helpers/position/box-position.js
 *
 * @param {Object} a
 * @param {Object} b
 * @return {String}
 */
function _default(a, b) {
  var aXSpan = a.left + a.width;
  var aYSpan = a.top + a.height;
  var bXSpan = b.left + b.width;
  var bYSpan = b.top + b.height;
  if (aXSpan <= b.left) return 'left'; // a is fully left of b
  if (a.left >= bXSpan) return 'right'; // a is fully right of b
  if (aYSpan <= b.top) return 'above'; // a is fully above b
  if (a.top >= bYSpan) return 'below'; // a is fully below b

  return 'overlap'; // boxes overlap
}
module.exports = exports.default;


},{}],35:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
/**
 * Prevent scrolling of a parent element (ie document body) when
 * scrollable element has reached it's boundary.
 * @param {HTMLElement} scrollable
 */

function prevent(ev) {
  typeof ev.stopPropagation == "function" ? ev.stopPropagation() : ev.cancelBubble = true; // IE 8
  ev.preventDefault();
  ev.returnValue = false;
  return false;
}
function handler(ev) {
  var el = ev.currentTarget,
    scrollTop = el.scrollTop,
    scrollHeight = el.scrollHeight,
    height = el.clientHeight,
    delta = ev.wheelDelta,
    up = delta > 0;
  if (scrollHeight === height) {
    // Element does not scroll, let event bubble to body.
    return;
  } else if (!up && -delta > scrollHeight - height - scrollTop) {
    // Scrolling down, but this will take us past the bottom.
    el.scrollTop = scrollHeight;
    return prevent(ev);
  } else if (up && delta > scrollTop) {
    // Scrolling up, but this will take us past the top.
    el.scrollTop = 0;
    return prevent(ev);
  }
}
var _default = exports["default"] = {
  bind: function bind(scrollable) {
    if (scrollable === window || scrollable === document || scrollable === document.bdoy) return;
    scrollable.addEventListener('DOMMouseScroll', handler);
    scrollable.addEventListener('mousewheel', handler);
  },
  unbind: function unbind(scrollable) {
    scrollable.removeEventListener('DOMMouseScroll', handler);
    scrollable.removeEventListener('mousewheel', handler);
  }
};
module.exports = exports.default;


},{}],36:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _isArrayLike = _interopRequireDefault(require("lodash/isArrayLike"));
var _indexOf = _interopRequireDefault(require("lodash/indexOf"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Get Parent
 * See if an element has another element for a parent.
 *
 * @param {Element} child
 * @param {Element|Array} possibleParent
 * @return {Boolean}
 *
 * @module helpers/traversal/get-parent.js
 */

function getParent(child, possibleParents) {
  possibleParents = (0, _isArrayLike["default"])(possibleParents) ? possibleParents : [possibleParents];
  var parent = child && child.parentNode;
  var matchedIndex;
  while (parent) {
    if ((matchedIndex = (0, _indexOf["default"])(possibleParents, parent)) !== -1) {
      return possibleParents[matchedIndex];
    }
    parent = parent.parentNode;
  }
  return false;
}
var _default = exports["default"] = getParent;
module.exports = exports.default;


},{"lodash/indexOf":265,"lodash/isArrayLike":268}],37:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
/**
 * # Matches
 * See if an element matches a query selector.
 *
 * @param {Element} el
 * @param {String} query
 * @return {Boolean}
 *
 * @module helpers/traversal/matches.js
 */
var vendorMatch = typeof Element !== 'undefined' && (Element.prototype.matches || Element.prototype.matchesSelector || Element.prototype.webkitMatchesSelector || Element.prototype.mozMatchesSelector || Element.prototype.msMatchesSelector || Element.prototype.oMatchesSelector);
function matches(el, query) {
  if (vendorMatch) return vendorMatch.call(el, query);
  var nodes = el.parentNode ? el.parentNode.querySelectorAll(query) : [];
  for (var i = 0; i < nodes.length; i++) {
    if (nodes[i] === el) return true;
  }
  return false;
}
var _default = exports["default"] = matches;
module.exports = exports.default;


},{}],38:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _affix = _interopRequireDefault(require("../helpers/position/affix"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Affixed Menu
 * Add the open/close/toggle functionality of a menu to a component.
 *
 * @example
 * import mixin from '../helpers/mixin';
 * import affixedMenu from '../utils/affixed-menu';
 * mixin(MyComponent.prototype, affixedMenu);
 *
 * @module utils/affixed-menu.js
 */
var _default = exports["default"] = {
  props: {
    anchorEl: null,
    rootEl: null,
    _isOpen: false,
    _parentEl: null,
    _affixEl: null
  },
  bindings: {
    anchorX: {
      type: 'attribute',
      name: 'data-anchor-x'
    },
    anchorY: {
      type: 'attribute',
      name: 'data-anchor-y'
    },
    _isOpen: {
      type: 'booleanClass',
      name: 'open'
    }
  },
  methods: {
    /**
     * Open the menu.
     * @param {Object} params
     */
    open: function open() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      if (this._isOpen) return this;
      this._isOpen = true;
      this._affixTo(this.anchorEl || params.anchorTo);
      this.trigger('opened');
    },
    /**
     * Close the menu.
     */
    close: function close() {
      if (!this._isOpen) return this;
      this._unaffix();
      this._isOpen = false;
      this.trigger('closed');
    },
    /**
     * Toggle the menu state.
     * @param {Object} params
     */
    toggle: function toggle() {
      var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
      this[this._isOpen ? 'close' : 'open'](params);
    },
    /**
     * Affix the element to a target.
     * @param {Element} target
     */
    _affixTo: function _affixTo(anchor) {
      if (!anchor) {
        throw new Error('Cannot affix a menu without an element!');
      }

      // Update an existing affixed instance.
      if (this._affix) {
        this._affix.update(anchor);
      }
      // Affix the content to the toggle
      else {
        this._affix = new _affix["default"]({
          el: this._affixEl,
          caretEl: this._caretEl,
          anchorEl: anchor,
          rootEl: this.rootEl,
          anchorX: this.anchorX,
          anchorY: this.anchorY,
          isFixed: hasFixedParent(anchor)
        });
      }
    },
    /**
     * Unaffix the affixed element.
     */
    _unaffix: function _unaffix() {
      if (!this._affix) return;
      if (this._parentEl) this._parentEl.appendChild(this._affixEl);
      this._affix.remove({
        keepEl: true
      });
      this._affix = null;
    }
  }
};
/**
 * Does an element have a position:fixed parent?
 * @param {Element} el
 * @return {Boolean}
 */
function hasFixedParent(el) {
  var parent = el;
  while (parent && parent !== document) {
    var style = getComputedStyle(parent);
    if (style.position === 'fixed') {
      return true;
    }
    parent = parent.parentNode;
  }
  return false;
}
module.exports = exports.default;


},{"../helpers/position/affix":33}],39:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _omit = _interopRequireDefault(require("lodash/omit"));
var _assign = _interopRequireDefault(require("lodash/assign"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Child Elements
 * Query and cache child elements.
 *
 * @example
 * childElements.init({});
 *
 * @module utils/child-elements.js
 */

var childElements = {
  /**
   * Initialize.
   * @param {Object} obj
   */
  init: function init(obj) {
    (0, _assign["default"])(obj, (0, _omit["default"])(childElements, ['init']));
    obj._elementCache = obj._elementCache || {};
  },
  /**
   * Query for a child element.
   * @param {String} s
   */
  query: function query(s) {
    return this.el && this.el.querySelector(s);
  },
  /**
   * Query for child elements.
   * @param {String} s
   */
  queryAll: function queryAll(s) {
    return this.el && this.el.querySelectorAll(s);
  },
  /**
   * Cache en element.
   * @param {String} sel
   * @return {Element|Undefined}
   */
  setCached: function setCached(sel) {
    var el = this.query(sel);
    if (el) this._elementCache[sel] = el;
    return el;
  },
  /**
   * Cache en element.
   * @param {String} sel
   * @return {Element|Undefined}
   */
  getCached: function getCached(sel) {
    return this._elementCache[sel];
  },
  /**
   * Get a cached element or query and cache.
   * @param {String} sel
   */
  getCachedOrQuery: function getCachedOrQuery(sel) {
    return this.getCached(sel) || this.setCached(sel);
  }
};
var _default = exports["default"] = childElements;
module.exports = exports.default;


},{"lodash/assign":247,"lodash/omit":291}],40:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _forOwn = _interopRequireDefault(require("lodash/forOwn"));
var _each = _interopRequireDefault(require("lodash/each"));
var _isFunction = _interopRequireDefault(require("lodash/isFunction"));
var _union = _interopRequireDefault(require("lodash/union"));
var _omit = _interopRequireDefault(require("lodash/omit"));
var _assign = _interopRequireDefault(require("lodash/assign"));
var _keyTreeStore = _interopRequireDefault(require("key-tree-store"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Derived Props
 * Properties that are based on other properties.
 * Based heavily on https://github.com/ampersandjs/ampersand-state
 *
 * @example
 * derived.init({});
 *
 * @module utils/derived-props.js
 */

var changeRE = /^change:/;

/**
 * Create a definition for a derived property.
 * @param  {Object} obj
 * @param  {String} name
 * @param  {Object} definition
 */
function create(obj, name, def) {
  // Store the definition
  def = obj._derived[name] = {
    fn: (0, _isFunction["default"])(def) ? def : def.fn,
    cache: def.cache !== false,
    deps: def.deps || []
  };

  // Store the dependencies on the object
  def.deps.forEach(function (dep) {
    obj._derivedDeps[dep] = (0, _union["default"])(obj._derivedDeps[dep] || [], [name]);
  });

  // Create a getter and setter
  Object.defineProperty(obj, name, {
    get: function get() {
      return this._getDerivedProperty(name);
    },
    set: function set() {
      throw new TypeError("\"".concat(name, "\" is a derived property, it can't be set directly."));
    }
  });
}

/**
 * Create an update function for a derived property.
 * @param {Object} t The scope.
 * @param {Object} def The property definition.
 * @param {String} name The property name.
 * @return {Function}
 */
function createUpdateFunction(t, def, name) {
  return function () {
    var newVal = def.fn.call(t);
    if (t._derivedCache[name] !== newVal || !def.cache) {
      if (def.cache) {
        t._oldProps[name] = t._derivedCache[name];
      }
      t._derivedCache[name] = newVal;
      t.trigger('change:' + name, t._derivedCache[name], t._oldProps[name], name, t);
    }
  };
}

/**
 * The derived property API.
 * @type {Object}
 */
var derived = {
  /**
  * Take the props from the prototype and initialize them with
  * getters and setters so that we can emit events when the
  * state changes.
  * @param {Object} t The object to init upon.
  * @param {Object} props Optional Parameters to use instead of t.derived.
  */
  init: function init(t, props) {
    props = props || t.derived;
    t._derived = {};
    t._derivedCache = {};
    t._derivedDeps = {};
    t._derivedKeyTree = new _keyTreeStore["default"]();

    // Mixin functionality
    (0, _assign["default"])(t || {}, (0, _omit["default"])(derived, ['init']));

    // Create each property definition.
    (0, _each["default"])(props, function (d, n) {
      create(t, n, d);
    });

    // Setup change events for when the value changes on a prop.
    (0, _forOwn["default"])(t._derived, function (def, name) {
      var update = createUpdateFunction(t, def, name);
      def.deps.forEach(function (propString) {
        t._derivedKeyTree.add(propString, update);
      });
    });

    // When there is an event fired, if it's a change event run the update function
    // for all the matching deps.
    t.on('all', function (eventName) {
      if (changeRE.test(eventName)) {
        t._derivedKeyTree.get(eventName.split(':')[1]).forEach(function (fn) {
          fn();
        });
      }
    }, t);

    // Unset the derived properties hash so it's clear that they aren't being used anymore.
    delete t.derived;
  },
  /**
   * Get the value of a derived property.
   * @param  {String} name
   * @param  {Boolean} flushCache
   * @return {Mixed}
   */
  _getDerivedProperty: function _getDerivedProperty(name) {
    var flushCache = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
    // No caching, compute.
    if (!this._derived[name].cache) {
      return this._derived[name].fn.apply(this);
    }

    // Not already computed or cache should be flushed.
    if (flushCache || !this._derivedCache.hasOwnProperty(name)) {
      this._derivedCache[name] = this._derived[name].fn.apply(this);
    }
    return this._derivedCache[name];
  }
};
var _default = exports["default"] = derived;
module.exports = exports.default;


},{"key-tree-store":54,"lodash/assign":247,"lodash/each":253,"lodash/forOwn":261,"lodash/isFunction":274,"lodash/omit":291,"lodash/union":309}],41:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.create = createBindingsStore;
exports["default"] = void 0;
var _keyTreeStore = _interopRequireDefault(require("key-tree-store"));
var _domManipulation = _interopRequireDefault(require("../helpers/dom-manipulation"));
var _matchesSelector = _interopRequireDefault(require("matches-selector"));
var _partial = _interopRequireDefault(require("lodash/partial"));
var _last = _interopRequireDefault(require("lodash/last"));
var _each = _interopRequireDefault(require("lodash/each"));
var _get = _interopRequireDefault(require("lodash/get"));
var _omit = _interopRequireDefault(require("lodash/omit"));
var _assign = _interopRequireDefault(require("lodash/assign"));
var _pick = _interopRequireDefault(require("lodash/pick"));
var _isBoolean = _interopRequireDefault(require("lodash/isBoolean"));
var _numberToString = _interopRequireDefault(require("../helpers/number-to-string"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); } /**
 * # DOM Bindings
 * Bind properties of an object to the DOM.
 * Based heavily on https://github.com/ampersandjs/ampersand-dom-bindings
 *
 * @example
 * let obj = {};
 * domBindings.init(obj);
 *
 * @module utils/dom-bindings.js
 */
/**
 * Get the children of an element that match a selector.
 * @param  {Element} el
 * @param  {String} selector
 * @return {Array}
 */
function getMatches(el, selector) {
  if (selector === '') return [el];
  var matches = [];
  if ((0, _matchesSelector["default"])(el, selector)) matches.push(el);
  return matches.concat(Array.prototype.slice.call(el.querySelectorAll(selector)));
}

/**
 * Set a list of attributes on an element.
 * @param {Element} el
 * @param {Object} attrs
 */
function setAttributes(el, attrs) {
  for (var name in attrs) {
    _domManipulation["default"].setAttribute(el, name, attrs[name]);
  }
}

/**
 * Remove a list of attributes from an element.
 * @param  {Element} el
 * @param  {Object} attrs
 */
function removeAttributes(el, attrs) {
  for (var name in attrs) {
    _domManipulation["default"].removeAttribute(el, name);
  }
}

/**
 * Convert a value into an array if it isn't already one.
 * @param  {Mixed} val
 * @return {Array}
 */
function makeArray(val) {
  return Array.isArray(val) ? val : [val];
}

/**
 * Handle showing and hiding of multiple elements based on a list of cases.
 * @param  {Object} binding
 * @param  {Element} el
 * @param  {Mixed} value
 */
function switchHandler(binding, el, value) {
  // the element selector to show
  var showValue = binding.cases[value];
  // hide all the other elements with a different value
  for (var item in binding.cases) {
    var curValue = binding.cases[item];
    if (value !== item && curValue !== showValue) {
      getMatches(el, curValue).forEach(function (match) {
        _domManipulation["default"].hide(match);
      });
    }
  }
  getMatches(el, showValue).forEach(function (match) {
    _domManipulation["default"].show(match);
  });
}

/**
 * Get the selector for a binding.
 * @param  {Object} binding
 * @return {String}
 */
function getSelector(binding) {
  if (typeof binding.selector === 'string') {
    return binding.selector;
  } else if (binding.hook) {
    return '[data-hook~="' + binding.hook + '"]';
  } else {
    return '';
  }
}

/**
 * Get the binding function for a context.
 * @param  {Object} binding
 * @param  {Object} context
 * @return {Function}
 */
function getBindingFunc(binding, context) {
  var type = binding.type || 'text';
  var isCustomBinding = typeof type === 'function';
  var selector = getSelector(binding);
  var yes = binding.yes;
  var no = binding.no;
  var hasYesNo = !!(yes || no);

  // storage variable for previous if relevant
  var previousValue;
  if (isCustomBinding) {
    return function (el, value) {
      getMatches(el, selector).forEach(function (match) {
        type.call(context, match, value, previousValue);
      });
      previousValue = value;
    };
  } else if (type === 'text') {
    return function (el, value) {
      getMatches(el, selector).forEach(function (match) {
        _domManipulation["default"].text(match, value);
      });
    };
  } else if (type === 'class') {
    return function (el, value) {
      getMatches(el, selector).forEach(function (match) {
        _domManipulation["default"].switchClass(match, previousValue, value);
      });
      previousValue = value;
    };
  } else if (type === 'attribute') {
    if (!binding.name) throw Error('attribute bindings must have a "name"');
    return function (el, value) {
      var names = makeArray(binding.name);
      getMatches(el, selector).forEach(function (match) {
        names.forEach(function (name) {
          if (value === undefined || value === null) _domManipulation["default"].removeAttribute(match, name);else _domManipulation["default"].setAttribute(match, name, value);
        });
      });
      previousValue = value;
    };
  } else if (type === 'textValue') {
    return function (el, value) {
      getMatches(el, selector).forEach(function (match) {
        if (!value && value !== 0) value = '';
        // only apply bindings if element is not currently focused
        if (document.activeElement !== match) match.value = value;
      });
      previousValue = value;
    };
  } else if (type === 'value') {
    return function (el, value) {
      getMatches(el, selector).forEach(function (match) {
        if (!value && value !== 0) value = '';
        // only apply bindings if element is not currently focused
        if (document.activeElement !== match) match.value = value;
      });
      previousValue = value;
    };
  } else if (type === 'booleanClass') {
    // if there's a `no` case this is actually a switch
    if (hasYesNo) {
      yes = makeArray(yes || '');
      no = makeArray(no || '');
      return function (el, value) {
        var prevClass = value ? no : yes;
        var newClass = value ? yes : no;
        getMatches(el, selector).forEach(function (match) {
          prevClass.forEach(function (pc) {
            _domManipulation["default"].removeClass(match, pc);
          });
          newClass.forEach(function (nc) {
            _domManipulation["default"].addClass(match, nc);
          });
        });
      };
    } else {
      return function (el, value, keyName) {
        var name = makeArray(binding.name || keyName);
        var invert = binding.invert || false;
        value = invert ? value ? false : true : value;
        getMatches(el, selector).forEach(function (match) {
          name.forEach(function (className) {
            _domManipulation["default"][value ? 'addClass' : 'removeClass'](match, className);
          });
        });
      };
    }
  } else if (type === 'booleanAttribute') {
    // if there are `yes` and `no` selectors, this swaps between them
    if (hasYesNo) {
      yes = makeArray(yes || '');
      no = makeArray(no || '');
      return function (el, value) {
        var prevAttribute = value ? no : yes;
        var newAttribute = value ? yes : no;
        getMatches(el, selector).forEach(function (match) {
          prevAttribute.forEach(function (pa) {
            if (pa) {
              _domManipulation["default"].removeAttribute(match, pa);
            }
          });
          newAttribute.forEach(function (na) {
            if (na) {
              _domManipulation["default"].addAttribute(match, na);
            }
          });
        });
      };
    } else {
      return function (el, value, keyName) {
        var name = makeArray(binding.name || keyName);
        var invert = binding.invert || false;
        value = invert ? value ? false : true : value;
        getMatches(el, selector).forEach(function (match) {
          name.forEach(function (attr) {
            _domManipulation["default"][value ? 'addAttribute' : 'removeAttribute'](match, attr);
          });
        });
      };
    }
  } else if (type === 'toggle') {
    var mode = binding.mode || 'display';
    var invert = binding.invert || false;
    // this doesn't require a selector since we can pass yes/no selectors
    if (hasYesNo) {
      return function (el, value) {
        getMatches(el, yes).forEach(function (match) {
          _domManipulation["default"][value ? 'show' : 'hide'](match, mode);
        });
        getMatches(el, no).forEach(function (match) {
          _domManipulation["default"][value ? 'hide' : 'show'](match, mode);
        });
      };
    } else {
      return function (el, value) {
        value = invert ? value ? false : true : value;
        getMatches(el, selector).forEach(function (match) {
          _domManipulation["default"][value ? 'show' : 'hide'](match, mode);
        });
      };
    }
  } else if (type === 'switch') {
    if (!binding.cases) throw Error('switch bindings must have "cases"');
    return (0, _partial["default"])(switchHandler, binding);
  } else if (type === 'innerHTML') {
    return function (el, value) {
      getMatches(el, selector).forEach(function (match) {
        _domManipulation["default"].html(match, value);
      });
    };
  } else if (type === 'switchClass') {
    if (!binding.cases) throw Error('switchClass bindings must have "cases"');
    return function (el, value, keyName) {
      var name = makeArray(binding.name || keyName);
      var _loop = function _loop(item) {
        getMatches(el, binding.cases[item]).forEach(function (match) {
          name.forEach(function (className) {
            _domManipulation["default"][value === item ? 'addClass' : 'removeClass'](match, className);
          });
        });
      };
      for (var item in binding.cases) {
        _loop(item);
      }
    };
  } else if (type === 'switchAttribute') {
    if (!binding.cases) throw Error('switchAttribute bindings must have "cases"');
    return function (el, value, keyName) {
      getMatches(el, selector).forEach(function (match) {
        if (previousValue) {
          removeAttributes(match, previousValue);
        }
        if (value in binding.cases) {
          var attrs = binding.cases[value];
          if (typeof attrs === 'string') {
            attrs = {};
            attrs[binding.name || keyName] = binding.cases[value];
          }
          setAttributes(match, attrs);
          previousValue = attrs;
        }
      });
    };
  } else {
    throw new Error('no such binding type: ' + type);
  }
}

/**
 * Create a key-tree-store of functions that can be applied to any element/model.
 * All resulting functions should be called like func(el, value, lastKeyName).
 * @param {Object} bindings
 * @param {Object} obj
 * @return {Object}
 */
function createBindingsStore(bindings, obj) {
  var store = new _keyTreeStore["default"]();
  var key;
  var current;
  for (key in bindings) {
    current = bindings[key];
    if (typeof current === 'string') {
      store.add(key, getBindingFunc({
        type: 'text',
        selector: current
      }));
    } else if (current.forEach) {
      current.forEach(function (binding) {
        store.add(key, getBindingFunc(binding, obj));
      });
    } else {
      store.add(key, getBindingFunc(current, obj));
    }
  }
  return store;
}

/**
 * Find if a binding is already populated on an element
 * and set its value accordingly.
 * Does not work for class or switchClass bindings.
 * @param {String} name
 * @param {Object} def
 * @param {Object} obj
 * @param {Element} el
 */
function findBindingValue(name, def, obj, el) {
  var sel = getSelector(def);
  var matches = getMatches(el, sel);
  var val;
  if (Array.isArray(def)) {
    return (0, _each["default"])(def, function (d) {
      findBindingValue(name, d, obj, el);
    });
  }

  // Find each kind of binding. Return false when a
  // match is found to break out of the loop.
  if (def.type === 'text') {
    (0, _each["default"])(matches, function (m) {
      if (m.textContent) {
        return !(val = m.textContent);
      }
    });
  } else if (def.type === 'attribute') {
    (0, _each["default"])(matches, function (m) {
      if (_domManipulation["default"].hasAttribute(m, def.name || name)) {
        return !(val = (0, _numberToString["default"])(m.getAttribute(def.name)));
      }
    });
  } else if (def.type === 'textValue') {
    (0, _each["default"])(matches, function (m) {
      return !(val = m.value);
    });
  } else if (def.type === 'value') {
    (0, _each["default"])(matches, function (m) {
      return !(val = (0, _numberToString["default"])(m.value || ''));
    });
  } else if (def.type === 'booleanClass') {
    if ((0, _isBoolean["default"])(obj[name])) {
      (0, _each["default"])(matches, function (m) {
        if (_domManipulation["default"].hasClass(m, def.name || name)) {
          return !(val = true);
        }
      });
    }
  } else if (def.type === 'booleanAttribute') {
    if ((0, _isBoolean["default"])(obj[name])) {
      (0, _each["default"])(matches, function (m) {
        if (_domManipulation["default"].hasAttribute(m, def.name || name)) {
          return !(val = true);
        }
      });
    }
  } else if (def.type === 'toggle') {
    (0, _each["default"])(matches, function (m) {
      var mode = def.mode || 'display';
      var style = window.getComputedStyle(m);
      if (mode === 'display') {
        return !(val = style.display !== 'hidden');
      } else {
        return !(val = style.visibility === 'visible');
      }
    });
  } else if (def.type === 'switch') {
    (0, _each["default"])(matches, function (m) {
      (0, _each["default"])(def.cases, function (v, k) {
        var subMatches = getMatches(m, v);
        (0, _each["default"])(subMatches, function (s) {
          var mode = def.mode || 'display';
          var style = window.getComputedStyle(s);
          if (mode === 'display' && style.display !== 'hidden') {
            return !(val = k);
          } else if (style.visibility === 'visible') {
            return !(val = k);
          }
        });
      });
    });
  } else if (def.type === 'switchClass') {
    (0, _each["default"])(matches, function (m) {
      (0, _each["default"])(def.cases, function (v, k) {
        var subMatches = getMatches(m, v);
        (0, _each["default"])(subMatches, function (s) {
          if (_domManipulation["default"].hasClass(s, k)) {
            return !(val = k);
          }
        });
      });
    });
  } else if (def.type === 'switchAttribute') {
    (0, _each["default"])(matches, function (m) {
      (0, _each["default"])(def.cases, function (v, k) {
        var subMatches = getMatches(m, v);
        (0, _each["default"])(subMatches, function (s) {
          if (s.getAttribute(def.name || k)) {
            return !(val = k);
          }
        });
      });
    });
  } else if (def.type === 'innerHTML') {
    (0, _each["default"])(matches, function (m) {
      if (m.innerHTML) {
        return !(val = m.innerHTML);
      }
    });
  }
  if (val !== undefined) {
    obj[name] = val;
  }
}

/**
 * Callback to be run when the change event fires on the object bindings are bound to.
 * @param {String} eventName
 */
function onChange(eventName) {
  if (!this._bindingsPaused && (eventName === null || eventName === void 0 ? void 0 : eventName.slice(0, 7)) === 'change:') {
    this.applyBindingsForKey(eventName.split(':')[1]);
  }
}

/**
 * DOM Bindings API.
 * @type {Object}
 */
var domBindings = {
  /**
   * Initialize the DOM bindings for a component.
   * @param  {Object} obj
   * @param  {Boolean} findValues Find binding values on the element.
   * @param  {Array} whitelistValues
   * @return {Object}
   */
  init: function init(obj) {
    var findValues = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : false;
    var whitelistValues = arguments.length > 2 ? arguments[2] : undefined;
    obj = obj || {};
    (0, _assign["default"])(obj, (0, _omit["default"])(domBindings, ['init']));
    obj._parsedBindings = createBindingsStore(obj.bindings || {}, obj);
    obj.addBindings();

    // If we should first find the values from an object
    if (findValues) {
      obj._storeBindingValuesForKey(whitelistValues);
    }
    obj.applyBindingsForKey();
    return obj;
  },
  /**
   * Add bindings.
   */
  addBindings: function addBindings() {
    if (!this.bindings || this._bindingsAdded) return;
    this.on('all', onChange, this);
    this._bindingsAdded = true;
  },
  /**
   * Add a single binding.
   * @param {String} name
   * @param {Object} config
   */
  addBinding: function addBinding(name, config) {
    this._parsedBindings.add(name, getBindingFunc(config, this));
    this.applyBindingsForKey(name);
  },
  /**
   * Remove bindings.
   */
  removeBindings: function removeBindings() {
    if (!this._bindingsAdded) return;
    var parsedBindings = this._parsedBindings;

    // Dereference all the bindings to prevent memory leaks
    (0, _each["default"])(parsedBindings, function (props, name) {
      if (_typeof(props) === 'object') {
        (0, _each["default"])(props, function (v, k) {
          delete parsedBindings[name][k];
        });
      }
      delete parsedBindings[name];
    });
    this.off('all', onChange, this);
    this._bindingsAdded = false;
  },
  /**
   * Apply the stored bindings for a particular key.
   * @param {String} key Optional
   */
  applyBindingsForKey: function applyBindingsForKey(key) {
    if (!this.el) {
      return;
    }
    if (this._parsedBindings) {
      var fns = this._parsedBindings.getGrouped(key);
      var item;
      for (item in fns) {
        fns[item].forEach(function (fn) {
          fn(this.el, (0, _get["default"])(this, item), (0, _last["default"])(item.split('.')));
        }, this);
      }
    }
  },
  /**
   * Pause updating bindings.
   */
  pauseBindings: function pauseBindings() {
    this._bindingsPaused = true;
  },
  /**
   * Resume updating bindings.
   */
  resumeBindings: function resumeBindings() {
    this._bindingsPaused = false;
  },
  /**
   * Find and store the value of each binding as is already
   * present on the target element.
   * @param  {String} key Optional
   */
  _storeBindingValuesForKey: function _storeBindingValuesForKey(key) {
    if (!this.el) {
      return;
    }
    var bindings = key ? (0, _pick["default"])(this.bindings, key) : this.bindings;
    var obj = this;
    (0, _each["default"])(bindings, function (d, n) {
      findBindingValue(n, d, obj, obj.el);
    });
  }
};
var _default = exports["default"] = domBindings;


},{"../helpers/dom-manipulation":26,"../helpers/number-to-string":31,"key-tree-store":54,"lodash/assign":247,"lodash/each":253,"lodash/get":262,"lodash/isBoolean":270,"lodash/last":285,"lodash/omit":291,"lodash/partial":294,"lodash/pick":295,"matches-selector":313}],42:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _eventsMixin = _interopRequireDefault(require("events-mixin"));
var _result = _interopRequireDefault(require("lodash/result"));
var _omit = _interopRequireDefault(require("lodash/omit"));
var _assign = _interopRequireDefault(require("lodash/assign"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # DOM Events
 * Allow for easy DOM event delegation.
 *
 * @example
 * let obj = {};
 * domEvents.init(obj);
 *
 * @module utils/dom-events.js
 */

/**
 * The DOM events API.
 * @type {Object}
 */
var domEvents = {
  /**
   * Initialize the DOM events on an element and object.
   * @param {Object} obj The object to dispatch events to.
   * @return {Object}
   */
  init: function init() {
    var obj = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    (0, _assign["default"])(obj, (0, _omit["default"])(domEvents, ['init']));
    obj.delegateEvents();
    return obj;
  },
  /**
   * Set callbacks, where `this.events` is a hash like:
   * {
   *   'click':  'onClick',
   *   'change input: 'onInputChange',
   *   'click .button': 'onButtonClick',
   *   'keypress .open': function (e) { ... }
   * }
   *
   * Callbacks will be bound to the object, with `this` set properly.
   * Uses event delegation for efficiency.
   * Omitting the selector binds the event to `this.el`.
   * This only works for delegate-able events: not `focus`, `blur`, and
   * not `change`, `submit`, and `reset` in Internet Explorer.
   * @param {Object} events Optional The events hash to use.
   * @return {Object} this
   */
  delegateEvents: function delegateEvents(events) {
    // Undelegate existing events.
    if (this._eventManager) {
      this.undelegateEvents();
    }
    this._eventManager = (0, _eventsMixin["default"])(this.el, this);

    // Take a hash of events or evaluate `this.events`. If we cannot use either, get out.
    if (!(events || (events = (0, _result["default"])(this, 'events')))) {
      return this;
    }

    // Bind each event
    for (var key in events) {
      this._eventManager.bind(key, events[key]);
    }
    return this;
  },
  /*
   * Clears all callbacks previously bound to the object with `delegateEvents`.
   * You usually don't need to use this, but may wish to if you have multiple
   * components attached to the same DOM element.
   */
  undelegateEvents: function undelegateEvents() {
    this._eventManager.unbind();
    return this;
  }
};
var _default = exports["default"] = domEvents;
module.exports = exports.default;


},{"events-mixin":52,"lodash/assign":247,"lodash/omit":291,"lodash/result":301}],43:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _once = _interopRequireDefault(require("lodash/once"));
var _isEmpty = _interopRequireDefault(require("lodash/isEmpty"));
var _uniqueId = _interopRequireDefault(require("lodash/uniqueId"));
var _forEach = _interopRequireDefault(require("lodash/forEach"));
var _omit = _interopRequireDefault(require("lodash/omit"));
var _assign = _interopRequireDefault(require("lodash/assign"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); } /**
 * # Events
 * Events to allow objects to communicate with each other.
 * Based heavily on https://github.com/AmpersandJS/ampersand-events
 *
 * @example
 * let obj = {};
 * events.init(obj);
 * obj.on('change', function() {});
 * obj.trigger('change');
 *
 * @module utils/events.js
 */
var eventSplitter = /\s+/;

/**
 * A difficult-to-believe, but optimized internal dispatch function for
 * triggering events. Tries to keep the usual cases speedy.
 * @param {Array} events
 * @param {Array} args
 */
function triggerEvents(events, args) {
  var ev;
  var i = -1;
  var l = events.length;
  var a1 = args[0];
  var a2 = args[1];
  var a3 = args[2];
  switch (args.length) {
    case 0:
      while (++i < l) (ev = events[i]).callback.call(ev.ctx);
      return;
    case 1:
      while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1);
      return;
    case 2:
      while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1, a2);
      return;
    case 3:
      while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1, a2, a3);
      return;
    default:
      while (++i < l) (ev = events[i]).callback.apply(ev.ctx, args);
      return;
  }
}

/**
 * Implement fancy features of the Events API such as multiple event
 * names `"change blur"` and jQuery-style event maps `{change: action}`
 * in terms of the existing API.
 * @param {Object} obj
 * @param {String} action
 * @param {Object|String} name
 * @param {Mixed} rest
 * @return {Boolean}
 */
function eventsApi(obj, action, name, rest) {
  if (!name) {
    return true;
  }

  // Handle event maps.
  if (_typeof(name) === 'object') {
    for (var key in name) {
      obj[action].apply(obj, [key, name[key]].concat(rest));
    }
    return false;
  }

  // Handle space separated event names.
  if (eventSplitter.test(name)) {
    var names = name.split(eventSplitter);
    for (var i = 0, l = names.length; i < l; i++) {
      obj[action].apply(obj, [names[i]].concat(rest));
    }
    return false;
  }
  return true;
}

/**
 * Inversion-of-control versions of `on` and `once`. Tell *this* object to
 * listen to an event in another object ... keeping track of what it's
 * listening to.
 * @param {String} implementation The event triggering method to use.
 * @return {Function}
 */
function createListenMethod(implementation) {
  return function listenMethod(obj, name, callback) {
    if (!obj || typeof obj === 'number') {
      throw new Error('Trying to listenTo event: \'' + name + '\' but the target object is undefined');
    }
    var listeningTo = this._listeningTo || (this._listeningTo = {});
    var id = obj._listenId || (obj._listenId = (0, _uniqueId["default"])('l'));
    listeningTo[id] = obj;
    if (!callback && _typeof(name) === 'object') {
      callback = this;
    }
    if (typeof obj[implementation] !== 'function') {
      throw new Error('Trying to listenTo event: \'' + name + '\' on object: ' + obj.toString() + ' but it does not have an \'on\' method so is unbindable');
    }
    obj[implementation](name, callback, this);
    return this;
  };
}

/**
 * The events API
 * @type {Object}
 */
var events = {
  /**
   * Extend an object with event capabilities if passed
   * or just return a new one.
   * @param {Object} obj Optinal
   */
  init: function init() {
    var obj = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    return (0, _assign["default"])(obj, (0, _omit["default"])(events, ['init']));
  },
  /**
   * Bind an event to a `callback` function. Passing `"all"` will bind
   * the callback to all events fired.
   * @param {String} name
   * @param {Function} callback
   * @param {Object} context
   */
  on: function on(name, callback, context) {
    if (!eventsApi(this, 'on', name, [callback, context]) || !callback) return this;
    this._events = this._events || {};
    var events = this._events[name] || (this._events[name] = []);
    events.push({
      callback: callback,
      context: context,
      ctx: context || this
    });
    return this;
  },
  /**
   * Bind an event to only be triggered a single time. After the first time
   * the callback is invoked, it will be removed.
   * @param {String} name
   * @param {Function} callback
   * @param {Object} context
   */
  once: function once(name, callback, context) {
    if (!eventsApi(this, 'once', name, [callback, context]) || !callback) return this;
    var self = this;
    var once = (0, _once["default"])(function () {
      self.off(name, once);
      callback.apply(self, arguments);
    });
    once._callback = callback;
    return this.on(name, once, context);
  },
  /**
   * Remove one or many callbacks. If `context` is null, removes all
   * callbacks with that function. If `callback` is null, removes all
   * callbacks for the event. If `name` is null, removes all bound
   * callbacks for all events.
   * @param {String} name
   * @param {Function} callback
   * @param {Object} context
   */
  off: function off(name, callback, context) {
    var retain, ev, events, names, i, l, j, k;
    if (!this._events || !eventsApi(this, 'off', name, [callback, context])) return this;
    if (!name && !callback && !context) {
      this._events = void 0;
      return this;
    }
    names = name ? [name] : Object.keys(this._events);
    for (i = 0, l = names.length; i < l; i++) {
      name = names[i];
      if (events = this._events[name]) {
        this._events[name] = retain = [];
        if (callback || context) {
          for (j = 0, k = events.length; j < k; j++) {
            ev = events[j];
            if (callback && callback !== ev.callback && callback !== ev.callback._callback || context && context !== ev.context) {
              retain.push(ev);
            }
          }
        }
        if (!retain.length) delete this._events[name];
      }
    }
    return this;
  },
  /**
   * Trigger one or many events, firing all bound callbacks. Callbacks are
   * passed the same arguments as `trigger` is, apart from the event name
   * (unless you're listening on `"all"`, which will cause your callback to
   * receive the true name of the event as the first argument).
   * @param {String} name
   * @param {...Mixed} args
   */
  trigger: function trigger(name) {
    for (var _len = arguments.length, args = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
      args[_key - 1] = arguments[_key];
    }
    if (!this._events) return this;
    if (!eventsApi(this, 'trigger', name, args)) return this;
    var events = this._events[name];
    var allEvents = this._events.all;
    if (events) triggerEvents(events, args);
    if (allEvents) triggerEvents(allEvents, arguments);
    return this;
  },
  /**
   * Tell this object to stop listening to either specific events ... or
   * to every object it's currently listening to.
   * @param {String} name
   * @param {Function} callback
   * @param {Object} context
   */
  stopListening: function stopListening(obj, name, callback) {
    var listeningTo = this._listeningTo;
    if (!listeningTo) return this;
    var remove = !name && !callback;
    if (!callback && _typeof(name) === 'object') callback = this;
    if (obj) (listeningTo = {})[obj._listenId] = obj;
    var self = this;
    (0, _forEach["default"])(listeningTo, function (item, id) {
      item.off(name, callback, self);
      if (remove || (0, _isEmpty["default"])(item._events)) delete self._listeningTo[id];
    });
    return this;
  },
  /**
   * Start proxying events from one object to another.
   * @param {Object} frm
   * @param {String} name The name to prefix the proxied events with
   */
  startProxyingEvents: function startProxyingEvents(frm, name) {
    var _this = this;
    this.listenTo(frm, 'all', function (n, val, oldVal, nm, obj) {
      var evtName = n.split(':');
      _this.trigger(evtName[0] + ':' + (name ? name + '.' : '') + evtName[1], val, oldVal, name + '.' + evtName[1], obj);
    });
  },
  /**
   * Stop proxying events from one object to another.
   * @param {Object} frm
   */
  stopProxyingEvents: function stopProxyingEvents(frm) {
    this.stopListening(frm, 'all');
  },
  /**
   * Listen for an event and run the callback right now.
   * @param  {Object}   obj
   * @param  {String}   name
   * @param  {Function} callback Optional
   * @return {Object}
   */
  listenToAndRun: function listenToAndRun(obj, name, callback) {
    this.listenTo.apply(this, arguments);
    if (!callback && _typeof(name) === 'object') callback = this;
    callback.apply(this);
    return this;
  },
  /**
   * Same as 'on', but inverted control.
   * @param {Object} obj
   * @param {String} name
   * @param {Function} callback
   * @param {Object} context
   */
  listenTo: createListenMethod('on'),
  /**
   * Same as 'once', but inverted control.
   * @param {Object} obj
   * @param {String} name
   * @param {Function} callback
   * @param {Object} context
   */
  listenToOnce: createListenMethod('once')
};
var _default = exports["default"] = events;
module.exports = exports.default;


},{"lodash/assign":247,"lodash/forEach":260,"lodash/isEmpty":273,"lodash/omit":291,"lodash/once":293,"lodash/uniqueId":311}],44:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _uniqueId = _interopRequireDefault(require("lodash/uniqueId"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Input Message
 * Add the ability for an input to display status messages.
 *
 * @example
 * import mixin from '../helpers/mixin';
 * import inputMessage from '../utils/input-message';
 * mixin(MyComponent.prototype, inputMessage);
 *
 * @module utils/input-message.js
 */

var fieldSelector = '[class$="__field"],[class*="__field "]';
var messageSelector = '[class$="__message"],[class*="__message "]';
var _default = exports["default"] = {
  props: {
    messageId: null,
    messageType: null,
    messageContent: null,
    messageRole: 'alert',
    messageAriaLive: 'polite'
  },
  whitelist: ['messageId', 'messageType', 'messageContent'],
  methods: {
    /**
     * Set the message content.
     * @param {String} message
     * @param {String} type Optional
     */
    setMessage: function setMessage(message, type) {
      this.messageContent = message;
      if (type) {
        this.messageType = type;
      }
    },
    /**
     * Clear message.
     */
    clearMessage: function clearMessage() {
      this.messageContent = null;
      this.messageType = null;
    }
  },
  bindings: {
    messageType: [{
      type: 'attribute',
      name: 'data-message-type'
    }, {
      type: function type(el, messageType) {
        var isError = messageType === 'error';
        el.setAttribute('aria-invalid', isError.toString());
      },
      selector: fieldSelector
    }],
    messageContent: {
      type: 'innerHTML',
      selector: messageSelector
    },
    messageId: [{
      type: 'attribute',
      name: 'id',
      selector: messageSelector
    }, {
      type: 'attribute',
      name: 'aria-describedby',
      selector: fieldSelector
    }],
    messageRole: {
      type: 'attribute',
      name: 'role',
      selector: messageSelector
    },
    messageAriaLive: {
      type: 'attribute',
      name: 'aria-live',
      selector: messageSelector
    }
  }
};
module.exports = exports.default;


},{"lodash/uniqueId":311}],45:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
/**
 * # Global Instance Cache
 * Creates a cache of instances and their elements.
 *
 * @module utils/instance-cache.js
 */

/**
 * Instance Cache Map
 * @type {Map}
 */
var cache = new Map();

/**
 * Returns the map from cache for a class.
 * @param {String} cls
 * @return {Map}
 */
function protoMap(inst) {
  var proto = Object.getPrototypeOf(inst);
  var _map = cache.get(proto);
  if (!_map) {
    _map = new Map();
    cache.set(proto, _map);
  }
  return _map;
}
var _default = exports["default"] = {
  get: function get(inst, el) {
    return protoMap(inst).get(el);
  },
  set: function set(inst, el) {
    protoMap(inst).set(el, inst);
  },
  "delete": function _delete(inst, el) {
    protoMap(inst)["delete"](el);
  },
  deleteInstance: function deleteInstance(inst) {
    var map = protoMap(inst);
    map.forEach(function (i, el) {
      if (i === inst) map["delete"](el);
    });
  }
};
module.exports = exports.default;


},{}],46:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _omit = _interopRequireDefault(require("lodash/omit"));
var _assign = _interopRequireDefault(require("lodash/assign"));
var _forEach = _interopRequireDefault(require("lodash/forEach"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
function _defineProperty(e, r, t) { return (r = _toPropertyKey(r)) in e ? Object.defineProperty(e, r, { value: t, enumerable: !0, configurable: !0, writable: !0 }) : e[r] = t, e; }
function _toPropertyKey(t) { var i = _toPrimitive(t, "string"); return "symbol" == _typeof(i) ? i : i + ""; }
function _toPrimitive(t, r) { if ("object" != _typeof(t) || !t) return t; var e = t[Symbol.toPrimitive]; if (void 0 !== e) { var i = e.call(t, r || "default"); if ("object" != _typeof(i)) return i; throw new TypeError("@@toPrimitive must return a primitive value."); } return ("string" === r ? String : Number)(t); }
function _typeof(o) { "@babel/helpers - typeof"; return _typeof = "function" == typeof Symbol && "symbol" == typeof Symbol.iterator ? function (o) { return typeof o; } : function (o) { return o && "function" == typeof Symbol && o.constructor === Symbol && o !== Symbol.prototype ? "symbol" : typeof o; }, _typeof(o); } /**
 * # Props
 * Properties that emit change events.
 *
 * @example
 * props.init({});
 *
 * @module utils/props.js
 */
/**
 * Is a value an evented object with properties?
 * @param {Mixed} val
 * @return {Boolean}
 */
function isEventedObject(val) {
  return val && _typeof(val) === 'object' && val._props && typeof val.on === 'function';
}
var props = {
  /**
   * Take the props from the prototype and initialize them with
   * getters and setters so that we can emit events when the
   * state changes.
   * @param {Object} t The object to init upon.
   * @param {Object} p Optional Parameters to use instead of t.props.
   */
  init: function init(t, p) {
    (0, _assign["default"])(t, (0, _omit["default"])(props, ['init']));
    t._props = {};
    t._oldProps = {};
    t.addProps(p || t.props);
    delete t.props;
    return t;
  },
  /**
   * Add a list of properties to an object
   * @param {Object} ps
   */
  addProps: function addProps(ps) {
    for (var i in ps) {
      this.addProp(i, ps[i]);
    }
  },
  /**
   * Add a property to an object. If the property is an evented object (or ever becomes one),
   * proxy through events from that object.
   * @param {String} name
   * @param {Mixed} value
   */
  addProp: function addProp(name, value) {
    var _this = this;
    if (this.hasOwnProperty(name)) {
      throw new Error("The ".concat(name, " property already exists on this object"), this);
    }
    if (isEventedObject(value)) this.startProxyingEvents(value, name);
    this._props[name] = value;
    this._oldProps[name] = undefined;
    Object.defineProperty(this, name, {
      get: function get() {
        return _this.get(name);
      },
      set: function set(v) {
        return _this.set(name, v);
      }
    });
  },
  /**
   * Get the value of a property.
   * @param {String} name
   */
  get: function get(name) {
    return this._props[name];
  },
  /**
   * Set the value of a property or multiple properties
   * @param {String|Object} name
   * @param {Mixed} val
   * @param {Object} opts Optional
   */
  set: function set(name, val) {
    var _this2 = this;
    var opts = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
    var attrs;

    // Allow for an object of values to be passed
    if (_typeof(name) === 'object') {
      attrs = name;
      opts = val || {};
    }
    // Key-value pair passed
    else {
      attrs = _defineProperty({}, name, val);
    }

    // Extract options
    var _opts = opts,
      _opts$silent = _opts.silent,
      silent = _opts$silent === void 0 ? false : _opts$silent,
      _opts$triggerBindings = _opts.triggerBindings,
      triggerBindings = _opts$triggerBindings === void 0 ? true : _opts$triggerBindings;

    // Batch all the change events to run at the end. Because change events
    // can be nested, we need to store the changing state across multiple sets.
    var wasChanging = this._propsChanging;
    this._propsChanging = true;
    var changeEvents = [];

    // Set each attribute
    (0, _forEach["default"])(attrs, function (v, k) {
      // No property exists
      if (!_this2._props.hasOwnProperty(k)) {
        _this2._propsChanging = false;
        throw new Error("The ".concat(k, " property does not exist on this object"), _this2);
      }

      // Current value
      var cur = _this2.get(k);

      // Don't set if the value is the same.
      if (v !== cur) {
        // Setting against a nested object
        if (cur && isEventedObject(cur) && _typeof(v) === 'object' && !isEventedObject(v)) {
          cur.set(v);
        }

        // New value being set will be a nested object
        if (isEventedObject(v)) _this2.startProxyingEvents(v, k);

        // Stop listening to any proxied events from an old property
        if (isEventedObject(_this2._oldProps[k])) _this2.stopProxyingEvents(_this2._oldProps[k]);

        // Store the old property
        _this2._oldProps[k] = cur;

        // Store the new property
        _this2._props[k] = v;

        // Queue a change event
        if (!silent) {
          changeEvents.push({
            oldValue: cur,
            value: v,
            name: k
          });
        }
      }
    });

    // Fire queued events
    if (changeEvents.length) this._propsPending = true;
    changeEvents.forEach(function (c) {
      if (!triggerBindings) _this2.pauseBindings();
      _this2.trigger('change:' + c.name, c.value, c.oldValue, c.name, _this2);
    });

    // If we were already changing, get out of here because
    // some other `set` will call the over-arching change event.
    if (wasChanging) return this;

    // This seems weird, but `set` can be called inside of things
    // responding to a `change` event, so we have to account for them.
    while (this._propsPending) {
      this._propsPending = false;
      if (!triggerBindings) this.pauseBindings();
      this.trigger('change', this, changeEvents);
    }

    // No more changes pending.
    this._propsPending = false;
    this._propsChanging = false;
    if (!triggerBindings) this.resumeBindings();
    return this;
  }
};
var _default = exports["default"] = props;
module.exports = exports.default;


},{"lodash/assign":247,"lodash/forEach":260,"lodash/omit":291}],47:[function(require,module,exports){
"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;
var _resultsComplex = _interopRequireDefault(require("../../../html/templates/precompiled/results-complex"));
var _softScroll = _interopRequireDefault(require("../helpers/soft-scroll"));
var _domManipulation = _interopRequireDefault(require("../helpers/dom-manipulation"));
var _affix2 = _interopRequireDefault(require("../helpers/position/affix"));
var _windowEvents = _interopRequireDefault(require("../helpers/events/window-events"));
var _makeElement = _interopRequireDefault(require("../helpers/make-element"));
var _debounce = _interopRequireDefault(require("lodash/debounce"));
var _tabs = _interopRequireDefault(require("../components/tabs"));
var _map = _interopRequireDefault(require("lodash/map"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { "default": e }; }
/**
 * # Results mixin
 * Add the ability for component to show and navigate results.
 *
 * @example
 * import mixin from '../helpers/mixin';
 * import results from '../utils/results-complex-mixin';
 * mixin(MyComponent.prototype, results);
 *
 * @module utils/results-complex-mixin.js
 */
var _default = exports["default"] = {
  props: {
    resultsId: null,
    _isOpen: false,
    _tabs: null,
    _badges: null,
    _cursor: null,
    _selectedCursor: null,
    __index: null,
    __selectedIndex: null
  },
  derived: {
    _cursorEl: {
      deps: ['_cursor'],
      fn: function fn() {
        if (this._resultsEl) {
          return this._resultsEl.querySelector(".eto-results__option[data-index=\"".concat(this._cursor, "\"]"));
        }
      }
    },
    _selectedCursorEl: {
      deps: ['_selectedCursor'],
      fn: function fn() {
        if (this._resultsEl) {
          return this._resultsEl.querySelector(".eto-results__selected-option[data-index=\"".concat(this._selectedCursor, "\"]"));
        }
      }
    }
  },
  whitelist: ['resultsId'],
  methods: {
    /**
     * Returns open state.
     * @return {boolean}
     */
    isOpen: function isOpen() {
      return this._isOpen;
    },
    /**
     * Opens results dropdown.
     */
    open: function open(availableLength, selectedLength) {
      if (this._isOpen) return;
      if (!this._tabs) {
        this._createTabs(availableLength, selectedLength);
      }
      this._isOpen = true;
      this._resultsEl.style.display = 'inherit';
      this._affix();
      if (!this._badges) {
        this._badges = this._tabs.queryAll('.eto-badge');
      }
      if (selectedLength > 0) {
        this._tabs.enableTab(1);
        this._badges[1].setAttribute('data-type', 'info');
      } else {
        this._tabs.disableTab(1);
        this._badges[1].removeAttribute('data-type');
      }
      this._tabs.setActive(0);
    },
    /**
     * Closes results dropdown.
     */
    close: function close() {
      if (!this._isOpen) return;
      this._unaffix();
      this._resultsEl.style.display = 'none';
      this._isOpen = false;
      if (this._processClose) this._processClose();
    },
    /**
     * Affix the element to a target.
     * @param {Element} target
     */
    _affix: function _affix() {
      this._resizeWidth();
      if (!this._affixed) this._affixed = new _affix2["default"]({
        el: this._resultsEl,
        anchorEl: this._anchorEl,
        anchorX: 'middle',
        anchorY: 'bottom',
        isFixed: false
      });
      this.resize();
    },
    /**
     * Unaffix the affixed element.
     */
    _unaffix: function _unaffix() {
      if (!this._affixed) return;
      this.el.appendChild(this._resultsEl);
      this._affixed.remove({
        keepEl: true
      });
      this._affixed = null;
    },
    /**
     * When the window is clicked and it's not part of the popover, close the popover.
     * @param {Objec} e
     */
    _onWindowClick: function _onWindowClick(e) {
      if (this._isOpen && !(this.el.contains(e.target) || this._resultsEl.contains(e.target) || this._tagsEl.contains(e.target))) {
        this.close();
      }
    },
    /**
     * Bind soft-scroll on scrolling element.
     */
    _bindSoftScroll: function _bindSoftScroll() {
      var scrollingEl = this.query('.eto-results__scroll');
      scrollingEl && _softScroll["default"].bind(scrollingEl);
    },
    /**
     * Unbind soft-scroll on scrolling element.
     */
    _unbindSoftScroll: function _unbindSoftScroll() {
      var scrollingEl = this.query('.eto-results__scroll');
      scrollingEl && _softScroll["default"].unbind(scrollingEl);
    },
    /**
     * Returns whether we have a cursor.
     * @return {boolean}
     */
    _hasCursor: function _hasCursor() {
      return typeof this._cursor === 'number';
    },
    /**
     * Returns whether we have a cursor.
     * @return {boolean}
     */
    _hasSelectedCursor: function _hasSelectedCursor() {
      return typeof this._selectedCursor === 'number';
    },
    /**
     * Move cursor.
     * @param {number} dir
     */
    _moveCursor: function _moveCursor(dir) {
      var hasCursor = this._hasCursor();
      var max = this.__index.length - 1;
      var cursor;
      if (dir < 0) {
        // if we have a cursor, we move to the previous sibling
        // if nothing is currently highlighted, we highlight the last option
        cursor = hasCursor ? this._cursor - 1 : max;
      } else if (dir > 0) {
        // if we have a cursor, we move to the next sibling
        // if nothing is currently highlighted, we highlight the first option
        cursor = hasCursor ? this._cursor + 1 : 0;
      }
      if (cursor < 0) cursor = max;else if (cursor > max) cursor = 0;
      this._moveWithArrowButton = true;
      this._cursor = cursor;
    },
    /**
     * Move cursor.
     * @param {number} dir
     */
    _moveSelectedCursor: function _moveSelectedCursor(dir) {
      var hasCursor = this._hasSelectedCursor();
      var max = this.__selectedIndex.length - 1;
      var cursor;
      if (dir < 0) {
        // if we have a cursor, we move to the previous sibling
        // if nothing is currently highlighted, we highlight the last option
        cursor = hasCursor ? this._selectedCursor - 1 : max;
      } else if (dir > 0) {
        // if we have a cursor, we move to the next sibling
        // if nothing is currently highlighted, we highlight the first option
        cursor = hasCursor ? this._selectedCursor + 1 : 0;
      }
      if (cursor < 0) cursor = max;else if (cursor > max) cursor = 0;
      this._moveWithArrowButton = true;
      this._selectedCursor = cursor;
    },
    _createTabs: function _createTabs(availableLength, selectedLength) {
      this._tabs = new _tabs["default"]({
        el: this._tabsEl,
        items: [{
          selector: ".eto-results-available",
          content: "Available <span class=\"eto-badge\">" + availableLength + "</span>",
          active: "true"
        }, {
          selector: ".eto-results-selected",
          content: "Selected <span class=\"eto-badge\">" + selectedLength + "</span>"
        }]
      });
      this._badges = this._tabs.queryAll('.eto-badge');
    },
    /**
     * Returns result from element.
     * @return {object}
     */
    _getResultFromElement: function _getResultFromElement(el) {
      var optionEl = _domManipulation["default"].getElementMatchingParent(el, '.eto-results__option', this._resultsEl);
      if (optionEl.getElementsByClassName('eto-checkbox__field').length > 0) {
        optionEl.getElementsByClassName('eto-checkbox__field')[0].setAttribute('checked', 'true');
      }
      this._tabs.enableTab(1);
      var index = parseInt(optionEl.getAttribute('data-index'), 10);
      var result = this.__index[index];
      return result;
    },
    /**
     * Ensure cursor is visible. If it is out of view, scroll into view.
     * @param {element} el
     */
    _ensureCursorVisible: function _ensureCursorVisible(cursor, selector) {
      var scrollEl = this._resultsEl.querySelector(selector + ' .eto-results__scroll');
      if (!scrollEl) return;
      if (this._cursor === 0) {
        scrollEl.scrollTop = 0;
      }
      if (cursor.nodeType === 1) {
        var elTop = cursor.offsetTop;
        var elBottom = elTop + cursor.offsetHeight;
        var nodeScrollTop = scrollEl.scrollTop;
        var nodeHeight = scrollEl.clientHeight; // height + padding
        if (elTop < nodeScrollTop) {
          scrollEl.scrollTop = elTop - nodeHeight;
        } else if (nodeScrollTop + nodeHeight < elBottom) {
          scrollEl.scrollTop = elBottom - nodeHeight;
        }
      }
    },
    /**
     * Set max height of dropdown based on available screen space.
     */
    _resizeHeight: function _resizeHeight(e, parentContainer) {
      // Set width.
      this._resizeWidth();
      if (this._isOpen) {
        // Set height.
        var el = this._resultsEl;
        var _dom$getElementOffset = _domManipulation["default"].getElementOffset(this._anchorEl),
          top = _dom$getElementOffset.top;
        var vh = viewHeight();
        var max;
        if (parentContainer) {
          var parentContainerElem = parentContainer.target;
          var _vh = parentContainerElem.offsetHeight;
          var _top = _domManipulation["default"].getElementOffset(this._anchorEl).top - _domManipulation["default"].getElementOffset(parentContainerElem).top;
          max = Math.min(Math.max(_vh + (typeof parentContainerElem.scrollY !== 'undefined' ? parentContainerElem.scrollTop : window.pageYOffset) - (_top + this._anchorEl.offsetHeight) - 10, _top - 10), _vh - 10);
        } else {
          var anchorElHeight = this._anchorEl.offsetHeight;
          // availableBottomspace is bottom space even with 0 scroll position
          var availableBottomspace = document.documentElement.offsetHeight - (anchorElHeight + top);
          // Available space is max of bottom available and top available space
          var availableSpace = Math.max(vh + (typeof window.scrollY !== 'undefined' ? window.scrollY : window.pageYOffset) - (top + this._anchorEl.offsetHeight) - 10, top - 10);
          // If available space is < 400 consider available space as max height for dropdown
          max = Math.min(availableSpace > 400 || availableBottomspace > 400 ? 400 : availableSpace, vh - 10);
          if (this._popupHeight) {
            max = this._popupHeight;
          }
        }
        max = max > 390 ? 390 : max;
        el.style.maxHeight = max + 'px';
        el.style.height = '';
        el.style.height = max + 'px';
        this._resultsEl.querySelector('.eto-results-available').style.height = max + 'px';
        this._popupHeight = max;
        // Update position.
        this._affixed.update();
      }
    },
    /**
     * Resize width based on field container.
     */
    _resizeWidth: function _resizeWidth() {
      var width = this._anchorEl.offsetWidth > 500 ? 500 : this._anchorEl.offsetWidth < 330 ? 330 : this._anchorEl.offsetWidth;
      this._resultsEl.style.minWidth = width + 'px';
      this._resultsEl.style.width = width + 'px';
    },
    /**
     * Highlights substrings a string that matches the regex.
     * @param {object} re - the regular expression
     * @param {string} str - the string to replace occurrences in
     * @return {number}
     */
    _highlightMatch: function _highlightMatch(re, str) {
      return str.replace(re, function (m) {
        return "<b>".concat(m, "</b>");
      });
    },
    /**
     * Renders options.
     * @param {array} collection - Available
     * @param {array} items - Selected
     * @param availableLength - Number of items available
     * @param selectedLength - Number of selected items
     * @param isEditable - If options are editable
     * @param isCopyToClipboardRequired - If copy to clipboard feature is enabled
     * @param loadMoreOptionsOnScrollMessage - Message if lazyload is enabled
     * @param highlight - weather to make text bold while templating
     * @param addTermIconName - Differentiate the add term icon between c-autocomplete and combobox components
     */
    _renderResults: function _renderResults(collection, items, availableLength, selectedLength, isCopyToClipboardRequired, isEditable, loadMoreOptionsOnScrollMessage, highlight, addTermIconName, supportHtmlAsOption) {
      // We index the collection for easy access,
      // and to add the index to each datum.
      this.__index = index(collection);
      this.__selectedIndex = index(items);
      // Render options from template.
      takeChildren(this._resultsEl, (0, _makeElement["default"])(_resultsComplex["default"].render({
        resultsId: this.resultsId,
        _tabs: this._tabs,
        selected: items,
        content: collection,
        resultColumns: this.resultColumns,
        isCopyToClipboardRequired: isCopyToClipboardRequired,
        isEditable: isEditable,
        loadMoreOptionsOnScrollMessage: loadMoreOptionsOnScrollMessage,
        availableLength: availableLength,
        highlight: highlight,
        addTermIconName: addTermIconName,
        supportHtmlAsOption: supportHtmlAsOption
      })));
      this._tabsEl = this._resultsEl.querySelector('.eto-tabs');
      this._createTabs(availableLength, selectedLength);
      if (selectedLength > 0) {
        this._tabs.enableTab(1);
      } else {
        this._tabs.disableTab(1);
      }

      // bind soft-scroll on new scrolling element
      this._bindSoftScroll();
      this._resizeHeight();
    },
    /**
     * Renders selected options.
     * @param {array} collection
     */
    _renderSelectedResults: function _renderSelectedResults(collection, items, availableLength, selectedLength, isCopyToClipboardRequired, isEditable) {
      // We index the collection for easy access,
      // and to add the index to each datum.
      this.__index = index(collection);
      index(items);

      // Render options from template.
      takeChildren(this._resultsEl, (0, _makeElement["default"])(_resultsComplex["default"].render({
        resultsId: this.resultsId,
        _tabs: this._tabs,
        selected: collection,
        isCopyToClipboardRequired: isCopyToClipboardRequired,
        isEditable: isEditable
      })));
      this._tabsEl = this._resultsEl.querySelector('.eto-tabs');
      this._createTabs(availableLength, selectedLength);
      this._tabs.setActive(1);

      // bind soft-scroll on new scrolling element
      this._bindSoftScroll();
      this._resizeHeight();
    },
    _resetCursors: function _resetCursors() {
      this._cursor = null;
      this._selectedCursor = null;
    }
  },
  bindings: {
    resultsId: [{
      type: 'attribute',
      name: 'id',
      selector: '.eto-results'
    }],
    _cursorEl: {
      type: function type(el, isActiveEl, wasActiveEl) {
        if (this._resultsEl) {
          // Remove active class from previous cursor
          if (wasActiveEl) {
            if (this._moveWithArrowButton) {
              //arrow event, need remove hover class
              _domManipulation["default"].removeClass(wasActiveEl, 'hover');
            }
          }

          // Add active or hover class to new cursor
          if (isActiveEl) {
            if (this._moveWithArrowButton) {
              _domManipulation["default"].addClass(isActiveEl, 'hover');
              this._ensureCursorVisible(isActiveEl, '.eto-results-available');
            }
          }
        }
        this._moveWithArrowButton = false;
      }
    },
    _selectedCursorEl: {
      type: function type(el, isActiveEl, wasActiveEl) {
        if (this._resultsEl) {
          // Remove active class from previous cursor
          if (wasActiveEl) {
            if (this._moveWithArrowButton) {
              //arrow event, need remove hover class
              _domManipulation["default"].removeClass(wasActiveEl, 'hover');
            }
          }

          // Add active or hover class to new cursor
          if (isActiveEl) {
            if (this._moveWithArrowButton) {
              _domManipulation["default"].addClass(isActiveEl, 'hover');
              this._ensureCursorVisible(isActiveEl, '.eto-results-selected');
            }
          }
        }
        this._moveWithArrowButton = false;
      }
    }
  }
}; /// Helpers
/**
 * Returns viewport height for all browsers.
 * @return {number}
 */
function viewHeight() {
  return window.innerHeight && document.documentElement.clientHeight ? Math.min(window.innerHeight, document.documentElement.clientHeight) : window.innerHeight || document.documentElement.clientHeight || document.getElementsByTagName('body')[0].clientHeight;
}

/**
 * Removes all children from one element to another.
 * @param {element} to
 * @param {element} from
 */
function takeChildren(to, from) {
  while (to.firstChild) {
    to.removeChild(to.firstChild);
  }
  while (from.firstChild) {
    to.appendChild(from.firstChild);
  }
}

/**
 * Indexes options for easy access, and add the index to each datum.
 * @param {array} collection
 */
function index(collection) {
  var index = [],
    i,
    j,
    k = 0;
  for (i = 0; i < collection.length; i++) {
    for (j = 0; j < collection[i].items.length; j++) {
      var item = collection[i].items[j];
      item._index = k++;
      index.push(item);
    }
  }
  return index;
}
module.exports = exports.default;


},{"../../../html/templates/precompiled/results-complex":14,"../components/tabs":22,"../helpers/dom-manipulation":26,"../helpers/events/window-events":27,"../helpers/make-element":29,"../helpers/position/affix":33,"../helpers/soft-scroll":35,"lodash/debounce":251,"lodash/map":286}],48:[function(require,module,exports){
var matches = require('matches-selector')

module.exports = function (element, selector, checkYoSelf) {
  var parent = checkYoSelf ? element : element.parentNode

  while (parent && parent !== document) {
    if (matches(parent, selector)) return parent;
    parent = parent.parentNode
  }
}

},{"matches-selector":49}],49:[function(require,module,exports){

/**
 * Element prototype.
 */

var proto = Element.prototype;

/**
 * Vendor function.
 */

var vendor = proto.matchesSelector
  || proto.webkitMatchesSelector
  || proto.mozMatchesSelector
  || proto.msMatchesSelector
  || proto.oMatchesSelector;

/**
 * Expose `match()`.
 */

module.exports = match;

/**
 * Match `el` to `selector`.
 *
 * @param {Element} el
 * @param {String} selector
 * @return {Boolean}
 * @api public
 */

function match(el, selector) {
  if (vendor) return vendor.call(el, selector);
  var nodes = el.parentNode.querySelectorAll(selector);
  for (var i = 0; i < nodes.length; ++i) {
    if (nodes[i] == el) return true;
  }
  return false;
}
},{}],50:[function(require,module,exports){
var bind = window.addEventListener ? 'addEventListener' : 'attachEvent',
    unbind = window.removeEventListener ? 'removeEventListener' : 'detachEvent',
    prefix = bind !== 'addEventListener' ? 'on' : '';

/**
 * Bind `el` event `type` to `fn`.
 *
 * @param {Element} el
 * @param {String} type
 * @param {Function} fn
 * @param {Boolean} capture
 * @return {Function}
 * @api public
 */

exports.bind = function(el, type, fn, capture){
  el[bind](prefix + type, fn, capture || false);
  return fn;
};

/**
 * Unbind `el` event `type`'s callback `fn`.
 *
 * @param {Element} el
 * @param {String} type
 * @param {Function} fn
 * @param {Boolean} capture
 * @return {Function}
 * @api public
 */

exports.unbind = function(el, type, fn, capture){
  el[unbind](prefix + type, fn, capture || false);
  return fn;
};
},{}],51:[function(require,module,exports){
/**
 * Module dependencies.
 */

var closest = require('closest')
  , event = require('component-event');

/**
 * Delegate event `type` to `selector`
 * and invoke `fn(e)`. A callback function
 * is returned which may be passed to `.unbind()`.
 *
 * @param {Element} el
 * @param {String} selector
 * @param {String} type
 * @param {Function} fn
 * @param {Boolean} capture
 * @return {Function}
 * @api public
 */

// Some events don't bubble, so we want to bind to the capture phase instead
// when delegating.
var forceCaptureEvents = ['focus', 'blur'];

exports.bind = function(el, selector, type, fn, capture){
  if (forceCaptureEvents.indexOf(type) !== -1) capture = true;

  return event.bind(el, type, function(e){
    var target = e.target || e.srcElement;
    e.delegateTarget = closest(target, selector, true, el);
    if (e.delegateTarget) fn.call(el, e);
  }, capture);
};

/**
 * Unbind event `type`'s callback `fn`.
 *
 * @param {Element} el
 * @param {String} type
 * @param {Function} fn
 * @param {Boolean} capture
 * @api public
 */

exports.unbind = function(el, type, fn, capture){
  if (forceCaptureEvents.indexOf(type) !== -1) capture = true;

  event.unbind(el, type, fn, capture);
};

},{"closest":48,"component-event":50}],52:[function(require,module,exports){

/**
 * Module dependencies.
 */

var events = require('component-event');
var delegate = require('delegate-events');
var forceCaptureEvents = ['focus', 'blur'];

/**
 * Expose `Events`.
 */

module.exports = Events;

/**
 * Initialize an `Events` with the given
 * `el` object which events will be bound to,
 * and the `obj` which will receive method calls.
 *
 * @param {Object} el
 * @param {Object} obj
 * @api public
 */

function Events(el, obj) {
  if (!(this instanceof Events)) return new Events(el, obj);
  if (!el) throw new Error('element required');
  if (!obj) throw new Error('object required');
  this.el = el;
  this.obj = obj;
  this._events = {};
}

/**
 * Subscription helper.
 */

Events.prototype.sub = function(event, method, cb){
  this._events[event] = this._events[event] || {};
  this._events[event][method] = cb;
};

/**
 * Bind to `event` with optional `method` name.
 * When `method` is undefined it becomes `event`
 * with the "on" prefix.
 *
 * Examples:
 *
 *  Direct event handling:
 *
 *    events.bind('click') // implies "onclick"
 *    events.bind('click', 'remove')
 *    events.bind('click', 'sort', 'asc')
 *
 *  Delegated event handling:
 *
 *    events.bind('click li > a')
 *    events.bind('click li > a', 'remove')
 *    events.bind('click a.sort-ascending', 'sort', 'asc')
 *    events.bind('click a.sort-descending', 'sort', 'desc')
 *
 *  Multiple events handling:
 *
 *    events.bind({
 *      'click .remove': 'remove',
 *      'click .add': 'add'
 *    });
 *
 * @param {String|object} - object is used for multiple binding,
 *                               string for single event binding
 * @param {String|function} [arg2] - method to call (optional)
 * @param {*} [arg3] - data for single event binding (optional)
 * @return {Function} callback
 * @api public
 */

Events.prototype.bind = function(arg1, arg2){
  var bindEvent = function(event, method) {
    var e = parse(event);
    var el = this.el;
    var obj = this.obj;
    var name = e.name;
    var method = method || 'on' + name;
    var args = [].slice.call(arguments, 2);

    // callback
    function cb(){
      var a = [].slice.call(arguments).concat(args);

      if (typeof method === 'function') {
          method.apply(obj, a);
          return;
      }

      if (!obj[method]) {
          throw new Error(method + ' method is not defined');
      } else {
          obj[method].apply(obj, a);
      }
    }

    // bind
    if (e.selector) {
      cb = delegate.bind(el, e.selector, name, cb);
    } else {
      events.bind(el, name, cb);
    }

    // subscription for unbinding
    this.sub(name, method, cb);

    return cb;
  };

  if (typeof arg1 == 'string') {
    bindEvent.apply(this, arguments);
  } else {
    for(var key in arg1) {
      if (arg1.hasOwnProperty(key)) {
        bindEvent.call(this, key, arg1[key]);
      }
    }
  }
};

/**
 * Unbind a single binding, all bindings for `event`,
 * or all bindings within the manager.
 *
 * Examples:
 *
 *  Unbind direct handlers:
 *
 *     events.unbind('click', 'remove')
 *     events.unbind('click')
 *     events.unbind()
 *
 * Unbind delegate handlers:
 *
 *     events.unbind('click', 'remove')
 *     events.unbind('click')
 *     events.unbind()
 *
 * @param {String|Function} [event]
 * @param {String|Function} [method]
 * @api public
 */

Events.prototype.unbind = function(event, method){
  if (0 == arguments.length) return this.unbindAll();
  if (1 == arguments.length) return this.unbindAllOf(event);

  // no bindings for this event
  var bindings = this._events[event];
  var capture = (forceCaptureEvents.indexOf(event) !== -1);
  if (!bindings) return;

  // no bindings for this method
  var cb = bindings[method];
  if (!cb) return;

  events.unbind(this.el, event, cb, capture);
};

/**
 * Unbind all events.
 *
 * @api private
 */

Events.prototype.unbindAll = function(){
  for (var event in this._events) {
    this.unbindAllOf(event);
  }
};

/**
 * Unbind all events for `event`.
 *
 * @param {String} event
 * @api private
 */

Events.prototype.unbindAllOf = function(event){
  var bindings = this._events[event];
  if (!bindings) return;

  for (var method in bindings) {
    this.unbind(event, method);
  }
};

/**
 * Parse `event`.
 *
 * @param {String} event
 * @return {Object}
 * @api private
 */

function parse(event) {
  var parts = event.split(/ +/);
  return {
    name: parts.shift(),
    selector: parts.join(' ')
  }
}

},{"component-event":50,"delegate-events":51}],53:[function(require,module,exports){
(function (global){(function (){
/*! https://mths.be/he v1.2.0 by @mathias | MIT license */
;(function(root) {

	// Detect free variables `exports`.
	var freeExports = typeof exports == 'object' && exports;

	// Detect free variable `module`.
	var freeModule = typeof module == 'object' && module &&
		module.exports == freeExports && module;

	// Detect free variable `global`, from Node.js or Browserified code,
	// and use it as `root`.
	var freeGlobal = typeof global == 'object' && global;
	if (freeGlobal.global === freeGlobal || freeGlobal.window === freeGlobal) {
		root = freeGlobal;
	}

	/*--------------------------------------------------------------------------*/

	// All astral symbols.
	var regexAstralSymbols = /[\uD800-\uDBFF][\uDC00-\uDFFF]/g;
	// All ASCII symbols (not just printable ASCII) except those listed in the
	// first column of the overrides table.
	// https://html.spec.whatwg.org/multipage/syntax.html#table-charref-overrides
	var regexAsciiWhitelist = /[\x01-\x7F]/g;
	// All BMP symbols that are not ASCII newlines, printable ASCII symbols, or
	// code points listed in the first column of the overrides table on
	// https://html.spec.whatwg.org/multipage/syntax.html#table-charref-overrides.
	var regexBmpWhitelist = /[\x01-\t\x0B\f\x0E-\x1F\x7F\x81\x8D\x8F\x90\x9D\xA0-\uFFFF]/g;

	var regexEncodeNonAscii = /<\u20D2|=\u20E5|>\u20D2|\u205F\u200A|\u219D\u0338|\u2202\u0338|\u2220\u20D2|\u2229\uFE00|\u222A\uFE00|\u223C\u20D2|\u223D\u0331|\u223E\u0333|\u2242\u0338|\u224B\u0338|\u224D\u20D2|\u224E\u0338|\u224F\u0338|\u2250\u0338|\u2261\u20E5|\u2264\u20D2|\u2265\u20D2|\u2266\u0338|\u2267\u0338|\u2268\uFE00|\u2269\uFE00|\u226A\u0338|\u226A\u20D2|\u226B\u0338|\u226B\u20D2|\u227F\u0338|\u2282\u20D2|\u2283\u20D2|\u228A\uFE00|\u228B\uFE00|\u228F\u0338|\u2290\u0338|\u2293\uFE00|\u2294\uFE00|\u22B4\u20D2|\u22B5\u20D2|\u22D8\u0338|\u22D9\u0338|\u22DA\uFE00|\u22DB\uFE00|\u22F5\u0338|\u22F9\u0338|\u2933\u0338|\u29CF\u0338|\u29D0\u0338|\u2A6D\u0338|\u2A70\u0338|\u2A7D\u0338|\u2A7E\u0338|\u2AA1\u0338|\u2AA2\u0338|\u2AAC\uFE00|\u2AAD\uFE00|\u2AAF\u0338|\u2AB0\u0338|\u2AC5\u0338|\u2AC6\u0338|\u2ACB\uFE00|\u2ACC\uFE00|\u2AFD\u20E5|[\xA0-\u0113\u0116-\u0122\u0124-\u012B\u012E-\u014D\u0150-\u017E\u0192\u01B5\u01F5\u0237\u02C6\u02C7\u02D8-\u02DD\u0311\u0391-\u03A1\u03A3-\u03A9\u03B1-\u03C9\u03D1\u03D2\u03D5\u03D6\u03DC\u03DD\u03F0\u03F1\u03F5\u03F6\u0401-\u040C\u040E-\u044F\u0451-\u045C\u045E\u045F\u2002-\u2005\u2007-\u2010\u2013-\u2016\u2018-\u201A\u201C-\u201E\u2020-\u2022\u2025\u2026\u2030-\u2035\u2039\u203A\u203E\u2041\u2043\u2044\u204F\u2057\u205F-\u2063\u20AC\u20DB\u20DC\u2102\u2105\u210A-\u2113\u2115-\u211E\u2122\u2124\u2127-\u2129\u212C\u212D\u212F-\u2131\u2133-\u2138\u2145-\u2148\u2153-\u215E\u2190-\u219B\u219D-\u21A7\u21A9-\u21AE\u21B0-\u21B3\u21B5-\u21B7\u21BA-\u21DB\u21DD\u21E4\u21E5\u21F5\u21FD-\u2205\u2207-\u2209\u220B\u220C\u220F-\u2214\u2216-\u2218\u221A\u221D-\u2238\u223A-\u2257\u2259\u225A\u225C\u225F-\u2262\u2264-\u228B\u228D-\u229B\u229D-\u22A5\u22A7-\u22B0\u22B2-\u22BB\u22BD-\u22DB\u22DE-\u22E3\u22E6-\u22F7\u22F9-\u22FE\u2305\u2306\u2308-\u2310\u2312\u2313\u2315\u2316\u231C-\u231F\u2322\u2323\u232D\u232E\u2336\u233D\u233F\u237C\u23B0\u23B1\u23B4-\u23B6\u23DC-\u23DF\u23E2\u23E7\u2423\u24C8\u2500\u2502\u250C\u2510\u2514\u2518\u251C\u2524\u252C\u2534\u253C\u2550-\u256C\u2580\u2584\u2588\u2591-\u2593\u25A1\u25AA\u25AB\u25AD\u25AE\u25B1\u25B3-\u25B5\u25B8\u25B9\u25BD-\u25BF\u25C2\u25C3\u25CA\u25CB\u25EC\u25EF\u25F8-\u25FC\u2605\u2606\u260E\u2640\u2642\u2660\u2663\u2665\u2666\u266A\u266D-\u266F\u2713\u2717\u2720\u2736\u2758\u2772\u2773\u27C8\u27C9\u27E6-\u27ED\u27F5-\u27FA\u27FC\u27FF\u2902-\u2905\u290C-\u2913\u2916\u2919-\u2920\u2923-\u292A\u2933\u2935-\u2939\u293C\u293D\u2945\u2948-\u294B\u294E-\u2976\u2978\u2979\u297B-\u297F\u2985\u2986\u298B-\u2996\u299A\u299C\u299D\u29A4-\u29B7\u29B9\u29BB\u29BC\u29BE-\u29C5\u29C9\u29CD-\u29D0\u29DC-\u29DE\u29E3-\u29E5\u29EB\u29F4\u29F6\u2A00-\u2A02\u2A04\u2A06\u2A0C\u2A0D\u2A10-\u2A17\u2A22-\u2A27\u2A29\u2A2A\u2A2D-\u2A31\u2A33-\u2A3C\u2A3F\u2A40\u2A42-\u2A4D\u2A50\u2A53-\u2A58\u2A5A-\u2A5D\u2A5F\u2A66\u2A6A\u2A6D-\u2A75\u2A77-\u2A9A\u2A9D-\u2AA2\u2AA4-\u2AB0\u2AB3-\u2AC8\u2ACB\u2ACC\u2ACF-\u2ADB\u2AE4\u2AE6-\u2AE9\u2AEB-\u2AF3\u2AFD\uFB00-\uFB04]|\uD835[\uDC9C\uDC9E\uDC9F\uDCA2\uDCA5\uDCA6\uDCA9-\uDCAC\uDCAE-\uDCB9\uDCBB\uDCBD-\uDCC3\uDCC5-\uDCCF\uDD04\uDD05\uDD07-\uDD0A\uDD0D-\uDD14\uDD16-\uDD1C\uDD1E-\uDD39\uDD3B-\uDD3E\uDD40-\uDD44\uDD46\uDD4A-\uDD50\uDD52-\uDD6B]/g;
	var encodeMap = {'\xAD':'shy','\u200C':'zwnj','\u200D':'zwj','\u200E':'lrm','\u2063':'ic','\u2062':'it','\u2061':'af','\u200F':'rlm','\u200B':'ZeroWidthSpace','\u2060':'NoBreak','\u0311':'DownBreve','\u20DB':'tdot','\u20DC':'DotDot','\t':'Tab','\n':'NewLine','\u2008':'puncsp','\u205F':'MediumSpace','\u2009':'thinsp','\u200A':'hairsp','\u2004':'emsp13','\u2002':'ensp','\u2005':'emsp14','\u2003':'emsp','\u2007':'numsp','\xA0':'nbsp','\u205F\u200A':'ThickSpace','\u203E':'oline','_':'lowbar','\u2010':'dash','\u2013':'ndash','\u2014':'mdash','\u2015':'horbar',',':'comma',';':'semi','\u204F':'bsemi',':':'colon','\u2A74':'Colone','!':'excl','\xA1':'iexcl','?':'quest','\xBF':'iquest','.':'period','\u2025':'nldr','\u2026':'mldr','\xB7':'middot','\'':'apos','\u2018':'lsquo','\u2019':'rsquo','\u201A':'sbquo','\u2039':'lsaquo','\u203A':'rsaquo','"':'quot','\u201C':'ldquo','\u201D':'rdquo','\u201E':'bdquo','\xAB':'laquo','\xBB':'raquo','(':'lpar',')':'rpar','[':'lsqb',']':'rsqb','{':'lcub','}':'rcub','\u2308':'lceil','\u2309':'rceil','\u230A':'lfloor','\u230B':'rfloor','\u2985':'lopar','\u2986':'ropar','\u298B':'lbrke','\u298C':'rbrke','\u298D':'lbrkslu','\u298E':'rbrksld','\u298F':'lbrksld','\u2990':'rbrkslu','\u2991':'langd','\u2992':'rangd','\u2993':'lparlt','\u2994':'rpargt','\u2995':'gtlPar','\u2996':'ltrPar','\u27E6':'lobrk','\u27E7':'robrk','\u27E8':'lang','\u27E9':'rang','\u27EA':'Lang','\u27EB':'Rang','\u27EC':'loang','\u27ED':'roang','\u2772':'lbbrk','\u2773':'rbbrk','\u2016':'Vert','\xA7':'sect','\xB6':'para','@':'commat','*':'ast','/':'sol','undefined':null,'&':'amp','#':'num','%':'percnt','\u2030':'permil','\u2031':'pertenk','\u2020':'dagger','\u2021':'Dagger','\u2022':'bull','\u2043':'hybull','\u2032':'prime','\u2033':'Prime','\u2034':'tprime','\u2057':'qprime','\u2035':'bprime','\u2041':'caret','`':'grave','\xB4':'acute','\u02DC':'tilde','^':'Hat','\xAF':'macr','\u02D8':'breve','\u02D9':'dot','\xA8':'die','\u02DA':'ring','\u02DD':'dblac','\xB8':'cedil','\u02DB':'ogon','\u02C6':'circ','\u02C7':'caron','\xB0':'deg','\xA9':'copy','\xAE':'reg','\u2117':'copysr','\u2118':'wp','\u211E':'rx','\u2127':'mho','\u2129':'iiota','\u2190':'larr','\u219A':'nlarr','\u2192':'rarr','\u219B':'nrarr','\u2191':'uarr','\u2193':'darr','\u2194':'harr','\u21AE':'nharr','\u2195':'varr','\u2196':'nwarr','\u2197':'nearr','\u2198':'searr','\u2199':'swarr','\u219D':'rarrw','\u219D\u0338':'nrarrw','\u219E':'Larr','\u219F':'Uarr','\u21A0':'Rarr','\u21A1':'Darr','\u21A2':'larrtl','\u21A3':'rarrtl','\u21A4':'mapstoleft','\u21A5':'mapstoup','\u21A6':'map','\u21A7':'mapstodown','\u21A9':'larrhk','\u21AA':'rarrhk','\u21AB':'larrlp','\u21AC':'rarrlp','\u21AD':'harrw','\u21B0':'lsh','\u21B1':'rsh','\u21B2':'ldsh','\u21B3':'rdsh','\u21B5':'crarr','\u21B6':'cularr','\u21B7':'curarr','\u21BA':'olarr','\u21BB':'orarr','\u21BC':'lharu','\u21BD':'lhard','\u21BE':'uharr','\u21BF':'uharl','\u21C0':'rharu','\u21C1':'rhard','\u21C2':'dharr','\u21C3':'dharl','\u21C4':'rlarr','\u21C5':'udarr','\u21C6':'lrarr','\u21C7':'llarr','\u21C8':'uuarr','\u21C9':'rrarr','\u21CA':'ddarr','\u21CB':'lrhar','\u21CC':'rlhar','\u21D0':'lArr','\u21CD':'nlArr','\u21D1':'uArr','\u21D2':'rArr','\u21CF':'nrArr','\u21D3':'dArr','\u21D4':'iff','\u21CE':'nhArr','\u21D5':'vArr','\u21D6':'nwArr','\u21D7':'neArr','\u21D8':'seArr','\u21D9':'swArr','\u21DA':'lAarr','\u21DB':'rAarr','\u21DD':'zigrarr','\u21E4':'larrb','\u21E5':'rarrb','\u21F5':'duarr','\u21FD':'loarr','\u21FE':'roarr','\u21FF':'hoarr','\u2200':'forall','\u2201':'comp','\u2202':'part','\u2202\u0338':'npart','\u2203':'exist','\u2204':'nexist','\u2205':'empty','\u2207':'Del','\u2208':'in','\u2209':'notin','\u220B':'ni','\u220C':'notni','\u03F6':'bepsi','\u220F':'prod','\u2210':'coprod','\u2211':'sum','+':'plus','\xB1':'pm','\xF7':'div','\xD7':'times','<':'lt','\u226E':'nlt','<\u20D2':'nvlt','=':'equals','\u2260':'ne','=\u20E5':'bne','\u2A75':'Equal','>':'gt','\u226F':'ngt','>\u20D2':'nvgt','\xAC':'not','|':'vert','\xA6':'brvbar','\u2212':'minus','\u2213':'mp','\u2214':'plusdo','\u2044':'frasl','\u2216':'setmn','\u2217':'lowast','\u2218':'compfn','\u221A':'Sqrt','\u221D':'prop','\u221E':'infin','\u221F':'angrt','\u2220':'ang','\u2220\u20D2':'nang','\u2221':'angmsd','\u2222':'angsph','\u2223':'mid','\u2224':'nmid','\u2225':'par','\u2226':'npar','\u2227':'and','\u2228':'or','\u2229':'cap','\u2229\uFE00':'caps','\u222A':'cup','\u222A\uFE00':'cups','\u222B':'int','\u222C':'Int','\u222D':'tint','\u2A0C':'qint','\u222E':'oint','\u222F':'Conint','\u2230':'Cconint','\u2231':'cwint','\u2232':'cwconint','\u2233':'awconint','\u2234':'there4','\u2235':'becaus','\u2236':'ratio','\u2237':'Colon','\u2238':'minusd','\u223A':'mDDot','\u223B':'homtht','\u223C':'sim','\u2241':'nsim','\u223C\u20D2':'nvsim','\u223D':'bsim','\u223D\u0331':'race','\u223E':'ac','\u223E\u0333':'acE','\u223F':'acd','\u2240':'wr','\u2242':'esim','\u2242\u0338':'nesim','\u2243':'sime','\u2244':'nsime','\u2245':'cong','\u2247':'ncong','\u2246':'simne','\u2248':'ap','\u2249':'nap','\u224A':'ape','\u224B':'apid','\u224B\u0338':'napid','\u224C':'bcong','\u224D':'CupCap','\u226D':'NotCupCap','\u224D\u20D2':'nvap','\u224E':'bump','\u224E\u0338':'nbump','\u224F':'bumpe','\u224F\u0338':'nbumpe','\u2250':'doteq','\u2250\u0338':'nedot','\u2251':'eDot','\u2252':'efDot','\u2253':'erDot','\u2254':'colone','\u2255':'ecolon','\u2256':'ecir','\u2257':'cire','\u2259':'wedgeq','\u225A':'veeeq','\u225C':'trie','\u225F':'equest','\u2261':'equiv','\u2262':'nequiv','\u2261\u20E5':'bnequiv','\u2264':'le','\u2270':'nle','\u2264\u20D2':'nvle','\u2265':'ge','\u2271':'nge','\u2265\u20D2':'nvge','\u2266':'lE','\u2266\u0338':'nlE','\u2267':'gE','\u2267\u0338':'ngE','\u2268\uFE00':'lvnE','\u2268':'lnE','\u2269':'gnE','\u2269\uFE00':'gvnE','\u226A':'ll','\u226A\u0338':'nLtv','\u226A\u20D2':'nLt','\u226B':'gg','\u226B\u0338':'nGtv','\u226B\u20D2':'nGt','\u226C':'twixt','\u2272':'lsim','\u2274':'nlsim','\u2273':'gsim','\u2275':'ngsim','\u2276':'lg','\u2278':'ntlg','\u2277':'gl','\u2279':'ntgl','\u227A':'pr','\u2280':'npr','\u227B':'sc','\u2281':'nsc','\u227C':'prcue','\u22E0':'nprcue','\u227D':'sccue','\u22E1':'nsccue','\u227E':'prsim','\u227F':'scsim','\u227F\u0338':'NotSucceedsTilde','\u2282':'sub','\u2284':'nsub','\u2282\u20D2':'vnsub','\u2283':'sup','\u2285':'nsup','\u2283\u20D2':'vnsup','\u2286':'sube','\u2288':'nsube','\u2287':'supe','\u2289':'nsupe','\u228A\uFE00':'vsubne','\u228A':'subne','\u228B\uFE00':'vsupne','\u228B':'supne','\u228D':'cupdot','\u228E':'uplus','\u228F':'sqsub','\u228F\u0338':'NotSquareSubset','\u2290':'sqsup','\u2290\u0338':'NotSquareSuperset','\u2291':'sqsube','\u22E2':'nsqsube','\u2292':'sqsupe','\u22E3':'nsqsupe','\u2293':'sqcap','\u2293\uFE00':'sqcaps','\u2294':'sqcup','\u2294\uFE00':'sqcups','\u2295':'oplus','\u2296':'ominus','\u2297':'otimes','\u2298':'osol','\u2299':'odot','\u229A':'ocir','\u229B':'oast','\u229D':'odash','\u229E':'plusb','\u229F':'minusb','\u22A0':'timesb','\u22A1':'sdotb','\u22A2':'vdash','\u22AC':'nvdash','\u22A3':'dashv','\u22A4':'top','\u22A5':'bot','\u22A7':'models','\u22A8':'vDash','\u22AD':'nvDash','\u22A9':'Vdash','\u22AE':'nVdash','\u22AA':'Vvdash','\u22AB':'VDash','\u22AF':'nVDash','\u22B0':'prurel','\u22B2':'vltri','\u22EA':'nltri','\u22B3':'vrtri','\u22EB':'nrtri','\u22B4':'ltrie','\u22EC':'nltrie','\u22B4\u20D2':'nvltrie','\u22B5':'rtrie','\u22ED':'nrtrie','\u22B5\u20D2':'nvrtrie','\u22B6':'origof','\u22B7':'imof','\u22B8':'mumap','\u22B9':'hercon','\u22BA':'intcal','\u22BB':'veebar','\u22BD':'barvee','\u22BE':'angrtvb','\u22BF':'lrtri','\u22C0':'Wedge','\u22C1':'Vee','\u22C2':'xcap','\u22C3':'xcup','\u22C4':'diam','\u22C5':'sdot','\u22C6':'Star','\u22C7':'divonx','\u22C8':'bowtie','\u22C9':'ltimes','\u22CA':'rtimes','\u22CB':'lthree','\u22CC':'rthree','\u22CD':'bsime','\u22CE':'cuvee','\u22CF':'cuwed','\u22D0':'Sub','\u22D1':'Sup','\u22D2':'Cap','\u22D3':'Cup','\u22D4':'fork','\u22D5':'epar','\u22D6':'ltdot','\u22D7':'gtdot','\u22D8':'Ll','\u22D8\u0338':'nLl','\u22D9':'Gg','\u22D9\u0338':'nGg','\u22DA\uFE00':'lesg','\u22DA':'leg','\u22DB':'gel','\u22DB\uFE00':'gesl','\u22DE':'cuepr','\u22DF':'cuesc','\u22E6':'lnsim','\u22E7':'gnsim','\u22E8':'prnsim','\u22E9':'scnsim','\u22EE':'vellip','\u22EF':'ctdot','\u22F0':'utdot','\u22F1':'dtdot','\u22F2':'disin','\u22F3':'isinsv','\u22F4':'isins','\u22F5':'isindot','\u22F5\u0338':'notindot','\u22F6':'notinvc','\u22F7':'notinvb','\u22F9':'isinE','\u22F9\u0338':'notinE','\u22FA':'nisd','\u22FB':'xnis','\u22FC':'nis','\u22FD':'notnivc','\u22FE':'notnivb','\u2305':'barwed','\u2306':'Barwed','\u230C':'drcrop','\u230D':'dlcrop','\u230E':'urcrop','\u230F':'ulcrop','\u2310':'bnot','\u2312':'profline','\u2313':'profsurf','\u2315':'telrec','\u2316':'target','\u231C':'ulcorn','\u231D':'urcorn','\u231E':'dlcorn','\u231F':'drcorn','\u2322':'frown','\u2323':'smile','\u232D':'cylcty','\u232E':'profalar','\u2336':'topbot','\u233D':'ovbar','\u233F':'solbar','\u237C':'angzarr','\u23B0':'lmoust','\u23B1':'rmoust','\u23B4':'tbrk','\u23B5':'bbrk','\u23B6':'bbrktbrk','\u23DC':'OverParenthesis','\u23DD':'UnderParenthesis','\u23DE':'OverBrace','\u23DF':'UnderBrace','\u23E2':'trpezium','\u23E7':'elinters','\u2423':'blank','\u2500':'boxh','\u2502':'boxv','\u250C':'boxdr','\u2510':'boxdl','\u2514':'boxur','\u2518':'boxul','\u251C':'boxvr','\u2524':'boxvl','\u252C':'boxhd','\u2534':'boxhu','\u253C':'boxvh','\u2550':'boxH','\u2551':'boxV','\u2552':'boxdR','\u2553':'boxDr','\u2554':'boxDR','\u2555':'boxdL','\u2556':'boxDl','\u2557':'boxDL','\u2558':'boxuR','\u2559':'boxUr','\u255A':'boxUR','\u255B':'boxuL','\u255C':'boxUl','\u255D':'boxUL','\u255E':'boxvR','\u255F':'boxVr','\u2560':'boxVR','\u2561':'boxvL','\u2562':'boxVl','\u2563':'boxVL','\u2564':'boxHd','\u2565':'boxhD','\u2566':'boxHD','\u2567':'boxHu','\u2568':'boxhU','\u2569':'boxHU','\u256A':'boxvH','\u256B':'boxVh','\u256C':'boxVH','\u2580':'uhblk','\u2584':'lhblk','\u2588':'block','\u2591':'blk14','\u2592':'blk12','\u2593':'blk34','\u25A1':'squ','\u25AA':'squf','\u25AB':'EmptyVerySmallSquare','\u25AD':'rect','\u25AE':'marker','\u25B1':'fltns','\u25B3':'xutri','\u25B4':'utrif','\u25B5':'utri','\u25B8':'rtrif','\u25B9':'rtri','\u25BD':'xdtri','\u25BE':'dtrif','\u25BF':'dtri','\u25C2':'ltrif','\u25C3':'ltri','\u25CA':'loz','\u25CB':'cir','\u25EC':'tridot','\u25EF':'xcirc','\u25F8':'ultri','\u25F9':'urtri','\u25FA':'lltri','\u25FB':'EmptySmallSquare','\u25FC':'FilledSmallSquare','\u2605':'starf','\u2606':'star','\u260E':'phone','\u2640':'female','\u2642':'male','\u2660':'spades','\u2663':'clubs','\u2665':'hearts','\u2666':'diams','\u266A':'sung','\u2713':'check','\u2717':'cross','\u2720':'malt','\u2736':'sext','\u2758':'VerticalSeparator','\u27C8':'bsolhsub','\u27C9':'suphsol','\u27F5':'xlarr','\u27F6':'xrarr','\u27F7':'xharr','\u27F8':'xlArr','\u27F9':'xrArr','\u27FA':'xhArr','\u27FC':'xmap','\u27FF':'dzigrarr','\u2902':'nvlArr','\u2903':'nvrArr','\u2904':'nvHarr','\u2905':'Map','\u290C':'lbarr','\u290D':'rbarr','\u290E':'lBarr','\u290F':'rBarr','\u2910':'RBarr','\u2911':'DDotrahd','\u2912':'UpArrowBar','\u2913':'DownArrowBar','\u2916':'Rarrtl','\u2919':'latail','\u291A':'ratail','\u291B':'lAtail','\u291C':'rAtail','\u291D':'larrfs','\u291E':'rarrfs','\u291F':'larrbfs','\u2920':'rarrbfs','\u2923':'nwarhk','\u2924':'nearhk','\u2925':'searhk','\u2926':'swarhk','\u2927':'nwnear','\u2928':'toea','\u2929':'tosa','\u292A':'swnwar','\u2933':'rarrc','\u2933\u0338':'nrarrc','\u2935':'cudarrr','\u2936':'ldca','\u2937':'rdca','\u2938':'cudarrl','\u2939':'larrpl','\u293C':'curarrm','\u293D':'cularrp','\u2945':'rarrpl','\u2948':'harrcir','\u2949':'Uarrocir','\u294A':'lurdshar','\u294B':'ldrushar','\u294E':'LeftRightVector','\u294F':'RightUpDownVector','\u2950':'DownLeftRightVector','\u2951':'LeftUpDownVector','\u2952':'LeftVectorBar','\u2953':'RightVectorBar','\u2954':'RightUpVectorBar','\u2955':'RightDownVectorBar','\u2956':'DownLeftVectorBar','\u2957':'DownRightVectorBar','\u2958':'LeftUpVectorBar','\u2959':'LeftDownVectorBar','\u295A':'LeftTeeVector','\u295B':'RightTeeVector','\u295C':'RightUpTeeVector','\u295D':'RightDownTeeVector','\u295E':'DownLeftTeeVector','\u295F':'DownRightTeeVector','\u2960':'LeftUpTeeVector','\u2961':'LeftDownTeeVector','\u2962':'lHar','\u2963':'uHar','\u2964':'rHar','\u2965':'dHar','\u2966':'luruhar','\u2967':'ldrdhar','\u2968':'ruluhar','\u2969':'rdldhar','\u296A':'lharul','\u296B':'llhard','\u296C':'rharul','\u296D':'lrhard','\u296E':'udhar','\u296F':'duhar','\u2970':'RoundImplies','\u2971':'erarr','\u2972':'simrarr','\u2973':'larrsim','\u2974':'rarrsim','\u2975':'rarrap','\u2976':'ltlarr','\u2978':'gtrarr','\u2979':'subrarr','\u297B':'suplarr','\u297C':'lfisht','\u297D':'rfisht','\u297E':'ufisht','\u297F':'dfisht','\u299A':'vzigzag','\u299C':'vangrt','\u299D':'angrtvbd','\u29A4':'ange','\u29A5':'range','\u29A6':'dwangle','\u29A7':'uwangle','\u29A8':'angmsdaa','\u29A9':'angmsdab','\u29AA':'angmsdac','\u29AB':'angmsdad','\u29AC':'angmsdae','\u29AD':'angmsdaf','\u29AE':'angmsdag','\u29AF':'angmsdah','\u29B0':'bemptyv','\u29B1':'demptyv','\u29B2':'cemptyv','\u29B3':'raemptyv','\u29B4':'laemptyv','\u29B5':'ohbar','\u29B6':'omid','\u29B7':'opar','\u29B9':'operp','\u29BB':'olcross','\u29BC':'odsold','\u29BE':'olcir','\u29BF':'ofcir','\u29C0':'olt','\u29C1':'ogt','\u29C2':'cirscir','\u29C3':'cirE','\u29C4':'solb','\u29C5':'bsolb','\u29C9':'boxbox','\u29CD':'trisb','\u29CE':'rtriltri','\u29CF':'LeftTriangleBar','\u29CF\u0338':'NotLeftTriangleBar','\u29D0':'RightTriangleBar','\u29D0\u0338':'NotRightTriangleBar','\u29DC':'iinfin','\u29DD':'infintie','\u29DE':'nvinfin','\u29E3':'eparsl','\u29E4':'smeparsl','\u29E5':'eqvparsl','\u29EB':'lozf','\u29F4':'RuleDelayed','\u29F6':'dsol','\u2A00':'xodot','\u2A01':'xoplus','\u2A02':'xotime','\u2A04':'xuplus','\u2A06':'xsqcup','\u2A0D':'fpartint','\u2A10':'cirfnint','\u2A11':'awint','\u2A12':'rppolint','\u2A13':'scpolint','\u2A14':'npolint','\u2A15':'pointint','\u2A16':'quatint','\u2A17':'intlarhk','\u2A22':'pluscir','\u2A23':'plusacir','\u2A24':'simplus','\u2A25':'plusdu','\u2A26':'plussim','\u2A27':'plustwo','\u2A29':'mcomma','\u2A2A':'minusdu','\u2A2D':'loplus','\u2A2E':'roplus','\u2A2F':'Cross','\u2A30':'timesd','\u2A31':'timesbar','\u2A33':'smashp','\u2A34':'lotimes','\u2A35':'rotimes','\u2A36':'otimesas','\u2A37':'Otimes','\u2A38':'odiv','\u2A39':'triplus','\u2A3A':'triminus','\u2A3B':'tritime','\u2A3C':'iprod','\u2A3F':'amalg','\u2A40':'capdot','\u2A42':'ncup','\u2A43':'ncap','\u2A44':'capand','\u2A45':'cupor','\u2A46':'cupcap','\u2A47':'capcup','\u2A48':'cupbrcap','\u2A49':'capbrcup','\u2A4A':'cupcup','\u2A4B':'capcap','\u2A4C':'ccups','\u2A4D':'ccaps','\u2A50':'ccupssm','\u2A53':'And','\u2A54':'Or','\u2A55':'andand','\u2A56':'oror','\u2A57':'orslope','\u2A58':'andslope','\u2A5A':'andv','\u2A5B':'orv','\u2A5C':'andd','\u2A5D':'ord','\u2A5F':'wedbar','\u2A66':'sdote','\u2A6A':'simdot','\u2A6D':'congdot','\u2A6D\u0338':'ncongdot','\u2A6E':'easter','\u2A6F':'apacir','\u2A70':'apE','\u2A70\u0338':'napE','\u2A71':'eplus','\u2A72':'pluse','\u2A73':'Esim','\u2A77':'eDDot','\u2A78':'equivDD','\u2A79':'ltcir','\u2A7A':'gtcir','\u2A7B':'ltquest','\u2A7C':'gtquest','\u2A7D':'les','\u2A7D\u0338':'nles','\u2A7E':'ges','\u2A7E\u0338':'nges','\u2A7F':'lesdot','\u2A80':'gesdot','\u2A81':'lesdoto','\u2A82':'gesdoto','\u2A83':'lesdotor','\u2A84':'gesdotol','\u2A85':'lap','\u2A86':'gap','\u2A87':'lne','\u2A88':'gne','\u2A89':'lnap','\u2A8A':'gnap','\u2A8B':'lEg','\u2A8C':'gEl','\u2A8D':'lsime','\u2A8E':'gsime','\u2A8F':'lsimg','\u2A90':'gsiml','\u2A91':'lgE','\u2A92':'glE','\u2A93':'lesges','\u2A94':'gesles','\u2A95':'els','\u2A96':'egs','\u2A97':'elsdot','\u2A98':'egsdot','\u2A99':'el','\u2A9A':'eg','\u2A9D':'siml','\u2A9E':'simg','\u2A9F':'simlE','\u2AA0':'simgE','\u2AA1':'LessLess','\u2AA1\u0338':'NotNestedLessLess','\u2AA2':'GreaterGreater','\u2AA2\u0338':'NotNestedGreaterGreater','\u2AA4':'glj','\u2AA5':'gla','\u2AA6':'ltcc','\u2AA7':'gtcc','\u2AA8':'lescc','\u2AA9':'gescc','\u2AAA':'smt','\u2AAB':'lat','\u2AAC':'smte','\u2AAC\uFE00':'smtes','\u2AAD':'late','\u2AAD\uFE00':'lates','\u2AAE':'bumpE','\u2AAF':'pre','\u2AAF\u0338':'npre','\u2AB0':'sce','\u2AB0\u0338':'nsce','\u2AB3':'prE','\u2AB4':'scE','\u2AB5':'prnE','\u2AB6':'scnE','\u2AB7':'prap','\u2AB8':'scap','\u2AB9':'prnap','\u2ABA':'scnap','\u2ABB':'Pr','\u2ABC':'Sc','\u2ABD':'subdot','\u2ABE':'supdot','\u2ABF':'subplus','\u2AC0':'supplus','\u2AC1':'submult','\u2AC2':'supmult','\u2AC3':'subedot','\u2AC4':'supedot','\u2AC5':'subE','\u2AC5\u0338':'nsubE','\u2AC6':'supE','\u2AC6\u0338':'nsupE','\u2AC7':'subsim','\u2AC8':'supsim','\u2ACB\uFE00':'vsubnE','\u2ACB':'subnE','\u2ACC\uFE00':'vsupnE','\u2ACC':'supnE','\u2ACF':'csub','\u2AD0':'csup','\u2AD1':'csube','\u2AD2':'csupe','\u2AD3':'subsup','\u2AD4':'supsub','\u2AD5':'subsub','\u2AD6':'supsup','\u2AD7':'suphsub','\u2AD8':'supdsub','\u2AD9':'forkv','\u2ADA':'topfork','\u2ADB':'mlcp','\u2AE4':'Dashv','\u2AE6':'Vdashl','\u2AE7':'Barv','\u2AE8':'vBar','\u2AE9':'vBarv','\u2AEB':'Vbar','\u2AEC':'Not','\u2AED':'bNot','\u2AEE':'rnmid','\u2AEF':'cirmid','\u2AF0':'midcir','\u2AF1':'topcir','\u2AF2':'nhpar','\u2AF3':'parsim','\u2AFD':'parsl','\u2AFD\u20E5':'nparsl','\u266D':'flat','\u266E':'natur','\u266F':'sharp','\xA4':'curren','\xA2':'cent','$':'dollar','\xA3':'pound','\xA5':'yen','\u20AC':'euro','\xB9':'sup1','\xBD':'half','\u2153':'frac13','\xBC':'frac14','\u2155':'frac15','\u2159':'frac16','\u215B':'frac18','\xB2':'sup2','\u2154':'frac23','\u2156':'frac25','\xB3':'sup3','\xBE':'frac34','\u2157':'frac35','\u215C':'frac38','\u2158':'frac45','\u215A':'frac56','\u215D':'frac58','\u215E':'frac78','\uD835\uDCB6':'ascr','\uD835\uDD52':'aopf','\uD835\uDD1E':'afr','\uD835\uDD38':'Aopf','\uD835\uDD04':'Afr','\uD835\uDC9C':'Ascr','\xAA':'ordf','\xE1':'aacute','\xC1':'Aacute','\xE0':'agrave','\xC0':'Agrave','\u0103':'abreve','\u0102':'Abreve','\xE2':'acirc','\xC2':'Acirc','\xE5':'aring','\xC5':'angst','\xE4':'auml','\xC4':'Auml','\xE3':'atilde','\xC3':'Atilde','\u0105':'aogon','\u0104':'Aogon','\u0101':'amacr','\u0100':'Amacr','\xE6':'aelig','\xC6':'AElig','\uD835\uDCB7':'bscr','\uD835\uDD53':'bopf','\uD835\uDD1F':'bfr','\uD835\uDD39':'Bopf','\u212C':'Bscr','\uD835\uDD05':'Bfr','\uD835\uDD20':'cfr','\uD835\uDCB8':'cscr','\uD835\uDD54':'copf','\u212D':'Cfr','\uD835\uDC9E':'Cscr','\u2102':'Copf','\u0107':'cacute','\u0106':'Cacute','\u0109':'ccirc','\u0108':'Ccirc','\u010D':'ccaron','\u010C':'Ccaron','\u010B':'cdot','\u010A':'Cdot','\xE7':'ccedil','\xC7':'Ccedil','\u2105':'incare','\uD835\uDD21':'dfr','\u2146':'dd','\uD835\uDD55':'dopf','\uD835\uDCB9':'dscr','\uD835\uDC9F':'Dscr','\uD835\uDD07':'Dfr','\u2145':'DD','\uD835\uDD3B':'Dopf','\u010F':'dcaron','\u010E':'Dcaron','\u0111':'dstrok','\u0110':'Dstrok','\xF0':'eth','\xD0':'ETH','\u2147':'ee','\u212F':'escr','\uD835\uDD22':'efr','\uD835\uDD56':'eopf','\u2130':'Escr','\uD835\uDD08':'Efr','\uD835\uDD3C':'Eopf','\xE9':'eacute','\xC9':'Eacute','\xE8':'egrave','\xC8':'Egrave','\xEA':'ecirc','\xCA':'Ecirc','\u011B':'ecaron','\u011A':'Ecaron','\xEB':'euml','\xCB':'Euml','\u0117':'edot','\u0116':'Edot','\u0119':'eogon','\u0118':'Eogon','\u0113':'emacr','\u0112':'Emacr','\uD835\uDD23':'ffr','\uD835\uDD57':'fopf','\uD835\uDCBB':'fscr','\uD835\uDD09':'Ffr','\uD835\uDD3D':'Fopf','\u2131':'Fscr','\uFB00':'fflig','\uFB03':'ffilig','\uFB04':'ffllig','\uFB01':'filig','fj':'fjlig','\uFB02':'fllig','\u0192':'fnof','\u210A':'gscr','\uD835\uDD58':'gopf','\uD835\uDD24':'gfr','\uD835\uDCA2':'Gscr','\uD835\uDD3E':'Gopf','\uD835\uDD0A':'Gfr','\u01F5':'gacute','\u011F':'gbreve','\u011E':'Gbreve','\u011D':'gcirc','\u011C':'Gcirc','\u0121':'gdot','\u0120':'Gdot','\u0122':'Gcedil','\uD835\uDD25':'hfr','\u210E':'planckh','\uD835\uDCBD':'hscr','\uD835\uDD59':'hopf','\u210B':'Hscr','\u210C':'Hfr','\u210D':'Hopf','\u0125':'hcirc','\u0124':'Hcirc','\u210F':'hbar','\u0127':'hstrok','\u0126':'Hstrok','\uD835\uDD5A':'iopf','\uD835\uDD26':'ifr','\uD835\uDCBE':'iscr','\u2148':'ii','\uD835\uDD40':'Iopf','\u2110':'Iscr','\u2111':'Im','\xED':'iacute','\xCD':'Iacute','\xEC':'igrave','\xCC':'Igrave','\xEE':'icirc','\xCE':'Icirc','\xEF':'iuml','\xCF':'Iuml','\u0129':'itilde','\u0128':'Itilde','\u0130':'Idot','\u012F':'iogon','\u012E':'Iogon','\u012B':'imacr','\u012A':'Imacr','\u0133':'ijlig','\u0132':'IJlig','\u0131':'imath','\uD835\uDCBF':'jscr','\uD835\uDD5B':'jopf','\uD835\uDD27':'jfr','\uD835\uDCA5':'Jscr','\uD835\uDD0D':'Jfr','\uD835\uDD41':'Jopf','\u0135':'jcirc','\u0134':'Jcirc','\u0237':'jmath','\uD835\uDD5C':'kopf','\uD835\uDCC0':'kscr','\uD835\uDD28':'kfr','\uD835\uDCA6':'Kscr','\uD835\uDD42':'Kopf','\uD835\uDD0E':'Kfr','\u0137':'kcedil','\u0136':'Kcedil','\uD835\uDD29':'lfr','\uD835\uDCC1':'lscr','\u2113':'ell','\uD835\uDD5D':'lopf','\u2112':'Lscr','\uD835\uDD0F':'Lfr','\uD835\uDD43':'Lopf','\u013A':'lacute','\u0139':'Lacute','\u013E':'lcaron','\u013D':'Lcaron','\u013C':'lcedil','\u013B':'Lcedil','\u0142':'lstrok','\u0141':'Lstrok','\u0140':'lmidot','\u013F':'Lmidot','\uD835\uDD2A':'mfr','\uD835\uDD5E':'mopf','\uD835\uDCC2':'mscr','\uD835\uDD10':'Mfr','\uD835\uDD44':'Mopf','\u2133':'Mscr','\uD835\uDD2B':'nfr','\uD835\uDD5F':'nopf','\uD835\uDCC3':'nscr','\u2115':'Nopf','\uD835\uDCA9':'Nscr','\uD835\uDD11':'Nfr','\u0144':'nacute','\u0143':'Nacute','\u0148':'ncaron','\u0147':'Ncaron','\xF1':'ntilde','\xD1':'Ntilde','\u0146':'ncedil','\u0145':'Ncedil','\u2116':'numero','\u014B':'eng','\u014A':'ENG','\uD835\uDD60':'oopf','\uD835\uDD2C':'ofr','\u2134':'oscr','\uD835\uDCAA':'Oscr','\uD835\uDD12':'Ofr','\uD835\uDD46':'Oopf','\xBA':'ordm','\xF3':'oacute','\xD3':'Oacute','\xF2':'ograve','\xD2':'Ograve','\xF4':'ocirc','\xD4':'Ocirc','\xF6':'ouml','\xD6':'Ouml','\u0151':'odblac','\u0150':'Odblac','\xF5':'otilde','\xD5':'Otilde','\xF8':'oslash','\xD8':'Oslash','\u014D':'omacr','\u014C':'Omacr','\u0153':'oelig','\u0152':'OElig','\uD835\uDD2D':'pfr','\uD835\uDCC5':'pscr','\uD835\uDD61':'popf','\u2119':'Popf','\uD835\uDD13':'Pfr','\uD835\uDCAB':'Pscr','\uD835\uDD62':'qopf','\uD835\uDD2E':'qfr','\uD835\uDCC6':'qscr','\uD835\uDCAC':'Qscr','\uD835\uDD14':'Qfr','\u211A':'Qopf','\u0138':'kgreen','\uD835\uDD2F':'rfr','\uD835\uDD63':'ropf','\uD835\uDCC7':'rscr','\u211B':'Rscr','\u211C':'Re','\u211D':'Ropf','\u0155':'racute','\u0154':'Racute','\u0159':'rcaron','\u0158':'Rcaron','\u0157':'rcedil','\u0156':'Rcedil','\uD835\uDD64':'sopf','\uD835\uDCC8':'sscr','\uD835\uDD30':'sfr','\uD835\uDD4A':'Sopf','\uD835\uDD16':'Sfr','\uD835\uDCAE':'Sscr','\u24C8':'oS','\u015B':'sacute','\u015A':'Sacute','\u015D':'scirc','\u015C':'Scirc','\u0161':'scaron','\u0160':'Scaron','\u015F':'scedil','\u015E':'Scedil','\xDF':'szlig','\uD835\uDD31':'tfr','\uD835\uDCC9':'tscr','\uD835\uDD65':'topf','\uD835\uDCAF':'Tscr','\uD835\uDD17':'Tfr','\uD835\uDD4B':'Topf','\u0165':'tcaron','\u0164':'Tcaron','\u0163':'tcedil','\u0162':'Tcedil','\u2122':'trade','\u0167':'tstrok','\u0166':'Tstrok','\uD835\uDCCA':'uscr','\uD835\uDD66':'uopf','\uD835\uDD32':'ufr','\uD835\uDD4C':'Uopf','\uD835\uDD18':'Ufr','\uD835\uDCB0':'Uscr','\xFA':'uacute','\xDA':'Uacute','\xF9':'ugrave','\xD9':'Ugrave','\u016D':'ubreve','\u016C':'Ubreve','\xFB':'ucirc','\xDB':'Ucirc','\u016F':'uring','\u016E':'Uring','\xFC':'uuml','\xDC':'Uuml','\u0171':'udblac','\u0170':'Udblac','\u0169':'utilde','\u0168':'Utilde','\u0173':'uogon','\u0172':'Uogon','\u016B':'umacr','\u016A':'Umacr','\uD835\uDD33':'vfr','\uD835\uDD67':'vopf','\uD835\uDCCB':'vscr','\uD835\uDD19':'Vfr','\uD835\uDD4D':'Vopf','\uD835\uDCB1':'Vscr','\uD835\uDD68':'wopf','\uD835\uDCCC':'wscr','\uD835\uDD34':'wfr','\uD835\uDCB2':'Wscr','\uD835\uDD4E':'Wopf','\uD835\uDD1A':'Wfr','\u0175':'wcirc','\u0174':'Wcirc','\uD835\uDD35':'xfr','\uD835\uDCCD':'xscr','\uD835\uDD69':'xopf','\uD835\uDD4F':'Xopf','\uD835\uDD1B':'Xfr','\uD835\uDCB3':'Xscr','\uD835\uDD36':'yfr','\uD835\uDCCE':'yscr','\uD835\uDD6A':'yopf','\uD835\uDCB4':'Yscr','\uD835\uDD1C':'Yfr','\uD835\uDD50':'Yopf','\xFD':'yacute','\xDD':'Yacute','\u0177':'ycirc','\u0176':'Ycirc','\xFF':'yuml','\u0178':'Yuml','\uD835\uDCCF':'zscr','\uD835\uDD37':'zfr','\uD835\uDD6B':'zopf','\u2128':'Zfr','\u2124':'Zopf','\uD835\uDCB5':'Zscr','\u017A':'zacute','\u0179':'Zacute','\u017E':'zcaron','\u017D':'Zcaron','\u017C':'zdot','\u017B':'Zdot','\u01B5':'imped','\xFE':'thorn','\xDE':'THORN','\u0149':'napos','\u03B1':'alpha','\u0391':'Alpha','\u03B2':'beta','\u0392':'Beta','\u03B3':'gamma','\u0393':'Gamma','\u03B4':'delta','\u0394':'Delta','\u03B5':'epsi','\u03F5':'epsiv','\u0395':'Epsilon','\u03DD':'gammad','\u03DC':'Gammad','\u03B6':'zeta','\u0396':'Zeta','\u03B7':'eta','\u0397':'Eta','\u03B8':'theta','\u03D1':'thetav','\u0398':'Theta','\u03B9':'iota','\u0399':'Iota','\u03BA':'kappa','\u03F0':'kappav','\u039A':'Kappa','\u03BB':'lambda','\u039B':'Lambda','\u03BC':'mu','\xB5':'micro','\u039C':'Mu','\u03BD':'nu','\u039D':'Nu','\u03BE':'xi','\u039E':'Xi','\u03BF':'omicron','\u039F':'Omicron','\u03C0':'pi','\u03D6':'piv','\u03A0':'Pi','\u03C1':'rho','\u03F1':'rhov','\u03A1':'Rho','\u03C3':'sigma','\u03A3':'Sigma','\u03C2':'sigmaf','\u03C4':'tau','\u03A4':'Tau','\u03C5':'upsi','\u03A5':'Upsilon','\u03D2':'Upsi','\u03C6':'phi','\u03D5':'phiv','\u03A6':'Phi','\u03C7':'chi','\u03A7':'Chi','\u03C8':'psi','\u03A8':'Psi','\u03C9':'omega','\u03A9':'ohm','\u0430':'acy','\u0410':'Acy','\u0431':'bcy','\u0411':'Bcy','\u0432':'vcy','\u0412':'Vcy','\u0433':'gcy','\u0413':'Gcy','\u0453':'gjcy','\u0403':'GJcy','\u0434':'dcy','\u0414':'Dcy','\u0452':'djcy','\u0402':'DJcy','\u0435':'iecy','\u0415':'IEcy','\u0451':'iocy','\u0401':'IOcy','\u0454':'jukcy','\u0404':'Jukcy','\u0436':'zhcy','\u0416':'ZHcy','\u0437':'zcy','\u0417':'Zcy','\u0455':'dscy','\u0405':'DScy','\u0438':'icy','\u0418':'Icy','\u0456':'iukcy','\u0406':'Iukcy','\u0457':'yicy','\u0407':'YIcy','\u0439':'jcy','\u0419':'Jcy','\u0458':'jsercy','\u0408':'Jsercy','\u043A':'kcy','\u041A':'Kcy','\u045C':'kjcy','\u040C':'KJcy','\u043B':'lcy','\u041B':'Lcy','\u0459':'ljcy','\u0409':'LJcy','\u043C':'mcy','\u041C':'Mcy','\u043D':'ncy','\u041D':'Ncy','\u045A':'njcy','\u040A':'NJcy','\u043E':'ocy','\u041E':'Ocy','\u043F':'pcy','\u041F':'Pcy','\u0440':'rcy','\u0420':'Rcy','\u0441':'scy','\u0421':'Scy','\u0442':'tcy','\u0422':'Tcy','\u045B':'tshcy','\u040B':'TSHcy','\u0443':'ucy','\u0423':'Ucy','\u045E':'ubrcy','\u040E':'Ubrcy','\u0444':'fcy','\u0424':'Fcy','\u0445':'khcy','\u0425':'KHcy','\u0446':'tscy','\u0426':'TScy','\u0447':'chcy','\u0427':'CHcy','\u045F':'dzcy','\u040F':'DZcy','\u0448':'shcy','\u0428':'SHcy','\u0449':'shchcy','\u0429':'SHCHcy','\u044A':'hardcy','\u042A':'HARDcy','\u044B':'ycy','\u042B':'Ycy','\u044C':'softcy','\u042C':'SOFTcy','\u044D':'ecy','\u042D':'Ecy','\u044E':'yucy','\u042E':'YUcy','\u044F':'yacy','\u042F':'YAcy','\u2135':'aleph','\u2136':'beth','\u2137':'gimel','\u2138':'daleth'};

	var regexEscape = /["&'<>`]/g;
	var escapeMap = {
		'"': '&quot;',
		'&': '&amp;',
		'\'': '&#x27;',
		'<': '&lt;',
		// See https://mathiasbynens.be/notes/ambiguous-ampersands: in HTML, the
		// following is not strictly necessary unless itâ€™s part of a tag or an
		// unquoted attribute value. Weâ€™re only escaping it to support those
		// situations, and for XML support.
		'>': '&gt;',
		// In Internet Explorer â‰¤ 8, the backtick character can be used
		// to break out of (un)quoted attribute values or HTML comments.
		// See http://html5sec.org/#102, http://html5sec.org/#108, and
		// http://html5sec.org/#133.
		'`': '&#x60;'
	};

	var regexInvalidEntity = /&#(?:[xX][^a-fA-F0-9]|[^0-9xX])/;
	var regexInvalidRawCodePoint = /[\0-\x08\x0B\x0E-\x1F\x7F-\x9F\uFDD0-\uFDEF\uFFFE\uFFFF]|[\uD83F\uD87F\uD8BF\uD8FF\uD93F\uD97F\uD9BF\uD9FF\uDA3F\uDA7F\uDABF\uDAFF\uDB3F\uDB7F\uDBBF\uDBFF][\uDFFE\uDFFF]|[\uD800-\uDBFF](?![\uDC00-\uDFFF])|(?:[^\uD800-\uDBFF]|^)[\uDC00-\uDFFF]/;
	var regexDecode = /&(CounterClockwiseContourIntegral|DoubleLongLeftRightArrow|ClockwiseContourIntegral|NotNestedGreaterGreater|NotSquareSupersetEqual|DiacriticalDoubleAcute|NotRightTriangleEqual|NotSucceedsSlantEqual|NotPrecedesSlantEqual|CloseCurlyDoubleQuote|NegativeVeryThinSpace|DoubleContourIntegral|FilledVerySmallSquare|CapitalDifferentialD|OpenCurlyDoubleQuote|EmptyVerySmallSquare|NestedGreaterGreater|DoubleLongRightArrow|NotLeftTriangleEqual|NotGreaterSlantEqual|ReverseUpEquilibrium|DoubleLeftRightArrow|NotSquareSubsetEqual|NotDoubleVerticalBar|RightArrowLeftArrow|NotGreaterFullEqual|NotRightTriangleBar|SquareSupersetEqual|DownLeftRightVector|DoubleLongLeftArrow|leftrightsquigarrow|LeftArrowRightArrow|NegativeMediumSpace|blacktriangleright|RightDownVectorBar|PrecedesSlantEqual|RightDoubleBracket|SucceedsSlantEqual|NotLeftTriangleBar|RightTriangleEqual|SquareIntersection|RightDownTeeVector|ReverseEquilibrium|NegativeThickSpace|longleftrightarrow|Longleftrightarrow|LongLeftRightArrow|DownRightTeeVector|DownRightVectorBar|GreaterSlantEqual|SquareSubsetEqual|LeftDownVectorBar|LeftDoubleBracket|VerticalSeparator|rightleftharpoons|NotGreaterGreater|NotSquareSuperset|blacktriangleleft|blacktriangledown|NegativeThinSpace|LeftDownTeeVector|NotLessSlantEqual|leftrightharpoons|DoubleUpDownArrow|DoubleVerticalBar|LeftTriangleEqual|FilledSmallSquare|twoheadrightarrow|NotNestedLessLess|DownLeftTeeVector|DownLeftVectorBar|RightAngleBracket|NotTildeFullEqual|NotReverseElement|RightUpDownVector|DiacriticalTilde|NotSucceedsTilde|circlearrowright|NotPrecedesEqual|rightharpoondown|DoubleRightArrow|NotSucceedsEqual|NonBreakingSpace|NotRightTriangle|LessEqualGreater|RightUpTeeVector|LeftAngleBracket|GreaterFullEqual|DownArrowUpArrow|RightUpVectorBar|twoheadleftarrow|GreaterEqualLess|downharpoonright|RightTriangleBar|ntrianglerighteq|NotSupersetEqual|LeftUpDownVector|DiacriticalAcute|rightrightarrows|vartriangleright|UpArrowDownArrow|DiacriticalGrave|UnderParenthesis|EmptySmallSquare|LeftUpVectorBar|leftrightarrows|DownRightVector|downharpoonleft|trianglerighteq|ShortRightArrow|OverParenthesis|DoubleLeftArrow|DoubleDownArrow|NotSquareSubset|bigtriangledown|ntrianglelefteq|UpperRightArrow|curvearrowright|vartriangleleft|NotLeftTriangle|nleftrightarrow|LowerRightArrow|NotHumpDownHump|NotGreaterTilde|rightthreetimes|LeftUpTeeVector|NotGreaterEqual|straightepsilon|LeftTriangleBar|rightsquigarrow|ContourIntegral|rightleftarrows|CloseCurlyQuote|RightDownVector|LeftRightVector|nLeftrightarrow|leftharpoondown|circlearrowleft|SquareSuperset|OpenCurlyQuote|hookrightarrow|HorizontalLine|DiacriticalDot|NotLessGreater|ntriangleright|DoubleRightTee|InvisibleComma|InvisibleTimes|LowerLeftArrow|DownLeftVector|NotSubsetEqual|curvearrowleft|trianglelefteq|NotVerticalBar|TildeFullEqual|downdownarrows|NotGreaterLess|RightTeeVector|ZeroWidthSpace|looparrowright|LongRightArrow|doublebarwedge|ShortLeftArrow|ShortDownArrow|RightVectorBar|GreaterGreater|ReverseElement|rightharpoonup|LessSlantEqual|leftthreetimes|upharpoonright|rightarrowtail|LeftDownVector|Longrightarrow|NestedLessLess|UpperLeftArrow|nshortparallel|leftleftarrows|leftrightarrow|Leftrightarrow|LeftRightArrow|longrightarrow|upharpoonleft|RightArrowBar|ApplyFunction|LeftTeeVector|leftarrowtail|NotEqualTilde|varsubsetneqq|varsupsetneqq|RightTeeArrow|SucceedsEqual|SucceedsTilde|LeftVectorBar|SupersetEqual|hookleftarrow|DifferentialD|VerticalTilde|VeryThinSpace|blacktriangle|bigtriangleup|LessFullEqual|divideontimes|leftharpoonup|UpEquilibrium|ntriangleleft|RightTriangle|measuredangle|shortparallel|longleftarrow|Longleftarrow|LongLeftArrow|DoubleLeftTee|Poincareplane|PrecedesEqual|triangleright|DoubleUpArrow|RightUpVector|fallingdotseq|looparrowleft|PrecedesTilde|NotTildeEqual|NotTildeTilde|smallsetminus|Proportional|triangleleft|triangledown|UnderBracket|NotHumpEqual|exponentiale|ExponentialE|NotLessTilde|HilbertSpace|RightCeiling|blacklozenge|varsupsetneq|HumpDownHump|GreaterEqual|VerticalLine|LeftTeeArrow|NotLessEqual|DownTeeArrow|LeftTriangle|varsubsetneq|Intersection|NotCongruent|DownArrowBar|LeftUpVector|LeftArrowBar|risingdotseq|GreaterTilde|RoundImplies|SquareSubset|ShortUpArrow|NotSuperset|quaternions|precnapprox|backepsilon|preccurlyeq|OverBracket|blacksquare|MediumSpace|VerticalBar|circledcirc|circleddash|CircleMinus|CircleTimes|LessGreater|curlyeqprec|curlyeqsucc|diamondsuit|UpDownArrow|Updownarrow|RuleDelayed|Rrightarrow|updownarrow|RightVector|nRightarrow|nrightarrow|eqslantless|LeftCeiling|Equilibrium|SmallCircle|expectation|NotSucceeds|thickapprox|GreaterLess|SquareUnion|NotPrecedes|NotLessLess|straightphi|succnapprox|succcurlyeq|SubsetEqual|sqsupseteq|Proportion|Laplacetrf|ImaginaryI|supsetneqq|NotGreater|gtreqqless|NotElement|ThickSpace|TildeEqual|TildeTilde|Fouriertrf|rmoustache|EqualTilde|eqslantgtr|UnderBrace|LeftVector|UpArrowBar|nLeftarrow|nsubseteqq|subsetneqq|nsupseteqq|nleftarrow|succapprox|lessapprox|UpTeeArrow|upuparrows|curlywedge|lesseqqgtr|varepsilon|varnothing|RightFloor|complement|CirclePlus|sqsubseteq|Lleftarrow|circledast|RightArrow|Rightarrow|rightarrow|lmoustache|Bernoullis|precapprox|mapstoleft|mapstodown|longmapsto|dotsquare|downarrow|DoubleDot|nsubseteq|supsetneq|leftarrow|nsupseteq|subsetneq|ThinSpace|ngeqslant|subseteqq|HumpEqual|NotSubset|triangleq|NotCupCap|lesseqgtr|heartsuit|TripleDot|Leftarrow|Coproduct|Congruent|varpropto|complexes|gvertneqq|LeftArrow|LessTilde|supseteqq|MinusPlus|CircleDot|nleqslant|NotExists|gtreqless|nparallel|UnionPlus|LeftFloor|checkmark|CenterDot|centerdot|Mellintrf|gtrapprox|bigotimes|OverBrace|spadesuit|therefore|pitchfork|rationals|PlusMinus|Backslash|Therefore|DownBreve|backsimeq|backprime|DownArrow|nshortmid|Downarrow|lvertneqq|eqvparsl|imagline|imagpart|infintie|integers|Integral|intercal|LessLess|Uarrocir|intlarhk|sqsupset|angmsdaf|sqsubset|llcorner|vartheta|cupbrcap|lnapprox|Superset|SuchThat|succnsim|succneqq|angmsdag|biguplus|curlyvee|trpezium|Succeeds|NotTilde|bigwedge|angmsdah|angrtvbd|triminus|cwconint|fpartint|lrcorner|smeparsl|subseteq|urcorner|lurdshar|laemptyv|DDotrahd|approxeq|ldrushar|awconint|mapstoup|backcong|shortmid|triangle|geqslant|gesdotol|timesbar|circledR|circledS|setminus|multimap|naturals|scpolint|ncongdot|RightTee|boxminus|gnapprox|boxtimes|andslope|thicksim|angmsdaa|varsigma|cirfnint|rtriltri|angmsdab|rppolint|angmsdac|barwedge|drbkarow|clubsuit|thetasym|bsolhsub|capbrcup|dzigrarr|doteqdot|DotEqual|dotminus|UnderBar|NotEqual|realpart|otimesas|ulcorner|hksearow|hkswarow|parallel|PartialD|elinters|emptyset|plusacir|bbrktbrk|angmsdad|pointint|bigoplus|angmsdae|Precedes|bigsqcup|varkappa|notindot|supseteq|precneqq|precnsim|profalar|profline|profsurf|leqslant|lesdotor|raemptyv|subplus|notnivb|notnivc|subrarr|zigrarr|vzigzag|submult|subedot|Element|between|cirscir|larrbfs|larrsim|lotimes|lbrksld|lbrkslu|lozenge|ldrdhar|dbkarow|bigcirc|epsilon|simrarr|simplus|ltquest|Epsilon|luruhar|gtquest|maltese|npolint|eqcolon|npreceq|bigodot|ddagger|gtrless|bnequiv|harrcir|ddotseq|equivDD|backsim|demptyv|nsqsube|nsqsupe|Upsilon|nsubset|upsilon|minusdu|nsucceq|swarrow|nsupset|coloneq|searrow|boxplus|napprox|natural|asympeq|alefsym|congdot|nearrow|bigstar|diamond|supplus|tritime|LeftTee|nvinfin|triplus|NewLine|nvltrie|nvrtrie|nwarrow|nexists|Diamond|ruluhar|Implies|supmult|angzarr|suplarr|suphsub|questeq|because|digamma|Because|olcross|bemptyv|omicron|Omicron|rotimes|NoBreak|intprod|angrtvb|orderof|uwangle|suphsol|lesdoto|orslope|DownTee|realine|cudarrl|rdldhar|OverBar|supedot|lessdot|supdsub|topfork|succsim|rbrkslu|rbrksld|pertenk|cudarrr|isindot|planckh|lessgtr|pluscir|gesdoto|plussim|plustwo|lesssim|cularrp|rarrsim|Cayleys|notinva|notinvb|notinvc|UpArrow|Uparrow|uparrow|NotLess|dwangle|precsim|Product|curarrm|Cconint|dotplus|rarrbfs|ccupssm|Cedilla|cemptyv|notniva|quatint|frac35|frac38|frac45|frac56|frac58|frac78|tridot|xoplus|gacute|gammad|Gammad|lfisht|lfloor|bigcup|sqsupe|gbreve|Gbreve|lharul|sqsube|sqcups|Gcedil|apacir|llhard|lmidot|Lmidot|lmoust|andand|sqcaps|approx|Abreve|spades|circeq|tprime|divide|topcir|Assign|topbot|gesdot|divonx|xuplus|timesd|gesles|atilde|solbar|SOFTcy|loplus|timesb|lowast|lowbar|dlcorn|dlcrop|softcy|dollar|lparlt|thksim|lrhard|Atilde|lsaquo|smashp|bigvee|thinsp|wreath|bkarow|lsquor|lstrok|Lstrok|lthree|ltimes|ltlarr|DotDot|simdot|ltrPar|weierp|xsqcup|angmsd|sigmav|sigmaf|zeetrf|Zcaron|zcaron|mapsto|vsupne|thetav|cirmid|marker|mcomma|Zacute|vsubnE|there4|gtlPar|vsubne|bottom|gtrarr|SHCHcy|shchcy|midast|midcir|middot|minusb|minusd|gtrdot|bowtie|sfrown|mnplus|models|colone|seswar|Colone|mstpos|searhk|gtrsim|nacute|Nacute|boxbox|telrec|hairsp|Tcedil|nbumpe|scnsim|ncaron|Ncaron|ncedil|Ncedil|hamilt|Scedil|nearhk|hardcy|HARDcy|tcedil|Tcaron|commat|nequiv|nesear|tcaron|target|hearts|nexist|varrho|scedil|Scaron|scaron|hellip|Sacute|sacute|hercon|swnwar|compfn|rtimes|rthree|rsquor|rsaquo|zacute|wedgeq|homtht|barvee|barwed|Barwed|rpargt|horbar|conint|swarhk|roplus|nltrie|hslash|hstrok|Hstrok|rmoust|Conint|bprime|hybull|hyphen|iacute|Iacute|supsup|supsub|supsim|varphi|coprod|brvbar|agrave|Supset|supset|igrave|Igrave|notinE|Agrave|iiiint|iinfin|copysr|wedbar|Verbar|vangrt|becaus|incare|verbar|inodot|bullet|drcorn|intcal|drcrop|cularr|vellip|Utilde|bumpeq|cupcap|dstrok|Dstrok|CupCap|cupcup|cupdot|eacute|Eacute|supdot|iquest|easter|ecaron|Ecaron|ecolon|isinsv|utilde|itilde|Itilde|curarr|succeq|Bumpeq|cacute|ulcrop|nparsl|Cacute|nprcue|egrave|Egrave|nrarrc|nrarrw|subsup|subsub|nrtrie|jsercy|nsccue|Jsercy|kappav|kcedil|Kcedil|subsim|ulcorn|nsimeq|egsdot|veebar|kgreen|capand|elsdot|Subset|subset|curren|aacute|lacute|Lacute|emptyv|ntilde|Ntilde|lagran|lambda|Lambda|capcap|Ugrave|langle|subdot|emsp13|numero|emsp14|nvdash|nvDash|nVdash|nVDash|ugrave|ufisht|nvHarr|larrfs|nvlArr|larrhk|larrlp|larrpl|nvrArr|Udblac|nwarhk|larrtl|nwnear|oacute|Oacute|latail|lAtail|sstarf|lbrace|odblac|Odblac|lbrack|udblac|odsold|eparsl|lcaron|Lcaron|ograve|Ograve|lcedil|Lcedil|Aacute|ssmile|ssetmn|squarf|ldquor|capcup|ominus|cylcty|rharul|eqcirc|dagger|rfloor|rfisht|Dagger|daleth|equals|origof|capdot|equest|dcaron|Dcaron|rdquor|oslash|Oslash|otilde|Otilde|otimes|Otimes|urcrop|Ubreve|ubreve|Yacute|Uacute|uacute|Rcedil|rcedil|urcorn|parsim|Rcaron|Vdashl|rcaron|Tstrok|percnt|period|permil|Exists|yacute|rbrack|rbrace|phmmat|ccaron|Ccaron|planck|ccedil|plankv|tstrok|female|plusdo|plusdu|ffilig|plusmn|ffllig|Ccedil|rAtail|dfisht|bernou|ratail|Rarrtl|rarrtl|angsph|rarrpl|rarrlp|rarrhk|xwedge|xotime|forall|ForAll|Vvdash|vsupnE|preceq|bigcap|frac12|frac13|frac14|primes|rarrfs|prnsim|frac15|Square|frac16|square|lesdot|frac18|frac23|propto|prurel|rarrap|rangle|puncsp|frac25|Racute|qprime|racute|lesges|frac34|abreve|AElig|eqsim|utdot|setmn|urtri|Equal|Uring|seArr|uring|searr|dashv|Dashv|mumap|nabla|iogon|Iogon|sdote|sdotb|scsim|napid|napos|equiv|natur|Acirc|dblac|erarr|nbump|iprod|erDot|ucirc|awint|esdot|angrt|ncong|isinE|scnap|Scirc|scirc|ndash|isins|Ubrcy|nearr|neArr|isinv|nedot|ubrcy|acute|Ycirc|iukcy|Iukcy|xutri|nesim|caret|jcirc|Jcirc|caron|twixt|ddarr|sccue|exist|jmath|sbquo|ngeqq|angst|ccaps|lceil|ngsim|UpTee|delta|Delta|rtrif|nharr|nhArr|nhpar|rtrie|jukcy|Jukcy|kappa|rsquo|Kappa|nlarr|nlArr|TSHcy|rrarr|aogon|Aogon|fflig|xrarr|tshcy|ccirc|nleqq|filig|upsih|nless|dharl|nlsim|fjlig|ropar|nltri|dharr|robrk|roarr|fllig|fltns|roang|rnmid|subnE|subne|lAarr|trisb|Ccirc|acirc|ccups|blank|VDash|forkv|Vdash|langd|cedil|blk12|blk14|laquo|strns|diams|notin|vDash|larrb|blk34|block|disin|uplus|vdash|vBarv|aelig|starf|Wedge|check|xrArr|lates|lbarr|lBarr|notni|lbbrk|bcong|frasl|lbrke|frown|vrtri|vprop|vnsup|gamma|Gamma|wedge|xodot|bdquo|srarr|doteq|ldquo|boxdl|boxdL|gcirc|Gcirc|boxDl|boxDL|boxdr|boxdR|boxDr|TRADE|trade|rlhar|boxDR|vnsub|npart|vltri|rlarr|boxhd|boxhD|nprec|gescc|nrarr|nrArr|boxHd|boxHD|boxhu|boxhU|nrtri|boxHu|clubs|boxHU|times|colon|Colon|gimel|xlArr|Tilde|nsime|tilde|nsmid|nspar|THORN|thorn|xlarr|nsube|nsubE|thkap|xhArr|comma|nsucc|boxul|boxuL|nsupe|nsupE|gneqq|gnsim|boxUl|boxUL|grave|boxur|boxuR|boxUr|boxUR|lescc|angle|bepsi|boxvh|varpi|boxvH|numsp|Theta|gsime|gsiml|theta|boxVh|boxVH|boxvl|gtcir|gtdot|boxvL|boxVl|boxVL|crarr|cross|Cross|nvsim|boxvr|nwarr|nwArr|sqsup|dtdot|Uogon|lhard|lharu|dtrif|ocirc|Ocirc|lhblk|duarr|odash|sqsub|Hacek|sqcup|llarr|duhar|oelig|OElig|ofcir|boxvR|uogon|lltri|boxVr|csube|uuarr|ohbar|csupe|ctdot|olarr|olcir|harrw|oline|sqcap|omacr|Omacr|omega|Omega|boxVR|aleph|lneqq|lnsim|loang|loarr|rharu|lobrk|hcirc|operp|oplus|rhard|Hcirc|orarr|Union|order|ecirc|Ecirc|cuepr|szlig|cuesc|breve|reals|eDDot|Breve|hoarr|lopar|utrif|rdquo|Umacr|umacr|efDot|swArr|ultri|alpha|rceil|ovbar|swarr|Wcirc|wcirc|smtes|smile|bsemi|lrarr|aring|parsl|lrhar|bsime|uhblk|lrtri|cupor|Aring|uharr|uharl|slarr|rbrke|bsolb|lsime|rbbrk|RBarr|lsimg|phone|rBarr|rbarr|icirc|lsquo|Icirc|emacr|Emacr|ratio|simne|plusb|simlE|simgE|simeq|pluse|ltcir|ltdot|empty|xharr|xdtri|iexcl|Alpha|ltrie|rarrw|pound|ltrif|xcirc|bumpe|prcue|bumpE|asymp|amacr|cuvee|Sigma|sigma|iiint|udhar|iiota|ijlig|IJlig|supnE|imacr|Imacr|prime|Prime|image|prnap|eogon|Eogon|rarrc|mdash|mDDot|cuwed|imath|supne|imped|Amacr|udarr|prsim|micro|rarrb|cwint|raquo|infin|eplus|range|rangd|Ucirc|radic|minus|amalg|veeeq|rAarr|epsiv|ycirc|quest|sharp|quot|zwnj|Qscr|race|qscr|Qopf|qopf|qint|rang|Rang|Zscr|zscr|Zopf|zopf|rarr|rArr|Rarr|Pscr|pscr|prop|prod|prnE|prec|ZHcy|zhcy|prap|Zeta|zeta|Popf|popf|Zdot|plus|zdot|Yuml|yuml|phiv|YUcy|yucy|Yscr|yscr|perp|Yopf|yopf|part|para|YIcy|Ouml|rcub|yicy|YAcy|rdca|ouml|osol|Oscr|rdsh|yacy|real|oscr|xvee|andd|rect|andv|Xscr|oror|ordm|ordf|xscr|ange|aopf|Aopf|rHar|Xopf|opar|Oopf|xopf|xnis|rhov|oopf|omid|xmap|oint|apid|apos|ogon|ascr|Ascr|odot|odiv|xcup|xcap|ocir|oast|nvlt|nvle|nvgt|nvge|nvap|Wscr|wscr|auml|ntlg|ntgl|nsup|nsub|nsim|Nscr|nscr|nsce|Wopf|ring|npre|wopf|npar|Auml|Barv|bbrk|Nopf|nopf|nmid|nLtv|beta|ropf|Ropf|Beta|beth|nles|rpar|nleq|bnot|bNot|nldr|NJcy|rscr|Rscr|Vscr|vscr|rsqb|njcy|bopf|nisd|Bopf|rtri|Vopf|nGtv|ngtr|vopf|boxh|boxH|boxv|nges|ngeq|boxV|bscr|scap|Bscr|bsim|Vert|vert|bsol|bull|bump|caps|cdot|ncup|scnE|ncap|nbsp|napE|Cdot|cent|sdot|Vbar|nang|vBar|chcy|Mscr|mscr|sect|semi|CHcy|Mopf|mopf|sext|circ|cire|mldr|mlcp|cirE|comp|shcy|SHcy|vArr|varr|cong|copf|Copf|copy|COPY|malt|male|macr|lvnE|cscr|ltri|sime|ltcc|simg|Cscr|siml|csub|Uuml|lsqb|lsim|uuml|csup|Lscr|lscr|utri|smid|lpar|cups|smte|lozf|darr|Lopf|Uscr|solb|lopf|sopf|Sopf|lneq|uscr|spar|dArr|lnap|Darr|dash|Sqrt|LJcy|ljcy|lHar|dHar|Upsi|upsi|diam|lesg|djcy|DJcy|leqq|dopf|Dopf|dscr|Dscr|dscy|ldsh|ldca|squf|DScy|sscr|Sscr|dsol|lcub|late|star|Star|Uopf|Larr|lArr|larr|uopf|dtri|dzcy|sube|subE|Lang|lang|Kscr|kscr|Kopf|kopf|KJcy|kjcy|KHcy|khcy|DZcy|ecir|edot|eDot|Jscr|jscr|succ|Jopf|jopf|Edot|uHar|emsp|ensp|Iuml|iuml|eopf|isin|Iscr|iscr|Eopf|epar|sung|epsi|escr|sup1|sup2|sup3|Iota|iota|supe|supE|Iopf|iopf|IOcy|iocy|Escr|esim|Esim|imof|Uarr|QUOT|uArr|uarr|euml|IEcy|iecy|Idot|Euml|euro|excl|Hscr|hscr|Hopf|hopf|TScy|tscy|Tscr|hbar|tscr|flat|tbrk|fnof|hArr|harr|half|fopf|Fopf|tdot|gvnE|fork|trie|gtcc|fscr|Fscr|gdot|gsim|Gscr|gscr|Gopf|gopf|gneq|Gdot|tosa|gnap|Topf|topf|geqq|toea|GJcy|gjcy|tint|gesl|mid|Sfr|ggg|top|ges|gla|glE|glj|geq|gne|gEl|gel|gnE|Gcy|gcy|gap|Tfr|tfr|Tcy|tcy|Hat|Tau|Ffr|tau|Tab|hfr|Hfr|ffr|Fcy|fcy|icy|Icy|iff|ETH|eth|ifr|Ifr|Eta|eta|int|Int|Sup|sup|ucy|Ucy|Sum|sum|jcy|ENG|ufr|Ufr|eng|Jcy|jfr|els|ell|egs|Efr|efr|Jfr|uml|kcy|Kcy|Ecy|ecy|kfr|Kfr|lap|Sub|sub|lat|lcy|Lcy|leg|Dot|dot|lEg|leq|les|squ|div|die|lfr|Lfr|lgE|Dfr|dfr|Del|deg|Dcy|dcy|lne|lnE|sol|loz|smt|Cup|lrm|cup|lsh|Lsh|sim|shy|map|Map|mcy|Mcy|mfr|Mfr|mho|gfr|Gfr|sfr|cir|Chi|chi|nap|Cfr|vcy|Vcy|cfr|Scy|scy|ncy|Ncy|vee|Vee|Cap|cap|nfr|scE|sce|Nfr|nge|ngE|nGg|vfr|Vfr|ngt|bot|nGt|nis|niv|Rsh|rsh|nle|nlE|bne|Bfr|bfr|nLl|nlt|nLt|Bcy|bcy|not|Not|rlm|wfr|Wfr|npr|nsc|num|ocy|ast|Ocy|ofr|xfr|Xfr|Ofr|ogt|ohm|apE|olt|Rho|ape|rho|Rfr|rfr|ord|REG|ang|reg|orv|And|and|AMP|Rcy|amp|Afr|ycy|Ycy|yen|yfr|Yfr|rcy|par|pcy|Pcy|pfr|Pfr|phi|Phi|afr|Acy|acy|zcy|Zcy|piv|acE|acd|zfr|Zfr|pre|prE|psi|Psi|qfr|Qfr|zwj|Or|ge|Gg|gt|gg|el|oS|lt|Lt|LT|Re|lg|gl|eg|ne|Im|it|le|DD|wp|wr|nu|Nu|dd|lE|Sc|sc|pi|Pi|ee|af|ll|Ll|rx|gE|xi|pm|Xi|ic|pr|Pr|in|ni|mp|mu|ac|Mu|or|ap|Gt|GT|ii);|&(Aacute|Agrave|Atilde|Ccedil|Eacute|Egrave|Iacute|Igrave|Ntilde|Oacute|Ograve|Oslash|Otilde|Uacute|Ugrave|Yacute|aacute|agrave|atilde|brvbar|ccedil|curren|divide|eacute|egrave|frac12|frac14|frac34|iacute|igrave|iquest|middot|ntilde|oacute|ograve|oslash|otilde|plusmn|uacute|ugrave|yacute|AElig|Acirc|Aring|Ecirc|Icirc|Ocirc|THORN|Ucirc|acirc|acute|aelig|aring|cedil|ecirc|icirc|iexcl|laquo|micro|ocirc|pound|raquo|szlig|thorn|times|ucirc|Auml|COPY|Euml|Iuml|Ouml|QUOT|Uuml|auml|cent|copy|euml|iuml|macr|nbsp|ordf|ordm|ouml|para|quot|sect|sup1|sup2|sup3|uuml|yuml|AMP|ETH|REG|amp|deg|eth|not|reg|shy|uml|yen|GT|LT|gt|lt)(?!;)([=a-zA-Z0-9]?)|&#([0-9]+)(;?)|&#[xX]([a-fA-F0-9]+)(;?)|&([0-9a-zA-Z]+)/g;
	var decodeMap = {'aacute':'\xE1','Aacute':'\xC1','abreve':'\u0103','Abreve':'\u0102','ac':'\u223E','acd':'\u223F','acE':'\u223E\u0333','acirc':'\xE2','Acirc':'\xC2','acute':'\xB4','acy':'\u0430','Acy':'\u0410','aelig':'\xE6','AElig':'\xC6','af':'\u2061','afr':'\uD835\uDD1E','Afr':'\uD835\uDD04','agrave':'\xE0','Agrave':'\xC0','alefsym':'\u2135','aleph':'\u2135','alpha':'\u03B1','Alpha':'\u0391','amacr':'\u0101','Amacr':'\u0100','amalg':'\u2A3F','amp':'&','AMP':'&','and':'\u2227','And':'\u2A53','andand':'\u2A55','andd':'\u2A5C','andslope':'\u2A58','andv':'\u2A5A','ang':'\u2220','ange':'\u29A4','angle':'\u2220','angmsd':'\u2221','angmsdaa':'\u29A8','angmsdab':'\u29A9','angmsdac':'\u29AA','angmsdad':'\u29AB','angmsdae':'\u29AC','angmsdaf':'\u29AD','angmsdag':'\u29AE','angmsdah':'\u29AF','angrt':'\u221F','angrtvb':'\u22BE','angrtvbd':'\u299D','angsph':'\u2222','angst':'\xC5','angzarr':'\u237C','aogon':'\u0105','Aogon':'\u0104','aopf':'\uD835\uDD52','Aopf':'\uD835\uDD38','ap':'\u2248','apacir':'\u2A6F','ape':'\u224A','apE':'\u2A70','apid':'\u224B','apos':'\'','ApplyFunction':'\u2061','approx':'\u2248','approxeq':'\u224A','aring':'\xE5','Aring':'\xC5','ascr':'\uD835\uDCB6','Ascr':'\uD835\uDC9C','Assign':'\u2254','ast':'*','asymp':'\u2248','asympeq':'\u224D','atilde':'\xE3','Atilde':'\xC3','auml':'\xE4','Auml':'\xC4','awconint':'\u2233','awint':'\u2A11','backcong':'\u224C','backepsilon':'\u03F6','backprime':'\u2035','backsim':'\u223D','backsimeq':'\u22CD','Backslash':'\u2216','Barv':'\u2AE7','barvee':'\u22BD','barwed':'\u2305','Barwed':'\u2306','barwedge':'\u2305','bbrk':'\u23B5','bbrktbrk':'\u23B6','bcong':'\u224C','bcy':'\u0431','Bcy':'\u0411','bdquo':'\u201E','becaus':'\u2235','because':'\u2235','Because':'\u2235','bemptyv':'\u29B0','bepsi':'\u03F6','bernou':'\u212C','Bernoullis':'\u212C','beta':'\u03B2','Beta':'\u0392','beth':'\u2136','between':'\u226C','bfr':'\uD835\uDD1F','Bfr':'\uD835\uDD05','bigcap':'\u22C2','bigcirc':'\u25EF','bigcup':'\u22C3','bigodot':'\u2A00','bigoplus':'\u2A01','bigotimes':'\u2A02','bigsqcup':'\u2A06','bigstar':'\u2605','bigtriangledown':'\u25BD','bigtriangleup':'\u25B3','biguplus':'\u2A04','bigvee':'\u22C1','bigwedge':'\u22C0','bkarow':'\u290D','blacklozenge':'\u29EB','blacksquare':'\u25AA','blacktriangle':'\u25B4','blacktriangledown':'\u25BE','blacktriangleleft':'\u25C2','blacktriangleright':'\u25B8','blank':'\u2423','blk12':'\u2592','blk14':'\u2591','blk34':'\u2593','block':'\u2588','bne':'=\u20E5','bnequiv':'\u2261\u20E5','bnot':'\u2310','bNot':'\u2AED','bopf':'\uD835\uDD53','Bopf':'\uD835\uDD39','bot':'\u22A5','bottom':'\u22A5','bowtie':'\u22C8','boxbox':'\u29C9','boxdl':'\u2510','boxdL':'\u2555','boxDl':'\u2556','boxDL':'\u2557','boxdr':'\u250C','boxdR':'\u2552','boxDr':'\u2553','boxDR':'\u2554','boxh':'\u2500','boxH':'\u2550','boxhd':'\u252C','boxhD':'\u2565','boxHd':'\u2564','boxHD':'\u2566','boxhu':'\u2534','boxhU':'\u2568','boxHu':'\u2567','boxHU':'\u2569','boxminus':'\u229F','boxplus':'\u229E','boxtimes':'\u22A0','boxul':'\u2518','boxuL':'\u255B','boxUl':'\u255C','boxUL':'\u255D','boxur':'\u2514','boxuR':'\u2558','boxUr':'\u2559','boxUR':'\u255A','boxv':'\u2502','boxV':'\u2551','boxvh':'\u253C','boxvH':'\u256A','boxVh':'\u256B','boxVH':'\u256C','boxvl':'\u2524','boxvL':'\u2561','boxVl':'\u2562','boxVL':'\u2563','boxvr':'\u251C','boxvR':'\u255E','boxVr':'\u255F','boxVR':'\u2560','bprime':'\u2035','breve':'\u02D8','Breve':'\u02D8','brvbar':'\xA6','bscr':'\uD835\uDCB7','Bscr':'\u212C','bsemi':'\u204F','bsim':'\u223D','bsime':'\u22CD','bsol':'\\','bsolb':'\u29C5','bsolhsub':'\u27C8','bull':'\u2022','bullet':'\u2022','bump':'\u224E','bumpe':'\u224F','bumpE':'\u2AAE','bumpeq':'\u224F','Bumpeq':'\u224E','cacute':'\u0107','Cacute':'\u0106','cap':'\u2229','Cap':'\u22D2','capand':'\u2A44','capbrcup':'\u2A49','capcap':'\u2A4B','capcup':'\u2A47','capdot':'\u2A40','CapitalDifferentialD':'\u2145','caps':'\u2229\uFE00','caret':'\u2041','caron':'\u02C7','Cayleys':'\u212D','ccaps':'\u2A4D','ccaron':'\u010D','Ccaron':'\u010C','ccedil':'\xE7','Ccedil':'\xC7','ccirc':'\u0109','Ccirc':'\u0108','Cconint':'\u2230','ccups':'\u2A4C','ccupssm':'\u2A50','cdot':'\u010B','Cdot':'\u010A','cedil':'\xB8','Cedilla':'\xB8','cemptyv':'\u29B2','cent':'\xA2','centerdot':'\xB7','CenterDot':'\xB7','cfr':'\uD835\uDD20','Cfr':'\u212D','chcy':'\u0447','CHcy':'\u0427','check':'\u2713','checkmark':'\u2713','chi':'\u03C7','Chi':'\u03A7','cir':'\u25CB','circ':'\u02C6','circeq':'\u2257','circlearrowleft':'\u21BA','circlearrowright':'\u21BB','circledast':'\u229B','circledcirc':'\u229A','circleddash':'\u229D','CircleDot':'\u2299','circledR':'\xAE','circledS':'\u24C8','CircleMinus':'\u2296','CirclePlus':'\u2295','CircleTimes':'\u2297','cire':'\u2257','cirE':'\u29C3','cirfnint':'\u2A10','cirmid':'\u2AEF','cirscir':'\u29C2','ClockwiseContourIntegral':'\u2232','CloseCurlyDoubleQuote':'\u201D','CloseCurlyQuote':'\u2019','clubs':'\u2663','clubsuit':'\u2663','colon':':','Colon':'\u2237','colone':'\u2254','Colone':'\u2A74','coloneq':'\u2254','comma':',','commat':'@','comp':'\u2201','compfn':'\u2218','complement':'\u2201','complexes':'\u2102','cong':'\u2245','congdot':'\u2A6D','Congruent':'\u2261','conint':'\u222E','Conint':'\u222F','ContourIntegral':'\u222E','copf':'\uD835\uDD54','Copf':'\u2102','coprod':'\u2210','Coproduct':'\u2210','copy':'\xA9','COPY':'\xA9','copysr':'\u2117','CounterClockwiseContourIntegral':'\u2233','crarr':'\u21B5','cross':'\u2717','Cross':'\u2A2F','cscr':'\uD835\uDCB8','Cscr':'\uD835\uDC9E','csub':'\u2ACF','csube':'\u2AD1','csup':'\u2AD0','csupe':'\u2AD2','ctdot':'\u22EF','cudarrl':'\u2938','cudarrr':'\u2935','cuepr':'\u22DE','cuesc':'\u22DF','cularr':'\u21B6','cularrp':'\u293D','cup':'\u222A','Cup':'\u22D3','cupbrcap':'\u2A48','cupcap':'\u2A46','CupCap':'\u224D','cupcup':'\u2A4A','cupdot':'\u228D','cupor':'\u2A45','cups':'\u222A\uFE00','curarr':'\u21B7','curarrm':'\u293C','curlyeqprec':'\u22DE','curlyeqsucc':'\u22DF','curlyvee':'\u22CE','curlywedge':'\u22CF','curren':'\xA4','curvearrowleft':'\u21B6','curvearrowright':'\u21B7','cuvee':'\u22CE','cuwed':'\u22CF','cwconint':'\u2232','cwint':'\u2231','cylcty':'\u232D','dagger':'\u2020','Dagger':'\u2021','daleth':'\u2138','darr':'\u2193','dArr':'\u21D3','Darr':'\u21A1','dash':'\u2010','dashv':'\u22A3','Dashv':'\u2AE4','dbkarow':'\u290F','dblac':'\u02DD','dcaron':'\u010F','Dcaron':'\u010E','dcy':'\u0434','Dcy':'\u0414','dd':'\u2146','DD':'\u2145','ddagger':'\u2021','ddarr':'\u21CA','DDotrahd':'\u2911','ddotseq':'\u2A77','deg':'\xB0','Del':'\u2207','delta':'\u03B4','Delta':'\u0394','demptyv':'\u29B1','dfisht':'\u297F','dfr':'\uD835\uDD21','Dfr':'\uD835\uDD07','dHar':'\u2965','dharl':'\u21C3','dharr':'\u21C2','DiacriticalAcute':'\xB4','DiacriticalDot':'\u02D9','DiacriticalDoubleAcute':'\u02DD','DiacriticalGrave':'`','DiacriticalTilde':'\u02DC','diam':'\u22C4','diamond':'\u22C4','Diamond':'\u22C4','diamondsuit':'\u2666','diams':'\u2666','die':'\xA8','DifferentialD':'\u2146','digamma':'\u03DD','disin':'\u22F2','div':'\xF7','divide':'\xF7','divideontimes':'\u22C7','divonx':'\u22C7','djcy':'\u0452','DJcy':'\u0402','dlcorn':'\u231E','dlcrop':'\u230D','dollar':'$','dopf':'\uD835\uDD55','Dopf':'\uD835\uDD3B','dot':'\u02D9','Dot':'\xA8','DotDot':'\u20DC','doteq':'\u2250','doteqdot':'\u2251','DotEqual':'\u2250','dotminus':'\u2238','dotplus':'\u2214','dotsquare':'\u22A1','doublebarwedge':'\u2306','DoubleContourIntegral':'\u222F','DoubleDot':'\xA8','DoubleDownArrow':'\u21D3','DoubleLeftArrow':'\u21D0','DoubleLeftRightArrow':'\u21D4','DoubleLeftTee':'\u2AE4','DoubleLongLeftArrow':'\u27F8','DoubleLongLeftRightArrow':'\u27FA','DoubleLongRightArrow':'\u27F9','DoubleRightArrow':'\u21D2','DoubleRightTee':'\u22A8','DoubleUpArrow':'\u21D1','DoubleUpDownArrow':'\u21D5','DoubleVerticalBar':'\u2225','downarrow':'\u2193','Downarrow':'\u21D3','DownArrow':'\u2193','DownArrowBar':'\u2913','DownArrowUpArrow':'\u21F5','DownBreve':'\u0311','downdownarrows':'\u21CA','downharpoonleft':'\u21C3','downharpoonright':'\u21C2','DownLeftRightVector':'\u2950','DownLeftTeeVector':'\u295E','DownLeftVector':'\u21BD','DownLeftVectorBar':'\u2956','DownRightTeeVector':'\u295F','DownRightVector':'\u21C1','DownRightVectorBar':'\u2957','DownTee':'\u22A4','DownTeeArrow':'\u21A7','drbkarow':'\u2910','drcorn':'\u231F','drcrop':'\u230C','dscr':'\uD835\uDCB9','Dscr':'\uD835\uDC9F','dscy':'\u0455','DScy':'\u0405','dsol':'\u29F6','dstrok':'\u0111','Dstrok':'\u0110','dtdot':'\u22F1','dtri':'\u25BF','dtrif':'\u25BE','duarr':'\u21F5','duhar':'\u296F','dwangle':'\u29A6','dzcy':'\u045F','DZcy':'\u040F','dzigrarr':'\u27FF','eacute':'\xE9','Eacute':'\xC9','easter':'\u2A6E','ecaron':'\u011B','Ecaron':'\u011A','ecir':'\u2256','ecirc':'\xEA','Ecirc':'\xCA','ecolon':'\u2255','ecy':'\u044D','Ecy':'\u042D','eDDot':'\u2A77','edot':'\u0117','eDot':'\u2251','Edot':'\u0116','ee':'\u2147','efDot':'\u2252','efr':'\uD835\uDD22','Efr':'\uD835\uDD08','eg':'\u2A9A','egrave':'\xE8','Egrave':'\xC8','egs':'\u2A96','egsdot':'\u2A98','el':'\u2A99','Element':'\u2208','elinters':'\u23E7','ell':'\u2113','els':'\u2A95','elsdot':'\u2A97','emacr':'\u0113','Emacr':'\u0112','empty':'\u2205','emptyset':'\u2205','EmptySmallSquare':'\u25FB','emptyv':'\u2205','EmptyVerySmallSquare':'\u25AB','emsp':'\u2003','emsp13':'\u2004','emsp14':'\u2005','eng':'\u014B','ENG':'\u014A','ensp':'\u2002','eogon':'\u0119','Eogon':'\u0118','eopf':'\uD835\uDD56','Eopf':'\uD835\uDD3C','epar':'\u22D5','eparsl':'\u29E3','eplus':'\u2A71','epsi':'\u03B5','epsilon':'\u03B5','Epsilon':'\u0395','epsiv':'\u03F5','eqcirc':'\u2256','eqcolon':'\u2255','eqsim':'\u2242','eqslantgtr':'\u2A96','eqslantless':'\u2A95','Equal':'\u2A75','equals':'=','EqualTilde':'\u2242','equest':'\u225F','Equilibrium':'\u21CC','equiv':'\u2261','equivDD':'\u2A78','eqvparsl':'\u29E5','erarr':'\u2971','erDot':'\u2253','escr':'\u212F','Escr':'\u2130','esdot':'\u2250','esim':'\u2242','Esim':'\u2A73','eta':'\u03B7','Eta':'\u0397','eth':'\xF0','ETH':'\xD0','euml':'\xEB','Euml':'\xCB','euro':'\u20AC','excl':'!','exist':'\u2203','Exists':'\u2203','expectation':'\u2130','exponentiale':'\u2147','ExponentialE':'\u2147','fallingdotseq':'\u2252','fcy':'\u0444','Fcy':'\u0424','female':'\u2640','ffilig':'\uFB03','fflig':'\uFB00','ffllig':'\uFB04','ffr':'\uD835\uDD23','Ffr':'\uD835\uDD09','filig':'\uFB01','FilledSmallSquare':'\u25FC','FilledVerySmallSquare':'\u25AA','fjlig':'fj','flat':'\u266D','fllig':'\uFB02','fltns':'\u25B1','fnof':'\u0192','fopf':'\uD835\uDD57','Fopf':'\uD835\uDD3D','forall':'\u2200','ForAll':'\u2200','fork':'\u22D4','forkv':'\u2AD9','Fouriertrf':'\u2131','fpartint':'\u2A0D','frac12':'\xBD','frac13':'\u2153','frac14':'\xBC','frac15':'\u2155','frac16':'\u2159','frac18':'\u215B','frac23':'\u2154','frac25':'\u2156','frac34':'\xBE','frac35':'\u2157','frac38':'\u215C','frac45':'\u2158','frac56':'\u215A','frac58':'\u215D','frac78':'\u215E','frasl':'\u2044','frown':'\u2322','fscr':'\uD835\uDCBB','Fscr':'\u2131','gacute':'\u01F5','gamma':'\u03B3','Gamma':'\u0393','gammad':'\u03DD','Gammad':'\u03DC','gap':'\u2A86','gbreve':'\u011F','Gbreve':'\u011E','Gcedil':'\u0122','gcirc':'\u011D','Gcirc':'\u011C','gcy':'\u0433','Gcy':'\u0413','gdot':'\u0121','Gdot':'\u0120','ge':'\u2265','gE':'\u2267','gel':'\u22DB','gEl':'\u2A8C','geq':'\u2265','geqq':'\u2267','geqslant':'\u2A7E','ges':'\u2A7E','gescc':'\u2AA9','gesdot':'\u2A80','gesdoto':'\u2A82','gesdotol':'\u2A84','gesl':'\u22DB\uFE00','gesles':'\u2A94','gfr':'\uD835\uDD24','Gfr':'\uD835\uDD0A','gg':'\u226B','Gg':'\u22D9','ggg':'\u22D9','gimel':'\u2137','gjcy':'\u0453','GJcy':'\u0403','gl':'\u2277','gla':'\u2AA5','glE':'\u2A92','glj':'\u2AA4','gnap':'\u2A8A','gnapprox':'\u2A8A','gne':'\u2A88','gnE':'\u2269','gneq':'\u2A88','gneqq':'\u2269','gnsim':'\u22E7','gopf':'\uD835\uDD58','Gopf':'\uD835\uDD3E','grave':'`','GreaterEqual':'\u2265','GreaterEqualLess':'\u22DB','GreaterFullEqual':'\u2267','GreaterGreater':'\u2AA2','GreaterLess':'\u2277','GreaterSlantEqual':'\u2A7E','GreaterTilde':'\u2273','gscr':'\u210A','Gscr':'\uD835\uDCA2','gsim':'\u2273','gsime':'\u2A8E','gsiml':'\u2A90','gt':'>','Gt':'\u226B','GT':'>','gtcc':'\u2AA7','gtcir':'\u2A7A','gtdot':'\u22D7','gtlPar':'\u2995','gtquest':'\u2A7C','gtrapprox':'\u2A86','gtrarr':'\u2978','gtrdot':'\u22D7','gtreqless':'\u22DB','gtreqqless':'\u2A8C','gtrless':'\u2277','gtrsim':'\u2273','gvertneqq':'\u2269\uFE00','gvnE':'\u2269\uFE00','Hacek':'\u02C7','hairsp':'\u200A','half':'\xBD','hamilt':'\u210B','hardcy':'\u044A','HARDcy':'\u042A','harr':'\u2194','hArr':'\u21D4','harrcir':'\u2948','harrw':'\u21AD','Hat':'^','hbar':'\u210F','hcirc':'\u0125','Hcirc':'\u0124','hearts':'\u2665','heartsuit':'\u2665','hellip':'\u2026','hercon':'\u22B9','hfr':'\uD835\uDD25','Hfr':'\u210C','HilbertSpace':'\u210B','hksearow':'\u2925','hkswarow':'\u2926','hoarr':'\u21FF','homtht':'\u223B','hookleftarrow':'\u21A9','hookrightarrow':'\u21AA','hopf':'\uD835\uDD59','Hopf':'\u210D','horbar':'\u2015','HorizontalLine':'\u2500','hscr':'\uD835\uDCBD','Hscr':'\u210B','hslash':'\u210F','hstrok':'\u0127','Hstrok':'\u0126','HumpDownHump':'\u224E','HumpEqual':'\u224F','hybull':'\u2043','hyphen':'\u2010','iacute':'\xED','Iacute':'\xCD','ic':'\u2063','icirc':'\xEE','Icirc':'\xCE','icy':'\u0438','Icy':'\u0418','Idot':'\u0130','iecy':'\u0435','IEcy':'\u0415','iexcl':'\xA1','iff':'\u21D4','ifr':'\uD835\uDD26','Ifr':'\u2111','igrave':'\xEC','Igrave':'\xCC','ii':'\u2148','iiiint':'\u2A0C','iiint':'\u222D','iinfin':'\u29DC','iiota':'\u2129','ijlig':'\u0133','IJlig':'\u0132','Im':'\u2111','imacr':'\u012B','Imacr':'\u012A','image':'\u2111','ImaginaryI':'\u2148','imagline':'\u2110','imagpart':'\u2111','imath':'\u0131','imof':'\u22B7','imped':'\u01B5','Implies':'\u21D2','in':'\u2208','incare':'\u2105','infin':'\u221E','infintie':'\u29DD','inodot':'\u0131','int':'\u222B','Int':'\u222C','intcal':'\u22BA','integers':'\u2124','Integral':'\u222B','intercal':'\u22BA','Intersection':'\u22C2','intlarhk':'\u2A17','intprod':'\u2A3C','InvisibleComma':'\u2063','InvisibleTimes':'\u2062','iocy':'\u0451','IOcy':'\u0401','iogon':'\u012F','Iogon':'\u012E','iopf':'\uD835\uDD5A','Iopf':'\uD835\uDD40','iota':'\u03B9','Iota':'\u0399','iprod':'\u2A3C','iquest':'\xBF','iscr':'\uD835\uDCBE','Iscr':'\u2110','isin':'\u2208','isindot':'\u22F5','isinE':'\u22F9','isins':'\u22F4','isinsv':'\u22F3','isinv':'\u2208','it':'\u2062','itilde':'\u0129','Itilde':'\u0128','iukcy':'\u0456','Iukcy':'\u0406','iuml':'\xEF','Iuml':'\xCF','jcirc':'\u0135','Jcirc':'\u0134','jcy':'\u0439','Jcy':'\u0419','jfr':'\uD835\uDD27','Jfr':'\uD835\uDD0D','jmath':'\u0237','jopf':'\uD835\uDD5B','Jopf':'\uD835\uDD41','jscr':'\uD835\uDCBF','Jscr':'\uD835\uDCA5','jsercy':'\u0458','Jsercy':'\u0408','jukcy':'\u0454','Jukcy':'\u0404','kappa':'\u03BA','Kappa':'\u039A','kappav':'\u03F0','kcedil':'\u0137','Kcedil':'\u0136','kcy':'\u043A','Kcy':'\u041A','kfr':'\uD835\uDD28','Kfr':'\uD835\uDD0E','kgreen':'\u0138','khcy':'\u0445','KHcy':'\u0425','kjcy':'\u045C','KJcy':'\u040C','kopf':'\uD835\uDD5C','Kopf':'\uD835\uDD42','kscr':'\uD835\uDCC0','Kscr':'\uD835\uDCA6','lAarr':'\u21DA','lacute':'\u013A','Lacute':'\u0139','laemptyv':'\u29B4','lagran':'\u2112','lambda':'\u03BB','Lambda':'\u039B','lang':'\u27E8','Lang':'\u27EA','langd':'\u2991','langle':'\u27E8','lap':'\u2A85','Laplacetrf':'\u2112','laquo':'\xAB','larr':'\u2190','lArr':'\u21D0','Larr':'\u219E','larrb':'\u21E4','larrbfs':'\u291F','larrfs':'\u291D','larrhk':'\u21A9','larrlp':'\u21AB','larrpl':'\u2939','larrsim':'\u2973','larrtl':'\u21A2','lat':'\u2AAB','latail':'\u2919','lAtail':'\u291B','late':'\u2AAD','lates':'\u2AAD\uFE00','lbarr':'\u290C','lBarr':'\u290E','lbbrk':'\u2772','lbrace':'{','lbrack':'[','lbrke':'\u298B','lbrksld':'\u298F','lbrkslu':'\u298D','lcaron':'\u013E','Lcaron':'\u013D','lcedil':'\u013C','Lcedil':'\u013B','lceil':'\u2308','lcub':'{','lcy':'\u043B','Lcy':'\u041B','ldca':'\u2936','ldquo':'\u201C','ldquor':'\u201E','ldrdhar':'\u2967','ldrushar':'\u294B','ldsh':'\u21B2','le':'\u2264','lE':'\u2266','LeftAngleBracket':'\u27E8','leftarrow':'\u2190','Leftarrow':'\u21D0','LeftArrow':'\u2190','LeftArrowBar':'\u21E4','LeftArrowRightArrow':'\u21C6','leftarrowtail':'\u21A2','LeftCeiling':'\u2308','LeftDoubleBracket':'\u27E6','LeftDownTeeVector':'\u2961','LeftDownVector':'\u21C3','LeftDownVectorBar':'\u2959','LeftFloor':'\u230A','leftharpoondown':'\u21BD','leftharpoonup':'\u21BC','leftleftarrows':'\u21C7','leftrightarrow':'\u2194','Leftrightarrow':'\u21D4','LeftRightArrow':'\u2194','leftrightarrows':'\u21C6','leftrightharpoons':'\u21CB','leftrightsquigarrow':'\u21AD','LeftRightVector':'\u294E','LeftTee':'\u22A3','LeftTeeArrow':'\u21A4','LeftTeeVector':'\u295A','leftthreetimes':'\u22CB','LeftTriangle':'\u22B2','LeftTriangleBar':'\u29CF','LeftTriangleEqual':'\u22B4','LeftUpDownVector':'\u2951','LeftUpTeeVector':'\u2960','LeftUpVector':'\u21BF','LeftUpVectorBar':'\u2958','LeftVector':'\u21BC','LeftVectorBar':'\u2952','leg':'\u22DA','lEg':'\u2A8B','leq':'\u2264','leqq':'\u2266','leqslant':'\u2A7D','les':'\u2A7D','lescc':'\u2AA8','lesdot':'\u2A7F','lesdoto':'\u2A81','lesdotor':'\u2A83','lesg':'\u22DA\uFE00','lesges':'\u2A93','lessapprox':'\u2A85','lessdot':'\u22D6','lesseqgtr':'\u22DA','lesseqqgtr':'\u2A8B','LessEqualGreater':'\u22DA','LessFullEqual':'\u2266','LessGreater':'\u2276','lessgtr':'\u2276','LessLess':'\u2AA1','lesssim':'\u2272','LessSlantEqual':'\u2A7D','LessTilde':'\u2272','lfisht':'\u297C','lfloor':'\u230A','lfr':'\uD835\uDD29','Lfr':'\uD835\uDD0F','lg':'\u2276','lgE':'\u2A91','lHar':'\u2962','lhard':'\u21BD','lharu':'\u21BC','lharul':'\u296A','lhblk':'\u2584','ljcy':'\u0459','LJcy':'\u0409','ll':'\u226A','Ll':'\u22D8','llarr':'\u21C7','llcorner':'\u231E','Lleftarrow':'\u21DA','llhard':'\u296B','lltri':'\u25FA','lmidot':'\u0140','Lmidot':'\u013F','lmoust':'\u23B0','lmoustache':'\u23B0','lnap':'\u2A89','lnapprox':'\u2A89','lne':'\u2A87','lnE':'\u2268','lneq':'\u2A87','lneqq':'\u2268','lnsim':'\u22E6','loang':'\u27EC','loarr':'\u21FD','lobrk':'\u27E6','longleftarrow':'\u27F5','Longleftarrow':'\u27F8','LongLeftArrow':'\u27F5','longleftrightarrow':'\u27F7','Longleftrightarrow':'\u27FA','LongLeftRightArrow':'\u27F7','longmapsto':'\u27FC','longrightarrow':'\u27F6','Longrightarrow':'\u27F9','LongRightArrow':'\u27F6','looparrowleft':'\u21AB','looparrowright':'\u21AC','lopar':'\u2985','lopf':'\uD835\uDD5D','Lopf':'\uD835\uDD43','loplus':'\u2A2D','lotimes':'\u2A34','lowast':'\u2217','lowbar':'_','LowerLeftArrow':'\u2199','LowerRightArrow':'\u2198','loz':'\u25CA','lozenge':'\u25CA','lozf':'\u29EB','lpar':'(','lparlt':'\u2993','lrarr':'\u21C6','lrcorner':'\u231F','lrhar':'\u21CB','lrhard':'\u296D','lrm':'\u200E','lrtri':'\u22BF','lsaquo':'\u2039','lscr':'\uD835\uDCC1','Lscr':'\u2112','lsh':'\u21B0','Lsh':'\u21B0','lsim':'\u2272','lsime':'\u2A8D','lsimg':'\u2A8F','lsqb':'[','lsquo':'\u2018','lsquor':'\u201A','lstrok':'\u0142','Lstrok':'\u0141','lt':'<','Lt':'\u226A','LT':'<','ltcc':'\u2AA6','ltcir':'\u2A79','ltdot':'\u22D6','lthree':'\u22CB','ltimes':'\u22C9','ltlarr':'\u2976','ltquest':'\u2A7B','ltri':'\u25C3','ltrie':'\u22B4','ltrif':'\u25C2','ltrPar':'\u2996','lurdshar':'\u294A','luruhar':'\u2966','lvertneqq':'\u2268\uFE00','lvnE':'\u2268\uFE00','macr':'\xAF','male':'\u2642','malt':'\u2720','maltese':'\u2720','map':'\u21A6','Map':'\u2905','mapsto':'\u21A6','mapstodown':'\u21A7','mapstoleft':'\u21A4','mapstoup':'\u21A5','marker':'\u25AE','mcomma':'\u2A29','mcy':'\u043C','Mcy':'\u041C','mdash':'\u2014','mDDot':'\u223A','measuredangle':'\u2221','MediumSpace':'\u205F','Mellintrf':'\u2133','mfr':'\uD835\uDD2A','Mfr':'\uD835\uDD10','mho':'\u2127','micro':'\xB5','mid':'\u2223','midast':'*','midcir':'\u2AF0','middot':'\xB7','minus':'\u2212','minusb':'\u229F','minusd':'\u2238','minusdu':'\u2A2A','MinusPlus':'\u2213','mlcp':'\u2ADB','mldr':'\u2026','mnplus':'\u2213','models':'\u22A7','mopf':'\uD835\uDD5E','Mopf':'\uD835\uDD44','mp':'\u2213','mscr':'\uD835\uDCC2','Mscr':'\u2133','mstpos':'\u223E','mu':'\u03BC','Mu':'\u039C','multimap':'\u22B8','mumap':'\u22B8','nabla':'\u2207','nacute':'\u0144','Nacute':'\u0143','nang':'\u2220\u20D2','nap':'\u2249','napE':'\u2A70\u0338','napid':'\u224B\u0338','napos':'\u0149','napprox':'\u2249','natur':'\u266E','natural':'\u266E','naturals':'\u2115','nbsp':'\xA0','nbump':'\u224E\u0338','nbumpe':'\u224F\u0338','ncap':'\u2A43','ncaron':'\u0148','Ncaron':'\u0147','ncedil':'\u0146','Ncedil':'\u0145','ncong':'\u2247','ncongdot':'\u2A6D\u0338','ncup':'\u2A42','ncy':'\u043D','Ncy':'\u041D','ndash':'\u2013','ne':'\u2260','nearhk':'\u2924','nearr':'\u2197','neArr':'\u21D7','nearrow':'\u2197','nedot':'\u2250\u0338','NegativeMediumSpace':'\u200B','NegativeThickSpace':'\u200B','NegativeThinSpace':'\u200B','NegativeVeryThinSpace':'\u200B','nequiv':'\u2262','nesear':'\u2928','nesim':'\u2242\u0338','NestedGreaterGreater':'\u226B','NestedLessLess':'\u226A','NewLine':'\n','nexist':'\u2204','nexists':'\u2204','nfr':'\uD835\uDD2B','Nfr':'\uD835\uDD11','nge':'\u2271','ngE':'\u2267\u0338','ngeq':'\u2271','ngeqq':'\u2267\u0338','ngeqslant':'\u2A7E\u0338','nges':'\u2A7E\u0338','nGg':'\u22D9\u0338','ngsim':'\u2275','ngt':'\u226F','nGt':'\u226B\u20D2','ngtr':'\u226F','nGtv':'\u226B\u0338','nharr':'\u21AE','nhArr':'\u21CE','nhpar':'\u2AF2','ni':'\u220B','nis':'\u22FC','nisd':'\u22FA','niv':'\u220B','njcy':'\u045A','NJcy':'\u040A','nlarr':'\u219A','nlArr':'\u21CD','nldr':'\u2025','nle':'\u2270','nlE':'\u2266\u0338','nleftarrow':'\u219A','nLeftarrow':'\u21CD','nleftrightarrow':'\u21AE','nLeftrightarrow':'\u21CE','nleq':'\u2270','nleqq':'\u2266\u0338','nleqslant':'\u2A7D\u0338','nles':'\u2A7D\u0338','nless':'\u226E','nLl':'\u22D8\u0338','nlsim':'\u2274','nlt':'\u226E','nLt':'\u226A\u20D2','nltri':'\u22EA','nltrie':'\u22EC','nLtv':'\u226A\u0338','nmid':'\u2224','NoBreak':'\u2060','NonBreakingSpace':'\xA0','nopf':'\uD835\uDD5F','Nopf':'\u2115','not':'\xAC','Not':'\u2AEC','NotCongruent':'\u2262','NotCupCap':'\u226D','NotDoubleVerticalBar':'\u2226','NotElement':'\u2209','NotEqual':'\u2260','NotEqualTilde':'\u2242\u0338','NotExists':'\u2204','NotGreater':'\u226F','NotGreaterEqual':'\u2271','NotGreaterFullEqual':'\u2267\u0338','NotGreaterGreater':'\u226B\u0338','NotGreaterLess':'\u2279','NotGreaterSlantEqual':'\u2A7E\u0338','NotGreaterTilde':'\u2275','NotHumpDownHump':'\u224E\u0338','NotHumpEqual':'\u224F\u0338','notin':'\u2209','notindot':'\u22F5\u0338','notinE':'\u22F9\u0338','notinva':'\u2209','notinvb':'\u22F7','notinvc':'\u22F6','NotLeftTriangle':'\u22EA','NotLeftTriangleBar':'\u29CF\u0338','NotLeftTriangleEqual':'\u22EC','NotLess':'\u226E','NotLessEqual':'\u2270','NotLessGreater':'\u2278','NotLessLess':'\u226A\u0338','NotLessSlantEqual':'\u2A7D\u0338','NotLessTilde':'\u2274','NotNestedGreaterGreater':'\u2AA2\u0338','NotNestedLessLess':'\u2AA1\u0338','notni':'\u220C','notniva':'\u220C','notnivb':'\u22FE','notnivc':'\u22FD','NotPrecedes':'\u2280','NotPrecedesEqual':'\u2AAF\u0338','NotPrecedesSlantEqual':'\u22E0','NotReverseElement':'\u220C','NotRightTriangle':'\u22EB','NotRightTriangleBar':'\u29D0\u0338','NotRightTriangleEqual':'\u22ED','NotSquareSubset':'\u228F\u0338','NotSquareSubsetEqual':'\u22E2','NotSquareSuperset':'\u2290\u0338','NotSquareSupersetEqual':'\u22E3','NotSubset':'\u2282\u20D2','NotSubsetEqual':'\u2288','NotSucceeds':'\u2281','NotSucceedsEqual':'\u2AB0\u0338','NotSucceedsSlantEqual':'\u22E1','NotSucceedsTilde':'\u227F\u0338','NotSuperset':'\u2283\u20D2','NotSupersetEqual':'\u2289','NotTilde':'\u2241','NotTildeEqual':'\u2244','NotTildeFullEqual':'\u2247','NotTildeTilde':'\u2249','NotVerticalBar':'\u2224','npar':'\u2226','nparallel':'\u2226','nparsl':'\u2AFD\u20E5','npart':'\u2202\u0338','npolint':'\u2A14','npr':'\u2280','nprcue':'\u22E0','npre':'\u2AAF\u0338','nprec':'\u2280','npreceq':'\u2AAF\u0338','nrarr':'\u219B','nrArr':'\u21CF','nrarrc':'\u2933\u0338','nrarrw':'\u219D\u0338','nrightarrow':'\u219B','nRightarrow':'\u21CF','nrtri':'\u22EB','nrtrie':'\u22ED','nsc':'\u2281','nsccue':'\u22E1','nsce':'\u2AB0\u0338','nscr':'\uD835\uDCC3','Nscr':'\uD835\uDCA9','nshortmid':'\u2224','nshortparallel':'\u2226','nsim':'\u2241','nsime':'\u2244','nsimeq':'\u2244','nsmid':'\u2224','nspar':'\u2226','nsqsube':'\u22E2','nsqsupe':'\u22E3','nsub':'\u2284','nsube':'\u2288','nsubE':'\u2AC5\u0338','nsubset':'\u2282\u20D2','nsubseteq':'\u2288','nsubseteqq':'\u2AC5\u0338','nsucc':'\u2281','nsucceq':'\u2AB0\u0338','nsup':'\u2285','nsupe':'\u2289','nsupE':'\u2AC6\u0338','nsupset':'\u2283\u20D2','nsupseteq':'\u2289','nsupseteqq':'\u2AC6\u0338','ntgl':'\u2279','ntilde':'\xF1','Ntilde':'\xD1','ntlg':'\u2278','ntriangleleft':'\u22EA','ntrianglelefteq':'\u22EC','ntriangleright':'\u22EB','ntrianglerighteq':'\u22ED','nu':'\u03BD','Nu':'\u039D','num':'#','numero':'\u2116','numsp':'\u2007','nvap':'\u224D\u20D2','nvdash':'\u22AC','nvDash':'\u22AD','nVdash':'\u22AE','nVDash':'\u22AF','nvge':'\u2265\u20D2','nvgt':'>\u20D2','nvHarr':'\u2904','nvinfin':'\u29DE','nvlArr':'\u2902','nvle':'\u2264\u20D2','nvlt':'<\u20D2','nvltrie':'\u22B4\u20D2','nvrArr':'\u2903','nvrtrie':'\u22B5\u20D2','nvsim':'\u223C\u20D2','nwarhk':'\u2923','nwarr':'\u2196','nwArr':'\u21D6','nwarrow':'\u2196','nwnear':'\u2927','oacute':'\xF3','Oacute':'\xD3','oast':'\u229B','ocir':'\u229A','ocirc':'\xF4','Ocirc':'\xD4','ocy':'\u043E','Ocy':'\u041E','odash':'\u229D','odblac':'\u0151','Odblac':'\u0150','odiv':'\u2A38','odot':'\u2299','odsold':'\u29BC','oelig':'\u0153','OElig':'\u0152','ofcir':'\u29BF','ofr':'\uD835\uDD2C','Ofr':'\uD835\uDD12','ogon':'\u02DB','ograve':'\xF2','Ograve':'\xD2','ogt':'\u29C1','ohbar':'\u29B5','ohm':'\u03A9','oint':'\u222E','olarr':'\u21BA','olcir':'\u29BE','olcross':'\u29BB','oline':'\u203E','olt':'\u29C0','omacr':'\u014D','Omacr':'\u014C','omega':'\u03C9','Omega':'\u03A9','omicron':'\u03BF','Omicron':'\u039F','omid':'\u29B6','ominus':'\u2296','oopf':'\uD835\uDD60','Oopf':'\uD835\uDD46','opar':'\u29B7','OpenCurlyDoubleQuote':'\u201C','OpenCurlyQuote':'\u2018','operp':'\u29B9','oplus':'\u2295','or':'\u2228','Or':'\u2A54','orarr':'\u21BB','ord':'\u2A5D','order':'\u2134','orderof':'\u2134','ordf':'\xAA','ordm':'\xBA','origof':'\u22B6','oror':'\u2A56','orslope':'\u2A57','orv':'\u2A5B','oS':'\u24C8','oscr':'\u2134','Oscr':'\uD835\uDCAA','oslash':'\xF8','Oslash':'\xD8','osol':'\u2298','otilde':'\xF5','Otilde':'\xD5','otimes':'\u2297','Otimes':'\u2A37','otimesas':'\u2A36','ouml':'\xF6','Ouml':'\xD6','ovbar':'\u233D','OverBar':'\u203E','OverBrace':'\u23DE','OverBracket':'\u23B4','OverParenthesis':'\u23DC','par':'\u2225','para':'\xB6','parallel':'\u2225','parsim':'\u2AF3','parsl':'\u2AFD','part':'\u2202','PartialD':'\u2202','pcy':'\u043F','Pcy':'\u041F','percnt':'%','period':'.','permil':'\u2030','perp':'\u22A5','pertenk':'\u2031','pfr':'\uD835\uDD2D','Pfr':'\uD835\uDD13','phi':'\u03C6','Phi':'\u03A6','phiv':'\u03D5','phmmat':'\u2133','phone':'\u260E','pi':'\u03C0','Pi':'\u03A0','pitchfork':'\u22D4','piv':'\u03D6','planck':'\u210F','planckh':'\u210E','plankv':'\u210F','plus':'+','plusacir':'\u2A23','plusb':'\u229E','pluscir':'\u2A22','plusdo':'\u2214','plusdu':'\u2A25','pluse':'\u2A72','PlusMinus':'\xB1','plusmn':'\xB1','plussim':'\u2A26','plustwo':'\u2A27','pm':'\xB1','Poincareplane':'\u210C','pointint':'\u2A15','popf':'\uD835\uDD61','Popf':'\u2119','pound':'\xA3','pr':'\u227A','Pr':'\u2ABB','prap':'\u2AB7','prcue':'\u227C','pre':'\u2AAF','prE':'\u2AB3','prec':'\u227A','precapprox':'\u2AB7','preccurlyeq':'\u227C','Precedes':'\u227A','PrecedesEqual':'\u2AAF','PrecedesSlantEqual':'\u227C','PrecedesTilde':'\u227E','preceq':'\u2AAF','precnapprox':'\u2AB9','precneqq':'\u2AB5','precnsim':'\u22E8','precsim':'\u227E','prime':'\u2032','Prime':'\u2033','primes':'\u2119','prnap':'\u2AB9','prnE':'\u2AB5','prnsim':'\u22E8','prod':'\u220F','Product':'\u220F','profalar':'\u232E','profline':'\u2312','profsurf':'\u2313','prop':'\u221D','Proportion':'\u2237','Proportional':'\u221D','propto':'\u221D','prsim':'\u227E','prurel':'\u22B0','pscr':'\uD835\uDCC5','Pscr':'\uD835\uDCAB','psi':'\u03C8','Psi':'\u03A8','puncsp':'\u2008','qfr':'\uD835\uDD2E','Qfr':'\uD835\uDD14','qint':'\u2A0C','qopf':'\uD835\uDD62','Qopf':'\u211A','qprime':'\u2057','qscr':'\uD835\uDCC6','Qscr':'\uD835\uDCAC','quaternions':'\u210D','quatint':'\u2A16','quest':'?','questeq':'\u225F','quot':'"','QUOT':'"','rAarr':'\u21DB','race':'\u223D\u0331','racute':'\u0155','Racute':'\u0154','radic':'\u221A','raemptyv':'\u29B3','rang':'\u27E9','Rang':'\u27EB','rangd':'\u2992','range':'\u29A5','rangle':'\u27E9','raquo':'\xBB','rarr':'\u2192','rArr':'\u21D2','Rarr':'\u21A0','rarrap':'\u2975','rarrb':'\u21E5','rarrbfs':'\u2920','rarrc':'\u2933','rarrfs':'\u291E','rarrhk':'\u21AA','rarrlp':'\u21AC','rarrpl':'\u2945','rarrsim':'\u2974','rarrtl':'\u21A3','Rarrtl':'\u2916','rarrw':'\u219D','ratail':'\u291A','rAtail':'\u291C','ratio':'\u2236','rationals':'\u211A','rbarr':'\u290D','rBarr':'\u290F','RBarr':'\u2910','rbbrk':'\u2773','rbrace':'}','rbrack':']','rbrke':'\u298C','rbrksld':'\u298E','rbrkslu':'\u2990','rcaron':'\u0159','Rcaron':'\u0158','rcedil':'\u0157','Rcedil':'\u0156','rceil':'\u2309','rcub':'}','rcy':'\u0440','Rcy':'\u0420','rdca':'\u2937','rdldhar':'\u2969','rdquo':'\u201D','rdquor':'\u201D','rdsh':'\u21B3','Re':'\u211C','real':'\u211C','realine':'\u211B','realpart':'\u211C','reals':'\u211D','rect':'\u25AD','reg':'\xAE','REG':'\xAE','ReverseElement':'\u220B','ReverseEquilibrium':'\u21CB','ReverseUpEquilibrium':'\u296F','rfisht':'\u297D','rfloor':'\u230B','rfr':'\uD835\uDD2F','Rfr':'\u211C','rHar':'\u2964','rhard':'\u21C1','rharu':'\u21C0','rharul':'\u296C','rho':'\u03C1','Rho':'\u03A1','rhov':'\u03F1','RightAngleBracket':'\u27E9','rightarrow':'\u2192','Rightarrow':'\u21D2','RightArrow':'\u2192','RightArrowBar':'\u21E5','RightArrowLeftArrow':'\u21C4','rightarrowtail':'\u21A3','RightCeiling':'\u2309','RightDoubleBracket':'\u27E7','RightDownTeeVector':'\u295D','RightDownVector':'\u21C2','RightDownVectorBar':'\u2955','RightFloor':'\u230B','rightharpoondown':'\u21C1','rightharpoonup':'\u21C0','rightleftarrows':'\u21C4','rightleftharpoons':'\u21CC','rightrightarrows':'\u21C9','rightsquigarrow':'\u219D','RightTee':'\u22A2','RightTeeArrow':'\u21A6','RightTeeVector':'\u295B','rightthreetimes':'\u22CC','RightTriangle':'\u22B3','RightTriangleBar':'\u29D0','RightTriangleEqual':'\u22B5','RightUpDownVector':'\u294F','RightUpTeeVector':'\u295C','RightUpVector':'\u21BE','RightUpVectorBar':'\u2954','RightVector':'\u21C0','RightVectorBar':'\u2953','ring':'\u02DA','risingdotseq':'\u2253','rlarr':'\u21C4','rlhar':'\u21CC','rlm':'\u200F','rmoust':'\u23B1','rmoustache':'\u23B1','rnmid':'\u2AEE','roang':'\u27ED','roarr':'\u21FE','robrk':'\u27E7','ropar':'\u2986','ropf':'\uD835\uDD63','Ropf':'\u211D','roplus':'\u2A2E','rotimes':'\u2A35','RoundImplies':'\u2970','rpar':')','rpargt':'\u2994','rppolint':'\u2A12','rrarr':'\u21C9','Rrightarrow':'\u21DB','rsaquo':'\u203A','rscr':'\uD835\uDCC7','Rscr':'\u211B','rsh':'\u21B1','Rsh':'\u21B1','rsqb':']','rsquo':'\u2019','rsquor':'\u2019','rthree':'\u22CC','rtimes':'\u22CA','rtri':'\u25B9','rtrie':'\u22B5','rtrif':'\u25B8','rtriltri':'\u29CE','RuleDelayed':'\u29F4','ruluhar':'\u2968','rx':'\u211E','sacute':'\u015B','Sacute':'\u015A','sbquo':'\u201A','sc':'\u227B','Sc':'\u2ABC','scap':'\u2AB8','scaron':'\u0161','Scaron':'\u0160','sccue':'\u227D','sce':'\u2AB0','scE':'\u2AB4','scedil':'\u015F','Scedil':'\u015E','scirc':'\u015D','Scirc':'\u015C','scnap':'\u2ABA','scnE':'\u2AB6','scnsim':'\u22E9','scpolint':'\u2A13','scsim':'\u227F','scy':'\u0441','Scy':'\u0421','sdot':'\u22C5','sdotb':'\u22A1','sdote':'\u2A66','searhk':'\u2925','searr':'\u2198','seArr':'\u21D8','searrow':'\u2198','sect':'\xA7','semi':';','seswar':'\u2929','setminus':'\u2216','setmn':'\u2216','sext':'\u2736','sfr':'\uD835\uDD30','Sfr':'\uD835\uDD16','sfrown':'\u2322','sharp':'\u266F','shchcy':'\u0449','SHCHcy':'\u0429','shcy':'\u0448','SHcy':'\u0428','ShortDownArrow':'\u2193','ShortLeftArrow':'\u2190','shortmid':'\u2223','shortparallel':'\u2225','ShortRightArrow':'\u2192','ShortUpArrow':'\u2191','shy':'\xAD','sigma':'\u03C3','Sigma':'\u03A3','sigmaf':'\u03C2','sigmav':'\u03C2','sim':'\u223C','simdot':'\u2A6A','sime':'\u2243','simeq':'\u2243','simg':'\u2A9E','simgE':'\u2AA0','siml':'\u2A9D','simlE':'\u2A9F','simne':'\u2246','simplus':'\u2A24','simrarr':'\u2972','slarr':'\u2190','SmallCircle':'\u2218','smallsetminus':'\u2216','smashp':'\u2A33','smeparsl':'\u29E4','smid':'\u2223','smile':'\u2323','smt':'\u2AAA','smte':'\u2AAC','smtes':'\u2AAC\uFE00','softcy':'\u044C','SOFTcy':'\u042C','sol':'/','solb':'\u29C4','solbar':'\u233F','sopf':'\uD835\uDD64','Sopf':'\uD835\uDD4A','spades':'\u2660','spadesuit':'\u2660','spar':'\u2225','sqcap':'\u2293','sqcaps':'\u2293\uFE00','sqcup':'\u2294','sqcups':'\u2294\uFE00','Sqrt':'\u221A','sqsub':'\u228F','sqsube':'\u2291','sqsubset':'\u228F','sqsubseteq':'\u2291','sqsup':'\u2290','sqsupe':'\u2292','sqsupset':'\u2290','sqsupseteq':'\u2292','squ':'\u25A1','square':'\u25A1','Square':'\u25A1','SquareIntersection':'\u2293','SquareSubset':'\u228F','SquareSubsetEqual':'\u2291','SquareSuperset':'\u2290','SquareSupersetEqual':'\u2292','SquareUnion':'\u2294','squarf':'\u25AA','squf':'\u25AA','srarr':'\u2192','sscr':'\uD835\uDCC8','Sscr':'\uD835\uDCAE','ssetmn':'\u2216','ssmile':'\u2323','sstarf':'\u22C6','star':'\u2606','Star':'\u22C6','starf':'\u2605','straightepsilon':'\u03F5','straightphi':'\u03D5','strns':'\xAF','sub':'\u2282','Sub':'\u22D0','subdot':'\u2ABD','sube':'\u2286','subE':'\u2AC5','subedot':'\u2AC3','submult':'\u2AC1','subne':'\u228A','subnE':'\u2ACB','subplus':'\u2ABF','subrarr':'\u2979','subset':'\u2282','Subset':'\u22D0','subseteq':'\u2286','subseteqq':'\u2AC5','SubsetEqual':'\u2286','subsetneq':'\u228A','subsetneqq':'\u2ACB','subsim':'\u2AC7','subsub':'\u2AD5','subsup':'\u2AD3','succ':'\u227B','succapprox':'\u2AB8','succcurlyeq':'\u227D','Succeeds':'\u227B','SucceedsEqual':'\u2AB0','SucceedsSlantEqual':'\u227D','SucceedsTilde':'\u227F','succeq':'\u2AB0','succnapprox':'\u2ABA','succneqq':'\u2AB6','succnsim':'\u22E9','succsim':'\u227F','SuchThat':'\u220B','sum':'\u2211','Sum':'\u2211','sung':'\u266A','sup':'\u2283','Sup':'\u22D1','sup1':'\xB9','sup2':'\xB2','sup3':'\xB3','supdot':'\u2ABE','supdsub':'\u2AD8','supe':'\u2287','supE':'\u2AC6','supedot':'\u2AC4','Superset':'\u2283','SupersetEqual':'\u2287','suphsol':'\u27C9','suphsub':'\u2AD7','suplarr':'\u297B','supmult':'\u2AC2','supne':'\u228B','supnE':'\u2ACC','supplus':'\u2AC0','supset':'\u2283','Supset':'\u22D1','supseteq':'\u2287','supseteqq':'\u2AC6','supsetneq':'\u228B','supsetneqq':'\u2ACC','supsim':'\u2AC8','supsub':'\u2AD4','supsup':'\u2AD6','swarhk':'\u2926','swarr':'\u2199','swArr':'\u21D9','swarrow':'\u2199','swnwar':'\u292A','szlig':'\xDF','Tab':'\t','target':'\u2316','tau':'\u03C4','Tau':'\u03A4','tbrk':'\u23B4','tcaron':'\u0165','Tcaron':'\u0164','tcedil':'\u0163','Tcedil':'\u0162','tcy':'\u0442','Tcy':'\u0422','tdot':'\u20DB','telrec':'\u2315','tfr':'\uD835\uDD31','Tfr':'\uD835\uDD17','there4':'\u2234','therefore':'\u2234','Therefore':'\u2234','theta':'\u03B8','Theta':'\u0398','thetasym':'\u03D1','thetav':'\u03D1','thickapprox':'\u2248','thicksim':'\u223C','ThickSpace':'\u205F\u200A','thinsp':'\u2009','ThinSpace':'\u2009','thkap':'\u2248','thksim':'\u223C','thorn':'\xFE','THORN':'\xDE','tilde':'\u02DC','Tilde':'\u223C','TildeEqual':'\u2243','TildeFullEqual':'\u2245','TildeTilde':'\u2248','times':'\xD7','timesb':'\u22A0','timesbar':'\u2A31','timesd':'\u2A30','tint':'\u222D','toea':'\u2928','top':'\u22A4','topbot':'\u2336','topcir':'\u2AF1','topf':'\uD835\uDD65','Topf':'\uD835\uDD4B','topfork':'\u2ADA','tosa':'\u2929','tprime':'\u2034','trade':'\u2122','TRADE':'\u2122','triangle':'\u25B5','triangledown':'\u25BF','triangleleft':'\u25C3','trianglelefteq':'\u22B4','triangleq':'\u225C','triangleright':'\u25B9','trianglerighteq':'\u22B5','tridot':'\u25EC','trie':'\u225C','triminus':'\u2A3A','TripleDot':'\u20DB','triplus':'\u2A39','trisb':'\u29CD','tritime':'\u2A3B','trpezium':'\u23E2','tscr':'\uD835\uDCC9','Tscr':'\uD835\uDCAF','tscy':'\u0446','TScy':'\u0426','tshcy':'\u045B','TSHcy':'\u040B','tstrok':'\u0167','Tstrok':'\u0166','twixt':'\u226C','twoheadleftarrow':'\u219E','twoheadrightarrow':'\u21A0','uacute':'\xFA','Uacute':'\xDA','uarr':'\u2191','uArr':'\u21D1','Uarr':'\u219F','Uarrocir':'\u2949','ubrcy':'\u045E','Ubrcy':'\u040E','ubreve':'\u016D','Ubreve':'\u016C','ucirc':'\xFB','Ucirc':'\xDB','ucy':'\u0443','Ucy':'\u0423','udarr':'\u21C5','udblac':'\u0171','Udblac':'\u0170','udhar':'\u296E','ufisht':'\u297E','ufr':'\uD835\uDD32','Ufr':'\uD835\uDD18','ugrave':'\xF9','Ugrave':'\xD9','uHar':'\u2963','uharl':'\u21BF','uharr':'\u21BE','uhblk':'\u2580','ulcorn':'\u231C','ulcorner':'\u231C','ulcrop':'\u230F','ultri':'\u25F8','umacr':'\u016B','Umacr':'\u016A','uml':'\xA8','UnderBar':'_','UnderBrace':'\u23DF','UnderBracket':'\u23B5','UnderParenthesis':'\u23DD','Union':'\u22C3','UnionPlus':'\u228E','uogon':'\u0173','Uogon':'\u0172','uopf':'\uD835\uDD66','Uopf':'\uD835\uDD4C','uparrow':'\u2191','Uparrow':'\u21D1','UpArrow':'\u2191','UpArrowBar':'\u2912','UpArrowDownArrow':'\u21C5','updownarrow':'\u2195','Updownarrow':'\u21D5','UpDownArrow':'\u2195','UpEquilibrium':'\u296E','upharpoonleft':'\u21BF','upharpoonright':'\u21BE','uplus':'\u228E','UpperLeftArrow':'\u2196','UpperRightArrow':'\u2197','upsi':'\u03C5','Upsi':'\u03D2','upsih':'\u03D2','upsilon':'\u03C5','Upsilon':'\u03A5','UpTee':'\u22A5','UpTeeArrow':'\u21A5','upuparrows':'\u21C8','urcorn':'\u231D','urcorner':'\u231D','urcrop':'\u230E','uring':'\u016F','Uring':'\u016E','urtri':'\u25F9','uscr':'\uD835\uDCCA','Uscr':'\uD835\uDCB0','utdot':'\u22F0','utilde':'\u0169','Utilde':'\u0168','utri':'\u25B5','utrif':'\u25B4','uuarr':'\u21C8','uuml':'\xFC','Uuml':'\xDC','uwangle':'\u29A7','vangrt':'\u299C','varepsilon':'\u03F5','varkappa':'\u03F0','varnothing':'\u2205','varphi':'\u03D5','varpi':'\u03D6','varpropto':'\u221D','varr':'\u2195','vArr':'\u21D5','varrho':'\u03F1','varsigma':'\u03C2','varsubsetneq':'\u228A\uFE00','varsubsetneqq':'\u2ACB\uFE00','varsupsetneq':'\u228B\uFE00','varsupsetneqq':'\u2ACC\uFE00','vartheta':'\u03D1','vartriangleleft':'\u22B2','vartriangleright':'\u22B3','vBar':'\u2AE8','Vbar':'\u2AEB','vBarv':'\u2AE9','vcy':'\u0432','Vcy':'\u0412','vdash':'\u22A2','vDash':'\u22A8','Vdash':'\u22A9','VDash':'\u22AB','Vdashl':'\u2AE6','vee':'\u2228','Vee':'\u22C1','veebar':'\u22BB','veeeq':'\u225A','vellip':'\u22EE','verbar':'|','Verbar':'\u2016','vert':'|','Vert':'\u2016','VerticalBar':'\u2223','VerticalLine':'|','VerticalSeparator':'\u2758','VerticalTilde':'\u2240','VeryThinSpace':'\u200A','vfr':'\uD835\uDD33','Vfr':'\uD835\uDD19','vltri':'\u22B2','vnsub':'\u2282\u20D2','vnsup':'\u2283\u20D2','vopf':'\uD835\uDD67','Vopf':'\uD835\uDD4D','vprop':'\u221D','vrtri':'\u22B3','vscr':'\uD835\uDCCB','Vscr':'\uD835\uDCB1','vsubne':'\u228A\uFE00','vsubnE':'\u2ACB\uFE00','vsupne':'\u228B\uFE00','vsupnE':'\u2ACC\uFE00','Vvdash':'\u22AA','vzigzag':'\u299A','wcirc':'\u0175','Wcirc':'\u0174','wedbar':'\u2A5F','wedge':'\u2227','Wedge':'\u22C0','wedgeq':'\u2259','weierp':'\u2118','wfr':'\uD835\uDD34','Wfr':'\uD835\uDD1A','wopf':'\uD835\uDD68','Wopf':'\uD835\uDD4E','wp':'\u2118','wr':'\u2240','wreath':'\u2240','wscr':'\uD835\uDCCC','Wscr':'\uD835\uDCB2','xcap':'\u22C2','xcirc':'\u25EF','xcup':'\u22C3','xdtri':'\u25BD','xfr':'\uD835\uDD35','Xfr':'\uD835\uDD1B','xharr':'\u27F7','xhArr':'\u27FA','xi':'\u03BE','Xi':'\u039E','xlarr':'\u27F5','xlArr':'\u27F8','xmap':'\u27FC','xnis':'\u22FB','xodot':'\u2A00','xopf':'\uD835\uDD69','Xopf':'\uD835\uDD4F','xoplus':'\u2A01','xotime':'\u2A02','xrarr':'\u27F6','xrArr':'\u27F9','xscr':'\uD835\uDCCD','Xscr':'\uD835\uDCB3','xsqcup':'\u2A06','xuplus':'\u2A04','xutri':'\u25B3','xvee':'\u22C1','xwedge':'\u22C0','yacute':'\xFD','Yacute':'\xDD','yacy':'\u044F','YAcy':'\u042F','ycirc':'\u0177','Ycirc':'\u0176','ycy':'\u044B','Ycy':'\u042B','yen':'\xA5','yfr':'\uD835\uDD36','Yfr':'\uD835\uDD1C','yicy':'\u0457','YIcy':'\u0407','yopf':'\uD835\uDD6A','Yopf':'\uD835\uDD50','yscr':'\uD835\uDCCE','Yscr':'\uD835\uDCB4','yucy':'\u044E','YUcy':'\u042E','yuml':'\xFF','Yuml':'\u0178','zacute':'\u017A','Zacute':'\u0179','zcaron':'\u017E','Zcaron':'\u017D','zcy':'\u0437','Zcy':'\u0417','zdot':'\u017C','Zdot':'\u017B','zeetrf':'\u2128','ZeroWidthSpace':'\u200B','zeta':'\u03B6','Zeta':'\u0396','zfr':'\uD835\uDD37','Zfr':'\u2128','zhcy':'\u0436','ZHcy':'\u0416','zigrarr':'\u21DD','zopf':'\uD835\uDD6B','Zopf':'\u2124','zscr':'\uD835\uDCCF','Zscr':'\uD835\uDCB5','zwj':'\u200D','zwnj':'\u200C'};
	var decodeMapLegacy = {'aacute':'\xE1','Aacute':'\xC1','acirc':'\xE2','Acirc':'\xC2','acute':'\xB4','aelig':'\xE6','AElig':'\xC6','agrave':'\xE0','Agrave':'\xC0','amp':'&','AMP':'&','aring':'\xE5','Aring':'\xC5','atilde':'\xE3','Atilde':'\xC3','auml':'\xE4','Auml':'\xC4','brvbar':'\xA6','ccedil':'\xE7','Ccedil':'\xC7','cedil':'\xB8','cent':'\xA2','copy':'\xA9','COPY':'\xA9','curren':'\xA4','deg':'\xB0','divide':'\xF7','eacute':'\xE9','Eacute':'\xC9','ecirc':'\xEA','Ecirc':'\xCA','egrave':'\xE8','Egrave':'\xC8','eth':'\xF0','ETH':'\xD0','euml':'\xEB','Euml':'\xCB','frac12':'\xBD','frac14':'\xBC','frac34':'\xBE','gt':'>','GT':'>','iacute':'\xED','Iacute':'\xCD','icirc':'\xEE','Icirc':'\xCE','iexcl':'\xA1','igrave':'\xEC','Igrave':'\xCC','iquest':'\xBF','iuml':'\xEF','Iuml':'\xCF','laquo':'\xAB','lt':'<','LT':'<','macr':'\xAF','micro':'\xB5','middot':'\xB7','nbsp':'\xA0','not':'\xAC','ntilde':'\xF1','Ntilde':'\xD1','oacute':'\xF3','Oacute':'\xD3','ocirc':'\xF4','Ocirc':'\xD4','ograve':'\xF2','Ograve':'\xD2','ordf':'\xAA','ordm':'\xBA','oslash':'\xF8','Oslash':'\xD8','otilde':'\xF5','Otilde':'\xD5','ouml':'\xF6','Ouml':'\xD6','para':'\xB6','plusmn':'\xB1','pound':'\xA3','quot':'"','QUOT':'"','raquo':'\xBB','reg':'\xAE','REG':'\xAE','sect':'\xA7','shy':'\xAD','sup1':'\xB9','sup2':'\xB2','sup3':'\xB3','szlig':'\xDF','thorn':'\xFE','THORN':'\xDE','times':'\xD7','uacute':'\xFA','Uacute':'\xDA','ucirc':'\xFB','Ucirc':'\xDB','ugrave':'\xF9','Ugrave':'\xD9','uml':'\xA8','uuml':'\xFC','Uuml':'\xDC','yacute':'\xFD','Yacute':'\xDD','yen':'\xA5','yuml':'\xFF'};
	var decodeMapNumeric = {'0':'\uFFFD','128':'\u20AC','130':'\u201A','131':'\u0192','132':'\u201E','133':'\u2026','134':'\u2020','135':'\u2021','136':'\u02C6','137':'\u2030','138':'\u0160','139':'\u2039','140':'\u0152','142':'\u017D','145':'\u2018','146':'\u2019','147':'\u201C','148':'\u201D','149':'\u2022','150':'\u2013','151':'\u2014','152':'\u02DC','153':'\u2122','154':'\u0161','155':'\u203A','156':'\u0153','158':'\u017E','159':'\u0178'};
	var invalidReferenceCodePoints = [1,2,3,4,5,6,7,8,11,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,64976,64977,64978,64979,64980,64981,64982,64983,64984,64985,64986,64987,64988,64989,64990,64991,64992,64993,64994,64995,64996,64997,64998,64999,65000,65001,65002,65003,65004,65005,65006,65007,65534,65535,131070,131071,196606,196607,262142,262143,327678,327679,393214,393215,458750,458751,524286,524287,589822,589823,655358,655359,720894,720895,786430,786431,851966,851967,917502,917503,983038,983039,1048574,1048575,1114110,1114111];

	/*--------------------------------------------------------------------------*/

	var stringFromCharCode = String.fromCharCode;

	var object = {};
	var hasOwnProperty = object.hasOwnProperty;
	var has = function(object, propertyName) {
		return hasOwnProperty.call(object, propertyName);
	};

	var contains = function(array, value) {
		var index = -1;
		var length = array.length;
		while (++index < length) {
			if (array[index] == value) {
				return true;
			}
		}
		return false;
	};

	var merge = function(options, defaults) {
		if (!options) {
			return defaults;
		}
		var result = {};
		var key;
		for (key in defaults) {
			// A `hasOwnProperty` check is not needed here, since only recognized
			// option names are used anyway. Any others are ignored.
			result[key] = has(options, key) ? options[key] : defaults[key];
		}
		return result;
	};

	// Modified version of `ucs2encode`; see https://mths.be/punycode.
	var codePointToSymbol = function(codePoint, strict) {
		var output = '';
		if ((codePoint >= 0xD800 && codePoint <= 0xDFFF) || codePoint > 0x10FFFF) {
			// See issue #4:
			// â€œOtherwise, if the number is in the range 0xD800 to 0xDFFF or is
			// greater than 0x10FFFF, then this is a parse error. Return a U+FFFD
			// REPLACEMENT CHARACTER.â€
			if (strict) {
				parseError('character reference outside the permissible Unicode range');
			}
			return '\uFFFD';
		}
		if (has(decodeMapNumeric, codePoint)) {
			if (strict) {
				parseError('disallowed character reference');
			}
			return decodeMapNumeric[codePoint];
		}
		if (strict && contains(invalidReferenceCodePoints, codePoint)) {
			parseError('disallowed character reference');
		}
		if (codePoint > 0xFFFF) {
			codePoint -= 0x10000;
			output += stringFromCharCode(codePoint >>> 10 & 0x3FF | 0xD800);
			codePoint = 0xDC00 | codePoint & 0x3FF;
		}
		output += stringFromCharCode(codePoint);
		return output;
	};

	var hexEscape = function(codePoint) {
		return '&#x' + codePoint.toString(16).toUpperCase() + ';';
	};

	var decEscape = function(codePoint) {
		return '&#' + codePoint + ';';
	};

	var parseError = function(message) {
		throw Error('Parse error: ' + message);
	};

	/*--------------------------------------------------------------------------*/

	var encode = function(string, options) {
		options = merge(options, encode.options);
		var strict = options.strict;
		if (strict && regexInvalidRawCodePoint.test(string)) {
			parseError('forbidden code point');
		}
		var encodeEverything = options.encodeEverything;
		var useNamedReferences = options.useNamedReferences;
		var allowUnsafeSymbols = options.allowUnsafeSymbols;
		var escapeCodePoint = options.decimal ? decEscape : hexEscape;

		var escapeBmpSymbol = function(symbol) {
			return escapeCodePoint(symbol.charCodeAt(0));
		};

		if (encodeEverything) {
			// Encode ASCII symbols.
			string = string.replace(regexAsciiWhitelist, function(symbol) {
				// Use named references if requested & possible.
				if (useNamedReferences && has(encodeMap, symbol)) {
					return '&' + encodeMap[symbol] + ';';
				}
				return escapeBmpSymbol(symbol);
			});
			// Shorten a few escapes that represent two symbols, of which at least one
			// is within the ASCII range.
			if (useNamedReferences) {
				string = string
					.replace(/&gt;\u20D2/g, '&nvgt;')
					.replace(/&lt;\u20D2/g, '&nvlt;')
					.replace(/&#x66;&#x6A;/g, '&fjlig;');
			}
			// Encode non-ASCII symbols.
			if (useNamedReferences) {
				// Encode non-ASCII symbols that can be replaced with a named reference.
				string = string.replace(regexEncodeNonAscii, function(string) {
					// Note: there is no need to check `has(encodeMap, string)` here.
					return '&' + encodeMap[string] + ';';
				});
			}
			// Note: any remaining non-ASCII symbols are handled outside of the `if`.
		} else if (useNamedReferences) {
			// Apply named character references.
			// Encode `<>"'&` using named character references.
			if (!allowUnsafeSymbols) {
				string = string.replace(regexEscape, function(string) {
					return '&' + encodeMap[string] + ';'; // no need to check `has()` here
				});
			}
			// Shorten escapes that represent two symbols, of which at least one is
			// `<>"'&`.
			string = string
				.replace(/&gt;\u20D2/g, '&nvgt;')
				.replace(/&lt;\u20D2/g, '&nvlt;');
			// Encode non-ASCII symbols that can be replaced with a named reference.
			string = string.replace(regexEncodeNonAscii, function(string) {
				// Note: there is no need to check `has(encodeMap, string)` here.
				return '&' + encodeMap[string] + ';';
			});
		} else if (!allowUnsafeSymbols) {
			// Encode `<>"'&` using hexadecimal escapes, now that theyâ€™re not handled
			// using named character references.
			string = string.replace(regexEscape, escapeBmpSymbol);
		}
		return string
			// Encode astral symbols.
			.replace(regexAstralSymbols, function($0) {
				// https://mathiasbynens.be/notes/javascript-encoding#surrogate-formulae
				var high = $0.charCodeAt(0);
				var low = $0.charCodeAt(1);
				var codePoint = (high - 0xD800) * 0x400 + low - 0xDC00 + 0x10000;
				return escapeCodePoint(codePoint);
			})
			// Encode any remaining BMP symbols that are not printable ASCII symbols
			// using a hexadecimal escape.
			.replace(regexBmpWhitelist, escapeBmpSymbol);
	};
	// Expose default options (so they can be overridden globally).
	encode.options = {
		'allowUnsafeSymbols': false,
		'encodeEverything': false,
		'strict': false,
		'useNamedReferences': false,
		'decimal' : false
	};

	var decode = function(html, options) {
		options = merge(options, decode.options);
		var strict = options.strict;
		if (strict && regexInvalidEntity.test(html)) {
			parseError('malformed character reference');
		}
		return html.replace(regexDecode, function($0, $1, $2, $3, $4, $5, $6, $7, $8) {
			var codePoint;
			var semicolon;
			var decDigits;
			var hexDigits;
			var reference;
			var next;

			if ($1) {
				reference = $1;
				// Note: there is no need to check `has(decodeMap, reference)`.
				return decodeMap[reference];
			}

			if ($2) {
				// Decode named character references without trailing `;`, e.g. `&amp`.
				// This is only a parse error if it gets converted to `&`, or if it is
				// followed by `=` in an attribute context.
				reference = $2;
				next = $3;
				if (next && options.isAttributeValue) {
					if (strict && next == '=') {
						parseError('`&` did not start a character reference');
					}
					return $0;
				} else {
					if (strict) {
						parseError(
							'named character reference was not terminated by a semicolon'
						);
					}
					// Note: there is no need to check `has(decodeMapLegacy, reference)`.
					return decodeMapLegacy[reference] + (next || '');
				}
			}

			if ($4) {
				// Decode decimal escapes, e.g. `&#119558;`.
				decDigits = $4;
				semicolon = $5;
				if (strict && !semicolon) {
					parseError('character reference was not terminated by a semicolon');
				}
				codePoint = parseInt(decDigits, 10);
				return codePointToSymbol(codePoint, strict);
			}

			if ($6) {
				// Decode hexadecimal escapes, e.g. `&#x1D306;`.
				hexDigits = $6;
				semicolon = $7;
				if (strict && !semicolon) {
					parseError('character reference was not terminated by a semicolon');
				}
				codePoint = parseInt(hexDigits, 16);
				return codePointToSymbol(codePoint, strict);
			}

			// If weâ€™re still here, `if ($7)` is implied; itâ€™s an ambiguous
			// ampersand for sure. https://mths.be/notes/ambiguous-ampersands
			if (strict) {
				parseError(
					'named character reference was not terminated by a semicolon'
				);
			}
			return $0;
		});
	};
	// Expose default options (so they can be overridden globally).
	decode.options = {
		'isAttributeValue': false,
		'strict': false
	};

	var escape = function(string) {
		return string.replace(regexEscape, function($0) {
			// Note: there is no need to check `has(escapeMap, $0)` here.
			return escapeMap[$0];
		});
	};

	/*--------------------------------------------------------------------------*/

	var he = {
		'version': '1.2.0',
		'encode': encode,
		'decode': decode,
		'escape': escape,
		'unescape': decode
	};

	// Some AMD build optimizers, like r.js, check for specific condition patterns
	// like the following:
	if (
		typeof define == 'function' &&
		typeof define.amd == 'object' &&
		define.amd
	) {
		define(function() {
			return he;
		});
	}	else if (freeExports && !freeExports.nodeType) {
		if (freeModule) { // in Node.js, io.js, or RingoJS v0.8.0+
			freeModule.exports = he;
		} else { // in Narwhal or RingoJS v0.7.0-
			for (var key in he) {
				has(he, key) && (freeExports[key] = he[key]);
			}
		}
	} else { // in Rhino or a web browser
		root.he = he;
	}

}(this));

}).call(this)}).call(this,typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : {})

},{}],54:[function(require,module,exports){
var slice = Array.prototype.slice;

// our constructor
function KeyTreeStore(options) {
    options = options || {};
    if (typeof options !== 'object') {
        throw new TypeError('Options must be an object');
    }
    var DEFAULT_SEPARATOR = '.';

    this.storage = {};
    this.separator = options.separator || DEFAULT_SEPARATOR;
}

// add an object to the store
KeyTreeStore.prototype.add = function (keypath, obj) {
    var arr = this.storage[keypath] || (this.storage[keypath] = []);
    arr.push(obj);
};

// remove an object
KeyTreeStore.prototype.remove = function (obj) {
    var path, arr;
    for (path in this.storage) {
        arr = this.storage[path];
        arr.some(function (item, index) {
            if (item === obj) {
                arr.splice(index, 1);
                return true;
            }
        });
    }
};

// get array of all all relevant functions, without keys
KeyTreeStore.prototype.get = function (keypath) {
    var res = [];
    var key;

    for (key in this.storage) {
        if (!keypath || keypath === key || key.indexOf(keypath + this.separator) === 0) {
            res = res.concat(this.storage[key]);
        }
    }

    return res;
};

// get all results that match keypath but still grouped by key
KeyTreeStore.prototype.getGrouped = function (keypath) {
    var res = {};
    var key;

    for (key in this.storage) {
        if (!keypath || keypath === key || key.indexOf(keypath + this.separator) === 0) {
            res[key] = slice.call(this.storage[key]);
        }
    }

    return res;
};

// get all results that match keypath but still grouped by key
KeyTreeStore.prototype.getAll = function (keypath) {
    var res = {};
    var key;

    for (key in this.storage) {
        if (keypath === key || key.indexOf(keypath + this.separator) === 0) {
            res[key] = slice.call(this.storage[key]);
        }
    }

    return res;
};

// run all matches with optional context
KeyTreeStore.prototype.run = function (keypath, context) {
    var args = slice.call(arguments, 2);
    this.get(keypath).forEach(function (fn) {
        fn.apply(context || this, args);
    });
};

module.exports = KeyTreeStore;

},{}],55:[function(require,module,exports){
var getNative = require('./_getNative'),
    root = require('./_root');

/* Built-in method references that are verified to be native. */
var DataView = getNative(root, 'DataView');

module.exports = DataView;

},{"./_getNative":174,"./_root":227}],56:[function(require,module,exports){
var hashClear = require('./_hashClear'),
    hashDelete = require('./_hashDelete'),
    hashGet = require('./_hashGet'),
    hashHas = require('./_hashHas'),
    hashSet = require('./_hashSet');

/**
 * Creates a hash object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function Hash(entries) {
  var index = -1,
      length = entries == null ? 0 : entries.length;

  this.clear();
  while (++index < length) {
    var entry = entries[index];
    this.set(entry[0], entry[1]);
  }
}

// Add methods to `Hash`.
Hash.prototype.clear = hashClear;
Hash.prototype['delete'] = hashDelete;
Hash.prototype.get = hashGet;
Hash.prototype.has = hashHas;
Hash.prototype.set = hashSet;

module.exports = Hash;

},{"./_hashClear":183,"./_hashDelete":184,"./_hashGet":185,"./_hashHas":186,"./_hashSet":187}],57:[function(require,module,exports){
var baseCreate = require('./_baseCreate'),
    baseLodash = require('./_baseLodash');

/** Used as references for the maximum length and index of an array. */
var MAX_ARRAY_LENGTH = 4294967295;

/**
 * Creates a lazy wrapper object which wraps `value` to enable lazy evaluation.
 *
 * @private
 * @constructor
 * @param {*} value The value to wrap.
 */
function LazyWrapper(value) {
  this.__wrapped__ = value;
  this.__actions__ = [];
  this.__dir__ = 1;
  this.__filtered__ = false;
  this.__iteratees__ = [];
  this.__takeCount__ = MAX_ARRAY_LENGTH;
  this.__views__ = [];
}

// Ensure `LazyWrapper` is an instance of `baseLodash`.
LazyWrapper.prototype = baseCreate(baseLodash.prototype);
LazyWrapper.prototype.constructor = LazyWrapper;

module.exports = LazyWrapper;

},{"./_baseCreate":85,"./_baseLodash":110}],58:[function(require,module,exports){
var listCacheClear = require('./_listCacheClear'),
    listCacheDelete = require('./_listCacheDelete'),
    listCacheGet = require('./_listCacheGet'),
    listCacheHas = require('./_listCacheHas'),
    listCacheSet = require('./_listCacheSet');

/**
 * Creates an list cache object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function ListCache(entries) {
  var index = -1,
      length = entries == null ? 0 : entries.length;

  this.clear();
  while (++index < length) {
    var entry = entries[index];
    this.set(entry[0], entry[1]);
  }
}

// Add methods to `ListCache`.
ListCache.prototype.clear = listCacheClear;
ListCache.prototype['delete'] = listCacheDelete;
ListCache.prototype.get = listCacheGet;
ListCache.prototype.has = listCacheHas;
ListCache.prototype.set = listCacheSet;

module.exports = ListCache;

},{"./_listCacheClear":201,"./_listCacheDelete":202,"./_listCacheGet":203,"./_listCacheHas":204,"./_listCacheSet":205}],59:[function(require,module,exports){
var baseCreate = require('./_baseCreate'),
    baseLodash = require('./_baseLodash');

/**
 * The base constructor for creating `lodash` wrapper objects.
 *
 * @private
 * @param {*} value The value to wrap.
 * @param {boolean} [chainAll] Enable explicit method chain sequences.
 */
function LodashWrapper(value, chainAll) {
  this.__wrapped__ = value;
  this.__actions__ = [];
  this.__chain__ = !!chainAll;
  this.__index__ = 0;
  this.__values__ = undefined;
}

LodashWrapper.prototype = baseCreate(baseLodash.prototype);
LodashWrapper.prototype.constructor = LodashWrapper;

module.exports = LodashWrapper;

},{"./_baseCreate":85,"./_baseLodash":110}],60:[function(require,module,exports){
var getNative = require('./_getNative'),
    root = require('./_root');

/* Built-in method references that are verified to be native. */
var Map = getNative(root, 'Map');

module.exports = Map;

},{"./_getNative":174,"./_root":227}],61:[function(require,module,exports){
var mapCacheClear = require('./_mapCacheClear'),
    mapCacheDelete = require('./_mapCacheDelete'),
    mapCacheGet = require('./_mapCacheGet'),
    mapCacheHas = require('./_mapCacheHas'),
    mapCacheSet = require('./_mapCacheSet');

/**
 * Creates a map cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function MapCache(entries) {
  var index = -1,
      length = entries == null ? 0 : entries.length;

  this.clear();
  while (++index < length) {
    var entry = entries[index];
    this.set(entry[0], entry[1]);
  }
}

// Add methods to `MapCache`.
MapCache.prototype.clear = mapCacheClear;
MapCache.prototype['delete'] = mapCacheDelete;
MapCache.prototype.get = mapCacheGet;
MapCache.prototype.has = mapCacheHas;
MapCache.prototype.set = mapCacheSet;

module.exports = MapCache;

},{"./_mapCacheClear":206,"./_mapCacheDelete":207,"./_mapCacheGet":208,"./_mapCacheHas":209,"./_mapCacheSet":210}],62:[function(require,module,exports){
var getNative = require('./_getNative'),
    root = require('./_root');

/* Built-in method references that are verified to be native. */
var Promise = getNative(root, 'Promise');

module.exports = Promise;

},{"./_getNative":174,"./_root":227}],63:[function(require,module,exports){
var getNative = require('./_getNative'),
    root = require('./_root');

/* Built-in method references that are verified to be native. */
var Set = getNative(root, 'Set');

module.exports = Set;

},{"./_getNative":174,"./_root":227}],64:[function(require,module,exports){
var MapCache = require('./_MapCache'),
    setCacheAdd = require('./_setCacheAdd'),
    setCacheHas = require('./_setCacheHas');

/**
 *
 * Creates an array cache object to store unique values.
 *
 * @private
 * @constructor
 * @param {Array} [values] The values to cache.
 */
function SetCache(values) {
  var index = -1,
      length = values == null ? 0 : values.length;

  this.__data__ = new MapCache;
  while (++index < length) {
    this.add(values[index]);
  }
}

// Add methods to `SetCache`.
SetCache.prototype.add = SetCache.prototype.push = setCacheAdd;
SetCache.prototype.has = setCacheHas;

module.exports = SetCache;

},{"./_MapCache":61,"./_setCacheAdd":228,"./_setCacheHas":229}],65:[function(require,module,exports){
var ListCache = require('./_ListCache'),
    stackClear = require('./_stackClear'),
    stackDelete = require('./_stackDelete'),
    stackGet = require('./_stackGet'),
    stackHas = require('./_stackHas'),
    stackSet = require('./_stackSet');

/**
 * Creates a stack cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function Stack(entries) {
  var data = this.__data__ = new ListCache(entries);
  this.size = data.size;
}

// Add methods to `Stack`.
Stack.prototype.clear = stackClear;
Stack.prototype['delete'] = stackDelete;
Stack.prototype.get = stackGet;
Stack.prototype.has = stackHas;
Stack.prototype.set = stackSet;

module.exports = Stack;

},{"./_ListCache":58,"./_stackClear":235,"./_stackDelete":236,"./_stackGet":237,"./_stackHas":238,"./_stackSet":239}],66:[function(require,module,exports){
var root = require('./_root');

/** Built-in value references. */
var Symbol = root.Symbol;

module.exports = Symbol;

},{"./_root":227}],67:[function(require,module,exports){
var root = require('./_root');

/** Built-in value references. */
var Uint8Array = root.Uint8Array;

module.exports = Uint8Array;

},{"./_root":227}],68:[function(require,module,exports){
var getNative = require('./_getNative'),
    root = require('./_root');

/* Built-in method references that are verified to be native. */
var WeakMap = getNative(root, 'WeakMap');

module.exports = WeakMap;

},{"./_getNative":174,"./_root":227}],69:[function(require,module,exports){
/**
 * A faster alternative to `Function#apply`, this function invokes `func`
 * with the `this` binding of `thisArg` and the arguments of `args`.
 *
 * @private
 * @param {Function} func The function to invoke.
 * @param {*} thisArg The `this` binding of `func`.
 * @param {Array} args The arguments to invoke `func` with.
 * @returns {*} Returns the result of `func`.
 */
function apply(func, thisArg, args) {
  switch (args.length) {
    case 0: return func.call(thisArg);
    case 1: return func.call(thisArg, args[0]);
    case 2: return func.call(thisArg, args[0], args[1]);
    case 3: return func.call(thisArg, args[0], args[1], args[2]);
  }
  return func.apply(thisArg, args);
}

module.exports = apply;

},{}],70:[function(require,module,exports){
/**
 * A specialized version of `_.forEach` for arrays without support for
 * iteratee shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns `array`.
 */
function arrayEach(array, iteratee) {
  var index = -1,
      length = array == null ? 0 : array.length;

  while (++index < length) {
    if (iteratee(array[index], index, array) === false) {
      break;
    }
  }
  return array;
}

module.exports = arrayEach;

},{}],71:[function(require,module,exports){
/**
 * A specialized version of `_.filter` for arrays without support for
 * iteratee shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} predicate The function invoked per iteration.
 * @returns {Array} Returns the new filtered array.
 */
function arrayFilter(array, predicate) {
  var index = -1,
      length = array == null ? 0 : array.length,
      resIndex = 0,
      result = [];

  while (++index < length) {
    var value = array[index];
    if (predicate(value, index, array)) {
      result[resIndex++] = value;
    }
  }
  return result;
}

module.exports = arrayFilter;

},{}],72:[function(require,module,exports){
var baseIndexOf = require('./_baseIndexOf');

/**
 * A specialized version of `_.includes` for arrays without support for
 * specifying an index to search from.
 *
 * @private
 * @param {Array} [array] The array to inspect.
 * @param {*} target The value to search for.
 * @returns {boolean} Returns `true` if `target` is found, else `false`.
 */
function arrayIncludes(array, value) {
  var length = array == null ? 0 : array.length;
  return !!length && baseIndexOf(array, value, 0) > -1;
}

module.exports = arrayIncludes;

},{"./_baseIndexOf":97}],73:[function(require,module,exports){
/**
 * This function is like `arrayIncludes` except that it accepts a comparator.
 *
 * @private
 * @param {Array} [array] The array to inspect.
 * @param {*} target The value to search for.
 * @param {Function} comparator The comparator invoked per element.
 * @returns {boolean} Returns `true` if `target` is found, else `false`.
 */
function arrayIncludesWith(array, value, comparator) {
  var index = -1,
      length = array == null ? 0 : array.length;

  while (++index < length) {
    if (comparator(value, array[index])) {
      return true;
    }
  }
  return false;
}

module.exports = arrayIncludesWith;

},{}],74:[function(require,module,exports){
var baseTimes = require('./_baseTimes'),
    isArguments = require('./isArguments'),
    isArray = require('./isArray'),
    isBuffer = require('./isBuffer'),
    isIndex = require('./_isIndex'),
    isTypedArray = require('./isTypedArray');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Creates an array of the enumerable property names of the array-like `value`.
 *
 * @private
 * @param {*} value The value to query.
 * @param {boolean} inherited Specify returning inherited property names.
 * @returns {Array} Returns the array of property names.
 */
function arrayLikeKeys(value, inherited) {
  var isArr = isArray(value),
      isArg = !isArr && isArguments(value),
      isBuff = !isArr && !isArg && isBuffer(value),
      isType = !isArr && !isArg && !isBuff && isTypedArray(value),
      skipIndexes = isArr || isArg || isBuff || isType,
      result = skipIndexes ? baseTimes(value.length, String) : [],
      length = result.length;

  for (var key in value) {
    if ((inherited || hasOwnProperty.call(value, key)) &&
        !(skipIndexes && (
           // Safari 9 has enumerable `arguments.length` in strict mode.
           key == 'length' ||
           // Node.js 0.10 has enumerable non-index properties on buffers.
           (isBuff && (key == 'offset' || key == 'parent')) ||
           // PhantomJS 2 has enumerable non-index properties on typed arrays.
           (isType && (key == 'buffer' || key == 'byteLength' || key == 'byteOffset')) ||
           // Skip index properties.
           isIndex(key, length)
        ))) {
      result.push(key);
    }
  }
  return result;
}

module.exports = arrayLikeKeys;

},{"./_baseTimes":125,"./_isIndex":193,"./isArguments":266,"./isArray":267,"./isBuffer":271,"./isTypedArray":282}],75:[function(require,module,exports){
/**
 * A specialized version of `_.map` for arrays without support for iteratee
 * shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the new mapped array.
 */
function arrayMap(array, iteratee) {
  var index = -1,
      length = array == null ? 0 : array.length,
      result = Array(length);

  while (++index < length) {
    result[index] = iteratee(array[index], index, array);
  }
  return result;
}

module.exports = arrayMap;

},{}],76:[function(require,module,exports){
/**
 * Appends the elements of `values` to `array`.
 *
 * @private
 * @param {Array} array The array to modify.
 * @param {Array} values The values to append.
 * @returns {Array} Returns `array`.
 */
function arrayPush(array, values) {
  var index = -1,
      length = values.length,
      offset = array.length;

  while (++index < length) {
    array[offset + index] = values[index];
  }
  return array;
}

module.exports = arrayPush;

},{}],77:[function(require,module,exports){
/**
 * A specialized version of `_.reduce` for arrays without support for
 * iteratee shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @param {*} [accumulator] The initial value.
 * @param {boolean} [initAccum] Specify using the first element of `array` as
 *  the initial value.
 * @returns {*} Returns the accumulated value.
 */
function arrayReduce(array, iteratee, accumulator, initAccum) {
  var index = -1,
      length = array == null ? 0 : array.length;

  if (initAccum && length) {
    accumulator = array[++index];
  }
  while (++index < length) {
    accumulator = iteratee(accumulator, array[index], index, array);
  }
  return accumulator;
}

module.exports = arrayReduce;

},{}],78:[function(require,module,exports){
/**
 * A specialized version of `_.some` for arrays without support for iteratee
 * shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} predicate The function invoked per iteration.
 * @returns {boolean} Returns `true` if any element passes the predicate check,
 *  else `false`.
 */
function arraySome(array, predicate) {
  var index = -1,
      length = array == null ? 0 : array.length;

  while (++index < length) {
    if (predicate(array[index], index, array)) {
      return true;
    }
  }
  return false;
}

module.exports = arraySome;

},{}],79:[function(require,module,exports){
var baseAssignValue = require('./_baseAssignValue'),
    eq = require('./eq');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Assigns `value` to `key` of `object` if the existing value is not equivalent
 * using [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * for equality comparisons.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */
function assignValue(object, key, value) {
  var objValue = object[key];
  if (!(hasOwnProperty.call(object, key) && eq(objValue, value)) ||
      (value === undefined && !(key in object))) {
    baseAssignValue(object, key, value);
  }
}

module.exports = assignValue;

},{"./_baseAssignValue":83,"./eq":254}],80:[function(require,module,exports){
var eq = require('./eq');

/**
 * Gets the index at which the `key` is found in `array` of key-value pairs.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} key The key to search for.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */
function assocIndexOf(array, key) {
  var length = array.length;
  while (length--) {
    if (eq(array[length][0], key)) {
      return length;
    }
  }
  return -1;
}

module.exports = assocIndexOf;

},{"./eq":254}],81:[function(require,module,exports){
var copyObject = require('./_copyObject'),
    keys = require('./keys');

/**
 * The base implementation of `_.assign` without support for multiple sources
 * or `customizer` functions.
 *
 * @private
 * @param {Object} object The destination object.
 * @param {Object} source The source object.
 * @returns {Object} Returns `object`.
 */
function baseAssign(object, source) {
  return object && copyObject(source, keys(source), object);
}

module.exports = baseAssign;

},{"./_copyObject":143,"./keys":283}],82:[function(require,module,exports){
var copyObject = require('./_copyObject'),
    keysIn = require('./keysIn');

/**
 * The base implementation of `_.assignIn` without support for multiple sources
 * or `customizer` functions.
 *
 * @private
 * @param {Object} object The destination object.
 * @param {Object} source The source object.
 * @returns {Object} Returns `object`.
 */
function baseAssignIn(object, source) {
  return object && copyObject(source, keysIn(source), object);
}

module.exports = baseAssignIn;

},{"./_copyObject":143,"./keysIn":284}],83:[function(require,module,exports){
var defineProperty = require('./_defineProperty');

/**
 * The base implementation of `assignValue` and `assignMergeValue` without
 * value checks.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */
function baseAssignValue(object, key, value) {
  if (key == '__proto__' && defineProperty) {
    defineProperty(object, key, {
      'configurable': true,
      'enumerable': true,
      'value': value,
      'writable': true
    });
  } else {
    object[key] = value;
  }
}

module.exports = baseAssignValue;

},{"./_defineProperty":161}],84:[function(require,module,exports){
var Stack = require('./_Stack'),
    arrayEach = require('./_arrayEach'),
    assignValue = require('./_assignValue'),
    baseAssign = require('./_baseAssign'),
    baseAssignIn = require('./_baseAssignIn'),
    cloneBuffer = require('./_cloneBuffer'),
    copyArray = require('./_copyArray'),
    copySymbols = require('./_copySymbols'),
    copySymbolsIn = require('./_copySymbolsIn'),
    getAllKeys = require('./_getAllKeys'),
    getAllKeysIn = require('./_getAllKeysIn'),
    getTag = require('./_getTag'),
    initCloneArray = require('./_initCloneArray'),
    initCloneByTag = require('./_initCloneByTag'),
    initCloneObject = require('./_initCloneObject'),
    isArray = require('./isArray'),
    isBuffer = require('./isBuffer'),
    isMap = require('./isMap'),
    isObject = require('./isObject'),
    isSet = require('./isSet'),
    keys = require('./keys'),
    keysIn = require('./keysIn');

/** Used to compose bitmasks for cloning. */
var CLONE_DEEP_FLAG = 1,
    CLONE_FLAT_FLAG = 2,
    CLONE_SYMBOLS_FLAG = 4;

/** `Object#toString` result references. */
var argsTag = '[object Arguments]',
    arrayTag = '[object Array]',
    boolTag = '[object Boolean]',
    dateTag = '[object Date]',
    errorTag = '[object Error]',
    funcTag = '[object Function]',
    genTag = '[object GeneratorFunction]',
    mapTag = '[object Map]',
    numberTag = '[object Number]',
    objectTag = '[object Object]',
    regexpTag = '[object RegExp]',
    setTag = '[object Set]',
    stringTag = '[object String]',
    symbolTag = '[object Symbol]',
    weakMapTag = '[object WeakMap]';

var arrayBufferTag = '[object ArrayBuffer]',
    dataViewTag = '[object DataView]',
    float32Tag = '[object Float32Array]',
    float64Tag = '[object Float64Array]',
    int8Tag = '[object Int8Array]',
    int16Tag = '[object Int16Array]',
    int32Tag = '[object Int32Array]',
    uint8Tag = '[object Uint8Array]',
    uint8ClampedTag = '[object Uint8ClampedArray]',
    uint16Tag = '[object Uint16Array]',
    uint32Tag = '[object Uint32Array]';

/** Used to identify `toStringTag` values supported by `_.clone`. */
var cloneableTags = {};
cloneableTags[argsTag] = cloneableTags[arrayTag] =
cloneableTags[arrayBufferTag] = cloneableTags[dataViewTag] =
cloneableTags[boolTag] = cloneableTags[dateTag] =
cloneableTags[float32Tag] = cloneableTags[float64Tag] =
cloneableTags[int8Tag] = cloneableTags[int16Tag] =
cloneableTags[int32Tag] = cloneableTags[mapTag] =
cloneableTags[numberTag] = cloneableTags[objectTag] =
cloneableTags[regexpTag] = cloneableTags[setTag] =
cloneableTags[stringTag] = cloneableTags[symbolTag] =
cloneableTags[uint8Tag] = cloneableTags[uint8ClampedTag] =
cloneableTags[uint16Tag] = cloneableTags[uint32Tag] = true;
cloneableTags[errorTag] = cloneableTags[funcTag] =
cloneableTags[weakMapTag] = false;

/**
 * The base implementation of `_.clone` and `_.cloneDeep` which tracks
 * traversed objects.
 *
 * @private
 * @param {*} value The value to clone.
 * @param {boolean} bitmask The bitmask flags.
 *  1 - Deep clone
 *  2 - Flatten inherited properties
 *  4 - Clone symbols
 * @param {Function} [customizer] The function to customize cloning.
 * @param {string} [key] The key of `value`.
 * @param {Object} [object] The parent object of `value`.
 * @param {Object} [stack] Tracks traversed objects and their clone counterparts.
 * @returns {*} Returns the cloned value.
 */
function baseClone(value, bitmask, customizer, key, object, stack) {
  var result,
      isDeep = bitmask & CLONE_DEEP_FLAG,
      isFlat = bitmask & CLONE_FLAT_FLAG,
      isFull = bitmask & CLONE_SYMBOLS_FLAG;

  if (customizer) {
    result = object ? customizer(value, key, object, stack) : customizer(value);
  }
  if (result !== undefined) {
    return result;
  }
  if (!isObject(value)) {
    return value;
  }
  var isArr = isArray(value);
  if (isArr) {
    result = initCloneArray(value);
    if (!isDeep) {
      return copyArray(value, result);
    }
  } else {
    var tag = getTag(value),
        isFunc = tag == funcTag || tag == genTag;

    if (isBuffer(value)) {
      return cloneBuffer(value, isDeep);
    }
    if (tag == objectTag || tag == argsTag || (isFunc && !object)) {
      result = (isFlat || isFunc) ? {} : initCloneObject(value);
      if (!isDeep) {
        return isFlat
          ? copySymbolsIn(value, baseAssignIn(result, value))
          : copySymbols(value, baseAssign(result, value));
      }
    } else {
      if (!cloneableTags[tag]) {
        return object ? value : {};
      }
      result = initCloneByTag(value, tag, isDeep);
    }
  }
  // Check for circular references and return its corresponding clone.
  stack || (stack = new Stack);
  var stacked = stack.get(value);
  if (stacked) {
    return stacked;
  }
  stack.set(value, result);

  if (isSet(value)) {
    value.forEach(function(subValue) {
      result.add(baseClone(subValue, bitmask, customizer, subValue, value, stack));
    });
  } else if (isMap(value)) {
    value.forEach(function(subValue, key) {
      result.set(key, baseClone(subValue, bitmask, customizer, key, value, stack));
    });
  }

  var keysFunc = isFull
    ? (isFlat ? getAllKeysIn : getAllKeys)
    : (isFlat ? keysIn : keys);

  var props = isArr ? undefined : keysFunc(value);
  arrayEach(props || value, function(subValue, key) {
    if (props) {
      key = subValue;
      subValue = value[key];
    }
    // Recursively populate clone (susceptible to call stack limits).
    assignValue(result, key, baseClone(subValue, bitmask, customizer, key, value, stack));
  });
  return result;
}

module.exports = baseClone;

},{"./_Stack":65,"./_arrayEach":70,"./_assignValue":79,"./_baseAssign":81,"./_baseAssignIn":82,"./_cloneBuffer":135,"./_copyArray":142,"./_copySymbols":144,"./_copySymbolsIn":145,"./_getAllKeys":167,"./_getAllKeysIn":168,"./_getTag":179,"./_initCloneArray":188,"./_initCloneByTag":189,"./_initCloneObject":190,"./isArray":267,"./isBuffer":271,"./isMap":276,"./isObject":277,"./isSet":280,"./keys":283,"./keysIn":284}],85:[function(require,module,exports){
var isObject = require('./isObject');

/** Built-in value references. */
var objectCreate = Object.create;

/**
 * The base implementation of `_.create` without support for assigning
 * properties to the created object.
 *
 * @private
 * @param {Object} proto The object to inherit from.
 * @returns {Object} Returns the new object.
 */
var baseCreate = (function() {
  function object() {}
  return function(proto) {
    if (!isObject(proto)) {
      return {};
    }
    if (objectCreate) {
      return objectCreate(proto);
    }
    object.prototype = proto;
    var result = new object;
    object.prototype = undefined;
    return result;
  };
}());

module.exports = baseCreate;

},{"./isObject":277}],86:[function(require,module,exports){
var SetCache = require('./_SetCache'),
    arrayIncludes = require('./_arrayIncludes'),
    arrayIncludesWith = require('./_arrayIncludesWith'),
    arrayMap = require('./_arrayMap'),
    baseUnary = require('./_baseUnary'),
    cacheHas = require('./_cacheHas');

/** Used as the size to enable large array optimizations. */
var LARGE_ARRAY_SIZE = 200;

/**
 * The base implementation of methods like `_.difference` without support
 * for excluding multiple arrays or iteratee shorthands.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {Array} values The values to exclude.
 * @param {Function} [iteratee] The iteratee invoked per element.
 * @param {Function} [comparator] The comparator invoked per element.
 * @returns {Array} Returns the new array of filtered values.
 */
function baseDifference(array, values, iteratee, comparator) {
  var index = -1,
      includes = arrayIncludes,
      isCommon = true,
      length = array.length,
      result = [],
      valuesLength = values.length;

  if (!length) {
    return result;
  }
  if (iteratee) {
    values = arrayMap(values, baseUnary(iteratee));
  }
  if (comparator) {
    includes = arrayIncludesWith;
    isCommon = false;
  }
  else if (values.length >= LARGE_ARRAY_SIZE) {
    includes = cacheHas;
    isCommon = false;
    values = new SetCache(values);
  }
  outer:
  while (++index < length) {
    var value = array[index],
        computed = iteratee == null ? value : iteratee(value);

    value = (comparator || value !== 0) ? value : 0;
    if (isCommon && computed === computed) {
      var valuesIndex = valuesLength;
      while (valuesIndex--) {
        if (values[valuesIndex] === computed) {
          continue outer;
        }
      }
      result.push(value);
    }
    else if (!includes(values, computed, comparator)) {
      result.push(value);
    }
  }
  return result;
}

module.exports = baseDifference;

},{"./_SetCache":64,"./_arrayIncludes":72,"./_arrayIncludesWith":73,"./_arrayMap":75,"./_baseUnary":128,"./_cacheHas":131}],87:[function(require,module,exports){
var baseForOwn = require('./_baseForOwn'),
    createBaseEach = require('./_createBaseEach');

/**
 * The base implementation of `_.forEach` without support for iteratee shorthands.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array|Object} Returns `collection`.
 */
var baseEach = createBaseEach(baseForOwn);

module.exports = baseEach;

},{"./_baseForOwn":92,"./_createBaseEach":149}],88:[function(require,module,exports){
var baseEach = require('./_baseEach');

/**
 * The base implementation of `_.filter` without support for iteratee shorthands.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} predicate The function invoked per iteration.
 * @returns {Array} Returns the new filtered array.
 */
function baseFilter(collection, predicate) {
  var result = [];
  baseEach(collection, function(value, index, collection) {
    if (predicate(value, index, collection)) {
      result.push(value);
    }
  });
  return result;
}

module.exports = baseFilter;

},{"./_baseEach":87}],89:[function(require,module,exports){
/**
 * The base implementation of `_.findIndex` and `_.findLastIndex` without
 * support for iteratee shorthands.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {Function} predicate The function invoked per iteration.
 * @param {number} fromIndex The index to search from.
 * @param {boolean} [fromRight] Specify iterating from right to left.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */
function baseFindIndex(array, predicate, fromIndex, fromRight) {
  var length = array.length,
      index = fromIndex + (fromRight ? 1 : -1);

  while ((fromRight ? index-- : ++index < length)) {
    if (predicate(array[index], index, array)) {
      return index;
    }
  }
  return -1;
}

module.exports = baseFindIndex;

},{}],90:[function(require,module,exports){
var arrayPush = require('./_arrayPush'),
    isFlattenable = require('./_isFlattenable');

/**
 * The base implementation of `_.flatten` with support for restricting flattening.
 *
 * @private
 * @param {Array} array The array to flatten.
 * @param {number} depth The maximum recursion depth.
 * @param {boolean} [predicate=isFlattenable] The function invoked per iteration.
 * @param {boolean} [isStrict] Restrict to values that pass `predicate` checks.
 * @param {Array} [result=[]] The initial result value.
 * @returns {Array} Returns the new flattened array.
 */
function baseFlatten(array, depth, predicate, isStrict, result) {
  var index = -1,
      length = array.length;

  predicate || (predicate = isFlattenable);
  result || (result = []);

  while (++index < length) {
    var value = array[index];
    if (depth > 0 && predicate(value)) {
      if (depth > 1) {
        // Recursively flatten arrays (susceptible to call stack limits).
        baseFlatten(value, depth - 1, predicate, isStrict, result);
      } else {
        arrayPush(result, value);
      }
    } else if (!isStrict) {
      result[result.length] = value;
    }
  }
  return result;
}

module.exports = baseFlatten;

},{"./_arrayPush":76,"./_isFlattenable":192}],91:[function(require,module,exports){
var createBaseFor = require('./_createBaseFor');

/**
 * The base implementation of `baseForOwn` which iterates over `object`
 * properties returned by `keysFunc` and invokes `iteratee` for each property.
 * Iteratee functions may exit iteration early by explicitly returning `false`.
 *
 * @private
 * @param {Object} object The object to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @param {Function} keysFunc The function to get the keys of `object`.
 * @returns {Object} Returns `object`.
 */
var baseFor = createBaseFor();

module.exports = baseFor;

},{"./_createBaseFor":150}],92:[function(require,module,exports){
var baseFor = require('./_baseFor'),
    keys = require('./keys');

/**
 * The base implementation of `_.forOwn` without support for iteratee shorthands.
 *
 * @private
 * @param {Object} object The object to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Object} Returns `object`.
 */
function baseForOwn(object, iteratee) {
  return object && baseFor(object, iteratee, keys);
}

module.exports = baseForOwn;

},{"./_baseFor":91,"./keys":283}],93:[function(require,module,exports){
var castPath = require('./_castPath'),
    toKey = require('./_toKey');

/**
 * The base implementation of `_.get` without support for default values.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to get.
 * @returns {*} Returns the resolved value.
 */
function baseGet(object, path) {
  path = castPath(path, object);

  var index = 0,
      length = path.length;

  while (object != null && index < length) {
    object = object[toKey(path[index++])];
  }
  return (index && index == length) ? object : undefined;
}

module.exports = baseGet;

},{"./_castPath":133,"./_toKey":242}],94:[function(require,module,exports){
var arrayPush = require('./_arrayPush'),
    isArray = require('./isArray');

/**
 * The base implementation of `getAllKeys` and `getAllKeysIn` which uses
 * `keysFunc` and `symbolsFunc` to get the enumerable property names and
 * symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Function} keysFunc The function to get the keys of `object`.
 * @param {Function} symbolsFunc The function to get the symbols of `object`.
 * @returns {Array} Returns the array of property names and symbols.
 */
function baseGetAllKeys(object, keysFunc, symbolsFunc) {
  var result = keysFunc(object);
  return isArray(object) ? result : arrayPush(result, symbolsFunc(object));
}

module.exports = baseGetAllKeys;

},{"./_arrayPush":76,"./isArray":267}],95:[function(require,module,exports){
var Symbol = require('./_Symbol'),
    getRawTag = require('./_getRawTag'),
    objectToString = require('./_objectToString');

/** `Object#toString` result references. */
var nullTag = '[object Null]',
    undefinedTag = '[object Undefined]';

/** Built-in value references. */
var symToStringTag = Symbol ? Symbol.toStringTag : undefined;

/**
 * The base implementation of `getTag` without fallbacks for buggy environments.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the `toStringTag`.
 */
function baseGetTag(value) {
  if (value == null) {
    return value === undefined ? undefinedTag : nullTag;
  }
  return (symToStringTag && symToStringTag in Object(value))
    ? getRawTag(value)
    : objectToString(value);
}

module.exports = baseGetTag;

},{"./_Symbol":66,"./_getRawTag":176,"./_objectToString":220}],96:[function(require,module,exports){
/**
 * The base implementation of `_.hasIn` without support for deep paths.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {Array|string} key The key to check.
 * @returns {boolean} Returns `true` if `key` exists, else `false`.
 */
function baseHasIn(object, key) {
  return object != null && key in Object(object);
}

module.exports = baseHasIn;

},{}],97:[function(require,module,exports){
var baseFindIndex = require('./_baseFindIndex'),
    baseIsNaN = require('./_baseIsNaN'),
    strictIndexOf = require('./_strictIndexOf');

/**
 * The base implementation of `_.indexOf` without `fromIndex` bounds checks.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} value The value to search for.
 * @param {number} fromIndex The index to search from.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */
function baseIndexOf(array, value, fromIndex) {
  return value === value
    ? strictIndexOf(array, value, fromIndex)
    : baseFindIndex(array, baseIsNaN, fromIndex);
}

module.exports = baseIndexOf;

},{"./_baseFindIndex":89,"./_baseIsNaN":103,"./_strictIndexOf":240}],98:[function(require,module,exports){
var baseGetTag = require('./_baseGetTag'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var argsTag = '[object Arguments]';

/**
 * The base implementation of `_.isArguments`.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 */
function baseIsArguments(value) {
  return isObjectLike(value) && baseGetTag(value) == argsTag;
}

module.exports = baseIsArguments;

},{"./_baseGetTag":95,"./isObjectLike":278}],99:[function(require,module,exports){
var baseIsEqualDeep = require('./_baseIsEqualDeep'),
    isObjectLike = require('./isObjectLike');

/**
 * The base implementation of `_.isEqual` which supports partial comparisons
 * and tracks traversed objects.
 *
 * @private
 * @param {*} value The value to compare.
 * @param {*} other The other value to compare.
 * @param {boolean} bitmask The bitmask flags.
 *  1 - Unordered comparison
 *  2 - Partial comparison
 * @param {Function} [customizer] The function to customize comparisons.
 * @param {Object} [stack] Tracks traversed `value` and `other` objects.
 * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
 */
function baseIsEqual(value, other, bitmask, customizer, stack) {
  if (value === other) {
    return true;
  }
  if (value == null || other == null || (!isObjectLike(value) && !isObjectLike(other))) {
    return value !== value && other !== other;
  }
  return baseIsEqualDeep(value, other, bitmask, customizer, baseIsEqual, stack);
}

module.exports = baseIsEqual;

},{"./_baseIsEqualDeep":100,"./isObjectLike":278}],100:[function(require,module,exports){
var Stack = require('./_Stack'),
    equalArrays = require('./_equalArrays'),
    equalByTag = require('./_equalByTag'),
    equalObjects = require('./_equalObjects'),
    getTag = require('./_getTag'),
    isArray = require('./isArray'),
    isBuffer = require('./isBuffer'),
    isTypedArray = require('./isTypedArray');

/** Used to compose bitmasks for value comparisons. */
var COMPARE_PARTIAL_FLAG = 1;

/** `Object#toString` result references. */
var argsTag = '[object Arguments]',
    arrayTag = '[object Array]',
    objectTag = '[object Object]';

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * A specialized version of `baseIsEqual` for arrays and objects which performs
 * deep comparisons and tracks traversed objects enabling objects with circular
 * references to be compared.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} [stack] Tracks traversed `object` and `other` objects.
 * @returns {boolean} Returns `true` if the objects are equivalent, else `false`.
 */
function baseIsEqualDeep(object, other, bitmask, customizer, equalFunc, stack) {
  var objIsArr = isArray(object),
      othIsArr = isArray(other),
      objTag = objIsArr ? arrayTag : getTag(object),
      othTag = othIsArr ? arrayTag : getTag(other);

  objTag = objTag == argsTag ? objectTag : objTag;
  othTag = othTag == argsTag ? objectTag : othTag;

  var objIsObj = objTag == objectTag,
      othIsObj = othTag == objectTag,
      isSameTag = objTag == othTag;

  if (isSameTag && isBuffer(object)) {
    if (!isBuffer(other)) {
      return false;
    }
    objIsArr = true;
    objIsObj = false;
  }
  if (isSameTag && !objIsObj) {
    stack || (stack = new Stack);
    return (objIsArr || isTypedArray(object))
      ? equalArrays(object, other, bitmask, customizer, equalFunc, stack)
      : equalByTag(object, other, objTag, bitmask, customizer, equalFunc, stack);
  }
  if (!(bitmask & COMPARE_PARTIAL_FLAG)) {
    var objIsWrapped = objIsObj && hasOwnProperty.call(object, '__wrapped__'),
        othIsWrapped = othIsObj && hasOwnProperty.call(other, '__wrapped__');

    if (objIsWrapped || othIsWrapped) {
      var objUnwrapped = objIsWrapped ? object.value() : object,
          othUnwrapped = othIsWrapped ? other.value() : other;

      stack || (stack = new Stack);
      return equalFunc(objUnwrapped, othUnwrapped, bitmask, customizer, stack);
    }
  }
  if (!isSameTag) {
    return false;
  }
  stack || (stack = new Stack);
  return equalObjects(object, other, bitmask, customizer, equalFunc, stack);
}

module.exports = baseIsEqualDeep;

},{"./_Stack":65,"./_equalArrays":162,"./_equalByTag":163,"./_equalObjects":164,"./_getTag":179,"./isArray":267,"./isBuffer":271,"./isTypedArray":282}],101:[function(require,module,exports){
var getTag = require('./_getTag'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var mapTag = '[object Map]';

/**
 * The base implementation of `_.isMap` without Node.js optimizations.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a map, else `false`.
 */
function baseIsMap(value) {
  return isObjectLike(value) && getTag(value) == mapTag;
}

module.exports = baseIsMap;

},{"./_getTag":179,"./isObjectLike":278}],102:[function(require,module,exports){
var Stack = require('./_Stack'),
    baseIsEqual = require('./_baseIsEqual');

/** Used to compose bitmasks for value comparisons. */
var COMPARE_PARTIAL_FLAG = 1,
    COMPARE_UNORDERED_FLAG = 2;

/**
 * The base implementation of `_.isMatch` without support for iteratee shorthands.
 *
 * @private
 * @param {Object} object The object to inspect.
 * @param {Object} source The object of property values to match.
 * @param {Array} matchData The property names, values, and compare flags to match.
 * @param {Function} [customizer] The function to customize comparisons.
 * @returns {boolean} Returns `true` if `object` is a match, else `false`.
 */
function baseIsMatch(object, source, matchData, customizer) {
  var index = matchData.length,
      length = index,
      noCustomizer = !customizer;

  if (object == null) {
    return !length;
  }
  object = Object(object);
  while (index--) {
    var data = matchData[index];
    if ((noCustomizer && data[2])
          ? data[1] !== object[data[0]]
          : !(data[0] in object)
        ) {
      return false;
    }
  }
  while (++index < length) {
    data = matchData[index];
    var key = data[0],
        objValue = object[key],
        srcValue = data[1];

    if (noCustomizer && data[2]) {
      if (objValue === undefined && !(key in object)) {
        return false;
      }
    } else {
      var stack = new Stack;
      if (customizer) {
        var result = customizer(objValue, srcValue, key, object, source, stack);
      }
      if (!(result === undefined
            ? baseIsEqual(srcValue, objValue, COMPARE_PARTIAL_FLAG | COMPARE_UNORDERED_FLAG, customizer, stack)
            : result
          )) {
        return false;
      }
    }
  }
  return true;
}

module.exports = baseIsMatch;

},{"./_Stack":65,"./_baseIsEqual":99}],103:[function(require,module,exports){
/**
 * The base implementation of `_.isNaN` without support for number objects.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is `NaN`, else `false`.
 */
function baseIsNaN(value) {
  return value !== value;
}

module.exports = baseIsNaN;

},{}],104:[function(require,module,exports){
var isFunction = require('./isFunction'),
    isMasked = require('./_isMasked'),
    isObject = require('./isObject'),
    toSource = require('./_toSource');

/**
 * Used to match `RegExp`
 * [syntax characters](http://ecma-international.org/ecma-262/7.0/#sec-patterns).
 */
var reRegExpChar = /[\\^$.*+?()[\]{}|]/g;

/** Used to detect host constructors (Safari). */
var reIsHostCtor = /^\[object .+?Constructor\]$/;

/** Used for built-in method references. */
var funcProto = Function.prototype,
    objectProto = Object.prototype;

/** Used to resolve the decompiled source of functions. */
var funcToString = funcProto.toString;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/** Used to detect if a method is native. */
var reIsNative = RegExp('^' +
  funcToString.call(hasOwnProperty).replace(reRegExpChar, '\\$&')
  .replace(/hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g, '$1.*?') + '$'
);

/**
 * The base implementation of `_.isNative` without bad shim checks.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a native function,
 *  else `false`.
 */
function baseIsNative(value) {
  if (!isObject(value) || isMasked(value)) {
    return false;
  }
  var pattern = isFunction(value) ? reIsNative : reIsHostCtor;
  return pattern.test(toSource(value));
}

module.exports = baseIsNative;

},{"./_isMasked":198,"./_toSource":243,"./isFunction":274,"./isObject":277}],105:[function(require,module,exports){
var getTag = require('./_getTag'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var setTag = '[object Set]';

/**
 * The base implementation of `_.isSet` without Node.js optimizations.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a set, else `false`.
 */
function baseIsSet(value) {
  return isObjectLike(value) && getTag(value) == setTag;
}

module.exports = baseIsSet;

},{"./_getTag":179,"./isObjectLike":278}],106:[function(require,module,exports){
var baseGetTag = require('./_baseGetTag'),
    isLength = require('./isLength'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var argsTag = '[object Arguments]',
    arrayTag = '[object Array]',
    boolTag = '[object Boolean]',
    dateTag = '[object Date]',
    errorTag = '[object Error]',
    funcTag = '[object Function]',
    mapTag = '[object Map]',
    numberTag = '[object Number]',
    objectTag = '[object Object]',
    regexpTag = '[object RegExp]',
    setTag = '[object Set]',
    stringTag = '[object String]',
    weakMapTag = '[object WeakMap]';

var arrayBufferTag = '[object ArrayBuffer]',
    dataViewTag = '[object DataView]',
    float32Tag = '[object Float32Array]',
    float64Tag = '[object Float64Array]',
    int8Tag = '[object Int8Array]',
    int16Tag = '[object Int16Array]',
    int32Tag = '[object Int32Array]',
    uint8Tag = '[object Uint8Array]',
    uint8ClampedTag = '[object Uint8ClampedArray]',
    uint16Tag = '[object Uint16Array]',
    uint32Tag = '[object Uint32Array]';

/** Used to identify `toStringTag` values of typed arrays. */
var typedArrayTags = {};
typedArrayTags[float32Tag] = typedArrayTags[float64Tag] =
typedArrayTags[int8Tag] = typedArrayTags[int16Tag] =
typedArrayTags[int32Tag] = typedArrayTags[uint8Tag] =
typedArrayTags[uint8ClampedTag] = typedArrayTags[uint16Tag] =
typedArrayTags[uint32Tag] = true;
typedArrayTags[argsTag] = typedArrayTags[arrayTag] =
typedArrayTags[arrayBufferTag] = typedArrayTags[boolTag] =
typedArrayTags[dataViewTag] = typedArrayTags[dateTag] =
typedArrayTags[errorTag] = typedArrayTags[funcTag] =
typedArrayTags[mapTag] = typedArrayTags[numberTag] =
typedArrayTags[objectTag] = typedArrayTags[regexpTag] =
typedArrayTags[setTag] = typedArrayTags[stringTag] =
typedArrayTags[weakMapTag] = false;

/**
 * The base implementation of `_.isTypedArray` without Node.js optimizations.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a typed array, else `false`.
 */
function baseIsTypedArray(value) {
  return isObjectLike(value) &&
    isLength(value.length) && !!typedArrayTags[baseGetTag(value)];
}

module.exports = baseIsTypedArray;

},{"./_baseGetTag":95,"./isLength":275,"./isObjectLike":278}],107:[function(require,module,exports){
var baseMatches = require('./_baseMatches'),
    baseMatchesProperty = require('./_baseMatchesProperty'),
    identity = require('./identity'),
    isArray = require('./isArray'),
    property = require('./property');

/**
 * The base implementation of `_.iteratee`.
 *
 * @private
 * @param {*} [value=_.identity] The value to convert to an iteratee.
 * @returns {Function} Returns the iteratee.
 */
function baseIteratee(value) {
  // Don't store the `typeof` result in a variable to avoid a JIT bug in Safari 9.
  // See https://bugs.webkit.org/show_bug.cgi?id=156034 for more details.
  if (typeof value == 'function') {
    return value;
  }
  if (value == null) {
    return identity;
  }
  if (typeof value == 'object') {
    return isArray(value)
      ? baseMatchesProperty(value[0], value[1])
      : baseMatches(value);
  }
  return property(value);
}

module.exports = baseIteratee;

},{"./_baseMatches":112,"./_baseMatchesProperty":113,"./identity":264,"./isArray":267,"./property":297}],108:[function(require,module,exports){
var isPrototype = require('./_isPrototype'),
    nativeKeys = require('./_nativeKeys');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * The base implementation of `_.keys` which doesn't treat sparse arrays as dense.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 */
function baseKeys(object) {
  if (!isPrototype(object)) {
    return nativeKeys(object);
  }
  var result = [];
  for (var key in Object(object)) {
    if (hasOwnProperty.call(object, key) && key != 'constructor') {
      result.push(key);
    }
  }
  return result;
}

module.exports = baseKeys;

},{"./_isPrototype":199,"./_nativeKeys":217}],109:[function(require,module,exports){
var isObject = require('./isObject'),
    isPrototype = require('./_isPrototype'),
    nativeKeysIn = require('./_nativeKeysIn');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * The base implementation of `_.keysIn` which doesn't treat sparse arrays as dense.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 */
function baseKeysIn(object) {
  if (!isObject(object)) {
    return nativeKeysIn(object);
  }
  var isProto = isPrototype(object),
      result = [];

  for (var key in object) {
    if (!(key == 'constructor' && (isProto || !hasOwnProperty.call(object, key)))) {
      result.push(key);
    }
  }
  return result;
}

module.exports = baseKeysIn;

},{"./_isPrototype":199,"./_nativeKeysIn":218,"./isObject":277}],110:[function(require,module,exports){
/**
 * The function whose prototype chain sequence wrappers inherit from.
 *
 * @private
 */
function baseLodash() {
  // No operation performed.
}

module.exports = baseLodash;

},{}],111:[function(require,module,exports){
var baseEach = require('./_baseEach'),
    isArrayLike = require('./isArrayLike');

/**
 * The base implementation of `_.map` without support for iteratee shorthands.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the new mapped array.
 */
function baseMap(collection, iteratee) {
  var index = -1,
      result = isArrayLike(collection) ? Array(collection.length) : [];

  baseEach(collection, function(value, key, collection) {
    result[++index] = iteratee(value, key, collection);
  });
  return result;
}

module.exports = baseMap;

},{"./_baseEach":87,"./isArrayLike":268}],112:[function(require,module,exports){
var baseIsMatch = require('./_baseIsMatch'),
    getMatchData = require('./_getMatchData'),
    matchesStrictComparable = require('./_matchesStrictComparable');

/**
 * The base implementation of `_.matches` which doesn't clone `source`.
 *
 * @private
 * @param {Object} source The object of property values to match.
 * @returns {Function} Returns the new spec function.
 */
function baseMatches(source) {
  var matchData = getMatchData(source);
  if (matchData.length == 1 && matchData[0][2]) {
    return matchesStrictComparable(matchData[0][0], matchData[0][1]);
  }
  return function(object) {
    return object === source || baseIsMatch(object, source, matchData);
  };
}

module.exports = baseMatches;

},{"./_baseIsMatch":102,"./_getMatchData":173,"./_matchesStrictComparable":212}],113:[function(require,module,exports){
var baseIsEqual = require('./_baseIsEqual'),
    get = require('./get'),
    hasIn = require('./hasIn'),
    isKey = require('./_isKey'),
    isStrictComparable = require('./_isStrictComparable'),
    matchesStrictComparable = require('./_matchesStrictComparable'),
    toKey = require('./_toKey');

/** Used to compose bitmasks for value comparisons. */
var COMPARE_PARTIAL_FLAG = 1,
    COMPARE_UNORDERED_FLAG = 2;

/**
 * The base implementation of `_.matchesProperty` which doesn't clone `srcValue`.
 *
 * @private
 * @param {string} path The path of the property to get.
 * @param {*} srcValue The value to match.
 * @returns {Function} Returns the new spec function.
 */
function baseMatchesProperty(path, srcValue) {
  if (isKey(path) && isStrictComparable(srcValue)) {
    return matchesStrictComparable(toKey(path), srcValue);
  }
  return function(object) {
    var objValue = get(object, path);
    return (objValue === undefined && objValue === srcValue)
      ? hasIn(object, path)
      : baseIsEqual(srcValue, objValue, COMPARE_PARTIAL_FLAG | COMPARE_UNORDERED_FLAG);
  };
}

module.exports = baseMatchesProperty;

},{"./_baseIsEqual":99,"./_isKey":195,"./_isStrictComparable":200,"./_matchesStrictComparable":212,"./_toKey":242,"./get":262,"./hasIn":263}],114:[function(require,module,exports){
var basePickBy = require('./_basePickBy'),
    hasIn = require('./hasIn');

/**
 * The base implementation of `_.pick` without support for individual
 * property identifiers.
 *
 * @private
 * @param {Object} object The source object.
 * @param {string[]} paths The property paths to pick.
 * @returns {Object} Returns the new object.
 */
function basePick(object, paths) {
  return basePickBy(object, paths, function(value, path) {
    return hasIn(object, path);
  });
}

module.exports = basePick;

},{"./_basePickBy":115,"./hasIn":263}],115:[function(require,module,exports){
var baseGet = require('./_baseGet'),
    baseSet = require('./_baseSet'),
    castPath = require('./_castPath');

/**
 * The base implementation of  `_.pickBy` without support for iteratee shorthands.
 *
 * @private
 * @param {Object} object The source object.
 * @param {string[]} paths The property paths to pick.
 * @param {Function} predicate The function invoked per property.
 * @returns {Object} Returns the new object.
 */
function basePickBy(object, paths, predicate) {
  var index = -1,
      length = paths.length,
      result = {};

  while (++index < length) {
    var path = paths[index],
        value = baseGet(object, path);

    if (predicate(value, path)) {
      baseSet(result, castPath(path, object), value);
    }
  }
  return result;
}

module.exports = basePickBy;

},{"./_baseGet":93,"./_baseSet":121,"./_castPath":133}],116:[function(require,module,exports){
/**
 * The base implementation of `_.property` without support for deep paths.
 *
 * @private
 * @param {string} key The key of the property to get.
 * @returns {Function} Returns the new accessor function.
 */
function baseProperty(key) {
  return function(object) {
    return object == null ? undefined : object[key];
  };
}

module.exports = baseProperty;

},{}],117:[function(require,module,exports){
var baseGet = require('./_baseGet');

/**
 * A specialized version of `baseProperty` which supports deep paths.
 *
 * @private
 * @param {Array|string} path The path of the property to get.
 * @returns {Function} Returns the new accessor function.
 */
function basePropertyDeep(path) {
  return function(object) {
    return baseGet(object, path);
  };
}

module.exports = basePropertyDeep;

},{"./_baseGet":93}],118:[function(require,module,exports){
var baseUnset = require('./_baseUnset'),
    isIndex = require('./_isIndex');

/** Used for built-in method references. */
var arrayProto = Array.prototype;

/** Built-in value references. */
var splice = arrayProto.splice;

/**
 * The base implementation of `_.pullAt` without support for individual
 * indexes or capturing the removed elements.
 *
 * @private
 * @param {Array} array The array to modify.
 * @param {number[]} indexes The indexes of elements to remove.
 * @returns {Array} Returns `array`.
 */
function basePullAt(array, indexes) {
  var length = array ? indexes.length : 0,
      lastIndex = length - 1;

  while (length--) {
    var index = indexes[length];
    if (length == lastIndex || index !== previous) {
      var previous = index;
      if (isIndex(index)) {
        splice.call(array, index, 1);
      } else {
        baseUnset(array, index);
      }
    }
  }
  return array;
}

module.exports = basePullAt;

},{"./_baseUnset":130,"./_isIndex":193}],119:[function(require,module,exports){
/**
 * The base implementation of `_.reduce` and `_.reduceRight`, without support
 * for iteratee shorthands, which iterates over `collection` using `eachFunc`.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @param {*} accumulator The initial value.
 * @param {boolean} initAccum Specify using the first or last element of
 *  `collection` as the initial value.
 * @param {Function} eachFunc The function to iterate over `collection`.
 * @returns {*} Returns the accumulated value.
 */
function baseReduce(collection, iteratee, accumulator, initAccum, eachFunc) {
  eachFunc(collection, function(value, index, collection) {
    accumulator = initAccum
      ? (initAccum = false, value)
      : iteratee(accumulator, value, index, collection);
  });
  return accumulator;
}

module.exports = baseReduce;

},{}],120:[function(require,module,exports){
var identity = require('./identity'),
    overRest = require('./_overRest'),
    setToString = require('./_setToString');

/**
 * The base implementation of `_.rest` which doesn't validate or coerce arguments.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @returns {Function} Returns the new function.
 */
function baseRest(func, start) {
  return setToString(overRest(func, start, identity), func + '');
}

module.exports = baseRest;

},{"./_overRest":222,"./_setToString":232,"./identity":264}],121:[function(require,module,exports){
var assignValue = require('./_assignValue'),
    castPath = require('./_castPath'),
    isIndex = require('./_isIndex'),
    isObject = require('./isObject'),
    toKey = require('./_toKey');

/**
 * The base implementation of `_.set`.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {Array|string} path The path of the property to set.
 * @param {*} value The value to set.
 * @param {Function} [customizer] The function to customize path creation.
 * @returns {Object} Returns `object`.
 */
function baseSet(object, path, value, customizer) {
  if (!isObject(object)) {
    return object;
  }
  path = castPath(path, object);

  var index = -1,
      length = path.length,
      lastIndex = length - 1,
      nested = object;

  while (nested != null && ++index < length) {
    var key = toKey(path[index]),
        newValue = value;

    if (key === '__proto__' || key === 'constructor' || key === 'prototype') {
      return object;
    }

    if (index != lastIndex) {
      var objValue = nested[key];
      newValue = customizer ? customizer(objValue, key, nested) : undefined;
      if (newValue === undefined) {
        newValue = isObject(objValue)
          ? objValue
          : (isIndex(path[index + 1]) ? [] : {});
      }
    }
    assignValue(nested, key, newValue);
    nested = nested[key];
  }
  return object;
}

module.exports = baseSet;

},{"./_assignValue":79,"./_castPath":133,"./_isIndex":193,"./_toKey":242,"./isObject":277}],122:[function(require,module,exports){
var identity = require('./identity'),
    metaMap = require('./_metaMap');

/**
 * The base implementation of `setData` without support for hot loop shorting.
 *
 * @private
 * @param {Function} func The function to associate metadata with.
 * @param {*} data The metadata.
 * @returns {Function} Returns `func`.
 */
var baseSetData = !metaMap ? identity : function(func, data) {
  metaMap.set(func, data);
  return func;
};

module.exports = baseSetData;

},{"./_metaMap":215,"./identity":264}],123:[function(require,module,exports){
var constant = require('./constant'),
    defineProperty = require('./_defineProperty'),
    identity = require('./identity');

/**
 * The base implementation of `setToString` without support for hot loop shorting.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */
var baseSetToString = !defineProperty ? identity : function(func, string) {
  return defineProperty(func, 'toString', {
    'configurable': true,
    'enumerable': false,
    'value': constant(string),
    'writable': true
  });
};

module.exports = baseSetToString;

},{"./_defineProperty":161,"./constant":250,"./identity":264}],124:[function(require,module,exports){
/**
 * The base implementation of `_.slice` without an iteratee call guard.
 *
 * @private
 * @param {Array} array The array to slice.
 * @param {number} [start=0] The start position.
 * @param {number} [end=array.length] The end position.
 * @returns {Array} Returns the slice of `array`.
 */
function baseSlice(array, start, end) {
  var index = -1,
      length = array.length;

  if (start < 0) {
    start = -start > length ? 0 : (length + start);
  }
  end = end > length ? length : end;
  if (end < 0) {
    end += length;
  }
  length = start > end ? 0 : ((end - start) >>> 0);
  start >>>= 0;

  var result = Array(length);
  while (++index < length) {
    result[index] = array[index + start];
  }
  return result;
}

module.exports = baseSlice;

},{}],125:[function(require,module,exports){
/**
 * The base implementation of `_.times` without support for iteratee shorthands
 * or max array length checks.
 *
 * @private
 * @param {number} n The number of times to invoke `iteratee`.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the array of results.
 */
function baseTimes(n, iteratee) {
  var index = -1,
      result = Array(n);

  while (++index < n) {
    result[index] = iteratee(index);
  }
  return result;
}

module.exports = baseTimes;

},{}],126:[function(require,module,exports){
var Symbol = require('./_Symbol'),
    arrayMap = require('./_arrayMap'),
    isArray = require('./isArray'),
    isSymbol = require('./isSymbol');

/** Used as references for various `Number` constants. */
var INFINITY = 1 / 0;

/** Used to convert symbols to primitives and strings. */
var symbolProto = Symbol ? Symbol.prototype : undefined,
    symbolToString = symbolProto ? symbolProto.toString : undefined;

/**
 * The base implementation of `_.toString` which doesn't convert nullish
 * values to empty strings.
 *
 * @private
 * @param {*} value The value to process.
 * @returns {string} Returns the string.
 */
function baseToString(value) {
  // Exit early for strings to avoid a performance hit in some environments.
  if (typeof value == 'string') {
    return value;
  }
  if (isArray(value)) {
    // Recursively convert values (susceptible to call stack limits).
    return arrayMap(value, baseToString) + '';
  }
  if (isSymbol(value)) {
    return symbolToString ? symbolToString.call(value) : '';
  }
  var result = (value + '');
  return (result == '0' && (1 / value) == -INFINITY) ? '-0' : result;
}

module.exports = baseToString;

},{"./_Symbol":66,"./_arrayMap":75,"./isArray":267,"./isSymbol":281}],127:[function(require,module,exports){
var trimmedEndIndex = require('./_trimmedEndIndex');

/** Used to match leading whitespace. */
var reTrimStart = /^\s+/;

/**
 * The base implementation of `_.trim`.
 *
 * @private
 * @param {string} string The string to trim.
 * @returns {string} Returns the trimmed string.
 */
function baseTrim(string) {
  return string
    ? string.slice(0, trimmedEndIndex(string) + 1).replace(reTrimStart, '')
    : string;
}

module.exports = baseTrim;

},{"./_trimmedEndIndex":244}],128:[function(require,module,exports){
/**
 * The base implementation of `_.unary` without support for storing metadata.
 *
 * @private
 * @param {Function} func The function to cap arguments for.
 * @returns {Function} Returns the new capped function.
 */
function baseUnary(func) {
  return function(value) {
    return func(value);
  };
}

module.exports = baseUnary;

},{}],129:[function(require,module,exports){
var SetCache = require('./_SetCache'),
    arrayIncludes = require('./_arrayIncludes'),
    arrayIncludesWith = require('./_arrayIncludesWith'),
    cacheHas = require('./_cacheHas'),
    createSet = require('./_createSet'),
    setToArray = require('./_setToArray');

/** Used as the size to enable large array optimizations. */
var LARGE_ARRAY_SIZE = 200;

/**
 * The base implementation of `_.uniqBy` without support for iteratee shorthands.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {Function} [iteratee] The iteratee invoked per element.
 * @param {Function} [comparator] The comparator invoked per element.
 * @returns {Array} Returns the new duplicate free array.
 */
function baseUniq(array, iteratee, comparator) {
  var index = -1,
      includes = arrayIncludes,
      length = array.length,
      isCommon = true,
      result = [],
      seen = result;

  if (comparator) {
    isCommon = false;
    includes = arrayIncludesWith;
  }
  else if (length >= LARGE_ARRAY_SIZE) {
    var set = iteratee ? null : createSet(array);
    if (set) {
      return setToArray(set);
    }
    isCommon = false;
    includes = cacheHas;
    seen = new SetCache;
  }
  else {
    seen = iteratee ? [] : result;
  }
  outer:
  while (++index < length) {
    var value = array[index],
        computed = iteratee ? iteratee(value) : value;

    value = (comparator || value !== 0) ? value : 0;
    if (isCommon && computed === computed) {
      var seenIndex = seen.length;
      while (seenIndex--) {
        if (seen[seenIndex] === computed) {
          continue outer;
        }
      }
      if (iteratee) {
        seen.push(computed);
      }
      result.push(value);
    }
    else if (!includes(seen, computed, comparator)) {
      if (seen !== result) {
        seen.push(computed);
      }
      result.push(value);
    }
  }
  return result;
}

module.exports = baseUniq;

},{"./_SetCache":64,"./_arrayIncludes":72,"./_arrayIncludesWith":73,"./_cacheHas":131,"./_createSet":158,"./_setToArray":231}],130:[function(require,module,exports){
var castPath = require('./_castPath'),
    last = require('./last'),
    parent = require('./_parent'),
    toKey = require('./_toKey');

/**
 * The base implementation of `_.unset`.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {Array|string} path The property path to unset.
 * @returns {boolean} Returns `true` if the property is deleted, else `false`.
 */
function baseUnset(object, path) {
  path = castPath(path, object);
  object = parent(object, path);
  return object == null || delete object[toKey(last(path))];
}

module.exports = baseUnset;

},{"./_castPath":133,"./_parent":223,"./_toKey":242,"./last":285}],131:[function(require,module,exports){
/**
 * Checks if a `cache` value for `key` exists.
 *
 * @private
 * @param {Object} cache The cache to query.
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function cacheHas(cache, key) {
  return cache.has(key);
}

module.exports = cacheHas;

},{}],132:[function(require,module,exports){
var identity = require('./identity');

/**
 * Casts `value` to `identity` if it's not a function.
 *
 * @private
 * @param {*} value The value to inspect.
 * @returns {Function} Returns cast function.
 */
function castFunction(value) {
  return typeof value == 'function' ? value : identity;
}

module.exports = castFunction;

},{"./identity":264}],133:[function(require,module,exports){
var isArray = require('./isArray'),
    isKey = require('./_isKey'),
    stringToPath = require('./_stringToPath'),
    toString = require('./toString');

/**
 * Casts `value` to a path array if it's not one.
 *
 * @private
 * @param {*} value The value to inspect.
 * @param {Object} [object] The object to query keys on.
 * @returns {Array} Returns the cast property path array.
 */
function castPath(value, object) {
  if (isArray(value)) {
    return value;
  }
  return isKey(value, object) ? [value] : stringToPath(toString(value));
}

module.exports = castPath;

},{"./_isKey":195,"./_stringToPath":241,"./isArray":267,"./toString":308}],134:[function(require,module,exports){
var Uint8Array = require('./_Uint8Array');

/**
 * Creates a clone of `arrayBuffer`.
 *
 * @private
 * @param {ArrayBuffer} arrayBuffer The array buffer to clone.
 * @returns {ArrayBuffer} Returns the cloned array buffer.
 */
function cloneArrayBuffer(arrayBuffer) {
  var result = new arrayBuffer.constructor(arrayBuffer.byteLength);
  new Uint8Array(result).set(new Uint8Array(arrayBuffer));
  return result;
}

module.exports = cloneArrayBuffer;

},{"./_Uint8Array":67}],135:[function(require,module,exports){
var root = require('./_root');

/** Detect free variable `exports`. */
var freeExports = typeof exports == 'object' && exports && !exports.nodeType && exports;

/** Detect free variable `module`. */
var freeModule = freeExports && typeof module == 'object' && module && !module.nodeType && module;

/** Detect the popular CommonJS extension `module.exports`. */
var moduleExports = freeModule && freeModule.exports === freeExports;

/** Built-in value references. */
var Buffer = moduleExports ? root.Buffer : undefined,
    allocUnsafe = Buffer ? Buffer.allocUnsafe : undefined;

/**
 * Creates a clone of  `buffer`.
 *
 * @private
 * @param {Buffer} buffer The buffer to clone.
 * @param {boolean} [isDeep] Specify a deep clone.
 * @returns {Buffer} Returns the cloned buffer.
 */
function cloneBuffer(buffer, isDeep) {
  if (isDeep) {
    return buffer.slice();
  }
  var length = buffer.length,
      result = allocUnsafe ? allocUnsafe(length) : new buffer.constructor(length);

  buffer.copy(result);
  return result;
}

module.exports = cloneBuffer;

},{"./_root":227}],136:[function(require,module,exports){
var cloneArrayBuffer = require('./_cloneArrayBuffer');

/**
 * Creates a clone of `dataView`.
 *
 * @private
 * @param {Object} dataView The data view to clone.
 * @param {boolean} [isDeep] Specify a deep clone.
 * @returns {Object} Returns the cloned data view.
 */
function cloneDataView(dataView, isDeep) {
  var buffer = isDeep ? cloneArrayBuffer(dataView.buffer) : dataView.buffer;
  return new dataView.constructor(buffer, dataView.byteOffset, dataView.byteLength);
}

module.exports = cloneDataView;

},{"./_cloneArrayBuffer":134}],137:[function(require,module,exports){
/** Used to match `RegExp` flags from their coerced string values. */
var reFlags = /\w*$/;

/**
 * Creates a clone of `regexp`.
 *
 * @private
 * @param {Object} regexp The regexp to clone.
 * @returns {Object} Returns the cloned regexp.
 */
function cloneRegExp(regexp) {
  var result = new regexp.constructor(regexp.source, reFlags.exec(regexp));
  result.lastIndex = regexp.lastIndex;
  return result;
}

module.exports = cloneRegExp;

},{}],138:[function(require,module,exports){
var Symbol = require('./_Symbol');

/** Used to convert symbols to primitives and strings. */
var symbolProto = Symbol ? Symbol.prototype : undefined,
    symbolValueOf = symbolProto ? symbolProto.valueOf : undefined;

/**
 * Creates a clone of the `symbol` object.
 *
 * @private
 * @param {Object} symbol The symbol object to clone.
 * @returns {Object} Returns the cloned symbol object.
 */
function cloneSymbol(symbol) {
  return symbolValueOf ? Object(symbolValueOf.call(symbol)) : {};
}

module.exports = cloneSymbol;

},{"./_Symbol":66}],139:[function(require,module,exports){
var cloneArrayBuffer = require('./_cloneArrayBuffer');

/**
 * Creates a clone of `typedArray`.
 *
 * @private
 * @param {Object} typedArray The typed array to clone.
 * @param {boolean} [isDeep] Specify a deep clone.
 * @returns {Object} Returns the cloned typed array.
 */
function cloneTypedArray(typedArray, isDeep) {
  var buffer = isDeep ? cloneArrayBuffer(typedArray.buffer) : typedArray.buffer;
  return new typedArray.constructor(buffer, typedArray.byteOffset, typedArray.length);
}

module.exports = cloneTypedArray;

},{"./_cloneArrayBuffer":134}],140:[function(require,module,exports){
/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * Creates an array that is the composition of partially applied arguments,
 * placeholders, and provided arguments into a single array of arguments.
 *
 * @private
 * @param {Array} args The provided arguments.
 * @param {Array} partials The arguments to prepend to those provided.
 * @param {Array} holders The `partials` placeholder indexes.
 * @params {boolean} [isCurried] Specify composing for a curried function.
 * @returns {Array} Returns the new array of composed arguments.
 */
function composeArgs(args, partials, holders, isCurried) {
  var argsIndex = -1,
      argsLength = args.length,
      holdersLength = holders.length,
      leftIndex = -1,
      leftLength = partials.length,
      rangeLength = nativeMax(argsLength - holdersLength, 0),
      result = Array(leftLength + rangeLength),
      isUncurried = !isCurried;

  while (++leftIndex < leftLength) {
    result[leftIndex] = partials[leftIndex];
  }
  while (++argsIndex < holdersLength) {
    if (isUncurried || argsIndex < argsLength) {
      result[holders[argsIndex]] = args[argsIndex];
    }
  }
  while (rangeLength--) {
    result[leftIndex++] = args[argsIndex++];
  }
  return result;
}

module.exports = composeArgs;

},{}],141:[function(require,module,exports){
/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * This function is like `composeArgs` except that the arguments composition
 * is tailored for `_.partialRight`.
 *
 * @private
 * @param {Array} args The provided arguments.
 * @param {Array} partials The arguments to append to those provided.
 * @param {Array} holders The `partials` placeholder indexes.
 * @params {boolean} [isCurried] Specify composing for a curried function.
 * @returns {Array} Returns the new array of composed arguments.
 */
function composeArgsRight(args, partials, holders, isCurried) {
  var argsIndex = -1,
      argsLength = args.length,
      holdersIndex = -1,
      holdersLength = holders.length,
      rightIndex = -1,
      rightLength = partials.length,
      rangeLength = nativeMax(argsLength - holdersLength, 0),
      result = Array(rangeLength + rightLength),
      isUncurried = !isCurried;

  while (++argsIndex < rangeLength) {
    result[argsIndex] = args[argsIndex];
  }
  var offset = argsIndex;
  while (++rightIndex < rightLength) {
    result[offset + rightIndex] = partials[rightIndex];
  }
  while (++holdersIndex < holdersLength) {
    if (isUncurried || argsIndex < argsLength) {
      result[offset + holders[holdersIndex]] = args[argsIndex++];
    }
  }
  return result;
}

module.exports = composeArgsRight;

},{}],142:[function(require,module,exports){
/**
 * Copies the values of `source` to `array`.
 *
 * @private
 * @param {Array} source The array to copy values from.
 * @param {Array} [array=[]] The array to copy values to.
 * @returns {Array} Returns `array`.
 */
function copyArray(source, array) {
  var index = -1,
      length = source.length;

  array || (array = Array(length));
  while (++index < length) {
    array[index] = source[index];
  }
  return array;
}

module.exports = copyArray;

},{}],143:[function(require,module,exports){
var assignValue = require('./_assignValue'),
    baseAssignValue = require('./_baseAssignValue');

/**
 * Copies properties of `source` to `object`.
 *
 * @private
 * @param {Object} source The object to copy properties from.
 * @param {Array} props The property identifiers to copy.
 * @param {Object} [object={}] The object to copy properties to.
 * @param {Function} [customizer] The function to customize copied values.
 * @returns {Object} Returns `object`.
 */
function copyObject(source, props, object, customizer) {
  var isNew = !object;
  object || (object = {});

  var index = -1,
      length = props.length;

  while (++index < length) {
    var key = props[index];

    var newValue = customizer
      ? customizer(object[key], source[key], key, object, source)
      : undefined;

    if (newValue === undefined) {
      newValue = source[key];
    }
    if (isNew) {
      baseAssignValue(object, key, newValue);
    } else {
      assignValue(object, key, newValue);
    }
  }
  return object;
}

module.exports = copyObject;

},{"./_assignValue":79,"./_baseAssignValue":83}],144:[function(require,module,exports){
var copyObject = require('./_copyObject'),
    getSymbols = require('./_getSymbols');

/**
 * Copies own symbols of `source` to `object`.
 *
 * @private
 * @param {Object} source The object to copy symbols from.
 * @param {Object} [object={}] The object to copy symbols to.
 * @returns {Object} Returns `object`.
 */
function copySymbols(source, object) {
  return copyObject(source, getSymbols(source), object);
}

module.exports = copySymbols;

},{"./_copyObject":143,"./_getSymbols":177}],145:[function(require,module,exports){
var copyObject = require('./_copyObject'),
    getSymbolsIn = require('./_getSymbolsIn');

/**
 * Copies own and inherited symbols of `source` to `object`.
 *
 * @private
 * @param {Object} source The object to copy symbols from.
 * @param {Object} [object={}] The object to copy symbols to.
 * @returns {Object} Returns `object`.
 */
function copySymbolsIn(source, object) {
  return copyObject(source, getSymbolsIn(source), object);
}

module.exports = copySymbolsIn;

},{"./_copyObject":143,"./_getSymbolsIn":178}],146:[function(require,module,exports){
var root = require('./_root');

/** Used to detect overreaching core-js shims. */
var coreJsData = root['__core-js_shared__'];

module.exports = coreJsData;

},{"./_root":227}],147:[function(require,module,exports){
/**
 * Gets the number of `placeholder` occurrences in `array`.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} placeholder The placeholder to search for.
 * @returns {number} Returns the placeholder count.
 */
function countHolders(array, placeholder) {
  var length = array.length,
      result = 0;

  while (length--) {
    if (array[length] === placeholder) {
      ++result;
    }
  }
  return result;
}

module.exports = countHolders;

},{}],148:[function(require,module,exports){
var baseRest = require('./_baseRest'),
    isIterateeCall = require('./_isIterateeCall');

/**
 * Creates a function like `_.assign`.
 *
 * @private
 * @param {Function} assigner The function to assign values.
 * @returns {Function} Returns the new assigner function.
 */
function createAssigner(assigner) {
  return baseRest(function(object, sources) {
    var index = -1,
        length = sources.length,
        customizer = length > 1 ? sources[length - 1] : undefined,
        guard = length > 2 ? sources[2] : undefined;

    customizer = (assigner.length > 3 && typeof customizer == 'function')
      ? (length--, customizer)
      : undefined;

    if (guard && isIterateeCall(sources[0], sources[1], guard)) {
      customizer = length < 3 ? undefined : customizer;
      length = 1;
    }
    object = Object(object);
    while (++index < length) {
      var source = sources[index];
      if (source) {
        assigner(object, source, index, customizer);
      }
    }
    return object;
  });
}

module.exports = createAssigner;

},{"./_baseRest":120,"./_isIterateeCall":194}],149:[function(require,module,exports){
var isArrayLike = require('./isArrayLike');

/**
 * Creates a `baseEach` or `baseEachRight` function.
 *
 * @private
 * @param {Function} eachFunc The function to iterate over a collection.
 * @param {boolean} [fromRight] Specify iterating from right to left.
 * @returns {Function} Returns the new base function.
 */
function createBaseEach(eachFunc, fromRight) {
  return function(collection, iteratee) {
    if (collection == null) {
      return collection;
    }
    if (!isArrayLike(collection)) {
      return eachFunc(collection, iteratee);
    }
    var length = collection.length,
        index = fromRight ? length : -1,
        iterable = Object(collection);

    while ((fromRight ? index-- : ++index < length)) {
      if (iteratee(iterable[index], index, iterable) === false) {
        break;
      }
    }
    return collection;
  };
}

module.exports = createBaseEach;

},{"./isArrayLike":268}],150:[function(require,module,exports){
/**
 * Creates a base function for methods like `_.forIn` and `_.forOwn`.
 *
 * @private
 * @param {boolean} [fromRight] Specify iterating from right to left.
 * @returns {Function} Returns the new base function.
 */
function createBaseFor(fromRight) {
  return function(object, iteratee, keysFunc) {
    var index = -1,
        iterable = Object(object),
        props = keysFunc(object),
        length = props.length;

    while (length--) {
      var key = props[fromRight ? length : ++index];
      if (iteratee(iterable[key], key, iterable) === false) {
        break;
      }
    }
    return object;
  };
}

module.exports = createBaseFor;

},{}],151:[function(require,module,exports){
var createCtor = require('./_createCtor'),
    root = require('./_root');

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1;

/**
 * Creates a function that wraps `func` to invoke it with the optional `this`
 * binding of `thisArg`.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @param {*} [thisArg] The `this` binding of `func`.
 * @returns {Function} Returns the new wrapped function.
 */
function createBind(func, bitmask, thisArg) {
  var isBind = bitmask & WRAP_BIND_FLAG,
      Ctor = createCtor(func);

  function wrapper() {
    var fn = (this && this !== root && this instanceof wrapper) ? Ctor : func;
    return fn.apply(isBind ? thisArg : this, arguments);
  }
  return wrapper;
}

module.exports = createBind;

},{"./_createCtor":152,"./_root":227}],152:[function(require,module,exports){
var baseCreate = require('./_baseCreate'),
    isObject = require('./isObject');

/**
 * Creates a function that produces an instance of `Ctor` regardless of
 * whether it was invoked as part of a `new` expression or by `call` or `apply`.
 *
 * @private
 * @param {Function} Ctor The constructor to wrap.
 * @returns {Function} Returns the new wrapped function.
 */
function createCtor(Ctor) {
  return function() {
    // Use a `switch` statement to work with class constructors. See
    // http://ecma-international.org/ecma-262/7.0/#sec-ecmascript-function-objects-call-thisargument-argumentslist
    // for more details.
    var args = arguments;
    switch (args.length) {
      case 0: return new Ctor;
      case 1: return new Ctor(args[0]);
      case 2: return new Ctor(args[0], args[1]);
      case 3: return new Ctor(args[0], args[1], args[2]);
      case 4: return new Ctor(args[0], args[1], args[2], args[3]);
      case 5: return new Ctor(args[0], args[1], args[2], args[3], args[4]);
      case 6: return new Ctor(args[0], args[1], args[2], args[3], args[4], args[5]);
      case 7: return new Ctor(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
    }
    var thisBinding = baseCreate(Ctor.prototype),
        result = Ctor.apply(thisBinding, args);

    // Mimic the constructor's `return` behavior.
    // See https://es5.github.io/#x13.2.2 for more details.
    return isObject(result) ? result : thisBinding;
  };
}

module.exports = createCtor;

},{"./_baseCreate":85,"./isObject":277}],153:[function(require,module,exports){
var apply = require('./_apply'),
    createCtor = require('./_createCtor'),
    createHybrid = require('./_createHybrid'),
    createRecurry = require('./_createRecurry'),
    getHolder = require('./_getHolder'),
    replaceHolders = require('./_replaceHolders'),
    root = require('./_root');

/**
 * Creates a function that wraps `func` to enable currying.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @param {number} arity The arity of `func`.
 * @returns {Function} Returns the new wrapped function.
 */
function createCurry(func, bitmask, arity) {
  var Ctor = createCtor(func);

  function wrapper() {
    var length = arguments.length,
        args = Array(length),
        index = length,
        placeholder = getHolder(wrapper);

    while (index--) {
      args[index] = arguments[index];
    }
    var holders = (length < 3 && args[0] !== placeholder && args[length - 1] !== placeholder)
      ? []
      : replaceHolders(args, placeholder);

    length -= holders.length;
    if (length < arity) {
      return createRecurry(
        func, bitmask, createHybrid, wrapper.placeholder, undefined,
        args, holders, undefined, undefined, arity - length);
    }
    var fn = (this && this !== root && this instanceof wrapper) ? Ctor : func;
    return apply(fn, this, args);
  }
  return wrapper;
}

module.exports = createCurry;

},{"./_apply":69,"./_createCtor":152,"./_createHybrid":155,"./_createRecurry":157,"./_getHolder":171,"./_replaceHolders":226,"./_root":227}],154:[function(require,module,exports){
var baseIteratee = require('./_baseIteratee'),
    isArrayLike = require('./isArrayLike'),
    keys = require('./keys');

/**
 * Creates a `_.find` or `_.findLast` function.
 *
 * @private
 * @param {Function} findIndexFunc The function to find the collection index.
 * @returns {Function} Returns the new find function.
 */
function createFind(findIndexFunc) {
  return function(collection, predicate, fromIndex) {
    var iterable = Object(collection);
    if (!isArrayLike(collection)) {
      var iteratee = baseIteratee(predicate, 3);
      collection = keys(collection);
      predicate = function(key) { return iteratee(iterable[key], key, iterable); };
    }
    var index = findIndexFunc(collection, predicate, fromIndex);
    return index > -1 ? iterable[iteratee ? collection[index] : index] : undefined;
  };
}

module.exports = createFind;

},{"./_baseIteratee":107,"./isArrayLike":268,"./keys":283}],155:[function(require,module,exports){
var composeArgs = require('./_composeArgs'),
    composeArgsRight = require('./_composeArgsRight'),
    countHolders = require('./_countHolders'),
    createCtor = require('./_createCtor'),
    createRecurry = require('./_createRecurry'),
    getHolder = require('./_getHolder'),
    reorder = require('./_reorder'),
    replaceHolders = require('./_replaceHolders'),
    root = require('./_root');

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1,
    WRAP_BIND_KEY_FLAG = 2,
    WRAP_CURRY_FLAG = 8,
    WRAP_CURRY_RIGHT_FLAG = 16,
    WRAP_ARY_FLAG = 128,
    WRAP_FLIP_FLAG = 512;

/**
 * Creates a function that wraps `func` to invoke it with optional `this`
 * binding of `thisArg`, partial application, and currying.
 *
 * @private
 * @param {Function|string} func The function or method name to wrap.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @param {*} [thisArg] The `this` binding of `func`.
 * @param {Array} [partials] The arguments to prepend to those provided to
 *  the new function.
 * @param {Array} [holders] The `partials` placeholder indexes.
 * @param {Array} [partialsRight] The arguments to append to those provided
 *  to the new function.
 * @param {Array} [holdersRight] The `partialsRight` placeholder indexes.
 * @param {Array} [argPos] The argument positions of the new function.
 * @param {number} [ary] The arity cap of `func`.
 * @param {number} [arity] The arity of `func`.
 * @returns {Function} Returns the new wrapped function.
 */
function createHybrid(func, bitmask, thisArg, partials, holders, partialsRight, holdersRight, argPos, ary, arity) {
  var isAry = bitmask & WRAP_ARY_FLAG,
      isBind = bitmask & WRAP_BIND_FLAG,
      isBindKey = bitmask & WRAP_BIND_KEY_FLAG,
      isCurried = bitmask & (WRAP_CURRY_FLAG | WRAP_CURRY_RIGHT_FLAG),
      isFlip = bitmask & WRAP_FLIP_FLAG,
      Ctor = isBindKey ? undefined : createCtor(func);

  function wrapper() {
    var length = arguments.length,
        args = Array(length),
        index = length;

    while (index--) {
      args[index] = arguments[index];
    }
    if (isCurried) {
      var placeholder = getHolder(wrapper),
          holdersCount = countHolders(args, placeholder);
    }
    if (partials) {
      args = composeArgs(args, partials, holders, isCurried);
    }
    if (partialsRight) {
      args = composeArgsRight(args, partialsRight, holdersRight, isCurried);
    }
    length -= holdersCount;
    if (isCurried && length < arity) {
      var newHolders = replaceHolders(args, placeholder);
      return createRecurry(
        func, bitmask, createHybrid, wrapper.placeholder, thisArg,
        args, newHolders, argPos, ary, arity - length
      );
    }
    var thisBinding = isBind ? thisArg : this,
        fn = isBindKey ? thisBinding[func] : func;

    length = args.length;
    if (argPos) {
      args = reorder(args, argPos);
    } else if (isFlip && length > 1) {
      args.reverse();
    }
    if (isAry && ary < length) {
      args.length = ary;
    }
    if (this && this !== root && this instanceof wrapper) {
      fn = Ctor || createCtor(fn);
    }
    return fn.apply(thisBinding, args);
  }
  return wrapper;
}

module.exports = createHybrid;

},{"./_composeArgs":140,"./_composeArgsRight":141,"./_countHolders":147,"./_createCtor":152,"./_createRecurry":157,"./_getHolder":171,"./_reorder":225,"./_replaceHolders":226,"./_root":227}],156:[function(require,module,exports){
var apply = require('./_apply'),
    createCtor = require('./_createCtor'),
    root = require('./_root');

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1;

/**
 * Creates a function that wraps `func` to invoke it with the `this` binding
 * of `thisArg` and `partials` prepended to the arguments it receives.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @param {*} thisArg The `this` binding of `func`.
 * @param {Array} partials The arguments to prepend to those provided to
 *  the new function.
 * @returns {Function} Returns the new wrapped function.
 */
function createPartial(func, bitmask, thisArg, partials) {
  var isBind = bitmask & WRAP_BIND_FLAG,
      Ctor = createCtor(func);

  function wrapper() {
    var argsIndex = -1,
        argsLength = arguments.length,
        leftIndex = -1,
        leftLength = partials.length,
        args = Array(leftLength + argsLength),
        fn = (this && this !== root && this instanceof wrapper) ? Ctor : func;

    while (++leftIndex < leftLength) {
      args[leftIndex] = partials[leftIndex];
    }
    while (argsLength--) {
      args[leftIndex++] = arguments[++argsIndex];
    }
    return apply(fn, isBind ? thisArg : this, args);
  }
  return wrapper;
}

module.exports = createPartial;

},{"./_apply":69,"./_createCtor":152,"./_root":227}],157:[function(require,module,exports){
var isLaziable = require('./_isLaziable'),
    setData = require('./_setData'),
    setWrapToString = require('./_setWrapToString');

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1,
    WRAP_BIND_KEY_FLAG = 2,
    WRAP_CURRY_BOUND_FLAG = 4,
    WRAP_CURRY_FLAG = 8,
    WRAP_PARTIAL_FLAG = 32,
    WRAP_PARTIAL_RIGHT_FLAG = 64;

/**
 * Creates a function that wraps `func` to continue currying.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @param {Function} wrapFunc The function to create the `func` wrapper.
 * @param {*} placeholder The placeholder value.
 * @param {*} [thisArg] The `this` binding of `func`.
 * @param {Array} [partials] The arguments to prepend to those provided to
 *  the new function.
 * @param {Array} [holders] The `partials` placeholder indexes.
 * @param {Array} [argPos] The argument positions of the new function.
 * @param {number} [ary] The arity cap of `func`.
 * @param {number} [arity] The arity of `func`.
 * @returns {Function} Returns the new wrapped function.
 */
function createRecurry(func, bitmask, wrapFunc, placeholder, thisArg, partials, holders, argPos, ary, arity) {
  var isCurry = bitmask & WRAP_CURRY_FLAG,
      newHolders = isCurry ? holders : undefined,
      newHoldersRight = isCurry ? undefined : holders,
      newPartials = isCurry ? partials : undefined,
      newPartialsRight = isCurry ? undefined : partials;

  bitmask |= (isCurry ? WRAP_PARTIAL_FLAG : WRAP_PARTIAL_RIGHT_FLAG);
  bitmask &= ~(isCurry ? WRAP_PARTIAL_RIGHT_FLAG : WRAP_PARTIAL_FLAG);

  if (!(bitmask & WRAP_CURRY_BOUND_FLAG)) {
    bitmask &= ~(WRAP_BIND_FLAG | WRAP_BIND_KEY_FLAG);
  }
  var newData = [
    func, bitmask, thisArg, newPartials, newHolders, newPartialsRight,
    newHoldersRight, argPos, ary, arity
  ];

  var result = wrapFunc.apply(undefined, newData);
  if (isLaziable(func)) {
    setData(result, newData);
  }
  result.placeholder = placeholder;
  return setWrapToString(result, func, bitmask);
}

module.exports = createRecurry;

},{"./_isLaziable":197,"./_setData":230,"./_setWrapToString":233}],158:[function(require,module,exports){
var Set = require('./_Set'),
    noop = require('./noop'),
    setToArray = require('./_setToArray');

/** Used as references for various `Number` constants. */
var INFINITY = 1 / 0;

/**
 * Creates a set object of `values`.
 *
 * @private
 * @param {Array} values The values to add to the set.
 * @returns {Object} Returns the new set.
 */
var createSet = !(Set && (1 / setToArray(new Set([,-0]))[1]) == INFINITY) ? noop : function(values) {
  return new Set(values);
};

module.exports = createSet;

},{"./_Set":63,"./_setToArray":231,"./noop":289}],159:[function(require,module,exports){
var baseSetData = require('./_baseSetData'),
    createBind = require('./_createBind'),
    createCurry = require('./_createCurry'),
    createHybrid = require('./_createHybrid'),
    createPartial = require('./_createPartial'),
    getData = require('./_getData'),
    mergeData = require('./_mergeData'),
    setData = require('./_setData'),
    setWrapToString = require('./_setWrapToString'),
    toInteger = require('./toInteger');

/** Error message constants. */
var FUNC_ERROR_TEXT = 'Expected a function';

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1,
    WRAP_BIND_KEY_FLAG = 2,
    WRAP_CURRY_FLAG = 8,
    WRAP_CURRY_RIGHT_FLAG = 16,
    WRAP_PARTIAL_FLAG = 32,
    WRAP_PARTIAL_RIGHT_FLAG = 64;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * Creates a function that either curries or invokes `func` with optional
 * `this` binding and partially applied arguments.
 *
 * @private
 * @param {Function|string} func The function or method name to wrap.
 * @param {number} bitmask The bitmask flags.
 *    1 - `_.bind`
 *    2 - `_.bindKey`
 *    4 - `_.curry` or `_.curryRight` of a bound function
 *    8 - `_.curry`
 *   16 - `_.curryRight`
 *   32 - `_.partial`
 *   64 - `_.partialRight`
 *  128 - `_.rearg`
 *  256 - `_.ary`
 *  512 - `_.flip`
 * @param {*} [thisArg] The `this` binding of `func`.
 * @param {Array} [partials] The arguments to be partially applied.
 * @param {Array} [holders] The `partials` placeholder indexes.
 * @param {Array} [argPos] The argument positions of the new function.
 * @param {number} [ary] The arity cap of `func`.
 * @param {number} [arity] The arity of `func`.
 * @returns {Function} Returns the new wrapped function.
 */
function createWrap(func, bitmask, thisArg, partials, holders, argPos, ary, arity) {
  var isBindKey = bitmask & WRAP_BIND_KEY_FLAG;
  if (!isBindKey && typeof func != 'function') {
    throw new TypeError(FUNC_ERROR_TEXT);
  }
  var length = partials ? partials.length : 0;
  if (!length) {
    bitmask &= ~(WRAP_PARTIAL_FLAG | WRAP_PARTIAL_RIGHT_FLAG);
    partials = holders = undefined;
  }
  ary = ary === undefined ? ary : nativeMax(toInteger(ary), 0);
  arity = arity === undefined ? arity : toInteger(arity);
  length -= holders ? holders.length : 0;

  if (bitmask & WRAP_PARTIAL_RIGHT_FLAG) {
    var partialsRight = partials,
        holdersRight = holders;

    partials = holders = undefined;
  }
  var data = isBindKey ? undefined : getData(func);

  var newData = [
    func, bitmask, thisArg, partials, holders, partialsRight, holdersRight,
    argPos, ary, arity
  ];

  if (data) {
    mergeData(newData, data);
  }
  func = newData[0];
  bitmask = newData[1];
  thisArg = newData[2];
  partials = newData[3];
  holders = newData[4];
  arity = newData[9] = newData[9] === undefined
    ? (isBindKey ? 0 : func.length)
    : nativeMax(newData[9] - length, 0);

  if (!arity && bitmask & (WRAP_CURRY_FLAG | WRAP_CURRY_RIGHT_FLAG)) {
    bitmask &= ~(WRAP_CURRY_FLAG | WRAP_CURRY_RIGHT_FLAG);
  }
  if (!bitmask || bitmask == WRAP_BIND_FLAG) {
    var result = createBind(func, bitmask, thisArg);
  } else if (bitmask == WRAP_CURRY_FLAG || bitmask == WRAP_CURRY_RIGHT_FLAG) {
    result = createCurry(func, bitmask, arity);
  } else if ((bitmask == WRAP_PARTIAL_FLAG || bitmask == (WRAP_BIND_FLAG | WRAP_PARTIAL_FLAG)) && !holders.length) {
    result = createPartial(func, bitmask, thisArg, partials);
  } else {
    result = createHybrid.apply(undefined, newData);
  }
  var setter = data ? baseSetData : setData;
  return setWrapToString(setter(result, newData), func, bitmask);
}

module.exports = createWrap;

},{"./_baseSetData":122,"./_createBind":151,"./_createCurry":153,"./_createHybrid":155,"./_createPartial":156,"./_getData":169,"./_mergeData":214,"./_setData":230,"./_setWrapToString":233,"./toInteger":306}],160:[function(require,module,exports){
var isPlainObject = require('./isPlainObject');

/**
 * Used by `_.omit` to customize its `_.cloneDeep` use to only clone plain
 * objects.
 *
 * @private
 * @param {*} value The value to inspect.
 * @param {string} key The key of the property to inspect.
 * @returns {*} Returns the uncloned value or `undefined` to defer cloning to `_.cloneDeep`.
 */
function customOmitClone(value) {
  return isPlainObject(value) ? undefined : value;
}

module.exports = customOmitClone;

},{"./isPlainObject":279}],161:[function(require,module,exports){
var getNative = require('./_getNative');

var defineProperty = (function() {
  try {
    var func = getNative(Object, 'defineProperty');
    func({}, '', {});
    return func;
  } catch (e) {}
}());

module.exports = defineProperty;

},{"./_getNative":174}],162:[function(require,module,exports){
var SetCache = require('./_SetCache'),
    arraySome = require('./_arraySome'),
    cacheHas = require('./_cacheHas');

/** Used to compose bitmasks for value comparisons. */
var COMPARE_PARTIAL_FLAG = 1,
    COMPARE_UNORDERED_FLAG = 2;

/**
 * A specialized version of `baseIsEqualDeep` for arrays with support for
 * partial deep comparisons.
 *
 * @private
 * @param {Array} array The array to compare.
 * @param {Array} other The other array to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} stack Tracks traversed `array` and `other` objects.
 * @returns {boolean} Returns `true` if the arrays are equivalent, else `false`.
 */
function equalArrays(array, other, bitmask, customizer, equalFunc, stack) {
  var isPartial = bitmask & COMPARE_PARTIAL_FLAG,
      arrLength = array.length,
      othLength = other.length;

  if (arrLength != othLength && !(isPartial && othLength > arrLength)) {
    return false;
  }
  // Check that cyclic values are equal.
  var arrStacked = stack.get(array);
  var othStacked = stack.get(other);
  if (arrStacked && othStacked) {
    return arrStacked == other && othStacked == array;
  }
  var index = -1,
      result = true,
      seen = (bitmask & COMPARE_UNORDERED_FLAG) ? new SetCache : undefined;

  stack.set(array, other);
  stack.set(other, array);

  // Ignore non-index properties.
  while (++index < arrLength) {
    var arrValue = array[index],
        othValue = other[index];

    if (customizer) {
      var compared = isPartial
        ? customizer(othValue, arrValue, index, other, array, stack)
        : customizer(arrValue, othValue, index, array, other, stack);
    }
    if (compared !== undefined) {
      if (compared) {
        continue;
      }
      result = false;
      break;
    }
    // Recursively compare arrays (susceptible to call stack limits).
    if (seen) {
      if (!arraySome(other, function(othValue, othIndex) {
            if (!cacheHas(seen, othIndex) &&
                (arrValue === othValue || equalFunc(arrValue, othValue, bitmask, customizer, stack))) {
              return seen.push(othIndex);
            }
          })) {
        result = false;
        break;
      }
    } else if (!(
          arrValue === othValue ||
            equalFunc(arrValue, othValue, bitmask, customizer, stack)
        )) {
      result = false;
      break;
    }
  }
  stack['delete'](array);
  stack['delete'](other);
  return result;
}

module.exports = equalArrays;

},{"./_SetCache":64,"./_arraySome":78,"./_cacheHas":131}],163:[function(require,module,exports){
var Symbol = require('./_Symbol'),
    Uint8Array = require('./_Uint8Array'),
    eq = require('./eq'),
    equalArrays = require('./_equalArrays'),
    mapToArray = require('./_mapToArray'),
    setToArray = require('./_setToArray');

/** Used to compose bitmasks for value comparisons. */
var COMPARE_PARTIAL_FLAG = 1,
    COMPARE_UNORDERED_FLAG = 2;

/** `Object#toString` result references. */
var boolTag = '[object Boolean]',
    dateTag = '[object Date]',
    errorTag = '[object Error]',
    mapTag = '[object Map]',
    numberTag = '[object Number]',
    regexpTag = '[object RegExp]',
    setTag = '[object Set]',
    stringTag = '[object String]',
    symbolTag = '[object Symbol]';

var arrayBufferTag = '[object ArrayBuffer]',
    dataViewTag = '[object DataView]';

/** Used to convert symbols to primitives and strings. */
var symbolProto = Symbol ? Symbol.prototype : undefined,
    symbolValueOf = symbolProto ? symbolProto.valueOf : undefined;

/**
 * A specialized version of `baseIsEqualDeep` for comparing objects of
 * the same `toStringTag`.
 *
 * **Note:** This function only supports comparing values with tags of
 * `Boolean`, `Date`, `Error`, `Number`, `RegExp`, or `String`.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {string} tag The `toStringTag` of the objects to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} stack Tracks traversed `object` and `other` objects.
 * @returns {boolean} Returns `true` if the objects are equivalent, else `false`.
 */
function equalByTag(object, other, tag, bitmask, customizer, equalFunc, stack) {
  switch (tag) {
    case dataViewTag:
      if ((object.byteLength != other.byteLength) ||
          (object.byteOffset != other.byteOffset)) {
        return false;
      }
      object = object.buffer;
      other = other.buffer;

    case arrayBufferTag:
      if ((object.byteLength != other.byteLength) ||
          !equalFunc(new Uint8Array(object), new Uint8Array(other))) {
        return false;
      }
      return true;

    case boolTag:
    case dateTag:
    case numberTag:
      // Coerce booleans to `1` or `0` and dates to milliseconds.
      // Invalid dates are coerced to `NaN`.
      return eq(+object, +other);

    case errorTag:
      return object.name == other.name && object.message == other.message;

    case regexpTag:
    case stringTag:
      // Coerce regexes to strings and treat strings, primitives and objects,
      // as equal. See http://www.ecma-international.org/ecma-262/7.0/#sec-regexp.prototype.tostring
      // for more details.
      return object == (other + '');

    case mapTag:
      var convert = mapToArray;

    case setTag:
      var isPartial = bitmask & COMPARE_PARTIAL_FLAG;
      convert || (convert = setToArray);

      if (object.size != other.size && !isPartial) {
        return false;
      }
      // Assume cyclic values are equal.
      var stacked = stack.get(object);
      if (stacked) {
        return stacked == other;
      }
      bitmask |= COMPARE_UNORDERED_FLAG;

      // Recursively compare objects (susceptible to call stack limits).
      stack.set(object, other);
      var result = equalArrays(convert(object), convert(other), bitmask, customizer, equalFunc, stack);
      stack['delete'](object);
      return result;

    case symbolTag:
      if (symbolValueOf) {
        return symbolValueOf.call(object) == symbolValueOf.call(other);
      }
  }
  return false;
}

module.exports = equalByTag;

},{"./_Symbol":66,"./_Uint8Array":67,"./_equalArrays":162,"./_mapToArray":211,"./_setToArray":231,"./eq":254}],164:[function(require,module,exports){
var getAllKeys = require('./_getAllKeys');

/** Used to compose bitmasks for value comparisons. */
var COMPARE_PARTIAL_FLAG = 1;

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * A specialized version of `baseIsEqualDeep` for objects with support for
 * partial deep comparisons.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} stack Tracks traversed `object` and `other` objects.
 * @returns {boolean} Returns `true` if the objects are equivalent, else `false`.
 */
function equalObjects(object, other, bitmask, customizer, equalFunc, stack) {
  var isPartial = bitmask & COMPARE_PARTIAL_FLAG,
      objProps = getAllKeys(object),
      objLength = objProps.length,
      othProps = getAllKeys(other),
      othLength = othProps.length;

  if (objLength != othLength && !isPartial) {
    return false;
  }
  var index = objLength;
  while (index--) {
    var key = objProps[index];
    if (!(isPartial ? key in other : hasOwnProperty.call(other, key))) {
      return false;
    }
  }
  // Check that cyclic values are equal.
  var objStacked = stack.get(object);
  var othStacked = stack.get(other);
  if (objStacked && othStacked) {
    return objStacked == other && othStacked == object;
  }
  var result = true;
  stack.set(object, other);
  stack.set(other, object);

  var skipCtor = isPartial;
  while (++index < objLength) {
    key = objProps[index];
    var objValue = object[key],
        othValue = other[key];

    if (customizer) {
      var compared = isPartial
        ? customizer(othValue, objValue, key, other, object, stack)
        : customizer(objValue, othValue, key, object, other, stack);
    }
    // Recursively compare objects (susceptible to call stack limits).
    if (!(compared === undefined
          ? (objValue === othValue || equalFunc(objValue, othValue, bitmask, customizer, stack))
          : compared
        )) {
      result = false;
      break;
    }
    skipCtor || (skipCtor = key == 'constructor');
  }
  if (result && !skipCtor) {
    var objCtor = object.constructor,
        othCtor = other.constructor;

    // Non `Object` object instances with different constructors are not equal.
    if (objCtor != othCtor &&
        ('constructor' in object && 'constructor' in other) &&
        !(typeof objCtor == 'function' && objCtor instanceof objCtor &&
          typeof othCtor == 'function' && othCtor instanceof othCtor)) {
      result = false;
    }
  }
  stack['delete'](object);
  stack['delete'](other);
  return result;
}

module.exports = equalObjects;

},{"./_getAllKeys":167}],165:[function(require,module,exports){
var flatten = require('./flatten'),
    overRest = require('./_overRest'),
    setToString = require('./_setToString');

/**
 * A specialized version of `baseRest` which flattens the rest array.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @returns {Function} Returns the new function.
 */
function flatRest(func) {
  return setToString(overRest(func, undefined, flatten), func + '');
}

module.exports = flatRest;

},{"./_overRest":222,"./_setToString":232,"./flatten":259}],166:[function(require,module,exports){
(function (global){(function (){
/** Detect free variable `global` from Node.js. */
var freeGlobal = typeof global == 'object' && global && global.Object === Object && global;

module.exports = freeGlobal;

}).call(this)}).call(this,typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : {})

},{}],167:[function(require,module,exports){
var baseGetAllKeys = require('./_baseGetAllKeys'),
    getSymbols = require('./_getSymbols'),
    keys = require('./keys');

/**
 * Creates an array of own enumerable property names and symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names and symbols.
 */
function getAllKeys(object) {
  return baseGetAllKeys(object, keys, getSymbols);
}

module.exports = getAllKeys;

},{"./_baseGetAllKeys":94,"./_getSymbols":177,"./keys":283}],168:[function(require,module,exports){
var baseGetAllKeys = require('./_baseGetAllKeys'),
    getSymbolsIn = require('./_getSymbolsIn'),
    keysIn = require('./keysIn');

/**
 * Creates an array of own and inherited enumerable property names and
 * symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names and symbols.
 */
function getAllKeysIn(object) {
  return baseGetAllKeys(object, keysIn, getSymbolsIn);
}

module.exports = getAllKeysIn;

},{"./_baseGetAllKeys":94,"./_getSymbolsIn":178,"./keysIn":284}],169:[function(require,module,exports){
var metaMap = require('./_metaMap'),
    noop = require('./noop');

/**
 * Gets metadata for `func`.
 *
 * @private
 * @param {Function} func The function to query.
 * @returns {*} Returns the metadata for `func`.
 */
var getData = !metaMap ? noop : function(func) {
  return metaMap.get(func);
};

module.exports = getData;

},{"./_metaMap":215,"./noop":289}],170:[function(require,module,exports){
var realNames = require('./_realNames');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Gets the name of `func`.
 *
 * @private
 * @param {Function} func The function to query.
 * @returns {string} Returns the function name.
 */
function getFuncName(func) {
  var result = (func.name + ''),
      array = realNames[result],
      length = hasOwnProperty.call(realNames, result) ? array.length : 0;

  while (length--) {
    var data = array[length],
        otherFunc = data.func;
    if (otherFunc == null || otherFunc == func) {
      return data.name;
    }
  }
  return result;
}

module.exports = getFuncName;

},{"./_realNames":224}],171:[function(require,module,exports){
/**
 * Gets the argument placeholder value for `func`.
 *
 * @private
 * @param {Function} func The function to inspect.
 * @returns {*} Returns the placeholder value.
 */
function getHolder(func) {
  var object = func;
  return object.placeholder;
}

module.exports = getHolder;

},{}],172:[function(require,module,exports){
var isKeyable = require('./_isKeyable');

/**
 * Gets the data for `map`.
 *
 * @private
 * @param {Object} map The map to query.
 * @param {string} key The reference key.
 * @returns {*} Returns the map data.
 */
function getMapData(map, key) {
  var data = map.__data__;
  return isKeyable(key)
    ? data[typeof key == 'string' ? 'string' : 'hash']
    : data.map;
}

module.exports = getMapData;

},{"./_isKeyable":196}],173:[function(require,module,exports){
var isStrictComparable = require('./_isStrictComparable'),
    keys = require('./keys');

/**
 * Gets the property names, values, and compare flags of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the match data of `object`.
 */
function getMatchData(object) {
  var result = keys(object),
      length = result.length;

  while (length--) {
    var key = result[length],
        value = object[key];

    result[length] = [key, value, isStrictComparable(value)];
  }
  return result;
}

module.exports = getMatchData;

},{"./_isStrictComparable":200,"./keys":283}],174:[function(require,module,exports){
var baseIsNative = require('./_baseIsNative'),
    getValue = require('./_getValue');

/**
 * Gets the native function at `key` of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {string} key The key of the method to get.
 * @returns {*} Returns the function if it's native, else `undefined`.
 */
function getNative(object, key) {
  var value = getValue(object, key);
  return baseIsNative(value) ? value : undefined;
}

module.exports = getNative;

},{"./_baseIsNative":104,"./_getValue":180}],175:[function(require,module,exports){
var overArg = require('./_overArg');

/** Built-in value references. */
var getPrototype = overArg(Object.getPrototypeOf, Object);

module.exports = getPrototype;

},{"./_overArg":221}],176:[function(require,module,exports){
var Symbol = require('./_Symbol');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Used to resolve the
 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
 * of values.
 */
var nativeObjectToString = objectProto.toString;

/** Built-in value references. */
var symToStringTag = Symbol ? Symbol.toStringTag : undefined;

/**
 * A specialized version of `baseGetTag` which ignores `Symbol.toStringTag` values.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the raw `toStringTag`.
 */
function getRawTag(value) {
  var isOwn = hasOwnProperty.call(value, symToStringTag),
      tag = value[symToStringTag];

  try {
    value[symToStringTag] = undefined;
    var unmasked = true;
  } catch (e) {}

  var result = nativeObjectToString.call(value);
  if (unmasked) {
    if (isOwn) {
      value[symToStringTag] = tag;
    } else {
      delete value[symToStringTag];
    }
  }
  return result;
}

module.exports = getRawTag;

},{"./_Symbol":66}],177:[function(require,module,exports){
var arrayFilter = require('./_arrayFilter'),
    stubArray = require('./stubArray');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Built-in value references. */
var propertyIsEnumerable = objectProto.propertyIsEnumerable;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeGetSymbols = Object.getOwnPropertySymbols;

/**
 * Creates an array of the own enumerable symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of symbols.
 */
var getSymbols = !nativeGetSymbols ? stubArray : function(object) {
  if (object == null) {
    return [];
  }
  object = Object(object);
  return arrayFilter(nativeGetSymbols(object), function(symbol) {
    return propertyIsEnumerable.call(object, symbol);
  });
};

module.exports = getSymbols;

},{"./_arrayFilter":71,"./stubArray":303}],178:[function(require,module,exports){
var arrayPush = require('./_arrayPush'),
    getPrototype = require('./_getPrototype'),
    getSymbols = require('./_getSymbols'),
    stubArray = require('./stubArray');

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeGetSymbols = Object.getOwnPropertySymbols;

/**
 * Creates an array of the own and inherited enumerable symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of symbols.
 */
var getSymbolsIn = !nativeGetSymbols ? stubArray : function(object) {
  var result = [];
  while (object) {
    arrayPush(result, getSymbols(object));
    object = getPrototype(object);
  }
  return result;
};

module.exports = getSymbolsIn;

},{"./_arrayPush":76,"./_getPrototype":175,"./_getSymbols":177,"./stubArray":303}],179:[function(require,module,exports){
var DataView = require('./_DataView'),
    Map = require('./_Map'),
    Promise = require('./_Promise'),
    Set = require('./_Set'),
    WeakMap = require('./_WeakMap'),
    baseGetTag = require('./_baseGetTag'),
    toSource = require('./_toSource');

/** `Object#toString` result references. */
var mapTag = '[object Map]',
    objectTag = '[object Object]',
    promiseTag = '[object Promise]',
    setTag = '[object Set]',
    weakMapTag = '[object WeakMap]';

var dataViewTag = '[object DataView]';

/** Used to detect maps, sets, and weakmaps. */
var dataViewCtorString = toSource(DataView),
    mapCtorString = toSource(Map),
    promiseCtorString = toSource(Promise),
    setCtorString = toSource(Set),
    weakMapCtorString = toSource(WeakMap);

/**
 * Gets the `toStringTag` of `value`.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the `toStringTag`.
 */
var getTag = baseGetTag;

// Fallback for data views, maps, sets, and weak maps in IE 11 and promises in Node.js < 6.
if ((DataView && getTag(new DataView(new ArrayBuffer(1))) != dataViewTag) ||
    (Map && getTag(new Map) != mapTag) ||
    (Promise && getTag(Promise.resolve()) != promiseTag) ||
    (Set && getTag(new Set) != setTag) ||
    (WeakMap && getTag(new WeakMap) != weakMapTag)) {
  getTag = function(value) {
    var result = baseGetTag(value),
        Ctor = result == objectTag ? value.constructor : undefined,
        ctorString = Ctor ? toSource(Ctor) : '';

    if (ctorString) {
      switch (ctorString) {
        case dataViewCtorString: return dataViewTag;
        case mapCtorString: return mapTag;
        case promiseCtorString: return promiseTag;
        case setCtorString: return setTag;
        case weakMapCtorString: return weakMapTag;
      }
    }
    return result;
  };
}

module.exports = getTag;

},{"./_DataView":55,"./_Map":60,"./_Promise":62,"./_Set":63,"./_WeakMap":68,"./_baseGetTag":95,"./_toSource":243}],180:[function(require,module,exports){
/**
 * Gets the value at `key` of `object`.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {string} key The key of the property to get.
 * @returns {*} Returns the property value.
 */
function getValue(object, key) {
  return object == null ? undefined : object[key];
}

module.exports = getValue;

},{}],181:[function(require,module,exports){
/** Used to match wrap detail comments. */
var reWrapDetails = /\{\n\/\* \[wrapped with (.+)\] \*/,
    reSplitDetails = /,? & /;

/**
 * Extracts wrapper details from the `source` body comment.
 *
 * @private
 * @param {string} source The source to inspect.
 * @returns {Array} Returns the wrapper details.
 */
function getWrapDetails(source) {
  var match = source.match(reWrapDetails);
  return match ? match[1].split(reSplitDetails) : [];
}

module.exports = getWrapDetails;

},{}],182:[function(require,module,exports){
var castPath = require('./_castPath'),
    isArguments = require('./isArguments'),
    isArray = require('./isArray'),
    isIndex = require('./_isIndex'),
    isLength = require('./isLength'),
    toKey = require('./_toKey');

/**
 * Checks if `path` exists on `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array|string} path The path to check.
 * @param {Function} hasFunc The function to check properties.
 * @returns {boolean} Returns `true` if `path` exists, else `false`.
 */
function hasPath(object, path, hasFunc) {
  path = castPath(path, object);

  var index = -1,
      length = path.length,
      result = false;

  while (++index < length) {
    var key = toKey(path[index]);
    if (!(result = object != null && hasFunc(object, key))) {
      break;
    }
    object = object[key];
  }
  if (result || ++index != length) {
    return result;
  }
  length = object == null ? 0 : object.length;
  return !!length && isLength(length) && isIndex(key, length) &&
    (isArray(object) || isArguments(object));
}

module.exports = hasPath;

},{"./_castPath":133,"./_isIndex":193,"./_toKey":242,"./isArguments":266,"./isArray":267,"./isLength":275}],183:[function(require,module,exports){
var nativeCreate = require('./_nativeCreate');

/**
 * Removes all key-value entries from the hash.
 *
 * @private
 * @name clear
 * @memberOf Hash
 */
function hashClear() {
  this.__data__ = nativeCreate ? nativeCreate(null) : {};
  this.size = 0;
}

module.exports = hashClear;

},{"./_nativeCreate":216}],184:[function(require,module,exports){
/**
 * Removes `key` and its value from the hash.
 *
 * @private
 * @name delete
 * @memberOf Hash
 * @param {Object} hash The hash to modify.
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function hashDelete(key) {
  var result = this.has(key) && delete this.__data__[key];
  this.size -= result ? 1 : 0;
  return result;
}

module.exports = hashDelete;

},{}],185:[function(require,module,exports){
var nativeCreate = require('./_nativeCreate');

/** Used to stand-in for `undefined` hash values. */
var HASH_UNDEFINED = '__lodash_hash_undefined__';

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Gets the hash value for `key`.
 *
 * @private
 * @name get
 * @memberOf Hash
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function hashGet(key) {
  var data = this.__data__;
  if (nativeCreate) {
    var result = data[key];
    return result === HASH_UNDEFINED ? undefined : result;
  }
  return hasOwnProperty.call(data, key) ? data[key] : undefined;
}

module.exports = hashGet;

},{"./_nativeCreate":216}],186:[function(require,module,exports){
var nativeCreate = require('./_nativeCreate');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Checks if a hash value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Hash
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function hashHas(key) {
  var data = this.__data__;
  return nativeCreate ? (data[key] !== undefined) : hasOwnProperty.call(data, key);
}

module.exports = hashHas;

},{"./_nativeCreate":216}],187:[function(require,module,exports){
var nativeCreate = require('./_nativeCreate');

/** Used to stand-in for `undefined` hash values. */
var HASH_UNDEFINED = '__lodash_hash_undefined__';

/**
 * Sets the hash `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Hash
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the hash instance.
 */
function hashSet(key, value) {
  var data = this.__data__;
  this.size += this.has(key) ? 0 : 1;
  data[key] = (nativeCreate && value === undefined) ? HASH_UNDEFINED : value;
  return this;
}

module.exports = hashSet;

},{"./_nativeCreate":216}],188:[function(require,module,exports){
/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Initializes an array clone.
 *
 * @private
 * @param {Array} array The array to clone.
 * @returns {Array} Returns the initialized clone.
 */
function initCloneArray(array) {
  var length = array.length,
      result = new array.constructor(length);

  // Add properties assigned by `RegExp#exec`.
  if (length && typeof array[0] == 'string' && hasOwnProperty.call(array, 'index')) {
    result.index = array.index;
    result.input = array.input;
  }
  return result;
}

module.exports = initCloneArray;

},{}],189:[function(require,module,exports){
var cloneArrayBuffer = require('./_cloneArrayBuffer'),
    cloneDataView = require('./_cloneDataView'),
    cloneRegExp = require('./_cloneRegExp'),
    cloneSymbol = require('./_cloneSymbol'),
    cloneTypedArray = require('./_cloneTypedArray');

/** `Object#toString` result references. */
var boolTag = '[object Boolean]',
    dateTag = '[object Date]',
    mapTag = '[object Map]',
    numberTag = '[object Number]',
    regexpTag = '[object RegExp]',
    setTag = '[object Set]',
    stringTag = '[object String]',
    symbolTag = '[object Symbol]';

var arrayBufferTag = '[object ArrayBuffer]',
    dataViewTag = '[object DataView]',
    float32Tag = '[object Float32Array]',
    float64Tag = '[object Float64Array]',
    int8Tag = '[object Int8Array]',
    int16Tag = '[object Int16Array]',
    int32Tag = '[object Int32Array]',
    uint8Tag = '[object Uint8Array]',
    uint8ClampedTag = '[object Uint8ClampedArray]',
    uint16Tag = '[object Uint16Array]',
    uint32Tag = '[object Uint32Array]';

/**
 * Initializes an object clone based on its `toStringTag`.
 *
 * **Note:** This function only supports cloning values with tags of
 * `Boolean`, `Date`, `Error`, `Map`, `Number`, `RegExp`, `Set`, or `String`.
 *
 * @private
 * @param {Object} object The object to clone.
 * @param {string} tag The `toStringTag` of the object to clone.
 * @param {boolean} [isDeep] Specify a deep clone.
 * @returns {Object} Returns the initialized clone.
 */
function initCloneByTag(object, tag, isDeep) {
  var Ctor = object.constructor;
  switch (tag) {
    case arrayBufferTag:
      return cloneArrayBuffer(object);

    case boolTag:
    case dateTag:
      return new Ctor(+object);

    case dataViewTag:
      return cloneDataView(object, isDeep);

    case float32Tag: case float64Tag:
    case int8Tag: case int16Tag: case int32Tag:
    case uint8Tag: case uint8ClampedTag: case uint16Tag: case uint32Tag:
      return cloneTypedArray(object, isDeep);

    case mapTag:
      return new Ctor;

    case numberTag:
    case stringTag:
      return new Ctor(object);

    case regexpTag:
      return cloneRegExp(object);

    case setTag:
      return new Ctor;

    case symbolTag:
      return cloneSymbol(object);
  }
}

module.exports = initCloneByTag;

},{"./_cloneArrayBuffer":134,"./_cloneDataView":136,"./_cloneRegExp":137,"./_cloneSymbol":138,"./_cloneTypedArray":139}],190:[function(require,module,exports){
var baseCreate = require('./_baseCreate'),
    getPrototype = require('./_getPrototype'),
    isPrototype = require('./_isPrototype');

/**
 * Initializes an object clone.
 *
 * @private
 * @param {Object} object The object to clone.
 * @returns {Object} Returns the initialized clone.
 */
function initCloneObject(object) {
  return (typeof object.constructor == 'function' && !isPrototype(object))
    ? baseCreate(getPrototype(object))
    : {};
}

module.exports = initCloneObject;

},{"./_baseCreate":85,"./_getPrototype":175,"./_isPrototype":199}],191:[function(require,module,exports){
/** Used to match wrap detail comments. */
var reWrapComment = /\{(?:\n\/\* \[wrapped with .+\] \*\/)?\n?/;

/**
 * Inserts wrapper `details` in a comment at the top of the `source` body.
 *
 * @private
 * @param {string} source The source to modify.
 * @returns {Array} details The details to insert.
 * @returns {string} Returns the modified source.
 */
function insertWrapDetails(source, details) {
  var length = details.length;
  if (!length) {
    return source;
  }
  var lastIndex = length - 1;
  details[lastIndex] = (length > 1 ? '& ' : '') + details[lastIndex];
  details = details.join(length > 2 ? ', ' : ' ');
  return source.replace(reWrapComment, '{\n/* [wrapped with ' + details + '] */\n');
}

module.exports = insertWrapDetails;

},{}],192:[function(require,module,exports){
var Symbol = require('./_Symbol'),
    isArguments = require('./isArguments'),
    isArray = require('./isArray');

/** Built-in value references. */
var spreadableSymbol = Symbol ? Symbol.isConcatSpreadable : undefined;

/**
 * Checks if `value` is a flattenable `arguments` object or array.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is flattenable, else `false`.
 */
function isFlattenable(value) {
  return isArray(value) || isArguments(value) ||
    !!(spreadableSymbol && value && value[spreadableSymbol]);
}

module.exports = isFlattenable;

},{"./_Symbol":66,"./isArguments":266,"./isArray":267}],193:[function(require,module,exports){
/** Used as references for various `Number` constants. */
var MAX_SAFE_INTEGER = 9007199254740991;

/** Used to detect unsigned integer values. */
var reIsUint = /^(?:0|[1-9]\d*)$/;

/**
 * Checks if `value` is a valid array-like index.
 *
 * @private
 * @param {*} value The value to check.
 * @param {number} [length=MAX_SAFE_INTEGER] The upper bounds of a valid index.
 * @returns {boolean} Returns `true` if `value` is a valid index, else `false`.
 */
function isIndex(value, length) {
  var type = typeof value;
  length = length == null ? MAX_SAFE_INTEGER : length;

  return !!length &&
    (type == 'number' ||
      (type != 'symbol' && reIsUint.test(value))) &&
        (value > -1 && value % 1 == 0 && value < length);
}

module.exports = isIndex;

},{}],194:[function(require,module,exports){
var eq = require('./eq'),
    isArrayLike = require('./isArrayLike'),
    isIndex = require('./_isIndex'),
    isObject = require('./isObject');

/**
 * Checks if the given arguments are from an iteratee call.
 *
 * @private
 * @param {*} value The potential iteratee value argument.
 * @param {*} index The potential iteratee index or key argument.
 * @param {*} object The potential iteratee object argument.
 * @returns {boolean} Returns `true` if the arguments are from an iteratee call,
 *  else `false`.
 */
function isIterateeCall(value, index, object) {
  if (!isObject(object)) {
    return false;
  }
  var type = typeof index;
  if (type == 'number'
        ? (isArrayLike(object) && isIndex(index, object.length))
        : (type == 'string' && index in object)
      ) {
    return eq(object[index], value);
  }
  return false;
}

module.exports = isIterateeCall;

},{"./_isIndex":193,"./eq":254,"./isArrayLike":268,"./isObject":277}],195:[function(require,module,exports){
var isArray = require('./isArray'),
    isSymbol = require('./isSymbol');

/** Used to match property names within property paths. */
var reIsDeepProp = /\.|\[(?:[^[\]]*|(["'])(?:(?!\1)[^\\]|\\.)*?\1)\]/,
    reIsPlainProp = /^\w*$/;

/**
 * Checks if `value` is a property name and not a property path.
 *
 * @private
 * @param {*} value The value to check.
 * @param {Object} [object] The object to query keys on.
 * @returns {boolean} Returns `true` if `value` is a property name, else `false`.
 */
function isKey(value, object) {
  if (isArray(value)) {
    return false;
  }
  var type = typeof value;
  if (type == 'number' || type == 'symbol' || type == 'boolean' ||
      value == null || isSymbol(value)) {
    return true;
  }
  return reIsPlainProp.test(value) || !reIsDeepProp.test(value) ||
    (object != null && value in Object(object));
}

module.exports = isKey;

},{"./isArray":267,"./isSymbol":281}],196:[function(require,module,exports){
/**
 * Checks if `value` is suitable for use as unique object key.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is suitable, else `false`.
 */
function isKeyable(value) {
  var type = typeof value;
  return (type == 'string' || type == 'number' || type == 'symbol' || type == 'boolean')
    ? (value !== '__proto__')
    : (value === null);
}

module.exports = isKeyable;

},{}],197:[function(require,module,exports){
var LazyWrapper = require('./_LazyWrapper'),
    getData = require('./_getData'),
    getFuncName = require('./_getFuncName'),
    lodash = require('./wrapperLodash');

/**
 * Checks if `func` has a lazy counterpart.
 *
 * @private
 * @param {Function} func The function to check.
 * @returns {boolean} Returns `true` if `func` has a lazy counterpart,
 *  else `false`.
 */
function isLaziable(func) {
  var funcName = getFuncName(func),
      other = lodash[funcName];

  if (typeof other != 'function' || !(funcName in LazyWrapper.prototype)) {
    return false;
  }
  if (func === other) {
    return true;
  }
  var data = getData(other);
  return !!data && func === data[0];
}

module.exports = isLaziable;

},{"./_LazyWrapper":57,"./_getData":169,"./_getFuncName":170,"./wrapperLodash":312}],198:[function(require,module,exports){
var coreJsData = require('./_coreJsData');

/** Used to detect methods masquerading as native. */
var maskSrcKey = (function() {
  var uid = /[^.]+$/.exec(coreJsData && coreJsData.keys && coreJsData.keys.IE_PROTO || '');
  return uid ? ('Symbol(src)_1.' + uid) : '';
}());

/**
 * Checks if `func` has its source masked.
 *
 * @private
 * @param {Function} func The function to check.
 * @returns {boolean} Returns `true` if `func` is masked, else `false`.
 */
function isMasked(func) {
  return !!maskSrcKey && (maskSrcKey in func);
}

module.exports = isMasked;

},{"./_coreJsData":146}],199:[function(require,module,exports){
/** Used for built-in method references. */
var objectProto = Object.prototype;

/**
 * Checks if `value` is likely a prototype object.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a prototype, else `false`.
 */
function isPrototype(value) {
  var Ctor = value && value.constructor,
      proto = (typeof Ctor == 'function' && Ctor.prototype) || objectProto;

  return value === proto;
}

module.exports = isPrototype;

},{}],200:[function(require,module,exports){
var isObject = require('./isObject');

/**
 * Checks if `value` is suitable for strict equality comparisons, i.e. `===`.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` if suitable for strict
 *  equality comparisons, else `false`.
 */
function isStrictComparable(value) {
  return value === value && !isObject(value);
}

module.exports = isStrictComparable;

},{"./isObject":277}],201:[function(require,module,exports){
/**
 * Removes all key-value entries from the list cache.
 *
 * @private
 * @name clear
 * @memberOf ListCache
 */
function listCacheClear() {
  this.__data__ = [];
  this.size = 0;
}

module.exports = listCacheClear;

},{}],202:[function(require,module,exports){
var assocIndexOf = require('./_assocIndexOf');

/** Used for built-in method references. */
var arrayProto = Array.prototype;

/** Built-in value references. */
var splice = arrayProto.splice;

/**
 * Removes `key` and its value from the list cache.
 *
 * @private
 * @name delete
 * @memberOf ListCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function listCacheDelete(key) {
  var data = this.__data__,
      index = assocIndexOf(data, key);

  if (index < 0) {
    return false;
  }
  var lastIndex = data.length - 1;
  if (index == lastIndex) {
    data.pop();
  } else {
    splice.call(data, index, 1);
  }
  --this.size;
  return true;
}

module.exports = listCacheDelete;

},{"./_assocIndexOf":80}],203:[function(require,module,exports){
var assocIndexOf = require('./_assocIndexOf');

/**
 * Gets the list cache value for `key`.
 *
 * @private
 * @name get
 * @memberOf ListCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function listCacheGet(key) {
  var data = this.__data__,
      index = assocIndexOf(data, key);

  return index < 0 ? undefined : data[index][1];
}

module.exports = listCacheGet;

},{"./_assocIndexOf":80}],204:[function(require,module,exports){
var assocIndexOf = require('./_assocIndexOf');

/**
 * Checks if a list cache value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf ListCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function listCacheHas(key) {
  return assocIndexOf(this.__data__, key) > -1;
}

module.exports = listCacheHas;

},{"./_assocIndexOf":80}],205:[function(require,module,exports){
var assocIndexOf = require('./_assocIndexOf');

/**
 * Sets the list cache `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf ListCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the list cache instance.
 */
function listCacheSet(key, value) {
  var data = this.__data__,
      index = assocIndexOf(data, key);

  if (index < 0) {
    ++this.size;
    data.push([key, value]);
  } else {
    data[index][1] = value;
  }
  return this;
}

module.exports = listCacheSet;

},{"./_assocIndexOf":80}],206:[function(require,module,exports){
var Hash = require('./_Hash'),
    ListCache = require('./_ListCache'),
    Map = require('./_Map');

/**
 * Removes all key-value entries from the map.
 *
 * @private
 * @name clear
 * @memberOf MapCache
 */
function mapCacheClear() {
  this.size = 0;
  this.__data__ = {
    'hash': new Hash,
    'map': new (Map || ListCache),
    'string': new Hash
  };
}

module.exports = mapCacheClear;

},{"./_Hash":56,"./_ListCache":58,"./_Map":60}],207:[function(require,module,exports){
var getMapData = require('./_getMapData');

/**
 * Removes `key` and its value from the map.
 *
 * @private
 * @name delete
 * @memberOf MapCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function mapCacheDelete(key) {
  var result = getMapData(this, key)['delete'](key);
  this.size -= result ? 1 : 0;
  return result;
}

module.exports = mapCacheDelete;

},{"./_getMapData":172}],208:[function(require,module,exports){
var getMapData = require('./_getMapData');

/**
 * Gets the map value for `key`.
 *
 * @private
 * @name get
 * @memberOf MapCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function mapCacheGet(key) {
  return getMapData(this, key).get(key);
}

module.exports = mapCacheGet;

},{"./_getMapData":172}],209:[function(require,module,exports){
var getMapData = require('./_getMapData');

/**
 * Checks if a map value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf MapCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function mapCacheHas(key) {
  return getMapData(this, key).has(key);
}

module.exports = mapCacheHas;

},{"./_getMapData":172}],210:[function(require,module,exports){
var getMapData = require('./_getMapData');

/**
 * Sets the map `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf MapCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the map cache instance.
 */
function mapCacheSet(key, value) {
  var data = getMapData(this, key),
      size = data.size;

  data.set(key, value);
  this.size += data.size == size ? 0 : 1;
  return this;
}

module.exports = mapCacheSet;

},{"./_getMapData":172}],211:[function(require,module,exports){
/**
 * Converts `map` to its key-value pairs.
 *
 * @private
 * @param {Object} map The map to convert.
 * @returns {Array} Returns the key-value pairs.
 */
function mapToArray(map) {
  var index = -1,
      result = Array(map.size);

  map.forEach(function(value, key) {
    result[++index] = [key, value];
  });
  return result;
}

module.exports = mapToArray;

},{}],212:[function(require,module,exports){
/**
 * A specialized version of `matchesProperty` for source values suitable
 * for strict equality comparisons, i.e. `===`.
 *
 * @private
 * @param {string} key The key of the property to get.
 * @param {*} srcValue The value to match.
 * @returns {Function} Returns the new spec function.
 */
function matchesStrictComparable(key, srcValue) {
  return function(object) {
    if (object == null) {
      return false;
    }
    return object[key] === srcValue &&
      (srcValue !== undefined || (key in Object(object)));
  };
}

module.exports = matchesStrictComparable;

},{}],213:[function(require,module,exports){
var memoize = require('./memoize');

/** Used as the maximum memoize cache size. */
var MAX_MEMOIZE_SIZE = 500;

/**
 * A specialized version of `_.memoize` which clears the memoized function's
 * cache when it exceeds `MAX_MEMOIZE_SIZE`.
 *
 * @private
 * @param {Function} func The function to have its output memoized.
 * @returns {Function} Returns the new memoized function.
 */
function memoizeCapped(func) {
  var result = memoize(func, function(key) {
    if (cache.size === MAX_MEMOIZE_SIZE) {
      cache.clear();
    }
    return key;
  });

  var cache = result.cache;
  return result;
}

module.exports = memoizeCapped;

},{"./memoize":287}],214:[function(require,module,exports){
var composeArgs = require('./_composeArgs'),
    composeArgsRight = require('./_composeArgsRight'),
    replaceHolders = require('./_replaceHolders');

/** Used as the internal argument placeholder. */
var PLACEHOLDER = '__lodash_placeholder__';

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1,
    WRAP_BIND_KEY_FLAG = 2,
    WRAP_CURRY_BOUND_FLAG = 4,
    WRAP_CURRY_FLAG = 8,
    WRAP_ARY_FLAG = 128,
    WRAP_REARG_FLAG = 256;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMin = Math.min;

/**
 * Merges the function metadata of `source` into `data`.
 *
 * Merging metadata reduces the number of wrappers used to invoke a function.
 * This is possible because methods like `_.bind`, `_.curry`, and `_.partial`
 * may be applied regardless of execution order. Methods like `_.ary` and
 * `_.rearg` modify function arguments, making the order in which they are
 * executed important, preventing the merging of metadata. However, we make
 * an exception for a safe combined case where curried functions have `_.ary`
 * and or `_.rearg` applied.
 *
 * @private
 * @param {Array} data The destination metadata.
 * @param {Array} source The source metadata.
 * @returns {Array} Returns `data`.
 */
function mergeData(data, source) {
  var bitmask = data[1],
      srcBitmask = source[1],
      newBitmask = bitmask | srcBitmask,
      isCommon = newBitmask < (WRAP_BIND_FLAG | WRAP_BIND_KEY_FLAG | WRAP_ARY_FLAG);

  var isCombo =
    ((srcBitmask == WRAP_ARY_FLAG) && (bitmask == WRAP_CURRY_FLAG)) ||
    ((srcBitmask == WRAP_ARY_FLAG) && (bitmask == WRAP_REARG_FLAG) && (data[7].length <= source[8])) ||
    ((srcBitmask == (WRAP_ARY_FLAG | WRAP_REARG_FLAG)) && (source[7].length <= source[8]) && (bitmask == WRAP_CURRY_FLAG));

  // Exit early if metadata can't be merged.
  if (!(isCommon || isCombo)) {
    return data;
  }
  // Use source `thisArg` if available.
  if (srcBitmask & WRAP_BIND_FLAG) {
    data[2] = source[2];
    // Set when currying a bound function.
    newBitmask |= bitmask & WRAP_BIND_FLAG ? 0 : WRAP_CURRY_BOUND_FLAG;
  }
  // Compose partial arguments.
  var value = source[3];
  if (value) {
    var partials = data[3];
    data[3] = partials ? composeArgs(partials, value, source[4]) : value;
    data[4] = partials ? replaceHolders(data[3], PLACEHOLDER) : source[4];
  }
  // Compose partial right arguments.
  value = source[5];
  if (value) {
    partials = data[5];
    data[5] = partials ? composeArgsRight(partials, value, source[6]) : value;
    data[6] = partials ? replaceHolders(data[5], PLACEHOLDER) : source[6];
  }
  // Use source `argPos` if available.
  value = source[7];
  if (value) {
    data[7] = value;
  }
  // Use source `ary` if it's smaller.
  if (srcBitmask & WRAP_ARY_FLAG) {
    data[8] = data[8] == null ? source[8] : nativeMin(data[8], source[8]);
  }
  // Use source `arity` if one is not provided.
  if (data[9] == null) {
    data[9] = source[9];
  }
  // Use source `func` and merge bitmasks.
  data[0] = source[0];
  data[1] = newBitmask;

  return data;
}

module.exports = mergeData;

},{"./_composeArgs":140,"./_composeArgsRight":141,"./_replaceHolders":226}],215:[function(require,module,exports){
var WeakMap = require('./_WeakMap');

/** Used to store function metadata. */
var metaMap = WeakMap && new WeakMap;

module.exports = metaMap;

},{"./_WeakMap":68}],216:[function(require,module,exports){
var getNative = require('./_getNative');

/* Built-in method references that are verified to be native. */
var nativeCreate = getNative(Object, 'create');

module.exports = nativeCreate;

},{"./_getNative":174}],217:[function(require,module,exports){
var overArg = require('./_overArg');

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeKeys = overArg(Object.keys, Object);

module.exports = nativeKeys;

},{"./_overArg":221}],218:[function(require,module,exports){
/**
 * This function is like
 * [`Object.keys`](http://ecma-international.org/ecma-262/7.0/#sec-object.keys)
 * except that it includes inherited enumerable properties.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 */
function nativeKeysIn(object) {
  var result = [];
  if (object != null) {
    for (var key in Object(object)) {
      result.push(key);
    }
  }
  return result;
}

module.exports = nativeKeysIn;

},{}],219:[function(require,module,exports){
var freeGlobal = require('./_freeGlobal');

/** Detect free variable `exports`. */
var freeExports = typeof exports == 'object' && exports && !exports.nodeType && exports;

/** Detect free variable `module`. */
var freeModule = freeExports && typeof module == 'object' && module && !module.nodeType && module;

/** Detect the popular CommonJS extension `module.exports`. */
var moduleExports = freeModule && freeModule.exports === freeExports;

/** Detect free variable `process` from Node.js. */
var freeProcess = moduleExports && freeGlobal.process;

/** Used to access faster Node.js helpers. */
var nodeUtil = (function() {
  try {
    // Use `util.types` for Node.js 10+.
    var types = freeModule && freeModule.require && freeModule.require('util').types;

    if (types) {
      return types;
    }

    // Legacy `process.binding('util')` for Node.js < 10.
    return freeProcess && freeProcess.binding && freeProcess.binding('util');
  } catch (e) {}
}());

module.exports = nodeUtil;

},{"./_freeGlobal":166}],220:[function(require,module,exports){
/** Used for built-in method references. */
var objectProto = Object.prototype;

/**
 * Used to resolve the
 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
 * of values.
 */
var nativeObjectToString = objectProto.toString;

/**
 * Converts `value` to a string using `Object.prototype.toString`.
 *
 * @private
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 */
function objectToString(value) {
  return nativeObjectToString.call(value);
}

module.exports = objectToString;

},{}],221:[function(require,module,exports){
/**
 * Creates a unary function that invokes `func` with its argument transformed.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {Function} transform The argument transform.
 * @returns {Function} Returns the new function.
 */
function overArg(func, transform) {
  return function(arg) {
    return func(transform(arg));
  };
}

module.exports = overArg;

},{}],222:[function(require,module,exports){
var apply = require('./_apply');

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * A specialized version of `baseRest` which transforms the rest array.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @param {Function} transform The rest array transform.
 * @returns {Function} Returns the new function.
 */
function overRest(func, start, transform) {
  start = nativeMax(start === undefined ? (func.length - 1) : start, 0);
  return function() {
    var args = arguments,
        index = -1,
        length = nativeMax(args.length - start, 0),
        array = Array(length);

    while (++index < length) {
      array[index] = args[start + index];
    }
    index = -1;
    var otherArgs = Array(start + 1);
    while (++index < start) {
      otherArgs[index] = args[index];
    }
    otherArgs[start] = transform(array);
    return apply(func, this, otherArgs);
  };
}

module.exports = overRest;

},{"./_apply":69}],223:[function(require,module,exports){
var baseGet = require('./_baseGet'),
    baseSlice = require('./_baseSlice');

/**
 * Gets the parent value at `path` of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array} path The path to get the parent value of.
 * @returns {*} Returns the parent value.
 */
function parent(object, path) {
  return path.length < 2 ? object : baseGet(object, baseSlice(path, 0, -1));
}

module.exports = parent;

},{"./_baseGet":93,"./_baseSlice":124}],224:[function(require,module,exports){
/** Used to lookup unminified function names. */
var realNames = {};

module.exports = realNames;

},{}],225:[function(require,module,exports){
var copyArray = require('./_copyArray'),
    isIndex = require('./_isIndex');

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMin = Math.min;

/**
 * Reorder `array` according to the specified indexes where the element at
 * the first index is assigned as the first element, the element at
 * the second index is assigned as the second element, and so on.
 *
 * @private
 * @param {Array} array The array to reorder.
 * @param {Array} indexes The arranged array indexes.
 * @returns {Array} Returns `array`.
 */
function reorder(array, indexes) {
  var arrLength = array.length,
      length = nativeMin(indexes.length, arrLength),
      oldArray = copyArray(array);

  while (length--) {
    var index = indexes[length];
    array[length] = isIndex(index, arrLength) ? oldArray[index] : undefined;
  }
  return array;
}

module.exports = reorder;

},{"./_copyArray":142,"./_isIndex":193}],226:[function(require,module,exports){
/** Used as the internal argument placeholder. */
var PLACEHOLDER = '__lodash_placeholder__';

/**
 * Replaces all `placeholder` elements in `array` with an internal placeholder
 * and returns an array of their indexes.
 *
 * @private
 * @param {Array} array The array to modify.
 * @param {*} placeholder The placeholder to replace.
 * @returns {Array} Returns the new array of placeholder indexes.
 */
function replaceHolders(array, placeholder) {
  var index = -1,
      length = array.length,
      resIndex = 0,
      result = [];

  while (++index < length) {
    var value = array[index];
    if (value === placeholder || value === PLACEHOLDER) {
      array[index] = PLACEHOLDER;
      result[resIndex++] = index;
    }
  }
  return result;
}

module.exports = replaceHolders;

},{}],227:[function(require,module,exports){
var freeGlobal = require('./_freeGlobal');

/** Detect free variable `self`. */
var freeSelf = typeof self == 'object' && self && self.Object === Object && self;

/** Used as a reference to the global object. */
var root = freeGlobal || freeSelf || Function('return this')();

module.exports = root;

},{"./_freeGlobal":166}],228:[function(require,module,exports){
/** Used to stand-in for `undefined` hash values. */
var HASH_UNDEFINED = '__lodash_hash_undefined__';

/**
 * Adds `value` to the array cache.
 *
 * @private
 * @name add
 * @memberOf SetCache
 * @alias push
 * @param {*} value The value to cache.
 * @returns {Object} Returns the cache instance.
 */
function setCacheAdd(value) {
  this.__data__.set(value, HASH_UNDEFINED);
  return this;
}

module.exports = setCacheAdd;

},{}],229:[function(require,module,exports){
/**
 * Checks if `value` is in the array cache.
 *
 * @private
 * @name has
 * @memberOf SetCache
 * @param {*} value The value to search for.
 * @returns {number} Returns `true` if `value` is found, else `false`.
 */
function setCacheHas(value) {
  return this.__data__.has(value);
}

module.exports = setCacheHas;

},{}],230:[function(require,module,exports){
var baseSetData = require('./_baseSetData'),
    shortOut = require('./_shortOut');

/**
 * Sets metadata for `func`.
 *
 * **Note:** If this function becomes hot, i.e. is invoked a lot in a short
 * period of time, it will trip its breaker and transition to an identity
 * function to avoid garbage collection pauses in V8. See
 * [V8 issue 2070](https://bugs.chromium.org/p/v8/issues/detail?id=2070)
 * for more details.
 *
 * @private
 * @param {Function} func The function to associate metadata with.
 * @param {*} data The metadata.
 * @returns {Function} Returns `func`.
 */
var setData = shortOut(baseSetData);

module.exports = setData;

},{"./_baseSetData":122,"./_shortOut":234}],231:[function(require,module,exports){
/**
 * Converts `set` to an array of its values.
 *
 * @private
 * @param {Object} set The set to convert.
 * @returns {Array} Returns the values.
 */
function setToArray(set) {
  var index = -1,
      result = Array(set.size);

  set.forEach(function(value) {
    result[++index] = value;
  });
  return result;
}

module.exports = setToArray;

},{}],232:[function(require,module,exports){
var baseSetToString = require('./_baseSetToString'),
    shortOut = require('./_shortOut');

/**
 * Sets the `toString` method of `func` to return `string`.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */
var setToString = shortOut(baseSetToString);

module.exports = setToString;

},{"./_baseSetToString":123,"./_shortOut":234}],233:[function(require,module,exports){
var getWrapDetails = require('./_getWrapDetails'),
    insertWrapDetails = require('./_insertWrapDetails'),
    setToString = require('./_setToString'),
    updateWrapDetails = require('./_updateWrapDetails');

/**
 * Sets the `toString` method of `wrapper` to mimic the source of `reference`
 * with wrapper details in a comment at the top of the source body.
 *
 * @private
 * @param {Function} wrapper The function to modify.
 * @param {Function} reference The reference function.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @returns {Function} Returns `wrapper`.
 */
function setWrapToString(wrapper, reference, bitmask) {
  var source = (reference + '');
  return setToString(wrapper, insertWrapDetails(source, updateWrapDetails(getWrapDetails(source), bitmask)));
}

module.exports = setWrapToString;

},{"./_getWrapDetails":181,"./_insertWrapDetails":191,"./_setToString":232,"./_updateWrapDetails":245}],234:[function(require,module,exports){
/** Used to detect hot functions by number of calls within a span of milliseconds. */
var HOT_COUNT = 800,
    HOT_SPAN = 16;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeNow = Date.now;

/**
 * Creates a function that'll short out and invoke `identity` instead
 * of `func` when it's called `HOT_COUNT` or more times in `HOT_SPAN`
 * milliseconds.
 *
 * @private
 * @param {Function} func The function to restrict.
 * @returns {Function} Returns the new shortable function.
 */
function shortOut(func) {
  var count = 0,
      lastCalled = 0;

  return function() {
    var stamp = nativeNow(),
        remaining = HOT_SPAN - (stamp - lastCalled);

    lastCalled = stamp;
    if (remaining > 0) {
      if (++count >= HOT_COUNT) {
        return arguments[0];
      }
    } else {
      count = 0;
    }
    return func.apply(undefined, arguments);
  };
}

module.exports = shortOut;

},{}],235:[function(require,module,exports){
var ListCache = require('./_ListCache');

/**
 * Removes all key-value entries from the stack.
 *
 * @private
 * @name clear
 * @memberOf Stack
 */
function stackClear() {
  this.__data__ = new ListCache;
  this.size = 0;
}

module.exports = stackClear;

},{"./_ListCache":58}],236:[function(require,module,exports){
/**
 * Removes `key` and its value from the stack.
 *
 * @private
 * @name delete
 * @memberOf Stack
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function stackDelete(key) {
  var data = this.__data__,
      result = data['delete'](key);

  this.size = data.size;
  return result;
}

module.exports = stackDelete;

},{}],237:[function(require,module,exports){
/**
 * Gets the stack value for `key`.
 *
 * @private
 * @name get
 * @memberOf Stack
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function stackGet(key) {
  return this.__data__.get(key);
}

module.exports = stackGet;

},{}],238:[function(require,module,exports){
/**
 * Checks if a stack value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Stack
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function stackHas(key) {
  return this.__data__.has(key);
}

module.exports = stackHas;

},{}],239:[function(require,module,exports){
var ListCache = require('./_ListCache'),
    Map = require('./_Map'),
    MapCache = require('./_MapCache');

/** Used as the size to enable large array optimizations. */
var LARGE_ARRAY_SIZE = 200;

/**
 * Sets the stack `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Stack
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the stack cache instance.
 */
function stackSet(key, value) {
  var data = this.__data__;
  if (data instanceof ListCache) {
    var pairs = data.__data__;
    if (!Map || (pairs.length < LARGE_ARRAY_SIZE - 1)) {
      pairs.push([key, value]);
      this.size = ++data.size;
      return this;
    }
    data = this.__data__ = new MapCache(pairs);
  }
  data.set(key, value);
  this.size = data.size;
  return this;
}

module.exports = stackSet;

},{"./_ListCache":58,"./_Map":60,"./_MapCache":61}],240:[function(require,module,exports){
/**
 * A specialized version of `_.indexOf` which performs strict equality
 * comparisons of values, i.e. `===`.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} value The value to search for.
 * @param {number} fromIndex The index to search from.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */
function strictIndexOf(array, value, fromIndex) {
  var index = fromIndex - 1,
      length = array.length;

  while (++index < length) {
    if (array[index] === value) {
      return index;
    }
  }
  return -1;
}

module.exports = strictIndexOf;

},{}],241:[function(require,module,exports){
var memoizeCapped = require('./_memoizeCapped');

/** Used to match property names within property paths. */
var rePropName = /[^.[\]]+|\[(?:(-?\d+(?:\.\d+)?)|(["'])((?:(?!\2)[^\\]|\\.)*?)\2)\]|(?=(?:\.|\[\])(?:\.|\[\]|$))/g;

/** Used to match backslashes in property paths. */
var reEscapeChar = /\\(\\)?/g;

/**
 * Converts `string` to a property path array.
 *
 * @private
 * @param {string} string The string to convert.
 * @returns {Array} Returns the property path array.
 */
var stringToPath = memoizeCapped(function(string) {
  var result = [];
  if (string.charCodeAt(0) === 46 /* . */) {
    result.push('');
  }
  string.replace(rePropName, function(match, number, quote, subString) {
    result.push(quote ? subString.replace(reEscapeChar, '$1') : (number || match));
  });
  return result;
});

module.exports = stringToPath;

},{"./_memoizeCapped":213}],242:[function(require,module,exports){
var isSymbol = require('./isSymbol');

/** Used as references for various `Number` constants. */
var INFINITY = 1 / 0;

/**
 * Converts `value` to a string key if it's not a string or symbol.
 *
 * @private
 * @param {*} value The value to inspect.
 * @returns {string|symbol} Returns the key.
 */
function toKey(value) {
  if (typeof value == 'string' || isSymbol(value)) {
    return value;
  }
  var result = (value + '');
  return (result == '0' && (1 / value) == -INFINITY) ? '-0' : result;
}

module.exports = toKey;

},{"./isSymbol":281}],243:[function(require,module,exports){
/** Used for built-in method references. */
var funcProto = Function.prototype;

/** Used to resolve the decompiled source of functions. */
var funcToString = funcProto.toString;

/**
 * Converts `func` to its source code.
 *
 * @private
 * @param {Function} func The function to convert.
 * @returns {string} Returns the source code.
 */
function toSource(func) {
  if (func != null) {
    try {
      return funcToString.call(func);
    } catch (e) {}
    try {
      return (func + '');
    } catch (e) {}
  }
  return '';
}

module.exports = toSource;

},{}],244:[function(require,module,exports){
/** Used to match a single whitespace character. */
var reWhitespace = /\s/;

/**
 * Used by `_.trim` and `_.trimEnd` to get the index of the last non-whitespace
 * character of `string`.
 *
 * @private
 * @param {string} string The string to inspect.
 * @returns {number} Returns the index of the last non-whitespace character.
 */
function trimmedEndIndex(string) {
  var index = string.length;

  while (index-- && reWhitespace.test(string.charAt(index))) {}
  return index;
}

module.exports = trimmedEndIndex;

},{}],245:[function(require,module,exports){
var arrayEach = require('./_arrayEach'),
    arrayIncludes = require('./_arrayIncludes');

/** Used to compose bitmasks for function metadata. */
var WRAP_BIND_FLAG = 1,
    WRAP_BIND_KEY_FLAG = 2,
    WRAP_CURRY_FLAG = 8,
    WRAP_CURRY_RIGHT_FLAG = 16,
    WRAP_PARTIAL_FLAG = 32,
    WRAP_PARTIAL_RIGHT_FLAG = 64,
    WRAP_ARY_FLAG = 128,
    WRAP_REARG_FLAG = 256,
    WRAP_FLIP_FLAG = 512;

/** Used to associate wrap methods with their bit flags. */
var wrapFlags = [
  ['ary', WRAP_ARY_FLAG],
  ['bind', WRAP_BIND_FLAG],
  ['bindKey', WRAP_BIND_KEY_FLAG],
  ['curry', WRAP_CURRY_FLAG],
  ['curryRight', WRAP_CURRY_RIGHT_FLAG],
  ['flip', WRAP_FLIP_FLAG],
  ['partial', WRAP_PARTIAL_FLAG],
  ['partialRight', WRAP_PARTIAL_RIGHT_FLAG],
  ['rearg', WRAP_REARG_FLAG]
];

/**
 * Updates wrapper `details` based on `bitmask` flags.
 *
 * @private
 * @returns {Array} details The details to modify.
 * @param {number} bitmask The bitmask flags. See `createWrap` for more details.
 * @returns {Array} Returns `details`.
 */
function updateWrapDetails(details, bitmask) {
  arrayEach(wrapFlags, function(pair) {
    var value = '_.' + pair[0];
    if ((bitmask & pair[1]) && !arrayIncludes(details, value)) {
      details.push(value);
    }
  });
  return details.sort();
}

module.exports = updateWrapDetails;

},{"./_arrayEach":70,"./_arrayIncludes":72}],246:[function(require,module,exports){
var LazyWrapper = require('./_LazyWrapper'),
    LodashWrapper = require('./_LodashWrapper'),
    copyArray = require('./_copyArray');

/**
 * Creates a clone of `wrapper`.
 *
 * @private
 * @param {Object} wrapper The wrapper to clone.
 * @returns {Object} Returns the cloned wrapper.
 */
function wrapperClone(wrapper) {
  if (wrapper instanceof LazyWrapper) {
    return wrapper.clone();
  }
  var result = new LodashWrapper(wrapper.__wrapped__, wrapper.__chain__);
  result.__actions__ = copyArray(wrapper.__actions__);
  result.__index__  = wrapper.__index__;
  result.__values__ = wrapper.__values__;
  return result;
}

module.exports = wrapperClone;

},{"./_LazyWrapper":57,"./_LodashWrapper":59,"./_copyArray":142}],247:[function(require,module,exports){
var assignValue = require('./_assignValue'),
    copyObject = require('./_copyObject'),
    createAssigner = require('./_createAssigner'),
    isArrayLike = require('./isArrayLike'),
    isPrototype = require('./_isPrototype'),
    keys = require('./keys');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Assigns own enumerable string keyed properties of source objects to the
 * destination object. Source objects are applied from left to right.
 * Subsequent sources overwrite property assignments of previous sources.
 *
 * **Note:** This method mutates `object` and is loosely based on
 * [`Object.assign`](https://mdn.io/Object/assign).
 *
 * @static
 * @memberOf _
 * @since 0.10.0
 * @category Object
 * @param {Object} object The destination object.
 * @param {...Object} [sources] The source objects.
 * @returns {Object} Returns `object`.
 * @see _.assignIn
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 * }
 *
 * function Bar() {
 *   this.c = 3;
 * }
 *
 * Foo.prototype.b = 2;
 * Bar.prototype.d = 4;
 *
 * _.assign({ 'a': 0 }, new Foo, new Bar);
 * // => { 'a': 1, 'c': 3 }
 */
var assign = createAssigner(function(object, source) {
  if (isPrototype(source) || isArrayLike(source)) {
    copyObject(source, keys(source), object);
    return;
  }
  for (var key in source) {
    if (hasOwnProperty.call(source, key)) {
      assignValue(object, key, source[key]);
    }
  }
});

module.exports = assign;

},{"./_assignValue":79,"./_copyObject":143,"./_createAssigner":148,"./_isPrototype":199,"./isArrayLike":268,"./keys":283}],248:[function(require,module,exports){
var copyObject = require('./_copyObject'),
    createAssigner = require('./_createAssigner'),
    keysIn = require('./keysIn');

/**
 * This method is like `_.assign` except that it iterates over own and
 * inherited source properties.
 *
 * **Note:** This method mutates `object`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @alias extend
 * @category Object
 * @param {Object} object The destination object.
 * @param {...Object} [sources] The source objects.
 * @returns {Object} Returns `object`.
 * @see _.assign
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 * }
 *
 * function Bar() {
 *   this.c = 3;
 * }
 *
 * Foo.prototype.b = 2;
 * Bar.prototype.d = 4;
 *
 * _.assignIn({ 'a': 0 }, new Foo, new Bar);
 * // => { 'a': 1, 'b': 2, 'c': 3, 'd': 4 }
 */
var assignIn = createAssigner(function(object, source) {
  copyObject(source, keysIn(source), object);
});

module.exports = assignIn;

},{"./_copyObject":143,"./_createAssigner":148,"./keysIn":284}],249:[function(require,module,exports){
var toInteger = require('./toInteger');

/** Error message constants. */
var FUNC_ERROR_TEXT = 'Expected a function';

/**
 * Creates a function that invokes `func`, with the `this` binding and arguments
 * of the created function, while it's called less than `n` times. Subsequent
 * calls to the created function return the result of the last `func` invocation.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Function
 * @param {number} n The number of calls at which `func` is no longer invoked.
 * @param {Function} func The function to restrict.
 * @returns {Function} Returns the new restricted function.
 * @example
 *
 * jQuery(element).on('click', _.before(5, addContactToList));
 * // => Allows adding up to 4 contacts to the list.
 */
function before(n, func) {
  var result;
  if (typeof func != 'function') {
    throw new TypeError(FUNC_ERROR_TEXT);
  }
  n = toInteger(n);
  return function() {
    if (--n > 0) {
      result = func.apply(this, arguments);
    }
    if (n <= 1) {
      func = undefined;
    }
    return result;
  };
}

module.exports = before;

},{"./toInteger":306}],250:[function(require,module,exports){
/**
 * Creates a function that returns `value`.
 *
 * @static
 * @memberOf _
 * @since 2.4.0
 * @category Util
 * @param {*} value The value to return from the new function.
 * @returns {Function} Returns the new constant function.
 * @example
 *
 * var objects = _.times(2, _.constant({ 'a': 1 }));
 *
 * console.log(objects);
 * // => [{ 'a': 1 }, { 'a': 1 }]
 *
 * console.log(objects[0] === objects[1]);
 * // => true
 */
function constant(value) {
  return function() {
    return value;
  };
}

module.exports = constant;

},{}],251:[function(require,module,exports){
var isObject = require('./isObject'),
    now = require('./now'),
    toNumber = require('./toNumber');

/** Error message constants. */
var FUNC_ERROR_TEXT = 'Expected a function';

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max,
    nativeMin = Math.min;

/**
 * Creates a debounced function that delays invoking `func` until after `wait`
 * milliseconds have elapsed since the last time the debounced function was
 * invoked. The debounced function comes with a `cancel` method to cancel
 * delayed `func` invocations and a `flush` method to immediately invoke them.
 * Provide `options` to indicate whether `func` should be invoked on the
 * leading and/or trailing edge of the `wait` timeout. The `func` is invoked
 * with the last arguments provided to the debounced function. Subsequent
 * calls to the debounced function return the result of the last `func`
 * invocation.
 *
 * **Note:** If `leading` and `trailing` options are `true`, `func` is
 * invoked on the trailing edge of the timeout only if the debounced function
 * is invoked more than once during the `wait` timeout.
 *
 * If `wait` is `0` and `leading` is `false`, `func` invocation is deferred
 * until to the next tick, similar to `setTimeout` with a timeout of `0`.
 *
 * See [David Corbacho's article](https://css-tricks.com/debouncing-throttling-explained-examples/)
 * for details over the differences between `_.debounce` and `_.throttle`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Function
 * @param {Function} func The function to debounce.
 * @param {number} [wait=0] The number of milliseconds to delay.
 * @param {Object} [options={}] The options object.
 * @param {boolean} [options.leading=false]
 *  Specify invoking on the leading edge of the timeout.
 * @param {number} [options.maxWait]
 *  The maximum time `func` is allowed to be delayed before it's invoked.
 * @param {boolean} [options.trailing=true]
 *  Specify invoking on the trailing edge of the timeout.
 * @returns {Function} Returns the new debounced function.
 * @example
 *
 * // Avoid costly calculations while the window size is in flux.
 * jQuery(window).on('resize', _.debounce(calculateLayout, 150));
 *
 * // Invoke `sendMail` when clicked, debouncing subsequent calls.
 * jQuery(element).on('click', _.debounce(sendMail, 300, {
 *   'leading': true,
 *   'trailing': false
 * }));
 *
 * // Ensure `batchLog` is invoked once after 1 second of debounced calls.
 * var debounced = _.debounce(batchLog, 250, { 'maxWait': 1000 });
 * var source = new EventSource('/stream');
 * jQuery(source).on('message', debounced);
 *
 * // Cancel the trailing debounced invocation.
 * jQuery(window).on('popstate', debounced.cancel);
 */
function debounce(func, wait, options) {
  var lastArgs,
      lastThis,
      maxWait,
      result,
      timerId,
      lastCallTime,
      lastInvokeTime = 0,
      leading = false,
      maxing = false,
      trailing = true;

  if (typeof func != 'function') {
    throw new TypeError(FUNC_ERROR_TEXT);
  }
  wait = toNumber(wait) || 0;
  if (isObject(options)) {
    leading = !!options.leading;
    maxing = 'maxWait' in options;
    maxWait = maxing ? nativeMax(toNumber(options.maxWait) || 0, wait) : maxWait;
    trailing = 'trailing' in options ? !!options.trailing : trailing;
  }

  function invokeFunc(time) {
    var args = lastArgs,
        thisArg = lastThis;

    lastArgs = lastThis = undefined;
    lastInvokeTime = time;
    result = func.apply(thisArg, args);
    return result;
  }

  function leadingEdge(time) {
    // Reset any `maxWait` timer.
    lastInvokeTime = time;
    // Start the timer for the trailing edge.
    timerId = setTimeout(timerExpired, wait);
    // Invoke the leading edge.
    return leading ? invokeFunc(time) : result;
  }

  function remainingWait(time) {
    var timeSinceLastCall = time - lastCallTime,
        timeSinceLastInvoke = time - lastInvokeTime,
        timeWaiting = wait - timeSinceLastCall;

    return maxing
      ? nativeMin(timeWaiting, maxWait - timeSinceLastInvoke)
      : timeWaiting;
  }

  function shouldInvoke(time) {
    var timeSinceLastCall = time - lastCallTime,
        timeSinceLastInvoke = time - lastInvokeTime;

    // Either this is the first call, activity has stopped and we're at the
    // trailing edge, the system time has gone backwards and we're treating
    // it as the trailing edge, or we've hit the `maxWait` limit.
    return (lastCallTime === undefined || (timeSinceLastCall >= wait) ||
      (timeSinceLastCall < 0) || (maxing && timeSinceLastInvoke >= maxWait));
  }

  function timerExpired() {
    var time = now();
    if (shouldInvoke(time)) {
      return trailingEdge(time);
    }
    // Restart the timer.
    timerId = setTimeout(timerExpired, remainingWait(time));
  }

  function trailingEdge(time) {
    timerId = undefined;

    // Only invoke if we have `lastArgs` which means `func` has been
    // debounced at least once.
    if (trailing && lastArgs) {
      return invokeFunc(time);
    }
    lastArgs = lastThis = undefined;
    return result;
  }

  function cancel() {
    if (timerId !== undefined) {
      clearTimeout(timerId);
    }
    lastInvokeTime = 0;
    lastArgs = lastCallTime = lastThis = timerId = undefined;
  }

  function flush() {
    return timerId === undefined ? result : trailingEdge(now());
  }

  function debounced() {
    var time = now(),
        isInvoking = shouldInvoke(time);

    lastArgs = arguments;
    lastThis = this;
    lastCallTime = time;

    if (isInvoking) {
      if (timerId === undefined) {
        return leadingEdge(lastCallTime);
      }
      if (maxing) {
        // Handle invocations in a tight loop.
        clearTimeout(timerId);
        timerId = setTimeout(timerExpired, wait);
        return invokeFunc(lastCallTime);
      }
    }
    if (timerId === undefined) {
      timerId = setTimeout(timerExpired, wait);
    }
    return result;
  }
  debounced.cancel = cancel;
  debounced.flush = flush;
  return debounced;
}

module.exports = debounce;

},{"./isObject":277,"./now":290,"./toNumber":307}],252:[function(require,module,exports){
var baseDifference = require('./_baseDifference'),
    baseFlatten = require('./_baseFlatten'),
    baseRest = require('./_baseRest'),
    isArrayLikeObject = require('./isArrayLikeObject');

/**
 * Creates an array of `array` values not included in the other given arrays
 * using [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * for equality comparisons. The order and references of result values are
 * determined by the first array.
 *
 * **Note:** Unlike `_.pullAll`, this method returns a new array.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Array
 * @param {Array} array The array to inspect.
 * @param {...Array} [values] The values to exclude.
 * @returns {Array} Returns the new array of filtered values.
 * @see _.without, _.xor
 * @example
 *
 * _.difference([2, 1], [2, 3]);
 * // => [1]
 */
var difference = baseRest(function(array, values) {
  return isArrayLikeObject(array)
    ? baseDifference(array, baseFlatten(values, 1, isArrayLikeObject, true))
    : [];
});

module.exports = difference;

},{"./_baseDifference":86,"./_baseFlatten":90,"./_baseRest":120,"./isArrayLikeObject":269}],253:[function(require,module,exports){
module.exports = require('./forEach');

},{"./forEach":260}],254:[function(require,module,exports){
/**
 * Performs a
 * [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * comparison between two values to determine if they are equivalent.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to compare.
 * @param {*} other The other value to compare.
 * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
 * @example
 *
 * var object = { 'a': 1 };
 * var other = { 'a': 1 };
 *
 * _.eq(object, object);
 * // => true
 *
 * _.eq(object, other);
 * // => false
 *
 * _.eq('a', 'a');
 * // => true
 *
 * _.eq('a', Object('a'));
 * // => false
 *
 * _.eq(NaN, NaN);
 * // => true
 */
function eq(value, other) {
  return value === other || (value !== value && other !== other);
}

module.exports = eq;

},{}],255:[function(require,module,exports){
module.exports = require('./assignIn');

},{"./assignIn":248}],256:[function(require,module,exports){
var arrayFilter = require('./_arrayFilter'),
    baseFilter = require('./_baseFilter'),
    baseIteratee = require('./_baseIteratee'),
    isArray = require('./isArray');

/**
 * Iterates over elements of `collection`, returning an array of all elements
 * `predicate` returns truthy for. The predicate is invoked with three
 * arguments: (value, index|key, collection).
 *
 * **Note:** Unlike `_.remove`, this method returns a new array.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @returns {Array} Returns the new filtered array.
 * @see _.reject
 * @example
 *
 * var users = [
 *   { 'user': 'barney', 'age': 36, 'active': true },
 *   { 'user': 'fred',   'age': 40, 'active': false }
 * ];
 *
 * _.filter(users, function(o) { return !o.active; });
 * // => objects for ['fred']
 *
 * // The `_.matches` iteratee shorthand.
 * _.filter(users, { 'age': 36, 'active': true });
 * // => objects for ['barney']
 *
 * // The `_.matchesProperty` iteratee shorthand.
 * _.filter(users, ['active', false]);
 * // => objects for ['fred']
 *
 * // The `_.property` iteratee shorthand.
 * _.filter(users, 'active');
 * // => objects for ['barney']
 *
 * // Combining several predicates using `_.overEvery` or `_.overSome`.
 * _.filter(users, _.overSome([{ 'age': 36 }, ['age', 40]]));
 * // => objects for ['fred', 'barney']
 */
function filter(collection, predicate) {
  var func = isArray(collection) ? arrayFilter : baseFilter;
  return func(collection, baseIteratee(predicate, 3));
}

module.exports = filter;

},{"./_arrayFilter":71,"./_baseFilter":88,"./_baseIteratee":107,"./isArray":267}],257:[function(require,module,exports){
var createFind = require('./_createFind'),
    findIndex = require('./findIndex');

/**
 * Iterates over elements of `collection`, returning the first element
 * `predicate` returns truthy for. The predicate is invoked with three
 * arguments: (value, index|key, collection).
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Collection
 * @param {Array|Object} collection The collection to inspect.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @param {number} [fromIndex=0] The index to search from.
 * @returns {*} Returns the matched element, else `undefined`.
 * @example
 *
 * var users = [
 *   { 'user': 'barney',  'age': 36, 'active': true },
 *   { 'user': 'fred',    'age': 40, 'active': false },
 *   { 'user': 'pebbles', 'age': 1,  'active': true }
 * ];
 *
 * _.find(users, function(o) { return o.age < 40; });
 * // => object for 'barney'
 *
 * // The `_.matches` iteratee shorthand.
 * _.find(users, { 'age': 1, 'active': true });
 * // => object for 'pebbles'
 *
 * // The `_.matchesProperty` iteratee shorthand.
 * _.find(users, ['active', false]);
 * // => object for 'fred'
 *
 * // The `_.property` iteratee shorthand.
 * _.find(users, 'active');
 * // => object for 'barney'
 */
var find = createFind(findIndex);

module.exports = find;

},{"./_createFind":154,"./findIndex":258}],258:[function(require,module,exports){
var baseFindIndex = require('./_baseFindIndex'),
    baseIteratee = require('./_baseIteratee'),
    toInteger = require('./toInteger');

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * This method is like `_.find` except that it returns the index of the first
 * element `predicate` returns truthy for instead of the element itself.
 *
 * @static
 * @memberOf _
 * @since 1.1.0
 * @category Array
 * @param {Array} array The array to inspect.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @param {number} [fromIndex=0] The index to search from.
 * @returns {number} Returns the index of the found element, else `-1`.
 * @example
 *
 * var users = [
 *   { 'user': 'barney',  'active': false },
 *   { 'user': 'fred',    'active': false },
 *   { 'user': 'pebbles', 'active': true }
 * ];
 *
 * _.findIndex(users, function(o) { return o.user == 'barney'; });
 * // => 0
 *
 * // The `_.matches` iteratee shorthand.
 * _.findIndex(users, { 'user': 'fred', 'active': false });
 * // => 1
 *
 * // The `_.matchesProperty` iteratee shorthand.
 * _.findIndex(users, ['active', false]);
 * // => 0
 *
 * // The `_.property` iteratee shorthand.
 * _.findIndex(users, 'active');
 * // => 2
 */
function findIndex(array, predicate, fromIndex) {
  var length = array == null ? 0 : array.length;
  if (!length) {
    return -1;
  }
  var index = fromIndex == null ? 0 : toInteger(fromIndex);
  if (index < 0) {
    index = nativeMax(length + index, 0);
  }
  return baseFindIndex(array, baseIteratee(predicate, 3), index);
}

module.exports = findIndex;

},{"./_baseFindIndex":89,"./_baseIteratee":107,"./toInteger":306}],259:[function(require,module,exports){
var baseFlatten = require('./_baseFlatten');

/**
 * Flattens `array` a single level deep.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Array
 * @param {Array} array The array to flatten.
 * @returns {Array} Returns the new flattened array.
 * @example
 *
 * _.flatten([1, [2, [3, [4]], 5]]);
 * // => [1, 2, [3, [4]], 5]
 */
function flatten(array) {
  var length = array == null ? 0 : array.length;
  return length ? baseFlatten(array, 1) : [];
}

module.exports = flatten;

},{"./_baseFlatten":90}],260:[function(require,module,exports){
var arrayEach = require('./_arrayEach'),
    baseEach = require('./_baseEach'),
    castFunction = require('./_castFunction'),
    isArray = require('./isArray');

/**
 * Iterates over elements of `collection` and invokes `iteratee` for each element.
 * The iteratee is invoked with three arguments: (value, index|key, collection).
 * Iteratee functions may exit iteration early by explicitly returning `false`.
 *
 * **Note:** As with other "Collections" methods, objects with a "length"
 * property are iterated like arrays. To avoid this behavior use `_.forIn`
 * or `_.forOwn` for object iteration.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @alias each
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} [iteratee=_.identity] The function invoked per iteration.
 * @returns {Array|Object} Returns `collection`.
 * @see _.forEachRight
 * @example
 *
 * _.forEach([1, 2], function(value) {
 *   console.log(value);
 * });
 * // => Logs `1` then `2`.
 *
 * _.forEach({ 'a': 1, 'b': 2 }, function(value, key) {
 *   console.log(key);
 * });
 * // => Logs 'a' then 'b' (iteration order is not guaranteed).
 */
function forEach(collection, iteratee) {
  var func = isArray(collection) ? arrayEach : baseEach;
  return func(collection, castFunction(iteratee));
}

module.exports = forEach;

},{"./_arrayEach":70,"./_baseEach":87,"./_castFunction":132,"./isArray":267}],261:[function(require,module,exports){
var baseForOwn = require('./_baseForOwn'),
    castFunction = require('./_castFunction');

/**
 * Iterates over own enumerable string keyed properties of an object and
 * invokes `iteratee` for each property. The iteratee is invoked with three
 * arguments: (value, key, object). Iteratee functions may exit iteration
 * early by explicitly returning `false`.
 *
 * @static
 * @memberOf _
 * @since 0.3.0
 * @category Object
 * @param {Object} object The object to iterate over.
 * @param {Function} [iteratee=_.identity] The function invoked per iteration.
 * @returns {Object} Returns `object`.
 * @see _.forOwnRight
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 *   this.b = 2;
 * }
 *
 * Foo.prototype.c = 3;
 *
 * _.forOwn(new Foo, function(value, key) {
 *   console.log(key);
 * });
 * // => Logs 'a' then 'b' (iteration order is not guaranteed).
 */
function forOwn(object, iteratee) {
  return object && baseForOwn(object, castFunction(iteratee));
}

module.exports = forOwn;

},{"./_baseForOwn":92,"./_castFunction":132}],262:[function(require,module,exports){
var baseGet = require('./_baseGet');

/**
 * Gets the value at `path` of `object`. If the resolved value is
 * `undefined`, the `defaultValue` is returned in its place.
 *
 * @static
 * @memberOf _
 * @since 3.7.0
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to get.
 * @param {*} [defaultValue] The value returned for `undefined` resolved values.
 * @returns {*} Returns the resolved value.
 * @example
 *
 * var object = { 'a': [{ 'b': { 'c': 3 } }] };
 *
 * _.get(object, 'a[0].b.c');
 * // => 3
 *
 * _.get(object, ['a', '0', 'b', 'c']);
 * // => 3
 *
 * _.get(object, 'a.b.c', 'default');
 * // => 'default'
 */
function get(object, path, defaultValue) {
  var result = object == null ? undefined : baseGet(object, path);
  return result === undefined ? defaultValue : result;
}

module.exports = get;

},{"./_baseGet":93}],263:[function(require,module,exports){
var baseHasIn = require('./_baseHasIn'),
    hasPath = require('./_hasPath');

/**
 * Checks if `path` is a direct or inherited property of `object`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path to check.
 * @returns {boolean} Returns `true` if `path` exists, else `false`.
 * @example
 *
 * var object = _.create({ 'a': _.create({ 'b': 2 }) });
 *
 * _.hasIn(object, 'a');
 * // => true
 *
 * _.hasIn(object, 'a.b');
 * // => true
 *
 * _.hasIn(object, ['a', 'b']);
 * // => true
 *
 * _.hasIn(object, 'b');
 * // => false
 */
function hasIn(object, path) {
  return object != null && hasPath(object, path, baseHasIn);
}

module.exports = hasIn;

},{"./_baseHasIn":96,"./_hasPath":182}],264:[function(require,module,exports){
/**
 * This method returns the first argument it receives.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Util
 * @param {*} value Any value.
 * @returns {*} Returns `value`.
 * @example
 *
 * var object = { 'a': 1 };
 *
 * console.log(_.identity(object) === object);
 * // => true
 */
function identity(value) {
  return value;
}

module.exports = identity;

},{}],265:[function(require,module,exports){
var baseIndexOf = require('./_baseIndexOf'),
    toInteger = require('./toInteger');

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMax = Math.max;

/**
 * Gets the index at which the first occurrence of `value` is found in `array`
 * using [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * for equality comparisons. If `fromIndex` is negative, it's used as the
 * offset from the end of `array`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Array
 * @param {Array} array The array to inspect.
 * @param {*} value The value to search for.
 * @param {number} [fromIndex=0] The index to search from.
 * @returns {number} Returns the index of the matched value, else `-1`.
 * @example
 *
 * _.indexOf([1, 2, 1, 2], 2);
 * // => 1
 *
 * // Search from the `fromIndex`.
 * _.indexOf([1, 2, 1, 2], 2, 2);
 * // => 3
 */
function indexOf(array, value, fromIndex) {
  var length = array == null ? 0 : array.length;
  if (!length) {
    return -1;
  }
  var index = fromIndex == null ? 0 : toInteger(fromIndex);
  if (index < 0) {
    index = nativeMax(length + index, 0);
  }
  return baseIndexOf(array, value, index);
}

module.exports = indexOf;

},{"./_baseIndexOf":97,"./toInteger":306}],266:[function(require,module,exports){
var baseIsArguments = require('./_baseIsArguments'),
    isObjectLike = require('./isObjectLike');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/** Built-in value references. */
var propertyIsEnumerable = objectProto.propertyIsEnumerable;

/**
 * Checks if `value` is likely an `arguments` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 *  else `false`.
 * @example
 *
 * _.isArguments(function() { return arguments; }());
 * // => true
 *
 * _.isArguments([1, 2, 3]);
 * // => false
 */
var isArguments = baseIsArguments(function() { return arguments; }()) ? baseIsArguments : function(value) {
  return isObjectLike(value) && hasOwnProperty.call(value, 'callee') &&
    !propertyIsEnumerable.call(value, 'callee');
};

module.exports = isArguments;

},{"./_baseIsArguments":98,"./isObjectLike":278}],267:[function(require,module,exports){
/**
 * Checks if `value` is classified as an `Array` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an array, else `false`.
 * @example
 *
 * _.isArray([1, 2, 3]);
 * // => true
 *
 * _.isArray(document.body.children);
 * // => false
 *
 * _.isArray('abc');
 * // => false
 *
 * _.isArray(_.noop);
 * // => false
 */
var isArray = Array.isArray;

module.exports = isArray;

},{}],268:[function(require,module,exports){
var isFunction = require('./isFunction'),
    isLength = require('./isLength');

/**
 * Checks if `value` is array-like. A value is considered array-like if it's
 * not a function and has a `value.length` that's an integer greater than or
 * equal to `0` and less than or equal to `Number.MAX_SAFE_INTEGER`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is array-like, else `false`.
 * @example
 *
 * _.isArrayLike([1, 2, 3]);
 * // => true
 *
 * _.isArrayLike(document.body.children);
 * // => true
 *
 * _.isArrayLike('abc');
 * // => true
 *
 * _.isArrayLike(_.noop);
 * // => false
 */
function isArrayLike(value) {
  return value != null && isLength(value.length) && !isFunction(value);
}

module.exports = isArrayLike;

},{"./isFunction":274,"./isLength":275}],269:[function(require,module,exports){
var isArrayLike = require('./isArrayLike'),
    isObjectLike = require('./isObjectLike');

/**
 * This method is like `_.isArrayLike` except that it also checks if `value`
 * is an object.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an array-like object,
 *  else `false`.
 * @example
 *
 * _.isArrayLikeObject([1, 2, 3]);
 * // => true
 *
 * _.isArrayLikeObject(document.body.children);
 * // => true
 *
 * _.isArrayLikeObject('abc');
 * // => false
 *
 * _.isArrayLikeObject(_.noop);
 * // => false
 */
function isArrayLikeObject(value) {
  return isObjectLike(value) && isArrayLike(value);
}

module.exports = isArrayLikeObject;

},{"./isArrayLike":268,"./isObjectLike":278}],270:[function(require,module,exports){
var baseGetTag = require('./_baseGetTag'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var boolTag = '[object Boolean]';

/**
 * Checks if `value` is classified as a boolean primitive or object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a boolean, else `false`.
 * @example
 *
 * _.isBoolean(false);
 * // => true
 *
 * _.isBoolean(null);
 * // => false
 */
function isBoolean(value) {
  return value === true || value === false ||
    (isObjectLike(value) && baseGetTag(value) == boolTag);
}

module.exports = isBoolean;

},{"./_baseGetTag":95,"./isObjectLike":278}],271:[function(require,module,exports){
var root = require('./_root'),
    stubFalse = require('./stubFalse');

/** Detect free variable `exports`. */
var freeExports = typeof exports == 'object' && exports && !exports.nodeType && exports;

/** Detect free variable `module`. */
var freeModule = freeExports && typeof module == 'object' && module && !module.nodeType && module;

/** Detect the popular CommonJS extension `module.exports`. */
var moduleExports = freeModule && freeModule.exports === freeExports;

/** Built-in value references. */
var Buffer = moduleExports ? root.Buffer : undefined;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeIsBuffer = Buffer ? Buffer.isBuffer : undefined;

/**
 * Checks if `value` is a buffer.
 *
 * @static
 * @memberOf _
 * @since 4.3.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a buffer, else `false`.
 * @example
 *
 * _.isBuffer(new Buffer(2));
 * // => true
 *
 * _.isBuffer(new Uint8Array(2));
 * // => false
 */
var isBuffer = nativeIsBuffer || stubFalse;

module.exports = isBuffer;

},{"./_root":227,"./stubFalse":304}],272:[function(require,module,exports){
var isObjectLike = require('./isObjectLike'),
    isPlainObject = require('./isPlainObject');

/**
 * Checks if `value` is likely a DOM element.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a DOM element, else `false`.
 * @example
 *
 * _.isElement(document.body);
 * // => true
 *
 * _.isElement('<body>');
 * // => false
 */
function isElement(value) {
  return isObjectLike(value) && value.nodeType === 1 && !isPlainObject(value);
}

module.exports = isElement;

},{"./isObjectLike":278,"./isPlainObject":279}],273:[function(require,module,exports){
var baseKeys = require('./_baseKeys'),
    getTag = require('./_getTag'),
    isArguments = require('./isArguments'),
    isArray = require('./isArray'),
    isArrayLike = require('./isArrayLike'),
    isBuffer = require('./isBuffer'),
    isPrototype = require('./_isPrototype'),
    isTypedArray = require('./isTypedArray');

/** `Object#toString` result references. */
var mapTag = '[object Map]',
    setTag = '[object Set]';

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Checks if `value` is an empty object, collection, map, or set.
 *
 * Objects are considered empty if they have no own enumerable string keyed
 * properties.
 *
 * Array-like values such as `arguments` objects, arrays, buffers, strings, or
 * jQuery-like collections are considered empty if they have a `length` of `0`.
 * Similarly, maps and sets are considered empty if they have a `size` of `0`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is empty, else `false`.
 * @example
 *
 * _.isEmpty(null);
 * // => true
 *
 * _.isEmpty(true);
 * // => true
 *
 * _.isEmpty(1);
 * // => true
 *
 * _.isEmpty([1, 2, 3]);
 * // => false
 *
 * _.isEmpty({ 'a': 1 });
 * // => false
 */
function isEmpty(value) {
  if (value == null) {
    return true;
  }
  if (isArrayLike(value) &&
      (isArray(value) || typeof value == 'string' || typeof value.splice == 'function' ||
        isBuffer(value) || isTypedArray(value) || isArguments(value))) {
    return !value.length;
  }
  var tag = getTag(value);
  if (tag == mapTag || tag == setTag) {
    return !value.size;
  }
  if (isPrototype(value)) {
    return !baseKeys(value).length;
  }
  for (var key in value) {
    if (hasOwnProperty.call(value, key)) {
      return false;
    }
  }
  return true;
}

module.exports = isEmpty;

},{"./_baseKeys":108,"./_getTag":179,"./_isPrototype":199,"./isArguments":266,"./isArray":267,"./isArrayLike":268,"./isBuffer":271,"./isTypedArray":282}],274:[function(require,module,exports){
var baseGetTag = require('./_baseGetTag'),
    isObject = require('./isObject');

/** `Object#toString` result references. */
var asyncTag = '[object AsyncFunction]',
    funcTag = '[object Function]',
    genTag = '[object GeneratorFunction]',
    proxyTag = '[object Proxy]';

/**
 * Checks if `value` is classified as a `Function` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a function, else `false`.
 * @example
 *
 * _.isFunction(_);
 * // => true
 *
 * _.isFunction(/abc/);
 * // => false
 */
function isFunction(value) {
  if (!isObject(value)) {
    return false;
  }
  // The use of `Object#toString` avoids issues with the `typeof` operator
  // in Safari 9 which returns 'object' for typed arrays and other constructors.
  var tag = baseGetTag(value);
  return tag == funcTag || tag == genTag || tag == asyncTag || tag == proxyTag;
}

module.exports = isFunction;

},{"./_baseGetTag":95,"./isObject":277}],275:[function(require,module,exports){
/** Used as references for various `Number` constants. */
var MAX_SAFE_INTEGER = 9007199254740991;

/**
 * Checks if `value` is a valid array-like length.
 *
 * **Note:** This method is loosely based on
 * [`ToLength`](http://ecma-international.org/ecma-262/7.0/#sec-tolength).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a valid length, else `false`.
 * @example
 *
 * _.isLength(3);
 * // => true
 *
 * _.isLength(Number.MIN_VALUE);
 * // => false
 *
 * _.isLength(Infinity);
 * // => false
 *
 * _.isLength('3');
 * // => false
 */
function isLength(value) {
  return typeof value == 'number' &&
    value > -1 && value % 1 == 0 && value <= MAX_SAFE_INTEGER;
}

module.exports = isLength;

},{}],276:[function(require,module,exports){
var baseIsMap = require('./_baseIsMap'),
    baseUnary = require('./_baseUnary'),
    nodeUtil = require('./_nodeUtil');

/* Node.js helper references. */
var nodeIsMap = nodeUtil && nodeUtil.isMap;

/**
 * Checks if `value` is classified as a `Map` object.
 *
 * @static
 * @memberOf _
 * @since 4.3.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a map, else `false`.
 * @example
 *
 * _.isMap(new Map);
 * // => true
 *
 * _.isMap(new WeakMap);
 * // => false
 */
var isMap = nodeIsMap ? baseUnary(nodeIsMap) : baseIsMap;

module.exports = isMap;

},{"./_baseIsMap":101,"./_baseUnary":128,"./_nodeUtil":219}],277:[function(require,module,exports){
/**
 * Checks if `value` is the
 * [language type](http://www.ecma-international.org/ecma-262/7.0/#sec-ecmascript-language-types)
 * of `Object`. (e.g. arrays, functions, objects, regexes, `new Number(0)`, and `new String('')`)
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an object, else `false`.
 * @example
 *
 * _.isObject({});
 * // => true
 *
 * _.isObject([1, 2, 3]);
 * // => true
 *
 * _.isObject(_.noop);
 * // => true
 *
 * _.isObject(null);
 * // => false
 */
function isObject(value) {
  var type = typeof value;
  return value != null && (type == 'object' || type == 'function');
}

module.exports = isObject;

},{}],278:[function(require,module,exports){
/**
 * Checks if `value` is object-like. A value is object-like if it's not `null`
 * and has a `typeof` result of "object".
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is object-like, else `false`.
 * @example
 *
 * _.isObjectLike({});
 * // => true
 *
 * _.isObjectLike([1, 2, 3]);
 * // => true
 *
 * _.isObjectLike(_.noop);
 * // => false
 *
 * _.isObjectLike(null);
 * // => false
 */
function isObjectLike(value) {
  return value != null && typeof value == 'object';
}

module.exports = isObjectLike;

},{}],279:[function(require,module,exports){
var baseGetTag = require('./_baseGetTag'),
    getPrototype = require('./_getPrototype'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var objectTag = '[object Object]';

/** Used for built-in method references. */
var funcProto = Function.prototype,
    objectProto = Object.prototype;

/** Used to resolve the decompiled source of functions. */
var funcToString = funcProto.toString;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/** Used to infer the `Object` constructor. */
var objectCtorString = funcToString.call(Object);

/**
 * Checks if `value` is a plain object, that is, an object created by the
 * `Object` constructor or one with a `[[Prototype]]` of `null`.
 *
 * @static
 * @memberOf _
 * @since 0.8.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a plain object, else `false`.
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 * }
 *
 * _.isPlainObject(new Foo);
 * // => false
 *
 * _.isPlainObject([1, 2, 3]);
 * // => false
 *
 * _.isPlainObject({ 'x': 0, 'y': 0 });
 * // => true
 *
 * _.isPlainObject(Object.create(null));
 * // => true
 */
function isPlainObject(value) {
  if (!isObjectLike(value) || baseGetTag(value) != objectTag) {
    return false;
  }
  var proto = getPrototype(value);
  if (proto === null) {
    return true;
  }
  var Ctor = hasOwnProperty.call(proto, 'constructor') && proto.constructor;
  return typeof Ctor == 'function' && Ctor instanceof Ctor &&
    funcToString.call(Ctor) == objectCtorString;
}

module.exports = isPlainObject;

},{"./_baseGetTag":95,"./_getPrototype":175,"./isObjectLike":278}],280:[function(require,module,exports){
var baseIsSet = require('./_baseIsSet'),
    baseUnary = require('./_baseUnary'),
    nodeUtil = require('./_nodeUtil');

/* Node.js helper references. */
var nodeIsSet = nodeUtil && nodeUtil.isSet;

/**
 * Checks if `value` is classified as a `Set` object.
 *
 * @static
 * @memberOf _
 * @since 4.3.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a set, else `false`.
 * @example
 *
 * _.isSet(new Set);
 * // => true
 *
 * _.isSet(new WeakSet);
 * // => false
 */
var isSet = nodeIsSet ? baseUnary(nodeIsSet) : baseIsSet;

module.exports = isSet;

},{"./_baseIsSet":105,"./_baseUnary":128,"./_nodeUtil":219}],281:[function(require,module,exports){
var baseGetTag = require('./_baseGetTag'),
    isObjectLike = require('./isObjectLike');

/** `Object#toString` result references. */
var symbolTag = '[object Symbol]';

/**
 * Checks if `value` is classified as a `Symbol` primitive or object.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a symbol, else `false`.
 * @example
 *
 * _.isSymbol(Symbol.iterator);
 * // => true
 *
 * _.isSymbol('abc');
 * // => false
 */
function isSymbol(value) {
  return typeof value == 'symbol' ||
    (isObjectLike(value) && baseGetTag(value) == symbolTag);
}

module.exports = isSymbol;

},{"./_baseGetTag":95,"./isObjectLike":278}],282:[function(require,module,exports){
var baseIsTypedArray = require('./_baseIsTypedArray'),
    baseUnary = require('./_baseUnary'),
    nodeUtil = require('./_nodeUtil');

/* Node.js helper references. */
var nodeIsTypedArray = nodeUtil && nodeUtil.isTypedArray;

/**
 * Checks if `value` is classified as a typed array.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a typed array, else `false`.
 * @example
 *
 * _.isTypedArray(new Uint8Array);
 * // => true
 *
 * _.isTypedArray([]);
 * // => false
 */
var isTypedArray = nodeIsTypedArray ? baseUnary(nodeIsTypedArray) : baseIsTypedArray;

module.exports = isTypedArray;

},{"./_baseIsTypedArray":106,"./_baseUnary":128,"./_nodeUtil":219}],283:[function(require,module,exports){
var arrayLikeKeys = require('./_arrayLikeKeys'),
    baseKeys = require('./_baseKeys'),
    isArrayLike = require('./isArrayLike');

/**
 * Creates an array of the own enumerable property names of `object`.
 *
 * **Note:** Non-object values are coerced to objects. See the
 * [ES spec](http://ecma-international.org/ecma-262/7.0/#sec-object.keys)
 * for more details.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Object
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 *   this.b = 2;
 * }
 *
 * Foo.prototype.c = 3;
 *
 * _.keys(new Foo);
 * // => ['a', 'b'] (iteration order is not guaranteed)
 *
 * _.keys('hi');
 * // => ['0', '1']
 */
function keys(object) {
  return isArrayLike(object) ? arrayLikeKeys(object) : baseKeys(object);
}

module.exports = keys;

},{"./_arrayLikeKeys":74,"./_baseKeys":108,"./isArrayLike":268}],284:[function(require,module,exports){
var arrayLikeKeys = require('./_arrayLikeKeys'),
    baseKeysIn = require('./_baseKeysIn'),
    isArrayLike = require('./isArrayLike');

/**
 * Creates an array of the own and inherited enumerable property names of `object`.
 *
 * **Note:** Non-object values are coerced to objects.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Object
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 *   this.b = 2;
 * }
 *
 * Foo.prototype.c = 3;
 *
 * _.keysIn(new Foo);
 * // => ['a', 'b', 'c'] (iteration order is not guaranteed)
 */
function keysIn(object) {
  return isArrayLike(object) ? arrayLikeKeys(object, true) : baseKeysIn(object);
}

module.exports = keysIn;

},{"./_arrayLikeKeys":74,"./_baseKeysIn":109,"./isArrayLike":268}],285:[function(require,module,exports){
/**
 * Gets the last element of `array`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Array
 * @param {Array} array The array to query.
 * @returns {*} Returns the last element of `array`.
 * @example
 *
 * _.last([1, 2, 3]);
 * // => 3
 */
function last(array) {
  var length = array == null ? 0 : array.length;
  return length ? array[length - 1] : undefined;
}

module.exports = last;

},{}],286:[function(require,module,exports){
var arrayMap = require('./_arrayMap'),
    baseIteratee = require('./_baseIteratee'),
    baseMap = require('./_baseMap'),
    isArray = require('./isArray');

/**
 * Creates an array of values by running each element in `collection` thru
 * `iteratee`. The iteratee is invoked with three arguments:
 * (value, index|key, collection).
 *
 * Many lodash methods are guarded to work as iteratees for methods like
 * `_.every`, `_.filter`, `_.map`, `_.mapValues`, `_.reject`, and `_.some`.
 *
 * The guarded methods are:
 * `ary`, `chunk`, `curry`, `curryRight`, `drop`, `dropRight`, `every`,
 * `fill`, `invert`, `parseInt`, `random`, `range`, `rangeRight`, `repeat`,
 * `sampleSize`, `slice`, `some`, `sortBy`, `split`, `take`, `takeRight`,
 * `template`, `trim`, `trimEnd`, `trimStart`, and `words`
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} [iteratee=_.identity] The function invoked per iteration.
 * @returns {Array} Returns the new mapped array.
 * @example
 *
 * function square(n) {
 *   return n * n;
 * }
 *
 * _.map([4, 8], square);
 * // => [16, 64]
 *
 * _.map({ 'a': 4, 'b': 8 }, square);
 * // => [16, 64] (iteration order is not guaranteed)
 *
 * var users = [
 *   { 'user': 'barney' },
 *   { 'user': 'fred' }
 * ];
 *
 * // The `_.property` iteratee shorthand.
 * _.map(users, 'user');
 * // => ['barney', 'fred']
 */
function map(collection, iteratee) {
  var func = isArray(collection) ? arrayMap : baseMap;
  return func(collection, baseIteratee(iteratee, 3));
}

module.exports = map;

},{"./_arrayMap":75,"./_baseIteratee":107,"./_baseMap":111,"./isArray":267}],287:[function(require,module,exports){
var MapCache = require('./_MapCache');

/** Error message constants. */
var FUNC_ERROR_TEXT = 'Expected a function';

/**
 * Creates a function that memoizes the result of `func`. If `resolver` is
 * provided, it determines the cache key for storing the result based on the
 * arguments provided to the memoized function. By default, the first argument
 * provided to the memoized function is used as the map cache key. The `func`
 * is invoked with the `this` binding of the memoized function.
 *
 * **Note:** The cache is exposed as the `cache` property on the memoized
 * function. Its creation may be customized by replacing the `_.memoize.Cache`
 * constructor with one whose instances implement the
 * [`Map`](http://ecma-international.org/ecma-262/7.0/#sec-properties-of-the-map-prototype-object)
 * method interface of `clear`, `delete`, `get`, `has`, and `set`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Function
 * @param {Function} func The function to have its output memoized.
 * @param {Function} [resolver] The function to resolve the cache key.
 * @returns {Function} Returns the new memoized function.
 * @example
 *
 * var object = { 'a': 1, 'b': 2 };
 * var other = { 'c': 3, 'd': 4 };
 *
 * var values = _.memoize(_.values);
 * values(object);
 * // => [1, 2]
 *
 * values(other);
 * // => [3, 4]
 *
 * object.a = 2;
 * values(object);
 * // => [1, 2]
 *
 * // Modify the result cache.
 * values.cache.set(object, ['a', 'b']);
 * values(object);
 * // => ['a', 'b']
 *
 * // Replace `_.memoize.Cache`.
 * _.memoize.Cache = WeakMap;
 */
function memoize(func, resolver) {
  if (typeof func != 'function' || (resolver != null && typeof resolver != 'function')) {
    throw new TypeError(FUNC_ERROR_TEXT);
  }
  var memoized = function() {
    var args = arguments,
        key = resolver ? resolver.apply(this, args) : args[0],
        cache = memoized.cache;

    if (cache.has(key)) {
      return cache.get(key);
    }
    var result = func.apply(this, args);
    memoized.cache = cache.set(key, result) || cache;
    return result;
  };
  memoized.cache = new (memoize.Cache || MapCache);
  return memoized;
}

// Expose `MapCache`.
memoize.Cache = MapCache;

module.exports = memoize;

},{"./_MapCache":61}],288:[function(require,module,exports){
/** Error message constants. */
var FUNC_ERROR_TEXT = 'Expected a function';

/**
 * Creates a function that negates the result of the predicate `func`. The
 * `func` predicate is invoked with the `this` binding and arguments of the
 * created function.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Function
 * @param {Function} predicate The predicate to negate.
 * @returns {Function} Returns the new negated function.
 * @example
 *
 * function isEven(n) {
 *   return n % 2 == 0;
 * }
 *
 * _.filter([1, 2, 3, 4, 5, 6], _.negate(isEven));
 * // => [1, 3, 5]
 */
function negate(predicate) {
  if (typeof predicate != 'function') {
    throw new TypeError(FUNC_ERROR_TEXT);
  }
  return function() {
    var args = arguments;
    switch (args.length) {
      case 0: return !predicate.call(this);
      case 1: return !predicate.call(this, args[0]);
      case 2: return !predicate.call(this, args[0], args[1]);
      case 3: return !predicate.call(this, args[0], args[1], args[2]);
    }
    return !predicate.apply(this, args);
  };
}

module.exports = negate;

},{}],289:[function(require,module,exports){
/**
 * This method returns `undefined`.
 *
 * @static
 * @memberOf _
 * @since 2.3.0
 * @category Util
 * @example
 *
 * _.times(2, _.noop);
 * // => [undefined, undefined]
 */
function noop() {
  // No operation performed.
}

module.exports = noop;

},{}],290:[function(require,module,exports){
var root = require('./_root');

/**
 * Gets the timestamp of the number of milliseconds that have elapsed since
 * the Unix epoch (1 January 1970 00:00:00 UTC).
 *
 * @static
 * @memberOf _
 * @since 2.4.0
 * @category Date
 * @returns {number} Returns the timestamp.
 * @example
 *
 * _.defer(function(stamp) {
 *   console.log(_.now() - stamp);
 * }, _.now());
 * // => Logs the number of milliseconds it took for the deferred invocation.
 */
var now = function() {
  return root.Date.now();
};

module.exports = now;

},{"./_root":227}],291:[function(require,module,exports){
var arrayMap = require('./_arrayMap'),
    baseClone = require('./_baseClone'),
    baseUnset = require('./_baseUnset'),
    castPath = require('./_castPath'),
    copyObject = require('./_copyObject'),
    customOmitClone = require('./_customOmitClone'),
    flatRest = require('./_flatRest'),
    getAllKeysIn = require('./_getAllKeysIn');

/** Used to compose bitmasks for cloning. */
var CLONE_DEEP_FLAG = 1,
    CLONE_FLAT_FLAG = 2,
    CLONE_SYMBOLS_FLAG = 4;

/**
 * The opposite of `_.pick`; this method creates an object composed of the
 * own and inherited enumerable property paths of `object` that are not omitted.
 *
 * **Note:** This method is considerably slower than `_.pick`.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Object
 * @param {Object} object The source object.
 * @param {...(string|string[])} [paths] The property paths to omit.
 * @returns {Object} Returns the new object.
 * @example
 *
 * var object = { 'a': 1, 'b': '2', 'c': 3 };
 *
 * _.omit(object, ['a', 'c']);
 * // => { 'b': '2' }
 */
var omit = flatRest(function(object, paths) {
  var result = {};
  if (object == null) {
    return result;
  }
  var isDeep = false;
  paths = arrayMap(paths, function(path) {
    path = castPath(path, object);
    isDeep || (isDeep = path.length > 1);
    return path;
  });
  copyObject(object, getAllKeysIn(object), result);
  if (isDeep) {
    result = baseClone(result, CLONE_DEEP_FLAG | CLONE_FLAT_FLAG | CLONE_SYMBOLS_FLAG, customOmitClone);
  }
  var length = paths.length;
  while (length--) {
    baseUnset(result, paths[length]);
  }
  return result;
});

module.exports = omit;

},{"./_arrayMap":75,"./_baseClone":84,"./_baseUnset":130,"./_castPath":133,"./_copyObject":143,"./_customOmitClone":160,"./_flatRest":165,"./_getAllKeysIn":168}],292:[function(require,module,exports){
var baseIteratee = require('./_baseIteratee'),
    negate = require('./negate'),
    pickBy = require('./pickBy');

/**
 * The opposite of `_.pickBy`; this method creates an object composed of
 * the own and inherited enumerable string keyed properties of `object` that
 * `predicate` doesn't return truthy for. The predicate is invoked with two
 * arguments: (value, key).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Object
 * @param {Object} object The source object.
 * @param {Function} [predicate=_.identity] The function invoked per property.
 * @returns {Object} Returns the new object.
 * @example
 *
 * var object = { 'a': 1, 'b': '2', 'c': 3 };
 *
 * _.omitBy(object, _.isNumber);
 * // => { 'b': '2' }
 */
function omitBy(object, predicate) {
  return pickBy(object, negate(baseIteratee(predicate)));
}

module.exports = omitBy;

},{"./_baseIteratee":107,"./negate":288,"./pickBy":296}],293:[function(require,module,exports){
var before = require('./before');

/**
 * Creates a function that is restricted to invoking `func` once. Repeat calls
 * to the function return the value of the first invocation. The `func` is
 * invoked with the `this` binding and arguments of the created function.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Function
 * @param {Function} func The function to restrict.
 * @returns {Function} Returns the new restricted function.
 * @example
 *
 * var initialize = _.once(createApplication);
 * initialize();
 * initialize();
 * // => `createApplication` is invoked once
 */
function once(func) {
  return before(2, func);
}

module.exports = once;

},{"./before":249}],294:[function(require,module,exports){
var baseRest = require('./_baseRest'),
    createWrap = require('./_createWrap'),
    getHolder = require('./_getHolder'),
    replaceHolders = require('./_replaceHolders');

/** Used to compose bitmasks for function metadata. */
var WRAP_PARTIAL_FLAG = 32;

/**
 * Creates a function that invokes `func` with `partials` prepended to the
 * arguments it receives. This method is like `_.bind` except it does **not**
 * alter the `this` binding.
 *
 * The `_.partial.placeholder` value, which defaults to `_` in monolithic
 * builds, may be used as a placeholder for partially applied arguments.
 *
 * **Note:** This method doesn't set the "length" property of partially
 * applied functions.
 *
 * @static
 * @memberOf _
 * @since 0.2.0
 * @category Function
 * @param {Function} func The function to partially apply arguments to.
 * @param {...*} [partials] The arguments to be partially applied.
 * @returns {Function} Returns the new partially applied function.
 * @example
 *
 * function greet(greeting, name) {
 *   return greeting + ' ' + name;
 * }
 *
 * var sayHelloTo = _.partial(greet, 'hello');
 * sayHelloTo('fred');
 * // => 'hello fred'
 *
 * // Partially applied with placeholders.
 * var greetFred = _.partial(greet, _, 'fred');
 * greetFred('hi');
 * // => 'hi fred'
 */
var partial = baseRest(function(func, partials) {
  var holders = replaceHolders(partials, getHolder(partial));
  return createWrap(func, WRAP_PARTIAL_FLAG, undefined, partials, holders);
});

// Assign default placeholders.
partial.placeholder = {};

module.exports = partial;

},{"./_baseRest":120,"./_createWrap":159,"./_getHolder":171,"./_replaceHolders":226}],295:[function(require,module,exports){
var basePick = require('./_basePick'),
    flatRest = require('./_flatRest');

/**
 * Creates an object composed of the picked `object` properties.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Object
 * @param {Object} object The source object.
 * @param {...(string|string[])} [paths] The property paths to pick.
 * @returns {Object} Returns the new object.
 * @example
 *
 * var object = { 'a': 1, 'b': '2', 'c': 3 };
 *
 * _.pick(object, ['a', 'c']);
 * // => { 'a': 1, 'c': 3 }
 */
var pick = flatRest(function(object, paths) {
  return object == null ? {} : basePick(object, paths);
});

module.exports = pick;

},{"./_basePick":114,"./_flatRest":165}],296:[function(require,module,exports){
var arrayMap = require('./_arrayMap'),
    baseIteratee = require('./_baseIteratee'),
    basePickBy = require('./_basePickBy'),
    getAllKeysIn = require('./_getAllKeysIn');

/**
 * Creates an object composed of the `object` properties `predicate` returns
 * truthy for. The predicate is invoked with two arguments: (value, key).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Object
 * @param {Object} object The source object.
 * @param {Function} [predicate=_.identity] The function invoked per property.
 * @returns {Object} Returns the new object.
 * @example
 *
 * var object = { 'a': 1, 'b': '2', 'c': 3 };
 *
 * _.pickBy(object, _.isNumber);
 * // => { 'a': 1, 'c': 3 }
 */
function pickBy(object, predicate) {
  if (object == null) {
    return {};
  }
  var props = arrayMap(getAllKeysIn(object), function(prop) {
    return [prop];
  });
  predicate = baseIteratee(predicate);
  return basePickBy(object, props, function(value, path) {
    return predicate(value, path[0]);
  });
}

module.exports = pickBy;

},{"./_arrayMap":75,"./_baseIteratee":107,"./_basePickBy":115,"./_getAllKeysIn":168}],297:[function(require,module,exports){
var baseProperty = require('./_baseProperty'),
    basePropertyDeep = require('./_basePropertyDeep'),
    isKey = require('./_isKey'),
    toKey = require('./_toKey');

/**
 * Creates a function that returns the value at `path` of a given object.
 *
 * @static
 * @memberOf _
 * @since 2.4.0
 * @category Util
 * @param {Array|string} path The path of the property to get.
 * @returns {Function} Returns the new accessor function.
 * @example
 *
 * var objects = [
 *   { 'a': { 'b': 2 } },
 *   { 'a': { 'b': 1 } }
 * ];
 *
 * _.map(objects, _.property('a.b'));
 * // => [2, 1]
 *
 * _.map(_.sortBy(objects, _.property(['a', 'b'])), 'a.b');
 * // => [1, 2]
 */
function property(path) {
  return isKey(path) ? baseProperty(toKey(path)) : basePropertyDeep(path);
}

module.exports = property;

},{"./_baseProperty":116,"./_basePropertyDeep":117,"./_isKey":195,"./_toKey":242}],298:[function(require,module,exports){
var arrayReduce = require('./_arrayReduce'),
    baseEach = require('./_baseEach'),
    baseIteratee = require('./_baseIteratee'),
    baseReduce = require('./_baseReduce'),
    isArray = require('./isArray');

/**
 * Reduces `collection` to a value which is the accumulated result of running
 * each element in `collection` thru `iteratee`, where each successive
 * invocation is supplied the return value of the previous. If `accumulator`
 * is not given, the first element of `collection` is used as the initial
 * value. The iteratee is invoked with four arguments:
 * (accumulator, value, index|key, collection).
 *
 * Many lodash methods are guarded to work as iteratees for methods like
 * `_.reduce`, `_.reduceRight`, and `_.transform`.
 *
 * The guarded methods are:
 * `assign`, `defaults`, `defaultsDeep`, `includes`, `merge`, `orderBy`,
 * and `sortBy`
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} [iteratee=_.identity] The function invoked per iteration.
 * @param {*} [accumulator] The initial value.
 * @returns {*} Returns the accumulated value.
 * @see _.reduceRight
 * @example
 *
 * _.reduce([1, 2], function(sum, n) {
 *   return sum + n;
 * }, 0);
 * // => 3
 *
 * _.reduce({ 'a': 1, 'b': 2, 'c': 1 }, function(result, value, key) {
 *   (result[value] || (result[value] = [])).push(key);
 *   return result;
 * }, {});
 * // => { '1': ['a', 'c'], '2': ['b'] } (iteration order is not guaranteed)
 */
function reduce(collection, iteratee, accumulator) {
  var func = isArray(collection) ? arrayReduce : baseReduce,
      initAccum = arguments.length < 3;

  return func(collection, baseIteratee(iteratee, 4), accumulator, initAccum, baseEach);
}

module.exports = reduce;

},{"./_arrayReduce":77,"./_baseEach":87,"./_baseIteratee":107,"./_baseReduce":119,"./isArray":267}],299:[function(require,module,exports){
var arrayFilter = require('./_arrayFilter'),
    baseFilter = require('./_baseFilter'),
    baseIteratee = require('./_baseIteratee'),
    isArray = require('./isArray'),
    negate = require('./negate');

/**
 * The opposite of `_.filter`; this method returns the elements of `collection`
 * that `predicate` does **not** return truthy for.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @returns {Array} Returns the new filtered array.
 * @see _.filter
 * @example
 *
 * var users = [
 *   { 'user': 'barney', 'age': 36, 'active': false },
 *   { 'user': 'fred',   'age': 40, 'active': true }
 * ];
 *
 * _.reject(users, function(o) { return !o.active; });
 * // => objects for ['fred']
 *
 * // The `_.matches` iteratee shorthand.
 * _.reject(users, { 'age': 40, 'active': true });
 * // => objects for ['barney']
 *
 * // The `_.matchesProperty` iteratee shorthand.
 * _.reject(users, ['active', false]);
 * // => objects for ['fred']
 *
 * // The `_.property` iteratee shorthand.
 * _.reject(users, 'active');
 * // => objects for ['barney']
 */
function reject(collection, predicate) {
  var func = isArray(collection) ? arrayFilter : baseFilter;
  return func(collection, negate(baseIteratee(predicate, 3)));
}

module.exports = reject;

},{"./_arrayFilter":71,"./_baseFilter":88,"./_baseIteratee":107,"./isArray":267,"./negate":288}],300:[function(require,module,exports){
var baseIteratee = require('./_baseIteratee'),
    basePullAt = require('./_basePullAt');

/**
 * Removes all elements from `array` that `predicate` returns truthy for
 * and returns an array of the removed elements. The predicate is invoked
 * with three arguments: (value, index, array).
 *
 * **Note:** Unlike `_.filter`, this method mutates `array`. Use `_.pull`
 * to pull elements from an array by value.
 *
 * @static
 * @memberOf _
 * @since 2.0.0
 * @category Array
 * @param {Array} array The array to modify.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @returns {Array} Returns the new array of removed elements.
 * @example
 *
 * var array = [1, 2, 3, 4];
 * var evens = _.remove(array, function(n) {
 *   return n % 2 == 0;
 * });
 *
 * console.log(array);
 * // => [1, 3]
 *
 * console.log(evens);
 * // => [2, 4]
 */
function remove(array, predicate) {
  var result = [];
  if (!(array && array.length)) {
    return result;
  }
  var index = -1,
      indexes = [],
      length = array.length;

  predicate = baseIteratee(predicate, 3);
  while (++index < length) {
    var value = array[index];
    if (predicate(value, index, array)) {
      result.push(value);
      indexes.push(index);
    }
  }
  basePullAt(array, indexes);
  return result;
}

module.exports = remove;

},{"./_baseIteratee":107,"./_basePullAt":118}],301:[function(require,module,exports){
var castPath = require('./_castPath'),
    isFunction = require('./isFunction'),
    toKey = require('./_toKey');

/**
 * This method is like `_.get` except that if the resolved value is a
 * function it's invoked with the `this` binding of its parent object and
 * its result is returned.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to resolve.
 * @param {*} [defaultValue] The value returned for `undefined` resolved values.
 * @returns {*} Returns the resolved value.
 * @example
 *
 * var object = { 'a': [{ 'b': { 'c1': 3, 'c2': _.constant(4) } }] };
 *
 * _.result(object, 'a[0].b.c1');
 * // => 3
 *
 * _.result(object, 'a[0].b.c2');
 * // => 4
 *
 * _.result(object, 'a[0].b.c3', 'default');
 * // => 'default'
 *
 * _.result(object, 'a[0].b.c3', _.constant('default'));
 * // => 'default'
 */
function result(object, path, defaultValue) {
  path = castPath(path, object);

  var index = -1,
      length = path.length;

  // Ensure the loop is entered when path is empty.
  if (!length) {
    length = 1;
    object = undefined;
  }
  while (++index < length) {
    var value = object == null ? undefined : object[toKey(path[index])];
    if (value === undefined) {
      index = length;
      value = defaultValue;
    }
    object = isFunction(value) ? value.call(object) : value;
  }
  return object;
}

module.exports = result;

},{"./_castPath":133,"./_toKey":242,"./isFunction":274}],302:[function(require,module,exports){
/** Used for built-in method references. */
var arrayProto = Array.prototype;

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeReverse = arrayProto.reverse;

/**
 * Reverses `array` so that the first element becomes the last, the second
 * element becomes the second to last, and so on.
 *
 * **Note:** This method mutates `array` and is based on
 * [`Array#reverse`](https://mdn.io/Array/reverse).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Array
 * @param {Array} array The array to modify.
 * @returns {Array} Returns `array`.
 * @example
 *
 * var array = [1, 2, 3];
 *
 * _.reverse(array);
 * // => [3, 2, 1]
 *
 * console.log(array);
 * // => [3, 2, 1]
 */
function reverse(array) {
  return array == null ? array : nativeReverse.call(array);
}

module.exports = reverse;

},{}],303:[function(require,module,exports){
/**
 * This method returns a new empty array.
 *
 * @static
 * @memberOf _
 * @since 4.13.0
 * @category Util
 * @returns {Array} Returns the new empty array.
 * @example
 *
 * var arrays = _.times(2, _.stubArray);
 *
 * console.log(arrays);
 * // => [[], []]
 *
 * console.log(arrays[0] === arrays[1]);
 * // => false
 */
function stubArray() {
  return [];
}

module.exports = stubArray;

},{}],304:[function(require,module,exports){
/**
 * This method returns `false`.
 *
 * @static
 * @memberOf _
 * @since 4.13.0
 * @category Util
 * @returns {boolean} Returns `false`.
 * @example
 *
 * _.times(2, _.stubFalse);
 * // => [false, false]
 */
function stubFalse() {
  return false;
}

module.exports = stubFalse;

},{}],305:[function(require,module,exports){
var toNumber = require('./toNumber');

/** Used as references for various `Number` constants. */
var INFINITY = 1 / 0,
    MAX_INTEGER = 1.7976931348623157e+308;

/**
 * Converts `value` to a finite number.
 *
 * @static
 * @memberOf _
 * @since 4.12.0
 * @category Lang
 * @param {*} value The value to convert.
 * @returns {number} Returns the converted number.
 * @example
 *
 * _.toFinite(3.2);
 * // => 3.2
 *
 * _.toFinite(Number.MIN_VALUE);
 * // => 5e-324
 *
 * _.toFinite(Infinity);
 * // => 1.7976931348623157e+308
 *
 * _.toFinite('3.2');
 * // => 3.2
 */
function toFinite(value) {
  if (!value) {
    return value === 0 ? value : 0;
  }
  value = toNumber(value);
  if (value === INFINITY || value === -INFINITY) {
    var sign = (value < 0 ? -1 : 1);
    return sign * MAX_INTEGER;
  }
  return value === value ? value : 0;
}

module.exports = toFinite;

},{"./toNumber":307}],306:[function(require,module,exports){
var toFinite = require('./toFinite');

/**
 * Converts `value` to an integer.
 *
 * **Note:** This method is loosely based on
 * [`ToInteger`](http://www.ecma-international.org/ecma-262/7.0/#sec-tointeger).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to convert.
 * @returns {number} Returns the converted integer.
 * @example
 *
 * _.toInteger(3.2);
 * // => 3
 *
 * _.toInteger(Number.MIN_VALUE);
 * // => 0
 *
 * _.toInteger(Infinity);
 * // => 1.7976931348623157e+308
 *
 * _.toInteger('3.2');
 * // => 3
 */
function toInteger(value) {
  var result = toFinite(value),
      remainder = result % 1;

  return result === result ? (remainder ? result - remainder : result) : 0;
}

module.exports = toInteger;

},{"./toFinite":305}],307:[function(require,module,exports){
var baseTrim = require('./_baseTrim'),
    isObject = require('./isObject'),
    isSymbol = require('./isSymbol');

/** Used as references for various `Number` constants. */
var NAN = 0 / 0;

/** Used to detect bad signed hexadecimal string values. */
var reIsBadHex = /^[-+]0x[0-9a-f]+$/i;

/** Used to detect binary string values. */
var reIsBinary = /^0b[01]+$/i;

/** Used to detect octal string values. */
var reIsOctal = /^0o[0-7]+$/i;

/** Built-in method references without a dependency on `root`. */
var freeParseInt = parseInt;

/**
 * Converts `value` to a number.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to process.
 * @returns {number} Returns the number.
 * @example
 *
 * _.toNumber(3.2);
 * // => 3.2
 *
 * _.toNumber(Number.MIN_VALUE);
 * // => 5e-324
 *
 * _.toNumber(Infinity);
 * // => Infinity
 *
 * _.toNumber('3.2');
 * // => 3.2
 */
function toNumber(value) {
  if (typeof value == 'number') {
    return value;
  }
  if (isSymbol(value)) {
    return NAN;
  }
  if (isObject(value)) {
    var other = typeof value.valueOf == 'function' ? value.valueOf() : value;
    value = isObject(other) ? (other + '') : other;
  }
  if (typeof value != 'string') {
    return value === 0 ? value : +value;
  }
  value = baseTrim(value);
  var isBinary = reIsBinary.test(value);
  return (isBinary || reIsOctal.test(value))
    ? freeParseInt(value.slice(2), isBinary ? 2 : 8)
    : (reIsBadHex.test(value) ? NAN : +value);
}

module.exports = toNumber;

},{"./_baseTrim":127,"./isObject":277,"./isSymbol":281}],308:[function(require,module,exports){
var baseToString = require('./_baseToString');

/**
 * Converts `value` to a string. An empty string is returned for `null`
 * and `undefined` values. The sign of `-0` is preserved.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 * @example
 *
 * _.toString(null);
 * // => ''
 *
 * _.toString(-0);
 * // => '-0'
 *
 * _.toString([1, 2, 3]);
 * // => '1,2,3'
 */
function toString(value) {
  return value == null ? '' : baseToString(value);
}

module.exports = toString;

},{"./_baseToString":126}],309:[function(require,module,exports){
var baseFlatten = require('./_baseFlatten'),
    baseRest = require('./_baseRest'),
    baseUniq = require('./_baseUniq'),
    isArrayLikeObject = require('./isArrayLikeObject');

/**
 * Creates an array of unique values, in order, from all given arrays using
 * [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * for equality comparisons.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Array
 * @param {...Array} [arrays] The arrays to inspect.
 * @returns {Array} Returns the new array of combined values.
 * @example
 *
 * _.union([2], [1, 2]);
 * // => [2, 1]
 */
var union = baseRest(function(arrays) {
  return baseUniq(baseFlatten(arrays, 1, isArrayLikeObject, true));
});

module.exports = union;

},{"./_baseFlatten":90,"./_baseRest":120,"./_baseUniq":129,"./isArrayLikeObject":269}],310:[function(require,module,exports){
var baseIteratee = require('./_baseIteratee'),
    baseUniq = require('./_baseUniq');

/**
 * This method is like `_.uniq` except that it accepts `iteratee` which is
 * invoked for each element in `array` to generate the criterion by which
 * uniqueness is computed. The order of result values is determined by the
 * order they occur in the array. The iteratee is invoked with one argument:
 * (value).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Array
 * @param {Array} array The array to inspect.
 * @param {Function} [iteratee=_.identity] The iteratee invoked per element.
 * @returns {Array} Returns the new duplicate free array.
 * @example
 *
 * _.uniqBy([2.1, 1.2, 2.3], Math.floor);
 * // => [2.1, 1.2]
 *
 * // The `_.property` iteratee shorthand.
 * _.uniqBy([{ 'x': 1 }, { 'x': 2 }, { 'x': 1 }], 'x');
 * // => [{ 'x': 1 }, { 'x': 2 }]
 */
function uniqBy(array, iteratee) {
  return (array && array.length) ? baseUniq(array, baseIteratee(iteratee, 2)) : [];
}

module.exports = uniqBy;

},{"./_baseIteratee":107,"./_baseUniq":129}],311:[function(require,module,exports){
var toString = require('./toString');

/** Used to generate unique IDs. */
var idCounter = 0;

/**
 * Generates a unique ID. If `prefix` is given, the ID is appended to it.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Util
 * @param {string} [prefix=''] The value to prefix the ID with.
 * @returns {string} Returns the unique ID.
 * @example
 *
 * _.uniqueId('contact_');
 * // => 'contact_104'
 *
 * _.uniqueId();
 * // => '105'
 */
function uniqueId(prefix) {
  var id = ++idCounter;
  return toString(prefix) + id;
}

module.exports = uniqueId;

},{"./toString":308}],312:[function(require,module,exports){
var LazyWrapper = require('./_LazyWrapper'),
    LodashWrapper = require('./_LodashWrapper'),
    baseLodash = require('./_baseLodash'),
    isArray = require('./isArray'),
    isObjectLike = require('./isObjectLike'),
    wrapperClone = require('./_wrapperClone');

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Creates a `lodash` object which wraps `value` to enable implicit method
 * chain sequences. Methods that operate on and return arrays, collections,
 * and functions can be chained together. Methods that retrieve a single value
 * or may return a primitive value will automatically end the chain sequence
 * and return the unwrapped value. Otherwise, the value must be unwrapped
 * with `_#value`.
 *
 * Explicit chain sequences, which must be unwrapped with `_#value`, may be
 * enabled using `_.chain`.
 *
 * The execution of chained methods is lazy, that is, it's deferred until
 * `_#value` is implicitly or explicitly called.
 *
 * Lazy evaluation allows several methods to support shortcut fusion.
 * Shortcut fusion is an optimization to merge iteratee calls; this avoids
 * the creation of intermediate arrays and can greatly reduce the number of
 * iteratee executions. Sections of a chain sequence qualify for shortcut
 * fusion if the section is applied to an array and iteratees accept only
 * one argument. The heuristic for whether a section qualifies for shortcut
 * fusion is subject to change.
 *
 * Chaining is supported in custom builds as long as the `_#value` method is
 * directly or indirectly included in the build.
 *
 * In addition to lodash methods, wrappers have `Array` and `String` methods.
 *
 * The wrapper `Array` methods are:
 * `concat`, `join`, `pop`, `push`, `shift`, `sort`, `splice`, and `unshift`
 *
 * The wrapper `String` methods are:
 * `replace` and `split`
 *
 * The wrapper methods that support shortcut fusion are:
 * `at`, `compact`, `drop`, `dropRight`, `dropWhile`, `filter`, `find`,
 * `findLast`, `head`, `initial`, `last`, `map`, `reject`, `reverse`, `slice`,
 * `tail`, `take`, `takeRight`, `takeRightWhile`, `takeWhile`, and `toArray`
 *
 * The chainable wrapper methods are:
 * `after`, `ary`, `assign`, `assignIn`, `assignInWith`, `assignWith`, `at`,
 * `before`, `bind`, `bindAll`, `bindKey`, `castArray`, `chain`, `chunk`,
 * `commit`, `compact`, `concat`, `conforms`, `constant`, `countBy`, `create`,
 * `curry`, `debounce`, `defaults`, `defaultsDeep`, `defer`, `delay`,
 * `difference`, `differenceBy`, `differenceWith`, `drop`, `dropRight`,
 * `dropRightWhile`, `dropWhile`, `extend`, `extendWith`, `fill`, `filter`,
 * `flatMap`, `flatMapDeep`, `flatMapDepth`, `flatten`, `flattenDeep`,
 * `flattenDepth`, `flip`, `flow`, `flowRight`, `fromPairs`, `functions`,
 * `functionsIn`, `groupBy`, `initial`, `intersection`, `intersectionBy`,
 * `intersectionWith`, `invert`, `invertBy`, `invokeMap`, `iteratee`, `keyBy`,
 * `keys`, `keysIn`, `map`, `mapKeys`, `mapValues`, `matches`, `matchesProperty`,
 * `memoize`, `merge`, `mergeWith`, `method`, `methodOf`, `mixin`, `negate`,
 * `nthArg`, `omit`, `omitBy`, `once`, `orderBy`, `over`, `overArgs`,
 * `overEvery`, `overSome`, `partial`, `partialRight`, `partition`, `pick`,
 * `pickBy`, `plant`, `property`, `propertyOf`, `pull`, `pullAll`, `pullAllBy`,
 * `pullAllWith`, `pullAt`, `push`, `range`, `rangeRight`, `rearg`, `reject`,
 * `remove`, `rest`, `reverse`, `sampleSize`, `set`, `setWith`, `shuffle`,
 * `slice`, `sort`, `sortBy`, `splice`, `spread`, `tail`, `take`, `takeRight`,
 * `takeRightWhile`, `takeWhile`, `tap`, `throttle`, `thru`, `toArray`,
 * `toPairs`, `toPairsIn`, `toPath`, `toPlainObject`, `transform`, `unary`,
 * `union`, `unionBy`, `unionWith`, `uniq`, `uniqBy`, `uniqWith`, `unset`,
 * `unshift`, `unzip`, `unzipWith`, `update`, `updateWith`, `values`,
 * `valuesIn`, `without`, `wrap`, `xor`, `xorBy`, `xorWith`, `zip`,
 * `zipObject`, `zipObjectDeep`, and `zipWith`
 *
 * The wrapper methods that are **not** chainable by default are:
 * `add`, `attempt`, `camelCase`, `capitalize`, `ceil`, `clamp`, `clone`,
 * `cloneDeep`, `cloneDeepWith`, `cloneWith`, `conformsTo`, `deburr`,
 * `defaultTo`, `divide`, `each`, `eachRight`, `endsWith`, `eq`, `escape`,
 * `escapeRegExp`, `every`, `find`, `findIndex`, `findKey`, `findLast`,
 * `findLastIndex`, `findLastKey`, `first`, `floor`, `forEach`, `forEachRight`,
 * `forIn`, `forInRight`, `forOwn`, `forOwnRight`, `get`, `gt`, `gte`, `has`,
 * `hasIn`, `head`, `identity`, `includes`, `indexOf`, `inRange`, `invoke`,
 * `isArguments`, `isArray`, `isArrayBuffer`, `isArrayLike`, `isArrayLikeObject`,
 * `isBoolean`, `isBuffer`, `isDate`, `isElement`, `isEmpty`, `isEqual`,
 * `isEqualWith`, `isError`, `isFinite`, `isFunction`, `isInteger`, `isLength`,
 * `isMap`, `isMatch`, `isMatchWith`, `isNaN`, `isNative`, `isNil`, `isNull`,
 * `isNumber`, `isObject`, `isObjectLike`, `isPlainObject`, `isRegExp`,
 * `isSafeInteger`, `isSet`, `isString`, `isUndefined`, `isTypedArray`,
 * `isWeakMap`, `isWeakSet`, `join`, `kebabCase`, `last`, `lastIndexOf`,
 * `lowerCase`, `lowerFirst`, `lt`, `lte`, `max`, `maxBy`, `mean`, `meanBy`,
 * `min`, `minBy`, `multiply`, `noConflict`, `noop`, `now`, `nth`, `pad`,
 * `padEnd`, `padStart`, `parseInt`, `pop`, `random`, `reduce`, `reduceRight`,
 * `repeat`, `result`, `round`, `runInContext`, `sample`, `shift`, `size`,
 * `snakeCase`, `some`, `sortedIndex`, `sortedIndexBy`, `sortedLastIndex`,
 * `sortedLastIndexBy`, `startCase`, `startsWith`, `stubArray`, `stubFalse`,
 * `stubObject`, `stubString`, `stubTrue`, `subtract`, `sum`, `sumBy`,
 * `template`, `times`, `toFinite`, `toInteger`, `toJSON`, `toLength`,
 * `toLower`, `toNumber`, `toSafeInteger`, `toString`, `toUpper`, `trim`,
 * `trimEnd`, `trimStart`, `truncate`, `unescape`, `uniqueId`, `upperCase`,
 * `upperFirst`, `value`, and `words`
 *
 * @name _
 * @constructor
 * @category Seq
 * @param {*} value The value to wrap in a `lodash` instance.
 * @returns {Object} Returns the new `lodash` wrapper instance.
 * @example
 *
 * function square(n) {
 *   return n * n;
 * }
 *
 * var wrapped = _([1, 2, 3]);
 *
 * // Returns an unwrapped value.
 * wrapped.reduce(_.add);
 * // => 6
 *
 * // Returns a wrapped value.
 * var squares = wrapped.map(square);
 *
 * _.isArray(squares);
 * // => false
 *
 * _.isArray(squares.value());
 * // => true
 */
function lodash(value) {
  if (isObjectLike(value) && !isArray(value) && !(value instanceof LazyWrapper)) {
    if (value instanceof LodashWrapper) {
      return value;
    }
    if (hasOwnProperty.call(value, '__wrapped__')) {
      return wrapperClone(value);
    }
  }
  return new LodashWrapper(value);
}

// Ensure wrappers are instances of `baseLodash`.
lodash.prototype = baseLodash.prototype;
lodash.prototype.constructor = lodash;

module.exports = lodash;

},{"./_LazyWrapper":57,"./_LodashWrapper":59,"./_baseLodash":110,"./_wrapperClone":246,"./isArray":267,"./isObjectLike":278}],313:[function(require,module,exports){
'use strict';

var proto = typeof Element !== 'undefined' ? Element.prototype : {};
var vendor = proto.matches
  || proto.matchesSelector
  || proto.webkitMatchesSelector
  || proto.mozMatchesSelector
  || proto.msMatchesSelector
  || proto.oMatchesSelector;

module.exports = match;

/**
 * Match `el` to `selector`.
 *
 * @param {Element} el
 * @param {String} selector
 * @return {Boolean}
 * @api public
 */

function match(el, selector) {
  if (!el || el.nodeType !== 1) return false;
  if (vendor) return vendor.call(el, selector);
  var nodes = el.parentNode.querySelectorAll(selector);
  for (var i = 0; i < nodes.length; i++) {
    if (nodes[i] == el) return true;
  }
  return false;
}

},{}],314:[function(require,module,exports){
(function (process,setImmediate){(function (){
/*! Browser bundle of nunjucks 3.2.4 (slim, only works with precompiled templates) */
(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["nunjucks"] = factory();
	else
		root["nunjucks"] = factory();
})(typeof self !== 'undefined' ? self : this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 6);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports) {



/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var ArrayProto = Array.prototype;
var ObjProto = Object.prototype;
var escapeMap = {
  '&': '&amp;',
  '"': '&quot;',
  '\'': '&#39;',
  '<': '&lt;',
  '>': '&gt;',
  '\\': '&#92;'
};
var escapeRegex = /[&"'<>\\]/g;
var exports = module.exports = {};
function hasOwnProp(obj, k) {
  return ObjProto.hasOwnProperty.call(obj, k);
}
exports.hasOwnProp = hasOwnProp;
function lookupEscape(ch) {
  return escapeMap[ch];
}
function _prettifyError(path, withInternals, err) {
  if (!err.Update) {
    // not one of ours, cast it
    err = new exports.TemplateError(err);
  }
  err.Update(path);

  // Unless they marked the dev flag, show them a trace from here
  if (!withInternals) {
    var old = err;
    err = new Error(old.message);
    err.name = old.name;
  }
  return err;
}
exports._prettifyError = _prettifyError;
function TemplateError(message, lineno, colno) {
  var err;
  var cause;
  if (message instanceof Error) {
    cause = message;
    message = cause.name + ": " + cause.message;
  }
  if (Object.setPrototypeOf) {
    err = new Error(message);
    Object.setPrototypeOf(err, TemplateError.prototype);
  } else {
    err = this;
    Object.defineProperty(err, 'message', {
      enumerable: false,
      writable: true,
      value: message
    });
  }
  Object.defineProperty(err, 'name', {
    value: 'Template render error'
  });
  if (Error.captureStackTrace) {
    Error.captureStackTrace(err, this.constructor);
  }
  var getStack;
  if (cause) {
    var stackDescriptor = Object.getOwnPropertyDescriptor(cause, 'stack');
    getStack = stackDescriptor && (stackDescriptor.get || function () {
      return stackDescriptor.value;
    });
    if (!getStack) {
      getStack = function getStack() {
        return cause.stack;
      };
    }
  } else {
    var stack = new Error(message).stack;
    getStack = function getStack() {
      return stack;
    };
  }
  Object.defineProperty(err, 'stack', {
    get: function get() {
      return getStack.call(err);
    }
  });
  Object.defineProperty(err, 'cause', {
    value: cause
  });
  err.lineno = lineno;
  err.colno = colno;
  err.firstUpdate = true;
  err.Update = function Update(path) {
    var msg = '(' + (path || 'unknown path') + ')';

    // only show lineno + colno next to path of template
    // where error occurred
    if (this.firstUpdate) {
      if (this.lineno && this.colno) {
        msg += " [Line " + this.lineno + ", Column " + this.colno + "]";
      } else if (this.lineno) {
        msg += " [Line " + this.lineno + "]";
      }
    }
    msg += '\n ';
    if (this.firstUpdate) {
      msg += ' ';
    }
    this.message = msg + (this.message || '');
    this.firstUpdate = false;
    return this;
  };
  return err;
}
if (Object.setPrototypeOf) {
  Object.setPrototypeOf(TemplateError.prototype, Error.prototype);
} else {
  TemplateError.prototype = Object.create(Error.prototype, {
    constructor: {
      value: TemplateError
    }
  });
}
exports.TemplateError = TemplateError;
function escape(val) {
  return val.replace(escapeRegex, lookupEscape);
}
exports.escape = escape;
function isFunction(obj) {
  return ObjProto.toString.call(obj) === '[object Function]';
}
exports.isFunction = isFunction;
function isArray(obj) {
  return ObjProto.toString.call(obj) === '[object Array]';
}
exports.isArray = isArray;
function isString(obj) {
  return ObjProto.toString.call(obj) === '[object String]';
}
exports.isString = isString;
function isObject(obj) {
  return ObjProto.toString.call(obj) === '[object Object]';
}
exports.isObject = isObject;

/**
 * @param {string|number} attr
 * @returns {(string|number)[]}
 * @private
 */
function _prepareAttributeParts(attr) {
  if (!attr) {
    return [];
  }
  if (typeof attr === 'string') {
    return attr.split('.');
  }
  return [attr];
}

/**
 * @param {string}   attribute      Attribute value. Dots allowed.
 * @returns {function(Object): *}
 */
function getAttrGetter(attribute) {
  var parts = _prepareAttributeParts(attribute);
  return function attrGetter(item) {
    var _item = item;
    for (var i = 0; i < parts.length; i++) {
      var part = parts[i];

      // If item is not an object, and we still got parts to handle, it means
      // that something goes wrong. Just roll out to undefined in that case.
      if (hasOwnProp(_item, part)) {
        _item = _item[part];
      } else {
        return undefined;
      }
    }
    return _item;
  };
}
exports.getAttrGetter = getAttrGetter;
function groupBy(obj, val, throwOnUndefined) {
  var result = {};
  var iterator = isFunction(val) ? val : getAttrGetter(val);
  for (var i = 0; i < obj.length; i++) {
    var value = obj[i];
    var key = iterator(value, i);
    if (key === undefined && throwOnUndefined === true) {
      throw new TypeError("groupby: attribute \"" + val + "\" resolved to undefined");
    }
    (result[key] || (result[key] = [])).push(value);
  }
  return result;
}
exports.groupBy = groupBy;
function toArray(obj) {
  return Array.prototype.slice.call(obj);
}
exports.toArray = toArray;
function without(array) {
  var result = [];
  if (!array) {
    return result;
  }
  var length = array.length;
  var contains = toArray(arguments).slice(1);
  var index = -1;
  while (++index < length) {
    if (indexOf(contains, array[index]) === -1) {
      result.push(array[index]);
    }
  }
  return result;
}
exports.without = without;
function repeat(char_, n) {
  var str = '';
  for (var i = 0; i < n; i++) {
    str += char_;
  }
  return str;
}
exports.repeat = repeat;
function each(obj, func, context) {
  if (obj == null) {
    return;
  }
  if (ArrayProto.forEach && obj.forEach === ArrayProto.forEach) {
    obj.forEach(func, context);
  } else if (obj.length === +obj.length) {
    for (var i = 0, l = obj.length; i < l; i++) {
      func.call(context, obj[i], i, obj);
    }
  }
}
exports.each = each;
function map(obj, func) {
  var results = [];
  if (obj == null) {
    return results;
  }
  if (ArrayProto.map && obj.map === ArrayProto.map) {
    return obj.map(func);
  }
  for (var i = 0; i < obj.length; i++) {
    results[results.length] = func(obj[i], i);
  }
  if (obj.length === +obj.length) {
    results.length = obj.length;
  }
  return results;
}
exports.map = map;
function asyncIter(arr, iter, cb) {
  var i = -1;
  function next() {
    i++;
    if (i < arr.length) {
      iter(arr[i], i, next, cb);
    } else {
      cb();
    }
  }
  next();
}
exports.asyncIter = asyncIter;
function asyncFor(obj, iter, cb) {
  var keys = keys_(obj || {});
  var len = keys.length;
  var i = -1;
  function next() {
    i++;
    var k = keys[i];
    if (i < len) {
      iter(k, obj[k], i, len, next);
    } else {
      cb();
    }
  }
  next();
}
exports.asyncFor = asyncFor;
function indexOf(arr, searchElement, fromIndex) {
  return Array.prototype.indexOf.call(arr || [], searchElement, fromIndex);
}
exports.indexOf = indexOf;
function keys_(obj) {
  /* eslint-disable no-restricted-syntax */
  var arr = [];
  for (var k in obj) {
    if (hasOwnProp(obj, k)) {
      arr.push(k);
    }
  }
  return arr;
}
exports.keys = keys_;
function _entries(obj) {
  return keys_(obj).map(function (k) {
    return [k, obj[k]];
  });
}
exports._entries = _entries;
function _values(obj) {
  return keys_(obj).map(function (k) {
    return obj[k];
  });
}
exports._values = _values;
function extend(obj1, obj2) {
  obj1 = obj1 || {};
  keys_(obj2).forEach(function (k) {
    obj1[k] = obj2[k];
  });
  return obj1;
}
exports._assign = exports.extend = extend;
function inOperator(key, val) {
  if (isArray(val) || isString(val)) {
    return val.indexOf(key) !== -1;
  } else if (isObject(val)) {
    return key in val;
  }
  throw new Error('Cannot use "in" operator to search for "' + key + '" in unexpected types.');
}
exports.inOperator = inOperator;

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var lib = __webpack_require__(1);
var arrayFrom = Array.from;
var supportsIterators = typeof Symbol === 'function' && Symbol.iterator && typeof arrayFrom === 'function';

// Frames keep track of scoping both at compile-time and run-time so
// we know how to access variables. Block tags can introduce special
// variables, for example.
var Frame = /*#__PURE__*/function () {
  function Frame(parent, isolateWrites) {
    this.variables = Object.create(null);
    this.parent = parent;
    this.topLevel = false;
    // if this is true, writes (set) should never propagate upwards past
    // this frame to its parent (though reads may).
    this.isolateWrites = isolateWrites;
  }
  var _proto = Frame.prototype;
  _proto.set = function set(name, val, resolveUp) {
    // Allow variables with dots by automatically creating the
    // nested structure
    var parts = name.split('.');
    var obj = this.variables;
    var frame = this;
    if (resolveUp) {
      if (frame = this.resolve(parts[0], true)) {
        frame.set(name, val);
        return;
      }
    }
    for (var i = 0; i < parts.length - 1; i++) {
      var id = parts[i];
      if (!obj[id]) {
        obj[id] = {};
      }
      obj = obj[id];
    }
    obj[parts[parts.length - 1]] = val;
  };
  _proto.get = function get(name) {
    var val = this.variables[name];
    if (val !== undefined) {
      return val;
    }
    return null;
  };
  _proto.lookup = function lookup(name) {
    var p = this.parent;
    var val = this.variables[name];
    if (val !== undefined) {
      return val;
    }
    return p && p.lookup(name);
  };
  _proto.resolve = function resolve(name, forWrite) {
    var p = forWrite && this.isolateWrites ? undefined : this.parent;
    var val = this.variables[name];
    if (val !== undefined) {
      return this;
    }
    return p && p.resolve(name);
  };
  _proto.push = function push(isolateWrites) {
    return new Frame(this, isolateWrites);
  };
  _proto.pop = function pop() {
    return this.parent;
  };
  return Frame;
}();
function makeMacro(argNames, kwargNames, func) {
  return function macro() {
    for (var _len = arguments.length, macroArgs = new Array(_len), _key = 0; _key < _len; _key++) {
      macroArgs[_key] = arguments[_key];
    }
    var argCount = numArgs(macroArgs);
    var args;
    var kwargs = getKeywordArgs(macroArgs);
    if (argCount > argNames.length) {
      args = macroArgs.slice(0, argNames.length);

      // Positional arguments that should be passed in as
      // keyword arguments (essentially default values)
      macroArgs.slice(args.length, argCount).forEach(function (val, i) {
        if (i < kwargNames.length) {
          kwargs[kwargNames[i]] = val;
        }
      });
      args.push(kwargs);
    } else if (argCount < argNames.length) {
      args = macroArgs.slice(0, argCount);
      for (var i = argCount; i < argNames.length; i++) {
        var arg = argNames[i];

        // Keyword arguments that should be passed as
        // positional arguments, i.e. the caller explicitly
        // used the name of a positional arg
        args.push(kwargs[arg]);
        delete kwargs[arg];
      }
      args.push(kwargs);
    } else {
      args = macroArgs;
    }
    return func.apply(this, args);
  };
}
function makeKeywordArgs(obj) {
  obj.__keywords = true;
  return obj;
}
function isKeywordArgs(obj) {
  return obj && Object.prototype.hasOwnProperty.call(obj, '__keywords');
}
function getKeywordArgs(args) {
  var len = args.length;
  if (len) {
    var lastArg = args[len - 1];
    if (isKeywordArgs(lastArg)) {
      return lastArg;
    }
  }
  return {};
}
function numArgs(args) {
  var len = args.length;
  if (len === 0) {
    return 0;
  }
  var lastArg = args[len - 1];
  if (isKeywordArgs(lastArg)) {
    return len - 1;
  } else {
    return len;
  }
}

// A SafeString object indicates that the string should not be
// autoescaped. This happens magically because autoescaping only
// occurs on primitive string objects.
function SafeString(val) {
  if (typeof val !== 'string') {
    return val;
  }
  this.val = val;
  this.length = val.length;
}
SafeString.prototype = Object.create(String.prototype, {
  length: {
    writable: true,
    configurable: true,
    value: 0
  }
});
SafeString.prototype.valueOf = function valueOf() {
  return this.val;
};
SafeString.prototype.toString = function toString() {
  return this.val;
};
function copySafeness(dest, target) {
  if (dest instanceof SafeString) {
    return new SafeString(target);
  }
  return target.toString();
}
function markSafe(val) {
  var type = typeof val;
  if (type === 'string') {
    return new SafeString(val);
  } else if (type !== 'function') {
    return val;
  } else {
    return function wrapSafe(args) {
      var ret = val.apply(this, arguments);
      if (typeof ret === 'string') {
        return new SafeString(ret);
      }
      return ret;
    };
  }
}
function suppressValue(val, autoescape) {
  val = val !== undefined && val !== null ? val : '';
  if (autoescape && !(val instanceof SafeString)) {
    val = lib.escape(val.toString());
  }
  return val;
}
function ensureDefined(val, lineno, colno) {
  if (val === null || val === undefined) {
    throw new lib.TemplateError('attempted to output null or undefined value', lineno + 1, colno + 1);
  }
  return val;
}
function memberLookup(obj, val) {
  if (obj === undefined || obj === null) {
    return undefined;
  }
  if (typeof obj[val] === 'function') {
    return function () {
      for (var _len2 = arguments.length, args = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
        args[_key2] = arguments[_key2];
      }
      return obj[val].apply(obj, args);
    };
  }
  return obj[val];
}
function callWrap(obj, name, context, args) {
  if (!obj) {
    throw new Error('Unable to call `' + name + '`, which is undefined or falsey');
  } else if (typeof obj !== 'function') {
    throw new Error('Unable to call `' + name + '`, which is not a function');
  }
  return obj.apply(context, args);
}
function contextOrFrameLookup(context, frame, name) {
  var val = frame.lookup(name);
  return val !== undefined ? val : context.lookup(name);
}
function handleError(error, lineno, colno) {
  if (error.lineno) {
    return error;
  } else {
    return new lib.TemplateError(error, lineno, colno);
  }
}
function asyncEach(arr, dimen, iter, cb) {
  if (lib.isArray(arr)) {
    var len = arr.length;
    lib.asyncIter(arr, function iterCallback(item, i, next) {
      switch (dimen) {
        case 1:
          iter(item, i, len, next);
          break;
        case 2:
          iter(item[0], item[1], i, len, next);
          break;
        case 3:
          iter(item[0], item[1], item[2], i, len, next);
          break;
        default:
          item.push(i, len, next);
          iter.apply(this, item);
      }
    }, cb);
  } else {
    lib.asyncFor(arr, function iterCallback(key, val, i, len, next) {
      iter(key, val, i, len, next);
    }, cb);
  }
}
function asyncAll(arr, dimen, func, cb) {
  var finished = 0;
  var len;
  var outputArr;
  function done(i, output) {
    finished++;
    outputArr[i] = output;
    if (finished === len) {
      cb(null, outputArr.join(''));
    }
  }
  if (lib.isArray(arr)) {
    len = arr.length;
    outputArr = new Array(len);
    if (len === 0) {
      cb(null, '');
    } else {
      for (var i = 0; i < arr.length; i++) {
        var item = arr[i];
        switch (dimen) {
          case 1:
            func(item, i, len, done);
            break;
          case 2:
            func(item[0], item[1], i, len, done);
            break;
          case 3:
            func(item[0], item[1], item[2], i, len, done);
            break;
          default:
            item.push(i, len, done);
            func.apply(this, item);
        }
      }
    }
  } else {
    var keys = lib.keys(arr || {});
    len = keys.length;
    outputArr = new Array(len);
    if (len === 0) {
      cb(null, '');
    } else {
      for (var _i = 0; _i < keys.length; _i++) {
        var k = keys[_i];
        func(k, arr[k], _i, len, done);
      }
    }
  }
}
function fromIterator(arr) {
  if (typeof arr !== 'object' || arr === null || lib.isArray(arr)) {
    return arr;
  } else if (supportsIterators && Symbol.iterator in arr) {
    return arrayFrom(arr);
  } else {
    return arr;
  }
}
module.exports = {
  Frame: Frame,
  makeMacro: makeMacro,
  makeKeywordArgs: makeKeywordArgs,
  numArgs: numArgs,
  suppressValue: suppressValue,
  ensureDefined: ensureDefined,
  memberLookup: memberLookup,
  contextOrFrameLookup: contextOrFrameLookup,
  callWrap: callWrap,
  handleError: handleError,
  isArray: lib.isArray,
  keys: lib.keys,
  SafeString: SafeString,
  copySafeness: copySafeness,
  markSafe: markSafe,
  asyncEach: asyncEach,
  asyncAll: asyncAll,
  inOperator: lib.inOperator,
  fromIterator: fromIterator
};

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


function _inheritsLoose(subClass, superClass) { subClass.prototype = Object.create(superClass.prototype); subClass.prototype.constructor = subClass; _setPrototypeOf(subClass, superClass); }
function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }
var Loader = __webpack_require__(4);
var PrecompiledLoader = /*#__PURE__*/function (_Loader) {
  _inheritsLoose(PrecompiledLoader, _Loader);
  function PrecompiledLoader(compiledTemplates) {
    var _this;
    _this = _Loader.call(this) || this;
    _this.precompiled = compiledTemplates || {};
    return _this;
  }
  var _proto = PrecompiledLoader.prototype;
  _proto.getSource = function getSource(name) {
    if (this.precompiled[name]) {
      return {
        src: {
          type: 'code',
          obj: this.precompiled[name]
        },
        path: name
      };
    }
    return null;
  };
  return PrecompiledLoader;
}(Loader);
module.exports = {
  PrecompiledLoader: PrecompiledLoader
};

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


function _inheritsLoose(subClass, superClass) { subClass.prototype = Object.create(superClass.prototype); subClass.prototype.constructor = subClass; _setPrototypeOf(subClass, superClass); }
function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }
var path = __webpack_require__(0);
var _require = __webpack_require__(5),
  EmitterObj = _require.EmitterObj;
module.exports = /*#__PURE__*/function (_EmitterObj) {
  _inheritsLoose(Loader, _EmitterObj);
  function Loader() {
    return _EmitterObj.apply(this, arguments) || this;
  }
  var _proto = Loader.prototype;
  _proto.resolve = function resolve(from, to) {
    return path.resolve(path.dirname(from), to);
  };
  _proto.isRelative = function isRelative(filename) {
    return filename.indexOf('./') === 0 || filename.indexOf('../') === 0;
  };
  return Loader;
}(EmitterObj);

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


// A simple class system, more documentation to come
function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, _toPropertyKey(descriptor.key), descriptor); } }
function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); Object.defineProperty(Constructor, "prototype", { writable: false }); return Constructor; }
function _toPropertyKey(arg) { var key = _toPrimitive(arg, "string"); return typeof key === "symbol" ? key : String(key); }
function _toPrimitive(input, hint) { if (typeof input !== "object" || input === null) return input; var prim = input[Symbol.toPrimitive]; if (prim !== undefined) { var res = prim.call(input, hint || "default"); if (typeof res !== "object") return res; throw new TypeError("@@toPrimitive must return a primitive value."); } return (hint === "string" ? String : Number)(input); }
function _inheritsLoose(subClass, superClass) { subClass.prototype = Object.create(superClass.prototype); subClass.prototype.constructor = subClass; _setPrototypeOf(subClass, superClass); }
function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }
var EventEmitter = __webpack_require__(13);
var lib = __webpack_require__(1);
function parentWrap(parent, prop) {
  if (typeof parent !== 'function' || typeof prop !== 'function') {
    return prop;
  }
  return function wrap() {
    // Save the current parent method
    var tmp = this.parent;

    // Set parent to the previous method, call, and restore
    this.parent = parent;
    var res = prop.apply(this, arguments);
    this.parent = tmp;
    return res;
  };
}
function extendClass(cls, name, props) {
  props = props || {};
  lib.keys(props).forEach(function (k) {
    props[k] = parentWrap(cls.prototype[k], props[k]);
  });
  var subclass = /*#__PURE__*/function (_cls) {
    _inheritsLoose(subclass, _cls);
    function subclass() {
      return _cls.apply(this, arguments) || this;
    }
    _createClass(subclass, [{
      key: "typename",
      get: function get() {
        return name;
      }
    }]);
    return subclass;
  }(cls);
  lib._assign(subclass.prototype, props);
  return subclass;
}
var Obj = /*#__PURE__*/function () {
  function Obj() {
    // Unfortunately necessary for backwards compatibility
    this.init.apply(this, arguments);
  }
  var _proto = Obj.prototype;
  _proto.init = function init() {};
  Obj.extend = function extend(name, props) {
    if (typeof name === 'object') {
      props = name;
      name = 'anonymous';
    }
    return extendClass(this, name, props);
  };
  _createClass(Obj, [{
    key: "typename",
    get: function get() {
      return this.constructor.name;
    }
  }]);
  return Obj;
}();
var EmitterObj = /*#__PURE__*/function (_EventEmitter) {
  _inheritsLoose(EmitterObj, _EventEmitter);
  function EmitterObj() {
    var _this2;
    var _this;
    _this = _EventEmitter.call(this) || this;
    // Unfortunately necessary for backwards compatibility
    (_this2 = _this).init.apply(_this2, arguments);
    return _this;
  }
  var _proto2 = EmitterObj.prototype;
  _proto2.init = function init() {};
  EmitterObj.extend = function extend(name, props) {
    if (typeof name === 'object') {
      props = name;
      name = 'anonymous';
    }
    return extendClass(this, name, props);
  };
  _createClass(EmitterObj, [{
    key: "typename",
    get: function get() {
      return this.constructor.name;
    }
  }]);
  return EmitterObj;
}(EventEmitter);
module.exports = {
  Obj: Obj,
  EmitterObj: EmitterObj
};

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var lib = __webpack_require__(1);
var _require = __webpack_require__(7),
  Environment = _require.Environment,
  Template = _require.Template;
var Loader = __webpack_require__(4);
var loaders = __webpack_require__(3);
var precompile = __webpack_require__(0);
var compiler = __webpack_require__(0);
var parser = __webpack_require__(0);
var lexer = __webpack_require__(0);
var runtime = __webpack_require__(2);
var nodes = __webpack_require__(0);
var installJinjaCompat = __webpack_require__(17);

// A single instance of an environment, since this is so commonly used
var e;
function configure(templatesPath, opts) {
  opts = opts || {};
  if (lib.isObject(templatesPath)) {
    opts = templatesPath;
    templatesPath = null;
  }
  var TemplateLoader;
  if (loaders.FileSystemLoader) {
    TemplateLoader = new loaders.FileSystemLoader(templatesPath, {
      watch: opts.watch,
      noCache: opts.noCache
    });
  } else if (loaders.WebLoader) {
    TemplateLoader = new loaders.WebLoader(templatesPath, {
      useCache: opts.web && opts.web.useCache,
      async: opts.web && opts.web.async
    });
  }
  e = new Environment(TemplateLoader, opts);
  if (opts && opts.express) {
    e.express(opts.express);
  }
  return e;
}
module.exports = {
  Environment: Environment,
  Template: Template,
  Loader: Loader,
  FileSystemLoader: loaders.FileSystemLoader,
  NodeResolveLoader: loaders.NodeResolveLoader,
  PrecompiledLoader: loaders.PrecompiledLoader,
  WebLoader: loaders.WebLoader,
  compiler: compiler,
  parser: parser,
  lexer: lexer,
  runtime: runtime,
  lib: lib,
  nodes: nodes,
  installJinjaCompat: installJinjaCompat,
  configure: configure,
  reset: function reset() {
    e = undefined;
  },
  compile: function compile(src, env, path, eagerCompile) {
    if (!e) {
      configure();
    }
    return new Template(src, env, path, eagerCompile);
  },
  render: function render(name, ctx, cb) {
    if (!e) {
      configure();
    }
    return e.render(name, ctx, cb);
  },
  renderString: function renderString(src, ctx, cb) {
    if (!e) {
      configure();
    }
    return e.renderString(src, ctx, cb);
  },
  precompile: precompile ? precompile.precompile : undefined,
  precompileString: precompile ? precompile.precompileString : undefined
};

/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


function _inheritsLoose(subClass, superClass) { subClass.prototype = Object.create(superClass.prototype); subClass.prototype.constructor = subClass; _setPrototypeOf(subClass, superClass); }
function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }
var asap = __webpack_require__(8);
var _waterfall = __webpack_require__(11);
var lib = __webpack_require__(1);
var compiler = __webpack_require__(0);
var filters = __webpack_require__(12);
var _require = __webpack_require__(3),
  FileSystemLoader = _require.FileSystemLoader,
  WebLoader = _require.WebLoader,
  PrecompiledLoader = _require.PrecompiledLoader;
var tests = __webpack_require__(14);
var globals = __webpack_require__(15);
var _require2 = __webpack_require__(5),
  Obj = _require2.Obj,
  EmitterObj = _require2.EmitterObj;
var globalRuntime = __webpack_require__(2);
var handleError = globalRuntime.handleError,
  Frame = globalRuntime.Frame;
var expressApp = __webpack_require__(16);

// If the user is using the async API, *always* call it
// asynchronously even if the template was synchronous.
function callbackAsap(cb, err, res) {
  asap(function () {
    cb(err, res);
  });
}

/**
 * A no-op template, for use with {% include ignore missing %}
 */
var noopTmplSrc = {
  type: 'code',
  obj: {
    root: function root(env, context, frame, runtime, cb) {
      try {
        cb(null, '');
      } catch (e) {
        cb(handleError(e, null, null));
      }
    }
  }
};
var Environment = /*#__PURE__*/function (_EmitterObj) {
  _inheritsLoose(Environment, _EmitterObj);
  function Environment() {
    return _EmitterObj.apply(this, arguments) || this;
  }
  var _proto = Environment.prototype;
  _proto.init = function init(loaders, opts) {
    var _this = this;
    // The dev flag determines the trace that'll be shown on errors.
    // If set to true, returns the full trace from the error point,
    // otherwise will return trace starting from Template.render
    // (the full trace from within nunjucks may confuse developers using
    //  the library)
    // defaults to false
    opts = this.opts = opts || {};
    this.opts.dev = !!opts.dev;

    // The autoescape flag sets global autoescaping. If true,
    // every string variable will be escaped by default.
    // If false, strings can be manually escaped using the `escape` filter.
    // defaults to true
    this.opts.autoescape = opts.autoescape != null ? opts.autoescape : true;

    // If true, this will make the system throw errors if trying
    // to output a null or undefined value
    this.opts.throwOnUndefined = !!opts.throwOnUndefined;
    this.opts.trimBlocks = !!opts.trimBlocks;
    this.opts.lstripBlocks = !!opts.lstripBlocks;
    this.loaders = [];
    if (!loaders) {
      // The filesystem loader is only available server-side
      if (FileSystemLoader) {
        this.loaders = [new FileSystemLoader('views')];
      } else if (WebLoader) {
        this.loaders = [new WebLoader('/views')];
      }
    } else {
      this.loaders = lib.isArray(loaders) ? loaders : [loaders];
    }

    // It's easy to use precompiled templates: just include them
    // before you configure nunjucks and this will automatically
    // pick it up and use it
    if (typeof window !== 'undefined' && window.nunjucksPrecompiled) {
      this.loaders.unshift(new PrecompiledLoader(window.nunjucksPrecompiled));
    }
    this._initLoaders();
    this.globals = globals();
    this.filters = {};
    this.tests = {};
    this.asyncFilters = [];
    this.extensions = {};
    this.extensionsList = [];
    lib._entries(filters).forEach(function (_ref) {
      var name = _ref[0],
        filter = _ref[1];
      return _this.addFilter(name, filter);
    });
    lib._entries(tests).forEach(function (_ref2) {
      var name = _ref2[0],
        test = _ref2[1];
      return _this.addTest(name, test);
    });
  };
  _proto._initLoaders = function _initLoaders() {
    var _this2 = this;
    this.loaders.forEach(function (loader) {
      // Caching and cache busting
      loader.cache = {};
      if (typeof loader.on === 'function') {
        loader.on('update', function (name, fullname) {
          loader.cache[name] = null;
          _this2.emit('update', name, fullname, loader);
        });
        loader.on('load', function (name, source) {
          _this2.emit('load', name, source, loader);
        });
      }
    });
  };
  _proto.invalidateCache = function invalidateCache() {
    this.loaders.forEach(function (loader) {
      loader.cache = {};
    });
  };
  _proto.addExtension = function addExtension(name, extension) {
    extension.__name = name;
    this.extensions[name] = extension;
    this.extensionsList.push(extension);
    return this;
  };
  _proto.removeExtension = function removeExtension(name) {
    var extension = this.getExtension(name);
    if (!extension) {
      return;
    }
    this.extensionsList = lib.without(this.extensionsList, extension);
    delete this.extensions[name];
  };
  _proto.getExtension = function getExtension(name) {
    return this.extensions[name];
  };
  _proto.hasExtension = function hasExtension(name) {
    return !!this.extensions[name];
  };
  _proto.addGlobal = function addGlobal(name, value) {
    this.globals[name] = value;
    return this;
  };
  _proto.getGlobal = function getGlobal(name) {
    if (typeof this.globals[name] === 'undefined') {
      throw new Error('global not found: ' + name);
    }
    return this.globals[name];
  };
  _proto.addFilter = function addFilter(name, func, async) {
    var wrapped = func;
    if (async) {
      this.asyncFilters.push(name);
    }
    this.filters[name] = wrapped;
    return this;
  };
  _proto.getFilter = function getFilter(name) {
    if (!this.filters[name]) {
      throw new Error('filter not found: ' + name);
    }
    return this.filters[name];
  };
  _proto.addTest = function addTest(name, func) {
    this.tests[name] = func;
    return this;
  };
  _proto.getTest = function getTest(name) {
    if (!this.tests[name]) {
      throw new Error('test not found: ' + name);
    }
    return this.tests[name];
  };
  _proto.resolveTemplate = function resolveTemplate(loader, parentName, filename) {
    var isRelative = loader.isRelative && parentName ? loader.isRelative(filename) : false;
    return isRelative && loader.resolve ? loader.resolve(parentName, filename) : filename;
  };
  _proto.getTemplate = function getTemplate(name, eagerCompile, parentName, ignoreMissing, cb) {
    var _this3 = this;
    var that = this;
    var tmpl = null;
    if (name && name.raw) {
      // this fixes autoescape for templates referenced in symbols
      name = name.raw;
    }
    if (lib.isFunction(parentName)) {
      cb = parentName;
      parentName = null;
      eagerCompile = eagerCompile || false;
    }
    if (lib.isFunction(eagerCompile)) {
      cb = eagerCompile;
      eagerCompile = false;
    }
    if (name instanceof Template) {
      tmpl = name;
    } else if (typeof name !== 'string') {
      throw new Error('template names must be a string: ' + name);
    } else {
      for (var i = 0; i < this.loaders.length; i++) {
        var loader = this.loaders[i];
        tmpl = loader.cache[this.resolveTemplate(loader, parentName, name)];
        if (tmpl) {
          break;
        }
      }
    }
    if (tmpl) {
      if (eagerCompile) {
        tmpl.compile();
      }
      if (cb) {
        cb(null, tmpl);
        return undefined;
      } else {
        return tmpl;
      }
    }
    var syncResult;
    var createTemplate = function createTemplate(err, info) {
      if (!info && !err && !ignoreMissing) {
        err = new Error('template not found: ' + name);
      }
      if (err) {
        if (cb) {
          cb(err);
          return;
        } else {
          throw err;
        }
      }
      var newTmpl;
      if (!info) {
        newTmpl = new Template(noopTmplSrc, _this3, '', eagerCompile);
      } else {
        newTmpl = new Template(info.src, _this3, info.path, eagerCompile);
        if (!info.noCache) {
          info.loader.cache[name] = newTmpl;
        }
      }
      if (cb) {
        cb(null, newTmpl);
      } else {
        syncResult = newTmpl;
      }
    };
    lib.asyncIter(this.loaders, function (loader, i, next, done) {
      function handle(err, src) {
        if (err) {
          done(err);
        } else if (src) {
          src.loader = loader;
          done(null, src);
        } else {
          next();
        }
      }

      // Resolve name relative to parentName
      name = that.resolveTemplate(loader, parentName, name);
      if (loader.async) {
        loader.getSource(name, handle);
      } else {
        handle(null, loader.getSource(name));
      }
    }, createTemplate);
    return syncResult;
  };
  _proto.express = function express(app) {
    return expressApp(this, app);
  };
  _proto.render = function render(name, ctx, cb) {
    if (lib.isFunction(ctx)) {
      cb = ctx;
      ctx = null;
    }

    // We support a synchronous API to make it easier to migrate
    // existing code to async. This works because if you don't do
    // anything async work, the whole thing is actually run
    // synchronously.
    var syncResult = null;
    this.getTemplate(name, function (err, tmpl) {
      if (err && cb) {
        callbackAsap(cb, err);
      } else if (err) {
        throw err;
      } else {
        syncResult = tmpl.render(ctx, cb);
      }
    });
    return syncResult;
  };
  _proto.renderString = function renderString(src, ctx, opts, cb) {
    if (lib.isFunction(opts)) {
      cb = opts;
      opts = {};
    }
    opts = opts || {};
    var tmpl = new Template(src, this, opts.path);
    return tmpl.render(ctx, cb);
  };
  _proto.waterfall = function waterfall(tasks, callback, forceAsync) {
    return _waterfall(tasks, callback, forceAsync);
  };
  return Environment;
}(EmitterObj);
var Context = /*#__PURE__*/function (_Obj) {
  _inheritsLoose(Context, _Obj);
  function Context() {
    return _Obj.apply(this, arguments) || this;
  }
  var _proto2 = Context.prototype;
  _proto2.init = function init(ctx, blocks, env) {
    var _this4 = this;
    // Has to be tied to an environment so we can tap into its globals.
    this.env = env || new Environment();

    // Make a duplicate of ctx
    this.ctx = lib.extend({}, ctx);
    this.blocks = {};
    this.exported = [];
    lib.keys(blocks).forEach(function (name) {
      _this4.addBlock(name, blocks[name]);
    });
  };
  _proto2.lookup = function lookup(name) {
    // This is one of the most called functions, so optimize for
    // the typical case where the name isn't in the globals
    if (name in this.env.globals && !(name in this.ctx)) {
      return this.env.globals[name];
    } else {
      return this.ctx[name];
    }
  };
  _proto2.setVariable = function setVariable(name, val) {
    this.ctx[name] = val;
  };
  _proto2.getVariables = function getVariables() {
    return this.ctx;
  };
  _proto2.addBlock = function addBlock(name, block) {
    this.blocks[name] = this.blocks[name] || [];
    this.blocks[name].push(block);
    return this;
  };
  _proto2.getBlock = function getBlock(name) {
    if (!this.blocks[name]) {
      throw new Error('unknown block "' + name + '"');
    }
    return this.blocks[name][0];
  };
  _proto2.getSuper = function getSuper(env, name, block, frame, runtime, cb) {
    var idx = lib.indexOf(this.blocks[name] || [], block);
    var blk = this.blocks[name][idx + 1];
    var context = this;
    if (idx === -1 || !blk) {
      throw new Error('no super block available for "' + name + '"');
    }
    blk(env, context, frame, runtime, cb);
  };
  _proto2.addExport = function addExport(name) {
    this.exported.push(name);
  };
  _proto2.getExported = function getExported() {
    var _this5 = this;
    var exported = {};
    this.exported.forEach(function (name) {
      exported[name] = _this5.ctx[name];
    });
    return exported;
  };
  return Context;
}(Obj);
var Template = /*#__PURE__*/function (_Obj2) {
  _inheritsLoose(Template, _Obj2);
  function Template() {
    return _Obj2.apply(this, arguments) || this;
  }
  var _proto3 = Template.prototype;
  _proto3.init = function init(src, env, path, eagerCompile) {
    this.env = env || new Environment();
    if (lib.isObject(src)) {
      switch (src.type) {
        case 'code':
          this.tmplProps = src.obj;
          break;
        case 'string':
          this.tmplStr = src.obj;
          break;
        default:
          throw new Error("Unexpected template object type " + src.type + "; expected 'code', or 'string'");
      }
    } else if (lib.isString(src)) {
      this.tmplStr = src;
    } else {
      throw new Error('src must be a string or an object describing the source');
    }
    this.path = path;
    if (eagerCompile) {
      try {
        this._compile();
      } catch (err) {
        throw lib._prettifyError(this.path, this.env.opts.dev, err);
      }
    } else {
      this.compiled = false;
    }
  };
  _proto3.render = function render(ctx, parentFrame, cb) {
    var _this6 = this;
    if (typeof ctx === 'function') {
      cb = ctx;
      ctx = {};
    } else if (typeof parentFrame === 'function') {
      cb = parentFrame;
      parentFrame = null;
    }

    // If there is a parent frame, we are being called from internal
    // code of another template, and the internal system
    // depends on the sync/async nature of the parent template
    // to be inherited, so force an async callback
    var forceAsync = !parentFrame;

    // Catch compile errors for async rendering
    try {
      this.compile();
    } catch (e) {
      var err = lib._prettifyError(this.path, this.env.opts.dev, e);
      if (cb) {
        return callbackAsap(cb, err);
      } else {
        throw err;
      }
    }
    var context = new Context(ctx || {}, this.blocks, this.env);
    var frame = parentFrame ? parentFrame.push(true) : new Frame();
    frame.topLevel = true;
    var syncResult = null;
    var didError = false;
    this.rootRenderFunc(this.env, context, frame, globalRuntime, function (err, res) {
      // TODO: this is actually a bug in the compiled template (because waterfall
      // tasks are both not passing errors up the chain of callbacks AND are not
      // causing a return from the top-most render function). But fixing that
      // will require a more substantial change to the compiler.
      if (didError && cb && typeof res !== 'undefined') {
        // prevent multiple calls to cb
        return;
      }
      if (err) {
        err = lib._prettifyError(_this6.path, _this6.env.opts.dev, err);
        didError = true;
      }
      if (cb) {
        if (forceAsync) {
          callbackAsap(cb, err, res);
        } else {
          cb(err, res);
        }
      } else {
        if (err) {
          throw err;
        }
        syncResult = res;
      }
    });
    return syncResult;
  };
  _proto3.getExported = function getExported(ctx, parentFrame, cb) {
    // eslint-disable-line consistent-return
    if (typeof ctx === 'function') {
      cb = ctx;
      ctx = {};
    }
    if (typeof parentFrame === 'function') {
      cb = parentFrame;
      parentFrame = null;
    }

    // Catch compile errors for async rendering
    try {
      this.compile();
    } catch (e) {
      if (cb) {
        return cb(e);
      } else {
        throw e;
      }
    }
    var frame = parentFrame ? parentFrame.push() : new Frame();
    frame.topLevel = true;

    // Run the rootRenderFunc to populate the context with exported vars
    var context = new Context(ctx || {}, this.blocks, this.env);
    this.rootRenderFunc(this.env, context, frame, globalRuntime, function (err) {
      if (err) {
        cb(err, null);
      } else {
        cb(null, context.getExported());
      }
    });
  };
  _proto3.compile = function compile() {
    if (!this.compiled) {
      this._compile();
    }
  };
  _proto3._compile = function _compile() {
    var props;
    if (this.tmplProps) {
      props = this.tmplProps;
    } else {
      var source = compiler.compile(this.tmplStr, this.env.asyncFilters, this.env.extensionsList, this.path, this.env.opts);
      var func = new Function(source); // eslint-disable-line no-new-func
      props = func();
    }
    this.blocks = this._getBlocks(props);
    this.rootRenderFunc = props.root;
    this.compiled = true;
  };
  _proto3._getBlocks = function _getBlocks(props) {
    var blocks = {};
    lib.keys(props).forEach(function (k) {
      if (k.slice(0, 2) === 'b_') {
        blocks[k.slice(2)] = props[k];
      }
    });
    return blocks;
  };
  return Template;
}(Obj);
module.exports = {
  Environment: Environment,
  Template: Template
};

/***/ }),
/* 8 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


// rawAsap provides everything we need except exception management.
var rawAsap = __webpack_require__(9);
// RawTasks are recycled to reduce GC churn.
var freeTasks = [];
// We queue errors to ensure they are thrown in right order (FIFO).
// Array-as-queue is good enough here, since we are just dealing with exceptions.
var pendingErrors = [];
var requestErrorThrow = rawAsap.makeRequestCallFromTimer(throwFirstError);

function throwFirstError() {
    if (pendingErrors.length) {
        throw pendingErrors.shift();
    }
}

/**
 * Calls a task as soon as possible after returning, in its own event, with priority
 * over other events like animation, reflow, and repaint. An error thrown from an
 * event will not interrupt, nor even substantially slow down the processing of
 * other events, but will be rather postponed to a lower priority event.
 * @param {{call}} task A callable object, typically a function that takes no
 * arguments.
 */
module.exports = asap;
function asap(task) {
    var rawTask;
    if (freeTasks.length) {
        rawTask = freeTasks.pop();
    } else {
        rawTask = new RawTask();
    }
    rawTask.task = task;
    rawAsap(rawTask);
}

// We wrap tasks with recyclable task objects.  A task object implements
// `call`, just like a function.
function RawTask() {
    this.task = null;
}

// The sole purpose of wrapping the task is to catch the exception and recycle
// the task object after its single use.
RawTask.prototype.call = function () {
    try {
        this.task.call();
    } catch (error) {
        if (asap.onerror) {
            // This hook exists purely for testing purposes.
            // Its name will be periodically randomized to break any code that
            // depends on its existence.
            asap.onerror(error);
        } else {
            // In a web browser, exceptions are not fatal. However, to avoid
            // slowing down the queue of pending tasks, we rethrow the error in a
            // lower priority turn.
            pendingErrors.push(error);
            requestErrorThrow();
        }
    } finally {
        this.task = null;
        freeTasks[freeTasks.length] = this;
    }
};


/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(global) {

// Use the fastest means possible to execute a task in its own turn, with
// priority over other events including IO, animation, reflow, and redraw
// events in browsers.
//
// An exception thrown by a task will permanently interrupt the processing of
// subsequent tasks. The higher level `asap` function ensures that if an
// exception is thrown by a task, that the task queue will continue flushing as
// soon as possible, but if you use `rawAsap` directly, you are responsible to
// either ensure that no exceptions are thrown from your task, or to manually
// call `rawAsap.requestFlush` if an exception is thrown.
module.exports = rawAsap;
function rawAsap(task) {
    if (!queue.length) {
        requestFlush();
        flushing = true;
    }
    // Equivalent to push, but avoids a function call.
    queue[queue.length] = task;
}

var queue = [];
// Once a flush has been requested, no further calls to `requestFlush` are
// necessary until the next `flush` completes.
var flushing = false;
// `requestFlush` is an implementation-specific method that attempts to kick
// off a `flush` event as quickly as possible. `flush` will attempt to exhaust
// the event queue before yielding to the browser's own event loop.
var requestFlush;
// The position of the next task to execute in the task queue. This is
// preserved between calls to `flush` so that it can be resumed if
// a task throws an exception.
var index = 0;
// If a task schedules additional tasks recursively, the task queue can grow
// unbounded. To prevent memory exhaustion, the task queue will periodically
// truncate already-completed tasks.
var capacity = 1024;

// The flush function processes all tasks that have been scheduled with
// `rawAsap` unless and until one of those tasks throws an exception.
// If a task throws an exception, `flush` ensures that its state will remain
// consistent and will resume where it left off when called again.
// However, `flush` does not make any arrangements to be called again if an
// exception is thrown.
function flush() {
    while (index < queue.length) {
        var currentIndex = index;
        // Advance the index before calling the task. This ensures that we will
        // begin flushing on the next task the task throws an error.
        index = index + 1;
        queue[currentIndex].call();
        // Prevent leaking memory for long chains of recursive calls to `asap`.
        // If we call `asap` within tasks scheduled by `asap`, the queue will
        // grow, but to avoid an O(n) walk for every task we execute, we don't
        // shift tasks off the queue after they have been executed.
        // Instead, we periodically shift 1024 tasks off the queue.
        if (index > capacity) {
            // Manually shift all values starting at the index back to the
            // beginning of the queue.
            for (var scan = 0, newLength = queue.length - index; scan < newLength; scan++) {
                queue[scan] = queue[scan + index];
            }
            queue.length -= index;
            index = 0;
        }
    }
    queue.length = 0;
    index = 0;
    flushing = false;
}

// `requestFlush` is implemented using a strategy based on data collected from
// every available SauceLabs Selenium web driver worker at time of writing.
// https://docs.google.com/spreadsheets/d/1mG-5UYGup5qxGdEMWkhP6BWCz053NUb2E1QoUTU16uA/edit#gid=783724593

// Safari 6 and 6.1 for desktop, iPad, and iPhone are the only browsers that
// have WebKitMutationObserver but not un-prefixed MutationObserver.
// Must use `global` or `self` instead of `window` to work in both frames and web
// workers. `global` is a provision of Browserify, Mr, Mrs, or Mop.

/* globals self */
var scope = typeof global !== "undefined" ? global : self;
var BrowserMutationObserver = scope.MutationObserver || scope.WebKitMutationObserver;

// MutationObservers are desirable because they have high priority and work
// reliably everywhere they are implemented.
// They are implemented in all modern browsers.
//
// - Android 4-4.3
// - Chrome 26-34
// - Firefox 14-29
// - Internet Explorer 11
// - iPad Safari 6-7.1
// - iPhone Safari 7-7.1
// - Safari 6-7
if (typeof BrowserMutationObserver === "function") {
    requestFlush = makeRequestCallFromMutationObserver(flush);

// MessageChannels are desirable because they give direct access to the HTML
// task queue, are implemented in Internet Explorer 10, Safari 5.0-1, and Opera
// 11-12, and in web workers in many engines.
// Although message channels yield to any queued rendering and IO tasks, they
// would be better than imposing the 4ms delay of timers.
// However, they do not work reliably in Internet Explorer or Safari.

// Internet Explorer 10 is the only browser that has setImmediate but does
// not have MutationObservers.
// Although setImmediate yields to the browser's renderer, it would be
// preferrable to falling back to setTimeout since it does not have
// the minimum 4ms penalty.
// Unfortunately there appears to be a bug in Internet Explorer 10 Mobile (and
// Desktop to a lesser extent) that renders both setImmediate and
// MessageChannel useless for the purposes of ASAP.
// https://github.com/kriskowal/q/issues/396

// Timers are implemented universally.
// We fall back to timers in workers in most engines, and in foreground
// contexts in the following browsers.
// However, note that even this simple case requires nuances to operate in a
// broad spectrum of browsers.
//
// - Firefox 3-13
// - Internet Explorer 6-9
// - iPad Safari 4.3
// - Lynx 2.8.7
} else {
    requestFlush = makeRequestCallFromTimer(flush);
}

// `requestFlush` requests that the high priority event queue be flushed as
// soon as possible.
// This is useful to prevent an error thrown in a task from stalling the event
// queue if the exception handled by Node.jsâ€™s
// `process.on("uncaughtException")` or by a domain.
rawAsap.requestFlush = requestFlush;

// To request a high priority event, we induce a mutation observer by toggling
// the text of a text node between "1" and "-1".
function makeRequestCallFromMutationObserver(callback) {
    var toggle = 1;
    var observer = new BrowserMutationObserver(callback);
    var node = document.createTextNode("");
    observer.observe(node, {characterData: true});
    return function requestCall() {
        toggle = -toggle;
        node.data = toggle;
    };
}

// The message channel technique was discovered by Malte Ubl and was the
// original foundation for this library.
// http://www.nonblocking.io/2011/06/windownexttick.html

// Safari 6.0.5 (at least) intermittently fails to create message ports on a
// page's first load. Thankfully, this version of Safari supports
// MutationObservers, so we don't need to fall back in that case.

// function makeRequestCallFromMessageChannel(callback) {
//     var channel = new MessageChannel();
//     channel.port1.onmessage = callback;
//     return function requestCall() {
//         channel.port2.postMessage(0);
//     };
// }

// For reasons explained above, we are also unable to use `setImmediate`
// under any circumstances.
// Even if we were, there is another bug in Internet Explorer 10.
// It is not sufficient to assign `setImmediate` to `requestFlush` because
// `setImmediate` must be called *by name* and therefore must be wrapped in a
// closure.
// Never forget.

// function makeRequestCallFromSetImmediate(callback) {
//     return function requestCall() {
//         setImmediate(callback);
//     };
// }

// Safari 6.0 has a problem where timers will get lost while the user is
// scrolling. This problem does not impact ASAP because Safari 6.0 supports
// mutation observers, so that implementation is used instead.
// However, if we ever elect to use timers in Safari, the prevalent work-around
// is to add a scroll event listener that calls for a flush.

// `setTimeout` does not call the passed callback if the delay is less than
// approximately 7 in web workers in Firefox 8 through 18, and sometimes not
// even then.

function makeRequestCallFromTimer(callback) {
    return function requestCall() {
        // We dispatch a timeout with a specified delay of 0 for engines that
        // can reliably accommodate that request. This will usually be snapped
        // to a 4 milisecond delay, but once we're flushing, there's no delay
        // between events.
        var timeoutHandle = setTimeout(handleTimer, 0);
        // However, since this timer gets frequently dropped in Firefox
        // workers, we enlist an interval handle that will try to fire
        // an event 20 times per second until it succeeds.
        var intervalHandle = setInterval(handleTimer, 50);

        function handleTimer() {
            // Whichever timer succeeds will cancel both timers and
            // execute the callback.
            clearTimeout(timeoutHandle);
            clearInterval(intervalHandle);
            callback();
        }
    };
}

// This is for `asap.js` only.
// Its name will be periodically randomized to break any code that depends on
// its existence.
rawAsap.makeRequestCallFromTimer = makeRequestCallFromTimer;

// ASAP was originally a nextTick shim included in Q. This was factored out
// into this ASAP package. It was later adapted to RSVP which made further
// amendments. These decisions, particularly to marginalize MessageChannel and
// to capture the MutationObserver implementation in a closure, were integrated
// back into ASAP proper.
// https://github.com/tildeio/rsvp.js/blob/cddf7232546a9cf858524b75cde6f9edf72620a7/lib/rsvp/asap.js

/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(10)))

/***/ }),
/* 10 */
/***/ (function(module, exports) {

var g;

// This works in non-strict mode
g = (function() {
	return this;
})();

try {
	// This works if eval is allowed (see CSP)
	g = g || Function("return this")() || (1,eval)("this");
} catch(e) {
	// This works if the window reference is available
	if(typeof window === "object")
		g = window;
}

// g can still be undefined, but nothing to do about it...
// We return undefined, instead of nothing here, so it's
// easier to handle this case. if(!global) { ...}

module.exports = g;


/***/ }),
/* 11 */
/***/ (function(module, exports, __webpack_require__) {

var __WEBPACK_AMD_DEFINE_ARRAY__, __WEBPACK_AMD_DEFINE_RESULT__;// MIT license (by Elan Shanker).
(function(globals) {
  'use strict';

  var executeSync = function(){
    var args = Array.prototype.slice.call(arguments);
    if (typeof args[0] === 'function'){
      args[0].apply(null, args.splice(1));
    }
  };

  var executeAsync = function(fn){
    if (typeof setImmediate === 'function') {
      setImmediate(fn);
    } else if (typeof process !== 'undefined' && process.nextTick) {
      process.nextTick(fn);
    } else {
      setTimeout(fn, 0);
    }
  };

  var makeIterator = function (tasks) {
    var makeCallback = function (index) {
      var fn = function () {
        if (tasks.length) {
          tasks[index].apply(null, arguments);
        }
        return fn.next();
      };
      fn.next = function () {
        return (index < tasks.length - 1) ? makeCallback(index + 1): null;
      };
      return fn;
    };
    return makeCallback(0);
  };
  
  var _isArray = Array.isArray || function(maybeArray){
    return Object.prototype.toString.call(maybeArray) === '[object Array]';
  };

  var waterfall = function (tasks, callback, forceAsync) {
    var nextTick = forceAsync ? executeAsync : executeSync;
    callback = callback || function () {};
    if (!_isArray(tasks)) {
      var err = new Error('First argument to waterfall must be an array of functions');
      return callback(err);
    }
    if (!tasks.length) {
      return callback();
    }
    var wrapIterator = function (iterator) {
      return function (err) {
        if (err) {
          callback.apply(null, arguments);
          callback = function () {};
        } else {
          var args = Array.prototype.slice.call(arguments, 1);
          var next = iterator.next();
          if (next) {
            args.push(wrapIterator(next));
          } else {
            args.push(callback);
          }
          nextTick(function () {
            iterator.apply(null, args);
          });
        }
      };
    };
    wrapIterator(makeIterator(tasks))();
  };

  if (true) {
    !(__WEBPACK_AMD_DEFINE_ARRAY__ = [], __WEBPACK_AMD_DEFINE_RESULT__ = (function () {
      return waterfall;
    }).apply(exports, __WEBPACK_AMD_DEFINE_ARRAY__),
				__WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__)); // RequireJS
  } else if (typeof module !== 'undefined' && module.exports) {
    module.exports = waterfall; // CommonJS
  } else {
    globals.waterfall = waterfall; // <script>
  }
})(this);


/***/ }),
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var lib = __webpack_require__(1);
var r = __webpack_require__(2);
var exports = module.exports = {};
function normalize(value, defaultValue) {
  if (value === null || value === undefined || value === false) {
    return defaultValue;
  }
  return value;
}
exports.abs = Math.abs;
function isNaN(num) {
  return num !== num; // eslint-disable-line no-self-compare
}

function batch(arr, linecount, fillWith) {
  var i;
  var res = [];
  var tmp = [];
  for (i = 0; i < arr.length; i++) {
    if (i % linecount === 0 && tmp.length) {
      res.push(tmp);
      tmp = [];
    }
    tmp.push(arr[i]);
  }
  if (tmp.length) {
    if (fillWith) {
      for (i = tmp.length; i < linecount; i++) {
        tmp.push(fillWith);
      }
    }
    res.push(tmp);
  }
  return res;
}
exports.batch = batch;
function capitalize(str) {
  str = normalize(str, '');
  var ret = str.toLowerCase();
  return r.copySafeness(str, ret.charAt(0).toUpperCase() + ret.slice(1));
}
exports.capitalize = capitalize;
function center(str, width) {
  str = normalize(str, '');
  width = width || 80;
  if (str.length >= width) {
    return str;
  }
  var spaces = width - str.length;
  var pre = lib.repeat(' ', spaces / 2 - spaces % 2);
  var post = lib.repeat(' ', spaces / 2);
  return r.copySafeness(str, pre + str + post);
}
exports.center = center;
function default_(val, def, bool) {
  if (bool) {
    return val || def;
  } else {
    return val !== undefined ? val : def;
  }
}

// TODO: it is confusing to export something called 'default'
exports['default'] = default_; // eslint-disable-line dot-notation

function dictsort(val, caseSensitive, by) {
  if (!lib.isObject(val)) {
    throw new lib.TemplateError('dictsort filter: val must be an object');
  }
  var array = [];
  // deliberately include properties from the object's prototype
  for (var k in val) {
    // eslint-disable-line guard-for-in, no-restricted-syntax
    array.push([k, val[k]]);
  }
  var si;
  if (by === undefined || by === 'key') {
    si = 0;
  } else if (by === 'value') {
    si = 1;
  } else {
    throw new lib.TemplateError('dictsort filter: You can only sort by either key or value');
  }
  array.sort(function (t1, t2) {
    var a = t1[si];
    var b = t2[si];
    if (!caseSensitive) {
      if (lib.isString(a)) {
        a = a.toUpperCase();
      }
      if (lib.isString(b)) {
        b = b.toUpperCase();
      }
    }
    return a > b ? 1 : a === b ? 0 : -1; // eslint-disable-line no-nested-ternary
  });

  return array;
}
exports.dictsort = dictsort;
function dump(obj, spaces) {
  return JSON.stringify(obj, null, spaces);
}
exports.dump = dump;
function escape(str) {
  if (str instanceof r.SafeString) {
    return str;
  }
  str = str === null || str === undefined ? '' : str;
  return r.markSafe(lib.escape(str.toString()));
}
exports.escape = escape;
function safe(str) {
  if (str instanceof r.SafeString) {
    return str;
  }
  str = str === null || str === undefined ? '' : str;
  return r.markSafe(str.toString());
}
exports.safe = safe;
function first(arr) {
  return arr[0];
}
exports.first = first;
function forceescape(str) {
  str = str === null || str === undefined ? '' : str;
  return r.markSafe(lib.escape(str.toString()));
}
exports.forceescape = forceescape;
function groupby(arr, attr) {
  return lib.groupBy(arr, attr, this.env.opts.throwOnUndefined);
}
exports.groupby = groupby;
function indent(str, width, indentfirst) {
  str = normalize(str, '');
  if (str === '') {
    return '';
  }
  width = width || 4;
  // let res = '';
  var lines = str.split('\n');
  var sp = lib.repeat(' ', width);
  var res = lines.map(function (l, i) {
    return i === 0 && !indentfirst ? l : "" + sp + l;
  }).join('\n');
  return r.copySafeness(str, res);
}
exports.indent = indent;
function join(arr, del, attr) {
  del = del || '';
  if (attr) {
    arr = lib.map(arr, function (v) {
      return v[attr];
    });
  }
  return arr.join(del);
}
exports.join = join;
function last(arr) {
  return arr[arr.length - 1];
}
exports.last = last;
function lengthFilter(val) {
  var value = normalize(val, '');
  if (value !== undefined) {
    if (typeof Map === 'function' && value instanceof Map || typeof Set === 'function' && value instanceof Set) {
      // ECMAScript 2015 Maps and Sets
      return value.size;
    }
    if (lib.isObject(value) && !(value instanceof r.SafeString)) {
      // Objects (besides SafeStrings), non-primative Arrays
      return lib.keys(value).length;
    }
    return value.length;
  }
  return 0;
}
exports.length = lengthFilter;
function list(val) {
  if (lib.isString(val)) {
    return val.split('');
  } else if (lib.isObject(val)) {
    return lib._entries(val || {}).map(function (_ref) {
      var key = _ref[0],
        value = _ref[1];
      return {
        key: key,
        value: value
      };
    });
  } else if (lib.isArray(val)) {
    return val;
  } else {
    throw new lib.TemplateError('list filter: type not iterable');
  }
}
exports.list = list;
function lower(str) {
  str = normalize(str, '');
  return str.toLowerCase();
}
exports.lower = lower;
function nl2br(str) {
  if (str === null || str === undefined) {
    return '';
  }
  return r.copySafeness(str, str.replace(/\r\n|\n/g, '<br />\n'));
}
exports.nl2br = nl2br;
function random(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}
exports.random = random;

/**
 * Construct select or reject filter
 *
 * @param {boolean} expectedTestResult
 * @returns {function(array, string, *): array}
 */
function getSelectOrReject(expectedTestResult) {
  function filter(arr, testName, secondArg) {
    if (testName === void 0) {
      testName = 'truthy';
    }
    var context = this;
    var test = context.env.getTest(testName);
    return lib.toArray(arr).filter(function examineTestResult(item) {
      return test.call(context, item, secondArg) === expectedTestResult;
    });
  }
  return filter;
}
exports.reject = getSelectOrReject(false);
function rejectattr(arr, attr) {
  return arr.filter(function (item) {
    return !item[attr];
  });
}
exports.rejectattr = rejectattr;
exports.select = getSelectOrReject(true);
function selectattr(arr, attr) {
  return arr.filter(function (item) {
    return !!item[attr];
  });
}
exports.selectattr = selectattr;
function replace(str, old, new_, maxCount) {
  var originalStr = str;
  if (old instanceof RegExp) {
    return str.replace(old, new_);
  }
  if (typeof maxCount === 'undefined') {
    maxCount = -1;
  }
  var res = ''; // Output

  // Cast Numbers in the search term to string
  if (typeof old === 'number') {
    old = '' + old;
  } else if (typeof old !== 'string') {
    // If it is something other than number or string,
    // return the original string
    return str;
  }

  // Cast numbers in the replacement to string
  if (typeof str === 'number') {
    str = '' + str;
  }

  // If by now, we don't have a string, throw it back
  if (typeof str !== 'string' && !(str instanceof r.SafeString)) {
    return str;
  }

  // ShortCircuits
  if (old === '') {
    // Mimic the python behaviour: empty string is replaced
    // by replacement e.g. "abc"|replace("", ".") -> .a.b.c.
    res = new_ + str.split('').join(new_) + new_;
    return r.copySafeness(str, res);
  }
  var nextIndex = str.indexOf(old);
  // if # of replacements to perform is 0, or the string to does
  // not contain the old value, return the string
  if (maxCount === 0 || nextIndex === -1) {
    return str;
  }
  var pos = 0;
  var count = 0; // # of replacements made

  while (nextIndex > -1 && (maxCount === -1 || count < maxCount)) {
    // Grab the next chunk of src string and add it with the
    // replacement, to the result
    res += str.substring(pos, nextIndex) + new_;
    // Increment our pointer in the src string
    pos = nextIndex + old.length;
    count++;
    // See if there are any more replacements to be made
    nextIndex = str.indexOf(old, pos);
  }

  // We've either reached the end, or done the max # of
  // replacements, tack on any remaining string
  if (pos < str.length) {
    res += str.substring(pos);
  }
  return r.copySafeness(originalStr, res);
}
exports.replace = replace;
function reverse(val) {
  var arr;
  if (lib.isString(val)) {
    arr = list(val);
  } else {
    // Copy it
    arr = lib.map(val, function (v) {
      return v;
    });
  }
  arr.reverse();
  if (lib.isString(val)) {
    return r.copySafeness(val, arr.join(''));
  }
  return arr;
}
exports.reverse = reverse;
function round(val, precision, method) {
  precision = precision || 0;
  var factor = Math.pow(10, precision);
  var rounder;
  if (method === 'ceil') {
    rounder = Math.ceil;
  } else if (method === 'floor') {
    rounder = Math.floor;
  } else {
    rounder = Math.round;
  }
  return rounder(val * factor) / factor;
}
exports.round = round;
function slice(arr, slices, fillWith) {
  var sliceLength = Math.floor(arr.length / slices);
  var extra = arr.length % slices;
  var res = [];
  var offset = 0;
  for (var i = 0; i < slices; i++) {
    var start = offset + i * sliceLength;
    if (i < extra) {
      offset++;
    }
    var end = offset + (i + 1) * sliceLength;
    var currSlice = arr.slice(start, end);
    if (fillWith && i >= extra) {
      currSlice.push(fillWith);
    }
    res.push(currSlice);
  }
  return res;
}
exports.slice = slice;
function sum(arr, attr, start) {
  if (start === void 0) {
    start = 0;
  }
  if (attr) {
    arr = lib.map(arr, function (v) {
      return v[attr];
    });
  }
  return start + arr.reduce(function (a, b) {
    return a + b;
  }, 0);
}
exports.sum = sum;
exports.sort = r.makeMacro(['value', 'reverse', 'case_sensitive', 'attribute'], [], function sortFilter(arr, reversed, caseSens, attr) {
  var _this = this;
  // Copy it
  var array = lib.map(arr, function (v) {
    return v;
  });
  var getAttribute = lib.getAttrGetter(attr);
  array.sort(function (a, b) {
    var x = attr ? getAttribute(a) : a;
    var y = attr ? getAttribute(b) : b;
    if (_this.env.opts.throwOnUndefined && attr && (x === undefined || y === undefined)) {
      throw new TypeError("sort: attribute \"" + attr + "\" resolved to undefined");
    }
    if (!caseSens && lib.isString(x) && lib.isString(y)) {
      x = x.toLowerCase();
      y = y.toLowerCase();
    }
    if (x < y) {
      return reversed ? 1 : -1;
    } else if (x > y) {
      return reversed ? -1 : 1;
    } else {
      return 0;
    }
  });
  return array;
});
function string(obj) {
  return r.copySafeness(obj, obj);
}
exports.string = string;
function striptags(input, preserveLinebreaks) {
  input = normalize(input, '');
  var tags = /<\/?([a-z][a-z0-9]*)\b[^>]*>|<!--[\s\S]*?-->/gi;
  var trimmedInput = trim(input.replace(tags, ''));
  var res = '';
  if (preserveLinebreaks) {
    res = trimmedInput.replace(/^ +| +$/gm, '') // remove leading and trailing spaces
    .replace(/ +/g, ' ') // squash adjacent spaces
    .replace(/(\r\n)/g, '\n') // normalize linebreaks (CRLF -> LF)
    .replace(/\n\n\n+/g, '\n\n'); // squash abnormal adjacent linebreaks
  } else {
    res = trimmedInput.replace(/\s+/gi, ' ');
  }
  return r.copySafeness(input, res);
}
exports.striptags = striptags;
function title(str) {
  str = normalize(str, '');
  var words = str.split(' ').map(function (word) {
    return capitalize(word);
  });
  return r.copySafeness(str, words.join(' '));
}
exports.title = title;
function trim(str) {
  return r.copySafeness(str, str.replace(/^\s*|\s*$/g, ''));
}
exports.trim = trim;
function truncate(input, length, killwords, end) {
  var orig = input;
  input = normalize(input, '');
  length = length || 255;
  if (input.length <= length) {
    return input;
  }
  if (killwords) {
    input = input.substring(0, length);
  } else {
    var idx = input.lastIndexOf(' ', length);
    if (idx === -1) {
      idx = length;
    }
    input = input.substring(0, idx);
  }
  input += end !== undefined && end !== null ? end : '...';
  return r.copySafeness(orig, input);
}
exports.truncate = truncate;
function upper(str) {
  str = normalize(str, '');
  return str.toUpperCase();
}
exports.upper = upper;
function urlencode(obj) {
  var enc = encodeURIComponent;
  if (lib.isString(obj)) {
    return enc(obj);
  } else {
    var keyvals = lib.isArray(obj) ? obj : lib._entries(obj);
    return keyvals.map(function (_ref2) {
      var k = _ref2[0],
        v = _ref2[1];
      return enc(k) + "=" + enc(v);
    }).join('&');
  }
}
exports.urlencode = urlencode;

// For the jinja regexp, see
// https://github.com/mitsuhiko/jinja2/blob/f15b814dcba6aa12bc74d1f7d0c881d55f7126be/jinja2/utils.py#L20-L23
var puncRe = /^(?:\(|<|&lt;)?(.*?)(?:\.|,|\)|\n|&gt;)?$/;
// from http://blog.gerv.net/2011/05/html5_email_address_regexp/
var emailRe = /^[\w.!#$%&'*+\-\/=?\^`{|}~]+@[a-z\d\-]+(\.[a-z\d\-]+)+$/i;
var httpHttpsRe = /^https?:\/\/.*$/;
var wwwRe = /^www\./;
var tldRe = /\.(?:org|net|com)(?:\:|\/|$)/;
function urlize(str, length, nofollow) {
  if (isNaN(length)) {
    length = Infinity;
  }
  var noFollowAttr = nofollow === true ? ' rel="nofollow"' : '';
  var words = str.split(/(\s+)/).filter(function (word) {
    // If the word has no length, bail. This can happen for str with
    // trailing whitespace.
    return word && word.length;
  }).map(function (word) {
    var matches = word.match(puncRe);
    var possibleUrl = matches ? matches[1] : word;
    var shortUrl = possibleUrl.substr(0, length);

    // url that starts with http or https
    if (httpHttpsRe.test(possibleUrl)) {
      return "<a href=\"" + possibleUrl + "\"" + noFollowAttr + ">" + shortUrl + "</a>";
    }

    // url that starts with www.
    if (wwwRe.test(possibleUrl)) {
      return "<a href=\"http://" + possibleUrl + "\"" + noFollowAttr + ">" + shortUrl + "</a>";
    }

    // an email address of the form username@domain.tld
    if (emailRe.test(possibleUrl)) {
      return "<a href=\"mailto:" + possibleUrl + "\">" + possibleUrl + "</a>";
    }

    // url that ends in .com, .org or .net that is not an email address
    if (tldRe.test(possibleUrl)) {
      return "<a href=\"http://" + possibleUrl + "\"" + noFollowAttr + ">" + shortUrl + "</a>";
    }
    return word;
  });
  return words.join('');
}
exports.urlize = urlize;
function wordcount(str) {
  str = normalize(str, '');
  var words = str ? str.match(/\w+/g) : null;
  return words ? words.length : null;
}
exports.wordcount = wordcount;
function float(val, def) {
  var res = parseFloat(val);
  return isNaN(res) ? def : res;
}
exports.float = float;
var intFilter = r.makeMacro(['value', 'default', 'base'], [], function doInt(value, defaultValue, base) {
  if (base === void 0) {
    base = 10;
  }
  var res = parseInt(value, base);
  return isNaN(res) ? defaultValue : res;
});
exports.int = intFilter;

// Aliases
exports.d = exports.default;
exports.e = exports.escape;

/***/ }),
/* 13 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.



var R = typeof Reflect === 'object' ? Reflect : null
var ReflectApply = R && typeof R.apply === 'function'
  ? R.apply
  : function ReflectApply(target, receiver, args) {
    return Function.prototype.apply.call(target, receiver, args);
  }

var ReflectOwnKeys
if (R && typeof R.ownKeys === 'function') {
  ReflectOwnKeys = R.ownKeys
} else if (Object.getOwnPropertySymbols) {
  ReflectOwnKeys = function ReflectOwnKeys(target) {
    return Object.getOwnPropertyNames(target)
      .concat(Object.getOwnPropertySymbols(target));
  };
} else {
  ReflectOwnKeys = function ReflectOwnKeys(target) {
    return Object.getOwnPropertyNames(target);
  };
}

function ProcessEmitWarning(warning) {
  if (console && console.warn) console.warn(warning);
}

var NumberIsNaN = Number.isNaN || function NumberIsNaN(value) {
  return value !== value;
}

function EventEmitter() {
  EventEmitter.init.call(this);
}
module.exports = EventEmitter;
module.exports.once = once;

// Backwards-compat with node 0.10.x
EventEmitter.EventEmitter = EventEmitter;

EventEmitter.prototype._events = undefined;
EventEmitter.prototype._eventsCount = 0;
EventEmitter.prototype._maxListeners = undefined;

// By default EventEmitters will print a warning if more than 10 listeners are
// added to it. This is a useful default which helps finding memory leaks.
var defaultMaxListeners = 10;

function checkListener(listener) {
  if (typeof listener !== 'function') {
    throw new TypeError('The "listener" argument must be of type Function. Received type ' + typeof listener);
  }
}

Object.defineProperty(EventEmitter, 'defaultMaxListeners', {
  enumerable: true,
  get: function() {
    return defaultMaxListeners;
  },
  set: function(arg) {
    if (typeof arg !== 'number' || arg < 0 || NumberIsNaN(arg)) {
      throw new RangeError('The value of "defaultMaxListeners" is out of range. It must be a non-negative number. Received ' + arg + '.');
    }
    defaultMaxListeners = arg;
  }
});

EventEmitter.init = function() {

  if (this._events === undefined ||
      this._events === Object.getPrototypeOf(this)._events) {
    this._events = Object.create(null);
    this._eventsCount = 0;
  }

  this._maxListeners = this._maxListeners || undefined;
};

// Obviously not all Emitters should be limited to 10. This function allows
// that to be increased. Set to zero for unlimited.
EventEmitter.prototype.setMaxListeners = function setMaxListeners(n) {
  if (typeof n !== 'number' || n < 0 || NumberIsNaN(n)) {
    throw new RangeError('The value of "n" is out of range. It must be a non-negative number. Received ' + n + '.');
  }
  this._maxListeners = n;
  return this;
};

function _getMaxListeners(that) {
  if (that._maxListeners === undefined)
    return EventEmitter.defaultMaxListeners;
  return that._maxListeners;
}

EventEmitter.prototype.getMaxListeners = function getMaxListeners() {
  return _getMaxListeners(this);
};

EventEmitter.prototype.emit = function emit(type) {
  var args = [];
  for (var i = 1; i < arguments.length; i++) args.push(arguments[i]);
  var doError = (type === 'error');

  var events = this._events;
  if (events !== undefined)
    doError = (doError && events.error === undefined);
  else if (!doError)
    return false;

  // If there is no 'error' event listener then throw.
  if (doError) {
    var er;
    if (args.length > 0)
      er = args[0];
    if (er instanceof Error) {
      // Note: The comments on the `throw` lines are intentional, they show
      // up in Node's output if this results in an unhandled exception.
      throw er; // Unhandled 'error' event
    }
    // At least give some kind of context to the user
    var err = new Error('Unhandled error.' + (er ? ' (' + er.message + ')' : ''));
    err.context = er;
    throw err; // Unhandled 'error' event
  }

  var handler = events[type];

  if (handler === undefined)
    return false;

  if (typeof handler === 'function') {
    ReflectApply(handler, this, args);
  } else {
    var len = handler.length;
    var listeners = arrayClone(handler, len);
    for (var i = 0; i < len; ++i)
      ReflectApply(listeners[i], this, args);
  }

  return true;
};

function _addListener(target, type, listener, prepend) {
  var m;
  var events;
  var existing;

  checkListener(listener);

  events = target._events;
  if (events === undefined) {
    events = target._events = Object.create(null);
    target._eventsCount = 0;
  } else {
    // To avoid recursion in the case that type === "newListener"! Before
    // adding it to the listeners, first emit "newListener".
    if (events.newListener !== undefined) {
      target.emit('newListener', type,
                  listener.listener ? listener.listener : listener);

      // Re-assign `events` because a newListener handler could have caused the
      // this._events to be assigned to a new object
      events = target._events;
    }
    existing = events[type];
  }

  if (existing === undefined) {
    // Optimize the case of one listener. Don't need the extra array object.
    existing = events[type] = listener;
    ++target._eventsCount;
  } else {
    if (typeof existing === 'function') {
      // Adding the second element, need to change to array.
      existing = events[type] =
        prepend ? [listener, existing] : [existing, listener];
      // If we've already got an array, just append.
    } else if (prepend) {
      existing.unshift(listener);
    } else {
      existing.push(listener);
    }

    // Check for listener leak
    m = _getMaxListeners(target);
    if (m > 0 && existing.length > m && !existing.warned) {
      existing.warned = true;
      // No error code for this since it is a Warning
      // eslint-disable-next-line no-restricted-syntax
      var w = new Error('Possible EventEmitter memory leak detected. ' +
                          existing.length + ' ' + String(type) + ' listeners ' +
                          'added. Use emitter.setMaxListeners() to ' +
                          'increase limit');
      w.name = 'MaxListenersExceededWarning';
      w.emitter = target;
      w.type = type;
      w.count = existing.length;
      ProcessEmitWarning(w);
    }
  }

  return target;
}

EventEmitter.prototype.addListener = function addListener(type, listener) {
  return _addListener(this, type, listener, false);
};

EventEmitter.prototype.on = EventEmitter.prototype.addListener;

EventEmitter.prototype.prependListener =
    function prependListener(type, listener) {
      return _addListener(this, type, listener, true);
    };

function onceWrapper() {
  if (!this.fired) {
    this.target.removeListener(this.type, this.wrapFn);
    this.fired = true;
    if (arguments.length === 0)
      return this.listener.call(this.target);
    return this.listener.apply(this.target, arguments);
  }
}

function _onceWrap(target, type, listener) {
  var state = { fired: false, wrapFn: undefined, target: target, type: type, listener: listener };
  var wrapped = onceWrapper.bind(state);
  wrapped.listener = listener;
  state.wrapFn = wrapped;
  return wrapped;
}

EventEmitter.prototype.once = function once(type, listener) {
  checkListener(listener);
  this.on(type, _onceWrap(this, type, listener));
  return this;
};

EventEmitter.prototype.prependOnceListener =
    function prependOnceListener(type, listener) {
      checkListener(listener);
      this.prependListener(type, _onceWrap(this, type, listener));
      return this;
    };

// Emits a 'removeListener' event if and only if the listener was removed.
EventEmitter.prototype.removeListener =
    function removeListener(type, listener) {
      var list, events, position, i, originalListener;

      checkListener(listener);

      events = this._events;
      if (events === undefined)
        return this;

      list = events[type];
      if (list === undefined)
        return this;

      if (list === listener || list.listener === listener) {
        if (--this._eventsCount === 0)
          this._events = Object.create(null);
        else {
          delete events[type];
          if (events.removeListener)
            this.emit('removeListener', type, list.listener || listener);
        }
      } else if (typeof list !== 'function') {
        position = -1;

        for (i = list.length - 1; i >= 0; i--) {
          if (list[i] === listener || list[i].listener === listener) {
            originalListener = list[i].listener;
            position = i;
            break;
          }
        }

        if (position < 0)
          return this;

        if (position === 0)
          list.shift();
        else {
          spliceOne(list, position);
        }

        if (list.length === 1)
          events[type] = list[0];

        if (events.removeListener !== undefined)
          this.emit('removeListener', type, originalListener || listener);
      }

      return this;
    };

EventEmitter.prototype.off = EventEmitter.prototype.removeListener;

EventEmitter.prototype.removeAllListeners =
    function removeAllListeners(type) {
      var listeners, events, i;

      events = this._events;
      if (events === undefined)
        return this;

      // not listening for removeListener, no need to emit
      if (events.removeListener === undefined) {
        if (arguments.length === 0) {
          this._events = Object.create(null);
          this._eventsCount = 0;
        } else if (events[type] !== undefined) {
          if (--this._eventsCount === 0)
            this._events = Object.create(null);
          else
            delete events[type];
        }
        return this;
      }

      // emit removeListener for all listeners on all events
      if (arguments.length === 0) {
        var keys = Object.keys(events);
        var key;
        for (i = 0; i < keys.length; ++i) {
          key = keys[i];
          if (key === 'removeListener') continue;
          this.removeAllListeners(key);
        }
        this.removeAllListeners('removeListener');
        this._events = Object.create(null);
        this._eventsCount = 0;
        return this;
      }

      listeners = events[type];

      if (typeof listeners === 'function') {
        this.removeListener(type, listeners);
      } else if (listeners !== undefined) {
        // LIFO order
        for (i = listeners.length - 1; i >= 0; i--) {
          this.removeListener(type, listeners[i]);
        }
      }

      return this;
    };

function _listeners(target, type, unwrap) {
  var events = target._events;

  if (events === undefined)
    return [];

  var evlistener = events[type];
  if (evlistener === undefined)
    return [];

  if (typeof evlistener === 'function')
    return unwrap ? [evlistener.listener || evlistener] : [evlistener];

  return unwrap ?
    unwrapListeners(evlistener) : arrayClone(evlistener, evlistener.length);
}

EventEmitter.prototype.listeners = function listeners(type) {
  return _listeners(this, type, true);
};

EventEmitter.prototype.rawListeners = function rawListeners(type) {
  return _listeners(this, type, false);
};

EventEmitter.listenerCount = function(emitter, type) {
  if (typeof emitter.listenerCount === 'function') {
    return emitter.listenerCount(type);
  } else {
    return listenerCount.call(emitter, type);
  }
};

EventEmitter.prototype.listenerCount = listenerCount;
function listenerCount(type) {
  var events = this._events;

  if (events !== undefined) {
    var evlistener = events[type];

    if (typeof evlistener === 'function') {
      return 1;
    } else if (evlistener !== undefined) {
      return evlistener.length;
    }
  }

  return 0;
}

EventEmitter.prototype.eventNames = function eventNames() {
  return this._eventsCount > 0 ? ReflectOwnKeys(this._events) : [];
};

function arrayClone(arr, n) {
  var copy = new Array(n);
  for (var i = 0; i < n; ++i)
    copy[i] = arr[i];
  return copy;
}

function spliceOne(list, index) {
  for (; index + 1 < list.length; index++)
    list[index] = list[index + 1];
  list.pop();
}

function unwrapListeners(arr) {
  var ret = new Array(arr.length);
  for (var i = 0; i < ret.length; ++i) {
    ret[i] = arr[i].listener || arr[i];
  }
  return ret;
}

function once(emitter, name) {
  return new Promise(function (resolve, reject) {
    function errorListener(err) {
      emitter.removeListener(name, resolver);
      reject(err);
    }

    function resolver() {
      if (typeof emitter.removeListener === 'function') {
        emitter.removeListener('error', errorListener);
      }
      resolve([].slice.call(arguments));
    };

    eventTargetAgnosticAddListener(emitter, name, resolver, { once: true });
    if (name !== 'error') {
      addErrorHandlerIfEventEmitter(emitter, errorListener, { once: true });
    }
  });
}

function addErrorHandlerIfEventEmitter(emitter, handler, flags) {
  if (typeof emitter.on === 'function') {
    eventTargetAgnosticAddListener(emitter, 'error', handler, flags);
  }
}

function eventTargetAgnosticAddListener(emitter, name, listener, flags) {
  if (typeof emitter.on === 'function') {
    if (flags.once) {
      emitter.once(name, listener);
    } else {
      emitter.on(name, listener);
    }
  } else if (typeof emitter.addEventListener === 'function') {
    // EventTarget does not have `error` event semantics like Node
    // EventEmitters, we do not listen for `error` events here.
    emitter.addEventListener(name, function wrapListener(arg) {
      // IE does not have builtin `{ once: true }` support so we
      // have to do it manually.
      if (flags.once) {
        emitter.removeEventListener(name, wrapListener);
      }
      listener(arg);
    });
  } else {
    throw new TypeError('The "emitter" argument must be of type EventEmitter. Received type ' + typeof emitter);
  }
}


/***/ }),
/* 14 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var SafeString = __webpack_require__(2).SafeString;

/**
 * Returns `true` if the object is a function, otherwise `false`.
 * @param { any } value
 * @returns { boolean }
 */
function callable(value) {
  return typeof value === 'function';
}
exports.callable = callable;

/**
 * Returns `true` if the object is strictly not `undefined`.
 * @param { any } value
 * @returns { boolean }
 */
function defined(value) {
  return value !== undefined;
}
exports.defined = defined;

/**
 * Returns `true` if the operand (one) is divisble by the test's argument
 * (two).
 * @param { number } one
 * @param { number } two
 * @returns { boolean }
 */
function divisibleby(one, two) {
  return one % two === 0;
}
exports.divisibleby = divisibleby;

/**
 * Returns true if the string has been escaped (i.e., is a SafeString).
 * @param { any } value
 * @returns { boolean }
 */
function escaped(value) {
  return value instanceof SafeString;
}
exports.escaped = escaped;

/**
 * Returns `true` if the arguments are strictly equal.
 * @param { any } one
 * @param { any } two
 */
function equalto(one, two) {
  return one === two;
}
exports.equalto = equalto;

// Aliases
exports.eq = exports.equalto;
exports.sameas = exports.equalto;

/**
 * Returns `true` if the value is evenly divisible by 2.
 * @param { number } value
 * @returns { boolean }
 */
function even(value) {
  return value % 2 === 0;
}
exports.even = even;

/**
 * Returns `true` if the value is falsy - if I recall correctly, '', 0, false,
 * undefined, NaN or null. I don't know if we should stick to the default JS
 * behavior or attempt to replicate what Python believes should be falsy (i.e.,
 * empty arrays, empty dicts, not 0...).
 * @param { any } value
 * @returns { boolean }
 */
function falsy(value) {
  return !value;
}
exports.falsy = falsy;

/**
 * Returns `true` if the operand (one) is greater or equal to the test's
 * argument (two).
 * @param { number } one
 * @param { number } two
 * @returns { boolean }
 */
function ge(one, two) {
  return one >= two;
}
exports.ge = ge;

/**
 * Returns `true` if the operand (one) is greater than the test's argument
 * (two).
 * @param { number } one
 * @param { number } two
 * @returns { boolean }
 */
function greaterthan(one, two) {
  return one > two;
}
exports.greaterthan = greaterthan;

// alias
exports.gt = exports.greaterthan;

/**
 * Returns `true` if the operand (one) is less than or equal to the test's
 * argument (two).
 * @param { number } one
 * @param { number } two
 * @returns { boolean }
 */
function le(one, two) {
  return one <= two;
}
exports.le = le;

/**
 * Returns `true` if the operand (one) is less than the test's passed argument
 * (two).
 * @param { number } one
 * @param { number } two
 * @returns { boolean }
 */
function lessthan(one, two) {
  return one < two;
}
exports.lessthan = lessthan;

// alias
exports.lt = exports.lessthan;

/**
 * Returns `true` if the string is lowercased.
 * @param { string } value
 * @returns { boolean }
 */
function lower(value) {
  return value.toLowerCase() === value;
}
exports.lower = lower;

/**
 * Returns `true` if the operand (one) is less than or equal to the test's
 * argument (two).
 * @param { number } one
 * @param { number } two
 * @returns { boolean }
 */
function ne(one, two) {
  return one !== two;
}
exports.ne = ne;

/**
 * Returns true if the value is strictly equal to `null`.
 * @param { any }
 * @returns { boolean }
 */
function nullTest(value) {
  return value === null;
}
exports.null = nullTest;

/**
 * Returns true if value is a number.
 * @param { any }
 * @returns { boolean }
 */
function number(value) {
  return typeof value === 'number';
}
exports.number = number;

/**
 * Returns `true` if the value is *not* evenly divisible by 2.
 * @param { number } value
 * @returns { boolean }
 */
function odd(value) {
  return value % 2 === 1;
}
exports.odd = odd;

/**
 * Returns `true` if the value is a string, `false` if not.
 * @param { any } value
 * @returns { boolean }
 */
function string(value) {
  return typeof value === 'string';
}
exports.string = string;

/**
 * Returns `true` if the value is not in the list of things considered falsy:
 * '', null, undefined, 0, NaN and false.
 * @param { any } value
 * @returns { boolean }
 */
function truthy(value) {
  return !!value;
}
exports.truthy = truthy;

/**
 * Returns `true` if the value is undefined.
 * @param { any } value
 * @returns { boolean }
 */
function undefinedTest(value) {
  return value === undefined;
}
exports.undefined = undefinedTest;

/**
 * Returns `true` if the string is uppercased.
 * @param { string } value
 * @returns { boolean }
 */
function upper(value) {
  return value.toUpperCase() === value;
}
exports.upper = upper;

/**
 * If ES6 features are available, returns `true` if the value implements the
 * `Symbol.iterator` method. If not, it's a string or Array.
 *
 * Could potentially cause issues if a browser exists that has Set and Map but
 * not Symbol.
 *
 * @param { any } value
 * @returns { boolean }
 */
function iterable(value) {
  if (typeof Symbol !== 'undefined') {
    return !!value[Symbol.iterator];
  } else {
    return Array.isArray(value) || typeof value === 'string';
  }
}
exports.iterable = iterable;

/**
 * If ES6 features are available, returns `true` if the value is an object hash
 * or an ES6 Map. Otherwise just return if it's an object hash.
 * @param { any } value
 * @returns { boolean }
 */
function mapping(value) {
  // only maps and object hashes
  var bool = value !== null && value !== undefined && typeof value === 'object' && !Array.isArray(value);
  if (Set) {
    return bool && !(value instanceof Set);
  } else {
    return bool;
  }
}
exports.mapping = mapping;

/***/ }),
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


function _cycler(items) {
  var index = -1;
  return {
    current: null,
    reset: function reset() {
      index = -1;
      this.current = null;
    },
    next: function next() {
      index++;
      if (index >= items.length) {
        index = 0;
      }
      this.current = items[index];
      return this.current;
    }
  };
}
function _joiner(sep) {
  sep = sep || ',';
  var first = true;
  return function () {
    var val = first ? '' : sep;
    first = false;
    return val;
  };
}

// Making this a function instead so it returns a new object
// each time it's called. That way, if something like an environment
// uses it, they will each have their own copy.
function globals() {
  return {
    range: function range(start, stop, step) {
      if (typeof stop === 'undefined') {
        stop = start;
        start = 0;
        step = 1;
      } else if (!step) {
        step = 1;
      }
      var arr = [];
      if (step > 0) {
        for (var i = start; i < stop; i += step) {
          arr.push(i);
        }
      } else {
        for (var _i = start; _i > stop; _i += step) {
          // eslint-disable-line for-direction
          arr.push(_i);
        }
      }
      return arr;
    },
    cycler: function cycler() {
      return _cycler(Array.prototype.slice.call(arguments));
    },
    joiner: function joiner(sep) {
      return _joiner(sep);
    }
  };
}
module.exports = globals;

/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

var path = __webpack_require__(0);
module.exports = function express(env, app) {
  function NunjucksView(name, opts) {
    this.name = name;
    this.path = name;
    this.defaultEngine = opts.defaultEngine;
    this.ext = path.extname(name);
    if (!this.ext && !this.defaultEngine) {
      throw new Error('No default engine was specified and no extension was provided.');
    }
    if (!this.ext) {
      this.name += this.ext = (this.defaultEngine[0] !== '.' ? '.' : '') + this.defaultEngine;
    }
  }
  NunjucksView.prototype.render = function render(opts, cb) {
    env.render(this.name, opts, cb);
  };
  app.set('view', NunjucksView);
  app.set('nunjucksEnv', env);
  return env;
};

/***/ }),
/* 17 */
/***/ (function(module, exports, __webpack_require__) {

function installCompat() {
  'use strict';

  /* eslint-disable camelcase */

  // This must be called like `nunjucks.installCompat` so that `this`
  // references the nunjucks instance
  var runtime = this.runtime;
  var lib = this.lib;
  // Handle slim case where these 'modules' are excluded from the built source
  var Compiler = this.compiler.Compiler;
  var Parser = this.parser.Parser;
  var nodes = this.nodes;
  var lexer = this.lexer;
  var orig_contextOrFrameLookup = runtime.contextOrFrameLookup;
  var orig_memberLookup = runtime.memberLookup;
  var orig_Compiler_assertType;
  var orig_Parser_parseAggregate;
  if (Compiler) {
    orig_Compiler_assertType = Compiler.prototype.assertType;
  }
  if (Parser) {
    orig_Parser_parseAggregate = Parser.prototype.parseAggregate;
  }
  function uninstall() {
    runtime.contextOrFrameLookup = orig_contextOrFrameLookup;
    runtime.memberLookup = orig_memberLookup;
    if (Compiler) {
      Compiler.prototype.assertType = orig_Compiler_assertType;
    }
    if (Parser) {
      Parser.prototype.parseAggregate = orig_Parser_parseAggregate;
    }
  }
  runtime.contextOrFrameLookup = function contextOrFrameLookup(context, frame, key) {
    var val = orig_contextOrFrameLookup.apply(this, arguments);
    if (val !== undefined) {
      return val;
    }
    switch (key) {
      case 'True':
        return true;
      case 'False':
        return false;
      case 'None':
        return null;
      default:
        return undefined;
    }
  };
  function getTokensState(tokens) {
    return {
      index: tokens.index,
      lineno: tokens.lineno,
      colno: tokens.colno
    };
  }
  if (false) {
    // i.e., not slim mode
    var Slice = nodes.Node.extend('Slice', {
      fields: ['start', 'stop', 'step'],
      init: function init(lineno, colno, start, stop, step) {
        start = start || new nodes.Literal(lineno, colno, null);
        stop = stop || new nodes.Literal(lineno, colno, null);
        step = step || new nodes.Literal(lineno, colno, 1);
        this.parent(lineno, colno, start, stop, step);
      }
    });
    Compiler.prototype.assertType = function assertType(node) {
      if (node instanceof Slice) {
        return;
      }
      orig_Compiler_assertType.apply(this, arguments);
    };
    Compiler.prototype.compileSlice = function compileSlice(node, frame) {
      this._emit('(');
      this._compileExpression(node.start, frame);
      this._emit('),(');
      this._compileExpression(node.stop, frame);
      this._emit('),(');
      this._compileExpression(node.step, frame);
      this._emit(')');
    };
    Parser.prototype.parseAggregate = function parseAggregate() {
      var _this = this;
      var origState = getTokensState(this.tokens);
      // Set back one accounting for opening bracket/parens
      origState.colno--;
      origState.index--;
      try {
        return orig_Parser_parseAggregate.apply(this);
      } catch (e) {
        var errState = getTokensState(this.tokens);
        var rethrow = function rethrow() {
          lib._assign(_this.tokens, errState);
          return e;
        };

        // Reset to state before original parseAggregate called
        lib._assign(this.tokens, origState);
        this.peeked = false;
        var tok = this.peekToken();
        if (tok.type !== lexer.TOKEN_LEFT_BRACKET) {
          throw rethrow();
        } else {
          this.nextToken();
        }
        var node = new Slice(tok.lineno, tok.colno);

        // If we don't encounter a colon while parsing, this is not a slice,
        // so re-raise the original exception.
        var isSlice = false;
        for (var i = 0; i <= node.fields.length; i++) {
          if (this.skip(lexer.TOKEN_RIGHT_BRACKET)) {
            break;
          }
          if (i === node.fields.length) {
            if (isSlice) {
              this.fail('parseSlice: too many slice components', tok.lineno, tok.colno);
            } else {
              break;
            }
          }
          if (this.skip(lexer.TOKEN_COLON)) {
            isSlice = true;
          } else {
            var field = node.fields[i];
            node[field] = this.parseExpression();
            isSlice = this.skip(lexer.TOKEN_COLON) || isSlice;
          }
        }
        if (!isSlice) {
          throw rethrow();
        }
        return new nodes.Array(tok.lineno, tok.colno, [node]);
      }
    };
  }
  function sliceLookup(obj, start, stop, step) {
    obj = obj || [];
    if (start === null) {
      start = step < 0 ? obj.length - 1 : 0;
    }
    if (stop === null) {
      stop = step < 0 ? -1 : obj.length;
    } else if (stop < 0) {
      stop += obj.length;
    }
    if (start < 0) {
      start += obj.length;
    }
    var results = [];
    for (var i = start;; i += step) {
      if (i < 0 || i > obj.length) {
        break;
      }
      if (step > 0 && i >= stop) {
        break;
      }
      if (step < 0 && i <= stop) {
        break;
      }
      results.push(runtime.memberLookup(obj, i));
    }
    return results;
  }
  function hasOwnProp(obj, key) {
    return Object.prototype.hasOwnProperty.call(obj, key);
  }
  var ARRAY_MEMBERS = {
    pop: function pop(index) {
      if (index === undefined) {
        return this.pop();
      }
      if (index >= this.length || index < 0) {
        throw new Error('KeyError');
      }
      return this.splice(index, 1);
    },
    append: function append(element) {
      return this.push(element);
    },
    remove: function remove(element) {
      for (var i = 0; i < this.length; i++) {
        if (this[i] === element) {
          return this.splice(i, 1);
        }
      }
      throw new Error('ValueError');
    },
    count: function count(element) {
      var count = 0;
      for (var i = 0; i < this.length; i++) {
        if (this[i] === element) {
          count++;
        }
      }
      return count;
    },
    index: function index(element) {
      var i;
      if ((i = this.indexOf(element)) === -1) {
        throw new Error('ValueError');
      }
      return i;
    },
    find: function find(element) {
      return this.indexOf(element);
    },
    insert: function insert(index, elem) {
      return this.splice(index, 0, elem);
    }
  };
  var OBJECT_MEMBERS = {
    items: function items() {
      return lib._entries(this);
    },
    values: function values() {
      return lib._values(this);
    },
    keys: function keys() {
      return lib.keys(this);
    },
    get: function get(key, def) {
      var output = this[key];
      if (output === undefined) {
        output = def;
      }
      return output;
    },
    has_key: function has_key(key) {
      return hasOwnProp(this, key);
    },
    pop: function pop(key, def) {
      var output = this[key];
      if (output === undefined && def !== undefined) {
        output = def;
      } else if (output === undefined) {
        throw new Error('KeyError');
      } else {
        delete this[key];
      }
      return output;
    },
    popitem: function popitem() {
      var keys = lib.keys(this);
      if (!keys.length) {
        throw new Error('KeyError');
      }
      var k = keys[0];
      var val = this[k];
      delete this[k];
      return [k, val];
    },
    setdefault: function setdefault(key, def) {
      if (def === void 0) {
        def = null;
      }
      if (!(key in this)) {
        this[key] = def;
      }
      return this[key];
    },
    update: function update(kwargs) {
      lib._assign(this, kwargs);
      return null; // Always returns None
    }
  };

  OBJECT_MEMBERS.iteritems = OBJECT_MEMBERS.items;
  OBJECT_MEMBERS.itervalues = OBJECT_MEMBERS.values;
  OBJECT_MEMBERS.iterkeys = OBJECT_MEMBERS.keys;
  runtime.memberLookup = function memberLookup(obj, val, autoescape) {
    if (arguments.length === 4) {
      return sliceLookup.apply(this, arguments);
    }
    obj = obj || {};

    // If the object is an object, return any of the methods that Python would
    // otherwise provide.
    if (lib.isArray(obj) && hasOwnProp(ARRAY_MEMBERS, val)) {
      return ARRAY_MEMBERS[val].bind(obj);
    }
    if (lib.isObject(obj) && hasOwnProp(OBJECT_MEMBERS, val)) {
      return OBJECT_MEMBERS[val].bind(obj);
    }
    return orig_memberLookup.apply(this, arguments);
  };
  return uninstall;
}
module.exports = installCompat;

/***/ })
/******/ ]);
});

}).call(this)}).call(this,require('_process'),require("timers").setImmediate)

},{"_process":315,"timers":316}],315:[function(require,module,exports){
// shim for using process in browser
var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

function defaultSetTimout() {
    throw new Error('setTimeout has not been defined');
}
function defaultClearTimeout () {
    throw new Error('clearTimeout has not been defined');
}
(function () {
    try {
        if (typeof setTimeout === 'function') {
            cachedSetTimeout = setTimeout;
        } else {
            cachedSetTimeout = defaultSetTimout;
        }
    } catch (e) {
        cachedSetTimeout = defaultSetTimout;
    }
    try {
        if (typeof clearTimeout === 'function') {
            cachedClearTimeout = clearTimeout;
        } else {
            cachedClearTimeout = defaultClearTimeout;
        }
    } catch (e) {
        cachedClearTimeout = defaultClearTimeout;
    }
} ())
function runTimeout(fun) {
    if (cachedSetTimeout === setTimeout) {
        //normal enviroments in sane situations
        return setTimeout(fun, 0);
    }
    // if setTimeout wasn't available but was latter defined
    if ((cachedSetTimeout === defaultSetTimout || !cachedSetTimeout) && setTimeout) {
        cachedSetTimeout = setTimeout;
        return setTimeout(fun, 0);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedSetTimeout(fun, 0);
    } catch(e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't trust the global object when called normally
            return cachedSetTimeout.call(null, fun, 0);
        } catch(e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error
            return cachedSetTimeout.call(this, fun, 0);
        }
    }


}
function runClearTimeout(marker) {
    if (cachedClearTimeout === clearTimeout) {
        //normal enviroments in sane situations
        return clearTimeout(marker);
    }
    // if clearTimeout wasn't available but was latter defined
    if ((cachedClearTimeout === defaultClearTimeout || !cachedClearTimeout) && clearTimeout) {
        cachedClearTimeout = clearTimeout;
        return clearTimeout(marker);
    }
    try {
        // when when somebody has screwed with setTimeout but no I.E. maddness
        return cachedClearTimeout(marker);
    } catch (e){
        try {
            // When we are in I.E. but the script has been evaled so I.E. doesn't  trust the global object when called normally
            return cachedClearTimeout.call(null, marker);
        } catch (e){
            // same as above but when it's a version of I.E. that must have the global object for 'this', hopfully our context correct otherwise it will throw a global error.
            // Some versions of I.E. have different rules for clearTimeout vs setTimeout
            return cachedClearTimeout.call(this, marker);
        }
    }



}
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = runTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    runClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        runTimeout(drainQueue);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;
process.prependListener = noop;
process.prependOnceListener = noop;

process.listeners = function (name) { return [] }

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };

},{}],316:[function(require,module,exports){
(function (setImmediate,clearImmediate){(function (){
var nextTick = require('process/browser.js').nextTick;
var apply = Function.prototype.apply;
var slice = Array.prototype.slice;
var immediateIds = {};
var nextImmediateId = 0;

// DOM APIs, for completeness

exports.setTimeout = function() {
  return new Timeout(apply.call(setTimeout, window, arguments), clearTimeout);
};
exports.setInterval = function() {
  return new Timeout(apply.call(setInterval, window, arguments), clearInterval);
};
exports.clearTimeout =
exports.clearInterval = function(timeout) { timeout.close(); };

function Timeout(id, clearFn) {
  this._id = id;
  this._clearFn = clearFn;
}
Timeout.prototype.unref = Timeout.prototype.ref = function() {};
Timeout.prototype.close = function() {
  this._clearFn.call(window, this._id);
};

// Does not start the time, just sets up the members needed.
exports.enroll = function(item, msecs) {
  clearTimeout(item._idleTimeoutId);
  item._idleTimeout = msecs;
};

exports.unenroll = function(item) {
  clearTimeout(item._idleTimeoutId);
  item._idleTimeout = -1;
};

exports._unrefActive = exports.active = function(item) {
  clearTimeout(item._idleTimeoutId);

  var msecs = item._idleTimeout;
  if (msecs >= 0) {
    item._idleTimeoutId = setTimeout(function onTimeout() {
      if (item._onTimeout)
        item._onTimeout();
    }, msecs);
  }
};

// That's not how node.js implements it but the exposed api is the same.
exports.setImmediate = typeof setImmediate === "function" ? setImmediate : function(fn) {
  var id = nextImmediateId++;
  var args = arguments.length < 2 ? false : slice.call(arguments, 1);

  immediateIds[id] = true;

  nextTick(function onNextTick() {
    if (immediateIds[id]) {
      // fn.call() is faster so we optimize for the common use-case
      // @see http://jsperf.com/call-apply-segu
      if (args) {
        fn.apply(null, args);
      } else {
        fn.call(null);
      }
      // Prevent ids from leaking
      exports.clearImmediate(id);
    }
  });

  return id;
};

exports.clearImmediate = typeof clearImmediate === "function" ? clearImmediate : function(id) {
  delete immediateIds[id];
};
}).call(this)}).call(this,require("timers").setImmediate,require("timers").clearImmediate)

},{"process/browser.js":315,"timers":316}]},{},[19])(19)
});

//# sourceMappingURL=complex-autocomplete.js.map
