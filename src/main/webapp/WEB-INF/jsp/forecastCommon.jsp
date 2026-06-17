<%@ include file="../common.jspf"%>
<%@ page import="com.test.repository.pcm.domain.PcmForecast.ForecastModel"%>
<%@ page import="com.test.repository.pcm.domain.PcmForecast"%>
<%@ page import="java.util.List"%>
<%@ page import="com.scplatform.pcm.web.action.forecast.ForecastForm"%>
<%@ page import="com.test.repository.common.domain.bom.meta.FlexAttributeDefn"%>
<%@ page import="java.util.ArrayList"%>
<c:set var="forecastModelAdj" value="ADJUSTABLE" />
<c:set var="forecastModelCurrent" value="CURRENT" />
<c:set var="itemDetailsExpanded"
	value="${e2ofn:getConfigValue('pcm.forecast.itemdetails.expanded')}" />
<c:set var="readOnly"
	value="${!e2ofn:hasAccess(appContext, 'FORECAST', 'Save')}" />
<%
	ForecastForm ff = (ForecastForm) session.getAttribute("forecastForm");
	String model = (String) request.getParameter("forecastModel");
	List fcList = ff.getForecastRecordsBasedOnModel(model);
	pageContext.setAttribute("fcList", fcList);
	pageContext.setAttribute("modelABR", model.substring(0, 1));
	List<FlexAttributeDefn> flexAttributeDefns = ff.getFlexAttributeDefnListForecast();
	pageContext.setAttribute("flexAttributeDefnListForecast", flexAttributeDefns);
%>
<c:if test="${param.forecastModel==forecastModelAdj}">
	<c:set value="grid_forecastModelAdj" var="grid_Id"></c:set>
</c:if>
<c:if test="${param.forecastModel==forecastModelCurrent}">
	<c:set value="grid_forecastModelCurrent" var="grid_Id"></c:set>
