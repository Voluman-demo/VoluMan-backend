package com.example.demo.Candidate;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/candidates")
public class CandidateController {


    private final CandidateRepository candidateRepository;
    private final CandidateService candidateService;

    public CandidateController(CandidateRepository candidateRepository, CandidateService candidateService) {
        this.candidateRepository = candidateRepository;
        this.candidateService = candidateService;
    }

    @GetMapping("")
    public List<Candidate> getCandidates() {
        return candidateRepository.findAll();
    }

    @GetMapping("/{idCandidate}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable long idCandidate) {
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping()
    public ResponseEntity<Candidate> addCandidate(@RequestBody Candidate candidate) {
        candidateRepository.save(candidate);
        return ResponseEntity.ok(candidate);
    }

    @PostMapping("{idCandidate}/accept")
    public ResponseEntity<Candidate> acceptCandidate(@PathVariable long idCandidate) {
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            candidateService.acceptCandidate(candidate);
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{idCandidate}/refuse")
    public ResponseEntity<Candidate> refuseCandidate(@PathVariable long idCandidate) {
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            candidateService.refuseCandidate(candidate);
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }
}
