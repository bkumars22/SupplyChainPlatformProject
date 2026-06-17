<%@ attribute name="field" required="true"
	type="com.scplatform.pcm.searchframework.dto.SearchParameter"%>
<%@ attribute name="fieldCount"%>
<%@ attribute name="autoSubmitEnabled"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<fmt:setBundle basename="scplatform-messages" />

<c:set var="filterDCFunction" value="handleFilterFieldChanged(this)" />
<c:set var="autoSubmitFunction" value="" />
<c:if test="${autoSubmitEnabled}">
	<c:set var="autoSubmitFunction" value="return handleAutoSubmit(event)" />
</c:if>

<c:set var="autoCorrect" value="handlePaste(event, ${fieldCount})" />
<fmt:message var="clearTitle" key="button.title.clear" />
<c:choose>
	<c:when test="${field.type == 'MULTISELECT'}">
		<c:set var="fieldTypeKey" value="search.selectMany" />
	</c:when>
	<c:when test="${field.type == 'SINGLESELECT'}">
		<c:set var="fieldTypeKey" value="search.selectOne" />
	</c:when>
	<c:when test="${field.type == 'DATE'}">
		<c:set var="fieldTypeKey" value="search.dateField" />
	</c:when>
	<c:when test="${field.type == 'MULTITEXT'}">
		<c:set var="fieldTypeKey" value="search.multitext" />
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${field.matchType == 'LIKE'}">
		<c:set var="fieldTitleKey" value="search.likeMatch" />
	</c:when>
	<c:when test="${field.matchType == 'ILIKE'}">
		<c:set var="fieldTitleKey" value="search.ilikeMatch" />
	</c:when>
	<c:when test="${field.matchType == 'EXACT'}">
		<c:set var="fieldTitleKey" value="search.equalMatch" />
	</c:when>
	<c:when test="${field.matchType == 'IEXACT'}">
		<c:set var="fieldTitleKey" value="search.equalNoCaseMatch" />
	</c:when>
	<c:when test="${field.matchType == 'LT'}">
		<c:set var="fieldTitleKey" value="search.lessThan" />
	</c:when>
	<c:when test="${field.matchType == 'LE'}">
		<c:set var="fieldTitleKey" value="search.lessThanEqual" />
	</c:when>
	<c:when test="${field.matchType == 'GT'}">
		<c:set var="fieldTitleKey" value="search.greaterThan" />
	</c:when>
	<c:when test="${field.matchType == 'GE'}">
		<c:set var="fieldTitleKey" value="search.greaterThanEqual" />
	</c:when>
	<c:otherwise>
		<c:set var="fieldTitleKey" value="search.equalMatch" />
	</c:otherwise>
</c:choose>
<fmt:message var="titleMessage" key="${fieldTitleKey}" />
<c:if test="${!empty fieldTypeKey}">
	<fmt:message var="typeMessage" key="${fieldTypeKey}">
		<c:if test="${field.type == 'MULTITEXT'}">
			<fmt:param>${field.delimiter}</fmt:param>
		</c:if>
	</fmt:message>
</c:if>
<%-- <td class="searchFieldLabel" title="${titleMessage} ${typeMessage}"
						style="width: 400px; padding: 10px 10px 5px 10px;"><h3 style="border-bottom: 1px solid #aea7a7; padding-bottom: 10px;"><fmt:message
							key="${field.labelKey}" /> <c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></h3>
