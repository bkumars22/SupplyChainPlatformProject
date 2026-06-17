<%@tag%>
<%@ attribute name="formName" required="true"%>
<%@ attribute name="numColumns" required="true"%>
<%@ attribute name="showFilter" required="true"%>
<%@ attribute name="resultTableId" required="false"%>
<%@ attribute name="showSaveFilter" required="false"%>
<%@ attribute name="showFilterCollapsed" required="true"%>
<%@ attribute name="searchFields" required="true"
	type="java.util.Collection"%>
<%@ attribute name="disabled" required="true"%>
<%@ attribute name="alertCount" %>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="e2ot"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<fmt:setBundle basename="scplatform-messages" />

<script>
function onMultiTextCallback(idx,finderValues)
{
	<c:set var="delim" value="${e2ofn:getConfigValue('pcm.web.search.multiValueDelimiter')}" />
	<c:if test="${empty delim}">
		<c:set var="delim" value="," />
	</c:if>
	var item = document.getElementById('searchField'+idx);
	for (var i=0; i < finderValues.length; i++)
	{
		if(!isValueEmpty(item.value))
		{
			item.value = item.value.concat('${delim}'+finderValues[i][1]);
		}
		else
		{
			item.value = finderValues[i][1];
		}
	}
}

<c:forEach var="field" items="${searchFields}" >
<c:if test="${!empty field.finderName}"> 
$(document).ready(function() 
			{
				$("#${field.name}").autocomplete(
					"ajaxQuery${field.finderName}",
					{
						delay:800,
						minChars:1,
						matchSubset:0,
						maxItemsToShow:20,						
						matchContains:0,
						cacheLength:1,
						extraParams:{${field.properties['finderParams']}},
						autoFill:false
					}
				);
			});   
</c:if>
</c:forEach>

</script>

<c:choose>
	<c:when test="${showFilter}">
		<!-- e2i2:container id="searchContainer" title="${title}"
			collapsable="yes" -->
			<div class="searchContainer">
				<div class="row">
					<c:set var="rowCount" value="0" />
					<c:forEach var="field" items="${searchFields}" varStatus="fieldCount">
						<c:set var="rowCount" value="${rowCount + 1}" />
					<e2ot:alertField field="${field}"
						autoSubmitEnabled="false" disabled="${disabled}"
						fieldCount="${alertCount}${fieldCount.count}" alertCount="${alertCount}"/>
					</c:forEach>
				</div>
				</div>
		<!-- /e2i2:container-->
	</c:when>
	<c:otherwise>
		<e2o:errors />
	</c:otherwise>
</c:choose>
