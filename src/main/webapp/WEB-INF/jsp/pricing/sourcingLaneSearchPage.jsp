<%@ include file="../common.jspf"%>
<%@page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@page import="java.util.ArrayList"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Cost-Search" />
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
}

function goNext(formName,action)
{
    formName.action=action;
    formName.buttonAction.value="next";
    formName.submit();
}

function goViewLane(key)
{
	document.forms['sourcingLaneSearchForm'].action="viewCostRecord.do";
	document.forms['sourcingLaneSearchForm'].selectedLaneKey.value = key;
	document.forms['sourcingLaneSearchForm'].backAction.value = "${sourcingLaneSearchForm.searchAction}";	
	document.forms['sourcingLaneSearchForm'].submit();
	showWaitBusy();
}

function init()
{
	hideElement();
	resizeResultArea();
	setupHideColumnState('slSearchResultTable',true);
	setupOrderColumnState('slSearchResultTable',true,
			{mouseOverTitle:'<fmt:message key="label.moveColumn"/>'});
		
}

/* function getCancle(){
	showWaitBusy();
	document.sourcingLaneSearchForm.action = "home.do";
	document.sourcingLaneSearchForm.submit();
	parent.BreadCrumbModule.homeItemClicked();
} */

$(document).ready(function() {
	<c:if test="${not empty sourcingLaneSearchForm.searchResult.values}">
	var div = document.createElement('div');
    div.className = 'eto-btn-group';
    var d = "";
  <c:if test="${!empty sourcingLaneSearchForm.previousAction}">
		d ='<button type="button" class="eto-btn" id="backButton" onclick=\'javascript:goBack(document.forms["sourcingLaneSearchForm"],"${fn:escapeXml(sourcingLaneSearchForm.previousAction)}")\'><bean:message key="button.back"/></button>';
	</c:if>    
	<c:if test="${!empty sourcingLaneSearchForm.nextAction}">
      d = '<button id="nextButton"  class="eto-btn" onclick="\'javascript:goNext(document.forms["sourcingLaneSearchForm"],"${fn:escapeXml(sourcingLaneSearchForm.nextAction)}")\'><bean:message key="button.next"/><button>';
	</c:if>
    div.innerHTML =d;
	document.getElementById('searchFiledsButtons').appendChild(div);
	</c:if>
});
</script>
<body onload="init()">
	<%
		ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(
				FlexAttributeManager.COST.getFlexAttributeDefinitionList());
		pageContext.setAttribute("flex", flexAttributeDefns);
	%>
	<form name="sourcingLaneSearchForm" action="submitSourcingLaneSearch" method="POST">
		<input type="hidden" name="selectedLaneKey" />
		<input type="hidden" name="backAction" />
		<script type="text/javascript">
		var gridColumns = [];
		gridColumns.push('');
		var jsonColumn =  '${sourcingLaneSearchForm.columns}';
		var gridRowExposedAction = false;
		<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.searchSourcingLane.grid.thead')}"/>
		<c:forEach var="col" items="${columnHeader}" varStatus="count">
			<c:set var="trimmedCol" value="${fn:trim(col)}"/>
			if(('<fmt:message key="${trimmedCol}"/>' == 'Platform') || ('<fmt:message key="${trimmedCol}"/>' == 'Responsibility')){
				gridColumns.push('<fmt:message key="${trimmedCol}"/>'+'_EXPANDCELL');
			} else {
				gridColumns.push('<fmt:message key="${trimmedCol}"/>');
			}
		</c:forEach>
		<c:if
		test="${e2ofn:hasEventAccess(appContext, 'FUNCTIONAL_GROUP' , 'Read')}">
		gridColumns.push('<fmt:message  key="item.functionalGroups"/>');
		gridColumns.push('<fmt:message  key="functionalGroup.parentName"/>');
		</c:if>
		var gridRows = [];
			<c:forEach var="row" items="${sourcingLaneSearchForm.searchResult.values}" varStatus="rowCount">
				<c:set var="sl" value="${row.values[0]}"/>
				<c:set var="sup" value="${row.values[1]}"/>
				<c:set var="cat" value="${row.values[2]}"/>
				var row = {};
				<c:set var="canEditItem" value ="${e2ofn:canEditItem(appContext,sl.item)}"/>
				<c:choose>
                    <c:when test="${canEditItem}">
                        row[''] = '<a class="eto-grid-row-actions__action" href=\'javascript:goViewLane("${sl.sourcingLaneKey}")\' title="Edit" data-action="edit"><span class="md-icon" aria-hidden="true">mode_edit</span></a>';
                    </c:when>
                     <c:otherwise>
                        row[''] = '<span class="md-icon" aria-hidden="true" title="Edit access denied" style="opacity: 0.4; cursor: not-allowed;">mode_edit</span>';
                     </c:otherwise>
				</c:choose>
				row['<fmt:message key="sl.status"/>'] =  '<c:out value="${sl.status}"/>';
				row['<fmt:message key="item.category.identifier"/>'] = '<c:out value="${cat.categoryId}"/>';
				row['<fmt:message key="item.categoryName"/>'] = '<c:out value="${cat.categoryName}"/>';
				var itemNumberLink = '<a href="#" onClick="openPopOver(\'${sl.item.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${sl.item.itemNumber}" removeColon="true"/></a>';
				row['<fmt:message key="sl.item"/>'] = itemNumberLink;
				row['<fmt:message key="item.itemDescription"/>'] = '<c:out value="${sl.item.description}"/>';
				row['<fmt:message key="item.businessEntityIdentifier"/>'] = '<c:out value="${sl.item.businessEntity.businessEntityIdentifier}"/>';
				row['<fmt:message key="item.businessName"/>'] = '<c:out value="${sl.item.businessEntity.businessEntityName}"/>';
				row['<fmt:message key="item.businessEntityType"/>'] = '<fmt:message key="business.businessTypeName.${sl.item.businessEntity.businessEntityTypeKey}" />';
				row['<fmt:message key="sl.supplier"/>'] = '<c:out value="${sl.supplier.businessEntityName}"/>';
				row['<fmt:message key="sl.fromSite"/>'] = '<c:out value="${sl.fromSite.siteDescription}"/>';
				row['<fmt:message key="sl.mrpSite"/>'] = '<c:out value="${sl.toSite.siteDetail.mrpSite}" />';
				row['<fmt:message key="sl.toSite"/>'] =  '<c:out value="${sl.toSite.siteDescription}"/>';
				row['<fmt:message key="sl.currency"/>'] = '<c:out value="${sl.currencyCode}"/>';
				row['<fmt:message key="sl.productState"/>'] = '<fmt:message key="sl.productState.${sl.productState}"/>';
				 
				var responsibilities = [];
				<c:forEach var="ia" items="${sl.item.assignments}" varStatus="iacount">
				var responsiblity = "<c:out value='${ia.userId} - ${ia.responsibility}'/>";
				var region = "<c:out value='${ia.region}'/>";
				if (region) {
					responsiblity = responsiblity + '- ' + region;
				}
				responsibilities.push(responsiblity);
				</c:forEach>
				row['<fmt:message  key="as.responsibility"/>'] = responsibilities.join(';');
			   <c:choose>
				<c:when test="${!empty sl.bom}">
					row['<fmt:message key="sl.nonManaged"/>']='<fmt:message key="info.yes"/>';
				</c:when>
				<c:otherwise>
               		row['<fmt:message key="sl.nonManaged"/>']='<fmt:message key="info.no"/>';
				</c:otherwise>
				</c:choose>
				row['<fmt:message  key="item.dataSource"/>'] = '<c:out value="${sl.item.dataSource}"/>';
				<c:if test="${e2ofn:hasEventAccess(appContext, 'FUNCTIONAL_GROUP' , 'Read')}">
					<c:forEach var="fgs" items="${sl.item.functionalGroups}" varStatus="count">
						<c:if test="${!empty fgs }">
							<c:set var = "fgsString" value = "${fgs.getName()}"/>
							<c:if test="${count.index ne 0}">row['<fmt:message  key="item.functionalGroups"/>'] = ' <c:out value=", " />';</c:if>
							row['<fmt:message  key="item.functionalGroups"/>'] = '<c:out value="${fgsString}"/>';
						</c:if>
					</c:forEach>
					<c:forEach var="fgs" items="${sl.item.functionalGroups}" varStatus="count1">
						<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}" varStatus="count">
							<c:if test="${!empty pfg}">
								<c:set var="pfgString" value="${pfg.getName()}"/>
								<c:if test="${count.index ne 0}">
								row['<fmt:message  key="functionalGroup.parentName"/>'] = '<c:out value=", "/>';
							</c:if>
							row['<fmt:message  key="functionalGroup.parentName"/>'] ='<c:out value="${pfgString}" />';
						</c:if>
					</c:forEach>
			</c:forEach>
		</c:if>
		gridRows.push(row);
	</c:forEach>
