package com.issuepilot.service;

import com.issuepilot.dto.CreateProjectRequest;
import com.issuepilot.dto.ProjectResponse;
import com.issuepilot.entity.Project;
import com.issuepilot.entity.User;
import com.issuepilot.exception.ResourceNotFoundException;
import com.issuepilot.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUserService;

    public ProjectService(
            ProjectRepository projectRepository,
            CurrentUserService currentUserService) {

        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
    }

    public ProjectResponse createProject(CreateProjectRequest request) {

        String projectKey = request.getKey()
                .trim()
                .toUpperCase();

        if (projectRepository.existsByKey(projectKey)) {
            throw new IllegalArgumentException(
                    "Project key already exists: " + projectKey
            );
        }

        User owner = currentUserService.getCurrentUser();

        Project project = new Project();

        project.setName(request.getName());
        project.setKey(projectKey);
        project.setDescription(request.getDescription());
        project.setOwner(owner);

        Project savedProject = projectRepository.save(project);

        return ProjectResponse.fromEntity(savedProject);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        return ProjectResponse.fromEntity(project);
    }

    public void deleteProject(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + id
                        )
                );

        User currentUser = currentUserService.getCurrentUser();

        boolean isAdmin =
                currentUser.getRole().name().equals("ADMIN");

        boolean isOwner =
                project.getOwner().getId().equals(currentUser.getId());

        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to delete this project"
            );
        }

        projectRepository.delete(project);
    }
}