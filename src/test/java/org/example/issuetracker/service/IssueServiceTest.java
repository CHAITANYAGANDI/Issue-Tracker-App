package org.example.issuetracker.service;

import org.example.issuetracker.dto.IssueRequestDTO;
import org.example.issuetracker.dto.IssueResponseDTO;
import org.example.issuetracker.entity.Issue;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.example.issuetracker.exception.IssueNotFoundException;
import org.example.issuetracker.repository.IssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueService issueService;

    @Test
    void createIssue_ShouldCreateAndReturnIssueResponseDTO() {

        IssueRequestDTO requestDTO = new IssueRequestDTO();

        requestDTO.setTitle("Login bug");
        requestDTO.setDescription("User cannot log in");
        requestDTO.setStatus(IssueStatus.OPEN);
        requestDTO.setPriority(IssuePriority.HIGH);
        requestDTO.setDueDate(LocalDate.of(2026, 5, 5));
        requestDTO.setAssignee("Sai");
        requestDTO.setReporter("Admin");

        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> {

            Issue issue = invocation.getArgument(0);
            issue.setId(1L);
            return issue;
        });

        IssueResponseDTO responseDTO = issueService.createIssue(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(1L,responseDTO.getId());
        assertEquals("Login bug", responseDTO.getTitle());
        assertEquals("User cannot log in", responseDTO.getDescription());
        assertEquals(IssueStatus.OPEN, responseDTO.getStatus());
        assertEquals(IssuePriority.HIGH, responseDTO.getPriority());
        assertEquals(LocalDate.of(2026, 5, 5), responseDTO.getDueDate());
        assertEquals("Sai", responseDTO.getAssignee());
        assertEquals("Admin", responseDTO.getReporter());

        verify(issueRepository,times(1)).save(any(Issue.class));

    }

    @Test
    void getIssueById_WhenIssueExists_ShouldReturnIssueResponseDTO() {
        Issue issue = new Issue(
                "Payment bug",
                "Payment fails when using credit card",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 10),
                "Sai",
                "Admin"
        );

        issue.setId(1L);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));

        IssueResponseDTO responseDTO = issueService.getIssueById(1L);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Payment bug", responseDTO.getTitle());
        assertEquals("Payment fails when using credit card", responseDTO.getDescription());
        assertEquals(IssueStatus.OPEN, responseDTO.getStatus());
        assertEquals(IssuePriority.HIGH, responseDTO.getPriority());
        assertEquals(LocalDate.of(2026, 5, 10), responseDTO.getDueDate());
        assertEquals("Sai", responseDTO.getAssignee());
        assertEquals("Admin", responseDTO.getReporter());

        verify(issueRepository, times(1)).findById(1L);
    }

    @Test
    void getIssueById_WhenIssueDoesNotExist_ShouldThrowIssueNotFoundException(){

        when(issueRepository.findById(100L)).thenReturn(Optional.empty());

        IssueNotFoundException exception = assertThrows(
                IssueNotFoundException.class,
                () -> issueService.getIssueById(100L)
        );

        assertEquals("Issue not found with id: 100", exception.getMessage());

        verify(issueRepository,times(1)).findById(100L);
    }

    @Test
    void updateIssueById_WhenIssueExists_ShouldUpdateAndReturnIssueResponseDTO() {
        Issue existingIssue = new Issue(
                "Old title",
                "Old description",
                IssueStatus.OPEN,
                IssuePriority.LOW,
                LocalDate.of(2026, 5, 1),
                "Old Assignee",
                "Old Reporter"
        );
        existingIssue.setId(1L);

        IssueRequestDTO updatedRequest = new IssueRequestDTO();
        updatedRequest.setTitle("Updated title");
        updatedRequest.setDescription("Updated description");
        updatedRequest.setStatus(IssueStatus.IN_PROGRESS);
        updatedRequest.setPriority(IssuePriority.HIGH);
        updatedRequest.setDueDate(LocalDate.of(2026, 5, 10));
        updatedRequest.setAssignee("Sai");
        updatedRequest.setReporter("Admin");

        when(issueRepository.findById(1L)).thenReturn(Optional.of(existingIssue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IssueResponseDTO responseDTO = issueService.updateIssueById(1L, updatedRequest);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Updated title", responseDTO.getTitle());
        assertEquals("Updated description", responseDTO.getDescription());
        assertEquals(IssueStatus.IN_PROGRESS, responseDTO.getStatus());
        assertEquals(IssuePriority.HIGH, responseDTO.getPriority());
        assertEquals(LocalDate.of(2026, 5, 10), responseDTO.getDueDate());
        assertEquals("Sai", responseDTO.getAssignee());
        assertEquals("Admin", responseDTO.getReporter());

        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(existingIssue);
    }

    @Test
    void deleteIssueById_WhenIssueExists_ShouldDeleteIssue() {
        Issue existingIssue = new Issue(
                "Delete test",
                "Issue to delete",
                IssueStatus.OPEN,
                IssuePriority.MEDIUM,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );
        existingIssue.setId(1L);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(existingIssue));

        issueService.deleteIssueById(1L);

        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).delete(existingIssue);
    }


    @Test
    void deleteIssueById_WhenIssueDoesNotExist_ShouldThrowIssueNotFoundException() {
        when(issueRepository.findById(100L)).thenReturn(Optional.empty());

        IssueNotFoundException exception = assertThrows(
                IssueNotFoundException.class,
                () -> issueService.deleteIssueById(100L)
        );

        assertEquals("Issue not found with id: 100", exception.getMessage());

        verify(issueRepository, times(1)).findById(100L);
        verify(issueRepository, never()).delete(any(Issue.class));
    }


    @Test
    void updateIssueStatusById_WhenIssueExists_ShouldUpdateStatus() {
        Issue existingIssue = new Issue(
                "Status test",
                "Testing status update",
                IssueStatus.OPEN,
                IssuePriority.MEDIUM,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );
        existingIssue.setId(1L);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(existingIssue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IssueResponseDTO responseDTO = issueService.updateIssueStatusById(1L, IssueStatus.RESOLVED);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(IssueStatus.RESOLVED, responseDTO.getStatus());
        assertEquals(IssuePriority.MEDIUM, responseDTO.getPriority());

        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(existingIssue);
    }

    @Test
    void updateIssuePriorityById_WhenIssueExists_ShouldUpdatePriority() {
        Issue existingIssue = new Issue(
                "Priority test",
                "Testing priority update",
                IssueStatus.OPEN,
                IssuePriority.LOW,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );
        existingIssue.setId(1L);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(existingIssue));
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IssueResponseDTO responseDTO = issueService.updateIssuePriorityById(1L, IssuePriority.HIGH);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(IssueStatus.OPEN, responseDTO.getStatus());
        assertEquals(IssuePriority.HIGH, responseDTO.getPriority());

        verify(issueRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).save(existingIssue);
    }

    @Test
    void getAllIssuesWithPagination_WhenSortFieldIsInvalid_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> issueService.getAllIssuesWithPagination(0, 5, "randomField", "asc")
        );

        assertTrue(exception.getMessage().contains("Invalid sort field: randomField"));
        assertTrue(exception.getMessage().contains("Allowed fields are"));

        verify(issueRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllIssuesWithPagination_WhenSortDirectionIsInvalid_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> issueService.getAllIssuesWithPagination(0, 5, "id", "wrong")
        );

        assertEquals(
                "Invalid sort direction: wrong. Allowed values are: asc, desc",
                exception.getMessage()
        );

        verify(issueRepository, never()).findAll(any(Pageable.class));
    }


    @Test
    void getAllIssuesWithPagination_WhenValidRequest_ShouldReturnPagedIssues() {
        Issue issue = new Issue(
                "Pagination test",
                "Testing pagination",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );
        issue.setId(1L);

        Page<Issue> issuePage = new PageImpl<>(List.of(issue));

        when(issueRepository.findAll(any(Pageable.class))).thenReturn(issuePage);

        Page<IssueResponseDTO> responsePage =
                issueService.getAllIssuesWithPagination(0, 5, "id", "asc");

        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Pagination test", responsePage.getContent().get(0).getTitle());
        assertEquals(IssueStatus.OPEN, responsePage.getContent().get(0).getStatus());

        verify(issueRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void filterIssues_WhenStatusPriorityAndAssigneeProvided_ShouldReturnFilteredIssues() {
        Issue issue = new Issue(
                "Filter test",
                "Testing advanced filter",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );
        issue.setId(1L);

        when(issueRepository.findAll(any(Specification.class))).thenReturn(List.of(issue));

        List<IssueResponseDTO> result = issueService.filterIssues(
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                "Sai"
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Filter test", result.get(0).getTitle());
        assertEquals(IssueStatus.OPEN, result.get(0).getStatus());
        assertEquals(IssuePriority.HIGH, result.get(0).getPriority());
        assertEquals("Sai", result.get(0).getAssignee());

        verify(issueRepository, times(1)).findAll(any(Specification.class));
    }




}