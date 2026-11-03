package llm_api;

import ai.djl.translate.TranslateException;

import java.util.ArrayList;
import llm_api.DTO.Document;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VectorStore {

    private final List<Document> documents = new ArrayList<>();

    private final EmbeddingService embeddingService;

    public VectorStore(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
        try {
            loadSampleData();
        }catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDocument(
            String id,
            String text,
            String role,
            String location
    ) throws TranslateException {

        Document document =
                new Document(id, text, role, location);

        // Generate embedding
        float[] embedding =
                embeddingService.createEmbedding(text);

        document.setEmbedding(embedding);

        documents.add(document);
    }

    public List<Document> getAll() {
        return documents;
    }

    public void loadSampleData() throws TranslateException {

        addDocument(
                "1",
                "Java Spring Boot backend developer",
                "Backend",
                "Bangalore"
        );

        addDocument(
                "2",
                "Python Django backend engineer",
                "Backend",
                "Hyderabad"
        );

        addDocument(
                "3",
                "React frontend developer",
                "Frontend",
                "Bangalore"
        );

        System.out.println("Documents loaded: " + documents.size());
    }
}