<%@ include file="common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache enabled="false" />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="SA-Create" />
</head>
<script>
	function callOnChange(field) {
		if (field.name == 'value(owner)') {
			if (field.value == 'REGIONAL') {
				document.getElementsByName("values(regions)")[0].disabled = false;
			} else {
				hideElement();
			}
		}
	}

	function hideElement() {
		if (document.getElementsByName("value(owner)")[0].value != 'REGIONAL') {
			document.getElementsByName("values(regions)")[0].disabled = true;
		}
	}

	function goBack(formName, action) {
		formName.action = action;
		formName.buttonAction.value = "back";
		formName.submit();
	}

	function goNext(formName, action) {
		formName.action = action;
		formName.buttonAction.value = "next";
		formName.submit();
	}

	function goCreateAllocation(key) {
		document.forms[0].action = 'createSupplyAllocation';
		document.forms[0].selectedItemKey.value = key;
		document.forms[0].backAction.value = 'submitSupplyAllocationItemSearch';
		document.forms[0].submit();
		showWaitBusy();
	}

	function init() {
		hideElement();
		resizeResultArea();
		setupHideColumnState('saItemSearchResultTable', true);
		setupOrderColumnState('saItemSearchResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});

	}
</script>
<body onload="init()">
	<form name="supplyAllocationItemSearchForm" action="submitSupplyAllocationItemSearch" method="POST">
		<input type="hidden" name="backAction" />
		<script type="text/javascript">
			var gridColumns = [];
			gridColumns.push('');

			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.createSupplyAllocation.grid.thead')}"/>
			<c:forEach var="col" items="${columnHeader}" varStatus="count">
				gridColumns.push('<fmt:message key="${fn:trim(col)}"/>');
			</c:forEach>

			var gridRows = [];
			<c:forEach var="row" items="${supplyAllocationItemSearchForm.searchResult.values}" varStatus="rowCount">
			var row = {};

			<c:set var="i" value="${row.values[0]}"/>
            <!-- Use taglib function to check if user has edit permission -->
            <c:set var="canEditItem" value="${e2ofn:canEditItem(appContext, i)}"/>
            <c:choose>
                <c:when test="${canEditItem}">
                    row[''] = '<a class="eto-grid-row-actions__action" href=\'javascript:goCreateAllocation("${i.itemKey}")\' title="Edit" data-action="edit"><span class="md-icon" aria-hidden="true">mode_edit</span></a>';
                </c:when>
                <c:otherwise>
                    row[''] = '<span class="md-icon" aria-hidden="true" title="Edit access denied" style="opacity: 0.4; cursor: not-allowed;">mode_edit</span>';
                </c:otherwise>
            </c:choose>

			var businessEntityName = '${be.businessEntityName}';
			var itemNumberLink = '<a href="#" onClick="openPopOver(\'${i.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${i.itemNumber}" removeColon="true"/></a>';
			row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
			row['<fmt:message  key="item.itemDescription"/>'] = '${i.description}';
			row['<fmt:message  key="item.itemRevision"/>'] = '${i.itemVersion.revision}';
			row['<fmt:message  key="item.itemVersion"/>'] = '${i.itemVersion}';
			row['<fmt:message  key="item.itemClassification"/>'] = '${i.itemClassification}';
			row['<fmt:message  key="item.itemProductFamily"/>'] = '${i.productFamily}';
			row['<fmt:message  key="item.supplierName"/>'] = "${row.values[8]}";
			row['<fmt:message  key="item.supplierItemNumber"/>'] = "${row.values[9]}";
			row['<fmt:message  key="item.supplierSite"/>'] = "${row.values[10]}";
			row['<fmt:message  key="item.businessName"/>'] = '${i.businessEntity.businessEntityName}';
			row['<fmt:message  key="item.businessEntityIdentifier"/>'] = '${i.businessEntity.businessEntityIdentifier}';
			row['<fmt:message  key="item.businessEntityType"/>'] = '<fmt:message key="business.businessTypeName.${i.businessEntity.businessEntityTypeKey}" />';
			row['<fmt:message  key="item.itemCategoryNames"/>'] = '${e2ofn:getItemCategoryNames(i)}';

			var responsibilities = [];
			<c:forEach var="ia" items="${i.assignments}" varStatus="iacount">
			var responsiblity = '${ia.userId} - ${ia.responsibility}';
			var region = '${ia.region}';
			if (region) {
				responsiblity = responsiblity + '- ' + region;
			}
			responsibilities.push(responsiblity);
			</c:forEach>
			row['<fmt:message  key="as.responsibility"/>'] = responsibilities;
			row['<fmt:message  key="item.dataSource"/>'] = '${i.dataSource}';
			gridRows.push(row);
			</c:forEach>
		</script>
		<e2ot:searchContainerControl
			searchFields="${supplyAllocationItemSearchForm.allParameters}"
			form="${supplyAllocationItemSearchForm}" formName="supplyAllocationItemSearchForm"
			resultTableId="saItemSearchResultTable"
			showFilterCollapsed="${supplyAllocationItemSearchForm.filterAreaCollapsed}"
			showFilter="${supplyAllocationItemSearchForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${supplyAllocationItemSearchForm}"
			formName="supplyAllocationItemSearchForm" resultTableId="saItemSearchResultTable"
			showHideMenu="true" showOrderMenu="true" title="New Supply Allocation(Sub-Tier)">
		</e2ot:searchResultsControl>
		<input type="hidden" name="requestType"/>
		<input type="hidden" name="previousAction"/>
		<input type="hidden" name="nextAction"/>
		<input type="hidden" name="buttonAction" />
		<input type="hidden" name="selectedItemKey" />
		<div class="footer" style="border-color: #c8c4c4;">
			<c:set var="previousActionEncoded">
				<c:out value="${supplyAllocationItemSearchForm.previousAction}" />
			</c:set>
			<nav class="eto-form__btns" style="margin-left: 30px">
				<div class="eto-btn-group" style="margin-top: 15px">
					<c:if test="${!empty previousActionEncoded}">
						<button class="eto-btn" type="button" id="backButton"
							onclick="javascript:goBack(document.forms['supplyAllocationItemSearchForm'],'${previousActionEncoded}')">
							<bean:message key="button.back" />
						</button>
					</c:if>
					<c:set var="nextActionEncoded">
						<c:out value="${supplyAllocationItemSearchForm.nextAction}" />
					</c:set>
					<c:if test="${!empty nextActionEncoded}">
						<button class="eto-btn" id="nextButton" type="button"
							onclick="javascript:goNext(document.forms['supplyAllocationItemSearchForm'],'${nextActionEncoded}');">
							<bean:message key="button.next" />
						</button>
					</c:if>
				</div>
			</nav>

		</div>
	</form>
</body>
</html>