/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.common.web.taglib;

import com.scplatform.pcm.util.common.SCPlatformConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.scplatform.testing.webui.taglib.Settings;

@SuppressWarnings("serial")
public class Shell extends TagSupport
{
    private final static Logger logger = LogManager.getLogger(Shell.class);

    String logo = null;
    String logoBackground = null;
    String logoText=null;
    String background = null;
    String productName = null;
    String username = null;
    String rolename = null;
    String actions = null;
    String contenturl = null;
    String onresize = "";
    String onresize2 = "";
    String onload = "";
    String onload2 = null;
    boolean framed = false;
    Settings settings;

    public void setLogo(String value)
    {
        logo = value;
    }

    public void setLogoBackground(String value)
    {
        logoBackground = value;
    }

    public void setLogoText(String text)
    {
        logoText = text;
    }

    public void setBackground(String value)
    {
        background = value;
    }

    public void setProductName(String value)
    {
        productName = value;
    }

    public void setRolename(String value)
    {
        rolename = value;
    }

    public void setUsername(String value)
    {
        username = value;
    }

    public void setActions(String value)
    {
        actions = value;
    }

    public void setOnresize(String value)
    {
        onresize = "onresize=\"" + value + "\" ";
        onresize2 = value + ";";
    }

    public void setOnload(String value)
    {
        onload = "onload=\"" + value + "\" ";
        onload2 = value + ";";
    }

    public void setContenturl(String value)
    {
        contenturl = value;
    }

    public void setFramed(String value)
    {
        if (value.toLowerCase().equals("yes"))
        {
            framed = true;
        }
    }

