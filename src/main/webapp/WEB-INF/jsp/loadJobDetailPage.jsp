<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin name="e2-modern" />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="false" ajaxSupport="true" />
<e2ot:help contextName="LoadJob-Details" />
<title>Load Event</title>
<style type="text/css">
.instructionsArea {
	background-color: transparent;
}
</style>
</head>
<script>
	function handleDataChanged() {
		document.forms[0].unsavedData.value = 'true';
		var msgArea = document.getElementById('unsavedDataMsg');
		if (msgArea != null) {
			msgArea.innerText = '<fmt:message key="info.unsaved_data"/>';
		}
	}

	function canLeavePage(callback) {
		if (document.forms[0].unsavedData.value == 'true') {
			showYesNoMessageBox('YES NO', 'WARN',
					"<fmt:message key='warn.changes_not_saved_yes_no'/>",
					"<fmt:message key='msg.warn'/>", callback);
			return false;
		}
		return true;
	}

	function goRefresh() {
		document.forms[0].submit();
		showWaitBusy();
	}

	function goBack() {
		if (canLeavePage(goBackCallback)) {
			goBackCallback();
		}
	}

	function goBackCallback() {
		document.forms[0].action = 'submitLoadJobSearch.do';
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}

	function goAssignTo(businessKey) {
		document.forms[0].assignToBusinessKey.value = businessKey;
		document.forms[0].action = 'setBusinessAlias';
		document.forms[0].submit();
		showWaitBusy();
	}

	function goRefreshStatus() {
		document.getElementById("getStatusUpdateBtn").disabled = true;
		document.forms[0].action = 'updateAsnycStatus';
		document.forms[0].submit();
		showWaitBusy();
	}

	function goClear() {
		document.forms[0].action = 'clearLoadErrors';
		document.forms[0].submit();
		showWaitBusy();
	}

	function goDelete() {
		var yesCallback = function() {
			document.forms[0].action = 'deleteLoadJob';
			document.forms[0].preserveSearchValues.value = "true";
			document.forms[0].submit();
			showWaitBusy();
		}
		showYesNoMessageBox('YES NO', 'WARN',
				"<fmt:message key='warn.delete_objects'/>",
				"<fmt:message key='msg.warn'/>", yesCallback);
		return;
	}

	function goReplay() {
		document.forms[0].action = 'replayLoadJob';
		document.forms[0].preserveSearchValues.value = "true";
		document.forms[0].submit();
		showWaitBusy();
	}

	function goViewJob(jobKey) {
		var url = 'readLoadJob?selectedLoadJobKey=' + encodeURIComponent(jobKey);
		if (parent && parent.BreadCrumbModule) {
			parent.BreadCrumbModule.getDom().contentFrame.setAttribute('src', url);
		} else {
			window.location.href = url;
		}
	}

	function onAssignTo(finderValues) {
		if (finderValues.length > 0) {
			if (finderValues[0][0] != undefined) {
				goAssignTo(finderValues[0][0]);
			}
		}
	}
	function handleAssignTo() {
		doFinderPopup('BusinessFinder', null, 'onAssignTo');
	}

	function handleClearAll(cb) {
		document.forms[0].clearAll.value = cb.checked;
	}

	function resizeArea() {
		var pad = 80;
		var area = $("#loadEventTable");
		if (area != null) {
			var h = $("#loadJobHeader").height() + pad;
			var b = $(document.body).height();
			area.height(b - h);
		}
	}

	function goDownload(dataSource) {
		var action = "downloadFGErrorFile.do";
		var errorFileName = dataSource;
		downloadErrorFile(action, errorFileName);
	}

	function goDownloadTAM(dataSource) {
		var action = "downloadTAMErrorFile.do";
		var errorFileName = "ErrorTAM-" + dataSource;
		downloadErrorFile(action, errorFileName);
	}

	function downloadErrorFile(action, errorFileName) {
		var num = Math.floor((Math.random() * 1000));
		$('input[name=fileLocation]').val(num);
		document.forms[0].action = action;
		document.forms[0].errorFileName.value = errorFileName;
		document.forms[0].submit();
		showWaitBusy();

		function checkFileCompleted() {
			$.ajax({
				type : "POST",
				url : 'tamDownloadUtilCheck.do',
				cache : false,
				dataType : 'text',
				data : {
					downloadKey : num
				},
				success : function(result) {
					if (result.match("^true")) {
						closeWaitBusy();
						clearInterval(checkFileCompletedHandler);
					}
				},
				error : function(error) {
					closeWaitBusy();
					clearInterval(checkFileCompletedHandler);
				}
			});
		}
		checkFileCompletedHandler = setInterval(function() {
			checkFileCompleted()
		}, 1000);
	}
