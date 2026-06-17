/**
 *	EntityChangeTracker.java
 *	Created on Jun 14, 2012
 *
 *	Copyright (c) 2010 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code.
 *
 *	Author: sgupta
 */
package com.scplatform.pcm.common.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tracks if an entity has changed. A transient instance of this class shoudl be placed in the entity class. The setters
 * should call the firePropertyChangeEvent passing the old and new values. If the oldvalue and newvalue are different
 * then the tracker will be marked as changed.
 *
 * @author sgupta
 */
public class ChangeTracker<T> {

    private final static Logger log = LogManager.getLogger(ChangeTracker.class);

    private T observedObject;

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    public void resetChange() {
        if (log.isDebugEnabled()) {
            log.debug("Entity " + this.observedObject + " has been explicitly marked unchanged");
        }
        changed = false;
    }

    public void markChanged() {
        if (log.isDebugEnabled()) {
            log.debug("Entity " + this.observedObject + " has been explicitly marked changed");
        }
        changed = true;
    }

    /**
     * @return the entity
     */
    public T getObservedObject() {
        return this.observedObject;
    }

    /**
     * @param entity
     *            the entity to set
     */
    public void setObservedObject(T entity) {
        this.observedObject = entity;
    }

    public void firePropertyChangeEvent(String propName, Object oldVal, Object newVal) {
        if (log.isDebugEnabled()) {
            log.debug("Property Change for entity " + this.observedObject + " -> Before:" + this.changed + " Prop:"
                    + propName + " old:" + oldVal + " new:" + newVal);
        }
        if (changed) {
            log.debug("Entity has changed. No comps required");
            return;
        } else {
            if ((oldVal == null && newVal != null) || (oldVal != null && newVal == null)) {
                changed = true;
            } else if (oldVal != null && newVal != null) {
                changed = !(oldVal.equals(newVal));
            }
            log.debug(" After:" + this.changed);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void firePropertyChangeEventUsingCompare(String propName, Comparable oldVal, Comparable newVal) {
        if (log.isDebugEnabled()) {
            log.debug("(Compare) Property Change for entity  " + this.observedObject + " -> Before:" + this.changed
                    + " Prop:" + propName + " old:" + oldVal + " new:" + newVal);
        }
        if (changed) {
            log.debug("Entity has changed. No comps required");
            return;
        } else {
            if ((oldVal == null && newVal != null) || (oldVal != null && newVal == null)) {
                changed = true;
            } else if (oldVal != null && newVal != null) {
                changed = (oldVal.compareTo(newVal) != 0);
            }
            log.debug(" After:" + this.changed);
        }
    }
}
