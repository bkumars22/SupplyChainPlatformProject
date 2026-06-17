/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
/**
 * Author Brian Blasko
 * MIT License
 * 
 *  onmove signature should be function(col,fromIndex,toIndex)
 */
(function($) 
{
	var defaults = {
		placeHolderClass:'ocmColumnPlaceholder',
		headerClass:'ocmColumnHeader',
		headerHoverClass:'ocmColumnHeaderHover',
		disableIfClass:null,
		mouseOverTitle:null,
		saveState: false,
		saveDuration:null,
		stateCookieId:null,
		stateFieldId:null,
		onmove: null
	};
	
	var cookieName = 'columnOrderManagerC';

	$.fn.columnOrderManager = function(options)
	{
		var settings = $.extend({}, defaults, options);
        return this.each(function()
        {
        	setupOrderColumnState(this,settings);
        });    
	};	
	
	$.fn.saveColumnOrder = function()
	{
		return this.each(function()
		{
			saveColumnOrder(this);
		});
	};
	
	$.fn.resetColumnOrder = function(clearSavedState)
	{
		return this.each(function()
		{
			resetColumnOrder(this,clearSavedState);
		});
	};
	
	function getMoveColumnHeader(table)
	{
		if (table == null)
		{
			return null;
		}
		var found = $('thead:first tr:last', table);

		if (found == null || found.length < 1)
		{
			return null;	
		}
		return found[0];	
	};

	function moveTableColumn(table,source,target,bodyOnly)
	{
		var rows = (bodyOnly) ? $('tbody tr', table): $('tr', table);

		if (rows == null)
		{
			return;
		}
		for(rowIdx=0; rowIdx < rows.length; rowIdx++)
		{
			var row = rows[rowIdx];
			var sourceCell = row.removeChild(row.cells[source]);		
			if (target < row.cells.length)
			{
				row.insertBefore(sourceCell,row.cells[target]);			
			}
			else
			{
				row.appendChild(sourceCell);
			}		 
		}
        if (table.cOMSaveState)
        {
        	saveColumnOrder(table);
        }
	};

	function orderColumns(table,colOrder)
	{
		if (colOrder == null || colOrder.length < 1)
		{
			return;
		}
		var row = getMoveColumnHeader(table);	
		for (tidx=0; tidx < colOrder.length; tidx++)
		{	
			var sidx = parseInt(colOrder[tidx]);
			for (cellIdx=0; cellIdx < row.cells.length; cellIdx++)
			{
				if (row.cells[cellIdx].realOrderIndex == sidx)
				{
					sidx = cellIdx;
					break;
				}
			}					
			if (sidx != tidx)
			{
				moveTableColumn(table,sidx,tidx,false);
			}
		}	
	};
	
	var saveColumnOrder = function(table)
	{	
		var row = getMoveColumnHeader(table);
		if (row == null)
		{
			return;
		}
		var order = new Array();
		for (cellIdx=0; cellIdx < row.cells.length; cellIdx++)
		{			
			order.push(row.cells[cellIdx].realOrderIndex);
		}	
		$.cookie(cookieName + table.cOMCookieId, order, {expires: table.cOMSaveDuration});
		if (table.cOMStateFieldId != null)
		{
		   $('#'+table.cOMStateFieldId).val(order);
		}
		
	};

	var resetColumnOrder = function(table,clearSavedState)
	{

		var row = getMoveColumnHeader(table);
		if (row == null)
		{
			return;
		}
		var colOrder = new Array();
		for (ridx=0;ridx < row.cells.length; ridx++)
		{
			colOrder.push(ridx);
		}
		orderColumns(table,colOrder);
		if (clearSavedState)
		{
			$.cookie(cookieName + table.cOMCookieId, '',-1);
			if (table.cOMStateFieldId != null)
			{
			   $('#'+table.cOMStateFieldId).val('');
			}
			
		}				
	};
	
	function setupOrderColumnState(table,settings)
	{	
		table.cOMSaveState = settings.saveState;
		table.cOMSaveDuration = settings.saveDuration;
		table.cOMStateFieldId = settings.stateFieldId;
		table.cOMCookieId = (settings.stateCookieId != null) ? settings.stateCookieId: table.id;
		var row = getMoveColumnHeader(table);
		if (row == null)
		{
			return;
		}
		for (cellIdx=0; cellIdx < row.cells.length; cellIdx++)
		{
			row.cells[cellIdx].realOrderIndex = cellIdx;
		}
		var filter = 'td';
		if (settings.disableIfClass != null)
		{
			filter += ':not(.' + settings.disableIfClass + ')';
		}
		$(row).sortable({items: filter,
			placeholder:settings.placeHolderClass, 
			forcePlaceholderSize:true, opacity:0.6,
			start: function(event,ui){start(event,ui)},
			update: function(event, ui){move(event,ui,settings.onmove)}});	
		$(filter,row).each(function() {
			$(this).addClass(settings.headerClass);
			if (settings.mouseOverTitle)
			{
				$(this).attr('title',settings.mouseOverTitle);
			}
			if (settings.headerHoverClass != null)
			{	
				$(this).hover(
					function () {$(this).addClass(settings.headerHoverClass);}, 
		            function () {$(this).removeClass(settings.headerHoverClass);}
			    );
			}
		});
		var colOrder = getColumnOrderState(table);
		if (colOrder != null)
		{
			orderColumns(table,colOrder);			
		}
	};

	function getColumnOrderState(table)
	{
		var order = null;
		var colOrder = null;
		if (table.cOMStateFieldId != null)
		{
		   order = $('#'+table.cOMStateFieldId).val();		
		   order = $.trim(order);
		}
		if (order == null || order.length == 0)
		{
			order = $.cookie(cookieName + table.cOMCookieId);
			order = $.trim(order);
		}
		if (order != null && order.length > 0)
		{
			colOrder = order.split(',');		
		}		
		return colOrder;
	}
	
	function move(event,ui,callback)
	{
		var index = $(ui.item).parent().children().index(ui.item);
		var col = $(ui.item).closest('table').get(0);
		moveTableColumn(col,ui.item.startPosition,index,true);
		if (jQuery.isFunction(callback))
		{
			callback(col,ui.item.startPosition,index);
		}
	};

	function start(event,ui)
	{
		var index = $(ui.item).parent().children().index(ui.item);
		ui.item.startPosition = index;
	};
	
})(jQuery);