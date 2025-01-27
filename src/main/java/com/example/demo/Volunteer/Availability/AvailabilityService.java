package com.example.demo.Volunteer.Availability;

import com.example.demo.Volunteer.Availability.AvailabilityDTO.AvailIntervalResponse;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.AvailResponse;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.limitOfHoursRequest;
import com.example.demo.Volunteer.Availability.AvailabilityDTO.VolunteerAvailRequest;
import com.example.demo.Volunteer.Availability.AvailabilityInterval.AvailabilityInterval;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final VolunteerRepository volunteerRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository, VolunteerRepository volunteerRepository) {
        this.availabilityRepository = availabilityRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public void addAvail(Availability availability) {
        availabilityRepository.save(availability);
    }

    public List<AvailResponse> getAllAvailability(List<Availability> availabilities) {
        return availabilities.stream().map(availability ->
                new AvailResponse(
                        availability.getAvailabilityId(),
                        availability.getVolunteer().getVolunteerId(),
                        availability.getDate(),
                        availability.getSlots().stream().map(slot ->
                                new AvailIntervalResponse(
                                        slot.getIntervalId(),
                                        slot.getStartTime().toString(),
                                        slot.getEndTime().toString()
                                )
                        ).collect(Collectors.toList())
                )
        ).collect(Collectors.toList());
    }

    public Availability getByVolunteerIdAndDate(Long volunteerId, LocalDate requestDate) {
        return availabilityRepository.findByVolunteer_VolunteerIdAndDate(volunteerId, requestDate)
                .orElse(new Availability());
    }

    public void setWeekHourLim(Long volunteerId, limitOfHoursRequest limitOfHoursRequest){
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);

        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.setLimitOfWeeklyHours(limitOfHoursRequest.getLimitOfWeeklyHours());
            volunteerRepository.save(vol);

        }
    }
    public Double getWeekHourLim(Long idVolunteer) {
        return volunteerRepository.findById(idVolunteer)
                .map(Volunteer::getLimitOfWeeklyHours)
                .orElse(0.0); // Domyślnie 0.0, jeśli wolontariusz nie istnieje
    }


    public List<Availability> getAvailabilitiesForDay(LocalDate date) {
        return availabilityRepository.findAllByDate(date);
    }
    public void chooseAvailabilities(Long volunteerId, int year, int week, VolunteerAvailRequest availRequest) throws Exception {
        // Validate volunteer
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new Exception("Volunteer not found"));

        // Validate and process availability for each day
        for (VolunteerAvailRequest.DayAvailabilityRequest dayRequest : availRequest.getDays()) {
            LocalDate requestDate = dayRequest.getDate();

            // Validate if the date beIDs to the specified week
            int requestWeek = requestDate.get(WeekFields.ISO.weekOfWeekBasedYear());
            if (requestDate.getYear() != year || requestWeek != week) {
                throw new Exception("Date " + requestDate + " does not beID to week " + week + " of year " + year);
            }

            // Create or update availability for the volunteer for this date
            Availability availability = getByVolunteerIdAndDate(volunteerId, requestDate);

            if (availability.getAvailabilityId() == null) {
//                availability = new Availability();
                availability.setVolunteer(volunteer);
                availability.setDate(requestDate);
                volunteer.getAvailabilities().add(availability);
            }

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
            addAvail(availability);
            volunteer.getAvailabilities().add(availability);
        }

    }
}
