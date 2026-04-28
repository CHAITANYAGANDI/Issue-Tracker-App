package org.example.issuetracker.service;

import org.example.issuetracker.dto.IssueRequestDTO;
import org.example.issuetracker.dto.IssueResponseDTO;
import org.example.issuetracker.entity.Issue;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.example.issuetracker.exception.IssueNotFoundException;
import org.example.issuetracker.repository.IssueRepository;
import org.example.issuetracker.specification.IssueSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "title",
            "status",
            "priority"
    );

    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository){

        this.issueRepository = issueRepository;
    }

    private IssueResponseDTO convertToResponseDto(Issue issue){

        return new IssueResponseDTO(issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getDueDate(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                issue.getAssignee(),
                issue.getReporter()
        );

    }

    private void validateSortField(String sortBy){

        if(sortBy == null || sortBy.isBlank()){

            throw new IllegalArgumentException("Sort field cannot be empty");
        }

        if(!ALLOWED_SORT_FIELDS.contains(sortBy)){

            logger.warn("Invalid sort field received: {}", sortBy);

            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy + ". Allowed fields are: " + ALLOWED_SORT_FIELDS
            );
        }
    }

    private void validateSortDirection(String sortDir){

        if(sortDir == null || sortDir.isBlank()){

            throw new IllegalArgumentException("Sort direction cannot be empty");
        }

        if(!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")){

            logger.warn("Invalid sort direction received: {}",sortDir);

            throw new IllegalArgumentException(
                    "Invalid sort direction: " + sortDir + ". Allowed values are: asc, desc"
            );
        }
    }

    @Transactional
    public IssueResponseDTO createIssue(IssueRequestDTO requestDTO){

        logger.info("Creating issue with title: {}", requestDTO.getTitle());

        Issue issue = new Issue(requestDTO.getTitle(),
                requestDTO.getDescription(),
                requestDTO.getStatus(),
                requestDTO.getPriority(),
                requestDTO.getDueDate(),
                requestDTO.getAssignee(),
                requestDTO.getReporter());

        Issue savedIssue = issueRepository.save(issue);

        logger.info("Issue created successfully with id: {}", savedIssue.getId());
        return convertToResponseDto(savedIssue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDTO> getAllIssues(){


        return issueRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDTO> getIssuesByStatus(IssueStatus status){

        return issueRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDTO> getIssuesByPriority(IssuePriority priority){

        return issueRepository.findByPriority(priority)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDTO> searchIssuesByTitle(String keyword){

        return issueRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    public IssueResponseDTO getIssueById(Long id){

        logger.info("Fetching issue with id: {}",id);

        Issue issue = issueRepository.findById(id).
                orElseThrow(() -> {
                    logger.warn("Issue not found with id: {}", id);
                    return new IssueNotFoundException("Issue not found with id: "+id);
                });

        return convertToResponseDto(issue);
    }


    @Transactional
    public IssueResponseDTO updateIssueById(Long id, IssueRequestDTO updatedIssue){

        logger.info("Updating issue with id: {}",id);

        Issue existingIssue = issueRepository.findById(id).
                orElseThrow(() -> {

                    logger.warn("Cannot update. Issue not found with id: {}",id);
                    return new IssueNotFoundException("Issue not found with id: "+id);
                });


        existingIssue.setTitle(updatedIssue.getTitle());
        existingIssue.setDescription(updatedIssue.getDescription());
        existingIssue.setStatus(updatedIssue.getStatus());
        existingIssue.setPriority(updatedIssue.getPriority());
        existingIssue.setDueDate(updatedIssue.getDueDate());
        existingIssue.setAssignee(updatedIssue.getAssignee());
        existingIssue.setReporter(updatedIssue.getReporter());

        Issue savedIssue = issueRepository.save(existingIssue);

        logger.info("Issue update successfully with id: {}", savedIssue.getId());

        return convertToResponseDto(savedIssue);


    }

    @Transactional
    public void deleteIssueById(Long id){

        logger.info("Deleting issue with id: {}", id);

        Issue existingIssue = issueRepository.findById(id).
                orElseThrow(() -> {
                    logger.warn("Cannot delete. Issue not found with id: {}",id);
                    return new IssueNotFoundException("Issue not found with id: "+ id);
                });

        logger.info("Issue deleted successfully with id: {}", id);
        issueRepository.deleteById(id);

    }

    @Transactional(readOnly = true)
    public Page<IssueResponseDTO> getAllIssuesWithPagination(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        logger.info("Fetching issue with pagination. page: {}, size: {}, sortBy; {}, sortDir: {}", page, size
        ,sortBy,sortDir);

        validateSortField(sortBy);
        validateSortDirection(sortDir);

        Sort sort;

        if(sortDir.equalsIgnoreCase("desc")){

            sort = Sort.by(sortBy).descending();
        }else{

            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Issue> issuePage = issueRepository.findAll(pageable);

        return issuePage.map(this::convertToResponseDto);
    }

    @Transactional
    public IssueResponseDTO updateIssueStatusById(Long id, IssueStatus status){

        logger.info("Updating issue status. id: {}, status: {}", id,status);

        Issue existingIssue = issueRepository.findById(id)
                .orElseThrow(() ->
                {
                    logger.warn("Cannot update status. Issue not found with id: {}",id);
                    return new IssueNotFoundException("Issue not found with id: "+ id);
                });

        existingIssue.setStatus(status);

        Issue savedIssue = issueRepository.save(existingIssue);

        logger.info("Issue status updated successfully. id: {}, status:{}", id, status);

        return convertToResponseDto(savedIssue);
    }

    @Transactional
    public IssueResponseDTO updateIssuePriorityById(Long id, IssuePriority priority){

        logger.info("Updating issue priority. id: {}, priority: {}",id,priority);

        Issue existingIssue = issueRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Cannot update priority. Issue not found with id: {}", id);
                    return new IssueNotFoundException("Issue not found with id: "+id);
                });

        existingIssue.setPriority(priority);

        Issue savedIssue = issueRepository.save(existingIssue);

        logger.info("Issue priority updated successfully. id: {}, priority: {}", id, priority);

        return convertToResponseDto(savedIssue);

    }

    public List<IssueResponseDTO> filterIssues(

            IssueStatus status,
            IssuePriority priority,
            String assignee
    ){

        logger.info("Filtering issues. status: {}, priority: {}, assignee: {}", status,priority,assignee);

        Specification<Issue> spec = ((root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction());

        if(status != null){

            spec = spec.and(IssueSpecification.hasStatus(status));
        }

        if(status != null){

            spec = spec.and(IssueSpecification.hasPriority(priority));
        }

        if(assignee != null && !assignee.isBlank()) {

            spec = spec.and(IssueSpecification.hasAssignee(assignee));
        }

        return issueRepository.findAll(spec)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
