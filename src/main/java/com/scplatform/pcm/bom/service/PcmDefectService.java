/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.bom.service;

import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.scplatform.pcm.bom.repository.PcmDefectTypeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for PcmDefectType-related operations.
 * Handles retrieval and business logic for defect types.
 */
@Service
@RequiredArgsConstructor
public class PcmDefectService {

    private static final Logger logger = LoggerFactory.getLogger(PcmDefectService.class);

    private final PcmDefectTypeRepository defectTypeRepository;

    /**
     * Retrieves all defect types ordered by defect name in ascending order.
     *
     * @return list of all PcmDefectType entities ordered by defectName, or empty list if none found
     */
    public List<PcmDefectType> getDefectTypes() {
        logger.debug("Fetching all defect types ordered by name");
        return defectTypeRepository.getDefectTypes();
    }

    /**
     * Check if any defect types exist in the system.
     * Useful for determining if attrition rate feature should be enabled.
     *
     * @return true if at least one defect type exists, false otherwise
     */
    public boolean hasDefectTypes() {
        return !getDefectTypes().isEmpty();
    }

}

