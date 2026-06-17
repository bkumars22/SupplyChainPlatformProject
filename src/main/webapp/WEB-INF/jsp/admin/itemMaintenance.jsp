<%@page import="java.util.Comparator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Collection"%>
<%@page import="com.scplatform.pcm.common.entity.AttributeDefn"%>
<%@page import="com.scplatform.pcm.common.entity.AdditionalAttributeManager"%>
<%@page import="com.scplatform.pcm.item.entity.Item"%>
<%@page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="../common.jspf"%>

<e2i2:doctype/> 
<e2i2:skin/> 
<e2i2:preferences/> 
<e2i2:clientcache/> 

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true"/>
<e2ot:help contextName="Admin-Item"/>
</head>
<script>
var fDataChanged = false; 
var deleteAvlCount = ${fn:length(itemMaintenanceForm.selectedAvls)};

function callOnChange(field){
	if(field.name=='value(owner)'){
		if(field.value=='REGIONAL'){
			var regionsElement = document.getElementsByName("values(regions)")[0];
			if(regionsElement) {
				regionsElement.disabled = false;
			}
		}else{
			hideElement();
		}
	}
}

function hideElement(){
	var ownerElement = document.getElementsByName("value(owner)")[0];
	var regionsElement = document.getElementsByName("values(regions)")[0];

	if(ownerElement && regionsElement) {
		if(ownerElement.value!='REGIONAL'){
			regionsElement.disabled = true;
		}
	}
}

function init ()
{
	hideElement();
    document.body.style.cursor='wait';
    i2uiCollapseTreeTable('amlDetails',1,null,0,true);           
    document.body.style.cursor='default';    
}

function canChangePage(op,callback)
{
   var fr = partial(canChangePageCallback,callback);
   var rc = handleSelectionChange(null,fr);
   if (rc == true)
   {
	   canChangePageCallback(callback);
   }
   return rc;
}

function canChangePageCallback(callback)
{
	var button = document.getElementById('${itemMaintenanceForm.selectedItemKey}');   
    if (button != null)
    {
       button.checked = false;
    }
    callback.apply();
}

function deleteAvlChecked(cb)
{
	deleteAvlCount += (cb.checked) ? 1 : -1;
}

function dataChanged()
{
   fDataChanged = true;
}

function handleSelectionChange(button,yesCallback)
{
    if (fDataChanged)
    {
    	var noCallback = partial(handleSelectionChangeNoCallback,button);
       	showYesNoMessageBox('YES NO','WARN',
			   "<fmt:message key='warn.changes_not_saved_yes_no'/>",
			   "<fmt:message key='msg.warn'/>", yesCallback, noCallback);
    	return false;
    }
    return true;
}

function handleSelectionChangeNoCallback(button)
{
	if (button != null)
    {
       button.checked = false;
       var old = document.getElementById('${itemMaintenanceForm.selectedItemKey}');
       if (old != null)
       {
          old.checked = true;
       }
    }
}

function goSelectItem(item)
{
	   document.forms[0].selectedItemKey.value =item;
	   document.forms[0].action="viewItemMaintenanceDetails.do";
	   document.forms[0].submit();
	   showWaitBusy();
}

function goSelectItemCallback(button)
{
   document.forms[0].selectedTabId.value='itemDetailTab';
   document.forms[0].selectedItemKey.value = button.value;
   document.forms[0].action="viewItemMaintenanceDetails.do";
   document.forms[0].submit();
   showWaitBusy();
}

function goDrilldown(key)
{
   var fr = partial(goDrilldownCallback,key);
   if (handleSelectionChange(null,fr))
   { 
	   goDrilldownCallback(key);
   }
}

function goDrilldownCallback(key)
{
   document.forms[0].selectedTabId.value='itemDetailTab';
   document.forms[0].drilldownItemKey.value = key;
   document.forms[0].action="drilldownItemMaintenanceDetails.do";
   document.forms[0].submit();
   showWaitBusy();
}

function goPopup()
{
   if (handleSelectionChange(null,goPopupCallback))
   {
	   goPopupCallback();
   }
}

function goPopupCallback()
{
   document.forms[0].action="popupItemMaintenanceDetails.do";
   document.forms[0].submit();
}

