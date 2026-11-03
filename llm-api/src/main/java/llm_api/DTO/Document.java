package llm_api.DTO;
public class Document {

    private String id;
    private String text;
    private String role;
    private String location;
    private float[] embedding;

    public Document(String id, String text, String role, String location) {
        this.id = id;
        this.text = text;
        this.role = role;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getRole() {
        return role;
    }

    public String getLocation() {
        return location;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}