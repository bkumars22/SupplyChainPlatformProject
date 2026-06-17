<%@ include file="common.jspf"%>

<e2i2:doctype />
<e2i2:skin name="e2-modern" />
<e2i2:preferences />
<e2i2:clientcache />

<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="UploadFile" />
<script>
	var submitButtonClicked=false;
function goUpload()
{
	if ($("#adminUploadfile").val() == '') {
		showOkMessageBox('OK', 'WARN', "Please select a file to upload",
				"<fmt:message key='msg.warn'/>", function() {
				});
		return false;
	} else {
		if(submitButtonClicked==false) {
		document.forms[0].action = "submitUploadFile";
			document.forms[0].submit();
			submitButtonClicked=true;
			showWaitBusy();
		}
	}
}

function goViewJob(jobKey)
{
	document.forms[0].selectedLoadJobKey.value = jobKey;
	document.forms[0].action = "readLoadJob";
	document.forms[0].submit();
	showWaitBusy();
}

function goDownload()
{
	document.forms[0].action = "downloadTemplate";
	document.forms[0].submit();
	showWaitBusy();
	setTimeout(function(){
		closeWaitBusy();
	}, 3000);
	document.forms[0].action = "submitUploadFile";
}

$(document).ready(
		function() {
			$('select[name="messageType"]').change(function(){
				var uploadType = $(this).val(); 
				$('input[name="downloadTemplateType"]').val(uploadType);
				if(uploadType=="MassUpdateCostRecordUI"||uploadType=="MassUpdateCostForecastUI"||uploadType=="TAMAllocationDeleteUploadUI"){
					$("#downloadButton").css("visibility", "");
				}else{
					$("#downloadButton").css("visibility", "hidden");
				}
			});
			
			var uploadType = $("#messageType option:selected").val();
			if(uploadType=="MassUpdateCostRecordUI"||uploadType=="MassUpdateCostForecastUI"||uploadType=="TAMAllocationDeleteUploadUI"){
				$("#downloadButton").css("visibility", "");
			}else{
				$("#downloadButton").css("visibility", "hidden");
			}
});  
</script>
</head>
<body style="margin: 5px">
	<label
		style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px; margin-right: 30px"><fmt:message
			key="upload.main.header" /></label>
	<c:set var="maxFiles" value="${uploadForm.maxFiles}" />
	<form name="uploadFileForm" action="submitUploadFile" method="POST"
		enctype="multipart/form-data">
		<input type="hidden" name="selectedLoadJobKey" />
		<input type="hidden" name="downloadTemplateType" />
		<input type="hidden" name="uploadMenuType" value="${uploadForm.uploadMenuType}" />
		<c:if test="${!uploadForm.error}">
		<e2i2:instructionsarea>
			<fmt:message key="upload.instructions">
				<fmt:param value="${maxFiles}" />
			</fmt:message>
		</e2i2:instructionsarea>
		</c:if>
		<e2o:errors maxErrors="4" styleId="errors" />
		<c:if test="${!empty uploadForm.successLinks}">
			<div width="100%" style="font-weight: bold" class="instructionsArea">
				<c:forEach var="successLink" items="${uploadForm.successLinks}">
					<li><fmt:message key="upload.success_link">
							<fmt:param value="${successLink['fileName']}" />
						</fmt:message> <c:set var="loadJobKey" value="${successLink['loadJobKey']}" />
						<a href="javascript:goViewJob('${loadJobKey}')">${successLink['transactionId']}</a>
					</li>
				</c:forEach>
			</div>
		</c:if>
		<c:if test="${!uploadForm.error}">
		<c:forEach begin="1" end="${maxFiles}" var="fileCount">
			<div class="row">
				<div class="col-xs-12 col-sm-6">
					<div class="row">
						<div class="col-sm-6">
							<div class="eto-select" id="select${fileCount}">
								<label class="eto-select__label"><fmt:message
										key="upload.dataFileField" /> ${fileCount}</label>
								<c:set var="xslxType" value="${uploadForm.xlxsType}"/>
								<div class="eto-select__field-container">
									<select name="messageType" id="messageType"
										Class="inputField" onchange="getmessageType();">
										<c:forEach var="msgGroups"
											items="${uploadForm.availableMessageTypes}">
											<c:forEach var="msgType" items="${msgGroups.value}">
												<fmt:message var="label" key="upload.${msgType}" />
												<c:if test="${fn:startsWith(label,'???')}">
													<c:set var="label" value="${msgType}" />
												</c:if>
												<c:if test="${!fn:contains(xslxType,msgType)}">
													<option value="${msgType}">${label}(*.xls)</option>
												</c:if>
												<c:if test="${fn:contains(xslxType,msgType)}">
													<option value="${msgType}">${label}(*.xlsx)</option>
												</c:if>
											</c:forEach>
										</c:forEach>
									</select>
								</div>
							</div>
						</div>
					</div>
					<div class="row" style="margin-top: 10px;">
						<div class="col-xs-12 col-sm-12">
							<div class="eto-upload" id="uploadfile">
								<label class="eto-upload__dropzone"> <span
									class="eto-upload__icon"> <span class="md-icon">cloud_upload</span>
								</span> 
								<span class="eto-upload__manual"> <span	class="eto-btn" tabindex="0" style="margin-top: 12px;">Select Files</span>
								<!--  
								commenting below lines as it is not supporting in IE and EDGE
								<span class="eto-upload__text">Drag and drop files here to upload.</span>
								<span class="eto-upload__manual"> <span	class="eto-btn" tabindex="0">Or Select Files</span>
								</script>
								-->
								  <input
										type="file" name="uploadFile"
										id="adminUploadfile" accept=".xls,.xlsx"
										style="cursor: pointer; border: 1px solid black;" size="40"/>
								</span>
								</label>
								<div class="eto-upload__loading">
									<span class="eto-upload__text"> <span
										class="eto-upload__filenames"></span> <span
										class="eto-btn--link eto-btn--icon-only cancel"> <span
											class="md-icon">close</span>
									</span>
									</span> <span class="eto-upload__determinate"> <span
										class="eto-upload__progress-bar"> <span
											class="eto-upload__progress-bar__fill" style="width: 0%"></span>
									</span> <span class="eto-upload__progress-label">0%</span>
									</span> <span class="eto-upload__indeterminate"> <svg
											preserveAspectRatio="xMidYMin meet" viewBox="0 0 150 100"
											version="1.1" xmlns="http://www.w3.org/2000/svg"
											xmlns:xlink="http://www.w3.org/1999/xlink">
        <g class="eto-loading" stroke="none" stroke-width="1"
												fill="none" fill-rule="evenodd">
          <path class="eto-loading__background"
												d="M75,0 L150,50 L0,50 L75,0 Z M0,50 L150,50 L75,100 L0,50 Z"
												fill-opacity="0.5" fill="#FFFFFF"></path>
          <polygon class="eto-loading__top-left"
												points="75 0 75 50 0 50"></polygon>
          <polygon class="eto-loading__top-right"
												points="75 0 150 50 75 50"></polygon>
          <polygon class="eto-loading__bottom-right"
												points="150 50 75 50 75 100"></polygon>
          <polygon class="eto-loading__bottom-left"
												points="0 50 75 50 75 100"></polygon>
        </g>
      </svg>
									</span>
								</div>
								<ul class="eto-upload__files"></ul>
							</div>
						</div>
					</div>
					<script>
								new eto.SelectInput( { el :
								   document.querySelector('#messageType')
								}); 
								 var sel = document.getElementById('messageType');
								 var opt;
							        for ( var i = 0, len = sel.options.length; i < len; i++ ) {
							            opt = sel.options[i];
							            if ( opt.value === parent.mcmApp.uploadFileType ) {
							            	opt.selected = true;
							                break;
							            }
							        }
							        
								</script>
				</div>
			</div>
		</c:forEach>
		</c:if>
		<c:if test="${!empty uploadForm.errorDetails}">
			<table class="eto-table" id="table-example-3" width="100%">
				<thead>
					<tr>
						<th><fmt:message key="loadevent.type" /></th>
						<th><fmt:message key="loadevent.data" /></th>
						<th><fmt:message key="loadevent.context" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="loadError" items="${uploadForm.errorDetails}">
						<tr>
							<td><c:out value="${loadError.type}" /></td>
							<td><c:out value="${loadError.message}" /></td>
							<td><c:out value="${loadError.line}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${!uploadForm.error}">
		<div class="eto-btn-group">
			<button type="button" class="eto-btn eto-btn--primary"
				onclick="javascript:goUpload()">
				<fmt:message key="button.submit" />
			</button>
			<button type="button" class="eto-btn"
				id="downloadButton" onclick="javascript:goDownload()">
				<fmt:message key="button.download" />
			</button>
		</div>
		</c:if>
		<script type="text/javascript">
		function getmessageType(){
			parent.mcmApp.uploadFileType = $("#messageType").val();
		}
	var upload = new eto.FileUpload({ el: document.querySelector('#uploadfile') });
		
		upload.on('fileAdded', function(fileObj) {
			$("#uploadfile input").attr("accept",".xls,.xlsx");
		});
			
		upload.on('fileRemoved', function(fileObj) {
			$("#uploadfile input").attr("accept",".xls,.xlsx");
		});
		upload.on('fileNotAllowed', function(fileObj) {
			$("#uploadfile input").attr("accept",".xls,.xlsx");
		});
		</script>
	</form>

</body>
</html>
