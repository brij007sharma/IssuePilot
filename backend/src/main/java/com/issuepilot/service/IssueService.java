
package com.issuepilot.service;

import com.issuepilot.ai.AiClient;
import com.issuepilot.ai.dto.PriorityPredictionResponse;
import com.issuepilot.dto.CreateIssueRequest;
import com.issuepilot.dto.IssueResponse;
import com.issuepilot.dto.PageResponse;
import com.issuepilot.dto.UpdateIssueRequest;
import com.issuepilot.entity.Issue;
import com.issuepilot.entity.IssuePriority;
import com.issuepilot.entity.IssueStatus;
import com.issuepilot.entity.Project;
import com.issuepilot.entity.User;
import com.issuepilot.exception.ResourceNotFoundException;
import com.issuepilot.repository.IssueRepository;
import com.issuepilot.repository.ProjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class IssueService {

    private static final Logger log =
            LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUserService;
    private final AiClient aiClient;

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            CurrentUserService currentUserService,
            AiClient aiClient) {

        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
        this.aiClient = aiClient;
    }

    private void checkProjectAccess(Project project) {

        User currentUser = currentUserService.getCurrentUser();

        boolean isAdmin =
                currentUser.getRole().name().equals("ADMIN");

        boolean isOwner =
                project.getOwner().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException(
                    "You are not authorized to manage issues in this project"
            );
        }
    }

    private IssuePriority determinePriority(CreateIssueRequest request) {

        // Respect the priority explicitly selected by the user.
        if (request.getPriority() != null) {
            return request.getPriority();
        }

        // Ask the Python AI service to predict priority.
        try {
            PriorityPredictionResponse prediction =
                    aiClient.predictPriority(
                            request.getTitle(),
                            request.getDescription()
                    );

            IssuePriority predictedPriority =
                    IssuePriority.valueOf(
                            prediction.predictedPriority()
                    );

            log.info(
                    "AI predicted priority {} with confidence {} using {}",
                    predictedPriority,
                    prediction.confidence(),
                    prediction.model()
            );

            return predictedPriority;

        } catch (RestClientException | IllegalArgumentException exception) {

            log.warn(
                    "AI priority prediction failed. Using MEDIUM as fallback.",
                    exception
            );

            return IssuePriority.MEDIUM;
        }
    }

    public IssueResponse createIssue(CreateIssueRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + request.getProjectId()
                        )
                );

        // Authorization happens before calling the AI service.
        checkProjectAccess(project);

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setStatus(request.getStatus());

        // AI prediction is used only when priority is not supplied.
        issue.setPriority(determinePriority(request));

        issue.setProject(project);

        Issue savedIssue = issueRepository.save(issue);

        return IssueResponse.fromEntity(savedIssue);
    }

    public PageResponse<IssueResponse> getAllIssues(
            IssueStatus status,
            IssuePriority priority,
            Long projectId,
            Pageable pageable) {

        Specification<Issue> specification =
                (root, query, criteriaBuilder) -> null;

        if (status != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    status
                            )
            );
        }

        if (priority != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("priority"),
                                    priority
                            )
            );
        }

        if (projectId != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("project").get("id"),
                                    projectId
                            )
            );
        }

        Page<Issue> page =
                issueRepository.findAll(specification, pageable);

        return PageResponse.fromPage(
                page,
                IssueResponse::fromEntity
        );
    }

    public IssueResponse getIssueById(Long id) {

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + id
                        )
                );

        return IssueResponse.fromEntity(issue);
    }

    public IssueResponse updateIssue(
            Long id,
            UpdateIssueRequest request) {

        Issue existingIssue = issueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + id
                        )
                );

        checkProjectAccess(existingIssue.getProject());

        existingIssue.setTitle(request.getTitle());
        existingIssue.setDescription(request.getDescription());
        existingIssue.setStatus(request.getStatus());
        existingIssue.setPriority(request.getPriority());

        Issue updatedIssue =
                issueRepository.save(existingIssue);

        return IssueResponse.fromEntity(updatedIssue);
    }

    public void deleteIssue(Long id) {

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + id
                        )
                );

        checkProjectAccess(issue.getProject());

        issueRepository.delete(issue);
    }
}
