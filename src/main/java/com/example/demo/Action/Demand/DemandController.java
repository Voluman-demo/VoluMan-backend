package com.example.demo.Action.Demand;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demands")
public class DemandController {


    private final DemandRepository demandRepository;

    public DemandController(DemandRepository demandRepository) {
        this.demandRepository = demandRepository;
    }

    @GetMapping("")
    public List<Demand> getDemands(){
        return demandRepository.findAll();
    }

}
