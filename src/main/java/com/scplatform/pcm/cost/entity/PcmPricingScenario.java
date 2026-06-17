/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
    public class PcmPricingScenario implements Serializable {

	private static final Logger log = LogManager.getLogger(PcmPricingScenario.class);

	@Id
	private Long pricingScenarioKey;
	
	private String pricingScenarioName;		
	
	@Column(name = "IS_RANGE_BASED", nullable = false, length = 1)
	private Boolean rangeBased;
	
	public PcmPricingScenario(Long pricingScenarioKey, String pricingScenarioName)
	{
		this.pricingScenarioKey = pricingScenarioKey;
		this.pricingScenarioName = pricingScenarioName;
	}

	public int hashCode()
	{
		return (pricingScenarioKey != null) ? pricingScenarioKey.hashCode():0;
	}
	
    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmPricingScenario))
            return false;
        PcmPricingScenario castOther = (PcmPricingScenario) other;
        EqualsBuilder eb = new EqualsBuilder();
	try {	   
	    eb.append(this.getPricingScenarioName(), castOther.getPricingScenarioName());
	    eb.append(this.isRangeBased(), castOther.isRangeBased());
	} catch (Throwable t) {	    
	    log.warn("Equals method failed with error",t);
	    eb.append(this.getPricingScenarioName(),
		    castOther.isRangeBased());
	}
	return eb.isEquals();
    }

    public Boolean isRangeBased()
    {
        return rangeBased;
    }


    @Override
	public String toString()
	{
		return pricingScenarioName.toString();
	}
	
}
