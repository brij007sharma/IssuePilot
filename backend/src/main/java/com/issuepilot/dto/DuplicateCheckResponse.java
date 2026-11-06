
package com.issuepilot.dto;

import com.issuepilot.ai.dto.DuplicateIssueMatch;

import java.util.List;

public record DuplicateCheckResponse(
        boolean hasDuplicates,
        List<DuplicateIssueMatch> matches,
        double threshold,
        String model
) {}
