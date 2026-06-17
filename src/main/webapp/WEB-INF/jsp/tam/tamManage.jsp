<%@ include file="../common.jspf"%>
<%@ page import="com.test.repository.common.domain.bom.meta.FlexAttributeManager"%>
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
	var fromDatePopover;
	var toDatePopover;
	var toastTAM;
	var downloadMenuOn;
	var screenType = null;
	var regionCheckboxValue;
	var siteCheckboxValue;
</script>
<link rel="stylesheet" type="text/css" href="css/tamAllocation.css" />
<c:set var='collapsedFgLength'
	value='${e2ofn:getConfigValue("pcm.tam.filter.collapsed.field.length")}' />
	<c:set var='expandFgLength'
	value='${e2ofn:getConfigValue("pcm.tam.filter.expand.field.length")}' />
	<c:set var='enableTamForUserItemType' value='${e2ofn:getConfigValue("pcm.feature.enable.tam.restriction.for.UserItemType")}' />
	<c:set var="genericConfigValue" value="${e2ofn:getConfigValue(pcm.item.flexattribute.usertItemType.genericTypes)}" />
    <c:set var='enableB2BIntegration' value='${e2ofn:getConfigValue("scplatform.feature.enable.tam.B2B.integration")}' />
</head>
<body onload="init()">
<%
 String associatedStr = null;
if(FlexAttributeManager.ITEM.getAttributeDefn("userItemType")!=null){
 associatedStr = FlexAttributeManager.ITEM.getAttributeDefn("userItemType").getAssociatedAttribute();
 }
 pageContext.setAttribute("associatedStr", associatedStr);
%>
	<e2o:form action="/saveFunctionalGroup" style="margin:0px,padding:0px">
		<input type="hidden" name="selectedSite">
		<input type="hidden" name="deleteValuesCollected">
		<input type="hidden" name="allocationTypeDelete">
		<input type="hidden" name="level">
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
		<input type="hidden" name="searchStartDate" value="">
		<c:set var="noralizationRoudingValue"
			value="${e2ofn:getConfigValue('pcm.tam.allocation.round')}" />
		<fmt:message var="title" key="search.filter.title.harmony" />
		<e2o:errors maxErrors="4" styleId="errors" />
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
						<c:if
							test="${tamAllocationForm.allocation != null && tamAllocationForm.pastScreen == true }">
							<fmt:message key="warn.history.editing_an_inactive_fg" />
						</c:if>
						<c:if
							test="${tamAllocationForm.allocation != null && (tamAllocationForm.pastScreen == false || tamAllocationForm.pastScreen == null)}">
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
				<div class="row" style="width: 100%; display: flex">
					<div>
						<h3 class="eto-expand__h3">${title}</h3>
					</div>
					<div id="expandRow" class="eto-expand__toggle" style="padding-left:5rem;display: flex;width:80%;cursor: default !important;user-select:text !important;">
						<c:if test="${tamAllocationForm.siteType=='global'}">
							<div>
								<span><fmt:message key="pcm.tam.siteType.global"/>: <b>${tamAllocationForm.allocation.site.siteDescription}</b></span>
							</div>
							<div style="padding-left: 10px;">
								<div class="vl"></div>
							</div>
						</c:if>
						<c:if test="${not empty tamAllocationForm.region}">
							<div>
								<span><fmt:message key="pcm.tam.siteType.region"/>: <b>${tamAllocationForm.region}</b></span>
							</div>
							<div style="padding-left: 10px;">
								<div class="vl"></div>
							</div>
						</c:if>
						<c:if
							test="${tamAllocationForm.siteDescription !=null && tamAllocationForm.siteDescription !='' && tamAllocationForm.siteType=='site'}">
							<div
								${fn:length(tamAllocationForm.siteDescription) > 35?'data-tooltip="#tooltip-site aria-describedby="#tooltip-example"" ':'' }