function goChangeTab(id)
{
	if (id == 'itemDetailTab')
	{
		$('#itemDetailTab').show();
		$('#resultTab').hide();
	}
	else
	{
		$('#itemDetailTab').hide();	
		$('#resultTab').show();	
	}	
	document.forms[0].selectedTabId.value=id;	
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
<fmt:message var="searchTypeLabel"  key="search.type.items" scope="page"/>
<fmt:message var="resultTitle"  key="label.results" scope="page"/>
<form name="itemMaintenanceForm" action="submitItemMaintenanceSearch" method="POST">
<input type="hidden" name="backAction" id="backAction">
<script>
	var gridColumns = [];
	var jsonColumn =  '${itemMaintenanceForm.columns}';
	var selectionType = 'none';
	<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.itemMaintenance.grid.thead')}"/>
	<c:forEach var="col" items="${columnHeader}" varStatus="count">
	    if(${col == 'item.platform' || col == 'as.responsibility'}){
	        gridColumns.push('<fmt:message key="${col}"/>'+'_EXPANDCELL');
	    } else if(${col == 'item.version.revision'}) {
	        gridColumns.push('<fmt:message key="item.version"/>/<fmt:message key="item.revision"/>');
	    } else {
	        gridColumns.push('<fmt:message key="${col}"/>');
	    }
	</c:forEach>

	var gridRows = [];
	<c:forEach var="row" items="${itemMaintenanceForm.searchResult.values}"> 
	  var row = {};
	 <c:set var="item" value="${row.values[0]}"/>
	 var itemlink = '<a href=\'javascript:goSelectItem(${item.itemKey})\'>${item.itemNumber}</a>';
	  row['<fmt:message key="item.itemNumber"/>'] = itemlink;
	  var itemTypeVal = '${item.itemType}';
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
		row['<fmt:message  key="item.itemDescription"/>'] = "<c:out value='${item.description}'/>";
		row['<fmt:message  key="item.businessEntityIdentifier"/>'] = "<c:out value='${item.businessEntity.businessEntityIdentifier}'/>";
		row['<fmt:message  key="item.businessName"/>'] = "<c:out value='${be.businessEntityName}'/>";
		row['<fmt:message key="item.version"/>/<fmt:message key="item.revision"/>'] = "<c:out value='${item.itemVersion.revision}'/>";
		row['<fmt:message key="item.version"/>'] = "<c:out value='${item.itemVersion.version}'/>";
		row['<fmt:message key="item.revision"/>'] = "<c:out value='${item.itemVersion.revision}'/>";
		var catNames = [];
		<c:forEach var="cat" items="${item.categories}" varStatus="ccount">
		var catName = "<c:out value='${cat.categoryName}'/>";
		catNames.push(catName);
		</c:forEach>
		//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
		row['<fmt:message  key="item.categoryName"/>'] = catNames.join(',');
		row['<fmt:message key="item.businessName"/>'] = '<c:out value="${item.businessEntity.businessEntityName}"/>';
		
		var fgNames = [];
		<c:forEach var="fg" items="${item.functionalGroups}" varStatus="fgcount">
		var fgName = "<c:out value='${fg.name}'/>";
		fgNames.push(fgName);
		</c:forEach>
		 row['<fmt:message key="functionalGroup.functionalGroupName" />'] = fgNames.join(',');
		 
		 var parentFunctionalGroupNames = [];
			<c:forEach var="fgs" items="${item.functionalGroups}" varStatus="count">
			<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}">
			var parentFunctionalGroupName = "<c:out value='${pfg.name}'/>";
			parentFunctionalGroupNames.push(parentFunctionalGroupName);
			</c:forEach>
			</c:forEach>
			row['<fmt:message  key="functionalGroup.parentName"/>'] = parentFunctionalGroupNames
					.join(',');
		var responsibilities = [];
		<c:forEach var="ia" items="${item.assignments}" varStatus="iacount">
		var responsiblity = "<c:out value='${ia.userId} - ${ia.responsibility}'/>";
		var region = "<c:out value='${ia.region}'/>";
		if (region) {
			responsiblity = responsiblity + '- ' + region;
		}
		responsibilities.push(responsiblity);
		</c:forEach>
		row['<fmt:message  key="as.responsibility"/>'] = responsibilities.join(';'); 

		var platFormNames = [];
		<c:forEach var="p" items="${item.platforms}" varStatus="pcount">
			var platformName = "<c:out value=' ${p.platformName} '/>";
			platFormNames.push(platformName);
		</c:forEach>
		row['<fmt:message  key="item.platform"/>'] = platFormNames.join(',');
		row['<fmt:message  key="item.dataSource"/>'] = "<c:out value='${item.dataSource}'/>";
		row['<fmt:message  key="item.itemClassification"/>'] = "<c:out value='${item.itemClassification}'/>";
		row['<fmt:message  key="item.itemProductFamily"/>'] = "<c:out value='${item.productFamily}'/>";
		var eolState = '${!empty item.eolType? item.eolType :"ACTIVE"}';
		row['<fmt:message  key="item.eol.state"/>'] = eolState;

		var topLevel = "No";
		<c:if test="${!empty item.isTopLevel && item.isTopLevel}">
		    topLevel = "Yes";
		</c:if>
		row['<fmt:message  key="item.isTopLevel"/>'] = topLevel;
		row['<fmt:message  key="item.insertDate"/>'] = "<c:out value='${item.insertDate}'/>";
		row['<fmt:message  key="item.updateDate"/>'] = "<c:out value='${item.updateDate}'/>";
		row['<fmt:message  key="item.managedFlag"/>'] = "<c:out value='${item.managedFlag}'/>";

		<c:forEach var="fl" items="${flex}" varStatus="count">
			<fmt:message key="flex.item.${fl.associatedAttribute}" var="flexAttr"/>
			row['${flexAttr}'] = "<c:out value='${item[fl.associatedAttribute]}'/>";
		</c:forEach>
		<c:forEach var="attr" items="${attributeDefns}" varStatus="count">
		     var name="${attr.name}";
		     var value="${item.getAttribute(attr.name)}";
		     row[name]= value;
	    </c:forEach>
	gridRows.push(row);
	</c:forEach>
</script>
<c:set var="viewOnly" value="${!e2ofn:hasAccess(appContext, 'ADMIN', 'SaveBusiness')}"/>
<c:set var="activeTab" value="${itemMaintenanceForm.selectedTabId}"/>
<c:if test="${empty itemMaintenanceForm.selectedItem}">
<c:set var="activeTab" value="resultTab"/>
</c:if>

<input type="hidden" name="selectedTabId"/>
<input type="hidden" name="drilldownItemKey"/>
<input type="hidden" name="selectedItemKey"/>
<e2ot:searchContainerControl searchFields="${itemMaintenanceForm.allParameters}" 
   formName="itemMaintenanceForm" form="${itemMaintenanceForm}"
   showFilterCollapsed="${itemMaintenanceForm.filterAreaCollapsed}"
   showFilter="${itemMaintenanceForm.showFilterArea}" numColumns="3"/>
 <e2ot:searchResultsControl searchForm="${itemMaintenanceForm}" 
   showTitle="false"	
   formName="itemMaintenanceForm" title="Manage Item" />
   
</form>
</body>
</html>
