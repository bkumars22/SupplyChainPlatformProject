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
.complexSelect .eto-tag__label
{
text-decoration: none !important;
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
  <c:set var="allowableAsynhronusCallList" value="${e2ofn:getConfigValue('pcm.report.asynchronous.download.list')}"></c:set>
  var popObj = null;
</script>
<c:if test="${empty searchContainerControlScripts}">
	<c:set var="searchContainerControlScripts" value="true" scope="request" />
	<script>
	var temp;
	var status = "less";
	var modalObj;
	var downloadMenuOn;
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
function onMultiTextCallback(idx,finderValues)
{
	<c:set var="delim" value="${e2ofn:getConfigValue('pcm.web.search.multiValueDelimiter')}" />
	<c:if test="${empty delim}">
		<c:set var="delim" value="," />
	</c:if>
	var item = document.getElementById('searchField'+idx);
	var parentNodeId = item.parentNode.id;
	console.log(parentNodeId);
	for (var i=0; i < finderValues.length; i++)
	 {
		multiSelectItem[parentNodeId].push(finderValues[i][1]);
	 }
	 multiSelectItem[parentNodeId] = $.unique(multiSelectItem[parentNodeId]);
	 addElementTags(parentNodeId,multiSelectItem[parentNodeId]);
	 $("input[name='value["+parentNodeId+"]']").val(multiSelectItem[parentNodeId].join(";"));
	 getMultiSelectData(parentNodeId,multiSelectItem[parentNodeId].join(";"));
}

function submitSaveFilter()
{
	let selectedFiterValue = $('#savedFilter').val();
	if(selectedFiterValue == ''){
		saveFilterModal.open();
	}else{
		document.body.style.cursor='wait';
		document.forms['${formName}'].operation.value='saveFilter';
		document.forms['${formName}'].submit();
		showBusy();
	}
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

function refreshFilters()
{
	document.forms['${formName}'].clearSelection.value = "true";
	document.forms['${formName}'].operation.value='refreshFilters';
	document.forms['${formName}'].submit();
    showSearchBusy();
}
function submitExtractToFileForBe(filterType)
{
    $("#filterType").val(filterType);
    if(filterType == 'OnlyBE'){
        submitExtractToFile("Business_Entity");
    }else{
        submitExtractToFile("Full_Business_Entity");
    }
}
function submitExtractToFileForBom(filterType)
{
$("#filterType").val(filterType);
submitExtractToFile();
}
function submitExtractToFile(fileTitle)
{
	var checkFileCompletedHandler = null;
	var num = Math.floor((Math.random() * 1000));
	var fileLocation = "/scplatform/app/scplatform/config/templates/downloadhelp"+num;
	document.forms['${formName}'].operation.value='extractToFile';
	document.forms['${formName}'].fileName.value=fileLocation;
	if (typeof fileTitle !== 'undefined' && fileTitle !== null
			&& document.forms['${formName}'].extractFileName) {
		document.forms['${formName}'].extractFileName.value = fileTitle;
	}
	document.forms['${formName}'].submit();
	showWaitBusy();
	function checkFileCompleted(){
		$.ajax({
			type: "POST",
			url: 'downloadUtilCheck.do',
			cache: false,
			dataType: 'text',
			data: {
				fileLocation: fileLocation
			},
			success: function(result) {
				if(result.match("^true")){
					closeWaitBusy();
					clearInterval(checkFileCompletedHandler);
				}
			},
			error: function(error){
//				alert('error'+error.statusText);
				closeWaitBusy();
				clearInterval(checkFileCompletedHandler);
			}
		});
	}
	checkFileCompletedHandler = setInterval(function(){ checkFileCompleted() }, 1000);

	//Because we stream data vs return a HTML response, reset the operation
	document.forms['${formName}'].operation.value=null;
}

function handleFilterChange(key, value)
{
	if(key != 'manageFilter-value'){
    if (key != '')
    {
    	document.forms['${formName}'].selectedFilterName.value = value;
    	document.forms['${formName}'].selectedFilter.value = key;
    	submitLoadFilter();
    }
    else
    {
		document.forms['${formName}'].selectedFilterName.value = '';
    }
	}
	else {
		/* document.forms[0].selectedFilter.selectedIndex = 0; */
	    document.forms[0].selectedFilterName.value = '';
		modalObj = new eto.Modal({
            el : document.querySelector('#manage-filter-modal')
        });
		modalObj.open();
		modalObj.on('closed', function(query) {
			// refreshFilters();
			if(popObj != null)
					popObj.close();
			$('select[name="selectedFilter"] option:selected').removeAttr('selected');
		});
		}
}

function clearSearchCriteria()
{
   var fields = $('#expand-container :input');
   for (idx=0; idx < fields.length; idx++)
   {
	   if (fields[idx].id != null &&
    	  fields[idx].id.indexOf('searchField') == 0)
      {
         clearField(fields[idx]);
      }
      if (fields[idx].id != null && fields[idx].type == 'select-multiple' && fields[idx].name != null &&
        	  fields[idx].name.indexOf('values') == 0)
          {
    	  var name = fields[idx].name;
    	  name = name.substring(7,name.length-1);
    	  var _options_legth = parent.parent.mcmApp.searchFields[name].options.length;
			for(var i = 0 ; i < _options_legth ; i++ ){
				$('#expand-container span.eto-tag__remove').each(function() {
					$( this ).trigger( "click" );
				});
			}
        }
   }
   if (document.forms[0].selectedFilter != undefined)
   {
   	   document.forms[0].selectedFilter.selectedIndex = 0;
       document.forms[0].selectedFilterName.value = '';
   }

   $('#expand-container span.eto-tag__remove').each(function() {
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
	  if(field == undefined && '${formName}' == 'costForecastVarianceForm') {
          field = document.forms['${formName}'].elements['values(${field.name})'];
      }
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

function goEditFilterName(idKey)
{
	$('#input-'+idKey).removeAttr("disabled");
	temp = idKey;
	popObj.oldKey = idKey;
	popObj.oldVal = $('#input-'+idKey).val();
	$('#popup-div').css("top",$('#input-'+idKey).offset().top+ $('#input-'+idKey).height());
	$('#popup-div').css("left",$('#input-'+idKey).offset().left);
	popObj.open();
}
function applyFilter(key, value)
{
	if (key != '' && value !='')
    {
    	document.forms['${formName}'].selectedFilterName.value = value;
    	document.forms['${formName}'].selectedFilter.value = key;
    	submitLoadFilter();
    }
    else
    {
		document.forms['${formName}'].selectedFilterName.value = '';
    }

}
function cancelSave()
{
	$('#input-'+temp).val(popObj.oldVal);
	$('#input-'+temp).attr('disabled', 'disabled');
	popObj.close();
}
function closeManageFilters()
{
	modalObj.close();
	$('select[name="selectedFilter"] option:selected').removeAttr('selected');
}
function saveEdit()
{
	// $('#edit-cancel').attr('disabled', 'disabled');
	var newFilterName= $('#input-'+temp).val();

	if(typeof newFilterName != 'undefined' && newFilterName != ''){
	$.ajax({
		type: "POST",
		url: 'renameFilter.do',
		cache: false,
//		dataType: 'text/plain',
		data: {
			newFilterName: newFilterName,
			oldFilterKey: popObj.oldKey
		},
		success: function(result) {
			closeWaitBusy();
		    $('select[name="selectedFilter"] option[value='+popObj.oldKey+']').text(newFilterName);
			popObj.close();
			$('#input-'+temp).val(newFilterName);
			$('#input-'+temp).attr('disabled', 'disabled');
		},
		error: function(error){
			alert('error'+error.statusText);
			closeWaitBusy();
		}
	});

	showSearchBusy();
	}
}

function deleteFilters()
{
	modalObj.close();
	var filterDeleteKeys = [];
	$('input[name="filterKeys[]"]:checked').each(function() {
		filterDeleteKeys.push(this.value);
		// $(this).parent().parent().parent().css('display','none');
	});

	if(filterDeleteKeys.length > 0){
		document.forms['${formName}'].selectedFilterKeys.value = filterDeleteKeys;
		document.forms['${formName}'].operation.value='deleteFilters';
		document.forms['${formName}'].submit();
		showSearchBusy();
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


function handleSaveFilter(){
	var filterValue =$("#newFilterName").val().trim().replace(/ {1,}/g," ");
	if(filterValue!= ''){
		$('#filterSaveModalButton').removeAttr("disabled");
	}else{
		$("#newFilterName").val(filterValue);
		$('#filterSaveModalButton').attr('disabled','disabled');
	}
}

function saveNewFilter(){
	document.forms['${formName}'].selectedFilterName.value = $('#newFilterName').val().trim();
	$('select[name="selectedFilter"] option:selected').removeAttr('selected');
	document.body.style.cursor='wait';
	document.forms['${formName}'].operation.value='saveFilter';
	document.forms['${formName}'].submit();
	showSearchBusy();
}

function handleSaveFilterModal(){
	if($('#input-'+temp).val() != ''){
		$('#edit-save').show();
	}else{
		$('#edit-save').hide();
	}
}
</script>
</c:if>
<input type="hidden" name="fileName" />
<input type="hidden" name="selectedFilterName"
	value="${form.selectedFilterName}" />
<input type="hidden" name="selectedFilterKeys"
	value="${form.selectedFilterKeys}" />
<input type="hidden" name="operation" />
<input type="hidden" name="clearSelection" value="false" />
<input type="hidden" name="searchParametersChanged" value="false" />
<input type="hidden" name="checkLastAccessParam" value="true">
<!-- MM 04 -->
<div style="padding-bottom:1rem;">  <e2o:errors /></div>
<c:choose>
	<c:when test="${showFilter}">
		<fmt:message var="title" key="search.filter.title.harmony" />
		<div class="eto-well eto-expand" id="expand-container">
			<div class="eto-expand__toggle display-xs-flex row">
				<h3 class="eto-expand__h3 col-sm-2 col-xs-2 col-md-2 col-lg-2 col-xl-2">${title}</h3>
				<!-- LP 02 -->
				<div class="col-sm-10 col-xs-10 col-md-10 col-lg-10 col-xl-10 flex-items-lg-right">
					<div class="float-xs-right display-xs-flex eto-expand__toggle" id="filterHeaderContainerDiv">
					<label class="eto-select float-xs-right"
						style="margin-right: 1rem; margin-top: 4px;width : 11rem;"><fmt:message
							key="label.availableFilters" /></label>
						<div class="eto-select" id="searchFilter">
							<div class="eto-select__field-container">
								<select class="eto-select__field" name="selectedFilter"
								onchange="javascript:handleFilterChange($('#savedFilter option:selected').val(), $('#savedFilter option:selected').text());"
								style="font-style: italic;width: auto;" id="savedFilter">
								<c:forEach var="filter" items="${form.availableFilters}">
								<c:set var='defaultFilter' value='${form.searchDefinition.name}' />
								<c:set var='defaultFilterValue' value= 'default${defaultFilter}' />
								<c:if test="${filter.value != defaultFilterValue}">
									<option value="${filter.key}"
										${form.selectedFilter == filter.key ? 'selected' : ''}>${filter.value}</option>
								</c:if>
								</c:forEach>
								<option class="other-option" value="manageFilter-value"
									id="manageFilter">Manage Filters</option>
								</select>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="eto-expand__content">
				<div class="container">
					<div id="alertDropdown"></div>

					<c:if test="${! empty searchFields}">
						<div id="search-Filters">
						<c:if test="${moreflagfilter == true}">
							<a id="toggleButton" onclick="toggleText();"
								href="javascript:void(0);" style="display: none;">More
								Options</a>
						</c:if>
						<c:if test="${moreflagfilter == false}">
								<a id="toggleButton" onclick="toggleText();"
								href="javascript:void(0);" style="display: none;">Less
								Options</a>
						</c:if>

							<div class="row" id="firstRow">
								<c:set var="rowCount" value="0" />
								<c:forEach var="field" items="${searchFields}" varStatus="fieldCount">
									<c:set var="rowCount" value="${rowCount + 1}" />
									<c:choose>
										<c:when test="${rowCount > lessFilterCount || field.properties['newLine'] == true}">
											<c:choose>
												<c:when test="${moreflagfilter == true}">
													<div class="col-xs-12 col-sm-3 col-md-2 col-lg-2 col-xl-2 secondRow margin-left-xs-2 margin-right-xs-2 margin-left-xl-20 margin-right-xl-20 margin-top-xs-2" style="display: none">
														<script type="text/javascript">
															$('#toggleButton').css('display','block');
														</script>
														<e2ot:searchField field="${field}"	fieldCount="${fieldCount.count}" autoSubmitEnabled="true" />
													</div>
												</c:when>
												<c:otherwise>
													<div class=" col-xs-12 col-sm-3 col-md-2 col-lg-2 col-xl-2 margin-left-xs-2 margin-right-xs-2  margin-right-xl-20 margin-left-xl-20 margin-top-xs-2 margin-top-xs-2">
													<e2ot:searchField field="${field}" fieldCount="${fieldCount.count}" autoSubmitEnabled="true" />
													</div>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<div class=" col-xs-12 col-sm-3 col-md-2 col-lg-2 col-xl-2 margin-left-xs-2 margin-right-xs-2  margin-right-xl-20 margin-left-xl-20 margin-top-xs-2 margin-top-xs-2">
											<e2ot:searchField field="${field}" fieldCount="${fieldCount.count}" autoSubmitEnabled="true" />
											</div>
										</c:otherwise>
									</c:choose>
							</c:forEach>
					</div>
				<div class="row">
					<div class="col-xs-12 margin-top-xs-3">
						<button type="button" onclick="javascript:goSubmitSearch()"
							class="eto-btn eto-btn--primary margin-right-xs-1">
							<fmt:message key="button.search.harmony" />
						</button>
						  <c:if test="${fn:contains(allowableAsynhronusCallList, formName )}">

                         <c:if test="${formName eq 'tamDownloadForm'}" >
                         <div class="eto-btn-split eto-dropdown margin-right-xs-1"
                            id="split-download-button">
                            <button type="button" class="eto-btn"
                                ><fmt:message key="button.search.asynchronous" /></button>
                            <button type="button"
                                class="eto-btn eto-btn--icon-only eto-dropdown__toggle">
                                <i class="md-icon" style="cursor: pointer;">keyboard_arrow_down</i>
                            </button>
                            <ul class="eto-dropdown__menu">
                                <li><a href="javascript:goAsynchronusSearch('itemDownload');" >Item Allocation Download</a></li>
                                <li><a href="javascript:goAsynchronusSearch('supplierDownload')">Supplier Allocation Download</a></li>
                            </ul>
                        </div>
                            <script>
                                new eto.Dropdown({ el: document.querySelector('#split-download-button') });
                            </script>
                         </c:if>
                         <c:if test="${formName eq 'forecastSearchForm'}" >
                         <div class="eto-btn-split eto-dropdown margin-right-xs-1"
                            id="split-download-button">
                            <button type="button" class="eto-btn"
                                ><fmt:message key="button.search.asynchronous" /></button>
                            <button type="button"
                                class="eto-btn eto-btn--icon-only eto-dropdown__toggle">
                                <i class="md-icon" style="cursor: pointer;">keyboard_arrow_down</i>
                            </button>
                            <ul class="eto-dropdown__menu">
                                <li><a href="javascript:goAsynchronusSearch('CURRENT');" ><fmt:message key="button.download.forecast.CURRENT"/></a></li>
                                <li><a href="javascript:goAsynchronusSearch('ADJUSTABLE')"><fmt:message key="button.download.forecast.ADJUSTABLE"/></a></li>
                            </ul>
                        </div>
                            <script>
                                new eto.Dropdown({ el: document.querySelector('#split-download-button') });
                            </script>
                         </c:if>
                         <c:if test="${formName ne 'tamDownloadForm' && formName ne 'forecastSearchForm'}" >
                        <button type="button" onclick="javascript:goAsynchronusSearch()"
                            class="eto-btn margin-right-xs-1">
                            <fmt:message key="button.search.asynchronous" />
                        </button>
                        </c:if>
                        </c:if>

						<div class="eto-btn-split eto-dropdown margin-right-xs-1"
							id="split-filter-button">
							<button type="button" class="eto-btn"
								onclick="javascript:submitSaveFilter();">Save</button>
							<button type="button"
								class="eto-btn eto-btn--icon-only eto-dropdown__toggle">
								<i class="md-icon" style="cursor: pointer;">keyboard_arrow_down</i>
							</button>
							<ul class="eto-dropdown__menu">
								<li><a href="#" data-modal="#save-filter-modal">Save As</a></li>
							</ul>
						</div>
						<button id="searchClearButton"
							onclick="javascript:clearSearchCriteria()" type="button"
							class="eto-btn">
							<fmt:message key="button.clear" />
						</button>

					</div>
				</div>
			</div>
			</c:if>
		</div>
		<!-- e2i2 container end -->
		<!-- edl container collapse end start-->
		</div>
		</div>
		<!-- edl container collapse end end-->
	</c:when>
	<c:otherwise>
		<%-- <e2o:errors /> --%>
	</c:otherwise>
</c:choose>

<div class="eto-modal" id="save-filter-modal">
	<div class="eto-modal__content col-xs-12 col-sm-3 col-lg-6 col-xl-4">
		<header class="eto-modal__header">
			<span>Save Filter</span>
			<button class="eto-modal__close" data-modal-close></button>
		</header>
		<section class="eto-modal__body">
			<div class="eto-input">
				<label class="eto-input__label" for="etot81">Filter Name</label>
				<input class="eto-input__field" type="text" size="30"
								maxlength="250"  name="newFilterInput" id="newFilterName"
								 oninput="handleSaveFilter();"
								placeholder="Enter filter Name">
							<div class="eto-input__message"></div>
			</div>
			<nav class="eto-form__btns" style="padding-top: 5%; float: right;">
				<div class="eto-btn-group">
					<button class="eto-btn" data-modal-close>Close</button>
					<button class="eto-btn eto-btn--primary" id='filterSaveModalButton'
						data-modal-close disabled="disabled" onclick="saveNewFilter();">Save</button>
				</div>
			</nav>
		</section>
	</div>
</div>
<div class="eto-modal" id="manage-filter-modal" style="display: block">
	<div class="eto-modal__content col-xs-12 col-sm-10 col-lg-8 col-xl-6"
		style="overflow-x: hidden;">
		<header class="eto-modal__header">
			<span>Manage Filters</span>
			<button class="eto-modal__close" data-modal-close></button>
		</header>
		<section class="eto-modal__body">
			<div class="eto-grid" id="grid-table">
				<div class="eto-grid-scroll">
					<table id="edit-filters" style="table-layout: auto !important;">
						<colgroup>
							<col>
							<col>
						</colgroup>
						<thead>
							<tr>
								<th><label class="eto-checkbox"> <input
										class="eto-checkbox__field eto-all-rows-indicator"
										type="checkbox"> <span class="eto-checkbox__box" onclick="setTimeout(function() {javascript:updateDelete();},200);"></span>
								</label></th>
								<th>Saved Filters</th>
							</tr>
						</thead>
						<tbody>

							<c:forEach var="filter" items="${form.availableFilters}">
							<c:if test="${filter.value != defaultFilterValue}">
								<c:if test="${not empty filter.key}">
									<tr>
										<td><label class="eto-checkbox"> <input
												class="eto-checkbox__field eto-row-indicator"
												type="checkbox" value='${filter.key}' name='filterKeys[]'>
												<span class="eto-checkbox__box"></span>
										</label></td>
										<td id="grid-td-${filter.value}" class="eto-grid-edit-cell">
											<div class="row">
											<div class="eto-input has-value col-xs-8 col-sm-8 col-md-8 col-lg-10 col-xl-10">
												<div class="eto-input__field-container">
													<input id="input-${filter.key}" class="eto-input__field"
														type="text" value="${filter.value}"
														oninput="handleSaveFilterModal();" disabled>
												</div>
											</div>
											<div style="display: flex;" class="col-xs-4 col-sm-4 col-md-4 col-lg-2 col-xl-2">
													<div style="align-items:center;display:flex;">
													<button class="eto-icon-btn" title="Edit" type="button" onclick="javascript:goEditFilterName('${filter.key}')">
														<i class="md-icon mtcm_icon">mode_edit</i>
													</button>
													</div>
													<div style="align-items:center;display:flex;">
													<button class="eto-icon-btn" title="Navigate" type="button" onclick="javascript:applyFilter('${filter.key}', '${filter.value}')">
														<i class="md-icon mtcm_icon">exit_to_app</i>
													</button>
													</div>
											</div>
											</div>
										</td>
									</tr>
								</c:if>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
					<script>
    						new eto.Grid({ el: document.querySelector('#grid-table') });
    				</script>
				</div>
			</div>
		</section>

		<footer class="eto-modal__footer"
			style="height: 60px; border-top-style: solid; border-top-width: 2px; border-top-color: #cddfe4">
			<div class="eto-btn-group"
				style="margin-top: 10px; margin-bottom: -3px">
				<button type="button" class="eto-btn"
					onclick="javascript:closeManageFilters()">Cancel</button>
				<button type="button" class="eto-btn eto-btn--primary" id="saveFilterDelete"
					onclick="javascript:deleteFilters()" disabled="disabled">Delete Filter</button>
			</div>
		</footer>
	</div>
</div>
<div id="popup-div" class="eto-popover popup-styl">
	<div class="eto-popover__content">
		<button type="button" id="edit-save" onclick="saveEdit()"
			class="eto-btn">Save</button>
		<button type="button" id="edit-cancel"
			onclick="javascript:cancelSave()" class="eto-btn">Cancel</button>
	</div>
</div>
<script>
new eto.SelectInput({ el: document.querySelector('#searchFilter') });
new eto.Expand({ el: document.querySelector('#expand-container') });
new eto.Dropdown({ el: document.querySelector('#split-filter-button') });
var saveFilterModal = new eto.Modal({ el: document.querySelector('#save-filter-modal') });
popObj = new eto.Popover({ el: document.querySelector('#popup-div') });
popObj.on('closed',function(query){cancelSave();});

$('input[name="filterKeys[]"]').click(function(){
	updateDelete();
});

function updateDelete(){
	if($('input[name="filterKeys[]"]:checked').length > 0){
		$('#saveFilterDelete').removeAttr('disabled');
	}else{
		$('#saveFilterDelete').attr('disabled','disabled');
	}
}


function closeThisTag(element){
	multiSelectItem[element].length = 0;
	 $("#badge"+element).hide();
	 $("input[name='value["+element+"]']").val("");
	 parent.parent.mcmApp.searchFields[element].clearValue();
	 multiSelectItem[element].length = 0;
	 invalidSelectItem[element].length = 0;
	 addElementTags(element,multiSelectItem[element]);
}
function closeSingleTag(element,itemtoRemove){
   $("#tag_"+$.escapeSelector(itemtoRemove)).remove();
   if (multiSelectItem[element] && Array.isArray(multiSelectItem[element])) {
	   multiSelectItem[element].splice($.inArray(itemtoRemove, multiSelectItem[element]), 1);
   }
   if (invalidSelectItem[element] && Array.isArray(invalidSelectItem[element])) {
	   invalidSelectItem[element].splice($.inArray(itemtoRemove, invalidSelectItem[element]), 1);
   }
   $("#badge"+element+" button span").html(invalidSelectItem[element]);
   $("input[name='value["+element+"]']").val(multiSelectItem[element].join(";"));
   parent.parent.mcmApp.searchFields[element].clearValue();
   addElementTags(element,multiSelectItem[element]);
   if(invalidSelectItem[element].length>0) {
	   $("#badge"+element+" button span").html(invalidSelectItem[element].length);
	  $("#badgeCount"+element).show();
   } else {
	   $("#badgeCount"+element).hide(); 
   }
}
</script>