package com.example.demo.Volunteer;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionRepository;
import com.example.demo.Action.Lang;
import com.example.demo.Model.Errors;

import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Availability.AvailabilityInterval.AvailabilityInterval;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Position.PositionService;
import com.example.demo.Volunteer.Preferences.Preferences;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VolunteerService implements Volunteers {

    private final VolunteerRepository volunteerRepository;
    private final ActionRepository actionRepository;
    private final PositionService PositionService;

    public VolunteerService(VolunteerRepository volunteerRepository, ActionRepository actionRepository, PositionService PositionService) {
        this.volunteerRepository = volunteerRepository;
        this.actionRepository = actionRepository;
        this.PositionService = PositionService;
    }

    @Override
    public Long createVolunteer() {
        Volunteer volunteer = new Volunteer();
        volunteer.setPreferences(new Preferences());
        volunteer.setAvailabilities(new ArrayList<>());
        Set<Action> actions = new HashSet<>(actionRepository.findAll());
        volunteer.getPreferences().setU(actions);
        volunteerRepository.save(volunteer);
        return volunteer.getVolunteerId();
    }

    @Override
    public Errors editVolunteer(Long volunteerId, VolunteerRequest request) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();

            vol.setFirstName(request.getFirstName());
            vol.setLastName(request.getLastName());
            vol.setEmail(request.getEmail());
            vol.setPhone(request.getPhone());
            vol.setDateOfBirth(request.getDateOfBirth());
            vol.setAddress(request.getAddress());
            vol.setSex(request.getSex());
            vol.setPosition(request.getPosition());
            vol.setLimitOfWeeklyHours(request.getLimitOfWeeklyHours());
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    public Errors editVolunteerDetails(Long volunteerId, VolunteerRequest details) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            if(details.getFirstName() != null){
                vol.setFirstName(details.getFirstName());
            }
            if(details.getLastName() != null){
                vol.setLastName(details.getLastName());
            }
            if(details.getEmail() != null){
                vol.setEmail(details.getEmail());
            }
            if(details.getPhone() != null){
                vol.setPhone(details.getPhone());
            }
            if(details.getDateOfBirth() != null){
                vol.setDateOfBirth(details.getDateOfBirth());
            }
            if(details.getAddress() != null){
                vol.setAddress(details.getAddress());
            }
            if(details.getSex() != null){
                vol.setSex(details.getSex());
            }
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    @Override
    public Errors deleteVolunteer(Long volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            volunteerRepository.delete(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Volunteer getVolunteerById(Long volunteerId) {
        return volunteerRepository.findById(volunteerId).orElse(null);
    }


    @Override
    public Errors assignPosition(Long volunteerId, Position newRole) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            PositionService.assignRole(vol, newRole);
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Position getPosition(Long volunteerId) {
        return volunteerRepository.findById(volunteerId)
                .map(Volunteer::getPosition)
                .orElse(null);
    }

    @Override
    public Errors setAvailabilities(Long volunteerId, List<Availability> availabilities) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.getAvailabilities().addAll(availabilities);
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    public Long createAndEditVolunteer(VolunteerRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        if (volunteerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }
        Long newVolunteerId = createVolunteer();

        Errors result = editVolunteer(newVolunteerId, request);
        if (result == Errors.SUCCESS) {
            return newVolunteerId;
        }
        return null;
    }

    public boolean existsVolunteerByEmail(String email) {
        return volunteerRepository.existsByEmail(email);
    }

    public List<Availability> getAvailabilities(Long volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            return vol.getAvailabilities();
        }
        return null;
    }

    public Double getLimitOfWeeklyHours(Long volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            return vol.getLimitOfWeeklyHours();
        }
        return null;
    }

    public List<PersonalData> getAllPersData(Position position) {
        List<Volunteer> volunteers = volunteerRepository.findAllByPosition(position);
        return volunteers.stream()
                .map(volunteer -> {
                    PersonalData data = new PersonalData();
                    data.setFirstName(volunteer.getFirstName());
                    data.setLastName(volunteer.getLastName());
                    data.setEmail(volunteer.getEmail());
                    data.setPhone(volunteer.getPhone());
                    data.setDateOfBirth(volunteer.getDateOfBirth());
                    data.setAddress(volunteer.getAddress());
                    data.setSex(volunteer.getSex());
                    return data;
                })
                .toList();
    }

    public Lang getLang(Long volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            return vol.getLanguage();
        }
        return null;
    }

    public Errors editVolunteerWeeklyHours(Long volunteerId, Double weeklyHours) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.setLimitOfWeeklyHours(weeklyHours);

            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    public List<Volunteer> getAllCandidates() {
        return volunteerRepository.findAllByPosition(Position.CANDIDATE);
    }

    public Availability convertToAvailability(AvailabilityRequest request, Long volId) {
        Availability availability = new Availability();
        availability.setDate(request.getDate());
        availability.setVolunteer(volunteerRepository.findById(volId).get());

        Set<AvailabilityInterval> intervals = request.getSlots().stream()
                .map(slot -> {
                    AvailabilityInterval interval = new AvailabilityInterval();
                    interval.setStartTime(LocalTime.parse(slot.getStartTime()));
                    interval.setEndTime(LocalTime.parse(slot.getEndTime()));
                    interval.setAvailability(availability);
                    return interval;
                })
                .collect(Collectors.toSet());

        availability.setSlots(intervals);
        return availability;
    }
}
