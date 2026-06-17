<%@ include file="common.jspf"%>

<e2i2:doctype/>
<e2i2:skin/>
<e2i2:preferences/>
<e2i2:clientcache/>


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true"/>
<e2ot:help contextName="SA-Search"/>
</head>
<script>


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

function goViewAllocation(key,destinationSiteKey, date, contextType, contextName, contextKey)
{
	document.forms['supplyAllocationSearchForm'].action="viewSupplyAllocation";
	document.forms['supplyAllocationSearchForm'].selectedItemKey.value = key;
	document.forms['supplyAllocationSearchForm'].selectedDestinationSite.value = destinationSiteKey;
	document.forms['supplyAllocationSearchForm'].fromDate.value = date;
	document.forms['supplyAllocationSearchForm'].toDate.value = date;
	document.forms['supplyAllocationSearchForm'].backAction.value = "${supplyAllocationSearchForm.searchAction}";
	document.forms['supplyAllocationSearchForm'].contextType.value = contextType;
	document.forms['supplyAllocationSearchForm'].contextName.value = contextName;
	document.forms['supplyAllocationSearchForm'].contextKey.value = contextKey;
	document.forms['supplyAllocationSearchForm'].submit();
	showWaitBusy();
}

</script>
<body onload="resizeResultArea()">
<form name="supplyAllocationSearchForm" action="${supplyAllocationSearchForm.searchAction}" method="POST">
<input type="hidden" name="selectedItemKey"/>
<input type="hidden" name="fromDate"/>
<input type="hidden" name="toDate"/>
<input type="hidden" name="backAction"/>
<input type="hidden" name="contextType"/>
<input type="hidden" name="contextName"/>
<input type="hidden" name="contextKey"/>
<input type="hidden" name="selectedDestinationSite"/>
<script type="text/javascript">
var jsonColumn =  '${supplyAllocationSearchForm.columns}';
var gridColumns = [];

gridColumns.push('');

<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.searchSupplyAllocation.grid.thead')}"/>
<c:forEach var="col" items="${columnHeader}" varStatus="count">
    <c:choose>
        <c:when test="${col == 'sa.contextType'}">
            <c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.incontext')}">
                gridColumns.push('<fmt:message key="sa.contextType" />');
            </c:if>
        </c:when>
        <c:when test="${col == 'sa.contextName'}">
            <c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.incontext')}">
                gridColumns.push('<fmt:message key="contextName" />');
            </c:if>
        </c:when>
        <c:otherwise>
            gridColumns.push('<fmt:message key="${fn:trim(col)}"/>');
        </c:otherwise>
    </c:choose>
</c:forEach>

var gridRows = [];
<c:forEach var="row" items="${supplyAllocationSearchForm.searchResult.values}" varStatus="rowCount">
var row = {};

<c:set var="sa" value="${row.values[0]}"/>
<%-- FC 07 --%>
<c:set var="canEditItem" value="${e2ofn:canEditItem(appContext, sa.customerItem)}"/>
<c:choose>
   <c:when test="${canEditItem}">
        row[''] ='<a class="eto-grid-row-actions__action" href=\'javascript:goViewAllocation("${sa.customerItem.itemKey}","${sa.destinationSite.siteKey}","<fmt:formatDate value="${sa.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/>","${sa.contextType}","${fn:escapeXml(sa.contextIdentifier)}","${sa.contextName}")\' title="Edit" data-action="edit"><span class="md-icon first" aria-hidden="true">mode_edit</span></a>';
   </c:when>
   <c:otherwise>
        row[''] = '<span class="md-icon" aria-hidden="true" title="Edit access denied" style="opacity: 0.4; cursor: not-allowed;">mode_edit</span>';
   </c:otherwise>
