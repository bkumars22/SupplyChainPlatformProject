<%@ include file="common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<style type="text/css">
.settingHeading {
	border: 1px solid #999999;
	padding: 2px;
	background-color: #d1d6f0;
	font-weight: bold;
	text-align: left;
}
</style>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="UserProfile" />
<title>User Profile</title>
<script>

function goToDashboard(){
	window.location.href = 'dashboard';
}

function goSaveProfile()
{  
    document.forms[0].action="saveProfile";
	document.forms[0].submit();
	showWaitBusy();	
}

function goLocale()
{
    document.forms[0].action="setActiveLocale";
	document.forms[0].submit();
}

function goAddDelegate()
{
    document.forms[0].action="addDelegate";    
	document.forms[0].submit();
	showWaitBusy();	
}

function goUpdateDelegate()
{
    document.forms[0].action="updateDelegate";    
	document.forms[0].submit();
	showWaitBusy();	
}

function goDeleteDelegate()
{
    document.forms[0].action="deleteDelegate";
	document.forms[0].submit();
	showWaitBusy();	
}

function setDelegateFields(idx)
{
	var rowId = "#delegateRow"+idx+" > td";
	var row = $(rowId);
	$("#delegateUserId").val($(row[1]).text());
	$("#delegateStartDate").val($(row[2]).text());
	$("#delegateEndDate").val($(row[3]).text());
}
$(document).ready(function() 
		{
			$("#delegateUserId").autocomplete(
				"ajaxQueryUserId.do",
				{
					delay:500,
					minChars:1,
					matchSubset:1,
					maxItemsToShow:20,						
					matchContains:0,
					cacheLength:20,
					autoFill:true,
					selectedCurrent:true
				})
			$(".ageField").blur(function(){
				var field = this;
				checkNumericField(field,false,function() {
					field.value = roundDecimal(field.value,0);
				});
			})
		});

