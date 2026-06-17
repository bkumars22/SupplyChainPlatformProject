<%@page import="java.util.Comparator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@ include file="common.jspf"%>
<!DOCTYPE html>
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-BomCost" />
</head>


<script>
	const gridErrors = {};
	function reapplyErrors() {
		Object.keys(gridErrors).forEach(rowIdx => {
			Object.keys(gridErrors[rowIdx]).forEach(colIdx => {
				buildErrorMessageByTr(rowIdx, colIdx, gridErrors[rowIdx][colIdx], '<fmt:message key="bom.cost.rollup.finalSellingPrice"/>');
			});
		});
	}
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
	function init() {
		resizeResultArea();
	}
	
	function gridUpdateCallBack(){
	    $('div .eto-grid-expand__truncated').each(function(){
			if($(this).html().length == 0){
				var value = $(this).parent().children( ".eto-grid-expand__content" ).html();
				$(this).closest('td').removeClass("eto-grid-expand");
				$(this).closest('td').html(value);
			}
		});
		$('td[data-lines]').each(function(){
			if($(this).attr("class").indexOf(expland_class) == -1){
				$(this).removeAttr('data-lines');
			}
		});
	}
	
	// Function to initialize the grid and fetch BOM costs
	function fetchBomCost(bomKey, rowIndex) {
        apiService.get(
            'bomCostRollup/viewBomCostRollup',
            { bomKey: bomKey },
            function(response) {
                const responseObj = typeof response === "string" ? JSON.parse(response) : response;
                const entry = responseObj.jsonNodeList[0];  // assuming one result per item
                if (!entry) return;
                // Update only that row
                const row = gridRows[rowIndex];
                if (!row) return;
                row['<fmt:message key="bom.cost.rollup.rollupPrice"/>'] =
                    (entry.ROLLUP_PRICE == null || entry.ROLLUP_PRICE === 0) ? '' : entry.ROLLUP_PRICE;
                row['<fmt:message key="bom.cost.rollup.finalSellingPrice"/>'] =
                        (entry.FINAL_SELLING_PRICE == null || entry.FINAL_SELLING_PRICE === 0) ? '' : entry.FINAL_SELLING_PRICE;
                <c:forEach var="costElement" items="${bomCostRollupForm.costElements}">
                row['${costElement.costElementName}'] =
                             (entry['${costElement.costElementKey}'] == null || entry['${costElement.costElementKey}'] === 0)
                              ? '' : entry['${costElement.costElementKey}'];
                </c:forEach>
				// Track error state
				if (entry.CURRENCY_CONVERSION_ERROR_MSG) {
					const colIndex = gridColumns.indexOf('<fmt:message key="bom.cost.rollup.finalSellingPrice"/>');
					if (!gridErrors[rowIndex]) gridErrors[rowIndex] = {};
					gridErrors[rowIndex][colIndex] = entry.CURRENCY_CONVERSION_ERROR_MSG;
				}

				createNewGridHandleEvents();
				reapplyErrors();
                // Refresh UI or re-render grid
            },
            function(error) {
                console.error("Error fetching BOM cost data for row " + rowIndex, error);
            }
        );
    }

	function goAsynchronusSearch(){
	    document.forms[0].action = "asynhronusSearchForBomCostRollup.do";
	    document.forms[0].submit();
	    showWaitBusy();
	}

	parent.parent.reportCall = function() {
		<c:url var="linkHref" value="initReports.do">
		<c:param name="reportType" value="bomCostRollUp" />
		</c:url>
		reloadBreadCrumb('initReports.do');
		document.forms[0].action = "${linkHref}";
		document.forms[0].submit();
	}
