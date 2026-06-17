<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache enabled="false" />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<c:choose>
	<c:when test="${forecastForm.selectedForecastTab == 'CURRENT'}">
		<e2ot:help contextName="Forecast-Current" />
	</c:when>
	<c:otherwise>
		<e2ot:help contextName="Forecast-Adjustable" />
	</c:otherwise>
</c:choose>
<style type="text/css">
#fcContainer {
	table-layout: fixed
}

.instructionsArea {
	background: transparent;
}

.periodFUTURE {
	background-color: #C4DFF2 !important;
}

.periodPAST {
	background-color: #f7f3ed !important;
}

.periodCURRENT {
	background-color: aliceblue !important;
}

.dividerColumn {
	padding: 0 !important;
	padding-right: 2px !important;
	margin: 0px !important;
	background-color: #999999
}

table tr>td {
	margin: 0px;
	padding: 0px;
}
table{
width:auto !important;
height:auto !important;
}

#tabUnSelected.tabText {
	width : 50px !important;
}

#tabSelected.tabText {
	width : 50px !important;
}

.tabScroller{
	width : 50px !important;
}

.scrollableTable{
	height: auto !important;
}
</style>
</head>
<c:set var="eventSet"
	value="${e2ofn:allValidEventsForList('Forecast',forecastForm.forecastRecords)}" />
<e2ot:eventActionSupport stateModel="Forecast"
	eventRecords="${forecastForm.forecastRecords}">
	<jsp:attribute name="eventRecordId">${eventRecord.forecastExternalId}</jsp:attribute>
</e2ot:eventActionSupport>
<c:set var="stateMap" value="${e2ofn:getStates('Forecast')}" />
<c:set var="decimalPrecision"
	value="${e2ofn:getConfigValue('pcm.forecast.decimal.precision')}" />
<c:set var="calculatedValueLimit"
	value="${e2ofn:getConfigValue('pcm.forecast.calculatedvalue.limit')}" />
<c:set var="roundingRule"
	value="${e2ofn:getConfigValue('pcm.forecast.roundingmode')}" />
<c:set var="fixedDisplay"
	value="${e2ofn:getConfigValue('pcm.forecast.adjustmentType.fixed')}" />
<c:set var="percentDisplay"
	value="${e2ofn:getConfigValue('pcm.forecast.adjustmentType.percent')}" />
<script>
var undoValueStack = new Array();
var number;
var keySet = new Object();
<c:forEach var="fc" items="${forecastForm.forecastRecords}">
keySet['${fc.forecastExternalId}'] = '${fc.forecastKey}'; 
</c:forEach>

function handleDataChangedForField(field)
{
	if (field != null) {
		markChangedFieldPredicate(field);
	}
	if (field.oldvalue != null) {
	 	var oldvalues = new Object();
		oldvalues[field.id] = field.oldvalue;
		pushUndoValues(oldvalues);
	}	
	handleDataChanged();
}

function handleDataChanged() {
	document.forms[0].unsavedData.value = 'true';
	var msgArea = document.getElementById('unsavedDataMsg');
	if (msgArea != null)
	{
		msgArea.innerText = '<fmt:message key="info.unsaved_data"/>';
	}
}


function validateData()
{
    var msg = "<fmt:message key='errors.fc.negativeCalculatedValue'/>";
	var calculatedValueIds=$("td[id^=calculatedVal_FUTURE_]");
	for(var i=0;i<calculatedValueIds.length;i++){
		if($(calculatedValueIds[i]).hasClass('errorText')){
			showOkMessageBox('OK','WARN',msg,"<fmt:message key='msg.warn'/>");
			return false;
		}	
	}
    return true;
}

function toggleRow(forecastExternalId,state) {
	
	var expandBtn = $("#expandBtn"+forecastExternalId);
	var expandBtnState = expandBtn.attr("expanded");
	if (state == null) { //toggle
		state = "true"== expandBtnState ? false : true;
	}
	
	var path = "div[name='adjustment" + forecastExternalId +"']";
	var abfAdjustments = $(path);
	for (i=0;i<abfAdjustments.length;i++) {
		var adj = abfAdjustments[i];
		if (state) {
			adj.className = 'adjshown';
		} else {
			adj.className = 'adjhidden';
		}
	}	
	var plusInnerHTML = '<e2i2:img src="/plus_norgie.gif" />';
	var minusInnerHTML = '<e2i2:img src="/minus_norgie.gif" />';
	if (state) {
		expandBtn.attr("expanded","true");
		expandBtn.html('add_circle');
	} else {
		expandBtn.attr("expanded","false");
		expandBtn.html('remove_circle');
	}
}	

function toggleAllRows(forecastId) {
	var ic = $("#expandBtn"+forecastId);
	if(ic.html() === 'add_circle'){
		ic.html('remove_circle');	
		$(".expend_fcTableADJ").addClass("expanded");
	}else if(ic.html() === 'remove_circle'){
		ic.html('add_circle');
		$(".expend_fcTableADJ").removeClass("expanded");
	}
	
	  
	var expandAllBtn = $("#expandBtn"+forecastId);	
	var expandAllBtnState = expandAllBtn.attr("expanded");
	var rowBtns = $(".expandRowBtn");
	var state;
	
	if ("false" == expandAllBtnState) {
		expandAllBtn.attr("expanded","true");
		expandAllBtn.html('remove_circle');
		$(".hiddenRow").removeClass('hidden')
		state=true;
		for(j=0;j<rowBtns.length;j++){		
			var fcextid=rowBtns[j].getAttribute("fcextid");
			toggleRow(fcextid,state);
		 }
	} else {
		expandAllBtn.attr("expanded","false");
		expandAllBtn.html('add_circle');
		state=false;
		$(".hiddenRow").addClass('hidden')
		 for(j=0;j<rowBtns.length;j++){
			 var fcextid=rowBtns[j].getAttribute("fcextid");
			 toggleRow(fcextid,state);
			}		
		}
	
}	

