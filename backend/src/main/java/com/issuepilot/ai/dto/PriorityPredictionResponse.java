package com.issuepilot.ai.dto;

public record PriorityPredictionResponse(
        String predictedPriority,
        Double confidence,
        String model
) {
}