/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.io.File;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.upload.entity.LoadEvent;
import com.scplatform.pcm.upload.entity.LoadJob;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;

import lombok.extern.log4j.Log4j2;

// TODO: Uncomment when legacy modules are available on the classpath:
// import com.scplatform.repository.common.bom.BomUtil;
// import com.scplatform.repository.common.bom.BomUtil.ItemUtil;
// import com.scplatform.repository.common.bom.DuplicateEntityFound;
// import com.scplatform.repository.common.domain.bom.*;
// import com.scplatform.repository.common.domain.bom.meta.AttributeEntityType;
// import com.scplatform.repository.pcm.domain.PcmUtil;
// import com.scplatform.repository.pcm.domain.Users;
// import com.scplatform.repository.pcm.spi.alert.AlertEvent;
// import com.scplatform.repository.pcm.util.ACLUtils;


@Log4j2
public abstract class BaseImporter {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    protected static final int    DEFAULT_BATCH_SIZE                    = 50;
    protected static final String DEFAULT_VERSION                        = null;
    protected static final int    DEFAULT_TIMEOUT_SECONDS               = 1800;
    protected static final String DEFAULT_REVISION                       = "*";
    protected static final boolean DEFAULT_BUSINESS_VALIDATION_ENABLED  = false;
    protected static final boolean DEFAULT_SOFT_ERRORS_ENABLED           = false;
    protected static final boolean DEFAULT_COMMIT_ON_BATCH_ENABLED       = false;
    protected static final boolean DEFAULT_CONTINUE_ON_ERROR_ENABLED     = false;
    protected static final boolean DEFAULT_WRITE_AUDIT_RECORD_ENABLED    = false;
    protected static final String  LOAD_RULE_FULL                        = "full";
    protected static final String  CONFIG_PREFIX_COMMIT                  = "pcm.loader.commitOnBatch.";
    protected static final String  CONFIG_PREFIX_BATCH_SIZE              = "pcm.loader.loadBatchSize.";

    /** Matches legacy {@code MessageLoader.SOURCE_UI}. */
    public static final String SOURCE_UI = "UI";

    protected enum OperationCode {
        ADD, DELETE, UPDATE, UNCHANGED, UNKNOWN
    }

    // -----------------------------------------------------------------------
    // Spring beans injected via protected constructor
    // -----------------------------------------------------------------------

    /** Spring Data repositories â€” replace HibernateUtil.currentSession(). */
    protected final LoadJobRepository   loadJobRepository;
    protected final LoadEventRepository loadEventRepository;

    /** Spring Boot config utility â€” replaces static ConfigurationUtils. */
    protected final PcmConfigUtil pcmConfigUtil;

    // -----------------------------------------------------------------------
    // In-memory caches (ConcurrentHashMap replaces EhCache CacheManager)
    // -----------------------------------------------------------------------

    private final Map<String, Object> itemCache            = new ConcurrentHashMap<>();
    private final Map<String, Object> attributeGroupCache  = new ConcurrentHashMap<>();
    private final Map<String, Object> businessEntityCache  = new ConcurrentHashMap<>();
    private final Map<String, Object> platformCache        = new ConcurrentHashMap<>();
    private final Map<String, Object> siteCache            = new ConcurrentHashMap<>();
    private final Map<String, Object> itemCategoryCache    = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------
    // State fields
    // -----------------------------------------------------------------------

