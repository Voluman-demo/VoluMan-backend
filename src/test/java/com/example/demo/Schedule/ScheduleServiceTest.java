package com.example.demo.Schedule;

import com.example.demo.Schedule.ActionNeedRequest;
import com.example.demo.Schedule.ScheduleService;
import com.example.demo.Schedule.VolunteerAvailRequest;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Availability.AvailabilityService;
import com.example.demo.Volunteer.Duty.DutyService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import com.example.demo.Volunteer.VolunteerService;
import com.example.demo.action.Action;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.ActionService;
import com.example.demo.action.demand.Demand;
import com.example.demo.action.demand.DemandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private ActionService actionService;

    @Mock
    private ActionRepository actionRepository;

    @Mock
    private VolunteerService volunteerService;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private DemandService demandService;

    @Mock
    private DutyService dutyService;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduleNeedAction_LeaderValidationFailure() {
        ActionNeedRequest request = new ActionNeedRequest();
        request.setLeaderId(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(false);

        assertThrows(Exception.class, () -> scheduleService.scheduleNeedAction(1L, 2024, 27, request));
    }

    @Test
    void testScheduleNeedAction_ActionNotFound() {
        ActionNeedRequest request = new ActionNeedRequest();
        request.setLeaderId(1L);

        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(true);
        when(actionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> scheduleService.scheduleNeedAction(1L, 2024, 27, request));
    }

    @Test
    void testScheduleNeedAction_DateValidationFailure() {
        ActionNeedRequest request = new ActionNeedRequest();
        request.setLeaderId(1L);
        ActionNeedRequest.DayRequest dayRequest = new ActionNeedRequest.DayRequest();
        dayRequest.setDate(LocalDate.of(2024, 7, 4));
        request.setDays(List.of(dayRequest));

        Action action = new Action();
        action.setStartDay(LocalDate.of(2024, 7, 5));
        action.setEndDay(LocalDate.of(2024, 7, 10));

        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(true);
        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));

        assertThrows(Exception.class, () -> scheduleService.scheduleNeedAction(1L, 2024, 27, request));
    }

    @Test
    void testScheduleNeedAction_Success() throws Exception {
        ActionNeedRequest request = new ActionNeedRequest();
        request.setLeaderId(1L);
        ActionNeedRequest.DayRequest dayRequest = new ActionNeedRequest.DayRequest();
        dayRequest.setDate(LocalDate.of(2024, 7, 4));
        ActionNeedRequest.SlotRequest slotRequest = new ActionNeedRequest.SlotRequest();
        slotRequest.setStartTime(LocalTime.of(9, 0));
        slotRequest.setEndTime(LocalTime.of(9, 30));
        slotRequest.setNeedMin(1L);
        slotRequest.setNeedMax(5L);
        dayRequest.setSlots(List.of(slotRequest));
        request.setDays(List.of(dayRequest));

        Action action = new Action();
        action.setStartDay(LocalDate.of(2024, 7, 1));
        action.setEndDay(LocalDate.of(2024, 7, 31));

        when(volunteerRepository.existsByVolunteerIdAndRole(1L, VolunteerRole.LEADER)).thenReturn(true);
        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));

        scheduleService.scheduleNeedAction(1L, 2024, 27, request);

        verify(actionRepository, times(1)).save(any(Action.class));
        verify(demandService, times(1)).addDemand(any(Demand.class));
    }

    @Test
    void testChooseAvailabilities_VolunteerNotFound() {
        VolunteerAvailRequest request = new VolunteerAvailRequest();

        when(volunteerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> scheduleService.chooseAvailabilities(1L, 2024, 27, request));
    }

    @Test
    void testChooseAvailabilities_DateValidationFailure() {
        VolunteerAvailRequest request = new VolunteerAvailRequest();
        VolunteerAvailRequest.DayAvailabilityRequest dayRequest = new VolunteerAvailRequest.DayAvailabilityRequest();
        dayRequest.setDate(LocalDate.of(2024, 8, 4)); // Date not matching the specified week
        request.setDays(List.of(dayRequest));

        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

        assertThrows(Exception.class, () -> scheduleService.chooseAvailabilities(1L, 2024, 27, request));
    }

    @Test
    void testChooseAvailabilities_Success() throws Exception {
        VolunteerAvailRequest request = new VolunteerAvailRequest();
        request.setLimitOfHours(20L);
        VolunteerAvailRequest.DayAvailabilityRequest dayRequest = new VolunteerAvailRequest.DayAvailabilityRequest();
        dayRequest.setDate(LocalDate.of(2024, 7, 3));
        VolunteerAvailRequest.AvailabilitySlotRequest slotRequest = new VolunteerAvailRequest.AvailabilitySlotRequest();
        slotRequest.setStartTime(LocalTime.of(9, 0));
        slotRequest.setEndTime(LocalTime.of(9, 30));
        dayRequest.setSlots(List.of(slotRequest));
        request.setDays(List.of(dayRequest));

        Volunteer volunteer = new Volunteer();

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(availabilityService.getByVolunteerIdAndDate(1L, LocalDate.of(2024, 7, 3))).thenReturn(null);

        scheduleService.chooseAvailabilities(1L, 2024, 27, request);

        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
        verify(availabilityService, times(1)).addAvail(any(Availability.class));
    }
}
