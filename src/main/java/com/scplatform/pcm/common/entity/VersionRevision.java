/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VersionRevision implements Serializable, Comparable<VersionRevision> {
    private static final long serialVersionUID = -4128926478225509571L;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "REVISION")
    private String revision;

    @Column(name = "VERSION_DATE")
    private Date versionDate;

    @Column(name = "REVISION_DATE")
    private Date revisionDate;

    public VersionRevision() {

    }

    public VersionRevision(String revision, String version) {
        super();
        this.revision = revision;
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    @Override
    public int compareTo(VersionRevision o) {
        int cmp = this.version != null && o.version != null ? this.version.compareTo(o.version) : 0;
        if (cmp == 0) {
            cmp = this.revision != null && o.revision != null ? this.revision.compareTo(o.revision) : 0;
        }
        return cmp;
    }
}