    protected boolean                    recordEvents;
    protected int                        loadEventCount;
    protected String                     loadJobId;
    protected String                     inputFileName;
    protected String                     messageType;
    protected String                     activeUserId;
    protected Users                      activeUserObj;
    protected UsersRepository            usersRepository;
    protected boolean                    useItemRevision;
    protected boolean                    setCurrentFlag      = false;
    protected LoadJob                    loadJob             = null;
    protected Map<String, String>        loadProps           = new HashMap<>();
    private   Set<String>                readOnlyDS          = null;
    private   Set<String>                readOnlyBE          = null;
    private   int                        transactionTimeout;
    private   int                        batchSize           = DEFAULT_BATCH_SIZE;
    protected boolean ignoreCaseForBusinessSearch            = true;
    protected boolean isBusinessValidationEnabled            = DEFAULT_BUSINESS_VALIDATION_ENABLED;
    protected boolean isSoftErrorsEnabled                    = DEFAULT_SOFT_ERRORS_ENABLED;
    protected boolean commitOnBatchEnabled                   = DEFAULT_COMMIT_ON_BATCH_ENABLED;
    protected boolean continueOnErrorEnabled                 = DEFAULT_CONTINUE_ON_ERROR_ENABLED;
    protected boolean writeAuditRecord                       = DEFAULT_WRITE_AUDIT_RECORD_ENABLED;
    protected List<String>               softErrors          = new ArrayList<>();
    protected List<String>               errors              = new ArrayList<>();
    protected List<String>               infoMessages        = new ArrayList<>();
    protected boolean                    accessControlEnabled = false;
    protected String                     loadSource          = null;
    protected ResourceBundle             messages            = ResourceBundle.getBundle("sc-messages");
    // TODO: restore when com.scplatform.repository.pcm.spi.alert.AlertEvent is available:
    // protected List<AlertEvent> alertEvents = new ArrayList<>();
    protected File                        uploadFile;
    protected String                      uploadXML;
    protected Map<String, List<String>>   lineNumberDetails   = new LinkedHashMap<>();
    protected int                         maxErrors;
    protected String[]                    datePatterns;
    protected List<String>                uploadTypeListWithoutE2NA;
    protected static List<DateTimeFormatter> dateFormatPatterns = new ArrayList<>();
    protected Integer                     overriddenResultCode = null;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Protected constructor. All Spring-managed subclasses must call
     * {@code super(loadJobRepository, loadEventRepository, pcmConfigUtil)} from their own constructor.
     *
     * @param loadJobRepository   injected by Spring
     * @param loadEventRepository injected by Spring
     * @param pcmConfigUtil       injected by Spring â€” replaces static ConfigurationUtils
     */
    protected BaseImporter(LoadJobRepository loadJobRepository,
                           LoadEventRepository loadEventRepository,
                           PcmConfigUtil pcmConfigUtil) {
        this.loadJobRepository   = loadJobRepository;
        this.loadEventRepository = loadEventRepository;
        this.pcmConfigUtil       = pcmConfigUtil;
        this.transactionTimeout = pcmConfigUtil.getIntValue(
                "pcm.loader.transaction.timeout", DEFAULT_TIMEOUT_SECONDS);
        this.maxErrors = pcmConfigUtil.getIntValue("pcm.max.error.limit", 1000);

        List<String> patterns = pcmConfigUtil.getList("pcm.common.all.possible.dateFormats");
        this.datePatterns = (patterns != null && !patterns.isEmpty())
                ? patterns.toArray(new String[0])
                : new String[]{"M/d/yy", "MM/dd/yyyy"};

        List<String> withoutE2na = pcmConfigUtil.getList("pcm.excel-upload.withoutE2NA");
        this.uploadTypeListWithoutE2NA = (withoutE2na != null) ? withoutE2na : new ArrayList<>();
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    public void cleanUp() {
        // subclasses may override
    }

    // -----------------------------------------------------------------------
    // Audit
    // -----------------------------------------------------------------------

    protected void recordAuditRecord(String operation, String simpleName,
                                     Object targetKey, String comment) {
        // TODO: integrate PcmUtil.writeAuditRecord when legacy pcm jar is available
        if (writeAuditRecord) {
            if (!operation.toUpperCase(Locale.ROOT).startsWith("UPLOAD:")) {
                operation = "UPLOAD:" + operation;
            }
            log.debug("Audit: {} {} {} {}", operation, simpleName, targetKey, comment);
        }
    }

    protected void recordAuditRecordWithSubTarget(String operation, String simpleName,
                                                   Object targetKey, String comment,
                                                   Object subTargetKey, String subTargetType) {
        // TODO: integrate PcmUtil.writeAuditRecord when legacy pcm jar is available
        if (writeAuditRecord) {
            if (!operation.toUpperCase(Locale.ROOT).startsWith("UPLOAD:")) {
                operation = "UPLOAD:" + operation;
            }
            log.debug("Audit: {} {} {} {} {} {}",
                    operation, simpleName, targetKey, comment, subTargetKey, subTargetType);
        }
    }

    // -----------------------------------------------------------------------
    // Source / flags
    // -----------------------------------------------------------------------

    public String getLoadSource() { return loadSource; }
    public void setLoadSource(String loadSource) { this.loadSource = loadSource; }

    public boolean isUIUpload() { return SOURCE_UI.equals(getLoadSource()); }

    public boolean getAccessControlEnabled() { return accessControlEnabled; }
    public void setAccessControlEnabled(boolean accessControlEnabled) {
        this.accessControlEnabled = accessControlEnabled;
    }

    public void setCurrentFlagOnUpdates(boolean setFlag) { setCurrentFlag = setFlag; }
    public void setUseItemRevision(boolean use) { useItemRevision = use; }
    public void recordLoadEvents(boolean record) { recordEvents = record; }

    public String getLoadJobId() {
        return (loadProps != null) ? loadProps.get("scplatform.existingLoadJob") : null;
    }

    public boolean checkReadOnlyDS(String dataSource) {
        return readOnlyDS != null && readOnlyDS.contains(dataSource);
    }

    // TODO: restore when BusinessEntity legacy domain is migrated:
    // public boolean checkReadOnlyBE(BusinessEntity businessEntity) {
    //     return readOnlyBE != null && readOnlyBE.contains(businessEntity.getBusinessEntityIdentifier());
    // }

    public void setInputFileName(String inputFileName) { this.inputFileName = inputFileName; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public void setUsersRepository(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        // If activeUserId was already set before the repository was injected, resolve now
        if (activeUserId != null && activeUserObj == null) {
            activeUserObj = usersRepository.findUserByUserId(activeUserId);
        }
    }

    public void setActiveUserId(String activeUserId) {
        this.activeUserId = activeUserId;
        this.activeUserObj = null;
        if (activeUserId != null && usersRepository != null) {
            activeUserObj = usersRepository.findUserByUserId(activeUserId);
        }
    }

    public String getActiveUserId() { return activeUserId; }

    protected Users getActiveUser() {
        if (activeUserObj != null) {
            return activeUserObj;
        } else if (activeUserId != null && usersRepository != null) {
            // fallback in case setUsersRepository was called after setActiveUserId
            activeUserObj = usersRepository.findUserByUserId(activeUserId);
        }
        return activeUserObj;
    }

    protected String getActiveUserRoleId() {
        Users user = getActiveUser();
        if (user != null && user.getRole() != null
                && StringUtils.isNotBlank(user.getRole().getRoleId())) {
            return user.getRole().getRoleId();
        }
        return "SYSTEM"; // Oracle treats "" as NULL; USER_ROLE column is NOT NULL
    }

    // -----------------------------------------------------------------------
    // Timeout / batch size
    // -----------------------------------------------------------------------

    public int getTransactionTimeout() { return transactionTimeout; }
    public void setTransactionTimeout(int timeoutInSeconds) {
        this.transactionTimeout = timeoutInSeconds;
    }

    public int getBatchSize() { return batchSize; }
    public void setBatchSize(int batchSize) { this.batchSize = batchSize; }

    // -----------------------------------------------------------------------
    // Props
    // -----------------------------------------------------------------------

    public void setProps(Map<String, String> loadProps, String message) {
        this.loadProps.putAll(loadProps);

        // TODO: use InterconnectConstants.* keys when available
        String values = StringUtils.trimToNull(this.loadProps.get("scplatform.readOnly.datasources"));
        if (values != null) {
            readOnlyDS = new HashSet<>(Arrays.asList(values.split(",")));
        }
        log.info("{} readOnly.datasources set to: {}", messageType, readOnlyDS);

        String bevalues = StringUtils.trimToNull(this.loadProps.get("scplatform.readOnly.businessIds"));
        if (bevalues != null) {
            readOnlyBE = new HashSet<>(Arrays.asList(bevalues.split(",")));
        }
        log.info("{} readOnly.businessIds set to: {}", messageType, readOnlyBE);

        String timeout = StringUtils.trimToNull(this.loadProps.get("scplatform.transaction.timeout"));
        if (timeout != null) {
            setTransactionTimeout(NumberUtils.toInt(timeout, transactionTimeout));
        }
        log.info("{} transaction.timeout set to: {}", messageType, getTransactionTimeout());

        String key = CONFIG_PREFIX_COMMIT + message;
        setCommitOnBatchEnabled(pcmConfigUtil.getBooleanValue(key, DEFAULT_COMMIT_ON_BATCH_ENABLED));
        log.info("{} {} set to: {}", messageType, key, isCommitOnBatchEnabled());

        key = CONFIG_PREFIX_BATCH_SIZE + message;
        setBatchSize(pcmConfigUtil.getIntValue(key, DEFAULT_BATCH_SIZE));
        log.info("{} {} set to: {}", messageType, key, getBatchSize());
    }

    public String getProperty(String propertyName) { return loadProps.get(propertyName); }

    public String defaultDataSource(String dataSource) {
        dataSource = StringUtils.trimToNull(dataSource);
        return (dataSource == null) ? getDefaultDataSource() : dataSource;
    }

    public String getDefaultDataSource() {
        // TODO: use InterconnectConstants.SCPLATFORM_DEFAULT_DATASOURCE when available
        String key = "scplatform.default.datasource";
        String dataSource = getProperty(key);
        if (dataSource == null) {
            String sysDefault = System.getProperty(key);
            if (sysDefault == null) {
                sysDefault = pcmConfigUtil.getString(key, "B2B");
            }
            dataSource = sysDefault;
            log.warn("getDefaultDataSource: no default datasource set for '{}', using '{}'",
                    key, sysDefault);
            loadProps.put(key, dataSource);
        }
        return dataSource;
    }

    // -----------------------------------------------------------------------
    // Cache helpers (ConcurrentHashMap â€” replaces EhCache get/put/clear/stats)
    // -----------------------------------------------------------------------

    protected void clearItemCache() {
        itemCache.clear();
        log.debug("Item cache cleared");
    }

    protected void clearBusinessEntityCache() {
        businessEntityCache.clear();
        log.debug("Business entity cache cleared");
    }

    protected void clearAttributeGroupCache() {
        attributeGroupCache.clear();
        log.debug("Attribute group cache cleared");
    }

    protected void clearPlatformCache() {
        platformCache.clear();
        log.debug("Platform cache cleared");
    }

    protected void clearSiteCache() {
        siteCache.clear();
        log.debug("Site cache cleared");
    }

    protected void dumpCacheStats() {
        dumpItemStats();
        dumpBusinessStats();
    }

    protected void dumpItemStats() {
        log.info("Item cache size: {}", itemCache.size());
        log.info("AttributeGroup cache size: {}", attributeGroupCache.size());
        log.info("Platform cache size: {}", platformCache.size());
    }

    protected void dumpBusinessStats() {
        log.info("Business entity cache size: {}", businessEntityCache.size());
        log.info("Site cache size: {}", siteCache.size());
    }

    private Object getObjectFromCache(Map<String, Object> cache, Object key) {
        return cache.get(String.valueOf(key));
    }

    private void putObjectInCache(Map<String, Object> cache, Object key, Object value) {
        cache.put(String.valueOf(key), value);
    }

    protected void putObjectInItemCategoryCache(Object cacheKey, Object value) {
        putObjectInCache(itemCategoryCache, cacheKey, value);
    }

    protected Object getObjectFromItemCategoryCache(Object key) {
        return getObjectFromCache(itemCategoryCache, key);
    }

    protected void putObjectInSiteCache(Object cacheKey, Object value) {
        putObjectInCache(siteCache, cacheKey, value);
    }

    protected Object getObjectFromSiteCache(Object key) {
        return getObjectFromCache(siteCache, key);
    }

    protected void putObjectInItemCache(Object cacheKey, Object value) {
        putObjectInCache(itemCache, cacheKey, value);
    }

    protected Object getObjectFromItemCache(Object key) {
        return getObjectFromCache(itemCache, key);
    }

    // -----------------------------------------------------------------------
    // Domain lookups â€” TODO: implement when legacy BomUtil is available
    // -----------------------------------------------------------------------
    //
    // The following methods depend on legacy domain types
    // (BusinessEntity, Item, Platform, Site, AvlSiteMapping, ItemCategory,
    //  AttributeGroup, AttributeEntityType, Currency, Avl, Users, ACLUtils)
    // that have not yet been migrated to Spring Boot JPA entities.
    //
    // They are preserved as commented stubs so the signatures are clear for
    // future integration. Replace entityManager.find() placeholders with the
    // full BomUtil-based logic once the legacy domain is ported.
    //
    // public Item getItem(String itemNumber, String itemUniqueId, String version,
    //                     String revision, BusinessEntity be) { ... }
    // protected Item getItem(String itemNumber, String itemUniqueId, String version,
    //                        String revision, BusinessEntity be, String itemType) { ... }
    // protected AttributeGroup getAttributeGroup(String groupName, AttributeEntityType groupType) { ... }
    // public Platform getPlatform(String platformName, String platformType, BusinessEntity be) { ... }
    // protected Long getBusinessEntityKey(String bename, String type) throws MessageLoaderException { ... }
    // public BusinessEntity getBusinessEntity(String bename, String type) throws MessageLoaderException { ... }
    // protected BusinessEntity handleMissingBusinessEntity(...) throws MessageLoaderException { ... }
    // protected Site getValidSite(String siteName) { ... }
    // protected Site getValidSite(String siteName, List<String> excludedSiteTypes) { ... }
    // protected Site getSite(String siteName, BusinessEntity be) { ... }
    // protected Site getSite(String siteName, BusinessEntity be, List<String> excludedSiteTypes) { ... }
    // public ItemCategory getItemCategory(String categoryId, BusinessEntity be) { ... }
    // public static Set<Long> getUserBusinessEntityKeys(Users user) { ... }
    // public BusinessEntity getBusinessEntity(XMLStreamReader xmlr) { ... }

    // -----------------------------------------------------------------------
    // Event recording (LoadJob + LoadEvent â€” Spring Boot JPA entities)
    // -----------------------------------------------------------------------

    /**
     * Persists a {@link LoadEvent} for the current {@link LoadJob}.
     * If no {@link LoadJob} exists yet it is created and persisted first.
     *
     * <p>Replaces the Hibernate {@code session.saveOrUpdate/save} calls with
     * standard JPA {@code entityManager.persist/merge}.</p>
     */
    protected LoadEvent recordEvent(LoadEvent.LoadEventType type, String data, String context) {
        if (loadJob == null) {
            String existingJobId = this.getLoadJobId();
            if (existingJobId != null) {
                loadJob = loadJobRepository.findById(existingJobId).orElse(null);
            }
            if (loadJob == null) {
                loadJob = new LoadJob();
                loadJob.setLoadJobKey(existingJobId);
                loadJob.setDatasource(FilenameUtils.getName(inputFileName));
                log.info("LoadJob not found, creating new job");

                // Only set loadJobType when creating a brand-new LoadJob
                String alias = StringUtils.defaultIfEmpty(getProperty("e2na.alias"), messageType);
                loadJob.setLoadJobType(alias);
            } else {
                // Existing job found â€” preserve the loadJobType already set by UploadFileService
                log.info("Found existing LoadJob '{}', loadJobType='{}'",
                        loadJob.getLoadJobKey(), loadJob.getLoadJobType());
            }
            loadJob.setLoadedBy(activeUserId);
            loadJob = loadJobRepository.save(loadJob);
        }

        LoadEvent le = new LoadEvent();
        le.setType(type);
        le.setLoadEventData(data);
        le.setLoadEventContext(context);
        le.setLoadJob(loadJob);
        le.setInsertDate(LocalDateTime.now());
        loadEventRepository.save(le);
        loadEventCount++;
        return le;
    }

    // -----------------------------------------------------------------------
    // Batch / session management
    // -----------------------------------------------------------------------

    /**
     * Flushes and optionally clears the persistence context.
     *
     * <p>In Spring Boot, commit/rollback is managed declaratively by
     * {@code @Transactional} on the calling service method. If {@code hasErrors()}
     * is true, the transaction should be rolled back by the caller (or mark
     * it rollback-only before returning).</p>
     */
    protected void handleBatchSession(XMLStreamReader xmlr, boolean doCommit)
            throws MessageLoaderException {
        // Spring Data repositories flush automatically within the transaction.
        // Explicit flush/clear is no longer needed here.
        if (doCommit) {
            if (hasErrors()) {
                log.info("Batch has errors â€” caller @Transactional will trigger rollback");
            } else {
                log.info("Batch processed");
            }
        }
    }

    protected void commitBatchSession(XMLStreamReader xmlr) throws MessageLoaderException {
        handleBatchSession(xmlr, true);
    }

    // -----------------------------------------------------------------------
    // XML utilities (javax.xml.stream â€” JDK API, unchanged)
    // -----------------------------------------------------------------------

    public String getLocation(XMLStreamReader xmlr) {
        return getLocation(xmlr.getLocation());
    }

    protected String getLocation(Location location) {
        return (location != null)
                ? "line=" + location.getLineNumber() + "," + location.getColumnNumber()
                : "?";
    }

    protected String getAllAttributes(XMLStreamReader xmlr) {
        StringBuilder buff = new StringBuilder();
        try {
            if (xmlr != null) {
                int n = xmlr.getAttributeCount();
                for (int i = 0; i < n; i++) {
                    if (i > 0) buff.append(";");
                    buff.append(xmlr.getAttributeLocalName(i))
                        .append("=")
                        .append(xmlr.getAttributeValue(i));
                }
            }
        } catch (Exception e) {
            buff.append("?");
            log.warn("getAllAttributes error", e);
        }
        return buff.toString();
    }

    public static boolean skipUntilChildElement(XMLStreamReader xmlr, String parentElementName)
            throws XMLStreamException {
        while (xmlr.hasNext()) {
            xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) continue;
            if (xmlr.isEndElement() && parentElementName.equals(xmlr.getLocalName())) {
                log.debug("Reached end of parent element '{}'. No child elements found",
                        parentElementName);
                return false;
            }
            if (xmlr.isStartElement()) return true;
        }
        throw new IllegalStateException(
                "Reached end of document â€” parentElementName may be misspelled: " + parentElementName);
    }

    public boolean skipUntilEnd(XMLStreamReader xmlr, String elementName)
            throws XMLStreamException {
        while (xmlr.hasNext()) {
            xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) continue;
            if (elementName.equals(xmlr.getLocalName()) && xmlr.isEndElement()) return true;
        }
        return false;
    }

