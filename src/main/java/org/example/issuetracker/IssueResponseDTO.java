package com.example.demo;

public class IssueResponseDTO {

    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;

    public IssueResponseDTO(Long id, String title, String description,IssueStatus status, IssuePriority priority){

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public Long getId() {
        return id;
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
