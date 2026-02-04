# YouTube MCP Server (Spring Boot)

Java/Spring Boot implementation of the remote YouTube MCP server. It mirrors the Python version by exposing MCP-style metadata plus two tools:
- `fetch_video_transcript` – fetch and format a YouTube transcript with timestamps.
- `fetch_instructions` – return prompt templates from the bundled `prompts/` directory.

## Quick start
1) **Prerequisites:** Java 17+, Maven, Auth0 tenant for issuing JWTs, and (optionally) an HTTP/HTTPS proxy for YouTube.
2) **Configure environment:** set `AUTH0_DOMAIN`, `AUTH0_AUDIENCE`, `RESOURCE_SERVER_URL`, and optionally `PROXY_USERNAME`, `PROXY_PASSWORD`, `PROXY_URL` (host:port). Example in `.env.example`.
3) **Build:** `mvn -f yt-mcp-server-java/pom.xml clean package`
4) **Run:** `mvn -f yt-mcp-server-java/pom.xml spring-boot:run` (or `java -jar target/yt-mcp-server-0.0.1-SNAPSHOT.jar`)
5) **Call endpoints (JWT required):**
   - `GET http://localhost:8000/mcp` – metadata + server instructions + tool definitions
   - `POST http://localhost:8000/mcp/tools/fetch_video_transcript` with body `{"url": "https://www.youtube.com/watch?v=VIDEO_ID"}`
   - `GET http://localhost:8000/mcp/tools/fetch_instructions/{promptName}` (e.g., `write_blog_post`)

All requests must include `Authorization: Bearer <Auth0 JWT>`. Health check is available at `/actuator/health` without auth.

## Notes
- Transcript retrieval uses YouTube's timedtext endpoint and respects the optional proxy settings; responses are formatted as `[MM:SS] text` per line.
- Prompts live under `src/main/resources/prompts`; `server_instructions.md` is served in the MCP metadata response.
- MCP metadata includes `resourceServerUrl` so clients can discover the externally reachable endpoint you configured.
