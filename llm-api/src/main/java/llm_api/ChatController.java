package llm_api;

import llm_api.DTO.ChatRequest;
import llm_api.DTO.ResumeRequest;
import llm_api.DTO.ResumeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatRequest> chat(@RequestBody ChatRequest request) {
        String response =  chatService.chat(request.getMessage());
        return ResponseEntity.ok(new ChatRequest(response));
    }

    @PostMapping("/analyzeResume")
    public ResponseEntity<ResumeResponse> analyzeResume(@RequestBody ResumeRequest request) {
        ResumeResponse response = chatService.analyzeResume(request.getResumeText());
        return ResponseEntity.ok(response);
    }
}