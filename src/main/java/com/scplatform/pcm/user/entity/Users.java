/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.user.entity;


import jakarta.persistence.*;
import lombok.*;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.contact.entity.Contact;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.scplatform.pcm.commodityProfile.entity.CommodityProfile;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.util.I18NUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.scplatform.pcm.util.common.SCPlatformConstant.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "PCM_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements Serializable
{
    public static final Users SYSTEM_USER = new Users();

    static {
        SYSTEM_USER.setUserId("System");
        SYSTEM_USER.setUserName("System");
    }

    @Id
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "PCM_USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @Column(name = "USER_KEY")
    private Long userKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_KEY")
    private Role role;

    @Column(name = "USER_ID", length = 60, nullable = false)
    private String userId;

    @Column(name = "USER_NAME", length = 50)
    private String userName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTACT_KEY")
    private Contact contact;
    
    @Column(name = "EMAIL_ADDRESS", length = 255)
    private String emailAddress;
    
    @Column(name = "FOREIGN_ID", length = 255)
    private String foreignId;

    @Column(name = "ENABLED_FLAG", length = 1)
    private boolean isEnabled = true;

    @Column(name = "USER_PASSWORD", length = 255)
    private String password;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUSINESS_ENTITY_KEY")
    private BusinessEntity businessEntity;
    
    @Column(name = "LAST_ACCESS_DATE", insertable = false, updatable = false)
    private Timestamp lastAccessDate;
    
    @Column(name = "FAVORITES")
    private String favorites;

    /**
     * User preferences stored in PCM_USER_PREFS table
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "PCM_USER_PREFS",
            joinColumns = @JoinColumn(name = "USER_KEY")
    )
    @MapKeyColumn(name = "PREF_NAME", length = 50)
    @Column(name = "PREF_VALUE", length = 512)
    private final Map<String,String> preferences = new HashMap<>();

    @OneToMany(mappedBy = "delegator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserDelegate> delegates = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "USER_COMMODITYPROFILE_MAPPING",
        joinColumns = @JoinColumn(name = "USER_KEY"),
        inverseJoinColumns = @JoinColumn(name = "PROFILE_NAME", referencedColumnName = "PROFILE_NAME")
    )
    private Set<CommodityProfile> userProfileMapping = new HashSet<>();

    // Additional constructors
    /** constructor with id */
    public Users(Long key)
    {
        this.userKey = key;
    }

    /** constructor with id and userprofilemapping */
    public Users(Long userKey, Set<CommodityProfile> userProfileMapping) {
        this.userKey = userKey;
        this.userProfileMapping = userProfileMapping;
    }

    // Legacy getter for getIsEnabled (non-standard naming)
    public boolean getIsEnabled()
    {
        return isEnabled;
    }

    // Legacy setter for setIsEnabled (non-standard naming)
    public void setIsEnabled(boolean enabled)
    {
        this.isEnabled = enabled;
    }


    public void setPreference(String pref, String value)
    {
        if (StringUtils.isBlank(value))
        {
            preferences.remove(pref);
        }
        else
        {
            preferences.put(pref,value);
        }
    }

    public void setPreferenceAsArray(String pref, String[] array)
    {
        String value = null;
        if (array != null && array.length > 0)
        {
            for (int idx=0; idx < array.length; idx++)
            {
                value = (value == null) ? array[idx]:","+array[idx];
            }
        }
        setPreference(pref,value);
    }

    /**
     * Returns the users preferences
     * NOTE: Care should be taken when using this, as no checks
     * are performed on the validity of the data.
     * @return
     */
    public Map<String,String> getPreferences()
    {
        return preferences; 
    }

    public String getPreference(String pref)
    {
        return preferences.get(pref);
    }

    public String[] getPreferenceAsArray(String pref)
    {
        String value = getPreference(pref);
        if (value != null)
        {
            return value.split(",");
        }
        return null;
    }

    public int getDefaultPageSize()
    {
        String size = getPreference(PAGE_SIZE);
        try
        {
            return Integer.parseInt(size);
        }
        catch(Exception e)
        {
            return 10;
        }
    }

    public void setDefaultPageSize(int i)
    {
        setPreference(PAGE_SIZE,String.valueOf(i));
    }

    public Locale getPreferedLocale()
    {
        String lang = getPreference(DEFAULT_LANG);
        String cty = getPreference(DEFAULT_COUNTRY);
        Locale l = I18NUtils.findLocale(lang,cty);
        if (l == null)
        {
            // If we cannot find a local with both lang and country, try just the lang
            l = I18NUtils.findLocale(lang,null);
        }
        return l;
    }

    public void setPreferedLocale(Locale locale)
    {
        setPreference(DEFAULT_LANG,locale.getISO3Language());
        setPreference(DEFAULT_COUNTRY,locale.getISO3Country());
    }

    public void setPreferedLocale(String lang, String country)
    {
        setPreference(DEFAULT_LANG,lang);
        setPreference(DEFAULT_COUNTRY,country);
    }

    public void addDelegate(UserDelegate da)
    {
        da.setDelegator(this);
        delegates.add(da);
    }

    public Set<UserDelegate> getDelegates()
    {
        return delegates;
    }

    public void setDelegates(Set<UserDelegate> delegates)
    {
        this.delegates = delegates;
    }

    public Set<CommodityProfile> getUserProfileMapping() {
        return userProfileMapping;
    }

    public void setUserProfileMapping(Set<CommodityProfile> userProfileMapping) {
        this.userProfileMapping = userProfileMapping;
    }

    public void addUserProfileMapping(CommodityProfile commodityProfile)
    {
        boolean flag = false;
        for(CommodityProfile cProfile : this.userProfileMapping){
            if(cProfile.getProfileName().equals(commodityProfile.getProfileName())){
                flag = true;
                break;
            }
        }

        if(!flag){
            this.userProfileMapping.add(commodityProfile);
        }
    }

    @Override
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof Users))
            return false;
        Users castOther = (Users) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getUserId(),castOther.getUserId());
        eb.append(this.getBusinessEntity(),castOther.getBusinessEntity());

        return eb.isEquals();
    }

    @Override
    public int hashCode()
    {
        int result = new HashCodeBuilder(17, 37).
                append(this.getUserId()).
                toHashCode();
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getUserId());
        sb.append("[Name=").append(this.getUserName());
        sb.append(", EMail=").append(this.getEmailAddress());
        sb.append(", Enabled=").append(this.getIsEnabled());
        sb.append(", BusinessEntity=[").append(this.getBusinessEntity()).append("]");
        sb.append("]");
        return sb.toString();
    }


}
