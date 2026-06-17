/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.accessControl.dto;

public class ACLType
{
	String action;
	String requiredBy;
	String requires;
	String xor;
	String businessObjectType;
	
	public ACLType(String action, String requiredBy, String requires, String xor)
	{
		super();
		this.action = action;
		this.requiredBy = requiredBy;
		this.requires = requires;
		this.xor = xor;
	}
	
	public String getBusinessObjectType()
	{
		return businessObjectType;
	}
	
	public void setBusinessObjectType(String type)
	{
		this.businessObjectType = type;
	}
	public String getAction()
	{
		return action;
	}
	public void setAction(String action)
	{
		this.action = action;
	}
	public String getRequiredBy()
	{
		return requiredBy;
	}
	public void setRequiredBy(String requiredBy)
	{
		this.requiredBy = requiredBy;
	}
	public String getRequires()
	{
		return requires;
	}
	public void setRequires(String requires)
	{
		this.requires = requires;
	}
	public String getXor()
	{
		return xor;
	}
	public void setXor(String xor)
	{
		this.xor = xor;
	}
	
	public String getValidator()
	{
		StringBuilder builder = new StringBuilder();
		if (xor != null)
		{
			builder.append("handleXOR(this,'");
			builder.append(xor);
			if (businessObjectType != null)
			{
				builder.append("','").append(businessObjectType);
			}			
			builder.append("');");
		}
		if (requires != null)
		{
			builder.append("handleRequires(this,'");
			builder.append(requires);
			if (businessObjectType != null)
			{
				builder.append("','").append(businessObjectType);
			}			
			builder.append("');");
		}			
		if (requiredBy != null)
		{
			builder.append("handleRequiredBy(this,'");
			builder.append(requiredBy);
			if (businessObjectType != null)
			{
				builder.append("','").append(businessObjectType);
			}			
			builder.append("');");
		}
		return builder.toString();
	}
	
}