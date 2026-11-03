package llm_api.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    private Usage usage;

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public static class Usage {

        @JsonProperty("input_tokens")
        private int inputTokens;

        @JsonProperty("output_tokens")
        private int outputTokens;

        @JsonProperty("total_tokens")
        private int totalTokens;

        public int getInputTokens() {
            return inputTokens;
        }

        public void setInputTokens(int inputTokens) {
            this.inputTokens = inputTokens;
        }

        public int getOutputTokens() {
            return outputTokens;
        }

        public void setOutputTokens(int outputTokens) {
            this.outputTokens = outputTokens;
        }

        public int getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
    }
}