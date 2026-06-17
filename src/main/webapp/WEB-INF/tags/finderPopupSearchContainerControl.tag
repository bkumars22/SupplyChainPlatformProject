<%@tag%>
<%@ attribute name="formName" required="true"%>
<%@ attribute name="numColumns" required="true"%>
<%@ attribute name="showFilter" required="true"%>
<%@ attribute name="resultTableId" required="false"%>
<%@ attribute name="showSaveFilter" required="false"%>
<%@ attribute name="showFilterCollapsed" required="true"%>
<%@ attribute name="searchFields" required="true"
	type="java.util.Collection"%>
<%@ attribute name="form" required="true"
	type="com.scplatform.pcm.searchframework.dto.SearchForm"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="e2ot"%>
<%@ taglib uri="/WEB-INF/i2/e2o.tld" prefix="e2o"%>
<c:set var='moreflagfilter' value='${e2ofn:getConfigValue("scplatform.search.more")}' />
<c:set var='lessFilterCount' value='${e2ofn:getConfigValue("scplatform.search.field.less.count")}' />
<style type="text/css">
table#edit-filters {
	width: 100% !important;
	table-layout:auto !important;
}

.other-option {
	color: #277ab5;
	padding-top: 3px;
}
</style>
<fmt:setBundle basename="scplatform-messages" />

<c:if test="${empty showSaveFilter}">
	<c:set var="showSaveFilter" value="true" />
</c:if>
<c:if test="${empty resultTableId}">
	<c:set var="resultTableId" value="searchResultTable" />
</c:if>

<script>
  var searchContainerControlScripts = '${searchContainerControlScripts}';  
  var popObj = null;
</script>
<c:if test="${empty searchContainerControlScripts}">
	<c:set var="searchContainerControlScripts" value="true" scope="request" />
	<script>
	var temp;
	var status = "less";
	var modalObj;
	function toggleText()
	{	    
	    if (status == "less") {
	    	$(".secondRow").css('display', '');
	        document.getElementById("toggleButton").innerText = "Less Options";
	        status = "more";
	    } else if (status == "more") {
	    	$(".secondRow").css('display', 'none');
	        document.getElementById("toggleButton").innerText = "More Options";
	        status = "less"
	    }
	}
/* function onFinderMultiTextCallback(idx,finderValues)
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
} */
 
  

function clearfilterValues()
{  
   var fields = $('#eto-well :input');
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
      if (fields[idx].id != null && fields[idx].type == 'select-multiple' && fields[idx].name != null &&
        	  fields[idx].name.indexOf('values') == 0)
          {
    	  var name = fields[idx].name;
    	  name = name.substring(7,name.length-1);
    	  var _options_legth = parent.parent.mcmApp.searchFields[name].options.length;
			for(var i = 0 ; i < _options_legth ; i++ ){
				$('#eto-well span.eto-tag__remove').each(function() {
					$( this ).trigger( "click" );
				});
			}
        }
   }
   
   $('#eto-well span.eto-tag__remove').each(function() {
	    $( this ).trigger( "click" );
   });
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

function goSearch(formName)
{
   formName.pageStartAt.value=0;
   formName.submit();
   showSearchBusy();
}

 function handlePaste(event, idx)
 {
	<c:set var="delim" value="${e2ofn:getConfigValue('pcm.web.search.multiValueDelimiter')}" />
	<c:if test="${empty delim}">
		<c:set var="delim" value="," />
	</c:if>
	var item = document.getElementById('searchField'+idx);
	$('#searchField'+idx).css('overflow','hidden');
	var keynum;
	if(window.event) // IE
	{
	  keynum = window.event.keyCode;
	}
	else if(event.which) // Netscape/Firefox/Opera
	{
	  keynum = event.which;
	}
	
	if (keynum == 86 && event.ctrlKey === true)
	{	
		var values = item.value.split(/\t+|\r+|\n+/);
		if(values.length > 0)
		{
			item.value = "";
			for (var i=0; i < values.length; i++)
			{
				if(!isValueEmpty(item.value))
				{
					if(!isValueEmpty(values[i]))
					{
						item.value = item.value.concat('${delim}'+values[i]);
					}
				}
				else
				{
					item.value = values[i];
				}
			}
		}
				
	}
 }


// Bind our handler to the toggle function
i2uiToggleContentUserFunction = 'handleSearchContainerToggle';     

function handleFilterFieldChanged(field)
{        
    document.forms['${formName}'].searchParametersChanged.value = true;
    callOnChange(field);
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


 
</script>
</c:if>
<input type="hidden" name="fileName" /> 
<input type="hidden" name="operation" />
<input type="hidden" name="clearSelection" value="false" />
<input type="hidden" name="searchParametersChanged" value="false" />
<!-- MM 04 -->
			<div class="eto-well" id="eto-well" style="padding:2rem !important;">
				<div>
							<div class="row" id="firstRow">
								<c:set var="rowCount" value="0" />
								<c:forEach var="field" items="${searchFields}" varStatus="fieldCount">
									<c:set var="rowCount" value="${rowCount + 1}" />
									<c:choose>
										<c:when test="${rowCount > numColumns || field.properties['newLine'] == true}">
											<c:choose>
												<c:when test="${moreflagfilter == true}">
													<div class="col-xs-12 col-sm-3 col-md-2 col-lg-2 col-xl-2 secondRow" style="display: none">
														<script type="text/javascript">
															$('#toggleButton').css('display','block');
														</script>
														<e2ot:searchField field="${field}"	fieldCount="${fieldCount.count}" autoSubmitEnabled="true" />
													</div>
												</c:when>
												<c:otherwise>
													<div class=" col-xs-12 col-sm-3 col-md-2 col-lg-2 col-xl-2">
													<e2ot:searchField field="${field}" fieldCount="${fieldCount.count}" autoSubmitEnabled="true" />
													</div>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<div class=" col-xs-12 col-sm-3 col-md-2 col-lg-2 col-xl-2">
											<e2ot:searchField field="${field}" fieldCount="${fieldCount.count}" autoSubmitEnabled="true" />
											</div>
										</c:otherwise>
									</c:choose>
							</c:forEach>
					</div>
				<div class="row margin-top-xs-2">
					<div class="col-xs-12 margin-top-sm-2">
						<button type="submit" onclick="javascript:goSearch(document.searchFinderForm)"
							class="eto-btn eto-btn--primary margin-right-xs-1">
							<fmt:message key="button.search" />
						</button>
						<button id="ClearButton"
							onclick="javascript:clearfilterValues(document.searchFinderForm)" type="button"
							class="eto-btn">
							<fmt:message key="button.clear" />
						</button>

					</div>
				</div>
			</div>
 </div>
