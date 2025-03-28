package com.example.demo.Schedule;

import com.example.demo.Schedule.ScheduleDto.*;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Action.Action;
import com.example.demo.Action.ActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

//    private ActionNeedRequest createActionNeedRequest() {
//        ActionNeedRequest request = new ActionNeedRequest();
//        request.setLeaderId(1L);
//
//        ActionNeedRequest.DayRequest dayRequest = new ActionNeedRequest.DayRequest();
//        dayRequest.setDate(LocalDate.now());
//
//        ActionNeedRequest.SlotRequest slotRequest = new ActionNeedRequest.SlotRequest();
//        slotRequest.setStartTime(LocalTime.of(9, 0));
//        slotRequest.setEndTime(LocalTime.of(9, 30));
//        slotRequest.setNeedMin(2L);
//        slotRequest.setNeedMax(5L);
//
//        dayRequest.setSlots(Collections.singletonList(slotRequest));
//        request.setDays(Collections.singletonList(dayRequest));
//
//        return request;
//    }
//
//    private VolunteerAvailRequest createVolunteerAvailRequest() {
//        VolunteerAvailRequest request = new VolunteerAvailRequest();
//        request.setLimitOfHours(40L);
//
//        VolunteerAvailRequest.DayAvailabilityRequest dayRequest = new VolunteerAvailRequest.DayAvailabilityRequest();
//        dayRequest.setDate(LocalDate.now());
//
//        VolunteerAvailRequest.AvailabilitySlotRequest slotRequest = new VolunteerAvailRequest.AvailabilitySlotRequest();
//        slotRequest.setStartTime(LocalTime.of(9, 0));
//        slotRequest.setEndTime(LocalTime.of(9, 30));
//
//        dayRequest.setSlots(Collections.singletonList(slotRequest));
//        request.setDays(Collections.singletonList(dayRequest));
//
//        return request;
//    }
//
//    @Test
//    void testChoosePref_ReturnsNotFound_WhenActionNotFound() {
//        Long actionId = 1L;
//        ActionPrefRequest request = new ActionPrefRequest(1L, "T");
//
//        when(actionRepository.existsById(actionId)).thenReturn(false);
//
//        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void testChoosePref_ReturnsNotFound_VolunteerNotFound() {
//        Long actionId = 1L;
//        ActionPrefRequest request = new ActionPrefRequest(1L, "T");
//
//        when(actionRepository.existsById(actionId)).thenReturn(true);
//        when(volunteerRepository.existsById(request.volunteerId())).thenReturn(false);
//
//        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }

//    @Test
//    void testChoose_ReturnsOk_WhenSuccess() {
//        Long actionId = 1L;
//        ActionPrefRequest request = new ActionPrefRequest(1L, "T");
//
//        when(actionRepository.existsById(actionId)).thenReturn(true);
//        when(volunteerRepository.existsById(request.volunteerId())).thenReturn(true);
//
//        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }

//    @Test
//    void testChooseNeed_ReturnsOk_Success() throws Exception {
//        int year = 2024;
//        int week = 27;
//        Long actionId = 1L;
//        ActionNeedRequest request = createActionNeedRequest();
//        request.setLeaderId(1L);
//
//        // Mock the existence of the action
//        when(actionRepository.existsById(actionId)).thenReturn(true);
//        // Mock the existence of the leader
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.getLeaderId(), Position.LEADER)).thenReturn(true);
//        Action action = new Action();
//        action.setLeader(new LeaderDto(request.getLeaderId(), "John", "Doe", "john.doe@example.com", "123456789"));
//        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
//        // Assuming the service call is expected to succeed
//        doNothing().when(scheduleService).scheduleNeedAction(anyID(), anyInt(), anyInt(), any(ActionNeedRequest.class));
//
//        ResponseEntity<?> response = scheduleController.chooseNeed(actionId, year, week,  request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }

