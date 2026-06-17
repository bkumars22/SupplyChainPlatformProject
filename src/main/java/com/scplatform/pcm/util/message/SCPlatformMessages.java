/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.message;

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.scplatform.pcm.config.util.PcmConfigUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Message source utility for accessing product and customer-specific message files.
 * 
 * Supports message resolution with customer-specific overrides:
 * - If customer is not "MCM" (default), checks for customer-specific files:
 *   - sc-messages-{customer}.properties
 *   - sc-audit-messages-{customer}.properties
 * - Falls back to product-default files:
 *   - sc-messages.properties
 *   - sc-audit-messages.properties
 * 
 * Customer name is loaded from PCM configuration key: pcm.customer
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SCPlatformMessages {
	public static SCPlatformMessages INSTANCE;

	private final PcmConfigUtil configUtil;

	private final ResourceBundleMessageSource msgSource = new ResourceBundleMessageSource();
	private final ResourceBundleMessageSource auditMsgSource = new ResourceBundleMessageSource();

	private volatile String customerName = "PCM";
	private static final String DEFAULT_CUSTOMER = "PCM";
	private static final String CUSTOMER_CONFIG_KEY = "pcm.customer";

	/**
	 * Initialize message sources after Spring has injected dependencies
	 */
	@PostConstruct
	public void initialize() {
		INSTANCE = this;
		log.info("Initializing SCPlatformMessages with customer-specific file support...");
		initializeMessageSources();
	}

	/**
	 * Initialize message sources with customer-specific basenames
	 * Customer-specific files take precedence over product defaults
	 */
	private void initializeMessageSources() {
		// Get customer name from configuration (defaults to MCM)
		customerName = configUtil.getString(CUSTOMER_CONFIG_KEY, DEFAULT_CUSTOMER);
		log.info("Loading messages for customer: {}", customerName);

		// Build basenames: customer-specific files first, then product defaults
		String msgBasenames = buildBasenames("sc-messages", customerName);
		String auditBasenames = buildBasenames("sc-audit-messages", customerName);

		msgSource.setBasenames(msgBasenames.split(","));
		auditMsgSource.setBasenames(auditBasenames.split(","));

		log.debug("Message source basenames: {}", msgBasenames);
		log.debug("Audit message source basenames: {}", auditBasenames);
	}

	/**
	 * Build comma-separated basenames list with customer-specific files first
	 * 
	 * @param baseBasename the base basename (e.g., "sc-messages")
	 * @param customer the customer name
	 * @return comma-separated basenames with customer files first, product default last
	 */
	private String buildBasenames(String baseBasename, String customer) {
		if (customer == null || customer.trim().isEmpty() || DEFAULT_CUSTOMER.equals(customer)) {
			// No customer-specific files for default customer
			return baseBasename;
		}
		// Customer-specific file first (for overrides), then product default (fallback)
		return baseBasename + "-" + customer + "," + baseBasename;
	}

	/**
	 * Refresh basenames if customer configuration has changed
	 * This can be called if customer configuration is updated at runtime
	 */
	public void refreshCustomerConfiguration() {
		log.info("Refreshing customer configuration for messages...");
		initializeMessageSources();
	}

	/**
	 * Get the current customer name
	 * 
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * Get message value from product and customer-specific sources
	 * 
	 * @param messageKey the message key
	 * @param args message arguments for placeholder replacement
	 * @param locale the locale for message resolution
	 * @return the resolved message, or "???key???" if not found in any source
	 */
	public String getMessage(String messageKey, Object[] args, Locale locale) {
		try {
			return msgSource.getMessage(messageKey, args, locale);
		} catch (NoSuchMessageException e) {
			return "???" + messageKey + "???";
		}
	}

	/**
	 * Get audit message value from product and customer-specific sources
	 * 
	 * @param messageKey the audit message key
	 * @param args message arguments for placeholder replacement
	 * @param locale the locale for message resolution
	 * @return the resolved audit message, or "???key???" if not found in any source
	 */
	public String getAuditMessage(String messageKey, Object[] args, Locale locale) {
		try {
			return auditMsgSource.getMessage(messageKey, args, locale);
		} catch (NoSuchMessageException e) {
			return "???" + messageKey + "???";
		}
	}

	/**
	 * Get a locale-specific message source for the given locale
	 * 
	 * @param locale the locale for message resolution
	 * @return LocaleSpecificMessageSource wrapper for the locale
	 */
	public LocaleSpecificMessageSource getLocaleSpecificMessageSource(Locale locale) {
		return new LocaleSpecificMessageSource(locale);
	}

	/**
	 * Inner class for locale-specific message access
	 */
	public class LocaleSpecificMessageSource {
		private final Locale locale;

		public LocaleSpecificMessageSource(Locale locale) {
			this.locale = locale;
		}

		public String getMessage(String messageKey, Object... args) {
			return SCPlatformMessages.this.getMessage(messageKey, args, locale);
		}
	}
}