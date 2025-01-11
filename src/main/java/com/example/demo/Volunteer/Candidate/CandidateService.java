package com.example.demo.Volunteer.Candidate;

import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.UserService;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final UserService userService;
    private final VolunteerRepository volunteerRepository;
    private final LogService logService;
    private final VolunteerService volunteerService;


    public CandidateService(CandidateRepository candidateRepository, UserService userService, VolunteerRepository volunteerRepository, LogService logService, VolunteerService volunteerService) {
        this.candidateRepository = candidateRepository;
        this.userService = userService;
        this.volunteerRepository = volunteerRepository;
        this.logService = logService;
        this.volunteerService = volunteerService;
    }

    public List<Candidate> getCandidates(ID recruiterId) {
        validateRecruiter(recruiterId, volunteerRepository);
        List<Candidate> candidates = candidateRepository.findAll();
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No candidates found");
        }
        return candidates;
    }

    public Candidate getCandidate(Long idCandidate, ID recruiterId) {
        validateRecruiter(recruiterId, volunteerRepository);
        return candidateRepository.findById(idCandidate)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
    }


    @Transactional
    public void acceptCandidate(Long idCandidate, ID recruiterId) {
        validateRecruiter(recruiterId, volunteerRepository);
        Candidate candidate = candidateRepository.findById(idCandidate)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        userService.register(Optional.of(candidate));
        candidateRepository.delete(candidate);

        logService.logCandidate(candidate, EventType.ACCEPT, "Candidate accepted by recruiter " + recruiterId);
    }

    @Transactional
    public void refuseCandidate(Long idCandidate, ID recruiterId) {
        validateRecruiter(recruiterId, volunteerRepository);
        Candidate candidate = candidateRepository.findById(idCandidate)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));
        candidateRepository.delete(candidate);
        logService.logCandidate(candidate, EventType.REFUSE, "Candidate refused by recruiter " + recruiterId);
    }

    public Candidate addCandidate(Candidate candidate) {
        if(!isEmailUnique(candidate.getEmail())){
            throw new IllegalArgumentException("Email address already in use");
        }
        logService.logCandidate(candidate, EventType.ADD, "added candidate");
        return candidateRepository.save(candidate);
    }

    private boolean isEmailUnique(String email) {
        return !volunteerService.existsVolunteerByEmail(email);
    }

    private void validateRecruiter(ID recruiterId, VolunteerRepository volunteerRepository) {
        if (volunteerRepository.existsByIdAndPosition(recruiterId, Position.RECRUITER)) {
            throw new IllegalArgumentException("Unauthorized: recruiter role required");
        }
    }
}
