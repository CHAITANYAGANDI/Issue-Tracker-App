package org.example.issuetracker.dto;

import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IssueResponseDTO {

    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assignee;
    private String reporter;

    public IssueResponseDTO(
            Long id,
            String title,
            String description,
            IssueStatus status,
            IssuePriority priority,
            LocalDate dueDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String assignee,
            String reporter
    ){

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assignee = assignee;
        this.reporter = reporter;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getReporter() {
        return reporter;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate(){

        return dueDate;
    }

}
