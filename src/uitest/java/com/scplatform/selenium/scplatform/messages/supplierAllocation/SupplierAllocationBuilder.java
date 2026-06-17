/**
 * @SupplierAllocationBuilder.java@
 *
 * Created on Wed Oct 22 11:04:17 PDT 2014
 *
 *      Copyright (c) 2014 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.supplierAllocation;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.iris.factory.DefaultMessageFactory;
import com.scplatform.qa.iris.factory.MessageLineEnricher;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.calendar.CalendarUtils;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedVendorListItem;
import com.google.common.collect.Lists;

/**
 * 
 *
 * Used to build default {@link SupplierAllocation} message data.  
 *
 * <br><br>
 * Chained Call Example
 * <pre>
 * SupplierAllocationBuilder<SupplierAllocation> builder = 
 * 				new SupplierAllocationBuilder<SupplierAllocation>(SupplierAllocation.class, item, calendar, enterprisePartner);
 * Iterable<SupplierAllocation> data = builder.build();
 * </pre>
 * 
 *
 */
public class SupplierAllocationBuilder<T extends SupplierAllocation> extends DefaultMessageFactory<T> {
    protected final static long defaultNumMessages = 1;
    protected List<Item> itemList;
    protected Iterable<Calendar> calendar;
    protected List<T> supplierAllocationData;
    protected Partner enterprisePartner;
    protected Class<T> allocationClazz = null;
    
    protected SupplierAllocationBuilder(Class<T> messageClazz, long numMessages) {
        super(messageClazz, numMessages);
        allocationClazz = messageClazz;
        this.setMessageLineEnricher(new SupplierAllocationEnricher());
    }

    public SupplierAllocationBuilder(Class<T> messageClazz, Iterable<Item> item, Iterable<Calendar> calendar, Partner enterprisePartner) {
        this(messageClazz, defaultNumMessages);
        this.itemList = Lists.newArrayList(item);
        this.calendar = calendar;
        this.enterprisePartner = enterprisePartner;
    }

	@Override
    public Iterable<T> build() {
    	generate();
    	setNumMessages(supplierAllocationData.size());
        return super.build();
    }
    
    protected class SupplierAllocationEnricher implements MessageLineEnricher<T> {

    	/**
    	 * Sets the data for a single line.  
    	 * 
    	 * @param messageLine	The message line
    	 * @param lineNumber	The line number
    	 * @return
    	 */
        @Override
        public T enrichMessageLine(T messageLine, long lineNumber) {
        	messageLine = supplierAllocationData.get((int) lineNumber);
            return messageLine;
        }
        
    }
    
   
	
    protected void generate()	{
    	supplierAllocationData = new ArrayList<T>();
    	
   	
		int allocationSum = 0;
		int allocationValue;
		
		for (int row = 0; row < itemList.size(); row++)	{
			if (itemList.get(row).getApprovedVendorListItem() != null){
				allocationValue = 100 / itemList.get(row).getApprovedVendorListItem().size();
				allocationSum = 0;
				
				for (int supplier = 0; supplier < itemList.get(row).getApprovedVendorListItem().size(); supplier++)	{
					
					T allocation = getBaseData (itemList.get(row), itemList.get(row).getApprovedVendorListItem().get(supplier));

					if (supplier == (itemList.get(row).getApprovedVendorListItem().size()-1)){
						allocation.setAllocation(100 - allocationSum);
					} else	{
						allocation.setAllocation(allocationValue + RandomUtils.rand(-10, 10));
						allocationSum += allocation.getAllocation();
					}
					
					supplierAllocationData.add(allocation);	
				}
			}
		}

    }
    
    
    protected T getBaseData(Item item, ApprovedVendorListItem approvedVendorListItem)	{
    	T messageLine = (T) T.Factory.newInstance();
    	
    	Calendar calendar = getCalendarDate(DateTime.now().withTimeAtStartOfDay());
		if (calendar == null){
			JLog.error(this.getClass() + ".getBaseData() - Unable to find Calendar for current date: " + DateTime.now().withTimeAtStartOfDay());
			calendar = Calendar.Factory.newInstance();
			calendar.setMonth_StartDate(DateTime.now().minusMonths(1));
			calendar.setMonth_EndDate(DateTime.now().plusMonths(1));
		}
		
    	messageLine.setCustomerItemGroupIdentifier(item.getItemIdentifier());
    	messageLine.setCustomerItemIdentifier( item.getItemIdentifier());
    	messageLine.setCustomerItemDescription(approvedVendorListItem.getDescription());
    	messageLine.setCustomerBusinessEntity(item.getBusinessEntity());
    	messageLine.setCustomerBusinessEntityType(item.getBusinessEntityType());
    	messageLine.setCustomerSite(enterprisePartner.getUdf1());
    	messageLine.setSupplierItemIdentifier(approvedVendorListItem.getVendorItemIdentifier());
    	messageLine.setSupplierBusinessEntity(approvedVendorListItem.getVendorBusinessEntity());
    	messageLine.setSupplierBusinessEntityType(approvedVendorListItem.getVendorBusinessEntityType());
    	messageLine.setSupplierSite(approvedVendorListItem.getSite());
    	messageLine.setDescription(approvedVendorListItem.getDescription());
    	messageLine.setAllocation(RandomUtils.rand(10, 20));
    	messageLine.setEffectiveFromDate(calendar.getMonth_StartDate().withTimeAtStartOfDay());
    	messageLine.setEffectiveToDate(calendar.getMonth_EndDate().withTimeAtStartOfDay());
//    	messageLine.setContextType("All");
//    	messageLine.setContextBusinessEntity();
//    	messageLine.setContextBusinessEntityType();
//    	messageLine.setContextItemIdentifier();
//    	messageLine.setContextPlatformName();
//    	messageLine.setContextPlatformType();
    	messageLine.setItemUniqueId(item.getItemUniqueId());
    	messageLine.setCustomerItemDescription(approvedVendorListItem.getDescription());
    	messageLine.setItemRevision(item.getRevision());
    	messageLine.setItemVersion(item.getVersion());
    	messageLine.setItemGroupVersion(null);
    	messageLine.setItemGroupRevision(null);
    	messageLine.setSupplierItemUniqueId(approvedVendorListItem.getVendorItemUniqueId());
    	messageLine.setSupplierItemRevision(approvedVendorListItem.getVendorRevision());
    	messageLine.setSupplierItemVersion(approvedVendorListItem.getVendorVersion());
    	messageLine.setComment("Supplier Allocation as of " + approvedVendorListItem.getLastUpdateDate());
    	messageLine.setOperationCode(null);
    	messageLine.setDataSource(item.getDataSource());
    	
    	return (T) messageLine;
    }
    
	protected Calendar getCalendarDate (DateTime date)	{
		CalendarUtils utils = new CalendarUtils();
		return utils.findByDate(this.calendar, date);
	}
	
	
    //===========================================
    // CHAINED CALLS
    //===========================================
    
    /**
     * Sets the line count. ADD DETAILS ON WHAT EACH LINE WILL DO
     * 
     * @param lines	The number of lines to create.
     * @return
     * 
     */
//    public SupplierAllocationBuilder<T> withLines(long lines) {
//        setNumMessages(lines);
//        return this;
//    }
    

}
