<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="i2uitaglib.tld" prefix="i2" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.scplatform.testing.webui.taglib.Settings" %>

<i2:preferences/>

<%
  Settings settings = Settings.getSessionSettings(request);
  String id = request.getParameter("id");
  String refdate = request.getParameter("refdate");
  String earliestdate = request.getParameter("earliestdate");
  String latestdate = request.getParameter("latestdate");
  String localeStr = request.getParameter("locale");
  if (localeStr == null) localeStr = settings.getLocale().toString();
 %>

<HTML>
  <HEAD>
    <TITLE>i2ui Date Picker</TITLE>
    <i2:stylesheet path="/style_sheet_core.css" default="yes" inline="no"/>
    <i2:dhtml datepickersupport="yes" locale="<%= localeStr %>"/>
    <SCRIPT language="JavaScript">
    function init ()
    {
      var refdate = null;
      var earliestdate = null;
      var latestdate = null;
      var refdateStr = "<%= refdate %>";
      var earliestdateStr = "<%= earliestdate %>";
      var latestdateStr = "<%= latestdate %>";
      var tmp = 0;
      if (refdateStr != "null")
      {
        tmp = refdateStr.split('-');
        refdate = new Date(tmp[0], tmp[1], tmp[2]);
      }
      if (earliestdateStr != "null")
      {
        tmp = earliestdateStr.split('-');
        earliestdate = new Date(tmp[0], tmp[1], tmp[2]);
      }
      if (latestdateStr != "null")
      {
        tmp = latestdateStr.split('-');
        latestdate = new Date(tmp[0], tmp[1], tmp[2]);
      }
      i2uiShowDatePicker("<%= id %>", refdate, earliestdate, latestdate);
    }

    function myOkHandler ()
    {
      i2uiDatePickerClose(i2uiDatePickerSelectedDate);
    }

    function myCancelHandler ()
    {
      i2uiDatePickerClose(null);
    }
    </SCRIPT>
  </HEAD>
  <BODY SCROLL="no" ONLOAD="init()"
        STYLE="margin-top:0;margin-bottom:0;margin-left:0;margin-right:0;">
    <i2:datepicker id="<%= id %>" okcallback="myOkHandler" cancelcallback="myCancelHandler" dialog="yes" locale="<%= localeStr %>"/>
  </BODY>
</HTML>
