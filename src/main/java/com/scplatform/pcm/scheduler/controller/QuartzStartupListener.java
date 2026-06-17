/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.scheduler.controller;

import com.scplatform.pcm.scheduler.service.JobLoaderService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuartzStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(QuartzStartupListener.class);

    private Scheduler scheduler;

    private final JobLoaderService jobLoaderService;

    //@PostConstruct
    public void contextInitialized() {
        try {
            scheduler = new StdSchedulerFactory("quartz.properties")
                    .getScheduler();
            scheduler.start();
            logger.info("Quartz Scheduler started.");
            jobLoaderService.loadJobs(scheduler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //@PreDestroy
    public void contextDestroyed() {
        try {
            scheduler.shutdown(true);
        } catch (Exception ignored) {}
    }
}