</c:choose>
row['<fmt:message key="sa.status"/>'] = '${sa.status}';
row['<fmt:message key="sa.item"/>'] = '${sa.customerItem.itemNumber}';
row['<fmt:message key="sa.customerItemCommodityNames"/>'] = '${e2ofn:getItemCategoryNames(sa.customerItem)}';
row['<fmt:message key="sa.supplierItem"/>'] = '${sa.supplierItem.itemNumber}';
row['<fmt:message key="sa.supplierItemCommodityNames"/>'] = '${e2ofn:getItemCategoryNames(sa.supplierItem)}';
row['<fmt:message  key="sa.suppliedBy"/>'] = '${sa.supplierBusinessEntity.businessEntityName}';
row['<fmt:message  key="sa.suppliedByType"/>'] = '<fmt:message key="business.businessTypeName.${sa.supplierBusinessEntity.businessEntityTypeKey}"/>'
row['<fmt:message  key="sa.suppliedByIdentifier"/>'] = '${sa.supplierBusinessEntity.businessEntityIdentifier}';
row['<fmt:message  key="sa.supplierSite"/>'] = '${sa.supplierSite.siteDescription}';
row['<fmt:message  key="sa.customerSite"/>'] = '${sa.customerSite.siteDescription}';
<c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.destinationSite.enabled.for.supplyAllocation') && !e2ofn:getConfigValue('pcm.supplierAllocation.fiscalCalendarValidation.enabled')}">
	row['<fmt:message  key="sa.destinationSiteName"/>'] = '${sa.destinationSite.siteDescription}';
</c:if>
row['<fmt:message  key="sa.allocation"/>'] = '<fmt:formatNumber value="${sa.allocation}" minFractionDigits="1" maxFractionDigits="1" maxIntegerDigits="3"/>';
row['<fmt:message  key="sa.startDate"/>'] = '<fmt:formatDate value="${sa.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/>';
row['<fmt:message  key="sa.endDate"/>'] = '<fmt:formatDate value="${sa.effectiveToDt}" pattern="${appContext.currentDateFormat}"/>';
row['<fmt:message  key="sa.contextType"/>'] = '${sa.contextType}';
row['<fmt:message  key="sa.contextName"/>'] = '${sa.contextIdentifier}';
row['<fmt:message  key="item.dataSource"/>'] = '${sa.customerItem.dataSource}';
row['<fmt:message  key="sa.businessName"/>'] = '${sa.customerItem.businessEntity.businessEntityName}';
row['<fmt:message  key="sa.businessType"/>'] = '<fmt:message key="business.businessTypeName.${sa.customerItem.businessEntity.businessEntityTypeKey}"/>'
row['<fmt:message  key="sa.businessIdentifier"/>'] = '${sa.customerItem.businessEntity.businessEntityIdentifier}';
row['<fmt:message key="sa.insertDate"/>'] = '<c:out value="${sa.insertDate}"/>';
row['<fmt:message key="sa.updateDate"/>'] = '<c:out value="${sa.updateDate}"/>';

gridRows.push(row);
console.log(${gridRows});
</c:forEach>
</script>

<e2ot:searchContainerControl searchFields="${supplyAllocationSearchForm.allParameters}" form="${supplyAllocationSearchForm}"
   formName="supplyAllocationSearchForm" resultTableId="saSearchResultTable"
   showFilterCollapsed="${supplyAllocationSearchForm.filterAreaCollapsed}"
   showFilter="${supplyAllocationSearchForm.showFilterArea}" numColumns="3"/>
<e2ot:searchResultsControl searchForm="${supplyAllocationSearchForm}"
   formName="supplyAllocationSearchForm" resultTableId="saSearchResultTable"
   showHideMenu="true" showOrderMenu="saSearchResultTable" title="Supply Allocation(Sub-Tier)">
<%-- <e2o:scrollableTable id="saSearchResultTable" width="100%"
   height="300px">
<e2o:scrollableTableHeader>
<e2o:scrollableTableHeaderRow>
<td class="fixedColumn ocmDisabled hcmDisabled"><fmt:message key="label.select"/>
<img style="cursor: pointer;" src="css/clearsort.gif"
    alt="<fmt:message key="info.clear_sort"/>" onclick="clearAllSortFields($(this).closest('thead'))"/>

