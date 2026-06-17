<!--[if IE ]><!doctype html><![endif]-->
<%@ include file="common.jspf"%>
<%
String walkMeEnabled = "False";
String allowPerfAnalyticCookies = "False";
Cookie[] cookies = request.getCookies();
if (cookies != null && cookies.length > 0) {
	for (Cookie cookie : cookies) {
		if (cookie.getName().equals("allowFunctionalCookies")) {
	   		walkMeEnabled = cookie.getValue();
   		} else if(cookie.getName().equals("allowPerfAnalyticCookies")){
   			allowPerfAnalyticCookies = cookie.getValue();
   		}
  	}
}
%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<e2i2:skin name="e2-modern" />
<e2i2:dhtml padsupport="yes" />
<e2i2:clientcache enabled="no" />
<e2ot:pcmSupport calendarSupport="false" ajaxSupport="true" />
<script type="text/javascript" src="./js/menuModule.js"></script>
<script type="text/javascript" src="./js/breadCrumbModule.js"></script>


<style type="text/css">
.eto-breadcrumbs {
	margin-left: 2rem;
}
#popup_modal_body ul {
list-style: none;
}
</style>
<script>
	var mcmApp = {};
	mcmApp.dom = {};
	mcmApp.searchFields = {};
	mcmApp.uploadFileType = "";
	mcmApp.enableSoftErrors = false;
	mcmApp.overrideReadonly = false;
	mcmApp.disableValidate = false;
	mcmApp.enableTabs = "";
	mcmApp.extractType = "";
	var reportCall;
	mcmApp.activeTabs = "";
	mcmApp.selectedOption = "";
	mcmApp.toast=null;
	var fullScreenModal;
	var popupModal;
	var lastUrl = "";
	mcmApp.removeBreadcrumbs = false;
	var reloadPage;
	var finderModal;
	var queryString = "";
	function isWalkmeUserBehaviorDataCollectionEnabled() {
		return ("true" === "<%= allowPerfAnalyticCookies %>".toLowerCase());
	}
	
	function sizeFrame() {
		console.log('in sizeFrame');
		if (jQuery.support.boxModel) {
			var h = $('html').height() - 80; //find the height of the internal page 
			if ($('#contentFrame').height() != h) {
				window.status = 'new size ' + h;
				$('#contentFrame').height(h);
			}
		}
	}

	function toggleNavarea(button) {
		console.log('in toggleNavarea');
		var toggleicon = document.getElementById('navareatogglericon');
		var bar = document.getElementById('navbar');
		if (toggleicon.src.indexOf('close') > 0) {
			toggleicon.src = i2uiImageDirectory + '/nav_pad_norgie_open.gif';
			bar.style.display = 'none';
			bar.parentNode.width = '1px';
		} else {
			toggleicon.src = i2uiImageDirectory + '/nav_pad_norgie_close.gif';
			bar.style.display = '';
			bar.parentNode.width = '100px'; // use something less than the nav area
		}
	}

	function loadContentFrame(url) {
		console.log('in loadContentFrame');
		var cf = frames['contentFrame'];
		cf.document.body.style.cursor = 'wait';
		cf.document.body.style.fontFamily = 'verdana,arial,helvetica,sans-serif';
		cf.document.body.innerHTML = '&nbsp;Loading page...';
		cf.location.href = url;
	}

	function goShowContentPageHelp() {
		console.log('in goShowContentPageHelp');
		var cf = frames['contentFrame'];
		var helpContext = null;
		if (cf != null) {
			helpContext = findMetaTag('MCMHELP', cf.document);
		}
		showHelp(helpContext);
	}

	function goNavigateStart(anchor) {
		console.log('in goNavigateStart');
		console.log('anchor', anchor);
		var cf = frames['contentFrame'];
		console.log('cf', cf);
		if (typeof (cf.canLeavePage) == 'function') {
			console.log('does it go for partial');
			var fr = partial(goNavigateStartCallback, anchor);
			if (cf.canLeavePage(fr) == false) {
				return false;
			}
		}
	}

	function goNavigateStartCallback(anchor) {
		console.log('in goNavigateStartCallback');
		var cf = frames['contentFrame'];
		if (typeof (cf.showWaitBusy) == 'function') {
			cf.showWaitBusy();
		}
		cf.location.href = anchor;
	}

	/*Commenting resize code due to screen resolution problem */
	function initPage() {
		if(window.location.search !=''){
			queryString = getRequests();
			}
			if (self != top) {
				top.location = 'main';
				return;
			}
			try {
				document.onhelp = function() {
					goShowContentPageHelp();
					return false;
				};
				//window.onresize =  sizeFrame;

				//sizeFrame();	   
				i2uiTilePads();
			} catch (e) {

			}
		    /* if(queryString!=""){
				redirectPage();	
			} */
		}
		function redirectPage(){
			if(typeof queryString["url"] != 'undefined' && queryString["exceptionid"] !='undefined'){
			var url = queryString["url"]+"?exceptionID="+queryString["exceptionid"];
			parent.BreadCrumbModule.getDom().contentFrame.setAttribute("src", url);
			}
		}
		function getRequests() {
		    var s1 = location.search.substring(1, location.search.length).split('&'),
		        r = {}, s2, i;
		    for (i = 0; i < s1.length; i += 1) {
		        s2 = s1[i].split('=');
		        r[decodeURIComponent(s2[0]).toLowerCase()] = decodeURIComponent(s2[1]);
		    }
		    return r;
		}
		function trimValue(field) {
		var val = field.value;
		field.value = val.replace(/^\s+|\s+$/g,"");
	}
