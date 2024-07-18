package com.example.demo.Schedule;

import com.example.demo.Interval.*;
import com.example.demo.Preferences.Preferences;
import com.example.demo.Schedule.Dto.ActionNeedRequest;
import com.example.demo.Schedule.Dto.ActionPrefRequest;
import com.example.demo.Schedule.Dto.ModifyScheduleRequest;
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
import com.example.demo.action.Dto.ActionScheduleDto;
import com.example.demo.action.demand.Demand;
import com.example.demo.action.demand.DemandDto;
import com.example.demo.action.demand.DemandRepository;
import com.example.demo.action.demand.DemandService;
import jakarta.transaction.Transactional;
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
    private final AvailabilityService availabilityService;
    private final DemandService demandService;
    private final DutyService dutyService;
    private final DutyRepository dutyRepository;
    private final DemandRepository demandRepository;

    public ScheduleService(ActionService actionService, ActionRepository actionRepository, VolunteerService volunteerService, VolunteerRepository volunteerRepository, AvailabilityService availabilityService, DemandService demandService, DutyService dutyService, DutyRepository dutyRepository, DutyRepository dutyRepository1, DemandRepository demandRepository) {
        this.actionService = actionService;
        this.actionRepository = actionRepository;
        this.volunteerService = volunteerService;
        this.volunteerRepository = volunteerRepository;
        this.availabilityService = availabilityService;
        this.demandService = demandService;
        this.dutyService = dutyService;
        this.dutyRepository = dutyRepository1;
        this.demandRepository = demandRepository;
    }


    public void choosePref(Long actionId, ActionPrefRequest actionPrefRequest) {
        switch (actionPrefRequest.decision()) {
            case "T":
                actionService.addDetermined(actionId, actionPrefRequest.volunteerId());
                actionService.addVolunteer(actionId, actionPrefRequest.volunteerId());
                volunteerService.addPreferences(actionId, actionPrefRequest.volunteerId(), Decision.T);
                break;
            case "R":
                actionService.addVolunteer(actionId, actionPrefRequest.volunteerId());
                volunteerService.addPreferences(actionId, actionPrefRequest.volunteerId(), Decision.R);
                break;
            case "N":
                volunteerService.addPreferences(actionId, actionPrefRequest.volunteerId(), Decision.N);
                break;
            default:
                throw new IllegalArgumentException("Invalid decision value: " + actionPrefRequest.decision());
        }

    }

    // Method to handle weekly scheduling
    public void scheduleNeedAction(Long actionId, int year, int week, ActionNeedRequest actionNeedRequest) throws Exception {
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
            demandService.addDemand(demand);


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
            Availability availability = availabilityService.getByVolunteerIdAndDate(volunteerId, requestDate);

            if (availability.getAvailabilityId() == null) {
//                availability = new Availability();
                availability.setVolunteer(volunteer);
                availability.setDate(requestDate);
                volunteer.getAvailabilities().add(availability);
            }

            // Process slots for the day
            Set<AvailabilityInterval> availabilityIntervals = availability.getSlots();
            if (availabilityIntervals == null) {
                availabilityIntervals = new HashSet<>();
                availability.setSlots(availabilityIntervals);
            } else {
                availabilityIntervals.clear(); // Clear existing intervals if necessary
            }

            for (VolunteerAvailRequest.AvailabilitySlotRequest slotRequest : dayRequest.getSlots()) {
                AvailabilityInterval interval = new AvailabilityInterval();
                interval.setStartTime(slotRequest.getStartTime());
                interval.setEndTime(slotRequest.getEndTime());
                interval.setAvailability(availability);
                availabilityIntervals.add(interval);
            }

            availability.setSlots(availabilityIntervals);
            availabilityService.addAvail(availability);
            volunteer.getAvailabilities().add(availability);
        }

        // Update volunteer's limit of hours
        volunteer.setLimitOfWeeklyHours(availRequest.getLimitOfHours());
        volunteerRepository.save(volunteer);
    }

