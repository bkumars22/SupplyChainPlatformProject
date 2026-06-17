// File: `src/test/java/com/scplatform/selenium/api_common/reporting/PublishResults.java`
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.common.reporting;

import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class PublishResults implements IExecutionListener, ITestListener {

  private static volatile boolean noStackInfo;
  private static volatile boolean isCucumber;
  private static volatile boolean zipReport = false;

  // Atomic counters — incremented by ITestListener callbacks
  private static final AtomicInteger passed  = new AtomicInteger(0);
  private static final AtomicInteger failed  = new AtomicInteger(0);
  private static final AtomicInteger skipped = new AtomicInteger(0);

  // Instance methods for framework compatibility (CukeBaseTest expects these)
  public void noStackInfo(boolean value) {
    noStackInfo = value;
  }

  public static boolean isStackInfo() {
    return noStackInfo;
  }

  public void setIsCucumber(boolean value) {
    isCucumber = value;
  }

  public boolean isCucumber() {
    return isCucumber;
  }

  public static void setZipReport(boolean value) {
    zipReport = value;
  }

  public static boolean isZipReport() {
    return zipReport;
  }

  // Static convenience methods for direct access (delegates to instance)
  private static final PublishResults INSTANCE = new PublishResults();

  public static void setNoStackInfoStatic(boolean value) {
    INSTANCE.noStackInfo(value);
  }

  public static void setIsCucumberStatic(boolean value) {
    INSTANCE.setIsCucumber(value);
  }

  public static boolean isStackInfoStatic() {
    return INSTANCE.isStackInfo();
  }

  public static boolean isCucumberStatic() {
    return INSTANCE.isCucumber();
  }

  // ── ITestListener: count every Cucumber scenario / TestNG test ──────────────

  @Override
  public void onTestSuccess(ITestResult result) {
    passed.incrementAndGet();
  }

  @Override
  public void onTestFailure(ITestResult result) {
    failed.incrementAndGet();
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    skipped.incrementAndGet();
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    passed.incrementAndGet();
  }

  // ── helpers ─────────────────────────────────────────────────────────────────

  private static void log(String msg) {
    String line = "[PublishResults] " + msg;
    // Bamboo usually captures stdout; Reporter helps when stdout is hidden.
    System.out.println(line);
    try {
      Reporter.log(line, true);
    } catch (Throwable ignored) {
      // Reporter may not be available in some runners; keep stdout logging.
    }
  }

  @Override
  public void onExecutionStart() {
    log("========================================");
    log("PUBLISH RESULTS - EXECUTION START");
    log("========================================");
    log("noStackInfo=" + noStackInfo);
    log("wtg_base_url=" + maskIfBlank(System.getProperty("wtg_base_url")));
    log("wtg_publish_endpoint=" + maskIfBlank(System.getProperty("wtg_publish_endpoint")));
    log("component_name=" + maskIfBlank(System.getProperty("component_name")));
    log("wtg_p_id=" + maskIfBlank(System.getProperty("wtg_p_id")));
    log("release_version=" + maskIfBlank(System.getProperty("release_version")));
    log("execution_type=" + maskIfBlank(System.getProperty("execution_type")));
    log("test_run_id=" + maskIfBlank(System.getProperty("test_run_id")));
    log("BambooBuildNumber=" + maskIfBlank(System.getProperty("BambooBuildNumber")));
    log("========================================");
  }

  @Override
  public void onExecutionFinish() {
    log("========================================");
    log("PUBLISH RESULTS - EXECUTION FINISH");
    log("========================================");
    log("Preparing to publish test results...");

    String baseUrl = prop("wtg_base_url", "");
    String endpoint = prop("wtg_publish_endpoint", "");

    log("Base URL: " + (baseUrl.isBlank() ? "<BLANK - WILL NOT PUBLISH>" : baseUrl));
    log("Endpoint: " + (endpoint.isBlank() ? "<BLANK - WILL NOT PUBLISH>" : endpoint));

    int p = passed.get();
    int f = failed.get();
    int s = skipped.get();
    int total = p + f + s;

    log("Test counts — passed=" + p + ", failed=" + f + ", skipped=" + s + ", total=" + total);

    // Build the payload (used for both HTTP POST and file report)
    String payload = "{"
        + "\"component_name\":\"" + esc(prop("component_name", prop("model", ""))) + "\","
        + "\"wtg_p_id\":\"" + esc(prop("wtg_p_id", "")) + "\","
        + "\"release_version\":\"" + esc(prop("release_version", "")) + "\","
        + "\"execution_type\":\"" + esc(prop("execution_type", "")) + "\","
        + "\"test_run_id\":\"" + esc(prop("test_run_id", "")) + "\","
        + "\"BambooBuildNumber\":\"" + esc(prop("BambooBuildNumber", prop("buildNumber", ""))) + "\","
        + "\"timestamp\":\"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\","
        + "\"total_tests\":" + total + ","
        + "\"passed\":" + p + ","
        + "\"failed\":" + f + ","
        + "\"skipped\":" + s + ","
        + "\"status\":\"" + (f > 0 ? "failed" : "passed") + "\""
        + "}";

    // Always write local report files for Bamboo artifact (both JSON and HTML)
    writeReportFile(payload);
    writeHtmlReportFile(payload, p, f, s, total);

    // Optionally send to remote URL if configured
    if (baseUrl.isBlank() || endpoint.isBlank()) {
      log("⚠️  Skipping remote publish: Missing URL configuration");
      log("⚠️  baseUrlBlank=" + baseUrl.isBlank() + ", endpointBlank=" + endpoint.isBlank());
      log("✅  Local report file created successfully");
      log("========================================");
      return;
    }

    String url = joinUrl(baseUrl, endpoint);
    log("Publishing to URL: " + url);
    log("Payload: " + payload);
    log("Sending HTTP POST request...");

    try {
      postJson(url, payload);
      log("✅ PUBLISH COMPLETED SUCCESSFULLY (Remote + Local)");
    } catch (Exception e) {
      log("⚠️  Remote publish failed, but local report file was created");
      log("⚠️  Error: " + e.getMessage());
    }

    log("========================================");
  }

  // Legacy compatibility for callers like `GenerateReport`
  public String publish(String suiteName, String recipients) {
    log("========================================");
    log("LEGACY PUBLISH METHOD CALLED");
    log("========================================");
    log("Suite Name: " + (suiteName == null ? "<null>" : suiteName));
    log("Recipients: " + (recipients == null ? "<null>" : recipients));

    String baseUrl = prop("wtg_base_url", "");
    String endpoint = prop("wtg_publish_endpoint", "");

    if (baseUrl.isBlank() || endpoint.isBlank()) {
      log("⚠️  Skipping legacy publish: missing wtg_base_url/wtg_publish_endpoint");
      log("⚠️  baseUrlBlank=" + baseUrl.isBlank() + ", endpointBlank=" + endpoint.isBlank());
      return "SKIPPED: Missing wtg_base_url or wtg_publish_endpoint";
    }

    String url = joinUrl(baseUrl, endpoint);
    String payload = "{"
        + "\"suiteName\":\"" + esc(suiteName == null ? "TestNG" : suiteName) + "\","
        + "\"recipients\":\"" + esc(recipients == null ? "" : recipients) + "\""
        + "}";

    log("Legacy payload: " + payload);

    try {
      postJson(url, payload);
      return "SUCCESS: Published to " + url;
    } catch (Exception e) {
      log("❌ Legacy publish failed: " + e.getMessage());
      return "FAILED: " + e.getMessage();
    }
  }

  private static String prop(String key, String def) {
    String v = System.getProperty(key);
    return (v == null || v.isBlank()) ? def : v.trim();
  }

  private static String joinUrl(String baseUrl, String endpoint) {
    String b = baseUrl.trim();
    String e = endpoint.trim();
    if (b.endsWith("/") && e.startsWith("/"))
      return b.substring(0, b.length() - 1) + e;
    if (!b.endsWith("/") && !e.startsWith("/"))
      return b + "/" + e;
    return b + e;
  }

  private static void postJson(String url, String json) {
    log("Sending POST request to: " + url);
    log("Request payload length: " + json.length() + " bytes");

    try {
      HttpClient http = HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(15))
          .build();

      HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(Duration.ofSeconds(60))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
          .build();

      log("Waiting for server response...");
      HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

      log("Response received: HTTP " + resp.statusCode());
      log("Response body: " + safe(resp.body()));
      log("Response body length: " + (resp.body() != null ? resp.body().length() : 0) + " bytes");

      if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
        log("❌ ERROR: HTTP " + resp.statusCode() + " - Publish failed!");
        throw new IllegalStateException("Publish failed. HTTP " + resp.statusCode());
      }

      log("✅ SUCCESS: HTTP " + resp.statusCode() + " - Results published successfully");
    } catch (IOException | InterruptedException e) {
      if (e instanceof InterruptedException)
        Thread.currentThread().interrupt();

      log("❌ EXCEPTION during publish:");
      log("   Exception type: " + e.getClass().getSimpleName());
      log("   Exception message: " + e.getMessage());

      if (noStackInfo) {
        log("   Stack trace suppressed (noStackInfo=true)");
      } else {
        log("   Stack trace:");
        e.printStackTrace(System.out);
      }

      throw new IllegalStateException("Publish failed: " + e.getMessage(), e);
    }
  }

  private static String esc(String s) {
    if (s == null)
      return "";
    return s.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\r", "\\r")
        .replace("\n", "\\n")
        .replace("\t", "\\t");
  }

  private static String safe(String s) {
    return s == null ? "<null>" : s;
  }

  private static String maskIfBlank(String v) {
    return (v == null || v.isBlank()) ? "<blank>" : v;
  }

  /**
   * Writes the automation report to a file for Bamboo artifact collection.
   * This ensures the "Automation Report" artifact is not 0 bytes.
   */
  private static void writeReportFile(String jsonPayload) {
    String reportDir = prop("automation.report.dir", "target");
    String reportFile = prop("automation.report.file", "automation-report.json");

    File dir = new File(reportDir);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      log("Report directory created: " + created + " (" + dir.getAbsolutePath() + ")");
    }

    File report = new File(dir, reportFile);

    try (PrintWriter writer = new PrintWriter(new FileWriter(report, StandardCharsets.UTF_8))) {
      // Write formatted JSON with metadata
      writer.println("{");
      writer.println("  \"report_metadata\": {");
      writer.println("    \"report_type\": \"automation_execution\",");
      writer.println(
          "    \"generated_at\": \"" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\",");
      writer.println("    \"report_version\": \"1.0\"");
      writer.println("  },");
      writer.println("  \"execution_data\": " + jsonPayload + ",");
      writer.println("  \"bamboo_info\": {");
      writer
          .println("    \"build_number\": \"" + esc(prop("BambooBuildNumber", prop("buildNumber", "unknown"))) + "\",");
      writer.println("    \"project_key\": \"" + esc(prop("bamboo.planKey", "unknown")) + "\",");
      writer.println("    \"build_key\": \"" + esc(prop("bamboo.buildKey", "unknown")) + "\"");
      writer.println("  },");
      writer.println("  \"reports\": {");
      writer.println("    \"surefire_reports\": \"target/surefire-reports\",");
      writer.println("    \"testng_reports\": \"test-output\"");
      writer.println("  }");
      writer.println("}");

      log("✅ Report file written successfully:");
      log("   Location: " + report.getAbsolutePath());
      log("   Size: " + report.length() + " bytes");

    } catch (IOException e) {
      log("❌ ERROR: Failed to write report file: " + e.getMessage());
      e.printStackTrace(System.out);
    }
  }

  /**
   * Writes the automation report as HTML for Bamboo artifact display.
   */
  private static void writeHtmlReportFile(String jsonPayload, int passedCount, int failedCount, int skippedCount, int totalCount) {
    String reportDir = prop("automation.report.dir", "target");
    String htmlReportFile = "bamboo-AutomationReport-artifact.html";

    File dir = new File(reportDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    File htmlReport = new File(dir, htmlReportFile);
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    String buildNumber = prop("BambooBuildNumber", prop("buildNumber", "unknown"));
    String componentName = prop("component_name", prop("model", "SCPlatform"));
    String releaseVersion = prop("release_version", "unknown");
    String executionType = prop("execution_type", "unknown");

    try (PrintWriter writer = new PrintWriter(new FileWriter(htmlReport, StandardCharsets.UTF_8))) {
      writer.println("<!DOCTYPE html>");
      writer.println("<html lang='en'>");
      writer.println("<head>");
      writer.println("    <meta charset='UTF-8'>");
      writer.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
      writer.println("    <title>SCPlatform Automation Report - Build " + esc(buildNumber) + "</title>");
      writer.println("    <style>");
      writer.println("        * { margin: 0; padding: 0; box-sizing: border-box; }");
      writer.println(
          "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f7fa; padding: 20px; }");
      writer.println(
          "        .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }");
      writer.println(
          "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 8px 8px 0 0; }");
      writer.println("        .header h1 { font-size: 28px; margin-bottom: 10px; }");
      writer.println("        .header p { opacity: 0.9; font-size: 14px; }");
      writer.println("        .content { padding: 30px; }");
      writer.println(
          "        .info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }");
      writer.println(
          "        .info-card { background: #f8f9fa; padding: 20px; border-radius: 6px; border-left: 4px solid #667eea; }");
      writer.println(
          "        .info-card .label { color: #6c757d; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px; }");
      writer.println("        .info-card .value { color: #212529; font-size: 20px; font-weight: 600; }");
      writer.println("        .info-card.passed  { border-left-color: #28a745; }");
      writer.println("        .info-card.failed  { border-left-color: #dc3545; }");
      writer.println("        .info-card.skipped { border-left-color: #ffc107; }");
      writer.println("        .info-card.total   { border-left-color: #17a2b8; }");
      writer.println("        .val-passed  { color: #28a745 !important; }");
      writer.println("        .val-failed  { color: #dc3545 !important; }");
      writer.println("        .val-skipped { color: #856404 !important; }");
      writer.println(
          "        .status { display: inline-block; padding: 6px 12px; border-radius: 4px; font-size: 12px; font-weight: 600; text-transform: uppercase; }");
      writer.println("        .status.success { background: #d4edda; color: #155724; }");
      writer.println("        .status.info { background: #d1ecf1; color: #0c5460; }");
      writer.println("        .section { margin-bottom: 30px; }");
      writer.println(
          "        .section h2 { font-size: 20px; color: #212529; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 2px solid #e9ecef; }");
      writer.println(
          "        .report-link { display: block; padding: 15px; background: #e7f3ff; border-radius: 6px; text-decoration: none; color: #0366d6; margin-bottom: 10px; transition: all 0.2s; }");
      writer.println("        .report-link:hover { background: #cfe8ff; transform: translateX(5px); }");
      writer.println("        .report-link strong { display: block; margin-bottom: 5px; }");
      writer.println("        .report-link span { font-size: 13px; color: #666; }");
      writer.println(
          "        .footer { background: #f8f9fa; padding: 20px 30px; border-radius: 0 0 8px 8px; border-top: 1px solid #e9ecef; color: #6c757d; text-align: center; font-size: 13px; }");
      writer.println(
          "        pre { background: #f4f4f4; padding: 15px; border-radius: 4px; overflow-x: auto; font-size: 12px; }");
      writer.println("    </style>");
      writer.println("</head>");
      writer.println("<body>");
      writer.println("    <div class='container'>");
      writer.println("        <div class='header'>");
      writer.println("            <h1>SCPlatform Automation Test Report</h1>");
      writer.println("            <p>Build #" + esc(buildNumber) + " | Generated on " + timestamp + "</p>");
      writer.println("        </div>");
      writer.println("        <div class='content'>");
      writer.println("            <div class='info-grid'>");
      writer.println("                <div class='info-card'>");
      writer.println("                    <div class='label'>Component</div>");
      writer.println("                    <div class='value'>" + esc(componentName) + "</div>");
      writer.println("                </div>");
      writer.println("                <div class='info-card'>");
      writer.println("                    <div class='label'>Release Version</div>");
      writer.println("                    <div class='value'>" + esc(releaseVersion) + "</div>");
      writer.println("                </div>");
      writer.println("                <div class='info-card'>");
      writer.println("                    <div class='label'>Execution Type</div>");
      writer.println("                    <div class='value'>" + esc(executionType) + "</div>");
      writer.println("                </div>");
      writer.println("                <div class='info-card'>");
      writer.println("                    <div class='label'>Build Number</div>");
      writer.println("                    <div class='value'>" + esc(buildNumber) + "</div>");
      writer.println("                </div>");
      writer.println("            </div>");
      writer.println("            <div class='info-grid'>");
      writer.println("                <div class='info-card total'>");
      writer.println("                    <div class='label'>Total Scenarios</div>");
      writer.println("                    <div class='value'>" + totalCount + "</div>");
      writer.println("                </div>");
      writer.println("                <div class='info-card passed'>");
      writer.println("                    <div class='label'>Passed</div>");
      writer.println("                    <div class='value val-passed'>" + passedCount + "</div>");
      writer.println("                </div>");
      writer.println("                <div class='info-card failed'>");
      writer.println("                    <div class='label'>Failed</div>");
      writer.println("                    <div class='value val-failed'>" + failedCount + "</div>");
      writer.println("                </div>");
      writer.println("                <div class='info-card skipped'>");
      writer.println("                    <div class='label'>Skipped</div>");
      writer.println("                    <div class='value val-skipped'>" + skippedCount + "</div>");
      writer.println("                </div>");
      writer.println("            </div>");
      writer.println("            <div class='section'>");
      writer.println("                <h2>📊 Test Reports</h2>");
      writer.println("                <a href='../../surefire-reports/index.html' class='report-link'>");
      writer.println("                    <strong>Surefire Test Report</strong>");
      writer.println("                    <span>Detailed unit and integration test results</span>");
      writer.println("                </a>");
      writer.println("                <a href='../../../test-output/index.html' class='report-link'>");
      writer.println("                    <strong>TestNG Report</strong>");
      writer.println("                    <span>TestNG execution summary and detailed results</span>");
      writer.println("                </a>");
      writer.println("                <a href='../../../target/cucumber-html-report/index.html' class='report-link'>");
      writer.println("                    <strong>Cucumber Report</strong>");
      writer.println("                    <span>BDD scenario execution results</span>");
      writer.println("                </a>");
      writer.println("            </div>");
      writer.println("            <div class='section'>");
      writer.println("                <h2>📝 Execution Summary</h2>");
      writer.println("                <pre>" + jsonPayload.replace("<", "&lt;").replace(">", "&gt;") + "</pre>");
      writer.println("            </div>");
      writer.println("        </div>");
      writer.println("        <div class='footer'>");
      writer.println(
          "            <p>&copy; 2026 E2open Inc. | Automation Framework | Build Timestamp: " + timestamp + "</p>");
      writer.println("        </div>");
      writer.println("    </div>");
      writer.println("</body>");
      writer.println("</html>");

      log("✅ HTML report file written successfully:");
      log("   Location: " + htmlReport.getAbsolutePath());
      log("   Size: " + htmlReport.length() + " bytes");

    } catch (IOException e) {
      log("❌ ERROR: Failed to write HTML report file: " + e.getMessage());
      e.printStackTrace(System.out);
    }
  }
}
