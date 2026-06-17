<%@ include file="../common.jspf"%>
<%@ include file="bomOptionalFeatures.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-BOM" />
</head>

<%@ include file="bomSearchScript.jspf"%>  
<body onload="init()">	
	<%@ include file="submitBomSearchForm.jspf"%>
	<%@ include file="viewBomDetailsForm.jspf"%>
</body>
<%@ include file="bomSearchDomManipulation.jspf"%>
</html>
