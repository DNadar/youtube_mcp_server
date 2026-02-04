package com.example.ytmcp.controller;

import com.example.ytmcp.config.Auth0Properties;
import com.example.ytmcp.model.McpMetadataResponse;
import com.example.ytmcp.model.PromptResponse;
import com.example.ytmcp.model.ToolDefinition;
import com.example.ytmcp.model.ToolParameter;
import com.example.ytmcp.model.TranscriptRequest;
import com.example.ytmcp.model.TranscriptResponse;
import com.example.ytmcp.service.PromptService;
import com.example.ytmcp.service.TranscriptService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mcp", produces = MediaType.APPLICATION_JSON_VALUE)
public class McpController {

    private final PromptService promptService;
    private final TranscriptService transcriptService;
    private final Auth0Properties auth0Properties;

    public McpController(PromptService promptService, TranscriptService transcriptService, Auth0Properties auth0Properties) {
        this.promptService = promptService;
        this.transcriptService = transcriptService;
        this.auth0Properties = auth0Properties;
    }

    @GetMapping
    public McpMetadataResponse metadata() {
        List<ToolDefinition> tools = List.of(
            new ToolDefinition(
                "fetch_video_transcript",
                "Extract transcript with timestamps from a YouTube video URL",
                List.of(new ToolParameter("url", "string", "YouTube video URL", true))
            ),
            new ToolDefinition(
                "fetch_instructions",
                "Load a prompt template (write_blog_post, write_social_post, write_video_chapters)",
                List.of(new ToolParameter("prompt_name", "string", "Name of prompt file without extension", true))
            )
        );

        String instructions = promptService.loadServerInstructions();
        return new McpMetadataResponse("yt-mcp", instructions, auth0Properties.getResourceServerUrl(), tools);
    }

    @PostMapping("/tools/fetch_video_transcript")
    public TranscriptResponse fetchVideoTranscript(@Valid @RequestBody TranscriptRequest request) {
        String transcript = transcriptService.fetchTranscript(request.getUrl());
        return new TranscriptResponse(transcript);
    }

    @GetMapping("/tools/fetch_instructions/{promptName}")
    public PromptResponse fetchPrompt(@PathVariable String promptName) {
        String prompt = promptService.loadPrompt(promptName);
        return new PromptResponse(promptName, prompt);
    }
}
