/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.web.config;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.web.view.CascadingJspViewResolver;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.net.URL;

/**
 * Web MVC configuration for JSP view resolution and static resource serving.
 *
 * <p>Registers the {@link CascadingJspViewResolver} which supports
 * customer-specific JSP overrides with product default fallback.
 * Customer is determined by {@code pcm.customer} DB config.
 *
 * <p>Also configures Spring to serve static resources (js, css, skins) from webapp directory.
 *
 * @author PCM Team
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Bean
    public ViewResolver jspViewResolver(PcmConfigUtil configUtil) {
        CascadingJspViewResolver resolver = new CascadingJspViewResolver(configUtil);
        resolver.setOrder(1);
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve /js/** from src/main/webapp/js/
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/", "file:src/main/webapp/js/");

        // Serve /css/** from src/main/webapp/css/
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/", "file:src/main/webapp/css/");

        // Serve /skins/** from src/main/webapp/skins/
        registry.addResourceHandler("/skins/**")
                .addResourceLocations("classpath:/static/skins/", "file:src/main/webapp/skins/");

        // Serve root static files (index.html, favicon.ico, etc.)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "file:src/main/webapp/");
    }

    // ...existing code...
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerCustomizer() {
        return factory -> {
            File webappDir = findWebappDir();
            if (webappDir != null && webappDir.exists()) {
                logger.info("Setting Tomcat document root to: {}", webappDir.getAbsolutePath());
                factory.setDocumentRoot(webappDir);
            } else {
                logger.warn("Could not find src/main/webapp directory for JSP support");
            }

            factory.addContextCustomizers(context -> {
                if (context.getJarScanner() instanceof StandardJarScanner jarScanner) {
                    jarScanner.setScanManifest(false);
                }
            });
        };
    }

    private File findWebappDir() {
        // Try relative path from CWD first
        File dir = new File("src/main/webapp");
        if (dir.exists()) {
            return dir;
        }

        // Try to resolve from classpath (works regardless of CWD)
        try {
            URL resource = getClass().getClassLoader().getResource("application.properties");
            if (resource != null) {
                File classesDir = new File(resource.toURI()).getParentFile();
                // classesDir is target/classes - go up to project root
                File projectRoot = classesDir.getParentFile().getParentFile();
                dir = new File(projectRoot, "src/main/webapp");
                if (dir.exists()) {
                    return dir;
                }
            }
        } catch (Exception e) {
            logger.debug("Error resolving webapp dir from classpath", e);
        }

        return null;
    }
}
