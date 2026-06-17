<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" />
<e2ot:help contextName="Admin-Contact" />
<title>Manage Contact</title>
</head>
<script>
	var fDataChanged = false;
	var setWidth = true;
	var selectedPageKey = "";

	function init() {
	}

	function canChangePage(op, callback) {
		var fr = partial(canChangePageCallback, callback);
		var rc = handleSelectionChange(null, fr);
		if (rc == true) {
			canChangePageCallback(callback);
		}
		return rc;
	}

	function canChangePageCallback(callback) {
		var button = document
				.getElementById('${contactAdminForm.selectedContactKey}');
		if (button != null) {
			button.checked = false;
		}
		callback.apply();
	}

	function dataChanged() {
		fDataChanged = true;
	}

	function checkRequiredFields() {
		if (isFieldEmpty('contactName')) {
			/* showOkMessageBox('OK', 'ERROR',
					"<fmt:message key='errors.field_required'/>",
					"<fmt:message key='msg.error'/>", function() {
						document.forms[0].contactName.focus();
					}); */
			$('#message-block-error-name').css('display', 'flex');
			return;
		}
		return true;
	}

	function goAdd() {
		if (handleSelectionChange(null, goAddCallback)) {
			goAddCallback();
		}
	}

	function goAddCallback() {
		document.forms[0].action = "createNewContact";
		document.forms[0].preserveSearchValues.value = "true";
		<c:if test="${empty contactAdminForm.searchResult.values}">
		document.forms[0].goInit.value = "true";
		</c:if>
		document.forms[0].submit();

	}

	function goSave() {
		if (checkRequiredFields()) {
			var emailId = $("#contactEmail").val();
			 var postalCode = $("#contactPostalCode").val();
			 if(emailId.trim()!=''){
			  if(validateEmailAddress(emailId)==false){
				  showOkMessageBox('OK', 'ERROR',
							"<fmt:message key='errors.email.ui.validate'/>",
							"<fmt:message key='msg.error'/>", function() {
							});
				 return;
			  }
			 }
			 if(postalCode.trim() !=''){
				  if(validateZipAddress(postalCode)==false){
					  showOkMessageBox('OK', 'ERROR',
								"<fmt:message key='errors.zipcode.ui.validate'/>",
								"<fmt:message key='msg.error'/>", function() {
								});
					 return;
				  }
				 }
			document.forms[0].action = "saveContactDetails";
			document.forms[0].submit();
		}
	}

	function handleSelectionChange(button, yesCallback) {
		if (fDataChanged) {
			var noCallback = partial(handleSelectionChangeNoCallback, button);
			showYesNoMessageBox('YES NO', 'WARN',
					"<fmt:message key='warn.changes_not_saved_yes_no'/>",
					"<fmt:message key='msg.warn'/>", yesCallback, noCallback);
			return false;
		}
		return true;
	}

	function handleSelectionChangeNoCallback(button) {
		if (button != null) {
			button.checked = false;
			var old = document
					.getElementById('${contactAdminForm.selectedContactKey}');
			if (old != null) {
				old.checked = true;
			}
		}
	}

	function goSelectContact(button) {
		if (handleSelectionChange(button, goSelectContactCallback)) {
			goSelectContactCallback();
		}
	}

	function goSelectContactCallback() {
		document.forms[0].action = "viewContactDetails";
		document.forms[0].submit();
	}

	function goSelectContactCallback(value) {
		document.forms[0].selectedContactKey.value = value;
		document.forms[0].action = "viewContactDetails";
		document.forms[0].submit();
	}
	
	function goBack(button){
		
		if (handleSelectionChange(button, goBackContactCallback)) {
			goBackContactCallback();
		}
	}
	
	function goBackContactCallback(){
		<c:if test="${contactAdminForm.goInit}">
		document.forms[0].action = "searchContact";
		document.forms[0].selectedContactKey.value = "";
		</c:if>
		
		<c:if test="${!contactAdminForm.goInit}">
		document.forms[0].action = "submitContactSearch";
		document.forms[0].preserveSearchValues.value = "true";
		</c:if>
		document.forms[0].submit();
	}
	
	function goDeleteContact(){
		document.forms[0].action = "deleteContact";
		document.forms[0].submit();
	}
