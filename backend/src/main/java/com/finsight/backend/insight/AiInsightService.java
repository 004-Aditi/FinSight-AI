package com.finsight.backend.insight;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AiInsightService {

    private final WebClient webClient;

    @Value("${openai.api-key}")
    private String apiKey;

    public AiInsightService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
    }

    public String enhanceInsights(List<String> insights) {

        String prompt = "You are a financial advisor. Improve these insights and make them more helpful and actionable:\n"
                + String.join("\n", insights);

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue("""
                        {
                          "model": "gpt-4o-mini",
                          "messages": [
                            {"role": "user", "content": "%s"}
                          ]
                        }
                        """.formatted(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}