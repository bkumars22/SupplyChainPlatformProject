<%@ include file="../common.jspf"%>
<%@page import="java.util.ArrayList"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<style>

    body{
        overflow:scroll;
    }

</style>

<html>
<head> 
    <e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
    <e2ot:help contextName="No Responsibilty TAM Exception" />
    <script>
        function init() {

        }
        function goSearch() {
            document.forms[0].action = "submitNoResponsibilityTamException.do";
            document.forms[0].submit();
            showWaitBusy();
        }
        function goContinue() {
            $("#cheackRows").val(true);
            submitExtractToFile();
            showWaitBusy();
            new eto.Modal({
                el : document.querySelector('#NoRespnsibilityTam_exp_popup_modal')
            }).close();
        }
        function goOk() {
            $("#cheackRows").val(false);
            document.forms[0].action = "submitNoResponsibilityTamExceptionToReport.do";
            document.forms[0].submit();
            showWaitBusy();
        }
        $(document).ready(function() {
            var message='${noResponsibilityTAMExceptionForm.messagePopup}';
            if(message!=""){
                $("#errMessage").html(message);
                new eto.Modal({
                    el : document.querySelector('#NoRespnsibilityTam_exp_popup_modal')
                }).open();
            }
        });
        parent.parent.reportCall = function() {
            <c:url var="linkHref" value="initReports.do">
            <c:param name="reportType" value="noResponsibilityTamException" />
            </c:url>
            reloadBreadCrumb('initReports.do');
            document.forms[0].action="${linkHref}";
            document.forms[0].submit();
            showWaitBusy();
        }
    </script>
</head>
<body onload="init()">
<e2o:form action="/submitNoResponsibilityTamException"  method="POST" style="margin:0px,padding:0px">
    <div class="eto-modal" id="NoRespnsibilityTam_exp_popup_modal">
        <div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
            <header class="eto-modal__header">
                <span>Info</span>
                <button class="eto-modal__close" data-modal-close></button>
            </header>
            <section class="eto-modal__body" style="overflow: hidden;">
                <p id="errMessage" style="white-space: normal;">
                </p>
            </section>
            <footer class="eto-modal__footer">
                <button type="button" onclick="goOk();" class="eto-btn ">Yes</button>
                <button type="button" class="eto-btn" data-modal-close class="eto-btn eto-btn--primary" >No</button>

            </footer>
        </div>
    </div>
    <input type="hidden" name="cheackRows" id="cheackRows" value="${noResponsibilityTAMExceptionForm.checkRows}"/>
    <script>
        var jsonColumn =  '${noResponsibilityTAMExceptionForm.columns}';
        var gridColumns = [];
        var selectionType = 'none';
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.functionalGroupId"/>');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.functionalGroupName"/>');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.functionalGroupType"/>');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.item" />');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.itemDescription" />');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.commodityCode" />');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.itemEol" />');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.site" />');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.updateDate" />');
        gridColumns.push('<fmt:message key="noResponsibility.tam.exception.updateBy" />');

        var gridRows = [];
        <c:forEach var="row" items="${noResponsibilityTAMExceptionForm.searchResult.values}" varStatus="rowCount">
        var row = {};
        <c:set var="eolVal" value="${row.values[7]}"></c:set>
        <c:if test="${row.values[7] == null || row.values[7] == 'N'}">
         <c:set var="eolVal" value="false"></c:set>
        </c:if>
        row['<fmt:message key="noResponsibility.tam.exception.functionalGroupId"/>']	= '<c:out value="${row.values[1]}"/> ';
        row['<fmt:message key="noResponsibility.tam.exception.functionalGroupName"/>']	= '<c:out value="${row.values[2]}"/> ';
        row['<fmt:message key="noResponsibility.tam.exception.functionalGroupType"/>'] ='<c:out value="${row.values[3]}"/>';

        row['<fmt:message key="noResponsibility.tam.exception.item"/>'] ='<c:out value="${row.values[4]}"/>';
        row['<fmt:message key="noResponsibility.tam.exception.itemDescription"/>'] ='<c:out value="${row.values[5]}"/>';
        row['<fmt:message key="noResponsibility.tam.exception.commodityCode"/>'] ='<c:out value="${row.values[6]}"/>';
        row['<fmt:message key="noResponsibility.tam.exception.itemEol"/>'] ='<c:out value="${eolVal}"/>';
        row['<fmt:message key="noResponsibility.tam.exception.site"/>'] = '<c:out value="${row.values[8]}"/> ';
        row['<fmt:message key="noResponsibility.tam.exception.updateDate"/>'] ='<c:out value="${row.values[9]}" />';
        row['<fmt:message key="noResponsibility.tam.exception.updateBy"/>'] ='<c:out value="${row.values[10]}"/>';
        gridRows.push(row);
        </c:forEach>
    </script>
    <logic:messagesPresent message="true">
        <html:messages id="message" message="true">
            <li>${message}</li>
        </html:messages>
    </logic:messagesPresent>
    <input type="hidden" name="backAction" />
    <e2ot:searchContainerControl
            searchFields="${noResponsibilityTAMExceptionForm.allParameters}"
            formName="noResponsibilityTAMExceptionForm" form="${noResponsibilityTAMExceptionForm}"
            resultTableId="tamExceptionResultTable"
            showFilterCollapsed="${noResponsibilityTAMExceptionForm.filterAreaCollapsed}"
            showFilter="${noResponsibilityTAMExceptionForm.showFilterArea}" numColumns="3" />
    <e2ot:searchResultsControl searchForm="${noResponsibilityTAMExceptionForm}"
                               formName="noResponsibilityTAMExceptionForm"
                               resultTableId="tamExceptionResultTable" showOrderMenu="true"
                               showHideMenu="true" title="No Responsibilty TAM Exception" />
   <html:hidden property="requestType"/>
    <html:hidden property="previousAction"/>
    <html:hidden property="nextAction"/>
    <input type="hidden" name="buttonAction"/>
</e2o:form>
</body>
</html>