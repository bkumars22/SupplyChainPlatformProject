/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * Author : @Suvasish Bhoi
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.priceTam.dto;


import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.transform.Transformers;

import java.text.SimpleDateFormat;
import java.util.*;


public class PriceTAMForm extends SearchForm {

	private final static Logger logger = LogManager.getLogger(PriceTAMForm.class);
	
	private static final String SEPARATOR = "~";
	private static final String MONTHLY_DATA_QUERY = "priceTAMUIMonthlyBucketValue";
	private List<FiscalPeriod> periods;
	private Boolean monthlySearch;
	private Map<String,Map<Date,PriceTAMFiscalData>> periodHorizonMonthlyPriceData;
	private Map<String,Map<Date,PriceTAMFiscalData>> periodHorizonMonthlyTAMData;
	private Map<String,Map<Date,PriceTAMFiscalData>> periodHorizonQuarterlyPriceData;
	private Map<String,Map<Date,PriceTAMFiscalData>> periodHorizonQuarterlyTAMData;
	private StringBuilder keyFrame = new StringBuilder();
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private String searchType;
	private String offsetConfigValue = null;
	private List<PriceTAMOffsetCost> offsetValueDataSet;
	private Integer maxOffsetValue;
	private List<FiscalPeriod> actualFiscalMonth;
	private List<FiscalPeriod> actualFiscalQuarter;
	private Set<String> proceesedMonthlyPriceDataKey = new HashSet<String>();
	private Set<String> proceesedMonthlyTAMDataKey = new HashSet<String>();
	private Set<String> proceesedQuarterlyPriceDataKey = new HashSet<String>();
	private Set<String> proceesedQuarterlyTAMDataKey = new HashSet<String>();
	private String headerDateFormat;
	private static final String PRICE = "PRICE";
	private static final String TAM = "TAM";
	private static Map<Date,PriceTAMFiscalData> emptyBlock = new HashMap<Date, PriceTAMFiscalData>();
	private Boolean pastBucket;
	private Date currentDate;
	
	public void reset() {
		periods = null;
		monthlySearch = null;
		offsetConfigValue = null;
		offsetValueDataSet = null;
		maxOffsetValue = null;
		actualFiscalMonth = null;
		actualFiscalQuarter= null;
		pastBucket= null;
		currentDate=null;
		clearCache();
	}
	
	public void clearCache() {
		if(periodHorizonMonthlyPriceData != null) {
			periodHorizonMonthlyPriceData.clear();
		}
		if(periodHorizonMonthlyTAMData != null) {
			periodHorizonMonthlyTAMData.clear();
		}
		if(periodHorizonQuarterlyPriceData != null) {
			periodHorizonQuarterlyPriceData.clear();
		}
		if(periodHorizonQuarterlyTAMData != null) {
			periodHorizonQuarterlyTAMData.clear();
		}
		proceesedMonthlyPriceDataKey.clear();
		proceesedMonthlyTAMDataKey.clear();
		proceesedQuarterlyPriceDataKey.clear();
		proceesedQuarterlyTAMDataKey.clear();
	}
	
	public String getHeaderDateFormat() {
		return headerDateFormat;
	}

	public void setHeaderDateFormat(String headerDateFormat) {
		this.headerDateFormat = headerDateFormat;
	}
	
	public List<FiscalPeriod> getActualFiscalQuarter() {
		return actualFiscalQuarter;
	}

	public void setActualFiscalQuarter(List<FiscalPeriod> actualFiscalQuarter) {
		this.actualFiscalQuarter = actualFiscalQuarter;
	}

	public List<FiscalPeriod> getTimelineForFiscal() {
		try {
			if(monthlySearch) {
				return actualFiscalMonth;
			}else {
				return actualFiscalQuarter;
			}
		}catch (NullPointerException e) {
			return null;
		}
	}

	public List<FiscalPeriod> getActualFiscalMonth() {
		return actualFiscalMonth;
	}

	public void setActualFiscalMonth(List<FiscalPeriod> actualFiscalMonth) {
		this.actualFiscalMonth = actualFiscalMonth;
	}

	public Integer getMaxOffsetValue() {
		return maxOffsetValue;
	}

