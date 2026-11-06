
package com.issuepilot.repository;

import com.issuepilot.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IssueRepository
        extends JpaRepository<Issue, Long>,
        JpaSpecificationExecutor<Issue> {

    List<Issue> findTop200ByProject_IdOrderByCreatedAtDesc(Long projectId);
}
