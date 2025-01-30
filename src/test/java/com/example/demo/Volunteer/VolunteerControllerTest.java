//package com.example.demo.Volunteer;
//
//import com.example.demo.Log.EventType;
//import com.example.demo.Log.LogService;
//import com.example.demo.Volunteer.VolunteerDto.AdminRequest;
//
//import com.example.demo.Model.Errors;
//import com.example.demo.Volunteer.Availability.Availability;
//import com.example.demo.Volunteer.Position.Position;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class VolunteerControllerTest {
//    @Mock
//    private LogService logService;
//
//    @Mock
//    private VolunteerRepository volunteerRepository;
//
//    @Mock
//    private VolunteerService volunteerService;
//
//    @InjectMocks
//    private VolunteerController volunteerController;
//
//    @Test
//    public void testGetVolunteers_ReturnsOk_WhenVolunteersFound() {
//        when(volunteerRepository.findAll()).thenReturn(List.of(new Volunteer()));
//
//
//        ResponseEntity<List<Volunteer>> response = volunteerController.getVolunteers(null);
//
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertFalse(response.getBody().isEmpty());
//    }
//
//
//    @Test
//    public void testAddVolunteer_ReturnsCreated_WhenVolunteerCreated() {
//        VolunteerRequest details = new VolunteerRequest();
//        Long newId = 1L;
//
//        when(volunteerService.createAndEditVolunteer(details)).thenReturn(newId);
//
//        ResponseEntity<?> response = volunteerController.addVolunteer(details);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(newId, response.getBody());
//    }
//
//    @Test
//    public void testAddVolunteer_ReturnsInternalServerError_WhenCreationFails() {
//        VolunteerRequest details = new VolunteerRequest();
//
//        when(volunteerService.createAndEditVolunteer(details)).thenReturn(null);
//
//        ResponseEntity<?> response = volunteerController.addVolunteer(details);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//    }
//
//    @Test
//    public void testGetVolunteer_ReturnsOk_WhenVolunteerFound() {
//        Long idVolunteer = 1L;
//        Volunteer volunteer = new Volunteer();
//
//
//        when(volunteerRepository.existsById(eq(idVolunteer))).thenReturn(true);
//        when(volunteerService.getVolunteerById(idVolunteer)).thenReturn(volunteer);
//
//        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(volunteer, response.getBody());
//    }
//
//    @Test
//    public void testGetVolunteer_ReturnsNotFound_WhenVolunteerNotFound() {
//        Long idVolunteer = 1L;
//
//        when(volunteerRepository.existsById(eq(idVolunteer))).thenReturn(true);
//        when(volunteerService.getVolunteerById(idVolunteer)).thenReturn(null);
//
//        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//    @Test
//    public void testSetAvailabilities_ReturnsOk_WhenAvailabilitiesSetSuccessfully() {
//        Long volunteerId = 1L;
//        SetAvailRequest availRequest = new SetAvailRequest();
//        availRequest.availabilities.add(new Availability()); // Example availability
//
//        when(volunteerService.setAvailabilities(volunteerId, availRequest.availabilities)).thenReturn(Errors.SUCCESS);
//
//        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        verify(volunteerService, times(1)).setAvailabilities(volunteerId, availRequest.availabilities);
//    }
//
//    @Test
//    public void testSetAvailabilities_ReturnsNotFound_WhenVolunteerNotFound() {
//        Long volunteerId = 1L;
//        SetAvailRequest availRequest = new SetAvailRequest();
//        availRequest.availabilities.add(new Availability());
//
//        when(volunteerService.setAvailabilities(volunteerId, availRequest.getAvailabilities())).thenReturn(Errors.NOT_FOUND);
//
//        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availRequest);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(volunteerService, times(1)).setAvailabilities(volunteerId, availRequest.getAvailabilities());
//    }
//
//    @Test
//    public void testGetAvailabilities_ReturnsOk_WhenAvailabilitiesFound() {
//        // Given
//        Long volunteerId = 1L;
//        ArrayList<Availability> availabilities = new ArrayList<>();
//        availabilities.add(new Availability());
//
//        // When
//        when(volunteerService.getAvailabilities(volunteerId)).thenReturn(availabilities);
//
//        ResponseEntity<List<Availability>> response = volunteerController.getAvailabilities(volunteerId);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertFalse(response.getBody().isEmpty());
//        assertEquals(availabilities, response.getBody());
//        verify(volunteerService, times(1)).getAvailabilities(volunteerId);
//    }
//
//    @Test
//    public void testGetAvailabilities_ReturnsNotFound_WhenAvailabilitiesNotFound() {
//        // Given
//        Long volunteerId = 1L;
//
//        // When
//        when(volunteerService.getAvailabilities(volunteerId)).thenReturn(null);
//
//        ResponseEntity<List<Availability>> response = volunteerController.getAvailabilities(volunteerId);
//
//        // Then
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(volunteerService, times(1)).getAvailabilities(volunteerId);
//    }
//
//
//
//    @Test
//    public void testDeleteVolunteer_ReturnsForbidden_WhenNotAdmin() {
//        Long idVolunteer = 1L;
//        AdminRequest request = new AdminRequest(2L);
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(false);
//
//        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);
//
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    public void testDeleteVolunteer_ReturnsNotFound_WhenVolunteerNotExists() {
//        Long idVolunteer = 1L;
//        AdminRequest request = new AdminRequest(2L);
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
//        when(volunteerService.deleteVolunteer(idVolunteer)).thenReturn(Errors.NOT_FOUND);
//
//        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    public void testDeleteVolunteer_ReturnsOk_WhenVolunteerDeleted() {
//        Long idVolunteer = 1L;
//        AdminRequest request = new AdminRequest(2L);
//
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
//        when(volunteerService.deleteVolunteer(idVolunteer)).thenReturn(Errors.SUCCESS);
//
//
//        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);
//
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//
//        verify(logService, times(1)).logVolunteer(
//                null,
//                EventType.DELETE,
//                "Volunteer deleted by admin with id: " + request.adminId()
//        );
//    }
//
//
//    @Test
//    public void testUpdateVolunteerDetails_ReturnsOk_WhenDetailsUpdated() {
//        Long idVolunteer = 1L;
//        VolunteerRequest details = new VolunteerRequest();
//
//        when(volunteerService.editVolunteer(idVolunteer, details)).thenReturn(Errors.SUCCESS);
//
//        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(idVolunteer, details);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//    @Test
//    public void testUpdateVolunteerDetails_ReturnsNotFound_WhenVolunteerNotFound() {
//        Long idVolunteer = 1L;
//        VolunteerRequest details = new VolunteerRequest();
//
//        when(volunteerService.editVolunteer(idVolunteer, details)).thenReturn(Errors.NOT_FOUND);
//
//        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(idVolunteer, details);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//
////
////    @Test
////    public void testAssignDuty_ReturnsOk_WhenDutyAssigned() {
////        Long idVolunteer = 1L;
////        Duty duty = new Duty();
////
////        when(volunteerService.assignDuty(idVolunteer, duty)).thenReturn(Errors.SUCCESS);
////
////        ResponseEntity<Void> response = volunteerController.assignDuty(idVolunteer, duty);
////
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////    }
////
////    @Test
////    public void testAssignDuty_ReturnsNotFound_WhenVolunteerNotFound() {
////        Long idVolunteer = 1L;
////        Duty duty = new Duty();
////
////        when(volunteerService.assignDuty(idVolunteer, duty)).thenReturn(Errors.NOT_FOUND);
////
////        ResponseEntity<Void> response = volunteerController.assignDuty(idVolunteer, duty);
////
////        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
////    }
////
////    @Test
////    public void testGetDuties_ReturnsOk_WhenDutiesFound() {
////        Long idVolunteer = 1L;
////        List<Duty> duties = List.of(new Duty());
////
////        when(volunteerService.getDuties(idVolunteer)).thenReturn(new ArrayList<>(duties));
////
////        ResponseEntity<List<Duty>> response = volunteerController.getDuties(idVolunteer);
////
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        assertEquals(duties, response.getBody());
////    }
////
////    @Test
////    public void testGetDuties_ReturnsNotFound_WhenNoDutiesFound() {
////        Long idVolunteer = 1L;
////
////        when(volunteerService.getDuties(idVolunteer)).thenReturn(null);
////
////        ResponseEntity<List<Duty>> response = volunteerController.getDuties(idVolunteer);
////
////        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
////    }
//    @Test
//    public void testassignPosition_ReturnsForbidden_WhenRequesterNotAdmin() {
//        // Arrange
//        Long volunteerId = 1L;
//        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
//        String role = "VOLUNTEER";
//
//        // Mock that the admin requester is not an admin
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(false);
//
//        // Act
//        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
//        verifyNoInteractions(volunteerService); // Ensure service method was not called
//    }
//
//    @Test
//    public void testassignPosition_ReturnsNotFound_WhenVolunteerDoesNotExist() {
//        // Arrange
//        Long volunteerId = 1L;
//        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
//        String role = "VOLUNTEER";
//
//        // Mock that the admin requester is an admin
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
//
//        // Mock that the volunteer does not exist
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);
//
//        // Act
//        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
//        verify(volunteerRepository, times(1)).existsById(volunteerId);
//        verifyNoInteractions(volunteerService); // Ensure service method was not called
//    }
//
//    @Test
//    public void testChangeRole_ReturnsOk_WhenPositionChangedSuccessfully() {
//        // Arrange
//        Long volunteerId = 1L;
//        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
//        String role = "LEADER";
//
//        // Mock that the admin requester is an admin
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
//
//        // Mock that the volunteer exists
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//
//        // Mock the successful role change
//        when(volunteerService.assignPosition(volunteerId, Position.valueOf(role))).thenReturn(Errors.SUCCESS);
//
//        // Act
//        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);
//
//        // Assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
//        verify(volunteerRepository, times(1)).existsById(volunteerId);
//        verify(volunteerService, times(1)).assignPosition(volunteerId, Position.valueOf(role));
//        verify(logService, times(1)).logVolunteer(
//                null,
//                EventType.UPDATE,
//                "Promoted by admin with id: " + request.adminId()
//        );
//    }
//
//    @Test
//    public void testChangeRole_ReturnsNotFound_WhenAssignPositionFails() {
//        // Arrange
//        Long volunteerId = 1L;
//        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
//        String role = "LEADER";
//
//        // Mock that the admin requester is an admin
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
//
//        // Mock that the volunteer exists
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//
//        // Mock a failure in assigning the role
//        when(volunteerService.assignPosition(volunteerId, Position.valueOf(role))).thenReturn(Errors.NOT_FOUND);
//
//        // Act
//        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
//        verify(volunteerRepository, times(1)).existsById(volunteerId);
//        verify(volunteerService, times(1)).assignPosition(volunteerId, Position.valueOf(role));
//        verifyNoInteractions(logService); // Ensure no logs are created
//    }
//
//}