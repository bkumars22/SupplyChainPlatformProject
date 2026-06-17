<!-- /*
 * Copyright (c) 2007 E2open Inc. All Rights Reserved
 * 
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by E2open Inc. All rights reserved.
 */
/**
Common routines for PCM
**/-->
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn" %>
<fmt:setBundle basename="scplatform-messages" />
<c:set var="checkFullModal" value="${e2ofn:getConfigValue('pcm.finder.enable.fullmodal')}"/>
<c:if test="${fullModalPopUp==true}">
	
<c:set var="checkFullModal" value="${fullModalPopUp}"/>
	</c:if>
	 <c:if test="${checkFullModal==true}">
    <%@ include file="/WEB-INF/jsp/finderFullModal.jspf"%>
	</c:if>
	<c:if test="${checkFullModal== false}">
    <%@ include file="/WEB-INF/jsp/fullModal.jspf"%>
	</c:if>
	<c:set var="popupFinderHeaders" value="${e2ofn:getConfigValue('pcm.popupfinder.allfinderHeaderName')}"/>
<script>
var popupHeadersMap = {};
var popupHeaderKey=[];
var popupHeaderValue=[];
var multiSelectpopObj= {};
var multiSelectItem = {};
var invalidSelectItem = {};
<c:forEach var="headers" items="${popupFinderHeaders}" varStatus="index">
var res='${headers}'.split(":");
popupHeaderKey.push(res[0]);
popupHeaderValue.push(res[1]);
</c:forEach>
popupHeaderKey.forEach(function (value, i) {
    popupHeadersMap[value] =popupHeaderValue[i]; 
});

if(typeof parent.parent.mcmApp != 'undefined'){
	parent.parent.mcmApp.toast.clearToasts();
}else if(typeof parent.mcmApp != 'undefined'){
	parent.mcmApp.toast.clearToasts();
}

var aj;
var modPopover;
var showWaitPopover = '<svg preserveAspectRatio="xMidYMin meet" viewBox="0 0 150 100" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" height="80">';
		showWaitPopover += '<g class="eto-loading" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">';
		showWaitPopover += '<path class="eto-loading__background" d="M75,0 L150,50 L0,50 L75,0 Z M0,50 L150,50 L75,100 L0,50 Z" fill-opacity="0.5" fill="#FFFFFF"></path>';
		showWaitPopover += '<polygon class="eto-loading__top-left" points="75 0 75 50 0 50"></polygon>';
		showWaitPopover += '<polygon class="eto-loading__top-right" points="75 0 150 50 75 50"></polygon>';
		showWaitPopover += '<polygon class="eto-loading__bottom-right" points="150 50 75 50 75 100"></polygon>';
		showWaitPopover += '<polygon class="eto-loading__bottom-left" points="0 50 75 50 75 100"></polygon></g></svg>';
		
		jQuery.browser = {};
		(function () {
		    jQuery.browser.msie = false;
		    jQuery.browser.version = 0;
		})();	
function showPopupBlockerMessage()
{
    window.alert("Popup blocker appears to be enabled, See above to allow popups for this site");   
}

function disableBackHistory()
{
    $(document).keydown(function(event)
    {
        if (event.keyCode == 8) 
        {
            var src = event.target;
            var tag = src.tagName ? src.tagName.toUpperCase() : '';
            var typ = (tag == 'INPUT') ? src.type.toUpperCase() : '';
            var isTextArea = (tag == 'TEXTAREA');
            var isTextField = ((tag == 'INPUT') && (typ == 'TEXT'));
            var isText = isTextField || isTextArea;
            var disabled = isText ? src.disabled : false;
            var readOnly = isText ? src.readOnly : false;
            if (!isText || disabled || readOnly) {
                event.preventDefault();
                event.stopPropagation();
            }
        }
    });
}

disableBackHistory();

var helpWindow = null;
function showHelp(anchorName)
{
    var url = 'help/help.jsp'
    if (anchorName != null)
    {         
        url += '?helpContext='+ anchorName;
    }   
    helpWindow = window.open(url,'helpPage','height=600,width=800,location=yes,resizable=yes');
    if (helpWindow == null)
    {
        showPopupBlockerMessage();
        return;
    }
    helpWindow.focus();
}

function goShowPageHelp()
{
    var helpContext = findMetaTag('MCMHELP',document);
    showHelp(helpContext);
}

var globalBusyDialog = null;
function showBusyDialog(title,msg)
{   
	var body='<div class="eto-loading-screen" style="height:10rem;"><div class="eto-loading-screen__content">'
		+'<svg preserveAspectRatio="xMidYMin meet" viewBox="0 0 150 100" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" height="80">'
		+'<g class="eto-loading" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">'
		+'<path class="eto-loading__background" d="M75,0 L150,50 L0,50 L75,0 Z M0,50 L150,50 L75,100 L0,50 Z" fill-opacity="0.5" fill="#FFFFFF"></path>'
		+'<polygon class="eto-loading__top-left" points="75 0 75 50 0 50"></polygon>'
		+'<polygon class="eto-loading__top-right" points="75 0 150 50 75 50"></polygon>'
		+'<polygon class="eto-loading__bottom-right" points="150 50 75 50 75 100"></polygon>'
		+'<polygon class="eto-loading__bottom-left" points="0 50 75 50 75 100"></polygon></g></svg></div></div>';
     globalBusyDialog = $(body);
     globalBusyDialog.modal({opacity:50,close:false}); 
    $("#simplemodal-container").css({ 'border' : 'none' });
	$('.eto-loading-screen').removeAttr('simplemodal-container');
    
} 

function closeBusyDialog()
{
    $.modal.close(); 
    globalBusyDialog = null;
    document.body.style.cursor='default';
}


function findMetaTag(mnanme, docobj)
{      
    var metatags = docobj.getElementsByTagName('meta'); 
    for(var tag in metatags)
    {    
        if(metatags[tag].name == mnanme)
        {      
            return metatags[tag].content;    
        }   
    } 
    return null;
}


function showBusy()
{
    document.body.style.cursor='wait';    
}


function zoomSection(areaid, amount)
{
   areaid.style.zoom = amount;
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
   }
}
 
function roundDecimal( num , dec )
{
    if (typeof(dec) == "undefined") 
    {   
        dec = 0;
    }
    else
    {
        dec = Math.floor( dec );
    }
    if (isNaN(num + dec) || dec < 0 || dec > 12)
    {
        return Math.round( num );
    }
    var n = Math.pow( 10, dec );
    return Math.round( num * n ) / n;
}

function truncateDecimal(num ,dec,k)
{
    var truncatedValue;
    if (typeof(dec) == "undefined") 
    {   
        dec = 0;
    }
    else
    {
        dec = Math.floor( dec );
    }
    if (isNaN(num + dec) || dec < 0 || dec > 12)
    {
        return Math.round( num );
    }   
    if(num>=0){
        truncatedValue=Math.floor(num*Math.pow(10,k)/Math.pow(10,k-dec))/Math.pow(10,dec);
    }else{
        truncatedValue=Math.ceil(num*Math.pow(10,k)/Math.pow(10,k-dec))/Math.pow(10,dec);
    }
    return truncatedValue;
}

/**
 * Return 
 * 0 if the value is not a number
 * 1 if integer >= 0
 * 2 if decimal >= 0
 * -1 if integer < 0
 * -2 if decimal < 0
 *  
 */
function checkNumber(value)
{
    value = trimWhitespace(value);
    if (isNaN(value))
    {
        return 0;
    }
    var num = parseFloat(value);

    if (isNaN(num))
    {
        return  0;
    }
        
    if (num >= 0)
    {
        return (Math.floor(num) == num) ? 1:2;
    }
    else
    {
        return (Math.floor(num) == num) ? -1:2;
    }
}

function isDecimal(value, allowNegative)
{
   if (allowNegative == null)
   {
       allowNegative = false;
   }
   
   value = trimWhitespace(value);

   if (value != null)
   {
       if (allowNegative == false && value.indexOf('-') == 0)
       {
           return false;
       }
   }

   var regEx = /^[+-]?([0-9]*\.?[0-9]+|[0-9]+\.?[0-9]*)$/;
   var result = value.match(regEx);
   if (result != null)
   {
      return value == result[0];
   }
   return false;
}

function isInteger(value, allowNegative)
{
   if (allowNegative == null)
   {
       allowNegative = false;
   }
   
   value = trimWhitespace(value)
   var regEx = (allowNegative) ? /^[-+]?[0-9]+$/ : /^[0-9]+$/;
   var result = value.match(regEx);
   if (result != null)
   {
      return value == result[0];
   }
   return false;
}

