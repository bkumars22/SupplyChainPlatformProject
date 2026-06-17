<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="AuditHistory" />
</head>
<body>
	<form  name="auditHistoryForm" action="submitAuditHistorySearch" method="POST">
		 <script>
	var jsonColumn =  '${auditHistoryForm.columns}';
	var gridColumns = [];
	var selectionType = "none";
	gridColumns.push('<fmt:message key="audit.actionDate" />');
	gridColumns.push('<fmt:message key="audit.userId" />');
	gridColumns.push('<fmt:message key="audit.roleId" />');
	gridColumns.push('<fmt:message key="audit.actionPerformed" />');
	gridColumns.push('<fmt:message key="audit.comment" />'+'_EXPANDCELL');
	gridColumns.push('<fmt:message key="audit.targetType" />');
	gridColumns.push('<fmt:message key="audit.lastLoadedByUser"/>');
	 var gridRows = [];
	 <c:forEach var="row" items="${auditHistoryForm.searchResult.values}" varStatus="rowCount">
	    <c:set var="a" value="${row.values[0]}"/>
         var row = {};

         <fmt:formatDate var="effFromdate"	pattern="dd-MMM-yyyy HH:mm:ss" value="${a.actionDate}" />
         row['<fmt:message key="audit.actionDate" />'] = '<c:out value = "${effFromdate}"/>';
         row['<fmt:message key="audit.comment" />'] = '<c:out value = "${fn:replace(a.comment,lineSeparator,'<br/>')}"/>';
         row['<fmt:message key="audit.userId" />'] = '<c:out value = "${a.userId}"/>';
         row['<fmt:message key="audit.roleId" />'] = '<c:out value = "${a.roleId}"/>';
         row['<fmt:message key="audit.actionPerformed" />'] = '<c:out value = "${a.actionPerformed}"/>';
         row['<fmt:message key="audit.lastLoadedByUser" />'] = '<c:out value = "${a.lastLoadedByUser}"/>';
         row['<fmt:message key="audit.targetType" />'] = '<c:out value="${a.targetType}"/>';
         gridRows.push(row);
	 </c:forEach>
	</script>
		<e2ot:searchContainerControl
			searchFields="${auditHistoryForm.allParameters}"
			form="${auditHistoryForm}" formName="auditHistoryForm"
			resultTableId="auditSearchResultTable"
			showFilterCollapsed="${auditHistoryForm.filterAreaCollapsed}"
			showFilter="${auditHistoryForm.showFilterArea}" numColumns="3" />
			
		<e2ot:searchResultsControl searchForm="${auditHistoryForm}"
			formName="auditHistoryForm" resultTableId="auditSearchResultTable"
			title="Audit History" showTitle="true" showHideMenu="true"
			showOrderMenu="true" />
	</form>
</body>
<script>

$(document).ready(
		function() {
			
			document.body.addEventListener('DOMSubtreeModified', handleClickDOM, true); 
			
			$('.eto-grid-expand__content').each(function(){
				if($(this).html().length != 0){
					$(this).closest( "td").addClass("eto-grid-expand--expanded");
					$(this).css("display", "contents");
				}
			});
			
			$('.eto-grid-expand__toggle').remove();

		});
		
		
function handleClickDOM(){
	$('.eto-grid-expand__content').each(function(){
		if($(this).html().length != 0){
			$(this).closest( "td").addClass("eto-grid-expand--expanded");
			$(this).css("display", "contents");
		}
	});
}
function gridUpdateCallBack(){
    $('.eto-grid-expand__content').each(function(){
        if($(this).html().length != 0){
            $(this).closest( "td").addClass("eto-grid-expand--expanded");
            $(this).css("display", "contents");
        }
    });
    $('.eto-grid-expand__toggle').remove();
    grid.alignRows();
}

function handleFilterFieldChanged(field){
	if(field.name==='dateValue[actionDateGE]'){
		if(field.value!=='' && field.value!=null){
			$('select[name="value[startYearPartition]"]').prop('disabled',true);
		}
	}
	
	if(field.name==='dateValue[actionDateLT]'){
		if(field.value!=='' && field.value!=null){
			$('select[name="value[startYearPartition]"]').prop('disabled',true);
		}
	}
	
	if(field.name==='value(startYearPartition)'){
        if(field.value!=='' && field.value!=null){
            $('input[name="dateValue[actionDateGE]"]').prop('disabled',true);
            $('input[name="dateValue[actionDateGE]"]').val('');
            $('input[name="dateValue[actionDateLT]"]').prop('disabled',true);
            $('input[name="dateValue[actionDateLT]"]').val('');
            $('.md-icon').css('pointer-events','none');
        }
        else{
            $('input[name="dateValue[actionDateGE]"]').prop('disabled',false);
            $('input[name="dateValue[actionDateLT]"]').prop('disabled',false);
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
	if($('input[name="dateValue[actionDateGE]"]').val()===''&& $('input[name="dateValue[actionDateLT]"]').val()===''){
	    $('select[name="value[startYearPartition]"]').prop('disabled',false);
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
   $('select[name="value[startYearPartition]"]').prop('disabled',false);
   const currentYear = $('select[name="value[startYearPartition]"] option:eq(0)').val();
   $('select[name="value[startYearPartition]"]').val(currentYear);
   $('input[name="dateValue[actionDateGE]"]').prop('disabled',false);
   $('input[name="dateValue[actionDateLT]"]').prop('disabled',false);
   $('.md-icon').css('pointer-events','');
}


</script>
</html>