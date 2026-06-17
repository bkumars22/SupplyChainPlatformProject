<%@ tag display-name="autoCompleteField"
	description="Autocomplete field" small-icon=""%>
<%@ attribute name="fieldId" required="true"%>
<%@ attribute name="finderName" required="true"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<fmt:setBundle basename="scplatform-messages" />
<fmt:message var="suggestFieldTitle" key="label.suggestAvailable" />
<jsp:doBody />
${fieldId}
<span style="vertical-align: super; padding: 0px; margin: 0px"
	title="${suggestFieldTitle}"> <e2i2:img
		src="/action_nav_pad.gif"
		onclick="javascript:document.getElementById('${fieldId}').autocompleter.flushCache()" />
</span>

<script>
</script>
