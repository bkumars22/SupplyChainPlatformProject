/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.tam.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;

import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.tam.entity.FunctionalGroupItemAllocation;
import com.scplatform.pcm.tam.entity.FunctionalGroupItemAllocationArchival;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocation;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocationArchival;
import com.scplatform.pcm.tam.entity.TAMAllocation;
import com.scplatform.pcm.tam.entity.TAMAllocationArchival;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.tam.repository.FunctionalGroupItemAllocationRepository;
import com.scplatform.pcm.tam.repository.FunctionalGroupSupplierAllocationRepository;
import com.scplatform.pcm.tam.repository.TAMAllocationArchivalRepository;
import com.scplatform.pcm.tam.repository.TAMAllocationRepository;
import com.scplatform.pcm.util.common.SCPlatformConstant;

@Service
@RequiredArgsConstructor
public class TAMAllocationService {

    private final TAMAllocationRepository tamAllocationRepository;

    private final TAMAllocationArchivalRepository tamAllocationArchivalRepository;

    private final FunctionalGroupSupplierAllocationRepository functionalGroupSupplierAllocationRepository;

    private final FunctionalGroupItemAllocationRepository functionalGroupItemAllocationRepository;

    private final FiscalPeriodService fiscalPeriodService;

    public TAMAllocation getTAMAllocationByFGAndSite(String fgname, String site, Date startDate, Date endDate) {
        return tamAllocationRepository.getTAMAllocationByFGAndSite(fgname, site, startDate, endDate);
    }

    public TAMAllocation getTAMAllocationByFGSiteDescriptionAndSiteType(String fgname, String siteDescription,
            String siteType, Date startDate, Date endDate) {
        return tamAllocationRepository.getTAMAllocationByFGSiteDescriptionAndSiteType(
                fgname, siteDescription, siteType, startDate, endDate);
    }

    public TAMAllocation getTAMAllocationById(Long id, Date startDate, Date endDate) {
        return tamAllocationRepository.getTAMAllocationById(id, startDate, endDate);
    }

    public TAMAllocation getTAMAllocationByFGAndSite(FunctionalGroup fgname, Site site, Date startDate,
            Date endDate) {
        return tamAllocationRepository.getTAMAllocationByFGAndSite(fgname, site, startDate, endDate);
    }

    public Double getTotalSupplierAllocationOnDate(Long tamAllocationId, Date startDate) {
        return functionalGroupSupplierAllocationRepository.getTotalSupplierAllocationOnDate(tamAllocationId, startDate);
    }

    public Boolean isAllocationExist(FunctionalGroup functionalGroup, Item item, Date fromDate) {
        Double result = functionalGroupItemAllocationRepository
                .sumAllocationByFunctionalGroupAndItemFromDate(functionalGroup, item, fromDate);
        return result != null && result > 0.0;
    }

    public Boolean isItemAllocationExitsForTemplate(FunctionalGroup functionalGroup, Site site, Date endDate) {
        Double result = functionalGroupItemAllocationRepository
                .sumAllocationByFunctionalGroupSiteAndSupplierEndDate(functionalGroup, site, endDate);
        return result != null && result > 0.0;
    }

    public Boolean isItemAllocationExits(FunctionalGroup functionalGroup, Site site, Date startDate) {
        Double result = functionalGroupItemAllocationRepository
                .sumItemAllocationByFunctionalGroupSiteAndStartDate(functionalGroup, site, startDate);
        return result != null && result > 0.0;
    }

    public Boolean isItemAllocationExists(FunctionalGroup functionalGroup, Site site, Date startDate, Date endDate) {
        Double result = functionalGroupItemAllocationRepository
                .sumItemAllocationByFunctionalGroupSiteAndDateRange(functionalGroup, site, startDate, endDate);
        return result != null && result > 0.0;
    }