function remTrailingZeros(value,min) 
{
    if (isNaN(min))
    {
        min = 0;
    }
    var decPos=value.indexOf(".")
    if (decPos>-1)
    {
        first=value.substring(0,decPos);
        second=value.substring(decPos,value.length);
        while (second.charAt(second.length-1)=="0")
        {
            second=second.substring(0,second.length-1);
            if (second.length == min+1)
            {
                break;
            }
        }
        if (second.length>1)
        {
            return first+second;
        }
        else
        {
            return first;
        }
    }
    return value;
}

function remLeadingZeros(value)
{
    while (value.charAt(0)=="0")
    {
        value=value.substring(1,value.length);
    }
    if (value.charAt(0)==".")
    {
        value="0"+value;
    }
    return value;
}

function formatDecimal(value,min,max)
{
    return remTrailingZeros(value.toFixed(max),min);    
}

//Here I add a little to the string objects functionality using the prototype...
String.prototype.lpad = function (size,PaddingCharacter) 
{ 
  var dif = size - this.length ; 
  var s = this;

  if (dif > 0) 
  { 
    for (;dif!=0;dif+=-1) { s = PaddingCharacter + s;}
  }
  return(s);
}


function isFieldNumeric(fieldName,allowNegative)
{
    var field = document.getElementById(fieldName);
    return isDecimal(field.value,allowNegative);
}

function isFieldNumericOrEmpty(fieldName,allowNegative)
{
    var field = document.getElementById(fieldName);
    if (isValueEmpty(field.value))
    {
        return true;
    }
    return isDecimal(field.value,allowNegative);
}

// Is the field empty
function isFieldEmpty(fieldName)
{
    var field = document.getElementById(fieldName);
    return isFieldValueEmpty(field);
}

function isFieldValueEmpty(field)
{
    return isValueEmpty(field.value);
}

function isValueEmpty(value)
{
    if (value == null || value.length == 0)
    {
       return true;
    }
    if (trimWhitespace(value).length < 1)
    {
       return true;
    }
    return false;
}
//check the field length
function checkfieldNameLength(fieldName)
{
    var field = document.getElementById(fieldName);
    return isFieldValue(field);
}

function isFieldValue(field)
{
    return checkValueLenth(field.value);
}

function checkValueLenth(value)
{
    if (value.length > 255)
    {
       return true;
    }
    
    return false;
}

// returns true if the object is an array
function isArray(a)
{
    return(typeof(a)==='object')?a.constructor.toString().match(/array/i)!==null||a.length!==undefined:false;   
}

function isObject(a)
{
    return(typeof(a)==='object');   
}

// Trim whitespace from either end
function trimWhitespace(s)
{
   return s.replace( /^\s*/, "" ).replace( /\s*$/, "" );
}


function checkNumericField(field,allowNegative,trueCallback,falseCallback)
{
    if (isFieldValueEmpty(field) == false)
    {
        checkNumericFieldRequired(field,allowNegative,trueCallback,falseCallback);
    }
}

function checkNumericFieldRequired(field,allowNegative,trueCallback,falseCallback)
{
    var defaultFalseCallback = function() {
        field.focus();
    }
    if (!$.isFunction(falseCallback)) {
        falseCallback = defaultFalseCallback;
    }
    if (isDecimal(field.value,allowNegative) == false)
    {
       var value = trimWhitespace(field.value);
       if (allowNegative == false && value != null && value.indexOf('-') == 0)
       {
           showOkMessageBox('OK','ERROR',
                   "<fmt:message key='errors.field_positive_decimal_required'/>",
                   "<fmt:message key='msg.error'/>");         
       }
       else
       {
           showOkMessageBox('OK','ERROR',
                   "<fmt:message key='errors.field_decimal_required'/>",
                   "<fmt:message key='msg.error'/>");
       }
       return false;
    }
    trueCallback();
}

function genericShowMessageBox(url, height, width)
{
  if (height == null)
    height = 150;
  else
    height = Math.max(150, height);
  if (width == null)
    width = 350;
  else
    width = Math.max(350, width);

  var screenX = (screen.availWidth  - width)  / 2;
  var screenY = (screen.availHeight - height) / 2;
      
  var dialogWindow = null;
  var i2uiMessageBoxRC = null;
  try
  {
          
      i2uiMessageBoxRC = window.showModalDialog(url,"","dialogleft:"+screenX+"px"
                  +";dialogtop:"+screenY+"px"
                  +";dialogwidth:"+width+"px"
                  +";dialogheight:"+height+"px"
                  +";status:no;unadorned:yes;help:no");
          
  }
  catch(e)
  {
      if (e.message.indexOf('Access is denied') > -1)
      {
          window.alert('You appear to have popup blocking enabled for this site, please disable');
      }
      else
      {
          window.alert(e.message);
      }
      return 'NOT_SUPPORTED';
  }
    
  
  return i2uiMessageBoxRC;
}

function showMessageBox(buttons, icon,prompt,title)
{
    var rc = genericShowMessageBox("modalwin_prompt.jsp?interaction="+buttons
                                +"&icontype="+icon+"&prompt="+prompt
                                +"&title="+title,170,350);
    if (rc == 'NOT_SUPPORTED')
    {
        var trc = window.confirm('Are you sure');
        if (buttons.indexOf('YES') > -1)
        {
           rc = (trc) ? 'yes':'no';
        }
        else
        {
           rc = (trc) ? 'ok':null;
        }
    }
    return rc;
}

function showInputBox(buttons, icon, prompt,title)
{
    var rc = genericShowMessageBox("modalwin_prompt.jsp?input=textbox&interaction="+buttons
                                +"&icontype="+icon+"&prompt="+prompt
                                +"&title="+title,170,350);
    if (rc == 'NOT_SUPPORTED')
    {
        rc = window.prompt('Enter value');
    }

    return rc;
}

function showTextBox(buttons, icon, prompt,title,maxsize)
{
    var rc = genericShowMessageBox("modalwin_prompt.jsp?input=text&interaction="+buttons
                                +"&icontype="+icon+"&prompt="+prompt
                                +"&maxsize="+maxsize
                                +"&title="+title,170,350);
    if (rc == 'NOT_SUPPORTED')
    {
        rc = window.prompt('Enter value');
    }

    return rc;
}

function showYesNoMessageBox(buttons, icon, prompt, title, callback)
{
    showInteractiveMessageBox(buttons, icon, prompt, title, callback, null,'Yes','No');
}

function showYesNoMessageBox(buttons, icon, prompt, title, yesCallback, noCallback)
{
    showInteractiveMessageBox(buttons, icon, prompt, title, yesCallback, noCallback,'Yes','No');
}

function showOkCancelMessageBox(buttons, icon, prompt, title, okCallback)
{
    showInteractiveMessageBox(buttons, icon, prompt, title, okCallback, null, 'Ok', 'Cancel');
}

function showOkCancelMessageBox(buttons, icon, prompt, title, okCallback, cancelCallback)
{
    showInteractiveMessageBox(buttons, icon, prompt, title, okCallback, cancelCallback, 'Ok', 'Cancel');
}

function showInteractiveMessageBox(buttons, icon, prompt, title, activeCallback, passiveCallback, active, passive)
{   
	parent.popupModal = new eto.Modal({ el: parent.document.querySelector('#popup_modal',parent.document) });
	parent.$('#popup_modal_header').text(title);
	parent.$('#popup_modal_body').html(prompt);
	/*MM 04*/
	parent.$('#popup_modal_footer').html('<button class="eto-btn" data-modal-close id="popup_modal_passiveButton">'+passive+'</button><button class="eto-btn eto-btn--primary" data-modal-close id="popup_modal_activeButton">'+active+'</button>');
	parent.popupModal.on('closed', function(query) {		
	});
	parent.$( "#popup_modal_activeButton" ).bind( "click", function() {
	  if ($.isFunction(activeCallback)) {
		  activeCallback.apply();
	 	}
	});
	parent.$( "#popup_modal_passiveButton" ).bind( "click", function() {
		if ($.isFunction(passiveCallback)) {
			passiveCallback.apply();
		}
	});
	parent.popupModal.open();
}


function showOkMessageBox(buttons, icon, prompt, title, okCallback)
{     
   parent.popupModal = new eto.Modal({ el: parent.document.querySelector('#popup_modal',parent.document) });
   parent.$('#popup_modal_header').text(title);
   parent.$('#popup_modal_body').text(prompt);
   parent.$('#popup_modal_footer').html('<button class="eto-btn eto-btn--primary" data-modal-close id="popup_modal_okButton" onclick="">'+buttons+'</button>');
   parent.popupModal.on('closed', function(query) {		
	});
   
   parent.$( "#popup_modal_okButton" ).bind( "click", function() {
	   if ($.isFunction(okCallback)) {
           okCallback.apply();
         }
	 });
   parent.popupModal.open();
}

