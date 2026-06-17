<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<style type="text/css">
.eto-grid-scroll table tbody tr td:nth-child(2n+1) {
	border-left-width: 1px;
	border-left-color: #28618a;
}

.eto-grid-scroll table tbody tr td:nth-child(2n) {
	border-right-width: 1px;
	border-right-color: #28618a;
}

.eto-grid-scroll table thead tr:first-child th {
	border-left-width: 1px;
	border-left-color: #28618a;
}

.eto-grid-scroll table thead tr:first-child th {
	border-right-width: 1px;
	border-right-color: #28618a;
}

.eto-grid-scroll table thead tr th:nth-child(2n+1) {
	border-left-width: 1px;
	border-left-color: #28618a;
}

.eto-grid-scroll table thead tr th:nth-child(2n) {
	border-right-width: 1px;
	border-right-color: #28618a;
}

.eto-grid-scroll table tbody tr td:nth-child(2n+2).evenPriceTAM0rowPast,td:nth-child(2n+2).evenPriceTAM1rowPast,td:nth-child(2n+2).oddPriceTAM0rowPast,td:nth-child(2n+2).oddPriceTAM1rowPast  {
	border-left-width: medium;
	border-left-color: #ffffff;
}

.evenPriceTAM0row {
	min-height: 24px;
}

.evenPriceTAM0rowPast {
	background-color: #ededed !important;
	min-height: 24px;
}

.oddPriceTAM0row {
	background-color: #cfdee4 !important;
	min-height: 24px;
}

.oddPriceTAM0rowPast {
	background-color: #cdd7db !important;
	min-height: 24px;
}

.evenPriceTAM1row {
	min-height: 24px;
}

.evenPriceTAM1rowPast {
	background-color: #e5e7e9 !important;
	min-height: 24px;
}

.oddPriceTAM1row {
	background-color: #d9e5e9 !important;
	min-height: 24px;
}

