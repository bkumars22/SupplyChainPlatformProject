/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.forecast.entity;

import com.scplatform.pcm.common.entity.AuditRevisionBase;
import com.scplatform.pcm.forecast.service.ForecastFormService;
import com.scplatform.pcm.forecast.service.PcmForecastValueService;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils;
import com.scplatform.pcm.util.datetime.ISO8601;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static jakarta.persistence.FetchType.LAZY;

/**
 * Models a specific value within a forecast. Multiple values can exist for the periods so long as they are different
 * measures Note: this class has a natural ordering that is inconsistent with equals. NOTE: The Forecast Measure Key is
 * treated as a case insensitive key
 */
@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "FORECAST_VALUE_TYPE")
@Table(name = "PCM_FORECAST_VALUE")
@SequenceGenerator(name = "PCM_FORECAST_VALUE_SEQ", sequenceName = "PCM_FORECAST_VALUE_SEQ",allocationSize = 1)
public abstract class PcmForecastValue extends AuditRevisionBase implements Comparable<PcmForecastValue>{
    public static final String SS_KEY_LAST_APPROVED_VALUE = "lav";

    @Id
    @Setter
    @GeneratedValue(generator = "PCM_FORECAST_VALUE_SEQ")
    @Column(name = "FORECAST_VALUE_KEY")
    private Long forecastValueKey;

