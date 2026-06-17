/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.sourcingLane;


import java.util.ArrayList;
import java.util.List;

import com.scplatform.qa.iris.predicates.criteria.StringCriteria;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Filters {@link SourcingLane} Messages
 * 
 * Chained Call Example
 * <pre>
 * SourcingLaneFilter filter = new SourcingLaneFilter();
 * Iterable<SourcingLane> data = filter.byFromBusinessEntity("Shenzhen").apply(sourcingLaneData);
 * </pre>
 * 
 * @author dgenrich
 * 
 * @see #bySourcingLaneIdentifier(String)
 * @see #byFromBusinessEntity(String)
 * @see #byFromBusinessEntityType(String)
 * @see #bySite(String)
 * @see #byFromSite(String)
 */
public class SourcingLaneFilter<T extends SourcingLane> {

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
     * Set to SourcingLane Name to filter on.  Can be called multiple times to filter by multiple SourcingLanes.
     * 
     * @param name
     * @return
     */
    public SourcingLaneFilter<T> bySourcingLaneIdentifier(String name) {
        sourcingLaneIdentifier.add(name);
        return this;
    }

    /**
     * Set to fromBusinessEntity to filter on.  Can be called ultiple times to filter by multiple fromBusinessEntitys.
     * @param fromEntity
     * @return
     */
    public SourcingLaneFilter<T> byFromBusinessEntity(String fromEntity) {
        fromBusinessEntity.add(fromEntity);
        return this;
    }

    /**
     * Set to FromBusinessEntityType to filter on.  Can be called multiple times to filter by multiple entityTypes.
     * 
     * @param entityType
     * @return
     */
    public SourcingLaneFilter<T> byFromBusinessEntityType(String entityType) {
    	fromBusinessEntityType.add(entityType);
        return this;
    }
    

    /**
     * Set to Site to filter on.  Can be called multiple times to filter by multiple sites.
     * 
     * @param siteName
     * @return
     */
    public SourcingLaneFilter<T> bySite(String siteName) {
    	site.add(siteName);
        return this;
    }


    /**
     * Set to From Site to filter on.  Can be called multiple times to filter by multiple sites.
     * 
     * @param fromSiteName
     * @return
     */
    public SourcingLaneFilter<T> byFromSite(String fromSiteName) {
    	fromSite.add(fromSiteName);
        return this;
    }
    

    // ===========================================
    // Predicates
    // ===========================================
    private Predicate<T> getPredicate() {
        List<Predicate<T>> predicates = new ArrayList<>();

        if (!sourcingLaneIdentifier.isEmpty())
            predicates.add(getSourcingLaneIdentifierPredicate());
        if (!fromBusinessEntity.isEmpty())
            predicates.add(getFromBusinessEntityPredicate());
        if (!fromBusinessEntityType.isEmpty())
            predicates.add(getFromBusinessEntityTypePredicate());
        if (!site.isEmpty())
            predicates.add(getSitePredicate());
        if (!fromSite.isEmpty())
            predicates.add(getFromSitePredicate());
        
        if (predicates.isEmpty())
            return null;
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        Predicate<T> anded = Predicates.and(predicates);
        return anded;
    }

    private final List<String> sourcingLaneIdentifier       = new ArrayList<String>();
    private final List<String> fromBusinessEntity           = new ArrayList<String>();
    private final List<String> fromBusinessEntityType       = new ArrayList<String>();
    private final List<String> site						    = new ArrayList<String>();
    private final List<String> fromSite 				    = new ArrayList<String>();
    
    private Predicate<T> getSourcingLaneIdentifierPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSourcingLaneIdentifier()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = sourcingLaneIdentifier.toArray(new String[sourcingLaneIdentifier.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }

    private Predicate<T> getFromBusinessEntityPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereFromBusinessEntity()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = fromBusinessEntity.toArray(new String[fromBusinessEntity.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }


    private Predicate<T> getFromBusinessEntityTypePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereFromBusinessEntityType()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = fromBusinessEntityType.toArray(new String[fromBusinessEntityType.size()]);
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

    private Predicate<T> getFromSitePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereFromSite()
    			.satisfies(new StringCriteria() {
					
					@Override
					public boolean evaluate() {
						String[] array = fromSite.toArray(new String[fromSite.size()]);
						return containsOneOf(array);
					}
				}).build();
    	
    	return predicate;
    }

}




