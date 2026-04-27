package org.example.issuetracker.dto;

import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;

import java.time.LocalDateTime;

public class IssueResponseDTO {

    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IssueResponseDTO(
            Long id,
            String title,
            String description,
            IssueStatus status,
            IssuePriority priority,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
