/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.commodityCode;

import java.util.ArrayList;
import java.util.List;

import com.scplatform.qa.iris.predicates.criteria.StringCriteria;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Filters {@link CommodityCode} Messages
 * 
 * Chained Call Example
 * <pre>
 * CommodityCodeFilter filter = new CommodityCodeFilter();
 * Iterable<CommodityCode> data = filter.byCommodityCodeName("DIS").apply(commodityCodeData);
 * </pre>
 * 
 * @author dgenrich
 * 
 * @see #byCommodityCodeName(String)
 * @see #byCommodityCodeDescription(String)
 * @see #byParentCommodityCode(String)
 */
public class CommodityCodeFilter<T extends CommodityCode> {

    public Iterable<T> apply(Iterable<T> data) {
        Predicate<T> predicate = getPredicate();
        if (predicate == null)
            return data;
        
        Iterable<T> filtered = Iterables.filter(data, predicate);
        return filtered;
    }

    public List<T> applyReturnList(Iterable<T> data) {
        Predicate<T> predicate = getPredicate();
        if (predicate == null)
            return Lists.newArrayList(data);

        Iterable<T> filtered = Iterables.filter(data, predicate);
        return Lists.newArrayList(filtered);
    }

    // ===========================================
    // CHAINED CALLS
    // ===========================================

    /**
     * Set to CommodityCode Name to filter on.  Can be called multiple times to filter by multiple CommodityCodes.
     * 
     * @param name
     * @return
     */
    public CommodityCodeFilter<T> byCommodityCodeName(String name) {
        commodityCodeNames.add(name);
        return this;
    }

    /**
     * Set to CommodityCode Description to filter on.  Can be called ultiple times to filter by multiple CommodityCodes Descriptions.
     * @param description
     * @return
     */
    public CommodityCodeFilter<T> byCommodityCodeDescription(String description) {
        commodityCodeDesc.add(description);
        return this;
    }

    /**
     * Set to Parent CommodityCode Name to filter on.  Can be called multiple times to filter by multiple CommodityCodes.
     * 
     * @param name
     * @return
     */
    public CommodityCodeFilter<T> byParentCommodityCode(String name) {
    	parentCommodityCode.add(name);
        return this;
    }
    


    // ===========================================
    // Predicates
    // ===========================================
    private Predicate<T> getPredicate() {
        List<Predicate<T>> predicates = new ArrayList<>();

        if (!commodityCodeNames.isEmpty())
            predicates.add(getCommodityCodeNamePredicate());
        if (!commodityCodeDesc.isEmpty())
            predicates.add(getCommodityCodeDescriptionPredicate());
        if (!parentCommodityCode.isEmpty())
            predicates.add(getParentCommodityCodePredicate());

        if (predicates.isEmpty())
            return null;
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        Predicate<T> anded = Predicates.and(predicates);
        return anded;
    }

    private final List<String> commodityCodeNames           = new ArrayList<String>();
    private final List<String> commodityCodeDesc           = new ArrayList<String>();
    private final List<String> parentCommodityCode           = new ArrayList<String>();
    
    
    private Predicate<T> getCommodityCodeNamePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereCommodityCodeName()
    			.satisfies(new StringCriteria() {
					
					public boolean evaluate() {
						String[] array = commodityCodeNames.toArray(new String[commodityCodeNames.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }

    private Predicate<T> getCommodityCodeDescriptionPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereDescription()
    			.satisfies(new StringCriteria() {
					
					public boolean evaluate() {
						String[] array = commodityCodeDesc.toArray(new String[commodityCodeDesc.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }


    private Predicate<T> getParentCommodityCodePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereParentCommodityCode()
    			.satisfies(new StringCriteria() {
					
					public boolean evaluate() {
						String[] array = parentCommodityCode.toArray(new String[parentCommodityCode.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }


}



