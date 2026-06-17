<%@ include file="common.jspf"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@ page import="java.util.ArrayList"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache enabled="false" />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Forecast-Search" />
</head>
<script>

function callOnChange(field){
	if(field.name=='value(owner)'){
		if(field.value=='REGIONAL'){
			document.getElementsByName("values(regions)")[0].disabled = false;
		}else{
			hideElement();
		}
	}
}

function hideElement(){
	if(document.getElementsByName("value(owner)")[0].value!='REGIONAL'){
		document.getElementsByName("values(regions)")[0].disabled = true;
	}
}


function goBack(formName,action)
{
    formName.action=action;
    formName.buttonAction.value="back";
    formName.submit();
    showBusy();
}

function goNext(formName,action)
{

	    if ($('input:checked[name="selectedPageKeys"]').length == 0)
		{
			showOkMessageBox('OK','WARN',"<fmt:message key='warn.nothingSelected'/>","<fmt:message key='msg.warn'/>",null);
			return;
		}
    formName.action=action;
    formName.buttonAction.value="next";
    formName.backAction.value="submitForecastSearch";
    formName.submit();
    showBusy();
}

function init()
{
	hideElement();
	resizeResultArea();
	setupHideColumnState('forecastSearchResultTable',true);
	setupOrderColumnState('forecastSearchResultTable',true,
			{mouseOverTitle:'<fmt:message key="label.moveColumn"/>'});

}

function submitForecastExtract(extractType)
{
	if(extractType == 'CURRENT')
	{
		document.forms[0].extractWriterClass.value = "com.scplatform.pcm.web.action.forecast.CurrentForecastExtractSSWriter";
		document.forms[0].extractFileName.value = '<fmt:message key="fc.search.download.filename.current"/>';
	}
	if(extractType == 'ADJUSTABLE')
	{
		document.forms[0].extractWriterClass.value = "com.scplatform.pcm.web.action.forecast.AdjustableForecastExtractSSWriter";
		document.forms[0].extractFileName.value = '<fmt:message key="fc.search.download.filename.adjustable"/>';
	}
	submitExtractToFile();
}

function goAsynchronusSearch(extractType)
{
	if(extractType == 'CURRENT')
	{
		$("#reportType").val("Simple_Forecast");
		document.forms[0].extractWriterClass.value = "com.scplatform.pcm.web.action.forecast.CurrentForecastExtractSSWriter";
		document.forms[0].extractFileName.value = '<fmt:message key="fc.search.download.filename.current"/>';
	}
	if(extractType == 'ADJUSTABLE')
	{
		$("#reportType").val("System_Generated_Forecast");
		document.forms[0].extractWriterClass.value = "com.scplatform.pcm.web.action.forecast.AdjustableForecastExtractSSWriter";
		document.forms[0].extractFileName.value = '<fmt:message key="fc.search.download.filename.adjustable"/>';
	}
	document.forms[0].action = "asynhronusSearchForForecast";
	document.forms[0].submit();
	showWaitBusy();
}
</script>

<body onload="init()">
	<%
		ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(
				FlexAttributeManager.COSTFORECAST.getFlexAttributeDefinitionList());
		pageContext.setAttribute("flexAttributeDefnListForecast", flexAttributeDefns);
	%>
	<form action="submitForecastSearch" method="POST" name="forecastSearchForm" id="forecastSearchForm">
		<input type="hidden" name="reportType" id="reportType" value="${forecastSearchForm.reportType}">
		<input type="hidden" name="backAction" id="backAction"/>
		<input type="hidden" name="extractWriterClass" />
		<input type="hidden" name="extractFileName" />
		<input type="hidden" name="buttonAction" id="buttonAction"/>
			<div style="font-weight: bold; white-space: pre-line;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					<li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
		<script type="text/javascript">
