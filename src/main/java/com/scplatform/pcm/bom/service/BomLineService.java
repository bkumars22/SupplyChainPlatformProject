/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.service;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.bom.entity.PcmBomLineAttritionRate;
import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.scplatform.pcm.bom.repository.PcmBomLineAttritionRateRepository;
import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils;
import com.scplatform.pcm.util.datetime.ISO8601;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.scplatform.pcm.util.common.SCPlatformConstant.*;

/**
 * Service class for BomLine-related business operations.
 * Handles BomLine transformation, serialization, and audit operations.
 */
@Service
@RequiredArgsConstructor
public class BomLineService {

    private final ItemService itemService;

    private final PcmBomLineAttritionRateRepository attritionRateRepository;

    /**
     * Generates a title for the BOM line
     *
     * @param bomLine the BOM line entity
     * @return formatted BOM line title
     */
    public String getTitle(BomLine bomLine) {
        return getTitle(bomLine, (String) null);
    }

    /**
     * Generates a title for the BOM line with optional changes
     *
     * @param bomLine the BOM line entity
     * @param auditBom the audit BOM
     * @return formatted BOM line title
     */
    public String getTitle(BomLine bomLine, Bom auditBom) {
        return getTitle(bomLine, auditBom, null);
    }

    /**
     * Generates a title for the BOM line with changes
     *
     * @param bomLine the BOM line entity
     * @param changes the changes description
     * @return formatted BOM line title
     */
    public String getTitle(BomLine bomLine, String changes) {
        return getTitle(bomLine, bomLine.getBom(), changes);
    }

