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
public class ScrollableTableTag extends BodyTagSupport
{
	private String width;
	private String height;
	private String id;
	private String style;

	protected String buildStyleClause()
	{
		String work = "";
		if (StringUtils.isBlank(height) == false)
		{
			if (getBrowserType().indexOf("IE") > 0)
			{				
				work += "height:" + height + ";";
			}
		}
		if (StringUtils.isBlank(style) == false)
		{
			work += style+";";
		}
		
		if (width != null && "auto".equals(width) == false)
		{
			work += "width:" + width + ";";
		}
		
		if (work.length() > 0)
		{
			return " style='" + work + "'";
		}
		return "";
	}
	
	public int doStartTag() throws JspException
	{
		JspWriter writer = pageContext.getOut();
		try
		{
			writer.print("<div class='scrollableTable'");
			writer.print(buildStyleClause());
			if (StringUtils.isBlank(id) == false)
			{
				writer.print(" id='" +id + "'");
			}
			writer.println(">");		
			
			writer.println("<table ");
			if (StringUtils.isBlank(id) == false)
			{
				writer.print(" id='" +id + "_data'");
			}				
			writer.println("cellspacing='0'>");
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
			writer.println("</table></div>");
		}
		catch (IOException e)
		{
			throw new JspException(e.toString());			
		}
		return super.doEndTag();
	}

	public void setWidth(String width)
	{
		this.width = width;
	}

	public void setHeight(String height)
	{
		this.height = height;
	}

	protected String getHeight()
	{
		return height;
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
}
