/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
@RequiredArgsConstructor
public class ExcelToXMLProcessorFactory {

    private static final String CONFIG_PREFIX = "pcm.excel.preprocessor.";

    /** Caches class name per fType to detect configuration changes. */
    private static final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();

    /** Caches loaded Class objects per fType. */
    private static final ConcurrentHashMap<String, Class<? extends IExcelToXml>> classCache =
            new ConcurrentHashMap<>();

    private final PcmConfigUtil pcmConfigUtil;

    /**
     * Returns a new {@link IExcelToXml} instance for {@code fType}, or {@code null} when
     * no preprocessor is configured for that type.
     *
     * @param fType upload type alias (e.g. {@code "CostRecordUI"})
     * @return new preprocessor instance, or {@code null}
     * @throws ProcessorInstantiationException if the class is configured but cannot be loaded or instantiated
     */
    public IExcelToXml getProcessor(String fType) throws ProcessorInstantiationException {
        log.info("Getting processor for fType: {}", fType);

        String currentClassName = pcmConfigUtil.getString(CONFIG_PREFIX + fType, null);
        log.info("Resolved preprocessor class name: {}", currentClassName);

        if (currentClassName == null || currentClassName.trim().isEmpty()) {
            log.debug("No preprocessor configured for fType: {} — returning null", fType);
            return null;
        }

        Class<? extends IExcelToXml> processorClass = getOrReloadClass(fType, currentClassName);
        return createInstance(fType, processorClass);
    }

    // -----------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------

    private static Class<? extends IExcelToXml> getOrReloadClass(String fType, String currentClassName)
            throws ProcessorInstantiationException {

        String cachedClassName = configCache.get(fType);
        if (cachedClassName != null && cachedClassName.equals(currentClassName)) {
            Class<? extends IExcelToXml> cachedClass = classCache.get(fType);
            if (cachedClass != null) {
                log.debug("Using cached class for fType: {}", fType);
                return cachedClass;
            }
        }

        log.info("Loading class for fType: {} (config changed or not cached)", fType);
        Class<? extends IExcelToXml> loadedClass = loadClass(currentClassName);
        configCache.put(fType, currentClassName);
        classCache.put(fType, loadedClass);
        return loadedClass;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends IExcelToXml> loadClass(String className)
            throws ProcessorInstantiationException {
        try {
            Class<?> loaded = Class.forName(className);
            if (!IExcelToXml.class.isAssignableFrom(loaded)) {
                throw new ProcessorInstantiationException(
                        "Class " + className + " does not implement IExcelToXml");
            }
            return (Class<? extends IExcelToXml>) loaded;
        } catch (ClassNotFoundException e) {
            throw new ProcessorInstantiationException(
                    "Preprocessor class not found: " + className, e);
        }
    }

    private static IExcelToXml createInstance(String fType, Class<? extends IExcelToXml> cls)
            throws ProcessorInstantiationException {
        try {
            IExcelToXml processor = SpringContextHolder.getBean(cls);
            log.info("Created processor instance: {}", processor.getClass().getName());
            return processor;
        } catch (Exception e) {
            throw new ProcessorInstantiationException(
                    "Failed to get Spring bean for preprocessor fType=" + fType + ", class=" + cls.getName(), e);
        }
    }

    /** Clears all caches — useful for testing or forced config reload. */
    public static void clearCache() {
        configCache.clear();
        classCache.clear();
        log.info("Processor factory caches cleared");
    }
}
