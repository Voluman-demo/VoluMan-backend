package com.example.demo.Volunteer;

import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Volunteer.VolunteerDto.AdminRequest;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Preferences.Preferences;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerControllerTest {
    @Mock
    private LogService logService;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private VolunteerService volunteerService;

    @InjectMocks
    private VolunteerController volunteerController;

    @Test
    public void testGetVolunteers_ReturnsOk_WhenVolunteersFound() {
       when(volunteerRepository.findAll()).thenReturn(List.of(new Volunteer()));


        ResponseEntity<List<Volunteer>> response = volunteerController.getVolunteers();


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }


    @Test
    public void testAddVolunteer_ReturnsCreated_WhenVolunteerCreated() {
        PersonalData details = new PersonalData();
        ID newId = new ID(1);

        when(volunteerService.createAndEditVolunteer(details)).thenReturn(newId);

        ResponseEntity<?> response = volunteerController.addVolunteer(details);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newId, response.getBody());
    }

    @Test
    public void testAddVolunteer_ReturnsInternalServerError_WhenCreationFails() {
        PersonalData details = new PersonalData();

        when(volunteerService.createAndEditVolunteer(details)).thenReturn(null);

        ResponseEntity<?> response = volunteerController.addVolunteer(details);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetVolunteer_ReturnsOk_WhenVolunteerFound() {
        ID idVolunteer = new ID(1);
        Volunteer volunteer = new Volunteer();

        when(volunteerService.getVolunteerById(idVolunteer)).thenReturn(volunteer);

        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(volunteer, response.getBody());
    }

    @Test
    public void testGetVolunteer_ReturnsNotFound_WhenVolunteerNotFound() {
        ID idVolunteer = new ID(1);

        when(volunteerService.getVolunteerById(idVolunteer)).thenReturn(null);

        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testSetAvailabilities_ReturnsOk_WhenAvailabilitiesSetSuccessfully() {
        ID volunteerId = new ID(1);
        List<Availability> availabilities = List.of(new Availability()); // Example availability

        when(volunteerService.setAvailabilities(volunteerId, availabilities)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availabilities);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).setAvailabilities(volunteerId, availabilities);
    }

    @Test
    public void testSetAvailabilities_ReturnsNotFound_WhenVolunteerNotFound() {
        ID volunteerId = new ID(1);
        List<Availability> availabilities = List.of(new Availability());

        when(volunteerService.setAvailabilities(volunteerId, availabilities)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availabilities);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerService, times(1)).setAvailabilities(volunteerId, availabilities);
    }

    @Test
    public void testGetAvailabilities_ReturnsOk_WhenAvailabilitiesFound() {
        // Given
        ID volunteerId = new ID(1);
        ArrayList<Availability> availabilities = new ArrayList<>();
        availabilities.add(new Availability());

        // When
        when(volunteerService.getAvailabilities(volunteerId)).thenReturn(availabilities);

        ResponseEntity<List<Availability>> response = volunteerController.getAvailabilities(volunteerId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(availabilities, response.getBody());
        verify(volunteerService, times(1)).getAvailabilities(volunteerId);
    }

    @Test
    public void testGetAvailabilities_ReturnsNotFound_WhenAvailabilitiesNotFound() {
        // Given
        ID volunteerId = new ID(1);

        // When
        when(volunteerService.getAvailabilities(volunteerId)).thenReturn(null);

        ResponseEntity<List<Availability>> response = volunteerController.getAvailabilities(volunteerId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(volunteerService, times(1)).getAvailabilities(volunteerId);
    }



    @Test
    public void testDeleteVolunteer_ReturnsForbidden_WhenNotAdmin() {
        ID idVolunteer = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2));

        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(false);

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testDeleteVolunteer_ReturnsNotFound_WhenVolunteerNotExists() {
        ID idVolunteer = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2));

        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
        when(volunteerService.deleteVolunteer(idVolunteer)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteVolunteer_ReturnsOk_WhenVolunteerDeleted() {
        ID idVolunteer = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2));


        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
        when(volunteerService.deleteVolunteer(idVolunteer)).thenReturn(Errors.SUCCESS);


        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);


        assertEquals(HttpStatus.OK, response.getStatusCode());


        verify(logService, times(1)).logVolunteer(
                null,
                EventType.DELETE,
                "Volunteer deleted by admin with id: " + request.adminId()
        );
    }


    @Test
    public void testUpdateVolunteerDetails_ReturnsOk_WhenDetailsUpdated() {
        ID idVolunteer = new ID(1);
        PersonalData details = new PersonalData();

        when(volunteerService.editVolunteer(idVolunteer, details)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(idVolunteer, details);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateVolunteerDetails_ReturnsNotFound_WhenVolunteerNotFound() {
        ID idVolunteer = new ID(1);
        PersonalData details = new PersonalData();

        when(volunteerService.editVolunteer(idVolunteer, details)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(idVolunteer, details);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }



    @Test
    public void testAssignDuty_ReturnsOk_WhenDutyAssigned() {
        ID idVolunteer = new ID(1);
        Duty duty = new Duty();

        when(volunteerService.assignDuty(idVolunteer, duty)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Void> response = volunteerController.assignDuty(idVolunteer, duty);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAssignDuty_ReturnsNotFound_WhenVolunteerNotFound() {
        ID idVolunteer = new ID(1);
        Duty duty = new Duty();

        when(volunteerService.assignDuty(idVolunteer, duty)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = volunteerController.assignDuty(idVolunteer, duty);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetDuties_ReturnsOk_WhenDutiesFound() {
        ID idVolunteer = new ID(1);
        List<Duty> duties = List.of(new Duty());

        when(volunteerService.getDuties(idVolunteer)).thenReturn(new ArrayList<>(duties));

        ResponseEntity<List<Duty>> response = volunteerController.getDuties(idVolunteer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(duties, response.getBody());
    }

    @Test
    public void testGetDuties_ReturnsNotFound_WhenNoDutiesFound() {
        ID idVolunteer = new ID(1);

        when(volunteerService.getDuties(idVolunteer)).thenReturn(null);

        ResponseEntity<List<Duty>> response = volunteerController.getDuties(idVolunteer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testChangeRole_ReturnsForbidden_WhenRequesterNotAdmin() {
        // Arrange
        ID volunteerId = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2)); // Admin ID: 2
        String role = "VOLUNTEER";

        // Mock that the admin requester is not an admin
        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = volunteerController.changeRole(volunteerId, request, role);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByIdAndPosition(request.adminId(), Position.ADMIN);
        verifyNoInteractions(volunteerService); // Ensure service method was not called
    }

    @Test
    public void testChangeRole_ReturnsNotFound_WhenVolunteerDoesNotExist() {
        // Arrange
        ID volunteerId = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2)); // Admin ID: 2
        String role = "VOLUNTEER";

        // Mock that the admin requester is an admin
        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);

        // Mock that the volunteer does not exist
        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = volunteerController.changeRole(volunteerId, request, role);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByIdAndPosition(request.adminId(), Position.ADMIN);
        verify(volunteerRepository, times(1)).existsById(volunteerId);
        verifyNoInteractions(volunteerService); // Ensure service method was not called
    }

    @Test
    public void testChangeRole_ReturnsOk_WhenRoleChangedSuccessfully() {
        // Arrange
        ID volunteerId = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2)); // Admin ID: 2
        String role = "LEADER";

        // Mock that the admin requester is an admin
        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);

        // Mock that the volunteer exists
        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);

        // Mock the successful role change
        when(volunteerService.assignPosition(volunteerId, Position.valueOf(role))).thenReturn(Errors.SUCCESS);

        // Act
        ResponseEntity<Void> response = volunteerController.changeRole(volunteerId, request, role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByIdAndPosition(request.adminId(), Position.ADMIN);
        verify(volunteerRepository, times(1)).existsById(volunteerId);
        verify(volunteerService, times(1)).assignPosition(volunteerId, Position.valueOf(role));
        verify(logService, times(1)).logVolunteer(
                null,
                EventType.UPDATE,
                "Promoted by admin with id: " + request.adminId()
        );
    }

    @Test
    public void testChangeRole_ReturnsNotFound_WhenAssignRoleFails() {
        // Arrange
        ID volunteerId = new ID(1);
        AdminRequest request = new AdminRequest(new ID(2)); // Admin ID: 2
        String role = "LEADER";

        // Mock that the admin requester is an admin
        when(volunteerRepository.existsByIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);

        // Mock that the volunteer exists
        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);

        // Mock a failure in assigning the role
        when(volunteerService.assignPosition(volunteerId, Position.valueOf(role))).thenReturn(Errors.NOT_FOUND);

        // Act
        ResponseEntity<Void> response = volunteerController.changeRole(volunteerId, request, role);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByIdAndPosition(request.adminId(), Position.ADMIN);
        verify(volunteerRepository, times(1)).existsById(volunteerId);
        verify(volunteerService, times(1)).assignPosition(volunteerId, Position.valueOf(role));
        verifyNoInteractions(logService); // Ensure no logs are created
    }

}