</td>
<td><fmt:message key="sa.item"/>
	<e2ot:orderByControl searchForm="${supplyAllocationSearchForm}" col="itemNumber"/>
</td>
<td><fmt:message key="sa.supplierItem"/>
	<e2ot:orderByControl searchForm="${supplyAllocationSearchForm}" col="supplierItemNumber"/>
</td>
<td><fmt:message key="sa.suppliedBy"/>
	<e2ot:orderByControl searchForm="${supplyAllocationSearchForm}" col="supplierName"/>
</td>
<td><fmt:message key="sa.supplierSite"/></td>
<td><fmt:message key="sa.allocation"/></td>
<td><fmt:message key="sa.startDate"/>
	<e2ot:orderByControl searchForm="${supplyAllocationSearchForm}" col="startDate"/>
</td>
<td><fmt:message key="sa.endDate"/>
	<e2ot:orderByControl searchForm="${supplyAllocationSearchForm}" col="endDate"/>
</td>
<c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.incontext')}">
<td><fmt:message key="sa.contextType"/></td>
<td><fmt:message key="sa.contextName"/></td>
</c:if>
<td><fmt:message key="item.dataSource"/></td>
</e2o:scrollableTableHeaderRow>
</e2o:scrollableTableHeader>
<e2o:scrollableTableBody>
<c:forEach var="row" items="${supplyAllocationSearchForm.searchResult.values}" varStatus="rowCount">
<c:set var="sa" value="${row.values[0]}"/>
<e2o:scrollableTableBodyRow>
<td class="fixedColumn">
<input type="radio" name="selectRowButton" onclick="javascript:goViewAllocation('${sa.customerItem.itemKey}','${sa.destinationSite.siteKey}','<fmt:formatDate value="${sa.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/>','${sa.contextType}','${fn:escapeXml(sa.contextIdentifier)}','${sa.contextName}')">
</td>
<td><c:out value="${sa.customerItem.itemNumber}"/></td>
<td><c:out value="${sa.supplierItem.itemNumber}"/></td>
<td><c:out value="${sa.supplierBusinessEntity.businessEntityName}"/></td>
<td><c:out value="${sa.supplierSite.siteDescription}"/></td>
<td><fmt:formatNumber value="${sa.allocation}"
	minFractionDigits="1" maxFractionDigits="1" maxIntegerDigits="3"/>
</td>
<td><fmt:formatDate value="${sa.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/></td>
<td><fmt:formatDate value="${sa.effectiveToDt}" pattern="${appContext.currentDateFormat}"/></td>
<c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.incontext')}">
<td><c:out value="${sa.contextType}"/></td>
<td><c:out value="${sa.contextIdentifier}"/></td>
</c:if>
<td><c:out value="${sa.customerItem.dataSource}"/></td>
</e2o:scrollableTableBodyRow>

</c:forEach>
</e2o:scrollableTableBody>
</e2o:scrollableTable> --%>
</e2ot:searchResultsControl>
<input type="hidden" name="requestType"/>
<input type="hidden" name="previousAction"/>
<input type="hidden" name="nextAction"/>
<input type="hidden" name="buttonAction"/>
<e2i2:buttonbar>
<c:if test="${!empty supplyAllocationSearchForm.previousAction}">
    <e2i2:button id="backButton" onclick="javascript:goBack(document.forms['supplyAllocationSearchForm'],'${fn:escapeXml(supplyAllocationSearchForm.previousAction)}')">
    	<bean:message key="button.back"/>
    </e2i2:button>
</c:if>
<c:if test="${!empty supplyAllocationSearchForm.nextAction}">
    <e2i2:button id="nextButton" onclick="javascript:goNext(document.forms['supplyAllocationSearchForm'],'${fn:escapeXml(supplyAllocationSearchForm.nextAction)}');">
    	<bean:message key="button.next"/>
    </e2i2:button>
</c:if>
</e2i2:buttonbar>
</form>
</body>
</html>