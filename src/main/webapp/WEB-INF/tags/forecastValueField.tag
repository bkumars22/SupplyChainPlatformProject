<%@ tag display-name="forecastValueTag" 
	description="Provides a data entry fields for the forecast value" small-icon="" %>
<%@ attribute name="fc" required="true" type="com.scplatform.repository.pcm.domain.PcmForecast"%>
<%@ attribute name="fpv" required="true" type="com.scplatform.repository.pcm.domain.PcmForecastValue"%>
<%@ attribute name="periodStartDate" required="true" type="java.util.Date"%>
<%@ attribute name="periodState" required="true" %>
<%@ attribute name="lineReadOnly" required="true" %>
<%@ attribute name="periodCountIndex" required="true" %>
<%@ attribute name="rowCountIndex" required="true" %>
<%@ attribute name="lineMessage" required="true" %>

<c:set var="forecastValueMeasureKey" value="ACTUALFORECAST"/>
<fmt:formatNumber maxFractionDigits="6" minFractionDigits="1" groupingUsed="false"
  value="${fpv.calculatedForecastValue}" var="fcvalue"/>
  <c:set var="fpvKey" value="${periodStartDate.time}.${forecastValueMeasureKey}"/>
<c:choose>
<c:when test="${periodState != 'PAST'}">
<html:text styleClass="inputField forecastValue" size="8"
    styleId="forecastValue_${periodCountIndex}_${rowCountIndex}"
	onchange="handleDataChanged()"
	disabled="${lineReadOnly}"
	onblur="validateForecastValue(this)"
    errorStyle="background-color:red"    
	property="forecastDataPeriodValue(${fc.forecastExternalId}.${periodStartDate.time}.${forecastValueMeasureKey})"/>
    <c:if test="${!lineReadOnly}">
    <e2i2:img src="/transfer_down.gif" alt="${pushdownTitle}"
         onclick="javascript:handlePushValueDown(${rowCountIndex},'forecastValue_${periodCountIndex}_{0}')"/>
    <e2i2:img src="/transfer_right.gif" alt="${pushacrossTitle}"
         onclick="javascript:handlePushValueRight(${periodCountIndex},'forecastValue_{0}_${rowCountIndex}')"/>
    </c:if>
	<html:messages id="lineMessage" property="forecastDataPeriodValue(${fc.forecastExternalId}.${periodStartDate.time}.${forecastValueMeasureKey})">        
		<e2i2:img src="/alert_yellow_static.gif" alt="${moreInfoTitle}"
 			onclick="javascript:showLineMessage(this,'${lineMessage}')"/>
		<c:set var="lineErrorExists" value="true"/>	   
	</html:messages>
	<fmt:formatNumber maxFractionDigits="6" minFractionDigits="1" groupingUsed="false"
  		value="${fpv.adjustableValue}" var="adjustableValue"/>
		<div class="adjhidden" name="adjustment${fc.forecastExternalId}">

			<table style="background: transparent;" cellpadding=""
				cellspacing="0">
				<tr>
					<td>Value</td>
					<td>${fpv.adjustableValue}</td>
				</tr>
				<tr>
					<td>Type</td>
					<td>${fpv.adjustmentType}</td>
				</tr>
				<tr>
					<td>Amount</td>
					<td>${fpv.adjustmentAmount}</td>
				</tr>
			</table>
		</div>
	</c:when>
<c:otherwise>
${fcvalue}
<div class="adjhidden" name="adjustment">
			<table style="background: transparent;" cellpadding=""
				cellspacing="0">
				<tr>
					<td>Value</td>
					<td>${fpv.adjustableValue}</td>
				</tr>
				<tr>
					<td>Type</td>
					<td>${fpv.adjustmentType}</td>
				</tr>
				<tr>
					<td>Amount</td>
					<td>${fpv.adjustmentAmount}</td>
				</tr>
			</table>
		</div>
	</c:otherwise>
</c:choose>