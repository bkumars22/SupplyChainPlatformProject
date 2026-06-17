<%@tag%>
<%@ attribute name="formName" required="true"%>
<%@ attribute name="numColumns" required="true"%>
<%@ attribute name="showFilter" required="true"%>
<%@ attribute name="resultTableId" required="false"%>
<%@ attribute name="showSaveFilter" required="false"%>
<%@ attribute name="showFilterCollapsed" required="true"%>
<%@ attribute name="searchFields" required="true" type="java.util.Collection"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="e2ot" %>
<fmt:setBundle basename="scplatform-messages" />

<c:if test="${empty showSaveFilter}"><c:set var="showSaveFilter" value="true"/></c:if>
<c:if test="${empty resultTableId}"><c:set var="resultTableId" value="searchResultTable"/></c:if>

<c:if test="${empty searchContainerControlScripts}">
<c:set var="searchContainerControlScripts" value="true" scope="request"/>
<script>
function onMultiTextCallback(idx,finderValues)
{
	<c:set var="delim" value="${e2ofn:getConfigValue('pcm.web.search.multiValueDelimiter')}" />
	<c:if test="${empty delim}">
		<c:set var="delim" value="," />
	</c:if>
	var item = document.getElementById('searchField'+idx);
	for (var i=0; i < finderValues.length; i++)
	{
		if(!isValueEmpty(item.value))
		{
			item.value = item.value.concat('${delim}'+finderValues[i][1]);
		}
		else
		{
			item.value = finderValues[i][1];
		}
	}
}

function submitSaveFilter()
{
    document.body.style.cursor='wait';
	document.forms['${formName}'].operation.value='saveFilter';
	document.forms['${formName}'].submit();
	showBusy();	
}

function submitDeleteFilter()
{
    document.body.style.cursor='wait';
	document.forms['${formName}'].operation.value='deleteFilter';
	document.forms['${formName}'].submit();
	showBusy();	
}

function submitLoadFilter()
{    
	document.forms['${formName}'].clearSelection.value = "true";    
	document.forms['${formName}'].operation.value='loadFilterAndSearch';
	document.forms['${formName}'].submit();
    showSearchBusy();
}

function submitExtractToFile()
{
	document.forms['${formName}'].operation.value='extractToFile';
	document.forms['${formName}'].submit();
	//Because we stream data vs return a HTML response, reset the operation
	document.forms['${formName}'].operation.value=null;	
}

function handleFilterChange(key, value)
{
    if (key != '')
    {
	    document.forms['${formName}'].selectedFilterName.value = value;    
    	submitLoadFilter();
    }
    else
    {
		document.forms['${formName}'].selectedFilterName.value = '';    
    }
}

function clearSearchCriteria()
{  
   var fields = $('#searchContainer :input');
   for (idx=0; idx < fields.length; idx++)
   {
      if (fields[idx].id != null && 
    	  fields[idx].id.indexOf('searchField') == 0)
      {
         clearField(fields[idx]);
         if (fields[idx].autocompleter != null)
         {
        	 fields[idx].autocompleter.flushCache();
         }
      }
   }
   if (document.forms[0].selectedFilter != undefined)
   {
   	   document.forms[0].selectedFilter.selectedIndex = 0;
       document.forms[0].selectedFilterName.value = '';
   }   
}


//Used to initially collapse area
function collapseFilterArea()
{
  i2uiCollapseContainer('searchContainer');
}

// This is the callback pointer
var resizeResultAreaCallback;

function resizeResultArea()
{
   var table = document.getElementById('${resultTableId}');
   if (table == null)
   {
      return;
   }
   
   var h = 0;
   if (document.getElementById('searchContainer') != null)
   { 
   	   h = document.getElementById('searchContainer').clientHeight;
   }
     
   h = getBodyHeight() - h; 
   if (resizeResultAreaCallback != undefined)
   {
   	   h = resizeResultAreaCallback(h);
   } 
   <c:if test="${fn:contains(header['USER-AGENT'],'MSIE') == false}">
   if (document.getElementById('${resultTableId}_data'))
   {
	   table = document.getElementById('${resultTableId}_data');
   }
   table.tBodies[0].style.height = (h-140) + 'px';
   </c:if>
   <c:if test="${fn:contains(header['USER-AGENT'],'MSIE') == true}">   
   table.style.height=(h - 90) + 'px';
   </c:if> 
}

function handleSearchContainerToggle(item, delta)
{
   if (item == 'searchContainer')
   {
      resizeResultArea();
      document.forms['${formName}'].filterAreaCollapsed.value = (delta < 0);
   }
}

function checkForRequiredSearchFields()
{
	var field = null;
	<c:forEach var="field" items="${searchFields}" varStatus="fieldCount" >
	  <c:if test="${field.required}">
	  field = document.forms['${formName}'].searchField${fieldCount.count};
      if (isFieldValueEmpty(field))
      { 
          return field;
      }
	  </c:if>
	</c:forEach>
	return null;	
}

function handleAutoSubmit(event)
{
	var keynum;
	if(window.event) // IE
	{
	  keynum = window.event.keyCode;
	}
	else if(event.which) // Netscape/Firefox/Opera
	{
	  keynum = event.which;
	}
	
	if (keynum == 13)
	{	
		goSubmitSearch();
	}	
}

// Bind our handler to the toggle function
i2uiToggleContentUserFunction = 'handleSearchContainerToggle';     

