/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.businessEntity;

import java.util.ArrayList;
import java.util.List;

import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.scplatform.qa.iris.model.exceptions.InvalidValueException;
import com.test.selenium.common.Partner;

public class BusinessEntityUtils {

	public List<BusinessEntity> removePartners(List<BusinessEntity> businessEntityData, List<Partner> removePartners) throws FieldNotFoundException, InvalidValueException	{
		List<BusinessEntity> newBusinessEntityData = new ArrayList<BusinessEntity>();
		
		for (BusinessEntity data : businessEntityData)	{
			if (!isMatch(data, removePartners))	{
				newBusinessEntityData.add(BusinessEntity.Factory.clone(data));
			}
		}
		
		return newBusinessEntityData;
	}

	private boolean isMatch(BusinessEntity data, List<Partner> inPartnerList) {
		boolean match = false;
		for (Partner partner : inPartnerList)	{
			if (partner.getDuns().equals(data.getBusinessEntityExternalId())){
				if (partner.getDescription().equals(data.getDescription()))	{
					if (partner.getSite().equals(data.getSite_Site()))	{
						match = true;
						break;
					}
				}
			}
		}
		return match;
	}
	
	public List<String> getUniquePartners(List<BusinessEntity> businessEntityData)	{
		List<String> businessEntityTypes = new ArrayList<String>();
		businessEntityTypes.add("Enterprise");
		businessEntityTypes.add("Operator");
		businessEntityTypes.add("Manufacturer");
		businessEntityTypes.add("Supplier");
		return getUniquePartners(businessEntityData, businessEntityTypes);
	}
	
	public List<String> getUniquePartners(List<BusinessEntity> businessEntityData, List<String> businessEntityTypes)	{
		List<String> uniquePartners = new ArrayList<String>();
		
		BusinessEntityFilter<BusinessEntity> filter = new BusinessEntityFilter<BusinessEntity>();
		for (String entityType : businessEntityTypes){
			filter.byBusinessEntityType(entityType.toUpperCase());
		}
		List<BusinessEntity> businessEntityDataFiltered = filter.applyReturnList(businessEntityData);

		for (BusinessEntity data : businessEntityDataFiltered){
			if (!uniquePartners.contains(data.getBusinessEntityName()))	{
				uniquePartners.add(data.getBusinessEntityName());
			}
		}
		
		return uniquePartners;
	}
}
