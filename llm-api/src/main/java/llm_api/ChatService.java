package llm_api;

import llm_api.DTO.EmbeddingResponse;
import tools.jackson.databind.json.JsonMapper;
import llm_api.DTO.OpenAIResponse;
import llm_api.DTO.ResumeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ChatService {

    private final RestClient restClient;
    private final String apiKey;

    public ChatService(@Value("${groq.api-key}") String apiKey) {

        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai")
                .build();
    }

    public String chat(String message) {

        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-120b",
                "input", message
        );

        long start = System.currentTimeMillis();

        OpenAIResponse openAIResponse =  restClient.post()
                .uri("/v1/responses")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(OpenAIResponse.class);

        long end = System.currentTimeMillis();
        System.out.println("OpenAI API call duration: " + (end - start) + " ms");

        int inputTokens = openAIResponse.getUsage().getInputTokens();
        int outputTokens = openAIResponse.getUsage().getOutputTokens();
        int totalTokens = openAIResponse.getUsage().getTotalTokens();

        System.out.println("Input tokens : " + inputTokens);
        System.out.println("Output tokens: " + outputTokens);
        System.out.println("Total tokens : " + totalTokens);

        double inputCost = (inputTokens / 1_000_000.0) * 0.10;
        double outputCost = (outputTokens / 1_000_000.0) * 0.50;

        double totalCost = inputCost + outputCost;

        System.out.println("Input cost   : $" + inputCost);
        System.out.println("Output cost  : $" + outputCost);
        System.out.println("Total cost   : $" + totalCost);

        return openAIResponse.getOutput().stream()
                .flatMap(outputItem -> outputItem.getContent().stream())
                .filter(contentItem -> "output_text".equals(contentItem.getType()))
                .map(OpenAIResponse.ContentItem::getText)
                .findFirst()
                .orElse("No response from OpenAI.");
    }

    public String analyzeResume(String resume) {

        String prompt = buildResumePrompt(resume);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", "openai/gpt-oss-120b");
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

        return json;
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

    public String createEmbedding(String text) {

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", "text-embedding-3-small");
        requestBody.put("input", text);

        String response = restClient.post()
                .uri("/v1/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return response;
    }
}