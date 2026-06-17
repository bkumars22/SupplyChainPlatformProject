<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Rebate-Detail" />
<title>Rebate Program</title>
<script type="text/javascript">

var tabsObj = null;
var textObj = null;

function goBack(action)
{
	var goBackCallback = function() {
		document.forms[0].action=action;
	    document.forms[0].preserveSearchValues.value="true";
	    document.forms[0].submit();
	    showWaitBusy();
	};
	if (canLeavePage(goBackCallback))
    {
		goBackCallback(action);
    }
}

function activateRemove(removeId)
{
		$('#'+removeId).removeAttr("disabled");
		$('#'+removeId).removeClass().addClass('eto-btn eto-btn--primary');
}

function goBack()
{
	var goBackCallback = function() {
		// document.forms[0].action=action;
		document.forms[0].action = "submitRebateProgramSearch.do";
	    document.forms[0].preserveSearchValues.value="true";
	    document.forms[0].submit();
	    showWaitBusy();
	};
	if (canLeavePage(goBackCallback))
    {
		/* goBackCallback(action); */
		goBackCallback();
    }
}

function handleDataChanged()
{
	document.forms[0].unsavedData.value = 'true';
	var msgArea = document.getElementById('unsavedDataMsg');
	if (msgArea != null)
	{
		msgArea.innerText = '<fmt:message key="info.unsaved_data"/>';
	}
	$('#updateRule').removeAttr("disabled");
}

function showMore(tdId)
{
$('#'+tdId).addClass("eto-grid-expand--expanded");
}

function checkValidity()
{
	var toDate = this.getValue();
}

function canLeavePage(callback)
{
	if (document.forms[0].unsavedData.value == 'true')
	{
		showYesNoMessageBox('YES NO','WARN',
			   "<fmt:message key='warn.changes_not_saved_yes_no'/>",
			   "<fmt:message key='msg.warn'/>", callback);
		return false;
	}
	return true;
}

function validateSelectedPricingLength(){
	var checkboxChecked = false;
	var checkb = document.getElementsByName("selectedItemAmountKeys");
	for(var i = 0 ; i < checkb.length ; i++){
		if(checkb[i].checked){
			checkboxChecked = true;
			break;
		}
	}
	if(!checkboxChecked){
		showOkMessageBox('OK','WARN',"<fmt:message key='warn.rebate.no_pricing_selected_to_delete'/>","<fmt:message key='msg.warn'/>",function() {});
         return false;
		}
	return true;
}

function validateData(ifValidatedCallback)
{
    if (isFieldEmpty('rebateName'))
    {
         showOkMessageBox('OK','WARN',"<fmt:message key='errors.rebate.program_name_required'/>","<fmt:message key='msg.warn'/>",function() {
        	 document.forms[0].rebateName.focus();                        
          });
          return;
    }
    
    if(checkfieldNameLength('rebateName'))
    {
     showOkMessageBox('OK','WARN',"<fmt:message key='errors.rebate.program_name_length_exceed'/>","<fmt:message key='msg.warn'/>",function() {
        	 document.forms[0].rebateName.focus();                        
          });
          return;
    } 
    if (isFieldEmpty('programStartDate'))
    {         
         showOkMessageBox('OK','WARN',"<fmt:message key='errors.rebate.program_start_date_missing'/>","<fmt:message key='msg.warn'/>",function() {
        	 document.forms[0].programStartDate.focus();                   
          });
          return;
    }

    if (isFieldEmpty('programEndDate'))
    {
         showOkMessageBox('OK','WARN',"<fmt:message key='errors.rebate.program_end_date_missing'/>","<fmt:message key='msg.warn'/>",function() {
        	 document.forms[0].programEndDate.focus();                   
          });
          return;
    }
    
    if (isFieldEmpty('businessEntityName'))
    {
         showOkMessageBox('OK','WARN',"<fmt:message key='errors.rebate.provider_missing'/>","<fmt:message key='msg.warn'/>",function() {
        	 document.forms[0].businessEntityName.focus();                   
          });
          return;
    }
    
    ifValidatedCallback();
}

function checkCommas(amountField){
	var amount = amountField.value;
	while (amount.search(",") >= 0) {
        amount = (amount + "").replace(',', '');
    }
	amountField.value = amount;
    return amountField;	
}

function checkRebateAmount(amountField)
{
    checkNumericFieldRequired(checkCommas(amountField),${rebateProgramForm.allowNegativeAmount});
}

function goCancel()
{
	if(canLeavePage(goCancelCallback)) {
		goCancelCallback();
	}
}

function goCancelCallback()
{
	document.forms[0].action="home.do";
	document.forms[0].submit();
}

function selectAllRuleData()
{
	if (document.forms[0].ruleItemKeys)
	{
		selectAllOptions(document.forms[0].ruleItemKeys,true);
		selectAllOptions(document.forms[0].rulePlatformKeys,true);
		selectAllOptions(document.forms[0].ruleCategoryKeys,true);
	}	
}


function goSaveAndContinue()
{
	validateData(function() {
		selectAllRuleData();
    	document.forms[0].action="saveAndContinueRebateProgram.do";
		document.forms[0].submit();	
		showWaitBusy();
    });
}

function goSave()
{
	validateData(function() {
		selectAllRuleData();
    	document.forms[0].action="saveRebateProgram.do";
		document.forms[0].submit();	
		showWaitBusy();
    });
}


function goEvent(eventName,promptName, ackMessage)
{	
	validateData(function(){	
	    var yesCallback = function() {
		    if (promptName != null && promptName.length > 0)
		    {
		       var result = showInputBox('OK','QUESTION',
		                 promptName,promptName);
		       if (result == '' || result == undefined)
		       {
		           showMessageBox('OK','ERROR',
		               "errors.field_required",
		               "msg.error");
		           return;
		       }
		       document.forms[0].lineEventMessage.value = result;
		    }
		
		    selectAllRuleData();
		    document.forms[0].lineEvent.value = eventName;
		    document.forms[0].action="processEventRebateProgram.do";
			document.forms[0].submit();
			showWaitBusy();	
		    if (ackMessage != null && ackMessage.length > 0)
		    {
		        showMessageBox('OK','INFO',
		               ackMessage,
		               'msg.info');
		    }
	    };
	    
	    if (eventName == 'Close')
	    {
	        showYesNoMessageBox('YES NO','WARN',
	 			   "<fmt:message key='warn.rebate.close_rebate'/>",
	 			   "<fmt:message key='msg.warn'/>", yesCallback);
	     } else {
	    	 yesCallback();
	     }
	});
}


function goSelectRule(ruleId)
{
	selectAllRuleData();
	document.forms[0].selectedRuleId.value = ruleId;
    document.forms[0].action="selectRebateRule.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
}

function goRemoveRule(ruleId)
{
	selectAllRuleData();
	document.forms[0].deleteRuleId.value = ruleId;
    document.forms[0].action="removeRebateRule.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
}

function goUpdateRule(ruleId)
{
	selectAllRuleData();
	/* if($('#useBomForItems').attr('checked')){
		document.forms[0].useBomValueOriginal.value = "true";
	}
	else{
		document.forms[0].useBomValueOriginal.value = "false";
	} */
    document.forms[0].action="updateRebateRule.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
}

function goAddAmount()
{
	selectAllRuleData();
    document.forms[0].action="addRebateAmount.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
}

function goDeleteAmount()
{
	selectAllRuleData();
	if(validateSelectedPricingLength()){
		document.forms[0].action="removeRebateAmount.do";
		document.forms[0].submit();	
		showWaitBusy();
	}
}

function goCopyAmount()
{
	selectAllRuleData();
    document.forms[0].action="copyRebateAmount.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
}

function goAddItems()
{
    document.forms[0].action="gotoSearchItemsRebateProgram.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
		
}

function goAddRule()
{
	selectAllRuleData();
    document.forms[0].action = "addRebateRule.do";
	document.forms[0].submit();
	showWaitBusy();
	// setTimeout(myFunction, 5000);
	
	/* $.ajax({
		type: "POST",
		url: 'addRebateRule.do',
		cache: false,
		dataType: 'text/plain',
		success: function(result) {
			console.log('Entered into success function');
			closeWaitBusy();
			document.getElementById('contentFrame').contentWindow.location.reload(true);
			// $('iframe').attr('src', $('iframe').attr('src'));
		},
		error: function(error){
			closeWaitBusy();
			alert('error'+error.statusText);
		}
	}); */
}

