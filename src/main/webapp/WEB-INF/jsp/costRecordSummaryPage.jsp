<%@ include file="common.jspf"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeDefn"%>
<%@ page import="com.scplatform.pcm.common.entity.FlexAttributeManager"%>
<%@page import="java.util.ArrayList"%>
<%@ page import="com.scplatform.pcm.cost.entity.PcmCostRecord" %>

<e2i2:doctype/>
<e2i2:skin/>
<e2i2:preferences/>
<e2i2:clientcache/>


<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true"/>
<e2ot:help contextName="Cost-SearchDetail"/>
<c:set var='maxFractionDigits'
	value='${e2ofn:getConfigValue("pcm.costrecord.maxFractionDigits")}' />
	<c:set var='minFractionDigits'
	value='${e2ofn:getConfigValue("pcm.costrecord.minFractionDigits")}' />
	<c:set var='isCurrencyConversionEnable' value="${e2ofn:getConfigValue('pcm.feature.enable.cr.currency.conversion')}"/>
<script>
	var screenName = "searchCostRecord";
function showLineMessage(line,message)
{
    var title = "<fmt:message key="msg.info"/>";
    showModalMessageBox(title,message);
}

function goAsynchronusSearch(){
	document.forms[0].action = "asynhronusSearchForCostRecord.do";
	document.forms[0].submit();
	showWaitBusy();
 }
function callOnChange(field){
	if(field.name=='value(owner)'){
		if(field.value=='REGIONAL'){
			document.getElementsByName("values(regions)")[0].disabled = false;
		}else{
			hideElement();
		}
	}
}

function hideElement(){
	if(document.getElementsByName("value(owner)")[0].value!='' && document.getElementsByName("value(owner)")[0].value!='REGIONAL'){
		document.getElementsByName("values(regions)")[0].disabled = true;
	}
	$("#RejectEventButton").attr("disabled", "disabled");
	$("#CloseEventButton").attr("disabled", "disabled");
	$("#ApproveEventButton").attr("disabled", "disabled");
	$("#SubmitEventButton").attr("disabled", "disabled");
}

function goEvent(eventName,promptName, ackMessage)
{
    if (promptName != null && promptName.length > 0)
    {
       var result = showInputBox('OK','QUESTION',
                 promptName,promptName);
       if (result == '' || result == undefined)
       {
           showMessageBox('OK','ERROR',
               "errors.field_required",
               "msg.error");
           return;
       }
       document.forms[0].lineEventMessage.value = result;
    }
	$("input[name='selectedPageKeys']:checked").each ( function() {
   		  $(this).val($(this).val().split(',')[3]);
 	  });
    document.forms[0].clearSelection.value = true;
    document.forms[0].lineEvent.value = eventName;
    document.forms[0].action="processEventCostRecordSummary.do";
	document.forms[0].submit();
    showBusy();
    if (ackMessage != null && ackMessage.length > 0)
    {
        showMessageBox('OK','INFO',
               ackMessage,
               'msg.info');
    }
}

function goViewLane(slkey,crkey,type)
{
	showWaitBusy();
	document.forms['costRecordSummaryForm'].action="viewCostRecord";
	document.forms['costRecordSummaryForm'].selectedLaneKey.value = slkey;
	document.forms['costRecordSummaryForm'].scrollToRecord.value = crkey;
	document.forms['costRecordSummaryForm'].selectCostRecordType.value = type;
	document.forms['costRecordSummaryForm'].backAction.value = "submitCostRecordSearch";
	document.forms['costRecordSummaryForm'].submit();
}

function init()
{
	hideElement();
	disableAllLineEventButtons(true);
	resizeResultArea();
	setupHideColumnState('crSummarySearchResultTable',true);
	setupOrderColumnState('crSummarySearchResultTable',true,
			{mouseOverTitle:'<fmt:message key="label.moveColumn"/>'});

}

function getCancle(){
	showWaitBusy();
	document.costRecordSummaryForm.action = "home.do";
	document.costRecordSummaryForm.submit();
}

$(document).ready(function() {
	var srchResId = document.getElementById("crSummarySearchResultTable_globalrowselector");
	if(srchResId != null)
	document.getElementById("crSummarySearchResultTable_globalrowselector").disabled = false;
	disableUndefinedAction();
});

