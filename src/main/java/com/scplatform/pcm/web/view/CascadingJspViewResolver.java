/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.web.view;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Cascading JSP View Resolver that supports customer-specific view overrides.
 *
 * <p>The customer is determined by the {@code pcm.customer} config key from the
 * database (via {@link PcmConfigUtil}). If the value is {@code "PCM"}, views
 * resolve from the product default directory. Any other value is treated as a
 * customer name and views are first looked up in the customer directory.
 *
 * <p>Resolution order:
 * <ol>
 *   <li>Customer directory: {@code /WEB-INF/jsp/{customerName}/{viewName}.jsp}</li>
 *   <li>Product directory (fallback): {@code /WEB-INF/jsp/{viewName}.jsp}</li>
 * </ol>
 *
 * <p>Directory layout example:
 * <pre>
 * /WEB-INF/jsp/
 *   login.jsp              &lt;-- product default (pcm.customer = PCM)
 *   main.jsp
 *   acme/                  &lt;-- customer "acme" overrides (pcm.customer = acme)
 *     login.jsp
 *   globex/                &lt;-- customer "globex" overrides (pcm.customer = globex)
 *     login.jsp
 *     main.jsp
 * </pre>
 *
 * @author PCM Team
 */
public class CascadingJspViewResolver extends InternalResourceViewResolver {

    private static final Logger logger = LoggerFactory.getLogger(CascadingJspViewResolver.class);

    private static final String DEFAULT_PREFIX = "/WEB-INF/jsp/";
    private static final String DEFAULT_SUFFIX = ".jsp";
    private static final String CONFIG_CUSTOMER = "pcm.customer";
    private static final String PRODUCT_DEFAULT = "PCM";

    private final PcmConfigUtil configUtil;

    public CascadingJspViewResolver(PcmConfigUtil configUtil) {
        this.configUtil = configUtil;
        setViewClass(JstlView.class);
        setPrefix(DEFAULT_PREFIX);
        setSuffix(DEFAULT_SUFFIX);
    }

    @Override
    protected InternalResourceView buildView(String viewName) throws Exception {
        // Strip any extension appended by content negotiation (e.g. viewName.xml)
        String resolvedName = viewName;
        int dotIndex = resolvedName.lastIndexOf('.');
        if (dotIndex > 0) {
            resolvedName = resolvedName.substring(0, dotIndex);
        }

        String customerCode = resolveCustomerCode();

        if (customerCode != null && !customerCode.isBlank()) {
            String customerPath = DEFAULT_PREFIX + customerCode + "/" + resolvedName + DEFAULT_SUFFIX;

            // Check if customer-specific JSP exists
            if (resourceExists(customerPath)) {
                logger.debug("Resolved customer view: {} for customer: {}", customerPath, customerCode);
                InternalResourceView view = (InternalResourceView) super.buildView(resolvedName);
                view.setUrl(customerPath);
                return view;
            }
            logger.debug("Customer view not found at {}, falling back to product default", customerPath);
        }

        // Fall back to product default: /WEB-INF/jsp/{viewName}.jsp
        logger.debug("Resolved product view: {}{}{}", DEFAULT_PREFIX, resolvedName, DEFAULT_SUFFIX);
        return (InternalResourceView) super.buildView(resolvedName);
    }

    /**
     * Check if a JSP resource exists at the given path.
     *
     * @param path the resource path to check
     * @return true if the resource exists
     */
    private boolean resourceExists(String path) {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest().getServletContext().getResource(path) != null;
            }
        } catch (Exception e) {
            logger.trace("Error checking resource existence for path: {}", path, e);
        }
        return false;
    }

    /**
     * Resolve customer code from the {@code pcm.customer} database config.
     *
     * <p>If the value is {@code "PCM"} (product default), returns {@code null}
     * so the product directory is used. Otherwise returns the customer name
     * which maps to a subdirectory under {@code /WEB-INF/jsp/}.
     *
     * @return customer code or null if product default
     */
    private String resolveCustomerCode() {
        try {
            String customer = configUtil.getString(CONFIG_CUSTOMER, PRODUCT_DEFAULT);
            if (customer == null || customer.isBlank() || PRODUCT_DEFAULT.equalsIgnoreCase(customer.trim())) {
                return null;
            }
            return customer.trim();
        } catch (Exception e) {
            logger.trace("Error resolving customer code from config", e);
            return null;
        }
    }
}
