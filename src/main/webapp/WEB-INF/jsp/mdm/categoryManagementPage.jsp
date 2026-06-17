<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="MDM-Commodity" />
</head>
<script>
	function validateSelectedRow() {
		var checkboxChecked = false;
		var checkb = document.getElementsByName("selectedPageKeys");
		for (var i = 0; i < checkb.length; i++) {
			if ($("#" + checkb[i].id).attr('checked')) {
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

	function goAssignOwner() {
		document.forms[0].action = "assignOwnerCategoryManagement.do"
		document.forms[0].submit();
		showWaitBusy();
	}

	function goUnassignOwner() {
		document.forms[0].action = "unassignOwnerCategoryManagement.do"
		document.forms[0].submit();
		showWaitBusy();
	}

	function goSetManaged() {
		document.forms[0].action = "setManagedCategoryManagement.do"
		document.forms[0].submit();
		showWaitBusy();
	}

	/* $(document).ready(function() {
		$("#assignmentUserId").autocomplete("ajaxQueryUserId.do", {
			delay : 500,
			minChars : 1,
			matchSubset : 1,
			maxItemsToShow : 20,
			matchContains : 0,
			cacheLength : 20,
			autoFill : true,
			selectedCurrent : true,
			extraParams : {
				activeOnly : 'yes'
			}
		})
	}); */

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

	<form name="categoryManagementForm" action="submitCategoryManagementSearch" method="POST">

		<script type="text/javascript">
			var jsonColumn =  '${categoryManagementForm.columns}';
			var gridColumns = [];

			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.commoditymanagementpage.grid.thead')}"/>
            <c:forEach var="col" items="${columnHeader}" varStatus="count">
                gridColumns.push('<fmt:message key="${col}"/>');
            </c:forEach>

			var gridRows = [];
			<c:forEach var="row" items="${categoryManagementForm.searchResult.values}" varStatus="rowCount">
			var row = {};
			row['checkboxValue'] = "${row.values[0]}";
			row['<fmt:message key="item.categoryName"/>'] = '${row.values[2]}';
			row['<fmt:message key="item.category.identifier"/>'] = "${row.values[1]}";
			row['<fmt:message key="mdm.managed"/>'] = "${row.values[3]}";
			row['<fmt:message key="as.user"/>'] = "${row.values[4]}";
			row['<fmt:message key="as.responsibility"/>'] = "${row.values[5]}";

			gridRows.push(row);
			</c:forEach>
		</script>

		<e2ot:searchContainerControl form="${categoryManagementForm}"
			searchFields="${categoryManagementForm.allParameters}"
			formName="categoryManagementForm"
			resultTableId="catMgmtSearchResultTable"
			showFilterCollapsed="${categoryManagementForm.filterAreaCollapsed}"
			showFilter="${categoryManagementForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${categoryManagementForm}"
			formName="categoryManagementForm"
			resultTableId="catMgmtSearchResultTable" showHideMenu="false"
			showOrderMenu="false" title="Item Categories" showTitle="true" />

		<fmt:message var="actionTitle" key="mdm.category.title" />
		<fmt:message var="clearTitle" key="button.title.clear" />
		<fmt:message var="pushdownTitle" key="button.title.pushdown" />

		<c:if
			test="${!empty categoryManagementForm.searchResult && e2ofn:hasAccess(appContext, 'ITEM_CATEGORY', 'Save')}">
			<div id="butttonsDiv" class="footer" style="width: 50%">
				<nav class="eto-form__btns" style="margin-left: 15px">
					<div class="eto-btn-group" style="margin-top: 15px">
						<c:if
							test="${e2ofn:hasAccess(appContext, 'ITEM_CATEGORY', 'Assign')}">
							<button type="button" id="assignButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<fmt:message key="button.assign_responsibility" />
							</button>

							<button type="button" id="unAssignButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<fmt:message key="button.unassign_responsibility" />
							</button>
						</c:if>
						<c:if
							test="${e2ofn:getConfigValue('pcm.mdm.category.managedFlag')}">
							<button type="button" id="setButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<fmt:message key="button.set_managedby" />
							</button>
						</c:if>
					</div>
				</nav>
			</div>
		</c:if>

		<div class="eto-modal" id="assignModal" style="width: 100%;">
			<div class="eto-modal__content" style="height: auto; width: auto;">
				<header class="eto-modal__header">
					<span><label id="modalHeader"></label></span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<div class="eto-messageblock" data-message-type="warn"
						id="message-block-example-3">
						<div class="eto-messageblock__body">
							<label id="rowCount" style="font-weight: bold;"></label>&nbsp;<b>selected</b><br>
							<!-- Are you sure you want to make changes for the selected
							commodities -->
							You are about to perform action on all the selected items
						</div>
					</div>

					<div id="checkAllDiv" style="margin-top: 15px; width: auto;">
						<label class="eto-checkbox"> <input
							class="eto-checkbox__field" type="checkbox" id="checkAll"
							name="applyToAll" value="true"> <span
							class="eto-checkbox__box"></span><span
							class="eto-checkbox__label"><b>Apply to ALL results on
									ALL pages</b></span>
						</label>
					</div>

					<div id="assignToDiv"
						style="display: none; margin-top: 15px; width: 300px;">
						<c:set var="userId"
							value="${categoryManagementForm.assignmentUserId}" />
						<c:if test="${empty userId}">
							<c:set var="userId" value="${appContext.currentUser.userId}" />
						</c:if>
						<div class="eto-autocomplete" id="assignmentUserIdAutoComplete">
							<label class="eto-autocomplete__label">Assign To</label><input
								class="eto-autocomplete__field" type="text"
								 placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>' id="assignmentUserId"
								name="assignmentUserId" value="${userId}" style="width: 300px">
							<div class="eto-autocomplete__message"></div>
							<div class="eto-results"></div>
						</div>
					</div>

					<div id="managedByDiv" class="eto-select" id="managedBySelect"
						style="display: none; margin-top: 15px; width: 300px;">
						<label class="eto-select__label"><fmt:message
								key="mdm.managed" /></label>
						<div class="eto-select__field-container">
							<select class="eto-select__field" name="managedFlag"
								id="managedByFlag">
								<option value=""><fmt:message key="mdm.managed.none" /></option>
								<c:forEach var="flag"
									items="${categoryManagementForm.managedFlagOptions}">
									<option value="${flag}"><fmt:message
											key="mdm.managed.${flag}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
				</section>
				<footer class="eto-modal__footer"
					style="margin-top: 5px; height: 60px; border-top-style: solid; border-top-width: 1px; border-top-color: lightgray;">
					<div class="eto-btn-group"
						style="margin-top: 10px; margin-bottom: -3px">
						<button class="eto-btn" data-modal-close>Cancel</button>
						<button id="dynamicButton" class="eto-btn eto-btn--primary"
							data-modal-close>
							<label id="dynamicButtonLabel" style="cursor: pointer;"></label>
						</button>
					</div>
				</footer>
			</div>
		</div>
	</form>
</body>


<script type="text/javascript">
	var modal = new eto.Modal({
		el : document.querySelector('#assignModal')
	});
	
	var auto = new eto.Autocomplete({
		el : document.querySelector('#assignmentUserIdAutoComplete')
	});

	auto.on('inputChange', function(query) {
		searchUsers(query, auto);
	});

	function searchUsers(query, auto) {
		var url = "ajaxQueryUserId.do?q=" + query;
		$.ajax({
			url : url,
			success : function(result) {
				var arr;
				if (result.includes("|")) {
					arr = result.split("|");
				} else {
					arr = result.split("\n");
				}
				
				auto.setContent(arr);
				auto.open();
			}
		});
	}

	setGridEvents(grid);
	function setGridEvents(grid){
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
			$("#assignButton").removeClass().addClass(
					"eto-btn eto-btn--primary");
			$("#assignButton").removeAttr("disabled");
			$("#unAssignButton").removeAttr("disabled");
			$("#setButton").removeAttr("disabled");
		} else {
			$("#assignButton").removeClass().addClass("eto-btn");
			$("#assignButton").attr("disabled", "disabled");
			$("#unAssignButton").attr("disabled", "disabled");
			$("#setButton").attr("disabled", "disabled");
		}
	});
	}

	var rowCountGlobal = 0;
	function openModal(button) {
		var id = button.id;
		var modalHeader = "";
		var rowCount = 0;
		var dynamicMethod = "";
		var dynamicButtonLabel = "";

		var selectedPageKeys = document.getElementsByName("selectedPageKeys");
		for (var i = 0; i < selectedPageKeys.length; i++) {
			if (selectedPageKeys[i].checked) {
				rowCount++;
			}
		}

		if (id == "assignButton") {
			modalHeader = "Assign Responsibility";
			dynamicMethod = goAssignOwner;
			dynamicButtonLabel = "Yes, Assign";
			$("#assignToDiv").css('display', 'block');
			$("#managedByDiv").css('display', 'none');
		} else if (id == "unAssignButton") {
			modalHeader = "Unassign Responsibility";
			dynamicMethod = goUnassignOwner;
			dynamicButtonLabel = "Yes, Unassign";
			$("#assignToDiv").css('display', 'none');
			$("#managedByDiv").css('display', 'none');
		} else {
			modalHeader = "Set Managed By";
			dynamicMethod = goSetManaged;
			dynamicButtonLabel = "Set";
			$("#assignToDiv").css('display', 'none');
			$("#managedByDiv").css('display', 'block');
		}

		$("#modalHeader").html(modalHeader);
		$("#rowCount").html(rowCount);
		rowCountGlobal = rowCount;
		if($("#checkAll").attr("checked")){
			$("#rowCount").html(${categoryManagementForm.totalRows});	
		}
		$('#dynamicButton').replaceWith($('#dynamicButton').clone());
		document.getElementById('dynamicButton').addEventListener("click",
				dynamicMethod)
		$("#dynamicButtonLabel").html(dynamicButtonLabel);

		modal.open();
	}
	
	$("#checkAll").change(function() {
	    if(this.checked) {
	    	$("#rowCount").html(${categoryManagementForm.totalRows});
	    }
	    else{
	    	$("#rowCount").html(rowCountGlobal);	
	    }
	});
</script>
</html>