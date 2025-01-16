package com.example.demo.Action;

import com.example.demo.Model.Errors;
import com.example.demo.Model.ID;
import com.example.demo.Volunteer.User.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ActionService implements Actions {
    private final ActionRepository actionRepository;

    private final HashMap<ID, Action> actions = new HashMap<>();
    private int currentId = 1;

    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Override
    public ID create() {
        ID id = new ID(currentId++);
        Action action = new Action();
        actions.put(id, action);
        return id;
    }

    @Override
    public Errors remove(ID actionId) {
        if (actions.containsKey(actionId)) {
            actions.remove(actionId);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setBeg(ID actionId, LocalDate beginDate) {
        Action action = actions.get(actionId);
        if (action != null) {
            action.setBegin(beginDate);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors setEnd(ID actionId, LocalDate endDate) {
        Action action = actions.get(actionId);
        if (action != null) {
            action.setEnd(endDate);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public LocalDate getBeg(ID actionId) {
        Action action = actions.get(actionId);
        return action != null ? action.getBegin() : null;
    }

    @Override
    public LocalDate getEnd(ID actionId) {
        Action action = actions.get(actionId);
        return action != null ? action.getEnd() : null;
    }

    @Override
    public Errors setDesc(ID actionId, Lang language, Description description) {
        Action action = actions.get(actionId);
        if (action != null) {
            action.getDescr().put(language, description);
            action.getDescr().get(language).setValid(true);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Errors remDesc(ID actionId, Lang language) {
        Action action = actions.get(actionId);
        if (action != null && action.getDescr().containsKey(language)) {
            action.getDescr().get(language).setValid(false);
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    @Override
    public Description getDesc(ID actionId, Lang language) {
        Action action = actions.get(actionId);
        if (action != null) {
            Version version = action.getDescr().get(language);
            if (version != null && version.isValid()) {
                Description description = new Description();
                description.setFullName(version.getFullName());
                description.setShortName(version.getShortName());
                description.setPlace(version.getPlace());
                description.setAddress(version.getAddress());
                description.setDescription(version.getDescription());
                description.setHours(version.getHours());
                description.setRoles(version.getRoles());
                description.setBegin(action.getBegin());
                description.setEnd(action.getEnd());
                return description;
            }
        }
        return null;
    }

    @Override
    public ArrayList<ID> getAllIds() {
        return new ArrayList<>(actions.keySet());
    }

    @Override
    public ArrayList<Description> getAllDesc(Lang language) {
        ArrayList<Description> descriptions = new ArrayList<>();
        for (Action action : actions.values()) {
            Description desc = getDesc(new ID(action.getActionId().getId()), language);
            if (desc != null) {
                descriptions.add(desc);
            }
        }
        return descriptions;
    }



    @Override
    public Errors setStronglyMine(User user, ID actionId) {
        // Implementation for setting action as StronglyMine for a user
        return Errors.SUCCESS;
    }

    @Override
    public Errors setWeaklyMine(User user, ID actionId) {
        // Implementation for setting action as WeaklyMine for a user
        return Errors.SUCCESS;
    }

    @Override
    public Errors setRejected(User user, ID actionId) {
        // Implementation for setting action as Rejected for a user
        return Errors.SUCCESS;
    }

    @Override
    public Errors setUndecided(User user, ID actionId) {
        // Implementation for setting action as Undecided for a user
        return Errors.SUCCESS;
    }

    @Override
    public ArrayList<Description> getStronglyMine(User user) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Description> getWeaklyMine(User user) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Description> getRejected(User user) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Description> getUndecided(User user) {
        return new ArrayList<>();
    }

    @Override
    public Errors isError() {
        return Errors.SUCCESS;
    }

    public Errors updateAction(ID actionId, Action newAction) {
        Action action = actions.get(actionId);
        if (action != null) {
            action.setBegin(newAction.getBegin());
            action.setEnd(newAction.getEnd());
            action.setDescr(newAction.getDescr());
            return Errors.SUCCESS;
        }
        return Errors.NOT_FOUND;
    }

    public Action getAction(ID actionId) {
        return actions.get(actionId);
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }
}
