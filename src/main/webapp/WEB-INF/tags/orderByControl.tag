<%@tag%>
<%@ attribute name="searchForm" required="true"
   type="com.scplatform.pcm.searchframework.dto.SearchForm"%>
<%@ attribute name="col" required="true"%>

<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="scplatform-messages" />
<c:set var="orderByField" value="${searchForm.orderColumnMap[col]}"/>
<c:if test="${!empty orderByField}">
 <c:choose>
  <c:when test="${searchForm.orderByMap[orderByField] == 'ASC'}">
    <fmt:message var="stitle" key="info.ascending"/> 
    <e2i2:img src="/ascending_table_column.gif" alt="${stitle}"      
       onclick="javascript:goSubmitSortSearch('orderBy(${orderByField})');"/>
  </c:when>
  <c:when test="${searchForm.orderByMap[orderByField] == 'DESC'}">
   <fmt:message var="stitle" key="info.descending"/>
    <e2i2:img src="/descending_table_column.gif" alt="${stitle}"
       onclick="javascript:goSubmitSortSearch('orderBy(${orderByField})');"/>
  </c:when>
  <c:otherwise>
   <fmt:message var="stitle" key="info.nosort"/>
   <e2i2:img src="/unsorted.gif" alt="${stitle}"
       onclick="javascript:goSubmitSortSearch('orderBy(${orderByField})');"/>
  </c:otherwise>
 </c:choose>
 <html:hidden property="orderBy(${orderByField})" styleClass="orderByField"/>
 <input type="hidden" name="totalRows" value="${searchForm.totalRows}" />
</c:if>
