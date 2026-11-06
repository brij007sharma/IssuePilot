
package com.issuepilot.ai;

import com.issuepilot.ai.dto.DuplicateDetectionAiRequest;
import com.issuepilot.ai.dto.DuplicateDetectionAiResponse;
import com.issuepilot.ai.dto.PriorityPredictionResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiClient {

    private final RestClient restClient;

    public AiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public PriorityPredictionResponse predictPriority(
            String title,
            String description
    ) {

        String safeDescription =
                description == null ? "" : description;

        return restClient.post()
                .uri("/api/ai/predict-priority")
                .body(new IssuePriorityRequest(title, safeDescription))
                .retrieve()
                .body(PriorityPredictionResponse.class);
    }

    public DuplicateDetectionAiResponse detectDuplicates(
            DuplicateDetectionAiRequest request
    ) {

        return restClient.post()
                .uri("/api/ai/detect-duplicates")
                .body(request)
                .retrieve()
                .body(DuplicateDetectionAiResponse.class);
    }

    private record IssuePriorityRequest(
            String title,
            String description
    ) {}
}
