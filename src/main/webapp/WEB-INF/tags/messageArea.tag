<%@tag display-name="MessageArea"  
	description="displays controller messages in a clearable area"%>
<%@ attribute name="numOfLines" required="false"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="scplatform-messages" />
<c:set var="msgCount" value="0"/>
<%-- This just counts the messages --%>
<html:messages message="true" id="msg"><c:set var="msgCount" value="${msgCount + 1}"/></html:messages>
<c:if test="${msgCount > 0}"> 
<div id="msgArea" style="margin:0px;padding:0px;" class="instructionsArea">
   <c:if test="${msgCount > numOfLines}"><div style="height:${numOfLines*13}px;overflow:auto;width:100%"></c:if>
   <html:messages message="true" id="msg" header="messages.header" footer="messages.footer"><li><c:out value="${msg}"/></li></html:messages>
   <jsp:doBody/>
   <c:if test="${msgCount > numOfLines}"></div></c:if>
   <%-- This emulates the standard button style --%>
   <TABLE style="margin:2px" cellspacing="1" cellpadding="0" class="buttonBorder"><TR>
      <TD id="buttonRegular" nowrap="yes" class="buttonText">
      <a href="#" title="<fmt:message key="messages.header.clear"/>" onclick="$('#msgArea').hide()">&nbsp;&nbsp;<fmt:message key="button.clear"/>&nbsp;&nbsp;</a>
   </TD></TR></TABLE>
</div>
</c:if>
