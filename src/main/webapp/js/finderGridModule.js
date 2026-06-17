/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
var el = document.querySelector('#grid-result');
var itemGridTemplate = {};
var gridRemovedColumn = [];
itemGridTemplate.rowSelection = {
	type : rowFinderSelectionType,
	name : "selectedPageKeys",
	valueField : "checkboxValue"
};

if (!(typeof selectionType == 'undefined')) {
	itemGridTemplate.rowSelection.type = selectionType;
}

itemGridTemplate.filterRow = false;
itemGridTemplate.rowExposedActions = [];

if ((!(typeof gridRowExposedAction == 'undefined')) && (gridRowExposedAction)) {
	itemGridTemplate.rowExposedActions = [ {
		label : 'Edit',
		icon : 'mode_edit',
		id : 'edit'
	} ];
}

itemGridTemplate.rowNumbers = false;

var actionCollapse = {
	id : 'collapse',
	label : 'Collapse',
	icon : 'vertical_align_bottom',
	iconClassName : 'rotate-90'
};

var actionFreeze = {
	id : 'freeze',
	label : 'Freeze',
	icon : 'ac_unit'
};

var actionUnFreeze = {
	id : 'unfreeze',
	label : 'Unfreeze',
	icon : 'ac_unit'
};

if (!(typeof jsonColumn === 'undefined') && jsonColumn != "") {
	gridRemovedColumn = [];
	itemGridTemplate.columns = "";
	itemGridTemplate.columns = JSON.parse(jsonColumn);
	// if the screen is PRICE TAM then append the remaining timeline part
	if (typeof screenName != 'undefined' && screenName == "PRICE_TAM") {
		if (typeof timeLineColumns != 'undefined' && timeLineColumns instanceof Array) {
			$.each(timeLineColumns, function( index, value ) {
				itemGridTemplate.columns.push(value);
			});
		}
	}
	
	$.each(itemGridTemplate.columns, function (index, value) {
		if(value.removed)
			gridRemovedColumn.push(value);
    });
	
	itemGridTemplate.columns = itemGridTemplate.columns.filter(function(col) {
		return !gridRemovedColumn.includes(col);
	});
	// console.log('at the end columns are ' + JSON.stringify(itemGridTemplate.columns));
} else {
	gridRemovedColumn = [];
	itemGridTemplate.columns = [];
	for (var i = 0; i < gridColumns.length; i++) {
		var expandFlag = false;
		var colName = gridColumns[i];
		if ('string' == typeof colName) {
			if (colName.indexOf("_EXPANDCELL") > 0) {
				var arr = colName.split("_");
				colName = arr[0];
				expandFlag = true;
			}

			var column = {
				"dataField" : colName,
				"name" : colName,
				"label" : colName,
				"collapsed" : false,
				"frozen" : false,
				"resizeable" : true,
				"removed" : false,
				"menuActions" : [ actionFreeze, actionCollapse]
			};
			if (expandFlag) {
				column.expandable = true;
				column.expandableLines = 1;
			}
			itemGridTemplate.columns.push(column);
		} else if ('object' == typeof colName) {
			if(typeof colName.menuActions == 'undefined')
					colName.menuActions = [ actionFreeze, actionCollapse];
			
			if(colName.columns) {
				$.each(colName.columns, function( index, value ) {
					if (typeof value.menuActions == 'undefined')
							value.menuActions = [ actionFreeze, actionCollapse];
				});
			}
			
			itemGridTemplate.columns.push(colName);
		}
	}
}

if (typeof rowMenuActions === 'undefined') {
	itemGridTemplate.rowMenuActions = [];
} else if (rowMenuActions != "") {
	itemGridTemplate.rowMenuActions = rowMenuActions;
} else {
	itemGridTemplate.rowMenuActions = [];
}

itemGridTemplate.rows = gridRows;
var grid = null;
createNewGridHandleEvents();

