<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page import="com.scplatform.pcm.dashboard.dto.DashboardForm"%>
<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="false" />
<title>Dashboard</title>
<e2ot:help contextName="Dashboard" />
<c:set var='defaultDaySold'
	value='${e2ofn:getConfigValue("pcm.dashboard.default.daysold")}' />
<style type="text/css">
.scplatform-dashboard-card--selected {
	box-shadow: 0 0 0 .5rem #277ab5 !important;
}

#table-example-1 {
	width: 100%;
}

form {
	background: #e9f1f9
}

.vl {
	z-index: 1;
	width: 100%;
	flex-direction: column;
	align-items: flex-start;
}

.eto-card__body {
	overflow: hidden;
}

table.eto-table tr:nth-child(even) td {
	background-color: #e9ecec;
}

.dashboardHeadre {
	background-color: #fff;
	box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0, 0, 0.24);
	border-radius: 0.5rem;
	position:
}

.horizontal {
	background-color: black;
	border: 1px;;
	height: 1px;
	width: 100%;
}

#close {
	float: right;
	display: block;
	margin-right: 0px;
	clear: left;
}

.e2mc-dashboard__header {
	padding: 24px;
	background-color: #fff;
	box-shadow: 0 2px 4px 0 rgba(0, 0, 0, .24);
	display: flex;
}

.scplatform-dashboard__title {
	display: inline-block;
	-ms-flex: 1;
	flex: 1;
}

.eto-icon-btn:last-child {
	margin-right: 0;
}

.eto-icon-btn:hover {
	text-decoration: none;
}

.eto-icon-btn:hover {
	color: #444;
}

.eto-icon-btn:hover {
	text-decoration: none;
}

.eto-tooltip__trigger {

}

.dashboard-card--selected {
	box-shadow: 0 0 0 .5rem #277ab5 !important;
}

.eto-card__header {
	background-color: #ffffff;
}
</style>
<script>
	var count = 0;
	$(document).ready(
			function() {
				count = 0;
				$("#editcontrol").hide();
				$("#dashboardCards input[type=checkbox]").change(
						function() {
							if ($(this).attr("checked") == true) {
								$(this).parent().parent().parent().parent()
										.parent().parent().toggleClass(
												"dashboard-card--selected");
								count++;
							} else if ($(this).attr("checked") == false) {
								$(this).parent().parent().parent().parent()
										.parent().parent().removeClass(
												"dashboard-card--selected");
								count--;
							}
							$(".eto-badge").text(count);
						});

			});
	function goNavigate(url) {
		serializeLayout();
		window.location = url;
		showWaitBusy();
	}

	function goRefresh() {
		serializeLayout();
		document.dashboardForm.action = "loadDashboard.do";
		document.dashboardForm.submit();
		showWaitBusy(0);

	}

	function goSaveLayout() {
		serializeLayout();
		document.dashboardForm.action = "saveDashboardLayout.do";
		document.dashboardForm.submit();
		showWaitBusy(800);
	}
	function goResetLayout() {
		document.dashboardForm.dashboardLayout.value = 'col-1:costRecord,col-1:sourcingLane,col-1:forecast,col-1:forecast_ADJ,col-2:rebateProgram,col-2:bom';
		document.dashboardForm.action = "saveDashboardLayout.do";
		document.dashboardForm.submit();
		showWaitBusy(800);

	}
	function serializeLayout() {
		var order = "";
		var items = $("div.dashboard-area");
		for (var x = 0; x < items.length; x++) {
			var col = items[x].parentNode;
			order += col.id + ":" + items[x].id + ",";
		}
		document.dashboardForm.dashboardLayout.value = order;
		return order;

	}
	function init() {
		<c:if test="${dashboardForm.refresh}">
			goRefresh();
		</c:if>
		/* if(top.document.getElementById("contentFrame").src.endsWith("dashboard.do") == false){
            setTimeout(deleteBreadCrumbs,300);
     	} */
	}

	function deleteBreadCrumbs(){
		document.querySelectorAll('.eto-breadcrumbs').forEach(function(a){
			a.remove();
			});
		var DOM = {};
		DOM = parent.BreadCrumbModule.getDom();
		var innerDoc = DOM.contentFrame.contentDocument
				|| DOM.contentFrame.contentWindow.document;
		parent.BreadCrumbModule.loadBreadCrumb('dashboard.do', innerDoc);
	}

	function openAlert() {
		new eto.Modal({
			el : document.querySelector('#alertModal')
		}).open();
	}

	function openUnassignedItemsModal() {
		new eto.Modal({
			el : document.querySelector('#newUnassignedItemsModal')
		}).open();
	}

	function goalertReview() {
		//clear TAM search
		document.forms[0].action = "AlertSearch.do";
		document.forms[0].submit();
		showWaitBusy();
	}
	function goEditCard() {
		$(".eto-dashboard__controls").hide();
		$(".eto-dashboard-editcontrol").show();
		$(".eto-checkbox").show();
		$("#newsalert").hide();
		$(".editBtn").hide();
		$("#saveBtn").attr("disabled","disabled");
		$("#saveBtn").removeClass("eto-btn--primary");
		$("#removeCard").removeAttr("disabled","disabled");
	}
	function doCancleEditControl() {
		$(".eto-dashboard__controls").show();
		$(".eto-dashboard-editcontrol").hide();
		$(".eto-checkbox").hide();
		$("#newsalert").show();
		$(".editBtn").show();
		$("input[type=checkbox]").each(
				function() {
					$(this).parent().parent().parent().parent().parent()
							.parent().removeClass("dashboard-card--selected");
					$('input[type="checkbox"]:checked').attr('checked', false);
					$(this).parent().parent().parent().parent().parent()
					.parent().parent().show();
				});
		$(".eto-badge").text(0);
		count = 0;
		$("#dashboardC").val('');
	}
	function goAddCards() {
		new eto.Modal({
			el : document.querySelector('#dashbordAddCard')
		}).open();
	}
	function removeCards() {
		var dashboardCard = [];
		$.each($("input[type=checkbox]:checked"), function() {
			dashboardCard.push($(this).val());
			$(this).parent().parent().parent().parent()
			.parent().parent().parent().hide();
		});

		$("#dashboardC").val(dashboardCard);
		$("#saveBtn").removeAttr("disabled","disabled");
		$("#saveBtn").addClass("eto-btn eto-btn--primary");
		$("#removeCard").attr("disabled","disabled");
		$("#removeCard").addClass("eto-btn");
	}
	function addCards() {
		var dashboardCard = [];
		$.each($("#dashbordAddCard input[type=checkbox]:checked"), function() {
			dashboardCard.push($(this).val());
		});
		$("#dashboardC").val(dashboardCard);
	}
	function saveCards() {
		serializeLayout();
		document.dashboardForm.action = "saveDashboardCards.do";
		document.dashboardForm.submit();
		showWaitBusy(800);
	}
	function editInactiveCards() {
		serializeLayout();
		document.dashboardForm.action = "editDashboardCards.do";
		document.dashboardForm.submit();
		showWaitBusy(800);
	}
	function removeSingleCard(dcard){
		var dashboardCard = [];
		dashboardCard.push(dcard);
		$("#dashboardC").val(dashboardCard);
		saveCards();
	}
	function addCardsPreferencesstat(){
			var dashboardCard = [];
			$.each($("#dashbordAddStatus input[type=checkbox]:checked"), function() {
				dashboardCard.push($(this).val());
			});
			$("#dashboardC").val(dashboardCard);
	}

	function openALerts(alertType){
		parent.mcmApp.selectedOption = alertType;
		$.ajax({
			type: "POST",
			url: alertType+'Alert.do',
			cache: false,
			dataType: 'text',
			success: function(result) {
				window.open(alertType+"AlertSearch.do","_self");
			},
			error: function(error){
				alert('error'+error.statusText);
			}
		});
	}
