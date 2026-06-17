<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="com.scplatform.pcm.common.entity.AdditionalAttributeManager"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.scplatform.pcm.common.entity.AttributeDefn"%>
<%@page import="java.util.List"%>
<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" />
<e2ot:help contextName="Admin-BusinessEntity" />
</head>

<script>
var fDataChanged = ${!empty param.contactChanged ? param.contactChanged : 'false'}; 
var alternateNames = {};

function init ()
{
}


function findContact(selectedContactKey)
{

    var url = "./viewSearchFinder?finderName=ContactFinder"
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
              
    callFinderAjax(url,"ContactFinder"); 
    
   /*  finderPopupWin = window.open(url,'finderPopup','height=400,width=450,top='
                            +window.screenTop+',left='
                            +window.screenLeft
                            +',resizable=yes,status=yes,toolbar=no,scrollbars=yes,menubar=no,location=no');
    finderPopupWin.focus();
    window.onunload = closeFinderIfOpen; */
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
	var button = document.getElementById('${businessAdminForm.selectedBusinessKey}');   
    if (button != null)
    {
       button.checked = false;
    }
    callback.apply();
}


function dataChanged()
{
   fDataChanged = true;
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
       var old = document.getElementById('${businessAdminForm.selectedBusinessKey}');
       if (old != null)
       {
          old.checked = true;
       }
    }
}

function goChangeContact()
{
   selectAllAlternates();	
   document.forms[0].contactChanged.value = true;
   document.forms[0].action="setBusinessContact";
   document.forms[0].submit();
}

function goNewContact()
{
   selectAllAlternates();	
   document.forms[0].contactChanged.value = true;
   document.forms[0].action="createBusinessContact";
   document.forms[0].submit();
}

function goAdd()
{
   document.forms[0].action="createNewBusiness";
   document.forms[0].submit();
}

function goSave()
{
	selectAllAlternates();	
    var emailId = $("#contactEmail").val();
    var postalCode = $("#contactPostalCode").val();
      if(typeof emailId != "undefined" && emailId.trim()!=''){
        if(validateEmailAddress(emailId)==false){
             showOkMessageBox('OK', 'ERROR',
              "<fmt:message key='errors.email.ui.validate'/>",
              "<fmt:message key='msg.error'/>", function() {
             });
           return;
    	}
    }
    if(typeof postalCode != "undefined" && postalCode.trim() !=''){
    	if(validateZipAddress(postalCode)==false){
          showOkMessageBox('OK', 'ERROR',
           "<fmt:message key='errors.zipcode.ui.validate'/>",
           "<fmt:message key='msg.error'/>", function() {
          });
       	  return;
    	}
    }
	document.forms[0].action="saveBusinessDetails";
   	document.forms[0].submit();
}


function goSaveAndStay(){
	if($("#alternateInput").val()!='') {
		showOkMessageBox('OK', 'WARN', "<fmt:message key='info.business.alternate.names.validation.warning'/>",
         "<fmt:message key='msg.warn'/>", function() {
         });
       return;
	}
	selectAllAlternates();	
    var emailId = $("#contactEmail").val();
    var postalCode = $("#contactPostalCode").val();
      if(typeof emailId != "undefined" && emailId.trim()!=''){
        if(validateEmailAddress(emailId)==false){
             showOkMessageBox('OK', 'ERROR',
              "<fmt:message key='errors.email.ui.validate'/>",
              "<fmt:message key='msg.error'/>", function() {
             });
           return;
    	}
    }
    if(typeof postalCode != "undefined" && postalCode.trim() !=''){
    	if(validateZipAddress(postalCode)==false){
          showOkMessageBox('OK', 'ERROR',
           "<fmt:message key='errors.zipcode.ui.validate'/>",
           "<fmt:message key='msg.error'/>", function() {
          });
       	  return;
    	}
    }
	document.forms[0].action="saveOnlyBusinessDetails";
   	document.forms[0].submit();
}

