/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.scheduler.service;

import com.scplatform.pcm.scheduler.entity.JobConfig;
import com.scplatform.pcm.scheduler.repository.JobConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobLoaderServiceTest {

    @Mock private JobConfigRepository repository;
    @Mock private Scheduler scheduler;
    @InjectMocks private JobLoaderService service;

    public static class NoOpTestJob implements Job {
        @Override public void execute(JobExecutionContext ctx) throws JobExecutionException {}
    }

    private static final String JOB_CLASS = NoOpTestJob.class.getName();

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    private JobConfig cronJob() {
        JobConfig j = new JobConfig();
        j.setJobName("nightly");
        j.setJobGroup("etl");
        j.setJobClass(JOB_CLASS);
        j.setJobType("CRON");
        j.setCron("0 0 * * * ?");
        j.setEnabled(true);
        return j;
    }

    private JobConfig simpleJob() {
        JobConfig j = new JobConfig();
        j.setJobName("poller");
        j.setJobGroup("monitor");
        j.setJobClass(JOB_CLASS);
        j.setJobType("SIMPLE");
        j.setRepeatInterval(60);
        j.setEnabled(true);
        return j;
    }

    @Test
    void loadJobs_emptyList_doesNothing() throws Exception {
        when(repository.findByEnabledTrue()).thenReturn(Collections.emptyList());
        service.loadJobs(scheduler);
        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void loadJobs_cronJob_schedulesWhenNotExisting() throws Exception {
        when(repository.findByEnabledTrue()).thenReturn(Collections.singletonList(cronJob()));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        service.loadJobs(scheduler);

        ArgumentCaptor<JobDetail> jdCap = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<Trigger> trgCap = ArgumentCaptor.forClass(Trigger.class);
        verify(scheduler).scheduleJob(jdCap.capture(), trgCap.capture());
        assertEquals("nightly", jdCap.getValue().getKey().getName());
        assertEquals("etl", jdCap.getValue().getKey().getGroup());
        assertNotNull(trgCap.getValue());
    }

    @Test
    void loadJobs_simpleJob_schedulesWithSimpleTrigger() throws Exception {
        when(repository.findByEnabledTrue()).thenReturn(Collections.singletonList(simpleJob()));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        service.loadJobs(scheduler);
        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void loadJobs_existingJob_isNotRescheduled() throws Exception {
        when(repository.findByEnabledTrue()).thenReturn(Collections.singletonList(cronJob()));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        service.loadJobs(scheduler);
        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void loadJobs_invalidJobClass_throws() throws Exception {
        JobConfig j = cronJob();
        j.setJobClass("does.not.Exist");
        when(repository.findByEnabledTrue()).thenReturn(Collections.singletonList(j));
        assertThrows(ClassNotFoundException.class, () -> service.loadJobs(scheduler));
    }

    @Test
    void loadJobs_multipleJobs_allScheduled() throws Exception {
        when(repository.findByEnabledTrue()).thenReturn(Arrays.asList(cronJob(), simpleJob()));
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        service.loadJobs(scheduler);
        verify(scheduler, times(2)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }
}