/* function myFunction(){
	closeWaitBusy();
} */

function goCopyAmount()
{
	selectAllRuleData();
    document.forms[0].action="copyRebateAmount.do";
	document.forms[0].submit();	
	showWaitBusy();	   			
}


function goRunProgram()
{
	/* if(canLeavePage(goRunProgramCallback)){
		goRunProgramCallback();
	} */
	goRunProgramCallback();
}

function goRunProgramCallback()
{
	selectAllRuleData();
   	document.forms[0].action="runRebateProgram.do";
	document.forms[0].submit();		
	showWaitBusy();
}
	
/* function goChangeTab(tabId)
{
    if (tabId == 'PRICING')
	{
        document.getElementById('RULES').style.display='none';    
        document.getElementById('RESULTS').style.display='none';        
        document.getElementById('PRICING').style.display='inline';

	}
    else if (tabId == 'RULES')
	{
        document.getElementById('PRICING').style.display='none';
        document.getElementById('RESULTS').style.display='none';        
        document.getElementById('RULES').style.display='inline';
	}
    else if (tabId == 'RESULTS')
	{
        document.getElementById('PRICING').style.display='none';
        document.getElementById('RULES').style.display='none';        
        document.getElementById('RESULTS').style.display='inline';
	}	
	document.forms[0].selectedPage.value=tabId;
} */

function setActiveTab(tabId)
{
	document.forms[0].selectedPage.value = tabId;
	if(tabId == "RESULTSTAB"){
		goRunProgram();
	}
}

function resizeArea()
{
	var pad = 120;
	var area = $("#rpDetails");
	if (area != null)
	{
		var h = $("#rpHeader").height() + pad;
		var b = $(document.body).height();
		area.height(b - h);
	}
}

function onBusinessSelected(finderValues)
{
	if(finderValues.length > 0)
	{
		document.getElementById('businessEntityName').value = finderValues[0][1];
		handleDataChanged();
	}
}

function platformSelected(li)
{
	// first extra is the key 
	onPlatformCallback(new Array(new Array(li.extra[0],li.selectValue,li.extra[1])));
}

function platformFoundValue(li)
{
	if (li != null)
	{
		platformSelected(li);
	}
	$("#rulePlatform").removeClass('ac_loading');
}

function onPlatformCallback(finderValues)
{
	var items = document.getElementById('rulePlatforms');
	for(var i=0; i < finderValues.length; i++)
	{
		if (findOption(items.options,finderValues[i][0]) == -1)
		{
			var text = finderValues[i][1]+" ("+finderValues[i][2]+")";
			addOption(items,text,finderValues[i][0]);
		}
	}
	//
	//sortSelect(selElem);
	sortPlatform()
	handleDataChanged();
}

function sortPlatform() {
	var selElem = document.getElementById('rulePlatforms');
    var tmpAry = new Array();
    for (var i=0;i<selElem.options.length;i++) {
        tmpAry[i] = new Array();
        tmpAry[i][0] = selElem.options[i].label;
        tmpAry[i][1] = selElem.options[i].value;
    }
    tmpAry.sort();
    while (selElem.options.length > 0) {
        selElem.options[0] = null;
    }
    for (var i=0;i<tmpAry.length;i++) {
        var op = new Option(tmpAry[i][0], tmpAry[i][1]);
        selElem.options[i] = op;
    }
    return;
}
function sortItem() {
	var selElem = document.getElementById('ruleItems');
    var tmpAry = new Array();
    for (var i=0;i<selElem.options.length;i++) {
        tmpAry[i] = new Array();
        tmpAry[i][0] = selElem.options[i].label;
        tmpAry[i][1] = selElem.options[i].value;
    }
    tmpAry.sort();
    while (selElem.options.length > 0) {
        selElem.options[0] = null;
    }
    for (var i=0;i<tmpAry.length;i++) {
        var op = new Option(tmpAry[i][0], tmpAry[i][1]);
        selElem.options[i] = op;
    }
    return;
}
function sortCategory() {
	var selElem = document.getElementById('ruleCategories');
    var tmpAry = new Array();
    for (var i=0;i<selElem.options.length;i++) {
        tmpAry[i] = new Array();
        tmpAry[i][0] = selElem.options[i].label;
        tmpAry[i][1] = selElem.options[i].value;
    }
    tmpAry.sort();
    while (selElem.options.length > 0) {
        selElem.options[0] = null;
    }
    for (var i=0;i<tmpAry.length;i++) {
        var op = new Option(tmpAry[i][0], tmpAry[i][1]);
        selElem.options[i] = op;
    }
    return;
}

function categorySelected(li)
{	
	// first extra is the key 
	onCategoryCallback(new Array(new Array(li.extra[0],li.selectValue)));
}

function categoryFoundValue(li)
{
	if (li != null)
	{
		categorySelected(li);
	}
	$("#ruleCategory").removeClass('ac_loading');
}

function onCategoryCallback(finderValues)
{
	var items = document.getElementById('ruleCategories');
	for(var i=0; i < finderValues.length; i++)
	{
		if (findOption(items.options,finderValues[i][0]) == -1)
		{
			addOption(items,finderValues[i][1],finderValues[i][0]);
		}
	}
	sortCategory();
	handleDataChanged();
}

<%-- Build the array so it matchs what would be returned from the popup finder.  NOTE that description
   is not available --%>
function itemSelected(li)
{
	onItemCallback(new Array(new Array(li.extra[0],li.selectValue,li.extra[2],li.extra[1])));
}

function itemFoundValue(li)
{
	if (li != null)
	{
		itemSelected(li);
	}
	$("#ruleItem").removeClass('ac_loading');
}

function abbreviate(text, limit) {
	if (limit < 4 || text.length <= limit) {
		return text;
	}
	return text.substring(0, limit-3) + "...";
}

function buildOptionText(finderValuesRow)
{
	var desc = trimWhitespace(finderValuesRow[2]);
	var itemInfo = finderValuesRow[1] + ' - ' + finderValuesRow[3];
	var maxDescLength = (52 - itemInfo.length);
	if (maxDescLength >= 4) {
	    desc = abbreviate(desc, maxDescLength);
	}  else {
		desc = '...';
	}
	var text = itemInfo + ' (' + desc + ')';
    return text;	
}

function onItemCallback(finderValues)
{
	var items = document.getElementById('ruleItems');
	for(var i=0; i < finderValues.length; i++)
	{
		if (findOption(items.options,finderValues[i][0]) == -1)
		{
			var optionText = buildOptionText(finderValues[i]);
			addOption(items, optionText, finderValues[i][0]);
		}
	}
	sortItem();
	handleDataChanged();
}

function formatItemData(row, i, num)
{
	return row[0] + ' ' + row[2];
}

function onAmountItemCallback(idx, finderValues)
{
	if(finderValues.length > 0)
	{
		var item = document.getElementById('itemNumber'+idx);
		item.value = finderValues[0][1];
	}
}


function checkForClearedItem(data,keyField)
{
   if (isValueEmpty(data))
   {
      keyField.value = '';
   }
}

function clearItem(rowId)
{
	document.getElementById('itemKey'+rowId).value = '';
	document.getElementById('itemNumber'+rowId).value = '';	
}

function addEntered(field)
{
	$(field)[0].autocompleter.flushCache();
	$(field).addClass('ac_loading');
	$(field)[0].autocompleter.findValue();
 
}


function removeSelected(selectObj)
{	
	removeSelectedOptions(selectObj);
	handleDataChanged();
}

function removeSelectedOptions(sellist)
{
   if (sellist.options == null)
   {
      return;
   }
   var len = sellist.options.length;
   while (len > 0)
   {
       if (sellist.options[len-1].selected)
       {
           sellist.options.remove(len-1);
       }
       len--;
   }
}

function handlePushDownValue(startIndex, fieldId)
{
	if (pushValueDownIndexed(startIndex,fieldId))
	{
		handleDataChanged();
	}
}

function showLineMessage(line,message)
{	
	var title = "<fmt:message key="msg.info"/>";
	showModalMessageBox(title,message);
}

