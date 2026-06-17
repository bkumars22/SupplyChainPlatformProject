<%@tag display-name="abbreviate" 
	description="Generates the body of the tag, and truncates to the length specificed" small-icon="" %>
<%@ attribute name="length" required="true"%>
<%@ attribute name="style" required="false"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn" %>
<fmt:setBundle basename="scplatform-messages" />
<jsp:doBody var="bodytext"/>
<c:choose>
<c:when test="${fn:length(bodytext) > length}">
<fmt:message var="etitle" key="label.expand"/>
<fmt:message var="ctitle" key="label.contract"/>
<div style="${style}">
<span title="${bodytext}">
${fn:substring(bodytext,0,length)}
</span><span style="cursor:pointer;" title="${etitle}" 
onclick="$(this).next('span').css('display','');$(this).css('display','none');">
 ...</span><span style="display:none;cursor:pointer;white-space:normal" title="${ctitle}"
onclick="$(this).prev('span').css('display','');$(this).css('display','none');">${fn:substring(bodytext,length,-1)}</span>
</div>
</c:when>
<c:otherwise><c:out value="${bodytext}"/></c:otherwise>
</c:choose>
