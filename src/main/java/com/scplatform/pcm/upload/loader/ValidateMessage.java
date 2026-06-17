/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.accessControl.service.AccessControlService;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.upload.entity.LoadEvent;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.common.SCPlatformConstant;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidateMessage extends BaseImporter {

    private boolean slCheck = false;

    private final BusinessEntityService businessEntityService;
    private final AccessControlService  accessControlService;
    private final FunctionalGroupRepository fgRepository;

    private static class BusinessEntityReference {
        final String entityName;
        final String entityType;

        BusinessEntityReference(String entityName, String entityType) {
            this.entityName = entityName;
            this.entityType = entityType;
        }
    }

    /**
     * Required constructor — forwards Spring-managed beans to {@link BaseImporter}.
     *
     * @param loadJobRepository   injected JPA repository for LoadJob
     * @param loadEventRepository injected JPA repository for LoadEvent
     * @param pcmConfigUtil       injected Spring config utility
     */
    public ValidateMessage(LoadJobRepository loadJobRepository,
                           LoadEventRepository loadEventRepository,
                           PcmConfigUtil pcmConfigUtil,
                           BusinessEntityService businessEntityService,
                           AccessControlService accessControlService,
                           FunctionalGroupRepository fgRepository) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.businessEntityService = businessEntityService;
        this.accessControlService  = accessControlService;
        this.fgRepository          = fgRepository;
    }

    // -----------------------------------------------------------------------
    // Validation
    // -----------------------------------------------------------------------

    /**
     * Validates all business-entity references in the XML stream.
     *
     * @param xmlr   XML stream to validate
     * @param status status object to record validation results
     * @return {@code true} if all references are valid; {@code false} otherwise
     * @throws XMLStreamException    on XML parsing error
     * @throws MessageLoaderException on loader-level error
     */
    public boolean validateBusinessEntity(XMLStreamReader xmlr, MessageLoaderStatus status)
            throws XMLStreamException, MessageLoaderException {

        long   runningTimer   = System.currentTimeMillis();
        long   searchTimer    = 0;
        long   timer          = 0;
        String itemIdentifier = null;

        if (log.isInfoEnabled()) {
            log.info("Validation starting....");
        }

        // Apply ACL visibility filter for restricted users
        Users activeUser = getActiveUser();
        Set<Long> restrictedBusinessKeys = null;
        if (activeUser != null && accessControlService.getHasRestrictedVisiblity(activeUser)) {
            restrictedBusinessKeys = new HashSet<>();
            if (activeUser.getBusinessEntity() != null) {
                restrictedBusinessKeys.add(activeUser.getBusinessEntity().getBusinessEntityKey());
            }
            List<Long> agentKeys = accessControlService.getResponderBusinessEntityKeys(
                    activeUser, SCPlatformConstant.AGENT_BE_ACL);
            restrictedBusinessKeys.addAll(agentKeys);
        }

        List<String> errors  = new ArrayList<>();
        boolean      isValid = true;

        while (xmlr.hasNext()) {
            xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) {
                continue;
            }

            List<BusinessEntityReference> beList = new ArrayList<>();
            Location loc = null;

            if ("Item".equals(xmlr.getLocalName()) || "BomLine".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "businessEntity"),
                            xmlr.getAttributeValue(null, "businessEntityType")));
                    loc = xmlr.getLocation();
                }
            } else if ("ApprovedVendorListItem".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement() && !isSoftErrorsEnabled()) {
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "vendorBusinessEntity"),
                            xmlr.getAttributeValue(null, "vendorBusinessEntityType")));
                    loc = xmlr.getLocation();
                }
            } else if ("ApprovedManufacturerListItem".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement() && !isSoftErrorsEnabled()) {
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "manufacturerBusinessEntity"),
                            xmlr.getAttributeValue(null, "manufacturerBusinessEntityType")));
                    loc = xmlr.getLocation();
                }
            } else if ("SupplierAllocation".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "businessEntity"),
                            xmlr.getAttributeValue(null, "businessEntityType")));
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "supplierBusinessEntity"),
                            xmlr.getAttributeValue(null, "supplierBusinessEntityType")));
                    loc = xmlr.getLocation();
                }
            } else if ("SourcingLane".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    slCheck       = true;
                    itemIdentifier = xmlr.getAttributeValue(null, "itemIdentifier");
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "businessEntity"),
                            xmlr.getAttributeValue(null, "businessEntityType")));
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "fromBusinessEntity"),
                            xmlr.getAttributeValue(null, "fromBusinessEntityType")));
                    loc = xmlr.getLocation();
                }
            } else if ("CostRecord".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    beList.add(new BusinessEntityReference(
                            xmlr.getAttributeValue(null, "costProviderBusinessEntity"),
                            xmlr.getAttributeValue(null, "costProviderBusinessEntityType")));
                    loc = xmlr.getLocation();
                }
            }

            while (!beList.isEmpty()) {
                BusinessEntityReference ber    = beList.remove(0);
                String                  beName = ber.entityName;
                String                  beType = ber.entityType;

                if (beName != null && beType != null) {
                    if (log.isInfoEnabled()) {
                        timer = System.currentTimeMillis();
                    }
                    boolean ret = existsBusinessEntity(beName, beType, loc, restrictedBusinessKeys);
                    if (log.isInfoEnabled()) {
                        searchTimer += (System.currentTimeMillis() - timer);
                    }
                    if (!ret) {
                        errors.add("BusinessNotFoundOrRestricted:" + beName + "|" + beType
                                .concat(getFgFromItem(itemIdentifier))
                                .concat(":")
                                .concat(getLocation(loc)));
                        if (isValid) {
                            status.setLoadJobId(loadJobId);
                        }
                    }
                    isValid = isValid && ret;
                }
            }
        }

        if (!errors.isEmpty()) {
            status.setResultMessage(StringUtils.join(errors, "\n"));
            status.setResultCode(MessageLoaderStatus.ERROR);
        }


        if (log.isInfoEnabled()) {
            runningTimer = System.currentTimeMillis() - runningTimer;
            log.info("Validation search time: {}", searchTimer);
            log.info("Validation Processing Time: {}", runningTimer);
            log.info("Validation completed: {}", isValid);
        }
        return isValid;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Checks whether a business entity with the given name and type exists,
     * and — when the active user has restricted visibility — whether the found
     * entity is within the user's permitted set of business-entity keys.
     */
    private boolean existsBusinessEntity(String beName, String beType, Location loc,
            Set<Long> restrictedBusinessKeys) throws MessageLoaderException {
        var be = businessEntityService.findUniqueBusinessByName(beName, beType, true);
        if (be == null) {
            if (recordEvents) {
                LoadEvent loadEvent = recordEvent(
                        LoadEvent.LoadEventType.MISSING_BUSINESS_ENTITY,
                        beName + "|" + beType, getLocation(loc));
                loadJobId = loadEvent.getLoadJob().getLoadJobKey();
            }
            return false;
        }
        // Visibility check: if the active user is restricted, the BE must be in their allowed set
        if (restrictedBusinessKeys != null && !restrictedBusinessKeys.isEmpty()
                && !restrictedBusinessKeys.contains(be.getBusinessEntityKey())) {
            if (recordEvents) {
                LoadEvent loadEvent = recordEvent(
                        LoadEvent.LoadEventType.MISSING_BUSINESS_ENTITY,
                        beName + "|" + beType, getLocation(loc));
                loadJobId = loadEvent.getLoadJob().getLoadJobKey();
            }
            return false;
        }
        return true;
    }

    /**
     * Returns a comma-separated string of functional-group names for the given
     * item number when processing a SourcingLane element.
     */
    private String getFgFromItem(String itemNumber) {
        if (slCheck && itemNumber != null) {
            List<String> fgNames = fgRepository.getFunctionalGroupListByItem(itemNumber)
                    .stream()
                    .map(fg -> fg.getName())
                    .collect(Collectors.toList());
            if (!fgNames.isEmpty()) {
                return "|FG:" + String.join(",", fgNames);
            }
        }
        return "";
    }

    // -----------------------------------------------------------------------
    // BaseImporter abstract method implementations
    // -----------------------------------------------------------------------

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws XMLStreamException, MessageLoaderException {
        // not used for validation
    }

    @Override
    public void updateStats(MessageLoaderStatus status) {
        // no counters to update for validation
    }

    @Override
    public int getCount() {
        return 0;
    }
}
