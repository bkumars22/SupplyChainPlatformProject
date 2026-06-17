<%@ page contentType="text/javascript;charset=UTF-8" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.scplatform.testing.webui.taglib.Utils" %>
<%@ page import="com.scplatform.testing.webui.taglib.Settings" %>
<%
Settings settings = Settings.getSessionSettings((HttpServletRequest)pageContext.getRequest());String localeString = request.getParameter("locale");if (localeString == null) localeString = settings.getLocale().toString();%>
var i2uiDatePickerLocaleString = '<%= URLEncoder.encode(localeString) %>';var i2uiMonthNames = new Array('<%= Utils.translate(localeString, "January") %>',
'<%= Utils.translate(localeString, "February") %>',
'<%= Utils.translate(localeString, "March") %>',
'<%= Utils.translate(localeString, "April") %>',
'<%= Utils.translate(localeString, "May") %>',
'<%= Utils.translate(localeString, "June") %>',
'<%= Utils.translate(localeString, "July") %>',
'<%= Utils.translate(localeString, "August") %>',
'<%= Utils.translate(localeString, "September") %>',
'<%= Utils.translate(localeString, "October") %>',
'<%= Utils.translate(localeString, "November") %>',
'<%= Utils.translate(localeString, "December") %>');var i2uiMonthLengths = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);var i2uiDatePicker_x = 0;var i2uiDatePicker_y = 0;var i2uiDatePickerActiveId = null;var i2uiDatePickerRefDate = null;var i2uiDatePickerEarliestDate = null;var i2uiDatePickerLatestDate = null;var i2uiDatePickerSelectedDate = null;var i2uiDatePickerSelectedDateId = null;var i2uiDatePickerToday = null;var i2uiDatePickerSearchTables = new Object();var i2uiDatePickerCalendarCellTable = new Object();var i2uiDatePickerMatchRegExp = /.*/;function showProps (msg, object, matchRegExp){if (matchRegExp == null) matchRegExp = i2uiDatePickerMatchRegExp;var str = msg + ": matching " + matchRegExp + "\n\n";var ctr = 0;var namesPerRow = 3;if (object){for (prop in object){if (prop.search(matchRegExp) != -1){str += prop + (ctr++ % namesPerRow == (namesPerRow-1) ? "\n" : " : ");}}
if (ctr == 0) str += "none";}
else{str += "null or undefined";}
alert(str);}
function findChildWithId (element, childId){var retval = null;if (element.all){retval = element.all[childId];}
else{var level = 1;var elementStack = new Array(128);var indexStack = new Array(128);elementStack[0] = element;indexStack[level] = 0;while (retval == null && level > 0){if (indexStack[level] < elementStack[level-1].childNodes.length){var child = elementStack[level-1].childNodes[indexStack[level]];if (child.id == childId){retval = child;}
else{elementStack[level] = child;++indexStack[level];++level;indexStack[level] = 0;}}
if (indexStack[level] == elementStack[level-1].childNodes.length){--level;}}}
return retval;}
function ReferenceDate(d, pBegin, pEnd, pMonth, pYear, nBegin, nEnd, nMonth, nYear){this.refDate = d;this.prevBegin = pBegin;this.prevEnd = pEnd;this.prevMonth = pMonth;this.prevYear = pYear;this.nextBegin = nBegin;this.nextEnd = nEnd;this.nextMonth = nMonth;this.nextYear = nYear;}
function i2uiIsEmbedded (){return mySelectionCallback != null && mySelectionCallback != "null";}
function i2uiDayClassName (date, month, year){var retval = "datePickerDay";var newDate = new Date(year, month, date);if (i2uiIsInRange(newDate)){if (i2uiIsSelectedDate(date, month, year)){retval = "datePickerDaySelected";}
else if (i2uiIsToday(date, month, year)){retval = "datePickerDayToday";}
else if (month == i2uiDatePickerRefDate.refDate.getMonth()){retval = "datePickerDayThisMonth";}}
else{retval = "datePickerDayNotVisible";}
return retval;}
function i2uiMonthLength (month, year){var retval = i2uiMonthLengths[month];if (month == 1 && i2uiIsLeapYear(year)) retval = 29;return retval;}
function i2uiIsLeapYear (year){return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);;}
function i2uiIsSelectedDate (date, month, year){var retval = false;if (i2uiDatePickerSelectedDate != null){retval = (date == i2uiDatePickerSelectedDate.getDate() &&month == i2uiDatePickerSelectedDate.getMonth() &&year == i2uiDatePickerSelectedDate.getFullYear());}
return retval;}
function i2uiIsToday (date, month, year){return date == i2uiDatePickerToday.getDate() &&month == i2uiDatePickerToday.getMonth() &&year == i2uiDatePickerToday.getFullYear();}
function i2uiIsInRange (date){return (i2uiDatePickerEarliestDate == null ||date.getTime() >= i2uiDatePickerEarliestDate.getTime()) &&(i2uiDatePickerLatestDate == null ||date.getTime() <= i2uiDatePickerLatestDate.getTime());}
function i2uiIsDisplayed (date, month, year){return (month == i2uiDatePickerRefDate.prevMonth &&year == i2uiDatePickerRefDate.prevYear &&date >= i2uiDatePickerRefDate.prevBegin) ||(month == i2uiDatePickerRefDate.refDate.getMonth() &&year == i2uiDatePickerRefDate.refDate.getFullYear()) ||(month == i2uiDatePickerRefDate.nextMonth &&year == i2uiDatePickerRefDate.nextYear &&date <= i2uiDatePickerRefDate.nextEnd);}
function i2uiSetDatePickerCoords(obj, e){var x, y;if (obj.clientLeft != null && obj.clientTop != null){x = e.clientX + document.body.scrollLeft;y = e.clientY + document.body.scrollTop;}
else if (obj.offsetLeft != null && obj.offsetTop != null){x = obj.offsetLeft + obj.offsetWidth;y = obj.offsetTop + obj.offsetHeight;}
else{x = e.pageX;y = e.pageY;}
i2uiSetDatePickerCoordsPrim(x, y);}
function i2uiSetDatePickerCoordsPrim(x, y){i2uiDatePicker_x = x;i2uiDatePicker_y = y;}
function i2uiHideDatePicker(){if (i2uiDatePickerActiveId != null){i2uiToggleItemVisibility(i2uiDatePickerActiveId, 'hide');i2uiDatePickerActiveId = null;i2uiDatePickerRefDate = null;i2uiDatePickerEarliestDate = null;i2uiDatePickerLatestDate = null;i2uiDatePickerSelectedDate = null;i2uiDatePickerSelectedDateId = null;i2uiDatePickerToday = null;}}
function i2uiShowDatePicker(id, refDate, earliestDate, latestDate){i2uiHideDatePicker();i2uiDatePickerActiveId = id;i2uiDatePickerToday = new Date();if (earliestDate != null) i2uiDatePickerEarliestDate = earliestDate;if (latestDate != null) i2uiDatePickerLatestDate = latestDate;if (refDate && !isNaN(refDate.valueOf())){if (! i2uiIsEmbedded()){i2uiDatePickerSelectedDate = refDate;}}
else{refDate = i2uiDatePickerToday;}
if (! i2uiIsInRange(refDate)){refDate = (i2uiDatePickerEarliestDate != null ? i2uiDatePickerEarliestDate : i2uiDatePickerLatestDate);}
var obj = null;obj = document.getElementById(id);var tdTable = new Object();i2uiDatePickerSearchTables[i2uiDatePickerActiveId] = tdTable;var tds = obj.getElementsByTagName("TD");for (var i = 0; i < tds.length; ++i){if (tds[i].id == "datePickerYearHeaderLabel" ||tds[i].id == "datePickerMonthHeaderLabel" ||tds[i].id.indexOf("cell") == 0){tdTable[tds[i].id] = tds[i];}}
i2uiUpdateDatePicker(refDate);if (obj != null &&i2uiDatePicker_x != null &&i2uiDatePicker_y != null){i2uiKeepMenuInWindow(obj, i2uiDatePicker_x, i2uiDatePicker_y, id);}}
function i2uiUpdateDatePicker(d){var startDate;var refDay = d.getDay(); // 0..6
var refDate = d.getDate();  // 1..31
var refMonth = d.getMonth(); // 0..11
var refYear = d.getFullYear(); // yyyy
var prevYear = refYear;var prevMonth = refMonth - 1;if (prevMonth == -1){--prevYear;prevMonth = 11;}
var nextYear = refYear;var nextMonth = refMonth + 1;if (nextMonth == 12){++nextYear;nextMonth = 0;}
var dayOfFirst = refDay - ((refDate - 1) % 7);if (dayOfFirst < 0) dayOfFirst += 7;var lastDate = i2uiMonthLength(prevMonth, refYear);startDate = lastDate - dayOfFirst + 1;if (dayOfFirst < 3) startDate -= 7;var endOfMonth = i2uiMonthLength(refMonth, refYear);var nextEnd = 42 - ((lastDate - startDate + 1) + endOfMonth)
i2uiDatePickerRefDate = new ReferenceDate(d, startDate, lastDate, prevMonth, prevYear, 1, nextEnd, nextMonth, nextYear);var obj = null;var tdTable = i2uiDatePickerSearchTables[i2uiDatePickerActiveId];obj = tdTable["datePickerYearHeaderLabel"];obj.innerHTML = refYear;var tdTable = i2uiDatePickerSearchTables[i2uiDatePickerActiveId];obj = tdTable["datePickerMonthHeaderLabel"];obj.innerHTML = i2uiMonthNames[refMonth];var date = startDate;var month = prevMonth;var year = prevYear;var className = null;var id = null;for (w = 0; w < 6; ++w){for (dow = 0; dow < 7; ++dow){className = i2uiDayClassName(date, month, year);id = "cell" + w + dow;if (className == "datePickerDaySelected"){i2uiDatePickerSelectedDateId = id;}
if (i2uiDatePickerCalendarCellTable[id] == null){i2uiDatePickerCalendarCellTable[id] = new Array(date, month, year);}
else{i2uiDatePickerCalendarCellTable[id][0] = date;i2uiDatePickerCalendarCellTable[id][1] = month
i2uiDatePickerCalendarCellTable[id][2] = year;}
var tdTable = i2uiDatePickerSearchTables[i2uiDatePickerActiveId];obj = tdTable[id];obj.className = className;var found = false;var childIdx = 0;for (; !found && childIdx < obj.childNodes.length; ++childIdx){found = (obj.childNodes[childIdx].tagName != null && obj.childNodes[childIdx].tagName == "A");}
if (found){--childIdx;obj.childNodes[childIdx].innerHTML = date;}
if (date == lastDate){date = 1;++month;if (month == 12){month = 0;++year;}
lastDate = i2uiMonthLength(month, year);}
else{++date;}}}}
function i2uiSetDatePickerToday(){if (i2uiDatePickerActiveId != null && i2uiIsInRange(i2uiDatePickerToday)){i2uiUpdateDatePicker(i2uiDatePickerToday);}}
function i2uiDatePickerOk(callback){eval(callback+"(i2uiDatePickerActiveId,i2uiDatePickerSelectedDate)");i2uiHideDatePicker();}
function i2uiDatePickerCancel(callback){if (callback != null && callback != '' && callback != 'null'){eval(callback+"(i2uiDatePickerActiveId)");}
i2uiHideDatePicker();}
function i2uiDatePickerPrevYear(){if (i2uiDatePickerActiveId != null &&i2uiDatePickerRefDate != null){var month = i2uiDatePickerRefDate.refDate.getMonth(); // 0..11
var year = i2uiDatePickerRefDate.refDate.getFullYear() - 1; // yyyy
var date = 31;if (month == 1){date = i2uiIsLeapYear(year) ? 29 : 28;}
else if (month == 3 ||month == 5 ||month == 8 ||month == 10){date = 30;}
var newDate = new Date(year, month, date);if (i2uiIsInRange(newDate)){i2uiUpdateDatePicker(newDate);}}}
function i2uiDatePickerNextYear(){if (i2uiDatePickerActiveId != null &&i2uiDatePickerRefDate != null){var month = i2uiDatePickerRefDate.refDate.getMonth(); // 0..11
var year = i2uiDatePickerRefDate.refDate.getFullYear() + 1; // yyyy
var newDate = new Date(year, month, 1);if (i2uiIsInRange(newDate)){i2uiUpdateDatePicker(newDate);}}}
function i2uiDatePickerPrevMonth(){if (i2uiDatePickerActiveId != null &&i2uiDatePickerRefDate != null){var month = i2uiDatePickerRefDate.refDate.getMonth(); // 0..11
var year = i2uiDatePickerRefDate.refDate.getFullYear(); // yyyy
if (--month == -1){month = 11; // Dec
--year;}
var date = 31;if (month == 1){date = i2uiIsLeapYear(year) ? 29 : 28;}
else if (month == 3 ||month == 5 ||month == 8 ||month == 10){date = 30;}
var newDate = new Date(year, month, date);if (i2uiIsInRange(newDate)){i2uiUpdateDatePicker(newDate);}}}
function i2uiDatePickerNextMonth(){if (i2uiDatePickerActiveId != null &&i2uiDatePickerRefDate != null){var month = i2uiDatePickerRefDate.refDate.getMonth(); // 0..11
var year = i2uiDatePickerRefDate.refDate.getFullYear(); // yyyy
if (++month == 12){month = 0; // Jan
++year;}
var newDate = new Date(year, month, 1);if (i2uiIsInRange(newDate)){i2uiUpdateDatePicker(newDate);}}}
function i2uiDatePickerSelect(cellId, embeddedCallback){if (i2uiDatePickerActiveId != null){var data = i2uiDatePickerCalendarCellTable[cellId];var obj = null;var newDate = new Date(data[2], data[1], data[0]);if (i2uiIsInRange(newDate)){if (! i2uiIsSelectedDate(data[0], data[1], data[2])){if (i2uiDatePickerSelectedDate != null &&i2uiIsDisplayed(i2uiDatePickerSelectedDate.getDate(),
i2uiDatePickerSelectedDate.getMonth(),
i2uiDatePickerSelectedDate.getFullYear())){var d = i2uiDatePickerSelectedDate;var id = i2uiDatePickerSelectedDateId;i2uiDatePickerSelectedDate = null;i2uiDatePickerSelectedDateId = null;var tdTable = i2uiDatePickerSearchTables[i2uiDatePickerActiveId];obj = tdTable[id];obj.className = i2uiDayClassName(d.getDate(), d.getMonth(), d.getFullYear());}
i2uiDatePickerSelectedDate = newDate;i2uiDatePickerSelectedDateId = cellId;var tdTable = i2uiDatePickerSearchTables[i2uiDatePickerActiveId];obj = tdTable[cellId];obj.className = "datePickerDaySelected";}
if (embeddedCallback != null && embeddedCallback != "null" && i2uiDatePickerSelectedDate != null){eval(embeddedCallback+"(i2uiDatePickerActiveId,i2uiDatePickerSelectedDate)");}}}
return false;}