</c:if>
<style>
.compact {
	min-height: 26px !important;
	height: 26px !important;
	border-bottom: 0px !important;
}
</style>
<div class="col-lg-12 eto-grid" id="${grid_Id}"
	style="padding-bottom: 10%;">
	<div class="eto-grid-scroll scrollableTable" id="${param.forecastId}">
		<table id="${param.forecastId}_data">
			<colgroup>
				<c:if test="${param.forecastModel==forecastModelAdj}">
					<col>
                    <col>
                    <col>
				</c:if>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
                <col>
				<c:if test="${param.forecastModel==forecastModelCurrent}">
					<c:set var="timeLine" value="${forecastForm.currentTimeline}" />
				</c:if>
				<c:if test="${param.forecastModel==forecastModelAdj}">
					<c:set var="timeLine" value="${forecastForm.adjustableTimeline}" />
				</c:if>
				<c:forEach var="period" items="${timeLine.periods}">
					<col>
				</c:forEach>

			</colgroup>
			<thead class="scrollableTableHeader">
				<!-- Grouped Columns (Column Band) -->
				<tr id="fcTableHeaderRow" class="tableColumnHeadings">
					<c:if test="${param.forecastModel==forecastModelAdj}">
						<th class="fixedColumn">
							<i id="expandBtn${param.forecastId}" class="md-icon mtcm_icon iconHover" onclick="return toggleAllRows('${param.forecastId}')">remove_circle</i>
							 </th>
					</c:if>
					<th class="fixedColumn" ><c:if test="${!readOnly}">
							<label class="eto-checkbox"> <input
								id="${param.forecastId}_globalrowselector"
								class=" eto-checkbox__field eto-row-indicator"
								onclick="i2uiToggleAllRowsSelectionState(this,'${param.forecastId}')"
								type="checkbox"> <span class="eto-checkbox__box"></span>
							</label>
						</c:if></th>
					<th class="fixedColumn"><fmt:message key="fc.item" /></th>
					<th class="fixedColumn"><fmt:message key="fc.site"/></th>
					<th class="fixedColumn"><fmt:message key="fc.status"/></th>
					<th class="fixedColumn hideableColumn"><fmt:message key="fc.itemCategory"/></th>
					<th class="fixedColumn hideableColumn"><fmt:message key="as.responsibility"/></th>
					<th class="fixedColumn hideableColumn"><fmt:message key="fc.rolloverExpiration"/></th>
					<th class="fixedColumn"><fmt:message key="fc.numberOfRollover"/></th>
					<th class="fixedColumn"><fmt:message key="fc.functionalGroups" /></th>
					<th class="fixedColumn"><fmt:message key="functionalGroup.parentName" /></th>
					<c:forEach var="attributeDefn" items = "${flexAttributeDefnListForecast}">
     					<th class="fixedColumn"><fmt:message key="flex.costforecast.${attributeDefn.associatedAttribute}"/></th>
					</c:forEach>
					<c:if test="${param.forecastModel==forecastModelAdj}">
						<th class="fixedColumn"><fmt:message key="fc.adjustment.type" /></th>
						<th class="fixedColumn"><fmt:message key="fc.adj.label" /></th>
					</c:if>
					<c:set var="timeLine" value="${forecastForm.currentTimeline}" />
					<c:if test="${param.forecastModel==forecastModelAdj}">
						<c:set var="timeLine" value="${forecastForm.adjustableTimeline}" />
					</c:if>
					<c:forEach var="period" items="${timeLine.periods}">
						<c:set var="periodTitle">
							<fmt:formatDate pattern="MMM dd yyyy" value="${period.startDate}" /> - <fmt:formatDate
								pattern="MMM dd yyyy" value="${period.endDate}" />
						</c:set>
						<th style="text-align: center;padding-left: 3.5rem;padding-right: 3.5rem" title="${periodTitle}" 
							class="period${period.state}" id="periodHeader${period.label}">
							${period.label}<br> <fmt:formatDate pattern="MMM dd"
								value="${period.startDate}" />
						</th>
					</c:forEach>
			</thead>
			<tbody>
			<script type="text/javascript">
			var fvStates${modelABR} = new Array();
			</script>
				<c:forEach var="fc" items="${fcList}" varStatus="rowCount">
					<c:set var="rowId" value="${modelABR}_${rowCount.index}" />
					<c:set var="canEdit"
						value="${e2ofn:allowOperation(param.businessProcess,fc.status,'Edit')}" />
					<c:set var="canDelete"
						value="${e2ofn:allowOperation(param.businessProcess,fc.status,'Delete')}" />
					<c:choose>
						<c:when test="${readOnly || !canEdit}">
							<c:set var="lineReadOnly" value="true" />
							<c:set var="lockedRow" value="lockedRow" />
						</c:when>
						<c:otherwise>
							<c:set var="lineReadOnly" value="false" />
							<c:set var="lockedRow" value="" />
						</c:otherwise>
					</c:choose>

					<tr id="fcRow_${fc.forecastExternalId}" fcextid="${fc.forecastExternalId}" expanded="${itemDetailsExpanded?'true':'false'}" class="expandRowBtn ${param.forecastModel==forecastModelAdj ? ' expend_fcTableADJ expanded':''}" data-depth="0">
