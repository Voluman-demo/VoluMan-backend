package com.example.demo.Candidate;


import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/candidates")
public class CandidateController {


    private final CandidateRepository candidateRepository;
    private final CandidateService candidateService;
    private final VolunteerRepository volunteerRepository;
    public CandidateController(CandidateRepository candidateRepository, CandidateService candidateService, VolunteerRepository volunteerRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateService = candidateService;
        this.volunteerRepository = volunteerRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Candidate>> getCandidates(@RequestParam Long recruiterId) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(candidateRepository.findAll());
    }

    @GetMapping("/{idCandidate}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable long idCandidate,@RequestParam Long recruiterId) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        return candidate.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("")
    public ResponseEntity<Candidate> addCandidate(@RequestBody Candidate candidate) { //DONE

        Candidate savedCandidate = candidateRepository.save(candidate);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
    }

    @PostMapping("{idCandidate}/accept")
    public ResponseEntity<Candidate> acceptCandidate(@PathVariable long idCandidate, @RequestParam Long recruiterId) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            candidateService.acceptCandidate(candidate);
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{idCandidate}/refuse")
    public ResponseEntity<Candidate> refuseCandidate(@PathVariable long idCandidate, @RequestParam Long recruiterId) { //DONE
        if (!volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<Candidate> candidate = candidateRepository.findById(idCandidate);
        if (candidate.isPresent()) {
            candidateService.refuseCandidate(candidate);
            return ResponseEntity.ok(candidate.get());
        }
        return ResponseEntity.notFound().build();
    }
}
