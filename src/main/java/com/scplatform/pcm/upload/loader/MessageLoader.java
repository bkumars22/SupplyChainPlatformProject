/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.audit.Service.PcmAuditHistoryService;
import com.scplatform.pcm.commodityProfile.service.CommodityProfileService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.service.UserService;
import com.scplatform.pcm.util.common.InterconnectConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageLoader {

    public static final String SOURCE_B2B = "B2B";
    public static final String SOURCE_UI  = "UI";

    private static final boolean DEFAULT_VALIDATION_ENABLED        = false;
    private static final boolean DEFAULT_SOFTERRORS_ENABLED        = false;
    private static final boolean DEFAULT_CONTINUE_ON_ERROR_ENABLED = false;
    private static final boolean DEFAULT_ACCESS_CONTROL_ENABLED    = false;
    private static final String  DEFAULT_USER                      = "BATCH";
    private static final String  DEFAULT_SOURCE                    = SOURCE_B2B;

    private final PcmConfigUtil pcmConfigUtil;
    private final MessageLoaderFactory messageLoaderFactory;
    private final CommodityProfileService commodityProfileService;
    private final UserService userService;

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Loads {@code file} using the {@link BaseImporter} registered for {@code message}.
     *
     * @param message   message / upload type (e.g. {@code "CostRecord"})
     * @param file      XML file to load
     * @param loadProps key-value properties (use {@link InterconnectConstants} keys)
     * @return status carrying result code, counts, and any messages
     */
    @Transactional(rollbackFor = Throwable.class)
    public MessageLoaderStatus load(String message, File file, Map<String, String> loadProps) {
        log.info("[DEBUG] MessageLoader.load() ENTERED: message='{}', file='{}', fileExists={}",
                message, file != null ? file.getAbsolutePath() : "NULL", 
                file != null && file.exists());

        MessageLoaderStatus status            = new MessageLoaderStatus();
        MessageLoaderStatus cumulativeResults = new MessageLoaderStatus();
        final List<String>  errors            = new ArrayList<>();

        int     parallelProcThreadCount = pcmConfigUtil.getInteger(
                "pcm.sourcinglane.parallel.processing.thread.count", 4);
        boolean parallelProcIsEnabled   = pcmConfigUtil.getBooleanValue(
                "pcm.costrecord.upload.parallelprocess", false);
        boolean isParallelProcessing    = false;
        long    timeKey                 = 0L;
        String  user                    = DEFAULT_USER;

        try {
            boolean isValid = true;
            String  source  = DEFAULT_SOURCE;

            if (loadProps != null) {
                if (loadProps.containsKey(InterconnectConstants.SCPLATFORM_USERID)) {
                    user = loadProps.get(InterconnectConstants.SCPLATFORM_USERID);
                }
                if (loadProps.containsKey(InterconnectConstants.SCPLATFORM_ORIGIN)) {
                    source = loadProps.get(InterconnectConstants.SCPLATFORM_ORIGIN);
                }
                log.info("MessageLoader invoked with properties {}", loadProps);
            }

            log.info("Starting load as user={} source={}", user, source);

            BaseImporter         loader = messageLoaderFactory.getMessageLoader(message);
            FileInputStream      fis    = null;
            XMLStreamReader      xmlr   = null;

            long time = System.currentTimeMillis();
            timeKey = time;

            // RequestMonitorUtil.addRequest(new RequestMonitor(
            //         message + "-" + timeKey, loadProps.get(InterconnectConstants.SCPLATFORM_ACTION), user));

            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,  Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE,              Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_COALESCING,                   Boolean.TRUE);

            log.info("Processing file {}", file.getName());

            boolean isBusinessValidationEnabled = isSettingEnabled(loadProps,
                    InterconnectConstants.SCPLATFORM_VALIDATION_BASE, source, message,
                    DEFAULT_VALIDATION_ENABLED);
            log.info("Business Validation: {}", isBusinessValidationEnabled);

            boolean isSoftErrorsEnabled = isSettingEnabled(loadProps,
                    InterconnectConstants.SCPLATFORM_SOFTERRORS_BASE, source, message,
                    DEFAULT_SOFTERRORS_ENABLED);
            log.info("Soft Errors: {}", isSoftErrorsEnabled);

            boolean isContinueOnErrorsEnabled = isSettingEnabled(loadProps,
                    InterconnectConstants.SCPLATFORM_CONTINUE_ON_ERROR_BASE, source, message,
                    DEFAULT_CONTINUE_ON_ERROR_ENABLED);
            log.info("Continue On Errors: {}", isContinueOnErrorsEnabled);

            boolean isWriteAuditRecordEnabled = isSettingEnabled(loadProps,
                    InterconnectConstants.SCPLATFORM_WRITE_AUDIT_RECORD_BASE, source, message,
                    BaseImporter.DEFAULT_WRITE_AUDIT_RECORD_ENABLED);
            log.info("Write Audit Record: {}", isWriteAuditRecordEnabled);

            boolean isAccessControlEnabled = isSettingEnabled(loadProps,
                    InterconnectConstants.SCPLATFORM_ACCESS_CONTROL_ENABLED, source, message,
                    DEFAULT_ACCESS_CONTROL_ENABLED);
            log.info("Access Control Enabled: {}", isAccessControlEnabled);

            // ------------------------------------------------------------------
            // Optional business validation pass
            // ------------------------------------------------------------------
            if (isBusinessValidationEnabled) {
                ValidateMessage validateMessage = null;
                String customer   = pcmConfigUtil.getString("pcm.customer", "PCM");
                String className  = "customer." + customer + ".com.scplatform.pcm.upload.loader.ValidateMessage";
                try {
                    validateMessage = (ValidateMessage) Class.forName(className)
                            .getConstructor().newInstance();
                } catch (Exception e) {
                    log.debug("No ValidateMessage override found for customer {}", customer);
                }
                if (validateMessage == null) {
                    validateMessage = SpringContextHolder.getBean(ValidateMessage.class);
                }
                validateMessage.recordLoadEvents(true);
                validateMessage.setInputFileName(file.getCanonicalPath());
                validateMessage.setMessageType(message);
                validateMessage.setActiveUserId(user);
                validateMessage.setSoftErrorsEnabled(isSoftErrorsEnabled);
                validateMessage.setContinueOnErrorEnabled(isContinueOnErrorsEnabled);
                validateMessage.setWriteAuditRecordEnabled(isWriteAuditRecordEnabled);
                validateMessage.setProps(loadProps, message);
                validateMessage.setLoadSource(source);
                try {
                    fis  = new FileInputStream(file);
                    xmlr = xmlif.createXMLStreamReader(fis);
                    isValid = validateMessage.validateBusinessEntity(xmlr, status);
                } finally {
                    closeQuietly(xmlr);
                    IOUtils.closeQuietly(fis);
                }
                validateMessage.updateStats(status);
                validateMessage.cleanUp();
                log.info("Validation result: {}", status.getResultMessage());
            }

            // ------------------------------------------------------------------
            // Main load
            // ------------------------------------------------------------------
            if (isValid) {
                loader.setUseItemRevision(false);
                loader.recordLoadEvents(true);
                loader.setInputFileName(file.getCanonicalPath());
                loader.setLoadSource(source);
                loader.setMessageType(message);
                loader.setActiveUserId(user);
                loader.setProps(loadProps, message);
                loader.setBusinessValidationEnabled(isBusinessValidationEnabled);
                loader.setSoftErrorsEnabled(isSoftErrorsEnabled);
                loader.setContinueOnErrorEnabled(isContinueOnErrorsEnabled);
                loader.setWriteAuditRecordEnabled(isWriteAuditRecordEnabled);
                loader.setAccessControlEnabled(isAccessControlEnabled);

                // List<String> restrictList = pcmConfigUtil.getList("pcm.commodityProfile.ui.restrict.upload.list");
                // if (restrictList != null
                //         && restrictList.contains(loadProps.get(InterconnectConstants.E2NA_ALIAS))
                //         && SOURCE_UI.equals(loadProps.get(InterconnectConstants.SCPLATFORM_ORIGIN))) {
                //     Users currentUser = userService.findByUserId(loadProps.get(InterconnectConstants.SCPLATFORM_USERID));
                //     FileInputStream cpFis = new FileInputStream(file);
                //     try {
                //         XMLStreamReader cpXmlr = xmlif.createXMLStreamReader(cpFis);
                //         commodityProfileService.checkExcludedItemExist(
                //                 cpXmlr, currentUser, loadProps.get(InterconnectConstants.E2NA_ALIAS), loader);
                //     } finally {
                //         IOUtils.closeQuietly(cpFis);
                //     }
                // }

                try {
                    for (int passNumber = 1; passNumber <= loader.numberPasses(); passNumber++) {
                        if (parallelProcIsEnabled && "SourcingLane".equals(message)) {
                            log.info("Processing Cost Record Upload in Parallel");
                            isParallelProcessing = true;
                            final String finalSource = source;
                            final String finalUser   = user;
                            loader.setUploadFile(file);
                            fis  = new FileInputStream(file);
                            xmlr = xmlif.createXMLStreamReader(fis);
                            Map<String, String>       slMessagesMap = loader.preProcessXml(xmlr);
                            final Map<String, List<String>> slLineNumbers = loader.getLineNumberDetails();
                            try {
                                List<Callable<Object>> callables = new ArrayList<>();
                                for (String slMessage : slMessagesMap.keySet()) {
                                    String          xmlStr    = slMessagesMap.get(slMessage);
                                    XMLStreamReader xmlReader = xmlif.createXMLStreamReader(new StringReader(xmlStr));
                                    callables.add(() -> {
                                        try {
                                            BaseImporter newLoader = messageLoaderFactory.getMessageLoader(message);
                                            newLoader.setUseItemRevision(false);
                                            newLoader.recordLoadEvents(true);
                                            newLoader.setInputFileName(file.getCanonicalPath());
                                            newLoader.setLoadSource(finalSource);
                                            newLoader.setMessageType(message);
                                            newLoader.setActiveUserId(finalUser);
                                            newLoader.setProps(loadProps, message);
                                            newLoader.setBusinessValidationEnabled(isBusinessValidationEnabled);
                                            newLoader.setSoftErrorsEnabled(isSoftErrorsEnabled);
                                            newLoader.setContinueOnErrorEnabled(isContinueOnErrorsEnabled);
                                            newLoader.setWriteAuditRecordEnabled(isWriteAuditRecordEnabled);
                                            newLoader.setAccessControlEnabled(isAccessControlEnabled);
                                            newLoader.setLineNumberDetails(slLineNumbers);
                                            newLoader.setUploadFile(null);
                                            newLoader.setUploadXML(xmlStr);
                                            newLoader.process(xmlReader, 1);
                                            cumulativeResults.setCount(newLoader.getCount());
                                            newLoader.updateCumulativeStats(cumulativeResults);
                                            if (newLoader.hasSoftErrors()) {
                                                errors.addAll(newLoader.getSoftErrors());
                                            }
                                        } catch (Exception e) {
                                            log.error("Exception during parallel processing", e);
                                            errors.add(e.getMessage());
                                        }
                                        return null;
                                    });
                                }
                                ExecutorService executor = Executors.newFixedThreadPool(parallelProcThreadCount);
                                executor.invokeAll(callables);
                                executor.shutdown();
                            } catch (Exception ex) {
                                log.error("Error during SourcingLane parallel processing", ex);
                                throw new MessageLoaderException(ex);
                            }
                        } else {
                            loader.setUploadFile(file);
                            fis  = new FileInputStream(file);
                            xmlr = xmlif.createXMLStreamReader(fis);
                            loader.process(xmlr, passNumber);
                            status.setCount(loader.getCount());
                        }
                    }
                } finally {
                    closeQuietly(xmlr);
                    IOUtils.closeQuietly(fis);

                    if (isParallelProcessing) {
                        copyUpdatedStats(status, cumulativeResults);
                    } else {
                        loader.updateStats(status);
                    }

                    if (!errors.isEmpty()) {
                        errors.forEach(loader::addSoftError);
                    }

                    StringBuilder messages = new StringBuilder();
                    if (loader.hasInfoMessages()) {
                        status.setStatistic("Information Messages", loader.getInfoMessages().size());
                        messages.append(StringUtils.join(loader.getInfoMessages(), "\n"));
                    }
                    if (loader.hasSoftErrors()) {
                        if (loader.overriddenResultCode != null) {
                            status.setResultCode(loader.overriddenResultCode);
                        } else {
                            status.setResultCode(MessageLoaderStatus.WARN);
                        }
                        status.setStatistic("Warnings", loader.getSoftErrors().size());
                        messages.append(StringUtils.join(loader.getSoftErrors(), "\n"));
                    }
                    if (loader.hasErrors()) {
                        status.setResultCode(isParallelProcessing
                                ? MessageLoaderStatus.WARN
                                : MessageLoaderStatus.ERROR);
                        status.setStatistic("Errors", loader.getErrors().size());
                        if (messages.length() > 0) messages.append("\n");
                        messages.append(StringUtils.join(loader.getErrors(), "\n"));
                    }
                    status.setResultMessage(messages.toString());
                    loader.cleanUp();
                }

                log.info("Total Processing Time: {} sec", (System.currentTimeMillis() - time) / 1000);
                log.info("Total load count: {}", loader.getCount());
            }

        } catch (Throwable t) {
            log.info("Load problem caught", t);
            status.setResultStatus(t);
            // Spring @Transactional(rollbackFor = Throwable.class) handles rollback automatically
        }
        // Spring manages EntityManager lifecycle — no HibernateUtil.closeEM() needed
        // TODO: wire RequestMonitorUtil.removeFromMap when available
        // RequestMonitorUtil.removeFromMap(message + "-" + timeKey);

        log.debug("MessageLoader returning status={}", status.getResultCode());

        // Audit record
        String loadJobType = null;
        String fileName    = file.getName();
        if (loadProps != null) {
            if (loadProps.containsKey(InterconnectConstants.E2NA_ALIAS)) {
                loadJobType = loadProps.get(InterconnectConstants.E2NA_ALIAS);
            }
            if (loadProps.containsKey("filename")) {
                fileName = loadProps.get("filename");
            }
        }
        if (loadJobType == null) {
            loadJobType = message;
        }

        String auditResult = (status.getResultCode() == MessageLoaderStatus.ERROR
                || status.getResultCode() == MessageLoaderStatus.UNKNOWN) ? "Failure" : "Success";
        PcmAuditHistoryService.writeAuditRecord(user, "UPLOAD", loadJobType, null,
                "Result:" + auditResult + " File:" + fileName);

        return status;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Determines if a loader setting is enabled, checking (in order):
     * loadProps override → base.source.message → base.source → base (config).
     */
    private boolean isSettingEnabled(Map<String, String> loadProps,
                                     String base, String source, String message,
                                     boolean defaultSetting) {
        boolean isEnabled = defaultSetting;
        List<String> tryParams = Arrays.asList(
                base + "." + source + "." + message,
                base + "." + source,
                base);
        for (String param : tryParams) {
            String value = (loadProps != null) ? loadProps.get(param) : null;
            if (value != null) {
                isEnabled = Boolean.parseBoolean(value);
                break;
            }
            String configValue = pcmConfigUtil.getString(param);
            if (configValue != null) {
                isEnabled = Boolean.parseBoolean(configValue);
                break;
            }
        }
        return isEnabled;
    }

    private static void copyUpdatedStats(MessageLoaderStatus status, MessageLoaderStatus cumulative) {
        status.setStatistic("SourcingLaneCount",    cumulative.getStatistic("SourcingLaneCount"));
        status.setStatistic("CostRecordCount",      cumulative.getStatistic("CostRecordCount"));
        status.setStatistic("CostRecordValueCount", cumulative.getStatistic("CostRecordValueCount"));
        status.setStatistic("CostValueDetailCount", cumulative.getStatistic("CostValueDetailCount"));
        status.setStatistic("LoadEvents",           cumulative.getStatistic("LoadEvents"));
    }

    public static void closeQuietly(XMLStreamReader xmlr) {
        try {
            if (xmlr != null) xmlr.close();
        } catch (XMLStreamException e) {
            // ignore
        }
    }
}