<script type="text/javascript">
fvStates${modelABR}[${rowCount.index}] = new Array();
</script>
						<c:if test="${param.forecastModel==forecastModelAdj}">
							<td class="fixedColumn eto-grid-row-drilldown">
								<div class="eto-grid-row-drilldown-toggle">
									<button type="button"
										class="eto-btn eto-btn--icon-only eto-btn--link"></button>
								</div>
							</td>
						</c:if>
						<td class="fixedColumn ${lockedRow}" style="display:-ms-flexbox;display:-webkit-box;"><label
							class="eto-checkbox"> <input name="selectedRecordKeys"
								Id="${param.forecastId}_rowselector"
								class=" eto-checkbox__field eto-row-indicator"
								onclick="handleLineEventButtons();handleLineButtons()"
								type="checkbox"
								value="<c:out value="${fc.forecastExternalId}" />"> <span
								class="eto-checkbox__box"></span>
						</label> <c:set var="lineMessages" value="" /> <html:messages
								id="lineMessage" property="forecast(${fc.forecastExternalId})">
								<c:set var="lineMessages" value="${lineMessages}${lineMessage}" />
							</html:messages> <c:if test="${!empty lineMessages }">
								<img src="skins/e2-modern/images/alert_yellow_static.gif" style="padding-left:1rem;" alt="${moreInfoTitle}"
									onclick="javascript:showLineMessage(this,'${lineMessages}')" />
							</c:if></td>
						<td class="fixedColumn ${lockedRow}">
						<a href="#" onClick="openPopOver('${fc.item.itemKey}');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${fc.item.itemNumber}" removeColon="true"/></a>						
						</td>
						<td
							class="fixedColumn compact ${empty fc.forecastKey?' eto-grid-edit-cell':''} ${lockedRow}">
							<c:choose>
								<c:when test="${empty fc.forecastKey}">
									<div class="eto-input">
										<div class="eto-select" id="select${fc.forecastKey}">
											<div class="eto-select__container">
												<div class="eto-select__field-container">
													<select
														name="forecastData(${fc.forecastExternalId}).siteKey"
														class="eto-select__field"
														style="width: auto;"
														onchange="handleDataChangedForField(this)">
														<c:forEach var="sites" items="${forecastForm.sites}">
															<option value="${sites.siteKey}">
																${sites.siteDescription}</option>
														</c:forEach>
													</select>
												</div>
											</div>
										</div>
									</div>
									<script>
											new eto.SelectInput({ el: document.querySelector('#select${fc.forecastKey}') });
									</script>
								</c:when>
								<c:otherwise>
									<c:out value="${fc.site.siteDescription}" />
								</c:otherwise>
							</c:choose> <html:messages id="lineMessage"
								property="forecastData(${fc.forecastExternalId}).siteKey">
								<e2i2:img src="/alert_yellow_static.gif" alt="${moreInfoTitle}"
									onclick="javascript:showLineMessage(this,'${lineMessage}')" />
								<c:set var="lineErrorExists" value="true" />
							</html:messages>
						</td>
						<td class="fixedColumn ${lockedRow}"><c:out
								value="${fc.status}" /></td>
						<td class="fixedColumn hideableColumn ${lockedRow}"><e2ot:abbreviate
								length="25">
								<c:forEach var="cat" items="${fc.item.categories}"
									varStatus="ccount">
									<c:if test="${!ccount.first}">, </c:if>
									<c:out value="${cat.categoryName}" />
								</c:forEach>
							</e2ot:abbreviate></td>
						<td class="fixedColumn hideableColumn ${lockedRow}"><e2ot:abbreviate
								length="25">
								<c:forEach var="ia" items="${fc.item.assignments}"
									varStatus="iacount">
									<c:if test="${!iacount.first}">, </c:if>
									<c:out value="${ia.userId} - ${ia.responsibility}" />
								</c:forEach>
							</e2ot:abbreviate></td>
						<td class="fixedColumn hideableColumn ${lockedRow}"
							style="white-space: nowrap"><c:choose>
								<c:when test="${fc.remainingRollovers > 0}">
									<fmt:formatDate var="expDate"
										pattern="${appContext.currentDateFormat}"
										value="${timeLine.periods[timeLine.currentPeriodIndex+fc.remainingRollovers].startDate}" />
													${!empty expDate ? expDate : 'N/A'}
										</c:when>
								<c:when test="${fc.remainingRollovers == 0}">
									<fmt:message key="label.expired" />
								</c:when>
								<c:otherwise>&nbsp;</c:otherwise>
							</c:choose></td>
						<td
							class="${lockedRow} fixedColumn compact  ${!lockedRow ?'eto-grid-edit-cell':''}"><html:text
								styleClass="inputField" size="8" styleId="forecastEP_${rowId}"
								onchange="handleDataChangedForField(this)"
								onblur="validateExtendedPeriod(this)" style="width: 90%;"
								errorStyle="background-color:red;" disabled="${lineReadOnly}"
								property="forecastData(${fc.forecastExternalId}).extendPeriods" />
							<c:if test="${!lineReadOnly}">
								<e2i2:img src="/transfer_down.gif" alt="${pushdownTitle}"
									onclick="javascript:handlePushValueDown(${rowCount.index},'forecastEP_${modelABR}_{0}')" />
							</c:if> <html:messages id="lineMessage"
								property="forecastData(${fc.forecastExternalId}).extendPeriods">
								<e2i2:img src="/alert_yellow_static.gif" alt="${moreInfoTitle}"
									onclick="javascript:showLineMessage(this,'${lineMessage}')" />
								<c:set var="lineErrorExists" value="true" />
							</html:messages></td>
							<td class="fixedColumn fixedColumn  ${lockedRow}" nowrap="nowrap"><c:forEach
									var="fgs" items="${fc.item.functionalGroups}" varStatus="count">
									<c:if test="${!empty fgs }">
										<c:set var="fgsString" value="${fgs.getName()}" />
										<c:if test="${count.index ne 0}">
											<c:out value=", "></c:out>
										</c:if>
										<c:out value="${fgsString}"></c:out>
									</c:if>
								</c:forEach></td>
							<td class="fixedColumn ${lockedRow}" nowrap="nowrap"><c:forEach
									var="fgs" items="${fc.item.functionalGroups}" varStatus="count">
									<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}">
										<c:if test="${!empty pfg}">
											<c:set var="pfgString" value="${pfg.getName()}">
											</c:set>
											<c:if test="${count.index ne 0}">
												<c:out value=", "></c:out>
											</c:if>
											<c:out value="${pfgString}"></c:out>
										</c:if>
									</c:forEach>
								</c:forEach></td>
						<c:forEach var="attributeDefn" items = "${flexAttributeDefnListForecast}">
						<td class="fixedColumn ${lockedRow}"><c:out
								value="${fc[attributeDefn.associatedAttribute]}" /></td>
					   </c:forEach>
						<c:if test="${param.forecastModel==forecastModelCurrent}">
							<c:set var="fcValueMap" value="${fc.forecastValuesByPeriod}" />
							<c:forEach var="period" items="${timeLine.periods}"
								varStatus="periodCount">
								<td class="eto-grid-edit-cell compact ${lockedRow}" 
									${lockedRow=='lockedRow'?'style="border-bottom: none;"':''}>
									<c:set var="forecastValueMeasureKey" value="ACTUALFORECAST" />
									<c:set var="fpv"
										value="${fcValueMap[period.startDate][forecastValueMeasureKey]}" />
									<c:set var="fcvalue" value="${fpv.calculatedForecastValue}" />
									<c:set var="fpvKey"
										value="${period.startDate.time}.${forecastValueMeasureKey}" />
									<c:set var="fpvState" value="${fpv.savedStates}" /> <fmt:formatNumber
										maxFractionDigits="6" minFractionDigits="1"
										groupingUsed="false" value="${fpv.calculatedForecastValue}"
										var="fcvalue" /> 
										<!-- To  display current record--> <span
									style="white-space: nowrap;"> <fmt:formatNumber
											maxFractionDigits="6" minFractionDigits="1"
											groupingUsed="false" value="${fpv.calculatedForecastValue}"
											var="fcvalue" />
										 <c:choose>
											<c:when test="${period.state != 'PAST'}">
												<html:text styleClass="inputField forecastValue" size="8"
													styleId="forecastValue_${rowId}_${periodCount.index}"
													onfocus="this.oldvalue = this.value" style="width:60%;"
													onchange="handleDataChangedForField(this)"
													disabled="${lineReadOnly}"
													onblur="validateForecastValue(this)"
													errorStyle="background-color:red"
													property="forecastData(${fc.forecastExternalId}).forecastValues(${period.startDate.time}).simpleMeasureValue(${forecastValueMeasureKey}).forecastValue" />

												<button type="button" class="eto-icon-btn"
													title="pushdownTitle"
													style="margin: 0px"
													onclick="javascript:handlePushValueDown(${rowCount.index},'forecastValue_${modelABR}_{0}_${periodCount.index}')">
													<i class="md-icon">arrow_downward</i>
												</button>
												<button type="button" class="eto-icon-btn"
													title="pushrightTitle"
													style="margin: 0px"
													onclick="javascript:handlePushValueRight(${periodCount.index},'forecastValue_${rowId}_{0}')">
													<i class="md-icon">arrow_forward</i>
												</button>
												<html:messages id="lineMessage"
													property="forecastDataPeriodValue(${fc.forecastExternalId}.${period.startDate.time}.${forecastValueMeasureKey}).forecastValue">
													<e2i2:img src="/alert_yellow_static.gif"
														alt="${moreInfoTitle}"
														onclick="javascript:showLineMessage(this,'${lineMessage}')" />
													<c:set var="lineErrorExists" value="true" />
												</html:messages>
											</c:when>
											<c:otherwise>
									${fcvalue}
								</c:otherwise>
										</c:choose></span>
										</td>
							</c:forEach>
						</c:if>
						<c:if test="${param.forecastModel==forecastModelAdj}">
							<td
								class="fixedColumn compact ${lockedRow} eto-grid-edit-cell">

								<div class="eto-select" id="select${fc.forecastKey}">
									<div class="eto-select__container">
										<div class="eto-select__field-container">
											<select id="rowAdjustmentType_${rowId}"
												name="forecastData(${fc.forecastExternalId}).rowAdjustmentType"
												class="eto-select__field"
												onfocus="this.oldvalue = this.value"
												onchange="getCalculatedValueForAdjustmentType('${rowId}',this);handleDataChanged();"
												${lineReadOnly?'disabled':''}>
												<option value="FIXED">${param.fixedDisplay}</option>
												<option value="PERCENT">${param.percentDisplay}</option>
											</select>
										</div>
									</div>
								</div>
							</td>
							<td class="fixedColumn compact ${lockedRow}" style="text-align: right;"
								nowrap="nowrap">
								<div class="${itemDetailsExpanded?'adjshown':'adjhidden'}"
									name="adjustment${fc.forecastExternalId}">
									<fmt:message key="fc.adjustable.value" />
								</div>
							</td>
							<c:set var="fcValueMap" value="${fc.forecastValuesByPeriod}" />
							<c:forEach var="period" items="${timeLine.periods}"
								varStatus="periodCount">
								<td class="${lockedRow}"><c:set
										var="forecastValueMeasureKey" value="ACTUALFORECAST" /> <c:set
										var="fpv"
										value="${fcValueMap[period.startDate][forecastValueMeasureKey]}" />
									<c:set var="fcvalue" value="${fpv.calculatedForecastValue}" />
									<c:set var="fpvKey"
										value="${period.startDate.time}.${forecastValueMeasureKey}" />
									<c:set var="fpvState" value="${fpv.savedStates}" /> <c:set
										var="adjustableValue" value="${fpv.adjustableValue}" /> <c:set
										var="adjustmentAmount" value="${fpv.adjustmentAmount}" /> <c:choose>
										<c:when
											test="${period.state != 'PAST' && period.state != 'CURRENT'}">
											<c:if test="${!empty adjustableValue}">
												<div id="adjustableValue_${rowId}_${periodCount.index}">
													${adjustableValue}</div>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:if test="${!empty adjustableValue}">
												<div id="adjustableValue_${rowId}_${periodCount.index}">
													${adjustableValue}</div>
											</c:if>
										</c:otherwise>
									</c:choose></td>
							</c:forEach>
						</c:if>
					</tr>
					<c:if test="${param.forecastModel==forecastModelAdj}">
						<tr data-depth="1" class="hiddenRow">
							<td class="fixedColumn eto-grid-row-drilldown">
								<div class="eto-grid-row-drilldown-indent"></div>
								<div class="eto-grid-row-drilldown-toggle"></div>
							</td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn hideableColumn"></td>
							<td class="fixedColumn hideableColumn"></td>
							<td class="fixedColumn hideableColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn" style="text-align: right;"><fmt:message key="fc.adjustment.amount" /></td>
							<td class="fixedColumn"></td>
							<c:forEach var="period" items="${timeLine.periods}"
								varStatus="periodCount">
								<c:set var="forecastValueMeasureKey" value="ACTUALFORECAST" />
								<c:set var="fpv"
									value="${fcValueMap[period.startDate][forecastValueMeasureKey]}" />
								<c:set var="fcvalue" value="${fpv.calculatedForecastValue}" />
								<c:set var="fpvKey"
									value="${period.startDate.time}.${forecastValueMeasureKey}" />
								<c:set var="fpvState" value="${fpv.savedStates}" />
								<td class="compact ${lockedRow=='lockedRow'? '':'eto-grid-edit-cell '} ${lockedRow}"
									${lockedRow=='lockedRow'? 'style="border-bottom: none;"':''}>
									<c:set var="adjustableValue" value="${fpv.adjustableValue}" />
									<c:set var="adjustmentAmount" value="${fpv.adjustmentAmount}" />
									<c:choose>
										<c:when
											test="${period.state != 'PAST' && period.state != 'CURRENT'}">
											<c:if test="${!empty adjustableValue}">
												<div class="row">
													<div class="col-sm-8">
														<html:text styleClass="inputField forecastValue" size="8"
															styleId="adjustmentAmount_${rowId}_${periodCount.index}"
															onfocus="this.oldvalue = this.value"
															onkeyup="checkAllowedDecimals(this);getCalculatedValueForAdjustmentAmountAndType('#adjustableValue_${rowId}_${periodCount.index}',
											'#adjustmentAmount_${rowId}_${periodCount.index}','#calculatedVal_${period.state}_${rowId}_${periodCount.index}',
											'#rowAdjustmentType_${rowId}',this);handleDataChangedForField(this);"
															disabled="${lineReadOnly}"
															errorStyle="background-color:red"
															property="forecastData(${fc.forecastExternalId}).forecastValues(${period.startDate.time}).adjustableMeasureValue(${forecastValueMeasureKey}).adjustmentAmount" />
														<html:hidden
															styleId="adjustmentType_${rowId}_${periodCount.index}"
															property="forecastData(${fc.forecastExternalId}).forecastValues(${period.startDate.time}).adjustableMeasureValue(${forecastValueMeasureKey}).adjustmentType" />
													</div>
													<div class="col-sm-4" style="padding-top: 1rem">
														<label
															id="adjustmentType_label_${rowId}_${periodCount.index}"
															for="adjustmentType_${rowId}_${periodCount.index}">
															<logic:equal name="forecastForm"
																property="forecastData(${fc.forecastExternalId}).forecastValues(${period.startDate.time}).adjustableMeasureValue(${forecastValueMeasureKey}).adjustmentType"
																value="PERCENT">
											${param.percentDisplay}
									</logic:equal> <logic:notEqual name="forecastForm"
																property="forecastData(${fc.forecastExternalId}).forecastValues(${period.startDate.time}).adjustableMeasureValue(${forecastValueMeasureKey}).adjustmentType"
																value="PERCENT">
											${param.fixedDisplay}
									</logic:notEqual>
														</label>
														<c:if test="${!lineReadOnly}">
															<e2i2:img src="/transfer_down.gif" alt="${pushdownTitle}"
																width="10px"
																onclick="javascript:handlePushValueDown(${rowCount.index},'adjustmentAmount_${modelABR}_{0}_${periodCount.index}')" />
															<e2i2:img src="/transfer_right.gif"
																alt="${pushacrossTitle}" width="10px"
																onclick="javascript:handlePushValueRight(${periodCount.index},'adjustmentAmount_${rowId}_{0}');getCalculatedValueForAdjustmentType('${rowId}',this);" />

														</c:if>
														<html:messages id="lineMessage"
															property="forecastDataPeriodValue(${fc.forecastExternalId}.${period.startDate.time}.${forecastValueMeasureKey}).adjustment">
															<e2i2:img src="/alert_yellow_static.gif"
																alt="${moreInfoTitle}"
																onclick="javascript:showLineMessage(this,'${lineMessage}')" />
															<c:set var="lineErrorExists" value="true" />
														</html:messages>
													</div>
												</div>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:if test="${!empty adjustableValue}">
												<c:choose>
													<c:when test="${!empty adjustmentAmount}">									
														<label style="padding-left: 1.5rem;">${adjustmentAmount}${fpv.adjustmentType.string}	</label>							
													</c:when>
													<c:otherwise>
														&nbsp;
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:otherwise>
									</c:choose>
								</td>
							</c:forEach>
						</tr>
						<tr data-depth="1" class="hiddenRow">
							<td class="fixedColumn eto-grid-row-drilldown">
								<div class="eto-grid-row-drilldown-indent"></div>
								<div class="eto-grid-row-drilldown-toggle"></div>
							</td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn hideableColumn"></td>
							<td class="fixedColumn hideableColumn"></td>
							<td class="fixedColumn hideableColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn"></td>
							<td class="fixedColumn fixedColumn" style="text-align: right;"><fmt:message key="fc.calculated.value" /></td>
							<c:forEach var="period" items="${timeLine.periods}"
								varStatus="periodCount">
								<c:set var="forecastValueMeasureKey" value="ACTUALFORECAST" />
								<c:set var="fpv"
									value="${fcValueMap[period.startDate][forecastValueMeasureKey]}" />
								<c:set var="fcvalue" value="${fpv.calculatedForecastValue}" />
								<c:set var="fpvKey"
									value="${period.startDate.time}.${forecastValueMeasureKey}" />
								<c:set var="fpvState" value="${fpv.savedStates}" />
								<td class="compact"><c:choose>
										<c:when test="${param.forecastModel==forecastModelAdj}">
											<c:set var="adjustableValue" value="${fpv.adjustableValue}" />
											<c:set var="adjustmentAmount" value="${fpv.adjustmentAmount}" />
											<div class="row">
												<c:if test="${!empty adjustableValue}">
													<c:choose>
														<c:when
															test="${period.state != 'PAST' && period.state != 'CURRENT'}">
															<div class="col-sm-8"
																id="calculatedVal_${period.state}_${rowId}_${periodCount.index}">
																${fcvalue}</div>
														</c:when>
														<c:otherwise>
															<div class="col-sm-8"
																id="calculatedVal_${period.state}_${rowId}_${periodCount.index}">
																${fcvalue}</div>
														</c:otherwise>
													</c:choose>
													<c:if test="${!empty fpvState }">
														<div class="col-sm-4">
															<script type="text/javascript">
                            									 fvStates${modelABR}[${rowCount.index}][${periodCount.index}]=${fpvState}["lav"];
                        										   </script>
															<e2i2:img src="/information.png" width="10px"
																onclick="getLastApprovedValue(fvStates${modelABR}[${rowCount.index}][${periodCount.index}])" />
														</div>
													</c:if>
												</c:if>
											</div>
										</c:when>
									</c:choose></td>
							</c:forEach>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
