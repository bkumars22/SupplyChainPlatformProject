/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.dto;

/**
 * Provides a definition of a search criteria
 * @author bblasko
 *
 */
public class SearchExpression
{
	public static enum OperatorType {EQ,IEQ,NE,GT,GTE,LT,LTE,LIKE,ILIKE,IN,FIXED};
	
	protected OperatorType operator;
	private String expression;
	private String dataType;
	
	public SearchExpression(String expression)
	{
		this.expression = expression;
		this.operator = null;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setOperator(OperatorType operator)
	{
		this.operator = operator;
	}

	public OperatorType getOperator()
	{
		return operator;
	}
	
	public boolean getSupportsWildcard()
	{
		return (operator == OperatorType.ILIKE || operator == OperatorType.LIKE);
	}


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
