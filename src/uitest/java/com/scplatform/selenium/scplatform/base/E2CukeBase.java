/*
 * E2CukeBase.java
 * Created on May 26, 2017
 *
 * Copyright (c) 2017 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.base;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.test.selenium.api.e2na.directoryAdapter.DirectoryAdapter;
import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.BrowserManager;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.common.browser.BrowserProxy;
import com.test.selenium.common.browser.BrowserType;
import com.test.selenium.common.browser.ChromeBrowser;
import com.test.selenium.common.browser.EdgeBrowser;
import com.test.selenium.common.cucumber.CukeBaseTest;
import com.test.selenium.common.messageResources.system;
import com.test.selenium.common.navigation.unity.Menu;
import com.test.selenium.common.reporting.PublishResults;
import com.test.selenium.scplatform.steps.data.Partners;

public class E2CukeBase extends CukeBaseTest {

  public static Utilities utils;
  private String unique;

  // Static block to configure WebDriver properties
  // NETWORK ISSUE: Cannot reach external driver download URLs (Azure CDN, GitHub)
  // SOLUTION: Either use Chrome (WebDriverManager works), configure proxy, or
  // manually set driver path
  static {
    System.out.println("[E2CukeBase] ========================================");
    System.out.println("[E2CukeBase] Configuring WebDriver environment");
    System.out.println("[E2CukeBase] ========================================");

    // CRITICAL FIX: Prevent WebDriverManager from hanging on CI agents without internet.
    // WDM's default connect/read timeout was not respected on some agent configurations,
    // causing "Creating New Runtime File" → 8+ hour hang on Bamboo (e.g. PTWTS1-2328).
    // These must be set BEFORE super.beforeSuite() calls WebDriverManager.setup().
    System.setProperty("wdm.timeout", "60");            // WDM HTTP timeout in seconds
    System.setProperty("wdm.forceDownload", "false");   // Use cached driver if present
    System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); // JVM connect timeout (ms)
    System.setProperty("sun.net.client.defaultReadTimeout",    "60000"); // JVM read timeout (ms)
    System.out.println("[E2CukeBase] ✓ WDM timeout set to 60s (prevents CI hang on agents without internet)");

    try {
      // Set properties for silent operation
      System.setProperty("webdriver.edge.silentOutput", "true");
      System.setProperty("webdriver.edge.verboseLogging", "false");
      System.setProperty("webdriver.chrome.silentOutput", "true");

      // Check for manually configured driver path (workaround for network issues)
      String manualEdgeDriver = System.getenv("EDGE_DRIVER_PATH");
      if (manualEdgeDriver != null && new File(manualEdgeDriver).exists()) {
        System.setProperty("webdriver.edge.driver", manualEdgeDriver);
        System.out.println("[E2CukeBase] ✓ Using manually configured EdgeDriver: " + manualEdgeDriver);
      } else {
        System.out.println("[E2CukeBase] ⚠ EDGE_DRIVER_PATH not set - will attempt auto-download");
        System.out.println(
            "[E2CukeBase] ⚠ If network issues occur, manually download msedgedriver.exe and set EDGE_DRIVER_PATH");
      }

      System.out.println("[E2CukeBase] ========================================");
    } catch (Exception e) {
      System.err.println("[E2CukeBase] ✗ Driver setup error: " + e.getMessage());
      e.printStackTrace();
    }

    // CRITICAL FIX: Set Menu filter locator EARLY to support both 'Filter
    // workflows' and 'Filter items'
    // This must be in static block to ensure it's set before ANY test execution or
    // Menu.filter() calls
    try {
      Menu.setMenuFilterLocator(By.xpath("//input[@placeholder='Filter workflows' or @placeholder='Filter items']"));
      // Enable double-click menu opening - helps ensure menu is fully opened and
      // filter input is visible
      Menu.doubleClickToOpenMenu(true);
      System.out.println("[E2CukeBase] ✓ Menu filter locator configured for both workflows and items");
      System.out.println("[E2CukeBase] ✓ Menu double-click opening enabled");
    } catch (Exception e) {
      System.err.println("[E2CukeBase] ✗ Menu locator setup error: " + e.getMessage());
    }
  }

  @Override
  @BeforeSuite(alwaysRun = true)
  public void beforeSuite() {
    super.beforeSuite();
    PublishResults.setNoStackInfoStatic(true);

    utils = new Utilities();
    utils.setup();

    // Clean up any lingering Chrome processes that might interfere
    // FIX: Use waitFor(10, SECONDS) so taskkill never blocks indefinitely on CI.
    try {
      Process p1 = Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
      p1.waitFor(10, TimeUnit.SECONDS);
      Process p2 = Runtime.getRuntime().exec("taskkill /F /IM chrome.exe /T");
      p2.waitFor(10, TimeUnit.SECONDS);
      Thread.sleep(1000);
      JLog.write("✓ Cleaned up lingering Chrome processes");
    } catch (Exception e) {
      // Ignore cleanup errors - not critical
    }

    // Only delete the Selenium cache when no valid ChromeDriver binary is present.
    // Deleting it unconditionally forces WebDriverManager to re-download ChromeDriver
    // mid-run, which causes the active ChromeDriver process to crash (ClosedChannelException)
    // on the first 2-3 scenarios while the download completes.
    try {
      String userHome = System.getProperty("user.home");
      java.io.File defaultCache = new java.io.File(userHome + "/.cache/selenium");
      boolean hasValidDriver = false;
      if (defaultCache.exists()) {
        // Walk cache subdirs looking for any chromedriver.exe
        java.io.File[] subdirs = defaultCache.listFiles(java.io.File::isDirectory);
        if (subdirs != null) {
          for (java.io.File sub : subdirs) {
            String[] drivers = sub.list((d, n) -> n.toLowerCase().contains("chromedriver"));
            if (drivers != null && drivers.length > 0) { hasValidDriver = true; break; }
            // Walk one level deeper (chromedriver/win64/<version>/chromedriver.exe)
            java.io.File[] deep1 = sub.listFiles(java.io.File::isDirectory);
            if (deep1 != null) {
              outer:
              for (java.io.File d1 : deep1) {
                java.io.File[] deep2 = d1.listFiles(java.io.File::isDirectory);
                if (deep2 != null) {
                  for (java.io.File d2 : deep2) {
                    String[] bins = d2.list((dir, n) -> n.toLowerCase().startsWith("chromedriver"));
                    if (bins != null && bins.length > 0) { hasValidDriver = true; break outer; }
                  }
                }
              }
            }
          }
        }
      }
      if (!hasValidDriver && defaultCache.exists()) {
        JLog.write("Selenium cache has no valid ChromeDriver — deleting stale cache: " + defaultCache.getAbsolutePath());
        deleteDirectoryRecursive(defaultCache);
        JLog.write(defaultCache.exists() ? "✗ WARNING: Cache still exists after deletion" : "✓ Stale cache removed");
      } else if (hasValidDriver) {
        JLog.write("✓ Selenium cache contains valid ChromeDriver — keeping cache to avoid re-download crash");
      } else {
        JLog.write("✓ Selenium cache doesn't exist (good)");
      }
      // CRITICAL: Framework's ChromeBrowser.driver() calls FileUtils.forceDelete on
      // ~/.cache/selenium and throws FileNotFoundException if it doesn't exist.
      // Pre-create the directory so forceDelete succeeds (deletes empty dir) and
      // every scenario can launch its browser cleanly.
      if (!defaultCache.exists()) {
        if (defaultCache.mkdirs()) {
          JLog.write("✓ Pre-created empty .cache/selenium to satisfy framework forceDelete: " + defaultCache.getAbsolutePath());
        } else {
          JLog.write("✗ Failed to pre-create " + defaultCache.getAbsolutePath());
        }
      }
    } catch (Exception e) {
      JLog.write("✗ Cache cleanup error: " + e.getMessage());
    }

    JLog.write("Start to initial data for WTG");

    unique = null;
    unique = DateTime.now().toString("yyMMddHHmmss");

    Configuration.setRuntime("model", "SCPlatform");
    Configuration.setRuntime("wtg_p_id", "54");
    Configuration.setRuntime("component_name", "SCPlatform");
    Configuration.setRuntime("execution_type", "UI");
    Configuration.setRuntime("BambooBuildNumber", System.getProperty("buildNumber"));
    Configuration.setRuntime("xmlSuite", System.getProperty("suite"));
    Configuration.setRuntime("test_run_id", unique);
    Configuration.setRuntime("release_version", "26.2");
  }

  /**
   * Setup for utils, prop, parnterships, etc.<br>
   * Does not start a browser.
   */
  @Override
  public void start_noBrowser() {
    super.start_noBrowser();

    utils = new Utilities();
    utils.setup();

    Menu.setMenuFilterLocator(By.xpath("//input[@placeholder='Filter workflows' or @placeholder='Filter items']"));
    Menu.setMenuClosedLocator(By.xpath(
        "//nav[@class='eto-header__menu' or @class='eto-header__menu filtering']/button[contains(text(),'Menu')]"));
    Menu.setOpenMenuLocator(By.xpath("//nav[contains(@class, 'eto-header__menu open')]/button[text()='Menu']"));
    // Menu.setMenuClosedLocator(menuClosedLocator);
    // this is defined in E2NA, Connection=Dir Hub Profile, Property=Inbox
    // Directory
    DirectoryAdapter.setInboxDir("/scplatform/var/shared/ssp/test/inbox/");

  }

  protected void setSystem() {

    String systemKey = "stack. scpm";
    if (StringUtils.isNotBlank(configKeyForStack)) {
      systemKey = configKeyForStack;
    }
    system.setHostKey(systemKey);

  }

  protected void registerPartners() {
    Partner enterprise = Partners.Enterprise();
    enterprise.setDescription(Configuration.getProperty("hub.company.id"));
    enterprise.setUdf4(Configuration.getProperty("hub.company.ref.id")); // BusinessEntityExternalId
    enterprise.setUdf3(null); // Alt name
    Partners.setOverridePartner(enterprise.getUniqueName(), enterprise);

    Partners.registerPartners();
  }

  /**
   * Helper method to recursively delete a directory and all its contents.
   * This is needed to clean up the Selenium cache directory that the E2open
   * framework tries to delete.
   * Uses Windows-specific commands for more reliable deletion.
   */
  private void deleteDirectoryRecursive(java.io.File dir) {
    if (!dir.exists()) {
      return;
    }

    try {
      String osName = System.getProperty("os.name").toLowerCase();

      if (osName.contains("win")) {
        // Use Windows commands for more reliable deletion
        // Remove read-only attributes first
        Process attrib = Runtime.getRuntime().exec(
            new String[] { "cmd", "/c", "attrib", "-R", "-H", "-S", dir.getAbsolutePath() + "\\*.*", "/S", "/D" });
        attrib.waitFor();

        // Force delete the directory
        Process rd = Runtime.getRuntime().exec(
            new String[] { "cmd", "/c", "rd", "/S", "/Q", dir.getAbsolutePath() });
        rd.waitFor();

        // Verify deletion
        if (dir.exists()) {
          // Fallback to Java deletion if Windows command failed
          deleteDirectoryJava(dir);
        }
      } else {
        // Unix/Linux - use Java deletion
        deleteDirectoryJava(dir);
      }
    } catch (Exception e) {
      // Fallback to Java deletion if command execution fails
      deleteDirectoryJava(dir);
    }
  }

  /**
   * Java-based recursive directory deletion (fallback method)
   */
  private void deleteDirectoryJava(java.io.File dir) {
    if (dir.exists()) {
      if (dir.isDirectory()) {
        java.io.File[] files = dir.listFiles();
        if (files != null) {
          for (java.io.File file : files) {
            if (file.isDirectory()) {
              deleteDirectoryJava(file);
            } else {
              file.setWritable(true);
              file.setReadable(true);
              file.delete();
            }
          }
        }
      }
      dir.setWritable(true);
      dir.setReadable(true);
      dir.delete();
    }
  }

  @AfterSuite
  public void afterSuite() {
    BrowserProxy.stopProxy();
    if (AbstractPage.isBrowser(BrowserType.CHROME)) {
      ChromeBrowser.stopChromeService(); // safe to keep or remove
    } else if (AbstractPage.isBrowser(BrowserType.EDGE)) {
      EdgeBrowser.stopEdgeService();
    }
    BrowserManager.INSTANCE.closeAll();
    PublishResults.setNoStackInfoStatic(true);
    try {
      generateReport();
    } catch (Exception e) {
      JLog.warning("afterSuite: generateReport/publishToWTG failed (non-fatal): " + e.getMessage());
    }
  }

}
