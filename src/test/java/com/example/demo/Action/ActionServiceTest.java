//package com.example.demo.Action;
package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ActionServiceTest {

    @InjectMocks
    private ActionService actionService;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    private ID testActionId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testActionId = new ID(1);
    }

    @Test
    public void testCreate() {
        ID newActionId = actionService.create();

        assertNotNull(newActionId);
        assertEquals(1, newActionId.getId());
    }

    @Test
    public void testRemove_ActionExists() {
        actionService.create();
        Errors result = actionService.remove(testActionId);

        assertEquals(Errors.SUCCESS, result);
    }

    @Test
    public void testRemove_ActionNotFound() {
        Errors result = actionService.remove(testActionId);

        assertEquals(Errors.NOT_FOUND, result);
    }

    @Test
    public void testSetBeg_ActionExists() {
        actionService.create();
        LocalDate beginDate = LocalDate.of(2025, 1, 1);

        Errors result = actionService.setBeg(testActionId, beginDate);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(beginDate, actionService.getBeg(testActionId));
    }

    @Test
    public void testSetBeg_ActionNotFound() {
        LocalDate beginDate = LocalDate.of(2025, 1, 1);

        Errors result = actionService.setBeg(testActionId, beginDate);

        assertEquals(Errors.NOT_FOUND, result);
    }

    @Test
    public void testSetEnd_ActionExists() {
        actionService.create();
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        Errors result = actionService.setEnd(testActionId, endDate);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(endDate, actionService.getEnd(testActionId));
    }

    @Test
    public void testSetEnd_ActionNotFound() {
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        Errors result = actionService.setEnd(testActionId, endDate);

        assertEquals(Errors.NOT_FOUND, result);
    }

    @Test
    public void testSetDesc_ActionExists() {

        ID actionId = actionService.create();
        Lang language = Lang.EN;

        Description description = new Description();
        description.setFullName("Test Action");



        Errors result = actionService.setDesc(actionId, language, description);


        assertEquals(Errors.SUCCESS, result, "Setting the description should return SUCCESS.");

        Description retrievedDesc = actionService.getDesc(actionId, language);


        assertNotNull(retrievedDesc, "The retrieved description should not be null.");
        //TODO ogarnac czemu zmienia valid
        assertEquals(true, retrievedDesc.isValid(), "The retrieved description should be valid.");
        assertEquals("Test Action", retrievedDesc.getFullName(), "The fullName should match the set value.");
    }


    @Test
    public void testSetDesc_ActionNotFound() {
        Lang language = Lang.EN;
        Description description = new Description();

        Errors result = actionService.setDesc(testActionId, language, description);

        assertEquals(Errors.NOT_FOUND, result);
    }

    @Test
    public void testRemDesc_ActionExists() {

        ID testActionId = actionService.create();
        Lang language = Lang.EN;


        Description description = new Description();
        description.setValid(true);
        description.setFullName("Test Action Full Name");
        Errors setDescResult = actionService.setDesc(testActionId, language, description);
        assertEquals(Errors.SUCCESS, setDescResult);


        Errors result = actionService.remDesc(testActionId, language);


        assertEquals(Errors.SUCCESS, result);


        Action action = actionService.getAction(testActionId);
        assertNotNull(action);
        Version version = action.getDescr().get(language);
        assertNotNull(version);
        assertFalse(version.isValid());


        Description updatedDesc = actionService.getDesc(testActionId, language);
        assertNull(updatedDesc);
    }





    @Test
    public void testRemDesc_ActionNotFound() {
        Lang language = Lang.EN;

        Errors result = actionService.remDesc(testActionId, language);

        assertEquals(Errors.NOT_FOUND, result);
    }

    @Test
    public void testGetDesc_ActionExists() {
        actionService.create();
        Lang language = Lang.EN;
        Description description = new Description();
        description.setFullName("Test Action");
        actionService.setDesc(testActionId, language, description);

        Description result = actionService.getDesc(testActionId, language);

        assertNotNull(result);
        assertEquals("Test Action", result.getFullName());
    }

    @Test
    public void testGetDesc_ActionNotFound() {
        Lang language = Lang.EN;

        Description result = actionService.getDesc(testActionId, language);

        assertNull(result);
    }

    @Test
    public void testGetAllIds() {
        actionService.create();
        actionService.create();

        List<ID> actionIds = actionService.getAllIds();

        assertEquals(2, actionIds.size());
        assertEquals(1, actionIds.get(0).getId());
        assertEquals(2, actionIds.get(1).getId());
    }

    @Test
    public void testGetAllDesc() {
        // Arrange: Properly create actions and set their descriptions
        ID actionId1 = new ID(1);
        ID actionId2 = new ID(2);

        // Manually create and initialize actions with proper IDs
        Action action1 = new Action();
        action1.setActionId(actionId1);

        Action action2 = new Action();
        action2.setActionId(actionId2);

        // Add actions to the service's internal storage
        actionService.create(); // Mock the creation behavior
        actionService.create(); // Simulate adding two actions

        Lang language = Lang.EN;

        // Create and set a valid description for the first action
        Description description1 = new Description();
        description1.setFullName("Test Action 1");
        description1.setBegin(LocalDate.of(2025, 1, 1));
        description1.setEnd(LocalDate.of(2025, 1, 31));
        Errors result1 = actionService.setDesc(actionId1, language, description1);
        assertEquals(Errors.SUCCESS, result1);

        // Create and set a valid description for the second action
        Description description2 = new Description();
        description2.setFullName("Test Action 2");
        description2.setBegin(LocalDate.of(2025, 2, 1));
        description2.setEnd(LocalDate.of(2025, 2, 28));
        Errors result2 = actionService.setDesc(actionId2, language, description2);
        assertEquals(Errors.SUCCESS, result2);

        // Act: Retrieve all descriptions for the specified language
        ArrayList<Description> descriptions = actionService.getAllDesc(language);

        // Assert: Verify that the descriptions are retrieved correctly
        assertEquals(2, descriptions.size());

        // Validate the first description
        Description retrievedDesc1 = descriptions.get(0);
        assertEquals("Test Action 1", retrievedDesc1.getFullName());
        assertEquals(LocalDate.of(2025, 1, 1), retrievedDesc1.getBegin());
        assertEquals(LocalDate.of(2025, 1, 31), retrievedDesc1.getEnd());

        // Validate the second description
        Description retrievedDesc2 = descriptions.get(1);
        assertEquals("Test Action 2", retrievedDesc2.getFullName());
        assertEquals(LocalDate.of(2025, 2, 1), retrievedDesc2.getBegin());
        assertEquals(LocalDate.of(2025, 2, 28), retrievedDesc2.getEnd());
    }




}