</script>
<body onload="resizeArea()">
	<form name="loadJobAdminForm" action="readLoadJob" method="POST">
		<input type="hidden" name="unsavedData"            value="${loadJobAdminForm.unsavedData}" />
		<input type="hidden" name="selectedLoadJobKey"     value="${loadJobAdminForm.selectedLoadJobKey}" />
		<input type="hidden" name="assignToBusinessKey"    value="${loadJobAdminForm.assignToBusinessKey}" />
		<input type="hidden" name="clearAll"               value="${loadJobAdminForm.clearAll}" />
		<input type="hidden" name="errorFileName"          value="${loadJobAdminForm.errorFileName}" />
		<input type="hidden" name="preserveSearchValues"   value="${loadJobAdminForm.preserveSearchValues}" />
		<input type="hidden" name="fileLocation" />
		<fmt:message var="title"  key="loadadmin.title" />
		<fmt:message var="resize" key="search.result.resize.title" />
		<c:set var="tamErrorButtonShow" value="false" />
		<section class="eto-well" id="loadJobHeader">
			<h1>${title}
				<button type="button" class="eto-icon-btn" style="float:right;"
					onclick="javascript:goRefreshStatus();" id="getStatusUpdateBtn"
					title="<fmt:message key="button.title.refreshJob"/>">
					<i class="md-icon md-icon--sm" style="color: #7a9ba4;">refresh</i>
				</button>
			</h1>
			<span id="unsavedDataMsg">
				<c:if test="${loadJobAdminForm.unsavedData}">
					<fmt:message key="info.unsaved_data" />
				</c:if>
			</span>
			<e2o:errors maxErrors="4" styleId="errors" />		<c:if test="${not empty loadJobAdminForm.jobErrorDetails}">
			<div style="color:red;padding:4px;"><c:out value="${loadJobAdminForm.jobErrorDetails}" /></div>
		</c:if>			<e2i2:instructionsarea>
				<fmt:message key="loadadmin.instructions" />
			</e2i2:instructionsarea>
			<div>
				<div class="row col-sm-12">
					<div class="col-sm-3">
						<div><fmt:message key="loadjob.loadDate" /></div>
						<div><b><c:out value="${loadJobAdminForm.selectedLoadJob.loadDate}" /></b></div>
					</div>
					<div class="col-sm-3">
						<div><fmt:message key="loadjob.id" /></div>
						<div><b><c:out value="${loadJobAdminForm.selectedLoadJob.externalId}" /></b></div>
					</div>
					<div class="col-sm-3">
						<div><fmt:message key="loadjob.loadedBy" /></div>
						<div><b><c:out value="${loadJobAdminForm.selectedLoadJob.loadedBy}" /></b></div>
					</div>
					<div class="col-sm-3">
						<div><fmt:message key="loadjob.loadType" /></div>
						<div>
							<c:set var="loadType" value="${loadJobAdminForm.selectedLoadJob.loadJobType}" />
							<fmt:message var="loadTypeVal" key="upload.${loadType}" />
							<c:choose>
								<c:when test="${not fn:startsWith(loadTypeVal,'???')}">
									<b><c:out value="${loadTypeVal}" /></b>
								</c:when>
								<c:otherwise>
									<b><c:out value="${loadType}" /></b>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
				<div class="row col-sm-12 margin-top-sm-2">
					<div class="col-sm-3">
						<div><fmt:message key="loadjob.state" /></div>
						<div><b><c:out value="${loadJobAdminForm.selectedLoadJob.state}" /></b></div>
					</div>
					<div class="col-sm-3">
						<div><fmt:message key="loadjob.status" /></div>
						<div><b><c:out value="${loadJobAdminForm.selectedLoadJob.status}" /></b></div>
					</div>
					<div class="col-sm-6">
						<div><fmt:message key="loadjob.datasource" /></div>
						<div style="word-break: break-word; white-space: normal;">
							<b><c:out value="${loadJobAdminForm.selectedLoadJob.datasource}" /></b>
						</div>
					</div>
				</div>
				<div class="row col-sm-12 margin-top-sm-2">
					<div class="col-sm-3">
						<label class="eto-checkbox">
							<input class="eto-checkbox__field" type="checkbox"
								onclick="handleClearAll(this)" name="checkbox-checked"
								${loadJobAdminForm.clearAll ? 'checked="checked"' : ''}>
							<span class="eto-checkbox__box"></span>
							<span class="eto-checkbox__label"><fmt:message key="loadadmin.clearAllMatching" /></span>
						</label>
					</div>
				</div>
			</div>
		</section>
		<div class="row margin-left-sm-1 margin-right-sm-1">
			<div class="col-sm-6 margin-top-xs-2 margin-bottom-xs-2">
				<fmt:message var="title" key="loadadmin.detail" />
				<label id="loadJobHeader"
					style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px; margin-right: 30px">${title}</label>
			</div>
			<div class="col-sm-6 margin-top-xs-2 margin-bottom-xs-2">
				<div class="eto-btn-group" style="float: right;">
					<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'CorrectBEAliasEvent')}">
						<button type="button" class="eto-btn"
							disabled="${canProcess ? 'no' : 'yes'}"
							onclick="javascript:handleAssignTo()">
							<fmt:message key="button.correct" />
						</button>
					</c:if>
					<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'ClearUploadEvent')}">
						<button type="button" class="eto-btn"
							disabled="${canProcess ? 'no' : 'yes'}"
							onclick="javascript:goClear()">
							<fmt:message key="button.clear" />
						</button>
					</c:if>
					<div style="border: 2px #e4e8e8 solid;"></div>
					<c:if test="${not empty loadJobAdminForm.selectedLoadJob.loadEvents}">
						<button type="button" class="eto-icon-btn"
							onclick="javascript:goRefresh();"
							title="<fmt:message key="button.refresh"/>">
							<i class="md-icon md-icon--sm" style="color: #7a9ba4;">refresh</i>
						</button>
					</c:if>
					<c:if test="${loadJobAdminForm.selectedLoadJob.loadJobType == 'FunctionalGroupItemUploadUI'
					           || loadJobAdminForm.selectedLoadJob.loadJobType == 'FunctionalGroupConfigUploadUI'}">
						<c:if test="${loadJobAdminForm.selectedLoadJob.status == 'ERROR'}">
							<button type="button" class="eto-icon-btn"
								onclick="javascript:goDownload('${loadJobAdminForm.selectedLoadJob.datasource}')"
								title='<fmt:message key="button.download.error" />'>
								<i class="md-icon md-icon--sm" style="color: #7a9ba4;">file_download</i>
							</button>
						</c:if>
					</c:if>
					<fmt:message var="rangeErr" key="errors.allocation.not.in.range" />
					<c:forEach var="loadEventErr"
						items="${loadJobAdminForm.selectedLoadJob.loadEvents}"
						varStatus="rowCount">
						<c:if test="${fn:contains(loadEventErr.loadEventData, rangeErr)}">
							<c:set var="tamErrorButtonShow" value="true" />
						</c:if>
					</c:forEach>
					<c:if test="${loadJobAdminForm.selectedLoadJob.loadJobType == 'FunctionalGroupItemUploadUI'}">
						<c:if test="${loadJobAdminForm.selectedLoadJob.status == 'ERROR' && tamErrorButtonShow}">
							<button type="button" class="eto-icon-btn"
								onclick="javascript:goDownloadTAM('${loadJobAdminForm.selectedLoadJob.datasource}')"
								title='<fmt:message key="button.download.error.tam" />'>
								<i class="md-icon md-icon--sm" style="color: #7a9ba4;">file_download</i>
							</button>
						</c:if>
					</c:if>
				</div>
			</div>
		</div>
		<div id="grid-result" style="padding-bottom: 7%;"
			class="margin-left-sm-2 margin-right-sm-2">
			<div class="eto-grid" id="grid-example-1">
				<div class="eto-grid-scroll">
					<table style="width: 100%">
						<thead>
							<tr>
								<th>
									<label class="eto-checkbox" id="loadEventTable_globalrowselector">
										<input class="eto-checkbox__field" type="checkbox"
											name="checkbox-checked"
											onclick="i2uiToggleAllRowsSelectionState(this,'loadEventTable')">
										<span class="eto-checkbox__box"></span>
									</label>
								</th>
								<th><fmt:message key="loadevent.type" /></th>
								<th><fmt:message key="loadevent.data" /></th>
								<th><fmt:message key="loadevent.context" /></th>
								<th><fmt:message key="loadevent.loadDate" /></th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty loadJobAdminForm.selectedLoadJob.loadEvents}">
									<c:forEach var="loadEvent"
										items="${loadJobAdminForm.selectedLoadJob.loadEvents}"
										varStatus="rowCount">
										<tr>
											<td>
												<c:if test="${loadEvent.canEventBeCleared}">
													<label class="eto-checkbox" id="loadEventTable_rowselector">
														<input class="eto-checkbox__field" type="checkbox"
															id="loadEventTable_globalrowselector"
															name="selectedEventKeys"
															onclick="i2uiToggleRowSelectionState(this,'tableRow${(rowCount.count) % 2}','loadEventTable',null,true)">
														<span class="eto-checkbox__box"></span>
													</label>
													<c:set var="canProcess" value="true" />
												</c:if>
											</td>
											<td><c:out value="${loadEvent.type}" /></td>
											<td style="word-break: break-word; white-space: normal;">
												<c:out value="${loadEvent.loadEventData}" />
											</td>
											<td><c:out value="${loadEvent.loadEventContext}" /></td>
											<td><c:out value="${loadEvent.insertDate}" /></td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan="5">No Records Found</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="footer">
			<div class="row" style="margin: 10px;">
				<nav class="eto-form__btns">
					<div class="eto-btn-group">
						<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DeleteJob')}">
							<button type="button" class="eto-btn eto-btn--primary"
								onclick="javascript:goDelete()">
								<fmt:message key="button.delete" />
							</button>
						</c:if>
						<c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'UploadFile')}">
							<button type="button" class="eto-btn"
								${loadJobAdminForm.replayAllowed ? '' : 'disabled'}
								onclick="javascript:goReplay()">
								<fmt:message key="button.replay" />
							</button>
						</c:if>
						<button type="button" class="eto-btn" onclick="javascript:goBack()">
							<fmt:message key="button.back" />
						</button>
					</div>
				</nav>
			</div>
		</div>
	</form>
</body>
</html>
