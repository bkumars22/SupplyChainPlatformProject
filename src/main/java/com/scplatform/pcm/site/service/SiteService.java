/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.site.service;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.repository.SiteRepository;
import com.scplatform.pcm.util.common.SCPlatformConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class SiteService {
	private static final String SITE_TYPE_CONFIG_KEY = "pcm.item.assignment.responsibility.siteType";

	private final SiteRepository siteRepository;
	private final PcmConfigUtil pcmConfigUtil;
	private final BusinessEntityService businessEntityService;


	public Site getSite(Long siteKey) {
		return siteRepository.findById(siteKey).orElse(null);
	}


	public List<Site> findSitesForBusiness(BusinessEntity businessEntity) {
		return siteRepository.findSitesForBusiness(businessEntity);
	}


	public List<Site> findSitesForBusinessType(Long businessEntityTypeKey) {
		if (businessEntityTypeKey == null) {
			return Collections.emptyList();
		}
		return siteRepository.findSitesForBusinessType(businessEntityTypeKey);
	}


	public Site findSiteByNaturalKey(BusinessEntity businessEntity, String siteName) {
		return siteRepository.findSiteByNaturalKey(businessEntity, siteName).orElse(null);
	}


	public Site findSiteByNaturalKey(String siteName, List<String> excludeTypes) {
		if (siteName == null) {
			return null;
		}
		// Filter by ENTERPRISE type and DESTINATION purpose
		List<Site> allSites = siteRepository.getAllSites();
		for (Site site : allSites) {
			if (site.getSiteName() != null && site.getSiteName().equals(siteName) &&
				site.getBusinessEntity() != null && 
				site.getBusinessEntity().getBusinessEntityTypeKey() == BusinessEntity.ENTERPRISE_TYPE &&
				(excludeTypes == null || !excludeTypes.contains(site.getSiteType()))) {
				return site;
			}
		}
		return null;
	}


	public Site findSiteByDescription(String siteDescription, List<String> excludeTypes) {
		if (siteDescription == null) {
			return null;
		}
		// Filter by ENTERPRISE type and DESTINATION purpose
		List<Site> allSites = siteRepository.getAllSites();
		for (Site site : allSites) {
			if (site.getSiteDescription() != null && site.getSiteDescription().equals(siteDescription) &&
				site.getBusinessEntity() != null && 
				site.getBusinessEntity().getBusinessEntityTypeKey() == BusinessEntity.ENTERPRISE_TYPE &&
				(excludeTypes == null || !excludeTypes.contains(site.getSiteType()))) {
				return site;
			}
		}
		return null;
	}


	public Site findSiteByDescription(String siteDescription) {
		if (siteDescription == null) {
			return null;
		}
		List<Site> allSites = siteRepository.getAllSites();
		for (Site site : allSites) {
			if (site.getSiteDescription() != null && 
				site.getSiteDescription().equals(siteDescription) &&
				(site.getSiteType().equals(Site.REGION_TYPE) || 
				 site.getSiteType().equals(Site.GLOBAL_TYPE) || 
				 site.getSiteType().equals(Site.SITE_TYPE))) {
				return site;
			}
		}
		return null;
	}

	public List<Site> findSitesForEnterprise() {
		return findSitesForBusinessType(BusinessEntity.ENTERPRISE_TYPE);
	}

	public List<Site> findSitesForEnterprise(String siteType) {
		List<Site> enterpriseSites = findSitesForBusinessType(BusinessEntity.ENTERPRISE_TYPE);
		if (siteType == null) {
			return enterpriseSites;
		}
		List<Site> filtered = new ArrayList<>();
		for (Site site : enterpriseSites) {
			if (siteType.equals(site.getSiteType())) {
				filtered.add(site);
			}
		}
		return filtered;
	}

	public List<Site> findRegion() {
		return siteRepository.findRegion();
	}

	public List<Site> findSiteForRegionList() {
		return findSiteForRegionList(false);
	}

	public List<Site> findSiteForRegionList(boolean hideByCostVisibleFlag) {
		List<String> siteTypes = pcmConfigUtil.getList(SITE_TYPE_CONFIG_KEY);
		if (siteTypes == null || siteTypes.isEmpty()) {
			return Collections.emptyList();
		}
		return siteRepository.findSiteForRegionList(siteTypes, hideByCostVisibleFlag);
	}

	public String getSiteDescriptionByKey(String[] keys) {
		List<Long> keyList = new ArrayList<>();
		for (String key : keys) {
			keyList.add(Long.parseLong(key));
		}

		List<String> descriptions = siteRepository.findSiteDescriptionsBySiteKeys(keyList);
		String result = descriptions.toString();
		return result.concat("-").substring(1, result.length() - 1);
	}

	public List<Site> getAllSites() {
		return siteRepository.getAllSites();
	}
	public List<Site> findSitesForBusiness(BusinessEntity businessEntity, List<String> siteTypes) {
		if (businessEntity == null) {
			return Collections.emptyList();
		}
		if (siteTypes == null || siteTypes.isEmpty()) {
			return siteRepository.findSitesForBusiness(businessEntity);
		}
		return siteRepository.findSitesForBusinessWithTypes(businessEntity, siteTypes);
	}


	public List<Site> findForecastSites(String forecastType) {
		if (forecastType == null || forecastType.isBlank()) {
			return Collections.emptyList();
		}
		BusinessEntity enterprise =
				businessEntityService.getEnterpriseBusinessEntity(BusinessEntity.ENTERPRISE_TYPE);
		if (enterprise == null) {
			return Collections.emptyList();
		}
		List<String> siteTypes = resolveForecastSiteTypes(forecastType);
		return findSitesForBusiness(enterprise, siteTypes);
	}

	private List<String> resolveForecastSiteTypes(String forecastType) {
		String key = "pcm.forecast." + forecastType + ".siteTypesAllowed";
		String csv = pcmConfigUtil.getString(key, "");
		if (csv == null || csv.isBlank()) {
			return Collections.emptyList();
		}
		String[] parts = csv.split(",");
		List<String> result = new ArrayList<>(parts.length);
		for (String p : parts) {
			String t = p.trim();
			if (!t.isEmpty()) {
				result.add(t);
			}
		}
		return result;
	}

	public List<Site> findSitesForBusiness(BusinessEntity businessEntity, List<String> siteTypes,
			boolean hideSiteWithCostingFlag) {
		if (businessEntity == null) {
			return Collections.emptyList();
		}
		List<Site> sites = findSitesForBusiness(businessEntity, siteTypes);
		if (!hideSiteWithCostingFlag) {
			return sites;
		}
		// Filter by cost visible flag
		List<Site> filtered = new ArrayList<>();
		for (Site site : sites) {
			if (site.getSiteDetail() != null && site.getSiteDetail().getCostVisibleFlag()) {
				filtered.add(site);
			}
		}
		return filtered;
	}
	public List<Site> findSites(Long[] businessTypes, String[] siteTypes) {
		if (siteTypes == null || siteTypes.length == 0) {
			return Collections.emptyList();
		}
		List<String> siteTypeList = Arrays.asList(siteTypes);
		if (businessTypes == null || businessTypes.length == 0) {
			return siteRepository.findSites(null, siteTypeList);
		}
		List<Long> businessTypeList = Arrays.asList(businessTypes);
		return siteRepository.findSites(businessTypeList, siteTypeList);
	}
	public List<Site> findSitesByBusinessEntitiesKey(Set<Long> businessEntityKeys, String[] siteTypes) {
		if (siteTypes == null || siteTypes.length == 0) {
			return Collections.emptyList();
		}
		if (businessEntityKeys == null || businessEntityKeys.isEmpty()) {
			return Collections.emptyList();
		}
		return siteRepository.findSitesByBusinessEntitiesKey(new ArrayList<>(businessEntityKeys), Arrays.asList(siteTypes));
	}


	public List<Site> findSitesForListInitializer(
			com.scplatform.pcm.role.entity.Role role,
			Long activeBeKey,
			Long enterpriseBeKey,
			String[] siteTypes,
			Long[] businessTypes) {

		if (role != null && SCPlatformConstant.ADMIN_TYPE.equalsIgnoreCase(role.getRoleId())) {
			return findSites(businessTypes, siteTypes);
		}

		Set<Long> businessEntityKeys = new java.util.HashSet<>();
		if (activeBeKey != null && activeBeKey > 0) {
			businessEntityKeys.add(activeBeKey);
		}
		if (enterpriseBeKey != null) {
			businessEntityKeys.add(enterpriseBeKey);
		} else {
			businessEntityKeys.add(11L);
		}
		return findSitesByBusinessEntitiesKey(businessEntityKeys, siteTypes);
	}

	public List<Site> findSitesByRegion(String region) {
		if (region == null) {
			return Collections.emptyList();
		}
		return siteRepository.findSitesByRegion(region);
	}

	public List<Site> findSitesByRegionXlob(String region) {
		if (region == null) {
			return Collections.emptyList();
		}
		return siteRepository.findSitesByRegionXlob(region);
	}


	public Site findCCNSiteByDescription(String siteDescription) {
		if (siteDescription == null) {
			return null;
		}
		return siteRepository.findCCNSiteByDescription(siteDescription).orElse(null);
	}


	public List<Site> getEnterpriseRegionList() {
		return siteRepository.getEnterpriseRegionList(BusinessEntity.ENTERPRISE_TYPE);
	}


	public List<Site> getEnterpriseRegionListXlob() {
		return siteRepository.getEnterpriseRegionListXlob(BusinessEntity.ENTERPRISE_TYPE);
	}


	public Site findTopSiteForBusiness(BusinessEntity businessEntity, String siteType) {
		List<Site> sites = siteRepository.findSitesForBusiness(businessEntity);
		for (Site site : sites) {
			if (site.getParentSite() == null && (siteType == null || siteType.equals(site.getSiteType()))) {
				return site;
			}
		}
		return null;
	}
}
