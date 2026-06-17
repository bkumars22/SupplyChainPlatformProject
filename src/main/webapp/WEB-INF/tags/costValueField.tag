<%@ tag display-name="costValueTag"
	description="Provides a data entry fields for the value, and optional value detail"
	small-icon=""%>
<%@ attribute name="costElementsDetails" required="true"
	type="java.util.Map"%>
<%@ attribute name="recordId" required="true"%>
<%@ attribute name="rangeKey" required="true"%>
<%@ attribute name="fieldId" required="true"%>
<%@ attribute name="costElementKey" required="true"%>
<%@ attribute name="costValueRequired" required="true"%> 
<%@ attribute name="status" required="true"%>
<%@ attribute name="value" required="true"
	type="com.scplatform.repository.pcm.domain.PcmCostRecordValue"%>
<%@ attribute name="readOnly" required="true"%>
<%@ attribute name="onchange" required="true"%>
<%@ attribute name="allowNegativeValues" required="true"%>
<%@ attribute name="isGTIE09" required="true"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>

<c:set var='maxFractionDigits'
	value='${e2ofn:getConfigValue("pcm.costrecord.maxFractionDigits")}' />
	<c:set var='minFractionDigits'
	value='${e2ofn:getConfigValue("pcm.costrecord.minFractionDigits")}' />
<fmt:setBundle basename="scplatform-messages" />
<c:set var="detailExists"
	value="${!empty costElementsDetails[costElementKey]}" />
<fmt:formatNumber maxFractionDigits="${maxFractionDigits}" minFractionDigits="${minFractionDigits}"
	groupingUsed="false" value="${value.costValue}" var="crvalue" />
<c:choose>
	<c:when test="${detailExists}">
