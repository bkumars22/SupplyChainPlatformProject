<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="MDM-Item" />
</head>
<script>

/* function toggleCheckAll(){
	if(sessionStorage.getItem("checkAllItemAssignment") == "false"){
		sessionStorage.setItem("checkAllItemAssignment", "true");
		toggleAllCheckboxes(true);
	}
	else{
		sessionStorage.setItem("checkAllItemAssignment", "false");
		toggleAllCheckboxes(false);
	}
}


function toggleAllCheckboxes(flag){
	var selectedPageKeys = document.getElementsByName("selectedPageKeys");
	for (var i = 0; i < selectedPageKeys.length; i++) {
		selectedPageKeys[i].checked = flag;
	}
} */

function hideElement(){
	if(document.getElementsByName("value(owner)")[0].value!='REGIONAL'){
		if(document.getElementsByName("values(regions)").length>0)
			document.getElementsByName("values(regions)")[0].disabled = true;
	}
	if(document.getElementById("responsibilityList").value=='REGIONAL'){
		document.getElementById('regionList').style.display = '';
	}
}

function showRegions(value){
	if(value=='REGIONAL'){
		document.getElementById('regionList').style.display = '';	
	}else{
		document.getElementById('regionList').style.display = 'none';
	}
}

function goAssignOwner()
{	
	if(hasRegionSelected()){
		var callback = function() {
			document.forms[0].action="assignOwnerItemManagement.do"
			document.forms[0].submit();
			showWaitBusy();
		}
			callback();
	}
}

function hasRegionSelected(){
	if(document.getElementById("responsibilityList").value=='REGIONAL'){
		if(document.getElementsByName("region")[0].value==''){
			showOkMessageBox('OK','WARN',"<fmt:message key='warn.select.region.for.regionalResponsibility'/>","<fmt:message key='msg.warn'/>",function() {});
			return false;
		}
	}
	return true;
}

function goUnassignOwner()
{
	var callback = function() {
		document.forms[0].action="unassignOwnerItemManagement.do"
		document.forms[0].submit();
		showWaitBusy();
	}
		callback();
}

function goReassignOwner() {
	document.forms[0].action = "reAssignItemAssignment.do";
	document.forms[0].assignmentResponsibility.value = '${itemManagementForm.assignmentResponsibility}';
	document.forms[0].submit();
	showWaitBusy();	
}

function goSetManaged()
{
	var callback = function() {
		document.forms[0].action="setManagedItemManagement.do"
		document.forms[0].submit();
		showWaitBusy();
	}
		callback();
}

$(document).ready(function() 
{
	var owner = "<%=request.getAttribute("owner")%>";
	$('input[name="value(owner)"]').val(owner);
	
	/* if(sessionStorage.getItem("checkAllItemAssignment") == "true"){
		toggleAllCheckboxes(true);
	} */
});

function init()
{
	hideElement();
	/* var array = $("#grid-result .eto-grid .eto-grid-frozen table thead tr th");
	if(array.length > 0){
		$("#expand_more").css("display","inline-block");
		var fullHTML = array[1].innerHTML + "&nbsp;" +$("#expand_more").html();
		array[1].innerHTML = fullHTML;	
	} */
	
	<c:if test="${itemManagementForm.unassignPopup == true }">
	setTimeout(function() {unassignResponsibilities();},100);
	$("input[name='unassignPopup']").val(false);
	</c:if>
}

function unassignResponsibilities() {
	var mod = new eto.Modal({
		el : document.querySelector('#unassign-modal')
	});
	mod.open();		
	
}
</script>

<body onload="init()">

