/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.tam.service.TAMAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Log4j2
@RequiredArgsConstructor
public class TAMController {


    private final TAMAllocationService tamAllocationService;

    /**
     * REST API to check if TAM allocation data exists for a functional group and site combination.
     *
     * @param functionalGroupId the functional group ID to check
     * @param siteKey the site Key to check
     * @return true if TAM allocation data exists with allocation > 0, false otherwise
     */
    @GetMapping("/mcm/api/checkTAMExistByFunctionalGroupAndSite")
    @ResponseBody
    public boolean checkTAMAllocationExist(@RequestParam("functionalGroupId") Long functionalGroupId, @RequestParam("siteKey") Long siteKey) {
        log.debug("Checking TAM allocation existence for functional group ID: {} and SiteKey : {}", functionalGroupId, siteKey);
        try {
            boolean exists = tamAllocationService.checkIfTAMExistsForFunctionalGroupAndSite(functionalGroupId, siteKey);
            log.debug("TAM allocation exist check result for FG ID {} and site key {} : {}", functionalGroupId, siteKey, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking TAM allocation existence for functional group ID: {} and Site Key : {}", functionalGroupId,siteKey, e);
            throw new RuntimeException("Failed to check TAM allocation existence", e);
        }
    }
}
