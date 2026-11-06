
package com.issuepilot.ai.dto;

public record DuplicateIssueMatch(
        Long issueId,
        String title,
        String description,
        double similarity
) {}
