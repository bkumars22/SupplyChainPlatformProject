/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
public class ScrollableTableHeaderTag extends BodyTagSupport
{
	private String id;
	private String style;
	private String styleClass;;
	
	public int doStartTag() throws JspException
	{
		JspWriter writer = pageContext.getOut();
		try
		{
			writer.print("<thead");
			if (StringUtils.isBlank(id) == false)
			{
				writer.print(" id='" +id + "'");
			}
			writer.print(" class='scrollableTableHeader");
			if (StringUtils.isBlank(styleClass) == false)
			{
				writer.print(" ");
				writer.print(styleClass);
			}
			writer.print("'");
			
			writer.println(">");
		}
		catch (IOException e)
		{
			throw new JspException(e.toString());			
		}
		
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException
	{
		JspWriter writer = pageContext.getOut();
		try
		{
			writer.println("</thead>");
		}
		catch (IOException e)
		{
			throw new JspException(e.toString());			
		}
		return super.doEndTag();
	}


	public void setId(String id)
	{
		this.id = id;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

}
