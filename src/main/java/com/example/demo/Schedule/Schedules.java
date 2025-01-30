package com.example.demo.Schedule;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionDemand.ActionDemand;
import com.example.demo.Action.ActionDemand.UpdateNeedDto;
import com.example.demo.Model.Errors;
import com.example.demo.Schedule.ScheduleDto.ModifyScheduleRequest;
import com.example.demo.Volunteer.Volunteer;

import java.time.LocalDate;
import java.util.List;


public interface Schedules {
    Long createSchedule(Action action, LocalDate startDate, LocalDate endDate);

    Schedule getScheduleById(Long scheduleId);

    Errors deleteSchedule(Long scheduleId);

    Errors generateSchedule(LocalDate date);

    Errors applyHeuristic(Action action, List<Volunteer> volunteers, List<ActionDemand> actionDemands);

    Errors modifySchedule(Long scheduleId, ModifyScheduleRequest modifications);

    Errors updateDemand(Long actionId, UpdateNeedDto updateNeedDto);

    Errors adjustAssignments(Long scheduleId);

    Errors assignVolunteerToDemand(Long volunteerId, ActionDemand actionDemand);

    Errors removeVolunteerFromDemand(Long volunteerId, ActionDemand actionDemand);


    List<Schedule> getVolunteerSchedules(Long volunteerId);

    List<Schedule> getActionSchedules(Long actionId);

    Errors validateScheduleDates(LocalDate startDate, LocalDate endDate);
}