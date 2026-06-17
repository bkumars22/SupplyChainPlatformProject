<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="SA-Detail" />
<title>Supply Allocation</title>
<script type="text/javascript">
	function handleDataChanged() {
		document.forms[0].unsavedData.value = 'true';
		var msgArea = document.getElementById('unsavedDataMsg');
		if (msgArea != null) {
			msgArea.innerText = '<fmt:message key="info.unsaved_data"/>';
		}
	}

	function canLeavePage(callback) {
		if (document.forms[0].unsavedData.value == 'true') {
			showYesNoMessageBox('YES NO', 'WARN',
					"<fmt:message key='warn.changes_not_saved_yes_no'/>",
					"<fmt:message key='msg.warn'/>", callback);
			return false;
		}
		return true;
	}

	function validateData() {
		if (isFieldEmpty('startDate')) {
			showOkMessageBox('OK', 'WARN',
					"<fmt:message key='errors.field_required'/>",
					"<fmt:message key='msg.warn'/>", function() {
						document.forms[0].programStartDate.focus();
					});
			return false;
		}

		return true;
	}

	function goBack() {
		if (canLeavePage(goBackCallback)) {
			goBackCallback();
		}
	}

	function goBackCallback() {
		document.forms[0].action = "${fn:escapeXml(supplyAllocationForm.backAction)}";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}

	function newPeriodSelected(date) {
		var target = document.getElementById("fromDateId");
		var options = target.options;
		var found = -1;
		for (var idx = 0; idx < options.length; idx++) {
			if (options[idx].value == date) {
				found = idx;
				break;
			}
		}
		if (found == -1) {
			var opt = document.createElement("OPTION");
			opt.text = date;
			opt.value = date;
			//opt.selected = true;
			options.add(opt);
		}
		target.value = date;
		if (found == -1) {
			goNew();
		}
	}

	function newPeriod() {
		showCalendar('newFromDate');
	}

	function checkAllocAmount(field) {
		var validCallback = function() {
			var number = parseFloat(field.value);
			if (number > 100 || number < 0.0) {
				var msg = "<fmt:message key="errors.range"/>";
				msg = msg.replace('{0}', field.value);
				msg = msg.replace('{1}', '0');
				msg = msg.replace('{2}', '100');
				showOkMessageBox('OK', 'ERROR', msg,
						"<fmt:message key='msg.error'/>", function() {
							field.focus();
						});
			}
			calculateTotal();
		}
		checkNumericField(field, false, validCallback);
	}

	function calculateTotal() {
		var index = 0;
		var field = document.getElementById('allocationAmount' + index);
		var total = 0.0;
		var cost = 0.0;
		while (field != null && field != undefined) {
			var val = parseFloat(field.value);
			if (isNaN(val) == false) {
				val = roundDecimal(val, 2);
				total += val;
			}
			index++;
			field = document.getElementById('allocationAmount' + index);
		}
		document.getElementById('allocationTotal').innerText = roundDecimal(
				total, 2);
	}

	function checkAndSave() {
		while (field != null && field != undefined) {
			if (number > 100 || number < 0.0) {
				var msg = "<fmt:message key="errors.range"/>";
				msg = msg.replace('{0}', field.value);
				msg = msg.replace('{1}', '0');
				msg = msg.replace('{2}', '100');
				showOkMessageBox('OK', 'ERROR', msg, "<fmt:message key='msg.error'/>", function () {
					field.focus();
				});
			}
			index++;
			field = document.getElementById('allocationAmount' + index);
		}
	}

	function goSaveAndContinue() {
		var index = 0;
		var field = document.getElementById('allocationAmount' + index);
		var number = parseFloat(field.value);
		if (number > 100 || number < 0.0) {
			checkAndSave();
		} else {
			document.forms[0].action = "saveAndContinueSupplyAllocation";
			document.forms[0].submit();
		}
	}

	function goSave() {
		var index = 0;
		var field = document.getElementById('allocationAmount' + index);
		var number = parseFloat(field.value);
		if (number > 100 || number < 0.0) {
			checkAndSave();
		} else {
			document.forms[0].action = "saveSupplyAllocation";
			document.forms[0].submit();
		}
	}

	function goReload() {
		if (canLeavePage(goReloadCallback)) {
			goReloadCallback();
		}
	}

	function goReloadCallback() {
		document.forms[0].action = "reloadSupplyAllocation";
		document.forms[0].submit();
	}

	function goRead() {
		if (canLeavePage(goReadCallback)) {
			goReadCallback();
		}
	}

	function goReadCallback() {
		document.forms[0].action = "viewSupplyAllocation";
		document.forms[0].submit();
	}

	function goNew() {
		if (canLeavePage(goNewCallback)) {
			goNewCallback();
		}
	}

	function goNewCallback() {
		document.forms[0].action = "newPeriodSupplyAllocation";
		document.forms[0].submit();
	}

	function goCancel() {
		if (canLeavePage(goCancelCallback)) {
			goCancelCallback();
		}
	}

	function goCancelCallback() {
		document.forms[0].action = "home";
		document.forms[0].submit();
	}

	function goCopy() {
		if (isFieldEmpty("toDate") == true) {
			showOkMessageBox('OK', 'ERROR',
					"<fmt:message key='errors.sa.no_end_date'/>",
					"<fmt:message key='msg.error'/>", null);
		} else {
			document.forms[0].action = "copySupplyAllocation";
			document.forms[0].submit();
		}
	}

	function goDelete() {
		showYesNoMessageBox('YES NO', 'WARN',
				"<fmt:message key='warn.delete_object'/>",
				"<fmt:message key='msg.warn'/>", function() {
					document.forms[0].action = "deleteSupplyAllocation";
					document.forms[0].submit();
				});

	}

	function showItemWindow(key) {
		showPopupWindow('viewItemDetails?itemKey=' + key, 'item', true);
	}

	function onContextSelected(finderValues) {
		if (finderValues.length > 0) {
			document.getElementById('contextName').value = finderValues[0][1];
			goRead();
		}
	}

	function handleContextChanged() {
		document.getElementById('contextName').value = '';
		document.forms[0].contextKey.value = '';

		var contextTypeValue = document.getElementById('contextType').value;

		var contextNameTD = document.getElementById('contextNameTD');

		var contextNameHtml = "<INPUT type=\"text\" name=\"contextName\" value=\"\" readonly=\"readonly\" id=\"contextName\" class=\"inputField searchField\">";

		if (contextTypeValue == "ITEM") {
			contextNameHtml = contextNameHtml
					+ "<A href=\"javascript:void 0\" onclick=\"javascript:doFinderPopup('EnterpriseItemFinder',document.forms[0].contextKey,'onContextSelected',null,'false')\"><img hspace=\"1\" src=\"./skins/e2-modern/images/search.gif\" border=\"0\"></A>";
		} else if (contextTypeValue == "PLATFORM") {
			contextNameHtml = contextNameHtml
					+ "<A href=\"javascript:void 0\" onclick=\"javascript:doFinderPopup('PlatformFinder',document.forms[0].contextKey,'onContextSelected',null,'false')\"><img hspace=\"1\" src=\"./skins/e2-modern/images/search.gif\" border=\"0\"></A>";
		}

		contextNameTD.innerHTML = contextNameHtml;

		goRead();
	}
	
	function goSubmitDate() {
		
			var startDate =$('#startDate').val();
			var endDate = $('#endDate').val();
		
		document.forms[0].action = "viewSupplyAllocation";
		document.forms[0].submit();
	}
	function callClear(element){
		 $("#"+element).val('');
		 document.forms[0].action = "viewSupplyAllocation";
		document.forms[0].submit();
	}  
	
	function openDateModal() {
  // Optional: Reset datesor log logic
  document.getElementById('newStartDate').value = '';
  document.getElementById('newEndDate').value = '';
}	