//public class ActionServiceTest {
//
//    @InjectMocks
//    private ActionService actionService;
//
//    @Mock
//    private ActionRepository actionRepository;
//
//    @Mock
//    private VolunteerRepository volunteerRepository;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testGetAllActions() {
//        List<Action> actions = List.of(new Action());
//        when(actionRepository.findAll()).thenReturn(actions);
//
//        List<Action> result = actionService.getAllActions();
//
//        assertEquals(actions, result);
//    }
//
//    @Test
//    public void testGetActionById() {
//        Action action = new Action();
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//
//        Optional<Action> result = actionService.getActionById(1L);
//
//        assertTrue(result.isPresent());
//        assertEquals(action, result.get());
//    }
//
//    @Test
//    public void testGetActionById_NotFound() {
//        when(actionRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Optional<Action> result = actionService.getActionById(1L);
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void testGetActionDescription() {
//        Action action = new Action();
//        action.setDescription("Description");
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//
//        Optional<String> result = actionService.getActionDescription(1L);
//
//        assertTrue(result.isPresent());
//        assertEquals("Description", result.get());
//    }
//
//    @Test
//    public void testGetActionDescription_NotFound() {
//        when(actionRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Optional<String> result = actionService.getActionDescription(1L);
//
//        assertTrue(result.isEmpty());
//    }
//
//    //TODO Poprawić
//    @Test
//    public void testGetActionHeading() {
//        Action action = new Action();
//        action.setHeading("Heading");
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//
//        Optional<String> result = actionService.getActionHeading(1L);
//
//        assertTrue(result.isPresent());
//        assertEquals("Heading", result.get());
//    }
//
//    @Test
//    public void testGetActionHeading_NotFound() {
//        when(actionRepository.findById(1L)).thenReturn(Optional.empty());
//
//        Optional<String> result = actionService.getActionHeading(1L);
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void testAddAction() {
//        Action action = new Action();
//        when(actionRepository.save(action)).thenReturn(action);
//
//        Action result = actionService.addAction(action);
//
//        assertEquals(action, result);
//    }
//
//    @Test
//    public void testCreateAndAddAction() {
//        AddActionRequest request = new AddActionRequest(
//                1L,
//                "Heading",
//                "Description",
//                ActionStatus.OPEN,
//                LocalDate.now(),
//                LocalDate.now().plusDays(1),
//                2L
//        );
//        Volunteer leader = new Volunteer();
//        leader.setRole(VolunteerRole.LEADER);
//        VolunteerDetails details = new VolunteerDetails();
//        details.setFirstname("John");
//        details.setLastname("Doe");
//        details.setEmail("john.doe@example.com");
//        details.setPhone("123456789");
//        details.setDateOfBirth(LocalDate.now());
//        details.setCity("City");
//        details.setStreet("Street");
//        details.setHouseNumber("1A");
//        details.setApartmentNumber("10");
//        details.setPostalNumber("12345");
//        details.setSex("M");
//        leader.setVolunteerDetails(details);
//
//        when(volunteerRepository.findById(2L)).thenReturn(Optional.of(leader));
//
//        Action action = new Action();
//        when(actionRepository.save(any(Action.class))).thenReturn(action);
//
//        Action result = actionService.createAndAddAction(request);
//
//        assertEquals(action, result);
//    }
//
//    @Test
//    public void testCloseAction() {
//        Action action = new Action();
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//
//        actionService.closeAction(1L, 1L);
//
//        assertEquals(ActionStatus.CLOSED, action.getStatus());
//        verify(actionRepository).save(action);
//    }
//
//    @Test
//    public void testChangeDescription() {
//        Action action = new Action();
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//
//        actionService.changeDescription(1L, "New Description");
//
//        assertEquals("New Description", action.getDescription());
//        verify(actionRepository).save(action);
//    }
//
//    @Test
//    public void testGetLeader() {
//        Volunteer leader = new Volunteer();
//        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(leader));
//
//        Optional<Volunteer> result = actionService.getLeader(1L);
//
//        assertTrue(result.isPresent());
//        assertEquals(leader, result.get());
//    }
//
//    @Test
//    public void testGetLeaderDto() {
//        Volunteer volunteer = new Volunteer();
//        volunteer.setRole(VolunteerRole.LEADER);
//        VolunteerDetails details = new VolunteerDetails();
//        details.setFirstname("Name");
//        details.setLastname("Lastname");
//        details.setEmail("email@example.com");
//        details.setPhone("123456789");
//        details.setDateOfBirth(LocalDate.now());
//        details.setCity("City");
//        details.setStreet("Street");
//        details.setHouseNumber("1A");
//        details.setApartmentNumber("10");
//        details.setPostalNumber("12345");
//        details.setSex("M");
//        volunteer.setVolunteerDetails(details);
//
//        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
//
//        Optional<LeaderDto> result = actionService.getLeaderDto(1L);
//
//        assertTrue(result.isPresent());
//        LeaderDto dto = result.get();
//        assertEquals(volunteer.getVolunteerId(), dto.leaderId());
//        assertEquals(details.getFirstname(), dto.name());
//        assertEquals(details.getLastname(), dto.lastname());
//        assertEquals(details.getEmail(), dto.email());
//        assertEquals(details.getPhone(), dto.phone());
//    }
//
//    @Test
//    public void testAddDetermined() {
//        Action action = new Action();
//        Volunteer volunteer = new Volunteer();
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//        when(volunteerRepository.findById(2L)).thenReturn(Optional.of(volunteer));
//
//        actionService.addDetermined(1L, 2L);
//
//        assertTrue(action.getDetermined().contains(volunteer));
//    }
//
//    @Test
//    public void testAddVolunteer() {
//        Action action = new Action();
//        Volunteer volunteer = new Volunteer();
//        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));
//        when(volunteerRepository.findById(2L)).thenReturn(Optional.of(volunteer));
//
//        actionService.addVolunteer(1L, 2L);
//
//        assertTrue(action.getVolunteers().contains(volunteer));
//    }
//}