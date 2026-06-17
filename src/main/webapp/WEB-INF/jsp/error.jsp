<% response.sendRedirect("http://localhost:3000"); %>
<%@ include file="common.jspf"%>
<%@ page import="com.scplatform.pcm.item.entity.Item"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@ page import="java.util.ArrayList"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
</head>
<script>
	function goHomepage() {
		window.location.href = 'welcome';
		reloadBreadCrumb('welcome');
	}	
</script>
<style type="text/css">
.anchorLike:hover {
	cursor: pointer;
}

.anchorLike {
	color: #428bca;
	text-decoration: none;
}
</style>
<body>
<div style="padding-top: 30px;background-image:url('skins/e2-modern/images/CommonError.png');position: fixed;height: 100%;width: 100%;">
<span style="font-weight: bold;margin-left: 17px;font-size: 40px;"><fmt:message key="info.Common.Error.Message" /></span>
<div style="display: flex;margin-left: 22px;padding-top: 15px;">
<span><fmt:message key="info.Common.Error.Second.Message"/></span>
</div>
<div style="display: flex;margin-left: 25px;padding-top: 50px;">
<i class="md-icon" style=margin-right: 5px;">home</i>
<label class="eto-input__label" style="font-size: smaller;margin-left: 7px;"><a href="javascript:goHomepage();"><fmt:message key="info.Common.Error.HomePage" /></a></label>
</div>
<div style="display: flex; margin-left: 25px;padding-top: 15px;">
<i class="md-icon" style=margin-right: 5px;">mail</i>
<label class="eto-input__label" style="font-size: smaller;margin-left: 7px;"><a href="https://github.com/bkumars22/SupplyChainPlatformProject" target="_blank"><fmt:message key="info.Common.Error.Support" /></a></label>
</div>
</div>	
</body>
</html>