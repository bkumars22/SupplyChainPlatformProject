/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.contact;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.iris.predicates.criteria.DateTimeCriteria;
import com.scplatform.qa.iris.predicates.criteria.StringCriteria;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Filters {@link Contact} Messages
 * 
 * Chained Call Example
 * <pre>
 * ContactFilter filter = new ContactFilter();
 * Iterable<T> data = filter.byBusinessEntityName("Shenzhen").apply(businessEntityData);
 * </pre>
 * 
 * @author dgenrich
 * 
 * @see #byBusinessEntity(String)
 * @see #byBusinessEntityName(String)
 * @see #byContactName(String)
 * @see #beforeEffectiveFromDate(DateTime)
 * @see #afterEffectiveFromDate(DateTime)
 * @see #beforeEffectiveToDate(DateTime)
 * @see #afterEffectiveToDate(DateTime)
 * 
 */
public class ContactFilter<T extends Contact> {

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
     * Set to BusinessEntity to filter on.  Can be called multiple times to filter by multiple BusinessEntity.
     * 
     * @param duns
     * @return
     */
    public ContactFilter<T> byBusinessEntity(String duns) {
    	businessEntity.add(duns);
        return this;
    }

    /**
     * Set to businessEntityName to filter on.  Can be called multiple times to filter by multiple businessEntityName.
     * 
     * @param name
     * @return
     */
    public ContactFilter<T> byBusinessEntityName(String name) {
    	businessEntityName.add(name);
        return this;
    }
    
    /**
     * Set to ContactName to filter on.  Can be called multiple times to filter by multiple ContactName
     * 
     * @param contactName
     * @return
     */
    public ContactFilter<T> byContactName(String name) {
    	contactName.add(name);
        return this;
    }

    /**
     * Set to a Before EffectiveFromDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public ContactFilter<T> beforeEffectiveFromDate(DateTime date) {
    	beforeEffectiveFromDate = date;
        return this;
    }

    /**
     * Set to a After EffectiveFromDate to filter on.  Only one after date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public ContactFilter<T> afterEffectiveFromDate(DateTime date) {
    	afterEffectiveFromDate = date;
        return this;
    }
    
    /**
     * Set to a Before EffectiveToDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public ContactFilter<T> beforeEffectiveToDate(DateTime date) {
    	beforeEffectiveToDate = date;
        return this;
    }

    /**
     * Set to a After EffectiveToDate to filter on.  Only one after date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public ContactFilter<T> afterEffectiveToDate(DateTime date) {
    	afterEffectiveToDate = date;
        return this;
    }
 

    
    
    // ===========================================
    // Predicates
    // ===========================================
    private Predicate<T> getPredicate() {
        List<Predicate<T>> predicates = new ArrayList<>();

        if (!businessEntity.isEmpty())
            predicates.add(getBusinessEntityPredicate());
        if (!businessEntityName.isEmpty())
            predicates.add(getBusinessEntityNamePredicate());
        if (!contactName.isEmpty())
            predicates.add(getContactNamePredicate());

        
        if ( (beforeEffectiveFromDate != null) && (afterEffectiveFromDate != null) ) {
        	predicates.add(getBetweenEffectiveFromDate());
        } else	{
            if (beforeEffectiveFromDate != null)
                predicates.add(getBeforeEffectiveFromDate());
            if (afterEffectiveFromDate != null)
                predicates.add(getAfterEffectiveFromDate());
        }

        if ( (beforeEffectiveToDate != null) && (afterEffectiveToDate != null) ) {
        	predicates.add(getBetweenEffectiveToDate());
        } else	{
            if (beforeEffectiveToDate != null)
                predicates.add(getBeforeEffectiveToDate());
            if (afterEffectiveToDate != null)
                predicates.add(getAfterEffectiveToDate());
        }
       
        
        if (predicates.isEmpty())
            return null;
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        Predicate<T> anded = Predicates.and(predicates);
        return anded;
    }

    private final List<String> businessEntity           		= new ArrayList<String>();
    private final List<String> businessEntityName       		= new ArrayList<String>();
    private final List<String> contactName    		    		= new ArrayList<String>();
    
    private DateTime beforeEffectiveFromDate					= null;
    private DateTime afterEffectiveFromDate						= null;
    private DateTime beforeEffectiveToDate						= null;
    private DateTime afterEffectiveToDate						= null;
    
  
    
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

    private Predicate<T> getBusinessEntityNamePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereBusinessName()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = businessEntityName.toArray(new String[businessEntityName.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getContactNamePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereContactName()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = contactName.toArray(new String[contactName.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }
    

    
    private Predicate<T> getBeforeEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereEffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.lessThan(beforeEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getAfterEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereEffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.greaterThan(afterEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBetweenEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereEffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.closed(afterEffectiveFromDate, beforeEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getBeforeEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereEffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.lessThan(beforeEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getAfterEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereEffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.greaterThan(afterEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBetweenEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereEffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.closed(afterEffectiveToDate, beforeEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }
    
    

    
}


