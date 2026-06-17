/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.businessEntity;

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
 * Filters {@link BusinessEntity} Messages
 * 
 * Chained Call Example
 * <pre>
 * BusinessEntityFilter filter = new BusinessEntityFilter();
 * Iterable<T> data = filter.bySite("site1").apply(businessEntityData);
 * </pre>
 * 
 * @author dgenrich
 * 
 * @see #byBusinessEntity(String)
 * @see #byBusinessEntityName(String)
 * @see #byBusinessEntityType(String)
 * @see #bySite(String)
 * @see #bySiteType(String)
 * @see #byAlternatesBusinessEntityName(String)
 * @see #beforeEffectiveFromDate(DateTime)
 * @see #afterEffectiveFromDate(DateTime)
 * @see #beforeEffectiveToDate(DateTime)
 * @see #afterEffectiveToDate(DateTime)
 * @see #beforeSiteEffectiveFromDate(DateTime)
 * @see #afterSiteEffectiveFromDate(DateTime)
 * @see #beforeSiteEffectiveToDate(DateTime)
 * @see #afterSiteEffectiveToDate(DateTime)
 * @see #beforeAlternatesEffectiveFromDate(DateTime)
 * @see #afterAlternatesEffectiveFromDate(DateTime)
 * @see #beforeAlternatesEffectiveToDate(DateTime)
 * @see #afterAlternatesEffectiveToDate(DateTime)
 * 
 */
public class BusinessEntityFilter<T extends BusinessEntity> {

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
    public BusinessEntityFilter<T> byBusinessEntity(String duns) {
    	businessEntity.add(duns);
        return this;
    }

    /**
     * Set to BusinessEntityName to filter on.  Can be called multiple times to filter by multiple BusinessEntityName.
     * 
     * @param name
     * @return
     */
    public BusinessEntityFilter<T> byBusinessEntityName(String name) {
    	businessEntityName.add(name);
        return this;
    }
    
    /**
     * Set to BusinessEntityType to filter on.  Can be called multiple times to filter by multiple BusinessEntityType.
     * 
     * @param name
     * @return
     */
    public BusinessEntityFilter<T> byBusinessEntityType(String type) {
    	businessEntityType.add(type);
        return this;
    }
    
    /**
     * Set to Site to filter on.  Can be called multiple times to filter by multiple sites
     * 
     * @param site
     * @return
     */
    public BusinessEntityFilter<T> bySite(String site) {
    	site_names.add(site);
        return this;
    }

    /**
     * Set to SiteType to filter on.  Can be called multiple times to filter by multiple types
     * 
     * @param siteType
     * @return
     */
    public BusinessEntityFilter<T> bySiteType(String siteType) {
    	site_type.add(siteType);
        return this;
    }
    
    /**
     * Set to Alternates BusinessEntityName to filter on.  Can be called multiple times to filter by multiple Alternates BusinessEntityName.
     * 
     * @param name
     * @return
     */
    public BusinessEntityFilter<T> byAlternatesBusinessEntityName(String name) {
    	alternates.add(name);
        return this;
    }
    
