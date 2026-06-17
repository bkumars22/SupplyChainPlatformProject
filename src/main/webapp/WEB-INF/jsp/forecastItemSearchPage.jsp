<%@ include file="common.jspf"%>

<e2i2:doctype/> 
<e2i2:skin/> 
<e2i2:preferences/> 
<e2i2:clientcache enabled="false"/> 


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true"/>
<e2ot:help contextName="Forecast-Create"/>
</head>
<script>

function callOnChange(field){
	if(field.name=='value(owner)'){
		var regions = document.getElementsByName("values(regions)")[0];
		if(field.value=='REGIONAL'){
			if(regions) regions.disabled = false;
		}else{
			hideElement();
		}
	}
}

function hideElement(){
	var owner = document.getElementsByName("value(owner)")[0];
	var regions = document.getElementsByName("values(regions)")[0];
	if(owner && regions && owner.value!='REGIONAL'){
		regions.disabled = true;
	}
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
	if ($('input:checked[name="selectedPageKeys"]').length == 0)
	{
		showOkMessageBox('OK','WARN',"<fmt:message key='warn.nothingSelected'/>","<fmt:message key='msg.warn'/>",null);
		return;		
	}
	 	formName.action=action;
	    formName.buttonAction.value="next";
	    formName.backAction.value="submitForecastItemSearch";
	    formName.submit();
		showWaitBusy();   
}


function init()
{
	hideElement();
	resizeResultArea();
	setupHideColumnState('forecastItemSearchResultTable',true);
	setupOrderColumnState('forecastItemSearchResultTable',true,
			{mouseOverTitle:'<fmt:message key="label.moveColumn"/>'});
	
}
</script>
<body onload="init()">
<form name="forecastItemSearchForm" id="forecastItemSearchForm" action="submitForecastItemSearch" method="POST">
<input type="hidden" name="backAction"/>
<input type="hidden" name="buttonAction" id="buttonAction"/>
<script type="text/javascript">
   var gridColumns = [];
   gridColumns.push('<fmt:message key="item.itemNumber"/>');
   gridColumns.push('<fmt:message key="as.responsibility"/>'+'_EXPANDCELL');
   gridColumns.push('<fmt:message key="item.functionalGroups" />');
   gridColumns.push('<fmt:message key="functionalGroup.parentName" />')
   gridColumns.push('<fmt:message key="item.categoryName"/>');
   gridColumns.push('<fmt:message key="item.platform"/>');
   gridColumns.push('<fmt:message key="item.classification"/>');
   gridColumns.push('<fmt:message key="item.itemProductFamily"/>');
   gridColumns.push('<fmt:message key="item.itemDescription"/>');
   gridColumns.push('<fmt:message key="item.dataSource"/>');
   
   var gridRows = [];
   
   <c:forEach var="row" items="${forecastItemSearchForm.searchResult.values}" varStatus="rowCount">
   <c:set var="im" value="${row.values[0]}"/>
	   var row = {};
	row['checkboxValue'] = '<c:out value="${im.itemKey}"/>';
	var itemNumberLink = '<a href="#" onClick="openPopOver(\'${im.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${im.itemNumber}" removeColon="true"/></a>';
	row['<fmt:message key="item.itemNumber"/>'] = itemNumberLink;
	
		  var responsibilities = [];
		<c:forEach var="ia" items="${im.assignments}" varStatus="iacount">
		var responsiblity = "${ia.userId} - ${ia.responsibility}";
		responsibilities.push(responsiblity);
		</c:forEach>
		row['<fmt:message key="as.responsibility" />'] = responsibilities.join(";");  
		
	var functionalGroupNames = [];
	<c:forEach var="fgs" items="${im.functionalGroups}" varStatus="count">
	var functionalGroupName = '${fgs.name}';
	functionalGroupNames.push(functionalGroupName);
	</c:forEach>
	row['<fmt:message  key="item.functionalGroups"/>'] = functionalGroupNames.join(',');
	
	var parentFunctionalGroupNames = [];
	<c:forEach var="fgs" items="${im.functionalGroups}" varStatus="count">
	<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}">
	var parentFunctionalGroupName = "<c:out value='${pfg.name}'/>";
	parentFunctionalGroupNames.push(parentFunctionalGroupName);
	</c:forEach>
	</c:forEach>
	row['<fmt:message  key="functionalGroup.parentName"/>'] = parentFunctionalGroupNames.join(',');
		 
	//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
	  var catNames = [];
	<c:forEach var="cat" items="${im.categories}" varStatus="ccount">
	var catName = '${cat.categoryName}';
	catNames.push(catName);
	</c:forEach>  
	//Check if reach limit of categories , then display less with 3 dots using EDL feature TODO
	row['<fmt:message  key="item.categoryName"/>'] = catNames.join(',');
	/* var platFormNames = [];
	<c:forEach var="p" items="${im.platforms}" varStatus="pcount">
	var platformName = '${p.platformName}';
	platFormNames.push(platformName);
	</c:forEach>
	row['<fmt:message  key="item.platform"/>'] = platFormNames.join(','); */
	row['<fmt:message key="item.classification"/>'] = '<c:out value="${im.itemClassification}"/>';
	row['<fmt:message key="item.itemProductFamily"/>'] = '<c:out value="${im.productFamily}"/>';
	row['<fmt:message key="item.itemDescription"/>'] = '<c:out value="${im.description}"/>';
	row['<fmt:message key="item.dataSource"/>'] = '<c:out value="${im.dataSource}"/>';
	gridRows.push(row);
   </c:forEach>
   </script>
<e2ot:searchContainerControl searchFields="${forecastItemSearchForm.allParameters}" 
   formName="forecastItemSearchForm" form="${forecastItemSearchForm}" resultTableId="forecastItemSearchResultTable"
   showFilterCollapsed="${forecastItemSearchForm.filterAreaCollapsed}"
   showFilter="${forecastItemSearchForm.showFilterArea}" numColumns="3" />
<e2ot:searchResultsControl searchForm="${forecastItemSearchForm}"    
   formName="forecastItemSearchForm" resultTableId="grid-result"
   showOrderMenu="true" showHideMenu="true" title="New Forecast" />
<script type="text/javascript">
		function goCancelCallback() {
			document.forms[0].action = "home";
			document.forms[0].submit();
		}
	<c:if test="${not empty forecastItemSearchForm.searchResult.values}">
		var footerButton = document.getElementById("searchFiledsButtons");
		var buttons = "";
			  <c:if test="${!empty forecastItemSearchForm.previousAction}">
				  buttons += '<button type="button" class="eto-btn" id="backButton" onclick="javascript:goBack(document.forms[\'forecastItemSearchForm\'],\'${fn:escapeXml(forecastItemSearchForm.previousAction)}\')">'
			  			+'<bean:message key="button.back" /></button>'
			  </c:if>
			  <c:if test="${!empty forecastItemSearchForm.nextAction}">
			  buttons += '<button type="button" class="eto-btn" id="nextButton" onclick="javascript:goNext(document.forms[\'forecastItemSearchForm\'],\'${fn:escapeXml(forecastItemSearchForm.nextAction)}\');">'
						+'<bean:message key="button.next" /></button>'
			  </c:if> 
			buttons += '<button type="button" class="eto-btn" id="cancelButton" onclick="goCancelCallback();"><fmt:message key="button.cancel" /></button>';
			if(buttons!=null && buttons!="")
			footerButton.innerHTML = buttons; 
			</c:if>
</script>
</form>
</body>
</html>