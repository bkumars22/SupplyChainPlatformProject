/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.businessEntity;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;

/**
 * @author dgenrich
 *
 * @param <T>	BusinessEntity or any class that extends it
 * 
 * @see MessageWriter
 */
public class BusinessEntityWriter<T extends BusinessEntity> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The BusinessEntity Message class, typically BusinessEntity.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link BusinessEntityBuilder}
	 */
	public BusinessEntityWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}

}