var gridColumns = [];
var jsonColumn =  '${forecastSearchForm.columns}';
gridColumns.push('<fmt:message key="item.itemNumber"/>');
gridColumns.push('<fmt:message key="item.itemDescription"/>');
gridColumns.push('<fmt:message key="fc.site"/>');
gridColumns.push('<fmt:message key="fc.functionalGroups"/>');
gridColumns.push('<fmt:message key="functionalGroup.parentName"/>');
gridColumns.push('<fmt:message key="item.categoryName"/>');
gridColumns.push('<fmt:message key="item.platform"/>');
gridColumns.push('<fmt:message key="item.classification"/>');
gridColumns.push('<fmt:message key="item.itemProductFamily"/>');
gridColumns.push('<fmt:message key="fc.forecastModel"/>');
gridColumns.push('<fmt:message key="fc.status"/>');
gridColumns.push('<fmt:message key="fc.numberOfRollover"/>');
gridColumns.push('<fmt:message key="as.responsibility"/>'+'_EXPANDCELL');
gridColumns.push('<fmt:message key="item.dataSource"/>');
gridColumns.push('<fmt:message key="label.last_changed"/>');
gridColumns.push('<fmt:message key="label.last_change_by"/>');
<c:forEach var="attributeDefn" items = "${flexAttributeDefnListForecast}">
     gridColumns.push('<fmt:message key="flex.costforecast.${attributeDefn.associatedAttribute}"/>')
</c:forEach>
<c:forEach var="period" items="${forecastSearchForm.timeline.periods}">
gridColumns.push('${period.label}(<fmt:formatDate pattern="MMM dd" value="${period.startDate}"/>)');
</c:forEach>
var gridRows = [];
<c:forEach var="row" items="${forecastSearchForm.searchResult.values}" varStatus="rowCount">
	<c:set var="fc" value="${row.values[0]}"/>
	<c:set var="im" value="${fc.item}"/>
	var row = {};
	row['checkboxValue'] = '<c:out value="${fc.forecastKey}"/>';
	var itemNumberLink = '<a href="#" onClick="openPopOver(\'${im.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${im.itemNumber}" removeColon="true"/></a>';
	row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
	row['<fmt:message key="item.itemDescription"/>'] = '<c:out value="${im.description}"/>';
	row['<fmt:message key="fc.site"/>'] = '<c:out value="${fc.site.siteDescription}"/>';
	var functionalGroupNames = [];
	<c:forEach var="fgs" items="${im.functionalGroups}" varStatus="count">
	var functionalGroupName = '${fgs.name}';
	functionalGroupNames.push(functionalGroupName);
	</c:forEach>
	row['<fmt:message  key="fc.functionalGroups"/>'] = functionalGroupNames.join(',');

	var parentFunctionalGroupNames = [];
	<c:forEach var="fgs" items="${im.functionalGroups}" varStatus="count">
	<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}">
	var parentFunctionalGroupName = '${pfg.name}';
	parentFunctionalGroupNames.push(parentFunctionalGroupName);
	</c:forEach>
	</c:forEach>
	row['<fmt:message  key="functionalGroup.parentName"/>'] = parentFunctionalGroupNames.join(',');
	var catNames = [];
	<c:forEach var="cat" items="${im.categories}" varStatus="ccount">
	var catName = '${cat.categoryName}';
	catNames.push(catName);
	</c:forEach>
	//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
	row['<fmt:message  key="item.categoryName"/>'] = catNames.join(',');
	var platFormNames = [];
	<c:forEach var="p" items="${im.platforms}" varStatus="pcount">
	var platformName = '<c:out value="${p.platformName}"/>';
	platFormNames.push(platformName);
	</c:forEach>
	row['<fmt:message  key="item.platform"/>'] = platFormNames.join(',');


	row['<fmt:message key="item.classification"/>'] = '<c:out value="${im.itemClassification}"/>';
	row['<fmt:message key="item.itemProductFamily"/>'] = '<c:out value="${im.productFamily}"/>';
	row['<fmt:message key="fc.forecastModel"/>'] = '<fmt:message  key="scplatform.forecast.model.${fc.forecastModel}"/>';
	row['<fmt:message key="fc.status"/>'] ='<c:out value="${fc.status}"/>';
	row['<fmt:message key="fc.numberOfRollover"/>'] = '<c:out value="${fc.remainingRollovers}"/>';

	var responsibilities = [];
	<c:forEach var="ia" items="${im.assignments}" varStatus="iacount">
	var responsiblity = "${ia.userId} - ${ia.responsibility}";
	var region = '${ia.region}';
	if (region) {
		responsiblity = responsiblity + '- ' + region;
	}
	responsibilities.push(responsiblity);
	</c:forEach>
	row['<fmt:message  key="as.responsibility"/>'] = responsibilities.join(';');
	row['<fmt:message key="item.dataSource"/>'] = '<c:out value="${im.dataSource}"/>';
	row['<fmt:message key="label.last_changed"/>'] = '<c:out value="${fc.updateDate}"/>';
	row['<fmt:message key="label.last_change_by"/>'] = '<c:out value="${fc.lastChangeBy}"/>';

	<c:forEach var="attributeDefn" items = "${flexAttributeDefnListForecast}">
	var attributeName = "${attributeDefn.associatedAttribute}";
	console.log(attributeName);
	<c:out value="${fc[attributeName]}"/>
	row['<fmt:message key="flex.costforecast.${attributeDefn.associatedAttribute}"/>'] = '<c:out value="${fc[attributeDefn.associatedAttribute]}"/>';
    </c:forEach>

	<c:set var="fcValueMap" value="${fc.forecastValuesByPeriod}"/>
		<c:forEach var="period" items="${forecastSearchForm.timeline.periods}">
		row['${period.label}(<fmt:formatDate pattern="MMM dd" value="${period.startDate}"/>)'] = '<fmt:formatNumber maxFractionDigits="6" minFractionDigits="1" groupingUsed="false" value="${fcValueMap[period.startDate][\'ActualForecast\'].calculatedForecastValue}"/>';
		</c:forEach>
		gridRows.push(row);
		</c:forEach>
