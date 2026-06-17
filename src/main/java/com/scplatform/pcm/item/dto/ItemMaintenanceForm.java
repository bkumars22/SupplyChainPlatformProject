/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.item.dto;

import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Stack;

public class ItemMaintenanceForm extends SearchForm
{
	private String selectedItemKey;
	private String displayedItemKey;	
	private String drilldownItemKey;
	private Stack drilldownStack = new Stack();	
	private Item selectedItem;
	private boolean itemMarkedForDelete;
	private String itemDescription;
	private Long[] itemCategories;
	private String[] selectedAvls;
	private boolean readOnly;
	private String selectedTabId;
	private List<ItemCategory> availableCategories;
	private Boolean unsavedData;
	
	public boolean getReadOnly()
	{
		return readOnly;
	}
	
	public void setSelectedItemKey(String selectedItemKey)
	{
		this.selectedItemKey = selectedItemKey;
	}
	public String getSelectedItemKey()
	{
		return selectedItemKey;
	}
	
	/*public void setSelectedItem(Item selectedItem)
	{
		this.selectedItem = selectedItem;
		readOnly = false;
		if (selectedItem != null)
		{
			if (selectedItem.getDeleteFlag())
			{
				setItemMarkedForDelete(selectedItem.getDeleteFlag().booleanValue());
			}
			setItemDescription(selectedItem.getDescription());
			List<Long> catKeys = new ArrayList<Long>();
			for (ItemCategory c : selectedItem.getCategories())
			{
				catKeys.add(c.getCategoryKey());
			}
			setItemCategories(catKeys.toArray(new Long[]{}));
			setSelectedItemKey(selectedItem.getItemKey().toString());
			
			List<String> readOnlyDS = ConfigurationUtils.getList("pcm.upload.readonly.datasources", null);
			if (readOnlyDS != null && readOnlyDS.contains(selectedItem.getDataSource()))
			{
				readOnly = true;
			}
			if (ConfigurationUtils.getBoolean("pcm.upload.readonly.enterprise",true))
			{
				Long entKey = BomUtil.getEnterpriseBusinessEntity().getBusinessEntityKey();
				if (selectedItem.getBusinessEntity().getBusinessEntityKey().equals(entKey))
				{
					readOnly = true;
				}
			}
		}	
		
	}*/
	public Item getSelectedItem()
	{
		return selectedItem;
	}
	public void setItemMarkedForDelete(boolean deletedItem)
	{
		this.itemMarkedForDelete = deletedItem;
	}
	public boolean getItemMarkedForDelete()
	{
		return itemMarkedForDelete;
	}
	public void setItemDescription(String itemDescription)
	{
		this.itemDescription = itemDescription;
	}
	public String getItemDescription()
	{
		return itemDescription;
	}
	public void setItemCategories(Long[] itemCategories)
	{
		this.itemCategories = itemCategories;
	}
	public Long[] getItemCategories()
	{
		return itemCategories;
	}
	public void setDrilldownItemKey(String drilldownItemKey)
	{
		this.drilldownItemKey = drilldownItemKey;
	}
	public String getDrilldownItemKey()
	{
		return drilldownItemKey;
	}
	public void setDisplayedItemKey(String displayedItemKey)
	{
		this.displayedItemKey = displayedItemKey;
	}
	public String getDisplayedItemKey()
	{
		return displayedItemKey;
	}

	public void setDrilldownStack(Stack drilldownStack)
	{
		this.drilldownStack = drilldownStack;
	}
	public Stack getDrilldownStack()
	{
		return drilldownStack;
	}
	public boolean getIsBackEnabled()
	{
		return drilldownStack.size() > 0;
	}
	
	public String[] getSelectedAvls()
	{
		return selectedAvls;
	}
	public void setSelectedAvls(String[] selectedAvls)
	{
		this.selectedAvls = selectedAvls;
	}
	
	public List<ItemCategory> getAvailableCategories()
	{
		return availableCategories;
	}

	public void setAvailableCategories(List<ItemCategory> availableCategories)
	{
		this.availableCategories = availableCategories;
	}
	
	@Override
	public void clearSelection()
	{
		selectedItemKey = null;
		
		super.clearSelection();
	}

	public String getSelectedTabId()
	{
		return selectedTabId;
	}

	public void setSelectedTabId(String selectedTabId)
	{
		this.selectedTabId = selectedTabId;
	}
	
	public void reset(HttpServletRequest request)
	{
		itemMarkedForDelete = false;
		selectedItem = null;
		selectedItemKey = null;
		displayedItemKey = null;
		drilldownItemKey = null;
		selectedAvls = null;
		itemCategories = null;
		selectedTabId = "resultTab";
		unsavedData = false;
		super.reset(request);
	}
	
	public Boolean getUnsavedData() {
		return unsavedData;
	}

	public void setUnsavedData(Boolean unsavedData) {
		this.unsavedData = unsavedData;
	}
	
}
