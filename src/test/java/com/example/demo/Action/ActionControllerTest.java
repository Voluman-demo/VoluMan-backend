package com.example.demo.Action;

import com.example.demo.Model.Errors;

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
import java.util.List;
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
        ArrayList<Long> actionIds = new ArrayList<>();
        actionIds.add(1L);
        when(actionService.getAllIds()).thenReturn(actionIds);

        ResponseEntity<ArrayList<Long>> response = actionController.getActions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(actionIds, response.getBody());
    }

    @Test
    public void testGetAction_ReturnsOk_WhenActionExists() {
        Action action = new Action();
        when(actionService.getAction(1L)).thenReturn(action);

        ResponseEntity<Action> response = actionController.getAction(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(action, response.getBody());
    }

    @Test
    public void testGetAction_ReturnsNotFound_WhenActionNotExist() {
        when(actionService.getAction(1L)).thenReturn(null);

        ResponseEntity<Action> response = actionController.getAction(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetActionDesc_ReturnsOk_WhenActionDescExists() {
        HashMap<Lang, Description> descriptions = new HashMap<>();
        descriptions.put(Lang.EN, new Description());
        Action action = new Action();
        action.setDescr(descriptions);

        when(actionService.getAction(1L)).thenReturn(action);

        ResponseEntity<ArrayList<Version>> response = actionController.getActionDesc(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetActionDesc_ReturnsNotFound_WhenActionDescNotExist() {
        when(actionService.getAction(1L)).thenReturn(null);

        ResponseEntity<ArrayList<Version>> response = actionController.getActionDesc(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetActionHeading_ReturnsOk_WhenHeadingExists() {
        Description description = new Description();
        description.setFullName("Test Action");
        when(actionService.getDesc(1L, Lang.EN)).thenReturn(description);

        ResponseEntity<String> response = actionController.getActionHeading(1L, Lang.EN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Action", response.getBody());
    }

    @Test
    public void testGetActionHeading_ReturnsNotFound_WhenHeadingNotExist() {
        when(actionService.getDesc(1L, Lang.EN)).thenReturn(null);

        ResponseEntity<String> response = actionController.getActionHeading(1L, Lang.EN);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddAction_ReturnsCreated_WhenActionAddedSuccessfully() {
        Long actionId = 1L;
        Action newAction = new Action();
        when(actionService.create()).thenReturn(actionId);
        when(actionService.updateAction(actionId, newAction)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Long> response = actionController.addAction(newAction);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(actionId, response.getBody());
    }

    @Test
    public void testAddAction_ReturnsInternalServerError_WhenActionAdditionFails() {
        Long actionId = 1L;
        Action newAction = new Action();
        when(actionService.create()).thenReturn(actionId);
        when(actionService.updateAction(actionId, newAction)).thenReturn(Errors.FAILURE);

        ResponseEntity<Long> response = actionController.addAction(newAction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testChangeDescription_ReturnsOk_WhenDescriptionUpdatedSuccessfully() {
        Description newDescription = new Description();
        when(actionService.setDesc(1L, Lang.EN, newDescription)).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.changeDesc(1L, Lang.EN, newDescription);

        assertEquals(null, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testChangeDescription_ReturnsNotFound_WhenDescriptionUpdateFails() {
        Description newDescription = new Description();
        when(actionService.setDesc(1L, Lang.EN, newDescription)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.changeDesc(1L, Lang.EN, newDescription);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testDeleteAction_ReturnsOk_WhenActionDeletedSuccessfully() {
        when(actionService.remove(1L)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Void> response = actionController.deleteAction(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(actionService).remove(1L);
    }

    @Test
    public void testDeleteAction_ReturnsNotFound_WhenActionDeletionFails() {
        when(actionService.remove(1L)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<Void> response = actionController.deleteAction(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).remove(1L);
    }

    @Test
    public void testChangeDesc_ReturnsOk_WhenDescriptionUpdatedSuccessfully() {
        Description newDescription = new Description();
        when(actionService.setDesc(1L, Lang.EN, newDescription)).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.changeDesc(1L, Lang.EN, newDescription);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setDesc(1L, Lang.EN, newDescription);
    }

    @Test
    public void testChangeDesc_ReturnsNotFound_WhenDescriptionUpdateFails() {
        Description newDescription = new Description();
        when(actionService.setDesc(1L, Lang.EN, newDescription)).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.changeDesc(1L, Lang.EN, newDescription);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).setDesc(1L, Lang.EN, newDescription);
    }

    @Test
    public void testSetBegin_ReturnsOk_WhenBeginDateSetSuccessfully() {
        when(actionService.setBeg(1L, LocalDate.of(2025, 1, 1))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.setBegin(1L, LocalDate.of(2025, 1, 1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setBeg(1L, LocalDate.of(2025, 1, 1));
    }

    @Test
    public void testSetBegin_ReturnsNotFound_WhenBeginDateSetFails() {
        when(actionService.setBeg(1L, LocalDate.of(2025, 1, 1))).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.setBegin(1L, LocalDate.of(2025, 1, 1));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).setBeg(1L, LocalDate.of(2025, 1, 1));
    }

    @Test
    public void testSetEnd_ReturnsOk_WhenEndDateSetSuccessfully() {
        when(actionService.setEnd(1L, LocalDate.of(2025, 12, 31))).thenReturn(Errors.SUCCESS);

        ResponseEntity<String> response = actionController.setEnd(1L, LocalDate.of(2025, 12, 31));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setEnd(1L, LocalDate.of(2025, 12, 31));
    }

    @Test
    public void testSetEnd_ReturnsNotFound_WhenEndDateSetFails() {
        when(actionService.setEnd(1L, LocalDate.of(2025, 12, 31))).thenReturn(Errors.NOT_FOUND);

        ResponseEntity<String> response = actionController.setEnd(1L, LocalDate.of(2025, 12, 31));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(actionService).setEnd(1L, LocalDate.of(2025, 12, 31));
    }
    @Test
    public void testAddStronglyMine_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        when(actionService.setPref(1L, "S", 1L)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Errors> response = actionController.setPref(1L, "S", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "S", 1L);;
    }

    @Test
    public void testAddStronglyMine_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        when(actionService.setPref(1L, "S", 1L)).thenReturn(Errors.FAILURE);

        ResponseEntity<Errors> response = actionController.setPref(1L, "S", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "S", 1L);
    }

    @Test
    public void testAddWeaklyMine_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        when(actionService.setPref(1L, "W", 1L)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Errors> response = actionController.setPref(1L, "W", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "W", 1L);
    }

    @Test
    public void testAddWeaklyMine_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        when(actionService.setPref(1L, "W", 1L)).thenReturn(Errors.FAILURE);

        ResponseEntity<Errors> response = actionController.setPref(1L, "W", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "W", 1L);
    }

    @Test
    public void testAddRejected_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        when(actionService.setPref(1L, "R", 1L)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Errors> response = actionController.setPref(1L, "R", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "R", 1L);
    }

    @Test
    public void testAddRejected_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        when(actionService.setPref(1L, "R", 1L)).thenReturn(Errors.FAILURE);

        ResponseEntity<Errors> response = actionController.setPref(1L, "R", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "R", 1L);
    }

    @Test
    public void testAddUndecided_ReturnsOk_WhenPreferenceAddedSuccessfully() {
        when(actionService.setPref(1L, "U", 1L)).thenReturn(Errors.SUCCESS);

        ResponseEntity<Errors> response = actionController.setPref(1L, "U", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "U", 1L);
    }

    @Test
    public void testAddUndecided_ReturnsInternalServerError_WhenPreferenceAdditionFails() {
        when(actionService.setPref(1L, "U", 1L)).thenReturn(Errors.FAILURE);

        ResponseEntity<Errors> response = actionController.setPref(1L, "U", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(actionService).setPref(1L, "U", 1L);
    }

    @Test
    public void testGetStronglyMine_ReturnsOk_WhenPreferencesExist() {
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getPref("S", 1L)).thenReturn(descriptions);

        ResponseEntity<List<Description>> response = actionController.getPref("S", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getPref("S", 1L);
    }

    @Test
    public void testGetWeaklyMine_ReturnsOk_WhenPreferencesExist() {
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getPref("W", 1L)).thenReturn(descriptions);

        ResponseEntity<List<Description>> response = actionController.getPref("W", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getPref("W", 1L);
    }

    @Test
    public void testGetRejected_ReturnsOk_WhenPreferencesExist() {
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getPref("R", 1L)).thenReturn(descriptions);

        ResponseEntity<List<Description>> response = actionController.getPref("R", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getPref("R", 1L);
    }

    @Test
    public void testGetUndecided_ReturnsOk_WhenPreferencesExist() {
        ArrayList<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description());

        when(actionService.getPref("U", 1L)).thenReturn(descriptions);

        ResponseEntity<List<Description>> response = actionController.getPref("U", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(descriptions, response.getBody());
        verify(actionService).getPref("U", 1L);
    }

}