</script>
		<e2ot:searchContainerControl
			searchFields="${forecastSearchForm.allParameters}"
			form="${forecastSearchForm}" formName="forecastSearchForm"
			resultTableId="forecastSearchResultTable"
			showFilterCollapsed="${forecastSearchForm.filterAreaCollapsed}"
			showFilter="${forecastSearchForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${forecastSearchForm}"
			formName="forecastSearchForm"
			resultTableId="forecastSearchResultTable" showOrderMenu="true"
			showHideMenu="true" title="Search Forecast" />
	</form>
	<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DownloadFile')}">
		<script type="text/javascript">
		function goCancelCallback() {
			document.forms[0].action = "home";
			document.forms[0].submit();
		}
	<c:if test="${not empty forecastSearchForm.searchResult.values}">
		var footerButton = document.getElementById("searchFiledsButtons");
		var buttons = "";
		 	<c:if test="${!empty forecastSearchForm.nextAction}">
		  		buttons += '<button type="button" class="eto-btn eto-btn--primary" style="margin:4px;" id="nextButton" onclick="javascript:goNext(document.forms[\'forecastSearchForm\'],\'${fn:escapeXml(forecastSearchForm.nextAction)}\');">'
					+'<bean:message key="button.next" /></button>'
		  		</c:if>

			  <c:if test="${!empty forecastSearchForm.previousAction}">
				  buttons += '<button type="button" class="eto-btn" style="margin:4px;" id="backButton" onclick="javascript:goBack(document.forms[\'forecastSearchForm\'],\'${fn:escapeXml(forecastSearchForm.previousAction)}\')">'
			  			+'<bean:message key="button.back" /></button>'
			  </c:if>
			  	buttons += '<button type="button" class="eto-btn" style="margin:4px;" id="cancelButton" onclick="goCancelCallback();"><fmt:message key="button.cancel" /></button>';
			footerButton.innerHTML = buttons;
    var downloadButtonTD = document.getElementById("downloadButtonTD");
    var downloadButtons = '<div class="eto-checkbox eto-checkbox-menu" id="checkbox-menu-fileDownload"><span role="menu" class="eto-dropdown" style="margin-top: 0px;">'
						+'<button type="button" class="eto-dropdown__toggle eto-icon-btn" style="font-size: 20px;"><i class="md-icon md-icon--sm"  >file_download</i></button>'
						+'<ul class="eto-dropdown__menu"><li name="this-page" data-checkbox-state="checked" role="menuitem">'
						+'<a href="#" onclick = "javascript:submitForecastExtract(\'CURRENT\')"><fmt:message key="button.download.forecast.CURRENT"/></a></li><li name="all-pages" data-checkbox-state="double" role="menuitem">'
						+'<a href="#" onclick="javascript:submitForecastExtract(\'ADJUSTABLE\')"><fmt:message key="button.download.forecast.ADJUSTABLE"/></a></li></ul></span></div>';
    downloadButtonTD.innerHTML = downloadButtons;
	new eto.CheckboxMenu({ el: document.querySelector('#checkbox-menu-fileDownload') });
	</c:if>

	parent.parent.reportCall = function() {
	    <c:url var="linkHref" value="initReports">
	    <c:param name="reportType" value="${forecastSearchForm.reportType}" />
	    </c:url>
	    reloadBreadCrumb('initReports');
	    document.forms[0].action = "${linkHref}";
	    document.forms[0].submit();
	}
</script>
	</c:if>

</body>
</html>