</script>

<body onload="init()">
	<div class="margin-top-xs-1 margin-bottom-xs-1">
		<div style="display: flex;">
			<div class="eto-messageblock" data-message-type="error"
				id="message-block-error-name" style="display: none; margin: auto;">
				<div class="eto-messageblock__body">Contact name cannot be
					left blank</div>
			</div>
		</div>
		<script type="text/javascript">
			var message = new eto.MessageBlock({ el: document.querySelector('#message-block-error-name') });
		</script>

		<div style="display: flex;">
			<div style="margin: auto;">
				<div style="font-weight: bold;">
					<logic:messagesPresent message="true">
						<html:messages id="message" message="true">
							<li>${message}</li>
						</html:messages>
					</logic:messagesPresent>
				</div>
			</div>
		</div>
	</div>

	<fmt:message var="searchTypeLabel" key="search.type.contacts"
		scope="page" />
	<fmt:message var="resultTitle" key="contact.resultsTitle" scope="page" />
	<fmt:message var="detailTitle" key="contact.detailTitle" scope="page" />
	<form name="contactAdminForm" action="submitContactSearch" method="POST">
		<input type="hidden" name="selectedContactKey"value="${contactAdminForm.selectedContactKey}" />
		<input type="hidden" name="preserveSearchValues" value="${contactAdminForm.preserveSearchValues}" />
		<input type="hidden" name="goInit" value="${contactAdminForm.goInit}" />
		<c:set var="viewOnly" value="${!e2ofn:hasAccess(appContext, 'ADMIN', 'SaveContact')}" />

		<script>
			var jsonColumn =  '${contactAdminForm.columns}';
			var gridColumns = [];
			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.contactSearch.grid.thead')}"/>
			<c:forEach var="col" items="${columnHeader}" varStatus="count">
				gridColumns.push('<fmt:message key="${col}"/>');
			</c:forEach>
			
			var gridRows = [];
			<c:forEach var="row" items="${contactAdminForm.searchResult.values}" varStatus="rowCount">
			<c:set var="contact" value="${row.values[0]}"/>
			var row = {};
			row['checkboxValue'] = "<c:out value='${contact.contactKey}'/>";
			var contactLink = '<a href=\'javascript:goSelectContactCallback(${contact.contactKey})\'>${contact.contactName}</a>';
			row['<bean:message key="contact.name"/>'] = contactLink;
			row['<bean:message key="contact.business"/>'] = "<c:out value='${contact.businessName}'/>";
			row['<bean:message key="contact.department"/>'] = "<c:out value='${contact.department}'/>";
			row['<bean:message  key="contact.email"/>'] = "<c:out value='${contact.email}'/>";
			row['<bean:message  key="contact.businessIdentifier"/>'] = "<c:out value='${contact.duns}'/>";
			row['<bean:message  key="contact.id"/>'] = "<c:out value='${contact.contactId}'/>";
			row['<bean:message  key="contact.be.businessName"/>'] = "<c:out value='${contact.businessEntity.businessEntityName}'/>";
			row['<bean:message  key="contact.be.businessIdentifier"/>'] = "<c:out value='${contact.businessEntity.businessEntityIdentifier}'/>";
			row['<bean:message  key="contact.city"/>'] = "<c:out value='${contact.city}'/>";
			row['<bean:message  key="contact.region"/>'] = "<c:out value='${contact.region}'/>";
			row['<bean:message  key="contact.postalCode"/>'] = "<c:out value='${contact.postalCode}'/>";
			row['<bean:message  key="contact.telephoneNumber"/>'] = "<c:out value='${contact.telephoneNumber}'/>";
			row['<bean:message  key="contact.countryCode"/>'] = "<c:out value='${contact.countryCode}'/>";
			row['<bean:message  key="contact.dataSource"/>'] = "<c:out value='${contact.dataSource}'/>";
			row['<bean:message  key="contact.effectiveFrom"/>'] = "<c:out value='${contact.effectiveFrom}'/>";
			row['<bean:message  key="contact.effectiveTo"/>'] = "<c:out value='${contact.effectiveTo}'/>";
			row['<bean:message  key="contact.insertDate"/>'] = "<c:out value='${contact.insertDate}'/>";
			row['<bean:message  key="contact.updateDate"/>'] = "<c:out value='${contact.updateDate}'/>";

			gridRows.push(row);
			</c:forEach>
		</script>


		<c:if test="${empty contactAdminForm.selectedContactKey}">
			<e2ot:searchContainerControl form="${contactAdminForm}"
				searchFields="${contactAdminForm.allParameters}"
				formName="contactAdminForm"
				showFilterCollapsed="${contactAdminForm.filterAreaCollapsed}"
				showFilter="${contactAdminForm.showFilterArea}" numColumns="3" />
			<e2ot:searchResultsControl searchForm="${contactAdminForm}"
				formName="contactAdminForm"
				resultTableId="contactAdminFormResultTable" showOrderMenu="false"
				showHideMenu="false" title="Contacts" showTitle="true" />
		</c:if>
		<c:if test="${!empty contactAdminForm.selectedContactKey}">
			<div id="contactDeatils"
				style="width: 100%; padding: 15px; margin-bottom: 250px">
				<h1>${detailTitle}</h1>
				<form class="eto-form eto-form--horizontal"
					id="horizontal-form-example">

					<div class="eto-input" style="margin-top: 15px">
						<label class="eto-input__label"><bean:message
								key="contact.name" /></label>
						<div class="eto-input__container">
							<input class="eto-input__field" type="text" size="30"
								maxlength="50" onchange="javascript:dataChanged()"
								id="contactName" name="selectedContact.contactName"
								value="${contactAdminForm.selectedContact.contactName}"
								placeholder="Enter contact name" style="width: 400px">
							<div class="eto-input__message"></div>
						</div>
					</div>

					<div class="eto-input" style="margin-top: 15px">
						<label class="eto-input__label"><bean:message
								key="contact.email" /></label>
						<div class="eto-input__container">
							<input class="eto-input__field" type="text" size="30"
								maxlength="50" onchange="javascript:dataChanged()"
								id="contactEmail" name="selectedContact.email"
								value="${contactAdminForm.selectedContact.email}"
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
								id="contactBusiness" name="selectedContact.businessName"
								value="${contactAdminForm.selectedContact.businessName}"
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
								id="contactId" name="selectedContact.contactId"
								value="${contactAdminForm.selectedContact.contactId}"
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
								id="contactDepartment" name="selectedContact.department"
								value="${contactAdminForm.selectedContact.department}"
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
								id="contactAddress1" name="selectedContact.addressL1"
								value="${contactAdminForm.selectedContact.addressL1}"
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
								id="contactAddress2" name="selectedContact.addressL2"
								value="${contactAdminForm.selectedContact.addressL2}"
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
								id="contactAddress3" name="selectedContact.addressL3"
								value="${contactAdminForm.selectedContact.addressL3}"
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
								id="contactCity" name="selectedContact.city"
								value="${contactAdminForm.selectedContact.city}"
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
								id="contactRegion" name="selectedContact.region"
								value="${contactAdminForm.selectedContact.region}"
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
								id="contactPostalCode" name="selectedContact.postalCode"
								value="${contactAdminForm.selectedContact.postalCode}"
								placeholder="Enter postal code" style="width: 400px">
							<div class="eto-input__message"></div>
						</div>
					</div>

					<c:if test="${contactAdminForm.businessContacts.size() > 0}">
						<div class="eto-input" style="margin-top: 15px">
							<label class="eto-input__label"><bean:message
									key="contact.businessContact" /></label>
							<div class="eto-input__container">
								<div style="width: 400px; margin-left: 15px">
									<ul>
										<c:forEach var="bec"
											items="${contactAdminForm.businessContacts}">
											<li><b><c:out value="${bec.value}" /></b></li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</div>
					</c:if>

					<div class="footer" style="width: 100%">
						<nav class="eto-form__btns" style="margin-left: 30px">
							<div class="eto-btn-group" style="margin-top: 15px">
								<c:if
									test="${e2ofn:hasAccess(appContext, 'ADMIN', 'SaveContact')}">
									<button type="button" id="saveExitButton" class="eto-btn"
										onclick="javascript:goSave();" disabled>
										<bean:message key="button.save_exit" />
									</button>

								</c:if>
								<button type="button" id="backButton" class="eto-btn"
									onclick="javascript:goBack(this);">
									<bean:message key="button.back" />
								</button>
							</div>
						</nav>
					</div>
				</form>
			</div>
		</c:if>
	</form>

	<c:if
		test="${empty contactAdminForm.selectedContactKey}">
		<div class="footer">
			<div class="eto-btn-group"
				style="margin-left: 15px; margin-top: 15px;">
				<c:if
					test="${e2ofn:hasAccess(appContext, 'ADMIN', 'CreateContact')}">
					<button type="button" id="addButton" class="eto-btn"
						onclick="javascript:goAdd();" disabled>
						<bean:message key="button.add_contact" />
					</button>
					<c:if test="${not empty contactAdminForm.searchResult.values}">
					<button type="button" id="deleteButton" class="eto-btn"
						data-modal="#delete-contact-modal" disabled>
						<bean:message key="button.delete_contact" />
					</button>
					</c:if>
				</c:if>
			</div>
		</div>
	</c:if>

	<div class="eto-modal" id="delete-contact-modal" style="width: 100%;">
		<div class="eto-modal__content" style="height: 275px; width: auto;">
			<header class="eto-modal__header">
				<span>Confirmation</span>
				<button class="eto-modal__close" data-modal-close></button>
			</header>
			<section class="eto-modal__body">
				<div class="eto-messageblock" data-message-type="warn"
					id="message-block-example-3">
					<div class="eto-messageblock__body">
						Are you sure you want to delete contact ?<br> There might be
						active primary contact for this address
					</div>
				</div>
			</section>
			<footer class="eto-modal__footer"
				style="height: 60px; border-top-style: solid; border-top-width: 2px; border-top-color: #cddfe4">
				<div class="eto-btn-group"
					style="margin-top: 10px; margin-bottom: -3px">
					<button class="eto-btn" data-modal-close>No, Keep Contact</button>
					<button class="eto-btn eto-btn--primary" data-modal-close
						onclick="javascript:goDeleteContact()">Yes, Delete
						Contact</button>
				</div>
			</footer>
		</div>
	</div>
</body>


<script type="text/javascript">
	
	var flag = ${viewOnly};
	
	if(!flag){
		$("#addButton").removeClass().addClass("eto-btn eto-btn--primary");
		$("#saveExitButton").removeClass().addClass("eto-btn");
		$("#addButton").removeAttr("disabled");
		$("#saveExitButton").removeAttr("disabled");
		$("#saveButton").removeAttr("disabled");
	}
			
			
	function setGridEvents(grid){
		if (!grid) return;
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
					$("#deleteButton").removeClass().addClass("eto-btn eto-btn--primary");
					$("#deleteButton").removeAttr("disabled");
				}
			else{
				    $("#deleteButton").removeClass().addClass("eto-btn");
					$("#deleteButton").attr("disabled", "disabled");
			}
		});
	}
	
	if (typeof grid !== 'undefined' && grid !== null) {
		setGridEvents(grid);
	}

		var modal = new eto.Modal({ el: document.querySelector('#delete-contact-modal') });
</script>

</html>