<script>
/* $(window).load(function () {
   	handleGridScrolls();
}); */
var currentgrid = "";
var adjustableGrid = "";
<c:if test='${param.forecastModel==forecastModelAdj}'>
adjustableGrid= new eto.Grid({ el: document.querySelector('#grid_forecastModelAdj') });
$("#fcTableADJ_data").attr('style', '');
adjustableGrid.alignRows();

</c:if>
<c:if test='${param.forecastModel==forecastModelCurrent}'>
currentgrid = new eto.Grid({ el: document.querySelector('#grid_forecastModelCurrent') });
currentgrid.alignRows();
</c:if>
new eto.CheckboxMenu({
	el : document.querySelector('#checkbox-menu-example')
});
document
		.querySelector('#grid-compact-control input')
		.addEventListener(
				'change',
				function(e) {
					if (e.target.checked) {
						$("#grid_forecastModelCurrent").addClass('eto-grid--compact');
						$("#grid_forecastModelAdj").addClass('eto-grid--compact');
						adjustableGrid.alignRows();
						currentgrid.alignRows();
					} else {
						$("#grid_forecastModelCurrent").removeClass('eto-grid--compact');
						$("#grid_forecastModelAdj").removeClass('eto-grid--compact');
						adjustableGrid.alignRows();
						currentgrid.alignRows();
					}
				});