function getCalculatedValueForAdjustmentType(rowCount,field){
   var adjustmentAmountId=$("input[id^=adjustmentAmount_"+rowCount+"]");   
   var adjustableValueId=$("td[id^=adjustableValue_"+rowCount+"]");
   var calculatedValueId=$("td[id^=calculatedVal_FUTURE_"+rowCount+"]");   
   var rowAdjustmentTypeId="#rowAdjustmentType_"+rowCount; 
   var adjustmentTypeValue=$(rowAdjustmentTypeId).val();
   $("input[id^=adjustmentType_"+rowCount+"]").val(adjustmentTypeValue);
   if(adjustmentTypeValue=='PERCENT'){	      
	   $("label[id^=adjustmentType_label_"+rowCount+"]").html("${percentDisplay}");
   }else if (adjustmentTypeValue=='FIXED'){
	   $("label[id^=adjustmentType_label_"+rowCount+"]").html("${fixedDisplay}");  
   }  
   if(adjustmentAmountId.length==adjustableValueId.length && adjustmentAmountId.length==calculatedValueId.length){
	   for(var i=0;i<adjustableValueId.length;i++){
		   getCalculatedValueForAdjustmentAmountAndType(adjustableValueId[i],adjustmentAmountId[i],calculatedValueId[i],rowAdjustmentTypeId);   
	   }
   }
	
}

function getCalculatedValueForAdjustmentAmountAndType(adjustableValueId,adjustmentAmountId,calculatedValueId,adjustmentTypeId,field){	
	var calculatedValueField=$(calculatedValueId);
	var adjustmentType=$(adjustmentTypeId).val();
	var adjustableValue= $(adjustableValueId).html();
	adjustableValue=$.trim(adjustableValue);	
	var adjustmentAmount=$(adjustmentAmountId).val();
	if (isValueEmpty(adjustmentAmount) || adjustmentAmount==null){
		adjustmentAmount=0;
	}else if(isNaN(adjustmentAmount)){
		var	msg = "<fmt:message key="errors.field_decimal_required"/>";
		showOkMessageBox('OK','WARN',msg,"<fmt:message key='msg.warn'/>",function(){
			$(adjustmentAmountId).val("");
	        calculatedValue=adjustableValue;
	        calculatedValueField.html(calculatedValue);	
		});
		return;
	}else{	
		adjustmentAmount=$.trim(adjustmentAmount);
		adjustmentAmount=formatValue(adjustmentAmount);
		$(adjustmentAmountId).val(adjustmentAmount);
	}		
	getCalculatedData(adjustmentType,adjustableValue,adjustmentAmount,calculatedValueField,field);
}

function getCalculatedData(adjustmentType,adjustableValue,adjustmentAmount,calculatedValueField,field){
	
	$.post("forecast/forecastCalculation.jsp", {adjType:adjustmentType, adjVal:adjustableValue,adjAmt:adjustmentAmount},
			function(data){		  
		    if(data<=${calculatedValueLimit}){
				 calculatedValueField.addClass('errorText');		 
				 calculatedValueField.html(data);
				 return;
			 }		
			calculatedValueField.html(data);
			if(calculatedValueField.hasClass('errorText')){
			calculatedValueField.removeClass('errorText');	
			}
			if (field != null) {
				markChangedFieldPredicate(field);
			}
		    
	},"text");
	
}

function checkAllowedDecimals(field){
	var adjustmentAmount=$(field).val();
	if (isValueEmpty(adjustmentAmount) || adjustmentAmount==null || isNaN(adjustmentAmount)){
		return false;
	}
	if(isDecimal(adjustmentAmount,true)){
	var noOfDecimals=getDecimals(adjustmentAmount);
	if(noOfDecimals>${decimalPrecision}){
		var	msg = "<fmt:message key="warn.field_truncated_decimal"/>";
		showOkMessageBox('OK','WARN',msg,"<fmt:message key='msg.warn'/>");
	}	
  }
}

function formatValue(val){
	 var noOfDecimals=getDecimals(val);
	 var formatVal;
	 if('${roundingRule}'=='DOWN'){	 
	 var tmp=new String(val);
	 formatVal=tmp.substring(0,(tmp.length-(noOfDecimals-${decimalPrecision})))
	 }else{
	 formatVal = roundDecimal(val,${decimalPrecision});	 
	 }		 
	 return eval(formatVal);	
}

function getDecimals(val){
	var tmp=new String(val);  
    if (tmp.indexOf(".")>-1)
        return tmp.length-tmp.indexOf(".")-1;
    else
        return 0;
}


