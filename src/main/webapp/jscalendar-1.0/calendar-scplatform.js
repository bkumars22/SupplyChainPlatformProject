/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
// Calendar functions 
// Page instance of the calendar
var calendar = null;
// This function gets called when the end-user clicks on some date.

var userDateFormat = '%Y-%m-%d';

function setUserDateFormat(format)
{
	userDateFormat = format;
}

function onCalendarSelectDefault(cal, date){
	var update = cal.dateClicked;
	if (update && cal.sel) {
		var oldValue = cal.sel.value; 
		cal.sel.value = date;
		if (typeof cal.sel.onchange == "function")	{
			if (oldValue != cal.sel.value)
			{	
				cal.sel.onchange();
			}
		}
	}
	if (update && cal.singleClick && cal.dateClicked){
		cal.callCloseHandler();
	}
};

function calendarDateSelected(cal, date) {
	var update = cal.dateClicked;
	if (update && cal.sel && cal.sel.tagName == 'SELECT') {
		var options = cal.sel.options;
		var found = -1;
		for ( var idx = 0; idx < options.length; idx++) {
			if (options[idx].value == date) {
				found = idx;
				break;
			}
		}
		if (found == -1) {
			var opt = document.createElement("OPTION");
			opt.text = date;
			opt.value = date;
			options.add(opt);
		}
	}
	onCalendarSelectDefault(cal, date);
}

function javaFmtToPrintF(format) {
	format = format.replace('yyyy', '%Y');
	format = format.replace('yy', '%y');
	format = format.replace(/(dd|d)/, '%d');
	format = format.replace('MMMM', '%B');
	format = format.replace('MMM', '%b');
	format = format.replace('MM', '%m');
	format = format.replace('M', '%m');
	return format;

}
// And this gets called when the end-user clicks on the _selected_ date,
// or clicks on the "Close" button.  It just hides the calendar without
// destroying it.
function closeCalendarHandler(cal) {
	cal.hide();
}

function clearDate(obj) {
	if (event.keyCode == 46) {
		if (typeof (obj) == 'string') {
			document.getElementById(obj).value = '';
		} else if (obj != null) {
			obj.value = '';
		}
	}
}


function showCalendar(bindTo, format, showsOtherMonths, 
		onCloseHandler, startOfWeek, defaultDate) 
{
	var bindToField = null;
	if (typeof (bindTo) == 'string') {
		bindToField = document.getElementById(bindTo);
	} else {
		bindToField = bindTo;
	}
	if (format == null) {
		format = javaFmtToPrintF(userDateFormat); //'%Y-%m-%d';     
	}
	
	if (calendar != null) {
		calendar.hide(); // so we hide it first.
	} else {
		// first-time call, create the calendar.
		var cal = new Calendar(0, null, calendarDateSelected,
				closeCalendarHandler);
		cal.weekNumbers = false;
		cal.showsOtherMonths = true;
		cal.setRange(1990, 2040); // min/max year allowed.
		cal.hideControls = false;
		cal.singleClick = true;
		cal.create();
		calendar = cal; // remember it in the global var
	}
	if (typeof (onCloseHandler) == 'function')
	{
		calendar.onClose = onCloseHandler;
	}
	if (startOfWeek != null)
	{
		calendar.setFirstDayOfWeek(startOfWeek);
	}
	calendar.setDateFormat(format); // set the specified date format
	calendar.setTtDateFormat(format);
	if (bindToField != null) 
	{
		if (bindToField.value != null && bindToField.value.length > 0)
		{
			calendar.parseDate(bindToField.value); // try to parse the text in field
		}
		else if (defaultDate != null)
		{
			calendar.parseDate(defaultDate);
		}
		calendar.sel = bindToField; // inform it what input field we use
		calendar.refresh();
		calendar.showAtElement(bindToField, "Br"); // show the calendar
	} else {
		calendar.parseDate('');
		calendar.showAt(10,10);		
	}
	return false;
}

function showCalendarPopup(bindTo, defaultDate, onCloseHandler, startOfWeek) 
{
	showCalendar(bindTo, null, null, onCloseHandler, startOfWeek, defaultDate);
}