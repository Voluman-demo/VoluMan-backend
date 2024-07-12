package com.example.demo.Volunteer;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerControllerTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private VolunteerService volunteerService;

    @InjectMocks
    private VolunteerController volunteerController;

    @Test
    public void testGetVolunteers() {
        when(volunteerRepository.findAll()).thenReturn(List.of(new Volunteer()));

        ResponseEntity<List<Volunteer>> response = volunteerController.getVolunteers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void testGetVolunteer() {
        Long idVolunteer = 1L;
        Volunteer volunteer = new Volunteer();
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.of(volunteer));

        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(volunteer, response.getBody());
    }

    @Test
    public void testGetVolunteerNotFound() {
        Long idVolunteer = 1L;
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.empty());

        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetVolunteerLeaders() {
        when(volunteerRepository.findAllByRole(VolunteerRole.LEADER)).thenReturn(List.of(new Volunteer()));

        ResponseEntity<List<Volunteer>> response = volunteerController.getVolunteerLeaders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetVolunteerLeadersNotFound() {
        when(volunteerRepository.findAllByRole(VolunteerRole.LEADER)).thenReturn(List.of());

        ResponseEntity<List<Volunteer>> response = volunteerController.getVolunteerLeaders();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetVolunteerLeader() {
        Long idVolunteer = 1L;
        Volunteer volunteer = new Volunteer();
        when(volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER)).thenReturn(Optional.of(volunteer));

        ResponseEntity<Volunteer> response = volunteerController.getVolunteerLeader(idVolunteer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(volunteer, response.getBody());
    }

    @Test
    public void testGetVolunteerLeaderNotFound() {
        Long idVolunteer = 1L;
        when(volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER)).thenReturn(Optional.empty());

        ResponseEntity<Volunteer> response = volunteerController.getVolunteerLeader(idVolunteer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPromoteToLeaderForbidden() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(false);

        ResponseEntity<Void> response = volunteerController.promoteToLeader(idVolunteer, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testPromoteToLeaderConflict() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER)).thenReturn(true);

        ResponseEntity<Void> response = volunteerController.promoteToLeader(idVolunteer, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testPromoteToLeaderNotFound() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER)).thenReturn(false);
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = volunteerController.promoteToLeader(idVolunteer, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testPromoteToLeader() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);
        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER)).thenReturn(false);
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.of(volunteer));

        ResponseEntity<Void> response = volunteerController.promoteToLeader(idVolunteer, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).promoteToLeader(idVolunteer);
    }

    @Test
    public void testDegradeLeaderForbidden() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(false);

        ResponseEntity<Void> response = volunteerController.degradeLeader(idVolunteer, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testDegradeLeaderConflict() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(idVolunteer, VolunteerRole.VOLUNTEER)).thenReturn(true);

        ResponseEntity<Void> response = volunteerController.degradeLeader(idVolunteer, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testDegradeLeaderNotFound() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(idVolunteer, VolunteerRole.VOLUNTEER)).thenReturn(false);
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = volunteerController.degradeLeader(idVolunteer, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDegradeLeader() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);
        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(idVolunteer, VolunteerRole.VOLUNTEER)).thenReturn(false);
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.of(volunteer));

        ResponseEntity<Void> response = volunteerController.degradeLeader(idVolunteer, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).degradeLeader(idVolunteer);
    }

    @Test
    public void testDeleteVolunteerForbidden() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(false);

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testDeleteVolunteerNotFound() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteVolunteer() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(1L);
        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.existsByVolunteerIdAndRole(request.adminId(), VolunteerRole.ADMIN)).thenReturn(true);
        when(volunteerRepository.findById(idVolunteer)).thenReturn(Optional.of(volunteer));

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerRepository, times(1)).deleteById(idVolunteer);
    }
}
