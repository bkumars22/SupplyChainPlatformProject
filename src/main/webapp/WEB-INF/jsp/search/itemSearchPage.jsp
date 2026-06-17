<%@ include file="../common.jspf"%>
<%@ page import="com.scplatform.pcm.item.entity.Item"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@page import="com.scplatform.pcm.common.entity.AttributeDefn"%>
<%@page import="com.scplatform.pcm.common.entity.AdditionalAttributeManager"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Comparator"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Search-Item" />
</head>

<script>
	function callOnChange(field) {
		if (field.name == 'value(owner)') {
			if (field.value == 'REGIONAL') {
				document.getElementsByName("values(regions)")[0].disabled = false;
			} else {
				hideElement();
			}
		}
	}
	function hideElement() {
		if (document.getElementsByName("value(owner)")[0].value != 'REGIONAL') {
			document.getElementsByName("values(regions)")[0].disabled = true;
		}
	}

	function init() {
		hideElement();
		resizeResultArea();
	}
</script>

<body onload="init()">
	<%
		ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(
				FlexAttributeManager.ITEM.getFlexAttributeDefinitionList());
		pageContext.setAttribute("flex", flexAttributeDefns);
		List<AttributeDefn> attributeDefns = new ArrayList<AttributeDefn>(
		AdditionalAttributeManager.ITEM_AVL.getAdditionalAttributeDefinitionList());
		Collections.sort(attributeDefns, new Comparator<AttributeDefn>() {
			public int compare(AttributeDefn a1, AttributeDefn a2) {
				return a1.getName().compareToIgnoreCase(a2.getName());
			}
		});
		pageContext.setAttribute("attributeDefns", attributeDefns);
	%>
	<script>
		var jsonColumn =  '${itemSearchForm.columns}';
		var selectionType = 'none';
		var gridColumns = [];
		<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.itemsearchpage.grid.thead')}"/>
		<c:forEach var="col" items="${columnHeader}" varStatus="count">
			<c:choose>
				<c:when test="${fn:trim(col) == 'item.platform' || fn:trim(col) == 'as.responsibility'}">
					gridColumns.push('<fmt:message key="${fn:trim(col)}"/>'+'_EXPANDCELL');
				</c:when>
				<c:otherwise>
					gridColumns.push('<fmt:message key="${fn:trim(col)}"/>');
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:if
		test="${e2ofn:hasAccess(appContext, 'FUNCTIONAL_GROUP' , 'Read')}">
		gridColumns.push('<fmt:message  key="item.functionalGroups"/>');
		gridColumns.push('<fmt:message  key="functionalGroup.parentName"/>');
		</c:if>
		var gridRows = [];
		<c:forEach var="row" items="${itemSearchForm.searchResult.values}" varStatus="rowCount">
		<c:set var="i" value="${row.values[0]}"/>
		<c:set var="be" value="${row.values[1]}"/>
		<c:set var="avl" value="${row.values[2]}"/>
		<c:set var="sup" value="${row.values[3]}"/>
		var itemNumber = '${i.itemNumber}';
		var businessEntityName = '${be.businessEntityName}';
		var itemType = '<fmt:message  key="item.type"/>';

		var row = {};
		var itemNumberLink = '<a href="#" onClick="openPopOver(\'${i.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${i.itemNumber}" removeColon="true"/></a>';
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
		row['<fmt:message  key="item.businessEntityIdentifier"/>'] = "<c:out value='${be.businessEntityIdentifier}'/>";
		row['<fmt:message  key="item.businessName"/>'] = "<c:out value='${be.businessEntityName}'/>";
		row['<fmt:message  key="item.itemRevision"/>'] = "<c:out value='${i.itemVersion.revision}'/>";
		row['<fmt:message  key="item.insertDate"/>'] = "<c:out value='${avl.insertDate}'/>";
		row['<fmt:message  key="item.updateDate"/>'] = "<c:out value='${avl.updateDate}'/>";
		var catNames = [];
		<c:forEach var="cat" items="${i.categories}" varStatus="ccount">
		var catName = "<c:out value='${cat.categoryName}'/>";
		catNames.push(catName);
		</c:forEach>
		//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
		row['<fmt:message  key="item.categoryName"/>'] = catNames.join(',');

		var platFormNames = [];
		<c:forEach var="p" items="${i.platforms}" varStatus="pcount">
		var platformName = "<c:out value='${p.platformName}'/>";
		platFormNames.push(platformName);
		</c:forEach>
		row['<fmt:message  key="item.platform"/>'] = platFormNames.join(',');
		row['<fmt:message  key="item.itemClassification"/>'] = "<c:out value='${i.itemClassification}'/>";
		row['<fmt:message  key="item.itemProductFamily"/>'] = "<c:out value='${i.productFamily}'/>";

		var responsibilities = [];
		<c:forEach var="ia" items="${i.assignments}" varStatus="iacount">
		var responsiblity = "${ia.userId} - ${ia.responsibility}";
		var region = '${ia.region}';
		if (region) {
			responsiblity = responsiblity + '- ' + region;
		}
		responsibilities.push(responsiblity);
		</c:forEach>
		row['<fmt:message  key="as.responsibility"/>'] = responsibilities.join(";");
		row['<fmt:message  key="item.dataSource"/>'] = "<c:out value='${i.dataSource}'/>";

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
		row['<fmt:message  key="item.supName"/>'] = '${avl.supplier.businessEntityName}';

		var supplierSiteNames = [];
		<c:forEach var="site" items="${avl.supplierSites}" varStatus="scount">
		var supplierSiteName = '${site.siteName}'
		supplierSiteNames.push(supplierSiteName);
		</c:forEach>
		row['<fmt:message  key="item.supplierSite"/>'] = supplierSiteNames
				.join(',');

		row['<fmt:message  key="item.supplierItemNumber"/>'] = '${avl.supplierItem.itemNumber}';
		var eolState = '${!empty i.eolType? i.eolType :"ACTIVE"}';
        row['<fmt:message  key="item.eol.state"/>'] = eolState;
        var topLevel = "No";
		<c:if test="${!empty i.isTopLevel && i.isTopLevel}">
			topLevel = "Yes";
		</c:if>
		row['<fmt:message  key="item.isTopLevel"/>'] = topLevel;
		<c:forEach var="attr" items="${attributeDefns}" varStatus="count">
			var name="${attr.name}";
			var value="${avl.getAttribute(attr.name)}";
			row[name]= value;
		</c:forEach> 
		gridRows.push(row);
		</c:forEach>
	</script>

	<form name ="itemSearchForm" action="submitItemSearch" method="POST">
		<e2ot:searchContainerControl form="${itemSearchForm}"
			searchFields="${itemSearchForm.allParameters}"
			formName="itemSearchForm" resultTableId="itemSearchResultTable"
			showFilterCollapsed="${itemSearchForm.filterAreaCollapsed}"
			showFilter="${itemSearchForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${itemSearchForm}"
			formName="itemSearchForm" resultTableId="itemSearchResultTable"
			showOrderMenu="false" showHideMenu="false" title="Items AVL"
			showTitle="true" />
	<form>
</body>
</html>