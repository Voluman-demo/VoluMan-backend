package com.example.demo.Volunteer;

import com.example.demo.Action.ActionRepository;
import com.example.demo.Action.ActionService;
import com.example.demo.Action.Lang;
import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.AvailabilityRequest;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.IntervalRequest;
import com.example.demo.Volunteer.Availability.AvailabilityInterval.AvailabilityInterval;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Position.PositionService;
import com.example.demo.Volunteer.VolunteerDto.VolunteerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
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

        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setVolunteerId(1L);
            return v;
        });

        Long id = volunteerService.createVolunteer();

        assertNotNull(id);
        assertEquals(1, id);


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
    public void testEditVolunteer_ReturnsFailure_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;
        VolunteerRequest request = new VolunteerRequest();

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Errors result = volunteerService.editVolunteer(volunteerId, request);

        // Then
        assertEquals(Errors.FAILURE, result);
        verify(volunteerRepository, never()).save(any());
    }



    @Test
    public void testDeleteVolunteer_ReturnsNotFound_WhenVolunteerDoesNotExist() {
        // Given
        Long volunteerId = 1L;
        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Errors result = volunteerService.deleteVolunteer(volunteerId);

        // Then
        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).delete(any());
    }


    @Test
    void testDeleteVolunteer_NotFound() {
        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Errors result = volunteerService.deleteVolunteer(1L);

        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any(Volunteer.class));
    }


    @Test
    public void testSetAvailabilities_ReturnsSuccess_WhenVolunteerExists() {
        // Given
        Long volunteerId = 1L;
        Volunteer volunteer = new Volunteer();
        volunteer.setAvailabilities(new ArrayList<>()); // Initialize empty list

        List<Availability> availabilities = List.of(new Availability(), new Availability()); // Mock availability list

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Errors result = volunteerService.setAvailabilities(volunteerId, availabilities);

        // Then
        assertEquals(Errors.SUCCESS, result);


        assertEquals(2, volunteer.getAvailabilities().size(), "Availabilities were not added correctly");


        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    public void testSetAvailabilities_ReturnsNotFound_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;
        List<Availability> availabilities = new ArrayList<>();

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Errors result = volunteerService.setAvailabilities(volunteerId, availabilities);

        // Then
        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any());
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

    @Test
    void  testCreateAndEditVolunteer_Success() {

        VolunteerRequest details = new VolunteerRequest();
        details.setFirstName("John");
        details.setLastName("Doe");
        details.setEmail("john.doe@example.com");


        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setVolunteerId(1L);
            return v;
        });

        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.of(new Volunteer()));

        Long result = volunteerService.createAndEditVolunteer(details);

        assertNotNull(result);
        assertEquals(1, result);

        verify(volunteerRepository, times(2)).save(any(Volunteer.class));
    }


    @Test
    void testCreateAndEditVolunteer_FailsToEdit() {

        VolunteerRequest details = new VolunteerRequest();
        details.setEmail("test@example.com");
        when(volunteerRepository.existsByEmail(anyString())).thenReturn(false);

        when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> {
            Volunteer v = invocation.getArgument(0);
            v.setVolunteerId(1L);
            return v;
        });


        when(volunteerRepository.findById(any(Long.class))).thenReturn(Optional.empty());


        Long result = volunteerService.createAndEditVolunteer(details);


        assertNull(result, "Expected null because editing the volunteer failed");


        verify(volunteerRepository, times(1)).save(any(Volunteer.class));

        verify(volunteerRepository, times(1)).findById(any(Long.class));


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
    @Test
    public void testAssignPosition_ReturnsSuccess_WhenVolunteerExists() {
        // Given
        Long volunteerId = 1L;
        Position newRole = Position.LEADER;
        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Errors result = volunteerService.assignPosition(volunteerId, newRole);

        // Then
        assertEquals(Errors.SUCCESS, result);
        verify(volunteerRepository, times(1)).save(volunteer);
    }

    @Test
    public void testAssignPosition_ReturnsNotFound_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;
        Position newRole = Position.LEADER;

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Errors result = volunteerService.assignPosition(volunteerId, newRole);

        // Then
        assertEquals(Errors.NOT_FOUND, result);
        verify(volunteerRepository, never()).save(any());
    }


    @Test
    public void testGetVolunteerById_ReturnsVolunteer_WhenFound() {
        // Given
        Long volunteerId = 1L;
        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Volunteer result = volunteerService.getVolunteerById(volunteerId);

        // Then
        assertNotNull(result);
        assertEquals(volunteer, result);
    }

    @Test
    public void testGetVolunteerById_ReturnsNull_WhenNotFound() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Volunteer result = volunteerService.getVolunteerById(volunteerId);

        // Then
        assertNull(result);
    }
    @Test
    public void testGetLimitOfWeeklyHours_ReturnsValue_WhenVolunteerExists() {
        // Given
        Long volunteerId = 1L;
        Volunteer volunteer = new Volunteer();
        volunteer.setLimitOfWeeklyHours(20.0);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Double result = volunteerService.getLimitOfWeeklyHours(volunteerId);

        // Then
        assertNotNull(result);
        assertEquals(20.0, result);
    }

    @Test
    public void testGetLimitOfWeeklyHours_ReturnsNull_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Double result = volunteerService.getLimitOfWeeklyHours(volunteerId);

        // Then
        assertNull(result);
    }
    @Test
    public void testConvertToAvailability_ReturnsAvailability_WhenValidRequest() {
        // Given
        Long volunteerId = 1L;
        AvailabilityRequest request = new AvailabilityRequest();
        request.setDate(LocalDate.of(2025, 2, 10));

        IntervalRequest slot1 = new IntervalRequest();
        slot1.setStartTime("09:00");
        slot1.setEndTime("10:00");

        IntervalRequest slot2 = new IntervalRequest();
        slot2.setStartTime("14:00");
        slot2.setEndTime("15:00");

        request.setSlots(List.of(slot1, slot2));

        Volunteer volunteer = new Volunteer();
        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Availability result = volunteerService.convertToAvailability(request, volunteerId);

        // Then
        assertNotNull(result);
        assertEquals(LocalDate.of(2025, 2, 10), result.getDate());
        assertEquals(volunteer, result.getVolunteer());
        assertEquals(2, result.getSlots().size());

        // Convert Set to List and sort by start time
        List<AvailabilityInterval> intervals = new ArrayList<>(result.getSlots());
        intervals.sort(Comparator.comparing(AvailabilityInterval::getStartTime));

        assertEquals(LocalTime.of(9, 0), intervals.get(0).getStartTime());
        assertEquals(LocalTime.of(10, 0), intervals.get(0).getEndTime());

        assertEquals(LocalTime.of(14, 0), intervals.get(1).getStartTime());
        assertEquals(LocalTime.of(15, 0), intervals.get(1).getEndTime());

        verify(volunteerRepository, times(1)).findById(volunteerId);
    }


    @Test
    public void testConvertToAvailability_ThrowsException_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;
        AvailabilityRequest request = new AvailabilityRequest();
        request.setDate(LocalDate.of(2025, 2, 10));

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            volunteerService.convertToAvailability(request, volunteerId);
        });

        verify(volunteerRepository, times(1)).findById(volunteerId);
    }
    @Test
    public void testEditVolunteerWeeklyHours_ReturnsSuccess_WhenVolunteerExists() {
        // Given
        Long volunteerId = 1L;
        Double newWeeklyHours = 20.0;

        Volunteer volunteer = new Volunteer();
        volunteer.setLimitOfWeeklyHours(15.0); // Previous value

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Errors result = volunteerService.editVolunteerWeeklyHours(volunteerId, newWeeklyHours);

        // Then
        assertEquals(Errors.SUCCESS, result);
        assertEquals(newWeeklyHours, volunteer.getLimitOfWeeklyHours());

        verify(volunteerRepository, times(1)).findById(volunteerId);
        verify(volunteerRepository, times(1)).save(volunteer);
    }
    @Test
    public void testEditVolunteerWeeklyHours_ReturnsFailure_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;
        Double newWeeklyHours = 20.0;

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Errors result = volunteerService.editVolunteerWeeklyHours(volunteerId, newWeeklyHours);

        // Then
        assertEquals(Errors.FAILURE, result);

        verify(volunteerRepository, times(1)).findById(volunteerId);
        verify(volunteerRepository, never()).save(any());
    }
    @Test
    public void testGetLang_ReturnsLanguage_WhenVolunteerExists() {
        // Given
        Long volunteerId = 1L;
        Lang expectedLang = Lang.EN;

        Volunteer volunteer = new Volunteer();
        volunteer.setLanguage(expectedLang);

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

        // When
        Lang result = volunteerService.getLang(volunteerId);

        // Then
        assertNotNull(result);
        assertEquals(expectedLang, result);

        verify(volunteerRepository, times(1)).findById(volunteerId);
    }
    @Test
    public void testGetLang_ReturnsNull_WhenVolunteerNotFound() {
        // Given
        Long volunteerId = 1L;

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.empty());

        // When
        Lang result = volunteerService.getLang(volunteerId);

        // Then
        assertNull(result);

        verify(volunteerRepository, times(1)).findById(volunteerId);
    }
    @Test
    public void testGetAllPersData_ReturnsPersonalDataList_WhenVolunteersExist() {
        // Given
        Position position = Position.VOLUNTEER;

        Volunteer volunteer1 = new Volunteer();
        volunteer1.setFirstName("John");
        volunteer1.setLastName("Doe");
        volunteer1.setEmail("john.doe@example.com");
        volunteer1.setPhone("+123456789");
        volunteer1.setDateOfBirth(LocalDate.of(1990, 1, 1));
        volunteer1.setAddress("123 Main St");
        volunteer1.setSex("M");

        Volunteer volunteer2 = new Volunteer();
        volunteer2.setFirstName("Jane");
        volunteer2.setLastName("Smith");
        volunteer2.setEmail("jane.smith@example.com");
        volunteer2.setPhone("+987654321");
        volunteer2.setDateOfBirth(LocalDate.of(1992, 5, 10));
        volunteer2.setAddress("456 Elm St");
        volunteer2.setSex("F");

        List<Volunteer> volunteerList = List.of(volunteer1, volunteer2);

        when(volunteerRepository.findAllByPosition(position)).thenReturn(volunteerList);

        // When
        List<PersonalData> result = volunteerService.getAllPersData(position);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("john.doe@example.com", result.get(0).getEmail());
        assertEquals("+123456789", result.get(0).getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), result.get(0).getDateOfBirth());
        assertEquals("123 Main St", result.get(0).getAddress());
        assertEquals("M", result.get(0).getSex());

        assertEquals("Jane", result.get(1).getFirstName());
        assertEquals("Smith", result.get(1).getLastName());
        assertEquals("jane.smith@example.com", result.get(1).getEmail());
        assertEquals("+987654321", result.get(1).getPhone());
        assertEquals(LocalDate.of(1992, 5, 10), result.get(1).getDateOfBirth());
        assertEquals("456 Elm St", result.get(1).getAddress());
        assertEquals("F", result.get(1).getSex());

        verify(volunteerRepository, times(1)).findAllByPosition(position);
    }
    @Test
    public void testGetAllPersData_ReturnsEmptyList_WhenNoVolunteersExist() {
        // Given
        Position position = Position.LEADER;

        when(volunteerRepository.findAllByPosition(position)).thenReturn(Collections.emptyList());

        // When
        List<PersonalData> result = volunteerService.getAllPersData(position);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(volunteerRepository, times(1)).findAllByPosition(position);
    }

}