package com.example.demo.Schedule;

import com.example.demo.Schedule.Dto.*;
import com.example.demo.Volunteer.LeaderDto;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import com.example.demo.action.Action;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.Dto.ActionScheduleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;



    @Mock
    private ActionRepository actionRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private ScheduleController scheduleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ActionNeedRequest createActionNeedRequest() {
        ActionNeedRequest request = new ActionNeedRequest();
        request.setLeaderId(1L);

        ActionNeedRequest.DayRequest dayRequest = new ActionNeedRequest.DayRequest();
        dayRequest.setDate(LocalDate.now());

        ActionNeedRequest.SlotRequest slotRequest = new ActionNeedRequest.SlotRequest();
        slotRequest.setStartTime(LocalTime.of(9, 0));
        slotRequest.setEndTime(LocalTime.of(9, 30));
        slotRequest.setNeedMin(2L);
        slotRequest.setNeedMax(5L);

        dayRequest.setSlots(Collections.singletonList(slotRequest));
        request.setDays(Collections.singletonList(dayRequest));

        return request;
    }

    private VolunteerAvailRequest createVolunteerAvailRequest() {
        VolunteerAvailRequest request = new VolunteerAvailRequest();
        request.setLimitOfHours(40L);

        VolunteerAvailRequest.DayAvailabilityRequest dayRequest = new VolunteerAvailRequest.DayAvailabilityRequest();
        dayRequest.setDate(LocalDate.now());

        VolunteerAvailRequest.AvailabilitySlotRequest slotRequest = new VolunteerAvailRequest.AvailabilitySlotRequest();
        slotRequest.setStartTime(LocalTime.of(9, 0));
        slotRequest.setEndTime(LocalTime.of(9, 30));

        dayRequest.setSlots(Collections.singletonList(slotRequest));
        request.setDays(Collections.singletonList(dayRequest));

        return request;
    }
    @Test
    void testChoosePref_ReturnsNotFound_WhenActionNotFound() {//Klaudiusz jeszcze sie bawi tym
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testChoosePref_ReturnsNotFound_VolunteerNotFound() {//Klaudiusz jeszcze sie bawi tym
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsById(request.volunteerId())).thenReturn(false);

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testChoose_ReturnsOk_Pref_Success() {//Klaudiusz jeszcze sie bawi tym
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsById(request.volunteerId())).thenReturn(true);

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
 //TODO
   /* @Test
    void testChooseNeed_Success() throws Exception {
        int year = 2024;
        int week = 27;
        Long actionId = 1L;
        ActionNeedRequest request = createActionNeedRequest();

        ResponseEntity<?> response = scheduleController.chooseNeed(year, week, actionId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testChooseAvail_Success() throws Exception {
        int year = 2024;
        int week = 27;
        Long volunteerId = 1L;
        VolunteerAvailRequest request = createVolunteerAvailRequest();

        ResponseEntity<?> response = scheduleController.chooseAvail(year, week, volunteerId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/

 /*   @Test
    void testGenerateSchedule_Success() {
        int year = 2024;
        int week = 27;
        GenerateScheduleRequest request = new GenerateScheduleRequest(LocalDate.now());

        ResponseEntity<?> response = scheduleController.generateSchedule(year, week, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/

    @Test
    void testChoosePref_Exception() {
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsById(request.volunteerId())).thenReturn(true);
        doThrow(new RuntimeException("Error")).when(scheduleService).choosePref(anyLong(), any(ActionPrefRequest.class));

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }
//TODO
   /* @Test
    void testChooseNeed_Exception() throws Exception {
        int year = 2024;
        int week = 27;
        Long actionId = 1L;
        ActionNeedRequest request = createActionNeedRequest();

        doThrow(new Exception("Error")).when(scheduleService).scheduleNeedAction(anyLong(), anyInt(), anyInt(), any(ActionNeedRequest.class));

        ResponseEntity<?> response = scheduleController.chooseNeed(year, week, actionId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }*/
    //TODO
    /*@Test
    void testChooseAvail_Exception() throws Exception {
        int year = 2024;
        int week = 27;
        Long volunteerId = 1L;
        VolunteerAvailRequest request = createVolunteerAvailRequest();

        doThrow(new Exception("Error")).when(scheduleService).chooseAvailabilities(anyLong(), anyInt(), anyInt(), any(VolunteerAvailRequest.class));

        ResponseEntity<?> response = scheduleController.chooseAvail(year, week, volunteerId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }*/
    @Test
    void testGenerateSchedule_Success() {
        // Given
        int year = 2024;
        int week = 27;
        LocalDate date = LocalDate.of(year, 7, 1);
        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, date);

        when(volunteerRepository.existsByVolunteerIdAndRole(any(Long.class), any(VolunteerRole.class))).thenReturn(true);

        // When
        ResponseEntity<?> response = scheduleController.generateSchedule(year, week, request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(scheduleService, times(1)).generateSchedule(date);
    }
    @Test
    void testGenerateSchedule_AdminNotFound() {
        // Given
        int year = 2024;
        int week = 27;
        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, LocalDate.of(year, 1, 1));

        when(volunteerRepository.existsByVolunteerIdAndRole(any(Long.class), any(VolunteerRole.class))).thenReturn(false);

        // When
        ResponseEntity<?> response = scheduleController.generateSchedule(year, week, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(scheduleService, never()).generateSchedule(any(LocalDate.class));
    }

    @Test
    void testGenerateSchedule_DateMismatch() {
        // Given
        int year = 2024;
        int week = 27;
        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, LocalDate.of(year, 1, 1));

        when(volunteerRepository.existsByVolunteerIdAndRole(any(Long.class), any(VolunteerRole.class))).thenReturn(true);

        // When
        ResponseEntity<?> response = scheduleController.generateSchedule(year, week, request);

        // Then
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        verify(scheduleService, never()).generateSchedule(any(LocalDate.class));
    }


    @Test
    void testGenerateSchedule_Exception() {
        // Given
        int year = 2024;
        int week = 27;
        LocalDate date = LocalDate.of(year, 7, 1);
        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, date);

        when(volunteerRepository.existsByVolunteerIdAndRole(any(Long.class), any(VolunteerRole.class))).thenReturn(true);
        doThrow(new RuntimeException("Error")).when(scheduleService).generateSchedule(date);

        // When
        ResponseEntity<?> response = scheduleController.generateSchedule(year, week, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error generating schedule.", response.getBody());
    }




    @Test
    void testGetScheduleByAction_R_ActionNotFound() {
        Long actionId = 1L;
        ScheduleByActionRequest request = new ScheduleByActionRequest(1L);

        when(actionRepository.existsById(actionId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    void testGetScheduleByAction_LeaderNotFound() {
        Long actionId = 1L;
        ScheduleByActionRequest request = new ScheduleByActionRequest(1L);

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(request.leaderId(), VolunteerRole.LEADER)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

    }

    @Test
    void testGetScheduleByAction_Conflict() {
        Long actionId = 1L;
        ScheduleByActionRequest request = new ScheduleByActionRequest(2L);

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(request.leaderId(), VolunteerRole.LEADER)).thenReturn(true);
        LeaderDto leaderDto = new LeaderDto(1L, "John", "Doe", "john.doe@example.com", "123456789");
        Action action = new Action();
        action.setLeader(leaderDto);
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    }

    @Test
    void testGetScheduleByAction_Success() {
        Long actionId = 1L;
        ScheduleByActionRequest request = new ScheduleByActionRequest(1L);
        ActionScheduleDto scheduleDto = new ActionScheduleDto(actionId, "Heading", "Description", Collections.emptyList());

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(request.leaderId(), VolunteerRole.LEADER)).thenReturn(true);
        LeaderDto leaderDto = new LeaderDto(1L, "John", "Doe", "john.doe@example.com", "123456789");
        Action action = new Action();
        action.setLeader(leaderDto);
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        when(scheduleService.getActionSchedule(actionId)).thenReturn(scheduleDto);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(scheduleDto, response.getBody());
    }

    @Test
    void testGetScheduleByAction_Exception() {
        Long actionId = 1L;
        ScheduleByActionRequest request = new ScheduleByActionRequest(1L);

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndRole(request.leaderId(), VolunteerRole.LEADER)).thenReturn(true);

        // Create a LeaderDto object
        LeaderDto leaderDto = new LeaderDto(1L, "John", "Doe", "john.doe@example.com", "123456789");

        Action action = new Action();
        action.setLeader(leaderDto); // Set the leader using LeaderDto
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        doThrow(new RuntimeException("Error")).when(scheduleService).getActionSchedule(actionId);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error retrieving action schedule.", response.getBody());
    }


}