	public void setMaxOffsetValue(Integer maxOffsetValue) {
		this.maxOffsetValue = maxOffsetValue;
	}

	public List<PriceTAMOffsetCost> getOffsetValueDataSet() {
		return offsetValueDataSet;
	}

	public void setOffsetValueDataSet(List<PriceTAMOffsetCost> offsetValueDataSet) {
		this.offsetValueDataSet = offsetValueDataSet;
	}

	public String getOffsetConfigValue() {
		return offsetConfigValue;
	}

	public void setOffsetConfigValue(String offsetConfigValue) {
		this.offsetConfigValue = offsetConfigValue;
	}
	
	public List<FiscalPeriod> getPeriods() {
		return periods;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public void setPeriods(List<FiscalPeriod> periods) {
		this.periods = periods;
	}

	public static Object getDate(PriceTAMForm o) {
		return o.getFilterType();
	}

	public Boolean getMonthlySearch() {
		return monthlySearch;
	}

	public void setMonthlySearch(Boolean monthlySearch) {
		this.monthlySearch = monthlySearch;
	}

	public Map<Date,PriceTAMFiscalData> getFiscalPeriodPriceData(Long itemKey, String mpn, Long supplierId, String costType,
			Long functionalGroupId, Long fromSiteKey, Long toSiteKey,Integer priceOffset) {
		if(costType == null || functionalGroupId == null) {
			return emptyBlock;
		}
		try {
			String costKey = getKey(itemKey, mpn, supplierId, costType, functionalGroupId, fromSiteKey, toSiteKey, null);
			if(monthlySearch) {
				if (periodHorizonMonthlyPriceData == null) {
					periodHorizonMonthlyPriceData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (periodHorizonMonthlyTAMData == null) {
					periodHorizonMonthlyTAMData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (!periodHorizonMonthlyPriceData.containsKey(costKey)) {
					//preparePriceTAMFiscalData(functionalGroupId);
				}
				
				
				if(priceOffset == null) {
					return periodHorizonMonthlyPriceData.get(costKey);
				}else {
					if(proceesedMonthlyPriceDataKey.contains(costKey)) {
						return periodHorizonMonthlyPriceData.get(costKey);
					}
					proceesedMonthlyPriceDataKey.add(costKey);
					return getPriceOffsetData(periodHorizonMonthlyPriceData,costKey,priceOffset);
				}
			}else {
				if (periodHorizonQuarterlyPriceData == null) {
					periodHorizonQuarterlyPriceData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (periodHorizonQuarterlyTAMData == null) {
					periodHorizonQuarterlyTAMData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (!periodHorizonQuarterlyPriceData.containsKey(costKey)) {
					//preparePriceTAMFiscalData(functionalGroupId);
				}
				
				if(priceOffset == null) {
					if(proceesedQuarterlyPriceDataKey.contains(costKey)) {
						return periodHorizonQuarterlyPriceData.get(costKey);
					}
					proceesedQuarterlyPriceDataKey.add(costKey);
					return getQuarterlyPriceDataFromMonthly(periodHorizonQuarterlyPriceData,costKey);
				}else {
					if(proceesedQuarterlyPriceDataKey.contains(costKey)) {
						return periodHorizonQuarterlyPriceData.get(costKey); 
					}
					proceesedQuarterlyPriceDataKey.add(costKey);
					getPriceOffsetData(periodHorizonQuarterlyPriceData,costKey, priceOffset);
					return getQuarterlyPriceDataFromMonthly(periodHorizonQuarterlyPriceData,costKey);
				}
			}
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	public Map<Date,PriceTAMFiscalData> getFiscalPeriodTAMData(Long itemKey, String mpn, Long supplierId,Long functionalGroupId,Long tamSiteKey) {
		if(tamSiteKey == null || functionalGroupId == null) {
			return emptyBlock;
		}
		try {
			String tamKey = getKey(itemKey, mpn, supplierId, null, functionalGroupId, null, null, tamSiteKey);
			if(monthlySearch) {
				if (periodHorizonMonthlyTAMData == null) {
					periodHorizonMonthlyTAMData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (periodHorizonMonthlyPriceData == null) {
					periodHorizonMonthlyPriceData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (!periodHorizonMonthlyTAMData.containsKey(tamKey)) {
					//preparePriceTAMFiscalData(functionalGroupId);
				}
				
				return periodHorizonMonthlyTAMData.get(tamKey);
			}else {
				if (periodHorizonQuarterlyTAMData == null) {
					periodHorizonQuarterlyTAMData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (periodHorizonQuarterlyPriceData == null) {
					periodHorizonQuarterlyPriceData = new HashMap<String, Map<Date,PriceTAMFiscalData>>();
				}
				if (!periodHorizonQuarterlyTAMData.containsKey(tamKey)) {
					//preparePriceTAMFiscalData(functionalGroupId);
				}
				
				if(proceesedQuarterlyTAMDataKey.contains(tamKey)) {
					return periodHorizonQuarterlyTAMData.get(tamKey);
				}
				proceesedQuarterlyTAMDataKey.add(tamKey);
				return getQuarterlyTAMDataFromMonthly(periodHorizonQuarterlyTAMData,tamKey);
			}
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	private String getKey(Long itemKey, String mpn, Long supplierId, String costTypeKey, Long functionalGroupId,
			Long fromSiteKey, Long toSiteKey, Long tamSiteKey) {
		keyFrame.delete(0, keyFrame.length());
		keyFrame.append(itemKey);
		keyFrame.append(SEPARATOR);
		keyFrame.append(mpn == null ? "" : mpn);
		keyFrame.append(SEPARATOR);
		keyFrame.append(supplierId == null ? "" : supplierId);
		keyFrame.append(SEPARATOR);
		keyFrame.append(costTypeKey == null ? "" : costTypeKey);
		keyFrame.append(SEPARATOR);
		keyFrame.append(functionalGroupId == null ? "" : functionalGroupId);
		keyFrame.append(SEPARATOR);
		keyFrame.append(fromSiteKey == null ? "" : fromSiteKey);
		keyFrame.append(SEPARATOR);
		keyFrame.append(toSiteKey == null ? "" : toSiteKey);
		keyFrame.append(SEPARATOR);
		keyFrame.append(tamSiteKey == null ? "" : tamSiteKey);
		return keyFrame.toString();
	}

	/*private void preparePriceTAMFiscalData(Long functionalGroupId) {
		StringBuilder queryString = new StringBuilder(
				HibernateUtil.currentSession().getNamedQuery(MONTHLY_DATA_QUERY).getQueryString());
		Query query = HibernateUtil.currentSession().createSQLQuery(queryString.toString())
				.addScalar("FISCALSTARTDATE",new TimestampType())
				.addScalar("FISCALENDDATE",new TimestampType())
				.addScalar("FUNCTIONALGROUPID", new LongType())
				.addScalar("ITEMKEY", new LongType())
				.addScalar("SUPPLIERKEY", new LongType())
				.addScalar("TOSITEKEY", new LongType())
				.addScalar("TOSITEDESCRIPTION",new StringType())
				.addScalar("SOURCINGLANEKEY", new LongType())
				.addScalar("FROMSITEKEY", new LongType())
				.addScalar("COSTTYPENAME",new StringType())
				.addScalar("COSTTYPEKEY",new StringType())
				.addScalar("MPN",new StringType())
				.addScalar("COSTVALUE", new DoubleType())
				.addScalar("XLOBID", new LongType())
				.addScalar("SITEKEY", new LongType())
				.addScalar("SITEDESCRIPTION", new StringType())
				.addScalar("ALLOCATION", new DoubleType());
		query.setLong("fgId", functionalGroupId);
		query.setString("fiscalPeriodStartDate", sdf.format(periods.get(0).getFiscalPeriodStartDate()));
		query.setString("fiscalPeriodEndDate",  sdf.format(periods.get(periods.size()-1).getFiscalPeriodEndDate()));
		query.setParameterList("configuredCostTypes", ConfigurationUtils.getList("pcm.mpn.cost.allowableCostTypes", new ArrayList<String>()));
		query.setResultTransformer(Transformers.aliasToBean(PriceTAMMonthlyFiscalDataMap.class));
		List<PriceTAMMonthlyFiscalDataMap> result = query.list();
		if(monthlySearch) {
			preparePeriodHorizonDataMapForMonth(result);
		}else {
			preparePeriodHorizonDataMapForQuarter(result);
		}
	}*/

	private void preparePeriodHorizonDataMapForQuarter(List<PriceTAMMonthlyFiscalDataMap> results) {
		if (periodHorizonQuarterlyPriceData.size() > 200) {
			periodHorizonQuarterlyPriceData.clear();
			proceesedQuarterlyPriceDataKey.clear();
		}
		if (periodHorizonQuarterlyTAMData.size() > 200) {
			periodHorizonQuarterlyTAMData.clear();
			proceesedQuarterlyTAMDataKey.clear();
		}
		
		String costKey = null;
		String xlobKey = null;
		
		for (PriceTAMMonthlyFiscalDataMap result : results) {
			costKey = getKey(result.getItemKey(), result.getMpn(), result.getSupplierKey(), result.getCostTypeKey(),
					result.getFunctionalGroupID(), result.getFromSiteKey(), result.getToSiteKey(), null);
			xlobKey = getKey(result.getItemKey(), result.getMpn(), result.getSupplierKey(), null,
					result.getFunctionalGroupID(), null, null, result.getSiteKey());
			
			if(result.getCostTypeKey() != null) {
				//get price data
				if (!periodHorizonQuarterlyPriceData.containsKey(costKey)) {
					periodHorizonQuarterlyPriceData.put(costKey, new LinkedHashMap<Date, PriceTAMFiscalData>());
				}
				if(!proceesedQuarterlyPriceDataKey.contains(costKey)) {
					periodHorizonQuarterlyPriceData.get(costKey).put(result.getFiscalStartDate(),new PriceTAMFiscalData(result.getFiscalStartDate(),result.getFiscalEndDate(), result.getCostValue(),
						null, false,false));
				}
			}
			
			if(result.getSiteKey() != null) {
				//get TAM data
				if (!periodHorizonQuarterlyTAMData.containsKey(xlobKey)) {
					periodHorizonQuarterlyTAMData.put(xlobKey, new LinkedHashMap<Date, PriceTAMFiscalData>());
				}
				if(!proceesedQuarterlyTAMDataKey.contains(xlobKey)) {
					periodHorizonQuarterlyTAMData.get(xlobKey).put(result.getFiscalStartDate(),new PriceTAMFiscalData(result.getFiscalStartDate(),result.getFiscalEndDate(), null,
						result.getAllocation(), false,false));
				}
			}
		}
	}

	private void preparePeriodHorizonDataMapForMonth(List<PriceTAMMonthlyFiscalDataMap> results) {
		if (periodHorizonMonthlyPriceData.size() > 200) {
			periodHorizonMonthlyPriceData.clear();
			proceesedMonthlyPriceDataKey.clear();
		}
		if (periodHorizonMonthlyTAMData.size() > 200) {
			periodHorizonMonthlyTAMData.clear();
			proceesedMonthlyTAMDataKey.clear();
		}
		
		String costKey = null;
		String xlobKey = null;
		for (PriceTAMMonthlyFiscalDataMap result : results) {
			costKey = getKey(result.getItemKey(), result.getMpn(), result.getSupplierKey(), result.getCostTypeKey(),
					result.getFunctionalGroupID(), result.getFromSiteKey(), result.getToSiteKey(), null);
			xlobKey = getKey(result.getItemKey(), result.getMpn(), result.getSupplierKey(), null,
					result.getFunctionalGroupID(), null, null, result.getSiteKey());
			
			if(result.getCostTypeKey() != null) {
				//get price data
				if (!periodHorizonMonthlyPriceData.containsKey(costKey)) {
					periodHorizonMonthlyPriceData.put(costKey, new LinkedHashMap<Date, PriceTAMFiscalData>());
				}
				if(!proceesedMonthlyPriceDataKey.contains(costKey)) {
					periodHorizonMonthlyPriceData.get(costKey).put(result.getFiscalStartDate(),new PriceTAMFiscalData(result.getFiscalStartDate(),result.getFiscalEndDate(),result.getCostValue(),
							null, false,false));
				}
			}
			
			if(result.getSiteKey() != null) {
				//get TAM data
				if (!periodHorizonMonthlyTAMData.containsKey(xlobKey)) {
					periodHorizonMonthlyTAMData.put(xlobKey, new LinkedHashMap<Date, PriceTAMFiscalData>());
				}
				if(!proceesedMonthlyTAMDataKey.contains(xlobKey)) {
					periodHorizonMonthlyTAMData.get(xlobKey).put(result.getFiscalStartDate(),new PriceTAMFiscalData(result.getFiscalStartDate(),result.getFiscalEndDate(), null,
							result.getAllocation(), false,false));
				}
			}
		}
	}
	
	private Map<Date,PriceTAMFiscalData> getPriceOffsetData(Map<String,Map<Date,PriceTAMFiscalData>> source,String key,Integer priceOffset) {
		Map<Date,PriceTAMFiscalData> fiscalHorizonData = source.get(key);
		for(int i = actualFiscalMonth.size()-1 ; i >= 0 ; i--) {
			PriceTAMFiscalData fiscalData = fiscalHorizonData.get(actualFiscalMonth.get(i).getFiscalPeriodStartDate());
			try {
				if(fiscalData == null) {
					if(fiscalHorizonData.containsKey(periods.get(i-maxOffsetValue+priceOffset).getFiscalPeriodStartDate())) {
						fiscalData = new PriceTAMFiscalData(actualFiscalMonth.get(i).getFiscalPeriodStartDate(),actualFiscalMonth.get(i).getFiscalPeriodEndDate(), fiscalHorizonData.get(periods.get(i-maxOffsetValue+priceOffset).getFiscalPeriodStartDate()).getPrice()
								, null, false, false);
						fiscalHorizonData.put(actualFiscalMonth.get(i).getFiscalPeriodStartDate(), fiscalData);
					}
				}else {
					if(fiscalHorizonData.containsKey(periods.get(i-maxOffsetValue+priceOffset).getFiscalPeriodStartDate())) {
						fiscalData.setPrice(fiscalHorizonData.get(periods.get(i-maxOffsetValue+priceOffset).getFiscalPeriodStartDate()).getPrice());
					}else {
						fiscalData.setPrice(null);
					}
				}
			}catch (NullPointerException e) {
				//no need to through. This is expected as verifying in two list with index
			}
		}
		source.put(key, fiscalHorizonData);
		return fiscalHorizonData;
	}
	
	private Map<Date,PriceTAMFiscalData> getQuarterlyPriceDataFromMonthly(Map<String,Map<Date,PriceTAMFiscalData>> source,String key){
		Map<Date,PriceTAMFiscalData> fiscalHorizonData = source.get(key);
		if(fiscalHorizonData == null) {
			return null;
		}
		Set<Double> priceSet = new HashSet<Double>();
		Set<Double> allocationSet = new HashSet<Double>();
		List<Date> dataToBeRemoved = new ArrayList<Date>();
		PriceTAMFiscalData priceTAMData = null;
		List<Date> retainPriceTAM = new ArrayList<Date>(); 
		
		for(int i = 0 ; i < actualFiscalQuarter.size() ; i++) {
			//each quarter can have max of 3 month. so this will run for max 3 time
			int count = 0;
			priceTAMData = null;
			
			//segregate month into quarter
			innerloop : for(Map.Entry<Date, PriceTAMFiscalData> fiscalEntry : fiscalHorizonData.entrySet()) {
				if(count == 3 ) {
					break innerloop;
				}
				//get all month for that quarter
				if(DateAndTimeUtils.between(fiscalEntry.getKey(),actualFiscalQuarter.get(i).getFiscalPeriodStartDate(),actualFiscalQuarter.get(i).getFiscalPeriodEndDate())) {
					count++;
					priceSet.add(fiscalEntry.getValue().getPrice());
					dataToBeRemoved.add(fiscalEntry.getKey());
					priceTAMData = fiscalEntry.getValue();
				}
			}
			//if exist find the variance, price and allocation for that quarter
			if(priceTAMData != null) {
				retainPriceTAM.add(priceTAMData.getFiscalStartDate());
				dataToBeRemoved.retainAll(retainPriceTAM);
			
				priceTAMData.setPriceVariance(priceSet.size() > 1);
				priceTAMData.setFiscalStartDate(actualFiscalQuarter.get(i).getFiscalPeriodStartDate());
			
				fiscalHorizonData.keySet().removeAll(dataToBeRemoved);
				fiscalHorizonData.put(actualFiscalQuarter.get(i).getFiscalPeriodStartDate(), priceTAMData);
			
				priceSet.clear();
				allocationSet.clear();
				dataToBeRemoved.clear();
				retainPriceTAM.clear();
			}
		}
		periodHorizonQuarterlyPriceData.put(key, fiscalHorizonData);
		return fiscalHorizonData;
	}
	
	private Map<Date,PriceTAMFiscalData> getQuarterlyTAMDataFromMonthly(Map<String,Map<Date,PriceTAMFiscalData>> source,String key){
		Map<Date,PriceTAMFiscalData> fiscalHorizonData = source.get(key);
		if(fiscalHorizonData == null) {
			return null;
		}
		Set<Double> priceSet = new HashSet<Double>();
		Set<Double> allocationSet = new HashSet<Double>();
		List<Date> dataToBeRemoved = new ArrayList<Date>();
		PriceTAMFiscalData priceTAMData = null;
		List<Date> retainPriceTAM = new ArrayList<Date>();
		for(int i = 0 ; i < actualFiscalQuarter.size() ; i++) {
			//each quarter can have max of 3 month. so this will run for max 3 time
			int count = 0;
			priceTAMData = null;
			Double lastMonthValue = null;
			
			//segregate month into quarter
			innerloop : for(Map.Entry<Date, PriceTAMFiscalData> fiscalEntry : fiscalHorizonData.entrySet()) {
				if(count == 3 ) {
					break innerloop;
				}
				//get all month for that quarter
				if(DateAndTimeUtils.between(fiscalEntry.getKey(),actualFiscalQuarter.get(i).getFiscalPeriodStartDate(),actualFiscalQuarter.get(i).getFiscalPeriodEndDate())) {
					count++;
					allocationSet.add(fiscalEntry.getValue().getAllocation());
					dataToBeRemoved.add(fiscalEntry.getKey());
					priceTAMData = fiscalEntry.getValue();
					if(DateAndTimeUtils.same(actualFiscalQuarter.get(i).getFiscalPeriodEndDate(), fiscalEntry.getValue().getFiscalEndDate())) {
						lastMonthValue = priceTAMData.getAllocation();
					}
				}
			}
			//if exist find the variance, price and allocation for that quarter
			if(priceTAMData != null) {
				retainPriceTAM.add(priceTAMData.getFiscalStartDate());
				dataToBeRemoved.retainAll(retainPriceTAM);
			
				priceTAMData.setAllocationVariance(allocationSet.size() > 1);
				priceTAMData.setFiscalStartDate(actualFiscalQuarter.get(i).getFiscalPeriodStartDate());
				priceTAMData.setAllocation(lastMonthValue);
				fiscalHorizonData.keySet().removeAll(dataToBeRemoved);
				fiscalHorizonData.put(actualFiscalQuarter.get(i).getFiscalPeriodStartDate(), priceTAMData);
				
				priceSet.clear();
				allocationSet.clear();
				dataToBeRemoved.clear();
				retainPriceTAM.clear();
			}
		}
		periodHorizonQuarterlyTAMData.put(key, fiscalHorizonData);
		return fiscalHorizonData;
	}
	
	public Date getCurrentFiscalPeriodStartDate() {
		/*
		 * int fiscalCount = actualFiscalMonth.size(); return
		 * periods.get(fiscalCount/2+1).getFiscalPeriodStartDate();
		 */
		return getCurrentDate();
	}
	
	public Integer getOffsetValueTag(String dataSource,String costType,String commodityName,String itemBusinessName) {
		PriceTAMOffsetCost ptoc = new PriceTAMOffsetCost(dataSource, costType,commodityName,itemBusinessName);
		int index = offsetValueDataSet.indexOf(ptoc);
		if(index != -1) {
			return offsetValueDataSet.get(index).getOffsetValue();
		}else {
			return null;
		}
	}

	public Boolean getPastBucket() {
		return pastBucket;
	}

	public void setPastBucket(Boolean pastBucket) {
		this.pastBucket = pastBucket;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
}