function getLastApprovedValue(state)
{
	var adjAmt=state.amt;
	if (adjAmt==null ||adjAmt==''){
		adjAmt=0;
	}
	var adjVal=state.av;
	var adjType=state.at;
	var user=state.aprUsr.uname;
	var appDt=state.aprDt;
	$.post("forecast/forecastCalculation.jsp", {adjType:state.at, adjVal:state.av,adjAmt:state.amt},
			function(data){
		  var url = "forecast/forecastLastApprovedValue.jsp?adjVal="+adjVal+"&adjAmt="+adjAmt+"&adjType="+adjType+"&user="+user+"&calVal="+data+"&appDt="+appDt;              
	      var popupWindow = window.open(url,'_blank',
		  'height=300,width=500,resizable=yes,status=yes,toolbar=yes,scrollbars=yes,menubar=no,location=no');
	     if (popupWindow == null)
	     {
	     showPopupBlockerMessage();
	     return;
	     }
	     popupWindow.focus();
	     window.onunload = closeFinderIfOpen;
		
	},"text");
    
}

function showLineMessage(line,message)
{	
	var title = "<fmt:message key="msg.info"/>";
	showModalMessageBox(title,message);
}

function showTabMessage()
{	
	var title = "<fmt:message key="msg.info"/>";
	var message="<fmt:message key="errors.fc.tab.message"/>";
	showModalMessageBox(title,message);
}

function validateExtendedPeriod(field)
{
	<fmt:message var="msg" key="errors.fc.invalidRollover">
	   <fmt:param>${forecastForm.minRolloverPeriods}</fmt:param>
	   <fmt:param>${forecastForm.maxRolloverPeriods}</fmt:param>
	</fmt:message>
	checkNumericField(field,false,function() {
		if (!isInteger(field.value,false)) {
			showOkMessageBox('OK','ERROR',
	                   "<fmt:message key='errors.field_integer_required'/>",
	                   "<fmt:message key='msg.error'/>",function() {
	                	   field.focus();
	                   });
		} else {
			number = parseInt(field.value);
			if (number > ${forecastForm.maxRolloverPeriods} || number < ${forecastForm.minRolloverPeriods})
		    {
		    	showOkMessageBox('OK','ERROR',"${msg}","<fmt:message key='msg.error'/>");
		    }
		}
	});
}

