/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.contact.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.bom.entity.BaseBomEntity;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;

import jakarta.persistence.*;

import java.util.Set;


/**
 * Models a contact, either a business or a person
 */
@Entity
@Table(name = "CONTACT")
@SuppressWarnings("serial")
public class Contact extends BaseBomEntity implements java.io.Serializable
{
	// Fields
	@Id
	@SequenceGenerator(name = "CONTACT_SEQ", sequenceName = "CONTACT_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "CONTACT_SEQ")
	@Column(name = "CONTACT_KEY")
	private Long contactKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY")
	private BusinessEntity businessEntity;

	@Column(name = "CONTACT_NAME", nullable = false)
	private String contactName;

	@Column(name = "CONTACT_ID")
	private String contactId;

	@Column(name = "CONTACT_UNIQUE_ID")
	private String contactUniqueId;

	@Column(name = "DATA_SOURCE", nullable = false)
	private String dataSource = "MCM";

	@Column(name = "ADDRESS_LINE1")
	private String addressL1;

	@Column(name = "ADDRESS_LINE2")
	private String addressL2;

	@Column(name = "ADDRESS_LINE3")
	private String addressL3;

	@Column(name = "CITY", length = 50)
	private String city;

	@Column(name = "REGION", length = 50)
	private String region;

	@Column(name = "COUNTRY_CODE", length = 3)
	private String countryCode;

	@Column(name = "COUNTRY", length = 50)
	private String country;

	@Column(name = "POSTAL_CODE", length = 20)
	private String postalCode;

	@Transient
	private String postOfficeBox;

	@Column(name = "PHONE_NUMBER", length = 20)
	private String telephoneNumber;

	@Column(name = "FACSIMILE_NUMBER", length = 20)
	private String faxNumber;

	@Column(name = "DEPARTMENT", length = 50)
	private String department;

	@Column(name = "BUSINESS_NAME")
	private String businessName;

	@Column(name = "BUSINESS_IDENTIFIER", length = 50)
	private String dunsId;

	@Column(name = "EMAIL", length = 50)
	private String eMail;

	@Column(name = "URI")
	private String companyUri;

	@Column(name = "STATUS", length = 50)
	private String status;

	@Column(name = "PARTNER_CLASSIFICATION", length = 50)
	private String partnerClassCode;

	@Transient
	private String partnerClassCodeOther;

	@Transient
	private String partnerSubclassCode;

	@Transient
	private String dunsPlus4;

	// Not currently used
	@Transient
	private Set setOfSupplierItem;

	@Transient
	private Set setOfMfgItem;

	@Transient
	private Set setOfItem;

	// Constructors
	/** default constructor */
	public Contact()
	{
	}

	/** constructor with id */
	public Contact(Long ContactKey)
	{
		this.contactKey = ContactKey;
	}

	// Property accessors
	/**
	 * 
	 */
	public Long getContactKey()
	{
		return this.contactKey;
	}

	public void setContactKey(Long ContactKey)
	{
		this.contactKey = ContactKey;
	}


	/**
	 * 
	 */
	public String getContactName()
	{
		return this.contactName;
	}

	public void setContactName(String ContactName)
	{
		this.contactName = ContactName;
	}

	/**
	 * 
	 * @param contactId
	 *            The contactId to set.
	 * 
	 */
	public void setContactId(String contactId)
	{
		this.contactId = contactId;
	}

	/**
	 * 
	 * @return Returns the contactId.
	 * 
	 */
	public String getContactId()
	{
		return contactId;
	}

	public void setContactUniqueId(String contactUniqueId)
	{
		this.contactUniqueId = contactUniqueId;
	}

	public String getContactUniqueId()
	{
		return contactUniqueId;
	}

	public String getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	public void setBusinessEntity(BusinessEntity businessEntity)
	{
		this.businessEntity = businessEntity;
	}

	public BusinessEntity getBusinessEntity()
	{
		return businessEntity;
	}
	
	/**
	 * 
	 */
	public String getAddressL1()
	{
		return this.addressL1;
	}

	public void setAddressL1(String AddressL1)
	{
		this.addressL1 = AddressL1;
	}

	/**
	 * 
	 */
	public String getAddressL2()
	{
		return this.addressL2;
	}

	public void setAddressL2(String AddressL2)
	{
		this.addressL2 = AddressL2;
	}

	/**
	 * 
	 */
	public String getAddressL3()
	{
		return this.addressL3;
	}

	public void setAddressL3(String AddressL3)
	{
		this.addressL3 = AddressL3;
	}

	/**
	 * 
	 */
	public String getCity()
	{
		return this.city;
	}

	public void setCity(String City)
	{
		this.city = City;
	}

	/**
	 * 
	 */
	public String getRegion()
	{
		return this.region;
	}

	public void setRegion(String Region)
	{
		this.region = Region;
	}

	/**
	 * 
	 */
	public String getCountryCode()
	{
		return this.countryCode;
	}

	public void setCountryCode(String CountryCode)
	{
		this.countryCode = CountryCode;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * 
	 */
	public String getPostalCode()
	{
		return this.postalCode;
	}

	public void setPostalCode(String PostalCode)
	{
		this.postalCode = PostalCode;
	}

	/**
	 * 
	 */
	public String getPostOfficeBox()
	{
		return this.postOfficeBox;
	}

	public void setPostOfficeBox(String PostOfficeBox)
	{
		this.postOfficeBox = PostOfficeBox;
	}

	/**
	 * 
	 */
	public String getTelephoneNumber()
	{
		return this.telephoneNumber;
	}

	public void setTelephoneNumber(String TelephoneNumber)
	{
		this.telephoneNumber = TelephoneNumber;
	}

	/**
	 * 
	 */
	public String getFaxNumber()
	{
		return this.faxNumber;
	}

	public void setFaxNumber(String FaxNumber)
	{
		this.faxNumber = FaxNumber;
	}

	/**
	 * 
	 */
	public String getDepartment()
	{
		return this.department;
	}

	public void setDepartment(String Department)
	{
		this.department = Department;
	}

	/**
	 * 
	 */
	public String getBusinessName()
	{
		return this.businessName;
	}

	public void setBusinessName(String BusinessName)
	{
		this.businessName = BusinessName;
	}

	/**
	 * 
	 */
	public String getDuns()
	{
		return this.dunsId;
	}

	public void setDuns(String Duns)
	{
		this.dunsId = Duns;
	}

	/**
	 * 
	 */
	public String getEmail()
	{
		return this.eMail;
	}

	public void setEmail(String Email)
	{
		this.eMail = Email;
	}

	/**
	 * 
	 */
	public String getCompanyUri()
	{
		return this.companyUri;
	}

	public void setCompanyUri(String CompanyUri)
	{
		this.companyUri = CompanyUri;
	}

	/**
	 * 
	 */
	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String Status)
	{
		this.status = Status;
	}

	/**
	 * 
	 */
	public String getPartnerClassCode()
	{
		return this.partnerClassCode;
	}

	public void setPartnerClassCode(String PartnerClassCode)
	{
		this.partnerClassCode = PartnerClassCode;
	}

	/**
	 * 
	 */
	public String getPartnerClassCodeOther()
	{
		return this.partnerClassCodeOther;
	}

	public void setPartnerClassCodeOther(String PartnerClassCodeOther)
	{
		this.partnerClassCodeOther = PartnerClassCodeOther;
	}

	/**
	 * 
	 */
	public String getPartnerSubclassCode()
	{
		return this.partnerSubclassCode;
	}

	public void setPartnerSubclassCode(String PartnerSubclassCode)
	{
		this.partnerSubclassCode = PartnerSubclassCode;
	}

	/**
	 * 
	 */
	public String getDunsPlus4()
	{
		return this.dunsPlus4;
	}

	public void setDunsPlus4(String DunsPlus4)
	{
		this.dunsPlus4 = DunsPlus4;
	}

	/**
	 * 
	 */
	public Set getSetOfSupplierItem()
	{
		return this.setOfSupplierItem;
	}

	public void setSetOfSupplierItem(Set SetOfSupplierItem)
	{
		this.setOfSupplierItem = SetOfSupplierItem;
	}

	/**
	 * 
	 */
	public Set getSetOfMfgItem()
	{
		return this.setOfMfgItem;
	}

	public void setSetOfMfgItem(Set mfgItems)
	{
		this.setOfMfgItem = mfgItems;
	}

	/**
	 * 
	 */
	public Set getSetOfItem()
	{
		return this.setOfItem;
	}

	public void setSetOfItem(Set SetOfItem)
	{
		this.setOfItem = SetOfItem;
	}
	
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof Contact))
            return false;
        Contact castOther = (Contact) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getContactName(),castOther.getContactName());
        eb.append(this.getContactId(),castOther.getContactId());        
        eb.append(this.getContactUniqueId(),castOther.getContactUniqueId());        
        return eb.isEquals();
    }
    
    public int hashCode()
    {
        int result = new HashCodeBuilder(17, 37).
        append(this.getContactName()).        
        append(this.getContactId()).
        append(this.getContactUniqueId()).
        toHashCode();
        return result;
    }

	
}