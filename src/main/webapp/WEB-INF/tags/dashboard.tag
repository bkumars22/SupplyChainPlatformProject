<%@tag description="Provides a column based dashboard that allows the user to change"%>
<%@ attribute name="id" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<table width="100%" id="${id}" class="dashboard">
<c:set var="dbColumnCount" value="0" scope="request"/>
<tr valign="top">
<jsp:doBody/>
</tr>
</table>
<script>
	$(".dashboard-column").sortable({items:'div.dashboard-area',
		opacity:0.6, connectWith:'.dashboard-column',
		cursor: 'move' });
	$(".dashboard-column").disableSelection();
</script>
