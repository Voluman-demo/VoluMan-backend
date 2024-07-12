package com.example.demo.Candidate;

import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
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
        verify(candidateRepository, never()).findAll();
    }

    @Test
    void testGetCandidates_Ok() {
        Long recruiterId = 1L;

        Candidate candidate1 = new Candidate(1L, "John", "Doe", "john@example.com", "123456789", new Date(90, 1, 1), "Street", "City", "1", "2", "12345", "Male");
        Candidate candidate2 = new Candidate(2L, "Jane", "Doe", "jane@example.com", "987654321", new Date(91, 2, 2), "Street", "City", "1", "2", "12345", "Female");
        List<Candidate> candidates = Arrays.asList(candidate1, candidate2);

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findAll()).thenReturn(candidates);

        ResponseEntity<List<Candidate>> response = candidateController.getCandidates(recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidates, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
        verify(candidateRepository, times(1)).findAll();
    }

    @Test
    void testGetCandidate_Forbidden() {
        Long recruiterId = 1L;
        Long candidateId = 1L;


        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(false);

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
        verify(candidateRepository, never()).findById(candidateId);
    }

    @Test
    void testGetCandidate_NotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;


        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
        verify(candidateRepository, times(1)).findById(candidateId);
    }

    @Test
    void testGetCandidate_Ok() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        Candidate candidate = new Candidate(candidateId, "John", "Doe", "john@example.com", "123456789", new Date(90, 1, 1), "Street", "City", "1", "2", "12345", "Male");

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResponseEntity<Candidate> response = candidateController.getCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
        verify(candidateRepository, times(1)).findById(candidateId);
    }

    @Test
    void testAddCandidate() {
        Candidate candidate = new Candidate(1L, "John", "Doe", "john@example.com", "123456789", new Date(90, 1, 1), "Street", "City", "1", "2", "12345", "Male");

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
        verify(candidateRepository, never()).findById(candidateId);
        verify(candidateService, never()).acceptCandidate(any());
    }

    @Test
    void testAcceptCandidate_NotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;


        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.acceptCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
        verify(candidateRepository, times(1)).findById(candidateId);
        verify(candidateService, never()).acceptCandidate(any());
    }

    @Test
    void testAcceptCandidate_Ok() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        Candidate candidate = new Candidate(candidateId, "John", "Doe", "john@example.com", "123456789", new Date(90, 1, 1), "Street", "City", "1", "2", "12345", "Male");

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
        verify(candidateRepository, never()).findById(candidateId);
        verify(candidateService, never()).refuseCandidate(any());
    }

    @Test
    void testRefuseCandidate_NotFound() {
        Long recruiterId = 1L;
        Long candidateId = 1L;


        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER);
        verify(candidateRepository, times(1)).findById(candidateId);
        verify(candidateService, never()).refuseCandidate(any());
    }

    @Test
    void testRefuseCandidate_Ok() {
        Long recruiterId = 1L;
        Long candidateId = 1L;

        Candidate candidate = new Candidate(candidateId, "John", "Doe", "john@example.com", "123456789", new Date(90, 1, 1), "Street", "City", "1", "2", "12345", "Male");

        when(volunteerRepository.existsByVolunteerIdAndRole(recruiterId, VolunteerRole.RECRUITER)).thenReturn(true);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));

        ResponseEntity<Candidate> response = candidateController.refuseCandidate(candidateId, recruiterId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(candidate, response.getBody());
        verify(candidateService, times(1)).refuseCandidate(Optional.of(candidate));
    }
}
