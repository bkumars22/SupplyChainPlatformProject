<%@tag description="Provides a column in the dashboard"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="dbColumnCount" value="${dbColumnCount +1}" scope="request"/>
<td class="dashboard-column" valign="top" id="col-${dbColumnCount}">
<jsp:doBody/>
</td>
