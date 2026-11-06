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
import io.qdrant.client.grpc.Points.QueryPoints;
import io.qdrant.client.grpc.Points.ScoredPoint;

import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;
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

    public String search(String query, int topK)
            throws Exception {

        // 1. Generate embedding for user query
        float[] queryEmbedding =
                embeddingService.createEmbedding(query);

        // 2. Search in Qdrant
        List<ScoredPoint> results =
                qdrantClient.queryAsync(
                        QueryPoints.newBuilder()
                                .setCollectionName("documents")
                                .setQuery(nearest(queryEmbedding))
                                .setLimit(topK)
                                .setWithPayload(enable(true))
                                .build()
                ).get();


        System.out.println("Qdrant search results for query: " + query);

        StringBuilder result = new StringBuilder();
        for (ScoredPoint point : results) {
            String text = point.getPayloadOrThrow("text").getStringValue();
            String role = point.getPayloadOrThrow("role").getStringValue();
            String location = point.getPayloadOrThrow("location").getStringValue();
            float score = point.getScore();

            result.append("Text: ")
                    .append(text)
                    .append(", Role: ")
                    .append(role).append(", Location: ")
                    .append(location).append(", Score: ")
                    .append(score).append("\n");
        }

        return result.toString();
    }


}