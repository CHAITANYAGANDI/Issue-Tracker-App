package org.example.issuetracker.repository;

import org.example.issuetracker.entity.Issue;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.example.issuetracker.specification.IssueSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class IssueRepositoryTest {

    @Autowired
    private IssueRepository issueRepository;

    @Test
    void findByStatus_WhenStatusExists_ShouldReturnMatchingIssues() {
        Issue issue1 = new Issue(
                "Login bug",
                "User cannot log in",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );

        Issue issue2 = new Issue(
                "Payment bug",
                "Payment fails",
                IssueStatus.RESOLVED,
                IssuePriority.MEDIUM,
                LocalDate.of(2026, 5, 10),
                "John",
                "Admin"
        );

        Issue issue3 = new Issue(
                "Profile bug",
                "Profile image not loading",
                IssueStatus.OPEN,
                IssuePriority.LOW,
                LocalDate.of(2026, 5, 15),
                "Sai",
                "User"
        );

        issueRepository.saveAll(List.of(issue1, issue2, issue3));

        List<Issue> result = issueRepository.findByStatus(IssueStatus.OPEN);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Issue::getStatus)
                .containsOnly(IssueStatus.OPEN);
    }

    @Test
    void findByPriority_WhenPriorityExists_ShouldReturnMatchingIssues() {
        Issue issue1 = new Issue(
                "Login bug",
                "User cannot log in",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );

        Issue issue2 = new Issue(
                "UI bug",
                "Button alignment issue",
                IssueStatus.IN_PROGRESS,
                IssuePriority.LOW,
                LocalDate.of(2026, 5, 7),
                "Mike",
                "Admin"
        );

        issueRepository.saveAll(List.of(issue1, issue2));

        List<Issue> result = issueRepository.findByPriority(IssuePriority.HIGH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Login bug");
        assertThat(result.get(0).getPriority()).isEqualTo(IssuePriority.HIGH);
    }

    @Test
    void findByTitleContainingIgnoreCase_WhenKeywordMatches_ShouldReturnMatchingIssues() {
        Issue issue1 = new Issue(
                "Login bug",
                "User cannot log in",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );

        Issue issue2 = new Issue(
                "Payment issue",
                "Payment fails",
                IssueStatus.OPEN,
                IssuePriority.MEDIUM,
                LocalDate.of(2026, 5, 10),
                "John",
                "Admin"
        );

        issueRepository.saveAll(List.of(issue1, issue2));

        List<Issue> result = issueRepository.findByTitleContainingIgnoreCase("LOGIN");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Login bug");
    }

    @Test
    void findAll_WithSpecification_ShouldReturnFilteredIssues() {
        Issue issue1 = new Issue(
                "Login bug",
                "User cannot log in",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 5),
                "Sai",
                "Admin"
        );

        Issue issue2 = new Issue(
                "Payment bug",
                "Payment fails",
                IssueStatus.OPEN,
                IssuePriority.HIGH,
                LocalDate.of(2026, 5, 10),
                "John",
                "Admin"
        );

        Issue issue3 = new Issue(
                "UI bug",
                "Button alignment issue",
                IssueStatus.RESOLVED,
                IssuePriority.LOW,
                LocalDate.of(2026, 5, 15),
                "Sai",
                "User"
        );

        issueRepository.saveAll(List.of(issue1, issue2, issue3));

        Specification<Issue> spec = IssueSpecification.hasStatus(IssueStatus.OPEN)
                .and(IssueSpecification.hasPriority(IssuePriority.HIGH))
                .and(IssueSpecification.hasAssignee("Sai"));

        List<Issue> result = issueRepository.findAll(spec);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Login bug");
        assertThat(result.get(0).getStatus()).isEqualTo(IssueStatus.OPEN);
        assertThat(result.get(0).getPriority()).isEqualTo(IssuePriority.HIGH);
        assertThat(result.get(0).getAssignee()).isEqualTo("Sai");
    }
}