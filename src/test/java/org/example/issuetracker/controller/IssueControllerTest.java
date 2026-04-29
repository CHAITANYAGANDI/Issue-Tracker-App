package org.example.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.issuetracker.dto.IssueRequestDTO;
import org.example.issuetracker.dto.IssueResponseDTO;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.example.issuetracker.exception.GlobalExceptionHandler;
import org.example.issuetracker.exception.IssueNotFoundException;
import org.example.issuetracker.service.IssueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IssueController.class)
@Import(GlobalExceptionHandler.class)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IssueService issueService;

//    @MockitoBean
//    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void createIssue_WhenValidRequest_ShouldReturnCreatedIssue() throws Exception {
        IssueRequestDTO requestDTO = new IssueRequestDTO();
        requestDTO.setTitle("Login bug");
        requestDTO.setDescription("User cannot log in");
        requestDTO.setStatus(IssueStatus.OPEN);
        requestDTO.setPriority(IssuePriority.HIGH);
        requestDTO.setDueDate(LocalDate.of(2026, 5, 5));
        requestDTO.setAssignee("Sai");
        requestDTO.setReporter("Admin");

        IssueResponseDTO responseDTO = new IssueResponseDTO(
                1L,
                "Login bug",
                "User cannot log in",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Sai",
                "Admin"

        );

        when(issueService.createIssue(any(IssueRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/issues")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Login bug"))
                .andExpect(jsonPath("$.description").value("User cannot log in"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value("2026-05-05"))
                .andExpect(jsonPath("$.assignee").value("Sai"))
                .andExpect(jsonPath("$.reporter").value("Admin"));

        verify(issueService, times(1)).createIssue(any(IssueRequestDTO.class));
    }

    @Test
    void createIssue_WhenTitleIsBlank_ShouldReturnBadRequest() throws Exception {
        IssueRequestDTO requestDTO = new IssueRequestDTO();
        requestDTO.setTitle("");
        requestDTO.setDescription("User cannot log in");
        requestDTO.setStatus(IssueStatus.OPEN);
        requestDTO.setPriority(IssuePriority.HIGH);

        mockMvc.perform(post("/api/v1/issues")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));

        verify(issueService, never()).createIssue(any(IssueRequestDTO.class));
    }

    @Test
    void getIssueById_WhenIssueExists_ShouldReturnIssue() throws Exception {
        IssueResponseDTO responseDTO = new IssueResponseDTO(
                1L,
                "Payment bug",
                "Payment fails when using credit card",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 10),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Sai",
                "Admin"
        );

        when(issueService.getIssueById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/issues/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Payment bug"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(issueService, times(1)).getIssueById(1L);
    }

    @Test
    void getIssueById_WhenIssueDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(issueService.getIssueById(100L))
                .thenThrow(new IssueNotFoundException("Issue not found with id: 100"));

        mockMvc.perform(get("/api/v1/issues/{id}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Issue not found with id: 100"))
                .andExpect(jsonPath("$.path").value("/api/v1/issues/100"));

        verify(issueService, times(1)).getIssueById(100L);
    }

    @Test
    void updateIssueStatus_WhenValidRequest_ShouldReturnUpdatedIssue() throws Exception {
        IssueResponseDTO responseDTO = new IssueResponseDTO(
                1L,
                "Login bug",
                "User cannot log in",
                IssueStatus.RESOLVED,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Sai",
                "Admin"
        );

        when(issueService.updateIssueStatusById(1L, IssueStatus.RESOLVED)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/issues/{id}/status", 1L)
                        .param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(issueService, times(1)).updateIssueStatusById(1L, IssueStatus.RESOLVED);
    }

    @Test
    void getAllIssues_WhenInvalidSortField_ShouldReturnBadRequest() throws Exception {
        when(issueService.getAllIssuesWithPagination(0, 5, "randomField", "asc"))
                .thenThrow(new IllegalArgumentException(
                        "Invalid sort field: randomField. Allowed fields are: [id, title, status, priority]"
                ));

        mockMvc.perform(get("/api/v1/issues")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "randomField")
                        .param("sortDir", "asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(
                        "Invalid sort field: randomField. Allowed fields are: [id, title, status, priority]"
                ));

        verify(issueService, times(1))
                .getAllIssuesWithPagination(0, 5, "randomField", "asc");
    }

    @Test
    void updateIssue_WhenValidRequest_ShouldReturnUpdatedIssue() throws Exception {
        IssueRequestDTO requestDTO = new IssueRequestDTO();
        requestDTO.setTitle("Updated title");
        requestDTO.setDescription("Updated description");
        requestDTO.setStatus(IssueStatus.IN_PROGRESS);
        requestDTO.setPriority(IssuePriority.MEDIUM);
        requestDTO.setDueDate(LocalDate.of(2026, 5, 10));
        requestDTO.setAssignee("Sai");
        requestDTO.setReporter("Admin");

        IssueResponseDTO responseDTO = new IssueResponseDTO(
                1L,
                "Updated title",
                "Updated description",
                IssueStatus.IN_PROGRESS,
                IssuePriority.MEDIUM,
                LocalDate.of(2026, 5, 10),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Sai",
                "Admin"
        );

        when(issueService.updateIssueById(eq(1L), any(IssueRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/issues/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.assignee").value("Sai"));

        verify(issueService, times(1))
                .updateIssueById(eq(1L), any(IssueRequestDTO.class));
    }


    @Test
    void deleteIssue_WhenIssueExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(issueService).deleteIssueById(1L);

        mockMvc.perform(delete("/api/v1/issues/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(issueService, times(1)).deleteIssueById(1L);
    }


    @Test
    void deleteIssue_WhenIssueDoesNotExist_ShouldReturnNotFound() throws Exception {
        doThrow(new IssueNotFoundException("Issue not found with id: 100"))
                .when(issueService)
                .deleteIssueById(100L);

        mockMvc.perform(delete("/api/v1/issues/{id}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Issue not found with id: 100"));

        verify(issueService, times(1)).deleteIssueById(100L);
    }

    @Test
    void updateIssuePriority_WhenValidRequest_ShouldReturnUpdatedIssue() throws Exception {
        IssueResponseDTO responseDTO = new IssueResponseDTO(
                1L,
                "Login bug",
                "User cannot log in",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Sai",
                "Admin"
        );

        when(issueService.updateIssuePriorityById(1L, IssuePriority.HIGH))
                .thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/issues/{id}/priority", 1L)
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        verify(issueService, times(1))
                .updateIssuePriorityById(1L, IssuePriority.HIGH);
    }


    @Test
    void updateIssueStatus_WhenInvalidEnum_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/issues/{id}/status", 1L)
                        .param("status", "DONE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(
                        "Invalid value for parameter 'status': DONE. Allowed values are: [OPEN, IN_PROGRESS, RESOLVED]"
                ));

        verify(issueService, never())
                .updateIssueStatusById(anyLong(), any(IssueStatus.class));
    }


    @Test
    void updateIssueStatus_WhenStatusParameterMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/issues/{id}/status", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Missing required parameter: status"));

        verify(issueService, never())
                .updateIssueStatusById(anyLong(), any(IssueStatus.class));
    }

    @Test
    void filterIssues_WhenValidParams_ShouldReturnFilteredIssues() throws Exception {
        IssueResponseDTO responseDTO = new IssueResponseDTO(
                1L,
                "Filter test",
                "Testing filters",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Sai",
                "Admin"
        );

        when(issueService.filterIssues(IssueStatus.OPEN, IssuePriority.HIGH, "Sai"))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/issues/filter")
                        .param("status", "OPEN")
                        .param("priority", "HIGH")
                        .param("assignee", "Sai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Filter test"))
                .andExpect(jsonPath("$[0].status").value("OPEN"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].assignee").value("Sai"));

        verify(issueService, times(1))
                .filterIssues(IssueStatus.OPEN, IssuePriority.HIGH, "Sai");
    }
}