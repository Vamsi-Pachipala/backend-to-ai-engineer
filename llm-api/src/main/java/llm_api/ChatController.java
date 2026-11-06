package llm_api;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import io.qdrant.client.grpc.Points;

import llm_api.DTO.ChatRequest;
import llm_api.DTO.ResumeRequest;
import llm_api.DTO.ResumeResponse;
import llm_api.DTO.SearchResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    private final EmbeddingService embeddingService;

    private final SemanticSearchService semanticSearchService;

    private final QdrantService qdrantService;

    public ChatController(ChatService chatService , EmbeddingService embeddingService , SemanticSearchService semanticSearchService, QdrantService qdrantService) {
        this.chatService = chatService;
        this.embeddingService = embeddingService;
        this.semanticSearchService = semanticSearchService;
        this.qdrantService = qdrantService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatRequest> chat(@RequestBody ChatRequest request) {
        String response =  chatService.chat(request.getMessage());
        return ResponseEntity.ok(new ChatRequest(response));
    }

    @PostMapping("/analyzeResume")
    public String analyzeResume(@RequestBody ResumeRequest request) {
        return chatService.analyzeResume(request.getResumeText());
    }

    @PostMapping("/generateEmbedding")
    public String generateEmbedding(@RequestBody String text) throws TranslateException {
        float[] response = embeddingService.createEmbedding(text);
        return "Embedding generated with dimensions: " + Arrays.toString(response);
    }

    @PostMapping("/cosineSimilarity")
    public String cosineSimilarity(@RequestBody ResumeRequest request) throws TranslateException {
        float[] embedding1 = embeddingService.createEmbedding("Java backend developer");
        float[] embedding2 = embeddingService.createEmbedding("I love eating pizza");
        double similarity = embeddingService.cosineSimilarity(embedding1, embedding2);
        return "Cosine similarity: " + similarity;
    }

    @PostMapping("/semanticSearch")
    public String semanticSearch(@RequestBody ResumeRequest request) throws TranslateException {
        return semanticSearchService.search(request.getResumeText());
    }

    @PostMapping("/semanticSearch-top-k")
    public List<SearchResult> semanticSearchTopK(@RequestBody ResumeRequest request) throws TranslateException {
        return semanticSearchService.searchTopK(request.getResumeText(), 3);
    }

    @PostMapping("/qdrant/search-top-k")
    public String qdrantSearchTopK(@RequestBody ResumeRequest request) throws TranslateException {
        try {
            return qdrantService.search(request.getResumeText(), 3);
        }catch (Exception e) {
            throw new RuntimeException("Error during Qdrant search", e);
        }
    }

}