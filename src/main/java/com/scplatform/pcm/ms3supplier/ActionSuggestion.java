/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

/**
 * requiresHumanApproval is always true and has no setter. Real procurement
 * relationships and money are involved -- this system recommends, a
 * person decides, and that is the correct default rather than a
 * limitation to relax later.
 */
public class ActionSuggestion {
    private final String type;
    private final String priority;
    private final String description;
    private final boolean requiresHumanApproval = true;

    public ActionSuggestion(String type, String priority, String description) {
        this.type = type;
        this.priority = priority;
        this.description = description;
    }

    public String getType() { return type; }
    public String getPriority() { return priority; }
    public String getDescription() { return description; }
    public boolean isRequiresHumanApproval() { return requiresHumanApproval; }
}
