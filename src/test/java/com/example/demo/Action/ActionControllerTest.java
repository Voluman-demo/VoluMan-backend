package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.User;
import com.example.demo.Volunteer.VolunteerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ActionControllerTest {

    @InjectMocks
    private ActionController actionController;

    @Mock
    private ActionService actionService;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetActions_ReturnsOk_WhenActionsListExists() {
        ArrayList<ID> actionIds = new ArrayList<>();
        actionIds.add(new ID(1));
        when(actionService.getAllIds()).thenReturn(actionIds);

        ResponseEntity<ArrayList<ID>> response = actionController.getActions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(actionIds, response.getBody());
    }

    @Test
    public void testGetAction_ReturnsOk_WhenActionExists() {
        Action action = new Action();
        when(actionService.getAction(new ID(1))).thenReturn(action);

        ResponseEntity<Action> response = actionController.getAction(new ID(1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(action, response.getBody());
    }

    @Test
    public void testGetAction_ReturnsNotFound_WhenActionNotExist() {
        when(actionService.getAction(new ID(1))).thenReturn(null);

        ResponseEntity<Action> response = actionController.getAction(new ID(1));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetActionDesc_ReturnsOk_WhenActionDescExists() {
        HashMap<Lang, Description> descriptions = new HashMap<>();
        descriptions.put(Lang.EN, new Description());
        Action action = new Action();
        action.setDescr(descriptions);

        when(actionService.getAction(new ID(1))).thenReturn(action);

        ResponseEntity<ArrayList<Version>> response = actionController.getActionDesc(new ID(1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetActionDesc_ReturnsNotFound_WhenActionDescNotExist() {
        when(actionService.getAction(new ID(1))).thenReturn(null);

        ResponseEntity<ArrayList<Version>> response = actionController.getActionDesc(new ID(1));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetActionHeading_ReturnsOk_WhenHeadingExists() {
        Description description = new Description();
        description.setFullName("Test Action");
        when(actionService.getDesc(new ID(1), Lang.EN)).thenReturn(description);

        ResponseEntity<String> response = actionController.getActionHeading(new ID(1), Lang.EN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Action", response.getBody());
    }

    @Test
    public void testGetActionHeading_ReturnsNotFound_WhenHeadingNotExist() {
        when(actionService.getDesc(new ID(1), Lang.EN)).thenReturn(null);

        ResponseEntity<String> response = actionController.getActionHeading(new ID(1), Lang.EN);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddAction_ReturnsCreated_WhenActionAddedSuccessfully() {
        ID actionId = new ID(1);
        Action newAction = new Action();
        when(actionService.create()).thenReturn(actionId);
        when(actionService.updateAction(actionId, newAction)).thenReturn(Errors.SUCCESS);

        ResponseEntity<ID> response = actionController.addAction(newAction);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(actionId, response.getBody());
    }

    @Test
    public void testAddAction_ReturnsInternalServerError_WhenActionAdditionFails() {
        ID actionId = new ID(1);
        Action newAction = new Action();
        when(actionService.create()).thenReturn(actionId);
        when(actionService.updateAction(actionId, newAction)).thenReturn(Errors.FAILURE);

        ResponseEntity<ID> response = actionController.addAction(newAction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testChangeDescription_ReturnsOk_WhenDescriptionUpdatedSuccessfully() {
        Description newDescription = new Description();
        when(actionService.setDesc(new ID(1), Lang.EN, newDescription)).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.changeDesc(new ID(1), Lang.EN, newDescription);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Description updated successfully.", response.getBody());
    }

    @Test
    public void testChangeDescription_ReturnsNotFound_WhenDescriptionUpdateFails() {
        Description newDescription = new Description();
        when(actionService.setDesc(new ID(1), Lang.EN, newDescription)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.changeDesc(new ID(1), Lang.EN, newDescription);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testDeleteAction_ReturnsOk_WhenActionDeletedSuccessfully() {
        when(actionService.remove(new ID(1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<Void> response = actionController.deleteAction(new ID(1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(actionService).remove(new ID(1));
    }

    @Test
    public void testDeleteAction_ReturnsNotFound_WhenActionDeletionFails() {
        when(actionService.remove(new ID(1))).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = actionController.deleteAction(new ID(1));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).remove(new ID(1));
    }

    @Test
    public void testChangeDesc_ReturnsOk_WhenDescriptionUpdatedSuccessfully() {
        Description newDescription = new Description();
        when(actionService.setDesc(new ID(1), Lang.EN, newDescription)).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.changeDesc(new ID(1), Lang.EN, newDescription);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Description updated successfully.", response.getBody());
        verify(actionService).setDesc(new ID(1), Lang.EN, newDescription);
    }

    @Test
    public void testChangeDesc_ReturnsNotFound_WhenDescriptionUpdateFails() {
        Description newDescription = new Description();
        when(actionService.setDesc(new ID(1), Lang.EN, newDescription)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.changeDesc(new ID(1), Lang.EN, newDescription);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).setDesc(new ID(1), Lang.EN, newDescription);
    }

    @Test
    public void testSetBegin_ReturnsOk_WhenBeginDateSetSuccessfully() {
        when(actionService.setBeg(new ID(1), LocalDate.of(2025, 1, 1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.setBegin(new ID(1), LocalDate.of(2025, 1, 1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Begin date set successfully.", response.getBody());
        verify(actionService).setBeg(new ID(1), LocalDate.of(2025, 1, 1));
    }

    @Test
    public void testSetBegin_ReturnsNotFound_WhenBeginDateSetFails() {
        when(actionService.setBeg(new ID(1), LocalDate.of(2025, 1, 1))).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.setBegin(new ID(1), LocalDate.of(2025, 1, 1));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).setBeg(new ID(1), LocalDate.of(2025, 1, 1));
    }

    @Test
    public void testSetEnd_ReturnsOk_WhenEndDateSetSuccessfully() {
        when(actionService.setEnd(new ID(1), LocalDate.of(2025, 12, 31))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.setEnd(new ID(1), LocalDate.of(2025, 12, 31));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("End date set successfully.", response.getBody());
        verify(actionService).setEnd(new ID(1), LocalDate.of(2025, 12, 31));
    }

    @Test
    public void testSetEnd_ReturnsNotFound_WhenEndDateSetFails() {
        when(actionService.setEnd(new ID(1), LocalDate.of(2025, 12, 31))).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.setEnd(new ID(1), LocalDate.of(2025, 12, 31));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).setEnd(new ID(1), LocalDate.of(2025, 12, 31));
    }
    @Test
    public void testAddStronglyMine_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        User user = new User();
        when(actionService.setStronglyMine(user, new ID(1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.addStronglyMine(new ID(1), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added to Strongly Mine successfully.", response.getBody());
        verify(actionService).setStronglyMine(user, new ID(1));
    }

    @Test
    public void testAddStronglyMine_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        User user = new User();
        when(actionService.setStronglyMine(user, new ID(1))).thenReturn(Errors.FAILURE);

        ResponseEntity<String> response = actionController.addStronglyMine(new ID(1), user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to add to Strongly Mine.", response.getBody());
        verify(actionService).setStronglyMine(user, new ID(1));
    }

    @Test
    public void testAddWeaklyMine_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        User user = new User();
        when(actionService.setWeaklyMine(user, new ID(1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.addWeaklyMine(new ID(1), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added to Weakly Mine successfully.", response.getBody());
        verify(actionService).setWeaklyMine(user, new ID(1));
    }

    @Test
    public void testAddWeaklyMine_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        User user = new User();
        when(actionService.setWeaklyMine(user, new ID(1))).thenReturn(Errors.FAILURE);

        ResponseEntity<String> response = actionController.addWeaklyMine(new ID(1), user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to add to Weakly Mine.", response.getBody());
        verify(actionService).setWeaklyMine(user, new ID(1));
    }

    @Test
    public void testAddRejected_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        User user = new User();
        when(actionService.setRejected(user, new ID(1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.addRejected(new ID(1), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added to Rejected successfully.", response.getBody());
        verify(actionService).setRejected(user, new ID(1));
    }

    @Test
    public void testAddRejected_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        User user = new User();
        when(actionService.setRejected(user, new ID(1))).thenReturn(Errors.FAILURE);

        ResponseEntity<String> response = actionController.addRejected(new ID(1), user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to add to Rejected.", response.getBody());
        verify(actionService).setRejected(user, new ID(1));
    }

    @Test
    public void testAddUndecided_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        User user = new User();
        when(actionService.setUndecided(user, new ID(1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.addUndecided(new ID(1), user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added to Undecided successfully.", response.getBody());
        verify(actionService).setUndecided(user, new ID(1));
    }

    @Test
    public void testAddUndecided_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        User user = new User();
        when(actionService.setUndecided(user, new ID(1))).thenReturn(Errors.FAILURE);

        ResponseEntity<String> response = actionController.addUndecided(new ID(1), user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to add to Undecided.", response.getBody());
        verify(actionService).setUndecided(user, new ID(1));
    }

    @Test
    public void testGetStronglyMine_ReturnsOk_WhenPreferencesExist() {
        User user = new User();
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getStronglyMine(user)).thenReturn(descriptions);

        ResponseEntity<ArrayList<Description>> response = actionController.getStronglyMine(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getStronglyMine(user);
    }

    @Test
    public void testGetWeaklyMine_ReturnsOk_WhenPreferencesExist() {
        User user = new User();
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getWeaklyMine(user)).thenReturn(descriptions);

        ResponseEntity<ArrayList<Description>> response = actionController.getWeaklyMine(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getWeaklyMine(user);
    }

    @Test
    public void testGetRejected_ReturnsOk_WhenPreferencesExist() {
        User user = new User();
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getRejected(user)).thenReturn(descriptions);

        ResponseEntity<ArrayList<Description>> response = actionController.getRejected(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getRejected(user);
    }

    @Test
    public void testGetUndecided_ReturnsOk_WhenPreferencesExist() {
        User user = new User();
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getUndecided(user)).thenReturn(descriptions);

        ResponseEntity<ArrayList<Description>> response = actionController.getUndecided(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getUndecided(user);
    }

}

//
//public class ActionControllerTest {
//
//    @InjectMocks
//    private ActionController actionController;
//
//    @Mock
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
//    //TODO DODAĆ TESTY DO HEADING
//
//    @Test
//    public void testGetActions_ReturnsOk_WhenActionsListExists() {
//        List<Action> actions = List.of(new Action());
//        when(actionService.getAllActions()).thenReturn(actions);
//
//        ResponseEntity<List<Action>> response = actionController.getActions();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(actions, response.getBody());
//    }
//
//    @Test
//    public void testGetAction_ReturnsOk_WhenActionExists() {
//        Action action = new Action();
//        when(actionService.getActionById(1L)).thenReturn(Optional.of(action));
//
//        ResponseEntity<Action> response = actionController.getAction(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(action, response.getBody());
//    }
//
//    @Test
//    public void testGetAction_ReturnsNotFound_WhenActionNotExist() {
//        when(actionService.getActionById(1L)).thenReturn(Optional.empty());
//
//        ResponseEntity<Action> response = actionController.getAction(1L);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    public void testGetActionDesc_ReturnsOk_WhenActionDescExists() {
//        String description = "Description";
//        when(actionService.getActionDescription(1L)).thenReturn(Optional.of(description));
//
//        ResponseEntity<DescriptionResponse> response = actionController.getActionDesc(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(description, Objects.requireNonNull(response.getBody()).description());
//    }
//
//    @Test
//    public void testGetActionDesc_ReturnsNotFound_WhenActionDescNotExist() {
//        when(actionService.getActionDescription(1L)).thenReturn(Optional.empty());
//
//        ResponseEntity<DescriptionResponse> response = actionController.getActionDesc(1L);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
////    @TestonStatus.OPEN,
////                LocalDate.now(),
////                LocalDate.now().plusDays(1),
////                2L
////        );
////        Action action = new Action();
////        when(actionService.get
//    ////    public void testAddAction_ReturnsCreated_WhenSuccess() {
//    ////        AddActionRequest request = new AddActionRequest(
//    ////                1L,
//    ////                "Heading",
//    ////                "Description",
//    ////                ActiLeader(2L)).thenReturn(Optional.of(new Volunteer()));
////        when(actionService.createAndAddAction(request)).thenReturn(action);
////
////        ResponseEntity<?> response = actionController.addAction(request);
////
////        assertEquals(HttpStatus.CREATED, response.getStatusCode());
////        assertEquals(action, response.getBody());
////    }
//
////    @Test
////    public void testAddAction_ReturnsForbidden_WhenLeaderNotFound() {
////        AddActionRequest request = new AddActionRequest(
////                1L,
////                "Heading",
////                "Description",
////                ActionStatus.OPEN,
////                LocalDate.now(),
////                LocalDate.now().plusDays(1),
////                2L
////        );
////        when(actionService.getLeader(2L)).thenReturn(Optional.empty());
////
////        ResponseEntity<?> response = actionController.addAction(request);
////
////        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
////    }
//
////    @Test
////    public void testAddAction_ReturnsBadRequest_WhenExceptionThrown() {
////        AddActionRequest request = new AddActionRequest(
////                1L,
////                "Heading",
////                "Description",
////                ActionStatus.OPEN,
////                LocalDate.now(),
////                LocalDate.now().plusDays(1),
////                2L
////        );
////        when(actionService.getLeader(2L)).thenReturn(Optional.of(new Volunteer()));
////        when(actionService.createAndAddAction(request)).thenThrow(new RuntimeException("Some error message"));
////
////        ResponseEntity<?> response = actionController.addAction(request);
////
////        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
////        assertEquals("Some error message", response.getBody());
////    }
//
////    @Test
////    public void testChangeDescription_ReturnsOk_WhenSuccess() {
////        ChangeDescriptionRequest request = new ChangeDescriptionRequest(1L, "New Description");
////        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(true);
////        when(actionRepository.existsById(1L)).thenReturn(true);
////
////        ResponseEntity<?> response = actionController.changeDescription(1L, request);
////
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        verify(actionService).changeDescription(1L, "New Description");
////    }
//
//    @Test
//    public void testChangeDescription_ReturnsForbidden_WhenLeaderNotFound() {
//        ChangeDescriptionRequest request = new ChangeDescriptionRequest(1L, "New Description");
//        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(false);
//
//        ResponseEntity<?> response = actionController.changeDescription(1L, request);
//
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    public void testChangeDescription_ReturnsNotFound_WhenActionNotFound() {
//        ChangeDescriptionRequest request = new ChangeDescriptionRequest(1L, "New Description");
//        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(true);
//        when(actionRepository.existsById(1L)).thenReturn(false);
//
//        ResponseEntity<?> response = actionController.changeDescription(1L, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
////    @Test
////    public void testCloseAction_ReturnsOk_WhenSuccess() {
////        CloseActionRequest request = new CloseActionRequest(1L);
////        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.ADMIN)).thenReturn(true);
////        when(actionRepository.existsById(1L)).thenReturn(true);
////
////        ResponseEntity<?> response = actionController.closeAction(1L, request);
////
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        verify(actionService).closeAction(1L, 1L);
////    }
//
//    @Test
//    public void testCloseAction_ReturnsForbidden_WhenAdminNotFound() {
//        CloseActionRequest request = new CloseActionRequest(1L);
//        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.ADMIN)).thenReturn(false);
//
//        ResponseEntity<?> response = actionController.closeAction(1L, request);
//
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    public void testCloseAction_ReturnsNotFound_WhenActionNotFound() {
//        CloseActionRequest request = new CloseActionRequest(1L);
//        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.ADMIN)).thenReturn(true);
//        when(actionRepository.existsById(1L)).thenReturn(false);
//
//        ResponseEntity<?> response = actionController.closeAction(1L, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//}
