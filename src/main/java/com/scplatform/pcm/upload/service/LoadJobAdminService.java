/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.service;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.upload.dto.LoadJobAdminForm;
import com.scplatform.pcm.upload.entity.LoadEvent;
import com.scplatform.pcm.upload.entity.LoadJob;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service handling load-job detail business logic.
 * Migrated from {@code LoadJobAdminAction}.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class LoadJobAdminService {

    private static final String REPLAY_TAG = "(Replayed)";

    private final LoadJobRepository loadJobRepository;
    private final LoadEventRepository loadEventRepository;
    private final PcmConfigUtil pcmConfigUtil;
    private final LoadEventService loadEventService;
    private final LoadJobService loadJobService;

    // -----------------------------------------------------------------------
    // readLoadJob — mirrors LoadJobAdminAction.readLoadJob()
    // -----------------------------------------------------------------------

    @Transactional(readOnly = true)
    public LoadJobAdminForm readLoadJob(HttpServletRequest request, String loadJobKey)
            throws InvalidUserContext {

        AppContextHelper.getValidContext(request);
        LoadJobAdminForm form = new LoadJobAdminForm();
        form.setSelectedLoadJobKey(loadJobKey);

        if (loadJobKey == null || loadJobKey.isBlank()) {
            return form;
        }

        Optional<LoadJob> jobOpt = loadJobRepository.findWithEventsByLoadJobKey(loadJobKey);
        if (jobOpt.isEmpty()) {
            log.warn("LoadJob not found for key: {}", loadJobKey);
            return form;
        }

        LoadJob job = jobOpt.get();
        form.setSelectedLoadJob(job);
        form.setReplayAllowed(resolveReplayAllowed(request, job));
        return form;
    }

    // -----------------------------------------------------------------------
    // deleteLoadJob — mirrors LoadJobAdminAction.deleteLoadJob()
    // -----------------------------------------------------------------------

    @Transactional
    public LoadJobAdminForm deleteLoadJob(HttpServletRequest request, String loadJobKey)
            throws InvalidUserContext {

        ApplicationContext ac = AppContextHelper.getValidContext(request);
        if (!AppContextHelper.hasAccess(ac, "UPDOWN", "DeleteJob")) {
            throw new InvalidUserContext("Access denied: UPDOWN/DeleteJob");
        }
        LoadJobAdminForm form = new LoadJobAdminForm();
        form.setSelectedLoadJobKey(loadJobKey);

        Optional<LoadJob> jobOpt = loadJobRepository.findWithEventsByLoadJobKey(loadJobKey);
        if (jobOpt.isEmpty()) {
            log.warn("deleteLoadJob: job not found for key {}", loadJobKey);
            return form;
        }

        LoadJob job = jobOpt.get();
        loadJobRepository.delete(job);
        form.setSelectedLoadJob(job);
        return form;
    }

    @Transactional
    public LoadJobAdminForm replayLoadJob(HttpServletRequest request, String loadJobKey)
            throws InvalidUserContext {

        ApplicationContext ac = AppContextHelper.getValidContext(request);
        if (!AppContextHelper.hasAccess(ac, "UPDOWN", "UploadFile")) {
            throw new InvalidUserContext("Access denied: UPDOWN/UploadFile");
        }
        String userId = ac.getCurrentUser().getUserId();

        LoadJobAdminForm form = new LoadJobAdminForm();
        form.setSelectedLoadJobKey(loadJobKey);

        Optional<LoadJob> jobOpt = loadJobRepository.findWithEventsByLoadJobKey(loadJobKey);
        if (jobOpt.isEmpty()) {
            log.warn("replayLoadJob: job not found for key {}", loadJobKey);
            form.setJobErrorDetails("Job not found: " + loadJobKey);
            return form;
        }

        LoadJob loadJob = jobOpt.get();

        if (loadJob.getExternalId() == null) {
            form.setSelectedLoadJob(loadJob);
            form.setReplayAllowed(resolveReplayAllowed(request, loadJob));
            return form;
        }

        boolean replayOwnJobsOnly = pcmConfigUtil.getBooleanValue(
                "pcm.upload.manage.replay.ownJobsOnly", false);
        if (!loadJobService.canLoadJobBeReplayedByUserId(loadJob, userId, replayOwnJobsOnly)) {
            form.setJobErrorDetails("errors.loadjob.replay.invaliduser:" + userId);
            form.setSelectedLoadJob(loadJob);
            return form;
        }

        String newExternalId = UUID.randomUUID().toString().replace("-", "");
        log.info("E2NA replay stub: original={} → new={}", loadJob.getExternalId(), newExternalId);

        String jobType = loadJob.getLoadJobType();
        if (jobType != null && !jobType.endsWith(REPLAY_TAG)) {
            jobType += REPLAY_TAG;
        }

        LoadJob replayJob = new LoadJob();
        replayJob.setLoadJobKey(UUID.randomUUID().toString().replace("-", ""));
        replayJob.setDatasource(loadJob.getDatasource());
        replayJob.setLoadedBy(userId);
        replayJob.setLoadJobType(jobType);
        replayJob.setState("REPLAY");
        replayJob.setStatus("PROCESSING");
        replayJob.setExternalId(newExternalId);
        replayJob.setLoadDate(LocalDateTime.now());
        loadJobRepository.save(replayJob);

        form.setSelectedLoadJob(replayJob);
        form.setSelectedLoadJobKey(replayJob.getLoadJobKey());
        form.setReplayAllowed(resolveReplayAllowed(request, replayJob));
        return form;
    }


    @Transactional
    public LoadJobAdminForm updateAsyncStatus(HttpServletRequest request, String loadJobKey)
            throws InvalidUserContext {

        ApplicationContext acUpload = AppContextHelper.getValidContext(request);
        if (!AppContextHelper.hasAccess(acUpload, "UPDOWN", "UploadFile")) {
            throw new InvalidUserContext("Access denied: UPDOWN/UploadFile");
        }

        LoadJobAdminForm form = new LoadJobAdminForm();
        form.setSelectedLoadJobKey(loadJobKey);

        Optional<LoadJob> jobOpt = loadJobRepository.findWithEventsByLoadJobKey(loadJobKey);
        if (jobOpt.isEmpty()) {
            log.warn("updateAsyncStatus: job not found for key {}", loadJobKey);
            return form;
        }

        LoadJob loadJob = jobOpt.get();

        if (loadJob.getExternalId() != null) {
            List<String> withoutE2na = pcmConfigUtil.getList("pcm.excel-upload.withoutE2NA");
            boolean isE2naType = withoutE2na == null || !withoutE2na.contains(loadJob.getLoadJobType());

            if (isE2naType) {
                // TODO: query E2NA state via ProtocolClient.getState(loadJob.getExternalId())
                //       and call E2NAClientUtil.updataJobResult(clientResult) for COMPLETED/FAILED.
                //       For now fall through to refresh the job from DB.
                log.info("E2NA status-check stub for externalId={}", loadJob.getExternalId());
            }
            // Re-load after any potential E2NA update
            loadJob = loadJobRepository.findWithEventsByLoadJobKey(loadJobKey).orElse(loadJob);
        }

        form.setSelectedLoadJob(loadJob);
        form.setReplayAllowed(resolveReplayAllowed(request, loadJob));
        return form;
    }

    // -----------------------------------------------------------------------
    // clearLoadErrors — mirrors LoadJobAdminAction.clearLoadErrors()
    // -----------------------------------------------------------------------

    @Transactional
    public LoadJobAdminForm clearLoadErrors(HttpServletRequest request,
                                            String loadJobKey,
                                            List<Long> selectedEventKeys)
            throws InvalidUserContext {

        ApplicationContext ac = AppContextHelper.getValidContext(request);
        if (!AppContextHelper.hasAccess(ac, "UPDOWN", "ClearUploadEvent")) {
            throw new InvalidUserContext("Access denied: UPDOWN/ClearUploadEvent");
        }

        LoadJobAdminForm form = new LoadJobAdminForm();
        form.setSelectedLoadJobKey(loadJobKey);

        if (selectedEventKeys == null || selectedEventKeys.isEmpty()) {
            reloadJobIntoForm(form, loadJobKey, request);
            return form;
        }

        for (Long key : selectedEventKeys) {
            Optional<LoadEvent> eventOpt = loadEventRepository.findById(key);
            if (eventOpt.isEmpty()) {
                log.warn("clearLoadErrors: event not found for key {}", key);
                continue;
            }
            LoadEvent event = eventOpt.get();
            if (!loadEventService.isLoadEventCleared(event)) {
                loadEventService.clearLoadEvent(event);
                loadEventRepository.save(event);
                log.info("User {} cleared event {} on job {}", ac.getCurrentUser().getUserId(),
                        key, loadJobKey);
            }
        }

        reloadJobIntoForm(form, loadJobKey, request);
        return form;
    }

    // -----------------------------------------------------------------------
    // setBusinessAlias — mirrors LoadJobAdminAction.setBusinessAlias()
    // NOTE: BomUtil / BusinessEntity integration is a TODO pending legacy jar availability
    // -----------------------------------------------------------------------

    @Transactional
    public LoadJobAdminForm setBusinessAlias(HttpServletRequest request,
                                             String loadJobKey,
                                             String assignToBusinessKey,
                                             boolean clearAll,
                                             List<Long> selectedEventKeys)
            throws InvalidUserContext {

        ApplicationContext acAlias = AppContextHelper.getValidContext(request);
        if (!AppContextHelper.hasAccess(acAlias, "UPDOWN", "CorrectBEAliasEvent")) {
            throw new InvalidUserContext("Access denied: UPDOWN/CorrectBEAliasEvent");
        }

        LoadJobAdminForm form = new LoadJobAdminForm();
        form.setSelectedLoadJobKey(loadJobKey);

        if (assignToBusinessKey == null || assignToBusinessKey.isBlank()
                || selectedEventKeys == null || selectedEventKeys.isEmpty()) {
            reloadJobIntoForm(form, loadJobKey, request);
            return form;
        }

        // TODO: integrate BomUtil.findBusinessEntityAlternates / BomUtil.saveOrUpdate
        //       when the legacy jar is available. For now just clear the events.
        log.info("setBusinessAlias stub: assignToKey={}, clearAll={}, events={}",
                assignToBusinessKey, clearAll, selectedEventKeys);

        for (Long key : selectedEventKeys) {
            Optional<LoadEvent> eventOpt = loadEventRepository.findById(key);
            if (eventOpt.isEmpty()) continue;

            LoadEvent event = eventOpt.get();
            loadEventService.clearLoadEvent(event);
            loadEventRepository.save(event);

            if (clearAll) {
                // Clear other events in the same job with the same data
                String targetData = event.getLoadEventData();
                Optional<LoadJob> jobOpt = loadJobRepository.findWithEventsByLoadJobKey(loadJobKey);
                jobOpt.ifPresent(job -> job.getLoadEvents().stream()
                        .filter(e -> !loadEventService.isLoadEventCleared(e)
                                && targetData != null
                                && targetData.equals(e.getLoadEventData()))
                        .forEach(e -> {
                            loadEventService.clearLoadEvent(e);
                            loadEventRepository.save(e);
                        }));
            }
        }

        reloadJobIntoForm(form, loadJobKey, request);
        return form;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void reloadJobIntoForm(LoadJobAdminForm form, String loadJobKey,
                                   HttpServletRequest request) {
        loadJobRepository.findWithEventsByLoadJobKey(loadJobKey).ifPresentOrElse(job -> {
            form.setSelectedLoadJob(job);
            try {
                form.setReplayAllowed(resolveReplayAllowed(request, job));
            } catch (InvalidUserContext e) {
                log.warn("Could not resolve replayAllowed: {}", e.getMessage());
            }
        }, () -> log.warn("reloadJobIntoForm: job not found for key {}", loadJobKey));
    }

    private boolean resolveReplayAllowed(HttpServletRequest request, LoadJob job)
            throws InvalidUserContext {
        ApplicationContext ac = AppContextHelper.getValidContext(request);
        boolean replayOwnJobsOnly = pcmConfigUtil.getBooleanValue(
                "pcm.upload.manage.replay.ownJobsOnly", false);
        return loadJobService.canLoadJobBeReplayedByUserId(job, ac.getCurrentUser().getUserId(), replayOwnJobsOnly);
    }
}
