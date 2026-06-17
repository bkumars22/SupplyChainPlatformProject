<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<c:choose>
	<c:when
		test="${parentFunctionalGroupForm.dynamicTitleName == 'Create Group'}">
		<e2ot:help contextName="Manage-ParentFunctionalGroupCreate" />
	</c:when>
	<c:when
		test="${parentFunctionalGroupForm.dynamicTitleName == 'Assign to Group'}">
		<e2ot:help contextName="Manage-ParentFunctionalGroupAssign" />
	</c:when>
	<c:otherwise>
		<e2ot:help contextName="Manage-ParentFunctionalGroupEdit" />
	</c:otherwise>
</c:choose>
<c:set var='allowdSpecialSymbols' value='${e2ofn:getConfigValue("pcm.functional.group.allowedCharacters.ui")}' />
<script>
        $(document).keypress(
		  function(event){
		    if (event.which == '13') {
		      event.preventDefault();
		    }
		});
	function onParentNameChange() {
		document.forms[0].action = "loadParentDetails.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}

	function init() {

	}

	function goDeleteFunctionalGroup() {
		var functionalGroupList = [];
		$('input[name="selectedFunctionalGroupId"]:checked').each(function() {
			functionalGroupList.push(this.value);
		});

		if (functionalGroupList.length == 0) {
			showOkMessageBox(
					'OK',
					'WARN',
					"<fmt:message key='warn.functionalGroup.no_row_selected_to_delete'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
		} else {
			document.forms[0].action = "deleteFunctionalGroupFromParent.do";
			document.forms[0].preserveSearchValues.value = "true";
			document.forms[0].deletefunctionalGroupList.value = functionalGroupList;
			document.forms[0].submit();
			showWaitBusy();
		}

	}

	function deleteSingleFunctionalGroupFromParent(functionalGroupId) {
		document.forms[0].action = "deleteSingleFunctionalGroupFromParent.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].deleteFunctionalGroup.value = functionalGroupId;
		document.forms[0].submit();
		showWaitBusy();
	}

	function goCancel() {
		if (canLeavePage(goCancelCallback)) {
			goCancelCallback();
		}
	}

	function goCancelCallback() {
		<c:if test="${!parentFunctionalGroupForm.isTAMRedirect}">
			showWaitBusy();
			document.forms[0].action = "welcome.do";
			document.forms[0].submit();
		</c:if>
		<c:if test="${parentFunctionalGroupForm.isTAMRedirect}">
		parent.parent.fullScreenModal.close(true);
		</c:if>
	}

	function canLeavePage(callback) {
		
		if (document.forms[0].unsavedData.value == 'true') {
		var popupModal =  new eto.Modal({
			el : document.querySelector('#pfg_modal')
		}); 
			 $('#popup_modal_header').text("<fmt:message key='msg.warn'/>");
			$('#popup_modal_body').html("<fmt:message key='warn.changes_not_saved_yes_no'/>");
			 $('#popup_modal_footer').html('<button class="eto-btn eto-btn--primary" data-modal-close id="popup_modal_activeButton">YES</button><button class="eto-btn" data-modal-close id="popup_modal_passiveButton">NO</button>');
			 popupModal.on('closed', function(query) {		
			});
			 $( "#popup_modal_activeButton" ).bind( "click", function() {
			  if ($.isFunction(callback)) {
				  callback.apply();
			 	}
			});
			popupModal.open(); 
			return false;
		}
		
		return true;
	}

	function goBack(action) {

		var goBackCallback = function() {
			document.forms[0].action = "${parentFunctionalGroupForm.backAction}";
			document.forms[0].preserveSearchValues.value = "true";
			document.forms[0].submit();
			showWaitBusy();
		}
		if (canLeavePage(goBackCallback)) {
			goBackCallback(action);
		}
	}

	function goSaveAndContinue() {
		var parentName = $("#parentFunctionalGroupName").val().trim().replace(/ {1,}/g," ");
		if(parentName == '' || parentName == null){
			 showOkMessageBox('OK','WARN',"<fmt:message key='error.parentGroup_name_empty' />","<fmt:message key='msg.warn'/>",function() {});
		}else{
		var patt1 = ${allowdSpecialSymbols};
		var result = parentName.match(patt1);
		$("#parentFunctionalGroupName").val(parentName);
		if(result != null){
			document.forms[0].action = "saveParentFunctionalGroup.do";
			document.forms[0].preserveSearchValues.value = "true";
			document.forms[0].submit();
			showWaitBusy();
		}
   else{
	   showOkMessageBox('OK','WARN',"<fmt:message key='warn.error_wrong_data_value_for_parent_fg' />","<fmt:message key='msg.warn'/>",function() {});
     }	
	}
	}

	function goSave() {
		var parentName = $("#parentFunctionalGroupName").val().trim().replace(/ {1,}/g," ");
		if(parentName == '' || parentName == null){
			 showOkMessageBox('OK','WARN',"<fmt:message key='error.parentGroup_name_empty' />","<fmt:message key='msg.warn'/>",function() {});
		}else{
		var patt1 = ${allowdSpecialSymbols};
		var result = parentName.match(patt1);
		$("#parentFunctionalGroupName").val(parentName);	
		if(result != null){
			document.forms[0].action = "saveParentAndExit.do";
			document.forms[0].preserveSearchValues.value = "true";
			document.forms[0].submit();
			showWaitBusy();
		}
   else{
	   showOkMessageBox('OK','WARN',"<fmt:message key='warn.error_wrong_data_value_for_parent_fg' />","<fmt:message key='msg.warn'/>",function() {});
   }
   }	
	}	

	function findFunctionalGroup() {
		doFinderPopup('FunctionalGroupFinder', null,
				'onFunctionalGroupAddCallback(%7B0%7D)');
	}

	function onFunctionalGroupAddCallback(finderValues) {

		var functionalGroupList = [];

		for (var i = 0; i < finderValues.length; i++) {
			functionalGroupList.push(finderValues[i][0]);
		}
		document.forms[0].action = "addFunctionalGroupToParent.do";
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].newFunctionalGroupList.value = functionalGroupList;
		document.forms[0].submit();
		showWaitBusy();
	}
	
	function trimValue(field) {
	       var val = field.value;
	       field.value = val.replace(/^\s+|\s+$/g,"");
    }
	 
	function callOnParentFunctionalGroupNameSelect(){
		if(this.selectedValue != null){
			onParentNameChange();			
		}			
	}
	
	function loadParentFunctionalGroupForAssignment(){
		setTimeout(function(){ onParentNameChange();}, 1000);
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

.messageBox {
	margin: auto;
	width: 60%;
	border: 3px;
	padding: 10px;
	background-color: #ebebeb;
	border-radius: 10px;
}
</style>
</head>
<body onload="init()">
	<fmt:message var="pfgBodyHeader" key="functionalGroup.group.details" />
	<e2o:form action="/saveFunctionalGroup" style="margin:0px,padding:0px">
		<html:hidden property="backAction" value="${parentFunctionalGroupForm.backAction}" />
		<input type="hidden" name="preserveSearchValues" />
		<input type="hidden" name="deletefunctionalGroupList" />
		<input type="hidden" name="newFunctionalGroupList" />
		<input type="hidden" name="deleteFunctionalGroup" />
		<input type="hidden" name="unsavedData"	value="${parentFunctionalGroupForm.unsavedData}" />
		<input type="hidden" name="isTAMRedirect" value="${parentFunctionalGroupForm.isTAMRedirect}" />
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
		<div id="pfunctionalGroup" align="left"
			class="col-sm-12" style="width: 100%;">
			<h1 style="padding-bottom: 10px;">${parentFunctionalGroupForm.dynamicTitleName}</h1>
			<div class="row ">
				<div class="col-sm-4">
					<div class="eto-input">
				<label class="eto-input__label"><fmt:message key="functionalGroup.parentName" /></label>						
				<div class="eto-input__container">
							<input class="eto-input__field" type="text" size="30"
								maxlength="60" id="parentFunctionalGroupName"
								onblur="trimValue(this)" class="inputField"
								name="parentGroupName"
								value="${parentFunctionalGroupForm.parentFunctionalGroup.name}"
								placeholder="Enter Parent name" style="width: 400px"
								autocomplete="off"
								${parentFunctionalGroupForm.dynamicTitleName == 'Assign to Group' ? 'onpaste="loadParentFunctionalGroupForAssignment();"' : ''} />
							<div class="eto-input__message"></div>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="eto-select">
						<label class="eto-select__label"><fmt:message
								key="functionalGroup.groupType" /></label>
						<div class="eto-select__container">
							<div class="eto-select__field-container" style="width: 400px;">
								<select name="parentGroupType" id="parentGroupType"
									class="eto-select__field">
									<option value="CFG"
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.type == 'CFG'}">selected="selected"</c:if>>CFG</option>
									<option value="NFG"
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.type == 'NFG'}">selected="selected"</c:if>>NFG</option>
									<option value="XLOB"
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.type == 'XLOB'}">selected="selected"</c:if>>XLOB</option>
								</select>
							</div>
							<div class="eto-select__message"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row  margin-bottom-sm-1">
				<div class="col-sm-4">
					<div class="eto-input">
						<div class="eto-input__container">
							<div class="eto-textarea" id="textarea-example-1">
								<label class="eto-textarea__label"><fmt:message
										key="parentFunctionalGroup.description" /></label>
								<textarea class="eto-textarea__field"
									name="parentGroupDescription" id="functionalGroupDescription"
									style="width: 400px"
									placeholder="Enter Parent group description"
									onblur="trimValue(this)"><c:out
										value="${parentFunctionalGroupForm.parentFunctionalGroup.description}"></c:out></textarea>
							</div>
							<div class="eto-input__message"></div>
						</div>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="eto-select">
						<label class="eto-select__label"><fmt:message
								key="parentFunctionalGroup.purpose" /></label>
						<div class="eto-select__container">
							<div class="eto-select__field-container" style="width: 400px;">
								<select name="parentGroupPurpose" id="parentGroupPurpose"
									class="eto-select__field">
									<option value="MASSUPDATE"
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.purpose == 'MASSUPDATE'}">selected="selected"</c:if>>MassUpdate
									<option value="AGGREGATION"
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.purpose == 'AGGREGATION'}">selected="selected"</c:if>>Aggregation</option>
									<option value="ALL"
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.purpose == 'ALL'}">selected="selected"</c:if>>All</option>
									<option value=""
										<c:if test="${parentFunctionalGroupForm.parentFunctionalGroup.purpose == ''}">selected="selected"</c:if>>None</option>
								</select>
							</div>
							<div class="eto-select__message"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row  margin-bottom-sm-1" style="padding-top: 10px; width: 100%;">
				<div class="col-sm-4">
					<label
						style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px; margin-right: 30px">${pfgBodyHeader}
					</label>
				</div>
				</div>
				<div class="row  margin-bottom-sm-1" style="padding-top: 10px; width: 100%;">
				<div class="col-sm-8">
					<div class="eto-btn-group" >
						<c:if
							test="${e2ofn:hasAccess(appContext, 'PARENT_FUNCTIONAL_GROUP', 'AddFG')}">
							<button type="button" class="eto-btn"
								onclick="javascript:findFunctionalGroup();">
								<fmt:message key="button.addFunctionalGroup" />
							</button>
						</c:if>
						<c:if
							test="${e2ofn:hasAccess(appContext, 'PARENT_FUNCTIONAL_GROUP', 'Delete')}">
							<button type="button" class="eto-btn"
								onclick="javascript:goDeleteFunctionalGroup();">
								<fmt:message key="button.remove_FunctionalGroup" />
							</button>
						</c:if>
					</div>
				</div>
			</div>
			<div class="row" style="padding-top: 10px;width: 100%;margin-bottom: 100px;margin-left: 0.1rem;">
				<table class="eto-table col-lg-12" 
					style="border: 2px #d3e2e6 solid;width: 100%;">
					<thead>
						<tr>
							<th></th>
							<th><fmt:message key="functionalGroup.functionalGroupName" /></th>
							<th><fmt:message key="functionalGroup.itemDescription" /></th>
							<th><fmt:message key="functionalGroup.groupType" /></th>
							<th><fmt:message key="functionalGroup.status" /></th>
							<th><fmt:message key="functionalGroup.createdOn" /></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="functionalGroup"
							items="${parentFunctionalGroupForm.parentFunctionalGroup.functionalGroups}">
							<tr>
							<td><label class="eto-checkbox"  style="z-index: 0;"> <input
												class="eto-checkbox__field" type="checkbox"
												id="selectAllItem" name="selectedFunctionalGroupId"
												onclick="javascript:changeSelectAll();"
												value="${functionalGroup.functionalGroupId}"> <span
												class="eto-checkbox__box"></span></label>
												 </td>
							<td><c:out value="${functionalGroup.name}"></c:out></td>
							<td><c:out value="${functionalGroup.description}"></c:out></td>
							<td><c:out value="${functionalGroup.type}"></c:out></td>
							<td><c:out value="${functionalGroup.status}"></c:out></td>
							<td><c:out value="${functionalGroup.createdOn}"></c:out></td>
							<td><c:if
									test="${e2ofn:hasAccess(appContext, 'PARENT_FUNCTIONAL_GROUP', 'Delete')}">
									<e2i2:img src="/delete.png"
										onclick="deleteSingleFunctionalGroupFromParent('${functionalGroup.functionalGroupId}');" />
								</c:if></td>
								</tr>
						</c:forEach>
						
					</tbody>
				</table>
			</div>
		</div>
	<div class="footer" >
		 <div class="row" style="margin: 10px;">
			<div class="eto-btn-group">
				<c:if
					test="${e2ofn:hasAccess(appContext, 'PARENT_FUNCTIONAL_GROUP', 'Save')}">
					<c:if test="${!parentFunctionalGroupForm.isTAMRedirect}">
						<button type="button" class="eto-btn eto-btn--primary"
							id="saveAndReturnButton" onclick="javascript:goSave();">
							<fmt:message key="button.save_all_return" />
						</button>
					</c:if>
					<button type="button" class="eto-btn"
						id="saveButton" onclick="javascript:goSaveAndContinue();">
						<fmt:message key="button.save_all" />
					</button>
				</c:if>
				<c:if
					test="${fn:startsWith(parentFunctionalGroupForm.dynamicTitleName,'Edit')}">
					<button type="button" class="eto-btn"
						id="showHistoryButton"
						onclick="javascript:showParentFGAuditHistory('${ parentFunctionalGroupForm.parentFunctionalGroup.name!=null ? parentFunctionalGroupForm.parentFunctionalGroup.name : 'null'}');">
						<fmt:message key="button.showHistory" />
					</button>
				</c:if>
				<button type="button" class="eto-btn"
					id="cancelButton" onclick="javascript:goCancel()">
					<fmt:message key="button.cancel" />
				</button>
				<c:if test="${!parentFunctionalGroupForm.isTAMRedirect}">
					<button type="button" class="eto-btn"
						id="backButton"
						onclick="javascript:goBack('${fn:escapeXml(parentFunctionalGroupForm.backAction)}');">
						<fmt:message key="button.back" />
					</button>
				</c:if>
			</div>
		</div>
		</div>
		<div class="eto-modal" id="pfg_modal">
		<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
			<header class="eto-modal__header">
				<span id="popup_modal_header"></span>
				<button class="eto-modal__close" data-modal-close></button>
			</header>
			<section class="eto-modal__body">
				<p style="white-space: normal;" id="popup_modal_body"></p>
			</section>
			<footer class="eto-modal__footer" id="popup_modal_footer">
			</footer>
		</div>
	</div>
	</e2o:form>
	<%@ include file="../fullModal.jspf"%>
	<c:if
		test="${parentFunctionalGroupForm.dynamicTitleName == 'Assign to Group'}">
		<script type="text/javascript">
			$(document).ready(
					function() {
						$("#parentFunctionalGroupName").autocomplete(
								"ajaxQueryParentFunctionalGroupName.do", {
									delay : 500,
									minChars : 1,
									matchSubset : 1,
									maxItemsToShow : 20,
									matchContains : 0,
									cacheLength : 20,
									autoFill : true,
									selectedCurrent : true,
									selectCallBack : callOnParentFunctionalGroupNameSelect,
									extraParams : {
										activeOnly : 'yes',
										businessType : '1'
									}
								});
					});


					function handleFilterFieldChanged(field){
                    	if(field.name==='dateValue(actionDateGE)'){
                    		if(field.value!=='' && field.value!=null){
                    			$('select[name="value(years)"]').prop('disabled',true);
                    		}
                    	}

                    	if(field.name==='dateValue(actionDateLT)'){
                    		if(field.value!=='' && field.value!=null){
                    			$('select[name="value(years)"]').prop('disabled',true);
                    		}
                    	}

                    	if(field.name==='value(years)'){
                            if(field.value!=='' && field.value!=null){
                                $('input[name="dateValue(actionDateGE)"]').prop('disabled',true);
                                $('input[name="dateValue(actionDateLT)"]').prop('disabled',true);
                                $('.md-icon').css('pointer-events','none');
                            }
                            else{
                                $('input[name="dateValue(actionDateGE)"]').prop('disabled',false);
                                $('input[name="dateValue(actionDateLT)"]').prop('disabled',false);
                                $('.md-icon').css('pointer-events','');
                            }
                        }
                    }

                    function clearField(field)
                    {
                       if (field.tagName == 'SELECT')
                       {
                          if (field.multiple == true)
                          {
                             for (i = 0; i < field.options.length; i++)
                             {
                                field.options[i].selected = false;
                             }
                             field.value='';
                             field.selectedIndex = -1;
                          }
                          else
                          {
                            var options = field.options;
                            if(options.length == 1) {
                            	field.selectedIndex = 0;
                            	field.value = options[0].value;
                            } else {
                            	field.selectedIndex=0;
                                //field.value='';
                            }
                          }
                       }
                       else
                       {
                          field.value='';
                          checkIfFilterIsEmpty(field);
                       }
                    }

                    function checkIfFilterIsEmpty(field){
                    	if($('input[name="dateValue(actionDateGE)"]').val()===''&& $('input[name="dateValue(actionDateLT)"]').val()===''){
                    	    $('select[name="value(years)"]').prop('disabled',false);
                    	}
                    }
		</script>
	</c:if>
</body>
</html>