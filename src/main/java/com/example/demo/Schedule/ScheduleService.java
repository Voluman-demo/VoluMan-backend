package com.example.demo.Schedule;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionDemand.*;
import com.example.demo.Action.ActionDemand.Dto.ActionDemandRequest;
import com.example.demo.Action.ActionDemand.Dto.ActionNeedRequest;
import com.example.demo.Action.ActionRepository;
import com.example.demo.Action.ActionDemand.ActionDemandInterval.ActionDemandInterval;
import com.example.demo.Action.ActionDemand.ActionDemandInterval.ActionDemandIntervalRepository;
import com.example.demo.Model.Errors;

import com.example.demo.Schedule.ScheduleDto.ModificationType;
import com.example.demo.Schedule.ScheduleDto.ModifyScheduleRequest;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Availability.AvailabilityInterval.AvailabilityInterval;
import com.example.demo.Volunteer.Availability.AvailabilityService;
import com.example.demo.Volunteer.Preferences.Preferences;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ScheduleService implements Schedules {
    private final ActionDemandService actionDemandService;
    private final AvailabilityService availabilityService;
    private final VolunteerRepository volunteerRepository;
    private final ActionRepository actionRepository;
    private final ScheduleRepository scheduleRepository;
    private final VolunteerService volunteerService;
    private final ActionDemandIntervalRepository actionDemandIntervalRepository;
    private final ActionDemandRepository actionDemandRepository;

    public ScheduleService(ActionDemandService actionDemandService,
                           AvailabilityService availabilityService,
                           VolunteerRepository volunteerRepository,
                           ActionRepository actionRepository,
//                           DutyService dutyService,
                           ScheduleRepository scheduleRepository, VolunteerService volunteerService, ActionDemandIntervalRepository actionDemandIntervalRepository, ActionDemandRepository actionDemandRepository) {
        this.actionDemandService = actionDemandService;
        this.availabilityService = availabilityService;
        this.volunteerRepository = volunteerRepository;
        this.actionRepository = actionRepository;
//        this.dutyService = dutyService;
        this.scheduleRepository = scheduleRepository;
        this.volunteerService = volunteerService;
        this.actionDemandIntervalRepository = actionDemandIntervalRepository;
        this.actionDemandRepository = actionDemandRepository;
    }


    @Override
    public Long createSchedule(Action action, LocalDate startDate, LocalDate endDate) {
        Errors validationResult = validateScheduleDates(startDate, endDate);
        if (validationResult != Errors.SUCCESS) {
            return null;
        }

        Schedule schedule = new Schedule(startDate, endDate);
        if (action != null) {
            schedule.setActions(List.of(action));
        } else {
            schedule.setActions(new ArrayList<>());
        }
        scheduleRepository.save(schedule);

        return schedule.getScheduleId();
    }

    @Override
    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }

    @Override
    public Errors deleteSchedule(Long scheduleId) {
        if (scheduleRepository.existsById(scheduleId)) {
            scheduleRepository.deleteById(scheduleId);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors generateSchedule(LocalDate weekStart) {
        LocalDate startOfWeek = weekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Long newSchedule = createSchedule(null, startOfWeek, endOfWeek);
        Schedule schedule = getScheduleById(newSchedule);

        if(schedule == null) {
            return Errors.FAILURE;
        }
        List<Action> allActions = actionRepository.findAll();
        for (Action action : allActions) {
            if(!action.getBegin().isAfter(schedule.getStartDate()) && !action.getEnd().isBefore(schedule.getStartDate())){
                schedule.getActions().add(action);
            }
        }

        for (LocalDate currentDay = startOfWeek; !currentDay.isAfter(endOfWeek); currentDay = currentDay.plusDays(1)) {

            List<Availability> availabilities = availabilityService.getAvailabilitiesForDay(currentDay);
            List<ActionDemand> actionDemands = actionDemandService.getDemandsForDay(currentDay);

            if (actionDemands.isEmpty() || availabilities.isEmpty()) {
                continue;
            }

            for (ActionDemand actionDemand : actionDemands) {
                Set<Volunteer> interestedVolunteers = getInterestedVolunteersForAction(actionDemand.getAction().getActionId());

                List<Availability> filteredAvailabilities = availabilities.stream()
                        .filter(availability -> interestedVolunteers.contains(availability.getVolunteer()))
                        .toList();

                for (ActionDemandInterval actionDemandInterval : actionDemand.getActionDemandIntervals()) {
                    List<Availability> matchingAvailabilities = filteredAvailabilities.stream()
                            .filter(availability -> isAvailabilityMatchingInterval(availability, actionDemandInterval))
                            .toList();

                    for (Availability matchingAvailability : matchingAvailabilities) {
                        Volunteer volunteer = matchingAvailability.getVolunteer();

                        if (isWithinWeeklyLimit(volunteer, currentDay)) {
                            actionDemandInterval.getAssignedVolunteers().add(volunteer);

                            volunteer.calculateActualWeeklyHours(startOfWeek, endOfWeek);


                            // Dodanie wolontariusza do harmonogramu
                            if (!schedule.getVolunteers().contains(volunteer)) {
                                schedule.getVolunteers().add(volunteer);
                            }

                            if(!actionDemand.getAction().getVolunteers().contains(volunteer)) {
                                actionDemand.getAction().getVolunteers().add(volunteer);
                                actionRepository.save(actionDemand.getAction());
                            }
                        }
                    }
                }
            }
            scheduleRepository.save(schedule);
        }

        if (schedule != null) {
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors applyHeuristic(Action action, List<Volunteer> volunteers, List<ActionDemand> actionDemands) {
        // Implementacja heurystyki dla przypisywania wolontariuszy
        return Errors.SUCCESS;
    }

    @Override
    public Errors modifySchedule(Long scheduleId, ModifyScheduleRequest modifications) {
        // Znajdź harmonogram na podstawie Long
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            return Errors.NOT_FOUND;
        }

        Schedule schedule = scheduleOpt.get();

        // Znajdź wolontariusza na podstawie Long
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(modifications.volunteerId());
        if (volunteerOpt.isEmpty()) {
            return Errors.NOT_FOUND;
        }

        Volunteer volunteer = volunteerOpt.get();

        // Iteruj po liście Long interwałów zapotrzebowania
        for (Long demandIntervalId : modifications.demandIntervalIds()) {
            Optional<ActionDemandInterval> intervalOpt = actionDemandIntervalRepository.findById(demandIntervalId);
            if (intervalOpt.isEmpty()) {
                continue; // Jeśli interwał nie istnieje, pomiń go
            }

            ActionDemandInterval actionDemandInterval = intervalOpt.get();

            // Modyfikacja interwału w zależności od typu
            if (modifications.modificationType() == ModificationType.ADD) {
                // Dodaj wolontariusza do interwału, jeśli go tam nie ma
                if (!actionDemandInterval.getAssignedVolunteers().contains(volunteer)) {
                    actionDemandInterval.getAssignedVolunteers().add(volunteer);

                    // Dodaj wolontariusza do harmonogramu, jeśli jeszcze nie jest w harmonogramie
                    if (!schedule.getVolunteers().contains(volunteer)) {
                        schedule.getVolunteers().add(volunteer);
                    }
                }
            } else if (modifications.modificationType() == ModificationType.REMOVE) {
                // Usuń wolontariusza z interwału, jeśli tam jest
                if (actionDemandInterval.getAssignedVolunteers().contains(volunteer)) {
                    actionDemandInterval.getAssignedVolunteers().remove(volunteer);

                    // Usuń wolontariusza z harmonogramu, jeśli nie jest przypisany do innych interwałów
                    boolean isAssignedElsewhere = schedule.getActionDemands().stream()
                            .flatMap(demand -> demand.getActionDemandIntervals().stream())
                            .anyMatch(interval -> interval.getAssignedVolunteers().contains(volunteer));

                    if (!isAssignedElsewhere) {
                        schedule.getVolunteers().remove(volunteer);
                    }
                }
            }
        }

        // Zapisz zmodyfikowany harmonogram i interwały
        scheduleRepository.save(schedule);

        return Errors.SUCCESS;
    }


    @Override
    public Errors updateDemand(Long actionId, ActionNeedRequest actionNeedRequest) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isEmpty()) {
            return Errors.NOT_FOUND;
        }

        Action action = actionOpt.get();
        ActionDemandRequest actionDemandRequest = actionNeedRequest.getActionDemand();

        // Znajdź istniejące zapotrzebowanie powiązane z akcją
        Optional<ActionDemand> existingDemandOpt = actionDemandRepository.findByActionAndDate(action, actionDemandRequest.getDate());
        ActionDemand actionDemand;

        if (existingDemandOpt.isPresent()) {
            // Aktualizacja istniejącego zapotrzebowania
            actionDemand = existingDemandOpt.get();
        } else {
            // Tworzenie nowego zapotrzebowania
            actionDemand = new ActionDemand();
            actionDemand.setDate(actionDemandRequest.getDate());
            actionDemand.setAction(action);
            actionDemand.setActionDemandIntervals(new HashSet<>());
        }

        // Aktualizacja lub dodanie interwałów czasowych
        for (ActionDemandIntervalRequest intervalRequest : actionNeedRequest.getActionDemandIntervals()) {
            LocalTime startTime = LocalTime.parse(intervalRequest.getStartTime());
            LocalTime endTime = LocalTime.parse(intervalRequest.getEndTime());

            Optional<ActionDemandInterval> matchingIntervalOpt = actionDemand.getActionDemandIntervals().stream()
                    .filter(interval -> interval.getStartTime().equals(startTime) && interval.getEndTime().equals(endTime))
                    .findFirst();

            if (matchingIntervalOpt.isPresent()) {
                // Aktualizacja istniejącego interwału
                ActionDemandInterval matchingInterval = matchingIntervalOpt.get();
                matchingInterval.setNeedMin(intervalRequest.getNeedMin());
                matchingInterval.setNeedMax(intervalRequest.getNeedMax());
            } else {
                // Dodanie nowego interwału
                ActionDemandInterval newInterval = new ActionDemandInterval();
                newInterval.setStartTime(startTime);
                newInterval.setEndTime(endTime);
                newInterval.setNeedMin(intervalRequest.getNeedMin());
                newInterval.setNeedMax(intervalRequest.getNeedMax());
                newInterval.setActionDemand(actionDemand);
                actionDemand.getActionDemandIntervals().add(newInterval);
            }
        }
        // Zapisz zapotrzebowanie
        actionDemandRepository.save(actionDemand);
        action.getActionDemands().add(actionDemand);
        actionRepository.save(action);
        return existingDemandOpt.isPresent() ? Errors.SUCCESS : Errors.CREATED;
    }

    @Override
    public Errors adjustAssignments(Long scheduleId) {
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (scheduleOpt.isPresent()) {
            Schedule schedule = scheduleOpt.get();
            // Zaktualizuj przypisania wolontariuszy
            scheduleRepository.save(schedule);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors assignVolunteerToDemand(Long volunteerId, ActionDemand actionDemand) {
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        if (volunteerOpt.isEmpty()) {
            return Errors.NOT_FOUND; // Jeśli wolontariusz nie istnieje
        }

        Volunteer volunteer = volunteerOpt.get();

        // Przechodzimy przez wszystkie interwały zapotrzebowania w ramach zapotrzebowania (Demand)
        for (ActionDemandInterval actionDemandInterval : actionDemand.getActionDemandIntervals()) {
            if (!actionDemandInterval.getAssignedVolunteers().contains(volunteer)) {
                actionDemandInterval.getAssignedVolunteers().add(volunteer);


                // Zapisz zmiany w bazie danych
                actionDemandIntervalRepository.save(actionDemandInterval);
            }
        }

        return Errors.SUCCESS;
    }

    @Override
    public Errors removeVolunteerFromDemand(Long volunteerId, ActionDemand actionDemand) {
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        if (volunteerOpt.isEmpty()) {
            return Errors.NOT_FOUND; // Jeśli wolontariusz nie istnieje
        }

        Volunteer volunteer = volunteerOpt.get();

        boolean removed = false;
        for (ActionDemandInterval actionDemandInterval : actionDemand.getActionDemandIntervals()) {
            if (actionDemandInterval.getAssignedVolunteers().contains(volunteer)) {
                actionDemandInterval.getAssignedVolunteers().remove(volunteer);

                // Zapisz zmiany w bazie danych
                actionDemandIntervalRepository.save(actionDemandInterval);
                removed = true;
            }
        }

        return removed ? Errors.SUCCESS : Errors.NOT_FOUND;
    }

    @Override
    public List<Schedule> getVolunteerSchedules(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new EntityNotFoundException("Volunteer not found"));

        return scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getActionDemands().stream()
                        .flatMap(demand -> demand.getActionDemandIntervals().stream())
                        .anyMatch(interval -> interval.getAssignedVolunteers().contains(volunteer)))
                .collect(Collectors.toList());
    }

    public List<Schedule> getActionSchedules(Long actionId) {
        return scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getActions().stream()
                        .anyMatch(action -> action.getActionId().equals(actionId)))
                .toList();
    }


    @Override
    public Errors validateScheduleDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return Errors.INVALID;
        }
        return Errors.SUCCESS;
    }

    private Set<Volunteer> getInterestedVolunteersForAction(Long actionId) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);

        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            return volunteerRepository.findAll().stream()
                    .filter(volunteer -> {
                        Preferences preferences = volunteer.getPreferences();
                        return preferences != null &&
                                (preferences.getS().contains(action) || preferences.getW().contains(action));
                    })
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    private boolean isAvailabilityMatchingInterval(Availability availability, ActionDemandInterval actionDemandInterval) {
        return availability.getSlots().stream()
                .anyMatch(slot -> isSlotMatchingInterval(slot, actionDemandInterval));
    }

    private boolean isSlotMatchingInterval(AvailabilityInterval slot, ActionDemandInterval actionDemandInterval) {
        return !slot.getStartTime().isAfter(actionDemandInterval.getStartTime()) &&
                !slot.getEndTime().isBefore(actionDemandInterval.getEndTime());
    }

    private boolean isWithinWeeklyLimit(Volunteer volunteer, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        double currentWeeklyLoad = volunteer.calculateActualWeeklyHours(startOfWeek, endOfWeek);
        return currentWeeklyLoad + 1 <= volunteer.getLimitOfWeeklyHours();
    }
}