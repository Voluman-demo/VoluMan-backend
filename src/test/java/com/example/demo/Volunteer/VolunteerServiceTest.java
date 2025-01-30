package com.example.demo.Volunteer;

import com.example.demo.Action.ActionRepository;
import com.example.demo.Action.ActionService;
import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Position.PositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ActionService actionService;
    @Mock
    private ActionRepository actionRepository;

    @Mock
    private PositionService positionService;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize a sample volunteer
        volunteer = new Volunteer();
        volunteer.setVolunteerId(1L);
        volunteer.setFirstName("John");
        volunteer.setLastName("Doe");

        when(actionRepository.findAll()).thenReturn(Collections.emptyList());
    }


    @Test
    void testCreateVolunteer_Success() {
        // Mock the save operation to simulate saving and returning a volunteer with an ID
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setVolunteerId(1L); // Assign a mock ID to the volunteer
            return v;
        });

        // Call the service method
        Long id = volunteerService.createVolunteer();

        // Assert that the ID is not null and matches the expected value
        assertNotNull(id);
        assertEquals(1, id);

        // Verify that the repository's save method was called exactly once
        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
    }

    @Test
    void testEditVolunteer_Success() {

        VolunteerRequest details = new VolunteerRequest();
        details.setFirstName("Jane");
        details.setLastName("Smith");
        details.setEmail("jane.smith@example.com");

        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(volunteer));


        Errors result = volunteerService.editVolunteer(1L, details);


        assertEquals(Errors.SUCCESS, result);
        assertEquals("Jane", volunteer.getFirstName());
        assertEquals("Smith", volunteer.getLastName());
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testEditVolunteer_NotFound() {
        VolunteerRequest details = new VolunteerRequest();
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());


        Errors result = volunteerService.editVolunteer(1L, details);


        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testDeleteVolunteer_Success() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.deleteVolunteer(1L);

        assertEquals(Errors.SUCCESS, result);
        assertFalse(volunteer.isValid());
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testDeleteVolunteer_NotFound() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Errors result = volunteerService.deleteVolunteer(1L);

        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testSetAvailabilities_Success() {
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(new Availability());

        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.setAvailabilities(1L, availabilities);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(availabilities, volunteer.getAvailabilities());
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testSetAvailabilities_NotFound() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        List<Availability> availabilities = new ArrayList<>();
        Errors result = volunteerService.setAvailabilities(1L, availabilities);

        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testAssignPosition_Success() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.assignPosition(1L, Position.LEADER);

        assertEquals(Errors.SUCCESS, result);
        verify(positionService, times(1)).assignRole(volunteer, Position.LEADER);
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testAssignPosition_NotFound() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Errors result = volunteerService.assignPosition(1L, Position.LEADER);

        assertEquals(Errors.NOT_FOUND, result);
        verify(positionService, never()).assignRole(any(Volunteer.class), any(Position.class));
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testGetAvailabilities_Success() {
        ArrayList<Availability> availabilities = new ArrayList<>();
        availabilities.add(new Availability());
        volunteer.setAvailabilities(availabilities);

        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(volunteer));

        List<Availability> result = volunteerService.getAvailabilities(1L);

        assertEquals(availabilities, result);
        verify(volunteerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAvailabilities_NotFound() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        List<Availability> result = volunteerService.getAvailabilities(1L);

        assertNull(result);
        verify(volunteerRepository, times(1)).findById(1L);
    }
    // Test: createAndEditVolunteer
    @Test
    void  testCreateAndEditVolunteer_Success() {
        // Given: Set up a PersonalData object and mock repository behavior
        VolunteerRequest details = new VolunteerRequest();
        details.setFirstName("John");
        details.setLastName("Doe");
        details.setEmail("john.doe@example.com");

        // Mock the behavior of saving a new volunteer
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setVolunteerId(1L); // Assign a mock ID to the volunteer
            return v;
        });

        // Mock the behavior of finding a volunteer by ID
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(new Volunteer()));

        // When: Call the service method
        Long result = volunteerService.createAndEditVolunteer(details);

        // Then: Assert that the result is not null and matches the expected ID
        assertNotNull(result);
        assertEquals(1, result);

        // Verify that the repository's save method was called twice
        // (once during creation and once during editing)
        verify(volunteerRepository, times(2)).save(any(Volunteer.class));
    }


    @Test
    void testCreateAndEditVolunteer_FailsToEdit() {
        // Given: Set up a PersonalData object
        VolunteerRequest details = new VolunteerRequest();

        // Mock the behavior of saving a new volunteer
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setVolunteerId(1L); // Assign a mock ID to the volunteer
            return v;
        });

        // Mock the behavior of not finding the volunteer by Long
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // When: Call the service method
        Long result = volunteerService.createAndEditVolunteer(details);

        // Then: Assert that the result is null (indicating a failure to edit)
        assertNull(result);

        // Verify that the repository's save method was called only once (for creation)
        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
    }



    @Test
    void testExistsVolunteerByEmail_ReturnsTrue() {
        // Given
        String email = "test@example.com";
        when(volunteerRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = volunteerService.existsVolunteerByEmail(email);


        assertTrue(result);
        verify(volunteerRepository, times(1)).existsByEmail(email);
    }

    @Test
    void testExistsVolunteerByEmail_ReturnsFalse() {

        String email = "test@example.com";
        when(volunteerRepository.existsByEmail(email)).thenReturn(false);


        boolean result = volunteerService.existsVolunteerByEmail(email);


        assertFalse(result);
        verify(volunteerRepository, times(1)).existsByEmail(email);
    }
//    @Test
//    void testAssignDuty_Success() {
//        Long volunteerId = 1L;
//        Duty duty = new Duty();
//        Volunteer volunteer = new Volunteer();
//        volunteer.setDuties(new HashSet<>());
//
//        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
//
//        Errors result = volunteerService.assignDuty(volunteerId, duty);
//
//        assertEquals(Errors.SUCCESS, result);
//        assertTrue(volunteer.getDuties().contains(duty));
//        verify(volunteerRepository, times(1)).save(volunteer);
//    }
//
//    @Test
//    void testAssignDuty_NotFound() {
//        Long volunteerId = 1L;
//        Duty duty = new Duty();
//
//        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());
//
//        Errors result = volunteerService.assignDuty(volunteerId, duty);
//        sitory, never()).save(any(Volunteer.class));
//    }
//
//
//
//
//
//    assertEquals(Errors.NOT_FOUND, result);
//        verify(volunteerRepo@Test
//    void testGetDuties_Success() {
//        Long volunteerId = 1L;
//        Set<Duty> duties = Set.of(new Duty());
//        Volunteer volunteer = new Volunteer();
//        volunteer.setDuties(duties);
//
//        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
//
//        ArrayList<Duty> result = volunteerService.getDuties(volunteerId);
//
//        assertNotNull(result);
//        assertEquals(new ArrayList<>(duties), result);
//        verify(volunteerRepository, times(1)).findById(volunteerId);
//    }
//
//    @Test
//    void testGetDuties_NotFound() {
//        Long volunteerId = 1L;
//
//        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());
//
//        ArrayList<Duty> result = volunteerService.getDuties(volunteerId);
//
//        assertNull(result);
//        verify(volunteerRepository, times(1)).findById(volunteerId);
//    }
    @Test
    void testGetPosition_Success() {
        // Given
        Long volunteerId = 1L;
        Volunteer volunteer = new Volunteer();
        volunteer.setPosition(Position.LEADER);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Position result = volunteerService.getPosition(volunteerId);

        // Then
        assertNotNull(result);
        assertEquals(Position.LEADER, result);
        verify(volunteerRepository, times(1)).findById(volunteerId);
    }

    @Test
    void testGetPosition_NotFound() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Position result = volunteerService.getPosition(volunteerId);

        // Then
        assertNull(result);
        verify(volunteerRepository, times(1)).findById(volunteerId);
    }

}