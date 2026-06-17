/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.forecast;


import java.util.ArrayList;
import java.util.List;

import com.scplatform.qa.iris.predicates.criteria.StringCriteria;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Filters {@link Forecast} Messages
 * 
 * Chained Call Example
 * <pre>
 * ForecastFilter filter = new ForecastFilter();
 * Iterable<Forecast> data = filter.byItemIdentifier("DISPLAY-17").apply(forecastData);
 * </pre>
 * 
 * @author dgenrich
 * 
 * @see #byBusinessEntity(String)
 * @see #byItemIdentifier(String)
 * @see #bySite(String)
 * @see #byForecastModel(String)
 */
public class ForecastFilter<T extends Forecast> {

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
     * Set to BusinessEntity Name to filter on.  Can be called multiple times to filter by multiple names.
     * 
     * @param name
     * @return
     */
    public ForecastFilter<T> byBusinessEntity(String name) {
        businessEntity.add(name);
        return this;
    }

    /**
     * Set to ItemIdentifier to filter on.  Can be called multiple times to filter by multiple item names.
     * @param itemName
     * @return
     */
    public ForecastFilter<T> byItemIdentifier(String itemName) {
        itemIdentifier.add(itemName);
        return this;
    }

    /**
     * Set to Site Name to filter on.  Can be called multiple times to filter by multiple sites.
     * 
     * @param name
     * @return
     */
    public ForecastFilter<T> bySite(String name) {
    	site.add(name);
        return this;
    }
    

    /**
     * Set to Forecast Model to filter on.  Can only be called once.
     * 
     * @param model	Case is ignored in this filter
     * @return
     */
    public ForecastFilter<T> byForecastModel(String model) {
    	forecastModel = model;
        return this;
    }

    // ===========================================
    // Predicates
    // ===========================================
    private Predicate<T> getPredicate() {
        List<Predicate<T>> predicates = new ArrayList<>();

        if (!businessEntity.isEmpty())
            predicates.add(getBusinessEntityPredicate());
        if (!itemIdentifier.isEmpty())
            predicates.add(getItemIdentifierPredicate());
        if (!site.isEmpty())
            predicates.add(getSitePredicate());
        if (forecastModel != null)	
        	predicates.add(getForecastModelPredicate());
        if (predicates.isEmpty())
            return null;
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        Predicate<T> anded = Predicates.and(predicates);
        return anded;
    }

    private final List<String> businessEntity        = new ArrayList<String>();
    private final List<String> itemIdentifier       = new ArrayList<String>();
    private final List<String> site           		= new ArrayList<String>();
    private String forecastModel					= null;
    
    private Predicate<T> getBusinessEntityPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereBusinessEntity()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = businessEntity.toArray(new String[businessEntity.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }

    private Predicate<T> getItemIdentifierPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereItemIdentifier()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = itemIdentifier.toArray(new String[itemIdentifier.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }


    private Predicate<T> getSitePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = site.toArray(new String[site.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }

    private Predicate<T> getForecastModelPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereForecastModel()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						return equalsIgnoreCase(forecastModel);
					}
				}).build();
    	
    	return predicate;
    }
    
    
}




