/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.item;


import java.util.ArrayList;
import java.util.List;

import com.scplatform.qa.iris.predicates.criteria.StringCriteria;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Filters {@link Item} Messages
 * 
 * Chained Call Example
 * <pre>
 * ItemFilter filter = new ItemFilter();
 * List<Item> data = filter.byItemIdentifier("item-123").applyReturnList(itemData);
 * </pre>
 * 
 * @author dgenrich
 * 
 * @see #byItemIdentifier(String)
 * @see #byBusinessEntity(String)
 */
public class ItemFilter<T extends Item> {

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
     * Set to byBusinessEntity to filter on.  Can be called multiple times to filter by multiple businesses.
     * 
     * @param customer
     * @return
     */
    public ItemFilter<T> byBusinessEntity(String customer) {
        businessEntity.add(customer);
        return this;
    }

    /**
     * Set to Item Name to filter on.  Can be called multiple times to filter by multiple items.
     * 
     * @param itemName
     * @return
     */
    public ItemFilter<T> byItemIdentifier(String itemName) {
        itemIdentifier.add(itemName);
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
         
        
        if (predicates.isEmpty())
            return null;
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        Predicate<T> anded = Predicates.and(predicates);
        return anded;
    }

    private final List<String> businessEntity           = new ArrayList<String>();
    private final List<String> itemIdentifier = new ArrayList<String>();

    private Predicate<T> getBusinessEntityPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereBusinessEntity()
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
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereItemIdentifier()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = itemIdentifier.toArray(new String[itemIdentifier.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }
    


    

}



