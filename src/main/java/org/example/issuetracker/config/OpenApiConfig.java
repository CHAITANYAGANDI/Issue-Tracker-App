package org.example.issuetracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI issueTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Issue Tracker API")
                        .description("Spring Boot REST API for managing issues, status, priority, assignee, reporter, due dates, filtering, pagination, and sorting.")
                        .version("v1.0"));
    }
}