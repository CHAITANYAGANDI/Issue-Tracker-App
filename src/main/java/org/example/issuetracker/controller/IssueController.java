package org.example.issuetracker.controller;

import jakarta.validation.Valid;
import org.example.issuetracker.dto.IssueRequestDTO;
import org.example.issuetracker.dto.IssueResponseDTO;
import org.example.issuetracker.enums.IssuePriority;
import org.example.issuetracker.enums.IssueStatus;
import org.example.issuetracker.service.IssueService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService){

        this.issueService = issueService;

    }

    @PostMapping
    public ResponseEntity<IssueResponseDTO> createIssue(@Valid @RequestBody IssueRequestDTO requestDTO){

       IssueResponseDTO createdIssue = issueService.createIssue(requestDTO);

        return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<IssueResponseDTO>> getAllIssues(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        Page<IssueResponseDTO> issues = issueService.getAllIssuesWithPagination(page, size,sortBy,sortDir);

        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> getIssueById(@PathVariable Long id){

        IssueResponseDTO issue = issueService.getIssueById(id);

        return ResponseEntity.ok(issue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> updateIssueById(@PathVariable Long id, @Valid @RequestBody IssueRequestDTO updatedIssue){

        IssueResponseDTO issue = issueService.updateIssueById(id,updatedIssue);

        return ResponseEntity.ok(issue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssueById(@PathVariable Long id){

        issueService.deleteIssueById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IssueResponseDTO>> getIssueByStatus(@PathVariable IssueStatus status){

        List<IssueResponseDTO> issues = issueService.getIssuesByStatus(status);

        return ResponseEntity.ok(issues);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<IssueResponseDTO>> getIssuesByPriority(@PathVariable IssuePriority priority){

        List<IssueResponseDTO> issues = issueService.getIssuesByPriority(priority);

        return ResponseEntity.ok(issues);
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<IssueResponseDTO>> searchIssuesByTitle(@RequestParam String keyword){

        List<IssueResponseDTO> issues = issueService.searchIssuesByTitle(keyword);

        return ResponseEntity.ok(issues);
    }
}
