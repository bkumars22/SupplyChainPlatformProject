<%@page import="com.scplatform.pcm.item.entity.Item"%>
<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" fullModalPopUp = "true"/>
<c:choose>
	<c:when
		test="${functionalGroupForm.dynamicTitleName == 'Create Group'}">
		<e2ot:help contextName="Manage-FunctionalGroupCreate" />
	</c:when>
	<c:when
		test="${functionalGroupForm.dynamicTitleName == 'Assign to Group'}">
		<e2ot:help contextName="Manage-FunctionalGroupAssign" />
	</c:when>
	<c:otherwise>
		<e2ot:help contextName="Manage-FunctionalGroupEdit" />
	</c:otherwise>
</c:choose>
<c:set var='allowdSpecialSymbols' value='${e2ofn:getConfigValue("pcm.functional.group.allowedCharacters.ui")}' />
<c:set var='lobMandatoryFgTypesVal' value='${e2ofn:getConfigValue("pcm.lob.mandatory.fgTypes")}' />
<c:set var='platformMandatoryFgTypesVal' value='${e2ofn:getConfigValue("pcm.functional.platform.mandatory.fgTypes")}'/>
<c:set var='fgTypes' value='${e2ofn:getConfigValue("pcm.functional.groupType")}'/>
<c:set var="enableRoleRestriction" value="${e2ofn:getConfigValue('scplatform.feature.enable.CFG.restriction.roles.UI')}" />