// Used to construct a function call with arguments that can be passed to other functions
function partial(func /*, 0..n args */) {
    var args = Array.prototype.slice.call(arguments, 1);
    return function() {
        var allArguments = args.concat(Array.prototype.slice.call(arguments));
        return func.apply(this, allArguments);
    };
}
    
// Used to eat a disabled button click
function eatDisabledButtonClick()
{
   return false;
}

// Used to disable or enable the i2 buttons since they are tables with a link
// you need eat the click
function setButtonEnabled(buttonId,fEnabled)
{
   var button = document.getElementById(buttonId);
   if (button != null)
   {
        button.className = (fEnabled) ? 'buttonBorder eto-btn':'buttonBorderDisabled eto-btn';
        if(fEnabled)
        	{
        	$("#"+buttonId).removeAttr('disabled');
        	}
        else{
        	$("#"+buttonId).attr('disabled','disabled');
        	}
       var bcell = button.getElementsByTagName('TD');
       if (bcell.length == 1)
       {
           bcell[0].style.backgroundColor = (fEnabled) ? '#fff6a6':'#fffbdb';
       }
       var link = button.getElementsByTagName('A');
       if (link.length == 1)
       {
           link[0].style.color = (fEnabled) ? '':'#b2b2b2';
           link[0].style.cursor = (fEnabled) ? '':'default';
           link[0].onclick=(fEnabled)? null:eatDisabledButtonClick;
       }
   }
}

var popupWindow = null;

function closePopupIfOpen()
{
   if (popupWindow != null && popupWindow.closed == false)
   {
       popupWindow.close();
   }
   popupWindow = null;
}     
function showPopupWindow(url, target, autoClose)
{
    if (target == undefined )
    {
        target = '_blank';
    }
    if (autoClose == undefined)
    {
        autoClose = false;
    }
    popupWindow = window.open(url,target,
            'height=400,width=600,resizable=yes,status=yes,toolbar=yes,scrollbars=yes,menubar=no,location=no');
    if (popupWindow == null)
    {
        showPopupBlockerMessage();
        return;
    }
    popupWindow.focus();
    if (autoClose && window != popupWindow)
    {
        window.onunload = closePopupIfOpen;
    }
}

function showPopup(url)
{
    showPopupWindow(url,'item');
}

function closePopOver()
{
	modPopover.close();
}


var historyWindow = null;

function closeHistoryIfOpen()
{

   if (historyWindow != null && historyWindow.closed == false)
   {
       historyWindow.close();
   }
}     

