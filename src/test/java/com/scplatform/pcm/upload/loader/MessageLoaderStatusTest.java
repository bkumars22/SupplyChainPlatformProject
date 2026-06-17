/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessageLoaderStatusTest {

    @Test
    void defaultConstructorSetsSuccessAndZeroCount() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        assertEquals(MessageLoaderStatus.SUCCESS, s.getResultCode());
        assertEquals(0, s.getCount());
        assertTrue(s.hasSucceeded());
        assertFalse(s.hasFailed());
        assertEquals("", s.getResultMessage());
    }

    @Test
    void twoArgConstructorAssignsCodeAndMessage() {
        MessageLoaderStatus s = new MessageLoaderStatus(MessageLoaderStatus.ERROR, "fail");
        assertEquals(MessageLoaderStatus.ERROR, s.getResultCode());
        assertEquals("fail", s.getResultMessage());
        assertTrue(s.hasFailed());
        assertFalse(s.hasSucceeded());
    }

    @Test
    void getResultMessagePrependsCauseWhenNotAlreadyContained() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        s.setResultMessage("primary");
        s.setExceptionCause(new RuntimeException("ROOT"));
        String msg = s.getResultMessage();
        assertTrue(msg.contains("ROOT"));
        assertTrue(msg.contains("primary"));
        assertTrue(msg.startsWith("ROOT"));
    }

    @Test
    void getResultMessageDoesNotDuplicateCauseWhenAlreadyContained() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        s.setResultMessage("Wrapped: ROOT happened");
        s.setExceptionCause(new RuntimeException("ROOT"));
        String msg = s.getResultMessage();
        // ROOT must appear only once (already inside resultMessage)
        assertEquals(1, msg.split("ROOT", -1).length - 1);
    }

    @Test
    void setResultStatusSetsErrorAndCause() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        Throwable cause = new IllegalStateException("bad");
        s.setResultStatus(cause);
        assertEquals(MessageLoaderStatus.ERROR, s.getResultCode());
        assertSame(cause, s.getExceptionCause());
        assertTrue(s.hasFailed());
    }

    @Test
    void statisticsRoundTrip() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        s.setStatistic("inserted", 5);
        s.setStatistic("updated", 3);
        assertEquals(5, s.getStatistic("inserted"));
        assertEquals(3, s.getStatistic("updated"));
        assertEquals(0, s.getStatistic("missing"));
        assertEquals(2, s.getStatistics().size());
    }

    @Test
    void toStringIncludesCoreFields() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        s.setResultMessage("hi");
        s.setLoadJobId("J-1");
        s.setStatistic("a", 1);
        String out = s.toString();
        assertNotNull(out);
        assertTrue(out.contains("ResultCode:"));
        assertTrue(out.contains("ResultMessage:hi"));
        assertTrue(out.contains("LoadJobId:J-1"));
        assertTrue(out.contains("a=1"));
    }

    @Test
    void toStringIndicatesNoneWhenStatisticsEmpty() {
        MessageLoaderStatus s = new MessageLoaderStatus();
        assertTrue(s.toString().contains("none"));
    }

    @Test
    void resultCodeConstantsHaveExpectedValues() {
        assertEquals(-99, MessageLoaderStatus.UNKNOWN);
        assertEquals(-1, MessageLoaderStatus.ERROR);
        assertEquals(0, MessageLoaderStatus.WARN);
        assertEquals(1, MessageLoaderStatus.SUCCESS);
    }
}
