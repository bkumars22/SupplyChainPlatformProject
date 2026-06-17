/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.RendererGroup;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PunctoRendererGroup implements RendererGroup {
	private static final Logger log_ = LoggerFactory.getLogger(PunctoRendererGroup.class);
	private HashMap renderers_ = new HashMap();

	public PunctoRendererGroup() throws TagRendererException {
		try {
			TagRenderer renderer = new ButtonTagRenderer();
            this.renderers_.put("button", renderer);
            TagRenderer e = new ButtonbarTagRenderer();
            this.renderers_.put("buttonbar", e);
            TagRenderer var4 = new ButtonbarDividerTagRenderer();
            this.renderers_.put("buttonbardivider", var4);
            TagRenderer var5 = new WizardTagRenderer();
            this.renderers_.put("wizard", var5);
            TagRenderer var6 = new ToolbarTagRenderer();
            this.renderers_.put("toolbarbutton", var6);
            TagRenderer var7 = new BreadcrumbsTagRenderer();
            this.renderers_.put("breadcrumbs", var7);
            TagRenderer var8 = new PadItemTagRenderer();
            this.renderers_.put("paditem", var8);
		} catch (Exception var2) {
			log_.error(var2.getMessage(), var2);
			throw new TagRendererException(var2.getMessage());
		}
	}

	public Map getRenderers() {
		return this.renderers_;
	}

	public int size() {
		return this.renderers_.size();
	}

	public String toString() {
		return "Puncto renderer group";
	}

}