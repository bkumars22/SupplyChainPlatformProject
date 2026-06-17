/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.config;

import com.scplatform.pcm.alert.enums.AlertTypes;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.BridgeConfiguration;
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration;
import org.apache.activemq.artemis.core.config.DivertConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.server.cluster.impl.MessageLoadBalancingType;
import org.apache.activemq.artemis.core.settings.impl.AddressFullMessagePolicy;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.artemis.autoconfigure.ArtemisAutoConfiguration;
import org.springframework.boot.artemis.autoconfigure.ArtemisConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.scheduling.annotation.EnableScheduling;

import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot configuration for the embedded Apache Artemis broker.
 *
 * <h3>What this configures:</h3>
 * <ul>
 *   <li>Embedded Artemis broker with <b>file-based journal persistence</b> (NOT database)</li>
 *   <li>One <b>topic</b> (MULTICAST) per AlertType (alert.topic.CostChange, alert.topic.CostPending, etc.)</li>
 *   <li>Dead Letter Queue (DLQ) for failed messages (ANYCAST — point-to-point)</li>
 *   <li>Expiry Queue for expired messages (ANYCAST — point-to-point)</li>
 *   <li>JSON message converter (Jackson) for AlertEvent serialization</li>
 *   <li>JmsTemplate configured for <b>topic publishing</b> (pubSubDomain=true)</li>
 *   <li>JmsListenerContainerFactory for <b>durable topic subscriptions</b></li>
 * </ul>
 *
 * <h3>Storage: File-Based Journal (NOT Database)</h3>
 * <p>Artemis uses its native <b>append-only journal</b> for message persistence.
 * Messages are written to binary journal files on the local filesystem, NOT to
 * a database. This is the same concept as the legacy PersistentQueue file-based
 * storage, but with industrial-strength reliability:</p>
 * <ul>
 *   <li>Journal files: {@code ./artemis-data/journal/} — append-only, fsync'd</li>
 *   <li>Bindings: {@code ./artemis-data/bindings/} — address/queue metadata</li>
 *   <li>Large messages: {@code ./artemis-data/large-messages/} — overflow for large payloads</li>
 *   <li>Paging: {@code ./artemis-data/paging/} — spill-to-disk when memory is full</li>
 * </ul>
 *
 * <h3>Topic Architecture (MULTICAST):</h3>
 * <pre>
 *   ┌──────────── SCPlatform Application JVM ──────────────────────────┐
 *   │                                                              │
 *   │  AlertHandler ──→ JmsTemplate ──→ Embedded Artemis Broker    │
 *   │                    (pubSub=true)       │                     │
 *   │                                   Journal Files (Disk)       │
 *   │                                   /artemis-data/journal/     │
 *   │                                        │                     │
 *   │                                   MULTICAST Topic            │
 *   │                                   alert.topic.CostChange     │
 *   │                                        │                     │
 *   │  AlertConsumerService ←── @JmsListener ←┘                    │
 *   │   (durable subscription)                                     │
 *   │         │                                                    │
 *   │         └──→ AlertPublisher ──→ DB (SC_ALERT_DETAIL)      │
 *   │                                                              │
 *   └──────────────────────────────────────────────────────────────┘
 * </pre>
 */
@Configuration
@EnableJms
@EnableScheduling
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
@Import(ArtemisAutoConfiguration.class)
public class ArtemisConfig {

    private static final Logger log = LoggerFactory.getLogger(ArtemisConfig.class);

    @Value("${pcm.alert.artemis.max-delivery-attempts:5}")
    private int maxDeliveryAttempts;

    @Value("${pcm.alert.artemis.redelivery-delay:5000}")
    private long redeliveryDelay;

    @Value("${pcm.alert.artemis.max-size-bytes:104857600}")
    private long maxSizeBytes;

    @Value("${pcm.alert.artemis.page-size-bytes:10485760}")
    private long pageSizeBytes;

    // ── Journal Configuration Properties ──
    @Value("${pcm.alert.artemis.journal.type:NIO}")
    private String journalType;

    @Value("${pcm.alert.artemis.journal.sync-transactional:true}")
    private boolean journalSyncTransactional;

