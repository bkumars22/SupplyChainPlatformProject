<%@ include file="../common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Rebate-Search" />
</head>
<script type="text/javascript">
var massUpdateRebateOwner = {
		programOwner:{ 
			modalHeader  : "Assign Program Owner",
			dynamicButtonLabel : "Yes, assign",
			data : {
				activeOnly : 'yes',
				businessType : 1,
		     }, 
		    url : "ajaxQueryUserId?q=",
		    dynamicMethod:goAssignProgramOwner
		 },
		financeOwner:{
			modalHeader  : "Assign Finance Owner",
			dynamicButtonLabel : "Yes, assign",
			data:{
				roleId:"FINANCE"
			},
		    url:"ajaxQueryUserId?q=",
		    dynamicMethod:goAssignFinanceOwner
		}
}

function goAssignProgramOwner()
{
	var callback = function() {
		document.forms[0].action="assignProgramOwnerRebateManagement"
		document.forms[0].submit();
		showWaitBusy();
	}
	callback();
}

function goAssignFinanceOwner()
{
	var callback = function() {
		document.forms[0].action="assignFinanceOwnerRebateManagement"
		document.forms[0].submit();
		showWaitBusy();
	}
	callback();
}


function goBack(formName,action)
{
    formName.action=action;
    formName.buttonAction.value="back";
    formName.submit();
    showBusy();
}

function goNext(formName,action)
{
    formName.action=action;
    formName.buttonAction.value="next";
    formName.submit();
    showBusy();
}

function goViewRebate(key)
{
	document.forms['rebateSearchForm'].action="viewRebateProgram";
	document.forms['rebateSearchForm'].rebateProgramKey.value = key;
    document.forms['rebateSearchForm'].backAction.value = "submitRebateProgramSearch";
    document.forms['rebateSearchForm'].submit();
	showWaitBusy();
}

function init()
{
	resizeResultArea();
	setupHideColumnState('rebateSearchResultTable',true);
	setupOrderColumnState('rebateSearchResultTable',true,
			{mouseOverTitle:'<fmt:message key="label.moveColumn"/>'});
	
}
function goCancel()
{
	document.forms[0].action="home";
	document.forms[0].submit();
}

function goNewRebateProgram()
{
	document.forms[0].action="newRebateProgram";
	document.forms[0].submit();	
	showWaitBusy();
}

$(document).ready(function(){
		<c:if test="${!empty rebateSearchForm.searchResult.values}">
		{
				$('#footer-rebate').css("width","50%");
		}
		</c:if>
		var inputTypeForGridRow = '';
		if(${e2ofn:hasAccess(appContext, 'REBATE', 'MassUpdate')}){
			inputTypeForGridRow = 'checkbox';
		}
		else{
			$("#checkAllDiv input[type = checkbox]}").attr("disabled", true);
			inputTypeForGridRow = 'radio';
		}
		var checkAllCheckBoxDisable = true;
		$("div#grid-result div.eto-grid-frozen tbody tr input[type="+inputTypeForGridRow+"]").each(function()
				{
			var value = $(this).val();
			if(RebateIdToStateMapping[value] == false)
				{
				$(this).attr("readonly",true);	
				$(this).attr("disabled",true);
				}
			else{
				checkAllCheckBoxDisable = false;
			  }
				})
			if(checkAllCheckBoxDisable){
			$("#grid-result .eto-all-rows-indicator").each(function(){
				$(this).attr("readonly",true);	
				$(this).attr("disabled",true);
			})}
		 if(inputTypeForGridRow=='radio'){
	            $("input[name=selectedPageKeys]").attr("disabled",true);
	       <c:if test="${e2ofn:hasAccess(appContext, 'REBATE', 'AssignOwner')}">
	       $("input[name=selectedPageKeys]").attr("disabled",false);
	       </c:if>
		 }
	});
</script>
<body onload="init()">

<div class="container" style="margin-top: 1%; margin-bottom: 1%;">
		<div style="font-weight: bold;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					 <li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
	</div>
	<form name="rebateSearchForm" action="submitRebateProgramSearch" method="POST">
		<input type="hidden" name="rebateProgramKey" />
		<input type="hidden" name="backAction" />
		<input type="hidden" name="preserveSearchValues" value="false"/>

		<script type="text/javascript">
		
