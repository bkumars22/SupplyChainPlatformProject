/*
 * GlobalHooks.java
 * Created on March 9, 2026
 *
 * Copyright (c) 2026 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.hooks;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.test.selenium.common.BrowserManager;
import com.test.selenium.common.JLog;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Global Cucumber hooks for managing browser sessions and error recovery.
 * <p>
 * This class provides:
 * - Browser session validation before each scenario
 * - Automatic browser cleanup after failed scenarios
 * - Session recovery mechanisms
 * <p>
 * Execution Order:
 * 1. @Before (lowest order = first)
 * 2. Test execution
 * 3. @After (highest order = first)
 */
public class GlobalHooks {

    private static int sessionRecoveryAttempts = 0;
    private static volatile boolean lastScenarioFailed = false;

    /**
     * Ensure ~/.cache/selenium exists. The framework's ChromeBrowser.driver()
     * calls FileUtils.forceDelete on this path which throws FileNotFoundException
     * if the directory does not exist, causing every scenario to fail at browser
     * launch. Pre-creating an empty directory makes forceDelete a no-op-style
     * delete and the subsequent driver creation succeeds.
     */
    static void ensureSeleniumCacheDirExists() {
        try {
            java.io.File cache = new java.io.File(System.getProperty("user.home") + "/.cache/selenium");
            if (!cache.exists()) {
                cache.mkdirs();
            }
        } catch (Exception ignore) {
            // Best-effort; framework will surface any real I/O issue.
        }
    }

    /**
     * Runs BEFORE every other @Before hook (order=-1). Guarantees the
     * ~/.cache/selenium directory exists so the framework's forceDelete in
     * ChromeBrowser.driver() does not throw FileNotFoundException.
     */
    @Before(order = -1)
    public void ensureCacheDirBeforeScenario(Scenario scenario) {
        ensureSeleniumCacheDirExists();
    }

