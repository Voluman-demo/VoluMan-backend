package com.example.demo.Volunteer;

import com.example.demo.Candidate.Candidate;
import com.example.demo.Preferences.Preferences;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public void addVolunteer(Optional<Candidate> candidate) {
        if (candidate.isPresent()) {
            VolunteerDetails volunteerDetails = mapCandidateToVolunteerDetails(candidate.get());

            Volunteer volunteer = new Volunteer();
            volunteer.setVolunteerDetails(volunteerDetails);
            volunteer.setRole(VolunteerRole.VOLUNTEER);

            // Inicjalizacja pustych obiektów
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
        Optional<Volunteer> volunteer = volunteerRepository.findByRoleAndVolunteerId(idVolunteer, VolunteerRole.VOLUNTEER);
        if (volunteer.isPresent()) {
            Volunteer volunteerEntity = volunteer.get();
            volunteerEntity.setRole(VolunteerRole.LEADER);
            volunteerRepository.save(volunteerEntity);
        }

    }
    public void degradeLeader(Long idVolunteer) {
        Optional<Volunteer> volunteer = volunteerRepository.findByRoleAndVolunteerId(idVolunteer, VolunteerRole.VOLUNTEER);
        if (volunteer.isPresent()) {
            Volunteer volunteerEntity = volunteer.get();
            volunteerEntity.setRole(VolunteerRole.VOLUNTEER);
            volunteerRepository.save(volunteerEntity);
        }
    }
}
