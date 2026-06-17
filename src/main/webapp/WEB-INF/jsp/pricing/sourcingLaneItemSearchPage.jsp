<%@ include file="../common.jspf"%>

<e2i2:doctype/> 
<e2i2:skin/> 
<e2i2:preferences/> 
<e2i2:clientcache enabled="false"/> 
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true"/>
<e2ot:help contextName="Cost-Create"/>
</head>
<style>
#header-label{
   margin-left: 40px;
}
</style>
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
    formName.action=action;
    formName.buttonAction.value="next";
    formName.submit();
    showBusy();    
}

function goNewLane(itemKey, supplierKey)
{
	document.forms['sourcingLaneForm'].action="newLaneAndCostRecord.do";
	document.forms['sourcingLaneForm'].selectedItemKey.value = itemKey;
	document.forms['sourcingLaneForm'].supplierKey.value = supplierKey;
	document.forms['sourcingLaneForm'].backAction.value = "submitSourcingLaneItemSearch.do";
	document.forms['sourcingLaneForm'].submit();
	showWaitBusy();
}

function init()
{
	hideElement();
	resizeResultArea();
	setupHideColumnState('slItemSearchResultTable',true);
	setupOrderColumnState('slItemSearchResultTable',true,
			{mouseOverTitle:'<fmt:message key="label.moveColumn"/>'});
	
}
function getBackCall(){
	document.forms[0].action = "home.do";
	document.forms[0].submit();
	showWaitBusy();
}

$(document).ready(function(){
    disableUndefinedAction();
});

</script>
<body onload="init()">
<form name="sourcingLaneForm" action="submitSourcingLaneItemSearch" method="POST">
<input type="hidden" name="backAction"/>
   <script type="text/javascript">
   
	var jsonColumn =  '${sourcingLaneForm.columns}';
	var gridRowExposedAction = false;
	var gridColumns = [];
	gridColumns.push('');
	<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.newSourcingLane.grid.thead')}"/>
	<c:forEach var="col" items="${columnHeader}" varStatus="count">
		<c:set var="trimmedCol" value="${fn:trim(col)}"/>
		if(('<fmt:message key="${trimmedCol}"/>' == 'Platform') || ('<fmt:message key="${trimmedCol}"/>' == 'Responsibility')){
			gridColumns.push('<fmt:message key="${trimmedCol}"/>'+'_EXPANDCELL');
		} else {
			gridColumns.push('<fmt:message key="${trimmedCol}"/>');
		}
	</c:forEach>
	<c:forEach var="fx" items="${flex}" varStatus="count">
		gridColumns.push('${fx.name}');
   	</c:forEach>
   
   var gridRows = [];
   <c:forEach var="row" items="${sourcingLaneForm.searchResult.values}" varStatus="rowCount">
	<c:set var="im" value="${row.values[0]}"/>
	<c:set var="sup" value="${row.values[1]}"/>
	<c:set var="cat" value="${row.values[2]}"/>
	<c:set var="ao" value="${row.values[3]}"/>
	  
	var row = {};
	var itemNumberLink = '<a href="#" onClick="openPopOver(\'${im.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${im.itemNumber}" removeColon="true"/></a>';
	<c:set var="canEditItem" value ="${e2ofn:canEditItem(appContext,im)}"/>
    <c:choose>
       <c:when test="${canEditItem}">
          row[''] = '<a class="eto-grid-row-actions__action" href=\'javascript:goNewLane("${im.itemKey}","${sup.businessEntityKey}")\' title="Edit" data-action="edit"><span class="md-icon" aria-hidden="true">mode_edit</span></a>';
       </c:when>
       <c:otherwise>
          row[''] = '<span class="md-icon" aria-hidden="true" title="Edit access denied" style="opacity: 0.4; cursor: not-allowed;">mode_edit</span>';
       </c:otherwise>
    </c:choose>
	row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
	row['<fmt:message key="item.itemDescription"/>'] = '<c:out value="${im.description}"/>';
	row['<fmt:message key="item.businessEntityIdentifier"/>'] = '<c:out value="${im.businessEntity.businessEntityIdentifier}"/>';
	row['<fmt:message key="item.businessName"/>'] = '<c:out value="${im.businessEntity.businessEntityName}"/>';
	row['<fmt:message key="item.businessEntityType"/>'] = '<fmt:message key="business.businessTypeName.${im.businessEntity.businessEntityTypeKey}" />'
	row['<fmt:message key="item.revision"/>'] = '<c:out value="${im.itemVersion.revision}"/>';
	row['<fmt:message key="item.categoryName"/>'] = '<c:forEach var="cat" items="${im.categories}"><c:out value="${cat.categoryName}"/></c:forEach>';
	row['<fmt:message key="item.supplierName"/>'] ='<c:forEach var="sup" items="${im.suppliers}" varStatus="scount"><c:if test="${scount.index > 0 }">, </c:if><c:out value="${sup.businessEntityName}"/></c:forEach>';
	var responsibilities = [];
	<c:forEach var="ia" items="${im.assignments}" varStatus="iacount">
	var responsiblity = "${ia.userId} - ${ia.responsibility}";
	responsibilities.push(responsiblity);
	</c:forEach>
	row['<fmt:message key="as.responsibility" />'] = responsibilities.join(";");
	
	row['<fmt:message key="item.dataSource"/>'] ='<c:out value="${im.dataSource}"/>';
	gridRows.push(row);
	</c:forEach>
   </script>
<c:set var="getTitle" value="${empty requestScope.sourcingLaneAlert ? 'New Sourcing Lane' : requestScope.sourcingLaneAlert}"/>
<e2ot:searchContainerControl searchFields="${sourcingLaneForm.allParameters}" 
formName="sourcingLaneForm"  form="${sourcingLaneForm}" resultTableId="slItemSearchResultTable"
showFilterCollapsed="${sourcingLaneForm.filterAreaCollapsed}"
showFilter="${sourcingLaneForm.showFilterArea}" numColumns="3"/>
<e2ot:searchResultsControl searchForm="${sourcingLaneForm}" 
formName="sourcingLaneForm" resultTableId="slItemSearchResultTable"
showHideMenu="true" showOrderMenu="true" title="${getTitle}"></e2ot:searchResultsControl>
<input type="hidden" name="requestType"/>
<input type="hidden" name="previousAction"/>
<input type="hidden" name="nextAction"/>
<input type="hidden" name="buttonAction"/>
<input type="hidden" name="selectedItemKey"/>
<input type="hidden" name="supplierKey"/>
<div class="eto-btn-group">
<c:if test="${!empty sourcingLaneForm.previousAction}">
   <button id="backButton" onclick="javascript:goBack(document.forms['sourcingLaneForm'],'${fn:escapeXml(sourcingLaneForm.previousAction)}')"><bean:message key="button.back"/></button>
</c:if>    
<c:if test="${!empty sourcingLaneForm.nextAction}">
    <button id="nextButton" onclick="javascript:goNext(document.forms['sourcingLaneForm'],'${fn:escapeXml(sourcingLaneForm.nextAction)}');"><bean:message key="button.next"/></button>
</c:if>
</div>
</form>
<script>
    function disableUndefinedAction() {
        setTimeout(function() {

        var checkBoxInputs = $('#grid-result .eto-grid .eto-grid-frozen table tbody tr input[type="checkbox"]');
        checkBoxInputs.each(function(index) {
			    var checkboxValue = $(this).val();
		        $(this).prop('disabled', true);
		});
    }, 500);
}
</script>
</body>
</html>