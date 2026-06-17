/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.service;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.upload.entity.LoadEvent;
import com.scplatform.pcm.upload.loader.MessageLoaderStatus;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;

import lombok.RequiredArgsConstructor;

/**
 * Separate Spring bean for updating LoadJob after async MessageLoader completes.
 * <p>
 * Extracted from {@link UploadFileService} so that {@code @Transactional} works
 * correctly when called from an async thread (calls go through the Spring proxy).
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class LoadJobUpdateService {

    private final LoadJobRepository loadJobRepository;
    private final LoadEventRepository loadEventRepository;

    /**
     * Updates the LoadJob state/status based on the loader result and records LoadEvents.
     */
    @Transactional
    public void updateLoadJobFromMessageLoader(String externalId, MessageLoaderStatus status) {
        log.info("[DEBUG] updateLoadJobFromMessageLoader ENTERED: externalId='{}', status={}", 
                externalId, status != null ? "resultCode=" + status.getResultCode() : "NULL");
        loadJobRepository.findByExternalId(externalId).ifPresentOrElse(job -> {
            log.info("[DEBUG] Found LoadJob: key='{}', currentState='{}', currentStatus='{}'", 
                    job.getLoadJobKey(), job.getState(), job.getStatus());
            int rc = status.getResultCode();
            String statusStr;
            if (rc == MessageLoaderStatus.SUCCESS) {
                job.setState("COMPLETED");
                job.setStatus("SUCCESS");
                statusStr = "SUCCESS";
            } else if (rc == MessageLoaderStatus.WARN) {
                job.setState("COMPLETED");
                job.setStatus("WARN");
                statusStr = "WARN";
            } else if (rc == MessageLoaderStatus.ERROR) {
                job.setState("COMPLETED");
                job.setStatus("ERROR");
                statusStr = "ERROR";
            } else {
                job.setState("COMPLETED");
                job.setStatus("UNKNOWN");
                statusStr = "UNKNOWN";
            }

            LocalDateTime now = LocalDateTime.now();

            // Record error/warning messages as load events
            String resultMessage = status.getResultMessage();
            if (resultMessage != null && !resultMessage.isBlank()) {
                String eventType = (rc == MessageLoaderStatus.ERROR) ? "GENERAL_ERROR" : "DATA_ERROR";
                for (String line : resultMessage.split("\n")) {
                    if (!line.isBlank()) {
                        LoadEvent event = new LoadEvent();
                        event.setLoadJob(job);
                        event.setType(eventType);
                        event.setLoadEventData(line.trim());
                        event.setLoadEventContext("NA");
                        event.setInsertDate(now);
                        loadEventRepository.save(event);
                    }
                }
            }

            // Record overall status event
            LoadEvent statusEvent = new LoadEvent();
            statusEvent.setLoadJob(job);
            statusEvent.setType("LOAD_STATUS");
            statusEvent.setLoadEventData("Status: " + statusStr);
            statusEvent.setLoadEventContext("NA");
            statusEvent.setInsertDate(now);
            loadEventRepository.save(statusEvent);

            // Record load statistics
            Map<String, Integer> stats = status.getStatistics();
            if (stats != null && !stats.isEmpty()) {
                LoadEvent statEvent = new LoadEvent();
                statEvent.setLoadJob(job);
                statEvent.setType("LOAD_STAT");
                statEvent.setLoadEventData(stats.toString());
                statEvent.setLoadEventContext("NA");
                statEvent.setInsertDate(now);
                loadEventRepository.save(statEvent);
            }

            loadJobRepository.save(job);
            log.info("LoadJob {} updated: state={}, status={}", externalId, job.getState(), job.getStatus());
        }, () -> log.error("LoadJob not found for externalId: {}", externalId));
    }

    /**
     * Marks a LoadJob as COMPLETED/ERROR when an uncaught exception occurs.
     */
    @Transactional
    public void markLoadJobError(String externalId, String errorMessage) {
        loadJobRepository.findByExternalId(externalId).ifPresentOrElse(job -> {
            job.setState("COMPLETED");
            job.setStatus("ERROR");
            loadJobRepository.save(job);

            if (errorMessage != null && !errorMessage.isBlank()) {
                LoadEvent event = new LoadEvent();
                event.setLoadJob(job);
                event.setType("GENERAL_ERROR");
                event.setLoadEventData(errorMessage);
                event.setLoadEventContext("NA");
                event.setInsertDate(LocalDateTime.now());
                loadEventRepository.save(event);
            }

            log.info("LoadJob {} marked ERROR: {}", externalId, errorMessage);
        }, () -> log.error("LoadJob not found for externalId: {}", externalId));
    }
}
