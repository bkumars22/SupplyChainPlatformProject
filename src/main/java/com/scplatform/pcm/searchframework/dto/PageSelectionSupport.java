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

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Set;

/**
 * Support for paging a result set of some kind. Provides the needed form
 * support to handle selection of rows across multiple pages as in search
 * results
 * @author bblasko
 */
public class PageSelectionSupport extends PcmBaseForm {
    public static final String RESETALL = "RA";
    public static final String RESETPAGE = "RP";
    private Set<String> selectedIds = new HashSet<String>();
    private Set currentPageIds = new HashSet();
    private Integer pageStartAt = -1;
    private Integer pageSize = -1;
    private Long totalRows = 0L;
    private boolean clearSelection = false;
    private boolean pagingEnabled = true;

    /**
     * Used when paging results sets Sets the keys selected for that page
     * 
     * @param keys
     */
    public void setSelectedPageKeys(String[] keys) {
    	//selectedIds.clear();
	for (int idx = 0; idx < keys.length; idx++) {
	    selectedIds.add(keys[idx]);
	}
    }

    public String[] getSelectedPageKeys() {
	return (String[]) selectedIds.toArray(new String[] {});
    }

    /**
     * Used by the MultipageCheckbox tag to fill a set of keys that are set
     * prior to posting the page. When the page is posted, the new set of ids
     * will be compared to this one. Any keys not in the new set will be
     * removed. This is to overcome the issue of non-checked HTML checkboxs not
     * submitting a value when they are not checked
     * 
     * @return
     */
    public Set getCurrentPageIds() {
	return currentPageIds;
    }

    public void setCurrentPageIds(Set c) {
	currentPageIds.addAll(c);
    }

    /**
     * Returns all selected keys
     * 
     * @return
     */
    public Set<String> getSelectedKeys() {
	return selectedIds;
    }

    /**
     * @param pageStartAt
     *            The pageStartAt to set.
     */
    public void setPageStartAt(Integer pageStartAt) {
        if(pageStartAt != null){
            this.pageStartAt = pageStartAt;
        }
    }

    /**
     * @return Returns the pageStartAt.
     */
    public int getPageStartAt() {
        return pageStartAt == null ? 0 : pageStartAt;
    }

    /**
     * @param pageSize
     *            The pageSize to set.
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return Returns the pageSize.
     */
    public int getPageSize() {
        return pageSize == null ? -1 : pageSize;
    }

    public int getCurrentPage() {
        int ps = getPageSize();
        int sa = getPageStartAt();
        return (ps > 0 && sa > 0) ? (sa / ps) + 1 : 1;
    }

    /**
     * @param totalRows
     *            The totalRows to set.
     */
    public void setTotalRows(Long totalRows) {
        if(totalRows != null){
            this.totalRows = totalRows;
        }
    }

    /**
     * @return Returns the totalRows.
     */
    public long getTotalRows() {
        return totalRows == null ? 0L : totalRows;
    }

    public long getAtPage() {
        int ps = getPageSize();
        int sa = getPageStartAt();
        return ps > 0 ? (sa / ps) + 1 : 1;
    }

    public long getMaxPage() {
        int ps = getPageSize();
        if (ps <= 0) {
            return 1;
        }
        long total = getTotalRows();
        long max = total / ps;
        if (total % ps > 0 || max == 0) {
            max++;
        }
        return max;
    }

    public void reset(HttpServletRequest request) {
        super.reset(request);

        // Clear the current page selection ids
        if (currentPageIds != null && currentPageIds.size() > 0) {
            selectedIds.removeAll(currentPageIds);
            currentPageIds.clear();
        }
        clearSelection = false;

        // Initialize page size if needed
        if (getAppContext() != null && getPagingEnabled() && getPageSize() < 0) {
            setPageSize(getAppContext().getCurrentUser().getDefaultPageSize());
        }
    }

    public void setClearSelection(boolean clearSelection) {
	this.clearSelection = clearSelection;
    }

    public boolean getClearSelection() {
	return clearSelection;
    }

    public void clearSelection() {
        if(clearSelection){
            selectedIds.clear();
        }
	    currentPageIds.clear();
    }

    public void resetPagingValues() {
        pageStartAt = 0;
        totalRows = 0L;
    }

    public void setPagingEnabled(boolean pagingEnabled) {
	this.pagingEnabled = pagingEnabled;
    }

    public boolean getPagingEnabled() {
	return pagingEnabled;
    }
}