/**
 *	ComputedCostRecordValueService.java
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
package com.scplatform.pcm.cost.service;

import bsh.Interpreter;
import com.scplatform.pcm.common.service.LocatorService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmCostRecordRange;
import com.scplatform.pcm.cost.entity.PcmCostRecordValue;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.enums.PcmCostElementType;
import com.scplatform.pcm.cost.enums.PcmCostRecordValueType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class to evaluate the computed cost value for a {@link PcmCostRecordValue}.
 * <p>
 * If the cost value type is 'C' (Custom), the system looks up a BeanShell script via the property
 * {@code pcm.cost.value.evaluation.<COST_TYPE_NAME>.<COST_ELEMENT_NAME>} and executes it.
 * Scripts are located in the directories configured by {@code pcm.cost.value.evaluation.scripts.dir}.
 */
@Service
@RequiredArgsConstructor
public class ComputedCostRecordValueService {

    private final PcmConfigUtil pcmConfigUtil;
    private final LocatorService locatorService;
    private final PcmCostRecordRangeService pcmCostRecordRangeService;

    private final Map<String, ScriptFileContents> scripts = new HashMap<>();

    /**
     * Static holder so that JPA entity {@link PcmCostRecordValue} can reach this service
     * without being a Spring-managed bean itself.
     */
    private static ComputedCostRecordValueService instance;

    @PostConstruct
    void registerInstance() {
        instance = this;
    }

