package com.example.demo.Candidate;

import com.example.demo.Volunteer.VolunteerService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateService {

    private final VolunteerService volunteerService;
    private final CandidateRepository candidateRepository;

    public CandidateService(VolunteerService volunteerService, CandidateRepository candidateRepository) {
        this.volunteerService = volunteerService;
        this.candidateRepository = candidateRepository;
    }

    public void acceptCandidate(Optional<Candidate> candidate) {
        volunteerService.addVolunteer(candidate);
        candidateRepository.delete(candidate.get());

    }
    public void refuseCandidate(Optional<Candidate> candidate) {
        candidateRepository.delete(candidate.get());
    }
}
