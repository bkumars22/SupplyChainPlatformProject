/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.user.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for extracting client information from HTTP requests.
 * 
 * <p>Used to generate unique client identifiers for session tracking
 * and concurrent login detection.
 * 
 * @author PCM Team
 */
public final class ClientInfoUtil {

    private ClientInfoUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Generate a unique client identifier based on browser, OS, and session ID.
     * 
     * @param request the HTTP request
     * @return unique client identifier string
     */
    public static String getClientUniqueId(HttpServletRequest request) {
        String browser = getClientBrowser(request);
        String os = getClientOS(request);
        String sessionId = getSessionId(request);

        return String.valueOf(browser.hashCode()) + os.hashCode() + sessionId;
    }

    /**
     * Get the session ID from the request.
     * 
     * @param request the HTTP request
     * @return session ID
     */
    public static String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }

    /**
     * Get the client's IP address, considering proxy headers.
     * 
     * @param request the HTTP request
     * @return client IP address
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isBlank(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isBlank(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Detect the client's operating system from User-Agent header.
     * 
     * @param request the HTTP request
     * @return detected OS name
     */
    public static String getClientOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }

        String lowerCaseAgent = userAgent.toLowerCase();

        if (lowerCaseAgent.contains("windows")) {
            return "Windows";
        } else if (lowerCaseAgent.contains("mac")) {
            return "Mac";
        } else if (lowerCaseAgent.contains("x11")) {
            return "Unix";
        } else if (lowerCaseAgent.contains("android")) {
            return "Android";
        } else if (lowerCaseAgent.contains("iphone")) {
            return "iPhone";
        } else {
            return "Unknown";
        }
    }

    /**
     * Detect the client's browser from User-Agent header.
     * 
     * @param request the HTTP request
     * @return detected browser name and version
     */
    public static String getClientBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }

        String lowerCaseAgent = userAgent.toLowerCase();

        if (lowerCaseAgent.contains("edg")) {
            return extractBrowserVersion(userAgent, "Edg");
        } else if (lowerCaseAgent.contains("msie")) {
            return extractIEVersion(userAgent);
        } else if (lowerCaseAgent.contains("trident")) {
            return "IE-11";
        } else if (lowerCaseAgent.contains("opr") || lowerCaseAgent.contains("opera")) {
            return extractOperaVersion(userAgent, lowerCaseAgent);
        } else if (lowerCaseAgent.contains("chrome")) {
            return extractBrowserVersion(userAgent, "Chrome");
        } else if (lowerCaseAgent.contains("safari") && lowerCaseAgent.contains("version")) {
            return extractSafariVersion(userAgent);
        } else if (lowerCaseAgent.contains("firefox")) {
            return extractBrowserVersion(userAgent, "Firefox");
        } else {
            return "Unknown";
        }
    }

    private static String extractBrowserVersion(String userAgent, String browserName) {
        try {
            int index = userAgent.indexOf(browserName);
            if (index >= 0) {
                String substring = userAgent.substring(index).split(" ")[0];
                return substring.replace("/", "-");
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return browserName;
    }

    private static String extractIEVersion(String userAgent) {
        try {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            return "IE-" + substring.split(" ")[1];
        } catch (Exception e) {
            return "IE";
        }
    }

    private static String extractOperaVersion(String userAgent, String lowerCaseAgent) {
        try {
            if (lowerCaseAgent.contains("opr")) {
                return userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]
                        .replace("/", "-").replace("OPR", "Opera");
            } else {
                return "Opera";
            }
        } catch (Exception e) {
            return "Opera";
        }
    }

    private static String extractSafariVersion(String userAgent) {
        try {
            String version = userAgent.substring(userAgent.indexOf("Version")).split(" ")[0].split("/")[1];
            return "Safari-" + version;
        } catch (Exception e) {
            return "Safari";
        }
    }

    private static boolean isBlank(String str) {
        return str == null || str.isEmpty() || "unknown".equalsIgnoreCase(str);
    }
}
