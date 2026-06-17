/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.exception.NotAuthorizedException;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.common.entity.MultiPurposeUses;
import com.scplatform.pcm.common.service.LocatorService;
import com.scplatform.pcm.common.service.MultiPurposeUsesService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.priceTam.dto.PriceTAMForm;
import com.scplatform.pcm.restriction.service.RoleBasedColumnRestrictionService;
import com.scplatform.pcm.searchframework.cache.SessionSearchCache;
import com.scplatform.pcm.searchframework.dto.*;
import com.scplatform.pcm.searchframework.entity.SearchFilter;
import com.scplatform.pcm.searchframework.entity.SearchFilterValue;
import com.scplatform.pcm.searchframework.exception.SearchFormException;
import com.scplatform.pcm.tam.dto.TAMDownloadForm;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.util.jpa.JPAFilterUtil;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.writter.dto.CharDelimitedTextExtractWriter;
import com.scplatform.pcm.writter.dto.ExtractWriter;
import com.scplatform.pcm.writter.dto.SCPlatformApplicationContextAware;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Provides basic functions for searching for something in the DB
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class SearchService {

    private final RoleBasedColumnRestrictionService roleBasedColumnRestrictionService;
    private final PcmConfigUtil pcmConfigUtill;
    private final JPAFilterUtil jpaFilterUtil;
    private final SearchFilterService searchFilterService;
    private final MultiPurposeUsesService multiPurposeUsesService;
    private final SearchQueryBuilder searchQueryBuilder;
    private final UsersRepository usersRepository;
    private final SearchFormLoader searchFormLoader;
    private final SessionSearchCache sessionSearchCache;

    private static boolean isBusinessFilterModified = false;

	public void init(Properties properties, Object form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (form instanceof SearchForm) {
			SearchForm sf = (SearchForm) form;
			HttpSession session = request.getSession();
			ApplicationContext ac = AppContextHelper.getValidContext(request);
            sf.reset(request);
			Long userId = ac.getCurrentUser().getUserKey();
			String classFormName = sf.getClass().getSimpleName();
			String requestedFormName = StringUtils.isNotBlank(sf.getFilterType()) ? sf.getFilterType() : classFormName;

			// Clear cache to ensure fresh initialization
			sessionSearchCache.clearCache(session, requestedFormName, userId);
			if (!classFormName.equals(requestedFormName)) {
				sessionSearchCache.clearCache(session, classFormName, userId);
			}
			log.debug("Cleared search cache for forms: [{}, {}] and user: {}", requestedFormName, classFormName, userId);

			initializeForm(properties, sf, request, response);
			String resolvedFormName = StringUtils.isNotBlank(sf.getFilterType()) ? sf.getFilterType() : classFormName;
			
			// Cache the newly initialized form and definition
			sessionSearchCache.cacheSearchForm(session, resolvedFormName, userId, sf.getSearchDefinition(), sf);
			if (!classFormName.equals(resolvedFormName)) {
				sessionSearchCache.cacheSearchForm(session, classFormName, userId, sf.getSearchDefinition(), sf);
			}
			log.debug("Cached search form for forms: [{}, {}] and user: {}", resolvedFormName, classFormName, userId);
			
			if (pcmConfigUtill.getBoolean("pcm.search.saveLastSearch", false) && sf.isCheckLastAccessParam()) {
				loadDefaultFilterAndSearch(properties, form, request, response);
			}
		}
	}

	public void setSelectedKeys(Properties mapping, Object form, HttpServletRequest request) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm sf = (SearchForm) form;
			request.setAttribute("selectedSearchKeys", sf.getSelectedKeys());
		}
	}

	public void search(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
            search(properties, form, request, response, null);
		} catch (Throwable t) {
            log.error(t);
            request.setAttribute(UiMessages.ERROR_ATTRIBUTE, t.getMessage());
		}
	}

	public void search(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response, Boolean readOnlyQuery) throws Exception, Error {
		if (form instanceof SearchForm) {
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			SearchForm sf = (SearchForm) form;
			SearchDefinition sd = sf.getSearchDefinition();
			if (sd == null) {
                init(properties, form ,request, response);
				sd = sf.getSearchDefinition();
				if (sd == null) {
					throw new SearchFormException("SearchDefinitionNotInitialized");
				}
			}
			if (pcmConfigUtill.getBoolean("pcm.search.saveLastSearch", false) && sf.isCheckLastAccessParam()) {
				saveDefaultSearchFilter(form, request, response, sf);
			}
			if (sf.getClearSelection()) {
				sf.setPageStartAt(0);
				sf.clearSelection();
				sf.setClearSelection(false);
			}


			if (readOnlyQuery != null) {
				searchQueryBuilder.setReadOnlyQuery(readOnlyQuery.booleanValue());
			}
			long count = 0;
			enableAccessFilter(ac, sd);

			boolean isInitialCall = false;
			if (sf.getPagingEnabled()) {
				if (sf.getSearchParametersChanged()) {
					sf.clearSelection();
					sf.resetPagingValues();
				} else if (sf.getPageStartAt() > 0) {
					count = sf.getTotalRows();
				}
				if (count == 0) {
					if (sd.getSourceQueryType() != null && sd.getSourceQueryType().equals("SQL")) {
						GenericResultSet rs = searchQueryBuilder.executeQueryWithRowCount(sd, sf.getAllParameters(),
								sf.getPresetValues(), ac, sf.getPagingEnabled() ? sf.getPageStartAt() : -1,
								sf.getPagingEnabled() ? sf.getPageSize() : -1);
						if (rs.getValues().size() > 0) {
							sf.setSearchResult(rs);
							isInitialCall = true;
							Iterator<GenericResultRow> ritr = rs.getValues().iterator();
							if (ritr.hasNext()) {
								GenericResultRow re = ritr.next();
								Object countValue = re.getObject(re.getValues().size() - 1);
								if (countValue instanceof BigDecimal) {
									BigDecimal b = (BigDecimal) countValue;
									count = b.longValue();
								} else {
									count = (Long) countValue;
								}
							}
						} else {
							count = 0;
						}
						if (sf.getPagingEnabled() == false) {
							count = rs.getValues().size();
						}
					} else {
						count = searchQueryBuilder.executeRowCountQuery(sd, sf.getAllParameters(), sf.getPresetValues(), ac);
					}

				}
			}
			if (sd.getSourceQueryType() != null && sd.getSourceQueryType().equals("SQL")) {
				if ((count > 0 || sf.getPagingEnabled() == false)) {
					if (!isInitialCall) {
						GenericResultSet rs = searchQueryBuilder.executeQuery(sd, sf.getAllParameters(), sf.getPresetValues(),
								sf.getPagingEnabled() ? sf.getPageStartAt() : -1,
								sf.getPagingEnabled() ? sf.getPageSize() : -1);
						sf.setSearchResult(rs);
						if (sf.getPagingEnabled() == false) {
							count = rs.getValues().size();
						}
					}
				} else {
					sf.setSearchResult(null);
				}
			} else {
				if (count > 0 || sf.getPagingEnabled() == false) {
					GenericResultSet rs = searchQueryBuilder.executeQuery(sd, sf.getAllParameters(), sf.getPresetValues(),
							sf.getPagingEnabled() ? sf.getPageStartAt() : -1,
							sf.getPagingEnabled() ? sf.getPageSize() : -1);
					sf.setSearchResult(rs);
					if (sf.getPagingEnabled() == false) {
						count = rs.getValues().size();
					}
				} else {
					sf.setSearchResult(null);
				}
			}
			sf.setTotalRows(count);
			sf.setInitFlag(false);
			// for display
			Set<MultiPurposeUses> multiPurposeSet = multiPurposeUsesService.getAllMultiPurposeList(ac.getCurrentUser().getUserKey(),
					sd.getName());
			boolean displayFlagCheck = false;
			if (multiPurposeSet != null) {
				for (MultiPurposeUses mp : multiPurposeSet) {
					if (mp.getLongParam1().equals(ac.getCurrentUser().getUserKey())) {
						if (mp.getLongParam2() == 1) {
							sf.setSelectedDisplay(mp.getId());
							sf.setColumns(mp.getClobData());
							displayFlagCheck = true;
							break;
						}
					}
				}
			}
			if (!displayFlagCheck) {
				if (sf.getSelectedDisplay() == 0) {
					sf.setColumns("");
					sf.setSelectedDisplay(0);
				} else {
					for (MultiPurposeUses multipurpose : multiPurposeSet) {
						if (multipurpose.getId() == sf.getSelectedDisplay()) {
							sf.setColumns(multipurpose.getClobData());
							sf.setSelectedDisplay(sf.getSelectedDisplay());
							break;
						}
					}
				}
			}
			sf.setAvailableDisplay(
					multiPurposeUsesService.getAllDisplay(ac.getCurrentUser().getUserKey(), sf.getSearchDefinition().getName()));

			/* 1. Get the restricted columns for this user by parsing the config file
			   2. Set these columns to a form variable for accessing in UI layer */
			String currentUserRoleId = ac.getCurrentRole().getRoleId();
			List<String> restrictedColumnList = getRestrictedColumnsByRole(sd.getName(), currentUserRoleId);
			sf.getRestrictedColumnList().clear();
			sf.setRestrictedColumnList(restrictedColumnList);

		}
	}

	private List<String> getRestrictedColumnsByRole(String screenName, String currentUserRoleId) {
		List<String> restrictedColumnList = roleBasedColumnRestrictionService.getRestrictedColumnList(screenName, currentUserRoleId);
		return restrictedColumnList;
	}

	public void finderSearch(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response, Boolean readOnlyQuery) throws Exception, Error {
		if (form instanceof SearchForm) {
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			SearchForm sf = (SearchForm) form;
			sf.setClearSelection(false);
			SearchDefinition sd = sf.getSearchDefinition();
			if (sd == null) {
				init(properties, form, request, response);
			}
			if (sf.getClearSelection()) {
				sf.setPageStartAt(0);
				sf.clearSelection();
				sf.setClearSelection(false);
			}

			if(sd.getName().equalsIgnoreCase("SearchDefEnterpriseItemFinderForNewRebateRule")) {
				SearchParameter sp = sf.getSearchParameter("itemType");
				if(sp.getValue()==null)
				sp.setValue("I");
			}

			if (readOnlyQuery != null) {
				searchQueryBuilder.setReadOnlyQuery(readOnlyQuery.booleanValue());
			}
			long count = 0;
			enableAccessFilter(ac, sd);

			boolean isInitialCall = false;
			if (sf.getPagingEnabled()) {
				if (sf.getSearchParametersChanged()) {
					sf.resetPagingValues();
					sf.clearSelection();
				} else if (sf.getPageStartAt() > 0) {
					count = sf.getTotalRows();
				}
				if (count == 0) {
					if (sd.getSourceQueryType() != null && sd.getSourceQueryType().equals("SQL")) {
						GenericResultSet rs = searchQueryBuilder.executeQueryWithRowCount(sd, sf.getAllParameters(),
								sf.getPresetValues(), ac, sf.getPagingEnabled() ? sf.getPageStartAt() : -1,
								sf.getPagingEnabled() ? sf.getPageSize() : -1);
						if (rs.getValues().size() > 0) {
							sf.setSearchResult(rs);
							isInitialCall = true;
							Iterator<GenericResultRow> ritr = rs.getValues().iterator();
							if (ritr.hasNext()) {
								GenericResultRow re = ritr.next();
								Object countValue = re.getObject(re.getValues().size() - 1);
								if (countValue instanceof BigDecimal) {
									BigDecimal b = (BigDecimal) countValue;
									count = b.longValue();
								} else {
									count = (Long) countValue;
								}
							}
							if(sd.getName().equalsIgnoreCase("SearchDefEnterpriseItemFinderForNewRebateRule")) {
								Set<String> currentPageBox = new HashSet<>();
								Iterator<GenericResultRow> ritr1 = rs.getValues().iterator();
								ArrayList<Object> values;
								while (ritr1.hasNext()) {
									GenericResultRow re = ritr1.next();
									values = new ArrayList<>(re.getValues());
									StringBuilder s1 = new StringBuilder();
									s1.append(StringUtils.join(values, "','"));
									currentPageBox.add(String.format("'%s'", s1.toString()));
								}
								sf.setCurrentPageIds(currentPageBox);
							}
						} else {
							count = 0;
						}
						if (sf.getPagingEnabled() == false) {
							count = rs.getValues().size();
						}
					} else {
						count = searchQueryBuilder.executeRowCountQuery(sd, sf.getAllParameters(), sf.getPresetValues(), ac);
					}

				}
			}
			if (sd.getSourceQueryType() != null && sd.getSourceQueryType().equals("SQL")) {
				if ((count > 0 || sf.getPagingEnabled() == false)) {
					if (!isInitialCall) {
						GenericResultSet rs = searchQueryBuilder.executeQuery(sd, sf.getAllParameters(), sf.getPresetValues(),
								sf.getPagingEnabled() ? sf.getPageStartAt() : -1,
								sf.getPagingEnabled() ? sf.getPageSize() : -1);
						sf.setSearchResult(rs);
						if (sf.getPagingEnabled() == false) {
							count = rs.getValues().size();
						}
						if(sd.getName().equalsIgnoreCase("SearchDefEnterpriseItemFinderForNewRebateRule")) {
							Set<String> currentPageBox = new HashSet<>();
							Iterator<GenericResultRow> ritr1 = rs.getValues().iterator();
							ArrayList<Object> values= new ArrayList<Object>();
							while (ritr1.hasNext()) {
								GenericResultRow re = ritr1.next();
								values = new ArrayList<Object>(re.getValues());
								StringBuilder s1 = new StringBuilder();
								s1.append(StringUtils.join(values, "','"));
								currentPageBox.add(String.format("'%s'", s1.toString()));
							}
							sf.setCurrentPageIds(currentPageBox);
						}
					}
				} else {
					sf.setSearchResult(null);
				}
			} else {
				if (count > 0 || sf.getPagingEnabled() == false) {
					GenericResultSet rs = searchQueryBuilder.executeQuery(sd, sf.getAllParameters(), sf.getPresetValues(),
							sf.getPagingEnabled() ? sf.getPageStartAt() : -1,
							sf.getPagingEnabled() ? sf.getPageSize() : -1);
					sf.setSearchResult(rs);
					if (sf.getPagingEnabled() == false) {
						count = rs.getValues().size();
					}
				} else {
					sf.setSearchResult(null);
				}
			}
			sf.setTotalRows(count);
			sf.setInitFlag(false);
		}
	}

	public boolean extractToFile(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			ApplicationContext ac = AppContextHelper.checkAccess(request, "UPDOWN", "DownloadFile");
			SearchForm sf = (SearchForm) form;
			String reportType = request.getParameter("filterType");
			if(reportType!=null && !reportType.isEmpty()){
				 ac.setReportType(reportType);
			}
			SearchDefinition sd = sf.getSearchDefinition();
			if (sd == null) {
				throw new Exception("Search Form not initialized");
			}

			enableAccessFilter(ac, sd);
			if (sd.getExtractQueryType() != null && sd.getExtractQueryType().equalsIgnoreCase("SQL")) {
				extractToFileSQL(properties, form, request, response);
                return true;
			}
			if (sd.getName().equals("SearchDefCostRecord")) {
				ApplicationContext appCtx = null;
				Map<String, Object> param = new HashMap<>();
				List<String> restrictedCostTypesList = Arrays.asList(new String[] { "WAP", "XWAP" });
				List<String> values = new ArrayList<String>(restrictedCostTypesList);
				try {
					appCtx = AppContextHelper.checkAccess(request, "COST_RECORD", "Read");
				} catch (NotAuthorizedException e) {
					return false;
				}
				for (String costType : restrictedCostTypesList) {
					String property = "pcm.costRecord." + costType + ".Read.allowedRoles";
					List<String> exemptedRolesList = pcmConfigUtill.getList(property,
							Arrays.asList(new String[] { "ADMIN" }));
					if (appCtx != null && exemptedRolesList.contains(appCtx.getCurrentUser().getUserId().toUpperCase())) {
						values.remove(costType);
					}
				}
				;

				if (!values.isEmpty()) {
					param.put("costTypes", values);
					jpaFilterUtil.enableFilter("costTypeExcludeFilter", param);
				}
			}

			int maxRows = pcmConfigUtill.getInteger("pcm.download.maxRows", 1000);
			int timeoutInSeconds = pcmConfigUtill.getInteger("pcm.download.timeoutInSeconds", -1);
			if (timeoutInSeconds > 0) {
				searchQueryBuilder.setQueryTimeout(timeoutInSeconds);
			}

			long rows = searchQueryBuilder.executeExtractRowCountQuery(sd, sf.getAllParameters(), sf.getPresetValues(), ac);
			if (rows > maxRows && !sf.getSearchDefinition().getName().equals("SearchDefTAMExceptionSupplyAllocation")) {
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.download_rows_exceeded", new Object[]{maxRows}, null));
                return false;
			}

			ExtractWriter rowWriter = null;
			String extractWriterClass = sf.getExtractWriterClass() != null ? sf.getExtractWriterClass()
					: sd.getExtractWriterClass();
			if (extractWriterClass != null) {
				Class<?> c = Class.forName(extractWriterClass);
				rowWriter = (ExtractWriter) c.newInstance();
			} else {
				rowWriter = new CharDelimitedTextExtractWriter();
			}

			if (rowWriter instanceof SCPlatformApplicationContextAware) {
				SCPlatformApplicationContextAware ctxAwareRowWriter = (SCPlatformApplicationContextAware) rowWriter;
				ctxAwareRowWriter.setApplicationContext(ac);
			}

			Map<String, Object> extractProps = new HashMap<String, Object>();

			extractProps.put("applicationContext", ac);
			extractProps.put("extractWriter", rowWriter);

			if (sf.getSearchDefinition().getName().equals("SearchDefPriceTAM")) {
				PriceTAMForm priceTAMForm = (PriceTAMForm) form;
				extractProps.put("priceTAMForm", priceTAMForm);
			}

			String extractFileName = StringUtils.trimToNull(sf.getExtractFileName());
			if (extractFileName == null) {
				extractFileName = sf.getSearchDefinition().getName();
				if (extractFileName == null) {
					extractFileName = "export";
				}
                if(reportType!= null){
                    extractFileName = extractFileName + "_" + reportType;
                }
			}
			extractProps.put("Name", extractFileName);

			String type = FilenameUtils.getExtension(extractFileName);
			if (StringUtils.isEmpty(type)) {
				extractFileName += ".xlsx";
			}
			extractFileName = URLEncoder.encode(extractFileName, "UTF-8");
			extractProps.put("FileName", extractFileName);
			extractProps.put("WriterProp", sd.getExtractWriterProp());

			if (sd.getExtractWriterTransform() != null) {
				/*ObjectTransformer transformer = loadTransformation(sd.getExtractWriterTransform());
				if (transformer != null) {
					extractProps.put("Transformer", transformer);
				}*/
			}
            final Set<String> valid_bom_searchDef = Set.of("SearchDefBom", "SearchDefBomMgmt");

            if(valid_bom_searchDef.contains(sd.getName()) && reportType != null && reportType.equals("ALL") ){
                rowWriter.inititalize(extractProps);
                searchQueryBuilder.setCallback(rowWriter);
                long  bomRows = searchQueryBuilder.executeExtractRowCountQuery(sd, sf.getAllParameters(), sf.getPresetValues(), ac);
                int maxBomRows= pcmConfigUtill.getInteger("pcm.bomMax.downloads.Rows",1000);
                if(bomRows > maxBomRows){
                    request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.download_max_bom_rows_exceeded", new Object[]{maxBomRows}, null));
                    return false;

                }
            }

			response.reset();
			response.setDateHeader("Expires", 0);
			// NOTE: DO NOT USE Pragma no-cache as it causes a problem with SSL
			// downloads!
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0, no-store");
			response.setHeader("Pragma", "public");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/force-download");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + extractFileName + "\"");

			rowWriter.inititalize(extractProps);
			rowWriter.setStream(response.getOutputStream(), "UTF-8");

            searchQueryBuilder.setCallback(rowWriter);

            searchQueryBuilder.executeUserQuery(sd.getExtractSource(), sd, sf.getAllParameters(), sf.getPresetValues(), -1, -1);
			rowWriter.close();

			// Not all writers can determine the number of chars written
			// If they cannot, they should return -1
			int contentLength = rowWriter.getCharsWritten();
			if (contentLength > -1) {
				response.setContentLength(contentLength);
			}
			response.flushBuffer();

			try {
				String fileName = request.getParameter("fileName");
				File file = new File(fileName);
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sd.getName().equals("SearchDefCostRecord")) {
				jpaFilterUtil.disableFilter("costTypeExcludeFilter");
			}
			return true;
		}
        return true;
	}

	public boolean extractToFileSQL(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			ApplicationContext ac = AppContextHelper.checkAccess(request, "UPDOWN", "DownloadFile");
			SearchForm sf = (SearchForm) form;

			SearchDefinition sd = sf.getSearchDefinition();
			if (sd == null) {
				throw new Exception("Search Form not initialized");
			}

			enableAccessFilter(ac,sd);

			int maxRows = pcmConfigUtill.getInteger("pcm.download.maxRows", 1000);
			int timeoutInSeconds = pcmConfigUtill.getInteger("pcm.download.timeoutInSeconds", -1);
			if (timeoutInSeconds > 0) {
                searchQueryBuilder.setQueryTimeout(timeoutInSeconds);
			}

			ExtractWriter rowWriter = null;
			String extractWriterClass = sf.getExtractWriterClass() != null ? sf.getExtractWriterClass()
					: sd.getExtractWriterClass();
			if (extractWriterClass != null) {
				Class<?> c = Class.forName(extractWriterClass);
				rowWriter = (ExtractWriter) c.newInstance();
			} else {
				rowWriter = new CharDelimitedTextExtractWriter();
			}

			if (rowWriter instanceof SCPlatformApplicationContextAware) {
				SCPlatformApplicationContextAware ctxAwareRowWriter = (SCPlatformApplicationContextAware) rowWriter;
				ctxAwareRowWriter.setApplicationContext(ac);
			}

			Map<String, Object> extractProps = new HashMap<String, Object>();

			if (sf.getSearchDefinition().getName().equals("SearchDefPriceTAM")) {
				PriceTAMForm priceTAMForm = (PriceTAMForm) form;
				extractProps.put("priceTAMForm", priceTAMForm);
			}

			String extractFileName = StringUtils.trimToNull(sf.getExtractFileName());
			if (extractFileName == null) {
				extractFileName = sf.getSearchDefinition().getName();
				if (extractFileName == null) {
					extractFileName = "export";
				}
			}
			extractProps.put("Name", extractFileName);

			String type = FilenameUtils.getExtension(extractFileName);
			if (StringUtils.isEmpty(type)) {
				extractFileName += ".xlsx";
			}
			extractFileName = URLEncoder.encode(extractFileName, "UTF-8");
			extractProps.put("FileName", extractFileName);
			extractProps.put("WriterProp", sd.getExtractWriterProp());
            searchQueryBuilder.setCallback(rowWriter);

			if (sd.getExtractWriterTransform() != null) {
				/*ObjectTransformer transformer = loadTransformation(sd.getExtractWriterTransform());
				if (transformer != null) {
					extractProps.put("Transformer", transformer);
				}*/
			}

			rowWriter.inititalize(extractProps);

			try {
                searchQueryBuilder.executeExtractRowCountDataQuerySQL(sd, sf.getAllParameters(), sf.getPresetValues(), ac,
						response, maxRows, rowWriter, extractProps, extractFileName);
			} catch (Exception e) {
				if (e.getMessage().equals("MAX_ROW_EXCEED_ERROR")) {
                    request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.download_rows_exceeded", new Object[]{maxRows}, null));
                    return false;
				} else {
					throw e;
				}
			}

			rowWriter.close();

			// Not all writers can determine the number of chars written
			// If they cannot, they should return -1
			int contentLength = rowWriter.getCharsWritten();
			if (contentLength > -1) {
				response.setContentLength(contentLength);
			}
			response.flushBuffer();

			try {
				String fileName = request.getParameter("fileName");
				File file = new File(fileName);
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sd.getName().equals("SearchDefCostRecord")) {
				jpaFilterUtil.disableFilter("costTypeExcludeFilter");
			}
		}
        return true;
	}

	public void deleteSearchFilter(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;
			if (StringUtils.isEmpty(searchForm.getSelectedFilter())) {
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, "errors.filter_name_required");
			}

			SearchFilter sf = searchFilterService.getSearchFilterById(Long.valueOf(searchForm.getSelectedFilter())).get();
			if (sf != null) {
				ApplicationContext cxt = AppContextHelper.getValidContext(request);
				if (cxt.getCurrentUser().getUserKey().equals(sf.getCreator().getUserKey()) == false) {
                    request.setAttribute(UiMessages.ERROR_ATTRIBUTE, "errors.delete_other_filter");
				}
				searchFilterService.delete(sf);
				searchForm.getAvailableFilters().remove(sf.getFilterKey().toString());
				searchForm.setSelectedFilter(null);
				searchForm.setSelectedFilterName(null);
			}
		}
	}

	public void loadSearchFilter(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;
			if (StringUtils.isEmpty(searchForm.getSelectedFilter())) {
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, "errors.filter_name_required");
                return;
			}
			Map<String, String> filters = searchForm.getAvailableFilters();
			filters.clear();
			filters.put("", SCPlatformMessages.INSTANCE.getMessage("label.newFilter", null,null));
			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			List results = searchFilterService.findUserSearchFilters(null, cxt.getCurrentUser(), searchForm.getSearchDefinition().getName());
			for (int idx = 0; idx < results.size(); idx++) {
				SearchFilter sf = (SearchFilter) results.get(idx);
				filters.put(String.valueOf(sf.getFilterKey()), sf.getName());
			}
			SearchFilter sf = searchFilterService.getSearchFilterById(Long.valueOf(searchForm.getSelectedFilter())).get();
			if (sf != null) {
				searchForm.clearParameterValues();
				Map values = sf.getFilterValueMap();
				Iterator itr = values.keySet().iterator();
				while (itr.hasNext()) {
					String fieldName = (String) itr.next();
					List fieldValues = (List) values.get(fieldName);
					SearchParameter sc = searchForm.getSearchParameter(fieldName);
					if (sc == null) {
						continue;
					}
					if (sc.isValueArray()) {
						searchForm.setValues(fieldName, fieldValues.toArray());
					} else {
						searchForm.setValue(fieldName, fieldValues.get(0));
					}
				}
			}
		}
	}

	public void refreshFilters(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			SearchForm sf = (SearchForm) form;
			String defaultFilter = properties.getProperty("defaultFilter");
			Map<String, String> filters = sf.getAvailableFilters();
			filters.clear();
			filters.put("", SCPlatformMessages.INSTANCE.getMessage("label.newFilter", null,null));
			/* sf.getFilterType() */
			List results = searchFilterService.findUserSearchFilters(null, cxt.getCurrentUser(), sf.getSearchDefinition().getName());
			for (int idx = 0; idx < results.size(); idx++) {
				SearchFilter sff = (SearchFilter) results.get(idx);
				filters.put(String.valueOf(sff.getFilterKey()), sff.getName());
				if (sff.getName().equals(defaultFilter)) {
					sf.setSelectedFilter(String.valueOf(sff.getFilterKey()));
					loadSearchFilter(properties, sf, request, response);
				}
			}
			for (Map.Entry<String, Object> preset : sf.getInitValues().entrySet()) {
				if (sf.getSearchParameterNames().contains(preset.getKey())) {
					sf.setValue(preset.getKey(), preset.getValue());
				}
			}

		}
	}

	public void renameFilter(Properties mapping, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String oldFilterKey = request.getParameter("oldFilterKey");
		String newFilterName = request.getParameter("newFilterName");
		SearchFilter searchFilter = searchFilterService.getSearchFilterById(Long.valueOf(oldFilterKey)).get();
		if (searchFilter != null) {
			searchFilter.setName(newFilterName);
			searchFilterService.delete(searchFilter);
			SearchForm sf = (SearchForm) form;
			if (sf != null && sf.getAvailableFilters().containsKey(oldFilterKey)) {
				sf.getAvailableFilters().put(oldFilterKey, newFilterName);
			}
		}
	}

	public void deleteFilters(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SearchForm searchForm = (SearchForm) form;
		String selectedDeleteKeys[] = searchForm.getSelectedFilterKeys();
		if (selectedDeleteKeys != null) {
			for (int i = 0; i < selectedDeleteKeys.length; i++) {
				String filterKey = selectedDeleteKeys[i];
				SearchFilter sf = searchFilterService.getSearchFilterById(Long.valueOf(filterKey)).get();
				if (sf != null) {
					ApplicationContext cxt = AppContextHelper.getValidContext(request);
					if (!cxt.getCurrentUser().getUserKey().equals(sf.getCreator().getUserKey())) {
                        request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.delete_other_filter", null, null));
					}
					searchFilterService.delete(sf);
					searchForm.getAvailableFilters().remove(sf.getFilterKey().toString());
					searchForm.clearParameterValues();
					searchForm.setSelectedFilter(null);
					searchForm.setSelectedFilterName(null);
				}
			}
		}
        search(properties, form, request, response);
	}

	public void loadFilterAndSearch(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;

			loadSearchFilter(properties, searchForm, request, response);
			// Reset the page
			searchForm.setPageStartAt(0);
			search(properties, searchForm, request, response);
		}
	}

	public void saveSearchFilter(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;

			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			SearchFilter searchFilter = null;
			if (StringUtils.isEmpty(searchForm.getSelectedFilterName())) {
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.filter_name_required", null, null));
                return;
			}

			if (StringUtils.isEmpty(searchForm.getSelectedFilter()) == false) {
				searchFilter = searchFilterService.getSearchFilterById(Long.valueOf(searchForm.getSelectedFilter())).get();
				if (searchFilter != null && searchFilter.getIsPublic() == true
						&& cxt.getCurrentUser().getUserKey().equals(searchFilter.getCreator().getUserKey()) == false) {
                    request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("warn.change_public_filter", null, null));
                    return;
				}
				searchForm.getAvailableFilters().remove(searchFilter.getFilterKey());
			}
			if (searchFilter == null) {
				String filterTypeName = searchForm.getSearchDefinition().getName();
				String filterType = (StringUtils.trimToNull(filterTypeName)) != null ? filterTypeName
						: searchForm.getClass().getName();
				if (searchForm.getSelectedFilterName().equals("default" + filterType)) {
                    request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("warn.default_filter", null, null));
                    return;
				}

				if (searchForm.getAvailableFilters().containsValue(searchForm.getSelectedFilterName())) {
					for (Map.Entry<String, String> preset : searchForm.getAvailableFilters().entrySet()) {
						if (preset.getValue().equals(searchForm.getSelectedFilterName())) {
							searchFilter = searchFilterService.getSearchFilterById(Long.valueOf(preset.getKey())).get();
							break;
						}
					}
				} else {
					searchFilter = new SearchFilter();
					/* searchFilter.setFilterType(searchForm.getFilterType()); */
					// after trimming SearchDefTAMExceptionSupplyAllocation
					searchFilter.setFilterType(filterType);
					searchFilter.setIsPublic(false);
					searchFilter.setCreator(usersRepository.findUserByKey(cxt.getCurrentUser().getUserKey()));
				}
			}
			searchFilter.setName(searchForm.getSelectedFilterName());
			searchFilter.clearFilterValues();
			Collection<SearchParameter> sps = searchForm.getAllParameters();
			Iterator<SearchParameter> itr = sps.iterator();
			while (itr.hasNext()) {
				SearchParameter sc = itr.next();
				if (sc != null) {
					if (sc.getValue() != null && sc.getValue().toString().length() > 0) {
						if (sc.getValue().toString().length() > 4000) {
							String label = SCPlatformMessages.INSTANCE.getMessage(sc.getLabelKey(), null, null);
							if (StringUtils.startsWith(label, "???")) {
								label = sc.getLabelKey();
							}
                            request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.filter_value_to_large", new Object[]{label, 4000}, null));
                            return;
						}
						searchFilter.addFilterValue(sc.getName(), sc.getValue());
					}
				}
			}
			if (searchFilter.getName().toString().length() > 55) {
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("errors.filter_name_exceeds_range", new Object[]{55}, null));
                return;
			}

			searchFilterService.save(searchFilter); // saving data in database
			searchForm.getAvailableFilters().put(String.valueOf(searchFilter.getFilterKey()), searchFilter.getName());
			searchForm.setSelectedFilter(String.valueOf(searchFilter.getFilterKey()));
			searchForm.setSelectedFilterName(searchFilter.getName());
		}
	}

	protected SearchDefinition initializeForm(Properties props,SearchForm sf, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
		ApplicationContext cxt = AppContextHelper.getValidContext(request);

		// Reset the search page
		sf.setClearSelection(true);
		sf.setInitFlag(true);
		sf.clearSearchParameters();
		sf.clearPresetValues();
		sf.setSearchResult(null);
		sf.clearSelection();
		sf.resetPagingValues();
		sf.setPagingEnabled(
				BooleanUtils.toBoolean(StringUtils.defaultIfEmpty(props.getProperty("enablePaging"), "true")));
		sf.setNextAction(props.getProperty("nextAction"));
		sf.setPreviousAction(props.getProperty("previousAction"));
		sf.setSearchAction(props.getProperty("searchAction"));
		sf.setShowFilterArea(!BooleanUtils.toBoolean(props.getProperty("hideFilterArea")));
		sf.setFilterAreaCollapsed(BooleanUtils.toBoolean(props.getProperty("filterAreaCollapsed")));
		List<String> configDirs = pcmConfigUtill.getList("pcm.search_configuration.dirs", Collections.EMPTY_LIST);
		if ((request.getParameter("checkLastAccessParam") != null)
				&& (!request.getParameter("checkLastAccessParam").isEmpty())
				&& (request.getParameter("checkLastAccessParam").equalsIgnoreCase("false"))) {
			sf.setCheckLastAccessParam(Boolean.FALSE);
		} else {
			sf.setCheckLastAccessParam(Boolean.TRUE);
		}
		String objectKeyName = pcmConfigUtill.getString("pcm.search_configuration.objectKeyName", "objectKey");
		int objectKeyRadix = pcmConfigUtill.getIntValue("pcm.search_configuration.objectKeyRadix", 10);

		SearchDefinition sd = null;
		// Load a definition
		if (sf.getFinderName() != null && (!sf.getFinderName().isBlank())) {
			String searchDefName = "SearchDef" + sf.getFinderName() + ".xml";
            props.setProperty("definition", searchDefName);
		}

		String defName = props.getProperty("definition");
		InputStream def = null;
		if (defName != null) {
			LocatorService ls = new LocatorService(pcmConfigUtill);
			URL url = ls.locateResource(defName, configDirs, true);
			if (url == null) {
				throw new Exception("Unable to load search page " + defName);
			}
			def = url.openStream();
			try {
                String xmlContent = IOUtils.toString(def, StandardCharsets.UTF_8);

                PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
                Properties properties = new Properties();
                pcmConfigUtill.getAllConfigurations().forEach((key, value) -> properties.put(key, value));
                String resolvedXml = helper.replacePlaceholders(xmlContent, properties);

                searchFormLoader.parse(new ByteArrayInputStream(resolvedXml.getBytes(StandardCharsets.UTF_8)));
			} catch (Exception le) {
				throw new SearchFormException("SearchDefLoadFailed", le);
			} finally {
				if (def != null) {
					def.close();
				}
			}
			Map<String, Object> context = new HashMap<String, Object>();
			context.put(SearchParameterInitializer.REQUEST_TYPE, sf.getRequestType());
			context.put(SearchParameterInitializer.APP_CONTEXT, cxt);
			context.put(SearchParameterInitializer.ROLE, cxt.getCurrentRole());
			context.put(SearchParameterInitializer.ACTIVE_BE, cxt.getEnterpriseKey());
			context.put(SearchParameterInitializer.ENTERPRISE_BE, cxt.getEnterpriseKey());
			if (defName.equals("SearchDefCostExceptionApproverFinder.xml")) {
				String[] roles = request.getParameter("roles").split(",");
				if (roles != null) {
					context.put("roles", roles);
				}
			}
			Iterator<SearchParameter> pItr = searchFormLoader.getSearchParameters().iterator();
			while (pItr.hasNext()) {
				pItr.next().initialize(context);
			}
			sd = searchFormLoader.getSearchDefinition();
			// Default the name
			String name = StringUtils.trimToNull(sd.getName());
			if (name == null) {
				name = sf.getClass().getName();
				sd.setName(name);
				log.warn("Search definition: " + url.toString() + " is missing a name, defaulted to form class name:"
						+ name);
			}
			sf.setFilterType(name);
			sf.setSearchDefinition(sd);
			sf.setSearchParameters(searchFormLoader.getSearchParameters());

			for (Map.Entry<String, SearchExpression> filterParameter : sd.getExpressions().entrySet()) {
				String key = filterParameter.getKey();
				String value = StringUtils.trimToNull(request.getParameter(key));
				if (value != null) {
					if (key.startsWith(objectKeyName)) {
						List<Long> objectKeys = new ArrayList<Long>();
						try {
							for (String str : value.split(",")) {
								str = str != null ? str.trim() : str;
								Long keyValue = Long.valueOf(str, objectKeyRadix);
								log.debug(" objectKey found: " + keyValue);
								objectKeys.add(keyValue);
							}
							sf.setPresetValue(key, objectKeys);
						} catch (NumberFormatException nfe) {
							log.warn("FilterExpression invalid objectKey value (" + value + ") for key=" + key, nfe);
						}
					} else if (filterParameter.getValue().getOperator() == SearchExpression.OperatorType.IN) {
						sf.setPresetValue(filterParameter.getKey(), value.split(","));

					} else {
						if(filterParameter.getValue().getDataType() != null){
							if(filterParameter.getValue().getDataType().equals("Long")){
								sf.setPresetValue(filterParameter.getKey(), Long.parseLong(value));
							}
						} else {
							sf.setPresetValue(filterParameter.getKey(), value);
						}
					}
				}
			}

			// Setup any presets from content
			sf.setPresetValue("requestType", sf.getRequestType());
			sf.setPresetValue("currentUser", cxt.getCurrentUser());
			sf.setPresetValue("currentUserId", cxt.getCurrentUser().getUserId());
			sf.setPresetValue("currentRole", cxt.getCurrentUser().getRole());
			sf.setPresetValue("currentRoleId", cxt.getCurrentUser().getRole().getRoleId());
			sf.setPresetValue("currentBusiness", cxt.getCurrentUser().getBusinessEntity());
			sf.setPresetValue("enterpriseBusinessKey", cxt.getEnterpriseKey());

			// Initialize filters
			String defaultFilter = props.getProperty("defaultFilter");
			Map<String, String> filters = sf.getAvailableFilters();
			filters.clear();
			/*
			 * filters.put("", "" + getResources(request).getMessage("label.newFilter") +
			 * "");
			 */
			filters.put("", SCPlatformMessages.INSTANCE.getMessage("label.newFilter",null,null));
			if (sf.isCheckLastAccessParam()) {
				if (sf.getFilterType() != null) {
					List<SearchFilter> results = searchFilterService.findUserSearchFilters(null, cxt.getCurrentUser(),
							sf.getFilterType());
					for (SearchFilter filter : results) {
						filters.put(String.valueOf(filter.getFilterKey()), filter.getName());
						if (filter.getName().equals(defaultFilter)) {
							sf.setSelectedFilter(String.valueOf(filter.getFilterKey()));
							loadSearchFilter(props, sf, request, response);
						}
					}
				}
			}
			for (Map.Entry<String, Object> preset : sf.getInitValues().entrySet()) {
				if (sf.getSearchParameterNames().contains(preset.getKey())) {
					sf.setValue(preset.getKey(), preset.getValue());
				}
			}

		}
		// Give the form a chance to anything it needs to do
		sf.initialize(request, response);

		return sd;
	}

	protected String getBusinessObjectKey(HttpServletRequest request) throws Exception {
		String objectKeyName = pcmConfigUtill.getString("pcm.search_configuration.objectKeyName",
				"objectKey");
		String key = StringUtils.trimToNull(request.getParameter(objectKeyName));
		if (key != null) {
			log.info("punch-in objectKey [" + key + "]");
		}
		return key;
	}

	/**
	 * Helper implementation of the callback.
	 */
	protected abstract class RowCallback implements SearchQueryResultCallback {
		@Override
		public void end(String[] columnNames) {
		}

		@Override
		public abstract boolean onRow(GenericResultRow row);

		@Override
		public void start(String[] columnNames) {
		}

	}

	public void refreshDisplay(Properties mapping, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;
			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			searchForm.setAvailableDisplay(multiPurposeUsesService.getAllDisplay(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName()));
			searchForm.setColumns(searchForm.getColumns());
			searchForm.setSelectedDisplay(searchForm.getSelectedDisplay());
		}
	}

	public void saveDisplay(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;
			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			MultiPurposeUses multiPurposeUses = new MultiPurposeUses();
			multiPurposeUses.setObjectType("GRID_VIEW");
			multiPurposeUses.setStringParam1(searchForm.getSelectedDisplayName());
			multiPurposeUses
					.setStringParam2(searchForm.getFilterType() == null ? searchForm.getSearchDefinition().getName()
							: searchForm.getFilterType());
			multiPurposeUses.setStringParam3(searchForm.getDisplayDescription());
			multiPurposeUses.setClobData(searchForm.getColumns());
			multiPurposeUses.setLongParam1(cxt.getCurrentUser().getUserKey());
			multiPurposeUses.setLongParam2(searchForm.isDefaultDisplay() == true ? 1l : 0);

			MultiPurposeUses muses = multiPurposeUsesService.checkDisplayAlreadyExist(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName(), searchForm.getSelectedDisplayName(), null);
			if (muses != null && muses.getStringParam1().equals(searchForm.getSelectedDisplayName())) {
                request.setAttribute(UiMessages.ERROR_ATTRIBUTE, SCPlatformMessages.INSTANCE.getMessage("error.display_name_exist", new Object[]{searchForm.getSelectedDisplayName()}, null));
                return;
			} else {
				if (searchForm.isDefaultDisplay()) {
					multiPurposeUsesService.updateDefaultDisplay(cxt.getCurrentUser().getUserKey(),
							searchForm.getSearchDefinition().getName());
				}
			}
			searchForm.setAvailableDisplay(multiPurposeUsesService.getAllDisplay(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName()));
			searchForm.setAvailableColumns(multiPurposeUsesService.getAvailableColumn(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName(), searchForm.getSelectedDisplayName()));
			searchForm.setColumns(multiPurposeUses.getClobData());
			searchForm.setSelectedDisplayName(multiPurposeUses.getStringParam1());
			searchForm.setSelectedDisplay(multiPurposeUses.getId());
		}
	}

	public void changeDisplay(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;
			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			MultiPurposeUses multiPurposeUses = multiPurposeUsesService.findById(cxt.getCurrentUser().getUserKey());
			searchForm.setAvailableDisplay(multiPurposeUsesService.getAllDisplay(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName()));
			if (multiPurposeUses != null) {
				searchForm.setColumns(multiPurposeUses.getClobData());
				searchForm.setSelectedDisplayName(multiPurposeUses.getStringParam1());
				searchForm.setSelectedDisplay(multiPurposeUses.getId());
			} else {
				searchForm.setColumns("");
				searchForm.setSelectedDisplayName("");
				searchForm.setSelectedDisplay(0);
			}
		}
	}

	public void deleteDisplay(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {
			SearchForm searchForm = (SearchForm) form;
			ApplicationContext cxt = AppContextHelper.getValidContext(request);
			multiPurposeUsesService.deleteDisplay(cxt.getCurrentUser().getUserKey(),
					searchForm.getSelectedDisplay());
			searchForm.getAvailableDisplay().clear();
			Set<MultiPurposeUses> multiPurposeSet = multiPurposeUsesService.getAllMultiPurposeList(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName());
			boolean displayFlagCheck = false;
			if (multiPurposeSet != null) {
				for (MultiPurposeUses mp : multiPurposeSet) {
					if (mp.getLongParam1().equals(cxt.getCurrentUser().getUserKey())) {
						if (mp.getLongParam2() == 1) {
							searchForm.setSelectedDisplay(mp.getId());
							searchForm.setColumns(mp.getClobData());
							displayFlagCheck = true;
							break;
						}
					}
				}
			}
			if (!displayFlagCheck) {
				searchForm.setColumns("");
				searchForm.setSelectedDisplayName("");
				searchForm.setSelectedDisplay(0);
			}
			searchForm.setAvailableDisplay(multiPurposeUsesService.getAllDisplay(cxt.getCurrentUser().getUserKey(),
					searchForm.getSearchDefinition().getName()));
		}
	}

	public void saveDefaultSearchFilter(Object form, HttpServletRequest request, HttpServletResponse response,
			SearchForm sf) throws Exception {
		HttpSession session = request.getSession(false);
		ApplicationContext cxt = AppContextHelper.getValidContext(request);
		SearchFilter searchFilter = null;
		searchFilter = new SearchFilter();
		String filterTypeName = sf.getSearchDefinition().getName();
		String filterType = (StringUtils.trimToNull(filterTypeName)) != null ? filterTypeName : sf.getClass().getName();
		searchFilter.setFilterType(filterType);
		searchFilter.setIsPublic(false);
		searchFilter.setCreator(cxt.getCurrentUser());
		searchFilter.setName("default" + filterType);
		searchFilter.clearFilterValues();
		Collection<SearchParameter> sps = sf.getAllParameters();
		Iterator<SearchParameter> itr = sps.iterator();
		while (itr.hasNext()) {
			SearchParameter sc = itr.next();
			if (sc != null) {
				if (sc.getValue() != null && sc.getValue().toString().length() > 0) {
					if (sc.getValue().toString().length() > 4000) {
						return;
					}
					if("itemState".equals(sc.getName()))
						searchFilter.addFilterValue(sc.getName(), ((List<Object>)sc.getValue()).get(2));
					else
						searchFilter.addFilterValue(sc.getName(), sc.getValue());
				}
			}
		}

		SearchFilter filter = null;
		if (session != null) {
			filter = (SearchFilter) session.getAttribute("filter" + filterType);
		}

		if (filter == null || !filter.equals(searchFilter)) {
			session.setAttribute("filter" + filterType, searchFilter);
			SearchFilter sFilter = searchFilterService.findUserSearchFilter("default" + filterType, cxt.getCurrentUser(),filterType).orElse(null);

			if (sFilter == null) {
                searchFilterService.save(searchFilter);
			} else {
				sFilter.getFilterValues().clear();
				for (Object obj : searchFilter.getFilterValues()) {
					SearchFilterValue sfv = (SearchFilterValue) obj;
					sFilter.addFilterValue(sfv.getFieldName(), sfv.getFieldValue());
				}
                searchFilterService.save(sFilter);
			}
		}
	}

	public void loadDefaultFilterAndSearch(Properties properties, Object form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (form instanceof SearchForm) {

			SearchForm searchForm = (SearchForm) form;
			String filterTypeName = searchForm.getSearchDefinition() != null
					? searchForm.getSearchDefinition().getName()
					: null;
			if (filterTypeName == null)
				return;
			String filterType = (StringUtils.trimToNull(filterTypeName)) != null ? filterTypeName
					: searchForm.getClass().getName();
			HttpSession session = request.getSession(false);
			SearchFilter filter = null;
			if (session != null) {
				filter = (SearchFilter) session.getAttribute("filter" + filterType);
			}
            List<String> punchInPaths = pcmConfigUtill.getList("pcm.Alert.punchInPath", Collections.EMPTY_LIST);
            String cleanedPath = request.getServletPath().replaceAll("^/|\\.do$", "");
                if (punchInPaths == null || !punchInPaths.contains(cleanedPath)) {
                    if (filter != null) {
                        Map values = filter.getFilterValueMap();
                        Iterator itr = values.keySet().iterator();
                        while (itr.hasNext()) {
                            String fieldName = (String) itr.next();
                            List fieldValues = (List) values.get(fieldName);
                            SearchParameter sc = searchForm.getSearchParameter(fieldName);
                            if (sc == null) {
                                continue;
                            }
                            if (sc.isValueArray()) {
                                searchForm.setValues(fieldName, fieldValues.toArray());
                            } else {
                                searchForm.setValue(fieldName, fieldValues.get(0));
                            }
                        }
                    } else {
                        ApplicationContext cxt = AppContextHelper.getValidContext(request);

                        Optional<SearchFilter> sfo = searchFilterService.findUserSearchFilter("default" + filterType, cxt.getCurrentUser(), filterType);
                        SearchFilter sf = null;
                        if (sfo.isPresent() && sfo.get() != null) {
                            sf = sfo.get();
                            searchForm.clearParameterValues();
                            Map values = sf.getFilterValueMap();
                            Iterator itr = values.keySet().iterator();
                            while (itr.hasNext()) {
                                String fieldName = (String) itr.next();
                                List fieldValues = (List) values.get(fieldName);
                                SearchParameter sc = searchForm.getSearchParameter(fieldName);
                                if (sc == null) {
                                    continue;
                                }
                                if (sc.isValueArray()) {
                                    searchForm.setValues(fieldName, fieldValues.toArray());
                                } else {
                                    searchForm.setValue(fieldName, fieldValues.get(0));
                                }
                            }
                        }
                        session.setAttribute("filter" + filterType, sf);
                    }
                }
		}
	}
    
	public GenericResultSet asynchronousSearch(Object form, ApplicationContext ac, Boolean readOnlyQuery)
			throws Exception {
		if (form instanceof SearchForm) {
			SearchForm sf = (SearchForm) form;
			SearchDefinition sd = sf.getSearchDefinition();
			if (sd == null) {
				throw new Exception("Search Form not initialized");
			}

			if (readOnlyQuery != null) {
				searchQueryBuilder.setReadOnlyQuery(readOnlyQuery.booleanValue());
			}
			return searchQueryBuilder.executeQuery(sd, sf.getAllParameters(), sf.getPresetValues(), -1, -1);
		}
		return null;
	}

    /**
     * This method has been extracted from SearchAction to enable access filter and to call modify business filter method based on client need.
     *
     */
    public void enableAccessFilter(ApplicationContext ac, SearchDefinition sd) throws InvalidUserContext {
        AppContextHelper.enableAccessFilter(ac,jpaFilterUtil,pcmConfigUtill);
        modifyBusinessFilter(ac, sd);
    }


    /**
     * This method is used to modify the business filter based on client need.
     *
     * @param ac
     * @param sd
     * @throws InvalidUserContext
     */
    @SuppressWarnings("rawtypes")
    public void modifyBusinessFilter(ApplicationContext ac, SearchDefinition sd) throws InvalidUserContext {

        boolean businessFilterModified = pcmConfigUtill.getBoolean("pcm.common.enterprise.data.enable.toSupplier", false);
        if (!businessFilterModified) {
            return;
        }

        businessFilterModified = pcmConfigUtill.getBoolean("pcm.common.enterprise.data.enable.toSupplier." + sd.getName(),
                false);

        if (!businessFilterModified) {
            return;
        }

        String customerCode = pcmConfigUtill.getString("pcm.customer", "scplatform");
        Map<String, Object> params = new HashMap<String, Object>();
        Set keys = ac.getValidBusinessEntityKeys();
        params.put("businessEntity", keys);
        params.put("enterpriseBusinessEntity", ac.getEnterpriseKey());

        AppContextHelper.modifyBusinessFilter(jpaFilterUtil,ac, customerCode.toUpperCase(), params);
        isBusinessFilterModified = true;
    }

    public boolean isBusinessFilterModified() {
        return isBusinessFilterModified;
    }

    /**
     * Merge request form with cached SearchForm
     * Extracts fresh SearchParameter values from request form
     * and combines with cached form's SearchDefinition
     * 
     * @param requestForm Fresh form from request binding (has fresh SearchParameter values)
     * @param request HttpServletRequest
     * @return Merged form with cached definition + fresh parameter values
     * @throws Exception if context cannot be obtained
     */
    public <T extends SearchForm> T mergeRequestWithCachedForm(T requestForm, HttpServletRequest request) throws Exception {
        if (requestForm == null) {
            log.warn("Request form is null, cannot merge");
            return requestForm;
        }

        try {
            HttpSession session = request.getSession();
            ApplicationContext ac = AppContextHelper.getValidContext(request);
            Long userId = ac.getCurrentUser().getUserKey();
			String classFormName = requestForm.getClass().getSimpleName();
			String formName = StringUtils.isNotBlank(requestForm.getFilterType()) ? requestForm.getFilterType() : classFormName;

            // Try to get cached form (has cached SearchDefinition and structure)
            T cachedForm = sessionSearchCache.getCachedSearchForm(session, formName, userId);
			if (cachedForm == null && !classFormName.equals(formName)) {
				cachedForm = sessionSearchCache.getCachedSearchForm(session, classFormName, userId);
				if (cachedForm != null) {
					log.debug("Found cached form using class-name key fallback: {}", classFormName);
				}
			}
            
            if (cachedForm != null) {
                // Merge: use cached form as base, update with fresh request form values
                T mergedForm = mergeFormWithRequestForm(cachedForm, requestForm, request);
				String mergedFormName = StringUtils.isNotBlank(mergedForm.getFilterType()) ? mergedForm.getFilterType() : classFormName;

                // Re-cache the merged form
				sessionSearchCache.cacheSearchForm(session, mergedFormName, userId,
                        cachedForm.getSearchDefinition(), mergedForm);
				if (!classFormName.equals(mergedFormName)) {
					sessionSearchCache.cacheSearchForm(session, classFormName, userId,
							cachedForm.getSearchDefinition(), mergedForm);
				}
                
                log.debug("Successfully merged request form with cached form");
                return mergedForm;
            } else {
                log.debug("No cached form found for {}, returning request form as-is", formName);
                return requestForm;
            }
        } catch (Exception e) {
            log.error("Error merging request with cached form", e);
            throw e;
        }
    }

    /**
     * Merge request form data into cached SearchForm
     * Extracts fresh SearchParameter values from the request-bound form
     * and KEEPS form properties (pagination, filters, display) from cached form
     *
     * This preserves user's pagination, filter selections from previous request
     * while updating SearchParameters with fresh request values
     *
     * @param cachedForm The cached SearchForm to update (has cached SearchDefinition + properties)
     * @param requestForm The fresh form from request binding (has fresh SearchParameter values)
     * @return Merged SearchForm with:
     *         - Fresh SearchParameters from requestForm
     *         - Form properties (pagination, filters) kept from cachedForm
     *         - Cached SearchDefinition reused
     */
    public <T extends SearchForm> T mergeFormWithRequestForm(T cachedForm, T requestForm, HttpServletRequest request) {
        if (cachedForm == null) {
            return requestForm;
        }

        if (requestForm == null) {
            return cachedForm;
        }

        try {
            {
                requestForm.getValue().forEach((fieldName, value) -> {
                    try {
                        cachedForm.setValue(fieldName, value);
                    } catch (Exception e) {
                        log.error("Error invoking setter for field: " + fieldName, e);
                    }
                });

                requestForm.getDateValue().forEach((fieldName, value) -> {
                    try {
                        cachedForm.setDateValue(fieldName, (String) value);
                    } catch (SearchFormException e) {
                        log.error("Error invoking setter for field: " + fieldName, e);
                    } catch (ParseException e) {
                        log.error("Parsing error while converting : " + fieldName, e);
                    }
                });
            }

            // Override specific fields from request form into cached form
            overrideSelectiveFields(cachedForm, requestForm, request);

        } catch (Exception e) {
            log.error("Error merging request form with cached form", e);
        }

        return cachedForm;
    }

    /**
     * Override specific UI fields from request form into cached form
     * These fields represent user interactions and UI state, should be fresh from request
     * All other fields kept from cache (SearchDefinition, SearchParameters, etc)
     * 
     * Fields overridden:
     * - selectedFilterName, selectedFilterKeys, operation, searchParametersChanged
     * - checkLastAccessParam, selectedFilter, columns, condensedView, defaultDisplay
     * - pageStartAt, totalRows, pageNum, pageSize (pagination)
     * 
     * @param cachedForm Cached form to update (in-place modification)
     * @param requestForm Fresh form from request binding
     */
    private void overrideSelectiveFields(SearchForm cachedForm, SearchForm requestForm, HttpServletRequest request) {
        try {
            if (requestForm.getPageStartAt() > 0) {
                cachedForm.setPageStartAt(requestForm.getPageStartAt());
            }
            
            if (requestForm.getPageSize() > 0) {
                cachedForm.setPageSize(requestForm.getPageSize());
            }

            if (requestForm.getTotalRows() > 0) {
                cachedForm.setTotalRows(requestForm.getTotalRows());
            }

            // ----- UI / interaction fields: override only if present in the request with a value -----
            // String fields
            if (hasRequestParam(request, "selectedFilterName")) {
                cachedForm.setSelectedFilterName(requestForm.getSelectedFilterName());
            }
            if (hasRequestParam(request, "selectedFilter")) {
                cachedForm.setSelectedFilter(requestForm.getSelectedFilter());
            }
            if (hasRequestParam(request, "operation")) {
                cachedForm.setOperation(requestForm.getOperation());
            }
            if (hasRequestParam(request, "columns")) {
                cachedForm.setColumns(requestForm.getColumns());
            }

            // String[] fields
            if (hasRequestParamValues(request, "selectedFilterKeys")) {
                cachedForm.setSelectedFilterKeys(requestForm.getSelectedFilterKeys());
            }

            // Boolean / primitive boolean fields (presence in request = override)
            if (hasRequestParam(request, "clearSelection")) {
                cachedForm.setClearSelection(requestForm.getClearSelection());
            }
            if (hasRequestParam(request, "searchParametersChanged")) {
                cachedForm.setSearchParametersChanged(requestForm.getSearchParametersChanged());
            }
            if (hasRequestParam(request, "checkLastAccessParam")) {
                cachedForm.setCheckLastAccessParam(requestForm.isCheckLastAccessParam());
            }
            if (hasRequestParam(request, "condensedView")) {
                cachedForm.setCondensedView(requestForm.getCondensedView());
            }
            if (hasRequestParam(request, "defaultDisplay")) {
                cachedForm.setDefaultDisplay(requestForm.isDefaultDisplay());
            }

        } catch (Exception e) {
            log.error("Error overriding selective fields", e);
        }
    }

    /**
     * Returns true if the given parameter is present in the request and has a non-blank value.
     */
    private boolean hasRequestParam(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return false;
        }
        String value = request.getParameter(name);
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Returns true if the given multi-valued parameter is present in the request
     * and has at least one non-blank value.
     */
    private boolean hasRequestParamValues(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            return false;
        }
        String[] values = request.getParameterValues(name);
        if (values == null || values.length == 0) {
            return false;
        }
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Capitalize first letter of a string
     * Examples: value → Value, param → Param, data → Data
     *
     * @param str String to capitalize
     * @return Capitalized string
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Dynamically construct and invoke a setter method on the form object
     * Looks for method: setterName(String key, Object[] values)
     * Uses reflection to find and invoke the method across class hierarchy
     *
     * Examples:
     * - setValues("profileName", ["xyz"])
     * - setParams("categoryId", ["123", "456"])
     * - setDatas("startDate", ["2026-04-30"])
     *
     * @param form Form object to invoke setter on
     * @param setterName Name of setter method (e.g., "setValues", "setParams")
     * @param key First parameter (property name)
     * @param values Second parameter (array of values from request)
     * @throws Exception if method not found or invocation fails
     */
    private void invokeSetterMethod(SearchForm form, String setterName, String key, String[] values) throws Exception {
        if (form == null || setterName == null || key == null || values == null) {
            return;
        }

        Class<?> clazz = form.getClass();

        // Search through class hierarchy
        while (clazz != null && !clazz.equals(Object.class)) {
            java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();

            // Find method matching: setterName(String, Object[])
            for (java.lang.reflect.Method method : methods) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 2) {
                    Class<?>[] paramTypes = method.getParameterTypes();

                    // Check if signature matches: (String, Object[])
                    if (String.class.equals(paramTypes[0]) && Object[].class.equals(paramTypes[1])) {
                        // Make accessible if private
                        method.setAccessible(true);

                        // Invoke: setValues(key, values) or setParams(key, values), etc.
                        method.invoke(form, key, (Object) values);
                        log.debug("Invoked setter '{}({}, Object[])' with key='{}' and {} values",
                                setterName, String.class.getSimpleName(), key, values.length);
                        return;
                    }
                }
            }

            // Try parent class
            clazz = clazz.getSuperclass();
        }

        // Method not found - log warning
        log.warn("Setter method '{}(String, Object[])' not found on form {} or its parent classes",
                setterName, form.getClass().getSimpleName());
    }

    public void setSupplierCountForTAMDownload (TAMDownloadForm form, String noSupplierQuery) {
        StringBuilder supplierQuery = new StringBuilder(
                "select sum(count(be)/count(be)) from FunctionalGroup as fg,Site as site left join site.siteDetail as siteDetail");
        supplierQuery.append(" inner join fg.functionalGroupItems im ");
        supplierQuery.append(" inner join im.avls avl ");
        supplierQuery.append(" inner join avl.supplier be ");
        supplierQuery.append(" left join fg.parentFunctionalGroup pfg");


        StringBuilder groupBySupplier = new StringBuilder(" group by be,site,fg");

        Long  supplierCount = searchQueryBuilder.getQueryCount(supplierQuery.toString(),
                form.getSearchDefinition(), form.getAllParameters(),
                form.getPresetValues(), groupBySupplier.toString(), noSupplierQuery);

        form.setSupplierCount(supplierCount == null ? 0 : supplierCount);
    }

    public void setItemCountForTAMDownload (TAMDownloadForm form, String noSupplierQuery) {
        StringBuilder itemQuery = new StringBuilder(
                " select sum(count(im)/count(im)) from FunctionalGroup as fg,Site as site left join site.siteDetail as siteDetail");
        itemQuery.append(" inner join fg.functionalGroupItems im ");
        itemQuery.append(" inner join im.avls avl ");
        itemQuery.append(" inner join avl.supplier be ");
        itemQuery.append(" left join fg.parentFunctionalGroup pfg");

        StringBuilder groupByItem = new StringBuilder(" group by be,im,site,fg");

        Long itemCount = searchQueryBuilder.getQueryCount(itemQuery.toString(), form.getSearchDefinition(),
                form.getAllParameters(), form.getPresetValues(),
                groupByItem.toString(), noSupplierQuery);

        form.setItemCount(itemCount == null ? 0 : itemCount);
    }
}