function createAllocation() {
  var startDate = $('#newStartDate').val();
  var endDate = $('#newEndDate').val();
  if (!startDate ) {
    alert("Please select Start Date .");
    return;
  }

  if (new Date(startDate) > new Date(endDate)) {	
    alert("End Date should be after Start Date.");
    return;
  }

  // Proceed with allocation logic
		document.forms[0].action = "newPeriodSupplyAllocation";
		document.forms[0].submit();
  document.querySelector('[data-modal-close]').click(); // Close modal
}


	

</script>
</head>
<style type="text/css">
i.md-icon {
	cursor: pointer;
	/* FC 05 */
	/*color: #468293;*/
	color: #417F90 !important;
}
 tr#allocTotalId>td {
background: #f2f2f2;
border: 1px #d3e2e6 solid;
} 
 table#saDetailTable {
    width: 1456px !important;
}
.eto-select__field-container {
    width: 128px;
}
</style>
<body>
<%@ include file="fullModal.jspf"%>
	<c:set var="readOnly"
		value="${!e2ofn:hasAccess(appContext, 'SUPPLY_ALLOC', 'Save')}" />
	<fmt:message var="saTitle" key="sa.header.title" />
	<fmt:message var="saDetailTitle" key="sa.detail.title" />
	<fmt:message var="clearTitle" key="button.title.clear" />
	<fmt:message var="pushdownTitle" key="button.title.pushdown" />
	<c:set var="lineErrors" value="${supplyAllocationForm.lineErrors}" />
	<e2o:errors maxErrors="4" styleId="errors" />
	<e2o:form action="/saveAndContinueSupplyAllocation">
		<html:hidden property="unsavedData" />
		<html:hidden property="backAction" />
		<c:set var="disableFiscal"
	value="${e2ofn:getConfigValue('pcm.supplierAllocation.fiscalCalendarValidation.enabled')}"/>
		<input type="hidden" name="preserveSearchValues" />

		<div class="container" title="${saTitle}" id="saHeader">
			<span id="unsavedDataMsg"><c:if
					test="${supplyAllocationForm.unsavedData}">
					<fmt:message key="info.unsaved_data" />
				</c:if></span>

