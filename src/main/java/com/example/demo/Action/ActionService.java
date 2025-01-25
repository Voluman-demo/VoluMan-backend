package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.User;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActionService implements Actions {
    private final ActionRepository actionRepository;

    private final VolunteerRepository volunteerRepository;

    public ActionService(ActionRepository actionRepository, VolunteerRepository volunteerRepository) {
        this.actionRepository = actionRepository;
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public ID create() {
        Action action = new Action();
        actionRepository.save(action);
        return action.getActionId();
    }

    @Override
    public Errors remove(ID actionId) {
        if (actionRepository.existsById(actionId)) {
            actionRepository.deleteById(actionId);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setBeg(ID actionId, LocalDate beginDate) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            action.setBegin(beginDate);
            actionRepository.save(action);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setEnd(ID actionId, LocalDate endDate) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            action.setEnd(endDate);
            actionRepository.save(action);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public LocalDate getBeg(ID actionId) {
        return actionRepository.findById(actionId)
                .map(Action::getBegin)
                .orElse(null);
    }

    @Override
    public LocalDate getEnd(ID actionId) {
        return actionRepository.findById(actionId)
                .map(Action::getEnd)
                .orElse(null);
    }

    @Override
    public Errors setDesc(ID actionId, Lang language, Description description) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            action.getDescr().put(language, description);
            description.setValid(true);
            actionRepository.save(action);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors remDesc(ID actionId, Lang language) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            if (action.getDescr().containsKey(language)) {
                action.getDescr().get(language).setValid(false);
                actionRepository.save(action);
                return Errors.SUCCESS;
            }
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Description getDesc(ID actionId, Lang language) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Version version = actionOpt.get().getDescr().get(language);
            if (version != null && version.isValid()) {
                Description description = new Description();
                description.setFullName(version.getFullName());
                description.setShortName(version.getShortName());
                description.setPlace(version.getPlace());
                description.setAddress(version.getAddress());
                description.setDescription(version.getDescription());
                description.setHours(version.getHours());
                description.setRoles(version.getRoles());
                description.setBegin(actionOpt.get().getBegin());
                description.setEnd(actionOpt.get().getEnd());
                return description;
            }
        }
        return null;
    }

    @Override
    public ArrayList<ID> getAllIds() {
        return actionRepository.findAll().stream()
                .map(Action::getActionId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Description> getAllDesc(Lang language) {
        return actionRepository.findAll().stream()
                .map(action -> getDesc(action.getActionId(), language))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }



    @Override
    public Errors setStronglyMine(User user, ID actionId) {
        Volunteer volunteer = user.getVolunteer();
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            volunteer.getPreferences().getS().add(action);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    @Override
    public Errors setWeaklyMine(User user, ID actionId) {
        Volunteer volunteer = user.getVolunteer();
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            volunteer.getPreferences().getW().add(action);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    @Override
    public Errors setRejected(User user, ID actionId) {
        Volunteer volunteer = user.getVolunteer();
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            volunteer.getPreferences().getR().add(action);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    @Override
    public Errors setUndecided(User user, ID actionId) {
        Volunteer volunteer = user.getVolunteer();
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            volunteer.getPreferences().getU().add(action);
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }




    @Override
    public ArrayList<Description> getWeaklyMine(User user) {
        Volunteer volunteer = volunteerRepository.getVolunteerById(user.getVolunteer().getId());
        return volunteer.getPreferences().getW().stream()
                .map(action -> action.getDescr().get(Lang.UK))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)); // Explicitly create an ArrayList
    }


    @Override
    public ArrayList<Description> getRejected(User user) {
        Volunteer volunteer = volunteerRepository.getVolunteerById(user.getVolunteer().getId());
        return volunteer.getPreferences().getR().stream()
                .map(action -> action.getDescr().get(Lang.UK))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)); // Create an ArrayList explicitly
    }

    @Override
    public ArrayList<Description> getUndecided(User user) {
        Volunteer volunteer = volunteerRepository.getVolunteerById(user.getVolunteer().getId());
        return volunteer.getPreferences().getU().stream()
                .map(action -> action.getDescr().get(Lang.UK))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)); // Create an ArrayList explicitly
    }

    @Override
    public ArrayList<Description> getStronglyMine(User user) {
        Volunteer volunteer = volunteerRepository.getVolunteerById(user.getVolunteer().getId());
        return volunteer.getPreferences().getS().stream()
                .map(action -> action.getDescr().get(Lang.UK))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)); // Create an ArrayList explicitly
    }


    @Override
    public Errors isError() {
        return Errors.SUCCESS;
    }

    public Errors updateAction(ID actionId, Action newAction) {
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent()) {
            Action action = actionOpt.get();
            action.setBegin(newAction.getBegin());
            action.setEnd(newAction.getEnd());
            action.setDescr(newAction.getDescr());
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    public Action getAction(ID actionId) {
        return actionRepository.findById(actionId).get();
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }
}
