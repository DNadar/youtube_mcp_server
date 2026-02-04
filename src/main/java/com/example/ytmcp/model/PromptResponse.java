package com.example.ytmcp.model;

public class PromptResponse {
    private String promptName;
    private String content;

    public PromptResponse(String promptName, String content) {
        this.promptName = promptName;
        this.content = content;
    }

    public String getPromptName() {
        return promptName;
    }

    public void setPromptName(String promptName) {
        this.promptName = promptName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