<!-- FC 05 -->
			<div class="row">
				<div class="col-sm-10" style="align: left">
					<h1>${supplyAllocationForm.selectedItem.itemNumber}</h1>
				</div>
				<div class="col-sm-2" style="float: right">
					<i class="md-icon" style="margin-left: 8px; float: right;"
						onclick="javascript:showAuditHistory('${fn:escapeXml(supplyAllocationForm.selectedItemKey)}','PcmSupplierAllocation')"
						title="History">history</i> <i class="md-icon"
						style="margin-left: 8px; margin-right: 8px; float: right;"
						onclick="javascript:goCopy()" title="Copy">content_copy</i><i
						class="md-icon" style="margin-right: 8px; float: right;"
						onclick="javascript:goRead()" title="Refresh">refresh</i>
				</div>
			</div>

			<div class="eto-well eto-expand" id="expand-container"
				style="padding: unset ! important;">
				<div class="row margin-bottom-xs-2">
					<div class="col-sm-3">
						<div class="eto-input"
							style="margin: 30px; display: inline-block; margin-right: 40px; padding-bottom: 100%; margin-bottom: -100%;">
							<label class="eto-input__label"> <fmt:message
									key="sa.item" />
							</label>
							<div class="eto-input__container">
								<b> <html:hidden property="selectedItemKey"
										styleId="selectedItemKey" /> <a
									href='javascript:showPopup("viewItemDetails?itemKey=${supplyAllocationForm.selectedItem.itemKey}")'>
										<c:out value="${supplyAllocationForm.selectedItem.itemNumber}" />
								</a>
								</b>
							</div>
						</div>
					</div>

					<div class="col-sm-3">
						<div class="eto-input"
							style="margin: 30px; display: inline-block; margin-right: 40px; padding-bottom: 100%; margin-bottom: -100%;">
							<label class="eto-input__label"> <fmt:message
									key="item.itemDescription" />
							</label>
							<div class="eto-input__container">
								<b> <html:hidden property="selectedItemKey"
										styleId="selectedItemKey" /> <c:out
										value="${supplyAllocationForm.selectedItem.description}" />
								</b>
							</div>
						</div>
					</div>

					<div class="col-sm-3">
						<div class="eto-input"
							style="margin: 30px; display: inline-block; margin-right: 40px; padding-bottom: 100%; margin-bottom: -100%;">
							<label class="eto-input__label" style="align: top"> <fmt:message
									key="mdm.managed" />
							</label>
							<div class="eto-input__container">
								<b> 
								<c:if test="${!empty supplyAllocationForm.selectedItem.derivedManagedFlag}">
										<c:choose>
											<c:when
												test="${supplyAllocationForm.selectedItem.derivedManagedFlag != 'none'}">
												<span>${supplyAllocationForm.selectedItem.derivedManagedFlag} Managed</span>
											</c:when>
											<c:otherwise>
												<span>Not Set</span>
											</c:otherwise>
										</c:choose>
									</c:if>
								</b>
							</div>
						</div>
					</div>
					<c:if test="${disableFiscal}">
					<div class="col-sm-3">
						<div class="eto-input"
							style="margin: 30px; display: inline-block; margin-right: 40px; padding-bottom: 100%; margin-bottom: -100%;">
							<label class="eto-input__label"> <fmt:message
									key="calendar.period" />
							</label>
							<div class="eto-input__container" style="width:">
								<b> <select class="eto-select__field" name="periodType"
									id="periodType"
									style="border-radius: 3px; background-image: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.08));">
										<c:forEach var="typeList"
											items="${supplyAllocationForm.periodTypes}">
											<option value="${typeList}"> 
<fmt:message key="calendar.${typeList}"/>
	  </option>
										</c:forEach>
								</select>
								</b>
							</div>
						</div>
					</div>
					</c:if>

					
				</div>
			</div>
			<%-- <e2i2:formtable id="supplyAllocation" >
<col style="font-weight:bold;width:150px"/>
<col/>
<col style="font-weight:bold;width:150px"/>
<col/>
<tr>
<e2i2:formlabel required="yes"><fmt:message key="sa.item"/></e2i2:formlabel>
<td>
<html:hidden property="selectedItemKey" styleId="selectedItemKey"/>
<a href='javascript:showPopup("viewItemDetails?itemKey=${supplyAllocationForm.selectedItem.itemKey}")'>
<c:out value="${supplyAllocationForm.selectedItem.itemNumber}"/>
</a>
</td>
<e2i2:formlabel required="yes"><fmt:message key="calendar.period"/></e2i2:formlabel>
<td valign="top" nowrap align="left">
<html:select property="periodType" styleId="periodType" styleClass="inputField"
	disabled="${readOnly}">
	<c:forEach var="typeList" items="${supplyAllocationForm.periodTypes}">
		<html:option value="${typeList}"><fmt:message key="calendar.${typeList}"/></html:option>
	</c:forEach>
</html:select>
</td>
</tr>
<tr>
<e2i2:formlabel required="no"><fmt:message key="item.itemDescription"/></e2i2:formlabel>
<td><c:out value="${supplyAllocationForm.selectedItem.description}"/></td>
<e2i2:formlabel required="no"><fmt:message key="mdm.managed"/></e2i2:formlabel>
<td>
<c:if test="${!empty supplyAllocationForm.selectedItem.derivedManagedFlag}">
<fmt:message key="mdm.managed.${supplyAllocationForm.selectedItem.derivedManagedFlag}"/>
</c:if>
</td>
</tr>
<tr>
<e2i2:formlabel required="yes"><fmt:message key="sa.startDate"/></e2i2:formlabel>
<td valign="top" nowrap align="left">
<input type="hidden" id="newFromDate" onchange="newPeriodSelected(this.value)"/>
<html:select property="fromDate" styleId="fromDate" styleClass="inputField"
	disabled="${readOnly}" onchange="goRead()">
    <c:forEach var="rt" items="${supplyAllocationForm.periods}">
		<fmt:formatDate var="dt" value="${rt}" pattern="${appContext.currentDateFormat}"/>
		<html:option value="${dt}">${dt}</html:option>
    </c:forEach>
