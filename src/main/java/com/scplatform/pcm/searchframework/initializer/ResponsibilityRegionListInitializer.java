/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResponsibilityRegionListInitializer implements SearchParameterInitializer {

    private final PcmConfigUtil pcmConfigUtil;
    private final SiteService siteService;
	
	protected String defaultType;
	public boolean initializeParameter(SearchParameter parameter, Map context)
	{
		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;

			boolean hideSiteWithCostingFlag = pcmConfigUtil
					.getBoolean("pcm.hide.region.without.cost.visible.in.ResponsibilityRegionListInitializer", false);

			List<Site> regionList = siteService.findSiteForRegionList(hideSiteWithCostingFlag);
			for(Site site : regionList)
			{
				list.addSelectValue(site.getSiteDescription(),String.valueOf(site.getSiteKey()));
			}
			return true;
		}
		else
		{
			return false;
		}
	}

    @Override
    public void setInitialData(String data) {

    }

}
