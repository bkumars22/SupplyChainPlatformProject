<%@ tag %>
<%@ attribute name="calendarSupport" type="Boolean"%>   
<%@ attribute name="ajaxSupport" type="Boolean"%>
<%@ attribute name="fullModalPopUp" type="Boolean"%>
<%@ attribute name="enableF1" required="false" type="Boolean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="e2ot" %>
<fmt:setBundle basename="scplatform-messages" />
<c:if test="${empty pcmSupportEnabled}">
   <c:set var="pcmSupportEnabled" value="true" scope="page"/>
   <e2i2:dhtml datepickersupport="no"/>   
   <e2i2:stylesheet path="/style_sheet_core.css"/>  
   <script type="text/javascript" src="js/jquery-3.6.2.min.js"></script>
   <script type="text/javascript" src="js/jquery.cookie.js"></script>
   <script type="text/javascript" src="js/jquery.columnmanager.js"></script>
   <script type="text/javascript" src="js/jquery.columnorder.js"></script>
   <script type="text/javascript" src="js/jquery.clickmenu.js"></script>
   <script type="text/javascript" src="js/jquery.simplemodal-1.4.4.js"></script>
   <script type="text/javascript" src="js/jquery-ui.js"></script>
   <link rel="stylesheet" type="text/css" href="skins/e2-modern/style_sheet_core.css"/>
   <link rel="stylesheet" type="text/css" href="css/jquery.clickmenu.css"/>
   <link rel="stylesheet" type="text/css" href="skins/edl/css/edl.min.css"/>
   <script type="text/javascript" src="js/polyfill.min.js"></script>
   <script type="text/javascript" src="js/classnames.js"></script>
   <script type="text/javascript" src="skins/edl/js/edl.min.js"></script>
   
   <!--  
   <script type="text/javascript" src="skins/header/index.js"></script>
   -->
   
   
   <c:if test="${ajaxSupport == true}">
        <link rel="stylesheet" type="text/css" href="css/jquery.autocomplete.css"/>
		<script src="js/jquery.autocomplete.js"></script>
        <c:set var="pcmAjaxEnabled" value="true" scope="page"/>
   </c:if>
   <!-- script type="text/javascript" src="js/pcmcommon.jsp"></script-->     
   <%@ include file="/js/pcmcommon.jsp" %>
   <c:if test="${calendarSupport == true}">
      <e2ot:calendarSupport/>
   </c:if>
	<script>
	function showSearchBusy()
	{	
	    document.body.style.cursor='wait';
	    window.setTimeout("showBusyDialog('<fmt:message key='msg.searching.title'/>','<fmt:message key='msg.searching'/>')",1000);		
	}
	
	function showWaitBusy()
	{
	    document.body.style.cursor='wait';
	    window.setTimeout("showBusyDialog('<fmt:message key='msg.working.title'/>','<fmt:message key='msg.working'/>')",1000);		
	}
	
	function loading(){
		window.setTimeout("showBusyDialog('<fmt:message key='msg.working.title'/>','<fmt:message key='msg.working'/>')",1000);
	}

	function showWaitBusy(timeout)
	{
	    document.body.style.cursor='wait';
	    if (timeout < 1)
	    {
	    	showBusyDialog('<fmt:message key='msg.working.title'/>','<fmt:message key='msg.working'/>');
	    }
	    else
	    {
	    	window.setTimeout("showBusyDialog('<fmt:message key='msg.working.title'/>','<fmt:message key='msg.working'/>')",timeout);
	    }		
	}

	function loadCompleted(){
		window.setTimeout("closeBusyDialog()",1000);
	}
	
	function closeWaitBusy()
	{
		document.body.style.cursor='default';
		window.setTimeout("closeBusyDialog()",1000);
	}
	
	function showModalMessageBox(title, message)
	{		
		
		 parent.popupModal = new eto.Modal({ el: parent.document.querySelector('#popup_modal',parent.document) });
		   parent.$('#popup_modal_header').text(title);
		   parent.$('#popup_modal_body').text(message);
		   parent.$('#popup_modal_footer').html('<button class="eto-btn eto-btn--primary" type="button" data-modal-close id="popup_modal_okButton" onclick="">OK</button>');
		   parent.popupModal.on('closed', function(query) {		
			});
		   
		   parent.popupModal.open();
		   
		   
	/* 	var body = "<div id='msgBox'><div id='msgBoxTitle' class='simplemodal-title'>";
		body += title;
		body += "</div><div class='simplemodal-body'>";
		body += message;
		body += "</div>";
		body += "<div style='padding-top:6px;width:100%;text-align:center'>";
		body += "<input id='msgBoxOK' class='simplemodel-container-button' ";
		body += "type='submit' value='Ok' onclick='$.modal.close();'/></div></div>";
		
		var msgBox = $(body);
		msgBox.modal({
			opacity:0,close:true,
			onShow: function (dialog) {
			    dialog.container.draggable({handle: "msgBoxTitle"});
			  }
			});
		$("#msgBoxOK").focus(); */
	}
	<c:if test="${empty enableF1 || enableF1 == true}">
	// This will allow F1 to work on all pages
	document.onhelp = function(){goShowPageHelp();return false;};
	</c:if>	 
	</script>
</c:if>
