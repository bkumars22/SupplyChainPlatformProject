<%@ include file="../common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<html>
<head>
<e2ot:pcmSupport calendarSupport="true" ajaxSupport="true" />
<e2ot:help contextName="Download-TAM" />
</head>
<c:set var='reportType' value='' />
<script>

	function init() {
		resizeResultArea();
		setupHideColumnState('tamMultipleDownloadResultTable', true);
		setupOrderColumnState('tamMultipleDownloadResultTable', true, {
			mouseOverTitle : '<fmt:message key="label.moveColumn"/>'
		});
	}
	function goAsynchronusSearch(downloadType){
		$('input[name=downloadOption]').val(downloadType);
		document.forms[0].action = "asynhronusSearchForTamDownload";
		document.forms[0].submit();
		showWaitBusy();
		<c:set var='reportType' value='${tamDownloadForm.downloadOption}' />
		}
	function goDownlaod(downloadType) {
		var checkFileCompletedHandler = null;
		$('input[name=downloadOption]').val(downloadType);
		document.forms[0].action = "downloadMultipleTAM";
		var num = Math.floor((Math.random() * 1000));
		$('input[name=fileLocation]').val(num);
		document.forms[0].submit();
		showWaitBusy();
		//below line requred to reset action as the page is not reloading
		document.forms[0].action = "searchTAMMultipleDownload";
		function checkFileCompleted(){
			$.ajax({
				type: "POST",
				url: 'tamDownloadUtilCheck',
				cache: false,
				dataType: 'text',
				data: {
					downloadKey: num
				},
				success: function(result) {
					if(result.match("^true")){
						closeWaitBusy();
						clearInterval(checkFileCompletedHandler);
					}
				},
				error: function(error){
					// alert('error'+error.statusText);
					closeWaitBusy();
					clearInterval(checkFileCompletedHandler);
				}
			}); 
		}
		checkFileCompletedHandler = setInterval(function(){ checkFileCompleted() }, 3000);
	}
	
</script>
<style type="text/css">
select {
	border-radius: 4px;
	width: 150px;
	height: 30px;
}
</style>
<body onload="init()" style="overflow-y : scroll;">
	<form  name="tamDownloadForm" action="searchTAMMultipleDownload" method="POST">
	<input type="hidden" name="downloadOption">
	<input type="hidden" name="fileLocation">
		<div style="font-weight: bold; white-space: pre-line;">
            <logic:messagesPresent message="true">
                <html:messages id="message" message="true">
                    <li>${message}</li>
                </html:messages>
            </logic:messagesPresent>
        </div>
	<script>
			var gridColumns = [];
			var selectionType = 'none';
			var jsonColumn =  '${tamDownloadForm.columns}';

			gridColumns.push('<fmt:message key="tamDownload.group_name" />');
			gridColumns.push('<fmt:message key="tamDownload.description" />');
			gridColumns.push('<fmt:message key="tamDownload.commodity_name" />'+'_EXPANDCELL');
			gridColumns.push('<fmt:message key="tamDownload.mrpSite" />');
			gridColumns.push('<fmt:message key="tamDownload.location" />');
			gridColumns.push('<fmt:message key="tamDownload.groupType" />');
			gridColumns.push('<fmt:message key="tamDownload.parent_name" />');
			gridColumns.push('<fmt:message key="tamDownload.tamExist" />');

			var gridRows = [];
			<c:forEach var="row" items="${tamDownloadForm.searchResult.values}" varStatus="rowCount">
				<c:set var="fg" value="${row.values[0]}" />
				<c:set var="site" value="${row.values[1]}" />
				<c:set var="siteDetail" value="${row.values[2]}" />

				var row = {};
				row['checkboxValue']= '';
				row['<fmt:message key="tamDownload.group_name" />']= '<c:out value = "${fg.name}"/>';
				row['<fmt:message key="tamDownload.description" />']= '<c:out value = "${fg.description}"/>';
				
				<c:set var="categories" value=""></c:set>
				<c:set var="categoriesValue" />
				<c:forEach items="${fg.functionalGroupItems}" var="item" varStatus="row">
					<c:forEach items="${item.categories}" var="category">
						<c:if test="${!fn:contains(categories, category)}">
							<c:set value="${categories},${category.categoryName}" var="categories" />
						</c:if>
					</c:forEach>
				</c:forEach>
				<c:set var="categoriesValue" value="${fn:length(categories) > 0 ? fn:substring(categories,1,fn:length(categories)) : ''}" />
				row['<fmt:message key="tamDownload.commodity_name" />']= '<c:out value = "${categoriesValue}"/>';
				row['<fmt:message key="tamDownload.mrpSite" />']= '<c:out value = "${siteDetail.mrpSite}"/>';
				row['<fmt:message key="tamDownload.location" />']= '<c:out value = "${site.siteDescription}"/>';
				row['<fmt:message key="tamDownload.groupType" />']= '<c:out value = "${fg.type}"/>';
				row['<fmt:message key="tamDownload.parent_name" />']= '<c:set value="${fn:length(fg.parentFunctionalGroup)}" var="parentCount"></c:set><c:forEach items="${fg.parentFunctionalGroup}" var="parent" varStatus="row">'
							+'<c:out value="${parent.name}" />'
							+'<c:if test="${parentCount != row.count}">'
							+'<c:out value=","></c:out></c:if></c:forEach>';
				row['<fmt:message key="tamDownload.tamExist" />']= '<span class="tam-exist-loader" data-fgid="' +  ${fg.functionalGroupId} + '" data-sitekey="' + ${site.siteKey} + '">Loading...</span>';

				gridRows.push(row);
			</c:forEach>
			</script>
			
		<e2ot:searchContainerControl searchFields="${tamDownloadForm.allParameters}"
			formName="tamDownloadForm" form="${tamDownloadForm}" resultTableId="tamMultipleDownloadResultTable"
			showFilterCollapsed="${tamDownloadForm.filterAreaCollapsed}"
			showFilter="${tamDownloadForm.showFilterArea}" numColumns="3" />
		<e2ot:searchResultsControl searchForm="${tamDownloadForm}" formName="tamDownloadForm"
			resultTableId="tamMultipleDownloadResultTable" showOrderMenu="true" showHideMenu="true" title="Download Allocation"/>
			
		
		<input type="hidden" name="requestType" value="${requestType}" />
		<input type="hidden" name="previousAction" value="${previousAction}" />
		<input type="hidden" name="nextAction" value="${nextAction}" />
		<input type="hidden" name="buttonAction" value="${buttonAction}" />
	</form>
	
	<script type="text/javascript">
		$(document).ready(function() {		
			$('input[type="checkbox"]').each(function (){
				$(this).attr('disabled',true);
			});
			  $("#defaultDisplay").attr('disabled',false);
			  $("#condensedCheckBox").attr('disabled',false);
			<c:if test="${!empty tamDownloadForm.searchResult.values}">
			var div = document.createElement('div');
		    div.className = 'eto-btn-group';
		    var d = '<button type="button" class="eto-btn eto-btn--primary" onclick="javascript:goDownlaod(\'supplierDownload\')">Supplier Allocation Download</button>'
		 		+ '<button type="button" class="eto-btn eto-btn--primary" onclick="javascript:goDownlaod(\'itemDownload\')">Item Allocation Download</button>'
		 		+'<button type="button" class="eto-btn eto-btn--primary" onclick="javascript:goDownlaod(\'pastSupplierDownload\')">Past Supplier Allocation Download</button>'
		 		+'<button type="button" class="eto-btn eto-btn--primary" onclick="javascript:goDownlaod(\'pastItemDownload\')">Past Item Allocation Download</button>';
		 	 div.innerHTML =d;
		    document.getElementById('searchFiledsButtons').appendChild(div);
		    
		    var downloadCount = "Supplier Allocation result found : "+${tamDownloadForm.supplierCount}+" , Item Allocation result found : "+${tamDownloadForm.itemCount};
		    
		  /* if ($('label:contains("Records")')) {
		    	$('label:contains("Records")').text("downloadCount");
		   
		    } */
		
		  $("#ManageFG").html(downloadCount);
		    
		    $('button[title="File Download"]').removeAttr( "onclick" );
		    
		    </c:if>

			document.body.addEventListener('DOMSubtreeModified', handleClickDOM, true); 
			
			$('.eto-grid-expand__content').each(function(){
				if($(this).html().length != 0){
					$(this).closest( "td").addClass("eto-grid-expand--expanded");
				}
			});
			
			$('.eto-grid-expand__toggle').remove();
		});
		
		
