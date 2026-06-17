<%@ attribute name="bindToFieldId" required="true"%>
<%@ attribute name="defaultDate"%>
<%@ attribute name="clearTitle"%>
<%@ attribute name="buttonTitle"%>
<%@ attribute name="disabled"%>
<%@ attribute name="onCloseHandler"%>
<%@ attribute name="firstDayOfWeek"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:if test="${empty disabled}"><c:set var="disabled" value="false"/></c:if>
<c:if test="${empty onCloseHandler}"><c:set var="onCloseHandler" value="null"/></c:if>
<c:if test="${empty firstDayOfWeek}"><c:set var="firstDayOfWeek" value="null"/></c:if>
<c:if test="${empty defaultDate}"><c:set var="defaultDate" value="null"/></c:if>
<e2i2:img alt="${buttonTitle}" disabled="${disabled ? 'yes':'no'}" src="/calendar.gif" onclick="javascript:showCalendarPopup('${bindToFieldId}',${defaultDate},${onCloseHandler},${firstDayOfWeek})" />
<c:if test="${!empty clearTitle && disabled == false}">
<e2i2:img src="/clear.gif" alt="${clearTitle}" onclick="javascript:clearField(document.getElementById('${bindToFieldId}'))"/>
</c:if>