    protected boolean skipUntilNext(XMLStreamReader xmlr, String elementName)
            throws XMLStreamException {
        if (skipUntilEnd(xmlr, elementName)) {
            while (xmlr.hasNext()) {
                if (!xmlr.isStartElement() && !xmlr.isEndElement()) continue;
                if (elementName.equals(xmlr.getLocalName()) && xmlr.isStartElement()) return true;
            }
        }
        return false;
    }

    protected boolean skipUntilNextUpdated(XMLStreamReader xmlr, String elementName)
            throws XMLStreamException {
        if (skipUntilEnd(xmlr, elementName)) {
            while (xmlr.hasNext()) {
                if (!xmlr.isStartElement() && !xmlr.isEndElement()) continue;
                if (elementName.equals(xmlr.getLocalName()) && xmlr.isEndElement()) return true;
            }
        }
        return false;
    }

    protected String generateLocationMessage(XMLStreamReader xmlr) {
        return generateLocationMessage(xmlr.getLocation());
    }

    protected String generateLocationMessage(Location location) {
        return " at location(line:col) " + location.getLineNumber() + ":"
                + location.getColumnNumber();
    }

    // -----------------------------------------------------------------------
    // Attribute / value helpers
    // -----------------------------------------------------------------------

    public String defaultVersion(XMLStreamReader xmlr, String attributeName) {
        String version = xmlr.getAttributeValue(null, attributeName);
        return StringUtils.isEmpty(version) ? DEFAULT_VERSION : version;
    }