    public static ComputedCostRecordValueService getInstance() {
        return instance;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Gets a computed value for a cost record value based on its cost value type.
     * Returns the raw cost value for non-computed types; executes a custom script for type 'C';
     * applies percentage calculation for percentage-based types.
     *
     * @param val the cost record value
     * @return computed BigDecimal value, or null if val is null
     */
    public BigDecimal computeCostValue(PcmCostRecordValue val) {
        if (val == null) {
            return null;
        }
        String costValueType = val.getCostValueType();
        PcmCostRecordValueType vt = PcmCostRecordValueType.valueOf(costValueType);
        switch (vt) {
            case C:
                PcmCostRecord costRecord = val.getCostRecordRange().getCostRecord();
                PcmCostType ct = costRecord.getCostType();
                PcmCostElement element = val.getCostElement();
                String script = getScript(ct, element);
                Interpreter interpreter = new Interpreter();
                try {
                    ComputedCostValueHelper helper = new ComputedCostValueHelper(costRecord, val);
                    interpreter.set("crv", helper);
                    return (BigDecimal) interpreter.eval(script);
                } catch (Exception e) {
                    throw new RuntimeException("Error executing custom script for cost record value because "
                            + e.getMessage(), e);
                }
            case PM:
                return percentageCalc(val, PcmCostElementType.MATERIAL);
            case PT:
                return percentageCalc(val, PcmCostElementType.TRANSFORMATION);
            case PF:
                return percentageCalc(val, PcmCostElementType.FIXED);
            case P:
                return percentageCalc(val, PcmCostElementType.MATERIAL);
            default:
                return val.getCostValue();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private List<String> getSearchPaths() {
        return pcmConfigUtil.getList("pcm.cost.value.evaluation.scripts.dir");
    }

    private BigDecimal percentageCalc(PcmCostRecordValue crv, PcmCostElementType type) {
        if (crv == null) {
            return null;
        }
        if (type == null) {
            throw new IllegalArgumentException("Cost Element type must be specified");
        }
        if (type.equals(crv.getCostElement().getCostElementType())) {
            throw new IllegalStateException("Cannot compute percentage of type " + type
                    + " since the cost record value is of the same type. Use Custom value type instead");
        }
        PcmCostRecord cr = crv.getCostRecordRange().getCostRecord();
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        PcmCostRecordRange activeRange = cr.getActiveCostRecordRange();
        if (activeRange != null) {
            total = pcmCostRecordRangeService.getComputedTotalByCostElementType(activeRange, type);
        }
        return total.multiply(crv.getCostValue().movePointLeft(2)).setScale(6, RoundingMode.HALF_UP);
    }

    private File getScriptFile(String key) {
        String srcFileNameProp = "pcm.cost.value.evaluation." + key;
        String srcFileName = StringUtils.trimToNull(pcmConfigUtil.getString(srcFileNameProp, null));
        if (srcFileName == null) {
            throw new RuntimeException("Cannot find script property " + srcFileNameProp
                    + " for a custom cost element value type");
        }
        List<String> searchPaths = getSearchPaths();
        URL resourceURL = locatorService.locateResource(srcFileName, searchPaths, true);
        if (resourceURL == null) {
            throw new RuntimeException("Did not find file " + srcFileName + " in the following paths " + searchPaths);
        }
        try {
            return new File(resourceURL.toURI());
        } catch (URISyntaxException e) {
            return new File(resourceURL.getPath());
        }
    }

    private String getScript(PcmCostType type, PcmCostElement element) {
        String key = type.getCostTypeName() + '.' + element.getCostElementName();
        File scriptFile = getScriptFile(key);
        long sflm = scriptFile.lastModified();
        ScriptFileContents sfc = this.scripts.get(key);
        if (sfc == null || sflm > sfc.getLastModified()) {
            synchronized (this) {
                try {
                    ScriptFileContents nsfc = new ScriptFileContents(scriptFile);
                    this.scripts.put(key, nsfc);
                    return nsfc.getContents();
                } catch (IOException e) {
                    throw new RuntimeException("Error loading file " + scriptFile);
                }
            }
        }
        return sfc.getContents();
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    /**
     * Helper facade passed to external BeanShell scripts so scripts cannot modify internal objects.
     */
    public class ComputedCostValueHelper {
        private final PcmCostRecord costRecord;
        private final PcmCostRecordValue costRecordValue;

        public ComputedCostValueHelper(PcmCostRecord costRecord, PcmCostRecordValue costRecordValue) {
            this.costRecord = costRecord;
            this.costRecordValue = costRecordValue;
        }

        public BigDecimal getCostRecordCostValue() {
            return this.costRecordValue.getCostValue();
        }

        public BigDecimal getCostRecordCostValue(String element) {
            PcmCostRecordValue crv = this.costRecord.getCostRecordValue(element);
            return crv != null ? crv.getCostValue() : null;
        }

        public BigDecimal getComputedCostRecordCostValue(String element) {
            String crvElement = this.costRecordValue.getCostElement().getCostElementName();
            if (crvElement.equals(element)) {
                throw new IllegalStateException(
                        "Cannot call getComputedCostRecordCostValue on the same element as this cost record value.");
            }
            PcmCostRecordValue crv = this.costRecord.getCostRecordValue(element);
            return crv != null ? crv.getComputedCostValue() : null;
        }

        public BigDecimal getTotalByCostElementType(String ctype) {
            PcmCostElementType ceType = PcmCostElementType.valueOf(ctype);
            if (ceType == null) {
                throw new IllegalArgumentException("No type by name " + ctype);
            }
            PcmCostRecordRange activeRange = this.costRecord.getActiveCostRecordRange();
            if (activeRange != null) {
                return pcmCostRecordRangeService.getTotalByCostElementType(activeRange, ceType);
            }
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        }

        public BigDecimal getComputedTotalByCostElementType(String ctype) {
            PcmCostElementType ceType = PcmCostElementType.valueOf(ctype);
            if (ceType == null) {
                throw new IllegalArgumentException("No type by name " + ctype);
            }
            if (ceType == this.costRecordValue.getCostElement().getCostElementType()) {
                throw new IllegalStateException(
                        "Cannot call getComputedTotalByCostElementType for the same type as the cost record value whose value is being computed");
            }
            PcmCostRecordRange activeRange = this.costRecord.getActiveCostRecordRange();
            if (activeRange != null) {
                return pcmCostRecordRangeService.getComputedTotalByCostElementType(activeRange, ceType);
            }
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        }

        public BigDecimal getTotalNotOfCostElementType(String ctype) {
            PcmCostElementType ceType = PcmCostElementType.valueOf(ctype);
            if (ceType == null) {
                throw new IllegalArgumentException("No type by name " + ctype);
            }
            PcmCostRecordRange activeRange = this.costRecord.getActiveCostRecordRange();
            if (activeRange != null) {
                return pcmCostRecordRangeService.getTotalNotOfCostElementType(activeRange, ceType);
            }
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        }

        public BigDecimal getComputedTotalNotOfCostElementType() {
            PcmCostElementType ceType = this.costRecordValue.getCostElement().getCostElementType();
            PcmCostRecordRange activeRange = this.costRecord.getActiveCostRecordRange();
            if (activeRange != null) {
                return pcmCostRecordRangeService.getTotalNotOfCostElementType(activeRange, ceType);
            }
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        }
    }

    /**
     * Cache entry holding script file contents and last-modified timestamp.
     */
    private static class ScriptFileContents {
        private long lastModified;
        private String contents;

        ScriptFileContents(File file) throws IOException {
            this.lastModified = file.lastModified();
            this.contents = FileUtils.readFileToString(file);
        }

        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
        public String getContents() { return contents; }
        public void setContents(String contents) { this.contents = contents; }
    }
}