function enableArea(areaId,enabled)
{
	if (enabled)
	{
		$("#"+areaId+" :input").removeAttr("readonly");	
		$("#"+areaId+" :input").removeAttr("disabled");
		$("#"+areaId+" img").removeAttr("disabled");
	}
	else
	{		
		$("#"+areaId+" :input").attr("readonly",true);	
		$("#"+areaId+" :input").attr("disabled",true);
		$("#"+areaId+" img").attr("disabled",true);
	}
	$('#status').attr('disabled', true);
	enableforAssignment(areaId);
}
function enableforAssignment(areaId){
    if("${rebateProgramForm.rebateProgram.status}" == "APPROVED"){
        $("#"+areaId+" :input").attr("readonly",true);
        $("#"+areaId+" :input").attr("disabled",true);
        $("#"+areaId+" img").attr("disabled",true);
    }
    <c:if test="${e2ofn:hasAccess(appContext, 'REBATE', 'AssignOwner')}">
        $("#financeOwner").removeAttr("readonly");   
        $("#financeOwner").removeAttr("disabled");
        $("#programOwner").removeAttr("readonly");   
        $("#programOwner").removeAttr("disabled");
    </c:if>
}

$(document).ready(function() 
	{	
	if($('#saDetailTable-pricingRecord').length){
		if($('#priceTabInfo').length){
		$('#priceTabInfo').remove();
		}
	  }
	else{
		var markup = "<tr id='priceTabInfo'><td colspan='6' style='background-color: #f1f6f7;'><fmt:message key='rp.item.detail'/></tr>";
		$('#saDetailTable tbody').append(markup);			
	}
 	enableArea("rebateProgramInfo",${e2ofn:allowOperation('Rebate',rebateProgramForm.rebateProgram.status,'EditProgram')});
		$("#financeOwner").autocomplete(
			"ajaxQueryUserId.do",
			{
				delay:500,
				minChars:1,
				matchSubset:1,
				maxItemsToShow:20,						
				matchContains:0,
				cacheLength:20,
				autoFill:true,
				selectedCurrent:true,
				extraParams:{roleId:'FINANCE'}
			}
		);
		$("#programOwner").autocomplete(
				"ajaxQueryUserId.do",
				{
					delay:500,
					minChars:1,
					matchSubset:1,
					maxItemsToShow:20,						
					matchContains:0,
					cacheLength:20,
					autoFill:true,
					selectedCurrent:true,
					extraParams:{activeOnly:'yes',businessType:'1'}
				}
			);
		$("#ruleItem").autocomplete(
				"ajaxQueryItemNumberDescriptionBusinessAndKey.do",
				{
					delay:500,
					minChars:1,
					matchSubset:1,
					maxItemsToShow:20,						
					matchContains:0,
					cacheLength:20,
					autoFill:true,
					selectedCurrent:true,
					onItemSelect: itemSelected,
					onFindValue:itemFoundValue,
					formatItem:formatItemData
				}
			);		
		$("#ruleCategory").autocomplete(
				"ajaxQueryItemCategory.do",
				{
					delay:500,
					minChars:1,
					matchSubset:1,
					maxItemsToShow:20,						
					matchContains:0,
					cacheLength:20,
					autoFill:true,
					selectedCurrent:true,
					onItemSelect: categorySelected,
					onFindValue:categoryFoundValue
				}
			);
		$("#rulePlatform").autocomplete(
				"ajaxQueryPlatform.do",
				{
					delay:500,
					minChars:1,
					matchSubset:1,
					maxItemsToShow:20,						
					matchContains:0,
					cacheLength:20,
					autoFill:true,
					selectedCurrent:true,
					onItemSelect: platformSelected,
					onFindValue:platformFoundValue
				}
			);
	});   
 
</script>
<style type="text/css">
::-webkit-scrollbar-button {
	background-color: none !important;
}

table#saDetailTable {
	border-bottom: 1px solid #eeeeee;
}

div#rebateRules-div {
	padding-top: 20px;
	padding-bottom: 10px;
}

/* #rpDetails {
	margin-left: 50px;
	padding-top: 15px;
} */
.eto-tab-content__item {
	padding-bottom: 15px;
	padding-top: 15px;
}

#pricing-grid, div#rebateRules-grid {
	padding-top: 15px;
}

table#rebateRules-details {
	margin-bottom: 20px;
	width: auto !important;
	border-bottom: 1px solid #eeeeee;
}

button#deleteRule {
	background: transparent;
	border: none;
}

#rebateRules td {
	padding: 0px 0px 10px 5px;
}

