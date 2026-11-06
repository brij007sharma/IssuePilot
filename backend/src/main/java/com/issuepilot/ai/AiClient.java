
package com.issuepilot.ai;

import com.issuepilot.ai.dto.PriorityPredictionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

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
                .body(Map.of(
                        "title", title,
                        "description", safeDescription
                ))
                .retrieve()
                .body(PriorityPredictionResponse.class);
    }
}
