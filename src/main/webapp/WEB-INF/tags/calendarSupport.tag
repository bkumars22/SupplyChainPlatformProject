<%@ tag %>
<%@ attribute name="path"%>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${empty calendarSupportEnabled}">
<c:set var="calendarSupportEnabled" value="true" scope="page"/>
<link rel="stylesheet" type="text/css" media="all" href="jscalendar-1.0/calendar-scplatform.css"/>
<script type="text/javascript" src="jscalendar-1.0/calendar.js"></script>
<script type="text/javascript" src="jscalendar-1.0/lang/calendar-en.js"></script>
<script type="text/javascript" src="jscalendar-1.0/calendar-scplatform.js"></script>
<script>
setUserDateFormat('${appContext.currentDateFormat}');
</script>
</c:if>
