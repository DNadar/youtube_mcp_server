package com.example.ytmcp.model;

import java.util.List;

public class ToolDefinition {
    private String name;
    private String description;
    private List<ToolParameter> parameters;

    public ToolDefinition(String name, String description, List<ToolParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ToolParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ToolParameter> parameters) {
        this.parameters = parameters;
    }
}
