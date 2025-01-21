package com.example.demo.Volunteer;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Model.PreferenceType;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Position.Position;

import java.util.ArrayList;
import java.util.List;


public interface Volunteers {

    // Core lifecycle management
    ID createVolunteer();

    Errors editVolunteer(ID volunteerId, PersonalData details);

    Errors deleteVolunteer(ID volunteerId);

    Volunteer getVolunteerById(ID volunteerId);

    // Role management
    Errors assignRole(ID volunteerId, Position newRole);

    Position getPosition(ID volunteerId);

    // Preference management
    Errors initializePreferences(ID volunteerId);

    Errors updatePreferences(ID volunteerId, ID actionId, PreferenceType type);

    Errors applyPreferencesToSchedule(ID volunteerId);

    // Availability and duties management
    Errors setAvailabilities(ID volunteerId, List<Availability> availabilities);

    ArrayList<Availability> getAvailabilities(ID volunteerId);

    Errors assignDuty(ID volunteerId, Duty duty);

    ArrayList<Duty> getDuties(ID volunteerId);
}