<%@ include file="../common.jspf"%>
<%@ include file="../search/bomOptionalFeatures.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="MDM-BOM" />
</head>

<%@ include file="bomManagementSearchFunctions.jspf"%>

<body onload="init()">
    <c:set var="count" value="0" />
     <%@ include file="message.jspf"%>
     <%@ include file="bomManagementSearchForm.jspf"%>
	 <%@ include file="bomManagementEventActionSupport.jspf"%>
	 <%@ include file="../search/viewBomDetailsForm.jspf"%>
</body>

<%@ include file="bomManagementDomManipulation.jspf"%>
</html>
