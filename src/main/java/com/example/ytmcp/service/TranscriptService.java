package com.example.ytmcp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TranscriptService {

    private static final Pattern VIDEO_ID_PATTERN = Pattern.compile("(?:v=|/)([0-9A-Za-z_-]{11}).*");
    private static final String TIMEDTEXT_URL_TEMPLATE = "https://www.youtube.com/api/timedtext?fmt=json3&lang=en&v=%s";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TranscriptService(
        @Value("${PROXY_USERNAME:}") String proxyUsername,
        @Value("${PROXY_PASSWORD:}") String proxyPassword,
        @Value("${PROXY_URL:}") String proxyUrl
    ) {
        this.httpClient = buildHttpClient(proxyUsername, proxyPassword, proxyUrl);
    }

    public String fetchTranscript(String url) {
        String videoId = extractVideoId(url);
        String transcriptUrl = TIMEDTEXT_URL_TEMPLATE.formatted(videoId);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(transcriptUrl))
            .timeout(Duration.ofSeconds(15))
            .GET()
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("YouTube returned HTTP " + response.statusCode());
            }
            return formatTranscript(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Transcript fetch interrupted", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to fetch transcript: " + e.getMessage(), e);
        }
    }

    private String extractVideoId(String url) {
        if (!StringUtils.hasText(url)) {
            throw new IllegalArgumentException("YouTube URL is required");
        }
        Matcher matcher = VIDEO_ID_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid YouTube URL");
        }
        return matcher.group(1);
    }

    private HttpClient buildHttpClient(String proxyUsername, String proxyPassword, String proxyUrl) {
        HttpClient.Builder builder = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10));

        if (StringUtils.hasText(proxyUrl)) {
            InetSocketAddress proxyAddress = parseProxy(proxyUrl);
            builder.proxy(ProxySelector.of(proxyAddress));
            if (StringUtils.hasText(proxyUsername)) {
                builder.authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                    }
                });
            }
        }

        return builder.build();
    }

    private InetSocketAddress parseProxy(String proxyUrl) {
        try {
            URI uri = proxyUrl.contains("://") ? new URI(proxyUrl) : new URI("http://" + proxyUrl);
            int port = uri.getPort() == -1 ? 80 : uri.getPort();
            return new InetSocketAddress(uri.getHost(), port);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid proxy URL", e);
        }
    }

    private String formatTranscript(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode events = root.path("events");
            if (!events.isArray()) {
                throw new IllegalStateException("Transcript format not recognized");
            }

            List<String> lines = new ArrayList<>();
            for (JsonNode event : events) {
                long startMs = event.path("tStartMs").asLong(-1);
                JsonNode segments = event.path("segs");
                if (startMs < 0 || !segments.isArray()) {
                    continue;
                }

                StringBuilder text = new StringBuilder();
                for (JsonNode segment : segments) {
                    text.append(segment.path("utf8").asText(""));
                }

                if (!StringUtils.hasText(text.toString())) {
                    continue;
                }

                lines.add("[%s] %s".formatted(formatTimestamp(startMs), text.toString().trim()));
            }

            if (lines.isEmpty()) {
                throw new IllegalStateException("No transcript entries found");
            }

            return String.join("\n", lines);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse transcript response", e);
        }
    }

    private String formatTimestamp(long startMs) {
        long totalSeconds = startMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return "%02d:%02d".formatted(minutes, seconds);
    }
}