<style type="text/css">
.eto-checkbox{
z-index: 0;
}
</style>
<script>
    var lobMandatoryFgTypes = [];
    <c:forEach var='lobMandatoryFgType' items = '${lobMandatoryFgTypesVal}'>
    	lobMandatoryFgTypes.push('${lobMandatoryFgType}')
    </c:forEach>
	var platformMandatoryFgTypes =[];
	<c:forEach var='platformMandatoryFgType' items = '${platformMandatoryFgTypesVal}'>
	platformMandatoryFgTypes.push('${platformMandatoryFgType}')
	</c:forEach>
	let tamErrorMod ;
	function downloadTAM(){
		tamErrorMod.close();
		document.forms[0].action = "downloadErrorFGTAM.do";
		document.forms[0].preserveSearchValues.value = "true";
		
		var num = Math.floor((Math.random() * 1000));
		$('input[name=fileLocation]').val(num);
		
		document.forms[0].submit();
		showWaitBusy();
		
		function checkFileCompleted(){
			$.ajax({
				type: "POST",
				url: 'tamDownloadUtilCheck.do',
				cache: false,
				dataType: 'text',
				data: {
					downloadKey: num
				},
				success: function(result) {
					if(result.match("^true")){
						closeWaitBusy();
						clearInterval(checkFileCompletedHandler);
					}
				},
				error: function(error){
					alert('error'+error.statusText);
					closeWaitBusy();
					clearInterval(checkFileCompletedHandler);
				}
			}); 
		}
		checkFileCompletedHandler = setInterval(function(){ checkFileCompleted() }, 3000);
		
	}
	function changeGroupType() {
		var fgType = $("#functionalGroupType").val();
		if (!platformMandatoryFgTypes.includes(fgType)) {
			$('#select-Platformtype').find('.requiredIndicator').hide();
		} else {
			$('#select-Platformtype').find('.requiredIndicator').show();
		}
		if(lobMandatoryFgTypes.includes(fgType)){
			$('#select-lobType').find('.requiredIndicator').show();
		}
		else{
			$('#select-lobType').find('.requiredIndicator').hide();
		}
		
	}

	function onFunctionalGroupNameChange() {
		document.forms[0].action = "loadFunctionalGroupDetails.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}
    function onFunctionalTypeChange() {
		document.forms[0].action = "createFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}
	function init() {

	}

	function goDeleteItem() {
		var itemList = [];
		$('input[name="items"]:checked').each(function() {
			itemList.push(this.value);
		});

		if (itemList.length == 0) {
			showOkMessageBox(
					'OK',
					'WARN',
					"<fmt:message key='warn.functionalGroup.no_row_selected_to_delete'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
		} else {
			canDelete(deleteMultipleItem,uncheckItem);
		}
	}
	
	function deleteSingleItem(){
		document.forms[0].action = "deleteSingleItemFromFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].deleteItem.value = singleItemValue;
		singleItemValue = '';
		document.forms[0].submit();
		showWaitBusy();
	}
	
	function deleteMultipleItem(){
		
		var itemList = [];
		$('input[name="items"]:checked').each(function() {
			itemList.push(this.value);
		});
		document.forms[0].action = "deleteItemFromFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].itemList.value = itemList;
		document.forms[0].submit();
		showWaitBusy();
	}
	
	function uncheckItem(){
		$('input[name="checkAllItem"]:checked').each(function() {
			$(this).prop("checked",false);
		});
		
		$('input[name="items"]:checked').each(function() {
			$(this).prop("checked",false);
		});
	}
	
	function changeSelectAll(){
		$('input[name="items"]').each(function() {
			if($(this).prop("checked") == false){
				$('#selectAllItem').prop("checked", false);
			    return false;
			}
			else{
				$('#selectAllItem').prop("checked", true);
			}
		});
	}

	
	function selectItem(){
		if($('#selectAllItem').prop("checked") == true){
			$('input[name="items"]').each(function() {
				$(this).prop("checked",true);
			});
		}else{
			uncheckItem();
		}
	}
	
	var singleItemValue = '';
	function deleteItemFromGroup(item) {
		singleItemValue = item;
		canDelete(deleteSingleItem);
	}

	function onParentCallback(finderValues) {
		var parentListByPopUpList = [];
		for (var i = 0; i < finderValues.length; i++) {
			parentListByPopUpList.push(finderValues[i][1]);
		}
		document.forms[0].action = "addParentByPopupSearch.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].parentListByPopUpList.value = parentListByPopUpList;
		document.forms[0].submit();
		showWaitBusy();

	}

	function onItemAddCallback(finderValues) {

		var itemKeyList = [];

		for (var i = 0; i < finderValues.length; i++) {
			itemKeyList.push(finderValues[i][0]);
		}
		document.forms[0].action = "addItemToFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].newItems.value = itemKeyList;
		document.forms[0].submit();
		showWaitBusy();

	}

	
	function goCancel() {

		if (canLeavePage(goCancelCallback)) {
			goCancelCallback();
		}
	}

	function goCancelCallback() {
		document.forms[0].action = "welcome.do";
		document.forms[0].submit();
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
	
	function canDelete(callbackActive,callbackPassive){
		showYesNoMessageBox('YES NO', 'WARN',
				"<fmt:message key='warn.delete_item_warn'/>",
				"<fmt:message key='msg.warn'/>", callbackActive,callbackPassive);
	}
	
	function canSave(callbackActive,callbackPassive){
		showYesNoMessageBox('YES NO', 'WARN',
				"<fmt:message key='info.save_all_item_fg'/>",
				"<fmt:message key='msg.warn'/>", callbackActive,callbackPassive);
	}

	function removeParent() {
		var parentList = [];
		parentList.push($("#parentList").val()); 
		document.forms[0].action = "deleteParentFromFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].selectedParent.value = parentList;
		document.forms[0].submit();
		showWaitBusy();
	}

	function goBack(action) {
		var goBackCallback = function() {
			document.forms[0].action = "searchManageFunctionalGroup.do";;
			document.forms[0].preserveSearchValues.value = "true";
			document.forms[0].submit();
			showWaitBusy();
		}
		if (canLeavePage(goBackCallback)) {
			goBackCallback(action);
		}
	}

	
	function findItem() {		
		console.log('functionalGroup type value', $("#functionalGroupType").val());		
		//doFinderPopup('ItemOnlyFinderWithItemTypeFilter', null, 'onItemAddCallback(%7B0%7D)');		
		if ($("#functionalGroupType").val() == 'XLOB') {

			findItemForXlob();
		}
		else {
			findItemForCFG();
		}
		
	}
	
	function findItemForXlob() {
		doFinderPopup('ItemOnlyFinderForXlob', null, 'onItemAddCallback(%7B0%7D)');
	}
	
	function findItemForCFG() {
		doFinderPopup('ItemOnlyFinderWithItemTypeFilter', null, 'onItemAddCallback(%7B0%7D)');
	}
	
	function findParentItem(){
		doFinderPopup('EnterpriseItemFinderItemType', null, 'onParentItemAddCallback',null,'false');
	}
	
	function removeParentItem(){
		 let values = [['']];
		 onParentItemAddCallback(values);
	}
	
	function removeODMPart(){
		let values = [['']];
		onODMPartAddCallback(values);
	}
	
	function onParentItemAddCallback(finderValues) {
		var parentItemOrODMPartKey = [];
		parentItemOrODMPartKey.push(finderValues[0][0]); 
		document.forms[0].action = "addParentItemToFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].parentItemOrODMPartKey.value = parentItemOrODMPartKey;
		document.forms[0].submit();
		showWaitBusy();

	}
	function findODMPart(){
		doFinderPopup('SupplierItemFinderSupplierType', null, 'onODMPartAddCallback',null,'false');
	}
	
	function onODMPartAddCallback(finderValues) {
		var parentItemOrODMPartKey = [];
		parentItemOrODMPartKey.push(finderValues[0][0]);		
		document.forms[0].action = "addODMPartToFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].parentItemOrODMPartKey.value = parentItemOrODMPartKey;
		document.forms[0].submit();
		showWaitBusy();

	}

	

	function goSaveAndContinue() {		
		var fgName = $("#functionalGroupName").val().trim().replace(/ {1,}/g," ");
		if(fgName == '' || fgName == null){
			showOkMessageBox('OK','WARN',"<fmt:message key='errors.functionalGroup_empty_name_error' />","<fmt:message key='msg.warn'/>",function() {});
		}
		else{
			var patt1 = ${allowdSpecialSymbols};
			var result = fgName.match(patt1);
			$("#functionalGroupName").val(fgName);
			var fgType = $("#functionalGroupType").val();
			if (platformMandatoryFgTypes.includes(fgType)) {
				var fgPlatform = $("#fgPlatform").val().trim().replace(/ {1,}/g," ");
				if(fgPlatform == "" || fgPlatform == null){
					showOkMessageBox('OK','WARN',"<fmt:message key='errors.platform_not_selected' />","<fmt:message key='msg.warn'/>",function() {});
					return;
				}
				else{
					var platformResult = fgPlatform.match(patt1);
					if(platformResult==null){
						showOkMessageBox('OK','WARN','<fmt:message key="warn.error_wrong_data_value_for_fg_platform_ui" />','<fmt:message key="msg.warn"/>',function() {});
						return ;
					}
				}
			}
			if(lobMandatoryFgTypes.includes(fgType))
				{
				var fgLob = $("#fgLob").val().trim().replace(/ {1,}/g," ");
				if(fgLob == ""){
					showOkMessageBox('OK','WARN','<fmt:message key="errors.fg_lob_not_selected" />','<fmt:message key="msg.warn"/>',function() {});
					return ;
				}
				else{
					var lobResult = fgLob.match(patt1);
					if(lobResult==null){
						showOkMessageBox('OK','WARN','<fmt:message key="warn.error_wrong_data_value_for_fg_lob_ui" />','<fmt:message key="msg.warn"/>',function() {});
						return ;
					}
				}
				}
		if(result != null){
			canSave(saveFG,'');
		}else{
			showOkMessageBox('OK','WARN','<fmt:message key="warn.error_wrong_data_value_for_fg_ui" />','<fmt:message key="msg.warn"/>',function() {});
		}
		}
	}

	function saveFG(){
		showWaitBusy();
		document.forms[0].action = "saveFunctionalGroup.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
	}

	function saveFGAndExit(){
		document.forms[0].action = "saveFunctionalGroupAndExit.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}

	function goSave() {
		var fgName = $("#functionalGroupName").val().trim().replace(/ {1,}/g," ");
		if(fgName == '' || fgName == null){
			showOkMessageBox('OK','WARN',"<fmt:message key='errors.functionalGroup_empty_name_error' />","<fmt:message key='msg.warn'/>",function() {});
		}
		else{
			var patt1 = ${allowdSpecialSymbols}
			var result = fgName.match(patt1);
			$("#functionalGroupName").val(fgName);
			var fgType = $("#functionalGroupType").val();
			if (platformMandatoryFgTypes.includes(fgType)) {
				var fgPlatform = $("#fgPlatform").val().trim().replace(/ {1,}/g," ");
				if(fgPlatform == "" || fgPlatform == null){
					showOkMessageBox('OK','WARN',"<fmt:message key='errors.platform_not_selected' />","<fmt:message key='msg.warn'/>",function() {});
					return;
				}
				else{
					var platformResult = fgPlatform.match(patt1);
					if(platformResult==null){
						showOkMessageBox('OK','WARN','<fmt:message key="warn.error_wrong_data_value_for_fg_platform_ui" />','<fmt:message key="msg.warn"/>',function() {});
						return ;
					}
				}
			}
			if(lobMandatoryFgTypes.includes(fgType))
			{
			var fgLob = $("#fgLob").val().trim().replace(/ {1,}/g," ");
			if(fgLob == ""){
				showOkMessageBox('OK','WARN','<fmt:message key="errors.fg_lob_not_selected" />','<fmt:message key="msg.warn"/>',function() {});
				return ;
			}
			else{
				var lobResult = fgLob.match(patt1);
				if(lobResult==null){
					showOkMessageBox('OK','WARN','<fmt:message key="warn.error_wrong_data_value_for_fg_lob_ui" />','<fmt:message key="msg.warn"/>',function() {});
					return ;
				}
			}
			}
		if(result != null){
			canSave(saveFGAndExit,'');
		}else{
			showOkMessageBox('OK','WARN','<fmt:message key="warn.error_wrong_data_value_for_fg_ui" />','<fmt:message key="msg.warn"/>',function() {});
		}
		}
	}

	function trimValue(field) {
		var val = field.value;
		field.value = val.replace(/^\s+|\s+$/g,"");
	}

	$(document).ready(function() {
		var platform = <e2ofn:escapePrint value="${functionalGroupForm.functionalGroup.fgPlatform}"/>;
		$("select[name='fgPlatform']").find("option[value='"+platform+"']").attr("selected",true);
		<c:if test="${functionalGroupForm.hasItemAcessError == 'ACTIVE' and fn:length(functionalGroupForm.nonFGItems) > 0}">
		setTimeout(function() {
			createItemAssignmentModal();
		}, 2000);
		</c:if>
		
		<c:if test="${functionalGroupForm.hasItemAcessError == 'INACTIVE'}">
		setTimeout(function() {
			createItemAssignmentErrorModal();
		}, 2000);
		</c:if>
		
		<c:if test="${!empty functionalGroupForm.fgSiteKeys}">
		setTimeout(function() {
			createDownloadTamModal();
		}, 2000);
		</c:if>
		
		$(function() {
			changeGroupType();
			$("select#functionalGroupType").change(changeGroupType);
		});
	});
	
	function parentNameValidationPopup(){
		 
		var parentName = $('#parentModal input[name="parentName"]').val().trim().replace(/ {1,}/g," ");
		if(parentName.trim() == '' || parentName.trim() == null){
			$('#parentSpan').text("Parent Name Shouldn't be empty");
			return false;
		}else{
			var patt1 = ${allowdSpecialSymbols};
		    var result = parentName.match(patt1);
		    if(result != null){
		    	$("#parentModal #parentName").val(parentName);
		    	return true;
		    }else{
		    	$('#parentModal #parentSpan').text("<fmt:message key ='warn.error_wrong_data_value_for_parent_fg'/>");
		    }
		}
	}
	
	function callOnFunctionalGroupNameSelect(){
		if(this.selectedValue != null){
			onFunctionalGroupNameChange();			
		}			
	}
	
	function loadFunctionalGroupForAssignment(){
		setTimeout(function(){ onFunctionalGroupNameChange();}, 1000);
	}
	function openParenetFgModal(){
		var type = $('#functionalGroupType :selected').text();
		$("#parentModal #parentType").val(type);
		new eto.Modal({
			el : document.querySelector('#parentModal')
		}).open();
	}
	function saveParentFunctionGroup(){
		var type = $('#functionalGroupType :selected').text();
		if(parentNameValidationPopup()){
			document.forms[0].action = "saveParentByPopup.do?parentName="
				+ encodeURIComponent($('#parentModal input[name="parentName"]').val())
				+ "&parentDesc=" + encodeURIComponent($('#parentModal #parentDescription').val())
				+ "&parentType=" + type
				+ "&parentPurpose=" + $('#parentModal #parentBehavior option:selected').val();
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
		new eto.Modal({
			el : document.querySelector('#parentModal')
		}).close();
		}
	}
	
	function showAuditHistory(){
		var fgId = '${functionalGroupForm.functionalGroup.functionalGroupId!=null ? functionalGroupForm.functionalGroup.functionalGroupId : null }';
		showFGAuditHistory(Number(fgId.trim()));
	}

	function createItemAssignmentModal() {
		let mod = new eto.Modal({
			el : document.querySelector('#item-assigment-option-modal')
		});
		mod.open();
	}
	
	function createItemAssignmentErrorModal() {
		let mod = new eto.Modal({
			el : document.querySelector('#item-assigment-error-modal')
		});
		mod.open();
	}
	
	function createDownloadTamModal() {
		tamErrorMod = new eto.Modal({
			el : document.querySelector('#fg-tam-download-option-modal')
		});
		tamErrorMod.open();
	}
	
	
	
</script>
<style type="text/css">
input {
	border-radius: 4px;
}

select {
	border-radius: 4px;
}

table {
	cellpadding: 1px;
}

.modal {
	display: none; /* Hidden by default */
	position: fixed; /* Stay in place */
	z-index: 1; /* Sit on top */
	padding-top: 100px; /* Location of the box */
	left: 0;
	top: 0;
	width: 100%; /* Full width */
	height: 100%; /* Full height */
	background-color: rgb(0, 0, 0); /* Fallback color */
	background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
}

.messageBox {
	margin: auto;
	width: 60%;
	border: 3px;
	padding: 10px;
	background-color: #ebebeb;
	border-radius: 10px;
}

.disableField {
	background-color: #ebebe4;
}
ul li
{
word-break: break-word; white-space: normal; text-align: justify;
}
.requiredIndicator {
	
	color: #ff0000;
}
</style>
</head>
<body onload="init()">
	<fmt:message var="fgBodyHeader" key="functionalGroup.item.details" />
	<e2o:form action="/saveFunctionalGroup"
		style="margin: 0px, padding:0px">
		<input type="hidden" name="backAction"
			value="${functionalGroupForm.backAction}" />
		<input type="hidden" name="hasItemAccessError">
		<input type="hidden" name="preserveSearchValues" />
		<input type="hidden" name="newItems" id="newItems" />
		<input type="hidden" name="itemList" />
		<input type="hidden" name="deleteItem" />
		<input type="hidden" name="selectedParent" />
		<input type="hidden" name="parentListByPopUpList" />
		<input type="hidden" name="parentItemOrODMPartKey" />
		
		<input type="hidden" name="unsavedData"
			value="${functionalGroupForm.unsavedData}" />
		<input type="hidden" name="fileLocation">
		<c:if var="readOnly"
			test="${functionalGroupForm.functionalGroup.status == 'INACTIVE'}" />

		<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
			<div style="font-weight: bold;">
				<e2o:errors maxErrors="4" styleId="errors" />
				<logic:messagesPresent message="true">
					<html:messages id="message" message="true">
						<li>${message}</li>
					</html:messages>
				</logic:messagesPresent>
			</div>
		</div>
		<div id="functionalGroup" align="left" class="col-sm-12"
			style="width: 100%;">
			<h1>${functionalGroupForm.dynamicTitleName}</h1>
			<div class="row margin-bottom-sm-1">
				<div class="col-sm-4">
					<div class="eto-input">
						<label class="eto-input__label"><fmt:message
								key="functionalGroup.functionalGroupName" /></label>
						<div class="eto-input__container">
							<input class="eto-input__field" type="text" size="30"
								maxlength="255" onchange="javascript:dataChanged()"
								id="functionalGroupName" name="functionalGroupName"
								value='${functionalGroupForm.functionalGroup.name}'
								placeholder="Enter Group name" style="width: 400px"
								${functionalGroupForm.dynamicTitleName == 'Assign to Group' ? 'onpaste="loadFunctionalGroupForAssignment();"' : ''}>
							<div class="eto-input__message"></div>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="eto-input">
						<label class="eto-input__label"><fmt:message
								key="functionalGroup.parentName" /></label>
						<div class="eto-select" style="max-width: 400px;">
							<div class="eto-input__gray-container"
								style="display: inline-flex;width: 100%;">
								<div class="eto-select__field-container" style="width: -webkit-fill-available;">
									<select name="parents" class="eto-select__field"
										id="parentList" >
										<c:forEach var="parent"
											items="${functionalGroupForm.functionalGroup.parentFunctionalGroup}">
											<option value="${parent.name}"><c:out
													value="${parent.name}"></c:out></option>
										</c:forEach>
									</select>
								</div>
								<div>
									<div style="margin-top: 0.5rem;margin-left: 0.5rem;display: inline-flex;">
										<c:if test="${(enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'AddParent'))||	(e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'AddParent') && functionalGroupForm.functionalGroup.type!='CFG')}">
											<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-create-fg"
												onclick="javascript:openParenetFgModal()" style="margin: 0;">
												<i class="md-icon color:#ababde">playlist_add</i>
											</button>
											<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-create-fg">
												<div class="eto-tooltip__content">Create</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-search-fg"
												onclick="javascript:doFinderPopup('ParentGroupFinder',null,'onParentCallback(%7B0%7D)')" style="margin: 0;">
												<i class="md-icon">search</i>
											</button>
											<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-search-fg">
												<div class="eto-tooltip__content">Add</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script type="text/javascript">
												new eto.Tooltip({ el: document.querySelector('#tooltip-create-fg') });
												new eto.Tooltip({ el: document.querySelector('#tooltip-search-fg') });
											</script>
										</c:if>
										<c:if
											test="${(enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'DeleteParent')) || (e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'DeleteParent') && functionalGroupForm.functionalGroup.type!='CFG')}">
											<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-delete-fg"
												onclick="javascript:removeParent()" style="margin: 0;">
												<i class="md-icon">delete</i>
											</button>
											<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-delete-fg">
												<div class="eto-tooltip__content">Delete</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script type="text/javascript">
												new eto.Tooltip({ el: document.querySelector('#tooltip-delete-fg') });
											</script>
										</c:if>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
					<c:choose>
						<c:when test="${not empty functionalGroupForm.functionalGroup.parentItem}">
							<c:set var="valueParent">
								<c:out value="${functionalGroupForm.functionalGroup.parentItem.itemNumber} [${functionalGroupForm.functionalGroup.parentItem.businessEntity.businessEntityName}]" /> 
							</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="valueParent" value="" />
						</c:otherwise>
					</c:choose>
					<div class="eto-input">
						<label class="eto-input__label"><fmt:message
								key="functionalGroup.parentItem" /></label>
						<div class="eto-input__gray-container" style="display: inline-flex;width: 100%;max-width: 400px;">
						<input class="eto-input__field" type="text" readonly
								maxlength="255"
								id="functionalGroupParentItem" name="functionalGroupParentItem"
								value="${valueParent}"
								 style="width: -webkit-fill-available">
							<div>
							<div style="margin-top: 0.5rem; margin-left: 0.5rem;display: inline-flex;">
							<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-search-fg"
										onclick="javascript:findParentItem()">
										<i class="md-icon">search</i>
								</button>
								<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-search-fg">
												<div class="eto-tooltip__content">Add</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script type="text/javascript">
												new eto.Tooltip({ el: document.querySelector('#tooltip-search-fg') });
											</script>
											<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-delete-fg"
												onclick="javascript:removeParentItem()">
												<i class="md-icon">delete</i>
											</button>
											<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-delete-fg">
												<div class="eto-tooltip__content">Delete</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script type="text/javascript">
												new eto.Tooltip({ el: document.querySelector('#tooltip-delete-fg') });
											</script>
											</div>
											</div>
							</div>
							<div class="eto-input__message"></div>
						</div>
					</div>
			</div>
			<div class="row margin-bottom-sm-1">
				<div class="col-sm-4">
					<div class="eto-input">
						<div class="eto-input__container">
							<div class="eto-textarea" id="fgDescription">
								<label class="eto-textarea__label"><fmt:message
										key="functionalGroup.groupDescription" /></label>
								<textarea class="eto-textarea__field"
									name="functionalGroupDescription"
									id="functionalGroupDescription" style="width: 400px"
									placeholder="Enter group description" onblur="trimValue(this)"></textarea>
							</div>
							<div class="eto-input__message"></div>
						</div>
					</div>
				</div>
				<script type="text/javascript">
				new eto.Textarea({
					  el: document.querySelector('#fgDescription'),
					  value: '<e2ofn:escapePrint value="${functionalGroupForm.functionalGroup.description}" removeColon="true"/>'
				});
				
				</script>
				
				<div class="col-sm-4 eto-checkbox-group"   style="display: flex;align-items: center;">
					<label class="eto-checkbox"> <input
						class="eto-checkbox__field" type="checkbox" name="status"
						value="on" ${readOnly ? 'checked' : ''}> <span
						class="eto-checkbox__box"></span> <span
						class="eto-checkbox__label"><fmt:message
								key="functionalGroup.inactiveGroup" /></span>
					</label>
				</div>
				
				<div class="col-sm-4">
					<c:choose>
						<c:when test="${not empty functionalGroupForm.functionalGroup.ODMPart}">
							<c:set var="valueODMPart">
								<c:out value="${functionalGroupForm.functionalGroup.ODMPart.itemNumber} [${functionalGroupForm.functionalGroup.ODMPart.businessEntity.businessEntityName}]" /> 
							</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="valueODMPart" value="" />
						</c:otherwise>
					</c:choose>
					<div class="eto-input">
						<label class="eto-input__label"><fmt:message
								key="functionalGroup.ODMPart" /></label>
						<div class="eto-input__gray-container" style="display: inline-flex;width: 100%;max-width: 400px;">
						<input class="eto-input__field" type="text" readonly
								maxlength="255"
								id="functionalGroupODMPart" name="functionalGroupODMPart"
								value='${valueODMPart}'
								style="width: -webkit-fill-available">
								<div>
								<div style="margin-top: 0.5rem; margin-left: 0.5rem;display: inline-flex;">
							<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-search-fg"
										onclick="javascript:findODMPart()">
										<i class="md-icon">search</i>
								</button>
								<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-search-fg">
												<div class="eto-tooltip__content">Add</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script type="text/javascript">
												new eto.Tooltip({ el: document.querySelector('#tooltip-search-fg') });
											</script>
											<button class="eto-icon-btn" type="button" data-tooltip="#tooltip-delete-fg"
												onclick="javascript:removeODMPart()">
												<i class="md-icon">delete</i>
											</button>
											<div class="eto-tooltip" data-anchor-x="center"
												data-anchor-y="middle" id="tooltip-delete-fg">
												<div class="eto-tooltip__content">Delete</div>
												<span class="eto-tooltip__caret"></span>
											</div>
											<script type="text/javascript">
												new eto.Tooltip({ el: document.querySelector('#tooltip-delete-fg') });
											</script>
											</div>
											</div>
								</div>
							<div class="eto-input__message"></div>
						</div>
					</div>
				</div>
				
			<div class="row margin-bottom-sm-1">
				<div class="col-sm-4">
					<div class="eto-select">
						<label class="eto-select__label"><fmt:message
								key="functionalGroup.groupType" /></label>
						<div class="eto-select__container" id="FGType">
							<div class="eto-select__field-container" style="width: 400px;">
								<select name="functionalGroupType" id="functionalGroupType"  class="eto-select__field" onchange="javascript:onFunctionalTypeChange()">
							    </select>
							</div>
							<div class="eto-select__message"></div>
							<script>
								var fgType = new eto.SelectInput({
									el : document
											.querySelector('#FGType')
								});
								var selectOptions = [];
								<c:forEach var="fgType" items="${fgTypes}">
								var itemKey =  '${fgType}';
								var itemVal = '${fgType}';

								var option = {
									"value" : itemKey,
									"label" : itemVal
								};
								var fieldVal = '${functionalGroupForm.functionalGroup.type}';
								if (fieldVal == itemKey) {
									option.selected = true;
								}
								selectOptions.push(option);
								</c:forEach>
								fgType.setOptions(selectOptions);
							</script>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
				<c:set var = 'platformTypes' value="${e2ofn:getConfigValue('pcm.functional.platform.types')}"></c:set>
					<div class="eto-select" id = "select-Platformtype">
						<label class="eto-select__label"><fmt:message
								key="functionalGroup.platform" />
								<SPAN class="requiredIndicator">*</SPAN></label>
						<div class="eto-select__container">
							<div class="eto-select__field-container" style="width: 400px;">
								<select name="fgPlatform" id="fgPlatform"
									class="eto-select__field">
									<option value = "">select platform type ...</option>
								</select>
							</div>
							<div class="eto-select__message"></div>
						</div>
					</div>
					<script type="text/javascript">
						var selectPlatform = new eto.SelectInput({
							el : document
							.querySelector('#select-Platformtype')
						});
						var selectedOptions = [];
						selectedOptions.push({'label':'N/A', 'value':''});
						<c:forEach var = 'platformType' items = '${platformTypes}'>
						var options = {
								'label' : '<c:out value="${platformType}"/>',
								'value': '<c:out value="${platformType}"/>'
						}
						var fieldVal = '<c:out value="${functionalGroupForm.functionalGroup.fgPlatform}" />';
						if (fieldVal == '<c:out value="${platformType}"/>')  {
							options.selected = true;
						}
						selectedOptions.push(options);
						</c:forEach>
						selectPlatform.setOptions(selectedOptions);
					</script>
				</div>
				<div class="col-sm-4">
				<c:set var = "lobTypes" value = "${e2ofn:getConfigValue('pcm.functional.lob.types')}"></c:set>
					<div class="eto-select" id="select-lobType">
						<label class="eto-select__label"><fmt:message
								key="functionalGroup.lob" /><SPAN
							class="requiredIndicator">*</SPAN></label>
						<div class="eto-select__container">
							<div class="eto-select__field-container" style="width: 400px;">
								<select name="fgLob" id="fgLob"
									class="eto-select__field">
									<option value = ''>select lob type ...</option>
								</select>
							</div>
							<div class="eto-select__message"></div>
						</div>
					</div>
					<script type="text/javascript">
						var selectLobType = new eto.SelectInput({
							el : document.querySelector('#select-lobType')
						});
						
						var selectOptions = [];
						selectOptions.push({'label':'N/A', 'value':''});
						
						<c:forEach var = 'lobType' items = '${lobTypes}'>
							var options = {
									'label':'${lobType}',
									'value':'${lobType}'
							};
							if('${lobType}' == '${functionalGroupForm.functionalGroup.fgLob.lobValue}')
								options.selected = true;
							selectOptions.push(options);
						</c:forEach>
						selectLobType.setOptions(selectOptions);

					</script>
				</div>
			</div>
			<div class="row margin-bottom-sm-1">
				<div class="col-sm-4">
					<div>
						<label class="eto-select__label"><fmt:message key="functionalGroup.fgStatus" /></label>
					</div>
					<div>
						<div class="eto-input__container">
							<input class="eto-input__field" type="text" size="30"
								maxlength="255" onchange="javascript:dataChanged()"
								id="fgstatus" name="fgstatus" style="width: 400px"
								value='${functionalGroupForm.functionalGroup.status}' disabled />
							<div class="eto-input__message"></div>
						</div>
					</div>
				</div>
				<c:if test="${e2ofn:getConfigValue('scplatform.feature.functionalgroup.aliasName.show') == 'true'}">
                    <div class="col-sm-4">
                        <div>
                            <label class="eto-select__label"><fmt:message key="functionalGroup.alias.functionalGroupName" /></label>
                        </div>
                        <div>
                            <div class="eto-input__container">
                                <input class="eto-input__field" type="text" size="30"
                                    maxlength="255" onchange="javascript:dataChanged()"
                                    id="fgAliasName" name="fgAliasName" style="width: 400px"
                                    value='${functionalGroupForm.functionalGroup.aliasName}' disabled />
                                <div class="eto-input__message"></div>
                            </div>
                        </div>
                    </div>
				</c:if>
			</div>
			<div class="row margin-bottom-sm-1" style="width: 100%;">
				<div class="col-sm-4">
					<label
						style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px;">Items</label>
				</div>
			</div>
			<div class="row margin-bottom-sm-1" style="width: 100%;">
				<div class="col-sm-8">
					 <div class="eto-btn-group">
					  <c:if test="${(enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'AddItem')) || (e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'AddItem') && functionalGroupForm.functionalGroup.type!='CFG')}">
                        <button type="button" class="eto-btn"}
                     	onclick="javascript:findItem();"> <fmt:message key="button.addItem" />
                     	 </button>
                        </c:if>
                     	 <c:if test="${(enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'DeleteItem')) || (e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'DeleteItem') && functionalGroupForm.functionalGroup.type!='CFG')}">
                                <button type="button" class="eto-btn" onclick="javascript:goDeleteItem();">Remove Item</button>
                        </c:if>
                     </div>
				</div>
			</div>
			<div class="row"
				style="padding-left: 15px; width: 100%; overflow: auto; padding-bottom: 100px;">
				<table class="eto-table col-lg-12"
					style="border: 2px #d3e2e6 solid;">
					<thead>
						<tr>
							<th><label class="eto-checkbox" style="z-index: 0">
									<input class="eto-checkbox__field" type="checkbox"
									id="selectAllItem" name="checkAllItem"
									onclick="javascript:selectItem();"> <span
									class="eto-checkbox__box"></span>
							</label></th>
							<th></th>
							<th><fmt:message key="functionalGroup.itemNumber" /></th>
							<th><fmt:message key="item.businessName" /></th>
							<th><fmt:message key="functionalGroup.itemDescription" /></th>
							<th><fmt:message key="functionalGroup.commodityName" /></th>
							<th><fmt:message key="functionalGroup.TAM.available" /></th>
							<th><fmt:message key="item.EOL" /></th>
							<c:if test="${!empty functionalGroupForm.addedItemKeys}">
								<th><fmt:message key="item.add.status" /></th>
							</c:if>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="itemMain"
							items="${functionalGroupForm.nonFGItems}">
							<c:forEach var="items"
								items="${functionalGroupForm.functionalGroup.functionalGroupItems}">
								<c:if test="${itemMain == items}">
									<tr id="${items.itemKey}">
										<td style="width: 1%;"><label class="eto-checkbox">
												<input class="eto-checkbox__field" type="checkbox"
												id="selectAllItem" name="items"
												onclick="javascript:changeSelectAll();"
												value="${items.itemKey}"> <span
												class="eto-checkbox__box"></span>
										</label></td>
										<td><c:if
												test="${functionalGroupForm.nonFGItems.contains(items)}">
												<img alt="New Items"
													src="./skins\e2-modern\images\new_item.png">
											</c:if></td>
										<td><c:out value="${items.itemNumber}"></c:out></td>
										<td><c:out
												value="${items.businessEntity.businessEntityName}"></c:out></td>
										<td><c:out value="${items.description}"></c:out></td>
										<td><c:forEach var="cat" items="${items.categories}"
												varStatus="status">
												<c:if test="${status.count != 1}">
													<c:out value="," />
												</c:if>
												<c:out value="${cat}" />
											</c:forEach></td>
										<td><c:out
												value="${functionalGroupForm.tamExistValues[items.itemKey]}" /></td>
										<td><c:out
												value="${ !empty items.eolType ? items.eolType : 'ACTIVE'}" /></td>
										<c:if test="${!empty functionalGroupForm.addedItemKeys}">
											<c:choose>
											<c:when test="${functionalGroupForm.isAddedItemPresentInResult(items.itemKey,functionalGroupForm.functionalGroup.functionalGroupId, functionalGroupForm.functionalGroup.name)}">
												<td><c:out value="New" /></td>
											</c:when>
											<c:otherwise>
											    <td><c:out value="" /></td>
											</c:otherwise>
											</c:choose>
										</c:if>
										<td>
										<c:if test="${(enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'DeleteItem'))|| (e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'DeleteItem') && functionalGroupForm.functionalGroup.type!='CFG')}">
                                        		 <e2i2:img src="/delete.png"  onclick="deleteItemFromGroup('${items.itemKey}');" alt="Remove Item" />
                                         </c:if>
										</td>
										</tr>
								</c:if>
							</c:forEach>
						</c:forEach>
						<c:forEach var="sortedItemKey"
							items="${functionalGroupForm.sortedFGItems}">
							<c:forEach var="items"
								items="${functionalGroupForm.functionalGroup.functionalGroupItems}">
								<c:if test="${sortedItemKey == items.itemKey}">
									<tr id="${items.itemKey}">
										<td><label class="eto-checkbox"> <input
												class="eto-checkbox__field" type="checkbox"
												id="selectAllItem" name="items"
												onclick="javascript:changeSelectAll();"
												value="${items.itemKey}"> <span
												class="eto-checkbox__box"></span></label></td>
										<td><c:if
												test="${functionalGroupForm.nonFGItems.contains(items)}">
												<img alt="New Items"
													src="./skins\e2-modern\images\new_item.png">
											</c:if></td>
										<td><c:out value="${items.itemNumber}"></c:out></td>
										<td><c:out
												value="${items.businessEntity.businessEntityName}"></c:out></td>
										<td><c:out value="${items.description}"></c:out></td>
										<td><c:forEach var="cat" items="${items.categories}"
												varStatus="status">
												<c:if test="${status.count != 1}">
													<c:out value="," />
												</c:if>
												<c:out value="${cat}" />
											</c:forEach></td>
										<td><c:out
												value="${functionalGroupForm.tamExistValues[items.itemKey]}" /></td>
										<td><c:out
												value="${ !empty items.eolType ? items.eolType : 'ACTIVE'}" /></td>
										<c:if test="${!empty functionalGroupForm.addedItemKeys}">
										    <c:choose>
											<c:when test="${functionalGroupForm.isAddedItemPresentInResult(items.itemKey,functionalGroupForm.functionalGroup.functionalGroupId, functionalGroupForm.functionalGroup.name)}">
												<td><c:out value="New" /></td>
											</c:when>
											<c:otherwise>
											    <td><c:out value="" /></td>
											</c:otherwise>
											</c:choose>
										</c:if>
										<td>
										<c:if test="${(enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'DeleteItem')) || (e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'DeleteItem')  && functionalGroupForm.functionalGroup.type!='CFG')}">
                                        	 <e2i2:img src="/delete.png"  onclick="deleteItemFromGroup('${items.itemKey}');" alt="Remove Item" />
                                        	 </c:if>
									  <td>
									</tr>
								</c:if>
							</c:forEach>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="row margin-bottom-sm-1" style="width: 100%;">
				<div class="col-sm-4">
					<label
						style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px;">Deleted Items</label>
				</div>
			</div>
			<div class="row"
				style="padding-left: 15px; width: 100%; overflow: auto; padding-bottom: 100px;">
				<table class="eto-table col-lg-4" id="deletedItemsTable"
					style="border: 2px #d3e2e6 solid;">
					<thead>
						<tr>
							<th><fmt:message key="functionalGroup.itemNumber" /></th>
							<th><fmt:message key="functionalGroup.item.removed.userid" /></th>
							<th><fmt:message key="functionalGroup.item.removed.timestamp" /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			<div id="loader" style="display:none; text-align:center; margin:10px;">
                <img src="skins/e2-modern/images/print_wait.gif" alt="Loading..." />
                <p>Loading data...</p>
            </div>
		</div>
		<div class="footer">
			<div class="row" style="margin: 1.5rem;">
				<div class="eto-btn-group">
					<div class="eto-btn-group">
						<c:if test="${(e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'Save') && functionalGroupForm.functionalGroup.type!='CFG') || (enableRoleRestriction && e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP_CFG', 'Save') && functionalGroupForm.functionalGroup.type=='CFG')}">
                           <button type="button" class="eto-btn eto-btn--primary" id="saveAndReturnButton" onclick="javascript:goSave();">
                                     <fmt:message key="button.save_all_return" />
                                 </button>
                                 <button type="button" class="eto-btn" id="saveButton" onclick="javascript:goSaveAndContinue();">
                                    <fmt:message key="button.save_all" />
                                  </button>
                         </c:if>
						<c:if
							test="${!fn:startsWith(functionalGroupForm.dynamicTitleName,'Create')}">
							<button type="button" class="eto-btn" id="showHistoryButton"
								onclick="javascript:showAuditHistory();">
								<fmt:message key="button.showHistory" />
							</button>
						</c:if>
						<button type="button" class="eto-btn" id="backButton"
							onclick="javascript:goBack('${fn:escapeXml(functionalGroupForm.backAction)}');">
							<fmt:message key="button.back" />
						</button>
						<button type="button" class="eto-btn" id="cancelButton"
							onclick="javascript:goCancel()">
							<fmt:message key="button.cancel" />
						</button>
					</div>
				</div>
			</div>
		</div>
		<c:if
			test="${functionalGroupForm.dynamicTitleName == 'Assign to Group'}">
			<script type="text/javascript">
			$(document).ready(
					function() {

						$("#functionalGroupName").autocomplete(
								"ajaxQueryFunctionalGroupName.do", {
									delay : 500,
									minChars : 1,
									matchSubset : 1,
									maxItemsToShow : 20,
									matchContains : 0,
									cacheLength : 20,
									autoFill : true,
									selectedCurrent : true,
									selectCallBack : callOnFunctionalGroupNameSelect
								});
					});
		</script>
		</c:if>
		<div class="eto-modal" id="parentModal"
			style="border: 2px #f1f6f7 solid;">
			<div class="eto-modal__content col-xs-4 col-sm-4 col-xl-4">
				<header class="eto-modal__header">
					<span>New Parent</span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<div style="margin: 0 auto;">
						<div class="row">
							<div class="eto-input col-lg-12" id="text-input-example-1"
								style="padding: 5px;">
								<label class="eto-input__label" style="padding-bottom: 10px;">Parent
									Name<span style="color: red;">*</span>
								</label> <input class="eto-input__field" type="text" name="parentName"
									id="parentName" placeholder="Enter Parent Group Name "><span
									id='parentSpan' style='color: red; font-weight: bolder;'></span>
							</div>
						</div>
						<div class="row">
							<div class="eto-textarea col-lg-12" id="textarea-example-1"
								style="padding: 5px;">
								<label class="eto-textarea__label" style="padding-bottom: 10px;">Parent
									Description</label>
								<textarea class="eto-textarea__field" name="parentDescription"
									id='parentDescription' placeholder="Enter Parent Description"></textarea>
							</div>
						</div>
						<div class="row">
							<div class="eto-input col-lg-12" id="text-input-example-1"
								style="padding: 5px;">
								<label class="eto-input__label" style="padding-bottom: 10px;">Type
									<span style="color: red;">*</span>
								</label> <input class="eto-input__field" type="text" id='parentType'
									name='parentType' placeholder="Enter Type" readonly="readonly">
							</div>
						</div>
						<div class="row">
							<div class="eto-select col-lg-12" id="select-example-1"
								style="padding: 5px;">
								<label class="eto-select__label" style="padding-bottom: 10px;">Behavior
									<span style="color: red;">*</span>
								</label>
								<div class="eto-select__field-container">
									<select class="eto-select__field" id='parentBehavior'
										name='parentBehavior'>
										<option value='MassUpdate'>Mass Update</option>
										<option value='Aggregation'>Aggregation</option>
									</select>
								</div>
							</div>
						</div>
					</div>
				</section>
				<footer class="eto-modal__footer" style="border: 2px #f1f6f7 solid;">
					<div style="padding-top: 5px;">
						<button class="eto-btn" data-modal-close>Close</button>
						<button class="eto-btn eto-btn--primary" type="button"
							onclick="javascript:saveParentFunctionGroup();">Save</button>
					</div>
				</footer>
			</div>
		</div>
		<div class="eto-modal" id="item-assigment-option-modal"
			style="width: 100%;">
			<script type="text/javascript">
				new eto.Modal({
					el : document.querySelector('#basic-modal-example')
				});
				let errorItemList = [];
			</script>
			<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
				<header class="eto-modal__header">
					<span>Assignment Error</span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<p>
						<fmt:message key="info.item.assignment.wanttoassign" />
						&nbsp;(
						<c:forEach items="${functionalGroupForm.selectedItems}" var="item"
							varStatus="loop">

							<script>
								errorItemList.push('${item.itemNumber}');
							</script>
					${item.itemNumber}
					<c:if test="${!loop.last}">,</c:if>
						</c:forEach>
						)
					</p>
				</section>
				<footer class="eto-modal__footer">
					<script>
						let itemAssigmentURL = 'submitItemManagementSearch.do?value(itemNumbers)='+ errorItemList.join('${e2ofn:getConfigValue("pcm.web.search.multiValueDelimiter")}')+'&values(itemType)=I${functionalGroupForm.functionalGroup != null && (functionalGroupForm.functionalGroup.type == 'XLOB' || functionalGroupForm.functionalGroup.type == 'EM')  ? '&values(itemType)=S' : ''}';
					</script>
					<button class="eto-btn" type="button"
						onclick="javascript:openItemAssignmentModal(itemAssigmentURL);"
						data-modal-close>Yes</button>
					<button class="eto-btn eto-btn--primary" data-modal-close>No</button>
				</footer>
			</div>
		</div>

		<div class="eto-modal" id="item-assigment-error-modal">
			<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
				<header class="eto-modal__header">
					<span>Error</span>
				</header>
				<section class="eto-modal__body">
					<p>
						<fmt:message key="info.item.assignment.error" />
					</p>
				</section>
				<footer class="eto-modal__footer">
					<button class="eto-btn" data-modal-close>Ok</button>
				</footer>
			</div>
		</div>

		<div class="eto-modal" id="fg-tam-download-option-modal">
			<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
				<header class="eto-modal__header">
					<span>Error</span>
				</header>
				<section class="eto-modal__body">
					<p>${functionalGroupForm.errorMessageActivationFG}</p>
				</section>
				<footer class="eto-modal__footer">
					<button class="eto-btn" onclick="javascript:downloadTAM();"
						data-modal-close>Download</button>
				</footer>
			</div>
		</div>
	</e2o:form>
	<%@ include file="../fullModal.jspf"%>
</body>
<script>
$(document).ready(
	function() {
	    var fgKey= '${functionalGroupForm.functionalGroup.functionalGroupId!=null ? functionalGroupForm.functionalGroup.functionalGroupId : null }';
	    if(fgKey && fgKey!=="null"){
            $.ajax({
                    type: "GET",
                    traditional: true,
                    url: "mcm/api/functionalGroup/deletedItem",
                    data: {fgkey:fgKey},
                    beforeSend: function() {
                        $("#loader").show();
                        $("#deletedItemsTable tbody").hide();
                    },
                    success: function(response) {
                        const responseObj = typeof response === "string" ? JSON.parse(response) : response;
                        $("#deletedItemsTable tbody").empty();
                        responseObj.forEach(item=>{
                            var tr = document.createElement("tr");
                            tr.id = item.auditKey;
                            ["itemIdentifier", "userId", "datePerformed"].forEach(key => {
                                var td = document.createElement("td");
                                td.textContent = item[key];
                                tr.appendChild(td);
                            });
                            document.querySelector("#deletedItemsTable tbody").appendChild(tr);
                        })
                        $("#loader").hide();
                        $("#deletedItemsTable tbody").show();
                    },
                    error: function(error) {
                        console.error("Error fetching data", error);
                    }
            });
        }
})

</script>
</html>