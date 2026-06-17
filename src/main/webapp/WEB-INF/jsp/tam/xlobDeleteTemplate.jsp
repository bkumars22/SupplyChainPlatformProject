<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="XLOB-Delete-Template-Download" />
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
		setupHideColumnState('xlobDeleteTemplateDownloadResultTable', true);
		setupOrderColumnState('xlobDeleteTemplateDownloadResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}

	$(document).ready(function() {

	});
</script>
<body onload="init()">
	<e2o:form action="searchDownloadXLOBDelete.do" method="POST">
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
			var jsonColumn = '${xlobDeleteTemplateDownloadForm.columns}';
			var gridColumns = [];
			var selectionType = 'none';
			gridColumns
					.push('<fmt:message key="xlob.ui.functional_group_name"/>');
			gridColumns
					.push('<fmt:message key="xlob.ui.functional_group_type" />');
			gridColumns.push('<fmt:message key="xlob.ui.site_description" />');
			gridColumns.push('<fmt:message key="xlob.ui.site_type" />');
			gridColumns.push('<fmt:message key="xlob.ui.tam_exist" />');

			var gridRows = [];
			<c:forEach var="row" items="${xlobDeleteTemplateDownloadForm.searchResult.values}" varStatus="rowCount">
			<c:set var="fgId" value="${row.values[0]}" />
			<c:set var="fgName" value="${row.values[1]}" />
			<c:set var="siteKey" value="${row.values[2]}" />
			<c:set var="siteDescription" value="${row.values[3]}" />
			<c:set var="siteType" value="${row.values[4]}" />
			<c:set var="allocationSum" value="${row.values[7]}" />

			var row = {};

			row['<fmt:message key="xlob.ui.functional_group_name"/>'] = <e2ofn:escapePrint value="${fgName}"/>;
			row['<fmt:message key="xlob.ui.functional_group_type" />'] = 'XLOB';
			row['<fmt:message  key="xlob.ui.site_description"/>'] = <e2ofn:escapePrint value="${siteDescription}"/>;
			row['<fmt:message key="xlob.ui.site_type" />'] = '${siteType}';
			row['<fmt:message key="xlob.ui.tam_exist" />'] = '${allocationSum > 0 ? "Yes" : "No"}';

			gridRows.push(row);
			</c:forEach>
		</script>
		<input type="hidden" name="backAction" />
		<e2ot:searchContainerControl
			searchFields="${xlobDeleteTemplateDownloadForm.allParameters}"
			formName="xlobDeleteTemplateDownloadForm"
			form="${xlobDeleteTemplateDownloadForm}"
			resultTableId="xlobDeleteTemplateDownloadResultTable"
			showFilterCollapsed="${xlobDeleteTemplateDownloadForm.filterAreaCollapsed}"
			showFilter="${xlobDeleteTemplateDownloadForm.showFilterArea}"
			numColumns="3" />
		<e2ot:searchResultsControl
			searchForm="${xlobDeleteTemplateDownloadForm}"
			formName="xlobDeleteTemplateDownloadForm"
			resultTableId="xlobDeleteTemplateDownloadResultTable"
			showOrderMenu="true" showHideMenu="true" title="XLOB Delete Template" />
	</e2o:form>
</body>
</html>