</html:select>

</td>
<e2i2:formlabel required="yes"><fmt:message key="sa.endDate"/></e2i2:formlabel>
<td><!-- 
	<html:text property="toDate" styleClass="inputField"
    styleId="toDate" 
	disabled="${readOnly}"/>
   <e2ot:calendarControl bindToFieldId="toDate" firstDayOfWeek="6"/>
    -->   
<html:select property="toDate" styleId="toDate" styleClass="inputField"
	disabled="${readOnly}" >
    <c:forEach var="rt" items="${supplyAllocationForm.availablePeriods}">
		<fmt:formatDate var="dt" value="${rt}" pattern="${appContext.currentDateFormat}"/>
		<html:option value="${dt}">${dt}</html:option>
    </c:forEach>
</html:select>

</td>
</tr>
<c:if  test="${e2ofn:getConfigValue('pcm.supplierAllocation.incontext')}">
<tr>
<e2i2:formlabel required="yes"><fmt:message key="sa.contextType"/></e2i2:formlabel>
<td>
<html:select property="contextType" styleId="contextType" styleClass="inputField"
	disabled="${readOnly}" onchange="handleContextChanged()">
	<html:option value=""></html:option>
    <html:option value="ITEM">Item</html:option>
    <html:option value="PLATFORM">Platform</html:option>
</html:select>
</td>
<html:hidden property="contextKey" styleId="contextKey"/>
<e2i2:formlabel required="yes"><fmt:message key="sa.contextName"/></e2i2:formlabel>
<td id="contextNameTD">
<html:text property="contextName" styleId="contextName" readonly="true" value="${supplyAllocationForm.contextName}" styleClass="inputField searchField"/>
<c:if test="${supplyAllocationForm.contextType == 'ITEM'}">
<e2i2:img src="/search.gif" onclick="javascript:doFinderPopup('EnterpriseItemFinder',document.forms[0].contextKey,'onContextSelected',null,'false')"/>
</c:if>
<c:if test="${supplyAllocationForm.contextType == 'PLATFORM'}">
<e2i2:img src="/search.gif" onclick="javascript:doFinderPopup('PlatformFinder',document.forms[0].contextKey,'onContextSelected',null,'false')"/>
</c:if>
</td>
</tr>
</c:if>
<tr>
</tr>
</e2i2:formtable>
<e2i2:footer>
<div class="buttonbar">
<e2i2:buttonbar>
<e2i2:button onclick="javascript:showAuditHistory('${fn:escapeXml(supplyAllocationForm.selectedItemKey)}','PcmSupplierAllocation')">
	<fmt:message key="button.history"/>
</e2i2:button>
<e2i2:button onclick="javascript:goRead()"><fmt:message key="button.refresh"/></e2i2:button>
<c:if test="${!supplyAllocationForm.selectiveCopyEnabled}">
<e2i2:buttonbardivider/>
<e2i2:button onclick="javascript:goCopy()" id="copyButton" ><fmt:message key="button.copy"/></e2i2:button>
</c:if>