>
								<fmt:message key="pcm.tam.siteType.site"/>: <b> ${fn:length(tamAllocationForm.siteDescription) <= 35 ? tamAllocationForm.siteDescription : ''}
									<c:if
										test="${fn:length(tamAllocationForm.siteDescription) > 35}">
											${fn:substring(tamAllocationForm.siteDescription,0,18)}...</c:if>
								</b>
							</div>
							<div style="padding-left: 10px;">
								<div class="vl"></div>
							</div>
						</c:if>
						<c:if
							test="${tamAllocationForm.groupName !=null && tamAllocationForm.groupName !=''}">
							<div data-tooltip="#header-tooltip-fg"
								aria-describedby="#tooltip-example">
								<span >Group: <b>
										${fn:length(tamAllocationForm.groupName) <= collapsedFgLength ? tamAllocationForm.groupName : ''}
										<c:if test="${fn:length(tamAllocationForm.groupName) > collapsedFgLength}">
											${fn:substring(tamAllocationForm.groupName,0,collapsedFgLength)}...
											</c:if>
								</b></span>
							</div>
							<div class="eto-tooltip col-xs-10 col-sm-6 col-md-4 col-lg-3"
								id="header-tooltip-fg">
								<div class="eto-tooltip__content">
									<span class="eto-tooltip__title" style="overflow-wrap: anywhere;"><c:out
											value="${tamAllocationForm.groupName}" escapeXml="true" /></span>
								</div>
								<span class="eto-tooltip__caret"></span>
							</div>
							<script type="text/javascript">
							var fglength = ${fn:length(tamAllocationForm.groupName)};
							var collapsedFgLength = ${collapsedFgLength};
							if(fglength>collapsedFgLength)
							 new eto.Tooltip({ el: document.querySelector('#header-tooltip-fg'),anchorX : 'center',anchorY: 'top'});

							</script>
						</c:if>
					</div>
				</div>
			</div>
			<div class="eto-expand__content">
				<div class="container">
					<div class="row">
						<div class="col-sm-2 col-sm-2">
							<label class="eto-input__label" for="etot81"><fmt:message
									key="tam.fgType" /></label>
						</div>
						<div class="col-sm-2 col-sm-2">
							<label class="eto-input__label" for="etot81"><fmt:message
									key="tam.planner" /></label>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-2 col-sm-2 margin-top-sm-1 fgType">
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
						<div class="col-sm-2 col-sm-2 margin-top-sm-1 tamPlanner">
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
									name="groupName" id="groupName" autocomplete="off"
									onblur="trimValue(this)" data-tooltip="#tooltip-fg"
									aria-describedby="#tooltip-example"
									value='<c:out value="${tamAllocationForm.groupName}"  escapeXml="true"/>' />
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
			var filterExpand = new eto.Expand({ el: document.querySelector('#expand-container')});
			$("#expandRow").hide();
			<c:if test="${!empty tamAllocationForm.allocation}">
				$('#expand-container').removeClass('eto-expand--expanded');
				$('#expand-container').addClass('eto-expand');
				$("#expandRow").show();
				</c:if>
			filterExpand.on('expanded', function(){
				$("#expandRow").hide();
				$('#expand-container').addClass('eto-expand--expanded');
				$('#expand-container').removeClass('eto-expand');
			});
			filterExpand.on('collapsed', function(){
				<c:if test="${!empty tamAllocationForm.allocation}">
				$("#expandRow").show();
				</c:if>
				$('#expand-container').removeClass('eto-expand--expanded');
				$('#expand-container').addClass('eto-expand');
			});
			
			regionComboBox = new eto.Combobox({el: document.querySelector('[name="region"]'),label: '<fmt:message key="tam.region" />'});
			siteComboBox = new eto.Combobox({el: document.querySelector('[name="siteDescription"]'),label: '<fmt:message key="tam.site" />'});
			itemSearch = new eto.Autocomplete({ el: document.querySelector('#autocomplete-itemNumber') ,add: true });
			itemSearch.on('inputChange',
				function(query) {
					var url = "ajaxQueryItemNumberWithOutCFGType.do";
					$.ajax({
						type:"POST",
						url : url,
						data :{ q : query },
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
							var url = "ajaxQueryFunctionalGroupName.do?fgType="+$("input[name='selectedFgType']:checked").val();
							$.ajax({
								type:"POST",
								url : url,
								data :{ q : query },
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
					<label style="white-space: nowrap; margin-right: 30px"><h1>
							<fmt:message key="tam.page_header" />
						</h1></label>
					<c:if test="${empty tamAllocationForm.allocation}">
						<div id="searchDetailsMessage">
							<label><i><fmt:message key="tam.search_msg" /></i></label>
						</div>
					</c:if>
				</div>
				<div class="col-sm-6" id="pageToolsDiv">
					<div style="float: right; display: flex;">
						<c:set var="buttonDisable"
							value="${empty tamAllocationForm.allocation.id}" />
						<button
							class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
							style="font-size: 2.5rem;" title="History" type="button"
							${buttonDisable ? 'disabled' : ''}
							<c:if test="${!buttonDisable}">
							onclick="javascript:showTAMAuditHistory('${tamAllocationForm.allocation.id!=null ? tamAllocationForm.allocation.id : null }');"
						</c:if>>
							<i class="md-icon ${buttonDisable ? '' : ' mtcm_icon'}">history</i>
						</button>
						<button type="button"
							id="openFullScreen" style="display: none; font-size: 2.5rem;"
							title="Open in full screen"
							class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
							id="fullscreen" onclick="makeFullscreen('open');">
							<i class="md-icon ${buttonDisable ? '' : 'mtcm_icon'} ">fullscreen</i>
						</button>
						<button id="exitFullScreen"
							type="button" style="display: block; font-size: 2.5rem;"
							title="Exit Full Screen"
							class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
							id="fullscreen_exit" onclick="makeFullscreen('exit');">
							<i class="md-icon ${buttonDisable ? '' : ' mtcm_icon'} ">fullscreen_exit</i>
						</button>


						<c:if test="${!empty tamAllocationForm.allocation}">
							<div id="checkbox-menu-fileDownload" style="display: flex;">
								<span role="menu" class="eto-dropdown" data-anchor-x="right"
									data-anchor-y="bottom" style="display: flex;">
									<button id="downloadButtonId"
										class="eto-dropdown__toggle  eto-btn eto-btn--link eto-btn--icon-only large-icon"
										style="font-size: 2.5rem;" title="File Download" type="button">
										<i class="md-icon" id="downloadButton">file_download</i>
									</button> 
										<ul class="eto-dropdown__menu" id="downloadMenu" data-affixed
											style="position: absolute !important; left: -100px; display: none;">
											<li name="this-page" data-checkbox-state="checked"
												role="menuitem"><a href="#"
												onclick="tamDownlaodOption('supplierDownload')">Supplier
													Allocation </a></li>

											<li name="all-pages" data-checkbox-state="double"
												role="menuitem"><a href="#"
												onclick="tamDownlaodOption('itemDownload')">Item
													Allocation</a></li>
										</ul>
								</span>

							</div>

						</c:if>
						<div class="eto-checkbox eto-checkbox-menu" id="dropdown-setting"
							style="display: inline-flex; padding-bottom: 1rem;">
							<span role="menu" class="eto-dropdown">
								<button type="button" style="font-size: 2.5rem; width: 100%"
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
			<c:if
				test="${tamAllocationForm.allocation != null && (tamAllocationForm.pastScreen == null || !tamAllocationForm.pastScreen)}">
				<c:choose>
					<c:when test="${tamAllocationForm.selectedFgType == 'CFG'}">
						<%@ include file="tamAllocationGridCFG.jspf"%>
					</c:when>
					<c:when test="${tamAllocationForm.selectedFgType == 'XLOB'}">
						<%@ include file="tamAllocationGridXLOB.jspf"%>
					</c:when>
				</c:choose>
			</c:if>
			<c:if
				test="${tamAllocationForm.allocation != null && tamAllocationForm.pastScreen == true }">
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
					<div class="col-xs-12 col-sm-6">
						<c:if
							test="${!empty tamAllocationForm.allocation.id && !tamAllocationForm.pastScreen}">
							<c:set var="saveType" value="Save-${tamAllocationForm.siteType}" />
							<c:if test="${e2ofn:hasAccess(appContext, 'TAM', saveType)}">
								<label class="eto-checkbox"> <input
									class="eto-checkbox__field" name="copyAllocationToOther"
									type="checkbox" value="copyAllocation"
									onclick="javascript:copyAllocationToSite();"> <span
									class="eto-checkbox__box"></span> <span
									class="eto-checkbox__label"><fmt:message
											key="tam.copy_allocation" /></span>
								</label>
							</c:if>
						</c:if>
					</div>
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
										<fmt:message key="info.tam.data.inherit" var="inheritNote" />
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
				<div id="copyAllocation" style="margin-bottom: 15px;">

					<!-- copy allocation module -->
					<c:if test="${!tamAllocationForm.pastScreen}">
						<%@ include file="applyAllocationModule.jspf"%>
					</c:if>
					<!-- copy allocation module -->

				</div>

				<!-- Footer button Bar start -->

				<div class="row tamFooter">
					<div class="col-xs-12 col-sm-12
						margin-sm-2"
						style="margin-top: 17px; margin-left: 13px;">
						<nav class="eto-form__btns">
							<div class="eto-btn-group">
								<c:set var="saveType" value="Save-${tamAllocationForm.siteType}" />

								<c:if test="${e2ofn:hasAccess(appContext, 'TAM', saveType)}">
									<button type="button" class="eto-btn eto-btn--primary"
										onclick="javascript:goSaveAndContinue();"
										${tamAllocationForm.pastScreen ? ' disabled ' : ''}>
										<fmt:message key="button.save" />
									</button>
									<button id="copyAllocationButton" type="button" class="eto-btn"
										onclick="javascript:goCopyAllocation();"
										${tamAllocationForm.pastScreen ? ' disabled ' : ''}>
										<fmt:message key="button.mass_copy" />
									</button>
									<button id="massUpdateButton" type="button" class="eto-btn"
										onclick="javascript:goMassUpdate();"
										${tamAllocationForm.pastScreen ? ' disabled ' : ''}>
										<fmt:message key="button.mass_update" />
									</button>
								</c:if>
									<c:set var="deleteType"
										value="Delete-${tamAllocationForm.siteType}" />
									<c:if test="${!tamAllocationForm.pastScreen}">
										<c:if test="${e2ofn:hasAccess(appContext, 'TAM', deleteType)}">
											<c:if test="${tamAllocationForm.siteType != 'site'}">
												<button id="deleteButton" type="button" class="eto-btn"
													onclick="javascript:goDel();">
													<fmt:message key="button.delete" />
												</button>
											</c:if>
											<c:if test="${tamAllocationForm.siteType == 'site'}">
												<button id="deleteButton" type="button" class="eto-btn"
													onclick="javascript:deleteFromSite()">
													<fmt:message key="button.delete" />
												</button>
											</c:if>
										</c:if>
									</c:if>
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

	<%@ include file="tamScript.jspf"%>

	<!-- All script goes inside tamScript -->
	<c:set var="tamLevel" value=""/>
	<div class="eto-modal eto-modal--fullscreen"
		id="Delete-Allocation-full-screen"
		style="width: 100%; overflow-y: hidden;">
		<div class="eto-modal__content">
			<header class="eto-modal__header">
				<span>Delete Allocations</span>
				<button class="eto-modal__close" data-modal-close></button>
			</header>
			<section class="eto-modal__body"
				style="padding: 0; margin: 0; overflow-y: hidden;">
				<div
					style="background-color: #F9FBFB; border-bottom: 1px solid #C8CFD2; border-top: 1px solid #C8CFD2; width: 100%">
					<div class="row" style="margin-right: 0;">
						<div class="col-sm-4" style="display: flex;">
							<label class="eto-input__label" style="margin-left: 20px;">Group
								Name</label> <label class="eto-input__label"
								style="margin-left: 20px; font-weight: bold;">${tamAllocationForm.groupName}</label>
						</div>

						<c:if test="${tamAllocationForm.siteType == 'global'}">
							<div class="col-sm-3" style="display: flex;">
								<label class="eto-input__label" style="margin-left: 40px;">Site</label>
								<label class="eto-input__label"
									style="margin-left: 30px; font-weight: bold;">Global</label>
							</div>
							<script>
							$('input[name="level"]').val("WW");
							<c:set var="tamLevel" value="WW"/>
							</script>
						</c:if>

						<c:if test="${tamAllocationForm.siteType == 'region'}">
							<div class="col-sm-3" style="display: flex;">
								<label class="eto-input__label" style="margin-left: 40px;">Region</label>
								<label class="eto-input__label"
									style="margin-left: 60px; font-weight: bold;">${tamAllocationForm.region}</label>
							</div>
							<script>
							$('input[name="level"]').val("${tamAllocationForm.region}");
							<c:set var="tamLevel" value="${tamAllocationForm.region}"/>
							</script>
						</c:if>
						<c:if test="${tamAllocationForm.siteType == 'site'}">
							<div class="col-sm-2" style="display: flex;">
								<label class="eto-input__label">Site</label> <label
									class="eto-input__label">${tamAllocationForm.siteDescription}</label>
							</div>
						</c:if>
					</div>
					<div class="row" style="margin-right: 0;">
						<div style="display: flex; padding-top: 20px; padding-left: 5px;">
							<label> <input class="eto-radio__field allocationType"
								type="radio" name="allocationType" id="both" value="both"
								checked="checked" onclick="javascript:allData();"> <span
								class="eto-radio__box" style="margin-left: 20px;"></span> <span
								class="eto-radio__label" style="display: inline;">Delete
									supplier & item allocations</span>
							</label> <label> <input class="eto-radio__field allocationType"
								type="radio" name="allocationType" id="supplier"
								value="supplier" onclick="javascript:supplierData();"> <span
								class="eto-radio__box" style="margin-left: 1rem;"></span> <span
								class="eto-radio__label" style="display: inline;">Delete
									supplier allocations</span>
							</label> <label> <input class="eto-radio__field allocationType"
								type="radio" name="allocationType" id="item" value="item"
								onclick="javascript:itemData();"> <span
								class="eto-radio__box" style="margin-left: 1rem;"></span> <span
								class="eto-radio__label" style="display: inline;">Delete
									item allocations</span>
							</label>
						</div>
					</div>
				</div>

				<div style="padding-top: 20px; width: 100%;" class="row">


					<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 col-xl-12"
						style="float: right">
						<div style="display: flex; float: right">
						<div style="display: flex;">
								<i class="md-icon"
									style="color: gray; font-size: small; margin-right: 5px;">lens</i>
								<label class="eto-input__label"
									style="font-size: smaller; font-style: oblique;"><fmt:message key="info.TAM.delete.tooltip.local" />									
									</label>
							</div>
							
							<div style="display: flex; margin-left: 10px;">

								<i class="md-icon"
									style="color: gray; font-size: small; margin-right: 5px;">block</i>


								<label class="eto-input__label"
									style="font-size: smaller; font-style: oblique;"><fmt:message key="info.TAM.delete.tooltip.No" /></label>

							</div>
							
							<div style="display: flex; margin-left: 10px;">


								<i class="md-icon"
									style="color: orange; font-size: small; margin-right: 5px;">data_usage</i>
								<label class="eto-input__label"
									style="font-size: smaller; font-style: oblique;">
									<fmt:message key="info.TAM.delete.tooltip.inherited">
									<fmt:param value="${tamAllocationForm.level}" />
									</fmt:message>									
									
									</label>
							</div>
							<div style="display: flex; margin-left: 10px;">

								<i class="md-icon"
									style="color: orange; font-size: small; margin-right: 5px;">tonality</i>

								<label class="eto-input__label"
									style="font-size: smaller; font-style: oblique;"><fmt:message key="info.TAM.delete.tooltip.Partial">
									<fmt:param value="${tamAllocationForm.level}" />
									</fmt:message>									
									</label>
							</div>
							
							<c:if test="${tamAllocationForm.siteType == 'global'}">
	      <div style="display: flex; margin-left: 10px;">
	      					<i class="md-icon" style=" color: teal;font-size: small;margin-right: 5px;">data_usage</i>
	      
	      <label class="eto-input__label" style="font-size: smaller;font-style: oblique;"><fmt:message key="info.TAM.delete.tooltip.Region" /></label>
	         	
	      </div>
	      </c:if>
	      
	      <c:if test="${tamAllocationForm.siteType == 'global'}">
	      <div style="display: flex; margin-left: 10px;">
	      					<i class="md-icon" style=" color: teal;font-size: small;margin-right: 5px;">tonality</i>
	      
	      <label class="eto-input__label" style="font-size: smaller;font-style: oblique;"><fmt:message key="info.TAM.delete.tooltip.Region.Partial" /></label>
	         	
	      </div>
	      </c:if>
						</div>
					</div>
				</div>
				<div style="display: flex; padding-top: 20px; width: 100%;">
					<div class="eto-toggle">
						<div class="eto-toggle__items"
							style="box-shadow: none; margin-left: 10px;">
							<label class="eto-toggle__item"> <input
								class="eto-toggle__input toogle" type="radio" value="GlobalData"
								name="FurtherType" checked="checked" id="GlobalData"
								onclick="javascript:checkData();"> <span
								class="eto-toggle__label">All</span>
							</label> <label class="eto-toggle__item"> <input
								class="eto-toggle__input toogle" type="radio"
								value="InheritedData" name="FurtherType" id="InheritedData"
								onclick="javascript:checkData();"> <span
								class="eto-toggle__label">Inherited data</span>
							</label> <label class="eto-toggle__item"> <input
								class="eto-toggle__input toogle" type="radio" value="LocalData"
								name="FurtherType" id="LocalData"
								onclick="javascript:checkData();"> <span
								class="eto-toggle__label">Local data</span>
							</label>
						</div>
					</div>
					<div style="display: flex; margin-left: 50px;">
						<label class="eto-switch"> <input
							class="eto-switch__field" type="checkbox"
							id="collaspseAllDeleteSites" name="collaspseAllDeleteSites"
							checked="checked" onclick="javascript:collapseAllDeleteDiv()">
							<span class="eto-switch__box"></span> <span
							class="eto-switch__label--on"><fmt:message
									key="tam.collaspseAllSite" /></span> <span
							class="eto-switch__label--off"><fmt:message
									key="tam.collaspseAllSite" /></span>
						</label>
					</div>
					<div style="display: flex; margin-left: 50px;">
						<label class="eto-checkbox"> <input
							class="eto-checkbox__field" type="checkbox"
							id="selectAllDeleteSite" name="selectAllDeleteSite"
							onclick="javascript:selectAllDeleteSites()"> <span
							class="eto-checkbox__box"></span> <span
							class="eto-checkbox__label">Select all regions & sites</span>
						</label>
					</div>

				</div>

				<div id="deleteAllocation"
					style="margin-top: 15px; width: 100%; overflow-y: auto; height: 60%; overflow-x: hidden;">
					<!-- List of Site for Copy Allocation -->

					<%@ include file="deleteAllocations.jspf"%>

					<!-- List of Site for Copy Allocation -->

				</div>
			</section>
			<footer class="eto-modal__footer">
				<button class="eto-btn" data-modal-close>Close</button>
				<button class="eto-btn eto-btn--primary" type="button"
					onclick="javascript:deleteSelected()">Delete</button>
			</footer>
		</div>

	</div>
	<c:if
		test="${!empty tamAllocationForm.allocation && (tamAllocationForm.pastScreen == null || !tamAllocationForm.pastScreen)}">
		<c:forEach var="subHeader" items="${tamAllocationForm.header}">
			<c:forEach var="subH" items="${subHeader.value}">
				<c:if test="${tamAllocationForm.currentDate le subH.endDate}">
					<div class="eto-tooltip" data-anchor-x="center" data-anchor-y="top"
						id="pre_${subH.startDate.time}">
						<div class="eto-tooltip__content">
							<fmt:message key="info.tam_inherited_supplier_tooltip" />
						</div>
						<span class="eto-tooltip__caret"></span>
					</div>
					<div class="eto-tooltip" data-anchor-x="center" data-anchor-y="top"
						id="item_${subH.startDate.time}">
						<div class="eto-tooltip__content">
							<fmt:message key="info.tam_inherited_item_tooltip" />
						</div>
						<span class="eto-tooltip__caret"></span>
					</div>
					<script>
						new eto.Tooltip({ el: document.querySelector('#pre_${subH.startDate.time}') });
						
						new eto.Tooltip({ el: document.querySelector('#item_${subH.startDate.time}') });
						
						new eto.Dropdown({ el: document.querySelector('#dropMenu${subH.startDate.time}') });						
					
				</script>
				</c:if>
			</c:forEach>
		</c:forEach>

		<div class="eto-popover col-xs-10 col-sm-6 col-md-4 col-lg-3 open"
			id="popoverDateSelector" data-anchor-x="center"
			data-anchor-y="bottom" data-affixed=""
			style="position: absolute !important; left: 50%; top: 50%; outline: currentcolor none medium; display: none;"
			tabindex="0">
			<div class="eto-popover__content">
				<span class="eto-popover__title"><fmt:message
						key="tam.menu.copy.to.specific" /></span>

				<!-- New implementation as select component insted of combo box component -->
				<div class="eto-select" id="select-copy-from">
					<label class="eto-select__label"><fmt:message
							key="tam.from" /></label>
					<div class="eto-select__field-container">
						<select class="eto-select__field" name="combobox-copy-from">
							<c:set var="countHeaderMonth" value="${0}" />
							<c:forEach var="headerMonth" items="${tamAllocationForm.header}">
								<c:forEach items="${headerMonth.value}" var="dates">
									<c:set var="countHeaderMonth" value="${countHeaderMonth+1}" />
									<fmt:formatDate var="month" pattern="MMM dd"
										value="${dates.startDate}" />
									<c:if test="${dates.endDate ge tamAllocationForm.currentDate}">
										<option value="${countHeaderMonth}">${month}</option>
									</c:if>
								</c:forEach>
							</c:forEach>
						</select>
					</div>
				</div>
				<br>
				<c:set var="countHeaderMonth" value="${0}" />
				<div class="eto-select" id="select-copy-to">
					<label class="eto-select__label"><fmt:message key="tam.to" /></label>
					<div class="eto-select__field-container">
						<select class="eto-select__field" name="combobox-copy-to">
							<c:forEach var="headerMonth" items="${tamAllocationForm.header}">
								<c:forEach items="${headerMonth.value}" var="dates">
									<c:set var="countHeaderMonth" value="${countHeaderMonth+1}" />
									<fmt:formatDate var="month" pattern="MMM dd"
										value="${dates.startDate}" />
									<fmt:formatDate var="monthEnd" pattern="MMM dd"
										value="${dates.endDate}" />
									<c:if test="${dates.endDate ge tamAllocationForm.currentDate}">
										<option value="${countHeaderMonth}">${monthEnd}</option>
									</c:if>
								</c:forEach>
							</c:forEach>
						</select>
					</div>
				</div>
				<br>
				<button type="button" class="eto-btn eto-btn--primary"
					onclick="copyToSpecificPopover();">Copy</button>
				<button type="button" class="eto-btn"
					onclick="$('#popoverDateSelector').hide();">Cancel</button>
				<script>
				fromDatePopover = new eto.SelectInput({
						  el: document.querySelector('#select-copy-from')
				});
				toDatePopover = new eto.SelectInput({
						el: document.querySelector('#select-copy-to')
				});
				</script>
			</div>
			<div class="focus-trap" tabindex="0"
				style="height: 0px; outline: currentcolor none medium;"></div>
		</div>
	</c:if>
	<div class="eto-modal" id="delete-from-site-level-modal"
		style="width: 100%; display: inline;">
		<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4"
			style="height: 60%; overflow: hidden;">
			<header class="eto-modal__header">
				<span>Delete</span>
			</header>
			<section class="eto-modal__body"
				style="-ms-overflow-y: hidden; overflow: hidden; min-height: 50%;">
				<iframe id="deleteFrame" frameborder="0" marginheight="0"
					marginwidth="0" width="100%" height="100%" src="deletesite.jsp">
				</iframe>
			</section>
			<footer class="eto-modal__footer">
				<button class="eto-btn" data-modal-close>Close</button>
				<button class="eto-btn eto-btn--primary" type="button"
					onclick="javascript:deleteSite()">Delete</button>
			</footer>
		</div>
	</div>

	<div class="eto-modal" id="fresh-TAM-error-modal">
		<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
			<header class="eto-modal__header">
				<span>Error</span>
			</header>
			<section class="eto-modal__body">
				<p>
					<c:if test="${tamAllocationForm.siteType == 'region'}">
						<fmt:message key="info.allocation.new.record.region.error" />
					</c:if>
					<c:if test="${tamAllocationForm.siteType == 'global'}">
						<fmt:message key="info.allocation.new.record.error" />
					</c:if>
				</p>
			</section>
			<footer class="eto-modal__footer">
				<button class="eto-btn" data-modal-close>Ok</button>
			</footer>
		</div>
	</div>

	<div class="eto-modal" id="final-TAM-delete-modal">
		<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
			<header class="eto-modal__header">
				<span>Warning</span>
			</header>
			<section class="eto-modal__body">
				<p>
					<fmt:message key="info.allocation.final.delete" />
				</p>
			</section>
			<footer class="eto-modal__footer">
				<button class="eto-btn" type="button"
					onclick="javascript:deleteAllocation();" data-modal-close>Yes</button>
				<button class="eto-btn eto-btn--primary" data-modal-close>No</button>
			</footer>
		</div>
	</div>

	<div class="eto-tooltip col-xs-10 col-sm-6 col-md-4 col-lg-3"
		id="tooltip-fg">
		<div class="eto-tooltip__content">
			<span class="eto-tooltip__title"><span id="fgTool" style="overflow-wrap: anywhere;"> <c:out
						value="${tamAllocationForm.groupName}" escapeXml="true" /></span></span>
		</div>
		<span class="eto-tooltip__caret"></span>
	</div>
	<div class="eto-tooltip col-xs-10 col-sm-6 col-md-4 col-lg-3"
		id="tooltip-site">
		<div class="eto-tooltip__content">
			<span class="eto-tooltip__title" style="overflow-wrap: anywhere;"> <c:out
					value="${tamAllocationForm.siteDescription}" escapeXml="true" /></span>
		</div>
		<span class="eto-tooltip__caret"></span>
	</div>
	<script>
	<c:if test="${not empty tamAllocationForm.allocation}">
	var fgName= $("#groupName").val();
	var expandFgLength= ${expandFgLength};
	if(fgName.length>expandFgLength){
	var fgToolTip = new eto.Tooltip({ el: document.querySelector('#tooltip-fg'),anchorX : 'center',anchorY: 'top'});
	}
	</c:if>
	</script>
	<%@ include file="../fullModal.jspf"%>
</body>
</html>