var RebateIdToStateMapping = {};
var gridRowExposedAction = true;
if(${!e2ofn:hasAccess(appContext, 'REBATE', 'MassUpdate')})
var selectionType = 'radio';
var jsonColumn =  '${rebateSearchForm.columns}';
var gridColumns = [];
gridColumns.push('<fmt:message key="rp.name"/>');
gridColumns.push('<fmt:message  key="rp.status"/>');
gridColumns.push('<fmt:message  key="rp.rebateType"/>');
gridColumns.push('<fmt:message  key="rp.startDate"/>');
gridColumns.push('<fmt:message  key="rp.endDate"/>');
gridColumns.push('<fmt:message  key="rp.programType"/>');
gridColumns.push('<fmt:message  key="rp.programOwner"/>');
gridColumns.push('<fmt:message  key="rp.financialOwner"/>');

var gridRows = [];
<c:forEach var="row" items="${rebateSearchForm.searchResult.values}" varStatus="rowCount">
<c:set var="rp" value="${row.values[0]}"/>
<c:set var="im" value="${row.values[1]}"/>
var row = {};
row['checkboxValue'] = '${rp.rebateProgramKey}';
row['<fmt:message key="rp.name"/>'] = "<c:out value='${rp.rebateName}'/>";
row['<fmt:message key="rp.status"/>'] = "<c:out value='${rp.status}'/>";
row['<fmt:message key="rp.rebateType"/>'] = "<c:out value='${rp.rebateType}'/>";
row['<fmt:message key="rp.startDate"/>'] = '<fmt:formatDate value="${rp.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/>';
row['<fmt:message key="rp.endDate"/>'] = '<fmt:formatDate value="${rp.effectiveToDt}" pattern="${appContext.currentDateFormat}"/>';
row['<fmt:message key="rp.programType"/>'] = "<c:out value='${rp.programType}'/>";
row['<fmt:message key="rp.programOwner"/>'] = "<c:out value='${rp.programOwner}'/>";
row['<fmt:message key="rp.financialOwner"/>'] = "<c:out value='${rp.financialProgramOwner}'/>";

RebateIdToStateMapping['${rp.rebateProgramKey}'] =  ${e2ofn:allowOperation('Rebate',rp.status,'EditProgram')}
gridRows.push(row);

</c:forEach>

</script>
		<e2ot:searchContainerControl
			searchFields="${rebateSearchForm.allParameters}"
			form="${rebateSearchForm}" formName="rebateSearchForm"
			resultTableId="rebateSearchResultTable"
			showFilterCollapsed="${rebateSearchForm.filterAreaCollapsed}"
			showFilter="${rebateSearchForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${rebateSearchForm}"
			formName="rebateSearchForm" resultTableId="rebateSearchResultTable"
			showHideMenu="false" showOrderMenu="false" title="Rebates"
			showTitle="true">
			<%-- <e2o:scrollableTable id="rebateSearchResultTable" width="100%" 
   height="200px">
<e2o:scrollableTableHeader>
<e2o:scrollableTableHeaderRow>
<td class="fixedColumn ocmDisabled hcmDisabled"><fmt:message key="label.select"/>
<img style="cursor: pointer;" src="css/clearsort.gif" 
    alt="<fmt:message key="info.clear_sort"/>" onclick="clearAllSortFields($(this).closest('thead'))"/>
</td>
<td><fmt:message key="rp.name"/>
	<e2ot:orderByControl searchForm="${rebateSearchForm}" col="name"/>
</td>
<td><fmt:message key="rp.status"/>
	<e2ot:orderByControl searchForm="${rebateSearchForm}" col="status"/>
</td>        
<td><fmt:message key="rp.rebateType"/>
	<e2ot:orderByControl searchForm="${rebateSearchForm}" col="type"/>
</td>        
<td><fmt:message key="rp.startDate"/>
	<e2ot:orderByControl searchForm="${rebateSearchForm}" col="startDate"/>
</td>
<td><fmt:message key="rp.endDate"/>
    <e2ot:orderByControl searchForm="${rebateSearchForm}" col="endDate"/>