</e2i2:buttonbar>
</div>
</e2i2:footer> --%>
			<div class="row">
				<div class="col-sm-6">
					<c:if test="${e2ofn:hasAccess(appContext, 'SUPPLY_ALLOC', 'Save')}">
						<%-- <button class="eto-btn" type="button"
							onclick="javascript:newPeriod()" id="newButton">
							<fmt:message key="button.new" />
						</button> --%>
						<button id="newButton" class="eto-btn eto-btn--primary"
								data-modal="#basic-modal-example"
								onclick="${!disableFiscal ? 'openDateModal()' : ''}">
								<fmt:message key="button.new" />
							</button>
						<c:if test="${supplyAllocationForm.selectiveCopyEnabled}">
							<button class="eto-btn" type="button"
								onclick="javascript:goCopy()" id="copyButton">
								<fmt:message key="button.copy" />
							</button>
						</c:if>
					</c:if>
					<c:if
						test="${e2ofn:hasAccess(appContext, 'SUPPLY_ALLOC', 'Delete')}">
						<button class="eto-btn" type="button"
							onclick="javascript:goDelete()">
							<fmt:message key="button.delete" />
						</button>
					</c:if>
					<c:if test="${e2ofn:hasAccess(appContext, 'SUPPLY_ALLOC', 'Save')}">
						<button class="eto-btn" onclick="javascript:goReload()"
							id="reloadButton">
							<fmt:message key="button.reload" />
						</button>
					</c:if>
				</div>
				
				
			
				<c:choose>
					<c:when test="${!disableFiscal}">
						<div class="row col-sm-6">
							<div class="col-sm-4">
								<div class="eto-input" id="datepickStartDate">
									<div class="eto-input__field-container">
										<label class="eto-select__label">Start Date</label>
										<fmt:formatDate var="rd"
											value="${supplyAllocationForm.startDate}"
											pattern="${appContext.currentDateFormat}" />
										<input class="eto-input__field" type="text" id="startDate"
											name="startDateAsString" readonly value="${rd}"
											data-format="${fn:toUpperCase(appContext.currentDateFormat)}"
											onchange="goSubmitDate()">
										<c:if test="${!lineReadOnly}">
											<span class="eto-input__addon" style="padding-top: 3rem;"><i
												class="md-icon"
												onclick="javascript:showCalendar('startDate')">event</i> <i
												class="md-icon" onclick="callClear('startDate')">clear</i> </span>
										</c:if>
									</div>
								</div>
								<script type="text/javascript">
								new eto.TextInput({el : document.querySelector('#datepickStartDate')});																					
							</script>
							</div>

							<div class="col-sm-4">
								<div class="eto-input" id="datepickEndDate">
									<div class="eto-input__field-container">
										<label>End Date</label>
										<fmt:formatDate var="rd"
											value="${supplyAllocationForm.endDate}"
											pattern="${appContext.currentDateFormat}" />
										<input class="eto-input__field" type="text" id="endDate"
											name="endDateAsString" readonly value="${rd}"
											data-format="${fn:toUpperCase(appContext.currentDateFormat)}"
											onchange="goSubmitDate()">
										<c:if test="${!lineReadOnly}">
											<span class="eto-input__addon" style="padding-top: 3rem;"><i
												class="md-icon" onclick="javascript:showCalendar('endDate')">event</i>
												<i class="md-icon" onclick="callClear('endDate')">clear</i>
											</span>
										</c:if>
									</div>
								</div>
								<script type="text/javascript">
								new eto.TextInput({el : document.querySelector('#datepickEndDate')});										
								
							</script>
							</div>
						</div>
					</c:when>
					<c:otherwise>
        <div class="col-xs-0 margin-top-xs-5" style="padding-left: 70px;">
					<span><label class="eto-input__label">Allocation
							Period</label></span>
				</div>
				<div class="col-xs-1 margin-top-xs-4">
					<div class="eto-select" id="select-example-1"
						${readOnly==true ? disabled:''}>
						<div class="eto-select__field-container">
							<select class="inputField" name="fromDate" id="fromDateId"
								onchange="goRead()">
								<c:forEach var="rt" items="${supplyAllocationForm.periods}">
									<fmt:formatDate var="dt" value="${rt}"
										pattern="${appContext.currentDateFormat}" />
									<option value="${dt}"
										${dt == supplyAllocationForm.fromDate ? "selected":''}>${dt}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<script>
						new eto.SelectInput({
							el : document.querySelector('#fromDateId')
						});
					</script>
				</div>
				<div class="col-xs-0 margin-top-xs-5" style="padding-left: 26px;">
					<label class="eto-select__label">To</label>
				</div>
				<div class="col-xs-1 margin-top-xs-4">
					<div class="eto-select"
						${readOnly==true ? disabled:''}>
						<div class="eto-select__field-container">
							<select name="toDate" id="toDate">
								<c:forEach var="rt"
									items="${supplyAllocationForm.availablePeriods}">
									<fmt:formatDate var="dt" value="${rt}"
										pattern="${appContext.currentDateFormat}" />
									<option value="${dt}">${dt}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<script>
						new eto.SelectInput({
							el : document.querySelector('#toDate')
						});
					</script>
				</div>
			</div>
    </c:otherwise>
