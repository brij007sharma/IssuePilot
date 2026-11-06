package com.issuepilot.ai;

import com.issuepilot.ai.dto.PriorityPredictionResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiTestController {

    private final AiClient aiClient;

    public AiTestController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @PostMapping("/test-priority")
    public PriorityPredictionResponse testPriority(
            @RequestParam String title,
            @RequestParam String description
    ) {
        return aiClient.predictPriority(title, description);
    }
}