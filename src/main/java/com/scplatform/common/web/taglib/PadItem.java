/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2007, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.common.web.taglib;

import java.io.*;
import jakarta.servlet.jsp.*;
import jakarta.servlet.jsp.tagext.*;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.scplatform.testing.webui.taglib.Pad;
import com.scplatform.testing.webui.taglib.Settings;

/**
 * This is a version of the old i2 PatItem but this one allows
 * other tags to be nested.
 * @author suvasish.bhoi
 *
 */
@SuppressWarnings("serial")
public class PadItem extends BodyTagSupport
{
	String onclick = null;
	String onnavigate = null;
	String onexpand = null;
	String text = null;

	String target = null;

	String tooltip = null;

	boolean disabled = false;

	boolean bSelected = false;

	Settings settings;

	String name;

	int nameCount = 1; // relative to 1 for collapse routine to work

	String indent;

	public void setOnclick(String value)
	{
		onclick = StringUtils.trimToNull(value);
	}

	public void setOnnavigate(String value)
	{
		onnavigate = StringUtils.trimToNull(value);;
	}

	public void setOnexpand(String value)
	{
		onexpand = StringUtils.trimToNull(value);;
	}
	
	public void setTarget(String value)
	{
		target = StringUtils.trimToNull(value);;
	}

	public void setText(String value)
	{
		text = value;
	}

	public void setTooltip(String value)
	{
		tooltip = value;
	}

	public void setSelected(String value)
	{
		if (value.toLowerCase().equals("yes"))
		{
			bSelected = true;
		}
	}

	public void setDisabled(String value)
	{
		if (value.toLowerCase().equals("yes"))
		{
			disabled = true;
		}
	}

	public String buildName()
	{
		String itemName = name + "_" + nameCount;
		nameCount++;
		return (itemName);
	}

	public String getIndent()
	{
		return (indent);
	}

	public int doStartTag() throws JspException
	{
		String styleinfo;
		StringBuffer result = new StringBuffer();
		Pad pad = (Pad) findAncestorWithClass(this, Pad.class);
		PadItem parentItem = (PadItem) findAncestorWithClass(this, PadItem.class);
		settings = Settings.getSessionSettings((HttpServletRequest) pageContext.getRequest());
		if (parentItem != null)
		{
			indent = ((PadItem) parentItem).getIndent() + "&nbsp;&nbsp;";
			name = ((PadItem) parentItem).buildName();
			styleinfo = " class=\"" + pad.getType() + "PadContent1\"";
		}		
		else if (pad != null)
		{
			indent = "&nbsp;";
			name = pad.buildName();
			styleinfo = " class=\"" + pad.getType() + "PadContent0\"";
		}
		else
		{
			return EVAL_BODY_INCLUDE;
		}
			if (bSelected)
				styleinfo += " id=\"" + pad.getType() + "HighlightedPadContent\" ";
			result.append("<TR " + styleinfo + ">");
			result.append("<TD nowrap=\"yes\" id=\"TREECELL_" + name + "\">");
			result.append(indent + "<a href=\"javascript:i2uiManagePadTree('");
			result.append(pad.getName());
			result.append("','" + name + "',0,null,null,null,'i2uiTilePads()')\"");			
			if (onexpand != null)
			{
				result.append(" onclick=\"").append(onexpand).append("\"");
			}			
			result.append(">");			
			result.append("<img id=\"TREECELLIMAGE_" + pad.getName() + "_" + name
					+ "\" border=\"0\" src=\"" + settings.getImageDirectory()
					+ "/plus_norgie.gif\" style=\"display:none\">");
			if (onclick != null && !disabled)
			{
				result.append("<a href=\"" + onclick + "\"");
				if (target != null)
					result.append(" target=\"" + target + "\"");
				if (tooltip != null)
					result.append(" title=\"" + tooltip + "\"");
				result.append(" onclick=\"javascript:i2uiHighlightPadItem('TREECELL_");
				result.append(name);
				result.append("','");
				result.append(pad.getType());
				result.append("')");
				if (onnavigate != null)
				{
					result.append(";").append(onnavigate);
				}
				
				result.append("\">");
			}
			if (disabled)
				result.append("<SPAN class=\"linkDisabled\">");
			result.append("&nbsp;" + text);
			if (disabled)
				result.append("</SPAN>");
			if (onclick != null && !disabled)
				result.append("</a>");
			result.append("</a></td></tr>");
		try
		{
			pageContext.getOut().write(result.toString());
		}
		catch (IOException e)
		{
			throw new JspException("IO Error: " + e.getMessage());
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException
	{
		StringBuffer result = new StringBuffer();
		try
		{
			if (bodyContent != null)
			{
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
			}
		}
		catch (IOException e)
		{
			throw new JspException(e.getMessage());
		}
		try
		{
			// retain name of selected item in order to unhighlight it when
			// user selects another item
			if (bSelected)
			{
				Pad pad = (Pad) findAncestorWithClass(this, Pad.class);
				if (pad != null && !pad.getType().equals("solution")){
					result.append("<script>i2uiHighlightPadItem('TREECELL_" + name + "','"
						+ pad.getType() + "');</script>");
				}
			}			
			pageContext.getOut().write(result.toString());
		}
		catch (IOException e)
		{
			throw new JspException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	public void release()
	{
		super.release();
		onclick = null;
		onnavigate = null;
		onexpand = null;
		text = null;
		target = null;
		indent = null;
		bSelected = false;
		disabled = false;
		nameCount = 1;
	}
}
