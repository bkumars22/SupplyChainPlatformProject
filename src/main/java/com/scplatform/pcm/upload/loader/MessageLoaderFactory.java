/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.loader.FunctionalGroupConfigLoader;
import com.scplatform.pcm.functionalGroup.loader.FunctionalGroupLoader;
import com.scplatform.pcm.parentFunctionalGroup.loader.ParentFunctionalGroupConfigLoader;
import com.scplatform.pcm.parentFunctionalGroup.loader.ParentFunctionalGroupLoader;
import com.scplatform.pcm.tam.loader.TAMAllocationDeleteLoader;
import com.scplatform.pcm.tam.loader.TAMAllocationMassUpdateCFGLoader;
import com.scplatform.pcm.tam.loader.TAMItemCFGLoader;
import com.scplatform.pcm.tam.loader.TAMSupplierCFGLoader;
import com.scplatform.pcm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageLoaderFactory {

    // SpringContextHolder.getBean() supplies a fresh prototype per call —
    // no ObjectProvider fields, no ApplicationContext injection needed.

    private final PcmConfigUtil   pcmConfigUtil;
    private final UsersRepository usersRepository;

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Returns the {@link BaseImporter} implementation for the given message type.
     *
     * <ol>
     *   <li>First tries a customer-specific extension class via reflection.</li>
     *   <li>Falls back to the built-in loader registry.</li>
     * </ol>
     *
     * @param message the upload/message type (e.g. {@code "FunctionalGroup"})
     * @return a fresh {@link BaseImporter} instance ready for use
     * @throws MessageLoaderException if no loader is registered for {@code message}
     */
    public BaseImporter getMessageLoader(String message) throws MessageLoaderException {
        BaseImporter loader = null;

        // 1. Try customer-specific extension factory (reflection intentional here)
        String customer = pcmConfigUtil.getString("pcm.customer", "dell");
        if (customer != null) {
            String className = "customer." + customer
                    + ".com.scplatform.pcm.spi.loader.MessageLoaderFactoryExt";
            try {
                Method method = Class.forName(className)
                        .getDeclaredMethod("getMessageLoader", String.class);
                loader = (BaseImporter) method.invoke(
                        Class.forName(className).getConstructor().newInstance(), message);
                loader.setUsersRepository(usersRepository);
                log.warn("Custom loader found: {}", loader);
            } catch (NoSuchMethodException | SecurityException | ClassNotFoundException
                     | IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException | InstantiationException e) {
                log.warn("Unable to get custom loader for message: {}", message, e);
            }
        }

        // 2. Built-in loader registry — SpringContextHolder supplies a fresh prototype each call
        if (loader == null) {
            loader = switch (message) {
                case "FunctionalGroup"                                                   -> SpringContextHolder.getBean(FunctionalGroupLoader.class);
                case "ParentFunctionalGroup"                                             -> SpringContextHolder.getBean(ParentFunctionalGroupLoader.class);
                case "FunctionalGroupConfig"                                             -> SpringContextHolder.getBean(FunctionalGroupConfigLoader.class);
                case "ParentFunctionalGroupConfig"                                       -> SpringContextHolder.getBean(ParentFunctionalGroupConfigLoader.class);
                case "TAMSupplierCFG", "TamSupplierAllocationCFGMRPSite"                -> SpringContextHolder.getBean(TAMSupplierCFGLoader.class);
                case "TAMItemCFG", "TAMItemAllocationCFGMRPSite"                        -> SpringContextHolder.getBean(TAMItemCFGLoader.class);
                case "TAMAllocationMassUpdateCFG", "TAMAllocationMassUpdateCFGMRPSite"  -> SpringContextHolder.getBean(TAMAllocationMassUpdateCFGLoader.class);
                case "TAMAllocationDelete"                                               -> SpringContextHolder.getBean(TAMAllocationDeleteLoader.class);
                default -> null;
            };
        }

        if (loader == null) {
            String msg = "Message '" + message + "' is not a supported message type.";
            log.error(msg);
            throw new MessageLoaderException(msg);
        }
        loader.setUsersRepository(usersRepository);
        return loader;
    }
}
