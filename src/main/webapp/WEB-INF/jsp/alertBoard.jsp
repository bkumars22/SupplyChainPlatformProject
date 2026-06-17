<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Review-Alerts" />
</head>
<style>
#alertDropdown {
	border-bottom: 1px #e5e5e5 solid;
	margin-bottom: 2rem;
}
</style>
<script>
$(document).ready(function() {
			var selectedItem = "";
			if("${alertForm.currentAlertTypeURL}" != "AlertSearch")
				{
						selectedItem = parent.mcmApp.selectedOption;
				}
			var alertHtml = '<fmt:message key="alert.Type" /><div class="eto-select" id="review-alerts" style="width: max-content;">'
			                + '<div class="eto-select__field-container" style="width: 300px;"><select id="selectDropdown" style="width:300px;margin-bottom:2rem;" onchange="changeFilter(this)">'
			                +'<c:forEach var="map" items="${alertForm.alertMap}"><option value="${map.key}"><c:choose><c:when test="${not empty map.key}"><fmt:message key="pcm.alertType.${map.key}" /></c:when><c:otherwise>${map.key}</c:otherwise></c:choose></option></c:forEach>'
			                +'</select> </div></div>';

			$("#alertDropdown").html(alertHtml);
			new eto.SelectInput({ el: document.querySelector('#selectDropdown') });

			var sel = document.getElementById("selectDropdown");
			 var opt;
		        for ( var i = 0, len = sel.options.length; i < len; i++ ) {
		            opt = sel.options[i];

		            if ( opt.value == selectedItem) {
		            	opt.selected = true;
		                break;
		            }
		        }
				var x = document.getElementById("footerId");
				var y = document.getElementById("grid-result");
				if (y == null) {
				    $("#footerId").css('display', 'none');
				}
		});

	function changeFilter(field)
	{
		if(field.value !="")
		{
		parent.mcmApp.selectedOption = field.value;
		document.forms[0].action = "/scplatform/AlertSearch?type=" + encodeURIComponent(field.value);
		document.forms[0].submit();
		showWaitBusy();
		}
		else
			{
					$('#search-Filters').css("display", "none");
			}
	}

	function goDelete() {
		if (validateLength(false)) {
			document.forms[0].action = "/scplatform/AlertSearch";
			document.forms[0].submit();
			showWaitBusy();
		}
	}

	function goDetails() {
		if (validateLength(true)) {
			document.forms[0].action = "/scplatform/AlertSearch";
			document.forms[0].submit();
			showWaitBusy();
		}
	}

	function validateLength(checkForMultiple) {
		var checkboxChecked = false;
		var count = 0;
		var checkb = document.getElementsByName("selectedPageKeys");
		for (var i = 0; i < checkb.length; i++) {
			if (checkb[i].checked) {
				count++;
				checkboxChecked = true;
				if(count > 1)
						break;
			}
		}

		if (!checkboxChecked) {
			showOkMessageBox('OK', 'WARN',
					"<fmt:message key='warn.select.alert'/>",
					"<fmt:message key='msg.warn'/>", function() {
					});
			return false;
		}

		if(checkForMultiple && count > 1){
			showOkMessageBox('OK', 'WARN',
					"<fmt:message key='warn.select.alert'/>",
					"Cannot select more than one record", function() {
					});
			return false;
		}
		return true;
	}

	function init() {
	}
