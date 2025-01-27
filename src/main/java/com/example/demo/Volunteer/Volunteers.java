package com.example.demo.Volunteer;

import com.example.demo.Model.Errors;
import com.example.demo.Volunteer.Availability.Availability;
import com.example.demo.Volunteer.Position.Position;

import java.util.ArrayList;
import java.util.List;


public interface Volunteers {

    Long createVolunteer();

    Errors editVolunteer(Long vId, VolunteerRequest details);

    Errors deleteVolunteer(Long vId);

    Volunteer getVolunteerById(Long vId);

    // Role management
    Errors assignPosition(Long vId, Position p);

    Position getPosition(Long vId);

    // Availability and duties management
    Errors setAvailabilities(Long vId, List<Availability> availabilities);

    ArrayList<Availability> getAvailabilities(Long vId);
//
//    Errors assignDuty(Long vId, Duty duty);
//
//    ArrayList<Duty> getDuties(Long vId);
}