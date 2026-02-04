## Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -e -DskipTests clean package

## Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
ENV AUTH0_DOMAIN=""
ENV AUTH0_AUDIENCE=""
ENV RESOURCE_SERVER_URL=""
ENV PROXY_USERNAME=""
ENV PROXY_PASSWORD=""
ENV PROXY_URL=""
EXPOSE 8000
COPY --from=builder /app/target/yt-mcp-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
