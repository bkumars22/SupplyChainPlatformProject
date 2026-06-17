/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.role.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.accessControl.entity.AccessControl;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.entity.YesNoConverter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.scplatform.pcm.commodityProfile.entity.RoleCommodityProfileMapping;

/**
 * Models a role
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "PCM_ROLE")
public class Role implements java.io.Serializable
{
	public static final String USE_IN_ASSIGNMENT_PREF = "ASSIGNMENT";
    // Fields

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_role_seq")
    @SequenceGenerator(name = "pcm_role_seq", sequenceName = "PCM_ROLE_SEQ", allocationSize = 1)
    @Column(name = "ROLE_KEY")
    protected Long roleKey;

    @Column(name = "ROLE_ID", length = 50, nullable = false)
    protected String roleId;

    @Column(name = "ROLE_NAME", length = 50, nullable = false)
    protected String roleName;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "PERM_ROLE", length = 1, nullable = false)
    protected boolean permRole;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AccessControl> acls = new HashSet<AccessControl>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PCM_ROLE_PREFS", joinColumns = @JoinColumn(name = "ROLE_KEY", nullable = false))
    @MapKeyColumn(name = "PREF_NAME", length = 50)
    @Column(name = "PREF_VALUE", length = 512)
    private Map<String,String> preferences = new HashMap<String,String>();    

    @OneToMany(mappedBy = "role")
    private Set<Users> users  = new HashSet<Users>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RoleCommodityProfileMapping> roleProfileMapping = new HashSet<RoleCommodityProfileMapping>();
    
    public Role()
    {
 
    }

    /** constructor with id */
    public Role(Long RoleKey)
    {
        this.roleKey = RoleKey;
    }

    // Property accessors
    /**
     */
    public Long getRoleKey()
    {
        return roleKey;
    }

    public void setRoleKey(Long key)
    {
        this.roleKey = key;
    }

    public void setRoleId(String roleId)
	{
		this.roleId = roleId;
	}

	public String getRoleId()
	{
		return roleId;
	}

	/**
     */
    public String getRoleName()
    {
        return this.roleName;
    }

    public void setRoleName(String RoleName)
    {
        this.roleName = RoleName;
    }

    /**
     */
    public Set<AccessControl> getAcls()
    {
        return this.acls;
    }

    public void setAcls(Set<AccessControl> acls)
    {
        this.acls = acls;
    }
    public Set<Users> getUsers()
	{
		return users;
	}

	public void setUsers(Set<Users> users)
	{
		this.users = users;
	}

	public void setPermRole(boolean canDelete)
	{
		this.permRole = canDelete;
	}

	public boolean getPermRole()
	{
		return permRole;
	}
    
	public String getPreference(String key)
	{
		return (String)preferences.get(key);
	}
	
	public boolean getPreferenceAsBoolean(String key)
	{
		return BooleanUtils.toBoolean(getPreference(key));
	}
	
	public void setPreference(String key, String value)
	{
		preferences.put(key,value);
	}

	public void setPreference(String key, boolean value)
	{
		preferences.put(key,Boolean.toString(value));
	}
	
	public Set<String> getPreferenceKeys()
	{
		return preferences.keySet();
	}

	public void setPreferences(Map<String,String> preferences)
	{
		this.preferences = preferences;
	}

	public Map<String,String> getPreferences()
	{
		return preferences;
	}

    public Set<RoleCommodityProfileMapping> getRoleProfileMapping() {
        return roleProfileMapping;
    }

    public void setRoleProfileMapping(Set<RoleCommodityProfileMapping> roleProfileMapping) {
        this.roleProfileMapping = roleProfileMapping;
    }

    public void addRoleProfileMapping(RoleCommodityProfileMapping roleCommodityProfileMapping)
    {
        boolean flag = false;
        for(RoleCommodityProfileMapping cProfile : this.roleProfileMapping){
            if(cProfile.equals(roleCommodityProfileMapping)){
                flag = true;
                break;
            }
        }

        if(!flag){
            this.roleProfileMapping.add(roleCommodityProfileMapping);
        }
    }

	public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof Role))
            return false;
        Role castOther = (Role) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getRoleKey(),castOther.getRoleKey());
        return eb.isEquals();
    }
    
    public int hashCode()
    {
        int result = new HashCodeBuilder(17, 37).
        append(this.getRoleKey()).        
        toHashCode();
        return result;
    }
	
    public String toString()
    {
       	StringBuilder sb = new StringBuilder();       
       	sb.append(this.getRoleId());
       	sb.append("[Name=").append(this.getRoleName());
   		sb.append(", Perm=").append(this.getPermRole());   		
       	sb.append("]");
       	return sb.toString();    	    	
    }
	
}