</script>
</head>

<body onload="init()">
	<e2o:form action="loadDashboard.do">
		<input type="hidden" name="newItemAge"
			value="${dashboardForm.newItemAge}" />
		<input type="hidden" name="dashboardLayout" />
		<input type="hidden" name="dashboardCard" id="dashboardC" />
		<input type="hidden" name="cardsPreferences"
			id="cardsStatusPreferences" />
		<input type="hidden" name="cardType" id="cardType" />
		<input type="hidden" name="checkLastAccessParam" value="false">
		<c:set var="dashboardLayoutEncoded">
			<c:out value="${dashboardForm.dashboardLayout}" />
		</c:set>
		<div class="eto-dashboard__header e2mc-dashboard__header">
			<span class="eto-dashboard__title scplatform-dashboard__title"><h1>My
				Workspace</h1></span>
			<div class="eto-dashboard-editcontrol" style="display: none;">
				<span style="padding: 10px 10px 10px 10px;"><span
					class="eto-badge" data-type="info"> </span> Selected Items</span><span
					class="eto-dashboard-editcontrol__remove"><button
						type="button" class="eto-btn" id="removeCard"
						onclick="removeCards();">
						<span class="md-icon">delete</span> Remove
					</button></span>
				<button type="button" class="eto-btn eto-btn--primary" id="saveBtn"
					onclick="javascript:saveCards();">
					<span class="md-icon">done</span> Save
				</button>
				<button type="button" class="eto-btn"
					onclick="javascript:doCancleEditControl()">
					<span class="md-icon">undo</span> Cancel
				</button>
			</div>

			<div class="eto-dashboard__controls">
				<div style="display: inline-block; position: relative;">
					<div class="eto-tooltip__trigger" style="position: relative;">
						<button type="button" class="eto-icon-btn"
							onclick="javascript:goRefresh()" data-tooltip="#tooltipautorenew"
							aria-describedby="#tooltip-example">
							<span class="md-icon">autorenew</span>
						</button>
					</div>
				</div>
				<div style="display: inline-block; position: relative;">
					<div class="eto-tooltip__trigger" style="position: relative;">
						<button type="button" class="eto-icon-btn" onclick="goEditCard()"
							data-tooltip="#tooltipedit" aria-describedby="#tooltip-example">
							<span class="md-icon">edit</span>
						</button>
					</div>
				</div>
				<div style="display: inline-block; position: relative;">
					<div class="eto-tooltip__trigger" style="position: relative;">
						<button type="button" class="eto-icon-btn" onclick="goAddCards()"
							data-tooltip="#tooltipadd" aria-describedby="#tooltip-example">
							<span class="md-icon">add_box</span>
						</button>
					</div>
				</div>
			</div>
		</div>

		<div class="eto-tooltip" id="tooltipautorenew">
			<div class="eto-tooltip__content">

					<fmt:message key="dashboard.lastRefresh">
						<fmt:param>
							<fmt:formatDate value="${dashboardForm.lastLoadedDate}"
								timeZone="${appContext.currentTimezone}"
								pattern="${appContext.currentDateFormat} hh:mm:ss a z" />
						</fmt:param>
					</fmt:message>
					<c:if
						test="${empty appContext.currentUser.preferences['DB_REFRESH_SECS']}">
								&nbsp;(<fmt:message key="dashboard.refreshDisabled" />)
								</c:if>

			</div>
			<span class="eto-tooltip__caret"></span>
		</div>
		<div class="eto-tooltip" id="tooltipedit">
			<div class="eto-tooltip__content">
				Edit Cards
			</div>
			<span class="eto-tooltip__caret"></span>
		</div>
		<div class="eto-tooltip" id="tooltipadd">
			<div class="eto-tooltip__content">