/* 
function handleGridScrolls(){
	var gridWidth=0;
 	<c:if test='${param.forecastModel==forecastModelCurrent}'>
	var scrollWidth=$(".eto-grid .eto-grid-scroll table").width();
	if(scrollWidth==null || scrollWidth==0){
		var scrollWidth=$(".eto-grid .eto-grid-scroll table#fcTableCUR_data").width();
	}
	</c:if>
	<c:if test='${param.forecastModel==forecastModelAdj}'>
	var scrollWidth=$(".eto-grid .eto-grid-scroll table").width();
	if(scrollWidth==null || scrollWidth==0){
		var scrollWidth=$(".eto-grid .eto-grid-scroll table#fcTableADJ_data").width();
	}
	</c:if>
		frozenWidth= 0 ;
	gridWidth = parseInt(scrollWidth);
	var contentWidth=$('body').width();
	if(gridWidth>contentWidth){
		gridWidth+=60;
		$('#scroller').show();
		$('#staticdiv').css('width',gridWidth+'px');
	   	$('#scroller').scroll(function(){
			$('.eto-grid-scroll').scrollLeft($(this).scrollLeft());
		});
	}  else{
		$('#scroller').hide();
	}
	window.onscroll = function(ev) {			
		var offset=$('.eto-grid')[0].getBoundingClientRect().top;
		if(offset<0){
			 $(".eto-grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,'+(-offset)+'px,0px)','position':'relative','z-index':2,}); 
		} else {
			 $(".eto-grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,0px,0px)','position':'static','z-index':'auto'}); 
		}
	   
    };		
} */
 </script>