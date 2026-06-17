/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.commodityCode;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;

/**
 * @author dgenrich
 *
 * @param <T>	CommodityCode or any class that extends it
 * 
 * @see MessageWriter
 */
public class CommodityCodeWriter<T extends CommodityCode> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The CommodityCode Message class, typically CommodityCode.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link CommodityCodeBuilder}
	 */
	public CommodityCodeWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}

}
