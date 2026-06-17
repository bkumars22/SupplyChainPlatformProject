/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.entity;

import com.scplatform.pcm.role.entity.Role;

import jakarta.persistence.*;

import java.io.Serializable;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;

@Entity
@Table(name = "ROLE_COMMODITY_PROFILE_MAPPING")
@IdClass(RoleCommodityProfileMappingId.class)
public class RoleCommodityProfileMapping implements Serializable {

    @Id
    @JoinColumn(name = "ROLE_KEY", referencedColumnName = "ROLE_KEY", nullable = false)
    @ManyToOne
    private Role role;

    @Id
    @JoinColumn(name = "PROFILE_ID", referencedColumnName = "PROFILE_ID", nullable = false)
    @ManyToOne
    private CommodityProfile commodityProfile;

    @Id
    @JoinColumn(name = "BUSINESS_ENTITY_KEY", referencedColumnName = "BUSINESS_ENTITY_KEY", nullable = false)
    @ManyToOne
    private BusinessEntity businessEntity;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public CommodityProfile getCommodityProfile() {
        return commodityProfile;
    }

    public void setCommodityProfile(CommodityProfile commodityProfile) {
        this.commodityProfile = commodityProfile;
    }

    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RoleCommodityProfileMapping)) return false;

        RoleCommodityProfileMapping that = (RoleCommodityProfileMapping) o;
        return role.getRoleKey().equals(that.role.getRoleKey()) && commodityProfile.getProfileId().equals(that.commodityProfile.getProfileId())
                && businessEntity.getBusinessEntityKey().equals(that.businessEntity.getBusinessEntityKey());
    }

    @Override
    public int hashCode() {
        int result =  role != null && role.getRoleKey() != null ? role.getRoleKey().hashCode() :  1;
        result = 31 * result + commodityProfile.getProfileId().hashCode();
        result = 31 * result + businessEntity.getBusinessEntityKey().hashCode();
        return result;
    }
}