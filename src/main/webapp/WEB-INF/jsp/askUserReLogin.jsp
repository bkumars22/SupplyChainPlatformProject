<%@ include file="common.jspf"%>
 <e2i2:skin name="e2-modern" />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="false" ajaxSupport="true" />
<script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
  <link rel="stylesheet" type="text/css" href="skins/edl/css/edl.min.css" />
<script type="text/javascript" src="skins/edl/js/edl.min.js"></script>
<script>
$(document).ready(function() {

	if($('#header-parent').length){
		$("#header-parent").hide();
	}
});

function goSubmit() {
	$("#actionType").val("continueWithSameId");
	document.forms[0].action = "authenticate"
	document.forms[0].submit();
}


	function doCancle() {
		$("#actionType").val("Cancel");
		document.forms[0].action = "cancleRelogin"
		document.forms[0].submit();
	}
	function initPage() {
		$("#header-parent").hide();
	}

</script>
<style type="text/css">
#header-parent{
display:none;
}
body{
        overflow: hidden;
    }

    .eto-breadcrumbs {
	   margin-left: 2rem;
    }

</style>
</head>
<body onload="initPage()">

<center>
	<div class="eto-card" style="width: 400px">
				<section class="eto-card__body">
				<form method="Get">
				<input type="hidden" name="actionType" id="actionType" value=""/>
				<h4>
					<fmt:message key="error.session.expired" />
				</h4>
				<div class="eto-btn-group" style="float:right;">
				<button type="button" onclick="javascript:doCancle();" class="eto-btn">Cancel</a>
				<button type="submit" onclick="javascript:goSubmit();" class="eto-btn eto-btn--primary">OK</a>
				</div>
				</form>
				</section>
				</div>
				</center>
</body>
</html>