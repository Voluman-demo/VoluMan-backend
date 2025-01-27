package com.example.demo.Schedule;

import com.example.demo.Action.Action;
import com.example.demo.Action.Demand.Demand;
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

    Errors applyHeuristic(Action action, List<Volunteer> volunteers, List<Demand> demands);

    Errors modifySchedule(Long scheduleId, ModifyScheduleRequest modifications);

    Errors updateDemand(Long actionId, Demand demand);

    Errors adjustAssignments(Long scheduleId);

    Errors assignVolunteerToDemand(Long volunteerId, Demand demand);

    Errors removeVolunteerFromDemand(Long volunteerId, Demand demand);


    List<Schedule> getVolunteerSchedules(Long volunteerId);

    List<Schedule> getActionSchedules(Long actionId);

    Errors validateScheduleDates(LocalDate startDate, LocalDate endDate);
}