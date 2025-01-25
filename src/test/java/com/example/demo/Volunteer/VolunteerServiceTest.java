package com.example.demo.Volunteer;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionService;
import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
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
    private PositionService positionService;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize a sample volunteer
        volunteer = new Volunteer();
        volunteer.setId(new ID(1));
        volunteer.setFirstName("John");
        volunteer.setLastName("Doe");
    }


    @Test
    void testCreateVolunteer_Success() {
        // Mock save operation
        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setId(new ID(1));
            return v;
        });


        ID id = volunteerService.createVolunteer();


        assertNotNull(id);
        assertEquals(1, id.getId());
        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
    }


    @Test
    void testEditVolunteer_Success() {

        PersonalData details = new PersonalData();
        details.setFirstName("Jane");
        details.setLastName("Smith");
        details.setEmail("jane.smith@example.com");

        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.of(volunteer));


        Errors result = volunteerService.editVolunteer(new ID(1), details);


        assertEquals(Errors.SUCCESS, result);
        assertEquals("Jane", volunteer.getFirstName());
        assertEquals("Smith", volunteer.getLastName());
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testEditVolunteer_NotFound() {
        PersonalData details = new PersonalData();
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.empty());


        Errors result = volunteerService.editVolunteer(new ID(1), details);


        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testDeleteVolunteer_Success() {
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.deleteVolunteer(new ID(1));

        assertEquals(Errors.SUCCESS, result);
        assertFalse(volunteer.isValid());
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testDeleteVolunteer_NotFound() {
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.empty());

        Errors result = volunteerService.deleteVolunteer(new ID(1));

        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testSetAvailabilities_Success() {
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(new Availability());

        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.setAvailabilities(new ID(1), availabilities);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(availabilities, volunteer.getAvailabilities());
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testSetAvailabilities_NotFound() {
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.empty());

        List<Availability> availabilities = new ArrayList<>();
        Errors result = volunteerService.setAvailabilities(new ID(1), availabilities);

        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testAssignPosition_Success() {
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.assignPosition(new ID(1), Position.LEADER);

        assertEquals(Errors.SUCCESS, result);
        verify(positionService, times(1)).assignRole(volunteer, Position.LEADER);
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testAssignPosition_NotFound() {
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.empty());

        Errors result = volunteerService.assignPosition(new ID(1), Position.LEADER);

        assertEquals(Errors.NOT_FOUND, result);
        verify(positionService, never()).assignRole(any(Volunteer.class), any(Position.class));
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testGetAvailabilities_Success() {
        ArrayList<Availability> availabilities = new ArrayList<>();
        availabilities.add(new Availability());
        volunteer.setAvailabilities(availabilities);

        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.of(volunteer));

        ArrayList<Availability> result = volunteerService.getAvailabilities(new ID(1));

        assertEquals(availabilities, result);
        verify(volunteerRepository, times(1)).findById(new ID(1));
    }

    @Test
    void testGetAvailabilities_NotFound() {
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.empty());

        ArrayList<Availability> result = volunteerService.getAvailabilities(new ID(1));

        assertNull(result);
        verify(volunteerRepository, times(1)).findById(new ID(1));
    }
    // Test: createAndEditVolunteer
    @Test
    void testCreateAndEditVolunteer_Success() {
        // Given
        PersonalData details = new PersonalData();
        details.setFirstName("John");
        details.setLastName("Doe");
        details.setEmail("john.doe@example.com");

        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setId(new ID(1));
            return v;
        });
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.of(new Volunteer()));

        // When
        ID result = volunteerService.createAndEditVolunteer(details);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(volunteerRepository, times(2)).save(any(Volunteer.class));
    }

    @Test
    void testCreateAndEditVolunteer_FailsToEdit() {
        // Given
        PersonalData details = new PersonalData();

        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setId(new ID(1));
            return v;
        });
        when(volunteerRepository.findById(any(ID.class))).thenReturn(Optional.empty());

        // When
        ID result = volunteerService.createAndEditVolunteer(details);

        // Then
        assertNull(result);
        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
    }

    @Test
    void testInitializePreferences_Success() {//TODO  zmienić potem na action
        // Given
        ID volunteerId = new ID(1);
        Volunteer volunteer = new Volunteer();
        List<Action> allActions = List.of(new Action());

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
        when(actionService.getAllActions()).thenReturn(allActions);

        // When
        Errors result = volunteerService.initializePreferences(volunteerId);

        // Then
        assertEquals(Errors.SUCCESS, result);
        assertEquals(allActions, new ArrayList<>(volunteer.getPreferences().getU()));
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testInitializePreferences_NotFound() {
        // Given
        ID volunteerId = new ID(1);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Errors result = volunteerService.initializePreferences(volunteerId);

        // Then
        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    void testApplyPreferencesToSchedule_Success() {
        // Given
        ID volunteerId = new ID(1);

        // When
        Errors result = volunteerService.applyPreferencesToSchedule(volunteerId);

        // Then
        assertEquals(Errors.SUCCESS, result);
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
    @Test
    void testAssignDuty_Success() {
        ID volunteerId = new ID(1);
        Duty duty = new Duty();
        Volunteer volunteer = new Volunteer();
        volunteer.setDuties(new HashSet<>());

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        Errors result = volunteerService.assignDuty(volunteerId, duty);

        assertEquals(Errors.SUCCESS, result);
        assertTrue(volunteer.getDuties().contains(duty));
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    void testAssignDuty_NotFound() {
        ID volunteerId = new ID(1);
        Duty duty = new Duty();

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        Errors result = volunteerService.assignDuty(volunteerId, duty);

        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }




    @Test
    void testGetDuties_Success() {
        ID volunteerId = new ID(1);
        Set<Duty> duties = Set.of(new Duty());
        Volunteer volunteer = new Volunteer();
        volunteer.setDuties(duties);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        ArrayList<Duty> result = volunteerService.getDuties(volunteerId);

        assertNotNull(result);
        assertEquals(new ArrayList<>(duties), result);
        verify(volunteerRepository, times(1)).findById(volunteerId);
    }

    @Test
    void testGetDuties_NotFound() {
        ID volunteerId = new ID(1);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        ArrayList<Duty> result = volunteerService.getDuties(volunteerId);

        assertNull(result);
        verify(volunteerRepository, times(1)).findById(volunteerId);
    }
    @Test
    void testGetPosition_Success() {
        // Given
        ID volunteerId = new ID(1);
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
        ID volunteerId = new ID(1);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Position result = volunteerService.getPosition(volunteerId);

        // Then
        assertNull(result);
        verify(volunteerRepository, times(1)).findById(volunteerId);
    }

}
