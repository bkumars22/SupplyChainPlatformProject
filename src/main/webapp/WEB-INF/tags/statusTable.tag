<%@tag description="Generates a table of statues"%>
<%@ attribute name="id" required="true" %>
<%@ attribute name="user" required="true" type="java.lang.Boolean"%>
<%@ attribute name="daysOld" required="false" type="java.lang.Integer"%>
<%@ attribute name="items" required="true" type="java.util.List"%>
<%@ attribute name="navTarget" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<fmt:setBundle basename="scplatform-messages" />
  <e2i2:table width="100%" id="${id}" >
     <e2i2:tr header="yes">
        <td><fmt:message key="label.status"/></td>
        <td style="width:120px"><fmt:message key="label.count"/></td>
     </e2i2:tr>
	 <c:forEach var="result" items="${items}">
     <c:if test="${!empty navTarget}">
       <c:url var="linkHref" value="${navTarget}">
           <c:param name="status" value="${result[1]}"/>
		   <c:if test="${user}">
               <c:param name="user" value="${appContext.currentUser.userId}"/>
		   </c:if>
   		   <c:if test="${!empty daysOld && daysOld > 0}">
               <c:param name="daysOld" value="${daysOld}"/>
		   </c:if>                              
       </c:url>
     </c:if>
     <e2i2:tr>
        <td>
           <c:if test="${!empty navTarget}"><a href="javascript:goNavigate('${linkHref}')"></c:if>
           ${result[1]}
		   <c:if test="${!empty navTarget}"></a></c:if>
        </td>
        <td>${result[0]}</td>
     </e2i2:tr>
	 </c:forEach>
  </e2i2:table>