//    @Test
//    void testChooseNeed_ReturnsConflict_WhenLeaderNotAuthorized() {
//        // Given
//        int year = 2024;
//        int week = 27;
//        Long actionId = 1L;
//        Long VolunteerId = 1L;
//        ActionNeedRequest request = createActionNeedRequest();
//        request.setLeaderId(VolunteerId);
//
//        // Mockowanie istnienia akcji
//        when(actionRepository.existsById(actionId)).thenReturn(true);
//        // Mockowanie braku odpowiedniej roli lidera
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.getLeaderId(), Position.LEADER)).thenReturn(false);
//
//        // When
//        ResponseEntity<?> response = scheduleController.chooseNeed(actionId, year, week, request);
//
//        // Then
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//    @Test
//    void testChooseNeed_ReturnsConflict_WhenLeaderMismatch() {
//        // Given
//        int year = 2024;
//        int week = 27;
//        Long actionId = 1L;
//
//        ActionNeedRequest request = createActionNeedRequest();
//        request.setLeaderId(1L);
//
//        // Mockowanie istnienia akcji
//        when(actionRepository.existsById(actionId)).thenReturn(true);
//
//        // Mockowanie lidera akcji jako inny lider
//        Action action = new Action();
//        action.setLeader(new LeaderDto(2L, "John", "Doe", "john.doe@example.com", "123456789"));
//        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
//
//        // Mockowanie istnienia lidera
//        when(volunteerRepository.existsByVolunteerIdAndPosition(request.getLeaderId(), Position.LEADER)).thenReturn(true);
//
//        // When
//        ResponseEntity<?> response = scheduleController.chooseNeed(actionId,year, week,  request);
//
//        // Then
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//    }
//    @Test
//    void testChooseNeed_Exception() throws Exception {
//        // Given
//        int year = 2024;
//        int week = 27;
//        Long actionId = 1L;
//        Long leaderId = 1L;
//        ActionNeedRequest request = createActionNeedRequest();
//        request.setLeaderId(leaderId);
//
//        // Mockowanie istnienia akcji
//        when(actionRepository.existsById(actionId)).thenReturn(true);
//        // Mockowanie istnienia lidera
//        when(volunteerRepository.existsByVolunteerIdAndPosition(leaderId, Position.LEADER)).thenReturn(true);
//        Action action = new Action();
//        action.setLeader(new LeaderDto(leaderId, "John", "Doe", "john.doe@example.com", "123456789"));
//        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
//        // Mockowanie wyjątku z serwisu
//        doThrow(new RuntimeException("Error")).when(scheduleService).scheduleNeedAction(anyID(), anyInt(), anyInt(), any(ActionNeedRequest.class));
//
//        // When
//        ResponseEntity<?> response = scheduleController.chooseNeed(actionId, year, week,  request);
//
//        // Then
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Error", response.getBody());
//    }

//    @Test
//    void testChooseAvail_ReturnsOk_Success() throws Exception {
//        int year = 2024;
//        int week = 27;
//        Long volunteerId = 1L;
//        VolunteerAvailRequest request = createVolunteerAvailRequest();
//
//        // Mock the existence of the volunteer
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//
//        // Assuming the service call is expected to succeed
//        doNothing().when(scheduleService).chooseAvailabilities(anyID(), anyInt(), anyInt(), any(VolunteerAvailRequest.class));
//
//        ResponseEntity<?> response = scheduleController.chooseAvail(volunteerId, year, week,  request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }

//    @Test
//   void testChooseAvail_ReturnsNotFound_WhenVolunteerNotFound() {
//        int year = 2024;
//        int week = 27;
//        Long volunteerId = 1L;
//        VolunteerAvailRequest request = createVolunteerAvailRequest();
//
//        // Mock the non-existence of the volunteer
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);
//
//        ResponseEntity<?> response = scheduleController.chooseAvail(volunteerId, year, week, request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }*//*
//
//    @Test
//    void testChooseAvail_Exception() throws Exception {
//        int year = 2024;
//        int week = 27;
//        Long volunteerId = 1L;
//        VolunteerAvailRequest request = createVolunteerAvailRequest();
//
//        // Mock the existence of the volunteer
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//
//        // Mocking an exception from the service
//        doThrow(new RuntimeException("Error")).when(scheduleService).chooseAvailabilities(anyID(), anyInt(), anyInt(), any(VolunteerAvailRequest.class));
//
//        ResponseEntity<?> response = scheduleController.chooseAvail(volunteerId, year, week, request);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Error", response.getBody());
//    }
//