    public Boolean isSupplierAllocationExitsForTemplate(FunctionalGroup functionalGroup, Site site, Date endDate) {
        Double result = functionalGroupSupplierAllocationRepository
                .sumAllocationByFunctionalGroupAndSiteFromEndDate(functionalGroup, site, endDate);
        return result != null && result > 0.0;
    }

    public Boolean isSupplierAllocationExits(FunctionalGroup functionalGroup, Site site, Date startDate) {
        Double result = functionalGroupSupplierAllocationRepository
                .sumAllocationByFunctionalGroupSiteAndStartDate(functionalGroup, site, startDate);
        return result != null && result > 0.0;
    }

    public Boolean isSupplierAllocationExitsForGlobal(FunctionalGroup functionalGroup, Date startDate) {
        Double result = functionalGroupSupplierAllocationRepository
                .sumSupplierAllocationByFunctionalGroupSiteTypeAndStartDate(
                        functionalGroup,
                        Site.GLOBAL_TYPE,
                        startDate);
        return result != null && result > 0.0;
    }

    public Boolean isItemAllocationExitsForGlobal(FunctionalGroup functionalGroup, Date startDate) {
        Double result = functionalGroupItemAllocationRepository
                .sumItemAllocationByFunctionalGroupSiteTypeAndStartDate(
                        functionalGroup,
                        Site.GLOBAL_TYPE,
                        startDate);
        return result != null && result > 0.0;
    }

    public List<Item> getUniqueItemListFromTAM(Long tamId, Date startDate) {
        return tamAllocationRepository.getUniqueItemListFromTAM(tamId, startDate);
    }

    public List<BusinessEntity> getUniqueSupplierListFromTAM(Long tamId, Date startDate) {
        return tamAllocationRepository.getUniqueSupplierListFromTAM(tamId, startDate);
    }

    public void markTAMAsUpdated(Long functionalGroupId) {
        tamAllocationRepository.markTAMAsUpdated(functionalGroupId);
    }

    public void markTAMAsAllAllocationDeleted(Long functionalGroupId) {
        tamAllocationRepository.markTAMAsAllAllocationDeleted(functionalGroupId);
    }

    public int deleteItemAllocationByItemAndFG(Long itemKey, Long functionalGroupId, Date startDate,
            Date endDate) {
        return functionalGroupItemAllocationRepository.deleteItemAllocationByItemAndFG(
                itemKey, functionalGroupId, startDate, endDate);
    }

    public int deleteItemAllocationByItemAndSupplier(BusinessEntity be, Long itemKey, Long functionalGroupId,
            Date startDate, Date endDate) {
        return functionalGroupItemAllocationRepository.deleteItemAllocationByItemAndSupplier(
                be, itemKey, functionalGroupId, startDate, endDate);
    }

    public int removeItemAllocationByItemAndTAM(Long itemKey, Long tamId, Date endDate) {
        return functionalGroupItemAllocationRepository.removeItemAllocationByItemAndTAM(
                itemKey, tamId, endDate);
    }

    public int deleteSupplierAllocationByBusinessEntityAndFG(Set<BusinessEntity> businessEntities,
            Long functionalGroupId, Date startDate, Date endDate) {
        return functionalGroupSupplierAllocationRepository.deleteSupplierAllocationByBusinessEntityAndFG(
                businessEntities, functionalGroupId, endDate);
    }

    public int deleteSupplierAllocationByBusinessEntityAndFGByDatePeriod(Set<BusinessEntity> businessEntities,
            Long functionalGroupId, Date startDate, Date endDate) {
        return functionalGroupSupplierAllocationRepository.deleteSupplierAllocationByBusinessEntityAndFGByDatePeriod(
                businessEntities, functionalGroupId, startDate, endDate);
    }

    public int removeSupplierAllocationByBusinessEntityAndTAM(Set<BusinessEntity> businessEntities,
            Long tamId, Date endDate) {
        return functionalGroupSupplierAllocationRepository.removeSupplierAllocationByBusinessEntityAndTAM(
                businessEntities, tamId, endDate);
    }

