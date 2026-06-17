/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.apache.commons.lang3.*;
public class ScrollableTableBodyTag extends BodyTagSupport
{
	private String id;
	private String style;
	private int currentRowCount;
	
	public int doStartTag() throws JspException
	{
		JspWriter writer = pageContext.getOut();
		try
		{
			writer.print("<tbody");
			if (StringUtils.isBlank(id) == false)
			{
				writer.print(" id='" +id + "'");
			}
			if (getBrowserType().indexOf("IE") == -1)
			{
				ScrollableTableTag ptag = (ScrollableTableTag)findAncestorWithClass(this, ScrollableTableTag.class);
				if (ptag != null)
				{
				   writer.print(" style='");
				   if (StringUtils.isNotBlank(style))
				   {
					   writer.print(style);
					   writer.print(";");
				   }
				   writer.print("height:" + ptag.getHeight() +";'");	
				}
			}
			writer.println(">");
			currentRowCount = 0;
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
			writer.println("</tbody>");
		}
		catch (IOException e)
		{
			throw new JspException(e.toString());			
		}
		currentRowCount = 0;		
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

	public String getBrowserType()
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String userAgent = request.getHeader("User-Agent");
		return (userAgent != null) ? userAgent:"UNKNOWN";		
	}

	public void setCurrentRowCount(int currentRowCount)
	{
		this.currentRowCount = currentRowCount;
	}

	public int getCurrentRowCount()
	{
		return currentRowCount;
	}
}
