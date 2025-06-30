package com.email.email_writer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.email_writer.models.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {

  private final WebClient webClient;
  private final String geminiApiKey;

  public EmailGeneratorService(WebClient.Builder webClientBuilder,
      @Value("${gemini.api.key}") String geminiApiKey) {
    this.webClient = webClientBuilder
        .baseUrl("https://generativelanguage.googleapis.com/v1beta")
        .defaultHeader("Content-Type", "application/json")
        .build();
    this.geminiApiKey = geminiApiKey;
  }

  public String generateEmailReply(EmailRequest emailRequest) {
    String prompt = buildPrompt(emailRequest);

    Map<String, Object> requestBody = Map.of(
        "contents", new Object[] {
            Map.of("parts", new Object[] {
                Map.of("text", prompt)
            })
        });

    try {
      String response = webClient.post()
          .uri(uriBuilder -> uriBuilder
              .path("/models/gemini-2.0-flash:generateContent")
              .queryParam("key", geminiApiKey)
              .build())
          .bodyValue(requestBody)
          .retrieve()
          .bodyToMono(String.class)
          .block();

      return extractResponseContent(response);

    } catch (Exception e) {
      return "Error generating reply: " + e.getMessage();
    }
  }

  private String extractResponseContent(String response) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(response);
      return rootNode.path("candidates")
          .get(0)
          .path("content")
          .path("parts")
          .get(0)
          .path("text")
          .asText();
    } catch (Exception e) {
      return "Error parsing response: " + e.getMessage();
    }
  }

  private String buildPrompt(EmailRequest emailRequest) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("You are an email writer. Write a professional reply to the following email:\n");
    if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
      prompt.append("Tone: ").append(emailRequest.getTone()).append("\n");
    }
    prompt.append("Email Content:\n").append(emailRequest.getEmailContent());
    return prompt.toString();
  }
}