        public int deleteItemAllocationByItemAndFGByDatePeriod(Long itemKey, Long functionalGroupId, Date startDate,
            Date endDate, Long tamId, Long businessEntityKey) {
        return functionalGroupItemAllocationRepository.deleteItemAllocationByItemAndFGByDatePeriod(
            itemKey, functionalGroupId, startDate, endDate, tamId, businessEntityKey);
        }

        public List<Date> getBucketsListToBeDefaulted(BusinessEntity be, Long itemKey, Date startDate, Long tamId) {
            return functionalGroupSupplierAllocationRepository.getBucketsListToBeDefaulted(
                    be, itemKey, startDate, tamId);
        }

    public Boolean isSupplierAllocationExists(FunctionalGroup functionalGroup, Site site, Date startDate, Date endDate) {
        Double result = functionalGroupSupplierAllocationRepository
                .sumAllocationByFunctionalGroupSiteAndDateRange(functionalGroup, site, startDate, endDate);
        return result != null && result > 0.0;
    }

    public Boolean isSupplierOrItemAllocationExist(FunctionalGroup functionalGroup, Site site) {
        List<Integer> result = functionalGroupSupplierAllocationRepository
                .findSupplierOrItemAllocationExist(
                        functionalGroup.getFunctionalGroupId(),
                        site.getSiteKey());
        return result != null && !result.isEmpty();
    }

    public Boolean isPastSupplierOrItemAllocationExist(FunctionalGroup functionalGroup, Site site) {
        Calendar currentMonthStartCalendar = Calendar.getInstance();
        currentMonthStartCalendar.set(Calendar.DAY_OF_MONTH, 1);
        currentMonthStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentMonthStartCalendar.set(Calendar.MINUTE, 0);
        currentMonthStartCalendar.set(Calendar.SECOND, 0);
        currentMonthStartCalendar.set(Calendar.MILLISECOND, 0);
        Date currentMonthStart = currentMonthStartCalendar.getTime();

        Calendar pastStartCalendar = (Calendar) currentMonthStartCalendar.clone();
        pastStartCalendar.add(Calendar.MONTH, -12);
        Date pastStartDate = pastStartCalendar.getTime();

        List<Integer> result = functionalGroupSupplierAllocationRepository
                .findPastSupplierOrItemAllocationExist(
                        functionalGroup.getFunctionalGroupId(),
                site.getSiteKey(),
                pastStartDate,
                currentMonthStart);
        return result != null && !result.isEmpty();
    }

    // ==================== WITH FGTYPE PARAMETER ====================