i.md-icon {
	color: #468293;
}
/* .eto-grid td{
	border-width: 0px !important;
} */
</style>
</head>
<body>
	<%@ include file="../fullModal.jspf"%>
	<c:set var="readOnly"
		value="${!e2ofn:hasAccess(appContext, 'REBATE', 'Save') || !e2ofn:allowOperation('Rebate',rebateProgramForm.rebateProgram.status,'EditDetail')}" />
	<fmt:message var="rpDetailTitle" key="rp.detail.title" />
	<fmt:message var="clearTitle" key="button.title.clear" />
	<fmt:message var="pushdownTitle" key="button.title.pushdown" />
	<fmt:message var="moreInfoTitle" key="info.more_information" />
	<fmt:message var="resize" key="search.result.resize.title" />
	<e2o:form action="/saveAndContinueRebateProgram">
		<html:hidden property="unsavedData" />
		<html:hidden property="selectedPage" />
		<html:hidden property="lineEvent" />
		<html:hidden property="lineEventMessage" />
		<input type="hidden" name="preserveSearchValues" />
		<html:hidden property="backAction" />
		<html:hidden property="selectedRuleId" />
		<html:hidden property="deleteRuleId" />

		<div class="container">
			<div class="row margin-sm-2 margin-left-sm-1">
				<div style="display: flex;">
					<h2 id="rpHeader" class="margin-left-sm-2">
						<fmt:message key="rp.header.title" />
						&nbsp;
					</h2>
					<button class="eto-icon-btn" title="Info"
						data-modal="#new-rebate-modal">
						<i class="md-icon">info</i>
					</button>
				</div>
				<c:if
					test="${!empty rebateProgramForm.rebateProgram.rebateProgramKey}">
					<div style="margin-left: auto;">
						<%-- <e2i2:button id="historyButton"
						onclick="javascript:showAuditHistory(${rebateProgramForm.rebateProgram.rebateProgramKey} ,'PcmRebateProgram')">
						<fmt:message key="button.history" />
					</e2i2:button> --%>

						<!-- data-tooltip="#audit-history-tooltip" -->
						<i class="md-icon" style="cursor: pointer; color: #468293;"
							onclick="javascript:showAuditHistory(${rebateProgramForm.rebateProgram.rebateProgramKey} ,'PcmRebateProgram');"
							title="History">history</i>
					</div>
				</c:if>
			</div>
			<div class="eto-modal" id="new-rebate-modal" style="width: 100%;">
				<div class="eto-modal__content">
					<header class="eto-modal__header">
						<span>Rule Info</span>
						<button class="eto-modal__close" data-modal-close></button>
					</header>
					<section class="eto-modal__body">

						<h3>Item Rule</h3>
						<hr>
						<p>
							Select the item numbers your rebate is associated with when you
							create an Item Rule. The Item numbers you select must be item
							numbers received at the factory. Finance multiplies the unit
							receipts of the item number in list again the rebate value, to
							collect the rebate amount from suppliers. <br> Qty Received
							x Rebate Value - $$$
						</p>
						<h3>Platform & Commodity</h3>
						<hr>
						<p>
							Select both the platform name and the commodity your rebate is
							associated with. Finance multiplies the unit shipments of the
							platform-commodity combination against the rebate value, to
							collect the rebate amount from suppliers <br> Qty Shipped x
							Rebate Value = $$$
						</p>

						<h3>BOM Rule</h3>
						<hr>
						<p>
							Application searches all assembly BOMs in the system and looks
							for the rebate item number you selected in item list. It then
							calculates the extended rebate value for the assembly (Item list
							Qty per assembly* item list rebate Value= Extended rebate value)
							Finance team multiplies the unit receipt of the assembly item
							number against the extended dollar value of the rebate then
							collects a rebate check from the supplier. <br> Qty Shipped
							x Rebate Value = $$$
						</p>
					</section>
					<footer class="eto-modal__footer">
						<button class="eto-btn" data-modal-close>Close</button>
					</footer>
				</div>
			</div>
			<script type="text/javascript">
						new eto.Modal({ el: document.querySelector('#new-rebate-modal') });
			</script>
					<e2o:errors maxErrors="4" styleId="errors" />
		<logic:messagesPresent message="true">
			<html:messages id="message" message="true">
				<li>${message}</li>
			</html:messages>
		</logic:messagesPresent>
			<div id="rebateProgramInfo">
			<div class="row margin-sm-2" >
				<div class="col-sm-4">
					<div class="eto-input" id="rebateNameInput">
						<label><fmt:message key="rp.name" /></label><span
							class="requiredIndicator">*</span><input class="eto-input__field"
							id="rebateName" type="text" name="rebateName"
							onchange="handleDataChanged()"
							value="${rebateProgramForm.rebateProgram.rebateName}"
							${readOnly == true ? 'disabled' : ''}>
					</div>
					<script type="text/javascript">new eto.TextInput({ el: document.querySelector('#rebateNameInput') });</script>
				</div>
				<div class="col-sm-4">
					<div class="eto-select" id="rebateTypeSelect">
						<label class="eto-select__label"><fmt:message
								key="rp.rebateType" /></label>
						<div class="eto-select__field-container">
							<select class="eto-select__field" name="rebateProgram.rebateType"
								${readOnly == true ? 'disabled' : ''}
								onchange="handleDataChanged()" id="rebateType">
								<c:forEach var="rt" items="${rebateProgramForm.rebateTypes}">
									<option value="${rt}">${rt}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<script type="text/javascript">new eto.SelectInput({ el: document.querySelector('#rebateTypeSelect') });</script>
				</div>
				<div class="col-sm-4">
					<div class="eto-input" id="statusInput">
						<label class="eto-input__label"><fmt:message
								key="rp.status" /></label><input class="eto-input__field" type="text"
							id="status" value="${rebateProgramForm.rebateProgram.status}"
							name="${rebateProgramForm.rebateProgram.status}" disabled>
						<div class="eto-input__message"></div>
					</div>

					<script type="text/javascript">
							 	textObj = new eto.TextInput({ el: document.querySelector('#statusInput') });
						</script>
				</div>
				</div>
				<div class="row margin-sm-2">
				<div class="col-sm-2">
					<fmt:formatDate var="rd"
						value="${rebateProgramForm.rebateProgram.effectiveFromDt}"
						pattern="${appContext.currentDateFormat}" />
					<div class="eto-input" data-message-type=""
						id="programStartDateContainer">
						<label><fmt:message key="rp.startDate" /></label><span
							class="requiredIndicator">*</span>
						<div class="eto-input__field-container">
							<html:text styleClass="eto-input__field"
								styleId="programStartDate" maxlength="20" size="12"
								readonly="true" disabled="${readOnly}" value="${rd}"
								onchange="handleDataChanged()" property="programStartDate" />
							<span class="eto-input__addon"><i class="md-icon"
								onclick="javascript:showCalendar('programStartDate')">event</i>
								<i class="md-icon"
								onclick="javascript:clearField(document.getElementById('programStartDate'))">close</i>
							</span>
						</div>
					</div>
				</div>
				<div class="col-sm-2">
					<fmt:formatDate var="rd"
						value="${rebateProgramForm.rebateProgram.effectiveToDt}"
						pattern="${appContext.currentDateFormat}" />
					<div class="eto-input" data-message-type=""
						id="programEndDateContainer">
						<label><fmt:message key="rp.endDate" /></label><span
							class="requiredIndicator">*</span>
						<div class="eto-input__field-container">
							<html:text styleClass="eto-input__field" styleId="programEndDate"
								maxlength="20" size="12" readonly="true" disabled="${readOnly}"
								value="${rd}" onchange="handleDataChanged()"
								property="programEndDate" />
							<span class="eto-input__addon"><i class="md-icon"
								onclick="javascript:showCalendar('programEndDate')">event</i> <i
								class="md-icon"
								onclick="javascript:clearField(document.getElementById('programEndDate'))">close</i>
							</span>
						</div>
					</div>
					<script>
						new eto.TextInput({el : document.querySelector('#programEndDateContainer')});
						new eto.TextInput({el : document.querySelector('#programStartDateContainer')});
					</script>
				</div>
				<div class="col-sm-4">
					<div class="eto-input" id="programTypeInput">
						<label class="eto-input__label"><fmt:message
								key="rp.programType" /></label> <input class="eto-input__field"
							id="programType" type="text" name="rebateProgram.programType"
							onchange="handleDataChanged()"
							value="${rebateProgramForm.rebateProgram.programType}"
							${readOnly == true ? 'disabled' : ''}>
					</div>
					<script type="text/javascript">new eto.TextInput({ el: document.querySelector('#programTypeInput') });</script>
				</div>
				<div class="col-sm-4">
					<div class="eto-autocomplete" id="autocomplete-financeOwner">
						<label class="eto-autocomplete__label"><fmt:message
								key="rp.financialOwner" /></label> <input
							class="eto-autocomplete__field" type="text" id="financeOwner"
							placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'
							onchange="handleDataChanged()"
							${readOnly == true? 'disabled' : ''}
							value="${rebateProgramForm.rebateProgram.financialProgramOwner}"
							style="display: inline-flex;" name="financialProgramOwner">
						<%-- <e2i2:img src="/action_nav_pad.gif"
								onclick="javascript:document.getElementById('financeOwner').autocompleter.flushCache()" /> --%>
						<div class="eto-autocomplete__message"></div>
						<div class="eto-results">
							<!-- results will render here -->
						</div>
					</div>
					<script type="text/javascript">
									var autoFinanceOwner = new eto.Autocomplete({ el: document.querySelector('#autocomplete-financeOwner') });
									autoFinanceOwner.on('inputChange', function(query) {
												var url = "ajaxQueryUserId.do?q="+query;
			$.ajax({
				url : url,
				success : function(result) {
					var arr;
					if (result.includes("|")) {
						arr = result.split("|");
					} else {
						arr = result.split("\n");
					}
					autoFinanceOwner.setContent(arr);
					autoFinanceOwner.open();
				}
			});

});
</script>
				</div>
				</div>
				<div class="row margin-sm-2">
				<div class="col-sm-4">
					<div class="eto-input" id="businessEntityNameInput">
						<label><fmt:message key="rp.rebateBusiness" /></label><span
							class="requiredIndicator">*</span>
						<html:hidden property="businessEntityKey"
							styleId="businessEntityKey" />
						<div class="eto-input__field-container">
							<input class="eto-input__field" type="text"
								value="${rebateProgramForm.rebateProgram.businessEntity.businessEntityName}"
								${readOnly == true? 'disabled' : ''} name="businessEntityName"
								onchange="handleDataChanged()" id="businessEntityName">
							<c:if test="${!readOnly}">
								<span class="eto-input__addon"><i class="md-icon"
									onclick="javascript:doFinderPopup('SupplierNameFinder',document.forms[0].businessEntityKey,'onBusinessSelected',null,'false')"
									style="cursor: pointer;">search</i></span>
							</c:if>
						</div>
					</div>
					<script>
								var objBusinessEntityName = new eto.TextInput({ el: document.querySelector('#businessEntityNameInput') });
						</script>
				</div>
				<div class="col-sm-4">
					<div class="eto-input" id="confidenceFactorInput">
						<label class="eto-input__label"><fmt:message
								key="rp.confidenceFactor" /></label>
						<fmt:formatNumber var="cf"
							value="${rebateProgramForm.rebateProgram.confidenceFactor}"
							minFractionDigits="0" maxFractionDigits="2" maxIntegerDigits="3" />
						<input class="eto-input__field" id="confidenceFactor" type="text"
							name="confidenceFactor" onchange="handleDataChanged()"
							value="${cf}" ${readOnly == true? 'disabled' : ''}>
					</div>
					<script type="text/javascript">new eto.TextInput({ el: document.querySelector('#confidenceFactorInput') });</script>
				</div>
				<div class="col-sm-4">
					<div class="eto-autocomplete" id="autocomplete-programOwner">
						<label class="eto-autocomplete__label"><fmt:message
								key="rp.programOwner" /></label> <input class="eto-autocomplete__field"
							type="text" id="programOwner" onchange="handleDataChanged()"
							placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'
							${readOnly == true? 'disabled' : ''}
							value="${rebateProgramForm.rebateProgram.programOwner}"
							style="display: inline-flex;" name="programOwner">
						<%-- <e2i2:img src="/action_nav_pad.gif"
								onclick="javascript:document.getElementById('programOwner').autocompleter.flushCache()" /> --%>
						<div class="eto-autocomplete__message"></div>
						<div class="eto-results">
							<!-- results will render here -->
						</div>
					</div>
					<script type="text/javascript">
								var autoProgramOwner = new eto.Autocomplete({ el: document.querySelector('#autocomplete-programOwner') });
								autoProgramOwner.on('inputChange', function(query) {
								var url = "ajaxQueryUserId.do?q="+query;
		$.ajax({
				url : url,
				success : function(result) {
					var arr;
					if (result.includes("|")) {
						arr = result.split("|");
					} else {
						arr = result.split("\n");
					}
					autoProgramOwner.setContent(arr);
					autoProgramOwner.open();
				}
			});

});
</script>
				</div>
				</div>
				<div class="row margin-sm-2">
				<div class="col-sm-4">
					<div class="eto-select" id="paymentTypeSelect">
						<label class="eto-select__label"><fmt:message
								key="rp.paymentType" /></label>
						<div class="eto-select__field-container">
							<c:set var="paymentTypeValue"
								value="${fn:toUpperCase(rebateProgramForm.rebateProgram.paymentType)}" />
							<select class="eto-select__field" name="paymentType"
								${readOnly == true? 'disabled' : ''}
								onchange="handleDataChanged()" id="paymentType">
								<c:forEach var="pt" items="${rebateProgramForm.paymentTypes}">
									<option value="${pt}"
										${pt == paymentTypeValue ? 'selected' : ''}>${pt}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<script type="text/javascript">new eto.SelectInput({ el: document.querySelector('#paymentTypeSelect') });</script>
				</div>
			</div>
		</div>
		<!-- </div> -->
		</div>
		<div class="container" style="margin-bottom: 150px;">
			<div class="row" style="margin: 3rem;">
				<div class="eto-card eto-expand eto-expand--expanded"
					id="rebatesCardDetails" style="width: 100%;">
					<!-- style="padding: 2rem !important;" -->
					<div class="eto-card__header eto-expand__toggle">
						<span>Rebate Details</span> 
					</div>
					<div class="eto-card__body eto-expand__content">
						<div id="rpDetails">
							<c:set var="lineMessages"
								value="${rebateProgramForm.rebateLineErrors}" />
							<nav class="eto-tabs" id="allRebateTabs">
								<div class="eto-tabs__container">
									<div class="eto-tabs__scroll">
										<a
											class="${rebateProgramForm.selectedPage == 'PRICINGTAB' ? 'eto-tabs__tab eto-tabs__tab--active' : 'eto-tabs__tab'}"
											id="PRICINGTAB" data-tab="#PRICING" tabindex="0"><span
											class="eto-tabs__tab-content"><fmt:message
													key="rp.tab.pricing" /></span><span class="eto-tabs__tab-close"></span></a>
										<a
											class="${rebateProgramForm.selectedPage == 'RULESTAB' ? 'eto-tabs__tab eto-tabs__tab--active' : 'eto-tabs__tab'}"
											id="RULESTAB" data-tab="#RULES" tabindex="1"><span
											class="eto-tabs__tab-content"><fmt:message
													key="rp.tab.rules" /></span><span class="eto-tabs__tab-close"></span></a>
										<c:if
											test="${! empty rebateProgramForm.rebateProgram.rebateProgramKey}">
											<a
												class="${rebateProgramForm.selectedPage == 'RESULTSTAB' ? 'eto-tabs__tab eto-tabs__tab--active' : 'eto-tabs__tab'}"
												id="RESULTSTAB" data-tab="#RESULTS" tabindex="2"><span
												class="eto-tabs__tab-content"><fmt:message
														key="rp.tab.results" /></span><span class="eto-tabs__tab-close"></span></a>
										</c:if>
									</div>
								</div>
								<div class="eto-tabs__btns">
									<a class="eto-tabs__btn eto-tabs__btn--backward"></a> <a
										class="eto-tabs__btn eto-tabs__btn--forward"></a>
								</div>
							</nav>

							<div class="eto-tab-content" style="margin-bottom: 200px">

								<section class="eto-tab-content__item" id="PRICING">
									<c:if test="${e2ofn:hasAccess(appContext, 'REBATE' , 'Save')}">
										<button class="eto-btn" type="button"
											id="newButton" ${readOnly ? 'disabled' : ''}
											onclick="javascript:goAddAmount();">
											<fmt:message key="button.add" />
										</button>
										<button class="eto-btn" type="button" id="deleteButton"
											style="margin-left: 1rem;" ${readOnly ? 'disabled' : ''}
											onclick="javascript:goDeleteAmount();">
											<fmt:message key="button.delete" />
										</button>
									</c:if>
									<c:set var="restrictItem"
										value="${rebateProgramForm.restrictRebateItemForPricing}" />
									<%-- <c:choose>
						<c:when test="${empty rebateProgramForm.rebateItemList}">
							<div style="display: flex;">
								<div class="eto-messageblock" data-message-type="info"
									id="message-block-no-results-pricing"
									style="margin: auto; width: 100%; margin-top: 5%;">
									<div class="eto-messageblock__body">No records found to
										display</div>
									<!-- <a href="javascript:void(0)" role="button"
										class="eto-messageblock__close"></a> -->
								</div>
								<script>
										new eto.MessageBlock({ el : document.querySelector('#message-block-no-results-pricing') });
								</script>
							</div>
						</c:when>
						<c:otherwise> --%>
									<div class="eto-grid" id="pricing-grid"
										style="border-bottom: none;">
										<table id="saDetailTable">
											<colgroup>
												<col/>
												<col/>
												<col/>
												<col/>
											</colgroup>
											<thead>
												<tr>
													<th><label class="eto-checkbox"><input
															class="eto-checkbox__field eto-all-rows-indicator"
															type="checkbox" ${readOnly ? 'disabled' : ''}> <span
															class="eto-checkbox__box"></span> </label></th>
													<th><fmt:message key="rp.period.status" /></th>
													<th colspan="3"><fmt:message key="rp.rebateValidity" /></th>
													<th><fmt:message key="rp.item.rebateAmount" /></th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="ri"
													items="${rebateProgramForm.rebateItemList}"
													varStatus="bidx">
													<tr id="saDetailTable-pricingRecord">
														<td><label class="eto-checkbox"><input
																class="eto-checkbox__field eto-row-indicator"
																type="checkbox" name="selectedItemAmountKeys" value="${bidx.index}"
																Id="raTable_rowselector" ${readOnly ? 'disabled' : ''}><span
																class="eto-checkbox__box"></span> </label> <c:set
																var="lineErrorExists" value="" /> <html:messages
																id="lineMessage" name="lineMessages"
																property="itemAmount[${bidx.index}]">
																<e2i2:img src="/missing_price.gif"
																	alt="${moreInfoTitle}"
																	onclick="javascript:showLineMessage(this,'${fn:escapeXml(lineMessage)}')" />
															</html:messages> <c:if test="${!empty lineErrorExists}">
																<e2i2:img src="/collab_problem.gif"
																	alt="${moreInfoTitle}"
																	onclick="javascript:showLineMessage(this,'${lineErrorExists}')" />
															</c:if> <html:hidden styleId="itemKey${bidx.index}"
																property="itemKey[${bidx.index}]"
																value="${ri.item.itemKey}" /></td>
														<%-- <c:choose>
													<c:when test="${restrictItem}">
														<td><c:out value="${ri.item.itemNumber}" /></td>
													</c:when>
													<c:otherwise>
														<td><input type="text" id="itemNumber${bidx.index}"
															class="inputField"
															onchange="checkForClearedItem(this.value, itemKey${bidx.index}); handleDataChanged()"
															${readOnly ? 'disabled' : ''} size="20"
															tabindex="${bidx.index}" value="${ri.item.itemNumber}" />
															<e2i2:img disabled="${readOnly ? 'yes':'no'}"
																src="/search.gif"
																onclick="javascript:doFinderPopup('EnterpriseItemFinder','itemKey${bidx.index}','onAmountItemCallback(${bidx.index},%7B0%7D)',null,'false')" />
															<e2i2:img disabled="${readOnly ? 'yes':'no'}"
																src="/clear.gif"
																onclick="javascript:clearItem(${bidx.index})" /></td>
													</c:otherwise>
												</c:choose> --%>
														<fmt:formatDate var="sd" value="${ri.effectiveToDt}"
															pattern="${appContext.currentDateFormat}" />
														<td id="status${bidx.index}">
														<script
																type="text/javascript">
														
