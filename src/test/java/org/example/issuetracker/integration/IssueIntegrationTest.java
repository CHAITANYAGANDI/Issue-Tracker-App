package org.example.issuetracker.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.issuetracker.repository.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class IssueIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IssueRepository issueRepository;

    @BeforeEach
    void setUp() {
        issueRepository.deleteAll();
    }

    @Test
    void createIssue_ThenGetIssueById_ShouldWorkEndToEnd() throws Exception {
        String requestBody = """
                {
                  "title": "Integration test issue",
                  "description": "Testing full flow with PostgreSQL Testcontainer",
                  "status": "OPEN",
                  "priority": "HIGH",
                  "dueDate": "2026-05-05",
                  "assignee": "Sai",
                  "reporter": "Admin"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration test issue"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.assignee").value("Sai"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long issueId = jsonNode.get("id").asLong();

        mockMvc.perform(get("/api/v1/issues/{id}", issueId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(issueId))
                .andExpect(jsonPath("$.title").value("Integration test issue"))
                .andExpect(jsonPath("$.description").value("Testing full flow with PostgreSQL Testcontainer"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value("2026-05-05"))
                .andExpect(jsonPath("$.assignee").value("Sai"))
                .andExpect(jsonPath("$.reporter").value("Admin"));
    }

    @Test
    void createIssue_WhenTitleIsBlank_ShouldReturnBadRequest() throws Exception {
        String requestBody = """
                {
                  "title": "",
                  "description": "Description is valid",
                  "status": "OPEN",
                  "priority": "HIGH"
                }
                """;

        mockMvc.perform(post("/api/v1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));
    }

    @Test
    void createIssue_ThenPatchStatus_ShouldUpdateStatus() throws Exception {
        Long issueId = createTestIssueAndReturnId();

        mockMvc.perform(patch("/api/v1/issues/{id}/status", issueId)
                        .param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(issueId))
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    void createIssue_ThenDeleteIssue_ShouldReturnNoContentAndThenNotFound() throws Exception {
        Long issueId = createTestIssueAndReturnId();

        mockMvc.perform(delete("/api/v1/issues/{id}", issueId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/issues/{id}", issueId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Issue not found with id: " + issueId));
    }

    @Test
    void filterIssues_WhenStatusPriorityAndAssigneeMatch_ShouldReturnFilteredIssues() throws Exception {
        createIssue("Login bug", "Login problem", "OPEN", "HIGH", "Sai");
        createIssue("Payment bug", "Payment problem", "OPEN", "HIGH", "John");
        createIssue("UI bug", "UI problem", "RESOLVED", "LOW", "Sai");

        mockMvc.perform(get("/api/v1/issues/filter")
                        .param("status", "OPEN")
                        .param("priority", "HIGH")
                        .param("assignee", "Sai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Login bug"))
                .andExpect(jsonPath("$[0].status").value("OPEN"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].assignee").value("Sai"));
    }

    private Long createTestIssueAndReturnId() throws Exception {
        String requestBody = """
                {
                  "title": "Test issue",
                  "description": "Test description",
                  "status": "OPEN",
                  "priority": "HIGH",
                  "dueDate": "2026-05-05",
                  "assignee": "Sai",
                  "reporter": "Admin"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/v1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("id").asLong();
    }

    private void createIssue(
            String title,
            String description,
            String status,
            String priority,
            String assignee
    ) throws Exception {
        String requestBody = """
                {
                  "title": "%s",
                  "description": "%s",
                  "status": "%s",
                  "priority": "%s",
                  "dueDate": "2026-05-05",
                  "assignee": "%s",
                  "reporter": "Admin"
                }
                """.formatted(title, description, status, priority, assignee);

        mockMvc.perform(post("/api/v1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }
}