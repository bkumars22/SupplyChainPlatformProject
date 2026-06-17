<%@tag%>
<%@ attribute name="formName" required="true"%>
<%@ attribute name="id" required="false"%>
<%@ attribute name="searchForm" required="true"
	type="com.scplatform.pcm.searchframework.dto.SearchForm"%>
<%@ attribute name="pagingCallback" required="false"%>
<%@ attribute name="collapsable" required="false" type="Boolean"%>
<%@ attribute name="resultTableId" required="false"%>
<%@ attribute name="showOrderMenu" required="false" type="Boolean"%>
<%@ attribute name="showHideMenu" required="false" type="Boolean"%>
<%@ attribute name="showClearSort" required="false" type="Boolean"%>
<%@ attribute name="showTitle" required="false" type="Boolean"%>
<%@ taglib uri="/WEB-INF/i2/i2uitaglib.tld" prefix="e2i2"%>
<%@ taglib uri="/WEB-INF/i2/scplatform-html.tld" prefix="html"%>
<%@ attribute name="title" required="false"%>
<%@ attribute name="showGrid" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="e2ot"%>
<%@ taglib uri="/WEB-INF/i2/e2pcmfn.tld" prefix="e2ofn"%>
<fmt:setBundle basename="scplatform-messages" />
<c:if test="${empty resultTableId}">
	<c:set var="resultTableId" value="searchResultTable" />
</c:if>
<c:if test="${showTitle == false}">
	<fmt:message var="title" key="search.result.title" />
</c:if>
<style>
 .eto-grid{
   margin-bottom:1rem;
}
.eto-grid-scroll{
  overflow-x:hidden !important;
}
</style>
<script>
	/* set the restricted columns to a global variable */
	var restrictedColumnMap = new Map();
    <c:forEach var="restrictedColumn" items="${searchForm.restrictedColumnList}">
			<fmt:message var="columnName" key="${restrictedColumn}" />
			<c:if test = "${fn:startsWith(columnName, '???')}">
				<c:set var="columnName" value="${restrictedColumn}" />
			</c:if>
			restrictedColumnMap.set('${columnName}', '${columnName}');
    </c:forEach>
</script>
<fmt:message var="resize" key="search.result.resize.title" />
<fmt:message var="download" key="search.result.download.title" />
<input type="hidden" name="columns" id="columns"  />
<input type="hidden" name="condensedView" id="condensedView" value="${searchForm.condensedView}"/>
<input type="hidden" name="defaultDisplay" id="hiddendefaultDisplay" value="false"/>
<c:if test="${empty searchForm.searchResult}">
	<div class="margin-left-sm-2 margin-top-sm-2">
		<label
			style="white-space: nowrap;clear: none; margin-right: 30px"><h1>${title}</h1></label>
	</div>
	<c:if test="${searchForm.initFlag}">
		<div class="margin-left-sm-2">
			<label><i><fmt:message key="searchFilter.filter.message" />
			</i></label>
		</div>
	</c:if>
	<c:if test="${!searchForm.initFlag}">
		<div style="display: flex; margin-bottom: 100px;">
			<div class="eto-messageblock" data-message-type="info"
				id="message-block-no-results"
				style="margin: auto; width: 100%; margin-top: 5%;">
				<div class="eto-messageblock__body">No records found to
					display, Refine your search in the filters</div>
				<a href="javascript:void(0)" role="button"
					class="eto-messageblock__close"></a>
			</div>
			<script>
				var msgBlock = new eto.MessageBlock({
					el : document.querySelector('#message-block-no-results')
				});
			</script>
		</div>
	</c:if>
