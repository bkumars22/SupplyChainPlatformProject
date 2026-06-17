/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.upload.dto.LoadMessage;
import com.scplatform.pcm.upload.dto.UploadFileResponse;
import com.scplatform.pcm.upload.enums.UploadMessageType;
import com.scplatform.pcm.upload.entity.LoadEvent;
import com.scplatform.pcm.upload.entity.LoadJob;
import com.scplatform.pcm.upload.loader.ExcelToXMLProcessorFactory;
import com.scplatform.pcm.upload.loader.IExcelToXml;
import com.scplatform.pcm.upload.loader.MessageLoader;
import com.scplatform.pcm.upload.loader.MessageLoaderStatus;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.api.service.ItemManagementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service handling file upload business logic migrated from UploadFileAction.
 *
 * <p>E2NA integration is deferred — only the direct (without-E2NA) loader path is active.
 * MessageLoader is invoked asynchronously after Excel-to-XML preprocessing.</p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class UploadFileService {

    private static final String UPLOAD_PROPS_RESOURCE = "/config/upload.properties";

    private final AppContextService appContextService;
    private final PcmConfigUtil pcmConfigUtil;
    private final LoadJobRepository loadJobRepository;
    private final LoadEventRepository loadEventRepository;
    private final ExcelToXMLProcessorFactory excelToXMLProcessorFactory;
    private final MessageLoader messageLoader;
    private final LoadJobUpdateService loadJobUpdateService;
    private final ItemManagementService itemManagementService;

    // Cached upload-type menu properties
    private volatile Properties uploadProperties;

    // -----------------------------------------------------------------------
    // Init page
    // -----------------------------------------------------------------------

    /**
     * Builds the upload page DTO.
     * Mirrors UploadFileAction.init().
     */
    public UploadFileResponse initUploadPage(HttpServletRequest request, String uploadMenuType)
            throws InvalidUserContext {

        ApplicationContext ac = AppContextHelper.getValidContext(request);
        UploadFileResponse response = new UploadFileResponse();
        response.setUploadMenuType(uploadMenuType);

        // ACL check for the upload page itself
        if (!appContextService.hasAccess(ac, "UPDOWN", "UploadFile")) {
            log.warn("User {} denied UploadFile access", ac.getCurrentUser().getUserId());
            response.setError(true);
            return response;
        }

        // Load upload.properties
        Properties props = getUploadProperties();

        // Populate available types for the requested menu group
        if (uploadMenuType != null && !uploadMenuType.isBlank()) {
            List<String> allTypes = splitCsv(props.getProperty("upload." + uploadMenuType, ""));
            if (!allTypes.isEmpty()) {
                response.addAvailableMessageTypes(uploadMenuType, allTypes);
            }
        } else {
            // No specific menu — expose every group
            for (String key : props.stringPropertyNames()) {
                if (!key.startsWith("upload.")) {
                    continue;
                }
                String group = key.substring("upload.".length());
                List<String> allTypes = splitCsv(props.getProperty(key, ""));
                if (!allTypes.isEmpty()) {
                    response.addAvailableMessageTypes(group, allTypes);
                }
            }
        }

        // xlsx-specific type list for UI labelling
        List<String> xlsxTypes = pcmConfigUtil.getList("pcm.upload.user.message.xlsx");
        if (xlsxTypes != null) {
            response.setXlxsType(xlsxTypes);
        }

        // Max files per upload — default to 1 if not configured in DB
        response.setMaxFiles(pcmConfigUtil.getInteger("pcm.upload.maxfiles", 1));

        return response;
    }

    // -----------------------------------------------------------------------
    // Process upload
    // -----------------------------------------------------------------------

    /**
     * Handles a multipart file upload.
     * Mirrors UploadFileAction.upload().
     *
     * @param request  the HTTP request (for session/user context)
     * @param files    uploaded files (usually one, governed by pcm.upload.maxfiles)
     * @param ftypes   upload type strings parallel to the files array
     * @return populated response carrying success links or error details
     */
    public UploadFileResponse processUpload(HttpServletRequest request,
                                            String uploadMenuType,
                                            List<MultipartFile> files,
                                            List<String> ftypes) throws InvalidUserContext {

        ApplicationContext ac = AppContextHelper.getValidContext(request);
        UploadFileResponse response = new UploadFileResponse();

        // ACL guard
        if (!appContextService.hasAccess(ac, "UPDOWN", "UploadFile")) {
            log.warn("User {} denied upload submission", ac.getCurrentUser().getUserId());
            response.setError(true);
            return response;
        }

        // Repopulate form fields so the page re-renders with the dropdown intact
        response.setUploadMenuType(uploadMenuType);
        Properties props = getUploadProperties();
        if (uploadMenuType != null && !uploadMenuType.isBlank()) {
            List<String> allTypes = splitCsv(props.getProperty("upload." + uploadMenuType, ""));
            if (!allTypes.isEmpty()) {
                response.addAvailableMessageTypes(uploadMenuType, allTypes);
            }
        } else {
            for (String key : props.stringPropertyNames()) {
                if (!key.startsWith("upload.")) continue;
                String group = key.substring("upload.".length());
                List<String> allTypes = splitCsv(props.getProperty(key, ""));
                if (!allTypes.isEmpty()) {
                    response.addAvailableMessageTypes(group, allTypes);
                }
            }
        }
        List<String> xlsxTypes = pcmConfigUtil.getList("pcm.upload.user.message.xlsx");
        if (xlsxTypes != null) {
            response.setXlxsType(xlsxTypes);
        }
        response.setMaxFiles(pcmConfigUtil.getInteger("pcm.upload.maxfiles", 1));

        File uploadDirFile;
        try {
            uploadDirFile = getWorkDirectory();
        } catch (Exception e) {
            log.error("Cannot resolve upload directories: {}", e.getMessage(), e);
            response.setError(true);
            return response;
        }
        Path uploadDirPath = uploadDirFile.toPath();

        List<LoadMessage> errors = new ArrayList<>();

        log.info("[DEBUG] processUpload: files.size={}, ftypes={}", 
                files != null ? files.size() : 0, ftypes);

        int max = (files != null) ? files.size() : 0;
        int ftypeIdx = 0;
        for (int i = 0; i < max; i++) {
            MultipartFile file = files.get(i);

            if (file == null || file.isEmpty()) {
                log.info("[DEBUG] Skipping empty file[{}]", i);
                continue;
            }

            String ftype = (ftypes != null && ftypes.size() > ftypeIdx) ? ftypes.get(ftypeIdx++) : "";
            log.info("[DEBUG] Processing file[{}]: name='{}', ftype='{}'", i,
                    file.getOriginalFilename(), ftype);

            // Per-type ACL check
            if (!ftype.isBlank() && !appContextService.hasAccess(ac, "UPLOAD_TYPE", ftype)) {
                log.warn("User {} denied access to upload type {}", ac.getCurrentUser().getUserId(), ftype);
                errors.add(new LoadMessage(file.getOriginalFilename(), "Access denied for type: " + ftype, "ERROR"));
                continue;
            }

            // Persist file to upload directory
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                log.warn("Uploaded file has no filename — skipping");
                errors.add(new LoadMessage("unknown", "Uploaded file has no filename", "ERROR"));
                continue;
            }
            String sanitizedName = Paths.get(originalFilename).getFileName().toString();
            Path dest = uploadDirPath.resolve(sanitizedName);
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                log.error("Failed to save uploaded file {}: {}", sanitizedName, e.getMessage(), e);
                errors.add(new LoadMessage(sanitizedName, "Failed to save file: " + e.getMessage(), "ERROR"));
                continue;
            }

            // Create and persist LoadJob
            String loadJobKey = UUID.randomUUID().toString().replace("-", "");
            String externalId = UUID.randomUUID().toString().replace("-", "");
            log.info("[DEBUG] Creating LoadJob: loadJobKey={}, externalId={}, ftype='{}', file='{}'",
                    loadJobKey, externalId, ftype, sanitizedName);

            LoadJob job = new LoadJob();
            job.setLoadJobKey(loadJobKey);
            job.setDatasource(sanitizedName);
            job.setLoadedBy(ac.getCurrentUser().getUserId());
            // Resolve display label for upload type (e.g. "Functional Group Item")
            String uploadTypeLabel = resolveUploadTypeLabel(ftype);
            log.info("[DEBUG] uploadTypeLabel resolved to: '{}'", uploadTypeLabel);
            job.setLoadJobType(uploadTypeLabel);
            job.setState("SUBMITTED");
            job.setStatus("PENDING");
            job.setExternalId(externalId);
            job.setLoadDate(LocalDateTime.now());
            LoadJob savedJob;
            try {
                // save() returns the managed instance — use it for all subsequent references
                savedJob = loadJobRepository.saveAndFlush(job);
            } catch (Exception e) {
                log.error("Failed to persist LoadJob for type {}: {}", ftype, e.getMessage(), e);
                errors.add(new LoadMessage(sanitizedName, "Failed to create load job: " + e.getMessage(), "ERROR"));
                continue;
            }

            // Record initial SUBMIT event
            LoadEvent submitEvent = new LoadEvent();
            submitEvent.setLoadJob(savedJob);
            submitEvent.setType("SUBMIT");
            submitEvent.setLoadEventData(sanitizedName);
            submitEvent.setLoadEventContext(ftype);
            submitEvent.setInsertDate(LocalDateTime.now());
            try {
                loadEventRepository.save(submitEvent);
            } catch (Exception e) {
                log.warn("Failed to persist initial LoadEvent for job {}: {}", loadJobKey, e.getMessage(), e);
                // Non-fatal: job was created; continue processing
            }

            Map<String, String> loadProps = buildLoadProps(ac, ftype, loadJobKey);
            boolean submittedOk = true;
            boolean useE2na = isE2naRequired(ftype);

            if (useE2na) {
                // TODO: wire E2NA submission when e2na-client jar is on the classpath.
                //       Calls E2NAClientUtil.submitToE2NA(dest.toFile(), ftype, user, fAsync, loadProps)
                //       then updates job.state/status from ClientResult.
                log.info("E2NA path for type {} — integration pending", ftype);
            } else {
                File xmlInFile = null;
                try {
                    xmlInFile = File.createTempFile(
                            ftype + "_" + System.currentTimeMillis(), ".xml", uploadDirFile);
                    Set<String> preprocessErrors = new HashSet<>();
                    log.info("[DEBUG] Getting preprocessor for ftype='{}'", ftype);
                    IExcelToXml preprocessor = excelToXMLProcessorFactory.getProcessor(ftype);
                    log.info("[DEBUG] Preprocessor returned: {}", preprocessor != null ? preprocessor.getClass().getName() : "NULL");
                    if (preprocessor != null) {
                        log.info("[DEBUG] Calling preprocessor.processReadExcel() for type '{}'", ftype);
                        preprocessor.processReadExcel(
                                loadProps, ftype, dest.toFile(), xmlInFile, preprocessErrors, null);
                        log.info("[DEBUG] preprocessor.processReadExcel() done. errors={}", preprocessErrors);
                    } else {
                        log.info("[DEBUG] No Excel preprocessor for type {} — passing raw file to MessageLoader", ftype);
                        xmlInFile = dest.toFile();
                    }
                    if (!preprocessErrors.isEmpty()) {
                        savedJob.setState("COMPLETED");
                        savedJob.setStatus("FAILED");
                        loadJobRepository.save(savedJob);
                        preprocessErrors.forEach(err ->
                                errors.add(new LoadMessage(sanitizedName, err, "VALIDATION ERROR")));
                        try { FileUtils.moveFileToDirectory(xmlInFile, getResultDirectory(false), true); } catch (IOException ioe) { log.warn("Could not move to error dir: {}", ioe.getMessage()); }
                        dest.toFile().delete();
                        submittedOk = false;
                    } else {
                        final File finalXmlInFile = xmlInFile;
                        final String finalExternalId = externalId;
                        final Map<String, String> finalLoadProps = loadProps;
                        final Path finalDest = dest;
                        // Map upload UI type (e.g. "FunctionalGroupItemUploadUI") to loader type ("FunctionalGroup")
                        UploadMessageType msgType = UploadMessageType.lookupMessageType(ftype);
                        final String finalLoaderType = (msgType != null) ? msgType.toString() : ftype;
                        log.info("[DEBUG] Submitting async loader: type='{}', xmlFile='{}', externalId='{}'",
                                finalLoaderType, finalXmlInFile.getAbsolutePath(), finalExternalId);
                        log.info("[DEBUG] loadProps keys: {}", finalLoadProps.keySet());
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.submit(() -> {
                            log.info("[DEBUG] Async thread STARTED for type='{}', externalId='{}'",
                                    finalLoaderType, finalExternalId);
                            try {
                                log.info("[DEBUG] Calling messageLoader.load('{}', '{}')",
                                        finalLoaderType, finalXmlInFile.getName());
                                MessageLoaderStatus loaderStatus =
                                        messageLoader.load(finalLoaderType, finalXmlInFile, finalLoadProps);
                                log.info("[DEBUG] messageLoader.load() returned: resultCode={}, resultMessage='{}'",
                                        loaderStatus != null ? loaderStatus.getResultCode() : "NULL",
                                        loaderStatus != null ? loaderStatus.getResultMessage() : "NULL");
                                log.info("[DEBUG] Calling loadJobUpdateService.updateLoadJobFromMessageLoader('{}')",
                                        finalExternalId);
                                loadJobUpdateService.updateLoadJobFromMessageLoader(
                                        finalExternalId, loaderStatus);
                                log.info("[DEBUG] loadJobUpdateService.updateLoadJobFromMessageLoader() completed successfully");
                                boolean success = loaderStatus != null && loaderStatus.getResultCode() == MessageLoaderStatus.SUCCESS;
                                try { FileUtils.moveFileToDirectory(finalXmlInFile, getResultDirectory(success), true); } catch (IOException ioe) { log.warn("Could not move xml to {} dir: {}", success ? "success" : "error", ioe.getMessage()); }
                                finalDest.toFile().delete();
                            } catch (Exception ex) {
                                log.error("[DEBUG] Async thread EXCEPTION for type '{}': {}",
                                        finalLoaderType, ex.getMessage(), ex);
                                loadJobUpdateService.markLoadJobError(
                                        finalExternalId, ex.getMessage());
                                try { FileUtils.moveFileToDirectory(finalXmlInFile, getResultDirectory(false), true); } catch (IOException ioe) { log.warn("Could not move to error dir: {}", ioe.getMessage()); }
                                finalDest.toFile().delete();
                            }
                            log.info("[DEBUG] Async thread FINISHED for externalId='{}'", finalExternalId);
                        });
                        executor.shutdown();
                    }
                } catch (Exception e) {
                    log.error("Upload processing failed for type {}: {}", ftype, e.getMessage(), e);
                    savedJob.setState("COMPLETED");
                    savedJob.setStatus("FAILED");
                    loadJobRepository.save(savedJob);
                    errors.add(new LoadMessage(sanitizedName, e.getMessage(), "ERROR"));
                    try { FileUtils.moveFileToDirectory(xmlInFile, getResultDirectory(false), true); } catch (IOException ioe) { log.warn("Could not move to error dir: {}", ioe.getMessage()); }
                    dest.toFile().delete();
                    submittedOk = false;
                }
            }

            if (submittedOk) {
                response.addSuccessLink(sanitizedName, externalId, loadJobKey);
            }
        }

        if (!errors.isEmpty()) {
            response.setErrorDetails(errors);
        }

        return response;
    }

    // -----------------------------------------------------------------------
    // Download template
    // -----------------------------------------------------------------------

    /**
     * Streams an upload template file from the configured download directory.
     * Mirrors UploadFileAction.downloadTemplate().
     */
    public ResponseEntity<Resource> downloadTemplate(String templateName) {
        if (templateName == null || templateName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Resolve only the file name to prevent path traversal
        String safeFileName = Paths.get(templateName).getFileName().toString();
        String templateDir = pcmConfigUtil.getString("pcm.upload.directory", "/tmp/scplatform/upload/");
        Path filePath = Paths.get(templateDir).resolve(safeFileName);

        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            log.warn("Template file not found or unreadable: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (MalformedURLException e) {
            log.error("Failed to create resource for template: {}", filePath, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Returns true when the upload type must go through E2NA.
     * Types listed under {@code pcm.excel-upload.withoutE2NA} bypass E2NA and go direct to MessageLoader.
     */
    private boolean isE2naRequired(String ftype) {
        List<String> withoutE2na = pcmConfigUtil.getList("pcm.excel-upload.withoutE2NA");
        return withoutE2na == null || !withoutE2na.contains(ftype);
    }

    /**
     * Returns true if the upload type is for XML-based item or AVL uploads.
     */
    private boolean isXmlItemUploadType(String ftype) {
        return "ItemXMLUploadUI".equalsIgnoreCase(ftype) || "ItemAVLXMLUploadUI".equalsIgnoreCase(ftype);
    }

    protected File getWorkDirectory() {
        String workDir = pcmConfigUtil.getString("pcm.upload.directory",
                "/scplatform/app/var/scplatform/uploads/");
        File dirFile = new File(workDir);
        if (!dirFile.exists()) {
            log.info("Directory '" + dirFile.getAbsolutePath() + "' does not exist, created");
            dirFile.mkdirs();
        }
        return dirFile;
    }

    protected File getResultDirectory(boolean success) {
        String workDir = pcmConfigUtil.getString("pcm.upload.directory",
                "/scplatform/app/var/scplatform/uploads/");
        String resultDir = FilenameUtils.concat(workDir, success ? "success" : "error");
        File dirFile = new File(resultDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirFile;
    }


    private Properties getUploadProperties() {
        if (uploadProperties == null) {
            synchronized (this) {
                if (uploadProperties == null) {
                    Properties props = new Properties();
                    try (InputStream is = getClass().getResourceAsStream(UPLOAD_PROPS_RESOURCE)) {
                        if (is != null) {
                            props.load(is);
                        } else {
                            log.warn("upload.properties not found at {}", UPLOAD_PROPS_RESOURCE);
                        }
                    } catch (IOException e) {
                        log.error("Failed to load upload.properties", e);
                    }
                    uploadProperties = props;
                }
            }
        }
        return uploadProperties;
    }

    private static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Builds the loadProps map passed to MessageLoader.
     * Key names match the literal string values in InterconnectConstants.
     * Note: "scplatform.orgin" and "scplatform.readony.datasources" preserve intentional legacy typos.
     */
    private Map<String, String> buildLoadProps(ApplicationContext ac, String ftype, String loadJobKey) {
        String userId = ac.getCurrentUser().getUserId();
        String roleId = (ac.getCurrentRole() != null) ? ac.getCurrentRole().getRoleId() : "";
        Map<String, String> loadProps = new HashMap<>();
        // InterconnectConstants.SCPLATFORM_DEFAULT_DATASOURCE
        loadProps.put("scplatform.datasource", pcmConfigUtil.getString("scplatform.datasource", "MCM"));
        // InterconnectConstants.SCPLATFORM_ORIGIN  ("orgin" is the legacy typo in the constant)
        loadProps.put("scplatform.orgin", "UI");
        // InterconnectConstants.SCPLATFORM_USERID
        loadProps.put("scplatform.userId", userId);
        // InterconnectConstants.SCPLATFORM_ROLEID
        loadProps.put("scplatform.roleId", roleId);
        // InterconnectConstants.SCPLATFORM_EXISTING_LOAD_JOB — tells E2NA/loader about the pre-created job
        loadProps.put("scplatform.existingLoadJob", loadJobKey);
        // InterconnectConstants.SCPLATFORM_READONLY_DATASOURCES  ("readony" is the legacy typo)
        List<String> readOnlyDS = pcmConfigUtil.getList("pcm.upload.readonly.datasources");
        if (readOnlyDS != null && !readOnlyDS.isEmpty()) {
            loadProps.put("scplatform.readony.datasources", String.join(",", readOnlyDS));
        }

        // FunctionalGroup-specific permission keys used by preprocessor and loader
        if (ftype != null && ftype.contains("FunctionalGroup")) {
            loadProps.put("FGItemAdd",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGItemAdd")));
            loadProps.put("FGItemDelete",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGItemDelete")));
            loadProps.put("FGUpdateItem",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGUpdateItem")));
            loadProps.put("FGRenameItem",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGRenameItem")));
            loadProps.put("FGCFGActivate",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGCFGActivate")));
            loadProps.put("FGCFGInactivate",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGCFGInactivate")));
            loadProps.put("FGActivate",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGActivate")));
            loadProps.put("FGDeactivate",
                    String.valueOf(appContextService.hasAccess(ac, "UPLOAD_TYPE", "FGDeactivate")));
        }

        return loadProps;
    }

    /**
     * Resolves the display label for an upload type key.
     * Looks up {@code upload.<ftype>} in sc-messages.properties.
     * Falls back to the raw key if not found.
     *
     * @param ftype the upload type key (e.g. "FunctionalGroupItemUploadUI")
     * @return display label (e.g. "Functional Group Item")
     */
    private String resolveUploadTypeLabel(String ftype) {
        log.info("[DEBUG] resolveUploadTypeLabel called with ftype='{}'", ftype);
        log.info("[DEBUG] SCPlatformMessages.INSTANCE is {}", SCPlatformMessages.INSTANCE != null ? "NOT NULL" : "NULL");
        try {
            if (SCPlatformMessages.INSTANCE != null) {
                String key = "upload." + ftype;
                String label = SCPlatformMessages.INSTANCE.getMessage(key, null, null);
                log.info("[DEBUG] SCPlatformMessages.getMessage('{}') returned: '{}'", key, label);
                // SCPlatformMessages returns "???key???" when key not found — detect and skip
                if (label != null && !label.isBlank() && !label.startsWith("???")) {
                    log.info("[DEBUG] Resolved upload type label: '{}'", label);
                    return label;
                }
                log.warn("[DEBUG] Message key '{}' not resolved (got '{}'), falling back to raw ftype", key, label);
            }
        } catch (Exception e) {
            log.warn("[DEBUG] Exception resolving upload label for '{}': {}", ftype, e.getMessage());
        }
        log.info("[DEBUG] resolveUploadTypeLabel returning raw ftype='{}'", ftype);
        return ftype;
    }
}
