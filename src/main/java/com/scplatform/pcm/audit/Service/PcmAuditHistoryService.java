/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.audit.Service;

import java.util.HashSet;
import java.util.Set;

import com.scplatform.pcm.audit.repository.PcmAuditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PcmAuditHistoryService {

    private final PcmAuditHistoryRepository pcmAuditHistoryRepository;

    /**
     * Get the minimum year from audit history records.
     * Used to determine the start year for audit reports.
     *
     * @return the minimum year from ACTION_DATE column, or null if no records exist
     */
    public Integer getStartYear() {
        return pcmAuditHistoryRepository.getStartYear();
    }

    public static void writeAuditRecord(String userId, String actionPerformed, String targetType, Object targetKey,
                                        String comment) {
        writeAuditRecord(userId, null, actionPerformed, targetType, targetKey, comment, null, null);
    }

    public static void writeAuditRecord(String userId, String lastLoadedByUser, String actionPerformed, String targetType, Object targetKey,
                                        String comment, Object subTargetKey, String subTargetType) {
        Set<String> targetKeys = new HashSet<>();
        if (targetKey != null) {
            targetKeys.add(targetKey.toString());
        } else {
            targetKeys.add("NOKEY");
        }

        writeAuditRecord(userId, lastLoadedByUser, actionPerformed, targetType, targetKeys, comment, subTargetKey, subTargetType);
    }

}
