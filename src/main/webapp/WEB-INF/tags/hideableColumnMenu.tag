<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="tableId" required="true"%>
<%@ attribute name="style" required="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<fmt:setBundle basename="scplatform-messages" />
<fmt:message var="showTitle" key="label.showColumnMenu"/>
<c:if test="${empty style}"><c:set var="style" value="width:200px;list-style: inside"/></c:if>
<ul id="${tableId}_menu" class="clickMenu">
<li><e2i2:img alt="${showTitle}" src="/dropdown.gif"/>
<%--Start by not showing --%>
<ul id="${tableId}_columnList" style="${style};display:none">
<li class="hcmColumnShown" style="font-weight:bold;"
   onclick="javascript:showAllColumns('${tableId}',true)"
   title="<fmt:message key="label.showColumns"/>"><fmt:message key="label.showAll"/></li>
</ul>
</li>
</ul>
<script>
$(document).ready(function()
{
	$('#${tableId}_menu').clickMenu({onClick: function(){}});
	if(findTable('${tableId}') != null)
	{
		$('#${tableId}_columnList').show();
	}	
});
</script>