</script>
		<e2ot:searchContainerControl
			searchFields="${sourcingLaneSearchForm.allParameters}"
			formName="sourcingLaneSearchForm" form="${sourcingLaneSearchForm}"
			resultTableId="slSearchResultTable"
			showFilterCollapsed="${sourcingLaneSearchForm.filterAreaCollapsed}"
			showFilter="${sourcingLaneSearchForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${sourcingLaneSearchForm}"
			formName="sourcingLaneSearchForm" resultTableId="slSearchResultTable"
			showHideMenu="true" showOrderMenu="true" title="Search Sourcing Lane">
		</e2ot:searchResultsControl>
		<input type="hidden" name="requestType" value="${sourcingLaneSearchForm.requestType}" />
		<input type="hidden" name="previousAction" value="${sourcingLaneSearchForm.previousAction}" />
		<input type="hidden" name="nextAction" value="${sourcingLaneSearchForm.nextAction}" />
		<input type="hidden" name="buttonAction"/>
		<div class="footer" style="border-color: #c8c4c4;">
			<c:set var="previousActionEncoded">
				<c:out value="${sourcingLaneSearchForm.previousAction}" />
			</c:set>
			<nav class="eto-form__btns" style="margin-left: 30px">
				<div class="eto-btn-group" style="margin-top: 15px">
					<c:if test="${!empty previousActionEncoded}">
						<button class="eto-btn" type="button" id="backButton"
							onclick="javascript:goBack(document.forms['sourcingLaneSearchForm'],'${previousActionEncoded}')">
							<bean:message key="button.back" />
						</button>
					</c:if>
					<c:set var="nextActionEncoded">
						<c:out value="${sourcingLaneSearchForm.nextAction}" />
					</c:set>
					<c:if test="${!empty nextActionEncoded}">
						<button class="eto-btn" id="nextButton" type="button"
							onclick="javascript:goNext(document.forms['sourcingLaneSearchForm'],'${nextActionEncoded}');">
							<bean:message key="button.next" />
						</button>
					</c:if>
				</div>
			</nav>
		</div>
	</form>
</body>
</html>