function ColumnAction(action, columnName, element) {
	var columns = itemGridTemplate.columns;
	var selectedColumn = null;
	columnName = columnName.trim().replace(/\s/g,'');
	columnName = columnName.replace('&nbsp;','');
	if(columnName.length > 0) {
	for (var i=0; i<columns.length; i++) {
		var column = columns[i];
		var colName = column.name.trim().replace(/\s/g,'');
		colName = colName.replace('&nbsp;','');
		if (colName == columnName) {
			selectedColumn = column;
			break;
		} else if (column.columns instanceof Array) {
			var subColumns = column.columns;
			for (var j=0; j<subColumns.length; j++) {
				var subColumn = subColumns[j];
				var subColName = subColumn.name.trim().replace(/\s/g,'');
				subColName = subColName.replace('&nbsp;','');
				if (subColName == columnName) {
					selectedColumn = subColumn;
					break;
				}
			}
		}
		if (selectedColumn != null && 'object' == typeof selectedColumn) {
			break;
		}
	}
	}
	if(selectedColumn != null) {
	if (action == 'freeze') {
		selectedColumn.frozen = true;
		selectedColumn.menuActions = [ actionUnFreeze, actionCollapse ];
	} else if (action == 'unfreeze') {
		selectedColumn.frozen = false;
		selectedColumn.menuActions = [ actionFreeze, actionCollapse ];
	} else if (action == 'collapse') {
		selectedColumn.collapsed = true;
		selectedColumn.width = -1;
	} else if (action == 'expand') {
		const tooltips = document.getElementsByClassName("eto-tooltip");
		Array.prototype.forEach.call(tooltips, function(tooltip) {
			tooltip.style.visibility = 'hidden';
		});
		selectedColumn.collapsed = false;
	} else if (action == 'remove') {
		const newTemplateColumns = itemGridTemplate.columns
				.filter(function(col) {
					var colName = col.name.trim().replace(/\s/g,'');
					colName = colName.replace('&nbsp;','');
					if (colName == columnName) {
						col.removed = true;
						gridRemovedColumn.push(col);
					} else if (col.columns instanceof Array) {
						var subColumns = col.columns;
						for (var i=0; i<subColumns.length; i++) {
							var subColumn = subColumns[i];
							var subColName = subColumn.name.trim().replace(/\s/g,'');
							subColName = subColName.replace('&nbsp;','');
							if (subColName == columnName) {
								subColumn.removed = true;
								gridRemovedColumn.push(subColumn);
								subColumns.splice(i,1);
								break;
							}
						}
					}
					return (colName !== columnName);
				});
		itemGridTemplate.columns = newTemplateColumns;
	}
	
	// if selectedColumn is the parent column then all the child columns under it should be in the same state
	updateChildColumns(selectedColumn, action);

	createNewGridHandleEvents();
	}
	checkCondensed(grid);
}

function updateChildColumns(selectedColumn, action) {
	// for remove action already the columns would be deleted so except that
	// action update for all other actions
	if (action !== 'remove') {
		if (action == 'freeze') {
			if (selectedColumn.columns instanceof Array) {
				var subColumns = selectedColumn.columns;
				for (var i = 0; i < subColumns.length; i++) {
					var subColumn = subColumns[i];
					subColumn.frozen = true;
					subColumn.menuActions = [ actionUnFreeze, actionCollapse ];
				}
			}
		} else if (action == 'unfreeze') {
			if (selectedColumn.columns instanceof Array) {
				var subColumns = selectedColumn.columns;
				for (var i = 0; i < subColumns.length; i++) {
					var subColumn = subColumns[i];
					subColumn.frozen = false;
					subColumn.menuActions = [ actionFreeze, actionCollapse ];
				}
			}
		} else if (action == 'collapse') {
			if (selectedColumn.columns instanceof Array) {
				var subColumns = selectedColumn.columns;
				for (var i = 0; i < subColumns.length; i++) {
					var subColumn = subColumns[i];
					subColumn.collapsed = true;
					subColumn.width = -1;
				}
			}
		} else if (action == 'expand') {
			if (selectedColumn.columns instanceof Array) {
				var subColumns = selectedColumn.columns;
				for (var i = 0; i < subColumns.length; i++) {
					var subColumn = subColumns[i];
					subColumn.collapsed = false;
				}
			}
		}
	}
}

function handleGridState(selectedPageKeys) {
	for (var i = 0; i < selectedPageKeys.length; i++) {
		$('#grid-result :input[value="' + selectedPageKeys[i] + '"]').trigger(
		'click');
	}
}

function handleResizeGrid() {
	$("#grid-result .eto-grid .eto-grid-scroll table").css('width', '100%');
	handleGridCells();
}

