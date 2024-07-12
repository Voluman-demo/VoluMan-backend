package com.example.demo.Schedule;

import com.example.demo.Interval.AvailabilityInterval;
import com.example.demo.Interval.DemandInterval;
import com.example.demo.Volunteer.*;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Availability.AvailabilityRepository;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.action.Action;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.ActionService;
import com.example.demo.action.demand.Demand;
import com.example.demo.action.demand.DemandRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ActionService actionService;
    private final ActionRepository actionRepository;
    private final VolunteerService volunteerService;
    private final VolunteerRepository volunteerRepository;
    private final DemandRepository demandRepository;
    private final AvailabilityRepository availabilityRepository;

    public ScheduleService(ActionService actionService, ActionRepository actionRepository, VolunteerService volunteerService, VolunteerRepository volunteerRepository, DemandRepository demandRepository, AvailabilityRepository availabilityRepository) {
        this.actionService = actionService;
        this.actionRepository = actionRepository;
        this.volunteerService = volunteerService;
        this.volunteerRepository = volunteerRepository;
        this.demandRepository = demandRepository;
        this.availabilityRepository = availabilityRepository;
    }


    public void choosePref(Long actionId, ActionDecisionRequest actionDecisionRequest) {
        switch (actionDecisionRequest.decision){
            case T :
                actionService.addDetermined(actionId, actionDecisionRequest.volunteerId);
                actionService.addVolunteer(actionId, actionDecisionRequest.volunteerId);
                break;
            case R :
                actionService.addVolunteer(actionId, actionDecisionRequest.volunteerId);
                break;
            default:
                break;
        }
        volunteerService.addPreferences(actionId, actionDecisionRequest.volunteerId, actionDecisionRequest.decision);
    }

    // Method to handle weekly scheduling
    public void scheduleWeeklyAction(Long actionId, int year, int week, ActionNeedRequest actionNeedRequest) throws Exception {
        // Validate leader
        if (!volunteerRepository.existsByVolunteerIdAndRole(actionNeedRequest.getLeaderId(), VolunteerRole.LEADER)) {
            throw new Exception("Leader not found or not authorized.");
        }

        // Get action
        Optional<Action> actionOptional = actionRepository.findById(actionId);
        if (actionOptional.isEmpty()) {
            throw new Exception("Action not found.");
        }

        Action action = actionOptional.get();

        // Get start and end date of the week
        LocalDate startOfWeek = getStartOfWeek(year, week);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Validate and schedule demands
        for (ActionNeedRequest.DayRequest dayRequest : actionNeedRequest.getDays()) {
            LocalDate requestDate = dayRequest.getDate();

            // Check if date is within the action's startDay and endDay
            if (requestDate.isBefore(action.getStartDay()) || requestDate.isAfter(action.getEndDay())) {
                throw new Exception("Date " + requestDate + " is not within the action's duration.");
            }

            // Check if date is within the specified week
            if (requestDate.isBefore(startOfWeek) || requestDate.isAfter(endOfWeek)) {
                throw new Exception("Date " + requestDate + " is not within the specified week.");
            }

            Demand demand = new Demand();
            demand.setAction(action);
            demand.setDate(requestDate);

            Set<DemandInterval> demandIntervals = new HashSet<>();
            for (ActionNeedRequest.SlotRequest slotRequest : dayRequest.getSlots()) {
                DemandInterval demandInterval = new DemandInterval();
                demandInterval.setStartTime(LocalTime.of(slotRequest.getStartTime().getHour(), slotRequest.getStartTime().getMinute()));
                demandInterval.setEndTime(LocalTime.of(slotRequest.getEndTime().getHour(), slotRequest.getEndTime().getMinute()));
                demandInterval.setNeedMin(slotRequest.getNeedMin());
                demandInterval.setNeedMax(slotRequest.getNeedMax());
                demandInterval.setDemand(demand);
                demandIntervals.add(demandInterval);
            }
            demand.setDemandIntervals(demandIntervals);
            demandRepository.save(demand);

            // Add demand to the action's demand list
            action.getDemands().add(demand);
        }
        // Save the updated action with new demands
        actionRepository.save(action);

    }

    public Optional<Action> getActionById(Long actionId) {
        return actionRepository.findById(actionId);
    }

    // Helper method to get the start of the week
    private LocalDate getStartOfWeek(int year, int week) {
        return LocalDate.ofYearDay(year, 1)
                .with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek())
                .plusWeeks(week - 1);
    }

    public void chooseAvailabilities(Long volunteerId, int year, int week, VolunteerAvailRequest availRequest) throws Exception {
        // Validate volunteer
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new Exception("Volunteer not found"));

        // Validate and process availability for each day
        for (VolunteerAvailRequest.DayAvailabilityRequest dayRequest : availRequest.getDays()) {
            LocalDate requestDate = dayRequest.getDate();

            // Validate if the date belongs to the specified week
            int requestWeek = requestDate.get(WeekFields.ISO.weekOfWeekBasedYear());
            if (requestDate.getYear() != year || requestWeek != week) {
                throw new Exception("Date " + requestDate + " does not belong to week " + week + " of year " + year);
            }

            // Create or update availability for the volunteer for this date
            Availability availability = availabilityRepository.findByVolunteer_VolunteerIdAndDate(volunteerId, requestDate)
                    .orElse(new Availability());

            availability.setVolunteer(volunteer);
            availability.setDate(requestDate);

            // Process slots for the day
            Set<AvailabilityInterval> availabilityIntervals = new HashSet<>();
            for (VolunteerAvailRequest.AvailabilitySlotRequest slotRequest : dayRequest.getSlots()) {
                AvailabilityInterval interval = new AvailabilityInterval();
                interval.setStartTime(slotRequest.getStartTime());
                interval.setEndTime(slotRequest.getEndTime());
                interval.setAvailability(availability);
                availabilityIntervals.add(interval);
            }

            availability.setSlots(availabilityIntervals);
            availabilityRepository.save(availability);
            volunteer.getAvailabilities().add(availability);
        }

        // Update volunteer's limit of hours
        volunteer.setLimitOfHours(availRequest.getLimitOfHours());
        volunteerRepository.save(volunteer);
    }