function validateForecastValue(field)
{
	checkNumericField(field,${forecastForm.allowNegativeValues},function() {
		var amount = roundDecimal(field.value,6);
        field.value = formatDecimal(amount,2,6);
	});
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

function handleLineButtons()
{ 
	var cbList = $('input:checked[name="selectedRecordKeys"]');
	var checked = cbList.length;
	if (checked > 0) 
	{
		setButtonEnabled("copyButton",true);
		setButtonEnabled("findButton",true);				
	}
	else
	{
		setButtonEnabled("copyButton",false);	
		setButtonEnabled("findButton",false);	
	}
	setButtonEnabled("autoUndoFillButton",undoValueStack.length > 0);
    if (checked > 0)
    {
    	var opCounts = getSelectedOperationCounts();        
    	setButtonEnabled("deleteButton", opCounts['Delete'] == checked);
    	setButtonEnabled("autoFillButton", opCounts['Edit'] == checked);
    }
    else
    {
    	setButtonEnabled("deleteButton", false);
    	setButtonEnabled("autoFillButton", false);
    }
    
    if(checked == 0){
    	setButtonEnabled("ApproveEventButton", false);
    }
    
	if (checked == 1)
	{
		var key = keySet[$('input:checked[name="selectedRecordKeys"]').val()];
		setButtonEnabled("historyButton", !isValueEmpty(key));
	}
	else
	{
		setButtonEnabled("historyButton", false);
	}
}

function scrollToColumnForTable(columnId,tableId)
{
	var offset = 0;
	var table = $(tableId);
	var headers =  $('tr:first',table).children(':not(.fixedColumn)');
	headers.each(function() {
		if (this.id == columnId)
		{
			return false;
		}						
	    offset += $(this).outerWidth();		 
	}); 
	$(table).scrollLeft(offset);
}

function scrollToColumn(columnId) {
	scrollToColumnForTable(columnId,'#fcTableCUR');
	scrollToColumnForTable(columnId,'#fcTableADJ');
}

function scrollToRecordForTable(recordId,tableId)
{ 
	var table = $(tableId);
	//var tablebody = $('#fcTableBody');
	var row = $('#'+recordId,'td:parent');
	if (row != null && row.length > 0)
	{
		row.css('background-color','#fff6a6');
		row[0].scrollIntoView(true);				
		var offset = (row.position().top - table.position().top);				
		// If the offset is less then the header we can scroll down to show the row
		if (offset < 45);
		{
			table.scrollTop(table.scrollTop()-45);
		}
	} 	 
}

function scrollToRecord(recordId){
	scrollToRecordForTable(recordId,'#fcTableCUR');	
	scrollToRecordForTable(recordId,'#fcTableADJ');
}

function fillRowRight(rowId, oldValues)
{
	var values = $('#fcRow_' + rowId + ' input:text.forecastValue');
	var valueField = null;
	var j = 0;
	for (j=values.length-1; j > -1; j--)
	{
		if (isValueEmpty(values[j].value) == false)
		{
			valueField = values[j];
			break;
		}			 
	}
	if (valueField != null)
	{
		// The id has three parts, we need the index
		var parts = valueField.id.split('_');
		if (pushValueDownIndexed(parts[3],parts[0]+'_'+parts[1]+'_'+parts[2]+'_{0}', oldValues,markChangedFieldPredicate))
		{
			handleDataChanged();
			if(parts[0].match(/^adjustment.*$/)){
			getCalculatedValueForAdjustmentType(parts[1]+'_'+parts[2]);
			}
			return true;
		}		
	}	
	return false;
}

function checkTabChange()
{
    return true;
}

function goChangeTab(showTab,hideTab,downloadForecastModel,forecastRowId)
{	
	   var tab = document.getElementById(hideTab);
	   if (tab != null)
	   {
	    	  tab.style.display='none';      
	   }	   
	   tab = document.getElementById(showTab);
	   // showWaitBusy(1);
	   if (tab != null)
	   {
	      tab.style.display='block';      
	   }
	   $("#selectedForecastTab").val(downloadForecastModel);
	   document.forms[0].downloadForecastModel.value = downloadForecastModel;
	   document.forms[0].selectedForecastTab.value = downloadForecastModel;
	   if(downloadForecastModel=="ADJUSTABLE"){
	   $('meta[name=MCMHELP]').attr('content','Forecast-Adjustable');
	   disableLineButtons(true);
	   }
	   else{
		   disableLineButtons(false);
		   $('meta[name=MCMHELP]').attr('content','Forecast-Current');
	   }
	   disableAllLineEventButtons(true);
	   i2uiToggleAllRowsSelectionState(this,forecastRowId);
	   document.getElementById(forecastRowId+"_globalrowselector").checked=false;	   
	   
	   
}

function pushUndoValues(oldValues)
{
	undoValueStack.push(oldValues);
	setButtonEnabled("autoUndoFillButton",undoValueStack.length > 0);	
}

var markChangedFieldPredicate = function markChangedField(field) {
	$("#"+field.id).css("background-color","#FFFF9F");
}
var calculatedValue=function getCalculatedValue(idx){
	getCalculatedValueForAdjustmentType('A_'+idx);
}


function handlePushValueDown(startIndex, fieldId)
{
	var oldValues = new Object();
	var result=false;
	if (fieldId.match(/^adjustment.*$/)){
		result=pushValueDownIndexed(startIndex,fieldId,oldValues,markChangedFieldPredicate,calculatedValue);	
	}else{
		result=pushValueDownIndexed(startIndex,fieldId,oldValues,markChangedFieldPredicate);
	}
	if(result){
	    handleDataChanged();
		pushUndoValues(oldValues);
	}	
}

function handlePushValueRight(startIndex, fieldId)
{
	var oldValues = new Object();		
	if (pushValueDownIndexed(startIndex,fieldId,oldValues,markChangedFieldPredicate))
	{
		handleDataChanged();
		pushUndoValues(oldValues);
	}
}

function handleUndoValues()
{
	var oldValues = undoValueStack.pop();
	if (oldValues != undefined)
	{
		for (var key in oldValues)
		{
			document.getElementById(key).value = oldValues[key];
			if (!keyExistsInUndoStack(key)) {
				$("#" + key).css("background-color","#FFFFFF");
			}
			
			if(key.match(/^adjustment.*$/)){
				var parts = key.split('_');
				getCalculatedValueForAdjustmentType(parts[1]+'_'+parts[2]);	
			}
		}			
	}			
	if (undoValueStack.length == 0)
	{
		setButtonEnabled("autoUndoFillButton",false);
	}
}

function keyExistsInUndoStack(key) {
	var keyexists = false;
	for (i=0 ; i<undoValueStack.length ; i++) {
		var undoValues =  undoValueStack[i];
		if (undoValues[key] != null) {
			keyexists = true;
			break;
		}
	}
	return keyexists;
}

function handleAutoFillRows()
{
	var checked = $('input:checked[name="selectedRecordKeys"]');
	var oldValues = new Object();
	var undo = false;
	for (var i=0; i < checked.length; i++)
	{		
		if(checked[i].id=='fcTableADJ_rowselector' && fillRowRight(checked[i].value,oldValues)){
			undo = true;
		}else if(checked[i].id=='fcTableCUR_rowselector' && fillRowRight(checked[i].value,oldValues)){
			undo = true;
		}
	}
	if (undo)
	{
		pushUndoValues(oldValues);
	}
}

function handleShowHistory()
{
	var checked = $('input:checked[name="selectedRecordKeys"]');
	if (checked.length > 0)
	{	
		var key = keySet[checked[0].value];
		showAuditHistory(key,'PcmForecast');
	}	
}

function handleConcurrentUpdate()
{
	showOkCancelMessageBox('OK CANCEL','QUESTION',
            "<fmt:message key='errors.another_user_changed_data'/>",
            "<fmt:message key='msg.warn'/>",
            function(){
		    	goMergeChanges();            	
            },
            function(){
                document.forms[0].action="viewForecast";
                document.forms[0].submit();	
                showBusy();
            });
}

function goEvent(eventName,promptName, ackMessage)
{
    if (promptName != null && promptName.length > 0)
    {
       var result = showInputBox('OK','QUESTION',
                 promptName,promptName);
       if (result == '' || result == undefined)
       {
    	   showOkMessageBox('OK','ERROR',
    			   "<fmt:message key='errors.field_required'/>","<fmt:message key='msg.error'/>");
           return;
       }
       document.forms[0].eventMessage.value = result;
    }


    document.forms[0].eventName.value = eventName;
    document.forms[0].action="processEventForecast";
	document.forms[0].submit();
	showWaitBusy();	
    if (ackMessage != null && ackMessage.length > 0)
    {
    	showOkMessageBox('OK','INFO',
                ackMessage,
                "<fmt:message key='msg.info'/>");
    }
}

function goMergeChanges()
{
   	document.forms[0].action="mergeForecast";
	document.forms[0].submit();	
	showWaitBusy();	   		
}

function goSaveAndContinue()
{
	if (validateData())
	{
		if(number==undefined || (number <= ${forecastForm.maxRolloverPeriods} && number >= ${forecastForm.minRolloverPeriods}))
		{
    	document.forms[0].action="saveAndContinueForecast";
		document.forms[0].submit();	
    	showBusy();
		}
		else{
			showOkMessageBox('OK','ERROR',"${msg}","<fmt:message key='msg.error'/>");
		}
	}	   		
}

function goSave()
{
	if (validateData())
	{	
		if(number == undefined || (number <= ${forecastForm.maxRolloverPeriods} && number >= ${forecastForm.minRolloverPeriods}))
		{
    	document.forms[0].action="saveForecast";
		document.forms[0].submit();	
		showWaitBusy();
		}
		else{
			showOkMessageBox('OK','ERROR',"${msg}","<fmt:message key='msg.error'/>");
		}
	}
}

function goDownload()
{
	document.forms[0].action="downloadForecast";
	document.forms[0].submit();		   		
}

function goCopy()
{
   	document.forms[0].action="copyForecast";
	document.forms[0].submit();	
	showWaitBusy();	   		
}

function goFindOther()
{
   	document.forms[0].action="viewOtherForecast";
	document.forms[0].submit();	
	showWaitBusy();	   		
}

function goDelete()
{
    showYesNoMessageBox('YES NO','WARN',
            "<fmt:message key='warn.delete_object'/>",
            "<fmt:message key='msg.warn'/>", function() {
            	document.forms[0].action="deleteForecast";
                document.forms[0].submit(); 
                showWaitBusy();    	
            });
}

function goClear()
{
	document.forms[0].action="deleteForecast";
	document.forms[0].submit();	
	showWaitBusy();	
}

function goBack(action)
{
	var fr = partial(goBackCallback,action);
	if (canLeavePage(fr))
    {
		goBackCallback(action);         
    }
}

function goBackCallback(action)
{
    document.forms[0].action=action;    
    document.forms[0].preserveSearchValues.value="true";    
	document.forms[0].submit();	
	showWaitBusy(); 		
}

function goCancel()
{
    if (canLeavePage(goCancelCallback))
    {
    	goCancelCallback();         
    }
}

function goCancelCallback()
{
    document.forms[0].action="home";
    document.forms[0].submit();
}

function hideColumnsForTable(buttonId,tableId)
{
	 
	$(tableId+' .hideableColumn').each(function() {
		$(this).hide();
	});
	var toggleicon = document.getElementById(buttonId);	
	$("i#md-compress-icon").text("chevron_right");
    toggleicon.title='<fmt:message key="label.showColumns"/>';
    document.forms[0].showAllColumns.value = false;
    $("#fcTableCUR_data").css("width","");

}
function hideColumns(buttonId){
	hideColumnsForTable(buttonId,'#fcTableCUR');
	hideColumnsForTable(buttonId,'#fcTableADJ');
}

function showColumnsForTable(buttonId,tableId)
{
	$(tableId+' .hideableColumn').each(function() {
		$(this).show();
	});	
	var toggleicon = document.getElementById(buttonId);	
	$("i#md-compress-icon").text("chevron_left");
    toggleicon.title='<fmt:message key="label.hideColumns"/>';
    document.forms[0].showAllColumns.value = true;	
}
function showColumns(buttonId){
	showColumnsForTable(buttonId,'#fcTableCUR');
	showColumnsForTable(buttonId,'#fcTableADJ');
}
function handleToggleColumns()
{
	if (document.forms[0].showAllColumns.value == 'true')
	{
		hideColumns('compressButton');
	}
	else
	{
		showColumns('compressButton');
	}
}

function disableLineButtons(removeCopyBtn){	
	setButtonEnabled("findButton",false);
    setButtonEnabled("autoUndoFillButton",false);
    setButtonEnabled("autoFillButton",false);
	setButtonEnabled("deleteButton", false);
	setButtonEnabled("historyButton", false);
	if(removeCopyBtn){
		$("#copyButton").hide();
		$("#CloseEventButton").hide();
	}else{
		$("#copyButton").show();
		$("#CloseEventButton").show();
		setButtonEnabled("copyButton",false);	
	}
		
}


function initPage()
{ 
	var header = 80 + $('#headerSection').height();	
    $('#fcTableCUR').height(getBodyHeight()-header);
    $('#fcTableADJ').height(getBodyHeight()-header);
    disableAllLineEventButtons(true);
	handleLineEventButtons();
	handleLineButtons();
	<c:if test="${forecastForm.selectedForecastTab == 'ADJUSTABLE'}">
	disableLineButtons(true);
	</c:if>
	if (document.forms[0].showAllColumns.value == 'false')
	{
		hideColumns('compressButton');
	}

	<c:if test="${!empty param.scrollToRecord}">
    scrollToRecord('${param.scrollToRecord}');
    </c:if>
    <c:choose>
    	<c:when test="${forecastForm.selectedForecastTab == 'ADJUSTABLE'}">
    		scrollToColumn('periodHeader${forecastForm.adjustableTimeline.currentPeriod.label}');
    	</c:when>
    	<c:otherwise>
			scrollToColumn('periodHeader${forecastForm.currentTimeline.currentPeriod.label}');
		</c:otherwise>
    </c:choose>
    i2uiManageTabs('headerTabSet',document.body.offsetWidth - 40);
	
}

function callfullScreenMethod() {
	$("#fullscreen_exit").show();
	$("#fullscreen").hide();
	$("#forecast_title").hide();
	$("#forecast_details_rec").hide();
}
function callExitFullScreen() {
	$("#fullscreen").show();
	$("#fullscreen_exit").hide();
	$("#forecast_title").show();
	$("#forecast_details_rec").show();
}

</script>
<body onload="initPage()" style="overflow-y : scroll;">

	<c:if test="${e2ofn:getConfigValue('pcm.forecast.json.enabled')}">
		<!-- <JSON> ${forecastForm.testJSON} </JSON> -->
	</c:if>

	<form action="saveAndContinueForecast" method="POST">
		<input type="hidden" name="preserveSearchValues" />
		<input type="hidden" name="headerAreaCollapsed" />
		<c:set var="readOnly"
			value="${!e2ofn:hasAccess(appContext, 'FORECAST', 'Save')}" />

		<html:hidden property="backAction" />
		<html:hidden property="unsavedData" />
		<html:hidden property="eventName" />
		<html:hidden property="eventMessage" />
		<html:hidden property="downloadForecastModel" />
		<html:hidden property="selectedForecastModel" />
		<html:hidden property="forecastType" />
		<html:hidden property="periodType" />
		<html:hidden property="calendarName" />
		<html:hidden property="showAllColumns" />
		<input type="hidden" name="selectedForecastTab" id="selectedForecastTab"/>

		<fmt:message var="fcTitle" key="fc.header.title" />
		<fmt:message var="fcDetailTitle" key="fc.detail.title" />
		<fmt:message var="clearTitle" key="button.title.clear" />
		<fmt:message var="pushdownTitle" key="button.title.pushdown" />
		<fmt:message var="moreInfoTitle" key="info.more_information" />
		<fmt:message var="pushacrossTitle" key="button.title.pushacross" />
		<fmt:message var="historyTitle" key="button.history" />
		<fmt:message var="hideColTitle" key="label.hideColumns" />
		<fmt:message var="showColTitle" key="label.showColumns" />
		<div class="row" id="forecast_title" style="padding: 10px; width: 100%">
			<div class="col-sm-6">
				<label
					style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px; padding-left: 20px;">
					<c:out value="${fcTitle}" />
				</label>
			</div>
			<div class="col-sm-6">
				<span id="unsavedDataMsg" style="font-style: italic; float: right;"><c:if
						test="${forecastForm.unsavedData}">
						<fmt:message key="info.unsaved_data" />
					</c:if></span>
			</div>
		</div>
		<div class="eto-well"  id="forecast_details_rec">
			<e2o:errors maxErrors="4" styleId="errors"
				property="com.test.controller.action.GLOBAL_MESSAGE" />
			<e2i2:instructionsarea>
				<fmt:message key="fc.title" />
			</e2i2:instructionsarea>
			<div class="row col-sm-12">
				<div class="col-sm-2">
					<div>
						<label><fmt:message key="fc.type" /></label>
					</div>
					<div>
						<span><b><c:out value="${forecastForm.forecastType}" /></b></span>
					</div>
				</div>
				<div class="col-sm-2">
					<div>
						<label><fmt:message key="fc.calendar" /></label>
					</div>
					<div>
						<span><b><fmt:message
									key="calendar.type.${fn:toLowerCase(fn:escapeXml(forecastForm.calendarName))}" /></b></span>
					</div>
				</div>
				<div class="col-sm-2">
					<div>
						<label><fmt:message key="fc.periodType" /></label>
					</div>
					<div>
						<span><b><fmt:message
									key="calendar.${fn:substring(forecastForm.periodType,0,1)}" /></b></span>
					</div>
				</div>
			</div>
		</div>
	<div class="row" style="padding-top: 10px; width: 100%">
			<div class="col-sm-3">
			<button type="button" id="compressButton" class="eto-icon-btn" title="${forecastForm.showAllColumns ? hideColTitle : showColTitle}" onclick="handleToggleColumns()" >
			<i class="md-icon" id="md-compress-icon">chevron_${forecastForm.showAllColumns ? 'left' : 'right'}</i></button>
				<label style=" margin: 0rem 3rem 1rem 1rem;font-size: 20px;font-weight: 700;padding-left: 2rem;">
					<span><c:out value="${fcDetailTitle}" /></span>
				</label>
			</div>
			<div class="col-sm-9">
				<div style="float: right; display: flex;">
					<label
						style="float: right; padding-top: 5px; margin: 0rem 0rem 2rem 0rem;margin-right: 12px;"><fmt:message
							key="fc.scrollToPeriod" />&nbsp;</label>
					<div>
						<div class="eto-input">
							<div class="eto-select">
								<div class="eto-select__container">
									<div class="eto-select__field-container">
										<select onchange="scrollToColumn('periodHeader'+this.value);">
											<c:forEach var="period"
												items="${forecastForm.currentTimeline.periods}">
												<option class="period${period.state}"
													${period.state == 'CURRENT' ? 'selected':''}
													value="${period.label}">${period.label}(
													<fmt:formatDate pattern="MMM dd"
														value="${period.startDate}" />)
												</option>
											</c:forEach>
										</select>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<nav class="row eto-tabs" id="tabs-forecast" style="padding-left:5rem;">
  <div class="eto-tabs__container">
    <div class="eto-tabs__scroll">
      <a class="eto-tabs__tab eto-tabs__tab--active" data-tab="#Tab_1" tabindex="0"
     			onclick="javascript:goChangeTab('CURRENT_div','ADJUSTABLE_div','CURRENT','fcTableADJ')"
						alttext="The Current Forecast" name="CURRENT_tab">  
						<span class="eto-tabs__tab-content"><fmt:message key="scplatform.forecast.model.CURRENT" />
						</span><span><c:if test="${!forecastForm.validCurrent}">
							<e2i2:img src="/tab_alert.gif" alt="true"
								onclick="showTabMessage()" />
						</c:if></span>
						<span class="eto-tabs__tab-close"></span>
					</a>
       <a class="eto-tabs__tab" data-tab="#Tab_2" tabindex="0"
						onclick="javascript:goChangeTab('ADJUSTABLE_div','CURRENT_div','ADJUSTABLE','fcTableCUR')"
						alttext="FOR" name="ADJUSTABLE_tab">
						<span class="eto-tabs__tab-content"><fmt:message key="scplatform.forecast.model.ADJUSTABLE" />
						</span><span><c:if test="${!forecastForm.validAdjustable}">
							<img src="skins/e2-modern/images/tab_alert.gif" alt="true"
								onclick="showTabMessage()" />
						</c:if></span>
					</a>
       </div>
  </div>
  <div class="eto-tabs__btns">
    <a class="eto-tabs__btn eto-tabs__btn--backward"></a>
    <a class="eto-tabs__btn eto-tabs__btn--forward"></a>
  </div>
</nav>
		<div class="eto-tab-content">
		<div class="col-lg-12" style="display: flex;padding: 2rem;">
				<div class="col-lg-9" style="padding: 0px;">
						<button type="button" class="eto-btn" id="historyButton" style="margin:4px;"
							onclick="javascript:handleShowHistory()">
							<fmt:message key="button.history" />
						</button>

						<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}">
							<button type="button" class="eto-btn" id="findButton" style="margin:4px;"
								onclick="javascript:goFindOther();">
								<fmt:message key="button.find" />
							</button>
						</c:if>

						<c:if test="${!readOnly}">
							<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Save')}">
								<button type="button" class="eto-btn" id="autoFillButton"
									onclick="javascript:handleAutoFillRows();" style="margin:4px;">
									<fmt:message key="button.autoFill" />
								</button>
								<button type="button" class="eto-btn" id="autoUndoFillButton" style="margin:4px;"
									onclick="javascript:handleUndoValues();">
									<fmt:message key="button.undo" />
								</button>
							</c:if>
							<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Copy')}">
								<button type="button" class="eto-btn" id="copyButton" style="margin:4px;"
									onclick="javascript:goCopy();">
									<fmt:message key="button.copy" />
								</button>
							</c:if>
							<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Delete')}">
								<button type="button" class="eto-btn" id="deleteButton" style="margin:4px;"
									onclick="javascript:goDelete();">
									<fmt:message key="button.delete" />
								</button>
							</c:if>
							<e2i2:buttonbardivider />
							<c:forEach var="pdcEvent" items="${eventSet}">
								<c:if
									test="${pdcEvent.uiMultiTargetAllowed && e2ofn:hasEventAccess(appContext, 'Forecast' , pdcEvent.eventName) && pdcEvent.eventName ne 'Approve'}">
									<button type="button" class="eto-btn" style="margin:4px;"
										id="${pdcEvent.eventName}EventButton"
										disabled="${readOnly ? 'yes':'no'}"
										onclick="javascript:goEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');">
										<fmt:message key="${pdcEvent.uiButtonLabel}" />
									</button>
								</c:if>
							</c:forEach>

							<c:if
								test="${e2ofn:hasEventAccess(appContext, 'Forecast' , 'Approve')}">
								<button type="button" class="eto-btn" id="ApproveEventButton" style="margin:4px;"
									disabled="${readOnly ? 'yes':'no'}"
									onclick="javascript:goEvent('Approve','','');">
									<fmt:message key="button.approve" />
								</button>
							</c:if>
						</c:if>
				</div>
				<div class="col-lg-3" style="padding: 0px;">
				<div style="float: right;padding-right: 3rem;">
					<button type="button" style="font-size: 3rem;"
						title="Open in full screen"
						class="eto-btn eto-btn--link eto-btn--icon-only " id="fullscreen"
						onclick="callfullScreenMethod();">
						<i class="md-icon">fullscreen</i>
					</button>
					<button type="button" style="display: none; font-size: 3rem;"
						title="Exit Full Screen"
						class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
						id="fullscreen_exit" onclick="callExitFullScreen();">
						<i class="md-icon">fullscreen_exit</i>
					</button>
					<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}">
						<c:if
							test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DownloadFile')}">
							<button class="eto-btn eto-btn--link eto-btn--icon-only" style="font-size: 3rem;"
								title="Download" id="downloadButton"
								onclick="javascript:goDownload()">
								<i class="md-icon">file_download</i>
							</button>
						</c:if>
					</c:if>
					<div class="eto-checkbox eto-checkbox-menu"
					id="checkbox-menu-example" style="display: inline;">
					<span role="menu" class="eto-dropdown" style="margin-top: 0px;">
						<button type="button" class="eto-dropdown__toggle eto-icon-btn"
							style="font-size: 2.5rem;" title="Settings">
							<i class="md-icon md-icon--sm">settings</i>
						</button>
						<ul class="eto-dropdown__menu">
							<li name="this-page" data-checkbox-state="checked"
								role="menuitem"><label
								style="padding: 1rem;"
								class="eto-switch" id="grid-compact-control">
									<input class="eto-switch__field" type="checkbox"
									id="condensedCheckBox"> <span class="eto-switch__box"></span>
									<span class="eto-switch__label--off">Default</span><span
									class="eto-switch__label--on">Condensed</span>
							</label></li>
						</ul>
					</span>
				</div>
					</div>
					</div>
					</div>
		
		<div style="width: 100%; padding: 1rem;">
		<section class="eto-tab-content__item" id="Tab_1">
    <div id="CURRENT_div" style="display:block;">
			<jsp:include page="forecastCommon.jsp">
				<jsp:param name="forecastId" value="fcTableCUR" />
				<jsp:param name="forecastModel" value="CURRENT" />
				<jsp:param name="businessProcess" value="Forecast" />
			</jsp:include>
		</div>
  </section>
  
  <section class="eto-tab-content__item" id="Tab_2">
     <div id="ADJUSTABLE_div" style="display:block;">
			<jsp:include page="forecastCommon.jsp">
				<jsp:param name="forecastId" value="fcTableADJ" />
				<jsp:param name="forecastModel" value="ADJUSTABLE" />
				<jsp:param name="businessProcess" value="Forecast_ADJ" />
				<jsp:param name="fixedDisplay" value="${fixedDisplay}" />
				<jsp:param name="percentDisplay" value="${percentDisplay}" />
			</jsp:include>
		</div>
  </section>
		</div>
		</div>
		
		<script>
