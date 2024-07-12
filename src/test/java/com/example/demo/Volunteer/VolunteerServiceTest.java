package com.example.demo.Volunteer;

import com.example.demo.Candidate.Candidate;
import com.example.demo.Preferences.Preferences;
import com.example.demo.Schedule.Decision;
import com.example.demo.action.Action;
import com.example.demo.action.ActionRepository;
import com.example.demo.action.ActionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ActionService actionService;

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    @Test
    public void testAddVolunteer() {
        Candidate candidate = new Candidate();
        candidate.setName("John");
        candidate.setLastname("Doe");
        Optional<Candidate> candidateOptional = Optional.of(candidate);

        volunteerService.addVolunteer(candidateOptional);

        verify(volunteerRepository, times(1)).save(any(Volunteer.class));
    }

    @Test
    public void testPromoteToLeader() {
        Long idVolunteer = 1L;
        Volunteer volunteer = new Volunteer();
        volunteer.setRole(VolunteerRole.VOLUNTEER);

        when(volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.VOLUNTEER)).thenReturn(Optional.of(volunteer));

        volunteerService.promoteToLeader(idVolunteer);

        verify(volunteerRepository, times(1)).save(volunteer);
        assertEquals(VolunteerRole.LEADER, volunteer.getRole());
    }

    @Test
    public void testDegradeLeader() {
        Long idVolunteer = 1L;
        Volunteer volunteer = new Volunteer();
        volunteer.setRole(VolunteerRole.LEADER);

        when(volunteerRepository.findByVolunteerIdAndRole(idVolunteer, VolunteerRole.LEADER)).thenReturn(Optional.of(volunteer));

        volunteerService.degradeLeader(idVolunteer);

        verify(volunteerRepository, times(1)).save(volunteer);
        assertEquals(VolunteerRole.VOLUNTEER, volunteer.getRole());
    }

    @Test
    public void testAddPreferences() {
        Long actionId = 1L;
        Long volunteerId = 1L;
        Decision decision = Decision.T;
        Volunteer volunteer = new Volunteer();
        volunteer.setPreferences(new Preferences());
        Action action = new Action();

        when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));
        when(actionService.getActionById(actionId)).thenReturn(Optional.of(action));

        volunteerService.addPreferences(actionId, volunteerId, decision);

        verify(volunteerRepository, times(1)).save(volunteer);
        assertTrue(volunteer.getPreferences().getT().contains(action));
    }
}