    /**
     * Set to a Before EffectiveFromDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> beforeEffectiveFromDate(DateTime date) {
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
    public BusinessEntityFilter<T> afterEffectiveFromDate(DateTime date) {
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
    public BusinessEntityFilter<T> beforeEffectiveToDate(DateTime date) {
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
    public BusinessEntityFilter<T> afterEffectiveToDate(DateTime date) {
    	afterEffectiveToDate = date;
        return this;
    }
 
    
    /**
     * Set to a Before Site EffectiveFromDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> beforeSiteEffectiveFromDate(DateTime date) {
    	beforeSiteEffectiveFromDate = date;
        return this;
    }

    /**
     * Set to a After Site EffectiveFromDate to filter on.  Only one after date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> afterSiteEffectiveFromDate(DateTime date) {
    	afterSiteEffectiveFromDate = date;
        return this;
    }
    
    /**
     * Set to a Before Site EffectiveToDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> beforeSiteEffectiveToDate(DateTime date) {
    	beforeSiteEffectiveToDate = date;
        return this;
    }

    /**
     * Set to a After Site EffectiveToDate to filter on.  Only one after date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> afterSiteEffectiveToDate(DateTime date) {
    	afterSiteEffectiveToDate = date;
        return this;
    }
    
    
    /**
     * Set to a Before Alternates EffectiveFromDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> beforeAlternatesEffectiveFromDate(DateTime date) {
    	beforeAlternatesEffectiveFromDate = date;
        return this;
    }

    /**
     * Set to a After Alternates EffectiveFromDate to filter on.  Only one after date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> afterAlternatesEffectiveFromDate(DateTime date) {
    	afterAlternatesEffectiveFromDate = date;
        return this;
    }
    
    /**
     * Set to a Before Alternates EffectiveToDate to filter on.  Only one before date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> beforeAlternatesEffectiveToDate(DateTime date) {
    	beforeAlternatesEffectiveToDate = date;
        return this;
    }

    /**
     * Set to a After Alternates EffectiveToDate to filter on.  Only one after date can be set.  
     * If called multiple times, only the last one is used.
     * 
     * @param date
     * @return
     */
    public BusinessEntityFilter<T> afterAlternatesEffectiveToDate(DateTime date) {
    	afterAlternatesEffectiveToDate = date;
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
        if (!businessEntityType.isEmpty())
            predicates.add(getBusinessEntityTypePredicate());
        if (!site_names.isEmpty())
            predicates.add(getSiteNamePredicate());
        if (!site_type.isEmpty())
            predicates.add(getSiteTypePredicate());
        if (!alternates.isEmpty())
            predicates.add(getAlternatesPredicate());
        
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

        if ( (beforeSiteEffectiveFromDate != null) && (afterSiteEffectiveFromDate != null) ) {
        	predicates.add(getBetweenSiteEffectiveFromDate());
        } else	{
            if (beforeSiteEffectiveFromDate != null)
                predicates.add(getBeforeSiteEffectiveFromDate());
            if (afterSiteEffectiveFromDate != null)
                predicates.add(getAfterSiteEffectiveFromDate());
        }        

        if ( (beforeSiteEffectiveToDate != null) && (afterSiteEffectiveToDate != null) ) {
        	predicates.add(getBetweenSiteEffectiveToDate());
        } else	{
            if (beforeSiteEffectiveToDate != null)
                predicates.add(getBeforeSiteEffectiveToDate());
            if (afterSiteEffectiveToDate != null)
                predicates.add(getAfterSiteEffectiveToDate());
        }      
        
        if ( (beforeAlternatesEffectiveFromDate != null) && (afterAlternatesEffectiveFromDate != null) ) {
        	predicates.add(getBetweenAlternatesEffectiveFromDate());
        } else	{
            if (beforeAlternatesEffectiveFromDate != null)
                predicates.add(getBeforeAlternatesEffectiveFromDate());
            if (afterAlternatesEffectiveFromDate != null)
                predicates.add(getAfterAlternatesEffectiveFromDate());
        }        

        if ( (beforeAlternatesEffectiveToDate != null) && (afterAlternatesEffectiveToDate != null) ) {
        	predicates.add(getBetweenAlternatesEffectiveToDate());
        } else	{
            if (beforeAlternatesEffectiveToDate != null)
                predicates.add(getBeforeAlternatesEffectiveToDate());
            if (afterAlternatesEffectiveToDate != null)
                predicates.add(getAfterAlternatesEffectiveToDate());
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
    private final List<String> businessEntityType          		= new ArrayList<String>();
    private final List<String> site_names    		    		= new ArrayList<String>();
    private final List<String> site_type	            		= new ArrayList<String>();
    private final List<String> alternates	            		= new ArrayList<String>();
    
    private DateTime beforeEffectiveFromDate					= null;
    private DateTime afterEffectiveFromDate						= null;
    private DateTime beforeEffectiveToDate						= null;
    private DateTime afterEffectiveToDate						= null;
    
    private DateTime beforeSiteEffectiveFromDate				= null;
    private DateTime afterSiteEffectiveFromDate					= null;
    private DateTime beforeSiteEffectiveToDate					= null;
    private DateTime afterSiteEffectiveToDate					= null;

    private DateTime beforeAlternatesEffectiveFromDate			= null;
    private DateTime afterAlternatesEffectiveFromDate			= null;
    private DateTime beforeAlternatesEffectiveToDate			= null;
    private DateTime afterAlternatesEffectiveToDate				= null;
   
    
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
    			(Predicate<T>) T.Factory.newFieldPredicate().whereBusinessEntityName()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = businessEntityName.toArray(new String[businessEntityName.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBusinessEntityTypePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereBusinessEntityType()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = businessEntityType.toArray(new String[businessEntityType.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getSiteNamePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereSite_Site()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = site_names.toArray(new String[site_names.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getSiteTypePredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereSite_SiteType()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = site_type.toArray(new String[site_type.size()]);
				return containsOneOf(array);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getAlternatesPredicate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = 
    			(Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_AlternateName()
    			.satisfies(new StringCriteria() {
    				
			@Override
			public boolean evaluate() {
				String[] array = alternates.toArray(new String[alternates.size()]);
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
    
    
    
    private Predicate<T> getBeforeSiteEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite_EffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.lessThan(beforeSiteEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getAfterSiteEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite_EffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.greaterThan(afterSiteEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBetweenSiteEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite_EffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.closed(afterSiteEffectiveFromDate, beforeSiteEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getBeforeSiteEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite_EffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.lessThan(beforeSiteEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getAfterSiteEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite_EffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.greaterThan(afterSiteEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBetweenSiteEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereSite_EffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.closed(afterSiteEffectiveToDate, beforeSiteEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }
    
    
    private Predicate<T> getBeforeAlternatesEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_EffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.lessThan(beforeAlternatesEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getAfterAlternatesEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_EffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.greaterThan(afterAlternatesEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBetweenAlternatesEffectiveFromDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_EffectiveFromDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.closed(afterAlternatesEffectiveFromDate, beforeAlternatesEffectiveFromDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }
    
    private Predicate<T> getBeforeAlternatesEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_EffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.lessThan(beforeAlternatesEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getAfterAlternatesEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_EffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.greaterThan(afterAlternatesEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }

    private Predicate<T> getBetweenAlternatesEffectiveToDate() {
    	@SuppressWarnings("unchecked")
    	Predicate<T> predicate = (Predicate<T>) T.Factory.newFieldPredicate().whereAlternates_EffectiveToDate()
    			.satisfies(new DateTimeCriteria() {
			
    		private final Range<DateTime> range = Range.closed(afterAlternatesEffectiveToDate, beforeAlternatesEffectiveToDate);
    				
			@Override
			public boolean evaluate() {	
				return isContainedByRange(range);
			}
		}).build();
    	
    	return predicate;
    }
    
}

