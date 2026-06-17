/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.item;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.Partner;

public class ItemUtils {

	public List<Item> findByPartner(List<Item> itemData, Partner partner)	{
		List<Item> itemsForPartner = new ArrayList<Item>();
		
		for (Item item : itemData)	{
			if (item.getBusinessEntity().equals(partner.getDuns()))	{
				itemsForPartner.add(item);
			}
			
			if ( (item.getApprovedVendorListItem() != null) && (!item.getApprovedVendorListItem().isEmpty()) )	{
				for (int avli = 0; avli < item.getApprovedVendorListItem().size(); avli++)	{
					if (item.getApprovedVendorListItem().get(avli).getVendorBusinessEntity().equals(partner.getDuns())) {
						if (item.getApprovedVendorListItem().get(avli).getSite().equals(partner.getUdf1())) {
							itemsForPartner.add(item);
						}
					}
				}
			}
			
			if ( (item.getApprovedManufacturerListItem() != null) && (!item.getApprovedManufacturerListItem().isEmpty()) )	{
				for (int avli = 0; avli < item.getApprovedVendorListItem().size(); avli++)	{
					if (item.getApprovedManufacturerListItem().get(avli).getManufacturerBusinessEntity().equals(partner.getDuns())) {
						if (item.getApprovedManufacturerListItem().get(avli).getSite().equals(partner.getUdf1())) {
							itemsForPartner.add(item);
						}
					}
				}
			}
			
		}
		return itemsForPartner;
	}
}