    /**
     * Runs BEFORE all other @Before hooks (order=0)
     * Ensures browser environment is clean before scenario starts
     * WITH RETRY LOGIC to ensure a healthy browser is ready
     */
    @Before(order = 0)
    public void validateEnvironmentBeforeScenario(Scenario scenario) {
        try {
            JLog.write("[GlobalHooks] ========================================");
            JLog.write("[GlobalHooks] Before Scenario: " + scenario.getName());
            JLog.write("[GlobalHooks] ========================================");

            // If previous scenario failed, always ensure a clean environment and
            // pre-start a fresh browser so the login step finds a ready session.
            if (lastScenarioFailed) {
                JLog.write("[GlobalHooks] ⚠ Previous scenario failed (attempt " + (sessionRecoveryAttempts + 1)
                        + ") - ensuring clean browser environment");
                cleanupBrowserEnvironment();
                sessionRecoveryAttempts++;
                
                // CRITICAL: Verify the browser was successfully started
                // If not, retry the startup (up to 3 attempts)
                Thread.sleep(1000); // Wait for browser to initialize
                boolean browserReady = false;
                int retries = 3;
                while (!browserReady && retries > 0) {
                    try {
                        if (isBrowserSessionHealthy()) {
                            JLog.write("[GlobalHooks] ✓ Browser successfully initialized");
                            browserReady = true;
                        } else {
                            JLog.write("[GlobalHooks] ⚠ Browser not healthy after cleanup - retrying (attempt " + (4 - retries) + ")");
                            Thread.sleep(2000);
                            ensureSeleniumCacheDirExists();
                            BrowserManager.INSTANCE.startBrowser();
                            Thread.sleep(1000);
                            retries--;
                        }
                    } catch (Exception e) {
                        JLog.write("[GlobalHooks] ⚠ Browser startup failed - retrying: " + e.getMessage());
                        retries--;
                        if (retries > 0) {
                            Thread.sleep(2000);
                        }
                    }
                }
                
                if (!browserReady) {
                    JLog.write("[GlobalHooks] ✗ CRITICAL: Browser failed to initialize after 3 retries - scenario will fail at login");
                }
            } else {
                // Reset counter on successful scenario
                sessionRecoveryAttempts = 0;
                
                // Verify current browser is still healthy (could have crashed between scenarios)
                if (!isBrowserSessionHealthy()) {
                    JLog.write("[GlobalHooks] ⚠ Browser health check failed even though previous scenario passed - forcing cleanup");
                    lastScenarioFailed = true;
                    cleanupBrowserEnvironment();
                    Thread.sleep(1000);
                    ensureSeleniumCacheDirExists();
                    BrowserManager.INSTANCE.startBrowser();
                    Thread.sleep(1000);
                }
            }

        } catch (InterruptedException ie) {
            JLog.write("[GlobalHooks] ✗ InterruptedException in validateEnvironmentBeforeScenario: " + ie.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ✗ Error in validateEnvironmentBeforeScenario: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Runs AFTER all other @After hooks (order=Integer.MAX_VALUE)
     * Ensures browser is properly closed after scenario failures.
     *
     * <p>Also handles the case where a passing scenario triggers a file download via
     * {@code javascript:reportCall()} — this opens an extra Chrome tab/window, and
     * after the download starts Chrome can disconnect its DevTools session even though
     * Selenium reports the scenario as PASSED.  The dead session then leaks into the
     * next scenario causing a cascade of NoSuchSessionException.
     *
     * <p>Fix: after every passing scenario we (1) close any extra download windows
     * that were left open, and (2) do a lightweight health-ping on the WebDriver
     * session.  If the session is found dead we force cleanup so that the next
     * scenario's {@code @Before} hook provides a fresh browser.
     */
    @After(order = Integer.MAX_VALUE)
    public void cleanupAfterScenario(Scenario scenario) {
        try {
            JLog.write("[GlobalHooks] ========================================");
            JLog.write("[GlobalHooks] After Scenario: " + scenario.getName());
            JLog.write("[GlobalHooks] Scenario Status: " + scenario.getStatus());
            JLog.write("[GlobalHooks] ========================================");

            if (scenario.isFailed()) {
                JLog.write("[GlobalHooks] ⚠ Scenario FAILED - closing browser for clean start");
                lastScenarioFailed = true;
                cleanupBrowserEnvironment();
            } else {
                // Even for PASSING scenarios:
                // reportCall() / download links open a new Chrome tab for the file.
                // That extra tab must be closed before the next scenario logs in,
                // otherwise the BrowserManager main-window reference becomes stale.
                closeExtraDownloadWindows();

                // After closing extra windows, verify the session is still alive.
                // Chrome can silently disconnect while processing a large Excel/ZIP
                // download even though Selenium sees the scenario as passed.
                if (!isBrowserSessionHealthy()) {
                    JLog.write("[GlobalHooks] ⚠ Browser session dead after passing scenario "
                            + "(likely a file-download crash) - forcing cleanup for next scenario");
                    lastScenarioFailed = true;
                    cleanupBrowserEnvironment();
                } else {
                    JLog.write("[GlobalHooks] ✓ Scenario PASSED and browser is healthy");
                    lastScenarioFailed = false;
                    sessionRecoveryAttempts = 0;
                }
            }

        } catch (Exception e) {
            JLog.write("[GlobalHooks] ✗ Error in cleanupAfterScenario: " + e.getMessage());
        }
    }

    /**
     * Closes any extra browser windows / tabs that were opened by a report-download
     * link (e.g. {@code javascript:reportCall()}) during the just-completed scenario.
     *
     * <p>Only the first window handle (the main MTCM window) is retained; all others
     * are closed and the driver is switched back to the main window so the next
     * scenario starts in a predictable state.
     */
    private void closeExtraDownloadWindows() {
        try {
            java.lang.reflect.Field driverField = BrowserManager.class.getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(null);
            if (driver == null) {
                JLog.write("[GlobalHooks] closeExtraDownloadWindows: driver is null, skipping");
                return;
            }

            java.util.Set<String> handles = driver.getWindowHandles();
            if (handles.size() <= 1) {
                return; // only the main window — nothing to do
            }

            JLog.write("[GlobalHooks] Detected " + handles.size()
                    + " open browser windows — closing extra download tab(s)");
            String mainWindow = handles.iterator().next();
            for (String handle : handles) {
                if (!handle.equals(mainWindow)) {
                    try {
                        driver.switchTo().window(handle).close();
                        JLog.write("[GlobalHooks] ✓ Closed extra download window: " + handle);
                    } catch (Exception ex) {
                        JLog.write("[GlobalHooks] ⚠ Could not close window " + handle
                                + ": " + ex.getMessage());
                    }
                }
            }
            driver.switchTo().window(mainWindow);
            JLog.write("[GlobalHooks] ✓ Switched back to main window after download cleanup");

        } catch (WebDriverException e) {
            // Session already dead (NoSuchSessionException is a WebDriverException subtype)
            JLog.write("[GlobalHooks] ⚠ Session already dead during window cleanup: "
                    + e.getMessage().split("\n")[0]);
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ⚠ Error during extra-window cleanup: " + e.getMessage());
        }
    }

    /**
     * Returns {@code true} if the current WebDriver session is alive and can accept
     * commands.  Uses the same reflection approach as {@link #cleanupBrowserEnvironment}
     * to avoid modifying the framework library.
     *
     * <p>A {@code getWindowHandle()} call is used as the health ping because it is
     * non-destructive and throws {@link NoSuchSessionException} immediately when the
     * ChromeDriver session has been disconnected (e.g. after a large file download
     * crashes the DevTools connection).
     */
    private boolean isBrowserSessionHealthy() {
        try {
            java.lang.reflect.Field driverField = BrowserManager.class.getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(null);
            if (driver == null) {
                JLog.write("[GlobalHooks] Health check: driver field is null");
                return false;
            }
            driver.getWindowHandle(); // basic liveness check
            // Also call getCurrentUrl() — more likely to expose a silent DevTools disconnect
            // that getWindowHandle() can miss immediately after a large file download.
            driver.getCurrentUrl();
            JLog.write("[GlobalHooks] Health check: session is alive");
            return true;
        } catch (WebDriverException e) {
            // NoSuchSessionException is a subtype of WebDriverException
            JLog.write("[GlobalHooks] Health check: session DEAD — "
                    + e.getMessage().split("\n")[0]);
            return false;
        } catch (Exception e) {
            JLog.write("[GlobalHooks] Health check: unexpected error — " + e.getMessage());
            return false; // treat as unhealthy to be safe
        }
    }

    /**
     * Cleanup browser environment - close browser and kill processes
     */
    private void cleanupBrowserEnvironment() {
        JLog.write("[GlobalHooks] Starting browser environment cleanup...");

        try {
            // Try graceful close first using BrowserManager
            BrowserManager.INSTANCE.closeAll();
            JLog.write("[GlobalHooks] ✓ BrowserManager.closeAll() executed");
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ⚠ Error during graceful close: " + e.getMessage());
        }

        // Force cleanup - kill any lingering browser processes (Chrome AND Edge)
        try {
            String[] killTargets = {
                "chromedriver.exe", "chrome.exe",
                "msedgedriver.exe", "msedge.exe"
            };
            for (String proc : killTargets) {
                try {
                    Process p = Runtime.getRuntime().exec("taskkill /F /IM " + proc + " /T");
                    p.waitFor();
                } catch (Exception ignore) {}
            }
            Thread.sleep(2000);
            JLog.write("[GlobalHooks] ✓ Browser processes cleaned up (Chrome+Edge)");
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ⚠ Error during process cleanup: " + e.getMessage());
        }

        // Force-clear stale BrowserManager static fields via reflection.
        // BrowserManager.closeAll() calls resetActiveBrowser() which internally calls
        // resetWebDriver() — if the session is already dead that call throws,
        // leaving activeBrowser/driver/browserTracker non-null with the dead session
        // ID. Directly nulling the static fields bypasses the broken resetWebDriver()
        // path so the next call to getBrowser() creates a brand-new browser instance.
        try {
            for (String fieldName : new String[]{"activeBrowser", "driver"}) {
                try {
                    java.lang.reflect.Field f = BrowserManager.class.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    f.set(null, null);
                } catch (NoSuchFieldException ignore) {}
            }
            try {
                java.lang.reflect.Field trackerField = BrowserManager.class.getDeclaredField("browserTracker");
                trackerField.setAccessible(true);
                trackerField.set(null, new java.util.LinkedHashSet<>());
            } catch (NoSuchFieldException ignore) {}
            try {
                java.lang.reflect.Field indexField = BrowserManager.class.getDeclaredField("browserIndex");
                indexField.setAccessible(true);
                indexField.setInt(null, 0);
            } catch (NoSuchFieldException ignore) {}
            try {
                java.lang.reflect.Field windowsField = BrowserManager.class.getDeclaredField("windowsStack");
                windowsField.setAccessible(true);
                windowsField.set(null, new java.util.Stack<>());
            } catch (NoSuchFieldException ignore) {}

            JLog.write("[GlobalHooks] ✓ BrowserManager stale state cleared");
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ⚠ Could not clear BrowserManager state: " + e.getMessage());
        }

        // CRITICAL: Also null AbstractBrowser.webdriver (static field shared by all
        // browser types). If Chrome crashes, resetWebDriver() never ran, so this
        // static field still holds the dead WebDriver. Next driver() call on
        // ChromeBrowser returns the dead driver without creating a new one.
        try {
            // AbstractBrowser is package-private so use Class.forName() for reflection
            Class<?> abstractBrowserClass = Class.forName("com.test.selenium.common.browser.AbstractBrowser");
            java.lang.reflect.Field wdField = abstractBrowserClass.getDeclaredField("webdriver");
            wdField.setAccessible(true);
            wdField.set(null, null);
            JLog.write("[GlobalHooks] ✓ AbstractBrowser.webdriver cleared");
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ⚠ Could not clear AbstractBrowser.webdriver: " + e.getMessage());
        }

        // Pre-start a fresh browser so the next scenario's login step finds an
        // active WebDriver session instead of a null/dead one.
        // TRY UP TO 3 TIMES to ensure browser is successfully started
        int browserStartRetries = 3;
        boolean browserStartedSuccessfully = false;
        while (browserStartRetries > 0 && !browserStartedSuccessfully) {
            try {
                Thread.sleep(500); // Small delay before retry
                ensureSeleniumCacheDirExists(); // framework forceDelete safety
                BrowserManager.INSTANCE.startBrowser();
                Thread.sleep(1500); // Wait for browser to fully initialize
                
                // Verify the browser is actually healthy
                if (isBrowserSessionHealthy()) {
                    JLog.write("[GlobalHooks] ✓ Fresh browser session pre-started successfully for next scenario");
                    browserStartedSuccessfully = true;
                } else {
                    JLog.write("[GlobalHooks] ⚠ Browser started but health check failed - retrying (attempt " + (4 - browserStartRetries) + "/3)");
                    browserStartRetries--;
                    if (browserStartRetries > 0) {
                        // Clear and try again
                        try {
                            BrowserManager.INSTANCE.closeAll();
                            Thread.sleep(1000);
                        } catch (Exception ignore) {}
                    }
                }
            } catch (InterruptedException ie) {
                JLog.write("[GlobalHooks] ⚠ Interrupted while pre-starting browser: " + ie.getMessage());
                Thread.currentThread().interrupt();
                browserStartRetries = 0;
            } catch (Exception e) {
                browserStartRetries--;
                if (browserStartRetries > 0) {
                    JLog.write("[GlobalHooks] ⚠ Could not pre-start browser (attempt " + (4 - browserStartRetries) + "/3, retrying): " + e.getMessage().split("\n")[0]);
                } else {
                    JLog.write("[GlobalHooks] ✗ CRITICAL: Could not pre-start browser after 3 attempts: " + e.getMessage().split("\n")[0]);
                    JLog.write("[GlobalHooks] ⚠ Next scenario will attempt to create browser during login step");
                }
            }
        }

        JLog.write("[GlobalHooks] Browser environment cleanup complete");
    }

    /**
     * Safe alert handling method - can be called from other classes
     * Returns true if an alert was handled, false otherwise
     */
    public static boolean safeHandleAlert(WebDriver driver) {
        if (driver == null) {
            JLog.write("[GlobalHooks] ⚠ Cannot handle alert - driver is null");
            return false;
        }

        try {
            driver.switchTo().alert().dismiss();
            JLog.write("[GlobalHooks] ✓ Alert dismissed");
            return true;
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            // No alert present - this is normal, not an error
            return false;
        } catch (NoSuchSessionException e) {
            JLog.write("[GlobalHooks] ⚠ Cannot handle alert - session invalid");
            lastScenarioFailed = true;
            return false;
        } catch (Exception e) {
            JLog.write("[GlobalHooks] ⚠ Unexpected error handling alert: " + e.getMessage());
            return false;
        }
    }
}
