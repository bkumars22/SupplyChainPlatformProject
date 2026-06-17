/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.itemAVL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.MessageWriterException;
import com.scplatform.qa.iris.model.FieldDefinition;
import com.scplatform.qa.iris.model.MessageLine;
import com.scplatform.qa.iris.model.MessageLineStructure;
import com.scplatform.qa.iris.model.exceptions.FieldNotFoundException;
import com.test.selenium.common.JLog;

/**
 * @author dgenrich
 *
 * @param <T>	ItemAVL or any class that extends it
 * 
 * @see MessageWriter
 */
public class ItemAVLWriter<T extends ItemAVL> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The ItemAVL Message class, typically ItemAVL.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ItemAVLBuilder}
	 */
	public ItemAVLWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}

	@Override
	public String generate() throws BeansException, MessageWriterException, IOException	{
		this.fieldsToSerialize = getFieldDefinitions();
		return super.generate();
	}

	private FieldDefinition[] getExcelUploadFields() {
		
		List<FieldDefinition> fields = new ArrayList<FieldDefinition>();
		fields.add(ItemAVL.Fields.ITEMIDENTIFIER);
		fields.add(ItemAVL.Fields.BUSINESSENTITY);
		fields.add(ItemAVL.Fields.BUSINESSENTITYTYPE);
		fields.add(ItemAVL.Fields.VENDORITEMIDENTIFIER);
		fields.add(ItemAVL.Fields.VENDORBUSINESSENTITY);
		fields.add(ItemAVL.Fields.VENDORBUSINESSENTITYTYPE);
		fields.add(ItemAVL.Fields.VENDORSITE);
		fields.add(ItemAVL.Fields.DESCRIPTION);
		fields.add(ItemAVL.Fields.REVISION);
		fields.add(ItemAVL.Fields.VERSION);
		fields.add(ItemAVL.Fields.ITEMCLASSIFICATION);
		fields.add(ItemAVL.Fields.COMMODITYCODE);
		fields.add(ItemAVL.Fields.MANAGEDBY);
		fields.add(ItemAVL.Fields.PRODUCTUNITOFMEASURECODE);
		fields.add(ItemAVL.Fields.MAKEBUY);
		fields.add(ItemAVL.Fields.OWNERNAME);
		fields.add(ItemAVL.Fields.CONTACTNAME);
		fields.add(ItemAVL.Fields.EFFECTIVEFROMDATE);
		fields.add(ItemAVL.Fields.EFFECTIVETODATE);		
		return fields.toArray(new FieldDefinition[fields.size()]);
	}
	
	/**
	 * Why do we have {@link #getExcelUploadFields()} AND {@link #getFieldDefinitions()}?
	 * The {@link #getExcelUploadFields()} would work fine for must uploads.  However, if 
	 * you look at how one of the ItemAVL.Fields are defined:<br>
	 * <code>public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");</code>
	 * Only the fieldName is defined.  This is fine for most cases.  However, in this case for the MTCM Excel file,
	 * I need the fieldName and the displayName.  This code gets the full FieldDefinition that is defined for each
	 * class member.
	 * @return
	 */
	private FieldDefinition[] getFieldDefinitions()	{
		FieldDefinition[] fields = getExcelUploadFields();
		List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
		
		ItemAVL itemAVLInstance = ItemAVL.Factory.newInstance();
		MessageLineStructure<ItemAVL> mls = (MessageLineStructure<ItemAVL>) itemAVLInstance.getMessageStructure();
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