    public String defaultVersion(String value) {
        return StringUtils.isEmpty(value) ? DEFAULT_VERSION : value;
    }

    public String defaultRevision(XMLStreamReader xmlr, String attributeName) {
        String revision = xmlr.getAttributeValue(null, attributeName);
        return StringUtils.isEmpty(revision) ? DEFAULT_REVISION : revision;
    }

    public String defaultRevision(String value) {
        return StringUtils.isEmpty(value) ? DEFAULT_REVISION : value;
    }

    protected Float defaultFloat(XMLStreamReader xmlr, String attributeName, float defaultValue) {
        String data = StringUtils.trimToNull(xmlr.getAttributeValue(null, attributeName));
        return (data != null) ? Float.valueOf(data) : defaultValue;
    }

    protected String defaultString(XMLStreamReader xmlr, String attributeName, String defaultValue) {
        String data = StringUtils.trimToNull(xmlr.getAttributeValue(null, attributeName));
        return (data == null) ? defaultValue : data;
    }

    protected String trimTo(String data, int length) {
        if (data != null && data.length() > length) {
            log.info("Value '{}' truncated to {}", data, length);
            return data.substring(0, length - 1);
        }
        return data;
    }

    protected BigDecimal getBigDecimalValue(XMLStreamReader xmlr, String attributeName)
            throws MessageLoaderException {
        String value = StringUtils.trimToNull(xmlr.getAttributeValue(null, attributeName));
        try {
            return NumberUtils.createBigDecimal(value);
        } catch (NumberFormatException nfe) {
            throw new MessageLoaderException("InvalidDecimal:{0}:{1}",
                    new Object[]{value, getLocation(xmlr)});
        }
    }

