package com.example.demo.Volunteer.Candidate;

import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Volunteer.Role.VolunteerRole;
import com.example.demo.Volunteer.VolunteerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/candidates")
@Tag(name = "Candidate Management", description = "Endpoints for managing candidates")
public class CandidateController {

    private final CandidateRepository candidateRepository;
    private final CandidateService candidateService;
    private final VolunteerRepository volunteerRepository;
    private final LogService logService;

    public CandidateController(CandidateRepository candidateRepository, CandidateService candidateService, VolunteerRepository volunteerRepository, LogService logService) {
        this.candidateRepository = candidateRepository;
        this.candidateService = candidateService;
        this.volunteerRepository = volunteerRepository;
        this.logService = logService;
    }

    @GetMapping("")
    @Operation(summary = "Get all candidates", description = "Retrieves a list of all candidates if the user is a recruiter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of candidates"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not a recruiter"),
            @ApiResponse(responseCode = "404", description = "No candidates found")
    })
    public ResponseEntity<List<Candidate>> getCandidates(
            @Parameter(description = "ID of the recruiter making the request", example = "1")
            @RequestParam Long recruiterId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Candidate> candidates = candidateRepository.findAll();
        if (candidates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/{idCandidate}")
    @Operation(summary = "Get candidate by ID", description = "Retrieves a specific candidate by their ID if the user is a recruiter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the candidate"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not a recruiter"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")
    })
    public ResponseEntity<Candidate> getCandidate(
            @Parameter(description = "ID of the candidate", example = "1") @PathVariable long idCandidate,
            @Parameter(description = "ID of the recruiter making the request", example = "1") @RequestParam Long recruiterId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        return candidate.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(summary = "Add a new candidate", description = "Adds a new candidate to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Candidate created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<Candidate> addCandidate(
            @Parameter(description = "Candidate details to add", required = true) @RequestBody Candidate candidate) {
        Candidate savedCandidate = candidateService.addCandidate(candidate);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
    }

    @PostMapping("{idCandidate}/accept")
    @Operation(summary = "Accept a candidate", description = "Accepts a candidate by their ID if the user is a recruiter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate accepted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not a recruiter"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")
    })
    public ResponseEntity<Candidate> acceptCandidate(
            @Parameter(description = "ID of the candidate to accept", example = "1") @PathVariable long idCandidate,
            @Parameter(description = "ID of the recruiter accepting the candidate", example = "1") @RequestParam Long recruiterId) {
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            candidateService.acceptCandidate(idCandidate, recruiterId);
            logService.logCandidate(candidate.get(), EventType.ACCEPT, "Candidate accepted by recruiter " + recruiterId);
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{idCandidate}/refuse")
    @Operation(summary = "Refuse a candidate", description = "Refuses a candidate by their ID if the user is a recruiter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidate refused successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not a recruiter"),
            @ApiResponse(responseCode = "404", description = "Candidate not found")
    })
    public ResponseEntity<Candidate> refuseCandidate(
            @Parameter(description = "ID of the candidate to refuse", example = "1") @PathVariable long idCandidate,
            @Parameter(description = "ID of the recruiter refusing the candidate", example = "1") @RequestParam Long recruiterId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            candidateService.refuseCandidate(idCandidate, recruiterId);
            logService.logCandidate(candidate.get(), EventType.REFUSE, "Candidate refused by recruiter " + recruiterId);
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }
}
