<%@ include file="common.jspf"%>
<%@ page import="com.scplatform.pcm.cost.entity.PcmCostRecord" %>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmcostrecordfn.tld" prefix="e2crfn"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<style type="text/css">
.eto-grid-scroll {
	overflow-x: hidden !important;
}

.compact {
	min-height: 26px !important;
	height: 26px !important;
	border-bottom: 0px !important;
}

.eto-messageblock {
	border: none;
	padding: 0px;
}

.vl {
	border-left: 2px solid #d3e2e6;
	height: 40px;
	padding-right: 10px;
	padding-bottom: 10px;
}

input[type=text], div>select {
	order: 2px #c0c3d4 solid;
	height: 30px;
}

.eto-card {
	width: 100%;
	border: 2px #a6a6ab solid;
}

.footer {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
	background-color: white;
	color: white;
	height: 60px;
	border-style: solid;
	border-color: #cddfe4;
	border-top-style: solid;
	border-top-width: 3px;
	z-index: 1;
}

#header-label {
	margin-left: 20px;
}

.eto-messageblock {
	margin: 0 auto;
	display: -ms-flexbox;
	display: flex;
	-ms-flex-align: stretch;
	background: #fff;
}

.eto-messageblock[data-message-type="info"] {
	background: none;
	border-color: none;
}

#expcostelement:onclick {
	border-left: 2px #8282dc solid;
}

a img {
	border: none;
	padding-right: 2rem;
}
table#crTable thead tr th{
  border: 1px solid rgb(205, 223, 228); ;
}
table#crTable tr, td {
  border: 1px solid rgb(205, 223, 228); 
}
</style>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Cost-Detail" />
<title>Cost Record Lane</title>
<c:set var='allowNegativeValues'
	value='${e2ofn:getConfigValue("pcm.sourcingLane.negativeValuesAllowed")}' />
<c:set var="allowPricingScenario"
	value="${e2ofn:getConfigValue('pcm.costRecord.pricing.scenario.enabled')}" />
	
<c:set var="filterPlatformBlank"
	value="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.blank.platform')}" />
	
<c:set var="filterLobBlank"
	value="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.blank.lob')}" />
<c:set var='isCurrencyConversionEnable' value="${e2ofn:getConfigValue('pcm.feature.enable.cr.currency.conversion')}"/>
<c:if test="${allowNegativeValues == null}">
	<c:set var="allowNegativeValues" value="false" />
</c:if>
<c:set var='maxFractionDigits'
	value='${e2ofn:getConfigValue("pcm.costrecord.maxFractionDigits")}' />
<c:set var='minFractionDigits'
	value='${e2ofn:getConfigValue("pcm.costrecord.minFractionDigits")}' />
<c:set var="eventSet"
	value="${e2ofn:allValidEventsForList('Sourcing',costRecordForm.selectedLane.costRecords)}" />
<c:set var='supplierViewOnly'
    value='${e2ofn:getConfigValue("pcm.common.enterprise.data.enable.toSupplier")}' />
<e2ot:eventActionSupport stateModel="Sourcing"
	eventRecords="${costRecordForm.selectedLane.costRecords}">
	<jsp:attribute name="eventRecordId">${eventRecord.costRecordExternalId}</jsp:attribute>
</e2ot:eventActionSupport>
	<script>
		/* set the restricted columns to a global variable */
		var restrictedColumnMap = new Map();
		<c:forEach var="restrictedColumn" items="${costRecordForm.restrictedColumnList}">
		<fmt:message var="columnName" key="${restrictedColumn}" />
		<c:if test = "${fn:startsWith(columnName, '???')}">
				<c:set var="columnName" value="${restrictedColumn}" />
		</c:if>
		restrictedColumnMap.set('${columnName}', '${columnName}');
		</c:forEach>
	</script>
<script type="text/javascript">
var  gridObj="";
function handleContextChanged()
{
	document.getElementById('summaryContextName').value = '';
	document.forms[0].summaryContextKey.value = '';
	
	var contextTypeValue = document.getElementById('summaryContextType').value;
	
	var contextNameTD = document.getElementById('contextNameTD');
	
	var contextNameHtml = "<INPUT type=\"text\" name=\"summaryContextName\" value=\"\" readonly=\"readonly\" id=\"summaryContextName\" class=\"inputField searchField\">";
	
	if(contextTypeValue == "NONE") {
		contextNameHtml = contextNameHtml;
    } else if (contextTypeValue == "BUSINESS") {
    	contextNameHtml = contextNameHtml +
    	"<A href=\"javascript:void 0\" onclick=\"javascript:doFinderPopup('BusinessFinder',document.forms[0].summaryContextKey,'onContextCallback',null,'false')\"><img hspace=\"1\" src=\"./skins/e2-modern/images/search.gif\" border=\"0\"></A>";
    }
	
	contextNameTD.innerHTML = contextNameHtml;
}

function onContextCallback(finderValues)
{
	if(finderValues.length > 0)
	{
		var fr = partial(setSummaryContextNameCallback,finderValues);
		if(canLeavePage(fr))
		{
			setSummaryContextNameCallback(finderValues);
		}
	}
}

function setSummaryContextNameCallback(finderValues)
{
	document.getElementById('summaryContextName').value = finderValues[0][1];
}

function updateSummaryRequired(updateSummary)
{
    if (updateSummary != undefined)
    {
        document.forms[0].summaryCalcRequired.value = updateSummary;
    }                   
}

function handleDataChanged(updateSummary)
{
    document.forms[0].unsavedData.value = 'true';
    var msgArea = document.getElementById('unsavedDataMsg');
    if (msgArea != null)
    {
        msgArea.innerText = '<fmt:message key="info.unsaved_data"/>';
    }       
    updateSummaryRequired(updateSummary);
}

function validateData(ifValidatedCallback)
{
    if (isFieldEmpty('sourcingLaneName'))
    {
         i2uiExpandContainer('slHeader');
         showOkMessageBox('OK','WARN',"<fmt:message key='errors.slane.name_required'/>","<fmt:message key='msg.warn'/>",function() {
         	document.forms[0].sourcingLaneName.focus();                       
         });
         return;
    }

    if (isFieldNumericOrEmpty('forecastOffset') == false)
    {
        i2uiExpandContainer('slHeader');
        showOkMessageBox('OK','WARN',"<fmt:message key='errors.field_decimal_required'/>","<fmt:message key='msg.warn'/>",function() {
       		document.forms[0].forecastOffset.focus();                       
        });
        return;
    }
    
    ifValidatedCallback();
}



function goReadLane()
{
	if (canLeavePage(goReadLaneCallback))
    {
		goReadLaneCallback();
    }
}

function goReadLaneCallback()
{
    document.forms[0].action="viewCostRecord.do";
    document.forms[0].summaryCalcRequired.value = true;        
    document.forms[0].submit(); 
    showWaitBusy();      
}

function goMergeChanges()
{
    document.forms[0].action="mergeCostRecord.do";
    document.forms[0].submit(); 
    showWaitBusy();         
}

function goSaveAndContinue()
{
    validateData(function() {
    	$('#isSaveAction').val('true');
        document.forms[0].action="saveAndContinueCostRecord.do";
        document.forms[0].submit(); 
        showBusy();
    });
}

function goSave()
{   
	validateData(function() {
		$('#isSaveAction').val('true');
    	 document.forms[0].action="saveCostRecord.do";
         document.forms[0].submit(); 
         showWaitBusy();
    });
}

function handleConcurrentUpdate()
{
	showOkCancelMessageBox('OK CANCEL','QUESTION',
			"<fmt:message key='errors.another_user_changed_data'/>",
            "<fmt:message key='msg.warn'/>",
            function() {
            	goMergeChanges();
            },
            function() {
            	document.forms[0].action="viewCostRecord.do";
                document.forms[0].summaryCalcRequired.value = true;        
                document.forms[0].submit(); 
                showBusy();
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

function canLeavePage(yesCallback, noCallback)
{
	if (document.forms[0].unsavedData.value == 'true')
	{
		showYesNoMessageBox('YES NO','WARN',
			   "<fmt:message key='warn.changes_not_saved_yes_no'/>",
			   "<fmt:message key='msg.warn'/>", yesCallback, noCallback);
		return false;
	}
	return true;
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
    document.forms[0].action="home.do";
    document.forms[0].submit();
}

function goViewItem(itemKey,direction)
{
	var fr = function() {
		document.forms[0].action="viewItemCostData.do";
	    document.forms[0].itemViewOperation.value = direction;
	    document.forms[0].selectedItemKey.value = itemKey;
	    document.forms[0].submit();
	    showWaitBusy();
	};
	if (canLeavePage(fr))
    {
		fr();         
    }
}

function goViewBom(bomKey,itemKey,direction)
{
	var fr = partial(goViewBomCallback,bomKey,itemKey,direction);
	if (canLeavePage(fr))
    {
		goViewBomCallback(bomKey,itemKey,direction);         
    }
}

function goViewBomCallback(bomKey,itemKey,direction)
{
    document.forms[0].action="viewItemCostData.do";
    document.forms[0].itemViewOperation.value = direction;
    document.forms[0].selectedItemKey.value = itemKey;    
    document.forms[0].selectedBomKey.value = bomKey;
    document.forms[0].submit(); 
    showWaitBusy();         
}

function goAdd()
{
	$('#operation').val('add');
    document.forms[0].action="addCostRecord.do";
    document.forms[0].submit(); 
    showWaitBusy();  
}

function goDelete()
{
	$('#operation').val('delete');
    document.forms[0].action="deleteCostRecord.do";
    document.forms[0].submit(); 
    showWaitBusy();         
}

function goCopy()
{
	$('#operation').val('copy');
    document.forms[0].action="copyCostRecord.do";
    document.forms[0].submit(); 
    showWaitBusy();         
}

function goCheck(autoCorrect)
{
    document.forms[0].action="validateCostRecord.do";
    document.forms[0].autoCorrect.value = autoCorrect;
    document.forms[0].submit(); 
    showWaitBusy();         
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
    			   "<fmt:message key='errors.field_required'/>",
    			   "<fmt:message key='msg.error'/>");
           return;
       }
       document.forms[0].eventMessage.value = result;
    }


    document.forms[0].eventName.value = eventName;
    document.forms[0].action="processEventCostRecord.do";
    document.forms[0].submit();
    showWaitBusy(); 
    if (ackMessage != null && ackMessage.length > 0)
    {
    	showOkMessageBox('OK','INFO',ackMessage,"<fmt:message key='msg.info'/>");
    }
}

function goLaneEvent(eventName,promptName, ackMessage) {
    var yesCallback = function() {
 	   if (promptName != null && promptName.length > 0)
 	    {
 	       var result = showInputBox('OK','QUESTION',
 	                 promptName,promptName);
 	       if (result == '' || result == undefined)
 	       {
 	           showOkMessageBox('OK','ERROR',"<fmt:message key='errors.field_required'/>","<fmt:message key='msg.error'/>",null);
 	           return;
 	       } else {
 	       		document.forms[0].eventMessage.value = result;
 	       }
 	    }
 	    document.forms[0].eventName.value = eventName;
 	    document.forms[0].action="processEventSourcingLane.do";
 	    document.forms[0].submit();
 	    showWaitBusy(); 
 	    if (ackMessage != null && ackMessage.length > 0)
 	    {    	           	        
 	        showOkMessageBox('OK','WARN',ackMessage,"<fmt:message key='msg.info'/>",null);
 	        
 	    }
    };
    
    var openLanes = ${costRecordForm.hasOpenCostRecords};
    if (eventName == 'Close' && openLanes)
    { 
       showYesNoMessageBox('YES NO','WARN',
			   "<fmt:message key='warn.sa.close_lane'/>",
			   "<fmt:message key='msg.warn'/>", yesCallback);
    } else {
    	yesCallback();
    }
}


function checkTabChange()
{
    return true;
}

function goChangeTab(costType)
{
    document.forms[0].action="viewCostRecordType.do";
    document.forms[0].selectCostRecordType.value=costType;    
    document.forms[0].submit();
    showWaitBusy();         
}

function goRecalcSummary()
{
    document.forms[0].action="viewCostRecordType.do";
    document.forms[0].summaryCalcRequired.value = true;        
    document.forms[0].submit(); 
    showWaitBusy();         
}

function goDownload()
{
    document.forms[0].action="downloadSourcingLane.do";        
    document.forms[0].submit();             
}

function goBack(action)
{
	var goBackCallback = function() {
		document.forms[0].action=action;    
	    document.forms[0].preserveSearchValues.value="true";    
	    document.forms[0].submit(); 
	    showWaitBusy();
	}
	if (canLeavePage(goBackCallback))
    {
		goBackCallback(action);
    }
}

function goLaneChanged(ctrl,resetValue)
{
	var noCallback = function() {
		ctrl.value = resetValue;
	}
	var yesCallback = function() {
		document.forms[0].selectedLaneKey.value = "";
	    document.forms[0].action="changeLaneCostRecord.do";
	    document.forms[0].submit();
	    showWaitBusy();
	}
	if(canLeavePage(yesCallback,noCallback)) {
		yesCallback();
	}
}


// Does the same as goLaneChange but we don't reset the lane key
function goJumpToLane(ctrl,resetValue,key)
{
    if (ctrl.value == resetValue || isValueEmpty(ctrl.value))
    {
        return false;
    }
    var yesCallback = function() {
    	document.forms[0].selectedLaneKey.value = key;
        document.forms[0].action="changeLaneCostRecord.do";    
        document.forms[0].submit(); 
        showWaitBusy();
    }
    var noCallback = function() {
    	ctrl.value = resetValue;
    }
	if(canLeavePage(yesCallback,noCallback)) {
		yesCallback();
	}
}

function onSupplierCallback(finderValues)
{
    if(finderValues.length > 0)
    {
        if (isValueEmpty(finderValues[0][1]) == false)
        {   
            var optIndex = findOption(document.forms[0].supplierKey.options,finderValues[0][0]); 
            if (optIndex < 0)
            {                       
                var opt = document.createElement("OPTION");     
                opt.text = finderValues[0][1];
                opt.value = finderValues[0][0];
                opt.selected = true;
                document.forms[0].supplierKey.options.add(opt);         
            } 
            else
            {
                document.forms[0].supplierKey.options[optIndex].selected = true;
            }   
            if (document.forms[0].supplierKey.onchange)
            {
                document.forms[0].supplierKey.onchange();
            }           
        }
    }
}


var providerRow = -1;
function clearCostProvider(rowId)
{
    document.getElementById('provider'+rowId).value = '';
    document.getElementById('providerName'+rowId).value = '';   
}

function showProviderFinder(rowId)
{
    providerRow = rowId;
    doFinderPopup('SupplierNameFinder',
                document.getElementById('provider'+rowId),'onProviderCallback',null,'false');   
}

function onProviderCallback(finderValues)
{
    if(finderValues.length > 0)
    {
        if (isValueEmpty(finderValues[0][1]) == false)
        {   
            document.getElementById('providerName'+providerRow).value = finderValues[0][1];
        }
    }
}

function handlePushDownValue(startId, fieldId, classname)
{
    if (pushCostValueDownIndexed(startId,fieldId,classname))
    {
        handleDataChanged();
    }
}

function pushCostValueDownIndexed(startIndex,fieldid,classname,prevValues,predicate,calculatedValue)
{	
	var rc = false;
	if (typeof(startIndex) == 'string')
	{
		startIndex = parseInt(startIndex);
	}
    var dataitem = document.getElementById(fieldid.replace("{0}",startIndex));
    if (dataitem == null)
    {
       return rc;
    }
    var value = dataitem.value;
    var idx = startIndex+1;
    var elems = document.getElementsByClassName(classname.replace("{0}",idx));
    var undo = isObject(prevValues);
    while (elems != undefined && elems.length == 1)
    {
    	var downField = elems[0];
        if (downField.readOnly == false && downField.disabled == false)
        {
            if (undo)
            {
            	prevValues[downField.id] = downField.value;
            }        	
            downField.value = value;
            if (predicate != null) {
            	predicate(downField);
            }
            if(calculatedValue!=null){
            	calculatedValue(idx);
            }
            rc = true;
        }        
        idx++;
        elems = document.getElementById(classname.replace("{0}",idx));
    }    
    return rc;
}


function calcBlends(value, costElementType, rowId)
{   
    var validCallback = function() {
	    <c:forEach var="ce" items="${costRecordForm.selectedCostElementDetails}">
	    var result = 0.0;
	    var total = 0.0;
	    if (costElementType == '${ce.key}')
	    {
	        <c:forEach var="ced" items="${ce.value}">
	        var blendField = document.getElementById('blend'+rowId+'${ced}');
	        var amountField = document.getElementById('value'+rowId+'${ced}');
	        var blend = roundDecimal(blendField.value,2);
	        var amount = roundDecimal(amountField.value,6);
	        if (isNaN(blend) == false)
	        {
	            blendField.value = blend;                                   
	        }
	        if (isNaN(amount) == false)
	        {
	            amountField.value = amount;                                 
	        }                        
	        result +=  (roundDecimal(blend / 100.00, 4) * amount);
	        total += blend;  
	        </c:forEach>        
	        document.getElementById('costValue'+rowId+'${ce.key}').value = roundDecimal(result,6).toFixed(6);
	        document.getElementById('blendTotal'+rowId+'${ce.key}').value = roundDecimal(total,2).toFixed(2);
	    }   
	    </c:forEach>        
    }
    checkNumericField(value,false,validCallback);
}