.oddPriceTAM1rowPast {
	background-color: #d4dcde !important;
	min-height: 24px;
}
</style>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Manage-PriceTAM" />
</head>
<script>
	var screenName = "PRICE_TAM";
	
	function goAsynchronusSearch(){
		document.forms[0].action = "priceTAMAsychronousUISearch.do";
		document.forms[0].submit();
		showWaitBusy();
		}
	function goBack(formName, action) {
		formName.action = action;
		formName.buttonAction.value = "back";
		formName.submit();
		showWaitBusy();
	}

	function goNext(formName, action) {
		formName.action = action;
		formName.buttonAction.value = "next";
		formName.submit();
		showWaitBusy();
	}

	function init() {
		resizeResultArea();
	}
	
	function getColumnObject(dataField, columnName, frozen, menuActions) {
		var column = {
			"dataField" : dataField,
			"name" : columnName,
			"label" : columnName,
			"collapsed" : false,
			"frozen" : frozen,
			"resizeable" : true,
			"removed" : false
		};
		column.menuActions = menuActions;
		return column;
	}
	
	function getColumnObjectCollapsed(dataField, columnName, frozen, menuActions) {
		var column = {
			"dataField" : dataField,
			"name" : columnName,
			"label" : columnName,
			"collapsed" : true,
			"frozen" : frozen,
			"resizeable" : true,
			"removed" : false
		};
		column.menuActions = menuActions;
		return column;
	}

	var priceTAMColumn = {};
	let colPriceObj;
	let colTAMObj;
	
	$(document).ready(function() {
		$('#expand-container .container').prepend('<div><label id="searchByProjectName" style="margin-left: 3rem;color: #277AB5;font-weight: bolder;" onclick="updateFilter();">Search by Project name</label></div>');
		
		$('#searchByProjectName').html("${empty priceTAMform.searchType ? 'Show default filters' : priceTAMform.searchType}");
		updateFilter();
		
		updateLineColor();
		adjustHeight();
		if($("#monthlySearch").val()=="true"){
			$("select[name='value(periodType)']").val("");
		}else{
			$("select[name='value(periodType)']").val("yes");
		}
	});
	
	function updateFilter(){
		var content = $('#searchByProjectName').html();
		if(content == 'Show default filters'){
			$('#firstRow').children().show();
			$('#firstRow > div:nth-child(1)').hide();
			$('#firstRow > div:nth-child(2)').hide();
			$('#searchByProjectName').html('Search by Project name');
			$('input[name="searchType"]').val('Show default filters');
		}
		if(content == 'Search by Project name'){
			$('#firstRow').children().hide();
			$('#firstRow > div:nth-child(1)').show();
			$('#firstRow > div:nth-child(2)').show();
			$('#firstRow > div:nth-child(17)').show();
			$('#firstRow > div:nth-child(18)').show();
			$('#firstRow > div:nth-child(19)').show();
			$('#firstRow > div:nth-child(20)').show();
			$('#searchByProjectName').html('Show default filters');
			$('input[name="searchType"]').val('Search by Project name');
		}
	}
	
	function gridUpdateCallBack(){
		updateLineColor();
		adjustHeight();
	}
	
	function updateLineColor(){
		$('div.eto-grid-frozen').find('div.evenPriceTAM0row').parent().parent().find('td').addClass('evenPriceTAM0row');
		$('div.eto-grid-frozen').find('div.evenPriceTAM0rowPast').parent().parent().find('td').addClass('evenPriceTAM0rowPast');
		$('div.eto-grid-frozen').find('div.oddPriceTAM0row').parent().parent().find('td').addClass('oddPriceTAM0row');
		$('div.eto-grid-frozen').find('div.oddPriceTAM0rowPast').parent().parent().find('td').addClass('oddPriceTAM0rowPast');
		$('div.eto-grid-frozen').find('div.evenPriceTAM1row').parent().parent().find('td').addClass('evenPriceTAM1row');
		$('div.eto-grid-frozen').find('div.evenPriceTAM1rowPast').parent().parent().find('td').addClass('evenPriceTAM1rowPast');
		$('div.eto-grid-frozen').find('div.oddPriceTAM1row').parent().parent().find('td').addClass('oddPriceTAM1row');
		$('div.eto-grid-frozen').find('div.oddPriceTAM1rowPast').parent().parent().find('td').addClass('oddPriceTAM1rowPast');
		
		$('div.eto-grid-scroll').find('div.evenPriceTAM0row').parent().addClass('evenPriceTAM0row');
		$('div.eto-grid-scroll').find('div.evenPriceTAM0rowPast').parent().addClass('evenPriceTAM0rowPast');
		$('div.eto-grid-scroll').find('div.oddPriceTAM0row').parent().addClass('oddPriceTAM0row');
		$('div.eto-grid-scroll').find('div.oddPriceTAM0rowPast').parent().addClass('oddPriceTAM0rowPast');
		$('div.eto-grid-scroll').find('div.evenPriceTAM1row').parent().addClass('evenPriceTAM1row');
		$('div.eto-grid-scroll').find('div.evenPriceTAM1rowPast').parent().addClass('evenPriceTAM1rowPast');
		$('div.eto-grid-scroll').find('div.oddPriceTAM1row').parent().addClass('oddPriceTAM1row');
		$('div.eto-grid-scroll').find('div.oddPriceTAM1rowPast').parent().addClass('oddPriceTAM1rowPast');
		
		$('.priceVariance').parent().attr('data-message-type','warn');
	}
	
	
	function onGridViewUpdateCallBack(){
		adjustHeight();
	}
	
	function adjustHeight(){
		var rightSideFirstHeight = $('div.eto-grid-frozen > table thead > tr > th:first-child > div').height();
		var rightSideSecondHeight = $('div.eto-grid-frozen > table thead > tr:nth-child(2) > th:first-child > div').height();
		
		$('div.eto-grid-scroll > table thead > tr > th:first-child > div').height(rightSideFirstHeight);
		$('div.eto-grid-scroll > table thead > tr:nth-child(2) > th:first-child > div').height(rightSideSecondHeight);
	}
