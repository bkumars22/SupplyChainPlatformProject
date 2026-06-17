<%@tag%>
<%@ attribute name="formName" required="true"%>
<%@ attribute name="pagingCallback" required="false"%>
<%@ attribute name="refreshOnPageSize" required="false"%>
<%@ attribute name="searchForm" required="true"
	type="com.scplatform.pcm.searchframework.dto.SearchForm"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="scplatform-messages" />
<input type="hidden" name="pageStartAt" value="${searchForm.pageStartAt}"/>
<input type="hidden" name="totalRows" value="${searchForm.totalRows}"/>

<c:set var="previousimage" value="/previous_inactive.gif" />
<c:set var="previousurl" value="#" />
<c:if test="${searchForm.hasPreviousPage}">
	<c:set var="previousurl"
		value="javascript:submitPrevPage(document.forms['${formName}'])"
		scope="page" />
	<c:set var="previousimage" value="/previous_active.gif" />
</c:if>
<c:set var="nextimage" value="/next_inactive.gif" />
<c:set var="nexturl" value="#" />
<c:if test="${searchForm.hasNextPage}">
	<c:set var="nexturl"
		value="javascript:submitNextPage(document.forms['${formName}'])"
		scope="page" />
	<c:set var="nextimage" value="/next_active.gif" />
</c:if>
<div style="margin-top: auto;margin-bottom: auto; display: flex; margin-right: 1rem;">
	<div style="margin: auto;">
		<a class="eto-pagination__back eto-btn eto-btn--icon-only"
			style="cursor: pointer; color: #468293; margin: 5px"
			onclick="${previousurl}"></a>
	</div>
	<div style="margin-top: auto;margin-bottom: auto; margin: 1rem;">
		<label><c:choose>
				<c:when test="${searchForm.pagingEnabled}">
					<c:if test="${searchForm.showPageMessage}">
						<fmt:message key="info.paging.harmony.page">
							<fmt:param value="${searchForm.atPage}" />
							<fmt:param value="${searchForm.maxPage}" />
						</fmt:message>
					</c:if>
				</c:when>
				<c:otherwise>
					<fmt:message key="info.result_count">
						<fmt:param value="${searchForm.totalRows}" />
					</fmt:message>
				</c:otherwise>
			</c:choose></label>
	</div>
	<div style="margin-top: auto;margin-bottom: auto;">
		<a class="eto-pagination__forward eto-btn eto-btn--icon-only"
			style="cursor: pointer; color: #468293; margin: 5px"
			onclick="${nexturl}"></a>
	</div>
</div>

<c:if test="${searchForm.showPageMessage}">
	<div class="eto-go-to-page"
		style="margin-top: auto;margin-bottom: auto; display: flex; margin-right: 1rem;align-self: center;">
		<div style="margin: auto; margin-right: 0.5rem;">
			<label class="eto-input"><input class="eto-input__field"
				type="text" size="6" maxlength="6" name="pagenum"
				placeholder="Go to page"></label>
		</div>
		<div style="margin: auto;">
			<button class="eto-btn"  id="jumpToPageBtn">
				<fmt:message key="button.jump" />
			</button>
		</div>
		<script>
		    document.getElementById('jumpToPageBtn').onclick = function(e) {		    	
		    	e.preventDefault();		    	
		    	submitJumpToPage(document.forms['${formName}']);
		    }
		</script>
	</div>
</c:if>

<c:if test="${!searchForm.showFilterArea || refreshOnPageSize}">
	<c:set var="changeAction"
		value="goSubmitPageSizeChanged(document.forms['${formName}']);" />
</c:if>

<div style="margin-top: auto;margin-bottom: auto;margin-right: 1rem;align-self: center;" id="items_per_page">
	<div class="eto-items-per-page">
		<label class="eto-select"><span class="eto-select__label"><fmt:message
					key="label.page_size" /></span>
			<div class="eto-select__field-container">
				<select class="eto-select__field" name="pageSize" id="pageSize_id"
					onchange="${changeAction}" style="margin: auto;">
					<option value="1" ${searchForm.pageSize == 1 ? 'selected' : ''}>1</option>
					<option value="5" ${searchForm.pageSize == 5 ? 'selected' : ''}>5</option>
					<option value="10" ${searchForm.pageSize == 10 ? 'selected' : ''}>10</option>
					<option value="20" ${searchForm.pageSize == 20 ? 'selected' : ''}>20</option>
					<option value="50" ${searchForm.pageSize == 50 ? 'selected' : ''}>50</option>
					<option value="70" ${searchForm.pageSize == 70 ? 'selected' : ''}>70</option>
					<option value="100" ${searchForm.pageSize == 100 ? 'selected' : ''}>100</option>
				</select>
			</div></label>
	</div>
</div>

<c:if test="${empty pageControlScript}">
	<c:set var="pageControlScript" value="true" scope="request" />
	<script>
function goSubmitPageSizeChanged(formObj)
{
   formObj.pageStartAt.value = 0;
   formObj.submit();
   showWaitBusy();
}

function submitNextPage(formObj)
{
<c:if test="${!empty pagingCallback}">
   var fr = partial(submitNextPageCallback,formObj);
   if (${pagingCallback}('next',fr) == false)
   {
      return;
   }
</c:if>
<c:if test="${empty pagingCallback}">
submitNextPageCallback(formObj);
</c:if>
}

function submitNextPageCallback(formObj)
{
<c:if test="${!empty searchForm.searchAction}">
   formObj.action="${searchForm.searchAction}";
</c:if>
   formObj.pageStartAt.value = (Number(formObj.pageStartAt.value) + Number(formObj.elements.namedItem("pageSize").value));
   formObj.submit();
   showWaitBusy();
}

function submitPrevPage(formObj)
{
   <c:if test="${!empty pagingCallback}">
   var fr = partial(submitPrevPageCallback,formObj);
   if (${pagingCallback}('prev') == false)
   {
      return;
   }
   </c:if>
   submitPrevPageCallback(formObj);
}

function submitPrevPageCallback(formObj)
{
   <c:if test="${!empty searchForm.searchAction}">
   formObj.action="${searchForm.searchAction}";
   </c:if>
   formObj.pageStartAt.value = (Number(formObj.pageStartAt.value) - Number(formObj.elements.namedItem("pageSize").value));
   formObj.submit();
   showWaitBusy();
}

function submitJumpToPage(formObj)
{
   <c:if test="${!empty searchForm.searchAction}">
      formObj.action="${searchForm.searchAction}";
   </c:if>
   var pageNumber = Math.ceil(Number(formObj.pagenum.value));
   if (pageNumber > ${searchForm.maxPage} || pageNumber < 1 || isNaN(pageNumber))
   {
      formObj.pagenum.value = '';
      return;
   }   
   <c:if test="${!empty pagingCallback}">
   var fr = partial(submitJumpToPageCallback,formObj,pageNumber);
   if (${pagingCallback}('jump') == false)
   {
      return;
   }
   </c:if>
   submitJumpToPageCallback(formObj,pageNumber);
}

function submitJumpToPageCallback(formObj,pageNumber)
{
   formObj.pageStartAt.value = ((pageNumber - 1) * Number(formObj.elements.namedItem("pageSize").value));
   formObj.submit();
   showWaitBusy();
}
</script>
</c:if>