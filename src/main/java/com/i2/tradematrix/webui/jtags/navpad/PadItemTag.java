/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.navpad;

import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.taglib.Pad;
import com.scplatform.testing.webui.taglib.Settings;
import java.util.ArrayList;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PadItemTag extends TagSupport {
	private static final Logger log_ = LoggerFactory.getLogger(PadItemTag.class);
	String name_;
	PadItemTree model_;

	public PadItemTag() {
		this.resetCustomAttributes();
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.name_ = null;
		this.model_ = null;
	}

	public void setName(String value) {
		this.name_ = value;
	}

	public int doStartTag() throws JspException {
		this.model_ = (PadItemTree) this.pageContext.findAttribute(this.name_);
		if (this.model_ == null) {
			log_.error("Missing nav pad item tree model for " + this.name_);
			return 0;
		} else {
			Pad pad = (Pad) findAncestorWithClass(this, Pad.class);
			if (pad != null) {
				this.model_.setId(pad.getName());
			}

			return 0;
		}
	}

	public int doEndTag() throws JspException {
		if (this.model_ == null) {
			return 6;
		} else {
			try {
				Settings settings = (Settings) this.pageContext.findAttribute("i2.settings");
				TagRenderer renderer = TagRendererFactory.getRenderer("paditem");
				renderer.render(this.model_, (BodyContent) null, this.pageContext.getOut(), settings);
				ArrayList stack = new ArrayList();

				for (int i = 0; i < this.model_.size(); ++i) {
					stack.add(this.model_.get(i));
				}

				while (true) {
					while (stack.size() > 0) {
						PadItem padItem = (PadItem) stack.remove(stack.size() - 1);
						if (padItem.isLeaf()) {
							if (padItem.isSelected()) {
								this.pageContext.setAttribute("Container.SelectedPadItem", padItem);
								return 6;
							}
						} else {
							for (int i = 0; i < padItem.size(); ++i) {
								stack.add(padItem.get(i));
							}
						}
					}

					return 6;
				}
			} catch (Exception var6) {
				log_.error(var6.getMessage(), var6);
				return 6;
			}
		}
	}

}