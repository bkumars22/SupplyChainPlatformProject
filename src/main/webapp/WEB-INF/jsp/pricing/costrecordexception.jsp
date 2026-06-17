<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-CostRecordException" />
</head> 
<script>

function init() {
	window.parent.$('html').css('width','99%');
	setTimeout(adjustComponent, 10);
}

function adjustComponent(){
	window.parent.$('html').css('width','100%');
}

function editException(key){
	showWaitBusy();
	document.forms['costRecordExceptionForm'].action = "editExceptionRequest";
	document.forms['costRecordExceptionForm'].exceptionId.value = key;
	$("#backAction").val('backToSearchExceptionRequest.do');
	document.forms['costRecordExceptionForm'].submit();
	}
	
Array.prototype.contains = function(v) {
	  for (var i = 0; i < this.length; i++) {
	    if (this[i] === v) return true;
	  }
	  return false;
	};

Array.prototype.unique = function() {
var arr = [];
for (var i = 0; i < this.length; i++) {
  if (!arr.contains(this[i])) {
    arr.push(this[i]);
  }
}
return arr;
}
</script>
<style>
.eto-grid-expand__content
{
   word-break: break-word;
    white-space: normal;
    text-align: justify;
}
</style>
<body onload="init()">

	<form name="costRecordExceptionForm" action="submitSearchExceptionRequest" method="POST">
	<input type="hidden" name="exceptionId" id="exceptionId"/>
	<input type="hidden" name="backAction" id="backAction" />
		<script>
		let lob = [];
		var jsonColumn =  '${costRecordExceptionForm.columns}';
		var allRequestType = JSON.parse('${allRequestType}');
			var selectionType = 'none';
			var gridColumns = [];
			gridColumns.push('<fmt:message key="costrecord.exception.results.ID"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.exceptionName"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.Requestor"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.exceptionOwner"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.exceptionApprover"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.status"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.costType"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.file.cost.count"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.requesttype"/>'+'_EXPANDCELL');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.subtier"/>');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.Commodity"/>'+'_EXPANDCELL');
			gridColumns.push('<fmt:message  key="costrecord.exception.results.platformName"/>'+'_EXPANDCELL');
			gridColumns.push('<fmt:message  key="costrecord.exception.lob"/>'+'_EXPANDCELL');

			var gridRows = [];
			<c:forEach var="row" items="${costRecordExceptionForm.searchResult.values}" varStatus="rowCount">
			<c:set var="cp" value="${row.values[0]}"/>
			var row = {};
			var exceptionIdLink='<a href="javascript:editException(\'${cp.exceptionId}\')"><c:out value='${cp.exceptionId}'/></a>';
			row['<fmt:message key="costrecord.exception.results.ID"/>'] = exceptionIdLink;
			row['<fmt:message  key="costrecord.exception.results.exceptionName"/>'] = "<c:out value='${cp.exceptionName}'/>";
			row['<fmt:message  key="costrecord.exception.results.Requestor"/>'] = "<c:out value='${cp.exceptionRequestor}'/>";
			row['<fmt:message  key="costrecord.exception.results.exceptionOwner"/>'] = "<c:out value='${cp.exceptionOwner}'/>";
			row['<fmt:message  key="costrecord.exception.results.exceptionApprover"/>'] = "<c:out value='${cp.exceptionApprover}'/>";
			var status1='<c:out value="${cp.state}" />';
		    var status2='';
			if(status1=='APPROVED')
	    	{
	    	status2='<div style="color:#40835F;font-weight: 600;">${cp.state}</div>';
	    	}
			else if(status1=='REJECTED')
	    	{
	    	status2='<div style="color:#CE452D;font-weight: 600;">${cp.state}</div>';
	    	}
			else if(status1=='PENDING')
	    	{
	    	status2='<div style="color:#BA830F;font-weight: 600;">${cp.state}</div>';
	    	}
			else
	    	{
	    	status2='<div style="font-weight: 600;">${cp.state}</div>';
	    	}
			row['<fmt:message  key="costrecord.exception.results.status"/>'] = status2;
			row['<fmt:message  key="costrecord.exception.results.costType"/>'] = "<c:out value='${cp.costType.costTypeName}'/>";
			var costCount="<c:out value='${cp.costExceptionPricing.getCostRecordCount()}'/>";
            if(costCount==null || costCount== "")
                    {
                    costCount=0;
                    }
            row['<fmt:message  key="costrecord.exception.file.cost.count"/>'] =costCount;
			row['<fmt:message  key="costrecord.exception.results.requesttype"/>'] = allRequestType["<c:out value='${cp.requestType}'/>"];
			row['<fmt:message  key="costrecord.exception.results.subtier"/>'] = '<fmt:message key="costrecord.exception.info.${cp.subtier ? 'Y' : 'N'}" />';
			row['<fmt:message  key="costrecord.exception.results.Commodity"/>'] = "<c:out value='${cp.commodity}'/>";
			row['<fmt:message  key="costrecord.exception.results.platformName"/>'] = "<c:out value='${cp.platformName}'/>";
			
			lob = [];
			<c:forEach var="ceLOB" items="${cp.costExceptionLOB}">
				lob.push('<c:out value="${ceLOB.lineOfBusiness}"/>');
			</c:forEach>
			row['<fmt:message  key="costrecord.exception.lob"/>'] = lob.unique().join(',');
			gridRows.push(row);
			</c:forEach>
		</script>


		<e2ot:searchContainerControl form="${costRecordExceptionForm}"
			searchFields="${costRecordExceptionForm.allParameters}"
			formName="costRecordExceptionForm"
			resultTableId="costRecordExceptionSearchResultTable"
			showFilterCollapsed="${costRecordExceptionForm.filterAreaCollapsed}"
			showFilter="${costRecordExceptionForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${costRecordExceptionForm}"
			formName="costRecordExceptionForm"
			resultTableId="costRecordExceptionSearchResultTable"
			showOrderMenu="false" showHideMenu="false" title="Exception Request"
			showTitle="true" />
			<script>

$(document).ready(
		function() {
			
			document.body.addEventListener('DOMSubtreeModified', handleClickDOM, true); 
			
			$('.eto-grid-expand__content').each(function(){
				if($(this).html().length != 0){
					$(this).closest( "td").addClass("eto-grid-expand--expanded");
				}
			});
			
			$('.eto-grid-expand__toggle').remove();
		});
		
		
function handleClickDOM(){
	$('.eto-grid-expand__content').each(function(){
		if($(this).html().length != 0){
			$(this).closest( "td").addClass("eto-grid-expand--expanded");
		}
	});
}
function gridUpdateCallBack(){
    $('.eto-grid-expand__content').each(function(){
        if($(this).html().length != 0){
            $(this).closest( "td").addClass("eto-grid-expand--expanded");
        }
    });
    $('.eto-grid-expand__toggle').remove();
    grid.alignRows();
}
</script>
	</form>
</body>
</html>