</script>
</head>
<body onload="init()" style="overflow-y : scroll;">
<%
 	ArrayList<FlexAttributeDefn> flexAttributeDefns = new ArrayList<FlexAttributeDefn>(FlexAttributeManager.COST.getFlexAttributeDefinitionList());
 	pageContext.setAttribute("flex", flexAttributeDefns);
%>
<e2ot:eventActionSupport stateModel="Sourcing"
    eventSet="${eventSet}"
    selectedKeyFieldName="document.forms[0].selectedPageKeys"
    lineEvents="${lineEvents}">
	<jsp:attribute name="eventRecordId">${eventRecord}</jsp:attribute>
</e2ot:eventActionSupport>
<form name="costRecordSummaryForm" action="submitCostRecordSearch" method="POST">
<input type="hidden" name="reportType" value="costRecord">
		<input type="hidden" name="lineEvent"/>
		<input type="hidden" name="lineEventMessage"/>
		<input type="hidden" name="selectedLaneKey"/>
		<input type="hidden" name="selectCostRecordType"/>
		<input type="hidden" name="scrollToRecord"/>
		<input type="hidden" name="selectedRecordKeys"/>
		<input type="hidden" name="backAction"/>
		<div style="font-weight: bold; white-space: pre-line;">
			<logic:messagesPresent message="true">
				<html:messages id="message" message="true">
					<li>${message}</li>
				</html:messages>
			</logic:messagesPresent>
		</div>
		<e2ot:searchContainerControl
			searchFields="${costRecordSummaryForm.allParameters}"
			formName="costRecordSummaryForm" form="${costRecordSummaryForm}"
			resultTableId="crSummarySearchResultTable"
			showFilterCollapsed="${costRecordSummaryForm.filterAreaCollapsed}"
			showFilter="${costRecordSummaryForm.showFilterArea}" numColumns="3" />
			<jsp:useBean id="eventSet" scope="page" class="java.util.HashSet" />
			<jsp:useBean id="lineEvents" scope="page" class="java.util.HashMap" />
			<script>
		var gridColumns = [];
		var error_msg_with_rowIndex = [];
		var jsonColumn =  '${costRecordSummaryForm.columns}';
		var gridRowExposedAction = true;
		var error = parent.$("#error-message-block").text();
    	if(error!=null && error!="") {
			gridColumns.push('<fmt:message key="cr.error.message"/>');
		}
		<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.costRecordSearch.grid.thead')}"/>
		<c:forEach var="col" items="${columnHeader}" varStatus="count">
	        <c:choose>
	            <c:when test="${col == 'cr.pricingScenario'}">
	                <c:if test="${e2ofn:getConfigValue('pcm.costRecord.pricing.scenario.enabled')}">
	                    gridColumns.push('<fmt:message key="cr.pricingScenario" />');
	                </c:if>
	            </c:when>
	            <c:when test="${col == 'cr.costRecordRange.from'}">
	                <c:if test="${e2ofn:getConfigValue('pcm.costRecord.pricing.scenario.enabled')}">
	                    gridColumns.push('<fmt:message key="cr.costRecordRange.from" />');
	                </c:if>
	            </c:when>
	            <c:when test="${col == 'cr.costRecordRange.to'}">
	                <c:if test="${e2ofn:getConfigValue('pcm.costRecord.pricing.scenario.enabled')}">
	                    gridColumns.push('<fmt:message key="cr.costRecordRange.to" />');
	                </c:if>
	            </c:when>
	            <c:when test="${col == 'cr.total.in.USD'}">
	                <c:if test="${isCurrencyConversionEnable}">
	                    gridColumns.push('<fmt:message key="cr.total.in.USD" />');
	                </c:if>
	            </c:when>
	            <c:when test="${col == 'cr.pcmCostElements'}">
	                <c:set var="cr" value="${costRecordSummaryForm.searchResult.values[0].values[1]}"/>
	                <c:forEach var="costValues" items="${costRecordSummaryForm.pcmCostElements}">
	                    gridColumns.push('${costValues.costElementName}');
	                </c:forEach>
	            </c:when>
	            <c:when test="${col == 'as.responsibility'}">
	                gridColumns.push('<fmt:message key="as.responsibility"/>'+'_EXPANDCELL');
	            </c:when>
	            <c:when test="${col == 'cr.reasonCode'}">
	                <c:if test="${e2ofn:getConfigValue('pcm.costRecord.reasonCodeEnabled')}">
	                    gridColumns.push('<fmt:message key="cr.reasonCode" />');
	                </c:if>
	            </c:when>
	            <c:otherwise>
	                gridColumns.push('<fmt:message key="${fn:trim(col)}"/>');
	            </c:otherwise>
	        </c:choose>
		</c:forEach>

		var lineErrorExists = false;
		var gridRows = [];
		<c:forEach var="row" items="${costRecordSummaryForm.searchResult.values}" begin="0" varStatus="rowCount">
			<c:set var="sl" value="${row.values[0]}"/>
			<c:set var="cr" value="${row.values[1]}"/>
			<c:set var="sup" value="${row.values[2]}"/>
			<c:set var="im" value="${row.values[3]}"/>
			<c:set var="cat" value="${row.values[4]}"/>
			<c:set var="fs" value="${row.values[5]}"/>
			<c:set var="ts" value="${row.values[6]}"/>
			<c:set var="xlobFGName" value="${row.values[7]}"/>
			<c:set var="fgPlatform" value="${row.values[8]}"/>
			<c:set var="lob" value="${row.values[9]}"/>
			<c:set var="isValidXLOB" value="${row.values[10]}"/>
			<c:set var="costType" value="${cr.costType}"/>
			<c:set var="eventSet" value="${e2ofn:addAll(eventSet,e2ofn:allValidEvents('Sourcing',cr))}"/>
			<c:set var="numranges" value="${fn:length(cr.costRecordRanges)}" />
			<c:set var="activeRangeKey" value="${cr.activeCostRecordRange.costRecordRangeKey}" />
		 var row = {};
		 <c:set var="canEditItem" value ="${e2ofn:canEditItem(appContext,im)}"/>
         <c:choose>
            <c:when test="${canEditItem}">
               row['checkboxValue']=  '${sl.sourcingLaneKey},${cr.costRecordExternalId},${cr.costType.costTypeKey},${cr.costRecordKey}';
            </c:when>
            <c:otherwise>
               row['checkboxValue']= '';
            </c:otherwise>
         </c:choose>
		 var spam = '<span id="span_${cr.costRecordExternalId}">';
		    	<c:set var="lineMessages" value="${costRecordSummaryForm.lineMessages}"/>
			    <c:set var="lineErrorExists" value="false"/>
			    <html:messages id="lineMessage" name="lineMessages" property="DATE|${cr.costRecordExternalId}">
			    spam += '<img src="skins/e2-modern/images/collab_problem.gif" alt="${moreInfoTitle}" onclick="javascript:showLineMessage(this,\'${lineMessage}\')"/>';
			    if(!lineErrorExists){
					lineErrorExists = true;
				}
			    </html:messages>
			    <html:messages id="lineMessage" name="lineMessages" property="AMOUNT|${cr.costRecordExternalId}">
			    spam += '<img src="skins/e2-modern/images/missing_price.gif"  alt="${moreInfoTitle}"  onclick="javascript:showLineMessage(this,\'${lineMessage}\')"/>';
			    if(!lineErrorExists){
					lineErrorExists = true;
				}
			    </html:messages>
			    <html:messages id="lineMessage" name="lineMessages" property="GENERAL|${cr.costRecordExternalId}">
			    spam += '<img src="skins/e2-modern/images/alert_yellow_static.gif" style="padding-right:0.25;" alt="${moreInfoTitle}" onclick="javascript:showLineMessage(this,\'${lineMessage}\')"/>';
			    spam += '';
			    if(!lineErrorExists){
					lineErrorExists = true;
				}
			    </html:messages>
			    <html:messages id="lineMessage" name="lineMessages" property="CORRECTION|${cr.costRecordExternalId}">
			    spam += '<img src="skins/e2-modern/images/alert_green_static.gif" alt="${moreInfoTitle}" onclick="javascript:showLineMessage(this,\'${lineMessage}\')"/>';
			    if(!lineErrorExists){
					lineErrorExists = true;
				}
		       </html:messages>
		       if(error!=null && error!=""){
			    	spam += '</span>';
					row['<fmt:message key="cr.error.message"/>']= spam;
				 }
				/* if(!lineErrorExists){
					var index=gridColumns.indexOf('<fmt:message key="cr.error.message"/>');
					if(index>-1)
					gridColumns.splice(index, 1);
				} */
		row['<fmt:message key="cr.status"/>'] = '<c:out value="${cr.status}" />';
		row['<fmt:message key="item.categoryName"/>'] = '<c:out value="${cat}" />';
		var itemNumberLink = '<a href="#" onClick="openPopOver(\'${im.itemKey}\');" data-popover="#item-popover" aria-haspopup="true" aria-controls="#item-popover"><e2ofn:escapePrint value="${im.itemNumber}" removeColon="true"/></a>';
		row['<fmt:message key="sl.item"/>'] = itemNumberLink;
		row['<fmt:message key="item.itemDescription"/>'] = '<c:out value="${im.description}" />';
		row['<fmt:message key="item.businessEntityIdentifier"/>'] = '<c:out value="${im.businessEntity.businessEntityIdentifier}" />';
		row['<fmt:message key="item.businessName"/>'] = '<c:out value="${im.businessEntity.businessEntityName}" />';
		row['<fmt:message key="sl.supplier"/>'] = '<c:out value="${sup}" />';
		row['<fmt:message key="sl.fromSite"/>'] = '<c:out value="${fs}" />';
		var mrpSite= "";
		var mrpSiteVal = '<c:out value="${sl.toSite.siteDetail.mrpSite }" />';
		if((mrpSiteVal!=null) && (mrpSiteVal!='') && (mrpSiteVal!='null')){
			mrpSite = mrpSiteVal;
		}
		row['<fmt:message key="sl.mrpSite"/>'] = mrpSite;
		row['<fmt:message key="sl.toSite"/>'] = '<c:out value="${ts}" />';
		row['<fmt:message key="cr.costType"/>'] ='<c:out value="${cr.costType.costTypeName}" />';
		<c:if test="${e2ofn:getConfigValue('pcm.costRecord.pricing.scenario.enabled')}" >
		row['<fmt:message key="cr.pricingScenario"/>'] = '${cr.pricingScenario}';
		</c:if>
		row['<fmt:message key="cr.fromDate"/>'] ='<fmt:formatDate value="${cr.effectiveFromDt}" pattern="${appContext.currentDateFormat}"/>	';
		row['<fmt:message key="cr.toDate"/>'] = '<fmt:formatDate value="${cr.effectiveToDt}" pattern="${appContext.currentDateFormat}"/>';
		row['<fmt:message key="sl.currency"/>'] = '${sl.currencyCode}';
		<c:forEach var="costRecordRange" items="${cr.costRecordRanges}" begin="0" end="0">
		  <c:if test="${e2ofn:getConfigValue('pcm.costRecord.pricing.scenario.enabled')}" >
      			<c:choose>
	        			<c:when test="${cr.rangeBased}">
			        		<c:if test="${activeRangeKey == costRecordRange.costRecordRangeKey}">*</c:if>
			        		row['<fmt:message key="cr.costRecordRange.from" />']= '${costRecordRange.fromRange}';
			        		row['<fmt:message key="cr.costRecordRange.to" />'] = '${costRecordRange.toRange}';
		 			  </c:when>
		    		<c:otherwise>
		    		  row['<fmt:message key="cr.costRecordRange.from" />'] = '';
		   			  row['<fmt:message key="cr.costRecordRange.to" />'] = '';
		   			</c:otherwise>
				</c:choose>
			</c:if>
	   		row['<fmt:message key="cr.costTotal"/>'] = '${e2ofn:formatCost(costRecordRange.computedTotalNotOfCostElementTypeFixed, maxFractionDigits, minFractionDigits)}';
     		 <c:if test="${isCurrencyConversionEnable}">
                  <c:choose>
                    <c:when test="${not empty cr.computedTotalNotOfCostElementTypeFixedInUSD}">
                       row['<fmt:message key="cr.total.in.USD"/>']='${e2ofn:formatCost(cr.computedTotalNotOfCostElementTypeFixedInUSD, maxFractionDigits, minFractionDigits)}';
                        </c:when>
                       <c:otherwise>
                              error_msg_with_rowIndex.push({
                                          rowIndex: ${rowCount.index},
                                          colIndex: gridColumns.indexOf('<fmt:message key="cr.total.in.USD"/>'),
                                          errorMsg: 'Currency Conversion Not Found',
                                          header : '<fmt:message key="cr.total.in.USD"/>'
                                  });
                         </c:otherwise>
                        </c:choose>
     			  	 </c:if>
     		<c:forEach var="costValueType" items="${costRecordSummaryForm.pcmCostElements}">
         		row['${costValueType.costElementName}'] = '${e2ofn:formatCost(costRecordRange.costRecordValues[costValueType.id.costElementKey].costValue, maxFractionDigits, minFractionDigits)}';
     		 </c:forEach>
  </c:forEach>

	<c:if test="${!empty sl.productState}">
	row['<fmt:message key="sl.productState"/>'] ='<fmt:message key="sl.productState.${sl.productState}"/>';
	</c:if>

	<c:choose>
	<c:when test="${!empty sl.bom}">
	row['<fmt:message key="sl.nonManaged"/>'] = '<fmt:message key="info.yes"/>';
	</c:when>
	<c:otherwise>
	row['<fmt:message key="sl.nonManaged"/>'] = '<fmt:message key="info.no"/>';
	</c:otherwise>
	</c:choose>
	row['<fmt:message key="sl.name"/>'] = '<c:out value="${sl.sourcingLaneName}"/>';
	var responsibilities = [];
	<c:forEach var="ia" items="${sl.item.assignments}" varStatus="iacount">
	var responsiblity = "<c:out value='${ia.userId} - ${ia.responsibility}'/>";
	var region = "<c:out value='${ia.region}'/>";
	if (region) {
		responsiblity = responsiblity + '- ' + region;
	}
	responsibilities.push(responsiblity);
	</c:forEach>
	row['<fmt:message  key="as.responsibility"/>'] = responsibilities.join(';');

	 row['<fmt:message key="cr.description"/>'] = '<c:out value="${cr.description}"/>';
	 <c:if test="${e2ofn:getConfigValue('pcm.costRecord.reasonCodeEnabled')}">
		row['<fmt:message key="cr.reasonCode"/>'] = '<c:out value="${cr.reasonCode}"/>';
	</c:if>
	row['<fmt:message key="cr.systemAction"/>'] = '<c:out value="${cr.systemAction}"/>';
	row['<fmt:message key="cr.projectName"/>'] = '<c:out value="${cr.projectName}"/>';
	row['<fmt:message  key="item.dataSource"/>'] = '<c:out value="${sl.item.dataSource}"/>';

	var functionalGroupNames = [];
	<c:forEach var="fgs" items="${im.functionalGroups}" varStatus="count">
		<c:if test="${fgs.type == 'CFG'}">
			var functionalGroupName = '${fgs.name}';
			functionalGroupNames.push(functionalGroupName);
		</c:if>
	</c:forEach>
	row['<fmt:message  key="cr.functionalGroup"/>'] = functionalGroupNames
			.join(',');

	var parentFunctionalGroupNames = [];
	<c:forEach var="fgs" items="${im.functionalGroups}" varStatus="count">
		<c:forEach var='pfg' items="${fgs.parentFunctionalGroup}">
			<c:if test="${pfg.type == 'CFG'}">
				var parentFunctionalGroupName = '${pfg.name}';
				parentFunctionalGroupNames.push(parentFunctionalGroupName);
			</c:if>
		</c:forEach>
	</c:forEach>
	row['<fmt:message  key="cr.parentFunctionalGroup"/>'] = parentFunctionalGroupNames.join(',');
	row['<fmt:message  key="cr.createdBy"/>'] = '<c:out value="${cr.createdBy}"/>';
	row['<fmt:message  key="cr.lastUpdatedBy"/>'] = '<c:out value="${cr.lastUpdatedBy}"/>';

	row['<fmt:message  key="cr.flex.xlob.platform"/>'] = '<c:out value="${fgPlatform}" />';
	row['<fmt:message  key="cr.flex.xlob.lob"/>'] = '<c:out value="${lob}" />';
	row['<fmt:message  key="cr.isValidXLOB"/>'] = '<c:out value="${isValidXLOB}" />';

	row['<fmt:message  key="cr.insertDate"/>'] = '<c:out value="${cr.insertDate}" />';
	row['<fmt:message  key="cr.lastRevChangeDate"/>'] = '<c:out value="${cr.lastRevChangeDate}" />';
	<c:set var="itemManagedFlag" value="${im.managedFlag}"/>
	<c:choose>
	    <c:when test="${!empty itemManagedFlag}">
	        row['<fmt:message key="mdm.managed.title" />'] = "<c:out value='${itemManagedFlag}'/>" + " Managed";
	    </c:when>
	    <c:otherwise>
	        row['<fmt:message key="mdm.managed.title" />'] = '<fmt:message key="mdm.managed.none"/>';
	    </c:otherwise>
	</c:choose>

	<c:forEach var="fl" items="${flex}" varStatus="count">
	<fmt:message key="flex.cost.${fl.associatedAttribute}" var="flexAttr"/>
	<c:if test="${fl.associatedAttribute == 'stringAttribute1'}">
		row['${flexAttr}']= '${cr.stringAttribute1}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute2'}">
		row['${flexAttr}']= '${cr.stringAttribute2}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute3'}">
		row['${flexAttr}']= '${cr.stringAttribute3}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute4'}">
		row['${flexAttr}']= '${cr.stringAttribute4}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute5'}">
		row['${flexAttr}']= '${cr.stringAttribute5}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute6'}">
		row['${flexAttr}']= '${cr.stringAttribute6}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute7'}">
		row['${flexAttr}']= '${cr.stringAttribute7}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute8'}">
		row['${flexAttr}']= '${cr.stringAttribute8}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute9'}">
		row['${flexAttr}']= '${cr.stringAttribute9}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'stringAttribute10'}">
		row['${flexAttr}']= '${cr.stringAttribute10}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute1'}">
		row['${flexAttr}'] = '${xlobFGName}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute2'}">
		row['${flexAttr}']= '${cr.numberAttribute2}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute3'}">
		row['${flexAttr}']= '${cr.numberAttribute3}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute4'}">
		row['${flexAttr}']= '${cr.numberAttribute4}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute5'}">
	row['${flexAttr}']= '${cr.numberAttribute5}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute6'}">
	row['${flexAttr}']= '${cr.numberAttribute6}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute7'}">
	row['${flexAttr}']= '${cr.numberAttribute7}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute8'}">
	row['${flexAttr}']= '${cr.numberAttribute8}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute9'}">
	row['${flexAttr}']= '${cr.numberAttribute9}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'numberAttribute10'}">
	row['${flexAttr}']= '${cr.numberAttribute10}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute1'}">
	row['${flexAttr}']='${cr.dateAttribute1}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute2'}">
	row['${flexAttr}']='${cr.dateAttribute2}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute3'}">
	row['${flexAttr}']='${cr.dateAttribute3}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute4'}">
	row['${flexAttr}']='${cr.dateAttribute4}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute5'}">
	row['${flexAttr}']='${cr.dateAttribute5}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute6'}">
	row['${flexAttr}']='${cr.dateAttribute6}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute7'}">
	row['${flexAttr}']='${cr.dateAttribute7}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute8'}">
	row['${flexAttr}']='${cr.dateAttribute8}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute9'}">
	row['${flexAttr}']='${cr.dateAttribute9}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'dateAttribute10'}">
	row['${flexAttr}']='${cr.dateAttribute10}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute1'}">
	row['${flexAttr}']='${cr.floatAttribute1}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute2'}">
	row['${flexAttr}']='${cr.floatAttribute2}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute3'}">
	row['${flexAttr}']='${cr.floatAttribute3}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute4'}">
	row['${flexAttr}']='${cr.floatAttribute4}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute5'}">
	row['${flexAttr}']='${cr.floatAttribute5}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute6'}">
	row['${flexAttr}']='${cr.floatAttribute6}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute7'}">
	row['${flexAttr}']='${cr.floatAttribute7}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute8'}">
	row['${flexAttr}']='${cr.floatAttribute8}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute9'}">
	row['${flexAttr}']='${cr.floatAttribute9}';
	</c:if>
	<c:if test="${fl.associatedAttribute == 'floatAttribute10'}">
	row['${flexAttr}']='${cr.floatAttribute10}';
	</c:if>
	</c:forEach>

	gridRows.push(row);
