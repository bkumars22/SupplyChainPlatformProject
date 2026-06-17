/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.upload.dto.UploadFileResponse;
import com.scplatform.pcm.upload.service.UploadFileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * REST controller for file upload pages.
 * Migrated from Struts UploadFileAction.
 */
@Log4j2
@Controller
@RequiredArgsConstructor
public class UploadFileController {

    private final UploadFileService uploadFileService;

    // -----------------------------------------------------------------------
    // GET /uploadFile  — renders the upload form
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
    public String initUploadPage(HttpServletRequest request,
                                 @RequestParam(required = false) String uploadMenuType,
                                 Model model) {
        UploadFileResponse response = new UploadFileResponse();
        try {
            response = uploadFileService.initUploadPage(request, uploadMenuType);
        } catch (InvalidUserContext e) {
            log.warn("Invalid user context on uploadFile: {}", e.getMessage());
            response.setError(true);
        }
        model.addAttribute("uploadForm", response);
        return "uploadFile";
    }

    // -----------------------------------------------------------------------
    // POST /submitUploadFile  — handles multipart file upload
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/submitUploadFile", method = RequestMethod.POST)
    public String processUpload(HttpServletRequest request,
                                @RequestParam(required = false) String uploadMenuType,
                                @RequestParam(value = "uploadFile", required = false) List<MultipartFile> files,
                                @RequestParam(value = "messageType", required = false) List<String> messageTypes,
                                Model model) {
        log.info("[DEBUG] processUpload called: uploadMenuType='{}', files={}, messageTypes={}",
                uploadMenuType,
                files != null ? files.size() : "NULL",
                messageTypes);
        // Also log all request parameters for debugging
        request.getParameterMap().forEach((key, values) -> 
            log.info("[DEBUG] Request param: '{}' = {}", key, java.util.Arrays.toString(values)));
        UploadFileResponse response = new UploadFileResponse();
        try {
            response = uploadFileService.processUpload(request, uploadMenuType, files, messageTypes);
        } catch (InvalidUserContext e) {
            log.warn("Invalid user context on submitUploadFile.do: {}", e.getMessage());
            response.setError(true);
        } catch (Exception e) {
            log.error("Unexpected error on submitUploadFile.do: {}", e.getMessage(), e);
            response.setError(true);
        }
        model.addAttribute("uploadForm", response);
        return "uploadFile";
    }

    // -----------------------------------------------------------------------
    // POST /downloadTemplate  — streams a template file
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/downloadTemplate", method = RequestMethod.POST)
    public ResponseEntity<Resource> downloadTemplate(
            @RequestParam(value = "templateName") String templateName) {
        return uploadFileService.downloadTemplate(templateName);
    }
}
