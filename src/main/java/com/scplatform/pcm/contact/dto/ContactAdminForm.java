/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.contact.dto;

import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ContactAdminForm extends SearchForm
{
	private String selectedContactKey = null;
	private Contact selectedContact;
	private String selectedBusinessKey;
	private Map businessContacts = new HashMap();
	private boolean goInit=false;

}
