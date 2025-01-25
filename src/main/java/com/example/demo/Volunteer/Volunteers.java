package com.example.demo.Volunteer;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Duty.Duty;
import com.example.demo.Volunteer.Position.Position;

import java.util.ArrayList;
import java.util.List;


public interface Volunteers {

    ID createVolunteer();

    Errors editVolunteer(ID vId, PersonalData details);

    Errors deleteVolunteer(ID vId);

    Volunteer getVolunteerById(ID vId);

    // Role management
    Errors assignPosition(ID vId, Position p);

    Position getPosition(ID vId);

    // Availability and duties management
    Errors setAvailabilities(ID vId, List<Availability> availabilities);

    ArrayList<Availability> getAvailabilities(ID vId);

    Errors assignDuty(ID vId, Duty duty);

    ArrayList<Duty> getDuties(ID vId);
}