package com.issuepilot.dto;

import com.issuepilot.entity.Project;

import java.time.LocalDateTime;

public class ProjectResponse {

    private Long id;
    private String name;
    private String key;
    private String description;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;

    public ProjectResponse() {
    }

    public ProjectResponse(
            Long id,
            String name,
            String key,
            String description,
            Long ownerId,
            String ownerName,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.key = key;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
    }

    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getKey(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getName(),
                project.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}