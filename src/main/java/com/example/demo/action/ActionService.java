package com.example.demo.action;

import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import com.example.demo.Volunteer.VolunteerRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionService {
    private final ActionRepository actionRepository;
    private final VolunteerRepository volunteerRepository;


    public ActionService(ActionRepository actionRepository, VolunteerRepository volunteerRepository) {
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    public Optional<Action> getActionById(Long idAction) {
        return actionRepository.findById(idAction);
    }

    public Optional<String> getActionDescription(Long idAction) {
        return actionRepository.findById(idAction).map(Action::getDescription);
    }

    public Optional<String> getActionHeading(Long idAction) {
        return actionRepository.findById(idAction).map(Action::getHeading);
    }

    public Action addAction(Action actionReq,Long adminId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(adminId, VolunteerRole.ADMIN)) {
            throw new RuntimeException("Admin not found");
        }

        Action action = new Action();
        action.setHeading(actionReq.getHeading());
        action.setDescription(actionReq.getDescription());
        action.setLeaderId(actionReq.getLeaderId());
        action.setStatus(ActionStatus.OPEN);

        return actionRepository.save(action);
    }

    public void closeAction(Long idAction, Long adminId) {
        if (!volunteerRepository.existsByVolunteerIdAndRole(adminId, VolunteerRole.ADMIN)) {
            throw new RuntimeException("Admin not found");
        }

        Action action = actionRepository.findById(idAction).orElseThrow(() -> new RuntimeException("Action not found"));
        action.setStatus(ActionStatus.CLOSED);
        actionRepository.save(action);
    }

    public void changeDescription(Long idAction, Long leaderId, String description) {
        Volunteer leader = volunteerRepository.findById(leaderId)
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        Action action = actionRepository.findById(idAction)
                .orElseThrow(() -> new RuntimeException("Action not found"));

        if (!action.getLeaderId().equals(leaderId)) {
            throw new RuntimeException("Unauthorized");
        }

        action.setDescription(description);
        actionRepository.save(action);
    }
}

