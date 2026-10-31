package com.issuepilot.repository;

import com.issuepilot.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IssueRepository
        extends JpaRepository<Issue, Long>,
        JpaSpecificationExecutor<Issue> {
}