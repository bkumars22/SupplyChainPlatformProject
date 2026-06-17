<table id="searchContainer" width="100%" border="0" cellspacing="0" cellpadding="0" class="shadow">
	<tbody>
		<tr valign="top">
			<td with="100%" class="containerHeaderLeft" id="containerOuter" nowrap="yes">Search Criteria</td>
		</tr>
	</tbody>
	<tbody>
		<tr valign="right">
			<td colspan="2" class="containerBody">
				<p class="instructionsArea">Enter values into any of the fields to refine the Cost Records</p>
				<table class="searchContainerArea" cellpadding="1" cellspacing="0" width="100%">
					<tr>
						<c:set var="rowCount" value="0"/>
						<c:forEach var="field" items="${bomDetailForm.allParameters}" varStatus="fieldCount" >
							<c:set var="rowCount" value="${rowCount + 1}"/>
							<c:if test="${rowCount > 3 || field.properties['newLine'] == true}">
						    	</tr><tr>
						  		<c:set var="rowCount" value="1"/>
							</c:if>
							<e2ot:searchField field="${field}" fieldCount="${fieldCount.count}"/>
						</c:forEach>
					</tr>
				</table>
			</td>
		</tr>
	</tbody>
</table>
<e2i2:buttonbar>
	<e2i2:button id="okButton" onclick="javascript:goDownloadCost()">
        <fmt:message key="button.ok" />
    </e2i2:button>
	<e2i2:button id="cancelButton" onclick="#close">
        <fmt:message key="button.cancel" />
    </e2i2:button>
</e2i2:buttonbar>