</c:forEach>
</script>
		<e2ot:searchResultsControl searchForm="${costRecordSummaryForm}"
			formName="costRecordSummaryForm"
			resultTableId="crSummarySearchResultTable" showOrderMenu="true"
			showHideMenu="true" title="Cost Records" />
		<c:forEach var="row"
			items="${costRecordSummaryForm.searchResult.values}"
			varStatus="rowCount">
			<c:set var="cr" value="${row.values[1]}" />
			<c:set target="${lineEvents}" property="${cr.costRecordKey}"
				value="${e2ofn:allEvents('Sourcing',cr,appContext)}" />
		</c:forEach>
		<input type="hidden" name="requestType"/>
        <input type="hidden" name="previousAction"/>
        <input type="hidden" name="nextAction"/>
		<input type="hidden" name="buttonAction" />
		<c:set var="hasEvents" value="false" />
		<c:if test="${not empty costRecordSummaryForm.searchResult.values}" >
		<div style="width: 50%" class="footer">
			<div class="eto-btn-group" style="padding: 10px;">
				<c:forEach var="pdcEvent" items="${eventSet}">
					<c:if
						test="${pdcEvent.uiMultiTargetAllowed && e2ofn:hasEventAccess(appContext, 'Sourcing' , pdcEvent.eventName)}">
						<button id="${pdcEvent.eventName}EventButton" type="button"
							class="eto-btn eto-btn--primary" style="margin: 4px;"
							onclick="javascript:goEvent('${pdcEvent.eventName}','${pdcEvent.uiPrompt}','${pdcEvent.uiAckMessage}');"
							  "? '':'disabled'}">
							<fmt:message key="${pdcEvent.uiButtonLabel}" />
						</button>
						<c:set var="hasEvents" value="true" />
					</c:if>
				</c:forEach>
			</div>
		</div>
		</c:if>
	</form>