</c:if>
<div class="row margin-left-sm-1 margin-right-sm-1 margin-top-sm-2 margin-bottom-sm-2">
	<c:if test="${!empty searchForm.searchResult}">
		<div class="col-sm-5"
			style="display: flex;">
			<label
				style="white-space: nowrap; font-weight: 600; clear: none; font-size: 20px;margin-left: 0rem; margin-right: 0rem;">${title}</label>
			<label style="padding: 0.512rem 0rem 0rem;"><c:choose>
					<c:when test="${searchForm.pagingEnabled}">
						<c:if test="${searchForm.showPageMessage}">
						<!-- LP 03 -->
							<span style="margin: auto; margin-left: 3rem;"><fmt:message key="info.paging.harmony.records">
								<fmt:param value="${searchForm.totalRows}" />
								<%-- <fmt:param value="${searchForm.atPage}" />
								<fmt:param value="${searchForm.maxPage}" /> --%>
							</fmt:message></span>
						</c:if>
					</c:when>
					<c:otherwise>
						<span style="margin-left: 5rem;"><fmt:message key="info.result_count">
							<fmt:param value="${searchForm.totalRows}" />
						</fmt:message></span>
					</c:otherwise>
				</c:choose> </label>

		</div>
		<!-- LP 03 -->
		<div class="col-sm-7 col-xs-7 col-md-7 col-lg-7 col-xl-7" id="savedDisplay" >
			<c:set var="count" value="0"/>
			<div style="display: flex; float: right" class="flex-items-xs-right">
				<span style="white-space: nowrap">Saved Displays</span>
				<!-- LP 03 -->
				<div class="eto-select col-xs-4" id="select-display" style="padding-left: 8px;">
					<div class="eto-select__field-container">
						<select class="eto-select__field" name="selectedDisplay"
							id="selectedDisplayId"
							onchange="changeDisplayColumn(this,this.value)">
							<option value="0">None</option>
							<c:forEach var="display" items="${searchForm.availableDisplay}">
								<option value="${display.key}"
									<c:if test="${display.key == searchForm.selectedDisplay}" >selected </c:if>>
									${display.value}</option>
								<c:set var="count" value="1"/>
							</c:forEach>
						</select>
					</div>
				</div>
				<script type="text/javascript">
					new eto.SelectInput({
						el : document.querySelector('#select-display')
					});
				</script>
				<div class="eto-btn-split eto-dropdown" id="split-button-display" style="box-shadow: none;display: flex;height: 31px;">
					<button class="eto-btn" type="button" onclick="updateDisplay();">Save</button>
					<button type="button"
						class="eto-btn eto-btn--icon-only eto-dropdown__toggle">
						<i class="md-icon">keyboard_arrow_down</i>
					</button>
					<ul class="eto-dropdown__menu">
						<li><a href="javascript:openDisplayModal()">Save As</a></li>
            		 <c:if test="${count==1}">
						<li><a href="javascript:deleteDisplay()">Delete</a></li>
						</c:if>
					</ul>
				</div>
				<script type="text/javascript">
					new eto.Dropdown({
						el : document.querySelector('#split-button-display')
					});
				</script>
			<!-- <span style="border-left: 1px solid gray;margin-left: 16px;height: 40px;"> -->
			<span style="border-left: 1px solid #CCCCCC; margin-left: 16px; height: 40px;"></span>
			<div class="eto-modal" id="save-display-modal">
				<div class="eto-modal__content col-xs-8 col-sm-8 col-lg-6 col-xl-6"
					tabindex="0">
					<header class="eto-modal__header">
						<h3>Save Display</h3>
						<button class="eto-modal__close" data-modal-close="true"></button>
					</header>
					<section class="eto-modal__body">
					<div id="errorpopUp"></div>
						<div id="displaySaveForm">
							<div class="eto-input">
								<label class="eto-input__label">Display Name</label> <input
									size="30" maxlength="60" style="width: 400px;"
									class="eto-input__field" type="text" id="newDisplayName"
									name="selectedDisplayName" placeholder="Enter Display Name">
							</div>
							<label class="eto-input"><span class="eto-input__label">Description</span><span
								class="eto-input__container"><input type="text"
									name="displayDescription" style="width: 400px;"
									class="eto-input__field" id="description" aria-invalid="false"></span></label>
							 
							<div>
								<label class="eto-switch"> 
								<input
									class="eto-switch__field" type="checkbox" id="defaultDisplay"> <span
									class="eto-switch__box"></span> <span
									class="eto-switch__label--on">Set Default</span> <span
									class="eto-switch__label--off">Set Default</span>
								</label>
							</div>  
							<footer class="eto-modal__footer margin-top-sm-1">
								<div class="eto-btn-group">
									<button class="eto-btn" type="button" data-modal-close>Close</button>
									<button class="eto-btn eto-btn--primary"  
										type="button" onclick="saveNewDisplay();">Save</button>
								</div>
							</footer>
						</div>
					</section>
				</div>
			</div>
			<div style="float: right; display: flex;">
				<button type="button" style="font-size: 2.5rem;"
					title="Open in full screen"
					class="eto-icon-btn"
					id="fullscreen" onclick="callfullScreenMethod();">
					<i class="md-icon">fullscreen</i>
				</button>
				<button type="button" style="display: none; font-size: 2.5rem;"
					title="Exit Full Screen"
					class="eto-icon-btn"
					id="fullscreen_exit" onclick="callExitFullScreen();">
					<i class="md-icon">fullscreen_exit</i>
				</button>
				<c:set var='featureFlagForFullBEDownload'
                	value='${e2ofn:getConfigValue("pcm.businessEntity.site.currency.download.flag")}' />
                <c:set var='featureFlagForBomTopLevelDownload'
                                	value='${e2ofn:getConfigValue("scplatform.feature.enable.topLevelBom.download")}' />
				<div id="downloadButtonTD">
				<c:if test="${searchForm.extractEnabled}">
				 <c:choose>
                     <c:when test="${formName == 'businessAdminForm'}">
                        <c:choose>
                            <c:when test="${!featureFlagForFullBEDownload}">
                                <c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DownloadFile')}">
                                    <button type="button"
                                        onclick="submitExtractToFileForBe('OnlyBE')"
                                        style="font-size:2.5rem;"
                                        class="eto-icon-btn"
                                        title="File Download">
                                        <i class="md-icon">file_download</i>
                                    </button>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div id="checkbox-menu-BE-fileDownload" style="display: flex;">
                                    <span role="menu"
                                    class="eto-dropdown"
                                    data-anchor-x="right"
                                    data-anchor-y="bottom"
                                    style="display: flex;">

                                    <button id="downloadButtonId"
                                            class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
                                            style="font-size: 2.5rem;"
                                            title="File Download"
                                            type="button">
                                        <i class="md-icon" id="downloadButton">file_download</i>
                                    </button>

                                    <ul class="eto-dropdown__menu"
                                        id="downloadMenu"
                                        data-affixed
                                        style="position: absolute !important; left: -100px; display: none;">

                                        <li role="menuitem">
                                            <a href="#" onclick="submitExtractToFileForBe('OnlyBE')">
                                                BusinessEntity
                                            </a>
                                        </li>

                                        <li role="menuitem">
                                            <a href="#" onclick="submitExtractToFileForBe('All')">
                                                Full BusinessEntity
                                            </a>
                                        </li>

                                    </ul>
                                    </span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                     </c:when>
                     <c:when test="${formName == 'bomManagementForm' || formName == 'bomSearchForm'}">
                                             <c:choose>
                                                 <c:when test="${!featureFlagForBomTopLevelDownload}">
                                                     <c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DownloadFile')}">
                                                         <button type="button"
                                                             onclick="submitExtractToFileForBom('ALL')"
                                                             style="font-size:2.5rem;"
                                                             class="eto-icon-btn"
                                                             title="File Download">
                                                             <i class="md-icon">file_download</i>
                                                         </button>
                                                     </c:if>
                                                 </c:when>
                                                 <c:otherwise>
                                                     <div id="checkbox-menu-BOM-fileDownload" style="display: flex;">
                                                         <span role="menu"
                                                         class="eto-dropdown"
                                                         data-anchor-x="right"
                                                         data-anchor-y="bottom"
                                                         style="display: flex;">

                                                         <button id="downloadButtonId"
                                                                 class="eto-dropdown__toggle eto-btn eto-btn--link eto-btn--icon-only large-icon"
                                                                 style="font-size: 2.5rem;"
                                                                 title="File Download"
                                                                 type="button">
                                                             <i class="md-icon" id="downloadButton">file_download</i>
                                                         </button>

                                                         <ul class="eto-dropdown__menu"
                                                             id="downloadMenu"
                                                             data-affixed
                                                             style="position: absolute !important; left: -100px; display: none;">

                                                             <li role="menuitem">
                                                                 <a href="#" onclick="submitExtractToFileForBom('ALL')">
                                                                     Full Bom Download
                                                                 </a>
                                                             </li>

                                                             <li role="menuitem">
                                                                 <a href="#" onclick="submitExtractToFileForBom('TopLevel')">
                                                                     Top Level Download
                                                                 </a>
                                                             </li>

                                                         </ul>
                                                         </span>
                                                     </div>
                                                 </c:otherwise>
                                             </c:choose>
                                          </c:when>
                     <c:otherwise>
                         <c:if test="${e2ofn:hasAccess(appContext, 'UPDOWN', 'DownloadFile')}">
                             <button type="button"
                                     onclick="submitExtractToFile()"
                                     style="font-size:2.5rem;"
                                     class="eto-icon-btn"
                                     title="File Download">
                                 <i class="md-icon">file_download</i>
                             </button>
                         </c:if>
                     </c:otherwise>

                 </c:choose>


					</c:if>
				</div>
				<div class="eto-checkbox eto-checkbox-menu"
					id="checkbox-menu-example" style="display: inline;">
					<span role="menu" class="eto-dropdown" style="margin:0px;">
						<button type="button" class="eto-dropdown__toggle eto-icon-btn" title="Settings"
							style="font-size: 2.5rem;">
							<i class="md-icon" >settings</i>
						</button>
						<ul class="eto-dropdown__menu">
							<li name="this-page" data-checkbox-state="checked"
								role="menuitem"><label
								style="border-bottom: 2px #cddfe4 solid; padding: 2rem;"
								class="eto-switch margin-bottom-xs-2" id="grid-compact-control">
									<input class="eto-switch__field" type="checkbox" id="condensedCheckBox"> <span
									class="eto-switch__box"></span> <span
									class="eto-switch__label--off">Default</span><span
									class="eto-switch__label--on">Condensed</span>
							</label></li>
							<li name="all-pages" data-checkbox-state="double" role="menuitem">
								<a href="javascript:void(0)" data-action="config"
								onclick="openGridEditor();"><span class="md-icon mtcm-icon"
									aria-hidden="true">settings</span>Open Grid Editor</a>
							</li>
						</ul>
					</span>
				</div>
				<script type="text/javascript">
					new eto.CheckboxMenu({
						el : document.querySelector('#checkbox-menu-example')
					});
					document
							.querySelector('#grid-compact-control input')
							.addEventListener(
									'change',
									function(e) {
										if (e.target.checked) {
										$("#condensedView").val(true);
											grid.el.classList.add('eto-grid--compact');
										} else {
										$("#condensedView").val(false);
											grid.el.classList
													.remove('eto-grid--compact');
										}
										grid.alignRows();
										
										if (typeof gridUpdateCallBack == 'function') {
                                            gridUpdateCallBack();
                                        }
									});
				 
				function persistCondesdedView()
				{
				if($("#condensedView").val()=="true"){
					  grid.el.classList.add('eto-grid--compact');
				}
				if($("#condensedView").val()=="false"){
				grid.el.classList
				.remove('eto-grid--compact');
				}
				grid.alignRows();
				}
				</script>
			</div>
		</div>
		</div>
	</c:if>