</script>
</head>
<body>
	<fmt:message var="clearTitle" key="button.title.clear" />
	<fmt:message var="generalTitle" key="user.generalTitle" />
	<fmt:message var="delegateTitle" key="user.delegateTitle" />
	<fmt:message var="dashboardTitle" key="user.dashboardTitle" />
	<label	style="white-space: nowrap; margin-right: 30px"><h1>
	<fmt:message key="user.edit.profile" /></h1></label>
	<form name="userProfileForm" action="saveProfile" method="POST">
		<e2o:errors />
		<e2i2:container title="${generalTitle}" width="100%">
			<e2i2:instructionsarea>
				<fmt:message key="user.general_instructions" />
			</e2i2:instructionsarea>
			<table width="100%" id="profile">
				<col nowrap width="150px" style="padding-top: 4; font-weight: bold"
					align="right" valign="top" />
				<col style="padding-top: 4;" valign="top" />
				<col nowrap width="150px" style="padding-top: 4; font-weight: bold"
					align="right" valign="top" />
				<col width="150px" style="padding-top: 4;" valign="top" />

				<tr>
					<td><fmt:message key="user.userid" /></td>
					<td><c:out value="${userProfileForm.user.userId}" /></td>
					<td><fmt:message key="user.dateFormat" /></td>
					<td><c:out
							value="${userProfileForm.user.preferences.DATE_FORMAT}" /></td>
				</tr>
				<tr>
					<td><fmt:message key="user.fullname" /></td>
					<td><c:out value="${userProfileForm.user.userName}" /></td>
					<td><fmt:message key="user.timeFormat" /></td>
					<td><c:out
							value="${userProfileForm.user.preferences.TIME_FORMAT}" /></td>
				</tr>
				<tr>
					<td><fmt:message key="user.role" /></td>
					<td><c:out value="${userProfileForm.role.roleName}" /></td>
					<td><fmt:message key="user.timezone" /></td>
					<td><c:out
							value="${userProfileForm.user.preferences.TIMEZONE}" /></td>
				</tr>
				<tr>
					<td><fmt:message key="user.email" /></td>
					<td><c:out value="${userProfileForm.user.emailAddress}" /></td>
					<td><fmt:message key="user.mailFormat" /></td>
					<td><c:out
							value="${userProfileForm.user.preferences.MAIL_FORMAT}" /></td>
				</tr>
				<tr>
					<td><fmt:message key="user.availableLanguages" /></td>
					<td><select name="language" class="inputField" style="width:130px">
							<c:forEach items="${userProfileForm.availableLanguages}" var="lang">
								<option value="${lang.value}" ${lang.value eq userProfileForm.language ? 'selected="selected"' : ''}>
									<c:out value="${lang.label}" />
								</option>
							</c:forEach>
						</select></td>
					<td><fmt:message key="user.pageSize" /></td>
					<td><c:out
							value="${userProfileForm.user.preferences.PAGE_SIZE}" /></td>
				</tr>
				<%-- <tr>
					<td></td>
					<td>
						<e2i2:button id="langButton"
							onclick="javascript:goLocale();">
							<fmt:message key="button.changeLanguage" />
						</e2i2:button>
						<div class='eto-btn-group'>
							<button id="langButton" type='button'
								class='eto-btn eto-btn--primary'
								onclick="javascript:goLocale();">
								<fmt:message key="button.changeLanguage" />
							</button>
						</div>
					</td>
				</tr> --%>
			</table>

			<div class='eto-btn-group'
				style="margin-left: 150px; margin-top: 5px;">
				<button id="langButton" type='button'
					class='eto-btn eto-btn--primary' onclick="javascript:goLocale();">
					<fmt:message key="button.changeLanguage" />
				</button>
			</div>
			<%-- <div style="margin: 50px">
				<label style="margin: 5px"><fmt:message key="user.userid" /></label>
				<label style="margin: 5px"><c:out
						value="${userProfileForm.user.userId}" /></label>
				<div style="float: right; display: inline-block;">
					<label style="margin: 5px"><fmt:message
							key="user.dateFormat" /></label> <label style="margin: 5px"><c:out
							value="${userProfileForm.user.preferences.DATE_FORMAT}" /></label>
				</div>
			</div> --%>

		</e2i2:container>

		<c:if test="${e2ofn:getConfigValue('pcm.user.allowDelegate')}">
			<e2i2:container title="${delegateTitle}" width="100%"
				collapsable="yes">

				<e2o:scrollableTable height="100%">
					<e2o:scrollableTableHeader>
						<e2o:scrollableTableHeaderRow>
							<td width="50px"><fmt:message key="label.select" /></td>
							<td width="150px"><fmt:message key="user.delegator" /></td>
							<td width="150px"><fmt:message key="user.delegate.fromDate" /></td>
							<td><fmt:message key="user.delegate.toDate" /></td>
						</e2o:scrollableTableHeaderRow>
					</e2o:scrollableTableHeader>
					<e2o:scrollableTableBody>
						<c:forEach var="delegate"
							items="${userProfileForm.whereDelegateFor}" varStatus="delidx">
							<e2o:scrollableTableBodyRow>
								<td></td>
								<td><c:out value="${delegate.delegator.userId}" /></td>
								<td><fmt:formatDate value="${delegate.effectiveFromDate}"
										pattern="${appContext.currentDateFormat}" /></td>
								<td><fmt:formatDate value="${delegate.effectiveToDate}"
										pattern="${appContext.currentDateFormat}" /></td>
							</e2o:scrollableTableBodyRow>
						</c:forEach>
					</e2o:scrollableTableBody>
				</e2o:scrollableTable>

				<e2o:scrollableTable
					height="${fn:length(userProfileForm.delegates) > 5 ? '150px' : '100%'}">
					<e2o:scrollableTableHeader>
						<e2o:scrollableTableHeaderRow>
							<td width="50px"><fmt:message key="label.select" /></td>
							<td width="150px"><fmt:message key="user.delegate" /></td>
							<td width="150px"><fmt:message key="user.delegate.fromDate" /></td>
							<td><fmt:message key="user.delegate.toDate" /></td>
						</e2o:scrollableTableHeaderRow>
					</e2o:scrollableTableHeader>
					<e2o:scrollableTableBody>
						<c:forEach var="delegate" items="${userProfileForm.delegates}"
							varStatus="delidx">
							<e2o:scrollableTableBodyRow id="delegateRow${delidx.index}">
								<td><input type="radio" name="selectedDelegateIdx"
										value="${delidx.index}"
										onclick="setDelegateFields(this.value)"
										${userProfileForm.selectedDelegateIdx == delidx.index ? 'checked="checked"' : ''} /></td>
								<td><c:out value="${delegate.delegateUserId}" /></td>
								<td><fmt:formatDate value="${delegate.effectiveFromDate}"
										pattern="${appContext.currentDateFormat}" /></td>
								<td><fmt:formatDate value="${delegate.effectiveToDate}"
										pattern="${appContext.currentDateFormat}" /></td>

							</e2o:scrollableTableBodyRow>

						</c:forEach>
					</e2o:scrollableTableBody>
				</e2o:scrollableTable>

				<e2i2:formtable>
					<col nowrap
						style="padding-top: 4; width: 100px; text-align: right;"
						valign="top" />
					<col valign="top" align="left" style="width: 150px" />
					<col nowrap
						style="padding-top: 4; width: 100px; text-align: right;"
						valign="top" />
					<col valign="top" align="left" />

					<tr>
						<e2i2:formlabel>
							<fmt:message key="user.delegate" />
						</e2i2:formlabel>
						<td><input type="text" class="inputField"
								id="delegateUserId" name="delegateUserId"
								value="${fn:escapeXml(userProfileForm.delegateUserId)}" /></td>
						<e2i2:formlabel>
							<fmt:message key="user.delegate.fromDate" />
						</e2i2:formlabel>
						<td><span style="white-space: nowrap"> <input
									class="inputField" style="clear:none"
									id="delegateStartDate" name="delegateStartDate" maxlength="100" size="12"
									readonly="readonly" value="${fn:escapeXml(userProfileForm.delegateStartDate)}" /> <e2ot:calendarControl
									bindToFieldId="delegateStartDate" clearTitle="${clearTitle}" />
						</span></td>
					</tr>
					<tr>
						<td colspan="2"></td>
						<e2i2:formlabel>
							<fmt:message key="user.delegate.toDate" />
						</e2i2:formlabel>
						<td><span style="white-space: nowrap"> <input
									class="inputField" style="clear:none"
									id="delegateEndDate" name="delegateEndDate" maxlength="100" size="12"
									readonly="readonly" value="${fn:escapeXml(userProfileForm.delegateEndDate)}" /> <e2ot:calendarControl
									bindToFieldId="delegateEndDate" clearTitle="${clearTitle}" />
						</span></td>
					</tr>
				</e2i2:formtable>
				<e2i2:buttonbar>
					<e2i2:button onclick="javascript:goAddDelegate()">
						<fmt:message key="button.add" />
					</e2i2:button>
					<e2i2:button onclick="javascript:goUpdateDelegate()">
						<fmt:message key="button.update" />
					</e2i2:button>

					<e2i2:button onclick="javascript:goDeleteDelegate()">
						<fmt:message key="button.delete" />
					</e2i2:button>
				</e2i2:buttonbar>
			</e2i2:container>
		</c:if>

		<e2i2:container title="${dashboardTitle}" width="100%">
			<c:set var="numColumns" value="6" />
			<table width="100%">
				<col nowrap
					style="padding-top: 4; width: 150px; text-align: left; padding-right: 5px"
					valign="top" />
				<!-- <col valign="top" align="left" style="width: 150px" />
				<col nowrap style="padding-top: 4; width: 150px; text-align: right;"
					valign="top" />
				<col valign="top" align="left" />
				<col nowrap style="padding-top: 4; width: 150px; text-align: right;"
					valign="top" />
				<col style="width: 150px;padding-right: 5px" valign="top" align="left" /> -->
				<tr>
					<td align="left" colspan="${numColumns}"><e2i2:instructionsarea>
							<fmt:message key="user.default_dashboard" />
						</e2i2:instructionsarea></td>
				</tr>
				<tr>
				<tr>
					<td colspan="${numColumns}" class="settingHeading"><fmt:message
							key="user.default_dashboard.general" /></td>
				</tr>
				<tr>
					<td><label class="eto-input__label"><fmt:message
								key="user.default_dashboard.ni" /></label></td>
					<td><input class="eto-input__field" type="text" name="name"
						onblur="checkNumericField(this,false)" /></td>
					<td><label class="eto-input__label"><fmt:message
								key="user.default_dashboard.refreshInSeconds" /></label></td>

					<%-- <td><html:text styleClass="inputField ageField"
							styleId="itemsDaysOld"
							property="userPreferenceValue(DB_ITEMS_DAYSOLD)"
							onblur="checkNumericField(this,false)" /></td> --%>
					<%-- <td><fmt:message key="user.default_dashboard.refreshInSeconds" /></td> --%>
					<td>
						<%-- <html:text styleClass="inputField"
							styleId="refreshDBTimeout"
							property="userPreferenceValue(DB_REFRESH_SECS)"
							onblur="checkNumericField(this,false)" /> --%> <input
						class="eto-input__field" type="text" name="name"
						onblur="checkNumericField(this,false)" style="width: 150px" />
					</td>
				</tr>


				<c:if test="${e2ofn:hasAccess(appContext, 'COST_RECORD', 'Read')}">
					<tr>
						<td colspan="${numColumns}" class="settingHeading"><fmt:message
								key="user.default_dashboard.costRecord" /></td>
					</tr>
					<tr>
						<td class="editProfileColWidth"><fmt:message
								key="user.default_dashboard.status_instructions" /></td>
						<td>
							<table cellpadding="0" cellspacing="0"
								style="padding: 0px; margin: 0px; border: 1px solid #999999; width: 150px; background-color: #ffffff; border: 0px solid #ffffff; border-radius: 4">
								<c:forEach var="state"
									items="${userProfileForm.availableStates['Sourcing']}">
									<tr>
										<td>
											<%-- <html:multibox style="height:14px"
												property="userPreferenceValueAsArray(DB_STATUS_costRecord)"
												value="${state.name}" />${state.label} --%> <label
											class="eto-checkbox"> <input
												class="eto-checkbox__field" type="checkbox"
												name="applyToAll" value="${state.name}"> <span
												class="eto-checkbox__box"></span> <span
												class="eto-checkbox__label">${state.label}</span>
										</label>
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
						<td><fmt:message key="user.default_dashboard.ownerOnly" /></td>
						<td class="editProfileColWidth">
							<%-- <html:radio
								property="userPreferenceValue(DB_STATUS_costRecord_OWNER_ONLY)"
								value="true">
								<fmt:message key="info.yes" />
							</html:radio> <br> <html:radio
								property="userPreferenceValue(DB_STATUS_costRecord_OWNER_ONLY)"
								value="false">
								<fmt:message key="info.no" />
							</html:radio> --%> <label class="eto-radio"> <input
								class="eto-radio__field" type="radio" name="radio-example"
								value="true"> <span class="eto-radio__box"></span> <span
								class="eto-radio__label"><fmt:message key="info.yes" /></span>
						</label> <label class="eto-radio"> <input class="eto-radio__field"
								type="radio" name="radio-example" value="false"> <span
								class="eto-radio__box"></span> <span class="eto-radio__label"><fmt:message
										key="info.no" /></span>
						</label>
						</td>
						<td><fmt:message key="user.default_dashboard.lastChanged" /></td>
						<td style="padding-right: 5px">
							<%-- <html:text styleClass="inputField ageField" size="4"
								styleId="costRecordDAYSOLD"
								property="userPreferenceValue(DB_STATUS_costRecord_DAYSOLD)" /> --%>
							<input class="eto-input__field" type="text" name="name" size="4" style="width: 150px"/>
						</td>
					</tr>
				</c:if>

				<c:if test="${e2ofn:hasAccess(appContext, 'SOURCING_LANE', 'Read')}">
					<tr>
						<td colspan="${numColumns}" class="settingHeading"><fmt:message
								key="user.default_dashboard.sourcingLane" /></td>
					</tr>
					<tr>
						<td class="editProfileColWidth"><fmt:message
								key="user.default_dashboard.status_instructions" /></td>
						<td>
							<table cellpadding="0" cellspacing="0"
								style="padding: 0px; margin: 0px; border: 1px solid #999999; width: 150px; background-color: #ffffff; border: 0px solid #ffffff; border-radius: 4">
								<c:forEach var="state"
									items="${userProfileForm.availableStates['Sourcing']}">
									<tr>
										<%-- <td><html:multibox style="height:14px"
												property="userPreferenceValueAsArray(DB_STATUS_sourcingLane)"
												value="${state.name}" />${state.label}</td> --%>
										<label class="eto-checkbox"> <input
											class="eto-checkbox__field" type="checkbox" name="applyToAll"
											value="${state.name}"> <span
											class="eto-checkbox__box"></span> <span
											class="eto-checkbox__label">${state.label}</span>
										</label>
									</tr>
								</c:forEach>
							</table>
						</td>
						<td><fmt:message key="user.default_dashboard.ownerOnly" /></td>
						<td class="editProfileColWidth">
							<%-- <html:radio
								property="userPreferenceValue(DB_STATUS_sourcingLane_OWNER_ONLY)"
								value="true">
								<fmt:message key="info.yes" />
							</html:radio> <br> <html:radio
								property="userPreferenceValue(DB_STATUS_sourcingLane_OWNER_ONLY)"
								value="false">
								<fmt:message key="info.no" />
							</html:radio> --%>

							<div class="eto-radio-group">
								<label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="true"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.yes" /></span>
								</label> <label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="false"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.no" /></span>
								</label>
							</div>
						</td>
						<td><fmt:message key="user.default_dashboard.lastChanged" /></td>
						<td style="padding-right: 5px">
							<%-- <html:text styleClass="inputField ageField" size="4"
								styleId="sourcingLaneDAYSOLD"
								property="userPreferenceValue(DB_STATUS_sourcingLane_DAYSOLD)" /> --%>
							<input class="eto-input__field" type="text" name="name" size="4" style="width: 150px"/>
						</td>
					</tr>
				</c:if>
				<c:if test="${e2ofn:hasAccess(appContext, 'REBATE', 'Read')}">
					<tr>
						<td colspan="${numColumns}" class="settingHeading"><fmt:message
								key="user.default_dashboard.rebateProgram" /></td>
					</tr>
					<tr>
						<td class="editProfileColWidth"><fmt:message
								key="user.default_dashboard.status_instructions" /></td>

						<td>

							<table cellpadding="0" cellspacing="0"
								style="padding: 0px; margin: 0px; border: 1px solid #999999; width: 150px; background-color: #ffffff; border: 0px solid #ffffff; border-radius: 4">
								<c:forEach var="state"
									items="${userProfileForm.availableStates['Rebate']}">
									<tr>
										<%-- <td><html:multibox style="height:14px"
												property="userPreferenceValueAsArray(DB_STATUS_rebateProgram)"
												value="${state.name}" />${state.label}</td> --%>

										<label class="eto-checkbox"> <input
											class="eto-checkbox__field" type="checkbox" name="applyToAll"
											value="${state.name}"> <span
											class="eto-checkbox__box"></span> <span
											class="eto-checkbox__label">${state.label}</span>
										</label>
									</tr>
								</c:forEach>
							</table>

						</td>
						<td><fmt:message key="user.default_dashboard.ownerOnly" /></td>
						<td class="editProfileColWidth">
							<%-- <html:radio
								property="userPreferenceValue(DB_STATUS_rebateProgram_OWNER_ONLY)"
								value="true">
								<fmt:message key="info.yes" />
							</html:radio> <br> <html:radio
								property="userPreferenceValue(DB_STATUS_rebateProgram_OWNER_ONLY)"
								value="false">
								<fmt:message key="info.no" />
							</html:radio> --%>

							<div class="eto-radio-group">
								<label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="true"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.yes" /></span>
								</label> <label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="false"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.no" /></span>
								</label>
							</div>
						</td>
						<td><fmt:message key="user.default_dashboard.lastChanged" /></td>
						<td style="padding-right: 5px">
							<%-- <html:text styleClass="inputField ageField" size="4"
								styleId="rebateProgramDAYSOLD"
								property="userPreferenceValue(DB_STATUS_rebateProgram_DAYSOLD)" /> --%>

							<input class="eto-input__field" type="text" name="name" size="4" style="width: 150px"/>
						</td>
					</tr>
				</c:if>

				<c:if test="${e2ofn:hasAccess(appContext, 'BOM', 'Read')}">
					<tr>
						<td colspan="${numColumns}" class="settingHeading"><fmt:message
								key="user.default_dashboard.bom" /></td>
					</tr>
					<tr>
						<td class="editProfileColWidth"><fmt:message
								key="user.default_dashboard.status_instructions" /></td>
						<td>
							<table cellpadding="0" cellspacing="0"
								style="padding: 0px; margin: 0px; border: 1px solid #999999; width: 150px; background-color: #ffffff; border: 0px solid #ffffff; border-radius: 4">
								<c:forEach var="state"
									items="${userProfileForm.availableStates['BOM']}">
									<tr>
										<%-- <td><html:multibox style="height:14px"
												property="userPreferenceValueAsArray(DB_STATUS_bom)"
												value="${state.name}" />${state.label}</td> --%>
										<td><label class="eto-checkbox"> <input
												class="eto-checkbox__field" type="checkbox"
												name="applyToAll" value="${state.name}"> <span
												class="eto-checkbox__box"></span> <span
												class="eto-checkbox__label">${state.label}</span>
										</label></td>
									</tr>
								</c:forEach>
							</table>
						</td>
						<td><fmt:message key="user.default_dashboard.ownerOnly" /></td>
						<td class="editProfileColWidth">
							<%-- <html:radio
								property="userPreferenceValue(DB_STATUS_bom_OWNER_ONLY)"
								value="true">
								<fmt:message key="info.yes" />
							</html:radio> <br> <html:radio
								property="userPreferenceValue(DB_STATUS_bom_OWNER_ONLY)"
								value="false">
								<fmt:message key="info.no" />
							</html:radio> --%>

							<div class="eto-radio-group">
								<label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="true"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.yes" /></span>
								</label> <label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="false"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.no" /></span>
								</label>
							</div>
						</td>
						<td><fmt:message key="user.default_dashboard.lastChanged" /></td>
						<td style="padding-right: 5px">
							<%-- <html:text styleClass="inputField ageField" size="4"
								styleId="bomDAYSOLD"
								property="userPreferenceValue(DB_STATUS_bom_DAYSOLD)" /> --%> <input
							class="eto-input__field" type="text" name="name" size="4" style="width: 150px"/>
						</td>
					</tr>
				</c:if>

				<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}">
					<tr>
						<td colspan="${numColumns}" class="settingHeading"><fmt:message
								key="user.default_dashboard.forecast" /></td>
					</tr>
					<tr>
						<td class="editProfileColWidth"><fmt:message
								key="user.default_dashboard.status_instructions" /></td>
						<td>
							<table cellpadding="0" cellspacing="0"
								style="padding: 0px; margin: 0px; border: 1px solid #999999; width: 150px; background-color: #ffffff; border: 0px solid #ffffff; border-radius: 4">
								<c:forEach var="state"
									items="${userProfileForm.availableStates['Forecast']}">
									<tr>
										<td>
											<%-- <html:multibox style="height:14px"
												property="userPreferenceValueAsArray(DB_STATUS_forecast)"
												value="${state.name}" />${state.label} --%> <label
											class="eto-checkbox"> <input
												class="eto-checkbox__field" type="checkbox"
												name="applyToAll" value="${state.name}"> <span
												class="eto-checkbox__box"></span> <span
												class="eto-checkbox__label">${state.label}</span>
										</label>
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
						<td><fmt:message key="user.default_dashboard.ownerOnly" /></td>
						<td class="editProfileColWidth">
							<%-- <html:radio
								property="userPreferenceValue(DB_STATUS_forecast_OWNER_ONLY)"
								value="true">
								<fmt:message key="info.yes" />
							</html:radio> <br> <html:radio
								property="userPreferenceValue(DB_STATUS_forecast_OWNER_ONLY)"
								value="false">
								<fmt:message key="info.no" />
							</html:radio> --%>

							<div class="eto-radio-group">
								<label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="true"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.yes" /></span>
								</label> <label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="false"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.no" /></span>
								</label>
							</div>
						</td>
						<td><fmt:message key="user.default_dashboard.lastChanged" /></td>
						<td style="padding-right: 5px">
							<%-- <html:text styleClass="inputField ageField" size="4"
								styleId="forecastDAYSOLD"
								property="userPreferenceValue(DB_STATUS_forecast_DAYSOLD)" /> --%>

							<input class="eto-input__field" type="text" name="name" size="4" style="width: 150px"/>
						</td>
					</tr>
				</c:if>

				<!-- Adjustable Forecasts -->
				<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}">
					<tr>
						<td colspan="${numColumns}" class="settingHeading"><fmt:message
								key="user.default_dashboard.forecast_ADJ" /></td>
					</tr>
					<tr>
						<td class="editProfileColWidth"><fmt:message
								key="user.default_dashboard.status_instructions" /></td>
						<td>
							<table cellpadding="0" cellspacing="0"
								style="padding: 0px; margin: 0px; border: 1px solid #999999; width: 150px; background-color: #ffffff; border: 0px solid #ffffff; border-radius: 4">
								<c:forEach var="state"
									items="${userProfileForm.availableStates['Forecast_ADJ']}">
									<tr>
										<td>
											<%-- <html:multibox style="height:14px"
												property="userPreferenceValueAsArray(DB_STATUS_forecast_ADJ)"
												value="${state.name}" />${state.label} --%> <label
											class="eto-checkbox"> <input
												class="eto-checkbox__field" type="checkbox"
												name="applyToAll" value="${state.name}"> <span
												class="eto-checkbox__box"></span> <span
												class="eto-checkbox__label">${state.label}</span>
										</label>

										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
						<td><fmt:message key="user.default_dashboard.ownerOnly" /></td>
						<td class="editProfileColWidth">
							<%-- <html:radio
								property="userPreferenceValue(DB_STATUS_forecast_ADJ_OWNER_ONLY)"
								value="true">
								<fmt:message key="info.yes" />
							</html:radio> <br> <html:radio
								property="userPreferenceValue(DB_STATUS_forecast_ADJ_OWNER_ONLY)"
								value="false">
								<fmt:message key="info.no" />
							</html:radio> --%>

							<div class="eto-radio-group">
								<label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="true"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.yes" /></span>
								</label> <label class="eto-radio"> <input
									class="eto-radio__field" type="radio" name="radio-example"
									value="false"> <span class="eto-radio__box"></span> <span
									class="eto-radio__label"><fmt:message key="info.no" /></span>
								</label>
							</div>
						</td>
						<td><fmt:message key="user.default_dashboard.lastChanged" /></td>
						<td style="padding-right: 5px">
							<%-- <html:text styleClass="inputField ageField" size="4"
								styleId="forecastADJDAYSOLD"
								property="userPreferenceValue(DB_STATUS_forecast_ADJ_DAYSOLD)" /> --%>

							<input class="eto-input__field" type="text" name="name" size="4" style="width: 150px"/>
						</td>
					</tr>
				</c:if>

			</table>
		</e2i2:container>
	<form>
	<%-- <e2i2:buttonbar>
		<e2i2:button id="saveButton" onclick="javascript:goSaveProfile()">
			<fmt:message key="button.save" />
		</e2i2:button>
		<e2i2:button id="cancelButton" onclick="dashboard.do">
			<fmt:message key="button.cancel" />
		</e2i2:button>
	</e2i2:buttonbar> --%>

	<div class="eto-btn-group">
		<button id="saveButton" type="button" class="eto-btn eto-btn--primary"
			onclick="javascript:goSaveProfile()">
			<fmt:message key="button.save" />
		</button>
		<button id="cancelButton" type="button"
			class="eto-btn"
			onclick="goToDashboard()">
			<fmt:message key="button.cancel" />
		</button>
	</div>
</body>
</html>
