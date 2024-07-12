package com.example.demo.Schedule;

import com.example.demo.Volunteer.VolunteerService;
import com.example.demo.action.ActionService;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final ActionService actionService;
    private final VolunteerService volunteerService;

    public ScheduleService(ActionService actionService, VolunteerService volunteerService) {
        this.actionService = actionService;
        this.volunteerService = volunteerService;
    }

    public void choosePref(Long actionId, ActionDecisionRequest actionDecisionRequest) {
        switch (actionDecisionRequest.decision){
            case T :
                actionService.addDetermined(actionId, actionDecisionRequest.volunteerId);
                actionService.addVolunteer(actionId, actionDecisionRequest.volunteerId);
                break;
            case R :
                actionService.addVolunteer(actionId, actionDecisionRequest.volunteerId);
                break;
            default:
                break;
        }
        volunteerService.addPreferences(actionId, actionDecisionRequest.volunteerId, actionDecisionRequest.decision);
    }
}
