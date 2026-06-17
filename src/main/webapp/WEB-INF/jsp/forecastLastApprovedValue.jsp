<%@ include file="../common.jspf"%>
<e2i2:stylesheet path="/style_sheet_core.css" default="yes" inline="no"/>
<html>
<body scroll="no" class="messageBoxBackground">
<table>
	<tr>
		<th colspan='2' style='font-weight: bold; font-size: 18px;'
			align='center'><fmt:message key="fc.last.approvedval"/></th>
	</tr>
	<tr>
		<td style='font-weight: normal; font-size: 15px;' align='right'>
		<fmt:message key="fc.adjustable.value"/>:</td>
		<td style='font-weight: normal; font-size: 15px;' align='left'>
			<c:out value="${param.adjVal}"/></td>
	</tr>
	<tr>
		<td style='font-weight: normal; font-size: 15px;' align='right'><fmt:message key="fc.adjustment.amount"/>:</td>
		<td style='font-weight: normal; font-size: 15px;' align='left'><c:out value="${param.adjAmt}"/>
			<c:if test="${param.adjType=='PERCENT'}">%</c:if>
		</td>
	</tr>
	<tr>
		<td style='font-weight: normal; font-size: 15px;' align='right'><fmt:message key="fc.calculated.value"/>:</td>
		<td style='font-weight: normal; font-size: 15px;' align='left'><c:out value="${param.calVal}"/></td>
	</tr>
	<tr>
		<td style='font-weight: normal; font-size: 15px;' align='right'><fmt:message key="fc.user"/>:</td>
		<td style='font-weight: normal; font-size: 15px;' align='left'><c:out value="${param.user}"/></td>
	</tr>
	<tr>
		<td style='font-weight: normal; font-size: 15px;' align='right'><fmt:message key="fc.last.approvalDt"/>:</td>
		<td style='font-weight: normal; font-size: 15px;' align='left'><c:out value="${param.appDt}"/></td>
	</tr>
</table>
</body>
</html>