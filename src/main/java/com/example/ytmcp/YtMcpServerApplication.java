package com.example.ytmcp;

import com.example.ytmcp.config.Auth0Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(Auth0Properties.class)
public class YtMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtMcpServerApplication.class, args);
    }
}
