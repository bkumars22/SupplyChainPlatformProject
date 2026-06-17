<%@ include file="common.jspf"%>

<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" />
<e2ot:help contextName="Price-Variance-Report" />
<title>Price Variance Report</title>
</head>

<body>
	<%-- <div class="margin-top-xs-1 margin-bottom-xs-1">
		<div style="display: flex;">
			<div style="margin: auto; align-items: center;">
				<div style="font-weight: bold;">
					<logic:messagesPresent message="true">
						<html:messages id="message" message="true">
							<li>${message}</li>
						</html:messages>
					</logic:messagesPresent>
				</div>
			</div>
		</div>
	</div> --%>

	<!--<form name="costRecordPriceVarianceForm" action="/costRecordPriceVarianceSearch" method="POST">-->
	<form name="costRecordPriceVarianceForm" action="costRecordPriceVarianceSearch" method="POST">
		<e2ot:searchContainerControl form="${costRecordPriceVarianceForm}"
			searchFields="${costRecordPriceVarianceForm.allParameters}"
			formName="costRecordPriceVarianceForm"
			showFilterCollapsed="${costRecordPriceVarianceForm.filterAreaCollapsed}"
			showFilter="${costRecordPriceVarianceForm.showFilterArea}"
			numColumns="3" />
	</form>

	<div class="container margin-top-xs-2">
		<h1>Cost Record Price Variance</h1>
		<label style="font-size: 14px;" class="margin-top-xs-2"><i>Please
				enter values into respective filters and click on Apply to download
				the report asynchronously</i></label>
	</div>

	<div class="margin-top-xs-3 margin-bottom-xs-3">
		<div style="display: flex;">
			<div style="margin: auto; align-items: center;">
				<div style="font-weight: bold;">
					<logic:messagesPresent message="true">
						<html:messages id="message" message="true">
							<li>${message}</li>
						</html:messages>
					</logic:messagesPresent>
				</div>
			</div>
		</div>
	</div>
</body>

<script type="text/javascript">
	$(document).ready(function() {
		$("div#expand-container").addClass("eto-expand--expanded");
	});

	parent.parent.reportCall = function() {
		<c:url var="linkHref" value="initReports.do">
		<c:param name="reportType" value="CostRecordPriceVariance" />
		</c:url>
		reloadBreadCrumb('initReports.do');
		document.forms[0].action = "${linkHref}";
		document.forms[0].submit();
		showWaitBusy();
	}
</script>
</html>