    @Value("${pcm.alert.artemis.journal.sync-non-transactional:false}")
    private boolean journalSyncNonTransactional;

    @Value("${pcm.alert.artemis.journal.file-size:10485760}")
    private int journalFileSize;

    @Value("${pcm.alert.artemis.journal.min-files:2}")
    private int journalMinFiles;

    @Value("${pcm.alert.artemis.journal.compact-min-files:10}")
    private int journalCompactMinFiles;

    @Value("${pcm.alert.artemis.journal.compact-percentage:30}")
    private int journalCompactPercentage;

    // ── HA (High Availability) Configuration Properties ──
    @Value("${pcm.alert.artemis.ha.enabled:false}")
    private boolean haEnabled;

    @Value("${pcm.alert.artemis.ha.backup:false}")
    private boolean haBackup;

    @Value("${pcm.alert.artemis.ha.shared-store:true}")
    private boolean haSharedStore;

    @Value("${pcm.alert.artemis.ha.failover-on-shutdown:false}")
    private boolean failoverOnShutdown;

    @Value("${pcm.alert.artemis.ha.cluster-password}")
    private String clusterPassword;

    @Value("${pcm.alert.artemis.ha.cluster-name:sc-alert-cluster}")
    private String clusterName;

    // ── Core Bridge Configuration Properties ──
    @Value("${pcm.alert.artemis.bridge.enabled:false}")
    private boolean bridgeEnabled;

    @Value("${pcm.alert.artemis.bridge.target-host:}")
    private String bridgeTargetHost;

    @Value("${pcm.alert.artemis.bridge.target-port:61616}")
    private int bridgeTargetPort;

    @Value("${pcm.alert.artemis.bridge.retry-interval:2000}")
    private long bridgeRetryInterval;

    @Value("${pcm.alert.artemis.bridge.reconnect-attempts:-1}")
    private int bridgeReconnectAttempts;

    @Value("${pcm.alert.artemis.bridge.queue-filter:alert.#}")
    private String bridgeQueueFilter;


    // ─────────────────────────────────────────────────────────────
    // Embedded Broker Customization
    // ─────────────────────────────────────────────────────────────