    /**
     * Generates a full audit title for the BOM line
     *
     * @param bomLine the BOM line entity
     * @param auditBom the audit BOM
     * @param changes the changes description
     * @return formatted BOM line audit title
     */
    public String getTitle(BomLine bomLine, Bom auditBom, String changes) {
        List<Object> args = new ArrayList<>();
        args.add(getAuditBom(auditBom));
        args.add(bomLine.getItem().getItemNumber());
        args.add(bomLine.getItemQty());
        if (StringUtils.stripToNull(changes) != null) {
            args.add(changes);
        }
        if (bomLine.getSubBom() != null) {
            args.add(getAuditBom(bomLine.getSubBom()));
            if (StringUtils.stripToNull(changes) != null) {
                return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomLineTitleWithChangesWithSubBom",
                        args.toArray(), null);
            }
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomLineTitleWithSubBom", args.toArray(), null);
        }
        if (StringUtils.stripToNull(changes) != null) {
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomLineTitleWithChanges", args.toArray(), null);
        }
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomLineTitle", args.toArray(), null);
    }

    /**
     * Serializes a BOM line to JSON representation
     *
     * @param bomLine the BOM line to serialize
     * @return JsonNode representation of the BOM line
     */
    public JsonNode getAsJson(BomLine bomLine) {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("bomLineKey", bomLine.getBomLineKey());

        // Enhanced item with categories
        ObjectNode itemJson = itemService.getItemNaturalKeyAsJSON(bomLine.getItem());
        ArrayNode categoriesArray = om.createArrayNode();
        for (ItemCategory category : bomLine.getItem().getCategories()) {
            categoriesArray.add(category.getCategoriesNaturalKeyAsJSON());
        }
        itemJson.set("categories", categoriesArray);
        o.set("item", itemJson);

        o.put("description", bomLine.getDescription());
        o.put("effectiveFrom",
                DateAndTimeUtils.getFormattedDate(bomLine.getEffectiveFrom(), DEFAULT_DATE_FORMAT));
        o.put("effectiveTo",
                DateAndTimeUtils.getFormattedDate(bomLine.getEffectiveTo(), DEFAULT_DATE_FORMAT));
        o.put("groupId", bomLine.getGroupId());
        o.put("bomLineAltType", bomLine.getBomLineAltType());
        o.put("priority", bomLine.getPriority() == null ? null : bomLine.getPriority().toString());
        o.put("reasonCode", bomLine.getReasonCode());
        o.put("leadTime", bomLine.getLeadTime());
        o.put("managedFlag", bomLine.getManagedFlag());

        ArrayNode flexAttrsNode = o.putArray("flexAttributes");
        getFlexAttributeDefinitionsArrayNode(bomLine, flexAttrsNode);

        o.put("itemQty", bomLine.getItemQty() == null ? null : bomLine.getItemQty().toString());

        if (bomLine.getSubBom() != null) {
            o.set("subbom", getAsJson(bomLine.getSubBom()));
        } else {
            o.putNull("subbom");
        }

        return o;
    }

    /**
     * Helper method to serialize BOM line flex attributes to JSON array
     *
     * @param bomLine the BOM line entity
     * @param flexAttrsNode the JSON array to populate
     */
    private void getFlexAttributeDefinitionsArrayNode(BomLine bomLine, ArrayNode flexAttrsNode) {
        List<FlexAttributeDefn> flexAttrsList = FlexAttributeManager.BOMLINE.getFlexAttributeDefinitionList();
        for (FlexAttributeDefn attrDfn : flexAttrsList) {
            ObjectMapper om = new ObjectMapper();
            ObjectNode o = om.createObjectNode();
            String associatedAttribute = attrDfn.getAssociatedAttribute();
            o.put(associatedAttribute, String.valueOf(bomLine.getFlexAttribute(associatedAttribute)));
            flexAttrsNode.add(o);
        }
    }

    /**
     * Get flex attribute definitions for BOM line
     *
     * @return list of flex attribute definitions
     */
    public List<FlexAttributeDefn> getFlexAttributeDefinitions() {
        return FlexAttributeManager.BOMLINE.getFlexAttributeDefinitionList();
    }

    /**
     * Returns attrition rates keyed by defect type for a BOM line and BOM item.
     *
     * @param bomLine target BOM line
     * @param bomItem target BOM item
     * @return map keyed by defect type
     */
    public Map<PcmDefectType, PcmBomLineAttritionRate> getBomLineAttritionRates(BomLine bomLine, Item bomItem) {
        List<PcmBomLineAttritionRate> rates = attritionRateRepository.findByIdBomLineAndIdBomItem(bomLine, bomItem);
        Map<PcmDefectType, PcmBomLineAttritionRate> result = new HashMap<>();
        for (PcmBomLineAttritionRate rate : rates) {
            result.put(rate.getDefectType(), rate);
        }
        return result;
    }

    /**
     * Generates an audit message for the BOM
     *
     * @param bom the BOM entity
     * @return formatted audit message
     */
    public String getAuditBom(Bom bom) {
        List<Object> args = new ArrayList<>();
        args.add(getAuditBomTitle(bom));
        args.add(bom.getStatus());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bom", args.toArray(), null);
    }

    /**
     * Generates an audit title for the BOM
     *
     * @param bom the BOM entity
     * @return formatted BOM title
     */
    public String getAuditBomTitle(Bom bom) {
        List<Object> args = new ArrayList<>();
        if (bom.getBomName() != null) {
            args.add(bom.getBomName());
        } else {
            args.add(bom.getItem().getItemNumber());
        }
        args.add(bom.getBomVersion());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomTitle", args.toArray(), null);
    }

    /**
     * Serializes a BOM to JSON representation
     *
     * @param bom the BOM to serialize
     * @return JsonNode representation of the BOM
     */
    public JsonNode getAsJson(Bom bom) {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("bomKey", bom.getBomKey());
        o.put("bomName", bom.getBomName());
        o.put("bomDesc", bom.getBomDesc());
        o.put("status", bom.getStatus());
        o.put("bomVersion", bom.getBomVersion().getVersion());
        o.put("bomRevision", bom.getBomVersion().getRevision());
        if (bom.getBusinessEntity() != null) {
            ObjectNode businessEntityAsJSON = bom.getBusinessEntity().getNaturalKeyAsJSON();
            o.set("business", businessEntityAsJSON);
        } else {
            o.putNull("business");
        }
        o.set("item", itemService.getItemNaturalKeyAsJSON(bom.getItem()));
        o.put("isTopLevel", (bom.getIsTopLevel() == null ? null : bom.getIsTopLevel().toString()));
        o.put("leadTime", (bom.getLeadTime() == null ? null : bom.getLeadTime().toString()));
        o.put("effectiveFrom", ISO8601.safeFormat(bom.getEffectiveFrom()));
        o.put("effectiveTo", ISO8601.safeFormat(bom.getEffectiveTo()));
        ArrayNode lines = o.putArray("bomLines");
        getBomLinesAsJson(bom, lines);
        return o;
    }

    /**
     * Helper method to serialize BOM lines to JSON array
     *
     * @param bom the BOM entity
     * @param lines the JSON array to populate
     */
    private void getBomLinesAsJson(Bom bom, ArrayNode lines) {
        for (BomLine line : bom.getSortedBomLines()) {
            lines.add(getAsJson(line));
        }
    }
}
