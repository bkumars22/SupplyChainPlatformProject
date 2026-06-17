<%@page import="java.util.Comparator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="com.scplatform.pcm.common.entity.AttributeDefn"%>
<%@page import="com.scplatform.pcm.common.entity.AdditionalAttributeManager"%>
<%@ include file="../common.jspf"%>
<%@ page import="com.scplatform.pcm.item.entity.Item"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@ page import="java.util.ArrayList"%>
<!DOCTYPE html>

<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-Item" />
</head>

<script>
	function goAsynchronusSearch(){
		document.forms[0].action = "asynhronusSearchForItem.do";
		document.forms[0].submit();
		showWaitBusy();
	}
	
	function callOnChange(field) {
		if (field.name == 'value(owner)') {
			var regionsField = document.getElementsByName("values(regions)")[0];
			if (!regionsField) {
				return;
			}
			if (field.value == 'REGIONAL') {
				regionsField.disabled = false;
			} else {
				hideElement();
			}
		}
	}
	function hideElement() {
		var ownerField = document.getElementsByName("value(owner)")[0];
		var regionsField = document.getElementsByName("values(regions)")[0];
		if (!ownerField || !regionsField) {
			return;
		}
		if (ownerField.value != 'REGIONAL') {
			regionsField.disabled = true;
		}
	}

	function init() {
		hideElement();
		resizeResultArea();
	}
	
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
</script>
<body onload="init()">
	<%
			ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(
			FlexAttributeManager.ITEM.getFlexAttributeDefinitionList());
			pageContext.setAttribute("flex", flexAttributeDefns);
	        // Sort flex attributes
			List<AttributeDefn> attributeDefns = new ArrayList<AttributeDefn>(
			AdditionalAttributeManager.ITEM.getAdditionalAttributeDefinitionList());
	        Collections.sort(attributeDefns, new Comparator<AttributeDefn>() {
		    public int compare(AttributeDefn a1, AttributeDefn a2) {
			     return a1.getName().compareToIgnoreCase(a2.getName());
		        }
			});
			pageContext.setAttribute("attributeDefns", attributeDefns);
	%>
	<form name="itemOnlySearchForm" action="submitItemOnlySearch" method="POST">
	<input type="hidden" name="reportType" value="Item">
	<div style="font-weight: bold; white-space: pre-line;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					<li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
		<script>
			var jsonColumn =  '${itemOnlySearchForm.columns}';
			var selectionType = 'none';
			var gridColumns = [];
			gridColumns.push('<fmt:message  key="item.audit.history"/>');
			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.itemOnlysearchPage.grid.thead')}"/>
			<c:forEach var="col" items="${columnHeader}" varStatus="count">
					if(('<fmt:message key="${fn:trim(col)}"/>' == 'Platform') || ('<fmt:message key="${fn:trim(col)}"/>' == 'Responsibility')){
	               		 gridColumns.push('<fmt:message key="${fn:trim(col)}"/>'+'_EXPANDCELL');
            		} else {
	               		 gridColumns.push('<fmt:message key="${fn:trim(col)}"/>');
           			 }
			</c:forEach>
			<c:if test="${e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP' , 'Read')}">
					gridColumns.push('<fmt:message  key="item.functionalGroups"/>');
					gridColumns.push('<fmt:message  key="functionalGroup.parentName"/>');
			</c:if>
			
			var gridRows = [];
			
			<c:forEach var="row" items="${itemOnlySearchForm.searchResult.values}" varStatus="rowCount">
			<c:set var="i" value="${row.values[0]}"/>
			<c:set var="be" value="${row.values[1]}"/>
			var itemNumber = "<c:out value='${i.itemNumber}'/>";
			var businessEntityName = '${be.businessEntityName}';
			var itemType = '<fmt:message  key="item.type"/>';

			var row = {};
			row['<fmt:message key="item.audit.history"/>']='<button type="button" class="eto-icon-btn" title="History" style="color: #468293;" id="historyButton" onclick="javascript:showItemAuditHistory(\'${i.itemKey}\')"><i class="md-icon">history</i></button>';          
			var itemNumberLink = '<a href="#" onClick="openPopOver(\'${i.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover">'+itemNumber+'</a>';
			row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
			var itemTypeVal = "<c:out value='${i.itemType}'/>";
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
			row['<fmt:message  key="item.itemDescription"/>'] = "<c:out value='${i.description}'/>";
			row['<fmt:message  key="item.businessName"/>'] = "<c:out value='${be.businessEntityName}'/>";
			row['<fmt:message  key="item.businessEntityIdentifier"/>'] = "<c:out value='${be.businessEntityIdentifier}'/>";
			row['<fmt:message  key="item.itemRevision"/>'] = "<c:out value='${i.itemVersion.revision}'/>";
			row['<fmt:message  key="item.insertDate"/>'] = "<c:out value='${i.insertDate}'/>";
			row['<fmt:message  key="item.updateDate"/>'] = "<c:out value='${i.updateDate}'/>";

			var catNames = [];
			<c:forEach var="cat" items="${i.categories}" varStatus="ccount">
			var catName = "<c:out value='${cat.categoryName}'/>";
			catNames.push(catName);
			</c:forEach>
			//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
			row['<fmt:message  key="item.categoryName"/>'] = catNames.join(',');

			var platFormNames = [];
			<c:forEach var="p" items="${i.platforms}" varStatus="pcount">
			var platformName = "<c:out value=' ${p.platformName} '/>";
			platFormNames.push(platformName);
			</c:forEach>
			row['<fmt:message  key="item.platform"/>'] = platFormNames
					.join(',');
			row['<fmt:message  key="item.itemClassification"/>'] = "<c:out value='${i.itemClassification}'/>";
			row['<fmt:message  key="item.itemProductFamily"/>'] = "<c:out value='${i.productFamily}'/>";

			var responsibilities = [];
			<c:forEach var="ia" items="${i.assignments}" varStatus="iacount">
			var responsiblity = "<c:out value='${ia.userId} - ${ia.responsibility}'/>";
			var region = "<c:out value='${ia.region}'/>";
			if (region) {
				responsiblity = responsiblity + '- ' + region;
			}
			responsibilities.push(responsiblity);
			</c:forEach>
			row['<fmt:message  key="as.responsibility"/>'] = responsibilities.join(';');
			row['<fmt:message  key="item.dataSource"/>'] = "<c:out value='${i.dataSource}'/>";
			var eolState = '${!empty i.eolType? i.eolType :"ACTIVE"}';
			/* <c:if test='${i.eol}'>
				<c:choose>
					<c:when test='${!empty i.eolType}'>
						eolState = '${i.eolType}';
					</c:when>
					<c:otherwise>
						eolState = 'INACTIVE';
					</c:otherwise>
				</c:choose>
			</c:if> */
			row['<fmt:message  key="item.eol.state"/>'] = eolState;
			var topLevel = "No";
			<c:if test="${!empty i.isTopLevel && i.isTopLevel}">
			topLevel = "Yes";
		 </c:if>
			row['<fmt:message  key="item.isTopLevel"/>'] = topLevel; 
			<c:forEach var="fl" items="${flex}" varStatus="count">
				row['${fl.name}'] = "<c:out value='${i[fl.associatedAttribute]}'/>";
			</c:forEach>
			<c:if test="${e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP' , 'Read')}">
				var functionalGroupNames = [];
				<c:forEach var="fgs" items="${i.functionalGroups}" varStatus="count">
					var functionalGroupName = "<c:out value='${fgs.name}'/>";
					functionalGroupNames.push(functionalGroupName);
				</c:forEach>
				row['<fmt:message  key="item.functionalGroups"/>'] = functionalGroupNames
					.join(',');

				var parentFunctionalGroupNames = [];
				<c:forEach var="fgs" items="${i.functionalGroups}" varStatus="count">
					<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}">
						var parentFunctionalGroupName = "<c:out value='${pfg.name}'/>";
						parentFunctionalGroupNames.push(parentFunctionalGroupName);
					</c:forEach>
				</c:forEach>
				row['<fmt:message  key="functionalGroup.parentName"/>'] = parentFunctionalGroupNames
					.join(',');
			</c:if>
			
			<c:forEach var="fl" items="${flex}" varStatus="count">
				<fmt:message key="flex.item.${fl.associatedAttribute}" var="flexAttr"/>
			<c:if test="${fl.associatedAttribute == 'stringAttribute1'}">
				row['${flexAttr}']= '${i.stringAttribute1}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute2'}">
				row['${flexAttr}']= '${i.stringAttribute2}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute3'}">
				row['${flexAttr}']= '${i.stringAttribute3}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute4'}">
				row['${flexAttr}']= '${i.stringAttribute4}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute5'}">
				row['${flexAttr}']= '${i.stringAttribute5}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute6'}">
				row['${flexAttr}']= '${i.stringAttribute6}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute7'}">
				row['${flexAttr}']= '${i.stringAttribute7}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute8'}">
				row['${flexAttr}']= '${i.stringAttribute8}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute9'}">
				row['${flexAttr}']= '${i.stringAttribute9}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'stringAttribute10'}">
				row['${flexAttr}']= '${i.stringAttribute10}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute1'}">
				row['${flexAttr}'] = '${xlobFGName}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute2'}">
				row['${flexAttr}']= '${i.numberAttribute2}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute3'}">
				row['${flexAttr}']= '${i.numberAttribute3}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute4'}">
				row['${flexAttr}']= '${i.numberAttribute4}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute5'}">
			row['${flexAttr}']= '${i.numberAttribute5}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute6'}">
			row['${flexAttr}']= '${i.numberAttribute6}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute7'}">
			row['${flexAttr}']= '${i.numberAttribute7}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute8'}">
			row['${flexAttr}']= '${i.numberAttribute8}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute9'}">
			row['${flexAttr}']= '${i.numberAttribute9}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'numberAttribute10'}">
			row['${flexAttr}']= '${i.numberAttribute10}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute1'}">
			row['${flexAttr}']='${i.dateAttribute1}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute2'}">
			row['${flexAttr}']='${i.dateAttribute2}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute3'}">
			row['${flexAttr}']='${i.dateAttribute3}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute4'}">
			row['${flexAttr}']='${i.dateAttribute4}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute5'}">
			row['${flexAttr}']='${i.dateAttribute5}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute6'}">
			row['${flexAttr}']='${i.dateAttribute6}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute7'}">
			row['${flexAttr}']='${i.dateAttribute7}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute8'}">
			row['${flexAttr}']='${i.dateAttribute8}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute9'}">
			row['${flexAttr}']='${i.dateAttribute9}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'dateAttribute10'}">
			row['${flexAttr}']='${i.dateAttribute10}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute1'}">
			row['${flexAttr}']='${i.floatAttribute1}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute2'}">
			row['${flexAttr}']='${i.floatAttribute2}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute3'}">
			row['${flexAttr}']='${i.floatAttribute3}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute4'}">
			row['${flexAttr}']='${i.floatAttribute4}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute5'}">
			row['${flexAttr}']='${i.floatAttribute5}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute6'}">
			row['${flexAttr}']='${i.floatAttribute6}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute7'}">
			row['${flexAttr}']='${i.floatAttribute7}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute8'}">
			row['${flexAttr}']='${i.floatAttribute8}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute9'}">
			row['${flexAttr}']='${i.floatAttribute9}';
			</c:if>
			<c:if test="${fl.associatedAttribute == 'floatAttribute10'}">
			row['${flexAttr}']='${i.floatAttribute10}';
			</c:if>
			</c:forEach> 
			<c:forEach var="attr" items="${attributeDefns}" varStatus="count">
			     var name="${attr.name}";
			     var value="${i.getAttribute(attr.name)}";
			     row[name]= value;
		    </c:forEach>		
			gridRows.push(row);
			</c:forEach>
		</script>
		<e2ot:searchContainerControl
			searchFields="${itemOnlySearchForm.allParameters}"
			form="${itemOnlySearchForm}" formName="itemOnlySearchForm"
			resultTableId="itemSearchResultTable"
			showFilterCollapsed="${itemOnlySearchForm.filterAreaCollapsed}"
			showFilter="${itemOnlySearchForm.showFilterArea}" numColumns="5" />
		<e2ot:searchResultsControl searchForm="${itemOnlySearchForm}"
			formName="itemOnlySearchForm" resultTableId="itemSearchResultTable"
			showOrderMenu="false" showHideMenu="false" title="Items"
			showTitle="true" />
	</form>
            <script>
            let show = function() {
                <c:url var="linkHref" value="initReports.do">
                <c:param name="reportType" value="Item" />
                </c:url>
                reloadBreadCrumb('initReports.do');
                document.forms[0].action = "${linkHref}";
                document.forms[0].submit();
            };
 				parent.parent.reportCall = function() { 
 				show();
                showWaitBusy();
             }
            </script>
			 <%@ include file="../fullModal.jspf" %>
</body>
</html>