    public TAMAllocation getTAMAllocationByFGAndSite(String fgname, String fgType, String site, Date startDate, Date endDate) {
        Object[] tamSearchTypeAndDate = isPastFiscalPeriod(startDate, endDate);
        TAMAllocation tam = null;

        if (SCPlatformConstant.TAM_ONLY_PAST.equals(tamSearchTypeAndDate[0])) {
            // Archival past data only query not needed for site-level
            tam = tamAllocationRepository.getTAMAllocationByFGAndSite(
                    fgname,
                    fgType,
                    site,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
        } else if (SCPlatformConstant.TAM_ONLY_CURRENT.equals(tamSearchTypeAndDate[0])) {
            tam = tamAllocationRepository.getTAMAllocationByFGAndSite(
                    fgname,
                    fgType,
                    site,
                    startDate,
                    endDate);
        } else if (SCPlatformConstant.TAM_PAST_CURRENT_MIX.equals(tamSearchTypeAndDate[0])) {
            tam = tamAllocationRepository.getTAMAllocationByFGAndSite(
                    fgname,
                    fgType,
                    site,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);

            TAMAllocation currentTam = tamAllocationRepository.getTAMAllocationByFGAndSite(
                    fgname,
                    fgType,
                    site,
                    (Date) tamSearchTypeAndDate[3],
                    (Date) tamSearchTypeAndDate[4]);
            if (tam != null && tam.getSupplierAllocations() != null
                    && currentTam != null && currentTam.getSupplierAllocations() != null) {
                tam.getSupplierAllocations().addAll(currentTam.getSupplierAllocations());
            } else if (tam == null) {
                tam = currentTam;
            }
        }

        return tam;
    }

    public TAMAllocation getRegionTAMAllocationByFG(String fgname, String fgType, String region, Date startDate, Date endDate) {
        Object[] tamSearchTypeAndDate = isPastFiscalPeriod(startDate, endDate);
        TAMAllocation tam = null;

        if (SCPlatformConstant.TAM_ONLY_PAST.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getRegionTAMAllocationByFG(
                    fgname,
                    fgType,
                    region,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }
        } else if (SCPlatformConstant.TAM_ONLY_CURRENT.equals(tamSearchTypeAndDate[0])) {
            tam = tamAllocationRepository.getRegionTAMAllocationByFG(
                    fgname,
                    fgType,
                    region,
                    startDate,
                    endDate);
        } else if (SCPlatformConstant.TAM_PAST_CURRENT_MIX.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getRegionTAMAllocationByFG(
                    fgname,
                    fgType,
                    region,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }

            TAMAllocation currentTam = tamAllocationRepository.getRegionTAMAllocationByFG(
                    fgname,
                    fgType,
                    region,
                    (Date) tamSearchTypeAndDate[3],
                    (Date) tamSearchTypeAndDate[4]);
            if (tam != null && tam.getSupplierAllocations() != null
                    && currentTam != null && currentTam.getSupplierAllocations() != null) {
                tam.getSupplierAllocations().addAll(currentTam.getSupplierAllocations());
            } else if (tam == null) {
                tam = currentTam;
            }
        }

        return tam;
    }

    public TAMAllocation getGlobalTAMAllocationByFG(String fgname, String fgType, Date startDate, Date endDate) {
        Object[] tamSearchTypeAndDate = isPastFiscalPeriod(startDate, endDate);
        TAMAllocation tam = null;

        if (SCPlatformConstant.TAM_ONLY_PAST.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    fgType,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }
        } else if (SCPlatformConstant.TAM_ONLY_CURRENT.equals(tamSearchTypeAndDate[0])) {
            tam = tamAllocationRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    fgType,
                    startDate,
                    endDate);
        } else if (SCPlatformConstant.TAM_PAST_CURRENT_MIX.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    fgType,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }

