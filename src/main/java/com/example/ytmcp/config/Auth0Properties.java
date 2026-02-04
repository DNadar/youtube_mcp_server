package com.example.ytmcp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {

    /**
     * Auth0 tenant domain (e.g., dev-abc123.us.auth0.com).
     */
    private String domain;

    /**
     * Auth0 audience / API identifier.
     */
    private String audience;

    /**
     * Public MCP endpoint URL, used for docs and metadata.
     */
    private String resourceServerUrl;

    @PostConstruct
    void validate() {
        if (!StringUtils.hasText(domain) || isPlaceholder(domain)) {
            throw new IllegalStateException("AUTH0_DOMAIN environment variable is required (auth0.domain)");
        }
        if (!StringUtils.hasText(audience) || isPlaceholder(audience)) {
            throw new IllegalStateException("AUTH0_AUDIENCE environment variable is required (auth0.audience)");
        }
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getResourceServerUrl() {
        return resourceServerUrl;
    }

    public void setResourceServerUrl(String resourceServerUrl) {
        this.resourceServerUrl = resourceServerUrl;
    }

    public String issuer() {
        return "https://" + domain + "/";
    }

    private boolean isPlaceholder(String value) {
        return value.contains("${") || value.contains("}");
    }
}