function showAuditHistory(key,type)
{
	var url = 'viewAuditHistory.do?showSaveFilter=false&targetType='+type;
	if (key != null)
    {
        url += '&targetKey='+key;
    }
	$('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Audit history");
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
		document.getElementById('loadingDiv').style.display = "block";
        parent.parent.fullScreenModal.open();
}

function showCostRecordAuditHistory(key,type,subTargetKey,subTargetType)
{
	var url = 'viewCostRecordAuditHistory.do?showSaveFilter=false&targetType='+type;
	if (key != null)
    {
        url += '&targetKey='+key;
    }
    if(subTargetKey != null) {
    	url += '&subTargetKey='+subTargetKey;
    	url += '&subTargetType='+subTargetType;
    }
    
	$('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Audit history");
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
		document.getElementById('loadingDiv').style.display = "block";
        parent.parent.fullScreenModal.open();
}

function showFGAuditHistory(key)
{
	var url = 'viewFunctionalGroupAuditHistory.do?functionalGroupId='+key;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Audit history(Functional Group)");
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
		document.getElementById('loadingDiv').style.display = "block";
        parent.parent.fullScreenModal.open();
 }

function showParentFGAuditHistory(key)
{
	var url = 'viewParentFunctionalGroupAuditHistory.do?pfgName='+encodeURIComponent(key);
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Audit history(Parent Functional Group)");
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
	document.getElementById('loadingDiv').style.display = "block";
    parent.parent.fullScreenModal.open();
	}

function showCostExceptionAuditHistory(targetKey)
{
    var url = 'viewCostExceptionAuditHistory.do?&targetKey='+targetKey;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Cost Exception Audit History");
    parent.parent.fullScreenModal.on('closed',function(){
        $('#mainModalFrame').attr('src', 'about:blank');
        parent.parent.fullScreenModal.remove(true);
        $('#fullscreen-main-page-modal').css("display",'none');
    });
	document.getElementById('loadingDiv').style.display = "block";
    parent.parent.fullScreenModal.open();
}


function showTAMAuditHistory(key,siteKey)
{
    var url = 'viewTAMAuditHistory.do?fgName='+encodeURIComponent(key)+'&siteKey='+siteKey;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Audit History(Allocation)");
    parent.$('#contentFrame').contents().find('html').css("overflow-y","hidden");
    parent.parent.fullScreenModal.on('closed',function(){
    	parent.$('#contentFrame').contents().find('html').css("overflow-y","");
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
    document.getElementById('loadingDiv').style.display = "block";
    parent.parent.fullScreenModal.open();
}

function showTAMAuditHistory(tamKey)
{
    var url = 'viewTAMAuditHistory.do?tamKey='+tamKey;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Audit History(Allocation)");
    parent.$('#contentFrame').contents().find('html').css("overflow-y","hidden");
    parent.parent.fullScreenModal.on('closed',function(){
    	parent.$('#contentFrame').contents().find('html').css("overflow-y","");
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
    document.getElementById('loadingDiv').style.display = "block";
    parent.parent.fullScreenModal.open();
}
function showItemAuditHistory(targetKey)
{
    var url = 'viewItemAuditHistory.do?&targetKey='+targetKey;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Item Audit History");
    parent.parent.fullScreenModal.on('closed',function(){
        $('#mainModalFrame').attr('src', 'about:blank');
        parent.parent.fullScreenModal.remove(true);
        $('#fullscreen-main-page-modal').css("display",'none');
    });
    document.getElementById('loadingDiv').style.display = "block";
    parent.parent.fullScreenModal.open();
}

function showItemAssignmAuditHistory(targetKey)
{
    var url = 'viewItemAssignmentAuditHistory.do?&targetKey='+targetKey;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    $("#fullscreen-main-page-modal .eto-modal__header span").text("Item Audit History");
    parent.parent.fullScreenModal.on('closed',function(){
        $('#mainModalFrame').attr('src', 'about:blank');
        parent.parent.fullScreenModal.remove(true);
        $('#fullscreen-main-page-modal').css("display",'none');
    });
    document.getElementById('loadingDiv').style.display = "block";
    parent.parent.fullScreenModal.open();
}

var finderPopupWin = null;

function closeFinderIfOpen()
{

   if (finderPopupWin != null && finderPopupWin.closed == false)
   {
      finderPopupWin.close();
   }
}     

// Launch the finder window
function doFinderPopup(finderName, finderField)
{
    doFinderPopup(finderName,finderField,null,null,'true');
}

function doFinderPopup(finderName, finderField, callbackFunction, finderSearchValue, multiSelect)
{
    var url = './viewSearchFinder.do?finderName='+finderName;
    if (finderField != null)
    {
        if (finderField.id != undefined)
        {
            finderField = finderField.id;
        }
        var element = document.getElementById(finderField);
        url += '&resultField='+element.id;
    }
   
    if (callbackFunction != null && callbackFunction.length > 0)
    {   
         url += '&resultCallbackFunc='+callbackFunction;
    }
    if (finderSearchValue != null && finderSearchValue.length > 0)
    {   
         url += "&finderParamValue="+finderSearchValue;
    }
    if (multiSelect != null && multiSelect.length > 0)
    {   
         url += "&multiSelect="+multiSelect;
    }
    callFinderAjax(url, finderName);
}
function callFinderAjax(url,finderName){
	showWaitBusy();
    $.ajax({
		type: "POST",
		url: url,
		cache: false,
		dataType: 'text html',
		data: {			
		},
		success: function(result) {
			 
			<c:if test="${checkFullModal == true}">
			$('#finder-fullscreen-main-page-modal #fullmainModalFrame').attr('src', url);  
			parent.parent.$('#contentFrame').contents().find('html').css("overflow-y","hidden");
			$('#finder-fullscreen-main-page-modal').css("display",'');
			$('#finder-fullscreen-main-page-modal .eto-modal__footer').css("display",'none');
		    parent.parent.finderModal =  new eto.Modal({ el: document.querySelector('#finder-fullscreen-main-page-modal') });
		    
		    $("#finder-fullscreen-main-page-modal .eto-modal__header span").text(popupHeadersMap[finderName]);
		    parent.parent.finderModal.on('closed',function(){
		    	$('#finder-fullscreen-main-page-modal #fullmainModalFrame').attr('src', 'about:blank');
		    	parent.parent.finderModal.remove(true);
		    	$('#finder-fullscreen-main-page-modal').css("display",'none');
		    	parent.parent.$('#contentFrame').contents().find('html').css("overflow-y","");
		    });
		    parent.parent.finderModal.open();
		    </c:if>
		    <c:if test="${checkFullModal == false}">
		    $('#popup_modal_for_Finder').css("display",'');
		    parent.parent.finderModal =  new eto.Modal({ el: document.querySelector('#popup_modal_for_Finder') });
		    $("#popup_modal_for_Finder .eto-modal__header span").text(popupHeadersMap[finderName]);
		    parent.parent.finderModal.on('closed',function(){
		    	$('#basicmainModalFrame').attr('src', 'about:blank');
		    	parent.parent.finderModal.remove(true);
		    	$('#popup_modal_for_Finder').css("display",'none');
		    	parent.parent.$('#contentFrame').contents().find('html').css("overflow-y","");
		    });
		    parent.parent.finderModal.open();  
		    $('#basicmainModalFrame').attr('src', url); 
		    </c:if>
		    closeBusyDialog();
		},
		error: function(error){
			console.log('errored out start popup finder');
			closeBusyDialog();
		}
	});  
    /*
    var url = url;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
    parent.parent.fullScreenModal.open(); 
     
    /* parent.parent.fullScreenModal.open();
    finderPopupWin = window.open(url,'finderPopup','height=600,width=850,top='
                            +window.screenTop+',left='
                            +window.screenLeft
                            +',resizable=yes,status=yes,toolbar=no,scrollbars=yes,menubar=no,location=no');
    if (finderPopupWin == null)
    {
        showPopupBlockerMessage();
        return;
    }
    finderPopupWin.focus();
    window.onunload = closeFinderIfOpen;  */ 
   /*  var url = url;
    $('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
    parent.parent.fullScreenModal.open(); */
}


function getBodyHeight() 
{
  if( typeof( window.innerWidth ) == 'number' ) 
  {
    //Non-IE
    return window.innerHeight;
  } 
  else if( document.documentElement && document.documentElement.clientHeight) 
  {
    //IE 6+ in 'standards compliant mode'
    return document.documentElement.clientHeight;
  } 
  else if( document.body && document.body.clientHeight) 
  {
    //IE 4 compatible
    return document.body.clientHeight;
  }
  else
  {
     return null;
  }
}

function getBodyWidth() 
{
  if( typeof( window.innerWidth ) == 'number' ) 
  {
    //Non-IE
    return window.innerWidth;
  } 
  else if( document.documentElement != null && document.documentElement.clientWidth)
  {
    //IE 6+ in 'standards compliant mode'
    return document.documentElement.clientWidth;
  } 
  else if( document.body != null && document.body.clientWidth)
  {
    //IE 4 compatible
    return document.body.clientWidth;
  }
  else
  {
     return null;
  }
}

function pushValueDown(startIndex,fieldid)
{
    var rc = false;
    if (typeof(startIndex) == 'string')
    {
        startIndex = parseInt(startIndex);
    }
    
    var dataitems = document.getElementsByName(fieldid);
    if (dataitems == null || dataitems.length < startIndex)
    {
       return rc;
    }
    var value = dataitems[startIndex].value;
    for (idx = startIndex+1; idx < dataitems.length; idx++)
    {
        if (dataitems[idx].value != value)
        {
           dataitems[idx].value = value;
           rc = true;
        }
    }    
    return rc;
}
// For when field names are indexed like dueDate1, dueDate2, etc
// The {0} is replaced in the fieldid with the index.
function pushValueDownIndexed(startIndex,fieldid, prevValues,predicate,calculatedValue)
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
    var downField = document.getElementById(fieldid.replace("{0}",idx));
    var undo = isObject(prevValues);
    while (downField != null && downField != undefined)
    {
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
        downField = document.getElementById(fieldid.replace("{0}",idx));
    }    
    return rc;
}



// Override standard taglib behavior
// This version supports rowspan.
function i2uiToggleRowSelectionState(obj, originalstate, tableid, treecell, multiSelect)
{
  // NS4 browser not supported
  if (document.layers != null)
    return;

  if (i2uiActiveRowSelector != null)
    obj = i2uiActiveRowSelector;
  else
  {
    // turn off any global selector
    var globalselector = document.getElementById(tableid+"_globalrowselector");
    if (globalselector != null) 
      globalselector.checked = false;
  }

  // if single select, turn off previous row
  if (multiSelect == false)
  {
    // test for scrollable table
    var table = document.getElementById(tableid+"_data");
    if (table == null)
      table = document.getElementById(tableid);

    var len = table.rows.length;
    for (var i=0; i<len; i++)
    {
      if (table.rows[i].lastClassName != null)
      {
        table.rows[i].className = table.rows[i].lastClassName;
        if (table.rows[i].cells[0].childNodes && 
            table.rows[i].cells[0].childNodes.length > 0 &&
            table.rows[i].cells[0].childNodes[0].style != null)
          table.rows[i].cells[0].childNodes[0].style.backgroundColor = "";
        table.rows[i].lastClassName = null;
      }
    }
    
    if(obj != null && obj.name != null)
    {
      var allCheckBoxes = document.getElementsByName(obj.name);
      for(var i=0; i<allCheckBoxes.length; i++)
      {
        if(allCheckBoxes[i] != obj)
        {
          allCheckBoxes[i].checked = false;
        }
      }
    }
  }

  // find owning row
  var rowobj = obj;
  var rowspan = 1;
  while (rowobj != null && rowobj.tagName != "TR")
  {
    if (rowobj.parentElement)
    {
      rowobj = rowobj.parentElement;
    }
    else
    {
      rowobj = rowobj.parentNode;
    }
    if (rowobj.tagName == "TD")
    {
       rowspan = rowobj.rowSpan;
    }
  }
  if (rowobj != null)
  {
    rowobj.className = obj.checked?"rowHighlight active":originalstate;
    rowobj.lastClassName = originalstate;
    if (rowobj.cells[0].childNodes[0].style)
    {
       rowobj.cells[0].childNodes[0].style.backgroundColor = "";
    }
    if (rowspan > 1)
    {
    var table = document.getElementById(tableid+"_data");
    if (table == null)
      table = document.getElementById(tableid);

       if (table != null && table.tagName == 'TABLE')
       {
          for (i=1;i < rowspan; i++)
          {
             table.rows[rowobj.rowIndex+i].className = obj.checked?"rowHighlight active":originalstate;
             table.rows[rowobj.rowIndex+i].lastClassName = originalstate;
          }   
       }
    }
  }

  // handle treetable
  if (treecell != null &&
      treecell != '' &&
      i2uiActiveRowSelector == null)
  {
    var cellname = rowobj.cells[treecell].id.substring(9);
    var depth1 = Math.floor(cellname);
    var table = document.getElementById(tableid);
    var len = table.rows.length;
    for (var i=0; i<len; i++)
    {
      // locate desired row in table
      if (table.rows[i].cells[treecell].id == "TREECELL_"+cellname)
      {
        // now process rest of table with respect to located
        for (var j=i+1; j<len; j++)
        {
          var newcell = table.rows[j].cells[treecell].id.substr(9);
          var depth2 = Math.floor(newcell);
          if ((depth2 == depth1 + 10 || depth2 == depth1 + 15) ||
              (depth2 > depth1 + 5))
          {
            table.rows[j].cells[0].childNodes[0].checked = obj.checked;
            if (obj.checked)
            {
              table.rows[j].className = "rowHighlight active";
              table.rows[j].cells[0].childNodes[0].style.backgroundColor = "";
            }
            else
            {
              var onclickhandler = table.rows[j].cells[0].childNodes[0].onclick+"!!!";
              var from = onclickhandler.indexOf("{");
              onclickhandler = onclickhandler.substring(from+1);
              var to = onclickhandler.lastIndexOf("}");
              onclickhandler = onclickhandler.substring(0,to);
              i2uiActiveRowSelector = table.rows[j].cells[0].childNodes[0];
              eval(onclickhandler);
              i2uiActiveRowSelector = null;
            }
          }
          if (depth2 <= depth1)
          {
            break;
          }
        }

        var handle = depth1 - 10;
        // now set partial state of all parents of selected node
        for (var j=i-1; j>-1; j--)
        {
          var newcell = table.rows[j].cells[treecell].id.substr(9);
          var depth2 = Math.floor(newcell);
          if (depth2 == handle || depth2 == handle-5)
          {
            var selectedcount = 0;
            var nonselectedcount = 0;
            for (var k=j+1; k<len; k++)
            {
              var newcell2 = table.rows[k].cells[treecell].id.substr(9);
              var depth3 = Math.floor(newcell2);
              if ((depth3 == depth2 + 10 || depth3 == depth2 + 15) ||
                  (depth3 > depth2 + 5))
              {
                if (table.rows[k].cells[0].childNodes[0].checked)
                  selectedcount++;
                else
                  nonselectedcount++;
              }
              if (depth3 <= depth2)
              {
                break;
              }
            }
            
            if (selectedcount > 0)
            {
              if (nonselectedcount > 0)
              {
                table.rows[j].cells[0].childNodes[0].checked=false;
                var onclickhandler = table.rows[j].cells[0].childNodes[0].onclick+"!!!";
                var from = onclickhandler.indexOf("{");
                onclickhandler = onclickhandler.substring(from+1);
                var to = onclickhandler.lastIndexOf("}");
                onclickhandler = onclickhandler.substring(0,to);
                i2uiActiveRowSelector = table.rows[j].cells[0].childNodes[0];
                eval(onclickhandler);
                i2uiActiveRowSelector = null;
                table.rows[j].cells[0].childNodes[0].style.backgroundColor = "#fff6a6";
              }
              else
              {
                table.rows[j].cells[0].childNodes[0].checked = true;
                table.rows[j].className = "rowHighlight active";
                table.rows[j].cells[0].childNodes[0].style.backgroundColor = "";
              }
            }
            else
            {
              table.rows[j].cells[0].childNodes[0].checked=false;
              table.rows[j].cells[0].childNodes[0].style.backgroundColor = "";

              if (nonselectedcount > 0)
              {
              var onclickhandler = table.rows[j].cells[0].childNodes[0].onclick+"!!!";
              var from = onclickhandler.indexOf("{");
              onclickhandler = onclickhandler.substring(from+1);
              var to = onclickhandler.lastIndexOf("}");
              onclickhandler = onclickhandler.substring(0,to);
              i2uiActiveRowSelector = table.rows[j].cells[0].childNodes[0];
              eval(onclickhandler);
         	  i2uiActiveRowSelector = null;
              }
            }
            // look up one more level
            handle -= 10;
            if (handle < 0)
              break;
          }
        }

        break;
      }
    }
  }
}

/* onload state is fired, append onclick action to the table's DIV */
/* container. This allows the HTML document to validate correctly. */
/* addIEonScroll added on 2005-01-28                               */
/* Terence Ordona, portal[AT]imaputz[DOT]com                       */
function addIEonScroll(tableId, tableHeaderId) {
    var thisContainer = document.getElementById(tableId);
    if (thisContainer == null) { return; }

    var onClickAction = 'toggleSelectBoxes("' + tableId + '","' + tableHeaderId + '");';
    thisContainer.onscroll = new Function(onClickAction);
}

/* Only WinIE will fire this function. All other browsers scroll the TBODY element and not the DIV */
/* This is to hide the SELECT elements from scrolling over the fixed Header. WinIE only.           */
/* toggleSelectBoxes added on 2005-01-28 */
/* Terence Ordona, portal[AT]imaputz[DOT]com         */
function toggleSelectBoxes(tableId,tableHeaderId) {
    var thisContainer = document.getElementById(tableId);
    var thisHeader = document.getElementById(tableHeaderId);
    if (thisContainer == null || thisHeader == null) { return; }

    var selectBoxes = thisContainer.getElementsByTagName('select');
    if (selectBoxes == null) { return; }

    for (var i = 0; i < selectBoxes.length; i++) {
        if (thisContainer.scrollTop > eval(selectBoxes[i].parentNode.offsetTop - thisHeader.offsetHeight)) {
            selectBoxes[i].style.visibility = 'hidden';
        } else {
            selectBoxes[i].style.visibility = 'visible';
        }
    }
} 


//Function that will adjust the header colums to match the
//the width of each data row.  This will not work
//when you have scrollableColumns
//tableid - tablename id
//height - height of the table
function alignHeaderColumns(tableid,height)
{
    var w;
    var scrolleritem = document.getElementById(tableid+"_scroller");   
    var headeritem = document.getElementById(tableid+"_header");
    var dataitem = document.getElementById(tableid+"_data");
    if (headeritem != null && dataitem != null)
    {
        if (height > 0 && scrolleritem != null)
        {
            scrolleritem.style.height = height;
        }
        headeritem.width       = dataitem.clientWidth;
        headeritem.style.width = dataitem.clientWidth;
        headeritem.style.tableLayout='fixed';   
        var headercolcount = headeritem.rows[0].cells.length;
        if (dataitem.rows.length > 0)
        {
            var len = dataitem.rows[0].cells.length;
            for (var i=0; i<headercolcount; i++)
            {
                //w = dataitem.rows[0].cells[i].clientWidth;
                w = calculateCellWidth(dataitem,i,headercolcount);
                if (w > 0)
                {
                    headeritem.rows[0].cells[i].style.width = w;
                }
            }
        }
    }
}

function calculateCellWidth(dataitem,cellIndex,headercolcount)
{
    var rowCount = dataitem.rows.length;
    for (var i=0; i < rowCount && i < 10; i++)
    {
        // Only test if the cell counts are the same
        if (dataitem.rows[i].cells.length == headercolcount)
        {
            return dataitem.rows[i].cells[cellIndex].clientWidth;
        }
    }
    return -1;
}


//Helpers
function getAbsolutePos(el)
{
    var SL = 0, ST = 0;
    var is_div = ('DIV' == el.tagName);
    if (is_div && el.scrollLeft)
    {
        SL = el.scrollLeft;
    }
    if (is_div && el.scrollTop)
    {
        ST = el.scrollTop;
    }
    var r = { x: el.offsetLeft - SL, y: el.offsetTop - ST };
    if (el.offsetParent) 
    {
        var tmp = getAbsolutePos(el.offsetParent);
        r.x += tmp.x;
        r.y += tmp.y;
    }
    return r;
};

function getScrollXY() 
{
  var scrOfX = 0, scrOfY = 0;
  if( typeof( window.pageYOffset ) == 'number' ) 
  {
    //Netscape compliant
    scrOfY = window.pageYOffset;
    scrOfX = window.pageXOffset;
  } 
  else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) 
  {
    //DOM compliant
    scrOfY = document.body.scrollTop;
    scrOfX = document.body.scrollLeft;
  } 
  else if( document.documentElement && 
          ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) 
  {
    //IE6 standards compliant mode
    scrOfY = document.documentElement.scrollTop;
    scrOfX = document.documentElement.scrollLeft;
  }
  return { x:scrOfX, y:scrOfY };
}

function disableAllChildren(node,flag)
{
   if (node == null || node == 'undefined')
   {
      return;
   }
   var tag = node.tagName;
   if (tag == 'INPUT' || tag == 'TEXTAREA' || tag == 'BUTTON' || tag == 'SELECT')
   {
      node.disabled=flag;
   }
   var len = node.childNodes.length;
   for (var idx=0; idx < len; idx++)
   {
      disableAllChildren(node.childNodes[idx],flag);
   }
}

function findOption(options, value)
{
    for (var idx=0; idx < options.length; idx++)
    {
        if (options[idx].value == value)
        {
            return idx;
        }
    }
    return -1;

}

function addOption(selobj,text,value)
{
    var opt = document.createElement("OPTION");
    opt.text = text;
    opt.value = value;
    selobj.options.add(opt);            
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

function selectAllOptions(sellist, selectFlag)
{
   if (sellist.options == null)
   {
      return;
   }
   var len = sellist.options.length;
   for(var idx = 0; idx < len; idx++)
   {
       sellist.options[idx].selected = selectFlag;
   }
}

function fillSelectFromArray(selobj,arrayobj)
{
   for (i=0; i < arrayobj.length; i++)
   {
       addOption(selobj,arrayobj[i],arrayobj[i]);
   }
}

function clearSelectList(sellist)
{
   if (sellist.options == null)
   {
      return;
   }
   var len = sellist.options.length;
   while (len > 0)
   {
      sellist.options.remove(0);
      len--;
   }
}

//Table helpers

// Finds the table, since i2 and e2o scrollable tables use a table in the div]
// if you pass the id of the div, it will return the table part
function findTable(tableId)
{
    var found = $('table[id='+tableId+']');
    if (found != null && found.length && found.length > 0)
    {
        return found[0];
    }
    found = $('#'+tableId + ' table');
    if (found != null && found.length && found.length > 0)
    {
        return found[0];
    }
    return null;
}

//TODO convert to use jquery
function findParentRow(obj)
{
    if (obj == null)
    {
        return null;
    }
    if (obj.parentNode.tagName == 'TR')
    {
        return obj.parentNode;
    }
    return findParentRow(obj.parentNode);
}

//TODO convert to use jquery
function filterTableRows(tablename, match, show, cellIndex)
{
    var table = document.getElementById(tablename);
    for (var ridx=0; ridx < table.rows.length; ridx++)
    {       
        var cell = table.rows[ridx].cells[cellIndex]
        var value = (cell.innerText) ? cell.innerText : cell.textContent;                           
        if (value != null)
        {
            value = trimWhitespace(value);
        }
        if (match == value)
        {
            cell.row.style.display = (show) ? '':'none';    
        }                               
    }
}

function setupOrderColumnState(tableId,save,options)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return;
    }
    if (options == null)
    {
        options = new Object();
    }
    $(table).columnOrderManager({disableIfClass:'ocmDisabled',
        mouseOverTitle:options.mouseOverTitle,
        stateCookieId:options.stateCookieId,
        stateFieldId:'ocmState_'+tableId,
        saveState: true, saveDuration: (save) ? 9999:null});
}

function saveColumnOrder(tableId)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return;
    }
    $(table).saveColumnOrder(tableId);
}

function resetColumnOrder(tableId,clearSavedState)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return;
    }
    $(table).resetColumnOrder(clearSavedState);
}

