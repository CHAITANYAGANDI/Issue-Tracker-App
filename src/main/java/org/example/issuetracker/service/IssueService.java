package org.example.issuetracker.service;

import org.example.issuetracker.dto.IssueRequestDTO;
import org.example.issuetracker.dto.IssueResponseDTO;
import org.example.issuetracker.entity.Issue;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.example.issuetracker.exception.IssueNotFoundException;
import org.example.issuetracker.repository.IssueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IssueService {

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

    @Transactional
    public IssueResponseDTO createIssue(IssueRequestDTO requestDTO){

        Issue issue = new Issue(requestDTO.getTitle(),
                requestDTO.getDescription(),
                requestDTO.getStatus(),
                requestDTO.getPriority());

        Issue savedIssue = issueRepository.save(issue);

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

        Issue issue = issueRepository.findById(id).orElseThrow(() -> new IssueNotFoundException("Issue not found with id: "+id));

        return convertToResponseDto(issue);
    }


    @Transactional
    public IssueResponseDTO updateIssueById(Long id, IssueRequestDTO updatedIssue){

        Issue existingIssue = issueRepository.findById(id).
                orElseThrow(() -> new IssueNotFoundException("Issue not found with id: "+id));


        existingIssue.setTitle(updatedIssue.getTitle());
        existingIssue.setDescription(updatedIssue.getDescription());
        existingIssue.setStatus(updatedIssue.getStatus());
        existingIssue.setPriority(updatedIssue.getPriority());

        Issue savedIssue = issueRepository.save(existingIssue);

        return convertToResponseDto(savedIssue);


    }

    @Transactional
    public void deleteIssueById(Long id){

        Issue existingIssue = issueRepository.findById(id).
                orElseThrow(() -> new IssueNotFoundException("Issue not found with id: "+ id));


        issueRepository.deleteById(id);


    }

    private IssueResponseDTO convertToResponseDto(Issue issue){

        return new IssueResponseDTO(issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority(),
                issue.getCreatedAt(),
                issue.getUpdatedAt());

    }

    private void validateSortField(String sortBy){

        if(sortBy == null || sortBy.isBlank()){

            throw new IllegalArgumentException("Sort field cannot be empty");
        }

        if(!ALLOWED_SORT_FIELDS.contains(sortBy)){

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

            throw new IllegalArgumentException(
                    "Invalid sort direction: " + sortDir + ". Allowed values are: asc, desc"
            );
        }
    }

    @Transactional(readOnly = true)
    public Page<IssueResponseDTO> getAllIssuesWithPagination(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

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
}
