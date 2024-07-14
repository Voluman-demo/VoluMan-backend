package com.example.demo.Schedule;

import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.action.ActionRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ScheduleControllerTest {

    @Mock
    private ScheduleService scheduleService;
/*
    @Mock
    private ScheduleRepository scheduleRepository;*/

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
    void testChoosePref_ActionNotFound() {//Klaudiusz jeszcze sie bawi tym
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(false);

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testChoosePref_VolunteerNotFound() {//Klaudiusz jeszcze sie bawi tym
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsById(request.getVolunteerId())).thenReturn(false);

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testChoosePref_Success() {//Klaudiusz jeszcze sie bawi tym
        Long actionId = 1L;
        ActionPrefRequest request = new ActionPrefRequest(1L, "T");

        when(actionRepository.existsById(actionId)).thenReturn(true);
        when(volunteerRepository.existsById(request.getVolunteerId())).thenReturn(true);

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
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
    }

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
        when(volunteerRepository.existsById(request.getVolunteerId())).thenReturn(true);
        doThrow(new RuntimeException("Error")).when(scheduleService).choosePref(anyLong(), any(ActionPrefRequest.class));

        ResponseEntity<?> response = scheduleController.choosePref(actionId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

    @Test
    void testChooseNeed_Exception() throws Exception {
        int year = 2024;
        int week = 27;
        Long actionId = 1L;
        ActionNeedRequest request = createActionNeedRequest();

        doThrow(new Exception("Error")).when(scheduleService).scheduleNeedAction(anyLong(), anyInt(), anyInt(), any(ActionNeedRequest.class));

        ResponseEntity<?> response = scheduleController.chooseNeed(year, week, actionId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

    @Test
    void testChooseAvail_Exception() throws Exception {
        int year = 2024;
        int week = 27;
        Long volunteerId = 1L;
        VolunteerAvailRequest request = createVolunteerAvailRequest();

        doThrow(new Exception("Error")).when(scheduleService).chooseAvailabilities(anyLong(), anyInt(), anyInt(), any(VolunteerAvailRequest.class));

        ResponseEntity<?> response = scheduleController.chooseAvail(year, week, volunteerId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

}
