/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemPlatform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.MessageWriterException;
import com.scplatform.qa.iris.model.FieldDefinition;
import com.scplatform.qa.iris.model.MessageLineStructure;
import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.messages.itemPlatform.ItemPlatform;

/**
 * @author dgenrich
 *
 * @param <T>	ItemPlatform or any class that extends it
 * 
 * @see MessageWriter
 */
public class ItemPlatformWriter<T extends ItemPlatform> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The ItemPlatform Message class, typically ItemPlatform.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ItemPlatformBuilder}
	 */
	public ItemPlatformWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}
	@Override
	public String generate() throws BeansException, MessageWriterException, IOException	{
		this.fieldsToSerialize = getFieldDefinitions();
		return super.generate();
	}

	private FieldDefinition[] getExcelUploadFields() {
		
		List<FieldDefinition> fields = new ArrayList<FieldDefinition>();
		fields.add(ItemPlatform.Fields.ITEMIDENTIFIER);
		fields.add(ItemPlatform.Fields.BUSINESSENTITY);
		fields.add(ItemPlatform.Fields.BUSINESSENTITYTYPE);
		fields.add(ItemPlatform.Fields.PLATFORMNAME);
		fields.add(ItemPlatform.Fields.DESCRIPTION);
		fields.add(ItemPlatform.Fields.EFFECTIVEFROMDATE);
		fields.add(ItemPlatform.Fields.EFFECTIVETODATE);		
		return fields.toArray(new FieldDefinition[fields.size()]);
	}
	
	/**
	 * Why do we have {@link #getExcelUploadFields()} AND {@link #getFieldDefinitions()}?
	 * The {@link #getExcelUploadFields()} would work fine for must uploads.  However, if 
	 * you look at how one of the ItemPlatform.Fields are defined:<br>
	 * <code>public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");</code>
	 * Only the fieldName is defined.  This is fine for most cases.  However, in this case for the MTCM Excel file,
	 * I need the fieldName and the displayName.  This code gets the full FieldDefinition that is defined for each
	 * class member.
	 * @return
	 */
	private FieldDefinition[] getFieldDefinitions()	{
		FieldDefinition[] fields = getExcelUploadFields();
		List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
		
		ItemPlatform ItemPlatformInstance = ItemPlatform.Factory.newInstance();
		MessageLineStructure<ItemPlatform> mls = (MessageLineStructure<ItemPlatform>) ItemPlatformInstance.getMessageStructure();
		try {
			for (FieldDefinition fd : fields)	{
				fieldDefinitions.add(mls.getFieldDefinitionByName(fd.getFieldName()));
			}
		} catch (FieldNotFoundException e) {
			JLog.error(e.getMessage());
		}
		
		return fieldDefinitions.toArray(new FieldDefinition[fieldDefinitions.size()]);
	}

}
