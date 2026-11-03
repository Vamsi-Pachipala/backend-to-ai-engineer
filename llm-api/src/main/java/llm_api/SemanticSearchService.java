package llm_api;

import ai.djl.translate.TranslateException;
import llm_api.DTO.Document;
import llm_api.DTO.SearchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class SemanticSearchService {

    private final EmbeddingService embeddingService;

    private final VectorStore vectorStore;

    public SemanticSearchService(EmbeddingService embeddingService , VectorStore vectorStore) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
    }

    public String search(String query) throws TranslateException {

        Map<String, String> documents = new LinkedHashMap<>();

        documents.put("doc1",
                "Java Spring Boot backend developer");
        documents.put("doc2",
                "Python Django software engineer");
        documents.put("doc3",
                "React frontend developer");

        // Create embedding for query
        float[] queryEmbedding =
                embeddingService.createEmbedding(query);

        String bestDocument = null;
        double bestScore = -1;

        // Compare query with every document
        for (String document : documents.values()) {

            float[] documentEmbedding =
                    embeddingService.createEmbedding(document);

            double score =
                    embeddingService.cosineSimilarity(
                            queryEmbedding,
                            documentEmbedding
                    );

            System.out.println(
                    document + " -> " + score
            );

            if (score > bestScore) {
                bestScore = score;
                bestDocument = document;
            }
        }

        return bestDocument;
    }

    public List<SearchResult> searchTopK(String query, int topK)
            throws TranslateException {

        // 1. Convert query into embedding
        float[] queryEmbedding =
                embeddingService.createEmbedding(query);

        List<SearchResult> results = new ArrayList<>();

        // 2. Compare query with every stored document
        for (Document document :vectorStore.getAll()) {

            double score =
                    embeddingService.cosineSimilarity(
                            queryEmbedding,
                            document.getEmbedding()
                    );

            results.add(
                    new SearchResult(document, score)
            );
        }

        // 3. Sort highest similarity first
        results.sort(
                (a, b) -> Double.compare(
                        b.getScore(),
                        a.getScore()
                )
        );

        // 4. Return Top-K
        return results.subList(
                0,
                Math.min(topK, results.size())
        );
    }

}