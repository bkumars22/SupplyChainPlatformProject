/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.util.Iterator;

import jakarta.servlet.jsp.JspException;

import org.owasp.encoder.Encode;

import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.common.web.taglib.html.ErrorsTag;

@SuppressWarnings("serial")
public class EscapedErrorTag extends ErrorsTag {

	@SuppressWarnings("rawtypes")
	@Override
	public int doStartTag() throws JspException {
		// Were any error messages specified?
		UiMessages errors = null;

		try {
			errors = TagUtils.getInstance().getMessages(pageContext, name);
		} catch (JspException e) {
			TagUtils.getInstance().saveException(pageContext, e);
			throw e;
		}

		if ((errors == null) || errors.isEmpty()) {
			return (EVAL_BODY_INCLUDE);
		}

		boolean headerPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getHeader());

		boolean footerPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getFooter());

		boolean prefixPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getPrefix());

		boolean suffixPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getSuffix());

		// Render the error messages appropriately
		StringBuffer results = new StringBuffer();
		boolean headerDone = false;
		String message = null;
		Iterator reports = (property == null) ? errors.get() : errors.get(property);
		int iteratorSize = 0;
		Iterator count = (property == null) ? errors.get() : errors.get(property);
		while (count.hasNext()) {
			count.next();
			iteratorSize++;
        }			

		while (reports.hasNext()) {
			String report = (String) reports.next();

			if (!headerDone) {
				if (headerPresent) {
					message = TagUtils.getInstance().message(pageContext, bundle, locale, getHeader());

					/* results.append(message); */
				}

				headerDone = true;
			}

			if (prefixPresent && iteratorSize > 1) { //li
				message = TagUtils.getInstance().message(pageContext, bundle, locale, getPrefix());
				results.append(message);
			}

			message = report;

			if (message != null) {
				if (message.length() > 140) {
					String messageFirstPart = message.substring(0, 140);
					String messageRemaining = message.substring(140);
					StringBuilder span = new StringBuilder();
					span.append("<span title='");
					span.append(Encode.forHtmlAttribute(message));
					span.append("'>");
					span.append(Encode.forHtmlContent(messageFirstPart));
					span.append("</span>");
					span.append(
							"<span style=\"cursor: pointer;\" title=\"Click to expand\" onclick=\"$(this).next('span').css('display','');$(this).css('display','none');\">");
					span.append("...</span>");
					span.append(
							" <span style=\"cursor: pointer; white-space: normal;display: none;\" title=\"Click to contract\" onclick=\"$(this).prev('span').css('display','');$(this).css('display','none');\">");
					span.append(Encode.forHtmlContent(messageRemaining));
					span.append("</span>");
					results.append(span.toString());
				} else {
					results.append(Encode.forHtmlContent(message));
				}
			}

			if (suffixPresent && iteratorSize > 1) { //<li>
				message = TagUtils.getInstance().message(pageContext, bundle, locale, getSuffix());
				results.append(message);
			}
		}

		/*
		 * if (headerDone && footerPresent) { message =
		 * TagUtils.getInstance().message(pageContext, bundle, locale, getFooter());
		 * results.append(message); }
		 */
		TagUtils.getInstance().write(pageContext, results.toString());

		return (EVAL_BODY_INCLUDE);
	}

}
