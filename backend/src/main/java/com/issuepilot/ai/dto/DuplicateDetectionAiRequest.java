
package com.issuepilot.ai.dto;

import java.util.List;

public record DuplicateDetectionAiRequest(
        String title,
        String description,
        List<DuplicateIssueCandidate> existingIssues,
        double threshold,
        int maxResults
) {}