    /**
     * Customizes the embedded Artemis broker configuration.
     *
     * <p>Creates MULTICAST addresses (topics) for each AlertType, configures DLQ,
     * expiry queue, address settings, and file-based journal tuning.</p>
     *
     * <h3>Why Topics (MULTICAST) instead of Queues (ANYCAST)?</h3>
     * <ul>
     *   <li>Topics allow multiple subscribers to each receive a copy of every message</li>
     *   <li>In a multi-node deployment, each node's consumer independently processes alerts</li>
     *   <li>Durable subscriptions ensure messages survive broker/consumer restarts</li>
     *   <li>Matches the legacy PersistentQueue semantics where all alert processors see all events</li>
     * </ul>
     *
     * <h3>Why File-Based Journal (NOT JDBC)?</h3>
     * <ul>
     *   <li>10-100x faster than JDBC store — append-only sequential writes with fsync</li>
     *   <li>No database dependency for message persistence — reduced contention</li>
     *   <li>Same concept as legacy PersistentQueue file-based storage, but crash-safe</li>
     *   <li>Paging support: spills to disk files when memory is full instead of OOM</li>
     * </ul>
     */
    @Bean
    public ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
        return configuration -> {
            try {
                log.info("Customizing embedded Artemis broker for Alert Topic system (file-based journal)...");

                // ── File-based journal tuning ──
                configureJournal(configuration);

                // ── Address settings for alert topics ──
                AddressSettings alertAddressSettings = new AddressSettings()
                        .setDeadLetterAddress(SimpleString.of("DLQ"))
                        .setExpiryAddress(SimpleString.of("ExpiryQueue"))
                        .setMaxDeliveryAttempts(maxDeliveryAttempts)
                        .setRedeliveryDelay(redeliveryDelay)
                        .setMaxSizeBytes(maxSizeBytes)
                        .setPageSizeBytes((int) pageSizeBytes)
                        .setAddressFullMessagePolicy(AddressFullMessagePolicy.PAGE)
                        .setAutoCreateQueues(true)
                        .setAutoDeleteQueues(false);

                configuration.addAddressSetting("alert.#", alertAddressSettings);

                // ── Default address settings ──
                AddressSettings defaultSettings = new AddressSettings()
                        .setDeadLetterAddress(SimpleString.of("DLQ"))
                        .setExpiryAddress(SimpleString.of("ExpiryQueue"))
                        .setMaxDeliveryAttempts(10);

                configuration.addAddressSetting("#", defaultSettings);

                // ── Create DLQ and ExpiryQueue (ANYCAST — point-to-point) ──
                // Using QueueConfiguration API (replaces deprecated CoreAddressConfiguration)
                configuration.addQueueConfiguration(
                        QueueConfiguration.of("DLQ")
                                .setAddress(SimpleString.of("DLQ"))
                                .setRoutingType(RoutingType.ANYCAST)
                                .setDurable(true)
                                .setAutoCreateAddress(true));

                configuration.addQueueConfiguration(
                        QueueConfiguration.of("ExpiryQueue")
                                .setAddress(SimpleString.of("ExpiryQueue"))
                                .setRoutingType(RoutingType.ANYCAST)
                                .setDurable(true)
                                .setAutoCreateAddress(true));

                // ── MULTICAST topics are auto-created via spring.artemis.embedded.topics ──
                // The spring.artemis.embedded.topics property in application.properties creates
                // MULTICAST addresses on broker startup. AddressSettings (alert.#) apply to all.
                for (AlertTypes alertType : AlertTypes.values()) {
                    String topicName = alertType.getTopicName();
                    log.info("Artemis MULTICAST topic expected: {} (auto-created via embedded.topics)", topicName);
                }

                log.info("Embedded Artemis broker customization complete. {} alert topics configured " +
                                "(MULTICAST, file-based journal).",
                        AlertTypes.values().length);

                // ── HA (High Availability) Configuration ──
                if (haEnabled) {
                    configureHighAvailability(configuration);
                }

                // ── Core Bridge Configuration ──
                if (bridgeEnabled) {
                    configureBridge(configuration);
                }

            } catch (Exception e) {
                log.error("Failed to customize Artemis broker configuration", e);
                throw new RuntimeException("Artemis broker configuration failed", e);
            }
        };
    }

    // ─────────────────────────────────────────────────────────────
    // File-Based Journal Configuration
    // ─────────────────────────────────────────────────────────────

    /**
     * Configures the Artemis file-based journal for message persistence.
     *
     * <p>The journal is a high-performance append-only log stored on the local
     * filesystem. It does NOT use a database — all message data is persisted
     * in binary journal files.</p>
     *
     * <p>Journal structure on disk:</p>
     * <pre>
     *   ./artemis-data/
     *   ├── journal/          ← message data (append-only binary files)
     *   │   ├── activemq-data-1.amq
     *   │   ├── activemq-data-2.amq
     *   │   └── ...
     *   ├── bindings/         ← address and queue metadata
     *   ├── large-messages/   ← messages exceeding journal inline threshold
     *   └── paging/           ← overflow when address memory limit reached
     *       └── alert.topic.CostChange/
     * </pre>
     */
    private void configureJournal(org.apache.activemq.artemis.core.config.Configuration configuration) {
        log.info("Configuring file-based journal: type={}, syncTransactional={}, fileSize={}",
                journalType, journalSyncTransactional, journalFileSize);

        configuration.setPersistenceEnabled(true);

        switch (journalType.toUpperCase()) {
            case "ASYNCIO", "AIO" ->
                    configuration.setJournalType(org.apache.activemq.artemis.core.server.JournalType.ASYNCIO);
            case "MAPPED" ->
                    configuration.setJournalType(org.apache.activemq.artemis.core.server.JournalType.MAPPED);
            default ->
                    configuration.setJournalType(org.apache.activemq.artemis.core.server.JournalType.NIO);
        }

        configuration.setJournalFileSize(journalFileSize);
        configuration.setJournalMinFiles(journalMinFiles);
        configuration.setJournalSyncTransactional(journalSyncTransactional);
        configuration.setJournalSyncNonTransactional(journalSyncNonTransactional);
        configuration.setJournalCompactMinFiles(journalCompactMinFiles);
        configuration.setJournalCompactPercentage(journalCompactPercentage);

        log.info("File-based journal configured. Data directory: {}",
                configuration.getJournalDirectory());
    }

    // ─────────────────────────────────────────────────────────────
    // HA (High Availability) Configuration
    // ─────────────────────────────────────────────────────────────

    private void configureHighAvailability(org.apache.activemq.artemis.core.config.Configuration configuration) {
        log.info("Configuring Artemis HA: sharedStore={}, backup={}, clusterName={}",
                haSharedStore, haBackup, clusterName);

        try {
            if (haSharedStore) {
                configuration.setHAPolicyConfiguration(
                        haBackup
                                ? new org.apache.activemq.artemis.core.config.ha.SharedStoreBackupPolicyConfiguration()
                                        .setFailoverOnServerShutdown(failoverOnShutdown)
                                        .setScaleDownConfiguration(null)
                                : new org.apache.activemq.artemis.core.config.ha.SharedStorePrimaryPolicyConfiguration()
                                        .setFailoverOnServerShutdown(failoverOnShutdown)
                );
            } else {
                configuration.setHAPolicyConfiguration(
                        haBackup
                                ? new org.apache.activemq.artemis.core.config.ha.ReplicaPolicyConfiguration()
                                        .setClusterName(clusterName)
                                : new org.apache.activemq.artemis.core.config.ha.ReplicatedPolicyConfiguration()
                                        .setClusterName(clusterName)
                );
            }

            Map<String, Object> connectorParams = new HashMap<>();
            connectorParams.put("host", "localhost");
            connectorParams.put("port", "61616");

            TransportConfiguration connectorConfig = new TransportConfiguration(
                    NettyConnectorFactory.class.getName(), connectorParams, "netty-connector");
            configuration.addConnectorConfiguration("netty-connector", connectorConfig);

            ClusterConnectionConfiguration clusterConfig = new ClusterConnectionConfiguration()
                    .setName(clusterName)
                    .setAddress("alert")
                    .setConnectorName("netty-connector")
                    .setRetryInterval(500)
                    .setDuplicateDetection(true)
                    .setMessageLoadBalancingType(MessageLoadBalancingType.ON_DEMAND)
                    .setStaticConnectors(List.of("netty-connector"));

            configuration.addClusterConfiguration(clusterConfig);
            configuration.setSecurityEnabled(false);

            log.info("Artemis HA configuration complete. Mode: {}, Role: {}",
                    haSharedStore ? "shared-store" : "replication",
                    haBackup ? "BACKUP" : "LIVE");

        } catch (Exception e) {
            log.error("Failed to configure Artemis HA. Continuing without HA.", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Core Bridge Configuration (Cross-Datacenter Replication)
    // ─────────────────────────────────────────────────────────────

    private void configureBridge(org.apache.activemq.artemis.core.config.Configuration configuration) {
        if (bridgeTargetHost == null || bridgeTargetHost.isBlank()) {
            log.warn("Bridge enabled but no target host configured. Skipping bridge setup.");
            return;
        }

        log.info("Configuring Artemis core bridge to {}:{}", bridgeTargetHost, bridgeTargetPort);

        try {
            Map<String, Object> remoteParams = new HashMap<>();
            remoteParams.put("host", bridgeTargetHost);
            remoteParams.put("port", String.valueOf(bridgeTargetPort));

            TransportConfiguration remoteConnector = new TransportConfiguration(
                    NettyConnectorFactory.class.getName(), remoteParams, "remote-connector");
            configuration.addConnectorConfiguration("remote-connector", remoteConnector);

            for (AlertTypes alertType : AlertTypes.values()) {
                String localTopicName = alertType.getTopicName();
                String bridgeAddressName = "alert.bridge.address." + alertType.name();
                String bridgeQueueName = "alert.bridge." + alertType.name();

                // Create bridge queue using QueueConfiguration API
                configuration.addQueueConfiguration(
                        QueueConfiguration.of(bridgeQueueName)
                                .setAddress(SimpleString.of(bridgeAddressName))
                                .setRoutingType(RoutingType.ANYCAST)
                                .setDurable(true)
                                .setAutoCreateAddress(true));

                DivertConfiguration divert = new DivertConfiguration()
                        .setName("divert-bridge-" + alertType.name())
                        .setAddress(localTopicName)
                        .setForwardingAddress(bridgeAddressName)
                        .setExclusive(false);

                configuration.addDivertConfiguration(divert);

                BridgeConfiguration bridge = new BridgeConfiguration()
                        .setName("bridge-" + alertType.name())
                        .setQueueName(bridgeQueueName)
                        .setForwardingAddress(localTopicName)
                        .setStaticConnectors(List.of("remote-connector"))
                        .setRetryInterval(bridgeRetryInterval)
                        .setReconnectAttempts(bridgeReconnectAttempts)
                        .setUseDuplicateDetection(true)
                        .setHA(haEnabled);

                configuration.getBridgeConfigurations().add(bridge);
                log.debug("Configured bridge for topic: {} → divert → {} → bridge → {}:{}",
                        localTopicName, bridgeQueueName, bridgeTargetHost, bridgeTargetPort);
            }

            log.info("Artemis core bridge configuration complete. {} bridge(s) configured to {}:{}",
                    AlertTypes.values().length, bridgeTargetHost, bridgeTargetPort);

        } catch (Exception e) {
            log.error("Failed to configure Artemis core bridges. Continuing without bridges.", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Jackson JsonMapper for JMS JSON Serialization (Jackson 3.x)
    // ─────────────────────────────────────────────────────────────


    @Bean("alertJsonMapper")
    public JsonMapper alertJsonMapper() {
        return JsonMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // JMS Message Converter (JSON via Jackson)
    // ─────────────────────────────────────────────────────────────

    @Bean
    public MessageConverter alertMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(alertJsonMapper());
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_alertType");
        return converter;
    }

    // ─────────────────────────────────────────────────────────────
    // JmsTemplate (Producer) — Topic Mode
    // ─────────────────────────────────────────────────────────────

    /**
     * JmsTemplate configured for publishing alert messages to <b>topics</b>.
     *
     * <p>{@code setPubSubDomain(true)} tells JmsTemplate to treat destinations
     * as JMS Topics (MULTICAST) instead of Queues (ANYCAST).</p>
     */
    @Bean
    public JmsTemplate alertJmsTemplate(ConnectionFactory connectionFactory,
                                        MessageConverter alertMessageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(alertMessageConverter);
        template.setPubSubDomain(true);          // ← TOPIC mode (MULTICAST)
        template.setDeliveryPersistent(true);    // Durable — written to journal files
        template.setExplicitQosEnabled(true);
        return template;
    }

    // ─────────────────────────────────────────────────────────────
    // JMS Listener Container Factory (Consumer) — Durable Topic Subscription
    // ─────────────────────────────────────────────────────────────

    /**
     * Factory for @JmsListener containers configured for <b>durable topic subscriptions</b>.
     *
     * <p>With durable subscriptions, Artemis creates a server-side queue for each
     * subscription that persists messages in the file-based journal. Even if the
     * consumer is down, messages are held until it reconnects.</p>
     */
    @Bean
    public JmsListenerContainerFactory<?> alertListenerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter alertMessageConverter) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(alertMessageConverter);
        factory.setPubSubDomain(true);              // ← TOPIC mode (MULTICAST)
        factory.setSubscriptionDurable(true);       // ← Durable — survives restarts
        factory.setSubscriptionShared(true);        // ← Shared — allows concurrent consumers
        factory.setConcurrency("1-3");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> log.error("Error in alert JMS topic listener", t));
        return factory;
    }

    // ─────────────────────────────────────────────────────────────
    // DLQ Listener Factory (ANYCAST Queue — NOT Topic)
    // ─────────────────────────────────────────────────────────────

    /**
     * Separate listener factory for the Dead Letter Queue.
     * DLQ uses ANYCAST (point-to-point) — only one consumer should process each DLQ message.
     */
    @Bean
    public JmsListenerContainerFactory<?> dlqListenerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter alertMessageConverter) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(alertMessageConverter);
        factory.setPubSubDomain(false);             // ← QUEUE mode (ANYCAST) for DLQ
        factory.setConcurrency("1");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> log.error("Error in DLQ listener", t));
        return factory;
    }
}
