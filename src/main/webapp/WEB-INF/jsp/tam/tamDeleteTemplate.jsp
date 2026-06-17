<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="TAM-Delete-Template-Download" />
</head>
<script>
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

	function init() {
		resizeResultArea();
		setupHideColumnState('tamDeleteTemplateDownloadResultTable', true);
		setupOrderColumnState('tamDeleteTemplateDownloadResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}
	
	$(document).ready(function() {

	});

</script>
<body onload="init()">
	<e2o:form action="searchDownloadtamDelete.do" method="POST">
		<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
			<div style="font-weight: bold;">
				<e2o:errors maxErrors="4" styleId="errors" />
				<logic:messagesPresent message="true">
					<html:messages id="message" message="true">
						<li>${message}</li>
					</html:messages>
				</logic:messagesPresent>
			</div>
		</div>
		<script>
			var jsonColumn = '${tamDeleteDownloadForm.columns}';
			var gridColumns = [];
			var selectionType = 'none';
			gridColumns
					.push('<fmt:message key="tam.ui.functional_group_name"/>');
			gridColumns
					.push('<fmt:message key="tam.ui.functional_group_type" />');
			gridColumns.push('<fmt:message key="tam.ui.site_type" />');

			gridColumns.push('<fmt:message key="tam.ui.site_description" />');
			gridColumns.push('<fmt:message key="tam.ui.supplier_exist" />');
			gridColumns.push('<fmt:message key="tam.ui.item_exist" />');

			var gridRows = [];
			<c:forEach var="row" items="${tamDeleteDownloadForm.searchResult.values}" varStatus="rowCount">
			<c:set var="fg" value="${row.values[0]}" />
			<c:set var="site" value="${row.values[1]}" />
			<c:set var="supplierAllocationCheck" value="${row.values[2]}" />
			<c:set var="itemAllocationCheck" value="${row.values[3]}" />
			var row = {};

			row['<fmt:message key="tam.ui.functional_group_name"/>'] = <e2ofn:escapePrint value="${fg.name}"/>;
			row['<fmt:message key="tam.ui.functional_group_type" />'] = 'CFG';
			row['<fmt:message key="tam.ui.site_type" />'] = <e2ofn:escapePrint value="${site.siteType}"/>;
			row['<fmt:message  key="tam.ui.site_description"/>'] = <e2ofn:escapePrint value="${site.siteDescription}"/>;
			row['<fmt:message key="tam.ui.supplier_exist" />'] = <e2ofn:escapePrint value="${supplierAllocationCheck}"/>;
			row['<fmt:message key="tam.ui.item_exist" />'] = <e2ofn:escapePrint value="${itemAllocationCheck}"/>;

			gridRows.push(row);
			</c:forEach>
		</script>
		<input type="hidden" name="backAction" />
		<e2ot:searchContainerControl
			searchFields="${tamDeleteDownloadForm.allParameters}"
			formName="tamDeleteDownloadForm"
			form="${tamDeleteDownloadForm}"
			resultTableId="tamDeleteTemplateDownloadResultTable"
			showFilterCollapsed="${tamDeleteDownloadForm.filterAreaCollapsed}"
			showFilter="${tamDeleteDownloadForm.showFilterArea}"
			numColumns="3" />
		<e2ot:searchResultsControl
			searchForm="${tamDeleteDownloadForm}"
			formName="tamDeleteDownloadForm"
			resultTableId="tamDeleteTemplateDownloadResultTable"
			showOrderMenu="true" showHideMenu="true" title="TAM Delete Template" />
	</e2o:form>
</body>
</html>