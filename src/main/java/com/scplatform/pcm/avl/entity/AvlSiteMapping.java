/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2026, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.avl.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.scplatform.pcm.site.entity.Site;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
* @author niprakash
*/

@Entity
@Table(name="AVL_SITE_MAPPING")
public class AvlSiteMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AVL_SITE_MAPPING_SEQ")
    @SequenceGenerator(name = "AVL_SITE_MAPPING_SEQ", sequenceName = "AVL_SITE_MAPPING_SEQ",allocationSize = 1)
    @Column(name="ID")
    private Long Id;

    @JoinColumn(name="AVL_KEY")
    @ManyToOne(fetch=LAZY)
    private Avl avl;

    @JoinColumn(name="SOURCE_SITE_KEY")
    @ManyToOne(fetch=LAZY)
    private Site sourceSite;

    @JoinColumn(name="DESTINATION_SITE_KEY")
    @ManyToOne(fetch=LAZY)
    private Site destSite;

    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        Id = id;
    }
    public Avl getAvl() {
        return avl;
    }
    public void setAvl(Avl avl) {
        this.avl = avl;
    }

    public Site getSourceSite(){
        return sourceSite;
    }

    public void setSourceSite(Site sourceSite){
        this.sourceSite = sourceSite;
    }

    public Site getDestSite(){
        return destSite;
    }
    public void setDestSite(Site destSite){
        this.destSite = destSite;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37) .append(this.getAvl()) .append(this.getSourceSite()) .append(this.getDestSite()) .toHashCode();
    }

    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof AvlSiteMapping))
            return false;
        AvlSiteMapping castOther = (AvlSiteMapping) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getAvl(), castOther.getAvl());
        eb.append(this.getSourceSite(),castOther.getSourceSite());
        eb.append(this.getDestSite(),castOther.getDestSite());
        return eb.isEquals();
    }
}
