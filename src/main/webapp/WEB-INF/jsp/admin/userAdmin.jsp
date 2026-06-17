<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />

<e2ot:help contextName="Admin-User" />
<title>User</title>
</head>
<script> 
var fDataChanged = false;
var agentKeys = [];

function findContact(name,selectedContactKey)
{
    var url = "./viewSearchFinder?finderName=ContactFinder"
               +"&finderParamValue="+name
               +"&resultCallbackFunc=goChangeContact";
    
    if (selectedContactKey != null)
    {
        if (selectedContactKey.id != undefined)
        {
            selectedContactKey = selectedContactKey.id;
        }
        var element = document.getElementById(selectedContactKey);
        url += '&resultField='+element.id;
    }
        url +="&multiSelect=false";
        callFinderAjax(url,"ContactFinder"); 
}

function addAgent()
{
    doFinderPopup('BusinessFinder',null,'agentAdded');
}

function agentAdded(finderValues)
{  
	for(var i=0; i < finderValues.length; i++)
	{
			agentKeys.push(finderValues[i][0]);
			var innerHTML = $("#populateTags").html();
	        var newTagHTML = "<span class='eto-tag eto-tag--sm'><span class=eto-tag__label>"+finderValues[i][3] + ' - ' + finderValues[i][1]+"</span><span class=eto-tag__remove tabindex=0 onclick=closeThisTag(this) id="+finderValues[i][0]+"><i class='md-icon md-icon--sm'>close</i></span></span>";
	        var completeHTML = newTagHTML + innerHTML;
	        $("#populateTags").html(completeHTML);
	}
	dataChanged();
}

function canChangePage(op,callback)
{
   var fr = partial(canChangePageCallback,callback);
   var rc = handleSelectionChange(null,fr);
   if (rc == true)
   {
	   canChangePageCallback(callback);
   }
   return rc;
}

function canChangePageCallback(callback)
{
	var button = document.getElementById('${userAdminForm.selectedUserKey}');   
    if (button != null)
    {
       button.checked = false;
    }
    callback.apply();
}

function dataChanged()
{
    fDataChanged = true;
	var msgArea = document.getElementById('unsavedDataMsg');
	if (msgArea != null)
	{
		msgArea.innerText = '<fmt:message key="info.unsaved_data"/>';
	}		
}

function selectAllAgents()
{
    // Select all the agents so they get submitted
	document.forms[0].agentKeyString.value = agentKeys;
}

function goSave()
{
	var emailId = $("#userEmail").val();
	 if(emailId.trim()!=''){
	  if(validateEmailAddress(emailId)==false){
		  showOkMessageBox('OK', 'ERROR',
					"<fmt:message key='errors.email.ui.validate'/>",
					"<fmt:message key='msg.error'/>", function() {
					});
		 return;
	  }
	 }
   selectAllAgents();
   document.forms[0].action="saveUserDetails";
   document.forms[0].unsavedData.value = false;   
   document.forms[0].submit();
   showWaitBusy();
}

function goSaveAndContinue()
{
	selectAllAgents();
	document.forms[0].action="saveUserDetailsAndContinue";
	document.forms[0].unsavedData.value = false;
	document.forms[0].submit();
	showWaitBusy();
}

function goImportNewUser()
{
    document.forms[0].action="importNewUser";
	document.forms[0].submit();
}

function goImportExistingUser()
{
    document.forms[0].action="importExistingUser";
	document.forms[0].submit();
	showWaitBusy();
}

function goChangeContact()
{
   selectAllAgents();
   document.forms[0].unsavedData.value = true;
   document.forms[0].action="setUserContact";
   document.forms[0].submit();
   showWaitBusy();
}

function goNewContact()
{
   selectAllAgents();
   document.forms[0].unsavedData.value = true;
   document.forms[0].action="createUserContact";
   document.forms[0].submit();
   showWaitBusy();
}

function goChangeBusiness()
{
   selectAllAgents();
   document.forms[0].unsavedData.value = true;
   document.forms[0].action="setUserBusiness";
   document.forms[0].submit();
   showWaitBusy();
}

function handleSelectionChange(button,yesCallback)
{
    if (fDataChanged)
    {
    	var noCallback = partial(handleSelectionChangeNoCallback,button);
       	showYesNoMessageBox('YES NO','WARN',
			   "<fmt:message key='warn.changes_not_saved_yes_no'/>",
			   "<fmt:message key='msg.warn'/>", yesCallback, noCallback);
    	return false;
    }
    return true;
}