    @Setter
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "FORECAST_KEY")
    private PcmForecast forecast;

    @Column(name = "FORECAST_MEASURE_KEY", nullable = false)
    private String forecastMeasureKey;

    @Column(name = "FORECAST_UOM", nullable = true)
    private String forecastValueUOM;

    @Column(name = "EFFECTIVE_FROM_DT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveFromDt;

    @Column(name = "EFFECTIVE_TO_DT", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveToDt;

    @Setter
    @Column(name = "SAVED_STATES", nullable = true)
    private String savedStates;

    public PcmForecastValue() {
        super();
    }

    public PcmForecastValue(PcmForecastValue copyValue) {
        super();
        this.effectiveFromDt = copyValue.effectiveFromDt;
        this.effectiveToDt = copyValue.effectiveToDt;
        this.forecastMeasureKey = copyValue.forecastMeasureKey;
        this.forecastValueUOM = copyValue.forecastValueUOM;
    }

    public PcmForecastValue(Date effectiveFromDt, Date effectiveToDt, String forecastMeasureKey, String forecastValueUOM) {
        super();
        this.effectiveFromDt = effectiveFromDt;
        this.effectiveToDt = effectiveToDt;
        this.forecastMeasureKey = forecastMeasureKey;
        this.forecastValueUOM = forecastValueUOM;
    }

    public void setForecastMeasureKey(String forecastMeasureKey) {
        this.forecastMeasureKey = forecastMeasureKey;
    }

    public void setEffectiveFromDt(Date effectiveFromDt) {
        this.effectiveFromDt = effectiveFromDt;
    }

    public void setEffectiveToDt(Date effectiveToDt) {
        this.effectiveToDt = effectiveToDt;
    }


    /**
     * Returns the path for this value which is in the form of the startDate as a long.measureKey
     */
    public String getPath() {
        StringBuffer sb = new StringBuffer();
        if (effectiveFromDt != null) {
            sb.append(effectiveFromDt.getTime());
        }
        sb.append(".");
        sb.append(forecastMeasureKey);
        return sb.toString();
    }

    @Transient
    public abstract BigDecimal getPitValue();

    @Transient
    public abstract BigDecimal getCalculatedForecastValue();

    @Transient
    public abstract boolean isValueUnset();

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof PcmForecastValue))
            return false;
        PcmForecastValue castOther = (PcmForecastValue) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getForecast(), castOther.getForecast());
        eb.append(StringUtils.upperCase(this.getForecastMeasureKey()),
                StringUtils.upperCase(castOther.getForecastMeasureKey()));
        eb.append(this.getEffectiveFromDt(), castOther.getEffectiveFromDt());
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(StringUtils.upperCase(this.getForecastMeasureKey())).toHashCode();
    }

    @Override
    public int compareTo(PcmForecastValue other) {
        CompareToBuilder cb = new CompareToBuilder();
        cb.append(StringUtils.upperCase(this.getForecastMeasureKey()),
                StringUtils.upperCase(other.getForecastMeasureKey()));
        cb.append(this.getEffectiveFromDt(), other.getEffectiveFromDt());
        cb.append(this.getEffectiveToDt(), other.getEffectiveToDt());
        return cb.toComparison();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getForecastMeasureKey() + "@" + getEffectiveFromDt() + "]";
    }

    // States

    /**
     * Return a saved state as a JSON string
     *
     * @param key
     *            the saved state key
     * @return
     * @throws StateManipulationException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws IOException
     */
    @Transient
    public String getSavedStateAsString(String key) throws StateManipulationException {
        JsonNode node = getSavedStateAsJSON(key);
        return node == null ? null : node.toString();
    }

    /**
     * @param key
     * @return
     * @throws StateManipulationException
     */
    public JsonNode getSavedStateAsJSON(String key) throws StateManipulationException {
        ObjectNode stateRootNode = getSavedStatesRootNode();
        if (stateRootNode == null) {
            return null;
        }
        JsonNode node = stateRootNode.get(key);
        return node;
    }

    @Transient
    private ObjectNode getSavedStatesRootNode() throws StateManipulationException {
        String states = this.getSavedStates();
        if (StringUtils.isBlank(states)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        ObjectNode statesRootNode;
        try {
            statesRootNode = mapper.readValue(states, ObjectNode.class);
        } catch (Exception e) {
            throw new StateManipulationException("Error whiel reading saved states", e);
        }
        return statesRootNode;
    }

    /**
     * Save the current state of the forecast value as JSON using the key
     *
     * @param key
     * @throws StateManipulationException
     */
    public synchronized void saveState(String key) throws StateManipulationException {
        saveState(key, null);
    }

    /**
     * Add the current state and any additional 'state' using the key
     *
     * @param key
     * @param additionalState
     * @throws StateManipulationException
     */
    public synchronized void saveState(String key, Map<String, JsonNode> additionalState)
            throws StateManipulationException {
        ObjectNode currentState = getCurrentStateAsJSON();
        if (additionalState != null) {
            currentState.putAll(additionalState);
        }
        addSavedState(key, currentState);
    }

    /**
     * Add a state using a key. This state is essentially a JsonNode which may or may not represent the state of this
     * forecast value but may be pertinent to this forecast value
     *
     * @param key
     * @param currentState
     * @throws StateManipulationException
     */
    public void addSavedState(String key, JsonNode currentState) throws StateManipulationException {
        ObjectNode stateRootNode = getSavedStatesRootNode();
        if (stateRootNode == null) {
            ObjectMapper o = new ObjectMapper();
            stateRootNode = o.createObjectNode();
        }
        stateRootNode.put(key, currentState);
        setSavedStates(stateRootNode.toString());
    }

    @Transient
    public ObjectNode getCurrentStateAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        this.writeFieldsToJSON(o);
        return o;
    }

    @Transient
    public ObjectNode getCurrentStateAsTestJSON() {
        ObjectNode state =  getCurrentStateAsJSON();
        state.put("fdt", effectiveFromDt == null ? null : "today " + DateAndTimeUtils.differenceInDaysFromNowAsString(effectiveFromDt));
        state.put("tdt", effectiveToDt == null ? null : "today " + DateAndTimeUtils.differenceInDaysFromNowAsString(effectiveToDt));
        return state;
    }

    protected void writeFieldsToJSON(ObjectNode jn) {
        jn.put("mk", forecastMeasureKey);
        jn.put("uom", forecastValueUOM);
        jn.put("fdt", effectiveFromDt == null ? null : ISO8601.format(effectiveFromDt));
        jn.put("tdt", effectiveToDt == null ? null : ISO8601.format(effectiveToDt));
    }

    public class StateManipulationException extends Exception {

        private static final long serialVersionUID = 1L;

        /**
         *
         */
        public StateManipulationException() {
            super();
            // TODO Auto-generated constructor stub
        }

        /**
         * @param message
         * @param cause
         */
        public StateManipulationException(String message, Throwable cause) {
            super(message, cause);
            // TODO Auto-generated constructor stub
        }

        /**
         * @param message
         */
        public StateManipulationException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
        }

        /**
         * @param cause
         */
        public StateManipulationException(Throwable cause) {
            super(cause);
            // TODO Auto-generated constructor stub
        }

    }

}
