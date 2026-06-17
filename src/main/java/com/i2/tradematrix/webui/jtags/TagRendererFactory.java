/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagRendererFactory {
	private static final Logger log_ = LoggerFactory.getLogger(TagRendererFactory.class);
	private static Map renderers_ = null;
	private static boolean ready_ = false;
	private static RendererGroup theGroup_ = null;
	private static Map rendererGroups_ = new HashMap();

	private TagRendererFactory() {
	}

	public static void setRendererGroup(String rendererGroupClassName) throws TagRendererException {
		try {
			ready_ = false;
			Class theClass = Class.forName(rendererGroupClassName);
			RendererGroup theGroup_ = (RendererGroup) rendererGroups_.get(theClass);
			if (theGroup_ == null) {
				theGroup_ = (RendererGroup) theClass.newInstance();
			}

			Map theRenderers = theGroup_.getRenderers();
			renderers_ = new HashMap();
			renderers_.putAll(theRenderers);
			ready_ = true;
		} catch (Exception var4) {
			log_.error(var4.getMessage(), var4);
			throw new TagRendererException("Unable to load renderers for " + rendererGroupClassName);
		}
	}

	public static TagRenderer getRenderer(String rendererName) throws TagRendererException {
		if (!ready_) {
			setRendererGroup("com.scplatform.testing.webui.jtags.renderers.puncto.PunctoRendererGroup");
		}

		TagRenderer renderer = (TagRenderer) renderers_.get(rendererName);
		if (log_.isDebugEnabled()) {
			log_.debug("renderer for " + rendererName + " = " + renderer);
		}

		return renderer;
	}

	public static void debug() {
		log_.debug("renderer group = " + theGroup_);
		log_.debug("number of renderers = " + renderers_.size());
		log_.debug("factory is ready? = " + ready_);
	}

}