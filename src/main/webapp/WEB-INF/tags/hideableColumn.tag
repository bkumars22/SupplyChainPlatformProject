<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="tableId" required="true"%>
<%@ attribute name="key" required="false"%>
<%@ attribute name="showIcon" required="false" type="Boolean"%>
<%@ attribute name="clickToHide" required="false" type="Boolean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<fmt:setBundle basename="scplatform-messages" />
<fmt:message var="hideTitle" key="label.hideColumn"/>
<c:if test="${clickToHide}">
<span onclick="javascript:hideColumn('${tableId}',this)" title="${hideTitle}"
   class="hcmColumnTitle" onmouseover="this.className='hcmColumnTitleHover'" 
   onmouseout="this.className='hcmColumnTitle'">
</c:if>
<c:if test="${!empty key}"><fmt:message key="${key}"/></c:if>
<jsp:doBody/>
<c:if test="${clickToHide}"></span></c:if>
<c:if test="${showIcon}">
<img style="cursor:pointer" src="css/hidecol.gif" onclick="javascript:hideColumn('${tableId}',this)" title="${hideTitle}"/>
</c:if>
