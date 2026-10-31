package com.issuepilot.service;

import com.issuepilot.dto.CreateIssueRequest;
import com.issuepilot.dto.IssueResponse;
import com.issuepilot.dto.PageResponse;
import com.issuepilot.dto.UpdateIssueRequest;
import com.issuepilot.entity.Issue;
import com.issuepilot.entity.IssuePriority;
import com.issuepilot.entity.IssueStatus;
import com.issuepilot.entity.Project;
import com.issuepilot.exception.ResourceNotFoundException;
import com.issuepilot.repository.IssueRepository;
import com.issuepilot.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository) {

        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
    }

    public IssueResponse createIssue(CreateIssueRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: "
                                        + request.getProjectId()
                        )
                );

        Issue issue = new Issue();

        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setStatus(request.getStatus());
        issue.setPriority(request.getPriority());
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

    public IssueResponse updateIssue(Long id, UpdateIssueRequest request) {

        Issue existingIssue = issueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + id
                        )
                );

        existingIssue.setTitle(request.getTitle());
        existingIssue.setDescription(request.getDescription());
        existingIssue.setStatus(request.getStatus());
        existingIssue.setPriority(request.getPriority());

        Issue updatedIssue = issueRepository.save(existingIssue);

        return IssueResponse.fromEntity(updatedIssue);
    }

    public void deleteIssue(Long id) {

        if (!issueRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Issue not found with id: " + id
            );
        }

        issueRepository.deleteById(id);
    }
}