function checkCondensed(grid){
	if ($("#condensedView").val() == "true") {
		grid.el.classList.add('eto-grid--compact');
		grid.alignRows();
	}
	if ($("#condensedView").val() == "false") {
		grid.el.classList.remove('eto-grid--compact');
		grid.alignRows();
	}
	$("#grid-result .eto-grid .eto-grid-scroll table").css('width', '100%');
}
function handleGridCells() {
	$(".cellMessage").each(function() {
		var messageType = "";
		var text = $(this).text();
		if (text == 'APPROVED')
			messageType = "success";
		else if (text == 'CLOSED')
			messageType = "error";
		else if (text == 'PENDING')
			messageType = "warn";
		$(this).parent().parent().attr('data-message-type', messageType);
	});
}

function getGridState(selectedPageKeys) {
	$('#grid-result :input[name="selectedPageKeys"]').each(function() {
		if ($(this).attr('checked'))
			selectedPageKeys.push($(this).val());
	});
}

function setGridState(selectedPageKeys) {
	if (selectedPageKeys.length > 0) {
		handleGridState(selectedPageKeys);
	}
}

function createNewGridHandleEvents() {
	if (el != null && typeof el !== 'undefined') {
		var selectedPageKeys = [];
		getGridState(selectedPageKeys);
		// console.log('before forming the grid, columns are ' + JSON.stringify(itemGridTemplate.columns));
		el.innerHTML = eto.GridTemplate(itemGridTemplate);
		grid = new eto.Grid({
			el : document.querySelector('#grid-result .eto-grid'),
		});
		grid.on("columnResize", function(column, width) {
			$("#grid-result .eto-grid .eto-grid-scroll table").css('width', '100%');
			var columnName = $(column).parent().attr('data-column').trim().replace(/\s/g,'');
			var columns = itemGridTemplate.columns;
			var selectedColumn = null;
			for (var i=0; i<columns.length; i++) {
				var column = columns[i];
				var colName = column.name.trim().replace(/\s/g,'');
				colName = colName.replace('&nbsp;','');
				if (colName == columnName) {
					selectedColumn = column;
					break;
				} else if (column.columns instanceof Array) {
					var subColumns = column.columns;
					for (var j=0; j<subColumns.length; j++) {
						var subColumn = subColumns[j];
						var subColName = subColumn.name.trim().replace(/\s/g,'');
						subColName = subColName.replace('&nbsp;','');
						if (subColName == columnName) {
							selectedColumn = subColumn;
							break;
						}
					}
				}
				if (selectedColumn != null && 'object' == typeof selectedColumn) {
					break;
				}
			}
			selectedColumn.width = width;
			handleGridScrolls();
			grid.alignRows();
		});
		/*
		 * grid.on("columnReorder", function(draggingElement, dropTargetElement,
		 * orientation) { var columns = itemGridTemplate.columns; var
		 * sourceColumn = $(draggingElement).attr('data-column'); var
		 * targetColumn = $(dropTargetElement).attr('data-column'); var
		 * sourceIndex = 0; var targetIndex = 0; for (var i = 0; i <
		 * columns.length; i++) { var column = columns[i]; if (column.name ==
		 * sourceColumn) { sourceIndex = i; } if (column.name == targetColumn) {
		 * targetIndex = i; } } var targetColumnObj = columns[targetIndex];
		 * columns.splice(targetIndex, 1, columns[sourceIndex]);
		 * columns.splice(sourceIndex, 1, targetColumnObj);
		 * createNewGridHandleEvents(); });
		 */
		grid.on("columnAction", ColumnAction);
		try {
			setGridEvents(grid);
		} catch (err) {
			// do nothing
		}
		checkCondensed(grid);
		handleResizeGrid();
		setGridState(selectedPageKeys);
		
		// if the content of tooltip is empty then just hide the icon
		$('.eto-grid-column--collapsed .eto-tooltip .eto-tooltip__content').each(function(){
			if($(this).html() == '&nbsp;')
				$(this).parent().parent().css('display','none');
		});
		
		if (typeof gridUpdateCallBack == 'function') { 
			gridUpdateCallBack(); 
		}
		gridExpandCallBack();
		grid.alignRows();
	}
}
function gridExpandCallBack(){
	if($('div.eto-grid-expand__container').length!=0){
	    $('.eto-grid-expand__content').each(function(){
	        if($(this).html().length != 0){
	            $(this).closest( "td").removeClass("eto-grid-expand--expanded");
	        }
	    });
	    }
	    }