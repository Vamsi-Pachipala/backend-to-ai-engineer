package llm_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import llm_api.DTO.OpenAIResponse;
import llm_api.DTO.ResumeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
@Service
public class ChatService {

    private final RestClient restClient;
    private final String apiKey;

    public ChatService(@Value("${openai.api-key}") String apiKey) {

        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com")
                .build();
    }

    public String chat(String message) {

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-6-luna",
                "input", message
        );

        OpenAIResponse openAIResponse =  restClient.post()
                .uri("/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(OpenAIResponse.class);

        return openAIResponse.getOutput().stream()
                .flatMap(outputItem -> outputItem.getContent().stream())
                .filter(contentItem -> "output_text".equals(contentItem.getType()))
                .map(OpenAIResponse.ContentItem::getText)
                .findFirst()
                .orElse("No response from OpenAI.");
    }

    public ResumeResponse analyzeResume(String resume) {

        String prompt = buildResumePrompt(resume);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", "gpt-6-luna");
        requestBody.put("input", prompt);

        OpenAIResponse openAIResponse = restClient.post()
                .uri("/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(OpenAIResponse.class);

        String json = openAIResponse.getOutput().stream()
                .flatMap(outputItem -> outputItem.getContent().stream())
                .filter(contentItem -> "output_text".equals(contentItem.getType()))
                .map(OpenAIResponse.ContentItem::getText)
                .findFirst()
                .orElse("{}");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ResumeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }

    private String buildResumePrompt(String resume) {

        return """
            ROLE:
            You are a senior technical recruiter.

            TASK:
            Analyze the following resume and extract:
            1. Technical skills
            2. Work experience
            3. Education
            4. A short professional summary

            CONTEXT:
            Resume:
            %s

            CONSTRAINTS:
            - Extract only information explicitly mentioned in the resume.
            - Do not invent or infer information.
            - Avoid duplicate skills.
            - Keep the summary concise.

            OUTPUT:
            Return only valid JSON in this format:

            {
              "skills": [],
              "experience": [],
              "education": [],
              "summary": ""
            }
            """.formatted(resume);
    }
}