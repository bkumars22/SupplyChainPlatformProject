<%@tag%>
<%@ attribute name="formName" required="true"%>
<%@ attribute name="id" required="false"%>
<%@ attribute name="searchForm" required="true"
	type="com.scplatform.pcm.searchframework.dto.SearchForm"%>
<%@ attribute name="pagingCallback" required="false"%>
<%@ attribute name="collapsable" required="false" type="Boolean"%>
<%@ attribute name="resultTableId" required="false"%>
<%@ attribute name="showOrderMenu" required="false" type="Boolean"%>
<%@ attribute name="showHideMenu" required="false" type="Boolean"%>
<%@ attribute name="showClearSort" required="false" type="Boolean"%>
<%@ attribute name="showTitle" required="false" type="Boolean"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ attribute name="title" required="false"%>
<%@ attribute name="showGrid" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="e2ot"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<fmt:setBundle basename="scplatform-messages" />
<c:if test="${empty resultTableId}">
	<c:set var="resultTableId" value="searchResultTable" />
</c:if>
<c:if test="${showTitle == false}">
	<fmt:message var="title" key="search.result.title" />
</c:if>
<style>
 .eto-grid{
   margin-bottom:1rem;
}
.eto-grid-scroll{
  overflow-x:hidden !important;
}
</style>
<input type="hidden" name="columns" id="columns"  />
 <input type="hidden" name="condensedView" id="condensedView" value="true"/>
 <c:if test="${empty searchForm.searchResult}">
	
	<c:if test="${searchForm.initFlag}">
		<div class="margin-left-sm-2">
			<label><i><fmt:message key="searchFilter.filter.message" />
			</i></label>
		</div>
	</c:if>
	<c:if test="${!searchForm.initFlag}">
		<div style="display: flex; margin-bottom: 100px;">
			<div class="eto-messageblock" data-message-type="info"
				id="message-block-no-results"
				style="margin: auto; width: 100%; margin-top: 5%;">
				<div class="eto-messageblock__body">No records found to
					display, Refine your search in the filters</div>
				<a href="javascript:void(0)" role="button"
					class="eto-messageblock__close"></a>
			</div>
			<script>
				var msgBlock = new eto.MessageBlock({
					el : document.querySelector('#message-block-no-results')
				});
			</script>
		</div>
	</c:if>
</c:if>
<c:if test="${!empty searchForm.searchResult}">
	<div id="grid-result" style="padding-bottom: 7%; overflow-y:auto;"
		class="finder-result margin-sm-2"></div>
	<jsp:doBody />
</c:if>
<c:if
	test="${searchForm.pagingEnabled and !empty searchForm.searchResult}">
	<div class="footer">
	<div class="row" id="scroller1" style="overflow-x: scroll; margin: 1rem; position: relative;">
	<div class="col-xs-12"><div id="staticdiv1" style="height: 5px; width:100%;"></div></div></div>
	<div class="row margin-sm-1">
			<div class="col-xs-9 col-sm-9">
				<div id="paginationDiv" style="display: flex;">
					<div
						style=" display: flex;"
						id="paginationDivInner">
						<e2ot:pagingControl searchForm="${searchForm}"
							refreshOnPageSize="true" formName="${formName}"
							pagingCallback="${pagingCallback}" />
					</div>
				</div>
			</div>
			<div class="col-xs-3 col-sm-3">
		<div class="eto-btn-group" style="float:right;">
		<button class="eto-btn" type="button" onclick="javascript:closePopup();"data-modal-close>Close</button>
				<button id="okButton" type="button" style="float:right;" class="eto-btn eto-btn--primary"
					onclick="javascript:goOk(document.searchFinderForm)">
					<fmt:message key="button.ok" />
				</button>
				</div>
			</div>
		</div>
	</div>
</c:if>
</div>
</div>
<%-- These fields are used to post the order and show/hide state of the result set--%>
<c:if test="${showHideMenu}">
	<c:set var="fieldId">
		<c:out value="hcmState_${resultTableId}" />
	</c:set>
	<input type="hidden" id="${fieldId}" name="${fieldId}"
		value="${fn:escapeXml(param[fieldId])}" />
</c:if>
<c:if test="${showOrderMenu}">
	<c:set var="fieldId">
		<c:out value="ocmState_${resultTableId}" />
	</c:set>
	<input type="hidden" id="${fieldId}" name="${fieldId}"
		value="${fn:escapeXml(param[fieldId])}" />
</c:if>
<c:set var="grid" value="${empty showGrid?true:false}"/>
<c:if test="${grid}">
<script type="text/javascript" src="./js/finderGridModule.js"></script>
</c:if>

<script type="text/javascript">

	checkboxObj =new eto.CheckboxMenu({ el: document.querySelector('.eto-checkbox-menu') });

	function closePopup(){
		 
		  parent.parent.finderModal.close(true);
	}
	$(window).on('load', function(){
		<c:if test="${!empty searchForm.searchResult}">
	   	handleGridScrolls();
	   	</c:if>
	});
	
	function handleGridScrolls(){
    	var gridWidth=0;
		var frozenWidth=$("#grid-result .eto-grid .eto-grid-frozen table").width();
		var scrollWidth=$("#grid-result .eto-grid .eto-grid-scroll table").width();
		if(frozenWidth == null)
				frozenWidth= 0 ;
		gridWidth+=parseInt(frozenWidth)+parseInt(scrollWidth);
		var contentWidth=$('body').width();
		if(gridWidth>contentWidth) {
			gridWidth+=60;
			$('#scroller1').show();
			$('#staticdiv1').css('width',gridWidth+'px');
		   	$('#scroller1').scroll(function(){
				$('.eto-grid-scroll').scrollLeft($(this).scrollLeft());
			});
		} else {
			$('#scroller1').hide();
		}
		window.onscroll = function(ev) {			
			var offset=$('#grid-result .eto-grid')[0].getBoundingClientRect().top;
			if(offset<0){
				 $("#grid-result .eto-grid .eto-grid-frozen table thead tr th").css({'transform':'translate3d(0px,'+(-offset)+'px,0px)','position':'relative','z-index':2});
				 $("#grid-result .eto-grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,'+(-offset)+'px,0px)','position':'relative','z-index':2,}); 
			} else {
				$("#grid-result .eto-grid .eto-grid-frozen table thead tr th").css({'transform':'translate3d(0px,0px,0px)','position':'static','z-index':'auto'});
				$("#grid-result .eto-grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,0px,0px)','position':'static','z-index':'auto'});
			}
		   
        };		
    }
	
</script>