    public int doStartTag() throws JspException {
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        if (actions == null)
        {
            actions = "";
        }
        if (username == null)
        {
            username = "";
        }
        if (rolename == null)
        {
            rolename = "";
        }
        if (productName == null)
        {
            productName = "";
        }
        if (logoText == null)
        {
            logoText = "&nbsp;";
        }
        settings = Settings.getSessionSettings((HttpServletRequest) pageContext.getRequest());
        String css = settings.getDefaultCSSStyleSheet();
        if (framed)
        {
            onload = "";
            onresize = "";
            result1.append("<script language=\"javascript\">");
            result1.append(" function i2ui_shell_init(action){");
            result1.append(" var content;");
            // result1.append(" if (document.layers && action==\"load\")");
            // result1.append(
            // " { window.outerWidth=1024; window.outerHeight=768;}");
            result1.append(" content='");
            if (css != null)
            {
                result1.append("<HEAD><LINK rel=\"STYLESHEET\" type=\"text/css\" href=\"");
                result1.append(css);
                result1.append("\"></HEAD>");
            }
        }
        result1.append("<BODY ").append(onload).append(" ").append(onresize);
        result1.append(" topmargin=\"0\" leftmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" class=\"shellBody\"");

        if (background != null)
        {
            result1.append(" background=\"").append(background).append("\"");
        }
        result1.append(">");
        // draw row containing logo image, username and action values
        result1.append("<TABLE width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"shellBackground\">");
        boolean launcpadEnabled = Boolean.valueOf(System.getProperty("mtcm.launchpad.enabled"));
        if (!launcpadEnabled) {
            result1.append("<TR><TD colspan='3'><div id='header-parent'><div id='header-example'></div></div></TD></TR>");
        } else {
            result1.append("<TR><TD colspan='3'><div id='header-parent'>")
                    .append("<header ")
                    .append(" data-url='").append(System.getProperty("env.clp.url")).append("'")
                    .append(" data-tenant='").append(System.getProperty("e2.tenant.name")).append("'")
                    .append(" data-solution='").append(System.getProperty("e2.solution.name")).append("'")
                    .append(" data-stack='").append(System.getProperty("e2.stack.id")).append("'")
                    .append(" data-version='").append("v1").append("'")
                    .append(" data-is-launchpad='").append("true").append("'")
                    .append(" data-current-app='").append(SCPlatformConstant.SC_APP).append("'")
                    .append(" id='").append("react-app").append("'")
                    .append(" style='").append("width:100%>").append("'")
                    .append("</header>")
                    .append("</div></TD></TR>");
        }


        if (framed)
        {
            result1.append("</TABLE></BODY>';");
            result1.append("i2ui_shell_top.document.open();");
            result1.append("i2ui_shell_top.document.write(content);");
            result1.append("i2ui_shell_top.document.close();");
            result2.append(" content='");
            result2.append("<BODY style=\"background-repeat:repeat-y;background-image:url("
                    + settings.getImageDirectory() + "/outerborder_leftbar.gif)\"></BODY>';");
            result2.append("i2ui_shell_left.document.open();");
            result2.append("i2ui_shell_left.document.write(content);");
            result2.append("i2ui_shell_left.document.close();");
        }
        else
        {
            // draw left portion row containing middle of content area
            result2.append("<TD height=\"100%\" class=\"shellContent\" valign=\"top\">");
        }
        try
        {
            pageContext.getOut().write(result1.toString());
            pageContext.getOut().write(result2.toString());
        }
        catch (java.io.IOException e)
        {
            throw new JspException("IO Error: " + e.getMessage());
        }
        // return EVAL_BODY_TAG;
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException
    {
        StringBuilder result1 = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        if (framed)
        {
            result1.append(" content='");
            result1.append("<BODY style=\"background-repeat:repeat-y;background-image:url("
                    + settings.getImageDirectory() + "/outerborder_rightbar.gif)\"></BODY>';");
        }
        else
        {
            result1.append("</TD>" + "</TR>");
        }
        if (framed)
        {
            result1.append("i2ui_shell_right.document.open();");
            result1.append("i2ui_shell_right.document.write(content);");
            result1.append("i2ui_shell_right.document.close();");
            result2.append(" content='");
            result2
                    .append("<BODY topmargin=\"0\" leftmargin=\"0\" marginwidth=\"0\" marginheight=\"0\"");
            if (background != null)
                result2.append(" background=\"" + background + "\"");
            result2
                    .append("><TABLE width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        }

        result2.append("</TABLE>" + "</BODY>");
        if (framed)
        {
            result2.append("';");
            result2.append("i2ui_shell_bottom.document.open();");
            result2.append("i2ui_shell_bottom.document.write(content);");
            result2.append("i2ui_shell_bottom.document.close();");
            if (onload2 != null)
                result2.append("if (action=='load') {" + onload2 + "}");
            if (onresize2 != null)
                result2.append("if (action=='resize') {" + onresize2 + "}");
            result2.append("}");
            result2.append("</script>");
            result2
                    .append("<frameset rows=\"46,*,12\" marginwidth=\"0\" border=\"0\" frameborder=\"0\" framespacing=\"0\" marginheight=\"0\" onload=\"i2ui_shell_init('load')\" onresize=\"i2ui_shell_init('resize')\">");
            result2
                    .append("<frame src=\""
                            + settings.getJavascriptDirectory()
                            + "/i2uiblank.html\" name=\"i2ui_shell_top\" scrolling=\"no\" frameborder=\"no\" noresize=\"yes\">");
            result2
                    .append("<frameset cols=\"12,*,12\" marginwidth=\"0\" border=\"0\" frameborder=\"0\" framespacing=\"0\" marginheight=\"0\">");
            result2
                    .append("<frame src=\""
                            + settings.getJavascriptDirectory()
                            + "/i2uiblank.html\"  name=\"i2ui_shell_left\" scrolling=\"no\" frameborder=\"no\" noresize=\"yes\">");
            if (contenturl == null)
                result2
                        .append("<frame src=\""
                                + settings.getJavascriptDirectory()
                                + "/i2uiblank.html\"  name=\"i2ui_shell_content\" scrolling=\"auto\" frameborder=\"no\" noresize=\"yes\">");
            else
                result2
                        .append("<frame src=\""
                                + contenturl
                                + "\" name=\"i2ui_shell_content\" scrolling=\"auto\" frameborder=\"no\" noresize=\"yes\">");
            result2
                    .append("<frame src=\""
                            + settings.getJavascriptDirectory()
                            + "/i2uiblank.html\"  name=\"i2ui_shell_right\" scrolling=\"no\" frameborder=\"no\" noresize=\"yes\">");
            result2.append("</frameset>");
            result2
                    .append("<frame src=\""
                            + settings.getJavascriptDirectory()
                            + "/i2uiblank.html\"  name=\"i2ui_shell_bottom\" scrolling=\"no\" frameborder=\"no\" noresize=\"yes\">");
            result2.append("</frameset>");
        }
        try
        {
            pageContext.getOut().write(result1.toString());
            pageContext.getOut().write(result2.toString());
        }
        catch (java.io.IOException e)
        {
            throw new JspException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

    public void release()
    {
        super.release();
        logo = null;
        background = null;
        username = null;
        actions = null;
        settings = null;
        onresize = "";
        onresize2 = "";
        onload = "";
        onload2 = null;
        framed = false;
    }
}