</c:choose>

					
		</div>
		<br>
		<div class="container" title="${saDetailTitle}"
			 id="saDetails"  style="padding-left: 53px;">
			<div class="eto-grid" id="grid-table">
				<div class="eto-grid-scroll">
					<table id="saDetailTable">
						<colgroup>
							<col />
							<col />
							<col />
							<col />
							<col />
							<col />
							<col />
							<col />
							<col />
							<col />
						</colgroup>
						<thead>
							<tr>
								<th><label class="eto-checkbox"> <input
										class="eto-checkbox__field eto-all-rows-indicator"
										type="checkbox"> <span class="eto-checkbox__box"></span>
								</label></th>
								<th><fmt:message key="sa.supplierItem" /></th>
								<th><fmt:message key="sa.suppliedBy" /></th>
								<th><fmt:message key="sa.supplierSite" /></th>
								<c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.destinationSite.enabled.for.supplyAllocation') && !e2ofn:getConfigValue('pcm.supplierAllocation.fiscalCalendarValidation.enabled')}">
								    <th><fmt:message key="sa.destinationSiteName" /></th>
								</c:if>
								<th><fmt:message key="sa.allocation" /></th>
								<th><fmt:message key="sa.price" /></th>

								<th><fmt:message key="sa.description" /></th>

								<th><fmt:message key="sa.startDate" /></th>

								<th><fmt:message key="sa.endDate" /></th>

								<th><fmt:message key="sa.status" /></th>
								<th><fmt:message key="sa.businessName" /></th>

							</tr>
						</thead>
						<tbody>
							<c:set var="saTotal" value="0.0" />
							<c:forEach var="saentry"
								items="${supplyAllocationForm.allocationRecords}"
								varStatus="saCount">
								<c:set var="sa" value="${saentry.value}" />
								<c:set var="locked"
									value="${supplyAllocationForm.todaysDate > sa.effectiveToDt}" />

								<c:set var="datefilter"
									value="${
                                      (empty supplyAllocationForm.startDate && empty supplyAllocationForm.endDate)
									  || (
									    (!empty supplyAllocationForm.startDate
									      && supplyAllocationForm.startDate >= sa.effectiveFromDt
									      && supplyAllocationForm.startDate <= sa.effectiveToDt
									      && (empty supplyAllocationForm.endDate || supplyAllocationForm.startDate <= supplyAllocationForm.endDate))
									    ||
									    (empty supplyAllocationForm.startDate && !empty supplyAllocationForm.endDate
									      && supplyAllocationForm.endDate >= sa.effectiveFromDt
									      && supplyAllocationForm.endDate <= sa.effectiveToDt)
									  )
									}"
									/>
								<c:choose>
							<c:when test="${!disableFiscal}">
								<c:if test="${datefilter}">

								<tr>
									<td><label class="eto-checkbox"> <input
											class="eto-checkbox__field eto-row-indicator" type="checkbox" name="selectedRecordKeys" value="${saentry.key}">
											<span class="eto-checkbox__box"></span>
									</label></td>
									<td><a
										href="javascript:showItemWindow(${sa.supplierItem.itemKey})">
											<c:out value="${sa.supplierItem.itemNumber}" />
									</a></td>
									<td><c:out
											value="${sa.supplierBusinessEntity.businessEntityName}" /></td>
									<td><c:out value="${sa.supplierSite.siteName}" /></td>
									<c:if test="${e2ofn:getConfigValue('pcm.supplierAllocation.destinationSite.enabled.for.supplyAllocation') && !e2ofn:getConfigValue('pcm.supplierAllocation.fiscalCalendarValidation.enabled')}">
									    <td><c:out value="${sa.destinationSite.siteName}" /></td>
									</c:if>
									<td class="eto-grid-edit-cell"><html:messages
											id="allocationAmountError"
											property="allocationAmount(${saentry.key})" name="lineErrors" />
										<fmt:formatNumber var="saAmount" value="${sa.allocation}"
											minFractionDigits="1" maxFractionDigits="1"
											maxIntegerDigits="3" /> <c:set var="saTotal"
											value="${saTotal + saAmount}" />
										<div class="eto-input has-value"
											id="allocationAmountId${saCount.index}">
											<div class="eto-input__field-container">
												<input class="eto-input__field"
													id="allocationAmount${saCount.index}"
													name="allocationAmount(${saentry.key})" type="text"
													onchange="handleDataChanged()"
													${(readOnly==true || locked==true)? "disabled":'' }
													size="10" value="${saAmount}"
													onblur="checkAllocAmount(this)"
													title="${allocationAmountError}">
											</div>
										</div> <script type="text/javascript">
											new eto.TextInput(
													{
														el : document
																.querySelector('#allocationAmountId${saCount.index}')
													});
										</script></td>
									<td style="width: 80px;" nowrap><c:if
											test="${!empty supplyAllocationForm.allocationPricing[saentry.key]}">
											<c:set var="pTitle">
												<fmt:formatDate
													value="${supplyAllocationForm.allocationPricing[saentry.key].effectiveFromDt}"
													pattern="${appContext.currentDateFormat}" />
   &nbsp;<c:out
													value="${supplyAllocationForm.allocationPricing[saentry.key].sourcingLane.title}" />
											</c:set>
											<fmt:formatNumber
												value="${supplyAllocationForm.allocationPricing[saentry.key].total}"
												minFractionDigits="1" maxFractionDigits="6" />
											<span title="${pTitle}">&nbsp(${supplyAllocationForm.allocationPricing[saentry.key].costType})</span>
										</c:if></td>

									<td class="eto-grid-edit-cell">
										<div class="eto-input has-value"
											id="allocationDescriptionId${saCount.index}">
											<div class="eto-input__field-container">
												<input class="eto-input__field"
													name="allocationDescription(${saentry.key})"
													id="allocationDescription${saCount.index}"
													onchange="handleDataChanged()"
													${(readOnly==true || locked==true)? "disabled":'' }
													size="23" maxlength="200" value="${sa.description}">
											</div>
										</div> 
										<script type="text/javascript">
											new eto.TextInput(
													{
														el : document
																.querySelector('#allocationDescriptionId${saCount.index}')
													});
										</script>
									</td>

									<td nowrap="nowrap"><fmt:formatDate
											value="${sa.effectiveFromDt}"
											pattern="${appContext.currentDateFormat}" /></td>
									<td nowrap="nowrap"><fmt:formatDate
											value="${sa.effectiveToDt}"
											pattern="${appContext.currentDateFormat}" /></td>
									<td><c:out value="${sa.status}" /></td>
										<td><c:out value="${sa.customerItem.businessEntity.businessEntityName}" /></td></tr>
								</tr>
								</c:if>
							 </c:when>
    <c:otherwise>
							<tr>
									<td><label class="eto-checkbox"> <input
											class="eto-checkbox__field eto-row-indicator" type="checkbox" name="selectedRecordKeys" value="${saentry.key}">
											<span class="eto-checkbox__box"></span>
									</label></td>
									<td><a
										href="javascript:showItemWindow(${sa.supplierItem.itemKey})">
											<c:out value="${sa.supplierItem.itemNumber}" />
									</a></td>
									<td><c:out
											value="${sa.supplierBusinessEntity.businessEntityName}" /></td>
									<td><c:out value="${sa.supplierSite.siteName}" /></td>
									<td class="eto-grid-edit-cell"><html:messages
											id="allocationAmountError"
											property="allocationAmount(${saentry.key})" name="lineErrors" />
										<fmt:formatNumber var="saAmount" value="${sa.allocation}"
											minFractionDigits="1" maxFractionDigits="1"
											maxIntegerDigits="3" /> <c:set var="saTotal"
											value="${saTotal + saAmount}" />
										<div class="eto-input has-value"
											id="allocationAmountId${saCount.index}">
											<div class="eto-input__field-container">
												<input class="eto-input__field"
													id="allocationAmount${saCount.index}"
													name="allocationAmount(${saentry.key})" type="text"
													onchange="handleDataChanged()"
													${(readOnly==true || locked==true)? "disabled":'' }
													size="10" value="${saAmount}"
													onblur="checkAllocAmount(this)"
													title="${allocationAmountError}">
											</div>
										</div> <script type="text/javascript">
											new eto.TextInput(
													{
														el : document
																.querySelector('#allocationAmountId${saCount.index}')
													});
										</script></td>
									<td style="width: 80px;" nowrap><c:if
											test="${!empty supplyAllocationForm.allocationPricing[saentry.key]}">
											<c:set var="pTitle">
												<fmt:formatDate
													value="${supplyAllocationForm.allocationPricing[saentry.key].effectiveFromDt}"
													pattern="${appContext.currentDateFormat}" />
   &nbsp;<c:out
													value="${supplyAllocationForm.allocationPricing[saentry.key].sourcingLane.title}" />
											</c:set>
											<fmt:formatNumber
												value="${supplyAllocationForm.allocationPricing[saentry.key].total}"
												minFractionDigits="1" maxFractionDigits="6" />
											<span title="${pTitle}">&nbsp(${supplyAllocationForm.allocationPricing[saentry.key].costType})</span>
										</c:if></td>

									<td class="eto-grid-edit-cell">
										<div class="eto-input has-value"
											id="allocationDescriptionId${saCount.index}">
											<div class="eto-input__field-container">
												<input class="eto-input__field"
													name="allocationDescription(${saentry.key})"
													id="allocationDescription${saCount.index}"
													onchange="handleDataChanged()"
													${(readOnly==true || locked==true)? "disabled":'' }
													size="23" maxlength="200" value="${sa.description}">
											</div>
										</div> 
										<script type="text/javascript">
											new eto.TextInput(
													{
														el : document
																.querySelector('#allocationDescriptionId${saCount.index}')
													});
										</script>
									</td>

									<td nowrap="nowrap"><fmt:formatDate
											value="${sa.effectiveFromDt}"
											pattern="${appContext.currentDateFormat}" /></td>
									<td nowrap="nowrap"><fmt:formatDate
											value="${sa.effectiveToDt}"
											pattern="${appContext.currentDateFormat}" /></td>
									<td><c:out value="${sa.status}" /></td>
										<td><c:out value="${sa.customerItem.businessEntity.businessEntityName}" /></td></tr>
								</tr>
						</c:otherwise>
