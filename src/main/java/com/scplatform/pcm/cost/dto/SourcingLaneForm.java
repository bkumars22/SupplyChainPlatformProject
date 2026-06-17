/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import com.scplatform.pcm.searchframework.dto.SearchForm;

public class SourcingLaneForm extends SearchForm
{
	private boolean unsavedData;
	private String[] selectedLineKeys; 
	protected List<PcmSourcingLane> lanes = new ArrayList<PcmSourcingLane>();
	private Set<Site> toSites;
	
	public void reset(HttpServletRequest request)
	{
		super.reset(request);
		selectedLineKeys = null;
		unsavedData = false;
	}

	public String[] getSelectedLineKeys()
	{
		return selectedLineKeys;
	}

	public void setSelectedLineKeys(String[] selectedLineKeys)
	{
		this.selectedLineKeys = selectedLineKeys;
	}

	public void clearLanes()
	{
		lanes = new ArrayList<PcmSourcingLane>();
	}
	
	public List<PcmSourcingLane> getLanes()
	{
		return lanes;
	}

	public void setLanes(List<PcmSourcingLane> lanes)
	{
		this.lanes = lanes;
	}

	public void setUnsavedData(boolean dataChanged)
	{
		this.unsavedData = dataChanged;
	}

	public boolean getUnsavedData()
	{
		return unsavedData;
	}

	public void setToSites(Set<Site> toSites)
	{
		this.toSites = toSites;		
	}

	public Set<Site>getToSites()
	{
		return this.toSites;
	}
	
	public String getLaneToSite(int index)
	{
		Site s =  lanes.get(index).getToSite();
		return (s != null) ? String.valueOf(s.getSiteKey()) : null;
	}
	
	public void setLaneToSite(int index, String siteKey)
	{
		Site s = null;
		if (siteKey != null)
		{	
			long fsKey = Long.valueOf(siteKey);
			for (Site fs: toSites)
			{
				if (fs.getSiteKey() == fsKey)
				{
					s = fs;
					break;
				}
			}			
		}
		lanes.get(index).setToSite(s);

	}
	
}
