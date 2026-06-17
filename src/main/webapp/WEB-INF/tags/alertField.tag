<%@ attribute name="field" required="true"
	type="com.scplatform.utility.pcm.util.search.SearchParameter"%>
<%@ attribute name="autoSubmitEnabled"%>
<%@ attribute name="disabled"%>
<%@ attribute name="fieldCount"%>
<%@ attribute name="alertCount"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>

<fmt:setBundle basename="scplatform-messages" />

<c:choose>
	<c:when test="${field.type == 'MULTISELECT'}">
		<c:set var="fieldTypeKey" value="search.selectMany" />
	</c:when>
	<c:when test="${field.type == 'SINGLESELECT'}">
		<c:set var="fieldTypeKey" value="search.selectOne" />
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

<div class="col-sm-4">
	<div id="grid-table${fieldCount}">
		<table>
			<colgroup>
				<col>
			</colgroup>
			<thead>
				<c:choose>
	<c:when test="${field.type == 'SINGLESELECT'}">
	<tr> <!-- style="border-bottom: 1px solid #aea7a7;" -->
					<th class="searchFieldLabel" title="${titleMessage} ${typeMessage}"
						style="width: 400px; padding: 10px 10px 5px 10px;"><h3 style="border-bottom: 1px solid #aea7a7; padding-bottom: 10px;"><fmt:message
							key="${field.labelKey}" /> <c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></h3></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="searchFieldControl"
						style="width: 1200px; padding: 10px 10px 5px 10px;">
	<div class="eto-select" id="selectDiv${fileCount}">
  <label class="eto-select__label">Select Option</label>
  <div class="eto-select__field-container">
    <select class="eto-select__field" name="values(${field.name})" ${ disabled ==true ?"disabled":''} id="select${fileCount}">
    <c:forEach var="litem" items="${field.selectValueEntries}">
<fmt:message var="temp" key="${litem.value}" />
      <option value="${litem.key}" ${litem.value==field.getValue() ? "selected" : '' }>
										<c:choose>
											<c:when test="${fn:startsWith(temp,'???')}">
												<c:out value="${litem.value}" />
											</c:when>
											<c:otherwise>
												<c:out value="${temp}" />
											</c:otherwise>
										</c:choose>
	  </option>
      </c:forEach>
    </select>
  </div>
  <div class="eto-select__message"></div>
