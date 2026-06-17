<%@ include file="common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-CommodityProfile" />
</head>

<script>

	var messageBlock = null;

	function goDelete() {
		var selectedKeys = [];
		var checkb = document.getElementsByName("selectedPageKeys");
		for (var i = 0; i < checkb.length; i++) {
			if (checkb[i].checked) {
				var value = checkb[i].value;
				selectedKeys.push(value);
			}
		}
		if (selectedKeys.length === 0) {
			showOkMessageBox('OK', 'WARN', 'No profiles selected for deletion.', 'Warning');
			return;
		}

		showWaitBusy();

		apiService.delete('commodityprofile/delete', selectedKeys,
			function() {
				showOkMessageBox('OK', 'INFO', '<bean:message key="info.commodity.profile.delete"/>', 'Success', function() {
					// Refresh the page or reload search
					document.forms[0].submit();
				});
			},
			function(xhr) {
				showOkMessageBox('OK', 'ERROR', 'Failed to delete some profiles: ' + xhr.status, 'Error');
			}
		);
	}

	function init() {
	}
</script>
<body onload="init()">

	<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
		<div style="font-weight: bold;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					 <li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
	</div>

	<form name="commodityProfileSearchForm" action="submitCommodityProfileSearch" method="POST">

		<script>
		    var jsonColumn =  '${commodityProfileSearchForm.columns}';
			var gridColumns = [];
			gridColumns.push('<fmt:message key="commprof.profileName"/>');
			gridColumns.push('<fmt:message  key="commprof.companyItemType"/>');
			gridColumns.push('<fmt:message  key="item.categoryName"/>');
			gridColumns.push('<fmt:message  key="item.category.identifier"/>');
			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.commodityprofile.grid.thead')}"/>
                        <c:forEach var="col" items="${columnHeader}" varStatus="count">
                            gridColumns.push('<fmt:message key="${col}"/>');
                        </c:forEach>

			var gridRows = [];
			<c:forEach var="row" items="${commodityProfileSearchForm.searchResult.values}" varStatus="rowCount">
			<c:set var="cp" value="${row.values[0]}"/>
			var row = {};
			row['checkboxValue'] = "<c:out value='${cp.profileId}~${cp.profileName}'/>";

			row['<fmt:message key="commprof.profileName"/>'] = "<c:out value='${cp.profileName}'/>";
			row['<fmt:message  key="commprof.companyItemType"/>'] = "<c:out value='${cp.companyItemType}'/>";
			row['<fmt:message  key="item.categoryName"/>'] = "<c:out value='${cp.itemCategory.categoryName}'/>";
			row['<fmt:message  key="item.category.identifier"/>'] = "<c:out value='${cp.itemCategory.categoryId}'/>";
			row['<fmt:message  key="commprof.includeExcludeCostRecord"/>'] = "<c:out value='${cp.includeExcludeCostRecord}'/>";
			row['<fmt:message  key="commprof.includeExcludeCostForecast"/>'] = "<c:out value='${cp.includeExcludeCostForecast}'/>";
			row['<fmt:message  key="commprof.includeExcludeRebate"/>'] = "<c:out value='${cp.includeExcludeRebate}'/>";
			row['<fmt:message  key="commprof.includeExcludeItem"/>'] = "<c:out value='${cp.includeExcludeItem}'/>";
			row['<fmt:message  key="commprof.includeExcludeBOM"/>'] = "<c:out value='${cp.includeExcludeBOM}'/>";
			row['<fmt:message  key="commprof.includeExcludeTAM"/>'] = "<c:out value='${cp.includeExcludeTAM}'/>";
			row['<fmt:message  key="commprof.includeExcludePriceTAM"/>'] = "<c:out value='${cp.includeExcludePriceTAM}'/>";

			gridRows.push(row);
			</c:forEach>
		</script>

		<e2ot:searchContainerControl form="${commodityProfileSearchForm}"
			searchFields="${commodityProfileSearchForm.allParameters}"
			formName="commodityProfileSearchForm"
			resultTableId="commodityProfileSearchResultTable"
			showFilterCollapsed="${commodityProfileSearchForm.filterAreaCollapsed}"
			showFilter="${commodityProfileSearchForm.showFilterArea}"
			numColumns="3" />
		<e2ot:searchResultsControl searchForm="${commodityProfileSearchForm}"
			formName="commodityProfileSearchForm"
			resultTableId="commodityProfileSearchResultTable"
			showOrderMenu="true" showHideMenu="true" title="Commodity profile"
			showTitle="true" />

		<c:if test="${!empty commodityProfileSearchForm.searchResult}">
			<div class="footer" style="width: 50%;">
				<nav class="eto-form__btns" style="margin-left: 15px">
					<div class="eto-btn-group" style="margin-top: 15px">
						<button type="button" id="deleteButton" class="eto-btn"
							data-modal="#delete-profile-modal" disabled>
							<bean:message key="button.delete" />
						</button>
					</div>
				</nav>
			</div>

			<div class="eto-modal" id="delete-profile-modal" style="width: 100%;">
				<div class="eto-modal__content" style="height: 275px; width: auto;">
					<header class="eto-modal__header">
						<span>Confirmation</span>
						<button class="eto-modal__close" data-modal-close></button>
					</header>
					<section class="eto-modal__body">
						<div class="eto-messageblock" data-message-type="warn"
							id="message-block-example-3"
							style="position: relative; top: 50%; transform: translateY(-50%); -ms-transform: translateY(-50%);">
							<div class="eto-messageblock__body">Are you sure you want
								to delete the selected commodity profile(s) ?</div>
						</div>
					</section>
					<footer class="eto-modal__footer"
						style="height: 60px; border-top-style: solid; border-top-width: 2px; border-top-color: #cddfe4">
						<div class="eto-btn-group"
							style="margin-top: 10px; margin-bottom: -3px">
							<button class="eto-btn" data-modal-close>No, Keep
								Profile</button>
							<button class="eto-btn eto-btn--primary" data-modal-close
								onclick="javascript:goDelete();">Yes, Delete Profile</button>
						</div>
					</footer>
				</div>
			</div>
		</c:if>
	</form>
</body>

<script type="text/javascript">
function setGridEvents(grid){
	if (!grid) {
		return;
	}
	grid.on("rowSelection", function(event) {
		var selectedPageKeys = document.getElementsByName("selectedPageKeys");
		var checked = false;
		for (var i = 0; i < selectedPageKeys.length; i++) {
			if (selectedPageKeys[i].checked) {
				checked = true;
				break;
			}
		}
		if (checked) {
			$("#deleteButton").removeClass().addClass(
					"eto-btn eto-btn--primary");
			$("#deleteButton").removeAttr("disabled");
		} else {
			$("#deleteButton").removeClass().addClass("eto-btn");
			$("#deleteButton").attr("disabled", "disabled");
		}
	});
}

// Wait for grid to be initialized before attaching events
setTimeout(function() {
	if (typeof grid !== 'undefined' && grid !== null) {
		setGridEvents(grid);
	}
}, 500);

var modal = new eto.Modal({
	el : document.querySelector('#delete-profile-modal')
});
</script>
</html>