            TAMAllocation currentTam = tamAllocationRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    fgType,
                    (Date) tamSearchTypeAndDate[3],
                    (Date) tamSearchTypeAndDate[4]);
            if (tam != null && tam.getSupplierAllocations() != null
                    && currentTam != null && currentTam.getSupplierAllocations() != null) {
                tam.getSupplierAllocations().addAll(currentTam.getSupplierAllocations());
            } else if (tam == null) {
                tam = currentTam;
            }
        }

        return tam;
    }

    public TAMAllocation getRegionTAMAllocationByFG(String fgname, String region, Date startDate, Date endDate) {
        Object[] tamSearchTypeAndDate = isPastFiscalPeriod(startDate, endDate);
        TAMAllocation tam = null;

        if (SCPlatformConstant.TAM_ONLY_PAST.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getRegionTAMAllocationByFG(
                    fgname,
                    region,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }
        } else if (SCPlatformConstant.TAM_ONLY_CURRENT.equals(tamSearchTypeAndDate[0])) {
            tam = tamAllocationRepository.getRegionTAMAllocationByFG(fgname, region, startDate, endDate);
        } else if (SCPlatformConstant.TAM_PAST_CURRENT_MIX.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getRegionTAMAllocationByFG(
                    fgname,
                    region,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }

            TAMAllocation currentTam = tamAllocationRepository.getRegionTAMAllocationByFG(
                    fgname,
                    region,
                    (Date) tamSearchTypeAndDate[3],
                    (Date) tamSearchTypeAndDate[4]);
            if (tam != null && tam.getSupplierAllocations() != null
                    && currentTam != null && currentTam.getSupplierAllocations() != null) {
                tam.getSupplierAllocations().addAll(currentTam.getSupplierAllocations());
            } else if (tam == null) {
                tam = currentTam;
            }
        }

        return tam;
    }

    public TAMAllocation getGlobalTAMAllocationByFG(String fgname, Date startDate, Date endDate) {
        Object[] tamSearchTypeAndDate = isPastFiscalPeriod(startDate, endDate);
        TAMAllocation tam = null;

        if (SCPlatformConstant.TAM_ONLY_PAST.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }
        } else if (SCPlatformConstant.TAM_ONLY_CURRENT.equals(tamSearchTypeAndDate[0])) {
            tam = tamAllocationRepository.getGlobalTAMAllocationByFG(fgname, startDate, endDate);
        } else if (SCPlatformConstant.TAM_PAST_CURRENT_MIX.equals(tamSearchTypeAndDate[0])) {
            TAMAllocationArchival archival = tamAllocationArchivalRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    (Date) tamSearchTypeAndDate[1],
                    (Date) tamSearchTypeAndDate[2]);
            if (archival != null) {
                tam = mapArchivalToTAMAllocation(archival);
            }

            TAMAllocation currentTam = tamAllocationRepository.getGlobalTAMAllocationByFG(
                    fgname,
                    (Date) tamSearchTypeAndDate[3],
                    (Date) tamSearchTypeAndDate[4]);
            if (tam != null && tam.getSupplierAllocations() != null
                    && currentTam != null && currentTam.getSupplierAllocations() != null) {
                tam.getSupplierAllocations().addAll(currentTam.getSupplierAllocations());
            } else if (tam == null) {
                tam = currentTam;
            }
        }

        return tam;
    }

    public boolean checkIfTAMExistsForFunctionalGroup(Long id) {
        Calendar today = Calendar.getInstance();
        List<FiscalPeriod> allFiscallMonth = fiscalPeriodService.getFiscalPeriods(
                today,
                FiscalPeriod.PeriodType.MONTH,
                0,
                11);

        Date startDate = new Date(allFiscallMonth.get(0).getFiscalPeriodStartDate().getTime());
        Date endDate = new Date(allFiscallMonth.get(allFiscallMonth.size() - 1).getFiscalPeriodEndDate().getTime());

        long count = tamAllocationRepository.countTAMWithAllocationByFunctionalGroupInDateRange(id, startDate, endDate);
        return count > 0;
    }

    public boolean checkIfTAMExistsForFunctionalGroupAndSite(Long functionalGroupId, Long siteKey) {
        Calendar today = Calendar.getInstance();
        List<FiscalPeriod> allFiscallMonth = fiscalPeriodService.getFiscalPeriods(
                today,
                FiscalPeriod.PeriodType.MONTH,
                0,
                11);

        Date startDate = new Date(allFiscallMonth.get(0).getFiscalPeriodStartDate().getTime());
        Date endDate = new Date(allFiscallMonth.get(allFiscallMonth.size() - 1).getFiscalPeriodEndDate().getTime());

        long count = tamAllocationRepository.countTAMWithAllocationByFunctionalGroupAndSiteInDateRange(functionalGroupId, siteKey, startDate, endDate);
        return count > 0;
    }

    public Object[] isPastFiscalPeriod(Date selectedStartDate, Date selectedEndDate) {
        Calendar today = Calendar.getInstance();
        List<FiscalPeriod> periods = fiscalPeriodService.getFiscalPeriods(today, FiscalPeriod.PeriodType.MONTH, 12, 0);
        Date oneYearsPastStartDate = periods.get(0).getFiscalPeriodStartDate();

        if (selectedStartDate.before(oneYearsPastStartDate)) {
            return selectedEndDate.after(oneYearsPastStartDate)
                        ? new Object[] { SCPlatformConstant.TAM_PAST_CURRENT_MIX, selectedStartDate, oneYearsPastStartDate,
                            oneYearsPastStartDate, selectedEndDate }
                        : new Object[] { SCPlatformConstant.TAM_ONLY_PAST, selectedStartDate, selectedEndDate };
        }
                return new Object[] { SCPlatformConstant.TAM_ONLY_CURRENT, selectedStartDate, selectedEndDate };
    }

    public static TAMAllocation mapArchivalToTAMAllocation(TAMAllocationArchival tamAllocationArchival) {
        if (tamAllocationArchival == null) {
            return null;
        }

        TAMAllocation tam = new TAMAllocation();
        tam.setId(tamAllocationArchival.getId());
        tam.setSite(tamAllocationArchival.getSite());
        tam.setFunctionalGroup(tamAllocationArchival.getFunctionalGroup());
        tam.setAllowHedging(tamAllocationArchival.getAllowHedging());
        tam.setLastChangedOn(tamAllocationArchival.getLastChangedOn());
        tam.setLastChangedBy(tamAllocationArchival.getLastChangedBy());
        tam.setCreatedOn(tamAllocationArchival.getCreatedOn());
        tam.setCreatedBy(tamAllocationArchival.getCreatedBy());
        tam.setExtractFlag(tamAllocationArchival.getExtractFlag());
        tam.setRollOverCount(tamAllocationArchival.getRollOverCount());
        tam.setNextRolloverDate(tamAllocationArchival.getNextRolloverDate());
        tam.setIsCurrentDataDeleted(tamAllocationArchival.getIsCurrentDataDeleted());
        tam.setDiscpExtractFlag(tamAllocationArchival.getDiscpExtractFlag());
        tam.setDiscpRolloverExtractFlag(tamAllocationArchival.getDiscpRolloverExtractFlag());
        tam.setSupplierAllocations(mapSupplierAllocations(tamAllocationArchival.getSupplierAllocationsArchival(), tam));
        return tam;
    }

    private static Set<FunctionalGroupSupplierAllocation> mapSupplierAllocations(
            Set<FunctionalGroupSupplierAllocationArchival> supplierAllocationsArchival,
            TAMAllocation parentTam) {
        if (supplierAllocationsArchival == null) {
            return null;
        }

        Set<FunctionalGroupSupplierAllocation> result = new HashSet<>();
        for (FunctionalGroupSupplierAllocationArchival archival : supplierAllocationsArchival) {
            FunctionalGroupSupplierAllocation allocation = new FunctionalGroupSupplierAllocation();
            allocation.setId(archival.getId());
            allocation.setStartDate(archival.getStartDate());
            allocation.setEndDate(archival.getEndDate());
            allocation.setAllocation(archival.getAllocation());
            allocation.setBusinessEntity(archival.getBusinessEntity());
            allocation.setTamAllocation(parentTam);
            allocation.setItemAllocations(mapItemAllocations(archival.getItemAllocationsArchival(), allocation));
            result.add(allocation);
        }
        return result;
    }

    private static Set<FunctionalGroupItemAllocation> mapItemAllocations(
            Set<FunctionalGroupItemAllocationArchival> itemAllocationsArchival,
            FunctionalGroupSupplierAllocation parentSupplierAllocation) {
        if (itemAllocationsArchival == null) {
            return null;
        }

        Set<FunctionalGroupItemAllocation> result = new HashSet<>();
        for (FunctionalGroupItemAllocationArchival archival : itemAllocationsArchival) {
            FunctionalGroupItemAllocation allocation = new FunctionalGroupItemAllocation();
            allocation.setId(archival.getId());
            allocation.setItem(archival.getItem());
            allocation.setAllocation(archival.getAllocation());
            allocation.setFunctionalGroupSupplierAllocation(parentSupplierAllocation);
            result.add(allocation);
        }
        return result;
    }
}