</div>
<div class="row margin-left-sm-2" id="ManageFG" style="display: flex;">
</div>
<c:if test="${!empty searchForm.searchResult}">
	<div id="grid-result" style="padding-bottom: 7%;"
		class="margin-left-sm-2 margin-right-sm-2"></div>
	<jsp:doBody />
</c:if>

<c:if
	test="${searchForm.pagingEnabled and !empty searchForm.searchResult}">
	<div class="footer">
	<div class="row" id="scroller" style="overflow-x: scroll; margin: 0px; position: relative;">
	<div class="col-xs-12"><div id="staticdiv" style="height: 5px; width:100%;"></div></div></div>
		<div class="margin-left-xs-1 margin-right-xs-1 margin-top-xs-2" style="    display: flex;">
			<div class="col-xs-12 col-sm-6">
				<nav class="eto-form__btns">
					<div class="eto-btn-group" id="searchFiledsButtons"></div>
				</nav>
			</div>
			<div class="col-xs-12 col-sm-6">
				<div id="paginationDiv" style="display: flex;">
					<div
						style="margin-right: -15px !important; display: flex; margin: auto;"
						id="paginationDivInner">
						<e2ot:pagingControl searchForm="${searchForm}"
							refreshOnPageSize="true" formName="${formName}"
							pagingCallback="${pagingCallback}" />
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>
<div class="eto-modal" id="grid_editor_modal" style="display: block;">
	<div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
		<header class="eto-modal__header">
			<span>Table Editor</span>
			<button class="eto-modal__close" type="button" id="grid-editor-close" onclick="reloadGrid();" data-modal-close></button>
		</header>
		<section class="eto-modal__body">
			<div class="display-case container">
			<nav class="eto-tabs" id="tabs-editor-1">
				<div class="eto-tabs__container">
					<div class="eto-tabs__scroll">
						<a class="eto-tabs__tab eto-tabs__tab--active" data-tab="#tab-1"
							tabindex="0"><span class="eto-tabs__tab-content">Define
								Grid Column</span><span class="eto-tabs__tab-close"></span></a>
					</div>
				</div>
				<div class="eto-tabs__btns">
					<a class="eto-tabs__btn eto-tabs__btn--backward"></a> <a
						class="eto-tabs__btn eto-tabs__btn--forward"></a>
				</div>
			</nav>
			<div class="eto-tab-content">
				<section class="eto-tab-content__item" id="tab-1">
					<div class="margin-top-sm-2">
						<div id="eto-grid-editor-display"></div>
					</div>
				</section>
			</div>
			</div>
		</section>
		<footer class="eto-modal__footer">
			 <button class="eto-btn eto-btn--primary" id="data-save" type="button"
				data-modal-close>Apply</button>
		</footer>
	</div>