function handleSelectionChangeNoCallback(button)
{
	if (button != null)
    {
       button.checked = false;
       var old = document.getElementById('${userAdminForm.selectedUserKey}');
       if (old != null)
       {
          old.checked = true;
       }
    }
}

function goSelectUser(button)
{
   if (handleSelectionChange(button,goSelectUserCallback))
   {
	   goSelectUserCallback();
   }
}

function goSelectUserCallback()
{
   document.forms[0].action="viewUserDetails";
   document.forms[0].submit();
   showWaitBusy();
}

function goSelectUserCallback(value)
{
   document.forms[0].selectedUserKey.value = value;
   document.forms[0].action="viewUserDetails";
   document.forms[0].submit();
   showWaitBusy();
}

function init ()
{
   	fDataChanged = ${userAdminForm.unsavedData};
}

function goBack(button){
	
	if (handleSelectionChange(button, goBackUserCallback)) {
		goBackUserCallback();
	}
}

function goBackUserCallback(){
	document.forms[0].preserveSearchValues.value = "true";
	document.forms[0].action = "submitUserSearch";
	document.forms[0].submit();
	showWaitBusy();
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
	<fmt:message var="searchTypeLabel" key="search.type.users" scope="page" />
	<fmt:message var="resultTitle" key="useradmin.resultsTitle"
		scope="page" />
	<fmt:message var="userTitle" key="useradmin.title" scope="page" />
	<fmt:message var="importTitle" key="useradmin.importUserTitle"
		scope="page" />
	<form name="userAdminForm" action="submitUserSearch" method="POST">
		<c:set var="viewOnly"
			value="${!e2ofn:hasAccess(appContext, 'ADMIN', 'SaveUser')}" />
		<c:set var="contactReadOnly"
			value="${!e2ofn:hasAccess(appContext, 'ADMIN', 'SaveUser')}" />
		<input type="hidden" name="unsavedData" />
		<input type="hidden" name="selectedTabId" />
		<input type="hidden" name="preserveSearchValues" value="false"/>
		<input type="hidden" name="selectedBusinessKey" class="selectedBusinessKey" />
		<input type="hidden" name="selectedContactKey" class="selectedContactKey"/>
		<input type="hidden" name="selectedUserKey" class="selectedUserKey"/>
		<input type="hidden" name="agentKeyString" class="agentKeyString" />

		<script>
			var jsonColumn =  '${userAdminForm.columns}';
			var selectionType = 'none';
			var gridColumns = [];
			gridColumns.push('<fmt:message key="useradmin.userid" />');
			gridColumns.push('<fmt:message key="useradmin.fullName" />');
			gridColumns.push('<fmt:message key="useradmin.businessName" />');
			gridColumns.push('<fmt:message key="useradmin.businessId" />');
			gridColumns.push('<fmt:message key="useradmin.roleName" />');
			gridColumns.push('<fmt:message key="useradmin.lastAccessDate" />');
			
			var gridRows = [];
			<c:forEach var="row" items="${userAdminForm.searchResult.values}" varStatus="rowCount">
			<c:set var="user" value="${row.values[0]}"/>
			<c:set var="role" value="${row.values[0].role}" />
			var row = {};
			row['checkboxValue'] = '${user.userKey}';
			var userLink = '<a href=\'javascript:goSelectUserCallback(${user.userKey})\'>${user.userId}</a>';
			row['<fmt:message key="useradmin.userid"/>'] = userLink;
			row['<fmt:message key="useradmin.fullName"/>'] = '<c:out value="${user.userName}"/>';
			row['<fmt:message key="useradmin.businessName"/>'] = '<c:out value="${user.businessEntity.businessEntityName}"/>';
			row['<fmt:message key="useradmin.businessId"/>'] = '<c:out value="${user.businessEntity.businessEntityIdentifier}"/>';
			row['<fmt:message key="useradmin.roleName"/>'] = '<c:out value="${role.roleName}"/>';
			row['<fmt:message key="useradmin.lastAccessDate"/>'] = '<fmt:formatDate pattern="${appContext.currentDateFormat} ${appContext.currentTimeFormat}" value="${user.lastAccessDate}" />';

			gridRows.push(row);
			</c:forEach>
		</script>

		<c:if
			test="${(empty userAdminForm.selectedUserKey) or (userAdminForm.selectedUserKey eq 0)}">
			<e2ot:searchContainerControl form="${userAdminForm}"
				searchFields="${userAdminForm.allParameters}"
				formName="userAdminForm"
				showFilterCollapsed="${userAdminForm.filterAreaCollapsed}"
				showFilter="${userAdminForm.showFilterArea}" numColumns="3" />

			<e2ot:searchResultsControl searchForm="${userAdminForm}"
				formName="userAdminForm" showOrderMenu="false" showHideMenu="false"
				resultTableId="userAdminFormResultTable" title="Users"
				showTitle="true" />
		</c:if>

		<c:if
			test="${(!empty userAdminForm.selectedUserKey) and (userAdminForm.selectedUserKey gt 0)}">
			<div id="userAllDetails" align="left" style="width: 100%;">
				<h1 style="margin: 15px">${userAdminForm.selectedUser.userId}</h1>

				<nav class="eto-tabs" id="tabs-example-1">
					<div class="eto-tabs__container">
						<div class="eto-tabs__scroll">
							<a
								class="${userAdminForm.selectedTabId ne 'contact' ? 'eto-tabs__tab eto-tabs__tab--active' : 'eto-tabs__tab'}"
								data-tab="#tab-1" tabindex="0"><span
								class="eto-tabs__tab-content">User Details</span><span
								class="eto-tabs__tab-close"></span></a> <a
								class="${userAdminForm.selectedTabId eq 'contact' ? 'eto-tabs__tab eto-tabs__tab--active' : 'eto-tabs__tab'}"
								data-tab="#tab-2" tabindex="0"><span
								class="eto-tabs__tab-content">Contact Details</span><span
								class="eto-tabs__tab-close"></span></a> <a class="eto-tabs__tab"
								data-tab="#tab-3" tabindex="0"><span
								class="eto-tabs__tab-content">Access Filters</span><span
								class="eto-tabs__tab-close"></span></a>
						</div>
					</div>
					<div class="eto-tabs__btns">
						<a class="eto-tabs__btn eto-tabs__btn--backward"></a> <a
							class="eto-tabs__btn eto-tabs__btn--forward"></a>
					</div>
				</nav>

				<div class="eto-tab-content" style="margin-bottom: 200px">
					<section class="eto-tab-content__item" id="tab-1">
						<div id="userDetails">
							<div id="userInfoBasic" style="background-color: #f1f6f7">
								<!-- <h2 style="padding: 15px">User Details</h2> -->
								<div style="display: flex; padding: 15px">
									<div class="eto-input"
										style="margin: auto; display: inline-block;">
										<label class="eto-input__label"><fmt:message
												key="useradmin.userid" /></label>
										<div class="eto-input__container">
											<b>${userAdminForm.selectedUser.userId}</b>
										</div>
									</div>

									<div class="eto-input"
										style="margin: auto; display: inline-block;">
										<label class="eto-input__label"><fmt:message
												key="useradmin.roleName" /></label>
										<div class="eto-input__container">
											<b>${userAdminForm.selectedUserRole.roleName}</b>
										</div>
									</div>

									<div class="eto-input"
										style="margin: auto; display: inline-block;">
										<label class="eto-input__label"><fmt:message
												key="useradmin.userEnabled" /></label>
										<div class="eto-input__container">
											<c:choose>
												<c:when test="${userAdminForm.selectedUser.isEnabled}">
													<b><fmt:message key="info.yes" /></b>
												</c:when>
												<c:otherwise>
													<b><fmt:message key="info.no" /></b>
												</c:otherwise>
											</c:choose>
										</div>
									</div>

									<div class="eto-input"
										style="margin: auto; display: inline-block;">
										<label class="eto-input__label"><fmt:message
												key="useradmin.lastAccessDate" /></label>
										<div class="eto-input__container">
											<b><fmt:formatDate
													pattern="${appContext.currentDateFormat} ${appContext.currentTimeFormat}"
													value="${userAdminForm.selectedUser.lastAccessDate}" /></b>
										</div>
									</div>
								</div>
							</div>

							<form class="eto-form eto-form--horizontal"
								id="horizontal-form-example">
								<div class="eto-input" style="margin: 15px">
									<label class="eto-input__label"><fmt:message
											key="useradmin.fullName" /></label>
									<div class="eto-input__container">
										<input class="eto-input__field" type="text" size="30"
											maxlength="50" onchange="javascript:dataChanged()"
											id="userFullName" name="selectedUser.userName"
											value="${userAdminForm.selectedUser.userName}"
											placeholder="Enter user fullname" style="width: 400px">
										<div class="eto-input__message"></div>
									</div>
								</div>

								<div class="eto-input" style="margin: 15px">
									<label class="eto-input__label"><fmt:message
											key="useradmin.eMail" /></label>
									<div class="eto-input__container">
										<input class="eto-input__field" type="text" size="30"
											maxlength="50" onchange="javascript:dataChanged()"
											id="userEmail" name="selectedUser.emailAddress"
											value="${userAdminForm.selectedUser.emailAddress}"
											placeholder="Enter email address" style="width: 400px">
										<div class="eto-input__message"></div>
									</div>
								</div>

								<div class="eto-combobox" id="userCompanyCombobox"
									style="margin: 15px; width: 400px">
									<label class="eto-combobox__label"><fmt:message
											key="useradmin.businessName" /></label>
									<div class="eto-combobox__field-container" role="presentation"
										aria-hidden="true">
										<input class="eto-combobox__field" type="text"
											name="combobox-example-name" autocomplete="off"> <span
											class="eto-combobox__btn">
											<button type="button" class="eto-btn eto-btn--icon-only">
												<span class="md-icon">expand_more</span>
											</button>
										</span>
									</div>
									<span class="eto-combobox__message"></span> <select
										name="selectedUser.businessEntity.businessEntityKey">
										<c:forEach var="businessEntity"
											items="${userAdminForm.businessEntityList}" varStatus="be">
											<option value="${businessEntity.businessEntityKey}"
												<c:out value="${(businessEntity.businessEntityKey eq userAdminForm.selectedUser.businessEntity.businessEntityKey) ? 'selected' : ''}" />>${businessEntity.businessEntityName}</option>
										</c:forEach>
									</select>
									<div class="eto-results" role="listbox" aria-live="polite"
										aria-relevant="all"></div>
								</div>

								<div class="eto-input" style="margin: 15px; width: 400px">
									<label class="eto-input__label"><fmt:message
											key="useradmin.agentBusinessAccess" /></label>
									<div class="eto-autocomplete__tags-container"
										style="background-color: #f1f6f7; vertical-align: middle;">
										<div class="eto-autocomplete__tags" id="populateTags"
											style="display: flex; flex-wrap: wrap;">
											<c:forEach var="altBusiness"
												items="${userAdminForm.agentOfBusinesses}" varStatus="abIdx">
												<span class='eto-tag eto-tag--sm'><span
													class=eto-tag__label><c:out
															value="${altBusiness.value}" /></span> <span
													class=eto-tag__remove tabindex=0
													onclick="closeThisTag(this)" id="${altBusiness.key}">
														<i class='md-icon md-icon--sm'>close</i>
												</span></span>
											</c:forEach>
										</div>
									</div>
									<div id="addBtnBusinessAgent">
										<button type="button" id="addBtnBusinessAgent"
											class="eto-btn eto-btn--primary"
											onclick="javascript:addAgent()">
											<fmt:message key="button.add" />
										</button>
									</div>
								</div>
							</form>
						</div>
					</section>

					<section class="eto-tab-content__item" id="tab-2">
						<div id="contact">
							<div id="userInfoContact"
								style="background-color: #f1f6f7; line-height: 70px;">
								<div id="alignmentDiv"
									style="vertical-align: middle; display: inline-block; width: 100%; padding: 15px;">
									<div
										style="float: left; display: inline-block; vertical-align: middle;">
										<h2>Contact Details</h2>
									</div>
									<div
										style="float: right; display: inline-block; vertical-align: middle;">
										<c:if
											test="${e2ofn:hasAccess(appContext, 'ADMIN', 'SaveUser')}">
											<div class="eto-btn-group">
												<c:if
													test="${e2ofn:hasAccess(appContext, 'ADMIN', 'SaveContact')}">
													<button type="button" id="changeContactButton"
														class="eto-btn"
														onclick="javascript:findContact('${fn:escapeXml(userAdminForm.selectedUser.contact.contactName)}', document.forms[0].selectedContactKey)">
														<fmt:message key="button.changeContact" />
													</button>
												</c:if>
												<c:if test="${empty userAdminForm.selectedUser.contact}">
													<button type="button" id="newContactButton" class="eto-btn"
														onclick="javascript:goNewContact();">
														<fmt:message key="button.newContact" />
													</button>
												</c:if>
											</div>
										</c:if>
									</div>
								</div>
							</div>

							<div id="contactDetailsUser" style="margin: 15px">
								<form class="eto-form eto-form--horizontal"
									id="horizontal-form-example">
									<c:if test="${empty userAdminForm.selectedUser.contact}">
										<span><i>No contact information</i></span>
									</c:if>
									<c:if test="${!empty userAdminForm.selectedUser.contact}">
										<div class="eto-input">
											<label class="eto-input__label" style="margin-right: 25px"><fmt:message
													key="contact.name" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactName" name="selectedUser.contact.contactName"
													value="${userAdminForm.selectedUser.contact.contactName}"
													placeholder="Enter contact name" style="width: 400px"
													disabled="disabled">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.email" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactEmail" name="selectedUser.contact.email"
													value="${userAdminForm.selectedUser.contact.email}"
													placeholder="Enter email address" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>


										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.business" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactBusiness"
													name="selectedUser.contact.businessName"
													value="${userAdminForm.selectedUser.contact.businessName}"
													placeholder="Enter business name" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.id" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactId" name="selectedUser.contact.contactId"
													value="${userAdminForm.selectedUser.contact.contactId}"
													placeholder="Enter contact identifier" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.department" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactDepartment"
													name="selectedUser.contact.department"
													value="${userAdminForm.selectedUser.contact.department}"
													placeholder="Enter department name" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.address1" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactAddress1" name="selectedUser.contact.addressL1"
													value="${userAdminForm.selectedUser.contact.addressL1}"
													placeholder="Enter street address" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.address2" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactAddress2" name="selectedUser.contact.addressL2"
													value="${userAdminForm.selectedUser.contact.addressL2}"
													placeholder="Enter street address" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.address3" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactAddress3" name="selectedUser.contact.addressL3"
													value="${userAdminForm.selectedUser.contact.addressL3}"
													placeholder="Enter street address" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.city" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactCity" name="selectedUser.contact.city"
													value="${userAdminForm.selectedUser.contact.city}"
													placeholder="Enter city name" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.region" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactRegion" name="selectedUser.contact.region"
													value="${userAdminForm.selectedUser.contact.region}"
													placeholder="Enter region" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>

										<div class="eto-input" style="margin-top: 15px">
											<label class="eto-input__label"><bean:message
													key="contact.postalCode" /></label>
											<div class="eto-input__container">
												<input class="eto-input__field" type="text" size="30"
													maxlength="50" onchange="javascript:dataChanged()"
													id="contactPostalCode"
													name="selectedUser.contact.postalCode"
													value="${userAdminForm.selectedUser.contact.postalCode}"
													placeholder="Enter postal code" style="width: 400px">
												<div class="eto-input__message"></div>
											</div>
										</div>
									</c:if>
								</form>
							</div>
						</div>
					</section>

					<section class="eto-tab-content__item" id="tab-3">
						<div id="access"
							style="width: 100%; padding: 15px; display: flex;">
							<fmt:message var="roleOverrideTitle"
								key="warning.userFilter.overrideNotAllowed" />
							<c:if
								test="${e2ofn:getConfigValue('pcm.common.enableDataFilter.CATEGORY')}">
								<div id="categoryFiltersParentDiv"
									style="display: inline-block; width: 30%; margin-right: auto;">
									<div id="categoryTableDiv"
										style="display: inline-block; width: 100%">
										<table class="eto-table" id="categoryTable"
											style="width: 100%; border-style: solid; border-width: 2px; border-color: #cddfe4;">
											<thead>
												<tr>
													<th><label class="eto-checkbox"> <input
															id="categoryTable_globalrowselector"
															class="eto-checkbox__field eto-all-rows-indicator"
															type="checkbox"
															onclick="i2uiToggleAllRowsSelectionState(this,'categoryTable');dataChanged()">
															<span class="eto-checkbox__box"></span>
													</label></th>
													<th><fmt:message key="useradmin.categories" /></th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="category"
													items="${userAdminForm.availableCategories}"
													varStatus="rowCount">

													<c:set var="strkey" value="${''}${category.categoryKey}" />
													<tr>
														<td><label class="eto-checkbox"> <input
																id="categoryTable_rowselector"
																class="eto-checkbox__field eto-row-indicator"
																type="checkbox" name="categoryKeys"
																value="${category.categoryKey}" onchange="dataChanged()"
																<c:out value="${e2ofn:arrayContains(userAdminForm.categoryKeys,strkey) ? 'checked' : ''}" />>
																<span class="eto-checkbox__box"></span>
														</label></td>

														<c:choose>
															<c:when
																test="${e2ofn:contains(userAdminForm.roleCategoryKeys,strkey)}">
																<td><e2i2:img src="/role.gif"
																		alt="${roleOverrideTitle}" /> <c:out
																		value="${category.categoryName}" /></td>
															</c:when>
															<c:when test="${viewOnly}">
																<c:if
																	test="${e2ofn:arrayContains(userAdminForm.categoryKeys,strkey)}">
																	<td><input type="hidden"
																		value="<c:out value='${category.categoryKey}'/>"
																		name="categoryKeys"> <c:out
																			value="${category.categoryName}" /></td>
																</c:if>
															</c:when>
															<c:otherwise>
																<td><c:out value="${category.categoryName}" /></td>
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</c:if>

							<c:if
								test="${e2ofn:getConfigValue('pcm.common.enableDataFilter.PLATFORM')}">
								<div id="platformFiltersParentDiv"
									style="display: inline-block; width: 30%; margin-right: auto;">
									<div id="platformFiltersDiv"
										style="display: inline-block; width: 100%">
										<table class="eto-table" id="platformTable"
											style="width: 100%; border-style: solid; border-width: 2px; border-color: #cddfe4;">
											<thead>
												<tr>
													<th><c:if test="${!viewOnly}">
															<label class="eto-checkbox"> <input
																id="platformTable_globalrowselector"
																class="eto-checkbox__field eto-all-rows-indicator"
																type="checkbox"
																onclick="i2uiToggleAllRowsSelectionState(this,'platformTable');dataChanged()">
																<span class="eto-checkbox__box"></span>
															</label>
														</c:if></th>
													<th><fmt:message key="useradmin.platforms" /></th>
													<th>Platform Type</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="platform"
													items="${userAdminForm.availablePlatforms}"
													varStatus="rowCount">

													<c:set var="strkey" value="${''}${platform.platformKey}" />
													<tr>
														<td><label class="eto-checkbox"> <input
																id="platformTable_rowselector"
																class="eto-checkbox__field eto-row-indicator"
																type="checkbox" name="platformKeys"
																value="${platform.platformKey}" onchange="dataChanged()"
																<c:out value="${(e2ofn:arrayContains(userAdminForm.platformKeys,strkey)) ? 'checked' : ''}" />>
																<span class="eto-checkbox__box"></span>
														</label></td>
														<c:choose>
															<c:when
																test="${e2ofn:contains(userAdminForm.rolePlatformKeys,strkey)}">
																<td><e2i2:img src="/role.gif"
																		alt="${roleOverrideTitle}" /></td>
																<td><c:out value="${platform.platformName}" /></td>
																<td><c:out value="${platform.platformType}" /></td>
															</c:when>
															<c:when test="${viewOnly}">
																<c:if
																	test="${e2ofn:arrayContains(userAdminForm.platformKeys,strkey)}">
																	<td><input type="hidden"
																		value="${platform.platformKey}" name="platformKeys"></td>
																	<td><c:out value="${platform.platformName}" /></td>
																	<td><c:out value="${platform.platformType}" /></td>
																</c:if>
															</c:when>
															<c:otherwise>
																<td><c:out value="${platform.platformName}" /></td>
																<td><c:out value="${platform.platformType}" /></td>
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</c:if>

							<c:if
								test="${e2ofn:getConfigValue('pcm.common.enableDataFilter.SITE')}">
								<div id="sitesFilterParentDiv" style="display: inline-block;">
									<div id="siteFiltersDiv"
										style="display: inline-block; width: 100%">
										<table class="eto-table" id="siteTable"
											style="width: 100%; border-style: solid; border-width: 2px; border-color: #cddfe4;">
											<thead>
												<tr>
													<th><c:if test="${!viewOnly}">
															<label class="eto-checkbox"> <input
																id="siteTable_globalrowselector"
																class="eto-checkbox__field eto-all-rows-indicator"
																type="checkbox"
																onclick="i2uiToggleAllRowsSelectionState(this,'siteTable');dataChanged()">
																<span class="eto-checkbox__box"></span>
															</label>
														</c:if></th>
													<th><fmt:message key="useradmin.sites" /></th>
													<th>Business entity name</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="site"
													items="${userAdminForm.availableSites}"
													varStatus="rowCount">

													<c:set var="strkey" value="${''}${site.siteKey}" />
													<tr>
														<td><label class="eto-checkbox"> <input
																id="siteTable_rowselector"
																class="eto-checkbox__field eto-row-indicator"
																type="checkbox" name="siteKeys" value="${site.siteKey}"
																onchange="dataChanged()"
																<c:out value="${(e2ofn:arrayContains(userAdminForm.siteKeys,strkey)) ? 'checked' : ''}" />>
																<span class="eto-checkbox__box"></span>
														</label></td>
														<c:choose>
															<c:when
																test="${e2ofn:contains(userAdminForm.roleSiteKeys,strkey)}">
																<td><e2i2:img src="/role.gif"
																		alt="${roleOverrideTitle}" /></td>
																<td><c:out value="${site.siteDescription}" /></td>
																<td><c:out
																		value="${site.businessEntity.businessEntityName}" /></td>
															</c:when>
															<c:when test="${viewOnly}">
																<c:if
																	test="${e2ofn:arrayContains(userAdminForm.siteKeys,strkey)}">
																	<td><input type="hidden" value="${site.siteKey}"
																		name="siteKeys"></td>
																	<td><c:out value="${site.siteDescription}" /></td>
																	<td><c:out
																			value="${site.businessEntity.businessEntityName}" /></td>
																</c:if>
															</c:when>
															<c:otherwise>
																<td><c:out value="${site.siteDescription}" /></td>
																<td><c:out
																		value="${site.businessEntity.businessEntityName}" /></td>
															</c:otherwise>
														</c:choose>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</c:if>
						</div>
					</section>
				</div>

				<div class="footer">
					<nav class="eto-form__btns" style="margin-left: 15px">
						<div class="eto-btn-group" style="margin-top: 15px">
							<c:if
								test="${e2ofn:hasAccess(appContext, 'ADMIN', 'SaveContact')}">
								<button type="button" id="saveExitButton" class="eto-btn"
									onclick="javascript:goSave();" disabled>
									<bean:message key="button.save_exit" />
								</button>
								<button type="button" id="saveButton" class="eto-btn"
										onclick="javascript:goSaveAndContinue();">
									<bean:message key="button.save" />
								</button>
							</c:if>
							<button type="button" id="backButton" class="eto-btn"
								onclick="javascript:goBack(this);">
								<bean:message key="button.back" />
							</button>
						</div>
					</nav>
				</div>
			</div>
		</c:if>
	</form>
</body>
<script type="text/javascript">

<c:forEach var="altBusiness" items="${userAdminForm.agentOfBusinesses}" varStatus="abIdx">
					agentKeys.push("${altBusiness.key}");
</c:forEach>

var flag = ${viewOnly};

if(!flag){
	$("#saveExitButton").removeClass().addClass("eto-btn eto-btn--primary");
	$("#saveExitButton").removeAttr("disabled");
	$("#saveButton").removeClass().addClass("eto-btn eto-btn--primary");
	$("#saveButton").removeAttr("disabled");
}

var tabs = new eto.Tabs({ el: document.querySelector('#tabs-example-1') });

if($("#userCompanyCombobox").length){
var userCompanySelect = new eto.Combobox(
		{
			el : document.querySelector('#userCompanyCombobox')
		});
}

function closeThisTag(element){
    var id = element.id;
    $("#"+id).parent().css('display','none');
    
    for( var i = 0; i < agentKeys.length; i++){ 
    	   if ( agentKeys[i] == id) {
    		   agentKeys.splice(i, 1); 
    	   }
    }
}

</script>
</html>