//    @Test
//    void testGenerateSchedule_ReturnsOk_Success() {
//        int year = 2024;
//        int week = 27;
//        LocalDate date = LocalDate.of(year, 7, 1);
//        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, date);
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(any(Long.class), any(Position.class))).thenReturn(true);
//
//        ResponseEntity<?> response = scheduleController.generateSchedule(year, week, request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        verify(scheduleService, times(1)).generateSchedule(date);
//    }

    @Test
    void testGetScheduleByVolunteer_ReturnsNotFound_WhenVolunteerNotFound() {
        Long volunteerId = 1L;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.getScheduleByVolunteer(volunteerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetScheduleByVolunteer_ReturnsNotFound_WhenNoSchedulesFound() {
        Long volunteerId = 1L;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
        when(scheduleService.getVolunteerSchedules(volunteerId)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = scheduleController.getScheduleByVolunteer(volunteerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetScheduleByVolunteer_ReturnsOk_WithSchedules() {
        Long volunteerId = 1L;

        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);

        Schedule schedule = new Schedule();
        List<Schedule> schedules = List.of(schedule);
        when(scheduleService.getVolunteerSchedules(volunteerId)).thenReturn(schedules);

        ResponseEntity<?> response = scheduleController.getScheduleByVolunteer(volunteerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(schedules, response.getBody());
    }



    //TODO
//
//    @Test
//    void testGenerateSchedule_ReturnsNotFound_AdminNotFound() {
//        int year = 2024;
//        int week = 27;
//        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, LocalDate.of(year, 1, 1));
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(any(Long.class), any(Position.class))).thenReturn(false);
//
//        ResponseEntity<?> response = scheduleController.generateSchedule(request);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(scheduleService, never()).generateSchedule(any(LocalDate.class));
//    }
//
//    @Test
//    void testGenerateSchedule_ReturnsNotAcceptable_WhenDateMismatch() {
//        int year = 2024;
//        int week = 27;
//        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, LocalDate.of(year, 1, 1));
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(any(Long.class), any(Position.class))).thenReturn(true);
//
//        ResponseEntity<?> response = scheduleController.generateSchedule(request);
//
//        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
//        verify(scheduleService, never()).generateSchedule(any(LocalDate.class));
//    }
//
//    @Test
//    void testGenerateSchedule_Exception() {
//        int year = 2024;
//        int week = 27;
//        LocalDate date = LocalDate.of(year, 7, 1);
//        GenerateScheduleRequest request = new GenerateScheduleRequest(1L, date);
//
//        when(volunteerRepository.existsByVolunteerIdAndPosition(any(Long.class), any(Position.class))).thenReturn(true);
//        doThrow(new RuntimeException("Error")).when(scheduleService).generateSchedule(date);
//
//        ResponseEntity<?> response = scheduleController.generateSchedule(request);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("Error generating schedule.", response.getBody());
//    }

    @Test
    void testGetScheduleByAction_ReturnsNotFound_WhenActionNotFound() {
        Long actionId = 1L;
        Long leaderId = 1L;
        when(volunteerRepository.existsByVolunteerIdAndPosition(eq(leaderId), eq(Position.LEADER))).thenReturn(true);
        when(actionRepository.existsById(actionId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, leaderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetScheduleByAction_ReturnsNotFound_WhenNoSchedulesFound() {
        Long actionId = 1L;
        Long leaderId = 1L;

        Action action = new Action();
        action.setActionId(actionId);

        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        when(scheduleService.getActionSchedules(actionId)).thenReturn(Collections.emptyList());
        when(volunteerRepository.existsByVolunteerIdAndPosition(eq(leaderId), eq(Position.LEADER))).thenReturn(true);
        when(actionRepository.existsById(actionId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, leaderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetScheduleByAction_ReturnsOk_WithSchedules() {
        Long actionId = 1L;
        Long leaderId = 1L;

        Schedule schedule = new Schedule();
        List<Schedule> schedules = new ArrayList<>(List.of(schedule));
        Action action = new Action();
        action.setActionId(actionId);
        schedules.add(schedule);

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(eq(leaderId), eq(Position.LEADER))).thenReturn(true);
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        when(scheduleService.getActionSchedules(actionId)).thenReturn(schedules);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, leaderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(schedules, response.getBody());
    }


    @Test
    void testGetScheduleByAction_ReturnsOk_Success() {
        Long actionId = 1L;
        Long leaderId = 1L;

        Action action = new Action();
        action.setActionId(actionId);
        Schedule schedule = new Schedule();
        List<Schedule> schedules = new ArrayList<>();
        schedules.add(schedule);

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsByVolunteerIdAndPosition(eq(leaderId), eq(Position.LEADER))).thenReturn(true);
        when(actionRepository.findById(actionId)).thenReturn(Optional.of(action));
        when(scheduleService.getActionSchedules(actionId)).thenReturn(schedules);

        ResponseEntity<?> response = scheduleController.getScheduleByAction(actionId, leaderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testModifySchedule_ReturnsNotFound_VolunteerNotFound() {
        Long volunteerId = 1L;
        ModifyScheduleRequest request = new ModifyScheduleRequest(1L, Collections.emptyList(), ModificationType.ADD);

        when(volunteerRepository.existsById(volunteerId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.modifySchedule(volunteerId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

//    @Test
//    void testModifySchedule_Success() throws Exception {
//        int year = 2024;
//        int week = 27;
//        Long volunteerId = 1L;
//        ModifyScheduleRequest request = new ModifyScheduleRequest(1L, Collections.emptyList());
//
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//        doNothing().when(scheduleService).modifySchedule(volunteerId, year, week, request);
//
//        ResponseEntity<?> response = scheduleController.modifySchedule(volunteerId, year, week, request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }

//    @Test
//    void testModifySchedule_Exception() throws Exception {
//        int year = 2024;
//        int week = 27;
//        Long volunteerId = 1L;
//        ModifyScheduleRequest request = new ModifyScheduleRequest(1L, Collections.emptyList());
//
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//        doThrow(new RuntimeException("Error")).when(scheduleService).modifySchedule(volunteerId, year, week, request);
//
//        ResponseEntity<?> response = scheduleController.modifySchedule(volunteerId, year, week, request);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Error", response.getBody());
//    }
//
//    @Test
//    void testGetScheduleByVolunteer_Success() {
//        int year = 2023;
//        int week = 10;
//        Long volunteerId = 1L;
//
////        VolunteerScheduleDto scheduleDto = new VolunteerScheduleDto(
////                volunteerId,
////                "John",
////                "Doe",
////                Collections.singletonList(new DutyIntervalDto(
////                        1L,
////                        LocalDate.now(),
////                        LocalTime.of(9, 0),
////                        LocalTime.of(11, 0),
////                        new ActionDto(1L, "Sample Action")
////                ))
////        );
//
//        when(volunteerRepository.existsById(volunteerId)).thenReturn(true);
//        when(scheduleService.getVolunteerSchedules(volunteerId)).thenReturn(scheduleDto);
//
//        ResponseEntity<?> response = scheduleController.getScheduleByVolunteer(volunteerId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        VolunteerScheduleDto responseBody = (VolunteerScheduleDto) response.getBody();
//
//        assertEquals(volunteerId, responseBody.volunteerId());
//        assertEquals("John", responseBody.name());
//        assertEquals("Doe", responseBody.lastname());
////        assertEquals(1, responseBody.dutyIntervals().size());
////        assertEquals(1L, responseBody.dutyIntervals().get(0).intervalId());
////        assertEquals(LocalTime.of(9, 0), responseBody.dutyIntervals().get(0).startTime());
////        assertEquals(LocalTime.of(11, 0), responseBody.dutyIntervals().get(0).endTime());
////        assertEquals(1L, responseBody.dutyIntervals().get(0).action().actionId());
////        assertEquals("Sample Action", responseBody.dutyIntervals().get(0).action().heading());
//    }
}



