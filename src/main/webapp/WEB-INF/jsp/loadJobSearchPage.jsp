<%@ include file="common.jspf"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@ page import="java.util.ArrayList"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="LoadJob-Search" />
</head>
<script>
function callOnChange(field){
	if(field.name=='value(owner)'){
		if(field.value=='REGIONAL'){
			document.getElementsByName("values(regions)")[0].disabled = false;
		}else{
			hideElement();
		}
	}
}
var selectedPageKey = "";
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

	function validateSelectedJobLength() {
		var checkboxChecked = false;
	 
		var checkb =$("input[type='checkbox']");
		for (var i = 1; i < checkb.length; i++) {
			if (checkb[i].checked) {
				checkboxChecked = true;
				break;
			}
		}
		if (!checkboxChecked) {
			showOkMessageBox(
					'OK',
					'WARN',
					"<fmt:message key='warn.manage_job.no_job_selected_to_delete'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
			return false;
		}
		return true;
	}

	function goDeleteloadJobs() {
		var loadJobKey  = new Array();
		 $.each($("input[name='selectedPageKeys']:checked"), function(){            
			 loadJobKey.push($(this).val());
            });
		 if(loadJobKey.length ==0){
			 showOkMessageBox(
						'OK',
						'WARN',
						"<fmt:message key='warn.manage_job.no_job_selected_to_delete'/>",
						"<fmt:message key='msg.warn'/>", function() {
						});
		 }else{
			// $("#loadjobKey").val(loadJobKey);
			 showOkCancelMessageBox('YES NO','WARN',
			            "<fmt:message key='warn.delete_objects'/>",
			            "<fmt:message key='msg.warn'/>", goDeleteJobs);
		 }
	}

	function goDeleteJobs() {
			document.forms[0].action = "deleteSelectedLoadJobs.do";
			document.forms[0].submit();
		 }
	function goReadJob() {
		document.forms[0].action = "readLoadJob.do";
		document.forms[0].submit();
	}
	
	function init() {
		resizeResultArea();
		setupHideColumnState('loadJobSearchResultTable', true);
		setupOrderColumnState('loadJobSearchResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}
</script>
<body onload="init()" style="overflow-y : scroll;">
	<form name="loadJobAdminForm" action="submitLoadJobSearch" method="POST">
		<input type="hidden" name="selectedLoadJobKey" id="selectedLoadJobKey">
		<script>
			var jsonColumn =  '${loadJobAdminForm.columns}';
			var gridColumns= [];
			var gridRowExposedAction = true; 
			gridColumns.push('<fmt:message key="loadjob.loadDate"/>');
			gridColumns.push('<fmt:message  key="loadjob.status"/>');
			gridColumns.push('<fmt:message  key="loadjob.state"/>');
			gridColumns.push('<fmt:message  key="loadjob.loadedBy"/>');
			gridColumns.push('<fmt:message  key="loadjob.loadType"/>');
			gridColumns.push('<fmt:message  key="loadjob.id"/>');
			gridColumns.push('<fmt:message  key="loadjob.datasource"/>');
			var gridRows = [];
			<c:forEach var="row" items="${loadJobAdminForm.searchResult.values}" varStatus="rowCount">
			var row = {};
			<c:set var="loadJob" value="${row.values[0]}"/>
			
				row['checkboxValue']='<c:out value="${loadJob.loadJobKey}"/>';
			row['LoadJobKey']= '<c:out value="${loadJob.loadJobKey}"/>';
			row['<fmt:message key="loadjob.loadDate"/>'] = '<c:out value="${loadJob.loadDate}"/>';
			row['<fmt:message key="loadjob.status"/>'] = '<c:out value="${loadJob.status}"/>';
			row['<fmt:message key="loadjob.state"/>'] = '<c:out value="${loadJob.state}"/>';
			row['<fmt:message key="loadjob.loadedBy"/>'] = '<c:out value="${loadJob.loadedBy}"/>';
			<fmt:message var="loadJobTypeLabel" key="upload.${loadJob.loadJobType}"/>
            <c:choose>
            	<c:when test="${not empty loadJobTypeLabel}">
                    row['<fmt:message key="loadjob.loadType"/>'] = '<c:out value="${loadJobTypeLabel}"/>';
                </c:when>
                <c:otherwise>
                    row['<fmt:message key="loadjob.loadType"/>'] = '<c:out value="${loadJob.loadJobType}"/>';
                </c:otherwise>
            </c:choose>
			row['<fmt:message key="loadjob.id"/>'] ='<c:out value="${loadJob.externalId}"/>';
			row['<fmt:message key="loadjob.datasource"/>'] = '<c:out value="${loadJob.datasource}"/>';
			gridRows.push(row);
			</c:forEach>
		</script>
		
		<e2ot:searchContainerControl
			searchFields="${loadJobAdminForm.allParameters}"
			formName="loadJobAdminForm" form="${loadJobAdminForm}"
			resultTableId="loadJobSearchResultTable"
			showFilterCollapsed="${loadJobAdminForm.filterAreaCollapsed}"
			showFilter="${loadJobAdminForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${loadJobAdminForm}"
			formName="loadJobAdminForm" resultTableId="loadJobSearchResultTable"
			showHideMenu="true" showOrderMenu="true" title="Manage Upload Jobs">
		</e2ot:searchResultsControl>
		<script type="text/javascript">
		
		</script>
		<c:if test="${not empty loadJobAdminForm.searchResult.values}" >
		<script>
		var footerButton = document.getElementById("searchFiledsButtons");
		var buttons = "";
		<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DeleteJob')}">
				  buttons += '<button type="button" class="eto-btn eto-btn--primary" id="deleteButton" onclick="javascript:goDeleteloadJobs()">'
			  			+'<bean:message key="button.delete" /></button>'
			  </c:if>
			  <c:if test="${!empty forecastItemSearchForm.nextAction}">
			  buttons += '<button type="button" class="eto-btn" id="nextButton" onclick="javascript:goNext(document.forms[\'forecastItemSearchForm\'],\'${fn:escapeXml(forecastItemSearchForm.nextAction)}\');">'
						+'<bean:message key="button.next" /></button>'
			  </c:if> 
			footerButton.innerHTML = buttons; 
		</script>
		</c:if>
		<script type="text/javascript">
		if(grid!=null){
			setGridEvents(grid);
		}
		function setGridEvents(grid){
		if(grid!=null){
				grid.on('rowAction', function() {
					var jobLoadkey = arguments[1];
					$("#selectedLoadJobKey").val(jobLoadkey);
					  goReadJob();
				});
		}
		}
		</script>  
		 
	 
	</form>
</body>
</html>