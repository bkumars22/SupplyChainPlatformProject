/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.initializer;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.service.SiteService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class SiteListInitializer implements SearchParameterInitializer {

    private static final String CFG_HIDE_SITE_WITHOUT_COST =
            "pcm.hide.region.without.cost.visible.in.ResponsibilityRegionListInitializer";
    private static final String CFG_LABEL_WITH_BUSINESS = "pcm.region.with.BusinessEntity";
    private final SiteService siteService;
    private final PcmConfigUtil pcmConfigUtil;
    private String[] siteTypes;
    private Long[] businessTypes;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (!(parameter instanceof SearchParameterSelect)) {
            return false;
        }
        SearchParameterSelect list = (SearchParameterSelect) parameter;

        Role role        = (Role) context.get(SearchParameterInitializer.ROLE);
        Long activeBe    = asLong(context.get(SearchParameterInitializer.ACTIVE_BE));
        Long enterprise  = asLong(context.get(SearchParameterInitializer.ENTERPRISE_BE));

        List<Site> sites = siteService.findSitesForListInitializer(
                role, activeBe, enterprise, siteTypes, businessTypes);

        if (sites == null || sites.isEmpty()) {
            return true;
        }

        boolean hideSiteWithCostingFlag = pcmConfigUtil.getBoolean(CFG_HIDE_SITE_WITHOUT_COST, false);
        boolean labelWithBusiness       = pcmConfigUtil.getBoolean(CFG_LABEL_WITH_BUSINESS, false);

        for (Site site : sites) {
            if (hideSiteWithCostingFlag
                    && site.getSiteDetail() != null
                    && site.getSiteDetail().getCostVisibleFlag() != null
                    && !site.getSiteDetail().getCostVisibleFlag()) {
                continue;
            }
            String label = labelWithBusiness
                    ? site.getBusinessEntity().getBusinessEntityIdentifier() + " - " + site.getSiteDescription()
                    : site.getSiteDescription();
            list.addSelectValue(label, String.valueOf(site.getSiteKey()));
        }
        return true;
    }

    @Override
    public void setInitialData(String data) {
        data = StringUtils.trimToNull(data);
        if (data == null) {
            return;
        }
        try {
            JSONObject jo = new JSONObject("{" + data + "}");
            if (jo.has("siteType")) {
                this.siteTypes = asStringArray(jo, "siteType");
            }
            if (jo.has("businessType")) {
                this.businessTypes = asLongArray(jo, "businessType");
            }
        } catch (JSONException e) {
            log.error("Unable to initialize list", e);
        }
    }



    private Long asLong(Object value) {
        return (value instanceof Long) ? (Long) value : null;
    }

    private String[] asStringArray(JSONObject jo, String key) throws JSONException {
        Object value = jo.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            String[] result = new String[array.length()];
            for (int idx = 0; idx < array.length(); idx++) {
                result[idx] = array.getString(idx);
            }
            return result;
        }
        return new String[] { value.toString() };
    }

    private Long[] asLongArray(JSONObject jo, String key) throws JSONException {
        Object value = jo.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            Long[] result = new Long[array.length()];
            for (int idx = 0; idx < array.length(); idx++) {
                result[idx] = array.getLong(idx);
            }
            return result;
        }
        if (value instanceof Number) {
            return new Long[] { ((Number) value).longValue() };
        }
        return null;
    }
}
