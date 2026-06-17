<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Allocation" />
<script>
	loading();
	var regionComboBox;
	var siteComboBox;
	var itemSearch;
	var functionalGroupSearch;
	var gridObj;
	var menuOption;
	var siteCopyTree = [];
	var siteCopyMainTree;
	var toastTAM;
	var searchStartDate;
</script>
<link rel="stylesheet" type="text/css" href="css/tamAllocationHistory.css" />
</head>
<body onload="init()">
	<e2o:form action="/clearTAMHistory" style="margin:0px,padding:0px">
		<input type="hidden" name="siteType"
			value="${tamAllocationForm.siteType}">
		<input type="hidden" name="downloadOption">
		<input type="hidden" name="fileLocation">
		<input type="hidden" name="minHedging" value="100">
		<input type="hidden" name="maxHedging" value="100">
		<input type="hidden" name="errorExist" value="0">
		<input type="hidden" name="unsavedData" value="false">
		<input type="hidden" name="freshSearch" value="true">
		<input type="hidden" name="hideSupplierWithNoAllocationPref">
		<input type="hidden" name="hideItemPref">
		<input type="hidden" name="hideMRPPref">
		<input type="hidden" name="massUpdateNoParentError" value="false">
		<input type="hidden" id="pastDateChange" name="changePastDate"
			value="" onchange="changePastUpdated();">
		<input type="hidden" name="pastScreen" value="true">
		<c:set var="pageIdentifier" value="TAM_HISTORY"/>
		<c:set var="noralizationRoudingValue"
			value="${e2ofn:getConfigValue('pcm.tam.allocation.round')}" />
		<c:set var='enableB2BIntegration' value='${e2ofn:getConfigValue("scplatform.feature.enable.tam.B2B.integration")}' />
		<fmt:message var="title" key="search.filter.title.harmony" />
		<%-- <e2o:errors maxErrors="4" styleId="errors" /> --%>
		<div style="font-weight: bold; white-space: pre-line;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					<li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
		<c:if
			test="${tamAllocationForm.allocation != null && tamAllocationForm.allocation.functionalGroup.status == 'INACTIVE'}">
			<div class="row" style="margin: 15px auto;">
				<div class="eto-messageblock" data-message-type="warn"
					id="inactivefg-message-block">
					<div class="eto-messageblock__body">
					<c:if test="${tamAllocationForm.allocation != null && tamAllocationForm.pastScreen == true }">
						<fmt:message key="warn.history.editing_an_inactive_fg" />
					</c:if>
					<c:if test="${tamAllocationForm.allocation != null && (tamAllocationForm.pastScreen == false || tamAllocationForm.pastScreen == null)}">
						<fmt:message key="warn.editing_an_inactive_fg" />
					</c:if>
					</div>
					<a href="javascript:void(0)" role="button"
						class="eto-messageblock__close"></a>
				</div>
				<script type="text/javascript">new eto.MessageBlock({ el: document.querySelector('#inactivefg-message-block') });</script>
			</div>
		</c:if>
		<div class="eto-well eto-expand--expanded" id="expand-container">
			<div class="eto-expand__toggle display-xs-flex">
				<h3 class="eto-expand__h3">${title}</h3>
			</div>
			<div class="eto-expand__content">
				<div class="container">
					<div class="row">
						<div class="col-xs-3 col-sm-2 col-md-2">
							<label class="eto-input__label" for="etot81"><fmt:message
									key="tam.fgType" /></label>
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2">
							<label class="eto-input__label" for="etot81"><fmt:message
									key="tam.planner" /></label>
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2">
							<label class="eto-input__label" for="etot81"><fmt:message
									key="tam.hostory.search.start.date" /></label>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-3 col-sm-2 col-md-2 margin-top-sm-1 fgType">
							<div class="eto-toggle">
								<div class="eto-toggle__items">
									<c:forEach items="${tamAllocationForm.fgTypeOption}"
										var="fgTypeValue">
										<label class="eto-toggle__item"> <input
											class="eto-toggle__input toogleFgType" type="radio"
											name="selectedFgType" value="${fgTypeValue}"
											${tamAllocationForm.selectedFgType == fgTypeValue ? 'checked="checked"' : ''}>
											<span class="eto-toggle__label">${fgTypeValue}</span>
										</label>
									</c:forEach>
								</div>
							</div>
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2 margin-top-sm-1 tamPlanner">
							<div class="eto-toggle">
								<div class="eto-toggle__items">
									<label class="eto-toggle__item"> <input
										class="eto-toggle__input toogleTd" type="radio"
										name="toggle-tamPlanner" id="global"
										${tamAllocationForm.siteType=="global" ? 'checked="checked"' : ''}>
										<span class="eto-toggle__label"><fmt:message
												key="button.global" /></span>
									</label> <label class="eto-toggle__item"> <input
										class="eto-toggle__input toogleTd" type="radio"
										name="toggle-tamPlanner" id="region"
										${tamAllocationForm.siteType=="region" ? 'checked="checked"' : ''}>
										<span class="eto-toggle__label"><fmt:message
												key="button.region" /></span>
									</label> <label class="eto-toggle__item"> <input
										class="eto-toggle__input toogleTd" type="radio"
										name="toggle-tamPlanner" id="site"
										${tamAllocationForm.siteType=="site" ? 'checked="checked"' : ''}>
										<span class="eto-toggle__label"><fmt:message
												key="button.site" /></span>
									</label>
								</div>
							</div>
						</div>
						<div class="col-xs-4 col-sm-3 col-md-2 margin-top-sm-1 tamPlanner">
							<div class="eto-input eto-form" data-message-type=""
								id="searchDateField">
								<div class="eto-input__field-container">
									<input class="eto-input__field" type="text"
										id="searchStartDate" name="searchStartDate" readonly="true">
									<span class="eto-input__addon"><i class="md-icon"
										onclick="javascript:showCalendar('searchStartDate')">event</i>
										<i class="md-icon"
										onclick="javascript:clearField(document.getElementById('searchStartDate'))">close</i>
									</span>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12 col-sm-3 margin-top-sm-3" id="regionDiv">
							<select name="region" id="regionInput"
								style="width: 150px; height: 30px;" disabled="disabled">
								<option value=""></option>
								<c:forEach var="region" items="${tamAllocationForm.regionList}">
									<option value="${region.siteDescription}"
										<c:if test="${tamAllocationForm.region == region.siteDescription}">selected="selected"</c:if>><c:out
											value="${region.siteDescription}" /></option>
								</c:forEach>
							</select>
						</div>
						<div class="col-xs-12 col-sm-3 margin-top-sm-3" id="siteDiv">
							<select name="siteDescription" id="siteInput"
								style="width: 150px; height: 30px;">
								<option value=""></option>
								<c:forEach var="site" items="${tamAllocationForm.sitesList}">
									<option value="${site.siteDescription}"
										<c:if test="${tamAllocationForm.siteDescription == site.siteDescription}">selected="selected"</c:if>><c:out
											value="${site.siteDescription}" /></option>
								</c:forEach>
							</select>
						</div>

						<div class="col-xs-12 col-sm-3 margin-top-sm-3" id="itemDiv">

							<div class="eto-autocomplete" id="autocomplete-itemNumber">
								<label class="eto-autocomplete__label"><fmt:message
										key="tam.itemNumber" /></label> <input
									class="eto-autocomplete__field" type="text"
									placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'
									name="itemNumber" autocomplete="off" onblur="trimValue(this)">
								<div class="eto-autocomplete__message"></div>
								<div class="eto-results"></div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-3 margin-top-sm-3"
							id="functionalGroupDiv">
							<div class="eto-autocomplete"
								id="autocomplete-functionalGroupInput">
								<label class="eto-autocomplete__label"><fmt:message
										key="tam.groupName" /></label> <input class="eto-autocomplete__field"
									type="text"
									placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'
									name="groupName" autocomplete="off" onblur="trimValue(this)"
									value="${tamAllocationForm.groupName}">
								<div class="eto-autocomplete__message"></div>
								<div class="eto-results"></div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12 col-sm-12 margin-top-sm-3" id="filterButton">
							<button type="button" class="eto-btn eto-btn--primary"
								onclick="javascript:goSearch();">
								<fmt:message key="button.search.harmony" />
							</button>

							<button type="button" class="eto-btn"
								onclick="javascript:goClear();" style="margin-left: 1%">
								<fmt:message key="button.clear" />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script>
			searchStartDate = new eto.TextInput({el : document.querySelector('#searchDateField')});
			searchStartDate.setValue('${tamAllocationForm.searchStartDate}');
			var filterExpand = new eto.Expand({ el: document.querySelector('#expand-container')});
			<c:if test="${!empty tamAllocationForm.allocation}">
				$('#expand-container').removeClass('eto-expand--expanded');
				$('#expand-container').addClass('eto-expand');
			</c:if>
			regionComboBox = new eto.Combobox({el: document.querySelector('[name="region"]'),label: '<fmt:message key="tam.region" />'});
			siteComboBox = new eto.Combobox({el: document.querySelector('[name="siteDescription"]'),label: '<fmt:message key="tam.site" />'});
			itemSearch = new eto.Autocomplete({ el: document.querySelector('#autocomplete-itemNumber') ,add: true });
			itemSearch.on('inputChange',
				function(query) {
					var url = "ajaxQueryItemNumberWithOutCFGType.do?q=" +query;
					$.ajax({
						url : url,
						success : function(result) {
							var arr;
							if (result.includes("|")) {
								arr = result.split("|");
							} else {
								arr = result.split("\n");
							}
							itemSearch.setContent(arr);
							itemSearch.open();
						}
					});
						
					});
					functionalGroupSearch = new eto.Autocomplete({ el: document.querySelector('#autocomplete-functionalGroupInput'),add: true  });
					functionalGroupSearch.on('inputChange',
						function(query) {
							var url = "ajaxQueryFunctionalGroupName.do?q=" +query+"&fgType="+$("input[name='selectedFgType']:checked").val();
							$.ajax({
								url : url,
								success : function(result) {
									var arr;
									if (result.includes("|")) {
										arr = result.split("|");
									} else {
										arr = result.split("\n");
									}
									functionalGroupSearch.setContent(arr);
									functionalGroupSearch.open();
								}
							});
								
					});
		</script>

		<div class="container" style="margin-bottom: 10%;">
			<div
				class="row margin-left-sm-1 margin-right-sm-1 margin-top-sm-2 margin-bottom-sm-2">
				<div class="col-sm-6" id="headerDiv">
					<label
						style="white-space: nowrap;margin-right: 30px"><h1><fmt:message
							key="tam.history.page_header" /></h1></label>
					<c:if test="${empty tamAllocationForm.allocation}">
						<div id="searchDetailsMessage">
							<label><i><fmt:message key="tam.search_msg" /></i></label>
						</div>
					</c:if>
				</div>
				<div class="col-sm-6" id="pageToolsDiv">
					<div style="float: right; display: flex;">
						<c:set var="buttonDisable"
							value="${empty tamAllocationForm.allocation}" />
						<button
							class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
							style="font-size: 2.5rem;" title="History" type="button"
							${buttonDisable ? 'disabled' : ''}
							<c:if test="${!buttonDisable}">
							onclick="javascript:showTAMAuditHistory('${tamAllocationForm.groupName!=null ? tamAllocationForm.groupName : 'null' }','${tamAllocationForm.allocation != null ? tamAllocationForm.allocation.site.siteKey : 'null' }');"
						</c:if>>
							<i class="md-icon ${buttonDisable ? '' : ' mtcm_icon'}">history</i>
						</button>
						<button type="button" ${buttonDisable ? 'disabled' : ''}
							id="openFullScreen" style="display: none; font-size: 2.5rem;"
							title="Open in full screen"
							class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
							id="fullscreen" onclick="makeFullscreen('open');">
							<i class="md-icon ${buttonDisable ? '' : 'mtcm_icon'} ">fullscreen</i>
						</button>
						<button id="exitFullScreen" ${buttonDisable ? 'disabled' : ''}
							type="button" style="display: block; font-size: 2.5rem;"
							title="Exit Full Screen"
							class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
							id="fullscreen_exit" onclick="makeFullscreen('exit');">
							<i class="md-icon ${buttonDisable ? '' : ' mtcm_icon'} ">fullscreen_exit</i>
						</button>
						<div class="eto-checkbox eto-checkbox-menu" id="dropdown-setting"
							style="display: inline-flex; padding-bottom: 1rem;">
							<span role="menu" class="eto-dropdown">
								<button type="button" style="font-size: 2.5rem;"
									title="Settings" class="eto-dropdown__toggle"
									${buttonDisable ? 'disabled' : ''}>
									<i class="md-icon ${buttonDisable ? '' : ' mtcm_icon'} ">settings</i>
									<!-- style="margin:1rem; " -->
								</button>
								<ul class="eto-dropdown__menu">
									<li name="this-page" data-checkbox-state="checked"
										role="menuitem"><label style="padding: 2rem;"
										class="eto-switch margin-bottom-xs-2"
										id="grid-compact-control"> <input
											class="eto-switch__field" type="checkbox"
											id="condensedCheckBox"> <span class="eto-switch__box"></span>
											<span class="eto-switch__label--off">Condensed</span><span
											class="eto-switch__label--on">Condensed</span>
									</label></li>
								</ul>
							</span>
						</div>
					</div>
				</div>
			</div>
			<div class="eto-toast-container" id="toast-container-tam"></div>
			<script type="text/javascript">
			<c:if test="${!buttonDisable}">
				menuOption = new eto.CheckboxMenu({ el: document.querySelector('#dropdown-setting') });
			</c:if>
				toastTAM = new eto.ToastContainer({
					  el: document.querySelector('#toast-container-tam')
					});
			</script>
			<c:if test="${tamAllocationForm.allocation != null}">
				<c:choose>
					<c:when test="${tamAllocationForm.selectedFgType == 'CFG'}">
						<%@ include file="tamAllocationHistoryGridCFG.jspf"%>
					</c:when>
					<c:when test="${tamAllocationForm.selectedFgType == 'XLOB'}">
						<%@ include file="tamAllocationHistoryGridXLOB.jspf"%>
					</c:when>
				</c:choose>
			</c:if>
			<c:if test="${!empty tamAllocationForm.allocation}">
				<div class="row margin-top-sm-3">
					<div class="col-xs-12 col-sm-6"></div>
					<div class="col-xs-12 col-sm-6 pull-sm-2">
						<table align="right">
							<tbody>
								<c:if test="${empty tamAllocationForm.dataLocation}">
									<tr>
										<td>
											<!-- Commented as per SCPlatform-3292  
										<i class="md-icon mtcm_icon md-icon--sm"
											style="color: rgba(98, 196, 98, 1) !important;">check_circle</i>
											-->
										</td>
										<td><label
											style="font-style: italic; font-family: initial;"><fmt:message
													key="info.tam_last_saved" /></label> <label> <c:out
													value="${tamAllocationForm.allocation.lastChangedBy}" />,
												<fmt:formatDate var="lastSaved" pattern="dd-MMM-yyyy"
													value="${tamAllocationForm.allocation.lastChangedOn}" /> <c:out
													value="${lastSaved}" />
												<c:if test="${enableB2BIntegration and not empty tamAllocationForm.allocation.sourceLastChangedBy}">
													,<fmt:message key="info.tam.sourceLastChangedBy" />
													<c:out value=" ${tamAllocationForm.allocation.sourceLastChangedBy}" />
												</c:if></label></td>
									</tr>
									<tr>
										<td>
											<!-- <img alt="" src="skins/e2-modern/images/alert_green_static.gif"
									class="copyRowImage"> -->
										</td>
										<td>
											<%-- <fmt:message key="info.tam_inherited_uncheck" /> --%>
										</td>
									</tr>
								</c:if>
								<c:if
									test="${fn:contains(tamAllocationForm.dataLocation,'global') || fn:contains(tamAllocationForm.dataLocation,'region')}">
									<tr>
										<td><label data-tooltip="#tooltip-data-location">
												<fmt:message key="info.tam_data_from_note" />
										</label> <!--  Commented as per SCPlatform-3260
										<i class="md-icon mtcm_icon md-icon--md"
											style="color: rgba(248, 148, 6, 1) !important;"
											data-tooltip="#tooltip-data-location"
											aria-describedby="#tooltip-data-location">check_circle</i>
											--></td>
										<fmt:message key="info.tam.data.inherit" var="inheritNote"/>
										<c:if test="${fn:length(inheritNote) > 0}">
											<div class="eto-tooltip" data-anchor-x="left"
												data-anchor-y="middle" id="tooltip-data-location">
												<div class="eto-tooltip__content">
													<c:out value="${inheritNote}"></c:out>
												</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script>
												new eto.Tooltip({ el: document.querySelector('#tooltip-data-location') });
											</script>
										</c:if>
										<td><fmt:message key="info.tam_data_inherited" /> <c:out
												value="${fn:toUpperCase(tamAllocationForm.dataLocation)}" /></td>
									</tr>
									<!--	<tr>
										<td>
											<img alt="" src="skins/e2-modern/images/alert_green_static.gif"
									class="copyRowImage">
										</td>
										<td>
											<fmt:message key="info.tam_inherited_uncheck" /> 
										</td> 
									</tr>-->
								</c:if>
								<c:if test="${tamAllocationForm.dataLocation == 'new'}">
									<tr>
										<td><fmt:message key="info.tam_data_from_note" /> <!--  Commented as per SCPlatform-3292
										<i class="md-icon mtcm_icon md-icon--sm"
											style="color: rgba(248, 148, 6, 1) !important;">assignment_late</i>
											--></td>
										<td><label
											style="font-style: italic; font-family: initial;"><fmt:message
													key="info.tam_new_record" /></label></td>
									<tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
				<!-- Footer button Bar start -->

				<div class="row tamFooter">
					<div class="col-xs-12 col-sm-12 margin-sm-2"
						style="margin-top: 17px; margin-left: 13px;">
						<nav class="eto-form__btns">
							<div class="eto-btn-group">
								<button id="resetButton" type="button" class="eto-btn"
									onclick="javascript:goReset();">
									<fmt:message key="button.reset" />
								</button>
							</div>
						</nav>
					</div>
				</div>

				<!-- Footer button Bar end -->
			</c:if>
	</e2o:form>

	<!-- All script goes inside tamScript -->

	<%@ include file="tamHistoryScript.jspf"%>

	<!-- All script goes inside tamScript -->

	<%@ include file="../fullModal.jspf"%>
	
	<e2o:errors maxErrors="4" styleId="errors" />
</body>
</html>