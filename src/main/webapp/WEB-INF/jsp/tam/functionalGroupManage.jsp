<%@ include file="../common.jspf"%>
<%@ page import="com.scplatform.pcm.item.entity.Item"%>
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
<e2ot:help contextName="Manage-FunctionalGroup" />
</head>
<script>
	function goAssignGroup() {
		var itemList = [];
		$('input[name="selectedPageKeys"]:checked').each(function() {
			itemList.push(this.value);
		});

		if (itemList.length == 0) {
			showOkMessageBox(
					'OK',
					'WARN',
					"<fmt:message key='warn.functionalGroup.no_row_selected_to_delete'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
		} else {
			document.forms[0].action = "assignToFunctionalGroup";
			document.forms[0].submit();
			showWaitBusy();
		}

	}
	
	function goCreateGroup() {
		var itemList = [];
		$('input[name="selectedPageKeys"]:checked').each(function() {
			itemList.push(this.value);
		});

		if (itemList.length == 0) {
			showOkMessageBox(
					'OK',
					'WARN',
					"<fmt:message key='warn.functionalGroup.no_row_selected_to_delete'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
		} else {
			document.forms[0].action = "createFunctionalGroup";
			document.forms[0].submit();
			showWaitBusy();
		}

	}

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

	function goEditGroup(fgId) {
		showWaitBusy();
		document.forms['functionalGroupForm'].action = "editFunctionalGroup";
		document.forms['functionalGroupForm'].functionalGroupId.value = fgId;
		document.forms['functionalGroupForm'].backAction.value = "searchManageFunctionalGroup";
		document.forms['functionalGroupForm'].submit();
	}
	
	function init() {
		resizeResultArea();
		setupHideColumnState('functionalGroupResultTable', true);
		setupOrderColumnState('functionalGroupResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}
	
	$(document).ready(function() {
		<c:if test="${!empty functionalGroupForm.searchResult.values}">
		var div = document.createElement('div');
	    div.className = 'eto-btn-group';
	    var d = "";
	    <c:if test="${e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'Save')}">
			var d ='<button type="button" class="eto-btn eto-btn--primary" onclick="javascript:goCreateGroup();">Create Group</button>'
	 		+ '<button type="button" class="eto-btn" onclick="javascript:goAssignGroup();">Assign To Group</button>';
	    </c:if>
	    div.innerHTML =d;
	    document.getElementById('searchFiledsButtons').appendChild(div);
	    </c:if>
		 
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
	
	let suppliers = [];
</script>
<style type="text/css">
.anchorLike:hover {
	cursor: pointer;
}

.anchorLike {
	color: #428bca;
	text-decoration: none;
}
</style>
<body onload="init()">
	<%
		ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(
				FlexAttributeManager.ITEM.getFlexAttributeDefinitionList());
		pageContext.setAttribute("flex", flexAttributeDefns);
	%>
	<form name="functionalGroupForm" action="searchManageFunctionalGroup" method="POST">
		<script>
		var jsonColumn =  '${functionalGroupForm.columns}';
		var gridColumns = [];
		 	gridColumns.push('<fmt:message key="item.itemNumber"/>');
			gridColumns.push('<fmt:message key="functionalGroup.itemDescription" />');
			gridColumns.push('<fmt:message key="item.type" />');
			gridColumns.push('<fmt:message key="item.businessName" />');
			gridColumns.push('<fmt:message key="item.EOL"/>');
			gridColumns.push('<fmt:message key="functionalGroup.itemSupplier" />');
			gridColumns.push('<fmt:message key="functionalGroup.commodityName" />');
			gridColumns.push('<fmt:message key="functionalGroup.responsibilityUser" />'+'_EXPANDCELL');
			gridColumns.push('<fmt:message key="functionalGroup.functionalGroupName" />');
			gridColumns.push('<fmt:message key="functionalGroup.groupType" />');
			gridColumns.push('<fmt:message key="flex.item.stringAttribute5" />');
			gridColumns.push('<fmt:message key="functionalGroup.status" />');
			gridColumns.push('<fmt:message key="functionalGroup.platform" />');
			gridColumns.push('<fmt:message key="functionalGroup.lob" />');
			gridColumns.push('<fmt:message key="functionalGroup.parentName" />');
			gridColumns.push('<fmt:message key="functionalGroup.parentItemNumber" />');
			gridColumns.push('<fmt:message key="functionalGroup.parentItemBusinessName" />');
			gridColumns.push('<fmt:message key="functionalGroup.ODMPartNumber" />');
			gridColumns.push('<fmt:message key="functionalGroup.ODMPartBusinessName" />');
            <c:if test="${e2ofn:getConfigValue('scplatform.feature.functionalgroup.aliasName.show') == 'true'}">
                gridColumns.push('<fmt:message key="functionalGroup.alias.functionalGroupName" />');
            </c:if>
			
			gridColumns.push('<fmt:message key="functionalGroup.TAM.exist" />');   
			var gridRows = [];
			<c:forEach var="row" items="${functionalGroupForm.searchResult.values}" varStatus="rowCount">
				<c:set var="im" value="${row.values[0]}" />
				<c:set var="cat" value="${row.values[1]}" />
				<c:set var="fg" value="${row.values[2]}" />
				<c:set var="pfg" value="${row.values[3]}" />
				<c:set var="fglob" value="${row.values[4]}" />
					var row = {};
				<c:set var="itemKey" value="${im.itemKey}"/>
				row['checkboxValue']= '<c:out value = "${itemKey}"/>';
				var itemNumberLink = '<a href="#" onClick="openPopOver(\'${im.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${im.itemNumber}" removeColon="true"/></a>';
				row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
				<c:set var="itemDesc" value="${im.description}" />
				 row['<fmt:message key="functionalGroup.itemDescription" />']='<c:out value="${itemDesc}" />';
				 var itemTypeVal = '${im.itemType}';
				var shownItemType;
				if (itemTypeVal == 'I') {
					shownItemType = '<fmt:message key="item.item"/>';
				} else if (itemTypeVal == 'CFG') {
					shownItemType = '<fmt:message key="item.cfgGroup"/>';
				} else if (itemTypeVal == 'M') {
					shownItemType = '<fmt:message key="item.mfgItem"/>';
				} else if (itemTypeVal == 'S') {
					shownItemType = '<fmt:message key="item.supplierItem"/>';
				} else if (itemTypeVal == 'PI') {
					shownItemType = '<fmt:message key="item.phantomItem"/>';
				} else {
					shownItemType = itemTypeVal;
				}
				row['<fmt:message  key="item.type"/>'] = shownItemType;
				row['<fmt:message key="item.businessName" />'] = '<c:out value = "${im.businessEntity.businessEntityName}"/>';
				<c:set var="supplierNames" value="" />

				suppliers = [];
				<c:forEach var="avl" items="${im.avls}">
						suppliers.push('<c:out value="${avl.supplier.businessEntityName}"/>');
				</c:forEach>
				row['<fmt:message  key="functionalGroup.itemSupplier"/>'] = suppliers.unique().join(',');
				var eolState = '${!empty im.eolType? im.eolType :"ACTIVE"}';

				row['<fmt:message  key="item.EOL"/>'] = eolState;
				row['<fmt:message  key="functionalGroup.status"/>'] = '<c:out value = "${fg.status}"/>';
				row['<fmt:message  key="functionalGroup.commodityName"/>'] = '<c:out value = "${cat.categoryName}"/>';
					
				row['<fmt:message  key="functionalGroup.responsibilityUser"/>'] ='<c:forEach var="ia" items="${im.assignments}" varStatus="iacount">'
				+'<c:if test="${ia.responsibility != 'BUYER'}"><c:if test="${!iacount.first}">, </c:if><c:out value="${ia.userId} - ${ia.responsibility}" /></c:if></c:forEach>';
				 var fgNameLink =  "";
				 <c:if test="${not empty fg.name}">
				 fgNameLink =  '<a href=\'javascript:goEditGroup("${fg.functionalGroupId}")\'>${fg.name}</a>';
				 </c:if>
				 row['<fmt:message key="functionalGroup.functionalGroupName" />'] =fgNameLink;
				 row['<fmt:message  key="functionalGroup.groupType"/>'] = '<c:out value = "${fg.type}"/>';
				  row['<fmt:message  key="flex.item.stringAttribute5"/>'] = '<c:out value = "${im.stringAttribute5}"/>'
				 row['<fmt:message  key="functionalGroup.platform"/>'] = '<c:out value = "${fg.fgPlatform}"/>';
				 row['<fmt:message key="functionalGroup.lob" />'] = '<c:out value = "${fglob.lobValue}"/>';
				 var parentFgLink = "";
				 <c:if test="${not empty pfg.name}">
				 var parentFgLink ='<a href="editParentFunctionalGroupByLink?parentFunctionalGroupId=${pfg.parentFunctionalGroupId}&backAction=searchManageFunctionalGroup" />${pfg.name}</a>';
				 </c:if>
				 row['<fmt:message  key="functionalGroup.parentName"/>'] =parentFgLink;

			 <!-- TAM existence will be checked via async API -->
			 var functionalGroupId = '${fg.functionalGroupId}';
			 row['<fmt:message  key="functionalGroup.TAM.exist"/>'] = '<span class="tam-exist-loader" data-fgid="' + functionalGroupId + '">Loading...</span>';

			 // Store functional group ID for later async API call
			 row.functionalGroupId = functionalGroupId;
					var parentItemNumberLink = '<a href="#" onClick="openPopOver(\'${fg.parentItem.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${fg.parentItem.itemNumber}" removeColon="true"/></a>';
					var ODMPartNumberLink = '<a href="#" onClick="openPopOver(\'${fg.ODMPart.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${fg.ODMPart.itemNumber}" removeColon="true"/></a>';
					row['<fmt:message key="functionalGroup.parentItemNumber" />'] = parentItemNumberLink
					row['<fmt:message key="functionalGroup.parentItemBusinessName" />'] = '<c:out value = "${fg.parentItem.businessEntity.businessEntityName}"/>';
				    row['<fmt:message key="functionalGroup.ODMPartNumber" />'] =  ODMPartNumberLink
					row['<fmt:message key="functionalGroup.ODMPartBusinessName" />'] ='<c:out value = "${fg.ODMPart.businessEntity.businessEntityName}"/>';
                    <c:if test="${e2ofn:getConfigValue('scplatform.feature.functionalgroup.aliasName.show') == 'true'}">
                        row['<fmt:message key="functionalGroup.alias.functionalGroupName" />'] ='<c:out value = "${fg.aliasName}"/>';
                    </c:if>
				gridRows.push(row);
			</c:forEach>

			</script>
		<input type="hidden" name="backAction" />
		<input type="hidden" name="functionalGroupId" />
		<e2ot:searchContainerControl
			searchFields="${functionalGroupForm.allParameters}"
			formName="functionalGroupForm" form="${functionalGroupForm}"
			resultTableId="functionalGroupResultTable"
			showFilterCollapsed="${functionalGroupForm.filterAreaCollapsed}"
			showFilter="${functionalGroupForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${functionalGroupForm}"
			formName="functionalGroupForm"
			resultTableId="functionalGroupResultTable" showOrderMenu="true"
			showHideMenu="true" title="Manage Functional Group" />
	</form>
</body>
<script>
var expland_class = 'eto-grid-expand';
$(document).ready(
		function() {
			
			document.body.addEventListener('DOMSubtreeModified', handleClickDOM, true); 
			
			$('div .eto-grid-expand__truncated').each(function(){
				if($(this).html().length == 0){
					var value = $(this).parent().children( ".eto-grid-expand__content" ).html();
					$(this).closest('td').removeClass("eto-grid-expand");
					$(this).closest('td').html(value);
				}
			});
			$('td[data-lines]').each(function(){
				if($(this).attr("class").indexOf(expland_class) == -1){
					$(this).removeAttr('data-lines');
				}
			});
		});
		
		
function handleClickDOM(){
	$('div .eto-grid-expand__truncated').each(function(){
		if($(this).html().length == 0){
			var value = $(this).parent().children( ".eto-grid-expand__content" ).html();
			$(this).closest('td').removeClass("eto-grid-expand");
			$(this).closest('td').html(value);
		}
	});
	$('td[data-lines]').each(function(){
		if($(this).attr("class").indexOf(expland_class) == -1){
			$(this).removeAttr('data-lines');
		}
	});
}
function gridUpdateCallBack(){
    $('div .eto-grid-expand__truncated').each(function(){
		if($(this).html().length == 0){
			var value = $(this).parent().children( ".eto-grid-expand__content" ).html();
			$(this).closest('td').removeClass("eto-grid-expand");
			$(this).closest('td').html(value);
		}
	});
	$('td[data-lines]').each(function(){
		if($(this).attr("class").indexOf(expland_class) == -1){
			$(this).removeAttr('data-lines');
		}
	});
}

/**
 * Function to load TAM existence status via async API for all functional groups
 * Uses apiService.getAsync() for guaranteed asynchronous execution
 */
function loadTAMExistenceStatus() {
	// Find all TAM loader spans and fetch their data
	$('.tam-exist-loader').each(function() {
		var loader = $(this);
		var functionalGroupId = loader.data('fgid');

		if (functionalGroupId) {
			// Call the async API using apiService.getAsync()
			apiService.getAsync('checkTAMExistByFunctionalGroup', {
				functionalGroupId: functionalGroupId
			})
			.then(function(response) {
				// Update the cell with the response (true/false)
				var tamStatus = response === true ? 'Yes' : 'No';
				loader.text(tamStatus);
			})
			.catch(function(error) {
				console.error('Error checking TAM existence for FG ID: ' + functionalGroupId, error);
				loader.text('Error');
			});
		} else{
		   loader.text('');
		}
	});
}

// Load TAM existence status when page is ready
$(document).ready(function() {
	// Load TAM existence data after grid is rendered
	setTimeout(function() {
		loadTAMExistenceStatus();
	}, 500);
});
</script>
</html>



