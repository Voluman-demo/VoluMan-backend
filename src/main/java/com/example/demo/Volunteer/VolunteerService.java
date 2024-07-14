package com.example.demo.Volunteer;

import com.example.demo.Candidate.Candidate;
import com.example.demo.Preferences.Preferences;
import com.example.demo.Preferences.PreferencesService;
import com.example.demo.Schedule.Decision;
import com.example.demo.action.Action;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.ActionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ActionService actionService;
    private final ActionRepository actionRepository;
    private final PreferencesService preferencesService;

    public VolunteerService(VolunteerRepository volunteerRepository, ActionService actionService, ActionRepository actionRepository, PreferencesService preferencesService) {
        this.volunteerRepository = volunteerRepository;
        this.actionService = actionService;
        this.actionRepository = actionRepository;
        this.preferencesService = preferencesService;
    }

    public void addVolunteer(Optional<Candidate> candidate) {
        if (candidate.isPresent()) {
            VolunteerDetails volunteerDetails = mapCandidateToVolunteerDetails(candidate.get());

            Volunteer volunteer = new Volunteer();
            volunteer.setVolunteerDetails(volunteerDetails);
            volunteer.setRole(VolunteerRole.VOLUNTEER);
            volunteer.setLimitOfWeeklyHours(null);

//          Inicjalizacja pustych obiektów
            volunteer.setPreferences(new Preferences());
            volunteer.setAvailabilities(new ArrayList<>());
            volunteer.setActions(new HashSet<>());

            volunteerRepository.save(volunteer);
        }
    }


    private VolunteerDetails mapCandidateToVolunteerDetails(Candidate candidate) {
        VolunteerDetails volunteerDetails = new VolunteerDetails();
        volunteerDetails.setName(candidate.getName());
        volunteerDetails.setLastname(candidate.getLastname());
        volunteerDetails.setEmail(candidate.getEmail());
        volunteerDetails.setPhone(candidate.getPhone());
        volunteerDetails.setDateOfBirth(candidate.getDateOfBirth());
        volunteerDetails.setCity(candidate.getCity());
        volunteerDetails.setStreet(candidate.getStreet());
        volunteerDetails.setHouseNumber(candidate.getHouseNumber());
        volunteerDetails.setApartmentNumber(candidate.getApartmentNumber());
        volunteerDetails.setPostalNumber(candidate.getPostalNumber());
        volunteerDetails.setSex(candidate.getSex());
        return volunteerDetails;
    }

    public void promoteToLeader(Long idVolunteer) {
        Optional<Volunteer> volunteer = volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.VOLUNTEER);
        if (volunteer.isPresent()) {
            Volunteer volunteerEntity = volunteer.get();
            volunteerEntity.setRole(VolunteerRole.LEADER);
            volunteerRepository.save(volunteerEntity);
        }

    }
    public void degradeLeader(Long idVolunteer) {
        Optional<Volunteer> volunteer = volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER);
        if (volunteer.isPresent()) {
            Volunteer volunteerEntity = volunteer.get();
            volunteerEntity.setRole(VolunteerRole.VOLUNTEER);
            volunteerRepository.save(volunteerEntity);
        }
    }

    public void addPreferences(Long actionId, Long volunteerId, Decision decision) {
        Optional<Volunteer> volunteer = volunteerRepository.findById(volunteerId);

        if (volunteer.isPresent()) {
            Volunteer volunteerEntity = volunteer.get();
            Optional<Action> action = actionService.getActionById(actionId);
            if (action.isPresent()) {
                Action actionEntity = action.get();
                Preferences preferences = volunteerEntity.getPreferences();
                if (preferences == null) {
                    preferences = new Preferences();
                    volunteerEntity.setPreferences(preferences);
                    preferencesService.addPreferences(preferences); // Zapisz nowe preferencje
                }

                if (decision == Decision.T) {
                    preferences.getT().add(actionEntity);
                }
                if (decision == Decision.R) {
                    preferences.getR().add(actionEntity);
                }
                if (decision == Decision.N) {
                    preferences.getN().add(actionEntity);
                }

                preferencesService.addPreferences(preferences);
            }
        }
    }


}