</td>
<td><fmt:message key="rp.programType"/></td>
<td><fmt:message key="rp.programOwner"/></td>
<td><fmt:message key="rp.financialOwner"/></td>
</e2o:scrollableTableHeaderRow>
</e2o:scrollableTableHeader>
<e2o:scrollableTableBody>
<c:forEach var="row" items="${rebateSearchForm.searchResult.values}" varStatus="rowCount">
<c:set var="rp" value="${row.values[0]}"/>
<c:set var="im" value="${row.values[1]}"/>
<e2o:scrollableTableBodyRow>
<td class="fixedColumn"><input type="radio" name="selectRowButton" onclick="javascript:goViewRebate('${rp.rebateProgramKey}')"/></td>
<td><c:out value="${rp.rebateName}"/></td>
<td><c:out value="${rp.status}"/></td>
<td><c:out value="${rp.rebateType}"/></td>
<td><fmt:formatDate value="${rp.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/></td>
<td><fmt:formatDate value="${rp.effectiveToDt}" pattern="${appContext.currentDateFormat}"/></td>
<td><c:out value="${rp.programType}"/></td>
<td><c:out value="${rp.programOwner}"/></td>
<td><c:out value="${rp.financialProgramOwner}"/></td>

</e2o:scrollableTableBodyRow>

</c:forEach>
</e2o:scrollableTableBody>
</e2o:scrollableTable> --%>
		</e2ot:searchResultsControl>
		<input type="hidden" name="requestType" />
		<input type="hidden" name="previousAction" />
		<input type="hidden" name="nextAction" />
		<input type="hidden" name="buttonAction" />
		<div id="footer-rebate" class="footer">
		<div class="eto-btn-group"
					style="margin-left: 15px; margin-top: 15px;">
		<c:if test="${e2ofn:hasAccess(appContext, 'REBATE', 'Create')}">
					<button type="button" class="eto-btn eto-btn--primary"
						id="newButton" onclick="javascript:goNewRebateProgram()">
						<bean:message key="button.newrebate" />
					</button>
		</c:if>
		<c:if test="${!empty rebateSearchForm.searchResult}">
					<button type="button" id="assignOwnerButton" class="eto-btn"
								onclick="javascript:openModal(this)" disabled>
								<fmt:message key="button.assignRebateOwner" />
							</button>
						</c:if>
						</div>
		</div>
		<div class="eto-modal" id="assignOwnerModal"
			style="width: 100%; overflow-y: scroll;">
			<div class="eto-modal__content"
				style="height: auto; width: auto; margin-top: 300px; margin-bottom: 250px; max-height: max-content; display: inline; max-height: none;">
				<header class="eto-modal__header">
					<span><label id="modalHeader"></label></span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<div class="eto-messageblock" data-message-type="warn"
						id="infoMesssage">
						<div class="eto-messageblock__body">
							<span id="selectedLabel"><label id="rowCount"
								style="font-weight: bold;"></label>&nbsp;<b>selected</b><br></span>
							Are you sure you want to make changes for the selected
							commodities
						</div>
					</div>

					<div id="checkAllDiv" style="margin-top: 15px;">
						<label class="eto-switch"> <input
							class="eto-switch__field" type="checkbox" id="checkAll"
							name="applyToAll" value="true"> <span
							class="eto-switch__box"></span> <span class="eto-switch__label"><b>Apply
									to ALL results on ALL pages</b></span>
						</label>
					</div>
					
					<div id="rebateOnwerTypeDiv" class="eto-select"
						style="margin-top: 15px; width: 300px;">
						<label class="eto-select__label"><fmt:message
								key="rp.ownerType" /></label>
						<div class="eto-select__field-container">
									<select class="eto-select__field"
										name="assignmentOwnerType" id="OwnerTypeList"
										onchange="callOnOwnerTypeChange();">
										<option value="programOwner" selected>Program Owner</option>
										<option value="financeOwner">Finance Owner</option>
									</select>
						</div>
					</div>
					
					<div id = "assignToDivOwner" style="margin-top: 15px">
						<div class="eto-autocomplete" id="ownerUserIdAutoComplete"
							style="width: 300px;" >
							<label class="eto-autocomplete__label"><fmt:message
									key="as.assignTo" /></label>
									
							<input class="eto-autocomplete__field" type="text"
										 placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>' id="ownerUserId"
										name="ownerUserId" />
				   			<div class="eto-autocomplete__message"></div>
							<div class="eto-results"></div>
						</div>
					</div>
				</section>
				<footer class="eto-modal__footer"
					style="margin-top: 5px; height: 60px; border-top-style: solid; border-top-width: 1px; border-top-color: lightgray;">
					<div class="eto-btn-group"
						style="margin-top: 10px; margin-bottom: -3px">
						<button class="eto-btn" data-modal-close>Cancel</button>
						<button id="dynamicButton" class="eto-btn eto-btn--primary" type="button">
							<label id="dynamicButtonLabel" style="cursor: pointer;"></label>
						</button>
					</div>
				</footer>
			</div>
		</div>
		
	</form>
