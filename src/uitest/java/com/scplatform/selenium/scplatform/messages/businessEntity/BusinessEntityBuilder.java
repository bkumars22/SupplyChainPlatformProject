/**
 * @BusinessEntityBuilder.java@
 *
 * Created on Thu Oct 16 14:09:07 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.businessEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.Partner;
import com.test.selenium.common.RandomUser;

/**
 * Used to build default {@link BusinessEntity} message data.  
 *
 * Default Data:
 * <UL>
 * <LI> effectiveFromDate = One month before current date.  Change using {@link #withEffectiveFromDate(DateTime)}
 * <LI> effectiveToDate = One year after current date.  Change using {@link #withEffectiveToDate(DateTime)}
 * <LI> operationCode = C.  Change using {@link #withOperationCode(String)}
 * <LI> currencyCode = USD.  Change using {@link #withCurrencyCode(String)}
 * <LI> site_DefaultCurrency = USD.  Change using {@link #withSiteDefaultCurrency(String)}
 * <LI> site_effectiveFromDate = One month before current date.  Change using {@link #withSiteEffectiveFromDate(DateTime)}
 * <LI> site_effectiveToDate = One year after current date.  Change using {@link #withSiteEffectiveToDate(DateTime)}
 * <LI> alternates_effectiveFromDate = One month before current date.  Change using {@link #withAlternatesEffectiveFromDate(DateTime)}
 * <LI> alternates_effectiveToDate = One year after current date.  Change using {@link #withAlternatesEffectiveToDate(DateTime)}
 * </UL>
 * <br><br>
 * Chained Call Example
 * <pre>
 * BusinessEntityBuilder<BusinessEntity> builder = 
 * 				new BusinessEntityBuilder<BusinessEntity>(BusinessEntity.class, enterpriseCompany, supplierCompanies, manufacturerCompanies);
 * Iterable<BusinessEntity> data = builder.build();
 * </pre>
 * 
 * <b>SPEC:</b> http://confluence.dev.scplatform.local/display/PUBT/Business+Entity
 */
