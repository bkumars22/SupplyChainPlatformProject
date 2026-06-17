/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.dto;


import com.scplatform.pcm.searchframework.exception.SearchFormException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SearchForm extends PageSelectionSupport {

    protected HashMap<String, Object> presetParameterValues = new HashMap<String, Object>();
	protected Map<String, SearchParameter> searchParameters = new LinkedHashMap<String, SearchParameter>();
    private GenericResultSet searchResult;
    private SearchDefinition searchDefinition;
    private String searchAction;
    private Map<String, String> availableFilters = new LinkedHashMap<String, String>();
    protected String[] selectedFilterKeys;
    private String selectedFilter;
    private String selectedFilterName;
    private String availableColumns;
    private String columns;
    private Map<Long, String> availableDisplay = new LinkedHashMap<Long, String>();
    private long selectedDisplay;
    private String displayDescription;
    private boolean defaultDisplay;
    private String selectedDisplayName;
    private String operation;
    private boolean searchParametersChanged = false;
    private boolean showSaveFilter = true;
    private boolean showFilterArea = true;
    private boolean filterAreaCollapsed = false;
    private String filterType;
    private String nextAction;
    private String previousAction;
    private boolean preserveSearchValues;
    private HashMap<String, Object> initValues = new HashMap<String, Object>();
    protected String extractFileName;
    private Map<String, Object> value = new LinkedHashMap<>();
    private Map<String, Object> dateValue = new LinkedHashMap<>();

    private boolean applyToAll = false;

    protected String extractWriterClass = null;
    
    private boolean initFlag = true;

	private boolean condensedView=true;

	private String finderName; 
	private String resultCallbackFunc;
	private String resultField;
	private String finderParamValue;	
	private boolean multiSelect;
	private boolean checkLastAccessParam = false;
    protected List<String> restrictedColumnList = new ArrayList<>();
    private String reportType;

    public List<String> getRestrictedColumnList() {
        return restrictedColumnList;
    }

    public void setRestrictedColumnList(List<String> restrictedColumnList) {
        this.restrictedColumnList = restrictedColumnList;
    }

	public boolean getCondensedView() {
		return condensedView;
	}

	public void setCondensedView(boolean condensedView) {
		this.condensedView = condensedView;
	}
	
	public boolean isInitFlag() {
		return initFlag;
	}

	public void setInitFlag(boolean initFlag) {
		this.initFlag = initFlag;
	}

	@Override
    public void reset(HttpServletRequest request) {
        super.reset(request);
        // Manually marshal the this attribute
        preserveSearchValues = "true".equalsIgnoreCase(request.getParameter("preserveSearchValues"));
        searchParametersChanged = false;
        operation = null;
        selectedFilterName = null;
        selectedFilter = null;
        extractFileName = null;
        applyToAll = false;
        // Default save filter to true
        showSaveFilter = true;
        // Collapse to false
        filterAreaCollapsed = false;
        initFlag = true;
        selectedFilterKeys = null;
        filterType = null;
	    selectedDisplay = 0L; 
	    defaultDisplay = false;
    	this.resultCallbackFunc = null;
    	this.resultField = null;
    	this.finderParamValue = null;
    	this.multiSelect = true;
    	this. checkLastAccessParam = false;
        /**
         * If the flag is set to not reset or we have init values don't reset them. The only way we would have full set
         * of initValues at this point is if we forwarded from another action, otherwise, they would not have been
         * marshalled yet. The initValues will be moved to the parameterValues via the init action of the SearchAction
         **/
        if (preserveSearchValues == false && initValues.isEmpty()) {
            clearParameterValues();
        }
        if (preserveSearchValues) {
            clearSelection();
        }
        initValues.clear();
        restrictedColumnList.clear();
    }

    public String[] getSelectedFilterKeys() {
		return selectedFilterKeys;
	}

	public void setSelectedFilterKeys(String[] selectedFilterKeys) {
		if(selectedFilterKeys != null && selectedFilterKeys.length > 0 && selectedFilterKeys[0].contains(","))
		{
			String temp[] = selectedFilterKeys[0].split(",");
			this.selectedFilterKeys = temp;
		}
		else
		{
			this.selectedFilterKeys = selectedFilterKeys;
		}
	}

    public void clearSearchParameters() {
        searchParameters.clear();
    }

    public boolean getExtractEnabled() {
        if (searchDefinition != null && searchDefinition.getExtractSource() != null) {
            return true;
        }
        return false;
    }

    public boolean getSearchParametersChanged() {
        return searchParametersChanged;
    }

    public void setSearchParametersChanged(boolean searchParametersChanged) {
        this.searchParametersChanged = searchParametersChanged;
    }

    public boolean getApplyToAll() {
        return applyToAll;
    }

    public void setApplyToAll(boolean applyToAll) {
        this.applyToAll = applyToAll;
    }

    public void addSearchParameter(SearchParameter parameter) {
        searchParameters.put(parameter.getName(), parameter);
    }

    public void setSearchParameters(List<SearchParameter> parameters) {
        searchParameters.clear();
        for (SearchParameter parameter : parameters) {
            addSearchParameter(parameter);
        }
    }

    public void clearParameterValues() {
        Iterator<SearchParameter> itr = getAllParameters().iterator();
        while (itr.hasNext()) {
            itr.next().setValue(null);
        }
    }

    public Collection<SearchParameter> getAllParameters() {
        return searchParameters.values();
    }

    public Set<String> getSearchParameterNames() {
        return searchParameters.keySet();
    }

    public SearchParameter getSearchParameter(String key) throws SearchFormException {
        SearchParameter sp = searchParameters.get(key);
        return sp;
    }

    public void setValues(String key, Object[] values) throws SearchFormException {
        SearchParameter sp = getSearchParameter(key);
        if (sp != null) {
            if ("Date".equalsIgnoreCase(sp.getDataType())) {
                Object[] newValues = new Object[values.length];
                String format = "MM-dd-yyyy";
                if (sp.getDataFormat() != null) {
                    format = sp.getDataFormat();
                }
                for (int i = 0; i < values.length; i++) {
                    Object value = values[i];
                    try {
                        SimpleDateFormat parserSDF = new SimpleDateFormat(format);
                        value = parserSDF.parse((String) value);
                    } catch (Exception e) {
                        throw new SearchFormException(e);
                    }
                    newValues[i] = value;
                }
                sp.setValue(newValues);
            } else {
                sp.setValue(values);
            }
        }
    }

    public Object[] getValues(String key) throws SearchFormException {
        SearchParameter sp = getSearchParameter(key);
        if (sp == null) {
            return null;
        }

        Object[] values = (Object[]) sp.getValue();
        if (values == null) {
            return null;
        }

        Object[] retValues = new Object[values.length];
        if (values.length > 0) {
            if ("Date".equalsIgnoreCase(sp.getDataType())) {
                String format = "MM-dd-yyyy";
                if (sp.getDataFormat() != null) {
                    format = sp.getDataFormat();
                }
                SimpleDateFormat df = new SimpleDateFormat(format);
                for (int i = 0; i < values.length; i++) {
                    Date date = (Date) values[i];
                    retValues[i] = df.format(date);
                }
            } else {
                retValues = values;
            }
        } else {
            retValues = values;
        }
        return retValues;
    }

    public void setValue(String key, Object value) throws SearchFormException {
        SearchParameter sp = getSearchParameter(key);
        if (sp != null) {
            if ("number".equalsIgnoreCase(sp.getDataType())) {
                String sv = StringUtils.trimToNull(value.toString());
                if (sv != null) {
                    value = NumberUtils.createLong(value.toString());
                } else {
                    value = null;
                }
            } else if ("boolean".equalsIgnoreCase(sp.getDataType())) {
                value = BooleanUtils.toBooleanObject(StringUtils.trimToNull((String.valueOf(value))));
            } else if ("Date".equalsIgnoreCase(sp.getDataType())) {
                if(value instanceof String){
                    String format = "MM-dd-yyyy";
                    if (sp.getDataFormat() != null) {
                        format = sp.getDataFormat();
                    }
                    try {
                        SimpleDateFormat parserSDF = new SimpleDateFormat(format);
                        value = parserSDF.parse((String) value);
                    } catch (Exception e) {
                        throw new SearchFormException(e);
                    }
                }
            } else if (value instanceof String) {
                value = StringUtils.trimToNull((String) value);
            }
            sp.setValue(value);
        }
    }

    public Object getValue(String key) throws SearchFormException {
        SearchParameter sp = getSearchParameter(key);
        return sp != null ? sp.getValue() : sp;
    }

    public Object getDateValue(String key) throws SearchFormException {
        SimpleDateFormat df = new SimpleDateFormat(getCurrentDateFormat(), getCurrentLocale());
        Object v = getValue(key);

        return (v != null) ? df.format(v) : null;
    }

    public void setOrderBy(String key, String value) {
        searchDefinition.setOrderBy(key, value);
    }

    public String getOrderBy(String key) {
        return searchDefinition.getOrderBy(key).name();
    }

    /**
     * This is used by EL since it cannot access using parameters
     * 
     * @return
     */
    public Map<String, String> getOrderColumnMap() {
        return searchDefinition.getOrderColumnMap();
    }

    public Map<String, SearchDefinition.Order> getOrderByMap() {
        return searchDefinition.getOrderByMap();
    }

    public Set<String> getOrderByFields() {
        return searchDefinition.getOrderByFields();
    }

    public void setSearchResult(GenericResultSet searchResult) {
        this.searchResult = searchResult;
    }

    public GenericResultSet getSearchResult() {
        return searchResult;
    }

    public void setSearchDefinition(SearchDefinition searchDefinition) {
        this.searchDefinition = searchDefinition;
    }

    public SearchDefinition getSearchDefinition() {
        return searchDefinition;
    }

    public void setSearchAction(String searchAction) {
        this.searchAction = searchAction;
    }

    public String getSearchAction() {
        return searchAction;
    }

    /**
     * @param availableFilters
     *            The availableFilters to set.
     */
    public void setAvailableFilters(Map<String, String> availableFilters) {
        this.availableFilters = availableFilters;
    }

    /**
     * @return Returns the availableFilters.
     */
    public Map<String, String> getAvailableFilters() {
        return availableFilters;
    }

    /**
     * @param selectedFilter
     *            The selectedFilter to set.
     */
    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    /**
     * @return Returns the selectedFilter.
     */
    public String getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilterName(String selectedFilterName) {
        this.selectedFilterName = selectedFilterName;
    }

    public String getSelectedFilterName() {
        return selectedFilterName;
    }

	public Map<Long, String> getAvailableDisplay() {
		return availableDisplay;
	}

	public void setAvailableDisplay(Map<Long, String> availableDisplay) {
		this.availableDisplay = availableDisplay;
	}

	public String getAvailableColumns() {
		return availableColumns;
	}

	public void setAvailableColumns(String availableColumns) {
		this.availableColumns = availableColumns;
	}

	public long getSelectedDisplay() {
		return selectedDisplay;
	}

	public void setSelectedDisplay(long selectedDisplay) {
		this.selectedDisplay = selectedDisplay;
	}

	public String getSelectedDisplayName() {
		return selectedDisplayName;
	}

	public void setSelectedDisplayName(String selectedDisplayName) {
		this.selectedDisplayName = selectedDisplayName;
	}

	public String getDisplayDescription() {
		return displayDescription;
	}

	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}

	public boolean isDefaultDisplay() {
		return defaultDisplay;
	}

	public void setDefaultDisplay(boolean defaultDisplay) {
		this.defaultDisplay = defaultDisplay;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setShowFilterArea(boolean showFilterArea) {
        this.showFilterArea = showFilterArea;
    }

    public boolean getShowFilterArea() {
        return showFilterArea;
    }

    public void setShowSaveFilter(boolean showSaveFilter) {
        this.showSaveFilter = showSaveFilter;
    }

    public boolean getShowSaveFilter() {
        return showSaveFilter;
    }

    public void setFilterAreaCollapsed(boolean filterAreaCollapsed) {
        this.filterAreaCollapsed = filterAreaCollapsed;
    }

    public boolean getFilterAreaCollapsed() {
        return filterAreaCollapsed;
    }

    public void clearPresetValues() {
        presetParameterValues.clear();
    }

    public HashMap<String, Object> getPresetValues() {
        return presetParameterValues;
    }

    public void setPresetValue(String name, Object value) {
        presetParameterValues.put(name, value);
    }

    public void removePresetValue(String name) {
        presetParameterValues.remove(name);
    }

    // initValues are only used to pass values to the search from a post
    // and use them to preset search parameters.
    public HashMap<String, Object> getInitValues() {
        return initValues;
    }

    public void setInitValue(String key, Object value) {
        initValues.put(key, value);
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setPreviousAction(String previousAction) {
        this.previousAction = previousAction;
    }

    public String getPreviousAction() {
        return previousAction;
    }

    public boolean getHasNextPage() {
        return (this.getTotalRows() == Long.MAX_VALUE) || (this.getAtPage() < this.getMaxPage());
    }

    public boolean getHasPreviousPage() {
        return (this.getPageStartAt() > 0);
    }

    public boolean getShowPageMessage() {
        return (this.getTotalRows() != Long.MAX_VALUE);
    }

    public void setPreserveSearchValues(boolean preserveSearchValues) {
        this.preserveSearchValues = preserveSearchValues;
    }

    public boolean getPreserveSearchValues() {
        return preserveSearchValues;
    }

    public void setExtractFileName(String name) {
        this.extractFileName = name;
    }

    public String getExtractFileName() {
        return extractFileName;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterType() {
        return this.filterType;
    }


    /**
     * Returns the export writer class if it exists and was set in the UI page. This can be used to override the export
     * writer class defined in the search definition configuration
     */
    public String getExtractWriterClass() {
        return extractWriterClass;
    }

    public void setExtractWriterClass(String extractWriterClass) {
        this.extractWriterClass = extractWriterClass;
    }

    /**
     * Base class does nothing. The standard SearchAction init action will call this after attempting to load any
     * external definitions. Subclasses can use this to update or create search defs as needed
     */
    public boolean initialize(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

	public String getFinderName() {
		return finderName;
	}

	public void setFinderName(String finderName) {
		this.finderName = finderName;
	}

	public String getResultCallbackFunc() {
		return resultCallbackFunc;
	}

	public void setResultCallbackFunc(String resultCallbackFunc) {
		this.resultCallbackFunc = resultCallbackFunc;
	}

	public String getResultField() {
		return resultField;
	}

	public void setResultField(String resultField) {
		this.resultField = resultField;
	}
	public String getFinderParamValue() {
		return finderParamValue;
	}

	public void setFinderParamValue(String finderParamValue) {
		this.finderParamValue = finderParamValue;
	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	
	public boolean isCheckLastAccessParam() {
		return checkLastAccessParam;
	}

	public void setCheckLastAccessParam(boolean checkLastAccessParam) {
		this.checkLastAccessParam = checkLastAccessParam;
	}

    public String getReportType() {
	    return this.reportType;
	  }

	public void setReportType(String reportType) {
	    this.reportType = reportType;
	  }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }

    public Map<String, Object> getDateValue() {
        return dateValue;
    }

    public void setDateValue(Map<String, Object> dateValue) {
        this.dateValue = dateValue;
    }

    public void setDateValue(String key, String date) throws SearchFormException, ParseException {
        if (date == null || date.toString().length() == 0 ) {
            setValue(key, null);
        } else {
            SimpleDateFormat df = new SimpleDateFormat(getCurrentDateFormat(), getCurrentLocale());
            Date d = df.parse(date.toString());
            setValue(key, d);
        }
    }
}