<script type="text/javascript">
setGridEvents(grid);

function setGridEvents(grid){
if(grid != null){
	grid.on('rowAction', function() {
	     var args = arguments[1].split(",");
	     goViewLane(args[0],args[1],args[2]);
	});


grid.on("rowSelection", function(event) {
	var selectedPageKeys = document.getElementsByName("selectedPageKeys");
	var checked = false;
	for (var i = 0; i < selectedPageKeys.length; i++) {
		if (selectedPageKeys[i].checked) {
			checked = true;
			break;
		}
	}

	if (checked) {

		$("#RejectEventButton").removeAttr("disabled");
		$("#CloseEventButton").removeAttr("disabled");
		$("#ApproveEventButton").removeAttr("disabled");
		$("#SubmitEventButton").removeAttr("disabled");
	} else {

		$("#RejectEventButton").attr("disabled", "disabled");
		$("#CloseEventButton").attr("disabled", "disabled");
		$("#ApproveEventButton").attr("disabled", "disabled");
		$("#SubmitEventButton").attr("disabled", "disabled");
	}
});
}
}
parent.parent.reportCall = function() {
	<c:url var="linkHref" value="initReports.do">
	<c:param name="reportType" value="costRecord" />
	</c:url>
	reloadBreadCrumb('initReports.do');
	document.forms[0].action = "${linkHref}";
	document.forms[0].submit();
	}

function disableUndefinedAction() {
    setTimeout(function() {

       var checkBoxInputs = $('#grid-result .eto-grid .eto-grid-frozen table tbody tr input[type="checkbox"]');
       checkBoxInputs.each(function(index) {
			var checkboxValue = $(this).val();
			if(checkboxValue === undefined || checkboxValue === '' || checkboxValue === 'undefined') {
				$(this).prop('disabled', true);
			}
		});

        $('#grid-result .eto-grid .eto-grid-frozen table tbody tr input[type="checkbox"]:disabled')
		  .closest('tr')
		  .find('td:first div')
		  .html('<span class="md-icon" aria-hidden="true" title="Edit access denied" style="opacity: 0.4; cursor: not-allowed;">mode_edit</span>');
    }, 500);
}
</script>
</body>
</html>
