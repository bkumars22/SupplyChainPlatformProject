<%@ include file="../common.jspf"%>
<%@page import="java.util.ArrayList"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Supply Allocation exception" />
<script>
function init() {
	
}
function goSearch() {
	document.forms[0].action = "submitSupplyAllocationException";
	document.forms[0].submit();
	showWaitBusy();
}
function goContinue() {
	$("#cheackRows").val(true);
	submitExtractToFile();
	showWaitBusy();
	new eto.Modal({
		el : document.querySelector('#Tam_exp_popup_modal')
	}).close();
}
function goOk() {
	$("#cheackRows").val(false);
	document.forms[0].action = "submitSupplyExceptionToReport";
	document.forms[0].submit();
	showWaitBusy();
}
$(document).ready(function() {
	var message='${tamExceptionForm.messagePopup}';
if(message!=""){
	$("#errMessage").html(message);
new eto.Modal({
	el : document.querySelector('#Tam_exp_popup_modal')
}).open();
}
});
parent.parent.reportCall = function() {
	<c:url var="linkHref" value="initReports">
	<c:param name="reportType" value="supplyAllocationException" />
	</c:url>
	reloadBreadCrumb('initReports');
	document.forms[0].action="${linkHref}";
    document.forms[0].submit(); 
    showWaitBusy(); 
}
</script>
</head>
<body onload="init()">
	<form name="tamExceptionForm" action="submitSupplyAllocationException"  method="POST" style="margin:0px,padding:0px">
<div class="eto-modal" id="Tam_exp_popup_modal">
  <div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
    <header class="eto-modal__header">
      <span>Info</span>
      <button class="eto-modal__close" data-modal-close></button>
    </header>
    <section class="eto-modal__body" style="overflow: hidden;">
      <p id="errMessage" style="white-space: normal;">
       </p>
    </section>
    <footer class="eto-modal__footer">
    <button type="button" onclick="goOk();" class="eto-btn eto-btn--primary">Yes</button>
    <button type="button" class="eto-btn" data-modal-close class="eto-btn eto-btn--primary" >No</button>
      
    </footer>
  </div>
</div>
<input type="hidden" name="cheackRows" id="cheackRows" value="${tamExceptionForm.cheackRows}"/>
	<script>
	var jsonColumn =  '${tamExceptionForm.columns}';
	var gridColumns = [];
	var selectionType = 'none';
	gridColumns.push('<fmt:message key="tam.exception.functionalGroupId"/>');
	gridColumns.push('<fmt:message key="tam.exception.functionalGroupName"/>');
	gridColumns.push('<fmt:message key="tam.exception.functionalGroupType"/>');
	gridColumns.push('<fmt:message key="tam.exception.startDate" />');
	gridColumns.push('<fmt:message key="tam.exception.endDate" />');
	gridColumns.push('<fmt:message key="tam.exception.location" />');
	gridColumns.push('<fmt:message key="tam.exception.siteType" />');
	gridColumns.push('<fmt:message key="tam.exception.allocation" />');
	gridColumns.push('<fmt:message key="tam.exception.updateDate" />');
	gridColumns.push('<fmt:message key="tam.exception.updateBy" />');
	var gridRows = [];
	<c:forEach var="row" items="${tamExceptionForm.searchResult.values}" varStatus="rowCount">
	var row = {}; 
	<c:set var="fg" value="${row.values[0]}" />
	<c:set var="lastChangedOn" value="${row.values[1]}"/>
	<c:set var="lastChangedBy" value="${row.values[2]}"/>
	<c:set var="startDate" value="${row.values[3]}"/>
	<c:set var="endDate" value="${row.values[4]}"/>
	<c:set var="siteDescription" value="${row.values[5]}"/>
	<c:set var="allocation" value="${row.values[6]}"/>
		<fmt:parseNumber var="allocationvalue" type="number" value="${allocation}" />  
		<fmt:formatDate var="datePerformed"	type="both" dateStyle="short" timeStyle="short"  value="${lastChangedOn}" />
			row['<fmt:message key="tam.exception.functionalGroupId"/>']	= '<c:out value="${row.values[7]}"/> ';
		row['<fmt:message key="tam.exception.functionalGroupName"/>']	= '<c:out value="${fg}"/> ';
		row['<fmt:message key="tam.exception.functionalGroupType"/>'] ='<c:out value="${row.values[8]}"/>';
		row['<fmt:message key="tam.exception.startDate"/>'] ='<fmt:formatDate value="${startDate}" pattern="dd/MM/yyyy" />';
		row['<fmt:message key="tam.exception.endDate"/>'] ='<fmt:formatDate value="${endDate}" pattern="dd/MM/yyyy" />'; 
		row['<fmt:message key="tam.exception.location"/>'] ='<c:out value="${siteDescription}"/>';
		row['<fmt:message key="tam.exception.siteType"/>'] ='<c:out value="${row.values[9]}"/>';
		row['<fmt:message key="tam.exception.allocation"/>'] = '<c:out value="${allocationvalue}"/> ';
		row['<fmt:message key="tam.exception.updateDate"/>'] ='<c:out value="${datePerformed}"/>';
		row['<fmt:message key="tam.exception.updateBy"/>'] ='<c:out value="${lastChangedBy}"/>';
		gridRows.push(row);
	</c:forEach>
	</script>
	<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					 <li>${message}</li>
				</html:messages>
				</logic:messagesPresent>
					<input type="hidden" name="backAction" />
		<e2ot:searchContainerControl
			searchFields="${tamExceptionForm.allParameters}"
			formName="tamExceptionForm" form="${tamExceptionForm}"
			resultTableId="tamExceptionResultTable"
			showFilterCollapsed="${tamExceptionForm.filterAreaCollapsed}"
			showFilter="${tamExceptionForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${tamExceptionForm}"
			formName="tamExceptionForm"
			resultTableId="tamExceptionResultTable" showOrderMenu="true"
			showHideMenu="true" title="Supplier Allocation Exception" />
			<input type="hidden" name="requestType" value="${requestType}"/>
			<input type="hidden" name="previousAction" value="${previousAction}"/>
			<input type="hidden" name="nextAction" value="${nextAction}"/>
			<input type="hidden" name="buttonAction" value="${buttonAction}"/>
	</form>
</body>
</html>