function goSelectBusiness(button)
{
   if (handleSelectionChange(button,goSelectBusinessCallback))
   {
	   goSelectBusinessCallback();
   }
}

function goSelectBusinessCallback()
{
	document.forms[0].action="viewBusinessDetails";
	document.forms[0].submit();
}

function goSelectBusinessCallback(value)
{
	document.forms[0].selectedBusinessKey.value = value;
	document.forms[0].action="viewBusinessDetails";
	document.forms[0].submit();
}

function selectAllAlternates()
{
   document.forms[0].alternateNamesString.value = JSON.stringify(alternateNames);
}

function goBack(button){
	if (handleSelectionChange(button, goBackBusinessCallback)) {
		goBackBusinessCallback();
	}
}

function goBackBusinessCallback(){
	document.forms[0].preserveSearchValues.value = "true";
	document.forms[0].selectedBusinessKey.value=null;
	document.forms[0].action = "submitBusinessSearch";
	document.forms[0].submit();
}

function goSiteDownload()
{
    document.forms[0].action='downloadBusinessEntityWithSite';
    document.forms[0].submit();
}

function goCurrencyDownload()
{
    document.forms[0].action='downloadBusinessEntityWithCurrency';
    document.forms[0].submit();
}
</script>

<body onload="init()">
		<%
		List<AttributeDefn> attributeDefns = new ArrayList<AttributeDefn>(
				AdditionalAttributeManager.BUSINESS_ENTITY.getAdditionalAttributeDefinitionList());
		Collections.sort(attributeDefns, new Comparator<AttributeDefn>() {
			public int compare(AttributeDefn a1, AttributeDefn a2) {
				return a1.getName().compareToIgnoreCase(a2.getName());
			}
		});
		pageContext.setAttribute("attributeDefns", attributeDefns);
		%>
	<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
		<div style="font-weight: bold;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					<li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
	</div>

	<fmt:message var="searchTypeLabel" key="search.type.businesses"
		scope="page" />
	<fmt:message var="resultTitle" key="business.resultsTitle" scope="page" />
	<fmt:message var="detailTitle" key="business.detailTitle" scope="page" />
	<form name="businessAdminForm" action="submitBusinessSearch" method="POST">
		<input type="hidden" name="selectedBusinessKey" />
		<input type="hidden" name="alternateNamesString" />
		<input type="hidden" name="preserveSearchValues" value="false"/>
		<input type="hidden" name="contactChanged" value="false"/>
		<input type="hidden" name="extractFileName" />
		<input type="hidden" name="filterType" id="filterType"/>
		<input type="hidden" class="selectedContactKey" name="selectedContactKey" />
		<script>
			var jsonColumn =  '${businessAdminForm.columns}';
			var selectionType = 'none';
			var gridColumns = [];
			gridColumns.push('<fmt:message key="business.name" />');
			gridColumns.push('<fmt:message key="business.id" />');
			gridColumns.push('<fmt:message key="business.businessType" />');
			gridColumns.push('<fmt:message key="business.description" />'+'_EXPANDCELL');
			gridColumns.push('<fmt:message key="business.primaryContact" />');
			gridColumns.push('<fmt:message key="business.primaryContactEMail" />');
			<c:forEach var="attr" items="${attributeDefns}" varStatus="count">
			var name = "${attr.name}";
			console.log(name);
			gridColumns.push(name);
			</c:forEach>
			var gridRows = [];
			<c:forEach var="row" items="${businessAdminForm.searchResult.values}" varStatus="rowCount">
				<c:set var="business" value="${row.values[0]}" />
				var row = {};
				row['checkboxValue'] = "<c:out value='${business.businessEntityKey}'/>";
				var businessLink = "<a href='javascript:goSelectBusinessCallback(${business.businessEntityKey})'><c:out value='${business.businessEntityName}'/></a>";
				row['<fmt:message key="business.name" />'] = businessLink;
				row['<fmt:message key="business.id" />'] = "<c:out value='${business.businessEntityIdentifier}'/>";
				row['<fmt:message key="business.businessType" />'] = '<fmt:message key="business.businessTypeName.${business.businessEntityTypeKey}" />';
				row['<fmt:message key="business.description" />'] = `${business.businessEntityDesc}`;
				row['<fmt:message key="business.primaryContact" />'] = "${business.contact.contactName}";
				row['<fmt:message key="business.primaryContactEMail" />'] = "${business.contact.email}";
				 
				<c:forEach var="attr" items="${attributeDefns}" varStatus="count">
				 var name="${attr.name}";
			     var value="${business.getAttribute(attr.name)}";
			     row[name]= value;
				</c:forEach>
				gridRows.push(row);
		</c:forEach>
		</script>


		<c:set var="viewOnly"
			value="${!e2ofn:hasAccess(appContext, 'ADMIN', 'SaveBusiness')}" />
		<c:if test="${empty businessAdminForm.selectedBusinessKey}">
			<e2ot:searchContainerControl form="${businessAdminForm}"
				searchFields="${businessAdminForm.allParameters}"
				formName="businessAdminForm"
				showFilterCollapsed="${businessAdminForm.filterAreaCollapsed}"
				showFilter="${businessAdminForm.showFilterArea}" numColumns="3" />
			<e2ot:searchResultsControl searchForm="${businessAdminForm}"
				formName="businessAdminForm"
				resultTableId="businessAdminFormResultTable" showOrderMenu="false"
				showHideMenu="false" title="Business Entities" showTitle="true" />
		</c:if>

		<c:if test="${!empty businessAdminForm.selectedBusinessKey}">
				<div class="row" id="businessDetails" style="width: 100%;">
					<div class="col-sm-6">
						<h1 style="margin: 15px">${detailTitle}</h1>
					</div>
					<div class="col-sm-6">
						<i class="md-icon" data-popover="#popover-BE"
							aria-haspopup="true" aria-controls=##popover-BE
							style="cursor: pointer; color: #468293; margin-right: 10%;float: right;"
							title="File Download">file_download</i>
						<div class="eto-popover hide-caret" id="popover-BE">
							<div class="eto-popover__content">
								<div>
									<div>
										<button type="button" class="eto-btn eto-btn--link"
											onclick="javascript:goSiteDownload('site')" style="color: black;">
											<i class="md-icon" style="margin-right: 10px">file_download</i>Download	Site</button>
									</div>
									<div>
										<button type="button" class="eto-btn eto-btn--link"
											style="color: black;" data-modal="#basic-modal-curency-download"
											onclick="javascript:goCurrencyDownload('currency')">
											<i class="md-icon" style="margin-right: 10px">file_download</i>Download Currency</button>
									</div>
								</div>
							</div>
							<span class="eto-popover__caret"></span>
						</div>
					</div>
				</div>

				<div id="businessBasicDetails"
					style="background-color: #f1f6f7; display: flex; min-height: 100px">
					<table class="eto-table" id="businessDetailsTable" style="width: 100%;">
						<thead>
							<tr>
								<th><fmt:message key="business.id" /></th>
								<th><fmt:message key="business.name" /></th>
								<th><fmt:message key="business.businessType" /></th>
								<th><fmt:message key="business.externalId" /></th>
								<th><fmt:message key="business.currencies" /></th>
								<th><fmt:message key="business.sites" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><b>${businessAdminForm.selectedBusiness.businessEntityIdentifier}</b></td>
								<td><b>${businessAdminForm.selectedBusiness.businessEntityName}</b></td>
								<td><b><fmt:message
										key="business.businessTypeName.${businessAdminForm.selectedBusiness.businessEntityTypeKey}" /></b></td>
								<td>
									<div class="eto-input__container">
										<c:set var="externalId"
											value="${businessAdminForm.selectedBusiness.externalId}" />
										<b><c:out
												value="${not empty externalId ? externalId : '-'}" /></b>
									</div></td>
								<td>
									<div class="eto-input__container">
										<ul style="font-weight: bold;">
											<c:forEach var="cur"
												items="${businessAdminForm.selectedBusiness.currencies}">
												<li>${cur.currencyCode}</li>
											</c:forEach>
										</ul>
									</div>
								</td>
								<td>
									<div class="eto-expand" id="expand-example-1"
										style="margin: auto; display: inline-block;">
										<ul style="font-weight: bold;">
											<c:forEach var="site"
												items="${businessAdminForm.selectedBusiness.sites}"
												begin="0" end="2">
												<li>${site.siteName}&nbsp;(${site.siteDescription})</li>
											</c:forEach>
										</ul>
										<c:if
											test="${businessAdminForm.selectedBusiness.sites.size() > 3}">
											<a href="javascript:void(0)" class="eto-expand__toggle">
												<span class="eto-expand__toggle-collapsed"><i
													class="md-icon md-icon--sm">add_circle_outline</i> View
													More</span> <span class="eto-expand__toggle-expanded"><i
													class="md-icon md-icon--sm">remove_circle_outline</i> View
													Less</span>
											</a>
											<ul class="eto-expand__content margin-top-xs-1"
												style="font-weight: bold;">
												<c:forEach var="site"
													items="${businessAdminForm.selectedBusiness.sites}"
													begin="3">
													<li>${site.siteName}&nbsp;(${site.siteDescription})</li>
												</c:forEach>
											</ul>
										</c:if>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>

				<Script>
					new eto.Table({ el: document.querySelector('#businessDetailsTable') });
				</Script>

				<div style="margin: 15px; margin-top: 35px">
					<h3>Change/Update Details</h3>
					<hr style="margin-top: 10px">
				</div>

				<div id="businessEditDetails" style="margin: 15px">
					<div class="eto-input">
						<div class="eto-input__container">
							<div class="eto-textarea" id="textBusinessEntityDesc">
								<label class="eto-textarea__label"><fmt:message
										key="business.description" /></label>
								<textarea class="eto-textarea__field"
									onchange="javascript:dataChanged()" id="businessEntityDesc"
									name="selectedBusiness.businessEntityDesc" maxlength="1024"
									placeholder="Enter business description"
									style="width: 750px; overflow: hidden;"></textarea>
							</div>

							<div class="eto-input__message"></div>
						</div>
					</div>
					<script type="text/javascript">
					var businessEntityDesc = `<e2ofn:escapePrint value="${businessAdminForm.selectedBusiness.businessEntityDesc}" removeColon="true"/>`;
					businessEntityDesc = businessEntityDesc.replace(/\/n/g, "");
                               new eto.Textarea({
                                         el: document.querySelector('#textBusinessEntityDesc'),
                                         value: businessEntityDesc
                              });
                             </script>

					<div id="alternate_names"
						style="width: 750px; height: 60px; margin-top: 15px">
						<div class="eto-input" style="vertical-align: baseline;">
							<label class="eto-input__label"><fmt:message
									key="business.alternateNames" /></label>
							<div class="eto-input__container">
								<input class="eto-input__field" type="text" size="30"
									maxlength="50" onchange="javascript:dataChanged()"
									id="alternateInput" placeholder="Enter alternate name">
								<div class="eto-input__message"></div>
							</div>
						</div>
						<div class="eto-autocomplete__tags-container"
							style="background-color: #f1f6f7; vertical-align: middle; padding: 10px; max-height: 100px; overflow-y: scroll">
							<div class="eto-autocomplete__tags" id="populateTags"
								style="display: flex; flex-wrap: wrap;">
								<c:if test="${businessAdminForm.contactChanged}">
									<c:set var="counter" value="0" />
									<c:forEach var="altBusiness"
										items="${businessAdminForm.alternateNamesList}"
										varStatus="abIdx">
										<c:if test="${not empty altBusiness}">
											<span class='eto-tag eto-tag--sm'><span
												class=eto-tag__label style="cursor: default;"><c:out
														value="${altBusiness}" /></span> <span class=eto-tag__remove
												tabindex=0 onclick="closeThisTag($(this))"
												id="alternateName${counter}"> <i
													class='md-icon md-icon--sm'>close</i>
											</span></span>
										</c:if>
										<c:set var="counter" value="${counter+1}" />
									</c:forEach>
								</c:if>

								<c:if test="${!businessAdminForm.contactChanged}">
									<c:set var="counter" value="0" />
									<c:forEach var="altBusiness1"
										items="${businessAdminForm.selectedBusiness.alternates}"
										varStatus="abIdx">
										<span class='eto-tag eto-tag--sm'><span
											class=eto-tag__label><c:out
													value="${altBusiness1.businessEntityName}" /></span> <span
											class=eto-tag__remove tabindex=0
											onclick="closeThisTag($(this))" id="alternateName${counter}">
												<c:set var="counter" value="${counter+1}" /> <i
												class='md-icon md-icon--sm'>close</i>
										</span></span>
									</c:forEach>
								</c:if>
							</div>
						</div>
					</div>
				</div>

				<div style="margin: 15px; margin-top: 125px">
					<h3>
						<fmt:message key="business.primaryContact" />
					</h3>
					<hr style="margin-top: 10px">
				</div>

				<div id="businessContactDetails"
					style="margin: 15px; margin-bottom: 200px;">
					<c:if
						test="${empty businessAdminForm.selectedBusiness.contact.contactName }">
						<span><i>No primary contact</i></span>
						<div style="margin-top: 20px">
							<nav class="eto-form__btns">
								<div class="eto-btn-group">
									<c:if
										test="${e2ofn:hasAccess(appContext, 'ADMIN', 'SaveContact')}">
										<button type="button" id="setContact" class="eto-btn"
											onclick="javascript:findContact(document.forms[0].selectedContactKey);">
											<fmt:message key="button.setContact" />
										</button>
										<button type="button" id="newContact" class="eto-btn"
											onclick="javascript:goNewContact();">
											<fmt:message key="button.newContact" />
										</button>
									</c:if>
								</div>
							</nav>
						</div>
					</c:if>
					<c:if
						test="${!empty businessAdminForm.selectedBusiness.contact.contactName }">
						<form class="eto-form eto-form--horizontal"
							id="horizontal-form-example" style="margin-top: 20px;">
							<div class="eto-input">
								<label class="eto-input__label"><fmt:message
										key="contact.name" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactName" name="selectedBusiness.contact.contactName"
										value="${businessAdminForm.selectedBusiness.contact.contactName}"
										placeholder="Enter contact name" style="width: 400px"
										disabled="disabled">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.email" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactEmail" name="selectedBusiness.contact.email"
										value="${businessAdminForm.selectedBusiness.contact.email}"
										placeholder="Enter email address" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>


							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.business" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactBusiness"
										name="selectedBusiness.contact.businessName"
										value="${businessAdminForm.selectedBusiness.contact.businessName}"
										placeholder="Enter business name" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.id" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactId" name="selectedBusiness.contact.contactId"
										value="${businessAdminForm.selectedBusiness.contact.contactId}"
										placeholder="Enter contact identifier" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.department" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactDepartment"
										name="selectedBusiness.contact.department"
										value="${businessAdminForm.selectedBusiness.contact.department}"
										placeholder="Enter department name" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.address1" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactAddress1" name="selectedBusiness.contact.addressL1"
										value="${businessAdminForm.selectedBusiness.contact.addressL1}"
										placeholder="Enter street address" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.address2" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactAddress2" name="selectedBusiness.contact.addressL2"
										value="${businessAdminForm.selectedBusiness.contact.addressL2}"
										placeholder="Enter street address" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.address3" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactAddress3" name="selectedBusiness.contact.addressL3"
										value="${businessAdminForm.selectedBusiness.contact.addressL3}"
										placeholder="Enter street address" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.city" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactCity" name="selectedBusiness.contact.city"
										value="${businessAdminForm.selectedBusiness.contact.city}"
										placeholder="Enter city name" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.region" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactRegion" name="selectedBusiness.contact.region"
										value="${businessAdminForm.selectedBusiness.contact.region}"
										placeholder="Enter region" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-input" style="margin-top: 15px;">
								<label class="eto-input__label"><bean:message
										key="contact.postalCode" /></label>
								<div class="eto-input__container">
									<input class="eto-input__field" type="text" size="30"
										maxlength="50" onchange="javascript:dataChanged()"
										id="contactPostalCode"
										name="selectedBusiness.contact.postalCode"
										value="${businessAdminForm.selectedBusiness.contact.postalCode}"
										placeholder="Enter postal code" style="width: 400px">
									<div class="eto-input__message"></div>
								</div>
							</div>

							<div class="eto-btn-group" style="margin-top: 15px;">
								<button type="button" id="changeContact1" class="eto-btn"
									onclick="javascript:findContact(document.forms[0].selectedContactKey);">
									<fmt:message key="button.changeContact" />
								</button>
								<button type="button" id="newContact1" class="eto-btn"
									onclick="javascript:goNewContact();">
									<fmt:message key="button.newContact" />
								</button>
							</div>
						</form>
					</c:if>
				</div>

				<div class="footer">
					<nav class="eto-form__btns" style="margin-left: 15px">
						<div class="eto-btn-group" style="margin-top: 15px">
							<c:if
								test="${e2ofn:hasAccess(appContext, 'ADMIN', 'SaveContact')}">
								<button type="button" id="saveButton"
									class="eto-btn eto-btn--primary"
									onclick="javascript:goSaveAndStay();" disabled>
									<bean:message key="button.save" />
								</button>
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
		</c:if>
	</form>