</script>
<body onload="init()">
	<form name="bomCostRollupForm" action="submitBomCostRollupManagement" method="POST">
	<input type="hidden" name="reportType" value="bomCostRollUp">
	<div style="font-weight: bold; white-space: pre-line;">
    			<logic:messagesPresent message="true">
    				<html:messages id="message" message="true">
    					<li>${message}</li>
    				</html:messages>
    			</logic:messagesPresent>
    		</div>
    		<script>
    			var jsonColumn =  '${bomCostRollupForm.columns}';
    			var selectionType = 'none';
    			var gridColumns = [];
    			var bomData = [];
    			gridColumns.push('<fmt:message key="item.isTopLevel" />');
    			gridColumns.push('<fmt:message key="item.itemNumber"/>');
    			gridColumns.push('<fmt:message key="item.categoryName" />');
    			gridColumns.push('<fmt:message key="item.businessName" />');
    			gridColumns.push('<fmt:message key="sl.currency"/>');
    			gridColumns.push('<fmt:message key="bom.cost.rollup.startDate" />');
    			gridColumns.push('<fmt:message key="bom.cost.rollup.endDate"/>');
    			gridColumns.push('<fmt:message key="bom.cost.rollup.status"/>');
    			gridColumns.push('<fmt:message key="bom.cost.rollup.finalSellingPrice"/>');
    			<c:if test="${not empty bomCostRollupForm.costElements}">
    			gridColumns.push('<fmt:message key="bom.cost.rollup.rollupPrice"/>');
                <c:forEach var="costElement" items="${bomCostRollupForm.costElements}" varStatus="count">
                     gridColumns.push('${costElement.costElementName}');
                </c:forEach>
    			</c:if>
    			var gridRows = [];
    			<c:forEach var="row" items="${bomCostRollupForm.searchResult.values}" varStatus="rowCount">
    				<c:set var="bom" value="${row.values[0]}" />
    					var row = {};
    					bomData.push({
                            bomKey: ${bom.bomKey}
                        });

    					row['<fmt:message key="item.itemNumber"/>'] = '<c:out value = "${bom.item.itemNumber}"/>';
    					if(${bom.item.isTopLevel}){
                            row['<fmt:message key="item.isTopLevel" />'] = 'Yes';
                        } else {
                            row['<fmt:message key="item.isTopLevel" />'] = 'No';
                        }
    				    row['<fmt:message key="item.businessName" />'] = '<c:out value = "${bom.businessEntity.businessEntityName}"/>';

    					var catNames = [];
    					<c:forEach var="cat" items="${bom.item.categories}" varStatus="count">
    					var catName = "<c:out value='${cat.categoryName}'/>";
    					catNames.push(catName);
    					</c:forEach>
    					//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
    					row['<fmt:message  key="item.categoryName"/>'] = catNames.join(',');
    					<c:set var='currency' value='${e2ofn:getConfigValue("pcm.mpn.cost.defaultcurrency")}'/>
    					row['<fmt:message key="sl.currency"/>'] = '<c:out value="${currency}"/>';
    					row['<fmt:message key="bom.cost.rollup.startDate" />'] = '<fmt:formatDate value="${bom.effectiveFrom}" pattern="${appContext.currentDateFormat}"/>';
    					row['<fmt:message key="bom.cost.rollup.endDate"/>'] = '<fmt:formatDate value="${bom.effectiveTo}" pattern="${appContext.currentDateFormat}"/>';
    					row['<fmt:message key="bom.cost.rollup.status"/>'] = '<c:out value="${bom.status}"/>';
    				    row['<fmt:message key="bom.cost.rollup.finalSellingPrice"/>'] = '<img src="skins/e2-modern/images/print_wait.gif" alt="Loading..." style="height:20px;">';
                        <c:if test="${not empty bomCostRollupForm.costElements}">
                           row['<fmt:message key="bom.cost.rollup.rollupPrice"/>'] ='<img src="skins/e2-modern/images/print_wait.gif" alt="Loading..." style="height:20px;">'
                           <c:forEach var="costElement" items="${bomCostRollupForm.costElements}">
                            	row['${costElement.costElementName}'] = '<img src="skins/e2-modern/images/print_wait.gif" alt="Loading..." style="height:20px;">';
                              </c:forEach>
                      	</c:if>
    					gridRows.push(row);
    			</c:forEach>
    			$(document).ready(function () {
                    if (bomData.length > 0) {
                        bomData.forEach((bom, index) => {
                            fetchBomCost(bom.bomKey, index);
                        });
                    }
                });
		</script>
		<e2ot:searchContainerControl
			searchFields="${bomCostRollupForm.allParameters}"
			form="${bomCostRollupForm}" formName="bomCostRollupForm"
			resultTableId="bomCostRollupSearchResultTable"
			showFilterCollapsed="${bomCostRollupForm.filterAreaCollapsed}"
			showFilter="${bomCostRollupForm.showFilterArea}" numColumns="5" />
		<e2ot:searchResultsControl searchForm="${bomCostRollupForm}"
			formName="bomCostRollupForm" resultTableId="bomCostRollupSearchResultTable"
			showOrderMenu="false" showHideMenu="false" title="BOM Cost Rollup"
			showTitle="true" />
	</form>
</body>
</html>