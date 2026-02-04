package com.example.ytmcp.model;

import java.util.List;

public class McpMetadataResponse {
    private String name;
    private String instructions;
    private String resourceServerUrl;
    private List<ToolDefinition> tools;

    public McpMetadataResponse(String name, String instructions, String resourceServerUrl, List<ToolDefinition> tools) {
        this.name = name;
        this.instructions = instructions;
        this.resourceServerUrl = resourceServerUrl;
        this.tools = tools;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getResourceServerUrl() {
        return resourceServerUrl;
    }

    public void setResourceServerUrl(String resourceServerUrl) {
        this.resourceServerUrl = resourceServerUrl;
    }

    public List<ToolDefinition> getTools() {
        return tools;
    }

    public void setTools(List<ToolDefinition> tools) {
        this.tools = tools;
    }
}