//    public Schedule showSchedule(int year, int week, int actionId) {
//        //
//
//        return ;
//    }

    public void generateSchedule(LocalDate date){
        List<Availability> availabilities = availabilityService.getAvailabilitiesForDay(date);

        List<Demand> demands = demandService.getDemandsForDay(date);


        for (Demand demand : demands) {
            // Pobierz listę zainteresowanych wolontariuszy
            Set<Volunteer> interestedVolunteers = demand.getAction().getVolunteers();

            // Przefiltruj dostępności, aby zachować tylko te, które interesują zainteresowanych wolontariuszy
            List<Availability> filteredAvailabilities = availabilities.stream()
                    .filter(availability -> interestedVolunteers.contains(availability.getVolunteer()))
                    .collect(Collectors.toList());

            // Dla każdego interwału zapotrzebowania
            for (DemandInterval demandInterval : demand.getDemandIntervals()) {
                // Znajdź dostępność zgodną z interwałem zapotrzebowania
                Optional<Availability> matchingAvailability = filteredAvailabilities.stream()
                        .filter(availability -> isAvailabilityMatchingInterval(availability, demandInterval))
                        .findFirst();

                if (matchingAvailability.isPresent()) {
                    // Dopóki aktualna liczba przypisanych wolontariuszy jest mniejsza od minimalnego zapotrzebowania
                    while (demandInterval.getCurrentVolunteers() < demandInterval.getNeedMin()) {
                        // Wybierz pierwszego wolontariusza z listy
                        Volunteer volunteer = matchingAvailability.get().getVolunteer();

                        // Sprawdź aktualne i maksymalne tygodniowe obciążenie wolontariusza
                        if (isWithinWeeklyLimit(volunteer)) {
                            // Utwórz nowy interwał dyżuru dla danego wolontariusza
                            createDutyInterval(volunteer, demandInterval);

                            // Aktualizuj obciążenie wolontariusza
                            updateWeeklyLoad(volunteer);

                            // Zaktualizuj liczbę wolontariuszy w interwale zapotrzebowania
                            demandInterval.setCurrentVolunteers(demandInterval.getCurrentVolunteers() + 1);
                        }
                    }
                }
            }
        }

        // Zapisz aktualne obciążenie wolontariuszy i przelicz plany dyżurów
        saveVolunteersWeeklyLoad();
        recalculateDutyPlansForDay(date);

    }

    private boolean isAvailabilityMatchingInterval(Availability availability, DemandInterval demandInterval) {
        // Sprawdź czy interwał dostępności pokrywa się z interwałem zapotrzebowania
        return availability.getSlots().stream()
                .anyMatch(slot -> isSlotMatchingInterval(slot, demandInterval));
    }

    private boolean isSlotMatchingInterval(AvailabilityInterval slot, DemandInterval demandInterval) {
        // Sprawdź czy slot dostępności pokrywa się z interwałem zapotrzebowania
        return slot.getStartTime().isBefore(demandInterval.getEndTime())
                && slot.getEndTime().isAfter(demandInterval.getStartTime());
    }

    private boolean isWithinWeeklyLimit(Volunteer volunteer) {
        // Pobierz aktualne obciążenie wolontariusza na ten tydzień
        long currentWeeklyLoad = volunteer.getDuties().stream()
                .filter(duty -> isWithinCurrentWeek(duty.getDate()))
                .mapToLong(Duty::getDutyIntervals)
                .sum();

        // Pobierz maksymalny limit godzinowy wolontariusza na tydzień
        long maxWeeklyLimit = volunteer.getLimitOfHours();

        // Sprawdź czy aktualne obciążenie jest mniejsze lub równe maksymalnemu limitowi
        return currentWeeklyLoad <= maxWeeklyLimit;
    }

    private boolean isWithinCurrentWeek(LocalDate dutyDate) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return !dutyDate.isBefore(startOfWeek) && !dutyDate.isAfter(endOfWeek);
    }

    private void createDutyInterval(Volunteer volunteer, DemandInterval demandInterval) {
        Duty duty = new Duty();
        duty.setVolunteer(volunteer);
        duty.setAction(demandInterval.getDemand().getAction());
        duty.setDate(demandInterval.getDemand().getDate());
        duty.setStartTime(demandInterval.getStartTime());
        duty.setEndTime(demandInterval.getEndTime());
        duty.setDurationHours(Duration.between(demandInterval.getStartTime(), demandInterval.getEndTime()).toHours());

        volunteer.getDuties().add(duty);
        dutyRepository.save(duty);
    }

    private void updateWeeklyLoad(Volunteer volunteer) {
        // Pobierz aktualne obciążenie wolontariusza na ten tydzień
        long currentWeeklyLoad = volunteer.getDuties().stream()
                .filter(duty -> isWithinCurrentWeek(duty.getDate()))
                .mapToLong(Duty::getDurationHours)
                .sum();

        // Zaktualizuj obciążenie wolontariusza
        volunteer.setCurrentWeeklyLoad(currentWeeklyLoad);
        volunteerRepository.save(volunteer);
    }

    private void saveVolunteersWeeklyLoad() {
        List<Volunteer> volunteers = volunteerRepository.findAll();
        for (Volunteer volunteer : volunteers) {
            long currentWeeklyLoad = volunteer.getDuties().stream()
                    .filter(duty -> isWithinCurrentWeek(duty.getDate()))
                    .mapToLong(Duty::getDurationHours)
                    .sum();
            volunteer.setCurrentWeeklyLoad(currentWeeklyLoad);
            volunteerRepository.save(volunteer);
        }
    }

    private void recalculateDutyPlansForDay(LocalDate date) {
        List<Duty> dutiesForDay = dutyRepository.findByDate(date);

        // Przelicz plany dyżurów dla danego dnia (np. generowanie raportów, statystyk, etc.)
        // Implementacja zależna od specyfiki Twojej aplikacji
    }

}
