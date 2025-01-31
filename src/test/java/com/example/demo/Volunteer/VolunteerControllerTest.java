package com.example.demo.Volunteer;

import com.example.demo.Action.Lang;
import com.example.demo.Log.EventType;
import com.example.demo.Log.LogService;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.AvailabilityRequest;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.SetAvailRequest;
import com.example.demo.Volunteer.VolunteerDto.AdminRequest;

import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.VolunteerDto.VolunteerRequest;
import com.example.demo.Volunteer.VolunteerDto.WeeklyHoursRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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


        ResponseEntity<List<Volunteer>> response = volunteerController.getVolunteers(null);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }


    @Test
    public void testAddVolunteer_ReturnsCreated_WhenVolunteerCreated() {
        VolunteerRequest details = new VolunteerRequest();
        Long newId = 1L;

        when(volunteerService.createAndEditVolunteer(details)).thenReturn(newId);

        ResponseEntity<?> response = volunteerController.addVolunteer(details);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newId, response.getBody());
    }

    @Test
    public void testAddVolunteer_ReturnsInternalServerError_WhenCreationFails() {
        VolunteerRequest details = new VolunteerRequest();

        when(volunteerService.createAndEditVolunteer(details)).thenReturn(null);

        ResponseEntity<?> response = volunteerController.addVolunteer(details);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetVolunteer_ReturnsOk_WhenVolunteerFound() {
        Long idVolunteer = 1L;
        Volunteer volunteer = new Volunteer();


        when(volunteerRepository.existsById(eq(idVolunteer))).thenReturn(true);
        when(volunteerService.getVolunteerById(idVolunteer)).thenReturn(volunteer);

        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(volunteer, response.getBody());
    }

    @Test
    public void testGetVolunteer_ReturnsNotFound_WhenVolunteerNotFound() {
        Long idVolunteer = 1L;

        when(volunteerRepository.existsById(eq(idVolunteer))).thenReturn(true);
        when(volunteerService.getVolunteerById(idVolunteer)).thenReturn(null);

        ResponseEntity<Volunteer> response = volunteerController.getVolunteer(idVolunteer);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testSetAvailabilities_ReturnsOk_WhenAvailabilitiesSetSuccessfully() {
        // Given
        Long volunteerId = 1L;

        SetAvailRequest availRequest = new SetAvailRequest();
        availRequest.setAvailabilities(new ArrayList<>()); // Ensure availabilities list is set

        List<Availability> availabilityList = availRequest.getAvailabilities().stream()
                .map(request -> volunteerService.convertToAvailability(request, volunteerId))
                .toList();

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
        when(volunteerService.setAvailabilities(volunteerId, availabilityList)).thenReturn(Errors.SUCCESS);

        // When
        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).setAvailabilities(volunteerId, availabilityList);
    }
    @Test
    public void testSetAvailabilities_ReturnsForbidden_WhenVolunteerIsCandidate() {
        // Given
        Long volunteerId = 1L;
        SetAvailRequest availRequest = new SetAvailRequest();
        availRequest.setAvailabilities(new ArrayList<>());

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(true);

        // When
        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerService, never()).setAvailabilities(anyLong(), anyList());
    }

    @Test
    public void testSetAvailabilities_ReturnsNotFound_WhenServiceFails() {
        // Given
        Long volunteerId = 1L;
        SetAvailRequest availRequest = new SetAvailRequest();
        availRequest.setAvailabilities(new ArrayList<>());

        List<Availability> availabilityList = availRequest.getAvailabilities().stream()
                .map(request -> volunteerService.convertToAvailability(request, volunteerId))
                .toList();

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
        when(volunteerService.setAvailabilities(volunteerId, availabilityList)).thenReturn(Errors.NOT_FOUND);

        // When
        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerService, times(1)).setAvailabilities(volunteerId, availabilityList);
    }


    @Test
    public void testSetAvailabilities_ReturnsForbidden_WhenVolunteerNotExists() {
        // Given
        Long volunteerId = 1L;
        SetAvailRequest availRequest = new SetAvailRequest();
        availRequest.setAvailabilities(new ArrayList<>());

        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        // When
        ResponseEntity<Void> response = volunteerController.setAvailabilities(volunteerId, availRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerService, never()).setAvailabilities(anyLong(), anyList());
    }

    @Test
    public void testGetAvailabilities_ReturnsOk_WhenAvailabilitiesFound() {
        // Given
        Long volunteerId = 1L;
        ArrayList<Availability> availabilities = new ArrayList<>();
        availabilities.add(new Availability());
        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
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
        Long volunteerId = 1L;
        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
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
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(2L);

        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(false);

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    @Test
    public void testGetAvailabilities_ReturnsForbidden_WhenVolunteerIsCandidate() {
        // Given
        Long volunteerId = 1L;


        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);


        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(true);

        // When
        ResponseEntity<List<Availability>> response = volunteerController.getAvailabilities(volunteerId);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());


        verify(volunteerService, never()).getAvailabilities(any());
    }


    @Test
    public void testDeleteVolunteer_ReturnsNotFound_WhenVolunteerNotExists() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(2L);

        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
        when(volunteerService.deleteVolunteer(idVolunteer)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = volunteerController.deleteVolunteer(idVolunteer, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteVolunteer_ReturnsOk_WhenVolunteerDeleted() {
        Long idVolunteer = 1L;
        AdminRequest request = new AdminRequest(2L);


        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);
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
        // Given
        Long volunteerId = 1L;
        VolunteerRequest details = new VolunteerRequest();

        // Simulate that the volunteer exists
        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);

        // Simulate successful update
        when(volunteerService.editVolunteerDetails(volunteerId, details)).thenReturn(Errors.SUCCESS);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(volunteerId, details);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).editVolunteerDetails(volunteerId, details);
    }




    @Test
    public void testassignPosition_ReturnsForbidden_WhenRequesterNotAdmin() {
        // Arrange
        Long volunteerId = 1L;
        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
        String role = "VOLUNTEER";

        // Mock that the admin requester is not an admin
        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
        verifyNoInteractions(volunteerService); // Ensure service method was not called
    }

    @Test
    public void testassignPosition_ReturnsNotFound_WhenVolunteerDoesNotExist() {
        // Arrange
        Long volunteerId = 1L;
        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
        String role = "VOLUNTEER";


        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);


        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);


        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
        verify(volunteerRepository, times(1)).existsById(volunteerId);
        verifyNoInteractions(volunteerService); // Ensure service method was not called
    }

    @Test
    public void testChangeRole_ReturnsOk_WhenPositionChangedSuccessfully() {

        Long volunteerId = 1L;
        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
        String role = "LEADER";


        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);


        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);


        when(volunteerService.assignPosition(volunteerId, Position.valueOf(role))).thenReturn(Errors.SUCCESS);


        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
        verify(volunteerRepository, times(1)).existsById(volunteerId);
        verify(volunteerService, times(1)).assignPosition(volunteerId, Position.valueOf(role));
        verify(logService, times(1)).logVolunteer(
                null,
                EventType.UPDATE,
                "Promoted by admin with id: " + request.adminId()
        );
    }

    @Test
    public void testChangeRole_ReturnsNotFound_WhenAssignPositionFails() {

        Long volunteerId = 1L;
        AdminRequest request = new AdminRequest(2L); // Admin Long: 2
        String role = "LEADER";


        when(volunteerRepository.existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN)).thenReturn(true);


        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);


        when(volunteerService.assignPosition(volunteerId, Position.valueOf(role))).thenReturn(Errors.NOT_FOUND);


        ResponseEntity<Void> response = volunteerController.assignPosition(volunteerId, request, role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerRepository, times(1)).existsByVolunteerIdAndPosition(request.adminId(), Position.ADMIN);
        verify(volunteerRepository, times(1)).existsById(volunteerId);
        verify(volunteerService, times(1)).assignPosition(volunteerId, Position.valueOf(role));
        verifyNoInteractions(logService); // Ensure no logs are created
    }
    @Test
    public void testGetLimitOfWeeklyHours_ReturnsOk_WhenVolunteerExists() {
        // Given
        Long volunteerId = 1L;
        double weeklyHoursLimit = 20.0;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
        when(volunteerService.getLimitOfWeeklyHours(volunteerId)).thenReturn(weeklyHoursLimit);

        // When
        ResponseEntity<Double> response = volunteerController.getLimitOfWeeklyHours(volunteerId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(weeklyHoursLimit, response.getBody());
        verify(volunteerService, times(1)).getLimitOfWeeklyHours(volunteerId);
    }

    @Test
    public void testGetLimitOfWeeklyHours_ReturnsForbidden_WhenVolunteerIsCandidate() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(true);

        // When
        ResponseEntity<Double> response = volunteerController.getLimitOfWeeklyHours(volunteerId);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());


        verify(volunteerService, never()).getLimitOfWeeklyHours(anyLong());
    }
    @Test
    public void testGetLimitOfWeeklyHours_ReturnsForbidden_WhenVolunteerNotExists() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        // When
        ResponseEntity<Double> response = volunteerController.getLimitOfWeeklyHours(volunteerId);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        verify(volunteerService, never()).getLimitOfWeeklyHours(anyLong());
    }
    @Test
    public void testGetLimitOfWeeklyHours_ReturnsNotFound_WhenLimitNotSet() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
        when(volunteerService.getLimitOfWeeklyHours(volunteerId)).thenReturn(null); // Limit not set

        // When
        ResponseEntity<Double> response = volunteerController.getLimitOfWeeklyHours(volunteerId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(volunteerService, times(1)).getLimitOfWeeklyHours(volunteerId);
    }
    @Test
    public void testGetPersData_ReturnsOk_WhenDataIsRetrievedSuccessfully() {
        // Given
        AdminRequest adminRequest = new AdminRequest(1L);
        Position position = Position.VOLUNTEER;
        List<PersonalData> personalDataList = List.of(new PersonalData());

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.ADMIN)).thenReturn(true);
        when(volunteerService.getAllPersData(position)).thenReturn(personalDataList);

        // When
        ResponseEntity<List<PersonalData>> response = volunteerController.getPersData(position, adminRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(personalDataList, response.getBody());
        verify(volunteerService, times(1)).getAllPersData(position);
    }
    @Test
    public void testGetPersData_ReturnsForbidden_WhenNotAdmin() {
        // Given
        AdminRequest adminRequest = new AdminRequest(1L);
        Position position = Position.VOLUNTEER;

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.ADMIN)).thenReturn(false);

        // When
        ResponseEntity<List<PersonalData>> response = volunteerController.getPersData(position, adminRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(volunteerService, never()).getAllPersData(any(Position.class));
    }
    @Test
    public void testGetPersData_ReturnsNotFound_WhenDataIsNull() {
        // Given
        AdminRequest adminRequest = new AdminRequest(1L);
        Position position = Position.VOLUNTEER;

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.ADMIN)).thenReturn(true);
        when(volunteerService.getAllPersData(position)).thenReturn(null);
        // When
        ResponseEntity<List<PersonalData>> response = volunteerController.getPersData(position, adminRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(volunteerService, times(1)).getAllPersData(position);
    }

    @Test
    public void testGetLang_ReturnsOk_WhenLanguageRetrievedSuccessfully() {
        // Given
        Long volunteerId = 1L;
        AdminRequest adminRequest = new AdminRequest(2L);
        Lang expectedLang = Lang.EN;

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.RECRUITER)).thenReturn(true);
        when(volunteerService.getLang(volunteerId)).thenReturn(expectedLang);

        // When
        ResponseEntity<Lang> response = volunteerController.getLang(volunteerId, adminRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedLang, response.getBody());
        verify(volunteerService, times(1)).getLang(volunteerId);
    }
    @Test
    public void testUpdateVolunteerWeeklyHours_ReturnsOk_WhenUpdatedSuccessfully() {
        // Given
        Long volunteerId = 1L;
        WeeklyHoursRequest weeklyHoursRequest = new WeeklyHoursRequest();
        weeklyHoursRequest.setLimitOfWeeklyHours(25.0);

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
        when(volunteerService.editVolunteerWeeklyHours(volunteerId, weeklyHoursRequest.getLimitOfWeeklyHours())).thenReturn(Errors.SUCCESS);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerWeeklyHours(volunteerId, weeklyHoursRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).editVolunteerWeeklyHours(volunteerId, weeklyHoursRequest.getLimitOfWeeklyHours());
    }

    @Test
    public void testUpdateVolunteerWeeklyHours_ReturnsForbidden_WhenVolunteerIsCandidate() {
        // Given
        Long volunteerId = 1L;
        WeeklyHoursRequest weeklyHoursRequest = new WeeklyHoursRequest();
        weeklyHoursRequest.setLimitOfWeeklyHours(25.0);

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(true);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerWeeklyHours(volunteerId, weeklyHoursRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerService, never()).editVolunteerWeeklyHours(anyLong(), anyDouble()); // Ensure service method is not called
    }
    @Test
    public void testUpdateVolunteerWeeklyHours_ReturnsForbidden_WhenVolunteerNotExists() {
        // Given
        Long volunteerId = 1L;
        WeeklyHoursRequest weeklyHoursRequest = new WeeklyHoursRequest();
        weeklyHoursRequest.setLimitOfWeeklyHours(25.0);

        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerWeeklyHours(volunteerId, weeklyHoursRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerService, never()).editVolunteerWeeklyHours(anyLong(), anyDouble());
    }
    @Test
    public void testUpdateVolunteerWeeklyHours_ReturnsNotFound_WhenUpdateFails() {
        // Given
        Long volunteerId = 1L;
        WeeklyHoursRequest weeklyHoursRequest = new WeeklyHoursRequest();
        weeklyHoursRequest.setLimitOfWeeklyHours(25.0);

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(volunteerId, Position.CANDIDATE)).thenReturn(false);
        when(volunteerService.editVolunteerWeeklyHours(volunteerId, weeklyHoursRequest.getLimitOfWeeklyHours())).thenReturn(Errors.NOT_FOUND);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerWeeklyHours(volunteerId, weeklyHoursRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerService, times(1)).editVolunteerWeeklyHours(volunteerId, weeklyHoursRequest.getLimitOfWeeklyHours());
    }



    @Test
    public void testGetLang_ReturnsForbidden_WhenNotRecruiter() {
        // Given
        Long volunteerId = 1L;
        AdminRequest adminRequest = new AdminRequest(2L);

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.RECRUITER)).thenReturn(false);

        // When
        ResponseEntity<Lang> response = volunteerController.getLang(volunteerId, adminRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(volunteerService, never()).getLang(anyLong());
    }
    @Test
    public void testGetLang_ReturnsNotFound_WhenLanguageIsNull() {
        // Given
        Long volunteerId = 1L;
        AdminRequest adminRequest = new AdminRequest(2L);

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.RECRUITER)).thenReturn(true);
        when(volunteerService.getLang(volunteerId)).thenReturn(null);

        // When
        ResponseEntity<Lang> response = volunteerController.getLang(volunteerId, adminRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(volunteerService, times(1)).getLang(volunteerId);
    }

    @Test
    public void testDeleteCandidate_ReturnsOk_WhenDeletedSuccessfully() {
        // Given
        Long volunteerId = 1L;
        AdminRequest adminRequest = new AdminRequest(2L);

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.RECRUITER)).thenReturn(true);
        when(volunteerService.deleteVolunteer(volunteerId)).thenReturn(Errors.SUCCESS);

        // When
        ResponseEntity<Void> response = volunteerController.deleteCandidate(volunteerId, adminRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(volunteerService, times(1)).deleteVolunteer(volunteerId);
        verify(logService, times(1)).logVolunteer(
                null,
                EventType.DELETE,
                "Volunteer deleted by admin with id: " + adminRequest.adminId()
        );
    }
    @Test
    public void testDeleteCandidate_ReturnsForbidden_WhenNotRecruiter() {
        // Given
        Long volunteerId = 1L;
        AdminRequest adminRequest = new AdminRequest(2L);

        when(volunteerRepository.existsByVolunteerIdAndPosition(adminRequest.adminId(), Position.RECRUITER)).thenReturn(false);

        // When
        ResponseEntity<Void> response = volunteerController.deleteCandidate(volunteerId, adminRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerService, never()).deleteVolunteer(anyLong());
        verify(logService, never()).logVolunteer(any(), any(), any());
    }
    @Test
    public void testUpdateVolunteerDetails_ReturnsForbidden_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;
        VolunteerRequest details = new VolunteerRequest();

        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(volunteerId, details);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(volunteerService, never()).editVolunteerDetails(anyLong(), any());
    }
    @Test
    public void testUpdateVolunteerDetails_ReturnsNotFound_WhenEditFails() {
        // Given
        Long volunteerId = 1L;
        VolunteerRequest details = new VolunteerRequest();
        details.setFirstName("Jane");

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(volunteerService.editVolunteerDetails(volunteerId, details)).thenReturn(Errors.FAILURE);

        // When
        ResponseEntity<Void> response = volunteerController.updateVolunteerDetails(volunteerId, details);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(volunteerService, times(1)).editVolunteerDetails(volunteerId, details);
    }


}