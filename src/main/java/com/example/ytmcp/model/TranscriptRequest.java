package com.example.ytmcp.model;

import jakarta.validation.constraints.NotBlank;

public class TranscriptRequest {

    @NotBlank(message = "url is required")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
