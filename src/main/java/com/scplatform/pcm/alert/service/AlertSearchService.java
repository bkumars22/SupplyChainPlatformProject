/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.alert.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.alert.entity.Alert;
import com.scplatform.pcm.alert.repository.AlertRepository;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.common.SCPlatformConstant;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AlertSearchService {

    private final AlertRepository alertRepository;

    public AlertSearchService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Users getCurrentUser(HttpServletRequest request) throws InvalidUserContext {
        ApplicationContext ac = AppContextHelper.getValidContext(request);
        return ac.getCurrentUser();
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlertsByUser(String userId) {
        return alertRepository.getAlertByUser(userId);
    }


    @Transactional(readOnly = true)
    public List<Alert> getAlertsByUser(HttpServletRequest request) throws InvalidUserContext {
        Users user = getCurrentUser(request);
        return alertRepository.getAlertByUser(user.getUserId());
    }

    @Transactional
    public void dismissAlerts(List<Long> ids, String userId) {
        alertRepository.dismissAlert(ids, userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getReviewAlertCounts(String userId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put(SCPlatformConstant.EMPTY_STRING, 1);
        if (userId == null || userId.isEmpty()) {
            return result;
        }
        List<Object[]> rows = alertRepository.countAlertsByTypeForUser(userId, Alert.ACTIVE);
        for (Object[] row : rows) {
            String type = (String) row[0];
            Number count = (Number) row[1];
            if (type != null && count != null) {
                result.put(type, count.intValue());
            }
        }
        return result;
    }

    public Map<String, Integer> buildAlertTypeCountMap(List<Alert> alerts) {
        Map<String, Integer> result = new HashMap<>();
        result.put(SCPlatformConstant.EMPTY_STRING, 1);
        if (alerts == null) {
            return result;
        }
        for (Alert a : alerts) {
            String type = a.getAlertType();
            if (type == null) {
                continue;
            }
            result.merge(type, 1, Integer::sum);
        }
        return result;
    }
}