</div>

<%-- These fields are used to post the order and show/hide state of the result set--%>
<c:if test="${showHideMenu}">
	<c:set var="fieldId">
		<c:out value="hcmState_${resultTableId}" />
	</c:set>
	<input type="hidden" id="${fieldId}" name="${fieldId}"
		value="${fn:escapeXml(param[fieldId])}" />
</c:if>
<c:if test="${showOrderMenu}">
	<c:set var="fieldId">
		<c:out value="ocmState_${resultTableId}" />
	</c:set>
	<input type="hidden" id="${fieldId}" name="${fieldId}"
		value="${fn:escapeXml(param[fieldId])}" />
</c:if>
<c:set var="grid" value="${empty showGrid?true:false}"/>
<c:if test="${grid}">
<script type="text/javascript" src="./js/gridModule.js"></script>
</c:if>

<script type="text/javascript">
	var ge = null;
	var gridEditorModal =  null;
	var columns = [];
	
	function openGridEditor() {
		gridEditorModal =  new eto.Modal({
			el : document.querySelector('#grid_editor_modal')
		});
		$('.eto-grid-scroll table').attr('id', '${resultTableId}');
		new eto.Tabs({
			el : document.querySelector('#tabs-editor-1')
		});
		$.each(gridRemovedColumn, function (index, value) {
			itemGridTemplate.columns.push(value);
		});
		// if the screen is PRICE_TAM then the grid editor columns should be only from the frozen part not from timeline part
		var disableFreeze = false;
		if (typeof screenName != 'undefined' && screenName == "PRICE_TAM") {
			var toBeRemovedColumn = [];
			$.each(itemGridTemplate.columns, function (index, value) {
				if(!value.frozen)
					toBeRemovedColumn.push(value);
		    });
	    	itemGridTemplate.columns = itemGridTemplate.columns.filter(function(col) {
				return !toBeRemovedColumn.includes(col);
			});
			disableFreeze = true;
		}
		columns = itemGridTemplate.columns;
		if(ge == null) {
			ge = new eto.GridEditorDisplay({
				el : document.querySelector('#eto-grid-editor-display'),
				disableCollapse : false,
				disableFreeze : disableFreeze,
				frozenIcon : 'ac_unit',
				columns : columns
			});
		} else {
			ge.updateColumns(columns);
		}
		gridEditorModal.open();
	}
	
	function reloadGrid() {	
	   			gridRemovedColumn = [];
				itemGridTemplate.columns = "";
				itemGridTemplate.columns = ge.getColumns();
				
				// if the screen is PRICE_TAM then append the remaining timeline part
				if (typeof screenName != 'undefined' && screenName == "PRICE_TAM") {
					if (typeof timeLineColumns != 'undefined' && timeLineColumns instanceof Array) {
						$.each(timeLineColumns, function( index, value ) {
							itemGridTemplate.columns.push(value);
						});
					}
				}
				
				$.each(itemGridTemplate.columns, function (index, value) {
					if (value.removed)
						gridRemovedColumn.push(value);
			    });
				
				itemGridTemplate.columns = itemGridTemplate.columns.filter(function(el) {
					return !gridRemovedColumn.includes(el);
				});
				
		  createNewGridHandleEvents();
		  gridEditorModal.close();
	}
	
	document.querySelector('#data-save').addEventListener('click', function(e) {
		var gridEditorcolumns = ge.getColumns();
		// if the screen is PRICE_TAM then the grid editor columns should be in frozen state always and collapsed should be consistent with the child column state
		if (typeof screenName != 'undefined' && screenName == "PRICE_TAM") {
		$.each(gridEditorcolumns, function (index, value) {
				value.frozen = true;
				var collapsedState = (value.collapsed) ? true : false;
				if (value.columns instanceof Array) {
					var subColumns = value.columns;
					for (var i=0; i<subColumns.length; i++) {
						var subColumn = subColumns[i];
						subColumn.collapsed = collapsedState;
					}
				}
	    });
		}
		$("#columns").val(JSON.stringify(gridEditorcolumns));
		updateGrid();
	});
	
	if ($("#grid-result").length)
		$("#grid-result .eto-grid .eto-grid-scroll table").css('width', '100%');

	window.onscroll = function(ev) {

		if (!(typeof $('#grid-result .eto-grid')[0] == 'undefined')) {
			var offset = $('#grid-result .eto-grid')[0].getBoundingClientRect().top;

			if (offset < 0) {
				$("#grid-result .eto-grid .eto-grid-frozen table thead tr th")
				.css(
						{
							'transform' : 'translate3d(0px,'
									+ (-offset) + 'px,0px)',
							'position' : 'static',
							'z-index' : 2
						});
		$("#grid-result .eto-grid .eto-grid-scroll table thead tr th")
				.css(
						{
							'transform' : 'translate3d(0px,'
									+ (-offset) + 'px,0px)',
							'position' : 'hidden !important',
							'z-index' : 2
						});
	} else {
		$("#grid-result .eto-grid .eto-grid-frozen table thead tr th")
				.css({
					'position' : 'static',
					'z-index' : 'auto'
				});
		$("#grid-result .eto-grid .eto-grid-scroll table thead tr th")
				.css({
					'position' : 'hidden !important',
					'z-index' : 'auto'
				});
			}
		}
	}
	
	function callfullScreenMethod() {
		$("#fullscreen_exit").show();
		$("#fullscreen").hide();
		$("#expand-container").hide();
	}
	
	function callExitFullScreen() {
		$("#fullscreen").show();
		$("#fullscreen_exit").hide();
		$("#expand-container").show();
	}
	
	function openDisplayModal() {
		new eto.Modal({
			el : document.querySelector('#save-display-modal')
		}).open();
	}
	 
	function saveNewDisplay() {
		document.body.style.cursor='wait';
		 if ($('#defaultDisplay').is(':checked')) {
	            $('#hiddendefaultDisplay').val(true);
	        } else {
	            $('#hiddendefaultDisplay').val(false);
	        }
		var error="";
		if($('#newDisplayName').val().trim().length == 0) {
		 error = '<div class="eto-messageblock" data-message-type="error" id="name_not_empty">'
	           +'<div class="eto-messageblock__body">Please Enter Display Name.</div>'
	           + '<a href="javascript:void(0)" role="button" class="eto-messageblock__close"></a> </div>';
		 $('#errorpopUp').html(error);
		 new eto.MessageBlock({ el: document.querySelector('#name_not_empty') });
		 return;
		} else {
		$('#errorpopUp').html("");
		document.forms['${formName}'].selectedDisplayName.value = $('#newDisplayName').val();
		document.forms['${formName}'].displayDescription.value = $('#description').val();
		document.forms['${formName}'].operation.value='saveDisplay';
		$.each(gridRemovedColumn, function (index, value) {
			itemGridTemplate.columns.push(value);
	    });
		
		// if the action is for Price_TAM screen then save only the common part (which is the frozen part) but not timeline
		if (typeof screenName != 'undefined' && screenName == "PRICE_TAM") {
			var toBeRemovedColumn = [];
			$.each(itemGridTemplate.columns, function (index, value) {
				if(!value.frozen){
					toBeRemovedColumn.push(value);
				}
				if(value.columns){
					value.columns[0].width = value.width;
				}
		    });
	    	itemGridTemplate.columns = itemGridTemplate.columns.filter(function(col) {
				return !toBeRemovedColumn.includes(col);
			});
		}
		
		$("#columns").val(JSON.stringify(itemGridTemplate.columns));
		document.forms['${formName}'].submit(); 
		showBusy();	
		}
	}
	
	function changeDisplayColumn(ctrl,key){
		document.body.style.cursor='wait';
		document.forms['${formName}'].operation.value='changeDisplay';
		document.forms['${formName}'].submit(); 
		showBusy();	 
	}
	
	function updateDisplay(){
		var error="";
		if($('#selectedDisplayId').val() == 0) {
			showOkMessageBox('OK', 'WARN', "Please select any display",
					"<fmt:message key='msg.warn'/>", function() {
					});
			return false;
		} else {
		document.body.style.cursor='wait';
		$.each(gridRemovedColumn, function (index, value) {
			itemGridTemplate.columns.push(value);
	    });
		// if the action is for Price_TAM screen then save only the common part (which is the frozen part) not timeline
		if (typeof screenName != 'undefined' && screenName == "PRICE_TAM") {
			var toBeRemovedColumn = [];
			$.each(itemGridTemplate.columns, function (index, value) {
				if(!value.frozen){
					toBeRemovedColumn.push(value);
				}
				if(value.columns){
					value.columns[0].width = value.width;
				}
		    });
	    	itemGridTemplate.columns = itemGridTemplate.columns.filter(function(col) {
				return !toBeRemovedColumn.includes(col);
			});
		}
		$("#columns").val(JSON.stringify(itemGridTemplate.columns));
		document.forms['${formName}'].operation.value='updateDisplay';
		document.forms['${formName}'].submit(); 
		showBusy();
		}
	}
	
	function deleteDisplay(){
		document.body.style.cursor='wait';
		document.forms['${formName}'].operation.value='deleteDisplay';
		document.forms['${formName}'].submit(); 
		showBusy();
	}
	
	function updateGrid(){
		document.body.style.cursor='wait';
		document.forms['${formName}'].operation.value='refreshDisplay';
		document.forms['${formName}'].submit(); 
		showBusy();
	}
	
	$(document).ready(function(){
	<c:if test="${!empty searchForm.searchResult}">
		<c:if test="${e2ofn:getConfigValue('pcm.search.enableExpand')}">
			$("div#expand-container").addClass("eto-expand--expanded");
    	</c:if>
		<c:if test="${searchForm.condensedView}">
		document.getElementById("condensedCheckBox").checked = true;
		$("#condensedView").val(true);
		persistCondesdedView();
		</c:if>
	</c:if>
	<c:if test="${empty searchForm.searchResult}">
		$("div#expand-container").addClass("eto-expand--expanded");
	</c:if>	
	});
	$(window).on('load', function(){
		<c:if test="${!empty searchForm.searchResult}">
	   	handleGridScrolls();
	   	</c:if>
	});
	function handleGridScrolls(){
    	var gridWidth=0;
		var frozenWidth=$("#grid-result .eto-grid .eto-grid-frozen table").width();
		var scrollWidth=$("#grid-result .eto-grid .eto-grid-scroll table").width();
		if(frozenWidth == null)
				frozenWidth= 0 ;
		gridWidth+=parseInt(frozenWidth)+parseInt(scrollWidth);
		var contentWidth=$('body').width();
		if(gridWidth>contentWidth) {
			gridWidth+=60;
			$('#scroller').show();
			$('#staticdiv').css('width',gridWidth+'px');
		   	$('#scroller').scroll(function(){
				$('.eto-grid-scroll').scrollLeft($(this).scrollLeft());
			});
		} else {
			$('#scroller').hide();
		}
		window.onscroll = function(ev) {			
			var offset=$('#grid-result .eto-grid')[0].getBoundingClientRect().top;
			if(offset<0){
				 $("#grid-result .eto-grid .eto-grid-frozen table thead tr th").css({'transform':'translate3d(0px,'+(-offset)+'px,0px)','position':'relative','z-index':2});
				 $("#grid-result .eto-grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,'+(-offset)+'px,0px)','position':'relative','z-index':2,}); 
			} else {
				$("#grid-result .eto-grid .eto-grid-frozen table thead tr th").css({'transform':'translate3d(0px,0px,0px)','position':'static','z-index':'auto'});
				$("#grid-result .eto-grid .eto-grid-scroll table thead tr th").css({'transform': 'translate3d(0px,0px,0px)','position':'static','z-index':'auto'});
			}
		   
        };

        $( "#checkbox-menu-BE-fileDownload" )
        					  .mouseenter(function() {
        						  $('#downloadMenu').css('display', "block");
        						  downloadMenuOn = true;
        					  })
        					  .mouseleave(function() {
        						  window.setTimeout( closeDownloadMenu, 5000 );
        					  });
        $( "#checkbox-menu-BOM-fileDownload" )
                					  .mouseenter(function() {
                						  $('#downloadMenu').css('display', "block");
                						  downloadMenuOn = true;
                					  })
                					  .mouseleave(function() {
                						  window.setTimeout( closeDownloadMenu, 5000 );
                					  });

    }
    function closeDownloadMenu(){
    		if($('#downloadMenu').css('display') === 'block' && downloadMenuOn){
    			 $('#downloadMenu').css('display', "none");
    		}
    	}
	
	$('.eto-grid-expand__content').each(function(){
		if($(this).html().length == 0){
			$(this).siblings('button').remove();
		}
	});
</script>