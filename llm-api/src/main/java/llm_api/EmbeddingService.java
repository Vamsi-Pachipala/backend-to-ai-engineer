package llm_api;

import ai.djl.ModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Arrays;


@Service
public class EmbeddingService {

    private ZooModel<String, float[]> model;
    private Predictor<String, float[]> predictor;

    @PostConstruct
    public void initialize() {
        try {
            loadModel();
        } catch (IOException | ModelException e) {
            throw new IllegalStateException(
                    "Failed to load embedding model", e
            );
        }
    }

    public void loadModel() throws IOException, ModelException {

        Criteria<String, float[]> criteria = Criteria.builder()
                .setTypes(String.class, float[].class)
                .optModelUrls(
                        "djl://ai.djl.huggingface.pytorch/sentence-transformers/all-MiniLM-L6-v2")
                .optEngine("PyTorch")
                .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                .build();

        model = criteria.loadModel();
        predictor = model.newPredictor();

        System.out.println("Embedding model loaded successfully!");
    }

    public float[] createEmbedding(String text) throws TranslateException {
        return predictor.predict(text);
    }

    public double cosineSimilarity(float[] a, float[] b) {

        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Vectors must have the same dimensions"
            );
        }

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            magnitudeA += a[i] * a[i];
            magnitudeB += b[i] * b[i];
        }

        return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
    }
}