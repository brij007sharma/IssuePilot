package com.issuepilot.controller;

import com.issuepilot.dto.CreateIssueRequest;
import com.issuepilot.dto.IssueResponse;
import com.issuepilot.dto.PageResponse;
import com.issuepilot.dto.UpdateIssueRequest;
import com.issuepilot.entity.IssuePriority;
import com.issuepilot.entity.IssueStatus;
import com.issuepilot.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.issuepilot.dto.DuplicateCheckRequest;
import com.issuepilot.dto.DuplicateCheckResponse;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Valid @RequestBody CreateIssueRequest request) {

        return ResponseEntity.ok(
                issueService.createIssue(request)
        );
    }

    @GetMapping
    public ResponseEntity<PageResponse<IssueResponse>> getAllIssues(
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) IssuePriority priority,
            @RequestParam(required = false) Long projectId,
            Pageable pageable) {

        return ResponseEntity.ok(
                issueService.getAllIssues(
                        status,
                        priority,
                        projectId,
                        pageable
                )
        );
    }
    @PostMapping("/duplicate-check")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicates(
            @Valid @RequestBody DuplicateCheckRequest request) {

        return ResponseEntity.ok(
                issueService.checkDuplicates(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                issueService.getIssueById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIssueRequest request) {

        return ResponseEntity.ok(
                issueService.updateIssue(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(
            @PathVariable Long id) {

        issueService.deleteIssue(id);

        return ResponseEntity.noContent().build();
    }
}