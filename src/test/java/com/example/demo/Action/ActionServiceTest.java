package com.example.demo.Action;

import com.example.demo.Model.Errors;

import com.example.demo.Volunteer.Preferences.Preferences;
import com.example.demo.Volunteer.User.User;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActionServiceTest {

    @InjectMocks
    private ActionService actionService;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    private Action mockAction;
    private Long testActionId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testActionId = 1L;
        mockAction = new Action();
        mockAction.setActionId(testActionId);
    }

    @Test
    public void testCreate() {
        when(actionRepository.save(any(Action.class))).thenAnswer(invocation -> {
            Action action = invocation.getArgument(0);
            action.setActionId(1L);
            return action;
        });

        Long newActionId = actionService.create();

        assertNotNull(newActionId);
        assertEquals(1, newActionId);
        verify(actionRepository, times(1)).save(any(Action.class));
    }

    @Test
    public void testRemove_ActionExists() {
        when(actionRepository.existsById(testActionId)).thenReturn(true);

        Errors result = actionService.remove(testActionId);

        assertEquals(Errors.SUCCESS, result);
        verify(actionRepository, times(1)).deleteById(testActionId);
    }

    @Test
    public void testRemove_ActionNotFound() {
        when(actionRepository.existsById(testActionId)).thenReturn(false);

        Errors result = actionService.remove(testActionId);

        assertEquals(Errors.NOT_FOUND, result);
        verify(actionRepository, never()).deleteById(testActionId);
    }

    @Test
    public void testSetBeg_ActionExists() {
        LocalDate beginDate = LocalDate.of(2025, 1, 1);
        when(actionRepository.findById(testActionId)).thenReturn(Optional.of(mockAction));

        Errors result = actionService.setBeg(testActionId, beginDate);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(beginDate, mockAction.getBegin());
        verify(actionRepository, times(1)).save(mockAction);
    }

    @Test
    public void testSetBeg_ActionNotFound() {
        when(actionRepository.findById(testActionId)).thenReturn(Optional.empty());

        Errors result = actionService.setBeg(testActionId, LocalDate.of(2025, 1, 1));

        assertEquals(Errors.NOT_FOUND, result);
        verify(actionRepository, never()).save(any(Action.class));
    }

    @Test
    public void testSetEnd_ActionExists() {
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        when(actionRepository.findById(testActionId)).thenReturn(Optional.of(mockAction));

        Errors result = actionService.setEnd(testActionId, endDate);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(endDate, mockAction.getEnd());
        verify(actionRepository, times(1)).save(mockAction);
    }

    @Test
    public void testSetEnd_ActionNotFound() {
        when(actionRepository.findById(testActionId)).thenReturn(Optional.empty());

        Errors result = actionService.setEnd(testActionId, LocalDate.of(2025, 12, 31));

        assertEquals(Errors.NOT_FOUND, result);
        verify(actionRepository, never()).save(any(Action.class));
    }

    @Test
    public void testSetDesc_ActionExists() {
        Lang language = Lang.EN;
        Description description = new Description();
        description.setFullName("Test Action Description");

        when(actionRepository.findById(testActionId)).thenReturn(Optional.of(mockAction));

        Errors result = actionService.setDesc(testActionId, language, description);

        assertEquals(Errors.SUCCESS, result);
        assertEquals(description, mockAction.getDescr().get(language));
        assertTrue(mockAction.getDescr().get(language).isValid());
        verify(actionRepository, times(1)).save(mockAction);
    }

    @Test
    public void testSetDesc_ActionNotFound() {
        Lang language = Lang.EN;
        Description description = new Description();

        when(actionRepository.findById(testActionId)).thenReturn(Optional.empty());

        Errors result = actionService.setDesc(testActionId, language, description);

        assertEquals(Errors.NOT_FOUND, result);
        verify(actionRepository, never()).save(any(Action.class));
    }

    @Test
    public void testRemDesc_ActionExists() {
        Lang language = Lang.EN;
        Description description = new Description();
        description.setValid(true);

        mockAction.getDescr().put(language, description);
        when(actionRepository.findById(testActionId)).thenReturn(Optional.of(mockAction));

        Errors result = actionService.remDesc(testActionId, language);

        assertEquals(Errors.SUCCESS, result);
        assertFalse(mockAction.getDescr().get(language).isValid());
        verify(actionRepository, times(1)).save(mockAction);
    }

    @Test
    public void testRemDesc_ActionNotFound() {
        when(actionRepository.findById(testActionId)).thenReturn(Optional.empty());

        Errors result = actionService.remDesc(testActionId, Lang.EN);

        assertEquals(Errors.NOT_FOUND, result);
        verify(actionRepository, never()).save(any(Action.class));
    }

    @Test
    public void testGetDesc_ValidDescription() {
        // Arrange: Create an Action with a valid description
        Long actionId = 1L;
        Action action = new Action();
        action.setActionId(actionId);
        action.setBegin(LocalDate.of(2025, 1, 1));
        action.setEnd(LocalDate.of(2025, 12, 31));

        Description validDescription = new Description();
        validDescription.setFullName("Valid Action Description");
        validDescription.setShortName("Short Desc");
        validDescription.setPlace("Test Place");
        validDescription.setAddress("123 Test Address");
        validDescription.setDescription("Detailed description of the action.");
        validDescription.setHours("10:00-15:00");
        validDescription.setRoles(new ArrayList<>(List.of(
                new Role(1L, "helper", "work"),
                new Role(2L, "worker", "helps")
        ))); // Use Role objects
        validDescription.setValid(true); // Mark as valid

        // Add the valid description for the specified language
        action.getDescr().put(Lang.EN, validDescription);

        // Mock the repository to return the action
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act: Call the method under test
        Description result = actionService.getDesc(actionId, Lang.EN);

        // Assert: Verify that the description matches the expected values
        assertNotNull(result, "The description should not be null.");
        assertEquals("Valid Action Description", result.getFullName(), "The full name should match.");
        assertEquals("Short Desc", result.getShortName(), "The short name should match.");
        assertEquals("Test Place", result.getPlace(), "The place should match.");
        assertEquals("123 Test Address", result.getAddress(), "The address should match.");
        assertEquals("Detailed description of the action.", result.getDescription(), "The detailed description should match.");
        assertEquals("10:00-15:00", result.getHours(), "The hours should match.");

        // Compare roles individually
        assertNotNull(result.getRoles(), "Roles should not be null.");
        assertEquals(2, result.getRoles().size(), "There should be 2 roles.");
        Role expectedRole1 = new Role(1L, "helper", "work");
        Role expectedRole2 = new Role(2L, "worker", "helps");

        Role actualRole1 = result.getRoles().get(0);
        Role actualRole2 = result.getRoles().get(1);

        assertEquals(expectedRole1.getRoleId(), actualRole1.getRoleId(), "Role 1 Long should match.");
        assertEquals(expectedRole1.getName(), actualRole1.getName(), "Role 1 name should match.");
        assertEquals(expectedRole1.getDuties(), actualRole1.getDuties(), "Role 1 duties should match.");

        assertEquals(expectedRole2.getRoleId(), actualRole2.getRoleId(), "Role 2 Long should match.");
        assertEquals(expectedRole2.getName(), actualRole2.getName(), "Role 2 name should match.");
        assertEquals(expectedRole2.getDuties(), actualRole2.getDuties(), "Role 2 duties should match.");

        assertEquals(LocalDate.of(2025, 1, 1), result.getBegin(), "The begin date should match.");
        assertEquals(LocalDate.of(2025, 12, 31), result.getEnd(), "The end date should match.");
    }





    @Test
    public void testGetDesc_InvalidDescription() {
        // Arrange: Create an Action with an invalid description
        Long actionId = 2L;
        Action action = new Action();
        action.setActionId(actionId);

        Description invalidDescription = new Description();
        invalidDescription.setFullName("Invalid Action Description");
        invalidDescription.setValid(false); // Mark as invalid

        // Add the invalid description for the specified language
        action.getDescr().put(Lang.EN, invalidDescription);

        // Mock the repository to return the action
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act: Call the method under test
        Description result = actionService.getDesc(actionId, Lang.EN);

        // Assert: Verify that the result is null for invalid descriptions
        assertNull(result, "The result should be null for an invalid description.");
    }

    @Test
    public void testGetDesc_NoDescription() {
        // Arrange: Create an Action without a description
        Long actionId = 3L;
        Action action = new Action();
        action.setActionId(actionId);

        // Mock the repository to return the action
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act: Call the method under test
        Description result = actionService.getDesc(actionId, Lang.EN);

        // Assert: Verify that the result is null when no description exists
        assertNull(result, "The result should be null when no description exists for the specified language.");
    }

    @Test
    public void testGetDesc_ActionNotFound() {
        // Arrange: Mock the repository to return empty for a non-existent action
        Long actionId = 4L;
        when(actionRepository.findById(actionId)).thenReturn(Optional.empty());

        // Act: Call the method under test
        Description result = actionService.getDesc(actionId, Lang.EN);

        // Assert: Verify that the result is null when the action does not exist
        assertNull(result, "The result should be null when the action does not exist.");
    }




    @Test
    public void testGetAllIds() {
        Action action1 = new Action();
        action1.setActionId(1L);
        Action action2 = new Action();
        action2.setActionId(2L);
        List<Action> actions = List.of(action1, action2);

        when(actionRepository.findAll()).thenReturn(actions);

        ArrayList<Long> ids = actionService.getAllIds();

        assertEquals(2, ids.size());
        assertEquals(1, ids.get(0));
        assertEquals(2, ids.get(1));
    }

    @Test
    public void testGetAllDesc() {
        // Arrange: Create multiple actions with valid and invalid descriptions
        Action action1 = new Action();
        action1.setActionId(1L);
        Description desc1 = new Description();
        desc1.setFullName("Description 1");
        desc1.setValid(true); // Ensure this description is valid
        action1.getDescr().put(Lang.EN, desc1);

        Action action2 = new Action();
        action2.setActionId(2L);
        Description desc2 = new Description();
        desc2.setFullName("Description 2");
        desc2.setValid(true); // Ensure this description is valid
        action2.getDescr().put(Lang.EN, desc2);

        Action action3 = new Action();
        action3.setActionId(3L);
        Description desc3 = new Description();
        desc3.setFullName("Description 3");
        desc3.setValid(false); // This description is invalid
        action3.getDescr().put(Lang.EN, desc3);

        Action action4 = new Action();
        action4.setActionId(4L);
        // No description for Lang.EN

        // Mock the repository to return these actions
        when(actionRepository.findAll()).thenReturn(List.of(action1, action2, action3, action4));
        when(actionRepository.findById(1L)).thenReturn(Optional.of(action1));
        when(actionRepository.findById(2L)).thenReturn(Optional.of(action2));
        when(actionRepository.findById(3L)).thenReturn(Optional.of(action3));
        when(actionRepository.findById(4L)).thenReturn(Optional.of(action4));

        // Act: Call the method under test
        ArrayList<Description> result = actionService.getAllDesc(Lang.EN);

        // Assert: Validate the result
        assertEquals(2, result.size(), "Expected 2 valid descriptions to be returned");

        // Validate the details of the first description
        assertEquals("Description 1", result.get(0).getFullName(), "First description should match");
        assertEquals("Description 2", result.get(1).getFullName(), "Second description should match");

        // Debugging Output
        System.out.println("Descriptions returned: " + result);
    }
    @Test
    public void testSetStronglyMine_Success() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class); // Mock the Preferences object
        Set<Action> stronglyMineSet = new HashSet<>(); // Mock the "S" set
        when(preferences.getS()).thenReturn(stronglyMineSet); // Stub the "S" getter
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        Action action = new Action();
        Long actionId = 1L;
        action.setActionId(actionId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act
        Errors result = actionService.setStronglyMine(user, actionId);

        // Assert
        assertEquals(Errors.SUCCESS, result, "Expected SUCCESS when adding action to strongly mine.");
        assertTrue(stronglyMineSet.contains(action), "The action should be added to the strongly mine set.");
        verify(actionRepository, times(1)).findById(actionId);
        verify(preferences, times(1)).getS(); // Verify that the Preferences object was called
    }


    @Test
    public void testSetWeaklyMine_Success() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class);
        Set<Action> weaklyMineSet = new HashSet<>();
        when(preferences.getW()).thenReturn(weaklyMineSet);
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        Action action = new Action();
        Long actionId = 1L;
        action.setActionId(actionId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act
        Errors result = actionService.setWeaklyMine(user, actionId);

        // Assert
        assertEquals(Errors.SUCCESS, result, "Expected SUCCESS when adding action to weakly mine.");
        assertTrue(weaklyMineSet.contains(action), "The action should be added to the weakly mine set.");
        verify(actionRepository, times(1)).findById(actionId);
        verify(preferences, times(1)).getW();
    }


    @Test
    public void testSetRejected_Success() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class); // Mock the Preferences object
        Set<Action> rejectedSet = new HashSet<>(); // Mock the "R" set
        when(preferences.getR()).thenReturn(rejectedSet); // Stub the "R" getter
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        Action action = new Action();
        Long actionId = 1L;
        action.setActionId(actionId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act
        Errors result = actionService.setRejected(user, actionId);

        // Assert
        assertEquals(Errors.SUCCESS, result, "Expected SUCCESS when adding action to rejected.");
        assertTrue(rejectedSet.contains(action), "The action should be added to the rejected set.");
        verify(actionRepository, times(1)).findById(actionId);
        verify(preferences, times(1)).getR(); // Verify that the Preferences object was called
    }


    @Test
    public void testSetUndecided_Success() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class);
        Set<Action> undecidedSet = new HashSet<>();
        when(preferences.getU()).thenReturn(undecidedSet);
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        Action action = new Action();
        Long actionId = 1L;
        action.setActionId(actionId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        // Act
        Errors result = actionService.setUndecided(user, actionId);

        // Assert
        assertEquals(Errors.SUCCESS, result, "Expected SUCCESS when adding action to undecided.");
        assertTrue(undecidedSet.contains(action), "The action should be added to the undecided set.");
        verify(actionRepository, times(1)).findById(actionId);
        verify(preferences, times(1)).getU();
    }


    @Test
    public void testSetStronglyMine_ActionNotFound() {
        // Arrange
        User user = new User();
        Volunteer volunteer = new Volunteer();
        volunteer.setPreferences(new Preferences());
        user.setVolunteer(volunteer);

        Long actionId = 1L;

        when(actionRepository.findById(actionId)).thenReturn(Optional.empty());

        // Act
        Errors result = actionService.setStronglyMine(user, actionId);

        // Assert
        assertEquals(Errors.FAILURE, result, "Expected FAILURE when action is not found.");
        verify(actionRepository, times(1)).findById(actionId);
    }
    @Test
    public void testGetStronglyMine() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class);
        Set<Action> stronglyMineSet = new HashSet<>();
        Action action = new Action();
        action.setActionId(1L);
        Description description = new Description();
        description.setFullName("Strongly Mine Action");
        action.getDescr().put(Lang.UK, description);
        stronglyMineSet.add(action);
        when(preferences.getS()).thenReturn(stronglyMineSet);
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        when(volunteerRepository.getVolunteerByVolunteerId(volunteer.getVolunteerId())).thenReturn(volunteer);

        // Act
        ArrayList<Description> result = actionService.getStronglyMine(user);

        // Assert
        assertEquals(1, result.size(), "Expected 1 strongly mine description");
        assertEquals("Strongly Mine Action", result.get(0).getFullName(), "Expected description to match");
        verify(volunteerRepository, times(1)).getVolunteerByVolunteerId(volunteer.getVolunteerId());
    }

    @Test
    public void testUpdateAction_Success() {
        // Arrange
        Long actionId = 1L;
        Action existingAction = new Action();
        existingAction.setActionId(actionId);

        Action newAction = new Action();
        newAction.setBegin(LocalDate.of(2025, 1, 1));
        newAction.setEnd(LocalDate.of(2025, 12, 31));
        newAction.setDescr(new HashMap<>());

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(existingAction));

        // Act
        Errors result = actionService.updateAction(actionId, newAction);

        // Assert
        assertEquals(Errors.SUCCESS, result, "Expected SUCCESS when updating action.");
        assertEquals(LocalDate.of(2025, 1, 1), existingAction.getBegin(), "Expected begin date to be updated.");
        assertEquals(LocalDate.of(2025, 12, 31), existingAction.getEnd(), "Expected end date to be updated.");
        verify(actionRepository, times(1)).findById(actionId);
    }

    @Test
    public void testUpdateAction_NotFound() {
        // Arrange
        Long actionId = 1L;
        Action newAction = new Action();

        when(actionRepository.findById(actionId)).thenReturn(Optional.empty());

        // Act
        Errors result = actionService.updateAction(actionId, newAction);

        // Assert
        assertEquals(Errors.NOT_FOUND, result, "Expected NOT_FOUND when action does not exist.");
        verify(actionRepository, times(1)).findById(actionId);
    }
    @Test
    public void testGetWeaklyMine() {
        // Arrange
        // Create a mock volunteer and their preferences
        Volunteer volunteer = new Volunteer();
        volunteer.setVolunteerId(1L);
        Preferences preferences = new Preferences();

        // Create a mock action with a valid description
        Action action = new Action();
        action.setActionId(100L);
        Description description = new Description();
        description.setFullName("Weakly Mine Action");
        description.setValid(true);
        action.getDescr().put(Lang.UK, description);

        // Add the action to the weakly mine set in preferences
        Set<Action> weaklyMineSet = new HashSet<>();
        weaklyMineSet.add(action);
        preferences.setW(weaklyMineSet);
        volunteer.setPreferences(preferences);

        // Mock the User and VolunteerRepository behavior
        User user = new User();
        user.setVolunteer(volunteer);

        when(volunteerRepository.getVolunteerByVolunteerId(volunteer.getVolunteerId())).thenReturn(volunteer);

        // Act
        ArrayList<Description> result = new ArrayList<>(actionService.getWeaklyMine(user)); // Ensure conversion to ArrayList

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(1, result.size(), "The result should contain one description.");
        assertEquals("Weakly Mine Action", result.get(0).getFullName(), "The description's full name should match.");
        verify(volunteerRepository, times(1)).getVolunteerByVolunteerId(volunteer.getVolunteerId());
    }

    @Test
    public void testGetRejected() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class);
        Set<Action> rejectedSet = new HashSet<>();
        Action action = new Action();
        action.setActionId(1L);
        Description description = new Description();
        description.setFullName("Rejected Action");
        action.getDescr().put(Lang.UK, description);
        rejectedSet.add(action);
        when(preferences.getR()).thenReturn(rejectedSet);
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        when(volunteerRepository.getVolunteerByVolunteerId(volunteer.getVolunteerId())).thenReturn(volunteer);

        // Act
        ArrayList<Description> result = actionService.getRejected(user);

        // Assert
        assertEquals(1, result.size(), "Expected 1 rejected description");
        assertEquals("Rejected Action", result.get(0).getFullName(), "Expected description to match");
        verify(volunteerRepository, times(1)).getVolunteerByVolunteerId(volunteer.getVolunteerId());
    }
    @Test
    public void testGetUndecided() {
        // Arrange
        Volunteer volunteer = new Volunteer();
        Preferences preferences = mock(Preferences.class);
        Set<Action> undecidedSet = new HashSet<>();
        Action action = new Action();
        action.setActionId(1L);
        Description description = new Description();
        description.setFullName("Undecided Action");
        action.getDescr().put(Lang.UK, description);
        undecidedSet.add(action);
        when(preferences.getU()).thenReturn(undecidedSet);
        volunteer.setPreferences(preferences);

        User user = new User();
        user.setVolunteer(volunteer);

        when(volunteerRepository.getVolunteerByVolunteerId(volunteer.getVolunteerId())).thenReturn(volunteer);

        // Act
        ArrayList<Description> result = actionService.getUndecided(user);

        // Assert
        assertEquals(1, result.size(), "Expected 1 undecided description");
        assertEquals("Undecided Action", result.get(0).getFullName(), "Expected description to match");
        verify(volunteerRepository, times(1)).getVolunteerByVolunteerId(volunteer.getVolunteerId());
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





