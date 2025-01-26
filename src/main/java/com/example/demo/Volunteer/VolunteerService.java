package com.example.demo.Volunteer;

import com.example.demo.Action.Action;
import com.example.demo.Action.ActionRepository;
import com.example.demo.Action.ActionService;
import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Position.Position;
import com.example.demo.Volunteer.Position.PositionService;
import com.example.demo.Volunteer.Preferences.Preferences;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VolunteerService implements Volunteers {

    private final VolunteerRepository volunteerRepository;
    private final ActionRepository actionRepository;
    private final PositionService PositionService;
    private final ActionService actionService;

    public VolunteerService(VolunteerRepository volunteerRepository, ActionRepository actionRepository, PositionService PositionService, ActionService actionService) {
        this.volunteerRepository = volunteerRepository;
        this.actionRepository = actionRepository;
        this.PositionService = PositionService;
        this.actionService = actionService;
    }

    @Override
    public ID createVolunteer() {
        Volunteer volunteer = new Volunteer();
        volunteer.setPreferences(new Preferences());
        Set<Action> actions = new HashSet<>(actionRepository.findAll());
        volunteer.getPreferences().setU(actions);
        volunteerRepository.save(volunteer);
        return volunteer.getVolunteerId();
    }

    @Override
    public Errors editVolunteer(ID volunteerId, PersonalData details) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();

            vol.setFirstName(details.getFirstName());
            vol.setLastName(details.getLastName());
            vol.setEmail(details.getEmail());
            vol.setPhone(details.getPhone());
            vol.setDateOfBirth(details.getDateOfBirth());
            vol.setAddress(details.getAddress());
            vol.setSex(details.getSex());

            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors deleteVolunteer(ID volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.setValid(false);
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Volunteer getVolunteerById(ID volunteerId) {
        return volunteerRepository.findById(volunteerId).orElse(null);
    }


    @Override
    public Errors assignPosition(ID volunteerId, Position newRole) {
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
    public Position getPosition(ID volunteerId) {
        return volunteerRepository.findById(volunteerId)
                .map(Volunteer::getPosition)
                .orElse(null);
    }

    @Override
    public Errors setAvailabilities(ID volunteerId, List<Availability> availabilities) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.getAvailabilities().addAll(availabilities);
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public ArrayList<Availability> getAvailabilities(ID volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        return volunteer.map(Volunteer::getAvailabilities).orElse(null);
    }

    @Override
    public Errors assignDuty(ID volunteerId, Duty duty) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        if (volunteer.isPresent()) {
            Volunteer vol = volunteer.get();
            vol.getDuties().add(duty);
            volunteerRepository.save(vol);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public ArrayList<Duty> getDuties(ID volunteerId) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);
        return volunteer.map(vol -> new ArrayList<>(vol.getDuties())).orElse(null);
    }

    public ID createAndEditVolunteer(PersonalData details) {
        ID newVolunteerId = createVolunteer();

        Errors result = editVolunteer(newVolunteerId, details);
        if (result == Errors.SUCCESS) {
            return newVolunteerId;
        }
        return null;
    }

    public boolean existsVolunteerByEmail(String email) {
        return volunteerRepository.existsByEmail(email);
    }

}
