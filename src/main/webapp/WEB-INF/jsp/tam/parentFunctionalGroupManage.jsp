<%@page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Manage-ParentFunctionalGroup" />
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
			document.forms[0].action = "assignToParentFunctionalGroup";
			document.forms[0].backAction.value = "searchParentGroup";
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
			document.forms[0].action = "createParentFunctionalGroup";
			document.forms[0].backAction.value = "searchParentGroup";
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

	function goEditGroup(pfgId) {
		showWaitBusy();
		document.forms['parentFunctionalGroupForm'].action = "editParentFunctionalGroup";
		document.forms['parentFunctionalGroupForm'].parentFunctionalGroupId.value = pfgId;
		document.forms['parentFunctionalGroupForm'].backAction.value = "searchParentGroup";
		document.forms['parentFunctionalGroupForm'].submit();
	}

	function init() {
		resizeResultArea();
		setupHideColumnState('parentFunctionalGroupResultTable', true);
		setupOrderColumnState('parentFunctionalGroupResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}

	$(document).ready(function(){
			$('input[name="value(parentName)"]').keyup(function() {
				var parentName = $("#searchField1").val().trim().replace(/ {1,}/g," ");
				$('input[name="value(parentName)"]').val(parentName);
			});
			  <c:if test="${!empty parentFunctionalGroupForm.searchResult.values}">
			  var div = document.createElement('div');
	   		  div.className = 'eto-btn-group';
	   		  var d = ""; 
			 <c:if test="${e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP', 'AddParent')}"> 
			 d ='<button type="button" class="eto-btn eto-btn--primary" onclick="javascript:goCreateGroup();"><fmt:message key="button.createParentGroup" /></button>'
	  			  + '<button type="button" class="eto-btn " onclick="javascript:goAssignGroup();"> <fmt:message key="button.assignToParentGroup" /></button>';
		   	 </c:if> 
		     <c:if test="${!empty parentFunctionalGroupForm.previousAction}">
		   			 d = d + '<button type="button" class="eto-btn " onclick="javascript:goBack(document.forms['parentFunctionalGroupForm'],'${fn:escapeXml(parentFunctionalGroupForm.previousAction)}')"><fmt:message key="button.back" /></button>'
		     </c:if>
		    <c:if test="${!empty parentFunctionalGroupForm.nextAction}">
		   		  d = d + '<button type="button" class="eto-btn" onclick="javascript:goNext(document.forms['parentFunctionalGroupForm'],'${fn:escapeXml(parentFunctionalGroupForm.nextAction)}');"><bean:message key="button.next" />';
		    </c:if> 
		    div.innerHTML = d;
		    document.getElementById('searchFiledsButtons').appendChild(div);
		    </c:if>
			});
</script>
<style type="text/css">
.anchorLike:hover {
	cursor: pointer;
}

.anchorLike {
	color: #428bca;
}
</style>
<body onload="init()">
	<%
		ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(
				FlexAttributeManager.ITEM.getFlexAttributeDefinitionList());
		pageContext.setAttribute("flex", flexAttributeDefns);
	%>
	<form name="parentFunctionalGroupForm" action="searchParentGroup" method="POST">
		<script >
			var jsonColumn =  '${parentFunctionalGroupForm.columns}';
			var gridColumns = [];
			gridColumns.push('<fmt:message key="functionalGroup.functionalGroupName" />');
			gridColumns.push('<fmt:message key="functionalGroup.parentName" />');
			gridColumns.push('<fmt:message key="functionalGroup.parentGroupType" />');

			var gridRows = [];
			<c:forEach var="row" items="${parentFunctionalGroupForm.searchResult.values}" varStatus="rowCount">
				<c:set var="pfg" value="${row.values[0]}" />
				<c:set var="fg" value="${row.values[1]}" />
				<c:set var="pfg_type" value="${row.values[2]}" />
				var row = {};
				row['checkboxValue'] = '<c:out value = "${fg.functionalGroupId}"/>';
				row['<fmt:message key="functionalGroup.functionalGroupName" />'] = '<c:out value = "${fg.name}"/>';
				var pfgNameLink = '<a href=\'javascript:goEditGroup("${pfg.parentFunctionalGroupId}")\'>${pfg.name}</a>';
				row['<fmt:message key="functionalGroup.parentName" />'] = pfgNameLink;
				row['<fmt:message key="functionalGroup.parentGroupType" />'] = '<c:out value = "${pfg_type}"/>';
				gridRows.push(row);
			</c:forEach>
		</script>
		<input type="hidden" name="backAction" />
		<input type="hidden" name="parentFunctionalGroupId" />
		<e2ot:searchContainerControl
			searchFields="${parentFunctionalGroupForm.allParameters}"
			formName="parentFunctionalGroupForm" form="${parentFunctionalGroupForm}"
			resultTableId="parentFunctionalGroupResultTable"
			showFilterCollapsed="${parentFunctionalGroupForm.filterAreaCollapsed}"
			showFilter="${parentFunctionalGroupForm.showFilterArea}"
			numColumns="3" />
		<e2ot:searchResultsControl searchForm="${parentFunctionalGroupForm}"
			formName="parentFunctionalGroupForm"
			resultTableId="parentFunctionalGroupResultTable" showOrderMenu="true"
			showHideMenu="true"  title="Manage Parent" />
			 
	</form>
</body>
</html>