function setupHideColumnState(tableId,save,options)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return ;
    }
    if (options == null)
    {
        options = new Object();
    }   
    $(table).columnManager({listTargetID: tableId+'_columnList',
        disableIfClass: 'hcmDisabled', onClass:'hcmColumnShown', offClass:'hcmColumnHidden',
        stateCookieId:options.stateCookieId,
        stateFieldId:'hcmState_'+tableId,
        saveState: true, saveDuration: (save) ? 9999:null});    
}

function showAllColumns(tableId, clearCookie)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return ;
    }   
    $(table).showAllColumns({listTargetID: tableId+'_columnList',
        onClass:'hcmColumnShown', offClass:'hcmColumnHidden', clearSavedState:clearCookie});
}

function showColumn(tableId, column)
{
    // In case we are in a child of the td, find the td
    var cell = $(column).closest('td');
    if (cell != null)
    {
        var index = $(cell).parent('tr').children().index(cell);
        // Column order manager is in use
        if(cell[0].realOrderIndex)
        {
            index = cell[0].realOrderIndex
        }       
        showColumnByIndex(tableId,index+1);
    }   
}

function showColumnByIndex(tableId, columnIndex)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return;
    }   
    $(table).showColumns([columnIndex], {listTargetID: tableId+'_columnList',
        onClass:'hcmColumnShown', offClass:'hcmColumnHidden'});
}

