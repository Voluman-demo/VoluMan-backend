package com.example.demo.Action;

import com.example.demo.Model.Errors;
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
    public Long create() {
        Action action = new Action();
        actionRepository.save(action);
        return action.getActionId();
    }

    @Override
    public Errors remove(Long actionId) {
        if (actionRepository.existsById(actionId)) {
            actionRepository.deleteById(actionId);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setBeg(Long actionId, LocalDate beginDate) {
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
    public Errors setEnd(Long actionId, LocalDate endDate) {
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
    public LocalDate getBeg(Long actionId) {
        return actionRepository.findById(actionId)
                .map(Action::getBegin)
                .orElse(null);
    }

    @Override
    public LocalDate getEnd(Long actionId) {
        return actionRepository.findById(actionId)
                .map(Action::getEnd)
                .orElse(null);
    }

    @Override
    public Errors setDesc(Long actionId, Lang language, Description description) {
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
    public Errors remDesc(Long actionId, Lang language) {
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
    public Description getDesc(Long actionId, Lang language) {
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
    public ArrayList<Long> getAllIds() {
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

    public Errors setPref(Long actionId, String preference, Long volunteerId) {
        Optional<Volunteer> volunteerOpt = volunteerRepository.findById(volunteerId);
        Optional<Action> actionOpt = actionRepository.findById(actionId);
        if (actionOpt.isPresent() && volunteerOpt.isPresent()) {
            Volunteer volunteer = volunteerOpt.get();
            Action action = actionOpt.get();
            switch (preference) {
                case "S" -> volunteer.getPreferences().getS().add(action);
                case "W" -> volunteer.getPreferences().getW().add(action);
                case "R" -> volunteer.getPreferences().getR().add(action);
                default -> volunteer.getPreferences().getU().add(action);
            }
            return Errors.SUCCESS;
        }
        return Errors.FAILURE;
    }

    public List<Description> getPref(String preference, Long volunteerId) {
        Volunteer volunteer = volunteerRepository.getVolunteerByVolunteerId(volunteerId);
        Lang lang = volunteer.getLanguage();

        return switch (preference) {
            case "S" -> volunteer.getPreferences().getS().stream()
                    .map(action -> action.getDescr().get(lang))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
            case "W" -> volunteer.getPreferences().getW().stream()
                    .map(action -> action.getDescr().get(lang))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
            case "R" -> volunteer.getPreferences().getR().stream()
                    .map(action -> action.getDescr().get(lang))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
            default -> volunteer.getPreferences().getU().stream()
                    .map(action -> action.getDescr().get(lang))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        };
    }


    @Override
    public Errors isError() {
        return Errors.SUCCESS;
    }

    public Errors updateAction(Long actionId, Action newAction) {
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

    public Action getAction(Long actionId) {
        return actionRepository.findById(actionId).get();
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }


    public List<String> getAllHeadings(Lang language) {
        return actionRepository.findAll().stream()
                .map(action -> getDesc(action.getActionId(), language).getFullName())
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }


}