</c:choose>
								

							</c:forEach>
							<c:set var="colspanFlag"
                                   value="${e2ofn:getConfigValue('pcm.supplierAllocation.destinationSite.enabled.for.supplyAllocation') and not e2ofn:getConfigValue('pcm.supplierAllocation.fiscalCalendarValidation.enabled')}" />
							<tr id="allocTotalId">
								<th colspan="${colspanFlag ? 5 : 4}" style="text-align: right ! important;"><fmt:message
										key="sa.total" /></td>
								<th id="allocationTotal" style="padding-left: 10px"><fmt:formatNumber
										value="${saTotal}" minFractionDigits="1" maxFractionDigits="1"
										maxIntegerDigits="3" /></td>
								<td id="avgCost" /></td>
								<td colspan="5"></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<script>
				var grid = new eto.Grid({
					el : document.querySelector('#grid-table')
				});
			</script>
		</div>

		<%-- <e2i2:container title="${saDetailTitle}" collapsable="no" width="100%" id="saDetails">
<e2i2:table id="saDetailTable">
<e2i2:tr header="yes">
<td>
<input id="saDetailTable_globalrowselector" 
    type="checkbox" 
    onclick="i2uiToggleAllRowsSelectionState(this,'saDetailTable')">
</td>
<td><fmt:message key="sa.supplierItem"/></td>
<td><fmt:message key="sa.suppliedBy"/></td>
<td><fmt:message key="sa.supplierSite"/></td>
<td><fmt:message key="sa.allocation"/></td>
<td><fmt:message key="sa.price"/></td>
<td><fmt:message key="sa.description"/></td>
<td><fmt:message key="sa.startDate"/></td>
<td><fmt:message key="sa.endDate"/></td>
<td><fmt:message key="sa.status"/></td>
</e2i2:tr>
<c:set var="saTotal" value="0.0"/>
<c:forEach var="saentry" items="${supplyAllocationForm.allocationRecords}" varStatus="saCount">
<c:set var="sa" value="${saentry.value}"/>
<c:set var="locked" value="${supplyAllocationForm.todaysDate > sa.effectiveToDt}"/>
<e2i2:tr>
<td><html:checkbox styleId="saDetailTable_rowselector" property="selectedRecordKeys"
   value="${saentry.key}" 
   onclick="i2uiToggleRowSelectionState(this,'tableRow${(saCount.count) % 2}','saDetailTable',null,true)"/>
</td>
<td>
<a href="javascript:showItemWindow(${sa.supplierItem.itemKey})">
<c:out value="${sa.supplierItem.itemNumber}"/>
</a>
</td>
<td><c:out value="${sa.supplierBusinessEntity.businessEntityName}"/></td>
<td><c:out value="${sa.supplierSite.siteName}"/></td>
<td>

