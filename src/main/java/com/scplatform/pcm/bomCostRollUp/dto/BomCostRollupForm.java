/*
 * Copyright (c) 2025 E2open Inc. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2025, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.bomCostRollUp.dto;
import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.searchframework.dto.SearchForm;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Form for Bom Cost Rollup Management
 *
 * @author mbharati
 */
@SuppressWarnings("serial")
public class BomCostRollupForm extends SearchForm {
    private SortedSet<String> pcmCostElements = new TreeSet<>();
    private List<PcmCostElement> costElements = new ArrayList<> ();
    public SortedSet<String> getPcmCostElements() {
        return pcmCostElements;
    }

    public void setPcmCostElements(SortedSet<String> pcmCostElements) {
        this.pcmCostElements = pcmCostElements;
    }
    public List<PcmCostElement> getCostElements() {
        return costElements;
    }
    public void setCostElements(List<PcmCostElement> costElements) {
        this.costElements = costElements;
    }
}
