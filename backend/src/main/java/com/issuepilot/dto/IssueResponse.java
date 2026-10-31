package com.issuepilot.dto;

import com.issuepilot.entity.Issue;
import com.issuepilot.entity.IssuePriority;
import com.issuepilot.entity.IssueStatus;

import java.time.LocalDateTime;

public class IssueResponse {

    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;

    private Long projectId;
    private String projectName;
    private String projectKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IssueResponse() {
    }

    public IssueResponse(
            Long id,
            String title,
            String description,
            IssueStatus status,
            IssuePriority priority,
            Long projectId,
            String projectName,
            String projectKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectKey = projectKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static IssueResponse fromEntity(Issue issue) {

        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getProject().getId(),
                issue.getProject().getName(),
                issue.getProject().getKey(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}