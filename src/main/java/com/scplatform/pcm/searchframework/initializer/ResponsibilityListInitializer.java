/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.responsibility.service.PcmResponsibilityService;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ResponsibilityListInitializer implements SearchParameterInitializer
{

    private final PcmResponsibilityService pcmResponsibilityService;

	List<String> responsibilityTypes = null;
	List<String> excludeResponsibilities = null;
	public boolean initializeParameter(SearchParameter parameter, Map context)
	{
		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;
			Role role = (Role)context.get(SearchParameterInitializer.ROLE);
			for (PcmResponsibility responsibility : pcmResponsibilityService
                    .findResponsibilities(role,responsibilityTypes,excludeResponsibilities))
			{
				list.addSelectValue(responsibility.getResponsibilityName(), responsibility.getResponsibilityKey());
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public void setInitialData(String data) {
		data = StringUtils.trimToNull(data);
		if (data != null) {
			try {
				JSONObject jo = new JSONObject("{" + data + "}");
				if (jo.has("responsibilityType")) {
					responsibilityTypes = asStringArray(jo,
							"responsibilityType");
				}
				if (jo.has("excludeResponsibility")) {
					excludeResponsibilities = asStringArray(jo,
							"excludeResponsibility");
				}
			} catch (JSONException e) {
				log.error("Unable to initialize list", e);
			}
		}
	}
	
	protected List<String> asStringArray(JSONObject jo,String key) 
		throws JSONException
	{
		List<String> result = new ArrayList<String>();
		Object value = jo.get(key);
		if (value != null)
		{
			if (value instanceof JSONArray)
			{
				JSONArray array = (JSONArray)value;
				for (int idx=0; idx <array.length(); idx++)
				{
					result.add(array.getString(idx));
				}
			}
			else
			{
				result.add(value.toString());
			}
		}
		return result;
	}
}