</script>
<body>
	<e2o:form action="/scplatform/AlertSearch" method="POST">

		<script>
			var gridColumns = [];
			<c:set var="columnHeader" value="${e2ofn:getConfigValue('pcm.reviewalerts.grid.thead')}"/>
			<c:forEach var="col" items="${columnHeader}" varStatus="count">
			    <c:set var="configKey" value="pcm.reviewalerts.grid.display.column.${col}" />
			    <c:set var="configuredAlertTypes" value="${e2ofn:getConfigValue(configKey)}" />
			    <c:choose>
			        <c:when test="${col == 'alert.updateBy'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.updateBy" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.supplier'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.supplier" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.sourceSite'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.sourceSite" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.cost.startDate'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.cost.startDate" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.cost.endDate'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.cost.endDate" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.destinationSite'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.destinationSite" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.costType'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.costType" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.status'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.status" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.region'}">
			            <c:if test="${not empty configuredAlertTypes and (configuredAlertTypes.contains(alertForm.type))}">
			                gridColumns.push('<fmt:message key="alert.region" />');
			            </c:if>
			        </c:when>
			        <c:when test="${col == 'alert.longSummary'}">
			            gridColumns.push('<fmt:message key="alert.longSummary" />'+'_EXPANDCELL');
			        </c:when>
			        <c:otherwise>
			            gridColumns.push('<fmt:message key="${col}"/>');
			        </c:otherwise>
			    </c:choose>
			</c:forEach>

			var gridRows = [];
			<c:forEach var="row" items="${alertForm.searchResult.values}" varStatus="rowCount">
			<c:set var="a" value="${row.values[0]}" />
			var row = {};
			<c:choose>
				<c:when test="${a.alertType == 'CostPending' || a.alertType == 'CostChange'}">
					<%-- Extract costRecordKey from alertId format: alertType|userId|objectType|costRecordKey --%>
					<c:set var="alertIdParts" value="${fn:split(a.alertId, '|')}" />
					row['checkboxValue'] = '${a.id}~costRecordKey:${alertIdParts[3]}';
				</c:when>
				<c:otherwise>
					row['checkboxValue'] = '${a.id}~${a.stringAttribute1}';
				</c:otherwise>
			</c:choose>
			row['<fmt:message key="alert.shortSummary"/>'] = "${a.shortSummary}";
			row['<fmt:message key="alert.generation"/>'] = "${a.created}";
			row['<fmt:message key="alert.expirationDate"/>'] = "${a.expirationDate}";
			row['<fmt:message key="alert.item.number"/>'] = "${a.stringAttribute1}";
			row['<fmt:message key="alert.longSummary"/>'] = "${a.longSummary}";
			row['<fmt:message key="alert.updateBy"/>'] = "${a.stringAttribute9}";
			row['<fmt:message key="alert.supplier"/>'] = "${a.stringAttribute2}";
			row['<fmt:message key="alert.sourceSite"/>'] = "${a.stringAttribute3}";
			row['<fmt:message key="alert.cost.startDate"/>'] = "${a.dateAttribute1}";
			row['<fmt:message key="alert.cost.endDate"/>'] = "${a.dateAttribute2}";
			row['<fmt:message key="alert.destinationSite"/>'] = "${a.stringAttribute4}";
			row['<fmt:message key="alert.costType"/>'] = "${a.stringAttribute7}";
			row['<fmt:message key="alert.status"/>'] = "${a.stringAttribute8}";
			<c:if test="${(alertForm.type=='ForecastChange')}">
				row['<fmt:message key="alert.shortSummary"/>'] = "${a.stringAttribute4}";
			</c:if>
			gridRows.push(row);
			</c:forEach>
		</script>

		<div id="alertDropdown"></div>

		<c:if test="${not empty alertForm.searchResult.values}" >
				<div style="width: 50%" class="footer">
			<nav class="eto-form__btns" style="margin-left: 15px;">
				<div class="eto-btn-group" style="margin-top: 15px;">
					<button type="button" id="detailsButton"
						class="eto-btn eto-btn--primary" onclick="javascript:goDelete()">
						<bean:message key="button.dismiss.alert" />
					</button>
					<button type="button" id="deleteButton" class="eto-btn"
						onclick="javascript:goDetails();">
						<bean:message key="button.details.alert" />
					</button>
				</div>
			</nav>
		</div>
		</c:if>
	</e2o:form>
</body>
</html>