function disableCostPage()
{
    $("#crTable input").attr("readonly",true);  
    $("#crTable img").attr("disabled",true);
    var buttons = $("#crButtonBar .buttonBorder");
    for (i=0; i < buttons.length; i++)
    {
        if (buttons[i].id != "")
        {
            setButtonEnabled(buttons[i].id,false);
        }
    } 
}

function disableLockedRows()
{       
    $(".lockedRow :input").addClass("lockedRow");   
    $(".lockedApprovedRow :input").addClass("lockedApprovedRow");   
}

function addToolTips()
{
  var total = $('select option').length;
  var cur;
  for ( var i = 0; i < total; i++ )
  {
        cur = $('select option:eq(' + i + ')');
        cur.attr( 'title', cur.text() );
  }
}

function handleLineButtons()
{
    var opCounts = getSelectedOperationCounts();        
    if (opCounts.TotalChecked > 0)
    {
        setButtonEnabled("deleteButton", opCounts.Delete == opCounts.TotalChecked);
        setButtonEnabled("copyButton",true);
    }
    else
    {
        setButtonEnabled("deleteButton", false);
        setButtonEnabled("copyButton",false);       
    }
}

function handleSearchContainerToggle(item, delta)
{
   if (item == 'slHeader')
   {
      document.forms[0].headerAreaCollapsed.value = (delta < 0);
      resizeArea();
   }
}

function showLineMessage(line,message)
{   
    var title = "<fmt:message key="msg.info"/>";
    showModalMessageBox(title,message);
}

function resizeArea()
{
    <c:choose>
    <c:when test="${fn:contains(header['USER-AGENT'],'MSIE') == false}">
    var area = $("#crTableBody");
    var pad = 120;
    </c:when>
    <c:otherwise>
    var area = $("#crTable");
    var pad = 120;
    </c:otherwise>
    </c:choose>
   /*  if (area != null)
    {
        var h = $("#slHeader").height() + pad;
        var b = $(document.body).height();
        area.height(b - h);
    } */
}
function handleFilterRecords(state,filter)
{
	var rowQuery = 'tr.cr-'+state;  
	if(filter)
		$('input[name="filterState(CLOSED)]"').attr("checked","checked");
    if ($('input[name="filterState(CLOSED)]"').attr("checked")==true)
    {
        $(rowQuery).attr("style","display:none");
    }
    else
    {
    	 $(rowQuery).attr("style","display:'' ");
    }
}
function displayTotalUSD(){
    var isConvertValueInUSD = document.getElementById("convertValueInUSD").checked;
    document.forms[0].convertValueInUSD.value = isConvertValueInUSD  ;
     if(isConvertValueInUSD==true){
     $(".crUSDTotal").show();
     } else {
     $(".crUSDTotal").hide();
     }
}
function scrollToRecord(crId)
{ 
    var table = $('#crTable');
    var tablebody = $('#crTableBody');
    var row = $('#'+crId,'td:parent');
    if (row != null && row.length > 0)
    {
        //row.css('background-color','#fff6a6');
        //row.children(':checkbox:first').attr('checked',true);
        row[0].scrollIntoView(true);                
        var offset = (row.position().top - table.position().top);               
        // If the offset is less then the header we can scroll down to show the row
        if (offset < 45);
        {
            table.scrollTop(table.scrollTop()-45);
        }
    } 
     
}

function setInvalidTabColor(tabName)
{
    var $tab = $("a[name='"+tabName+"']");
    var color = "#e6e6e6";
    if ($tab.attr('id') == 'tabSelected')
    {
        color = "#d6d6d6";
    }
    $tab.css('color','#999999');
    $tab.css('background-color',color);
    $tab.parent().css('background-color',color);    
    $tab.parent().prev().css('background-color',color); 
    $tab.parent().next().css('background-color',color);
}

function init()
{
    i2uiToggleContentUserFunction = 'handleSearchContainerToggle';
    <c:if test="${param.headerAreaCollapsed}">
    i2uiCollapseContainer('slHeader');  
    </c:if>
        
    disableLockedRows();
    disableAllLineEventButtons(true);
    handleLineEventButtons();
    handleLineButtons();
    addToolTips();
    <c:if test="${costRecordForm.filterStates.CLOSED == 'true'}">
    handleFilterRecords('CLOSED','true');
    </c:if> 
    i2uiManageTabs('headerTabSet',document.body.offsetWidth - 40);
        
    try
    {   
        i2uiCollapseTreeTable('summaryDetails',1,null,0,true);  
    }
    catch(e)
    {
    }
    resizeArea();
    <c:if test="${costRecordForm.lockError}">
    handleConcurrentUpdate();
    </c:if> 
    <c:if test="${!empty param.scrollToRecord}">
        scrollToRecord('${fn:escapeXml(param.scrollToRecord)}');
    </c:if>
    <c:if test="${costRecordForm.showAllTabs}">
    <c:forEach var="costType" items="${costRecordForm.costTypes}">
    <c:if test="${costRecordForm.isValidCostType[costType.costTypeKey] == false}">
    setInvalidTabColor('${costType.costTypeKey}');  
    </c:if>       
    </c:forEach>
    <c:if test="${costRecordForm.isValidCostType[costRecordForm.selectCostRecordType] == false}">
    disableCostPage();
    </c:if>
    </c:if>
	<c:forEach var="exculdedType" items="${costRecordForm.excludeCostTypeList}">
		<c:if test="${exculdedType eq costRecordForm.selectCostRecordType}">
			setButtonEnabled("addButton",false);
		</c:if>  
	</c:forEach>
	<c:forEach var="crExculdedType" items="${costRecordForm.includeExcludeCostRecordList}">
		<c:if test="${crExculdedType eq costRecordForm.selectCostRecordType || crExculdedType eq 'ALL'}">
			setButtonEnabled("addButton",false);
		</c:if>
	</c:forEach>

	$("#crTable").css({'table-layout': 'auto'});
	removeRestrictedColumns();
	preInitialize();
	closeBusyDialog();
	displayTotalUSD();
}
function preInitialize(){
	<c:if test="${costRecordForm.selectCostRecordType != 'SUMMARY'}">
	showdropdownbystatus();
	showdropdown();
	showdropdownbyEffectively();
	showdropdownbypricingscenario();
	showdropdownbyplatform();
	showdropdownbylob();
	if(uniquecostRecordStatus.length<=1){
		$("#statuscheck").hide();
		}
	if(uniquecostRecordEffectively.length<=1){
		$("#statuseffective").hide();
		}
	if(uniquecostrecordpricingscenario.length<=1){
		$("#pricingscenario").hide();
		}
	if(uniquecostRecordPlatform.length <= 1){
		$("#statusplatform").hide();
	}
	
	if(uniquecostRecordLob.length <= 1){
		$("#statuslob").hide();
	}
	/* Hide Filter items by default */
	checkAllDefaults();
	$("#removeexpand").hide();
	</c:if>;
}

<!-- Initialize Column Groups -->
var columnGroups1 = {};
addColumnGroup('default','Default');
var crcol_status1="<fmt:message key="cr.status"/>";
addColumn('default','status',crcol_status1);
addColumn('default','history','History');
var crcol_frmDate1="<fmt:message key="cr.fromDate"/>";
addColumn('default','fromDate',crcol_frmDate1);
var crcol_toDate1="<fmt:message key="cr.toDate"/>";
addColumn('default','toDate',crcol_toDate1);
var crcol_total1="<fmt:message key="cr.costTotal"/>";
addColumn('default','crTotal',crcol_total1);
var crcol_description="<fmt:message key="cr.description"/>";
addColumn('default','description',crcol_description);
var crcol_provider ="<fmt:message key="cr.provider"/>";
addColumn('default','provider',crcol_provider);
var crcol_pricingScenario="<fmt:message key="cr.pricingScenario"/>";
addColumn('default','pricingScenario',crcol_pricingScenario);
var crcol_reasonCode="<fmt:message key="cr.reasonCode"/>";
addColumn('default','reasonCode',crcol_reasonCode);
var crcol_systemAction="<fmt:message key="cr.systemAction"/>";
addColumn('default','systemAction',crcol_systemAction);
var crcol_projectName="<fmt:message key="cr.projectName" />";
addColumn('default','projectName',crcol_projectName);

<c:if test="${allowPricingScenario}">
var crcolgrp_range="<fmt:message key="cr.columngroup.range"/>";
addColumnGroup('crrange',crcolgrp_range);

var crrange_active="<fmt:message key="cr.costRecordRange.active" />";
addColumn('crrange','rangeActive',crrange_active);
var crrange_from="<fmt:message key="cr.costRecordRange.from"/>";
addColumn('crrange','rangeFrom',crrange_from);
var crrange_to="<fmt:message key="cr.costRecordRange.to"/>";
addColumn('crrange','rangeTo',crrange_to);
</c:if>

var crcolgrp_flex="<fmt:message key="cr.columngroup.flex"/>";
addColumnGroup('crFlex',crcolgrp_flex); 

<c:forEach var="costElement" items="${costRecordForm.selectedCostElements}">
var columnGroupName1="CET${costElement.costElementType}";
var columnName1="${costElement.costElementKey}";
var displayname1="${costElement.costElementKey}";
	addColumn(columnGroupName1,columnName1,displayname1);
</c:forEach>

<c:forEach var="attributeDefn" items="${costRecordForm.flexAttributeDefinitions}">		
		<c:set var="attributeMessage" value="flex.cost.${attributeDefn.associatedAttribute}" />
			<fmt:message key="${attributeMessage}" var="crflexAttr" />
				addColumn('crFlex','${attributeDefn.associatedAttribute}','${crflexAttr}');
				 <c:if test="${attributeDefn.associatedAttribute == 'numberAttribute1'}">
				     <fmt:message key="cr.flex.xlob.platform" var="crcol_xlobFGPlatform" />
				     addColumn('crFlex','xlobFGPlatform','${crcol_xlobFGPlatform}');
				     <fmt:message key="cr.flex.xlob.lob" var="crcol_fgLOB" />
	                     addColumn('crFlex','xlobFGLOB','${crcol_fgLOB}');
	                     <fmt:message key="cr.isValidXLOB" var="isValidXLOB" />
	                         addColumn('crFlex','isValidXLOBFG','${isValidXLOB}');
              </c:if>
</c:forEach>

function addColumn(columnGroupName,columnName,displayname) {
	if (typeof columnGroups1[columnGroupName] === 'undefined') {
		var displaName1=displayname;
		if(columnGroupName=='default'){
			displayname1="";}
    	if(displayname=='MATERIAL'){
    		displayname1="<fmt:message key="cr.columngroup.MATERIAL"/>";
    	}
    	if(displayname=='TRANSFORMATION')
    		{
    		displayname1="<fmt:message key="cr.columngroup.TRANSFORMATION"/>"; 
    		}
    	if(displayname=='FIXED')
    	{
    	displayname1="<fmt:message key="cr.columngroup.FIXED"/>"; 
    	}
        columnGroups1[columnGroupName] = {"columns":[],"hidden":false,"displayName":displayname1};
    }
    columnGroups1[columnGroupName].columns.push({"name":columnName,"displayName":displayname,"hidden":false});
}

function addColumnGroup(columnGroupName,columnGroupDisplayName) {
     columnGroups1[columnGroupName] = {"columns":[],"hidden":false,"displayName":columnGroupDisplayName};
};

function checkAllDefaults(){
<c:forEach var="hideColumn"
	items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.columns')}">
	hideCostRecordColumnsByDefault('${hideColumn}');
</c:forEach>
<c:forEach var="hideStatus"
	items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.status')}">
	if (!(jQuery.inArray('${hideStatus}', uniquecostRecordStatus) == -1)) {
		togStatus('${hideStatus}');
	 }
</c:forEach>
<c:if test="${allowPricingScenario}">
	<c:forEach var="hidePS"
		items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.pricingscenario')}">
	if(!(jQuery.inArray('${hidePS}', uniquecostrecordpricingscenario) == -1)){
		togglepricingscenario('${hidePS}');
		}
	</c:forEach>
</c:if>
<c:forEach var="hideEF"
	items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.effectivity')}">
if(!(jQuery.inArray('${hideEF}', uniquecostRecordEffectively) == -1)){
	toggleEffective('${hideEF}');
}
</c:forEach>
}

function hideCostRecordColumnsByDefault(hideColumn){
	if(hideColumn.includes(":")) {
		var hideColmun1=hideColumn.split(":");
		var id="";
		if(hideColmun1[0]=="MATERIAL" || hideColmun1[0]=="TRANSFORMATION" || hideColmun1[0]=="FIXED"){
		 id="CET"+hideColmun1[0]+"_"+hideColmun1[1];
		 }
		else {
			id=hideColmun1[0]+"_"+hideColmun1[1];
		}
		var present=false;
		$.each(columnGroups1,function(i,val){
			var id1=id.split("_");
			if(i==id1[0]){
				$.each(val.columns,function(i1,val1){
					if(val1.name==id1[1]){
						present=true;
					}
					});
			}
				if(present)
					return present;
		});
		if(present) {
			// toggleColumnGroup(id);
		}
	}
	else{
		var id=hideColumn;
		if(hideColumn=="MATERIAL" || hideColumn=="TRANSFORMATION" || hideColumn=="FIXED"){
			 id="CET"+id;
			 }
		$.each(columnGroups1,function(i,val){
			if(i==id){
				toggleAllColumnGroup(id);
			}
	});
		}
	}
var costRecordStatus={};
var uniquecostRecordStatus=[];
var costRecordEffectively={};
var uniquecostRecordEffectively=[];
var costrecordpricingscenario={};
var uniquecostrecordpricingscenario=[];
var costRecordPlatform={};
var uniquecostRecordPlatform=[];
var costRecordLob={};
var uniquecostRecordLob=[];
var limitedCosttypeForLobPlatform = [];


var platformIdCounter = 1;
var lobIdCounter = 1;

var platformIdMapping = [];
var lobIdMapping = [];

<c:forEach var="costType" items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.lob.platform.costtype')}">
limitedCosttypeForLobPlatform.push("${costType}")
</c:forEach>
<c:forEach var="cr" items="${costRecordForm.sortedCostRecords}" varStatus="rowCount">
if('${costRecordForm.selectCostRecordType}'=='${cr.costType}'){
addCostRecordByStatus('${rowCount.count}','${cr.status}','${cr.costType.costTypeName}');
var creffectivitytense= "<fmt:message key="cr.filter.byeffectivity.${cr.costRecordEffectivity}"/>";
var creffectivitydefault="<fmt:message key="cr.filter.byeffectivity.DEFAULT"/>";
addcostRecordEffectively('${rowCount.count}','${cr.costRecordEffectivity}','${creffectivitytense}');
<c:if test="${e2crfn:isCostRecordHiddenForEffectivity(costRecordForm.hideCostRecordEffectiveBefore,cr)}">
addcostRecordEffectively('${rowCount.count}','DEFAULT','${creffectivitydefault}'); </c:if> 
 if('${cr.pricingScenario}'!=null && '${cr.pricingScenario}'!=""){
	 addCostRecordByPricingScenario('${rowCount.count}','${cr.pricingScenario}'); }
 if(jQuery.inArray('${cr.costType}', limitedCosttypeForLobPlatform)!= -1){
	 
	 addCostRecordByPlatform('${rowCount.count}','${costRecordForm.getCostData(cr.costRecordExternalId).xlobPlatform}','${cr.costType.costTypeName}');
	 addCostRecordByLob('${rowCount.count}','${costRecordForm.getCostData(cr.costRecordExternalId).fgLOB}','${cr.costType.costTypeName}'); 
 }
 }
</c:forEach>

function addCostRecordByPricingScenario(rowcount,pricingscenario){
	var pricingscenario1=pricingscenario;
	if(pricingscenario.includes(" "))
	{
		pricingscenario1=pricingscenario.split(" ");
		pricingscenario=pricingscenario1[0];
		}
		costrecordpricingscenario[rowcount]={"pricingscenario":pricingscenario};
		adduniqueCostRecordByPricingScenario(pricingscenario);
}
	
function adduniqueCostRecordByPricingScenario(pricingscenario){
	if(jQuery.inArray(pricingscenario, uniquecostrecordpricingscenario) == -1){
		uniquecostrecordpricingscenario.push(pricingscenario);
		}
}

