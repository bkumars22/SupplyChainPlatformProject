/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
var i2uitracelevel = 0;var i2uitracetext  = "";var i2uiResizeWidthVariable  = new Array();var i2uiResizeMasterVariable = new Array();var i2uiResizeSlaveVariable  = new Array();var i2uiResizeSlave2Variable = new Array();var i2uiResizeFlagVariable   = new Array();var i2uiResizeKeyword        = "TABLERESIZE_";var i2uiResizeKeywordLength  = i2uiResizeKeyword.length;var i2uiResizeSlaveorigX       = 0;var i2uiResizeSlavenewX        = 0;var i2uiResizeSlavewhichEl     = null;var i2uiResizeSlaveOrigonmouseup;var i2uiResizeSlaveOrigonmousemove;var i2uiResizeSlaveOrigondragend;var i2uiMenuOrigonmouseup;var i2uiMenuActiveId = null;var i2uiSubMenuActiveId = null;var i2uiMenu_x = null;var i2uiMenu_y = null;var i2uiSubMenu_x = null;var i2uiSubMenu_y = null;var i2uiSubMenuFlag = null;var i2uiManageTreeTableUserFunction = null;var i2uiToggleContentUserFunction = null;var i2uiImageDirectory = "/skins/e2-modern/images";var i2uiBrowser = "IE";var i2uiActiveTreeNode = null;var i2uiDraggingRow = false;var i2uiSourceRow = null;var i2uiSyncScrollLock = false;var i2uiVerticalResizeHeightVariable  = "";var i2uiVerticalResizeKeyword        = "VERTICAL_TABLERESIZE_";var i2uiVerticalResizeKeywordLength  = i2uiVerticalResizeKeyword.length;var i2uiVerticalResizeSlaveorigY       = '0px';var i2uiVerticalResizeSlavenewY        = '0px';var i2uiVerticalResizeSlavewhichEl     = null;var i2uiVerticalResizeSlaveOrigonmouseup;var i2uiVerticalResizeSlaveOrigonmousemove;var i2uiVerticalResizeSlaveOrigondragend;var i2uiVerticalResizeSlavebuffer      = '0px';function i2uiSetImageDirectory(imageDirectory){i2uiImageDirectory = imageDirectory;}
function i2uiSetBrowserType(browser) {i2uiBrowser = browser;}
function i2uiToggleContent(item, nest, relatedroutine){var owningtable = item;if (item.tagName == "A"){item = item.childNodes[0];}
while (owningtable != null && nest > 0){if (owningtable.parentElement){owningtable = owningtable.parentElement;}
else{owningtable = owningtable.parentNode;}
if (owningtable != null && owningtable.tagName == 'TABLE'){nest--;}}
var ownerid = owningtable.id;if (ownerid == ""){var superowner = owningtable;while (superowner != null && ownerid == ""){if (superowner.parentElement){superowner = superowner.parentElement;}
else{superowner = superowner.parentNode;}
if (superowner != null && superowner.tagName == 'TABLE'){ownerid = superowner.id;}}}
if (owningtable != null){var pretogglewidth = owningtable.offsetWidth;var len = owningtable.getElementsByTagName('TBODY').length;var contenttbody;for (var i=0; i<len; i++){contenttbody = owningtable.getElementsByTagName('TBODY')[i];if (contenttbody.id == '_containerBody' ||contenttbody.id == '_containerbody' ||contenttbody.id == 'containerBodyIndent' ||contenttbody.id == 'containerbody'){var delta;if (contenttbody.style.display == "none"){contenttbody.style.display = "";item.src = i2uiImageDirectory+"/container_collapse.png";delta = contenttbody.offsetHeight;}
else{delta = 0 - contenttbody.offsetHeight;contenttbody.style.display = "none";item.src = i2uiImageDirectory+"/container_expand.png";}
if (i2uiToggleContentUserFunction != null){eval(i2uiToggleContentUserFunction+"('"+ownerid+"',"+delta+")");}
break;}}
if (pretogglewidth != owningtable.offsetWidth){owningtable.style.width = pretogglewidth + 'px';owningtable.width = pretogglewidth;}
if (relatedroutine != null){setTimeout(relatedroutine,200);}}}
function i2uiCollapseContainer(id){var nest = 1;var obj = document.getElementById(id);if (obj != null){var len = obj.rows[0].cells[0].childNodes.length;if (len == 1 && obj.tagName == "TABLE"){obj = obj.rows[0].cells[0].childNodes[0];nest = 2;len = obj.rows[0].cells[0].childNodes.length;}
for (var i=0; i<len; i++){if (obj.rows[0].cells[0].childNodes[i].tagName == 'IMG' &&obj.rows[0].cells[0].childNodes[i].src.indexOf("container_collapse.png") != -1){i2uiToggleContent(obj.rows[0].cells[0].childNodes[i], nest);obj.rows[0].cells[0].childNodes[i].src = i2uiImageDirectory+"/container_expand.png";break;}
else{if (obj.rows[0].cells[0].childNodes[i].tagName == 'A'){var len2 = obj.rows[0].cells[0].childNodes[i].childNodes.length;for (var j=0; j<len2; j++){if (obj.rows[0].cells[0].childNodes[i].childNodes[j].tagName == 'IMG' &&obj.rows[0].cells[0].childNodes[i].childNodes[j].src.indexOf("container_collapse.png") != -1){i2uiToggleContent(obj.rows[0].cells[0].childNodes[i], nest);obj.rows[0].cells[0].childNodes[i].childNodes[j].src = i2uiImageDirectory+"/container_expand.png";break;}}
break;}}}}}
function i2uiExpandContainer(id){var nest = 1;var obj = document.getElementById(id);if (obj != null){var len = obj.rows[0].cells[0].childNodes.length;if (len == 1 && obj.tagName == "TABLE"){obj = obj.rows[0].cells[0].childNodes[0];nest = 2;len = obj.rows[0].cells[0].childNodes.length;}
for (var i=0; i<len; i++){if (obj.rows[0].cells[0].childNodes[i].tagName == 'IMG' &&obj.rows[0].cells[0].childNodes[i].src.indexOf("container_expand.png") != -1){i2uiToggleContent(obj.rows[0].cells[0].childNodes[i], nest);obj.rows[0].cells[0].childNodes[i].src = i2uiImageDirectory+"/container_collapse.png";break;}
else{if (obj.rows[0].cells[0].childNodes[i].tagName == 'A'){var len2 = obj.rows[0].cells[0].childNodes[i].childNodes.length;for (var j=0; j<len2; j++){if (obj.rows[0].cells[0].childNodes[i].childNodes[j].tagName == 'IMG' &&obj.rows[0].cells[0].childNodes[i].childNodes[j].src.indexOf("container_expand.png") != -1){i2uiToggleContent(obj.rows[0].cells[0].childNodes[i], nest);obj.rows[0].cells[0].childNodes[i].childNodes[j].src = i2uiImageDirectory+"/container_collapse.png";break;}}
break;}}}}}
function i2uiToggleItemVisibility(id,state){var item;item = document.getElementById(id);if (item != null){if (state == null){if (item.style.display == "none"){item.style.display = "";item.style.visibility = "visible";}
else{item.style.display = "none";}}
else{if (state == 'show'){item.style.display = "";item.style.visibility = "visible";}
else{item.style.display = "none";}}}}
function i2uiToggleTabNoop(){}
function i2uiToggleTab(tabset_id, alttext, tab_element){if (tab_element.tagName == 'undefined' || tab_element.tagName == null) {return;}
var item = document.getElementById(tabset_id);item = item.getElementsByTagName('TBODY')[0];var len = item.getElementsByTagName('TR').length;if (len > 0) {item = item.getElementsByTagName('TR')[0];len = item.getElementsByTagName('TD').length;var item2;var located = -1;var selectedtabid   = "tabSelected";var unselectedtabid = "tabUnSelected";var unselectedtabid2 = "tabUnSelected";for (var i=0; i<len; i++) {item2 = item.getElementsByTagName('TD')[i];if (item2.id == "tabSelected") {selectedtabid   = item2.id;unselectedtabid = "tabUnSelected";item2.id = unselectedtabid;} else {if (item2.id == "powerTabSelected") {selectedtabid   = item2.id;unselectedtabid = "powerTabUnSelected";item2.id = unselectedtabid;}}
if (item2.getElementsByTagName('A').length > 0) {item2 = item2.getElementsByTagName('A')[0];if (item2 == tab_element) {located = i;} else {if (item2.id == "tabSelected" || item2.id == "powerTabSelected") {item2.id = unselectedtabid2;}}}}
if (located > 0) {for (i=located-1; i<located+2; i++) {item2 = item.getElementsByTagName('TD')[i];item2.id = selectedtabid;if (item2.getElementsByTagName('A').length > 0) {item2 = item2.getElementsByTagName('A')[0];item2.id = selectedtabid;}}}
item = document.getElementById(tabset_id+"_description");if (item != null) {item.innerHTML = alttext;}}}
function i2uiToggleVerticalTab(tabset_id, alttext, tab_element){if (tab_element.tagName == 'undefined' ||tab_element.tagName == null){return;}
var item = document.getElementById(tabset_id);var item3 = item.getElementsByTagName('TBODY')[0];var len;var len2 = item3.getElementsByTagName('TR').length;if (len2 > 0){var item2;var located = -1;var selectedtabid   = "tabSelected";var unselectedtabid = "tabUnSelected";for (var j=0; j<len2; j++){item = item3.getElementsByTagName('TR')[j];len = item.getElementsByTagName('TD').length;for (var i=0; i<len; i++){item2 = item.getElementsByTagName('TD')[i];if (item2.id == "tabSelectedVert"){item2.id = "tabUnSelectedVert";}
else{if (item2.id == "tabSelectedVert2"){item2.id = "tabUnSelectedVert2";}}
if (item2.getElementsByTagName('A').length > 0){item2 = item2.getElementsByTagName('A')[0];if (item2 == tab_element){located = j;}
else{if (item2.id == "tabSelectedVert"){item2.id = "tabUnSelectedVert";}}}}}
if (located > -1){item = tab_element.parentElement;if (item != null){item = item.parentElement;}
if (item != null){item2 = item.getElementsByTagName('TD')[0];if (item2 != null){item2.id = "tabSelectedVert";}
item2 = item.getElementsByTagName('TD')[1];if (item2 != null){item2.id = "tabSelectedVert2";if (item2.getElementsByTagName('A').length > 0){item2 = item2.getElementsByTagName('A')[0];item2.id = "tabSelectedVert";}}}
item = item3.getElementsByTagName('TR')[located+1];if (item != null){var len = item.getElementsByTagName('TD').length;for (i=0; i<len; i++){item2 = item.getElementsByTagName('TD')[i];if (item2 != null){item2.id = "tabSelectedVert";}}}}}
if (item != null){item = document.getElementById(tabset_id+"_description");}
if (item != null){item.innerHTML = alttext;}}
function i2uiLoadContent(id, src){var item = document.getElementById(id);if (item != null){var cmd = "document.getElementById('"+id+"').src='"+src+"'";setTimeout(cmd,50);}}
function i2uiCheckForAlignmentRow(tableid){var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");if (headeritem   != null &&dataitem     != null &&dataitem.rows.length > 0){var lastheaderrow = headeritem.rows.length - 1;var len = headeritem.rows[lastheaderrow].cells.length;if(dataitem.rows[dataitem.rows.length-1].className != 'tableColumnHeadings' ) {return false;}}
return true;}
function i2uiResizeColumnsWithFixedHeaderHeight(tableid, headerheight, slave){var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");var scrolleritem  = document.getElementById(tableid+"_scroller");if (headeritem   != null &&dataitem     != null &&scrolleritem != null &&dataitem.rows.length > 0){if (headeritem.style.tableLayout != "fixed"){var newrow = null;if (document.all)
newrow = headeritem.insertRow();else
newrow = document.createElement('tr');if (newrow != null){newrow.className = "tableColumnHeadings";var i;var lastheaderrow = headeritem.rows.length - 2;var len = headeritem.rows[lastheaderrow].cells.length;var newcell;var attempts;var newcellwidth;var widths = new Array();var overallwidth = len - 1; // initial width is for cell dividers
if (slave != null || slave == 1){var shrinkWidth = 5 * dataitem.rows[0].cells.length;dataitem.style.width = shrinkWidth + 'px';headeritem.style.width = shrinkWidth + 'px';}
for (i=0; i<len-1; i++){headeritem.rows[lastheaderrow].cells[i].width = 15;headeritem.rows[lastheaderrow].cells[i].style.width = 15 + 'px';dataitem.rows[0].cells[i].width = 15;dataitem.rows[0].cells[i].style.width = 15 + 'px';}
if( document.all)
newcell = newrow.insertCell();else {newcell = document.createElement('td');newrow.appendChild(newcell);}
if (newcell != null){var growthfactor;if (scrolleritem.offsetWidth > headeritem.clientWidth)
growthfactor = Math.max(100,scrolleritem.offsetWidth-headeritem.clientWidth);else
growthfactor = 100;var tempWidth = headeritem.clientWidth + growthfactor;headeritem.style.width = tempWidth + 'px';for (i=0; i<len; i++){newcell.style.width = 15 + 'px';newcell.innerHTML = headeritem.rows[lastheaderrow].cells[i].innerHTML;newcellwidth = newcell.clientWidth;attempts = 0;while (attempts < 8 &&newrow.clientHeight > headerheight){attempts++;newcellwidth = parseInt(newcellwidth * 1.25);newcell.style.width = newcellwidth + 'px';}
widths[i] = Math.max(newcell.clientWidth,
dataitem.rows[0].cells[i].clientWidth);overallwidth += widths[i];}
var spread = parseInt((scrolleritem.clientWidth - overallwidth) / len);if (spread > 0){overallwidth += spread * len;for (i=0; i<len; i++)
widths[i] += spread;}
var newitem;for (i=0; i<len; i++){newitem = document.createElement("<COL width="+widths[i]+">");if (newitem != null)
headeritem.appendChild(newitem);newitem = document.createElement("<COL width="+widths[i]+">");if (newitem != null)
dataitem.appendChild(newitem);}
headeritem.style.tableLayout = "fixed";dataitem.style.tableLayout = "fixed";headeritem.rows[lastheaderrow].style.height = headerheight + 'px';for (i=0; i<len; i++){headeritem.rows[lastheaderrow].cells[i].style.width = widths[i] + 'px';dataitem.rows[0].cells[i].style.width = widths[i] + 'px';}}}}}
return headeritem.clientWidth;}
function i2uiTableHasHorizontalScroller(tableid){var rc = false;var scrolleritem  = document.getElementById(tableid+"_scroller");var scrolleritem2 = document.getElementById(tableid+"_header_scroller");if (scrolleritem != null && scrolleritem2 != null){var adjust = scrolleritem2.clientWidth - scrolleritem.clientWidth;if (adjust != 0){rc = true;}}
return rc;}
function i2uiManageTableScroller(id,maxheight,newheight){}
function i2uiShrinkScrollableTable(tableid){if (tableid == null)
return;var tableitem     = document.getElementById(tableid);var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");var scrolleritem  = document.getElementById(tableid+"_scroller");if (tableitem != null &&headeritem != null &&dataitem != null &&scrolleritem != null){var newwidth = 100;var scrolleritem2 = document.getElementById(tableid+"_header_scroller");if (scrolleritem2 != null){scrolleritem2.style.width = newwidth + 'px';}
scrolleritem.style.width  = newwidth + 'px';tableitem.style.width     = newwidth + 'px';dataitem.style.width      = newwidth + 'px';}}
function i2uiCheckAlignment(tableid){var rc = true;var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");if (headeritem != null && dataitem != null && dataitem.rows.length > 0) {var lastheaderrow = headeritem.rows.length - 1;var len = headeritem.rows[lastheaderrow].cells.length;for (var i=0; i<len; i++) {if (headeritem.rows[lastheaderrow].cells[i].clientWidth != dataitem.rows[0].cells[i].clientWidth) {rc = false;break;}}}
return rc;}
function i2uiResizeTable(mastertableid, minheight, minwidth, slavetableid, flag){var tableitem     = document.getElementById(mastertableid);var headeritem    = document.getElementById(mastertableid+"_header");var dataitem      = document.getElementById(mastertableid+"_data");var scrolleritem  = document.getElementById(mastertableid+"_scroller");var scrolleritem2 = document.getElementById(mastertableid+"_header_scroller");if (tableitem != null &&headeritem != null &&dataitem != null &&scrolleritem != null &&scrolleritem2 != null){scrolleritem2.style.width = "100px";scrolleritem.style.height = "100px";scrolleritem.style.width  = "100px";tableitem.style.width  = "100px";dataitem.style.width   = "100px";headeritem.style.width = "100px";var cmd = "i2uiResizeScrollableArea('"+mastertableid+"', '"+minheight+"', '"+minwidth+"', '"+slavetableid+"', '"+flag+"')";for (var i=0; i<4; i++){eval(cmd);if (i2uiCheckAlignment(mastertableid)){break;}}}}
function i2uiSyncdScroll(mastertableid, slavetableid){var masterscrolleritem;var slavescrolleritem;if (slavetableid == null){masterscrolleritem = document.getElementById(mastertableid+"_scroller");slavescrolleritem  = document.getElementById(mastertableid+"_header_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){slavescrolleritem.scrollTop  = masterscrolleritem.scrollTop;slavescrolleritem.scrollLeft = masterscrolleritem.scrollLeft;}}
else{masterscrolleritem = document.getElementById(mastertableid+"_scroller");slavescrolleritem  = document.getElementById(slavetableid+"_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){slavescrolleritem.scrollTop  = masterscrolleritem.scrollTop;}}}
function i2uiSetSyncScrollLock(scrollLock) {i2uiSyncScrollLock = scrollLock;if (i2uiSyncScrollLock) {var upperFrame = '';var lowerFrame = '';if (window.name == "UpperSectionFrame") {upperFrame = parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.document.getElementById('LowerSectionFrame');}
else if (parent.window.name == "UpperSectionFrame") {upperFrame = parent.parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.parent.document.getElementById('LowerSectionFrame');}
else if (window.name == "LowerSectionFrame") {upperFrame = parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.document.getElementById('LowerSectionFrame');}
else if (parent.window.name == "LowerSectionFrame") {upperFrame = parent.parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.parent.document.getElementById('LowerSectionFrame');}
var upperscroller = '';var lowerscroller = '';if (upperFrame) {upperscroller = upperFrame.contentWindow.document.getElementById("syncmaster_scroller");if (!upperscroller) {try {var currentSubFrame = upperFrame.contentWindow.getCurrentPageFrameName();upperscroller = upperFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");}
catch (e) {}}}
if (lowerFrame) {lowerscroller = lowerFrame.contentWindow.document.getElementById("syncmaster_scroller");if (!lowerscroller) {try {var currentSubFrame = lowerFrame.contentWindow.getCurrentPageFrameName();lowerscroller = lowerFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");}
catch (e) {}}}
if(!upperscroller || !lowerscroller)
return;if (window.name == "UpperSectionFrame" || window.name == "LowerSectionFrame") {parent.upperscrollLeft = upperscroller.scrollLeft;parent.lowerscrollLeft = lowerscroller.scrollLeft;}
else if (parent.window.name == "UpperSectionFrame" || parent.window.name == "LowerSectionFrame") {parent.parent.upperscrollLeft = upperscroller.scrollLeft;parent.parent.lowerscrollLeft = lowerscroller.scrollLeft;}}}
var sourceScrolled = false;function i2uiExternalSyncdScroll(){if(!i2uiSyncScrollLock)
return;var lastScrollSource = '';var upperFrame = '';var lowerFrame = '';var upperScrolled = false;var summaryScrolled = false;if (window.name == "UpperSectionFrame" || parent.window.name == "UpperSectionFrame") {upperScrolled = true;if (window.name == "UpperSectionFrame") {summaryScrolled = true;upperFrame = parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.document.getElementById('LowerSectionFrame');if (parent.syncScrollSource!='') lastScrollSource = parent.syncScrollSource;parent.syncScrollSource = 'Upper';}
else {upperFrame = parent.parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.parent.document.getElementById('LowerSectionFrame');if (parent.parent.syncScrollSource != '') lastScrollSource = parent.parent.syncScrollSource;parent.parent.syncScrollSource = 'Upper';}}
else if (window.name == "LowerSectionFrame" || parent.window.name == "LowerSectionFrame") {if (window.name == "LowerSectionFrame") {summaryScrolled = true;upperFrame = parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.document.getElementById('LowerSectionFrame');if (parent.syncScrollSource!='') lastScrollSource = parent.syncScrollSource;parent.syncScrollSource = 'Lower';}
else {upperFrame = parent.parent.document.getElementById('UpperSectionFrame');lowerFrame = parent.parent.document.getElementById('LowerSectionFrame');if (parent.parent.syncScrollSource!='') lastScrollSource = parent.parent.syncScrollSource;parent.parent.syncScrollSource = 'Lower';}}
else // not split mcv
return;var sourcescrolleritem;var targetscrolleritem;var targetheaderscrollitem;if (upperScrolled) {if (summaryScrolled) {sourcescrolleritem = upperFrame.contentWindow.document.getElementById("syncmaster_scroller");targetscrolleritem = lowerFrame.contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = lowerFrame.contentWindow.document.getElementById("syncmaster_header_scroller");if (!targetscrolleritem) { // mcv need to take care of additional frames
var currentSubFrame = lowerFrame.contentWindow.getCurrentPageFrameName();targetscrolleritem = lowerFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = lowerFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_header_scroller");}}
else { // mcv scrolled
var currentSubFrame = upperFrame.contentWindow.getCurrentPageFrameName();sourcescrolleritem = upperFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");targetscrolleritem = lowerFrame.contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = lowerFrame.contentWindow.document.getElementById("syncmaster_header_scroller");if (!targetscrolleritem) { // mcv need to take care of additional frames
var currentSubFrame = lowerFrame.contentWindow.getCurrentPageFrameName();targetscrolleritem = lowerFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = lowerFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_header_scroller");}}}
else {if (summaryScrolled) {sourcescrolleritem = lowerFrame.contentWindow.document.getElementById("syncmaster_scroller");targetscrolleritem = upperFrame.contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = upperFrame.contentWindow.document.getElementById("syncmaster_header_scroller");if (!targetscrolleritem) { // mcv need to take care of additional frames
var currentSubFrame = upperFrame.contentWindow.getCurrentPageFrameName();targetscrolleritem = upperFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = upperFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_header_scroller");}}
else { // mcv scrolled
var currentSubFrame = lowerFrame.contentWindow.getCurrentPageFrameName();sourcescrolleritem = lowerFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");targetscrolleritem = upperFrame.contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = upperFrame.contentWindow.document.getElementById("syncmaster_header_scroller");if (!targetscrolleritem) { // mcv need to take care of additional frames
var currentSubFrame = upperFrame.contentWindow.getCurrentPageFrameName();targetscrolleritem = upperFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_scroller");targetheaderscrollitem = upperFrame.contentWindow.document.getElementById(currentSubFrame).contentWindow.document.getElementById("syncmaster_header_scroller");}}}
if(!sourceScrolled) {sourceScrolled = true;var ratio = ((targetscrolleritem.scrollWidth - targetscrolleritem.width) /
(sourcescrolleritem.scrollWidth - sourcescrolleritem.width));var upperscrollLeft = 0;var lowerscrollLeft = 0;if (parent.syncScrollSource) {upperscrollLeft = parent.upperscrollLeft;lowerscrollLeft = parent.lowerscrollLeft;}
else if (parent.parent.syncScrollSource) {upperscrollLeft = parent.parent.upperscrollLeft;lowerscrollLeft = parent.parent.lowerscrollLeft;}
if (sourcescrolleritem != null &&targetscrolleritem != null &&targetheaderscrollitem != null){if (lastScrollSource!='') {if (lastScrollSource=='Upper' && sourcescrolleritem.scrollLeft == upperscrollLeft)
return;else if (lastScrollSource=='Lower' && sourcescrolleritem.scrollLeft == lowerscrollLeft)
return;}
var diff = upperScrolled ?
(sourcescrolleritem.scrollLeft - upperscrollLeft) :
(sourcescrolleritem.scrollLeft - lowerscrollLeft);targetscrolleritem.scrollLeft  += ratio * diff;if (upperScrolled) {upperscrollLeft = sourcescrolleritem.scrollLeft;lowerscrollLeft = targetscrolleritem.scrollLeft;}
else {lowerscrollLeft = sourcescrolleritem.scrollLeft;upperscrollLeft = targetscrolleritem.scrollLeft;}
targetheaderscrollitem.scrollTop = targetscrolleritem.scrollTop;if (summaryScrolled) {parent.syncScrollSource = '';parent.upperscrollLeft = upperscrollLeft;parent.lowerscrollLeft = lowerscrollLeft;}
else {parent.parent.syncScrollSource = '';parent.parent.upperscrollLeft = upperscrollLeft;parent.parent.lowerscrollLeft = lowerscrollLeft;}}
} else {sourceScrolled = false;}}
function i2uiRecap(tableid){var tableitem     = document.getElementById(tableid);var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");var scrolleritem  = document.getElementById(tableid+"_scroller");if (tableitem    != null &&headeritem   != null &&dataitem     != null &&scrolleritem != null &&dataitem.rows.length > 0){var len = headeritem.rows[0].cells.length;i2uitrace (0,"recap for "+tableid);i2uitrace (0,"data: width="+dataitem.width+" style.width="+dataitem.style.width+" client="+dataitem.clientWidth+" scroll="+dataitem.scrollWidth+" offset="+dataitem.offsetWidth);i2uitrace (0,"header: width="+headeritem.width+" style.width="+headeritem.style.width+" client="+headeritem.clientWidth+" scroll="+headeritem.scrollWidth+" offset="+headeritem.offsetWidth);i2uitrace (0,"scroller: width="+scrolleritem.width+" style.width="+scrolleritem.style.width+" client="+scrolleritem.clientWidth+" scroll="+scrolleritem.scrollWidth+" offset="+scrolleritem.offsetWidth);for (var i=0; i<len; i++){i2uitrace (0,"row i="+i+" header="+headeritem.rows[0].cells[i].clientWidth+" data="+dataitem.rows[0].cells[i].clientWidth);}}
return;}
function i2uitrace (level,text){if (level <= i2uitracelevel){if (text == 'clear'){i2uitracetext="";}
else{i2uitracetext += text;}
i2uitracetext += "<BR>";i2uitracewindow = window.open("",
"i2uitrace",
"status=no,scrollbars=yes,resizable=yes");i2uitracewindow.document.open();i2uitracewindow.document.write("<html><title>Trace Window</title><body>");i2uitracewindow.document.write(i2uitracetext);i2uitracewindow.document.write("</body></html>");i2uitracewindow.document.close();}}
function i2uiResizeSlaveonmousedown(aEvent){var name;var myEvent = aEvent ? aEvent : window.event;i2uiResizeSlaveOrigonmouseup   = document.onmouseup;i2uiResizeSlaveOrigondragend   = document.ondragend;i2uiResizeSlaveOrigonmousemove = document.onmousemove;document.onmousemove = i2uiResizeSlaveonmousemove;document.onmouseup   = i2uiResizeSlaveonmouseup;document.ondragend   = i2uiResizeSlaveonmouseup;i2uiResizeSlavewhichEl = null;i2uiResizeSlavewhichEl = aEvent? aEvent.target : event.srcElement;name = i2uiResizeSlavewhichEl.id;while (i2uiResizeSlavewhichEl.id.indexOf(i2uiResizeKeyword) == -1){i2uiResizeSlavewhichEl = i2uiResizeSlavewhichEl.parentNode;if (i2uiResizeSlavewhichEl == null){return;}}
if (i2uiResizeSlavewhichEl == null){return;}
i2uiResizeSlavewhichEl.style.cursor = "move";i2uiResizeSlaveorigX = myEvent.screenX;}
function i2uiResizeSlaveonmousemove(aEvent){if (i2uiResizeSlavewhichEl == null){if(aEvent)
return true;else
event.returnValue = true;}
else{if(aEvent && aEvent.preventDefault) {aEvent.preventDefault();return false;}
else
event.returnValue = false;}}
function i2uiResizeSlaveonmouseup(aEvent){var myEvent = aEvent ? aEvent : window.event;document.onmousemove = i2uiResizeSlaveOrigonmousemove;document.onmouseup   = i2uiResizeSlaveOrigonmouseup;document.ondragend   = i2uiResizeSlaveOrigonmouseup;if (i2uiResizeSlavewhichEl == null){return;}
i2uiResizeSlavenewX = myEvent.screenX;i2uiResizeSlaveresize();if(aEvent && aEvent.preventDefault)
aEvent.preventDefault();else
event.returnValue = false;i2uiResizeSlavewhichEl = null;}
function i2uiResizeSlaveonmouseover(aEvent){var myEvent = aEvent ? aEvent : window.event;var srcId = aEvent ? aEvent.target.id : event.srcElement.id;if (i2uiResizeSlavewhichEl == null && srcId.indexOf(i2uiResizeKeyword) != -1){if(aEvent)
aEvent.target.style.cursor = "move";else
event.srcElement.style.cursor = "move";}
if(aEvent && aEvent.preventDefault)
aEvent.preventDefault();else
event.returnValue = true;}
function i2uiResizeSlaveresize(){var distanceX = i2uiResizeSlavenewX - i2uiResizeSlaveorigX;if (distanceX != 0){if (i2uiIsVariableDefined(i2uiResizeSlavewhichEl.id.substring(i2uiResizeKeywordLength))){var w = i2uiResizeSlavewhichEl.id.substring(i2uiResizeKeywordLength);var newwidth = eval(w) + distanceX;var len = i2uiResizeWidthVariable.length;for (var i=0; i<len; i++){if (i2uiResizeWidthVariable[i] == i2uiResizeSlavewhichEl.id.substring(i2uiResizeKeywordLength)){var scrolleritem  = document.getElementById(i2uiResizeSlave2Variable[i]+"_scroller");if (scrolleritem != null){newwidth = Math.min(newwidth,scrolleritem.scrollWidth);}
if(newwidth < 0)
newwidth = 0;if(newwidth >= 0) {i2uiResizeScrollableArea(i2uiResizeSlave2Variable[i],null,newwidth);i2uiResizeScrollableArea(i2uiResizeMasterVariable[i],null,20,i2uiResizeSlaveVariable[i],i2uiResizeFlagVariable[i],newwidth);eval(i2uiResizeSlavewhichEl.id.substring(i2uiResizeKeywordLength)+"="+newwidth);}
break;}}}
else{window.status = "ERROR: variable ["+i2uiResizeSlavewhichEl.id.substring(i2uiResizeKeywordLength)+"] not valid";}}}
function i2uiResizableSlave(width, master, slave, slave2, flag){var len = i2uiResizeWidthVariable.length;i2uiResizeWidthVariable[len]  = width;i2uiResizeMasterVariable[len] = master;i2uiResizeSlaveVariable[len]  = slave;i2uiResizeSlave2Variable[len] = slave2;i2uiResizeFlagVariable[len]   = flag;var obj = document.getElementById(i2uiResizeKeyword+width);if (obj != null){obj.onmouseover      = i2uiResizeSlaveonmouseover;obj.onmousedown      = i2uiResizeSlaveonmousedown;}}
function i2uiVerticalResizableTables(prefix, buffer){var len = i2uiVerticalResizeHeightVariable.length;i2uiVerticalResizeHeightVariable  = prefix;if(buffer){i2uiVerticalResizeSlavebuffer = buffer;}
var obj = document.getElementById(i2uiVerticalResizeKeyword+prefix);if (obj != null){obj.onmouseover      = i2uiVerticalResizeSlaveonmouseover;obj.onmousedown      = i2uiVerticalResizeSlaveonmousedown;}}
function i2uiVerticalResizeSlaveonmousedown(aEvent){var myEvent = aEvent ? aEvent : window.event;var name;i2uiVerticalResizeSlaveOrigonmouseup   = document.onmouseup;i2uiVerticalResizeSlaveOrigonmousemove = document.onmousemove;i2uiVerticalResizeSlaveOrigondragend   = document.ondragend;document.onmousemove = i2uiVerticalResizeSlaveonmousemove;document.onmouseup   = i2uiVerticalResizeSlaveonmouseup;document.ondragend   = i2uiVerticalResizeSlaveonmouseup;i2uiVerticalResizeSlavewhichEl = null;i2uiVerticalResizeSlavewhichEl = aEvent ? aEvent.target : event.srcElement;name = i2uiVerticalResizeSlavewhichEl.id;window.status = "mouse down on " + name;while (i2uiVerticalResizeSlavewhichEl.id.indexOf(i2uiVerticalResizeKeyword) == -1){i2uiVerticalResizeSlavewhichEl = i2uiVerticalResizeSlavewhichEl.parentNode;if (i2uiVerticalResizeSlavewhichEl == null){return;}}
if (i2uiVerticalResizeSlavewhichEl == null){return;}
i2uiVerticalResizeSlavewhichEl.style.cursor = "move";i2uiVerticalResizeSlaveorigY = myEvent.screenY;}
function i2uiVerticalResizeSlaveonmousemove(aEvent){if (i2uiVerticalResizeSlavewhichEl == null){if(aEvent)
return true;else
event.returnValue = true;}
else{if(aEvent && aEvent.preventDefault)
aEvent.preventDefault();else
event.returnValue = false;}}
function i2uiVerticalResizeSlaveonmouseup(aEvent){var myEvent = aEvent ? aEvent : window.event;document.onmousemove = i2uiVerticalResizeSlaveOrigonmousemove;document.onmouseup   = i2uiVerticalResizeSlaveOrigonmouseup;document.ondragend   = i2uiVerticalResizeSlaveOrigondragend;if (i2uiVerticalResizeSlavewhichEl == null){window.status = "mouseup but no item";return;}
window.status = "mouseup";i2uiVerticalResizeSlavenewY = myEvent.screenY;i2uiVerticalResizeSlaveresize();if(aEvent && aEvent.preventDefault)
aEvent.preventDefault();else
event.returnValue = false;i2uiVerticalResizeSlavewhichEl = null;}
function i2uiVerticalResizeSlaveonmouseover(aEvent){var myEvent = aEvent ? aEvent : window.event;var src = aEvent ? aEvent.target : event.srcElement;if (i2uiVerticalResizeSlavewhichEl == null && src.id.indexOf(i2uiVerticalResizeKeyword) != -1){src.style.cursor = "move";}
if(aEvent && aEvent.preventDefault)
aEvent.preventDefault();else
event.returnValue = true;}
function i2uiVerticalResizeSlaveresize(){var distanceY = i2uiVerticalResizeSlavenewY - i2uiVerticalResizeSlaveorigY;i2uitrace(1,"resize dist="+distanceY);if (distanceY != 0){i2uitrace(1,"resize variable="+i2uiVerticalResizeSlavewhichEl.id);window.status = "mouse down on " + i2uiVerticalResizeSlavewhichEl.id;i2uiVerticalResizeHeight(distanceY);}}
function i2uiVerticalResizeHeight(distanceY){var i2uiVerticalResizeBox1;var i2uiVerticalResizeBox2;var hasInnerFrames = false;if (window.name == "LowerSectionFrame" || parent.window.name == "LowerSectionFrame"){if (window.name == "LowerSectionFrame"){i2uiVerticalResizeBox1 = parent.document.getElementById('UpperSectionFrame');i2uiVerticalResizeBox2 = parent.document.getElementById('LowerSectionFrame');} else {i2uiVerticalResizeBox1 = parent.parent.document.getElementById('UpperSectionFrame');i2uiVerticalResizeBox2 = parent.parent.document.getElementById('LowerSectionFrame');hasInnerFrames = true;}}
else
return;var height1 = i2uiVerticalResizeBox1.height;var height2 = i2uiVerticalResizeBox2.height;if(distanceY < 0){var height = height1 * 1 + distanceY * 1;if(height < 100)
height = 100;i2uiVerticalResizeBox1.height = height;i2uiVerticalResizeBox1.style.height = height + 'px';var totalHeight = 0;if(hasInnerFrames) {totalHeight = parent.parent.document.body.clientHeight;}
else {totalHeight = parent.document.body.clientHeight;}
var newHeight = totalHeight * 1 - height * 1 - i2uiVerticalResizeSlavebuffer * 1;i2uiVerticalResizeBox2.height = newHeight;i2uiVerticalResizeBox2.style.height = newHeight + 'px';}
else {var height = height2 * 1 - distanceY * 1;if(height < 100)
height = 100;i2uiVerticalResizeBox2.height = height;i2uiVerticalResizeBox2.style.height = height + 'px';var totalHeight = 0;if(hasInnerFrames){totalHeight = parent.parent.document.body.clientHeight;} else {totalHeight = parent.document.body.clientHeight;}
var newHeight = totalHeight * 1 - height * 1 - i2uiVerticalResizeSlavebuffer * 1;i2uiVerticalResizeBox1.height = newHeight;i2uiVerticalResizeBox1.style.height = newHeight + 'px';}
if(hasInnerFrames) {if(parent.parent.updateCtx)
parent.parent.updateCtx(i2uiVerticalResizeBox1.height, i2uiVerticalResizeBox2.height);} else {if(parent.updateCtx)
parent.updateCtx(i2uiVerticalResizeBox1.height, i2uiVerticalResizeBox2.height);}}
function i2uiIsVariableDefined(name){var check;eval("check = ''+window."+name);if (check == "undefined"){return false;}
else{return true;}}
function i2uiManagePadTree(tablename, cellname, column, relatedtablenames, name, recurse, relatedroutine){if (recurse == null && relatedtablenames != null){var img = document.getElementById("TREECELLIMAGE_"+tablename+"_"+cellname);if (img != null && img.src.indexOf("bullet") == -1)
i2uiShrinkScrollableTable(relatedtablenames);}
var table;var savemasterscrolltop;var saveslavescrolltop;var loadondemand = false;table = document.getElementById(tablename+"_data");if (table == null){table = document.getElementById(tablename);}
if (table != null &&table.rows != null){var masterscrolleritem = document.getElementById(relatedtablenames+"_scroller");var slavescrolleritem  = document.getElementById(tablename+"_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){savemasterscrolltop = masterscrolleritem.scrollTop;saveslavescrolltop = slavescrolleritem.scrollTop;}
var relatedtable = null;if (relatedtablenames != null &&relatedtablenames != 'undefined'){relatedtable = document.getElementById(relatedtablenames+"_data");}
var img = document.getElementById("TREECELLIMAGE_"+tablename+"_"+cellname);if (img.src.indexOf("bullet") != -1){return;}
var action;if (img != null && img.src != null){if (img.src.indexOf("_loadondemand") != -1){loadondemand = true;}
else
if (img.src.indexOf("minus") == -1){if (recurse == null){img.src = i2uiImageDirectory+"/minus_norgie.png";action = "";}
else{action = "none";}}
else
if (img.src.indexOf("plus") == -1){if (recurse == null){img.src = i2uiImageDirectory+"/plus_norgie.png";action = "none";}
else{action = "";}}}
var depth1 = cellname.split("_").length;var len = table.rows.length;for (var i=1; i<len; i++){if (table.rows[i].cells[column].id.substr(0,cellname.length+10) == "TREECELL_"+cellname+"_"){if (action == "none"){table.rows[i].style.display = action;if (relatedtable != null){relatedtable.rows[i].style.display = action;}}
else{var depth2 = table.rows[i].cells[column].id.split("_").length;if (depth2 == depth1 + 2){table.rows[i].style.display = action;if (relatedtable != null){relatedtable.rows[i].style.display = action;}
var newcell = table.rows[i].cells[column].id.substr(9);i2uiManagePadTree(tablename,newcell,column,relatedtablenames,name,1);}}}}
if (recurse == null){if (relatedtablenames != null ||document.getElementById(tablename+"_data") != null){var masterscrolleritem = document.getElementById(relatedtablenames+"_scroller");var slavescrolleritem  = document.getElementById(tablename+"_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){masterscrolleritem.scrollTop = 0;slavescrolleritem.scrollTop  = 0;masterscrolleritem.style.scrollTop = '0px';slavescrolleritem.style.scrollTop  = '0px';}}
if (relatedroutine != null){setTimeout(relatedroutine,100);}
if (i2uiManageTreeTableUserFunction != null){if (name == null){name = 'undefined';}
if (relatedtablenames != null){eval(i2uiManageTreeTableUserFunction+"('"+tablename+"','"+relatedtablenames+"','"+action+"',"+savemasterscrolltop+",'"+name+"',"+loadondemand+")");}
else{eval(i2uiManageTreeTableUserFunction+"('"+tablename+"','undefined','"+action+"',null,'"+name+"',"+loadondemand+")");}}}}}
function i2uiManageTreeTable(tablename, cellname, column, relatedtablenames, name, recurse, relatedroutine, startat){if (recurse == null && relatedtablenames != null){var img = document.getElementById("TREECELLIMAGE_"+tablename+"_"+cellname);if (img != null && img.src.indexOf("bullet") == -1)
i2uiShrinkScrollableTable(relatedtablenames);}
var table;var savemasterscrolltop;var saveslavescrolltop;var loadondemand = false;table = document.getElementById(tablename+"_data");if (table == null){table = document.getElementById(tablename);}
if (table != null &&table.rows != null){var masterscrolleritem = document.getElementById(relatedtablenames+"_scroller");var slavescrolleritem  = document.getElementById(tablename+"_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){savemasterscrolltop = masterscrolleritem.scrollTop;saveslavescrolltop = slavescrolleritem.scrollTop;}
var relatedtable = null;if (relatedtablenames != null &&relatedtablenames != 'undefined'){relatedtable = document.getElementById(relatedtablenames+"_data");}
var img = document.getElementById("TREECELLIMAGE_"+tablename+"_"+cellname);if (img != null && img.src != null && img.src.indexOf("bullet") != -1){return;}
var action;var action2;if (img != null && img.src != null){if (img.src.indexOf("_loadondemand") != -1){loadondemand = true;action2 = "none";}
else
if (img.src.indexOf("minus") == -1){action2 = "expand";if (recurse == null){img.src = i2uiImageDirectory+"/minus_norgie.png";action = "";}
else{action = "none";}}
else
if (img.src.indexOf("plus") == -1){action2 = "collapse";if (recurse == null){img.src = i2uiImageDirectory+"/plus_norgie.png";action = "none";}
else{action = "";}}}
var depth1 = Math.floor(cellname);var len = table.rows.length;if (startat == null)
startat = 0;for (var i=startat; i<len; i++){if (table.rows[i].cells[column].id == "TREECELL_"+cellname){for (var j=i+1; j<len; j++){var newcell = table.rows[j].cells[column].id.substr(9);var depth2 = Math.floor(newcell);if (((depth2 == depth1 + 10 || depth2 == depth1 + 15) && action2 == "expand") ||(depth2 > depth1 + 5 && action2 == "collapse")){table.rows[j].style.display = action;if (relatedtable != null)
relatedtable.rows[j].style.display = action;if (depth2 == depth1 + 10 && action2 == "expand" && recurse == null){i2uiManageTreeTable(tablename,newcell,column,relatedtablenames,name,1,null,j);}}
if (depth2 <= depth1){break;}}
break;}}
if (recurse == null){if (relatedtablenames != null ||document.getElementById(tablename+"_data") != null){var masterscrolleritem = document.getElementById(relatedtablenames+"_scroller");var slavescrolleritem  = document.getElementById(tablename+"_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){masterscrolleritem.scrollTop = 0;slavescrolleritem.scrollTop  = 0;masterscrolleritem.style.scrollTop = '0px';slavescrolleritem.style.scrollTop  = '0px';}}
if (relatedroutine != null){setTimeout(relatedroutine,100);}
if (i2uiManageTreeTableUserFunction != null){if (name == null){name = 'undefined';}
if (relatedtablenames != null){eval(i2uiManageTreeTableUserFunction+"('"+tablename+"','"+relatedtablenames+"','"+action+"',"+savemasterscrolltop+",'"+name+"',"+loadondemand+")");}
else{eval(i2uiManageTreeTableUserFunction+"('"+tablename+"','undefined','"+action+"',null,'"+name+"',"+loadondemand+")");}}}}}
function i2uiCollapsePadTree(tablename, depth){var column = 0;var table;var savemasterscrolltop;var saveslavescrolltop;table = document.getElementById(tablename+"_data");if (table == null){table = document.getElementById(tablename);}
if (table != null &&table.rows != null){var len = table.rows.length;var rowdepth;var img;var cellname;var childkey;var childnode;for (var i=0; i<len; i++){rowdepth = table.rows[i].cells[column].id.split("_").length - 2;if (rowdepth <0)
continue;cellname = table.rows[i].cells[column].id.substr(9);childkey = "TREECELLIMAGE_"+tablename+"_"+cellname + "_1";childnode = document.getElementById(childkey);img = document.getElementById("TREECELLIMAGE_"+tablename+"_"+cellname);if (rowdepth == depth){if (img != null){if (childnode == null){if (img.src.indexOf("plus_loadondemand.png") == -1)
img.src = i2uiImageDirectory+"/tree_bullet.png";}
else{img.src = i2uiImageDirectory+"/plus_norgie.png";}}
table.rows[i].style.display = "";}
else{if (rowdepth > depth){if (childnode == null){if (img != null){if (img.src.indexOf("plus_loadondemand.png") == -1)
img.src = i2uiImageDirectory+"/tree_bullet.png";}}
table.rows[i].style.display = "none";}
else{if (childnode == null){if (img != null){if (img.src.indexOf("plus_loadondemand.png") == -1)
img.src = i2uiImageDirectory+"/tree_bullet.png";}}
else{if (img != null){img.src = i2uiImageDirectory+"/minus_norgie.png";}}
table.rows[i].style.display = "";}}}}}
function i2uiCollapseTreeTable(tablename, depth, relatedtablenames, column, processdescendants){if (column == null){column = 0;}
var table;var savemasterscrolltop;var saveslavescrolltop;table = document.getElementById(tablename+"_data");if (table == null){table = document.getElementById(tablename);}
if (table != null &&table.rows != null){var relatedtable = null;if (relatedtablenames != null &&relatedtablenames != 'undefined'){relatedtable = document.getElementById(relatedtablenames+"_data");var masterscrolleritem = document.getElementById(relatedtablenames+"_scroller");if (masterscrolleritem != null){savemasterscrolltop = masterscrolleritem.scrollTop;}}
var len = table.rows.length;var rowdepth;var newdepth;var node;var img;var cellname;var childkey;var childnode;for (var i=0; i<len; i++){cellname = table.rows[i].cells[column].id.substr(9);rowdepth = Math.floor(cellname);node = cellname.split(".")[1];if (rowdepth < 0)
continue;newdepth = rowdepth + 10;node++;childkey = "TREECELLIMAGE_"+tablename+"_"+newdepth+"."+node;childnode = document.getElementById(childkey);img = document.getElementById("TREECELLIMAGE_"+tablename+"_"+cellname);if (rowdepth == depth * 10){if (img != null){if (childnode == null){if (img.src.indexOf("plus_loadondemand.png") == -1)
img.src = i2uiImageDirectory+"/tree_bullet.png";}
else{img.src = i2uiImageDirectory+"/plus_norgie.png";}}
table.rows[i].style.display = "";if (relatedtable != null && relatedtable.rows[i] != null){relatedtable.rows[i].style.display = "";}}
else{if (rowdepth == (depth * 10) + 5){continue;}
else
if (rowdepth + 5 > depth * 10){if (childnode == null){if (img != null){if (img.src.indexOf("plus_loadondemand.png") == -1)
img.src = i2uiImageDirectory+"/tree_bullet.png";}}
else{if (processdescendants != null && processdescendants){img.src = i2uiImageDirectory+"/plus_norgie.png";}}
table.rows[i].style.display = "none";if (relatedtable != null && relatedtable.rows[i] != null){relatedtable.rows[i].style.display = "none";}}
else{if (childnode == null){if (img != null){if (img.src.indexOf("plus_loadondemand.png") == -1)
img.src = i2uiImageDirectory+"/tree_bullet.png";}}
else{if (img != null){img.src = i2uiImageDirectory+"/minus_norgie.png";}}
table.rows[i].style.display = "";if (relatedtable != null && relatedtable.rows[i] != null){relatedtable.rows[i].style.display = "";}}}}
if (relatedtablenames != null ||document.getElementById(tablename+"_data") != null){var masterscrolleritem = document.getElementById(relatedtablenames+"_scroller");var slavescrolleritem  = document.getElementById(tablename+"_scroller");if (slavescrolleritem != null &&masterscrolleritem != null){masterscrolleritem.scrollTop = 0;slavescrolleritem.scrollTop  = 0;masterscrolleritem.style.scrollTop = '0px';slavescrolleritem.style.scrollTop  = '0px';}
if (i2uiManageTreeTableUserFunction != null){if (relatedtablenames != null){eval(i2uiManageTreeTableUserFunction+"('"+tablename+"','"+relatedtablenames+"','none',"+savemasterscrolltop+")");}
else{eval(i2uiManageTreeTableUserFunction+"('"+tablename+"','undefined','none')");}}}}}
function i2uiSetTabFillerHeight(id){var filler_obj = document.getElementById(id+"_filler");var tab_table_obj = document.getElementById(id);if (filler_obj != null && tab_table_obj != null){var container_table_obj = tab_table_obj.parentNode;while (container_table_obj != null){if (container_table_obj.tagName == 'TABLE'){break;}
container_table_obj = container_table_obj.parentNode;}
if (container_table_obj != null &&tab_table_obj != null){var h = container_table_obj.offsetHeight -
tab_table_obj.offsetHeight;filler_obj.style.height = h + 'px';}}}
function i2uiResizeScrollableContainer(id, height, delta, width, useminheight, autoscrollers){if (!i2uiIsSuitableBrowser()){return;}
var scroller_obj = document.getElementById(id+"_scroller");if (scroller_obj != null){i2uiComputeScrollHeight(id+"_scroller",true);if (width != null){scroller_obj.style.width = Math.max(1,width) + 'px';var obj = document.getElementById(id);if (obj != null){obj.style.width = Math.max(1,width) + 'px';if (width > 0 &&width < obj.offsetWidth - 3){scroller_obj.style.width = parseInt(obj.offsetWidth - 3)  + 'px';}}}
if (height != null){if (useminheight != null &&useminheight == true &&scroller_obj.scrollHeight != null){var scrollHeight = scroller_obj.scrollHeight;if (scroller_obj.offsetWidth < scroller_obj.scrollWidth)
scrollHeight += 16;scroller_obj.style.height = parseInt(Math.min(Math.max(1,height), scrollHeight))  + 'px';}
else{scroller_obj.style.height = parseInt(Math.max(1,height)) + 'px';}}
else{if (delta != null){var x = scroller_obj.offsetWidth;var y = scroller_obj.offsetHeight + delta;if (x > 0 && y > 0){if (useminheight != null &&useminheight == true &&scroller_obj.scrollHeight != null){scroller_obj.style.height = parseInt(Math.min(y, scroller_obj.scrollHeight)) + 'px';}
else{scroller_obj.style.height = y + 'px';}}}}
if (autoscrollers != null && autoscrollers == 'yes'){if (scroller_obj.scrollHeight <= scroller_obj.offsetHeight &&scroller_obj.scrollWidth  <= scroller_obj.offsetWidth    ){scroller_obj.style.overflow="hidden";}
else{scroller_obj.style.overflow="auto";}}}}
function i2uiHighlightMenuOption(obj,flag,id,menuid){i2uiSubMenuFlag = flag;if (menuid != null &&flag == "Highlighted" &&i2uiSubMenuActiveId != null &&menuid == i2uiMenuActiveId){i2uiToggleItemVisibility(i2uiSubMenuActiveId, 'hide');i2uiSubMenuActiveId = null;}
while (obj != null && obj.tagName != 'TR'){obj = obj.parentNode;}
if (obj != null){obj.className = "menu"+flag;}}
function i2uiSetMenuCoords(obj, e){if (obj.clientLeft != null && obj.clientTop != null){i2uiMenu_x = e.clientX + document.body.scrollLeft;i2uiMenu_y = e.clientY + document.body.scrollTop;}
else{if (obj.offsetLeft != null && obj.offsetTop != null){i2uiMenu_x = obj.offsetLeft + obj.offsetWidth;i2uiMenu_y = obj.offsetTop + obj.offsetHeight;}
else{i2uiMenu_x = e.pageX;i2uiMenu_y = e.pageY;}}}
function i2uiSetSubMenuCoords(obj, e){if (obj.clientTop != null){i2uiSubMenu_y = e.clientY + document.body.scrollTop - 20;}
else{if (obj.offsetTop != null){i2uiSubMenu_y = obj.offsetTop + obj.offsetHeight - 20;}
else{i2uiSubMenu_y = e.pageY - 20;}}}
function i2uiShowMenu(id){i2uiHideMenu();i2uiMenuActiveId = id;i2uiMenuOrigonmouseup = document.onmouseup;var obj;var objwidth = 25;obj = document.getElementById(id);if (obj != null &&i2uiMenu_x != null &&i2uiMenu_y != null){i2uiKeepMenuInWindow(obj, i2uiMenu_x, i2uiMenu_y, id);if (!document.layers){objwidth = obj.offsetWidth;}
i2uiSubMenu_x = i2uiMenu_x + objwidth - 5;}
document.onmouseup = i2uiCancelMenu;}
function i2uiShowSubMenu(id){if (i2uiSubMenuActiveId != null){i2uiToggleItemVisibility(i2uiSubMenuActiveId, 'hide');}
i2uiSubMenuActiveId = id;var obj;obj = document.getElementById(id);if (obj != null &&i2uiSubMenu_x != null &&i2uiSubMenu_y != null){i2uiKeepMenuInWindow(obj, i2uiSubMenu_x, i2uiSubMenu_y, id, i2uiMenu_x);}}
function i2uiKeepMenuInWindow(obj, x, y, id, x2){var ExtraSpace = 10;var WindowLeftEdge;var WindowTopEdge;var WindowWidth;var WindowHeight;if (window.innerWidth != null){WindowWidth    = window.innerWidth;WindowHeight   = window.innerHeight;}
else{WindowWidth    = document.body.clientWidth;WindowHeight   = document.body.clientHeight;}
if (window.pageXOffset != null){WindowLeftEdge = window.pageXOffset;WindowTopEdge  = window.pageYOffset;}
else{WindowLeftEdge = document.body.scrollLeft;WindowTopEdge  = document.body.scrollTop;}
var WindowRightEdge  = (WindowLeftEdge + WindowWidth) - ExtraSpace;var WindowBottomEdge = (WindowTopEdge + WindowHeight) - ExtraSpace;var MenuLeftEdge   = x;var MenuTopEdge    = y;var MenuRightEdge;var MenuBottomEdge;if (document.layers){MenuRightEdge  = x + obj.clip.width;MenuBottomEdge = y + obj.clip.height;}
else{i2uiToggleItemVisibility(id,'show');MenuRightEdge  = x + obj.offsetWidth;MenuBottomEdge = y + obj.offsetHeight;}
var dif;if (MenuRightEdge > WindowRightEdge){if (x2 == null){dif = MenuRightEdge - WindowRightEdge;}
else{dif = MenuRightEdge - x2;}
x -= dif;}
if (MenuBottomEdge > WindowBottomEdge){dif = MenuBottomEdge - WindowBottomEdge;y -= dif;}
if (x < WindowLeftEdge){x = 5;}
if (y < WindowTopEdge){y = 5;}
obj.style.left = x + 'px';obj.style.top  = y + 'px';if (x2 == null)
i2uiMenu_x = x;}
function i2uiHideMenu(){if (i2uiSubMenuActiveId != null){i2uiToggleItemVisibility(i2uiSubMenuActiveId,'hide');i2uiSubMenuActiveId = null;}
if (i2uiMenuActiveId != null){i2uiToggleItemVisibility(i2uiMenuActiveId,'hide');i2uiMenuActiveId = null;}}
function i2uiCancelMenu(e){if (i2uiSubMenuFlag != "Highlighted"){if (i2uiSubMenuActiveId != null){i2uiToggleItemVisibility(i2uiSubMenuActiveId,'hide');i2uiSubMenuActiveId = null;}
if (i2uiMenuActiveId != null){i2uiToggleItemVisibility(i2uiMenuActiveId,'hide');i2uiMenuActiveId = null;}
document.onmouseup = i2uiMenuOrigonmouseup;}}
function i2uiComputeTop(obj){if (typeof obj == "string"){obj = document.getElementById(obj);}
var y = 0;if (obj != null && obj.offsetTop != null){y = obj.offsetTop;}
if (obj != null && obj.offsetParent != null){return y + i2uiComputeTop(obj.offsetParent);}
return y;}
function i2uiComputeLeft(obj){if (typeof obj == "string"){obj = document.getElementById(obj);}
var x = 0;if (obj != null && obj.offsetLeft != null){x = obj.offsetLeft;}
if (obj != null && obj.offsetParent != null){return x + i2uiComputeLeft(obj.offsetParent);}
return x;}
function i2uiTreeTableAction(object_name, action){var owningrow = null;var owningtable = null;var object = document.getElementById(object_name);while (object){if (object.tagName == 'TR'){owningrow = object;}
else
if (object.tagName == 'TABLE'){owningtable = object;break;}
object = object.parentNode;if (owningtable){try{var priorselected = eval(owningtable.id+"ActiveTreeNode");var priorclass = eval(owningtable.id+"ActiveTreeNodeClassname");priorselected.className=priorclass;}
catch(e){}
eval(owningtable.id+"ActiveTreeNodeClassname = owningrow.className");owningrow.className = "rowHighlight";eval(owningtable.id+"ActiveTreeNode=owningrow");}}
eval(action);}
function i2uiduallistboxcopyit(fromlistbox, tolistbox, picked){if (picked == null)
picked = fromlistbox.selectedIndex;if (picked >= 0){var len = tolistbox.options.length;tolistbox.options.length++;tolistbox.options[len].text  = fromlistbox.options[picked].text;tolistbox.options[len].value = fromlistbox.options[picked].value;fromlistbox.options[picked].selected = false;}
if (fromlistbox.selectedIndex >= 0)
i2uiduallistboxcopyit(fromlistbox, tolistbox);}
function i2uiduallistboxmoveit(fromlistbox, tolistbox, picked){if (picked == null)
picked = fromlistbox.selectedIndex;if (picked >= 0){var len = tolistbox.options.length;tolistbox.options.length++;tolistbox.options[len].text = fromlistbox.options[picked].text;tolistbox.options[len].value = fromlistbox.options[picked].value;fromlistbox.options[picked].selected = false;fromlistbox.removeChild(fromlistbox.options[picked]);}
if (fromlistbox.selectedIndex >= 0)
i2uiduallistboxmoveit(fromlistbox, tolistbox);}
function i2uiduallistboxmoveall(fromlistbox, tolistbox){while(fromlistbox.options.length > 0){i2uiduallistboxmoveit(fromlistbox, tolistbox, 0);}}
function i2uiduallistboxcopyall(fromlistbox, tolistbox){var len = fromlistbox.options.length;for (var i=0; i<len; i++){i2uiduallistboxcopyit(fromlistbox, tolistbox, i);}}
function i2uiResizeRegion(insideframe){var region = document.getElementById('i2uiregion');if (region != null){var regiontop = i2uiComputeTop('i2uiregion');var newheight;newheight = document.body.offsetHeight;newheight -= regiontop;if (insideframe != null && insideframe == 'nonframedshell'){newheight -= 18;}
if (newheight < region.scrollHeight)
region.style.overflowY="auto";else
region.style.overflowY="hidden";if (region.scrollWidth < region.offsetWidth)
region.style.overflowX="hidden";else
region.style.overflowX="auto";region.style.height = newheight + 'px';}}
function i2uiBreadcrumbs() {this.speed = 40;this.iens6 = document.all||document.getElementById;}
i2uiBreadcrumbs.prototype.init = function () {this.crossobj=document.getElementById?document.getElementById("breadcrumbsContent"):document.all.breadcrumbsContent;this.contentwidth=this.crossobj.offsetWidth;}
i2uiBreadcrumbs.prototype.toString = function () { return "Breadcrumbs"; }
function i2uiSetBreadcrumbsWidth () {breadcrumbs.contentwidth = breadcrumbs.crossobj.offsetWidth;breadcrumbs.speed = initialBreadcrumbsOffset;breadcrumbs.scrollHorizontal(0);breadcrumbs.speed = 40;}
function i2uiSetBreadcrumbsScrollSpeed (speed) {breadcrumbs.speed = speed;}
i2uiBreadcrumbs.prototype.scrollHorizontal = function (dir) {if (dir == 0) {if (parseInt(this.crossobj.style.left) > (this.contentwidth*(-1) + this.speed)) {var newLeft = parseInt(this.crossobj.style.left) - this.speed;this.crossobj.style.left = newLeft + 'px';}
} else if (dir == 1) {if (parseInt(this.crossobj.style.left) < 0 ) {var maxDelta = Math.min(this.speed, (0 - parseInt(this.crossobj.style.left)));var newLeft = parseInt(this.crossobj.style.left) + maxDelta;this.crossobj.style.left = newLeft + 'px';}}}
i2uiBreadcrumbs.prototype.resetBreadcrumbsPosition = function () {this.crossobj.style.left = '0px';}
function i2uiToggleNavarea(name){var item = document.getElementById(name);if (item != null){if (item.tagName == "IFRAME" ||item.tagName == "DIV"){if (item.style.display == "none"){item.style.display = "";item.style.visibility = "visible";}
else{item.style.display = "none";}}
else
if (item.tagName == "FRAME"){var colarray = item.parentElement.cols.split(",");var result = "";for (var i=0; i<item.parentElement.children.length; i++){if (item.parentElement.children[i].name == name){if (item.style.display == "none"){item.style.display = "";item.style.visibility = "visible";colarray[i] = 170;}
else{item.style.display = "none";colarray[i] = 0;}}
if (i > 0)
result += ",";result += colarray[i];}
item.parentElement.cols = result;}}}
function i2uiScrollTabsRight(id){var obj = document.getElementById(id);if (obj != null){var tablen = obj.rows[0].cells.length;var showid = 0;for (var i=1; i<tablen-4; i++){if (obj.rows[0].cells[i].style.display == "none"){showid = i;i += 3;}
else
if (showid > 0){if (showid == 1)
i2uiToggleItemVisibility(id+'_tabscrollerright','hide');else
i2uiToggleItemVisibility(id+'_tabscrollerright','show');obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid++;obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid++;obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid++;obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid++;for (var j=showid; j<tablen-2; j++){obj.rows[0].cells[j].style.display = "";obj.rows[0].cells[j].style.visibility = "visible";}
i2uiManageTabs(id,null,"back",showid-4,null);break;}}}}
function i2uiScrollTabsLeft(id){var obj = document.getElementById(id);if (obj != null){var tablen = obj.rows[0].cells.length;var showid = 0;for (var i=tablen-3; i>4; i--){if (obj.rows[0].cells[i].style.display == "none"){showid = i;i -= 3;}
else
if (showid > 0){if (showid == 1)
i2uiToggleItemVisibility(id+'_tabscrollerright','hide');else
i2uiToggleItemVisibility(id+'_tabscrollerright','show');obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid--;obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid--;obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid--;obj.rows[0].cells[showid].style.display = "";obj.rows[0].cells[showid].style.visibility = "visible";showid--;for (var j=showid; j>0; j--){obj.rows[0].cells[j].style.display = "";obj.rows[0].cells[j].style.visibility = "visible";}
i2uiManageTabs(id,null,"front",null,showid+4);break;}}}}
function i2uiManageTabs(id, allowedwidth, direction, hiddenleft, hiddenright){var obj = document.getElementById(id);if (obj != null){if (allowedwidth == null){eval("allowedwidth = "+id+"_allowed_width");if (allowedwidth == 0)
allowedwidth = document.body.offsetWidth - 40;}
else{eval(id+"_allowed_width="+allowedwidth);}
var tablen = obj.rows[0].cells.length;var widthbefore = 0;if (direction == "front"){for (var i=1; i<tablen-4; i++){widthbefore = obj.offsetWidth;if (obj.offsetWidth <= allowedwidth ||obj.rows[0].cells[i+4].style.display == "none"){hiddenleft = i;break;}
obj.rows[0].cells[i].style.display = "none";i++;obj.rows[0].cells[i].style.display = "none";i++;obj.rows[0].cells[i].style.display = "none";i++;obj.rows[0].cells[i].style.display = "none";if (widthbefore == obj.offsetWidth){for (var j=i; j>i-4; j--){obj.rows[0].cells[j].style.display = "";obj.rows[0].cells[j].style.visibility = "visible";}
hiddenleft = j;break;}}}
else{for (var i=tablen-3; i>3; i--){widthbefore = obj.offsetWidth;if (obj.offsetWidth <= allowedwidth ||obj.rows[0].cells[i-4].style.display == "none"){hiddenright = i;break;}
obj.rows[0].cells[i].style.display = "none";i--;obj.rows[0].cells[i].style.display = "none";i--;obj.rows[0].cells[i].style.display = "none";i--;obj.rows[0].cells[i].style.display = "none";if (widthbefore == obj.offsetWidth){for (var j=i; j<i+4; j++){obj.rows[0].cells[j].style.display = "";obj.rows[0].cells[j].style.visibility = "visible";}
hiddenright = j;break;}}}
if (obj.rows[0].cells[tablen-3].style.display == "none"){i2uiToggleItemVisibility(id+'_tabscrollerright','show');var icon = document.getElementById(id+'_tabscrollerright');if (icon != null)
icon.alt = Math.ceil((tablen - hiddenright - 3) / 4) + " \xed\xaf\xec ";}
else{i2uiToggleItemVisibility(id+'_tabscrollerright','none');}
if (obj.rows[0].cells[1].style.display == "none"){i2uiToggleItemVisibility(id+'_tabscrollerleft','show');var icon = document.getElementById(id+'_tabscrollerleft');if (icon != null)
icon.alt = Math.ceil((hiddenleft - 1) / 4) + " \xed\xaf\xec ";}
else{i2uiToggleItemVisibility(id+'_tabscrollerleft','none');}}}
function i2uiResetTabs(id, allowedwidth){var obj = document.getElementById(id);if (obj != null){if (allowedwidth == null){eval("allowedwidth = "+id+"_allowed_width");if (allowedwidth == 0)
allowedwidth = document.body.offsetWidth - 40;}
else{eval(id+"_allowed_width="+allowedwidth);}
var tablen = obj.rows[0].cells.length;for (var i=1; i<tablen-3; i++){obj.rows[0].cells[i].style.display = "";obj.rows[0].cells[i].style.visibility = "visible";}
i2uiManageTabs(id,allowedwidth,"back");}}
function i2uiComputeScrollHeight(id, assign){var height = 0;var unknown = 0;var obj = document.getElementById(id)
if (obj != null){return obj.scrollHeight;}
return height;}
function i2uiComputeScrollWidth(id, assign){var width = 0;var obj = document.getElementById(id)
if (obj != null){return obj.scrollWidth;}
return width;}
function i2uiIsSuitableBrowser(){if(document.all || document.getElementById)
return true;else
return false;}
var i2uiMessageBoxRC = null;function i2uiShowMessageBox(url, height, width){if (height == null)
height = 150;else
height = Math.max(150, height);if (width == null)
width = 350;else
width = Math.max(350, width);i2uiMessageBoxRC = null;i2uiMessageBoxRC = showModalDialog(url,"","dialogWidth:"+width+"px;dialogheight:"+height+"px;status:no;unadorned:yes;help:no");return i2uiMessageBoxRC;}
function i2uiCloseMessageBox(returnValue){window.returnValue = returnValue;window.close();}
function i2uiMessageBoxInit(){var obj = document.getElementsByTagName('button');if (obj != null){obj[obj.length-1].focus();}}
var i2uiDatePickerWidthTable = new Object();i2uiDatePickerWidthTable["en"] = 149;i2uiDatePickerWidthTable["en-IE"] = 155;i2uiDatePickerWidthTable["de"] = 161;i2uiDatePickerWidthTable["de-IE"] = 173;i2uiDatePickerWidthTable["fr"] = 176;i2uiDatePickerWidthTable["fr-IE"] = 188;i2uiDatePickerWidthTable["ja"] = 148;i2uiDatePickerWidthTable["ja-IE"] = 154;i2uiDatePickerWidthTable["ko"] = 148;i2uiDatePickerWidthTable["ko-IE"] = 154;var i2uiDatePickerHeightTable = new Object();i2uiDatePickerHeightTable["en"] = 200;i2uiDatePickerHeightTable["en-IE"] = 225;i2uiDatePickerHeightTable["de"] = 200;i2uiDatePickerHeightTable["de-IE"] = 225;i2uiDatePickerHeightTable["fr"] = 200;i2uiDatePickerHeightTable["fr-IE"] = 225;i2uiDatePickerHeightTable["ja"] = 200;i2uiDatePickerHeightTable["ja-IE"] = 227;i2uiDatePickerHeightTable["ko"] = 200;i2uiDatePickerHeightTable["ko-IE"] = 227;var i2uiDatePickerJspDir = null;var i2uiDatePickerRC = null;function i2uiDatePickerCreateAndPost(refDate, localeStr, screenX, screenY, earliestDate, latestDate){i2uiDatePickerRC = null;var refDateStr = "null";var earliestDateStr = "null";var latestDateStr = "null";if (refDate != null){refDateStr = refDate.getFullYear() + "-" + refDate.getMonth() + "-" + refDate.getDate();}
if (earliestDate != null){earliestDateStr = earliestDate.getFullYear() + "-" + earliestDate.getMonth() + "-" + earliestDate.getDate();}
if (latestDate != null){latestDateStr = latestDate.getFullYear() + "-" + latestDate.getMonth() + "-" + latestDate.getDate();}
var key = null;if (localeStr != null){key = localeStr.substr(0,2);}
else{key = document.all != null ? navigator.userLanguage : navigator.language;key = key.substr(0,2);}
if (document.all != null) key += "-IE";var width = i2uiDatePickerWidthTable[key] != null ? i2uiDatePickerWidthTable[key] : (document.all != null ? 155 : 149);var height = i2uiDatePickerHeightTable[key] != null ? i2uiDatePickerHeightTable[key] : (document.all != null ? 229 : 204);var xyStr = "";if (screenX != null && screenY != null){xyStr = document.all ? ";dialogLeft:"+screenX+";dialogTop:"+screenY
: ",screenX="+screenX+",screenY="+screenY;}
var url="i2uidatepickerdialog.jsp?id=datepickerDialog&refdate="+refDateStr+
"&earliestdate="+earliestDateStr+
"&latestdate="+latestDateStr+
(localeStr != null ? "&locale="+localeStr : "");if (i2uiDatePickerJspDir != null){url = i2uiDatePickerJspDir + "/" + url;}
i2uiDatePickerRC = showModalDialog(url,"","dialogWidth:"+width+"px;dialogheight:"+height+"px;status:no;unadorned:yes;help:no"+xyStr);return i2uiDatePickerRC != null ? new Date(i2uiDatePickerRC) : null;}
function i2uiDatePickerClose(returnValue){if (returnValue != null) returnValue = returnValue.toUTCString();window.returnValue = returnValue;window.close();}
function i2uiDatePickerSetJspDir (dir){i2uiDatePickerJspDir = dir;}
function i2uiInitEditableTable(objname, ownername){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");buildit(objname, tblobj, ssobj);i2uiStartTableEdit(objname);i2uiSaveTableEdit(objname);i2uiResizeEditableTable(objname, ownername);}
function i2uiResizeEditableTable(objname, ownername){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");ownerobj = document.getElementById(ownername);ssobj.height = parseInt(ownerobj.clientHeight-38) + 'px';ssobj.width  = parseInt(Math.max(ownerobj.clientWidth-2,340)) + 'px';align(objname,tblobj,ssobj);}
function i2uiStartTableEdit(objname){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");PopulateSpreadsheet(objname, tblobj, ssobj);tblobj.style.display="none";document.getElementById(objname+"EditAction").style.display="none";document.getElementById(objname+"CancelAction").style.display="";document.getElementById(objname+"SaveAction").style.display="";document.getElementById(objname+"SortAction").style.display="";ssobj.style.display="";}
function i2uiCancelTableEdit(objname){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");tblobj.style.display="";document.getElementById(objname+"EditAction").style.display="";document.getElementById(objname+"CancelAction").style.display="none";document.getElementById(objname+"SaveAction").style.display="none";document.getElementById(objname+"SortAction").style.display="none";ssobj.style.display="none";if (eval(drawtablechart()) != null)
drawtablechart();}
function i2uiSaveTableEdit(objname){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");PopulateTable(objname, tblobj, ssobj);tblobj.style.display="";document.getElementById(objname+"EditAction").style.display="";document.getElementById(objname+"CancelAction").style.display="none";document.getElementById(objname+"SaveAction").style.display="none";document.getElementById(objname+"SortAction").style.display="none";ssobj.style.display="none";}
function i2uiExportTable(objname){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");ExportTable(objname, tblobj, ssobj);}
var i2uiSortDirection = 0;var i2uiSortColumn = -1;function i2uiSortTable(objname){tblobj = document.getElementById(objname);ssobj = document.getElementById(objname+"Spreadsheet");SortTable(objname, tblobj, ssobj);}
var i2uiActiveRowSelector = null;function i2uiToggleRowSelectionState(obj, originalstate, tableid, treecell, multiSelect){if (i2uiActiveRowSelector != null)
obj = i2uiActiveRowSelector;else{var globalselector = document.getElementById(tableid+"_globalrowselector");if (globalselector != null)
globalselector.checked = false;}
if (multiSelect == false){var table = document.getElementById(tableid+"_data");if (table == null)
table = document.getElementById(tableid);var len = table.rows.length;for (var i=0; i<len; i++){if (table.rows[i].lastClassName != null){table.rows[i].className = table.rows[i].lastClassName;if (table.rows[i].cells[0].childNodes &&table.rows[i].cells[0].childNodes.length > 0 &&table.rows[i].cells[0].childNodes[0].style != null)
table.rows[i].cells[0].childNodes[0].style.backgroundColor = "";table.rows[i].lastClassName = null;}}}
var rowobj = obj;while (rowobj != null && rowobj.tagName != "TR"){if (rowobj.parentElement){rowobj = rowobj.parentElement;}
else{rowobj = rowobj.parentNode;}}
if (rowobj != null){rowobj.className = obj.checked?"rowHighlight":originalstate;rowobj.lastClassName = originalstate;rowobj.cells[0].childNodes[0].style.backgroundColor = "";}
if (treecell != null &&treecell != '' &&i2uiActiveRowSelector == null){var cellname = rowobj.cells[treecell].id.substring(9);var depth1 = Math.floor(cellname);var table = document.getElementById(tableid);var len = table.rows.length;for (var i=0; i<len; i++){if (table.rows[i].cells[treecell].id == "TREECELL_"+cellname){for (var j=i+1; j<len; j++){var newcell = table.rows[j].cells[treecell].id.substr(9);var depth2 = Math.floor(newcell);if ((depth2 == depth1 + 10 || depth2 == depth1 + 15) ||(depth2 > depth1 + 5)){table.rows[j].cells[0].childNodes[0].checked = obj.checked;if (obj.checked){table.rows[j].className = "rowHighlight";table.rows[j].cells[0].childNodes[0].style.backgroundColor = "";}
else{var onclickhandler = table.rows[j].cells[0].childNodes[0].onclick+"!!!";var from = onclickhandler.indexOf("{");onclickhandler = onclickhandler.substring(from+1);var to = onclickhandler.lastIndexOf("}");onclickhandler = onclickhandler.substring(0,to);i2uiActiveRowSelector = table.rows[j].cells[0].childNodes[0];eval(onclickhandler);i2uiActiveRowSelector = null;}}
if (depth2 <= depth1){break;}}
var handle = depth1 - 10;for (var j=i-1; j>-1; j--){var newcell = table.rows[j].cells[treecell].id.substr(9);var depth2 = Math.floor(newcell);if (depth2 == handle || depth2 == handle-5){var selectedcount = 0;var nonselectedcount = 0;for (var k=j+1; k<len; k++){var newcell2 = table.rows[k].cells[treecell].id.substr(9);var depth3 = Math.floor(newcell2);if ((depth3 == depth2 + 10 || depth3 == depth2 + 15) ||(depth3 > depth2 + 5)){if (table.rows[k].cells[0].childNodes[0].checked)
selectedcount++;else
nonselectedcount++;}
if (depth3 <= depth2){break;}}
if (selectedcount > 0){if (nonselectedcount > 0){table.rows[j].cells[0].childNodes[0].checked=false;var onclickhandler = table.rows[j].cells[0].childNodes[0].onclick+"!!!";var from = onclickhandler.indexOf("{");onclickhandler = onclickhandler.substring(from+1);var to = onclickhandler.lastIndexOf("}");onclickhandler = onclickhandler.substring(0,to);i2uiActiveRowSelector = table.rows[j].cells[0].childNodes[0];eval(onclickhandler);i2uiActiveRowSelector = null;table.rows[j].cells[0].childNodes[0].style.backgroundColor = "#fff6a6";}
else{table.rows[j].cells[0].childNodes[0].checked = true;table.rows[j].className = "rowHighlight";table.rows[j].cells[0].childNodes[0].style.backgroundColor = "";}}
else{table.rows[j].cells[0].childNodes[0].checked=false;table.rows[j].cells[0].childNodes[0].style.backgroundColor = "";if (nonselectedcount > 0){var onclickhandler = table.rows[j].cells[0].childNodes[0].onclick+"!!!";var from = onclickhandler.indexOf("{");onclickhandler = onclickhandler.substring(from+1);var to = onclickhandler.lastIndexOf("}");onclickhandler = onclickhandler.substring(0,to);i2uiActiveRowSelector = table.rows[j].cells[0].childNodes[0];eval(onclickhandler);i2uiActiveRowSelector = null;}}
handle -= 10;if (handle < 0)
break;}}
break;}}}}
function i2uiToggleAllRowsSelectionState(obj,tableid){var tableobj = document.getElementById(tableid);if (tableobj != null){var checkboxes;if(tableobj.document)
checkboxes = tableobj.document.getElementsByTagName("INPUT");else
checkboxes = document.getElementsByTagName("INPUT");if (checkboxes != null){var len = checkboxes.length;for(var i=0; i<len; i++){if (checkboxes[i].id == tableid+"_rowselector"){i2uiActiveRowSelector = checkboxes[i];checkboxes[i].checked = obj.checked;var onclickhandler = checkboxes[i].onclick+"!!!";var from = onclickhandler.indexOf("{");onclickhandler = onclickhandler.substring(from+1);var to = onclickhandler.lastIndexOf("}");onclickhandler = onclickhandler.substring(0,to);eval(onclickhandler);}}
i2uiActiveRowSelector = null;}}}
function i2uiGetSelectedRowNums(tableid){var selected = new Array();var tableobj = document.getElementById(tableid);if (tableobj != null){var checkboxes;if(tableobj.document)
checkboxes = tableobj.document.getElementsByTagName("INPUT");else
checkboxes = document.getElementsByTagName("INPUT");if (checkboxes != null){var len = checkboxes.length;var j = 0;for(var i=0; i<len; i++){if (checkboxes[i].id == tableid+"_rowselector"){j++;if (checkboxes[i].checked)
selected[selected.length] = j;}}}}
return selected;}
function i2uiGetSelectedRowIds(tableid){var selected = new Array();var tableobj = document.getElementById(tableid);if (tableobj != null){var checkboxes;if(tableobj.document)
checkboxes = tableobj.document.getElementsByTagName("INPUT");else
checkboxes = document.getElementsByTagName("INPUT");if (checkboxes != null){var len = checkboxes.length;var j = 0;for(var i=0; i<len; i++){if (checkboxes[i].id == tableid+"_rowselector"){j++;if (checkboxes[i].checked){var rowobj = checkboxes[i];while (rowobj != null && rowobj.tagName != "TR"){if (rowobj.parentElement){rowobj = rowobj.parentElement;}
else{rowobj = rowobj.parentNode;}}
var id = null;if (rowobj != null)
id = rowobj.getAttribute("id");if (id != null && id.length > 0)
selected[selected.length] = id;else
selected[selected.length] = j;}}}}}
return selected;}
function i2uiStartedRowDragging() {i2uiDraggingRow = true;i2uiSourceRow = event.srcElement.parentNode.parentNode;}
function i2uiRowDragEnter() {if (i2uiDraggingRow) window.event.returnValue = false;}
function i2uiRowDragOver() {if (i2uiDraggingRow) {var targetRow = event.srcElement;while (targetRow.parentNode != null && targetRow.tagName && targetRow.tagName.toLowerCase() != 'tr')
targetRow = targetRow.parentNode;window.event.returnValue = false;}}
function i2uiRowDragLeave() {if (i2uiDraggingRow) {var targetRow = event.srcElement;while (targetRow.parentNode != null && targetRow.tagName && targetRow.tagName.toLowerCase() != 'tr')
targetRow = targetRow.parentNode;}}
function i2uiRowDropped() {if (i2uiDraggingRow) {var targetRow = event.srcElement;while (targetRow.parentNode != null && targetRow.tagName && targetRow.tagName.toLowerCase() != 'tr')
targetRow = targetRow.parentNode;var targetTable = targetRow.parentNode;if(targetTable != null && targetTable.tagName && targetTable.tagName.toLowerCase() != 'table')
targetTable = targetTable.parentNode;var sourceTable = i2uiSourceRow.parentNode;if(sourceTable != null && sourceTable.tagName && sourceTable.tagName.toLowerCase() != 'table')
sourceTable = sourceTable.parentNode;if(sourceTable && targetTable && sourceTable.id == targetTable.id) {var sourceIndex = i2uiSourceRow.rowIndex;var targetIndex = targetRow.rowIndex;if(sourceIndex < targetIndex) {for(i = sourceIndex; i < targetIndex; i++)
(sourceTable.rows[i]).swapNode(sourceTable.rows[i+1]);} else if(sourceIndex > targetIndex) {for(i = sourceIndex; i > targetIndex; i--)
(sourceTable.rows[i]).swapNode(sourceTable.rows[i-1]);}
recomputeTableStyles(sourceTable);}
i2uiDraggingRow = false;}}
function recomputeTableStyles(tbl) {var className = 'tableRow';for(i = 0; i < tbl.rows.length; i++) {if((tbl.rows[i].className).indexOf('tableColumnHeadings') == -1)
tbl.rows[i].className = className + (i % 2);}}
function getAdjustedHeight() {var myHeight = 0;if( typeof( window.innerWidth ) == 'number' ) {myHeight = window.innerHeight;} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {myHeight = document.documentElement.clientHeight;} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {myHeight = document.body.clientHeight;}
return myHeight;}
function getAdjustedWidth() {var myWidth = 0;if( typeof( window.innerWidth ) == 'number' ) {myWidth = window.innerWidth;} else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {myWidth = document.documentElement.clientWidth;} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {myWidth = document.body.clientWidth;}
return myWidth;}
function wrapAround(elemToWrap, wrapWith) {if (document.createRange) {     // all browsers, except IE before version 9
var rangeObj = document.createRange ();rangeObj.selectNodeContents (elemToWrap);rangeObj.surroundContents(wrapWith);var documentFragment = rangeObj.extractContents ();elemToWrap.appendChild (documentFragment);} else {          // Internet Explorer before version 9
var documentFragment = document.createDocumentFragment ();documentFragment.appendChild(wrapWith);var child = elemToWrap.firstChild;while (child) {wrapWith.appendChild (child);child = child.nextSibling;}
elemToWrap.appendChild (documentFragment);}}
function convertToArray(nodes){var nodesArray = null;try {nodesArray = Array.prototype.slice.call(nodes, 0); //non-IE and IE9+
} catch (ex) {nodesArray = new Array();for (var i=0, len=nodes.length; i < len; i++){nodesArray.push(nodes[i]);}}
return nodesArray;}
function getStyle(elem,styleProp){var y;if (elem.currentStyle)
y = elem.currentStyle[styleProp];else if (window.getComputedStyle) {var styleObj = window.getComputedStyle(elem,null);y = styleObj.getPropertyValue(styleProp);}
return y;}
function setDivWidth(cellDivId, cellObj, widthToSet, retry) {var cellDiv = document.getElementById(cellDivId);if(!cellDiv) {var divToWrap = document.createElement('div');divToWrap.id = cellDivId;divToWrap.className = 'cellWrapper';wrapAround(cellObj, divToWrap);}
cellDiv = document.getElementById(cellDivId);var paddingAdjust = parseInt(getStyle(cellObj, 'padding-left')) +
parseInt(getStyle(cellObj, 'padding-right'));if(isNaN(paddingAdjust)) {paddingAdjust = parseInt(getStyle(cellObj, 'paddingLeft')) +
parseInt(getStyle(cellObj, 'paddingRight'));if(isNaN(paddingAdjust))
paddingAdjust = 0;}
var w = (widthToSet - paddingAdjust);cellDiv.style.width = w + 'px';if(retry) {if(cellObj.clientWidth != widthToSet) {cellDiv.style.width = widthToSet + 'px';}}}
function setDivHeight(cellDivId, cellObj, heightToSet) {var cellDiv = document.getElementById(cellDivId);if(!cellDiv) {var divToWrap = document.createElement('div');divToWrap.id = cellDivId;divToWrap.className = 'cellWrapper';wrapAround(cellObj, divToWrap);}
cellDiv = document.getElementById(cellDivId);cellDiv.style.height = heightToSet + 'px';}
function i2uiResizeScrollableArea(mastertableid, minheight, minwidth, slavetableid, flag, slave2width){var slavetableid2 = null;if (slavetableid != null && slavetableid != 'undefined') {slavetableid2 = slavetableid+"2";}
var tableitem     = document.getElementById(mastertableid);var headeritem    = document.getElementById(mastertableid+"_header");var dataitem      = document.getElementById(mastertableid+"_data");var scrolleritem  = document.getElementById(mastertableid+"_scroller");if (tableitem != null && headeritem != null && dataitem != null && scrolleritem != null) {var slavewidth = 0;  // this is for the margins of the page
if (flag != null && flag != "undefined") {if (slave2width != null && slave2width != 'undefined') {slavewidth = Math.ceil(2 * (flag / 1));} else {slavewidth = flag / 1;}}
if (slavewidth == 0 && slave2width != null && slave2width != 'undefined') {slavewidth = 16;}
if (window.document.body != null && (window.document.body.scroll == null  || window.document.body.scroll == "" || window.document.body.scroll == 'yes' || window.document.body.scroll == 'auto')) {slavewidth += 16;}
if (slavetableid != null && slavetableid != 'undefined') {slavewidth += i2uiResizeColumns(slavetableid,1,1,1);}
if (slavetableid2 != null && slavetableid2 != 'undefined' && document.getElementById(slavetableid2) != null) {var x = i2uiResizeColumns(slavetableid2,1,1,1);if (slave2width != null && slave2width != 'undefined'){slavewidth += slave2width;} else {slavewidth += x;}}
if (minwidth != null && minwidth != 'undefined') {var scrolleritem2 = document.getElementById(mastertableid+"_header_scroller");if (scrolleritem2 != null) {var newwidth;if (slavetableid != null && slavetableid != 'undefined') {newwidth  = Math.max(headeritem.clientWidth, dataitem.clientWidth);newwidth  = Math.min(newwidth, minwidth);newwidth  = Math.max(minwidth, document.body.offsetWidth - slavewidth);} else {newwidth = minwidth;}
newwidth = Math.max(1,newwidth);scrolleritem2.style.width = newwidth + 'px';scrolleritem.style.width  = newwidth + 'px';if(headeritem.style.tableLayout != 'fixed') {tableitem.style.width     = newwidth + 'px';dataitem.style.width      = newwidth + 'px';}
var adjust = scrolleritem2.clientWidth - scrolleritem.clientWidth;if (adjust != 0) {var adjustedWidth = newwidth + adjust;scrolleritem.style.width  = adjustedWidth + 'px';}
if (newwidth != scrolleritem.clientWidth) {newwidth--;var adjustedWidth = newwidth + adjust;scrolleritem2.style.width = newwidth + 'px';scrolleritem.style.width  = adjustedWidth + 'px';if(headeritem.style.tableLayout != 'fixed') {tableitem.style.width     = newwidth + 'px';dataitem.style.width      = newwidth + 'px';}}
scrolleritem.scrollLeft = 0;scrolleritem2.scrollLeft = 0;if(headeritem.style.tableLayout != 'fixed') {i2uiResizeMasterColumns(mastertableid);if (!i2uiCheckAlignment(mastertableid)) {setTimeout("i2uiResizeColumns('"+mastertableid+"',1,1)", 250);}}}}
if (minheight != null && minheight != 'undefined' && (slavetableid == null || slavetableid != 'undefined')) {var newheight = Math.max(1, Math.min(dataitem.clientHeight, minheight)) + 1;scrolleritem.style.height  = newheight + 'px';var adjust = dataitem.clientHeight - scrolleritem.clientHeight + 1;if (adjust != 0) {scrolleritem.style.height = parseInt(newheight + Math.min(16,adjust)) + 'px';}
if (slavetableid != null && slavetableid != 'undefined') {var scrolleritem2 = document.getElementById(slavetableid+"_scroller");if (scrolleritem2 != null && document.getElementById(slavetableid+"_data") != null) {scrolleritem2.style.height = newheight + 'px';var adjust = scrolleritem2.clientHeight - scrolleritem.clientHeight + 1;if (adjust != 0) {scrolleritem2.style.height = parseInt(newheight - adjust) + 'px';}}
if (slavetableid2 != null && slavetableid2 != 'undefined') {var scrolleritem3 = document.getElementById(slavetableid2+"_scroller");if (scrolleritem3 != null) {scrolleritem3.style.height = newheight + 'px';var adjust = scrolleritem3.clientHeight - scrolleritem.clientHeight + 1;if (adjust != 0) {scrolleritem3.style.height = parseInt(newheight - adjust) + 'px';}}}}}}
if (mastertableid && slavetableid) {var tableDemarkerClass = " tableDemarkation";var scrolleritem2 = document.getElementById(slavetableid+"_scroller");if(scrolleritem2 != null) {var clsName = (scrolleritem2.className) ? scrolleritem2.className.replace(tableDemarkerClass,"") : "";scrolleritem2.className = clsName + tableDemarkerClass;}
if (slavetableid2 != null && slavetableid2 != 'undefined') {var scrolleritem3 = document.getElementById(slavetableid2+"_scroller");if (scrolleritem3 != null) {clsName = (scrolleritem3.className) ? scrolleritem3.className.replace(tableDemarkerClass,"") : "";scrolleritem3.className = clsName + tableDemarkerClass;}}
i2uiSyncdScroll(mastertableid);i2uiSyncdScroll(mastertableid,slavetableid);}}
function i2uiResizeColumns(tableid, shrink, copyheader, slave){var width = 0;var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");var scrolleritem  = document.getElementById(tableid+"_scroller");var scrolleritem2 = document.getElementById(tableid+"_header_scroller");if (document.getElementById(tableid) == null || headeritem == null || dataitem == null || scrolleritem == null || dataitem.rows.length <= 0) {return 0;}
if(headeritem.style.tableLayout == 'fixed') {return 0;}
if (i2uiCheckAlignment(tableid)) {return headeritem.clientWidth;}
var lastheaderrow = headeritem.rows.length - 1;var len = headeritem.rows[lastheaderrow].cells.length;if (copyheader == null || copyheader == 1) {var newwidth = dataitem.scrollWidth;var shrinkWidth = 5 * dataitem.rows[0].cells.length;dataitem.style.width = shrinkWidth + 'px';headeritem.style.width = shrinkWidth + 'px';for (i=0; i<headeritem.rows[lastheaderrow].cells.length-1; i++){headeritem.rows[lastheaderrow].cells[i].style.whiteSpace = 'normal';dataitem.rows[0].cells[i].style.whiteSpace = 'normal';headeritem.rows[lastheaderrow].cells[i].style.width = '15px';dataitem.rows[0].cells[i].style.width = '15px';}
dataitem.style.width   = newwidth + "px";headeritem.style.width = newwidth + "px";if (scrolleritem2 != null && (slave == null || slave == 0)) {for (i=0; i<len; i++) {var w = dataitem.rows[0].cells[i].clientWidth;headeritem.rows[lastheaderrow].cells[i].style.width = dataitem.rows[0].cells[i].clientWidth + 'px';setDivWidth(tableid + "_headerdiv_" + lastheaderrow + '_' + i, headeritem.rows[lastheaderrow].cells[i], w);}
return newwidth;}}
var adjust = 0;if ((scrolleritem2 != null && scrolleritem2.clientWidth < headeritem.clientWidth) || headeritem.rows[lastheaderrow].cells.length < 3) {} else {adjust = parseInt(headeritem.cellPadding, 10) * 2;if(dataitem.rows[0].cells[len-1].id.indexOf("TREECELL_") == -1) {len--;}}
if(scrolleritem.style.overflowY == 'scroll' || scrolleritem.style.overflowY == 'hidden') {headeritem.style.width = dataitem.clientWidth + 'px';dataitem.style.width   = dataitem.clientWidth + 'px';} else {if(scrolleritem2 != null) {headeritem.style.width = dataitem.clientWidth + 'px';shrink = 0;}}
if (document.body.clientWidth < headeritem.clientWidth) {len++;adjust = 0;}
if (shrink != null && shrink == 1) {var shrinkWidth = 5 * dataitem.rows[0].cells.length;dataitem.style.width = shrinkWidth + 'px';headeritem.style.width = shrinkWidth + 'px';for (i=0; i<headeritem.rows[lastheaderrow].cells.length-1; i++){headeritem.rows[lastheaderrow].cells[i].style.whiteSpace = 'normal';dataitem.rows[0].cells[i].style.whiteSpace = 'normal';headeritem.rows[lastheaderrow].cells[i].style.width = '15px';dataitem.rows[0].cells[i].style.width = '15px';}}
len = Math.min(len, headeritem.rows[lastheaderrow].cells.length);if (headeritem.clientWidth < dataitem.clientWidth) {headeritem.style.width = dataitem.clientWidth + 'px';} else {dataitem.style.width = headeritem.clientWidth + 'px';}
var w1, w2, w3;for (var i=0; i<len; i++) {w1 = headeritem.rows[lastheaderrow].cells[i].scrollWidth;w2 = dataitem.rows[0].cells[i].scrollWidth;w3 = Math.max(w1,w2) - adjust;if (w1 != w3) {setDivWidth(tableid + "_headerdiv_" + lastheaderrow + '_' + i, headeritem.rows[lastheaderrow].cells[i], w3, true);headeritem.rows[lastheaderrow].cells[i].style.width = w3 + 'px';}
if (w2 != w3) {setDivWidth(tableid + "_datadiv_0_" + i, dataitem.rows[0].cells[i], w3, true);dataitem.rows[0].cells[i].style.width = w3 + 'px';}
if (headeritem.clientWidth != dataitem.clientWidth) {if (document.body.scrollWidth != document.body.offsetWidth && (slave == null || slave == 0)) {dataitem.style.width = document.body.scrollWidth + 'px';}
if (headeritem.clientWidth < dataitem.clientWidth) {headeritem.style.width = dataitem.clientWidth + 'px';} else {dataitem.style.width = headeritem.clientWidth + 'px';}}}
return headeritem.clientWidth;}
function i2uiResizeMasterColumns(tableid){var width = 0;var headeritem    = document.getElementById(tableid+"_header");var dataitem      = document.getElementById(tableid+"_data");var scrolleritem  = document.getElementById(tableid+"_scroller");var scrolleritem2 = document.getElementById(tableid+"_header_scroller");if (document.getElementById(tableid) == null || headeritem == null || dataitem  == null || scrolleritem == null || dataitem.rows.length <= 0) {return 0;}
if(headeritem.style.tableLayout == 'fixed') {return 0;}
var i, w1, w2, w3, adjust;var lastheaderrow = headeritem.rows.length - 1;var len = headeritem.rows[lastheaderrow].cells.length;if (scrolleritem2 != null && scrolleritem2.clientWidth < headeritem.clientWidth) {adjust = 0;len--;} else {adjust = parseInt(headeritem.cellPadding, 10) * 2;len--;}
dataitem.style.width = parseInt(5 * dataitem.rows[0].cells.length) + 'px';headeritem.style.width = parseInt(5 * headeritem.rows[lastheaderrow].cells.length) + 'px';for (i=0; i<len; i++) {headeritem.rows[lastheaderrow].cells[i].style.whiteSpace = 'normal';dataitem.rows[0].cells[i].style.whiteSpace = 'normal';headeritem.rows[lastheaderrow].cells[i].style.width = '15px';dataitem.rows[0].cells[i].style.width = '15px';}
len = headeritem.rows[lastheaderrow].cells.length;for (i=0; i<len; i++) {w1 = headeritem.rows[lastheaderrow].cells[i].scrollWidth;w2 = dataitem.rows[0].cells[i].scrollWidth;w3 = Math.max(w1,w2) - adjust;if (w1 != w3) {headeritem.rows[lastheaderrow].cells[i].style.width = w3 + 'px';setDivWidth(tableid + "_headerdiv_" + lastheaderrow + '_' + i, headeritem.rows[lastheaderrow].cells[i], w3);}
if (w2 != w3) {dataitem.rows[0].cells[i].style.width = w3 + 'px';setDivWidth(tableid + "_datadiv_0_" + i, dataitem.rows[0].cells[i], w3);}}
if (dataitem.clientWidth > headeritem.clientWidth) {headeritem.style.width = dataitem.clientWidth + 'px';} else if (dataitem.clientWidth < headeritem.clientWidth) {dataitem.style.width = headeritem.clientWidth + 'px';}
if (scrolleritem.clientWidth > dataitem.clientWidth) {dataitem.style.width   = scrolleritem.clientWidth + 'px';headeritem.style.width = scrolleritem.clientWidth + 'px';}
if (!i2uiCheckAlignment(tableid)) {for (i=0; i<len; i++) {w1 = headeritem.rows[lastheaderrow].cells[i].scrollWidth;w2 = dataitem.rows[0].cells[i].scrollWidth;w3 = Math.max(w1,w2) - adjust;if (w1 != w3 || w2 != w3) {headeritem.rows[lastheaderrow].cells[i].style.width = w3 + 'px';setDivWidth(tableid + "_headerdiv_" + lastheaderrow + '_' + i, headeritem.rows[lastheaderrow].cells[i], w3);dataitem.rows[0].cells[i].style.width = w3 + 'px';setDivWidth(tableid + "_datadiv_0_" + i, dataitem.rows[0].cells[i], w3);}}}
return dataitem.clientWidth;}