var arrDate = '${sd}'.split("-");
var currentDate = new Date();
var effEnddt = new Date(arrDate[2], arrDate[0] - 1, arrDate[1]);
if(currentDate > effEnddt)
{
	document.getElementById('status${bidx.index}').innerText ="Expired";
}
else
{
    document.getElementById('status${bidx.index}').innerText ="Active";
}
</script></td>
														<html:messages id="lineMessage1" name="lineMessages"
															property="itemFromDate[${bidx.index}]">
															<c:set var="lineErrorExists"
																value="${lineErrorExists}<li>${fn:escapeXml(lineMessage)}</li>" />
														</html:messages>
														<td class="eto-grid-edit-cell"
															<c:if test="${readOnly}">style="border-bottom: 0px;"</c:if>
															id="itemFromDate${bidx.index}-td"
															<c:if test="${not empty lineMessage1 }"> data-tooltip='#itemFromDate${bidx.index}-tooltip' aria-describedby= '#itemFromDate${bidx.index}-tooltip' data-message-type='error'</c:if>>

															<fmt:formatDate var="effFromdate"
																value="${ri.effectiveFromDt}"
																pattern="${appContext.currentDateFormat}" /> <!-- -->

															<div class="eto-input" data-message-type=""
																id="itemFromDateContainer${bidx.index}">
																<div class="eto-input__field-container">
																	<html:text styleClass="eto-input__field"
																		styleId="itemFromDate${bidx.index}" maxlength="20"
																		size="14" readonly="true" errorKey="rebateLineErrors"
																		disabled="${readOnly}" value="${effFromdate}"
																		onchange="checkValidity()"
																		property="itemFromDate[${bidx.index}]" />
																	<span class="eto-input__addon"><i
																		class="md-icon"
																		onclick="javascript:showCalendar('itemFromDate${bidx.index}')">event</i>
																		<script>
																		if(currentDate <= effEnddt)
																		{
																			document.write('<i class="md-icon" onclick="javascript:clearField(document.getElementById(\'itemFromDate${bidx.index}\'))">close</i>');
																		}
																		</script>
									
																		</span>
																</div>
															</div>
															<div class="eto-tooltip" data-message-type="error"
																id="itemFromDate${bidx.index}-tooltip">
																<div class="eto-tooltip__content">${lineMessage1}</div>
																<span class="eto-tooltip__caret"></span>
															</div> 
															<script type="text/javascript">
																new eto.TextInput({el : document.querySelector('#itemFromDateContainer${bidx.index}')});
															</script>
														</td>
														<td><label style="padding: 5px;"> To </label></td>
														<html:messages id="lineMessage2" name="lineMessages"
															property="itemToDate[${bidx.index}]">
														</html:messages>
														<td class="eto-grid-edit-cell"
															<c:if test="${readOnly}">style="border-bottom: 0px;"</c:if>
															id="itemToDate${bidx.index}-td"
															<c:if test="${not empty lineMessage2 }"> data-tooltip='#itemToDate${bidx.index}-tooltip' aria-describedby= '#itemToDate${bidx.index}-tooltip' data-message-type='error'</c:if>>
															<fmt:formatDate var="effTodate"	value="${ri.effectiveToDt}"
																pattern="${appContext.currentDateFormat}" />
															<div class="eto-input" data-message-type=""
																id="itemToDateContainer${bidx.index}">
																<div class="eto-input__field-container">
																	<html:text styleClass="eto-input__field"
																		styleId="itemToDate${bidx.index}" maxlength="20"
																		size="14" readonly="true" errorKey="rebateLineErrors"
																		disabled="${readOnly}" value="${effTodate}"
																		onchange="handleDataChanged()"
																		property="itemToDate[${bidx.index}]" />
																	<span class="eto-input__addon"><i
																		class="md-icon"
																		onclick="javascript:showCalendar('itemToDate${bidx.index}')">event</i>
																		<script>
																		if(currentDate <= effEnddt)
																		{
																			document.write('<i class="md-icon" onclick="javascript:clearField(document.getElementById(\'itemToDate${bidx.index}\'))">close</i>');
																		}
																		</script>
																		</span>
																</div>
															</div>
															<div class="eto-tooltip" data-message-type="error"
																id="itemToDate${bidx.index}-tooltip">
																<div class="eto-tooltip__content">${lineMessage2}</div>
																<span class="eto-tooltip__caret"></span>
															</div> 
															<script type="text/javascript">
																new eto.TextInput({el : document.querySelector('#itemToDateContainer${bidx.index}')});
															</script>
														</td>
														<td class="eto-grid-edit-cell"
															<c:if test="${readOnly}">style="border-bottom: 0px;"</c:if>><html:messages
																id="itemAmountError"
																property="itemAmount[${bidx.index}]"
																name="rebateLineErrors" /> <input type="text"
															Id="itemAmount${bidx.index}"
															onchange="handleDataChanged()"
															${readOnly ? "disabled":''} size="10" maxlength="10"
															tabindex="${bidx.index}" onblur="checkRebateAmount(this)"
															value="${ri.rebateAmount}" title="${itemAmountError}"
															name="itemAmount[${bidx.index}]" /></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
									<script type="text/javascript">
								new eto.Grid({ el: document.querySelector('#pricing-grid') });
							</script>
									<%-- </c:otherwise>
					</c:choose> --%>
								</section>

								<section class="eto-tab-content__item" id="RULES">
									<c:if test="${e2ofn:hasAccess(appContext, 'REBATE', 'Save')}">
										<button class="eto-btn" style="border-style: dashed;"
											type="button" ${readOnly ? 'disabled' : ''} id="newRule"
											onclick="javascript:goAddRule();">
											<i class="md-icon">add</i>
											<fmt:message key="button.add.rule" />
										</button>
									</c:if>
									<c:choose>
										<c:when
											test="${empty rebateProgramForm.rebateProgram.rebateRules}">
											<div style="display: flex;">
												<div class="eto-messageblock" data-message-type="info"
													id="message-block-no-results-rules"
													style="margin: auto; width: 100%; margin-top: 5%;">
													<div class="eto-messageblock__body">No rebate rules
														records are found to display, add a new rule</div>
													<!-- <a href="javascript:void(0)" role="button"
										class="eto-messageblock__close"></a> -->
												</div>
												<script>
										new eto.MessageBlock({ el : document.querySelector('#message-block-no-results-rules') });
								</script>
											</div>
										</c:when>
										<c:otherwise>
											<div class="eto-grid" id="rebateRules-grid"
												style="border-bottom: none; overflow-x: auto; margin-top: 1%">
												<table id="rebateRules-details" style="width: 100%;">
													<colgroup>
														<col />
														<col />
														<col />
														<col />
														<col />
														<col />
													</colgroup>
													<thead>
														<tr>
															<th></th>
															<th><fmt:message key="rp.rule.id" /></th>
															<th><fmt:message key="rp.rule.useBom" /></th>
															<th><fmt:message key="rp.rule.items" /></th>
															<th><fmt:message key="rp.rule.platforms" /></th>
															<th><fmt:message key="rp.rule.commodities" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="rule"
															items="${rebateProgramForm.rebateProgram.rebateRules}">
															<c:set var="ruleRowStyle" value="vertical-align:top" />
															<tr>
																<td><c:if test="${!readOnly}">
																		<button name="selectedRule"
																			value="${rule.rebateRuleId}" type="button"
																			id="editRuleId" class="eto-icon-btn" title="Info"
																			${readOnly ? 'disabled' : ''}
																			style="vertical-align: top;"
																			onclick="goSelectRule('${rule.rebateRuleId}');">
																			<i class="md-icon">mode_edit</i>
																		</button>
																		<button name="deleteRule" value="${rule.rebateRuleId}"
																			type="button" id="deletedRuleId" class="eto-icon-btn"
																			title="Info" ${readOnly ? 'disabled' : ''}
																			style="vertical-align: top;"
																			onclick="javascript:goRemoveRule('${rule.rebateRuleId}');">
																			<i class="md-icon">delete</i>
																		</button>
																	</c:if></td>
																<td>${rule.rebateRuleKey}<html:messages
																		id="lineMessage" name="lineMessages"
																		property="rule('${rule.rebateRuleId}')">
																		<e2i2:img src="/alert_yellow_static.gif"
																			alt="${moreInfoTitle}"
																			onclick="javascript:showLineMessage(this,'${fn:escapeXml(lineMessage)}')" />
																		<c:set var="ruleRowStyle"
																			value="${ruleRowStyle};background-color:red" />
																	</html:messages>
																</td>
																<td><c:choose>
																		<c:when test="${rule.useBom}">
																			<fmt:message key="info.yes" />
																		</c:when>
																		<c:otherwise>
																			<fmt:message key="info.no" />
																		</c:otherwise>
																	</c:choose></td>
																<td class="eto-grid-expand" style="vertical-align: baseline;">
																	<div class="eto-grid-expand__container">
																		<div class="eto-grid-expand__content" id="item_${rule.rebateRuleId}"
																			style="white-space: pre-line;">
																		</div>
																		<script>
																		 var itemArray= [];
																		  var cols = "";
																		  <c:forEach var="ri" items="${rule.items}">
																		  var itemMultiSelOption = {
																					"itemNumber" : '${ri.itemNumber}',
																					"businessEntityName" : '${ri.businessEntity.businessEntityName}'
																				};
																		  itemArray.push(itemMultiSelOption);
																		  </c:forEach>
																		  itemArray.sort(function(a,b){
																			return a.itemNumber.localeCompare(b.itemNumber);
																		});
																		  <c:choose>
																			<c:when test="${not empty rule.items}">
																			 cols= "<ul>";
																			for (var key in itemArray) {
																				  cols+= "<li>";
																			  cols+= itemArray[key].itemNumber+" ["+itemArray[key].businessEntityName+"]\n";
																			  cols+= "<li>";
																			}
																			cols+= "</ul>";
																			</c:when>
																			<c:otherwise>
																			cols +='&nbsp;'
																			</c:otherwise>
																			</c:choose>
																			 $('#item_${rule.rebateRuleId}').html(cols); 
																		</script>
																		<div class="eto-grid-expand__truncated"
																			style="white-space: pre-line;"></div>
																		<button class="eto-grid-expand__toggle" type="button"
																			style="min-height: 100px;"></button>
																	</div>
																</td>
																<td class="eto-grid-expand" style="vertical-align: baseline;">
																	<div class="eto-grid-expand__container">
																		<div class="eto-grid-expand__content" id="platformUL_${rule.rebateRuleId}"  
																			style="white-space: pre-line;">
																		</div>
																			<script>
																			  var platformArray= [];
																			  var cols = "";
																			  <c:forEach var="ri" items="${rule.platforms}">
																			  var multiSelOption = {
																						"platformName" : '${ri.platformName}',
																						"platformType" : '${ri.platformType}'
																					};
																			  platformArray.push(multiSelOption);
																			  </c:forEach>
																			platformArray.sort(function(a,b){
		 																	return a.platformName.localeCompare(b.platformName);
																			});
																			<c:choose>
																			<c:when test="${not empty rule.platforms}">
		 																	 cols= "<ul>";
		 																	for (var key in platformArray) {
		 																		  cols+= "<li>";
																				  cols+= platformArray[key].platformName+" ("+platformArray[key].platformType+") \n";
																				cols+= "</li>";
																				}
		 																	cols+= "</ul>";
		 																	</c:when>
																			<c:otherwise>
																			cols +='&nbsp;'
																			</c:otherwise>
																			</c:choose>
		 																	 $('#platformUL_${rule.rebateRuleId}').html(cols); 
																		</script>
																		<div class="eto-grid-expand__truncated" 
																			style="white-space: pre-line;"></div>
																		<button class="eto-grid-expand__toggle" type="button"
																			style="min-height: 100px;"></button>
																	</div>
																</td>
																<td class="eto-grid-expand" style="vertical-align: baseline;">
																	<div class="eto-grid-expand__container">
																		<div class="eto-grid-expand__content" id="category_${rule.rebateRuleId}"
																			style="white-space: pre-line;">
																		</div>
																		<script>
																			  var categoryArray= [];
																			  var cols = "";
																			  <c:forEach var="ri" items="${rule.categories}">
																			  var multiSelOption = {
																						"categoryName" : '${ri.categoryName}',
																					};
																			  categoryArray.push(multiSelOption);
																			  </c:forEach>
																			  categoryArray.sort(function(a,b){
		 																	return a.categoryName.localeCompare(b.categoryName);
																			});
																			  <c:choose>
																				<c:when test="${not empty rule.categories}">
			 																	 cols= "<ul>";
			 																	for (var key in categoryArray) {
			 																		  cols+= "<li>";
																					  cols+= categoryArray[key].categoryName+"\n";
																					cols+= "</li>";
																					}
			 																	cols+= "</ul>";
																				</c:when>
																				<c:otherwise>
																				cols +='&nbsp;'
																				</c:otherwise>
																				</c:choose>
		 																	 $('#category_${rule.rebateRuleId}').html(cols); 
																		</script>
																		<div class="eto-grid-expand__truncated"
																			style="white-space: pre-line;"></div>
																		<button class="eto-grid-expand__toggle" type="button"
																			style="min-height: 100px;"></button>
																	</div>
																	
																</td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
											<script type="text/javascript">
									new eto.Grid({ el: document.querySelector('#rebateRules-grid') });
							</script>
										</c:otherwise>
									</c:choose>

									<c:if
										test="${!empty rebateProgramForm.selectedRule && !readOnly}">
										<div class="container"
											style="background-color: #f1f6f7; margin-top: 3%;">
											<h2 style="padding-top: 20px;">
												<fmt:message key="rp.edit.rule" />
											</h2>
											<div class="row" id="rebateRules-div">
												<div class="col-sm-4">
													<!-- <div style="display: flex;"> -->
													<h3>
														<fmt:message key="rp.rule.items" />
													</h3>
													<!-- </div> -->
													<hr>
												</div>
												<div class="col-sm-4">
													<h3>
														<fmt:message key="rp.rule.platforms" />
													</h3>
													<hr>
												</div>
												<div class="col-sm-4">
													<h3>
														<fmt:message key="rp.rule.commodities" />
													</h3>
													<hr>
												</div>
												<div class="col-sm-4">
													<%-- <html:hidden property="useBomValueOriginal"
										styleId="useBomValueOriginal"
										value="${rebateProgramForm.selectedRule.useBom}" /> --%>
													<label class="eto-checkbox"> <input
														id="useBomForItems" class="eto-checkbox__field"
														type="checkbox" name="selectedRule.useBom"
														onclick="handleDataChanged()"
														${rebateProgramForm.selectedRule.useBom ? 'checked' :  ''}><span
														class="eto-checkbox__box"></span> <span
														class="eto-checkbox__label"><fmt:message
																key="rp.rule.useBom" /></span>
													</label>
												</div>
												<div class="col-sm-4"></div>
												<div class="col-sm-4"></div>
												<div class="col-sm-4">Item</div>
												<div class="col-sm-4">Platform</div>
												<div class="col-sm-4">Commodity</div>
												<div class="col-sm-4">
													<div class="eto-input__field-container">
														<input class="eto-input__field" type="text" id="ruleItem">
														<span class="eto-input__addon"><i class="md-icon"
															onclick="javascript:doFinderPopup('EnterpriseItemFinderForNewRebateRule',null,'onItemCallback(%7B0%7D)')"
															style="cursor: pointer;">search</i></span>
													</div>
												</div>
												<div class="col-sm-4">
													<div class="eto-input__field-container">
														<input class="eto-input__field" type="text"
															id="rulePlatform"> <span class="eto-input__addon"><i
															class="md-icon"
															onclick="javascript:doFinderPopup('PlatformFinder',null,'onPlatformCallback(%7B0%7D)')"
															style="cursor: pointer;">search</i></span>
													</div>
												</div>
												<div class="col-sm-4">
													<div class="eto-input__field-container">
														<input class="eto-input__field" type="text"
															id="ruleCategory"> <span class="eto-input__addon"><i
															class="md-icon"
															onclick="javascript:doFinderPopup('ItemCategoryFinder',null,'onCategoryCallback')"
															style="cursor: pointer;">search</i></span>
													</div>
												</div>
												<div class="col-sm-4" style="padding-top: 5px;">
													<html:select multiple="true" styleId="ruleItems"
														style="border: 2px solid #ccc; border-radius: 4px; width:inherit;"
														property="ruleItemKeys"
														onchange="activateRemove('itemRemove')">
														<c:forEach var="ruleItem"
															items="${rebateProgramForm.selectedRule.items}">
															<c:set var="firstPart">
																<c:out
																	value="${ruleItem.itemNumber} - ${ruleItem.businessEntity.businessEntityName}" />
															</c:set>
															<c:set var="lengthFirstPart"
																value="${fn:length(firstPart)}" />
															<c:set var="lengthDesc" value="${52-lengthFirstPart}" />
															<c:choose>
																<c:when test="${lengthDesc >= 4}">
																	<c:set var="ruleItemDesc"
																		value="${e2ofn:abbreviate(ruleItem.description,lengthDesc)}" />
																</c:when>
																<c:otherwise>
																	<c:set var="ruleItemDesc" value="..." />
																</c:otherwise>
															</c:choose>
															<option value="${ruleItem.itemKey}">${firstPart}
																(${ruleItemDesc})</option>
														</c:forEach>
													</html:select>
													<script>
													sortItem();
													</script>
												</div>
												<div class="col-sm-4" style="padding-top: 5px;">
													 <html:select multiple="true" styleId="rulePlatforms"
														style="border: 2px solid #ccc; border-radius: 4px; width:inherit"
														property="rulePlatformKeys"
														onchange="activateRemove('platformRemove')">
														<c:forEach var="rulePlatform"
															items="${rebateProgramForm.selectedRule.platforms}">
															<option value="${rulePlatform.platformKey}" label="${rulePlatform.platformName}  (${rulePlatform.platformType})"></option>
														</c:forEach>
													</html:select>
													<script>
													sortPlatform();
													</script>
												</div>
												<div class="col-sm-4" style="padding-top: 5px;">
													<html:select multiple="true" styleId="ruleCategories"
														style="border: 2px solid #ccc; border-radius: 4px; width:inherit"
														property="ruleCategoryKeys"
														onchange="activateRemove('categoryRemove')">
														<c:forEach var="ruleCat"
															items="${rebateProgramForm.selectedRule.categories}">
															<option value="${ruleCat.categoryKey}">${ruleCat.categoryName}</option>
														</c:forEach>
													</html:select>
													<script>
													sortCategory();
													</script>
												</div>
											</div>
											<div class="row">
												<div class="col-sm-4">
													<button class="eto-btn" type="button"
														onclick="javascript:removeSelected(document.forms[0].ruleItems)"
														id="itemRemove" disabled>
														<fmt:message key="button.remove" />
													</button>
												</div>
												<div class="col-sm-4">
													<button class="eto-btn" type="button"
														onclick="javascript:removeSelected(document.forms[0].rulePlatforms)"
														id="platformRemove" disabled>
														<fmt:message key="button.remove" />
													</button>
												</div>
												<div class="col-sm-4">
													<button class="eto-btn" type="button"
														onclick="javascript:removeSelected(document.forms[0].ruleCategories)"
														id="categoryRemove" disabled>
														<fmt:message key="button.remove" />
													</button>
												</div>
												<div class="col-sm-4"
													style="padding-top: 30px; padding-bottom: 10px;">
													<button type="button" class="eto-btn eto-btn--primary"
														id="updateRule"
														onclick="javascript:goUpdateRule('${rebateProgramForm.selectedRule.rebateRuleId}');"
														disabled>
														<fmt:message key="rp.save.rule" />
													</button>
												</div>
											</div>
										</div>
									</c:if>
								</section>

								<section class="eto-tab-content__item" id="RESULTS">

									<%-- ${empty rebateProgramForm.rebateProgram.rebateProgramKey ? 'disabled':''} --%>
									<%-- <button id="previewButton" type="button"
								class="eto-btn eto-btn--primary" ${readOnly ? 'disabled' : ''}
								onclick="javascript:goRunProgram();">
								<fmt:message key="button.preview" />
							</button> --%>

									<c:choose>
										<c:when test="${empty rebateProgramForm.affectedItems}">
											<div style="display: flex;">
												<div class="eto-messageblock" data-message-type="info"
													id="message-block-no-results-preview"
													style="margin: auto; width: 100%; margin-top: 5%;">
													<div class="eto-messageblock__body">No affected items
														to display, try adding different set of items</div>
													<!-- <a href="javascript:void(0)" role="button"
										class="eto-messageblock__close"></a> -->
												</div>
												<script>
										new eto.MessageBlock({ el : document.querySelector('#message-block-no-results-preview') });
								</script>
											</div>
										</c:when>
										<c:otherwise>
											<div class="eto-grid" id="resultsGrid"
												style="margin-top: 15px;">
												<table id="previewTable" style="width: 100%;">
													<thead>
														<tr>
															<th><fmt:message key="rp.results.itemNumber" /></th>
															<th><fmt:message key="rp.results.itemDescription" /></th>
															<th><fmt:message key="rp.results.qpa" /></th>
															<th><fmt:message key="rp.results.business" /></th>
															<th><fmt:message key="rp.results.rulesApplied" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="affectedItem"
															items="${rebateProgramForm.affectedItems}">
															<tr>
																<td><c:out value="${affectedItem.itemIdentifier}" /></td>
																<td><c:out value="${affectedItem.itemDescription}" /></td>
																<td><c:choose>
																		<c:when test="${empty affectedItem.itemQuantity}">
																			<fmt:formatNumber value="1" minFractionDigits="1"
																				maxFractionDigits="6" />
																			<span style="font-style: italic">&nbsp;(<fmt:message
																					key="rp.results.noqpa.title" />)
																			</span>
																		</c:when>
																		<c:otherwise>
																			<fmt:formatNumber
																				value="${affectedItem.itemQuantity}"
																				minFractionDigits="1" maxFractionDigits="6" />
																		</c:otherwise>
																	</c:choose></td>
																<td><c:out value="${affectedItem.businessEntity}" /></td>
																<%-- <td><a
													href="javascript:i2uiToggleTab('allRebateTabs','null',document.getElementById('RULESTAB').parentNode);goChangeTab('RULES')">${affectedItem.ruleKeys}</a></td> --%>
																<td><a href="javascript:goChangeTab();">${affectedItem.ruleKeys}</a></td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
											<script type="text/javascript"> 
								new eto.Grid({ el: document.querySelector('#resultsGrid') });
							</script>
										</c:otherwise>
									</c:choose>
								</section>
							</div>

							<script type="text/javascript">
			tabsObj = new eto.Tabs({ el: document.querySelector('#allRebateTabs') });
			tabsObj.on('activeChanged', function(){
				var tabId = tabsObj.getActive().id;		
				setActiveTab(tabId);
			});
			
			/* $("#newRule").click(function(event){
			     event.preventDefault();
			     goAddRule();
		    }); */
			
			function goChangeTab(){
				if(tabsObj != null)
						tabsObj.setActive(1);
			}
			</script>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="footer">
			<div class="eto-btn-group"
				style="margin-top: 15px; margin-left: 15px;">
				<c:if test="${e2ofn:hasAccess(appContext, 'REBATE', 'Save')}">
					<button type="button" class="eto-btn eto-btn--primary" id="saveAndContinueButton"
						onclick="javascript:goSaveAndContinue();"
						${readOnly ? 'disabled' : ''}>
						<fmt:message key="button.save" />
					</button>
			</c:if>
					<c:if test="${!empty rebateProgramForm.backAction}">
						<button type="button" class="eto-btn" id="backButton"
							onclick="javascript:goBack('${fn:escapeXml(rebateProgramForm.backAction)}');">
							<fmt:message key="button.back" />
						</button>
					</c:if>
				<c:forEach var="pdcEvent"
					items="${e2ofn:allValidEvents('Rebate',rebateProgramForm.rebateProgram)}">
					<c:set var="disableFlag" value="${false}" />
					<c:choose>
						<c:when
							test="${!e2ofn:hasEventAccess(appContext, 'Rebate', pdcEvent.eventName)}">
							<c:set var="disableFlag" value="${true}" />
						</c:when>
					</c:choose>
					<button type="button" class="eto-btn"
						id="${pdcEvent.eventName}EventButton" ${disableFlag ? 'disabled' :''}
						onclick="javascript:goEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');">
						<fmt:message key="${pdcEvent.uiButtonLabel}" />
					</button>
				</c:forEach>
			</div>
		</div>
	</e2o:form>

	<script type="text/javascript">
				// $('#pricing-grid table').css('width','100%');
				$('#rebateRules-grid table').css('width','100%');
				$('#resultsGrid table').css('width','100%');
			//	var rebatesCardDetails = new eto.Expand({ el: document.querySelector('#rebatesCardDetails') });
			
			</script>
</body>