var forecastTab =  new eto.Tabs({ el: document.querySelector('#tabs-forecast') });
<c:if test="${forecastForm.selectedForecastTab == 'CURRENT'}">
console.log("current");
if(forecastTab!=null)
forecastTab.setActive(0);
</c:if>
console.log("hi :${forecastForm.selectedForecastTab == 'ADJUSTABLE'}");
<c:if test="${forecastForm.selectedForecastTab == 'ADJUSTABLE'}">
if(forecastTab!=null)
forecastTab.setActive(1);
</c:if>
</script>
		<e2i2:footer>
			<div class="buttonbar">
				<e2i2:buttonbar newrowcount="10">
					<e2i2:button id="historyButton"
						onclick="javascript:handleShowHistory()">
						<fmt:message key="button.history" />
					</e2i2:button>
					<e2i2:buttonbardivider />
					<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}">
						<e2i2:button id="findButton" onclick="javascript:goFindOther();">
							<fmt:message key="button.find" />
						</e2i2:button>
					</c:if>

					<c:if test="${!readOnly}">
						<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Save')}">
							<e2i2:button id="autoFillButton"
								onclick="javascript:handleAutoFillRows();">
								<fmt:message key="button.autoFill" />
							</e2i2:button>
							<e2i2:button id="autoUndoFillButton"
								onclick="javascript:handleUndoValues();">
								<fmt:message key="button.undo" />
							</e2i2:button>
						</c:if>
						<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Copy')}">
							<e2i2:button id="copyButton" onclick="javascript:goCopy();">
								<fmt:message key="button.copy" />
							</e2i2:button>
						</c:if>
						<c:if test="${e2ofn:hasAccess(appContext, 'FORECAST', 'Delete')}">
							<e2i2:button id="deleteButton" onclick="javascript:goDelete();">
								<fmt:message key="button.delete" />
							</e2i2:button>
						</c:if>
						<e2i2:buttonbardivider />
						<c:forEach var="pdcEvent" items="${eventSet}">
							<c:if
								test="${pdcEvent.uiMultiTargetAllowed && e2ofn:hasEventAccess(appContext, 'Forecast' , pdcEvent.eventName) && pdcEvent.eventName ne 'Approve'}">
								<e2i2:button id="${pdcEvent.eventName}EventButton"
									disabled="${readOnly ? 'yes':'no'}"
									onclick="javascript:goEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');">
									<fmt:message key="${pdcEvent.uiButtonLabel}" />
								</e2i2:button>
							</c:if>
						</c:forEach>

						<c:if
							test="${e2ofn:hasEventAccess(appContext, 'Forecast' , 'Approve')}">
							<e2i2:button id="ApproveEventButton"
								disabled="${readOnly ? 'yes':'no'}"
								onclick="javascript:goEvent('Approve','','');">
								<fmt:message key="button.approve" />
							</e2i2:button>
						</c:if>
					</c:if>
				</e2i2:buttonbar>
			</div>
		</e2i2:footer>
		<div class="footer">
		<!-- <div class="row" id="scroller" style="overflow-x: scroll; margin: 0px; position: relative;">
	    <div class="col-xs-12"><div id="staticdiv" style="height: 5px; width: 4123px;"></div></div></div> -->
			<div class="row" style="padding-top: 1rem;">
				<div class="col-xs-12 col-sm-6">
					<div class="col-xs-12 col-sm-6">
								<c:if
									test="${!readOnly && e2ofn:hasAccess(appContext, 'FORECAST', 'Save')}">
									<button type="button" class="eto-btn eto-btn--primary" id="saveAndReturnButton"
										onclick="javascript:goSave();">
										<fmt:message key="button.save_return" />
									</button>
									<button type="button" class="eto-btn " id="saveButton"
										onclick="javascript:goSaveAndContinue();">
										<fmt:message key="button.save" />
									</button>
								</c:if>
								<c:if test="${!empty forecastForm.backAction}">
									<button type="button" class="eto-btn" id="backButton"
										onclick="javascript:goBack('${fn:escapeXml(forecastForm.backAction)}');">
										<fmt:message key="button.back" />
									</button>
								</c:if>
							</div>
				</div>
			</div>
		</div>
	</form>
	<%@ include file="../fullModal.jspf"%>
</body>
</html>