<!-- FC 06 -->				Add Cards
			</div>
			<span class="eto-tooltip__caret"></span>
		</div>
		<script type="text/javascript">
			new eto.Tooltip({
				el : document.querySelector('#tooltipautorenew')
			});
			new eto.Tooltip({
				el : document.querySelector('#tooltipedit')
			});
			new eto.Tooltip({
				el : document.querySelector('#tooltipadd')
			});
		</script>
		<div class="container" id="dashboardCards"
			style="margin: 0 auto; padding-top: 20px; padding-bottom: 1rem; display: flex;">
			<c:if
				test="${empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, 'News/Alerts')}">
				<div class="col-sm-3 " style="padding: 0rem 0rem 0rem 1rem;">
					<div class="margin-bottom-sm-3"
						style="text-align: center; width: 100%">
						<div class="eto-card" id="eto-card-newsAlert">
							<header class="eto-card__header">
								<div class="col-sm-10">
									<span><b>News/Alerts</b></span>
								</div>
								<div class="col-sm-2">
									<span style="float: right;"> <span class="eto-dropdown ">
											<label class="eto-checkbox" style="display: none;"> <input
												class="eto-checkbox__field" type="checkbox"
												value="News/Alerts"> <span class="eto-checkbox__box"></span>
										</label>
											<button type="button" class="eto-dropdown__toggle editBtn">
												<i class="md-icon md-icon--sm">more_vert</i>
											</button>
											<ul class="eto-dropdown__menu">
												<li role="menuitem"><a
													href="javascript:removeSingleCard('News/Alerts')"><span
														class="md-icon">delete</span>Remove</a></li>
											</ul>
									</span>
									</span>
								</div>
							</header>

							<table class="table eto-table" id="table-example-2"
								style="width: 100%;">
								<tbody>
									<%
										int count = 0;
									%>
									<c:forEach var="alert" items="${dashboardForm.userAlerts}">
										<%
											count++;
										%>
										<%
											if (count < 4) {
										%>
										<tr>
											<td colspan="3"
												style="width: 10px; height: 110px; overflow: hidden;">
												<c:choose>
										<c:when test="${alert.alertURL != null}">
										<a href="${alert.alertURL}" target="_blank"><p
														style="word-break: break-word; white-space: normal; text-align: justify; padding: 5px 30px 10px;">${alert.alertTitle}</p>
										</a>
										</c:when>
										<c:otherwise>
										<p style="word-break: break-word; white-space: normal; text-align: justify; padding: 5px 30px 10px;">${alert.alertTitle}</p>

										</c:otherwise>
										</c:choose><span
												style="color: #d0ccc6; font-style: italic; padding-left: 90px;"><fmt:formatDate
														value="${alert.alertDate}" /></span>
											</td>
										</tr>
										<%
											}
										%>
									</c:forEach>
									<tr>
										<td><span style="font-style: italic; padding-left: 90px;">
												${dashboardForm.userAlerts.size()} &nbsp; TOTALS </span>
											<button type="button" class="eto-icon-btn" title="Info"
												onclick="javascript:openAlert();"
												style="font-size: 15px; padding-left: 10px;">
												<i class="md-icon">launch</i>
											</button></td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-newsAlert .eto-dropdown')
											});
								</script>
				</div>
			</c:if>

			<div class="col-sm-3"
				style="padding: 0rem 0rem 0rem 1rem; margin: 0px; text-align: center; width: 100%">
				<c:if
					test="${ empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, 'ReviewAlerts')}">
					<div class="margin-bottom-sm-3">
					<div class="eto-card " id="eto-card-ReviewAlerts">
						<div>
							<header class="eto-card__header">
								<div class="col-sm-10">
									<span><b>Review Alerts</b></span>
								</div>
								<div class="col-sm-2">
									<span style="float: right;"> <span class="eto-dropdown ">
											<label class="eto-checkbox" style="display: none;"> <input
												class="eto-checkbox__field" type="checkbox"
												value="ReviewAlerts"> <span
												class="eto-checkbox__box"></span>
										</label>
											<button type="button" class="eto-dropdown__toggle editBtn">
												<i class="md-icon md-icon--sm">more_vert</i>
											</button>
											<ul class="eto-dropdown__menu">
												<li role="menuitem"><a
													href="javascript:goalertReview()"><span class="md-icon">edit</span>Edit</a></li>
												<li role="menuitem"><a
													href="javascript:removeSingleCard('ReviewAlerts')"><span
														class="md-icon">delete</span>Remove</a></li>
											</ul>
									</span>
									</span>
								</div>
							</header>
							<section class="eto-card__body">
								<div class="v1">
									<c:if test="${not empty dashboardForm.reviwAlert}">
										<c:set var="link" value="./AlertSearch.do" />
										<c:forEach items="${dashboardForm.reviwAlert}"
											var="reviwAlert">
											<c:if test="${reviwAlert.key =='ItemAssignment'}">
												<c:url var="linkHref" value="${link}">
													<c:param name="status" value="${result[1]}" />
													<c:if test="${user}">
														<c:param name="user"
															value="${appContext.currentUser.userId}" />
													</c:if>
													<c:if
														test="${!empty dashboardForm.recordStatusAge[parts[1]]}">
														<c:param name="daysOld"
															value="${dashboardForm.recordStatusAge[parts[1]]}" />
													</c:if>
													<c:if
														test="${empty dashboardForm.recordStatusAge[parts[1]] && dashboardForm.recordStatusAge[parts[1]] == null}">
														<c:param name="daysOld" value="${defaultDaySold}" />
													</c:if>
												</c:url>
												<div class="row">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('ItemAssignment')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">assignment_turned_in</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">ITEM ASSIGNMENT</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='ItemUnassignment'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('ItemUnassignment')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">assignment_late</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">ITEM UNASSIGNMENT</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='CostChange'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('CostChange')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">track_changes</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">COST CHANGE</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='CostMissing'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('CostMissing')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #5b9ca2; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">radio_button_checked</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">COST MISSING</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='CostExpiring'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('CostExpiring')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">hourglass_empty</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">COST EXPIRING</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='CostPending'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('CostPending')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #f39c12; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">pending</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">COST PENDING</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='ForecastChange'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('ForecastChange')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #5b9ca2; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">cast</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">FORECAST CHANGE</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='ForecastExpiring'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('ForecastExpiring')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
														<div
															style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: white; padding-top: 15px; font-size: px;">cast_connected</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">FORECAST EXPIRING</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='RebatesExpiring'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('RebatesExpiring')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4">
														<span class="md-icon"
															style="color: #c04630; padding-top: 10px; font-size: 60px; float: right;">album</span>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">REBATES EXPIRING</span>
													</div>
												</div>
											</c:if>
											<c:if test="${reviwAlert.key =='RegionAdded'}">
												<div class="row" style="padding-top: 10px;">
													<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
														<span style="font-size: 20px; float: right;"><a href="javascript:openALerts('RegionAdded')"><b>${reviwAlert.value}</b></a></span>
													</div>
													<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4">
														<div
															style="border-radius: 40px; width: 50px; height: 50px; float: right;">
															<span class="md-icon"
																style="color: #5b9ca2; font-size: 60px; padding-top: 10px;">add_circle</span>
														</div>
													</div>
													<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
														<span style="color: #aba7a2;">REGION ADDED</span>
													</div>
												</div>
											</c:if>
										</c:forEach>
									</c:if>
									<c:if test="${empty dashboardForm.reviwAlert}">
										<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
									</c:if>
								</div>
							</section>
						</div>
					</div>
					</div>
					<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-ReviewAlerts .eto-dropdown')
											});
								</script>
				</c:if>
				<c:if
					test="${empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, 'news')}">
					<div class="margin-bottom-sm-3">
						<div class="eto-card" id="eto-card-news">
							<header class="eto-card__header">
								<div class="col-sm-10">
									<span><b> New unassigned items </b></span>
								</div>
								<div class="col-sm-2">
									<span style="float: right;"> <span class="eto-dropdown ">
											<label class="eto-checkbox" style="display: none;"> <input
												class="eto-checkbox__field" type="checkbox" value="news">
												<span class="eto-checkbox__box"></span>
										</label>
											<button type="button" class="eto-dropdown__toggle editBtn">
												<i class="md-icon md-icon--sm">more_vert</i>
											</button>
											<ul class="eto-dropdown__menu">
												<li role="menuitem"><a
													href="javascript:removeSingleCard('news')"><span
														class="md-icon">delete</span>Remove</a></li>
											</ul>
									</span>
									</span>
								</div>
							</header>
							<section class="eto-card__body">
								<c:if test="${not empty dashboardForm.newItemStatus}">
									<%
										int itemCount = 0;
									%>
									<c:forEach var="result" items="${dashboardForm.newItemStatus}">
										<%
											itemCount++;
										%>
										<%
											if (itemCount <= 5) {
										%>
										<c:url var="linkHref" value="./viewItemAssignmentSummary.do">
											<c:param name="categoryNameExact" value='${result[1]}' />
											<c:param name="initValue(isAssigned)" value = "not exists"/>
											<c:param name="initValue(daysOld)"
												value="${dashboardForm.newItemAge}" />
											<c:param name="checkLastAccessParam" value="false" />
										</c:url>
										<div class="row" style="padding-top: 10px;">
											<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
												<a href="javascript:goNavigate('${linkHref}')"><span
													style="font-size: 20px; float: right;"><b><c:out
																value="${result[0] > 999 ? '999+': result[0]}" /></b></span></a>
											</div>
											<div class="margin-left-xs-1 margin-left-sm-3 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
												<div
													style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right; display: flex;">
													<span class="md-icon"
														style="color: white; margin: auto; font-size: px;">new_releases</span>
												</div>
											</div>
											<div class="margin-left-xs-1 col-sm-4 col-xs-6"
												style="padding-top: 10px; overflow: hidden; text-overflow: ellipsis;">
												<span style="color: #aba7a2;" title="${result[1]}">${result[1]}</span>
											</div>
										</div>
										<%
											}
										%>
									</c:forEach>
								</c:if>
								<c:if test="${empty dashboardForm.newItemStatus}">
									<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
								</c:if>
							</section>
							<c:if test="${not empty dashboardForm.newItemStatus}">
								<footer class="eto-card__footer"
									style="justify-content: center;">
									<div id="modalElement">
										<span style="font-style: italic;">
											${dashboardForm.newItemStatus.size()} &nbsp; TOTALS </span>
										<button type="button" class="eto-icon-btn" title="Info"
											onclick="javascript:openUnassignedItemsModal();"
											style="font-size: 15px; padding-left: 10px;">
											<i class="md-icon">launch</i>
										</button>
									</div>
								</footer>
							</c:if>
						</div>
						<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-news .eto-dropdown')
											});
								</script>
					</div>
				</c:if>
			</div>
			<c:set var="CostRecorCard" value=""/>
				<c:set var="rebatesCard" value=""/>
				<c:set var="forecastAdjustableCard" value=""/>
				<c:set var="forecastCurrentCard" value=""/>
				<c:set var="bomCard" value=""/>
				<c:set var="SourcingLaneCard" value=""/>
				<c:forEach var="part"
					items="${fn:split(dashboardLayoutEncoded,',')}">
					<c:set var="parts" value="${fn:split(part,':')}" />
					<c:if test="${parts[1] == 'costRecord'}">
					<c:set var="CostRecorCard" value="${parts[1]}"/>
					</c:if>
					<c:if test="${parts[1] == 'rebateProgram'}">
					<c:set var="rebatesCard" value="${parts[1] }"/>
					</c:if>
					<c:if test="${parts[1] == 'forecast'}">
					<c:set var="forecastCurrentCard" value="${parts[1]}"/>
					</c:if>
					<c:if test="${parts[1] == 'sourcingLane'}">
					<c:set var="SourcingLaneCard" value="${parts[1]}"/>
					</c:if>
					<c:if test="${parts[1] == 'bom'}">
					<c:set var="bomCard" value="${parts[1]}"/>
					</c:if>
					<c:if test="${parts[1] == 'forecast_ADJ'}">
					<c:set var="forecastAdjustableCard" value="${parts[1]}"/>
					</c:if>
					</c:forEach>

			<div class="col-sm-3"
				style="padding: 0rem 0rem 0rem 1rem; margin: 0px; text-align: center; width: 100%">
						<c:if
							test="${empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, CostRecorCard) }">
							<c:set var="link" value="./viewCostRecordSummary.do" />
							<c:set var="show"
								value="${e2ofn:hasAccess(appContext, 'COST_RECORD', 'Read')}" />
							<div class="margin-bottom-sm-3"
								style="width: 100%; text-align: center;">
								<div class="eto-card" id="eto-card-costRecord">
									<header class="eto-card__header">
										<div class="col-sm-10">
											<span><b><fmt:message
														key="dashboard.record_status.${CostRecorCard}"></fmt:message> </b></span>
										</div>
										<div class="col-sm-2">
											<span style="float: right;"> <span
												class="eto-dropdown "> <label class="eto-checkbox"
													style="display: none;"> <input
														class="eto-checkbox__field" type="checkbox"
														value="${CostRecorCard}"> <span
														class="eto-checkbox__box"></span>
												</label>
													<button type="button" class="eto-dropdown__toggle editBtn">
														<i class="md-icon md-icon--sm">more_vert</i>
													</button>
													<ul class="eto-dropdown__menu">
														<li role="menuitem"><a
															href="javascript:openStatusModal('${CostRecorCard}')"><span
																class="md-icon">edit</span>Edit</a></li>
														<li role="menuitem"><a
															href="javascript:removeSingleCard('${CostRecorCard}')"><span
																class="md-icon">delete</span>Remove</a></li>
													</ul>
											</span>
											</span>
										</div>
									</header>
									<section class="eto-card__body">
										<div>
											<c:if
												test="${not empty dashboardForm.recordStatus[CostRecorCard]}">
												<c:forEach var="result"
													items="${dashboardForm.recordStatus[CostRecorCard]}">
													<c:url var="linkHref" value="${link}">
														<c:param name="status" value="${result[1]}" />
														<c:if test="${user}">
															<c:param name="user"
																value="${appContext.currentUser.userId}" />
														</c:if>
														<c:if
															test="${!empty dashboardForm.recordStatusAge[CostRecorCard]}">
															<c:param name="daysOld"
																value="${dashboardForm.recordStatusAge[CostRecorCard]}" />
														</c:if>
														<c:if
															test="${empty dashboardForm.recordStatusAge[CostRecorCard] && dashboardForm.recordStatusAge[CostRecorCard] == null}">
															<c:param name="daysOld" value="${defaultDaySold}" />
														</c:if>
													</c:url>
													<c:if test="${result[1]=='NEW'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">new_releases</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">NEW</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='PENDING'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">timer</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">PENDING</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='APPROVED'}">
														<div class="row">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">thumb_up</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">APPROVED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='CLOSED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #afacac; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">close</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">CLOSED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='REJECTED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">block</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">REJECTED</span>
															</div>
														</div>
													</c:if>
												</c:forEach>
											</c:if>
											<c:if test="${empty dashboardForm.recordStatus[CostRecorCard]}">
												<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
											</c:if>
										</div>
									</section>
								</div>
								<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-costRecord .eto-dropdown')
											});
								</script>
							</div>
						</c:if>
						<c:if
							test="${empty dashboardForm.dashboardCards || (e2ofn:contains(dashboardForm.dashboardCards, 'rebateProgram'))}">
							<c:set var="link" value="./viewRebateProgramSummary.do" />
							<c:set var="show"
								value="${e2ofn:hasAccess(appContext, 'REBATE', 'Read')}" />
							<div class="margin-bottom-sm-3">
								<div class="eto-card" id="eto-card-rebateProgram">
									<header class="eto-card__header">
										<div class="col-sm-10">
											<span><b><fmt:message
														key="dashboard.record_status.${rebatesCard}"></fmt:message> </b></span>
										</div>
										<div class="col-sm-2">
											<span style="float: right;"> <span
												class="eto-dropdown "> <label class="eto-checkbox"
													style="display: none;"> <input
														class="eto-checkbox__field" type="checkbox"
														value="${rebatesCard}"> <span
														class="eto-checkbox__box"></span>
												</label>
													<button type="button" class="eto-dropdown__toggle editBtn">
														<i class="md-icon md-icon--sm">more_vert</i>
													</button>
													<ul class="eto-dropdown__menu">
														<li role="menuitem"><a
															href="javascript:openStatusModal('${rebatesCard}')"><span
																class="md-icon">edit</span>Edit</a></li>
														<li role="menuitem"><a
															href="javascript:removeSingleCard('${rebatesCard}')"><span
																class="md-icon">delete</span>Remove</a></li>
													</ul>
											</span>
											</span>
										</div>
									</header>
									<section class="eto-card__body">
										<div>
											<c:if
												test="${not empty dashboardForm.recordStatus[rebatesCard]}">
												<c:forEach var="result"
													items="${dashboardForm.recordStatus[rebatesCard]}">
													<c:url var="linkHref" value="${link}">
														<c:param name="status" value="${result[1]}" />
														<c:if test="${user}">
															<c:param name="user"
																value="${appContext.currentUser.userId}" />
														</c:if>
														<c:if
															test="${!empty dashboardForm.recordStatusAge[rebatesCard]}">
															<c:param name="daysOld"
																value="${dashboardForm.recordStatusAge[rebatesCard]}" />
														</c:if>
														<c:if
															test="${empty dashboardForm.recordStatusAge[rebatesCard] && dashboardForm.recordStatusAge[rebatesCard] == null}">
															<c:param name="daysOld" value="${defaultDaySold}" />
															</c:if>
													</c:url>
													<c:if test="${result[1]=='NEW'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">new_releases</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">NEW</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='PENDING'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">timer</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">PENDING</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='APPROVED'}">
														<div class="row">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">thumb_up</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">APPROVED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='CLOSED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #afacac; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">close</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">CLOSED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='REJECTED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">block</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">REJECTED</span>
															</div>
														</div>
													</c:if>
												</c:forEach>
											</c:if>
											<c:if test="${empty dashboardForm.recordStatus[rebatesCard]}">
												<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
											</c:if>
										</div>
									</section>
								</div>
								<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-rebateProgram .eto-dropdown')
											});
								</script>
							</div>
						</c:if>
						<c:if
							test="${empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, forecastCurrentCard)}">
							<c:set var="link" value="./viewCurrentForecastSummary.do" />
							<c:set var="show"
								value="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}" />
							<div class="margin-bottom-sm-3">
								<div class="eto-card" id="eto-card-forecast">
									<header class="eto-card__header">
										<div class="col-sm-10">
											<span><b><fmt:message
														key="dashboard.record_status.${forecastCurrentCard}"></fmt:message> </b></span>
										</div>
										<div class="col-sm-2">
											<span style="float: right;"> <span
												class="eto-dropdown "> <label class="eto-checkbox"
													style="display: none;"> <input
														class="eto-checkbox__field" type="checkbox"
														value="${forecastCurrentCard}"> <span
														class="eto-checkbox__box"></span>
												</label>
													<button type="button" class="eto-dropdown__toggle editBtn">
														<i class="md-icon md-icon--sm">more_vert</i>
													</button>
													<ul class="eto-dropdown__menu">
														<li role="menuitem"><a
															href="javascript:openStatusModal('${forecastCurrentCard}')"><span
																class="md-icon">edit</span>Edit</a></li>
														<li role="menuitem"><a
															href="javascript:removeSingleCard('${forecastCurrentCard}')"><span
																class="md-icon">delete</span>Remove</a></li>
													</ul>
											</span>
											</span>
										</div>
									</header>
									<section class="eto-card__body">
										<div>
											<c:if
												test="${not empty dashboardForm.recordStatus[forecastCurrentCard]}">
												<c:forEach var="result"
													items="${dashboardForm.recordStatus[forecastCurrentCard]}">
													<c:url var="linkHref" value="${link}">
														<c:param name="status" value="${result[1]}" />
														<c:if test="${user}">
															<c:param name="user"
																value="${appContext.currentUser.userId}" />
														</c:if>
														<c:if
															test="${!empty dashboardForm.recordStatusAge[forecastCurrentCard]}">
															<c:param name="daysOld"
																value="${dashboardForm.recordStatusAge[forecastCurrentCard]}" />
														</c:if>
														<c:if
															test="${empty dashboardForm.recordStatusAge[forecastCurrentCard] && dashboardForm.recordStatusAge[forecastCurrentCard] == null}">
															<c:param name="daysOld" value="${defaultDaySold}" />
														</c:if>
													</c:url>
													<c:if test="${result[1]=='NEW'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">new_releases</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">NEW</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='PENDING'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">timer</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">PENDING</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='APPROVED'}">
														<div class="row">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">thumb_up</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">APPROVED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='CLOSED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #afacac; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">close</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">CLOSED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='REJECTED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">block</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">REJECTED</span>
															</div>
														</div>
													</c:if>
												</c:forEach>
											</c:if>
											<c:if test="${empty dashboardForm.recordStatus[forecastCurrentCard]}">
												<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
											</c:if>
										</div>
									</section>
								</div>
								<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-forecast .eto-dropdown')
											});
								</script>
							</div>
						</c:if>
			</div>
			<div class="col-sm-3"
				style="padding: 0rem 0rem 0rem 1rem; margin: 0px; text-align: center; width: 100%">
						<c:if
							test="${empty dashboardForm.dashboardCards || (e2ofn:contains(dashboardForm.dashboardCards,SourcingLaneCard))}">
							<c:set var="link" value="./viewSourcingLaneSummary.do" />
							<c:set var="show"
								value="${e2ofn:hasAccess(appContext, 'SOURCING_LANE', 'Read')}" />
							<div class="margin-bottom-sm-3">
								<div class="eto-card" id="eto-card-sourcingLane">
									<header class="eto-card__header">
										<div class="col-sm-10">
											<span><b><fmt:message
														key="dashboard.record_status.${SourcingLaneCard}"></fmt:message> </b></span>
										</div>
										<div class="col-sm-2">
											<span style="float: right;"> <span
												class="eto-dropdown "> <label class="eto-checkbox"
													style="display: none;"> <input
														class="eto-checkbox__field" type="checkbox"
														value="${SourcingLaneCard}"> <span
														class="eto-checkbox__box"></span>
												</label>
													<button type="button" class="eto-dropdown__toggle editBtn">
														<i class="md-icon md-icon--sm">more_vert</i>
													</button>
													<ul class="eto-dropdown__menu">
														<li role="menuitem"><a
															href="javascript:openStatusModal('${SourcingLaneCard}')"><span
																class="md-icon">edit</span>Edit</a></li>
														<li role="menuitem"><a
															href="javascript:removeSingleCard('${SourcingLaneCard}')"><span
																class="md-icon">delete</span>Remove</a></li>
													</ul>
											</span>
											</span>
										</div>
									</header>
									<section class="eto-card__body">
										<div>
											<c:if
												test="${not empty dashboardForm.recordStatus[SourcingLaneCard]}">
												<c:forEach var="result"
													items="${dashboardForm.recordStatus[SourcingLaneCard]}">
													<c:url var="linkHref" value="${link}">
														<c:param name="status" value="${result[1]}" />
														<c:if test="${user}">
															<c:param name="user"
																value="${appContext.currentUser.userId}" />
														</c:if>
														<c:if
															test="${!empty dashboardForm.recordStatusAge[SourcingLaneCard] }">
															<c:param name="daysOld"
																value="${dashboardForm.recordStatusAge[SourcingLaneCard]}" />
														</c:if>
														<c:if
															test="${empty dashboardForm.recordStatusAge[SourcingLaneCard] && dashboardForm.recordStatusAge[SourcingLaneCard] == null}">
															<c:param name="daysOld" value="${defaultDaySold}" />
														</c:if>
													</c:url>
													<c:if test="${result[1]=='NEW'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">new_releases</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">NEW</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='PENDING'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">timer</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">PENDING</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='APPROVED'}">
														<div class="row">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">thumb_up</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">APPROVED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='CLOSED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #afacac; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">close</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">CLOSED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='REJECTED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">block</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">REJECTED</span>
															</div>
														</div>
													</c:if>
												</c:forEach>
											</c:if>
											<c:if test="${empty dashboardForm.recordStatus[SourcingLaneCard]}">
												<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
											</c:if>
										</div>
									</section>
								</div>
								<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-sourcingLane .eto-dropdown')
											});
								</script>
							</div>
						</c:if>
						<c:if
							test="${empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, bomCard)}">
							<c:set var="link" value="./viewBomManagementSummary.do" />
							<c:set var="show"
							value="${e2ofn:hasAccess(appContext, 'BOM', 'Read')}" />
							<div class="margin-bottom-sm-3">
								<div class="eto-card" id="eto-card-bom">
									<header class="eto-card__header">
										<div class="col-sm-10">
											<span><b><fmt:message
														key="dashboard.record_status.${bomCard}"></fmt:message> </b></span>
										</div>
										<div class="col-sm-2">
											<span style="float: right;"> <span
												class="eto-dropdown "> <label class="eto-checkbox"
													style="display: none;"> <input
														class="eto-checkbox__field" type="checkbox"
														value="${bomCard}"> <span
														class="eto-checkbox__box"></span>
												</label>
													<button type="button" class="eto-dropdown__toggle editBtn">
														<i class="md-icon md-icon--sm">more_vert</i>
													</button>
													<ul class="eto-dropdown__menu">
														<li role="menuitem"><a
															href="javascript:openStatusModal('${bomCard}')"><span
																class="md-icon">edit</span>Edit</a></li>
														<li role="menuitem"><a
															href="javascript:removeSingleCard('${bomCard}')"><span
																class="md-icon">delete</span>Remove</a></li>
													</ul>
											</span>
											</span>
										</div>
									</header>
									<section class="eto-card__body">
										<div>
											<c:if
												test="${not empty dashboardForm.recordStatus[bomCard]}">
												<c:forEach var="result"
													items="${dashboardForm.recordStatus[bomCard]}">
													<c:url var="linkHref" value="${link}">
														<c:param name="status" value="${result[1]}" />
														<c:if test="${user}">
															<c:param name="user"
																value="${appContext.currentUser.userId}" />
														</c:if>
														<c:if
															test="${!empty dashboardForm.recordStatusAge[bomCard]  }">
															<c:param name="daysOld"
																value="${dashboardForm.recordStatusAge[bomCard]}" />
														</c:if>
														<c:if
															test="${empty dashboardForm.recordStatusAge[bomCard] && dashboardForm.recordStatusAge[bomCard] == null}">
															<c:param name="daysOld" value="${defaultDaySold}" />
														</c:if>
													</c:url>
													<c:out value="${daysOld}"></c:out>
													<c:if test="${result[1]=='NEW'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">new_releases</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">NEW</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='PENDING'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">timer</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">PENDING</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='APPROVED'}">
														<div class="row">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">thumb_up</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">APPROVED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='CLOSED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #afacac; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">close</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">CLOSED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='REJECTED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">block</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">REJECTED</span>
															</div>
														</div>
													</c:if>
												</c:forEach>
											</c:if>
											<c:if test="${empty dashboardForm.recordStatus[bomCard]}">
												<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
											</c:if>
										</div>
									</section>
								</div>
								<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-bom .eto-dropdown')
											});
								</script>
							</div>
						</c:if>
						<c:if
							test="${empty dashboardForm.dashboardCards || e2ofn:contains(dashboardForm.dashboardCards, forecastAdjustableCard)}">
							<c:set var="link" value="./viewAdjustableForecastSummary.do" />
							<c:set var="show"
								value="${e2ofn:hasAccess(appContext, 'FORECAST', 'Read')}" />
							<div class="margin-bottom-sm-3">
								<div class="eto-card" id="eto-card-forecast_ADJ">
									<header class="eto-card__header">
										<div class="col-sm-10">
											<span><b><fmt:message
														key="dashboard.record_status.${forecastAdjustableCard}"></fmt:message> </b></span>
										</div>
										<div class="col-sm-2">
											<span style="float: right;"> <span
												class="eto-dropdown "> <label class="eto-checkbox"
													style="display: none;"> <input
														class="eto-checkbox__field" type="checkbox"
														value="${forecastAdjustableCard}"> <span
														class="eto-checkbox__box"></span>
												</label>
													<button type="button" class="eto-dropdown__toggle editBtn">
														<i class="md-icon md-icon--sm">more_vert</i>
													</button>
													<ul class="eto-dropdown__menu">
														<li role="menuitem"><a
															href="javascript:openStatusModal('${forecastAdjustableCard}')"><span
																class="md-icon">edit</span>Edit</a></li>
														<li role="menuitem"><a
															href="javascript:removeSingleCard('${forecastAdjustableCard}')"><span
																class="md-icon">delete</span>Remove</a></li>
													</ul>
											</span>
											</span>
										</div>
									</header>
									<section class="eto-card__body">
										<div>
											<c:if
												test="${not empty dashboardForm.recordStatus[forecastAdjustableCard]}">
												<c:forEach var="result"
													items="${dashboardForm.recordStatus[forecastAdjustableCard]}">
													<c:url var="linkHref" value="${link}">
														<c:param name="status" value="${result[1]}" />
														<c:if test="${user}">
															<c:param name="user"
																value="${appContext.currentUser.userId}" />
														</c:if>
														<c:if
															test="${!empty dashboardForm.recordStatusAge[forecastAdjustableCard]}">
															<c:param name="daysOld"
																value="${dashboardForm.recordStatusAge[forecastAdjustableCard]}" />
														</c:if>
														<c:if
															test="${empty dashboardForm.recordStatusAge[forecastAdjustableCard] && dashboardForm.recordStatusAge[forecastAdjustableCard] == null}">
															<c:param name="daysOld" value="${defaultDaySold}" />
														</c:if>
													</c:url>
													<c:if test="${result[1]=='NEW'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">new_releases</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">NEW</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='PENDING'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #e1b634; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">timer</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">PENDING</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='APPROVED'}">
														<div class="row">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #3cab3c; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">thumb_up</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">APPROVED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='CLOSED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #afacac; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">close</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">CLOSED</span>
															</div>
														</div>
													</c:if>
													<c:if test="${result[1]=='REJECTED'}">
														<div class="row" style="padding-top: 10px;">
															<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
																<span style="font-size: 20px;"><b><a
																		href="javascript:goNavigate('${linkHref}')"><c:out
																				value="${result[0] > 999 ? '999+': result[0]}"></c:out></a></b></span>
															</div>
															<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
																<div
																	style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right;">
																	<span class="md-icon"
																		style="color: white; padding-top: 15px; font-size: px;">block</span>
																</div>
															</div>
															<div class="margin-left-xs-1 col-sm-5 col-xs-6" style="padding-top: 10px;">
																<span style="color: #aba7a2;">REJECTED</span>
															</div>
														</div>
													</c:if>
												</c:forEach>
											</c:if>
											<c:if test="${empty dashboardForm.recordStatus[forecastAdjustableCard]}">
												<p style="padding-top: 10px;">NO DATA TO DISPLAY</p>
											</c:if>
										</div>
									</section>
								</div>
								<script type="text/javascript">
									new eto.Dropdown(
											{
												el : document
														.querySelector('#eto-card-forecast_ADJ .eto-dropdown')
											});
								</script>
							</div>
						</c:if>
			</div>
		</div>
		<script type="text/javascript">
		function openStatusModal(card){
			$("#dashboardC").val(card);
			 var cardsDivElement = "";
		 	if(card === "costRecord"){
		 		var div = "";
		 		var stat="";
		 		$("#cardType").val('DB_STATUS_costRecord');
		 		$(".ageField").attr("name","userPreferenceValue(DB_STATUS_costRecord_DAYSOLD)");
		 		$(".ageField").attr("value","${e2ofn:getDayOldDashBoardCard(appContext,'costRecord')}");
		 		$("#dashbordAddStatus #cardProgramStat").html('<fmt:message key="dashboard.record_status.costRecord"/>');
		 		<c:forEach var="state" items='${dashboardForm.availableStates["Sourcing"]}'>
		 		    stat ="";
		 		    <c:if test="${e2ofn:arrayContains(dashboardForm.costRecordStatPref,fn:toUpperCase(state.label))}">
		 		     stat= "checked" ;
		 		   </c:if>
		 		div = div+ '<div class="row"><label class="eto-checkbox"> <input class="eto-checkbox__field" type="checkbox" name="" '+stat+' value="${state.name}"> '
				+'<span class="eto-checkbox__box"></span> <span	class="eto-checkbox__label"> ${state.label}</span></label></div>';
				</c:forEach>
		 		cardsDivElement = div;
			}
		 	else if(card === "sourcingLane"){
		 		var div = "";
		 		$("#cardType").val('DB_STATUS_sourcingLane');
		 		$(".ageField").attr("name","userPreferenceValue(DB_STATUS_sourcingLane_DAYSOLD)");
		 		$(".ageField").attr("value","${e2ofn:getDayOldDashBoardCard(appContext,'sourcingLane')}");
		 		$("#dashbordAddStatus #cardProgramStat").html('<fmt:message key="dashboard.record_status.sourcingLane"/>');
		 		<c:forEach var="state" items='${dashboardForm.availableStates["Sourcing"]}'>
		 		  stat ="";
		 		    <c:if test="${e2ofn:arrayContains(dashboardForm.sourcingLaneStatPref,fn:toUpperCase(state.label))}">
		 		     stat= "checked" ;
		 		   </c:if>
		 		div = div+ '<div class="row"><label class="eto-checkbox"> <input class="eto-checkbox__field" type="checkbox" name="" '+stat+' value="${state.name}"> '
				+'<span class="eto-checkbox__box"></span> <span	class="eto-checkbox__label"> ${state.label}</span></label></div>';
				</c:forEach>
		 		cardsDivElement = div;
			}
		 	else if(card =="forecast_ADJ"){
		 		var div = "";
		 		$("#cardType").val('DB_STATUS_forecast_ADJ');
		 		$(".ageField").attr("name","userPreferenceValue(DB_STATUS_forecast_ADJ_DAYSOLD)");
		 		$(".ageField").attr("value","${e2ofn:getDayOldDashBoardCard(appContext,'forecast_ADJ')}");
		 		$("#dashbordAddStatus #cardProgramStat").html('<fmt:message key="dashboard.record_status.forecast_ADJ"/>');
		 		<c:forEach var="state" items='${dashboardForm.availableStates["Forecast_ADJ"]}'>
		 		 stat ="";
	 		    <c:if test="${e2ofn:arrayContains(dashboardForm.adjustableforecastStatPref,fn:toUpperCase(state.label))}">
		 		     stat= "checked" ;
		 		   </c:if>
		 		div = div+ '<div class="row"><label class="eto-checkbox"> <input class="eto-checkbox__field" type="checkbox" name="" '+stat+' value="${state.name}"> '
				+'<span class="eto-checkbox__box"></span> <span	class="eto-checkbox__label"> ${state.label}</span></label></div>';
				</c:forEach>
		 		cardsDivElement = div;
			}
		 	else if(card === "rebateProgram"){
		 		var div = "";
		 		$("#cardType").val('DB_STATUS_rebateProgram');
		 		$(".ageField").attr("name","userPreferenceValue(DB_STATUS_rebateProgram_DAYSOLD)");
		 		$(".ageField").attr("value","${e2ofn:getDayOldDashBoardCard(appContext,'rebateProgram')}");
		 		$("#dashbordAddStatus #cardProgramStat").html('<fmt:message key="dashboard.record_status.rebateProgram"/>');
		 		<c:forEach var="state" items='${dashboardForm.availableStates["Rebate"]}'>
		 		  stat ="";
		 		    <c:if test="${e2ofn:arrayContains(dashboardForm.rebateStatPref,fn:toUpperCase(state.label))}">
		 		     stat= "checked" ;
		 		   </c:if>
		 		div = div+ '<div class="row"><label class="eto-checkbox"> <input class="eto-checkbox__field" type="checkbox" name="" '+stat+' value="${state.name}"> '
				+'<span class="eto-checkbox__box"></span> <span	class="eto-checkbox__label"> ${state.label}</span></label></div>';
				</c:forEach>
		 		cardsDivElement = div;
			}
		 	else if(card === "bom"){
		 		var div = "";
		 		$("#cardType").val('DB_STATUS_bom');
		 		$("#dashbordAddStatus #cardProgramStat").html('<fmt:message key="dashboard.record_status.bom"/>');
		 		$(".ageField").attr("name","userPreferenceValue(DB_STATUS_bom_DAYSOLD)");
		 		$(".ageField").attr("value","${e2ofn:getDayOldDashBoardCard(appContext,'bom')}");
		 		<c:forEach var="state" items='${dashboardForm.availableStates["BOM"]}'>
		 		  stat ="";
		 		    <c:if test="${e2ofn:arrayContains(dashboardForm.bomStatPref,fn:toUpperCase(state.label))}">
		 		     stat= "checked" ;
		 		   </c:if>
		 		div = div+ '<div class="row"><label class="eto-checkbox"> <input class="eto-checkbox__field" type="checkbox" name="" '+stat+' value="${state.name}"> '
				+'<span class="eto-checkbox__box"></span> <span	class="eto-checkbox__label"> ${state.label}</span></label></div>';
				</c:forEach>
		 		cardsDivElement = div;
			}
		 	else if(card === "forecast"){
		 		var div = "";
		 		$("#cardType").val('DB_STATUS_forecast');
		 		$(".ageField").attr("name","userPreferenceValue(DB_STATUS_forecast_DAYSOLD)");
		 		$(".ageField").attr("value","${e2ofn:getDayOldDashBoardCard(appContext,'forecast')}");
		 		$("#dashbordAddStatus #cardProgramStat").html('<fmt:message key="dashboard.record_status.forecast"/>');
		 		<c:forEach var="state" items='${dashboardForm.availableStates["Forecast"]}'>
		 		 stat ="";
		 		    <c:if test="${e2ofn:arrayContains(dashboardForm.forecastStatPref,fn:toUpperCase(state.label))}">
		 		     stat= "checked" ;
		 		   </c:if>
		 		div = div+ '<div class="row"><label class="eto-checkbox"> <input class="eto-checkbox__field" type="checkbox" name="" '+stat+' value="${state.name}"> '
				+'<span class="eto-checkbox__box"></span> <span	class="eto-checkbox__label"> ${state.label}</span></label></div>';
				</c:forEach>
		 		cardsDivElement = div;
			}
		 	new eto.Modal({
				el : document.querySelector('#dashbordAddStatus')
			}).open();
		 	$("#dashboardStatusDiv").html(cardsDivElement);

		}
		function saveCardStatus(){
			var dashboardCard = [];
			$.each($("#dashbordAddStatus input[type=checkbox]:checked"), function() {
				dashboardCard.push($(this).val());
			});
			$("#cardsStatusPreferences").val(dashboardCard);
			savedashboardCardsStatPref();
		}
	function savedashboardCardsStatPref()	{
		serializeLayout();
		document.dashboardForm.action = "saveDashboardCardsStatus.do";
		document.dashboardForm.submit();
		showWaitBusy(800);
		}
		</script>
		<div class="eto-modal" id="alertModal">
			<div class="eto-modal__content col-sm-10"
				style="border: 2px #e9ecec solid;">
				<header class="eto-modal__header"
					style="border: 1px #f2f2f9 solid; background: white">
					<span>Publish News/Alerts</span> <span id="close"><button
							type="button" style="float: right; font-size: 30px;"
							class="eto-modal__close" data-modal-close></button></span>
				</header>
				<section class="eto-modal__body">
					<table class="eto-table" style="border: 2px #dbdbe0 solid;">
						<thead>
							<tr>
								<th width="60%">Title</th>
								<th width="40%">Dated</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="alert" items="${dashboardForm.userAlerts}"
								varStatus="count">
								<tr>
									<td width="60%"
										style="height: 100px; word-break: break-word; white-space: normal">
										<c:choose>
										<c:when test="${alert.alertURL != null}">
										<a href="${alert.alertURL}" target="_blank"><span>${alert.alertTitle} </span>
										</a>
										</c:when>
										<c:otherwise>
										<span>${alert.alertTitle} </span>
										</c:otherwise>
										</c:choose>
										 </td>
									<td width="40%"><fmt:formatDate value="${alert.alertDate}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</section>

			</div>
		</div>
		<div class="eto-modal" id="newUnassignedItemsModal">
			<div class="eto-modal__content col-sm-5"
				style="border: 2px #e9ecec solid;">
				<header class="eto-modal__header"
					style="border: 1px #f2f2f9 solid; background: white">
					<span>New unassigned items</span> <span id="close"><button
							type="button" style="float: right; font-size: 30px;"
							class="eto-modal__close" data-modal-close></button></span>
				</header>
				<section class="eto-modal__body">
					<c:forEach var="result" items="${dashboardForm.newItemStatus}">
						<c:url var="linkHref" value="./viewItemAssignmentSummary.do">
							<c:param name="initValue(categoryName)" value='${result[1]}' />
							<c:param name="initValue(isAssigned)" />
							<c:param name="initValue(daysOld)"
								value="${dashboardForm.newItemAge}" />
							<c:param name="checkLastAccessParam" value="false" />
						</c:url>
						<div class="row" style="padding-top: 10px;">
							<div class="col-sm-3 col-xs-4" style="padding-top: 10px;">
								<a href="javascript:goNavigate('${linkHref}')"><span
									style="font-size: 20px; float: right;"><b><c:out
												value="${result[0] > 999 ? '999+': result[0]}" /></b></span></a>
							</div>
							<div class="margin-left-xs-1 margin-left-sm-5 col-sm-2 col-xs-4" style="padding: 0px 10px 0px 30px;">
								<div
									style="border-radius: 40px; background: #c04630; width: 50px; height: 50px; float: right; display: flex;">
									<span class="md-icon"
										style="color: white; font-size: px; margin: auto;">new_releases</span>
								</div>
							</div>
							<div class="margin-left-xs-1 col-sm-5 col-xs-6"
								style="padding-top: 10px; overflow: hidden; text-overflow: ellipsis;">
								<span style="color: #aba7a2;" title="${result[1]}">${result[1]}</span>
							</div>
						</div>
					</c:forEach>
				</section>
			</div>
		</div>
		<div class="eto-modal" id="dashbordAddCard">
			<div class="eto-modal__content col-xs-12 col-sm-4 col-lg-6 col-xl-4">
				<header class="eto-modal__header">
					<span>Add Cards</span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<c:forEach var="result"
						items="${dashboardForm.inactiveDashboardCards}">
						<div class="row">
							<label class="eto-checkbox"> <input
								class="eto-checkbox__field" type="checkbox"
								onchange="addCards();" name="" value="${result}"> <span
								class="eto-checkbox__box"></span> <span
								class="eto-checkbox__label"><fmt:message
										key="dashboard.record_status.${result}"></fmt:message> </span>
							</label>
						</div>
					</c:forEach>
				</section>
				<footer class="eto-modal__footer">
					<button type="button" class="eto-btn" data-modal-close>Close</button>
					<button type="button" class="eto-btn eto-btn--primary"
						onclick="javascript:editInactiveCards();"
						<c:if test="${empty dashboardForm.inactiveDashboardCards}" >disabled</c:if>>Save</button>
				</footer>
			</div>
		</div>

		<div class="eto-modal" id="dashbordAddStatus" style="height: 500px;">
			<div class="eto-modal__content col-xs-4 col-sm-4 col-lg-6 col-xl-4">
				<header class="eto-modal__header">
					<span><span id="cardProgramStat"> </span> Program Status</span>
					<button class="eto-modal__close" data-modal-close></button>
				</header>
				<section class="eto-modal__body">
					<div style="padding: 10px;">
						<div class="row"
							style="padding: 10px 0px 0px 10px; font-size: 15px;">
							<span>Change in the last <input type="text"
								Class="inputField ageField" size="4" value="" /> &nbsp; days <span
								style="color: #d0ccc6; font-style: italic;"> Default
									:${defaultDaySold} Days</span></span>
						</div>
						<div class="row"
							style="padding: 10px 0px 0px 10px; font-size: 15px; font-style: oblique;">
							<span>Select the status you want displayed </span>
						</div>
						<div style="padding: 10px 0px 0px 10px;">
							<div id="dashboardStatusDiv"></div>
						</div>
					</div>
				</section>
				<footer class="eto-modal__footer">
					<button type="button" class="eto-btn" data-modal-close>Close</button>
					<button type="button" class="eto-btn eto-btn--primary"
						onclick="javascript:saveCardStatus();">Save</button>
				</footer>
			</div>
		</div>

		<script type="text/javascript">
			new eto.Modal({
				el : document.querySelector('#dashbordAddCard')
			});
			new eto.Modal({
				el : document.querySelector('#dashbordAddStatus')
			});
		</script>
	</e2o:form>