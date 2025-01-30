package com.example.demo.Action.ActionDemand;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demands")
public class DemandController {


    private final ActionDemandRepository actionDemandRepository;

    public DemandController(ActionDemandRepository actionDemandRepository) {
        this.actionDemandRepository = actionDemandRepository;
    }

    @GetMapping("")
    public List<ActionDemand> getDemands(){
        return actionDemandRepository.findAll();
    }

}