</td> --%>
<td class="searchFieldControl"><c:choose>
		<c:when test="${field.type == 'SINGLESELECT'}">

			<div class="eto-select eto-form eto-form--responsive" id="select${field.name}">
				<label class="eto-select__label"><fmt:message
						key="${field.labelKey}" /><c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></label>
				<div class="eto-select__field-container">
					<select class="eto-select__field" name="value[${field.name}]"
						onchange="${filterDCFunction}" onkeypress="${autoSubmitFunction}"
						id="searchField${fieldCount}">
					</select>
				</div>
			</div>
			<script>
			parent.parent.mcmApp.searchFields['${field.name}'] = new eto.SelectInput(
						{
							el : document.querySelector('#select${field.name}')
						});
				var myFieldName = '${field.name}';
				var selectOptions = [];
				<c:forEach var="litem" items="${field.selectValueEntries}">
				var litemKey = '${litem.key}';
				var litemVal = '${litem.value}';
				var ignoreThis = '<fmt:message var="temp" key="${litem.value}"/>';
				var tempVal = '${temp}';
				var optionVal;
				if (tempVal.startsWith('???')) {
					optionVal = litemVal;
				} else {
					optionVal = tempVal;
				}
				var option = {
					"value" : litemKey,
					"label" : optionVal
				};
				var fieldVal = '${field.value}';
				if (fieldVal && fieldVal == litemKey) {
					option.selected = true;
				}
				selectOptions.push(option);
				</c:forEach>
				parent.parent.mcmApp.searchFields['${field.name}']
						.setOptions(selectOptions);
			</script>
			<c:set var="fieldWith" value="width:150px" />
			<c:if test="${field.dataType == 'Date'}">
				<c:set var="fieldWith" value="width:210px" />
			</c:if>

		</c:when>
		<c:when test="${field.type == 'MULTISELECT'}">
			<c:set var="fieldWith" value="width:150px" />
			<c:if test="${field.dataType == 'Date'}">
				<c:set var="fieldWith" value="width:210px" />
			</c:if>
			<div id="complexSelect${field.name}" class="complexSelect">
				<label class="eto-select__label"><fmt:message
						key="${field.labelKey}" /><c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></label> <select id="multiSelect${field.name}"
					name="values[${field.name}]" multiple>
				</select>
			</div>
			<script>
			 parent.parent.mcmApp.searchFields['${field.name}'] = new eto.ComplexCombobox(
						{
							el : document
									.querySelector('#multiSelect${field.name}')
						});
				
				var multiSelectOptions = [];
				var checkSelect = false;
				<c:forEach var="litem" items="${field.selectValueEntries}">
				var litemKey = '${litem.key}';
				var litemVal = '${litem.value}';
				var ignoreThis = '<fmt:message var="temp" key="${litem.value}"/>';
				var tempVal = '${temp}';
				var multiOptionVal;
				if (tempVal.startsWith('???')) {
					multiOptionVal = litemVal;
				} else {
					multiOptionVal = tempVal;
				}
				var multiSelOption = {
					"value" : litemKey,
					"label" : multiOptionVal
				};
				<c:forEach var="selItem" items="${field.value}">
				if (multiSelOption.value == '${selItem}') {
					multiSelOption.selected = true;
					checkSelect = true;
				}
				</c:forEach>
				multiSelectOptions.push(multiSelOption);
				</c:forEach>
				parent.parent.mcmApp.searchFields['${field.name}']
						.setOptions(multiSelectOptions);
				var fieldVal = '${field.value}';
				<c:forEach var="selItem" items="${field.value}">
				</c:forEach>
				if(checkSelect){
					$('#complexSelect${field.name} .eto-complex-combobox__inline-tags').css('display', 'block');
				}
				else{
					$('#complexSelect${field.name} .eto-complex-combobox__inline-tags').css('display', 'none');
				}
				if('values[${field.name}]'=='values[regions]'){
					parent.parent.mcmApp.searchFields['${field.name}'].disable();
				
				function callOnChange(field) {
					if (field.name == 'value[owner]') {
						if('values[${field.name}]'=='values[regions]')
							parent.parent.mcmApp.searchFields['${field.name}'].disable();
						if (field.value == 'REGIONAL') {
								parent.parent.mcmApp.searchFields['${field.name}'].enable();
						} else {
							hideElement();
						}
					}
				}
				}
			</script>
		</c:when>

		<c:when test="${field.type == 'DATE'}">
			<div class="eto-input eto-form eto-form--responsive" data-message-type=""
				id="divSearchField${fieldCount}">
				<label class="eto-input__label"><fmt:message
						key="${field.labelKey}" /><c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></label>
				<div class="eto-input__field-container">
					<input class="eto-input__field" type="text" id="searchField${fieldCount}"
						name="dateValue[${field.name}]" onkeypress="${autoSubmitFunction}"
						onchange="${filterDCFunction}" readonly="true"> <span
						class="eto-input__addon"><i class="md-icon"
						onclick="javascript:showCalendar('searchField${fieldCount}')">event</i>
						<i class="md-icon"
						onclick="javascript:clearField(document.getElementById('searchField${fieldCount}'))">close</i>
						</span>
				</div>
			</div>
			<c:set var="formatedDateValue" value=""/>
			<c:if test="${not empty field.value}">
				<c:catch var="dateFormatEx">
					<fmt:formatDate value="${field.value}" pattern="${appContext.currentDateFormat}" var="formatedDateValue"/>
				</c:catch>
				<c:if test="${not empty dateFormatEx}">
					<c:set var="formatedDateValue" value="${field.value}"/>
				</c:if>
			</c:if>
			<script>
				parent.parent.mcmApp.searchFields['${field.name}'] = new eto.TextInput({el : document.querySelector('#divSearchField${fieldCount}')});
				parent.parent.mcmApp.searchFields['${field.name}'].setValue('${formatedDateValue}');
			</script>

		</c:when>
		<c:when test="${field.type == 'MULTITEXT'}">
 			<input type="hidden" name="value[${field.name}]" id="value[${field.name}]" value=""/>
			<div style="display: flex;">
				<div class="eto-autocomplete eto-form eto-form--responsive"
					id="autocompleteMulti${fieldCount}" style="width: 100%;">
					<label class="eto-autocomplete__label"><fmt:message
							key="${field.labelKey}" /> <c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></label>
					<div class="eto-autocomplete__container">
						<div class="eto-autocomplete__gray-container" id="${field.name}">
							<input id="searchField${fieldCount}"
								class="eto-autocomplete__field" type="text"
								delimiter="semicolon" aria-autocomplete="list"
								autocomplete="off" placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'/>
								<c:if test="${!empty field.popupFinderName}">
					<span class="eto-input__addon"
							onclick="javascript:doFinderPopup('${field.popupFinderName}',null,'onMultiTextCallback(${fieldCount},%7B0%7D)')"
							style="cursor: pointer;">
							<span class="md-icon">search</span>
						</span>
					</c:if>
							<div class="eto-autocomplete__tags-container" id="tagContainer${field.name}">
								<div class="eto-autocomplete__tags" id="autocomplte_tag_${field.name}" style="overflow: scroll">
								</div>
								<button type="button" onclick="closeThisTag('${field.name}')" class="eto-autocomplete__clear"></button>
							</div>
							<div class="eto-autocomplete__show-selected" role="presentation"
								aria-hidden="false" id="badge${field.name}">
								<a href="javascript:void(0)">View all tags <span
									class="eto-badge" id="viewTag${field.name}" data-type="info"></span></a>
									 <button type="button" class="eto-btn eto-btn--link" data-popover="#badgePopover${field.name}" aria-haspopup="true" aria-controls="#badgePopover${field.name}" id="badgeCount${field.name}" style="display:none;padding: 0px;">Invalid <span
									class="eto-badge" data-type="error"></span></button>
									<div id="badgePopover${field.name}" style="width:15% !important" class="eto-popover" data-anchor-x="right" data-anchor-y="middle"data-affixed>
  									<div class="eto-popover__content" ></div>
  									<span class="eto-popover__caret"></span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<script>
			parent.parent.mcmApp.searchFields['${field.name}'] = new eto.Autocomplete({
				  el: document.querySelector('#autocompleteMulti${fieldCount}')
			});
			
	 multiSelectpopObj["badgePop_"+'${field.name}'] = new eto.Popover({ el: document.querySelector('#badgePopover${field.name}') });
	 multiSelectItem["${field.name}"] = [];
	 var fieldData_${field.name} = '${field.value}'.split(';');
	 if (fieldData_${field.name} != "") {
		 getMultiSelectData('${field.name}','${field.value}');
		 multiSelectItem["${field.name}"] = $.unique(fieldData_${field.name});
		 $("input[name='value[${field.name}]']").val(multiSelectItem["${field.name}"].join(";"));
		 addElementTags('${field.name}',multiSelectItem["${field.name}"]);
	  }
	 
	   $('#searchField${fieldCount}').keypress(function(event){
		   var keycode = (event.keyCode ? event.keyCode : event.which);
		    if(keycode == 13 || keycode==59){
		        var value = $(this).val().trim();
		        if(value == "") return;
		        if(multiSelectItem["${field.name}"][value] != null)  {
					return;
		        }
		        multiSelectItem["${field.name}"].push(value);
		        $(this).val('');
		        $("input[name='value[${field.name}]']").val(multiSelectItem["${field.name}"].join(";"));
		        addElementTags('${field.name}',multiSelectItem["${field.name}"]); 
		        let multiselectValue = multiSelectItem["${field.name}"].join(";");
		        getMultiSelectData('${field.name}',multiselectValue);
		    }  
		});
		$('#searchField${fieldCount}').bind('paste', function(e) {
    		var searchField${fieldCount}clipboardData = e.clipboardData || e.originalEvent.clipboardData || window.clipboardData;
    		var searchField${fieldCount} = searchField${fieldCount}clipboardData.getData('text');
    		var value = searchField${fieldCount}.split(/[\r\n;]+/).filter(entry => /\S/.test(entry)).map(entry => entry.trim()); // Split by new lines or semicolons and trim whitespace

    		value.forEach(entry => {
        		if (multiSelectItem["${field.name}"].indexOf(entry) === -1) { // Check if the value is not already in the array
           			multiSelectItem["${field.name}"].push(entry);
        		}
    		});

    		$(this).val('');
    		let multiselectValue = multiSelectItem["${field.name}"].join(";");
    		$("input[name='value[${field.name}]']").val(multiselectValue);
    		addElementTags('${field.name}', multiSelectItem["${field.name}"]);
    		getMultiSelectData('${field.name}', multiselectValue);
		});
			</script>
		</c:when>

		<c:when test="${field.type == 'TEXT' && empty field.finderName}">
			<!-- This is for regular text fields -->
			<div class="eto-input eto-form eto-form--responsive" id="divSearchField${fieldCount}">
				<label class="eto-input__label"><fmt:message
						key="${field.labelKey}" /><c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></label> <input
					onkeypress="${autoSubmitFunction}" onchange="${filterDCFunction}"
					class="eto-input__field" type="text" name="value[${field.name}]"
					placeholder="" id="searchField${fieldCount}">
			</div>
			<script>
				parent.parent.mcmApp.searchFields['${field.name}'] = new eto.TextInput(
						{
							el : document
									.querySelector('#divSearchField${fieldCount}')
						});
				parent.parent.mcmApp.searchFields['${field.name}']
						.setValue('${field.value==null ?'':field.value}');
			</script>
		</c:when>

		<c:otherwise>
			<!-- This is for autocomplete fields -->

			<div style="display: flex;">
				<div class="eto-autocomplete eto-form eto-form--responsive" id="autocomplete${fieldCount}"
					style="width: 100%;">
					<label class="eto-autocomplete__label"><fmt:message
							key="${field.labelKey}" /><c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></label> <input id="searchField${fieldCount}"
						onkeypress="${autoSubmitFunction}" onchange="${filterDCFunction}"
						class="eto-autocomplete__field" type="text"  placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'
						name="value[${field.name}]" autocomplete="off">
					<!-- <img
					src="../../skins/e2-modern/images/working.gif" alt="loading"> -->
					<div class="eto-autocomplete__message"></div>
					<div class="eto-results"></div>
				</div>
				<div style="margin: auto; display: none; margin-top: 3rem;"
					id="autoCompleteLoader_${field.name}">
					<svg version="1.1" xmlns="http://www.w3.org/2000/svg" x="0px"
						y="0px" width="24px" height="24px" viewBox="0 0 50 50"
						style="enable-background: new 0 0 50 50;" xml:space="preserve">
  					<path fill="#277ab5"
							d="M25.251,6.461c-10.318,0-18.683,8.365-18.683,18.683h4.068c0-8.071,6.543-14.615,14.615-14.615V6.461z">
    				<animateTransform attributeType="xml" attributeName="transform"
							type="rotate" from="0 25 25" to="360 25 25" dur="0.6s"
							repeatCount="indefinite" />
  					</path>
					</svg>
				</div>
			</div>
			<script>
			  $(document).ready(function() {
					$('#searchField${fieldCount}').keyup(function() {
					return	$(this).val($(this).val().replace(/^\s+/g, '').replace(/\s\s+/g, ' '));
			        });
				}); 
			parent.parent.mcmApp.searchFields['${field.name}'] = new eto.Autocomplete(
						{
							el : document
									.querySelector('#autocomplete${fieldCount}'),add: true 
						});
				parent.parent.mcmApp.searchFields['${field.name}'].setValue('<c:out value="${field.value}"  escapeXml="true"/>');
				parent.parent.mcmApp.searchFields['${field.name}'].on('inputChange',
						function(query) {
					if(query.trim() != ""){
						$('#autoCompleteLoader_${field.name}').css('display','inline');
						var url = "ajaxQuery${field.finderName}?q=" +encodeURIComponent(query);
						var extra = {${field.properties['finderParams']}};
						for (var i in extra) {
							url += "&" + i + "=" + encodeURI(extra[i]);
						}
						
						$.ajax({
							url : url,
							success : function(result) {
								var arr;
								if (result.includes("|")) {
									arr = result.split("|");
								} else {
									arr = result.split("\n");
								}
								parent.parent.mcmApp.searchFields['${field.name}']
										.setContent(arr);
								parent.parent.mcmApp.searchFields['${field.name}']
										.open();
								$('#autoCompleteLoader_${field.name}').css('display','none');
							}
						});
					}
					else{
						var element = parent.parent.mcmApp.searchFields['${field.name}'];
						if(element.isOpen()){
							element.setContent([]);
							element.close();
						}
						$('#autoCompleteLoader_${field.name}').css('display','none');
					}
				});
			 
			</script>
		</c:otherwise>
	</c:choose> <jsp:doBody /></td>