public class BusinessEntityBuilder<T extends BusinessEntity> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected static Partner enterprise;
    protected static Map<String, String> contactNameList;
    protected static int lineCounter = 0;
    protected Map<Integer, PartnerDetails> partnerDetailsMap;

    protected BusinessEntityBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new BusinessEntityEnricher());
    }

	/**
	 * @param messageClazz	
	 * 		The BusinessEntity Message class, typically BusinessEntity.class, but can be any class that extends it.
	 * @param enterpriseCompany 	
	 * 		The Enterprise Company {@link Partner}
	 * @param supplierCompanies
	 * 		List of Supplier Companies {@link Partner}
	 * @param manufacturerCompanies
	 * 		List of Manufacturer Companies {@link Partner}
	 * 
	 * <br><br>
	 * Special Information about Partner data:
	 * <ul>
	 * <li> UDF1 holds the Site Description
	 * <li> UDF2 holds the Site Type
	 * <li> UDF3 holds the Business Entity Alternates names (for Enterprise)
	 * <li> UDF4 holds the BusinessEntityExternalId
	 * </ul>
	 */
    public BusinessEntityBuilder(
    		Class<T> messageClazz, 
    		Partner enterpriseCompany, 
    		List<Partner> supplierCompanies, 
    		List<Partner> manufacturerCompanies) {
    	    	
        this(messageClazz, defaultNumMessages);
    	partnerDetailsMap = new HashMap<Integer, PartnerDetails>();
    	contactNameList = new HashMap<String, String>();
    	
    	lineCounter = 0;
    	enterprise = enterpriseCompany;
    	setPartnerDetails(enterpriseCompany, "ENTERPRISE");
    	setPartnerDetails(supplierCompanies, "SUPPLIER");
    	setPartnerDetails(manufacturerCompanies, "MANUFACTURER");
    	
    }
    
    @Override
    public Iterable<T> build() {
     	setNumMessages(partnerDetailsMap.size());
        return super.build();
    }

    protected class BusinessEntityEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  ADD DETAILS ON SPECIFICATIONS
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	PartnerDetails partnerDetails = partnerDetailsMap.get((int) lineNumber);
        	Partner partnerInfo = partnerDetails.getPartnerInfo();
        	
        	
         	
        	messageLine.setBusinessEntity(partnerInfo.getDuns());
        	messageLine.setBusinessEntityName(partnerInfo.getName());
        	messageLine.setDescription(partnerInfo.getDescription());
        	messageLine.setBusinessEntityExternalId(partnerInfo.getUdf4());
        	messageLine.setBusinessEntityType(partnerDetails.getBusinessEntityType());
        	messageLine.setContactName(partnerDetails.getContactName());
        	messageLine.setContactUniqueId(makeContactUniqueID("primary.contact", partnerInfo.getName()));
        	messageLine.setDataSource(enterprise.getName());
        	messageLine.setEffectiveFromDate(effectiveFromDate);
        	messageLine.setEffectiveToDate(effectiveToDate);
        	messageLine.setOperationCode(operationCode);
        	messageLine.setCurrency_CurrencyCode(currencyCode);
        	messageLine.setCurrency_OperationCode(operationCode);
        	messageLine.setSite_Site(partnerInfo.getSite());
        	messageLine.setSite_Description(partnerInfo.getUdf1());
        	messageLine.setSite_ParentSite("");
        	messageLine.setSite_SiteType(partnerInfo.getUdf2());
        	messageLine.setSite_ContactName(partnerDetails.getContactName());
        	messageLine.setSite_DefaultCurrency(site_DefaultCurrency);
        	messageLine.setSite_ContactUniqueId(makeContactUniqueID(partnerDetails.getContactName(), partnerInfo.getSite()));
        	messageLine.setSite_EffectiveFromDate(site_effectiveFromDate);
        	messageLine.setSite_EffectiveToDate(site_effectiveToDate);
        	messageLine.setSite_OperationCode(operationCode);
        	
        	if ("ENTERPRISE".equals(partnerDetails.getBusinessEntityType()))	{
        		if (StringUtils.isNotBlank(partnerInfo.getUdf3())){
                	messageLine.setAlternates_AlternateName(partnerInfo.getUdf3());
                	messageLine.setAlternates_EffectiveFromDate(alternates_effectiveFromDate);
                	messageLine.setAlternates_EffectiveToDate(alternates_effectiveToDate);
                	messageLine.setAlternates_OperationCode(operationCode);
        		}
        	}

        	return messageLine;
        }
        
        
    }
    
    
	public String makeContactUniqueID(String contactName, String businessEntityName) {
		if (contactNameList.containsKey(businessEntityName))	{
			return contactNameList.get(businessEntityName);
		}
		String contactID = contactName.replace(" ",  ".") + "@" + businessEntityName.replace(" ", "") + ".com".toLowerCase().trim();
		contactNameList.put(businessEntityName, contactID);
		return contactID;
	}
	
	
    
	protected void setPartnerDetails(Partner partnerInfo, String businessEntityType)	{
		PartnerDetails details = new PartnerDetails();
		RandomUser randomUser = new RandomUser();
		
		details.setPartnerInfo(partnerInfo);
		details.setBusinessEntityType(businessEntityType);
		details.setContactName(randomUser.getFirstName() + " " + randomUser.getLastName());
		
		partnerDetailsMap.put(lineCounter, details);
		lineCounter++;
	}
	
	protected void setPartnerDetails(List<Partner> partnerInfo, String businessEntityType)	{
		for (int i = 0; i < partnerInfo.size(); i++)	{
			setPartnerDetails(partnerInfo.get(i), businessEntityType);
		}
	}
	
    protected class PartnerDetails	{
    	private Partner partnerInfo;
    	private String businessEntityType;
    	private String contactName;
    	
		public Partner getPartnerInfo() {
			return partnerInfo;
		}
		public void setPartnerInfo(Partner partnerInfo) {
			this.partnerInfo = partnerInfo;
		}
		public String getBusinessEntityType() {
			return businessEntityType;
		}
		public void setBusinessEntityType(String businessEntityType) {
			this.businessEntityType = businessEntityType;
		}
		public String getContactName() {
			return contactName;
		}
		public void setContactName(String contactName) {
			this.contactName = contactName;
		}
    	
    }
    
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
    protected DateTime effectiveFromDate = DateTime.now().withTimeAtStartOfDay().minusMonths(1);
    protected DateTime effectiveToDate = DateTime.now().withTimeAtStartOfDay().plusYears(1);
    protected DateTime site_effectiveFromDate = DateTime.now().withTimeAtStartOfDay().minusMonths(1);
    protected DateTime site_effectiveToDate = DateTime.now().withTimeAtStartOfDay().plusYears(1);
    protected DateTime alternates_effectiveFromDate = DateTime.now().withTimeAtStartOfDay().minusMonths(1);
    protected DateTime alternates_effectiveToDate = DateTime.now().withTimeAtStartOfDay().plusYears(1);    
    protected String operationCode = "C";
    protected String currencyCode = "USD";
    protected String site_DefaultCurrency = "USD";
    
    /**
	 * Used to set {@link BusinessEntity#setEffectiveFromDate(DateTime)}<br>
	 * Default is one month before current date: 
	 * 		DateTime.now().withTimeAtStartOfDay().minusMonths(1)
     */
    public BusinessEntityBuilder<T> withEffectiveFromDate(DateTime value){
    	this.effectiveFromDate = value;
    	return this;
    }
    
    /**
	 * Used to set {@link BusinessEntity#setSite_EffectiveFromDate(DateTime)}<br>
	 * Default is one month before current date: 
	 * 		DateTime.now().withTimeAtStartOfDay().minusMonths(1)
     */
    public BusinessEntityBuilder<T> withSiteEffectiveFromDate(DateTime value){
    	this.site_effectiveFromDate = value;
    	return this;
    }

    /**
	 * Used to set {@link BusinessEntity#setAlternates_EffectiveFromDate(DateTime)}<br>
	 * Default is one month before current date: 
	 * 		DateTime.now().withTimeAtStartOfDay().minusMonths(1)
     */
    public BusinessEntityBuilder<T> withAlternatesEffectiveFromDate(DateTime value){
    	this.alternates_effectiveFromDate = value;
    	return this;
    }
    
    /**
	 * Used to set {@link BusinessEntity#setEffectiveToDate(DateTime)}<br>
	 * Default is one year after current date:
	 * 		DateTime.now().withTimeAtStartOfDay().plusYears(1);
     */
    public BusinessEntityBuilder<T> withEffectiveToDate(DateTime value){
    	this.effectiveToDate = value;
    	return this;
    }

    /**
	 * Used to set {@link BusinessEntity#setSite_EffectiveToDate(DateTime)}<br>
	 * Default is one year after current date:
	 * 		DateTime.now().withTimeAtStartOfDay().plusYears(1);
     */
    public BusinessEntityBuilder<T> withSiteEffectiveToDate(DateTime value){
    	this.site_effectiveToDate = value;
    	return this;
    }
    
    /**
	 * Used to set {@link BusinessEntity#setAlternates_EffectiveToDate(DateTime)}<br>
	 * Default is one year after current date:
	 * 		DateTime.now().withTimeAtStartOfDay().plusYears(1);
     */
    public BusinessEntityBuilder<T> withAlternatesEffectiveToDate(DateTime value){
    	this.alternates_effectiveToDate = value;
    	return this;
    }
    
    /**
	 * Used to set {@link BusinessEntity#setOperationCode(String)}<br>
	 * Default: C
     */
    public BusinessEntityBuilder<T> withOperationCode(String value){
    	this.operationCode = value;
    	return this;
    }

    /**
	 * Used to set {@link BusinessEntity#setCurrency_CurrencyCode(String)}<br>
	 * Default: USD
     */
    public BusinessEntityBuilder<T> withCurrencyCode(String value){
    	this.currencyCode = value;
    	return this;
    }

    /**
	 * Used to set {@link BusinessEntity#setSite_DefaultCurrency(String)}<br>
	 * Default: USD
     */
    public BusinessEntityBuilder<T> withSiteDefaultCurrency(String value){
    	this.site_DefaultCurrency = value;
    	return this;
    }

}