function showdropdownbypricingscenario(){
	$("#addpricingscenario").append('<ul  style="list-style-type: none;padding-left: 5px">');
	$("#addpricingscenario").append('<li onclick="showAllpricingscenarioColumns()" class="cr-criteria-showall"><a href=""><fmt:message
			key="label.showAll"/></a></li>');
	$.each(uniquecostrecordpricingscenario, function(i,val) {
		$("#addpricingscenario").append('<li  id="'+val+'" onclick="togglepricingscenario(this.id)"><a><i id="'+val+'i" class="fa fa-square-o"></i>&nbsp;'+val+' based</a></li>');
		});
}

function showAllpricingscenarioColumns(){
	$("#pricingscenarioi").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
	$.each(uniquecostrecordpricingscenario, function(i, val) {
		var id2="#"+val+"i";
		var class2=$(id2).attr('class');
		if(class2.includes("fa-check-square-o")){
			$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
			$.each(costrecordpricingscenario, function(i1, val1) {
				if(val==val1.pricingscenario){
					var  id3="#"+"crrow_"+i1;
					if(checkActiveFilters("#"+"crrow_"+i1, "Pricing"))
					$(id3).show();
					}
				});
			}
		});
}
	
function togglepricingscenario(id){
	var pricingscenario1=id;
	if(id.includes(" ")){
		pricingscenario1=id.split(" ");
		id=pricingscenario1[0];
		}
	var id2="#"+id+"i";
	var class2=$(id2).attr('class');
	if(class2.includes("fa-square-o")){
		$(id2).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
		$.each(costrecordpricingscenario, function(i1, val) {
			if(id==val.pricingscenario){
				var  id3="#"+"crrow_"+i1;
				$(id3).attr('activeFilter', $(id3).attr('activeFilter')+'Pricing;')
				$("#"+"crrow_"+i1).hide();
				}
			});
		}
	else{
		$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		$.each(costrecordpricingscenario, function(i1, val) {
		if(id==val.pricingscenario){
			if(checkActiveFilters("#"+"crrow_"+i1, "Pricing"))
			$("#"+"crrow_"+i1).show();
			}
		});
		}
	var bool=false;
	$.each(uniquecostrecordpricingscenario,function(i,val1){
		var id2="#"+val1+"i";
		var class12=$(id2).attr('class');
	    if(class12.includes("fa-square-o")){
				 bool=true;
		}
	    else{
	    	bool=false;
	    	return bool;
	    	}
	    });
	if(bool)
	{
		$("#pricingscenarioi").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		}
	else{
		$("#pricingscenarioi").removeClass('fa fa-caret-down cr-caret').addClass('fa fa-filter cr-filter-combo');
		}
	} 
	
function addcostRecordEffectively(rowcount,costRecordEffectivity,creffectivitytense){
	costRecordEffectively[rowcount]={"costRecordEffectivity":costRecordEffectivity,"creffectivitytense":creffectivitytense};
	adduniquecostRecordEffectively(costRecordEffectivity);
}

function adduniquecostRecordEffectively(costRecordEffectivity){
	if(jQuery.inArray(costRecordEffectivity, uniquecostRecordEffectively) == -1){
		uniquecostRecordEffectively.push(costRecordEffectivity);
		}
}

function showdropdownbyEffectively(){
	$("#addeffectivetypes").append('<li onclick="showAllEffectivesColumns()" class="cr-criteria-showall"><a><fmt:message key="label.showAll"/></a></li>');
	$.each(uniquecostRecordEffectively, function(i,val) {
		$("#addeffectivetypes").append('<li  id="'+val+'" onclick="toggleEffective(this.id)"><a><i id="'+val+'i" class="fa fa-square-o"></i>&nbsp;'+val+'</a></li>');
		});
}
	
function showAllEffectivesColumns(){
	$("#statuseffectivei").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
	$.each(uniquecostRecordEffectively, function(i, val) {
		var id2="#"+val+"i";
		var class2=$(id2).attr('class');
		if(class2.includes("fa-check-square-o")){
			$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
			$.each(costRecordEffectively, function(i1, val1) {
				if(val==val1.costRecordEffectivity){
					var  id3="#"+"crrow_"+i1;
					if(checkActiveFilters("#"+"crrow_"+i1, "Effective"))
					$(id3).show();
					}
				});
			}
		});
}

function toggleEffective(id){
	var id2="#"+id+"i";
	var class2=$(id2).attr('class');
	if(class2.includes("fa-square-o")){
		$(id2).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
		$.each(costRecordEffectively, function(i1, val) {
			if(id==val.costRecordEffectivity){
				var  id3="#"+"crrow_"+i1;
				$(id3).attr('activeFilter', $(id3).attr('activeFilter')+'Effective;')
				$("#"+"crrow_"+i1).hide();
				}
			});
		}
	else{
		$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		$.each(costRecordEffectively, function(i1, val) {
			if(id==val.costRecordEffectivity){
				if(checkActiveFilters("#"+"crrow_"+i1, "Effective"))
				$("#"+"crrow_"+i1).show();
				}
			});
		}
	var bool=false;
	$.each(uniquecostRecordEffectively,function(i,val1){
		var id2="#"+val1+"i";
		var class12=$(id2).attr('class');
		if(class12.includes("fa-square-o")){
			bool=true;
			}
		else{
			bool=false;
			return bool;
			}
		});
	if(bool)
	{
		$("#statuseffectivei").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		}
	else{
		$("#statuseffectivei").removeClass('fa fa-caret-down cr-caret').addClass('fa fa-filter cr-filter-combo');
		}
}

function addCostRecordByStatus(rowcount,status,costtype){
	costRecordStatus[rowcount]={"status":status,"costtype":costtype};
	addUniqueCostRecordbystatus(costtype,status);
}
	
function addUniqueCostRecordbystatus(costtype,status){
	if (jQuery.inArray(status, uniquecostRecordStatus) == -1) {
		uniquecostRecordStatus.push(status);
		}
}


function addCostRecordByPlatform(rowcount, platform, costtype){
	var platformId;
	if(platform == "" || platform == null){
		platform = '${filterPlatformBlank}';
		platformId = 'platformEmptyLabelId';
	}
	else{
		if(! (platform in platformIdMapping))
			platformIdMapping[platform] = 'platformDropdownId'  +  platformIdCounter++;
		platformId = platformIdMapping[platform];
	}
	costRecordPlatform[rowcount]={"platform":platformId, "costtype":costtype};
	addUniqueCostRecordByPlatform(platform, costtype, platformId);
}

function isObjectPresentPlatform(array, value)
{
	return array.find((ele) => ele.platform == value)
}

function addUniqueCostRecordByPlatform(platform, costtype, platformId)
{
	if(!isObjectPresentPlatform(uniquecostRecordPlatform, platform)){
		uniquecostRecordPlatform.push({"platform" :platform, "platformId":platformId});
	}
}

function isObjectPresentLob(array, value)
{
	return array.find((ele) => ele.lob == value)
}

function addCostRecordByLob(rowcount, lob, costtype){
	var lobId;
	if(lob == "" || lob == null){
		lob = '${filterLobBlank}';
		lobId = 'lobEmptyLabelId';
	}
	else{
		if(! (lob in lobIdMapping)){
			lobIdMapping[lob] = 'lobDropdownId'  +  lobIdCounter++;
		}
		lobId = lobIdMapping[lob];
	}
	costRecordLob[rowcount]={"lob":lobId, "costtype":costtype};
	addUniqueCostRecordByLob(lob, costtype, lobId);
}

function addUniqueCostRecordByLob(lob, costtype, lobId)
{
	if(!isObjectPresentLob(uniquecostRecordLob, lob)){
		uniquecostRecordLob.push({"lob" :lob, "lobId":lobId});
	}
}

function showdropdownbystatus(){
		 $("#addstatustypes").append('<li onclick="showAllStatusColumns()" class="cr-criteria-showall"><a href=""><fmt:message
	               key="label.showAll"/></a></li>');
		$.each(uniquecostRecordStatus, function(i1, val) {
			 $("#addstatustypes").append('<li  id="'+val+'" onclick="togStatus(this.id)"><a><i id="'+val+'i" class="fa fa-square-o"></i>&nbsp;'+val+'</a></li>');
			 });
}


function showdropdownbyplatform(){
	 $("#addplatformtypes").append('<li onclick="showAllPlatformColumns()" class="cr-criteria-showall"><a href=""><fmt:message
             key="label.showAll"/></a></li>');
	 $.each(uniquecostRecordPlatform, function(i1, val) {
		 $("#addplatformtypes").append('<li  id="'+val.platformId+'" onclick="togPlatform(this.id)"><a><i id="'+val.platformId+'i" class="fa fa-square-o"></i>&nbsp;'+val.platform+'</a></li>');
		 });
}

function showdropdownbylob(){
	 $("#addlobtypes").append('<li onclick="showAllLobColumns()" class="cr-criteria-showall"><a href=""><fmt:message
            key="label.showAll"/></a></li>');
	 $.each(uniquecostRecordLob, function(i1, val) {
		 $("#addlobtypes").append('<li  id="'+val.lobId+'" onclick="togLob(this.id)"><a><i id="'+val.lobId+'i" class="fa fa-square-o"></i>&nbsp;'+val.lob+'</a></li>');
		 });
}

function checkActiveFilters(id, filter)
{
	
	var filtersActive = $(id).attr('activeFilter').split(';').filter(item => item !== "");
	filtersActive = filtersActive.filter(item => item !== filter);
	
	if(filtersActive.length == 0){
	   $(id).attr('activeFilter', '');
		return true;
		}
	else{
		$(id).attr('activeFilter', filtersActive.join(";")+';');
		return false;
	}
	
}

function togStatus(id){
	var id2="#"+id+"i";
    var class2=$(id2).attr('class');
	 if(class2.includes("fa-square-o")){
		 $(id2).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
	$.each(costRecordStatus, function(i1, val) {
		if(id==val.status){
			
			var  id3="#"+"crrow_"+i1;
			$(id3).attr('activeFilter', $(id3).attr('activeFilter')+'Status;')
		$("#"+"crrow_"+i1).hide();
		}
	});
	}
	 else{
		 $(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		 $.each(costRecordStatus, function(i1, val) {
				if(id==val.status){
				if(checkActiveFilters("#"+"crrow_"+i1, "Status"))
				$("#"+"crrow_"+i1).show();
				}
			});
	 }
	 var bool=false;
	 $.each(uniquecostRecordStatus,function(i,val1){
		 var id2="#"+val1+"i";
		 var class12=$(id2).attr('class');
		 if(class12.includes("fa-square-o")){
			 bool=true;
		 }
		 else{
			 bool=false;
			 return bool;
		 }
		 });
	 if(bool)
		 {
		 $("#statuschecki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		 }
	 else{
		 $("#statuschecki").removeClass('fa fa-caret-down cr-caret').addClass('fa fa-filter cr-filter-combo');
	 }
}

function togPlatform(id){
	var id2 = "#"+id+"i";
	var class2=$(id2).attr('class');
	if(class2.includes("fa-square-o")){
		$(id2).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
		$.each(costRecordPlatform, function(i1, val){
			if(id==val.platform){
				var id3 = "#"+"crrow_"+i1;
				$(id3).attr('activeFilter', $(id3).attr('activeFilter')+'Platform;')
				$("#"+"crrow_"+i1).hide();
			}
		});
	}
	else{
		$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		 $.each(costRecordPlatform, function(i1, val) {
				if(id==val.platform){
				if(checkActiveFilters("#"+"crrow_"+i1, "Platform"))
				$("#"+"crrow_"+i1).show();
				}
			});
	}
	var bool=false;
	 $.each(uniquecostRecordPlatform,function(i,val1){
		 
		 var id2="#"+val1.platformId+"i";
		 var class12=$(id2).attr('class');
		 if(class12.includes("fa-square-o")){
			 bool=true;
		 }
		 else{
			 bool=false;
			 return bool;
		 }
		 });
	 if(bool)
		 {
		 $("#platformchecki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		 }
	 else{
		 $("#platformchecki").removeClass('fa fa-caret-down cr-caret').addClass('fa fa-filter cr-filter-combo');
	 }
}


function togLob(id){
	var id2 = "#"+id+"i";
	var class2=$(id2).attr('class');
	if(class2.includes("fa-square-o")){
		$(id2).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
		$.each(costRecordLob, function(i1, val){
			if(id==val.lob){
				var id3 = "#"+"crrow_"+i1;
				$(id3).attr('activeFilter', $(id3).attr('activeFilter')+'Lob;')
				$("#"+"crrow_"+i1).hide();
			}
		});
	}
	else{
		$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		 $.each(costRecordLob, function(i1, val) {
				if(id==val.lob){
				if(checkActiveFilters("#"+"crrow_"+i1, "Lob"))
				$("#"+"crrow_"+i1).show();
				}
			});
	}
	var bool=false;
	 $.each(uniquecostRecordLob,function(i,val1){
		 var id2="#"+val1.lobId+"i";
		 var class12=$(id2).attr('class');
		 if(class12.includes("fa-square-o")){
			 bool=true;
		 }
		 else{
			 bool=false;
			 return bool;
		 }
		 });
	 if(bool)
		 {
		 $("#lobchecki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		 }
	 else{
		 $("#lobchecki").removeClass('fa fa-caret-down cr-caret').addClass('fa fa-filter cr-filter-combo');
	 }
}

function showAllStatusColumns(){
	 $("#statuschecki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
	$.each(uniquecostRecordStatus, function(i, val) {
		// $("#addstatustypes").append('<li  id="'+i1+'" onclick="togStatus(this.id)"><i id="'+i1+'i" class="fa fa-square-o">&nbsp;&nbsp;'+i1+'</li>');
			var id2="#"+val+"i";
			 var class2=$(id2).attr('class');
			 if(class2.includes("fa-check-square-o")){
				 $(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
			$.each(costRecordStatus, function(i1, val1) {
				if(val==val1.status){
					var  id3="#"+"crrow_"+i1;
				if(checkActiveFilters(id3, "Status"))
				$(id3).show();
				}
			});
			}
	});
}



function showAllPlatformColumns(){
	$("platformchecki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
	$.each(uniquecostRecordPlatform, function(i, val) {
		var id2 = "#" + val.platformId + "i";
		var class2 = $(id2).attr('class');
		if(class2.includes('fa-check-square-o')){
			$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
			$.each(costRecordPlatform, function(i1, val1){
				if(val.platformId == val1.platform){
					var id3 = "#" + "crrow_"+i1;
					if(checkActiveFilters(id3, "Platform"))
					$(id3).show();
				}
			});
		}
	});
	
}

function showAllLobColumns(){
	$("lobchecki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
	$.each(uniquecostRecordLob, function(i, val) {
		var id2 = "#" + val.lobId + "i";
		var class2 = $(id2).attr('class');
		if(class2.includes('fa-check-square-o')){
			$(id2).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
			$.each(costRecordLob, function(i1, val1){
				if(val.lobId == val1.lob){
					var id3 = "#" + "crrow_"+i1;
					if(checkActiveFilters(id3, "Lob"))
					$(id3).show();
				}
			});
		}
	});
	
}


function showdropdown(){	          
	           $("#divadddd").append('<li onclick="showAllColumns()" class="cr-criteria-showall"><a><fmt:message
               key="label.showAll"/></a></li>');
$.each(columnGroups1, function(i1, val) {
	 
	if(i1!='default'){
	    $("#divadddd").append('<li  id="'+i1+'" onclick="toggleAllColumnGroup(this.id)"><a><i id="'+i1+'i" class="fa fa-square-o"></i>&nbsp;&nbsp;<strong>'+val.displayName+'</strong></a>');
	}
	   if( val.columns.length > 0){
	    $.each(val.columns,function(i,val1){
	    	if(i1=='default')
	    		{
	    		$("#divadddd").append('<li  id="'+i1+"_"+val1.name+'" onclick="toggleColumnGroup(this.id)"><a><i id="'+i1+"_"+val1.name+'i" class="fa fa-square-o"></i>&nbsp;'+val.columns[i].displayName+'</a></li>');
	    		}
	    	else{
	    	$("#divadddd").append('<li style="padding-left: 8px;" id="'+i1+"_"+val1.name+'" onclick="toggleColumnGroup(this.id)"><a><i id="'+i1+"_"+val1.name+'i" class="fa fa-square-o"></i>&nbsp;'+val.columns[i].displayName+'</a></li>');
	    	}
	    	});
	    }
	   });
}

function showAllColumns(){
    showAllStatusColumns();
    removeRestrictedColumns();
}
function showAll(){
	doNotCloseDropdown(event);
	showAllColumns();
	showAllEffectivesColumns();
	showAllpricingscenarioColumns();
	showAllLobColumns();
	showAllPlatformColumns();
}

function toggleAllColumnGroup(elem){ 
	 var id="#"+elem+"i";
	 var class1=$(id).attr('class');
	 if(class1.includes("fa-square-o")){
		 $(id).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
		 $.each(columnGroups1[elem].columns,function(i,val1){
		 var id12="#"+elem+"_"+val1.name+"i";
		 $(id12).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');	    
		 $("#"+val1.name+"head").hide();		
		 <c:forEach var="cr" items="${costRecordForm.sortedCostRecords}"
				varStatus="rowCount">
				var count=${rowCount.count};
				 $("#"+val1.name+count).hide();
				</c:forEach>
				});
	 $("#cr_colgrp_"+elem).hide();
	 }
	else{
		//$("#checki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		$(id).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		$.each(columnGroups1[elem].columns,function(i,val1){
			var id12="#"+elem+"_"+val1.name+"i";
			$(id12).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
			$("#"+val1.name+"head").show();
			<c:forEach var="cr" items="${costRecordForm.sortedCostRecords}" varStatus="rowCount">
			var count=${rowCount.count};
			$("#"+val1.name+count).show();
			</c:forEach>
			});
		$("#cr_colgrp_"+elem).show();
		document.getElementById("cr_colgrp_"+elem).colSpan=columnGroups1[elem].columns.length; 
		}
		checkFilterIcon();
	}
	
function toggleColumnGroup(elem){		
	 var id="#"+elem+"i";
	 var elem1=elem.split('_');
	 var class1=$(id).attr('class');
	if(class1.includes("fa-square-o")){
	 $(id).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
	 var bool=false;
	 $.each(columnGroups1[elem1[0]].columns,function(i,val1){
		 var id12="#"+elem1[0]+"_"+val1.name+"i";
		 var class12=$(id12).attr('class');
		 if(class12.includes("fa-check-square-o")){
			 bool=true;
		 }
		 else{
			 bool=false;
			 return bool;
		 }
		 });
	 if(bool)
		 {
		 var id1="#"+elem1[0]+"i";
		 $(id1).removeClass('fa fa-square-o').addClass('fa fa-check-square-o');
		 $("#cr_colgrp_"+elem1[0]).hide();
		 }
	 $("#"+elem1[1]+"head").hide();
		
	 <c:forEach var="cr"
			items="${costRecordForm.sortedCostRecords}"
			varStatus="rowCount">
			var count=${rowCount.count};
			 $("#"+elem1[1]+count).hide();
			</c:forEach>
	 
	}
	else{		
		 $(id).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		 var id1="#"+elem1[0]+"i";
		 $(id1).removeClass('fa fa-check-square-o').addClass('fa fa-square-o');
		 $("#"+elem1[1]+"head").show();
			
		 <c:forEach var="cr"
				items="${costRecordForm.sortedCostRecords}"
				varStatus="rowCount">
				var count=${rowCount.count};
				 $("#"+elem1[1]+count).show();
				</c:forEach>
		 $("#cr_colgrp_"+elem1[0]).show();
	}
	checkFilterIcon();
	 var count=0;
	 if(elem1[0]!='default'){
	$.each(columnGroups1[elem1[0]].columns,function(i,val1){
		 var id12="#"+elem1[0]+"_"+val1.name+"i";
		 var class12=$(id12).attr('class');
		 if(class12.includes("fa-square-o")){
			 count++;
		 }
		 });
	document.getElementById("cr_colgrp_"+elem1[0]).colSpan=count;
	}
 }
 
function checkFilterIcon(){
	 var bool1=true;
	 $.each(columnGroups1, function(i1, val) {
		 var bool=true;
	 $.each(val.columns,function(i,val1){
		 var id12="#"+i1+"_"+val1.name+"i";
		 var class12=$(id12).attr('class');
		 if(class12.includes("fa-square-o")){
			 bool=true;
		 }
		 else{
			 bool=false;
			 return bool;
		 }
		 });
	 if(!bool)
		 bool1=false;
		 return bool1;
	 });
	 if(bool1)
		 {
		 $("#checki").removeClass('fa fa-filter cr-filter-combo').addClass('fa fa-caret-down cr-caret');
		 }
	 else{
		 $("#checki").removeClass('fa fa-caret-down cr-caret').addClass('fa fa-filter cr-filter-combo');
	 }
 }

function doNotCloseDropdown(event) {
     event.preventDefault();
     event.stopPropagation();
}

function checkCostValue(field,allowNegative,isRequired) {
	if (isRequired) {
		checkNumericFieldRequired(field,allowNegative);
	} else {
		checkNumericField(field,allowNegative);
	}
}
   function callClear(element){
	 $("#"+element).val('');
}  
 function costRecordExpend(){
	 $("#removeexpand").show();
	 $("#expand").hide();
	 $(".data-depth").addClass("expended");
	 $(".data-depth-hidden").removeClass("hidden");
 }

function costRecordRemoveExpend(){
	$("#expand").show();
	$("#removeexpand").hide()
	$(".data-depth").removeClass("expended");
	$(".data-depth-hidden").addClass("hidden");
 }
 
 function getEscapeValue(value){
	 if (value.indexOf('/') > 0) {
		 return value.replaceAll('/', '\\/');
	 }
	 return value;
 }
 
 
 $(document).ready(function(){
		$.each(costRecordStatus, function(i, val) {
			$("#crrow_"+i).attr('activeFilter', '');
		})
 });
</script>

<link rel="stylesheet" type="text/css" href="css/costrecordedit.css" />
<link rel="stylesheet" type="text/css" href="css/font-awesome.min.css" />
</head>
<body onload="init()" ng-controller="costRecordController">      
	<div>
		<c:if test="${costRecordForm.selectCostRecordType == 'SUMMARY'}">
			<script>
showBusyDialog('<fmt:message key='msg.working.title'/>','<fmt:message key='msg.calculatingSummary'/>');
</script>
			<%
				response.flushBuffer();
			%>
		</c:if>
		<c:if test="${costRecordForm.selectCostRecordType != 'SUMMARY'}">
			<script>
showBusyDialog('<fmt:message key='msg.working.title'/>','<fmt:message key='msg.working'/>');
</script>
			<%
				response.flushBuffer();
			%>
		</c:if>
		<form action="/saveAndContinueCostRecord">
			<html:hidden property="backAction" />
			<html:hidden property="unsavedData" />
			<html:hidden property="eventName" value="" />
			<html:hidden property="eventMessage" />
			<html:hidden property="selectCostRecordType"/>
			<html:hidden property="selectedItemKey" />
			<html:hidden property="itemViewOperation" />
			<html:hidden property="selectedBomKey" />
			<html:hidden property="selectedLaneKey" />
			<input type="hidden" name="sourceSystemDataSource" id="sourceSystemDataSource" value="${costRecordForm.selectedLane.item.dataSource}"/>
			<input type="hidden" name="scrollToRecord" />
			<input type="hidden" name="summaryCalcRequired" />
			<input type="hidden" name="autoCorrect" />
			<input type="hidden" name="preserveSearchValues" />
			<input type="hidden" name="headerAreaCollapsed" />
			<input type="hidden" name="isSaveAction" id="isSaveAction" value="false"/>
			<input type="hidden" name="operation" id="operation" value=""/>
			<c:set var="readOnly" value="${costRecordForm.selectedLane.status == 'CLOSED' || costRecordForm.readOnly}" />
			<c:if test="${costRecordForm.selectCostRecordType=='XWAP' && !e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'UIEdit', costRecordForm.selectCostRecordType)}">
				<c:set var="readOnly" value="true" />
			</c:if>
			<fmt:message var="slTitle" key="sl.header.title" />
			<fmt:message var="clearTitle" key="button.title.clear" />
			<fmt:message var="pushdownTitle" key="button.title.pushdown" />
			<fmt:message var="moreInfoTitle" key="info.more_information" />
			<div class="row" style="padding: 10px; width: 100%">
				<div class="col-sm-6">
					<label
						style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px; padding-left: 20px;">
						<c:out value="${costRecordForm.selectedLane.item.itemNumber}" />
					</label>
				</div>
				<div class="col-sm-6">
					<div style="float: right; padding-right: 20px;">
						<c:if test="${!empty costRecordForm.selectedLane.sourcingLaneKey}">
							<button type="button" class="eto-icon-btn" title="History"
								style="color: #468293;" id="historyButton"
								onclick="javascript:showAuditHistory(${costRecordForm.selectedLane.sourcingLaneKey} ,'PcmSourcingLane')">
								<i class="md-icon">history</i>
							</button>
							<c:if
								test="${e2ofn:hasAccess(appContext, 'COST_RECORD', 'Read')}">
								<button type="button" class="eto-icon-btn" title="Download"
									style="color: #468293;" onclick="javascript:goDownload()">
									<i class="md-icon">file_download</i>
								</button>
							</c:if>
						</c:if>
						<button type="button" class="eto-icon-btn" title="Refresh"
							style="color: #468293;" onclick="javascript:goReadLane()">
							<i class="md-icon">refresh</i>
						</button>
					</div>
				</div>
			</div>
			<span id="unsavedDataMsg" style="font-style: italic"><c:if
					test="${costRecordForm.unsavedData}">
					<fmt:message key="info.unsaved_data" />
				</c:if></span>
			<div class="eto-well eto-expand eto-expand--expanded"
				id="expand-container">
				<div class="row eto-expand__toggle display-xs-flex">
					<div class="col-sm-7 col-md-7 col-lg-7"><h3 class="eto-expand__h3">${slTitle}</h3></div>
					
					<div class="col-sm-5 col-md-5 col-lg-5">
						<div class="flex-items-xs-right eto-expand__toggle" style="display: flex;">
						<label class="eto-select float-xs-right" style="margin-top: 4px"> <span><fmt:message key="sl.existingLanes" />&nbsp;</span></label>
							<div class="eto-select" id="searchSourcingLaneFilter">
								<div class="eto-select__field-container">
									<select name="costLaneKey" class="inputField" onchange="goJumpToLane(this,'${costRecordForm.selectedLane.sourcingLaneKey}',this.value)">
										<option value=""></option>
										<c:forEach var="lane" items="${costRecordForm.allSourcingLanes}">
											<option value="${lane.sourcingLaneKey}" title="<c:out value='${lane.shortTitle}'/>"
												${costRecordForm.selectedLane.sourcingLaneKey == lane.sourcingLaneKey ? 'SELECTED':''}>
												<c:out value="${lane.shortTitle}" />
											</option>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="eto-expand__content">
					<div>
						<div class="row">
							<c:if test="${!empty costRecordForm.selectedLane.bom}">
								<e2i2:instructionsarea>
									<span title="<fmt:message key="sl.nonManaged.title"/>">
										<e2i2:img src="/price_display_manual_item.gif" /> <fmt:message
											key="sl.nonManaged" />
									</span>
								</e2i2:instructionsarea>
							</c:if>
							<e2o:errors maxErrors="4" styleId="errors" />
							<c:if test="${!empty costRecordForm.messages}">
								<c:set var="messages" value="${costRecordForm.messages}" />
								<html:messages id="msg" name="messages" header="messages.header"
									footer="messages.footer">
									<li><c:out value="${msg}" /></li>
								</html:messages>
							</c:if>
							<c:if test="${empty costRecordForm.selectedLane.sourcingLaneKey}">
								<div style="padding-bottom: 10px; padding-left: 10px;">
									<span><i class="md-icon" style="color: #468293;">info</i><span><b>
												: </b></span> <fmt:message key="sl.newLane" /></span>
								</div>
							</c:if>
						</div>
						<div class="row">
							<div class="col-sm-4">
								<div class="eto-input">
									<label class="eto-input__label"><fmt:message
											key="sl.name" /></label>
									<div class="eto-input__container">
										<input class="eto-input__field" type="text" size="30"
											${readOnly?'disabled':''} maxlength="100"
											onchange="javascript:dataChanged()" Id="sourcingLaneName"
											name="selectedLane.sourcingLaneName"
											value='${costRecordForm.selectedLane.sourcingLaneName}'>
										<div class="eto-input__message"></div>
									</div>
								</div>
							</div>
							<div class="col-sm-4">
								<div class="eto-input">
									<div class="eto-select">
										<label class="eto-select__label"><fmt:message
												key="sl.toSite" /></label>
										<div class="eto-select__container">
											<div class="eto-select__field-container">
											    <c:choose>
                                                    <c:when test="${e2ofn:getConfigValue('pcm.supplierAllocation.destinationSite.enabled.for.sourcingLane')}">
                                                	    <e2ot:siteSelectControl property="toSiteKey"
                                                			value="${costRecordForm.selectedLane.toSite.siteKey}"
                                                			onchange="goLaneChanged(this,'${costRecordForm.selectedLane.toSite.siteKey}')"
                                                			styleId="toSiteKey"
                                                			indentValue="&nbsp;&nbsp;" markedSiteValue="*"
                                                			markedSiteStyle="color:black;background-color:#E0E0E0"
                                                			markedSites="${costRecordForm.activeToSites}"
                                                			sites="${costRecordForm.toSites}">
                                                		</e2ot:siteSelectControl>
                                                	</c:when>
                                                	<c:otherwise>
                                                		<e2ot:siteSelectControl property="toSiteKey"
                                                            value="${costRecordForm.selectedLane.toSite.siteKey}"
                                                            onchange="goLaneChanged(this,'${costRecordForm.selectedLane.toSite.siteKey}')"
                                                            styleId="toSiteKey" disabled="${readOnly}"
                                                            indentValue="&nbsp;&nbsp;" markedSiteValue="*"
                                                            markedSiteStyle="color:black;background-color:#E0E0E0"
                                                            markedSites="${costRecordForm.activeToSites}"
                                                            sites="${costRecordForm.toSites}">
                                                        </e2ot:siteSelectControl>
                                                    </c:otherwise>
                                                </c:choose>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="col-sm-4">
								<div class="eto-input">
									<label class="eto-input__label"><fmt:message
											key="sl.status" /></label>
									<div class="eto-input__container">
										<input class="eto-input__field" type="text" size="30"
											maxlength="100" onchange="javascript:dataChanged()"
											value="${costRecordForm.selectedLane.status}" disabled>
										<div class="eto-input__message"></div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-4">
								<div class="eto-select">
									<label class="eto-select__label"><fmt:message
											key="sl.supplier" /></label>
									<div class="eto-input__gray-container"
										style="display: inline-flex; width: 100%">
										<div class="eto-select__field-container" style="width: 100%;">
											<select Class="eto-select__field"
												${readOnly || costRecordForm.validLaneUsage.SUPPLIER == 'NOT_ALLOWED' ?'disabled' :''}
												name="supplierKey" id="supplierKey"
												value="${costRecordForm.selectedLane.supplier.businessEntityKey}"
												onchange="goLaneChanged(this,'${costRecordForm.selectedLane.supplier.businessEntityKey}')">
												<option value="" />
												<c:forEach var="supplier"
													items="${costRecordForm.suppliers}">
													<option value="${supplier.businessEntityKey}"
														title="<c:out value='${supplier.businessEntityName}'/>"
														${costRecordForm.selectedLane.supplier.businessEntityKey == supplier.businessEntityKey ? 'SELECTED':''}><c:out
															value="${supplier.businessEntityName}" /></option>
												</c:forEach>
											</select>
										</div>
										<c:if
											test="${!readOnly && costRecordForm.validLaneUsage.SUPPLIER != 'NOT_ALLOWED'}">
											<div style="margin-top: 0.5rem; margin-left: 0.5rem;">
												<button class="eto-icon-btn" type="button" title="Create"
													onclick="javascript:doFinderPopup('SupplierNameFinder',document.forms[0].supplierKey,'onSupplierCallback',null,'false')">
													<i class="md-icon color:#ababde">search</i>
												</button>
											</div>
										</c:if>

									</div>
								</div>

							</div>
							<div class="col-sm-4">
								<div class="eto-input">
									<label class="eto-input__label"><fmt:message
											key="sl.forecastOffset" /></label>
									<div class="eto-input__container">
										<input class="eto-input__field inputField" type="text"
											size="30" maxlength="100" onchange="javascript:dataChanged()"
											Id="forecastOffset" name="selectedLane.dateOffset"
											value="${costRecordForm.selectedLane.dateOffset}"
											<c:if test="${readOnly}">disabled</c:if> />
										<div class="eto-input__message"></div>
									</div>
								</div>
							</div>
							<div class="col-sm-4">
								<div class="eto-input">
									<label class="eto-input__label"><fmt:message
											key="sl.item" /></label>
									<div class="eto-input__container">
										<a href="#" onClick="openPopOver('${costRecordForm.selectedLane.item.itemKey}');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover">
											<c:out value="${costRecordForm.selectedLane.item.itemNumber}" />
										</a><span style="font-style: italic; width: 220px; cursor: hand"><c:out
												value="(${costRecordForm.selectedLane.item.businessEntity.businessEntityName})" /></span>
										<div class="eto-input__message"></div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-4">
								<div class="eto-input">
									<div class="eto-select">
										<label class="eto-select__label"><fmt:message
												key="sl.fromSite" /></label>
										<div class="eto-select__container">
											<div class="eto-select__field-container">
												<e2ot:siteSelectControl property="fromSiteKey"
													value="${costRecordForm.selectedLane.fromSite.siteKey}"
													onchange="goLaneChanged(this,'${costRecordForm.selectedLane.fromSite.siteKey}')"
													disabled="${readOnly || costRecordForm.validLaneUsage.FROMSITE == 'NOT_ALLOWED'}"
													styleId="fromSite" indentValue="&nbsp;&nbsp;"
													markedSiteValue="*"
													markedSiteStyle="color:black;background-color:#E0E0E0"
													markedSites="${costRecordForm.activeFromSites}"
													sites="${costRecordForm.fromSites}">
													<c:if test="${!empty costRecordForm.fromSites}">
														<option value=""><fmt:message
																key="sl.noSiteSelected" /></option>
													</c:if>
												</e2ot:siteSelectControl>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="col-sm-4">
								<c:set var="restrictCurrency"
									value="${e2ofn:getConfigValue('pcm.sourcingLane.restrictCurrencyToFromSite')}" />
								<div class="eto-select">
									<label class="eto-select__label"><fmt:message
											key="sl.currency" /></label>
									<div class="eto-select__container">
										<div class="eto-select__field-container">
											<select name="selectedLane.currencyCode"
												id="supplierCurrency"
												value="${(!empty costRecordForm.selectedLane.currencyCode) ? costRecordForm.selectedLane.currencyCode : 'USD'}"
												onchange="goLaneChanged(this,'${costRecordForm.selectedLane.currencyCode}')">
												<c:forEach var="cc" items="${costRecordForm.currencies}">
													<c:choose>
														<c:when
															test="${restrictCurrency == true && (not empty costRecordForm.selectedLane.fromSite && costRecordForm.selectedLane.fromSite.defaultCurrencyCode!=null 
															&& costRecordForm.selectedLane.fromSite.defaultCurrencyCode==cc.currencyCode)}">
															<option value="${cc.currencyCode}"
																${costRecordForm.selectedLane.currencyCode==cc.currencyCode? 'selected' :'' }>
																<c:out value="${cc.currencyName}" />
															</option>
														</c:when>
														<c:otherwise>
															<option value="${cc.currencyCode}"
																${costRecordForm.selectedLane.currencyCode==cc.currencyCode? 'selected' :'' }>
																<c:out value="${cc.currencyName}" />
															</option>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</select>
										</div>
										<div class="eto-select__message"></div>
									</div>
								</div>
							</div>
							<div class="col-sm-4">
								<div class="eto-input">
									<label class="eto-input__label"> <fmt:message
											key="item.dataSource" />
									</label>
									<div class="eto-input__container">
										<input class="eto-input__fiel" type="text" size="30"
											maxlength="100" style="width: 100%;"
											value="${costRecordForm.selectedLane.item.dataSource}"
											disabled>
										<div class="eto-input__message"></div>
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-4">
								<div class="eto-select">
									<label class="eto-select__label"><e2i2:formlabel>
											<fmt:message key="sl.productState" />
										</e2i2:formlabel></label>
									<div class="eto-select__container">
										<div class="eto-select__field-container">
											<html:select styleClass="inputField"
												onchange="handleDataChanged()" styleId="productState"
												disabled="${readOnly}" property="selectedLane.productState">
												<html:option value="PRODUCTION"
													key="sl.productState.PRODUCTION" />
												<html:option value="EOL" key="sl.productState.EOL" />
												<html:option value="PREPRODUCTION"
													key="sl.productState.PREPRODUCTION" />
											</html:select>
										</div>
									</div>
								</div>
							</div>
							<div class="col-sm-4">
								<div style="padding-top: 20px;">
									<input type="hidden" Id="endDateRequired"
										name="selectedLane.endDateRequired" /> <label
										class="eto-checkbox"><span class="eto-checkbox__label"><fmt:message
												key="sl.endDateRequired" /></span> <input
										class="eto-checkbox__field" type="checkbox"
										onclick="document.getElementById('endDateRequired').value=this.checked;handleDataChanged()"
										${costRecordForm.selectedLane.endDateRequired ? 'checked="checked"':''}"  ${readOnly ? 'disabled' : ''}>
										<span class="eto-checkbox__box"></span> </label>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="eto-btn-group" style="padding: 10px;">
								<c:if test="${costRecordForm.lockError}">
									<button type="button" class="eto-btn" id="mergeButton"
										onclick="javascript:goMergeChanges()">
										<fmt:message key="button.replace" />
									</button>
								</c:if>
								<c:set var="laneEventSet"
									value="${e2ofn:allValidEvents('Sourcing', costRecordForm.selectedLane)}" />
								<c:forEach var="pdcEvent" items="${laneEventSet}">
									<c:if
										test="${e2ofn:hasEventAccess(appContext, 'Sourcing' , pdcEvent.eventName)}">
										<button type="button" class="eto-btn"
											id="${pdcEvent.eventName}LaneEventButton"
											onclick="javascript:goLaneEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');">
											<fmt:message key="${pdcEvent.uiButtonLabel}" />
										</button>
									</c:if>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
			</div>
			<script type="text/javascript">
				new eto.SelectInput({ el: document.querySelector('.eto-select') });
				new eto.Expand({ el: document.querySelector('#expand-container') });
			</script>
			<%
				pageContext.setAttribute("isGTIE09", !request.getHeader("User-Agent").matches(".*?MSIE [2-9]{1}.*"));
			%>
			<c:if test="${costRecordForm.selectCostRecordType != null && !empty costRecordForm.selectCostRecordType}">
			<c:if test='${isGTIE09}'>
				<c:if test="${costRecordForm.selectCostRecordType != 'SUMMARY'}">
					<%@include file="costRecordPageFilter.jspf"%>
				</c:if>
			</c:if>
			<fmt:message var="crTitle" key="cr.header.title" />
			<fmt:message var="resize" key="search.result.resize.title" />
			<%-- The following trickery is to force the containers table layout to fixed
            and since we don't have a style attribute , we stick it in as part of the id.
            If this is removed, the table will not scroll horizontially--%>
			<div class="container">
				<div class="row" style="padding-top: 20px; padding-bottom: 20px;">
					<div class="col-sm-3">
						<h3>${crTitle}</h3>
					</div>
				</div>
				<c:if test="${costRecordForm.selectCostRecordType != 'SUMMARY'}">
					<div class="row" style="float: right; margin-right: 20px; margin: -5px;">
                <c:if test="${isCurrencyConversionEnable}">
					<label class="eto-switch">
					                        <input
                    								class="eto-switch__field" type="checkbox" 
                    								id="convertValueInUSD" name="convertValueInUSD" ${costRecordForm.convertValueInUSD ? 'checked="checked"':''}
                    								onchange="javascript:displayTotalUSD()"> <span
                    								class="eto-switch__box"></span><span style="padding: 5px;">In USD</span> <c:set var="disabled"
                    									value="false" />
                    							</label>
                    							</c:if>
						<div style="display: flex;">
							<label class="eto-switch"> <input
								class="eto-switch__field" type="checkbox"
								name="filterState(CLOSED)" value="${alertType}"
								onchange="handleFilterRecords('CLOSED',this.value)"> <span
								class="eto-switch__box"></span><span style="padding: 5px;"><fmt:message
										key="cr.filterInactiveRecord" /></span> <c:set var="disabled"
									value="false" />
							</label>
						</div>
					</div>
				</c:if>
				<div class="row">
					<e2i2:tabset id="headerTabSet" onclick="checkTabChange()"
						mintabwidth="auto">
						<c:if
							test="${e2ofn:hasAccess(appContext, 'SOURCING_LANE', 'ViewSummary')}">
							<e2i2:tab onclick="goChangeTab('SUMMARY')"
								selected="${costRecordForm.selectCostRecordType == 'SUMMARY' ? 'yes':'no'}"
								name="SUMMARY">
								<fmt:message key="label.summary" />
								<c:if test="${!empty costRecordForm.tabMessages['SUMMARY']}">
									<fmt:message var="tabMessage"
										key="${costRecordForm.tabMessages['SUMMARY'].key}" />
									<img src="skins/e2-modern/images/tab_alert.gif" style="padding-left:0.25rem;" alt="${tabMessage}" />
								</c:if>
							</e2i2:tab>
						</c:if>
						<c:forEach var="costType" items="${costRecordForm.costTypes}">
							<c:if
								test="${costRecordForm.showAllTabs || costRecordForm.isValidCostType[costType.costTypeKey]}">
								<e2i2:tab onclick="goChangeTab('${costType.costTypeKey}')"
									selected="${costRecordForm.selectCostRecordType == costType.costTypeKey ? 'yes':'no'}"
									name="${costType.costTypeKey}">${costType.costTypeName}
                                  <c:if
										test="${!empty costRecordForm.tabMessages[costType.costTypeKey]}">
										<fmt:message var="tabMessage"
											key="${costRecordForm.tabMessages[costType.costTypeKey].key}" />
										<img src="skins/e2-modern/images/tab_alert.gif" style="padding-left:0.25rem;" alt="${tabMessage}" />
									</c:if>
								</e2i2:tab>
							</c:if>
						</c:forEach>
					</e2i2:tabset>
				</div>
				<div
					style="border-top: 1px #d7d7e2 solid; padding-top: 10px; width: 100%">
					<c:if
						test="${costRecordForm.selectCostRecordType == 'SUMMARY' && e2ofn:hasAccess(appContext, 'SOURCING_LANE', 'ViewSummary')}">
						<fmt:message var="crSummaryTitle" key="cr.summary.title" />
						<fmt:message var="crRollupTitle" key="cr.rollup.title" />
						<fmt:message var="crMarketTitle" key="sl.suggestedMarket" />
						<div class="row">
							<div class="col-sm-6">
								<span><b><fmt:message key="bom.attritionBomItemNumber" /></b> <a href="#" onClick="openPopOver('${costRecordForm.summaryBomItem.itemKey}');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${costRecordForm.summaryBomItem.itemNumber}" removeColon="true"/></a>
		                    </span><span style="font-style: italic; width: 220px; cursor: hand"><c:out
										value="(${costRecordForm.summaryBomItem.businessEntity.businessEntityName})" /></span>
							</div>
							<div class="col-sm-3">
								<span style="float: right;"><fmt:message
										key="label.effective_date" /></span>
							</div>
							<div class="col-sm-3">
								<fmt:formatDate var="rd"
									value="${costRecordForm.summaryEffectiveDateAsDate}"
									pattern="${appContext.currentDateFormat}" />
								<div class="eto-input" data-message-type="" id="effactivedate">
									<div class="eto-input__field-container">
										<input class="eto-input__field" type="text"
											id="summaryEffectiveDate" name="summaryEffectiveDate"
											onkeypress="${autoSubmitFunction}"
											onchange="updateSummaryRequired(true)" readonly="true"
											value="${rd}"> <span class="eto-input__addon"><i
											class="md-icon"
											onclick="javascript:showCalendar('summaryEffectiveDate')">event</i></span>
									</div>
								</div>
								<script type="text/javascript">
								new eto.TextInput({el : document.querySelector('#effactivedate')});
								</script>
								<c:if test="${appContext.attritionRateAllowed}">
									<div class="eto-select">
										<label class="eto-select__label"><fmt:message
												key="label.effective_date" /></label>
										<div class="eto-select__container">
											<div class="eto-select__field-container">
												<select name="summaryDefectTypeKey"
													id="summaryDefectTypeKey"
													value="${costRecordForm.summaryDefectTypeKeyAsLong}"
													onchange="updateSummaryRequired(true)">
													<option value=""></option>
													<c:forEach var="defectType"
														items="${costRecordForm.defectTypes}">
														<option value="${defectType.defectTypeKey}">
															<c:out value="${defectType.defectName}" />
														</option>
													</c:forEach>
												</select>
											</div>
											<div class="eto-select__message"></div>
										</div>
									</div>
								</c:if>
							</div>
						</div>
						<input type="hidden" name="summaryContextKey"
							id="summaryContextKey" />
						<c:if test="${e2ofn:getConfigValue('pcm.bom.incontext')}">
							<div class="row">
								<div class="col-sm-6">
									<div class="eto-select">
										<label class="eto-select__label"><fmt:message
												key="bom.contextType" /></label>
										<div class="eto-select__container">
											<div class="eto-select__field-container">
												<select name="summaryContextType" id="summaryContextType"
													onchange="handleContextChanged()">
													<option value="">None</option>
													<option value="BUSINESS">Business</ option>
												</select>
											</div>
											<div class="eto-select__message"></div>
										</div>
									</div>
									<div class="col-sm-6">
										<span><b><fmt:message key="bom.contextName" /></b></span>
										<div id="contextNameTD">
											<input class="eto-input__field" type="text" size="30"
												maxlength="100" onchange="javascript:dataChanged()"
												readonly="readonly" Id="summaryContextName"
												name="summaryContextName"
												value="${costRecordForm.summaryContextName}" />
											<c:if
												test="${costRecordForm.summaryContextType == 'BUSINESS'}">
												<e2i2:img src="/search.gif"
													onclick="javascript:doFinderPopup('BusinessFinder',document.forms[0].summaryContextKey,'onContextCallback',null,'false')" />
											</c:if>
										</div>
									</div>
								</div>
							</div>
						</c:if>
						<br />
						<div class="row" style="padding: 10px;">
							<div class="eto-card eto-expand eto-expand--expanded"
								id="expand-cr">
								<div class="eto-card__header eto-expand__toggle">
									<span>${crMarketTitle}</span> <span
										class="eto-expand__toggle-expanded md-icon"
										style="flex: 0 0 auto; font-size: 18px;">keyboard_arrow_up</span>
									<span class="eto-expand__toggle-collapsed md-icon"
										style="flex: 0 0 auto; font-size: 18px;">keyboard_arrow_down</span>

								</div>
								<div class="eto-card__body eto-expand__content">
									<div class="crMarketTitle">
										<div class="eto-grid">
											<div class="eto-scroll" style="width: 100%;">
												<table style="width: 100%;">
													<thead>
														<tr>
															<th><fmt:message key="sl.supplierItem" /></th>
															<th><fmt:message key="sl.supplier" /></th>
															<th><fmt:message key="sl.supplierSite" /></th>
															<th><fmt:message key="sa.allocation" /></th>
															<th><fmt:message key="costtype.LIST" /></th>
														</tr>
													</thead>
													<tbody>
														<c:set var="marketTotal" value="0.0" />
														<c:catch var="marketError">
															<c:forEach var="marketData"
																items="${costRecordForm.suggestedMarket}">
																<tr>
																	<td><a
																		href="javascript:goViewItem(${marketData.supplierItem.itemKey},'push')"
																		title="${marketData.supplierItem.description}"> <c:out
																				value="${marketData.supplierItem.itemNumber}" /></a></td>
																	<td><c:out
																			value="${marketData.supplierItem.businessEntity.businessEntityName}" /></td>
																	<td><c:out
																			value="${marketData.supplierSite.siteName}" /></td>
																	<td><c:out value="${marketData.supplyAllocation}%" /></td>
																	<td><fmt:formatNumber maxFractionDigits="${maxFractionDigits}"
																			minFractionDigits="${ minFractionDigits}" value="${marketData.listPrice}" />
																		<c:set var="marketTotal"
																			value="${marketTotal + (marketData.weightedPrice)}" /></td>
																</tr>
															</c:forEach>
														</c:catch>
														<c:choose>
															<c:when test="${!empty marketError.cause}">
																<tr>
																	<td colspan="4" class="errorMessage"><c:out
																			value="${marketError.cause.message}" /></td>
																</tr>
															</c:when>
															<c:when test="${!empty marketError}">
																<tr>
																	<td colspan="4" class="errorMessage"><c:out
																			value="${marketError.message}" /></td>
																</tr>
															</c:when>
														</c:choose>
														<tr>
															<td colspan="3"><fmt:message
																	key="sl.calculatedMarket" /></td>
															<td><fmt:formatNumber maxFractionDigits="${maxFractionDigits}"
																	minFractionDigits="${ minFractionDigits}" value="${marketTotal}" /></td>
															<td></td>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
							<script>
						new eto.Expand({ el: document.querySelector('#expand-cr') });
						</script>
						</div>

						<div class="row" style="padding: 10px; margin-bottom: 100px;"
							id="crSummaryTitle">
							<div class="eto-card eto-expand eto-expand--expanded"
								id="expand-example-3">
								<div class="eto-card__header eto-expand__toggle">
									<span>${crSummaryTitle}</span> <span
										class="eto-expand__toggle-expanded md-icon"
										style="flex: 0 0 auto; font-size: 18px;">keyboard_arrow_up</span>
									<span class="eto-expand__toggle-collapsed md-icon"
										style="flex: 0 0 auto; font-size: 18px;">keyboard_arrow_down</span>

								</div>
								<div class="eto-card__body eto-expand__content">
									<div class="row" style="margin: 0 auto;">
										<button type="button" class="eto-btn" id="recalcButton"
											onclick="javascript:goRecalcSummary();"
											style="margin: 0px 10px 10px 10px;">
											<fmt:message key="button.recalc" />
										</button>
									</div>
									<div class="crMarketTitle">
										<div class="eto-grid">
											<div class="eto-scroll" style="width: 100%;">
												<e2i2:table id="summaryDetails">
													<e2i2:tr header="yes">
														<td style="width: 160px" rowspan="2"><fmt:message
																key="cr.costTotal" /></td>
														<c:forEach var="costType"
															items="${costRecordForm.costTypes}">
															<td width="100px">${costType.costTypeKey}</td>
														</c:forEach>
													</e2i2:tr>
													<c:set var="summaryData"
														value="${costRecordForm.summaryData}" />
													<e2i2:tr header="yes">
														<c:forEach var="costType"
															items="${costRecordForm.costTypes}">
															<c:choose>
																<c:when test="${!costType.allowMultiProvider}">
																	<td class="tableRow0"><a
																		href="javascript:goChangeTab('${costType.costTypeKey}')">
																			<fmt:formatNumber maxFractionDigits="${maxFractionDigits}"
																				minFractionDigits="${ minFractionDigits}"
																				value="${summaryData[costType.costTypeKey]}" />
																	</a> &nbsp;</td>
																</c:when>
																<c:otherwise>
																	<td class="maskedCostType">&nbsp;</td>
																</c:otherwise>
															</c:choose>
														</c:forEach>
													</e2i2:tr>

													<%-- Never rollup non managed cost adjustment and if no boms --%>
													<c:if
														test="${empty costRecordForm.selectedLane.bom && !empty costRecordForm.laneItem.containsBoms}">
														<e2i2:tr header="yes">
															<td>${crRollupTitle}</td>
															<c:forEach var="costType"
																items="${costRecordForm.costTypes}">
																<td width="100px"></td>
															</c:forEach>
														</e2i2:tr>
														<c:set var="excludeTypes"
															value="${e2ofn:getConfigValue('pcm.excludedFromRollup')}" />
														<%-- Get the bom rollup data, this will start the async get, do not use the varaiablbe as it
     is not alwasy correct --%>
														<e2i2:tr header="no">
															<td colspan="${fn:length(costRecordForm.costTypes)+1}">

																<c:set var="summaryStatus"
																	value="${costRecordForm.pendingSummaryStatus}" /> <c:choose>
																	<c:when test="${summaryStatus == 'RUNNING'}">
																		<span class="instructionArea"> <fmt:message
																				key="sl.bomSummaryInProgress" /> <a
																			href="javascript:goChangeTab('SUMMARY')"><fmt:message
																					key="sl.bomSummaryInProgressDetail" /></a> <span
																			style="font-style: italic">${costRecordForm.summaryProcessStatus}</span>
																		</span>
																	</c:when>
																	<c:when test="${summaryStatus == 'ERROR'}">
																		<span class="errorMessage"> <span
																			style="font-style: italic">${summaryStatus}</span>
																		</span>
																	</c:when>
																	<c:when test="${summaryStatus == 'LOCKERROR'}">
																		<span class="errorMessage"> <span
																			style="font-style: italic"><fmt:message
																					key="errors.changed_data_refresh" /></span>
																		</span>
																	</c:when>
			
																	<c:when
																	test="${e2ofn:getConfigValue('pcm.enable.bomCostRollup') }">
																	<c:if test="${empty costRecordForm.bomCostRollupRecordDetails.hasBoms}">
																	<span class="errorMessage"> <fmt:message
																				key="sl.noBomForPeriod">
																				<fmt:param>
																					<fmt:formatDate
																						value="${costRecordForm.summaryEffectiveDateWithOffset}"
																						pattern="${appContext.currentDateFormat}" />
																				</fmt:param>
																			</fmt:message>
																		</span>
																	</c:if>
																	</c:when>
																	<c:when
																	test="${e2ofn:getConfigValue('pcm.enable.bomCostRollup') == false }">
																	<c:if test="${empty costRecordForm.rollupRecordDetails.hasBoms}">
																	<span class="errorMessage"> <fmt:message
																				key="sl.noBomForPeriod">
																				<fmt:param>
																					<fmt:formatDate
																						value="${costRecordForm.summaryEffectiveDateWithOffset}"
																						pattern="${appContext.currentDateFormat}" />
																				</fmt:param>
																			</fmt:message>
																		</span>
																	</c:if>
																	</c:when>
																</c:choose>
															</td>
														</e2i2:tr>
														<%-- Ask again because if we use the last value, there is the chance that
     														we have the data but not in the var here --%>
    													 <c:choose>
																<c:when
																	test="${e2ofn:getConfigValue('pcm.enable.bomCostRollup')}">
																	  <%@ include file="./bomCostRollupNew.jspf"%>
																</c:when>
																<c:otherwise>
																	  <%@ include file="./bomCostRollupOld.jspf"%>
																</c:otherwise>
															</c:choose>
													</c:if>
												</e2i2:table>
											</div>
										</div>
									</div>
								</div>
							</div>
							<script>
						new eto.Expand({ el: document.querySelector('#expand-example-3') });
						</script>
						</div>

					</c:if>
					<c:set var="costElementsDetails" value="" />

					<%-- The costElements is the set of elements in the correct display order for the active cost type
     						The set get used numerious times, so we get it once --%>
					<c:if test="${costRecordForm.selectCostRecordType != 'SUMMARY'}">
						<c:set var="costElements"
							value="${costRecordForm.selectedCostElements}" />
						<c:set var="costElementsDetails"
							value="${costRecordForm.selectedCostElementDetails}" />
						<!-- Initialize Column Groups in controller -->
						 <fmt:message key="cr.columngroup.range" var="crcolgrp_range" />
						
						<fmt:message key="cr.columngroup.flex" var="crcolgrp_flex" />
						
						<fmt:message key="cr.columngroup.MATERIAL" var="crcolgrp_material" />
						
						<fmt:message key="cr.columngroup.TRANSFORMATION"
							var="crcolgrp_transformation" />
					
						<fmt:message key="cr.columngroup.FIXED" var="crcolgrp_fixed" />
						
						<!-- End initialize Column Groups in controller -->
						<div class="row">
							<div id="grid-result"
								style="width: 100%; margin-bottom: 25rem; padding-top: 10px;">
								<div class="eto-grid" id="CostRecord-Grid">
									<div class="eto-grid-scroll">
										<table id="crTable" style="width: 100%;">
											<colgroup>
												<col>
												<col>
												<col>
												<col>
												<col>
												<col>
												<col>
												<col>
												<col>
												<c:if test="${allowPricingScenario}">
													<col>
												</c:if>
												<c:if
													test="${e2ofn:getConfigValue('pcm.costRecord.reasonCodeEnabled')}">
													<col>
												</c:if>
												<c:if test="${allowPricingScenario}">
													<col>
													<col>
													<col>
												</c:if>
												<c:forEach var="costElement" items="${costElements}">
													<col>
												</c:forEach>
												<col>
												<col>
												<!-- <col> -->
												<c:forEach var="attributeDefn"
													items="${costRecordForm.flexAttributeDefinitions}">
													<col>
													<c:if test="${attributeDefn.associatedAttribute == 'numberAttribute1'}">
			 											<col>
			 											<col>
			 											<col>
			 										</c:if>
												</c:forEach>
												<!-- <col> -->
											</colgroup>
											<thead>
												<tr id="crTableTableHeader">
													<th style="border-bottom: 0px;" class="fixedColum"></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<th></th>
													<c:if test="${isCurrencyConversionEnable}">
													<th class="crUSDTotal></th>
													</c:if>
													<th></th>
													<c:if test="${allowPricingScenario}">
														<th id="pricing"></th>
													</c:if>
													<c:if
														test="${e2ofn:getConfigValue('pcm.costRecord.reasonCodeEnabled')}">
														<th id="reason"></th>
													</c:if>
													<c:if test="${allowPricingScenario}">
														<th id="cr_colgrp_crrange" colspan="3" align="center"
															>${crcolgrp_range}</th>
													</c:if>
													<c:set var="costElementTypeLength"
														value="${costRecordForm.numberCostElements!=null?costRecordForm.numberCostElements:'null'}" />
													<c:if test="${costElementTypeLength!=null}">
														<c:forEach var="costElementType"
															items="${costRecordForm.orderedCostElementTypes}">
															<c:set var="colspan"
																value="${costElementTypeLength[costElementType]}" />
															<c:if test="${colspan!=null}">
																<th id="cr_colgrp_costElement"
																	colspan="${colspan}" align="center"
																	>${costElementType}</th>
															</c:if>
														</c:forEach>
													</c:if>
													<%--<c:forEach var="attributeDefn"
														items="${costRecordForm.flexAttributeDefinitions}">
														<c:set var="attributeMessage"
															value="flex.cost.${attributeDefn.associatedAttribute}" />
														<!-- <th></th> -->
													</c:forEach>--%>
													<!-- <th class="projectNamehead"></th>
													<th class="systemActionhead"></th> -->
													<c:set var="numranges" value="${fn:length(costRecordForm.flexAttributeDefinitions)}" />
													<th id="cr_colgrp_crFlex" colspan="${numranges+5}"></th>
													<!-- <th></th> -->
													
												</tr>
												<tr style="border: 2px #cddfe4 solid;">
													<th><c:if test="${!readOnly}">
															<label class="eto-checkbox" style="z-index: 0;">
																<input id="crTable_globalrowselector"
																class="eto-checkbox__field eto-all-rows-indicator"
																onclick="i2uiToggleAllRowsSelectionState(this,'crTable')"
																type="checkbox"> <span class="eto-checkbox__box"></span>
															</label>
														</c:if></th>
													<th
														style="border-right: 4px solid #d3e2e6;" id="statushead"><fmt:message
															key="cr.status" var="crcol_status" /> <span
														class="eto-grid-column__action eto-dropdown">${crcol_status}</span>
													</th>
													<th id="historyhead"><span>History</span></th>
													<th id="fromDatehead"><fmt:message
															key="cr.fromDate" var="crcol_frmDate" />${crcol_frmDate}</th>
													<th id="toDatehead"><fmt:message
															key="cr.toDate" var="crcol_toDate" />${crcol_toDate}</th>
													<th id="crTotalhead"><fmt:message
															key="cr.costTotal" var="crcol_total" />${crcol_total}</th>
													<c:if test="${isCurrencyConversionEnable}">
													<th class="crUSDTotal" id="crTotalheadInUSD"><fmt:message
                                                    	key="cr.total.in.USD" var="crcol_total_USD" />${crcol_total_USD}</th>
                                                    </c:if>
													<th id="descriptionhead"><fmt:message
															key="cr.description" var="crcol_description" />${crcol_description}
														<div style="display: none"
															></div>
													</th>
													<th id="providerhead"><fmt:message
															key="cr.provider" var="crcol_provider" />${crcol_provider}<div
															style="display: none"
															></div></th>
													<c:if test="${allowPricingScenario}">
														<th id="pricingScenariohead" ><fmt:message
																key="cr.pricingScenario" var="crcol_pricingScenario" />${crcol_pricingScenario}<div
																style="display: none"
																></div></th>
													</c:if>
													<c:if
														test="${e2ofn:getConfigValue('pcm.costRecord.reasonCodeEnabled')}">
														<th id="reasonCodehead" rowspan="2"
															><fmt:message
																key="cr.reasonCode" var="crcol_reasonCode" />${crcol_reasonCode}<div
																style="display: none"
															></div></th>
													</c:if>
													<c:if test="${allowPricingScenario}">
														<th id="rangeActivehead"><fmt:message
																key="cr.costRecordRange.active" var="crrange_active" />${crrange_active}<div
																style="display: none"
																></div></th>

														<th id="rangeFromhead"><fmt:message
																key="cr.costRecordRange.from" var="crrange_from" />${crrange_from}<div
																style="display: none"
																></div></th>

														<th id="rangeTohead"><fmt:message
																key="cr.costRecordRange.to" var="crrange_to" />${crrange_to}<div
																style="display: none"
															></div></th>

													</c:if>
													<c:forEach var="costElement" items="${costElements}">
														<th id="${costElement.costElementKey}head" class="costElement">
															${costElement.costElementName}<c:if
																test="${costElement.costElementRequired}">*</c:if> <c:if
																test="${costElement.costElementValueType == 'B' && isGTIE09 }">
																<i id="expand"class="md-icon" style="font-size: 15px;"
																	id="expcostelement" onclick="costRecordExpend();"
																	<%-- ng-show="isCostValueDetailColumnHidden('${costElement.costElementKey}')"
																	ng-click="toggleCostValueDetailsForColumn('${costElement.costElementKey}')" --%>>add_circle_outline</i>
																<i id="removeexpand" class="md-icon" style="font-size: 15px;" onclick="costRecordRemoveExpend();"
																	<%-- ng-hide="isCostValueDetailColumnHidden('${costElement.costElementKey}')"
																	ng-click="toggleCostValueDetailsForColumn('${costElement.costElementKey}')" --%>>remove_circle_outline</i>
															</c:if>
														</th>
													</c:forEach>
													<th class="projectNamehead">
														<fmt:message key="cr.projectName" var="crcol_projectName" />${crcol_projectName}
													</th>
															
													<th class="systemActionhead"><fmt:message
															key="cr.systemAction" var="crcol_systemAction" />${crcol_systemAction}
													</th>
													
													<c:forEach var="attributeDefn"
														items="${costRecordForm.flexAttributeDefinitions}">
														<c:set var="attributeMessage"
															value="flex.cost.${attributeDefn.associatedAttribute}" />
														<th id="${attributeDefn.associatedAttribute}head" class="flexATTR"><fmt:message
																key="${attributeMessage}" var="crflexAttr" />${crflexAttr}
														</th>
														 <c:if test="${attributeDefn.associatedAttribute == 'numberAttribute1'}">
														 	<th id="xlobFGPlatformhead" class="flexATTR">
																<fmt:message key="cr.flex.xlob.platform" var="crcol_xlobFGPlatform" />${crcol_xlobFGPlatform}
															</th>
															<th id="xlobFGLOBhead" class="flexATTR">
																<fmt:message key="cr.flex.xlob.lob" var="crcol_fgLOB" />${crcol_fgLOB}
															</th> 
			 												<th id="isValidXLOBFGhead" class="flexATTR"><fmt:message  
			 													key="cr.isValidXLOB" var="isValidXLOB"/>${isValidXLOB}
															</th>
			 											 </c:if>
													</c:forEach>
												</tr>
											</thead>
											<tbody id="crTableBody">
												<c:if test="${not empty costRecordForm.sortedCostRecords}">
													<fmt:message key="cr.filter.byeffectivity.DEFAULT"
														var="creffectivitydefault" />
													<c:forEach var="cr"  items="${costRecordForm.sortedCostRecords}" varStatus="rowCount">
														<c:if
															test="${costRecordForm.selectCostRecordType == cr.costType.costTypeKey}">
															<html:hidden property="costData(${cr.costRecordExternalId}).costType" value="${costRecordForm.selectCostRecordType}" styleId="costType${rowCount.count}" />
															<c:choose>
																<c:when
																	test="${e2ofn:allowOperation('Sourcing',cr.status,'Edit') == false}">
																	<c:set var="lineReadOnly" value="true" />
																	<c:set var="lockedRow" value="lockedRow" />
																	<c:if test="${cr.status == 'APPROVED'}">
																		<c:set var="lockedRow" value="lockedApprovedRow" />
																	</c:if>

																</c:when>
																<c:when test="${readOnly}">
																	<c:set var="lineReadOnly" value="true" />
																	<c:set var="lockedRow" value="lockedRow" />
																</c:when>
																<c:otherwise>
																	<c:set var="lineReadOnly" value="false" />
																	<c:set var="lockedRow" value="" />
																</c:otherwise>
															</c:choose>
															<%-- <tbody id="tbcrrow_${cr.costRecordKey}"
																ng-hide="isCostRecordHidden('${cr.costRecordKey}')"> --%>
															<c:forEach var="crrange" items="${cr.costRecordRanges}"
																varStatus="status">
																<tr id="crrow_${rowCount.count}"
																	class="cr-${cr.status} compact"
																	>
																	<c:if test="${status.first}">
																		<c:set var="numranges"
																			value="${fn:length(cr.costRecordRanges)}" />
																		<c:set var="lineMessages"
																			value="${costRecordForm.lineMessages}" />
																		<td rowspan="${numranges}" class="fixedColumn" style="display:-webkit-box; min-block-size: -webkit-fill-available;"
																			id="${cr.costRecordExternalId}">
																			<%-- Note that this control uses readOnly because we still want to manage the states --%>
																			<label class="eto-checkbox" style="z-index: 0;margin-bottom: 10px;">
																				<input name="selectedRecordKeys"
																				Id="crTable_rowselector"
																				class="eto-checkbox__field eto-row-indicator"
																				onclick="i2uiToggleRowSelectionState(this,'tableRow${(rowCount.count) % 2}','crTable',null,true);handleLineEventButtons();handleLineButtons()"
																				type="checkbox"
																				value='<c:out value="${cr.costRecordExternalId}" />' />
																				<span class="eto-checkbox__box"></span>
																				</label>
																				<c:set
																					var="lineMessages"
																					value="${costRecordForm.lineMessages}" /> <c:if
																					test="${not empty lineMessages }">										
																						<c:set var="lineErrorExists" value="false" />
																						<html:messages id="lineMessage"
																							name="lineMessages"
																							property="DATE|${cr.costRecordExternalId}">
																							<img src="skins/e2-modern/images/collab_problem.gif" style="padding-left:0.5rem; padding-right:2rem;"
																								alt="${moreInfoTitle}"
																								onclick="javascript:showLineMessage(this,'${lineMessage}')" />
																							<c:set var="lineErrorExists" value="true" />
																						</html:messages>
																						<html:messages id="lineMessage"
																							name="lineMessages"
																							property="AMOUNT|${cr.costRecordExternalId}">
																							<img src="skins/e2-modern/images/missing_price.gif" style="padding-left:0.5rem; padding-right:2rem;"
																								alt="${moreInfoTitle}"
																								onclick="javascript:showLineMessage(this,'${lineMessage}')" />
																							<c:set var="lineErrorExists" value="true" />
																						</html:messages>
																						<html:messages id="lineMessage"
																							name="lineMessages"
																							property="GENERAL|${cr.costRecordExternalId}">
																							<img src="skins/e2-modern/images/alert_yellow_static.gif" style="padding-left:0.5rem; padding-right:2rem;"
																								alt="${moreInfoTitle}"
																								onclick="javascript:showLineMessage(this,'${lineMessage}')" />
																							<c:set var="lineErrorExists" value="true" />
																						</html:messages>
																						<html:messages id="lineMessage"
																							name="lineMessages"
																							property="CORRECTION|${cr.costRecordExternalId}">
																							<img src="skins/e2-modern/images/alert_green_static.gif" style="padding-left:0.5rem; padding-right:2rem;"
																								alt="${moreInfoTitle}"
																								onclick="javascript:showLineMessage(this,'${lineMessage}')" />
																						</html:messages>
																				</c:if>
																		</td>
																		<td rowspan="${numranges}" class="${lockedRow}"
																			style="border-right: 4px solid #d3e2e6;" id="status${rowCount.count}">
																			<fmt:message
																				key="cr.filter.byeffectivity.${cr.costRecordEffectivity}"
																				var="creffectivitytense" />
																		<c:out value="${cr.status}" />
																		</td>
																		<td id="history${rowCount.count}">
																	<button type="button" class="eto-icon-btn" 
																data-popover="#popover${rowCount.count}"
																aria-haspopup="false" aria-controls="#popover-info${rowCount.count}">
																<span class="md-icon">info</span>
															</button>
															<div
																class="eto-popover col-xs-10 col-sm-6 col-md-4 col-lg-3"
																id="popover${rowCount.count}">
																<div class="eto-popover__content">
																<h3>History</h3>
																<ul style="margin:2rem;">
																<li><label><fmt:message key="label.statusChangedOn">
																				<fmt:param value="${cr.statusChangeDate}" />
																			</fmt:message> </label></li>
																<li><label><fmt:message key="label.statusChangedBy">
																				<fmt:param value="${cr.statusLastChangeBy}" />
																			</fmt:message></label></li>
																<li><label><fmt:message key="label.createInfo">
																				<fmt:param value="${cr.insertDate}" />
																			</fmt:message> </label></li>
																<c:choose>
																	<c:when test="${cr.updateDate!=null}">
																		<li><label><fmt:message key="label.changeInfo">
																			<fmt:param value="${cr.updateDate}" /></fmt:message></label></li>
																	</c:when>
																	<c:otherwise>
																		<li><label><fmt:message key="label.changeInfo"> 
																		<fmt:param value="${cr.insertDate}" />
																					</fmt:message></label></li>
																	</c:otherwise>
																</c:choose>
																<li><label>	<fmt:message key="label.externalId">
																				<fmt:param value="${cr.costRecordExternalId}" />
																			</fmt:message></label></li>
																			</ul>
																</div>
																<span class="eto-popover__caret"></span>
															</div> <script type="text/javascript">
																			new eto.Popover({ el :
																				document.querySelector('#popover${rowCount.count}'),
																				hideCaret:true	
																			})
																			</script>
																	</td>
																		<td rowspan="${numranges} id="fromDate${rowCount.count}"
																			class=" eto-grid-edit-cell compact ${lockedRow}"
																			><fmt:formatDate
																				var="rd" value="${cr.effectiveFromDt}"
																				pattern="${appContext.currentDateFormat}" />
																			<div class="eto-input" data-message-type=""
																				style="z-index: 0; width: 20rem; width: 150px;"
																				id="datepick_${cr.costRecordExternalId}_${rd}">
																				<div class="eto-input__field-container">
																					<input class="eto-input__field" type="text"
																						name="costData(${cr.costRecordExternalId}).fromDate"
																						readonly value="${rd}" id="costData(${cr.costRecordExternalId}).fromDate"
																						onchange="handleDataChanged()"
																						data-format="${fn:toUpperCase(appContext.currentDateFormat)}">
																					<c:if test="${!lineReadOnly}">
																						<span class="eto-input__addon"><i
																							class="md-icon"
																							onclick="javascript:showCalendar('costData(${cr.costRecordExternalId}).fromDate')">event</i>
																							<i class="md-icon"
																							onclick="callClear('costData(${cr.costRecordExternalId}).fromDate')">clear</i>
																						</span>
																					</c:if>
																				</div>
																			</div> <script type="text/javascript">
																					var selector_id = '#datepick_${cr.costRecordExternalId}_'+ getEscapeValue('${rd}');
																					new eto.TextInput({el : document.querySelector(selector_id)});																				
																					</script></td>
																		<td rowspan="${numranges}" id="toDate${rowCount.count}"
																			class="eto-grid-edit-cell compact ${lockedRow}"
																			><fmt:formatDate
																				var="rd" value="${cr.effectiveToDt}"
																				pattern="${appContext.currentDateFormat}" />
																			<div class="eto-input" data-message-type=""
																				style="z-index: 0; width: 20rem; width: 150px;"
																				id="datepick_${cr.costRecordExternalId}_toDate">
																				<div class="eto-input__field-container">
																					<input class="eto-input__field" type="text"
																						name="costData(${cr.costRecordExternalId}).toDate"
																						id="costData(${cr.costRecordExternalId}).toDate"
																						readonly="true">
																					<c:if test="${!lineReadOnly}">
																						<span class="eto-input__addon"><i
																							class="md-icon"
																							onclick="javascript:showCalendar('costData(${cr.costRecordExternalId}).toDate')">event</i>
																							<i class="md-icon"
																							onclick="callClear('costData(${cr.costRecordExternalId}).toDate')">clear</i>
																						</span>
																					</c:if>
																				</div>
																			</div> <script type="text/javascript">
																					new eto.TextInput({el : document.querySelector('#datepick_${cr.costRecordExternalId}_toDate'),value:'${rd}'});																					
																					</script></td>
																		<td rowspan="${numranges}" class="${lockedRow}" id="crTotal${rowCount.count}"
																			><fmt:formatNumber
																				maxFractionDigits="${maxFractionDigits}" minFractionDigits="${ minFractionDigits}"
																				value="${cr.computedTotalNotOfCostElementTypeFixed}" /></td>
																				</td>
                                                                               <!-- Medium Severity -->
                                                                               <c:if test="${isCurrencyConversionEnable}">
                                                                                <c:choose>
                                                                                  <c:when test="${cr.computedTotalNotOfCostElementTypeFixedInUSD !=null}">
                                                                                   <td rowspan="${numranges}" class="${lockedRow} crUSDTotal" id="crUSDTotal${rowCount.count}">
                                                                                   <fmt:formatNumber maxFractionDigits="${maxFractionDigits}" minFractionDigits="${ minFractionDigits}"
                                                                                       value="${cr.computedTotalNotOfCostElementTypeFixedInUSD}" /></td>
                                                                                  </c:when>
                                                                                  <c:otherwise>
                                                                                       <td class="crUSDTotal" data-tooltip="#grid-error-7" aria-describedby="#grid-error-7" data-message-type="error">
                                                                                            <div class="eto-tooltip" data-message-type="error" id="grid-error-7">
                                                                                              <div class="eto-tooltip__content">Currency conversion not found</div>
                                                                                              <span class="eto-tooltip__caret" data-position="above"></span>
                                                                                            </div>
                                                                                          </td>
                                                                                          </c:otherwise>
                                                                                          </c:choose>
                                                                                    </c:if>
																		<td id="description${rowCount.count}" rowspan="${numranges}"
																			class="eto-grid-edit-cell compact ${lockedRow}"
																			><c:if
																				test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'CreateNote', costRecordForm.selectCostRecordType) || e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'ReadNote', costRecordForm.selectCostRecordType)}">
																				<div class="eto-input has-value" id="etodescription">
																					<div class="eto-input__field-container">
																						<input class="eto-input__field" type="text"
																							style="width: 200px;"
																							Id="description${rowCount.count}"
																							value="${cr.description}"
																							name="costData(${cr.costRecordExternalId}).description"
																							maxlength="300" size="30"
																							onchange="handleDataChanged()"
																							${lineReadOnly || !e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'CreateNote', costRecordForm.selectCostRecordType)? 'readonly':''}>
																					</div>
																				</div>
																				<script>
																				new eto.TextInput({ el: document.querySelector('#etodescription') });
																				</script>
																			</c:if></td>
																		<td id="provider${rowCount.count}" rowspan="${numranges}"
																			class="eto-grid-edit-cell compact ${lockedRow}"
																			><c:choose>
																				<c:when test="${cr.costType.allowMultiProvider }">
																					<html:hidden
																						property="costData(${cr.costRecordExternalId}).costProviderKey"
																						value="${cr.costProvider.businessEntityKey}"
																						styleId="provider${rowCount.count}" />
																					<div style="width: 30rem;">
																						<div style="width: 80%; float: left">
																							<input type="text" class="inputField"
																								id="providerName${rowCount.count}" size="20"
																								readonly="readonly"
																								value="${cr.costProvider.businessEntityName}"
																								onchange="handleDataChanged()" />
																						</div>
																						<c:if test="${!lineReadOnly}">
																							<div style="width: 20%; padding-top: 2rem;">
																								<e2i2:img src="/search.gif"
																									onclick="javascript:showProviderFinder(${rowCount.count})" />
																								<e2i2:img src="/clear.gif"
																									onclick="javascript:clearCostProvider(${rowCount.count})" />
																							</div>
																						</c:if>
																					</div>
																				</c:when>
																				<c:otherwise>
																					<c:out
																						value="${cr.costProvider.businessEntityName}" />
																				</c:otherwise>
																			</c:choose></td>
																		<c:if test="${allowPricingScenario}">
																			<td id="pricingScenario${rowCount.count}"rowspan="${numranges}" class="${lockedRow}"
																				>
																				${cr.pricingScenario}
																			</td>
																		</c:if>
																		<c:if
																			test="${e2ofn:getConfigValue('pcm.costRecord.reasonCodeEnabled')}">
																			<td id="reasonCode${rowCount.count}" rowspan="${numranges}"
																				class="eto-grid-edit-cell compact ${lockedRow}"
																				><c:set
																					var="reasonCodeValue" value="${cr.reasonCode}" />
																				<c:if test="${lineReadOnly}">
																					<html:hidden
																						property="costData(${cr.costRecordExternalId}).reasonCode"
																						value="${cr.reasonCode}"
																						styleId="reasonCode${rowCount.count}" />
																				</c:if>
																				<div class="eto-select" style="width: 25rem;">
																					<div class="eto-select__field-container">
																						<html:select
																							property="costData(${cr.costRecordExternalId}).reasonCode"
																							value="${reasonCodeValue}"
																							styleClass="inputField eto-select__field"
																							style="font-size:8pt;width:200px;"
																							styleId="reasonCode${rowCount.count}"
																							disabled="${lineReadOnly}"
																							onchange="handleDataChanged()">
																							<html:option value=""></html:option>
																							<html:optionsCollection property="reasonCodes"
																								value="reasonCodeText" label="reasonCodeText"
																								style="" />
																							<c:if
																								test="${ cr.reasonCode != null && cr.reasonCode != '' && !costRecordForm.reasonCodesSet.contains(cr.reasonCode)}">
																								<option value="${cr.reasonCode}" selected>${cr.reasonCode}</option>
																							</c:if>
																							</html:select>
																					</div>
																				</div>
																		</c:if>
																	</c:if>
																	<c:choose>
																		<c:when
																			test="${cr.rangeBased && allowPricingScenario}">
																			<td id="rangeActive${rowCount.count}"
																				class="${lockedRow}" nowrap="nowrap"><c:if
																					test="${lineReadOnly}">
																					<html:radio value="${crrange.costRecordRangeKey}"
																						styleClass="inputField" onchange="${onchange}"
																						onclick="if(event.preventDefault) {event.preventDefault()} else {event.returnValue = false};return false;"
																						property="costData(${cr.costRecordExternalId}).activeRangeKey" />
																				</c:if> <c:if test="${!lineReadOnly}">
																					<html:radio value="${crrange.costRecordRangeKey}"
																						styleClass="inputField" onchange="${onchange}"
																						property="costData(${cr.costRecordExternalId}).activeRangeKey" />
																				</c:if></td>
																			<td id="rangeFrom${rowCount.count}"
																				class="${lockedRow}" nowrap="nowrap"><html:text
																					value="${crrange.fromRange}" size="8"
																					onblur="checkNumericField(this,false)"
																					styleClass="inputField" readonly="${lineReadOnly}"
																					onchange="${onchange}"
																					property="costData(${cr.costRecordExternalId}).rangeData(${crrange.costRecordRangeKey}).fromRange" />
																			</td>
																			<td id="rangeTo${rowCount.count}"
																				class="${lockedRow}" nowrap="nowrap"><html:text
																					value="${crrange.toRange}" size="8"
																					onblur="checkNumericField(this,false)"
																					styleClass="inputField" readonly="${lineReadOnly}"
																					onchange="${onchange}"
																					property="costData(${cr.costRecordExternalId}).rangeData(${crrange.costRecordRangeKey}).toRange" />
																			</td>
																		</c:when>
																		<c:otherwise>
																			<input type="hidden"
																				name="costData(${cr.costRecordExternalId}).activeRangeKey"
																				value="" />
																			<input type="hidden"
																				name="costData(${cr.costRecordExternalId}).rangeData(${crrange.costRecordRangeKey}).fromRange"
																				value="${crrange.fromRange}" />
																			<input type="hidden"
																				name="costData(${cr.costRecordExternalId}).rangeData(${crrange.costRecordRangeKey}).toRange"
																				value="${crrange.toRange}" />
																			<c:if test="${allowPricingScenario}">
																				<td class="${lockedRow}" nowrap="nowrap"
																					id="rangeActive${rowCount.count}">&nbsp;</td>
																				<td class="${lockedRow}" nowrap="nowrap"
																					id="rangeFrom${rowCount.count}">&nbsp;</td>
																				<td class="${lockedRow}" nowrap="nowrap"
																					id="rangeTo${rowCount.count}">&nbsp;</td>
																			</c:if>
																		</c:otherwise>
																	</c:choose>
																	<c:set var="currentCostRecordValues"
																		value="${crrange.costRecordValues}" />
																	<c:forEach var="costElement" items="${costElements}">
																		<c:set var="value"
																			value="${currentCostRecordValues[costElement.costElementKey]}" />
																		<td id="${costElement.costElementKey}${rowCount.count}"
																			class="eto-grid-edit-cell  compact ${lockedRow} cet-${costElement.costElementType}"
																			><e2ot:costValueField
																				costElementKey="${costElement.costElementKey}"
																				costValueRequired="${costElement.costElementRequired}"
																				costElementsDetails="${costElementsDetails}"
																				onchange="handleDataChanged()"
																				fieldId="${rowCount.count}"
																				recordId="${cr.costRecordExternalId}"
																				rangeKey="${crrange.costRecordRangeKey}" status="${cr.status}"
																				value="${value}" readOnly="${lineReadOnly}"
																				allowNegativeValues="${allowNegativeValues}"
																				isGTIE09="${isGTIE09}" /></td>
																	</c:forEach>
																	<td id="projectName${rowCount.count}" rowspan="${numranges}"	class="eto-grid-edit-cell compact ${lockedRow}"
																			>
																			<c:if	test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'CreateNote', costRecordForm.selectCostRecordType) || e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'ReadNote', costRecordForm.selectCostRecordType)}">
																				<div class="eto-input has-value" id="etoprojectName">
																					<div class="eto-input__field-container">
																						<input class="eto-input__field" type="text"
																							style="width: 200px;"
																							id="projectName${rowCount.count}"
																							value="${cr.projectName}"
																							name="costData(${cr.costRecordExternalId}).projectName"
																							maxlength="300" size="30"
																							onchange="handleDataChanged()"
																							${lineReadOnly || !e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'CreateNote', costRecordForm.selectCostRecordType)? 'readonly':''}>
																					</div>
																				</div>
																				<script>
																				new eto.TextInput({ el: document.querySelector('#etoprojectName') });
																				</script>
																			</c:if></td>	
																	<td id="systemAction${rowCount.count}" rowspan="${numranges}"
																		class="eto-grid-edit-cell compact ${lockedRow}"
																		><c:if
																			test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'CreateNote', costRecordForm.selectCostRecordType) || e2ofn:hasAccessForCostType(appContext, 'COST_RECORD', 'ReadNote', costRecordForm.selectCostRecordType)}">
																			<input type="text" class="inputField" style="min-width: 200px;width: 200px;"
																				id="systemAction${rowCount.count}" maxlength="300"
																				size="30" value="${cr.systemAction}" readonly="true"
																				onchange="handleDataChanged()"
																				name="costData(${cr.costRecordExternalId}).systemAction" />
																		</c:if></td>
																		<c:set var="xlobFGName" value="${costRecordForm.getCostData(cr.costRecordExternalId).xlobFGName}" />
																		<c:forEach var="attributeDefn"
																			items="${costRecordForm.flexAttributeDefinitions}">
																			<%
																				PcmCostRecord costRecord = (PcmCostRecord) pageContext.findAttribute("cr");
																												FlexAttributeDefn attributeDefn = (FlexAttributeDefn) pageContext
																														.findAttribute("attributeDefn");
																												String associatedAttribute = attributeDefn.getAssociatedAttribute();
																												Object attributeValue = null;
																												if ("numberAttribute1".equals(associatedAttribute)) {
																													attributeValue = pageContext.findAttribute("xlobFGName");
																												} else {
																													attributeValue = costRecord.getAttribute(associatedAttribute);
																												}
																												if (attributeValue == null) {
																													attributeValue = "";
																												}
																												pageContext.setAttribute("associatedAttribute", associatedAttribute);
																												pageContext.setAttribute("attributeValue", attributeValue);
																			%>

																			<td
																				id="${attributeDefn.associatedAttribute}${rowCount.count}"
																				rowspan="${numranges}"
																				class="eto-grid-edit-cell compact ${lockedRow}">
																				<html:text styleClass="inputField"
																					styleId="${associatedAttribute}_${rowCount.count}"
																					maxlength="400" size="30" value="${attributeValue}"
																					style="min-width: 200px; width: 200px;"
																					readonly="${cr.status == 'APPROVED' || cr.status == 'CLOSED' || associatedAttribute == 'stringAttribute5' || (associatedAttribute == 'numberAttribute1' && cr.costType.costTypeKey != 'XWAP')}"
																					onchange="handleDataChanged()"
																					property="costData(${cr.costRecordExternalId}).attributeData(${associatedAttribute})" />
																			</td>

																			<c:if
																				test="${associatedAttribute == 'numberAttribute1'}">
																				<c:set var="xlobFGPlatform"
																						value="${costRecordForm.getCostData(cr.costRecordExternalId).xlobPlatform}" />
																				<c:set var="fgLOBValue"
																						value="${costRecordForm.getCostData(cr.costRecordExternalId).fgLOB}" />
																				<c:set var="isValidXLOB"
																						value="${costRecordForm.getCostData(cr.costRecordExternalId).isValid}" />
																				<td id="xlobFGPlatform${rowCount.count}"
																					rowspan="${numranges}"
																					class="eto-grid-edit-cell compact ${lockedRow}">
																					<input type="text" class="inputField"
																					style="min-width: 200px; width: 200px;"
																					id="xlobFGPlatform_${rowCount.count}"
																					maxlength="300" size="30" value="${xlobFGPlatform}"
																					readonly="true" />
																				</td>
																				
																				<td id="xlobFGLOB${rowCount.count}"
																					rowspan="${numranges}"
																					class="eto-grid-edit-cell compact ${lockedRow}">
																					<input type="text" class="inputField"
																					style="min-width: 200px; width: 200px;"
																					id="xlobFGLOB_${rowCount.count}" maxlength="300"
																					size="30" value="${fgLOBValue}" readonly="true" />
																				</td>

																				<td id="isValidXLOBFG${rowCount.count}"
																					rowspan="${numranges}"
																					class="eto-grid-edit-cell compact ${lockedRow}">
																					<input type="text" class="inputField"
																					style="min-width: 200px; width: 200px;"
																					id="isValidXLOBFG_${rowCount.count}"
																					maxlength="300" size="30" value="${isValidXLOB}"
																					readonly="true" />
																				</td>
																			</c:if>
																		</c:forEach>
																	</tr>
															</c:forEach>
														</c:if>
													</c:forEach>
												</c:if>
											</tbody>
										</table>
										<div id="bottom_anchor"></div>
									</div>
								</div>
								<script type="text/javascript">
								function hideInnerTooltip(param){
									$("#"+param+" .tooltip-inner").css("display","none;");
								}
								$(document).ready(
										function() {	
											<c:if test="${costRecordForm.selectCostRecordType != 'SUMMARY'}">
											gridObj = new eto.Grid({ el: document.querySelector('#CostRecord-Grid') });
											gridObj.alignRows();
											</c:if>
											$("#CostRecord-Grid .eto-grid-scroll table").css({'width':'100%'});
											$("#CostRecord-Grid .eto-grid-scroll table tbody tr").css({'height':''});
										});
									$(window).on('load', function(){
										handleGridScrolls();
										});
										function handleGridScrolls(){
											var gridWidth=0;
											var scrollWidth=$("#CostRecord-Grid .eto-grid-scroll table").width();
											gridWidth = parseInt(scrollWidth);
											var contentWidth=$('body').width();
											if(gridWidth>contentWidth){
												gridWidth+=60;
												$('#scroller').show();
												$('#staticdiv').css('width',gridWidth+'px');
												$('#scroller').scroll(function(){
												$('#CostRecord-Grid .eto-grid-scroll').scrollLeft($(this).scrollLeft());
											});
										}  else{
											$('#scroller').hide();
										}
										window.onscroll = function(ev) {
											var offset=$('#CostRecord-Grid .eto-grid-scroll')[0].getBoundingClientRect().top;
											if(offset<0){
												$("#CostRecord-Grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,'+(-offset)+'px,0px)','position':'relative','z-index':2,}); 
											} else {
												$("#CostRecord-Grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,0px,0px)','position':'static','z-index':'auto'}); 
											}  

										};		
										}
								</script>
							</div>
						</div>
					</c:if>
				</div>
				<!-- Hide Filter items by default -->
				<%-- <c:forEach var="hideColumn"
					items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.columns')}">
					<div style="display: none"
						ng-init="hideCostRecordColumnsByDefault('${hideColumn}')"></div>
				</c:forEach>
				<c:forEach var="hideStatus"
					items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.status')}">
					<div style="display: none"
						ng-init="toggleCostRecordStatus('${hideStatus}')"></div>
				</c:forEach>
				<c:if test="${allowPricingScenario}">
					<c:forEach var="hidePS"
						items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.pricingscenario')}">
						<div style="display: none"
							ng-init="toggleCostRecordsByPricingScenario('${hidePS}')"></div>
					</c:forEach>
				</c:if>
				<c:forEach var="hideEF"
					items="${e2ofn:getConfigValue('pcm.costRecord.ui.filter.hide.effectivity')}">
					<div style="display: none"
						ng-init="toggleCostRecordsByEffectivity('${hideEF}')"></div>
				</c:forEach> --%>
			</div>
			<c:if test="${!(appContext.currentRole.roleId == 'SUPPLIER' && costRecordForm.selectedLane.item.businessEntity.businessEntityTypeName == 'ENTERPRISE')}">
                <c:set var="isEditable" value="true"/>
            </c:if>
			<div class="footer" style="height: auto;">
				<div class="row">
						<c:choose>
							<c:when test="${!supplierViewOnly}">
								<c:if
									test="${costRecordForm.isValidCostType[costRecordForm.selectCostRecordType]}">
									<div class="col-lg-6 margin-left-sm-2 margin-top-sm-1"
										style="display: flex;">
										<div>
											<c:if
												test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD' , 'Create', costRecordForm.selectCostRecordType) && !readOnly}">
												<button type="button" class="eto-btn"
													style="margin: 0.5rem;" id="addButton"
													${readOnly ? 'disabled' : ''} onclick="javascript:goAdd();">
													<fmt:message key="button.add" />
												</button>
											</c:if>
											<c:if
												test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD' , 'Copy', costRecordForm.selectCostRecordType) && !readOnly}">
												<button type="button" class="eto-btn"
													style="margin: 0.5rem;" id="copyButton"
													${readOnly ? 'disabled' : ''}
													onclick="javascript:goCopy();">
													<fmt:message key="button.copy" />
												</button>
											</c:if>
											<c:if
												test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD' , 'Delete', costRecordForm.selectCostRecordType) && !readOnly}">
												<button type="button" class="eto-btn"
													style="margin: 0.5rem;" id="deleteButton"
													${readOnly ? 'disabled' : ''}
													onclick="javascript:goDelete();">
													<fmt:message key="button.delete" />
												</button>
											</c:if>
										</div>
										<div style="padding-left: 10px;">
											<div class="vl"></div>
										</div>
										<div>
											<c:forEach var="pdcEvent" items="${eventSet}">
												<c:if
													test="${e2ofn:hasEventAccessForCostType(appContext, 'Sourcing' , pdcEvent.eventName, costRecordForm.selectCostRecordType) && !readOnly}">
													<button type="button" class="eto-btn"
														style="margin: 0.5rem;"
														id="${pdcEvent.eventName}EventButton"
														${readOnly ? 'disabled' : ''}
														onclick="javascript:goEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');">
														<fmt:message key="${pdcEvent.uiButtonLabel}" />
													</button>
												</c:if>
											</c:forEach>
										</div>
									</div>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test="${isEditable}">
									<c:if
										test="${costRecordForm.isValidCostType[costRecordForm.selectCostRecordType] }">
										<div class="col-lg-6 margin-left-sm-2 margin-top-sm-1"
											style="display: flex;">
											<div>
												<c:if
													test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD' , 'Create', costRecordForm.selectCostRecordType) && !readOnly}">
													<button type="button" class="eto-btn"
														style="margin: 0.5rem;" id="addButton"
														${readOnly ? 'disabled' : ''}
														onclick="javascript:goAdd();">
														<fmt:message key="button.add" />
													</button>
												</c:if>
												<c:if
													test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD' , 'Copy', costRecordForm.selectCostRecordType) && !readOnly}">
													<button type="button" class="eto-btn"
														style="margin: 0.5rem;" id="copyButton"
														${readOnly ? 'disabled' : ''}
														onclick="javascript:goCopy();">
														<fmt:message key="button.copy" />
													</button>
												</c:if>
												<c:if
													test="${e2ofn:hasAccessForCostType(appContext, 'COST_RECORD' , 'Delete', costRecordForm.selectCostRecordType) && !readOnly}">
													<button type="button" class="eto-btn"
														style="margin: 0.5rem;" id="deleteButton"
														${readOnly ? 'disabled' : ''}
														onclick="javascript:goDelete();">
														<fmt:message key="button.delete" />
													</button>
												</c:if>
											</div>
											<div style="padding-left: 10px;">
												<div class="vl"></div>
											</div>
											<div>
												<c:forEach var="pdcEvent" items="${eventSet}">
													<c:if
														test="${e2ofn:hasEventAccessForCostType(appContext, 'Sourcing' , pdcEvent.eventName, costRecordForm.selectCostRecordType) && !readOnly}">
														<button type="button" class="eto-btn"
															style="margin: 0.5rem;"
															id="${pdcEvent.eventName}EventButton"
															${readOnly ? 'disabled' : ''}
															onclick="javascript:goEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');">
															<fmt:message key="${pdcEvent.uiButtonLabel}" />
														</button>
													</c:if>
												</c:forEach>
											</div>
										</div>
									</c:if>
								</c:if>
							</c:otherwise>
						</c:choose>

					</div>
				<div class="row" id="scroller"
					style="overflow-x: scroll; margin: 0px; position: relative;">
					<div class="col-xs-12">
						<div id="staticdiv" style="height:0.5rem; width: 100%;"></div>
					</div>
				</div>
				<div
					class="row eto-btn-group margin-left-xs-2 margin-right-xs-1 margin-bottom-sm-2 margin-top-sm-2"
					style="display: flex;">
						<c:choose>
							<c:when test="${!supplierViewOnly}">
								<c:if
									test="${!readOnly && e2ofn:hasAccess(appContext, 'SOURCING_LANE', 'Save')}">
									<button type="button" class="eto-btn eto-btn--primary"
										style="margin: 0.5rem;" id="saveButton"
										onclick="javascript:goSaveAndContinue();">
										<fmt:message key="button.save" />
									</button>
									<button type="button" class="eto-btn" style="margin: 0.5rem;"
										id="saveAndReturnButton" onclick="javascript:goSave();">
										<fmt:message key="button.save_return" />
									</button>
									<button type="button" class="eto-btn" id="checkButton"
										style="margin: 0.5rem;" onclick="javascript:goCheck(false);">
										<fmt:message key="button.validate" />
									</button>
									<button type="button" class="eto-btn" id="correctButton"
										style="margin: 0.5rem;" onclick="javascript:goCheck(true);">
										<fmt:message key="button.auto_correct" />
									</button>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test="${isEditable}">
									<c:if
										test="${!readOnly && e2ofn:hasAccess(appContext, 'SOURCING_LANE', 'Save') }">
										<button type="button" class="eto-btn eto-btn--primary"
											style="margin: 0.5rem;" id="saveButton"
											onclick="javascript:goSaveAndContinue();">
											<fmt:message key="button.save" />
										</button>
										<button type="button" class="eto-btn" style="margin: 0.5rem;"
											id="saveAndReturnButton" onclick="javascript:goSave();">
											<fmt:message key="button.save_return" />
										</button>
										<button type="button" class="eto-btn" id="checkButton"
											style="margin: 0.5rem;" onclick="javascript:goCheck(false);">
											<fmt:message key="button.validate" />
										</button>
										<button type="button" class="eto-btn" id="correctButton"
											style="margin: 0.5rem;" onclick="javascript:goCheck(true);">
											<fmt:message key="button.auto_correct" />
										</button>
									</c:if>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${!empty costRecordForm.previousSelectedItem}">
						<button type="button" class="eto-btn" id="upButton" style="margin:0.5rem;"
							onclick="javascript:goViewItem('${costRecordForm.previousSelectedItem}','pop');">
							<fmt:message key="button.up" />
						</button>
					</c:if>
					<c:if test="${!empty costRecordForm.backAction}">
						<button type="button" id="backButton" class="eto-btn" style="margin:0.5rem;"
							onclick="javascript:goBack('${fn:escapeXml(costRecordForm.backAction)}');">
							<fmt:message key="button.back" />
						</button>
					</c:if>
				</div>
			</div>
			</c:if>
			<c:if test="${costRecordForm.selectCostRecordType == null || empty costRecordForm.selectCostRecordType}">
				<div style="margin: auto;width: 24%;padding-top: 3%;"><h3><fmt:message key="cr.no_cost_tab" /></h3></div>
			</c:if>
		</form>
	</div>
	<%@ include file="fullModal.jspf"%>

	<script>
		function removeRestrictedColumns() {
			$.each(columnGroups1, function (key, columnObj) {
				if (columnObj.columns.length > 0) {
					$.each(columnObj.columns, function (i, column) {
						if (restrictedColumnMap.has(column.displayName)) {
							var hideColumn = key + ":" + column.name;
							restrictCostRecordColumnsByRole(hideColumn);
						}
					});
				}
			});
			$("#cr_colgrp_costElement").attr("colspan",$('.costElement:visible').length);
		    $("#cr_colgrp_crFlex").attr("colspan",$('.flexATTR:visible').length);
		}
		

		function restrictCostRecordColumnsByRole(hideColumn) {
			if (hideColumn.includes(":")) {
				var hideColmun1 = hideColumn.split(":");
				var id = "";
				if (hideColmun1[0] == "MATERIAL" || hideColmun1[0] == "TRANSFORMATION" || hideColmun1[0] == "FIXED") {
					id = "CET" + hideColmun1[0] + "_" + hideColmun1[1];
				} else {
					id = hideColmun1[0] + "_" + hideColmun1[1];
				}
				var present = false;
				$.each(columnGroups1, function (i, val) {
					var id1 = id.split(/_(.*)/s)
					if (i == id1[0]) {
						$.each(val.columns, function (i1, val1) {
							if (val1.name == id1[1]) {
								present = true;
							}
						});
					}
					if (present)
						return present;
				});
				if (present) {
					removeColumn(id);
				}
			} else {
				var id = hideColumn;
				if (hideColumn == "MATERIAL" || hideColumn == "TRANSFORMATION" || hideColumn == "FIXED") {
					id = "CET" + id;
				}
				$.each(columnGroups1, function (i, val) {
					if (i == id) {
						toggleAllColumnGroup(id);
					}
				});
			}
		}

		function removeColumn(elem) {
			console.log('element to be hideen is : ' + elem);
			var elem1 = elem.split(/_(.*)/s);
			$("#" + elem1[1] + "head").hide();
			$("." + elem1[1] + "head").hide();
			<c:forEach var="cr" items="${costRecordForm.sortedCostRecords}" varStatus="rowCount">
			var count =${rowCount.count};
			$("#" + elem1[1] + count).hide();
			
			</c:forEach>
			columnGroups1[elem1[0]].columns = columnGroups1[elem1[0]].columns.filter(function(column) {
				return column.name != elem1[1];
			});
		}
	</script>
</body>
</html>