function handleFilterFieldChanged(field)
{        
    document.forms['${formName}'].searchParametersChanged.value=true;	
}


function clearAllSortFields(targetHeader)
{
    if (targetHeader == null || targetHeader.length == 0)
    {
        return;
    }    
	var fields = $('.orderByField',targetHeader);	
    for (idx=0; idx < fields.length; idx++)
    {
	   document.forms['${formName}'].elements[fields[idx].name].value = 'NOTSET';
    }	
    goSubmitSortSearch();
}

function setSortField(fieldName,required)
{
    if (required == null)
    {
       required = false;
    }
    var field = document.forms['${formName}'].elements[fieldName];

    if (field.value == 'ASC')
    {
        field.value = 'DESC';
    }
    else if (field.value == 'DESC' && required == false)
    {
        field.value = 'NOTSET';
    }
    else
    {
        field.value = 'ASC';
    }	
}

function goSubmitSortSearch(fieldName,required)
{
	if (fieldName != null)
	{
		setSortField(fieldName,required);
	}
    document.body.style.cursor='wait';
    <c:if test="${searchForm.pagingEnabled}">    
    document.forms['${formName}'].pageStartAt.value=0;
    </c:if>
    document.forms['${formName}'].submit();
    showSearchBusy();    
}


function goSubmitSearch()
{
	var errorField = checkForRequiredSearchFields()
	if (errorField != null)
	{
		showOkMessageBox('OK','WARN',
                "<fmt:message key='errors.field_required'/>",
                "<fmt:message key='msg.warn'/>",function() {
                    errorField.focus();                     
                });
		return;
	}
   document.forms['${formName}'].clearSelection.value = "true";       
   document.forms['${formName}'].submit();
   showSearchBusy();   
}
<c:forEach var="field" items="${searchFields}" varStatus="fieldCount" >
   <c:if test="${!empty field.finderName}"> 
   $(document).ready(function() 
			{
				$("#searchField${fieldCount.count}").autocomplete(
					"ajaxQuery${field.finderName}",
					{
						delay:800,
						minChars:1,
						matchSubset:0,
						maxItemsToShow:20,						
						matchContains:0,
						cacheLength:1,
						extraParams:{${field.properties['finderParams']}},
						autoFill:false
					}
				);
			});   
   </c:if>
</c:forEach>
</script>
</c:if>
<input type="hidden" name="operation" />
<input type="hidden" name="clearSelection" value="false"/>  
<input type="hidden" name="searchParametersChanged" value="false"/>
<c:choose>
<c:when test="${showFilter}">
<fmt:message var="title" key="search.filter.title"/>
<e2i2:container id="searchContainer" title="${title}" collapsable="yes">
<e2i2:header>
<c:if test="${showSaveFilter}">
  <table style="font-weight:normal">
    <tr>
    <td align="right" nowrap >
      <fmt:message key="label.availableFilters"/>
    </td>
    <td>
      <html:select styleId="searchFilter" onchange="javascript:handleFilterChange(this.options[this.selectedIndex].value, this.options[this.selectedIndex].text)"
             style="width:150" styleClass="inputField"
             property='selectedFilter'>
         <html:optionsCollection property="availableFilters" label="value" value="key"/>
      </html:select>
    </td>
    <td><html:text style="width:100px;" styleClass="inputField" property="selectedFilterName"/></td>
    <td>
       <e2i2:button small="yes" id="saveFilterButton" onclick="javascript:submitSaveFilter()"><fmt:message key="button.save"/></e2i2:button>
    </td>
    <td>   
       <e2i2:button small="yes" id="deleteFilterButton" onclick="javascript:submitDeleteFilter()"><fmt:message key="button.delete"/></e2i2:button>   
    </td>
    </tr>
  </table>
</c:if>
</e2i2:header>
<e2o:errors/>
<e2i2:instructionsarea>
<fmt:message key="search.instructions"/>
</e2i2:instructionsarea>
<table class="searchContainerArea" cellpadding="1" cellspacing="0">
<tr>
<c:set var="rowCount" value="0"/>
<c:forEach var="field" items="${searchFields}" varStatus="fieldCount" >
  <c:set var="rowCount" value="${rowCount + 1}"/>
  <c:if test="${rowCount > numColumns || field.properties['newLine'] == true}">
    </tr><tr>
   <c:set var="rowCount" value="1"/>    
  </c:if>
  <e2ot:searchField field="${field}" fieldCount="${fieldCount.count}" autoSubmitEnabled="true"/>  
</c:forEach>
</tr>
</table>
<e2i2:buttonbar>
<e2i2:button id="searchClearButton" onclick="javascript:clearSearchCriteria()"><fmt:message key="button.clear"/></e2i2:button>
<%-- <c:if test="${extractEnabled}"> --%>
<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DownloadFile')}">
<e2i2:button onclick="javascript:submitExtractToFile()" small="yes">
<fmt:message key="button.download"/>
</e2i2:button>
</c:if>
<%-- </c:if> --%>
</e2i2:buttonbar>
</e2i2:container>
</c:when>
<c:otherwise>
<e2o:errors/>
</c:otherwise>
</c:choose>
<html:hidden property="filterAreaCollapsed"/>
<c:if test="${showFilterCollapsed}">
    <script>collapseFilterArea();</script>
</c:if>