<html:messages id="allocationAmountError" property="allocationAmount(${saentry.key})" name="lineErrors"/>
<fmt:formatNumber var="saAmount" value="${sa.allocation}" 
	minFractionDigits="1" maxFractionDigits="1" maxIntegerDigits="3"/>    
<c:set var="saTotal" value="${saTotal + saAmount}"/>    
<html:text styleId="allocationAmount${saCount.index}" styleClass="inputField"
	onchange="handleDataChanged()"  disabled="${readOnly || locked}"
	size="10" errorKey="lineErrors" errorStyle="background-color:red"	
    value="${saAmount}" 
	onblur="checkAllocAmount(this)"
    title="${allocationAmountError}"
	property="allocationAmount(${saentry.key})"/>
</td>
<td style="width:80px;" nowrap>
<c:if test="${!empty supplyAllocationForm.allocationPricing[saentry.key]}">
<c:set var="pTitle">
   <fmt:formatDate value="${supplyAllocationForm.allocationPricing[saentry.key].effectiveFromDt}" pattern="${appContext.currentDateFormat}"/>
   &nbsp;<c:out value="${supplyAllocationForm.allocationPricing[saentry.key].sourcingLane.title}"/>
</c:set>
   <fmt:formatNumber value="${supplyAllocationForm.allocationPricing[saentry.key].total}" 
         minFractionDigits="1" maxFractionDigits="6"/>
   <span title="${pTitle}">&nbsp(${supplyAllocationForm.allocationPricing[saentry.key].costType})</span>
</c:if>    
</td>

<td><html:text styleId="allocationDescription${saCount.index}"  styleClass="inputField"
	onchange="handleDataChanged()"  disabled="${readOnly || locked}"
	size="23" maxlength="200"
	value="${sa.description}"
	property="allocationDescription(${saentry.key})"/>
</td>
<td nowrap="nowrap"><fmt:formatDate value="${sa.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/></td>
<td nowrap="nowrap"><fmt:formatDate value="${sa.effectiveToDt}" pattern="${appContext.currentDateFormat}"/></td>
<td><c:out value="${sa.status}"/></td>
</e2i2:tr>
</c:forEach>
<e2i2:tr>
<td colspan="4" align="right">
<fmt:message key="sa.total"/>
</td>
<td id="allocationTotal" style="padding-left:10px">
<fmt:formatNumber value="${saTotal}" minFractionDigits="1" maxFractionDigits="1" maxIntegerDigits="3"/>    
</td>
<td id="avgCost"/></td>
<td colspan="4"></td>
</e2i2:tr>
</e2i2:table>

</e2i2:container> --%>
		<div class="eto-modal" id="basic-modal-example">
			<div class="eto-modal__content" style="width: 600px;">
				<header class="eto-modal__header">
					<span>New Allocation - Select Date</span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<c:choose>
					<c:when test="${!disableFiscal}">
						<section class="eto-modal__body" style="padding: 50px; font-family:Arial">

							<label for="start-date" style="font-size: 20px; padding: 5px">Start
								Date:</label> <input type="date" id="newStartDate" name="newStartDate" />

							<label for="end-date" style="font-size: 20px; padding: 5px">End
								Date:</label> <input type="date" id="newEndDate" name="newEndDate" />

						</section>
						<footer class="eto-modal__footer">
							<button class="eto-btn" data-modal-close>Cancel</button>
							<button class="eto-btn eto-btn--primary"
								onclick="createAllocation()">Create</button>
						</footer>
					</c:when>
					<c:otherwise>
						<section class="eto-modal__body" style="padding: 50px">
							<div class="eto-calendar" id="calendar-example"></div>
						</section>
						<footer class="eto-modal__footer">
							<button class="eto-btn" data-modal-close>Cancel</button>
							<button class="eto-btn eto-btn--primary" data-modal-close>Create</button>
						</footer>

					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<script type="text/javascript">
			new eto.Modal({
				el : document.querySelector('#basic-modal-example')
			});
			today = new Date();
			var currDate = (today.getMonth() + 1) + '-' + today.getDate() + '-'
					+ today.getFullYear();
			var cal = new eto.Calendar({
				el : document.querySelector('#calendar-example'),
				value : currDate,
				min : '01-01-1990',
				max : '12-31-2040',
				format : 'MM-DD-YYYY'
			});
			cal.on('change:value', function() {
				var newValue = cal.getValue().format("${appContext.currentDateFormat}".toUpperCase());
				newPeriodSelected(newValue);
			});
		</script>
		<div class="footer" style="border-color: #c8c4c4;">
			<nav class="eto-form__btns" style="margin-left: 30px">
				<div class="eto-btn-group" style="margin-top: 15px">
					<button class="eto-btn" type="button" onclick="javascript:goBack()">
						<fmt:message key="button.back" />
					</button>
					<c:if test="${e2ofn:hasAccess(appContext, 'SUPPLY_ALLOC', 'Save')}">
						<button class="eto-btn eto-btn--primary" type="button"
							onclick="javascript:goSave()">
							<fmt:message key="button.save_return" />
						</button>
						<button class="eto-btn" type="button"
							onclick="javascript:goSaveAndContinue()">
							<fmt:message key="button.save" />
						</button>
					</c:if>
				</div>
			</nav>

		</div>

	</e2o:form>
</body>
</html>