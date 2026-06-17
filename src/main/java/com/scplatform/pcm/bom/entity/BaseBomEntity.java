/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.entity;

import org.apache.commons.beanutils.PropertyUtils;

import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.common.entity.TrackDelta;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Convert;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author bblasko
 * Base entity for bom model entities.  Contains the special attributes need by the ETL
 * loader
 * 
 */
@MappedSuperclass
public class BaseBomEntity
	implements TrackDelta
	
{
	@Convert(converter = YesNoConverter.class)
	@Column(name = "CURRENT_FLAG", nullable = false)
	private boolean isCurrent = true;

	@Convert(converter = YesNoConverter.class)
	@Column(name = "DELETE_FLAG")
	private Boolean isDeleted = Boolean.FALSE;

	@Column(name = "EFFECTIVE_FROM_DT")
	private Date effectiveFrom = null;

	@Column(name = "EFFECTIVE_TO_DT")
	private Date effectiveTo = null;

	@Column(name = "INSERT_DT", nullable = false)
	private Date insertDate = new Date();

	@Column(name = "UPDATE_DT")
	private Date updateDate = null;

	@Transient
	protected Boolean isRollupExtractEnable = true;
	
        // Flex Attributes
        @Transient private String stringAttribute1;
        @Transient private String stringAttribute2;
        @Transient private String stringAttribute3;
        @Transient private String stringAttribute4;
        @Transient private String stringAttribute5;
        @Transient private String stringAttribute6;
        @Transient private String stringAttribute7;
        @Transient private String stringAttribute8;
        @Transient private String stringAttribute9;
        @Transient private String stringAttribute10;
        @Transient private Integer numberAttribute1;
        @Transient private Integer numberAttribute2;
        @Transient private Integer numberAttribute3;
        @Transient private Integer numberAttribute4;
        @Transient private Integer numberAttribute5;
        @Transient private Integer numberAttribute6;
        @Transient private Integer numberAttribute7;
        @Transient private Integer numberAttribute8;
        @Transient private Integer numberAttribute9;
        @Transient private Integer numberAttribute10;
        @Transient private BigDecimal floatAttribute1;
        @Transient private BigDecimal floatAttribute2;
        @Transient private BigDecimal floatAttribute3;
        @Transient private BigDecimal floatAttribute4;
        @Transient private BigDecimal floatAttribute5;
        @Transient private BigDecimal floatAttribute6;
        @Transient private BigDecimal floatAttribute7;
        @Transient private BigDecimal floatAttribute8;
        @Transient private BigDecimal floatAttribute9;
        @Transient private BigDecimal floatAttribute10;
        @Transient private Date dateAttribute1;
        @Transient private Date dateAttribute2;
        @Transient private Date dateAttribute3;
        @Transient private Date dateAttribute4;
        @Transient private Date dateAttribute5;
        @Transient private Date dateAttribute6;
        @Transient private Date dateAttribute7;
        @Transient private Date dateAttribute8;
        @Transient private Date dateAttribute9;
        @Transient private Date dateAttribute10;

	public boolean getCurrentFlag()
	{
		return this.isCurrent;
	}

	public Boolean getDeleteFlag()
	{
		return this.isDeleted;
	}

	public void setCurrentFlag(boolean currentFlag)
	{
		this.isCurrent = currentFlag;
		
	}

	public void setDeleteFlag(Boolean deleteFlag)
	{
		this.isDeleted = deleteFlag;		
	}
	
	@Deprecated
	public boolean getIsCurrent()
	{
		return this.getCurrentFlag();
	}

	@Deprecated
	public void setIsCurrent(boolean current)
	{
		this.setCurrentFlag(current);
	}

	@Deprecated
	public void setIsDeleted(Boolean isDeleted)
	{
		this.setDeleteFlag(isDeleted);
	}

	@Deprecated
	public Boolean getIsDeleted()
	{
		return this.getDeleteFlag();
	}

	/**
	 * 
	 */
	public Date getInsertDate()
	{
		return this.insertDate;
	}

	public void setInsertDate(Date InsertDate)
	{
		this.insertDate = InsertDate;
	}

	/**
	 * 
	 */
	public Date getUpdateDate()
	{
		return this.updateDate;
	}

	public void setUpdateDate(Date UpdateDate)
	{
		this.updateDate = UpdateDate;
	}

	public void setEffectiveFrom(Date effectiveFrom)
	{
		this.effectiveFrom = effectiveFrom;
	}

	public Date getEffectiveFrom()
	{
		return effectiveFrom;
	}

	public void setEffectiveTo(Date effectiveTo)
	{
		this.effectiveTo = effectiveTo;
	}

	public Date getEffectiveTo()
	{
		return effectiveTo;
	}


	protected boolean compareNullSafe(Object thisObj, Object thatObj)
	{
		if (thisObj == thatObj)
		{
			return true;
		}
		else if (thisObj != null)
		{
			return thisObj.equals(thatObj);
		}
		return false;
	}
	
	// Flex Fields
	    
        /**
         * @param attribute
         */
        public void addFlexAttribute(Attribute attribute) {
            setFlexAttribute(attribute.getAssociatedAttribute(), attribute.getAttrValue());
        }
    
        /**
         * @param associatedAttribute
         * @return value of the flex attribute
         */
        public Object getFlexAttribute(String associatedAttribute) {
            Object obj = null;
            try {
                obj = PropertyUtils.getProperty(this, associatedAttribute);
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return obj;
        }
    
        /**
         * @param key
         * @param value
         */
        public void setFlexAttribute(String key, Object value) {
            try {
                PropertyUtils.setProperty(this, key, value);
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        }
    
        /**
         * @param copy
         */
        public void copyFlexAttributes(BaseBomEntity copy) {
            copy.stringAttribute1 = this.stringAttribute1;
            copy.stringAttribute2 = this.stringAttribute2;
            copy.stringAttribute3 = this.stringAttribute3;
            copy.stringAttribute4 = this.stringAttribute4;
            copy.stringAttribute5 = this.stringAttribute5;
            copy.stringAttribute6 = this.stringAttribute6;
            copy.stringAttribute7 = this.stringAttribute7;
            copy.stringAttribute8 = this.stringAttribute8;
            copy.stringAttribute9 = this.stringAttribute9;
            copy.stringAttribute10 = this.stringAttribute10;
    
            copy.numberAttribute1 = this.numberAttribute1;
            copy.numberAttribute2 = this.numberAttribute2;
            copy.numberAttribute3 = this.numberAttribute3;
            copy.numberAttribute4 = this.numberAttribute4;
            copy.numberAttribute5 = this.numberAttribute5;
            copy.numberAttribute6 = this.numberAttribute6;
            copy.numberAttribute7 = this.numberAttribute7;
            copy.numberAttribute8 = this.numberAttribute8;
            copy.numberAttribute9 = this.numberAttribute9;
            copy.numberAttribute10 = this.numberAttribute10;
    
            copy.floatAttribute1 = this.floatAttribute1;
            copy.floatAttribute2 = this.floatAttribute2;
            copy.floatAttribute3 = this.floatAttribute3;
            copy.floatAttribute4 = this.floatAttribute4;
            copy.floatAttribute5 = this.floatAttribute5;
            copy.floatAttribute6 = this.floatAttribute6;
            copy.floatAttribute7 = this.floatAttribute7;
            copy.floatAttribute8 = this.floatAttribute8;
            copy.floatAttribute9 = this.floatAttribute9;
            copy.floatAttribute10 = this.floatAttribute10;
    
            copy.dateAttribute1 = this.dateAttribute1;
            copy.dateAttribute2 = this.dateAttribute2;
            copy.dateAttribute3 = this.dateAttribute3;
            copy.dateAttribute4 = this.dateAttribute4;
            copy.dateAttribute5 = this.dateAttribute5;
            copy.dateAttribute6 = this.dateAttribute6;
            copy.dateAttribute7 = this.dateAttribute7;
            copy.dateAttribute8 = this.dateAttribute8;
            copy.dateAttribute9 = this.dateAttribute9;
            copy.dateAttribute10 = this.dateAttribute10;
        }
    
        /**
         * @return the stringAttribute1
         */
        public String getStringAttribute1() {
            return stringAttribute1;
        }
    
        /**
         * @param stringAttribute1
         *            the stringAttribute1 to set
         */
        public void setStringAttribute1(String stringAttribute1) {
            this.stringAttribute1 = stringAttribute1;
        }
    
        /**
         * @return the stringAttribute2
         */
        public String getStringAttribute2() {
            return stringAttribute2;
        }
    
        /**
         * @param stringAttribute2
         *            the stringAttribute2 to set
         */
        public void setStringAttribute2(String stringAttribute2) {
            this.stringAttribute2 = stringAttribute2;
        }
    
        /**
         * @return the stringAttribute3
         */
        public String getStringAttribute3() {
            return stringAttribute3;
        }
    
        /**
         * @param stringAttribute3
         *            the stringAttribute3 to set
         */
        public void setStringAttribute3(String stringAttribute3) {
            this.stringAttribute3 = stringAttribute3;
        }
    
        /**
         * @return the stringAttribute4
         */
        public String getStringAttribute4() {
            return stringAttribute4;
        }
    
        /**
         * @param stringAttribute4
         *            the stringAttribute4 to set
         */
        public void setStringAttribute4(String stringAttribute4) {
            this.stringAttribute4 = stringAttribute4;
        }
    
        /**
         * @return the stringAttribute5
         */
        public String getStringAttribute5() {
            return stringAttribute5;
        }
    
        /**
         * @param stringAttribute5
         *            the stringAttribute5 to set
         */
        public void setStringAttribute5(String stringAttribute5) {
            this.stringAttribute5 = stringAttribute5;
        }
    
        /**
         * @return the stringAttribute6
         */
        public String getStringAttribute6() {
            return stringAttribute6;
        }
    
        /**
         * @param stringAttribute6
         *            the stringAttribute6 to set
         */
        public void setStringAttribute6(String stringAttribute6) {
            this.stringAttribute6 = stringAttribute6;
        }
    
        /**
         * @return the stringAttribute7
         */
        public String getStringAttribute7() {
            return stringAttribute7;
        }
    
        /**
         * @param stringAttribute7
         *            the stringAttribute7 to set
         */
        public void setStringAttribute7(String stringAttribute7) {
            this.stringAttribute7 = stringAttribute7;
        }
    
        /**
         * @return the stringAttribute8
         */
        public String getStringAttribute8() {
            return stringAttribute8;
        }
    
        /**
         * @param stringAttribute8
         *            the stringAttribute8 to set
         */
        public void setStringAttribute8(String stringAttribute8) {
            this.stringAttribute8 = stringAttribute8;
        }
    
        /**
         * @return the stringAttribute9
         */
        public String getStringAttribute9() {
            return stringAttribute9;
        }
    
        /**
         * @param stringAttribute9
         *            the stringAttribute9 to set
         */
        public void setStringAttribute9(String stringAttribute9) {
            this.stringAttribute9 = stringAttribute9;
        }
    
        /**
         * @return the stringAttribute10
         */
        public String getStringAttribute10() {
            return stringAttribute10;
        }
    
        /**
         * @param stringAttribute10
         *            the stringAttribute10 to set
         */
        public void setStringAttribute10(String stringAttribute10) {
            this.stringAttribute10 = stringAttribute10;
        }
    
        /**
         * @return the numberAttribute1
         */
        public Integer getNumberAttribute1() {
            return numberAttribute1;
        }
    
        /**
         * @param numberAttribute1
         *            the numberAttribute1 to set
         */
        public void setNumberAttribute1(Integer numberAttribute1) {
            this.numberAttribute1 = numberAttribute1;
        }
    
        /**
         * @return the numberAttribute2
         */
        public Integer getNumberAttribute2() {
            return numberAttribute2;
        }
    
        /**
         * @param numberAttribute2
         *            the numberAttribute2 to set
         */
        public void setNumberAttribute2(Integer numberAttribute2) {
            this.numberAttribute2 = numberAttribute2;
        }
    
        /**
         * @return the numberAttribute3
         */
        public Integer getNumberAttribute3() {
            return numberAttribute3;
        }
    
        /**
         * @param numberAttribute3
         *            the numberAttribute3 to set
         */
        public void setNumberAttribute3(Integer numberAttribute3) {
            this.numberAttribute3 = numberAttribute3;
        }
    
        /**
         * @return the numberAttribute4
         */
        public Integer getNumberAttribute4() {
            return numberAttribute4;
        }
    
        /**
         * @param numberAttribute4
         *            the numberAttribute4 to set
         */
        public void setNumberAttribute4(Integer numberAttribute4) {
            this.numberAttribute4 = numberAttribute4;
        }
    
        /**
         * @return the numberAttribute5
         */
        public Integer getNumberAttribute5() {
            return numberAttribute5;
        }
    
        /**
         * @param numberAttribute5
         *            the numberAttribute5 to set
         */
        public void setNumberAttribute5(Integer numberAttribute5) {
            this.numberAttribute5 = numberAttribute5;
        }
    
        /**
         * @return the numberAttribute6
         */
        public Integer getNumberAttribute6() {
            return numberAttribute6;
        }
    
        /**
         * @param numberAttribute6
         *            the numberAttribute6 to set
         */
        public void setNumberAttribute6(Integer numberAttribute6) {
            this.numberAttribute6 = numberAttribute6;
        }
    
        /**
         * @return the numberAttribute7
         */
        public Integer getNumberAttribute7() {
            return numberAttribute7;
        }
    
        /**
         * @param numberAttribute7
         *            the numberAttribute7 to set
         */
        public void setNumberAttribute7(Integer numberAttribute7) {
            this.numberAttribute7 = numberAttribute7;
        }
    
        /**
         * @return the numberAttribute8
         */
        public Integer getNumberAttribute8() {
            return numberAttribute8;
        }
    
        /**
         * @param numberAttribute8
         *            the numberAttribute8 to set
         */
        public void setNumberAttribute8(Integer numberAttribute8) {
            this.numberAttribute8 = numberAttribute8;
        }
    
        /**
         * @return the numberAttribute9
         */
        public Integer getNumberAttribute9() {
            return numberAttribute9;
        }
    
        /**
         * @param numberAttribute9
         *            the numberAttribute9 to set
         */
        public void setNumberAttribute9(Integer numberAttribute9) {
            this.numberAttribute9 = numberAttribute9;
        }
    
        /**
         * @return the numberAttribute10
         */
        public Integer getNumberAttribute10() {
            return numberAttribute10;
        }
    
        /**
         * @param numberAttribute10
         *            the numberAttribute10 to set
         */
        public void setNumberAttribute10(Integer numberAttribute10) {
            this.numberAttribute10 = numberAttribute10;
        }
    
        /**
         * @return the floatAttribute1
         */
        public BigDecimal getFloatAttribute1() {
            return floatAttribute1;
        }
    
        /**
         * @param floatAttribute1
         *            the floatAttribute1 to set
         */
        public void setFloatAttribute1(BigDecimal floatAttribute1) {
            this.floatAttribute1 = floatAttribute1;
        }
    
        /**
         * @return the floatAttribute2
         */
        public BigDecimal getFloatAttribute2() {
            return floatAttribute2;
        }
    
        /**
         * @param floatAttribute2
         *            the floatAttribute2 to set
         */
        public void setFloatAttribute2(BigDecimal floatAttribute2) {
            this.floatAttribute2 = floatAttribute2;
        }
    
        /**
         * @return the floatAttribute3
         */
        public BigDecimal getFloatAttribute3() {
            return floatAttribute3;
        }
    
        /**
         * @param floatAttribute3
         *            the floatAttribute3 to set
         */
        public void setFloatAttribute3(BigDecimal floatAttribute3) {
            this.floatAttribute3 = floatAttribute3;
        }
    
        /**
         * @return the floatAttribute4
         */
        public BigDecimal getFloatAttribute4() {
            return floatAttribute4;
        }
    
        /**
         * @param floatAttribute4
         *            the floatAttribute4 to set
         */
        public void setFloatAttribute4(BigDecimal floatAttribute4) {
            this.floatAttribute4 = floatAttribute4;
        }
    
        /**
         * @return the floatAttribute5
         */
        public BigDecimal getFloatAttribute5() {
            return floatAttribute5;
        }
    
        /**
         * @param floatAttribute5
         *            the floatAttribute5 to set
         */
        public void setFloatAttribute5(BigDecimal floatAttribute5) {
            this.floatAttribute5 = floatAttribute5;
        }
    
        /**
         * @return the floatAttribute6
         */
        public BigDecimal getFloatAttribute6() {
            return floatAttribute6;
        }
    
        /**
         * @param floatAttribute6
         *            the floatAttribute6 to set
         */
        public void setFloatAttribute6(BigDecimal floatAttribute6) {
            this.floatAttribute6 = floatAttribute6;
        }
    
        /**
         * @return the floatAttribute7
         */
        public BigDecimal getFloatAttribute7() {
            return floatAttribute7;
        }
    
        /**
         * @param floatAttribute7
         *            the floatAttribute7 to set
         */
        public void setFloatAttribute7(BigDecimal floatAttribute7) {
            this.floatAttribute7 = floatAttribute7;
        }
    
        /**
         * @return the floatAttribute8
         */
        public BigDecimal getFloatAttribute8() {
            return floatAttribute8;
        }
    
        /**
         * @param floatAttribute8
         *            the floatAttribute8 to set
         */
        public void setFloatAttribute8(BigDecimal floatAttribute8) {
            this.floatAttribute8 = floatAttribute8;
        }
    
        /**
         * @return the floatAttribute9
         */
        public BigDecimal getFloatAttribute9() {
            return floatAttribute9;
        }
    
        /**
         * @param floatAttribute9
         *            the floatAttribute9 to set
         */
        public void setFloatAttribute9(BigDecimal floatAttribute9) {
            this.floatAttribute9 = floatAttribute9;
        }
    
        /**
         * @return the floatAttribute10
         */
        public BigDecimal getFloatAttribute10() {
            return floatAttribute10;
        }
    
        /**
         * @param floatAttribute10
         *            the floatAttribute10 to set
         */
        public void setFloatAttribute10(BigDecimal floatAttribute10) {
            this.floatAttribute10 = floatAttribute10;
        }
    
        /**
         * @return the dateAttribute1
         */
        public Date getDateAttribute1() {
            return dateAttribute1;
        }
    
        /**
         * @param dateAttribute1
         *            the dateAttribute1 to set
         */
        public void setDateAttribute1(Date dateAttribute1) {
            this.dateAttribute1 = dateAttribute1;
        }
    
        /**
         * @return the dateAttribute2
         */
        public Date getDateAttribute2() {
            return dateAttribute2;
        }
    
        /**
         * @param dateAttribute2
         *            the dateAttribute2 to set
         */
        public void setDateAttribute2(Date dateAttribute2) {
            this.dateAttribute2 = dateAttribute2;
        }
    
        /**
         * @return the dateAttribute3
         */
        public Date getDateAttribute3() {
            return dateAttribute3;
        }
    
        /**
         * @param dateAttribute3
         *            the dateAttribute3 to set
         */
        public void setDateAttribute3(Date dateAttribute3) {
            this.dateAttribute3 = dateAttribute3;
        }
    
        /**
         * @return the dateAttribute4
         */
        public Date getDateAttribute4() {
            return dateAttribute4;
        }
    
        /**
         * @param dateAttribute4
         *            the dateAttribute4 to set
         */
        public void setDateAttribute4(Date dateAttribute4) {
            this.dateAttribute4 = dateAttribute4;
        }
    
        /**
         * @return the dateAttribute5
         */
        public Date getDateAttribute5() {
            return dateAttribute5;
        }
    
        /**
         * @param dateAttribute5
         *            the dateAttribute5 to set
         */
        public void setDateAttribute5(Date dateAttribute5) {
            this.dateAttribute5 = dateAttribute5;
        }
    
        /**
         * @return the dateAttribute6
         */
        public Date getDateAttribute6() {
            return dateAttribute6;
        }
    
        /**
         * @param dateAttribute6
         *            the dateAttribute6 to set
         */
        public void setDateAttribute6(Date dateAttribute6) {
            this.dateAttribute6 = dateAttribute6;
        }
    
        /**
         * @return the dateAttribute7
         */
        public Date getDateAttribute7() {
            return dateAttribute7;
        }
    
        /**
         * @param dateAttribute7
         *            the dateAttribute7 to set
         */
        public void setDateAttribute7(Date dateAttribute7) {
            this.dateAttribute7 = dateAttribute7;
        }
    
        /**
         * @return the dateAttribute8
         */
        public Date getDateAttribute8() {
            return dateAttribute8;
        }
    
        /**
         * @param dateAttribute8
         *            the dateAttribute8 to set
         */
        public void setDateAttribute8(Date dateAttribute8) {
            this.dateAttribute8 = dateAttribute8;
        }
    
        /**
         * @return the dateAttribute9
         */
        public Date getDateAttribute9() {
            return dateAttribute9;
        }
    
        /**
         * @param dateAttribute9
         *            the dateAttribute9 to set
         */
        public void setDateAttribute9(Date dateAttribute9) {
            this.dateAttribute9 = dateAttribute9;
        }
    
        /**
         * @return the dateAttribute10
         */
        public Date getDateAttribute10() {
            return dateAttribute10;
        }
    
        /**
         * @param dateAttribute10
         *            the dateAttribute10 to set
         */
        public void setDateAttribute10(Date dateAttribute10) {
            this.dateAttribute10 = dateAttribute10;
        }

		public Boolean getIsRollupExtractEnable() {
			return isRollupExtractEnable;
		}

		public void setIsRollupExtractEnable(Boolean isRollupExtractEnable) {
			this.isRollupExtractEnable = isRollupExtractEnable;
		}

}
