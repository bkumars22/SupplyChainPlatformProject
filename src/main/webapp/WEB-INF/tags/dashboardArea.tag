<%@tag description="Provides a movable area in the dashboard"%>
<%@ attribute name="id" required="true" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="titleKey" required="false" %>
<%@ attribute name="subTitle" required="false" %>
<%@ attribute name="subTitleKey" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="scplatform-messages" />
<div class="dashboard-area" id="${id}">
<div class="dashboard-area-title" title="<fmt:message key="label.dragEnabled"/>">
<table style="width:100%" cellspacing="0" cellpadding="0">
<tr style="margin:0px;padding:0px;">
<td style="font-weight:bold;padding:2px">
<c:if test="${!empty title}">${title}</c:if>
<c:if test="${!empty titleKey}"><fmt:message key="${titleKey}"/></c:if>
<c:if test="${!empty subTitle}"><span style="font-style:italic;font-weight:normal"><br>&nbsp(${subTitle})<span></c:if>
<c:if test="${!empty subTitleKey}"><span style="font-style:italic;font-weight:normal"><br>(<fmt:message key="${subTitleKey}"/>)<span></c:if>
</td>
<td style="width:20px;background: url(css/move.gif) right top no-repeat;">&nbsp;</td>
</tr></table>
</div>
<div class="dashboard-area-body">
<jsp:doBody/>
</div>
</div>