<input type="hidden" name="unassignPopup">

	<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
		<div style="font-weight: bold;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					 <li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
	</div>

	<form name="itemManagementForm" action="submitItemManagementSearch" method="POST">

		<script type="text/javascript">
			var responsibilityValue;
			var jsonColumn =  '${itemManagementForm.columns}';
			var gridColumns = [];

			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.itemManagement.grid.thead')}"/>
			<c:forEach var="col" items="${columnHeader}" varStatus="count">
				<c:if test="${col != 'item.category' }">
					gridColumns.push('<fmt:message key="${col}"/>');
				</c:if>
				<c:if test="${col == 'item.category' }">
					var colName = '<fmt:message key="item.category"/>  (<fmt:message key="mdm.managed"/>)';
					gridColumns.push(colName);
				</c:if>
			</c:forEach>
			<c:if test="${e2ofn:getConfigValue('pcm.mdm.item.managedFlag') == 'true' }">
				gridColumns.push('<fmt:message key="mdm.managed"/>');
			</c:if>

			var gridRows = [];
			<c:forEach var="row" items="${itemManagementForm.searchResult.values}" varStatus="rowCount">
			var row = {};
			row['checkboxValue'] = "<c:out value='${row.values[9]}'/>";
			row['<fmt:message key="item.audit.history"/>']='<button type="button" class="eto-icon-btn" title="History" style="color: #468293;" id="historyButton" onclick="javascript:showItemAssignmAuditHistory(\'${row.values[0]}\')"><i class="md-icon">history</i></button>';          
			var itemNumberLink = '<a href="#" onClick="openPopOver(\'${row.values[0]}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${row.values[1]}" removeColon="true"/></a>';
			row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
			
			var itemTypeVal = "<c:out value='${row.values[10]}'/>";
			var shownItemType;
			if (itemTypeVal == 'I') {
				shownItemType = '<fmt:message key="item.item"/>';
			} else if (itemTypeVal == 'CFG') {
				shownItemType = '<fmt:message key="item.cfgGroup"/>';
			} else if (itemTypeVal == 'M') {
				shownItemType = '<fmt:message key="item.mfgItem"/>';
			} else if (itemTypeVal == 'S') {
				shownItemType = '<fmt:message key="item.supplierItem"/>';
			} else if (itemTypeVal == 'PI') {
				shownItemType = '<fmt:message key="item.phantomItem"/>';
			} else {
				shownItemType = itemTypeVal;
			}
			row['<fmt:message  key="item.type"/>'] = shownItemType;
			
			row['<fmt:message  key="item.itemDescription"/>'] = '<c:out value="${e2ofn:abbreviate(row.values[2],30)}"/>';
			row[colName] ="<c:out value='${row.values[6]} (${row.values[8]})'/>";
			row['<fmt:message  key="business.name"/>'] = "<c:out value='${row.values[3].businessEntityName}'/>";
			row['<fmt:message  key="business.businessIdentifier"/>'] = "<c:out value='${row.values[3].businessEntityIdentifier}'/>";
			<c:if test="${e2ofn:getConfigValue('pcm.mdm.item.managedFlag') == 'true' }">
					row['<fmt:message  key="mdm.managed"/>'] = '<c:out value="${row.values[7]}"/>';
			</c:if>
			row['<fmt:message key="label.daysOld"/>'] = "<c:out value='${row.values[4]}'/>";
			responsibilityValue = "<c:out value='${row.values[5]}'/>";
			if(responsibilityValue.trim() == '-'){
				row['<fmt:message key="as.responsibility"/>'] = '';	
			}else{
				row['<fmt:message key="as.responsibility"/>'] = responsibilityValue;
			}
			row['<fmt:message  key="item.dataSource"/>'] = "<c:out value='${row.values[12]}'/>";
			row['<fmt:message  key="as.lastRevChangeDate"/>'] = "<c:out value='${row.values[13]}'/>";
			gridRows.push(row);
			</c:forEach>
		</script>

		<e2ot:searchContainerControl form="${itemManagementForm}"
			searchFields="${itemManagementForm.allParameters}"
			formName="itemManagementForm"
			resultTableId="itemMgmtSearchResultTable"
			showFilterCollapsed="${itemManagementForm.filterAreaCollapsed}"
			showFilter="${itemManagementForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${itemManagementForm}"
			formName="itemManagementForm"
			resultTableId="itemMgmtSearchResultTable" showHideMenu="false"
			title="Items" showTitle="true" showOrderMenu="false" />

		<c:if
			test="${!empty itemManagementForm.searchResult.values && e2ofn:hasAccess(appContext, 'ITEM_ASSIGNMENT', 'Save')}">
			<div id="butttonsDiv" class="footer"
				style="width: ${itemManagementForm.pagingEnabled ? '50%' : '100%'}">
				<nav class="eto-form__btns" style="margin-left: 15px">
					<div class="eto-btn-group" style="margin-top: 15px">
						<c:if
							test="${e2ofn:hasAccess(appContext, 'ITEM_ASSIGNMENT', 'Assign') || e2ofn:hasAccess(appContext, 'ITEM_ASSIGNMENT', 'AssignSelf')}">
							<button type="button" id="assignButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<fmt:message key="button.assign_responsibility" />
							</button>
							<button type="button" id="unAssignButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<fmt:message key="button.unassign_responsibility" />
							</button>
						</c:if>
						<%-- <c:if
							test="${e2ofn:getConfigValue('pcm.mdm.category.managedFlag')}">
							<button type="button" id="setButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<bean:message key="button.set_managedby" />
							</button>
						</c:if> --%>
					</div>
				</nav>
			</div>
		</c:if>


		<!-- <div id="expand_more" style="display: none;">
			<span class="eto-dropdown" data-anchor-x="left"
				data-anchor-y="bottom" id="dropdown-example"> <span
				class="eto-dropdown__toggle"><span class="md-icon">expand_more</span>
			</span>
				<ul class="eto-dropdown__menu">
					<li><label id="checkUncheckAllLabel" style="cursor: pointer;"
						onclick="toggleCheckAll()">Check all on all pages</label></li>
				</ul>
			</span>
		</div> -->

		<div class="eto-modal" id="assignModal"
			style="width: 100%; overflow-y: scroll;">
			<div class="eto-modal__content"
				style="height: auto; width: auto; margin-top: 300px; margin-bottom: 250px; max-height: max-content; display: inline; max-height: none;">
				<header class="eto-modal__header">
					<span><label id="modalHeader"></label></span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<div class="eto-messageblock" data-message-type="warn"
						id="infoMesssage">
						<div class="eto-messageblock__body">
							<span id="selectedLabel"><label id="rowCount"
								style="font-weight: bold;"></label>&nbsp;<b>selected</b><br></span>
							Are you sure you want to make changes for the selected
							commodities
						</div>
					</div>

					<div id="checkAllDiv" style="margin-top: 15px;">
						<label class="eto-switch"> <input
							class="eto-switch__field" type="checkbox" id="checkAll"
							name="applyToAll" value="true"> <span
							class="eto-switch__box"></span> <span class="eto-switch__label"><b>Apply
									to ALL results on ALL pages</b></span>
						</label>
					</div>

					<div id="assignToDiv" style="display: none; margin-top: 15px">
						<c:set var="userId" value="${itemManagementForm.assignmentUserId}" />
						<c:if test="${empty userId}">
							<c:set var="userId" value="${appContext.currentUser.userId}" />
						</c:if>
						<div class="eto-autocomplete" id="assignmentUserIdAutoComplete"
							style="width: 300px;" >
							<label class="eto-autocomplete__label"><fmt:message
									key="as.assignTo" /></label>
							<c:choose>
								<c:when
									test="${e2ofn:hasAccess(appContext, 'ITEM_ASSIGNMENT', 'Assign')}">
									<input class="eto-autocomplete__field" type="text"
										 placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>' id="assignmentUserId"
										name="assignmentUserId" value="${userId}" />
								</c:when>
								<c:otherwise>
									<input type="text" readonly="readonly" value="${userId}" />
								<input type="hidden" name="assignmentUserId" value="${userId}" />
								</c:otherwise>
							</c:choose>

							<div class="eto-autocomplete__message"></div>
							<div class="eto-results"></div>
						</div>
					</div>

					<div id="responsibilityDiv" class="eto-select"
						style="display: none; margin-top: 15px; width: 300px;">
						<label class="eto-select__label"><fmt:message
								key="as.responsibility" /></label>
						<div class="eto-select__field-container">
							<c:set var="responsibility"
								value="${itemManagementForm.assignmentResponsibility}" />
							<c:choose>
								<c:when
									test="${e2ofn:hasAccess(appContext, 'ITEM_ASSIGNMENT', 'Assign') || e2ofn:hasAccess(appContext, 'ITEM_ASSIGNMENT', 'AssignSelf')}">
									<select class="eto-select__field"
										name="assignmentResponsibility" id="responsibilityList"
										onchange="showRegions(this.value);dataChanged();">
										<c:forEach var="responsibility"
											items="${itemManagementForm.responsibilities}">
											<option value="${responsibility.responsibilityKey}">${responsibility.responsibilityName}</option>
										</c:forEach>
									</select>
								</c:when>
								<c:otherwise>
									<c:out value="${responsibility}" />
									<input type="hidden" name="assignmentResponsibility" value="${responsibility}" />
								</c:otherwise>
							</c:choose>
						</div>
					</div>

					<div class="eto-combobox" id="regionList"
						style="display: none; margin-top: 15px; width: 300px;">
						<c:set var="regions" value="${itemManagementForm.region}"></c:set>
						<label class="eto-combobox__label"><fmt:message
								key="contact.region" /></label>
						<div class="eto-combobox__container">
							<div class="eto-combobox__gray-container">
								<div class="eto-combobox__field-container" role="presentation"
									aria-hidden="true">
									<input class="eto-combobox__field" type="text"
										placeholder="Select a region">
									<div class="eto-combobox__btn">
										<button type="button" class="eto-btn">
											<span class="md-icon">expand_more</span>
										</button>
									</div>
								</div>
								<div class="eto-combobox__tags-container">
									<div class="eto-combobox__tags"></div>
									<button type="button" class="eto-combobox__clear"></button>
								</div>
								<div class="eto-combobox__show-selected" role="presentation"
									aria-hidden="true">
									<a href="javascript:void(0)">View all tags <span
										class="eto-badge" data-type="info"></span></a>
								</div>
							</div>
						</div>
						<select name="region" multiple>
							<c:forEach var="site" items="${itemManagementForm.siteList}">
								<option value="${site.siteKey}">${site.siteDescription}</option>
							</c:forEach>
						</select>
						<div class="eto-results"></div>
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
									items="${itemManagementForm.managedFlagOptions}">
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
						<button id="dynamicButton" class="eto-btn eto-btn--primary" type="button">
							<label id="dynamicButtonLabel" style="cursor: pointer;"></label>
						</button>
					</div>
				</footer>
			</div>
		</div>
		<div class="eto-modal" id="unassign-modal">
		<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
		  <header class="eto-modal__header">
		    <span>Warning</span>
		  </header>
		  <section class="eto-modal__body">
		    <p>
		      <fmt:message key="errors.responsibility_assigned_popup">
		      <fmt:param value="${itemManagementForm.alreadyAssignedResponsibility}" />
		      <fmt:param value="${itemManagementForm.alreadyAssignedUser}" />
		      <fmt:param value="${itemManagementForm.item}" />
		      <fmt:param value="${itemManagementForm.assignmentUserId}" />	      
	            </fmt:message>
		    </p>
		  </section>
		  <footer class="eto-modal__footer">
		  <button class="eto-btn eto-btn--primary" type="button" id = "unAssignButtonFromAssign"
								onclick="javascript:goReassignOwner()" data-modal-close>Yes</button>
		  <button class="eto-btn" type="button" data-modal-close>No</button>
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
		el : document.querySelector('#assignmentUserIdAutoComplete'),add: true
	});
	var regionListCombo = new eto.Combobox({ el: document.querySelector('#regionList') });

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
	if(grid != null){
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
	}

	var rowCountGlobal = 0;
	function openModal(button) {
		var id = button.id;
		var rowCount = 0;
		var modalHeader = "";
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
			$("#responsibilityDiv").css('display', 'block');
		} else if (id == "unAssignButton") {
			modalHeader = "Unassign Responsibility";
			dynamicMethod = goUnassignOwner;
			dynamicButtonLabel = "Yes, Unassign";
			$("#assignToDiv").css('display', 'none');
			$("#managedByDiv").css('display', 'none');
			$("#responsibilityDiv").css('display', 'none');
		} 
		
		/* else {
			modalHeader = "Set Managed By";
			dynamicMethod = goSetManaged;
			dynamicButtonLabel = "Set";
			$("#assignToDiv").css('display', 'none');
			$("#managedByDiv").css('display', 'block');
			$("#responsibilityDiv").css('display', 'none');
		} */

		$("#modalHeader").html(modalHeader);
		$("#rowCount").html(rowCount);
		rowCountGlobal = rowCount;
		if($("#checkAll").attr("checked")){
			$("#rowCount").html(${itemManagementForm.totalRows});	
		}
		$('#dynamicButton').replaceWith($('#dynamicButton').clone());
		document.getElementById('dynamicButton').addEventListener("click",
				dynamicMethod)
		$("#dynamicButtonLabel").html(dynamicButtonLabel);

		modal.open();
	}
	
	/* var dropdown = new eto.Dropdown({ el: document.querySelector('#dropdown-example') }); */
	
	$("#checkAll").change(function() {
	    if(this.checked) {
	    	// $("#selectedLabel").css('display','none');
	    	$("#rowCount").html(${itemManagementForm.totalRows});
	    }
	    else{
	    	// $("#selectedLabel").css('display','block');
	    	$("#rowCount").html(rowCountGlobal);	
	    }
	});
</script>
   <%@ include file="../fullModal.jspf"%>
</html>