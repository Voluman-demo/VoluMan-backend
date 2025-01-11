package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Model.Language.LangISO;
import com.example.demo.Volunteer.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
public class ActionService implements Actions {

    private final ActionRepository actionRepository;
    private final VolunteerService volunteerService;

    public ActionService(ActionRepository actionRepository, VolunteerService volunteerService) {
        this.actionRepository = actionRepository;
        this.volunteerService = volunteerService;
    }

    @Override
    public Errors addAction(SingleAction action) {
        try {
            SingleAction newAction = new SingleAction();
            newAction.setShortName(action.getShortName());
            newAction.setFullName(action.getFullName());
            newAction.setPlace(action.getPlace());
            newAction.setDescription(action.getDescription());
            newAction.setActionEnd(action.getActionBeg());
            newAction.setActionEnd(action.getActionEnd());
            newAction.setRoles(action.getRoles());
            newAction.setLanguages(action.getLanguages());

            actionRepository.save(newAction);
            return Errors.SUCCESS;
        } catch (Exception e) {
            return Errors.FAILURE;
        }
    }


    @Override
    public Errors setMy(Volunteer v, ID d) {
        return null;
    }

    @Override
    public Errors setRejected(Volunteer v, ID d) {
        return null;
    }

    @Override
    public Errors setUndecided(Volunteer v, ID d) {
        return null;
    }

    @Override
    public Errors setLanguage(LangISO lang, ID d) {
        return null;
    }

    @Override
    public Errors remAction(ID id) {
        if (actionRepository.existsById((long) id.getId())) {
            actionRepository.deleteById((long) id.getId());
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors updAction(ID id, SingleAction action) {
        Optional<SingleAction> optionalAction = actionRepository.findById((long) id.getId());
        if (optionalAction.isPresent()) {
            SingleAction existingAction = optionalAction.get();
            existingAction.setShortName(action.getShortName());
            existingAction.setFullName(action.getFullName());
            existingAction.setPlace(action.getPlace());
            existingAction.setDescription(action.getDescription());
            existingAction.setActionBeg(action.getActionBeg());
            existingAction.setActionEnd(action.getActionEnd());
            existingAction.setRoles(action.getRoles());
            existingAction.setLanguages(action.getLanguages());
            actionRepository.save(existingAction);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public ArrayList<SingleAction> getAllActions(LangISO lang) {
        List<SingleAction> actions = actionRepository.findAll().stream()
                .filter(action -> action.getLanguages().contains(lang))
                .toList();

        return new ArrayList<>(actions);
    }

//    @Override
//    public ArrayList<SingleAction> getMyActions(Long volunteerId) {
//        return getFilteredActions(volunteerId, Action::getVolunteers);
//    }

//    @Override
//    public ArrayList<SingleAction> getRejectedActions(Long volunteerId) {
//        return getFilteredActions(volunteerService.findVolunteerById(volunteerId), Action::getDetermined);
//    }
//
//    @Override
//    public ArrayList<SingleAction> getUndecidedActions(Long volunteerId) {
//        return new ArrayList<>(actionRepository.findAll().stream()
//                .filter(singleAction -> !singleAction.get().stream()
//                        .anyMatch(v -> v.getVolunteerId().equals(volunteer.getVolunteerId())))
//                .toList());
//    }

    @Override
    public <T> ResponseEntity<?> isError(Errors error, T body) {
        return Actions.super.isError(error, body);
    }

    private ArrayList<Object> getFilteredActions(Volunteer volunteer, Function<SingleAction, Set<Volunteer>> selector) {
        return new ArrayList<>(actionRepository.findAll().stream()
                .filter(action -> selector.apply(action).stream()
                        .anyMatch(v -> v.getId().equals(volunteer.getId())))
                .toList());
    }
}