function hideColumn(tableId, column)
{   
    // In case we are in a child of the td, find the td 
    var cell = $(column).closest('td');
    if (cell != null && cell.length && cell.length > 0)
    {
        var index = $(cell).parent('tr').children().index(cell);    
        // Column order manager is in use
        if(cell[0].realOrderIndex)
        {
            index = cell[0].realOrderIndex;
        }       
        
        hideColumnByIndex(tableId,index+1);
    }   
}

function hideColumnByIndex(tableId, columnIndex)
{
    var table = findTable(tableId);
    if (table == null)
    {
        return;
    }   
    $(table).hideColumns([columnIndex],{listTargetID: tableId+'_columnList',
        onClass:'hcmColumnShown', offClass:'hcmColumnHidden'}   
);
}

function showColumnMenu(tableId, button)
{
    $('#'+tableId+'_columnMenu').show();    
}


function showAssignOrCreateParentScreen(fgId,type)
{
	var itemList = [];
	itemList.push(fgId);
	if(type == 'CREATE'){
		var url = 'createParentFunctionalGroup.do?selectedPageKeys='+itemList+'&isTAMRedirect=true';
	}else if(type == 'ASSIGN'){
		 var url = 'assignToParentFunctionalGroup.do?selectedPageKeys='+itemList+'&isTAMRedirect=true';		
	}	
	 openParentFunctionalGroupModal(url);
}

function openParentFunctionalGroupModal(url)
{
	console.log(url);
	$('#mainModalFrame').attr('src', url);
    $('#fullscreen-main-page-modal').css("display",'');
    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
    parent.parent.fullScreenModal.on('closed',function(){
    	$('#mainModalFrame').attr('src', 'about:blank');
    	parent.parent.fullScreenModal.remove(true);
    	$('#fullscreen-main-page-modal').css("display",'none');
    });
    
    parent.parent.fullScreenModal.open();
    $('#mainModalFrame').attr('src', url);
    console.log('openParentFunctionalGroupModal url', url);
}

function resetRedirectValue(){
	$.ajax({
		type: "POST",
		url: 'resetRedirectValue.do',
		cache: false,
		dataType: 'text/plain'
	});
}

function openItemAssignmentModal(url)
{
	
	$.ajax({
		type: "POST",
		url: 'startItemManagement.do',
		cache: false,
		dataType: 'text html',
		data: {			
		},
		success: function(result) {
			console.log('startItemManagement success');
			parent.parent.$('#contentFrame').contents().find('html').css("overflow-y","hidden");
			$('#fullscreen-main-page-modal').css("display",'');
		    parent.parent.fullScreenModal =  new eto.Modal({ el: document.querySelector('#fullscreen-main-page-modal') });
		    $("#fullscreen-main-page-modal .eto-modal__header span").text("Item Assignment");
		    parent.parent.fullScreenModal.on('closed',function(){
		    	$('#mainModalFrame').attr('src', 'about:blank');
		    	parent.parent.fullScreenModal.remove(true);
		    	$('#fullscreen-main-page-modal').css("display",'none');
		    	parent.parent.$('#contentFrame').contents().find('html').css("overflow-y","");
		    });
		    parent.parent.fullScreenModal.open();
		    console.log('openItemAssigmentModal url', url);
		    $('#fullscreen-main-page-modal #mainModalFrame').attr('src', url);
		    
		},
		error: function(error){
			console.log('errored out startItemManagement.do');
		}
	});  
    
}
function reloadBreadCrumb(url){
	parent.BreadCrumbModule.getDom().contentFrame.setAttribute("src", url);
}
function validateEmailAddress(emailId){
	 var url =  /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	 return url.test(emailId);
}
function validateZipAddress(zipcode){
	var regex = /^(?:[A-Z0-9]+([- ]?[A-Z0-9]+)*)?$/;
	 return regex.test(zipcode);
	
}
</script>

<div class="eto-popover" data-anchor-x="right" data-anchor-y="middle" id="item-popover">
  <!--div class="eto-popover__content"></div-->
  <span class="eto-popover__caret"></span>
  <div id="mainPopoverDiv">
  </div>
</div>

<fmt:message var="title" key="item.detailTitle">
<fmt:param>
</fmt:param>
</fmt:message>

