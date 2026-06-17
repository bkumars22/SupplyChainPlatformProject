/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.costRecord;

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
import com.test.selenium.scplatform.messages.costRecord.CostRecord;

/**
 * @author dgenrich
 *
 * @param <T>	CostRecord or any class that extends it
 * 
 * @see MessageWriter
 */
public class CostRecordWriter<T extends CostRecord> extends MessageWriter<T> {

	/**
	 * @param messageClazz	
	 * 		The CostRecord Message class, typically CostRecord.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link CostRecordBuilder}
	 */
	public CostRecordWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
	}
	
	@Override
	public String generate() throws BeansException, MessageWriterException, IOException	{
		this.fieldsToSerialize = getFieldDefinitions();
		return super.generate();
	}
   private FieldDefinition[] getExcelUploadFields() {
		
		List<FieldDefinition> fields = new ArrayList<FieldDefinition>();
		fields.add(CostRecord.Fields.ITEMIDENTIFIER);
		fields.add(CostRecord.Fields.ITEMDESCRIPTION);
		fields.add(CostRecord.Fields.BUSINESSENTITY);
		fields.add(CostRecord.Fields.BUSINESSENTITYTYPE);
		fields.add(CostRecord.Fields.LIFECYCLECODE);
		fields.add(CostRecord.Fields.FROMBUSINESSENTITY);
		fields.add(CostRecord.Fields.FROMBUSINESSENTITYTYPE);
		fields.add(CostRecord.Fields.FROMSITE);
		fields.add(CostRecord.Fields.SITE);
		fields.add(CostRecord.Fields.COSTBUSINESSENTITY);
		fields.add(CostRecord.Fields.COSTBUSINESSENTITYTYPE);
		fields.add(CostRecord.Fields.DATEOFFSET);
		fields.add(CostRecord.Fields.CURRENCYCODE);
		fields.add(CostRecord.Fields.EFFECTIVEFROMDATE);
		fields.add(CostRecord.Fields.EFFECTIVETODATE);
		fields.add(CostRecord.Fields.ENDDATESREQUIRED);
		fields.add(CostRecord.Fields.STATUS);
		fields.add(CostRecord.Fields.COSTTYPE);
		fields.add(CostRecord.Fields.PRICINGSCENARIO);
		fields.add(CostRecord.Fields.ISACTIVE);
		fields.add(CostRecord.Fields.TOTALPRICE);
		fields.add(CostRecord.Fields.MATERIAL);
		fields.add(CostRecord.Fields.MVA);
		fields.add(CostRecord.Fields.PROFITMARGIN);
		fields.add(CostRecord.Fields.SGA);
		fields.add(CostRecord.Fields.VAT);
		fields.add(CostRecord.Fields.TAXES);
		fields.add(CostRecord.Fields.TRANSPORTATION);
		fields.add(CostRecord.Fields.AIR_VALUE);
		fields.add(CostRecord.Fields.AIR_PERCENT);
		fields.add(CostRecord.Fields.SEA_VALUE);
		fields.add(CostRecord.Fields.SEA_PERCENT);
		fields.add(CostRecord.Fields.LAND_VALUE);
		fields.add(CostRecord.Fields.LAND_PERCENT);
		fields.add(CostRecord.Fields.INFREIGHT);
		fields.add(CostRecord.Fields.OUTFREIGHT);
		fields.add(CostRecord.Fields.ROYALTY);
		fields.add(CostRecord.Fields.AMORTIZATION);
		fields.add(CostRecord.Fields.OPEXNRE);
		fields.add(CostRecord.Fields.NPITOOLING);
		fields.add(CostRecord.Fields.CAPEX);
		fields.add(CostRecord.Fields.PRODUCTIONRESPONSIBILITY);
		fields.add(CostRecord.Fields.SERVICERESPONSIBILITY);
		fields.add(CostRecord.Fields.COMMENT);
		fields.add(CostRecord.Fields.REASONCODE);
		
		
		
		return fields.toArray(new FieldDefinition[fields.size()]);
	}
	
	/**
	 * Why do we have {@link #getExcelUploadFields()} AND {@link #getFieldDefinitions()}?
	 * The {@link #getExcelUploadFields()} would work fine for must uploads.  However, if 
	 * you look at how one of the CostRecord.Fields are defined:<br>
	 * <code>public static final FieldDefinition ITEMIDENTIFIER = new FieldDefinition("ItemIdentifier");</code>
	 * Only the fieldName is defined.  This is fine for most cases.  However, in this case for the MTCM Excel file,
	 * I need the fieldName and the displayName.  This code gets the full FieldDefinition that is defined for each
	 * class member.
	 * @return
	 */
	private FieldDefinition[] getFieldDefinitions()	{
		FieldDefinition[] fields = getExcelUploadFields();
		List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
		
		CostRecord CostRecordInstance = CostRecord.Factory.newInstance();
		MessageLineStructure<CostRecord> mls = (MessageLineStructure<CostRecord>) CostRecordInstance.getMessageStructure();
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
