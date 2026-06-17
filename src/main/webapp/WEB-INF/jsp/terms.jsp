<!--[if IE ]>
<!doctype html>
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<![endif]-->
<%@ include file="common.jspf"%>
<e2i2:skin name="e2-modern" />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="false" ajaxSupport="true" />
<script>
	function goSubmit() {
		var ackValue = $('input[name=agreement]:radio').val();
		document.forms[0].action = "ackTerms"
		document.forms[0].submit();
	}

	function setAckValue(value) {
		document.forms[0].ackValue.value = value;
		setButtonEnabled('submitButton', true);
	}

	function initPage() {
		setButtonEnabled('submitButton', false);
	}
</script>
<style>
.termsClause, .termsClause p, .termsClause li, .termsClause div {
	white-space: normal;
}
</style>
</head>
<body onload="initPage()">
	<form method="post" name="terms" class="termsClause">
		<input type="hidden" name="ackValue" value="" />
		<fmt:message var="termsTitle" key="terms.title" />
		<e2i2:container title="${termsTitle}">
			<e2i2:instructionsarea>
				<fmt:message key="terms.instructions" />
			</e2i2:instructionsarea>
			<div style="margin: 4px;">
				<jsp:include page="termsClause.jspf"></jsp:include>
			</div>
			<center>
				<input type="radio" name="agreement" value="yes"
					onclick="setAckValue(true)" />
				<fmt:message key="terms.accept" />
				<input type="radio" name="agreement" value="no" style="margin-left:2rem;"
					onclick="setAckValue(false)" />
				<fmt:message key="terms.decline" />
			</center>
			<e2i2:footer>
				<e2i2:buttonbar aligncontents="right">
					<button id="submitButton" type="button"
						class="eto-btn eto-btn--primary" onclick="javascript:goSubmit()"
						style="position: absolute;right: 5%">
						<fmt:message key="button.ok" />
					</button>
				</e2i2:buttonbar>
			</e2i2:footer>
		</e2i2:container>
	</form>
</body>
</html>