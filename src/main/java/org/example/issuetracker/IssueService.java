package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository){

        this.issueRepository = issueRepository;
    }

    public IssueResponseDTO createIssue(IssueRequestDTO requestDTO){

        Issue issue = new Issue(requestDTO.getTitle(),
                requestDTO.getDescription(),
                requestDTO.getStatus(),
                requestDTO.getPriority());

        Issue savedIssue = issueRepository.save(issue);

        return convertToResponseDto(savedIssue);
    }

    public List<IssueResponseDTO> getAllIssues(){


        return issueRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<IssueResponseDTO> getIssuesByStatus(IssueStatus status){

        return issueRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<IssueResponseDTO> getIssuesByPriority(IssuePriority priority){

        return issueRepository.findByPriority(priority)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<IssueResponseDTO> searchIssuesByTitle(String keyword){

        return issueRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public IssueResponseDTO getIssueById(Long id){

        Issue issue = issueRepository.findById(id).orElseThrow(() -> new IssueNotFoundException("Issue not found with id: "+id));

        return convertToResponseDto(issue);
    }


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

    public void deleteIssueById(Long id){

        if(!issueRepository.existsById(id)){

            throw new IssueNotFoundException("Issue not found with id: "+id);
        }

        issueRepository.deleteById(id);


    }

    private IssueResponseDTO convertToResponseDto(Issue issue){

        return new IssueResponseDTO(issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getPriority());

    }

    public Page<IssueResponseDTO> getAllIssuesWithPagination(int page, int size, String sortBy, String sortDir){

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