    protected Integer getIntegerValue(XMLStreamReader xmlr, String attributeName)
            throws MessageLoaderException {
        String value = StringUtils.trimToNull(xmlr.getAttributeValue(null, attributeName));
        try {
            return NumberUtils.createInteger(value);
        } catch (NumberFormatException nfe) {
            throw new MessageLoaderException("InvalidInteger:{0}:{1}",
                    new Object[]{value, getLocation(xmlr)});
        }
    }

    // -----------------------------------------------------------------------
    // Date conversion
    // -----------------------------------------------------------------------

    public static Date convertDateString(String dateString) {
        // TODO: read pcm.date.chop.time via PcmConfigUtil instance when available
        return convertDateString(dateString, true);
    }

    protected static Date convertDateString(String dateString, boolean chopTime) {
        log.debug("Parsing date string: '{}' chopTime={}", dateString, chopTime);
        Date result = null;
        if (dateString != null && dateString.length() >= 10) {
            int len = dateString.length();
            if (len == 10 || (len > 10 && chopTime)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(true);
                    result = sdf.parse(dateString);
                } catch (ParseException pe) {
                    log.error("Invalid date: {}", dateString, pe);
                }
            } else if (len > 10) {
                // TODO: integrate ISO8601.parse when utility jar is available
                log.warn("ISO8601 parsing not yet integrated for: {}", dateString);
            }
        }
        log.debug("Resulting date: {}", result);
        return result;
    }

    protected void getAllConfigDateValues() {
        List<DateTimeFormatter> list = new ArrayList<>();
        for (String p : datePatterns) {
            try {
                list.add(DateTimeFormatter.ofPattern(p.trim(), Locale.ENGLISH));
            } catch (Exception e) {
                log.error("Invalid date pattern in config: {}", p, e);
            }
        }
        dateFormatPatterns = Collections.unmodifiableList(list);
    }

    protected static Date convertDateAsString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        for (DateTimeFormatter formatter : dateFormatPatterns) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {
                log.error("Invalid date found: {}", dateString);
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Operation code
    // -----------------------------------------------------------------------

    /**
     * Maps a single-letter operation code to {@link OperationCode}.
     * A / blank = ADD, C = UPDATE, D = DELETE, U = UNCHANGED, else = UNKNOWN.
     */
    public OperationCode getOperation(String code) {
        if ("A".equalsIgnoreCase(code) || StringUtils.isBlank(code)) return OperationCode.ADD;
        else if ("C".equalsIgnoreCase(code))                           return OperationCode.UPDATE;
        else if ("D".equalsIgnoreCase(code))                           return OperationCode.DELETE;
        else if ("U".equalsIgnoreCase(code))                           return OperationCode.UNCHANGED;
        else                                                            return OperationCode.UNKNOWN;
    }

    // -----------------------------------------------------------------------
    // Error / info tracking
    // -----------------------------------------------------------------------

    public void addError(String message)        { errors.add(message); }
    public List<String> getErrors()             { return errors; }
    public boolean hasErrors()                  { return errors != null && !errors.isEmpty(); }

    public boolean isSoftErrorsEnabled()        { return isSoftErrorsEnabled; }
    public void setSoftErrorsEnabled(boolean e) { isSoftErrorsEnabled = e; }
    public void addSoftError(String message)    { softErrors.add(message); }
    public List<String> getSoftErrors()         { return softErrors; }
    public boolean hasSoftErrors()              { return softErrors != null && !softErrors.isEmpty(); }

    public void addInfoMessage(String message)  { infoMessages.add(message); }
    public List<String> getInfoMessages()       { return infoMessages; }
    public boolean hasInfoMessages()            { return infoMessages != null && !infoMessages.isEmpty(); }

    public boolean isCommitOnBatchEnabled()        { return commitOnBatchEnabled; }
    public void setCommitOnBatchEnabled(boolean e) { commitOnBatchEnabled = e; }

    public boolean isContinueOnErrorEnabled()        { return continueOnErrorEnabled; }
    public void setContinueOnErrorEnabled(boolean e) { continueOnErrorEnabled = e; }

    public boolean isWriteAuditRecordEnabled()        { return writeAuditRecord; }
    public void setWriteAuditRecordEnabled(boolean e) { writeAuditRecord = e; }

    public boolean isBusinessValidationEnabled()        { return isBusinessValidationEnabled; }
    public void setBusinessValidationEnabled(boolean e) { isBusinessValidationEnabled = e; }

    // -----------------------------------------------------------------------
    // Messages
    // -----------------------------------------------------------------------

    public String getMessage(String messageKey, Object[] args) {
        try {
            String msg = messages.getString(messageKey);
            return (msg != null) ? formatMessage(msg, args) : null;
        } catch (MissingResourceException e) {
            return "???" + messageKey + "???";
        }
    }

    public String formatMessage(String message, Object[] args) {
        MessageFormat mf = new MessageFormat(message);
        return (args != null) ? mf.format(args) : mf.format(new Object[0]);
    }

    // -----------------------------------------------------------------------
    // XML event type
    // -----------------------------------------------------------------------

    public static String getEventTypeString(int eventType) {
        switch (eventType) {
            case XMLEvent.START_ELEMENT:            return "START_ELEMENT";
            case XMLEvent.END_ELEMENT:              return "END_ELEMENT";
            case XMLEvent.PROCESSING_INSTRUCTION:   return "PROCESSING_INSTRUCTION";
            case XMLEvent.CHARACTERS:               return "CHARACTERS";
            case XMLEvent.COMMENT:                  return "COMMENT";
            case XMLEvent.START_DOCUMENT:           return "START_DOCUMENT";
            case XMLEvent.END_DOCUMENT:             return "END_DOCUMENT";
            case XMLEvent.ENTITY_REFERENCE:         return "ENTITY_REFERENCE";
            case XMLEvent.ATTRIBUTE:                return "ATTRIBUTE";
            case XMLEvent.DTD:                      return "DTD";
            case XMLEvent.CDATA:                    return "CDATA";
            default:                                return "UNKNOWN_EVENT_TYPE";
        }
    }

    // -----------------------------------------------------------------------
    // File / upload accessors
    // -----------------------------------------------------------------------

    protected File getUploadFile()                                    { return uploadFile; }
    protected void setUploadFile(File uploadFile)                     { this.uploadFile = uploadFile; }
    public String getUploadXML()                                      { return uploadXML; }
    public void setUploadXML(String uploadXML)                        { this.uploadXML = uploadXML; }
    public Map<String, List<String>> getLineNumberDetails()           { return lineNumberDetails; }
    public void setLineNumberDetails(Map<String, List<String>> d)     { this.lineNumberDetails = d; }

    // -----------------------------------------------------------------------
    // Alerts
    // -----------------------------------------------------------------------

    /**
     * Commits queued alert events.
     * TODO: restore full implementation when AlertEvent is available.
     */
    public void commitAlerts() {
        // alertEvents.forEach(AlertEvent::commit);
        // alertEvents.clear();
        log.debug("commitAlerts: AlertEvent integration pending");
    }

    // -----------------------------------------------------------------------
    // Defaults / overrides
    // -----------------------------------------------------------------------

    public static String getDefaultRevision() { return DEFAULT_REVISION; }

    protected Map<String, String> preProcessXml(XMLStreamReader xmlr) throws Exception {
        return new LinkedHashMap<>();
    }

    /** Hook for subclasses to accumulate cumulative run stats. */
    public void updateCumulativeStats(MessageLoaderStatus status) {
        // subclasses may override
    }

    // -----------------------------------------------------------------------
    // Abstract methods â€” subclasses must implement
    // -----------------------------------------------------------------------

    /**
     * Processes the XML stream for a single pass.
     * Transaction management is handled declaratively via {@code @Transactional}
     * on the concrete subclass service method.
     *
     * @param xmlr       the XML stream reader
     * @param passNumber the current pass (1-based)
     * @throws Exception on any processing or persistence error
     */
    public abstract void process(XMLStreamReader xmlr, int passNumber) throws Exception;

    /** Returns the number of processing passes required. Default is 1. */
    public int numberPasses() { return 1; }

    /** Writes final run statistics into the given status holder. */
    public abstract void updateStats(MessageLoaderStatus status);

    /** Returns the total count of processed records. */
    public abstract int getCount();
}
