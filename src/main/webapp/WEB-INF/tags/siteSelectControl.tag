<%@tag display-name="siteSelectControl" 
	description="Generates a select with nested level of sites" small-icon="" %>
<%@ attribute name="sites" required="true" type="java.util.Collection"%>
<%@ attribute name="markedSites" required="false" type="java.util.Collection"%>
<%@ attribute name="markedSiteValue" required="false"%>
<%@ attribute name="markedSiteStyle" required="false"%>
<%@ attribute name="indentValue" required="true"%>
<%@ attribute name="onchange" required="false"%>
<%@ attribute name="styleId" required="false"%>
<%@ attribute name="style" required="false"%>
<%@ attribute name="property" required="true"%>
<%@ attribute name="value" required="false"%>
<%@ attribute name="disabled" required="false"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn" %>
<fmt:setBundle basename="scplatform-messages" />
<c:set var="lastLevel" value="0"/>
<c:set var="indent" value=""/>
<select ${disabled ? 'disabled' :''} style="${style}"  class="eto-select__field"
    onchange="${onchange}" Id="${styleId}" 
    name="${property}">
<jsp:doBody/>
<c:forEach var="site" items="${sites}"> 
 <c:choose>
    <c:when test="${site.level > lastLevel }"><c:set var="indent" value="${indent}${indentValue}"/></c:when>
    <c:when test="${site.level < lastLevel }"><c:set var="indent" value="${fn:substring(indent,fn:length(indent)-fn:length(indentValue),-1)}"/></c:when>
 </c:choose>
<c:set var="inuse" value=""/>
<c:if test="${e2ofn:contains(markedSites,site.siteKey)}"><c:set var="inuse" value="${markedSiteStyle}"/></c:if>
<option value="${site.siteKey}" style="${inuse}" ${value==site.siteKey?'selected':''}>
 ${indent}<c:if test="${e2ofn:contains(markedSites,site.siteKey)}"><c:out value="${markedSiteValue}"/></c:if>
    <c:out value="${site.siteDescription} ${site.defaultCurrencyCode}"/>
 </option>
 <c:set var="lastLevel" value="${site.level}"/>
</c:forEach> 
</select>
