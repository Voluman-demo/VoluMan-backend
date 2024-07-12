package com.example.demo.Candidate;

import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidateControllerTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CandidateService candidateService;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private CandidateController candidateController;

    @Test
    public void testGetCandidatesForbidden() {
        Long recruiterId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<List<Candidate>> response = candidateController.getCandidates(recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetCandidates() {
        Long recruiterId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findAll()).thenReturn(List.of(new Candidate()));

        ResponseEntity<List<Candidate>> response = candidateController.getCandidates(recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetCandidateForbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testGetCandidateNotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetCandidate() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        Candidate candidate = new Candidate();

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
    }

    @Test
    public void testAddCandidate() {
        Candidate candidate = new Candidate();

        when(candidateRepository.save(candidate)).thenReturn(candidate);

        ResponseEntity<Candidate> response = candidateController.addCandidate(candidate);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(candidate, response.getBody());
    }

    @Test
    public void testAcceptCandidateForbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testAcceptCandidateNotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAcceptCandidate() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        Candidate candidate = new Candidate();
        Optional<Candidate> candidateOptional = Optional.of(candidate);

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(candidateOptional);

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(candidateService, times(1)).acceptCandidate(candidateOptional);
    }

    @Test
    public void testRefuseCandidateForbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testRefuseCandidateNotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRefuseCandidate() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        Candidate candidate = new Candidate();
        Optional<Candidate> candidateOptional = Optional.of(candidate);

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(candidateOptional);

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(candidateService, times(1)).refuseCandidate(candidateOptional);
    }
}
