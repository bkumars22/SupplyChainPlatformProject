<%@ include file="common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-UserCommodityProfileMapping" />
</head>

<script>
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

        	apiService.delete('usercommodityprofile/delete', selectedKeys,
        		function() {
        			showOkMessageBox('OK', 'INFO', '<bean:message key="info.user.commodity.profile.delete"/>', 'Success', function() {
        	    		// Refresh the page or reload search
        				document.forms[0].submit();
        			});
        		},
        		function(xhr) {
        		    showOkMessageBox('OK', 'ERROR', 'Failed to delete some profiles: ' + xhr.status, 'Error');
        		}
        	);
	}

	var messageBlock = null;

	function validateSelectedPricingLength() {
		var checkboxChecked = false;
		var checkb = document.getElementsByName("selectedPageKeys");
		for (var i = 0; i < checkb.length; i++) {
			if (checkb[i].checked) {
				checkboxChecked = true;
				break;
			}
		}
		if (!checkboxChecked) {
			showOkMessageBox(
					'OK',
					'WARN',
					"<fmt:message key='warn.commodityProfile.no_row_selected_to_delete'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
			return false;
		}
		return true;
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

	<form name="userCommodityProfileMappingForm" action="submitUserCommodityProfileMappingSearch" method="POST">

		<script>
			var gridColumns = [];
			<c:set var="profileUserMappingType" value="${e2ofn:getConfigValue('pcm.commodityProfile.user.mapping.type')}" />
			<c:if test="${profileUserMappingType == 'user'}">
			    gridColumns.push('<fmt:message  key="user.userLoginId"/>');
			    gridColumns.push('<fmt:message  key="user.userName"/>');
			    gridColumns.push('<fmt:message  key="roleAdmin.roleName"/>');
			</c:if>
			<c:if test="${profileUserMappingType == 'role'}">
                gridColumns.push('<fmt:message  key="commodity.profile.mapping.roleId"/>');
                gridColumns.push('<fmt:message  key="commodity.profile.mapping.businessEntityId"/>');
                gridColumns.push('<fmt:message  key="commodity.profile.mapping.businessEntityName"/>');
                gridColumns.push('<fmt:message  key="commodity.profile.mapping.businessEntityType"/>');
            </c:if>
			gridColumns.push('<fmt:message  key="commprof.profileName"/>');

			var gridRows = [];
			<c:forEach var="row" items="${userCommodityProfileMappingForm.searchResult.values}" varStatus="rowCount">

            var row = {};

			<c:if test="${profileUserMappingType == 'user'}">
			    <c:set var="userId" value="${row.values[0]}" />
                <c:set var="username" value="${row.values[1]}" />
                <c:set var="cp" value="${row.values[2]}" />
                <c:set var="roleName" value="${row.values[3]}" />
                <c:set var="rowKey" value="${row.values[4]}" />

                row['checkboxValue'] = "<c:out value='${rowKey}~${cp}'/>";
			</c:if>

			<c:if test="${profileUserMappingType == 'role'}">
                <c:set var="roleId" value="${row.values[0]}" />
                <c:set var="cp" value="${row.values[1]}" />
                <c:set var="businessEntityIdentifier" value="${row.values[2]}" />
                <c:set var="businessEntityName" value="${row.values[3]}" />
                <c:set var="businessEntityType" value="${row.values[4]}" />

                <c:set var="roleKey" value="${row.values[5]}" />
                <c:set var="businessEntityKey" value="${row.values[6]}" />

                row['checkboxValue'] = "<c:out value='${roleKey}~${cp}~${businessEntityKey}'/>";
            </c:if>

            <c:if test="${profileUserMappingType == 'user'}">
			    row['<fmt:message  key="user.userLoginId"/>'] = "<c:out value='${userId}'/>";
			    row['<fmt:message  key="user.userName"/>'] = "<c:out value='${username}'/>";
			    row['<fmt:message  key="roleAdmin.roleName"/>'] = "<c:out value='${roleName}'/>";
			</c:if>

            <c:if test="${profileUserMappingType == 'role'}">
                row['<fmt:message  key="commodity.profile.mapping.roleId"/>'] = "<c:out value='${roleId}'/>";
                row['<fmt:message  key="commodity.profile.mapping.businessEntityId"/>'] = "<c:out value='${businessEntityIdentifier}'/>";
                row['<fmt:message  key="commodity.profile.mapping.businessEntityName"/>'] = "<c:out value='${businessEntityName}'/>";
                row['<fmt:message  key="commodity.profile.mapping.businessEntityType"/>'] = "<c:out value='${businessEntityType}'/>";
            </c:if>

			row['<fmt:message  key="commprof.profileName"/>'] = "<c:out value='${cp}'/>";

			gridRows.push(row);
			</c:forEach>
		</script>

		<e2ot:searchContainerControl form="${userCommodityProfileMappingForm}"
			searchFields="${userCommodityProfileMappingForm.allParameters}"
			formName="userCommodityProfileMappingForm"
			resultTableId="userCommodityProfileMappingResultTable"
			showFilterCollapsed="${userCommodityProfileMappingForm.filterAreaCollapsed}"
			showFilter="${userCommodityProfileMappingForm.showFilterArea}"
			numColumns="3" />

        <e2ot:searchResultsControl
            searchForm="${userCommodityProfileMappingForm}"
            formName="userCommodityProfileMappingForm"
            resultTableId="userCommodityProfileMappingResultTable"
            showOrderMenu="true"
            showHideMenu="true"
            title="${e2ofn:getConfigValue('pcm.commodity.profile.label')}"
            showTitle="true" />

		<c:if test="${!empty userCommodityProfileMappingForm.searchResult}">
			<div class="footer" style="width: 50%">
				<nav class="eto-form__btns" style="margin-left: 15px;">
					<div class="eto-btn-group" style="margin-top: 15px">
						<button type="button" id="deleteButton" class="eto-btn"
							data-modal="#delete-user-profile-modal" disabled>
							<bean:message key="button.delete" />
						</button>
					</div>
				</nav>
			</div>

			<div class="eto-modal" id="delete-user-profile-modal"
				style="width: 100%;">
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
								to delete the selected user commodity profile(s) ?</div>
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
setGridEvents(grid);
function setGridEvents(grid){
	if (grid != null) {
		grid.on("rowSelection", function(event) {
			var selectedPageKeys = document
					.getElementsByName("selectedPageKeys");
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
}

	var modal = new eto.Modal({
		el : document.querySelector('#delete-user-profile-modal')
	});
</script>

</html>