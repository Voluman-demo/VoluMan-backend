package com.example.demo.Candidate;

import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CandidateControllerTest {
    @InjectMocks
    private CandidateController candidateController;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private VolunteerRepository volunteerRepository;
    @Mock
    private CandidateService candidateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testGetCandidates_Forbidden() {
        Long recruiterId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<List<Candidate>> response = candidateController.getCandidates(recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
    }

    @Test
    void testGetCandidates_Ok() {
        Long recruiterId = 1L;
        Candidate candidate = new Candidate();
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findAll()).thenReturn(Collections.singletonList(candidate));

        ResponseEntity<List<Candidate>> response = candidateController.getCandidates(recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetCandidate_Forbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
    }

    @Test
    void testGetCandidate_NotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetCandidate_Ok() {
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
    void testAddCandidate() {
        Candidate candidate = new Candidate();
        when(candidateRepository.save(candidate)).thenReturn(candidate);

        ResponseEntity<Candidate> response = candidateController.addCandidate(candidate);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void testAcceptCandidate_Forbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
    }

    @Test
    void testAcceptCandidate_NotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAcceptCandidate_Ok() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        Candidate candidate = new Candidate();
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(candidateService, times(1)).acceptCandidate(Optional.of(candidate));
    }

    @Test
    void testRefuseCandidate_Forbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
    }

    @Test
    void testRefuseCandidate_NotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testRefuseCandidate_Ok() {
        Long recruiterId = 1L;
        Long candidateId = 1L;
        Candidate candidate = new Candidate();
        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(candidateService, times(1)).refuseCandidate(Optional.of(candidate));
    }
}
