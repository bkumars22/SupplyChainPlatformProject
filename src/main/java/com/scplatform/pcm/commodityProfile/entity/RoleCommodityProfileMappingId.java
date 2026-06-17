/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.entity;

import java.io.Serializable;
import java.util.Objects;

public class RoleCommodityProfileMappingId implements Serializable {
    private Long role;
    private Long commodityProfile;
    private Long businessEntity;

    public RoleCommodityProfileMappingId() {}

    public RoleCommodityProfileMappingId(Long role, Long commodityProfile, Long businessEntity) {
        this.role = role;
        this.commodityProfile = commodityProfile;
        this.businessEntity = businessEntity;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public Long getCommodityProfile() {
        return commodityProfile;
    }

    public void setCommodityProfile(Long commodityProfile) {
        this.commodityProfile = commodityProfile;
    }

    public Long getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntityKey(Long businessEntity) {
        this.businessEntity = businessEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleCommodityProfileMappingId)) return false;
        RoleCommodityProfileMappingId that = (RoleCommodityProfileMappingId) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(commodityProfile, that.commodityProfile) &&
                Objects.equals(businessEntity, that.businessEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, commodityProfile, businessEntity);
    }
}
