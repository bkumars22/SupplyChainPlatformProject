<%@ tag display-name="eventActionSupport" 
	description="Provides line event support for a collection of records" small-icon="" %>
<%@ attribute name="stateModel" required="true"%>
<%@ attribute name="eventRecordId" fragment="true"%>
<%@ attribute name="selectedKeyFieldName"%>
<%@ attribute name="lineEvents" type="java.util.Map"%>
<%@ attribute name="eventSet" type="java.util.Set"%>
<%@ attribute name="eventRecords" type="java.util.Collection"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn" %>
<%@ variable name-given="eventRecord" %>


<c:if test="${!empty eventRecords}">

<c:set var="eventSet" value="${e2ofn:allValidEventsForList(stateModel,eventRecords)}" scope="page"/>
<c:set var="lineEvents" value="${e2ofn:allEventsForList(stateModel,eventRecords,appContext)}"/>
</c:if>

<c:if test="${empty selectedKeyFieldName}">
	<c:set var="selectedKeyFieldName" value="document.forms[0].selectedRecordKeys"/>
</c:if>

<script type="text/javascript">

var eventList = new Object();

<c:forEach var="lineEvent" items="${lineEvents}">
<c:set var="eventRecord" value="${lineEvent.key}"/>
eventList['<jsp:invoke fragment="eventRecordId"/>'] = '${lineEvent.value}';
</c:forEach>         

var operationList = new Object();
<c:forEach var="eventRecord" items="${eventRecords}">
operationList['<jsp:invoke fragment="eventRecordId"/>'] = {'deleteAllowed':${e2ofn:allowOperation(stateModel,eventRecord.status,'Delete')},'editAllowed':${e2ofn:allowOperation(stateModel,eventRecord.status,'Edit')}};
</c:forEach>

function handleLineEventButtons()
{
   var total = 0;
   var eventCount = new Object();   
<c:forEach var="validEvent" items="${eventSet}">
   eventCount['${validEvent}'] = 0;
</c:forEach>         

   var cbList = $(${selectedKeyFieldName});
   for (var idx=0; idx < cbList.length; idx++)
   {
      var cb = cbList[idx];

      if (cb.checked)
      {
         total += 1;
         key = cb.value;
         if (eventList[key] != null)
         {
<c:forEach var="validEvent" items="${eventSet}">
         eventCount['${validEvent.eventName}'] += eventList[key].indexOf('|${validEvent.eventName}|') > -1 ? 1:0;
</c:forEach>         
         }
      }      
   }
   disableLineEventButtons(eventCount,total);
}

function disableLineEventButtons(eventCount, total)
{
   var count = 0;
<c:forEach var="validEvent" items="${eventSet}">
   <c:if test="${validEvent.uiMultiTargetAllowed}">
  /*  setButtonEnabled('${validEvent.eventName}EventButton',!(total != eventCount['${validEvent.eventName}'] || total == 0)); */
  
   if(!(total != eventCount['${validEvent.eventName}'] || total == 0)){
	   $("#"+'${validEvent.eventName}EventButton').removeAttr("disabled");
   }
   else{
	   $("#"+'${validEvent.eventName}EventButton').attr('disabled','disabled');
   }
   </c:if>
   <c:if test="${!validEvent.uiMultiTargetAllowed}">
   /* setButtonEnabled('${validEvent.eventName}EventButton',(total == eventCount['${validEvent.eventName}'] && total == 1)); */
   
   if(total == eventCount['${validEvent.eventName}'] && total == 1){
	   $("#"+'${validEvent.eventName}EventButton').removeAttr("disabled");
   }
   else{
	   $("#"+'${validEvent.eventName}EventButton').attr('disabled','disabled');
   }
   </c:if>
</c:forEach>         
}

function disableAllLineEventButtons(flag)
{
<c:forEach var="validEvent" items="${eventSet}">
   setButtonEnabled('${validEvent.eventName}EventButton',!flag);
</c:forEach>         
}

function getLineOperation(lineId)
{
	return operationList[lineId];
}

function getSelectedOperationCounts()
{
	var opCount = new Object();
	opCount.Delete=0;
	opCount.Edit=0;

	var cbList = $(${selectedKeyFieldName}).filter('input:checked');
    $(cbList).each(function() { 
        var op = operationList[this.value];
        if (op != null)
        {
       		opCount.Delete += (op.deleteAllowed) ? 1:0;  
       		opCount.Edit += (op.editAllowed) ? 1:0;
        }
        });
    opCount.TotalChecked = cbList.length;
    return opCount;			
}

</script>
