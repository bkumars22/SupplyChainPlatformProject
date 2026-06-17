<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="TAMAuditHistory" />
</head>
<script>
	function goBack(formName, action) {
		formName.action = action;
		formName.buttonAction.value = "back";
		formName.submit();
	}

	function goNext(formName, action) {
		formName.action = action;
		formName.buttonAction.value = "next";
		formName.submit();
	}

	function init() {
		resizeResultArea();
		setupHideColumnState('tamAuditSearchResultTable', true);
		setupOrderColumnState('tamAuditSearchResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}
</script>
<body onload="init()" style="margin: 0px;">
	<form name="tamHistoryForm" action="submitSupplyAllocationHistory" method="POST">

		<script>
		    var jsonColumn =  '${tamHistoryForm.columns}';
			var gridColumns = [];
			var selectionType = "none";
			gridColumns.push('<fmt:message key="functional.group.audit.datePerformed" />');
			gridColumns.push('<fmt:message key="functional.group.audit.userId" />');
			gridColumns.push('<fmt:message key="functional.group.audit.userRole" />');
			gridColumns.push('<fmt:message key="functional.group.audit.actionPerformed" />');
			gridColumns.push('<fmt:message key="functional.group.audit.operationCode" />');
			gridColumns.push('<fmt:message key="tam.audit.groupName" />');
			gridColumns.push('<fmt:message key="functional.group.audit.fgType"/>');
			gridColumns.push('<fmt:message key="tam.audit.site" />');
			gridColumns.push('<fmt:message key="tam.audit.itemNumber" />');
			gridColumns.push('<fmt:message key="tam.audit.supplier" />');
			gridColumns.push('<fmt:message key="tam.audit.bucketStartDate" />');
			gridColumns.push('<fmt:message key="tam.audit.bucketEndDate" />');
			gridColumns.push('<fmt:message key="functional.group.audit.comment" />'+'_EXPANDCELL');
			
	   var gridRows = [];
		<c:forEach var="row" items="${tamHistoryForm.searchResult.values}"
			varStatus="rowCount">
		   var row = {};
			<c:set var="ah" value="${row.values[0]}" />
				<!-- Common column start -->
				<fmt:formatDate var="datePerformed"	pattern="dd-MMM-yyyy HH:mm:ss" value="${ah.datePerformed}" />
				row['<fmt:message key="functional.group.audit.datePerformed" />'] = '<c:out value = "${datePerformed}"/>';
				row['<fmt:message key="functional.group.audit.userId" />'] = '<c:out value = "${ah.userId}"/>';
				row['<fmt:message key="functional.group.audit.userRole" />'] = '<c:out value = "${ah.userRole}"/>';
				row['<fmt:message key="functional.group.audit.actionPerformed" />'] = '<c:out value = "${ah.actionPerformed}"/>';
				row['<fmt:message key="functional.group.audit.operationCode" />'] = '<c:out value = "${ah.operationCode}"/>';
				<!-- Common column end -->
				<c:if test="${ah.source == 'TAM' || ah.source == 'XLOB'}">
					row['<fmt:message key="tam.audit.groupName" />'] = '<c:out value = "${ah.functionalGroup.name}"/>';
					row['<fmt:message key="functional.group.audit.fgType" />'] = '<c:out value = "${ah.functionalGroup.type}"/>';
					row['<fmt:message key="tam.audit.site" />'] = '<c:out value = "${ah.site.siteDescription}"/>';
					row['<fmt:message key="tam.audit.itemNumber" />'] = '<c:out value = "${ah.item.itemNumber}"/>';
					row['<fmt:message key="tam.audit.supplier" />'] = '<c:out value = "${ah.supplier.businessEntityName}"/>';
					<fmt:formatDate var="startDate" pattern="dd-MMM-yyyy" value="${ah.bucketStartDate}" />
					row['<fmt:message key="tam.audit.bucketStartDate" />'] = '<c:out value = "${startDate}"/>';
					<fmt:formatDate var="endDate" pattern="dd-MMM-yyyy" value="${ah.bucketEndDate}" />
					row['<fmt:message key="tam.audit.bucketEndDate" />'] = '<c:out value = "${endDate}"/>';
				</c:if>
				<c:if test="${ah.source == 'com.test.repository.pcm.domain.AllocationAuditHistory'}">
					row['<fmt:message key="tam.audit.groupName" />'] = '<c:out value = "${ah.functionalGroup.name}"/>';
					row['<fmt:message key="functional.group.audit.fgType" />'] = '<c:out value = "${ah.functionalGroup.type}"/>';
		  		</c:if>
		  		<c:if test="${ah.source == 'PFG'}">
		  			<c:if test="${ah.operationCode!='PFG DELETED'}">
			  	 		row['<fmt:message key="tam.audit.groupName" />'] = '<c:out value="${ah.parentFunctionalGroup.name}" />';
				  		row['<fmt:message key="functional.group.audit.fgType" />'] = 'Parent -<c:out value="${ah.parentFunctionalGroup.type}" />';
			  		</c:if>
			  		<c:if test="${ah.operationCode=='PFG DELETED'}">
			  	 		row['<fmt:message key="tam.audit.groupName" />'] = '${fn:substringBefore(ah.comment,"Parent Functional Group deleted")}';
				 		row['<fmt:message key="functional.group.audit.fgType" />'] = '${fn:substringAfter(ah.comment,"Parent Functional Group deleted of type")}';
			  		</c:if>
			  	</c:if>
			  	<c:if test="${!(ah.source == 'TAM'  || ah.source == 'XLOB')}">
					row['<fmt:message key="tam.audit.site" />'] = '';
					row['<fmt:message key="tam.audit.itemNumber" />'] = '';
					row['<fmt:message key="tam.audit.supplier" />'] = '';
					row['<fmt:message key="tam.audit.bucketStartDate" />'] = '';
					row['<fmt:message key="tam.audit.bucketEndDate" />'] = '';
				</c:if>
				row['<fmt:message key="functional.group.audit.comment" />'] = '<c:out value = "${ah.comment}"/>';
				gridRows.push(row);
			   </c:forEach>
				</script>

		<input type="hidden" name="requestType" value="${requestType}"/>
		<input type="hidden" name="previousAction" value="${previousAction}"/>
		<input type="hidden" name="nextAction" value="${nextAction}"/>
		<input type="hidden" name="buttonAction" value="${buttonAction}"/>
		<input type="hidden" name="showSaveFilter" value="${empty showSaveFilter ? 'false' : showSaveFilter}"/>
		<e2ot:searchContainerControl
			searchFields="${tamHistoryForm.allParameters}"
			formName="tamHistoryForm" form="${tamHistoryForm}"
			resultTableId="tamAuditSearchResultTable"
			showSaveFilter="${tamHistoryForm.showSaveFilter}"
			showFilterCollapsed="${tamHistoryForm.filterAreaCollapsed}"
			showFilter="${tamHistoryForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${tamHistoryForm}"
			formName="tamHistoryForm" resultTableId="tamAuditSearchResultTable"
			showHideMenu="true" showOrderMenu="true" />
	</form>
</body>
<script type="text/javascript">
var opCodeOption = [];

parent.mcmApp.searchFields['auditType'].on('change:value',
		function(query) {
	setOperationCode(parent.mcmApp.searchFields['auditType'].value);
});




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

$(document).ready(
		function() {
			document.body.addEventListener('DOMSubtreeModified', handleClickDOM, true);
			$('.eto-grid-expand__content').each(function(){
				if($(this).html().length != 0){
					$(this).closest( "td").addClass("eto-grid-expand--expanded");
				}
			});
			
			$('.eto-grid-expand__toggle').remove();
			$("select[name='value(operationCode)'] option").each(function(index,item)
			{
				opCodeOption.push(item);
			});
			var selectedValue = parent.mcmApp.searchFields['operationCode'].value;
			setOperationCode(parent.mcmApp.searchFields['auditType'].value);
			$('select[name="value(operationCode)"] option[value="'+selectedValue+'"]').attr('selected', true);
		});

	
function setOperationCode(value){
	if(value.length == 0){
		for(var i = 0 ; i < opCodeOption.length ; i++){
			$("select[name='value(operationCode)']").append(opCodeOption[i]);
		}
		$('select[name="value(operationCode)"] option:selected').removeAttr('selected');
	}else{
		$('select[name="value(operationCode)"] option:selected').removeAttr('selected');
		$('select[name="value(operationCode)"] option').remove();
		value.unique().forEach(function(curr){
			$("select[name='value(operationCode)']").append(opCodeOption[0]);
			 if(curr == 'TAM'){
				 for(var i = 1 ; i < opCodeOption.length ; i++){
					if(opCodeOption[i].value.startsWith('TAM') || opCodeOption[i].value=='Site Change TAM Cascade Extract'){
					 	$("select[name='value(operationCode)']").append(opCodeOption[i]);
					} 
				 }
	         }
			 if(curr == 'PFG'){
				 for(var i = 1 ; i < opCodeOption.length ; i++){
					if(opCodeOption[i].value.includes('PFG')){
					 	$("select[name='value(operationCode)']").append(opCodeOption[i]);
					}
				 }
	         }
			 if(curr == 'com.test.repository.pcm.domain.AllocationAuditHistory'){
				 for(var i = 1 ; i < opCodeOption.length ; i++){
					if(opCodeOption[i].value.includes('ITEM') || opCodeOption[i].value == 'FG CREATED' || opCodeOption[i].value == 'REMOVE FG' || opCodeOption[i].value == 'FG RENAMED' || opCodeOption[i].value == 'FG UPDATED' || opCodeOption[i].value == 'EXTRACT'){
						 	$("select[name='value(operationCode)']").append(opCodeOption[i]);
					}
				 }
	         }
			 if(curr == 'XLOB'){
				 for(var i = 1 ; i < opCodeOption.length ; i++){
					if(opCodeOption[i].value.startsWith('XLOB') || opCodeOption[i].value == 'TAM COPIED'){
							 	$("select[name='value(operationCode)']").append(opCodeOption[i]);
					}
				}
			 }
			 $('select[name="value(operationCode)"] option[value=""]').attr('selected', true);
		});
	}
}

function handleClickDOM(){
	$('.eto-grid-expand__content').each(function(){
		if($(this).html().length != 0){
			$(this).closest( "td").addClass("eto-grid-expand--expanded");
		}
	});
}

function handleFilterFieldChanged(field){
	if(field.name==='dateValue(actionDateGE)'){
		if(field.value!=='' && field.value!=null){
			$('select[name="value(startYearPartition)"]').prop('disabled',true);
		}
	}
	
	if(field.name==='dateValue(actionDateLE)'){
		if(field.value!=='' && field.value!=null){
			$('select[name="value(startYearPartition)"]').prop('disabled',true);
		}
	}
	
	if(field.name==='value(startYearPartition)'){
        if(field.value!=='' && field.value!=null){
            $('input[name="dateValue(actionDateGE)"]').prop('disabled',true);
            $('input[name="dateValue(actionDateGE)"]').val('');
            $('input[name="dateValue(actionDateLE)"]').prop('disabled',true);
            $('input[name="dateValue(actionDateLE)"]').val('');
            $('.md-icon').css('pointer-events','none');
        }
        else{
            $('input[name="dateValue(actionDateGE)"]').prop('disabled',false);
            $('input[name="dateValue(actionDateLE)"]').prop('disabled',false);
            $('.md-icon').css('pointer-events','');
        }
    }
}
 
function clearField(field)
{
   if (field.tagName == 'SELECT')
   {
      if (field.multiple == true)
      {
         for (i = 0; i < field.options.length; i++)
         {
            field.options[i].selected = false;
         }
         field.value='';
         field.selectedIndex = -1;         
      }
      else
      {
        var options = field.options;
        if(options.length == 1) {
        	field.selectedIndex = 0;
        	field.value = options[0].value;
        } else {
        	field.selectedIndex=0;   
            //field.value='';
        }
      }
   }
   else
   {             
      field.value='';
      checkIfFilterIsEmpty(field);
   }
}
 
function checkIfFilterIsEmpty(field){
	if($('input[name="dateValue(actionDateGE)"]').val()===''&& $('input[name="dateValue(actionDateLE)"]').val()===''){
	    $('select[name="value(startYearPartition)"]').prop('disabled',false);
	}
}

function clearSearchCriteria()
{  
   var fields = $('#expand-container :input');
   for (idx=0; idx < fields.length; idx++)
   {
	   if (fields[idx].id != null && 
    	  fields[idx].id.indexOf('searchField') == 0)
      {
         clearField(fields[idx]);
      }
      if (fields[idx].id != null && fields[idx].type == 'select-multiple' && fields[idx].name != null &&
        	  fields[idx].name.indexOf('values') == 0)
          {
    	  var name = fields[idx].name;
    	  name = name.substring(7,name.length-1);
    	  var _options_legth = parent.parent.mcmApp.searchFields[name].options.length;
			for(var i = 0 ; i < _options_legth ; i++ ){
				$('#expand-container span.eto-tag__remove').each(function() {
					$( this ).trigger( "click" );
				});
			}
        }
   }
   if (document.forms[0].selectedFilter != undefined)
   {
   	   document.forms[0].selectedFilter.selectedIndex = 0;
       document.forms[0].selectedFilterName.value = '';
   }   
   
   $('#expand-container span.eto-tag__remove').each(function() {
	    $( this ).trigger( "click" );
   });
   $('select[name="value(startYearPartition)"]').prop('disabled',false);
   const currentYear = $('select[name="value(startYearPartition)"] option:eq(0)').val();
   $('select[name="value(startYearPartition)"]').val(currentYear);
   $('input[name="dateValue(actionDateGE)"]').prop('disabled',false);
   $('input[name="dateValue(actionDateLE)"]').prop('disabled',false);
   $('.md-icon').css('pointer-events','');
}
</script>
</html>