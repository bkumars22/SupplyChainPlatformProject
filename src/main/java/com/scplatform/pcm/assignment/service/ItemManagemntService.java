/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.assignment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.repository.SiteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemManagemntService {

    private final PcmConfigUtil pcmConfigUtil;
    private final SiteRepository siteRepository;

    public List<Site> getSiteList() {
        List<String> siteTypes = pcmConfigUtil.getList("pcm.site.type.for.region");
        return siteRepository.findSiteForRegionList(siteTypes);
    }
}