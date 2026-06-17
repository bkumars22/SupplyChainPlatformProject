/**
 * @ContactBuilder.java@
 *
 * Created on Thu Oct 16 14:14:28 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.contact;

import java.util.List;

import com.scplatform.qa.e2Messages.utilities.PartnerInfo;
import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.RandomUser;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntityBuilder;
import com.google.common.collect.Iterables;

/**
 * Used to build default {@link Contact} message data.  
 *
 * Default Data:
 * <UL>
 * <LI> operationCode = C.  Change using {@link #withOperationCode(String)}
 * </UL>
 * <br><br>
 * Chained Call Example
 * <pre>
 * ContactBuilder<Contact> builder = 
 * 				new ContactBuilder<Contact>(Contact.class, businessEntityData, allPartnersInBusinessEntity);
 * Iterable<Contact> data = builder.build();
 * </pre>
 * 
 *
 */
public class ContactBuilder<T extends Contact> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected BusinessEntity[] businessEntityArray;
    List<PartnerInfo> allPartners;
    
    protected ContactBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        this.setMessageLineEnricher(new ContactEnricher());
    }

    /**
     * @param messageClazz
     * 		The Contact Message class, typically Contact.class, but can be any class that extends it.
     * @param businessEntityData
     * 		{@link BusinessEntity} data, typically from {@link BusinessEntityBuilder}, that the Contact data will be created against.
     * @param allPartnersInBusinessEntity
     * 		All {@link PartnerInfo} that was used in {@link BusinessEntityBuilder}.  Data is looked up to populate the Conact data.
     */
    public ContactBuilder(
    		Class<T> messageClazz, 
    		Iterable<BusinessEntity> businessEntityData, 
    		List<PartnerInfo> allPartnersInBusinessEntity) {
    	
        this(messageClazz, defaultNumMessages);
        
        businessEntityArray = Iterables.toArray(businessEntityData, BusinessEntity.class);
        allPartners = allPartnersInBusinessEntity;
    }

    @Override
    public Iterable<T> build() {
     	setNumMessages(businessEntityArray.length);
        return super.build();
    }
    
    protected class ContactEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  ADD DETAILS ON SPECIFICATIONS
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	BusinessEntity businessEntity = businessEntityArray[(int) lineNumber];
        	PartnerInfo partner = findPartner(businessEntity);
        	RandomUser randomUser = new RandomUser();
        	
        	messageLine.setContactName(businessEntity.getContactName());
        	messageLine.setContactId(businessEntity.getContactUniqueId());
        	messageLine.setContactUniqueId(businessEntity.getContactUniqueId());
        	messageLine.setAddressLine1(partner.getAddress1());
        	messageLine.setAddressLine2(partner.getAddress2());
        	messageLine.setAddressLine3(partner.getAddress3());
        	messageLine.setCityName(partner.getCity());
        	messageLine.setRegionName(partner.getCounty());
        	messageLine.setStateName(partner.getSite());
        	messageLine.setCountryName(partner.getCountry());
        	messageLine.setTelephoneNumber(randomUser.getPhone());
        	messageLine.setNationalPostalCode(partner.getZip());
        	messageLine.setGlobalCountryCode(partner.getCountry());
        	messageLine.setFascimileNumber(randomUser.getPhone());
        	messageLine.setDepartment("Department " + businessEntity.getSite_Site());
        	messageLine.setBusinessEntity(businessEntity.getBusinessEntity());
        	messageLine.setBusinessEntityType(businessEntity.getBusinessEntityType());
        	messageLine.setBusinessName(businessEntity.getBusinessEntityName());
        	messageLine.setEmailAddress(businessEntity.getContactUniqueId());
        	messageLine.setPartnerClassificationCode(null);
        	messageLine.setRole(null);
        	messageLine.setDataSource(businessEntity.getDataSource());
        	messageLine.setEffectiveFromDate(businessEntity.getEffectiveFromDate());
        	messageLine.setEffectiveToDate(businessEntity.getEffectiveToDate());
        	messageLine.setOperationCode(operationCode);
        	return messageLine;
        }
        
    }
    
   
	
    
	protected PartnerInfo findPartner(BusinessEntity businessEntity) {
		PartnerInfo partner = null;
		
		for (PartnerInfo partnerInfo : allPartners)	{
			if (partnerInfo.getSite().equals(businessEntity.getSite_Site())){
				if (partnerInfo.getName().equals(businessEntity.getBusinessEntityName()))	{
					partner = partnerInfo;
					break;
				}
			}
		}
		return partner;
	}
    
    
    //===========================================
    // CHAINED CALLS
    //===========================================
	protected String operationCode = "C";
	

    /**
	 * Used to set {@link Contact#setOperationCode(String)}<br>
	 * Default: C
     */
    public ContactBuilder<T> withOperationCode(String value){
    	this.operationCode = value;
    	return this;
    }

    

    

}
