package llm_api;

import io.qdrant.client.QdrantClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.JsonWithInt.Value;
import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

import java.util.*;

@Service
public class QdrantService {

    private final QdrantClient qdrantClient;
    private final EmbeddingService embeddingService;

    public QdrantService(
            QdrantClient qdrantClient,
            EmbeddingService embeddingService
    ) {
        this.qdrantClient = qdrantClient;
        this.embeddingService = embeddingService;
    }

    public void checkConnection() {
        try {
            var collections = qdrantClient.listCollectionsAsync().get();

            System.out.println("Connected to Qdrant!");
            System.out.println("Collections: " + collections);

        } catch (Exception e) {
            throw new RuntimeException("Qdrant connection failed", e);
        }
    }

    public void insertDocument(
            String id,
            String text,
            String role,
            String location
    ) throws Exception {

        // 1. Generate embedding
        float[] embedding =
                embeddingService.createEmbedding(text);

        // 2. Convert float[] to List<Float>
        List<Float> vector = new ArrayList<>();

        for (float value : embedding) {
            vector.add(value);
        }

        // 3. Create payload
        Map<String, Value> payload = new HashMap<>();

        payload.put("text", Value.newBuilder().setStringValue(text).build());
        payload.put("role", Value.newBuilder().setStringValue(role).build());
        payload.put("location", Value.newBuilder().setStringValue(location).build());

        PointStruct point = PointStruct.newBuilder()
                .setId(id(Long.parseLong(id)))
                .setVectors(vectors(vector))
                .putAllPayload(Map.of(
                        "text", value(text),
                        "role", value(role),
                        "location", value(location)
                ))
                .build();

        qdrantClient.upsertAsync(
                "documents",
                List.of(point)
        ).get();

        System.out.println("Document inserted: " + text);
    }


}