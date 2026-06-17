/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.scheduler.service;

import com.scplatform.pcm.scheduler.entity.JobConfig;
import com.scplatform.pcm.scheduler.repository.JobConfigRepository;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobLoaderService {

    private final JobConfigRepository jobConfigRepository;

    public JobLoaderService(JobConfigRepository jobConfigRepository) {
        this.jobConfigRepository = jobConfigRepository;
    }

    public void loadJobs(Scheduler scheduler) throws Exception {

        List<JobConfig> jobs = jobConfigRepository.findByEnabledTrue();

        for (JobConfig cfg : jobs) {

            Class<? extends Job> jobClass =
                    (Class<? extends Job>) Class.forName(cfg.getJobClass());

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(cfg.getJobName(), cfg.getJobGroup())
                    .storeDurably()
                    .build();

            Trigger trigger;

            if ("CRON".equals(cfg.getJobType())) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(cfg.getJobName() + "_TRG", cfg.getJobGroup())
                        .withSchedule(
                                CronScheduleBuilder.cronSchedule(cfg.getCron())
                        )
                        .forJob(jobDetail)
                        .build();
            } else {
                trigger = TriggerBuilder.newTrigger()
                        .withSchedule(
                                SimpleScheduleBuilder.simpleSchedule()
                                        .withIntervalInSeconds(cfg.getRepeatInterval())
                                        .repeatForever()
                        )
                        .build();
            }

            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
    }
}