</body>
<script type="text/javascript">
		setGridEvents(grid);
		function setGridEvents(grid){
		if(grid != null){

		grid.on('rowAction', function() {
			goViewRebate(arguments[1]);
		});

		grid.on("rowSelection", function(event) {
			var selectedPageKeys = document.getElementsByName("selectedPageKeys");
			var checked = false;
			for (var i = 0; i < selectedPageKeys.length; i++) {
				if (selectedPageKeys[i].checked) {
					checked = true;
					break;
				}
			}

			if (checked) {
				$("#assignOwnerButton").removeClass().addClass(
				"eto-btn eto-btn--primary");
				$("#assignOwnerButton").removeAttr("disabled");
			} else {
				$("#assignOwnerButton").removeClass().addClass("eto-btn");
				$("#assignOwnerButton").attr("disabled", "disabled");
			}
		});
		}
		}
		
		function callOnOwnerTypeChange(){
			var ownerType = $('#OwnerTypeList').val(); 
			$("#modalHeader").html(massUpdateRebateOwner[ownerType].modalHeader);
			$('#dynamicButton').replaceWith($('#dynamicButton').clone());
			document.getElementById('dynamicButton').addEventListener("click",
					massUpdateRebateOwner[ownerType].dynamicMethod)
			$("#dynamicButtonLabel").html(massUpdateRebateOwner[ownerType].dynamicButtonLabel);
		}
		
				
				var modal = new eto.Modal({
					el : document.querySelector('#assignOwnerModal')
				});
				
				var autoOwner = new eto.Autocomplete({
					el : document.querySelector('#ownerUserIdAutoComplete'),add: true
				});
				
				
				
				autoOwner.on('inputChange', function(query) {
					/**var url = "ajaxQueryUserId?q=" + query;**/
					var ownerType = $('#OwnerTypeList').val();
					var url = massUpdateRebateOwner[ownerType].url + query;
					$.ajax({
						url : url,
						data : massUpdateRebateOwner[ownerType].data,
						success : function(result) {
							var arr;
							if (result.includes("|")) {
								arr = result.split("|");
							} else {
								arr = result.split("\n");
							}
							
							autoOwner.setContent(arr);
							autoOwner.open();
						}
					});
				});
				
			
				var rowCountGlobal = 0;
				function openModal(button) {
		
					var id = button.id;
					var rowCount = 0;

					var selectedPageKeys = document.getElementsByName("selectedPageKeys");
					for (var i = 0; i < selectedPageKeys.length; i++) {
						if (selectedPageKeys[i].checked) {
							rowCount++;
						}
					}
					
					callOnOwnerTypeChange();
					
					$("#rowCount").html(rowCount);
					rowCountGlobal = rowCount;
					if($("#checkAll").attr("checked")){
						$("#rowCount").html(${rebateSearchForm.totalRows});	
					}

					modal.open();
				}
				
				/* var dropdown = new eto.Dropdown({ el: document.querySelector('#dropdown-example') }); */
				
				$("#checkAll").change(function() {
				    if(this.checked) {
				    	// $("#selectedLabel").css('display','none');
				    	$("#rowCount").html(${rebateSearchForm.totalRows});
				    }
				    else{
				    	// $("#selectedLabel").css('display','block');
				    	$("#rowCount").html(rowCountGlobal);	
				    }
				});
		</script>
</html>
