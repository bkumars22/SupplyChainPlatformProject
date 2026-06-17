/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.contact;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;

/**
 * @author dgenrich
 *
 * @param <T>	Contact or any class that extends it
 * 
 * @see MessageWriter
 */
public class ContactWriter<T extends Contact> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The Contact Message class, typically Contact.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ContactBuilder}
	 */
	public ContactWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}

}
