package com.example.demo.Volunteer.Candidate;

import com.example.demo.Volunteer.User.UserService;
import com.example.demo.Volunteer.VolunteerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateService {

    private final VolunteerService volunteerService;
    private final CandidateRepository candidateRepository;
    private final UserService userService;

    public CandidateService(VolunteerService volunteerService, CandidateRepository candidateRepository, UserService userService) {
        this.volunteerService = volunteerService;
        this.candidateRepository = candidateRepository;
        this.userService = userService;
    }

    @Transactional
    public void acceptCandidate(Optional<Candidate> candidate) {
        userService.register(candidate);
        candidateRepository.delete(candidate.get());
    }

    @Transactional
    public void refuseCandidate(Optional<Candidate> candidate) {
        candidateRepository.delete(candidate.get());
    }

}
