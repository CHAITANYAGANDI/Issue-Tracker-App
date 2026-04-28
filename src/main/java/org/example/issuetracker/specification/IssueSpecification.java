package org.example.issuetracker.specification;

import org.example.issuetracker.entity.Issue;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.springframework.data.jpa.domain.Specification;

public class IssueSpecification {

    public static Specification<Issue> hasStatus(IssueStatus status){

        return (root,query,criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"),status);
    }

    public static Specification<Issue> hasPriority(IssuePriority priority){

        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("priority"),priority));
    }

    public static Specification<Issue> hasAssignee(String assignee){

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("assignee")),
                        assignee.toLowerCase()
                ));
    }
}
