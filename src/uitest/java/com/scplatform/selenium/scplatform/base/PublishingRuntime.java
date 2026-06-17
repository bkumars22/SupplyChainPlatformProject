
// File: `src/test/java/com/scplatform/selenium/scplatform/base/PublishingRuntime.java`
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.base;

import java.util.Objects;
import java.util.UUID;

public final class PublishingRuntime {

  private PublishingRuntime() {}

  public static void initialize(String componentName,
                                String projectId,
                                String releaseVersion,
                                String executionType) {

    setIfMissing("component_name", componentName);
    setIfMissing("wtg_p_id", projectId);
    setIfMissing("release_version", releaseVersion);
    setIfMissing("execution_type", executionType);

    // Ensure a run id exists (commonly required server-side).
    setIfMissing("test_run_id", UUID.randomUUID().toString());

    // If Bamboo env var exists, mirror it.
    String bambooBuild = System.getenv("bamboo_buildNumber");
    if (bambooBuild != null && !bambooBuild.isBlank()) {
      setIfMissing("BambooBuildNumber", bambooBuild);
    }
  }

  private static void setIfMissing(String key, String value) {
    String current = System.getProperty(key);
    if (current == null || current.isBlank()) {
      System.setProperty(key, Objects.toString(value, ""));
    }
  }
}