//    public Schedule showSchedule(int year, int week, int actionId) {
//        //
//
//        return ;
//    }

    public void generateSchedule(LocalDate date) {
        List<Availability> availabilities = availabilityService.getAvailabilitiesForDay(date);

        List<Demand> demands = demandService.getDemandsForDay(date);

        for (Demand demand : demands) {
            // Pobierz listę zainteresowanych wolontariuszy
            Set<Volunteer> interestedVolunteers = getInterestedVolunteersForAction(demand.getAction().getActionId());

            // Przefiltruj dostępności, aby zachować tylko te, które interesują zainteresowanych wolontariuszy
            List<Availability> filteredAvailabilities = availabilities.stream()
                    .filter(availability -> interestedVolunteers.contains(availability.getVolunteer()))
                    .toList();

            // Dla każdego interwału zapotrzebowania
            for (DemandInterval demandInterval : demand.getDemandIntervals()) {
                // Znajdź dostępność zgodną z interwałem zapotrzebowania
                List<Availability> matchingAvailability = filteredAvailabilities.stream()
                        .filter(availability -> isAvailabilityMatchingInterval(availability, demandInterval))
                        .toList();

                for (Availability availabilityIter : matchingAvailability) {
                    if(demandInterval.getCurrentVolunteersNumber() < demandInterval.getNeedMax()){
                        Volunteer volunteer = availabilityIter.getVolunteer();

                        // Sprawdź aktualne i maksymalne tygodniowe obciążenie wolontariusza
                        if (isWithinWeeklyLimit(volunteer)) {
                            // Utwórz nowy interwał dyżuru dla danego wolontariusza
                            createDutyInterval(volunteer, demandInterval);

                            // Aktualizuj obciążenie wolontariusza
                            updateWeeklyLoad(volunteer);

                            // Zaktualizuj liczbę wolontariuszy w interwale zapotrzebowania
                            demandInterval.setCurrentVolunteersNumber(demandInterval.getCurrentVolunteersNumber() + 1);
                        }
                    }
                }
                /*if (matchingAvailability.isPresent()) {
                    // Dopóki aktualna liczba przypisanych wolontariuszy jest mniejsza od minimalnego zapotrzebowania
                    while (demandInterval.getCurrentVolunteersNumber() < demandInterval.getNeedMax()) {
                        // Wybierz pierwszego wolontariusza z listy
                        Volunteer volunteer = matchingAvailability.get().getVolunteer();

                        // Sprawdź aktualne i maksymalne tygodniowe obciążenie wolontariusza
                        if (isWithinWeeklyLimit(volunteer)) {
                            // Utwórz nowy interwał dyżuru dla danego wolontariusza
                            createDutyInterval(volunteer, demandInterval);

                            // Aktualizuj obciążenie wolontariusza
                            updateWeeklyLoad(volunteer);

                            // Zaktualizuj liczbę wolontariuszy w interwale zapotrzebowania
                            demandInterval.setCurrentVolunteersNumber(demandInterval.getCurrentVolunteersNumber() + 1);
                        }
                    }
                }*/
            }
        }

        // Zapisz aktualne obciążenie wolontariuszy i przelicz plany dyżurów
        saveVolunteersWeeklyLoad();
        recalculateDutyPlansForDay(date);
    }

    public Set<Volunteer> getInterestedVolunteersForAction(Long actionId) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);

        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            Set<Volunteer> interestedVolunteers = new HashSet<>();

            List<Volunteer> allVolunteers = volunteerRepository.findAll();
            for (Volunteer volunteer : allVolunteers) {
                Preferences preferences = volunteer.getPreferences();
                if (preferences != null) {
                    if (preferences.getT().contains(action) || preferences.getR().contains(action)) {
                        interestedVolunteers.add(volunteer);
                    }
                }
            }

            return interestedVolunteers;
        }

        return Collections.emptySet();
    }

    private boolean isAvailabilityMatchingInterval(Availability availability, DemandInterval demandInterval) {
        // Sprawdź czy interwał dostępności pokrywa się z interwałem zapotrzebowania
        return availability.getSlots().stream()
                .anyMatch(slot -> isSlotMatchingInterval(slot, demandInterval));
    }

    private boolean isSlotMatchingInterval(AvailabilityInterval slot, DemandInterval demandInterval) {
        // Sprawdź czy slot dostępności pokrywa się z interwałem zapotrzebowania
        return slot.getStartTime().equals(demandInterval.getStartTime()) && slot.getEndTime().equals(demandInterval.getEndTime());
    }

    private boolean isWithinWeeklyLimit(Volunteer volunteer) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        double currentWeeklyLoad = volunteer.calculateCurrentWeeklyHours(startOfWeek, endOfWeek);
        double maxWeeklyLimit = volunteer.getLimitOfWeeklyHours();
        return currentWeeklyLoad <= maxWeeklyLimit;
    }

    private boolean isWithinCurrentWeek(LocalDate dutyDate) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return !dutyDate.isBefore(startOfWeek) && !dutyDate.isAfter(endOfWeek);
    }

    private void createDutyInterval(Volunteer volunteer, DemandInterval demandInterval) {
        Duty duty = volunteer.getDuties().stream()
                .filter(d -> d.getDate().equals(demandInterval.getDemand().getDate()))
                .findFirst()
                .orElseGet(() -> {
                    Duty newDuty = new Duty();
                    newDuty.setVolunteer(volunteer);
                    newDuty.setDate(demandInterval.getDemand().getDate());
                    volunteer.getDuties().add(newDuty);
                    return newDuty;
                });

        // Sprawdź, czy istnieje już DutyInterval dla danego interwału
        Optional<DutyInterval> existingInterval = duty.getDutyIntervals().stream()
                .filter(interval -> interval.getStartTime().equals(demandInterval.getStartTime()) &&
                        interval.getEndTime().equals(demandInterval.getEndTime()))
                .findFirst();

        if (existingInterval.isPresent()) {
            // Jeśli istnieje, inkrementuj pole assign
            DutyInterval interval = existingInterval.get();
//            interval.setAssign(interval.getAssign() + 1);
            dutyService.updateDutyInterval(interval); // Aktualizuj interwał w bazie danych
        } else {
            // Jeśli nie istnieje, utwórz nowy DutyInterval
            DutyInterval newInterval = new DutyInterval();
            newInterval.setStartTime(demandInterval.getStartTime());
            newInterval.setEndTime(demandInterval.getEndTime());
            newInterval.setStatus(DutyIntervalStatus.ASSIGNED);

            newInterval.setDuty(duty);
//            duty.getDutyIntervals().add(newInterval);
            dutyService.addDutyInterval(newInterval, duty); // Zapisz nowy interwał do bazy danych
        }
    }

    private void updateWeeklyLoad(Volunteer volunteer) {
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        double currentWeeklyLoad = volunteer.calculateCurrentWeeklyHours(startOfWeek, endOfWeek);
        volunteer.setCurrentWeeklyHours(currentWeeklyLoad);
        volunteerRepository.save(volunteer);
    }

    private void saveVolunteersWeeklyLoad() {
        List<Volunteer> volunteers = volunteerRepository.findAll();
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        for (Volunteer volunteer : volunteers) {
            double currentWeeklyLoad = volunteer.calculateCurrentWeeklyHours(startOfWeek, endOfWeek);
            volunteer.setCurrentWeeklyHours(currentWeeklyLoad);
            volunteerRepository.save(volunteer);
        }
    }

    private void recalculateDutyPlansForDay(LocalDate date) {
        List<Duty> dutiesForDay = dutyService.findByDate(date);

        // Przelicz plany dyżurów dla danego dnia (np. generowanie raportów, statystyk, etc.)
        // Implementacja zależna od specyfiki Twojej aplikacji
    }

    public ActionScheduleDto getActionSchedule(Long actionId) {
        Action action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("Action not found"));

        List<DemandDto> demandDtos = action.getDemands().stream()
                .map(demand -> {
                    List<DemandIntervalDto> intervalDtos = demand.getDemandIntervals().stream()
                            .map(demandInterval -> {
                                // Find matching DutyIntervals
                                List<VolunteerDto> assignedVolunteers = dutyRepository.findAll().stream()
                                        .flatMap(duty -> duty.getDutyIntervals().stream())
                                        .filter(dutyInterval ->
                                                dutyInterval.getStartTime().equals(demandInterval.getStartTime()) &&
                                                        dutyInterval.getEndTime().equals(demandInterval.getEndTime())
                                        )
                                        .map(dutyInterval -> {
                                            Volunteer volunteer = dutyInterval.getDuty().getVolunteer();
                                            return new VolunteerDto(
                                                    volunteer.getVolunteerId(),
                                                    volunteer.getVolunteerDetails().getName(),
                                                    volunteer.getVolunteerDetails().getLastname()
                                            );
                                        })
                                        .collect(Collectors.toList());

                                return new DemandIntervalDto(
                                        demandInterval.getIntervalId(),
                                        demandInterval.getStartTime(),
                                        demandInterval.getEndTime(),
                                        assignedVolunteers
                                );
                            })
                            .collect(Collectors.toList());

                    return new DemandDto(
                            demand.getDemandId(),
                            demand.getDate(),
                            intervalDtos
                    );
                })
                .collect(Collectors.toList());

        return new ActionScheduleDto(
                action.getActionId(),
                action.getHeading(),
                action.getDescription(),
                demandDtos
        );
    }


    @Transactional
    public void modifySchedule(Long volunteerId, int year, int week, ModifyScheduleRequest modifyScheduleRequest) {
        // Pobranie wolontariusza
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer not found"));

        // Określenie początku i końca tygodnia
        LocalDate startOfWeek = LocalDate.ofYearDay(year, (week - 1) * 7 + 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Pobranie obowiązków wolontariusza z danego tygodnia
        List<Duty> duties = dutyRepository.findAllByVolunteerAndDateBetween(volunteer, startOfWeek, endOfWeek);

        // Pobranie wszystkich demand związanych z daną akcją
        List<Demand> demands = demandService.findAllByActionId(modifyScheduleRequest.actionId());

        // Przeliczenie czasu trwania interwałów, które mają być usunięte
        double totalCanceledHours = 0.0;

        // Iterowanie przez dutyIntervals z requestu
        for (DutyInterval requestInterval : modifyScheduleRequest.dutyIntervals()) {
            // Szukanie odpowiadającego interwału w istniejących obowiązkach
            for (Duty duty : duties) {
                for (DutyInterval interval : duty.getDutyIntervals()) {
                    if (interval.getIntervalId().equals(requestInterval.getIntervalId())) {
                        interval.setStatus(DutyIntervalStatus.CANCELED);
                        totalCanceledHours += Duration.between(interval.getStartTime(), interval.getEndTime()).toMinutes() / 60.0;

                        // Znalezienie odpowiedniego DemandInterval i zmniejszenie currentVolunteersNumber
                        for (Demand demand : demands) {
                            Iterator<DemandInterval> iterator = demand.getDemandIntervals().iterator();
                            while (iterator.hasNext()) {
                                DemandInterval demandInterval = iterator.next();
                                if (demandInterval.getStartTime().equals(interval.getStartTime())
                                        && demandInterval.getEndTime().equals(interval.getEndTime())) {
                                    demandInterval.setCurrentVolunteersNumber(demandInterval.getCurrentVolunteersNumber() - 1);
                                    iterator.remove(); // Usunięcie z aktualnej kolekcji
                                }
                            }
                            demandRepository.save(demand);
                        }
                    }
                }
            }
        }

        // Zaktualizowanie currentWeeklyHours wolontariusza
        volunteer.setCurrentWeeklyHours(volunteer.getCurrentWeeklyHours() - totalCanceledHours);

        // Zapisanie zmian w obowiązkach
        dutyRepository.saveAll(duties);

        // Zapisanie zmian w wolontariuszu
        volunteerRepository.save(volunteer);
    }
}
