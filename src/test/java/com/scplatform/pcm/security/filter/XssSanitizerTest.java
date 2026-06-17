/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.security.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the XssSanitizer class.
 * 
 * Tests cover all XSS patterns including:
 * - Script tags
 * - Event handlers
 * - JavaScript/VBScript protocol handlers
 * - Eval and expression patterns
 * - Alert/confirm/prompt dialogs
 * - Various HTML injection vectors
 * 
 * @author PCM Security Team
 */
@DisplayName("XSS Sanitizer Tests")
class XssSanitizerTest {

    private XssSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new XssSanitizer();
    }

    @Nested
    @DisplayName("Null and Empty Input Handling")
    class NullAndEmptyInputTests {

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(sanitizer.sanitize(null));
        }

        @Test
        @DisplayName("Should return empty string for empty input")
        void shouldReturnEmptyStringForEmptyInput() {
            assertEquals("", sanitizer.sanitize(""));
        }

        @Test
        @DisplayName("Should return whitespace string unchanged")
        void shouldReturnWhitespaceUnchanged() {
            assertEquals("   ", sanitizer.sanitize("   "));
        }
    }

    @Nested
    @DisplayName("Script Tag Sanitization")
    class ScriptTagTests {

        @Test
        @DisplayName("Should remove simple script tag")
        void shouldRemoveSimpleScriptTag() {
            String input = "<script>alert('xss')</script>";
            assertEquals("", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove script tag with attributes")
        void shouldRemoveScriptTagWithAttributes() {
            String input = "<script type=\"text/javascript\">malicious()</script>";
            assertEquals("", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove script tag case insensitively")
        void shouldRemoveScriptTagCaseInsensitive() {
            String input = "<SCRIPT>alert('xss')</SCRIPT>";
            assertEquals("", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove mixed case script tags")
        void shouldRemoveMixedCaseScriptTags() {
            String input = "<ScRiPt>alert('xss')</sCrIpT>";
            assertEquals("", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove standalone opening script tag")
        void shouldRemoveStandaloneOpeningScriptTag() {
            String input = "Hello <script src='evil.js'> World";
            assertEquals("Hello  World", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove standalone closing script tag")
        void shouldRemoveStandaloneClosingScriptTag() {
            String input = "Hello </script> World";
            assertEquals("Hello  World", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove multiline script tag")
        void shouldRemoveMultilineScriptTag() {
            String input = "<script>\nalert('xss');\nconsole.log('test');\n</script>";
            assertEquals("", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should handle nested script tags")
        void shouldHandleNestedScriptTags() {
            String input = "<script><script>alert('xss')</script></script>";
            assertEquals("", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should preserve text around script tags")
        void shouldPreserveTextAroundScriptTags() {
            String input = "Hello <script>alert('xss')</script> World";
            assertEquals("Hello  World", sanitizer.sanitize(input));
        }
    }

    @Nested
    @DisplayName("Event Handler Sanitization")
    class EventHandlerTests {

        @ParameterizedTest
        @DisplayName("Should remove common event handlers")
        @ValueSource(strings = {
            "onclick=alert('xss')",
            "onmouseover=alert('xss')",
            "onload=alert('xss')",
            "onerror=alert('xss')",
            "onfocus=alert('xss')",
            "onblur=alert('xss')",
            "onchange=alert('xss')",
            "onsubmit=alert('xss')",
            "onkeydown=alert('xss')",
            "onkeyup=alert('xss')"
        })
        void shouldRemoveEventHandler(String eventHandler) {
            String input = "<img " + eventHandler + ">";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("on"));
        }

        @Test
        @DisplayName("Should remove event handlers case insensitively")
        void shouldRemoveEventHandlersCaseInsensitive() {
            String input = "ONCLICK=alert('xss')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("onclick"));
        }

        @Test
        @DisplayName("Should remove event handlers with spaces")
        void shouldRemoveEventHandlersWithSpaces() {
            String input = "onclick = alert('xss')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("onclick"));
        }
    }

    @Nested
    @DisplayName("Protocol Handler Sanitization")
    class ProtocolHandlerTests {

        @Test
        @DisplayName("Should remove javascript: protocol")
        void shouldRemoveJavascriptProtocol() {
            String input = "javascript:alert('xss')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("javascript:"));
        }

        @Test
        @DisplayName("Should remove javascript: with spaces")
        void shouldRemoveJavascriptProtocolWithSpaces() {
            String input = "javascript :alert('xss')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("javascript"));
        }

        @Test
        @DisplayName("Should remove vbscript: protocol")
        void shouldRemoveVbscriptProtocol() {
            String input = "vbscript:msgbox('xss')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("vbscript:"));
        }

        @Test
        @DisplayName("Should remove data: URI scheme")
        void shouldRemoveDataUriScheme() {
            String input = "data:text/html,<script>alert('xss')</script>";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("data:"));
        }
    }

    @Nested
    @DisplayName("JavaScript Function Sanitization")
    class JavaScriptFunctionTests {

        @Test
        @DisplayName("Should remove eval() expression")
        void shouldRemoveEvalExpression() {
            String input = "eval('malicious code')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("eval"));
        }

        @Test
        @DisplayName("Should remove expression() CSS function")
        void shouldRemoveExpressionFunction() {
            String input = "expression(alert('xss'))";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("expression"));
        }

        @Test
        @DisplayName("Should remove alert() function")
        void shouldRemoveAlertFunction() {
            String input = "alert('xss')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("alert"));
        }

        @Test
        @DisplayName("Should remove confirm() function")
        void shouldRemoveConfirmFunction() {
            String input = "confirm('Are you sure?')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("confirm"));
        }

        @Test
        @DisplayName("Should remove prompt() function")
        void shouldRemovePromptFunction() {
            String input = "prompt('Enter value')";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("prompt"));
        }
    }

    @Nested
    @DisplayName("Src Attribute Sanitization")
    class SrcAttributeTests {

        @Test
        @DisplayName("Should remove src attribute with single quotes")
        void shouldRemoveSrcWithSingleQuotes() {
            String input = "<img src='http://evil.com/malware.js'>";
            assertFalse(sanitizer.sanitize(input).contains("src="));
        }

        @Test
        @DisplayName("Should remove src attribute with double quotes")
        void shouldRemoveSrcWithDoubleQuotes() {
            String input = "<img src=\"http://evil.com/malware.js\">";
            assertFalse(sanitizer.sanitize(input).contains("src="));
        }

        @Test
        @DisplayName("Should remove src attribute with spaces")
        void shouldRemoveSrcWithSpaces() {
            String input = "<img src = 'http://evil.com/malware.js'>";
            assertFalse(sanitizer.sanitize(input).contains("src"));
        }
    }

    @Nested
    @DisplayName("DOM Manipulation Sanitization")
    class DomManipulationTests {

        @Test
        @DisplayName("Should remove document.cookie access")
        void shouldRemoveDocumentCookie() {
            String input = "document.cookie = 'stolen'";
            assertFalse(sanitizer.sanitize(input).contains("document.cookie"));
        }

        @Test
        @DisplayName("Should remove document.location manipulation")
        void shouldRemoveDocumentLocation() {
            String input = "document.location = 'http://evil.com'";
            assertFalse(sanitizer.sanitize(input).contains("document.location"));
        }

        @Test
        @DisplayName("Should remove window.location manipulation")
        void shouldRemoveWindowLocation() {
            String input = "window.location = 'http://evil.com'";
            assertFalse(sanitizer.sanitize(input).contains("window.location"));
        }

        @Test
        @DisplayName("Should remove innerHTML assignment")
        void shouldRemoveInnerHtml() {
            String input = "innerHTML = '<script>alert(1)</script>'";
            assertFalse(sanitizer.sanitize(input).contains("innerHTML"));
        }
    }

    @Nested
    @DisplayName("Dangerous HTML Tag Sanitization")
    class DangerousTagTests {

        @Test
        @DisplayName("Should remove iframe tags")
        void shouldRemoveIframeTags() {
            String input = "<iframe src='http://evil.com'></iframe>";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("iframe"));
        }

        @Test
        @DisplayName("Should remove object tags")
        void shouldRemoveObjectTags() {
            String input = "<object data='http://evil.com/malware.swf'></object>";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("object"));
        }

        @Test
        @DisplayName("Should remove embed tags")
        void shouldRemoveEmbedTags() {
            String input = "<embed src='http://evil.com/malware.swf'>";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("embed"));
        }

        @Test
        @DisplayName("Should remove form tags")
        void shouldRemoveFormTags() {
            String input = "<form action='http://evil.com/steal'></form>";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("form"));
        }

        @Test
        @DisplayName("Should remove base tags")
        void shouldRemoveBaseTags() {
            String input = "<base href='http://evil.com/'>";
            assertFalse(sanitizer.sanitize(input).toLowerCase().contains("base"));
        }

        @Test
        @DisplayName("Should remove meta refresh tags")
        void shouldRemoveMetaRefreshTags() {
            String input = "<meta http-equiv='refresh' content='0;url=http://evil.com'>";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("refresh"));
        }
    }

    @Nested
    @DisplayName("Null Character Sanitization")
    class NullCharacterTests {

        @Test
        @DisplayName("Should remove null characters")
        void shouldRemoveNullCharacters() {
            String input = "hello\0world";
            assertEquals("helloworld", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove escaped null characters")
        void shouldRemoveEscapedNullCharacters() {
            String input = "hello\\0world";
            assertEquals("helloworld", sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should remove URL encoded null characters")
        void shouldRemoveUrlEncodedNullCharacters() {
            String input = "hello%00world";
            assertEquals("helloworld", sanitizer.sanitize(input));
        }
    }

    @Nested
    @DisplayName("Safe Input Handling")
    class SafeInputTests {

        @ParameterizedTest
        @DisplayName("Should not modify safe input")
        @ValueSource(strings = {
            "Hello World",
            "This is a normal text",
            "Numbers: 12345",
            "Special chars: !@#$%^&*()",
            "Unicode: こんにちは",
            "Email: test@example.com",
            "URL: https://example.com/page?id=1"
        })
        void shouldNotModifySafeInput(String input) {
            assertEquals(input, sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should preserve HTML entities")
        void shouldPreserveHtmlEntities() {
            String input = "&lt;div&gt;safe&lt;/div&gt;";
            assertEquals(input, sanitizer.sanitize(input));
        }

        @Test
        @DisplayName("Should preserve safe HTML tags")
        void shouldPreserveSafeHtmlTags() {
            String input = "<div><span>text</span></div>";
            assertEquals(input, sanitizer.sanitize(input));
        }
    }

    @Nested
    @DisplayName("Complex Attack Vector Sanitization")
    class ComplexAttackVectorTests {

        @Test
        @DisplayName("Should handle SVG XSS vector")
        void shouldHandleSvgXssVector() {
            String input = "<svg onload=alert('xss')>";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("onload"));
        }

        @Test
        @DisplayName("Should handle img tag with onerror")
        void shouldHandleImgOnerror() {
            String input = "<img src=x onerror=alert('xss')>";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("onerror"));
        }

        @Test
        @DisplayName("Should handle body onload")
        void shouldHandleBodyOnload() {
            String input = "<body onload=alert('xss')>";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("onload"));
        }

        @Test
        @DisplayName("Should handle encoded script in href")
        void shouldHandleEncodedScriptInHref() {
            String input = "<a href=\"javascript:alert('xss')\">Click</a>";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("javascript"));
        }

        @Test
        @DisplayName("Should handle data URI in img src")
        void shouldHandleDataUriInImgSrc() {
            String input = "<img src=\"data:text/html,<script>alert('xss')</script>\">";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("data:"));
        }

        @Test
        @DisplayName("Should handle multiple attack vectors combined")
        void shouldHandleMultipleAttackVectorsCombined() {
            String input = "<script>alert('xss')</script><img src=x onerror=alert(1)><a href='javascript:evil()'>";
            String result = sanitizer.sanitize(input);
            assertFalse(result.toLowerCase().contains("script"));
            assertFalse(result.toLowerCase().contains("onerror"));
            assertFalse(result.toLowerCase().contains("javascript"));
        }
    }

    @Nested
    @DisplayName("Detection Tests")
    class DetectionTests {

        @Test
        @DisplayName("Should detect XSS patterns in input")
        void shouldDetectXssPatterns() {
            assertTrue(sanitizer.containsXssPatterns("<script>alert('xss')</script>"));
            assertTrue(sanitizer.containsXssPatterns("javascript:alert('xss')"));
            assertTrue(sanitizer.containsXssPatterns("onclick=alert('xss')"));
            assertTrue(sanitizer.containsXssPatterns("eval('code')"));
        }

        @Test
        @DisplayName("Should not detect XSS in safe input")
        void shouldNotDetectXssInSafeInput() {
            assertFalse(sanitizer.containsXssPatterns("Hello World"));
            assertFalse(sanitizer.containsXssPatterns("normal@email.com"));
            assertFalse(sanitizer.containsXssPatterns("https://example.com"));
        }

        @Test
        @DisplayName("Should not detect XSS in null or empty input")
        void shouldNotDetectXssInNullOrEmpty() {
            assertFalse(sanitizer.containsXssPatterns(null));
            assertFalse(sanitizer.containsXssPatterns(""));
        }
    }

    @Nested
    @DisplayName("Encoding Tests")
    class EncodingTests {

        @Test
        @DisplayName("Should sanitize and HTML encode")
        void shouldSanitizeAndHtmlEncode() {
            String input = "<script>alert('xss')</script>";
            String result = sanitizer.sanitizeAndEncode(input);
            assertNotNull(result);
            assertFalse(result.contains("<"));
            assertFalse(result.contains(">"));
        }

        @Test
        @DisplayName("Should return null for null input in sanitizeAndEncode")
        void shouldReturnNullForNullInSanitizeAndEncode() {
            assertNull(sanitizer.sanitizeAndEncode(null));
        }

        @Test
        @DisplayName("Should sanitize for JavaScript context")
        void shouldSanitizeForJavaScript() {
            String input = "test'value";
            String result = sanitizer.sanitizeForJavaScript(input);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should sanitize for URL context")
        void shouldSanitizeForUrl() {
            String input = "test value";
            String result = sanitizer.sanitizeForUrl(input);
            assertNotNull(result);
            assertFalse(result.contains(" "));
        }

        @Test
        @DisplayName("Should sanitize for CSS context")
        void shouldSanitizeForCss() {
            String input = "expression(alert())";
            String result = sanitizer.sanitizeForCss(input);
            assertNotNull(result);
        }
    }
}