</body>
<script type="text/javascript">

<c:if test="${!empty businessAdminForm.selectedBusinessKey}">
	var expand = new eto.Expand({ el: document.querySelector('#expand-example-1') });
</c:if>

<c:forEach var="altBusiness" items="${businessAdminForm.alternateNamesList}" varStatus="count">
	alternateNames["alternateName"+${count.index}] = <e2ofn:escapePrint value="${altBusiness}" />;
</c:forEach>

var flag = ${viewOnly};

if(!flag){
	$("#addButton").removeClass().addClass("eto-btn eto-btn--primary");
	$("#saveExitButton").removeClass().addClass("eto-btn");
	$("#saveButton").removeClass().addClass("eto-btn eto-btn--primary");
	$("#addButton").removeAttr("disabled");
	$("#saveExitButton").removeAttr("disabled");
	$("#saveButton").removeAttr("disabled");
}

function closeThisTag(element){
	var id = element.attr("id");
	element.parent().css('display','none');
	delete alternateNames[id];
}

$('#alternateInput').keypress(function(event){
    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13') {
        event.preventDefault();
    }
    if(keycode == '13' && $("#alternateInput").val()!=''){
        var value = $(this).val().trim();
        
        if(alternateNames[value] != null)  {
			return;
        }
        alternateNames["alternateName"+Object.keys(alternateNames).length+1] = value;
        $(this).val('');
        var innerHTML = $("#populateTags").html();
        var newTagHTML = "<span class='eto-tag eto-tag--sm'><span class=eto-tag__label>"+value+"</span><span class=eto-tag__remove tabindex=0 onclick=closeThisTag(this) id='"+Object.keys(alternateNames).length+"'><i class='md-icon md-icon--sm'>close</i></span></span>";
        completeHTML = newTagHTML + innerHTML;
        $("#populateTags").html(completeHTML);
    }
});

new eto.Popover({ el: document.querySelector('#popover-BE') });
</script>
</html>