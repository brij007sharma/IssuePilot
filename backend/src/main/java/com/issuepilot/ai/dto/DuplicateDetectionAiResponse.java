
package com.issuepilot.ai.dto;

import java.util.List;

public record DuplicateDetectionAiResponse(
        List<DuplicateIssueMatch> matches,
        double threshold,
        String model
) {}
