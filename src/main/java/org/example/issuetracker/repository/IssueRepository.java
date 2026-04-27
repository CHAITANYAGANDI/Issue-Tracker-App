package org.example.issuetracker.repository;

import org.example.issuetracker.entity.Issue;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByStatus(IssueStatus status);
    List<Issue> findByPriority(IssuePriority priority);
    List<Issue> findByTitleContainingIgnoreCase(String keyword);

}
