package com.example.demo.Schedule;

import com.example.demo.Interval.AvailabilityInterval;
import com.example.demo.Interval.DemandInterval;
import com.example.demo.Interval.DutyInterval;
import com.example.demo.Interval.DutyIntervalRepository;
import com.example.demo.Preferences.Preferences;
import com.example.demo.Schedule.Dto.ActionNeedRequest;
import com.example.demo.Schedule.Dto.VolunteerAvailRequest;
import com.example.demo.Volunteer.*;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Availability.AvailabilityService;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Duty.DutyRepository;
import com.example.demo.Volunteer.Duty.DutyService;
import com.example.demo.action.Action;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.ActionService;
import com.example.demo.action.demand.Demand;
import com.example.demo.action.demand.DemandRepository;
import com.example.demo.action.demand.DemandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ScheduleServiceTest {
    @Mock
    private DutyIntervalRepository dutyIntervalRepository;

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

    @Mock
    private DemandRepository demandRepository;

    @InjectMocks
    private ScheduleService scheduleService;


    @Mock
    private DutyRepository dutyRepository;


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
        action.setDemands(new ArrayList<>()); // Initialize demands list

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
        request.setLimitOfHours(10L);
        VolunteerAvailRequest.DayAvailabilityRequest dayRequest = new VolunteerAvailRequest.DayAvailabilityRequest();
        dayRequest.setDate(LocalDate.of(2024, 7, 5));
        VolunteerAvailRequest.AvailabilitySlotRequest slotRequest = new VolunteerAvailRequest.AvailabilitySlotRequest();
        slotRequest.setStartTime(LocalTime.of(9, 0));
        slotRequest.setEndTime(LocalTime.of(17, 0));
        dayRequest.setSlots(List.of(slotRequest));
        request.setDays(List.of(dayRequest));

        Volunteer volunteer = new Volunteer();
        volunteer.setVolunteerId(1L);
        volunteer.setAvailabilities(new ArrayList<>());

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(availabilityService.getByVolunteerIdAndDate(1L, LocalDate.of(2024, 7, 5))).thenReturn(new Availability());

        scheduleService.chooseAvailabilities(1L, 2024, 27, request);

        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
        verify(availabilityService, times(1)).addAvail(any(Availability.class));
    }

    @Test
    void testGenerateSchedule() {
        LocalDate date = LocalDate.now();

        // Mocking availabilities
        Volunteer volunteer = new Volunteer();
        volunteer.setVolunteerId(1L);
        volunteer.setLimitOfWeeklyHours(10);
        volunteer.setDuties(new HashSet<>());



        Availability availability = new Availability();
        availability.setAvailabilityId(1L);
        availability.setVolunteer(volunteer);
        availability.setDate(date);

        AvailabilityInterval interval = new AvailabilityInterval();
        interval.setIntervalId(1L);
        interval.setStartTime(LocalTime.of(9, 0));
        interval.setEndTime(LocalTime.of(11, 0));
        availability.setSlots(Set.of(interval));

        when(availabilityService.getAvailabilitiesForDay(date)).thenReturn(List.of(availability));

        // Mocking demands
        Action action = new Action();
        action.setActionId(1L);
        action.setStartDay(date.minusDays(3));
        action.setEndDay(date.plusDays(3));
//        action.setLeader(new LeaderD);

        Preferences preferences = new Preferences();
        preferences.setPreferenceId(1L);
        preferences.getT().add(action);

        volunteer.setPreferences(preferences);

        Demand demand = new Demand();
        demand.setAction(action);
        demand.setDate(date);

        DemandInterval demandInterval = new DemandInterval();
        demandInterval.setStartTime(LocalTime.of(9, 0));
        demandInterval.setEndTime(LocalTime.of(11, 0));
        demandInterval.setNeedMax(1L);
        demandInterval.setCurrentVolunteersNumber(0L);
        demandInterval.setDemand(demand);
        demand.setDemandIntervals(Set.of(demandInterval));

        when(demandService.getDemandsForDay(date)).thenReturn(List.of(demand));
        when(actionRepository.findById(any())).thenReturn(Optional.of(action));
        when(volunteerRepository.findAll()).thenReturn(List.of(volunteer));

        doNothing().when(dutyService).addDutyInterval(any(DutyInterval.class), any(Duty.class));
        doNothing().when(dutyService).updateDutyInterval(any(DutyInterval.class));

        // Call the method
        scheduleService.generateSchedule(date);

        // Verify the interactions
        verify(demandService, times(1)).getDemandsForDay(date);
        verify(availabilityService, times(1)).getAvailabilitiesForDay(date);
        // Verify that save was called on the dutyIntervalRepository with any instance of DutyInterval
        //  verify(dutyIntervalRepository, times(1)).save(new DutyInterval());

        verify(dutyService, times(1)).addDutyInterval(any(DutyInterval.class), any(Duty.class));


            // Assert that exactly one of these methods was called, not both
        verify(dutyService, atMost(1)).addDutyInterval(any(DutyInterval.class), any(Duty.class));
        verify(dutyService, atMost(1)).updateDutyInterval(any(DutyInterval.class));

        // Additional verifications for other methods if needed
        verify(volunteerRepository, times(2)).save(volunteer);
    }

    }

