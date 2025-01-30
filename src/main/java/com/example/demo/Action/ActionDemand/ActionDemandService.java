package com.example.demo.Action.ActionDemand;

import com.example.demo.Action.Action;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ActionDemandService {
    private final ActionDemandRepository actionDemandRepository;

    public ActionDemandService(ActionDemandRepository actionDemandRepository) {
        this.actionDemandRepository = actionDemandRepository;
    }

    public Optional<ActionDemand> findByActionAndDate(Action action, LocalDate date) {
        return actionDemandRepository.findByActionAndDate(action, date);
    }

    public void addDemand(ActionDemand actionDemand) {
        actionDemandRepository.save(actionDemand);
    }

    public List<ActionDemand> getDemandsForDay(LocalDate date) {
        return actionDemandRepository.findAllByDate(date);
    }

    public List<ActionDemand> findAllByActionId(Long actionId) {
        return actionDemandRepository.findAllByAction_ActionId(actionId);
    }
}