<%-- 		<div style="display: none"
			ng-init="addCostValueDetail('${costElementKey}','${recordId}')"></div> --%>
		<div id="detailTable${fieldId}${costElementKey}"
			class="costElementDetailTable"
			style="background: transparent; width: max-content;min-width: 300px; max-width: 350px;">
			<c:set var="blendTotal" value="0.0" />
			<ul class="eto-tree-list" id="tree-${fieldId}${costElementKey}">
			<c:forEach var="detailType"
					items="${costElementsDetails[costElementKey]}">
					<fmt:formatNumber maxFractionDigits="${maxFractionDigits}" minFractionDigits="${minFractionDigits}"
						groupingUsed="false"
						value="${value.costValueDetails[detailType].costValueValue}"
						var="blendvalue" />
					<fmt:formatNumber maxFractionDigits="${maxFractionDigits}" minFractionDigits="${minFractionDigits}"
						groupingUsed="false"
						value="${value.costValueDetails[detailType].costValueBlend}"
						var="blend" />
						<c:set var="blendTotal" value="${blend + blendTotal}" />
						</c:forEach>
			<li data-depth="0" class="data-depth" style="${status =='APPROVED'?' background-color: transparent !important;':''}">
					<div class="eto-tree-list__toggle">
						<c:if test='${isGTIE09}'>
							<button type="button" style="margin-left:0.8rem;"
								class="eto-btn eto-btn--icon-only eto-btn--link"></button>
						</c:if>
					</div>
					<div class="eto-tree-list__content" style="padding: 0px;">
						<div style="display: inline-flex;width:100%;" >
							<div  class="col-lg-3" style="padding-top:1.5rem;">
								<fmt:message key="cr.costTotal" />
							</div>
							<div class="col-lg-4"
								style="margin: 0px; padding: 0px; border: none">
								<html:text value="${crvalue}" size="8"
									onblur="checkNumericField(this,${allowNegativeValues})"
									styleClass="inputField" readonly="true"
									style="text-align:right;background:none;border:none;clear:none;margin:0px;padding:0px;"
									styleId="costValue${fieldId}${costElementKey}"
									property="costData(${recordId}).rangeData(${rangeKey}).value(${costElementKey})" />
							</div>
							<div class="col-lg-4"
								style="padding: 0px; margin: 0px; border: none">
								<input type="text" readonly value="${blendTotal}"
									class="inputField" size="6"
									style="text-align:right;background:none;border:none;clear:none;margin:0px;padding:0px;"
									id="blendTotal${fieldId}${costElementKey}" />%
							</div>
							<div class="col-lg-1"></div>
						</div>
					</div>
				</li>
				<c:forEach var="detailType"
					items="${costElementsDetails[costElementKey]}">
					<fmt:formatNumber maxFractionDigits="${maxFractionDigits}" minFractionDigits="${minFractionDigits}"
						groupingUsed="false"
						value="${value.costValueDetails[detailType].costValueValue}"
						var="blendvalue" />
					<fmt:formatNumber maxFractionDigits="3" minFractionDigits="${minFractionDigits}"
						groupingUsed="false"
						value="${value.costValueDetails[detailType].costValueBlend}"
						var="blend" />
						<c:set var="blendFunc" value="" />
									<c:set var="amountFunc" value="" />
									<c:if test="${!readOnly}">
										<c:set var="blendFunc"
											value="calcBlends(this, '${costElementKey}',${fieldId});" />
										<c:set var="amountFunc"
											value="calcBlends(this, '${costElementKey}',${fieldId});" />
									</c:if>
					<li data-depth="1" class="data-depth-hidden hidden" style="${status =='APPROVED'?' background-color: transparent !important;':''}">
						<div class="eto-tree-list__indent"></div>
						<div class="eto-tree-list__content" style="padding: 0px;">
							<div style="display: inline-flex;width:100%;" >
								<div class="col-lg-3"
									style="margin: 0px; padding: 0px; border: none;padding-top:1.5rem;">
									<span>${detailType}</span>
								</div>
								<div class="col-lg-4"
									style="margin: 0px; padding: 0px; border: none;padding-right: 0.5rem;">
									<html:text value="${blendvalue}" size="8"
										onblur="${amountFunc}" styleClass="inputField"
										readonly="${readOnly}" styleId="value${fieldId}${detailType}"
										style="text-align:right;padding:0px;" onchange="${onchange}"
										titleKey="cr.blendAmountTitle"
										property="costData(${recordId}).rangeData(${rangeKey}).valueDetail(${costElementKey}.${detailType}).value" />
								</div>
								<div class="col-lg-4"
									style="padding: 0px; margin: 0px; padding: 0px; border: 0px;">
									<html:text value="${blend}" size="6" onblur="${blendFunc}" 
										styleClass="inputField" readonly="${readOnly}"
										styleId="blend${fieldId}${detailType}"
										style="text-align:right;padding:0px;" onchange="${onchange}"
										property="costData(${recordId}).rangeData(${rangeKey}).valueDetail(${costElementKey}.${detailType}).valueBlend"
										titleKey="cr.blendPercentTitle" />%
								</div>
								<div class="col-lg-1"></div>
							</div>
						</div>
					</li>
				</c:forEach>
			</ul>
			<script>
						new eto.TreeList({ el: document.querySelector('#tree-${fieldId}${costElementKey}') });
					
						</script>
		</div>

	</c:when>
	<c:otherwise>
		<div style="min-width: 200px">
			<div style="width: 90%; float: left;">
				<html:text value="${crvalue}" size="8"
					onblur="checkCostValue(this,${allowNegativeValues},${costValueRequired})"
					styleClass="inputField costValue${fieldId}_${costElementKey}"
					readonly="${readOnly}" onchange="${onchange}"
					styleId="costValue${fieldId}_rng${rangeKey}_${costElementKey}"
					property="costData(${recordId}).rangeData(${rangeKey}).value(${costElementKey})" />
			</div>
			<c:if test="${!readOnly}">
				<div style="width: 30%; padding-top: 10px;">
					<button type="button" class="eto-icon-btn" title="pushdownTitle"
						onclick="javascript:handlePushDownValue(${fieldId},'costValue{0}_rng${rangeKey}_${costElementKey}','inputField costValue{0}_${costElementKey}')">
						<i class="md-icon">arrow_drop_down</i>
					</button>
				</div>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>