function handleClickDOM(){
	$('.eto-grid-expand__content').each(function(){
		if($(this).html().length != 0){
			$(this).closest( "td").addClass("eto-grid-expand--expanded");
		}
	});
}
function gridUpdateCallBack(){
    $('.eto-grid-expand__content').each(function(){
        if($(this).html().length != 0){
            $(this).closest( "td").addClass("eto-grid-expand--expanded");
        }
    });
    $('.eto-grid-expand__toggle').remove();
    grid.alignRows();
}

/**
 * Function to load TAM existence status via async API for all functional groups
 * Uses apiService.getAsync() for guaranteed asynchronous execution
 */
function loadTAMExistenceStatus() {
	// Find all TAM loader spans and fetch their data
	$('.tam-exist-loader').each(function() {
		var loader = $(this);
		var functionalGroupId = loader.data('fgid');
		var siteKey = loader.data('sitekey');

		if (functionalGroupId && siteKey) {
			// Call the async API using apiService.getAsync()
			apiService.getAsync('checkTAMExistByFunctionalGroupAndSite', {
				functionalGroupId: functionalGroupId,
				siteKey: siteKey
			})
			.then(function(response) {
				// Update the cell with the response (true/false)
				var tamStatus = response === true ? 'Yes' : 'No';
				loader.text(tamStatus);
			})
			.catch(function(error) {
				console.error('Error checking TAM existence for FG ID: ' + functionalGroupId, error);
				loader.text('Error');
			});
		} else{
		   loader.text('');
		}
	});
}

parent.parent.reportCall = function() {
	
    <c:url var="linkHref" value="initReports">
    <c:param name="reportType" value="${reportType}" />
    </c:url>
    reloadBreadCrumb('initReports');
    document.forms[0].action = "${linkHref}";
    document.forms[0].submit();
    showWaitBusy();
}


// Load TAM existence status when page is ready
$(document).ready(function() {
	// Load TAM existence data after grid is rendered
	setTimeout(function() {
		loadTAMExistenceStatus();
	}, 500);
});
</script>
</body>
</html>