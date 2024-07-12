package com.example.demo.Candidate;

import com.example.demo.Volunteer.VolunteerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {

    @Mock
    private VolunteerService volunteerService;

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    public void testAcceptCandidate() {
        Candidate candidate = new Candidate();
        Optional<Candidate> candidateOptional = Optional.of(candidate);

        candidateService.acceptCandidate(candidateOptional);

        verify(volunteerService, times(1)).addVolunteer(candidateOptional);
        verify(candidateRepository, times(1)).delete(candidate);
    }

    @Test
    public void testRefuseCandidate() {
        Candidate candidate = new Candidate();
        Optional<Candidate> candidateOptional = Optional.of(candidate);

        candidateService.refuseCandidate(candidateOptional);

        verify(candidateRepository, times(1)).delete(candidate);
    }
}
