package com.example.ytmcp.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

@Service
public class PromptService {

    private static final String PROMPT_DIRECTORY = "prompts/";
    private static final String SERVER_INSTRUCTIONS = PROMPT_DIRECTORY + "server_instructions.md";

    public String loadServerInstructions() {
        try {
            return readResource(SERVER_INSTRUCTIONS);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load server instructions", e);
        }
    }

    public String loadPrompt(String promptName) {
        String sanitized = sanitizePromptName(promptName);
        String path = PROMPT_DIRECTORY + sanitized + ".md";
        try {
            return readResource(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Prompt not found: " + promptName);
        }
    }

    private String sanitizePromptName(String promptName) {
        if (!StringUtils.hasText(promptName)) {
            throw new IllegalArgumentException("Prompt name is required");
        }
        return promptName.toLowerCase(Locale.ROOT).trim();
    }

    private String readResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            throw new IOException("Resource not found: " + path);
        }
        try (InputStream stream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(stream);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
