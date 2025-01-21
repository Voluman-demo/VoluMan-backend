package com.example.demo.Schedule;

import com.example.demo.Action.Action;
import com.example.demo.Action.Demand.Demand;
import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Schedule.ScheduleDto.ModifyScheduleRequest;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Volunteer;

import java.time.LocalDate;
import java.util.List;


public interface Schedules {
    ID createSchedule(Action action, LocalDate startDate, LocalDate endDate);

    Schedule getScheduleById(ID scheduleId);

    Errors deleteSchedule(ID scheduleId);

    Errors generateSchedule(LocalDate date);

    Errors applyHeuristic(Action action, List<Volunteer> volunteers, List<Demand> demands);

    Errors modifySchedule(ID scheduleId, ModifyScheduleRequest modifications);

    Errors updateDemand(ID actionId, Demand demand);

    Errors adjustAssignments(ID scheduleId);

    Errors assignVolunteerToDuty(ID volunteerId, Duty duty);

    Errors removeVolunteerFromDuty(ID volunteerId, Duty duty);


    List<Schedule> getVolunteerSchedules(ID volunteerId);

    List<Schedule> getActionSchedules(ID actionId);

    Errors validateScheduleDates(LocalDate startDate, LocalDate endDate);
}