</div>
<script type="text/javascript"> 
new eto.SelectInput({ el: document.querySelector('#select${fileCount}') });
</script>
</td>
	</c:when>
							<c:when test="${field.type == 'MULTISELECT'}">
	<tr>
					<th class="searchFieldLabel" title="${titleMessage} ${typeMessage}"
						style="width: 400px; padding: 10px 10px 5px 10px;"><h3 style="border-bottom: 1px solid #aea7a7; padding-bottom: 10px;"><fmt:message
							key="${field.labelKey}" /></h3> <c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if>
						<label class="eto-checkbox" style=" padding-top: 10px;"> <input
									${ disabled == true ? "disabled" : '' }
									class="eto-checkbox__field eto-all-rows-indicator"
									type="checkbox"> <span class="eto-checkbox__box"></span>
									<span class="eto-checkbox__label" style="font-weight: bold;">Select All</span>
								</label>
						</th>
				</tr>
			</thead>
			<tbody>
								<c:forEach var="litem" items="${field.selectValueEntries}">
				<tr>
					<td class="searchFieldControl"
						style="width: 1200px; padding: 10px 10px 5px 10px;">
								
									<label class="eto-checkbox"><input
										class="eto-checkbox__field eto-row-indicator" type="checkbox"
										name="values(${field.name})" value="${litem.key}"
										${ disabled ==true ?"disabled":''}
										${e2ofn:arrayContains(field.value,litem.key) ? 'checked' : ''}>
										<span class="eto-checkbox__box"></span> <span
										class="eto-checkbox__label"><fmt:message var="temp"
												key="${litem.value}" /> <c:choose>
												<c:when test="${fn:startsWith(temp,'???')}">
													<c:out value="${litem.value}" />
												</c:when>
												<c:otherwise>
													<c:out value="${temp}" />
												</c:otherwise>
											</c:choose> </span> </label>
								</td>
								</tr>
								</c:forEach>
							</c:when>
							<c:when test="${field.type == 'MULTITEXT'}">
	<tr> <!-- style="border-bottom: 1px solid #aea7a7;" -->
					<th class="searchFieldLabel" title="${titleMessage} ${typeMessage}"
						style="width: 400px; padding: 10px 10px 5px 10px;"><h3 style="border-bottom: 1px solid #aea7a7; padding-bottom: 10px;"><fmt:message
							key="${field.labelKey}" /> <c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></h3></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="searchFieldControl"
						style="width: 1200px; padding: 10px 10px 5px 10px;">
								<div class="eto-autocomplete" id="autocomplete${fieldCount}">
									<label class="eto-autocomplete__label"></label>
									<div class="eto-autocomplete__container">
										<div class="eto-autocomplete__gray-container">
											<input class="eto-autocomplete__field" type="text"
												id="searchField${fieldCount}"
												 placeholder='<fmt:message key="searchFilter.filter.placeholder.value"/>'
												onchange="${filterDCFunction}"
												${ disabled ==true ?"disabled":''}
												class="eto-autocomplete__field" name="values(${field.name})"
												autocomplete="off">
											<div class="eto-autocomplete__tags-container">
												<div class="eto-autocomplete__tags" style="overflow: scroll">
													<c:forEach var="supplierName" items="${field.value}">
														<span class='eto-tag eto-tag--sm'><span
															class=eto-tag__label><c:out
																	value="${supplierName}" /></span> <span class=eto-tag__remove
															tabindex=0 onclick="closeThisTag(this)"
															id="${supplierName}"> <i
																class='md-icon md-icon--sm'>close</i></span> </span>
													</c:forEach>
												</div>
												<button type="button" class="eto-autocomplete__clear"></button>
											</div>
											<div class="eto-autocomplete__show-selected"
												role="presentation" aria-hidden="false">
												<a href="javascript:void(0)">View all tags <span
													class="eto-badge" data-type="info"></span></a>
											</div>
										</div>
									</div>
									<div class="eto-autocomplete__message"></div>
									<div class="eto-results"></div>
								</div>
								<script>
									search['${field.name}'] = new eto.Autocomplete(
											{
												el : document
														.querySelector('#autocomplete${fieldCount}')
											});
									var supplierString = '${field.value}'
											.split(';');
									if (supplierString != "") {
										search['${field.name}']
												.setValue(supplierString);
									}

									search['${field.name}']
											.on(
													'inputChange',
													function(query) {

														var url = "ajaxQuery${field.popupFinderName}?q="
																+ query;
														$
																.ajax({
																	url : url,
																	success : function(
																			result) {
																		var arr;
																		if (result
																				.includes("|")) {
																			arr = result
																					.split("|");
																		} else {
																			arr = result
																					.split("\n");
																		}
																		search['${field.name}']
																				.setContent(arr);
																		search['${field.name}']
																				.open();
																	}
																});

													});
								</script>
								</td>
							</c:when>
							<c:otherwise>
								<tr>
					<th class="searchFieldLabel" title="${titleMessage} ${typeMessage}"
						style="width: 400px; padding: 10px 10px 5px 10px;"><h3 style="border-bottom: 1px solid #aea7a7; padding-bottom: 10px;"><fmt:message
							key="${field.labelKey}" /> <c:if test="${field.required}">
							<SPAN class="requiredIndicator">*</SPAN>
						</c:if></h3></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="searchFieldControl"
						style="width: 1200px; padding: 10px 10px 5px 10px;">
							
								<div class="eto-input" id="text-input-example-1">
									<input class="eto-input__field" type="text"
										name="value(${field.name})" placeholder="Alert Age"
										${ disabled ==true ?"disabled":''} value="${field.getValue()}">
								</div>
								</td>
							</c:otherwise>
						</c:choose> <jsp:doBody /> <c:if test="${!empty field.popupFinderName}">
							<fmt:message var="suggestFieldTitle" key="label.suggestAvailable" />
							<span style="vertical-align: super; padding: 0px; margin: 0px; display: table-column; "
								title="${suggestFieldTitle}"> <e2i2:img
									src="/action_nav_pad.gif"
									onclick="javascript:document.getElementById('searchField${fieldCount}').autocompleter.flushCache()" />
							</span>
						</c:if>
				</tr>
			</tbody>
		</table>
	</div>
	
	<script type="text/javascript">
		new eto.Grid({
			el : document.querySelector('#grid-table${fieldCount}')
		});
		$('#grid-table${fieldCount} table').css('width','100%');
	</script>

</div>