</script>
<jsp:useBean id="date" class="java.util.Date" />
<fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" />

<fmt:message var="title" key="product.title" />
<title>${title}<c:if
		test="${appContext.currentRole.roleId == 'ADMIN'}">&nbsp;(${e2ofn:getConfigValue('e2.hostname')})</c:if></title>
</head>
<fmt:message var="help" key="label.help" />
<fmt:message var="exit" key="label.exit" />
<fmt:message var="support" key="label.support" />
<fmt:message var="about" key="label.about" />
<fmt:message var="userTitle" key="label.userTitle">
	<fmt:param>
		<c:out value="${appContext.currentUser.userId}" />
	</fmt:param>
	<fmt:param>
		<c:out value="${appContext.currentUser.userName}" />
	</fmt:param>
</fmt:message>
<fmt:message var="roleTitle" key="label.roleTitle">
	<fmt:param>
		<c:out value="${appContext.currentRole.roleName}" />
	</fmt:param>
</fmt:message>
<c:set var="helpAction"
	value="<a href='javascript:goShowContentPageHelp()'>${help}</a>" />
<c:set var="supportAction"
	value="<a target=support href='../common/support.do' onclick='showHelpPage(this.href);return false'>${support}</a>" />
<c:set var="aboutAction"
	value="<a target=about href='../common/about_e2modern.jsp' onclick='showAboutPage(this.href)'>${about}</a>" />
<c:set var="exitAction"
	value="<a target=_top href='logout.do'>${exit}</a>" />
<c:set var="brandingURL" value="../common/skins/e2-standard/branding" />

<e2o:shell framed="no" onload="initPage()" username="${userTitle}"
	rolename="${roleTitle}"
	actions="${helpAction}&nbsp;&nbsp;&nbsp;${supportAction}&nbsp;&nbsp;&nbsp;${aboutAction}&nbsp;&nbsp;&nbsp;${exitAction}"
	background="" logo="${brandingURL}/main_logo_white.png"
	logoBackground="#1d426b">

	<table cellpadding=0 cellspacing=0 width="100%" height="100%"
		id="shellTable">
		<tr>
			<fmt:message var="padTitle" key="product.name" />
			<fmt:message var="dashboard" key="navigator.dashboard.title" />
			<fmt:message var="utilities" key="navigator.utilities.title" />
			<fmt:message var="reports" key="navigator.reports.title" />
			<fmt:message var="submitData"
				key="navigator.utilities.submitdata.title" />
			<td>
				<table cellpadding=0 cellspacing=0 width="100%" height="100%">
					<tr>
						<td><iframe
								style="border-top: 1px solid #999999; border-left: 1px solid #999999; border-bottom: 1px solid #ffffff; border-right: 1px solid #ffffff; padding: 0px;"
								id="contentFrame" name="contentFrame" frameborder="0"
								marginheight="0" marginwidth="0" width="100%" height="100%"
								src="${!empty param.bmAction ? param.bmAction : sessionScope['homepage'] != null ? sessionScope['homepage'] :'welcome'}">
							</iframe></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</e2o:shell>
<div class="eto-modal" id="popup_modal">
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
<div id="add-fav-modal" role="dialog" class="eto-modal">
	<div class="eto-modal__content col-sm-4 col-md-3 col-lg-3" tabIndex="0">
		<header class="eto-modal__header">
			<span>Add to favorites</span>
			<button type="button" class="eto-modal__close" data-modal-close=""
				aria-label="close"></button>
		</header>
		<section class="eto-modal__body">
		<div id="errorpopUp"></div>
			<div id="favNameDiv" class="eto-input required">
				<label class="eto-input__label">Name</label> <input
					class="eto-input__field" type="text" name="favName" id="favName"
					  onblur="trimValue(this)" /> <span
					class="eto-input__message" role="alert" aria-live="polite"></span>
			</div>
		</section>

		<footer class="eto-modal__footer">
			<button type="button" data-modal-close=""
				id="_01f988a0-f51e-4036-9bdc-3bdb2cb23df9" class="eto-btn">Cancel</button>
			<button type="button" id="okBtn" class="eto-btn eto-btn--primary"
				onclick="MenuModule.saveFavClicked()">Save</button>
		</footer>
	</div>
	</div>
	

	<%@ include file="launchpad.jspf"%>
	<script type="text/javascript" src="./js/launchpadModule.js"></script>
	<div class='eto-toast-container' id='global-toast-message-block'></div>
	<script>
	   BreadCrumbModule.init();
	   mcmApp.toast=new eto.ToastContainer({el:document.querySelector('#global-toast-message-block')});
	</script>
	
	
	<!-- <div class='eto-toast-container' id='toast-error-message-block'></div> -->
</html>