</script>
<style type="text/css">
</style>
<c:set var='rowColorKey' value='${e2ofn:getConfigValue("priceTAM.ui.rowSeparatorKey")}' />
<body onload="init()" style="overflow-y : scroll">
	<e2o:form action="priceTAMSubmitUISearch.do" method="POST">
	<input type="hidden" name="monthlySearch" value="${priceTAMform.monthlySearch}" id="monthlySearch">
		<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
			<div style="font-weight: bold;">
				<logic:messagesPresent message="true">
					<html:messages id="message" message="true">
						<li>${message}</li>
					</html:messages>
				</logic:messagesPresent>
			</div>
		</div>
		<script>
			var jsonColumn =  '${priceTAMform.columns}';
			var gridColumns = [];
			var selectionType = 'none';
			
			var periodScrollHeader = [];
			
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

			var actionRemove = {
					id : 'remove',
					label : 'Remove',
					icon : 'remove_circle_outline'
				};

			var actionUnFreeze = {
					id : 'unfreeze',
					label : 'Unfreeze',
					icon : 'ac_unit'
				};
			
			var menuActions = [ actionCollapse, actionRemove ];
			var frozenColumns = [];
			var firstHeaderFrozen = getColumnObject(
					'<fmt:message key="priceTAM.ui.header.itemNumber"/>',
					'<fmt:message key="priceTAM.ui.header.itemNumber"/>',
					true, menuActions);
			var secondHeaderFrozen = [];
			secondHeaderFrozen
					.push(getColumnObject('<fmt:message key="priceTAM.ui.header.itemNumber"/>','&nbsp;',true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject(
					'<fmt:message key="priceTAM.ui.header.item.businessEntity" />',
					'<fmt:message key="priceTAM.ui.header.item.businessEntity" />',
					true, menuActions);
			secondHeaderFrozen
					.push(getColumnObject('<fmt:message key="priceTAM.ui.header.item.businessEntity" />','&nbsp;',true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObjectCollapsed('<fmt:message key="priceTAM.ui.header.item.businessEntity.type" />',
					'<fmt:message key="priceTAM.ui.header.item.businessEntity.type" />', true, menuActions);
			secondHeaderFrozen
					.push(getColumnObjectCollapsed(
							'<fmt:message key="priceTAM.ui.header.item.businessEntity.type" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject('<fmt:message key="priceTAM.ui.header.mpn" />',
					'<fmt:message key="priceTAM.ui.header.mpn" />',true, menuActions);
			secondHeaderFrozen.push(getColumnObject(
					'<fmt:message key="priceTAM.ui.header.mpn" />',
					'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject('<fmt:message key="priceTAM.ui.header.supplier" />',
					'<fmt:message key="priceTAM.ui.header.supplier" />',true, menuActions);
			secondHeaderFrozen.push(getColumnObject(
					'<fmt:message key="priceTAM.ui.header.supplier" />',
					'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject('<fmt:message key="priceTAM.ui.header.functionalGroup.name" />',
					'<fmt:message key="priceTAM.ui.header.functionalGroup.name" />',true, menuActions);
			secondHeaderFrozen
					.push(getColumnObject(
							'<fmt:message key="priceTAM.ui.header.functionalGroup.name" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObjectCollapsed('<fmt:message key="priceTAM.ui.header.functionalGroup.type" />',
					'<fmt:message key="priceTAM.ui.header.functionalGroup.type" />',true, menuActions);
			secondHeaderFrozen
					.push(getColumnObjectCollapsed(
							'<fmt:message key="priceTAM.ui.header.functionalGroup.type" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObjectCollapsed('<fmt:message key="priceTAM.ui.header.functionalGroup.platform" />',
					'<fmt:message key="priceTAM.ui.header.functionalGroup.platform" />',true, menuActions);
			secondHeaderFrozen
					.push(getColumnObjectCollapsed(
							'<fmt:message key="priceTAM.ui.header.functionalGroup.platform" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject('<fmt:message key="priceTAM.ui.header.cost.type" />',
					'<fmt:message key="priceTAM.ui.header.cost.type" />',true, menuActions);
			secondHeaderFrozen
					.push(getColumnObject(
							'<fmt:message key="priceTAM.ui.header.cost.type" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject('<fmt:message key="priceTAM.ui.header.sl.destination" />',
					'<fmt:message key="priceTAM.ui.header.sl.destination" />',true, menuActions);
			secondHeaderFrozen.push(getColumnObject(
					'<fmt:message key="priceTAM.ui.header.sl.destination" />',
					'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObject('<fmt:message key="priceTAM.ui.header.xlob.site" />',
					'<fmt:message key="priceTAM.ui.header.xlob.site" />',true, menuActions);
			secondHeaderFrozen
					.push(getColumnObject(
							'<fmt:message key="priceTAM.ui.header.xlob.site" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			firstHeaderFrozen = getColumnObjectCollapsed('<fmt:message key="priceTAM.ui.header.priceEffectiveOffsset" />',
					'<fmt:message key="priceTAM.ui.header.priceEffectiveOffsset" />',true, menuActions);
			secondHeaderFrozen
					.push(getColumnObjectCollapsed(
							'<fmt:message key="priceTAM.ui.header.priceEffectiveOffsset" />',
							'&nbsp;', true, null));
			firstHeaderFrozen.columns = secondHeaderFrozen;
			gridColumns.push(firstHeaderFrozen);
			frozenColumns.push(firstHeaderFrozen);
			secondHeaderFrozen = [];
			
			var menuActions1 = [];
			<c:forEach var="period" items="${priceTAMform.timelineForFiscal}">
				<fmt:formatDate value="${period.fiscalPeriodStartDate}" pattern="${priceTAMform.headerDateFormat}" var="formatedDateValue"/>
					var fiscalHeaderScroll = getColumnObject('${formatedDateValue}','${period.fiscalPeriodName}&nbsp;${formatedDateValue}',false, menuActions1);
				
					var secondHeaderScroll = [];
					
					secondHeaderScroll.push(getColumnObject('${period.fiscalPeriodStartDate}Price','Price', false, null));
					secondHeaderScroll.push(getColumnObject('${period.fiscalPeriodStartDate}TAM','TAM',false, null));
					
					fiscalHeaderScroll.columns = secondHeaderScroll;
					
					periodScrollHeader.push(fiscalHeaderScroll);
			</c:forEach>
			
			var timeLineColumns = [];
			$.each(periodScrollHeader, function( index, value ) {
				gridColumns.push(value);
				timeLineColumns.push(value);
			});
			
			var gridRows = [];
			
			var prevTAM = '';
			var currentTAM = '';
			var classToApply = 'oddPriceTAM';
			var prevRowCount = '';
			
			<c:forEach var="row" items="${priceTAMform.searchResult.values}" varStatus="rowCount">
			<c:set var="fgId" value="${row.values[0]}" />
			<c:set var="fgName" value="${row.values[1]}" />
			<c:set var="fgType" value="${row.values[2]}" />
			<c:set var="fgPlatform" value="${row.values[3]}" />
			<c:set var="itemKey" value="${row.values[4]}" />
			<c:set var="itemIdentifier" value="${row.values[5]}" />
			<c:set var="itemType" value="${row.values[6]}" />
			<c:set var="itemBusinessEntity" value="${row.values[7]}" />
			<c:set var="itemBusinessEntityType" value="${row.values[8]}" />
			<c:set var="supplierKey" value="${row.values[9]}" />
			<c:set var="suppluerName" value="${row.values[10]}" />
			<c:set var="toSiteKey" value="${row.values[11]}" />
			<c:set var="toSiteDescription" value="${row.values[12]}" />
			<c:set var="sourcingLaneKey" value="${row.values[13]}" />
			<c:set var="fromSiteKey" value="${row.values[14]}" />
			<c:set var="fromSiteDescription" value="${row.values[15]}" />
			<c:set var="costTypeName" value="${row.values[16]}" />
			<c:set var="costTypeKey" value="${row.values[17]}" />
			<c:set var="mpn" value="${row.values[18]}" />
			<c:set var="tamSiteKey" value="${row.values[19]}" />
			<c:set var="tamSiteDescription" value="${row.values[20]}" />
			<c:set var="itemDataSource" value="${row.values[21]}" />
			<c:set var="itemCategoryName" value="${row.values[22]}" />
			
			var fgId_var = '${fgId}';
			var fgName_var = '${fgName}';
			var fgType_var = '${fgType}';
			var fgPlatform_var = '${fgPlatform}';
			var itemKey_var = '${itemKey}';
			var itemIdentifier_var = '${itemIdentifier}';
			var itemType_var = '${itemType}';
			var itemBusinessEntity_var = '${itemBusinessEntity}';
			var itemBusinessEntityType_var = '${itemBusinessEntityType}';
			var supplierKey_var = '${supplierKey}';
			var suppluerName_var = '${suppluerName}';
			var toSiteKey_var = '${toSiteKey}';
			var toSiteDescription_var = '${toSiteDescription}';
			var sourcingLaneKey_var = '${sourcingLaneKey}';
			var fromSiteKey_var = '${fromSiteKey}';
			var fromSiteDescription_var = '${fromSiteDescription}';
			var costTypeName_var = '${costTypeName}';
			var mpn_var = '${mpn}';
			var tamSiteKey_var = '${tamSiteKey}';
			var tamSiteDescription_var ='${tamSiteDescription}';
			var itemDataSource_var = '${itemDataSource}';
			var itemCategoryName_var = '${itemCategoryName}';
			
			currentTAM = ${rowColorKey};
			
			if(prevTAM != currentTAM){
				if(classToApply.startsWith('oddPriceTAM')){
					classToApply = 'evenPriceTAM';
				}else{
					classToApply = 'oddPriceTAM';
				}
			}
			
			if(!classToApply.endsWith('row')){
				classToApply = classToApply + '${rowCount.index % 2}row';	
			}else{
				classToApply = classToApply.replace(prevRowCount,'${rowCount.index % 2}');
			}
			
			prevTAM = currentTAM;
			prevRowCount = '${rowCount.index % 2}';
			
			var row = {};
			var itemNumberLink = '<a href="#" onClick="openPopOver(\'${itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${itemIdentifier}" removeColon="true"/></a>';
			row['<fmt:message key="priceTAM.ui.header.itemNumber"/>'] = '<div class="'+classToApply+'">'+itemNumberLink+'</div>';
			row['<fmt:message key="priceTAM.ui.header.item.businessEntity" />'] = '<div class="'+classToApply+'">'+<e2ofn:escapePrint value="${itemBusinessEntity}"/>+'</div>';
			row['<fmt:message key="priceTAM.ui.header.item.businessEntity.type" />'] = '<div class="'+classToApply+'">'+'${itemBusinessEntityType}'+'</div>';
			row['<fmt:message key="priceTAM.ui.header.mpn" />'] = '<div class="'+classToApply+'">'+<e2ofn:escapePrint value="${mpn}"/>+'</div>';
			row['<fmt:message key="priceTAM.ui.header.supplier" />'] = '<div class="'+classToApply+'">'+<e2ofn:escapePrint value="${suppluerName}"/>+'</div>';
			row['<fmt:message key="priceTAM.ui.header.functionalGroup.name" />'] = '<div class="'+classToApply+'">'+<e2ofn:escapePrint value="${fgName}"/>+'</div>';
			row['<fmt:message key="priceTAM.ui.header.functionalGroup.type" />'] = '<div class="'+classToApply+'">'+'${fgType}'+'</div>';
			row['<fmt:message key="priceTAM.ui.header.functionalGroup.platform" />'] = '<div class="'+classToApply+'">'+<e2ofn:escapePrint value="${fgPlatform}"/>+'</div>';
			row['<fmt:message key="priceTAM.ui.header.cost.type" />'] = '<div class="'+classToApply+'">'+'${costTypeName}'+'</div>';
			row['<fmt:message key="priceTAM.ui.header.sl.destination" />'] = '<div class="'+classToApply+'">'+'${toSiteDescription}'+'</div>';
			row['<fmt:message key="priceTAM.ui.header.xlob.site" />'] = '<div class="'+classToApply+'">'+'${tamSiteDescription}'+'</div>';

			<c:if test="${empty tamSiteKey && empty sourcingLaneKey}">
				row['<fmt:message key="priceTAM.ui.header.priceEffectiveOffsset" />'] = '<div class="'+classToApply+'"></div>';
				
				<c:forEach var="period" items="${priceTAMform.timelineForFiscal}" varStatus="count">
					var newClass = classToApply+'${period.fiscalPeriodStartDate < priceTAMform.currentFiscalPeriodStartDate ? 'Past' : ''}';
					row['${period.fiscalPeriodStartDate}Price'] = '<div class="'+newClass+'"></div>';
					row['${period.fiscalPeriodStartDate}TAM'] = '<div class="'+newClass+'"></div>';
				</c:forEach>
			</c:if>
			
			<c:if test="${!empty tamSiteKey || !empty sourcingLaneKey}">
				
				<c:set var="priceOffset" value="${e2ofn:getPriceOffset(priceTAMform,itemDataSource,costTypeKey,itemCategoryName,itemBusinessEntity)}"/>				
				
				row['<fmt:message key="priceTAM.ui.header.priceEffectiveOffsset" />'] = '<div class="'+classToApply+'">'+'${empty priceOffset ? '' : priceOffset}'+'</div>';
				
				<c:set var="periodPriceData" value="${e2ofn:getPriceHorizon(priceTAMform,itemKey,mpn,supplierKey,costTypeKey,fgId,fromSiteKey,toSiteKey,tamSiteKey,priceOffset)}"/>
				<c:set var="periodTAMData" value="${e2ofn:getTAMHorizon(priceTAMform,itemKey,mpn,supplierKey,costTypeKey,fgId,fromSiteKey,toSiteKey,tamSiteKey,priceOffset)}"/>
				
				<c:forEach var="period" items="${priceTAMform.timelineForFiscal}" varStatus="count">
					var newClass = classToApply+'${period.fiscalPeriodStartDate < priceTAMform.currentFiscalPeriodStartDate ? 'Past' : ''}';
					<c:if test="${not empty periodPriceData[period.fiscalPeriodStartDate]}">
						<c:if test="${periodPriceData[period.fiscalPeriodStartDate].price != null}">
							<c:if test="${periodPriceData[period.fiscalPeriodStartDate].isPriceVariance}">
								row['${period.fiscalPeriodStartDate}Price'] = '<div class="'+newClass+' priceVariance">'+${periodPriceData[period.fiscalPeriodStartDate].price}+'</div>';
							</c:if>
							<c:if test="${!periodPriceData[period.fiscalPeriodStartDate].isPriceVariance}">
								row['${period.fiscalPeriodStartDate}Price'] = '<div class="'+newClass+'">'+${periodPriceData[period.fiscalPeriodStartDate].price}+'</div>';
							</c:if>
						</c:if>
						<c:if test="${periodPriceData[period.fiscalPeriodStartDate].price == null}">
							row['${period.fiscalPeriodStartDate}Price'] = '<div class="'+newClass+'"></div>';
						</c:if>
					</c:if>
					<c:if test="${not empty periodTAMData[period.fiscalPeriodStartDate]}">
						<c:if test="${periodTAMData[period.fiscalPeriodStartDate].allocation != null}">
							<c:if test="${periodTAMData[period.fiscalPeriodStartDate].isAllocationVariance}">
								row['${period.fiscalPeriodStartDate}TAM'] = '<div class="'+newClass+' priceVariance">'+${periodTAMData[period.fiscalPeriodStartDate].allocation}+'</div>';
							</c:if>
							<c:if test="${!periodTAMData[period.fiscalPeriodStartDate].isAllocationVariance}">
								row['${period.fiscalPeriodStartDate}TAM'] = '<div class="'+newClass+'">'+${periodTAMData[period.fiscalPeriodStartDate].allocation}+'</div>';
							</c:if>
						</c:if>
						<c:if test="${periodTAMData[period.fiscalPeriodStartDate].allocation == null}">
							row['${period.fiscalPeriodStartDate}TAM'] = '<div class="'+newClass+'"></div>';
						</c:if>
					</c:if>
					
					<c:if test="${empty periodPriceData[period.fiscalPeriodStartDate]}">
						row['${period.fiscalPeriodStartDate}Price'] = '<div class="'+newClass+'"></div>';
					</c:if>
					<c:if test="${empty periodTAMData[period.fiscalPeriodStartDate]}">
						row['${period.fiscalPeriodStartDate}TAM'] = '<div class="'+newClass+'"></div>';
					</c:if>
				</c:forEach>
			</c:if>
			
			gridRows.push(row);
			</c:forEach>
		</script>
		<input type="hidden" name="backAction" />
		<input type="hidden" name="searchType" />
		<fmt:message key='managePriceTAM.page.header' var="headerMessage"/>
		<e2ot:searchContainerControl
			searchFields="${priceTAMform.allParameters}" formName="priceTAMform"
			form="${priceTAMform}" resultTableId="priceTAMformResultTable"
			showFilterCollapsed="${priceTAMform.filterAreaCollapsed}"
			showFilter="${priceTAMform.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${priceTAMform}"
			formName="priceTAMform" resultTableId="priceTAMformResultTable"
			title="${headerMessage}" />
	</e2o:form>
	
	<script type="text/javascript">
	$(document).ready(function() {
		$("div#expand-container").addClass("eto-expand--expanded");
	});

	parent.parent.reportCall = function() {
		<c:url var="linkHref" value="initReports.do">
		<c:param name="reportType" value="PriceTam" />
		</c:url>
		reloadBreadCrumb('initReports.do');
		document.forms[0].action = "${linkHref}";
		document.forms[0].submit();
		showWaitBusy();
	}
</script>
</body>
</html>