<script>
		var itemPopOverData = '<div class="container" id="item_details_table" style="width:140rem">';
			itemPopOverData += '<div><h3 id="popupItemIdentifier"><c:out value="${title}" /></h3><hr></div><div class="row"><div class="col-xs-3"><b><fmt:message key="item.itemId" /></b></div><div class="col-xs-3" id="popupItemKey"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.itemDescription" /></b></div><div class="col-xs-3" id="popupItemDescription" id="popupItemDescription" style="white-space: pre-wrap"></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.type" /></b></div><div class="col-xs-3" id="popupItemItemType"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="as.responsibility" /></b></div><div class="col-xs-3"><ul id="popupItemAssignment" style="margin-left: 5%"></ul></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.managedBy" /></b></div><div class="col-xs-3" id="popupItemManagedBy"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.revision" /></b></div><div class="col-xs-3" id="popupItemRevision"></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.version" /></b></div><div class="col-xs-3" id="popupItemVersion"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.isTopLevel" /></b></div><div class="col-xs-3" id="popupItemIsTopLevel"></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.category" /></b></div><div class="col-xs-3"><ul id="popupItemCategories" style="margin-left: 5%;"></ul></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.classification" /></b></div><div class="col-xs-3" id="popupItemItemClassification"></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.platform" /></b></div><div class="col-xs-3"><ul id="popupItemPlatforms" style="margin-left: 5%;"></ul></div>';		
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.state" /></b></div><div class="col-xs-3" id="popupItemState"></div><div class="col-xs-3"><b><fmt:message key="item.productUomCode" /></b></div>';
			itemPopOverData += '<div class="col-xs-3" id="popupItemProductUomCode"></div></div><div class="row"><div class="col-xs-3"><b><fmt:message key="item.inventory" /></b></div><div class="col-xs-3" id="popupItemInventory"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.businessName" /></b></div><div class="col-xs-3" id="popupItemBusiness"></div></div>';
			<c:if	test="${e2ofn:getConfigValue('pcm.bom.autoapprove.alternateItem')}">
				itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.alternates" /></b></div><div class="col-xs-3">';
				itemPopOverData += '<ul id="popupItemAlternates" style="margin-left: 5%;"></ul></div></div>';
			</c:if>
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.insertDate" /></b></div><div class="col-xs-3" id="popupItemInsertDate"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.updateDate" /></b></div><div class="col-xs-3" id="popupItemUpdateDate"></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-3"><b><fmt:message key="item.dataSource" /></b></div><div class="col-xs-3" id="popupItemDataSource"></div>';
			itemPopOverData += '<div class="col-xs-3"><b><fmt:message key="item.eol.state" /></b></div><div class="col-xs-3" id="popupItemEOLState"></div></div>';
			
			itemPopOverData += '<div class="eto-expand margin-top-xs-3" id="expand-example-2"><div class="eto-expand__toggle"><h4 class="eto-expand__h4">Show more</h4></div><div class="eto-expand__content margin-top-xs-1">';
			itemPopOverData += '<div class="row" id="fgRow"><div class="col-xs-12"><b><fmt:message key="item.functionalGroups" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12" ><ul id="popupItemFunctionalGroups" style="margin-left: 5%;"></ul></div></div>';
			itemPopOverData += '<div class="row" id="pfgRow"><div class="col-xs-12"><b><fmt:message key="functionalGroup.parentName" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemParentFunctionalGroups" style="margin-left: 5%;"></ul></div></div>';
				
				<!-- Additional Attributes -->
			itemPopOverData += '<div class="row"><div class="col-xs-12" ><b><fmt:message key="item.attributes" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemAttributes" style="margin-left: 5%;"></ul></div></div>';
			
			<!-- product family -->
			itemPopOverData += '<div class="row"><div class="col-xs-12" ><b><fmt:message key="item.productFamily" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemProductFamily" style="margin-left: 5%;"></ul></div></div>';
			
			<!-- Flex Attributes -->
			itemPopOverData += '<div class="row"><div class="col-xs-12" ><b><fmt:message key="item.flexAttributes" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemFlex" style="margin-left: 5%;"></ul></div></div>';
			
			<!-- Mfg Info -->
			itemPopOverData += '<div class="row"><div class="col-xs-12" ><b><fmt:message key="item.aml" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemAml" style="margin-left: 5%;"></ul></div></div>';
			
			<!-- Supplier Info -->
			itemPopOverData += '<div class="row"><div class="col-xs-12" ><b><fmt:message key="item.avl" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemAvls" style="margin-left: 5%;"></ul></div></div>';
			
			<!-- Where Used -->
			itemPopOverData += '<div class="row"><div class="col-xs-12"><b><fmt:message key="item.whereUsed" /></b></div></div>';
			itemPopOverData += '<div class="row"><div class="col-xs-12"><ul id="popupItemWhereUsed" style="margin-left: 5%;">';
			
			<c:if test="${e2ofn:getConfigValue('pcm.bom.incontext')}">
				itemPopOverData += '<ul id="popupItemWhereUsedContext" style="margin-left: 5%;"></ul>';
			</c:if>
			itemPopOverData += '</div></div></div></div></div>';
		modPopover = new eto.Popover({ el: document.querySelector('#item-popover')});
		modPopover.on('opened',function(query){
			$('#mainPopoverDiv').html(showWaitPopover);
		});
		modPopover.on('closed',function(query){
			$('#mainPopoverDiv').html('');
		});
	
	function inlinePopOver(response)
	{
		$('#mainPopoverDiv').html('');
		$('#mainPopoverDiv').html(itemPopOverData);
		aj = response;
	$('#popupItemKey').text(aj.item.itemKey);
	$('#popupItemDescription').text(aj.item.description);
	$('#popupItemRevision').text(aj.item.revision);
	$('#popupItemVersion').text(aj.item.version);
	$('#popupItemItemClassification').text(aj.item.itemClassification);
	$('#popupItemProductUomCode').text(aj.item.productUomCode);
	$('#popupItemInventory').text(aj.item.inventory);
	$('#popupItemInsertDate').text(aj.item.insertDate);
	$('#popupItemUpdateDate').text(aj.item.updateDate);
	$('#popupItemDataSource').text(aj.item.dataSource);
	$('#popupItemEOLState').text(aj.item.eolState);
	$('#popupItemItemNumber').text(aj.item.itemNumber);
	$('#popupItemIdentifier').text($('#popupItemIdentifier').text().concat(aj.item.itemIdentifier));
	
	var itemTypeValue;
	if (aj.item.itemType == 'I') {
		itemTypeValue = "Item";
	$('#popupItemItemType').text(itemTypeValue);
	}
	else if (aj.item.itemType == 'S') {
		itemTypeValue = "Supplier Item";
	$('#popupItemItemType').text(itemTypeValue);		
	}
	else if (aj.item.itemType == 'M') {
		itemTypeValue = "Mfg Item";
	$('#popupItemItemType').text(itemTypeValue);
	}
	else if (aj.item.itemType == 'PI') {
		itemTypeValue = "Phantom Item";
	$('#popupItemItemType').text(itemTypeValue);
	}
	else if (aj.item.itemType == 'CFG') {
		itemTypeValue = "CFG";
	$('#popupItemItemType').text(itemTypeValue);
	}
    else {
    	itemTypeValue = "Unknown Type";
    $('#popupItemItemType').text(itemTypeValue);
    }
	
	//Item Responsibility
    for(var i = 0; i < aj.item.assignment.length; i++){
    	var responsibilityList = document.createElement("li");
    	if(aj.item.assignment[i].region != null && !aj.item.assignment[i].region.isEmpty()) {
    		var textnode = document.createTextNode(aj.item.assignment[i].userId + '-' + aj.item.assignment[i].responsibility.responsibility + '-' +aj.item.assignment[i].region); 
    	}
    	else {
    		var textnode = document.createTextNode(aj.item.assignment[i].userId + '-' + aj.item.assignment[i].responsibility.responsibility); 	
    	}
    	   	
    	responsibilityList.appendChild(textnode);
    	document.getElementById("popupItemAssignment").appendChild(responsibilityList);
	}
    
    //Categories
    for(var i = 0; i < aj.item.categories.length; i++){
    	var categoriesList = document.createElement("li");
    	var categoriestextnode = document.createTextNode(aj.item.categories[i].categoryName); 
    	categoriesList.appendChild(categoriestextnode);
    	document.getElementById("popupItemCategories").appendChild(categoriesList);
	} 
    
    //product family
    if(aj.item.productFamily != null  && aj.item.productFamily != "") {
    var pf = aj.item.productFamily.split(',');
    for(var i = 0; i < pf.length; i++){
    	var productFamilyList = document.createElement("li");
    	var productFamilytextnode = document.createTextNode(pf[i]); 
    	productFamilyList.appendChild(productFamilytextnode);
    	document.getElementById("popupItemProductFamily").appendChild(productFamilyList);
    }    
	}  
       
    //Platforms
    for(var i = 0; i < aj.item.platforms.length; i++){
    	var platformsList = document.createElement("li");
    	var platformstextnode = document.createTextNode(aj.item.platforms[i].platformName);
    	platformsList.appendChild(platformstextnode);
    	document.getElementById("popupItemPlatforms").appendChild(platformsList);
	}  
   
    //Managed By
    var managedByValue=" Managed";
    if(aj.item.managedFlag != null && aj.item.managedFlag!='') {
    	$('#popupItemManagedBy').text(aj.item.managedFlag+managedByValue);		
	}
    else {
        itemTypeValue = "Not Set";
        $('#popupItemManagedBy').text(itemTypeValue);
	}
    
    $('#popupItemIsTopLevel').text("No");
    if(aj.item.isTopLevel) {
    $('#popupItemIsTopLevel').text("Yes");	
    }
    
    //State
    var state;
    if(aj.item.lifeCycleTypeCode == null && aj.item.lifeCycleTypeCodeOther != null) {
    $('#popupItemState').text(aj.item.lifeCycleTypeCodeOther);	   	
    }
    
    else if(aj.item.lifeCycleTypeCodeOther == null && aj.item.lifeCycleTypeCode != null) {
        $('#popupItemState').text(aj.item.lifeCycleTypeCode);	   	
        }
    
    else if(aj.item.lifeCycleTypeCode != null && aj.item.lifeCycleTypeCodeOther != null) {
    	 $('#popupItemState').text(aj.item.lifeCycleTypeCode + '/' + aj.item.lifeCycleTypeCodeOther);
    }
    	
   //Business
        $('#popupItemBusiness').text(aj.item.business.identifier + ':' + aj.item.business.type);
        
  //Alternate
        for(var i = 0; i < aj.item.alternates.length; i++){
        	var alternatesList = document.createElement("li");
        	var alternatestextnode = document.createTextNode(aj.item.alternates[i].alternateItem); 
        	alternatesList.appendChild(alternatestextnode);
        	document.getElementById("popupItemAlternates").appendChild(alternatesList);
    	}  
        
  //Functional Group
    if(aj.item.functionalGroups.length>0){
        for(var i = 0; i < aj.item.functionalGroups.length; i++){
        	var functionalGroupsList = document.createElement("li");
        	var functionalGroupstextnode = document.createTextNode(aj.item.functionalGroups[i].functionalGroups); 
        	functionalGroupsList.appendChild(functionalGroupstextnode);
        	document.getElementById("popupItemFunctionalGroups").appendChild(functionalGroupsList);
    	}  
        
      //ParentFunctional Group
        for(var i = 0; i < aj.item.functionalGroups.length; i++){
        	if(aj.item.functionalGroups[i].parentFunctionalGroups[i] != null) {
        	var parentfunctionalGroupsList = document.createElement("li");
        	var parentfunctionalGroupstextnode = document.createTextNode(aj.item.functionalGroups[i].parentFunctionalGroups[i]); 
        	parentfunctionalGroupsList.appendChild(parentfunctionalGroupstextnode);
        	document.getElementById("popupItemParentFunctionalGroups").appendChild(parentfunctionalGroupsList);
        	}
    	}
    } else{
    	$("#fgRow").remove();
    	$("#pfgRow").remove();
    }
        
      //Additional Attributes
        for(var i = 0; i < aj.item.attributes.length; i++){
        	var attributesList = document.createElement("li");
        	var attributestextnode = document.createTextNode(aj.item.attributes[i].attributeName + ":" + aj.item.attributes[i].attributeValue);
        	attributesList.appendChild(attributestextnode);
        	document.getElementById("popupItemAttributes").appendChild(attributesList);
    	}
        
      //Flex
        for(var i = 0; i < aj.item.flex.length; i++){
        	var flexList = document.createElement("li");
        	var flextextnode = document.createTextNode(Object.keys(aj.item.flex[i]) + ":" + Object.values(aj.item.flex[i])); 
        	flexList.appendChild(flextextnode);
        	document.getElementById("popupItemFlex").appendChild(flexList);
    	}
        
      //Aml
        for(var i = 0; i < aj.item.amls.length; i++){
        	var amlsList = document.createElement("li");
        	var amlstextnode = document.createTextNode(aj.item.amls[i].mfgBy + ":" + aj.item.amls[i].mfgItemNumber + ":" + aj.item.amls[i].mfgItemDescription); 
        	amlsList.appendChild(amlstextnode);
        	document.getElementById("popupItemAml").appendChild(amlsList);
    	}
        
      //Avls
        for(var i = 0; i < aj.item.avls.length; i++){
        	var avlsList = document.createElement("li");
        	var avlstextnode = document.createTextNode('Supplied By : ' + aj.item.avls[i].suppliedBy + ', Supplier Item Number : ' + aj.item.avls[i].supplierItemNumber + ', Supplier Item Description : ' + aj.item.avls[i].supplierItemDescription); 
        	avlsList.appendChild(avlstextnode);
        	document.getElementById("popupItemAvls").appendChild(avlsList);
    	}
        
      //WhereUsed
        for(var i = 0; i < aj.item.whereUsed.length; i++){
        	var whereUsedList = document.createElement("li");
        	var whereUsedContextList = document.createElement("li");
        	if (aj.item.whereUsed[i].description != null) {
        		whereUsedtextnode = document.createTextNode('Business Entity Name :' +  aj.item.whereUsed[i].businessEntityName + ', Name :' +  aj.item.whereUsed[i].name + ', Status :' + aj.item.whereUsed[i].status + ', Description :' + aj.item.whereUsed[i].description);       		
        	}
        	else if(aj.item.whereUsed[i].description == null) {
        		whereUsedtextnode = document.createTextNode('Business Entity Name :' +  aj.item.whereUsed[i].businessEntityName + ', Name :' +  aj.item.whereUsed[i].name + ', Status :' + aj.item.whereUsed[i].status);
        	}
        	
        	if (aj.item.whereUsed[i].contextType != null && aj.item.whereUsed[i].identifier != null) {
        	whereUsedContexttextnode = document.createTextNode('Context :' + aj.item.whereUsed[i].identifier +  '(' + aj.item.whereUsed[i].contextType + ')' );
        	whereUsedContextList.appendChild(whereUsedContexttextnode);
        	document.getElementById("popupItemWhereUsedContext").appendChild(whereUsedContextList);
        	}
        	whereUsedList.appendChild(whereUsedtextnode);
        	document.getElementById("popupItemWhereUsed").appendChild(whereUsedList);
    	}
		var BothAll = new eto.Expand({
			el : document.querySelector('#expand-example-2')
		});
	}
	
	function openPopOver(URL) 
	{
		$.ajax({
 		  type: "GET",
 		  url: "mcm/api/loadItemDetails/itemKey/"+URL,
 		  contentType: "application/json" ,
 		  success: function(response) {
			inlinePopOver(response);
          }
		});
	}
	 function getMultiSelectData(fieldName,arrData) {
			var url = "ajaxQuery"+fieldName+".do?multiSelect=true&q="+ encodeURIComponent(String(arrData));
						$.ajax({
							url : url,
							success : function(result) {
								var arr;
								if (result.includes("|")) {
									arr = result.split("|");
								} else {
									arr = result.split("\n");
								}
									let forData = arrData.split(";");
									arr = $.grep(arr, function(value) {
									    return value !== null && value !== undefined && value !== "" && value !== false && value !== 0;
									});
									invalidSelectItem[fieldName] = [...new Set(forData.filter(f => !arr.map(a => a.toLowerCase()).includes(f.toLowerCase())))];
									if(invalidSelectItem[fieldName].length>0)
										{
											$("#badge"+fieldName+" button span").html(invalidSelectItem[fieldName].length);
											$("#badgeCount"+fieldName).attr("style","display:'';padding:0px");
										}
									else
                                        {
                                            $("#badge"+fieldName+" button span").html("");
                                            $("#badgeCount"+fieldName).attr("style","display:none");
                                        }
									var invalidPopItem="";
									invalidSelectItem[fieldName].forEach((item) => {
									    invalidPopItem = invalidPopItem +'<span class="eto-tag eto-tag--sm float-xs-left eto-tag--error" style="width:100%;" id=tag_'+item+'>"<span class="eto-tag__label">'+item+
									    '</span><span tabindex="0"><i style="float:right;" translate="no" class="notranslate md-icon md-icon--sm" onClick="closeSingleTag(&#39;'+fieldName+'&#39;,&#39;'+item+'&#39;)">close</i></span></span>';
									});
									$("#badgePopover"+fieldName+" .eto-popover__content").html(invalidPopItem);
									$("#badgePopover"+fieldName+" .eto-popover__caret").html("");
							}
						});
						 $("#badge"+fieldName).show();
        		}
	 
	function addElementTags(elementName,value) {
		 var arrData = [...new Set(value)];
		 var completeHTML= "";
		 for (var i = 0; i < arrData.length; i++) {
	        var newTagHTML = "<span class='eto-tag eto-tag--sm'><span class=eto-tag__label>"+arrData[i]+"</span><span class=eto-tag__remove tabindex=0 onclick='closeSingleTag(&#39;"+elementName+"&#39;,&#39;"+arrData[i]+"&#39;)' ><i class='md-icon md-icon--sm'>close</i></span></span>";
	        completeHTML =completeHTML+ newTagHTML;
		 }
		 $("#viewTag"+elementName).html(arrData.length);
		 $("#autocomplte_tag_"+elementName).html(completeHTML);
		 if(arrData.length>0)
         {
			 $("#badge"+elementName).show();
			 $("#viewTag"+elementName).show();
			 $("#autocomplte_tag_"+elementName).show();
			 $("#badge"+elementName).show();
		 	 $("#tagContainer"+elementName).show();
          } else {
        	  $("#badge"+elementName).hide();
 			 $("#viewTag"+elementName).hide();
 			 $("#autocomplte_tag_"+elementName).hide();
 			 $("#badge"+elementName).hide();
 		 	 $("#tagContainer"+elementName).hide();
        	  
         }	
	 }
</script>
