/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.accessControl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;

@SuppressWarnings("serial")
@Entity
@Table(name = "PCM_ACCESS_CONTROL")
public class AccessControl implements java.io.Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_acl_seq")
    @SequenceGenerator(name = "pcm_acl_seq", sequenceName = "PCM_ACL_SEQ", allocationSize = 1)
    @Column(name = "ACCESS_CONTROL_KEY")
    private Long accessControlKey;

    @ManyToOne
    @JoinColumn(name = "ROLE_KEY")
    private Role role;

    @Column(name = "ENTITY_TYPE", length = 40, nullable = false)
    private String entityType;

    @Column(name = "TARGET_ENTITY_KEY", length = 36)
    private String entityKey;

    @ManyToOne
    @JoinColumn(name = "USER_KEY")
    private Users user;

    @Column(name = "ACL", length = 50, nullable = false)
    private String acl;

    // Constructors

    /** default constructor */
    public AccessControl()
    {
    }

    /** constructor with id */
    public AccessControl(Long key)
    {
        accessControlKey = key;
    }


    /**
     * @param accessControlKey The accessControlKey to set.
     */
    public void setAccessControlKey(Long accessControlKey)
    {
        this.accessControlKey = accessControlKey;
    }

    /**
     * @return Returns the accessControlKey.
     */
    public Long getAccessControlKey()
    {
        return accessControlKey;
    }

    public Role getRole()
    {
        return this.role;
    }

    public void setRole(Role Role)
    {
        this.role = Role;
    }

    /**
     */
    public String getEntityKey()
    {
        return this.entityKey;
    }

    public void setEntityKey(String EntityKey)
    {
        this.entityKey = EntityKey;
    }
    
    /**
     * @param entityType The entityType to set.
     */
    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    /**
     * @return Returns the entityType.
     */
    public String getEntityType()
    {
        return entityType;
    }
    
    public void setUser(Users user)
	{
		this.user = user;
	}

	public Users getUser()
	{
		return user;
	}

	/**
     */
    public String getAcl()
    {
        return this.acl;
    }

    public void setAcl(String Acl)
    {
        this.acl = Acl;
    }

    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof AccessControl))
            return false;
        AccessControl castOther = (AccessControl) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getEntityType(),castOther.getEntityType());
        eb.append(this.getEntityKey(),castOther.getEntityKey());
        eb.append(this.getRole(),castOther.getRole());
        eb.append(this.getUser(),castOther.getUser());
        eb.append(this.getAcl(),castOther.getAcl());
        return eb.isEquals();
    }
    
    public int hashCode()
    {
        int result = new HashCodeBuilder(17, 37).
        append(this.getEntityType()).
        append(this.getAcl()).        
        toHashCode();
        return result;
    }
    
    public String toString()
    {
       	StringBuilder sb = new StringBuilder();       
       	sb.append(this.getRole());
       	if (this.getUser() != null)
       	{
       		sb.append(" User=").append(this.getUser());
       	}
       	sb.append("[Acl=").append(this.getAcl());
   		sb.append(", EntityType=").append(this.getEntityType());
   		sb.append(", EntityKey=").append(this.getEntityKey());   		
       	sb.append("]");
       	return sb.toString();    	    	
    }
}
