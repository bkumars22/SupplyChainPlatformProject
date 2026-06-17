/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.entity;

import com.scplatform.pcm.common.converter.BooleanToTFConverter;
import com.scplatform.pcm.user.entity.Users;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;
/**
 * Entity representing a saved search filter configuration.
 * 
 * Stores user-defined search filters that can be reused to refine searches
 * across various modules in the PCM application.
 * 
 * Mapped to PCM_SEARCH_FILTER table with sequence PCM_SEARCH_FILTER_SEQ
 * 
 * @author PCM Team
 */
@Entity
@Table(name = "PCM_SEARCH_FILTER")
public class SearchFilter
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_search_filter_seq")
    @SequenceGenerator(name = "pcm_search_filter_seq", sequenceName = "PCM_SEARCH_FILTER_SEQ", allocationSize = 1)
    @Column(name = "SEARCH_FILTER_KEY")
    private Long filterKey;

    @Column(name = "SEARCH_FILTER_NAME", length = 55, nullable = false)
    private String name;

    @Column(name = "IS_PUBLIC", length = 1, nullable = false)
    @Convert(converter = BooleanToTFConverter.class)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATOR_USER_KEY", nullable = false)
    private Users creator;

    @Column(name = "FILTER_TYPE", length = 128, nullable = false)
    private String filterType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "PCM_SEARCH_FILTER_VALUE",
            joinColumns = @JoinColumn(name = "SEARCH_FILTER_KEY", nullable = false)
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SearchFilterValue> filterValues = new HashSet<>();

    public SearchFilter()
    {

    }

    public SearchFilter(Long key)
    {
        this.setFilterKey(key);
    }
    /**
     * @param filterKey The filterKey to set.
     */
    public void setFilterKey(Long filterKey)
    {
        this.filterKey = filterKey;
    }
    /**
     * @return Returns the filterKey.
     */
    public Long getFilterKey()
    {
        return filterKey;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param isPublic The isPublic to set.
     */
    public void setIsPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }
    /**
     * @return Returns the isPublic.
     */
    public boolean getIsPublic()
    {
        return isPublic;
    }
    /**
     * @param creator The creator to set.
     */
    public void setCreator(Users creator)
    {
        this.creator = creator;
    }
    /**
     * @return Returns the creator.
     */
    public Users getCreator()
    {
        return creator;
    }
    /**
     * @param filterType The filterType to set.
     */
    public void setFilterType(String filterType)
    {
        this.filterType = filterType;
    }
    /**
     * @return Returns the filterType.
     */
    public String getFilterType()
    {
        return filterType;
    }

    public void clearFilterValues()
    {
        filterValues.clear();
    }

    /**
     * Add a filter to map
     */
    public void addFilterValue(String fieldName, Object value)
    {
        if (value instanceof Object[])
        {
            Object[] list = (Object[])value;
            for (int idx=0;idx < list.length; idx++)
            {
                if (list[idx] != null)
                {
                    if (list[idx] instanceof String && StringUtils.isEmpty((String)list[idx]))
                    {
                        continue;
                    }
                    filterValues.add(new SearchFilterValue(fieldName,list[idx]));
                }
            }
        }
        else
        {
            filterValues.add(new SearchFilterValue(fieldName,value));
        }
    }
    /**
     * @param filterValues The filterValues to set.
     */
    public void setFilterValues(Set<SearchFilterValue> filterValues)
    {
        this.filterValues = filterValues;
    }
    /**
     * @return Returns the filterValues.
     */
    public Set<SearchFilterValue> getFilterValues()
    {
        return filterValues;
    }

    /**
     * Returns a map of the raw values where the key is the field name, and value is
     * a List of values
     *
     * @return map
     */
    public Map<String, List<Object>> getFilterValueMap()
    {
        Map<String, List<Object>> result = new HashMap<>();
        for (SearchFilterValue sfv : filterValues)
        {
            List<Object> values = result.get(sfv.getFieldName());
            if (values == null)
            {
                values = new ArrayList<>();
                result.put(sfv.getFieldName(), values);
            }
            values.add(sfv.getFieldValue());
        }
        return result;
    }
}
