package llm_api.DTO;
import java.util.List;

public class OpenAIResponse {

    private List<OutputItem> output;

    public List<OutputItem> getOutput() {
        return output;
    }

    public void setOutput(List<OutputItem> output) {
        this.output = output;
    }

    public static class OutputItem {
        private String type;
        private List<ContentItem> content;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<ContentItem> getContent() {
            return content;
        }

        public void setContent(List<ContentItem> content) {
            this.content = content;
        }
    }

    public static class ContentItem {
        private String type;
        private String text;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}