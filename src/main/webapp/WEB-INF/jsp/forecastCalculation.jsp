<%@ page import="java.io.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.test.repository.pcm.domain.PcmAdjustableForecastValue.AdjustmentType" %>

<%
try{
String calculatedValue = null;
BigDecimal adjValue=null;
BigDecimal adjAmt=null;
String adjustmentType = request.getParameter("adjType");
String adjustableValue = request.getParameter("adjVal");
if(adjustableValue!=null){
    adjValue=new BigDecimal(adjustableValue);  
}
String adjustmentAmount = request.getParameter("adjAmt");
if(adjustmentAmount!=null){
    adjAmt=new BigDecimal(adjustmentAmount);  
}
if (adjustmentType != null) {
    AdjustmentType at = AdjustmentType.valueOf(adjustmentType);            
    BigDecimal calValue = at.getCalculator().getAdjustedForecastValue(adjValue, adjAmt);
    if(calValue!=null){
        calculatedValue= calValue.toString();
        System.out.println("calculatedValue="+calculatedValue);
    }
}
response.getWriter().write(calculatedValue);
}catch(Exception e){
    System.out.println("error while getting calcluated Value--"+e);
}







%>